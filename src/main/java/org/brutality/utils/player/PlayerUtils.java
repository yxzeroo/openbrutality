package org.brutality.utils.player;

import com.google.common.collect.Multimap;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.brutality.event.impl.movement.IsSafewalkEvent;
import org.brutality.utils.mcp.McpApi;

public class PlayerUtils implements McpApi {
    public static final double amount = 0.21585904623731839;
    public static final double amount2 = 0.28060730580133125;
    public static final double amount3 = 0.09989148404308008;
    public static final double r = 0.28623662093593094;
    public static final double amount4 = 1.9561839658913562;
    public static final double amount5 = 0.587619839258055;
    public static final double amount6 = 2.016843005849186;
    public static final double amount7 = 0.98F;
    public static final double w = 0.41999998688698;
    public static final double amount8 = -0.0784000015258789;
    public static final double amount9 = -0.09800000190735147;
    public static final double amount10 = 0.221;
    public static final double amount11 = 1.3;
    public static final double amount12 = 0.3;
    public static final double amount13 = 0.2;
    public static final double amount14 = 1.0E-5;
    public static final double amount15 = 0.0626;
    public static final double amount16 = 0.8F;
    public static final double amount17 = 0.5;
    private static final HashMap<Integer, Integer> lookupMap = new HashMap<Integer, Integer>() {
        {
            this.put(6, 1);
            this.put(10, 2);
            this.put(11, 3);
            this.put(21, 4);
            this.put(22, 5);
            this.put(23, 6);
            this.put(5, 7);
            this.put(1, 8);
            this.put(12, 9);
            this.put(14, 10);
            this.put(3, 11);
            this.put(13, 12);
        }
    };
    private static final long timestamp = 4597087394516828057L;

    public static double computeDouble2() {
        double d = 3.1;
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            d += amplifier + 1;
        }

        return d;
    }

    public static ItemStack computeItemStack3() {
        int size = mc.thePlayer.inventoryContainer.getInventory().size();
        ItemStack stack = null;

        for (int i = 0; i < size; i++) {
            ItemStack stack2 = mc.thePlayer.inventoryContainer.getInventory().get(i);
            if (stack2 != null && stack2.getItem() instanceof ItemAxe) {
                if (stack == null) {
                    stack = stack2;
                } else if (matchesItemStack(stack2, stack, Blocks.planks)) {
                    stack = stack2;
                }
            }
        }

        return stack;
    }

    public static int computeInt(EntityLivingBase living) {
        return living.getDisplayName().equals(mc.thePlayer.getDisplayName()) ? new Color((int)timestamp).getRGB() : new Color(-1).getRGB();
    }

    public static boolean checkState() {
        boolean flag = false;
        AxisAlignedBB entityBoundingBox = mc.thePlayer.getEntityBoundingBox();
        double minY = (int)entityBoundingBox.offset(0.0, -0.01, 0.0).minY;

        for (double i = MathHelper.floor_double(entityBoundingBox.minX); i < MathHelper.floor_double(entityBoundingBox.maxX) + 1; i++) {
            for (double j = MathHelper.floor_double(entityBoundingBox.minZ); j < MathHelper.floor_double(entityBoundingBox.maxZ) + 1; j++) {
                Block block = mc.theWorld.getBlockState(new BlockPos(i, minY, j)).getBlock();
                if (block != null && !(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }

                    flag = true;
                }
            }
        }

        return flag;
    }

    public static Block computeBlock(double d, double d2, double d3) {
        return mc.theWorld.getBlockState(new BlockPos(d, d2, d3)).getBlock();
    }

    public static boolean matchesDouble(double d, double d2, double d3) {
        for (double i = d2; i > 0.0; i--) {
            if (!(mc.theWorld.getBlockState(new BlockPos(d, i, d3)).getBlock() instanceof BlockAir)) {
                return false;
            }
        }

        return true;
    }

    public static double onIsSafewalkEvent(IsSafewalkEvent isSafewalkEvent) {
        return Math.hypot(isSafewalkEvent.getX(), isSafewalkEvent.getZ());
    }

    public static boolean matchesItemStack(ItemStack stack, ItemStack stack2, Block block) {
        return computeFloat(stack, block) > computeFloat(stack2, block);
    }

    public static boolean checkState3() {
        return matchesDouble(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }

    public static String computeString(EntityPlayer player) {
        return player.getCommandSenderName();
    }

    public static ItemStack computeItemStack2() {
        int size = mc.thePlayer.inventoryContainer.getInventory().size();
        ItemStack stack = null;

        for (int i = 0; i < size; i++) {
            ItemStack stack2 = mc.thePlayer.inventoryContainer.getInventory().get(i);
            if (stack2 != null && stack2.getItem() instanceof ItemSword) {
                if (stack == null) {
                    stack = stack2;
                } else if (matchesItemStack2(stack2, stack)) {
                    stack = stack2;
                }
            }
        }

        return stack;
    }

    public static double computeDouble(double d) {
        return mc.thePlayer.isPotionActive(Potion.jump) ? d + (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1 : d;
    }

    public static boolean matchesInt2(int n) {
        return lookupMap.containsKey(n);
    }

    public static void process() {
        for (int i = 36; i < 45; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && Item.getIdFromItem(stack.getItem()) == 346) {
                mc.thePlayer.inventory.currentItem = i - 36;
                break;
            }
        }
    }

    public static double computeDouble() {
        return computeDouble(0.41999998688698);
    }

    public static void handleLong(long l) {
        try {
            TimeUnit.MILLISECONDS.sleep(l);
        } catch (Exception ignored) {
        }
    }

    private static boolean matchesItemStack(ItemStack stack, ItemStack stack2) {
        int n = stack.getMaxDamage() - stack.getItemDamage();
        int n2 = stack2.getMaxDamage() - stack2.getItemDamage();
        return n > n2;
    }

    public int computeInt(int n) {
        return lookupMap.getOrDefault(n, -1);
    }

    public static double computeDouble3() {
        double d = 0.2873;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            double amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            d *= 1.0 + 0.2 * (amplifier + 1.0);
        }

        return d;
    }

    public static int computeInt(Entity entity) {
        Minecraft mc = Minecraft.getMinecraft();
        int n = mc.thePlayer.inventory.currentItem = 0;
        int n2 = -1;
        int n3 = 1;

        for (int i = 0; i < 9; i++) {
            mc.thePlayer.inventory.currentItem = i;
            ItemStack heldItem = mc.thePlayer.getHeldItem();
            if (heldItem != null) {
                int n4 = (int)computeFloat(heldItem);
                int n5 = (int)(n4 + EnchantmentHelper.func_152377_a(heldItem, EnumCreatureAttribute.UNDEFINED));
                if (n5 > n3) {
                    n3 = n5;
                    n2 = i;
                }
            }
        }

        return n2 != -1 ? n2 : n;
    }

    public static boolean matchesInt(int n) {
        for (int i = (int)(mc.thePlayer.posY - n); i > 0; i--) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, (double)i, mc.thePlayer.posZ);
            if (!(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir)) {
                return true;
            }
        }

        return false;
    }

    public static ItemStack computeItemStack() {
        ItemStack stack = null;

        for (int i = 0; i < mc.thePlayer.inventory.getSizeInventory(); i++) {
            ItemStack stack2 = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack2 != null && stack2.getItem() instanceof ItemPickaxe && (stack == null || matchesItemStack(stack2, stack))) {
                stack = stack2;
            }
        }

        return stack;
    }

    public static String computeString() {
        String str = "Main Menu";
        if (mc.isIntegratedServerRunning()) {
            str = "SinglePlayer";
        } else if (mc.theWorld != null && mc.theWorld.isRemote) {
            ServerData serverData = mc.getCurrentServerData();
            if (serverData != null) {
                str = serverData.serverIP;
            }
        }

        return str;
    }

    public static int computeInt(int n, int n2) {
        for (int i = n; i < n2; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() == Items.fishing_rod) {
                return i;
            }
        }

        return -1;
    }

    public static float computeFloat(ItemStack stack) {
        Multimap attributeModifiers = stack.getAttributeModifiers();
        if (!attributeModifiers.isEmpty()) {
            Iterator it = attributeModifiers.entries().iterator();
            if (it.hasNext()) {
                Entry entry = (Entry)it.next();
                AttributeModifier val = (AttributeModifier)entry.getValue();
                double d = val.getOperation() != 1 && val.getOperation() != 2 ? val.getAmount() : val.getAmount() * 100.0;
                if (val.getAmount() > 1.0) {
                    return 1.0F + (float)d;
                }

                return 1.0F;
            }
        }

        return 1.0F;
    }

    public static double getSpeed() {
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    public static float computeFloat(ItemSword item, ItemStack stack) {
        float maxDamage = item.getMaxDamage();
        return maxDamage + EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F;
    }

    public static float computeFloat(ItemStack stack, Block block) {
        float strVsBlock = stack.getStrVsBlock(block);
        if (strVsBlock > 1.0F) {
            int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);
            if (enchantmentLevel > 0) {
                strVsBlock += enchantmentLevel * enchantmentLevel + 1;
            }
        }

        return strVsBlock;
    }

    public static int computeInt(Block block) {
        int currentItem = mc.thePlayer.inventory.currentItem;

        for (int i = 36; i < 45; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null
                && mc.thePlayer.inventoryContainer.getSlot(currentItem).getStack() != null
                && matchesItemStack(stack, mc.thePlayer.inventoryContainer.getSlot(currentItem).getStack(), block)) {
                currentItem = i;
            }
        }

        return currentItem;
    }

    public static boolean matchesDouble(double d) {
        for (int i = (int)mc.thePlayer.posY; i >= 0; i--) {
            if (!(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, (double)i, mc.thePlayer.posZ)).getBlock() instanceof BlockAir)) {
                return true;
            }
        }

        return false;
    }

    public static Block computeBlock2(double d, double d2, double d3) {
        return computeBlock(mc.thePlayer.posX + d, mc.thePlayer.posY + d2, mc.thePlayer.posZ + d3);
    }

    public static boolean matchesItemStack2(ItemStack stack, ItemStack stack2) {
        return computeFloat((ItemSword)stack.getItem(), stack) > computeFloat((ItemSword)stack2.getItem(), stack2);
    }

    public String name() {
        return mc.thePlayer.getCommandSenderName();
    }

    public static void process2() {
        mc.thePlayer.inventory.currentItem = computeInt((Entity)mc.thePlayer);
    }

    public static boolean matchesDouble2(double d, double d2, double d3) {
        return GetBlockStateHelper.matchesBlockPos(new BlockPos(d, d2 - 1.0, d3));
    }

    public static boolean matchesEntityPlayer(EntityPlayer player) {
        return player.getTeam() != null && mc.thePlayer.getTeam() != null
            ? player.getDisplayName().getFormattedText().charAt(1) == mc.thePlayer.getDisplayName().getFormattedText().charAt(1)
            : false;
    }

    public static boolean checkState2() {
        return matchesDouble2(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }
}
