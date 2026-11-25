package org.brutality.clickgui.newclick;

import java.awt.Color;
import java.util.List;
import net.minecraft.client.gui.Gui;
import org.brutality.clickgui.discord.DiscordComponent3;
import org.brutality.clickgui.discord.ColorUtils;
import org.brutality.clickgui.discord.Inter18ptSemiboldTtfComponent;
import org.brutality.module.impl.client.ClickGUI;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.OutlineThicknessHelper;

public class ThemePickerPopup {
    private static final int count = 3;
    private static final int count2 = 28;
    private static final int count3 = 14;
    private static final int count4 = 8;
    private static final int count5 = 12;
    private boolean open2;
    private final DiscordComponent3 discordComponent3 = new DiscordComponent3(0.0F, 0.22F);
    private final ThemePickerPopup.Entry entry = new ThemePickerPopup.Entry();
    private static final String text = "THEMES";

    public void close() {
        this.open2 = false;
    }

    private void handleInt(int n, int n2, int n3, int n4, BrutalityHelper brutalityHelper, boolean flag, boolean flag2) {
        Color color = brutalityHelper.getColor();
        Color color2 = brutalityHelper.getColor2();

        for (int i = 0; i < n3; i++) {
            float f = n3 <= 1 ? 0.0F : (float)i / (n3 - 1);
            Color color3 = ColorUtils.computeColor(color, color2, f);
            Gui.drawRect(n + i, n2, n + i + 1, n2 + n4, color3.getRGB());
        }

        if (flag) {
            OutlineThicknessHelper.handleFloat(n - 1, n2 - 1, n3 + 2, n4 + 2, 4.0F, 1.2F, ColorUtils.color20, ColorUtils.color8);
            this.handleInt(n, n2, n3, n4);
        } else if (flag2) {
            Gui.drawRect(n, n2, n + n3, n2 + n4, new Color(255, 255, 255, 25).getRGB());
        }
    }

    private void handleInt(int n, int n2, int n3, int n4, int n5, int n6, Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent, boolean flag) {
        OutlineThicknessHelper.handleFloat(n, n2, n3, n4, 8.0F, ColorUtils.color3);
        OutlineThicknessHelper.handleFloat(n, n2, n3, n4, 8.0F, 0.6F, ColorUtils.color20, ColorUtils.color17);
        if (flag) {
            inter18ptSemiboldTtfComponent.handleString(text, n + 12, n2 + 12, ColorUtils.color14.getRGB());
        }

        int n7 = n2 + (flag ? 28 : 12);
        int n8 = n4 - (flag ? 36 : 24);
        int n9 = (n3 - 24 - 16) / 3;
        int n10 = 50;
        List list = BrutalityHelper.computeList2();
        int n11 = (int)Math.ceil(list.size() / 3.0F);
        int n12 = n11 * n10;
        float f = Math.max(0, n12 - n8);
        this.entry.handleFloat(f);
        NewClickGUI.handleInt2(n + 12, n7, n3 - 24, n8);
        int n13 = n7 + (int)this.entry.getFactor();
        BrutalityHelper brutalityHelper = computeBrutalityHelper();

        for (int i = 0; i < list.size(); i++) {
            BrutalityHelper brutalityHelper2 = (BrutalityHelper)list.get(i);
            int n14 = i % 3;
            int n15 = i / 3;
            int n16 = n + 12 + n14 * (n9 + 8);
            int n17 = n13 + n15 * n10;
            boolean flag2 = matchesInt(n5, n6, n16, n17, n9, 42);
            boolean flag3 = brutalityHelper != null && brutalityHelper.getName().equals(brutalityHelper2.getName());
            this.handleInt(n16, n17, n9, 28, brutalityHelper2, flag3, flag2);
            inter18ptSemiboldTtfComponent.handleString2(brutalityHelper2.getName(), n16, n17 + 28 + 2, flag3 ? ColorUtils.color10.getRGB() : ColorUtils.color15.getRGB());
        }

        NewClickGUI.process4();
    }

    public boolean matchesInt2(int n, int n2, int n3, int n4, int n5) {
        if (this.open2 && n5 == 0) {
            int n6 = Math.min(340, n - 40);
            int n7 = Math.min(380, n2 - 40);
            int n8 = (n - n6) / 2;
            int n9 = (n2 - n7) / 2;
            if (!matchesInt(n3, n4, n8, n9, n6, n7)) {
                this.close();
                return true;
            }
            return false;
        }
        return false;
    }

    public void setOpen2(boolean open2) {
        this.open2 = open2;
    }

    private static BrutalityHelper computeBrutalityHelper() {
        ClickGUI instance = ClickGUI.getInstance();
        return instance != null ? instance.computeBrutalityHelper() : BrutalityHelper.getInstance();
    }

    public void r() {
        this.open2 = !this.open2;
    }

    public void handleInt(int n, int n2, int n3, int n4, int n5, int n6, Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent) {
        this.handleInt(n, n2, n3, n4, n5, n6, inter18ptSemiboldTtfComponent, false);
    }

    private static boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6) {
        return n >= n3 && n <= n3 + n5 && n2 >= n4 && n2 <= n4 + n6;
    }

    private boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6, boolean flag) {
        int n7 = n2 + (flag ? 28 : 12);
        int n8 = (n3 - 24 - 16) / 3;
        int n9 = 50;
        List list = BrutalityHelper.computeList2();

        for (int i = 0; i < list.size(); i++) {
            BrutalityHelper brutalityHelper = (BrutalityHelper)list.get(i);
            int n10 = i % 3;
            int n11 = i / 3;
            int n12 = n + 12 + n10 * (n8 + 8);
            int n13 = n7 + (int)this.entry.getFactor() + n11 * n9;
            if (matchesInt(n5, n6, n12, n13, n8, 42)) {
                handleBrutalityHelper(brutalityHelper);
                return true;
            }
        }

        return false;
    }

    public boolean matchesInt(int n, int n2, int n3, int n4, Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent) {
        this.discordComponent3.setFactor2(this.open2 ? 1.0F : 0.0F);
        float f = this.discordComponent3.computeFloat();
        if (f < 0.01F && !this.open2) {
            return false;
        }
        int n5 = (int)(110.0F * f);
        Gui.drawRect(0, 0, n, n2, new Color(0, 0, 0, n5).getRGB());
        int n6 = Math.min(340, n - 40);
        int n7 = Math.min(380, n2 - 40);
        int n8 = (n - n6) / 2;
        int n9 = (n2 - n7) / 2;
        this.handleInt(n8, n9, n6, n7, n3, n4, inter18ptSemiboldTtfComponent, true);
        return true;
    }

    private void handleInt(int n, int n2, int n3, int n4) {
        int n5 = n + n3 / 2 - 2;
        int n6 = n2 + n4 / 2;
        int rgb = ColorUtils.color8.getRGB();
        Gui.drawRect(n5, n6, n5 + 1, n6 + 1, rgb);
        Gui.drawRect(n5 + 1, n6 + 1, n5 + 2, n6 + 2, rgb);
        Gui.drawRect(n5 + 2, n6 + 2, n5 + 3, n6 + 3, rgb);
        Gui.drawRect(n5 + 3, n6 + 1, n5 + 4, n6 + 2, rgb);
        Gui.drawRect(n5 + 4, n6, n5 + 5, n6 + 1, rgb);
    }

    public void handleInt(int n, int n2) {
        List list = BrutalityHelper.computeList2();
        int n3 = (int)Math.ceil(list.size() / 3.0F);
        byte b = 50;
        float f = Math.max(0, n3 * b - n2);
        this.entry.handleInt(n, f);
    }

    public boolean matchesInt(int n, int n2, int n3, int n4, int n5) {
        if (this.open2 && n5 == 0) {
            int n6 = Math.min(340, n - 40);
            int n7 = Math.min(380, n2 - 40);
            int n8 = (n - n6) / 2;
            int n9 = (n2 - n7) / 2;
            return this.matchesInt(n8, n9, n6, n7, n3, n4, true);
        }
        return this.open2;
    }

    public boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        return n7 != 0 ? false : this.matchesInt(n, n2, n3, n4, n5, n6, false);
    }

    public void handleInt(int n, int n2, int n3) {
        if (this.open2 || n3 > 0) {
            byte b = 50;
            float f = Math.max(0, n3 * b - n2);
            this.entry.handleInt(n, f);
        }
    }

    public boolean isOpen() {
        return this.open2;
    }

    private static void handleBrutalityHelper(BrutalityHelper brutalityHelper) {
        ClickGUI instance = ClickGUI.getInstance();
        if (instance != null) {
            instance.handleBrutalityHelper(brutalityHelper);
        }
    }

    public static final class Entry {
        private float factor;
        private float factor2;

        private static float clamp(float f, float f2, float f3) {
            return Math.max(f2, Math.min(f3, f));
        }

        public void handleFloat(float f) {
            if (f <= 0.5F) {
                this.factor = 0.0F;
                this.factor2 = 0.0F;
            } else {
                this.factor2 = clamp(this.factor2, -f, 0.0F);
                this.factor = this.factor + (this.factor2 - this.factor) * 0.32F;
                if (Math.abs(this.factor2 - this.factor) < 0.4F) {
                    this.factor = this.factor2;
                }

                this.factor = clamp(this.factor, -f, 0.0F);
            }
        }

        public float getFactor() {
            return this.factor;
        }

        public void handleInt(int n, float f) {
            if (!(f <= 0.5F) && n != 0) {
                this.factor2 = clamp(this.factor2 + n, -f, 0.0F);
            }
        }
    }
}
