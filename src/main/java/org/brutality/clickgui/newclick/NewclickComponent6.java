package org.brutality.clickgui.newclick;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import org.brutality.clickgui.discord.ColorUtils;
import org.brutality.clickgui.discord.Inter18ptSemiboldTtfComponent;
import org.brutality.settings.impl.MultiBoolSetting;

public class NewclickComponent6 extends SettingComponent {
    private final MultiBoolSetting multiBoolSetting;
    private boolean open;
    private float factor;
    private long timestamp;
    private static final long timestamp2 = -1118090917547343873L;

    public NewclickComponent6(MultiBoolSetting multiBoolSetting) {
        super(multiBoolSetting, 15);
        this.multiBoolSetting = multiBoolSetting;
    }

    @Override
    public int getHeight() {
        this.process2();
        return this.height + Math.round((this.multiBoolSetting.getEntries().size() * 17 + 2) * this.factor);
    }

    private void process2() {
        long now = System.nanoTime();
        float f = this.timestamp == 0L ? 0.016666668F : Math.max(0.004166667F, Math.min(0.05F, (float)(now - this.timestamp) / 1.0E9F));
        this.timestamp = now;
        float f2 = this.open ? 1.0F : 0.0F;
        this.factor = this.factor + (f2 - this.factor) * (1.0F - (float)Math.exp(-28.0F * f));
        if (Math.abs(f2 - this.factor) < 0.002F) {
            this.factor = f2;
        }
    }

    @Override
    public boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6) {
        this.process2();
        if (this.open && this.factor > 0.7F) {
            int n7 = n2 + this.height;

            for (int i = 0; i < this.multiBoolSetting.getEntries().size(); i++) {
                int n8 = n7 + Math.round(i * 17.0F * this.factor);
                int n9 = Math.max(1, Math.round(17.0F * this.factor));
                if ((n6 == 0 || n6 == 1) && matchesInt2(n4, n5, n + 1, n8, n3 - 2, n9)) {
                    this.multiBoolSetting.handleString(this.multiBoolSetting.getEntries().get(i));
                    return true;
                }
            }
        }

        if ((n6 == 0 || n6 == 1) && matchesInt2(n4, n5, n + 1, n2 + 1, n3 - 2, 13)) {
            this.open = !this.open;
            return true;
        }
        return false;
    }

    @Override
    public void handleInter18ptSemiboldTtfComponent(Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent, int n, int n2, int n3, int n4, int n5) {
        this.process2();
        Gui.drawRect(n + 1, n2 + 1, n + n3 - 1, n2 + 14, new Color(0, 0, 0, 50).getRGB());
        String str = this.multiBoolSetting.computeString().toUpperCase();
        str = inter18ptSemiboldTtfComponent.computeString(str, Math.max(24, n3 / 2));
        int n6 = inter18ptSemiboldTtfComponent.computeInt3(str);
        this.handleInter18ptSemiboldTtfComponent(inter18ptSemiboldTtfComponent, n + 3, n2 + 4, n3 - n6 - 12, n4, n5, ColorUtils.color14.getRGB());
        inter18ptSemiboldTtfComponent.handleString2(str, n + n3 - n6 - 4, n2 + 4, ColorUtils.color15.getRGB());
        if (!(this.factor <= 0.01F)) {
            int n7 = n2 + this.height;

            for (int i = 0; i < this.multiBoolSetting.getEntries().size(); i++) {
                String str2 = this.multiBoolSetting.getEntries().get(i);
                boolean flag = this.multiBoolSetting.matchesString(str2);
                int n8 = n7 + Math.round(i * 17.0F * this.factor);
                int n9 = Math.max(1, Math.round(17.0F * this.factor));
                boolean flag2 = matchesInt2(n4, n5, n + 1, n8, n3 - 2, n9);
                if (flag || flag2) {
                    int n10 = flag ? 55 : 45;
                    Gui.drawRect(n + 1, n8, n + n3 - 1, n8 + n9, new Color(n10, n10, n10, Math.round(255.0F * this.factor)).getRGB());
                }

                int n11 = flag ? -1 : ColorUtils.color15.getRGB();
                int n12 = ((int)timestamp2 | ~n11) - ~n11 + (Math.round(255.0F * this.factor) << 24 & ~(((int)timestamp2 | ~n11) - ~n11));
                inter18ptSemiboldTtfComponent.handleString2((flag ? "> " : "  ") + inter18ptSemiboldTtfComponent.computeString(str2, n3 - 20), n + 8, n8 + Math.min(5, Math.max(1, n9 - 7)), n12);
            }
        }
    }

    private static boolean matchesInt2(int n, int n2, int n3, int n4, int n5, int n6) {
        return n >= n3 && n <= n3 + n5 && n2 >= n4 && n2 <= n4 + n6;
    }
}
