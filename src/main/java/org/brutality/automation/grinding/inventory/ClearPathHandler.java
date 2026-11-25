package org.brutality.automation.grinding.inventory;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.brutality.automation.mining.FenceHandler;
import org.brutality.core.ExtraAttributesHandler;
import org.brutality.util.BEASTHelper;
import org.brutality.util.math.BuildTargetComparatorHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;

public class ClearPathHandler {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final Minecraft mc2 = Minecraft.getMinecraft();
    private final int count;
    private final org.brutality.automation.navigation.ClearPathHandler clearPathHandler;
    private BlockPos targetPosition;
    private Random random;
    private static final long timestamp = -2426624447424233461L;

    public ClearPathHandler() {
        this.count = (int)timestamp;
        this.clearPathHandler = new org.brutality.automation.navigation.ClearPathHandler();
        this.targetPosition = null;
        this.random = new Random();
    }

    public void handleFlag(boolean flag) {
        if (FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
            if (this.targetPosition != null && this.mc2.thePlayer != null) {
                if (Math.sqrt(this.mc2.thePlayer.getDistanceSqToCenter(this.targetPosition)) < 2.25) {
                    this.clearPathHandler.process2();
                } else {
                    this.clearPathHandler.handleBlockPos(this.targetPosition, !flag, 45, 11, 0.5);
                    ExtraAttributesHandler.process2();
                }
            } else {
                this.clearPathHandler.process2();
            }
        }
    }

    public void process() {
        if (this.mc2.thePlayer != null) {
            BlockPos pos = BEASTHelper.computeBlockPos(this.mc2.thePlayer).down();

            for (int i = 0; i < 1000; i++) {
                int n = this.random.nextInt(24) - 12;
                BlockPos pos2 = new BlockPos(n, pos.getY(), this.random.nextInt(24) - 12);
                boolean flag;
                if (FenceHandler.matchesBlockPos5(pos2)) {
                    flag = false;
                } else if (!FenceHandler.matchesBlockPos5(pos2.up())) {
                    flag = false;
                } else if (!FenceHandler.matchesBlockPos5(pos2.up(2))) {
                    flag = false;
                } else if (!BuildTargetComparatorHelper.matchesEntityPlayer(this.mc2.thePlayer, new Vec3(pos2.up(3)))) {
                    flag = false;
                } else {
                    for (int j = -2; j <= 2; j++) {
                        for (int k = -2; k <= 2; k++) {
                            BlockPos pos3 = pos2.add(j, 0, k);
                            if (FenceHandler.matchesBlockPos5(pos3)) {
                                flag = false;
                                break;
                            }

                            if (this.mc2.theWorld.getBlockState(pos3).getBlock() == Blocks.slime_block) {
                                flag = false;
                                break;
                            }
                        }
                    }

                    flag = true;
                }

                if (flag) {
                    this.targetPosition = pos2;
                    return;
                }
            }
        }
    }

    public void clearPath() {
        this.clearPathHandler.clearPath();
    }
}
