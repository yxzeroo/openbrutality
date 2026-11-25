package org.brutality.automation.grinding.movement;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.brutality.util.GetNonBotPlayersHelper;
import org.brutality.util.KeybindStateUtil;
import org.brutality.util.math.BuildTargetComparatorHelper;
import org.brutality.utils.MessageUtils;

public class ChooseTargetPositionHandler {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static double amount = 2.5;
    private static double amount2 = 0.5;
    private static BlockPos targetPosition = null;
    private static org.brutality.automation.navigation.ClearPathHandler clearPathHandler = new org.brutality.automation.navigation.ClearPathHandler();

    public static void reset() {
        targetPosition = null;
        clearPathHandler.clearPath();
    }

    public static void handleInt(int n, int n2) {
        if (targetPosition == null) {
            MessageUtils.x("[AutoGrinder] No target position set, cannot perform spawn AFK.");
            clearPathHandler.handleFlag2(false);
        } else {
            EntityPlayerSP player = mc.thePlayer;
            double x = targetPosition.getX();
            double y = targetPosition.getY();
            double dist = player.getDistance(x, y, targetPosition.getZ());
            boolean flag = y < 4.0;
            if (dist < amount) {
                KeybindStateUtil.process();
            } else {
                clearPathHandler.handleBlockPos(targetPosition, n, n2, amount2);
                clearPathHandler.handleFlag2(true);
            }
        }
    }

    public static void chooseTargetPosition() {
        amount = 1.6F + Math.random() * 1.5;
        amount2 = Math.random();
        ArrayList<BlockPos> list = new ArrayList<>();
        BlockPos pos = GetNonBotPlayersHelper.computeBlockPos5();
        if (pos != null) {
            list.add(pos);
        }

        BlockPos pos2 = GetNonBotPlayersHelper.computeBlockPos2();
        if (pos2 != null) {
            list.add(pos2);
        }

        BlockPos pos3 = GetNonBotPlayersHelper.computeBlockPos4();
        if (pos3 != null) {
            list.add(pos3);
        }

        var list2 = GetNonBotPlayersHelper.computeList(Blocks.enchanting_table);
        var list3 = GetNonBotPlayersHelper.computeList(Blocks.ender_chest);
        list3.replaceAll(pos4 -> pos4.add(0.5, 0.5, 0.5));
        list2.replaceAll(pos5 -> pos5.add(0.5, 0.5, 0.5));
        int n = 0;

        BlockPos pos6;
        while (true) {
            if (n >= 10) {
                pos6 = computeBlockPos();
                break;
            }

            BlockPos pos7 = computeBlockPos();
            if (BuildTargetComparatorHelper.matchesEntityPlayer(mc.thePlayer, new Vec3(pos7))) {
                pos6 = pos7;
                break;
            }

            n++;
        }

        list.addAll(list2);
        list.addAll(list3);
        BlockPos pos8;
        if (list.isEmpty()) {
            pos8 = null;
        } else {
            BlockPos pos9 = null;
            double MAX_VALUE = Double.MAX_VALUE;

            for (BlockPos pos10 : list) {
                EntityPlayerSP player = mc.thePlayer;
                double x = pos10.getX();
                double y = pos10.getY();
                double dist = player.getDistance(x, y, pos10.getZ());
                if (y < MAX_VALUE) {
                    MAX_VALUE = dist;
                    pos9 = pos10;
                }
            }

            if (Math.random() >= 0.6) {
                if (pos9 != null && !BuildTargetComparatorHelper.matchesEntityPlayer(mc.thePlayer, new Vec3(pos9))) {
                    ;
                }

                pos8 = pos9;
            } else {
                pos8 = pos6;
            }
        }

        targetPosition = pos8;
    }

    private static BlockPos computeBlockPos() {
        return new BlockPos(
            mc.thePlayer.posX + (Math.random() + 2.0) * 3.0 - 3.0,
            mc.thePlayer.posY + mc.thePlayer.getEyeHeight() + Math.random() * 0.15 - 0.075,
            mc.thePlayer.posZ + (Math.random() + 2.0) * 3.0 - 3.0
        );
    }
}
