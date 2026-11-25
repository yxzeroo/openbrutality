package org.brutality.clickgui.newclick;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.gui.Gui;
import org.brutality.clickgui.discord.ColorUtils;
import org.brutality.clickgui.discord.Inter18ptSemiboldTtfComponent;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.KeybindListSetting;

public class NewclickComponent2 extends SettingComponent {
    private final KeybindListSetting keybindListSetting;
    private final List<SettingComponent> entries = new ArrayList<>();
    private boolean open;

    @Override
    public boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6) {
        if (matchesInt2(n4, n5, n + 1, n2 + 1, n3 - 2, 13) && n6 == 1) {
            this.open = !this.open;
            return true;
        }
        if (!this.open) {
            return false;
        }
        int n7 = n2 + this.height;

        for (SettingComponent newclickComponent7 : this.entries) {
            if (newclickComponent7.getSetting().isVisible()) {
                if (newclickComponent7.matchesInt(n + 6, n7, n3 - 6, n4, n5, n6)) {
                    return true;
                }

                n7 += newclickComponent7.getHeight();
            }
        }

        return false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.open) {
            for (SettingComponent newclickComponent7 : this.entries) {
                newclickComponent7.keyTyped(typedChar, keyCode);
            }
        }
    }

    public NewclickComponent2(KeybindListSetting keybindListSetting) {
        super(keybindListSetting, 15);
        this.keybindListSetting = keybindListSetting;
        this.process2();
    }

    private static boolean matchesInt2(int n, int n2, int n3, int n4, int n5, int n6) {
        return n >= n3 && n <= n3 + n5 && n2 >= n4 && n2 <= n4 + n6;
    }

    @Override
    public boolean checkState() {
        if (!this.open) {
            return false;
        }
        for (SettingComponent newclickComponent7 : this.entries) {
            if (newclickComponent7.checkState()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getHeight() {
        if (this.open) {
            this.process2();
        }

        if (!this.open) {
            return this.height;
        }
        int height = this.height;

        for (SettingComponent newclickComponent7 : this.entries) {
            if (newclickComponent7.getSetting().isVisible()) {
                height += newclickComponent7.getHeight();
            }
        }

        return height;
    }

    private void process2() {
        HashMap map = new HashMap();

        for (SettingComponent newclickComponent7 : this.entries) {
            map.put(newclickComponent7.getSetting(), newclickComponent7);
        }

        ArrayList list = new ArrayList();

        for (Setting setting : this.keybindListSetting.getEntries()) {
            if (setting.isVisible() && ThemeComponent.matchesSetting(setting, this.keybindListSetting.getParent())) {
                SettingComponent newclickComponent72 = (SettingComponent)map.get(setting);
                if (newclickComponent72 == null) {
                    newclickComponent72 = SettingComponent.computeNewclickComponent7(setting);
                }

                if (newclickComponent72 != null) {
                    list.add(newclickComponent72);
                }
            }
        }

        this.entries.clear();
        this.entries.addAll(list);
    }

    @Override
    public void mouseReleased(int n, int n2) {
        for (SettingComponent newclickComponent7 : this.entries) {
            newclickComponent7.mouseReleased(n, n2);
        }
    }

    @Override
    public void handleInter18ptSemiboldTtfComponent(Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent, int n, int n2, int n3, int n4, int n5) {
        if (this.open) {
            this.process2();
        }

        Gui.drawRect(n + 1, n2 + 1, n + n3 - 1, n2 + 14, new Color(0, 0, 0, 70).getRGB());
        Gui.drawRect(n + 1, n2 + 1, n + 3, n2 + 14, ColorUtils.color8.getRGB());
        this.handleInter18ptSemiboldTtfComponent(inter18ptSemiboldTtfComponent, n + 6, n2 + 4, n3 - 18, n4, n5, ColorUtils.color8.getRGB());
        inter18ptSemiboldTtfComponent.handleString2(this.open ? "-" : "+", n + n3 - 8, n2 + 4, ColorUtils.color8.getRGB());
        if (this.open) {
            int n6 = n2 + this.height;

            for (SettingComponent newclickComponent7 : this.entries) {
                if (newclickComponent7.getSetting().isVisible()) {
                    newclickComponent7.handleInter18ptSemiboldTtfComponent(inter18ptSemiboldTtfComponent, n + 6, n6, n3 - 6, n4, n5);
                    n6 += newclickComponent7.getHeight();
                }
            }
        }
    }

    @Override
    public void handleInt(int n, int n2, int n3, int n4, int n5) {
        if (this.open) {
            int n6 = n4 + this.height;

            for (SettingComponent newclickComponent7 : this.entries) {
                if (newclickComponent7.getSetting().isVisible()) {
                    newclickComponent7.handleInt(n, n2, n3 + 6, n6, n5 - 6);
                    n6 += newclickComponent7.getHeight();
                }
            }
        }
    }
}
