package org.brutality.alt.altmanager.auth;

import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class SessionHolder {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static Field field = null;

    public static Session getSession() {
        return mc.getSession();
    }

    public static Field computeField() {
        if (field == null) {
            try {
                for (Field declaredField : Minecraft.class.getDeclaredFields()) {
                    if (declaredField.getType() == Session.class) {
                        field = declaredField;
                        field.setAccessible(true);
                        break;
                    }
                }
            } catch (Exception e) {
                field = null;
            }
        }

        return field;
    }

    public static void setSession(Session session) {
        mc.session = session;
    }
}
