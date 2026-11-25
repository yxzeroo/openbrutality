package org.brutality.utils.world;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import org.brutality.module.impl.misc.WorldExporter;

public class WorldExportManager {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static File computeFile() {
        File file = new File("Brutality/WorldExports");
        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }

    public static void handleMap(Map<BlockPos, WorldExporter.Entry> map, File file) throws IOException {
        try (DataOutputStream dataOutputStream = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file)))) {
            NBTTagCompound nbt = new NBTTagCompound();
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
            nbt.setInteger("Width", n);
            nbt.setInteger("Height", n2);
            nbt.setInteger("Length", n3);
        }
    }

    public static boolean matchesFile(File file) {
        if (file.exists() && file.isFile()) {
            String str = file.getName().toLowerCase();
            return str.endsWith(".json") || str.endsWith(".bwe");
        }
        return false;
    }

    public static void handleMap(Map<BlockPos, WorldExporter.Entry> map, BlockPos pos, WorldExportManager.Listener listener) {
        if (mc.theWorld != null && mc.thePlayer != null) {
            int size = map.size();
            int n = 0;

            for (Entry entry : map.entrySet()) {
                BlockPos key = (BlockPos)entry.getKey();
                WorldExporter.Entry val = (WorldExporter.Entry)entry.getValue();
                BlockPos pos2 = new BlockPos(pos.getX() + val.count, pos.getY() + val.count2, pos.getZ() + val.count3);
                Block blockById = Block.getBlockById(val.blockId);
                if (blockById != null && blockById != Blocks.air) {
                    mc.theWorld.setBlockState(pos2, blockById.getDefaultState());
                    if (val.text3 != null) {
                        try {
                            TileEntity tileEntity = mc.theWorld.getTileEntity(pos2);
                            if (tileEntity != null) {
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }

                if (listener != null && ++n % 100 == 0) {
                    listener.handleInt(n, size);
                }
            }

            if (listener != null) {
                listener.handleInt(size);
            }
        } else {
            if (listener != null) {
                listener.handleString("Not in a world");
            }
        }
    }

    public static File[] listExportFiles() {
        File file = computeFile();
        return file.listFiles((file2, str) -> str.toLowerCase().endsWith(".json") || str.toLowerCase().endsWith(".bwe"));
    }

    public static Map<BlockPos, WorldExporter.Entry> computeMap(File file) throws IOException {
        String str = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        JsonObject asJsonObject = new JsonParser().parse(str).getAsJsonObject();
        HashMap map = new HashMap();
        JsonArray asJsonArray = asJsonObject.getAsJsonArray("blocks");

        for (int i = 0; i < asJsonArray.size(); i++) {
            JsonObject asJsonObject2 = asJsonArray.get(i).getAsJsonObject();
            WorldExporter.Entry entry = new WorldExporter.Entry(
                asJsonObject2.get("relX").getAsInt(),
                asJsonObject2.get("relY").getAsInt(),
                asJsonObject2.get("relZ").getAsInt(),
                asJsonObject2.get("blockId").getAsInt(),
                asJsonObject2.get("blockName").getAsString(),
                asJsonObject2.get("blockState").getAsString()
            );
            if (asJsonObject2.has("tileEntityData") && !asJsonObject2.get("tileEntityData").isJsonNull()) {
                entry.text3 = asJsonObject2.get("tileEntityData").getAsString();
            }

            map.put(new BlockPos(entry.count, entry.count2, entry.count3), entry);
        }

        return map;
    }

    public static Map<BlockPos, WorldExporter.Entry> computeMap2(File file) throws IOException {
        HashMap map = new HashMap();

        try (DataInputStream dataInputStream = new DataInputStream(new GZIPInputStream(new FileInputStream(file)))) {
            String str = dataInputStream.readUTF();
            if (!str.equals("BRUTALITY_WORLD_EXPORT")) {
                throw new IOException("Invalid file format");
            }

            int n = dataInputStream.readInt();
            if (n != 1) {
                throw new IOException("Unsupported version: " + n);
            }

            dataInputStream.readInt();
            dataInputStream.readInt();
            dataInputStream.readInt();
            dataInputStream.readInt();
            dataInputStream.readLong();
            int n2 = dataInputStream.readInt();

            for (int i = 0; i < n2; i++) {
                WorldExporter.Entry entry = new WorldExporter.Entry(dataInputStream.readInt(), dataInputStream.readInt(), dataInputStream.readInt(), dataInputStream.readInt(), dataInputStream.readUTF(), "");
                int n3 = dataInputStream.readInt();
                byte[] bytes = new byte[n3];
                dataInputStream.readFully(bytes);
                entry.text2 = new String(bytes, StandardCharsets.UTF_8);
                boolean flag = dataInputStream.readBoolean();
                if (flag) {
                    int n4 = dataInputStream.readInt();
                    byte[] bytes2 = new byte[n4];
                    dataInputStream.readFully(bytes2);
                    entry.text3 = new String(bytes2, StandardCharsets.UTF_8);
                }

                map.put(new BlockPos(entry.count, entry.count2, entry.count3), entry);
            }
        }

        return map;
    }

    public interface Listener {
        void handleInt(int current, int total);

        void handleInt(int total);

        void handleString(String error);
    }
}
