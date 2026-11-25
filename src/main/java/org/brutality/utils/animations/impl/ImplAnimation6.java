package org.brutality.utils.animations.impl;

import org.brutality.utils.animations.ForwardsType;
import org.brutality.utils.animations.IsDoneHelper;

public class ImplAnimation6 extends IsDoneHelper {
    @Override
    protected double computeDouble(double d) {
        return -2.0 * Math.pow(d, 3.0) + 3.0 * Math.pow(d, 2.0);
    }

    public ImplAnimation6(int n, double d, ForwardsType forwardsType) {
        super(n, d, forwardsType);
    }

    public ImplAnimation6(int n, double d) {
        super(n, d);
    }
}
