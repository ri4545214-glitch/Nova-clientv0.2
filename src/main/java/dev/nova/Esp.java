package dev.nova;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/** Draws boxes/tracers on the HUD layer, so they show through walls by nature. */
public final class Esp extends Module {
    final Setting.Bool players = bool("Players", true), hostile = bool("Hostile", true), passive = bool("Passive", false);
    final Setting.Bool names = bool("Names", true), boxes = bool("Boxes", true), box3d = bool("3D Box", false), tracers = bool("Tracers", true);
    final Setting.Num range = num("Range", 64, 8, 128, true), width = num("Line Width", 2, 1, 4, true);
    final ColorSetting boxColor = color("Box", 0xFF4040), lineColor = color("Line", 0x40FFFF);

    public Esp() { super("ESP", "See players and mobs through walls.", true); }

    @Override
    public void onHud(DrawContext c, float td) {
        if (mc.world == null || mc.player == null) return;
        Camera cam = mc.gameRenderer.getCamera();
        int sw = c.getScaledWindowWidth(), sh = c.getScaledWindowHeight();
        double fov = mc.options.getFov().getValue();
        int bc = boxColor.get(), lc = lineColor.get(), w = (int) width.value;

        for (Entity e : mc.world.getEntities()) {
            if (!accepts(e, players, hostile, passive)) continue;
            float dist = mc.player.distanceTo(e);
            if (dist > range.value) continue;

            Vec3d pos = e.getLerpedPos(td);
            Box bb = e.getBoundingBox().offset(pos.x - e.getX(), pos.y - e.getY(), pos.z - e.getZ());

            if (tracers.value) {
                double[] s = Draw.project(bb.getCenter(), cam, fov, sw, sh);
                if (s != null) Draw.line(c, sw / 2.0, sh, s[0], s[1], w, lc);
            }
            if (!boxes.value && !names.value) continue;

            double[][] pts = new double[8][];
            double minX = 1e9, minY = 1e9, maxX = -1e9, maxY = -1e9;
            boolean ok = true;
            for (int i = 0; i < 8 && ok; i++) {
                Vec3d v = new Vec3d((i & 1) == 0 ? bb.minX : bb.maxX, (i & 2) == 0 ? bb.minY : bb.maxY, (i & 4) == 0 ? bb.minZ : bb.maxZ);
                pts[i] = Draw.project(v, cam, fov, sw, sh);
                if (pts[i] == null) { ok = false; break; }
                minX = Math.min(minX, pts[i][0]); maxX = Math.max(maxX, pts[i][0]);
                minY = Math.min(minY, pts[i][1]); maxY = Math.max(maxY, pts[i][1]);
            }
            if (!ok) continue;

            if (boxes.value) {
                if (box3d.value) {
                    for (int i = 0; i < 8; i++)
                        for (int bit = 1; bit <= 4; bit <<= 1)
                            if ((i & bit) == 0) Draw.line(c, pts[i][0], pts[i][1], pts[i | bit][0], pts[i | bit][1], w, bc);
                } else {
                    Draw.rect(c, (int) minX, (int) minY, (int) maxX, (int) maxY, w, bc);
                }
            }
            if (names.value) {
                String label = e.getName().getString() + " " + (int) dist + "m";
                int tw = mc.textRenderer.getWidth(label);
                c.drawText(mc.textRenderer, label, (int) ((minX + maxX) / 2) - tw / 2, (int) minY - 10, 0xFFFFFFFF, true);
            }
        }
    }
}
