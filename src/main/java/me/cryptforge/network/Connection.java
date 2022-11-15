package me.cryptforge.network;

import me.cryptforge.exception.NotImplementedException;
import me.cryptforge.handler.HandshakeHandler;
import me.cryptforge.handler.LoginHandler;
import me.cryptforge.handler.PacketHandler;
import me.cryptforge.handler.StatusHandler;
import me.cryptforge.network.packet.Packet;
import me.cryptforge.network.packet.SendablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.channels.SocketChannel;

public class Connection {

    private final Logger logger = LoggerFactory.getLogger(Connection.class);

    private final SocketChannel channel;
    private final ByteBuf readBuffer;
    private final ByteBuf writeBuffer;
    private final InetSocketAddress address;

    private PacketHandler handler;
    private ConnectionState state;
    private boolean initialized;

    private int protocolVersion;

    public Connection(SocketChannel channel) {
        this.channel = channel;
        this.readBuffer = ByteBuf.allocate(4096);
        this.writeBuffer = ByteBuf.allocate(4096);
        try {
            this.address = (InetSocketAddress) channel.getRemoteAddress();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        switchState(ConnectionState.HANDSHAKE);
    }

    public void sendPacket(SendablePacket packet) {
        writeBuffer.nio().clear();
        final int startPosition = writeBuffer.nio().position();
        writeBuffer.nio().position(writeBuffer.nio().position() + 3);
        writeBuffer.writeVarInt(PacketRegistry.getId(packet.getClass())); // write packet id
        packet.write(writeBuffer); // write packet
        final int length = writeBuffer.nio().position() - (startPosition + 3);
        if (length > 2097151) {
            logger.warn("Attempted to send packet that exceeded the maximum packet size (size: {}, class: {})", length, packet.getClass().toString());
            return;
        }
        writeBuffer.writeVarIntHeader(startPosition, length); // write packet length
        writeBuffer.nio().flip();
        try {
            channel.write(writeBuffer.nio());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processPacket() throws IOException {
        readBuffer.nio().clear();
        final int bytesRead = channel.read(readBuffer.nio());
        if (bytesRead == -1) {
            close();
            return;
        }
        readBuffer.nio().flip();

        while (readBuffer.nio().hasRemaining()) {
            try {
                final int length = readBuffer.readVarInt();
                if (!readBuffer.hasBytes(length - 3)) {
                    logger.warn("Received incomplete packet");
                    return;
                }
                final int id = readBuffer.readVarInt();
                if (!PacketRegistry.isValidId(id, state)) {
                    logger.warn("Received unimplemented packet (id: {}, state: {})", id, state);
                    readBuffer.skip(length - 3 - readBuffer.getVarIntSize(id));
                    continue;
                }
                final Packet packet = PacketRegistry.getConstructor(id, state).create(readBuffer);
                handler.handlePacket(packet);
            } catch (BufferOverflowException | BufferUnderflowException e) {
                logger.error("Received invalid packet");
                readBuffer.nio().clear();
            }

        }
    }

    public void switchState(ConnectionState newState) {
        if (state == newState) {
            return;
        }
        if (newState == ConnectionState.PLAY) {
            logger.warn("Attempted to switch to PLAY state. Ignoring");
            return;
        }
        handler = switch (newState) {
            case HANDSHAKE -> new HandshakeHandler(this);
            case STATUS -> new StatusHandler(this);
            case LOGIN -> new LoginHandler(this);
            case PLAY -> throw new NotImplementedException("No play handler");
        };
        state = newState;
    }

    public void close() {
        try {
            channel.close();
        } catch (IOException e) {
            logger.error("Failed to close connection with {}", address.getHostString());
            e.printStackTrace();
        }
    }

    public void init(int protocolVersion) {
        this.protocolVersion = protocolVersion;
        this.initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public int getProtocolVersion() {
        if (!initialized) {
            throw new RuntimeException("Attempted to get protocol before handshake could be completed!");
        }
        return protocolVersion;
    }

    public InetSocketAddress getAddress() {
        return address;
    }
}
