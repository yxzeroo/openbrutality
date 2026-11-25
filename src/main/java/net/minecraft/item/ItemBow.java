package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import org.brutality.event.impl.player.GetPlayerEvent;
import org.brutality.module.impl.pit.hypixel.utility.PitDebugger;

public class ItemBow extends Item {
    public static final String[] bowPullIconNameArray = new String[]{"pulling_0", "pulling_1", "pulling_2"};

    public ItemBow() {
        this.maxStackSize = 1;
        this.setMaxDamage(384);
        this.setCreativeTab(CreativeTabs.tabCombat);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft) {
        if (worldIn.isRemote) {
            PitDebugger.handleEntityPlayer(playerIn, this.getMaxItemUseDuration(stack) - timeLeft);
        }

        boolean flag = playerIn.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;
        if (flag || playerIn.inventory.hasItem(Items.arrow)) {
            int n = this.getMaxItemUseDuration(stack) - timeLeft;
            float f = n / 20.0F;
            float f2 = (f * f + f * 2.0F) / 3.0F;
            int itemInUseDuration = playerIn.getItemInUseDuration();
            GetPlayerEvent getPlayerEvent = new GetPlayerEvent(playerIn, stack, itemInUseDuration);
            getPlayerEvent.process();
            if (f2 < 0.1) {
                return;
            }

            if (f2 > 1.0F) {
                f2 = 1.0F;
            }

            EntityArrow entity = new EntityArrow(worldIn, playerIn, f2 * 2.0F);
            if (f2 == 1.0F) {
                entity.setIsCritical(true);
            }

            int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
            if (enchantmentLevel > 0) {
                entity.setDamage(entity.getDamage() + enchantmentLevel * 0.5 + 0.5);
            }

            int enchantmentLevel2 = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
            if (enchantmentLevel2 > 0) {
                entity.setKnockbackStrength(enchantmentLevel2);
            }

            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0) {
                entity.setFire(100);
            }

            stack.damageItem(1, playerIn);
            worldIn.playSoundAtEntity(playerIn, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f2 * 0.5F);
            if (flag) {
                entity.canBePickedUp = 2;
            } else {
                playerIn.inventory.consumeInventoryItem(Items.arrow);
            }

            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
            if (!worldIn.isRemote) {
                worldIn.spawnEntityInWorld(entity);
            }
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        if (playerIn.capabilities.isCreativeMode || playerIn.inventory.hasItem(Items.arrow)) {
            playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        }

        return itemStackIn;
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }
}
