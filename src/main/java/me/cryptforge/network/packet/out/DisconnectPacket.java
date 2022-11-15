package me.cryptforge.network.packet.out;

import me.cryptforge.network.ByteBuf;
import me.cryptforge.network.packet.SendablePacket;
import net.kyori.adventure.text.Component;

public record DisconnectPacket(Component reason) implements SendablePacket {
    @Override
    public void write(ByteBuf buffer) {
        buffer.writeComponent(reason);
    }
}
