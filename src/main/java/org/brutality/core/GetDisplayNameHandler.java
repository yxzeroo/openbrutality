package org.brutality.core;

import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.util.PlaySoundHelper;
import org.brutality.utils.MessageUtils;

public abstract class GetDisplayNameHandler<T> {
    private final Function<T, Boolean> function;
    private final String displayName;
    private final String text;
    private final T object;
    private final String key;
    public T value;
    public SortSettingsHandler sortSettingsHandler;

    public String getDisplayName() {
        return this.displayName;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T val) {
        this.handleObject((T)val, false);
    }

    public SortSettingsHandler getSortSettingsHandler() {
        return this.sortSettingsHandler;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && this.getClass() == obj.getClass()) {
            GetDisplayNameHandler getDisplayNameHandler = (GetDisplayNameHandler)obj;
            return Objects.equals(this.sortSettingsHandler, getDisplayNameHandler.sortSettingsHandler)
                && Objects.equals(this.displayName, getDisplayNameHandler.displayName)
                && Objects.equals(this.key, getDisplayNameHandler.key)
                && Objects.equals(this.value, getDisplayNameHandler.value)
                && Objects.equals(this.object, getDisplayNameHandler.object)
                && Objects.equals(this.function, getDisplayNameHandler.function)
                && Objects.equals(this.text, getDisplayNameHandler.text);
        }
        return false;
    }

    public String getKey() {
        return this.key;
    }

    public abstract EnumChatFormatting computeEnumChatFormatting();

    public void process2() {
        PlaySoundHelper.process10();
    }

    public void handleObject(T val, boolean flag) {
        this.handleObject((T)val, flag, true);
    }

    public void handleObject(T val, boolean flag, boolean flag2) {
        if (!this.isValid((T)val)) {
            this.process();
            this.activate();
        } else {
            this.value = (T)val;
            if (!flag) {
                EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
                EnumChatFormatting WHITE = EnumChatFormatting.WHITE;
                String displayName = this.getDisplayName();
                EnumChatFormatting GRAY2 = EnumChatFormatting.GRAY;
                EnumChatFormatting enumChatFormatting = this.computeEnumChatFormatting();
                EnumChatFormatting enumChatFormatting2 = GRAY2;
                String str = displayName;
                EnumChatFormatting enumChatFormatting3 = WHITE;
                MessageUtils.x(GRAY + "Set " + enumChatFormatting3 + str + enumChatFormatting2 + " to " + enumChatFormatting + val);
                if (flag2) {
                    this.process2();
                }
            }
        }
    }

    public GetDisplayNameHandler(SortSettingsHandler sortSettingsHandler, String str, String str2, T val) {
        this(sortSettingsHandler, str, str2, (T)val, null, null);
    }

    public abstract T computeObject(String serializedValue);

    public GetDisplayNameHandler(String str, String str2, T val) {
        this(null, str, str2, (T)val, null, null);
    }

    public void setSortSettingsHandler(SortSettingsHandler sortSettingsHandler) {
        this.sortSettingsHandler = sortSettingsHandler;
    }

    public GetDisplayNameHandler(String str, String str2, T val, Function<T, Boolean> fn, String str3) {
        this(null, str, str2, (T)val, fn, str3);
    }

    public void activate() {
        this.handleObject(this.object, false, false);
    }

    protected void process() {
        PlaySoundHelper.process12();
        if (this.text != null) {
            MessageUtils.x(EnumChatFormatting.RED + this.text);
        }
    }

    public void handleString(String str, boolean flag) {
        this.handleObject(this.computeObject(str), flag);
    }

    @Override
    public String toString() {
        return this.key + ":" + this.value;
    }

    public String computeString() {
        EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
        EnumChatFormatting WHITE = EnumChatFormatting.WHITE;
        String key = this.getKey();
        Object val = this.getValue();
        EnumChatFormatting enumChatFormatting = this.computeEnumChatFormatting();
        String str = key;
        EnumChatFormatting enumChatFormatting2 = WHITE;
        return GRAY + "» " + enumChatFormatting2 + str + ": " + enumChatFormatting + val;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.sortSettingsHandler, this.displayName, this.key, this.value, this.object, this.function, this.text);
    }

    protected boolean isValid(T val) {
        return val != null && (this.function == null || this.function.apply((T)val));
    }

    public String computeString2() {
        return this.key.replace("-", "");
    }

    public GetDisplayNameHandler(SortSettingsHandler sortSettingsHandler, String key, String displayName, T val, Function<T, Boolean> function, String text) {
        this.sortSettingsHandler = sortSettingsHandler;
        this.key = key;
        this.displayName = displayName;
        this.value = (T)val;
        this.object = (T)val;
        this.function = function;
        this.text = text;
    }

    public void handleString(String str) {
        this.handleString(str, false);
    }
}
