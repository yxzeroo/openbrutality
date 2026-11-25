package org.brutality.utils.player;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockTNT;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public final class ExtraAttributesHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final List<Item> entries = Arrays.asList(
        Items.fishing_rod, Items.water_bucket, Items.bucket, Items.arrow, Items.bow, Items.snowball, Items.egg, Items.ender_pearl
    );

    public static ItemStack computeItemStack(String str, String str2) {
        String str3 = String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", str2);
        String str4 = Base64.getEncoder().encodeToString(str3.getBytes());
        return computeItemStack(
            String.format("skull 1 3 {SkullOwner:{Id:\"%s\",Name:\"%s\",Properties:{textures:[{Value:\"%s\"}]}}}", UUID.randomUUID(), str, str4)
        );
    }

    public static ItemStack computeItemStack(String str) {
        try {
            str = str.replace('&', '§');
            int n = 1;
            int n2 = 0;
            String[] parts = str.split(" ");
            ResourceLocation location = new ResourceLocation(parts[0]);
            Item item = Item.itemRegistry.getObject(location);
            if (parts.length >= 2 && parts[1].matches("\\d+")) {
                n = Integer.parseInt(parts[1]);
            }

            if (parts.length >= 3 && parts[2].matches("\\d+")) {
                n2 = Integer.parseInt(parts[2]);
            }

            ItemStack stack = new ItemStack(item, n, n2);
            if (parts.length >= 4) {
                StringBuilder sb = new StringBuilder();

                for (int i = 3; i < parts.length; i++) {
                    sb.append(" ").append(parts[i]);
                }

                stack.setTagCompound(JsonToNBT.getTagFromJson(sb.toString()));
            }

            return stack;
        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack(Blocks.barrier);
        }
    }

    public static boolean checkState() {
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        return heldItem == null ? false : heldItem.getItem() instanceof ItemSword;
    }

    public static boolean checkState3() {
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (heldItem == null) {
            return false;
        }
        if (heldItem.hasTagCompound()) {
            NBTTagCompound tagCompound = heldItem.getTagCompound();
            if (tagCompound.hasKey("ExtraAttributes")) {
                NBTTagCompound compoundTag = tagCompound.getCompoundTag("ExtraAttributes");
                if (compoundTag.hasKey("UHCid")) {
                    long l = compoundTag.getLong("UHCid");
                    if (l == 50006L || l == 50009L) {
                        return true;
                    }
                }
            }

            if (tagCompound.hasKey("HideFlags") && heldItem.getItem() instanceof ItemSpade && ((ItemSpade)heldItem.getItem()).getToolMaterial() == ToolMaterial.EMERALD) {
                return true;
            }
        }

        if (heldItem.getItem() instanceof ItemEnchantedBook) {
            return false;
        }
        return EnchantmentHelper.getEnchantments(heldItem).containsKey(19) ? true : heldItem.getItem() instanceof ItemSword;
    }

    public static boolean checkState4() {
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        return heldItem == null ? false : heldItem.getItem() instanceof ItemBow;
    }

    public static boolean checkState2() {
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        return heldItem == null ? false : heldItem.getItem() instanceof ItemTool;
    }

    public static String computeString(String str, String str2) {
        String str3 = String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", str2);
        String str4 = Base64.getEncoder().encodeToString(str3.getBytes());
        return String.format("SkullOwner:{Id:\"%s\",Name:\"%s\",Properties:{textures:[{Value:\"%s\"}]}}", UUID.randomUUID(), str, str4);
    }

    @Generated
    private ExtraAttributesHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static boolean matchesItemStack(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ItemPotion item2) {
            return ItemPotion.isSplash(stack.getMetadata()) && PlayerUtils.matchesInt2(item2.getEffects(stack).get(0).getPotionID());
        }
        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock)item).getBlock();
            if (block instanceof BlockGlass
                || block instanceof BlockStainedGlass
                || block.isFullBlock() && !(block instanceof BlockTNT) && !(block instanceof BlockSlime) && !(block instanceof BlockFalling)) {
                return true;
            }
        }

        return item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemArmor || item instanceof ItemFood || entries.contains(item);
    }
}
