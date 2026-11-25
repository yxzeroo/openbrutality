package org.brutality.gui;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.mainmenu.effects.MainMenuIconRenderer;
import org.brutality.utils.render.OutlineThicknessHelper;
import org.brutality.utils.render.GlStateGuard;
import org.lwjgl.opengl.GL11;

public final class MenuButtonRenderer {
    public static final int count = -1511690;
    public static final int count2 = -6575427;
    public static final int count3 = -8144657;
    public static final int count4 = -9972828;
    public static final int count5 = -8877150;
    private static final long timestamp = 4000000000L;

    private static void handleFloat(
        float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, Color color, Color color2, float f9
    ) {
        float f10 = Math.abs(f7 - f8);
        float f11 = Math.max(0.0F, 1.0F - Math.min(f10, 1.0F - f10) * 6.0F);
        f11 *= f11;
        float f12 = 0.5F + 0.5F * (float)Math.cos((f7 - f8) * 2.0F * Math.PI);
        float f13 = (color.getRed() + (color2.getRed() - color.getRed()) * f12) / 255.0F;
        float f14 = (color.getGreen() + (color2.getGreen() - color.getGreen()) * f12) / 255.0F;
        float f15 = (color.getBlue() + (color2.getBlue() - color.getBlue()) * f12) / 255.0F;
        GL11.glColor4f(
            f13 + (1.0F - f13) * f11 * 0.2F,
            f14 + (1.0F - f14) * f11 * 0.2F,
            f15 + (1.0F - f15) * f11 * 0.2F,
            f9 * (0.22F + 0.78F * f11)
        );
        GL11.glVertex2f(f + f3 * f5, f2 + f4 * f5);
        GL11.glVertex2f(f + f3 * f6, f2 + f4 * f6);
    }

    private static void handleInt(int n, int n2, int n3, int n4, float f, int n5, int n6, float f2) {
        float f3 = f - 1.05F;
        float f4 = f - 0.15F;
        float f5 = n3 - 2.0F * f;
        float f6 = n4 - 2.0F * f;
        float f7 = f * (float) (Math.PI / 2);
        float f8 = 4.0F * f7 + 2.0F * (f5 + f6);
        float f9 = (float)Math.floorMod(System.nanoTime(), timestamp) / 4.0E9F;
        Color color = new Color(n5);
        Color color2 = new Color(n6);
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        float f10 = 0.0F;

        for (int i = 0; i < 4; i++) {
            float f11 = n + (i != 0 && i != 1 ? f : n3 - f);
            float f12 = n2 + (i != 1 && i != 2 ? f : n4 - f);

            for (int j = 0; j <= 5; j++) {
                double d = (i - 1 + j / 5.0) * Math.PI / 2.0;
                handleFloat(
                    f11, f12, (float)Math.cos(d), (float)Math.sin(d), f3, f4, (f10 + f7 * j / 5.0F) / f8, f9, color, color2, f2
                );
            }

            float f13 = f10 + f7;
            float f14 = i % 2 == 0 ? f6 : f5;
            int n7 = Math.max(1, (int)Math.ceil(f14 / 6.0F));
            float f15 = i == 0 ? 1.0F : (i == 2 ? -1.0F : 0.0F);
            float f16 = i == 1 ? 1.0F : (i == 3 ? -1.0F : 0.0F);

            for (int k = 1; k <= n7; k++) {
                float f17 = f14 * k / n7;
                handleFloat(f11 - f16 * f17, f12 + f15 * f17, f15, f16, f3, f4, (f13 + f17) / f8, f9, color, color2, f2);
            }

            f10 = f13 + f14;
        }

        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.resetColor();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void handleInt(int n, int n2, int n3, int n4, float f, Color color, Color color2) {
        if (OpenGlHelper.shadersSupported && OutlineThicknessHelper.shaderEffect2 != null) {
            OutlineThicknessHelper.handleFloat(n, n2, n3, n4, f, 0.5F, color, color2);
        } else {
            Gui.drawRect(n, n2, n + n3, n2 + n4, color.getRGB());
            Gui.drawRect(n, n2, n + n3, n2 + 1, color2.getRGB());
            Gui.drawRect(n, n2 + n4 - 1, n + n3, n2 + n4, color2.getRGB());
            Gui.drawRect(n, n2, n + 1, n2 + n4, color2.getRGB());
            Gui.drawRect(n + n3 - 1, n2, n + n3, n2 + n4, color2.getRGB());
        }
    }

    public static void handleInt(int n, int n2, int n3, int n4, String str, CustomFontRenderer drawStringWithShadowComponent, int n5, float f, boolean flag) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            handleInt(n, n2, n3, n4, n5, f, flag);
            if (drawStringWithShadowComponent.getStringWidth(str) > n3 - 8) {
                int len = str.length();

                while (len > 0 && drawStringWithShadowComponent.getStringWidth(str.substring(0, len) + "…") > n3 - 8) {
                    len--;
                }

                str = str.substring(0, len) + "…";
            }

            int n6 = 208 + (int)(47.0F * Math.max(0.0F, Math.min(1.0F, f)));
            drawStringWithShadowComponent.drawString(
                str,
                n + (n3 - drawStringWithShadowComponent.getStringWidth(str)) / 2,
                n2 + (n4 - drawStringWithShadowComponent.FONT_HEIGHT) / 2 + 1,
                flag ? 0xFF000000 | n6 << 16 | n6 << 8 | n6 : -10524552
            );
        }
    }

    private static void process() {
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void handleInt(int n, int n2, int n3, int n4, boolean flag, boolean flag2, boolean flag3) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            process();
            Color color = OceanComponent.computeColor();
            Color color2 = flag2
                ? new Color(color.getRed() / 6 + 12, color.getGreen() / 6 + 12, color.getBlue() / 6 + 16, 215)
                : (flag ? new Color(24, 26, 34, 215) : new Color(13, 14, 20, 196));
            Color color3 = flag3
                ? new Color(-9972828, true)
                : (flag2 ? computeColor(color, 210) : (flag ? new Color(255, 255, 255, 52) : new Color(255, 255, 255, 22)));
            boolean flag4 = OpenGlHelper.shadersSupported && OutlineThicknessHelper.shaderEffect != null && OutlineThicknessHelper.shaderEffect2 != null;
            if (flag3 && flag4) {
                Color color4 = new Color(-9972828, true);
                float f = 0.85F + 0.15F * (float)Math.sin(System.currentTimeMillis() / 520.0);
                int[] ints = new int[]{120, 82, 52, 30, 14};

                for (int i = 0; i < ints.length; i++) {
                    float f2 = 1.0F + i * 1.3F;
                    OutlineThicknessHelper.handleFloat(
                        n - f2,
                        n2 - f2,
                        n3 + f2 * 2.0F,
                        n4 + f2 * 2.0F,
                        6.0F + f2,
                        1.1F,
                        new Color(0, 0, 0, 0),
                        new Color(color4.getRed(), color4.getGreen(), color4.getBlue(), (int)(ints[i] * f))
                    );
                }
            }

            handleInt(n, n2, n3, n4, 6.0F, color2, color3);
            if (flag2 && !flag3 && flag4) {
                OutlineThicknessHelper.handleFloat(n + 1, n2 + 9, 2.0F, Math.max(2, n4 - 18), 1.0F, color);
            }
        }
    }

    private MenuButtonRenderer() {
    }

    public static void handleInt2(int n, int n2, int n3, int n4) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            process();
            if (OpenGlHelper.shadersSupported && OutlineThicknessHelper.shaderEffect2 != null) {
                MainMenuIconRenderer.handleInt(n, n2, n3, n4, 10.0F, 1.15F);
                MainMenuIconRenderer.handleInt(n + 12, n2 + 24, n3 - 24, 34);
            } else {
                handleInt(n, n2, n3, n4, 10.0F, new Color(1121580), new Color(4612478));
                Gui.drawRect(n + 12, n2 + 24, n + n3 - 12, n2 + 25, -13349786);
            }
        }
    }

    public static void handleInt(int n, int n2, int n3, int n4, String str, CustomFontRenderer drawStringWithShadowComponent, int n5, float f) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            process();
            float f2 = Math.min(4.0F, n4 / 4.0F);
            handleInt(
                n,
                n2,
                n3,
                n4,
                f2,
                new Color(28 + (int)(f * 8.0F), 29 + (int)(f * 8.0F), 36 + (int)(f * 10.0F), 168 + (int)(f * 50.0F)),
                new Color(255, 255, 255, 18)
            );
            handleInt(n, n2, n3, n4, f2, n5, n5, 0.72F + 0.24F * f);
            int n6 = 215 + (int)(40.0F * f);
            drawStringWithShadowComponent.drawString(
                str,
                n + (n3 - drawStringWithShadowComponent.getStringWidth(str)) / 2,
                n2 + (n4 - drawStringWithShadowComponent.FONT_HEIGHT) / 2 + 1,
                n6 + ((0xFF000000 | n6 << 16 | n6 << 8) & ~n6)
            );
        }
    }

    public static void handleInt(
        int n, int n2, int n3, int n4, String str, CustomFontRenderer drawStringWithShadowComponent, float f, boolean flag, boolean flag2
    ) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            process();
            Color color = new Color(28 + (int)(f * 22.0F), 28 + (int)(f * 22.0F), 32 + (int)(f * 24.0F), 150 + (int)(f * 40.0F));
            Color color2 = new Color(255, 255, 255, 34 + (int)(f * 50.0F));
            handleInt(n, n2, n3, n4, Math.min(3.0F, n4 / 4.0F), color, color2);
            int textWidth = drawStringWithShadowComponent.getStringWidth(str);
            int n5 = flag2 ? 7 : 0;
            int n6 = n + (n3 - textWidth - n5) / 2 + n5;
            if (flag2 && OpenGlHelper.shadersSupported && OutlineThicknessHelper.shaderEffect != null) {
                OutlineThicknessHelper.handleFloat(n6 - n5, n2 + n4 / 2.0F - 2.0F, 4.0F, 4.0F, 2.0F, new Color(-9972828, true));
            }

            int n7 = flag ? 220 + (int)(35.0F * f) : 120;
            drawStringWithShadowComponent.drawString(str, n6, n2 + (n4 - drawStringWithShadowComponent.FONT_HEIGHT) / 2 + 1, 0xFF000000 | n7 << 16 | n7 << 8 | n7);
        }
    }

    public static void handleInt(int n, int n2, int n3, int n4) {
        if (!OceanComponent.matchesInt(n, n2)) {
            Gui.drawRect(0, 0, n, n2, -15920096);
        }

        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            process();
            Gui.drawRect(0, 0, n, n3, -1207301359);
            Gui.drawRect(0, n4, n, n2, -1207301359);
            MainMenuIconRenderer.handleInt(0, n3 - 1, n, 46);
            MainMenuIconRenderer.handleInt(0, n4, n, 46);
        }
    }

    public static void handleInt(int n, int n2, int n3, int n4, int n5, float f, boolean flag) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            process();
            Color color = new Color(n5, true);
            Color color2 = flag
                ? new Color(28 + (int)(f * 8.0F), 29 + (int)(f * 8.0F), 36 + (int)(f * 10.0F), 168 + (int)(f * 50.0F))
                : new Color(18, 19, 24, 140);
            Color color3 = flag
                ? new Color(
                    (int)(255.0F + (color.getRed() - 255) * f),
                    (int)(255.0F + (color.getGreen() - 255) * f),
                    (int)(255.0F + (color.getBlue() - 255) * f),
                    26 + (int)(164.0F * f)
                )
                : new Color(255, 255, 255, 14);
            handleInt(n, n2, n3, n4, Math.min(4.0F, n4 / 4.0F), color2, color3);
            if (flag && f > 0.02F && OpenGlHelper.shadersSupported && OutlineThicknessHelper.shaderEffect != null) {
                OutlineThicknessHelper.handleFloat(
                    n + 1,
                    n2 + 1,
                    n3 - 2,
                    n4 - 2,
                    Math.min(3.0F, n4 / 4.0F),
                    new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(34.0F * f)),
                    new Color(0, 0, 0, 0)
                );
            }
        }
    }

    public static void handleInt(int n, int n2, int n3, int n4, boolean flag, int n5, int n6) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            process();
            handleInt(n, n2, n3, n4, 6.0F, flag ? new Color(24, 26, 34, 215) : new Color(13, 14, 20, 196), new Color(255, 255, 255, flag ? 36 : 17));
            handleInt(n, n2, n3, n4, 6.0F, n5, n6, flag ? 0.82F : 0.59F);
        }
    }

    private static Color computeColor(Color color, int n) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, n)));
    }
}
