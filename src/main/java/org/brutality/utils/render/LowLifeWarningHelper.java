package org.brutality.utils.render;

import net.minecraft.inventory.Slot;
import org.brutality.Brutality;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.pit.hypixel.automation.LowLifeWarning;

public final class LowLifeWarningHelper {
    private static Object computeObject() {
        Brutality.getInstance();
        ModuleManager moduleManager = Brutality.getModuleManager();
        if (moduleManager == null) {
            return null;
        }
        Module module = moduleManager.computeModule("Low Life Warning");
        return module != null && module.checkState44() ? module : null;
    }

    private LowLifeWarningHelper() {
    }

    public static void handleSlot(Slot slot, int n, int n2) {
        Object obj = computeObject();
        if (obj != null) {
            if (obj instanceof LowLifeWarning) {
                ((LowLifeWarning)obj).handleSlot(slot, n, n2);
            }
        }
    }

    public static void handleInt(int n, int n2, int n3) {
        Object obj = computeObject();
        if (obj != null) {
            if (obj instanceof LowLifeWarning) {
                ((LowLifeWarning)obj).handleInt(n, n2, n3);
            }
        }
    }
}
