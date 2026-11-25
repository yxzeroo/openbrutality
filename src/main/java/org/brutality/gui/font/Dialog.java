package org.brutality.gui.font;

import java.awt.Font;
import java.awt.geom.Rectangle2D.Float;
import net.minecraft.client.gui.FontRenderer;

public final class Dialog extends CustomFontRenderer {
    private final FontRenderer fontRenderer;

    @Override
    public int getStringWidth(String str) {
        return this.fontRenderer.getStringWidth(str);
    }

    @Override
    public int getHeight() {
        return this.fontRenderer.FONT_HEIGHT;
    }

    public boolean matchesFontRenderer(FontRenderer fontRenderer) {
        return this.fontRenderer == fontRenderer;
    }

    @Override
    public CustomFontRenderer.Entry computeEntry(String str) {
        int textWidth = this.getStringWidth(str);
        return new CustomFontRenderer.Entry(textWidth, new Float(0.0F, 0.0F, textWidth, this.fontRenderer.FONT_HEIGHT));
    }

    @Override
    public int drawString(String str, float f, float f2, int n, boolean flag) {
        return this.fontRenderer.drawString(str, f, f2, n, flag);
    }

    @Override
    public float getFONT_HEIGHT() {
        return this.fontRenderer.FONT_HEIGHT;
    }

    @Override
    public int drawStringWithShadow(String str, float f, float f2, int n) {
        return this.fontRenderer.drawStringWithShadow(str, f, f2, n);
    }

    @Override
    public int computeInt3(String str, float f, float f2, int n) {
        return this.fontRenderer.drawString(str, f, f2, n, false);
    }

    public Dialog(FontRenderer fontRenderer) {
        super(new Font("Dialog", 0, 18));
        this.fontRenderer = fontRenderer;
        this.FONT_HEIGHT = fontRenderer.FONT_HEIGHT;
    }

    @Override
    public int getCharWidth(char ch) {
        return this.fontRenderer.getCharWidth(ch);
    }
}
