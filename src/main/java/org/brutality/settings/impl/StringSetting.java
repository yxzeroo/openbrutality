package org.brutality.settings.impl;

import org.brutality.module.Module;
import org.brutality.service.itemsearch.IsVisibleApi;
import org.brutality.settings.Setting;

public class StringSetting extends Setting {
    private String value;
    private final String text;

    public StringSetting(String name, Module parent, String defaultValue) {
        super(name, parent);
        this.text = computeString(defaultValue);
        this.value = this.text;
    }

    public StringSetting(String name, String defaultValue) {
        super(name, null);
        this.text = computeString(defaultValue);
        this.value = this.text;
    }

    public String getValue() {
        return this.value;
    }

    public String getText() {
        return this.text;
    }

    public void setValue(String value) {
        this.value = computeString(value);
    }

    public StringSetting computeStringSetting(IsVisibleApi visibility) {
        this.setConditionalVisibility(visibility);
        return this;
    }

    @Override
    public String toString() {
        return this.value;
    }

    private static String computeString(String str) {
        if (str == null) {
            return null;
        }
        char[] chars = str.toCharArray();

        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(chars[i + 1]) > -1) {
                chars[i] = 167;
            }
        }

        return new String(chars);
    }

    public String getValue2() {
        return this.value;
    }

    public String computeString() {
        return stripColorCodes(this.value);
    }

    private static String stripColorCodes(String str) {
        return str == null ? null : str.replaceAll("§[0-9a-fk-or]", "");
    }
}
