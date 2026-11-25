package org.brutality.utils.performance;

import org.brutality.event.Event;
import org.brutality.module.Module;

public class UnknownHelper {
    public static void handleModule(Module module, String str, Runnable task) {
        if (!GetTopSlowestHelper.isEnabled()) {
            task.run();
        } else {
            handleModule(module, str);

            try {
                task.run();
            } finally {
                handleModule2(module, str);
            }
        }
    }

    public static void handleModule(Module module, String str) {
        if (GetTopSlowestHelper.isEnabled()) {
            GetTopSlowestHelper.handleModule2(module, str);
        }
    }

    public static void handleModule(Module module, Event event, Runnable task) {
        if (!GetTopSlowestHelper.isEnabled()) {
            task.run();
        } else {
            String str = onEvent(event);
            GetTopSlowestHelper.handleModule2(module, str);

            try {
                task.run();
            } finally {
                GetTopSlowestHelper.handleModule(module, str);
            }
        }
    }

    private static String onEvent(Event event) {
        if (event == null) {
            return "Unknown";
        }
        String simpleName = event.getClass().getSimpleName();
        if (simpleName.startsWith("Event")) {
            simpleName = simpleName.substring(5);
        }

        return simpleName;
    }

    public static void handleModule2(Module module, Event event) {
        if (GetTopSlowestHelper.isEnabled()) {
            GetTopSlowestHelper.handleModule2(module, onEvent(event));
        }
    }

    public static void handleModule2(Module module, String str) {
        if (GetTopSlowestHelper.isEnabled()) {
            GetTopSlowestHelper.handleModule(module, str);
        }
    }

    public static void handleModule(Module module, Event event) {
        if (GetTopSlowestHelper.isEnabled()) {
            GetTopSlowestHelper.handleModule(module, onEvent(event));
        }
    }
}
