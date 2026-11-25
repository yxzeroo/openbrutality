package org.brutality.clickgui.discord;

import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.utils.render.FontResourceLoader;

public final class Inter18ptSemiboldTtfComponent {
    public final CustomFontRenderer drawStringWithShadowComponent = FontResourceLoader.computeDrawStringWithShadowComponent3(
        "inter.18pt-semibold.ttf", 22.0F
    );
    public final CustomFontRenderer drawStringWithShadowComponent2 = FontResourceLoader.computeDrawStringWithShadowComponent3(
        "inter.18pt-semibold.ttf", 17.0F
    );
    public final CustomFontRenderer drawStringWithShadowComponent3 = FontResourceLoader.computeDrawStringWithShadowComponent3(
        "inter.18pt-semibold.ttf", 13.0F
    );

    public int computeInt3(String str) {
        return this.drawStringWithShadowComponent3.getStringWidth(str);
    }

    public void handleString(String str, int n, int n2, int n3) {
        this.drawStringWithShadowComponent2.drawStringWithShadow(str, n, n2, n3);
    }

    public void handleString2(String str, int n, int n2, int n3) {
        this.drawStringWithShadowComponent3.drawStringWithShadow(str, n, n2, n3);
    }

    public String computeString(String str, int n) {
        if (str != null && !str.isEmpty()) {
            if (this.computeInt3(str) <= n) {
                return str;
            }
            var str2 = "...";
            int n2 = this.computeInt3((String)str2);
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < str.length(); i++) {
                sb.append(str.charAt(i));
                if (this.computeInt3(sb.toString()) + n2 > n) {
                    sb.setLength(Math.max(0, sb.length() - 1));
                    return sb + str2;
                }
            }

            return str;
        }
        return "";
    }

    public void handleString3(String str, int n, int n2, int n3) {
        this.drawStringWithShadowComponent.drawStringWithShadow(str, n, n2, n3);
    }

    public int computeInt(String str) {
        return this.drawStringWithShadowComponent.getStringWidth(str);
    }

    public int computeInt2(String str) {
        return this.drawStringWithShadowComponent2.getStringWidth(str);
    }
}
