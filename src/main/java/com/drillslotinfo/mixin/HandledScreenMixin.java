package com.drillslotinfo.mixin;

import com.drillslotinfo.DrillItemParser;
import com.drillslotinfo.ParsedDrillData;
import com.drillslotinfo.client.DrillSlotInfoClient;
import com.drillslotinfo.config.DrillConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class HandledScreenMixin {

    @Shadow protected int leftPos;
    @Shadow protected int topPos;

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void afterExtractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!DrillConfig.enabled) return;

        Font font = Minecraft.getInstance().font;
        AbstractContainerScreen<?> self = (AbstractContainerScreen<?>) (Object) this;

        for (Slot slot : self.getMenu().slots) {
            ItemStack stack = slot.getItem();
            if (stack.isEmpty()) continue;

            ParsedDrillData data = DrillItemParser.getData(stack);
            if (data == null) continue;

            DrillSlotInfoClient.renderOverlay(context, font, data,
                    this.leftPos + slot.x, this.topPos + slot.y);
        }
    }
}
