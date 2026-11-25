package org.brutality.utils.network;

import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import org.brutality.utils.mcp.McpApi;

public final class NetworkHelper implements McpApi {
    public static void handleDouble(double d, boolean flag) {
        handlePacket2(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + d, mc.thePlayer.posZ, flag));
    }

    public static void handlePacket2(Packet<?> packet) {
        mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }

    public static void handlePacket(Packet<?> packet, int n) {
        for (int i = 0; i < n; i++) {
            handlePacket(packet);
        }
    }

    public static void handlePacket2(Packet<?> packet, int n) {
        for (int i = 0; i < n; i++) {
            handlePacket2(packet);
        }
    }

    public static void handlePacket(Packet<?> packet) {
        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
    }

    public static boolean matchesPacket(Packet packet) {
        return !(packet instanceof C00PacketLoginStart)
            && !(packet instanceof C00Handshake)
            && !(packet instanceof C00PacketServerQuery)
            && !(packet instanceof C01PacketPing);
    }

    public static boolean matchesPacket2(Packet<?> packet) {
        return packet instanceof S01PacketJoinGame || packet instanceof S07PacketRespawn || packet instanceof S08PacketPlayerPosLook;
    }
}
