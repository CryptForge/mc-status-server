package me.cryptforge;

import me.cryptforge.exception.InvalidConfigValueException;
import me.cryptforge.game.Favicon;
import me.cryptforge.game.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public record Config(
        String address,
        int port,
        Component disconnectMessage,
        Component motd,
        Favicon favicon,
        boolean hidePlayers,
        int onlinePlayers,
        int maxPlayers,
        List<Player> sample
) {

    public static Config load(CommentedConfigurationNode root) {
        final MiniMessage miniMessage = MiniMessage.miniMessage();

        final String address = notNull(root.node("address").getString(), "address");

        final int port = root.node("port").getInt();
        check(port >= 1 && port <= 65535, "Port out of range");

        final Component disconnectMessage = miniMessage.deserialize(notNull(root.node("disconnect_message").getString(), "disconnect_message"));

        final Component description = miniMessage.deserialize(notNull(root.node("motd").getString(), "motd"));

        final String faviconPath = notNull(root.node("favicon").getString(), "favicon");

        final Favicon favicon;
        if (faviconPath.isEmpty()) {
            favicon = null;
        } else {
            favicon = new Favicon(loadFile(faviconPath));
            check(favicon.data() != null, "Invalid favicon file");
        }

        final boolean hidePlayers = root.node("hide_players").getBoolean();

        final int onlinePlayers = root.node("online_players").getInt();

        final int maxPlayers = root.node("max_players").getInt();

        final List<String> sample;
        try {
            sample = notNull(root.node("sample").getList(String.class), "sample");
        } catch (SerializationException e) {
            throw new InvalidConfigValueException("Player sample is invalid");
        }

        return new Config(
                address,
                port,
                disconnectMessage,
                description,
                favicon,
                hidePlayers,
                onlinePlayers,
                maxPlayers,
                sample.stream().map(name -> new Player(name, UUID.randomUUID())).toList()
        );
    }

    private static void check(boolean condition, String error) {
        if (!condition) {
            throw new InvalidConfigValueException(error);
        }
    }

    private static <T> @NotNull T notNull(T value, String fieldName) {
        if (value == null) {
            throw new InvalidConfigValueException(fieldName + " cannot be null!");
        }
        return value;
    }

    private static byte[] loadFile(String source) {
        try (final InputStream inputStream = new FileInputStream(source)) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }

}
