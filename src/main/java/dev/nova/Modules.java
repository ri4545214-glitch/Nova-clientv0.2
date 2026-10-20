package dev.nova;

import java.util.List;

public final class Modules {
    public static final Esp ESP = new Esp();
    public static final Aimbot AIMBOT = new Aimbot();
    public static final Sprint SPRINT = new Sprint();
    public static final Fullbright FULLBRIGHT = new Fullbright();
    public static final Overlay OVERLAY = new Overlay();
    public static final ConfigModule CONFIG = new ConfigModule();
    public static final List<Module> ALL = List.of(ESP, AIMBOT, SPRINT, FULLBRIGHT, OVERLAY, CONFIG);

    private Modules() {}
}
