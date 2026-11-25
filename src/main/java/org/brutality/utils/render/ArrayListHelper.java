package org.brutality.utils.render;

import java.awt.Color;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.visual.ArrayList;
import org.lwjgl.opengl.GL11;

public final class ArrayListHelper {
    private static final float[] floatValueArray = new float[]{0.0F, 0.0F, 7.0F, 7.0F, 0.0F, 7.0F, 7.0F, 0.0F, 0.0F, 5.0F, 2.0F, 7.0F, 5.0F, 0.0F, 7.0F, 2.0F};
    private static final float[] floatValueArray2 = new float[]{0.0F, 7.0F, 3.0F, 0.0F, 3.0F, 0.0F, 7.0F, 7.0F, 1.0F, 5.0F, 6.0F, 5.0F};
    private static final float[] floatValueArray3 = new float[]{
        0.0F, 3.0F, 3.0F, 0.0F, 3.0F, 0.0F, 7.0F, 3.0F, 7.0F, 3.0F, 3.0F, 6.0F, 3.0F, 6.0F, 0.0F, 3.0F, 3.0F, 2.0F, 3.0F, 4.0F
    };
    private static final float[] floatValueArray4 = new float[]{
        2.0F,
        0.0F,
        5.0F,
        0.0F,
        5.0F,
        0.0F,
        5.0F,
        3.0F,
        5.0F,
        3.0F,
        2.0F,
        3.0F,
        2.0F,
        3.0F,
        2.0F,
        0.0F,
        0.0F,
        7.0F,
        1.0F,
        5.0F,
        1.0F,
        5.0F,
        6.0F,
        5.0F,
        6.0F,
        5.0F,
        7.0F,
        7.0F
    };
    private static final float[] floatValueArray5 = new float[]{
        0.0F,
        2.0F,
        3.0F,
        0.0F,
        3.0F,
        0.0F,
        7.0F,
        2.0F,
        7.0F,
        2.0F,
        3.0F,
        4.0F,
        3.0F,
        4.0F,
        0.0F,
        2.0F,
        0.0F,
        2.0F,
        0.0F,
        6.0F,
        0.0F,
        6.0F,
        3.0F,
        8.0F,
        3.0F,
        8.0F,
        7.0F,
        6.0F,
        7.0F,
        6.0F,
        7.0F,
        2.0F,
        3.0F,
        4.0F,
        3.0F,
        8.0F
    };
    private static final float[] floatValueArray6 = new float[]{4.0F, 0.0F, 1.0F, 4.0F, 1.0F, 4.0F, 5.0F, 4.0F, 5.0F, 4.0F, 2.0F, 8.0F};
    private static final float[] floatValueArray7 = new float[]{
        0.0F, 1.0F, 7.0F, 1.0F, 7.0F, 1.0F, 7.0F, 6.0F, 7.0F, 6.0F, 0.0F, 6.0F, 0.0F, 6.0F, 0.0F, 1.0F, 2.0F, 8.0F, 5.0F, 8.0F, 3.0F, 6.0F, 3.0F, 8.0F
    };
    private static final float[] floatValueArray8 = new float[]{0.0F, 3.0F, 7.0F, 3.0F, 3.0F, 0.0F, 3.0F, 7.0F};

    public static void handleFloat(float f, float f2, float f3, float f4) {
        handleFloat(f, f2, f3, f4, 4.0F);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5) {
        handleFloat(f, f2, f3, f4, f5, new Color(12, 14, 22, 190));
        handleFloat(f + 1.0F, f2 + 1.0F, f3 - 2.0F, 1.0F, 0.0F, new Color(255, 255, 255, 18));
    }

    public static String computeString(FontRenderer fontRenderer, String str, int n) {
        if (str == null) {
            return "--";
        }
        String str2 = str.replace('\n', ' ').replace('\r', ' ');
        if (fontRenderer.getStringWidth(str2) <= n) {
            return str2;
        }
        while (!str2.isEmpty() && fontRenderer.getStringWidth(str2 + "...") > n) {
            str2 = str2.substring(0, str2.length() - 1);
        }

        return str2 + "...";
    }

    public static void handleCategory(Category category, float f, float f2, int n) {
        float[] floats = switch (category) {
            case COMBAT -> floatValueArray;
            case MOVEMENT -> floatValueArray2;
            case VISUAL -> floatValueArray3;
            case PLAYER -> floatValueArray4;
            case PIT -> floatValueArray5;
            case EXPLOIT -> floatValueArray6;
            case CLIENT -> floatValueArray7;
            default -> floatValueArray8;
        };
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.color((n >> 16 & 0xFF) / 255.0F, (n >> 8 & 0xFF) / 255.0F, (n & 0xFF) / 255.0F, 1.0F);
        GL11.glLineWidth(1.2F);
        GL11.glBegin(GL11.GL_LINES);

        for (int i = 0; i < floats.length; i += 2) {
            GL11.glVertex2f(f + floats[i], f2 + floats[i + 1]);
        }

        GL11.glEnd();
        GL11.glLineWidth(1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static Color computeColor(Module module, String str) {
        if ("ArrayList".equals(str)) {
            return ArrayList.computeColor(module);
        }
        Color color = ModuleSpecificHelper.computeBrutalityHelper().getColor();
        return color == null ? Color.WHITE : color;
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, Color color, Color color2) {
        if (OpenGlHelper.shadersSupported) {
            OutlineThicknessHelper.handleFloat3(f, f2, f3, f4, f5, color, color2);
        } else {
            Gui.drawRect((int)f, (int)f2, (int)(f + f3), (int)(f2 + f4), color.getRGB());
        }
    }

    public static void handleFontRenderer(FontRenderer fontRenderer, String str, float f, float f2, Color color, Color color2) {
        for (int i = 0; i < str.length(); i++) {
            float f3 = (float)i / Math.max(1, str.length() - 1);
            int n = 0xFF000000
                | (int)(color.getRed() + (color2.getRed() - color.getRed()) * f3) << 16
                | (int)(color.getGreen() + (color2.getGreen() - color.getGreen()) * f3) << 8;
            int n2 = (int)(color.getBlue() + (color2.getBlue() - color.getBlue()) * f3);
            int n3 = n2 + (n & ~n2);
            String str2 = str.substring(i, i + 1);
            fontRenderer.drawStringWithShadow(str2, f, f2, n3);
            f += fontRenderer.getStringWidth(str2);
        }
    }

    public static int computeInt(Color color, Color color2) {
        return (color.getRed() + color2.getRed()) * 0.1063 + (color.getGreen() + color2.getGreen()) * 0.3576 + (color.getBlue() + color2.getBlue()) * 0.0361 > 170.0
            ? -15460059
            : -1;
    }

    private ArrayListHelper() {
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, Color color) {
        if (!(f3 <= 0.0F) && !(f4 <= 0.0F)) {
            if (OpenGlHelper.shadersSupported) {
                OutlineThicknessHelper.handleFloat(f, f2, f3, f4, f5, color);
            } else {
                Gui.drawRect((int)f, (int)f2, (int)Math.ceil(f + f3), (int)Math.ceil(f2 + f4), color.getRGB());
            }
        }
    }

    public static Color computeColor2(Module module, String str) {
        if ("ArrayList".equals(str)) {
            return computeColor(module, str).brighter();
        }
        Color color2 = ModuleSpecificHelper.computeBrutalityHelper().getColor2();
        return color2 == null ? computeColor(module, str).brighter() : color2;
    }
}
