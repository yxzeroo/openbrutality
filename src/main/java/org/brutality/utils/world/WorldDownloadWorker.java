package org.brutality.utils.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.storage.RegionFile;

public final class WorldDownloadWorker {
    public static final int count = 64;
    private final ArrayBlockingQueue<AnvilChunkSerializer> arrayBlockingQueue = new ArrayBlockingQueue<>(64);
    private final LinkedHashMap<Long, RegionFile> linkedHashMap = new LinkedHashMap<>(16, 0.75F, true);
    public final Path path;
    private final NBTTagCompound nbtTagCompound;
    private final Thread thread;
    private volatile boolean flag;
    private boolean flag2;
    public volatile boolean finished;
    public volatile String text;
    public volatile long timestamp;
    public volatile int count2;

    public boolean matchesAnvilChunkSerializer(AnvilChunkSerializer anvilChunkSerializer) {
        return !this.flag && !this.finished && this.arrayBlockingQueue.offer(anvilChunkSerializer);
    }

    public WorldDownloadWorker(Path path, NBTTagCompound nbtTagCompound) {
        this.path = path;
        this.nbtTagCompound = nbtTagCompound;
        this.thread = new Thread(this::run, "Brutality World Downloader");
        this.thread.setDaemon(true);
        this.thread.start();
    }

    private void run() {
        try {
            Files.createDirectories(this.path.getParent());
            Files.createDirectory(this.path);
            this.flag2 = true;
            Files.createDirectory(this.path.resolve("region"));

            try (OutputStream out = Files.newOutputStream(this.path.resolve("level.dat"))) {
                CompressedStreamTools.writeCompressed(this.nbtTagCompound, out);
            }

            Files.write(
                this.path.resolve("README.txt"),
                "Brutality World Downloader - Minecraft 1.8 / Spigot 1.8.8\nWait for the completed message before copying this folder.\nStop Spigot, back up its existing world, and copy this folder as a NEW world.\nSet level-name in server.properties to this folder name, level-type=FLAT, and generator-settings=3;1*minecraft:air;1;\nOnly received chunks are captured. Unvisited areas generate empty terrain.\nThe current dimension is exported as a standalone normal world at its original coordinates.\nServer plugins, NPC behavior and the original world seed are not copied.\nContainer inventories may be missing; only data present in client block entities is saved.\nClient-side block changes will also appear in the export.\n"
                    .getBytes(StandardCharsets.UTF_8)
            );

            while (!this.flag || !this.arrayBlockingQueue.isEmpty()) {
                AnvilChunkSerializer anvilChunkSerializer = this.arrayBlockingQueue.poll(100L, TimeUnit.MILLISECONDS);
                if (anvilChunkSerializer != null) {
                    RegionFile regionFile = this.computeRegionFile(anvilChunkSerializer.count, anvilChunkSerializer.count2);

                    try (DataOutputStream chunkDataOutputStream = regionFile.getChunkDataOutputStream(anvilChunkSerializer.count & 31, anvilChunkSerializer.count2 & 31)) {
                        CompressedStreamTools.write(anvilChunkSerializer.nbtTagCompound, chunkDataOutputStream);
                    }

                    try (DataInputStream chunkDataInputStream = regionFile.getChunkDataInputStream((31 | ~anvilChunkSerializer.count) - ~anvilChunkSerializer.count, anvilChunkSerializer.count2 & 31)) {
                        if (chunkDataInputStream == null || !anvilChunkSerializer.nbtTagCompound.equals(CompressedStreamTools.read(chunkDataInputStream))) {
                            throw new IOException("Region write verification failed");
                        }
                    }

                    this.timestamp++;
                }
            }

            Path path = this.path.resolve("capture-status.txt");
            int n = this.count2;
            long timestamp = this.timestamp;
            Files.write(path, ("Complete\nChunk writes: " + timestamp + "\nMissed snapshots: " + n + "\n").getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            this.text = "World download could not finish writing. Check disk space and folder permissions.";
            if (this.flag2) {
                try {
                    Files.write(this.path.resolve("capture-status.txt"), ("Incomplete\n" + this.text + "\n").getBytes(StandardCharsets.UTF_8));
                } catch (IOException ignored) {
                }
            }
        } finally {
            for (RegionFile regionFile2 : this.linkedHashMap.values()) {
                try {
                    regionFile2.close();
                } catch (IOException ignored2) {
                }
            }

            this.linkedHashMap.clear();
            this.arrayBlockingQueue.clear();
            this.finished = true;
        }
    }

    public void handleLong(long l) throws InterruptedException {
        this.thread.join(l);
    }

    public void handleInt(int n) {
        this.count2 = n;
        this.flag = true;
    }

    public boolean checkState() {
        return this.arrayBlockingQueue.remainingCapacity() > 0 && !this.finished;
    }

    private RegionFile computeRegionFile(int n, int n2) throws IOException {
        long l = (long)(n >> 5) << 32 ^ n2 >> 5 & 4294967295L;
        RegionFile regionFile = this.linkedHashMap.get(l);
        if (regionFile == null) {
            if (this.linkedHashMap.size() == 8) {
                Iterator it = this.linkedHashMap.values().iterator();
                RegionFile regionFile2 = (RegionFile)it.next();
                it.remove();
                regionFile2.close();
            }

            regionFile = new RegionFile(this.path.resolve("region/r." + (n >> 5) + "." + (n2 >> 5) + ".mca").toFile());
            this.linkedHashMap.put(l, regionFile);
        }

        return regionFile;
    }
}
