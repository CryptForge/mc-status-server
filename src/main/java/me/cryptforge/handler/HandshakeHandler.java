package me.cryptforge.handler;

import me.cryptforge.game.MinecraftVersion;
import me.cryptforge.network.Connection;
import me.cryptforge.network.packet.Packet;
import me.cryptforge.network.packet.in.HandshakePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HandshakeHandler extends PacketHandler {

    private final Logger logger = LoggerFactory.getLogger(HandshakeHandler.class);

    public HandshakeHandler(Connection connection) {
        super(connection);
    }

    @Override
    public void handlePacket(Packet packet) {
        // Handshake state only has 1 packet
        final HandshakePacket handshakePacket = (HandshakePacket) packet;
        logger.info("Connected with {} using version {}", connection().getAddress().getHostString(), MinecraftVersion.getVersion(handshakePacket.protocolVersion()));
        connection().init(handshakePacket.protocolVersion());
        connection().switchState(handshakePacket.nextState());
    }
}
