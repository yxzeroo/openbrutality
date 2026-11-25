package org.brutality.utils.network;

import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class SetSessionHelper {
    public static Session computeSession() {
        return Minecraft.getMinecraft().getSession();
    }

    public static void setSession(Session session) {
        try {
            for (Field declaredField : Minecraft.class.getDeclaredFields()) {
                if (declaredField.getType() == Session.class) {
                    declaredField.setAccessible(true);
                    declaredField.set(Minecraft.getMinecraft(), session);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
