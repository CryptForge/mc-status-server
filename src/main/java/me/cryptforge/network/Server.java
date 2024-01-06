package me.cryptforge.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private final InetSocketAddress address;
    private boolean running;

    public Server(String address, int port) {
        this.address = new InetSocketAddress(address, port);
    }

    public void start() {
        if (running) {
            return;
        }
        running = true;
        try (
                final ServerSocketChannel serverChannel = ServerSocketChannel.open();
                final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()
        ) {
            serverChannel.bind(address);

            while (running) {
                final SocketChannel channel = serverChannel.accept();

                executor.submit(() -> {
                    LOGGER.info("Client connected!");
                    try {
                        final Connection connection = new Connection(channel);

                        while (channel.isOpen()) {
                            connection.processPacket();
                        }
                    } catch (IOException e) {
                        LOGGER.error("Critical failure while reading packet", e);
                    } finally {
                        try {
                            channel.close();
                            LOGGER.info("Client disconnected!");
                        } catch (IOException e) {
                            LOGGER.error("Failed to close connection", e);
                        }
                    }
                });
            }
            LOGGER.info("Shutting down...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
