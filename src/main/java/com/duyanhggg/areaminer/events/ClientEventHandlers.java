package com.duyanhggg.areaminer.events;

import com.duyanhggg.areaminer.AreaMinerClient;
import com.duyanhggg.areaminer.gui.MiningScreen;
import com.duyanhggg.areaminer.renderer.AreaRenderer;
import net.minecraft.client.MinecraftClient;

public class ClientEventHandlers {
    
    public static void onClientTick(MinecraftClient client) {
        // Check for key presses
        while (AreaMinerClient.openGuiKey.wasPressed()) {
            if (client.currentScreen == null) {
                client.setScreen(new MiningScreen());
            }
        }
        
        while (AreaMinerClient.toggleVisualizationKey.wasPressed()) {
            AreaRenderer.toggleRender();
            
            // Send feedback to player
            if (client.player != null) {
                String message = AreaRenderer.isRenderEnabled() 
                    ? "Area visualization enabled" 
                    : "Area visualization disabled";
                client.player.sendMessage(
                    net.minecraft.text.Text.literal(message),
                    true // Overlay
                );
            }
        }
    }
}
