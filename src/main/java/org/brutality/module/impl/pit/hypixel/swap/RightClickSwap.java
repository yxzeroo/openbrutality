package org.brutality.module.impl.pit.hypixel.swap;

import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.BasicEvent3;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.player.StartHotbarMovesHelper;

public class RightClickSwap extends Module {
    private final PitCategory pitCategory = PitCategory.SWAP;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final ModeSetting silentSetting = new ModeSetting("Mode", this, "Normal", Arrays.asList("Normal", "Silent"));
    private final BooleanSetting onlyPantsSetting = new BooleanSetting("Only Pants", this, true);
    private final BooleanSetting safeModeSetting = new BooleanSetting("Safe Mode", this, true);
    private final BooleanSetting playSoundSetting = new BooleanSetting("Play Sound", this, true);
    private final BooleanSetting sendMessageSetting = new BooleanSetting("Send Message", this, true);
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 100.0, 1.0, 500.0, 50.0);

    public RightClickSwap() {
        super("RightClickSwap", 0, Category.PIT);
        this.handleSettingArray(
            new Setting[]{this.silentSetting, this.onlyPantsSetting, this.safeModeSetting, this.playSoundSetting, this.sendMessageSetting, this.delaySetting}
        );
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            StartHotbarMovesHelper.onEvent3(event);
            if (!StartHotbarMovesHelper.flag) {
                if (event instanceof BasicEvent3 basicEvent3) {
                    if (basicEvent3.count6 == 1 && basicEvent3.flag) {
                        if (mc.thePlayer != null && mc.theWorld != null && mc.currentScreen == null) {
                            ItemStack heldItem = mc.thePlayer.getHeldItem();
                            if (heldItem != null && heldItem.getItem() instanceof ItemArmor) {
                                ItemArmor item = (ItemArmor)heldItem.getItem();
                                int armorType = item.armorType;
                                if (!this.onlyPantsSetting.isEnabled() || armorType == 2) {
                                    int n = 3 - armorType;
                                    ItemStack stack = mc.thePlayer.inventory.armorInventory[n];
                                    if (stack != null) {
                                        event.cancel();
                                        int currentItem = mc.thePlayer.inventory.currentItem;
                                        int n2 = 36 + currentItem;
                                        ArrayList list = new ArrayList();
                                        list.add(new StartHotbarMovesHelper.Entry3(armorType, n2));
                                        if (this.silentSetting.matchesString2("Silent")) {
                                            StartHotbarMovesHelper.startSwap(
                                                list,
                                                this.safeModeSetting.isEnabled(),
                                                (long)this.delaySetting.computeDouble(),
                                                this.playSoundSetting.isEnabled(),
                                                this.sendMessageSetting.isEnabled(),
                                                null
                                            );
                                        } else {
                                            StartHotbarMovesHelper.handleList(
                                                list,
                                                this.safeModeSetting.isEnabled(),
                                                (long)this.delaySetting.computeDouble(),
                                                this.playSoundSetting.isEnabled(),
                                                this.sendMessageSetting.isEnabled(),
                                                null
                                            );
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
