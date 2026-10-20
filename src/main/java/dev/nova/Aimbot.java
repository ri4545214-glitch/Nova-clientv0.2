package dev.nova;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public final class Aimbot extends Module {
    final Setting.Num range = num("Range", 6, 1, 100, false);
    final Setting.Num smooth = num("Smoothness", 8, 1, 50, false);
    final Setting.Num fov = num("FOV", 90, 10, 360, true);
    final Setting.Bool players = bool("Players", true), hostile = bool("Hostile", true), passive = bool("Passive", false);
    final Setting.Bool visibleOnly = bool("Visible Only", true), head = bool("Aim Head", false), onlyAttack = bool("Only While Attacking", false);

    public Aimbot() { super("Aimbot", "Smoothly aims at the nearest target in your FOV.", true); }

    @Override
    public void onTick() {
        ClientPlayerEntity p = mc.player;
        if (p == null || mc.world == null || mc.currentScreen != null) return;
        if (onlyAttack.value && !mc.options.attackKey.isPressed()) return;

        Vec3d eye = p.getEyePos();
        Vec3d bestAim = null;
        double bestDist = Double.MAX_VALUE;

        for (Entity e : mc.world.getEntities()) {
            if (!accepts(e, players, hostile, passive)) continue;
            Vec3d aim = head.value ? e.getEyePos() : e.getBoundingBox().getCenter();
            double d = eye.distanceTo(aim);
            if (d > range.value || d >= bestDist) continue;
            float[] rot = rotations(eye, aim);
            double dyaw = MathHelper.wrapDegrees(rot[0] - p.getYaw()), dpitch = rot[1] - p.getPitch();
            if (Math.hypot(dyaw, dpitch) > fov.value / 2.0) continue;
            if (visibleOnly.value && !canSee(p, eye, aim)) continue;
            bestDist = d; bestAim = aim;
        }
        if (bestAim == null) return;

        float[] rot = rotations(eye, bestAim);
        float s = (float) Math.max(1.0, smooth.value);
        float dyaw = MathHelper.wrapDegrees(rot[0] - p.getYaw());
        float dpitch = rot[1] - p.getPitch();
        p.setYaw(p.getYaw() + dyaw / s);
        p.setPitch(MathHelper.clamp(p.getPitch() + dpitch / s, -90f, 90f));
    }

    private boolean canSee(ClientPlayerEntity p, Vec3d from, Vec3d to) {
        return mc.world.raycast(new RaycastContext(from, to, RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE, p)).getType() == HitResult.Type.MISS;
    }

    private static float[] rotations(Vec3d from, Vec3d to) {
        double dx = to.x - from.x, dy = to.y - from.y, dz = to.z - from.z;
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
        return new float[]{yaw, pitch};
    }
}
