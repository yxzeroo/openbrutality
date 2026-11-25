package org.brutality.module.impl.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.brutality.event.Event;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.lwjgl.input.Mouse;

public class RightClicker extends Module {
    private long timestamp;
    private long timestamp2;
    private double speed;
    private double amount;
    private final NumberSetting minSetting = new NumberSetting("Min", this, 30.0, 0.0, 100.0, 1.0);
    private final NumberSetting maxSetting = new NumberSetting("Max", this, 30.0, 0.0, 100.0, 1.0);

    private boolean checkState2() {
        return KillAura.instance != null && KillAura.instance.checkState44() && KillAura.entityLivingBase2 != null;
    }

    public RightClicker() {
        super("Right Clicker", 0, Category.COMBAT);
        this.handleSettingArray(new Setting[]{this.minSetting, this.maxSetting});
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventTick) {
                    if (mc.thePlayer == null || mc.theWorld == null) {
                        return;
                    }

                    if (this.checkState2()) {
                        return;
                    }

                    if (!Mouse.isButtonDown(1)) {
                        return;
                    }

                    if (this.checkState()) {
                        return;
                    }

                    if (System.currentTimeMillis() - this.timestamp > this.speed * 1000.0) {
                        this.timestamp = System.currentTimeMillis();
                        if (this.timestamp2 < this.timestamp) {
                            this.timestamp2 = this.timestamp;
                        }

                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                        KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                        this.process();
                    } else if (System.currentTimeMillis() - this.timestamp2 > this.amount * 1000.0) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                        this.process();
                    }
                }
            }
        }
    }

    private boolean checkState() {
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        return heldItem != null && heldItem.getItem() instanceof ItemSword;
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.process();
        }
    }

    private void process() {
        double d = this.minSetting.computeDouble();
        double d2 = this.maxSetting.computeDouble();
        if (d2 <= d) {
            d2 = d + 1.0;
            this.maxSetting.handleDouble2(d2);
        }

        double d3 = Math.max(0.1, d - 0.2);
        double d4 = Math.max(d3 + 0.1, d2);
        double d5 = Math.max(0.1, d);
        double d6 = Math.max(d5 + 0.1, d2);
        this.speed = 1.0 / ThreadLocalRandom.current().nextDouble(d3, d4);
        this.amount = this.speed / ThreadLocalRandom.current().nextDouble(d5, d6);
    }
}
