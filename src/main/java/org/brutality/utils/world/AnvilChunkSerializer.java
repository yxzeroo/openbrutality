package org.brutality.utils.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public final class AnvilChunkSerializer {
    public final int count;
    public final int count2;
    public final NBTTagCompound nbtTagCompound;

    public static AnvilChunkSerializer computeAnvilChunkSerializer(World world, Chunk chunk, boolean flag) {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagCompound nbt2 = new NBTTagCompound();
        nbt.setTag("Level", nbt2);
        nbt2.setByte("V", (byte)1);
        nbt2.setInteger("xPos", chunk.xPosition);
        nbt2.setInteger("zPos", chunk.zPosition);
        nbt2.setLong("LastUpdate", world.getTotalWorldTime());
        nbt2.setLong("InhabitedTime", chunk.getInhabitedTime());
        nbt2.setIntArray("HeightMap", (int[])chunk.getHeightMap().clone());
        nbt2.setByteArray("Biomes", (byte[])chunk.getBiomeArray().clone());
        nbt2.setBoolean("TerrainPopulated", true);
        nbt2.setBoolean("LightPopulated", false);
        NBTTagList tagList = new NBTTagList();

        for (ExtendedBlockStorage extendedBlockStorage : chunk.getBlockStorageArray()) {
            if (extendedBlockStorage != null) {
                NBTTagCompound nbt3 = new NBTTagCompound();
                byte[] bytes = new byte[4096];
                NibbleArray nibbleArray = new NibbleArray();
                NibbleArray nibbleArray2 = null;
                char[] data = extendedBlockStorage.getData();

                for (int i = 0; i < data.length; i++) {
                    char ch = data[i];
                    int n = i & 15;
                    int n2 = i >> 8 & 15;
                    int n3 = i >> 4 & 15;
                    bytes[i] = (byte)(ch >> 4);
                    nibbleArray.set(n, n2, n3, (15 | ~ch) - ~ch);
                    if (ch >> '\f' != 0) {
                        if (nibbleArray2 == null) {
                            nibbleArray2 = new NibbleArray();
                        }

                        nibbleArray2.set(n, n2, n3, ch >> '\f');
                    }
                }

                nbt3.setByte("Y", (byte)(extendedBlockStorage.getYLocation() >> 4));
                nbt3.setByteArray("Blocks", bytes);
                nbt3.setByteArray("Data", nibbleArray.getData());
                if (nibbleArray2 != null) {
                    nbt3.setByteArray("Add", nibbleArray2.getData());
                }

                nbt3.setByteArray("BlockLight", (byte[])extendedBlockStorage.getBlocklightArray().getData().clone());
                nbt3.setByteArray("SkyLight", extendedBlockStorage.getSkylightArray() == null ? new byte[2048] : (byte[])extendedBlockStorage.getSkylightArray().getData().clone());
                tagList.appendTag(nbt3);
            }
        }

        nbt2.setTag("Sections", tagList);
        NBTTagList tagList2 = new NBTTagList();

        for (TileEntity tileEntity : chunk.getTileEntityMap().values()) {
            NBTTagCompound nbt4 = new NBTTagCompound();
            tileEntity.writeToNBT(nbt4);
            tagList2.appendTag(nbt4);
        }

        nbt2.setTag("TileEntities", tagList2);
        NBTTagList tagList3 = new NBTTagList();
        if (flag) {
            ClassInheritanceMultiMap[] entityLists = chunk.getEntityLists();
            int len = entityLists.length;

            for (int j = 0; j < len; j++) {
                for (Entity entity : ((Iterable<Entity>)(Object)(entityLists[j]))) {
                    if (!(entity instanceof EntityPlayer) && !entity.isDead) {
                        NBTTagCompound nbt5 = new NBTTagCompound();
                        if (entity.writeToNBTOptional(nbt5)) {
                            tagList3.appendTag(nbt5);
                        }
                    }
                }
            }
        }

        nbt2.setTag("Entities", tagList3);
        return new AnvilChunkSerializer(chunk.xPosition, chunk.zPosition, nbt);
    }

    private AnvilChunkSerializer(int count, int n, NBTTagCompound nbtTagCompound) {
        this.count = count;
        this.count2 = n;
        this.nbtTagCompound = nbtTagCompound;
    }
}
