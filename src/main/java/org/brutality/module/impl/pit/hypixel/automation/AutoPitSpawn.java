package org.brutality.module.impl.pit.hypixel.automation;

import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.hypixel.pit.StripColorHelper;
import org.brutality.utils.timer.TimerUtil;

public class AutoPitSpawn extends Module {
    private static AutoPitSpawn instance;
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private final TimerUtil timerUtil = new TimerUtil();
    private boolean flag = false;
    private long timestamp = 0L;
    private boolean flag2 = true;
    private boolean flag3;
    private final BooleanSetting debugSetting = new BooleanSetting("Debug", this, false);
    private long timestamp2;
    private int count;
    private int count2;

    public AutoPitSpawn() {
        super("Auto Pit Spawn", 205, Category.PIT);
        instance = this;
        this.handleSettingArray(new Setting[]{this.debugSetting});
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.count = this.count2 = 0;
            this.timestamp2 = 0L;
            this.trace("Enabled; acquiring combat tracker");
            StripColorHelper.process4();
            this.flag = false;
            this.timestamp = 0L;
            this.flag2 = true;
            this.flag3 = false;
            this.timerUtil.reset();
            if (mc.thePlayer != null) {
                if (FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                    MessageUtils.x(MessageUtils.computeString("[AutoPitSpawn]", MessageUtils.text7) + " You're already in spawn.");
                    this.process28();
                } else {
                    this.handleFlag(true);
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (mc.thePlayer != null) {
                    StripColorHelper.onEvent(event);
                    if (event instanceof EventTick) {
                        if (((EventTick)event).getState() != EventState.PRE) {
                            return;
                        }

                        if (this.debugSetting.isEnabled() && System.currentTimeMillis() - this.timestamp2 >= 1000L) {
                            this.timestamp2 = System.currentTimeMillis();
                            boolean flag = this.flag;
                            boolean flag2 = this.flag2;
                            long l = Math.max(0L, this.timestamp - this.timerUtil.computeLong4());
                            long l2 = StripColorHelper.computeLong3();
                            long l3 = StripColorHelper.computeLong();
                            long l4 = l2;
                            long l5 = l;
                            boolean flag3 = flag2;
                            boolean flag4 = flag;
                            this.trace(
                                "State waiting="
                                    + flag4
                                    + " needsRespawn="
                                    + flag3
                                    + " retryRemainingMs="
                                    + l5
                                    + " combatMs="
                                    + l4
                                    + " commandCooldownMs="
                                    + l3
                            );
                        }

                        if (FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                            MessageUtils.x(MessageUtils.computeString("[AutoPitSpawn]", MessageUtils.text11) + " Successfully respawned.");
                            this.process28();
                            return;
                        }

                        if (this.flag) {
                            if (this.timerUtil.matchesLong4(this.timestamp)) {
                                this.flag = false;
                                this.timestamp = 0L;
                                StripColorHelper.process();
                                this.flag3 = false;
                                this.handleFlag(false);
                                return;
                            }

                            return;
                        }

                        if (StripColorHelper.checkState()) {
                            this.flag2 = true;
                            return;
                        }

                        if (this.flag2) {
                            this.handleFlag(!this.flag3);
                            this.flag3 = false;
                        }
                    }
                }
            }
        }
    }

    private void handleFlag(boolean flag) {
        if (mc.thePlayer != null) {
            if (flag) {
                StripColorHelper.process2();
            }

            mc.thePlayer.sendChatMessage("/respawn");
            this.trace("Sent /respawn attempt=" + ++this.count + " cooldown=" + flag);
            this.flag2 = false;
        }
    }

    private void handleString(String str) {
        int n = StripColorHelper.computeInt2(str);
        if (str != null && (str.toLowerCase(Locale.ROOT).contains("respawn") || str.toLowerCase(Locale.ROOT).contains("hold up"))) {
            boolean flag = this.flag;
            this.trace("Server text=" + str + " parsedSeconds=" + n + " waitingBefore=" + flag);
        }

        if (n >= 0) {
            this.count2++;
            this.timestamp = n * 1000L + 150L;
            this.timerUtil.reset();
            this.flag = true;
            this.flag2 = false;
            this.flag3 = true;
            long timestamp = this.timestamp;
            int n2 = this.count2;
            this.trace("Denial #" + n2 + " scheduled retryMs=" + timestamp);
            MessageUtils.x(MessageUtils.computeString("[AutoPitSpawn]", MessageUtils.text7) + " Combat detected — retrying in " + n + "s.");
        }
    }

    public static void handleString2(String str) {
        AutoPitSpawn autoPitSpawn = instance;
        // VENDOR-DISABLED: if (autoPitSpawn != null && autoPitSpawn.checkState44() && RuntimeAuthGuard.checkState2()) {
        if (autoPitSpawn != null && autoPitSpawn.checkState44() && true) {
            autoPitSpawn.handleString(str);
        }
    }

    private void trace(String str) {
        if (this.debugSetting.isEnabled()) {
            String str2 = "[AutoPitSpawn Debug] " + str;
            LogManager.getLogger(AutoPitSpawn.class).info(str2);
            if (mc.thePlayer != null) {
                MessageUtils.x(str2);
            }
        }
    }

    @Override
    public void onDisable() {
        this.trace("Disabled; attempts=" + this.count + " matchedDenials=" + this.count2);
        super.onDisable();
        StripColorHelper.release();
        this.flag = false;
        this.timestamp = 0L;
        this.flag2 = false;
        this.flag3 = false;
    }
}
