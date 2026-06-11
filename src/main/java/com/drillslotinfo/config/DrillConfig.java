package com.drillslotinfo.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

import java.nio.file.Files;
import java.nio.file.Path;

public class DrillConfig {

    // XSMALL = old Small (smallest), SMALL = old Medium (default, unchanged size),
    // MEDIUM = new (fits ~5 chars), LARGE = unchanged
    public enum TimerSize     { XSMALL, SMALL, MEDIUM, LARGE }
    public enum TimerPosition { TOP, MIDDLE, BOTTOM }
    public enum PriceSize     { XSMALL, SMALL, MEDIUM, LARGE }
    public enum SymbolSide    { LEFT, RIGHT, OFF }
    public enum HorizPosition { LEFT, MIDDLE, RIGHT }

    // ── Timer Display ─────────────────────────────────────────────────────────
    public static boolean       enabled            = true;
    public static boolean       showTimer          = true;
    public static boolean       showBackground     = true;
    public static int           timerBgColor       = 0x000000;
    public static TimerSize     timerSize          = TimerSize.SMALL;
    public static TimerPosition timerPosition      = TimerPosition.MIDDLE;
    public static HorizPosition timerHorizPosition = HorizPosition.RIGHT;
    public static boolean       separateSlotColors = true;
    public static boolean       hoursOnly          = false;

    public static int  twoDayColor = 0xFF55FF;
    public static int  dayColor    = 0x55FFFF;
    public static int  hourColor   = 0xFFAA00;
    public static int  minuteColor = 0xFF5555;

    public static boolean twoDayBold = false;
    public static boolean dayBold    = false;
    public static boolean hourBold   = false;
    public static boolean minuteBold = false;

    // ── Auction Price ─────────────────────────────────────────────────────────
    public static boolean       showPrice            = true;
    public static TimerPosition pricePosition        = TimerPosition.TOP;
    public static HorizPosition priceHorizPosition   = HorizPosition.LEFT;
    public static boolean       showPriceBackground  = true;
    public static int           auctionBgColor       = 0x000000;
    public static PriceSize     priceSize            = PriceSize.SMALL;
    public static int           priceColor           = 0x55FF55;
    public static boolean       priceBold            = false;

    // ── Order Price ───────────────────────────────────────────────────────────
    public static boolean       showOrderPrice            = true;
    public static TimerPosition orderPricePosition        = TimerPosition.TOP;
    public static HorizPosition orderPriceHorizPosition   = HorizPosition.LEFT;
    public static boolean       showOrderPriceBackground  = true;
    public static int           orderPriceBgColor         = 0x000000;
    public static PriceSize     orderPriceSize            = PriceSize.SMALL;
    public static int           orderPriceColor           = 0x55FF55;
    public static boolean       orderPriceBold            = false;

    // ── Main / shared ─────────────────────────────────────────────────────────
    public static boolean showCurrencySymbol = true;

    // ── Key Counter ───────────────────────────────────────────────────────────
    public static boolean       showKeyCount       = true;
    public static boolean       showKeyLabel       = false;
    public static TimerPosition keyPosition        = TimerPosition.BOTTOM;
    public static HorizPosition keyHorizPosition   = HorizPosition.LEFT;
    public static boolean       showKeyBackground  = true;
    public static int           keyBgColor         = 0x000000;
    public static PriceSize     keySize            = PriceSize.SMALL;
    public static int           keyCountColor      = 0x55FFFF;
    public static boolean       keyCountBold       = false;

    // ── Key Cost ──────────────────────────────────────────────────────────────
    public static boolean       showKeyCost           = true;
    public static TimerPosition keyCostVertPosition   = TimerPosition.TOP;
    public static HorizPosition keyCostHorizPosition  = HorizPosition.RIGHT;
    public static int           keyCostColor          = 0xFF5555;
    public static boolean       keyCostBold           = false;
    public static boolean       showKeyCostBackground = true;
    public static int           keyCostBgColor        = 0x000000;
    public static PriceSize     keyCostSize           = PriceSize.SMALL;

    // ── Buy Price ─────────────────────────────────────────────────────────────
    public static boolean       showBuyPrice       = true;
    public static TimerPosition buyPosition        = TimerPosition.TOP;
    public static HorizPosition buyHorizPosition   = HorizPosition.LEFT;
    public static int           buyColor           = 0x55FF55;
    public static boolean       buyBold            = false;
    public static boolean       showBuyBackground  = true;
    public static int           buyBgColor         = 0x000000;
    public static PriceSize     buySize            = PriceSize.SMALL;

    // ── Sell Price ────────────────────────────────────────────────────────────
    public static boolean       showSellPrice       = true;
    public static TimerPosition sellPosition        = TimerPosition.BOTTOM;
    public static HorizPosition sellHorizPosition   = HorizPosition.LEFT;
    public static int           sellColor           = 0xFF5555;
    public static boolean       sellBold            = false;
    public static boolean       showSellBackground  = true;
    public static int           sellBgColor         = 0x000000;
    public static PriceSize     sellSize            = PriceSize.SMALL;

    // ── Order Count (delivered) ───────────────────────────────────────────────
    public static boolean       showDelivered            = true;
    public static TimerPosition deliveredPosition        = TimerPosition.BOTTOM;
    public static HorizPosition deliveredHorizPosition   = HorizPosition.LEFT;
    public static boolean       showDeliveredBackground  = true;
    public static int           deliveredBgColor         = 0x000000;
    public static PriceSize     deliveredSize            = PriceSize.SMALL;
    public static int           deliveredColor           = 0xFFFFFF;
    public static boolean       deliveredBold            = false;

    // ── Shards ────────────────────────────────────────────────────────────────
    public static boolean       showShards            = true;
    public static TimerPosition shardsPosition        = TimerPosition.TOP;
    public static HorizPosition shardsHorizPosition   = HorizPosition.LEFT;
    public static SymbolSide    shardsSymbolSide      = SymbolSide.OFF;
    public static int           shardsDigitColor      = 0;
    public static int           shardsSymbolColor     = 0xFF55FF;
    public static boolean       showShardsBackground  = true;
    public static int           shardsBgColor         = 0x000000;
    public static PriceSize     shardsSize            = PriceSize.SMALL;
    public static boolean       shardsBold            = false;

    // ── Coins ─────────────────────────────────────────────────────────────────
    public static boolean       showCoins            = true;
    public static TimerPosition coinsPosition        = TimerPosition.TOP;
    public static HorizPosition coinsHorizPosition   = HorizPosition.LEFT;
    public static SymbolSide    coinsSymbolSide      = SymbolSide.OFF;
    public static int           coinsDigitColor      = 0;
    public static int           coinsSymbolColor     = 0xFFAA00;
    public static boolean       showCoinsBackground  = true;
    public static int           coinsBgColor         = 0x000000;
    public static PriceSize     coinsSize            = PriceSize.SMALL;
    public static boolean       coinsBold            = false;

    // ── Rubies ────────────────────────────────────────────────────────────────
    public static boolean       showRubies            = false;
    public static TimerPosition rubiesPosition        = TimerPosition.TOP;
    public static HorizPosition rubiesHorizPosition   = HorizPosition.LEFT;
    public static SymbolSide    rubiesSymbolSide      = SymbolSide.RIGHT;
    public static int           rubiesDigitColor      = 0;
    public static int           rubiesSymbolColor     = 0xFF55FF;
    public static boolean       showRubiesBackground  = true;
    public static int           rubiesBgColor         = 0x000000;
    public static PriceSize     rubiesSize            = PriceSize.SMALL;
    public static boolean       rubiesBold            = false;

    // ── Gems ──────────────────────────────────────────────────────────────────
    public static boolean       showGems            = true;
    public static TimerPosition gemsPosition        = TimerPosition.TOP;
    public static HorizPosition gemsHorizPosition   = HorizPosition.LEFT;
    public static SymbolSide    gemsSymbolSide      = SymbolSide.OFF;
    public static int           gemsDigitColor      = 0;
    public static int           gemsSymbolColor     = 0x00AA00;
    public static boolean       showGemsBackground  = true;
    public static int           gemsBgColor         = 0x000000;
    public static PriceSize     gemsSize            = PriceSize.SMALL;
    public static boolean       gemsBold            = false;

    // ── Chance ────────────────────────────────────────────────────────────────
    public static boolean       showChance              = true;
    public static int           chanceColor             = 0xFFFF55;
    public static boolean       chanceBold              = false;
    public static boolean       showChanceBackground    = false;
    public static int           chanceBgColor           = 0x000000;
    public static PriceSize     chanceSize              = PriceSize.MEDIUM;
    public static TimerPosition chanceVertPosition      = TimerPosition.BOTTOM;
    public static HorizPosition chanceHorizPosition     = HorizPosition.RIGHT;
    public static boolean       showChanceSymbol        = true;
    public static int           chanceSymbolColor       = 0;
    public static boolean       chanceFractionMode      = false;
    public static int           chanceNumeratorColor    = 0;
    public static int           chanceDenominatorColor  = 0;
    public static int           chanceSlashColor        = 0;

    // ── Display Context ───────────────────────────────────────────────────────
    public static boolean showInContainer = true;
    public static boolean showInInventory = true;
    public static boolean showInHotbar    = true;

    // ── Pending overrides (applied in save() after all GUI save-consumers fire) ─
    public static Boolean   allBgPending      = null;
    public static Integer   allBgColorPending = null;
    public static PriceSize allSizePending    = null;

    // ── Persistence ───────────────────────────────────────────────────────────

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("infoinslot.json");

    public static void init() { load(); }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) return;
        try {
            ConfigData d = GSON.fromJson(Files.readString(CONFIG_PATH), ConfigData.class);
            if (d == null) return;
            boolean migrate = (d.configVersion == 0);
            showCurrencySymbol = d.showCurrencySymbol;
            // Key Counter
            showKeyCount      = d.showKeyCount;
            showKeyLabel      = d.showKeyLabel;
            keyPosition       = safeEnum(TimerPosition.class, d.keyPosition,      keyPosition);
            keyHorizPosition  = safeEnum(HorizPosition.class, d.keyHorizPosition, keyHorizPosition);
            showKeyBackground = d.showKeyBackground;
            keyBgColor        = d.keyBgColor;
            keySize           = safeEnum(PriceSize.class, migSz(migrate, d.keySize), keySize);
            keyCountColor     = d.keyCountColor;
            keyCountBold      = d.keyCountBold;
            // Key Cost
            showKeyCost           = d.showKeyCost;
            keyCostVertPosition   = safeEnum(TimerPosition.class, d.keyCostVertPosition,  keyCostVertPosition);
            keyCostHorizPosition  = safeEnum(HorizPosition.class, d.keyCostHorizPosition, keyCostHorizPosition);
            if (d.keyCostColor != 0) keyCostColor = d.keyCostColor;
            keyCostBold           = d.keyCostBold;
            showKeyCostBackground = d.showKeyCostBackground;
            keyCostBgColor        = d.keyCostBgColor;
            keyCostSize           = safeEnum(PriceSize.class, migSz(migrate, d.keyCostSize), keyCostSize);
            // Buy Price
            showBuyPrice      = d.showBuyPrice;
            buyPosition       = safeEnum(TimerPosition.class, d.buyPosition,      buyPosition);
            buyHorizPosition  = safeEnum(HorizPosition.class, d.buyHorizPosition, buyHorizPosition);
            if (d.buyColor != 0) buyColor = d.buyColor;
            buyBold           = d.buyBold;
            showBuyBackground = d.showBuyBackground;
            buyBgColor        = d.buyBgColor;
            buySize           = safeEnum(PriceSize.class, migSz(migrate, d.buySize), buySize);
            // Sell Price
            showSellPrice      = d.showSellPrice;
            sellPosition       = safeEnum(TimerPosition.class, d.sellPosition,      sellPosition);
            sellHorizPosition  = safeEnum(HorizPosition.class, d.sellHorizPosition, sellHorizPosition);
            if (d.sellColor != 0) sellColor = d.sellColor;
            sellBold           = d.sellBold;
            showSellBackground = d.showSellBackground;
            sellBgColor        = d.sellBgColor;
            sellSize           = safeEnum(PriceSize.class, migSz(migrate, d.sellSize), sellSize);
            // Timer
            enabled            = d.enabled;
            showTimer          = d.showTimer;
            showBackground     = d.showBackground;
            timerBgColor       = d.timerBgColor;
            timerSize          = safeEnum(TimerSize.class, migSz(migrate, d.timerSize), timerSize);
            timerPosition      = safeEnum(TimerPosition.class, d.timerPosition,      timerPosition);
            timerHorizPosition = safeEnum(HorizPosition.class, d.timerHorizPosition, timerHorizPosition);
            separateSlotColors = d.separateSlotColors;
            hoursOnly          = d.hoursOnly;
            if (d.twoDayColor != 0) twoDayColor = d.twoDayColor;
            if (d.dayColor    != 0) dayColor    = d.dayColor;
            if (d.hourColor   != 0) hourColor   = d.hourColor;
            if (d.minuteColor != 0) minuteColor = d.minuteColor;
            twoDayBold = d.twoDayBold; dayBold = d.dayBold; hourBold = d.hourBold; minuteBold = d.minuteBold;
            // Auction Price
            showPrice            = d.showPrice;
            pricePosition        = safeEnum(TimerPosition.class, d.pricePosition,        pricePosition);
            priceHorizPosition   = safeEnum(HorizPosition.class, d.priceHorizPosition,   priceHorizPosition);
            showPriceBackground  = d.showPriceBackground;
            auctionBgColor       = d.auctionBgColor;
            priceSize            = safeEnum(PriceSize.class, migSz(migrate, d.priceSize), priceSize);
            if (d.priceColor != 0) priceColor = d.priceColor;
            priceBold            = d.priceBold;
            // Order Price
            showOrderPrice           = d.showOrderPrice;
            orderPricePosition       = safeEnum(TimerPosition.class, d.orderPricePosition,       orderPricePosition);
            orderPriceHorizPosition  = safeEnum(HorizPosition.class, d.orderPriceHorizPosition,  orderPriceHorizPosition);
            showOrderPriceBackground = d.showOrderPriceBackground;
            orderPriceBgColor        = d.orderPriceBgColor;
            orderPriceSize           = safeEnum(PriceSize.class, migSz(migrate, d.orderPriceSize), orderPriceSize);
            if (d.orderPriceColor != 0) orderPriceColor = d.orderPriceColor;
            orderPriceBold           = d.orderPriceBold;
            // Order Count
            showDelivered           = d.showDelivered;
            deliveredPosition       = safeEnum(TimerPosition.class, d.deliveredPosition,       deliveredPosition);
            deliveredHorizPosition  = safeEnum(HorizPosition.class, d.deliveredHorizPosition,  deliveredHorizPosition);
            showDeliveredBackground = d.showDeliveredBackground;
            deliveredBgColor        = d.deliveredBgColor;
            deliveredSize           = safeEnum(PriceSize.class, migSz(migrate, d.deliveredSize), deliveredSize);
            if (d.deliveredColor != 0) deliveredColor = d.deliveredColor;
            deliveredBold           = d.deliveredBold;
            // Shards
            showShards           = d.showShards;
            shardsPosition       = safeEnum(TimerPosition.class, d.shardsPosition,       shardsPosition);
            shardsHorizPosition  = safeEnum(HorizPosition.class, d.shardsHorizPosition,  shardsHorizPosition);
            shardsSymbolSide     = safeEnum(SymbolSide.class,    d.shardsSymbolSide,     shardsSymbolSide);
            shardsDigitColor     = d.shardsDigitColor;
            if (d.shardsSymbolColor != 0) shardsSymbolColor = d.shardsSymbolColor;
            showShardsBackground = d.showShardsBackground;
            shardsBgColor        = d.shardsBgColor;
            shardsSize           = safeEnum(PriceSize.class, migSz(migrate, d.shardsSize), shardsSize);
            shardsBold           = d.shardsBold;
            // Coins
            showCoins           = d.showCoins;
            coinsPosition       = safeEnum(TimerPosition.class, d.coinsPosition,       coinsPosition);
            coinsHorizPosition  = safeEnum(HorizPosition.class, d.coinsHorizPosition,  coinsHorizPosition);
            coinsSymbolSide     = safeEnum(SymbolSide.class,    d.coinsSymbolSide,     coinsSymbolSide);
            coinsDigitColor     = d.coinsDigitColor;
            if (d.coinsSymbolColor != 0) coinsSymbolColor = d.coinsSymbolColor;
            showCoinsBackground = d.showCoinsBackground;
            coinsBgColor        = d.coinsBgColor;
            coinsSize           = safeEnum(PriceSize.class, migSz(migrate, d.coinsSize), coinsSize);
            coinsBold           = d.coinsBold;
            // Rubies
            showRubies           = d.showRubies;
            rubiesPosition       = safeEnum(TimerPosition.class, d.rubiesPosition,       rubiesPosition);
            rubiesHorizPosition  = safeEnum(HorizPosition.class, d.rubiesHorizPosition,  rubiesHorizPosition);
            rubiesSymbolSide     = safeEnum(SymbolSide.class,    d.rubiesSymbolSide,     rubiesSymbolSide);
            rubiesDigitColor     = d.rubiesDigitColor;
            if (d.rubiesSymbolColor != 0) rubiesSymbolColor = d.rubiesSymbolColor;
            showRubiesBackground = d.showRubiesBackground;
            rubiesBgColor        = d.rubiesBgColor;
            rubiesSize           = safeEnum(PriceSize.class, migSz(migrate, d.rubiesSize), rubiesSize);
            rubiesBold           = d.rubiesBold;
            // Gems
            showGems           = d.showGems;
            gemsPosition       = safeEnum(TimerPosition.class, d.gemsPosition,       gemsPosition);
            gemsHorizPosition  = safeEnum(HorizPosition.class, d.gemsHorizPosition,  gemsHorizPosition);
            gemsSymbolSide     = safeEnum(SymbolSide.class,    d.gemsSymbolSide,     gemsSymbolSide);
            gemsDigitColor     = d.gemsDigitColor;
            if (d.gemsSymbolColor != 0) gemsSymbolColor = d.gemsSymbolColor;
            showGemsBackground = d.showGemsBackground;
            gemsBgColor        = d.gemsBgColor;
            gemsSize           = safeEnum(PriceSize.class, migSz(migrate, d.gemsSize), gemsSize);
            gemsBold           = d.gemsBold;
            // Chance
            showChance           = d.showChance;
            chanceColor          = d.chanceColor;
            chanceBold           = d.chanceBold;
            showChanceBackground = d.showChanceBackground;
            chanceBgColor        = d.chanceBgColor;
            chanceSize           = safeEnum(PriceSize.class, migSz(migrate, d.chanceSize), chanceSize);
            chanceVertPosition   = safeEnum(TimerPosition.class, d.chanceVertPosition,  chanceVertPosition);
            chanceHorizPosition  = safeEnum(HorizPosition.class, d.chanceHorizPosition, chanceHorizPosition);
            showChanceSymbol     = d.showChanceSymbol;
            chanceSymbolColor    = d.chanceSymbolColor;
            chanceFractionMode   = d.chanceFractionMode;
            chanceNumeratorColor   = d.chanceNumeratorColor;
            chanceDenominatorColor = d.chanceDenominatorColor;
            chanceSlashColor       = d.chanceSlashColor;
            // Display Context
            showInContainer = d.showInContainer;
            showInInventory = d.showInInventory;
            showInHotbar    = d.showInHotbar;
        } catch (Exception ignored) { }
    }

    public static void save() {
        if (allBgPending != null)      { setAllBackgrounds(allBgPending);          allBgPending      = null; }
        if (allBgColorPending != null) { setAllBackgroundColors(allBgColorPending); allBgColorPending = null; }
        if (allSizePending != null)    { setAllSizes(allSizePending);              allSizePending    = null; }
        try { Files.writeString(CONFIG_PATH, GSON.toJson(new ConfigData())); }
        catch (Exception ignored) { }
    }

    private static String migSz(boolean migrate, String name) {
        if (!migrate || name == null) return name;
        return switch (name.toUpperCase()) {
            case "SMALL"  -> "XSMALL";
            case "MEDIUM" -> "SMALL";
            default       -> name;
        };
    }

    private static <T extends Enum<T>> T safeEnum(Class<T> cls, String name, T fallback) {
        if (name == null || name.isEmpty()) return fallback;
        try { return Enum.valueOf(cls, name.toUpperCase()); }
        catch (Exception e) { return fallback; }
    }

    private static class ConfigData {
        int configVersion = 1;
        boolean showCurrencySymbol = DrillConfig.showCurrencySymbol;
        // Key Counter
        boolean showKeyCount       = DrillConfig.showKeyCount;
        boolean showKeyLabel       = DrillConfig.showKeyLabel;
        String  keyPosition        = DrillConfig.keyPosition.name();
        String  keyHorizPosition   = DrillConfig.keyHorizPosition.name();
        boolean showKeyBackground  = DrillConfig.showKeyBackground;
        int     keyBgColor         = DrillConfig.keyBgColor;
        String  keySize            = DrillConfig.keySize.name();
        int     keyCountColor      = DrillConfig.keyCountColor;
        boolean keyCountBold       = DrillConfig.keyCountBold;
        // Key Cost
        boolean showKeyCost           = DrillConfig.showKeyCost;
        String  keyCostVertPosition   = DrillConfig.keyCostVertPosition.name();
        String  keyCostHorizPosition  = DrillConfig.keyCostHorizPosition.name();
        int     keyCostColor          = DrillConfig.keyCostColor;
        boolean keyCostBold           = DrillConfig.keyCostBold;
        boolean showKeyCostBackground = DrillConfig.showKeyCostBackground;
        int     keyCostBgColor        = DrillConfig.keyCostBgColor;
        String  keyCostSize           = DrillConfig.keyCostSize.name();
        // Buy Price
        boolean showBuyPrice       = DrillConfig.showBuyPrice;
        String  buyPosition        = DrillConfig.buyPosition.name();
        String  buyHorizPosition   = DrillConfig.buyHorizPosition.name();
        int     buyColor           = DrillConfig.buyColor;
        boolean buyBold            = DrillConfig.buyBold;
        boolean showBuyBackground  = DrillConfig.showBuyBackground;
        int     buyBgColor         = DrillConfig.buyBgColor;
        String  buySize            = DrillConfig.buySize.name();
        // Sell Price
        boolean showSellPrice       = DrillConfig.showSellPrice;
        String  sellPosition        = DrillConfig.sellPosition.name();
        String  sellHorizPosition   = DrillConfig.sellHorizPosition.name();
        int     sellColor           = DrillConfig.sellColor;
        boolean sellBold            = DrillConfig.sellBold;
        boolean showSellBackground  = DrillConfig.showSellBackground;
        int     sellBgColor         = DrillConfig.sellBgColor;
        String  sellSize            = DrillConfig.sellSize.name();
        // Timer
        boolean enabled            = DrillConfig.enabled;
        boolean showTimer          = DrillConfig.showTimer;
        boolean showBackground     = DrillConfig.showBackground;
        int     timerBgColor       = DrillConfig.timerBgColor;
        String  timerSize          = DrillConfig.timerSize.name();
        String  timerPosition      = DrillConfig.timerPosition.name();
        String  timerHorizPosition = DrillConfig.timerHorizPosition.name();
        boolean separateSlotColors = DrillConfig.separateSlotColors;
        boolean hoursOnly          = DrillConfig.hoursOnly;
        int     twoDayColor        = DrillConfig.twoDayColor;
        int     dayColor           = DrillConfig.dayColor;
        int     hourColor          = DrillConfig.hourColor;
        int     minuteColor        = DrillConfig.minuteColor;
        boolean twoDayBold         = DrillConfig.twoDayBold;
        boolean dayBold            = DrillConfig.dayBold;
        boolean hourBold           = DrillConfig.hourBold;
        boolean minuteBold         = DrillConfig.minuteBold;
        // Auction Price
        boolean showPrice            = DrillConfig.showPrice;
        String  pricePosition        = DrillConfig.pricePosition.name();
        String  priceHorizPosition   = DrillConfig.priceHorizPosition.name();
        boolean showPriceBackground  = DrillConfig.showPriceBackground;
        int     auctionBgColor       = DrillConfig.auctionBgColor;
        String  priceSize            = DrillConfig.priceSize.name();
        int     priceColor           = DrillConfig.priceColor;
        boolean priceBold            = DrillConfig.priceBold;
        // Order Price
        boolean showOrderPrice           = DrillConfig.showOrderPrice;
        String  orderPricePosition       = DrillConfig.orderPricePosition.name();
        String  orderPriceHorizPosition  = DrillConfig.orderPriceHorizPosition.name();
        boolean showOrderPriceBackground = DrillConfig.showOrderPriceBackground;
        int     orderPriceBgColor        = DrillConfig.orderPriceBgColor;
        String  orderPriceSize           = DrillConfig.orderPriceSize.name();
        int     orderPriceColor          = DrillConfig.orderPriceColor;
        boolean orderPriceBold           = DrillConfig.orderPriceBold;
        // Order Count
        boolean showDelivered            = DrillConfig.showDelivered;
        String  deliveredPosition        = DrillConfig.deliveredPosition.name();
        String  deliveredHorizPosition   = DrillConfig.deliveredHorizPosition.name();
        boolean showDeliveredBackground  = DrillConfig.showDeliveredBackground;
        int     deliveredBgColor         = DrillConfig.deliveredBgColor;
        String  deliveredSize            = DrillConfig.deliveredSize.name();
        int     deliveredColor           = DrillConfig.deliveredColor;
        boolean deliveredBold            = DrillConfig.deliveredBold;
        // Shards
        boolean showShards            = DrillConfig.showShards;
        String  shardsPosition        = DrillConfig.shardsPosition.name();
        String  shardsHorizPosition   = DrillConfig.shardsHorizPosition.name();
        String  shardsSymbolSide      = DrillConfig.shardsSymbolSide.name();
        int     shardsDigitColor      = DrillConfig.shardsDigitColor;
        int     shardsSymbolColor     = DrillConfig.shardsSymbolColor;
        boolean showShardsBackground  = DrillConfig.showShardsBackground;
        int     shardsBgColor         = DrillConfig.shardsBgColor;
        String  shardsSize            = DrillConfig.shardsSize.name();
        boolean shardsBold            = DrillConfig.shardsBold;
        // Coins
        boolean showCoins            = DrillConfig.showCoins;
        String  coinsPosition        = DrillConfig.coinsPosition.name();
        String  coinsHorizPosition   = DrillConfig.coinsHorizPosition.name();
        String  coinsSymbolSide      = DrillConfig.coinsSymbolSide.name();
        int     coinsDigitColor      = DrillConfig.coinsDigitColor;
        int     coinsSymbolColor     = DrillConfig.coinsSymbolColor;
        boolean showCoinsBackground  = DrillConfig.showCoinsBackground;
        int     coinsBgColor         = DrillConfig.coinsBgColor;
        String  coinsSize            = DrillConfig.coinsSize.name();
        boolean coinsBold            = DrillConfig.coinsBold;
        // Rubies
        boolean showRubies            = DrillConfig.showRubies;
        String  rubiesPosition        = DrillConfig.rubiesPosition.name();
        String  rubiesHorizPosition   = DrillConfig.rubiesHorizPosition.name();
        String  rubiesSymbolSide      = DrillConfig.rubiesSymbolSide.name();
        int     rubiesDigitColor      = DrillConfig.rubiesDigitColor;
        int     rubiesSymbolColor     = DrillConfig.rubiesSymbolColor;
        boolean showRubiesBackground  = DrillConfig.showRubiesBackground;
        int     rubiesBgColor         = DrillConfig.rubiesBgColor;
        String  rubiesSize            = DrillConfig.rubiesSize.name();
        boolean rubiesBold            = DrillConfig.rubiesBold;
        // Gems
        boolean showGems            = DrillConfig.showGems;
        String  gemsPosition        = DrillConfig.gemsPosition.name();
        String  gemsHorizPosition   = DrillConfig.gemsHorizPosition.name();
        String  gemsSymbolSide      = DrillConfig.gemsSymbolSide.name();
        int     gemsDigitColor      = DrillConfig.gemsDigitColor;
        int     gemsSymbolColor     = DrillConfig.gemsSymbolColor;
        boolean showGemsBackground  = DrillConfig.showGemsBackground;
        int     gemsBgColor         = DrillConfig.gemsBgColor;
        String  gemsSize            = DrillConfig.gemsSize.name();
        boolean gemsBold            = DrillConfig.gemsBold;
        // Chance
        boolean showChance             = DrillConfig.showChance;
        int     chanceColor            = DrillConfig.chanceColor;
        boolean chanceBold             = DrillConfig.chanceBold;
        boolean showChanceBackground   = DrillConfig.showChanceBackground;
        int     chanceBgColor          = DrillConfig.chanceBgColor;
        String  chanceSize             = DrillConfig.chanceSize.name();
        String  chanceVertPosition     = DrillConfig.chanceVertPosition.name();
        String  chanceHorizPosition    = DrillConfig.chanceHorizPosition.name();
        boolean showChanceSymbol       = DrillConfig.showChanceSymbol;
        int     chanceSymbolColor      = DrillConfig.chanceSymbolColor;
        boolean chanceFractionMode     = DrillConfig.chanceFractionMode;
        int     chanceNumeratorColor   = DrillConfig.chanceNumeratorColor;
        int     chanceDenominatorColor = DrillConfig.chanceDenominatorColor;
        int     chanceSlashColor       = DrillConfig.chanceSlashColor;
        // Display Context
        boolean showInContainer = DrillConfig.showInContainer;
        boolean showInInventory = DrillConfig.showInInventory;
        boolean showInHotbar    = DrillConfig.showInHotbar;
    }

    // ── Cloth Config GUI ───────────────────────────────────────────────────────

    public static ConfigBuilder getConfigBuilder() {
        ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(Component.literal("Info in Slot"))
                .setSavingRunnable(DrillConfig::save);

        ConfigEntryBuilder eb = builder.entryBuilder();

        // Capture initial values so universal toggles only apply when actually changed
        boolean   initAllBg           = allBackgroundsOn();
        int       initUniversalBgColor = timerBgColor;
        PriceSize initUniversalSize    = priceSize;
        boolean   initShowTimer        = showTimer;
        boolean   initShowOrderPrice   = showOrderPrice;
        boolean   initShowDelivered    = showDelivered;
        boolean   initShowCoins        = showCoins;
        boolean   initShowShards       = showShards;
        boolean   initShowKeyCount     = showKeyCount;

        // ── Main ─────────────────────────────────────────────────────────────
        ConfigCategory mainCat = builder.getOrCreateCategory(Component.literal("Main"));

        mainCat.addEntry(eb.startBooleanToggle(Component.literal("Enable Mod"), enabled)
                .setDefaultValue(true).setSaveConsumer(v -> enabled = v).build());
        mainCat.addEntry(eb.startBooleanToggle(Component.literal("Show Timer"), showTimer)
                .setDefaultValue(true).setSaveConsumer(v -> { if (v != initShowTimer) showTimer = v; }).build());
        mainCat.addEntry(eb.startBooleanToggle(Component.literal("Show Order/Auction Price"), showOrderPrice)
                .setDefaultValue(true).setSaveConsumer(v -> { if (v != initShowOrderPrice) showOrderPrice = v; }).build());
        mainCat.addEntry(eb.startBooleanToggle(Component.literal("Show Order Count"), showDelivered)
                .setDefaultValue(true).setSaveConsumer(v -> { if (v != initShowDelivered) showDelivered = v; }).build());
        mainCat.addEntry(eb.startBooleanToggle(Component.literal("Toggle Coins"), showCoins)
                .setDefaultValue(true).setSaveConsumer(v -> { if (v != initShowCoins) showCoins = v; }).build());
        mainCat.addEntry(eb.startBooleanToggle(Component.literal("Toggle Shards"), showShards)
                .setDefaultValue(true).setSaveConsumer(v -> { if (v != initShowShards) showShards = v; }).build());
        mainCat.addEntry(eb.startBooleanToggle(Component.literal("Toggle Keys"), showKeyCount)
                .setDefaultValue(true).setSaveConsumer(v -> { if (v != initShowKeyCount) showKeyCount = v; }).build());
        mainCat.addEntry(eb.startBooleanToggle(Component.literal("Show Currency Symbol"), showCurrencySymbol)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Show/hide the $ prefix on Auction Price and Order Price"))
                .setSaveConsumer(v -> showCurrencySymbol = v).build());
        mainCat.addEntry(eb.startBooleanToggle(Component.literal("Display Stats in Container"), showInContainer)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Show slot stats in chest/shop/container screens"))
                .setSaveConsumer(v -> showInContainer = v).build());
        mainCat.addEntry(eb.startBooleanToggle(Component.literal("Display Stats in Inventory"), showInInventory)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Show slot stats in the player inventory screen (36 slots)"))
                .setSaveConsumer(v -> showInInventory = v).build());
        mainCat.addEntry(eb.startBooleanToggle(Component.literal("Display Stats in Hotbar"), showInHotbar)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Show slot stats on the hotbar when no screen is open"))
                .setSaveConsumer(v -> showInHotbar = v).build());
        mainCat.addEntry(eb.startBooleanToggle(Component.literal("All Backgrounds"), allBackgroundsOn())
                .setDefaultValue(true)
                .setTooltip(Component.literal("Toggle backgrounds for ALL displays at once"))
                .setSaveConsumer(v -> { if (v != initAllBg) allBgPending = v; }).build());
        mainCat.addEntry(new ColorSwatchEntry(Component.literal("Universal Background Color"),
                timerBgColor, 0x000000, v -> { if (!v.equals(initUniversalBgColor)) allBgColorPending = v; }));
        mainCat.addEntry(eb.startEnumSelector(Component.literal("Universal Size"), PriceSize.class, priceSize)
                .setDefaultValue(PriceSize.SMALL)
                .setTooltip(Component.literal("Set the same size for ALL displays at once"))
                .setEnumNameProvider(v -> sizeLabel((PriceSize) v))
                .setSaveConsumer(v -> { if (!v.equals(initUniversalSize)) allSizePending = v; }).build());

        // ── Order/Auction Price ───────────────────────────────────────────────
        ConfigCategory orderPriceCat = builder.getOrCreateCategory(Component.literal("Order/Auction Price"));
        orderPriceCat.addEntry(eb.startBooleanToggle(Component.literal("Show Order/Auction Price"), showOrderPrice)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Show /order price ($ X) and /ah price ($X) on item slots"))
                .setSaveConsumer(v -> { if (v != initShowOrderPrice) showOrderPrice = v; }).build());
        orderPriceCat.addEntry(eb.startEnumSelector(Component.literal("Position Height"), TimerPosition.class, orderPricePosition)
                .setDefaultValue(TimerPosition.TOP).setEnumNameProvider(v -> posLabel((TimerPosition) v))
                .setSaveConsumer(v -> orderPricePosition = v).build());
        orderPriceCat.addEntry(eb.startEnumSelector(Component.literal("Position Width"), HorizPosition.class, orderPriceHorizPosition)
                .setDefaultValue(HorizPosition.LEFT).setEnumNameProvider(v -> horizLabel((HorizPosition) v))
                .setSaveConsumer(v -> orderPriceHorizPosition = v).build());
        orderPriceCat.addEntry(eb.startBooleanToggle(Component.literal("Background"), showOrderPriceBackground)
                .setDefaultValue(true).setSaveConsumer(v -> showOrderPriceBackground = v).build());
        orderPriceCat.addEntry(new ColorSwatchEntry(Component.literal("Background Color"),
                orderPriceBgColor, 0x000000, v -> orderPriceBgColor = v));
        orderPriceCat.addEntry(eb.startEnumSelector(Component.literal("Size"), PriceSize.class, orderPriceSize)
                .setDefaultValue(PriceSize.SMALL).setEnumNameProvider(v -> sizeLabel((PriceSize) v))
                .setSaveConsumer(v -> orderPriceSize = v).build());
        orderPriceCat.addEntry(new ColorSwatchEntry(Component.literal("Color"), orderPriceColor, 0x55FF55,
                orderPriceBold, false, v -> orderPriceColor = v, v -> orderPriceBold = v));

        // ── Order Count ───────────────────────────────────────────────────────
        ConfigCategory orderCountCat = builder.getOrCreateCategory(Component.literal("Order Count"));
        orderCountCat.addEntry(eb.startBooleanToggle(Component.literal("Show Order Count"), showDelivered)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Remaining items to deliver, e.g. '800k' from '1.2M/2M Delivered'"))
                .setSaveConsumer(v -> { if (v != initShowDelivered) showDelivered = v; }).build());
        orderCountCat.addEntry(eb.startEnumSelector(Component.literal("Position Height"), TimerPosition.class, deliveredPosition)
                .setDefaultValue(TimerPosition.BOTTOM).setEnumNameProvider(v -> posLabel((TimerPosition) v))
                .setSaveConsumer(v -> deliveredPosition = v).build());
        orderCountCat.addEntry(eb.startEnumSelector(Component.literal("Position Width"), HorizPosition.class, deliveredHorizPosition)
                .setDefaultValue(HorizPosition.LEFT).setEnumNameProvider(v -> horizLabel((HorizPosition) v))
                .setSaveConsumer(v -> deliveredHorizPosition = v).build());
        orderCountCat.addEntry(eb.startBooleanToggle(Component.literal("Background"), showDeliveredBackground)
                .setDefaultValue(true).setSaveConsumer(v -> showDeliveredBackground = v).build());
        orderCountCat.addEntry(new ColorSwatchEntry(Component.literal("Background Color"),
                deliveredBgColor, 0x000000, v -> deliveredBgColor = v));
        orderCountCat.addEntry(eb.startEnumSelector(Component.literal("Size"), PriceSize.class, deliveredSize)
                .setDefaultValue(PriceSize.SMALL).setEnumNameProvider(v -> sizeLabel((PriceSize) v))
                .setSaveConsumer(v -> deliveredSize = v).build());
        orderCountCat.addEntry(new ColorSwatchEntry(Component.literal("Color"), deliveredColor, 0xFFFFFF,
                deliveredBold, false, v -> deliveredColor = v, v -> deliveredBold = v));

        // ── Timer ─────────────────────────────────────────────────────────────
        ConfigCategory timerCat = builder.getOrCreateCategory(Component.literal("Timer"));
        timerCat.addEntry(eb.startBooleanToggle(Component.literal("Show Timer"), showTimer)
                .setDefaultValue(true).setSaveConsumer(v -> { if (v != initShowTimer) showTimer = v; }).build());
        timerCat.addEntry(eb.startBooleanToggle(Component.literal("Show Background"), showBackground)
                .setDefaultValue(true).setSaveConsumer(v -> showBackground = v).build());
        timerCat.addEntry(new ColorSwatchEntry(Component.literal("Background Color"),
                timerBgColor, 0x000000, v -> timerBgColor = v));
        timerCat.addEntry(eb.startEnumSelector(Component.literal("Timer Size"), TimerSize.class, timerSize)
                .setDefaultValue(TimerSize.SMALL)
                .setEnumNameProvider(v -> switch ((TimerSize) v) {
                    case XSMALL -> Component.literal("XSmall");
                    case SMALL  -> Component.literal("Small");
                    case MEDIUM -> Component.literal("Medium");
                    case LARGE  -> Component.literal("Large");
                })
                .setSaveConsumer(v -> timerSize = v).build());
        timerCat.addEntry(eb.startEnumSelector(Component.literal("Timer Position Height"), TimerPosition.class, timerPosition)
                .setDefaultValue(TimerPosition.MIDDLE).setEnumNameProvider(v -> posLabel((TimerPosition) v))
                .setSaveConsumer(v -> timerPosition = v).build());
        timerCat.addEntry(eb.startEnumSelector(Component.literal("Timer Position Width"), HorizPosition.class, timerHorizPosition)
                .setDefaultValue(HorizPosition.RIGHT).setEnumNameProvider(v -> horizLabel((HorizPosition) v))
                .setSaveConsumer(v -> timerHorizPosition = v).build());
        timerCat.addEntry(eb.startBooleanToggle(Component.literal("Separate Slot Colors"), separateSlotColors)
                .setDefaultValue(true).setSaveConsumer(v -> separateSlotColors = v).build());
        timerCat.addEntry(eb.startBooleanToggle(Component.literal("Hours Only"), hoursOnly)
                .setDefaultValue(false).setSaveConsumer(v -> hoursOnly = v).build());
        timerCat.addEntry(new ColorSwatchEntry(Component.literal("2+ Day Color"), twoDayColor, 0xFF55FF,
                twoDayBold, false, v -> twoDayColor = v, v -> twoDayBold = v));
        timerCat.addEntry(new ColorSwatchEntry(Component.literal("Day Color"), dayColor, 0x55FFFF,
                dayBold, false, v -> dayColor = v, v -> dayBold = v));
        timerCat.addEntry(new ColorSwatchEntry(Component.literal("Hour Color"), hourColor, 0xFFAA00,
                hourBold, false, v -> hourColor = v, v -> hourBold = v));
        timerCat.addEntry(new ColorSwatchEntry(Component.literal("Minute Color"), minuteColor, 0xFF5555,
                minuteBold, false, v -> minuteColor = v, v -> minuteBold = v));

        // ── Buy/Sell ──────────────────────────────────────────────────────────
        ConfigCategory buySellCat = builder.getOrCreateCategory(Component.literal("Buy/Sell"));
        buySellCat.addEntry(eb.startBooleanToggle(Component.literal("Show Buy Price"), showBuyPrice)
                .setDefaultValue(true).setSaveConsumer(v -> showBuyPrice = v).build());
        buySellCat.addEntry(eb.startEnumSelector(Component.literal("Buy Position Height"), TimerPosition.class, buyPosition)
                .setDefaultValue(TimerPosition.TOP).setEnumNameProvider(v -> posLabel((TimerPosition) v))
                .setSaveConsumer(v -> buyPosition = v).build());
        buySellCat.addEntry(eb.startEnumSelector(Component.literal("Buy Position Width"), HorizPosition.class, buyHorizPosition)
                .setDefaultValue(HorizPosition.LEFT).setEnumNameProvider(v -> horizLabel((HorizPosition) v))
                .setSaveConsumer(v -> buyHorizPosition = v).build());
        buySellCat.addEntry(new ColorSwatchEntry(Component.literal("Buy Color"), buyColor, 0x55FF55,
                buyBold, false, v -> buyColor = v, v -> buyBold = v));
        buySellCat.addEntry(eb.startBooleanToggle(Component.literal("Buy Background"), showBuyBackground)
                .setDefaultValue(true).setSaveConsumer(v -> showBuyBackground = v).build());
        buySellCat.addEntry(new ColorSwatchEntry(Component.literal("Buy Background Color"),
                buyBgColor, 0x000000, v -> buyBgColor = v));
        buySellCat.addEntry(eb.startEnumSelector(Component.literal("Buy Size"), PriceSize.class, buySize)
                .setDefaultValue(PriceSize.SMALL).setEnumNameProvider(v -> sizeLabel((PriceSize) v))
                .setSaveConsumer(v -> buySize = v).build());
        buySellCat.addEntry(eb.startBooleanToggle(Component.literal("Show Sell Price"), showSellPrice)
                .setDefaultValue(true).setSaveConsumer(v -> showSellPrice = v).build());
        buySellCat.addEntry(eb.startEnumSelector(Component.literal("Sell Position Height"), TimerPosition.class, sellPosition)
                .setDefaultValue(TimerPosition.BOTTOM).setEnumNameProvider(v -> posLabel((TimerPosition) v))
                .setSaveConsumer(v -> sellPosition = v).build());
        buySellCat.addEntry(eb.startEnumSelector(Component.literal("Sell Position Width"), HorizPosition.class, sellHorizPosition)
                .setDefaultValue(HorizPosition.LEFT).setEnumNameProvider(v -> horizLabel((HorizPosition) v))
                .setSaveConsumer(v -> sellHorizPosition = v).build());
        buySellCat.addEntry(new ColorSwatchEntry(Component.literal("Sell Color"), sellColor, 0xFF5555,
                sellBold, false, v -> sellColor = v, v -> sellBold = v));
        buySellCat.addEntry(eb.startBooleanToggle(Component.literal("Sell Background"), showSellBackground)
                .setDefaultValue(true).setSaveConsumer(v -> showSellBackground = v).build());
        buySellCat.addEntry(new ColorSwatchEntry(Component.literal("Sell Background Color"),
                sellBgColor, 0x000000, v -> sellBgColor = v));
        buySellCat.addEntry(eb.startEnumSelector(Component.literal("Sell Size"), PriceSize.class, sellSize)
                .setDefaultValue(PriceSize.SMALL).setEnumNameProvider(v -> sizeLabel((PriceSize) v))
                .setSaveConsumer(v -> sellSize = v).build());

        // ── Shards ────────────────────────────────────────────────────────────
        ConfigCategory shardsCat = builder.getOrCreateCategory(Component.literal("Shards"));
        shardsCat.addEntry(eb.startBooleanToggle(Component.literal("Toggle Shards"), showShards)
                .setDefaultValue(true).setSaveConsumer(v -> { if (v != initShowShards) showShards = v; }).build());
        shardsCat.addEntry(eb.startEnumSelector(Component.literal("Position Height"), TimerPosition.class, shardsPosition)
                .setDefaultValue(TimerPosition.TOP).setEnumNameProvider(v -> posLabel((TimerPosition) v))
                .setSaveConsumer(v -> shardsPosition = v).build());
        shardsCat.addEntry(eb.startEnumSelector(Component.literal("Position Width"), HorizPosition.class, shardsHorizPosition)
                .setDefaultValue(HorizPosition.LEFT).setEnumNameProvider(v -> horizLabel((HorizPosition) v))
                .setSaveConsumer(v -> shardsHorizPosition = v).build());
        shardsCat.addEntry(eb.startEnumSelector(Component.literal("Display Symbol (S)"), SymbolSide.class, shardsSymbolSide)
                .setDefaultValue(SymbolSide.OFF).setEnumNameProvider(v -> symLabel((SymbolSide) v))
                .setTooltip(Component.literal("Left: S109k | Right: 109kS | Off: 109k"))
                .setSaveConsumer(v -> shardsSymbolSide = v).build());
        shardsCat.addEntry(eb.startEnumSelector(Component.literal("Size"), PriceSize.class, shardsSize)
                .setDefaultValue(PriceSize.SMALL).setEnumNameProvider(v -> sizeLabel((PriceSize) v))
                .setSaveConsumer(v -> shardsSize = v).build());
        shardsCat.addEntry(new ColorSwatchEntry(Component.literal("Digit Color (0=auto)"),
                shardsDigitColor, 0, shardsBold, false, v -> shardsDigitColor = v, v -> shardsBold = v));
        shardsCat.addEntry(new ColorSwatchEntry(Component.literal("Symbol Color"),
                shardsSymbolColor, 0xFF55FF, v -> shardsSymbolColor = v));
        shardsCat.addEntry(eb.startBooleanToggle(Component.literal("Background"), showShardsBackground)
                .setDefaultValue(true).setSaveConsumer(v -> showShardsBackground = v).build());
        shardsCat.addEntry(new ColorSwatchEntry(Component.literal("Background Color"),
                shardsBgColor, 0x000000, v -> shardsBgColor = v));

        // ── Coins ─────────────────────────────────────────────────────────────
        ConfigCategory coinsCat = builder.getOrCreateCategory(Component.literal("Coins"));
        coinsCat.addEntry(eb.startBooleanToggle(Component.literal("Toggle Coins"), showCoins)
                .setDefaultValue(true).setSaveConsumer(v -> { if (v != initShowCoins) showCoins = v; }).build());
        coinsCat.addEntry(eb.startEnumSelector(Component.literal("Position Height"), TimerPosition.class, coinsPosition)
                .setDefaultValue(TimerPosition.TOP).setEnumNameProvider(v -> posLabel((TimerPosition) v))
                .setSaveConsumer(v -> coinsPosition = v).build());
        coinsCat.addEntry(eb.startEnumSelector(Component.literal("Position Width"), HorizPosition.class, coinsHorizPosition)
                .setDefaultValue(HorizPosition.LEFT).setEnumNameProvider(v -> horizLabel((HorizPosition) v))
                .setSaveConsumer(v -> coinsHorizPosition = v).build());
        coinsCat.addEntry(eb.startEnumSelector(Component.literal("Display Symbol (C)"), SymbolSide.class, coinsSymbolSide)
                .setDefaultValue(SymbolSide.OFF).setEnumNameProvider(v -> symLabel((SymbolSide) v))
                .setTooltip(Component.literal("Left: C109k | Right: 109kC | Off: 109k"))
                .setSaveConsumer(v -> coinsSymbolSide = v).build());
        coinsCat.addEntry(eb.startEnumSelector(Component.literal("Size"), PriceSize.class, coinsSize)
                .setDefaultValue(PriceSize.SMALL).setEnumNameProvider(v -> sizeLabel((PriceSize) v))
                .setSaveConsumer(v -> coinsSize = v).build());
        coinsCat.addEntry(new ColorSwatchEntry(Component.literal("Digit Color (0=auto)"),
                coinsDigitColor, 0, coinsBold, false, v -> coinsDigitColor = v, v -> coinsBold = v));
        coinsCat.addEntry(new ColorSwatchEntry(Component.literal("Symbol Color"),
                coinsSymbolColor, 0xFFAA00, v -> coinsSymbolColor = v));
        coinsCat.addEntry(eb.startBooleanToggle(Component.literal("Background"), showCoinsBackground)
                .setDefaultValue(true).setSaveConsumer(v -> showCoinsBackground = v).build());
        coinsCat.addEntry(new ColorSwatchEntry(Component.literal("Background Color"),
                coinsBgColor, 0x000000, v -> coinsBgColor = v));

        // ── Rubies ────────────────────────────────────────────────────────────
        ConfigCategory rubiesCat = builder.getOrCreateCategory(Component.literal("Rubies"));
        rubiesCat.addEntry(eb.startBooleanToggle(Component.literal("Toggle Rubies"), showRubies)
                .setDefaultValue(false).setSaveConsumer(v -> showRubies = v).build());
        rubiesCat.addEntry(eb.startEnumSelector(Component.literal("Position Height"), TimerPosition.class, rubiesPosition)
                .setDefaultValue(TimerPosition.TOP).setEnumNameProvider(v -> posLabel((TimerPosition) v))
                .setSaveConsumer(v -> rubiesPosition = v).build());
        rubiesCat.addEntry(eb.startEnumSelector(Component.literal("Position Width"), HorizPosition.class, rubiesHorizPosition)
                .setDefaultValue(HorizPosition.LEFT).setEnumNameProvider(v -> horizLabel((HorizPosition) v))
                .setSaveConsumer(v -> rubiesHorizPosition = v).build());
        rubiesCat.addEntry(eb.startEnumSelector(Component.literal("Display Symbol (R)"), SymbolSide.class, rubiesSymbolSide)
                .setDefaultValue(SymbolSide.RIGHT).setEnumNameProvider(v -> symLabel((SymbolSide) v))
                .setTooltip(Component.literal("Left: R109k | Right: 109kR | Off: 109k"))
                .setSaveConsumer(v -> rubiesSymbolSide = v).build());
        rubiesCat.addEntry(eb.startEnumSelector(Component.literal("Size"), PriceSize.class, rubiesSize)
                .setDefaultValue(PriceSize.SMALL).setEnumNameProvider(v -> sizeLabel((PriceSize) v))
                .setSaveConsumer(v -> rubiesSize = v).build());
        rubiesCat.addEntry(new ColorSwatchEntry(Component.literal("Digit Color (0=auto)"),
                rubiesDigitColor, 0, rubiesBold, false, v -> rubiesDigitColor = v, v -> rubiesBold = v));
        rubiesCat.addEntry(new ColorSwatchEntry(Component.literal("Symbol Color"),
                rubiesSymbolColor, 0xFF55FF, v -> rubiesSymbolColor = v));
        rubiesCat.addEntry(eb.startBooleanToggle(Component.literal("Background"), showRubiesBackground)
                .setDefaultValue(true).setSaveConsumer(v -> showRubiesBackground = v).build());
        rubiesCat.addEntry(new ColorSwatchEntry(Component.literal("Background Color"),
                rubiesBgColor, 0x000000, v -> rubiesBgColor = v));

        // ── Gems ──────────────────────────────────────────────────────────────
        ConfigCategory gemsCat = builder.getOrCreateCategory(Component.literal("Gems"));
        gemsCat.addEntry(eb.startBooleanToggle(Component.literal("Toggle Gems"), showGems)
                .setDefaultValue(true).setSaveConsumer(v -> showGems = v).build());
        gemsCat.addEntry(eb.startEnumSelector(Component.literal("Position Height"), TimerPosition.class, gemsPosition)
                .setDefaultValue(TimerPosition.TOP).setEnumNameProvider(v -> posLabel((TimerPosition) v))
                .setSaveConsumer(v -> gemsPosition = v).build());
        gemsCat.addEntry(eb.startEnumSelector(Component.literal("Position Width"), HorizPosition.class, gemsHorizPosition)
                .setDefaultValue(HorizPosition.LEFT).setEnumNameProvider(v -> horizLabel((HorizPosition) v))
                .setSaveConsumer(v -> gemsHorizPosition = v).build());
        gemsCat.addEntry(eb.startEnumSelector(Component.literal("Display Symbol (G)"), SymbolSide.class, gemsSymbolSide)
                .setDefaultValue(SymbolSide.OFF).setEnumNameProvider(v -> symLabel((SymbolSide) v))
                .setTooltip(Component.literal("Left: G109k | Right: 109kG | Off: 109k"))
                .setSaveConsumer(v -> gemsSymbolSide = v).build());
        gemsCat.addEntry(eb.startEnumSelector(Component.literal("Size"), PriceSize.class, gemsSize)
                .setDefaultValue(PriceSize.SMALL).setEnumNameProvider(v -> sizeLabel((PriceSize) v))
                .setSaveConsumer(v -> gemsSize = v).build());
        gemsCat.addEntry(new ColorSwatchEntry(Component.literal("Digit Color (0=auto)"),
                gemsDigitColor, 0, gemsBold, false, v -> gemsDigitColor = v, v -> gemsBold = v));
        gemsCat.addEntry(new ColorSwatchEntry(Component.literal("Symbol Color"),
                gemsSymbolColor, 0x00AA00, v -> gemsSymbolColor = v));
        gemsCat.addEntry(eb.startBooleanToggle(Component.literal("Background"), showGemsBackground)
                .setDefaultValue(true).setSaveConsumer(v -> showGemsBackground = v).build());
        gemsCat.addEntry(new ColorSwatchEntry(Component.literal("Background Color"),
                gemsBgColor, 0x000000, v -> gemsBgColor = v));

        // ── Keys ──────────────────────────────────────────────────────────────
        ConfigCategory keysCat = builder.getOrCreateCategory(Component.literal("Keys"));
        keysCat.addEntry(eb.startBooleanToggle(Component.literal("Show Key Count"), showKeyCount)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Display key/balance count from tooltips (e.g. '5 Keys', 'You have 0 Keys')"))
                .setSaveConsumer(v -> { if (v != initShowKeyCount) showKeyCount = v; }).build());
        keysCat.addEntry(eb.startBooleanToggle(Component.literal("Show Key Label"), showKeyLabel)
                .setDefaultValue(false)
                .setTooltip(Component.literal("Off: shows digit only. On: shows '5 Keys'"))
                .setSaveConsumer(v -> showKeyLabel = v).build());
        keysCat.addEntry(eb.startEnumSelector(Component.literal("Position Height"), TimerPosition.class, keyPosition)
                .setDefaultValue(TimerPosition.BOTTOM).setEnumNameProvider(v -> posLabel((TimerPosition) v))
                .setSaveConsumer(v -> keyPosition = v).build());
        keysCat.addEntry(eb.startEnumSelector(Component.literal("Position Width"), HorizPosition.class, keyHorizPosition)
                .setDefaultValue(HorizPosition.LEFT).setEnumNameProvider(v -> horizLabel((HorizPosition) v))
                .setSaveConsumer(v -> keyHorizPosition = v).build());
        keysCat.addEntry(eb.startEnumSelector(Component.literal("Size"), PriceSize.class, keySize)
                .setDefaultValue(PriceSize.SMALL).setEnumNameProvider(v -> sizeLabel((PriceSize) v))
                .setSaveConsumer(v -> keySize = v).build());
        keysCat.addEntry(new ColorSwatchEntry(Component.literal("Key Count Color (0=auto)"),
                keyCountColor, 0x55FFFF, keyCountBold, false, v -> keyCountColor = v, v -> keyCountBold = v));
        keysCat.addEntry(eb.startBooleanToggle(Component.literal("Background"), showKeyBackground)
                .setDefaultValue(true).setSaveConsumer(v -> showKeyBackground = v).build());
        keysCat.addEntry(new ColorSwatchEntry(Component.literal("Background Color"),
                keyBgColor, 0x000000, v -> keyBgColor = v));
        // Key Cost
        keysCat.addEntry(eb.startBooleanToggle(Component.literal("Show Key Cost"), showKeyCost)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Display key cost from 'Cost: X keys' in tooltips"))
                .setSaveConsumer(v -> showKeyCost = v).build());
        keysCat.addEntry(eb.startEnumSelector(Component.literal("Key Cost Position Height"), TimerPosition.class, keyCostVertPosition)
                .setDefaultValue(TimerPosition.TOP).setEnumNameProvider(v -> posLabel((TimerPosition) v))
                .setSaveConsumer(v -> keyCostVertPosition = v).build());
        keysCat.addEntry(eb.startEnumSelector(Component.literal("Key Cost Position Width"), HorizPosition.class, keyCostHorizPosition)
                .setDefaultValue(HorizPosition.RIGHT).setEnumNameProvider(v -> horizLabel((HorizPosition) v))
                .setSaveConsumer(v -> keyCostHorizPosition = v).build());
        keysCat.addEntry(eb.startEnumSelector(Component.literal("Key Cost Size"), PriceSize.class, keyCostSize)
                .setDefaultValue(PriceSize.SMALL).setEnumNameProvider(v -> sizeLabel((PriceSize) v))
                .setSaveConsumer(v -> keyCostSize = v).build());
        keysCat.addEntry(new ColorSwatchEntry(Component.literal("Key Cost Color"), keyCostColor, 0xFF5555,
                keyCostBold, false, v -> keyCostColor = v, v -> keyCostBold = v));
        keysCat.addEntry(eb.startBooleanToggle(Component.literal("Key Cost Background"), showKeyCostBackground)
                .setDefaultValue(true).setSaveConsumer(v -> showKeyCostBackground = v).build());
        keysCat.addEntry(new ColorSwatchEntry(Component.literal("Key Cost BG Color"),
                keyCostBgColor, 0x000000, v -> keyCostBgColor = v));

        // ── Chance ────────────────────────────────────────────────────────────
        ConfigCategory chanceCat = builder.getOrCreateCategory(Component.literal("Chance"));
        chanceCat.addEntry(eb.startBooleanToggle(Component.literal("Show Chance"), showChance)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Detect and display 'Chance: X%' or any X% in tooltips"))
                .setSaveConsumer(v -> showChance = v).build());
        chanceCat.addEntry(eb.startBooleanToggle(Component.literal("Show Symbol (%)"), showChanceSymbol)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Show or hide the % symbol after the number"))
                .setSaveConsumer(v -> showChanceSymbol = v).build());
        chanceCat.addEntry(eb.startEnumSelector(Component.literal("Position Height"), TimerPosition.class, chanceVertPosition)
                .setDefaultValue(TimerPosition.BOTTOM).setEnumNameProvider(v -> posLabel((TimerPosition) v))
                .setSaveConsumer(v -> chanceVertPosition = v).build());
        chanceCat.addEntry(eb.startEnumSelector(Component.literal("Position Width"), HorizPosition.class, chanceHorizPosition)
                .setDefaultValue(HorizPosition.RIGHT).setEnumNameProvider(v -> horizLabel((HorizPosition) v))
                .setSaveConsumer(v -> chanceHorizPosition = v).build());
        chanceCat.addEntry(eb.startEnumSelector(Component.literal("Size"), PriceSize.class, chanceSize)
                .setDefaultValue(PriceSize.MEDIUM).setEnumNameProvider(v -> sizeLabel((PriceSize) v))
                .setSaveConsumer(v -> chanceSize = v).build());
        chanceCat.addEntry(new ColorSwatchEntry(Component.literal("Color (0=auto from tooltip)"),
                chanceColor, 0xFFFF55, chanceBold, false, v -> chanceColor = v, v -> chanceBold = v));
        chanceCat.addEntry(new ColorSwatchEntry(Component.literal("Symbol Color (0=auto)"),
                chanceSymbolColor, 0, v -> chanceSymbolColor = v));
        chanceCat.addEntry(eb.startBooleanToggle(Component.literal("Background"), showChanceBackground)
                .setDefaultValue(false).setSaveConsumer(v -> showChanceBackground = v).build());
        chanceCat.addEntry(new ColorSwatchEntry(Component.literal("Background Color"),
                chanceBgColor, 0x000000, v -> chanceBgColor = v));
        chanceCat.addEntry(eb.startBooleanToggle(Component.literal("Fraction Mode"), chanceFractionMode)
                .setDefaultValue(false)
                .setTooltip(Component.literal("Convert percentage to fraction: 0.01% → 1/10k"))
                .setSaveConsumer(v -> chanceFractionMode = v).build());
        chanceCat.addEntry(new ColorSwatchEntry(Component.literal("Numerator Color (0=auto)"),
                chanceNumeratorColor, 0, v -> chanceNumeratorColor = v));
        chanceCat.addEntry(new ColorSwatchEntry(Component.literal("Denominator Color (0=auto)"),
                chanceDenominatorColor, 0, v -> chanceDenominatorColor = v));
        chanceCat.addEntry(new ColorSwatchEntry(Component.literal("Slash Color (0=auto)"),
                chanceSlashColor, 0, v -> chanceSlashColor = v));

        return builder;
    }

    // ── Universal helpers ─────────────────────────────────────────────────────

    public static boolean allBackgroundsOn() {
        return showBackground && showPriceBackground && showOrderPriceBackground
            && showDeliveredBackground && showKeyBackground && showKeyCostBackground
            && showBuyBackground && showSellBackground
            && showShardsBackground && showCoinsBackground
            && showRubiesBackground && showGemsBackground
            && showChanceBackground;
    }

    public static void setAllBackgrounds(boolean v) {
        showBackground = showPriceBackground = showOrderPriceBackground = showDeliveredBackground = v;
        showKeyBackground = showKeyCostBackground = showBuyBackground = showSellBackground = v;
        showShardsBackground = showCoinsBackground = showRubiesBackground = showGemsBackground = v;
        showChanceBackground = v;
    }

    public static void setAllBackgroundColors(int color) {
        timerBgColor = auctionBgColor = orderPriceBgColor = deliveredBgColor = color;
        keyBgColor = keyCostBgColor = buyBgColor = sellBgColor = color;
        shardsBgColor = coinsBgColor = rubiesBgColor = gemsBgColor = color;
        chanceBgColor = color;
    }

    public static void setAllSizes(PriceSize size) {
        priceSize = orderPriceSize = deliveredSize = buySize = sellSize = size;
        keySize = keyCostSize = shardsSize = coinsSize = rubiesSize = gemsSize = size;
        chanceSize = size;
        timerSize = TimerSize.valueOf(size.name());
    }

    // ── Label helpers ─────────────────────────────────────────────────────────

    public static Component posLabel(TimerPosition p) {
        return switch (p) {
            case TOP    -> Component.literal("Top");
            case MIDDLE -> Component.literal("Middle");
            case BOTTOM -> Component.literal("Bottom");
        };
    }

    public static Component sizeLabel(PriceSize s) {
        return switch (s) {
            case XSMALL -> Component.literal("XSmall");
            case SMALL  -> Component.literal("Small");
            case MEDIUM -> Component.literal("Medium");
            case LARGE  -> Component.literal("Large");
        };
    }

    private static Component symLabel(SymbolSide s) {
        return switch (s) {
            case LEFT  -> Component.literal("Left");
            case RIGHT -> Component.literal("Right");
            case OFF   -> Component.literal("Off");
        };
    }

    public static Component horizLabel(HorizPosition h) {
        return switch (h) {
            case LEFT   -> Component.literal("Left");
            case MIDDLE -> Component.literal("Middle");
            case RIGHT  -> Component.literal("Right");
        };
    }
}
