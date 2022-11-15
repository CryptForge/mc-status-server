package me.cryptforge.game;

import net.kyori.adventure.text.Component;

import java.util.List;

public record ServerStatus(
        MinecraftVersion version,
        boolean hidePlayers,
        PlayerSample players,
        Component description,
        boolean previewsChat,
        boolean enforcesSecureChat
) {

    public record PlayerSample(int max, int online, List<Player> sample) {
    }

}
