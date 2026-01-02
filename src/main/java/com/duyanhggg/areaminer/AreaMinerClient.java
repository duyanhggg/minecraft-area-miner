package com.duyanhggg.areaminer;

import com.duyanhggg.areaminer.events.ClientEventHandlers;
import com.duyanhggg.areaminer.gui.MiningScreen;
import com.duyanhggg.areaminer.network.NetworkHandler;
import com.duyanhggg.areaminer.renderer.AreaRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AreaMinerClient implements ClientModInitializer {
    public static KeyBinding openGuiKey;
    public static KeyBinding toggleVisualizationKey;

    @Override
    public void onInitializeClient() {
        AreaMiner.LOGGER.info("Initializing Area Miner client");
        
        // Register key bindings
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.area-miner.open_gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "category.area-miner"
        ));
        
        toggleVisualizationKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.area-miner.toggle_visualization",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "category.area-miner"
        ));
        
        // Register client tick events
        ClientTickEvents.END_CLIENT_TICK.register(ClientEventHandlers::onClientTick);
        
        // Initialize network handlers
        NetworkHandler.registerClientPackets();
        
        // Initialize renderer
        AreaRenderer.initialize();
        
        AreaMiner.LOGGER.info("Area Miner client initialized successfully");
    }
}
