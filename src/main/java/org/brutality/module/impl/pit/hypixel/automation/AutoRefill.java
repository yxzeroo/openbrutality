package org.brutality.module.impl.pit.hypixel.automation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.player.StartHotbarMovesHelper;

public class AutoRefill extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final BooleanSetting autoOpenInventorySetting = new BooleanSetting("Auto Open Inventory", this, false);
    private final BooleanSetting autoCloseInventorySetting = new BooleanSetting("Auto Close Inventory", this, true);
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 2.0, 0.0, 10.0, 1.0);
    private final NumberSetting obsidianThresholdSetting = new NumberSetting("Obsidian Threshold", this, 16.0, 1.0, 64.0, 1.0);
    private final BooleanSetting refillObsidianSetting = new BooleanSetting("Refill Obsidian", this, true);
    private final BooleanSetting refillJumpPotionSetting = new BooleanSetting("Refill Jump Potion", this, true);
    private final BooleanSetting limitObsidianSlotsSetting = new BooleanSetting("Limit Obsidian Slots", this, true);
    private final NumberSetting maxObsidianSlotsSetting = new NumberSetting("Max Obsidian Slots", this, 3.0, 1.0, 9.0, 1.0);
    private final BooleanSetting limitJumpPotionSlotsSetting = new BooleanSetting("Limit Jump Potion Slots", this, true);
    private final NumberSetting maxJumpPotionSlotsSetting = new NumberSetting("Max Jump Potion Slots", this, 3.0, 1.0, 9.0, 1.0);
    private final BooleanSetting smartRefillSetting = new BooleanSetting("Smart Refill", this, true);
    private final BooleanSetting disableOnManualInteractionSetting = new BooleanSetting("Disable on Manual Interaction", this, true);
    private final BooleanSetting autoToggleOffSetting = new BooleanSetting("Auto Toggle Off", this, false);
    private int count = 0;
    private boolean flag = false;
    private boolean flag2 = false;
    private final Map<Integer, AutoRefill.ObsidianMode> lookupMap = new HashMap<>();
    private boolean flag3 = false;
    private boolean flag4 = false;
    private final Set<Integer> itemSet = new HashSet<>();
    private int count2 = 0;
    private int count3 = 0;
    private final Set<Integer> itemSet2 = new HashSet<>();
    private ItemStack[] itemStackArray = new ItemStack[45];
    private boolean flag5 = false;

    private int findBestSourceSlot(AutoRefill.ObsidianMode obsidianMode, ItemStack stack) {
        List<Integer> integers = new ArrayList<>();

        for (int i = 9; i < 36; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            if (slot.getHasStack()) {
                ItemStack stack2 = slot.getStack();
                switch (obsidianMode) {
                    case OBSIDIAN:
                        if (this.refillObsidianSetting.isEnabled() && stack2.getItem() instanceof ItemBlock) {
                            ItemBlock item = (ItemBlock)stack2.getItem();
                            if (item.getBlock() == Blocks.obsidian) {
                                integers.add(i);
                            }
                        }
                        break;
                    case JUMP_POTION:
                        if (this.refillJumpPotionSetting.isEnabled() && this.matchesItemStack(stack2)) {
                            integers.add(i);
                        }
                }
            }
        }

        if (integers.isEmpty()) {
            return -1;
        }
        if (obsidianMode == AutoRefill.ObsidianMode.OBSIDIAN) {
            integers.sort((integer, integer2) -> {
                ItemStack stack3 = mc.thePlayer.inventoryContainer.getSlot(integer).getStack();
                ItemStack stack4 = mc.thePlayer.inventoryContainer.getSlot(integer2).getStack();
                return Integer.compare(stack4.stackSize, stack3.stackSize);
            });
        }

        return (Integer)integers.get(0);
    }

    private void process() {
        HashMap map = new HashMap();

        for (int i = 36; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            if (!slot.getHasStack()) {
                if (this.lookupMap.containsKey(i) && this.lookupMap.get(i) != AutoRefill.ObsidianMode.EMPTY) {
                    map.put(i, this.lookupMap.get(i));
                }
            } else {
                ItemStack stack = slot.getStack();
                if (this.refillObsidianSetting.isEnabled() && stack.getItem() instanceof ItemBlock) {
                    ItemBlock item = (ItemBlock)stack.getItem();
                    if (item.getBlock() == Blocks.obsidian) {
                        map.put(i, AutoRefill.ObsidianMode.OBSIDIAN);
                        continue;
                    }
                }

                if (this.refillJumpPotionSetting.isEnabled() && this.matchesItemStack(stack)) {
                    map.put(i, AutoRefill.ObsidianMode.JUMP_POTION);
                } else if (this.lookupMap.containsKey(i) && this.lookupMap.get(i) != AutoRefill.ObsidianMode.EMPTY) {
                    map.put(i, this.lookupMap.get(i));
                }
            }
        }

        this.lookupMap.clear();
        this.lookupMap.putAll(map);
    }

    private boolean checkState2() {
        if (mc.thePlayer != null && mc.thePlayer.inventoryContainer != null) {
            if (this.count2 <= 0 && this.count3 <= 0) {
                if (!this.flag5) {
                    this.process3();
                    this.flag5 = true;
                    return false;
                }
                for (int i = 0; i < 45; i++) {
                    Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
                    ItemStack stack = slot.getHasStack() ? slot.getStack() : null;
                    ItemStack stack2 = this.itemStackArray[i];
                    if (!this.matchesItemStack(stack, stack2)) {
                        return true;
                    }
                }

                return false;
            }
            this.process3();
            return false;
        }
        return false;
    }

    public AutoRefill() {
        super("Auto Refill", 0, Category.PIT);
        this.handleSettingArray(
            new Setting[]{
                this.autoOpenInventorySetting,
                this.autoCloseInventorySetting,
                this.delaySetting,
                this.obsidianThresholdSetting,
                this.refillObsidianSetting,
                this.refillJumpPotionSetting,
                this.limitObsidianSlotsSetting,
                this.maxObsidianSlotsSetting,
                this.limitJumpPotionSlotsSetting,
                this.maxJumpPotionSlotsSetting,
                this.smartRefillSetting,
                this.disableOnManualInteractionSetting,
                this.autoToggleOffSetting
            }
        );
    }

    private String computeString(ItemStack stack) {
        if (stack.hasDisplayName()) {
            return stack.getDisplayName();
        }
        if (stack.getItem() instanceof ItemBlock) {
            ItemBlock item = (ItemBlock)stack.getItem();
            if (item.getBlock() == Blocks.obsidian) {
                return "Obsidian";
            }
        }

        return this.matchesItemStack(stack) ? "Jump Potion" : stack.getUnlocalizedName();
    }

    public void ok() {
        if (!this.lookupMap.isEmpty()) {
            MessageUtils.x("§aTracked slots:");

            for (Map.Entry entry : this.lookupMap.entrySet()) {
                int n = (Integer)entry.getKey() - 35;
                MessageUtils.x("§7Slot " + n + ": " + ((AutoRefill.ObsidianMode)entry.getValue()).name());
            }
        }
    }

    private void process4() {
        if (mc.thePlayer != null && mc.thePlayer.inventoryContainer != null) {
            if (this.autoOpenInventorySetting.isEnabled() && !(mc.currentScreen instanceof GuiInventory)) {
                mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
                this.flag = true;
            } else if (mc.currentScreen instanceof GuiInventory) {
                ArrayList<Integer> integers = new ArrayList<>();

                for (int i = 36; i < 45; i++) {
                    Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
                    if (!slot.getHasStack()) {
                        integers.add(i);
                    }
                }

                if (integers.isEmpty()) {
                    this.flag3 = true;
                    this.process();
                    MessageUtils.x("§aInitial fill complete - no empty slots");
                } else {
                    int n = this.computeInt(AutoRefill.ObsidianMode.OBSIDIAN);
                    int n2 = this.computeInt(AutoRefill.ObsidianMode.JUMP_POTION);
                    int n3 = 0;
                    int n4 = 0;

                    for (int j = 36; j < 45; j++) {
                        Slot slot2 = mc.thePlayer.inventoryContainer.getSlot(j);
                        if (slot2.getHasStack()) {
                            ItemStack stack = slot2.getStack();
                            if (stack.getItem() instanceof ItemBlock) {
                                ItemBlock item = (ItemBlock)stack.getItem();
                                if (item.getBlock() == Blocks.obsidian) {
                                    n3++;
                                }
                            }

                            if (this.matchesItemStack(stack)) {
                                n4++;
                            }
                        }
                    }

                    for (int n5 : integers) {
                        AutoRefill.ObsidianMode obsidianMode = null;
                        if (this.refillJumpPotionSetting.isEnabled()) {
                            if (this.limitJumpPotionSlotsSetting.isEnabled()) {
                                if (n4 < (int)this.maxJumpPotionSlotsSetting.computeDouble() && n2 > 0) {
                                    obsidianMode = AutoRefill.ObsidianMode.JUMP_POTION;
                                }
                            } else if (n2 > 0) {
                                obsidianMode = AutoRefill.ObsidianMode.JUMP_POTION;
                            }
                        }

                        if (obsidianMode == null && this.refillObsidianSetting.isEnabled()) {
                            if (this.limitObsidianSlotsSetting.isEnabled()) {
                                if (n3 < (int)this.maxObsidianSlotsSetting.computeDouble() && n > 0) {
                                    obsidianMode = AutoRefill.ObsidianMode.OBSIDIAN;
                                }
                            } else if (n > 0) {
                                obsidianMode = AutoRefill.ObsidianMode.OBSIDIAN;
                            }
                        }

                        if (obsidianMode != null) {
                            int n6 = this.computeInt2(obsidianMode);
                            if (n6 != -1) {
                                if (this.count >= this.delaySetting.computeDouble()) {
                                    MessageUtils.x("§aFilling empty slot " + (n5 - 35) + " with " + obsidianMode.name());
                                    this.swap(n6, n5);
                                    this.lookupMap.put(n5, obsidianMode);
                                    if (obsidianMode == AutoRefill.ObsidianMode.OBSIDIAN) {
                                        n3++;
                                        n--;
                                    } else if (obsidianMode == AutoRefill.ObsidianMode.JUMP_POTION) {
                                        n4++;
                                        n2--;
                                    }

                                    return;
                                }

                                return;
                            }
                        }
                    }

                    this.flag3 = true;
                    this.process();
                    MessageUtils.x("§aInitial fill complete");
                }
            }
        }
    }

    private void process3() {
        if (mc.thePlayer != null && mc.thePlayer.inventoryContainer != null) {
            for (int i = 0; i < 45; i++) {
                Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
                if (slot.getHasStack()) {
                    ItemStack stack = slot.getStack();
                    this.itemStackArray[i] = stack.copy();
                } else {
                    this.itemStackArray[i] = null;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.count = 0;
        this.flag = false;
        this.lookupMap.clear();
        this.flag3 = false;
        this.flag4 = false;
        this.itemSet.clear();
        this.count2 = 0;
        this.count3 = 0;
        this.itemSet2.clear();
        Arrays.fill(this.itemStackArray, null);
        this.flag5 = false;
        this.process();
    }

    private boolean matchesItemStack2(ItemStack stack) {
        return stack != null && stack.getItem() == Items.glass_bottle;
    }

    private void handleInt(int n) {
        if (this.count >= this.delaySetting.computeDouble()) {
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, n, 1, 4, mc.thePlayer);
            this.count = 0;
            this.process3();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.lookupMap.clear();
        this.flag3 = false;
        this.flag4 = false;
        this.itemSet.clear();
        this.count2 = 0;
        this.count3 = 0;
        this.itemSet2.clear();
        Arrays.fill(this.itemStackArray, null);
        this.flag5 = false;
        if (this.flag && mc.currentScreen instanceof GuiInventory) {
            mc.thePlayer.closeScreen();
            this.flag = false;
        }
    }

    private int computeInt(AutoRefill.ObsidianMode obsidianMode) {
        int n = 0;

        for (int i = 9; i < 36; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            if (slot.getHasStack()) {
                ItemStack stack = slot.getStack();
                if (obsidianMode == AutoRefill.ObsidianMode.OBSIDIAN && stack.getItem() instanceof ItemBlock) {
                    ItemBlock item = (ItemBlock)stack.getItem();
                    if (item.getBlock() == Blocks.obsidian) {
                        n++;
                    }
                } else if (obsidianMode == AutoRefill.ObsidianMode.JUMP_POTION && this.matchesItemStack(stack)) {
                    n++;
                }
            }
        }

        return n;
    }

    private boolean matchesItemStack(ItemStack stack, ItemStack stack2) {
        if (stack == null && stack2 == null) {
            return true;
        }
        return stack != null && stack2 != null
            ? stack.getItem() == stack2.getItem() && stack.stackSize == stack2.stackSize && stack.getItemDamage() == stack2.getItemDamage()
            : false;
    }

    private void on() {
        if (mc.thePlayer != null && mc.thePlayer.inventoryContainer != null) {
            if (this.autoOpenInventorySetting.isEnabled() && !(mc.currentScreen instanceof GuiInventory)) {
                mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
                this.flag = true;
            } else if (mc.currentScreen instanceof GuiInventory) {
                boolean flag = false;

                for (int i = 36; i < 45; i++) {
                    Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
                    if (slot.getHasStack()) {
                        ItemStack stack = slot.getStack();
                        if (this.matchesItemStack2(stack)) {
                            if (this.count >= this.delaySetting.computeDouble()) {
                                MessageUtils.x("§eDropping glass bottle from hotbar slot " + (i - 35));
                                this.handleInt(i);
                                flag = true;
                                return;
                            }

                            flag = true;
                            return;
                        }
                    }
                }

                for (int j = 9; j < 36; j++) {
                    Slot slot2 = mc.thePlayer.inventoryContainer.getSlot(j);
                    if (slot2.getHasStack()) {
                        ItemStack stack2 = slot2.getStack();
                        if (this.matchesItemStack2(stack2)) {
                            if (this.count >= this.delaySetting.computeDouble()) {
                                MessageUtils.x("§eDropping glass bottle from inventory slot " + j);
                                this.handleInt(j);
                                flag = true;
                                return;
                            }

                            flag = true;
                            return;
                        }
                    }
                }

                if (!flag) {
                    this.flag4 = true;
                    MessageUtils.x("§aFinished dropping glass bottles");
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate) {
                if (StartHotbarMovesHelper.flag) {
                    this.flag = false;
                    this.flag5 = false;
                } else {
                    EventUpdate event2 = (EventUpdate)event;
                    if (event2.getState() == EventState.PRE) {
                        if (mc.thePlayer != null
                            && mc.thePlayer.openContainer == mc.thePlayer.inventoryContainer
                            && (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory)) {
                            this.count++;
                            if (this.count2 > 0) {
                                this.count2--;
                                if (this.count2 == 0) {
                                    this.itemSet.clear();
                                }
                            }

                            if (this.count3 > 0) {
                                this.count3--;
                            }

                            if (!(mc.currentScreen instanceof GuiInventory)) {
                                if (this.autoToggleOffSetting.isEnabled() && this.flag2) {
                                    this.flag2 = false;
                                    this.flag = false;
                                    this.count3 = 0;
                                    this.process28();
                                    return;
                                }

                                this.flag2 = false;
                                this.flag = false;
                                this.count3 = 0;
                            } else {
                                this.flag2 = true;
                                if (this.disableOnManualInteractionSetting.isEnabled() && this.checkState2()) {
                                    MessageUtils.x("§eAuto Refill disabled due to manual inventory interaction");
                                    this.process28();
                                    return;
                                }
                            }

                            if (!this.flag4) {
                                this.on();
                            } else if (!this.flag3) {
                                this.process4();
                            } else {
                                if (this.count % 5 == 0) {
                                    this.process();
                                }

                                boolean flag = this.checkState();
                                if (flag
                                    || !this.autoToggleOffSetting.isEnabled()
                                    || this.flag && mc.currentScreen instanceof GuiInventory && this.autoCloseInventorySetting.isEnabled()) {
                                    if (flag && this.autoOpenInventorySetting.isEnabled() && !(mc.currentScreen instanceof GuiInventory)) {
                                        mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
                                        this.flag = true;
                                    } else {
                                        if (mc.currentScreen instanceof GuiInventory) {
                                            if (this.count2 == 0 && flag) {
                                                this.performRefill();
                                            } else if (!flag && this.autoCloseInventorySetting.isEnabled() && this.flag) {
                                                if (this.count3 == 0 && this.count2 == 0) {
                                                    this.count3 = 20;
                                                }

                                                if (this.count3 == 1) {
                                                    mc.thePlayer.closeScreen();
                                                    this.flag = false;
                                                    MessageUtils.x("§aAll items refilled, closing inventory");
                                                    this.count3 = 0;
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    MessageUtils.x("§aAuto Refill complete - nothing else to do");
                                    this.process28();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void performRefill() {
        if (mc.thePlayer != null && mc.thePlayer.inventoryContainer != null) {
            List list = new ArrayList();

            for (Map.Entry entry : new ArrayList<>(this.lookupMap.entrySet())) {
                int key = (Integer)entry.getKey();
                AutoRefill.ObsidianMode val = (AutoRefill.ObsidianMode)entry.getValue();
                if (!this.itemSet.contains(key) && val != AutoRefill.ObsidianMode.EMPTY) {
                    Slot slot = mc.thePlayer.inventoryContainer.getSlot(key);
                    ItemStack stack = slot.getHasStack() ? slot.getStack() : null;
                    boolean flag = false;
                    boolean flag2 = false;
                    byte b = 0;
                    if (stack == null) {
                        flag = true;
                        b = 2;
                    } else if (this.matchesItemStack2(stack)) {
                        flag = true;
                        flag2 = true;
                        b = 1;
                    } else if (val == AutoRefill.ObsidianMode.OBSIDIAN) {
                        if (stack.getItem() instanceof ItemBlock) {
                            ItemBlock item = (ItemBlock)stack.getItem();
                            if (item.getBlock() == Blocks.obsidian && stack.stackSize < this.obsidianThresholdSetting.computeDouble()) {
                                flag = true;
                                b = 3;
                            }
                        } else {
                            flag = true;
                            b = 2;
                        }
                    } else if (val == AutoRefill.ObsidianMode.JUMP_POTION && !this.matchesItemStack(stack)) {
                        flag = true;
                        b = 2;
                    }

                    if (flag) {
                        list.add(new AutoRefill.Entry(key, val, stack, flag2, b));
                    }
                }
            }

            ((List<AutoRefill.Entry>)list).sort(Comparator.comparingInt(entry2 -> entry2.priority));
            if (!list.isEmpty()) {
                AutoRefill.Entry entry3 = (AutoRefill.Entry)list.get(0);
                if (entry3.flag) {
                    if (this.count >= this.delaySetting.computeDouble()) {
                        MessageUtils.x("§eDropping glass bottle from slot " + (entry3.count - 35));
                        this.handleInt(entry3.count);
                        this.itemSet.add(entry3.count);
                        this.count2 = 5;
                    }

                    return;
                }

                int n = this.findBestSourceSlot(entry3.obsidianMode, entry3.itemStack);
                if (n != -1) {
                    ItemStack stack2 = mc.thePlayer.inventoryContainer.getSlot(n).getStack();
                    String str = this.computeString(stack2);
                    if (entry3.obsidianMode == AutoRefill.ObsidianMode.OBSIDIAN
                        && entry3.itemStack != null
                        && entry3.itemStack.getItem() instanceof ItemBlock
                        && stack2.getItem() instanceof ItemBlock) {
                        ItemBlock item2 = (ItemBlock)entry3.itemStack.getItem();
                        ItemBlock item3 = (ItemBlock)stack2.getItem();
                        if (item2.getBlock() == Blocks.obsidian && item3.getBlock() == Blocks.obsidian) {
                            MessageUtils.x("§aStacking obsidian in slot " + (entry3.count - 35));
                            this.handleInt(n, entry3.count);
                            this.itemSet.add(entry3.count);
                            this.itemSet2.remove(entry3.count);
                            this.count2 = 5;
                            return;
                        }
                    }

                    MessageUtils.x("§aRefilling slot " + (entry3.count - 35) + " with " + str);
                    this.swap(n, entry3.count);
                    this.itemSet.add(entry3.count);
                    this.itemSet2.remove(entry3.count);
                    this.count2 = 5;
                    return;
                }

                if (!this.itemSet2.contains(entry3.count)) {
                    MessageUtils.x("§cNo replacement " + entry3.obsidianMode.name() + " found for slot " + (entry3.count - 35));
                    this.itemSet2.add(entry3.count);
                }

                if (this.autoToggleOffSetting.isEnabled()
                    && this.computeInt(AutoRefill.ObsidianMode.OBSIDIAN) == 0
                    && this.computeInt(AutoRefill.ObsidianMode.JUMP_POTION) == 0) {
                    MessageUtils.x("§eAuto Refill disabled - no obsidian or jump potions left");
                    this.process28();
                    return;
                }
            }
        }
    }

    private void swap(int n, int n2) {
        if (this.count >= this.delaySetting.computeDouble()) {
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, n, n2 - 36, 2, mc.thePlayer);
            this.count = 0;
            this.process3();
        }
    }

    private boolean checkState() {
        if (!this.smartRefillSetting.isEnabled()) {
            return false;
        }
        for (Map.Entry entry : this.lookupMap.entrySet()) {
            int key = (Integer)entry.getKey();
            AutoRefill.ObsidianMode val = (AutoRefill.ObsidianMode)entry.getValue();
            if (val != AutoRefill.ObsidianMode.EMPTY) {
                Slot slot = mc.thePlayer.inventoryContainer.getSlot(key);
                if (!slot.getHasStack()) {
                    return true;
                }

                ItemStack stack = slot.getStack();
                if (this.matchesItemStack2(stack)) {
                    return true;
                }

                if (val == AutoRefill.ObsidianMode.OBSIDIAN) {
                    if (!(stack.getItem() instanceof ItemBlock)) {
                        return true;
                    }

                    ItemBlock item = (ItemBlock)stack.getItem();
                    if (item.getBlock() == Blocks.obsidian && stack.stackSize < this.obsidianThresholdSetting.computeDouble()) {
                        return true;
                    }
                }

                if (val == AutoRefill.ObsidianMode.JUMP_POTION && !this.matchesItemStack(stack)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void handleInt(int n, int n2) {
        if (this.count >= this.delaySetting.computeDouble()) {
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, n, 0, 1, mc.thePlayer);
            this.count = 0;
            this.process3();
        }
    }

    private boolean matchesItemStack(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            if (stack.hasDisplayName()) {
                String displayName = stack.getDisplayName();
                if (displayName.contains("Jump") && displayName.contains("IV")) {
                    return true;
                }

                if (displayName.contains("Potion of Jump")) {
                    return true;
                }
            }

            ItemPotion item = (ItemPotion)stack.getItem();
            var effects = item.getEffects(stack);
            if (effects == null) {
                return false;
            }
            for (PotionEffect effect : effects) {
                if (effect.getPotionID() == Potion.jump.id) {
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    private int computeInt2(AutoRefill.ObsidianMode obsidianMode) {
        List list = new ArrayList();

        for (int i = 9; i < 36; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            if (slot.getHasStack()) {
                ItemStack stack = slot.getStack();
                switch (obsidianMode) {
                    case OBSIDIAN:
                        if (this.refillObsidianSetting.isEnabled() && stack.getItem() instanceof ItemBlock) {
                            ItemBlock item = (ItemBlock)stack.getItem();
                            if (item.getBlock() == Blocks.obsidian) {
                                list.add(i);
                            }
                        }
                        break;
                    case JUMP_POTION:
                        if (this.refillJumpPotionSetting.isEnabled() && this.matchesItemStack(stack)) {
                            list.add(i);
                        }
                }
            }
        }

        if (list.isEmpty()) {
            return -1;
        }
        boolean flag = false;
        if (obsidianMode == AutoRefill.ObsidianMode.OBSIDIAN && !this.limitObsidianSlotsSetting.isEnabled()) {
            flag = true;
        } else if (obsidianMode == AutoRefill.ObsidianMode.JUMP_POTION && !this.limitJumpPotionSlotsSetting.isEnabled()) {
            flag = true;
        }

        if (flag && list.size() > 1) {
            Random random = new Random();
            return (Integer)list.get(random.nextInt(list.size()));
        }
        return (Integer)list.get(0);
    }

    public static class Entry {
        public int count;
        public AutoRefill.ObsidianMode obsidianMode;
        public ItemStack itemStack;
        public boolean flag;
        public int priority;

        public Entry(int count, AutoRefill.ObsidianMode obsidianMode, ItemStack itemStack, boolean flag, int priority) {
            this.count = count;
            this.obsidianMode = obsidianMode;
            this.itemStack = itemStack;
            this.flag = flag;
            this.priority = priority;
        }
    }

    public static enum ObsidianMode {
        OBSIDIAN,
        JUMP_POTION,
        EMPTY;

        private static AutoRefill.ObsidianMode[] computeObsidianModeArray() {
            return new AutoRefill.ObsidianMode[]{OBSIDIAN, JUMP_POTION, EMPTY};
        }
    }
}
