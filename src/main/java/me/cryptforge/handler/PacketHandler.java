package me.cryptforge.handler;

import me.cryptforge.network.Connection;
import me.cryptforge.network.packet.Packet;

public abstract sealed class PacketHandler permits HandshakeHandler, StatusHandler, LoginHandler {

    private final Connection connection;

    protected PacketHandler(Connection connection) {
        this.connection = connection;
    }

    public abstract void handlePacket(Packet packet);

    public Connection connection() {
        return connection;
    }
}
