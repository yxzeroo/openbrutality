package org.brutality.util.setting;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.core.GetDisplayNameHandler;
import org.brutality.core.SortSettingsHandler;
import org.brutality.util.ExecuteCommandHelper;

public class GetAllowedValuesHelper<E extends Enum<E>> extends GetDisplayNameHandler<E> {
    private final Map<String, E> lookupMap;
    private final Class<E> classValue;
    private final E[] enumTypeArray;

    private String computeString3() {
        StringBuilder sb = new StringBuilder(EnumChatFormatting.GRAY + "[");

        for (int i = 0; i < this.enumTypeArray.length; i++) {
            if (i > 0) {
                sb.append(EnumChatFormatting.GRAY).append(", ");
            }

            sb.append(EnumChatFormatting.LIGHT_PURPLE).append(this.enumTypeArray[i].name());
        }

        return sb.append(EnumChatFormatting.GRAY).append(']').toString();
    }

    public int computeInt(E val) {
        for (int i = 0; i < this.enumTypeArray.length; i++) {
            if (this.enumTypeArray[i] == val) {
                return i;
            }
        }

        return -1;
    }

    public E computeObject(String str) {
        if (str == null) {
            return null;
        }
        Enum val = this.lookupMap.get(computeString(str));
        if (val == null) {
            EnumChatFormatting RED = EnumChatFormatting.RED;
            EnumChatFormatting WHITE = EnumChatFormatting.WHITE;
            EnumChatFormatting RED2 = EnumChatFormatting.RED;
            EnumChatFormatting WHITE2 = EnumChatFormatting.WHITE;
            String key = this.getKey();
            EnumChatFormatting RED3 = EnumChatFormatting.RED;
            String str2 = this.computeString3();
            EnumChatFormatting enumChatFormatting = RED3;
            String str3 = key;
            EnumChatFormatting enumChatFormatting2 = WHITE2;
            EnumChatFormatting enumChatFormatting3 = RED2;
            EnumChatFormatting enumChatFormatting4 = WHITE;
            ExecuteCommandHelper.handleString5(
                RED + "Invalid value: " + enumChatFormatting4 + str + "\n" + enumChatFormatting3 + "Possible values for " + enumChatFormatting2 + str3 + enumChatFormatting + ": " + str2
            );
        }

        return (E)val;
    }

    public List<E> getAllowedValues() {
        return Collections.unmodifiableList(Arrays.asList(this.enumTypeArray));
    }

    @Override
    public String toString() {
        return this.getKey() + ":" + this.getValue().name();
    }

    private Map<String, E> computeMap(E[] vals) {
        LinkedHashMap map = new LinkedHashMap();

        for (Enum val : vals) {
            map.put(computeString(val.name()), val);
            map.put(computeString(val.toString()), val);
        }

        return map;
    }

    public GetAllowedValuesHelper(String str, String str2, E val) {
        this(str, str2, val.getDeclaringClass(), (E)val);
    }

    public GetAllowedValuesHelper(SortSettingsHandler sortSettingsHandler, String str, String str2, Class<E> classValue, E val) {
        super(sortSettingsHandler, str, str2, (E)val);
        this.classValue = classValue;
        this.enumTypeArray = (E[])classValue.getEnumConstants();
        this.lookupMap = this.computeMap(this.enumTypeArray);
    }

    @Override
    public EnumChatFormatting computeEnumChatFormatting() {
        return EnumChatFormatting.LIGHT_PURPLE;
    }

    @Override
    public void activate() {
        int n = this.computeInt(this.getValue());
        int n2 = n < 0 ? 0 : (n + 1) % this.enumTypeArray.length;
        this.setValue(this.enumTypeArray[n2]);
    }

    public GetAllowedValuesHelper(String str, String str2, Class<E> classValue, E val) {
        super(str, str2, (E)val);
        this.classValue = classValue;
        this.enumTypeArray = (E[])classValue.getEnumConstants();
        this.lookupMap = this.computeMap(this.enumTypeArray);
    }

    private static String computeString(String str) {
        return str.toLowerCase(Locale.ROOT).replace("_", "").replace("-", "").replace(" ", "");
    }
}
