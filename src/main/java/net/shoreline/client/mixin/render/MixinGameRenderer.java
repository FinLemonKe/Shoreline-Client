package net.shoreline.client.mixin.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.shoreline.client.Shoreline;
import net.shoreline.client.impl.event.network.ReachEvent;
import net.shoreline.client.impl.event.render.*;
import net.shoreline.client.impl.event.world.UpdateCrosshairTargetEvent;
import net.shoreline.client.init.Programs;
import net.shoreline.client.util.Globals;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author linus
 * @see GameRenderer
 * @since 1.0
 */
@Mixin(GameRenderer.class)
public class MixinGameRenderer implements Globals {
    //
    @Shadow
    @Final
    MinecraftClient client;

    @Shadow
    private float lastFovMultiplier;

    @Shadow
    private float fovMultiplier;

    @Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = {"ldc=hand"}), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onRenderWorld(float tickDelta, long limitTime, CallbackInfo ci, boolean bl, Camera camera, Entity entity, double d, Matrix4f matrix4f, MatrixStack matrixStack, float f, float g, Matrix4f matrix4f2) {
        RenderWorldEvent.Game renderWorldEvent = new RenderWorldEvent.Game(matrixStack, tickDelta);
        Shoreline.EVENT_HANDLER.dispatch(renderWorldEvent);
    }

    @Inject(method = "updateCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", shift = At.Shift.AFTER))
    private void hookUpdateTargetedEntity$1(final float tickDelta, final CallbackInfo info) {
        UpdateCrosshairTargetEvent event = new UpdateCrosshairTargetEvent(tickDelta, client.getCameraEntity());
        Shoreline.EVENT_HANDLER.dispatch(event);
    }

    /**
     * @param matrices
     * @param tickDelta
     * @param ci
     */
    @Inject(method = "tiltViewWhenHurt", at = @At(value = "HEAD"),
            cancellable = true)
    private void hookTiltViewWhenHurt(MatrixStack matrices, float tickDelta,
                                      CallbackInfo ci) {
        HurtCamEvent hurtCamEvent = new HurtCamEvent();
        Shoreline.EVENT_HANDLER.dispatch(hurtCamEvent);
        if (hurtCamEvent.isCanceled()) {
            ci.cancel();
        }
    }

    /**
     * @param floatingItem
     * @param ci
     */
    @Inject(method = "showFloatingItem", at = @At(value = "HEAD"),
            cancellable = true)
    private void hookShowFloatingItem(ItemStack floatingItem, CallbackInfo ci) {
        RenderFloatingItemEvent renderFloatingItemEvent =
                new RenderFloatingItemEvent(floatingItem);
        Shoreline.EVENT_HANDLER.dispatch(renderFloatingItemEvent);
        if (renderFloatingItemEvent.isCanceled()) {
            ci.cancel();
        }
    }

    /**
     * @param distortionStrength
     * @param ci
     */
    @Inject(method = "renderNausea", at = @At(value = "HEAD"), cancellable = true)
    private void hookRenderNausea(DrawContext context, float distortionStrength, CallbackInfo ci) {
        RenderNauseaEvent renderNauseaEvent = new RenderNauseaEvent();
        Shoreline.EVENT_HANDLER.dispatch(renderNauseaEvent);
        if (renderNauseaEvent.isCanceled()) {
            ci.cancel();
        }
    }

    /**
     * @param cir
     */
    @Inject(method = "shouldRenderBlockOutline", at = @At(value = "HEAD"),
            cancellable = true)
    private void hookShouldRenderBlockOutline(CallbackInfoReturnable<Boolean> cir) {
        RenderBlockOutlineEvent renderBlockOutlineEvent =
                new RenderBlockOutlineEvent();
        Shoreline.EVENT_HANDLER.dispatch(renderBlockOutlineEvent);
        if (renderBlockOutlineEvent.isCanceled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    /**
     * @param tickDelta
     * @param info
     */
    @Inject(method = "updateCrosshairTarget", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/render/GameRenderer;findCrosshairTarget(Lnet/minecraft/entity/Entity;DDF)Lnet/minecraft/util/hit/HitResult;"), cancellable = true)
    private void hookUpdateTargetedEntity$2(float tickDelta, CallbackInfo info) {
        TargetEntityEvent targetEntityEvent = new TargetEntityEvent();
        Shoreline.EVENT_HANDLER.dispatch(targetEntityEvent);
        if (targetEntityEvent.isCanceled() && client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            client.getProfiler().pop();
            info.cancel();
        }
    }


    /**
     * @param matrices
     * @param tickDelta
     * @param ci
     */
    @Inject(method = "bobView", at = @At(value = "HEAD"), cancellable = true)
    private void hookBobView(MatrixStack matrices, float tickDelta,
                             CallbackInfo ci) {
        BobViewEvent bobViewEvent = new BobViewEvent();
        Shoreline.EVENT_HANDLER.dispatch(bobViewEvent);
        if (bobViewEvent.isCanceled()) {
            ci.cancel();
        }
    }

    /**
     *
     * @param camera
     * @param tickDelta
     * @param changingFov
     * @param cir
     */
    @Inject(method = "getFov", at = @At(value = "HEAD"), cancellable = true)
    private void hookGetFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        FovEvent fovEvent = new FovEvent();
        Shoreline.EVENT_HANDLER.dispatch(fovEvent);
        if (fovEvent.isCanceled()) {
            cir.cancel();
            cir.setReturnValue(fovEvent.getFov() * (double) MathHelper.lerp(tickDelta, lastFovMultiplier, fovMultiplier));
        }
    }

    /**
     * @param factory
     * @param ci
     */
    @Inject(method = "loadPrograms", at = @At(value = "INVOKE",
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
            ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void initPrograms(ResourceFactory factory, CallbackInfo ci) {
        Programs.initPrograms();
    }
}
