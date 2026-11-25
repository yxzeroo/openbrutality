package org.brutality.mellow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import org.brutality.event.Event;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.mellow.cache.ClearCacheHandler;
import org.brutality.mellow.config.IsPregameStatsHandler;
import org.brutality.mellow.util.RemoveHelper;
import org.brutality.mellow.util.UtilHelper;

public class ProtectYourBedHandler {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final IsPregameStatsHandler isPregameStatsHandler;
    private final UtilHelper utilHelper;
    private final ClearCacheHandler clearCacheHandler;
    private final RemoveHelper removeHelper;
    private boolean flag = false;
    private boolean flag2 = false;

    public ProtectYourBedHandler(IsPregameStatsHandler isPregameStatsHandler, UtilHelper utilHelper, ClearCacheHandler clearCacheHandler, RemoveHelper removeHelper) {
        this.isPregameStatsHandler = isPregameStatsHandler;
        this.utilHelper = utilHelper;
        this.clearCacheHandler = clearCacheHandler;
        this.removeHelper = removeHelper;
    }

    private void onEventPacketReceive(EventPacketReceive event) {
        if (event.getPacket() instanceof S02PacketChat packet) {
            String text = packet.getChatComponent().getUnformattedText();
            this.handleString(text);
        }
    }

    private void handleString(String str) {
        if (!str.contains("Protect your bed") && !str.contains("ONLINE") && str.contains("BED WARS")) {
        }

        if (str.contains("Protect your bed and destroy the enemy beds.") && !str.contains(":") && !str.contains("SHOUT")) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000L);
                    this.process3();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        } else if (str.contains("Returning to hub") || str.contains("You are not in a game") || str.contains("You died")) {
            this.process();
        } else if (this.flag) {
            if (str.startsWith("ONLINE:")) {
                String str2 = str.substring("ONLINE:".length()).trim();
                String[] parts = str2.split(",\\s*");
                ArrayList list = new ArrayList<>(Arrays.asList(parts));
                this.utilHelper.handleList(list);
                this.handleList(list);
            }
        }
    }

    public void process2() {
        this.flag = false;
        this.flag2 = false;
        this.utilHelper.clear();
    }

    public void onEvent(Event event) {
        if (event instanceof EventPacketReceive) {
            this.onEventPacketReceive((EventPacketReceive)event);
        }
    }

    private void process3() {
        boolean flag = this.checkState();
        if (flag) {
            this.flag = true;
            if (this.isPregameStatsHandler.isAutoWho() && !this.flag2) {
                this.mc.thePlayer.sendChatMessage("/who");
                this.flag2 = true;
            }
        } else {
            new Thread(() -> {
                try {
                    Thread.sleep(2000L);
                    if (this.checkState()) {
                        this.flag = true;
                        if (this.isPregameStatsHandler.isAutoWho() && !this.flag2) {
                            this.mc.thePlayer.sendChatMessage("/who");
                            this.flag2 = true;
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    private void handleList(List<String> list) {
        for (String str : list) {
            if (!this.utilHelper.matchesString(str)) {
                this.clearCacheHandler.handleString2(str);
                if (Mellow.getInstance() != null) {
                    if (Mellow.getInstance().getUrchinEnabledSetting() != null && Mellow.getInstance().getUrchinEnabledSetting().isEnabled()) {
                        Mellow.getInstance().computeUtilHelper3().handleString(str);
                    }

                    if (Mellow.getInstance().getSeraphEnabledSetting() != null && Mellow.getInstance().getSeraphEnabledSetting().isEnabled()) {
                        Mellow.getInstance().computeUtilHelper3().handleString2(str);
                    }
                }
            }
        }
    }

    private boolean checkState() {
        if (this.mc.theWorld == null) {
            return false;
        }
        Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
        if (scoreboard == null) {
            return false;
        }
        ScoreObjective objectiveInDisplaySlot = scoreboard.getObjectiveInDisplaySlot(1);
        if (objectiveInDisplaySlot == null) {
            return false;
        }
        String displayName = objectiveInDisplaySlot.getDisplayName();
        String str = displayName.replaceAll("§.", "");
        return str.equals("BED WARS");
    }

    private void process() {
        this.flag = false;
        this.flag2 = false;
        this.utilHelper.clear();
    }
}
