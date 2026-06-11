package com.drillslotinfo;

public class ParsedDrillData {
    public final int days, hours, minutes, seconds;
    public final String timer;
    public final String auctionPrice;
    public final String orderPrice;
    public final String delivered;
    public final String deliveredDone;
    public final boolean orderFinished;
    public final String keyCount;
    public final int keyColor;
    public final String keyCost;
    public final int keyCostColor;
    public final String buyPrice;
    public final String sellPrice;
    public final String shardsAmount;
    public final int    shardsColor;
    public final String coinsAmount;
    public final int    coinsColor;
    public final String rubiesAmount;
    public final int    rubiesColor;
    public final String gemsAmount;
    public final int    gemsColor;
    public final String chanceFormatted; // bare number without %, e.g. "0.01" or "67"
    public final int    chanceRawColor;  // color extracted from the tooltip line

    /** Timer-only constructor (all currency/price/chance fields null). */
    public ParsedDrillData(int days, int hours, int minutes, int seconds) {
        this(days, hours, minutes, seconds,
             null, null, null, null, false,
             null, 0xFFFFFF, null, 0xFFFFFF, null, null,
             null, 0xFFFFFF, null, 0xFFFFFF,
             null, 0xFFFFFF, null, 0xFFFFFF,
             null, 0xFFFFFF);
    }

    public ParsedDrillData(int days, int hours, int minutes, int seconds,
                           String auctionPrice, String orderPrice,
                           String delivered, String deliveredDone, boolean orderFinished,
                           String keyCount, int keyColor,
                           String keyCost, int keyCostColor,
                           String buyPrice, String sellPrice,
                           String shardsAmount, int shardsColor,
                           String coinsAmount, int coinsColor,
                           String rubiesAmount, int rubiesColor,
                           String gemsAmount, int gemsColor,
                           String chanceFormatted, int chanceRawColor) {
        this.days           = days;
        this.hours          = hours;
        this.minutes        = minutes;
        this.seconds        = seconds;
        this.auctionPrice   = auctionPrice;
        this.orderPrice     = orderPrice;
        this.delivered      = delivered;
        this.deliveredDone  = deliveredDone;
        this.orderFinished  = orderFinished;
        this.keyCount       = keyCount;
        this.keyColor       = keyColor;
        this.keyCost        = keyCost;
        this.keyCostColor   = keyCostColor;
        this.buyPrice       = buyPrice;
        this.sellPrice      = sellPrice;
        this.shardsAmount   = shardsAmount;
        this.shardsColor    = shardsColor;
        this.coinsAmount    = coinsAmount;
        this.coinsColor     = coinsColor;
        this.rubiesAmount   = rubiesAmount;
        this.rubiesColor    = rubiesColor;
        this.gemsAmount     = gemsAmount;
        this.gemsColor      = gemsColor;
        this.chanceFormatted= chanceFormatted;
        this.chanceRawColor = chanceRawColor;
        this.timer          = build(days, hours, minutes, seconds);
    }

    private static String build(int d, int h, int m, int s) {
        int[]  v = {d, h, m, s};
        char[] u = {'d', 'h', 'm', 's'};
        int start = -1;
        for (int i = 0; i < 4; i++) if (v[i] > 0)  { start = i; break; }
        if (start == -1)
        for (int i = 0; i < 4; i++) if (v[i] >= 0) { start = i; break; }
        if (start == -1) return "?";
        StringBuilder sb = new StringBuilder();
        sb.append(v[start]).append(u[start]);
        if (start + 1 < 4 && v[start + 1] >= 0 && !(u[start] == 'd' && v[start] >= 10)) {
            sb.append(' ').append(v[start + 1]).append(u[start + 1]);
        }
        return sb.toString();
    }
}
