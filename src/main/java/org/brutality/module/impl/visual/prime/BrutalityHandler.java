package org.brutality.module.impl.visual.prime;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.render.FontResourceLoader;
import org.brutality.utils.render.GetColorHelper;
import org.brutality.utils.render.OutlineThicknessHelper;
import org.lwjgl.opengl.GL11;

public final class BrutalityHandler {
    private static final long timestamp = 220L;
    private static final long timestamp2 = 250L;
    private static final int count = 6;
    private static final float factor = 28.0F;
    private static final float factor2 = 5.0F;
    private static final float factor3 = 5.0F;
    private static final float factor4 = 2.0F;
    private static final float factor5 = 4.0F;
    private static final Color color = new Color(80, 200, 100);
    private static final Color color2 = new Color(210, 60, 60);
    private static final Color color3 = new Color(0, 0, 0);
    private static final List<BrutalityHandler.Entry> entries = new ArrayList<>();

    public static void clear() {
        entries.clear();
    }

    public static void handleMinecraft(Minecraft mc, int n, long l, FontRenderer fontRenderer) {
        if (mc.thePlayer != null) {
            ScaledResolution sr = InvalidateHelper.computeScaledResolution(mc);
            long now = System.currentTimeMillis();
            Iterator it = entries.iterator();

            while (it.hasNext()) {
                if (now - ((BrutalityHandler.Entry)it.next()).timestamp > l + 250L) {
                    it.remove();
                }
            }

            CustomFontRenderer drawStringWithShadowComponent = FontResourceLoader.computeDrawStringWithShadowComponent2("Brutality", 10.0F);
            CustomFontRenderer drawStringWithShadowComponent2 = FontResourceLoader.computeDrawStringWithShadowComponent2("Brutality", 9.0F);
            float f = 60.0F;
            float f2 = 50.0F;

            for (BrutalityHandler.Entry entry : entries) {
                String str = entry.flag ? "enabled" : "disabled";
                if (drawStringWithShadowComponent != null) {
                    f = Math.max(f, (float)drawStringWithShadowComponent.getStringWidth(entry.text));
                }

                if (drawStringWithShadowComponent2 != null) {
                    f2 = Math.max(f2, (float)drawStringWithShadowComponent2.getStringWidth(str));
                }
            }

            float f3 = Math.max(f, f2) + 2.0F + 14.0F + 14.0F;
            boolean flag = n == 0 || n == 2;
            boolean flag2 = n == 2 || n == 3;
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            int n2 = 0;

            for (BrutalityHandler.Entry entry2 : entries) {
                long l2 = now - entry2.timestamp;
                float f4 = Math.min(1.0F, (float)l2 / 220.0F);
                float f5 = f4 * (2.0F - f4);
                float f6;
                if (l2 > l) {
                    f6 = 1.0F - Math.min(1.0F, (float)(l2 - l) / 250.0F);
                } else {
                    f6 = 1.0F;
                }

                Color color4 = entry2.flag ? color : color2;
                Color color5 = GetColorHelper.computeColor(color3, color4, 0.65F);
                Color color6 = GetColorHelper.computeColor(color5, 0.7F * f6);
                float f7 = (1.0F - f5) * (f3 + 10.0F);
                float f8;
                if (flag) {
                    f8 = sr.getScaledWidth() - f3 - 5.0F + f7;
                } else {
                    f8 = 5.0F - f7;
                }

                float f9;
                if (flag2) {
                    f9 = sr.getScaledHeight() - 5.0F - 28.0F - n2 * 33.0F;
                } else {
                    f9 = 5.0F + n2 * 33.0F;
                }

                OutlineThicknessHelper.handleFloat(f8, f9, f3, 28.0F, 4.0F, color6);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                Color color7 = GetColorHelper.computeColor(color4, f6);
                OutlineThicknessHelper.handleFloat(f8, f9, 2.0F, 28.0F, 1.0F, color7);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                int rgb = new Color(255, 255, 255, (int)(f6 * 255.0F)).getRGB();
                int rgb2 = new Color(200, 200, 200, (int)(f6 * 255.0F)).getRGB();
                float f10 = f8 + 2.0F + 7.0F;
                String str2 = entry2.flag ? "enabled" : "disabled";
                if (drawStringWithShadowComponent != null && drawStringWithShadowComponent2 != null) {
                    float f11 = f9 + 4.0F;
                    float f12 = f9 + 4.0F + drawStringWithShadowComponent.getHeight() + 2.0F;
                    drawStringWithShadowComponent.drawString(entry2.text, (int)f10, (int)f11, rgb, false);
                    drawStringWithShadowComponent2.drawString(str2, (int)f10, (int)f12, rgb2, false);
                } else {
                    FontRenderer fontRenderer2 = mc.fontRendererObj;
                    float f13 = f9 + 14.0F - fontRenderer2.FONT_HEIGHT / 2.0F;
                    fontRenderer2.drawStringWithShadow(entry2.text + " " + (entry2.flag ? "§aenabled" : "§cdisabled"), f10, f13, rgb);
                }

                n2++;
            }

            GlStateManager.popMatrix();
        }
    }

    private BrutalityHandler() {
    }

    public static void handleString(String str, boolean flag) {
        BrutalityHandler.Entry entry = new BrutalityHandler.Entry(str, flag);
        entries.add(0, entry);

        while (entries.size() > 6) {
            entries.remove(entries.size() - 1);
        }
    }

    public static class Entry {
        public final String text;
        public final boolean flag;
        public final long timestamp;

        public Entry(String text, boolean flag) {
            this.text = text;
            this.flag = flag;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
