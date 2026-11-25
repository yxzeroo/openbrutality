package org.brutality.module.impl.pit.hypixel.utility;

import java.util.Arrays;
import org.brutality.event.Event;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.timer.TimerUtil;

public class CombatLogger extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private final NumberSetting healthThresholdSetting;
    private final ModeSetting disconnectSetting;
    private final NumberSetting delayMsSetting;
    private final BooleanSetting notifySetting;
    private final TimerUtil timerUtil = new TimerUtil();
    private boolean flag = false;

    private void process() {
        String str = this.disconnectSetting.getText().toLowerCase();
        switch (str) {
            case "hub":
                mc.thePlayer.sendChatMessage("/hub");
                break;
            case "lobby":
                mc.thePlayer.sendChatMessage("/lobby");
                break;
            case "disconnect":
                if (mc.theWorld != null) {
                    mc.theWorld.sendQuittingDisconnectingPacket();
                }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventTick) {
                    if (mc.thePlayer != null) {
                        double d = mc.thePlayer.getHealth() + mc.thePlayer.getAbsorptionAmount();
                        if (d <= this.healthThresholdSetting.computeDouble() && !this.flag) {
                            if (!this.timerUtil.matchesLong4((long)this.delayMsSetting.computeDouble())) {
                                return;
                            }

                            this.flag = true;
                            this.timerUtil.reset();
                            if (this.notifySetting.isEnabled()) {
                                String text = this.disconnectSetting.getText();
                                MessageUtils.x("Health dropped to " + d + ", executing " + text + "...");
                            }

                            this.process();
                        } else if (d > this.healthThresholdSetting.computeDouble()) {
                            this.flag = false;
                        }
                    }
                }
            }
        }
    }

    public CombatLogger() {
        super("CombatLogger", 0, Category.PIT);
        this.healthThresholdSetting = new NumberSetting("Health Threshold", this, 5.0, 0.5, 20.0, 0.1);
        this.disconnectSetting = new ModeSetting("Exit Mode", this, "Hub", Arrays.asList("Hub", "Lobby", "Disconnect"));
        this.delayMsSetting = new NumberSetting("Delay (ms)", this, 200.0, 0.0, 2000.0, 20.0);
        this.notifySetting = new BooleanSetting("Notify", this, true);
        this.handleSettingArray(new Setting[]{this.healthThresholdSetting, this.disconnectSetting, this.delayMsSetting, this.notifySetting});
    }
}
