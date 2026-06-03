package com.drillslotinfo;

public class ParsedDrillData {
    public final int days, hours, minutes, seconds; // -1 if absent from lore
    public final String timer;        // formatted display string for normal mode
    public final String auctionPrice; // nullable — "$1.27B" from /ah tooltip
    public final String orderPrice;   // nullable — "$130.1k" from /order tooltip (space removed)
    public final String delivered;    // nullable — remaining count to deliver, e.g. "800k"
    public final String deliveredDone;  // nullable — done count (numerator), shown for expired/completed orders
    public final boolean orderFinished; // true if order is expired or completed

    public ParsedDrillData(int days, int hours, int minutes, int seconds) {
        this(days, hours, minutes, seconds, null, null, null, null, false);
    }

    public ParsedDrillData(int days, int hours, int minutes, int seconds,
                           String auctionPrice, String orderPrice, String delivered,
                           String deliveredDone, boolean orderFinished) {
        this.days          = days;
        this.hours         = hours;
        this.minutes       = minutes;
        this.seconds       = seconds;
        this.auctionPrice  = auctionPrice;
        this.orderPrice    = orderPrice;
        this.delivered     = delivered;
        this.deliveredDone = deliveredDone;
        this.orderFinished = orderFinished;
        this.timer         = build(days, hours, minutes, seconds);
    }

    public ParsedDrillData withAuctionPrice(String p) {
        return new ParsedDrillData(days, hours, minutes, seconds, p, orderPrice, delivered, deliveredDone, orderFinished);
    }

    public ParsedDrillData withOrderPrice(String p) {
        return new ParsedDrillData(days, hours, minutes, seconds, auctionPrice, p, delivered, deliveredDone, orderFinished);
    }

    public ParsedDrillData withDelivered(String d) {
        return new ParsedDrillData(days, hours, minutes, seconds, auctionPrice, orderPrice, d, deliveredDone, orderFinished);
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
        // When days >= 10, skip hours entirely (e.g. "10d" not "10d17h")
        if (start + 1 < 4 && v[start + 1] >= 0 && !(u[start] == 'd' && v[start] >= 10)) {
            sb.append(' ').append(v[start + 1]).append(u[start + 1]);
        }
        return sb.toString();
    }
}
