package org.brutality.clickgui.newclick;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.Gui;
import org.brutality.Brutality;
import org.brutality.clickgui.discord.CLIENTComponent;
import org.brutality.clickgui.discord.DiscordComponent3;
import org.brutality.clickgui.discord.ColorUtils;
import org.brutality.clickgui.discord.Inter18ptSemiboldTtfComponent;
import org.brutality.clickgui.discord.NONEComponent;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.utils.hypixel.PhaseHelper;

public class NewclickComponent5 {
    private static final int count = 14;
    private static final int count2 = 5;
    private static final int count3 = 2;
    private static final int count4 = ColorUtils.color8.getRGB();
    private final Module module;
    private final CLIENTComponent clientComponent;
    private final NONEComponent noneComponent;
    private final List<SettingComponent> entries = new ArrayList<>();
    private final Map<Setting, SettingComponent> lookupMap = new IdentityHashMap<>();
    private final DiscordComponent3 discordComponent3 = new DiscordComponent3(0.0F, 0.2F);
    private final DiscordComponent3 discordComponent32 = new DiscordComponent3(0.0F, 0.2F);
    public int count5 = 14;
    private float factor = 14.0F;
    public boolean flag;

    private static void handleInt(int n, int n2, int n3, int n4) {
        Gui.drawRect(n, n2, n + n3, n2 + 1, count4);
        Gui.drawRect(n, n2 + n4 - 1, n + n3, n2 + n4, count4);
        Gui.drawRect(n, n2, n + 1, n2 + n4, count4);
        Gui.drawRect(n + n3 - 1, n2, n + n3, n2 + n4, count4);
    }

    private void process2() {
        var settings = Brutality.settingsManager.computeList2(this.module);
        this.entries.clear();

        for (Setting setting : settings) {
            if (setting.isVisible() && ThemeComponent.matchesSetting(setting, this.module)) {
                SettingComponent newclickComponent7 = this.lookupMap.get(setting);
                if (newclickComponent7 == null) {
                    newclickComponent7 = SettingComponent.computeNewclickComponent7(setting);
                    if (newclickComponent7 != null) {
                        this.lookupMap.put(setting, newclickComponent7);
                    }
                }

                if (newclickComponent7 != null) {
                    this.entries.add(newclickComponent7);
                }
            }
        }
    }

    public Module getModule() {
        return this.module;
    }

    private static int computeInt(int n, int n2, float f) {
        f = Math.max(0.0F, Math.min(1.0F, f));
        int n3 = n >> 16 & 0xFF;
        int n4 = (0xFF | ~(n >> 8)) - ~(n >> 8);
        int n5 = n & 0xFF;
        int n6 = (0xFF | ~(n2 >> 16)) - ~(n2 >> 16);
        int n7 = n2 >> 8 & 0xFF;
        int n8 = n2 & 0xFF;
        int n9 = (int)(n3 + (n6 - n3) * f);
        int n10 = (int)(n4 + (n7 - n4) * f);
        int n11 = (int)(n5 + (n8 - n5) * f);
        return 0xFF000000 | n9 << 16 | n10 << 8 | n11;
    }

    public void process() {
        if (this.flag) {
            this.process2();
        }

        int n = this.flag ? this.computeInt() : 14;
        float f = this.flag ? 0.22F : 0.42F;
        float f2 = n - this.factor;
        if (Math.abs(f2) < 0.5F) {
            this.factor = n;
        } else {
            float f3 = f2 * f;
            if (Math.abs(f3) < 1.0F) {
                f3 = f2 > 0.0F ? 1.0F : -1.0F;
            }

            this.factor += f3;
        }

        this.count5 = Math.max(14, Math.round(this.factor));
    }

    public NewclickComponent5(Module module, CLIENTComponent clientComponent) {
        this.module = module;
        this.clientComponent = clientComponent;
        this.noneComponent = new NONEComponent(module);
    }

    private void handleInter18ptSemiboldTtfComponent(Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent, int n, int n2) {
        String name = this.module.getName();
        String text = this.clientComponent.getNewClickGUI().getText();
        if (text != null && !text.trim().isEmpty()) {
            String str = text.toLowerCase();
            String str2 = name.toLowerCase();
            int n3 = str2.indexOf(str);
            if (n3 < 0) {
                inter18ptSemiboldTtfComponent.handleString2(name, n, n2, -1);
            } else {
                String str3 = name.substring(0, n3);
                String str4 = name.substring(n3, Math.min(name.length(), n3 + text.length()));
                String str5 = name.substring(Math.min(name.length(), n3 + text.length()));
                inter18ptSemiboldTtfComponent.handleString2(str3, n, n2, -1);
                int n4 = n + inter18ptSemiboldTtfComponent.computeInt3(str3);
                inter18ptSemiboldTtfComponent.handleString2(str4, n4, n2, ColorUtils.color10.getRGB());
                inter18ptSemiboldTtfComponent.handleString2(str5, n4 + inter18ptSemiboldTtfComponent.computeInt3(str4), n2, -1);
            }
        } else {
            inter18ptSemiboldTtfComponent.handleString2(name, n, n2, -1);
        }
    }

    public int getCount5() {
        return this.count5;
    }

    public boolean checkState() {
        for (SettingComponent newclickComponent7 : this.entries) {
            if (newclickComponent7.checkState()) {
                return true;
            }
        }

        return false;
    }

    public void handleInt(int n, int n2, int n3, int n4, int n5) {
        if (this.flag) {
            int n6 = n3 + 5;
            int n7 = n5 - 5 - 1;
            int n8 = n4 + 14 + 14;

            for (SettingComponent newclickComponent7 : this.entries) {
                if (newclickComponent7.getSetting().isVisible()) {
                    newclickComponent7.handleInt(n, n2, n6, n8, n7);
                    n8 += newclickComponent7.getHeight();
                }
            }
        }
    }

    public int computeInt() {
        if (!this.flag) {
            return 14;
        }
        this.process2();
        int n = 30;

        for (SettingComponent newclickComponent7 : this.entries) {
            if (newclickComponent7.getSetting().isVisible()) {
                n += newclickComponent7.getHeight();
            }
        }

        return n;
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        for (SettingComponent newclickComponent7 : this.entries) {
            newclickComponent7.mouseReleased(mouseX, mouseY);
        }
    }

    private static boolean matchesInt2(int n, int n2, int n3, int n4, int n5, int n6) {
        return n >= n3 && n <= n3 + n5 && n2 >= n4 && n2 <= n4 + n6;
    }

    public int computeInt(int n, int n2, int n3, int n4, int n5, Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent) {
        this.process();
        boolean flag = matchesInt2(n4, n5, n, n2, n3, 14);
        this.discordComponent3.setFactor2(this.module.checkState44() ? 1.0F : 0.0F);
        this.discordComponent32.setFactor2(flag ? 1.0F : 0.0F);
        float f = this.discordComponent3.computeFloat();
        float f2 = this.discordComponent32.computeFloat();
        int rgb = new Color(34, 34, 36, 255).getRGB();
        int rgb2 = new Color(29, 29, 29, 255).getRGB();
        int rgb3 = new Color(33, 33, 33, 255).getRGB();
        int rgb4 = ColorUtils.color8.getRGB();
        if (this.flag && this.count5 > 14) {
            handleInt(n, n2, n3, this.count5);
        }

        Gui.drawRect(n, n2, n + n3, n2 + 14, computeInt(rgb2, rgb4, f));
        if (f < 0.01F) {
            Gui.drawRect(n, n2, n + n3, n2 + 14, computeInt(rgb3, rgb2, f2));
        }

        if (this.count5 > 14) {
            Gui.drawRect(n + 1, n2 + 14, n + n3 - 1, n2 + this.count5 - 1, rgb);
        }

        this.handleInter18ptSemiboldTtfComponent(inter18ptSemiboldTtfComponent, n + 3, n2 + 4);
        if (PhaseHelper.matchesModule(this.module)) {
            inter18ptSemiboldTtfComponent.handleString2("✗", n + n3 - 19, n2 + 4, new Color(255, 70, 70).getRGB());
        }

        inter18ptSemiboldTtfComponent.handleString2(this.flag ? "-" : "+", n + n3 - 8, n2 + 4, ColorUtils.color15.getRGB());
        if (this.flag && this.count5 > 14) {
            NewClickGUI newClickGUI = this.clientComponent.getNewClickGUI();
            boolean flag2 = newClickGUI.matchesModule(this.module);
            int n6 = n + 5;
            int n7 = n3 - 5 - 1;
            int n8 = n2 + 14;
            this.noneComponent.handleInter18ptSemiboldTtfComponent(inter18ptSemiboldTtfComponent, n6, n8, n7, flag2);
            int n9 = n8 + 14;

            for (SettingComponent newclickComponent7 : this.entries) {
                if (newclickComponent7.getSetting().isVisible()) {
                    newclickComponent7.handleInter18ptSemiboldTtfComponent(inter18ptSemiboldTtfComponent, n6, n9, n7, n4, n5);
                    n9 += newclickComponent7.getHeight();
                }
            }
        }

        return this.count5;
    }

    public boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6) throws IOException {
        if (matchesInt2(n4, n5, n, n2, n3, 14)) {
            if (n6 == 0) {
                this.module.process28();
                return true;
            }

            if (n6 == 1) {
                this.flag = !this.flag;
                return true;
            }
        }

        if (!this.flag) {
            return false;
        }
        NewClickGUI newClickGUI = this.clientComponent.getNewClickGUI();
        int n7 = n + 5;
        int n8 = n3 - 5 - 1;
        int n9 = n2 + 14;
        if (this.noneComponent.matchesInt(n7, n9, n8, n4, n5, n6, newClickGUI)) {
            return true;
        }
        int n10 = n9 + 14;

        for (SettingComponent newclickComponent7 : this.entries) {
            if (newclickComponent7.getSetting().isVisible()) {
                if (newclickComponent7.matchesInt(n7, n10, n8, n4, n5, n6)) {
                    return true;
                }

                n10 += newclickComponent7.getHeight();
            }
        }

        return false;
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (this.flag) {
            for (SettingComponent newclickComponent7 : this.entries) {
                newclickComponent7.keyTyped(typedChar, keyCode);
            }
        }
    }
}
