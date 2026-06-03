package com.drillslotinfo.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import java.nio.file.Files;
import java.nio.file.Path;

public class DrillConfig {

    public enum TimerSize     { SMALL, MEDIUM, LARGE }
    public enum TimerPosition { TOP, MIDDLE, BOTTOM }
    public enum PriceSize     { SMALL, MEDIUM }

    // ── Timer Display ─────────────────────────────────────────────────────────
    public static boolean       enabled            = true;
    public static boolean       showTimer          = true;
    public static boolean       showBackground     = true;
    public static TimerSize     timerSize          = TimerSize.MEDIUM;
    public static TimerPosition timerPosition      = TimerPosition.MIDDLE;
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

    // ── Auction Price (/ah tooltip: "$1.27B" no space) ────────────────────────
    public static boolean       showPrice           = true;
    public static TimerPosition pricePosition       = TimerPosition.TOP;
    public static boolean       showPriceBackground = true;
    public static PriceSize     priceSize           = PriceSize.MEDIUM;
    public static int           priceColor          = 0x55FF55; // lime green
    public static boolean       priceBold           = false;

    // ── Order Price (/order tooltip: "$ 130.1k" with space) ──────────────────
    public static boolean       showOrderPrice           = true;
    public static TimerPosition orderPricePosition       = TimerPosition.TOP;
    public static boolean       showOrderPriceBackground = true;
    public static PriceSize     orderPriceSize           = PriceSize.MEDIUM;
    public static int           orderPriceColor          = 0x55FF55; // lime
    public static boolean       orderPriceBold           = false;

    // ── Order Display (delivered count) ──────────────────────────────────────
    public static boolean       showDelivered           = true;
    public static TimerPosition deliveredPosition       = TimerPosition.BOTTOM;
    public static boolean       showDeliveredBackground = true;
    public static PriceSize     deliveredSize           = PriceSize.MEDIUM;
    public static int           deliveredColor          = 0xFFFFFF; // white
    public static boolean       deliveredBold           = false;

    // ── Persistence ───────────────────────────────────────────────────────────

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("infoinslot.json");

    public static void init() {
        load();
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) return;
        try {
            ConfigData d = GSON.fromJson(Files.readString(CONFIG_PATH), ConfigData.class);
            if (d == null) return;
            // Timer Display
            enabled            = d.enabled;
            showTimer          = d.showTimer;
            showBackground     = d.showBackground;
            timerSize          = safeEnum(TimerSize.class,     d.timerSize,          timerSize);
            timerPosition      = safeEnum(TimerPosition.class, d.timerPosition,      timerPosition);
            separateSlotColors = d.separateSlotColors;
            hoursOnly          = d.hoursOnly;
            if (d.twoDayColor != 0) twoDayColor = d.twoDayColor;
            if (d.dayColor    != 0) dayColor    = d.dayColor;
            if (d.hourColor   != 0) hourColor   = d.hourColor;
            if (d.minuteColor != 0) minuteColor = d.minuteColor;
            twoDayBold = d.twoDayBold;
            dayBold    = d.dayBold;
            hourBold   = d.hourBold;
            minuteBold = d.minuteBold;
            // Auction Price
            showPrice           = d.showPrice;
            pricePosition       = safeEnum(TimerPosition.class, d.pricePosition,      pricePosition);
            showPriceBackground = d.showPriceBackground;
            priceSize           = safeEnum(PriceSize.class,     d.priceSize,          priceSize);
            if (d.priceColor != 0) priceColor = d.priceColor;
            priceBold           = d.priceBold;
            // Order Price
            showOrderPrice           = d.showOrderPrice;
            orderPricePosition       = safeEnum(TimerPosition.class, d.orderPricePosition, orderPricePosition);
            showOrderPriceBackground = d.showOrderPriceBackground;
            orderPriceSize           = safeEnum(PriceSize.class,     d.orderPriceSize,     orderPriceSize);
            if (d.orderPriceColor != 0) orderPriceColor = d.orderPriceColor;
            orderPriceBold           = d.orderPriceBold;
            // Order Display
            showDelivered           = d.showDelivered;
            deliveredPosition       = safeEnum(TimerPosition.class, d.deliveredPosition, deliveredPosition);
            showDeliveredBackground = d.showDeliveredBackground;
            deliveredSize           = safeEnum(PriceSize.class,     d.deliveredSize,     deliveredSize);
            if (d.deliveredColor != 0) deliveredColor = d.deliveredColor;
            deliveredBold           = d.deliveredBold;
        } catch (Exception ignored) { }
    }

    public static void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(new ConfigData()));
        } catch (Exception ignored) { }
    }

    private static <T extends Enum<T>> T safeEnum(Class<T> cls, String name, T fallback) {
        if (name == null || name.isEmpty()) return fallback;
        try { return Enum.valueOf(cls, name); }
        catch (Exception e) { return fallback; }
    }

    private static class ConfigData {
        // Timer Display
        boolean enabled            = DrillConfig.enabled;
        boolean showTimer          = DrillConfig.showTimer;
        boolean showBackground     = DrillConfig.showBackground;
        String  timerSize          = DrillConfig.timerSize.name();
        String  timerPosition      = DrillConfig.timerPosition.name();
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
        boolean showPrice           = DrillConfig.showPrice;
        String  pricePosition       = DrillConfig.pricePosition.name();
        boolean showPriceBackground = DrillConfig.showPriceBackground;
        String  priceSize           = DrillConfig.priceSize.name();
        int     priceColor          = DrillConfig.priceColor;
        boolean priceBold           = DrillConfig.priceBold;
        // Order Price
        boolean showOrderPrice           = DrillConfig.showOrderPrice;
        String  orderPricePosition       = DrillConfig.orderPricePosition.name();
        boolean showOrderPriceBackground = DrillConfig.showOrderPriceBackground;
        String  orderPriceSize           = DrillConfig.orderPriceSize.name();
        int     orderPriceColor          = DrillConfig.orderPriceColor;
        boolean orderPriceBold           = DrillConfig.orderPriceBold;
        // Order Display
        boolean showDelivered           = DrillConfig.showDelivered;
        String  deliveredPosition       = DrillConfig.deliveredPosition.name();
        boolean showDeliveredBackground = DrillConfig.showDeliveredBackground;
        String  deliveredSize           = DrillConfig.deliveredSize.name();
        int     deliveredColor          = DrillConfig.deliveredColor;
        boolean deliveredBold           = DrillConfig.deliveredBold;
    }

    // ── Cloth Config GUI ──────────────────────────────────────────────────────

    public static ConfigBuilder getConfigBuilder() {
        ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(Text.literal("Info in Slot"))
                .setSavingRunnable(DrillConfig::save);

        ConfigEntryBuilder eb = builder.entryBuilder();

        // ── Timer Display ─────────────────────────────────────────────────────
        ConfigCategory timerCat = builder.getOrCreateCategory(Text.literal("Timer Display"));

        timerCat.addEntry(eb.startBooleanToggle(Text.literal("Enable Mod"), enabled)
                .setDefaultValue(true).setSaveConsumer(v -> enabled = v).build());

        timerCat.addEntry(eb.startBooleanToggle(Text.literal("Show Timer"), showTimer)
                .setDefaultValue(true).setSaveConsumer(v -> showTimer = v).build());

        timerCat.addEntry(eb.startBooleanToggle(Text.literal("Show Background"), showBackground)
                .setDefaultValue(true).setSaveConsumer(v -> showBackground = v).build());

        timerCat.addEntry(eb.startEnumSelector(Text.literal("Timer Size"), TimerSize.class, timerSize)
                .setDefaultValue(TimerSize.MEDIUM)
                .setEnumNameProvider(v -> switch ((TimerSize) v) {
                    case SMALL  -> Text.literal("Small");
                    case MEDIUM -> Text.literal("Medium");
                    case LARGE  -> Text.literal("Large (Hours Only)");
                })
                .setSaveConsumer(v -> timerSize = v)
                .setTooltip(Text.literal("Large is only available with Hours Only enabled"))
                .build());

        timerCat.addEntry(eb.startEnumSelector(Text.literal("Timer Position"), TimerPosition.class, timerPosition)
                .setDefaultValue(TimerPosition.MIDDLE)
                .setEnumNameProvider(v -> posLabel((TimerPosition) v))
                .setSaveConsumer(v -> timerPosition = v).build());

        timerCat.addEntry(eb.startBooleanToggle(Text.literal("Separate Slot Colors"), separateSlotColors)
                .setDefaultValue(true).setSaveConsumer(v -> separateSlotColors = v).build());

        timerCat.addEntry(eb.startBooleanToggle(Text.literal("Hours Only"), hoursOnly)
                .setDefaultValue(false)
                .setSaveConsumer(v -> {
                    hoursOnly = v;
                    if (!v && timerSize == TimerSize.LARGE) timerSize = TimerSize.MEDIUM;
                }).build());

        timerCat.addEntry(new ColorSwatchEntry(Text.literal("2+ Day Color"), twoDayColor, 0xFF55FF,
                twoDayBold, false, v -> twoDayColor = v, v -> twoDayBold = v));
        timerCat.addEntry(new ColorSwatchEntry(Text.literal("Day Color"), dayColor, 0x55FFFF,
                dayBold, false, v -> dayColor = v, v -> dayBold = v));
        timerCat.addEntry(new ColorSwatchEntry(Text.literal("Hour Color"), hourColor, 0xFFAA00,
                hourBold, false, v -> hourColor = v, v -> hourBold = v));
        timerCat.addEntry(new ColorSwatchEntry(Text.literal("Minute Color"), minuteColor, 0xFF5555,
                minuteBold, false, v -> minuteColor = v, v -> minuteBold = v));

        // ── Auction Price ─────────────────────────────────────────────────────
        ConfigCategory auctionCat = builder.getOrCreateCategory(Text.literal("Auction Price"));

        auctionCat.addEntry(eb.startBooleanToggle(Text.literal("Show Auction Price"), showPrice)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Price from /ah — shown as $1.27B in lime green"))
                .setSaveConsumer(v -> showPrice = v).build());

        auctionCat.addEntry(eb.startEnumSelector(Text.literal("Position"), TimerPosition.class, pricePosition)
                .setDefaultValue(TimerPosition.TOP)
                .setEnumNameProvider(v -> posLabel((TimerPosition) v))
                .setSaveConsumer(v -> pricePosition = v).build());

        auctionCat.addEntry(eb.startBooleanToggle(Text.literal("Background"), showPriceBackground)
                .setDefaultValue(true).setSaveConsumer(v -> showPriceBackground = v).build());

        auctionCat.addEntry(eb.startEnumSelector(Text.literal("Size"), PriceSize.class, priceSize)
                .setDefaultValue(PriceSize.MEDIUM)
                .setEnumNameProvider(v -> sizeLabel((PriceSize) v))
                .setSaveConsumer(v -> priceSize = v).build());

        auctionCat.addEntry(new ColorSwatchEntry(Text.literal("Color"), priceColor, 0x55FF55,
                priceBold, false, v -> priceColor = v, v -> priceBold = v));

        // ── Order Price ───────────────────────────────────────────────────────
        ConfigCategory orderPriceCat = builder.getOrCreateCategory(Text.literal("Order Price"));

        orderPriceCat.addEntry(eb.startBooleanToggle(Text.literal("Show Order Price"), showOrderPrice)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Price from /order — shown as $ 130.1k (space stripped on display)"))
                .setSaveConsumer(v -> showOrderPrice = v).build());

        orderPriceCat.addEntry(eb.startEnumSelector(Text.literal("Position"), TimerPosition.class, orderPricePosition)
                .setDefaultValue(TimerPosition.TOP)
                .setEnumNameProvider(v -> posLabel((TimerPosition) v))
                .setSaveConsumer(v -> orderPricePosition = v).build());

        orderPriceCat.addEntry(eb.startBooleanToggle(Text.literal("Background"), showOrderPriceBackground)
                .setDefaultValue(true).setSaveConsumer(v -> showOrderPriceBackground = v).build());

        orderPriceCat.addEntry(eb.startEnumSelector(Text.literal("Size"), PriceSize.class, orderPriceSize)
                .setDefaultValue(PriceSize.MEDIUM)
                .setEnumNameProvider(v -> sizeLabel((PriceSize) v))
                .setSaveConsumer(v -> orderPriceSize = v).build());

        orderPriceCat.addEntry(new ColorSwatchEntry(Text.literal("Color"), orderPriceColor, 0x55FF55,
                orderPriceBold, false, v -> orderPriceColor = v, v -> orderPriceBold = v));

        // ── Order Display (delivered) ─────────────────────────────────────────
        ConfigCategory orderDisplayCat = builder.getOrCreateCategory(Text.literal("Order Display"));

        orderDisplayCat.addEntry(eb.startBooleanToggle(Text.literal("Show Delivered"), showDelivered)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Remaining items to deliver, e.g. '800k' from '1.2M/2M Delivered'"))
                .setSaveConsumer(v -> showDelivered = v).build());

        orderDisplayCat.addEntry(eb.startEnumSelector(Text.literal("Position"), TimerPosition.class, deliveredPosition)
                .setDefaultValue(TimerPosition.BOTTOM)
                .setEnumNameProvider(v -> posLabel((TimerPosition) v))
                .setSaveConsumer(v -> deliveredPosition = v).build());

        orderDisplayCat.addEntry(eb.startBooleanToggle(Text.literal("Background"), showDeliveredBackground)
                .setDefaultValue(true).setSaveConsumer(v -> showDeliveredBackground = v).build());

        orderDisplayCat.addEntry(eb.startEnumSelector(Text.literal("Size"), PriceSize.class, deliveredSize)
                .setDefaultValue(PriceSize.MEDIUM)
                .setEnumNameProvider(v -> sizeLabel((PriceSize) v))
                .setSaveConsumer(v -> deliveredSize = v).build());

        orderDisplayCat.addEntry(new ColorSwatchEntry(Text.literal("Color"), deliveredColor, 0xFFFFFF,
                deliveredBold, false, v -> deliveredColor = v, v -> deliveredBold = v));

        return builder;
    }

    private static Text posLabel(TimerPosition p) {
        return switch (p) {
            case TOP    -> Text.literal("Top");
            case MIDDLE -> Text.literal("Middle");
            case BOTTOM -> Text.literal("Bottom");
        };
    }

    private static Text sizeLabel(PriceSize s) {
        return switch (s) {
            case SMALL  -> Text.literal("Small");
            case MEDIUM -> Text.literal("Medium");
        };
    }
}
