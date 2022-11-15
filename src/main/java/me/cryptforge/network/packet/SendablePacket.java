package me.cryptforge.network.packet;

import me.cryptforge.network.ByteBuf;

public interface SendablePacket extends Packet {

    void write(ByteBuf buffer);

}
