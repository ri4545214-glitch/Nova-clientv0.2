package dev.nova;

/** Not a toggle: holds the GUI/particle appearance settings (the "Config" tab). */
public final class ConfigModule extends Module {
    public final ColorSetting guiColor = color("GUI", 0x6A5CFF);
    public final ColorSetting particleColor = color("Particle", 0xFFFFFF);
    public final Setting.Num particleSpeed = num("Particle Speed", 1.0, 0.1, 5.0, false);
    public final Setting.Num particleCount = num("Particle Count", 70, 0, 250, true);
    public final Setting.Num particleSize = num("Particle Size", 2, 1, 6, true);

    public ConfigModule() { super("Config", "Change GUI color and particles.", false); }
}
