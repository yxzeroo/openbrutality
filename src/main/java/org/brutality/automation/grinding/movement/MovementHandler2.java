package org.brutality.automation.grinding.movement;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.brutality.core.ConnectToServerHandler;

public class MovementHandler2 {
    public static Vec3 computeVec3() {
        BlockPos pos = computeBlockPos(null, 0.0, 0.0);
        return pos == null ? null : new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }

    private static BlockPos computeBlockPos(World world, double d, double d2) {
        if (world == null) {
            return null;
        }
        int n = ConnectToServerHandler.computeInt();
        if (n <= 0) {
            n = 90;
        }

        double angle = Math.atan2(d2, d);
        BlockPos pos = null;
        double MAX_VALUE = Double.MAX_VALUE;
        double MAX_VALUE2 = Double.MAX_VALUE;
        byte b = 40;
        int n2 = n - 2;
        int n3 = n + 6;

        for (int i = ~b + 1; i <= b; i++) {
            for (int j = ~b + 1; j <= b; j++) {
                for (int k = n2; k <= n3; k++) {
                    BlockPos pos2 = new BlockPos(i, k, j);
                    if (world.getBlockState(pos2).getBlock() == Blocks.slime_block) {
                        double angle2 = Math.atan2(j, i);
                        double d3 = Math.abs(computeDouble(angle2, angle));
                        double d4 = i * i + j * j;
                        if (d3 < MAX_VALUE || d3 == MAX_VALUE && d4 < MAX_VALUE2) {
                            MAX_VALUE = d3;
                            MAX_VALUE2 = d4;
                            pos = pos2;
                        }
                    }
                }
            }
        }

        return pos;
    }

    public static BlockPos computeBlockPos2(World world, double d, double d2) {
        return computeBlockPos(world, d, d2);
    }

    private static double computeDouble(double d, double d2) {
        double d3 = d - d2;

        while (d3 > Math.PI) {
            d3 -= Math.PI * 2;
        }

        while (d3 < -Math.PI) {
            d3 += Math.PI * 2;
        }

        return d3;
    }
}
