package org.brutality.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import org.brutality.gui.font.CustomFontRenderer;

public class GuiComponent extends GuiTextField {
    private final FontRenderer fontRenderer;

    public GuiComponent(int n, FontRenderer fontRenderer, int n2, int n3, int n4, int n5) {
        super(n, fontRenderer, n2, n3, n4, n5);
        this.fontRenderer = fontRenderer;
    }

    private String computeString(String str, int n, CustomFontRenderer drawStringWithShadowComponent) {
        if (str != null && !str.isEmpty()) {
            String str2 = "...";
            int textWidth = drawStringWithShadowComponent.getStringWidth(str2);
            if (drawStringWithShadowComponent.getStringWidth(str) <= n) {
                return str;
            }
            int n2 = 0;
            int len = str.length();
            String str3 = str;

            while (n2 < len) {
                int n3 = (n2 + len + 1) / 2;
                String str4 = str.substring(0, n3) + str2;
                int textWidth2 = drawStringWithShadowComponent.getStringWidth(str4);
                if (textWidth2 <= n) {
                    str3 = str4;
                    n2 = n3;
                } else {
                    len = n3 - 1;
                }
            }

            return drawStringWithShadowComponent.getStringWidth(str3) > n && textWidth <= n ? str2 : str3;
        }
        return str;
    }

    @Override
    public void drawTextBox() {
        if (this.getVisible()) {
            if (this.fontRenderer instanceof CustomFontRenderer) {
                String text = this.getText();
                int n = this.getWidth() - 10;
                if (!text.isEmpty()) {
                    int textWidth = this.fontRenderer.getStringWidth(text);
                    if (textWidth > n) {
                        text = this.computeString(text, n, (CustomFontRenderer)this.fontRenderer);
                    }
                }
            }

            super.drawTextBox();
        }
    }
}
