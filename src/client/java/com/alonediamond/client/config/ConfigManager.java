package com.alonediamond.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("ChatMessageFilter.json");

    private static ConfigManager instance;
    private FilterConfig config;

    public static ConfigManager getInstance() {
        if (instance == null) instance = new ConfigManager();
        return instance;
    }

    private ConfigManager() {}

    public FilterConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                config = GSON.fromJson(reader, FilterConfig.class);
                if (config == null) config = new FilterConfig();
                if (config.words == null) config.words = new java.util.ArrayList<>();
            } catch (IOException e) {
                config = new FilterConfig();
            }
        } else {
            config = new FilterConfig();
        }
        return config;
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            org.slf4j.LoggerFactory.getLogger("chat-message-filter").error("Failed to save config", e);
        }
    }

    public FilterConfig getConfig() {
        if (config == null) load();
        return config;
    }
}
