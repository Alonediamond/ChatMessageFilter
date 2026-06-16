package com.alonediamond.client;

import com.alonediamond.client.config.ConfigManager;
import com.alonediamond.client.filter.ChatFilterManager;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatMessageFilterClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("chat-message-filter");

    @Override
    public void onInitializeClient() {
        ConfigManager.getInstance().load();
        ChatFilterManager.getInstance().reload();
        LOGGER.info("ChatMessageFilter initialized - enabled: {}, words: {}",
            ConfigManager.getInstance().getConfig().enabled,
            ConfigManager.getInstance().getConfig().words.size());
    }
}
