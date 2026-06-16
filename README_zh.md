# Chat Message Filter（聊天消息过滤器）

一个客户端 Fabric 模组，在聊天消息显示到 HUD 之前删除不需要的词语。

> 此为 **Minecraft 1.21.11** 版本。Minecraft 26.1.2 版本请见 [master 分支](https://github.com/Alonediamond/ChatMessageFilter)。

## 功能

- **静默删除** — 屏蔽词会被直接删除，不替换为星号或其他字符
- **中英文支持** — 无缝支持中英文混合聊天
- **大小写敏感选项** — 可选择是否区分字母大小写
- **配置界面** — 通过 Mod Menu 集成管理屏蔽词（添加 / 删除 / 清空 / 保存）
- **热重载** — 配置修改后即时生效，无需重启
- **纯客户端** — 完全在客户端运行，无需服务端安装

## 环境要求

| 依赖          | 版本             |
|---------------|------------------|
| Minecraft     | 1.21.11          |
| Fabric Loader | >= 0.19.3        |
| Fabric API    | 0.141.4+1.21.11  |
| Java          | >= 21            |
| Mod Menu      | 17.0.0（可选，用于配置界面） |

## 安装

1. 为 Minecraft 1.21.11 安装 [Fabric Loader](https://fabricmc.net/use/)
2. 安装 [Fabric API](https://modrinth.com/mod/fabric-api)
3. （可选）安装 [Mod Menu](https://modrinth.com/mod/modmenu) 以使用游戏内配置界面
4. 将 `chat-message-filter-<version>.jar` 放入 `mods/` 文件夹

## 使用方法

### 快速开始

1. 启动安装了模组的 Minecraft
2. 打开 Mod Menu → **Chat Message Filter**（或点击配置按钮）
3. 将 **启用过滤** 切换为 ON
4. 在输入框中添加要屏蔽的词语（例如 `badword`、`测试词`）
5. 点击 **保存** — 过滤即刻生效
6. 在聊天中发送包含屏蔽词的消息 — 词语将被静默删除

### 配置文件

模组将配置存储在 `config/ChatMessageFilter.json`：

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

| 字段             | 类型      | 说明                         |
|------------------|-----------|------------------------------|
| `enabled`        | `boolean` | 总开关（默认：`false`）       |
| `caseSensitive`  | `boolean` | 区分大小写（默认：`false`）   |
| `words`          | `list`    | 要过滤的词语列表              |

可以在游戏关闭时直接编辑此文件，也可以使用游戏内 GUI 进行管理。

### 配置界面一览

| 组件         | 说明                           |
|--------------|--------------------------------|
| 启用过滤     | 总开关                         |
| 区分大小写   | 切换大小写敏感匹配              |
| 词语列表     | 可滚动的过滤词列表，点击 ✕ 删除 |
| 添加词语     | 输入词语后点击添加              |
| 清空全部     | 删除所有过滤词                  |
| 保存         | 写入配置文件 + 热重载          |

## 过滤规则

- 仅作用于**聊天栏消息**（不影响告示牌、书本等）
- 使用**子串匹配**（非正则，非全词匹配）
- **直接删除**匹配文本（不替换为其他字符）
- 保留所有格式（颜色、悬停事件、点击事件、样式）
- 递归遍历文本组件树，仅过滤叶子文本节点

## 技术原理

```
ChatListener（入口）
  └─ handlePlayerChatMessage / handleSystemMessage / handleDisguisedChatMessage
       └─ ChatComponent
            └─ addMessage(Component, MessageSignature, GuiMessageTag) [单一入口]
```

模组通过 Mixin 同时注入 `ChatListener` 和 `ChatComponent`。当消息到达时，过滤器：

1. 检查过滤是否启用且已配置词语
2. 递归遍历 `Component` 树
3. 对每个 `PlainTextContents` 节点执行字符串替换（支持大小写敏感/不敏感）
4. 返回过滤后的 `MutableComponent`，保留全部原始样式

过滤后的消息通过同一方法重新提交。由于被删除的词语无法重新构成屏蔽词（删除操作是幂等的），第二次检查不会匹配，消息正常显示。

## 版本

| Minecraft | 分支                                                          |
|-----------|---------------------------------------------------------------|
| 26.1.2    | [master](https://github.com/Alonediamond/ChatMessageFilter)   |
| 1.21.11   | [1.21.11](https://github.com/Alonediamond/ChatMessageFilter/tree/1.21.11) |

## 从源码构建

```bash
./gradlew build
```

编译后的 JAR 位于 `build/libs/chat-message-filter-<version>.jar`。

需要 JDK 21+。

## 许可证

[MIT](LICENSE)
