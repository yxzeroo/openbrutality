package org.brutality.utils.modules;

public enum NoneType {
    NONE,
    ANTI_VOID,
    AUTO_BLOCK,
    BLINK,
    NO_FALL,
    NO_SLOW;

    private static NoneType[] computeNoneTypeArray() {
        return new NoneType[]{NONE, ANTI_VOID, AUTO_BLOCK, BLINK, NO_FALL, NO_SLOW};
    }
}
