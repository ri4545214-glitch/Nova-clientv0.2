package dev.nova;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public final class ClickGuiScreen extends Screen {
    private static final int W = 470, H = 290, TAB_W = 100, TITLE_H = 24, ROW_H = 18, ROW_GAP = 2, ROWS = 9, PAD = 8;
    private static final int COL_W = (W - TAB_W - PAD * 3) / 2;

    private final ParticleField particles = new ParticleField();
    private Module selected = Modules.ALL.get(0);
    private Setting.Num dragging;
    private int dragIndex, x0, y0;

    public ClickGuiScreen() { super(Text.literal("Nova")); }

    @Override protected void init() { x0 = (width - W) / 2; y0 = (height - H) / 2; }
    @Override public boolean shouldPause() { return false; }

    // ---- layout helpers ----
    private int paneX() { return x0 + TAB_W + PAD; }
    private int rowX(int i) { return paneX() + (i / ROWS) * (COL_W + PAD); }
    private int rowY(int i) { return y0 + TITLE_H + 42 + (i % ROWS) * (ROW_H + ROW_GAP); }
    private int tabY(int i) { return y0 + TITLE_H + 6 + i * 22; }
    private int toggleX() { return paneX() + 2 * COL_W + PAD - 54; }
    private int toggleY() { return y0 + TITLE_H + 8; }
    private static boolean in(double mx, double my, int x, int y, int w, int h) { return mx >= x && mx < x + w && my >= y && my < y + h; }

    @Override
    public void render(DrawContext c, int mx, int my, float delta) {
        int accent = Modules.CONFIG.guiColor.get();
        c.fill(0, 0, width, height, 0xA0000000);
        c.fill(x0, y0, x0 + W, y0 + H, 0xDD121218);
        particles.render(c, width, height);

        c.fill(x0, y0, x0 + W, y0 + TITLE_H, Draw.alpha(accent, 0x70));
        c.fill(x0, y0 + TITLE_H, x0 + W, y0 + TITLE_H + 1, accent);
        c.drawText(textRenderer, "NOVA CLIENT", x0 + 8, y0 + 8, 0xFFFFFFFF, true);
        c.fill(x0 + TAB_W, y0 + TITLE_H + 1, x0 + TAB_W + 1, y0 + H, 0x40FFFFFF);

        for (int i = 0; i < Modules.ALL.size(); i++) {
            Module m = Modules.ALL.get(i);
            int ty = tabY(i);
            if (m == selected) c.fill(x0 + 6, ty, x0 + TAB_W - 6, ty + 18, Draw.alpha(accent, 0x90));
            else if (in(mx, my, x0 + 6, ty, TAB_W - 12, 18)) c.fill(x0 + 6, ty, x0 + TAB_W - 6, ty + 18, 0x30FFFFFF);
            c.drawText(textRenderer, m.name, x0 + 12, ty + 5, 0xFFFFFFFF, false);
            if (m.toggleable) c.fill(x0 + TAB_W - 16, ty + 6, x0 + TAB_W - 10, ty + 12, m.enabled ? 0xFF40E070 : 0xFF50505C);
        }

        c.drawText(textRenderer, selected.name, paneX(), y0 + TITLE_H + 10, accent, true);
        c.drawText(textRenderer, selected.description, paneX(), y0 + TITLE_H + 24, 0xFF9A9AA8, false);
        if (selected.toggleable) {
            c.fill(toggleX(), toggleY(), toggleX() + 54, toggleY() + 16, selected.enabled ? 0xFF2FAE5A : 0xFF4A4A56);
            String t = selected.enabled ? "ENABLED" : "DISABLED";
            c.drawText(textRenderer, t, toggleX() + 27 - textRenderer.getWidth(t) / 2, toggleY() + 4, 0xFFFFFFFF, false);
        }

        for (int i = 0; i < selected.settings.size(); i++) {
            Setting s = selected.settings.get(i);
            int rx = rowX(i), ry = rowY(i);
            c.fill(rx, ry, rx + COL_W, ry + ROW_H, 0xFF1E1E26);
            if (s instanceof Setting.Num n) {
                double t = (n.value - n.min) / (n.max - n.min);
                c.fill(rx, ry, rx + (int) (COL_W * t), ry + ROW_H, Draw.alpha(n.tint != 0 ? n.tint : accent, 0xB0));
                String v = n.integer ? String.valueOf((int) Math.round(n.value)) : String.format("%.1f", n.value);
                c.drawText(textRenderer, n.name, rx + 5, ry + 5, 0xFFFFFFFF, false);
                c.drawText(textRenderer, v, rx + COL_W - 5 - textRenderer.getWidth(v), ry + 5, 0xFFFFFFFF, false);
            } else if (s instanceof Setting.Bool b) {
                c.drawText(textRenderer, b.name, rx + 5, ry + 5, 0xFFFFFFFF, false);
                c.fill(rx + COL_W - 16, ry + 4, rx + COL_W - 6, ry + 14, 0xFF3A3A46);
                if (b.value) c.fill(rx + COL_W - 14, ry + 6, rx + COL_W - 8, ry + 12, accent);
            }
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mx = click.x(), my = click.y();
        if (click.button() != 0) return super.mouseClicked(click, doubled);

        for (int i = 0; i < Modules.ALL.size(); i++)
            if (in(mx, my, x0 + 6, tabY(i), TAB_W - 12, 18)) { selected = Modules.ALL.get(i); return true; }

        if (selected.toggleable && in(mx, my, toggleX(), toggleY(), 54, 16)) {
            selected.setEnabled(!selected.enabled);
            ConfigIO.save();
            return true;
        }
        for (int i = 0; i < selected.settings.size(); i++) {
            if (!in(mx, my, rowX(i), rowY(i), COL_W, ROW_H)) continue;
            Setting s = selected.settings.get(i);
            if (s instanceof Setting.Bool b) { b.value = !b.value; ConfigIO.save(); }
            else if (s instanceof Setting.Num n) { dragging = n; dragIndex = i; applyDrag(mx); }
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (dragging != null) { applyDrag(click.x()); return true; }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (dragging != null) { dragging = null; ConfigIO.save(); return true; }
        return super.mouseReleased(click);
    }

    private void applyDrag(double mx) {
        double t = MathHelper.clamp((mx - rowX(dragIndex)) / COL_W, 0, 1);
        double v = dragging.min + (dragging.max - dragging.min) * t;
        dragging.value = dragging.integer ? Math.round(v) : Math.round(v * 10) / 10.0;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_RIGHT_SHIFT) { close(); return true; }
        return super.keyPressed(input);
    }

    @Override public void removed() { ConfigIO.save(); }
}
