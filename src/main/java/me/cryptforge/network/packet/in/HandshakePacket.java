package me.cryptforge.network.packet.in;

import me.cryptforge.network.ByteBuf;
import me.cryptforge.network.ConnectionState;
import me.cryptforge.network.packet.Packet;

public record HandshakePacket(
        int protocolVersion,
        String address,
        int port,
        ConnectionState nextState
) implements Packet {

    public HandshakePacket(ByteBuf buffer) {
        this(buffer.readVarInt(), buffer.readString(255), buffer.nio().getShort(), buffer.readConnectionState());
    }
}
