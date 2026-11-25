package org.brutality.util.module;

import net.minecraft.util.EnumChatFormatting;

public enum AutoUseType {
    AUTO_USE(String.valueOf(new StringBuilder().append(EnumChatFormatting.GREEN).append("Auto Use"))),
    RENDER(String.valueOf(new StringBuilder().append(EnumChatFormatting.YELLOW).append("Render"))),
    MISC(String.valueOf(new StringBuilder().append(EnumChatFormatting.LIGHT_PURPLE).append("Misc"))),
    COMBAT(String.valueOf(new StringBuilder().append(EnumChatFormatting.RED).append("Combat"))),
    PLAYER_LIST(String.valueOf(new StringBuilder().append(EnumChatFormatting.AQUA).append("Player List"))),
    NOTIFICATIONS(String.valueOf(new StringBuilder().append(EnumChatFormatting.WHITE).append("Notifications"))),
    NEW_PREMIUM(String.valueOf(new StringBuilder().append(EnumChatFormatting.GOLD).append("Premium"))),
    OTHER(String.valueOf(new StringBuilder().append(EnumChatFormatting.GRAY).append("Other"))),
    INTERNAL(""),
    TIMER(""),
    STAFFCHECKER(String.valueOf(new StringBuilder().append(EnumChatFormatting.DARK_RED).append("Staff")));

    public final String displayName;

    private AutoUseType(String displayName) {
        this.displayName = displayName;
    }

    private static AutoUseType[] computeAutoUseTypeArray() {
        return new AutoUseType[]{AUTO_USE, RENDER, MISC, COMBAT, PLAYER_LIST, NOTIFICATIONS, NEW_PREMIUM, OTHER, INTERNAL, TIMER, STAFFCHECKER};
    }
}
