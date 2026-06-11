package com.drillslotinfo.client;

import com.drillslotinfo.DrillItemParser;
import com.drillslotinfo.DrillSlotInfo;
import com.drillslotinfo.ParsedDrillData;
import com.drillslotinfo.config.DrillConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class DrillSlotInfoClient implements ClientModInitializer {

    public static KeyBinding toggleModKey;
    public static KeyBinding toggleHoursOnlyKey;
    public static KeyBinding cycleSizeKey;
    public static KeyBinding toggleAllBackgroundsKey;
    public static KeyBinding toggleFractionModeKey;

    @Override
    public void onInitializeClient() {
        DrillSlotInfo.LOGGER.info("Info in Slot client initialized");

        ClientCommandRegistrationCallback.EVENT.register(SlotInfoCommand::register);

        KeyBinding.Category cat = KeyBinding.Category.create(Identifier.of("infoinslot", "keys"));
        toggleModKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.infoinslot.toggle_mod", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, cat));
        toggleHoursOnlyKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.infoinslot.toggle_hours_only", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, cat));
        cycleSizeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.infoinslot.cycle_size", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, cat));
        toggleAllBackgroundsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.infoinslot.toggle_all_backgrounds", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Y, cat));
        toggleFractionModeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.infoinslot.toggle_fraction_mode", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, cat));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleModKey.wasPressed()) {
                DrillConfig.enabled = !DrillConfig.enabled;
                DrillConfig.save();
            }
            while (toggleHoursOnlyKey.wasPressed()) {
                DrillConfig.hoursOnly = !DrillConfig.hoursOnly;
                DrillConfig.save();
            }
            while (cycleSizeKey.wasPressed()) {
                DrillConfig.TimerSize[] sizes = DrillConfig.TimerSize.values();
                DrillConfig.timerSize = sizes[(DrillConfig.timerSize.ordinal() + 1) % sizes.length];
                DrillConfig.save();
            }
            while (toggleAllBackgroundsKey.wasPressed()) {
                DrillConfig.setAllBackgrounds(!DrillConfig.allBackgroundsOn());
                DrillConfig.save();
            }
            while (toggleFractionModeKey.wasPressed()) {
                DrillConfig.chanceFractionMode = !DrillConfig.chanceFractionMode;
                DrillConfig.save();
            }
        });

        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            if (!DrillConfig.enabled) return;
            if (!DrillConfig.showInHotbar) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.currentScreen != null) return;

            TextRenderer tr = client.textRenderer;
            int sw = client.getWindow().getScaledWidth();
            int sh = client.getWindow().getScaledHeight();

            int hotbarLeft = sw / 2 - 91;
            int iconY      = sh - 22 + 3;

            for (int i = 0; i < 9; i++) {
                ItemStack stack = client.player.getInventory().getStack(i);
                if (stack.isEmpty()) continue;
                ParsedDrillData data = DrillItemParser.getData(stack);
                if (data == null) continue;
                renderOverlay(context, tr, data, hotbarLeft + i * 20 + 2, iconY);
            }
        });
    }

    public static void renderOverlay(DrawContext context, TextRenderer tr,
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
            float scale = Math.min(cap, maxPx / Math.max(1f, tr.getWidth(scaleRef)));

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
            renderTextLine(context, tr, label, color, bold, scale,
                    DrillConfig.showBackground, DrillConfig.timerPosition,
                    DrillConfig.timerHorizPosition,
                    sx, sy, DrillConfig.separateSlotColors, DrillConfig.timerBgColor);
        }

        // ── Auction Price (uses Order/Auction Price config) ────────────────────
        if (DrillConfig.showOrderPrice && data.auctionPrice != null) {
            String aLabel = DrillConfig.showCurrencySymbol
                    ? data.auctionPrice : stripLeadingSymbol(data.auctionPrice);
            renderTextLine(context, tr, aLabel,
                    DrillConfig.orderPriceColor | 0xFF000000, DrillConfig.orderPriceBold,
                    priceScale(tr, DrillConfig.orderPriceSize),
                    DrillConfig.showOrderPriceBackground, DrillConfig.orderPricePosition,
                    DrillConfig.orderPriceHorizPosition,
                    sx, sy, false, DrillConfig.orderPriceBgColor);
        }

        // ── Order Price ────────────────────────────────────────────────────────
        if (DrillConfig.showOrderPrice && data.orderPrice != null) {
            String oLabel = DrillConfig.showCurrencySymbol
                    ? data.orderPrice : stripLeadingSymbol(data.orderPrice);
            renderTextLine(context, tr, oLabel,
                    DrillConfig.orderPriceColor | 0xFF000000, DrillConfig.orderPriceBold,
                    priceScale(tr, DrillConfig.orderPriceSize),
                    DrillConfig.showOrderPriceBackground, DrillConfig.orderPricePosition,
                    DrillConfig.orderPriceHorizPosition,
                    sx, sy, false, DrillConfig.orderPriceBgColor);
        }

        // ── Order Count ────────────────────────────────────────────────────────
        if (DrillConfig.showDelivered) {
            String deliveredLabel = (data.orderFinished && data.deliveredDone != null)
                    ? data.deliveredDone : data.delivered;
            if (deliveredLabel != null) {
                renderTextLine(context, tr, deliveredLabel,
                        DrillConfig.deliveredColor | 0xFF000000, DrillConfig.deliveredBold,
                        priceScale(tr, DrillConfig.deliveredSize),
                        DrillConfig.showDeliveredBackground, DrillConfig.deliveredPosition,
                        DrillConfig.deliveredHorizPosition,
                        sx, sy, false, DrillConfig.deliveredBgColor);
            }
        }

        // ── Buy Price ──────────────────────────────────────────────────────────
        if (DrillConfig.showBuyPrice && data.buyPrice != null) {
            String bLabel = DrillConfig.showCurrencySymbol ? data.buyPrice : stripLeadingSymbol(data.buyPrice);
            renderTextLine(context, tr, bLabel,
                    DrillConfig.buyColor | 0xFF000000, DrillConfig.buyBold,
                    priceScale(tr, DrillConfig.buySize),
                    DrillConfig.showBuyBackground, DrillConfig.buyPosition,
                    DrillConfig.buyHorizPosition,
                    sx, sy, false, DrillConfig.buyBgColor);
        }

        // ── Sell Price ─────────────────────────────────────────────────────────
        if (DrillConfig.showSellPrice && data.sellPrice != null) {
            String sLabel = DrillConfig.showCurrencySymbol ? data.sellPrice : stripLeadingSymbol(data.sellPrice);
            renderTextLine(context, tr, sLabel,
                    DrillConfig.sellColor | 0xFF000000, DrillConfig.sellBold,
                    priceScale(tr, DrillConfig.sellSize),
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
            renderTextLine(context, tr, kLabel,
                    kColor, DrillConfig.keyCountBold,
                    priceScale(tr, DrillConfig.keySize),
                    DrillConfig.showKeyBackground, DrillConfig.keyPosition,
                    DrillConfig.keyHorizPosition,
                    sx, sy, false, DrillConfig.keyBgColor);
        }

        // ── Key Cost ──────────────────────────────────────────────────────────
        if (DrillConfig.showKeyCost && data.keyCost != null) {
            renderTextLine(context, tr, data.keyCost,
                    DrillConfig.keyCostColor | 0xFF000000, DrillConfig.keyCostBold,
                    priceScale(tr, DrillConfig.keyCostSize),
                    DrillConfig.showKeyCostBackground, DrillConfig.keyCostVertPosition,
                    DrillConfig.keyCostHorizPosition,
                    sx, sy, false, DrillConfig.keyCostBgColor);
        }

        // ── Shards ─────────────────────────────────────────────────────────────
        if (DrillConfig.showShards && data.shardsAmount != null) {
            int digitCol = DrillConfig.shardsDigitColor != 0
                    ? DrillConfig.shardsDigitColor | 0xFF000000 : data.shardsColor | 0xFF000000;
            renderCurrencyLine(context, tr, data.shardsAmount, "S",
                    DrillConfig.shardsSymbolSide, digitCol,
                    DrillConfig.shardsSymbolColor | 0xFF000000,
                    DrillConfig.shardsBold,
                    priceScale(tr, DrillConfig.shardsSize),
                    DrillConfig.showShardsBackground, DrillConfig.shardsPosition,
                    DrillConfig.shardsHorizPosition,
                    sx, sy, DrillConfig.shardsBgColor);
        }

        // ── Coins ──────────────────────────────────────────────────────────────
        boolean hasBuySell = data.buyPrice != null || data.sellPrice != null;
        if (DrillConfig.showCoins && data.coinsAmount != null && !hasBuySell) {
            int digitCol = DrillConfig.coinsDigitColor != 0
                    ? DrillConfig.coinsDigitColor | 0xFF000000 : data.coinsColor | 0xFF000000;
            renderCurrencyLine(context, tr, data.coinsAmount, "C",
                    DrillConfig.coinsSymbolSide, digitCol,
                    DrillConfig.coinsSymbolColor | 0xFF000000,
                    DrillConfig.coinsBold,
                    priceScale(tr, DrillConfig.coinsSize),
                    DrillConfig.showCoinsBackground, DrillConfig.coinsPosition,
                    DrillConfig.coinsHorizPosition,
                    sx, sy, DrillConfig.coinsBgColor);
        }

        // ── Rubies ─────────────────────────────────────────────────────────────
        if (DrillConfig.showRubies && data.rubiesAmount != null) {
            int digitCol = DrillConfig.rubiesDigitColor != 0
                    ? DrillConfig.rubiesDigitColor | 0xFF000000 : data.rubiesColor | 0xFF000000;
            renderCurrencyLine(context, tr, data.rubiesAmount, "R",
                    DrillConfig.rubiesSymbolSide, digitCol,
                    DrillConfig.rubiesSymbolColor | 0xFF000000,
                    DrillConfig.rubiesBold,
                    priceScale(tr, DrillConfig.rubiesSize),
                    DrillConfig.showRubiesBackground, DrillConfig.rubiesPosition,
                    DrillConfig.rubiesHorizPosition,
                    sx, sy, DrillConfig.rubiesBgColor);
        }

        // ── Gems ───────────────────────────────────────────────────────────────
        if (DrillConfig.showGems && data.gemsAmount != null) {
            int digitCol = DrillConfig.gemsDigitColor != 0
                    ? DrillConfig.gemsDigitColor | 0xFF000000 : data.gemsColor | 0xFF000000;
            renderCurrencyLine(context, tr, data.gemsAmount, "G",
                    DrillConfig.gemsSymbolSide, digitCol,
                    DrillConfig.gemsSymbolColor | 0xFF000000,
                    DrillConfig.gemsBold,
                    priceScale(tr, DrillConfig.gemsSize),
                    DrillConfig.showGemsBackground, DrillConfig.gemsPosition,
                    DrillConfig.gemsHorizPosition,
                    sx, sy, DrillConfig.gemsBgColor);
        }

        // ── Chance ────────────────────────────────────────────────────────────
        if (DrillConfig.showChance && data.chanceFormatted != null) {
            int digitCol = DrillConfig.chanceColor != 0
                    ? DrillConfig.chanceColor | 0xFF000000
                    : data.chanceRawColor | 0xFF000000;
            float scale = priceScale(tr, DrillConfig.chanceSize);

            if (DrillConfig.chanceFractionMode) {
                String[] frac  = DrillItemParser.computeFraction(data.chanceFormatted);
                int numCol   = DrillConfig.chanceNumeratorColor   != 0 ? DrillConfig.chanceNumeratorColor   | 0xFF000000 : digitCol;
                int denCol   = DrillConfig.chanceDenominatorColor != 0 ? DrillConfig.chanceDenominatorColor | 0xFF000000 : digitCol;
                int slashCol = DrillConfig.chanceSlashColor       != 0 ? DrillConfig.chanceSlashColor       | 0xFF000000 : digitCol;
                renderFractionLine(context, tr, frac[0], "/", frac[1],
                        numCol, slashCol, denCol, DrillConfig.chanceBold, scale,
                        DrillConfig.showChanceBackground, DrillConfig.chanceVertPosition,
                        DrillConfig.chanceHorizPosition, sx, sy, DrillConfig.chanceBgColor);
            } else if (DrillConfig.showChanceSymbol) {
                int symCol = DrillConfig.chanceSymbolColor != 0
                        ? DrillConfig.chanceSymbolColor | 0xFF000000
                        : digitCol;
                renderCurrencyLine(context, tr, data.chanceFormatted, "%",
                        DrillConfig.SymbolSide.RIGHT, digitCol, symCol,
                        DrillConfig.chanceBold, scale,
                        DrillConfig.showChanceBackground, DrillConfig.chanceVertPosition,
                        DrillConfig.chanceHorizPosition, sx, sy, DrillConfig.chanceBgColor);
            } else {
                renderTextLine(context, tr, data.chanceFormatted, digitCol, DrillConfig.chanceBold, scale,
                        DrillConfig.showChanceBackground, DrillConfig.chanceVertPosition,
                        DrillConfig.chanceHorizPosition, sx, sy, false, DrillConfig.chanceBgColor);
            }
        }
    }

    // ── Currency render helper ────────────────────────────────────────────────

    private static void renderCurrencyLine(DrawContext context, TextRenderer tr,
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
        int rawW = (int) Math.ceil(tr.getWidth(full) * scale) + 2 + (bold ? 2 : 0);

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

        var m = context.getMatrices();
        m.pushMatrix();
        m.translate(sx + xOff + 1f, sy + yOff + 1f);
        m.scale(scale, scale);

        switch (side) {
            case OFF -> drawPart(context, tr, amount, 0, 0, digitColor, bold);
            case LEFT -> {
                int symW = tr.getWidth(symbol);
                drawPart(context, tr, symbol, 0, 0, symbolColor, bold);
                drawPart(context, tr, amount, symW, 0, digitColor, bold);
            }
            case RIGHT -> {
                int amtW = tr.getWidth(amount);
                drawPart(context, tr, amount, 0, 0, digitColor, bold);
                drawPart(context, tr, symbol, amtW, 0, symbolColor, bold);
            }
        }
        m.popMatrix();
    }

    // ── Fraction render (num / den with separate per-segment colors) ──────────

    private static void renderFractionLine(DrawContext context, TextRenderer tr,
                                           String num, String slash, String den,
                                           int numColor, int slashColor, int denColor,
                                           boolean bold,
                                           float scale, boolean showBg,
                                           DrillConfig.TimerPosition vertPos,
                                           DrillConfig.HorizPosition horizPos,
                                           int sx, int sy, int bgColor) {
        int numW   = tr.getWidth(num);
        int slashW = tr.getWidth(slash);
        int denW   = tr.getWidth(den);
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

        var m = context.getMatrices();
        m.pushMatrix();
        m.translate(sx + xOff + 1f, sy + yOff + 1f);
        m.scale(scale, scale);
        drawPart(context, tr, num,   0,              0, numColor,   bold);
        drawPart(context, tr, slash, numW,            0, slashColor, bold);
        drawPart(context, tr, den,   numW + slashW,   0, denColor,   bold);
        m.popMatrix();
    }

    // ── Core text render (V-position + H-position) ────────────────────────────

    /**
     * Renders a text label with independent vertical and horizontal positioning.
     * For RIGHT/MIDDLE horizPos: anchors to right/center — text can extend past the
     * slot's left boundary if wider than 16 scaled pixels.
     * For LEFT: starts at left edge, may overflow right for very long text.
     */
    private static void renderTextLine(DrawContext context, TextRenderer tr,
                                       String label, int color, boolean bold, float scale,
                                       boolean showBg, DrillConfig.TimerPosition vertPos,
                                       DrillConfig.HorizPosition horizPos,
                                       int sx, int sy, boolean useSeparateColors, int bgColor) {
        int bgH  = (int) Math.ceil(9 * scale) + 1;
        int rawW = (int) Math.ceil(tr.getWidth(label) * scale) + 2 + (bold ? 2 : 0);

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

        var m = context.getMatrices();
        m.pushMatrix();
        m.translate(sx + xOff + 1f, sy + yOff + 1f);
        m.scale(scale, scale);

        if (useSeparateColors && label.contains(" ")) {
            int sp = label.indexOf(' ');
            String p1 = label.substring(0, sp);
            String p2 = label.substring(sp + 1);
            drawPart(context, tr, p1, 0, 0, colorForPart(p1), boldForPart(p1));
            drawPart(context, tr, p2, tr.getWidth(p1) + tr.getWidth(" "), 0, colorForPart(p2), boldForPart(p2));
        } else if (useSeparateColors) {
            int split = -1;
            for (int k = 0; k < label.length() - 1; k++) {
                char c = label.charAt(k);
                if (c == 'd' || c == 'h' || c == 'm' || c == 's') { split = k + 1; break; }
            }
            if (split > 0) {
                String p1 = label.substring(0, split);
                String p2 = label.substring(split);
                drawPart(context, tr, p1, 0, 0, colorForPart(p1), boldForPart(p1));
                drawPart(context, tr, p2, tr.getWidth(p1), 0, colorForPart(p2), boldForPart(p2));
            } else {
                drawPart(context, tr, label, 0, 0, color, bold);
            }
        } else {
            drawPart(context, tr, label, 0, 0, color, bold);
        }
        m.popMatrix();
    }

    private static void drawPart(DrawContext context, TextRenderer tr,
                                 String text, int x, int y, int color, boolean bold) {
        if (bold)
            context.drawText(tr, Text.literal(text).styled(s -> s.withBold(true)), x, y, color, false);
        else
            context.drawText(tr, text, x, y, color, false);
    }

    // ── Shared helpers ────────────────────────────────────────────────────────

    private static String stripLeadingSymbol(String s) {
        int i = 0;
        while (i < s.length() && !Character.isDigit(s.charAt(i))) i++;
        return i < s.length() ? s.substring(i) : s;
    }

    static float priceScale(TextRenderer tr, DrillConfig.PriceSize size) {
        return switch (size) {
            case LARGE  -> Math.min(0.80f, 16f / Math.max(1f, tr.getWidth("1234")));
            case MEDIUM -> Math.min(0.70f, 15f / Math.max(1f, tr.getWidth("12345")));
            case SMALL  -> Math.min(0.60f, 15f / Math.max(1f, tr.getWidth("$99.99M")));
            case XSMALL -> Math.min(0.50f, 14f / Math.max(1f, tr.getWidth("$99.99M")));
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
