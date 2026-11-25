package net.minecraft.client.audio;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import io.netty.util.internal.ThreadLocalRandom;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.brutality.event.impl.ImplEvent;
import org.brutality.utils.render.SoundRemoverHelper;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.SoundSystemLogger;
import paulscode.sound.Source;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class SoundManager {
    static final Marker LOG_MARKER = MarkerManager.getMarker("SOUNDS");
    static final Logger logger = LogManager.getLogger();
    private final SoundHandler sndHandler;
    final GameSettings options;
    SoundSystem sndSystem;
    boolean loaded;
    private int playTime = 0;
    private final Map<String, ISound> playingSounds = HashBiMap.create();
    private final Map<ISound, String> invPlayingSounds;
    private Map<ISound, SoundPoolEntry> playingSoundPoolEntries;
    private final Multimap<SoundCategory, String> categorySounds;
    private final List<ITickableSound> tickableSounds;
    private final Map<ISound, Integer> delayedSounds;
    private final Map<String, Integer> playingSoundsStopTime;
    private final Object soundMapLock = new Object();

    public SoundManager(SoundHandler p_i45119_1_, GameSettings p_i45119_2_) {
        this.invPlayingSounds = ((BiMap)this.playingSounds).inverse();
        this.playingSoundPoolEntries = Maps.newHashMap();
        this.categorySounds = HashMultimap.create();
        this.tickableSounds = Lists.newArrayList();
        this.delayedSounds = Maps.newHashMap();
        this.playingSoundsStopTime = Maps.newHashMap();
        this.sndHandler = p_i45119_1_;
        this.options = p_i45119_2_;

        try {
            SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
            SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
        } catch (SoundSystemException e) {
            logger.error(LOG_MARKER, "Error linking with the LibraryJavaSound plug-in", (Throwable)e);
        }
    }

    public void reloadSoundSystem() {
        this.unloadSoundSystem();
        this.loadSoundSystem();
    }

    private synchronized void loadSoundSystem() {
        if (!this.loaded) {
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SoundSystemConfig.setLogger(new SoundSystemLogger() {
                            @Override
                            public void message(String p_message_1_, int p_message_2_) {
                                if (!p_message_1_.isEmpty()) {
                                    SoundManager.logger.info(p_message_1_);
                                }
                            }

                            @Override
                            public void importantMessage(String p_importantMessage_1_, int p_importantMessage_2_) {
                                if (!p_importantMessage_1_.isEmpty()) {
                                    SoundManager.logger.warn(p_importantMessage_1_);
                                }
                            }

                            @Override
                            public void errorMessage(String p_errorMessage_1_, String p_errorMessage_2_, int p_errorMessage_3_) {
                                if (!p_errorMessage_2_.isEmpty()) {
                                    SoundManager.logger.error("Error in class '" + p_errorMessage_1_ + "'");
                                    SoundManager.logger.error(p_errorMessage_2_);
                                }
                            }
                        });
                        SoundManager.this.sndSystem = SoundManager.this.new SoundSystemStarterThread();
                        SoundManager.this.loaded = true;
                        SoundManager.this.sndSystem.setMasterVolume(SoundManager.this.options.getSoundLevel(SoundCategory.MASTER));
                        SoundManager.logger.info(SoundManager.LOG_MARKER, "Sound engine started");
                    }
                }, "Sound Library Loader").start();
            } catch (RuntimeException e) {
                logger.error(LOG_MARKER, "Error starting SoundSystem. Turning off sounds & music", (Throwable)e);
                this.options.setSoundLevel(SoundCategory.MASTER, 0.0F);
                this.options.saveOptions();
            }
        }
    }

    private float getSoundCategoryVolume(SoundCategory category) {
        return category != null && category != SoundCategory.MASTER ? this.options.getSoundLevel(category) : 1.0F;
    }

    public void setSoundCategoryVolume(SoundCategory category, float volume) {
        if (this.loaded) {
            if (category == SoundCategory.MASTER) {
                this.sndSystem.setMasterVolume(volume);
            } else {
                synchronized (this.soundMapLock) {
                    for (String str : this.categorySounds.get(category)) {
                        ISound iSound = this.playingSounds.get(str);
                        if (iSound != null) {
                            float normalizedVolume = this.getNormalizedVolume(iSound, this.playingSoundPoolEntries.get(iSound), category);
                            if (normalizedVolume <= 0.0F) {
                                this.stopSound(iSound);
                            } else {
                                this.sndSystem.setVolume(str, normalizedVolume);
                            }
                        }
                    }
                }
            }
        }
    }

    public void unloadSoundSystem() {
        if (this.loaded) {
            this.stopAllSounds();
            this.sndSystem.cleanup();
            this.loaded = false;
        }
    }

    public void stopAllSounds() {
        if (this.loaded) {
            synchronized (this.soundMapLock) {
                for (String str : this.playingSounds.keySet()) {
                    this.sndSystem.stop(str);
                }

                this.playingSounds.clear();
                this.delayedSounds.clear();
                this.tickableSounds.clear();
                this.categorySounds.clear();
                this.playingSoundPoolEntries.clear();
                this.playingSoundsStopTime.clear();
            }
        }
    }

    public void updateAllSounds() {
        this.playTime++;
        synchronized (this.soundMapLock) {
            for (ITickableSound tickableSound : this.tickableSounds) {
                tickableSound.update();
                if (tickableSound.isDonePlaying()) {
                    this.stopSoundInternal(tickableSound);
                } else {
                    String str = this.invPlayingSounds.get(tickableSound);
                    if (str != null) {
                        SoundPoolEntry soundPoolEntry = this.playingSoundPoolEntries.get(tickableSound);
                        if (soundPoolEntry != null) {
                            this.sndSystem
                                .setVolume(str, this.getNormalizedVolume(tickableSound, soundPoolEntry, this.sndHandler.getSound(tickableSound.getSoundLocation()).getSoundCategory()));
                            this.sndSystem.setPitch(str, this.getNormalizedPitch(tickableSound, soundPoolEntry));
                            this.sndSystem.setPosition(str, tickableSound.getXPosF(), tickableSound.getYPosF(), tickableSound.getZPosF());
                        }
                    }
                }
            }
        }

        synchronized (this.soundMapLock) {
            Iterator it = this.playingSounds.entrySet().iterator();

            while (it.hasNext()) {
                Entry entry = (Entry)it.next();
                String key = (String)entry.getKey();
                ISound val = (ISound)entry.getValue();
                if (!this.sndSystem.playing(key)) {
                    Integer integer = this.playingSoundsStopTime.get(key);
                    if (integer != null) {
                        int n = integer;
                        if (n <= this.playTime) {
                            int repeatDelay = val.getRepeatDelay();
                            if (val.canRepeat() && repeatDelay > 0) {
                                this.delayedSounds.put(val, this.playTime + repeatDelay);
                            }

                            it.remove();
                            logger.debug(LOG_MARKER, "Removed channel {} because it's not playing anymore", key);
                            this.sndSystem.removeSource(key);
                            this.playingSoundsStopTime.remove(key);
                            this.playingSoundPoolEntries.remove(val);

                            try {
                                this.categorySounds.remove(this.sndHandler.getSound(val.getSoundLocation()).getSoundCategory(), key);
                            } catch (RuntimeException ignored) {
                            }

                            if (val instanceof ITickableSound) {
                                this.tickableSounds.remove(val);
                            }
                        }
                    }
                }
            }
        }

        Iterator it2 = this.delayedSounds.entrySet().iterator();

        while (it2.hasNext()) {
            Entry entry2 = (Entry)it2.next();
            if (this.playTime >= (Integer)entry2.getValue()) {
                ISound key2 = (ISound)entry2.getKey();
                if (key2 instanceof ITickableSound) {
                    ((ITickableSound)key2).update();
                }

                this.playSound(key2);
                it2.remove();
            }
        }
    }

    public boolean isSoundPlaying(ISound sound) {
        if (!this.loaded) {
            return false;
        }
        synchronized (this.soundMapLock) {
            String str = this.invPlayingSounds.get(sound);
            return str == null
                ? false
                : this.sndSystem.playing(str) || this.playingSoundsStopTime.containsKey(str) && this.playingSoundsStopTime.get(str) <= this.playTime;
        }
    }

    public void stopSound(ISound sound) {
        if (this.loaded) {
            synchronized (this.soundMapLock) {
                this.stopSoundInternal(sound);
            }
        }
    }

    private void stopSoundInternal(ISound sound) {
        String str = this.invPlayingSounds.get(sound);
        if (str != null) {
            this.sndSystem.stop(str);
        }
    }

    public void playSound(ISound sound) {
        if (sound != null) {
            if (sound.getSoundLocation().getResourcePath().trim().isEmpty()) {
                new ImplEvent.Entry(this, sound, "empty sound identifier").process();
            } else if (!this.loaded) {
                new ImplEvent.Entry(this, sound, "sound engine is not ready").process();
            } else if (SoundRemoverHelper.matchesISound(sound)) {
                new ImplEvent.Entry(this, sound, "suppressed by Sound Remover").process();
            } else {
                if (this.loaded) {
                    if (this.sndSystem.getMasterVolume() <= 0.0F) {
                        logger.debug(LOG_MARKER, "Skipped playing soundEvent: {}, master volume was zero", sound.getSoundLocation());
                        new ImplEvent.Entry(this, sound, "master volume is zero").process();
                    } else {
                        SoundEventAccessorComposite sound2 = this.sndHandler.getSound(sound.getSoundLocation());
                        if (sound2 == null) {
                            logger.warn(LOG_MARKER, "Unable to play unknown soundEvent: {}", sound.getSoundLocation());
                            new ImplEvent.Entry(this, sound, "unregistered sound event").process();
                        } else {
                            SoundPoolEntry soundPoolEntry = sound2.cloneEntry();
                            if (soundPoolEntry == SoundHandler.missing_sound) {
                                logger.warn(LOG_MARKER, "Unable to play empty soundEvent: {}", sound2.getSoundEventLocation());
                                new ImplEvent.Entry(this, sound, "empty sound event").process();
                            } else {
                                float volume = sound.getVolume();
                                float f = 16.0F;
                                if (volume > 1.0F) {
                                    f *= volume;
                                }

                                SoundCategory soundCategory = sound2.getSoundCategory();
                                float normalizedVolume = this.getNormalizedVolume(sound, soundPoolEntry, soundCategory);
                                double normalizedPitch = this.getNormalizedPitch(sound, soundPoolEntry);
                                ResourceLocation soundPoolEntryLocation = soundPoolEntry.getSoundPoolEntryLocation();
                                if (normalizedVolume == 0.0F) {
                                    logger.debug(LOG_MARKER, "Skipped playing sound {}, volume was zero.", soundPoolEntryLocation);
                                    new ImplEvent.Entry(this, sound, "category/sound volume is zero").process();
                                } else {
                                    boolean flag = sound.canRepeat() && sound.getRepeatDelay() == 0;
                                    String str = MathHelper.getRandomUuid(ThreadLocalRandom.current()).toString();
                                    if (soundPoolEntry.isStreamingSound()) {
                                        this.sndSystem
                                            .newStreamingSource(
                                                false,
                                                str,
                                                getURLForSoundResource(soundPoolEntryLocation),
                                                soundPoolEntryLocation.toString(),
                                                flag,
                                                sound.getXPosF(),
                                                sound.getYPosF(),
                                                sound.getZPosF(),
                                                sound.getAttenuationType().getTypeInt(),
                                                f
                                            );
                                        new ImplEvent.Entry2(this, sound, str, soundPoolEntryLocation, true).process();
                                    } else {
                                        this.sndSystem
                                            .newSource(
                                                false,
                                                str,
                                                getURLForSoundResource(soundPoolEntryLocation),
                                                soundPoolEntryLocation.toString(),
                                                flag,
                                                sound.getXPosF(),
                                                sound.getYPosF(),
                                                sound.getZPosF(),
                                                sound.getAttenuationType().getTypeInt(),
                                                f
                                            );
                                        new ImplEvent.Entry2(this, sound, str, soundPoolEntryLocation, false).process();
                                    }

                                    logger.debug(
                                        LOG_MARKER,
                                        "Playing sound {} for event {} as channel {}",
                                        soundPoolEntry.getSoundPoolEntryLocation(),
                                        sound2.getSoundEventLocation(),
                                        str
                                    );
                                    this.sndSystem.setPitch(str, (float)normalizedPitch);
                                    this.sndSystem.setVolume(str, normalizedVolume);
                                    this.sndSystem.play(str);
                                    synchronized (this.soundMapLock) {
                                        this.playingSoundsStopTime.put(str, this.playTime + 20);
                                        this.playingSounds.put(str, sound);
                                        this.playingSoundPoolEntries.put(sound, soundPoolEntry);
                                        if (soundCategory != SoundCategory.MASTER) {
                                            this.categorySounds.put(soundCategory, str);
                                        }

                                        if (sound instanceof ITickableSound) {
                                            this.tickableSounds.add((ITickableSound)sound);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private float getNormalizedPitch(ISound sound, SoundPoolEntry entry) {
        return (float)MathHelper.clamp_double(sound.getPitch() * entry.getPitch(), 0.5, 2.0);
    }

    private float getNormalizedVolume(ISound sound, SoundPoolEntry entry, SoundCategory category) {
        return (float)MathHelper.clamp_double(sound.getVolume() * entry.getVolume(), 0.0, 1.0) * this.getSoundCategoryVolume(category);
    }

    public void pauseAllSounds() {
        synchronized (this.soundMapLock) {
            for (String str : this.playingSounds.keySet()) {
                logger.debug(LOG_MARKER, "Pausing channel {}", str);
                this.sndSystem.pause(str);
            }
        }
    }

    public void resumeAllSounds() {
        synchronized (this.soundMapLock) {
            for (String str : this.playingSounds.keySet()) {
                logger.debug(LOG_MARKER, "Resuming channel {}", str);
                this.sndSystem.play(str);
            }
        }
    }

    public void playDelayedSound(ISound sound, int delay) {
        this.delayedSounds.put(sound, this.playTime + delay);
    }

    private static URL getURLForSoundResource(final ResourceLocation p_148612_0_) {
        String str = String.format("%s:%s:%s", "mcsounddomain", p_148612_0_.getResourceDomain(), p_148612_0_.getResourcePath());
        URLStreamHandler urlStreamHandler = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL p_openConnection_1_) {
                return new URLConnection(p_openConnection_1_) {
                    @Override
                    public void connect() throws IOException {
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return Minecraft.getMinecraft().getResourceManager().getResource(p_148612_0_).getInputStream();
                    }
                };
            }
        };

        try {
            return new URL((URL)null, str, urlStreamHandler);
        } catch (MalformedURLException e) {
            throw new Error("TODO: Sanely handle url exception! :D");
        }
    }

    public void setListener(EntityPlayer player, float p_148615_2_) {
        if (this.loaded && player != null) {
            float f = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * p_148615_2_;
            float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * p_148615_2_;
            double d = player.prevPosX + (player.posX - player.prevPosX) * p_148615_2_;
            double d2 = player.prevPosY + (player.posY - player.prevPosY) * p_148615_2_ + player.getEyeHeight();
            double d3 = player.prevPosZ + (player.posZ - player.prevPosZ) * p_148615_2_;
            float cos = MathHelper.cos((f2 + 90.0F) * (float) (Math.PI / 180.0));
            float sin = MathHelper.sin((f2 + 90.0F) * (float) (Math.PI / 180.0));
            float cos2 = MathHelper.cos(-f * (float) (Math.PI / 180.0));
            float sin2 = MathHelper.sin(-f * (float) (Math.PI / 180.0));
            float cos3 = MathHelper.cos((-f + 90.0F) * (float) (Math.PI / 180.0));
            float sin3 = MathHelper.sin((-f + 90.0F) * (float) (Math.PI / 180.0));
            float f3 = cos * cos2;
            float f4 = sin * cos2;
            float f5 = cos * cos3;
            float f6 = sin * cos3;
            this.sndSystem.setListenerPosition((float)d, (float)d2, (float)d3);
            this.sndSystem.setListenerOrientation(f3, sin2, f4, f5, sin3, f6);
        }
    }

    class SoundSystemStarterThread extends SoundSystem {
        @Override
        public boolean playing(String p_playing_1_) {
            synchronized (SoundSystemConfig.THREAD_SYNC) {
                if (this.soundLibrary == null) {
                    return false;
                }
                Source source = this.soundLibrary.getSources().get(p_playing_1_);
                return source == null ? false : source.playing() || source.paused() || source.preLoad;
            }
        }
    }
}
