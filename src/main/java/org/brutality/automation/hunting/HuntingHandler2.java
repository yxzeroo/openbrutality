package org.brutality.automation.hunting;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public final class HuntingHandler2 {
    public static HuntingHandler2.Entry computeEntry(EntityPlayerSP player, World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        int currentItem = player.inventory.currentItem;
        HuntingHandler2.Entry entry = new HuntingHandler2.Entry(currentItem, computeFloat(player, world, pos, player.inventory.getStackInSlot(currentItem)));

        for (int i = 0; i < 9; i++) {
            HuntingHandler2.Entry entry2 = new HuntingHandler2.Entry(i, computeFloat(player, world, pos, player.inventory.getStackInSlot(i)));
            if (entry2.count2 < entry.count2 || entry2.count2 == entry.count2 && entry2.factor > entry.factor) {
                entry = entry2;
            }
        }

        return entry;
    }

    public static float computeFloat(EntityPlayerSP player, World world, BlockPos pos, ItemStack stack) {
        Block block = world.getBlockState(pos).getBlock();
        float blockHardness = block.getBlockHardness(world, pos);
        if (blockHardness < 0.0F) {
            return 0.0F;
        }
        if (!player.capabilities.isCreativeMode && blockHardness != 0.0F) {
            float f = stack == null ? 1.0F : stack.getStrVsBlock(block);
            int n = stack == null ? 0 : EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);
            if (f > 1.0F && n > 0) {
                f += n * n + 1;
            }

            if (player.isPotionActive(Potion.digSpeed)) {
                f *= 1.0F + (player.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
            }

            if (player.isPotionActive(Potion.digSlowdown)) {
                int amplifier = player.getActivePotionEffect(Potion.digSlowdown).getAmplifier();
                f *= amplifier == 0 ? 0.3F : (amplifier == 1 ? 0.09F : (amplifier == 2 ? 0.0027F : 8.1E-4F));
            }

            if (player.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(player)) {
                f /= 5.0F;
            }

            if (!player.onGround) {
                f /= 5.0F;
            }

            boolean flag = block.getMaterial().isToolNotRequired() || stack != null && stack.canHarvestBlock(block);
            return f / blockHardness / (flag ? 30 : 100);
        }
        return 1.0F;
    }

    private HuntingHandler2() {
    }

    public static final class Entry {
        public final int count;
        public final int count2;
        public final float factor;
        private static final long timestamp = 1283768553010364415L;

        public Entry(int count, float factor) {
            this.count = count;
            this.factor = factor;
            this.count2 = factor > 0.0F ? (factor >= 1.0F ? 1 : Math.max(1, (int)Math.ceil(1.0 / factor)) + 1) : (int)timestamp;
        }
    }
}
