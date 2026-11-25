package org.brutality.automation.mining;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.brutality.automation.navigation.NavigationHandler;

public final class FenceHandler {
    private static final Minecraft mc = Minecraft.getMinecraft();

    private FenceHandler() {
    }

    public static boolean matchesBlockPos(BlockPos pos) {
        return matchesBlockPos6(pos) && matchesBlockPos6(pos.up());
    }

    private static boolean matchesBlockPos2(BlockPos pos, BlockPos pos2) {
        WorldClient world = mc.theWorld;
        return world != null && MiningHandler2.matchesWorld(world, pos.getX() + 0.5, MiningHandler2.computeDouble(world, pos), pos.getZ() + 0.5, pos2);
    }

    private static boolean matchesBlockPos2(BlockPos pos) {
        WorldClient world = mc.theWorld;
        if (world == null) {
            return false;
        }
        IBlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        if (block.getMaterial() == Material.air || block.getMaterial().isLiquid()) {
            return false;
        }
        if (block instanceof BlockStairs) {
            return true;
        }
        AxisAlignedBB collisionBoundingBox = block.getCollisionBoundingBox(world, pos, blockState);
        if (collisionBoundingBox == null) {
            return false;
        }
        double d = collisionBoundingBox.maxY - pos.getY();
        return d > 0.0 && d < 1.0;
    }

    public static boolean matchesBlockPos3(BlockPos pos) {
        WorldClient world = mc.theWorld;
        if (world != null && world.isBlockLoaded(pos)) {
            Material material = world.getBlockState(pos).getBlock().getMaterial();
            return !material.isLiquid() && !material.isReplaceable() && material != Material.air;
        }
        return true;
    }

    public static boolean matchesBlockPos8(BlockPos pos) {
        WorldClient world = mc.theWorld;
        if (world == null) {
            return false;
        }
        IBlockState blockState = world.getBlockState(pos);
        return !(blockState.getBlock() instanceof BlockSlab block) ? false : !block.isDouble() && blockState.getValue(BlockSlab.HALF) == EnumBlockHalf.BOTTOM;
    }

    public static boolean matchesBlockPos6(BlockPos pos) {
        WorldClient world = mc.theWorld;
        return world != null && world.isBlockLoaded(pos) ? world.getBlockState(pos).getBlock().isPassable(world, pos) || matchesBlockPos5(pos) : false;
    }

    private static boolean matchesBlockPos3(BlockPos pos, BlockPos pos2) {
        int n = pos2.getX() - pos.getX();
        int n2 = pos2.getZ() - pos.getZ();
        if (n != 0 && n2 != 0) {
            BlockPos pos3 = new BlockPos(pos2.getX(), pos.getY(), pos.getZ());
            BlockPos pos4 = new BlockPos(pos.getX(), pos.getY(), pos2.getZ());
            boolean flag = !matchesBlockPos(pos3);
            boolean flag2 = !matchesBlockPos(pos4);
            if (!flag && !flag2) {
                boolean flag3 = !matchesBlockPos(pos2);
                if (pos.getY() == pos2.getY()) {
                    return flag3;
                }
                if (flag3) {
                    return true;
                }
                BlockPos pos5 = new BlockPos(pos2.getX(), pos.getY(), pos2.getZ());
                return !matchesBlockPos(pos5);
            }
            return true;
        }
        return !matchesBlockPos(pos2);
    }

    public static boolean matchesBlockPos4(BlockPos pos) {
        WorldClient world = mc.theWorld;
        if (world == null) {
            return false;
        }
        String str = world.getBlockState(pos).getBlock().getUnlocalizedName().toLowerCase();
        return str.contains("fence") || str.contains("wall");
    }

    private static boolean matchesBlockPos(BlockPos pos, BlockPos pos2, double d) {
        if (matchesBlockPos6(pos2.down())) {
            return false;
        }
        double d2 = d;
        if (matchesBlockPos8(pos.down()) || matchesBlockPos4(pos2.down())) {
            d2 = d + 0.5;
        }

        if (matchesBlockPos8(pos2.down()) || matchesBlockPos4(pos.down())) {
            d2 -= 0.5;
        }

        int n = NavigationHandler.computeInt();
        if (d2 > n) {
            return false;
        }
        for (int i = 2; i <= n + 1; i++) {
            if (!matchesBlockPos5(pos.up(i))) {
                return false;
            }
        }

        return matchesBlockPos5(pos2.up()) && !matchesBlockPos3(pos, pos2);
    }

    public static boolean matchesBlockPos4(BlockPos pos, BlockPos pos2) {
        int n = pos.getY() - pos2.getY();
        if (n > 10) {
            return false;
        }
        int n2 = pos2.getX() - pos.getX();
        int n3 = pos2.getZ() - pos.getZ();
        BlockPos pos3 = new BlockPos(pos.getX() + n2, pos.getY(), pos.getZ());
        BlockPos pos4 = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + n3);
        if (!matchesBlockPos(pos3) && !matchesBlockPos(pos4)) {
            return false;
        }
        BlockPos pos5 = pos2.up(n);

        for (int i = 0; i < n; i++) {
            if (!matchesBlockPos(pos5.down(i))) {
                return false;
            }
        }

        BlockPos pos6 = new BlockPos(pos2.getX(), pos.getY(), pos2.getZ());
        return !matchesBlockPos3(pos, pos6);
    }

    public static boolean matchesBlockPos5(BlockPos pos) {
        WorldClient world = mc.theWorld;
        return world != null && world.isBlockLoaded(pos) && world.isAirBlock(pos);
    }

    public static boolean checkState() {
        BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        float f = ((mc.thePlayer.rotationYaw + 180.0F) % 360.0F + 360.0F) % 360.0F - 180.0F;
        BlockPos pos2;
        if (f >= -45.0F && f < 45.0F) {
            pos2 = pos.south();
        } else if (f >= 45.0F && f < 135.0F) {
            pos2 = pos.west();
        } else if (f >= -135.0F && f < -45.0F) {
            pos2 = pos.east();
        } else {
            pos2 = pos.north();
        }

        return matchesBlockPos3(pos2);
    }

    public static boolean matchesBlockPos7(BlockPos pos) {
        WorldClient world = mc.theWorld;
        if (world == null || !world.isBlockLoaded(pos) || !world.isBlockLoaded(pos.down()) || !world.isBlockLoaded(pos.up())) {
            return false;
        }
        if (matchesBlockPos2(pos)) {
            return matchesBlockPos6(pos.up());
        }
        BlockPos pos2 = pos.down();
        return matchesBlockPos3(pos2) && !matchesBlockPos6(pos2) && matchesBlockPos(pos);
    }

    public static boolean matchesBlockPos(BlockPos pos, BlockPos pos2) {
        return matchesBlockPos7(pos2) && matchesBlockPos2(pos, pos2);
    }
}
