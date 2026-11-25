package org.brutality.module.impl.visual.interfaces;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.utils.render.FontResourceLoader;
import org.lwjgl.opengl.GL11;

public class AtmosphereHandler {
    private final Minecraft mc;
    private FontRenderer fontRenderer;
    private static final ResourceLocation resourceLocation = new ResourceLocation("brutality/atmosphere_logo.png");
    private static final String text = "Atmosphere";
    private static final String text2 = "0.3";
    private static final String text3 = "(Development)";
    private static final String text4 = "hypixel.net";
    public static Color color = new Color(128, 128, 255);
    public static Color color2 = new Color(148, 148, 255);
    public static Color color3 = new Color(35, 35, 35);
    public static Color color4 = new Color(20, 20, 20);
    public static Color color5 = new Color(80, 80, 80);
    public static Color color6 = new Color(10, 10, 10, 50);
    public static Color color7 = new Color(10, 10, 10, 100);
    public static Color color8 = new Color(10, 10, 10, 200);
    public static Color color9 = new Color(60, 60, 60, 50);
    public static Color color10 = new Color(40, 40, 40, 100);
    public static Color color11 = new Color(20, 20, 20, 200);
    private static final int count = 6;
    private static final int count2 = 4;
    private static final int count3 = 2;
    private static final int count4 = 12;
    private static final int count5 = 5;
    private static final int count6 = 18;
    private final Map<String, CustomFontRenderer> z = new HashMap<>();
    private String text5 = "";
    private float factor = 1.0F;

    public void handleScaledResolution(ScaledResolution sr, float f, float f2, String str, float f3) {
        FontRenderer fontRenderer = this.computeFontRenderer(str, f3);
        int n = (int)(6.0F * f3);
        int n2 = (int)(4.0F * f3);
        int n3 = (int)(2.0F * f3);
        int n4 = (int)(12.0F * f3);
        int n5 = (int)(5.0F * f3);
        int textWidth = fontRenderer.getStringWidth("Atmosphere");
        int textWidth2 = fontRenderer.getStringWidth(" 0.3");
        int textWidth3 = fontRenderer.getStringWidth(" (Development)");
        int textWidth4 = fontRenderer.getStringWidth("  ");
        int textWidth5 = fontRenderer.getStringWidth("hypixel.net");
        int FONT_HEIGHT = fontRenderer.FONT_HEIGHT;
        int n6 = n4 + n5 + textWidth + textWidth2 + textWidth3 + textWidth4 + textWidth5;
        int n7 = n * 2 + n6;
        int n8 = n2 * 2 + Math.max(n4, FONT_HEIGHT) + n3;
        int n9 = (int)f;
        int n10 = (int)f2;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        Gui.drawRect(n9, n10, n9 + n7, n10 + n8, color8.getRGB());
        Gui.drawRect(n9, n10, n9 + n7, n10 + n3, color.getRGB());
        int n11 = n9 + n;
        int n12 = n10 + n3 + n2 + (Math.max(n4, FONT_HEIGHT) - n4) / 2;
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(resourceLocation);
        Gui.drawModalRectWithCustomSizedTexture(n11, n12, 0.0F, 0.0F, n4, n4, n4, n4);
        int n13 = n11 + n4 + n5;
        int n14 = n10 + n3 + n2;
        fontRenderer.drawString("Atmosphere", n13, n14, Color.WHITE.getRGB(), false);
        int n15 = n13 + textWidth;
        fontRenderer.drawString(" 0.3", n15, n14, Color.LIGHT_GRAY.getRGB(), false);
        n15 += textWidth2;
        fontRenderer.drawString(" (Development)", n15, n14, Color.LIGHT_GRAY.getRGB(), false);
        n15 += textWidth3;
        fontRenderer.drawString("  hypixel.net", n15, n14, Color.WHITE.getRGB(), false);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private FontRenderer computeFontRenderer(String str, int n) {
        return FontResourceLoader.computeDrawStringWithShadowComponent(str, n);
    }

    private FontRenderer computeFontRenderer(String str, float factor) {
        int n = (int)(18.0F * factor);
        String str2 = str + "_" + n;
        if (!str.equals(this.text5) || factor != this.factor) {
            this.text5 = str;
            this.factor = factor;
        }

        if (this.z.containsKey(str2)) {
            return this.z.get(str2);
        }
        FontRenderer fontRenderer = this.computeFontRenderer(str, n);
        if (fontRenderer != null) {
            this.z.put(str2, (CustomFontRenderer)fontRenderer);
            return fontRenderer;
        }
        return this.mc.fontRendererObj;
    }

    public AtmosphereHandler(Minecraft mc) {
        this.mc = mc;
    }

    public void setFontRenderer(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
    }

    private Font computeFont(String str, int n) {
        return FontResourceLoader.computeFont(str, n);
    }

    public void handleScaledResolution(ScaledResolution sr, float f, float f2) {
        this.handleScaledResolution(sr, f, f2, "Brutality", 1.0F);
    }
}
