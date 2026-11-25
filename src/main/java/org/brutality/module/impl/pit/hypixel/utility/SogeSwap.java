package org.brutality.module.impl.pit.hypixel.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.attack.AttackEvent;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.pit.hypixel.automation.AutoGrinder;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.player.PlayerHelper12;
import org.lwjgl.input.Mouse;

public class SogeSwap extends Module {
    private final PitCategory pitCategory = PitCategory.SWAP;
    private final NumberSetting delaySetting;
    private final BooleanSetting holdLeftClickSetting;
    private final BooleanSetting onlyOnHitSetting;
    private long timestamp;
    private long timestamp2;
    private int count = -1;
    private int count2 = -1;
    private boolean flag = false;

    private boolean matchesString(String str) {
        return str.contains("Tier I") || str.contains("Tier II") || str.contains("Tier III");
    }

    private void process() {
        this.count = -1;
        this.count2 = -1;

        for (int i = 0; i < 9; i++) {
            if (Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(i) != null) {
                ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(i);
                String displayName = stack.getDisplayName();
                if (this.matchesItemStack(stack)) {
                    this.count = i;
                }

                if (this.matchesString(displayName)) {
                    this.count2 = i;
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                if (Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().theWorld == null) {
                    return;
                }

                if (this.onlyOnHitSetting.isEnabled() && !this.flag) {
                    return;
                }

                if (System.currentTimeMillis() - this.timestamp >= 1000L) {
                    this.process();
                    this.timestamp = System.currentTimeMillis();
                }

                if (System.currentTimeMillis() - this.timestamp2 >= this.delaySetting.computeDouble()) {
                    if (this.holdLeftClickSetting.isEnabled()
                        && (
                            (!Mouse.isCreated() || !Mouse.isButtonDown(0)) && !AutoGrinder.checkState15()
                                || Minecraft.getMinecraft().currentScreen instanceof GuiContainer
                        )) {
                        return;
                    }

                    if (Minecraft.getMinecraft().thePlayer.getHeldItem() != null) {
                        ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();
                        if (this.matchesItemStack(heldItem) || this.matchesString(heldItem.getDisplayName())) {
                            this.process3();
                        }
                    }

                    this.timestamp2 = System.currentTimeMillis();
                    this.flag = false;
                }
            }

            if (event instanceof AttackEvent attackEvent && attackEvent.getEntityPlayer() == Minecraft.getMinecraft().thePlayer) {
                this.flag = true;
            }
        }
    }

    private boolean matchesItemStack(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemSword) {
            String displayName = stack.getDisplayName();
            return displayName.contains("Diamond Sword") || displayName.contains("Mystic Sword");
        }
        return false;
    }

    @Override
    public void onEnable() {
        this.timestamp = 0L;
        this.timestamp2 = 0L;
        this.count = -1;
        this.count2 = -1;
        this.flag = false;
        this.process();
    }

    public SogeSwap() {
        super("SogeSwap", 0, Category.PIT);
        this.delaySetting = new NumberSetting("Delay", this, 50.0, 0.0, 100.0, 50.0);
        this.holdLeftClickSetting = new BooleanSetting("Hold Left Click", this, true);
        this.onlyOnHitSetting = new BooleanSetting("Only On Hit", this, false);
        this.handleSettingArray(new Setting[]{this.delaySetting, this.holdLeftClickSetting, this.onlyOnHitSetting});
    }

    private void process3() {
        int n = 0;
        if (this.count != -1) {
            n++;
        }

        if (this.count2 != -1) {
            n++;
        }

        if (n >= 2) {
            ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();
            if (this.matchesItemStack(heldItem)) {
                PlayerHelper12.matchesObject(this, this.count2, 1500L);
            } else if (this.matchesString(heldItem.getDisplayName())) {
                PlayerHelper12.matchesObject(this, this.count, 1500L);
            }
        }

        this.timestamp2 = System.currentTimeMillis();
    }
}
