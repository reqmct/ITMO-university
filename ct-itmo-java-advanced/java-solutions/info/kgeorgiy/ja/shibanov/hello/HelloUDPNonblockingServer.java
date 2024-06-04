package info.kgeorgiy.ja.shibanov.hello;

import info.kgeorgiy.java.advanced.hello.NewHelloServer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class HelloUDPNonblockingServer implements NewHelloServer {
    private ExecutorService threads;
    private Selector selector;
    private Thread thread;
    private static final int QUEUE_CAPACITY = 32768;
    private final Pattern pattern = Pattern.compile("\\$");

    private record RequestDescription(SocketAddress address, String response) {
    }

    Queue<RequestDescription> queue;


    private void receiveDatagram(SelectionKey key) throws SocketException {
        try {
            DatagramChannel channel = (DatagramChannel) key.channel();

            String template = (String) key.attachment();

            ByteBuffer buffer = ByteBuffer.allocate(channel.socket().getReceiveBufferSize());
            SocketAddress address = channel.receive(buffer);
            buffer.flip();

            threads.submit(() -> {
                String request = new String(buffer.array(), buffer.position(), buffer.limit(), StandardCharsets.UTF_8);
                String response = pattern.matcher(template).replaceAll(request);
                queue.add(new RequestDescription(address, response));
                key.interestOpsOr(SelectionKey.OP_WRITE);
                selector.wakeup();
            });
        } catch (IOException e) {
            System.err.println("Can't send: " + e);
        }
    }

    private void sendDatagram(SelectionKey key) {
        @SuppressWarnings("resource") DatagramChannel channel = (DatagramChannel) key.channel();
        if (!queue.isEmpty()) {
            RequestDescription description = queue.poll();
            String response = description.response();
            ByteBuffer buffer = ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));
            try {
                channel.send(buffer, description.address);
            } catch (IOException e) {
                System.err.println("Can't send: " + e);
            }
        } else {
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    @Override
    public void start(int threads, Map<Integer, String> ports) {
        try {
            selector = Selector.open();
            for (var entry : ports.entrySet()) {
                DatagramChannel channel = DatagramChannel.open();
                channel.configureBlocking(false);
                SocketAddress socketAddr = new InetSocketAddress(entry.getKey());
                channel.bind(socketAddr);
                channel.register(selector, SelectionKey.OP_READ, entry.getValue());
            }
        } catch (IOException e) {
            close();
            throw new UncheckedIOException(e);
        }
        this.threads = Executors.newFixedThreadPool(threads);
        queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        thread = new Thread(() -> {
            try {
                while (!Thread.interrupted() && selector.isOpen()) {
                    selector.select();
                    for (var key : selector.selectedKeys()) {
                        if (key.isWritable()) {
                            sendDatagram(key);
                        } else if (key.isReadable()) {
                            receiveDatagram(key);
                        }
                    }
                    selector.selectedKeys().clear();
                }

            } catch (IOException e) {
                System.err.println("I/O exception with server: " + e);
            }
        });
        thread.start();
    }


    @Override
    public void close() {
        if (thread != null) {
            boolean interrupted = false;
            thread.interrupt();
            while (true) {
                try {
                    thread.join();
                    break;
                } catch (InterruptedException ignored) {
                    interrupted = true;
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }

            if (threads != null) {
                threads.close();
            }
        }


        if (selector != null) {
            for (var key : selector.keys()) {
                try {
                    key.channel().close();
                } catch (IOException e) {
                    System.err.println("Can't close chanel: " + e);
                }
                key.cancel();
            }


            try {
                selector.close();
            } catch (IOException e) {
                System.err.println("Can't close selector: " + e);
            }
        }

    }

    public static void main(String[] args) {
        if (args == null || args.length != 2 || Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println("Incorrect args");
            return;
        }
        try (var server = new HelloUDPNonblockingServer()) {
            server.start(
                    Integer.parseInt(args[0]),
                    Integer.parseInt(args[1])
            );
        } catch (NumberFormatException e) {
            System.err.println("Incorrect arg: " + e.getMessage());
        }
    }
}
