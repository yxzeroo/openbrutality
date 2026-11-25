package org.brutality.notification.old;

import org.brutality.notification.modern.RenderAllHandler;

public class ModuleEnabledHandler {
    public static void handleString(String str, String str2, long l) {
        RenderAllHandler renderAllHandler = new RenderAllHandler(InfoType.SUCCESS, str, str2, 5, l);
        renderAllHandler.process5();
    }

    public static void handleString(String str, boolean flag) {
        if (flag) {
            handleString5("Module Enabled", str + " has been enabled!");
        } else {
            handleString("Module Disabled", str + " has been disabled.");
        }
    }

    public static void handleString2(String str, String str2) {
        handleString2(str, str2, 3000L);
    }

    public static void handleString3(String str, String str2, long l) {
        RenderAllHandler renderAllHandler = new RenderAllHandler(InfoType.INFO, str, str2, 5, l);
        renderAllHandler.process5();
    }

    public static void handleString4(String str, String str2, long l) {
        RenderAllHandler renderAllHandler = new RenderAllHandler(InfoType.ERROR, str, str2, 5, l);
        renderAllHandler.process5();
    }

    public static void handleString2(String str, boolean flag) {
        if (flag) {
            handleString5("Config Loaded", "Successfully loaded " + str);
        } else {
            handleString2("Config Failed", "Failed to load " + str);
        }
    }

    public static void handleString4(String str, String str2) {
        handleString4(str, str2, 3000L);
    }

    public static void handleString5(String str, String str2) {
        handleString(str, str2, 3000L);
    }

    public static void handleString3(String str, String str2) {
        handleString5(str, str2, 3000L);
    }

    public static void handleString5(String str, String str2, long l) {
        RenderAllHandler renderAllHandler = new RenderAllHandler(InfoType.WARNING, str, str2, 5, l);
        renderAllHandler.process5();
    }

    public static void handleFlag(boolean flag) {
        if (flag) {
            handleString5("Connected", "Successfully connected to server!");
        } else {
            handleString4("Disconnected", "Lost connection to server.");
        }
    }

    public static void handleString(String str, String str2) {
        handleString3(str, str2, 3000L);
    }

    public static void handleString2(String str, String str2, long l) {
        RenderAllHandler renderAllHandler = new RenderAllHandler(InfoType.FAILURE, str, str2, 5, l);
        renderAllHandler.process5();
    }
}
