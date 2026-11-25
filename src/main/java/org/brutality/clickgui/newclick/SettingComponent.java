package org.brutality.clickgui.newclick;

import org.brutality.clickgui.discord.Inter18ptSemiboldTtfComponent;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ColorThemeSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.MultiBoolSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;

public abstract class SettingComponent {
    protected final Setting setting;
    protected int height;
    private long timestamp = -1L;
    private static SettingComponent instance;

    public Setting getSetting() {
        return this.setting;
    }

    protected void process4() {
        if (instance != null && instance != this) {
            instance.process();
        }

        instance = this;
    }

    public boolean checkState() {
        return false;
    }

    public void handleInt(int n, int n2, int n3, int n4, int n5) {
    }

    public static SettingComponent computeNewclickComponent7(Setting setting) {
        if (setting instanceof NumberSetting) {
            return new NewclickComponent8((NumberSetting)setting);
        }
        if (setting instanceof BooleanSetting) {
            return new NewclickComponent9((BooleanSetting)setting);
        }
        if (setting instanceof ModeSetting) {
            return new IsOpenComponent((ModeSetting)setting);
        }
        if (setting instanceof MultiBoolSetting) {
            return new NewclickComponent6((MultiBoolSetting)setting);
        }
        if (setting instanceof StringSetting) {
            return new GetClipboardStringComponent((StringSetting)setting);
        }
        if (setting instanceof ColorThemeSetting) {
            return new NewclickComponent((ColorThemeSetting)setting);
        }
        if (setting instanceof ColorSetting) {
            return new SaturationComponent((ColorSetting)setting);
        }
        return setting instanceof KeybindListSetting ? new NewclickComponent2((KeybindListSetting)setting) : null;
    }

    public void mouseReleased(int n, int n2) {
    }

    protected SettingComponent(Setting setting, int height) {
        this.setting = setting;
        this.height = height;
    }

    public abstract void handleInter18ptSemiboldTtfComponent(Inter18ptSemiboldTtfComponent fonts, int x, int y, int w, int mx, int my);

    public boolean checkState2() {
        return false;
    }

    public abstract boolean matchesInt(int x, int y, int w, int mx, int my, int button);

    protected void handleInter18ptSemiboldTtfComponent(Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent, int n, int n2, int n3, int n4, int n5, int n6) {
        int n7 = Math.max(1, n3);
        String name = this.setting.getName();
        if (inter18ptSemiboldTtfComponent.computeInt3(name) <= n7) {
            this.timestamp = -1L;
            inter18ptSemiboldTtfComponent.handleString2(name, n, n2, n6);
        } else {
            boolean flag = n4 >= n && n4 <= n + n7 && n5 >= n2 - 2 && n5 <= n2 + 10;
            if (!flag) {
                this.timestamp = -1L;
                inter18ptSemiboldTtfComponent.handleString2(inter18ptSemiboldTtfComponent.computeString(name, n7), n, n2, n6);
            } else {
                if (this.timestamp < 0L) {
                    this.timestamp = System.currentTimeMillis();
                }

                int n8 = inter18ptSemiboldTtfComponent.computeInt3(name) - n7;
                long l = System.currentTimeMillis() - this.timestamp;
                long l2 = Math.max(1L, n8 * 35L);
                long l3 = l % (l2 * 2L + 1000L);
                int n9 = l3 < 500L
                    ? 0
                    : (
                        l3 < l2 + 500L
                            ? (int)((l3 - 500L) * n8 / l2)
                            : (l3 < l2 + 1000L ? n8 : n8 - (int)((l3 - l2 - 1000L) * n8 / l2))
                    );
                NewClickGUI.handleInt2(n, n2 - 2, n7, 13);
                inter18ptSemiboldTtfComponent.handleString2(name, n - n9, n2, n6);
                NewClickGUI.process4();
            }
        }
    }

    public static void process3() {
        if (instance != null) {
            instance.process();
        }

        instance = null;
    }

    protected void process() {
    }

    public void keyTyped(char typedChar, int keyCode) {
    }

    public int getHeight() {
        return this.height;
    }
}
