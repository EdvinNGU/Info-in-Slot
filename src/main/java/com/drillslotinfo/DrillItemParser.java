package com.drillslotinfo;

import com.drillslotinfo.config.DrillConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DrillItemParser {

    private static final Pattern TIME_UNIT  = Pattern.compile("(\\d+)\\s*([dDhHmMsS])(?![a-zA-Z])");
    private static final Pattern COLOR_CODE = Pattern.compile("§[0-9a-fk-orA-FK-OR]");

    // ── Auction price: "$X" no space ──────────────────────────────────────────
    private static final Pattern AUCTION_PRICE_PAT = Pattern.compile("\\$(?!\\s)([0-9,.]+[BMKbmkTtQq]?)");
    // ── Order price: "$ X" with space ─────────────────────────────────────────
    private static final Pattern ORDER_PRICE_PAT   = Pattern.compile("\\$\\s+([0-9,.]+[kKmMbBtTqQ]?)");
    // ── Delivered: "X/Y Delivered" ────────────────────────────────────────────
    private static final Pattern DELIVERED_PAT     = Pattern.compile(
            "([0-9][0-9,.]*[kKmMbBtTqQ]?)/([0-9][0-9,.]*[kKmMbBtTqQ]?)\\s+delivered",
            Pattern.CASE_INSENSITIVE);

    // ── Alternative currency: R-prefix ────────────────────────────────────────
    private static final Pattern R_PREFIX_PAT = Pattern.compile("(?<!\\w)R\\s*(\\d[0-9,.]*[kKmMbBtTqQ]?)");

    // ── Shop Buy / Sell ───────────────────────────────────────────────────────
    private static final Pattern BUY_PRICE_PAT   = Pattern.compile(
            "(?i)\\bBuy(?:\\s+Price)?\\s*:?\\s*([$]?\\d[0-9,.]*[kKmMbBtTqQ]?)");
    private static final Pattern SELL_PRICE_PAT  = Pattern.compile(
            "(?i)\\bSell(?:\\s+Price)?\\s*:?\\s*([$]?\\d[0-9,.]*[kKmMbBtTqQ]?)");
    private static final Pattern BUY_LABEL_CHECK  = Pattern.compile(
            "(?i)\\bBuy(?:\\s+Price)?\\s*:?\\s*[$]?\\d");
    private static final Pattern SELL_LABEL_CHECK = Pattern.compile(
            "(?i)\\bSell(?:\\s+Price)?\\s*:?\\s*[$]?\\d");
    // Matches a standalone price value on its own line (for multi-line Buy/Sell format)
    private static final Pattern PLAIN_PRICE_PAT  = Pattern.compile(
            "^([$]?\\d[0-9,.]*[kKmMbBtTqQ]?)$");
    // Finds the first number anywhere in a line (handles "· 12 Coins" bullet-prefix format)
    private static final Pattern PRICE_IN_LINE_PAT = Pattern.compile(
            "([$]?\\d[0-9,.]*[kKmMbBtTqQ]?)");

    // ── Key / balance counter ─────────────────────────────────────────────────
    private static final Pattern KEY_STRIPPED_PAT = Pattern.compile(
            "(?i)(?:(?:balance|crate|you\\s+have)\\s*:?\\s*)?(\\d[0-9,.]*[kKmMbBtTqQ]?)\\s+keys?\\b");

    // ── Key Cost ──────────────────────────────────────────────────────────────
    private static final Pattern KEY_COST_PAT = Pattern.compile(
            "(?i)\\bCost\\s*:\\s*(\\d[0-9,.]*[kKmMbBtTqQ]?)\\s+keys?\\b");

    // ── Per-currency patterns ─────────────────────────────────────────────────
    private static final Pattern SHARDS_LABEL_PAT  = Pattern.compile("(?i)shards\\s*:\\s*([0-9,.]+[kKmMbBtTqQ]?)");
    private static final Pattern SHARDS_SUFFIX_PAT = Pattern.compile("(?i)([0-9,.]+[kKmMbBtTqQ]?)\\s+shards\\b");
    private static final Pattern COINS_LABEL_PAT   = Pattern.compile("(?i)coins\\s*:\\s*([0-9,.]+[kKmMbBtTqQ]?)");
    private static final Pattern COINS_SUFFIX_PAT  = Pattern.compile("(?i)([0-9,.]+[kKmMbBtTqQ]?)\\s+coins\\b");
    private static final Pattern RUBIES_LABEL_PAT  = Pattern.compile("(?i)rubies?\\s*:\\s*([0-9,.]+[kKmMbBtTqQ]?)");
    private static final Pattern RUBIES_SUFFIX_PAT = Pattern.compile("(?i)([0-9,.]+[kKmMbBtTqQ]?)\\s+rubies?\\b");
    private static final Pattern GEMS_LABEL_PAT    = Pattern.compile("(?i)gems\\s*:\\s*([0-9,.]+[kKmMbBtTqQ]?)");
    private static final Pattern GEMS_SUFFIX_PAT   = Pattern.compile("(?i)([0-9,.]+[kKmMbBtTqQ]?)\\s+gems\\b");

    // ── Equipment stat header ("When on Head:", "When Worn:", etc.) ───────────
    private static final Pattern EQUIPMENT_HEADER_PAT = Pattern.compile("(?i)^When\\b.+:");

    // ── Auction / listing expiry: "Ends in: 12d", "Ends in: 6h 30m" ──────────
    private static final Pattern ENDS_IN_PAT = Pattern.compile("(?i)^ends?\\s+in\\s*:");

    // ── Chance ────────────────────────────────────────────────────────────────
    // Labeled: "Chance: X%" — takes priority over bare match
    private static final Pattern CHANCE_LABELED_PAT = Pattern.compile(
            "(?i)\\bChance\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)\\s*%");
    // Bare fallback: any "X%" or "X.Y%"
    private static final Pattern CHANCE_BARE_PAT = Pattern.compile(
            "([0-9]+(?:\\.[0-9]+)?)\\s*%");

    private static final Set<String> loggedNames  = new HashSet<>();
    private static final Set<String> loggedTimers = new HashSet<>();
    private static final Set<String> loggedMisses = new HashSet<>();

    private static final WeakHashMap<ItemStack, ParsedDrillData> CACHE =
            new WeakHashMap<>();
    private static final Set<ItemStack> MISS_CACHE =
            Collections.newSetFromMap(new WeakHashMap<>());

    public static ParsedDrillData getData(ItemStack stack) {
        if (stack.isEmpty() || !DrillConfig.enabled) return null;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return null;

        if (MISS_CACHE.contains(stack)) return null;
        ParsedDrillData cached = CACHE.get(stack);
        if (cached != null) return cached;

        List<Text> tooltip = stack.getTooltip(
                Item.TooltipContext.create(client.world),
                client.player,
                TooltipType.BASIC);
        if (tooltip.isEmpty()) { MISS_CACHE.add(stack); return null; }

        String name = COLOR_CODE.matcher(tooltip.get(0).getString()).replaceAll("").trim();
        boolean isBundleItem = name.toLowerCase().contains("bundle");

        if (DrillSlotInfo.LOGGER.isDebugEnabled() && loggedNames.add(name))
            DrillSlotInfo.LOGGER.debug("[InfoInSlot] saw item '{}'", name);

        ParsedDrillData timerResult  = null;
        String auctionPriceStr       = null;
        String orderPriceStr         = null;
        String deliveredStr          = null;
        String deliveredDoneStr      = null;
        boolean orderFinishedFlag    = false;
        String keyCountStr           = null;
        int    keyColorVal           = 0xFFFFFF;
        String keyCostStr            = null;
        int    keyCostColorVal       = 0xFFFFFF;
        String buyPriceStr           = null;
        String sellPriceStr          = null;
        boolean pendingBuyLabel      = false;  // saw standalone "Buy" label, next value line is buy price
        boolean pendingSellLabel     = false;  // saw standalone "Sell" label, next value line is sell price
        String shardsAmountStr       = null;
        int    shardsColorVal        = 0xFFFFFF;
        String coinsAmountStr        = null;
        int    coinsColorVal         = 0xFFFFFF;
        String rubiesAmountStr       = null;
        int    rubiesColorVal        = 0xFFFFFF;
        String gemsAmountStr         = null;
        int    gemsColorVal          = 0xFFFFFF;
        // Chance: labeled wins over bare; both tracked simultaneously
        String chanceStr             = null;   // labeled match
        int    chanceColorVal        = 0xFFFFFF;
        String chanceBareStr         = null;   // first bare match (fallback)
        int    chanceBareColorVal    = 0xFFFFFF;
        boolean hasEquipmentStatHeader = false;

        for (int i = 1; i < tooltip.size(); i++) {
            Text lineText = tooltip.get(i);
            String rawWithCodes = lineText.getString();
            String raw = COLOR_CODE.matcher(rawWithCodes).replaceAll("").trim();
            if (raw.isEmpty()) continue;

            if (raw.toLowerCase().startsWith("purchased for")) continue;

            // Check for standalone Buy/Sell labels (value on the next line)
            boolean rawIsBuyOnly  = buyPriceStr  == null && (raw.equalsIgnoreCase("buy")  || raw.equalsIgnoreCase("buy:"));
            boolean rawIsSellOnly = sellPriceStr == null && (raw.equalsIgnoreCase("sell") || raw.equalsIgnoreCase("sell:"));
            if (rawIsBuyOnly)  { pendingBuyLabel  = true; }
            if (rawIsSellOnly) { pendingSellLabel = true; }

            // If we have a pending label, try to capture this line as the price value
            boolean lineConsumedForBuySell = false;
            if (!rawIsBuyOnly && !rawIsSellOnly) {
                if (pendingBuyLabel && buyPriceStr == null) {
                    Matcher bm = PLAIN_PRICE_PAT.matcher(raw);
                    if (bm.find()) {
                        buyPriceStr = compressPrice(bm.group(1));
                        lineConsumedForBuySell = true;
                    } else {
                        Matcher lm = PRICE_IN_LINE_PAT.matcher(raw);
                        if (lm.find()) {
                            buyPriceStr = compressPrice(lm.group(1));
                            lineConsumedForBuySell = true;
                        }
                    }
                    pendingBuyLabel = false;
                }
                if (pendingSellLabel && sellPriceStr == null) {
                    Matcher sm = PLAIN_PRICE_PAT.matcher(raw);
                    if (sm.find()) {
                        sellPriceStr = compressPrice(sm.group(1));
                        lineConsumedForBuySell = true;
                    } else {
                        Matcher lm = PRICE_IN_LINE_PAT.matcher(raw);
                        if (lm.find()) {
                            sellPriceStr = compressPrice(lm.group(1));
                            lineConsumedForBuySell = true;
                        }
                    }
                    pendingSellLabel = false;
                }
            }

            // hasBuyLabel/hasSellLabel: true when same-line value OR standalone keyword
            boolean hasBuyLabel  = BUY_LABEL_CHECK.matcher(raw).find() || rawIsBuyOnly;
            boolean hasSellLabel = SELL_LABEL_CHECK.matcher(raw).find() || rawIsSellOnly;

            // ── Buy / Sell (same-line format) ─────────────────────────────────
            if (hasBuyLabel && !rawIsBuyOnly && buyPriceStr == null) {
                Matcher bm = BUY_PRICE_PAT.matcher(raw);
                if (bm.find()) buyPriceStr = compressPrice(bm.group(1));
            }
            if (hasSellLabel && !rawIsSellOnly && sellPriceStr == null) {
                Matcher sm = SELL_PRICE_PAT.matcher(raw);
                if (sm.find()) sellPriceStr = compressPrice(sm.group(1));
            }

            // ── Auction price "$X" ────────────────────────────────────────────
            if (auctionPriceStr == null && !raw.contains("Worth:") && !hasBuyLabel && !hasSellLabel) {
                Matcher pm = AUCTION_PRICE_PAT.matcher(raw);
                if (pm.find()) auctionPriceStr = "$" + formatCompact(parseCompact(pm.group(1)));
            }

            // ── Order price "$ X" ─────────────────────────────────────────────
            if (orderPriceStr == null) {
                Matcher pm = ORDER_PRICE_PAT.matcher(raw);
                if (pm.find()) orderPriceStr = "$" + formatCompact(parseCompact(pm.group(1)));
            }

            // ── R-prefix fallback ──────────────────────────────────────────────
            if (auctionPriceStr == null && !hasBuyLabel && !hasSellLabel) {
                Matcher rm = R_PREFIX_PAT.matcher(raw);
                if (rm.find()) auctionPriceStr = "R" + formatCompact(parseCompact(rm.group(1)));
            }

            // ── Currencies ────────────────────────────────────────────────────
            if (!hasBuyLabel && !hasSellLabel && !lineConsumedForBuySell) {
                if (shardsAmountStr == null) {
                    Matcher lm = SHARDS_LABEL_PAT.matcher(raw);
                    Matcher sm = SHARDS_SUFFIX_PAT.matcher(raw);
                    if (lm.find()) { shardsAmountStr = formatCompact(parseCompact(lm.group(1))); shardsColorVal = extractBestColor(lineText, rawWithCodes, raw, lm.start(1)); }
                    else if (sm.find()) { shardsAmountStr = formatCompact(parseCompact(sm.group(1))); shardsColorVal = extractBestColor(lineText, rawWithCodes, raw, sm.start(1)); }
                }
                if (coinsAmountStr == null) {
                    Matcher lm = COINS_LABEL_PAT.matcher(raw);
                    Matcher sm = COINS_SUFFIX_PAT.matcher(raw);
                    if (lm.find()) { coinsAmountStr = formatCompact(parseCompact(lm.group(1))); coinsColorVal = extractBestColor(lineText, rawWithCodes, raw, lm.start(1)); }
                    else if (sm.find()) { coinsAmountStr = formatCompact(parseCompact(sm.group(1))); coinsColorVal = extractBestColor(lineText, rawWithCodes, raw, sm.start(1)); }
                }
                if (rubiesAmountStr == null) {
                    Matcher lm = RUBIES_LABEL_PAT.matcher(raw);
                    Matcher sm = RUBIES_SUFFIX_PAT.matcher(raw);
                    if (lm.find()) { rubiesAmountStr = formatCompact(parseCompact(lm.group(1))); rubiesColorVal = extractBestColor(lineText, rawWithCodes, raw, lm.start(1)); }
                    else if (sm.find()) { rubiesAmountStr = formatCompact(parseCompact(sm.group(1))); rubiesColorVal = extractBestColor(lineText, rawWithCodes, raw, sm.start(1)); }
                }
                if (gemsAmountStr == null) {
                    Matcher lm = GEMS_LABEL_PAT.matcher(raw);
                    Matcher sm = GEMS_SUFFIX_PAT.matcher(raw);
                    if (lm.find()) { gemsAmountStr = formatCompact(parseCompact(lm.group(1))); gemsColorVal = extractBestColor(lineText, rawWithCodes, raw, lm.start(1)); }
                    else if (sm.find()) { gemsAmountStr = formatCompact(parseCompact(sm.group(1))); gemsColorVal = extractBestColor(lineText, rawWithCodes, raw, sm.start(1)); }
                }
            }

            // ── Delivered count ───────────────────────────────────────────────
            if (deliveredStr == null) {
                Matcher pm = DELIVERED_PAT.matcher(raw);
                if (pm.find()) {
                    double done      = parseCompact(pm.group(1));
                    double total     = parseCompact(pm.group(2));
                    double remaining = Math.max(0, total - done);
                    deliveredStr     = formatCompact(remaining);
                    deliveredDoneStr = formatCompact(done);
                }
            }

            // ── Key Cost (must run before key count) ──────────────────────────
            boolean lineIsKeyCost = false;
            if (keyCostStr == null) {
                Matcher km = KEY_COST_PAT.matcher(raw);
                if (km.find()) {
                    keyCostStr      = formatCompact(parseCompact(km.group(1)));
                    keyCostColorVal = extractBestColor(lineText, rawWithCodes, raw, km.start(1));
                    lineIsKeyCost   = true;
                }
            }

            // ── Key / balance counter (skip cost lines) ───────────────────────
            if (keyCountStr == null && !lineIsKeyCost) {
                Matcher km = KEY_STRIPPED_PAT.matcher(raw);
                if (km.find()) {
                    keyCountStr = formatCompact(parseCompact(km.group(1)));
                    keyColorVal = extractBestColor(lineText, rawWithCodes, raw, km.start(1));
                }
            }

            // ── Equipment stat header detection ───────────────────────────────────
            if (!hasEquipmentStatHeader && EQUIPMENT_HEADER_PAT.matcher(raw).matches())
                hasEquipmentStatHeader = true;

            // ── Chance ────────────────────────────────────────────────────────
            // Labeled match overrides bare; collect both and resolve after loop
            if (chanceStr == null) {
                Matcher cm = CHANCE_LABELED_PAT.matcher(raw);
                if (cm.find()) {
                    try {
                        double v = Double.parseDouble(cm.group(1));
                        chanceStr      = formatChanceValue(v);
                        chanceColorVal = extractBestColor(lineText, rawWithCodes, raw, cm.start(1));
                    } catch (NumberFormatException ignored) { }
                }
            }
            if (chanceBareStr == null) {
                Matcher cm = CHANCE_BARE_PAT.matcher(raw);
                if (cm.find()) {
                    try {
                        double v = Double.parseDouble(cm.group(1));
                        chanceBareStr      = formatChanceValue(v);
                        chanceBareColorVal = extractBestColor(lineText, rawWithCodes, raw, cm.start(1));
                    } catch (NumberFormatException ignored) { }
                }
            }

            // ── Order finished ────────────────────────────────────────────────
            if (!orderFinishedFlag) {
                String lower = raw.toLowerCase();
                if (lower.contains("expir") || lower.contains("complet")) {
                    if (extractTimer(raw) == null) orderFinishedFlag = true;
                }
            }

            // ── Timer ─────────────────────────────────────────────────────────
            if (timerResult == null) {
                // "Ends in: 12d" — dedicated labeled-field match (Hypixel auction expiry)
                if (ENDS_IN_PAT.matcher(raw).find()) {
                    timerResult = extractTimer(raw);
                } else {
                    String lower = raw.toLowerCase();
                    // "self" removed — "destruct" already covers self-destruct, and "self"
                    // is a substring of "yourself", causing false positives on ability text
                    if (lower.contains("destruct") || lower.contains("expir")
                            || lower.contains("complet")
                            || lower.contains("remaining")) {
                        timerResult = extractTimer(raw);
                        if (timerResult == null && i + 1 < tooltip.size()) {
                            String nextRaw = COLOR_CODE.matcher(tooltip.get(i + 1).getString()).replaceAll("").trim();
                            timerResult = extractTimer(nextRaw);
                        }
                    }
                }
            }
        }

        // Suppress chance for bundles (fill %) and equipment items (stat modifiers like +400%)
        boolean suppressChance  = isBundleItem || hasEquipmentStatHeader;
        String finalChanceStr   = suppressChance ? null : (chanceStr != null ? chanceStr : chanceBareStr);
        int    finalChanceColor = chanceStr   != null ? chanceColorVal : chanceBareColorVal;

        if (timerResult != null || auctionPriceStr != null || orderPriceStr != null
                || deliveredStr != null || keyCountStr != null || keyCostStr != null
                || buyPriceStr != null || sellPriceStr != null
                || shardsAmountStr != null || coinsAmountStr != null
                || rubiesAmountStr != null || gemsAmountStr != null
                || finalChanceStr != null) {
            int d = timerResult != null ? timerResult.days    : -1;
            int h = timerResult != null ? timerResult.hours   : -1;
            int m = timerResult != null ? timerResult.minutes : -1;
            int s = timerResult != null ? timerResult.seconds : -1;
            ParsedDrillData result = new ParsedDrillData(d, h, m, s,
                    auctionPriceStr, orderPriceStr, deliveredStr, deliveredDoneStr, orderFinishedFlag,
                    keyCountStr, keyColorVal, keyCostStr, keyCostColorVal,
                    buyPriceStr, sellPriceStr,
                    shardsAmountStr, shardsColorVal,
                    coinsAmountStr, coinsColorVal,
                    rubiesAmountStr, rubiesColorVal,
                    gemsAmountStr, gemsColorVal,
                    finalChanceStr, finalChanceColor);
            if (DrillSlotInfo.LOGGER.isDebugEnabled() && loggedTimers.add(name + "\0" + result.timer))
                DrillSlotInfo.LOGGER.debug(
                    "[InfoInSlot] timer='{}' auction='{}' order='{}' delivered='{}' key='{}' keyCost='{}' " +
                    "buy='{}' sell='{}' shards='{}' coins='{}' rubies='{}' gems='{}' chance='{}' on '{}'",
                    result.timer, result.auctionPrice, result.orderPrice, result.delivered,
                    result.keyCount, result.keyCost, result.buyPrice, result.sellPrice,
                    result.shardsAmount, result.coinsAmount, result.rubiesAmount, result.gemsAmount,
                    result.chanceFormatted, name);
            CACHE.put(stack, result);
            return result;
        }

        MISS_CACHE.add(stack);
        if (DrillSlotInfo.LOGGER.isDebugEnabled() && loggedMisses.add(name)) {
            List<String> lines = new ArrayList<>();
            for (int i = 1; i < tooltip.size(); i++)
                lines.add(COLOR_CODE.matcher(tooltip.get(i).getString()).replaceAll("").trim());
            DrillSlotInfo.LOGGER.debug("[InfoInSlot] '{}' has no usable data. Lines: {}", name, lines);
        }
        return null;
    }

    // ── Compact number helpers ────────────────────────────────────────────────

    static double parseCompact(String s) {
        s = s.replace(",", "").trim();
        if (s.isEmpty()) return 0;
        char last = s.charAt(s.length() - 1);
        double mult = 1;
        String num  = s;
        if      (last == 'k' || last == 'K') { mult = 1e3;  num = s.substring(0, s.length() - 1); }
        else if (last == 'm' || last == 'M') { mult = 1e6;  num = s.substring(0, s.length() - 1); }
        else if (last == 'b' || last == 'B') { mult = 1e9;  num = s.substring(0, s.length() - 1); }
        else if (last == 't' || last == 'T') { mult = 1e12; num = s.substring(0, s.length() - 1); }
        else if (last == 'q' || last == 'Q') { mult = 1e15; num = s.substring(0, s.length() - 1); }
        try { return Double.parseDouble(num) * mult; }
        catch (NumberFormatException e) { return 0; }
    }

    static String formatCompact(double v) {
        if (v <= 0) return "0";
        if (v >= 1e15) return compactNum(v / 1e15) + "Q";
        if (v >= 1e12) return compactNum(v / 1e12) + "T";
        if (v >= 1e9)  return compactNum(v / 1e9)  + "B";
        if (v >= 1e6)  return compactNum(v / 1e6)  + "M";
        if (v >= 1e3)  return compactNum(v / 1e3)  + "k";
        return compactNum(v);
    }

    private static String compactNum(double v) {
        String s = String.format("%.2f", v);
        if (s.contains(".")) s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }

    /**
     * Format a percentage value with appropriate precision.
     * Preserves leading zeros for very small values (e.g. 0.000009 stays "0.000009").
     * Strips insignificant trailing zeros (e.g. 0.0400001 → "0.04").
     */
    static String formatChanceValue(double v) {
        if (v <= 0) return "0";
        if (v >= 1) return compactNum(v);
        // v in (0, 1): determine decimal places needed to show meaningful digits
        if (v >= 0.1) return compactNum(v); // 2dp, strip zeros
        // Count magnitude: 0.01 → magnitude=2, 0.000009 → magnitude=5
        int magnitude = (int) Math.floor(-Math.log10(v));
        int decPlaces = magnitude + 1; // leading zeros + 2 sig digits
        String s = String.format("%." + decPlaces + "f", v);
        if (s.contains(".")) s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }

    /** Compute fraction from a formatted percentage string (e.g. "0.01" → ["1","10k"]). */
    public static String[] computeFraction(String formatted) {
        double v;
        try { v = Double.parseDouble(formatted); } catch (NumberFormatException e) { return new String[]{"?", "?"}; }
        if (v <= 0) return new String[]{"0", "1"};
        int dot = formatted.indexOf('.');
        int decPlaces = dot < 0 ? 0 : formatted.length() - dot - 1;
        long mult = 1;
        for (int i = 0; i < decPlaces; i++) mult *= 10;
        long num = Math.round(v * mult);
        long den = 100L * mult;
        long g = gcd(Math.abs(num), Math.abs(den));
        return new String[]{ String.valueOf(num / g), formatDenominator(den / g) };
    }

    static long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    static String formatDenominator(long den) {
        if (den < 1000L) return String.valueOf(den);
        if (den < 1_000_000L) {
            String s = String.format("%.2f", den / 1000.0).replaceAll("0+$", "").replaceAll("\\.$", "");
            return s + "k";
        }
        if (den < 1_000_000_000L) {
            String s = String.format("%.2f", den / 1_000_000.0).replaceAll("0+$", "").replaceAll("\\.$", "");
            return s + "M";
        }
        String s = String.format("%.2f", den / 1_000_000_000.0).replaceAll("0+$", "").replaceAll("\\.$", "");
        return s + "B";
    }

    private static String compressPrice(String raw) {
        boolean dollar = raw.startsWith("$");
        String num = dollar ? raw.substring(1) : raw;
        return dollar ? "$" + formatCompact(parseCompact(num)) : formatCompact(parseCompact(num));
    }

    // ── Color extraction ──────────────────────────────────────────────────────

    static int extractBestColor(Text lineText, String rawWithCodes, String strippedTrimmed, int strippedPos) {
        String strippedFull = COLOR_CODE.matcher(rawWithCodes).replaceAll("");
        int leadingSpaces = strippedFull.length() - strippedFull.stripLeading().length();
        int actualPos = strippedPos + leadingSpaces;
        int componentColor = extractColorFromTextComponent(lineText, actualPos);
        if (componentColor != 0xFFFFFF) return componentColor;
        return extractColorAtPos(rawWithCodes, actualPos);
    }

    private static int extractColorFromTextComponent(Text text, int targetStrippedPos) {
        int[] pos   = {0};
        int[] found = {0xFFFFFF};
        text.visit((Style style, String s) -> {
            String clean = COLOR_CODE.matcher(s).replaceAll("");
            int end = pos[0] + clean.length();
            if (targetStrippedPos >= pos[0] && targetStrippedPos < end) {
                if (style.getColor() != null) found[0] = style.getColor().getRgb();
                return Optional.of(Unit.INSTANCE);
            }
            pos[0] = end;
            return Optional.empty();
        }, Style.EMPTY);
        return found[0];
    }

    private static int extractColorAtPos(String rawWithCodes, int strippedTargetStart) {
        int rawIdx = 0, strippedIdx = 0, lastColor = 0xFFFFFF;
        while (rawIdx < rawWithCodes.length()) {
            char c = rawWithCodes.charAt(rawIdx);
            if (c == '§' && rawIdx + 1 < rawWithCodes.length()) {
                char code = Character.toLowerCase(rawWithCodes.charAt(rawIdx + 1));
                if ((code >= '0' && code <= '9') || (code >= 'a' && code <= 'f'))
                    lastColor = sectionCodeToRgb(code);
                rawIdx += 2;
            } else {
                if (strippedIdx >= strippedTargetStart) break;
                rawIdx++; strippedIdx++;
            }
        }
        return lastColor;
    }

    private static int sectionCodeToRgb(char c) {
        return switch (c) {
            case '0' -> 0x000000; case '1' -> 0x0000AA; case '2' -> 0x00AA00;
            case '3' -> 0x00AAAA; case '4' -> 0xAA0000; case '5' -> 0xAA00AA;
            case '6' -> 0xFFAA00; case '7' -> 0xAAAAAA; case '8' -> 0x555555;
            case '9' -> 0x5555FF; case 'a' -> 0x55FF55; case 'b' -> 0x55FFFF;
            case 'c' -> 0xFF5555; case 'd' -> 0xFF55FF; case 'e' -> 0xFFFF55;
            default  -> 0xFFFFFF;
        };
    }

    // ── Timer extraction ──────────────────────────────────────────────────────

    private static ParsedDrillData extractTimer(String text) {
        Matcher m = TIME_UNIT.matcher(text);
        int[] v = {-1, -1, -1, -1};
        while (m.find()) {
            int val = Integer.parseInt(m.group(1));
            switch (Character.toLowerCase(m.group(2).charAt(0))) {
                case 'd' -> v[0] = val;
                case 'h' -> v[1] = val;
                case 'm' -> v[2] = val;
                case 's' -> v[3] = val;
            }
        }
        int start = -1;
        for (int i = 0; i < 4; i++) if (v[i] > 0)  { start = i; break; }
        if (start == -1)
        for (int i = 0; i < 4; i++) if (v[i] >= 0) { start = i; break; }
        if (start == -1) return null;
        return new ParsedDrillData(v[0], v[1], v[2], v[3]);
    }
}
