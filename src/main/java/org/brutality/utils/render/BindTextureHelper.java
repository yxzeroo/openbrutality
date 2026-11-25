package org.brutality.utils.render;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import org.brutality.utils.mcp.McpApi3;
import org.brutality.utils.performance.InvalidateHelper;
import org.lwjgl.opengl.GL11;

public class BindTextureHelper implements McpApi3 {
    public static void handleDouble2(
        double d, double d2, double d3, double d4, double d5, double d6, float f, float f2, float f3, float f4
    ) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.color(f, f2, f3, f4);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(d, d2, d3).func_181675_d();
        worldRenderer.func_181662_b(d4, d2, d3).func_181675_d();
        worldRenderer.func_181662_b(d4, d2, d6).func_181675_d();
        worldRenderer.func_181662_b(d, d2, d6).func_181675_d();
        worldRenderer.func_181662_b(d, d5, d3).func_181675_d();
        worldRenderer.func_181662_b(d4, d5, d3).func_181675_d();
        worldRenderer.func_181662_b(d4, d5, d6).func_181675_d();
        worldRenderer.func_181662_b(d, d5, d6).func_181675_d();
        worldRenderer.func_181662_b(d, d2, d3).func_181675_d();
        worldRenderer.func_181662_b(d4, d2, d3).func_181675_d();
        worldRenderer.func_181662_b(d4, d5, d3).func_181675_d();
        worldRenderer.func_181662_b(d, d5, d3).func_181675_d();
        worldRenderer.func_181662_b(d, d2, d6).func_181675_d();
        worldRenderer.func_181662_b(d4, d2, d6).func_181675_d();
        worldRenderer.func_181662_b(d4, d5, d6).func_181675_d();
        worldRenderer.func_181662_b(d, d5, d6).func_181675_d();
        worldRenderer.func_181662_b(d, d2, d3).func_181675_d();
        worldRenderer.func_181662_b(d, d2, d6).func_181675_d();
        worldRenderer.func_181662_b(d, d5, d6).func_181675_d();
        worldRenderer.func_181662_b(d, d5, d3).func_181675_d();
        worldRenderer.func_181662_b(d4, d2, d3).func_181675_d();
        worldRenderer.func_181662_b(d4, d2, d6).func_181675_d();
        worldRenderer.func_181662_b(d4, d5, d6).func_181675_d();
        worldRenderer.func_181662_b(d4, d5, d3).func_181675_d();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void handleInt(int n, int n2, int n3, int n4, int n5, int n6) {
        float f = (n5 >> 24 & 0xFF) / 255.0F;
        float f2 = (n5 >> 16 & 0xFF) / 255.0F;
        float f3 = ((0xFF | ~(n5 >> 8)) - ~(n5 >> 8)) / 255.0F;
        float f4 = (n5 & 0xFF) / 255.0F;
        float f5 = ((0xFF | ~(n6 >> 24)) - ~(n6 >> 24)) / 255.0F;
        float f6 = ((0xFF | ~(n6 >> 16)) - ~(n6 >> 16)) / 255.0F;
        float f7 = (n6 >> 8 & 0xFF) / 255.0F;
        float f8 = (n6 & 0xFF) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(n, n2, 0.0)
            .func_181669_b((int)(f2 * 255.0F), (int)(f3 * 255.0F), (int)(f4 * 255.0F), (int)(f * 255.0F))
            .func_181675_d();
        worldRenderer.func_181662_b(n, n4, 0.0)
            .func_181669_b((int)(f2 * 255.0F), (int)(f3 * 255.0F), (int)(f4 * 255.0F), (int)(f * 255.0F))
            .func_181675_d();
        worldRenderer.func_181662_b(n3, n4, 0.0)
            .func_181669_b((int)(f6 * 255.0F), (int)(f7 * 255.0F), (int)(f8 * 255.0F), (int)(f5 * 255.0F))
            .func_181675_d();
        worldRenderer.func_181662_b(n3, n2, 0.0)
            .func_181669_b((int)(f6 * 255.0F), (int)(f7 * 255.0F), (int)(f8 * 255.0F), (int)(f5 * 255.0F))
            .func_181675_d();
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void handleInt(int n, float f) {
        float f2 = (n >> 16 & 0xFF) / 255.0F;
        float f3 = (n >> 8 & 0xFF) / 255.0F;
        float f4 = (n & 0xFF) / 255.0F;
        GlStateManager.color(f2, f3, f4, f);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, int n, int n2) {
        handleFloat2(f, f2, f3, f4, n);
        handleFloat(f, f2, f3, f4, f5, n2);
    }

    public static void handleInt(int n) {
        handleInt(n, (n >> 24 & 0xFF) / 255.0F);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, int n) {
        handleFloat3(f, f2, f3, f2, f5, n);
        handleFloat3(f, f4, f3, f4, f5, n);
        handleFloat3(f, f2, f, f4, f5, n);
        handleFloat3(f3, f2, f3, f4, f5, n);
    }

    public static void handleDouble2(double d, double d2, double d3, double d4) {
        d3 = Math.max(d3, 0.1);
        ScaledResolution sr = InvalidateHelper.computeScaledResolution(mc);
        double scaleFactor = sr.getScaleFactor();
        d2 = sr.getScaledHeight() - d2;
        d *= scaleFactor;
        d2 *= scaleFactor;
        d3 *= scaleFactor;
        d4 *= scaleFactor;
        GL11.glScissor((int)d, (int)(d2 - d4), (int)d3, (int)d4);
    }

    public static void handleFloat2(float f, float f2, float f3, float f4, int n, float f5) {
        handleFloat2(f + f5, f2 + f5, f3 + f5, f4 + f5, n);
        handleFloat2(f, f2, f3, f4, Color.WHITE.getRGB());
    }

    public static void handleFloat(float f, float f2, float f3, float f4, int n, float f5) {
        float f6 = (float)(0.5 + 0.5 * Math.sin((float)System.currentTimeMillis() * f5 / 1000.0));
        int n2 = (int)(((0xFF | ~(n >> 24)) - ~(n >> 24)) * f6);
        int n3 = n2 << 24 | n & 16777215;
        handleFloat2(f, f2, f3, f4, n3);
    }

    public static void process4() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }

    public static void bindTexture(int n) {
        GlStateManager.bindTexture(n);
    }

    public static int computeInt(float f, float f2, float f3) {
        float f4 = (float)(System.currentTimeMillis() % (long)(2000.0F / f)) / (2000.0F / f);
        return Color.HSBtoRGB(f4, f2, f3);
    }

    /** @deprecated */
    public static void handleFloat(float f, float f2, float f3, int n) {
        float f4 = (n >> 24 & 0xFF) / 255.0F;
        float f5 = (n >> 16 & 0xFF) / 255.0F;
        float f6 = (n >> 8 & 0xFF) / 255.0F;
        float f7 = ((0xFF | ~n) - ~n) / 255.0F;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.color(f5, f6, f7, f4);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(6, DefaultVertexFormats.field_181705_e);

        for (int i = 0; i <= 360; i++) {
            double d = i * Math.PI / 180.0;
            double d2 = f + Math.cos(d) * f3;
            double d3 = f2 + Math.sin(d) * f3;
            worldRenderer.func_181662_b(d2, d3, 0.0).func_181675_d();
        }

        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static Framebuffer computeFramebuffer(Framebuffer framebuffer) {
        if (framebuffer == null
            || framebuffer.framebufferWidth != mc.displayWidth
            || framebuffer.framebufferHeight != mc.displayHeight
            || OpenGlHelper.isFramebufferEnabled() && framebuffer.framebufferObject < 0) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }

            return new Framebuffer(mc.displayWidth, mc.displayHeight, false);
        }
        return framebuffer;
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, int n, boolean flag) {
        float f6 = f;
        float f7 = f2;
        float f8 = f3;
        float f9 = f4;
        if (!flag) {
            f6 = f - f5;
            f7 = f2 - f5;
            f8 = f3 + f5;
            f9 = f4 + f5;
        }

        handleFloat(f6, f7, f8, f7 + f5, n);
        handleFloat(f6, f9 - f5, f8, f9, n);
        handleFloat(f6, f7 + f5, f6 + f5, f9 - f5, n);
        handleFloat(f8 - f5, f7 + f5, f8, f9 - f5, n);
    }

    @Deprecated
    public static void handleDouble(
        double d, double d2, double d3, double d4, double d5, double d6, float f, float f2, float f3, float f4
    ) {
        handleDouble2(d, d2, d3, d4, d5, d6, f, f2, f3, f4);
    }

    public static void handleInt(int n, int n2, int n3, int n4) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(n, mc.displayHeight - n2 - n4, n3, n4);
    }

    public static void drawRect(int n, int n2, int n3, int n4, int n5) {
        Gui.drawRect(n, n2, n + n3, n2 + n4, n5);
    }

    public static void handleFloat3(float f, float f2, float f3, float f4, int n, float f5) {
        for (int i = 0; i < f5; i++) {
            int n2 = (int)((n >> 24 & 0xFF) / 255.0F * (1.0F - i / f5) * 255.0F);
            int n3 = n2 << 24 | n & 16777215;
            handleFloat2(f - i, f2 - i, f3 + i, f4 + i, n3);
        }

        handleFloat2(f, f2, f3, f4, n);
    }

    public static int computeInt(int n, int n2) {
        return n & 16777215 | n2 << 24;
    }

    @Deprecated
    public static void handleFloat3(float f, float f2, float f3, float f4, float f5, int n) {
        handleFloat2(f, f2, f3, f4, f5, n);
    }

    public static void process2() {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public static void handleFloat2(float f, float f2, float f3, float f4, int n) {
        if (f < f3) {
            float f5 = f;
            f = f3;
            f3 = f5;
        }

        if (f2 < f4) {
            float f6 = f2;
            f2 = f4;
            f4 = f6;
        }

        float f7 = ((0xFF | ~(n >> 24)) - ~(n >> 24)) / 255.0F;
        float f8 = (n >> 16 & 0xFF) / 255.0F;
        float f9 = ((0xFF | ~(n >> 8)) - ~(n >> 8)) / 255.0F;
        float f10 = (n & 0xFF) / 255.0F;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.color(f8, f9, f10, f7);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(f, f4, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f3, f4, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f3, f2, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f, f2, 0.0).func_181675_d();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void handleDouble(double d, double d2, double d3, double d4) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        ScaledResolution sr = InvalidateHelper.computeScaledResolution(mc);
        double scaleFactor = sr.getScaleFactor();
        double d5 = Math.max(0.0, d4 * scaleFactor);
        double d6 = (sr.getScaledHeight() - d2) * scaleFactor;
        GL11.glScissor((int)(d * scaleFactor), (int)(d6 - d5), Math.max(0, (int)(d3 * scaleFactor)), Math.max(0, (int)d5));
    }

    public static void handleDouble(double d, double d2, double d3, double d4, double d5) {
        d3 = Math.max(d3, 0.1);
        ScaledResolution sr = InvalidateHelper.computeScaledResolution(mc);
        d2 = sr.getScaledHeight() - d2;
        d *= d5;
        d2 *= d5;
        d3 *= d5;
        d4 *= d5;
        GL11.glScissor((int)d, (int)(d2 - d4), (int)d3, (int)d4);
    }

    public static void handleFloat(float f) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, (float)(f * 0.01));
    }

    public static void handleDouble(double d, double d2, double d3, int n) {
        float f = (n >> 24 & 0xFF) / 255.0F;
        float f2 = (n >> 16 & 0xFF) / 255.0F;
        float f3 = (n >> 8 & 0xFF) / 255.0F;
        float f4 = ((0xFF | ~n) - ~n) / 255.0F;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.color(f2, f3, f4, f);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(2, DefaultVertexFormats.field_181705_e);

        for (int i = 0; i < 360; i++) {
            double d4 = d + Math.sin(Math.toRadians(i)) * d3;
            double d5 = d2 + Math.cos(Math.toRadians(i)) * d3;
            worldRenderer.func_181662_b(d4, d5, 0.0).func_181675_d();
        }

        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void handleDouble(double d, double d2, double d3, double d4, double d5, double d6) {
        GL11.glVertex3d(d, d2, d3);
        GL11.glVertex3d(d4, d2, d3);
        GL11.glVertex3d(d4, d2, d3);
        GL11.glVertex3d(d4, d2, d6);
        GL11.glVertex3d(d4, d2, d6);
        GL11.glVertex3d(d, d2, d6);
        GL11.glVertex3d(d, d2, d6);
        GL11.glVertex3d(d, d2, d3);
        GL11.glVertex3d(d, d5, d3);
        GL11.glVertex3d(d4, d5, d3);
        GL11.glVertex3d(d4, d5, d3);
        GL11.glVertex3d(d4, d5, d6);
        GL11.glVertex3d(d4, d5, d6);
        GL11.glVertex3d(d, d5, d6);
        GL11.glVertex3d(d, d5, d6);
        GL11.glVertex3d(d, d5, d3);
        GL11.glVertex3d(d, d2, d3);
        GL11.glVertex3d(d, d5, d3);
        GL11.glVertex3d(d4, d2, d3);
        GL11.glVertex3d(d4, d5, d3);
        GL11.glVertex3d(d4, d2, d6);
        GL11.glVertex3d(d4, d5, d6);
        GL11.glVertex3d(d, d2, d6);
        GL11.glVertex3d(d, d5, d6);
    }

    public static void resetColor() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Deprecated
    public static void handleDouble2(double d, double d2, double d3, int n) {
        handleDouble(d, d2, d3, n);
    }

    public static void handleFloat(float f, float f2, float f3, int n, int n2) {
        float f4 = f;
        float f5 = f2;
        float f6 = f;
        float f7 = f2;
        float f8 = f;
        float f9 = f2;
        switch (n) {
            case 0:
                f4 = f;
                f5 = f2 - f3;
                f6 = f - f3 / 2.0F;
                f7 = f2;
                f8 = f + f3 / 2.0F;
                f9 = f2;
                break;
            case 1:
                f4 = f + f3;
                f5 = f2;
                f6 = f;
                f7 = f2 - f3 / 2.0F;
                f8 = f;
                f9 = f2 + f3 / 2.0F;
                break;
            case 2:
                f4 = f;
                f5 = f2 + f3;
                f6 = f - f3 / 2.0F;
                f7 = f2;
                f8 = f + f3 / 2.0F;
                f9 = f2;
                break;
            case 3:
                f4 = f - f3;
                f5 = f2;
                f6 = f;
                f7 = f2 - f3 / 2.0F;
                f8 = f;
                f9 = f2 + f3 / 2.0F;
        }

        handleFloat(f4, f5, f6, f7, f8, f9, n2);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, int n, int n2) {
        float f5 = (n >> 24 & 0xFF) / 255.0F;
        float f6 = (n >> 16 & 0xFF) / 255.0F;
        float f7 = (n >> 8 & 0xFF) / 255.0F;
        float f8 = (n & 0xFF) / 255.0F;
        float f9 = ((0xFF | ~(n2 >> 24)) - ~(n2 >> 24)) / 255.0F;
        float f10 = (n2 >> 16 & 0xFF) / 255.0F;
        float f11 = (n2 >> 8 & 0xFF) / 255.0F;
        float f12 = ((0xFF | ~n2) - ~n2) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(f3, f2, 0.0)
            .func_181669_b((int)(f6 * 255.0F), (int)(f7 * 255.0F), (int)(f8 * 255.0F), (int)(f5 * 255.0F))
            .func_181675_d();
        worldRenderer.func_181662_b(f, f2, 0.0)
            .func_181669_b((int)(f6 * 255.0F), (int)(f7 * 255.0F), (int)(f8 * 255.0F), (int)(f5 * 255.0F))
            .func_181675_d();
        worldRenderer.func_181662_b(f, f4, 0.0)
            .func_181669_b((int)(f10 * 255.0F), (int)(f11 * 255.0F), (int)(f12 * 255.0F), (int)(f9 * 255.0F))
            .func_181675_d();
        worldRenderer.func_181662_b(f3, f4, 0.0)
            .func_181669_b((int)(f10 * 255.0F), (int)(f11 * 255.0F), (int)(f12 * 255.0F), (int)(f9 * 255.0F))
            .func_181675_d();
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5) {
        float f6 = (float)(System.currentTimeMillis() % (long)(2000.0F / f5)) / (2000.0F / f5);
        int n = Color.HSBtoRGB(f6, 1.0F, 1.0F);
        handleFloat2(f, f2, f3, f4, n);
    }

    public static void handleColor(Color color) {
        handleInt2(color.getRGB());
    }

    public static int computeInt(float f, float f2) {
        float f3 = f / f2;
        if (f3 > 0.75F) {
            return Color.GREEN.getRGB();
        }
        if (f3 > 0.5F) {
            return Color.YELLOW.getRGB();
        }
        return f3 > 0.25F ? Color.ORANGE.getRGB() : Color.RED.getRGB();
    }

    public static void handleInt2(int n) {
        float f = (n >> 24 & 0xFF) / 255.0F;
        float f2 = ((0xFF | ~(n >> 16)) - ~(n >> 16)) / 255.0F;
        float f3 = (n >> 8 & 0xFF) / 255.0F;
        float f4 = (n & 0xFF) / 255.0F;
        GL11.glColor4f(f2, f3, f4, f);
    }

    public static void process3() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void process() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, float f6, int n) {
        float f7 = ((0xFF | ~(n >> 24)) - ~(n >> 24)) / 255.0F;
        float f8 = ((0xFF | ~(n >> 16)) - ~(n >> 16)) / 255.0F;
        float f9 = ((0xFF | ~(n >> 8)) - ~(n >> 8)) / 255.0F;
        float f10 = ((0xFF | ~n) - ~n) / 255.0F;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.color(f8, f9, f10, f7);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(4, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(f, f2, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f3, f4, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f5, f6, 0.0).func_181675_d();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void handleDouble(double d, double d2, double d3, float f) {
        GL11.glPushMatrix();
        GL11.glLineWidth(3.5F);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        for (int i = 0; i < 360; i++) {
            handleInt2(Color.HSBtoRGB(1.0F, 0.0F, f));
            GL11.glVertex2d(d, d2);
            handleInt2(Color.HSBtoRGB(i / 360.0F, 1.0F, f));
            GL11.glVertex2d(d + Math.sin(Math.toRadians(i)) * d3, d2 + Math.cos(Math.toRadians(i)) * d3);
        }

        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glPopMatrix();
    }

    public static void handleDouble(double d, double d2, double d3, double d4, double d5, int n, boolean flag) {
        handleFloat((float)d, (float)d2, (float)d3, (float)d4, (float)d5, n, flag);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, int n) {
        Gui.drawRect((int)f, (int)f2, (int)f3, (int)f4, n);
    }

    public static int computeInt(int n, int n2, float f) {
        f = Math.max(0.0F, Math.min(1.0F, f));
        int n3 = n >> 24 & 0xFF;
        int n4 = n >> 16 & 0xFF;
        int n5 = n >> 8 & 0xFF;
        int n6 = (0xFF | ~n) - ~n;
        int n7 = (0xFF | ~(n2 >> 24)) - ~(n2 >> 24);
        int n8 = (0xFF | ~(n2 >> 16)) - ~(n2 >> 16);
        int n9 = n2 >> 8 & 0xFF;
        int n10 = (0xFF | ~n2) - ~n2;
        int n11 = (int)(n3 + (n7 - n3) * f);
        int n12 = (int)(n4 + (n8 - n4) * f);
        int n13 = (int)(n5 + (n9 - n5) * f);
        int n14 = (int)(n6 + (n10 - n6) * f);
        return (n13 << 8) + ((n11 << 24 | n12 << 16) & ~(n13 << 8)) | n14;
    }

    public static void handleFloat2(float f, float f2, float f3, float f4, float f5, int n) {
        float f6 = (n >> 24 & 0xFF) / 255.0F;
        float f7 = ((0xFF | ~(n >> 16)) - ~(n >> 16)) / 255.0F;
        float f8 = (n >> 8 & 0xFF) / 255.0F;
        float f9 = (n & 0xFF) / 255.0F;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.color(f7, f8, f9, f6);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(f5);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(1, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(f, f2, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f3, f4, 0.0).func_181675_d();
        tessellator.draw();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
