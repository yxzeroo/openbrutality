package org.brutality.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.S02PacketChat;
import org.brutality.event.Event;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;

public class AutoGG extends Module {
    private static final String[] stringArray = new String[]{
        "PIT EVENT ENDED",
        "1st Killer - ",
        "1st Place - ",
        "Winner: ",
        " - Damage Dealt - ",
        "1st - ",
        "Winning Team - ",
        "Winners: ",
        "Winner: ",
        "Winning Team: ",
        " win the game!",
        "Top Seeker: ",
        "1st Place: ",
        "Last team standing!",
        "Winner #1 (",
        "Top Survivors",
        "Winners - "
    };

    public AutoGG() {
        super("AutoGG", 0, Category.PLAYER);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof GetPacketEvent && ((GetPacketEvent)event).getPacket() instanceof S02PacketChat) {
                S02PacketChat packet = ((GetPacketEvent)event).getPacket();
                String text = packet.getChatComponent().getUnformattedText();

                for (String str : stringArray) {
                    if (text.contains(str)) {
                        this.process();
                        break;
                    }
                }
            }
        }
    }

    private void process() {
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/achat gg");
    }
}
