package org.brutality.optifine;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class OptifineCustomItemsEnableHandler {
    private static boolean flag = false;

    public static void process() {
    }

    private static void process2() throws Exception {
        System.setProperty("optifine.CustomItems.enable", "false");

        try {
            Class clazz = Class.forName("net.optifine.CustomItems");
            Field declaredField = clazz.getDeclaredField("enabled");
            declaredField.setAccessible(true);
            declaredField.setBoolean(null, false);
        } catch (Exception ignored) {
        }
    }

    public static void process3() {
        if (!flag) {
            try {
                process5();
                process4();
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();

                try {
                    process2();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private static void process4() throws Exception {
        Class clazz = Class.forName("net.optifine.CustomItems");
        Field[] declaredFields = clazz.getDeclaredFields();

        for (Field field : declaredFields) {
            if (field.getType() == List.class || field.getType() == ArrayList.class) {
                field.setAccessible(true);
                if (field.get(null) instanceof ArrayList list) {
                    handleArrayList(list, 500);
                }
            }
        }
    }

    private static void process5() throws Exception {
        Class clazz = Class.forName("net.optifine.CustomItems");
        Method method = null;
        Method[] declaredMethods = clazz.getDeclaredMethods();

        for (Method method2 : declaredMethods) {
            if (method2.getName().equals("addToList")) {
                method = method2;
                break;
            }
        }

        if (method == null) {
            throw new Exception("addToList method not found");
        }
    }

    private static void handleArrayList(ArrayList list, int n) {
        while (list.size() < n) {
            list.add(null);
        }
    }

    public static class Entry {
        public static void handleArrayList2(ArrayList<Object> list, int n, Object obj) {
            if (n >= 0 && n <= 2000) {
                handleArrayList(list, n, obj);
            }
        }

        public static void handleArrayList(ArrayList<Object> list, int n, Object obj) {
            try {
                while (true) {
                    if (list.size() <= n) {
                        list.add(null);
                    } else {
                        list.set(n, obj);
                        break;
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }
}
