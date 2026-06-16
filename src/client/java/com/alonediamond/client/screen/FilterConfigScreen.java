package com.alonediamond.client.screen;

import com.alonediamond.client.config.ConfigManager;
import com.alonediamond.client.config.FilterConfig;
import com.alonediamond.client.filter.ChatFilterManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class FilterConfigScreen extends Screen {
    private final Screen parent;
    private final FilterConfig config;
    private boolean enabled;
    private boolean caseSensitive;
    private final List<String> words;
    private EditBox addWordField;
    private int scrollOffset;

    private static final int LIST_START_Y = 85;
    private static final int LIST_HEIGHT = 130;
    private static final int ENTRY_HEIGHT = 22;

    public FilterConfigScreen(Screen parent) {
        super(Component.translatable("chatfilter.title"));
        this.parent = parent;
        this.config = ConfigManager.getInstance().getConfig();
        this.enabled = config.enabled;
        this.caseSensitive = config.caseSensitive;
        this.words = new ArrayList<>(config.words);
        this.scrollOffset = 0;
    }

    @Override
    protected void init() {
        this.clearWidgets();

        int centerX = this.width / 2;
        int maxVisible = LIST_HEIGHT / ENTRY_HEIGHT;
        int maxScroll = Math.max(0, words.size() - maxVisible);
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;

        addRenderableWidget(Button.builder(
            Component.translatable("chatfilter.enable").append(": ").append(
                enabled ? Component.translatable("chatfilter.enabled") : Component.translatable("chatfilter.disabled")),
            btn -> { enabled = !enabled; rebuildWidgets(); })
            .bounds(centerX - 100, 35, 200, 20).build());

        addRenderableWidget(Button.builder(
            Component.translatable("chatfilter.case_sensitive").append(": ").append(
                caseSensitive ? Component.translatable("chatfilter.enabled") : Component.translatable("chatfilter.disabled")),
            btn -> { caseSensitive = !caseSensitive; rebuildWidgets(); })
            .bounds(centerX - 100, 60, 200, 20).build());

        // Scroll up button
        if (scrollOffset > 0) {
            addRenderableWidget(Button.builder(Component.literal("▲"),
                btn -> { scrollOffset--; rebuildWidgets(); })
                .bounds(centerX + 95, LIST_START_Y, 20, 20).build());
        }

        // Word list with remove buttons
        for (int i = scrollOffset; i < Math.min(words.size(), scrollOffset + maxVisible); i++) {
            final int idx = i;
            int y = LIST_START_Y + (i - scrollOffset) * ENTRY_HEIGHT;

            StringWidget label = new StringWidget(0, 0, 160, 20,
                Component.literal(words.get(i)), this.font);
            label.setX(centerX - 90);
            label.setY(y);
            addRenderableWidget(label);

            addRenderableWidget(Button.builder(Component.literal("✕"),
                btn -> { words.remove(idx); rebuildWidgets(); })
                .bounds(centerX + 70, y, 20, 20).build());
        }

        // Scroll down button
        if (scrollOffset < maxScroll) {
            addRenderableWidget(Button.builder(Component.literal("▼"),
                btn -> { scrollOffset++; rebuildWidgets(); })
                .bounds(centerX + 95, LIST_START_Y + (Math.min(words.size(), scrollOffset + maxVisible) - scrollOffset) * ENTRY_HEIGHT - 20, 20, 20).build());
        }

        addWordField = new EditBox(this.font, centerX - 100, LIST_START_Y + LIST_HEIGHT + 5, 140, 20,
            Component.translatable("chatfilter.enter_word"));
        addRenderableWidget(addWordField);

        addRenderableWidget(Button.builder(Component.translatable("chatfilter.add_word"),
            btn -> {
                String word = addWordField.getValue().trim();
                if (!word.isEmpty() && !words.contains(word)) {
                    words.add(word);
                    addWordField.setValue("");
                    rebuildWidgets();
                }
            })
            .bounds(centerX + 45, LIST_START_Y + LIST_HEIGHT + 5, 55, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("chatfilter.clear_all"),
            btn -> { words.clear(); rebuildWidgets(); })
            .bounds(centerX - 100, LIST_START_Y + LIST_HEIGHT + 35, 95, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("chatfilter.save"),
            btn -> {
                config.enabled = enabled;
                config.caseSensitive = caseSensitive;
                config.words = new ArrayList<>(words);
                ConfigManager.getInstance().save();
                ChatFilterManager.getInstance().reload();
                this.minecraft.setScreen(parent);
            })
            .bounds(centerX + 5, LIST_START_Y + LIST_HEIGHT + 35, 95, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("gui.done"),
            btn -> onClose())
            .bounds(centerX - 100, this.height - 30, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics extractor, int mouseX, int mouseY, float delta) {
        super.render(extractor, mouseX, mouseY, delta);
        extractor.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);

        if (words.isEmpty()) {
            extractor.drawCenteredString(this.font, Component.translatable("chatfilter.no_words"),
                this.width / 2, LIST_START_Y + 50, 0x808080);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int maxVisible = LIST_HEIGHT / ENTRY_HEIGHT;
        int maxScroll = Math.max(0, words.size() - maxVisible);
        int newOffset = scrollOffset - (int) scrollY;
        if (newOffset < 0) newOffset = 0;
        if (newOffset > maxScroll) newOffset = maxScroll;
        if (newOffset != scrollOffset) {
            scrollOffset = newOffset;
            rebuildWidgets();
        }
        return true;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
