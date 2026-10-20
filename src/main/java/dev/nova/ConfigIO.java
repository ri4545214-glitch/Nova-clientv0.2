package dev.nova;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigIO {
    private ConfigIO() {}

    private static Path file() { return FabricLoader.getInstance().getConfigDir().resolve("novaclient.json"); }

    public static void save() {
        JsonObject o = new JsonObject();
        for (Module m : Modules.ALL) {
            if (m.toggleable) o.addProperty(m.name + ".enabled", m.enabled);
            for (Setting s : m.settings) {
                String k = m.name + "." + s.name;
                if (s instanceof Setting.Bool b) o.addProperty(k, b.value);
                else if (s instanceof Setting.Num n) o.addProperty(k, n.value);
            }
        }
        try { Files.writeString(file(), new GsonBuilder().setPrettyPrinting().create().toJson(o)); }
        catch (IOException ignored) {}
    }

    public static void load() {
        Path f = file();
        if (!Files.exists(f)) return;
        try (Reader r = Files.newBufferedReader(f)) {
            JsonObject o = JsonParser.parseReader(r).getAsJsonObject();
            for (Module m : Modules.ALL) {
                if (m.toggleable && o.has(m.name + ".enabled")) m.enabled = o.get(m.name + ".enabled").getAsBoolean();
                for (Setting s : m.settings) {
                    String k = m.name + "." + s.name;
                    if (!o.has(k)) continue;
                    if (s instanceof Setting.Bool b) b.value = o.get(k).getAsBoolean();
                    else if (s instanceof Setting.Num n) n.value = o.get(k).getAsDouble();
                }
            }
        } catch (Exception ignored) {}
    }
}
