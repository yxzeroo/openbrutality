package org.brutality.utils.render;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public class RenderHelper10 {
    public static void handleFontRenderer(FontRenderer fontRenderer, String str, int n, int n2, int n3) {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            int n4 = computeInt(n3, i * 0.2F);
            fontRenderer.drawStringWithShadow(String.valueOf(ch), n, n2, n4);
            n += fontRenderer.getCharWidth(ch);
        }
    }

    public static void handleString(String str, Minecraft mc, int n, int n2, boolean flag, int n3) {
        new ScaledResolution(mc);
        int textWidth = mc.fontRendererObj.getStringWidth(str);
        int n4 = n - textWidth / 2;
        if (flag) {
            mc.fontRendererObj.drawStringWithShadow(str, n4, n2, n3);
        } else {
            mc.fontRendererObj.drawString(str, n4, n2, n3);
        }
    }

    private static int computeInt(int n, int n2, float f) {
        int n3 = (int)((n >> 16 & 0xFF) * (1.0F - f) + ((0xFF | ~(n2 >> 16)) - ~(n2 >> 16)) * f);
        int n4 = (int)((n >> 8 & 0xFF) * (1.0F - f) + ((0xFF | ~(n2 >> 8)) - ~(n2 >> 8)) * f);
        int n5 = (int)((n & 0xFF) * (1.0F - f) + (n2 & 0xFF) * f);
        return n3 << 16 | n4 << 8 | n5;
    }

    public static void handleFontRenderer(FontRenderer fontRenderer, String str, int n, int n2, int n3, int n4, int n5) {
        int textWidth = fontRenderer.getStringWidth(str);

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            int n6 = computeInt(n3, n4, (float)i / str.length());
            fontRenderer.drawStringWithShadow(String.valueOf(ch), n, n2, n6);
            n += fontRenderer.getCharWidth(ch);
        }
    }

    public static void handleString(String str, double d, double d2, int n, float f) {
        GL11.glPushMatrix();
        GL11.glScalef(f, f, f);
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(str, (float)d / f, (float)d2 / f, n);
        GL11.glPopMatrix();
    }

    private static int computeInt(int n, float f) {
        float f2 = ((float)System.currentTimeMillis() + f * 1000.0F) % 360.0F / 360.0F;
        return Color.HSBtoRGB(f2, 1.0F, 1.0F);
    }
}
