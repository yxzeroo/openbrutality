package org.brutality.util.inventory;

import net.minecraft.util.EnumChatFormatting;

public class GetSlotIndexHelper {
    public final String text;
    public final String text2;
    public final String text3;
    public final EnumChatFormatting enumChatFormatting;
    private final int slotIndex;
    private final String text4;

    public GetSlotIndexHelper(String text, String str, String str2, EnumChatFormatting enumChatFormatting) {
        this.text = text;
        this.text2 = str;
        this.text3 = str2;
        this.enumChatFormatting = enumChatFormatting;
        this.slotIndex = -1;
        this.text4 = null;
    }

    public GetSlotIndexHelper(int slotIndex, String str) {
        this.slotIndex = slotIndex;
        this.text4 = str;
        this.text = null;
        this.text2 = null;
        this.text3 = null;
        this.enumChatFormatting = null;
    }

    public String getText4() {
        return this.text4;
    }

    public int getSlotIndex() {
        return this.slotIndex;
    }
}
