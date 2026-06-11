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
    public static KeyMapping toggleAllBackgroundsKey;
    public static KeyMapping toggleFractionModeKey;

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
        toggleAllBackgroundsKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.infoinslot.toggle_all_backgrounds",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_Y,
                cat
        ));
        toggleFractionModeKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.infoinslot.toggle_fraction_mode",
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
                DrillConfig.save();
            }
            while (cycleSizeKey.consumeClick()) {
                DrillConfig.TimerSize[] sizes = DrillConfig.TimerSize.values();
                DrillConfig.timerSize = sizes[(DrillConfig.timerSize.ordinal() + 1) % sizes.length];
                DrillConfig.save();
            }
            while (toggleAllBackgroundsKey.consumeClick()) {
                DrillConfig.setAllBackgrounds(!DrillConfig.allBackgroundsOn());
                DrillConfig.save();
            }
            while (toggleFractionModeKey.consumeClick()) {
                DrillConfig.chanceFractionMode = !DrillConfig.chanceFractionMode;
                DrillConfig.save();
            }
        });

        // ── HUD hotbar overlay ─────────────────────────────────────────────────
        HudElementRegistry.addLast(
            Identifier.fromNamespaceAndPath("infoinslot", "hotbar_overlay"),
            (graphics, delta) -> {
                if (!DrillConfig.enabled) return;
                if (!DrillConfig.showInHotbar) return;

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
            float maxPx; String scaleRef; float cap;
            switch (size) {
                case LARGE  -> { maxPx = 14f; scaleRef = "99h";     cap = 0.9f; }
                case MEDIUM -> { maxPx = 15f; scaleRef = "9h 9m";   cap = 0.75f; }
                case SMALL  -> { maxPx = 15f; scaleRef = "23h 9m";  cap = 0.6f; }
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
                    DrillConfig.timerHorizPosition,
                    sx, sy, DrillConfig.separateSlotColors, DrillConfig.timerBgColor);
        }

        // ── Auction Price (uses Order/Auction Price config) ────────────────────
        if (DrillConfig.showOrderPrice && data.auctionPrice != null) {
            String aLabel = DrillConfig.showCurrencySymbol
                    ? data.auctionPrice : stripLeadingSymbol(data.auctionPrice);
            renderTextLine(context, font, aLabel,
                    DrillConfig.orderPriceColor | 0xFF000000, DrillConfig.orderPriceBold,
                    priceScale(font, DrillConfig.orderPriceSize),
                    DrillConfig.showOrderPriceBackground, DrillConfig.orderPricePosition,
                    DrillConfig.orderPriceHorizPosition,
                    sx, sy, false, DrillConfig.orderPriceBgColor);
        }

        // ── Order Price ────────────────────────────────────────────────────────
        if (DrillConfig.showOrderPrice && data.orderPrice != null) {
            String oLabel = DrillConfig.showCurrencySymbol
                    ? data.orderPrice : stripLeadingSymbol(data.orderPrice);
            renderTextLine(context, font, oLabel,
                    DrillConfig.orderPriceColor | 0xFF000000, DrillConfig.orderPriceBold,
                    priceScale(font, DrillConfig.orderPriceSize),
                    DrillConfig.showOrderPriceBackground, DrillConfig.orderPricePosition,
                    DrillConfig.orderPriceHorizPosition,
                    sx, sy, false, DrillConfig.orderPriceBgColor);
        }

        // ── Order Count ────────────────────────────────────────────────────────
        if (DrillConfig.showDelivered) {
            String deliveredLabel = (data.orderFinished && data.deliveredDone != null)
                    ? data.deliveredDone : data.delivered;
            if (deliveredLabel != null) {
                renderTextLine(context, font, deliveredLabel,
                        DrillConfig.deliveredColor | 0xFF000000, DrillConfig.deliveredBold,
                        priceScale(font, DrillConfig.deliveredSize),
                        DrillConfig.showDeliveredBackground, DrillConfig.deliveredPosition,
                        DrillConfig.deliveredHorizPosition,
                        sx, sy, false, DrillConfig.deliveredBgColor);
            }
        }

        // ── Buy Price ──────────────────────────────────────────────────────────
        if (DrillConfig.showBuyPrice && data.buyPrice != null) {
            String bLabel = DrillConfig.showCurrencySymbol ? data.buyPrice : stripLeadingSymbol(data.buyPrice);
            renderTextLine(context, font, bLabel,
                    DrillConfig.buyColor | 0xFF000000, DrillConfig.buyBold,
                    priceScale(font, DrillConfig.buySize),
                    DrillConfig.showBuyBackground, DrillConfig.buyPosition,
                    DrillConfig.buyHorizPosition,
                    sx, sy, false, DrillConfig.buyBgColor);
        }

        // ── Sell Price ─────────────────────────────────────────────────────────
        if (DrillConfig.showSellPrice && data.sellPrice != null) {
            String sLabel = DrillConfig.showCurrencySymbol ? data.sellPrice : stripLeadingSymbol(data.sellPrice);
            renderTextLine(context, font, sLabel,
                    DrillConfig.sellColor | 0xFF000000, DrillConfig.sellBold,
                    priceScale(font, DrillConfig.sellSize),
                    DrillConfig.showSellBackground, DrillConfig.sellPosition,
                    DrillConfig.sellHorizPosition,
                    sx, sy, false, DrillConfig.sellBgColor);
        }

        // ── Key / Balance Counter ──────────────────────────────────────────────
        if (DrillConfig.showKeyCount && data.keyCount != null) {
            String kLabel = DrillConfig.showKeyLabel ? data.keyCount + " Keys" : data.keyCount;
            int kColor = DrillConfig.keyCountColor != 0
                    ? DrillConfig.keyCountColor | 0xFF000000
                    : data.keyColor | 0xFF000000;
            renderTextLine(context, font, kLabel,
                    kColor, DrillConfig.keyCountBold,
                    priceScale(font, DrillConfig.keySize),
                    DrillConfig.showKeyBackground, DrillConfig.keyPosition,
                    DrillConfig.keyHorizPosition,
                    sx, sy, false, DrillConfig.keyBgColor);
        }

        // ── Key Cost ──────────────────────────────────────────────────────────
        if (DrillConfig.showKeyCost && data.keyCost != null) {
            renderTextLine(context, font, data.keyCost,
                    DrillConfig.keyCostColor | 0xFF000000, DrillConfig.keyCostBold,
                    priceScale(font, DrillConfig.keyCostSize),
                    DrillConfig.showKeyCostBackground, DrillConfig.keyCostVertPosition,
                    DrillConfig.keyCostHorizPosition,
                    sx, sy, false, DrillConfig.keyCostBgColor);
        }

        // ── Shards ─────────────────────────────────────────────────────────────
        if (DrillConfig.showShards && data.shardsAmount != null) {
            int digitCol = DrillConfig.shardsDigitColor != 0
                    ? DrillConfig.shardsDigitColor | 0xFF000000 : data.shardsColor | 0xFF000000;
            renderCurrencyLine(context, font, data.shardsAmount, "S",
                    DrillConfig.shardsSymbolSide, digitCol,
                    DrillConfig.shardsSymbolColor | 0xFF000000,
                    DrillConfig.shardsBold,
                    priceScale(font, DrillConfig.shardsSize),
                    DrillConfig.showShardsBackground, DrillConfig.shardsPosition,
                    DrillConfig.shardsHorizPosition,
                    sx, sy, DrillConfig.shardsBgColor);
        }

        // ── Coins ──────────────────────────────────────────────────────────────
        boolean hasBuySell = data.buyPrice != null || data.sellPrice != null;
        if (DrillConfig.showCoins && data.coinsAmount != null && !hasBuySell) {
            int digitCol = DrillConfig.coinsDigitColor != 0
                    ? DrillConfig.coinsDigitColor | 0xFF000000 : data.coinsColor | 0xFF000000;
            renderCurrencyLine(context, font, data.coinsAmount, "C",
                    DrillConfig.coinsSymbolSide, digitCol,
                    DrillConfig.coinsSymbolColor | 0xFF000000,
                    DrillConfig.coinsBold,
                    priceScale(font, DrillConfig.coinsSize),
                    DrillConfig.showCoinsBackground, DrillConfig.coinsPosition,
                    DrillConfig.coinsHorizPosition,
                    sx, sy, DrillConfig.coinsBgColor);
        }

        // ── Rubies ─────────────────────────────────────────────────────────────
        if (DrillConfig.showRubies && data.rubiesAmount != null) {
            int digitCol = DrillConfig.rubiesDigitColor != 0
                    ? DrillConfig.rubiesDigitColor | 0xFF000000 : data.rubiesColor | 0xFF000000;
            renderCurrencyLine(context, font, data.rubiesAmount, "R",
                    DrillConfig.rubiesSymbolSide, digitCol,
                    DrillConfig.rubiesSymbolColor | 0xFF000000,
                    DrillConfig.rubiesBold,
                    priceScale(font, DrillConfig.rubiesSize),
                    DrillConfig.showRubiesBackground, DrillConfig.rubiesPosition,
                    DrillConfig.rubiesHorizPosition,
                    sx, sy, DrillConfig.rubiesBgColor);
        }

        // ── Gems ───────────────────────────────────────────────────────────────
        if (DrillConfig.showGems && data.gemsAmount != null) {
            int digitCol = DrillConfig.gemsDigitColor != 0
                    ? DrillConfig.gemsDigitColor | 0xFF000000 : data.gemsColor | 0xFF000000;
            renderCurrencyLine(context, font, data.gemsAmount, "G",
                    DrillConfig.gemsSymbolSide, digitCol,
                    DrillConfig.gemsSymbolColor | 0xFF000000,
                    DrillConfig.gemsBold,
                    priceScale(font, DrillConfig.gemsSize),
                    DrillConfig.showGemsBackground, DrillConfig.gemsPosition,
                    DrillConfig.gemsHorizPosition,
                    sx, sy, DrillConfig.gemsBgColor);
        }

        // ── Chance ────────────────────────────────────────────────────────────
        if (DrillConfig.showChance && data.chanceFormatted != null) {
            int digitCol = DrillConfig.chanceColor != 0
                    ? DrillConfig.chanceColor | 0xFF000000
                    : data.chanceRawColor | 0xFF000000;
            float scale = priceScale(font, DrillConfig.chanceSize);

            if (DrillConfig.chanceFractionMode) {
                String[] frac  = DrillItemParser.computeFraction(data.chanceFormatted);
                int numCol   = DrillConfig.chanceNumeratorColor   != 0 ? DrillConfig.chanceNumeratorColor   | 0xFF000000 : digitCol;
                int denCol   = DrillConfig.chanceDenominatorColor != 0 ? DrillConfig.chanceDenominatorColor | 0xFF000000 : digitCol;
                int slashCol = DrillConfig.chanceSlashColor       != 0 ? DrillConfig.chanceSlashColor       | 0xFF000000 : digitCol;
                renderFractionLine(context, font, frac[0], "/", frac[1],
                        numCol, slashCol, denCol, DrillConfig.chanceBold, scale,
                        DrillConfig.showChanceBackground, DrillConfig.chanceVertPosition,
                        DrillConfig.chanceHorizPosition, sx, sy, DrillConfig.chanceBgColor);
            } else if (DrillConfig.showChanceSymbol) {
                int symCol = DrillConfig.chanceSymbolColor != 0
                        ? DrillConfig.chanceSymbolColor | 0xFF000000
                        : digitCol;
                renderCurrencyLine(context, font, data.chanceFormatted, "%",
                        DrillConfig.SymbolSide.RIGHT, digitCol, symCol,
                        DrillConfig.chanceBold, scale,
                        DrillConfig.showChanceBackground, DrillConfig.chanceVertPosition,
                        DrillConfig.chanceHorizPosition, sx, sy, DrillConfig.chanceBgColor);
            } else {
                renderTextLine(context, font, data.chanceFormatted, digitCol, DrillConfig.chanceBold, scale,
                        DrillConfig.showChanceBackground, DrillConfig.chanceVertPosition,
                        DrillConfig.chanceHorizPosition, sx, sy, false, DrillConfig.chanceBgColor);
            }
        }
    }

    // ── Currency render helper ────────────────────────────────────────────────

    private static void renderCurrencyLine(GuiGraphicsExtractor context, Font font,
                                           String amount, String symbol,
                                           DrillConfig.SymbolSide side,
                                           int digitColor, int symbolColor,
                                           boolean bold,
                                           float scale, boolean showBg,
                                           DrillConfig.TimerPosition vertPos,
                                           DrillConfig.HorizPosition horizPos,
                                           int sx, int sy, int bgColor) {
        String full = switch (side) {
            case LEFT  -> symbol + amount;
            case RIGHT -> amount + symbol;
            case OFF   -> amount;
        };

        int bgH  = (int) Math.ceil(9 * scale) + 1;
        int rawW = (int) Math.ceil(font.width(full) * scale) + 2 + (bold ? 2 : 0);

        int yOff = switch (vertPos) {
            case MIDDLE -> (16 - bgH) / 2;
            case BOTTOM -> Math.max(0, 16 - bgH);
            default     -> 0;
        };
        int xOff = switch (horizPos) {
            case RIGHT  -> 16 - rawW;
            case MIDDLE -> (16 - rawW) / 2;
            default     -> 0;
        };

        if (showBg)
            context.fill(sx + xOff, sy + yOff, sx + xOff + rawW, sy + yOff + bgH,
                    (0xBB << 24) | (bgColor & 0xFFFFFF));

        var m = context.pose();
        m.pushMatrix();
        m.translate(sx + xOff + 1f, sy + yOff + 1f);
        m.scale(scale, scale);

        switch (side) {
            case OFF -> drawPart(context, font, amount, 0, 0, digitColor, bold);
            case LEFT -> {
                int symW = font.width(symbol);
                drawPart(context, font, symbol, 0, 0, symbolColor, bold);
                drawPart(context, font, amount, symW, 0, digitColor, bold);
            }
            case RIGHT -> {
                int amtW = font.width(amount);
                drawPart(context, font, amount, 0, 0, digitColor, bold);
                drawPart(context, font, symbol, amtW, 0, symbolColor, bold);
            }
        }
        m.popMatrix();
    }

    // ── Fraction render (num / den with separate per-segment colors) ──────────

    private static void renderFractionLine(GuiGraphicsExtractor context, Font font,
                                           String num, String slash, String den,
                                           int numColor, int slashColor, int denColor,
                                           boolean bold,
                                           float scale, boolean showBg,
                                           DrillConfig.TimerPosition vertPos,
                                           DrillConfig.HorizPosition horizPos,
                                           int sx, int sy, int bgColor) {
        int numW   = font.width(num);
        int slashW = font.width(slash);
        int denW   = font.width(den);
        int totalW = numW + slashW + denW;

        int bgH  = (int) Math.ceil(9 * scale) + 1;
        int rawW = (int) Math.ceil(totalW * scale) + 2 + (bold ? 2 : 0);

        int yOff = switch (vertPos) {
            case MIDDLE -> (16 - bgH) / 2;
            case BOTTOM -> Math.max(0, 16 - bgH);
            default     -> 0;
        };
        int xOff = switch (horizPos) {
            case RIGHT  -> 16 - rawW;
            case MIDDLE -> (16 - rawW) / 2;
            default     -> 0;
        };

        if (showBg)
            context.fill(sx + xOff, sy + yOff, sx + xOff + rawW, sy + yOff + bgH,
                    (0xBB << 24) | (bgColor & 0xFFFFFF));

        var m = context.pose();
        m.pushMatrix();
        m.translate(sx + xOff + 1f, sy + yOff + 1f);
        m.scale(scale, scale);
        drawPart(context, font, num,   0,              0, numColor,   bold);
        drawPart(context, font, slash, numW,            0, slashColor, bold);
        drawPart(context, font, den,   numW + slashW,   0, denColor,   bold);
        m.popMatrix();
    }

    // ── Core text render (V-position + H-position) ────────────────────────────

    private static void renderTextLine(GuiGraphicsExtractor context, Font font,
                                       String label, int color, boolean bold, float scale,
                                       boolean showBg, DrillConfig.TimerPosition vertPos,
                                       DrillConfig.HorizPosition horizPos,
                                       int sx, int sy, boolean useSeparateColors, int bgColor) {
        int bgH  = (int) Math.ceil(9 * scale) + 1;
        int rawW = (int) Math.ceil(font.width(label) * scale) + 2 + (bold ? 2 : 0);

        int yOff = switch (vertPos) {
            case MIDDLE -> (16 - bgH) / 2;
            case BOTTOM -> Math.max(0, 16 - bgH);
            default     -> 0;
        };
        int xOff = switch (horizPos) {
            case RIGHT  -> 16 - rawW;
            case MIDDLE -> (16 - rawW) / 2;
            default     -> 0;
        };

        if (showBg)
            context.fill(sx + xOff, sy + yOff, sx + xOff + rawW, sy + yOff + bgH,
                    (0xBB << 24) | (bgColor & 0xFFFFFF));

        var m = context.pose();
        m.pushMatrix();
        m.translate(sx + xOff + 1f, sy + yOff + 1f);
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
        if (bold)
            context.text(font, Component.literal(text).withStyle(s -> s.withBold(true)), x, y, color, false);
        else
            context.text(font, text, x, y, color, false);
    }

    // ── Shared helpers ────────────────────────────────────────────────────────

    private static String stripLeadingSymbol(String s) {
        int i = 0;
        while (i < s.length() && !Character.isDigit(s.charAt(i))) i++;
        return i < s.length() ? s.substring(i) : s;
    }

    static float priceScale(Font font, DrillConfig.PriceSize size) {
        return switch (size) {
            case LARGE  -> Math.min(0.80f, 16f / Math.max(1f, font.width("1234")));
            case MEDIUM -> Math.min(0.70f, 15f / Math.max(1f, font.width("12345")));
            case SMALL  -> Math.min(0.60f, 15f / Math.max(1f, font.width("$99.99M")));
            case XSMALL -> Math.min(0.50f, 14f / Math.max(1f, font.width("$99.99M")));
        };
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
