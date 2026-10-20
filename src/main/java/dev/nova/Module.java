package dev.nova;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    public final String name, description;
    public final boolean toggleable;
    public boolean enabled;
    public final List<Setting> settings = new ArrayList<>();

    protected Module(String name, String description, boolean toggleable) {
        this.name = name; this.description = description; this.toggleable = toggleable;
    }

    public void setEnabled(boolean v) {
        if (v == enabled) return;
        enabled = v;
        if (v) onEnable(); else onDisable();
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}
    public void onHud(DrawContext ctx, float tickDelta) {}

    Setting.Bool bool(String n, boolean d) { Setting.Bool s = new Setting.Bool(n, d); settings.add(s); return s; }
    Setting.Num num(String n, double d, double min, double max, boolean i) {
        Setting.Num s = new Setting.Num(n, d, min, max, i); settings.add(s); return s;
    }
    ColorSetting color(String label, int rgb) { return new ColorSetting(this, label, rgb); }

    /** Shared target filter for ESP and Aimbot. */
    protected boolean accepts(Entity e, Setting.Bool players, Setting.Bool hostile, Setting.Bool passive) {
        if (e == mc.player || !(e instanceof LivingEntity le) || !le.isAlive() || le.isSpectator()) return false;
        if (e instanceof PlayerEntity) return players.value;
        if (e instanceof Monster) return hostile.value;
        return e instanceof PassiveEntity && passive.value;
    }
}
