package dev.nova;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;

public final class Draw {
    private Draw() {}

    public static int alpha(int rgb, int a) { return (a << 24) | (rgb & 0xFFFFFF); }

    /** World position -> scaled GUI coordinates, or null if behind the camera. */
    public static double[] project(Vec3d p, Camera cam, double fovDeg, int sw, int sh) {
        Vec3d d = p.subtract(cam.getPos());
        double yaw = Math.toRadians(cam.getYaw()), pitch = Math.toRadians(cam.getPitch());
        Vec3d fwd = new Vec3d(-Math.sin(yaw) * Math.cos(pitch), -Math.sin(pitch), Math.cos(yaw) * Math.cos(pitch));
        Vec3d right = new Vec3d(-Math.cos(yaw), 0, -Math.sin(yaw));
        Vec3d up = right.crossProduct(fwd);
        double z = d.dotProduct(fwd);
        if (z < 0.05) return null;
        double t = Math.tan(Math.toRadians(fovDeg) / 2.0);
        double aspect = (double) sw / sh;
        double nx = d.dotProduct(right) / (z * t * aspect);
        double ny = d.dotProduct(up) / (z * t);
        return new double[]{(nx + 1) * 0.5 * sw, (1 - ny) * 0.5 * sh};
    }

    public static void line(DrawContext c, double x1, double y1, double x2, double y2, int w, int color) {
        double dx = x2 - x1, dy = y2 - y1;
        int n = (int) Math.min(4000, Math.max(1, Math.max(Math.abs(dx), Math.abs(dy)) / w));
        int sw = c.getScaledWindowWidth(), sh = c.getScaledWindowHeight();
        for (int i = 0; i <= n; i++) {
            int x = (int) (x1 + dx * i / n), y = (int) (y1 + dy * i / n);
            if (x < -w || y < -w || x > sw || y > sh) continue;
            c.fill(x, y, x + w, y + w, color);
        }
    }

    public static void rect(DrawContext c, int x1, int y1, int x2, int y2, int w, int color) {
        c.fill(x1, y1, x2, y1 + w, color);
        c.fill(x1, y2 - w, x2, y2, color);
        c.fill(x1, y1, x1 + w, y2, color);
        c.fill(x2 - w, y1, x2, y2, color);
    }
}
