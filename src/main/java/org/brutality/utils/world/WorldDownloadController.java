package org.brutality.utils.world;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.storage.WorldInfo;
import org.brutality.utils.MessageUtils;

public final class WorldDownloadController {
    private static WorldDownloadController.Entry entry;
    private static final int count = 512;

    public static void stop() {
        WorldDownloadController.Entry entry2 = entry;
        if (entry2 != null && !entry2.flag2) {
            entry2.flag2 = true;
            entry2.iterator = ((ChunkProviderClient)entry2.worldClient.getChunkProvider()).getLoadedChunks().iterator();
            message("Finishing world download…");
        }
    }

    public static void tick() {
        WorldDownloadController.Entry entry2 = entry;
        if (entry2 != null) {
            if (entry2.worldDownloadWorker.finished) {
                String str;
                if (entry2.worldDownloadWorker.text != null) {
                    str = entry2.worldDownloadWorker.text;
                } else {
                    String str2 = entry2.count > 0 ? ", " + entry2.count + " missed snapshots; revisit affected areas" : "";
                    long timestamp = entry2.worldDownloadWorker.timestamp;
                    Path path = entry2.worldDownloadWorker.path;
                    str = "Saved world: " + path + " (" + timestamp + " chunk writes" + str2 + ").";
                }

                message(str);
                entry2.linkedHashMap.clear();
                entry2.iterator = null;
                entry2.worldClient = null;
                entry = null;
            } else {
                long l = System.nanoTime() + 4000000L;

                for (int i = 0; i < 4 && entry2.worldDownloadWorker.checkState() && System.nanoTime() < l; i++) {
                    Chunk chunk = null;
                    if (!entry2.linkedHashMap.isEmpty()) {
                        Iterator it = entry2.linkedHashMap.values().iterator();
                        chunk = (Chunk)it.next();
                        it.remove();
                    } else if (entry2.iterator != null && entry2.iterator.hasNext()) {
                        chunk = entry2.iterator.next();
                        if (!entry2.flag2 && entry2.worldClient.getChunkProvider().provideChunk(chunk.xPosition, chunk.zPosition) != chunk) {
                            continue;
                        }
                    }

                    if (chunk == null) {
                        entry2.iterator = null;
                        if (entry2.flag2) {
                            entry2.worldClient = null;
                            entry2.worldDownloadWorker.handleInt(entry2.count);
                        }
                        break;
                    }

                    if (!(chunk instanceof EmptyChunk)) {
                        try {
                            if (!entry2.worldDownloadWorker
                                .matchesAnvilChunkSerializer(AnvilChunkSerializer.computeAnvilChunkSerializer(entry2.worldClient, chunk, entry2.flag))) {
                                entry2.count++;
                            }
                        } catch (RuntimeException e) {
                            entry2.count++;
                        }
                    }
                }
            }
        }
    }

    private WorldDownloadController() {
    }

    public static boolean isEntry() {
        return entry != null;
    }

    public static void handleWorld(World world, Chunk chunk) {
        WorldDownloadController.Entry entry2 = entry;
        if (entry2 != null && !entry2.flag2 && entry2.worldClient == world && !(chunk instanceof EmptyChunk) && !entry2.worldDownloadWorker.finished) {
            long l = (long)chunk.xPosition << 32 ^ chunk.zPosition & 4294967295L;
            if (!entry2.linkedHashMap.containsKey(l) && entry2.linkedHashMap.size() == 512) {
                entry2.count++;
                if (!entry2.warned) {
                    entry2.warned = true;
                    message("Disk saving is falling behind. Pause exploring until it catches up; some chunks may need revisiting.");
                }
            } else {
                entry2.linkedHashMap.put(l, chunk);
            }
        }
    }

    public static void shutdown() {
        stop();
        long l = System.nanoTime() + 15000000000L;

        while (entry != null && System.nanoTime() < l) {
            tick();

            try {
                Thread.sleep(5L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (entry != null) {
            WorldDownloadController.Entry entry2 = entry;
            int size = entry2.linkedHashMap.size();
            if (entry2.iterator != null) {
                while (entry2.iterator.hasNext()) {
                    entry2.iterator.next();
                    size++;
                }
            }

            entry2.worldDownloadWorker.handleInt(entry2.count + size);
            entry2.linkedHashMap.clear();
            entry2.iterator = null;
            entry2.worldClient = null;

            try {
                entry2.worldDownloadWorker.handleLong(5000L);
            } catch (InterruptedException e2) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void message(String str) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            MessageUtils.x("[World Downloader] " + str);
        }
    }

    public static boolean matchesWorldClient(WorldClient world, BlockPos pos, String str, boolean flag) {
        if (entry != null) {
            message("Previous recording is still finishing. Please wait.");
            return false;
        }
        Path path = Minecraft.getMinecraft().mcDataDir.toPath().resolve("Brutality/WorldDownloads").toAbsolutePath().normalize();
        String str2 = str == null ? "World" : str.replaceAll("[^a-zA-Z0-9_-]", "_");
        if (str2.isEmpty()) {
            str2 = "World";
        }

        if (str2.length() > 48) {
            str2 = str2.substring(0, 48);
        }

        Path path2 = path.resolve(str2 + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8));
        if (!path2.getParent().equals(path)) {
            throw new IllegalArgumentException("Invalid export path");
        }
        WorldInfo worldInfo = new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.FLAT), str2);
        worldInfo.setSpawn(pos);
        worldInfo.setSaveVersion(19133);
        worldInfo.setServerInitialized(true);
        worldInfo.setAllowCommands(true);
        worldInfo.setWorldTime(world.getWorldTime());
        worldInfo.setWorldTotalTime(world.getTotalWorldTime());
        NBTTagCompound nbt = worldInfo.cloneNBTCompound(null);
        nbt.setString("generatorOptions", "3;1*minecraft:air;1;");
        nbt.setInteger("BrutalitySourceDimension", world.provider.getDimensionId());
        NBTTagCompound nbt2 = new NBTTagCompound();
        nbt2.setTag("Data", nbt);
        entry = new WorldDownloadController.Entry(world, new WorldDownloadWorker(path2, nbt2), flag);
        message("Recording loaded chunks. Explore the map, then turn World Downloader off to finish. Folder: " + path2.getFileName());
        return true;
    }

    public static final class Entry {
        public WorldClient worldClient;
        public final WorldDownloadWorker worldDownloadWorker;
        public final boolean flag;
        public final LinkedHashMap<Long, Chunk> linkedHashMap = new LinkedHashMap<>();
        public Iterator<Chunk> iterator;
        public boolean flag2;
        public boolean warned;
        public int count;

        public Entry(WorldClient worldClient, WorldDownloadWorker worldDownloadWorker, boolean flag) {
            this.worldClient = worldClient;
            this.worldDownloadWorker = worldDownloadWorker;
            this.flag = flag;
            this.iterator = ((ChunkProviderClient)worldClient.getChunkProvider()).getLoadedChunks().iterator();
        }
    }
}
