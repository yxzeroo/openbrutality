package org.brutality.automation.mining;

import java.util.ArrayList;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.brutality.automation.navigation.NavigationHandler;

public final class MiningHandler2 {
    public static boolean matchesWorld(World world, double d, double d2, double d3, BlockPos pos) {
        double d4 = pos.getX() + 0.5;
        double d5 = computeDouble(world, pos);
        double d6 = pos.getZ() + 0.5;
        if (d5 - d2 > NavigationHandler.computeInt() + 0.01 || d2 - d5 > 3.01) {
            return false;
        }
        if (Math.abs(d5 - d2) < 0.05) {
            return matchesWorld(world, d, d2, d3, d4, d5, d6, true);
        }
        double d7 = Math.max(d2, d5);
        return matchesWorld(world, d, d2, d3, d, d7, d3, false)
            && matchesWorld(world, d, d7, d3, d4, d7, d6, false)
            && matchesWorld(world, d4, d7, d6, d4, d5, d6, false);
    }

    public static boolean matchesBlockPos(BlockPos pos) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null && mc.theWorld != null) {
            BlockPos pos2 = computeBlockPos(pos);
            if (pos2 == null) {
                return false;
            }
            BlockPos pos3 = mc.thePlayer.onGround ? mc.thePlayer.getPosition() : computeBlockPos(mc.thePlayer.getPosition());
            return matchesBlockPos(pos2, pos3, true);
        }
        return false;
    }

    public static BlockPos computeBlockPos(BlockPos pos) {
        for (int i = 0; i <= 2; i++) {
            BlockPos pos2 = pos.down(i);
            if (FenceHandler.matchesBlockPos7(pos2)) {
                return pos2;
            }
        }

        return null;
    }

    public static boolean matchesBlockPos(BlockPos pos, BlockPos pos2, boolean flag) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null || pos2 == null) {
            return false;
        }
        if (mc.thePlayer.onGround) {
            return matchesBlockPos(pos, flag);
        }
        double d = computeDouble(mc.theWorld, pos2);
        double d2 = computeDouble(mc.theWorld, pos);
        if (flag && Math.abs(d2 - d) > 0.05) {
            return false;
        }
        if (!matchesWorld(mc.theWorld, mc.thePlayer.posX, d, mc.thePlayer.posZ, pos)) {
            return false;
        }
        double minY = mc.thePlayer.getEntityBoundingBox().minY;
        return matchesWorld(mc.theWorld, mc.thePlayer.posX, minY, mc.thePlayer.posZ, pos.getX() + 0.5, minY, pos.getZ() + 0.5, false);
    }

    private MiningHandler2() {
    }

    private static boolean matchesWorld2(World world, double d, double d2, double d3) {
        BlockPos pos = new BlockPos(d, d2 - 0.05, d3);
        if (!world.isBlockLoaded(pos)) {
            return false;
        }
        IBlockState blockState = world.getBlockState(pos);
        AxisAlignedBB collisionBoundingBox = blockState.getBlock().getCollisionBoundingBox(world, pos, blockState);
        return collisionBoundingBox != null && collisionBoundingBox.maxY >= d2 - 0.6 && collisionBoundingBox.maxY <= d2 + 0.01;
    }

    public static double computeDouble(World world, BlockPos pos) {
        if (world != null && world.isBlockLoaded(pos)) {
            IBlockState blockState = world.getBlockState(pos);
            AxisAlignedBB collisionBoundingBox = blockState.getBlock().getCollisionBoundingBox(world, pos, blockState);
            return collisionBoundingBox != null && collisionBoundingBox.maxY > pos.getY() && collisionBoundingBox.maxY <= pos.getY() + 1 ? collisionBoundingBox.maxY : pos.getY();
        }
        return pos.getY();
    }

    public static boolean matchesWorld(World world, double d, double d2, double d3) {
        if (world != null && Double.isFinite(d + d2 + d3)) {
            AxisAlignedBB box = new AxisAlignedBB(d - 0.299, d2 + 0.001, d3 - 0.299, d + 0.299, d2 + 1.799, d3 + 0.299);
            ArrayList list = new ArrayList();

            for (int i = MathHelper.floor_double(box.minX); i <= MathHelper.floor_double(box.maxX); i++) {
                for (int j = MathHelper.floor_double(box.minZ); j <= MathHelper.floor_double(box.maxZ); j++) {
                    for (int k = MathHelper.floor_double(box.minY) - 1; k <= MathHelper.floor_double(box.maxY); k++) {
                        BlockPos pos = new BlockPos(i, k, j);
                        if (!world.isBlockLoaded(pos)) {
                            return false;
                        }

                        IBlockState blockState = world.getBlockState(pos);
                        if (blockState.getBlock().getMaterial() == Material.lava || blockState.getBlock().getMaterial() == Material.fire) {
                            return false;
                        }

                        blockState.getBlock().addCollisionBoxesToList(world, pos, blockState, box, list, Minecraft.getMinecraft().thePlayer);
                        if (!list.isEmpty()) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
        return false;
    }

    public static boolean matchesBlockPos(BlockPos pos, boolean flag) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null && mc.thePlayer != null) {
            double minY = mc.thePlayer.getEntityBoundingBox().minY;
            return flag && Math.abs(computeDouble(mc.theWorld, pos) - minY) > 0.05
                ? false
                : matchesWorld(mc.theWorld, mc.thePlayer.posX, minY, mc.thePlayer.posZ, pos);
        }
        return false;
    }

    public static boolean matchesWorld(World world, double d, double d2, double d3, double d4, double d5, double d6, boolean flag) {
        double dist = Math.sqrt((d4 - d) * (d4 - d) + (d5 - d2) * (d5 - d2) + (d6 - d3) * (d6 - d3));
        if (dist > 32.0) {
            return false;
        }
        int n = Math.max(1, (int)Math.ceil(dist / 0.2));

        for (int i = 0; i <= n; i++) {
            double d7 = (double)i / n;
            double d8 = d + (d4 - d) * d7;
            double d9 = d2 + (d5 - d2) * d7;
            double d10 = d3 + (d6 - d3) * d7;
            if (!matchesWorld(world, d8, d9, d10) || flag && !matchesWorld2(world, d8, d9, d10)) {
                return false;
            }
        }

        return true;
    }
}
