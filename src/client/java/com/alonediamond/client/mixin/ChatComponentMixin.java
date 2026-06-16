package com.alonediamond.client.mixin;

import com.alonediamond.client.filter.ChatFilterManager;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {

    @Unique
    private boolean chatMessageFilter$filtering;

    @Invoker("addClientSystemMessage")
    public abstract void invokeAddClientSystemMessage(Component message);

    @Invoker("addServerSystemMessage")
    public abstract void invokeAddServerSystemMessage(Component message);

    @Invoker("addPlayerMessage")
    public abstract void invokeAddPlayerMessage(Component message, MessageSignature signature, GuiMessageTag tag);

    @Inject(method = "addClientSystemMessage(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"), cancellable = true)
    private void filterClientSystemMessage(Component message, CallbackInfo ci) {
        if (chatMessageFilter$filtering || !ChatFilterManager.getInstance().isEnabled()) return;
        chatMessageFilter$filtering = true;
        try {
            ci.cancel();
            this.invokeAddClientSystemMessage(ChatFilterManager.getInstance().filter(message));
        } finally {
            chatMessageFilter$filtering = false;
        }
    }

    @Inject(method = "addServerSystemMessage(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"), cancellable = true)
    private void filterServerSystemMessage(Component message, CallbackInfo ci) {
        if (chatMessageFilter$filtering || !ChatFilterManager.getInstance().isEnabled()) return;
        chatMessageFilter$filtering = true;
        try {
            ci.cancel();
            this.invokeAddServerSystemMessage(ChatFilterManager.getInstance().filter(message));
        } finally {
            chatMessageFilter$filtering = false;
        }
    }

    @Inject(method = "addPlayerMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V", at = @At("HEAD"), cancellable = true)
    private void filterPlayerMessage(Component message, MessageSignature signature, GuiMessageTag tag, CallbackInfo ci) {
        if (chatMessageFilter$filtering || !ChatFilterManager.getInstance().isEnabled()) return;
        chatMessageFilter$filtering = true;
        try {
            ci.cancel();
            this.invokeAddPlayerMessage(ChatFilterManager.getInstance().filter(message), signature, tag);
        } finally {
            chatMessageFilter$filtering = false;
        }
    }
}
