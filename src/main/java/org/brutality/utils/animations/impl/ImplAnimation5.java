package org.brutality.utils.animations.impl;

import org.brutality.utils.animations.ForwardsType;
import org.brutality.utils.animations.IsDoneHelper;

public class ImplAnimation5 extends IsDoneHelper {
    public float factor;
    public float factor2;
    public boolean flag;

    @Override
    protected double computeDouble(double d) {
        d = Math.pow(d, this.factor2);
        double d2 = this.factor * 0.1F;
        return Math.pow(2.0, -10.0 * (this.flag ? Math.sqrt(d) : d)) * Math.sin((d - d2 / 4.0) * ((Math.PI * 2) / d2)) + 1.0;
    }

    public ImplAnimation5(int n, double d, float factor, float f, boolean flag) {
        super(n, d);
        this.factor = factor;
        this.factor2 = f;
        this.flag = flag;
    }

    public ImplAnimation5(int n, double d, float factor, float f, boolean flag, ForwardsType forwardsType) {
        super(n, d, forwardsType);
        this.factor = factor;
        this.factor2 = f;
        this.flag = flag;
    }
}
