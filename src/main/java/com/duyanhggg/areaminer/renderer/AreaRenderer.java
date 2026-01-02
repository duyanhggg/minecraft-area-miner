package com.duyanhggg.areaminer.renderer;

import com.duyanhggg.areaminer.config.ConfigManager;
import com.duyanhggg.areaminer.config.ModConfig;
import com.duyanhggg.areaminer.mining.MiningArea;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;

public class AreaRenderer {
    private static MiningArea currentArea = null;
    private static boolean renderEnabled = true;
    
    public static void initialize() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(AreaRenderer::render);
    }
    
    public static void setArea(MiningArea area) {
        currentArea = area;
    }
    
    public static void clearArea() {
        currentArea = null;
    }
    
    public static void setRenderEnabled(boolean enabled) {
        renderEnabled = enabled;
    }
    
    public static boolean isRenderEnabled() {
        return renderEnabled;
    }
    
    public static void toggleRender() {
        renderEnabled = !renderEnabled;
    }
    
    private static void render(WorldRenderContext context) {
        if (!renderEnabled || currentArea == null) {
            return;
        }
        
        ModConfig config = ConfigManager.getConfig();
        if (!config.visualizationEnabled) {
            return;
        }
        
        MatrixStack matrices = context.matrixStack();
        Camera camera = context.camera();
        
        matrices.push();
        
        // Translate to camera position
        var cameraPos = camera.getPos();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        
        // Get area bounds
        BlockPos min = currentArea.getMinPos();
        BlockPos max = currentArea.getMaxPos();
        
        int minX = Math.min(min.getX(), max.getX());
        int minY = Math.min(min.getY(), max.getY());
        int minZ = Math.min(min.getZ(), max.getZ());
        int maxX = Math.max(min.getX(), max.getX());
        int maxY = Math.max(min.getY(), max.getY());
        int maxZ = Math.max(min.getZ(), max.getZ());
        
        Box box = new Box(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);
        
        // Setup rendering
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        
        // Extract color components
        int color = config.visualizationColor;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = config.visualizationAlpha;
        
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        // Draw box outline
        drawBoxOutline(buffer, matrix, box, r, g, b, a);
        
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        
        // Restore rendering state
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        
        matrices.pop();
    }
    
    private static void drawBoxOutline(BufferBuilder buffer, Matrix4f matrix, Box box, float r, float g, float b, float a) {
        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;
        
        // Bottom face
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, z1).color(r, g, b, a);
        
        buffer.vertex(matrix, x2, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, z2).color(r, g, b, a);
        
        buffer.vertex(matrix, x2, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y1, z2).color(r, g, b, a);
        
        buffer.vertex(matrix, x1, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);
        
        // Top face
        buffer.vertex(matrix, x1, y2, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z1).color(r, g, b, a);
        
        buffer.vertex(matrix, x2, y2, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
        
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, z2).color(r, g, b, a);
        
        buffer.vertex(matrix, x1, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, z1).color(r, g, b, a);
        
        // Vertical edges
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, z1).color(r, g, b, a);
        
        buffer.vertex(matrix, x2, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z1).color(r, g, b, a);
        
        buffer.vertex(matrix, x2, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
        
        buffer.vertex(matrix, x1, y1, z2).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, z2).color(r, g, b, a);
    }
}
