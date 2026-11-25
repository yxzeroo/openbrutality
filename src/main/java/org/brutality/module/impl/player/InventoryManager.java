package org.brutality.module.impl.player;

import java.util.Locale;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBoat;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;

public class InventoryManager extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting startDelaySetting = new NumberSetting("Start Delay", this, 1.0, 0.0, 10.0, 1.0);
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 1.0, 0.0, 10.0, 1.0);
    private final NumberSetting swordSlotSetting = new NumberSetting("Sword Slot", this, 1.0, 1.0, 9.0, 1.0);
    private final NumberSetting blockSlotSetting = new NumberSetting("Block Slot", this, 2.0, 1.0, 9.0, 1.0);
    private final NumberSetting gappleSlotSetting = new NumberSetting("Gapple Slot", this, 3.0, 1.0, 9.0, 1.0);
    private final BooleanSetting dropTrashSetting = new BooleanSetting("Drop Trash", this, true);
    private int count;
    private int count2;
    private int count3 = -1;

    private void vx() {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && this.matchesItemStack3(mc.thePlayer.inventoryContainer.getSlot(i).getStack())
                )
             {
                this.handleInt(i);
            }
        }
    }

    private boolean matchesItemStack(ItemStack stack, int n) {
        double d = this.computeDouble(stack);
        String str = "";
        if (n == 1) {
            str = "helmet";
        } else if (n == 2) {
            str = "chestplate";
        } else if (n == 3) {
            str = "leggings";
        } else if (n == 4) {
            str = "boots";
        }

        if (!stack.getUnlocalizedName().contains(str)) {
            return false;
        }
        for (int i = 5; i < 45; i++) {
            ItemStack stack2;
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
                && this.computeDouble(stack2 = mc.thePlayer.inventoryContainer.getSlot(i).getStack()) > d
                && stack2.getUnlocalizedName().contains(str)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void vy() {
        int n = (int)this.gappleSlotSetting.computeDouble();
        int n2 = n - 1;
        int n3 = n + 35;
        Slot slot = mc.thePlayer.inventoryContainer.getSlot(n3);
        if (!slot.getHasStack() || !(slot.getStack().getItem() instanceof ItemAppleGold)) {
            for (int i = 9; i < 45; i++) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
                    && mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemAppleGold) {
                    if (i != n3) {
                        this.swap(i, n2);
                    }
                    break;
                }
            }
        }
    }

    public InventoryManager() {
        super("Inventory Manager", 0, Category.PLAYER);
        this.handleSettingArray(
            new Setting[]{
                this.startDelaySetting, this.delaySetting, this.swordSlotSetting, this.blockSlotSetting, this.gappleSlotSetting, this.dropTrashSetting
            }
        );
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate event2) {
                if (event2.getState() != EventState.PRE) {
                    return;
                }

                if (mc.currentScreen instanceof GuiInventory) {
                    this.count2++;
                    if (this.count2 < this.startDelaySetting.computeDouble()) {
                        return;
                    }

                    if (!mc.thePlayer.inventoryContainer.getSlot(36).getHasStack()) {
                        this.handleInt5((int)this.swordSlotSetting.computeDouble());
                    } else if (!this.matchesItemStack4(mc.thePlayer.inventoryContainer.getSlot(36).getStack())) {
                        this.handleInt5((int)this.swordSlotSetting.computeDouble());
                    }

                    this.vz();
                    this.process();
                    if (this.dropTrashSetting.isEnabled()) {
                        this.vx();
                    }

                    this.vy();
                    this.handleInt3((int)this.blockSlotSetting.computeDouble());
                    this.count++;
                } else {
                    this.count2 = 0;
                    this.count3 = -1;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.count = 0;
        this.count2 = 0;
        this.count3 = -1;
    }

    private void handleInt3(int n) {
        if (n != (int)this.swordSlotSetting.computeDouble() && n != (int)this.gappleSlotSetting.computeDouble()) {
            int n2 = n + 35;
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(n2).getStack();
            if (stack != null && this.matchesItemStack2(stack) && stack.stackSize >= 64) {
                this.count3 = n2;
            } else {
                if (stack == null || !this.matchesItemStack2(stack)) {
                    int n3 = -1;
                    int n4 = -1;

                    for (int i = 9; i < 36; i++) {
                        Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
                        if (slot.getHasStack()) {
                            ItemStack stack2 = slot.getStack();
                            if (this.matchesItemStack2(stack2) && stack2.stackSize > n4) {
                                n3 = i;
                                n4 = stack2.stackSize;
                            }
                        }
                    }

                    if (n3 != -1) {
                        this.swap(n3, n - 1);
                        this.count3 = n2;
                    }
                } else if (stack.stackSize < 64) {
                    for (int j = 9; j < 36; j++) {
                        Slot slot2 = mc.thePlayer.inventoryContainer.getSlot(j);
                        if (slot2.getHasStack()) {
                            ItemStack stack3 = slot2.getStack();
                            if (this.matchesItemStack2(stack3) && stack3.getItem() == stack.getItem() && stack3.getMetadata() == stack.getMetadata()) {
                                if (this.count >= this.delaySetting.computeDouble()) {
                                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, j, 0, 0, mc.thePlayer);
                                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, n2, 0, 0, mc.thePlayer);
                                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, j, 0, 0, mc.thePlayer);
                                    this.count = 0;
                                }
                                break;
                            }
                        }
                    }

                    this.count3 = n2;
                }
            }
        }
    }

    private void handleInt(int n) {
        if (this.count >= this.delaySetting.computeDouble()) {
            if (n < 0 || n >= mc.thePlayer.inventoryContainer.inventorySlots.size()) {
                return;
            }

            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(n).getStack();
            if (this.matchesItemStack(stack)) {
                return;
            }

            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, n, 1, 4, mc.thePlayer);
            this.count = 0;
        }
    }

    private void handleInt5(int n) {
        ItemStack stack = null;
        int n2 = -1;

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack stack2 = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (stack2.getItem() instanceof ItemSword && this.matchesItemStack4(stack2) && this.computeFloat(stack2) > 0.0F) {
                    stack = stack2;
                    n2 = i;
                    break;
                }
            }
        }

        if (stack != null && n2 != -1) {
            this.swap(n2, n - 1);
        }
    }

    private boolean matchesItemStack4(ItemStack stack) {
        float f = this.computeFloat(stack);

        for (int i = 9; i < 45; i++) {
            ItemStack stack2;
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
                && this.computeFloat(stack2 = mc.thePlayer.inventoryContainer.getSlot(i).getStack()) > f
                && stack2.getItem() instanceof ItemSword) {
                return false;
            }
        }

        return stack.getItem() instanceof ItemSword;
    }

    private void vz() {
        for (int i = 1; i < 5; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(4 + i).getHasStack()) {
                ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(4 + i).getStack();
                if (this.matchesItemStack(stack, i)) {
                    continue;
                }

                this.handleInt(4 + i);
            }

            for (int j = 9; j < 45; j++) {
                if (mc.thePlayer.inventoryContainer.getSlot(j).getHasStack()) {
                    ItemStack stack2 = mc.thePlayer.inventoryContainer.getSlot(j).getStack();
                    if (this.matchesItemStack(stack2, i)) {
                        this.handleInt2(j);
                    }
                }
            }
        }
    }

    private boolean matchesItemStack2(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock)stack.getItem()).getBlock();
            if (block.isFullBlock() && block != Blocks.sand && block != Blocks.gravel) {
                return true;
            }
        }

        return false;
    }

    private float computeFloat(ItemStack stack) {
        float f = 0.0F;
        if (stack.getItem() instanceof ItemSword item) {
            f += item.getMaxDamage();
        }

        float f2 = f
            + (
                EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F
                    + EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 2.0F
            );
        if (stack.getItemDamage() * 3 > stack.getMaxItemUseDuration()) {
            f2 -= 3.0F;
        }

        return f2;
    }

    private int computeInt(ItemArmor item) {
        return item.getArmorMaterial().getDamageReductionAmount(item.armorType);
    }

    private boolean matchesItemStack3(ItemStack stack) {
        if (this.matchesItemStack(stack)) {
            return false;
        }
        String str = stack.getUnlocalizedName().toLowerCase(Locale.ROOT);
        return str.contains("diamond")
            || str.contains("web")
            || str.contains("work")
            || str.contains("blaze")
            || str.contains("chest")
            || str.contains("seeds")
            || str.contains("tnt")
            || str.contains("cobblestone")
            || str.contains("spawn")
            || str.contains("cobweb")
            || str.contains("piston")
            || str.contains("redstone")
            || str.contains("egg")
            || str.contains("flint")
            || str.contains("snow")
            || str.contains("axe")
            || str.contains("pickaxe")
            || str.contains("shovel")
            || str.contains("fish")
            || str.contains("string")
            || str.contains("stick")
            || str.contains("bucket")
            || stack.getItem() instanceof ItemExpBottle
            || stack.getItem() instanceof ItemAxe;
    }

    private boolean matchesItemStack(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (!(stack.getItem() instanceof ItemSword) && !(stack.getItem() instanceof ItemBoat)) {
            String str = stack.getUnlocalizedName().toLowerCase(Locale.ROOT);
            return str.contains("boat") || str.contains("workbench") || str.contains("crafting") || str.contains("wood") || str.contains("oak");
        }
        return true;
    }

    private double computeDouble(ItemStack stack) {
        double d = 0.0;
        if (stack.getItem() instanceof ItemArmor) {
            ItemArmor item = (ItemArmor)stack.getItem();
            d += item.damageReduceAmount
                + (100 - item.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.0075;
            d += EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100.0;
            d += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100.0;
            d += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100.0;
            d += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50.0;
        }

        return d;
    }

    private void handleInt2(int n) {
        if (this.count >= this.delaySetting.computeDouble()) {
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, n, 0, 1, mc.thePlayer);
            this.count = 0;
        }
    }

    private void process() {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (stack.getUnlocalizedName().contains("helmet") && !this.matchesItemStack(stack, 1)) {
                    this.handleInt(i);
                } else if (stack.getUnlocalizedName().contains("chestplate") && !this.matchesItemStack(stack, 2)) {
                    this.handleInt(i);
                } else if (stack.getUnlocalizedName().contains("leggings") && !this.matchesItemStack(stack, 3)) {
                    this.handleInt(i);
                } else if (stack.getUnlocalizedName().contains("boots") && !this.matchesItemStack(stack, 4)) {
                    this.handleInt(i);
                }
            }
        }
    }

    public void swap(int n, int n2) {
        if (this.count >= this.delaySetting.computeDouble()) {
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, n, n2, 2, mc.thePlayer);
            this.count = 0;
        }
    }
}
