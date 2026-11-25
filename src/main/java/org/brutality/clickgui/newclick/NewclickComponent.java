package org.brutality.clickgui.newclick;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import org.brutality.clickgui.discord.ColorUtils;
import org.brutality.clickgui.discord.Inter18ptSemiboldTtfComponent;
import org.brutality.settings.impl.ColorThemeSetting;

public class NewclickComponent extends SettingComponent {
    private final ColorThemeSetting colorThemeSetting;
    private boolean dragging;

    private void handleInt(int n, int n2, int n3) {
        float f = Math.max(0.0F, Math.min(1.0F, (float)(n - n2) / Math.max(1, n3)));
        this.colorThemeSetting.setDefaultColor(Color.HSBtoRGB(f, 1.0F, 1.0F));
    }

    public NewclickComponent(ColorThemeSetting colorThemeSetting) {
        super(colorThemeSetting, 20);
        this.colorThemeSetting = colorThemeSetting;
    }

    @Override
    public void handleInter18ptSemiboldTtfComponent(Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent, int n, int n2, int n3, int n4, int n5) {
        this.handleInter18ptSemiboldTtfComponent(inter18ptSemiboldTtfComponent, n + 3, n2 + 3, n3 - 23, n4, n5, ColorUtils.color14.getRGB());
        Gui.drawRect(n + n3 - 16, n2 + 3, n + n3 - 4, n2 + 15, this.colorThemeSetting.getDefaultColor());
        int n6 = n + 1;
        int n7 = n2 + 12;
        int n8 = n3 - 20;

        for (int i = 0; i < n8; i++) {
            float f = (float)i / Math.max(1, n8 - 1);
            Gui.drawRect(n6 + i, n7, n6 + i + 1, n7 + 4, Color.HSBtoRGB(f, 1.0F, 1.0F));
        }
    }

    @Override
    public boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6) {
        if (n6 != 0) {
            return false;
        }
        if (matchesInt2(n4, n5, n + 1, n2 + 12, n3 - 20, 5)) {
            this.dragging = true;
            this.handleInt(n4, n + 1, n3 - 20);
            return true;
        }
        return false;
    }

    @Override
    public void handleInt(int n, int n2, int n3, int n4, int n5) {
        if (this.dragging) {
            this.handleInt(n, n3 + 1, n5 - 20);
        }
    }

    @Override
    public void mouseReleased(int n, int n2) {
        this.dragging = false;
    }

    private static boolean matchesInt2(int n, int n2, int n3, int n4, int n5, int n6) {
        return n >= n3 && n <= n3 + n5 && n2 >= n4 && n2 <= n4 + n6;
    }
}
