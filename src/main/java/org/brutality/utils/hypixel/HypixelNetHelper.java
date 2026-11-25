package org.brutality.utils.hypixel;

import net.minecraft.client.Minecraft;

public class HypixelNetHelper {
    public static boolean checkState() {
        if (Minecraft.getMinecraft().getCurrentServerData() == null) {
            return false;
        }
        String serverIp = Minecraft.getMinecraft().getCurrentServerData().serverIP;
        return serverIp != null && serverIp.endsWith("hypixel.net");
    }
}
