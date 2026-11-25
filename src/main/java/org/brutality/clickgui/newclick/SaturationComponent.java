package org.brutality.clickgui.newclick;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import org.brutality.clickgui.discord.ColorUtils;
import org.brutality.clickgui.discord.Inter18ptSemiboldTtfComponent;
import org.brutality.settings.impl.ColorSetting;

public class SaturationComponent extends SettingComponent {
    private static final int count = 14;
    private static final int count2 = 12;
    private static final int count3 = 48;
    private static final int count4 = new Color(40, 40, 40).getRGB();
    private static final int count5 = new Color(55, 55, 55).getRGB();
    private final ColorSetting colorSetting;
    private boolean height;
    private String text;

    public SaturationComponent(ColorSetting colorSetting) {
        super(colorSetting, 14);
        this.colorSetting = colorSetting;
    }

    @Override
    public void handleInt(int n, int n2, int n3, int n4, int n5) {
        if (this.text != null) {
            this.handleInt2(n, n3 + 1, n5 - 2);
        }
    }

    @Override
    public boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6) {
        if (n6 == 1 && matchesInt2(n4, n5, n, n2, n3, 14)) {
            this.height = !this.height;
            this.text = null;
            return true;
        }
        if (this.height && n6 == 0) {
            int n7 = n + 1;
            int n8 = n2 + 14 + 1;
            int n9 = n3 - 2;
            if (!matchesInt2(n4, n5, n7, n8, n9, 48)) {
                return false;
            }
            int n10 = n5 - n8;
            if (n10 < 12) {
                this.text = "hue";
            } else if (n10 < 24) {
                this.text = "saturation";
            } else if (n10 < 36) {
                this.text = "brightness";
            } else {
                this.text = "alpha";
            }

            this.handleInt2(n4, n7, n9);
            return true;
        }
        return false;
    }

    private static void handleInt(int n, int n2, int n3, float f) {
        int n4 = Math.max(n + 1, Math.min(n + (int)(f * n2), n + n2 - 2));
        Gui.drawRect(n4 - 1, n3 - 1, n4 + 2, n3 + 12 + 1, -16777216);
        Gui.drawRect(n4, n3, n4 + 1, n3 + 12, -1);
    }

    private void handleInt2(int n, int n2, int n3) {
        float f = Math.max(0.0F, Math.min(1.0F, (float)(n - n2) / Math.max(1, n3)));
        String text = this.text;
        switch (text) {
            case "hue":
                this.colorSetting.handleFloat3(f);
                break;
            case "saturation":
                this.colorSetting.handleFloat4(f);
                break;
            case "brightness":
                this.colorSetting.handleFloat2(f);
                break;
            case "alpha":
                this.colorSetting.handleFloat(f);
        }
    }

    private static boolean matchesInt2(int n, int n2, int n3, int n4, int n5, int n6) {
        return n >= n3 && n <= n3 + n5 && n2 >= n4 && n2 <= n4 + n6;
    }

    @Override
    public int getHeight() {
        return this.height ? 64 : 14;
    }

    @Override
    public void handleInter18ptSemiboldTtfComponent(Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent, int n, int n2, int n3, int n4, int n5) {
        this.handleInter18ptSemiboldTtfComponent(inter18ptSemiboldTtfComponent, n + 3, n2 + 4, n3 - 15, n4, n5, ColorUtils.color14.getRGB());
        inter18ptSemiboldTtfComponent.handleString2(this.height ? "-" : "+", n + n3 - 8, n2 + 4, ColorUtils.color15.getRGB());
        if (this.height) {
            int n6 = n + 1;
            int n7 = n2 + 14 + 1;
            int n8 = n3 - 2;
            this.handleInt(n6, n7, n8);
        }
    }

    private void handleInt(int n, int n2, int n3) {
        int n4 = n2;
        int n5 = n2 + 12;
        int n6 = n2 + 24;
        int n7 = n2 + 36;

        for (int i = 0; i < n3; i++) {
            float f = (float)i / Math.max(1, n3 - 1);
            Gui.drawRect(n + i, n4, n + i + 1, n4 + 12, Color.HSBtoRGB(f, 1.0F, 1.0F));
            Gui.drawRect(n + i, n5, n + i + 1, n5 + 12, Color.HSBtoRGB(this.colorSetting.getFactor(), f, this.colorSetting.getFactor4()));
            Gui.drawRect(n + i, n6, n + i + 1, n6 + 12, Color.HSBtoRGB(this.colorSetting.getFactor(), this.colorSetting.getSaturation(), f));
        }

        Color hsbColor = Color.getHSBColor(this.colorSetting.getFactor(), this.colorSetting.getSaturation(), this.colorSetting.getFactor4());

        for (int j = 0; j < n3; j++) {
            float f2 = (float)j / Math.max(1, n3 - 1);
            if (j / 4 % 2 == 0) {
                Gui.drawRect(n + j, n7, n + j + 1, n7 + 12, count4);
            } else {
                Gui.drawRect(n + j, n7, n + j + 1, n7 + 12, count5);
            }

            int n8 = (int)(f2 * 255.0F);
            Gui.drawRect(n + j, n7, n + j + 1, n7 + 12, new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), n8).getRGB());
        }

        handleInt(n, n3, n4, this.colorSetting.getFactor());
        handleInt(n, n3, n5, this.colorSetting.getSaturation());
        handleInt(n, n3, n6, this.colorSetting.getFactor4());
        handleInt(n, n3, n7, this.colorSetting.getAlpha());
    }

    @Override
    public void mouseReleased(int n, int n2) {
        this.text = null;
    }
}
