package org.brutality.gui.mainmenu.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public final class BaseComponent {
    private static void handleFloat(float f, float f2, float f3, double d, double d2) {
        GL11.glBegin(GL11.GL_LINE_STRIP);
        handleFloat2(f, f2, f3, d, d2);
        GL11.glEnd();
    }

    private static void handleFloat(float f) {
        int scaleFactor = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        GL11.glLineWidth(Math.max(1.0F, f * scaleFactor));
    }

    private BaseComponent() {
    }

    public static void handleThemesMode(BaseComponent.ThemesMode themesMode, float f, float f2, float f3, int n) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GlStateManager.color((n >> 16 & 0xFF) / 255.0F, (n >> 8 & 0xFF) / 255.0F, (n & 0xFF) / 255.0F, (n >>> 24) / 255.0F);
        handleFloat(1.2F);
        float f4 = f3 / 16.0F;
        float f5 = f + f3 / 2.0F;
        float f6 = f2 + f3 / 2.0F;
        switch (themesMode) {
            case THEMES:
                handleFloat(f5, f6, 6.6F * f4, 0.0, Math.PI * 2);
                handleFloat(f5 + 2.4F * f4, f6 + 2.6F * f4, 1.5F * f4, 0.0, Math.PI * 2);
                handleFloat(f5 - 2.9F * f4, f6 - 1.4F * f4, 1.15F * f4);
                handleFloat(f5 - 0.6F * f4, f6 - 3.7F * f4, 1.15F * f4);
                handleFloat(f5 + 2.7F * f4, f6 - 2.6F * f4, 1.15F * f4);
                break;
            case CHANGELOG:
                GL11.glBegin(GL11.GL_LINE_STRIP);
                GL11.glVertex2f(f + 9.5F * f4, f2 + 2.0F * f4);
                GL11.glVertex2f(f + 3.5F * f4, f2 + 2.0F * f4);
                GL11.glVertex2f(f + 3.5F * f4, f2 + 14.0F * f4);
                GL11.glVertex2f(f + 12.5F * f4, f2 + 14.0F * f4);
                GL11.glVertex2f(f + 12.5F * f4, f2 + 5.0F * f4);
                GL11.glVertex2f(f + 9.5F * f4, f2 + 2.0F * f4);
                GL11.glVertex2f(f + 9.5F * f4, f2 + 5.0F * f4);
                GL11.glVertex2f(f + 12.5F * f4, f2 + 5.0F * f4);
                GL11.glEnd();
                GL11.glBegin(GL11.GL_LINES);

                for (int i = 0; i < 3; i++) {
                    float f7 = f2 + (7.5F + i * 2.3F) * f4;
                    GL11.glVertex2f(f + 5.5F * f4, f7);
                    GL11.glVertex2f(f + (i == 2 ? 8.5F : 10.5F) * f4, f7);
                }

                GL11.glEnd();
                break;
            case SETTINGS:
                int n2 = 8;
                GL11.glBegin(GL11.GL_LINE_LOOP);

                for (int j = 0; j < n2 * 4; j++) {
                    double d = j * Math.PI * 2.0 / (n2 * 4);
                    float f8 = (j % 4 != 0 && j % 4 != 1 ? 5.0F : 6.6F) * f4;
                    GL11.glVertex2f(f5 + (float)Math.cos(d) * f8, f6 + (float)Math.sin(d) * f8);
                }

                GL11.glEnd();
                handleFloat(f5, f6, 2.1F * f4, 0.0, Math.PI * 2);
                break;
            case QUIT:
                handleFloat(f5, f6 + 0.6F * f4, 5.6F * f4, -0.8207963267948966, 3.9623889803846897);
                GL11.glBegin(GL11.GL_LINES);
                GL11.glVertex2f(f5, f6 - 6.4F * f4);
                GL11.glVertex2f(f5, f6 + 0.2F * f4);
                GL11.glEnd();
        }

        GL11.glLineWidth(1.0F);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
    }

    private static void handleFloat2(float f, float f2, float f3, double d, double d2) {
        int n = Math.max(2, (int)Math.ceil(Math.abs(d2 - d) / (Math.PI * 2) * 40.0));

        for (int i = 0; i <= n; i++) {
            double d3 = d + (d2 - d) * i / n;
            GL11.glVertex2f(f + (float)Math.cos(d3) * f3, f2 + (float)Math.sin(d3) * f3);
        }
    }

    private static void handleFloat(float f, float f2, float f3) {
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(f, f2);

        for (int i = 0; i <= 16; i++) {
            double d = i * Math.PI * 2.0 / 16.0;
            GL11.glVertex2f(f + (float)Math.cos(d) * f3, f2 + (float)Math.sin(d) * f3);
        }

        GL11.glEnd();
    }

    public static enum ThemesMode {
        THEMES,
        CHANGELOG,
        SETTINGS,
        QUIT;

        private static BaseComponent.ThemesMode[] computeThemesModeArray() {
            return new BaseComponent.ThemesMode[]{THEMES, CHANGELOG, SETTINGS, QUIT};
        }
    }
}
