package org.brutality.module.impl.misc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.player.GetBlockStateHelper;

public class WorldExporter extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final Map<BlockPos, WorldExporter.Entry> lookupMap = new LinkedHashMap<>();
    private boolean flag = false;
    private int count = 0;
    private int count2 = 0;
    private int count3 = 0;
    private String text = null;

    @Override
    public void onEvent(Event event) {
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.theWorld != null && mc.thePlayer != null) {
            this.lookupMap.clear();
            this.count = 0;
            this.count3 = 0;
            this.text = null;
            this.process();
        } else {
            this.handleFlag3(false);
        }
    }

    public String getText() {
        return this.text;
    }

    public int getCount() {
        return this.count;
    }

    public void handleString(String str) {
        if (this.lookupMap.isEmpty()) {
            this.sendMessage("§cNo blocks captured. Enable the module first.");
        } else {
            try {
                File file = new File("Brutality/WorldExports");
                if (!file.exists()) {
                    file.mkdirs();
                }

                String str2 = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                File file2 = new File(file, str + "_" + str2 + ".json");
                WorldExporter.Entry2 entry2 = new WorldExporter.Entry2(
                    mc.thePlayer.getPosition(),
                    mc.theWorld.provider.getDimensionId(),
                    mc.theWorld.getWorldInfo().getWorldName(),
                    new ArrayList<>(this.lookupMap.values()),
                    System.currentTimeMillis()
                );
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String str3 = gson.toJson(entry2);
                Files.write(file2.toPath(), str3.getBytes(StandardCharsets.UTF_8));
                this.text = file2.getAbsolutePath();
                this.sendMessage("§aExported §f" + this.count3 + " §ablocks to:");
                this.sendMessage("§f" + file2.getName());
            } catch (Exception e) {
                this.sendMessage("§cExport failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public boolean isFlag() {
        return this.flag;
    }

    public WorldExporter() {
        super("WorldExporter", 0, Category.MISCELLANEOUS);
        this.setRequiresInGame(true);
    }

    public void handleString2(String str) {
        this.handleString(str);
    }

    private void process() {
        if (!this.flag) {
            int n = (int)((NumberSetting)Brutality.settingsManager.computeSetting(this, "Radius")).computeDouble();
            boolean enabled = ((BooleanSetting)Brutality.settingsManager.computeSetting(this, "IncludeAir")).isEnabled();
            boolean enabled2 = ((BooleanSetting)Brutality.settingsManager.computeSetting(this, "IncludeTileEntities")).isEnabled();
            int n2 = n * 2 + 1;
            this.count2 = n2 * n2 * mc.theWorld.getHeight() / 16 * 16;
            this.sendMessage("§aScanning " + this.count2 + " blocks in radius " + n + "...");
            this.flag = true;
            this.count = 0;
            CompletableFuture.runAsync(() -> {
                try {
                    this.handleInt(n, enabled, enabled2);
                } catch (Exception e) {
                    this.sendMessage("§cScan error: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    this.flag = false;
                }
            });
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.flag = false;
        if (this.count3 > 0 && ((BooleanSetting)Brutality.settingsManager.computeSetting(this, "AutoExport")).isEnabled() && !this.lookupMap.isEmpty()) {
            this.handleString2("auto_export");
        }
    }

    private void handleInt(int n, boolean flag, boolean flag2) {
        BlockPos pos = mc.thePlayer.getPosition();
        int y = pos.getY();
        int n2 = Math.max(0, y - n);
        int n3 = Math.min(mc.theWorld.getHeight() - 1, y + n);
        int n4 = 0;
        int n5 = 0;

        for (int i = n2; i <= n3; i++) {
            for (int j = -n; j <= n; j++) {
                for (int k = -n; k <= n; k++) {
                    BlockPos pos2 = new BlockPos(pos.getX() + j, i, pos.getZ() + k);
                    n4++;
                    this.count = (int)((double)n4 / this.count2 * 100.0);
                    IBlockState blockState = GetBlockStateHelper.computeIBlockState(pos2);
                    Block block = blockState.getBlock();
                    if (flag || !block.getMaterial().isReplaceable()) {
                        WorldExporter.Entry entry = new WorldExporter.Entry(
                            pos2.getX() - pos.getX(),
                            i - n2,
                            pos2.getZ() - pos.getZ(),
                            Block.getIdFromBlock(block),
                            block.getUnlocalizedName(),
                            blockState.toString()
                        );
                        if (flag2) {
                            TileEntity tileEntity = mc.theWorld.getTileEntity(pos2);
                            if (tileEntity != null) {
                                try {
                                    NBTTagCompound nbt = new NBTTagCompound();
                                    tileEntity.writeToNBT(nbt);
                                    entry.text3 = nbt.toString();
                                } catch (Exception ignored) {
                                }
                            }
                        }

                        this.lookupMap.put(pos2, entry);
                        n5++;
                        if (n4 % 1000 == 0) {
                            this.sendMessage("§7Scan progress: §f" + this.count + "% §7(§f" + n5 + " blocks§7)");
                        }
                    }
                }
            }
        }

        this.count3 = n5;
        this.sendMessage("§aScan complete! Captured §f" + n5 + " §ablocks.");
        this.sendMessage("§7Use .export to save the data.");
    }

    public int computeInt() {
        return this.lookupMap.size();
    }

    public void handleString3(String str) {
        if (this.lookupMap.isEmpty()) {
            this.sendMessage("§cNo blocks captured. Enable the module first.");
        } else {
            try {
                File file = new File("Brutality/WorldExports");
                if (!file.exists()) {
                    file.mkdirs();
                }

                String str2 = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                File file2 = new File(file, str + "_" + str2 + ".bwe");

                try (DataOutputStream dataOutputStream = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file2)))) {
                    dataOutputStream.writeUTF("BRUTALITY_WORLD_EXPORT");
                    dataOutputStream.writeInt(1);
                    BlockPos pos = mc.thePlayer.getPosition();
                    dataOutputStream.writeInt(pos.getX());
                    dataOutputStream.writeInt(pos.getY());
                    dataOutputStream.writeInt(pos.getZ());
                    dataOutputStream.writeInt(mc.theWorld.provider.getDimensionId());
                    dataOutputStream.writeLong(System.currentTimeMillis());
                    dataOutputStream.writeInt(this.lookupMap.size());

                    for (WorldExporter.Entry entry : this.lookupMap.values()) {
                        dataOutputStream.writeInt(entry.count);
                        dataOutputStream.writeInt(entry.count2);
                        dataOutputStream.writeInt(entry.count3);
                        dataOutputStream.writeInt(entry.blockId);
                        dataOutputStream.writeUTF(entry.text);
                        byte[] bytes = entry.text2.getBytes(StandardCharsets.UTF_8);
                        dataOutputStream.writeInt(bytes.length);
                        dataOutputStream.write(bytes);
                        if (entry.text3 != null) {
                            dataOutputStream.writeBoolean(true);
                            byte[] bytes2 = entry.text3.getBytes(StandardCharsets.UTF_8);
                            dataOutputStream.writeInt(bytes2.length);
                            dataOutputStream.write(bytes2);
                        } else {
                            dataOutputStream.writeBoolean(false);
                        }
                    }
                }

                this.text = file2.getAbsolutePath();
                this.sendMessage("§aExported §f" + this.count3 + " §ablocks (binary) to:");
                this.sendMessage("§f" + file2.getName());
            } catch (Exception e) {
                this.sendMessage("§cExport failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(String str) {
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText("§8[§4WorldExporter§8] §f" + str));
        }
    }

    public void process3() {
        this.lookupMap.clear();
        this.count = 0;
        this.count3 = 0;
        this.sendMessage("§cCleared all captured data.");
    }

    public int getCount3() {
        return this.count3;
    }

    @Override
    public void process9() {
        this.handleSettingArray(
            new Setting[]{
                new NumberSetting("Radius", this, 50.0, 5.0, 200.0, 1.0),
                new BooleanSetting("IncludeAir", this, false),
                new BooleanSetting("IncludeTileEntities", this, true),
                new BooleanSetting("AutoExport", this, false)
            }
        );
    }

    public static class Entry {
        public int count;
        public int count2;
        public int count3;
        public int blockId;
        public String text;
        public String text2;
        public String text3;

        public Entry(int count, int n, int n2, int blockId, String text, String str) {
            this.count = count;
            this.count2 = n;
            this.count3 = n2;
            this.blockId = blockId;
            this.text = text;
            this.text2 = str;
        }
    }

    public static class Entry2 {
        public BlockPos blockPos;
        public int dimension;
        public String worldName;
        public List<WorldExporter.Entry> entries;
        public long timestamp;

        public Entry2(BlockPos blockPos, int dimension, String worldName, List<WorldExporter.Entry> entries, long timestamp) {
            this.blockPos = blockPos;
            this.dimension = dimension;
            this.worldName = worldName;
            this.entries = entries;
            this.timestamp = timestamp;
        }
    }
}
