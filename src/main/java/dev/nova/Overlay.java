package dev.nova;

import net.minecraft.client.gui.DrawContext;

public final class Overlay extends Module {
    final Setting.Bool watermark = bool("Watermark", true), list = bool("Module List", true), coords = bool("Coordinates", true);
    final ColorSetting color = color("Text", 0x80C0FF);

    public Overlay() { super("Overlay", "HUD: watermark, module list, coordinates.", true); }

    @Override
    public void onHud(DrawContext c, float td) {
        if (mc.player == null) return;
        int col = color.get(), y = 4;
        if (watermark.value) { c.drawText(mc.textRenderer, "Nova Client", 4, y, col, true); y += 10; }
        if (coords.value) {
            String s = String.format("XYZ %.1f / %.1f / %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
            c.drawText(mc.textRenderer, s, 4, y, 0xFFFFFFFF, true);
        }
        if (list.value) {
            int ry = 4, sw = c.getScaledWindowWidth();
            for (Module m : Modules.ALL) {
                if (!m.enabled || !m.toggleable) continue;
                c.drawText(mc.textRenderer, m.name, sw - mc.textRenderer.getWidth(m.name) - 4, ry, col, true);
                ry += 10;
            }
        }
    }
}
