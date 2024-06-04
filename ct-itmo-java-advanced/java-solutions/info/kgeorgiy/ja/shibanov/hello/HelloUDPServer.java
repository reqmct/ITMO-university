package info.kgeorgiy.ja.shibanov.hello;

import info.kgeorgiy.java.advanced.hello.NewHelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class HelloUDPServer implements NewHelloServer {

    private ExecutorService submitersPool;
    private ExecutorService handlersPool;
    private List<DatagramSocket> sockets;
    private Phaser phaser;

    @Override
    public void start(int threads, Map<Integer, String> ports) {
        if (ports.isEmpty()) {
            return;
        }
        handlersPool = Executors.newFixedThreadPool(ports.size());
        submitersPool = Executors.newFixedThreadPool(threads);
        sockets = new ArrayList<>();
        phaser = new Phaser(1);
        for (var entry : ports.entrySet()) {
            try {
                DatagramSocket socket = new DatagramSocket(entry.getKey());
                sockets.add(socket);
                handlersPool.submit(handlerLogic(socket, entry.getValue()));
            } catch (SocketException e) {
                System.err.println("Socket could not be opened: " + e);
            }
        }
        phaser.arriveAndAwaitAdvance();
    }

    private Runnable handlerLogic(DatagramSocket socket, String template) {
        phaser.register();
        return () -> {
            try {
                phaser.arriveAndDeregister();
                while (!socket.isClosed()) {
                    int size = socket.getReceiveBufferSize();
                    DatagramPacket packet = new DatagramPacket(new byte[size], size);
                    socket.receive(packet);
                    String request = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
                    String response = template.replaceAll("\\$", request);
                    packet.setData(response.getBytes(StandardCharsets.UTF_8));
                    submitersPool.submit(
                            () -> {
                                try {
                                    socket.send(packet);
                                } catch (IOException e) {
                                    System.err.println("Can't send packet: " + e);
                                }
                            }
                    );
                }
            } catch (IOException e) {
                System.err.println("I/O exception with socket: " + e);
            }
        };
    }

    @Override
    public void close() {
        if (sockets != null) {
            for (var socket : sockets) {
                socket.close();
            }
        }
        if (handlersPool != null) {
            handlersPool.close();
        }
        if (submitersPool != null) {
            submitersPool.close();
        }
    }

    public static void main(String[] args) {
        if (args == null || args.length != 2 || Arrays.stream(args).anyMatch(Objects::isNull)) {
            System.err.println("Incorrect args");
            return;
        }
        try (var server = new HelloUDPServer()) {
            server.start(
                    Integer.parseInt(args[0]),
                    Integer.parseInt(args[1])
            );
        } catch (NumberFormatException e) {
            System.err.println("Incorrect arg: " + e);
        }
    }
}
