package org.brutality.module.impl.visual.interfaces;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

public class PrimecheatsGgHandler {
    private final Minecraft mc;
    private FontRenderer fontRenderer;
    private long lastUpdateTime = System.currentTimeMillis();
    private int count = 0;
    private boolean flag = false;
    private boolean flag2 = false;
    private boolean flag3 = false;
    private long timestamp = 0L;
    private long timestamp2 = 0L;
    private boolean flag4 = false;
    private int count2 = 0;
    private float factor = 10.0F;
    private float factor2 = 20.0F;
    private static final String text = "primecheats.gg";
    private static final EnumChatFormatting enumChatFormatting = EnumChatFormatting.DARK_AQUA;
    private static final EnumChatFormatting enumChatFormatting2 = EnumChatFormatting.AQUA;
    private static final EnumChatFormatting enumChatFormatting3 = EnumChatFormatting.WHITE;
    private static final int count3 = 150;
    private static final int count4 = 5000;
    private static final int count5 = 200;

    public PrimecheatsGgHandler(Minecraft mc) {
        this.mc = mc;
    }

    public void setFontRenderer(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
    }

    private void handleFontRenderer(FontRenderer fontRenderer, EnumChatFormatting enumChatFormatting, int n, int n2) {
        StringBuilder sb = new StringBuilder();
        sb.append(enumChatFormatting).append("primecheats.gg");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        fontRenderer.drawStringWithShadow(sb.toString(), n, n2, 16777215);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void handleScaledResolution(ScaledResolution sr, float factor, float f) {
        FontRenderer fontRenderer = this.fontRenderer != null ? this.fontRenderer : this.mc.fontRendererObj;
        long now = System.currentTimeMillis();
        this.factor = factor;
        this.factor2 = f;
        if (this.flag3) {
            if (now - this.timestamp2 >= 200L) {
                this.count2++;
                if (this.count2 >= 4) {
                    this.flag3 = false;
                    this.count2 = 0;
                    this.flag2 = true;
                    this.timestamp = now;
                    this.flag4 = true;
                } else {
                    this.timestamp2 = now;
                }
            }

            EnumChatFormatting enumChatFormatting4 = this.count2 % 2 == 0 ? enumChatFormatting2 : enumChatFormatting;
            this.handleFontRenderer(fontRenderer, enumChatFormatting4, (int)factor, (int)f);
        } else {
            if (this.flag2) {
                if (now - this.timestamp < 5000L && this.flag4) {
                    this.handleFontRenderer(fontRenderer, enumChatFormatting2, (int)factor, (int)f);
                    return;
                }

                this.flag2 = false;
                this.count = 0;
                this.flag = false;
                this.flag4 = false;
                this.lastUpdateTime = now;
            }

            if (now - this.lastUpdateTime >= 150L) {
                this.count++;
                if (this.count > "primecheats.gg".length()) {
                    if (this.flag) {
                        this.flag3 = true;
                        this.timestamp2 = now;
                        this.count2 = 0;
                        EnumChatFormatting enumChatFormatting5 = this.count2 % 2 == 0 ? enumChatFormatting2 : enumChatFormatting;
                        this.handleFontRenderer(fontRenderer, enumChatFormatting5, (int)factor, (int)f);
                        return;
                    }

                    this.count = 0;
                    this.flag = !this.flag;
                }

                this.lastUpdateTime = now;
            }

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < "primecheats.gg".length(); i++) {
                if (i == this.count && this.count < "primecheats.gg".length()) {
                    sb.append(enumChatFormatting3).append("primecheats.gg".charAt(i));
                } else if (i < this.count) {
                    if (this.flag) {
                        sb.append(enumChatFormatting2).append("primecheats.gg".charAt(i));
                    } else {
                        sb.append(enumChatFormatting).append("primecheats.gg".charAt(i));
                    }
                } else if (this.flag) {
                    sb.append(enumChatFormatting).append("primecheats.gg".charAt(i));
                } else {
                    sb.append(enumChatFormatting2).append("primecheats.gg".charAt(i));
                }
            }

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            fontRenderer.drawStringWithShadow(sb.toString(), factor, f, 16777215);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
}
