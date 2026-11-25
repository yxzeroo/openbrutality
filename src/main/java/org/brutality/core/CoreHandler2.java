package org.brutality.core;

import java.util.concurrent.atomic.AtomicBoolean;

public class CoreHandler2 {
    private static final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    public static void handleFlag(boolean flag) {
        atomicBoolean.set(flag);
    }

    public static boolean checkState() {
        return atomicBoolean.get();
    }
}
