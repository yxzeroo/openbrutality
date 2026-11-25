package org.brutality.event.impl;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.brutality.event.Event;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.misc.SoundDebugger;
import org.brutality.module.impl.misc.SoundPlayer;

public class ImplEvent extends Event {
    public final SoundManager soundManager;

    public ImplEvent(SoundManager soundManager) {
        this.soundManager = soundManager;
    }

    @Override
    public void process() {
        for (Module module : ModuleManager.getModuleArray()) {
            if (module.checkState44() && (module instanceof SoundDebugger || module instanceof SoundPlayer)) {
                try {
                    module.onEvent(this);
                } catch (RuntimeException e) {
                    LogManager.getLogger(ImplEvent.class).error("Sound listener failed: " + module.name2, (Throwable)e);
                }
            }
        }
    }

    public static final class Entry extends ImplEvent {
        public final ISound iSound;
        public final String text;

        public Entry(SoundManager soundManager, ISound iSound, String text) {
            super(soundManager);
            this.iSound = iSound;
            this.text = text;
        }
    }

    public static class Entry2 extends ImplEvent {
        public final ISound iSound;
        public final String text;
        public final String text2;
        public final boolean flag;
        public final ResourceLocation resourceLocation;

        public Entry2(SoundManager soundManager, ISound iSound, String text, ResourceLocation resourceLocation, boolean flag) {
            super(soundManager);
            this.text2 = iSound.getSoundLocation().getResourcePath();
            this.iSound = iSound;
            this.text = text;
            this.resourceLocation = resourceLocation;
            this.flag = flag;
        }

        public Entry2(SoundManager soundManager, ISound iSound, String str) {
            this(soundManager, iSound, str, null, false);
        }
    }
}
