package org.brutality.module.impl.combat;

import java.util.Arrays;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.attack.GetDamageEvent;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class HitSelect extends Module {
    private final NumberSetting chanceSetting = new NumberSetting("Chance", this, 10.0, 1.0, 100.0, 1.0);
    private final NumberSetting thresholdSetting = new NumberSetting("Threshold", this, 300.0, 0.0, 500.0, 5.0);
    public ModeSetting pauseSetting = new ModeSetting("Mode", this, "Active", Arrays.asList("Active", "Pause"));
    public ModeSetting criticalSetting = new ModeSetting("Preference", this, "Movement", Arrays.asList("Movement", "Reduce", "Critical"));
    private long timestamp = -1L;
    private boolean flag = false;

    @Override
    public void onDisable() {
        this.process();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof GetDamageEvent) {
                    if (this.pauseSetting.matchesString2("Active") && !this.flag) {
                        event.cancel();
                        return;
                    }

                    if (this.pauseSetting.matchesString2("Active") || this.flag) {
                        this.timestamp = System.currentTimeMillis();
                    }
                }

                if (event instanceof RenderEvent4) {
                    this.process();
                }

                if (event instanceof EventUpdate event2) {
                    if (event2.getState() != EventState.PRE) {
                        return;
                    }

                    if (mc.thePlayer == null) {
                        return;
                    }

                    this.flag = false;
                    if (Math.random() * 100.0 > this.chanceSetting.computeDouble()) {
                        this.flag = true;
                        return;
                    }

                    String str = this.criticalSetting.getText().toLowerCase();
                    switch (str) {
                        case "movement":
                            double d = mc.thePlayer.posX - mc.thePlayer.prevPosX;
                            double d2 = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
                            this.flag = Math.sqrt(d * d + d2 * d2) > 0.1;
                            break;
                        case "reduce":
                            this.flag = mc.thePlayer.hurtTime > 0 && !mc.thePlayer.onGround;
                            break;
                        case "critical":
                            this.flag = !mc.thePlayer.onGround && mc.thePlayer.motionY < 0.0;
                    }

                    if (!this.flag) {
                        this.flag = System.currentTimeMillis() - this.timestamp >= this.thresholdSetting.computeDouble();
                    }
                }
            }
        }
    }

    public HitSelect() {
        super("Hit Select", 0, Category.COMBAT);
        this.handleSettingArray(new Setting[]{this.chanceSetting, this.thresholdSetting, this.pauseSetting, this.criticalSetting});
    }

    private void process() {
        this.timestamp = -1L;
        this.flag = false;
    }
}
