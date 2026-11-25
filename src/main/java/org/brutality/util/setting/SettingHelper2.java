package org.brutality.util.setting;

import java.util.function.Function;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.core.GetDisplayNameHandler;
import org.brutality.core.SortSettingsHandler;
import org.brutality.util.ExecuteCommandHelper;

public class SettingHelper2 extends GetDisplayNameHandler<Double> {
    public SettingHelper2(String str, String str2, Double val, Function<Double, Boolean> fn, String str3) {
        super(str, str2, val, fn, str3);
    }

    public SettingHelper2(SortSettingsHandler sortSettingsHandler, String str, String str2, Double val, Function<Double, Boolean> fn, String str3) {
        super(sortSettingsHandler, str, str2, val, fn, str3);
    }

    public SettingHelper2(String str, String str2, Double val) {
        super(str, str2, val);
    }

    public SettingHelper2(SortSettingsHandler sortSettingsHandler, String str, String str2, Double val) {
        super(sortSettingsHandler, str, str2, val);
    }

    @Override
    public EnumChatFormatting computeEnumChatFormatting() {
        return EnumChatFormatting.GOLD;
    }

    public Double computeDoubleType(String str) {
        return this.computeObject(str);
    }

    public Double computeObject(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            EnumChatFormatting RED = EnumChatFormatting.RED;
            EnumChatFormatting YELLOW = EnumChatFormatting.YELLOW;
            ExecuteCommandHelper.handleString5(EnumChatFormatting.RED + "Invalid number: " + YELLOW + str + RED + "!");
            return null;
        }
    }
}
