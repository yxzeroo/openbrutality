package org.brutality.module.impl.pit.hypixel.utility.spire;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S22PacketMultiBlockChange.BlockUpdateData;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;

public final class SizeHelper {
    private static final Map<BlockPos, SizeHelper.Entry> lookupMap = new LinkedHashMap<>();
    private static volatile boolean active;
    private static long timestamp;
    private static long timestamp2;
    private static long timestamp3;
    private static Set<String> itemSet = new HashSet<>();
    private static String text = "";
    private static Packet<?> packet;

    private static void handleDataOutputStream(DataOutputStream dataOutputStream, String str) throws IOException {
        dataOutputStream.writeByte(9);
        dataOutputStream.writeUTF(str);
        dataOutputStream.writeByte(10);
        dataOutputStream.writeInt(0);
    }

    private SizeHelper() {
    }

    public static synchronized long getTimestamp2() {
        return timestamp2;
    }

    public static boolean isActive() {
        return active;
    }

    private static void handleDataOutputStream(DataOutputStream dataOutputStream, String str, int n) throws IOException {
        dataOutputStream.writeByte(3);
        dataOutputStream.writeUTF(str);
        dataOutputStream.writeInt(n);
    }

    private static boolean matchesBlockPos(BlockPos pos, IBlockState blockState) {
        long now = System.currentTimeMillis();
        if (matchesIBlockState(blockState)) {
            if (timestamp == 0L) {
                timestamp = now;
            }

            timestamp2 = now;
            SizeHelper.Entry entry = lookupMap.get(pos);
            lookupMap.put(pos, new SizeHelper.Entry(blockState, entry != null ? entry.timestamp : now - timestamp));
            return true;
        }
        lookupMap.remove(pos);
        return false;
    }

    public static synchronized void handleString(String text2) {
        if (text2 != null && !text2.equals(text)) {
            text = text2;
            HashSet set = new HashSet();

            for (String str : text2.split(",")) {
                String str2 = str.trim().toLowerCase(Locale.ROOT);
                if (str2.startsWith("minecraft:")) {
                    str2 = str2.substring("minecraft:".length());
                }

                if (!str2.isEmpty()) {
                    set.add(str2);
                }
            }

            itemSet = set;
        }
    }

    public static synchronized void tick() {
        String str = BossStatus.bossName == null ? "" : EnumChatFormatting.getTextWithoutFormattingCodes(BossStatus.bossName);
        boolean flag = BossStatus.statusBarTime > 0 && str.toUpperCase(Locale.ROOT).contains("SPIRE");
        if (flag && !active) {
            active = true;
            lookupMap.clear();
            timestamp2 = 0L;
            timestamp = 0L;
            timestamp3 = 0L;
        } else if (!flag && active) {
            active = false;
            timestamp3 = System.currentTimeMillis();
        }
    }

    public static synchronized int size() {
        return lookupMap.size();
    }

    private static void handleDataOutputStream2(DataOutputStream dataOutputStream, String str, int n) throws IOException {
        dataOutputStream.writeByte(2);
        dataOutputStream.writeUTF(str);
        dataOutputStream.writeShort(n);
    }

    private static List<BlockPos> computeList(Packet<?> packet) {
        List list = new ArrayList();
        if (packet instanceof S23PacketBlockChange) {
            BlockPos blockPosition = ((S23PacketBlockChange)packet).getBlockPosition();
            if (lookupMap.containsKey(blockPosition)) {
                list.add(blockPosition);
            }
        } else if (packet instanceof S22PacketMultiBlockChange) {
            for (BlockUpdateData changedBlock : ((S22PacketMultiBlockChange)packet).getChangedBlocks()) {
                if (changedBlock != null && lookupMap.containsKey(changedBlock.getPos())) {
                    list.add(changedBlock.getPos());
                }
            }
        }

        return list;
    }

    private static boolean matchesIBlockState(IBlockState blockState) {
        if (blockState != null && blockState.getBlock() != Blocks.air) {
            ResourceLocation nameForObject = Block.blockRegistry.getNameForObject(blockState.getBlock());
            return nameForObject == null || !itemSet.contains(nameForObject.getResourcePath().toLowerCase(Locale.ROOT));
        }
        return false;
    }

    public static synchronized List<BlockPos> computeList2(Packet<?> packet2) {
        List list = new ArrayList();
        if (!active) {
            return list;
        }
        if (packet2 == packet) {
            return computeList(packet2);
        }
        packet = packet2;
        if (packet2 instanceof S23PacketBlockChange packet3) {
            if (matchesBlockPos(packet3.getBlockPosition(), packet3.getBlockState())) {
                list.add(packet3.getBlockPosition());
            }
        } else if (packet2 instanceof S22PacketMultiBlockChange) {
            for (BlockUpdateData changedBlock : ((S22PacketMultiBlockChange)packet2).getChangedBlocks()) {
                if (changedBlock != null && matchesBlockPos(changedBlock.getPos(), changedBlock.getBlockState())) {
                    list.add(changedBlock.getPos());
                }
            }
        }

        return list;
    }

    public static synchronized Map<BlockPos, SizeHelper.Entry> computeMap() {
        return new LinkedHashMap<>(lookupMap);
    }

    public static synchronized void reset() {
        active = false;
        lookupMap.clear();
        timestamp3 = 0L;
        timestamp2 = 0L;
        timestamp = 0L;
    }

    public static File computeFile() throws IOException {
        var map = computeMap();
        if (map.isEmpty()) {
            return null;
        }
        int MAX_VALUE = Integer.MAX_VALUE;
        int MAX_VALUE2 = Integer.MAX_VALUE;
        int MAX_VALUE3 = Integer.MAX_VALUE;
        int MIN_VALUE = Integer.MIN_VALUE;
        int MIN_VALUE2 = Integer.MIN_VALUE;
        int MIN_VALUE3 = Integer.MIN_VALUE;

        for (BlockPos pos : map.keySet()) {
            MAX_VALUE = Math.min(MAX_VALUE, pos.getX());
            MAX_VALUE2 = Math.min(MAX_VALUE2, pos.getY());
            MAX_VALUE3 = Math.min(MAX_VALUE3, pos.getZ());
            MIN_VALUE = Math.max(MIN_VALUE, pos.getX());
            MIN_VALUE2 = Math.max(MIN_VALUE2, pos.getY());
            MIN_VALUE3 = Math.max(MIN_VALUE3, pos.getZ());
        }

        int n = MIN_VALUE - MAX_VALUE + 1;
        int n2 = MIN_VALUE2 - MAX_VALUE2 + 1;
        int n3 = MIN_VALUE3 - MAX_VALUE3 + 1;
        byte[] bytes = new byte[n * n2 * n3];
        byte[] bytes2 = new byte[bytes.length];
        JsonArray jsonArray = new JsonArray();

        for (Map.Entry entry : map.entrySet()) {
            BlockPos key = (BlockPos)entry.getKey();
            IBlockState iBlockState = ((SizeHelper.Entry)entry.getValue()).iBlockState;
            int n4 = key.getX() - MAX_VALUE;
            int n5 = key.getY() - MAX_VALUE2;
            int n6 = key.getZ() - MAX_VALUE3;
            int idFromBlock = Block.getIdFromBlock(iBlockState.getBlock());
            int metaFromState = iBlockState.getBlock().getMetaFromState(iBlockState);
            int n7 = (n5 * n3 + n6) * n + n4;
            bytes[n7] = (byte)idFromBlock;
            bytes2[n7] = (byte)metaFromState;
            JsonObject json = new JsonObject();
            json.addProperty("x", n4);
            json.addProperty("y", n5);
            json.addProperty("z", n6);
            json.addProperty("id", idFromBlock);
            json.addProperty("data", metaFromState);
            ResourceLocation nameForObject = Block.blockRegistry.getNameForObject(iBlockState.getBlock());
            json.addProperty("block", nameForObject == null ? "unknown" : nameForObject.toString());
            json.addProperty("t", ((SizeHelper.Entry)entry.getValue()).timestamp);
            jsonArray.add(json);
        }

        File file = new File(Minecraft.getMinecraft().mcDataDir, "Brutality/spire-exports");
        if (!file.isDirectory() && !file.mkdirs()) {
            throw new IOException("Cannot create " + file);
        }
        String str = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        File file2 = new File(file, "spire-" + str + ".schematic");

        try (DataOutputStream dataOutputStream = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file2)))) {
            dataOutputStream.writeByte(10);
            dataOutputStream.writeUTF("Schematic");
            handleDataOutputStream2(dataOutputStream, "Width", n);
            handleDataOutputStream2(dataOutputStream, "Height", n2);
            handleDataOutputStream2(dataOutputStream, "Length", n3);
            dataOutputStream.writeByte(8);
            dataOutputStream.writeUTF("Materials");
            dataOutputStream.writeUTF("Alpha");
            handleDataOutputStream(dataOutputStream, "Blocks", bytes);
            handleDataOutputStream(dataOutputStream, "Data", bytes2);
            handleDataOutputStream(dataOutputStream, "Entities");
            handleDataOutputStream(dataOutputStream, "TileEntities");
            handleDataOutputStream(dataOutputStream, "WEOriginX", MAX_VALUE);
            handleDataOutputStream(dataOutputStream, "WEOriginY", MAX_VALUE2);
            handleDataOutputStream(dataOutputStream, "WEOriginZ", MAX_VALUE3);
            handleDataOutputStream(dataOutputStream, "WEOffsetX", 0);
            handleDataOutputStream(dataOutputStream, "WEOffsetY", 0);
            handleDataOutputStream(dataOutputStream, "WEOffsetZ", 0);
            dataOutputStream.writeByte(0);
        }

        JsonObject json2 = new JsonObject();
        json2.addProperty("name", "Spire");
        json2.addProperty("capturedAt", str);
        JsonObject json3 = new JsonObject();
        json3.addProperty("x", MAX_VALUE);
        json3.addProperty("y", MAX_VALUE2);
        json3.addProperty("z", MAX_VALUE3);
        json2.add("origin", json3);
        JsonObject json4 = new JsonObject();
        json4.addProperty("x", n);
        json4.addProperty("y", n2);
        json4.addProperty("z", n3);
        json2.add("size", json4);
        json2.addProperty("blockCount", map.size());
        json2.addProperty("note", "Positions are relative to origin. id/data are 1.8.8 legacy ids; t is ms after the first spire block (spawn order).");
        json2.add("blocks", jsonArray);
        Files.write(
            new File(file, "spire-" + str + ".json").toPath(),
            new GsonBuilder().setPrettyPrinting().create().toJson((JsonElement)json2).getBytes(StandardCharsets.UTF_8)
        );
        LogManager.getLogger(SizeHelper.class).info("Exported spire (" + map.size() + " blocks) to " + file2);
        return file2;
    }

    private static void handleDataOutputStream(DataOutputStream dataOutputStream, String str, byte[] bytes) throws IOException {
        dataOutputStream.writeByte(7);
        dataOutputStream.writeUTF(str);
        dataOutputStream.writeInt(bytes.length);
        dataOutputStream.write(bytes);
    }

    public static synchronized long getTimestamp3() {
        return timestamp3;
    }

    public static final class Entry {
        public final IBlockState iBlockState;
        public final long timestamp;

        public Entry(IBlockState iBlockState, long timestamp) {
            this.iBlockState = iBlockState;
            this.timestamp = timestamp;
        }
    }
}
