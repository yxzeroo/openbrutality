package org.brutality.util.setting;

import java.util.function.Function;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.core.GetDisplayNameHandler;
import org.brutality.core.SortSettingsHandler;

public class SettingHelper5 extends GetDisplayNameHandler<String> {
    public SettingHelper5(SortSettingsHandler sortSettingsHandler, String str, String str2, String str3, Function<String, Boolean> fn, String str4) {
        super(sortSettingsHandler, str, str2, str3, fn, str4);
    }

    public String computeString(String str) {
        return this.computeObject(str);
    }

    public SettingHelper5(SortSettingsHandler sortSettingsHandler, String str, String str2, String str3) {
        super(sortSettingsHandler, str, str2, str3);
    }

    public String computeObject(String str) {
        return str == null ? "" : str;
    }

    @Override
    public EnumChatFormatting computeEnumChatFormatting() {
        return EnumChatFormatting.AQUA;
    }

    public SettingHelper5(String str, String str2, String str3) {
        super(str, str2, str3);
    }
}
