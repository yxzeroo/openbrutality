package org.brutality.clickgui.discord;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public final class DiscordComponent {
    public static void handleDouble(double d, double d2, float f, int n) {
        float f2 = ((0xFF | ~(n >> 24)) - ~(n >> 24)) / 255.0F;
        float f3 = ((0xFF | ~(n >> 16)) - ~(n >> 16)) / 255.0F;
        float f4 = ((0xFF | ~(n >> 8)) - ~(n >> 8)) / 255.0F;
        float f5 = (n & 0xFF) / 255.0F;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glPushMatrix();
        GL11.glColor4f(f3, f4, f5, f2 == 0.0F ? 1.0F : f2);
        GL11.glLineWidth(f);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2d(d, d2);
        GL11.glVertex2d(d + 2.0, d2 + 3.0);
        GL11.glVertex2d(d + 6.0, d2 - 2.0);
        GL11.glEnd();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    public static void handleFloat(float f, float f2, float f3, int n) {
        float f4 = (n >> 24 & 0xFF) / 255.0F;
        float f5 = ((0xFF | ~(n >> 16)) - ~(n >> 16)) / 255.0F;
        float f6 = ((0xFF | ~(n >> 8)) - ~(n >> 8)) / 255.0F;
        float f7 = (n & 0xFF) / 255.0F;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        float PI = (float) Math.PI;

        for (int i = 0; i <= 360; i++) {
            GL11.glVertex2d(f + MathHelper.sin(i * PI / 180.0F) * f3, f2 + MathHelper.cos(i * PI / 180.0F) * f3);
        }

        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, int n, int n2) {
        handleFloat(f, f2, f3, f4, n2);
        float f6 = ((0xFF | ~(n >> 24)) - ~(n >> 24)) / 255.0F;
        float f7 = (n >> 16 & 0xFF) / 255.0F;
        float f8 = (n >> 8 & 0xFF) / 255.0F;
        float f9 = ((0xFF | ~n) - ~n) / 255.0F;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glPushMatrix();
        GL11.glColor4f(f7, f8, f9, f6);
        GL11.glLineWidth(f5);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(f, f2);
        GL11.glVertex2d(f, f4);
        GL11.glVertex2d(f3, f4);
        GL11.glVertex2d(f3, f2);
        GL11.glVertex2d(f, f2);
        GL11.glVertex2d(f3, f2);
        GL11.glVertex2d(f, f4);
        GL11.glVertex2d(f3, f4);
        GL11.glEnd();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    private DiscordComponent() {
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, int n) {
        float f6 = f + f3;
        float f7 = f2 + f4;
        float f8 = (n >> 24 & 0xFF) / 255.0F;
        float f9 = (n >> 16 & 0xFF) / 255.0F;
        float f10 = (n >> 8 & 0xFF) / 255.0F;
        float f11 = (n & 0xFF) / 255.0F;
        GL11.glPushAttrib(0);
        GL11.glScaled(0.5, 0.5, 0.5);
        float f12 = f * 2.0F;
        float f13 = f2 * 2.0F;
        float f14 = f6 * 2.0F;
        float f15 = f7 * 2.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(f9, f10, f11, f8);
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBegin(GL11.GL_POLYGON);
        double d = Math.PI / 180.0;

        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(
                f12 + f5 + MathHelper.sin((float)(i * d)) * (f5 * -1.0F), f13 + f5 + MathHelper.cos((float)(i * d)) * (f5 * -1.0F)
            );
        }

        for (int j = 90; j <= 180; j += 3) {
            GL11.glVertex2d(
                f12 + f5 + MathHelper.sin((float)(j * d)) * (f5 * -1.0F), f15 - f5 + MathHelper.cos((float)(j * d)) * (f5 * -1.0F)
            );
        }

        for (int k = 0; k <= 90; k += 3) {
            GL11.glVertex2d(f14 - f5 + MathHelper.sin((float)(k * d)) * f5, f15 - f5 + MathHelper.cos((float)(k * d)) * f5);
        }

        for (int idx = 90; idx <= 180; idx += 3) {
            GL11.glVertex2d(f14 - f5 + MathHelper.sin((float)(idx * d)) * f5, f13 + f5 + MathHelper.cos((float)(idx * d)) * f5);
        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glScaled(2.0, 2.0, 2.0);
        GL11.glPopAttrib();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, int n) {
        float f5 = (n >> 24 & 0xFF) / 255.0F;
        float f6 = (n >> 16 & 0xFF) / 255.0F;
        float f7 = ((0xFF | ~(n >> 8)) - ~(n >> 8)) / 255.0F;
        float f8 = (n & 0xFF) / 255.0F;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glPushMatrix();
        GL11.glColor4f(f6, f7, f8, f5);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(f3, f2);
        GL11.glVertex2d(f, f2);
        GL11.glVertex2d(f, f4);
        GL11.glVertex2d(f3, f4);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
