package dev.nova;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class NovaClient implements ClientModInitializer {
    private static KeyBinding openGui;

    @Override
    public void onInitializeClient() {
        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("novaclient", "main"));
        openGui = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.novaclient.open_gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, category));

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> ConfigIO.load());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGui.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new ClickGuiScreen());
            }
            for (Module m : Modules.ALL) if (m.enabled) m.onTick();
        });

        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.of("novaclient", "hud"), (ctx, tick) -> {
            float td = tick.getTickProgress(false);
            for (Module m : Modules.ALL) if (m.enabled) m.onHud(ctx, td);
        });
    }
}
