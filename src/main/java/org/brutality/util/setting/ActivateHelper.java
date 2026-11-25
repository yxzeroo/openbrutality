package org.brutality.util.setting;

import java.util.function.Function;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.core.GetDisplayNameHandler;
import org.brutality.core.SortSettingsHandler;
import org.brutality.util.ExecuteCommandHelper;

public class ActivateHelper extends GetDisplayNameHandler<Boolean> {
    @Override
    public String computeString() {
        EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
        EnumChatFormatting WHITE = EnumChatFormatting.WHITE;
        String key = this.getKey();
        String str = this.isEnabled() ? "enabled" : "disabled";
        EnumChatFormatting enumChatFormatting = this.computeEnumChatFormatting();
        String str2 = key;
        EnumChatFormatting enumChatFormatting2 = WHITE;
        return GRAY + "» " + enumChatFormatting2 + str2 + ": " + enumChatFormatting + str;
    }

    public boolean isEnabled() {
        return this.getValue();
    }

    public ActivateHelper(SortSettingsHandler sortSettingsHandler, String str, String str2, Boolean val) {
        super(sortSettingsHandler, str, str2, val);
    }

    public Boolean computeObject(String str) {
        if (!str.equalsIgnoreCase("true") && !str.equalsIgnoreCase("false")) {
            EnumChatFormatting RED = EnumChatFormatting.RED;
            EnumChatFormatting YELLOW = EnumChatFormatting.YELLOW;
            ExecuteCommandHelper.handleString5(EnumChatFormatting.RED + "Invalid boolean: " + YELLOW + str + RED + "!");
            return null;
        }
        return Boolean.parseBoolean(str);
    }

    public ActivateHelper(String str, String str2, Boolean val) {
        super(str, str2, val);
    }

    @Override
    public void activate() {
        this.value = !this.value;
        this.process2();
        var displayName = this.getDisplayName();
        EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
        String str = this.isEnabled() ? "enabled" : "disabled";
        EnumChatFormatting enumChatFormatting = this.computeEnumChatFormatting();
        EnumChatFormatting enumChatFormatting2 = GRAY;
        ExecuteCommandHelper.handleString7(displayName + enumChatFormatting2 + " - " + enumChatFormatting + str);
    }

    @Override
    public EnumChatFormatting computeEnumChatFormatting() {
        return this.isEnabled() ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
    }

    public Boolean computeBooleanType(String str) {
        return this.computeObject(str);
    }

    public ActivateHelper(SortSettingsHandler sortSettingsHandler, String str, String str2, Boolean val, Function<Boolean, Boolean> fn, String str3) {
        super(sortSettingsHandler, str, str2, val, fn, str3);
    }
}
