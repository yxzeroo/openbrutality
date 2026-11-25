package org.brutality.utils.animations;

public enum ForwardsType {
    FORWARDS,
    BACKWARDS;

    private static ForwardsType[] computeForwardsTypeArray() {
        return new ForwardsType[]{FORWARDS, BACKWARDS};
    }

    public ForwardsType computeForwardsType() {
        return this == FORWARDS ? BACKWARDS : FORWARDS;
    }

    public boolean checkState2() {
        return this == BACKWARDS;
    }

    public boolean checkState() {
        return this == FORWARDS;
    }
}
