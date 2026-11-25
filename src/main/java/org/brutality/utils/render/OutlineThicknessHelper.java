package org.brutality.utils.render;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.brutality.utils.mcp.McpApi2;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.render.shader.ShaderEffect;
import org.lwjgl.opengl.GL11;

public class OutlineThicknessHelper implements McpApi2 {
    public static ShaderEffect shaderEffect;
    public static ShaderEffect shaderEffect2;
    private static ShaderEffect shaderEffect3;
    private static ShaderEffect shaderEffect4;

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, Color color) {
        handleFloat(f, f2, f3, f4, f5, false, color);
    }

    public static void handleFloat2(float f, float f2, float f3, float f4, float f5, Color color, Color color2) {
        Color color3 = GetColorHelper.computeColor(color2, color, 0.5F);
        handleFloat(f, f2, f3, f4, f5, color, color3, color3, color2);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, float f6, Color color, Color color2) {
        if (shaderEffect2 != null) {
            ResetColorHelper.resetColor();
            EnableDepthHelper.process5();
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            ResetColorHelper.handleFloat2(0.0F);
            shaderEffect2.init();
            ScaledResolution sr = InvalidateHelper.computeScaledResolution(Minecraft.getMinecraft());
            handleFloat(f, f2, f3, f4, f5, shaderEffect2);
            shaderEffect2.handleString("outlineThickness", f6 * sr.getScaleFactor());
            shaderEffect2.handleString("color", color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
            shaderEffect2.handleString("outlineColor", color2.getRed() / 255.0F, color2.getGreen() / 255.0F, color2.getBlue() / 255.0F, color2.getAlpha() / 255.0F);
            ShaderEffect.handleFloat(f - (2.0F + f6), f2 - (2.0F + f6), f3 + (4.0F + f6 * 2.0F), f4 + (4.0F + f6 * 2.0F));
            shaderEffect2.unload();
            EnableDepthHelper.process6();
        }
    }

    static {
        try {
            shaderEffect = new ShaderEffect("roundedRect");
            shaderEffect2 = new ShaderEffect("roundRectOutline");
            shaderEffect3 = new ShaderEffect("roundRectTexture");
            shaderEffect4 = new ShaderEffect("roundedRectGradient");
        } catch (Exception e) {
            e.printStackTrace();
            shaderEffect = null;
            shaderEffect2 = null;
            shaderEffect3 = null;
            shaderEffect4 = null;
        }
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, float f6) {
        if (shaderEffect3 != null) {
            ResetColorHelper.resetColor();
            ResetColorHelper.handleFloat2(0.0F);
            EnableDepthHelper.process5();
            shaderEffect3.init();
            shaderEffect3.handleString("textureIn", 0);
            handleFloat(f, f2, f3, f4, f5, shaderEffect3);
            shaderEffect3.handleString("alpha", f6);
            ShaderEffect.handleFloat(f - 1.0F, f2 - 1.0F, f3 + 2.0F, f4 + 2.0F);
            shaderEffect3.unload();
            EnableDepthHelper.process6();
        }
    }

    public static void handleFloat4(float f, float f2, float f3, float f4, float f5, Color color, Color color2) {
        Color color3 = GetColorHelper.computeColor(color, color2, 0.5F);
        handleFloat(f, f2, f3, f4, f5, color3, color, color2, color3);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, boolean flag, Color color) {
        if (shaderEffect != null) {
            ResetColorHelper.resetColor();
            EnableDepthHelper.process5();
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            ResetColorHelper.handleFloat2(0.0F);
            shaderEffect.init();
            handleFloat(f, f2, f3, f4, f5, shaderEffect);
            shaderEffect.handleString("blur", flag ? 1 : 0);
            shaderEffect.handleString("color", color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
            ShaderEffect.handleFloat(f - 1.0F, f2 - 1.0F, f3 + 2.0F, f4 + 2.0F);
            shaderEffect.unload();
            EnableDepthHelper.process6();
        }
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, Color color, Color color2, Color color3, Color color4) {
        if (shaderEffect4 != null) {
            ResetColorHelper.handleFloat2(0.0F);
            ResetColorHelper.resetColor();
            EnableDepthHelper.process5();
            shaderEffect4.init();
            handleFloat(f, f2, f3, f4, f5, shaderEffect4);
            shaderEffect4.handleString("color1", color2.getRed() / 255.0F, color2.getGreen() / 255.0F, color2.getBlue() / 255.0F, color2.getAlpha() / 255.0F);
            shaderEffect4.handleString("color2", color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
            shaderEffect4.handleString("color3", color4.getRed() / 255.0F, color4.getGreen() / 255.0F, color4.getBlue() / 255.0F, color4.getAlpha() / 255.0F);
            shaderEffect4.handleString("color4", color3.getRed() / 255.0F, color3.getGreen() / 255.0F, color3.getBlue() / 255.0F, color3.getAlpha() / 255.0F);
            ShaderEffect.handleFloat(f - 1.0F, f2 - 1.0F, f3 + 2.0F, f4 + 2.0F);
            shaderEffect4.unload();
            EnableDepthHelper.process6();
        }
    }

    private static void handleFloat(float f, float f2, float f3, float f4, float f5, ShaderEffect shaderEffect) {
        if (shaderEffect != null) {
            ScaledResolution sr = InvalidateHelper.computeScaledResolution(Minecraft.getMinecraft());
            shaderEffect.handleString(
                "location", f * sr.getScaleFactor(), Minecraft.getMinecraft().displayHeight - f4 * sr.getScaleFactor() - f2 * sr.getScaleFactor()
            );
            shaderEffect.handleString("rectSize", f3 * sr.getScaleFactor(), f4 * sr.getScaleFactor());
            shaderEffect.handleString("radius", f5 * sr.getScaleFactor());
        }
    }

    public static void handleFloat3(float f, float f2, float f3, float f4, float f5, Color color, Color color2) {
        handleFloat(f, f2, f3, f4, f5, color, color, color2, color2);
    }

    public static void handleFloat(float f, float f2, float f3, float f4, float f5, Color color, Color color2) {
        handleFloat(f, f2, f3, f4, f5, color2, color, color2, color);
    }
}
