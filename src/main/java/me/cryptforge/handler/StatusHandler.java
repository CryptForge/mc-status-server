package me.cryptforge.handler;

import me.cryptforge.Main;
import me.cryptforge.game.MinecraftVersion;
import me.cryptforge.game.ServerStatus;
import me.cryptforge.network.Connection;
import me.cryptforge.network.packet.Packet;
import me.cryptforge.network.packet.in.PingRequestPacket;
import me.cryptforge.network.packet.in.StatusRequestPacket;
import me.cryptforge.network.packet.out.PingResponsePacket;
import me.cryptforge.network.packet.out.StatusResponsePacket;

public final class StatusHandler extends PacketHandler {

    public StatusHandler(Connection connection) {
        super(connection);
    }

    @Override
    public void handlePacket(Packet packet) {
        if (packet instanceof StatusRequestPacket) {
            final ServerStatus status = new ServerStatus(
                    MinecraftVersion.getVersion(connection().getProtocolVersion()),
                    Main.config().hidePlayers(),
                    new ServerStatus.PlayerSample(
                            Main.config().maxPlayers(),
                            Main.config().onlinePlayers(),
                            Main.config().sample()
                    ),
                    Main.config().motd(),
                    Main.config().favicon(),
                    true,
                    true
            );

            connection().sendPacket(new StatusResponsePacket(status));
            return;
        }
        if (packet instanceof PingRequestPacket pingRequestPacket) {
            connection().sendPacket(new PingResponsePacket(pingRequestPacket.payload()));
            connection().close();
        }
    }
}
