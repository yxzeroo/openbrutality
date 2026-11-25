package org.brutality.gui.mainmenu.theme;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.gui.mainmenu.base.MainMenuScene;
import org.brutality.utils.render.GlStateGuard;
import org.brutality.utils.render.shader.ShaderEffect;
import org.lwjgl.opengl.GL11;

public class OceanScene extends MainMenuScene {
    private ShaderEffect shaderEffect;
    private boolean flag;
    private long timestamp = System.currentTimeMillis();
    private CustomFontRenderer drawStringWithShadowComponent;

    private void drawGradientRect(int n, int n2, int n3, int n4, int n5, int n6) {
        float f = (n5 >> 24 & 0xFF) / 255.0F;
        float f2 = (n5 >> 16 & 0xFF) / 255.0F;
        float f3 = (n5 >> 8 & 0xFF) / 255.0F;
        float f4 = ((0xFF | ~n5) - ~n5) / 255.0F;
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

    private boolean checkState() {
        if (this.flag) {
            return false;
        }
        try {
            if (this.shaderEffect == null || !this.shaderEffect.isValid()) {
                this.shaderEffect = new ShaderEffect("mainmenu");
            }

            this.shaderEffect.init();
            this.shaderEffect.handleString("TIME", (float)(System.currentTimeMillis() - this.timestamp) / 1000.0F);
            return true;
        } catch (RuntimeException e) {
            this.flag = true;
            return false;
        }
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

        float f = ((0xFF | ~(n5 >> 24)) - ~(n5 >> 24)) / 255.0F;
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
        return "Ocean";
    }

    private int computeInt(Color color, Color color2, float f) {
        f = Math.max(0.0F, Math.min(1.0F, f));
        int n = (int)(color.getRed() + (color2.getRed() - color.getRed()) * f);
        int n2 = (int)(color.getGreen() + (color2.getGreen() - color.getGreen()) * f);
        int n3 = (int)(color.getBlue() + (color2.getBlue() - color.getBlue()) * f);
        int n4 = (int)(color.getAlpha() + (color2.getAlpha() - color.getAlpha()) * f);
        return n4 << 24 | n << 16 | n2 << 8 | n3;
    }

    @Override
    public void handleInt(int n, int n2, int n3, int n4, float f) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            if (this.checkState()) {
                ShaderEffect.handleShaderEffect(this.shaderEffect, n, n2, n3, n4);
            } else {
                Gui.drawRect(n, n2, n + n3, n2 + n4, -15453112);
            }
        }
    }

    @Override
    public void handleInt(int n, int n2, float f, int n3, int n4) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            if (this.checkState()) {
                ShaderEffect.handleShaderEffect(this.shaderEffect, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            } else {
                Gui.drawRect(0, 0, n, n2, -15453112);
            }
        }
    }

    @Override
    public String getDescription() {
        return "Calm waters with gentle waves";
    }

    @Override
    public void dispose() {
        if (this.shaderEffect != null) {
            this.shaderEffect.delete();
            this.shaderEffect = null;
        }

        this.flag = false;
    }

    @Override
    public void handleInt(int width2, int height2) {
        this.width2 = width2;
        this.height2 = height2;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
    }

    @Override
    public void handleInt(int n, int n2, int n3, int n4, String str, float f, int n5) {
        Color color = new Color(0, 20, 40, 150);
        Color color2 = new Color(10, 40, 70, 190);
        int n6 = this.computeInt(color, color2, f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.drawRect(n, n2, n + n3, n2 + n4, n6);
        int n7 = Math.max(2, n4 / 5);
        Color color3 = new Color(200, 230, 255, 40 + (int)(40.0F * f));
        Color color4 = new Color(200, 230, 255, 0);
        this.drawGradientRect(n, n2, n + n3, n2 + n7, color3.getRGB(), color4.getRGB());
        int n8 = 80 + (int)(140.0F * f);
        Color color5 = new Color(50, 180, 220, n8);
        this.drawRect(n + 2, n2 + n4 - 2, n + n3 - 2, n2 + n4, color5.getRGB());
        if (f > 0.2F) {
            long now = System.currentTimeMillis();
            int n9 = (int)(f * 80.0F);
            Color color6 = new Color(100, 210, 255, n9);

            for (int i = 0; i < 3; i++) {
                float f2 = (float)(now / 400.0 + i * 0.8 + n5 * 0.3);
                int n10 = n2 + (int)(Math.sin(f2) * n4 / 6.0) + n4 / 2;
                int n11 = n + (int)((Math.cos(f2 * 0.7) * 0.5 + 0.5) * (n3 - 10)) + 5;
                this.drawRect(n11, n10, n11 + 8, n10 + 1, color6.getRGB());
            }
        }

        int n12 = (int)(60.0F + 80.0F * f);
        Color color7 = new Color(80, 160, 200, n12);
        this.drawRect(n, n2, n + n3, n2 + 1, color7.getRGB());
        this.drawRect(n, n2 + n4 - 1, n + n3, n2 + n4, color7.getRGB());
        this.drawRect(n, n2, n + 1, n2 + n4, color7.getRGB());
        this.drawRect(n + n3 - 1, n2, n + n3, n2 + n4, color7.getRGB());
        Color color8 = new Color(200, 230, 255);
        Color color9 = new Color(100, 210, 255);
        int n13 = this.computeInt(color8, color9, f * 0.5F);
        int n14 = n + n3 / 2 - this.drawStringWithShadowComponent.getStringWidth(str) / 2;
        int n15 = n2 + n4 / 2 - this.drawStringWithShadowComponent.FONT_HEIGHT / 2;
        this.drawStringWithShadowComponent.drawString(str, n14 + 1, n15 + 2, new Color(0, 30, 60, 100).getRGB());
        this.drawStringWithShadowComponent.drawString(str, n14, n15, n13);
        GlStateManager.disableBlend();
    }
}
