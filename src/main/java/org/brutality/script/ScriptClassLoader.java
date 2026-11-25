package org.brutality.script;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import org.brutality.Brutality;

public class ScriptClassLoader extends URLClassLoader {
    private static final List<String> entries = Arrays.asList(
        "sun.reflect", "org.brutality", "java.lang", "java.util", "java.io", "java.awt", "net.minecraft", "org.lwjgl"
    );

    public ScriptClassLoader(URL[] urls, ClassLoader classLoader) {
        super(urls, classLoader);
    }

    private boolean isClassSafe(String str) {
        boolean flag = str.endsWith("Exception") || str.endsWith("Throwable");
        boolean flag2 = Brutality.scriptManager.entries.stream().anyMatch(str2 -> str.toLowerCase().startsWith(str2.toLowerCase()));
        boolean flag3 = str.startsWith("sc_") && !str.contains(".");
        boolean flag4 = entries.stream().anyMatch(str::startsWith);
        return flag || flag2 || flag3 || flag4;
    }

    @Override
    protected Class<?> loadClass(String str, boolean flag) throws ClassNotFoundException {
        if (str.startsWith("jdk.") || str.startsWith("sun.reflect")) {
            return this.getParent().loadClass(str);
        }
        if (!this.isClassSafe(str)) {
            throw new ClassNotFoundException("Unsafe class detected: " + str);
        }
        return super.loadClass(str, flag);
    }
}
