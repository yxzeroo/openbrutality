package org.brutality.util.setting;

import net.minecraft.util.EnumChatFormatting;
import org.brutality.core.SortSettingsHandler;
import org.brutality.util.ExecuteCommandHelper;
import org.brutality.util.PlaySoundHelper;

public class SettingHelper extends SettingHelper4 {
    public SettingHelper(String str, String str2, String str3) {
        super(str, str2, str3);
    }

    public SettingHelper(SortSettingsHandler sortSettingsHandler, String str, String str2, String str3) {
        super(sortSettingsHandler, str, str2, str3);
    }

    @Override
    public String computeObject(String str) {
        boolean flag = str.length() == 6;
        if (flag) {
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (!Character.isDigit(ch) && (ch < 'A' || ch > 'F')) {
                    flag = false;
                    break;
                }
            }
        }

        if (flag) {
            return str;
        }
        PlaySoundHelper.process12();
        EnumChatFormatting RED = EnumChatFormatting.RED;
        EnumChatFormatting YELLOW = EnumChatFormatting.YELLOW;
        ExecuteCommandHelper.handleString5(EnumChatFormatting.RED + "Invalid color: " + YELLOW + str + RED + "! Please use a 6 digit hex code (RRGGBB).");
        return null;
    }

    @Override
    public String computeString(String str) {
        return this.computeObject(str);
    }

    @Override
    public EnumChatFormatting computeEnumChatFormatting() {
        return EnumChatFormatting.AQUA;
    }
}
