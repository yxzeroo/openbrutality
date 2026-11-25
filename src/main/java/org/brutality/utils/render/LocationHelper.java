package org.brutality.utils.render;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.render.shader.ShaderProgram;
import org.lwjgl.opengl.GL11;

public class LocationHelper {
    public static ShaderProgram shaderProgram = new ShaderProgram("roundedRect");
    public static ShaderProgram shaderProgram2 = new ShaderProgram("roundRectOutline");
    private static final ShaderProgram shaderProgram3 = new ShaderProgram("roundRectTexture");
    private static final ShaderProgram shaderProgram4 = new ShaderProgram("roundedRectGradient");

    public static void handleFloat(
        float f, float f2, float f3, float f4, float f5, Color color, float f6, Color color2, float f7, float f8, float f9, float f10
    ) {
        f10 = Math.max(0.0F, Math.min(1.0F, f10));
        int n = (int)Math.min(255.0F, Math.max(0.0F, color.getAlpha() * f10));
        handleFloat2(f, f2, f3, f4, f5, new Color(color.getRed(), color.getGreen(), color.getBlue(), n));
        int n2 = (int)Math.min(255.0F, Math.max(0.0F, 255.0F * f7 * f10));
        if (n2 > 1) {
            handleFloat(f, f2, f3, f4, f5, 1.0F, new Color(0, 0, 0, 0), new Color(color2.getRed(), color2.getGreen(), color2.getBlue(), n2));
        }
    }

    public static void handleFloat4(float f, float f2, float f3, float f4, float f5, Color color, Color color2) {
        Color color3 = GetColorHelper.computeColor(color, color2, 0.5F);
        handleFloat(f, f2, f3, f4, f5, color3, color, color2, color3);
    }

    private static void handleFloat(float f, float f2, float f3, float f4, float f5, ShaderProgram shaderProgram) {
        ScaledResolution sr = InvalidateHelper.computeScaledResolution(Minecraft.getMinecraft());
        shaderProgram.handleString(
            "location", f * sr.getScaleFactor(), Minecraft.getMinecraft().displayHeight - f4 * sr.getScaleFactor() - f2 * sr.getScaleFactor()
        );
        shaderProgram.handleString("rectSize", f3 * sr.getScaleFactor(), f4 * sr.getScaleFactor());
        shaderProgram.handleString("radius", f5 * sr.getScaleFactor());
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, float f6) {
        BindTextureHelper.resetColor();
        BindTextureHelper.handleFloat(0.0F);
        DisableDepthHelper.process6();
        shaderProgram3.init();
        shaderProgram3.handleString("textureIn", 0);
        handleFloat(f, f2, f3, f4, f5, shaderProgram3);
        shaderProgram3.handleString("alpha", f6);
        ShaderProgram.handleFloat(f - 1.0F, f2 - 1.0F, f3 + 2.0F, f4 + 2.0F);
        shaderProgram3.unload();
        DisableDepthHelper.process5();
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, Color color, float f6) {
        handleFloat(f, f2, f3, f4, f5, color, 0.35F, new Color(255, 255, 255), 0.55F, 2.2F, 0.012F, f6);
    }

    public static void handleFloat2(float f, float f2, float f3, float f4, float f5, Color color, Color color2) {
        Color color3 = GetColorHelper.computeColor(color2, color, 0.5F);
        handleFloat(f, f2, f3, f4, f5, color, color3, color3, color2);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, Color color) {
        BindTextureHelper.resetColor();
        DisableDepthHelper.process6();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        BindTextureHelper.handleFloat(0.0F);
        shaderProgram.init();

        try {
            shaderProgram.handleString("blur", 0);
            shaderProgram.handleString("color", color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);

            for (int i = 4; i >= 1; i--) {
                float f6 = i * 1.5F;
                float f7 = f - f6;
                float f8 = f2 - f6 + 2.0F;
                float f9 = f3 + f6 * 2.0F;
                float f10 = f4 + f6 * 2.0F;
                handleFloat(f7, f8, f9, f10, f5 + f6, shaderProgram);
                ShaderProgram.handleFloat(f7 - 1.0F, f8 - 1.0F, f9 + 2.0F, f10 + 2.0F);
            }
        } finally {
            shaderProgram.unload();
            DisableDepthHelper.process5();
        }
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, boolean flag, Color color) {
        BindTextureHelper.resetColor();
        DisableDepthHelper.process6();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        BindTextureHelper.handleFloat(0.0F);
        shaderProgram.init();
        handleFloat(f, f2, f3, f4, f5, shaderProgram);
        shaderProgram.handleString("blur", flag ? 1 : 0);
        shaderProgram.handleString("color", color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
        ShaderProgram.handleFloat(f - 1.0F, f2 - 1.0F, f3 + 2.0F, f4 + 2.0F);
        shaderProgram.unload();
        DisableDepthHelper.process5();
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, float f6, Color color, Color color2) {
        BindTextureHelper.resetColor();
        DisableDepthHelper.process6();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        BindTextureHelper.handleFloat(0.0F);
        shaderProgram2.init();
        ScaledResolution sr = InvalidateHelper.computeScaledResolution(Minecraft.getMinecraft());
        handleFloat(f, f2, f3, f4, f5, shaderProgram2);
        shaderProgram2.handleString("outlineThickness", f6 * sr.getScaleFactor());
        shaderProgram2.handleString("color", color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
        shaderProgram2.handleString("outlineColor", color2.getRed() / 255.0F, color2.getGreen() / 255.0F, color2.getBlue() / 255.0F, color2.getAlpha() / 255.0F);
        ShaderProgram.handleFloat(f - (2.0F + f6), f2 - (2.0F + f6), f3 + (4.0F + f6 * 2.0F), f4 + (4.0F + f6 * 2.0F));
        shaderProgram2.unload();
        DisableDepthHelper.process5();
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, Color color, Color color2, Color color3, Color color4) {
        BindTextureHelper.handleFloat(0.0F);
        BindTextureHelper.resetColor();
        DisableDepthHelper.process6();
        shaderProgram4.init();
        handleFloat(f, f2, f3, f4, f5, shaderProgram4);
        shaderProgram4.handleString("color1", color2.getRed() / 255.0F, color2.getGreen() / 255.0F, color2.getBlue() / 255.0F, color2.getAlpha() / 255.0F);
        shaderProgram4.handleString("color2", color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
        shaderProgram4.handleString("color3", color4.getRed() / 255.0F, color4.getGreen() / 255.0F, color4.getBlue() / 255.0F, color4.getAlpha() / 255.0F);
        shaderProgram4.handleString("color4", color3.getRed() / 255.0F, color3.getGreen() / 255.0F, color3.getBlue() / 255.0F, color3.getAlpha() / 255.0F);
        ShaderProgram.handleFloat(f - 1.0F, f2 - 1.0F, f3 + 2.0F, f4 + 2.0F);
        shaderProgram4.unload();
        DisableDepthHelper.process5();
    }

    public static void handleFloat2(float f, float f2, float f3, float f4, float f5, Color color) {
        handleFloat(f, f2, f3, f4, f5, false, color);
    }

    public static void handleFloat3(float f, float f2, float f3, float f4, float f5, Color color, Color color2) {
        handleFloat(f, f2, f3, f4, f5, color, color, color2, color2);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, Color color, Color color2) {
        handleFloat(f, f2, f3, f4, f5, color2, color, color2, color);
    }
}
