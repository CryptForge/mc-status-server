package me.cryptforge.network.packet.in;

import me.cryptforge.network.ByteBuf;
import me.cryptforge.network.packet.Packet;

public record StatusRequestPacket() implements Packet {

    public StatusRequestPacket(ByteBuf buffer) {
        this();
    }
}
