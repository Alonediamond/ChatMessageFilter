# Chat Message Filter

A client-side Fabric mod that filters chat messages by removing unwanted words before they appear in the chat HUD.

> This is the **Minecraft 1.21.11** version. For Minecraft 26.1.2, see the [master branch](https://github.com/Alonediamond/ChatMessageFilter).

## Features

- **Silent word removal** — blocked words are deleted from chat messages (not replaced with stars or other characters)
- **Chinese + English support** — works seamlessly with mixed-language chat
- **Case-insensitive mode** — optionally ignores letter case when matching
- **Config GUI** — manage filter words via Mod Menu integration with add / delete / clear / save
- **Hot reload** — configuration changes take effect immediately, no restart needed
- **Client-only** — runs entirely on the client; no server installation required

## Requirements

| Dependency    | Version          |
|---------------|------------------|
| Minecraft     | 1.21.11          |
| Fabric Loader | >= 0.19.3        |
| Fabric API    | 0.141.4+1.21.11  |
| Java          | >= 21            |
| Mod Menu      | 17.0.0 (optional, for config GUI) |

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.11
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. (Optional) Install [Mod Menu](https://modrinth.com/mod/modmenu) for the in-game config screen
4. Place `chat-message-filter-<version>.jar` into your `mods/` folder

## Usage

### Quick Start

1. Launch Minecraft with the mod installed
2. Open Mod Menu → **Chat Message Filter** (or use the config button)
3. Toggle **Enable Filter** to ON
4. Add words you want to block (e.g. `badword`, `测试词`) in the input field
5. Click **Save** — filtering takes effect immediately
6. Type a message containing a blocked word in chat — the word will be silently removed

### Configuration

The mod stores settings in `config/ChatMessageFilter.json`:

```json
{
  "enabled": true,
  "caseSensitive": false,
  "words": [
    "badword",
    "测试词"
  ]
}
```

| Field           | Type      | Description                               |
|-----------------|-----------|-------------------------------------------|
| `enabled`       | `boolean` | Master switch (default: `false`)          |
| `caseSensitive` | `boolean` | Case-sensitive matching (default: `false`) |
| `words`         | `list`    | Words to filter from chat                 |

You can edit this file directly while the game is closed, or use the in-game GUI.

### Config GUI Overview

| Component       | Description                                |
|-----------------|--------------------------------------------|
| Enable Filter   | Master on/off toggle                       |
| Case Sensitive  | Toggle case-sensitive matching             |
| Word list       | Scrollable list of filter words with ✕ to remove |
| Add Word field  | Type a word and click Add                  |
| Clear All       | Remove all filter words                    |
| Save            | Write config to disk + hot reload          |

## Filtering Rules

- Only matches in **chat HUD messages** (not signs, books, etc.)
- Performs **substring matching** (not regex, not whole-word)
- **Deletes** the matched text (no replacement character)
- Preserves all formatting (colors, hover events, click events, styles)
- Recursively traverses the text component tree; only filters leaf text nodes

## How It Works (Technical)

```
ChatListener (entry point)
  └─ handlePlayerChatMessage / handleSystemMessage / handleDisguisedChatMessage
       └─ ChatComponent
            └─ addMessage(Component, MessageSignature, GuiMessageTag) [single entry]
```

The mod injects into both `ChatListener` and `ChatComponent` via Mixin. When a message arrives, the filter:

1. Checks if filtering is enabled and words are configured
2. Recursively walks the `Component` tree
3. For each `PlainTextContents` node, applies string replacement (case-sensitive or case-insensitive)
4. Returns a new `MutableComponent` with filtered text, preserving all original styling

The filtered message is re-submitted through the same method. Since removed words cannot re-create blocked words (deletion is idempotent), the second pass finds no matches and the message displays normally.

## Versions

| Minecraft | Branch / Tag                                                |
|-----------|-------------------------------------------------------------|
| 26.1.2    | [master](https://github.com/Alonediamond/ChatMessageFilter) |
| 1.21.11   | [1.21.11](https://github.com/Alonediamond/ChatMessageFilter/tree/1.21.11) |

## Building from Source

```bash
./gradlew build
```

The compiled JAR will be at `build/libs/chat-message-filter-<version>.jar`.

Requires JDK 21+.

## License

[MIT](LICENSE)
