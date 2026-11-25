package org.brutality.utils.misc;

import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;

public class GetBlockHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

    public static boolean matchesBlock2(Block block) {
        if (matchesBlock3(block) || block == Blocks.bed || block == Blocks.lever) {
            return true;
        }
        if (block instanceof BlockTrapDoor || block instanceof BlockFence || block instanceof BlockFenceGate) {
            return true;
        }
        return !(block instanceof BlockDoor) ? false : block != Blocks.iron_door;
    }

    public static Block getBlock(BlockPos pos) {
        return computeIBlockState(pos).getBlock();
    }

    public static float computeFloat(ItemStack stack, Block block, boolean flag) {
        float f = stack == null ? 1.0F : stack.getItem().getStrVsBlock(stack, block);
        int n;
        if (f > 1.0F && (n = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack)) > 0) {
            f += n * n + 1;
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

            if (!mc.thePlayer.onGround) {
                f /= 5.0F;
            }
        }

        return f;
    }

    public static boolean matchesBlock4(Block block) {
        if (block == null || block == Blocks.air) {
            return false;
        }
        if (block instanceof BlockLiquid) {
            return false;
        }
        return block.getMaterial().isReplaceable() ? false : !matchesBlock2(block);
    }

    public static Block computeBlock(ItemStack stack) {
        return matchesItemStack(stack) ? ((ItemBlock)stack.getItem()).getBlock() : null;
    }

    public static BlockPos computeBlockPos() {
        return new BlockPos(MathHelper.floor_double(mc.thePlayer.posX), MathHelper.floor_double(mc.thePlayer.posY), MathHelper.floor_double(mc.thePlayer.posZ));
    }

    public static void handleList(List<BlockPos> list, Set<BlockPos> set, Set<BlockPos> set2, BlockPos pos) {
        for (BlockPos pos2 : set2) {
            if (computeInt(pos2, pos) <= 3) {
                for (EnumFacing facing : EnumFacing.values()) {
                    if (set.contains(pos2.offset(facing))) {
                        list.add(pos2);
                        break;
                    }
                }
            }
        }
    }

    public static boolean matchesBlockPos4(BlockPos pos) {
        if (matchesBlockPos2(pos)) {
            return true;
        }
        Block block = getBlock(pos);
        return block.isFullBlock() ? false : block instanceof BlockSlab || block == Blocks.snow_layer;
    }

    public static boolean matchesBlockPos(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        return getBlock(mutableBlockPos.func_181079_c(x + 1, y, z)) == Blocks.bed
            || getBlock(mutableBlockPos.func_181079_c(x - 1, y, z)) == Blocks.bed
            || getBlock(mutableBlockPos.func_181079_c(x, y, z + 1)) == Blocks.bed
            || getBlock(mutableBlockPos.func_181079_c(x, y, z - 1)) == Blocks.bed
            || getBlock(mutableBlockPos.func_181079_c(x, y - 1, z)) == Blocks.bed;
    }

    public static float computeFloat2(Block block, ItemStack stack, boolean flag) {
        if (block == null) {
            return 0.0F;
        }
        float blockHardness = block.getBlockHardness(mc.theWorld, mc.thePlayer.getPosition());
        if (blockHardness < 0.0F) {
            return 0.0F;
        }
        boolean flag2 = block.getMaterial().isToolNotRequired() || stack != null && stack.canHarvestBlock(block);
        return flag2 ? computeFloat(stack, block, flag) / blockHardness / 30.0F : computeFloat(stack, block, flag) / blockHardness / 100.0F;
    }

    public static float computeFloat(Block block, ItemStack stack, boolean flag) {
        float f = computeFloat2(block, stack, flag);
        return f <= 0.0F ? 0.0F : 1.0F / f;
    }

    public static boolean matchesBlockPos2(BlockPos pos) {
        Block block = getBlock(pos);
        return block == Blocks.air || block == Blocks.fire || block instanceof BlockLiquid || block.getMaterial().isReplaceable();
    }

    public static int computeInt(BlockPos pos, BlockPos pos2) {
        return Math.max(Math.abs(pos.getX() - pos2.getX()), Math.abs(pos.getZ() - pos2.getZ()));
    }

    public static boolean matchesItemStack(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemBlock;
    }

    public static boolean matchesMovingObjectPosition(MovingObjectPosition hit, ItemStack stack) {
        return ((ItemBlock)stack.getItem()).canPlaceBlockOnSide(mc.theWorld, hit.getBlockPos(), hit.sideHit, mc.thePlayer, stack);
    }

    public static boolean matchesBlock(Block block) {
        return block == Blocks.glass
            || block.isFullBlock()
                && block != Blocks.gravel
                && block != Blocks.sand
                && block != Blocks.soul_sand
                && block != Blocks.tnt
                && block != Blocks.crafting_table
                && block != Blocks.furnace
                && block != Blocks.dispenser
                && block != Blocks.dropper
                && block != Blocks.noteblock
                && block != Blocks.command_block;
    }

    public static boolean matchesBlock3(Block block) {
        return block instanceof BlockContainer || block == Blocks.crafting_table || block == Blocks.anvil;
    }

    public static IBlockState computeIBlockState(BlockPos pos) {
        return mc.theWorld.getBlockState(pos);
    }

    public static int computeInt(BlockPos pos, Set<BlockPos> set) {
        int MAX_VALUE = Integer.MAX_VALUE;

        for (BlockPos pos2 : set) {
            int n = Math.abs(pos.getX() - pos2.getX());
            int n2 = Math.abs(pos.getZ() - pos2.getZ());
            int n3 = Math.abs(pos.getY() - pos2.getY());
            int n4 = Math.max(n, n2) + n3 * 2;
            if (n4 < MAX_VALUE) {
                MAX_VALUE = n4;
            }
        }

        return MAX_VALUE == Integer.MAX_VALUE ? 0 : MAX_VALUE;
    }

    public static boolean matchesBlockPos3(BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            if (matchesBlock4(getBlock(pos.offset(facing.getOpposite())))) {
                return true;
            }
        }

        return false;
    }

    public static void handleSet(Set<BlockPos> set, Set<BlockPos> set2, Set<BlockPos> set3, BlockPos pos, int n, int n2, int n3) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        for (int i = -n; i <= n; i++) {
            int n4 = Math.abs(i);

            for (int j = -n; j <= n; j++) {
                if (Math.max(n4, Math.abs(j)) <= n) {
                    for (int k = n2 - 1; k <= n3 + 1; k++) {
                        mutableBlockPos.func_181079_c(x + i, y + k, z + j);
                        if (!matchesBlockPos5(mutableBlockPos)) {
                            if (matchesBlock4(getBlock(mutableBlockPos))) {
                                set.add(new BlockPos(mutableBlockPos));
                            } else {
                                BlockPos pos2;
                                if (k >= n2
                                    && k <= n3
                                    && matchesBlockPos4(mutableBlockPos)
                                    && !set3.contains(pos2 = new BlockPos(mutableBlockPos))) {
                                    set2.add(pos2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean matchesBlockPos5(BlockPos pos) {
        BlockPos pos2 = computeBlockPos();
        return pos.equals(pos2) || pos.equals(pos2.up());
    }
}
