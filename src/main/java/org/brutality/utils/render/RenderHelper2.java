package org.brutality.utils.render;

import java.awt.Color;
import java.awt.geom.Rectangle2D.Float;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.Module;
import org.brutality.utils.performance.InvalidateHelper;

public final class RenderHelper2 {
    private final FontRenderer fontRenderer;
    private final float factor;
    private final float factor2;
    private static final long timestamp = -7590812125363175424L;

    public float computeFloat(float f, float f2) {
        if (this.fontRenderer instanceof CustomFontRenderer) {
            Float floatValue = ((CustomFontRenderer)this.fontRenderer).computeEntry("Ag").floatValue;
            return this.computeFloat(f + (f2 - floatValue.height) / 2.0F - floatValue.y);
        }
        return this.computeFloat(f + (f2 - this.computeFloat()) / 2.0F);
    }

    public void handleString(String str, float f, float f2, float f3, int n) {
        this.handleString(str, f, this.computeFloat(f2, f3), n);
    }

    public RenderHelper2(Module module, float f, float f2) {
        Minecraft mc = Minecraft.getMinecraft();
        this.factor2 = Math.max(1, InvalidateHelper.computeScaledResolution(mc).getScaleFactor());
        this.fontRenderer = module.computeFontRenderer(f * f2);
        this.factor = this.fontRenderer == mc.fontRendererObj ? Math.max(1, Math.round(f2 * this.factor2)) / this.factor2 : 1.0F;
    }

    public float computeFloat(float f) {
        return Math.round(f * this.factor2) / this.factor2;
    }

    public void handleString(String str, float f, float f2, int n) {
        this.handleString(str, f, f2, n, false);
    }

    public int computeInt(String str) {
        return (int)Math.ceil(this.computeFloat(str));
    }

    public void handleString(String str, float f, float f2, Color color, Color color2) {
        float f3 = f;

        for (int i = 0; i < str.length(); i++) {
            float f4 = (float)i / Math.max(1, str.length() - 1);
            int n = (int)timestamp | (int)(color.getRed() + (color2.getRed() - color.getRed()) * f4) << 16;
            int n2 = (int)(color.getGreen() + (color2.getGreen() - color.getGreen()) * f4) << 8;
            n = n2 + (n & ~n2);
            n2 = (int)(color.getBlue() + (color2.getBlue() - color.getBlue()) * f4);
            int n3 = n2 + (n & ~n2);
            String str2 = str.substring(i, i + 1);
            this.handleString(str2, f3 + this.computeFloat(str.substring(0, i)), f2, n3, true);
        }
    }

    public float computeFloat(String str) {
        return this.fontRenderer instanceof CustomFontRenderer
            ? ((CustomFontRenderer)this.fontRenderer).computeEntry(str).factor
            : this.fontRenderer.getStringWidth(str) * this.factor;
    }

    public String computeString(String str, float f) {
        if (str == null) {
            return "--";
        }
        String str2 = str.replace('\n', ' ').replace('\r', ' ');
        if (this.computeInt(str2) <= f) {
            return str2;
        }
        while (!str2.isEmpty() && this.computeInt(str2 + "...") > f) {
            str2 = str2.substring(0, str2.length() - 1);
        }

        return str2 + "...";
    }

    public float computeFloat() {
        return this.fontRenderer.FONT_HEIGHT * this.factor;
    }

    public void handleString(String str, float f, float f2, int n, boolean flag) {
        float f3 = this.computeFloat(f);
        float f4 = this.computeFloat(f2);
        if (this.fontRenderer instanceof CustomFontRenderer) {
            ((CustomFontRenderer)this.fontRenderer).computeInt(str, f3, f4, n, flag);
        } else if (this.factor == 1.0F) {
            if (flag) {
                this.fontRenderer.drawStringWithShadow(str, f3, f4, n);
            } else {
                this.fontRenderer.drawString(str, f3, f4, n, false);
            }
        } else {
            GlStateManager.pushMatrix();

            try {
                GlStateManager.translate(f3, f4, 0.0F);
                GlStateManager.scale(this.factor, this.factor, 1.0F);
                if (flag) {
                    this.fontRenderer.drawStringWithShadow(str, 0.0F, 0.0F, n);
                } else {
                    this.fontRenderer.drawString(str, 0.0F, 0.0F, n, false);
                }
            } finally {
                GlStateManager.popMatrix();
            }
        }
    }
}
