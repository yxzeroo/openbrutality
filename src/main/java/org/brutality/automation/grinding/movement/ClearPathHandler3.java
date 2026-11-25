package org.brutality.automation.grinding.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import org.brutality.core.ExtraAttributesHandler;
import org.brutality.util.GetNonBotPlayersHelper;
import org.brutality.util.StopwatchTimer;
import org.brutality.util.math.BuildTargetComparatorHelper;

public class ClearPathHandler3 {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final org.brutality.automation.navigation.ClearPathHandler clearPathHandler = new org.brutality.automation.navigation.ClearPathHandler();
    private static final StopwatchTimer utilHelper8 = new StopwatchTimer(0L);
    private static final String text = "No viable path, pausing for a few seconds";
    private static final long timestamp = 6000L;

    public static void clearPath() {
        clearPathHandler.clearPath();
    }

    private static boolean isPaused() {
        return !utilHelper8.matchesLong(timestamp);
    }

    public static void reset() {
        clearPathHandler.clearPath();
        utilHelper8.clear();
    }

    public static void handleFlag(boolean flag) {
        BlockPos pos = GetNonBotPlayersHelper.computeBlockPos3();
        if (pos != null && mc.thePlayer != null) {
            if (clearPathHandler.checkState()) {
                if (!isPaused()) {
                    mc.thePlayer.addChatMessage(new ChatComponentText(text));
                }

                utilHelper8.reset();
            }

            if (isPaused()) {
                clearPathHandler.process2();
            } else {
                double d;
                if (mc.thePlayer != null) {
                    if (pos == null) {
                        d = -1.0;
                    } else {
                        d = mc.thePlayer.getDistance(pos.getX(), pos.getY(), pos.getZ());
                    }
                } else {
                    d = -1.0;
                }

                if (d < 1.8F) {
                    BuildTargetComparatorHelper.handleBlockPos(pos, 31, 11);
                    clearPathHandler.process2();
                } else {
                    clearPathHandler.handleBlockPos(pos, !flag, 31, 11, 0.3F);
                    ExtraAttributesHandler.process2();
                }
            }
        } else {
            clearPathHandler.process2();
        }
    }
}
