package me.cryptforge.game;

import java.util.UUID;

public record Player(
        String name,
        UUID id
) {
}
