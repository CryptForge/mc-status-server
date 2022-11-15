package me.cryptforge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.cryptforge.game.MinecraftVersion;
import me.cryptforge.network.Server;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {

    public static final String VERSION_DATA_URL = "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/master/data/pc/common/protocolVersions.json";
    public static final String CONFIG_FILE_NAME = "config.conf";

    private static final ObjectMapper objectMapper = createObjectMapper();
    private static final GsonComponentSerializer componentSerializer = GsonComponentSerializer.gson();
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static Config config;


    public static void main(String[] args) {
        final CompletableFuture<Void> versionLoading = MinecraftVersion.load(VERSION_DATA_URL);

        createConfigFile();

        final HoconConfigurationLoader configLoader = HoconConfigurationLoader.builder().path(Path.of(CONFIG_FILE_NAME)).build();
        final CommentedConfigurationNode root;
        try {
            root = configLoader.load();
        } catch (ConfigurateException e) {
            logger.error("Something went wrong while loading the config");
            e.printStackTrace();
            return;
        }

        final Config config = Config.load(root);
        logger.info("Config loaded!");

        try {
            versionLoading.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException e) {
            logger.error("Unable to load minecraft versions");
            return;
        }
        Main.config = config;
        final Server server = new Server(config.address(), config.port());
        server.start();
    }

    private static void createConfigFile() {
        final File file = new File(CONFIG_FILE_NAME);
        if (file.exists()) {
            return;
        }

        try (final InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
            if (inputStream == null) {
                logger.error("Config file resource is not present");
                return;
            }

            Files.copy(inputStream, file.toPath());
        } catch (IOException e) {
            logger.error("Unable to copy bundled config file to file system");
            e.printStackTrace();
        }
    }

    public static Config config() {
        if (config == null) {
            throw new RuntimeException("Attempted to get config before it was loaded!");
        }
        return config;
    }

    public static ObjectMapper objectMapper() {
        return objectMapper;
    }

    public static GsonComponentSerializer componentSerializer() {
        return componentSerializer;
    }

    private static ObjectMapper createObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        final SimpleModule module = new SimpleModule();
        module.addSerializer(new ComponentSerializer());
        module.addSerializer(new ServerStatusSerializer());
        mapper.registerModule(module);
        return mapper;
    }
}