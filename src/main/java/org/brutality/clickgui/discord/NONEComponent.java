package org.brutality.clickgui.discord;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import org.brutality.clickgui.newclick.NewClickGUI;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.lwjgl.input.Keyboard;

public class NONEComponent {
    public static final int count = 14;
    private final Module module;

    public boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6, NewClickGUI newClickGUI) {
        if (!matchesInt(n4, n5, n, n2, n3, 14)) {
            return false;
        }
        if (n6 == 0) {
            newClickGUI.setModule(this.module);
            return true;
        }
        if (n6 == 1) {
            ModuleManager.handleModule2(this.module);
            return true;
        }
        return false;
    }

    public static String computeString(int n) {
        if (n == 0 || n == 0) {
            return "NONE";
        }
        if (n < 0) {
            switch (n) {
                case -100:
                    return "LMB";
                case -99:
                    return "RMB";
                case -98:
                    return "MMB";
                case -97:
                    return "MB4";
                case -96:
                    return "MB5";
                default:
                    int n2 = n + 101;
                    return n2 > 0 ? "MB" + n2 : "MOUSE";
            }
        } else {
            String keyName = Keyboard.getKeyName(n);
            return keyName != null ? keyName : "UNKNOWN";
        }
    }

    public NONEComponent(Module module) {
        this.module = module;
    }

    public void handleInter18ptSemiboldTtfComponent(Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent, int n, int n2, int n3, boolean flag) {
        boolean flag2 = ModuleManager.matchesModule(this.module);
        int n4 = !flag && flag2 ? ColorUtils.color16.getRGB() : -1;
        String str = computeString(this.module.getKey());
        String str2;
        if (flag) {
            str2 = computeString();
        } else {
            str2 = "Bind";
        }

        int n5 = n2 + 7 - 4;
        String str3 = str.equals("NONE") ? "None" : str;
        int n6 = inter18ptSemiboldTtfComponent.computeInt3(str3) + 4;
        int n7 = flag ? n3 - 6 : n3 - n6 - 12;
        inter18ptSemiboldTtfComponent.handleString2(inter18ptSemiboldTtfComponent.computeString(str2, n7), n + 3, n5, n4);
        if (!flag) {
            int n8 = n + n3 - n6 - 3;
            int n9 = str.equals("NONE") ? ColorUtils.color16.getRGB() : ColorUtils.color15.getRGB();
            Gui.drawRect(n8 - 2, n2 + 2, n + n3 - 2, n2 + 14 - 2, new Color(0, 0, 0, 70).getRGB());
            inter18ptSemiboldTtfComponent.handleString2(str3, n8, n5, n9);
        }
    }

    private static String computeString() {
        int n = (int)(System.currentTimeMillis() / 400L % 4L);
        StringBuilder sb = new StringBuilder("Waiting for key");

        for (int i = 0; i < n; i++) {
            sb.append('.');
        }

        return sb.toString();
    }

    private static boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6) {
        return n >= n3 && n <= n3 + n5 && n2 >= n4 && n2 <= n4 + n6;
    }
}
