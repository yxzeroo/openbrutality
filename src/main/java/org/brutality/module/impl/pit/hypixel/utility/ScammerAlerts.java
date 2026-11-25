package org.brutality.module.impl.pit.hypixel.utility;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;

public class ScammerAlerts extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof ChatEvent) {
                if (mc.thePlayer != null) {
                    String str = ((ChatEvent)event).message.getUnformattedText().replaceAll("§[0-9a-fk-or]", "");
                    String str2 = null;
                    if (str.startsWith("TRADE REQUEST! Sent to ")) {
                        str2 = str.replace("TRADE REQUEST! Sent to ", "").split(" ")[1].replace("!", "");
                    } else if (str.startsWith("TRADE REQUEST! From ")) {
                        str2 = str.replace("TRADE REQUEST! From ", "").split(" ")[1].replace("!", "");
                    }

                    if (str2 != null && !str2.isEmpty()) {
                        EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
                        EnumChatFormatting YELLOW = EnumChatFormatting.YELLOW;
                        EnumChatFormatting DARK_GRAY = EnumChatFormatting.DARK_GRAY;
                        EnumChatFormatting RED = EnumChatFormatting.RED;
                        mc.thePlayer
                            .addChatMessage(
                                new ChatComponentText(
                                    EnumChatFormatting.DARK_GRAY
                                        + "["
                                        + RED
                                        + "ScammerAlerts"
                                        + DARK_GRAY
                                        + "] "
                                        + YELLOW
                                        + "Trade with "
                                        + str2
                                        + GRAY
                                        + " - check if they're a known scammer!"
                                )
                            );
                    }
                }
            }
        }
    }

    public ScammerAlerts() {
        super("ScammerAlerts", 0, Category.PIT);
    }
}
