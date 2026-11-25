package org.brutality.automation.grinding.targeting;

import org.brutality.module.impl.combat.KillAura;

public final class ReleaseHandler {
    private KillAura killAura;
    private double amount;
    private double amount2;
    private boolean flag;
    private boolean flag2;
    private String text;
    private String text2;

    public void handleKillAura(KillAura killAura, boolean flag) {
        if (this.killAura != killAura) {
            this.release();
            this.killAura = killAura;
            this.amount = killAura.horizontalAngleSetting.computeDouble();
            this.amount2 = killAura.verticalAngleSetting.computeDouble();
        }

        if (killAura.horizontalAngleSetting.computeDouble() != 360.0) {
            killAura.horizontalAngleSetting.handleDouble2(360.0);
        }

        if (killAura.verticalAngleSetting.computeDouble() != 360.0) {
            killAura.verticalAngleSetting.handleDouble2(360.0);
        }

        if (flag) {
            if (!this.flag) {
                this.text = killAura.verusSetting.getText();
                this.text2 = killAura.computeString();
                this.flag2 = killAura.manualBlockSetting.isEnabled();
                this.flag = true;
            }

            if (!killAura.verusSetting.matchesString2("Watchdog")) {
                killAura.verusSetting.handleString("Watchdog");
            }

            killAura.handleString("7-CPS");
            killAura.manualBlockSetting.setEnabled(true);
        } else {
            this.process();
        }
    }

    private void process() {
        if (this.killAura != null && this.flag) {
            this.killAura.verusSetting.handleString(this.text);
            this.killAura.handleString(this.text2);
            this.killAura.manualBlockSetting.setEnabled(this.flag2);
            this.flag = false;
        }
    }

    public boolean matchesKillAura(KillAura killAura) {
        return this.killAura == killAura && this.flag;
    }

    public void release() {
        if (this.killAura != null) {
            this.process();
            this.killAura.horizontalAngleSetting.handleDouble2(this.amount);
            this.killAura.verticalAngleSetting.handleDouble2(this.amount2);
            this.killAura = null;
        }
    }
}
