package org.brutality.module.impl.visual.interfaces;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.GetColorHelper;
import org.lwjgl.opengl.GL11;

public class ThemeHandler {
    private final Minecraft mc;
    private long lastUpdateTime = System.currentTimeMillis();
    private int count = 0;
    private int count2 = 0;
    private long timestamp = 0L;
    private boolean flag = false;
    private int count3 = 0;
    private long timestamp2;
    private String text = "Normal";
    private BrutalityHelper brutalityHelper = null;
    private FontRenderer fontRenderer;
    private static final String text2 = "brutality.lol";
    private static final EnumChatFormatting enumChatFormatting = EnumChatFormatting.DARK_RED;
    private static final EnumChatFormatting enumChatFormatting2 = EnumChatFormatting.RED;
    private static final EnumChatFormatting enumChatFormatting3 = EnumChatFormatting.GOLD;
    private static final EnumChatFormatting enumChatFormatting4 = EnumChatFormatting.DARK_AQUA;
    private static final EnumChatFormatting enumChatFormatting5 = EnumChatFormatting.AQUA;
    private static final EnumChatFormatting enumChatFormatting6 = EnumChatFormatting.WHITE;
    private static final int count4 = 150;
    private static final int count5 = 200;
    private static final int count6 = 5000;

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

    public void handleString(String text) {
        if (!text.equalsIgnoreCase(this.text)) {
            this.text = text;
            this.count = 0;
            this.count2 = 0;
            this.flag = false;
            this.count3 = 0;
            this.timestamp2 = 0L;
            this.lastUpdateTime = System.currentTimeMillis();
        }
    }

    public void handleScaledResolution(ScaledResolution sr, float f, float f2) {
        FontRenderer fontRenderer = this.computeFontRenderer();
        long now = System.currentTimeMillis();
        if (this.text.equalsIgnoreCase("Theme") && this.brutalityHelper != null) {
            this.handleFontRenderer(fontRenderer, f, f2, now);
        } else {
            boolean flag = this.text.equalsIgnoreCase("Prime");
            EnumChatFormatting enumChatFormatting7 = flag ? enumChatFormatting4 : enumChatFormatting;
            EnumChatFormatting enumChatFormatting8 = flag ? enumChatFormatting5 : enumChatFormatting2;
            EnumChatFormatting enumChatFormatting9 = flag ? enumChatFormatting6 : enumChatFormatting3;
            if (this.matchesLong(now)) {
                this.handleFontRenderer(fontRenderer, enumChatFormatting8, (int)f, (int)f2);
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

                EnumChatFormatting enumChatFormatting10 = this.count3 % 2 == 0 ? enumChatFormatting8 : enumChatFormatting7;
                this.handleFontRenderer(fontRenderer, enumChatFormatting10, (int)f, (int)f2);
            } else {
                if (now - this.lastUpdateTime >= 150L) {
                    this.count++;
                    if (this.count > "brutality.lol".length()) {
                        this.count = 0;
                        this.count2++;
                        if (this.count2 >= 2) {
                            this.flag = true;
                            this.timestamp = now;
                            this.count3 = 0;
                            this.count2 = 0;
                            this.handleFontRenderer(fontRenderer, enumChatFormatting8, (int)f, (int)f2);
                            return;
                        }
                    }

                    this.lastUpdateTime = now;
                }

                EnumChatFormatting enumChatFormatting11 = this.count2 == 0 ? enumChatFormatting7 : enumChatFormatting8;
                EnumChatFormatting enumChatFormatting12 = this.count2 == 0 ? enumChatFormatting8 : enumChatFormatting7;
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < "brutality.lol".length(); i++) {
                    if (i == this.count && this.count < "brutality.lol".length()) {
                        sb.append(enumChatFormatting9).append("brutality.lol".charAt(i));
                    } else if (i < this.count) {
                        sb.append(enumChatFormatting11).append("brutality.lol".charAt(i));
                    } else {
                        sb.append(enumChatFormatting12).append("brutality.lol".charAt(i));
                    }
                }

                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                fontRenderer.drawStringWithShadow(sb.toString(), f, f2, -1);
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
    }

    private void handleFontRenderer(FontRenderer fontRenderer, float f, float f2, long lastUpdateTime) {
        Color color = this.brutalityHelper.getColor();
        Color color2 = this.brutalityHelper.getColor2();
        if (this.matchesLong(lastUpdateTime)) {
            this.handleFontRenderer(fontRenderer, "brutality.lol", f, f2, color, color);
        } else if (this.flag) {
            if (lastUpdateTime - this.timestamp >= 200L) {
                this.count3++;
                if (this.count3 >= 4) {
                    this.flag = false;
                    this.count3 = 0;
                    this.count2 = 0;
                    this.count = 0;
                    this.lastUpdateTime = lastUpdateTime;
                    this.timestamp2 = lastUpdateTime + 5000L;
                } else {
                    this.timestamp = lastUpdateTime;
                }
            }

            Color color3 = this.count3 % 2 == 0 ? color : color2;
            this.handleFontRenderer(fontRenderer, "brutality.lol", f, f2, color3, color3);
        } else {
            if (lastUpdateTime - this.lastUpdateTime >= 150L) {
                this.count++;
                if (this.count > "brutality.lol".length()) {
                    this.count = 0;
                    this.count2++;
                    if (this.count2 >= 2) {
                        this.flag = true;
                        this.timestamp = lastUpdateTime;
                        this.count3 = 0;
                        this.count2 = 0;
                        this.handleFontRenderer(fontRenderer, "brutality.lol", f, f2, color, color2);
                        return;
                    }
                }

                this.lastUpdateTime = lastUpdateTime;
            }

            Color color4 = this.count2 == 0 ? color : color2;
            Color color5 = this.count2 == 0 ? color2 : color;
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            float f3 = f;

            for (int i = 0; i < "brutality.lol".length(); i++) {
                String str = String.valueOf("brutality.lol".charAt(i));
                Color color6;
                if (i == this.count && this.count < "brutality.lol".length()) {
                    color6 = Color.WHITE;
                } else {
                    color6 = i < this.count ? color4 : color5;
                }

                fontRenderer.drawStringWithShadow(str, f3, f2, color6.getRGB() | 0xFF000000);
                f3 += fontRenderer.getStringWidth(str);
            }

            GlStateManager.popMatrix();
        }
    }

    private void handleFontRenderer(FontRenderer fontRenderer, String str, float f, float f2, Color color, Color color2) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        float f3 = f;
        int len = str.length();

        for (int i = 0; i < len; i++) {
            float f4 = len > 1 ? (float)i / (len - 1) : 0.0F;
            Color color3 = GetColorHelper.computeColor(color, color2, f4);
            String str2 = String.valueOf(str.charAt(i));
            fontRenderer.drawStringWithShadow(str2, f3, f2, color3.getRGB() | 0xFF000000);
            f3 += fontRenderer.getStringWidth(str2);
        }

        GlStateManager.popMatrix();
    }

    public void setFontRenderer(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
    }

    private void handleFontRenderer(FontRenderer fontRenderer, EnumChatFormatting enumChatFormatting, int n, int n2) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        fontRenderer.drawStringWithShadow(enumChatFormatting + "brutality.lol", n, n2, -1);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public ThemeHandler(Minecraft mc) {
        this.mc = mc;
    }

    public void setBrutalityHelper(BrutalityHelper brutalityHelper) {
        this.brutalityHelper = brutalityHelper;
    }

    public String getText() {
        return this.text;
    }

    private FontRenderer computeFontRenderer() {
        return this.fontRenderer != null ? this.fontRenderer : this.mc.fontRendererObj;
    }
}
