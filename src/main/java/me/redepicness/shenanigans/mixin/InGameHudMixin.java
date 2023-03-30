package me.redepicness.shenanigans.mixin;

import me.redepicness.shenanigans.ShenanigansModKt;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    //Redirect the entire renderCrosshair call within render method
    /*@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderCrosshair(Lnet/minecraft/client/util/math/MatrixStack;)V"), method = "render")
    private void renderCrosshairProxy(InGameHud hud, MatrixStack matrices) {
        ShenanigansModKt.renderCustomCrosshair(matrices);
    }*/

    //Inject at the top od the renderCrosshair
    /*@Inject(at = @At("HEAD"), method = "renderCrosshair")
    private void renderCrosshair(MatrixStack matrices, CallbackInfo info) {
        ShenanigansModKt.renderCustomCrosshair(matrices);
        info.cancel();
    }*/

    //Inject before the blend function, to allow other crosshair checks to run before us
    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;blendFuncSeparate(Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;)V"), method = "renderCrosshair", cancellable = true)
    private void renderCrosshair(MatrixStack matrices, CallbackInfo info) {
        ShenanigansModKt.renderCustomCrosshair(matrices);
        info.cancel();
    }

}
