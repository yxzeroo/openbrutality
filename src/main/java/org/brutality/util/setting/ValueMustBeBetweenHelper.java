package org.brutality.util.setting;

import java.util.function.Function;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.core.GetDisplayNameHandler;
import org.brutality.core.SortSettingsHandler;
import org.brutality.util.ExecuteCommandHelper;

public class ValueMustBeBetweenHelper extends GetDisplayNameHandler<Integer> {
    private int count;
    private int count2;
    private boolean flag;

    @Override
    public EnumChatFormatting computeEnumChatFormatting() {
        return EnumChatFormatting.YELLOW;
    }

    public ValueMustBeBetweenHelper(String str, String str2, Integer integer, Function<Integer, Boolean> fn, String str3) {
        super(str, str2, integer, fn, str3);
    }

    public ValueMustBeBetweenHelper(SortSettingsHandler sortSettingsHandler, String str, String str2, Integer integer, Function<Integer, Boolean> fn, String str3) {
        super(sortSettingsHandler, str, str2, integer, fn, str3);
    }

    public ValueMustBeBetweenHelper(String str, String str2, Integer integer, int count, int n) {
        super(str, str2, integer);
        this.flag = true;
        this.count = count;
        this.count2 = n;
    }

    public Integer computeInteger(String str) {
        return this.computeObject(str);
    }

    public ValueMustBeBetweenHelper(SortSettingsHandler sortSettingsHandler, String str, String str2, Integer integer) {
        super(sortSettingsHandler, str, str2, integer);
    }

    public Integer computeObject(String str) {
        try {
            int n = Integer.parseInt(str);
            if (!this.flag || n >= this.count && n <= this.count2) {
                return n;
            }

            EnumChatFormatting RED = EnumChatFormatting.RED;
            int n2 = this.count2;
            EnumChatFormatting YELLOW = EnumChatFormatting.YELLOW;
            EnumChatFormatting RED2 = EnumChatFormatting.RED;
            int count = this.count;
            EnumChatFormatting YELLOW2 = EnumChatFormatting.YELLOW;
            ExecuteCommandHelper.handleString5(EnumChatFormatting.RED + "Value must be between " + YELLOW2 + count + RED2 + " and " + YELLOW + n2 + RED + "!");
        } catch (NumberFormatException e) {
            EnumChatFormatting RED3 = EnumChatFormatting.RED;
            EnumChatFormatting YELLOW3 = EnumChatFormatting.YELLOW;
            ExecuteCommandHelper.handleString5(EnumChatFormatting.RED + "Invalid number: " + YELLOW3 + str + RED3 + "!");
        }

        return null;
    }

    public ValueMustBeBetweenHelper(String str, String str2, Integer integer) {
        super(str, str2, integer);
    }
}
