package org.brutality.gui.mainmenu.theme;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.gui.mainmenu.base.MainMenuScene;
import org.brutality.utils.render.shader.ShaderEffect;
import org.lwjgl.opengl.GL11;

public class FireScene extends MainMenuScene {
    private ShaderEffect shaderEffect;
    private long timestamp = System.currentTimeMillis();
    private CustomFontRenderer drawStringWithShadowComponent;

    @Override
    public void handleInt(int n, int n2, float f, int n3, int n4) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        float scaleFactor = sr.getScaleFactor();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        if (this.shaderEffect == null) {
            this.shaderEffect = new ShaderEffect("fire");
        }

        this.shaderEffect.init();
        this.shaderEffect.handleString("TIME", (float)(System.currentTimeMillis() - this.timestamp) / 1000.0F);
        ShaderEffect.handleShaderEffect(this.shaderEffect, n * scaleFactor, n2 * scaleFactor);
        GlStateManager.disableBlend();
    }

    @Override
    public void handleInt(int width2, int height2) {
        this.width2 = width2;
        this.height2 = height2;
        this.shaderEffect = null;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
    }

    @Override
    public void handleInt(int n, int n2, int n3, int n4, String str, float f, int n5) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        int n6 = 150 + (int)(40.0F * f);
        int n7 = 140 + (int)(115.0F * f);
        Color color = new Color(28, 6, 4, n6);
        Color color2 = new Color(255, 110, 30, n7);
        this.drawRect(n, n2, n + n3, n2 + n4, color.getRGB());
        this.drawRect(n, n2, n + n3, n2 + 1, color2.getRGB());
        this.drawRect(n, n2 + n4 - 1, n + n3, n2 + n4, color2.getRGB());
        this.drawRect(n, n2, n + 1, n2 + n4, color2.getRGB());
        this.drawRect(n + n3 - 1, n2, n + n3, n2 + n4, color2.getRGB());
        short s = 255;
        int n8 = 200 + (int)(40.0F * f);
        int n9 = 150 + (int)(40.0F * f);
        int rgb = new Color(s, n8, n9).getRGB();
        int n10 = n + n3 / 2 - this.drawStringWithShadowComponent.getStringWidth(str) / 2;
        int n11 = n2 + n4 / 2 - this.drawStringWithShadowComponent.FONT_HEIGHT / 2;
        this.drawStringWithShadowComponent.drawString(str, n10 + 1, n11 + 1, new Color(0, 0, 0, 160).getRGB());
        this.drawStringWithShadowComponent.drawString(str, n10, n11, rgb);
        GlStateManager.disableBlend();
    }

    @Override
    public String getName() {
        return "Fire";
    }

    @Override
    public String getDescription() {
        return "Intense flames rising from the depths";
    }

    private void drawGradientRect(int n, int n2, int n3, int n4, int n5, int n6) {
        float f = ((0xFF | ~(n5 >> 24)) - ~(n5 >> 24)) / 255.0F;
        float f2 = ((0xFF | ~(n5 >> 16)) - ~(n5 >> 16)) / 255.0F;
        float f3 = ((0xFF | ~(n5 >> 8)) - ~(n5 >> 8)) / 255.0F;
        float f4 = (n5 & 0xFF) / 255.0F;
        float f5 = (n6 >> 24 & 0xFF) / 255.0F;
        float f6 = ((0xFF | ~(n6 >> 16)) - ~(n6 >> 16)) / 255.0F;
        float f7 = (n6 >> 8 & 0xFF) / 255.0F;
        float f8 = (n6 & 0xFF) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(n3, n2, 0.0).func_181666_a(f2, f3, f4, f).func_181675_d();
        worldRenderer.func_181662_b(n, n2, 0.0).func_181666_a(f2, f3, f4, f).func_181675_d();
        worldRenderer.func_181662_b(n, n4, 0.0).func_181666_a(f6, f7, f8, f5).func_181675_d();
        worldRenderer.func_181662_b(n3, n4, 0.0).func_181666_a(f6, f7, f8, f5).func_181675_d();
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    @Override
    public void handleInt(int n, int n2, int n3, int n4, float f) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int scaledHeight = sr.getScaledHeight();
        int scaleFactor = sr.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(n * scaleFactor, (scaledHeight - n2 - n4) * scaleFactor, n3 * scaleFactor, n4 * scaleFactor);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        if (this.shaderEffect == null) {
            this.shaderEffect = new ShaderEffect("fire");
        }

        this.shaderEffect.init();
        this.shaderEffect.handleString("TIME", (float)(System.currentTimeMillis() - this.timestamp) / 1000.0F);
        ShaderEffect.handleShaderEffect(this.shaderEffect, n, n2, n3, n4);
        GlStateManager.disableBlend();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private void drawRect(int n, int n2, int n3, int n4, int n5) {
        if (n > n3) {
            int n6 = n;
            n = n3;
            n3 = n6;
        }

        if (n2 > n4) {
            int n7 = n2;
            n2 = n4;
            n4 = n7;
        }

        float f = (n5 >> 24 & 0xFF) / 255.0F;
        float f2 = ((0xFF | ~(n5 >> 16)) - ~(n5 >> 16)) / 255.0F;
        float f3 = (n5 >> 8 & 0xFF) / 255.0F;
        float f4 = (n5 & 0xFF) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n, n4);
        GL11.glVertex2f(n3, n4);
        GL11.glVertex2f(n3, n2);
        GL11.glVertex2f(n, n2);
        GL11.glEnd();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
    }

    private int computeInt(Color color, Color color2, float f) {
        f = Math.max(0.0F, Math.min(1.0F, f));
        int n = (int)(color.getRed() + (color2.getRed() - color.getRed()) * f);
        int n2 = (int)(color.getGreen() + (color2.getGreen() - color.getGreen()) * f);
        int n3 = (int)(color.getBlue() + (color2.getBlue() - color.getBlue()) * f);
        int n4 = (int)(color.getAlpha() + (color2.getAlpha() - color.getAlpha()) * f);
        return (n << 16) + (n4 << 24 & ~(n << 16)) | n2 << 8 | n3;
    }
}
