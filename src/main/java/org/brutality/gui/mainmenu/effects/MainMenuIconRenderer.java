package org.brutality.gui.mainmenu.effects;

import java.awt.Color;
import java.awt.Rectangle;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.gui.mainmenu.base.BaseComponent;
import org.brutality.utils.render.OutlineThicknessHelper;
import org.brutality.utils.render.GlStateGuard;
import org.lwjgl.opengl.GL11;

public final class MainMenuIconRenderer {
    private static final Color color = new Color(0, 0, 0, 0);

    public static void handleInt(int n, int n2, int n3, BaseComponent.ThemesMode themesMode, float f) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            process();
            Gui.drawRect(n, n2, n + n3, n2 + n3, new Color(26, 26, 26, (int)(82.0F + 30.0F * f)).getRGB());
            if (checkState()) {
                OutlineThicknessHelper.handleFloat(n, n2, n3, n3, 0.0F, 0.5F, color, new Color(26, 26, 26, (int)(122.0F + 30.0F * f)));
            }

            int n4 = (int)(201.0F + 40.0F * f);
            float f2 = n3 - 7;
            BaseComponent.handleThemesMode(
                themesMode, n + (n3 - f2) / 2.0F, n2 + (n3 - f2) / 2.0F, f2, new Color(n4, n4, n4, (int)(200.0F + 55.0F * f)).getRGB()
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

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, int n) {
        if (checkState()) {
            for (int i = 1; i <= 5; i++) {
                float f6 = i * 2.2F;
                int n2 = Math.max(1, n / (i + 1));
                OutlineThicknessHelper.handleFloat(
                    f - f6, f2 - f6 + 3.0F, f3 + f6 * 2.0F, f4 + f6 * 2.0F, f5 + f6, new Color(0, 0, 0, n2)
                );
            }
        }
    }

    private static Color computeColor(Color color, float f) {
        return new Color((int)(color.getRed() * f), (int)(color.getGreen() * f), (int)(color.getBlue() * f));
    }

    public static void handleInt(int n, int n2, int n3, int n4, String str, String str2, String str3, float f, int[] ints) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            process();
            Color color2 = new Color(ints[0], ints[1], ints[2]);
            if (f > 0.01F && checkState()) {
                OutlineThicknessHelper.handleFloat3(
                    n, n2, n3, n4, 6.0F, computeColor(color2, (int)(46.0F * f)), new Color(255, 255, 255, (int)(6.0F * f))
                );
                OutlineThicknessHelper.handleFloat(n, n2, n3, n4, 6.0F, 0.5F, color, new Color(255, 255, 255, (int)(16.0F * f)));
                float f2 = (n4 - 12) * f;
                OutlineThicknessHelper.handleFloat(n + 3, n2 + (n4 - f2) / 2.0F, 2.0F, f2, 1.0F, computeColor(color2, (int)(255.0F * f)));
            } else if (f > 0.01F) {
                Gui.drawRect(n, n2, n + n3, n2 + n4, computeColor(color2, (int)(40.0F * f)).getRGB());
            }

            CustomFontRenderer drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
            int n5 = n2 + (n4 - drawStringWithShadowComponent.FONT_HEIGHT) / 2 + 1;
            Color color3 = computeColor2(new Color(128, 136, 152), computeColor2(color2, Color.WHITE, 0.25F), f);
            int n6 = n + 14 + (int)(2.0F * f);
            drawStringWithShadowComponent.computeInt3(str, n6 + (12 - drawStringWithShadowComponent.getStringWidth(str)) / 2.0F, n5, color3.getRGB());
            drawStringWithShadowComponent.drawString(str2, n + 38 + (int)(4.0F * f), n5, computeColor2(new Color(205, 211, 222), Color.WHITE, f).getRGB());
            if (f > 0.02F) {
                int textWidth = drawStringWithShadowComponent.getStringWidth(str3);
                drawStringWithShadowComponent.drawString(
                    str3,
                    n + n3 - textWidth - 12 - (int)(4.0F * (1.0F - f)),
                    n5,
                    computeColor(computeColor2(color2, Color.WHITE, 0.3F), (int)(230.0F * f)).getRGB()
                );
            }
        }
    }

    public static void handleInt(int n, int n2, int n3, int n4, String str, CustomFontRenderer drawStringWithShadowComponent, float f, float f2) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            process();
            float f3 = Math.max(0.0F, Math.min(1.0F, f2));
            int n5 = (int)((82.0F + 30.0F * f) * f3 + 18.0F * (1.0F - f3) * f);
            Gui.drawRect(n, n2, n + n3, n2 + n4, new Color(26, 26, 26, Math.max(0, Math.min(255, n5))).getRGB());
            if (checkState()) {
                OutlineThicknessHelper.handleFloat(n, n2, n3, n4, 0.0F, 0.5F, color, new Color(26, 26, 26, (int)(122.0F + 30.0F * f)));
            } else {
                int rgb = new Color(26, 26, 26, (int)(122.0F + 30.0F * f)).getRGB();
                Gui.drawRect(n, n2, n + n3, n2 + 1, rgb);
                Gui.drawRect(n, n2 + n4 - 1, n + n3, n2 + n4, rgb);
                Gui.drawRect(n, n2, n + 1, n2 + n4, rgb);
                Gui.drawRect(n + n3 - 1, n2, n + n3, n2 + n4, rgb);
            }

            int n6 = (int)(201.0F + 25.0F * f);
            int n7 = (int)(190.0F + 60.0F * f);
            GlStateManager.enableTexture2D();
            drawStringWithShadowComponent.computeInt3(
                str,
                n + (n3 - drawStringWithShadowComponent.getStringWidth(str)) / 2.0F,
                n2 + (n4 - drawStringWithShadowComponent.FONT_HEIGHT) / 2.0F + 1.0F,
                new Color(n6, n6, n6, n7).getRGB()
            );
        }
    }

    public static void handleInt(int n, int n2, int n3, int n4, Color color2, boolean flag) {
        if (!checkState()) {
            Gui.drawRect(n, n2, n + n3, n2 + n4, computeColor(color2, 40).getRGB());
        } else {
            OutlineThicknessHelper.handleFloat(n, n2, n3, n4, n4 / 2.0F, flag ? computeColor(color2, 52) : new Color(255, 255, 255, 10));
            OutlineThicknessHelper.handleFloat(n, n2, n3, n4, n4 / 2.0F, 0.5F, color, flag ? computeColor(color2, 120) : new Color(255, 255, 255, 30));
        }
    }

    public static void handleRectangle(
        Rectangle rect,
        String str,
        String str2,
        CustomFontRenderer drawStringWithShadowComponent,
        CustomFontRenderer drawStringWithShadowComponent2,
        float f,
        boolean flag,
        int[] ints,
        BaseComponent.ThemesMode themesMode
    ) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            process();
            float f2 = flag ? 1.0F : f;
            int n = rect.x;
            int n2 = rect.y;
            int width = rect.width;
            int height = rect.height;
            Gui.drawRect(n, n2, n + width, n2 + height, new Color(26, 26, 26, (int)(82.0F + 30.0F * f2)).getRGB());
            if (checkState()) {
                OutlineThicknessHelper.handleFloat(n, n2, width, height, 0.0F, 0.5F, color, new Color(26, 26, 26, (int)(122.0F + 30.0F * f2)));
            }

            int n3 = (int)(201.0F + 25.0F * f2);
            GlStateManager.enableTexture2D();
            int n4 = themesMode == null ? 0 : 16;
            int n5 = n4 + drawStringWithShadowComponent.getStringWidth(str) + (str2.isEmpty() ? 0 : 6 + drawStringWithShadowComponent2.getStringWidth(str2));
            int n6 = n + (width - n5) / 2;
            if (themesMode != null) {
                float f3 = Math.min(11, height - 6);
                BaseComponent.handleThemesMode(
                    themesMode, n6, n2 + (height - f3) / 2.0F, f3, new Color(n3, n3, n3, (int)(200.0F + 55.0F * f2)).getRGB()
                );
                n6 += n4;
                GlStateManager.enableTexture2D();
            }

            drawStringWithShadowComponent.drawString(str, n6, n2 + (height - drawStringWithShadowComponent.FONT_HEIGHT) / 2 + 1, new Color(n3, n3, n3, (int)(190.0F + 60.0F * f2)).getRGB());
            if (!str2.isEmpty()) {
                drawStringWithShadowComponent2.drawString(
                    str2, n6 + drawStringWithShadowComponent.getStringWidth(str) + 6, n2 + (height - drawStringWithShadowComponent2.FONT_HEIGHT) / 2 + 1, new Color(ints[0], ints[1], ints[2], 230).getRGB()
                );
            }
        }
    }

    public static void handleFloat(float f, float f2, float f3, float f4, Color color) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            float f5 = color.getRed() / 255.0F;
            float f6 = color.getGreen() / 255.0F;
            float f7 = color.getBlue() / 255.0F;
            float f8 = color.getAlpha() / 255.0F;
            float f9 = f + f3 / 2.0F;
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(f5, f6, f7, 0.0F);
            GL11.glVertex2f(f, f2 + f4);
            GL11.glVertex2f(f, f2);
            GL11.glColor4f(f5, f6, f7, f8);
            GL11.glVertex2f(f9, f2);
            GL11.glVertex2f(f9, f2 + f4);
            GL11.glColor4f(f5, f6, f7, f8);
            GL11.glVertex2f(f9, f2 + f4);
            GL11.glVertex2f(f9, f2);
            GL11.glColor4f(f5, f6, f7, 0.0F);
            GL11.glVertex2f(f + f3, f2);
            GL11.glVertex2f(f + f3, f2 + f4);
            GL11.glEnd();
            GlStateManager.shadeModel(GL11.GL_FLAT);
            GlStateManager.enableTexture2D();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public static int computeInt(String str, String str2, CustomFontRenderer drawStringWithShadowComponent, CustomFontRenderer drawStringWithShadowComponent2, boolean flag) {
        return (flag ? 16 : 0) + drawStringWithShadowComponent.getStringWidth(str) + (str2.isEmpty() ? 0 : 6 + drawStringWithShadowComponent2.getStringWidth(str2)) + 16;
    }

    public static void handleInt(int n, int n2, int n3, int n4) {
        handleFloat(n, n2, n3, 1.0F, new Color(255, 255, 255, n4));
    }

    public static void handleRectangle(
        Rectangle rect, String str, String str2, CustomFontRenderer drawStringWithShadowComponent, CustomFontRenderer drawStringWithShadowComponent2, float f, boolean flag, int[] ints
    ) {
        handleRectangle(rect, str, str2, drawStringWithShadowComponent, drawStringWithShadowComponent2, f, flag, ints, null);
    }

    private static Color computeColor(Color color, Color color2, float f) {
        float f2 = Math.max(0.0F, Math.min(1.0F, f));
        return new Color(
            (int)(color.getRed() + (color2.getRed() - color.getRed()) * f2),
            (int)(color.getGreen() + (color2.getGreen() - color.getGreen()) * f2),
            (int)(color.getBlue() + (color2.getBlue() - color.getBlue()) * f2),
            (int)(color.getAlpha() + (color2.getAlpha() - color.getAlpha()) * f2)
        );
    }

    private static boolean checkState() {
        return OpenGlHelper.shadersSupported && OutlineThicknessHelper.shaderEffect != null && OutlineThicknessHelper.shaderEffect2 != null;
    }

    private static float computeFloat(Color color) {
        return (0.2126F * color.getRed() + 0.7152F * color.getGreen() + 0.0722F * color.getBlue()) / 255.0F;
    }

    private static Color computeColor2(Color color, float f) {
        return computeColor2(color, Color.WHITE, f);
    }

    public static Color computeColor(Color color, int n) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, n)));
    }

    private static Color computeColor2(Color color, Color color2, float f) {
        float f2 = Math.max(0.0F, Math.min(1.0F, f));
        return new Color(
            (int)(color.getRed() + (color2.getRed() - color.getRed()) * f2),
            (int)(color.getGreen() + (color2.getGreen() - color.getGreen()) * f2),
            (int)(color.getBlue() + (color2.getBlue() - color.getBlue()) * f2)
        );
    }

    public static void handleInt(int n, int n2, int n3, int n4, float f, float f2) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            process();
            if (!checkState()) {
                Gui.drawRect(n, n2, n + n3, n2 + n4, new Color(9, 10, 16, (int)(225.0F * f2)).getRGB());
                Gui.drawRect(n, n2, n + n3, n2 + 1, 620756991);
                return;
            }

            handleFloat(n, n2, n3, n4, f, (int)(70.0F * Math.min(1.0F, f2)));
            Color color2 = new Color(18, 19, 28, Math.min(255, (int)(205.0F * f2)));
            Color color3 = new Color(8, 9, 14, Math.min(255, (int)(228.0F * f2)));
            OutlineThicknessHelper.handleFloat(n, n2, n3, n4, f, color2, color3);
            OutlineThicknessHelper.handleFloat(n, n2, n3, n4, f, 0.5F, color, new Color(255, 255, 255, 26));
            handleFloat(n + f, n2 + 0.5F, n3 - f * 2.0F, 1.0F, new Color(255, 255, 255, 30));
        }
    }

    public static void handleInt(int n, int n2, int n3, int n4, String str, CustomFontRenderer drawStringWithShadowComponent, Color color2, float f, boolean flag) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            process();
            float f2 = Math.min(8.0F, n4 / 2.0F);
            Color color3;
            if (checkState()) {
                if (flag) {
                    Color color4 = computeColor2(color2, 0.1F + 0.12F * f);
                    Color color5 = computeColor(color2, 0.82F + 0.08F * f);
                    OutlineThicknessHelper.handleFloat(n, n2, n3, n4, f2, color4, color5);
                    OutlineThicknessHelper.handleFloat(n, n2, n3, n4, f2, 0.5F, color, new Color(255, 255, 255, 40 + (int)(30.0F * f)));
                    color3 = computeFloat(color2) > 0.55F ? new Color(14, 16, 22) : Color.WHITE;
                } else {
                    OutlineThicknessHelper.handleFloat(n, n2, n3, n4, f2, new Color(255, 255, 255, 8 + (int)(12.0F * f)));
                    OutlineThicknessHelper.handleFloat(
                        n, n2, n3, n4, f2, 0.5F, color, computeColor(new Color(255, 255, 255, 28), computeColor(color2, 140), f)
                    );
                    color3 = computeColor2(new Color(214, 220, 230), Color.WHITE, f);
                }
            } else {
                Gui.drawRect(n, n2, n + n3, n2 + n4, flag ? color2.getRGB() : 587202559);
                color3 = flag ? new Color(14, 16, 22) : Color.WHITE;
            }

            drawStringWithShadowComponent.drawString(str, n + (n3 - drawStringWithShadowComponent.getStringWidth(str)) / 2, n2 + (n4 - drawStringWithShadowComponent.FONT_HEIGHT) / 2 + 1, color3.getRGB());
        }
    }

    public static int[] computeIntArray(Rectangle rect, String str, CustomFontRenderer drawStringWithShadowComponent) {
        int n = rect.x + 10 + drawStringWithShadowComponent.getStringWidth(str) + 9;
        int n2 = rect.x + rect.width - 10 - drawStringWithShadowComponent.getStringWidth("100%") - 8;
        return new int[]{n, Math.max(10, n2 - n)};
    }

    public static void handleInt(int n, int n2, int n3, int n4, String str, float f, int[] ints) {
        handleInt(n, n2, n3, n4, str, FontComponent.computeDrawStringWithShadowComponent(), new Color(ints[0], ints[1], ints[2]), f, false);
    }

    public static void handleRectangle(Rectangle rect, String str, int n, CustomFontRenderer drawStringWithShadowComponent, float f, boolean flag, int[] ints) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            process();
            int n2 = rect.x;
            int n3 = rect.y;
            int width = rect.width;
            int height = rect.height;
            float f2 = height / 2.0F;
            float f3 = flag ? 1.0F : f;
            Color color2 = new Color(ints[0], ints[1], ints[2]);
            int[] ints2 = computeIntArray(rect, str, drawStringWithShadowComponent);
            float f4 = Math.max(0, Math.min(100, n)) / 100.0F;
            float f5 = n3 + height / 2.0F;
            Gui.drawRect(n2, n3, n2 + width, n3 + height, new Color(26, 26, 26, (int)(82.0F + 30.0F * f3)).getRGB());
            if (checkState()) {
                OutlineThicknessHelper.handleFloat(n2, n3, width, height, 0.0F, 0.5F, color, new Color(26, 26, 26, (int)(122.0F + 30.0F * f3)));
                OutlineThicknessHelper.handleFloat(ints2[0], f5 - 1.5F, ints2[1], 3.0F, 1.5F, new Color(255, 255, 255, 34));
                if (f4 > 0.0F) {
                    OutlineThicknessHelper.handleFloat(ints2[0], f5 - 1.5F, ints2[1] * f4, 3.0F, 1.5F, color2);
                }

                float f6 = 7.0F + 2.0F * f3;
                float f7 = ints2[0] + ints2[1] * f4;
                OutlineThicknessHelper.handleFloat(
                    f7 - f6 / 2.0F - 2.0F,
                    f5 - f6 / 2.0F - 2.0F,
                    f6 + 4.0F,
                    f6 + 4.0F,
                    (f6 + 4.0F) / 2.0F,
                    computeColor(color2, (int)(60.0F * f3))
                );
                OutlineThicknessHelper.handleFloat(f7 - f6 / 2.0F, f5 - f6 / 2.0F, f6, f6, f6 / 2.0F, new Color(240, 243, 248));
            } else {
                Gui.drawRect(n2, n3, n2 + width, n3 + height, new Color(10, 11, 17, 190).getRGB());
                Gui.drawRect(ints2[0], (int)f5 - 1, ints2[0] + (int)(ints2[1] * f4), (int)f5 + 1, color2.getRGB());
            }

            int n4 = n3 + (height - drawStringWithShadowComponent.FONT_HEIGHT) / 2 + 1;
            drawStringWithShadowComponent.drawString(str, n2 + 10, n4, computeColor2(new Color(190, 198, 210), Color.WHITE, f3).getRGB());
            String str2 = n + "%";
            drawStringWithShadowComponent.drawString(str2, n2 + width - 10 - drawStringWithShadowComponent.getStringWidth(str2), n4, computeColor2(new Color(170, 178, 192), Color.WHITE, f3).getRGB());
        }
    }

    private MainMenuIconRenderer() {
    }
}
