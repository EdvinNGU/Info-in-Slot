package com.drillslotinfo.client;

import com.drillslotinfo.config.DrillConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
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
            "auto",        0,
        };
        for (int i = 0; i < pairs.length; i += 2) {
            COLOR_NAMES.put((String) pairs[i], (Integer) pairs[i + 1]);
            COLOR_LOOKUP.putIfAbsent((Integer) pairs[i + 1], (String) pairs[i]);
        }
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess) {
        dispatcher.register(
            literal("infoinslot")
                .executes(ctx -> showStatus(ctx.getSource()))

                // ── /infoinslot main ──────────────────────────────────────────
                .then(literal("main")
                    .executes(ctx -> showMainStatus(ctx.getSource()))

                    .then(literal("enablemod")
                        .then(literal("on") .executes(ctx -> { DrillConfig.enabled = true;  return ok(ctx.getSource(), "Enable Mod → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.enabled = false; return ok(ctx.getSource(), "Enable Mod → Off"); })))

                    .then(literal("showtimer")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showTimer = true;  return ok(ctx.getSource(), "Show Timer → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showTimer = false; return ok(ctx.getSource(), "Show Timer → Off"); })))

                    .then(literal("showahorderprice")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showOrderPrice = true;  return ok(ctx.getSource(), "Show Order/Auction Price → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showOrderPrice = false; return ok(ctx.getSource(), "Show Order/Auction Price → Off"); })))

                    .then(literal("togglecoins")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showCoins = true;  return ok(ctx.getSource(), "Toggle Coins → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showCoins = false; return ok(ctx.getSource(), "Toggle Coins → Off"); })))

                    .then(literal("toggleshards")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showShards = true;  return ok(ctx.getSource(), "Toggle Shards → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showShards = false; return ok(ctx.getSource(), "Toggle Shards → Off"); })))

                    .then(literal("togglekeys")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showKeyCount = true;  return ok(ctx.getSource(), "Toggle Keys → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showKeyCount = false; return ok(ctx.getSource(), "Toggle Keys → Off"); })))

                    .then(literal("showcontainer")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showInContainer = true;  return ok(ctx.getSource(), "Display in Container → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showInContainer = false; return ok(ctx.getSource(), "Display in Container → Off"); })))

                    .then(literal("showinventory")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showInInventory = true;  return ok(ctx.getSource(), "Display in Inventory → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showInInventory = false; return ok(ctx.getSource(), "Display in Inventory → Off"); })))

                    .then(literal("showhotbar")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showInHotbar = true;  return ok(ctx.getSource(), "Display in Hotbar → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showInHotbar = false; return ok(ctx.getSource(), "Display in Hotbar → Off"); })))

                    .then(literal("showordercount")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showDelivered = true;  return ok(ctx.getSource(), "Show Order Count → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showDelivered = false; return ok(ctx.getSource(), "Show Order Count → Off"); })))

                    .then(literal("showsymbol")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showCurrencySymbol = true;  return ok(ctx.getSource(), "Currency Symbol → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showCurrencySymbol = false; return ok(ctx.getSource(), "Currency Symbol → Off"); })))

                    .then(literal("backgrounds")
                        .then(literal("on") .executes(ctx -> { DrillConfig.setAllBackgrounds(true);  return ok(ctx.getSource(), "All Backgrounds → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.setAllBackgrounds(false); return ok(ctx.getSource(), "All Backgrounds → Off"); })))

                    .then(literal("universalbgcolor")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> {
                                Integer val = parseColor(ctx.getSource(), getString(ctx, "color"));
                                if (val == null) return 0;
                                DrillConfig.setAllBackgroundColors(val);
                                return ok(ctx.getSource(), "Universal BG Color → " + getString(ctx, "color"));
                            })))

                    .then(literal("universalsize")
                        .then(literal("xsmall").executes(ctx -> { DrillConfig.setAllSizes(DrillConfig.PriceSize.XSMALL); return ok(ctx.getSource(), "Universal Size → XSmall"); }))
                        .then(literal("small") .executes(ctx -> { DrillConfig.setAllSizes(DrillConfig.PriceSize.SMALL);  return ok(ctx.getSource(), "Universal Size → Small");  }))
                        .then(literal("medium").executes(ctx -> { DrillConfig.setAllSizes(DrillConfig.PriceSize.MEDIUM); return ok(ctx.getSource(), "Universal Size → Medium"); }))
                        .then(literal("large") .executes(ctx -> { DrillConfig.setAllSizes(DrillConfig.PriceSize.LARGE);  return ok(ctx.getSource(), "Universal Size → Large");  }))))

                // ── /infoinslot timer ─────────────────────────────────────────
                .then(literal("timer")
                    .executes(ctx -> showTimerStatus(ctx.getSource()))

                    .then(literal("show")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showTimer = true;  return ok(ctx.getSource(), "Show Timer → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showTimer = false; return ok(ctx.getSource(), "Show Timer → Off"); })))

                    .then(literal("background")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showBackground = true;  return ok(ctx.getSource(), "Timer Background → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showBackground = false; return ok(ctx.getSource(), "Timer Background → Off"); })))

                    .then(literal("size")
                        .then(literal("xsmall").executes(ctx -> { DrillConfig.timerSize = DrillConfig.TimerSize.XSMALL; return ok(ctx.getSource(), "Timer Size → XSmall"); }))
                        .then(literal("small") .executes(ctx -> { DrillConfig.timerSize = DrillConfig.TimerSize.SMALL;  return ok(ctx.getSource(), "Timer Size → Small");  }))
                        .then(literal("medium").executes(ctx -> { DrillConfig.timerSize = DrillConfig.TimerSize.MEDIUM; return ok(ctx.getSource(), "Timer Size → Medium"); }))
                        .then(literal("large") .executes(ctx -> { DrillConfig.timerSize = DrillConfig.TimerSize.LARGE;  return ok(ctx.getSource(), "Timer Size → Large");  })))

                    .then(literal("positionheight")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.timerPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Timer Height → Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.timerPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Timer Height → Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.timerPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Timer Height → Bottom"); })))

                    .then(literal("position")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.timerPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Timer Height → Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.timerPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Timer Height → Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.timerPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Timer Height → Bottom"); })))

                    .then(literal("positionwidth")
                        .then(literal("left")  .executes(ctx -> { DrillConfig.timerHorizPosition = DrillConfig.HorizPosition.LEFT;   return ok(ctx.getSource(), "Timer Width → Left");   }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.timerHorizPosition = DrillConfig.HorizPosition.MIDDLE; return ok(ctx.getSource(), "Timer Width → Middle"); }))
                        .then(literal("right") .executes(ctx -> { DrillConfig.timerHorizPosition = DrillConfig.HorizPosition.RIGHT;  return ok(ctx.getSource(), "Timer Width → Right");  })))

                    .then(literal("separatecolors")
                        .then(literal("on") .executes(ctx -> { DrillConfig.separateSlotColors = true;  return ok(ctx.getSource(), "Separate Colors → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.separateSlotColors = false; return ok(ctx.getSource(), "Separate Colors → Off"); })))

                    .then(literal("hoursonly")
                        .then(literal("on") .executes(ctx -> { DrillConfig.hoursOnly = true;  return ok(ctx.getSource(), "Hours Only → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.hoursOnly = false; return ok(ctx.getSource(), "Hours Only → Off"); })))

                    .then(literal("bold")
                        .then(argument("tier", StringArgumentType.word())
                            .suggests((ctx, b) -> { for (String t : new String[]{"2day","day","hour","minute","all"}) b.suggest(t); return b.buildFuture(); })
                            .then(literal("on") .executes(ctx -> setTimerBold(ctx.getSource(), getString(ctx, "tier"), true)))
                            .then(literal("off").executes(ctx -> setTimerBold(ctx.getSource(), getString(ctx, "tier"), false)))))

                    .then(literal("color")
                        .then(argument("tier", StringArgumentType.word())
                            .suggests((ctx, b) -> { for (String t : new String[]{"2day","day","hour","minute"}) b.suggest(t); return b.buildFuture(); })
                            .then(argument("color", StringArgumentType.word())
                                .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                                .executes(ctx -> setTimerColor(ctx.getSource(), getString(ctx, "tier"), getString(ctx, "color")))))))

                // ── /infoinslot ahorderprice ─────────────────────────────────
                .then(literal("ahorderprice")
                    .executes(ctx -> showOrderPriceStatus(ctx.getSource()))

                    .then(literal("show")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showOrderPrice = true;  return ok(ctx.getSource(), "Order/Auction Price → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showOrderPrice = false; return ok(ctx.getSource(), "Order/Auction Price → Off"); })))

                    .then(literal("background")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showOrderPriceBackground = true;  return ok(ctx.getSource(), "Order Price BG → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showOrderPriceBackground = false; return ok(ctx.getSource(), "Order Price BG → Off"); })))

                    .then(literal("positionheight")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.orderPricePosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Order Price Height → Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.orderPricePosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Order Price Height → Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.orderPricePosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Order Price Height → Bottom"); })))

                    .then(literal("position")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.orderPricePosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Order Price Height → Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.orderPricePosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Order Price Height → Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.orderPricePosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Order Price Height → Bottom"); })))

                    .then(literal("positionwidth")
                        .then(literal("left")  .executes(ctx -> { DrillConfig.orderPriceHorizPosition = DrillConfig.HorizPosition.LEFT;   return ok(ctx.getSource(), "Order Price Width → Left");   }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.orderPriceHorizPosition = DrillConfig.HorizPosition.MIDDLE; return ok(ctx.getSource(), "Order Price Width → Middle"); }))
                        .then(literal("right") .executes(ctx -> { DrillConfig.orderPriceHorizPosition = DrillConfig.HorizPosition.RIGHT;  return ok(ctx.getSource(), "Order Price Width → Right");  })))

                    .then(literal("size")
                        .then(literal("xsmall").executes(ctx -> { DrillConfig.orderPriceSize = DrillConfig.PriceSize.XSMALL; return ok(ctx.getSource(), "Order Price Size → XSmall"); }))
                        .then(literal("small") .executes(ctx -> { DrillConfig.orderPriceSize = DrillConfig.PriceSize.SMALL;  return ok(ctx.getSource(), "Order Price Size → Small");  }))
                        .then(literal("medium").executes(ctx -> { DrillConfig.orderPriceSize = DrillConfig.PriceSize.MEDIUM; return ok(ctx.getSource(), "Order Price Size → Medium"); }))
                        .then(literal("large") .executes(ctx -> { DrillConfig.orderPriceSize = DrillConfig.PriceSize.LARGE;  return ok(ctx.getSource(), "Order Price Size → Large");  })))

                    .then(literal("color")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> setDisplayColor(ctx.getSource(), getString(ctx, "color"), "orderprice"))))

                    .then(literal("bold")
                        .then(literal("on") .executes(ctx -> { DrillConfig.orderPriceBold = true;  return ok(ctx.getSource(), "Order Price Bold → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.orderPriceBold = false; return ok(ctx.getSource(), "Order Price Bold → Off"); }))))

                // ── /infoinslot ordercount ────────────────────────────────────
                .then(literal("ordercount")
                    .executes(ctx -> showOrderCountStatus(ctx.getSource()))

                    .then(literal("show")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showDelivered = true;  return ok(ctx.getSource(), "Order Count → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showDelivered = false; return ok(ctx.getSource(), "Order Count → Off"); })))

                    .then(literal("background")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showDeliveredBackground = true;  return ok(ctx.getSource(), "Order Count BG → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showDeliveredBackground = false; return ok(ctx.getSource(), "Order Count BG → Off"); })))

                    .then(literal("positionheight")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.deliveredPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Order Count Height → Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.deliveredPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Order Count Height → Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.deliveredPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Order Count Height → Bottom"); })))

                    .then(literal("position")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.deliveredPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Order Count Height → Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.deliveredPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Order Count Height → Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.deliveredPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Order Count Height → Bottom"); })))

                    .then(literal("positionwidth")
                        .then(literal("left")  .executes(ctx -> { DrillConfig.deliveredHorizPosition = DrillConfig.HorizPosition.LEFT;   return ok(ctx.getSource(), "Order Count Width → Left");   }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.deliveredHorizPosition = DrillConfig.HorizPosition.MIDDLE; return ok(ctx.getSource(), "Order Count Width → Middle"); }))
                        .then(literal("right") .executes(ctx -> { DrillConfig.deliveredHorizPosition = DrillConfig.HorizPosition.RIGHT;  return ok(ctx.getSource(), "Order Count Width → Right");  })))

                    .then(literal("size")
                        .then(literal("xsmall").executes(ctx -> { DrillConfig.deliveredSize = DrillConfig.PriceSize.XSMALL; return ok(ctx.getSource(), "Order Count Size → XSmall"); }))
                        .then(literal("small") .executes(ctx -> { DrillConfig.deliveredSize = DrillConfig.PriceSize.SMALL;  return ok(ctx.getSource(), "Order Count Size → Small");  }))
                        .then(literal("medium").executes(ctx -> { DrillConfig.deliveredSize = DrillConfig.PriceSize.MEDIUM; return ok(ctx.getSource(), "Order Count Size → Medium"); }))
                        .then(literal("large") .executes(ctx -> { DrillConfig.deliveredSize = DrillConfig.PriceSize.LARGE;  return ok(ctx.getSource(), "Order Count Size → Large");  })))

                    .then(literal("color")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> setDisplayColor(ctx.getSource(), getString(ctx, "color"), "delivered"))))

                    .then(literal("bold")
                        .then(literal("on") .executes(ctx -> { DrillConfig.deliveredBold = true;  return ok(ctx.getSource(), "Order Count Bold → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.deliveredBold = false; return ok(ctx.getSource(), "Order Count Bold → Off"); }))))

                // ── /infoinslot buysell ───────────────────────────────────────
                .then(literal("buysell")
                    .executes(ctx -> showBuySellStatus(ctx.getSource()))

                    .then(literal("buyprice")
                        .then(literal("show")
                            .then(literal("on") .executes(ctx -> { DrillConfig.showBuyPrice = true;  return ok(ctx.getSource(), "Buy Price → On");  }))
                            .then(literal("off").executes(ctx -> { DrillConfig.showBuyPrice = false; return ok(ctx.getSource(), "Buy Price → Off"); })))
                        .then(literal("positionheight")
                            .then(literal("top")   .executes(ctx -> { DrillConfig.buyPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Buy Height → Top");    }))
                            .then(literal("middle").executes(ctx -> { DrillConfig.buyPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Buy Height → Middle"); }))
                            .then(literal("bottom").executes(ctx -> { DrillConfig.buyPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Buy Height → Bottom"); })))
                        .then(literal("position")
                            .then(literal("top")   .executes(ctx -> { DrillConfig.buyPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Buy Height → Top");    }))
                            .then(literal("middle").executes(ctx -> { DrillConfig.buyPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Buy Height → Middle"); }))
                            .then(literal("bottom").executes(ctx -> { DrillConfig.buyPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Buy Height → Bottom"); })))
                        .then(literal("positionwidth")
                            .then(literal("left")  .executes(ctx -> { DrillConfig.buyHorizPosition = DrillConfig.HorizPosition.LEFT;   return ok(ctx.getSource(), "Buy Width → Left");   }))
                            .then(literal("middle").executes(ctx -> { DrillConfig.buyHorizPosition = DrillConfig.HorizPosition.MIDDLE; return ok(ctx.getSource(), "Buy Width → Middle"); }))
                            .then(literal("right") .executes(ctx -> { DrillConfig.buyHorizPosition = DrillConfig.HorizPosition.RIGHT;  return ok(ctx.getSource(), "Buy Width → Right");  })))
                        .then(literal("size")
                            .then(literal("xsmall").executes(ctx -> { DrillConfig.buySize = DrillConfig.PriceSize.XSMALL; return ok(ctx.getSource(), "Buy Size → XSmall"); }))
                            .then(literal("small") .executes(ctx -> { DrillConfig.buySize = DrillConfig.PriceSize.SMALL;  return ok(ctx.getSource(), "Buy Size → Small");  }))
                            .then(literal("medium").executes(ctx -> { DrillConfig.buySize = DrillConfig.PriceSize.MEDIUM; return ok(ctx.getSource(), "Buy Size → Medium"); }))
                            .then(literal("large") .executes(ctx -> { DrillConfig.buySize = DrillConfig.PriceSize.LARGE;  return ok(ctx.getSource(), "Buy Size → Large");  })))
                        .then(literal("bold")
                            .then(literal("on") .executes(ctx -> { DrillConfig.buyBold = true;  return ok(ctx.getSource(), "Buy Bold → On");  }))
                            .then(literal("off").executes(ctx -> { DrillConfig.buyBold = false; return ok(ctx.getSource(), "Buy Bold → Off"); })))
                        .then(literal("background")
                            .then(literal("on") .executes(ctx -> { DrillConfig.showBuyBackground = true;  return ok(ctx.getSource(), "Buy BG → On");  }))
                            .then(literal("off").executes(ctx -> { DrillConfig.showBuyBackground = false; return ok(ctx.getSource(), "Buy BG → Off"); })))
                        .then(literal("color")
                            .then(argument("color", StringArgumentType.word())
                                .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                                .executes(ctx -> setDisplayColor(ctx.getSource(), getString(ctx, "color"), "buy")))))

                    .then(literal("sellprice")
                        .then(literal("show")
                            .then(literal("on") .executes(ctx -> { DrillConfig.showSellPrice = true;  return ok(ctx.getSource(), "Sell Price → On");  }))
                            .then(literal("off").executes(ctx -> { DrillConfig.showSellPrice = false; return ok(ctx.getSource(), "Sell Price → Off"); })))
                        .then(literal("positionheight")
                            .then(literal("top")   .executes(ctx -> { DrillConfig.sellPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Sell Height → Top");    }))
                            .then(literal("middle").executes(ctx -> { DrillConfig.sellPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Sell Height → Middle"); }))
                            .then(literal("bottom").executes(ctx -> { DrillConfig.sellPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Sell Height → Bottom"); })))
                        .then(literal("position")
                            .then(literal("top")   .executes(ctx -> { DrillConfig.sellPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Sell Height → Top");    }))
                            .then(literal("middle").executes(ctx -> { DrillConfig.sellPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Sell Height → Middle"); }))
                            .then(literal("bottom").executes(ctx -> { DrillConfig.sellPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Sell Height → Bottom"); })))
                        .then(literal("positionwidth")
                            .then(literal("left")  .executes(ctx -> { DrillConfig.sellHorizPosition = DrillConfig.HorizPosition.LEFT;   return ok(ctx.getSource(), "Sell Width → Left");   }))
                            .then(literal("middle").executes(ctx -> { DrillConfig.sellHorizPosition = DrillConfig.HorizPosition.MIDDLE; return ok(ctx.getSource(), "Sell Width → Middle"); }))
                            .then(literal("right") .executes(ctx -> { DrillConfig.sellHorizPosition = DrillConfig.HorizPosition.RIGHT;  return ok(ctx.getSource(), "Sell Width → Right");  })))
                        .then(literal("size")
                            .then(literal("xsmall").executes(ctx -> { DrillConfig.sellSize = DrillConfig.PriceSize.XSMALL; return ok(ctx.getSource(), "Sell Size → XSmall"); }))
                            .then(literal("small") .executes(ctx -> { DrillConfig.sellSize = DrillConfig.PriceSize.SMALL;  return ok(ctx.getSource(), "Sell Size → Small");  }))
                            .then(literal("medium").executes(ctx -> { DrillConfig.sellSize = DrillConfig.PriceSize.MEDIUM; return ok(ctx.getSource(), "Sell Size → Medium"); }))
                            .then(literal("large") .executes(ctx -> { DrillConfig.sellSize = DrillConfig.PriceSize.LARGE;  return ok(ctx.getSource(), "Sell Size → Large");  })))
                        .then(literal("bold")
                            .then(literal("on") .executes(ctx -> { DrillConfig.sellBold = true;  return ok(ctx.getSource(), "Sell Bold → On");  }))
                            .then(literal("off").executes(ctx -> { DrillConfig.sellBold = false; return ok(ctx.getSource(), "Sell Bold → Off"); })))
                        .then(literal("background")
                            .then(literal("on") .executes(ctx -> { DrillConfig.showSellBackground = true;  return ok(ctx.getSource(), "Sell BG → On");  }))
                            .then(literal("off").executes(ctx -> { DrillConfig.showSellBackground = false; return ok(ctx.getSource(), "Sell BG → Off"); })))
                        .then(literal("color")
                            .then(argument("color", StringArgumentType.word())
                                .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                                .executes(ctx -> setDisplayColor(ctx.getSource(), getString(ctx, "color"), "sell"))))))

                // ── /infoinslot shards ────────────────────────────────────────
                .then(literal("shards")
                    .executes(ctx -> showCurrencyStatus(ctx.getSource(), "Shards"))
                    .then(literal("show")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showShards = true;  return ok(ctx.getSource(), "Shards → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showShards = false; return ok(ctx.getSource(), "Shards → Off"); })))
                    .then(literal("positionheight")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.shardsPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Shards Height → Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.shardsPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Shards Height → Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.shardsPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Shards Height → Bottom"); })))
                    .then(literal("position")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.shardsPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Shards Height → Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.shardsPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Shards Height → Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.shardsPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Shards Height → Bottom"); })))
                    .then(literal("positionwidth")
                        .then(literal("left")  .executes(ctx -> { DrillConfig.shardsHorizPosition = DrillConfig.HorizPosition.LEFT;   return ok(ctx.getSource(), "Shards Width → Left");   }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.shardsHorizPosition = DrillConfig.HorizPosition.MIDDLE; return ok(ctx.getSource(), "Shards Width → Middle"); }))
                        .then(literal("right") .executes(ctx -> { DrillConfig.shardsHorizPosition = DrillConfig.HorizPosition.RIGHT;  return ok(ctx.getSource(), "Shards Width → Right");  })))
                    .then(literal("size")
                        .then(literal("xsmall").executes(ctx -> { DrillConfig.shardsSize = DrillConfig.PriceSize.XSMALL; return ok(ctx.getSource(), "Shards Size → XSmall"); }))
                        .then(literal("small") .executes(ctx -> { DrillConfig.shardsSize = DrillConfig.PriceSize.SMALL;  return ok(ctx.getSource(), "Shards Size → Small");  }))
                        .then(literal("medium").executes(ctx -> { DrillConfig.shardsSize = DrillConfig.PriceSize.MEDIUM; return ok(ctx.getSource(), "Shards Size → Medium"); }))
                        .then(literal("large") .executes(ctx -> { DrillConfig.shardsSize = DrillConfig.PriceSize.LARGE;  return ok(ctx.getSource(), "Shards Size → Large");  })))
                    .then(literal("symbol")
                        .then(literal("left") .executes(ctx -> { DrillConfig.shardsSymbolSide = DrillConfig.SymbolSide.LEFT;  return ok(ctx.getSource(), "Shards Symbol → Left");  }))
                        .then(literal("right").executes(ctx -> { DrillConfig.shardsSymbolSide = DrillConfig.SymbolSide.RIGHT; return ok(ctx.getSource(), "Shards Symbol → Right"); }))
                        .then(literal("off")  .executes(ctx -> { DrillConfig.shardsSymbolSide = DrillConfig.SymbolSide.OFF;   return ok(ctx.getSource(), "Shards Symbol → Off");   })))
                    .then(literal("color")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.shardsDigitColor = v; return ok(ctx.getSource(), "Shards Digit Color → " + getString(ctx,"color")); })))
                    .then(literal("symbolcolor")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.shardsSymbolColor = v; return ok(ctx.getSource(), "Shards Symbol Color → " + getString(ctx,"color")); })))
                    .then(literal("bold")
                        .then(literal("on") .executes(ctx -> { DrillConfig.shardsBold = true;  return ok(ctx.getSource(), "Shards Bold → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.shardsBold = false; return ok(ctx.getSource(), "Shards Bold → Off"); })))
                    .then(literal("background")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showShardsBackground = true;  return ok(ctx.getSource(), "Shards BG → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showShardsBackground = false; return ok(ctx.getSource(), "Shards BG → Off"); })))
                    .then(literal("backgroundcolor")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.shardsBgColor = v; return ok(ctx.getSource(), "Shards BG Color → " + getString(ctx,"color")); }))))

                // ── /infoinslot coins ─────────────────────────────────────────
                .then(literal("coins")
                    .executes(ctx -> showCurrencyStatus(ctx.getSource(), "Coins"))
                    .then(literal("show")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showCoins = true;  return ok(ctx.getSource(), "Coins → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showCoins = false; return ok(ctx.getSource(), "Coins → Off"); })))
                    .then(literal("positionheight")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.coinsPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Coins Height → Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.coinsPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Coins Height → Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.coinsPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Coins Height → Bottom"); })))
                    .then(literal("position")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.coinsPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Coins Height → Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.coinsPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Coins Height → Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.coinsPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Coins Height → Bottom"); })))
                    .then(literal("positionwidth")
                        .then(literal("left")  .executes(ctx -> { DrillConfig.coinsHorizPosition = DrillConfig.HorizPosition.LEFT;   return ok(ctx.getSource(), "Coins Width → Left");   }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.coinsHorizPosition = DrillConfig.HorizPosition.MIDDLE; return ok(ctx.getSource(), "Coins Width → Middle"); }))
                        .then(literal("right") .executes(ctx -> { DrillConfig.coinsHorizPosition = DrillConfig.HorizPosition.RIGHT;  return ok(ctx.getSource(), "Coins Width → Right");  })))
                    .then(literal("size")
                        .then(literal("xsmall").executes(ctx -> { DrillConfig.coinsSize = DrillConfig.PriceSize.XSMALL; return ok(ctx.getSource(), "Coins Size → XSmall"); }))
                        .then(literal("small") .executes(ctx -> { DrillConfig.coinsSize = DrillConfig.PriceSize.SMALL;  return ok(ctx.getSource(), "Coins Size → Small");  }))
                        .then(literal("medium").executes(ctx -> { DrillConfig.coinsSize = DrillConfig.PriceSize.MEDIUM; return ok(ctx.getSource(), "Coins Size → Medium"); }))
                        .then(literal("large") .executes(ctx -> { DrillConfig.coinsSize = DrillConfig.PriceSize.LARGE;  return ok(ctx.getSource(), "Coins Size → Large");  })))
                    .then(literal("symbol")
                        .then(literal("left") .executes(ctx -> { DrillConfig.coinsSymbolSide = DrillConfig.SymbolSide.LEFT;  return ok(ctx.getSource(), "Coins Symbol → Left");  }))
                        .then(literal("right").executes(ctx -> { DrillConfig.coinsSymbolSide = DrillConfig.SymbolSide.RIGHT; return ok(ctx.getSource(), "Coins Symbol → Right"); }))
                        .then(literal("off")  .executes(ctx -> { DrillConfig.coinsSymbolSide = DrillConfig.SymbolSide.OFF;   return ok(ctx.getSource(), "Coins Symbol → Off");   })))
                    .then(literal("color")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.coinsDigitColor = v; return ok(ctx.getSource(), "Coins Digit Color → " + getString(ctx,"color")); })))
                    .then(literal("symbolcolor")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.coinsSymbolColor = v; return ok(ctx.getSource(), "Coins Symbol Color → " + getString(ctx,"color")); })))
                    .then(literal("bold")
                        .then(literal("on") .executes(ctx -> { DrillConfig.coinsBold = true;  return ok(ctx.getSource(), "Coins Bold → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.coinsBold = false; return ok(ctx.getSource(), "Coins Bold → Off"); })))
                    .then(literal("background")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showCoinsBackground = true;  return ok(ctx.getSource(), "Coins BG → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showCoinsBackground = false; return ok(ctx.getSource(), "Coins BG → Off"); })))
                    .then(literal("backgroundcolor")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.coinsBgColor = v; return ok(ctx.getSource(), "Coins BG Color → " + getString(ctx,"color")); }))))

                // ── /infoinslot rubies ────────────────────────────────────────
                .then(literal("rubies")
                    .executes(ctx -> showCurrencyStatus(ctx.getSource(), "Rubies"))
                    .then(literal("show")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showRubies = true;  return ok(ctx.getSource(), "Rubies → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showRubies = false; return ok(ctx.getSource(), "Rubies → Off"); })))
                    .then(literal("positionheight")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.rubiesPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Rubies Height → Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.rubiesPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Rubies Height → Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.rubiesPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Rubies Height → Bottom"); })))
                    .then(literal("position")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.rubiesPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Rubies Height → Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.rubiesPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Rubies Height → Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.rubiesPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Rubies Height → Bottom"); })))
                    .then(literal("positionwidth")
                        .then(literal("left")  .executes(ctx -> { DrillConfig.rubiesHorizPosition = DrillConfig.HorizPosition.LEFT;   return ok(ctx.getSource(), "Rubies Width → Left");   }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.rubiesHorizPosition = DrillConfig.HorizPosition.MIDDLE; return ok(ctx.getSource(), "Rubies Width → Middle"); }))
                        .then(literal("right") .executes(ctx -> { DrillConfig.rubiesHorizPosition = DrillConfig.HorizPosition.RIGHT;  return ok(ctx.getSource(), "Rubies Width → Right");  })))
                    .then(literal("size")
                        .then(literal("xsmall").executes(ctx -> { DrillConfig.rubiesSize = DrillConfig.PriceSize.XSMALL; return ok(ctx.getSource(), "Rubies Size → XSmall"); }))
                        .then(literal("small") .executes(ctx -> { DrillConfig.rubiesSize = DrillConfig.PriceSize.SMALL;  return ok(ctx.getSource(), "Rubies Size → Small");  }))
                        .then(literal("medium").executes(ctx -> { DrillConfig.rubiesSize = DrillConfig.PriceSize.MEDIUM; return ok(ctx.getSource(), "Rubies Size → Medium"); }))
                        .then(literal("large") .executes(ctx -> { DrillConfig.rubiesSize = DrillConfig.PriceSize.LARGE;  return ok(ctx.getSource(), "Rubies Size → Large");  })))
                    .then(literal("symbol")
                        .then(literal("left") .executes(ctx -> { DrillConfig.rubiesSymbolSide = DrillConfig.SymbolSide.LEFT;  return ok(ctx.getSource(), "Rubies Symbol → Left");  }))
                        .then(literal("right").executes(ctx -> { DrillConfig.rubiesSymbolSide = DrillConfig.SymbolSide.RIGHT; return ok(ctx.getSource(), "Rubies Symbol → Right"); }))
                        .then(literal("off")  .executes(ctx -> { DrillConfig.rubiesSymbolSide = DrillConfig.SymbolSide.OFF;   return ok(ctx.getSource(), "Rubies Symbol → Off");   })))
                    .then(literal("color")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.rubiesDigitColor = v; return ok(ctx.getSource(), "Rubies Digit Color → " + getString(ctx,"color")); })))
                    .then(literal("symbolcolor")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.rubiesSymbolColor = v; return ok(ctx.getSource(), "Rubies Symbol Color → " + getString(ctx,"color")); })))
                    .then(literal("bold")
                        .then(literal("on") .executes(ctx -> { DrillConfig.rubiesBold = true;  return ok(ctx.getSource(), "Rubies Bold → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.rubiesBold = false; return ok(ctx.getSource(), "Rubies Bold → Off"); })))
                    .then(literal("background")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showRubiesBackground = true;  return ok(ctx.getSource(), "Rubies BG → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showRubiesBackground = false; return ok(ctx.getSource(), "Rubies BG → Off"); })))
                    .then(literal("backgroundcolor")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.rubiesBgColor = v; return ok(ctx.getSource(), "Rubies BG Color → " + getString(ctx,"color")); }))))

                // ── /infoinslot gems ──────────────────────────────────────────
                .then(literal("gems")
                    .executes(ctx -> showCurrencyStatus(ctx.getSource(), "Gems"))
                    .then(literal("show")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showGems = true;  return ok(ctx.getSource(), "Gems → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showGems = false; return ok(ctx.getSource(), "Gems → Off"); })))
                    .then(literal("positionheight")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.gemsPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Gems Height → Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.gemsPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Gems Height → Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.gemsPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Gems Height → Bottom"); })))
                    .then(literal("position")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.gemsPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Gems Height → Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.gemsPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Gems Height → Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.gemsPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Gems Height → Bottom"); })))
                    .then(literal("positionwidth")
                        .then(literal("left")  .executes(ctx -> { DrillConfig.gemsHorizPosition = DrillConfig.HorizPosition.LEFT;   return ok(ctx.getSource(), "Gems Width → Left");   }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.gemsHorizPosition = DrillConfig.HorizPosition.MIDDLE; return ok(ctx.getSource(), "Gems Width → Middle"); }))
                        .then(literal("right") .executes(ctx -> { DrillConfig.gemsHorizPosition = DrillConfig.HorizPosition.RIGHT;  return ok(ctx.getSource(), "Gems Width → Right");  })))
                    .then(literal("size")
                        .then(literal("xsmall").executes(ctx -> { DrillConfig.gemsSize = DrillConfig.PriceSize.XSMALL; return ok(ctx.getSource(), "Gems Size → XSmall"); }))
                        .then(literal("small") .executes(ctx -> { DrillConfig.gemsSize = DrillConfig.PriceSize.SMALL;  return ok(ctx.getSource(), "Gems Size → Small");  }))
                        .then(literal("medium").executes(ctx -> { DrillConfig.gemsSize = DrillConfig.PriceSize.MEDIUM; return ok(ctx.getSource(), "Gems Size → Medium"); }))
                        .then(literal("large") .executes(ctx -> { DrillConfig.gemsSize = DrillConfig.PriceSize.LARGE;  return ok(ctx.getSource(), "Gems Size → Large");  })))
                    .then(literal("symbol")
                        .then(literal("left") .executes(ctx -> { DrillConfig.gemsSymbolSide = DrillConfig.SymbolSide.LEFT;  return ok(ctx.getSource(), "Gems Symbol → Left");  }))
                        .then(literal("right").executes(ctx -> { DrillConfig.gemsSymbolSide = DrillConfig.SymbolSide.RIGHT; return ok(ctx.getSource(), "Gems Symbol → Right"); }))
                        .then(literal("off")  .executes(ctx -> { DrillConfig.gemsSymbolSide = DrillConfig.SymbolSide.OFF;   return ok(ctx.getSource(), "Gems Symbol → Off");   })))
                    .then(literal("color")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.gemsDigitColor = v; return ok(ctx.getSource(), "Gems Digit Color → " + getString(ctx,"color")); })))
                    .then(literal("symbolcolor")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.gemsSymbolColor = v; return ok(ctx.getSource(), "Gems Symbol Color → " + getString(ctx,"color")); })))
                    .then(literal("bold")
                        .then(literal("on") .executes(ctx -> { DrillConfig.gemsBold = true;  return ok(ctx.getSource(), "Gems Bold → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.gemsBold = false; return ok(ctx.getSource(), "Gems Bold → Off"); })))
                    .then(literal("background")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showGemsBackground = true;  return ok(ctx.getSource(), "Gems BG → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showGemsBackground = false; return ok(ctx.getSource(), "Gems BG → Off"); })))
                    .then(literal("backgroundcolor")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.gemsBgColor = v; return ok(ctx.getSource(), "Gems BG Color → " + getString(ctx,"color")); }))))

                // ── /infoinslot keys ──────────────────────────────────────────
                .then(literal("keys")
                    .executes(ctx -> showKeysStatus(ctx.getSource()))
                    .then(literal("show")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showKeyCount = true;  return ok(ctx.getSource(), "Key Count → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showKeyCount = false; return ok(ctx.getSource(), "Key Count → Off"); })))
                    .then(literal("label")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showKeyLabel = true;  return ok(ctx.getSource(), "Key Label → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showKeyLabel = false; return ok(ctx.getSource(), "Key Label → Off"); })))
                    .then(literal("positionheight")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.keyPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Key Height → Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.keyPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Key Height → Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.keyPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Key Height → Bottom"); })))
                    .then(literal("position")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.keyPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Key Height → Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.keyPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Key Height → Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.keyPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Key Height → Bottom"); })))
                    .then(literal("positionwidth")
                        .then(literal("left")  .executes(ctx -> { DrillConfig.keyHorizPosition = DrillConfig.HorizPosition.LEFT;   return ok(ctx.getSource(), "Key Width → Left");   }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.keyHorizPosition = DrillConfig.HorizPosition.MIDDLE; return ok(ctx.getSource(), "Key Width → Middle"); }))
                        .then(literal("right") .executes(ctx -> { DrillConfig.keyHorizPosition = DrillConfig.HorizPosition.RIGHT;  return ok(ctx.getSource(), "Key Width → Right");  })))
                    .then(literal("size")
                        .then(literal("xsmall").executes(ctx -> { DrillConfig.keySize = DrillConfig.PriceSize.XSMALL; return ok(ctx.getSource(), "Key Size → XSmall"); }))
                        .then(literal("small") .executes(ctx -> { DrillConfig.keySize = DrillConfig.PriceSize.SMALL;  return ok(ctx.getSource(), "Key Size → Small");  }))
                        .then(literal("medium").executes(ctx -> { DrillConfig.keySize = DrillConfig.PriceSize.MEDIUM; return ok(ctx.getSource(), "Key Size → Medium"); }))
                        .then(literal("large") .executes(ctx -> { DrillConfig.keySize = DrillConfig.PriceSize.LARGE;  return ok(ctx.getSource(), "Key Size → Large");  })))
                    .then(literal("color")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.keyCountColor = v; return ok(ctx.getSource(), "Key Count Color → " + getString(ctx,"color")); })))
                    .then(literal("bold")
                        .then(literal("on") .executes(ctx -> { DrillConfig.keyCountBold = true;  return ok(ctx.getSource(), "Key Count Bold → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.keyCountBold = false; return ok(ctx.getSource(), "Key Count Bold → Off"); })))
                    .then(literal("background")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showKeyBackground = true;  return ok(ctx.getSource(), "Key BG → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showKeyBackground = false; return ok(ctx.getSource(), "Key BG → Off"); })))
                    .then(literal("backgroundcolor")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.keyBgColor = v; return ok(ctx.getSource(), "Key BG Color → " + getString(ctx,"color")); })))

                    // ── Key Cost sub-commands ─────────────────────────────────
                    .then(literal("keycost")
                        .executes(ctx -> showKeyCostStatus(ctx.getSource()))
                        .then(literal("show")
                            .then(literal("on") .executes(ctx -> { DrillConfig.showKeyCost = true;  return ok(ctx.getSource(), "Key Cost → On");  }))
                            .then(literal("off").executes(ctx -> { DrillConfig.showKeyCost = false; return ok(ctx.getSource(), "Key Cost → Off"); })))
                        .then(literal("positionheight")
                            .then(literal("top")   .executes(ctx -> { DrillConfig.keyCostVertPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Key Cost Height → Top");    }))
                            .then(literal("middle").executes(ctx -> { DrillConfig.keyCostVertPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Key Cost Height → Middle"); }))
                            .then(literal("bottom").executes(ctx -> { DrillConfig.keyCostVertPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Key Cost Height → Bottom"); })))
                        .then(literal("vertposition")
                            .then(literal("top")   .executes(ctx -> { DrillConfig.keyCostVertPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Key Cost Height → Top");    }))
                            .then(literal("middle").executes(ctx -> { DrillConfig.keyCostVertPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Key Cost Height → Middle"); }))
                            .then(literal("bottom").executes(ctx -> { DrillConfig.keyCostVertPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Key Cost Height → Bottom"); })))
                        .then(literal("positionwidth")
                            .then(literal("left")  .executes(ctx -> { DrillConfig.keyCostHorizPosition = DrillConfig.HorizPosition.LEFT;   return ok(ctx.getSource(), "Key Cost Width → Left");   }))
                            .then(literal("middle").executes(ctx -> { DrillConfig.keyCostHorizPosition = DrillConfig.HorizPosition.MIDDLE; return ok(ctx.getSource(), "Key Cost Width → Middle"); }))
                            .then(literal("right") .executes(ctx -> { DrillConfig.keyCostHorizPosition = DrillConfig.HorizPosition.RIGHT;  return ok(ctx.getSource(), "Key Cost Width → Right");  })))
                        .then(literal("horizposition")
                            .then(literal("left")  .executes(ctx -> { DrillConfig.keyCostHorizPosition = DrillConfig.HorizPosition.LEFT;   return ok(ctx.getSource(), "Key Cost Width → Left");   }))
                            .then(literal("middle").executes(ctx -> { DrillConfig.keyCostHorizPosition = DrillConfig.HorizPosition.MIDDLE; return ok(ctx.getSource(), "Key Cost Width → Middle"); }))
                            .then(literal("right") .executes(ctx -> { DrillConfig.keyCostHorizPosition = DrillConfig.HorizPosition.RIGHT;  return ok(ctx.getSource(), "Key Cost Width → Right");  })))
                        .then(literal("size")
                            .then(literal("xsmall").executes(ctx -> { DrillConfig.keyCostSize = DrillConfig.PriceSize.XSMALL; return ok(ctx.getSource(), "Key Cost Size → XSmall"); }))
                            .then(literal("small") .executes(ctx -> { DrillConfig.keyCostSize = DrillConfig.PriceSize.SMALL;  return ok(ctx.getSource(), "Key Cost Size → Small");  }))
                            .then(literal("medium").executes(ctx -> { DrillConfig.keyCostSize = DrillConfig.PriceSize.MEDIUM; return ok(ctx.getSource(), "Key Cost Size → Medium"); }))
                            .then(literal("large") .executes(ctx -> { DrillConfig.keyCostSize = DrillConfig.PriceSize.LARGE;  return ok(ctx.getSource(), "Key Cost Size → Large");  })))
                        .then(literal("color")
                            .then(argument("color", StringArgumentType.word())
                                .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                                .executes(ctx -> setDisplayColor(ctx.getSource(), getString(ctx, "color"), "keycost"))))
                        .then(literal("bold")
                            .then(literal("on") .executes(ctx -> { DrillConfig.keyCostBold = true;  return ok(ctx.getSource(), "Key Cost Bold → On");  }))
                            .then(literal("off").executes(ctx -> { DrillConfig.keyCostBold = false; return ok(ctx.getSource(), "Key Cost Bold → Off"); })))
                        .then(literal("background")
                            .then(literal("on") .executes(ctx -> { DrillConfig.showKeyCostBackground = true;  return ok(ctx.getSource(), "Key Cost BG → On");  }))
                            .then(literal("off").executes(ctx -> { DrillConfig.showKeyCostBackground = false; return ok(ctx.getSource(), "Key Cost BG → Off"); })))
                        .then(literal("backgroundcolor")
                            .then(argument("color", StringArgumentType.word())
                                .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                                .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.keyCostBgColor = v; return ok(ctx.getSource(), "Key Cost BG Color → " + getString(ctx,"color")); })))))

                // ── /infoinslot chance ────────────────────────────────────────
                .then(literal("chance")
                    .executes(ctx -> showChanceStatus(ctx.getSource()))

                    .then(literal("show")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showChance = true;  return ok(ctx.getSource(), "Chance → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showChance = false; return ok(ctx.getSource(), "Chance → Off"); })))

                    .then(literal("symbol")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showChanceSymbol = true;  return ok(ctx.getSource(), "Chance Symbol → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showChanceSymbol = false; return ok(ctx.getSource(), "Chance Symbol → Off"); })))

                    .then(literal("positionheight")
                        .then(literal("top")   .executes(ctx -> { DrillConfig.chanceVertPosition = DrillConfig.TimerPosition.TOP;    return ok(ctx.getSource(), "Chance Height → Top");    }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.chanceVertPosition = DrillConfig.TimerPosition.MIDDLE; return ok(ctx.getSource(), "Chance Height → Middle"); }))
                        .then(literal("bottom").executes(ctx -> { DrillConfig.chanceVertPosition = DrillConfig.TimerPosition.BOTTOM; return ok(ctx.getSource(), "Chance Height → Bottom"); })))

                    .then(literal("positionwidth")
                        .then(literal("left")  .executes(ctx -> { DrillConfig.chanceHorizPosition = DrillConfig.HorizPosition.LEFT;   return ok(ctx.getSource(), "Chance Width → Left");   }))
                        .then(literal("middle").executes(ctx -> { DrillConfig.chanceHorizPosition = DrillConfig.HorizPosition.MIDDLE; return ok(ctx.getSource(), "Chance Width → Middle"); }))
                        .then(literal("right") .executes(ctx -> { DrillConfig.chanceHorizPosition = DrillConfig.HorizPosition.RIGHT;  return ok(ctx.getSource(), "Chance Width → Right");  })))

                    .then(literal("size")
                        .then(literal("xsmall").executes(ctx -> { DrillConfig.chanceSize = DrillConfig.PriceSize.XSMALL; return ok(ctx.getSource(), "Chance Size → XSmall"); }))
                        .then(literal("small") .executes(ctx -> { DrillConfig.chanceSize = DrillConfig.PriceSize.SMALL;  return ok(ctx.getSource(), "Chance Size → Small");  }))
                        .then(literal("medium").executes(ctx -> { DrillConfig.chanceSize = DrillConfig.PriceSize.MEDIUM; return ok(ctx.getSource(), "Chance Size → Medium"); }))
                        .then(literal("large") .executes(ctx -> { DrillConfig.chanceSize = DrillConfig.PriceSize.LARGE;  return ok(ctx.getSource(), "Chance Size → Large");  })))

                    .then(literal("color")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.chanceColor = v; return ok(ctx.getSource(), "Chance Color → " + getString(ctx,"color")); })))

                    .then(literal("bold")
                        .then(literal("on") .executes(ctx -> { DrillConfig.chanceBold = true;  return ok(ctx.getSource(), "Chance Bold → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.chanceBold = false; return ok(ctx.getSource(), "Chance Bold → Off"); })))

                    .then(literal("symbolcolor")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.chanceSymbolColor = v; return ok(ctx.getSource(), "Chance Symbol Color → " + getString(ctx,"color")); })))

                    .then(literal("background")
                        .then(literal("on") .executes(ctx -> { DrillConfig.showChanceBackground = true;  return ok(ctx.getSource(), "Chance BG → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.showChanceBackground = false; return ok(ctx.getSource(), "Chance BG → Off"); })))

                    .then(literal("backgroundcolor")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.chanceBgColor = v; return ok(ctx.getSource(), "Chance BG Color → " + getString(ctx,"color")); })))

                    .then(literal("fractionmode")
                        .then(literal("on") .executes(ctx -> { DrillConfig.chanceFractionMode = true;  return ok(ctx.getSource(), "Fraction Mode → On");  }))
                        .then(literal("off").executes(ctx -> { DrillConfig.chanceFractionMode = false; return ok(ctx.getSource(), "Fraction Mode → Off"); })))

                    .then(literal("numeratorcolor")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.chanceNumeratorColor = v; return ok(ctx.getSource(), "Numerator Color → " + getString(ctx,"color")); })))

                    .then(literal("denominatorcolor")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.chanceDenominatorColor = v; return ok(ctx.getSource(), "Denominator Color → " + getString(ctx,"color")); })))

                    .then(literal("slashcolor")
                        .then(argument("color", StringArgumentType.word())
                            .suggests((ctx, b) -> { COLOR_NAMES.keySet().forEach(b::suggest); return b.buildFuture(); })
                            .executes(ctx -> { Integer v = parseColor(ctx.getSource(), getString(ctx,"color")); if (v==null) return 0; DrillConfig.chanceSlashColor = v; return ok(ctx.getSource(), "Slash Color → " + getString(ctx,"color")); }))))
        );
    }

    // ── Status displays ───────────────────────────────────────────────────────

    private static int showStatus(FabricClientCommandSource src) {
        showMainStatus(src); showTimerStatus(src);
        showOrderPriceStatus(src); showOrderCountStatus(src);
        showBuySellStatus(src);
        showCurrencyStatus(src, "Shards"); showCurrencyStatus(src, "Coins");
        showCurrencyStatus(src, "Rubies"); showCurrencyStatus(src, "Gems");
        showKeysStatus(src); showKeyCostStatus(src); showChanceStatus(src);
        return 1;
    }

    private static int showMainStatus(FabricClientCommandSource src) {
        src.sendFeedback(Text.literal("§e=== Main ==="));
        src.sendFeedback(Text.literal(
            "§7Mod: §f" + bool(DrillConfig.enabled) +
            " §7Symbol: §f" + bool(DrillConfig.showCurrencySymbol) +
            " §7All BG: §f" + bool(DrillConfig.allBackgroundsOn())));
        src.sendFeedback(Text.literal(
            "§7Container: §f" + bool(DrillConfig.showInContainer) +
            " §7Inventory: §f" + bool(DrillConfig.showInInventory) +
            " §7Hotbar: §f" + bool(DrillConfig.showInHotbar)));
        return 1;
    }

    private static int showTimerStatus(FabricClientCommandSource src) {
        src.sendFeedback(Text.literal(
            "§e=== Timer === §7Show: §f" + bool(DrillConfig.showTimer) +
            " §7Size: §f"   + cap(DrillConfig.timerSize.name()) +
            " §7H: §f"      + cap(DrillConfig.timerPosition.name()) +
            " §7W: §f"      + cap(DrillConfig.timerHorizPosition.name()) +
            " §7HoursOnly: §f" + bool(DrillConfig.hoursOnly)));
        return 1;
    }

    private static int showOrderPriceStatus(FabricClientCommandSource src) {
        src.sendFeedback(Text.literal(
            "§e=== Order/Auction Price === §7Show: §f" + bool(DrillConfig.showOrderPrice) +
            " §7Size: §f" + cap(DrillConfig.orderPriceSize.name()) +
            " §7H: §f"    + cap(DrillConfig.orderPricePosition.name()) +
            " §7W: §f"    + cap(DrillConfig.orderPriceHorizPosition.name()) +
            " §7Color: §f" + cname(DrillConfig.orderPriceColor)));
        return 1;
    }

    private static int showOrderCountStatus(FabricClientCommandSource src) {
        src.sendFeedback(Text.literal(
            "§e=== Order Count === §7Show: §f" + bool(DrillConfig.showDelivered) +
            " §7Size: §f" + cap(DrillConfig.deliveredSize.name()) +
            " §7H: §f"    + cap(DrillConfig.deliveredPosition.name()) +
            " §7W: §f"    + cap(DrillConfig.deliveredHorizPosition.name()) +
            " §7Color: §f" + cname(DrillConfig.deliveredColor)));
        return 1;
    }

    private static int showBuySellStatus(FabricClientCommandSource src) {
        src.sendFeedback(Text.literal("§e=== Buy/Sell ==="));
        src.sendFeedback(Text.literal(
            "§7Buy: §f"  + bool(DrillConfig.showBuyPrice) +
            " §7H: §f"   + cap(DrillConfig.buyPosition.name()) +
            " §7W: §f"   + cap(DrillConfig.buyHorizPosition.name()) +
            " §7Size: §f" + cap(DrillConfig.buySize.name())));
        src.sendFeedback(Text.literal(
            "§7Sell: §f" + bool(DrillConfig.showSellPrice) +
            " §7H: §f"   + cap(DrillConfig.sellPosition.name()) +
            " §7W: §f"   + cap(DrillConfig.sellHorizPosition.name()) +
            " §7Size: §f" + cap(DrillConfig.sellSize.name())));
        return 1;
    }

    private static int showCurrencyStatus(FabricClientCommandSource src, String name) {
        boolean show; DrillConfig.TimerPosition pos; DrillConfig.HorizPosition horiz;
        DrillConfig.SymbolSide sym; int digitCol; boolean bg; DrillConfig.PriceSize sz; boolean boldVal;
        switch (name) {
            case "Shards" -> { show=DrillConfig.showShards; pos=DrillConfig.shardsPosition; horiz=DrillConfig.shardsHorizPosition; sym=DrillConfig.shardsSymbolSide; digitCol=DrillConfig.shardsDigitColor; bg=DrillConfig.showShardsBackground; sz=DrillConfig.shardsSize; boldVal=DrillConfig.shardsBold; }
            case "Coins"  -> { show=DrillConfig.showCoins;  pos=DrillConfig.coinsPosition;  horiz=DrillConfig.coinsHorizPosition;  sym=DrillConfig.coinsSymbolSide;  digitCol=DrillConfig.coinsDigitColor;  bg=DrillConfig.showCoinsBackground;  sz=DrillConfig.coinsSize;  boldVal=DrillConfig.coinsBold;  }
            case "Rubies" -> { show=DrillConfig.showRubies; pos=DrillConfig.rubiesPosition; horiz=DrillConfig.rubiesHorizPosition; sym=DrillConfig.rubiesSymbolSide; digitCol=DrillConfig.rubiesDigitColor; bg=DrillConfig.showRubiesBackground; sz=DrillConfig.rubiesSize; boldVal=DrillConfig.rubiesBold; }
            default       -> { show=DrillConfig.showGems;   pos=DrillConfig.gemsPosition;   horiz=DrillConfig.gemsHorizPosition;   sym=DrillConfig.gemsSymbolSide;   digitCol=DrillConfig.gemsDigitColor;   bg=DrillConfig.showGemsBackground;   sz=DrillConfig.gemsSize;   boldVal=DrillConfig.gemsBold;   }
        }
        src.sendFeedback(Text.literal(
            "§e=== " + name + " === §7Show: §f" + bool(show) +
            " §7H: §f" + cap(pos.name()) +
            " §7W: §f" + cap(horiz.name()) +
            " §7Size: §f" + cap(sz.name()) +
            " §7Sym: §f"  + cap(sym.name()) +
            " §7Digit: §f" + (digitCol==0?"auto":cname(digitCol)) +
            " §7Bold: §f" + bool(boldVal) +
            " §7BG: §f" + bool(bg)));
        return 1;
    }

    private static int showKeysStatus(FabricClientCommandSource src) {
        src.sendFeedback(Text.literal(
            "§e=== Keys === §7Show: §f" + bool(DrillConfig.showKeyCount) +
            " §7Label: §f" + bool(DrillConfig.showKeyLabel) +
            " §7H: §f"     + cap(DrillConfig.keyPosition.name()) +
            " §7W: §f"     + cap(DrillConfig.keyHorizPosition.name()) +
            " §7Size: §f"  + cap(DrillConfig.keySize.name()) +
            " §7Color: §f" + (DrillConfig.keyCountColor==0?"auto":cname(DrillConfig.keyCountColor)) +
            " §7Bold: §f"  + bool(DrillConfig.keyCountBold) +
            " §7BG: §f"    + bool(DrillConfig.showKeyBackground)));
        return 1;
    }

    private static int showKeyCostStatus(FabricClientCommandSource src) {
        src.sendFeedback(Text.literal(
            "§e=== Key Cost === §7Show: §f" + bool(DrillConfig.showKeyCost) +
            " §7H: §f"     + cap(DrillConfig.keyCostVertPosition.name()) +
            " §7W: §f"     + cap(DrillConfig.keyCostHorizPosition.name()) +
            " §7Size: §f"  + cap(DrillConfig.keyCostSize.name()) +
            " §7Color: §f" + cname(DrillConfig.keyCostColor) +
            " §7BG: §f"    + bool(DrillConfig.showKeyCostBackground)));
        return 1;
    }

    private static int showChanceStatus(FabricClientCommandSource src) {
        src.sendFeedback(Text.literal(
            "§e=== Chance === §7Show: §f" + bool(DrillConfig.showChance) +
            " §7Symbol: §f" + bool(DrillConfig.showChanceSymbol) +
            " §7H: §f"      + cap(DrillConfig.chanceVertPosition.name()) +
            " §7W: §f"      + cap(DrillConfig.chanceHorizPosition.name()) +
            " §7Size: §f"   + cap(DrillConfig.chanceSize.name()) +
            " §7Color: §f"  + (DrillConfig.chanceColor==0?"auto":cname(DrillConfig.chanceColor)) +
            " §7Bold: §f"   + bool(DrillConfig.chanceBold) +
            " §7Frac: §f"   + bool(DrillConfig.chanceFractionMode) +
            " §7BG: §f"     + bool(DrillConfig.showChanceBackground)));
        return 1;
    }

    // ── Handlers ──────────────────────────────────────────────────────────────

    private static int setTimerBold(FabricClientCommandSource src, String tier, boolean on) {
        switch (tier.toLowerCase()) {
            case "2day"   -> DrillConfig.twoDayBold = on;
            case "day"    -> DrillConfig.dayBold    = on;
            case "hour"   -> DrillConfig.hourBold   = on;
            case "minute" -> DrillConfig.minuteBold = on;
            case "all"    -> { DrillConfig.twoDayBold = DrillConfig.dayBold = DrillConfig.hourBold = DrillConfig.minuteBold = on; }
            default -> { src.sendFeedback(Text.literal("§c[InfoInSlot] Unknown tier: " + tier)); return 0; }
        }
        return ok(src, "Timer Bold [" + tier + "] → " + (on ? "On" : "Off"));
    }

    private static int setTimerColor(FabricClientCommandSource src, String tier, String colorArg) {
        Integer val = COLOR_NAMES.get(colorArg.toLowerCase());
        if (val == null) { src.sendFeedback(Text.literal("§c[InfoInSlot] Unknown color: " + colorArg)); return 0; }
        switch (tier.toLowerCase()) {
            case "2day"   -> DrillConfig.twoDayColor = val;
            case "day"    -> DrillConfig.dayColor    = val;
            case "hour"   -> DrillConfig.hourColor   = val;
            case "minute" -> DrillConfig.minuteColor = val;
            default -> { src.sendFeedback(Text.literal("§c[InfoInSlot] Unknown tier: " + tier)); return 0; }
        }
        return ok(src, "Timer Color [" + tier + "] → " + colorArg);
    }

    private static int setDisplayColor(FabricClientCommandSource src, String colorArg, String target) {
        Integer val = COLOR_NAMES.get(colorArg.toLowerCase());
        if (val == null) { src.sendFeedback(Text.literal("§c[InfoInSlot] Unknown color: " + colorArg)); return 0; }
        switch (target) {
            case "orderprice"   -> DrillConfig.orderPriceColor = val;
            case "delivered"    -> DrillConfig.deliveredColor  = val;
            case "buy"          -> DrillConfig.buyColor        = val;
            case "sell"         -> DrillConfig.sellColor       = val;
            case "keycost"      -> DrillConfig.keyCostColor    = val;
        }
        return ok(src, cap(target) + " Color → " + colorArg);
    }

    private static Integer parseColor(FabricClientCommandSource src, String colorArg) {
        Integer val = COLOR_NAMES.get(colorArg.toLowerCase());
        if (val == null) src.sendFeedback(Text.literal("§c[InfoInSlot] Unknown color: " + colorArg + ". Use 'auto' for tooltip-detected."));
        return val;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static int ok(FabricClientCommandSource src, String msg) {
        src.sendFeedback(Text.literal("§a[InfoInSlot] " + msg));
        DrillConfig.save();
        return 1;
    }

    private static String cname(int rgb) { return COLOR_LOOKUP.getOrDefault(rgb, String.format("#%06X", rgb)); }
    private static String bool(boolean b) { return b ? "On" : "Off"; }
    private static String cap(String s) {
        if (s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
}
