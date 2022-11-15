package me.cryptforge.handler;

import me.cryptforge.Main;
import me.cryptforge.network.Connection;
import me.cryptforge.network.packet.Packet;
import me.cryptforge.network.packet.in.LoginStartPacket;
import me.cryptforge.network.packet.out.DisconnectPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoginHandler extends PacketHandler {

    private final Logger logger = LoggerFactory.getLogger(LoginHandler.class);

    public LoginHandler(Connection connection) {
        super(connection);
    }

    @Override
    public void handlePacket(Packet packet) {
        if (packet instanceof LoginStartPacket startPacket) {
            logger.info("{} attempted to login with username {}", connection().getAddress().getHostString(), startPacket.username());
            connection().sendPacket(new DisconnectPacket(Main.config().disconnectMessage()));
            connection().close();
        }
    }
}
