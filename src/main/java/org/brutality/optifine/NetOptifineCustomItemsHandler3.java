package org.brutality.optifine;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Properties;
import net.optifine.ItemIdConverter;

public class NetOptifineCustomItemsHandler3 {
    private static boolean flag = false;
    private static Method method = null;

    public static void process3() {
        try {
            Class clazz = Class.forName("net.optifine.CustomItems");
            Method[] declaredMethods = clazz.getDeclaredMethods();

            for (Method method : declaredMethods) {
                if (method.getName().contains("loadProperties") || method.getName().contains("readProperties") || method.getName().contains("parseProperties")) {
                    handleMethod(method);
                    break;
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static void process() throws Exception {
        System.setProperty("java.util.Properties.class", NetOptifineCustomItemsHandler3.Entry.class.getName());
    }

    private static void handleMethod(Method method) {
    }

    public static void process2() {
        if (!flag) {
            try {
                process();
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class Entry extends Properties {
        @Override
        public synchronized void load(Reader reader) throws IOException {
            StringBuilder sb = new StringBuilder();
            char[] chars = new char[1024];

            int n;
            while ((n = reader.read(chars)) != -1) {
                sb.append(chars, 0, n);
            }

            String str = sb.toString();
            if (this.matchesString(str)) {
                String str2 = ItemIdConverter.convertItemIds(str);
                super.load(new StringReader(str2));
            } else {
                super.load(new StringReader(str));
            }
        }

        private boolean matchesString(String str) {
            return str.contains("type=item")
                || str.contains("type=armor")
                || str.contains("items=")
                || str.contains("texture=")
                || str.contains("nbt.display");
        }
    }
}
