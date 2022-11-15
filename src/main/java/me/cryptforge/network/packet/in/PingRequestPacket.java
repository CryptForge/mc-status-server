package me.cryptforge.network.packet.in;

import me.cryptforge.network.ByteBuf;
import me.cryptforge.network.packet.Packet;

public record PingRequestPacket(long payload) implements Packet {

    public PingRequestPacket(ByteBuf buffer) {
        this(buffer.nio().getLong());
    }
}
