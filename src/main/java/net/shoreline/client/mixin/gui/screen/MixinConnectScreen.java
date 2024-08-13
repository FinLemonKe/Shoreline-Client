package net.shoreline.client.mixin.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.shoreline.client.init.Managers;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author linus
 * @since 1.0
 */
@Mixin(ConnectScreen.class)
public class MixinConnectScreen {
    @Inject(method = "connect(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;Lnet/minecraft/client/network/ServerInfo;Lnet/minecraft/client/network/CookieStorage;)V", at = @At(value = "HEAD"))
    private void onConnect(final MinecraftClient client, final ServerAddress address, final ServerInfo info, @Nullable final CookieStorage cookieStorage, CallbackInfo ci) {
        Managers.NETWORK.setAddress(address);
        Managers.NETWORK.setInfo(info);
    }
}
