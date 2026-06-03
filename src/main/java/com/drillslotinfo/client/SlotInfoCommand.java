package com.drillslotinfo.client;

import com.drillslotinfo.config.DrillConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SlotInfoCommand {

    private static final Map<String, Integer> COLOR_NAMES  = new LinkedHashMap<>();
    private static final Map<Integer, String> COLOR_LOOKUP = new LinkedHashMap<>();

    static {
        Object[] pairs = {
            "black",       0x000000,
            "dark_blue",   0x0000AA,
            "dark_green",  0x00AA00,
            "dark_aqua",   0x00AAAA,
            "dark_red",    0xAA0000,
            "dark_purple", 0xAA00AA,
            "gold",        0xFFAA00,
            "gray",        0xAAAAAA,
            "dark_gray",   0x555555,
            "blue",        0x5555FF,
            "green",       0x55FF55,
            "aqua",        0x55FFFF,
            "red",         0xFF5555,
            "magenta",     0xFF55FF,
            "yellow",      0xFFFF55,
            "white",       0xFFFFFF,
        };
        for (int i = 0; i < pairs.length; i += 2) {
            String name = (String) pairs[i];
            int    val  = (Integer) pairs[i + 1];
            COLOR_NAMES.put(name, val);
            COLOR_LOOKUP.putIfAbsent(val, name);
        }
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess) {
        dispatcher.register(
            literal("slotinfo")
                .executes(ctx -> showStatus(ctx.getSource()))

                .then(literal("enable")
                    .then(literal("on") .executes(ctx -> { DrillConfig.enabled = true;  return ok(ctx.getSource(), "Mod enabled");  }))
                    .then(literal("off").executes(ctx -> { DrillConfig.enabled = false; return ok(ctx.getSource(), "Mod disabled"); })))

                // ── Timer settings ────────────────────────────────────────────
                .then(literal("background")
                    .then(literal("on") .executes(ctx -> { DrillConfig.showBackground = true;  return ok(ctx.getSource(), "Timer background on");  }))
                    .then(literal("off").executes(ctx -> { DrillConfig.showBackground = false; return ok(ctx.getSource(), "Timer background off"); })))

                .then(literal("hours")
                    .then(literal("on") .executes(ctx -> { DrillConfig.hoursOnly = true; return ok(ctx.getSource(), "Hours Only on"); }))
                    .then(literal("off").executes(ctx -> {
                        DrillConfig.hoursOnly = false;
                        if (DrillConfig.timerSize == DrillConfig.TimerSize.LARGE)
                            DrillConfig.timerSize = DrillConfig.TimerSize.MEDIUM;
                        return ok(ctx.getSource(), "Hours Only off");
                    })))

                .then(literal("separate")
                    .then(literal("on") .executes(ctx -> { DrillConfig.separateSlotColors = true;  return ok(ctx.getSource(), "Separate Colors on");  }))
                    .then(literal("off").executes(ctx -> { DrillConfig.separateSlotColors = false; return ok(ctx.getSource(), "Separate Colors off"); })))

                .then(literal("size")
                    .then(literal("small") .executes(ctx -> setTimerSize(ctx.getSource(), DrillConfig.TimerSize.SMALL)))
                    .then(literal("medium").executes(ctx -> setTimerSize(ctx.getSource(), DrillConfig.TimerSize.MEDIUM)))
                    .then(literal("large") .executes(ctx -> setTimerSize(ctx.getSource(), DrillConfig.TimerSize.LARGE))))

                .then(literal("position")
                    .then(literal("top")   .executes(ctx -> { DrillConfig.timerPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Timer position: Top");    }))
                    .then(literal("middle").executes(ctx -> { DrillConfig.timerPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Timer position: Middle"); }))
                    .then(literal("bottom").executes(ctx -> { DrillConfig.timerPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Timer position: Bottom"); })))

                .then(literal("bold")
                    .then(argument("tier", StringArgumentType.word())
                        .suggests((ctx, b) -> {
                            for (String t : new String[]{"2day","day","hour","minute","all"}) b.suggest(t);
                            return b.buildFuture();
                        })
                        .then(literal("on") .executes(ctx -> setBold(ctx.getSource(), getString(ctx, "tier"), true)))
                        .then(literal("off").executes(ctx -> setBold(ctx.getSource(), getString(ctx, "tier"), false)))))

                .then(literal("color")
                    .then(argument("tier", StringArgumentType.word())
                        .suggests((ctx, b) -> {
                            for (String t : new String[]{"2day","day","hour","minute"}) b.suggest(t);
                            return b.buildFuture();
                        })
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> setTimerColor(ctx.getSource(), getString(ctx, "tier"), getString(ctx, "color"))))))

                // ── Auction Price ─────────────────────────────────────────────
                .then(literal("price")
                    .executes(ctx -> showPriceStatus(ctx.getSource()))
                    .then(literal("on") .executes(ctx -> { DrillConfig.showPrice = true;  return ok(ctx.getSource(), "Auction price on");  }))
                    .then(literal("off").executes(ctx -> { DrillConfig.showPrice = false; return ok(ctx.getSource(), "Auction price off"); }))
                    .then(literal("background")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showPriceBackground = true;  return ok(ctx.getSource(), "Auction price background on");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showPriceBackground = false; return ok(ctx.getSource(), "Auction price background off"); })))
                    .then(literal("size")
                        .then(literal("small") .executes(ctx -> { DrillConfig.priceSize = DrillConfig.PriceSize.SMALL;  return ok(ctx.getSource(), "Auction price size: Small");  }))
                        .then(literal("medium").executes(ctx -> { DrillConfig.priceSize = DrillConfig.PriceSize.MEDIUM; return ok(ctx.getSource(), "Auction price size: Medium"); })))
                    .then(literal("position")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.pricePosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Auction price position: Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.pricePosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Auction price position: Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.pricePosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Auction price position: Bottom"); })))
                    .then(literal("color")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> setColor(ctx.getSource(), getString(ctx, "color"), "price"))))
                    .then(literal("bold")
                        .then(literal("on") .executes(ctx -> { DrillConfig.priceBold = true;  return ok(ctx.getSource(), "Auction price bold on");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.priceBold = false; return ok(ctx.getSource(), "Auction price bold off"); }))))

                // ── Order Price ───────────────────────────────────────────────
                .then(literal("orderprice")
                    .executes(ctx -> showOrderPriceStatus(ctx.getSource()))
                    .then(literal("on") .executes(ctx -> { DrillConfig.showOrderPrice = true;  return ok(ctx.getSource(), "Order price on");  }))
                    .then(literal("off").executes(ctx -> { DrillConfig.showOrderPrice = false; return ok(ctx.getSource(), "Order price off"); }))
                    .then(literal("background")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showOrderPriceBackground = true;  return ok(ctx.getSource(), "Order price background on");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showOrderPriceBackground = false; return ok(ctx.getSource(), "Order price background off"); })))
                    .then(literal("size")
                        .then(literal("small") .executes(ctx -> { DrillConfig.orderPriceSize = DrillConfig.PriceSize.SMALL;  return ok(ctx.getSource(), "Order price size: Small");  }))
                        .then(literal("medium").executes(ctx -> { DrillConfig.orderPriceSize = DrillConfig.PriceSize.MEDIUM; return ok(ctx.getSource(), "Order price size: Medium"); })))
                    .then(literal("position")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.orderPricePosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Order price position: Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.orderPricePosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Order price position: Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.orderPricePosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Order price position: Bottom"); })))
                    .then(literal("color")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> setColor(ctx.getSource(), getString(ctx, "color"), "orderprice"))))
                    .then(literal("bold")
                        .then(literal("on") .executes(ctx -> { DrillConfig.orderPriceBold = true;  return ok(ctx.getSource(), "Order price bold on");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.orderPriceBold = false; return ok(ctx.getSource(), "Order price bold off"); }))))

                // ── Order Delivered ───────────────────────────────────────────
                .then(literal("delivered")
                    .executes(ctx -> showDeliveredStatus(ctx.getSource()))
                    .then(literal("on") .executes(ctx -> { DrillConfig.showDelivered = true;  return ok(ctx.getSource(), "Delivered display on");  }))
                    .then(literal("off").executes(ctx -> { DrillConfig.showDelivered = false; return ok(ctx.getSource(), "Delivered display off"); }))
                    .then(literal("background")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showDeliveredBackground = true;  return ok(ctx.getSource(), "Delivered background on");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showDeliveredBackground = false; return ok(ctx.getSource(), "Delivered background off"); })))
                    .then(literal("size")
                        .then(literal("small") .executes(ctx -> { DrillConfig.deliveredSize = DrillConfig.PriceSize.SMALL;  return ok(ctx.getSource(), "Delivered size: Small");  }))
                        .then(literal("medium").executes(ctx -> { DrillConfig.deliveredSize = DrillConfig.PriceSize.MEDIUM; return ok(ctx.getSource(), "Delivered size: Medium"); })))
                    .then(literal("position")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.deliveredPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Delivered position: Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.deliveredPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Delivered position: Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.deliveredPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Delivered position: Bottom"); })))
                    .then(literal("color")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> setColor(ctx.getSource(), getString(ctx, "color"), "delivered"))))
                    .then(literal("bold")
                        .then(literal("on") .executes(ctx -> { DrillConfig.deliveredBold = true;  return ok(ctx.getSource(), "Delivered bold on");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.deliveredBold = false; return ok(ctx.getSource(), "Delivered bold off"); }))))
        );
    }

    // ── Status displays ───────────────────────────────────────────────────────

    private static int showStatus(FabricClientCommandSource src) {
        src.sendFeedback(Text.literal("§e=== Info in Slot Settings ==="));
        src.sendFeedback(Text.literal(
            "§7Enabled: §f" + bool(DrillConfig.enabled) +
            " §7| Show Timer: §f" + bool(DrillConfig.showTimer)));
        src.sendFeedback(Text.literal(
            "§7Size: §f" + cap(DrillConfig.timerSize.name()) +
            " §7| Hours Only: §f" + bool(DrillConfig.hoursOnly) +
            " §7| Pos: §f" + cap(DrillConfig.timerPosition.name())));
        src.sendFeedback(Text.literal(
            "§7BG: §f" + bool(DrillConfig.showBackground) +
            " §7| Separate: §f" + bool(DrillConfig.separateSlotColors)));
        src.sendFeedback(Text.literal(
            "§72+Day §f" + cname(DrillConfig.twoDayColor) + btag(DrillConfig.twoDayBold) +
            " §7Day §f"  + cname(DrillConfig.dayColor)    + btag(DrillConfig.dayBold) +
            " §7Hour §f" + cname(DrillConfig.hourColor)   + btag(DrillConfig.hourBold) +
            " §7Min §f"  + cname(DrillConfig.minuteColor) + btag(DrillConfig.minuteBold)));
        showPriceStatus(src);
        showOrderPriceStatus(src);
        showDeliveredStatus(src);
        return 1;
    }

    private static int showPriceStatus(FabricClientCommandSource src) {
        src.sendFeedback(Text.literal(
            "§e[Auction Price] §7Show: §f" + bool(DrillConfig.showPrice) +
            " §7Pos: §f" + cap(DrillConfig.pricePosition.name()) +
            " §7BG: §f"  + bool(DrillConfig.showPriceBackground) +
            " §7Size: §f" + cap(DrillConfig.priceSize.name()) +
            " §7Color: §f" + cname(DrillConfig.priceColor) + btag(DrillConfig.priceBold)));
        return 1;
    }

    private static int showOrderPriceStatus(FabricClientCommandSource src) {
        src.sendFeedback(Text.literal(
            "§e[Order Price] §7Show: §f" + bool(DrillConfig.showOrderPrice) +
            " §7Pos: §f" + cap(DrillConfig.orderPricePosition.name()) +
            " §7BG: §f"  + bool(DrillConfig.showOrderPriceBackground) +
            " §7Size: §f" + cap(DrillConfig.orderPriceSize.name()) +
            " §7Color: §f" + cname(DrillConfig.orderPriceColor) + btag(DrillConfig.orderPriceBold)));
        return 1;
    }

    private static int showDeliveredStatus(FabricClientCommandSource src) {
        src.sendFeedback(Text.literal(
            "§e[Delivered] §7Show: §f" + bool(DrillConfig.showDelivered) +
            " §7Pos: §f" + cap(DrillConfig.deliveredPosition.name()) +
            " §7BG: §f"  + bool(DrillConfig.showDeliveredBackground) +
            " §7Size: §f" + cap(DrillConfig.deliveredSize.name()) +
            " §7Color: §f" + cname(DrillConfig.deliveredColor) + btag(DrillConfig.deliveredBold)));
        return 1;
    }

    // ── Handlers ──────────────────────────────────────────────────────────────

    private static int setTimerSize(FabricClientCommandSource src, DrillConfig.TimerSize size) {
        if (size == DrillConfig.TimerSize.LARGE && !DrillConfig.hoursOnly) {
            src.sendFeedback(Text.literal("§c[SlotInfo] Large requires Hours Only first."));
            return 0;
        }
        DrillConfig.timerSize = size;
        return ok(src, "Timer size: " + cap(size.name()));
    }

    private static int setBold(FabricClientCommandSource src, String tier, boolean on) {
        switch (tier.toLowerCase()) {
            case "2day"   -> DrillConfig.twoDayBold = on;
            case "day"    -> DrillConfig.dayBold    = on;
            case "hour"   -> DrillConfig.hourBold   = on;
            case "minute" -> DrillConfig.minuteBold = on;
            case "all"    -> { DrillConfig.twoDayBold = DrillConfig.dayBold = DrillConfig.hourBold = DrillConfig.minuteBold = on; }
            default -> { src.sendFeedback(Text.literal("§c[SlotInfo] Unknown tier: " + tier)); return 0; }
        }
        return ok(src, "Bold " + (on ? "on" : "off") + " for " + tier);
    }

    private static int setTimerColor(FabricClientCommandSource src, String tier, String colorArg) {
        Integer val = COLOR_NAMES.get(colorArg.toLowerCase());
        if (val == null) { src.sendFeedback(Text.literal("§c[SlotInfo] Unknown color: " + colorArg)); return 0; }
        switch (tier.toLowerCase()) {
            case "2day"   -> DrillConfig.twoDayColor  = val;
            case "day"    -> DrillConfig.dayColor     = val;
            case "hour"   -> DrillConfig.hourColor    = val;
            case "minute" -> DrillConfig.minuteColor  = val;
            default -> { src.sendFeedback(Text.literal("§c[SlotInfo] Unknown tier: " + tier)); return 0; }
        }
        return ok(src, "Timer color for " + tier + " → " + colorArg);
    }

    private static int setColor(FabricClientCommandSource src, String colorArg, String target) {
        Integer val = COLOR_NAMES.get(colorArg.toLowerCase());
        if (val == null) { src.sendFeedback(Text.literal("§c[SlotInfo] Unknown color: " + colorArg)); return 0; }
        switch (target) {
            case "price"      -> DrillConfig.priceColor      = val;
            case "orderprice" -> DrillConfig.orderPriceColor = val;
            case "delivered"  -> DrillConfig.deliveredColor  = val;
        }
        return ok(src, cap(target) + " color → " + colorArg);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static int ok(FabricClientCommandSource src, String msg) {
        src.sendFeedback(Text.literal("§a[SlotInfo] " + msg));
        DrillConfig.save();
        return 1;
    }

    private static String cname(int rgb) { return COLOR_LOOKUP.getOrDefault(rgb, String.format("#%06X", rgb)); }
    private static String btag(boolean b) { return b ? " §7(Bold)" : ""; }
    private static String bool(boolean b) { return b ? "On" : "Off"; }
    private static String cap(String s) {
        if (s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
}
