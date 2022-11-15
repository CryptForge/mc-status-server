package me.cryptforge.network.packet.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.cryptforge.Main;
import me.cryptforge.game.ServerStatus;
import me.cryptforge.network.ByteBuf;
import me.cryptforge.network.packet.SendablePacket;

import java.util.HashMap;
import java.util.Map;

public record StatusResponsePacket(ServerStatus status) implements SendablePacket {

    private static final Map<ServerStatus, String> cache = new HashMap<>();

    @Override
    public void write(ByteBuf buffer) {
        final String json;
        if (cache.containsKey(status)) {
            json = cache.get(status);
        } else {
            try {
                json = Main.objectMapper().writeValueAsString(status);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize server status", e);
            }
            cache.put(status, json);
        }
        buffer.writeString(json);
    }
}
