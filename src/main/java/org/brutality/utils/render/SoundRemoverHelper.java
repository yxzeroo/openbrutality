package org.brutality.utils.render;

import net.minecraft.client.audio.ISound;
import net.minecraft.util.ResourceLocation;
import org.brutality.Brutality;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.pit.hypixel.utility.SoundRemover;

public final class SoundRemoverHelper {
    private SoundRemoverHelper() {
    }

    public static boolean matchesISound(ISound iSound) {
        try {
            if (iSound == null) {
                return false;
            }
            ModuleManager moduleManager;
            if (Brutality.getInstance() == null) {
                moduleManager = null;
            } else {
                Brutality.getInstance();
                moduleManager = Brutality.getModuleManager();
            }

            ModuleManager moduleManager2 = moduleManager;
            if (moduleManager2 == null) {
                return false;
            }
            Module module = moduleManager2.computeModule("Sound Remover");
            if (module instanceof SoundRemover && module.checkState44()) {
                ResourceLocation soundLocation = iSound.getSoundLocation();
                return soundLocation == null ? false : ((SoundRemover)module).matchesString(soundLocation.getResourcePath());
            }
            return false;
        } catch (Throwable e) {
            return false;
        }
    }
}
