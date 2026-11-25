package org.brutality.module.impl.player;

import net.minecraft.network.play.server.S02PacketChat;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;
import org.brutality.utils.timer.TimerUtil;

public class AutoHypixel extends Module {
    private final TimerUtil timerUtil = new TimerUtil();
    private boolean flag;
    private static final long timestamp = 1500L;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate && this.flag && this.timerUtil.computeLong3() >= timestamp) {
                mc.thePlayer.sendChatMessage("/play solo_normal");
                this.timerUtil.reset();
                this.flag = false;
            }

            if (event instanceof GetPacketEvent getPacketEvent && getPacketEvent.getPacket() instanceof S02PacketChat) {
                S02PacketChat packet = getPacketEvent.getPacket();
                String text = packet.getChatComponent().getUnformattedText();
                if ((text.contains("You won! Want to play again? Click here!") || text.contains("You died! Want to play again? Click here!"))
                    && text.length() < "You won! Want to play again? Click here!".length() + 3) {
                    this.flag = true;
                    this.process();
                    this.timerUtil.reset();
                }
            }
        }
    }

    public void process() {
        NotificationFeedHandler.handleString("§e[Auto Play!] §fSending you to a new lobby..");
    }

    public AutoHypixel() {
        super("Auto Hypixel", 0, Category.PLAYER);
    }
}
