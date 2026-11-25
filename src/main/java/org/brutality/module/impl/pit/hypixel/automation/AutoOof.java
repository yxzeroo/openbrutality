package org.brutality.module.impl.pit.hypixel.automation;

import java.util.Arrays;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.timer.TimerUtil;

public class AutoOof extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private final ModeSetting uberstreakSetting = new ModeSetting("Mode", this, "Overdrive", Arrays.asList("Overdrive", "Uberstreak"));
    private final TimerUtil timerUtil = new TimerUtil();
    private static final long timestamp = 2000L;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (event instanceof EventUpdate) {
                    if (((EventUpdate)event).getState() == EventState.PRE) {
                        if (this.timerUtil.matchesLong4(timestamp)) {
                            if (this.matchesString("Status: Overdrive")) {
                                if (!this.uberstreakSetting.getText().equals("Uberstreak") || this.checkState()) {
                                    this.timerUtil.reset();
                                    mc.thePlayer.sendChatMessage("/oof");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public AutoOof() {
        super("AutoOof", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.uberstreakSetting});
    }

    private boolean checkState() {
        for (String str : GetCurrentScreenHelper.computeList2()) {
            if (str != null && this.computeString(str).contains("Streak:")) {
                try {
                    String str2 = this.computeString(str).replaceAll("[^0-9]", "");
                    if (!str2.isEmpty() && Integer.parseInt(str2) >= 400) {
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        return false;
    }

    private boolean matchesString(String str) {
        for (String str2 : GetCurrentScreenHelper.computeList2()) {
            if (str2 != null && this.computeString(str2).contains(str)) {
                return true;
            }
        }

        return false;
    }

    private String computeString(String str) {
        return str.replaceAll("§[0-9a-fk-or]", "");
    }
}
