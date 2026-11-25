package org.brutality.module.impl.visual.interfaces;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

public class GwapcheatsGgHandler {
    private final Minecraft mc;
    private FontRenderer fontRenderer;
    private long lastUpdateTime = System.currentTimeMillis();
    private int count = 0;
    private int count2 = 0;
    private long timestamp = 0L;
    private boolean flag = false;
    private int count3 = 0;
    private long timestamp2;
    private static final String text = "gwapcheats.gg";
    private static final EnumChatFormatting enumChatFormatting = EnumChatFormatting.DARK_AQUA;
    private static final EnumChatFormatting enumChatFormatting2 = EnumChatFormatting.AQUA;
    private static final EnumChatFormatting enumChatFormatting3 = EnumChatFormatting.WHITE;
    private static final int count4 = 150;
    private static final int count5 = 200;
    private static final int count6 = 5000;

    private void handleFontRenderer(FontRenderer fontRenderer, EnumChatFormatting enumChatFormatting, int n, int n2) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        fontRenderer.drawStringWithShadow(enumChatFormatting + "gwapcheats.gg", n, n2, 16777215);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public GwapcheatsGgHandler(Minecraft mc) {
        this.mc = mc;
    }

    private boolean matchesLong(long lastUpdateTime) {
        if (this.timestamp2 == 0L) {
            return false;
        }
        if (lastUpdateTime < this.timestamp2) {
            return true;
        }
        this.timestamp2 = 0L;
        this.lastUpdateTime = lastUpdateTime;
        return false;
    }

    public void handleScaledResolution(ScaledResolution sr, float f, float f2) {
        FontRenderer fontRenderer = this.fontRenderer != null ? this.fontRenderer : this.mc.fontRendererObj;
        long now = System.currentTimeMillis();
        if (this.matchesLong(now)) {
            this.handleFontRenderer(fontRenderer, enumChatFormatting2, (int)f, (int)f2);
        } else if (this.flag) {
            if (now - this.timestamp >= 200L) {
                this.count3++;
                if (this.count3 >= 4) {
                    this.flag = false;
                    this.count3 = 0;
                    this.count2 = 0;
                    this.count = 0;
                    this.lastUpdateTime = now;
                    this.timestamp2 = now + 5000L;
                } else {
                    this.timestamp = now;
                }
            }

            EnumChatFormatting enumChatFormatting4 = this.count3 % 2 == 0 ? enumChatFormatting2 : enumChatFormatting;
            this.handleFontRenderer(fontRenderer, enumChatFormatting4, (int)f, (int)f2);
        } else {
            if (now - this.lastUpdateTime >= 150L) {
                this.count++;
                if (this.count > "gwapcheats.gg".length()) {
                    this.count = 0;
                    this.count2++;
                    if (this.count2 >= 2) {
                        this.flag = true;
                        this.timestamp = now;
                        this.count3 = 0;
                        this.count2 = 0;
                        this.handleFontRenderer(fontRenderer, enumChatFormatting2, (int)f, (int)f2);
                        return;
                    }
                }

                this.lastUpdateTime = now;
            }

            EnumChatFormatting enumChatFormatting5 = this.count2 == 0 ? enumChatFormatting : enumChatFormatting2;
            EnumChatFormatting enumChatFormatting6 = this.count2 == 0 ? enumChatFormatting2 : enumChatFormatting;
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < "gwapcheats.gg".length(); i++) {
                if (i == this.count && this.count < "gwapcheats.gg".length()) {
                    sb.append(enumChatFormatting3).append("gwapcheats.gg".charAt(i));
                } else if (i < this.count) {
                    sb.append(enumChatFormatting5).append("gwapcheats.gg".charAt(i));
                } else {
                    sb.append(enumChatFormatting6).append("gwapcheats.gg".charAt(i));
                }
            }

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            fontRenderer.drawStringWithShadow(sb.toString(), f, f2, 16777215);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public void setFontRenderer(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
    }
}
