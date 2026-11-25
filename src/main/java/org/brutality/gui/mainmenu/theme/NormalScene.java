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

public class NormalScene extends MainMenuScene {
    private ShaderEffect shaderEffect;
    private long timestamp = System.currentTimeMillis();
    private CustomFontRenderer drawStringWithShadowComponent;
    private static final int count = 50;
    private float[][] floatValueArray;

    @Override
    public void handleInt(int width2, int height2) {
        this.width2 = width2;
        this.height2 = height2;
        this.shaderEffect = null;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
        this.process();
    }

    private void handleInt(int n, int n2, int n3, Color color) {
        if (n3 > 0) {
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            float f = color.getRed() / 255.0F;
            float f2 = color.getGreen() / 255.0F;
            float f3 = color.getBlue() / 255.0F;
            float f4 = color.getAlpha() / 255.0F * 0.25F;
            int n4 = Math.max(12, n3 * 2);
            GL11.glColor4f(f, f2, f3, f4);
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex2f(n, n2);

            for (int i = 0; i <= n4; i++) {
                float f5 = (float)(i * 2 * Math.PI / n4);
                GL11.glVertex2f(n + (float)Math.cos(f5) * n3 * 2.0F, n2 + (float)Math.sin(f5) * n3 * 2.0F);
            }

            GL11.glEnd();
            float f6 = color.getAlpha() / 255.0F * 0.6F;
            int n5 = Math.max(8, n3);
            GL11.glColor4f(f, f2, f3, f6);
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex2f(n, n2);

            for (int j = 0; j <= n5; j++) {
                float f7 = (float)(j * 2 * Math.PI / n5);
                GL11.glVertex2f(n + (float)Math.cos(f7) * n3, n2 + (float)Math.sin(f7) * n3);
            }

            GL11.glEnd();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableTexture2D();
        }
    }

    private void handleInt(int n, int n2, float f) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        for (int i = 0; i < 50; i++) {
            this.floatValueArray[i][0] = this.floatValueArray[i][0] + this.floatValueArray[i][2] * n * f;
            this.floatValueArray[i][1] = this.floatValueArray[i][1] + this.floatValueArray[i][3] * n2 * f;
            if (this.floatValueArray[i][0] > 1.0F) {
                this.floatValueArray[i][0]--;
            }

            if (this.floatValueArray[i][0] < 0.0F) {
                this.floatValueArray[i][0]++;
            }

            if (this.floatValueArray[i][1] > 1.0F) {
                this.floatValueArray[i][1]--;
            }

            if (this.floatValueArray[i][1] < 0.0F) {
                this.floatValueArray[i][1]++;
            }

            float f2 = this.floatValueArray[i][0] * n;
            float f3 = this.floatValueArray[i][1] * n2;
            float f4 = this.floatValueArray[i][4];
            float f5 = this.floatValueArray[i][5];
            Color color = new Color(200, 210, 230, (int)(f5 * 255.0F));
            this.handleInt((int)f2, (int)f3, (int)f4, color);
        }

        GlStateManager.disableBlend();
    }

    @Override
    public void handleInt(int n, int n2, float f, int n3, int n4) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        float scaleFactor = sr.getScaleFactor();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        if (this.shaderEffect == null) {
            this.shaderEffect = new ShaderEffect("light");
        }

        this.shaderEffect.init();
        this.shaderEffect.handleString("TIME", (float)(System.currentTimeMillis() - this.timestamp) / 1000.0F);
        ShaderEffect.handleShaderEffect(this.shaderEffect, n * scaleFactor, n2 * scaleFactor);
        this.handleInt(n, n2, f);
        GlStateManager.disableBlend();
    }

    public NormalScene() {
        this.process();
    }

    private int computeInt(Color color, Color color2, float f) {
        f = Math.max(0.0F, Math.min(1.0F, f));
        int n = (int)(color.getRed() + (color2.getRed() - color.getRed()) * f);
        int n2 = (int)(color.getGreen() + (color2.getGreen() - color.getGreen()) * f);
        int n3 = (int)(color.getBlue() + (color2.getBlue() - color.getBlue()) * f);
        int n4 = (int)(color.getAlpha() + (color2.getAlpha() - color.getAlpha()) * f);
        return (n << 16) + (n4 << 24 & ~(n << 16)) | n2 << 8 | n3;
    }

    @Override
    public void handleInt(int n, int n2, int n3, int n4, String str, float f, int n5) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        int n6 = 140 + (int)(40.0F * f);
        int n7 = 120 + (int)(120.0F * f);
        Color color = new Color(20, 22, 30, n6);
        Color color2 = new Color(180, 190, 220, n7);
        this.drawRect(n, n2, n + n3, n2 + n4, color.getRGB());
        this.drawRect(n, n2, n + n3, n2 + 1, color2.getRGB());
        this.drawRect(n, n2 + n4 - 1, n + n3, n2 + n4, color2.getRGB());
        this.drawRect(n, n2, n + 1, n2 + n4, color2.getRGB());
        this.drawRect(n + n3 - 1, n2, n + n3, n2 + n4, color2.getRGB());
        Color color3 = new Color(225, 230, 245);
        int n8 = n + n3 / 2 - this.drawStringWithShadowComponent.getStringWidth(str) / 2;
        int n9 = n2 + n4 / 2 - this.drawStringWithShadowComponent.FONT_HEIGHT / 2;
        this.drawStringWithShadowComponent.drawString(str, n8 + 1, n9 + 1, new Color(0, 0, 0, 160).getRGB());
        this.drawStringWithShadowComponent.drawString(str, n8, n9, color3.getRGB());
        GlStateManager.disableBlend();
    }

    @Override
    public String getDescription() {
        return "Clean & minimal";
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
            this.shaderEffect = new ShaderEffect("light");
        }

        this.shaderEffect.init();
        this.shaderEffect.handleString("TIME", (float)(System.currentTimeMillis() - this.timestamp) / 1000.0F);
        ShaderEffect.handleShaderEffect(this.shaderEffect, n, n2, n3, n4);
        GlStateManager.disableBlend();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private void process() {
        this.floatValueArray = new float[50][6];

        for (int i = 0; i < 50; i++) {
            this.floatValueArray[i][0] = (float)Math.random();
            this.floatValueArray[i][1] = (float)Math.random();
            this.floatValueArray[i][2] = 1.0E-4F + (float)Math.random() * 3.0E-4F;
            this.floatValueArray[i][3] = -2.0E-4F - (float)Math.random() * 4.0E-4F;
            this.floatValueArray[i][4] = 1.0F + (float)Math.random() * 2.0F;
            this.floatValueArray[i][5] = 0.2F + (float)Math.random() * 0.4F;
        }
    }

    private void drawGradientRect(int n, int n2, int n3, int n4, int n5, int n6) {
        float f = (n5 >> 24 & 0xFF) / 255.0F;
        float f2 = ((0xFF | ~(n5 >> 16)) - ~(n5 >> 16)) / 255.0F;
        float f3 = (n5 >> 8 & 0xFF) / 255.0F;
        float f4 = (n5 & 0xFF) / 255.0F;
        float f5 = ((0xFF | ~(n6 >> 24)) - ~(n6 >> 24)) / 255.0F;
        float f6 = (n6 >> 16 & 0xFF) / 255.0F;
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

    @Override
    public String getName() {
        return "Normal";
    }
}
