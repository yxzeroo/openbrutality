package org.brutality.utils.network;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.network.OldServerPinger;
import org.brutality.Brutality;
import org.brutality.event.impl.player.EventTick;
import org.brutality.utils.GetCurrentScreenHelper;

public class PingerUtils extends GetCurrentScreenHelper {
    public static long timestamp = 30000L;
    private final OldServerPinger oldServerPinger = new OldServerPinger();
    private final Map<String, Long> lookupMap = new HashMap<>();
    private final Map<String, Boolean> lookupMap2 = new HashMap<>();
    private Long serverPing = null;

    public void onTickEvent(EventTick event) {
        this.handleServerData(Minecraft.getMinecraft().getCurrentServerData());
    }

    public void handleServerData(ServerData serverData) {
        if (serverData != null) {
            Long val = this.lookupMap.get(serverData.serverIP);
            if ((val == null || val + timestamp <= System.currentTimeMillis()) && !this.lookupMap2.getOrDefault(serverData.serverIP, false)) {
                this.lookupMap2.put(serverData.serverIP, true);
                new Thread(() -> {
                    try {
                        this.oldServerPinger.ping(serverData);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }

                    this.lookupMap2.put(serverData.serverIP, false);
                    this.lookupMap.put(serverData.serverIP, System.currentTimeMillis());
                }).start();
            }

            if (!ServerUtils.checkState() || serverData.pingToServer != 1L) {
                this.serverPing = serverData.pingToServer;
            }
        }
    }

    public static String computeString2() {
        int n = 0;
        if (!mc.isSingleplayer()) {
            NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
            if (playerInfo != null) {
                n = playerInfo.getResponseTime();
            }

            if (ServerUtils.checkState() && n == 1) {
                int n2 = Brutality.instance.getPingerUtils().getServerPing().intValue();
                if (n2 != -1) {
                    n = n2;
                }
            }
        }

        return n == 0 ? "?" : String.valueOf(n);
    }

    @Generated
    public Long getServerPing() {
        return this.serverPing;
    }
}
