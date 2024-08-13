package net.shoreline.client.mixin.item;

import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.shoreline.client.Shoreline;
import net.shoreline.client.impl.event.item.DurabilityEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author linus
 * @since 1.0
 */
@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    /**
     * @return
     */
    @Shadow
    public abstract int getDamage();

    @Shadow @Nullable public abstract <T> T set(DataComponentType<? super T> type, @Nullable T value);

    /**
     * @param item
     * @param count
     * @param ci
     */
    @Inject(method = "<init>(Lnet/minecraft/item/ItemConvertible;I)V", at = @At(
            value = "RETURN"))
    private void hookInitItem(ItemConvertible item, int count, CallbackInfo ci) {
        if (Shoreline.EVENT_HANDLER == null) {
            return;
        }
        DurabilityEvent durabilityEvent = new DurabilityEvent(getDamage());
        Shoreline.EVENT_HANDLER.dispatch(durabilityEvent);
        if (durabilityEvent.isCanceled()) {
            set(DataComponentTypes.DAMAGE, durabilityEvent.getDamage());
        }
    }
}
