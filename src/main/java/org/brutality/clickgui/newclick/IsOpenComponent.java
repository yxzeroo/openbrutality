package org.brutality.clickgui.newclick;

import java.awt.Color;
import java.util.List;
import net.minecraft.client.gui.Gui;
import org.brutality.clickgui.discord.ColorUtils;
import org.brutality.clickgui.discord.Inter18ptSemiboldTtfComponent;
import org.brutality.settings.impl.ModeSetting;

public class IsOpenComponent extends SettingComponent {
    private final ModeSetting modeSetting;
    private boolean open2;
    private float factor;
    private long timestamp;
    private static final long timestamp2 = -7131567279711453185L;

    @Override
    public int getHeight() {
        this.process2();
        return this.height + Math.round((this.modeSetting.computeList2().size() * 17 + 2) * this.factor);
    }

    @Override
    public void handleInter18ptSemiboldTtfComponent(Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent, int n, int n2, int n3, int n4, int n5) {
        Gui.drawRect(n + 1, n2 + 1, n + n3 - 1, n2 + 14, new Color(0, 0, 0, 50).getRGB());
        String str = this.modeSetting.getText().toUpperCase();
        int n6 = inter18ptSemiboldTtfComponent.computeInt3(str);
        this.handleInter18ptSemiboldTtfComponent(inter18ptSemiboldTtfComponent, n + 3, n2 + 4, n3 - n6 - 12, n4, n5, ColorUtils.color14.getRGB());
        inter18ptSemiboldTtfComponent.handleString2(str, n + n3 - n6 - 4, n2 + 4, ColorUtils.color15.getRGB());
        this.process2();
        if (this.factor > 0.01F) {
            List list = this.modeSetting.computeList2();
            int n7 = n2 + this.height;

            for (int i = 0; i < list.size(); i++) {
                String str2 = (String)list.get(i);
                boolean flag = this.modeSetting.matchesString2(str2);
                int n8 = n7 + Math.round(i * 17.0F * this.factor);
                int n9 = Math.max(1, Math.round(17.0F * this.factor));
                boolean flag2 = matchesInt2(n4, n5, n + 1, n8, n3 - 2, n9);
                if (flag) {
                    Gui.drawRect(n + 1, n8, n + n3 - 1, n8 + n9, new Color(55, 55, 55, Math.round(255.0F * this.factor)).getRGB());
                } else if (flag2) {
                    Gui.drawRect(n + 1, n8, n + n3 - 1, n8 + n9, new Color(45, 45, 45, Math.round(255.0F * this.factor)).getRGB());
                }

                int n10 = flag ? -1 : ColorUtils.color15.getRGB();
                int n11 = Math.round(255.0F * this.factor) << 24 | n10 & (int)timestamp2;
                inter18ptSemiboldTtfComponent.handleString2(inter18ptSemiboldTtfComponent.computeString(str2, n3 - 16), n + 12, n8 + Math.min(5, Math.max(1, n9 - 7)), n11);
            }
        }
    }

    public IsOpenComponent(ModeSetting modeSetting) {
        super(modeSetting, 15);
        this.modeSetting = modeSetting;
    }

    private void process2() {
        long now = System.nanoTime();
        float f = this.timestamp == 0L ? 0.016666668F : Math.max(0.004166667F, Math.min(0.05F, (float)(now - this.timestamp) / 1.0E9F));
        this.timestamp = now;
        float f2 = this.open2 ? 1.0F : 0.0F;
        this.factor = this.factor + (f2 - this.factor) * (1.0F - (float)Math.exp(-28.0F * f));
        if (Math.abs(f2 - this.factor) < 0.002F) {
            this.factor = f2;
        }
    }

    @Override
    public boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6) {
        this.process2();
        if (this.open2 && this.factor > 0.7F) {
            List list = this.modeSetting.computeList2();
            int n7 = n2 + this.height;

            for (int i = 0; i < list.size(); i++) {
                int n8 = n7 + Math.round(i * 17.0F * this.factor);
                int n9 = Math.max(1, Math.round(17.0F * this.factor));
                if ((n6 == 0 || n6 == 1) && matchesInt2(n4, n5, n + 1, n8, n3 - 2, n9)) {
                    this.modeSetting.handleString((String)list.get(i));
                    this.open2 = false;
                    return true;
                }
            }
        }

        if (matchesInt2(n4, n5, n + 1, n2 + 1, n3 - 2, 13) && (n6 == 0 || n6 == 1)) {
            this.open2 = !this.open2;
            return true;
        }
        return false;
    }

    public void close() {
        this.open2 = false;
    }

    public boolean isOpen() {
        return this.open2;
    }

    private static boolean matchesInt2(int n, int n2, int n3, int n4, int n5, int n6) {
        return n >= n3 && n <= n3 + n5 && n2 >= n4 && n2 <= n4 + n6;
    }
}
