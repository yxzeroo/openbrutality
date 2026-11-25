package org.brutality.gui.menu;

import lombok.Generated;
import org.brutality.utils.timer.TimerUtil;

public class IsAnimationDoneComponent {
    private String name2;
    private boolean hovered2;
    private TimerUtil animationTimer;
    private final long timestamp = 200L;
    private final long timestamp2 = 70L;
    private boolean animationDone;

    public double computeDouble() {
        double d = this.animationTimer.computeLong5();
        return Math.min((this.hovered2 ? d : 70.0 - d) / (this.hovered2 ? 200L : 70L), 1.0);
    }

    @Generated
    public boolean isAnimationDone() {
        return this.animationDone;
    }

    @Generated
    public String getName() {
        return this.name2;
    }

    @Generated
    public long getAnimInDuration() {
        return 200L;
    }

    @Generated
    public long getAnimOutDuration() {
        return 70L;
    }

    @Generated
    public boolean isHovered() {
        return this.hovered2;
    }

    @Generated
    public void setAnimationDone(boolean animationDone) {
        this.animationDone = animationDone;
    }

    public void handleFlag(boolean hovered2) {
        if (this.hovered2 != hovered2) {
            this.animationTimer.reset();
            this.hovered2 = hovered2;
            this.animationDone = false;
        }

        if (this.animationTimer.computeLong5() >= (this.hovered2 ? 200L : 70L)) {
            this.animationDone = true;
        }
    }

    public IsAnimationDoneComponent(String name2) {
        this.name2 = name2;
        this.hovered2 = false;
        this.animationTimer = new TimerUtil();
    }

    @Generated
    public TimerUtil getAnimationTimer() {
        return this.animationTimer;
    }
}
