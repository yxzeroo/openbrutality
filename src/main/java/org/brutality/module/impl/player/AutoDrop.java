package org.brutality.module.impl.player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;

public class AutoDrop extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 0.0, 0.0, 10.0, 1.0);
    private final BooleanSetting ironSetting = new BooleanSetting("Iron", this, true);
    private final BooleanSetting goldSetting = new BooleanSetting("Gold", this, true);
    private final BooleanSetting diamondsSetting = new BooleanSetting("Diamonds", this, true);
    private final BooleanSetting emeraldsSetting = new BooleanSetting("Emeralds", this, true);
    private int count = 0;
    private int count2 = 0;

    private Map<Integer, ItemStack> computeMap() {
        HashMap map = new HashMap();
        int n = 0;
        if (mc.currentScreen instanceof GuiInventory) {
            for (int i = 0; i < 5; i++) {
                map.put(n++, null);
            }

            for (int j = 3; j >= 0; j--) {
                map.put(n++, mc.thePlayer.getCurrentArmor(j));
            }
        } else if (mc.currentScreen instanceof GuiChest gui) {
            int n2 = gui.inventorySlots.inventorySlots.size() - 36;

            for (int k = 0; k < n2; k++) {
                map.put(n++, gui.inventorySlots.getSlot(k).getStack());
            }
        }

        for (int idx = 9; idx < 36; idx++) {
            map.put(n++, mc.thePlayer.inventory.getStackInSlot(idx));
        }

        for (int idx2 = 0; idx2 < 9; idx2++) {
            map.put(n++, mc.thePlayer.inventory.getStackInSlot(idx2));
        }

        return map;
    }

    public AutoDrop() {
        super("Auto Drop", 0, Category.PLAYER);
        this.handleSettingArray(new Setting[]{this.delaySetting, this.ironSetting, this.goldSetting, this.diamondsSetting, this.emeraldsSetting});
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.count = 0;
        this.count2 = 0;
        if (mc.thePlayer != null) {
            mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate event2) {
                if (event2.getState() != EventState.PRE) {
                    return;
                }

                if (this.count++ >= 3) {
                    this.process28();
                    return;
                }

                if (!(mc.currentScreen instanceof GuiInventory)) {
                    return;
                }

                this.count = 0;
                if (--this.count2 > 0) {
                    return;
                }

                var map = this.computeMap();
                HashSet set = new HashSet();
                if (this.ironSetting.isEnabled()) {
                    set.add(Items.iron_ingot);
                }

                if (this.goldSetting.isEnabled()) {
                    set.add(Items.gold_ingot);
                }

                if (this.diamondsSetting.isEnabled()) {
                    set.add(Items.diamond);
                }

                if (this.emeraldsSetting.isEnabled()) {
                    set.add(Items.emerald);
                }

                for (Entry entry : map.entrySet()) {
                    ItemStack val = (ItemStack)entry.getValue();
                    if (val != null && set.contains(val.getItem())) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, (Integer)entry.getKey(), 1, 4, mc.thePlayer);
                        this.count2 = (int)this.delaySetting.computeDouble();
                        if (this.count2 > 0) {
                            return;
                        }
                    }
                }

                this.process28();
                mc.thePlayer.closeScreen();
            }
        }
    }
}
