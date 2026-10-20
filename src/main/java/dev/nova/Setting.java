package dev.nova;

public abstract class Setting {
    public final String name;
    protected Setting(String name) { this.name = name; }

    public static final class Bool extends Setting {
        public boolean value;
        public Bool(String name, boolean value) { super(name); this.value = value; }
    }

    public static final class Num extends Setting {
        public double value;
        public final double min, max;
        public final boolean integer;
        public int tint = 0; // optional slider color (RGB)
        public Num(String name, double value, double min, double max, boolean integer) {
            super(name);
            this.value = value; this.min = min; this.max = max; this.integer = integer;
        }
    }
}
