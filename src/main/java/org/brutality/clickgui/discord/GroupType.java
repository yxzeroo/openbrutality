package org.brutality.clickgui.discord;

public enum GroupType {
    GROUP,
    COMBOBOX,
    SELECTBOX,
    CHECKBOX,
    SLIDER,
    TEXTBOX,
    COLOR_PICKER;

    private static GroupType[] computeGroupTypeArray() {
        return new GroupType[]{GROUP, COMBOBOX, SELECTBOX, CHECKBOX, SLIDER, TEXTBOX, COLOR_PICKER};
    }
}
