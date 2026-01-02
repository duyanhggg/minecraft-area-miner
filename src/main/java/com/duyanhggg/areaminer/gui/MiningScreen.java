package com.duyanhggg.areaminer.gui;

import com.duyanhggg.areaminer.config.ConfigManager;
import com.duyanhggg.areaminer.config.ModConfig;
import com.duyanhggg.areaminer.mining.MiningArea;
import com.duyanhggg.areaminer.network.NetworkHandler;
import com.duyanhggg.areaminer.renderer.AreaRenderer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class MiningScreen extends Screen {
    private TextFieldWidget minXField;
    private TextFieldWidget minYField;
    private TextFieldWidget minZField;
    private TextFieldWidget maxXField;
    private TextFieldWidget maxYField;
    private TextFieldWidget maxZField;
    private TextFieldWidget speedField;
    
    private ButtonWidget startButton;
    private ButtonWidget stopButton;
    private ButtonWidget pauseButton;
    private ButtonWidget previewButton;
    
    private MiningArea currentArea;
    private boolean isPreviewing = false;
    private float currentSpeed = 1.0f;
    
    public MiningScreen() {
        super(Text.literal("Area Miner Configuration"));
        loadLastConfig();
    }
    
    private void loadLastConfig() {
        ModConfig config = ConfigManager.getConfig();
        if (config.autoLoadLastConfig) {
            currentArea = new MiningArea(
                config.lastMinX, config.lastMinY, config.lastMinZ,
                config.lastMaxX, config.lastMaxY, config.lastMaxZ
            );
            currentSpeed = config.lastSpeed;
        } else {
            currentArea = new MiningArea(0, 0, 0, 0, 0, 0);
        }
    }
    
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 40;
        int fieldWidth = 60;
        int spacing = 70;
        
        // Title
        // Position input fields
        this.minXField = new TextFieldWidget(this.textRenderer, centerX - 180, startY, fieldWidth, 20, Text.literal("Min X"));
        this.minXField.setMaxLength(10);
        this.minXField.setText(String.valueOf(currentArea.getMinPos().getX()));
        this.addSelectableChild(this.minXField);
        
        this.minYField = new TextFieldWidget(this.textRenderer, centerX - 180, startY + 30, fieldWidth, 20, Text.literal("Min Y"));
        this.minYField.setMaxLength(10);
        this.minYField.setText(String.valueOf(currentArea.getMinPos().getY()));
        this.addSelectableChild(this.minYField);
        
        this.minZField = new TextFieldWidget(this.textRenderer, centerX - 180, startY + 60, fieldWidth, 20, Text.literal("Min Z"));
        this.minZField.setMaxLength(10);
        this.minZField.setText(String.valueOf(currentArea.getMinPos().getZ()));
        this.addSelectableChild(this.minZField);
        
        this.maxXField = new TextFieldWidget(this.textRenderer, centerX - 60, startY, fieldWidth, 20, Text.literal("Max X"));
        this.maxXField.setMaxLength(10);
        this.maxXField.setText(String.valueOf(currentArea.getMaxPos().getX()));
        this.addSelectableChild(this.maxXField);
        
        this.maxYField = new TextFieldWidget(this.textRenderer, centerX - 60, startY + 30, fieldWidth, 20, Text.literal("Max Y"));
        this.maxYField.setMaxLength(10);
        this.maxYField.setText(String.valueOf(currentArea.getMaxPos().getY()));
        this.addSelectableChild(this.maxYField);
        
        this.maxZField = new TextFieldWidget(this.textRenderer, centerX - 60, startY + 60, fieldWidth, 20, Text.literal("Max Z"));
        this.maxZField.setMaxLength(10);
        this.maxZField.setText(String.valueOf(currentArea.getMaxPos().getZ()));
        this.addSelectableChild(this.maxZField);
        
        // Speed field
        this.speedField = new TextFieldWidget(this.textRenderer, centerX + 60, startY, fieldWidth, 20, Text.literal("Speed"));
        this.speedField.setMaxLength(5);
        this.speedField.setText(String.valueOf(currentSpeed));
        this.addSelectableChild(this.speedField);
        
        // Buttons
        int buttonY = startY + 100;
        
        this.startButton = ButtonWidget.builder(Text.literal("Start Mining"), button -> {
            startMining();
        }).dimensions(centerX - 180, buttonY, 80, 20).build();
        this.addDrawableChild(this.startButton);
        
        this.stopButton = ButtonWidget.builder(Text.literal("Stop"), button -> {
            stopMining();
        }).dimensions(centerX - 90, buttonY, 80, 20).build();
        this.addDrawableChild(this.stopButton);
        
        this.pauseButton = ButtonWidget.builder(Text.literal("Pause"), button -> {
            pauseMining();
        }).dimensions(centerX, buttonY, 80, 20).build();
        this.addDrawableChild(this.pauseButton);
        
        this.previewButton = ButtonWidget.builder(Text.literal("Toggle Preview"), button -> {
            togglePreview();
        }).dimensions(centerX + 90, buttonY, 100, 20).build();
        this.addDrawableChild(this.previewButton);
        
        // Close button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> {
            this.close();
        }).dimensions(centerX - 60, this.height - 30, 120, 20).build());
    }
    
    private void startMining() {
        try {
            int minX = Integer.parseInt(minXField.getText());
            int minY = Integer.parseInt(minYField.getText());
            int minZ = Integer.parseInt(minZField.getText());
            int maxX = Integer.parseInt(maxXField.getText());
            int maxY = Integer.parseInt(maxYField.getText());
            int maxZ = Integer.parseInt(maxZField.getText());
            float speed = Float.parseFloat(speedField.getText());
            
            // Update current area
            currentArea.setCoordinates(minX, minY, minZ, maxX, maxY, maxZ);
            currentSpeed = speed;
            
            // Save to config
            ModConfig config = ConfigManager.getConfig();
            config.updateLastUsed(minX, minY, minZ, maxX, maxY, maxZ, speed, false);
            ConfigManager.saveConfig();
            
            // Send packet to server
            ClientPlayNetworking.send(new NetworkHandler.StartMiningPayload(minX, minY, minZ, maxX, maxY, maxZ, speed));
            
            if (client != null && client.player != null) {
                client.player.sendMessage(Text.literal("Mining started!"), false);
            }
        } catch (NumberFormatException e) {
            if (client != null && client.player != null) {
                client.player.sendMessage(Text.literal("Invalid input values!"), false);
            }
        }
    }
    
    private void stopMining() {
        ClientPlayNetworking.send(new NetworkHandler.StopMiningPayload());
        if (client != null && client.player != null) {
            client.player.sendMessage(Text.literal("Mining stopped!"), false);
        }
    }
    
    private void pauseMining() {
        ClientPlayNetworking.send(new NetworkHandler.PauseMiningPayload());
        if (client != null && client.player != null) {
            client.player.sendMessage(Text.literal("Mining paused!"), false);
        }
    }
    
    private void togglePreview() {
        isPreviewing = !isPreviewing;
        
        if (isPreviewing) {
            try {
                int minX = Integer.parseInt(minXField.getText());
                int minY = Integer.parseInt(minYField.getText());
                int minZ = Integer.parseInt(minZField.getText());
                int maxX = Integer.parseInt(maxXField.getText());
                int maxY = Integer.parseInt(maxYField.getText());
                int maxZ = Integer.parseInt(maxZField.getText());
                
                currentArea.setCoordinates(minX, minY, minZ, maxX, maxY, maxZ);
                AreaRenderer.setArea(currentArea);
                AreaRenderer.setRenderEnabled(true);
            } catch (NumberFormatException e) {
                isPreviewing = false;
            }
        } else {
            AreaRenderer.clearArea();
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        // Draw title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        
        // Draw labels
        int centerX = this.width / 2;
        int startY = 40;
        
        context.drawTextWithShadow(this.textRenderer, "Min Position:", centerX - 240, startY + 5, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "X:", centerX - 210, startY + 5, 0xAAAAAA);
        context.drawTextWithShadow(this.textRenderer, "Y:", centerX - 210, startY + 35, 0xAAAAAA);
        context.drawTextWithShadow(this.textRenderer, "Z:", centerX - 210, startY + 65, 0xAAAAAA);
        
        context.drawTextWithShadow(this.textRenderer, "Max Position:", centerX - 120, startY + 5, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "X:", centerX - 90, startY + 5, 0xAAAAAA);
        context.drawTextWithShadow(this.textRenderer, "Y:", centerX - 90, startY + 35, 0xAAAAAA);
        context.drawTextWithShadow(this.textRenderer, "Z:", centerX - 90, startY + 65, 0xAAAAAA);
        
        context.drawTextWithShadow(this.textRenderer, "Speed:", centerX + 20, startY + 5, 0xFFFFFF);
        
        // Draw info
        int totalBlocks = currentArea.getTotalBlocks();
        context.drawTextWithShadow(this.textRenderer, "Total Blocks: " + totalBlocks, centerX - 180, startY + 140, 0x00FF00);
        context.drawTextWithShadow(this.textRenderer, "Size: " + currentArea.getWidth() + "x" + currentArea.getHeight() + "x" + currentArea.getDepth(), 
            centerX - 180, startY + 155, 0x00FF00);
        
        // Draw text fields
        minXField.render(context, mouseX, mouseY, delta);
        minYField.render(context, mouseX, mouseY, delta);
        minZField.render(context, mouseX, mouseY, delta);
        maxXField.render(context, mouseX, mouseY, delta);
        maxYField.render(context, mouseX, mouseY, delta);
        maxZField.render(context, mouseX, mouseY, delta);
        speedField.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (minXField.mouseClicked(mouseX, mouseY, button)) return true;
        if (minYField.mouseClicked(mouseX, mouseY, button)) return true;
        if (minZField.mouseClicked(mouseX, mouseY, button)) return true;
        if (maxXField.mouseClicked(mouseX, mouseY, button)) return true;
        if (maxYField.mouseClicked(mouseX, mouseY, button)) return true;
        if (maxZField.mouseClicked(mouseX, mouseY, button)) return true;
        if (speedField.mouseClicked(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (minXField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (minYField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (minZField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (maxXField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (maxYField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (maxZField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (speedField.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (minXField.charTyped(chr, modifiers)) return true;
        if (minYField.charTyped(chr, modifiers)) return true;
        if (minZField.charTyped(chr, modifiers)) return true;
        if (maxXField.charTyped(chr, modifiers)) return true;
        if (maxYField.charTyped(chr, modifiers)) return true;
        if (maxZField.charTyped(chr, modifiers)) return true;
        if (speedField.charTyped(chr, modifiers)) return true;
        return super.charTyped(chr, modifiers);
    }
    
    @Override
    public void close() {
        if (isPreviewing) {
            AreaRenderer.clearArea();
        }
        super.close();
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}
