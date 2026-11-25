package org.brutality.alt.altmanager.utils;

import java.net.URI;

public class BrowserLauncher {
    public static void openWebLink(URI uri) {
        try {
            Class clazz = Class.forName("java.awt.Desktop");
            Object obj = clazz.getMethod("getDesktop").invoke(null);
            clazz.getMethod("browse", URI.class).invoke(obj, uri);
        } catch (Throwable ignored) {
        }
    }
}
