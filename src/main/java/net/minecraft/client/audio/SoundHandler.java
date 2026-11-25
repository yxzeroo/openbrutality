package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.client.audio.SoundList.SoundEntry;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundHandler implements IResourceManagerReloadListener, ITickable {
    private static final Logger logger = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
    private static final ParameterizedType TYPE = new ParameterizedType() {
        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{String.class, SoundList.class};
        }

        @Override
        public Type getRawType() {
            return Map.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    };
    public static final SoundPoolEntry missing_sound = new SoundPoolEntry(new ResourceLocation("meta:missing_sound"), 0.0, 0.0, false);
    final SoundRegistry sndRegistry = new SoundRegistry();
    public final SoundManager sndManager;
    private final IResourceManager mcResourceManager;

    public SoundHandler(IResourceManager manager, GameSettings gameSettingsIn) {
        this.mcResourceManager = manager;
        this.sndManager = new SoundManager(this, gameSettingsIn);
    }

    public void registerMusicTrack(ResourceLocation event, ResourceLocation file) throws IOException {
        InputStream inputStream = this.mcResourceManager.getResource(file).getInputStream();
        if (inputStream != null) {
            inputStream.close();
        }

        SoundEventAccessorComposite soundEventAccessorComposite = new SoundEventAccessorComposite(event, 1.0, 1.0, SoundCategory.MASTER);
        soundEventAccessorComposite.addSoundToEventPool(new SoundEventAccessor(new SoundPoolEntry(file, 1.0, 1.0, true), 1));
        this.sndRegistry.registerSound(soundEventAccessorComposite);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.sndManager.reloadSoundSystem();
        this.sndRegistry.clearMap();

        for (String resourceDomain : resourceManager.getResourceDomains()) {
            try {
                for (IResource iResource : resourceManager.getAllResources(new ResourceLocation(resourceDomain, "sounds.json"))) {
                    try {
                        var soundMap = this.getSoundMap(iResource.getInputStream());

                        for (Entry entry : soundMap.entrySet()) {
                            this.loadSoundResource(new ResourceLocation(resourceDomain, (String)entry.getKey()), (SoundList)entry.getValue());
                        }
                    } catch (RuntimeException e) {
                        logger.warn("Invalid sounds.json", (Throwable)e);
                    }
                }
            } catch (IOException ignored) {
            }
        }
    }

    protected Map<String, SoundList> getSoundMap(InputStream stream) {
        Map map;
        try {
            map = GSON.fromJson(new InputStreamReader(stream), TYPE);
        } finally {
            IOUtils.closeQuietly(stream);
        }

        return map;
    }

    private void loadSoundResource(ResourceLocation location, SoundList sounds) {
        boolean flag = !this.sndRegistry.containsKey(location);
        SoundEventAccessorComposite soundEventAccessorComposite;
        if (!flag && !sounds.canReplaceExisting()) {
            soundEventAccessorComposite = this.sndRegistry.getObject(location);
        } else {
            if (!flag) {
                logger.debug("Replaced sound event location {}", location);
            }

            soundEventAccessorComposite = new SoundEventAccessorComposite(location, 1.0, 1.0, sounds.getSoundCategory());
            this.sndRegistry.registerSound(soundEventAccessorComposite);
        }

        for (final SoundEntry soundEntry : sounds.getSoundList()) {
            String soundEntryName = soundEntry.getSoundEntryName();
            ResourceLocation location2 = new ResourceLocation(soundEntryName);
            final String str = soundEntryName.contains(":") ? location2.getResourceDomain() : location.getResourceDomain();
            Object obj;
            switch (soundEntry.getSoundEntryType()) {
                case FILE:
                    ResourceLocation location3 = new ResourceLocation(str, "sounds/" + location2.getResourcePath() + ".ogg");
                    InputStream in = null;

                    try {
                        in = this.mcResourceManager.getResource(location3).getInputStream();
                    } catch (FileNotFoundException e) {
                        logger.warn("File {} does not exist, cannot add it to event {}", location3, location);
                        continue;
                    } catch (IOException e2) {
                        logger.warn("Could not load sound file " + location3 + ", cannot add it to event " + location, (Throwable)e2);
                        continue;
                    } finally {
                        IOUtils.closeQuietly(in);
                    }

                    obj = new SoundEventAccessor(
                        new SoundPoolEntry(location3, soundEntry.getSoundEntryPitch(), soundEntry.getSoundEntryVolume(), soundEntry.isStreaming()), soundEntry.getSoundEntryWeight()
                    );
                    break;
                case SOUND_EVENT:
                    obj = new ISoundEventAccessor<SoundPoolEntry>() {
                        final ResourceLocation field_148726_a = new ResourceLocation(str, soundEntry.getSoundEntryName());

                        @Override
                        public int getWeight() {
                            SoundEventAccessorComposite soundEventAccessorComposite2 = SoundHandler.this.sndRegistry.getObject(this.field_148726_a);
                            return soundEventAccessorComposite2 == null ? 0 : soundEventAccessorComposite2.getWeight();
                        }

                        public SoundPoolEntry cloneEntry() {
                            SoundEventAccessorComposite soundEventAccessorComposite3 = SoundHandler.this.sndRegistry.getObject(this.field_148726_a);
                            return soundEventAccessorComposite3 == null ? SoundHandler.missing_sound : soundEventAccessorComposite3.cloneEntry();
                        }
                    };
                    break;
                default:
                    throw new IllegalStateException("IN YOU FACE");
            }

            soundEventAccessorComposite.addSoundToEventPool((ISoundEventAccessor<SoundPoolEntry>)obj);
        }
    }

    public SoundEventAccessorComposite getSound(ResourceLocation location) {
        return this.sndRegistry.getObject(location);
    }

    public void playSound(ISound sound) {
        this.sndManager.playSound(sound);
    }

    public void playDelayedSound(ISound sound, int delay) {
        this.sndManager.playDelayedSound(sound, delay);
    }

    public void setListener(EntityPlayer player, float p_147691_2_) {
        this.sndManager.setListener(player, p_147691_2_);
    }

    public void pauseSounds() {
        this.sndManager.pauseAllSounds();
    }

    public void stopSounds() {
        this.sndManager.stopAllSounds();
    }

    public void unloadSounds() {
        this.sndManager.unloadSoundSystem();
    }

    @Override
    public void update() {
        this.sndManager.updateAllSounds();
    }

    public void resumeSounds() {
        this.sndManager.resumeAllSounds();
    }

    public void setSoundLevel(SoundCategory category, float volume) {
        if (category == SoundCategory.MASTER && volume <= 0.0F) {
            this.stopSounds();
        }

        this.sndManager.setSoundCategoryVolume(category, volume);
    }

    public void stopSound(ISound p_147683_1_) {
        this.sndManager.stopSound(p_147683_1_);
    }

    public SoundEventAccessorComposite getRandomSoundFromCategories(SoundCategory... categories) {
        java.util.List list = Lists.newArrayList();

        for (ResourceLocation key : this.sndRegistry.getKeys()) {
            SoundEventAccessorComposite soundEventAccessorComposite = this.sndRegistry.getObject(key);
            if (ArrayUtils.contains(categories, soundEventAccessorComposite.getSoundCategory())) {
                list.add(soundEventAccessorComposite);
            }
        }

        return list.isEmpty() ? null : (SoundEventAccessorComposite)list.get(new Random().nextInt(list.size()));
    }

    public boolean isSoundPlaying(ISound sound) {
        return this.sndManager.isSoundPlaying(sound);
    }
}
