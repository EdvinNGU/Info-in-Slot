package com.drillslotinfo.mixin;

import com.drillslotinfo.DrillItemParser;
import com.drillslotinfo.ParsedDrillData;
import com.drillslotinfo.client.DrillSlotInfoClient;
import com.drillslotinfo.config.DrillConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    @Shadow protected int x;
    @Shadow protected int y;

    @Inject(method = "render", at = @At("TAIL"))
    private void afterRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!DrillConfig.enabled) return;

        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        HandledScreen<?> self = (HandledScreen<?>) (Object) this;

        for (Slot slot : self.getScreenHandler().slots) {
            if (slot.getStack().isEmpty()) continue;

            ParsedDrillData data = DrillItemParser.getData(slot.getStack());
            if (data == null) continue;

            // slot.x / slot.y are GUI-relative; this.x / this.y are the GUI origin in screen space
            DrillSlotInfoClient.renderOverlay(context, tr, data,
                    this.x + slot.x, this.y + slot.y);
        }
    }
}
