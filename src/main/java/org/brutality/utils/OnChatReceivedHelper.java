package org.brutality.utils;

import org.brutality.event.impl.chat.ChatEvent;
// VENDOR-DISABLED: import org.brutality.system.prestige.PrestigeAnnouncer;
import org.brutality.utils.player.ApplyHelper;

public class OnChatReceivedHelper {
    public void onChatReceived(ChatEvent chatEvent) {
        if (chatEvent.message != null) {
            String text = chatEvent.message.getUnformattedText();
            if (text != null && !text.isEmpty()) {
                // VENDOR-DISABLED: PrestigeAnnouncer.onChatLine(text);
            }

            if (chatEvent.byteVal == 0) {
                chatEvent.message = ApplyHelper.apply(chatEvent.message);
            }
        }
    }
}
