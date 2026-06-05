package com.drillslotinfo.client;

import com.drillslotinfo.DrillItemParser;
import com.drillslotinfo.DrillSlotInfo;
import com.drillslotinfo.ParsedDrillData;
import com.drillslotinfo.config.DrillConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class DrillSlotInfoClient implements ClientModInitializer {

    public static KeyMapping toggleModKey;
    public static KeyMapping toggleHoursOnlyKey;
    public static KeyMapping cycleSizeKey;

    @Override
    public void onInitializeClient() {
        DrillSlotInfo.LOGGER.info("Info in Slot client initialized");

        ClientCommandRegistrationCallback.EVENT.register(SlotInfoCommand::register);

        // ── Keybindings — "Info in Slot" category in Controls ─────────────────
        KeyMapping.Category cat = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("infoinslot", "keys"));
        toggleModKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.infoinslot.toggle_mod",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                cat
        ));
        toggleHoursOnlyKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.infoinslot.toggle_hours_only",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                cat
        ));
        cycleSizeKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.infoinslot.cycle_size",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                cat
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleModKey.consumeClick()) {
                DrillConfig.enabled = !DrillConfig.enabled;
                DrillConfig.save();
            }
            while (toggleHoursOnlyKey.consumeClick()) {
                DrillConfig.hoursOnly = !DrillConfig.hoursOnly;
                if (!DrillConfig.hoursOnly && DrillConfig.timerSize == DrillConfig.TimerSize.LARGE)
                    DrillConfig.timerSize = DrillConfig.TimerSize.MEDIUM;
                DrillConfig.save();
            }
            while (cycleSizeKey.consumeClick()) {
                DrillConfig.TimerSize[] sizes = DrillConfig.TimerSize.values();
                int next = (DrillConfig.timerSize.ordinal() + 1) % sizes.length;
                DrillConfig.TimerSize nextSize = sizes[next];
                if (nextSize == DrillConfig.TimerSize.LARGE && !DrillConfig.hoursOnly)
                    nextSize = DrillConfig.TimerSize.SMALL;
                DrillConfig.timerSize = nextSize;
                DrillConfig.save();
            }
        });

        // ── HUD hotbar overlay ─────────────────────────────────────────────────
        HudElementRegistry.addLast(
            Identifier.fromNamespaceAndPath("infoinslot", "hotbar_overlay"),
            (graphics, delta) -> {
                if (!DrillConfig.enabled) return;

                Minecraft client = Minecraft.getInstance();
                if (client.player == null || client.screen != null) return;

                Font font = client.font;
                int sw = client.getWindow().getGuiScaledWidth();
                int sh = client.getWindow().getGuiScaledHeight();

                int hotbarLeft = sw / 2 - 91;
                int iconY      = sh - 22 + 3;

                for (int i = 0; i < 9; i++) {
                    ItemStack stack = client.player.getInventory().getItem(i);
                    if (stack.isEmpty()) continue;

                    ParsedDrillData data = DrillItemParser.getData(stack);
                    if (data == null) continue;

                    renderOverlay(graphics, font, data, hotbarLeft + i * 20 + 2, iconY);
                }
            }
        );
    }

    public static void renderOverlay(GuiGraphicsExtractor context, Font font,
                                     ParsedDrillData data, int sx, int sy) {
        // ── Timer ──────────────────────────────────────────────────────────────
        if (DrillConfig.showTimer && !(data.days == -1 && data.hours == -1 && data.minutes == -1 && data.seconds == -1)) {
            DrillConfig.TimerSize size = DrillConfig.timerSize;
            if (size == DrillConfig.TimerSize.LARGE && !DrillConfig.hoursOnly)
                size = DrillConfig.TimerSize.MEDIUM;

            float maxPx; String scaleRef; float cap;
            switch (size) {
                case LARGE  -> { maxPx = 14f; scaleRef = "99h";     cap = 0.9f; }
                case MEDIUM -> { maxPx = 15f; scaleRef = "23h 9m";  cap = 0.6f; }
                default     -> { maxPx = 14f; scaleRef = "23h 59m"; cap = 0.5f; }
            }
            float scale = Math.min(cap, maxPx / Math.max(1f, font.width(scaleRef)));

            String label; int color; boolean bold;
            if (DrillConfig.hoursOnly) {
                int totalHours = Math.max(0, data.days) * 24 + Math.max(0, data.hours);
                if (totalHours > 0) {
                    label = totalHours + "h"; color = colorForData(data); bold = boldForData(data);
                } else {
                    if (data.minutes > 0)       label = data.minutes + "m";
                    else if (data.seconds >= 0) label = data.seconds + "s";
                    else                        label = data.timer;
                    color = DrillConfig.minuteColor | 0xFF000000;
                    bold  = DrillConfig.minuteBold;
                }
            } else {
                label = (data.days < 0 && data.hours >= 10) ? (data.hours + "h") : data.timer;
                color = colorForData(data); bold = boldForData(data);
            }

            renderTextLine(context, font, label, color, bold, scale,
                    DrillConfig.showBackground, DrillConfig.timerPosition,
                    sx, sy, DrillConfig.separateSlotColors);
        }

        // ── Auction Price ("/ah" — lime green, no space) ───────────────────────
        if (DrillConfig.showPrice && data.auctionPrice != null) {
            float scale = priceScale(font, DrillConfig.priceSize);
            renderTextLine(context, font, data.auctionPrice,
                    DrillConfig.priceColor | 0xFF000000, DrillConfig.priceBold, scale,
                    DrillConfig.showPriceBackground, DrillConfig.pricePosition,
                    sx, sy, false);
        }

        // ── Order Price ("/order" — white digits, space removed) ──────────────
        if (DrillConfig.showOrderPrice && data.orderPrice != null) {
            float scale = priceScale(font, DrillConfig.orderPriceSize);
            renderTextLine(context, font, data.orderPrice,
                    DrillConfig.orderPriceColor | 0xFF000000, DrillConfig.orderPriceBold, scale,
                    DrillConfig.showOrderPriceBackground, DrillConfig.orderPricePosition,
                    sx, sy, false);
        }

        // ── Delivered count ────────────────────────────────────────────────────
        if (DrillConfig.showDelivered) {
            String deliveredLabel = (data.orderFinished && data.deliveredDone != null)
                    ? data.deliveredDone : data.delivered;
            if (deliveredLabel != null) {
                float scale = priceScale(font, DrillConfig.deliveredSize);
                renderTextLine(context, font, deliveredLabel,
                        DrillConfig.deliveredColor | 0xFF000000, DrillConfig.deliveredBold, scale,
                        DrillConfig.showDeliveredBackground, DrillConfig.deliveredPosition,
                        sx, sy, false);
            }
        }
    }

    // ── Shared render helpers ─────────────────────────────────────────────────

    private static float priceScale(Font font, DrillConfig.PriceSize size) {
        return size == DrillConfig.PriceSize.MEDIUM
                ? Math.min(0.6f, 15f / Math.max(1f, font.width("$99.99M")))
                : Math.min(0.5f, 14f / Math.max(1f, font.width("$99.99M")));
    }

    private static void renderTextLine(GuiGraphicsExtractor context, Font font,
                                       String label, int color, boolean bold, float scale,
                                       boolean showBg, DrillConfig.TimerPosition pos,
                                       int sx, int sy, boolean useSeparateColors) {
        int bgH = (int) Math.ceil(9 * scale) + 1;
        int bgW = Math.min((int) Math.ceil(font.width(label) * scale) + 2 + (bold ? 2 : 0), 16);

        int yOff = switch (pos) {
            case MIDDLE -> (16 - bgH) / 2;
            case BOTTOM -> Math.max(0, 16 - bgH);
            default     -> 0;
        };

        if (showBg) {
            context.fill(sx, sy + yOff, sx + bgW, sy + yOff + bgH, 0xBB000000);
        }

        var m = context.pose();
        m.pushMatrix();
        m.translate(sx + 1f, sy + yOff + 1f);
        m.scale(scale, scale);

        if (useSeparateColors && label.contains(" ")) {
            int sp = label.indexOf(' ');
            String p1 = label.substring(0, sp);
            String p2 = label.substring(sp + 1);
            drawPart(context, font, p1, 0, 0, colorForPart(p1), boldForPart(p1));
            drawPart(context, font, p2, font.width(p1) + font.width(" "), 0, colorForPart(p2), boldForPart(p2));
        } else if (useSeparateColors) {
            int split = -1;
            for (int k = 0; k < label.length() - 1; k++) {
                char c = label.charAt(k);
                if (c == 'd' || c == 'h' || c == 'm' || c == 's') { split = k + 1; break; }
            }
            if (split > 0) {
                String p1 = label.substring(0, split);
                String p2 = label.substring(split);
                drawPart(context, font, p1, 0, 0, colorForPart(p1), boldForPart(p1));
                drawPart(context, font, p2, font.width(p1), 0, colorForPart(p2), boldForPart(p2));
            } else {
                drawPart(context, font, label, 0, 0, color, bold);
            }
        } else {
            drawPart(context, font, label, 0, 0, color, bold);
        }

        m.popMatrix();
    }

    private static void drawPart(GuiGraphicsExtractor context, Font font,
                                 String text, int x, int y, int color, boolean bold) {
        if (bold) {
            context.text(font, Component.literal(text).withStyle(s -> s.withBold(true)), x, y, color, false);
        } else {
            context.text(font, text, x, y, color, false);
        }
    }

    // ── Timer color / bold helpers ────────────────────────────────────────────

    static int colorForData(ParsedDrillData data) {
        if (data.days >= 2) return DrillConfig.twoDayColor | 0xFF000000;
        if (data.days == 1) return DrillConfig.dayColor    | 0xFF000000;
        if (data.hours > 0) return DrillConfig.hourColor   | 0xFF000000;
        return                     DrillConfig.minuteColor | 0xFF000000;
    }

    static boolean boldForData(ParsedDrillData data) {
        if (data.days >= 2) return DrillConfig.twoDayBold;
        if (data.days == 1) return DrillConfig.dayBold;
        if (data.hours > 0) return DrillConfig.hourBold;
        return                     DrillConfig.minuteBold;
    }

    private static int colorForPart(String part) {
        char unit = part.charAt(part.length() - 1);
        if (unit == 'd') {
            int days = Integer.parseInt(part.substring(0, part.length() - 1));
            return (days >= 2 ? DrillConfig.twoDayColor : DrillConfig.dayColor) | 0xFF000000;
        }
        if (unit == 'h') return DrillConfig.hourColor   | 0xFF000000;
        return                   DrillConfig.minuteColor | 0xFF000000;
    }

    private static boolean boldForPart(String part) {
        char unit = part.charAt(part.length() - 1);
        if (unit == 'd') {
            int days = Integer.parseInt(part.substring(0, part.length() - 1));
            return days >= 2 ? DrillConfig.twoDayBold : DrillConfig.dayBold;
        }
        if (unit == 'h') return DrillConfig.hourBold;
        return                   DrillConfig.minuteBold;
    }
}
