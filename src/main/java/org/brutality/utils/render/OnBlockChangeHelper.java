package org.brutality.utils.render;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBed.EnumPartType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S22PacketMultiBlockChange.BlockUpdateData;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public final class OnBlockChangeHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final OnBlockChangeHelper instance = new OnBlockChangeHelper();
    private final Map<Long, Set<BlockPos>> lookupMap = new ConcurrentHashMap<>();
    private final Set<OnBlockChangeHelper.Listener> itemSet = ConcurrentHashMap.newKeySet();
    private final Deque<long[]> deque = new ArrayDeque<>();
    private boolean flag;
    private static final long timestamp = 4294967295L;

    public void onBlockChange(BlockPos pos, IBlockState blockState) {
        long l = chunkKey(pos.getX() >> 4, pos.getZ() >> 4);
        BlockPos pos2 = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
        if (matchesIBlockState(blockState)) {
            this.lookupMap.computeIfAbsent(l, val -> ConcurrentHashMap.newKeySet()).add(pos2);
        } else {
            Set set = this.lookupMap.get(l);
            if (set != null) {
                set.remove(pos);
                if (set.isEmpty()) {
                    this.lookupMap.remove(l);
                }
            }
        }

        for (OnBlockChangeHelper.Listener listener : this.itemSet) {
            listener.handleBlockPos(pos2, blockState);
        }
    }

    public int computeInt() {
        int n = 0;

        for (Set set : this.lookupMap.values()) {
            n += set.size();
        }

        return n;
    }

    public static OnBlockChangeHelper getInstance() {
        return instance;
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void handleListener(OnBlockChangeHelper.Listener listener) {
        if (listener != null) {
            this.itemSet.remove(listener);
        }
    }

    private static boolean matchesIBlockState(IBlockState blockState) {
        return blockState != null && blockState.getBlock() instanceof BlockBed && blockState.getValue(BlockBed.PART) == EnumPartType.FOOT;
    }

    public void removeChunk(int n, int n2) {
        long l = chunkKey(n, n2);
        this.lookupMap.remove(l);

        for (OnBlockChangeHelper.Listener listener : this.itemSet) {
            listener.handleInt(n, n2);
        }
    }

    public void handlePacket(Packet<?> packet) {
        if (this.isFlag() && packet != null) {
            if (packet instanceof S23PacketBlockChange packet2) {
                this.onBlockChange(packet2.getBlockPosition(), packet2.getBlockState());
            } else if (packet instanceof S22PacketMultiBlockChange packet3) {
                for (BlockUpdateData changedBlock : packet3.getChangedBlocks()) {
                    this.onBlockChange(changedBlock.getPos(), changedBlock.getBlockState());
                }
            } else if (packet instanceof S21PacketChunkData packet4) {
                if (packet4.getExtractedSize() == 0) {
                    this.removeChunk(packet4.getChunkX(), packet4.getChunkZ());
                } else {
                    this.handleInt(packet4.getChunkX(), packet4.getChunkZ());
                }
            } else if (packet instanceof S26PacketMapChunkBulk packet5) {
                for (int i = 0; i < packet5.getChunkCount(); i++) {
                    this.handleInt(packet5.getChunkX(i), packet5.getChunkZ(i));
                }
            }
        }
    }

    public void handleListener2(OnBlockChangeHelper.Listener listener) {
        if (listener != null) {
            this.itemSet.add(listener);
        }
    }

    public void handleInt(int n) {
        if (mc.theWorld != null && this.isFlag()) {
            int n2 = Math.max(0, n);

            while (n2 > 0 && !this.deque.isEmpty()) {
                long[] ls = this.deque.pollFirst();
                int n3 = (int)ls[0];
                int n4 = (int)ls[1];
                Chunk chunkFromChunkCoords = mc.theWorld.getChunkFromChunkCoords(n3, n4);
                if (chunkFromChunkCoords != null && !(chunkFromChunkCoords instanceof EmptyChunk)) {
                    n2 -= this.computeInt(chunkFromChunkCoords);
                }
            }
        }
    }

    private OnBlockChangeHelper() {
    }

    public Iterable<Entry<Long, Set<BlockPos>>> computeIterable() {
        return this.lookupMap.entrySet();
    }

    private static long chunkKey(int n, int n2) {
        return (long)n << 32 | n2 & timestamp;
    }

    public void handleInt(int n, int n2) {
        if (this.isFlag()) {
            this.deque.addLast(new long[]{n, n2});

            for (OnBlockChangeHelper.Listener listener : this.itemSet) {
                listener.handleInt2(n, n2);
            }
        }
    }

    public void process2() {
        this.flag = true;
    }

    public void process3() {
        if (this.isFlag()) {
            this.deque.clear();
            if (mc.theWorld != null && mc.thePlayer != null) {
                int renderDistanceChunks = mc.gameSettings.renderDistanceChunks;
                int n = (int)mc.thePlayer.posX >> 4;
                int n2 = (int)mc.thePlayer.posZ >> 4;

                for (int i = n - renderDistanceChunks; i <= n + renderDistanceChunks; i++) {
                    for (int j = n2 - renderDistanceChunks; j <= n2 + renderDistanceChunks; j++) {
                        Chunk chunkFromChunkCoords = mc.theWorld.getChunkFromChunkCoords(i, j);
                        if (chunkFromChunkCoords != null && !(chunkFromChunkCoords instanceof EmptyChunk)) {
                            this.handleInt(i, j);
                        }
                    }
                }
            }
        }
    }

    private int computeInt(Chunk chunk) {
        int n = 0;
        long l = chunkKey(chunk.xPosition, chunk.zPosition);
        KeySetView keySetView = ConcurrentHashMap.newKeySet();
        ExtendedBlockStorage[] blockStorageArray = chunk.getBlockStorageArray();
        int n2 = chunk.xPosition << 4;
        int n3 = chunk.zPosition << 4;

        for (int i = 0; i < blockStorageArray.length; i++) {
            ExtendedBlockStorage extendedBlockStorage = blockStorageArray[i];
            if (extendedBlockStorage != null) {
                n++;
                int n4 = i << 4;

                for (int j = 0; j < 16; j++) {
                    for (int k = 0; k < 16; k++) {
                        for (int idx = 0; idx < 16; idx++) {
                            IBlockState blockState = extendedBlockStorage.get(idx, j, k);
                            if (blockState != null && matchesIBlockState(blockState)) {
                                keySetView.add(new BlockPos(n2 + idx, n4 + j, n3 + k));
                            }
                        }
                    }
                }
            }
        }

        if (!keySetView.isEmpty()) {
            this.lookupMap.put(l, keySetView);
        } else {
            this.lookupMap.remove(l);
        }

        return Math.max(n, 1);
    }

    public void clear() {
        this.lookupMap.clear();
        this.deque.clear();

        for (OnBlockChangeHelper.Listener listener : this.itemSet) {
            listener.process();
        }
    }

    public void process() {
        this.flag = false;
        this.lookupMap.clear();
    }

    public interface Listener {
        void handleInt2(int chunkX, int chunkZ);

        void process();

        void handleInt(int chunkX, int chunkZ);

        void handleBlockPos(BlockPos pos, IBlockState newState);
    }
}
