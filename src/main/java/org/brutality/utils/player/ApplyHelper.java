package org.brutality.utils.player;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.brutality.module.impl.misc.ChatMute;

public final class ApplyHelper {
    private ApplyHelper() {
    }

    public static IChatComponent apply(IChatComponent component) {
        if (component == null) {
            return null;
        }
        if (matchesIChatComponent(component)) {
            return component;
        }
        String str = NormalizeHelper.computeString(component);
        if (str != null && PlayerHelper7.matchesString2(str)) {
            IChatComponent component2 = component.createCopy();
            ChatMute instance = ChatMute.getInstance();
            ChatComponentText chatComponentText = instance != null
                ? instance.computeChatComponentText(str)
                : new ChatComponentText("§8§o\ud83d\udd07 §7" + str + " §8said something §8§o(hover to reveal)");
            PlayerHelper6.handlePlayerHelper8(new PlayerHelper8(str, component2, chatComponentText));
            ChatMute.process4();
            return chatComponentText;
        }
        return component;
    }

    private static boolean matchesIChatComponent(IChatComponent component) {
        String text = component.getUnformattedText();
        return text != null && text.contains("(hover to reveal)");
    }
}
