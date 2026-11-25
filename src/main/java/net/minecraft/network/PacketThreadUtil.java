package net.minecraft.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.src.Config;
import net.minecraft.util.IThreadListener;
import org.brutality.module.impl.pit.hypixel.utility.PitDebugger;

public class PacketThreadUtil {
    public static int lastDimensionId = Integer.MIN_VALUE;

    public static <T extends INetHandler> void checkThreadAndEnqueue(final Packet<T> p_180031_0_, final T p_180031_1_, IThreadListener p_180031_2_) throws ThreadQuickExitException {
        if (isInactiveClientHandler(p_180031_1_)) {
            throw ThreadQuickExitException.field_179886_a;
        }
        if (!p_180031_2_.isCallingFromMinecraftThread()) {
            p_180031_2_.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    if (!PacketThreadUtil.isInactiveClientHandler(p_180031_1_)) {
                        PacketThreadUtil.clientPreProcessPacket(p_180031_0_);
                        if (p_180031_1_ instanceof NetHandlerPlayClient) {
                            PitDebugger.handlePacket(p_180031_0_);
                        }

                        p_180031_0_.processPacket(p_180031_1_);
                    }
                }
            });
            throw ThreadQuickExitException.field_179886_a;
        }
        clientPreProcessPacket(p_180031_0_);
        if (p_180031_1_ instanceof NetHandlerPlayClient) {
            PitDebugger.handlePacket(p_180031_0_);
        }
    }

    static boolean isInactiveClientHandler(INetHandler handler) {
        return handler instanceof NetHandlerPlayClient && ((NetHandlerPlayClient)handler).isCleanedUp();
    }

    protected static void clientPreProcessPacket(Packet p_clientPreProcessPacket_0_) {
        if (p_clientPreProcessPacket_0_ instanceof S08PacketPlayerPosLook) {
            Config.getRenderGlobal().onPlayerPositionSet();
        }

        if (p_clientPreProcessPacket_0_ instanceof S07PacketRespawn packet) {
            lastDimensionId = packet.getDimensionID();
        } else if (p_clientPreProcessPacket_0_ instanceof S01PacketJoinGame packet2) {
            lastDimensionId = packet2.getDimension();
        } else {
            lastDimensionId = Integer.MIN_VALUE;
        }
    }
}
