package org.brutality.utils.combat;

import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBed.EnumPartType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public final class IsSetHelper {
    private static final int count = 16;
    private static final int count2 = 4;
    private static final int count3 = 100;
    private static volatile BlockPos blockPos;
    private static volatile BlockPos blockPos2;
    private static boolean flag;
    private static int count4;

    private IsSetHelper() {
    }

    public static synchronized void tick() {
        if (flag) {
            if (checkState()) {
                flag = false;
                count4 = 0;
            } else {
                if (++count4 >= 100) {
                    flag = false;
                    count4 = 0;
                }
            }
        }
    }

    public static synchronized void process() {
        flag = true;
        count4 = 0;
    }

    private static BlockPos[] computeBlockPosArray(World world, BlockPos pos) {
        try {
            IBlockState blockState = world.getBlockState(pos);
            if (blockState != null && blockState.getBlock() instanceof BlockBed) {
                EnumFacing val = blockState.getValue(BlockBed.FACING);
                if (val == null) {
                    return null;
                }
                EnumPartType val2 = blockState.getValue(BlockBed.PART);
                return val2 == EnumPartType.HEAD ? new BlockPos[]{pos.offset(val.getOpposite()), pos} : new BlockPos[]{pos, pos.offset(val)};
            }
            return null;
        } catch (Throwable e) {
            return null;
        }
    }

    public static synchronized boolean isSet() {
        return blockPos != null || blockPos2 != null;
    }

    public static synchronized void reset() {
        blockPos = null;
        blockPos2 = null;
        flag = false;
        count4 = 0;
    }

    public static synchronized boolean isFlag() {
        return flag;
    }

    public static synchronized void handleBlockPos(BlockPos pos, BlockPos pos2) {
        blockPos = pos == null ? null : new BlockPos(pos);
        blockPos2 = pos2 == null ? null : new BlockPos(pos2);
    }

    public static synchronized boolean matchesBlockPos(BlockPos pos) {
        return pos == null ? false : pos.equals(blockPos) || pos.equals(blockPos2);
    }

    public static synchronized boolean checkState() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc == null) {
                return false;
            }
            EntityPlayerSP player = mc.thePlayer;
            WorldClient world = mc.theWorld;
            if (player != null && world != null) {
                BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
                BlockPos pos2 = null;
                double MAX_VALUE = Double.MAX_VALUE;

                for (int i = -16; i <= 16; i++) {
                    for (int j = -4; j <= 4; j++) {
                        for (int k = -16; k <= 16; k++) {
                            BlockPos pos3 = pos.add(i, j, k);
                            IBlockState blockState = world.getBlockState(pos3);
                            if (blockState != null && blockState.getBlock() instanceof BlockBed) {
                                double d = pos3.getX() + 0.5 - player.posX;
                                double d2 = pos3.getY() + 0.5 - player.posY;
                                double d3 = pos3.getZ() + 0.5 - player.posZ;
                                double d4 = d * d + d2 * d2 + d3 * d3;
                                if (d4 < MAX_VALUE) {
                                    MAX_VALUE = d4;
                                    pos2 = pos3;
                                }
                            }
                        }
                    }
                }

                if (pos2 == null) {
                    return false;
                }
                BlockPos[] posArray = computeBlockPosArray(world, pos2);
                if (posArray == null) {
                    return false;
                }
                handleBlockPos(posArray[0], posArray[1]);
                return true;
            }
            return false;
        } catch (Throwable e) {
            return false;
        }
    }
}
