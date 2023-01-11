package me.cryptforge.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private boolean hasStarted;

    private final Selector selector;

    private final InetSocketAddress address;

    public Server(String address, int port) {
        this.address = new InetSocketAddress(address, port);

        try {
            selector = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        if (hasStarted) return;
        try {
            final ServerSocketChannel channel = ServerSocketChannel.open();
            final ServerSocket socket = channel.socket();

            socket.bind(address);
            channel.configureBlocking(false);

            channel.register(selector, SelectionKey.OP_ACCEPT);

            hasStarted = true;
            logger.info("Server started at " + address.getHostName() + ":" + address.getPort() + "!");

            while (true) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = keys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    if (key.isAcceptable()) {
                        acceptClient(channel);
                    } else if (key.isReadable()) {
                        process(key);
                    }
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void process(SelectionKey key) throws IOException {
        final Connection connection = (Connection) key.attachment();
        connection.processPacket();
    }

    private void acceptClient(ServerSocketChannel serverChannel) throws IOException {
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ, new Connection(client));
    }

}
