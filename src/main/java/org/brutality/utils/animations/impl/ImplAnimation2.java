package org.brutality.utils.animations.impl;

import org.brutality.utils.animations.ForwardsType;
import org.brutality.utils.animations.IsDoneHelper;

public class ImplAnimation2 extends IsDoneHelper {
    private final float factor;

    @Override
    protected boolean checkState() {
        return true;
    }

    @Override
    protected double computeDouble(double d) {
        float f = this.factor + 1.0F;
        return Math.max(0.0, 1.0 + f * Math.pow(d - 1.0, 3.0) + this.factor * Math.pow(d - 1.0, 2.0));
    }

    public ImplAnimation2(int n, double d, float factor, ForwardsType forwardsType) {
        super(n, d, forwardsType);
        this.factor = factor;
    }

    public ImplAnimation2(int n, double d, float factor) {
        super(n, d);
        this.factor = factor;
    }
}
