package dev.nova;

public final class Sprint extends Module {
    public Sprint() { super("Sprint", "Sprints automatically while moving forward.", true); }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (mc.options.forwardKey.isPressed() && !mc.player.isSneaking() && !mc.player.horizontalCollision
                && mc.player.getHungerManager().getFoodLevel() > 6) {
            mc.player.setSprinting(true);
        }
    }
}
