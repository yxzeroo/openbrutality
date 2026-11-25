package org.brutality.utils.animations;

import lombok.Generated;
import org.brutality.utils.animations.impl.ImplAnimation6;

public class GetAnimationHelper {
    private float factor;
    private float factor2;
    private IsDoneHelper animation = new ImplAnimation6(0, 0.0, ForwardsType.BACKWARDS);

    @Generated
    public IsDoneHelper getAnimation() {
        return this.animation;
    }

    public void handleFloat(float f, int n) {
        this.factor = (float)(this.factor2 - this.animation.computeDouble());
        this.factor2 = f;
        if (this.factor != this.factor2 - f) {
            this.animation = new ImplAnimation6(n, this.factor2 - this.factor, ForwardsType.BACKWARDS);
        }
    }

    public boolean isDone() {
        return this.factor == this.factor2 || this.animation.isDone();
    }

    public float computeFloat() {
        this.factor = (float)(this.factor2 - this.animation.computeDouble());
        return this.factor;
    }
}
