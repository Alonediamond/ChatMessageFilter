package com.alonediamond.client.mixin;

import com.alonediamond.client.filter.ChatFilterManager;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatListener.class)
public class ChatListenerMixin {

    @Inject(method = "handleSystemMessage(Lnet/minecraft/network/chat/Component;Z)V",
        at = @At("HEAD"), cancellable = true)
    private void filterSystemMessage(Component message, boolean overlay, CallbackInfo ci) {
        if (!ChatFilterManager.getInstance().isEnabled()) return;
        Component filtered = ChatFilterManager.getInstance().filter(message);
        if (message.getString().equals(filtered.getString())) return;
        ci.cancel();
        ((ChatListener) (Object) this).handleSystemMessage(filtered, overlay);
    }

    @Inject(method = "handleDisguisedChatMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/ChatType$Bound;)V",
        at = @At("HEAD"), cancellable = true)
    private void filterDisguisedChatMessage(Component message, net.minecraft.network.chat.ChatType.Bound chatTypeBound, CallbackInfo ci) {
        if (!ChatFilterManager.getInstance().isEnabled()) return;
        Component filtered = ChatFilterManager.getInstance().filter(message);
        if (message.getString().equals(filtered.getString())) return;
        ci.cancel();
        ((ChatListener) (Object) this).handleDisguisedChatMessage(filtered, chatTypeBound);
    }
}
