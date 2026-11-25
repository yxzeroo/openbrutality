package org.brutality.module.impl.player;

import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;

public class AutoHotbar extends Module {
    private final BooleanSetting autoSortSetting = new BooleanSetting("Auto Sort", this, true);
    private long timestamp = 0L;
    private static final long timestamp2 = 500L;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (mc.thePlayer != null) {
                if (event instanceof EventUpdate) {
                    if (this.autoSortSetting.isEnabled()) {
                        long now = System.currentTimeMillis();
                        if (now - this.timestamp > timestamp2) {
                            this.timestamp = now;
                            this.process();
                        }
                    }
                }
            }
        }
    }

    private void process() {
        ItemStack[] mainInventory = mc.thePlayer.inventory.mainInventory;
        int[] ints = new int[9];

        for (int i = 0; i < 9; i++) {
            ints[i] = mainInventory[i] == null ? 99 : this.computeInt(mainInventory[i]);
        }

        for (int j = 0; j < 9; j++) {
            for (int k = j + 1; k < 9; k++) {
                if (ints[k] < ints[j]) {
                    ItemStack stack = mainInventory[j];
                    mainInventory[j] = mainInventory[k];
                    mainInventory[k] = stack;
                    int n = ints[j];
                    ints[j] = ints[k];
                    ints[k] = n;
                }
            }
        }
    }

    public AutoHotbar() {
        super("AutoHotbar", 0, Category.PLAYER);
        this.handleSettingArray(new Setting[]{this.autoSortSetting});
    }

    private int computeInt(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ItemSword) {
            return 0;
        }
        if (item instanceof ItemBlock) {
            return 1;
        }
        if (item instanceof ItemPickaxe || item instanceof ItemAxe || item instanceof ItemSpade) {
            return 2;
        }
        if (item instanceof ItemBow) {
            return 3;
        }
        if (item instanceof ItemAppleGold) {
            return 4;
        }
        if (item instanceof ItemEnderPearl) {
            return 5;
        }
        if (item instanceof ItemPotion) {
            return 6;
        }
        return item instanceof ItemFood ? 7 : 8;
    }
}
