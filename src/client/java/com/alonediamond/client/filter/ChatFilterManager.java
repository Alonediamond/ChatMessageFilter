package com.alonediamond.client.filter;

import com.alonediamond.client.config.ConfigManager;
import com.alonediamond.client.config.FilterConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ChatFilterManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("chat-message-filter");
    private static ChatFilterManager instance;
    private final Set<String> filterWords = new HashSet<>();

    public static ChatFilterManager getInstance() {
        if (instance == null) instance = new ChatFilterManager();
        return instance;
    }

    private ChatFilterManager() {}

    public void reload() {
        FilterConfig config = ConfigManager.getInstance().getConfig();
        filterWords.clear();
        for (String word : config.words) {
            if (!word.isEmpty()) {
                filterWords.add(word);
            }
        }
        LOGGER.info("Reloaded {} filter words", filterWords.size());
    }

    public boolean isEnabled() {
        return ConfigManager.getInstance().getConfig().enabled && !filterWords.isEmpty();
    }

    public Component filter(Component component) {
        MutableComponent result;
        var contents = component.getContents();

        if (contents instanceof PlainTextContents plain) {
            result = Component.literal(applyFilters(plain.text()));
        } else {
            result = component.copy();
        }

        result.setStyle(component.getStyle());

        for (Component sibling : component.getSiblings()) {
            result.append(filter(sibling));
        }

        return result;
    }

    private String applyFilters(String text) {
        boolean caseSensitive = ConfigManager.getInstance().getConfig().caseSensitive;
        System.out.println("Original message: " + text);
        for (String word : filterWords) {
            if (caseSensitive) {
                text = text.replace(word, "");
                System.out.println("Modified word: " + word);
                System.out.println("Modified text: " + text);
            } else {
                text = caseInsensitiveReplace(text, word);
            }
        }
        System.out.println("Final message: " + text);
        return text;
    }

    private String caseInsensitiveReplace(String text, String word) {
        StringBuilder result = new StringBuilder();
        String lowerText = text.toLowerCase(Locale.ROOT);
        String lowerWord = word.toLowerCase(Locale.ROOT);
        int lastEnd = 0;
        int index = lowerText.indexOf(lowerWord);
        while (index != -1) {
            result.append(text, lastEnd, index);
            lastEnd = index + word.length();
            index = lowerText.indexOf(lowerWord, lastEnd);
        }
        result.append(text.substring(lastEnd));
        return result.toString();
    }
}
