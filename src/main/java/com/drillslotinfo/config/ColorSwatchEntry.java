package com.drillslotinfo.config;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.text.Text;

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
    private static final int ROW_H      = SWATCH + GAP; // 10
    private static final int BOLD_BTN_W = 28;
    private static final int BOLD_BTN_H = 9;

    private int value;
    private final int defaultValue;  // factory default — used by the Reset button
    private final int initialValue;  // value when screen opened — used by isEdited()
    private final Consumer<Integer> saveConsumer;

    private boolean boldValue;
    private final boolean defaultBold;  // factory default
    private final boolean initialBold;  // value when screen opened
    private final Consumer<Boolean> boldSave;

    // Cached render coords for hit-detection in mouseClicked.
    private int lastX, lastY, lastW, lastH;

    public ColorSwatchEntry(Text fieldName, int value, int defaultValue,
                            boolean bold, boolean defaultBold,
                            Consumer<Integer> saveConsumer, Consumer<Boolean> boldSave) {
        super(fieldName, false);
        this.value        = value;
        this.defaultValue = defaultValue;
        this.initialValue = value;   // snapshot of the loaded value
        this.saveConsumer = saveConsumer;
        this.boldValue    = bold;
        this.defaultBold  = defaultBold;
        this.initialBold  = bold;    // snapshot of the loaded bold state
        this.boldSave     = boldSave;
    }

    @Override public Integer getValue()                        { return value; }
    @Override public Optional<Integer> getDefaultValue()      { return Optional.of(defaultValue); }
    @Override public void save()                              { saveConsumer.accept(value); boldSave.accept(boldValue); }

    // Compare against the value that was loaded when the screen opened, not the factory default.
    // This ensures the Save button stays active when the user undoes a non-default value back to
    // a state that still differs from the factory default (e.g. bold was saved as true, user turns it off).
    @Override public boolean isEdited()                       { return value != initialValue || boldValue != initialBold; }

    @Override public List<? extends Element> children()       { return Collections.emptyList(); }
    @Override public List<? extends Selectable> narratables() { return Collections.emptyList(); }

    @Override
    public void render(DrawContext ctx, int index, int y, int x,
                       int entryWidth, int entryHeight, int mx, int my,
                       boolean hovered, float delta) {
        lastX = x; lastY = y; lastW = entryWidth; lastH = entryHeight;
        MinecraftClient mc = MinecraftClient.getInstance();

        // Field label
        ctx.drawTextWithShadow(mc.textRenderer, getDisplayedFieldName(), x, y + 9, 0xFFFFFFFF);

        // Swatch grid: 8 cols × 2 rows, right-aligned
        int swatchAreaW = COLS * (SWATCH + GAP) - GAP; // 78
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

        // Colour preview square: left of swatch grid
        int previewX = swX0 - 16;
        int previewY = y + (entryHeight - 12) / 2;
        ctx.fill(previewX - 1, previewY - 1, previewX + 13, previewY + 13, 0xFFAAAAAA);
        ctx.fill(previewX, previewY, previewX + 12, previewY + 12, value | 0xFF000000);

        // Bold toggle button: left of preview square
        int boldBtnX = previewX - 4 - BOLD_BTN_W;
        int boldBtnY = y + (entryHeight - BOLD_BTN_H) / 2;
        ctx.fill(boldBtnX, boldBtnY, boldBtnX + BOLD_BTN_W, boldBtnY + BOLD_BTN_H,
                 boldValue ? 0xFF686868 : 0xFF303030);
        if (boldValue) {
            ctx.drawTextWithShadow(mc.textRenderer,
                    Text.literal("Bold").styled(s -> s.withBold(true)),
                    boldBtnX + 3, boldBtnY + 1, 0xFFFFFFFF);
        } else {
            ctx.drawTextWithShadow(mc.textRenderer, "Bold", boldBtnX + 3, boldBtnY + 1, 0xFF777777);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean bl) {
        if (click.button() != 0) return false;
        double mx = click.x();
        double my = click.y();

        int swatchAreaW = COLS * (SWATCH + GAP) - GAP;
        int swX0 = lastX + lastW - swatchAreaW;

        // Bold button hit
        int previewX = swX0 - 16;
        int boldBtnX = previewX - 4 - BOLD_BTN_W;
        int boldBtnY = lastY + (lastH - BOLD_BTN_H) / 2;
        if (mx >= boldBtnX && mx < boldBtnX + BOLD_BTN_W &&
            my >= boldBtnY && my < boldBtnY + BOLD_BTN_H) {
            boldValue = !boldValue;
            return true;
        }

        // Color swatch hit
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
