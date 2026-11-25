package org.brutality.utils.animations.impl;

import org.brutality.utils.animations.ForwardsType;
import org.brutality.utils.animations.IsDoneHelper;

public class ImplAnimation3 extends IsDoneHelper {
    public ImplAnimation3(int n, double d) {
        super(n, d);
    }

    public ImplAnimation3(int n, double d, ForwardsType forwardsType) {
        super(n, d, forwardsType);
    }

    @Override
    protected double computeDouble(double d) {
        return d < 0.5 ? 2.0 * Math.pow(d, 2.0) : 1.0 - Math.pow(-2.0 * d + 2.0, 2.0) / 2.0;
    }
}
