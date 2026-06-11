package com.drillslotinfo.mixin;

import com.drillslotinfo.DrillItemParser;
import com.drillslotinfo.ParsedDrillData;
import com.drillslotinfo.client.DrillSlotInfoClient;
import com.drillslotinfo.config.DrillConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void afterRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!DrillConfig.enabled) return;
        if (!DrillConfig.showInInventory) return;

        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        InventoryScreen self = (InventoryScreen) (Object) this;
        HandledScreenAccessor acc = (HandledScreenAccessor) self;
        int sx = acc.getGuiLeft();
        int sy = acc.getGuiTop();

        for (Slot slot : self.getScreenHandler().slots) {
            if (slot.getStack().isEmpty()) continue;

            ParsedDrillData data = DrillItemParser.getData(slot.getStack());
            if (data == null) continue;

            DrillSlotInfoClient.renderOverlay(context, tr, data,
                    sx + slot.x, sy + slot.y);
        }
    }
}
