package info.kgeorgiy.ja.shibanov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class HelloUDPNonblockingClient implements HelloClient {
    private static final int SELECT_TIMEOUT = 100;

    private String prefix;
    private InetSocketAddress socketAddr;
    private int requests;

    private String generateMessage(String prefix, int threadNumber, int requestNumber) {
        return String.format("%s%d_%d", prefix, threadNumber, requestNumber);
    }

    private boolean validateResponse(String response, String prefix, int threadNumber, int requestNumber) {
        String greeting = "Hello, " + prefix;
        if (!response.startsWith(greeting)) {
            return false;
        }
        String number = response.substring(greeting.length());
        var splitByUnderLine = number.split("_");
        if (splitByUnderLine.length != 2) {
            return false;
        }
        try {
            var actualThreadNumber = Integer.parseInt(splitByUnderLine[0]);
            var actualRequestNumber = Integer.parseInt(splitByUnderLine[1]);
            if (actualThreadNumber != threadNumber || actualRequestNumber != requestNumber) {
                return false;
            }
        } catch (NumberFormatException ignored) {
            return false;
        }
        return true;
    }

    private void sendDatagram(SelectionKey key) {
        DatagramChannel channel = (DatagramChannel) key.channel();
        Stats stats = (Stats) key.attachment();
        String request = generateMessage(prefix, stats.threadNumber(), stats.requestNumber());
        ByteBuffer byteBuffer = ByteBuffer.wrap(request.getBytes(StandardCharsets.UTF_8));
        try {
            channel.send(byteBuffer, socketAddr);
        } catch (IOException e) {
            System.err.println("Can't send request: " + request);
        }
        System.out.println("Request has been sent: " + request);
        key.interestOps(SelectionKey.OP_READ);
    }

    private void receiveDatagram(SelectionKey key) throws IOException {
        DatagramChannel channel = (DatagramChannel) key.channel();
        Stats stats = (Stats) key.attachment();
        ByteBuffer buffer = stats.buffer.clear();
        try {
            channel.receive(buffer);
        } catch (IOException e) {
            System.err.println("Can't receive the package: " + e);
            return;
        }
        buffer.flip();
        String response = new String(buffer.array(), buffer.position(), buffer.limit(), StandardCharsets.UTF_8);
        if (validateResponse(response, prefix, stats.threadNumber, stats.requestNumber)) {
            System.out.println("Response has been received: " + response);

            if (stats.requestNumber == requests) {
                channel.close();
                key.cancel();
                return;
            }

            key.attach(new Stats(stats.buffer, stats.threadNumber, stats.requestNumber + 1));
        }

        key.interestOps(SelectionKey.OP_WRITE);
    }

    private record Stats(ByteBuffer buffer, int threadNumber, int requestNumber) {
    }

    private void closeChanels(Selector selector) throws IOException {
        for (var key : selector.keys()) {
            try {
                key.channel().close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        this.prefix = prefix;
        this.requests = requests;
        socketAddr = new InetSocketAddress(host, port);
        try (Selector selector = Selector.open()) {
            for (int i = 1; i <= threads; i++) {
                try {
                    DatagramChannel datagramChannel = DatagramChannel.open();
                    datagramChannel.configureBlocking(false);
                    datagramChannel.register(selector, SelectionKey.OP_WRITE,
                            new Stats(ByteBuffer.allocate(datagramChannel.socket().getReceiveBufferSize()),
                                    i, 1));
                } catch (IOException e) {
                    closeChanels(selector);
                    throw new UncheckedIOException(e);
                }
            }

            try {
                while (!selector.keys().isEmpty()) {
                    if (selector.select(SELECT_TIMEOUT) == 0) {
                        for (SelectionKey key : selector.keys()) {
                            if (key.interestOps() == SelectionKey.OP_READ && !key.isReadable()) {
                                key.interestOps(SelectionKey.OP_WRITE);
                            }
                        }
                        continue;
                    }

                    for (var key : selector.selectedKeys()) {
                        if (key.isReadable()) {
                            try {
                                receiveDatagram(key);
                            } catch (IOException e) {
                                System.err.println("Can't receive packet: " + e);
                                return;
                            }
                        } else if (key.isWritable()) {
                            sendDatagram(key);
                        }
                    }
                    selector.selectedKeys().clear();
                }
            } catch (IOException e) {
                System.err.println("I/O exception with selector: " + e);
                closeChanels(selector);
            }


        } catch (IOException e) {
            System.err.println("Can't open selector: " + e);
        }
    }

    public static void main(String[] args) {
        var client = new HelloUDPNonblockingClient();
        if (args == null || args.length != 5 || Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println("Incorrect args");
            return;
        }
        try {
            client.run(
                    args[0],
                    Integer.parseInt(args[1]),
                    args[2],
                    Integer.parseInt(args[3]),
                    Integer.parseInt(args[4])
            );
        } catch (NumberFormatException e) {
            System.err.println("Incorrect arg: " + e.getMessage());
        }
    }
}
