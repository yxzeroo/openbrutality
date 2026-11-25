package org.brutality.module.impl.visual.targethud;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.brutality.utils.render.GetColorHelper;
import org.brutality.utils.render.GlStateGuard;
import org.brutality.utils.render.RenderHelper8;
import org.lwjgl.opengl.GL11;

public final class TargethudHandler {
    private final TargethudHandler.Entry entry = new TargethudHandler.Entry();

    public static TargethudHandler.Entry2 computeEntry2(FontRenderer fontRenderer, String str, String str2, String str3, String str4, boolean flag, boolean flag2) {
        float f = Math.max(10, fontRenderer.FONT_HEIGHT + 1);
        float f2 = 2.0F + f * 2.0F + 5.0F;
        float f3 = flag ? f2 - 2.0F : 0.0F;
        float f4 = fontRenderer.getStringWidth(str) + (flag2 ? 4 + fontRenderer.getStringWidth(str3) : 0);
        float f5 = fontRenderer.getStringWidth(str2) + (flag2 ? 4 + fontRenderer.getStringWidth(str4) : 0);
        return new TargethudHandler.Entry2(Math.max(f3 + 70.0F, f3 + 4.0F + Math.max(f4, f5)), f2, f3 + 2.0F, 2.0F + f, 2.0F + f * 2.0F);
    }

    private static void handleFloat(float f, float f2, float f3, float f4, int n) {
        GlStateManager.disableTexture2D();
        GlStateManager.color((n >> 16 & 0xFF) / 255.0F, ((0xFF | ~(n >> 8)) - ~(n >> 8)) / 255.0F, (n & 0xFF) / 255.0F, (n >>> 24) / 255.0F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(f, f4);
        GL11.glVertex2f(f3, f4);
        GL11.glVertex2f(f3, f2);
        GL11.glVertex2f(f, f2);
        GL11.glEnd();
    }

    public float computeFloat(Object obj, float f, float f2, long l, boolean flag) {
        return this.entry.computeFloat(obj, f, f2, l, flag);
    }

    public void reset() {
        this.entry.reset();
    }

    public void handleEntry2(
        TargethudHandler.Entry2 entry2,
        FontRenderer fontRenderer,
        float f,
        float f2,
        float f3,
        float f4,
        ResourceLocation location,
        String str,
        String str2,
        String str3,
        String str4,
        float f5,
        Color color,
        Color color2,
        Color color3,
        boolean flag,
        boolean flag2,
        boolean flag3
    ) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            if (OpenGlHelper.shadersSupported) {
                OpenGlHelper.glUseProgram(0);
            }

            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GlStateManager.pushMatrix();

            try {
                GlStateManager.scale(f3, f3, 1.0F);
                GlStateManager.translate(f, f2, -450.0F);
                RenderHelper8.process();
                handleFloat(0.0F, 0.0F, entry2.factor, entry2.factor2, new Color(0, 0, 0, Math.round(Math.max(0.0F, Math.min(1.0F, f4)) * 255.0F)).getRGB());
                if (flag2) {
                    GlStateManager.disableTexture2D();
                    GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
                    GL11.glLineWidth(1.5F);
                    GL11.glEnable(GL11.GL_LINE_SMOOTH);
                    GL11.glBegin(GL11.GL_LINE_LOOP);
                    GL11.glVertex2f(0.0F, 0.0F);
                    GL11.glVertex2f(entry2.factor, 0.0F);
                    GL11.glVertex2f(entry2.factor, entry2.factor2);
                    GL11.glVertex2f(0.0F, entry2.factor2);
                    GL11.glEnd();
                }

                GlStateManager.enableTexture2D();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                handleFontRenderer(fontRenderer, str, entry2.factor3, 2.0F, -1, flag3);
                handleFontRenderer(fontRenderer, str2, entry2.factor3, entry2.factor4, -1, flag3);
                if (flag) {
                    handleFontRenderer(fontRenderer, str3, entry2.factor - 2.0F - fontRenderer.getStringWidth(str3), 2.0F, color3.getRGB(), flag3);
                    handleFontRenderer(
                        fontRenderer, str4, entry2.factor - 2.0F - fontRenderer.getStringWidth(str4), entry2.factor4, GetColorHelper.computeColor2(color3, 0.8F).getRGB(), flag3
                    );
                }

                if (location != null && entry2.factor3 > 2.0F) {
                    RenderHelper8.process();
                    GlStateManager.enableTexture2D();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    Minecraft.getMinecraft().getTextureManager().bindTexture(location);
                    int n = (int)entry2.factor2 - 4;
                    Gui.drawScaledCustomSizeModalRect(2, 2, 8.0F, 8.0F, 8, 8, n, n, 64.0F, 64.0F);
                    Gui.drawScaledCustomSizeModalRect(2, 2, 40.0F, 8.0F, 8, 8, n, n, 64.0F, 64.0F);
                }

                RenderHelper8.process();
                handleFloat(entry2.factor3, entry2.factor5, entry2.factor - 2.0F, entry2.factor5 + 3.0F, GetColorHelper.computeColor2(color2, 0.2F).getRGB());
                handleFloat(entry2.factor3, entry2.factor5, entry2.factor3 + f5 * (entry2.factor - 2.0F - entry2.factor3), entry2.factor5 + 3.0F, color2.getRGB());
            } finally {
                GlStateManager.popMatrix();
            }
        }
    }

    private static void handleFontRenderer(FontRenderer fontRenderer, String str, float f, float f2, int n, boolean flag) {
        if (flag) {
            fontRenderer.drawStringWithShadow(str, f, f2, n);
        } else {
            fontRenderer.drawString(str, f, f2, n, false);
        }
    }

    public static final class Entry {
        private Object object;
        private float factor;
        private float factor2;
        private long timestamp;

        public void reset() {
            this.object = null;
            this.factor = this.factor2 = 0.0F;
            this.timestamp = 0L;
        }

        public float computeFloat(Object object, float f, float f2, long timestamp, boolean flag) {
            if (this.object != object) {
                this.object = object;
                this.factor = this.factor2 = f;
                this.timestamp = timestamp - 150L;
            }

            if (!flag) {
                this.factor = this.factor2 = f;
                this.timestamp = timestamp - 150L;
            } else if (timestamp - this.timestamp >= 150L || timestamp < this.timestamp) {
                this.factor = this.factor2;
                this.factor2 = f;
                if (this.factor != this.factor2) {
                    this.timestamp = timestamp;
                }
            }

            float f3 = Math.max(0.0F, Math.min(1.0F, (float)(timestamp - this.timestamp) / 150.0F));
            float f4 = this.factor + (this.factor2 - this.factor) * f3;
            return f2 > 0.0F ? Math.max(0.0F, Math.min(1.0F, f4 / f2)) : 0.0F;
        }
    }

    public static final class Entry2 {
        public final float factor;
        public final float factor2;
        public final float factor3;
        public final float factor4;
        public final float factor5;

        public Entry2(float factor, float f, float f2, float f3, float f4) {
            this.factor = factor;
            this.factor2 = f;
            this.factor3 = f2;
            this.factor4 = f3;
            this.factor5 = f4;
        }
    }
}
