package org.brutality.utils.animations.impl;

import org.brutality.utils.animations.ForwardsType;
import org.brutality.utils.animations.IsDoneHelper;

public class ImplAnimation4 extends IsDoneHelper {
    public ImplAnimation4(int n, double d, ForwardsType forwardsType) {
        super(n, d, forwardsType);
    }

    public ImplAnimation4(int n, double d) {
        super(n, d);
    }

    @Override
    protected boolean checkState() {
        return true;
    }

    @Override
    protected double computeDouble(double d) {
        return Math.sin(d * (Math.PI / 2));
    }
}
