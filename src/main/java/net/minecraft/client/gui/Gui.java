package net.minecraft.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.brutality.gui.chat.ChatComponent;
import org.brutality.utils.render.DisableDepthHelper;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.opengl.GL11;

public class Gui {
    public static final ResourceLocation optionsBackground = new ResourceLocation("textures/gui/options_background.png");
    public static final ResourceLocation statIcons = new ResourceLocation("textures/gui/container/stats_icons.png");
    public static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
    protected float zLevel;

    public static void drawRect2(double x, double y, double width, double height, int color) {
        ResetColorHelper.resetColor();
        ResetColorHelper.handleFloat2(0.0F);
        DisableDepthHelper.handleFlag(true);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(x, y, 0.0).color(color).func_181675_d();
        worldRenderer.func_181662_b(x, y + height, 0.0).color(color).func_181675_d();
        worldRenderer.func_181662_b(x + width, y + height, 0.0).color(color).func_181675_d();
        worldRenderer.func_181662_b(x + width, y, 0.0).color(color).func_181675_d();
        tessellator.draw();
        DisableDepthHelper.process3();
    }

    protected void drawHorizontalLine(int startX, int endX, int y, int color) {
        if (endX < startX) {
            int n = startX;
            startX = endX;
            endX = n;
        }

        drawRect(startX, y, endX + 1, y + 1, color);
    }

    protected void drawVerticalLine(int x, int startY, int endY, int color) {
        if (endY < startY) {
            int n = startY;
            startY = endY;
            endY = n;
        }

        drawRect(x, startY + 1, x + 1, endY, color);
    }

    public static void drawRect(int left, int top, int right, int bottom, int color) {
        if (left < right) {
            int n = left;
            left = right;
            right = n;
        }

        if (top < bottom) {
            int n2 = top;
            top = bottom;
            bottom = n2;
        }

        float f = (color >> 24 & 0xFF) / 255.0F;
        float f2 = (color >> 16 & 0xFF) / 255.0F;
        float f3 = (color >> 8 & 0xFF) / 255.0F;
        float f4 = (color & 0xFF) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.color(f2, f3, f4, f);
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(left, bottom, 0.0).func_181675_d();
        worldRenderer.func_181662_b(right, bottom, 0.0).func_181675_d();
        worldRenderer.func_181662_b(right, top, 0.0).func_181675_d();
        worldRenderer.func_181662_b(left, top, 0.0).func_181675_d();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        float f = (startColor >> 24 & 0xFF) / 255.0F;
        float f2 = (startColor >> 16 & 0xFF) / 255.0F;
        float f3 = (startColor >> 8 & 0xFF) / 255.0F;
        float f4 = (startColor & 0xFF) / 255.0F;
        float f5 = (endColor >> 24 & 0xFF) / 255.0F;
        float f6 = (endColor >> 16 & 0xFF) / 255.0F;
        float f7 = (endColor >> 8 & 0xFF) / 255.0F;
        float f8 = (endColor & 0xFF) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        worldRenderer.func_181662_b(right, top, this.zLevel).func_181666_a(f2, f3, f4, f).func_181675_d();
        worldRenderer.func_181662_b(left, top, this.zLevel).func_181666_a(f2, f3, f4, f).func_181675_d();
        worldRenderer.func_181662_b(left, bottom, this.zLevel).func_181666_a(f6, f7, f8, f5).func_181675_d();
        worldRenderer.func_181662_b(right, bottom, this.zLevel).func_181666_a(f6, f7, f8, f5).func_181675_d();
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        FontRenderer fontRenderer = ChatComponent.computeFontRenderer(fontRendererIn);
        fontRenderer.drawStringWithShadow(text, x - fontRenderer.getStringWidth(text) / 2, y, color);
    }

    public void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        ChatComponent.computeFontRenderer(fontRendererIn).drawStringWithShadow(text, x, y, color);
    }

    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        float f = 0.00390625F;
        float f2 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        worldRenderer.func_181662_b(x + 0, y + height, this.zLevel).func_181673_a((textureX + 0) * f, (textureY + height) * f2).func_181675_d();
        worldRenderer.func_181662_b(x + width, y + height, this.zLevel).func_181673_a((textureX + width) * f, (textureY + height) * f2).func_181675_d();
        worldRenderer.func_181662_b(x + width, y + 0, this.zLevel).func_181673_a((textureX + width) * f, (textureY + 0) * f2).func_181675_d();
        worldRenderer.func_181662_b(x + 0, y + 0, this.zLevel).func_181673_a((textureX + 0) * f, (textureY + 0) * f2).func_181675_d();
        tessellator.draw();
    }

    public void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
        float f = 0.00390625F;
        float f2 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        worldRenderer.func_181662_b(xCoord + 0.0F, yCoord + maxV, this.zLevel).func_181673_a((minU + 0) * f, (minV + maxV) * f2).func_181675_d();
        worldRenderer.func_181662_b(xCoord + maxU, yCoord + maxV, this.zLevel).func_181673_a((minU + maxU) * f, (minV + maxV) * f2).func_181675_d();
        worldRenderer.func_181662_b(xCoord + maxU, yCoord + 0.0F, this.zLevel).func_181673_a((minU + maxU) * f, (minV + 0) * f2).func_181675_d();
        worldRenderer.func_181662_b(xCoord + 0.0F, yCoord + 0.0F, this.zLevel).func_181673_a((minU + 0) * f, (minV + 0) * f2).func_181675_d();
        tessellator.draw();
    }

    public void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        worldRenderer.func_181662_b(xCoord + 0, yCoord + heightIn, this.zLevel).func_181673_a(textureSprite.getMinU(), textureSprite.getMaxV()).func_181675_d();
        worldRenderer.func_181662_b(xCoord + widthIn, yCoord + heightIn, this.zLevel).func_181673_a(textureSprite.getMaxU(), textureSprite.getMaxV()).func_181675_d();
        worldRenderer.func_181662_b(xCoord + widthIn, yCoord + 0, this.zLevel).func_181673_a(textureSprite.getMaxU(), textureSprite.getMinV()).func_181675_d();
        worldRenderer.func_181662_b(xCoord + 0, yCoord + 0, this.zLevel).func_181673_a(textureSprite.getMinU(), textureSprite.getMinV()).func_181675_d();
        tessellator.draw();
    }

    public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
        float f = 1.0F / textureWidth;
        float f2 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        worldRenderer.func_181662_b(x, y + height, 0.0).func_181673_a(u * f, (v + height) * f2).func_181675_d();
        worldRenderer.func_181662_b(x + width, y + height, 0.0).func_181673_a((u + width) * f, (v + height) * f2).func_181675_d();
        worldRenderer.func_181662_b(x + width, y, 0.0).func_181673_a((u + width) * f, v * f2).func_181675_d();
        worldRenderer.func_181662_b(x, y, 0.0).func_181673_a(u * f, v * f2).func_181675_d();
        tessellator.draw();
    }

    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f2 = 1.0F / tileHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        worldRenderer.func_181662_b(x, y + height, 0.0).func_181673_a(u * f, (v + vHeight) * f2).func_181675_d();
        worldRenderer.func_181662_b(x + width, y + height, 0.0).func_181673_a((u + uWidth) * f, (v + vHeight) * f2).func_181675_d();
        worldRenderer.func_181662_b(x + width, y, 0.0).func_181673_a((u + uWidth) * f, v * f2).func_181675_d();
        worldRenderer.func_181662_b(x, y, 0.0).func_181673_a(u * f, v * f2).func_181675_d();
        tessellator.draw();
    }
}
