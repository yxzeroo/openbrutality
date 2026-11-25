package org.brutality.optifine;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class NetOptifineCustomItemsHandler2 {
    private static boolean flag = false;

    public static void process() {
        if (!flag) {
            try {
                Class clazz = Class.forName("net.optifine.CustomItems");
                Field[] declaredFields = clazz.getDeclaredFields();
                int n = 0;

                for (Field field : declaredFields) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        field.setAccessible(true);

                        try {
                            if (field.get(null) instanceof ArrayList list) {
                                handleArrayList(list, 500);
                                n++;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }

                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
                process2();
            }
        }
    }

    public static void process3() {
    }

    private static void process2() {
        try {
            System.setProperty("optifine.CustomItems", "false");
            System.setProperty("of.customItems", "false");
            Class clazz = Class.forName("net.optifine.CustomItems");
            Field[] declaredFields = clazz.getDeclaredFields();

            for (Field field : declaredFields) {
                if (field.getType() == boolean.class && Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    field.setBoolean(null, false);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static void handleArrayList(ArrayList<Object> list, int n) {
        if (list != null) {
            list.ensureCapacity(n);

            while (list.size() < n) {
                list.add(null);
            }
        }
    }
}
