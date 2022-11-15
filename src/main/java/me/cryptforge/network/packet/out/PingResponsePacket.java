package me.cryptforge.network.packet.out;

import me.cryptforge.network.ByteBuf;
import me.cryptforge.network.packet.SendablePacket;

public record PingResponsePacket(long payload) implements SendablePacket {

    public PingResponsePacket(ByteBuf buffer) {
        this(buffer.nio().getLong());
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.nio().putLong(payload);
    }
}
