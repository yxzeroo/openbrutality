package org.brutality.module.impl.pit.hypixel.swap;

public class PodSwap extends ModeModule {
    private static PodSwap instance;

    public PodSwap() {
        super("PodSwap", false);
        instance = this;
    }

    public boolean checkState() {
        return FindHandler.matchesFlag(false, true);
    }

    public static PodSwap getInstance() {
        return instance;
    }
}
