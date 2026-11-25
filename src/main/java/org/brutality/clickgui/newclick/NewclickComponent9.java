package org.brutality.clickgui.newclick;

import org.brutality.clickgui.discord.DiscordComponent3;
import org.brutality.clickgui.discord.ColorUtils;
import org.brutality.clickgui.discord.Inter18ptSemiboldTtfComponent;
import org.brutality.settings.impl.BooleanSetting;

public class NewclickComponent9 extends SettingComponent {
    private final BooleanSetting booleanSetting;
    private final DiscordComponent3 discordComponent3;

    private static boolean matchesInt2(int n, int n2, int n3, int n4, int n5, int n6) {
        return n >= n3 && n <= n3 + n5 && n2 >= n4 && n2 <= n4 + n6;
    }

    public NewclickComponent9(BooleanSetting booleanSetting) {
        super(booleanSetting, 15);
        this.booleanSetting = booleanSetting;
        this.discordComponent3 = new DiscordComponent3(booleanSetting.isEnabled() ? 1.0F : 0.0F, 0.28F);
    }

    @Override
    public void handleInter18ptSemiboldTtfComponent(Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent, int n, int n2, int n3, int n4, int n5) {
        boolean enabled = this.booleanSetting.isEnabled();
        this.discordComponent3.setFactor2(enabled ? 1.0F : 0.0F);
        this.discordComponent3.computeFloat();
        int n6 = NewclickComponent3.computeInt(n, n3);
        int n7 = n2 + 2;
        NewclickComponent3.handleInt(n6, n7, enabled);
        this.handleInter18ptSemiboldTtfComponent(inter18ptSemiboldTtfComponent, n + 3, n2 + 4, n6 - (n + 3) - 4, n4, n5, ColorUtils.color14.getRGB());
    }

    @Override
    public boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6) {
        if (n6 != 0) {
            return false;
        }
        int n7 = NewclickComponent3.computeInt(n, n3);
        int n8 = n2 + 2;
        if (!NewclickComponent3.matchesInt(n4, n5, n7, n8) && !matchesInt2(n4, n5, n, n2, n3, this.height)) {
            return false;
        }
        this.booleanSetting.r();
        return true;
    }
}
