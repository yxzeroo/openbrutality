package org.brutality.utils.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.mcp.McpApi;

public class GetBlockStateHelper implements McpApi {
    private static final long timestamp = 6485146080018369856L;

    public static Vec3 computeVec3(Vec3 vec) {
        return new Vec3(Math.floor(vec.xCoord), Math.floor(vec.yCoord), Math.floor(vec.zCoord));
    }

    public static boolean matchesAxisAlignedBB(AxisAlignedBB box, Predicate<Block> predicate) {
        for (int i = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX);
            i < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1;
            i++
        ) {
            for (int j = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ);
                j < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1;
                j++
            ) {
                BlockPos pos = new BlockPos((double)i, box.minY, (double)j);
                Block block = getBlock(pos);
                if (predicate.test(block)) {
                    AxisAlignedBB box2 = block != null ? block.getCollisionBoundingBox(mc.theWorld, pos, computeIBlockState(pos)) : null;
                    if (box2 != null && mc.thePlayer.getEntityBoundingBox().intersectsWith(box2)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean matchesBlockPos5(BlockPos pos) {
        return getBlock(pos) != null
            && Objects.requireNonNull(getBlock(pos)).canCollideCheck(computeIBlockState(pos), false)
            && mc.theWorld.getWorldBorder().contains(pos);
    }

    public static EnumFacing computeEnumFacing(BlockPos pos, double d, double d2) {
        double d3 = d - (pos.getX() + 0.5);
        double d4 = d2 - (pos.getZ() + 0.5);
        if (d3 > 0.0) {
            if (d4 > d3) {
                return EnumFacing.SOUTH;
            }
            return -d4 > d3 ? EnumFacing.NORTH : EnumFacing.EAST;
        }
        if (d4 > -d3) {
            return EnumFacing.SOUTH;
        }
        return d4 < d3 ? EnumFacing.NORTH : EnumFacing.WEST;
    }

    public static IBlockState getBlockState(BlockPos pos) {
        return mc.theWorld.getBlockState(pos);
    }

    public static boolean matchesBlock2(Block block) {
        return block instanceof BlockFenceGate
            || block instanceof BlockLadder
            || block instanceof BlockFlowerPot
            || block instanceof BlockBasePressurePlate
            || matchesBlock(block)
            || block instanceof BlockFence
            || block instanceof BlockAnvil
            || block instanceof BlockEnchantmentTable
            || block instanceof BlockChest
            || block instanceof BlockWeb
            || block instanceof BlockTallGrass;
    }

    public static boolean matchesBlockPos2(BlockPos pos) {
        return !GetCurrentScreenHelper.checkState2() ? true : getBlock(pos).isReplaceable(mc.theWorld, pos);
    }

    public static float computeFloat(Block block, ItemStack stack, boolean flag, boolean flag2) {
        float blockHardness = block.getBlockHardness(mc.theWorld, null);
        if (blockHardness < 0.0F) {
            return 0.0F;
        }
        return !block.getMaterial().isToolNotRequired() && (stack == null || !stack.canHarvestBlock(block))
            ? computeFloat(stack, block, flag, flag2) / blockHardness / 100.0F
            : computeFloat(stack, block, flag, flag2) / blockHardness / 30.0F;
    }

    public static float[] computeFloatArray(BlockPos pos) {
        return null;
    }

    public static float[] computeFloatArray(EnumFacing facing, BlockPos pos) {
        Vec3i directionVec = facing.getDirectionVec();
        Vec3 vec = new Vec3(directionVec.getX() * 0.5, directionVec.getY() * 0.5, directionVec.getZ() * 0.5);
        vec = vec.add(pos.toVec3());
        vec = vec.addVector(0.5, 0.5, 0.5);
        return null;
    }

    public static double computeDouble(BlockPos pos) {
        return mc.thePlayer.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public static Block getBlock(BlockPos pos) {
        return mc.theWorld != null && pos != null ? mc.theWorld.getBlockState(pos).getBlock() : null;
    }

    public static int computeInt(BlockPos pos) {
        return pos.getX() + pos.getY() * 1000 + pos.getZ() * (int)timestamp;
    }

    public static Material computeMaterial(BlockPos pos) {
        Block block = getBlock(pos);
        return block != null ? block.getMaterial() : null;
    }

    public static boolean matchesBlockPos6(BlockPos pos) {
        AxisAlignedBB box = getBlock(pos) != null ? getBlock(pos).getCollisionBoundingBox(mc.theWorld, pos, computeIBlockState(pos)) : null;
        return box == null ? false : box.maxX - box.minX == 1.0 && box.maxY - box.minY == 1.0 && box.maxZ - box.minZ == 1.0;
    }

    public static IBlockState computeIBlockState(BlockPos pos) {
        WorldClient world = Minecraft.getMinecraft().theWorld;
        return world.getBlockState(pos);
    }

    public static double computeDouble2(BlockPos pos) {
        return computeDouble(pos) + Math.abs(MathHelper.wrapAngleTo180_double(computeFloatArray(pos)[0] - mc.thePlayer.rotationYaw)) / 130.0;
    }

    public static Map<BlockPos, Block> computeMap(int n) {
        HashMap map = new HashMap();

        for (int i = n; i >= -n + 1; i--) {
            for (int j = n; j >= -n + 1; j--) {
                for (int k = n; k >= ~n + 1 + 1; k--) {
                    BlockPos pos = new BlockPos(mc.thePlayer.posX + i, mc.thePlayer.posY + j, mc.thePlayer.posZ + k);
                    Block block = getBlock(pos);
                    if (block != null) {
                        map.put(pos, block);
                    }
                }
            }
        }

        return map;
    }

    public static float computeFloat(ItemStack stack, Block block, boolean flag, boolean flag2) {
        float f = stack == null ? 1.0F : stack.getItem().getStrVsBlock(stack, block);
        if (f > 1.0F) {
            int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);
            if (enchantmentLevel > 0 && stack != null) {
                f += enchantmentLevel * enchantmentLevel + 1;
            }
        }

        if (mc.thePlayer.isPotionActive(Potion.digSpeed)) {
            f *= 1.0F + (mc.thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
        }

        if (!flag) {
            if (mc.thePlayer.isPotionActive(Potion.digSlowdown)) {
                f *= switch (mc.thePlayer.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
                    case 0 -> 0.3F;
                    case 1 -> 0.09F;
                    case 2 -> 0.0027F;
                    default -> 8.1E-4F;
                };
            }

            if (mc.thePlayer.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(mc.thePlayer)) {
                f /= 5.0F;
            }

            if (!mc.thePlayer.onGround && !flag2) {
                f /= 5.0F;
            }
        }

        return f;
    }

    public static boolean matchesAxisAlignedBB(AxisAlignedBB box, Function<Block, Boolean> fn) {
        for (int i = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX);
            i < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1;
            i++
        ) {
            for (int j = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ);
                j < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1;
                j++
            ) {
                Block block = getBlock(new BlockPos((double)i, box.minY, (double)j));
                if (!(Boolean)fn.apply(block)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static EnumFacing computeEnumFacing(BlockPos pos) {
        return computeEnumFacing(pos, mc.thePlayer.posX, mc.thePlayer.posZ);
    }

    public static boolean matchesBlock(Block block) {
        return block.getMaterial() == Material.lava || block.getMaterial() == Material.water;
    }

    public static boolean matchesBlockPos(BlockPos pos) {
        Material material = computeMaterial(pos);
        return material == Material.air;
    }

    public static String computeString(int n) {
        return Block.getBlockById(n).getLocalizedName();
    }

    public static float computeFloat(BlockPos pos) {
        return getBlock(pos).getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, pos);
    }

    public Block computeBlock(Vec3 vec) {
        return getBlock(new BlockPos(vec));
    }

    public static double computeDouble3(BlockPos pos) {
        return computeDouble(pos) + Math.abs(MathHelper.wrapAngleTo180_double(computeFloatArray(pos)[0] - (mc.thePlayer.rotationYaw + 180.0F))) / 130.0;
    }

    public static boolean matchesBlockPos3(BlockPos pos) {
        Material material = computeMaterial(pos);
        return material != null && material.isReplaceable() && !(getBlock(pos) instanceof BlockBed);
    }

    public static boolean matchesBlockPos4(BlockPos pos) {
        Material material = computeMaterial(pos);
        return material != null && material.isReplaceable();
    }
}
