package org.brutality.utils.player;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.network.NetworkHelper;

public class PlayerHelper3 extends GetCurrentScreenHelper {
    public static void handleWatchdogupMode(PlayerHelper3.WatchdogupMode watchdogupMode) {
        if (mc.thePlayer != null) {
            double x = mc.thePlayer.posX;
            double y = mc.thePlayer.posY;
            double z = mc.thePlayer.posZ;
            switch (watchdogupMode) {
                case WATCHDOGUP:
                    for (int i = 0; i < 49; i++) {
                        NetworkHelper.handlePacket(
                            new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false)
                        );
                        NetworkHelper.handlePacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                    }

                    NetworkHelper.handlePacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                    break;
                case WATCHDOGDOWN:
                    for (int j = 0; j < 49; j++) {
                        NetworkHelper.handlePacket(
                            new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.0625, mc.thePlayer.posZ, false)
                        );
                        NetworkHelper.handlePacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                    }

                    NetworkHelper.handlePacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                    break;
                case NCP:
                    for (int k = 0; k <= GetSpeedHelper.computeFloat2() / 0.0625; k++) {
                        handlePacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0625, z, false));
                        handlePacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                    }

                    handlePacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
                    break;
                case VANILLA:
                    handlePacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 3.01, z, false));
                    handlePacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                    handlePacket(new C03PacketPlayer(true));
                    break;
                case VERUS:
                    for (int idx = 0; idx < 3; idx++) {
                        for (double d : new double[]{
                            0.41999998688698,
                            0.7531999805212,
                            1.00133597911214,
                            1.16610926093821,
                            1.24918707874468,
                            1.1707870772188,
                            1.0155550727022,
                            0.78502770378924,
                            0.4807108763317,
                            0.10408037809304
                        }) {
                            handlePacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y + d, z, false));
                        }

                        handlePacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                    }

                    handlePacket(new C03PacketPlayer(true));
                case SUFFOCATE:
                    handlePacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y - 2.0, z, false));
            }
        }
    }

    private static void handlePacket(Packet<?> packet) {
        NetworkHelper.handlePacket(packet);
    }

    public static enum WatchdogupMode {
        WATCHDOGUP,
        WATCHDOGDOWN,
        NCP,
        VANILLA,
        VERUS,
        SUFFOCATE;

        private static PlayerHelper3.WatchdogupMode[] computeWatchdogupModeArray() {
            return new PlayerHelper3.WatchdogupMode[]{WATCHDOGUP, WATCHDOGDOWN, NCP, VANILLA, VERUS, SUFFOCATE};
        }
    }
}
