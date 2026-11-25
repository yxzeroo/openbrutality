package org.brutality.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import org.brutality.Brutality;

public enum PartyWarpType {
    PARTY_WARP("/p warp"),
    PARTY_LEAVE("/p leave"),
    OOF("/oof"),
    SPAWN("/spawn"),
    HUB("/hub"),
    PLAY_PIT("/play pit"),
    REPORT("/report"),
    PARTY("/p"),
    AUCTION_MENU("/auctionmenu");

    private final String commandText;
    private static final Minecraft MINECRAFT = Minecraft.getMinecraft();
    private static final Map<PartyWarpType, Long> lastExecutionTimes = new HashMap<>();

    public boolean matchesInt(int n) {
        return this.matchesInt(n, null);
    }

    public boolean matchesInt(int n, String str) {
        long l = n;
        long now = System.currentTimeMillis();
        boolean flag;
        if (lastExecutionTimes.containsKey(this)) {
            if (now - lastExecutionTimes.get(this) >= l) {
                flag = true;
            } else {
                flag = false;
            }
        } else {
            flag = true;
        }

        if (!flag) {
            return false;
        }
        if (str != null && !str.isEmpty()) {
            ExecuteCommandHelper.handleString2(str);
        }

        this.process();
        lastExecutionTimes.put(this, System.currentTimeMillis());
        return true;
    }

    public void handleInt(int n) {
        Brutality.scheduledExecutorService.schedule(this::process, (long)n, TimeUnit.MILLISECONDS);
    }

    public void handleString(String str) {
        MINECRAFT.addScheduledTask(() -> {
            if (MINECRAFT.thePlayer != null) {
                MINECRAFT.thePlayer.sendChatMessage(String.valueOf(new StringBuilder().append(this.commandText).append(" ").append(str)));
            }
        });
    }

    private static PartyWarpType[] computePartyWarpTypeArray() {
        return new PartyWarpType[]{PARTY_WARP, PARTY_LEAVE, OOF, SPAWN, HUB, PLAY_PIT, REPORT, PARTY, AUCTION_MENU};
    }

    public void process() {
        MINECRAFT.addScheduledTask(() -> {
            if (MINECRAFT.thePlayer != null) {
                MINECRAFT.thePlayer.sendChatMessage(this.commandText);
            }
        });
    }

    public void handleInt(int n, String str) {
        Brutality.scheduledExecutorService.schedule(() -> this.handleString(str), (long)n, TimeUnit.MILLISECONDS);
    }

    private PartyWarpType(String commandText) {
        this.commandText = commandText;
    }
}
