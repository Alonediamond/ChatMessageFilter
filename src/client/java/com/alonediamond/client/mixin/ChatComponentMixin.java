package com.alonediamond.client.mixin;

import com.alonediamond.client.filter.ChatFilterManager;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V",
            at = @At("HEAD"), cancellable = true)
    private void filterClientSystemMessage(Component message, MessageSignature messageSignature, GuiMessageTag guiMessageTag, CallbackInfo ci) {
        if (!ChatFilterManager.getInstance().isEnabled()) return;
        Component filtered = ChatFilterManager.getInstance().filter(message);
        if (message.getString().equals(filtered.getString())) return;
        ci.cancel();
        ((ChatComponent) (Object) this).addMessage(filtered);
    }

    /*@Inject(method = "addClientSystemMessage(Lnet/minecraft/network/chat/Component;)V",
        at = @At("HEAD"), cancellable = true)
    private void filterClientSystemMessage(Component message, CallbackInfo ci) {
        if (!ChatFilterManager.getInstance().isEnabled()) return;
        Component filtered = ChatFilterManager.getInstance().filter(message);
        if (message.getString().equals(filtered.getString())) return;
        ci.cancel();
        ((ChatComponent) (Object) this).addClientSystemMessage(filtered);
    }

    @Inject(method = "addServerSystemMessage(Lnet/minecraft/network/chat/Component;)V",
        at = @At("HEAD"), cancellable = true)
    private void filterServerSystemMessage(Component message, CallbackInfo ci) {
        if (!ChatFilterManager.getInstance().isEnabled()) return;
        Component filtered = ChatFilterManager.getInstance().filter(message);
        if (message.getString().equals(filtered.getString())) return;
        ci.cancel();
        ((ChatComponent) (Object) this).addServerSystemMessage(filtered);
    }

    @Inject(method = "addPlayerMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V",
        at = @At("HEAD"), cancellable = true)
    private void filterPlayerMessage(Component message, MessageSignature signature, GuiMessageTag tag, CallbackInfo ci) {
        if (!ChatFilterManager.getInstance().isEnabled()) return;
        Component filtered = ChatFilterManager.getInstance().filter(message);
        if (message.getString().equals(filtered.getString())) return;
        ci.cancel();
        ((ChatComponent) (Object) this).addPlayerMessage(filtered, signature, tag);
    }*/
}
