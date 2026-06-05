package com.drillslotinfo.config;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ColorSwatchEntry extends AbstractConfigListEntry<Integer> {

    static final int[] PRESETS = {
        0x000000, 0x0000AA, 0x00AA00, 0x00AAAA,
        0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
        0x555555, 0x5555FF, 0x55FF55, 0x55FFFF,
        0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF
    };

    private static final int SWATCH     = 8;
    private static final int GAP        = 2;
    private static final int COLS       = 8;
    private static final int ROW_H      = SWATCH + GAP;
    private static final int BOLD_BTN_W = 28;
    private static final int BOLD_BTN_H = 9;

    private int value;
    private final int defaultValue;
    private final int initialValue;
    private final Consumer<Integer> saveConsumer;

    private boolean boldValue;
    private final boolean defaultBold;
    private final boolean initialBold;
    private final Consumer<Boolean> boldSave;

    private int lastX, lastY, lastW, lastH;

    public ColorSwatchEntry(Component fieldName, int value, int defaultValue,
                            boolean bold, boolean defaultBold,
                            Consumer<Integer> saveConsumer, Consumer<Boolean> boldSave) {
        super(fieldName, false);
        this.value        = value;
        this.defaultValue = defaultValue;
        this.initialValue = value;
        this.saveConsumer = saveConsumer;
        this.boldValue    = bold;
        this.defaultBold  = defaultBold;
        this.initialBold  = bold;
        this.boldSave     = boldSave;
    }

    @Override public Integer getValue()                   { return value; }
    @Override public Optional<Integer> getDefaultValue()  { return Optional.of(defaultValue); }
    @Override public void save()                          { saveConsumer.accept(value); boldSave.accept(boldValue); }
    @Override public boolean isEdited()                   { return value != initialValue || boldValue != initialBold; }

    @Override
    public List<? extends GuiEventListener> children()   { return Collections.emptyList(); }
    @Override
    public List<? extends NarratableEntry> narratables() { return Collections.emptyList(); }

    @Override
    public void extractRenderState(GuiGraphicsExtractor ctx, int index, int y, int x,
                                   int entryWidth, int entryHeight, int mx, int my,
                                   boolean hovered, float delta) {
        lastX = x; lastY = y; lastW = entryWidth; lastH = entryHeight;
        Minecraft mc = Minecraft.getInstance();

        ctx.text(mc.font, getDisplayedFieldName(), x, y + 9, 0xFFFFFFFF, true);

        int swatchAreaW = COLS * (SWATCH + GAP) - GAP;
        int swX0 = x + entryWidth - swatchAreaW;

        for (int i = 0; i < PRESETS.length; i++) {
            int col = i % COLS;
            int row = i / COLS;
            int sx = swX0 + col * (SWATCH + GAP);
            int sy = y + 2 + row * ROW_H;
            boolean sel = (PRESETS[i] == value);
            ctx.fill(sx - 1, sy - 1, sx + SWATCH + 1, sy + SWATCH + 1,
                     sel ? 0xFFFFFFFF : 0xFF444444);
            ctx.fill(sx, sy, sx + SWATCH, sy + SWATCH, PRESETS[i] | 0xFF000000);
        }

        int previewX = swX0 - 16;
        int previewY = y + (entryHeight - 12) / 2;
        ctx.fill(previewX - 1, previewY - 1, previewX + 13, previewY + 13, 0xFFAAAAAA);
        ctx.fill(previewX, previewY, previewX + 12, previewY + 12, value | 0xFF000000);

        int boldBtnX = previewX - 4 - BOLD_BTN_W;
        int boldBtnY = y + (entryHeight - BOLD_BTN_H) / 2;
        ctx.fill(boldBtnX, boldBtnY, boldBtnX + BOLD_BTN_W, boldBtnY + BOLD_BTN_H,
                 boldValue ? 0xFF686868 : 0xFF303030);
        if (boldValue) {
            ctx.text(mc.font,
                    Component.literal("Bold").withStyle(Style.EMPTY.withBold(true)),
                    boldBtnX + 3, boldBtnY + 1, 0xFFFFFFFF, false);
        } else {
            ctx.text(mc.font, "Bold", boldBtnX + 3, boldBtnY + 1, 0xFF777777, false);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean bl) {
        if (click.button() != 0) return false;
        double mx = click.x();
        double my = click.y();

        int swatchAreaW = COLS * (SWATCH + GAP) - GAP;
        int swX0 = lastX + lastW - swatchAreaW;

        int previewX = swX0 - 16;
        int boldBtnX = previewX - 4 - BOLD_BTN_W;
        int boldBtnY = lastY + (lastH - BOLD_BTN_H) / 2;
        if (mx >= boldBtnX && mx < boldBtnX + BOLD_BTN_W &&
            my >= boldBtnY && my < boldBtnY + BOLD_BTN_H) {
            boldValue = !boldValue;
            return true;
        }

        for (int i = 0; i < PRESETS.length; i++) {
            int col = i % COLS;
            int row = i / COLS;
            int sx = swX0 + col * (SWATCH + GAP);
            int sy = lastY + 2 + row * ROW_H;
            if (mx >= sx && mx < sx + SWATCH && my >= sy && my < sy + SWATCH) {
                value = PRESETS[i];
                return true;
            }
        }
        return false;
    }
}
