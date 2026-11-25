package org.brutality.util.module;

import net.minecraft.util.EnumChatFormatting;

public enum ClientOnlyType {
    CLIENT_ONLY(EnumChatFormatting.GREEN),
    MAYBE_DANGEROUS(EnumChatFormatting.YELLOW),
    DANGEROUS(EnumChatFormatting.RED);

    private EnumChatFormatting color;

    private static ClientOnlyType[] computeClientOnlyTypeArray() {
        return new ClientOnlyType[]{CLIENT_ONLY, MAYBE_DANGEROUS, DANGEROUS};
    }

    private ClientOnlyType(EnumChatFormatting color) {
        this.color = color;
    }

    public String getColorCode() {
        return String.valueOf(new StringBuilder().append(this.color));
    }
}
