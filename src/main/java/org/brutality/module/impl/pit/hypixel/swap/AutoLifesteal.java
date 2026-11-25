package org.brutality.module.impl.pit.hypixel.swap;

import java.util.Locale;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.player.PlayerHelper12;

public class AutoLifesteal extends Module {
    private final PitCategory pitCategory = PitCategory.SWAP;
    private final NumberSetting healthThresholdSetting = new NumberSetting("Health Threshold", this, 10.0, 1.0, 20.0, 1.0);
    private static AutoLifesteal instance;
    private int count = -1;
    private Object object;
    private Object object2;
    private static final long timestamp = 250L;

    public AutoLifesteal() {
        super("Auto Lifesteal", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.healthThresholdSetting});
        instance = this;
    }

    private void process3() {
        if (this.object != mc.thePlayer || this.object2 != mc.theWorld) {
            this.count = -1;
            PlayerHelper12.handleObject(this);
            this.object = mc.thePlayer;
            this.object2 = mc.theWorld;
        }
    }

    private int computeInt(boolean flag) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.getItem() instanceof ItemSword) {
                String str = stack.hasTagCompound() ? stack.getTagCompound().toString().toLowerCase(Locale.ROOT) : "";
                String str2 = stack.getDisplayName() == null ? "" : stack.getDisplayName().toLowerCase(Locale.ROOT);
                if (flag
                    ? str.contains("lifesteal") || str2.contains("lifesteal")
                    : str.contains("combo heal") || str.contains("combo_heal") || str2.contains("combo heal")) {
                    return i;
                }
            }
        }

        return -1;
    }

    @Override
    public void onDisable() {
        this.process3();
        this.process();
        this.count = -1;
    }

    public static int computeInt() {
        if (instance != null
            && instance.checkState44()
            && mc.thePlayer != null
            && mc.theWorld != null
            && !mc.thePlayer.isDead
            && !(mc.thePlayer.getHealth() <= 0.0F)
            && !(mc.thePlayer.getHealth() > instance.healthThresholdSetting.computeDouble())) {
            int n = instance.computeInt(true);
            return n >= 0 ? n : instance.computeInt(false);
        }
        return -1;
    }

    public static boolean checkState() {
        int n = computeInt();
        if (n >= 0 && PlayerHelper12.checkState()) {
            instance.process3();
            if (instance.count < 0) {
                instance.count = mc.thePlayer.inventory.currentItem;
            }

            return PlayerHelper12.matchesObject(instance, n);
        }
        return false;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof RenderEvent4) {
            this.count = -1;
            PlayerHelper12.handleObject(this);
        } else if (this.checkState44() && event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
            this.process3();
            if (PlayerHelper12.checkState()) {
                if (computeInt() >= 0) {
                    checkState();
                } else {
                    this.process();
                }
            }
        }
    }

    private void process() {
        PlayerHelper12.handleObject(this);
        if (this.count >= 0 && PlayerHelper12.checkState()) {
            PlayerHelper12.matchesObject(null, this.count, timestamp);
            this.count = -1;
        }
    }
}
