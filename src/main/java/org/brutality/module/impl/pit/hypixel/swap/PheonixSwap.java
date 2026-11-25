package org.brutality.module.impl.pit.hypixel.swap;

public class PheonixSwap extends ModeModule {
    private static PheonixSwap instance;

    public static PheonixSwap getInstance() {
        return instance;
    }

    public PheonixSwap() {
        super("PheonixSwap", true);
        instance = this;
    }

    public boolean checkState() {
        return FindHandler.matchesFlag(true, true);
    }
}
