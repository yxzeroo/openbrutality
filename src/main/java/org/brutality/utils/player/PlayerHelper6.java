package org.brutality.utils.player;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.gui.ChatLine;

public final class PlayerHelper6 {
    private static final ThreadLocal<PlayerHelper8> threadLocal = new ThreadLocal<>();
    private static final Map<ChatLine, PlayerHelper8> lookupMap = new WeakHashMap<>();

    public static void handlePlayerHelper8(PlayerHelper8 playerHelper8) {
        threadLocal.set(playerHelper8);
    }

    public static PlayerHelper8 computePlayerHelper8(ChatLine chatLine) {
        return chatLine == null ? null : lookupMap.get(chatLine);
    }

    public static void handleChatLine(ChatLine chatLine, PlayerHelper8 playerHelper8) {
        if (chatLine != null && playerHelper8 != null) {
            lookupMap.put(chatLine, playerHelper8);
        }
    }

    private PlayerHelper6() {
    }

    public static PlayerHelper8 computePlayerHelper8() {
        PlayerHelper8 playerHelper8 = threadLocal.get();
        threadLocal.remove();
        return playerHelper8;
    }

    public static void clear() {
        threadLocal.remove();
        lookupMap.clear();
    }
}
