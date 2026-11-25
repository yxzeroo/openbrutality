package org.brutality.automation.grinding.movement;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import org.brutality.module.impl.pit.hypixel.automation.AutoGrinder;
import org.brutality.util.KeybindStateUtil;
import org.brutality.util.StopwatchTimer;
import org.brutality.util.math.BuildTargetComparatorHelper;
import org.brutality.utils.mcp.McpApi;

public class ClearPathHandler {
    private static double amount;
    private static double amount2;
    private static double amount3;
    private static double amount4 = 0.5;
    private static final org.brutality.automation.navigation.ClearPathHandler clearPathHandler = new org.brutality.automation.navigation.ClearPathHandler();
    private static BlockPos blockPos;
    private static final StopwatchTimer utilHelper8 = new StopwatchTimer();
    private static final long timestamp = 5000L;

    public static void handleFlag(boolean flag) {
        BuildTargetComparatorHelper.handleDouble(amount2, McpApi.mc.thePlayer.posY + 1.0 + amount3, amount, 45, 32);
        if (flag) {
            KeybindStateUtil.process();
        } else {
            AutoGrinder.process46();
            KeyBinding.setKeyBindState(McpApi.mc.gameSettings.keyBindJump.getKeyCode(), (Math.random() > amount4 ? 1 : (Math.random() == amount4 ? 0 : -1)) < 0);
        }
    }

    private static BlockPos computeBlockPos(AutoGrinder autoGrinder) {
        BlockPos pos = autoGrinder.computeBlockPos();
        if (pos == null) {
            blockPos = null;
            return null;
        }
        if (blockPos == null || utilHelper8.matchesLong(timestamp)) {
            blockPos = MovementHandler2.computeBlockPos2(McpApi.mc.theWorld, pos.getX(), pos.getZ());
            utilHelper8.reset();
        }

        return blockPos;
    }

    public static void handleAutoGrinder(AutoGrinder autoGrinder, boolean flag) {
        clearPathHandler.handleFlag2(false);
        if (autoGrinder.checkState17()) {
            BlockPos blockPos2 = autoGrinder.getBlockPos2();
            if (blockPos2 != null) {
                clearPathHandler.handleBlockPos(blockPos2, true, 45, 32, (float)amount4);
                clearPathHandler.handleFlag2(true);
            }
        } else {
            BlockPos pos = computeBlockPos(autoGrinder);
            if (pos != null) {
                clearPathHandler.handleBlockPos(pos, true, 45, 32, 0.0);
                clearPathHandler.handleFlag2(true);
            } else if (autoGrinder.checkState()) {
                KeybindStateUtil.process();
            } else {
                handleFlag(flag);
            }
        }
    }

    public static void clearPath() {
        clearPathHandler.clearPath();
        blockPos = null;
    }

    public static void process() {
        amount2 = Math.random() * 8.0 - 4.0;
        amount3 = Math.random() * 7.0 - 7.0;
        amount = Math.random() * 8.0 - 4.0;
        amount4 = Math.min(Math.random() * 2.0, 1.0);
        clearPathHandler.clearPath();
        blockPos = null;
    }
}
