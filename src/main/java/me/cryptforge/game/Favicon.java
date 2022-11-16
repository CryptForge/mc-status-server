package me.cryptforge.game;

import java.util.Base64;

public record Favicon(
        byte[] data
) {

    public String encode() {
        return Base64.getEncoder().encodeToString(data);
    }

}
