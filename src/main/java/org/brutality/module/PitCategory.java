package org.brutality.module;

public enum PitCategory {
    SWAP,
    AUTOMATION,
    HUD,
    UTILITY,
    MAJOR_EVENT,
    MINOR_EVENT,
    PREMIUM;

    private static PitCategory[] computePitCategoryArray() {
        return new PitCategory[]{SWAP, AUTOMATION, HUD, UTILITY, MAJOR_EVENT, MINOR_EVENT, PREMIUM};
    }
}
