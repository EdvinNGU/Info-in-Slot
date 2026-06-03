package com.drillslotinfo;

import com.drillslotinfo.config.DrillConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DrillItemParser {

    private static final Pattern TIME_UNIT    = Pattern.compile("(\\d+)\\s*([dDhHmMsS])(?![a-zA-Z])");
    private static final Pattern COLOR_CODE   = Pattern.compile("§[0-9a-fk-orA-FK-OR]");

    // Auction price: "$" immediately followed by digits (no space), optional suffix — e.g. "$1.27B", "$0.11", "$107"
    // "Worth:" lines are excluded before this pattern runs to avoid picking up shop NPC prices
    private static final Pattern AUCTION_PRICE_PAT = Pattern.compile("\\$(?!\\s)([0-9,.]+[BMKbmkTtQq]?)");
    // Order price: "$" followed by one or more spaces then digits — e.g. "$ 130.1k", "$ 107M"
    private static final Pattern ORDER_PRICE_PAT   = Pattern.compile("\\$\\s+([0-9,.]+[kKmMbBtTqQ]?)");
    // Delivered: "X/Y Delivered" — X is completed, Y is total
    private static final Pattern DELIVERED_PAT     = Pattern.compile(
            "([0-9][0-9,.]*[kKmMbBtTqQ]?)/([0-9][0-9,.]*[kKmMbBtTqQ]?)\\s+delivered", Pattern.CASE_INSENSITIVE);

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

        if (DrillSlotInfo.LOGGER.isDebugEnabled() && loggedNames.add(name))
            DrillSlotInfo.LOGGER.debug("[InfoInSlot] saw item '{}'", name);

        ParsedDrillData timerResult    = null;
        String auctionPriceStr         = null;
        String orderPriceStr           = null;
        String deliveredStr            = null;
        String deliveredDoneStr        = null;
        boolean orderFinishedFlag      = false;

        for (int i = 1; i < tooltip.size(); i++) {
            String raw = COLOR_CODE.matcher(tooltip.get(i).getString()).replaceAll("").trim();
            if (raw.isEmpty()) continue;

            // Auction price: "$X.XXB/M/k" — no space after $, suffix letter required
            // Skip lines containing "Worth:" to avoid picking up shop/NPC price displays
            if (auctionPriceStr == null && !raw.contains("Worth:")) {
                Matcher pm = AUCTION_PRICE_PAT.matcher(raw);
                if (pm.find()) auctionPriceStr = pm.group(0); // includes the $
            }

            // Order price: "$ X.XXk" or "$ 107M" — space after $; store without the space
            if (orderPriceStr == null) {
                Matcher pm = ORDER_PRICE_PAT.matcher(raw);
                if (pm.find()) orderPriceStr = "$" + pm.group(1); // strip space
            }

            // Delivered count: "X/Y Delivered"
            // Store remaining (Y - X) and also done count (X) for expired/completed orders
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

            // Order finished detection: line contains "expir" or "complet" but has no time units
            // This distinguishes "Order Expired" / "Completed" status from countdown timers
            if (!orderFinishedFlag) {
                String lower = raw.toLowerCase();
                if (lower.contains("expir") || lower.contains("complet")) {
                    if (extractTimer(raw) == null) {
                        orderFinishedFlag = true;
                    }
                }
            }

            // Timer detection (expiry countdown, self-destruct, or order-completed timer)
            if (timerResult == null) {
                String lower = raw.toLowerCase();
                if (lower.contains("destruct") || lower.contains("expir")
                        || lower.contains("self") || lower.contains("complet")) {
                    timerResult = extractTimer(raw);
                    if (timerResult == null && i + 1 < tooltip.size()) {
                        String nextRaw = COLOR_CODE.matcher(tooltip.get(i + 1).getString()).replaceAll("").trim();
                        timerResult = extractTimer(nextRaw);
                    }
                }
            }
        }

        // Return data if we found anything useful — timer is not required
        if (timerResult != null || auctionPriceStr != null || orderPriceStr != null || deliveredStr != null) {
            int d = timerResult != null ? timerResult.days    : -1;
            int h = timerResult != null ? timerResult.hours   : -1;
            int m = timerResult != null ? timerResult.minutes : -1;
            int s = timerResult != null ? timerResult.seconds : -1;
            ParsedDrillData result = new ParsedDrillData(d, h, m, s,
                    auctionPriceStr, orderPriceStr, deliveredStr, deliveredDoneStr, orderFinishedFlag);
            if (DrillSlotInfo.LOGGER.isDebugEnabled() && loggedTimers.add(name + "\0" + result.timer))
                DrillSlotInfo.LOGGER.debug("[InfoInSlot] timer='{}' auction='{}' order='{}' delivered='{}' finished='{}' on '{}'",
                        result.timer, result.auctionPrice, result.orderPrice, result.delivered, result.orderFinished, name);
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
        return String.valueOf((long) Math.round(v));
    }

    private static String compactNum(double v) {
        long whole = (long) v;
        if (whole == v) return String.valueOf(whole);
        return String.format("%.1f", v);
    }

    // ── Timer extraction ──────────────────────────────────────────────────────

    private static ParsedDrillData extractTimer(String text) {
        Matcher m = TIME_UNIT.matcher(text);
        int[] v = {-1, -1, -1, -1}; // d h m s
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
