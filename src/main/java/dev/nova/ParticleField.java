package dev.nova;

import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Floating particles for the GUI. Color, speed, count and size come from the Config tab. */
final class ParticleField {
    private static final class P { float x, y, vx, vy, phase; }

    private final List<P> list = new ArrayList<>();
    private final Random rnd = new Random();
    private long last = System.nanoTime();

    private P spawn(int w, int h) {
        P p = new P();
        p.x = rnd.nextFloat() * w; p.y = rnd.nextFloat() * h;
        p.vx = (rnd.nextFloat() - 0.5f) * 0.8f; p.vy = -(0.2f + rnd.nextFloat() * 0.8f);
        p.phase = rnd.nextFloat() * 6.28f;
        return p;
    }

    void render(DrawContext c, int w, int h) {
        long now = System.nanoTime();
        float dt = Math.min(0.1f, (now - last) / 1e9f);
        last = now;

        ConfigModule cfg = Modules.CONFIG;
        int target = (int) cfg.particleCount.value, size = (int) cfg.particleSize.value, col = cfg.particleColor.get();
        float speed = (float) cfg.particleSpeed.value * 30f * dt;
        while (list.size() < target) list.add(spawn(w, h));
        while (list.size() > target) list.remove(list.size() - 1);

        double t = now / 4e8;
        for (P p : list) {
            p.x += p.vx * speed; p.y += p.vy * speed;
            if (p.y < -6) { p.y = h + 6; p.x = rnd.nextFloat() * w; }
            if (p.x < -6) p.x = w + 6; else if (p.x > w + 6) p.x = -6;
            int a = (int) (90 + 70 * Math.sin(p.phase + t));
            int x = (int) p.x, y = (int) p.y;
            c.fill(x, y, x + size, y + size, Draw.alpha(col, a));
        }
    }
}
