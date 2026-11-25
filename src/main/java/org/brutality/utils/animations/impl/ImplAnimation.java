package org.brutality.utils.animations.impl;

import org.brutality.utils.animations.ForwardsType;
import org.brutality.utils.animations.IsDoneHelper;

public class ImplAnimation extends IsDoneHelper {
    public ImplAnimation(int n, double d, ForwardsType forwardsType) {
        super(n, d, forwardsType);
    }

    public ImplAnimation(int n, double d) {
        super(n, d);
    }

    @Override
    protected double computeDouble(double d) {
        return 1.0 - (d - 1.0) * (d - 1.0);
    }
}
