package dev.nova;

import net.minecraft.util.math.MathHelper;

/** R/G/B sliders plus a Rainbow toggle. Shows up as 4 rows in the GUI. */
public final class ColorSetting {
    public final Setting.Num r, g, b;
    public final Setting.Bool rainbow;

    ColorSetting(Module m, String label, int rgb) {
        r = m.num(label + " R", (rgb >> 16) & 255, 0, 255, true); r.tint = 0xFF4040;
        g = m.num(label + " G", (rgb >> 8) & 255, 0, 255, true);  g.tint = 0x40FF40;
        b = m.num(label + " B", rgb & 255, 0, 255, true);         b.tint = 0x4080FF;
        rainbow = m.bool(label + " Rainbow", false);
    }

    /** ARGB color, animated when Rainbow is on. */
    public int get() {
        if (rainbow.value) {
            float hue = (System.currentTimeMillis() % 3000L) / 3000f;
            return 0xFF000000 | MathHelper.hsvToRgb(hue, 0.85f, 1f);
        }
        return 0xFF000000 | ((int) r.value << 16) | ((int) g.value << 8) | (int) b.value;
    }
}
