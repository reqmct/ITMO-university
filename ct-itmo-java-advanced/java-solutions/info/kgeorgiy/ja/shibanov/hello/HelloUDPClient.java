package info.kgeorgiy.ja.shibanov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class HelloUDPClient implements HelloClient {
    private static final int SOCKET_TIMEOUT = 500;

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        ExecutorService threadPool = Executors.newFixedThreadPool(threads);
        InetSocketAddress socketAddr;
        Phaser phaser = new Phaser(1);
        try {
            socketAddr = new InetSocketAddress(host, port);
        } catch (IllegalArgumentException e) {
            System.err.println("Incorrect port: " + e);
            return;
        }
        if (socketAddr.isUnresolved()) {
            System.err.println("Incorrect host: " + host);
            return;
        }

        for (int i = 1; i <= threads; i++) {
            phaser.register();
            threadPool.submit(threadLogic(socketAddr, i, prefix, requests, phaser));
        }
        phaser.arriveAndAwaitAdvance();
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

    private Runnable threadLogic(
            SocketAddress socketAddress,
            int threadNumber,
            String prefix,
            int requests,
            Phaser phaser
    ) {
        return () -> {
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setSoTimeout(SOCKET_TIMEOUT);
                int receivedSize = socket.getReceiveBufferSize();
                for (int i = 1; i <= requests; i++) {
                    String request = generateMessage(prefix, threadNumber, i);
                    var data = request.getBytes(StandardCharsets.UTF_8);
                    DatagramPacket packet = new DatagramPacket(data, data.length, socketAddress);
                    while (true) {
                        try {
                            socket.send(packet);
                            System.out.println("Request was send successfully: " + request);
                            DatagramPacket receivedPacket = new DatagramPacket(new byte[receivedSize], receivedSize);
                            socket.receive(receivedPacket);
                            String response = new String(
                                    receivedPacket.getData(),
                                    receivedPacket.getOffset(),
                                    receivedPacket.getLength(),
                                    StandardCharsets.UTF_8
                            );
                            if (!validateResponse(response, prefix, threadNumber, i)) {
                                System.err.printf("Request: %s was not processed; Bad response: %s", request, response);
                                System.out.println();
                                continue;
                            }
                            System.out.println("Response was received successfully: " + response);
                            break;
                        } catch (SocketTimeoutException e) {
                            System.err.println("Time's up: " + e);
                        } catch (IOException e) {
                            System.err.printf("Packet with message %s has I/O exception %s", request, e.getMessage());
                            System.err.println();
                        }
                    }
                }
            } catch (SocketException e) {
                System.err.println("Socket exception: " + e);
            } finally {
                phaser.arriveAndDeregister();
                System.out.printf("Thread %d has completed work", threadNumber);
                System.out.println();
            }
        };
    }


    private String generateMessage(String prefix, int threadNumber, int requestNumber) {
        return String.format("%s%d_%d", prefix, threadNumber, requestNumber);
    }

    public static void main(String[] args) {
        var client = new HelloUDPClient();
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
            System.err.println("Incorrect arg: " + e);
        }
    }
}
