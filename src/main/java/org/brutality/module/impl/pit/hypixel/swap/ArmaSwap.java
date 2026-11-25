package org.brutality.module.impl.pit.hypixel.swap;

import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.player.FindIdentityHelper;
import org.brutality.utils.player.StartHotbarMovesHelper;

public class ArmaSwap extends Module {
    private final PitCategory pitCategory = PitCategory.SWAP;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final ModeSetting silentSetting = new ModeSetting("Mode", this, "Normal", Arrays.asList("Normal", "Silent"));
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 50.0, 1.0, 500.0, 50.0);
    private final BooleanSetting safeModeSetting = new BooleanSetting("Safe Mode", this, true);

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.thePlayer == null || mc.theWorld == null) {
            this.process28();
        } else if (StartHotbarMovesHelper.flag) {
            this.process28();
        } else {
            ItemStack currentArmor = mc.thePlayer.getCurrentArmor(0);
            ItemArmor item = currentArmor != null && currentArmor.getItem() == Items.diamond_boots ? Items.leather_boots : Items.diamond_boots;
            int n = FindIdentityHelper.computeInt(stack -> stack.getItem() == item, true);
            if (n == -1) {
                mc.thePlayer.playSound("mob.villager.no", 1.0F, 0.7F);
                this.process28();
            } else {
                ArrayList list = new ArrayList();
                list.add(new StartHotbarMovesHelper.Entry3(3, n));
                if (this.silentSetting.matchesString2("Silent")) {
                    StartHotbarMovesHelper.startSwap(
                        list, this.safeModeSetting.isEnabled(), (long)this.delaySetting.computeDouble(), true, true, this::process28
                    );
                } else {
                    StartHotbarMovesHelper.handleList(
                        list, this.safeModeSetting.isEnabled(), (long)this.delaySetting.computeDouble(), true, true, this::process28
                    );
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            StartHotbarMovesHelper.onEvent3(event);
        }
    }

    public ArmaSwap() {
        super("ArmaSwap", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.silentSetting, this.delaySetting, this.safeModeSetting});
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
