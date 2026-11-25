package org.brutality.launcher.internal;

import java.net.URL;
import java.net.URLClassLoader;

final class BackendTlsContext extends URLClassLoader {
    BackendTlsContext(URL[] urls, ClassLoader classLoader) {
        super(urls, classLoader);
    }

    @Override
    protected Class<?> loadClass(String str, boolean flag) throws ClassNotFoundException {
        if (matchesString(str)) {
            return super.loadClass(str, flag);
        }
        synchronized (this.getClassLoadingLock(str)) {
            Class clazz = this.findLoadedClass(str);
            if (clazz == null) {
                try {
                    clazz = this.findClass(str);
                } catch (ClassNotFoundException e) {
                    clazz = super.loadClass(str, false);
                }
            }

            if (flag) {
                this.resolveClass(clazz);
            }

            return clazz;
        }
    }

    private static boolean matchesString(String str) {
        return str.startsWith("java.")
            || str.startsWith("javax.")
            || str.startsWith("sun.")
            || str.startsWith("jdk.")
            || str.startsWith("com.google.")
            || str.startsWith("com.mojang.")
            || str.startsWith("joptsimple.")
            || str.startsWith("org.apache.")
            || str.startsWith("org.lwjgl.")
            || str.startsWith("org.slf4j.")
            || str.startsWith("io.netty.")
            || str.startsWith("paulscode.")
            || str.startsWith("net.java.")
            || str.startsWith("tv.twitch.");
    }
}
