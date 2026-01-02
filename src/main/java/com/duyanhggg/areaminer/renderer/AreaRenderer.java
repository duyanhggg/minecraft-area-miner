package com.duyanhggg.areaminer.renderer;

import com.duyanhggg.areaminer.util.BlockPosition;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * Renderer for visualizing mining areas in the game world.
 * Provides functionality to draw outlines and highlight selected areas.
 */
public class AreaRenderer {

    /**
     * Draws a box outline between two block positions.
     * Used for visualizing the selected mining area.
     *
     * @param startPos the starting block position (one corner of the box)
     * @param endPos   the ending block position (opposite corner of the box)
     * @param red      red color component (0.0 - 1.0)
     * @param green    green color component (0.0 - 1.0)
     * @param blue     blue color component (0.0 - 1.0)
     * @param alpha    alpha/transparency component (0.0 - 1.0)
     * @param lineWidth width of the lines in pixels
     */
    public static void drawBoxOutline(BlockPosition startPos, BlockPosition endPos, 
                                      float red, float green, float blue, float alpha, float lineWidth) {
        if (startPos == null || endPos == null) {
            return;
        }

        // Get the minimum and maximum coordinates
        int minX = Math.min(startPos.getX(), endPos.getX());
        int minY = Math.min(startPos.getY(), endPos.getY());
        int minZ = Math.min(startPos.getZ(), endPos.getZ());
        
        int maxX = Math.max(startPos.getX(), endPos.getX()) + 1;
        int maxY = Math.max(startPos.getY(), endPos.getY()) + 1;
        int maxZ = Math.max(startPos.getZ(), endPos.getZ()) + 1;

        // Enable line rendering
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(lineWidth);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        // Start drawing lines
        bufferBuilder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);

        // Draw the 12 edges of the box
        // Bottom face (Y = minY)
        drawLine(bufferBuilder, minX, minY, minZ, maxX, minY, minZ, red, green, blue, alpha);
        drawLine(bufferBuilder, maxX, minY, minZ, maxX, minY, maxZ, red, green, blue, alpha);
        drawLine(bufferBuilder, maxX, minY, maxZ, minX, minY, maxZ, red, green, blue, alpha);
        drawLine(bufferBuilder, minX, minY, maxZ, minX, minY, minZ, red, green, blue, alpha);

        // Top face (Y = maxY)
        drawLine(bufferBuilder, minX, maxY, minZ, maxX, maxY, minZ, red, green, blue, alpha);
        drawLine(bufferBuilder, maxX, maxY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        drawLine(bufferBuilder, maxX, maxY, maxZ, minX, maxY, maxZ, red, green, blue, alpha);
        drawLine(bufferBuilder, minX, maxY, maxZ, minX, maxY, minZ, red, green, blue, alpha);

        // Vertical edges connecting bottom and top
        drawLine(bufferBuilder, minX, minY, minZ, minX, maxY, minZ, red, green, blue, alpha);
        drawLine(bufferBuilder, maxX, minY, minZ, maxX, maxY, minZ, red, green, blue, alpha);
        drawLine(bufferBuilder, maxX, minY, maxZ, maxX, maxY, maxZ, red, green, blue, alpha);
        drawLine(bufferBuilder, minX, minY, maxZ, minX, maxY, maxZ, red, green, blue, alpha);

        tessellator.draw();

        // Restore OpenGL state
        GL11.glLineWidth(1.0f);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Helper method to draw a single line in the buffer.
     *
     * @param bufferBuilder the buffer builder to add vertices to
     * @param x1            starting X coordinate
     * @param y1            starting Y coordinate
     * @param z1            starting Z coordinate
     * @param x2            ending X coordinate
     * @param y2            ending Y coordinate
     * @param z2            ending Z coordinate
     * @param red           red color component (0.0 - 1.0)
     * @param green         green color component (0.0 - 1.0)
     * @param blue          blue color component (0.0 - 1.0)
     * @param alpha         alpha/transparency component (0.0 - 1.0)
     */
    private static void drawLine(BufferBuilder bufferBuilder, 
                                 double x1, double y1, double z1,
                                 double x2, double y2, double z2,
                                 float red, float green, float blue, float alpha) {
        bufferBuilder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
    }

    /**
     * Draws a filled box between two block positions.
     * Used for visualizing the selected mining area with transparency.
     *
     * @param startPos the starting block position (one corner of the box)
     * @param endPos   the ending block position (opposite corner of the box)
     * @param red      red color component (0.0 - 1.0)
     * @param green    green color component (0.0 - 1.0)
     * @param blue     blue color component (0.0 - 1.0)
     * @param alpha    alpha/transparency component (0.0 - 1.0)
     */
    public static void drawFilledBox(BlockPosition startPos, BlockPosition endPos,
                                     float red, float green, float blue, float alpha) {
        if (startPos == null || endPos == null) {
            return;
        }

        // Get the minimum and maximum coordinates
        int minX = Math.min(startPos.getX(), endPos.getX());
        int minY = Math.min(startPos.getY(), endPos.getY());
        int minZ = Math.min(startPos.getZ(), endPos.getZ());
        
        int maxX = Math.max(startPos.getX(), endPos.getX()) + 1;
        int maxY = Math.max(startPos.getY(), endPos.getY()) + 1;
        int maxZ = Math.max(startPos.getZ(), endPos.getZ()) + 1;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        // Draw filled quads
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // Bottom face
        drawQuad(bufferBuilder, minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ, 
                 red, green, blue, alpha);

        // Top face
        drawQuad(bufferBuilder, minX, maxY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, minX, maxY, maxZ, 
                 red, green, blue, alpha);

        // Front face (Z = minZ)
        drawQuad(bufferBuilder, minX, minY, minZ, maxX, minY, minZ, maxX, maxY, minZ, minX, maxY, minZ, 
                 red, green, blue, alpha);

        // Back face (Z = maxZ)
        drawQuad(bufferBuilder, minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ, 
                 red, green, blue, alpha);

        // Left face (X = minX)
        drawQuad(bufferBuilder, minX, minY, minZ, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ, 
                 red, green, blue, alpha);

        // Right face (X = maxX)
        drawQuad(bufferBuilder, maxX, minY, minZ, maxX, minY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, 
                 red, green, blue, alpha);

        tessellator.draw();

        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Helper method to draw a single quad (4 vertices) in the buffer.
     *
     * @param bufferBuilder the buffer builder to add vertices to
     * @param x1            first vertex X coordinate
     * @param y1            first vertex Y coordinate
     * @param z1            first vertex Z coordinate
     * @param x2            second vertex X coordinate
     * @param y2            second vertex Y coordinate
     * @param z2            second vertex Z coordinate
     * @param x3            third vertex X coordinate
     * @param y3            third vertex Y coordinate
     * @param z3            third vertex Z coordinate
     * @param x4            fourth vertex X coordinate
     * @param y4            fourth vertex Y coordinate
     * @param z4            fourth vertex Z coordinate
     * @param red           red color component (0.0 - 1.0)
     * @param green         green color component (0.0 - 1.0)
     * @param blue          blue color component (0.0 - 1.0)
     * @param alpha         alpha/transparency component (0.0 - 1.0)
     */
    private static void drawQuad(BufferBuilder bufferBuilder,
                                 double x1, double y1, double z1,
                                 double x2, double y2, double z2,
                                 double x3, double y3, double z3,
                                 double x4, double y4, double z4,
                                 float red, float green, float blue, float alpha) {
        bufferBuilder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(x3, y3, z3).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(x4, y4, z4).color(red, green, blue, alpha).endVertex();
    }
}
