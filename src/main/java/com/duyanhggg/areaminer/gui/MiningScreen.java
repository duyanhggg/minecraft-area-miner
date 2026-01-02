package com.duyanhggg.areaminer.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

/**
 * MiningScreen - Main GUI screen for the Area Miner mod
 * Displays mining progress, statistics, and controls
 */
public class MiningScreen extends Screen {
    
    private static final int SCREEN_WIDTH = 256;
    private static final int SCREEN_HEIGHT = 220;
    private static final ResourceLocation TEXTURE = new ResourceLocation("areaminer", "textures/gui/mining_screen.png");
    
    // Screen positioning
    private int leftPos;
    private int topPos;
    
    // Mining state variables
    private int miningProgress = 0;
    private int maxProgress = 100;
    private long blocksMined = 0;
    private long totalBlocks = 1000;
    private boolean isMining = false;
    private float miningSpeed = 1.0f;
    
    // UI Components
    private MiningButton startButton;
    private MiningButton stopButton;
    private MiningButton settingsButton;
    
    // Time tracking
    private long startTime = 0;
    private long elapsedTime = 0;
    
    public MiningScreen() {
        super(Component.literal("Area Miner - Mining Screen"));
    }
    
    @Override
    protected void init() {
        // Center the screen
        this.leftPos = (this.width - SCREEN_WIDTH) / 2;
        this.topPos = (this.height - SCREEN_HEIGHT) / 2;
        
        // Initialize buttons
        this.startButton = new MiningButton(
            this.leftPos + 20,
            this.topPos + 180,
            60,
            20,
            Component.literal("Start"),
            button -> onStartMining()
        );
        this.addRenderableWidget(this.startButton);
        
        this.stopButton = new MiningButton(
            this.leftPos + 90,
            this.topPos + 180,
            60,
            20,
            Component.literal("Stop"),
            button -> onStopMining()
        );
        this.addRenderableWidget(this.stopButton);
        
        this.settingsButton = new MiningButton(
            this.leftPos + 160,
            this.topPos + 180,
            76,
            20,
            Component.literal("Settings"),
            button -> onOpenSettings()
        );
        this.addRenderableWidget(this.settingsButton);
        
        // Initialize mining state
        if (this.startTime == 0) {
            this.startTime = System.currentTimeMillis();
        }
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // Render background
        this.renderBg(poseStack);
        
        // Draw main panel background
        fill(poseStack, this.leftPos, this.topPos, this.leftPos + SCREEN_WIDTH, this.topPos + SCREEN_HEIGHT, 0xFF1a1a1a);
        
        // Draw border
        fill(poseStack, this.leftPos - 1, this.topPos - 1, this.leftPos + SCREEN_WIDTH + 1, this.topPos + 1, 0xFF8B4513);
        fill(poseStack, this.leftPos - 1, this.topPos - 1, this.leftPos + 1, this.topPos + SCREEN_HEIGHT + 1, 0xFF8B4513);
        fill(poseStack, this.leftPos + SCREEN_WIDTH - 1, this.topPos - 1, this.leftPos + SCREEN_WIDTH + 1, this.topPos + SCREEN_HEIGHT + 1, 0xFF8B4513);
        fill(poseStack, this.leftPos - 1, this.topPos + SCREEN_HEIGHT - 1, this.leftPos + SCREEN_WIDTH + 1, this.topPos + SCREEN_HEIGHT + 1, 0xFF8B4513);
        
        // Draw title
        poseStack.pushPose();
        this.font.draw(poseStack, "Area Miner", this.leftPos + 12, this.topPos + 8, 0xFFFFFF);
        poseStack.popPose();
        
        // Draw status indicator
        int statusColor = isMining ? 0xFF00FF00 : 0xFFFF0000;
        fill(poseStack, this.leftPos + 220, this.topPos + 8, this.leftPos + 230, this.topPos + 16, statusColor);
        String statusText = isMining ? "MINING" : "IDLE";
        this.font.draw(poseStack, statusText, this.leftPos + 12, this.topPos + 25, 0xFFCCCCCC);
        
        // Draw progress bar section
        drawProgressSection(poseStack);
        
        // Draw statistics section
        drawStatisticsSection(poseStack);
        
        // Draw mining info section
        drawMiningInfoSection(poseStack);
        
        // Update elapsed time
        if (isMining) {
            elapsedTime = System.currentTimeMillis() - startTime;
        }
        
        // Call parent render for buttons
        super.render(poseStack, mouseX, mouseY, partialTick);
    }
    
    private void drawProgressSection(PoseStack poseStack) {
        int sectionY = this.topPos + 45;
        
        // Section title
        this.font.draw(poseStack, "Progress", this.leftPos + 12, sectionY, 0xFFFFD700);
        
        // Progress bar background
        fill(poseStack, this.leftPos + 12, sectionY + 15, this.leftPos + SCREEN_WIDTH - 12, sectionY + 30, 0xFF333333);
        
        // Progress bar fill
        int progressWidth = (int) ((blocksMined / (float) totalBlocks) * (SCREEN_WIDTH - 24));
        fill(poseStack, this.leftPos + 12, sectionY + 15, this.leftPos + 12 + progressWidth, sectionY + 30, 0xFF00AA00);
        
        // Progress text
        String progressText = String.format("%.1f%%", (blocksMined / (float) totalBlocks) * 100);
        int textWidth = this.font.width(progressText);
        this.font.draw(poseStack, progressText, this.leftPos + SCREEN_WIDTH / 2 - textWidth / 2, sectionY + 17, 0xFFFFFFFF);
        
        // Blocks mined / total
        String blockCountText = String.format("%d / %d blocks", blocksMined, totalBlocks);
        this.font.draw(poseStack, blockCountText, this.leftPos + 12, sectionY + 35, 0xFFCCCCCC);
    }
    
    private void drawStatisticsSection(PoseStack poseStack) {
        int sectionY = this.topPos + 90;
        
        // Section title
        this.font.draw(poseStack, "Statistics", this.leftPos + 12, sectionY, 0xFFFFD700);
        
        // Mining speed
        String speedText = String.format("Speed: %.2f blocks/sec", miningSpeed);
        this.font.draw(poseStack, speedText, this.leftPos + 12, sectionY + 15, 0xFFCCCCCC);
        
        // Elapsed time
        long seconds = elapsedTime / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        String timeText = String.format("Time: %02d:%02d", minutes, seconds);
        this.font.draw(poseStack, timeText, this.leftPos + 12, sectionY + 28, 0xFFCCCCCC);
        
        // Estimated time remaining
        if (isMining && miningSpeed > 0) {
            long remainingBlocks = totalBlocks - blocksMined;
            long estimatedSeconds = (long) (remainingBlocks / miningSpeed);
            long estMinutes = estimatedSeconds / 60;
            estimatedSeconds = estimatedSeconds % 60;
            String estTimeText = String.format("Est. Time: %02d:%02d", estMinutes, estimatedSeconds);
            this.font.draw(poseStack, estTimeText, this.leftPos + 12, sectionY + 41, 0xFFCCCCCC);
        }
        
        // Efficiency
        String efficiencyText = "Efficiency: 100%";
        this.font.draw(poseStack, efficiencyText, this.leftPos + 12, sectionY + 54, 0xFFCCCCCC);
    }
    
    private void drawMiningInfoSection(PoseStack poseStack) {
        int sectionY = this.topPos + 155;
        
        // Section title
        this.font.draw(poseStack, "Mining Info", this.leftPos + 12, sectionY, 0xFFFFD700);
        
        // Mining mode
        String modeText = "Mode: Normal";
        this.font.draw(poseStack, modeText, this.leftPos + 12, sectionY + 15, 0xFFCCCCCC);
        
        // Range info
        String rangeText = "Range: 64 blocks";
        this.font.draw(poseStack, rangeText, this.leftPos + 130, sectionY + 15, 0xFFCCCCCC);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Simulate mining progress
        if (isMining && blocksMined < totalBlocks) {
            blocksMined += (long) (miningSpeed * 0.5);
            if (blocksMined > totalBlocks) {
                blocksMined = totalBlocks;
            }
            miningProgress = (int) ((blocksMined / (float) totalBlocks) * 100);
        }
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC to close
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public void onClose() {
        super.onClose();
    }
    
    private void onStartMining() {
        if (!isMining) {
            isMining = true;
            startTime = System.currentTimeMillis();
            elapsedTime = 0;
        }
    }
    
    private void onStopMining() {
        if (isMining) {
            isMining = false;
        }
    }
    
    private void onOpenSettings() {
        // Open settings screen
        if (this.minecraft != null) {
            this.minecraft.setScreen(new MiningSettingsScreen(this));
        }
    }
    
    // Getters and setters
    public boolean isMining() {
        return isMining;
    }
    
    public void setMining(boolean mining) {
        isMining = mining;
    }
    
    public long getBlocksMined() {
        return blocksMined;
    }
    
    public void setBlocksMined(long blocks) {
        blocksMined = blocks;
    }
    
    public long getTotalBlocks() {
        return totalBlocks;
    }
    
    public void setTotalBlocks(long blocks) {
        totalBlocks = blocks;
    }
    
    public float getMiningSpeed() {
        return miningSpeed;
    }
    
    public void setMiningSpeed(float speed) {
        miningSpeed = speed;
    }
    
    public int getProgress() {
        return miningProgress;
    }
    
    public void setProgress(int progress) {
        miningProgress = progress;
    }
    
    public long getElapsedTime() {
        return elapsedTime;
    }
}
