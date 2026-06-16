package com.alonediamond.client.mixin;

import com.alonediamond.client.filter.ChatFilterManager;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {

    @ModifyVariable(method = "addClientSystemMessage(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"), argsOnly = true)
    private Component filterClientSystemMessage(Component message) {
        if (!ChatFilterManager.getInstance().isEnabled()) return message;
        return ChatFilterManager.getInstance().filter(message);
    }

    @ModifyVariable(method = "addServerSystemMessage(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"), argsOnly = true)
    private Component filterServerSystemMessage(Component message) {
        if (!ChatFilterManager.getInstance().isEnabled()) return message;
        return ChatFilterManager.getInstance().filter(message);
    }

    @ModifyVariable(method = "addPlayerMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V", at = @At("HEAD"), argsOnly = true)
    private Component filterPlayerMessage(Component message) {
        if (!ChatFilterManager.getInstance().isEnabled()) return message;
        return ChatFilterManager.getInstance().filter(message);
    }
}
