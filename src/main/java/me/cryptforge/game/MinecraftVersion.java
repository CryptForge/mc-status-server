package me.cryptforge.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import me.cryptforge.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public record MinecraftVersion(
        @JsonProperty("minecraftVersion") String name,
        @JsonProperty("version") int protocol,
        @JsonProperty("dataVersion") int dataVersion,
        @JsonProperty("usesNetty") boolean netty,
        @JsonProperty("majorVersion") String majorVersion
) {

    private static final Logger logger = LoggerFactory.getLogger(MinecraftVersion.class);
    private static final Map<Integer, MinecraftVersion> protocolVersionMap = new HashMap<>();

    public static CompletableFuture<Void> load(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final List<MinecraftVersion> list = Main.objectMapper().readValue(new URL(url), new TypeReference<>() {
                });
                final Map<Integer, MinecraftVersion> map = new HashMap<>();

                list.forEach(version -> map.put(version.protocol(), version));

                return Collections.unmodifiableMap(map);
            } catch (IOException e) {
                logger.error("Failed to load minecraft versions!");
                throw new RuntimeException(e);
            }
        }).thenAccept(protocolVersionMap::putAll);
    }

    public static MinecraftVersion getVersion(int protocolVersion) {
        return protocolVersionMap.get(protocolVersion);
    }

    @Override
    public String toString() {
        return name;
    }
}
