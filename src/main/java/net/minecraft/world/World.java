package net.minecraft.world;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;
import org.brutality.module.impl.misc.AntiTrap;

public abstract class World implements IBlockAccess {
    private int field_181546_a = 63;
    protected boolean scheduledUpdatesAreImmediate;
    public final List<Entity> loadedEntityList = Lists.newArrayList();
    protected final List<Entity> unloadedEntityList = Lists.newArrayList();
    public final List<TileEntity> loadedTileEntityList = Lists.newArrayList();
    public final List<TileEntity> tickableTileEntities = Lists.newArrayList();
    private final List<TileEntity> addedTileEntityList = Lists.newArrayList();
    private final List<TileEntity> tileEntitiesToBeRemoved = Lists.newArrayList();
    public final List<EntityPlayer> playerEntities = Lists.newArrayList();
    public final List<Entity> weatherEffects = Lists.newArrayList();
    protected final IntHashMap<Entity> entitiesById = new IntHashMap<>();
    private long cloudColour = 16777215L;
    private int skylightSubtracted;
    protected int updateLCG = new Random().nextInt();
    protected final int DIST_HASH_MAGIC = 1013904223;
    protected float prevRainingStrength;
    protected float rainingStrength;
    protected float prevThunderingStrength;
    protected float thunderingStrength;
    private int lastLightningBolt;
    public final Random rand = new Random();
    public final WorldProvider provider;
    protected List<IWorldAccess> worldAccesses = Lists.newArrayList();
    protected IChunkProvider chunkProvider;
    protected final ISaveHandler saveHandler;
    protected WorldInfo worldInfo;
    protected boolean findingSpawnPoint;
    protected MapStorage mapStorage;
    protected VillageCollection villageCollectionObj;
    public final Profiler theProfiler;
    private final Calendar theCalendar = Calendar.getInstance();
    protected Scoreboard worldScoreboard = new Scoreboard();
    public final boolean isRemote;
    protected Set<ChunkCoordIntPair> activeChunkSet = Sets.newHashSet();
    private int ambientTickCountdown = this.rand.nextInt(12000);
    protected boolean spawnHostileMobs = true;
    protected boolean spawnPeacefulMobs = true;
    private boolean processingLoadedTiles;
    private final WorldBorder worldBorder;
    int[] lightUpdateBlockList = new int[32768];

    public World(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
        this.saveHandler = saveHandlerIn;
        this.theProfiler = profilerIn;
        this.worldInfo = info;
        this.provider = providerIn;
        this.isRemote = client;
        this.worldBorder = providerIn.getWorldBorder();
    }

    public World init() {
        return this;
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(final BlockPos pos) {
        if (this.isBlockLoaded(pos)) {
            Chunk chunkFromBlockCoords = this.getChunkFromBlockCoords(pos);

            try {
                return chunkFromBlockCoords.getBiome(pos, this.provider.getWorldChunkManager());
            } catch (Throwable e) {
                CrashReport crashReport = CrashReport.makeCrashReport(e, "Getting biome");
                CrashReportCategory crashReportCategory = crashReport.makeCategory("Coordinates of biome request");
                crashReportCategory.addCrashSectionCallable("Location", new Callable<String>() {
                    public String call() throws Exception {
                        return CrashReportCategory.getCoordinateInfo(pos);
                    }
                });
                throw new ReportedException(crashReport);
            }
        } else {
            return this.provider.getWorldChunkManager().getBiomeGenerator(pos, BiomeGenBase.plains);
        }
    }

    public WorldChunkManager getWorldChunkManager() {
        return this.provider.getWorldChunkManager();
    }

    protected abstract IChunkProvider createChunkProvider();

    public void initialize(WorldSettings settings) {
        this.worldInfo.setServerInitialized(true);
    }

    public void setInitialSpawnLocation() {
        this.setSpawnPoint(new BlockPos(8, 64, 8));
    }

    public Block getGroundAboveSeaLevel(BlockPos pos) {
        BlockPos pos2 = new BlockPos(pos.getX(), this.func_181545_F(), pos.getZ());

        while (!this.isAirBlock(pos2.up())) {
            pos2 = pos2.up();
        }

        return this.getBlockState(pos2).getBlock();
    }

    private boolean isValid(BlockPos pos) {
        return pos.getX() >= -30000000
            && pos.getZ() >= -30000000
            && pos.getX() < 30000000
            && pos.getZ() < 30000000
            && pos.getY() >= 0
            && pos.getY() < 256;
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return this.getBlockState(pos).getBlock().getMaterial() == Material.air;
    }

    public boolean isBlockLoaded(BlockPos pos) {
        return this.isBlockLoaded(pos, true);
    }

    public boolean isBlockLoaded(BlockPos pos, boolean allowEmpty) {
        return !this.isValid(pos) ? false : this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, allowEmpty);
    }

    public boolean isAreaLoaded(BlockPos center, int radius) {
        return this.isAreaLoaded(center, radius, true);
    }

    public boolean isAreaLoaded(BlockPos center, int radius, boolean allowEmpty) {
        return this.isAreaLoaded(center.getX() - radius, center.getY() - radius, center.getZ() - radius, center.getX() + radius, center.getY() + radius, center.getZ() + radius, allowEmpty);
    }

    public boolean isAreaLoaded(BlockPos from, BlockPos to) {
        return this.isAreaLoaded(from, to, true);
    }

    public boolean isAreaLoaded(BlockPos from, BlockPos to, boolean allowEmpty) {
        return this.isAreaLoaded(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ(), allowEmpty);
    }

    public boolean isAreaLoaded(StructureBoundingBox box) {
        return this.isAreaLoaded(box, true);
    }

    public boolean isAreaLoaded(StructureBoundingBox box, boolean allowEmpty) {
        return this.isAreaLoaded(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, allowEmpty);
    }

    private boolean isAreaLoaded(int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd, boolean allowEmpty) {
        if (yEnd >= 0 && yStart < 256) {
            xStart >>= 4;
            zStart >>= 4;
            xEnd >>= 4;
            zEnd >>= 4;

            for (int i = xStart; i <= xEnd; i++) {
                for (int j = zStart; j <= zEnd; j++) {
                    if (!this.isChunkLoaded(i, j, allowEmpty)) {
                        return false;
                    }
                }
            }

            return true;
        }
        return false;
    }

    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
        return this.chunkProvider.chunkExists(x, z) && (allowEmpty || !this.chunkProvider.provideChunk(x, z).isEmpty());
    }

    public Chunk getChunkFromBlockCoords(BlockPos pos) {
        return this.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public Chunk getChunkFromChunkCoords(int chunkX, int chunkZ) {
        return this.chunkProvider.provideChunk(chunkX, chunkZ);
    }

    public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
        if (!this.isValid(pos)) {
            return false;
        }
        if (!this.isRemote && this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
            return false;
        }
        Chunk chunkFromBlockCoords = this.getChunkFromBlockCoords(pos);
        Block block = newState.getBlock();
        IBlockState blockState = chunkFromBlockCoords.setBlockState(pos, newState);
        if (blockState == null) {
            return false;
        }
        Block block2 = blockState.getBlock();
        if (block.getLightOpacity() != block2.getLightOpacity() || block.getLightValue() != block2.getLightValue()) {
            this.theProfiler.startSection("checkLight");
            this.checkLight(pos);
            this.theProfiler.endSection();
        }

        if ((flags & 2) != 0 && (!this.isRemote || (flags & 4) == 0) && chunkFromBlockCoords.isPopulated()) {
            this.markBlockForUpdate(pos);
        }

        if (!this.isRemote && (flags & 1) != 0) {
            this.notifyNeighborsRespectDebug(pos, blockState.getBlock());
            if (block.hasComparatorInputOverride()) {
                this.updateComparatorOutputLevel(pos, block);
            }
        }

        return true;
    }

    public boolean setBlockToAir(BlockPos pos) {
        return this.setBlockState(pos, Blocks.air.getDefaultState(), 3);
    }

    public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
        IBlockState blockState = this.getBlockState(pos);
        Block block = blockState.getBlock();
        if (block.getMaterial() == Material.air) {
            return false;
        }
        this.playAuxSFX(2001, pos, Block.getStateId(blockState));
        if (dropBlock) {
            block.dropBlockAsItem(this, pos, blockState, 0);
        }

        return this.setBlockState(pos, Blocks.air.getDefaultState(), 3);
    }

    public boolean setBlockState(BlockPos pos, IBlockState state) {
        return this.setBlockState(pos, state, 3);
    }

    public void markBlockForUpdate(BlockPos pos) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).markBlockForUpdate(pos);
        }
    }

    public void notifyNeighborsRespectDebug(BlockPos pos, Block blockType) {
        if (this.worldInfo.getTerrainType() != WorldType.DEBUG_WORLD) {
            this.notifyNeighborsOfStateChange(pos, blockType);
        }
    }

    public void markBlocksDirtyVertical(int x1, int z1, int x2, int z2) {
        if (x2 > z2) {
            int n = z2;
            z2 = x2;
            x2 = n;
        }

        if (!this.provider.getHasNoSky()) {
            for (int i = x2; i <= z2; i++) {
                this.checkLightFor(EnumSkyBlock.SKY, new BlockPos(x1, i, z1));
            }
        }

        this.markBlockRangeForRenderUpdate(x1, x2, z1, x1, z2, z1);
    }

    public void markBlockRangeForRenderUpdate(BlockPos rangeMin, BlockPos rangeMax) {
        this.markBlockRangeForRenderUpdate(rangeMin.getX(), rangeMin.getY(), rangeMin.getZ(), rangeMax.getX(), rangeMax.getY(), rangeMax.getZ());
    }

    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
        }
    }

    public void notifyNeighborsOfStateChange(BlockPos pos, Block blockType) {
        this.notifyBlockOfStateChange(pos.west(), blockType);
        this.notifyBlockOfStateChange(pos.east(), blockType);
        this.notifyBlockOfStateChange(pos.down(), blockType);
        this.notifyBlockOfStateChange(pos.up(), blockType);
        this.notifyBlockOfStateChange(pos.north(), blockType);
        this.notifyBlockOfStateChange(pos.south(), blockType);
    }

    public void notifyNeighborsOfStateExcept(BlockPos pos, Block blockType, EnumFacing skipSide) {
        if (skipSide != EnumFacing.WEST) {
            this.notifyBlockOfStateChange(pos.west(), blockType);
        }

        if (skipSide != EnumFacing.EAST) {
            this.notifyBlockOfStateChange(pos.east(), blockType);
        }

        if (skipSide != EnumFacing.DOWN) {
            this.notifyBlockOfStateChange(pos.down(), blockType);
        }

        if (skipSide != EnumFacing.UP) {
            this.notifyBlockOfStateChange(pos.up(), blockType);
        }

        if (skipSide != EnumFacing.NORTH) {
            this.notifyBlockOfStateChange(pos.north(), blockType);
        }

        if (skipSide != EnumFacing.SOUTH) {
            this.notifyBlockOfStateChange(pos.south(), blockType);
        }
    }

    public void notifyBlockOfStateChange(BlockPos pos, final Block blockIn) {
        if (!this.isRemote) {
            IBlockState blockState = this.getBlockState(pos);

            try {
                blockState.getBlock().onNeighborBlockChange(this, pos, blockState, blockIn);
            } catch (Throwable e) {
                CrashReport crashReport = CrashReport.makeCrashReport(e, "Exception while updating neighbours");
                CrashReportCategory crashReportCategory = crashReport.makeCategory("Block being updated");
                crashReportCategory.addCrashSectionCallable(
                    "Source block type",
                    new Callable<String>() {
                        public String call() throws Exception {
                            try {
                                return String.format(
                                    "ID #%d (%s // %s)", Block.getIdFromBlock(blockIn), blockIn.getUnlocalizedName(), blockIn.getClass().getCanonicalName()
                                );
                            } catch (Throwable e2) {
                                return "ID #" + Block.getIdFromBlock(blockIn);
                            }
                        }
                    }
                );
                CrashReportCategory.addBlockInfo(crashReportCategory, pos, blockState);
                throw new ReportedException(crashReport);
            }
        }
    }

    public boolean isBlockTickPending(BlockPos pos, Block blockType) {
        return false;
    }

    public boolean canSeeSky(BlockPos pos) {
        return this.getChunkFromBlockCoords(pos).canSeeSky(pos);
    }

    public boolean canBlockSeeSky(BlockPos pos) {
        if (pos.getY() >= this.func_181545_F()) {
            return this.canSeeSky(pos);
        }
        BlockPos pos2 = new BlockPos(pos.getX(), this.func_181545_F(), pos.getZ());
        if (!this.canSeeSky(pos2)) {
            return false;
        }
        for (BlockPos i = pos2.down(); i.getY() > pos.getY(); i = i.down()) {
            Block block = this.getBlockState(i).getBlock();
            if (block.getLightOpacity() > 0 && !block.getMaterial().isLiquid()) {
                return false;
            }
        }

        return true;
    }

    public int getLight(BlockPos pos) {
        if (pos.getY() < 0) {
            return 0;
        }
        if (pos.getY() >= 256) {
            pos = new BlockPos(pos.getX(), 255, pos.getZ());
        }

        return this.getChunkFromBlockCoords(pos).getLightSubtracted(pos, 0);
    }

    public int getLightFromNeighbors(BlockPos pos) {
        return this.getLight(pos, true);
    }

    public int getLight(BlockPos pos, boolean checkNeighbors) {
        if (pos.getX() < -30000000 || pos.getZ() < -30000000 || pos.getX() >= 30000000 || pos.getZ() >= 30000000) {
            return 15;
        }
        if (checkNeighbors && this.getBlockState(pos).getBlock().getUseNeighborBrightness()) {
            int light = this.getLight(pos.up(), false);
            int light2 = this.getLight(pos.east(), false);
            int light3 = this.getLight(pos.west(), false);
            int light4 = this.getLight(pos.south(), false);
            int light5 = this.getLight(pos.north(), false);
            if (light2 > light) {
                light = light2;
            }

            if (light3 > light) {
                light = light3;
            }

            if (light4 > light) {
                light = light4;
            }

            if (light5 > light) {
                light = light5;
            }

            return light;
        }
        if (pos.getY() < 0) {
            return 0;
        }
        if (pos.getY() >= 256) {
            pos = new BlockPos(pos.getX(), 255, pos.getZ());
        }

        Chunk chunkFromBlockCoords = this.getChunkFromBlockCoords(pos);
        return chunkFromBlockCoords.getLightSubtracted(pos, this.skylightSubtracted);
    }

    public BlockPos getHeight(BlockPos pos) {
        int n;
        if (pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000) {
            if (this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, true)) {
                n = this.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4).getHeightValue(pos.getX() & 15, pos.getZ() & 15);
            } else {
                n = 0;
            }
        } else {
            n = this.func_181545_F() + 1;
        }

        return new BlockPos(pos.getX(), n, pos.getZ());
    }

    public int getChunksLowestHorizon(int x, int z) {
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
            if (!this.isChunkLoaded(x >> 4, z >> 4, true)) {
                return 0;
            }
            Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(x >> 4, z >> 4);
            return chunkFromChunkCoords.getLowestHeight();
        }
        return this.func_181545_F() + 1;
    }

    public int getLightFromNeighborsFor(EnumSkyBlock type, BlockPos pos) {
        if (this.provider.getHasNoSky() && type == EnumSkyBlock.SKY) {
            return 0;
        }
        if (pos.getY() < 0) {
            pos = new BlockPos(pos.getX(), 0, pos.getZ());
        }

        if (!this.isValid(pos)) {
            return type.defaultLightValue;
        }
        if (!this.isBlockLoaded(pos)) {
            return type.defaultLightValue;
        }
        if (this.getBlockState(pos).getBlock().getUseNeighborBrightness()) {
            int lightFor = this.getLightFor(type, pos.up());
            int lightFor2 = this.getLightFor(type, pos.east());
            int lightFor3 = this.getLightFor(type, pos.west());
            int lightFor4 = this.getLightFor(type, pos.south());
            int lightFor5 = this.getLightFor(type, pos.north());
            if (lightFor2 > lightFor) {
                lightFor = lightFor2;
            }

            if (lightFor3 > lightFor) {
                lightFor = lightFor3;
            }

            if (lightFor4 > lightFor) {
                lightFor = lightFor4;
            }

            if (lightFor5 > lightFor) {
                lightFor = lightFor5;
            }

            return lightFor;
        }
        Chunk chunkFromBlockCoords = this.getChunkFromBlockCoords(pos);
        return chunkFromBlockCoords.getLightFor(type, pos);
    }

    public int getLightFor(EnumSkyBlock type, BlockPos pos) {
        if (pos.getY() < 0) {
            pos = new BlockPos(pos.getX(), 0, pos.getZ());
        }

        if (!this.isValid(pos)) {
            return type.defaultLightValue;
        }
        if (!this.isBlockLoaded(pos)) {
            return type.defaultLightValue;
        }
        Chunk chunkFromBlockCoords = this.getChunkFromBlockCoords(pos);
        return chunkFromBlockCoords.getLightFor(type, pos);
    }

    public void setLightFor(EnumSkyBlock type, BlockPos pos, int lightValue) {
        if (this.isValid(pos) && this.isBlockLoaded(pos)) {
            Chunk chunkFromBlockCoords = this.getChunkFromBlockCoords(pos);
            chunkFromBlockCoords.setLightFor(type, pos, lightValue);
            this.notifyLightSet(pos);
        }
    }

    public void notifyLightSet(BlockPos pos) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).notifyLightSet(pos);
        }
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        int lightFromNeighborsFor = this.getLightFromNeighborsFor(EnumSkyBlock.SKY, pos);
        int lightFromNeighborsFor2 = this.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, pos);
        if (lightFromNeighborsFor2 < lightValue) {
            lightFromNeighborsFor2 = lightValue;
        }

        return lightFromNeighborsFor << 20 | lightFromNeighborsFor2 << 4;
    }

    public float getLightBrightness(BlockPos pos) {
        return this.provider.getLightBrightnessTable()[this.getLightFromNeighbors(pos)];
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        if (!this.isValid(pos)) {
            return Blocks.air.getDefaultState();
        }
        Chunk chunkFromBlockCoords = this.getChunkFromBlockCoords(pos);
        return chunkFromBlockCoords.getBlockState(pos);
    }

    public boolean isDaytime() {
        return this.skylightSubtracted < 4;
    }

    public MovingObjectPosition rayTraceBlocks(Vec3 p_72933_1_, Vec3 p_72933_2_) {
        return this.rayTraceBlocks(p_72933_1_, p_72933_2_, false, false, false);
    }

    public MovingObjectPosition rayTraceBlocks(Vec3 start, Vec3 end, boolean stopOnLiquid) {
        return this.rayTraceBlocks(start, end, stopOnLiquid, false, false);
    }

    public MovingObjectPosition rayTraceBlocks(Vec3 vec31, Vec3 vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        if (Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord)) {
            return null;
        }
        if (!Double.isNaN(vec32.xCoord) && !Double.isNaN(vec32.yCoord) && !Double.isNaN(vec32.zCoord)) {
            int n = MathHelper.floor_double(vec32.xCoord);
            int n2 = MathHelper.floor_double(vec32.yCoord);
            int n3 = MathHelper.floor_double(vec32.zCoord);
            int n4 = MathHelper.floor_double(vec31.xCoord);
            int n5 = MathHelper.floor_double(vec31.yCoord);
            int n6 = MathHelper.floor_double(vec31.zCoord);
            BlockPos pos = new BlockPos(n4, n5, n6);
            IBlockState blockStateAntiTrap = this.getBlockStateAntiTrap(pos);
            Block block = blockStateAntiTrap.getBlock();
            if ((!ignoreBlockWithoutBoundingBox || block.getCollisionBoundingBox(this, pos, blockStateAntiTrap) != null) && block.canCollideCheck(blockStateAntiTrap, stopOnLiquid)) {
                MovingObjectPosition hit = block.collisionRayTrace(this, pos, vec31, vec32);
                if (hit != null) {
                    return hit;
                }
            }

            MovingObjectPosition hit2 = null;
            int n7 = 200;

            while (n7-- >= 0) {
                if (Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord)) {
                    return null;
                }

                if (n4 == n && n5 == n2 && n6 == n3) {
                    return returnLastUncollidableBlock ? hit2 : null;
                }

                boolean flag = true;
                boolean flag2 = true;
                boolean flag3 = true;
                double d = 999.0;
                double d2 = 999.0;
                double d3 = 999.0;
                if (n > n4) {
                    d = n4 + 1.0;
                } else if (n < n4) {
                    d = n4 + 0.0;
                } else {
                    flag = false;
                }

                if (n2 > n5) {
                    d2 = n5 + 1.0;
                } else if (n2 < n5) {
                    d2 = n5 + 0.0;
                } else {
                    flag2 = false;
                }

                if (n3 > n6) {
                    d3 = n6 + 1.0;
                } else if (n3 < n6) {
                    d3 = n6 + 0.0;
                } else {
                    flag3 = false;
                }

                double d4 = 999.0;
                double d5 = 999.0;
                double d6 = 999.0;
                double d7 = vec32.xCoord - vec31.xCoord;
                double d8 = vec32.yCoord - vec31.yCoord;
                double d9 = vec32.zCoord - vec31.zCoord;
                if (flag) {
                    d4 = (d - vec31.xCoord) / d7;
                }

                if (flag2) {
                    d5 = (d2 - vec31.yCoord) / d8;
                }

                if (flag3) {
                    d6 = (d3 - vec31.zCoord) / d9;
                }

                if (d4 == -0.0) {
                    d4 = -1.0E-4;
                }

                if (d5 == -0.0) {
                    d5 = -1.0E-4;
                }

                if (d6 == -0.0) {
                    d6 = -1.0E-4;
                }

                EnumFacing facing;
                if (d4 < d5 && d4 < d6) {
                    facing = n > n4 ? EnumFacing.WEST : EnumFacing.EAST;
                    vec31 = new Vec3(d, vec31.yCoord + d8 * d4, vec31.zCoord + d9 * d4);
                } else if (d5 < d6) {
                    facing = n2 > n5 ? EnumFacing.DOWN : EnumFacing.UP;
                    vec31 = new Vec3(vec31.xCoord + d7 * d5, d2, vec31.zCoord + d9 * d5);
                } else {
                    facing = n3 > n6 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                    vec31 = new Vec3(vec31.xCoord + d7 * d6, vec31.yCoord + d8 * d6, d3);
                }

                n4 = MathHelper.floor_double(vec31.xCoord) - (facing == EnumFacing.EAST ? 1 : 0);
                n5 = MathHelper.floor_double(vec31.yCoord) - (facing == EnumFacing.UP ? 1 : 0);
                n6 = MathHelper.floor_double(vec31.zCoord) - (facing == EnumFacing.SOUTH ? 1 : 0);
                pos = new BlockPos(n4, n5, n6);
                IBlockState blockStateAntiTrap2 = this.getBlockStateAntiTrap(pos);
                Block block2 = blockStateAntiTrap2.getBlock();
                if (!ignoreBlockWithoutBoundingBox || block2.getCollisionBoundingBox(this, pos, blockStateAntiTrap2) != null) {
                    if (block2.canCollideCheck(blockStateAntiTrap2, stopOnLiquid)) {
                        MovingObjectPosition hit3 = block2.collisionRayTrace(this, pos, vec31, vec32);
                        if (hit3 != null) {
                            return hit3;
                        }
                    } else {
                        hit2 = new MovingObjectPosition(MovingObjectType.MISS, vec31, facing, pos);
                    }
                }
            }

            return returnLastUncollidableBlock ? hit2 : null;
        }
        return null;
    }

    private IBlockState getBlockStateAntiTrap(BlockPos blockPos) {
        AntiTrap instance = AntiTrap.getInstance();
        if (instance != null && instance.checkState44() && instance.matchesWorld(this, blockPos)) {
            if (instance.getSetAirSetting().isEnabled()) {
                this.setBlockToAir(blockPos);
            }

            return Blocks.air.getDefaultState();
        }
        return this.getBlockState(blockPos);
    }

    public void playSoundAtEntity(Entity entityIn, String name, float volume, float pitch) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).playSound(name, entityIn.posX, entityIn.posY, entityIn.posZ, volume, pitch);
        }
    }

    public void playSoundToNearExcept(EntityPlayer player, String name, float volume, float pitch) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).playSoundToNearExcept(player, name, player.posX, player.posY, player.posZ, volume, pitch);
        }
    }

    public void playSoundEffect(double x, double y, double z, String soundName, float volume, float pitch) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).playSound(soundName, x, y, z, volume, pitch);
        }
    }

    public void playSound(double x, double y, double z, String soundName, float volume, float pitch, boolean distanceDelay) {
    }

    public void playRecord(BlockPos pos, String name) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).playRecord(name, pos);
        }
    }

    public void spawnParticle(EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... p_175688_14_) {
        this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, p_175688_14_);
    }

    public void spawnParticle(
        EnumParticleTypes particleType, boolean p_175682_2_, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... p_175682_15_
    ) {
        this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange() | p_175682_2_, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, p_175682_15_);
    }

    private void spawnParticle(int particleID, boolean p_175720_2_, double xCood, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... p_175720_15_) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).spawnParticle(particleID, p_175720_2_, xCood, yCoord, zCoord, xOffset, yOffset, zOffset, p_175720_15_);
        }
    }

    public boolean addWeatherEffect(Entity entityIn) {
        this.weatherEffects.add(entityIn);
        return true;
    }

    public boolean spawnEntityInWorld(Entity entityIn) {
        int n = MathHelper.floor_double(entityIn.posX / 16.0);
        int n2 = MathHelper.floor_double(entityIn.posZ / 16.0);
        boolean forceSpawn = entityIn.forceSpawn;
        if (entityIn instanceof EntityPlayer) {
            forceSpawn = true;
        }

        if (!forceSpawn && !this.isChunkLoaded(n, n2, true)) {
            return false;
        }
        if (entityIn instanceof EntityPlayer player) {
            this.playerEntities.add(player);
            this.updateAllPlayersSleepingFlag();
        }

        this.getChunkFromChunkCoords(n, n2).addEntity(entityIn);
        this.loadedEntityList.add(entityIn);
        this.onEntityAdded(entityIn);
        return true;
    }

    public void onEntityAdded(Entity entityIn) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).onEntityAdded(entityIn);
        }
    }

    public void onEntityRemoved(Entity entityIn) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).onEntityRemoved(entityIn);
        }
    }

    public void removeEntity(Entity entityIn) {
        if (entityIn.riddenByEntity != null) {
            entityIn.riddenByEntity.mountEntity((Entity)null);
        }

        if (entityIn.ridingEntity != null) {
            entityIn.mountEntity((Entity)null);
        }

        entityIn.setDead();
        if (entityIn instanceof EntityPlayer) {
            this.playerEntities.remove(entityIn);
            this.updateAllPlayersSleepingFlag();
            this.onEntityRemoved(entityIn);
        }
    }

    public void removePlayerEntityDangerously(Entity entityIn) {
        entityIn.setDead();
        if (entityIn instanceof EntityPlayer) {
            this.playerEntities.remove(entityIn);
            this.updateAllPlayersSleepingFlag();
        }

        int chunkCoordX = entityIn.chunkCoordX;
        int chunkCoordZ = entityIn.chunkCoordZ;
        if (entityIn.addedToChunk && this.isChunkLoaded(chunkCoordX, chunkCoordZ, true)) {
            this.getChunkFromChunkCoords(chunkCoordX, chunkCoordZ).removeEntity(entityIn);
        }

        this.loadedEntityList.remove(entityIn);
        this.onEntityRemoved(entityIn);
    }

    public void addWorldAccess(IWorldAccess worldAccess) {
        this.worldAccesses.add(worldAccess);
    }

    public void removeWorldAccess(IWorldAccess worldAccess) {
        this.worldAccesses.remove(worldAccess);
    }

    public List<AxisAlignedBB> getCollidingBoundingBoxes(Entity entityIn, AxisAlignedBB bb) {
        List list = Lists.newArrayList();
        int n = MathHelper.floor_double(bb.minX);
        int n2 = MathHelper.floor_double(bb.maxX + 1.0);
        int n3 = MathHelper.floor_double(bb.minY);
        int n4 = MathHelper.floor_double(bb.maxY + 1.0);
        int n5 = MathHelper.floor_double(bb.minZ);
        int n6 = MathHelper.floor_double(bb.maxZ + 1.0);
        WorldBorder worldBorder = this.getWorldBorder();
        boolean outsideBorder = entityIn.isOutsideBorder();
        boolean insideBorder = this.isInsideBorder(worldBorder, entityIn);
        IBlockState defaultState = Blocks.stone.getDefaultState();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int i = n; i < n2; i++) {
            for (int j = n5; j < n6; j++) {
                if (this.isBlockLoaded(mutablePos.func_181079_c(i, 64, j))) {
                    for (int k = n3 - 1; k < n4; k++) {
                        mutablePos.func_181079_c(i, k, j);
                        if (outsideBorder && insideBorder) {
                            entityIn.setOutsideBorder(false);
                        } else if (!outsideBorder && !insideBorder) {
                            entityIn.setOutsideBorder(true);
                        }

                        IBlockState blockState = defaultState;
                        if (worldBorder.contains(mutablePos) || !insideBorder) {
                            blockState = this.getBlockState(mutablePos);
                        }

                        blockState.getBlock().addCollisionBoxesToList(this, mutablePos, blockState, bb, list, entityIn);
                    }
                }
            }
        }

        double d = 0.25;
        var entitiesWithinAABBExcludingEntity = this.getEntitiesWithinAABBExcludingEntity(entityIn, bb.expand(d, d, d));

        for (int idx = 0; idx < entitiesWithinAABBExcludingEntity.size(); idx++) {
            if (entityIn.riddenByEntity != entitiesWithinAABBExcludingEntity && entityIn.ridingEntity != entitiesWithinAABBExcludingEntity) {
                AxisAlignedBB collisionBoundingBox = ((Entity)entitiesWithinAABBExcludingEntity.get(idx)).getCollisionBoundingBox();
                if (collisionBoundingBox != null && collisionBoundingBox.intersectsWith(bb)) {
                    list.add(collisionBoundingBox);
                }

                collisionBoundingBox = entityIn.getCollisionBox((Entity)entitiesWithinAABBExcludingEntity.get(idx));
                if (collisionBoundingBox != null && collisionBoundingBox.intersectsWith(bb)) {
                    list.add(collisionBoundingBox);
                }
            }
        }

        return list;
    }

    public boolean isInsideBorder(WorldBorder worldBorderIn, Entity entityIn) {
        double d = worldBorderIn.minX();
        double d2 = worldBorderIn.minZ();
        double d3 = worldBorderIn.maxX();
        double d4 = worldBorderIn.maxZ();
        if (entityIn.isOutsideBorder()) {
            d++;
            d2++;
            d3--;
            d4--;
        } else {
            d--;
            d2--;
            d3++;
            d4++;
        }

        return entityIn.posX > d && entityIn.posX < d3 && entityIn.posZ > d2 && entityIn.posZ < d4;
    }

    public List<AxisAlignedBB> func_147461_a(AxisAlignedBB bb) {
        List list = Lists.newArrayList();
        int n = MathHelper.floor_double(bb.minX);
        int n2 = MathHelper.floor_double(bb.maxX + 1.0);
        int n3 = MathHelper.floor_double(bb.minY);
        int n4 = MathHelper.floor_double(bb.maxY + 1.0);
        int n5 = MathHelper.floor_double(bb.minZ);
        int n6 = MathHelper.floor_double(bb.maxZ + 1.0);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int i = n; i < n2; i++) {
            for (int j = n5; j < n6; j++) {
                if (this.isBlockLoaded(mutablePos.func_181079_c(i, 64, j))) {
                    for (int k = n3 - 1; k < n4; k++) {
                        mutablePos.func_181079_c(i, k, j);
                        IBlockState blockState;
                        if (i >= -30000000 && i < 30000000 && j >= -30000000 && j < 30000000) {
                            blockState = this.getBlockState(mutablePos);
                        } else {
                            blockState = Blocks.bedrock.getDefaultState();
                        }

                        blockState.getBlock().addCollisionBoxesToList(this, mutablePos, blockState, bb, list, (Entity)null);
                    }
                }
            }
        }

        return list;
    }

    public int calculateSkylightSubtracted(float p_72967_1_) {
        float celestialAngle = this.getCelestialAngle(p_72967_1_);
        float f = 1.0F - (MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F);
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);
        f = 1.0F - f;
        f = (float)(f * (1.0 - this.getRainStrength(p_72967_1_) * 5.0F / 16.0));
        f = (float)(f * (1.0 - this.getThunderStrength(p_72967_1_) * 5.0F / 16.0));
        f = 1.0F - f;
        return (int)(f * 11.0F);
    }

    public float getSunBrightness(float p_72971_1_) {
        float celestialAngle = this.getCelestialAngle(p_72971_1_);
        float f = 1.0F - (MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.2F);
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);
        f = 1.0F - f;
        f = (float)(f * (1.0 - this.getRainStrength(p_72971_1_) * 5.0F / 16.0));
        f = (float)(f * (1.0 - this.getThunderStrength(p_72971_1_) * 5.0F / 16.0));
        return f * 0.8F + 0.2F;
    }

    public Vec3 getSkyColor(Entity entityIn, float partialTicks) {
        float celestialAngle = this.getCelestialAngle(partialTicks);
        float f = MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);
        int n = MathHelper.floor_double(entityIn.posX);
        int n2 = MathHelper.floor_double(entityIn.posY);
        int n3 = MathHelper.floor_double(entityIn.posZ);
        BlockPos pos = new BlockPos(n, n2, n3);
        BiomeGenBase biomeGenForCoords = this.getBiomeGenForCoords(pos);
        float floatTemperature = biomeGenForCoords.getFloatTemperature(pos);
        int skyColorByTemp = biomeGenForCoords.getSkyColorByTemp(floatTemperature);
        float f2 = (skyColorByTemp >> 16 & 0xFF) / 255.0F;
        float f3 = (skyColorByTemp >> 8 & 0xFF) / 255.0F;
        float f4 = (skyColorByTemp & 0xFF) / 255.0F;
        float f5 = f2 * f;
        float f6 = f3 * f;
        float f7 = f4 * f;
        float rainStrength = this.getRainStrength(partialTicks);
        if (rainStrength > 0.0F) {
            float f8 = (f5 * 0.3F + f6 * 0.59F + f7 * 0.11F) * 0.6F;
            float f9 = 1.0F - rainStrength * 0.75F;
            f5 = f5 * f9 + f8 * (1.0F - f9);
            f6 = f6 * f9 + f8 * (1.0F - f9);
            f7 = f7 * f9 + f8 * (1.0F - f9);
        }

        float thunderStrength = this.getThunderStrength(partialTicks);
        if (thunderStrength > 0.0F) {
            float f10 = (f5 * 0.3F + f6 * 0.59F + f7 * 0.11F) * 0.2F;
            float f11 = 1.0F - thunderStrength * 0.75F;
            f5 = f5 * f11 + f10 * (1.0F - f11);
            f6 = f6 * f11 + f10 * (1.0F - f11);
            f7 = f7 * f11 + f10 * (1.0F - f11);
        }

        if (this.lastLightningBolt > 0) {
            float f12 = this.lastLightningBolt - partialTicks;
            if (f12 > 1.0F) {
                f12 = 1.0F;
            }

            f12 *= 0.45F;
            f5 = f5 * (1.0F - f12) + 0.8F * f12;
            f6 = f6 * (1.0F - f12) + 0.8F * f12;
            f7 = f7 * (1.0F - f12) + 1.0F * f12;
        }

        return new Vec3(f5, f6, f7);
    }

    public float getCelestialAngle(float partialTicks) {
        return this.provider.calculateCelestialAngle(this.worldInfo.getWorldTime(), partialTicks);
    }

    public int getMoonPhase() {
        return this.provider.getMoonPhase(this.worldInfo.getWorldTime());
    }

    public float getCurrentMoonPhaseFactor() {
        return WorldProvider.moonPhaseFactors[this.provider.getMoonPhase(this.worldInfo.getWorldTime())];
    }

    public float getCelestialAngleRadians(float partialTicks) {
        float celestialAngle = this.getCelestialAngle(partialTicks);
        return celestialAngle * (float) Math.PI * 2.0F;
    }

    public Vec3 getCloudColour(float partialTicks) {
        float celestialAngle = this.getCelestialAngle(partialTicks);
        float f = MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
        float f2 = MathHelper.clamp_float(f, 0.0F, 1.0F);
        float f3 = (float)(this.cloudColour >> 16 & 255L) / 255.0F;
        float f4 = (float)(this.cloudColour >> 8 & 255L) / 255.0F;
        float f5 = (float)(this.cloudColour & 255L) / 255.0F;
        float rainStrength = this.getRainStrength(partialTicks);
        if (rainStrength > 0.0F) {
            float f6 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.6F;
            float f7 = 1.0F - rainStrength * 0.95F;
            f3 = f3 * f7 + f6 * (1.0F - f7);
            f4 = f4 * f7 + f6 * (1.0F - f7);
            f5 = f5 * f7 + f6 * (1.0F - f7);
        }

        float f8 = f3 * (f2 * 0.9F + 0.1F);
        float f9 = f4 * (f2 * 0.9F + 0.1F);
        float f10 = f5 * (f2 * 0.85F + 0.15F);
        float thunderStrength = this.getThunderStrength(partialTicks);
        if (thunderStrength > 0.0F) {
            float f11 = (f8 * 0.3F + f9 * 0.59F + f10 * 0.11F) * 0.2F;
            float f12 = 1.0F - thunderStrength * 0.95F;
            f8 = f8 * f12 + f11 * (1.0F - f12);
            f9 = f9 * f12 + f11 * (1.0F - f12);
            f10 = f10 * f12 + f11 * (1.0F - f12);
        }

        return new Vec3(f8, f9, f10);
    }

    public Vec3 getFogColor(float partialTicks) {
        float celestialAngle = this.getCelestialAngle(partialTicks);
        return this.provider.getFogColor(celestialAngle, partialTicks);
    }

    public BlockPos getPrecipitationHeight(BlockPos pos) {
        return this.getChunkFromBlockCoords(pos).getPrecipitationHeight(pos);
    }

    public BlockPos getTopSolidOrLiquidBlock(BlockPos pos) {
        Chunk chunkFromBlockCoords = this.getChunkFromBlockCoords(pos);
        BlockPos pos2 = new BlockPos(pos.getX(), chunkFromBlockCoords.getTopFilledSegment() + 16, pos.getZ());

        while (pos2.getY() >= 0) {
            BlockPos pos3 = pos2.down();
            Material material = chunkFromBlockCoords.getBlock(pos3).getMaterial();
            if (material.blocksMovement() && material != Material.leaves) {
                break;
            }

            pos2 = pos3;
        }

        return pos2;
    }

    public float getStarBrightness(float partialTicks) {
        float celestialAngle = this.getCelestialAngle(partialTicks);
        float f = 1.0F - (MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.25F);
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);
        return f * f * 0.5F;
    }

    public void scheduleUpdate(BlockPos pos, Block blockIn, int delay) {
    }

    public void updateBlockTick(BlockPos pos, Block blockIn, int delay, int priority) {
    }

    public void scheduleBlockUpdate(BlockPos pos, Block blockIn, int delay, int priority) {
    }

    public void updateEntities() {
        this.theProfiler.startSection("entities");
        this.theProfiler.startSection("global");

        for (int i = 0; i < this.weatherEffects.size(); i++) {
            Entity entity = this.weatherEffects.get(i);

            try {
                entity.ticksExisted++;
                entity.onUpdate();
            } catch (Throwable e) {
                CrashReport crashReport = CrashReport.makeCrashReport(e, "Ticking entity");
                CrashReportCategory crashReportCategory = crashReport.makeCategory("Entity being ticked");
                if (entity == null) {
                    crashReportCategory.addCrashSection("Entity", "~~NULL~~");
                } else {
                    entity.addEntityCrashInfo(crashReportCategory);
                }

                throw new ReportedException(crashReport);
            }

            if (entity.isDead) {
                this.weatherEffects.remove(i--);
            }
        }

        this.theProfiler.endStartSection("remove");
        this.loadedEntityList.removeAll(this.unloadedEntityList);

        for (int j = 0; j < this.unloadedEntityList.size(); j++) {
            Entity entity2 = this.unloadedEntityList.get(j);
            int chunkCoordX = entity2.chunkCoordX;
            int chunkCoordZ = entity2.chunkCoordZ;
            if (entity2.addedToChunk && this.isChunkLoaded(chunkCoordX, chunkCoordZ, true)) {
                this.getChunkFromChunkCoords(chunkCoordX, chunkCoordZ).removeEntity(entity2);
            }
        }

        for (int k = 0; k < this.unloadedEntityList.size(); k++) {
            this.onEntityRemoved(this.unloadedEntityList.get(k));
        }

        this.unloadedEntityList.clear();
        this.theProfiler.endStartSection("regular");

        for (int idx = 0; idx < this.loadedEntityList.size(); idx++) {
            Entity entity3 = this.loadedEntityList.get(idx);
            if (entity3.ridingEntity != null) {
                if (!entity3.ridingEntity.isDead && entity3.ridingEntity.riddenByEntity == entity3) {
                    continue;
                }

                entity3.ridingEntity.riddenByEntity = null;
                entity3.ridingEntity = null;
            }

            this.theProfiler.startSection("tick");
            if (!entity3.isDead) {
                try {
                    this.updateEntity(entity3);
                } catch (Throwable e2) {
                    CrashReport crashReport2 = CrashReport.makeCrashReport(e2, "Ticking entity");
                    CrashReportCategory crashReportCategory2 = crashReport2.makeCategory("Entity being ticked");
                    entity3.addEntityCrashInfo(crashReportCategory2);
                    throw new ReportedException(crashReport2);
                }
            }

            this.theProfiler.endSection();
            this.theProfiler.startSection("remove");
            if (entity3.isDead) {
                int chunkCoordX2 = entity3.chunkCoordX;
                int chunkCoordZ2 = entity3.chunkCoordZ;
                if (entity3.addedToChunk && this.isChunkLoaded(chunkCoordX2, chunkCoordZ2, true)) {
                    this.getChunkFromChunkCoords(chunkCoordX2, chunkCoordZ2).removeEntity(entity3);
                }

                this.loadedEntityList.remove(idx--);
                this.onEntityRemoved(entity3);
            }

            this.theProfiler.endSection();
        }

        this.theProfiler.endStartSection("blockEntities");
        this.processingLoadedTiles = true;
        var it = this.tickableTileEntities.iterator();

        while (it.hasNext()) {
            TileEntity tileEntity = (TileEntity)it.next();
            if (!tileEntity.isInvalid() && tileEntity.hasWorldObj()) {
                BlockPos pos = tileEntity.getPos();
                if (this.isBlockLoaded(pos) && this.worldBorder.contains(pos)) {
                    try {
                        ((ITickable)tileEntity).update();
                    } catch (Throwable e3) {
                        CrashReport crashReport3 = CrashReport.makeCrashReport(e3, "Ticking block entity");
                        CrashReportCategory crashReportCategory3 = crashReport3.makeCategory("Block entity being ticked");
                        tileEntity.addInfoToCrashReport(crashReportCategory3);
                        throw new ReportedException(crashReport3);
                    }
                }
            }

            if (tileEntity.isInvalid()) {
                it.remove();
                this.loadedTileEntityList.remove(tileEntity);
                if (this.isBlockLoaded(tileEntity.getPos())) {
                    this.getChunkFromBlockCoords(tileEntity.getPos()).removeTileEntity(tileEntity.getPos());
                }
            }
        }

        this.processingLoadedTiles = false;
        if (!this.tileEntitiesToBeRemoved.isEmpty()) {
            this.tickableTileEntities.removeAll(this.tileEntitiesToBeRemoved);
            this.loadedTileEntityList.removeAll(this.tileEntitiesToBeRemoved);
            this.tileEntitiesToBeRemoved.clear();
        }

        this.theProfiler.endStartSection("pendingBlockEntities");
        if (!this.addedTileEntityList.isEmpty()) {
            for (int idx2 = 0; idx2 < this.addedTileEntityList.size(); idx2++) {
                TileEntity tileEntity2 = this.addedTileEntityList.get(idx2);
                if (!tileEntity2.isInvalid()) {
                    if (!this.loadedTileEntityList.contains(tileEntity2)) {
                        this.addTileEntity(tileEntity2);
                    }

                    if (this.isBlockLoaded(tileEntity2.getPos())) {
                        this.getChunkFromBlockCoords(tileEntity2.getPos()).addTileEntity(tileEntity2.getPos(), tileEntity2);
                    }

                    this.markBlockForUpdate(tileEntity2.getPos());
                }
            }

            this.addedTileEntityList.clear();
        }

        this.theProfiler.endSection();
        this.theProfiler.endSection();
    }

    public boolean addTileEntity(TileEntity tile) {
        boolean flag = this.loadedTileEntityList.add(tile);
        if (flag && tile instanceof ITickable) {
            this.tickableTileEntities.add(tile);
        }

        return flag;
    }

    public void addTileEntities(Collection<TileEntity> tileEntityCollection) {
        if (this.processingLoadedTiles) {
            this.addedTileEntityList.addAll(tileEntityCollection);
        } else {
            for (TileEntity tileEntity : tileEntityCollection) {
                this.loadedTileEntityList.add(tileEntity);
                if (tileEntity instanceof ITickable) {
                    this.tickableTileEntities.add(tileEntity);
                }
            }
        }
    }

    public void updateEntity(Entity ent) {
        this.updateEntityWithOptionalForce(ent, true);
    }

    public void updateEntityWithOptionalForce(Entity entityIn, boolean forceUpdate) {
        int n = MathHelper.floor_double(entityIn.posX);
        int n2 = MathHelper.floor_double(entityIn.posZ);
        int n3 = 32;
        if (!forceUpdate || this.isAreaLoaded(n - n3, 0, n2 - n3, n + n3, 0, n2 + n3, true)) {
            entityIn.lastTickPosX = entityIn.posX;
            entityIn.lastTickPosY = entityIn.posY;
            entityIn.lastTickPosZ = entityIn.posZ;
            entityIn.prevRotationYaw = entityIn.rotationYaw;
            entityIn.prevRotationPitch = entityIn.rotationPitch;
            if (forceUpdate && entityIn.addedToChunk) {
                entityIn.ticksExisted++;
                if (entityIn.ridingEntity != null) {
                    entityIn.updateRidden();
                } else {
                    entityIn.onUpdate();
                }
            }

            this.theProfiler.startSection("chunkCheck");
            if (Double.isNaN(entityIn.posX) || Double.isInfinite(entityIn.posX)) {
                entityIn.posX = entityIn.lastTickPosX;
            }

            if (Double.isNaN(entityIn.posY) || Double.isInfinite(entityIn.posY)) {
                entityIn.posY = entityIn.lastTickPosY;
            }

            if (Double.isNaN(entityIn.posZ) || Double.isInfinite(entityIn.posZ)) {
                entityIn.posZ = entityIn.lastTickPosZ;
            }

            if (Double.isNaN(entityIn.rotationPitch) || Double.isInfinite(entityIn.rotationPitch)) {
                entityIn.rotationPitch = entityIn.prevRotationPitch;
            }

            if (Double.isNaN(entityIn.rotationYaw) || Double.isInfinite(entityIn.rotationYaw)) {
                entityIn.rotationYaw = entityIn.prevRotationYaw;
            }

            int n4 = MathHelper.floor_double(entityIn.posX / 16.0);
            int n5 = MathHelper.floor_double(entityIn.posY / 16.0);
            int n6 = MathHelper.floor_double(entityIn.posZ / 16.0);
            if (!entityIn.addedToChunk || entityIn.chunkCoordX != n4 || entityIn.chunkCoordY != n5 || entityIn.chunkCoordZ != n6) {
                if (entityIn.addedToChunk && this.isChunkLoaded(entityIn.chunkCoordX, entityIn.chunkCoordZ, true)) {
                    this.getChunkFromChunkCoords(entityIn.chunkCoordX, entityIn.chunkCoordZ).removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
                }

                if (this.isChunkLoaded(n4, n6, true)) {
                    entityIn.addedToChunk = true;
                    this.getChunkFromChunkCoords(n4, n6).addEntity(entityIn);
                } else {
                    entityIn.addedToChunk = false;
                }
            }

            this.theProfiler.endSection();
            if (forceUpdate && entityIn.addedToChunk && entityIn.riddenByEntity != null) {
                if (!entityIn.riddenByEntity.isDead && entityIn.riddenByEntity.ridingEntity == entityIn) {
                    this.updateEntity(entityIn.riddenByEntity);
                } else {
                    entityIn.riddenByEntity.ridingEntity = null;
                    entityIn.riddenByEntity = null;
                }
            }
        }
    }

    public boolean checkNoEntityCollision(AxisAlignedBB bb) {
        return this.checkNoEntityCollision(bb, (Entity)null);
    }

    public boolean checkNoEntityCollision(AxisAlignedBB bb, Entity entityIn) {
        var entitiesWithinAABBExcludingEntity = this.getEntitiesWithinAABBExcludingEntity((Entity)null, bb);

        for (int i = 0; i < entitiesWithinAABBExcludingEntity.size(); i++) {
            Entity entity = (Entity)entitiesWithinAABBExcludingEntity.get(i);
            if (!entity.isDead && entity.preventEntitySpawning && entity != entityIn && (entityIn == null || entityIn.ridingEntity != entity && entityIn.riddenByEntity != entity)) {
                return false;
            }
        }

        return true;
    }

    public boolean checkBlockCollision(AxisAlignedBB bb) {
        int n = MathHelper.floor_double(bb.minX);
        int n2 = MathHelper.floor_double(bb.maxX);
        int n3 = MathHelper.floor_double(bb.minY);
        int n4 = MathHelper.floor_double(bb.maxY);
        int n5 = MathHelper.floor_double(bb.minZ);
        int n6 = MathHelper.floor_double(bb.maxZ);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int i = n; i <= n2; i++) {
            for (int j = n3; j <= n4; j++) {
                for (int k = n5; k <= n6; k++) {
                    Block block = this.getBlockState(mutablePos.func_181079_c(i, j, k)).getBlock();
                    if (block.getMaterial() != Material.air) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isAnyLiquid(AxisAlignedBB bb) {
        int n = MathHelper.floor_double(bb.minX);
        int n2 = MathHelper.floor_double(bb.maxX);
        int n3 = MathHelper.floor_double(bb.minY);
        int n4 = MathHelper.floor_double(bb.maxY);
        int n5 = MathHelper.floor_double(bb.minZ);
        int n6 = MathHelper.floor_double(bb.maxZ);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int i = n; i <= n2; i++) {
            for (int j = n3; j <= n4; j++) {
                for (int k = n5; k <= n6; k++) {
                    Block block = this.getBlockState(mutablePos.func_181079_c(i, j, k)).getBlock();
                    if (block.getMaterial().isLiquid()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isFlammableWithin(AxisAlignedBB bb) {
        int n = MathHelper.floor_double(bb.minX);
        int n2 = MathHelper.floor_double(bb.maxX + 1.0);
        int n3 = MathHelper.floor_double(bb.minY);
        int n4 = MathHelper.floor_double(bb.maxY + 1.0);
        int n5 = MathHelper.floor_double(bb.minZ);
        int n6 = MathHelper.floor_double(bb.maxZ + 1.0);
        if (this.isAreaLoaded(n, n3, n5, n2, n4, n6, true)) {
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

            for (int i = n; i < n2; i++) {
                for (int j = n3; j < n4; j++) {
                    for (int k = n5; k < n6; k++) {
                        Block block = this.getBlockState(mutablePos.func_181079_c(i, j, k)).getBlock();
                        if (block == Blocks.fire || block == Blocks.flowing_lava || block == Blocks.lava) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean handleMaterialAcceleration(AxisAlignedBB bb, Material materialIn, Entity entityIn) {
        int n = MathHelper.floor_double(bb.minX);
        int n2 = MathHelper.floor_double(bb.maxX + 1.0);
        int n3 = MathHelper.floor_double(bb.minY);
        int n4 = MathHelper.floor_double(bb.maxY + 1.0);
        int n5 = MathHelper.floor_double(bb.minZ);
        int n6 = MathHelper.floor_double(bb.maxZ + 1.0);
        if (!this.isAreaLoaded(n, n3, n5, n2, n4, n6, true)) {
            return false;
        }
        boolean flag = false;
        Vec3 vec = new Vec3(0.0, 0.0, 0.0);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int i = n; i < n2; i++) {
            for (int j = n3; j < n4; j++) {
                for (int k = n5; k < n6; k++) {
                    mutablePos.func_181079_c(i, j, k);
                    IBlockState blockState = this.getBlockState(mutablePos);
                    Block block = blockState.getBlock();
                    if (block.getMaterial() == materialIn) {
                        double d = j + 1 - BlockLiquid.getLiquidHeightPercent(blockState.getValue(BlockLiquid.LEVEL));
                        if (n4 >= d) {
                            flag = true;
                            vec = block.modifyAcceleration(this, mutablePos, entityIn, vec);
                        }
                    }
                }
            }
        }

        if (vec.lengthVector() > 0.0 && entityIn.isPushedByWater()) {
            vec = vec.normalize();
            double d2 = 0.014;
            entityIn.motionX = entityIn.motionX + vec.xCoord * d2;
            entityIn.motionY = entityIn.motionY + vec.yCoord * d2;
            entityIn.motionZ = entityIn.motionZ + vec.zCoord * d2;
        }

        return flag;
    }

    public boolean isMaterialInBB(AxisAlignedBB bb, Material materialIn) {
        int n = MathHelper.floor_double(bb.minX);
        int n2 = MathHelper.floor_double(bb.maxX + 1.0);
        int n3 = MathHelper.floor_double(bb.minY);
        int n4 = MathHelper.floor_double(bb.maxY + 1.0);
        int n5 = MathHelper.floor_double(bb.minZ);
        int n6 = MathHelper.floor_double(bb.maxZ + 1.0);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int i = n; i < n2; i++) {
            for (int j = n3; j < n4; j++) {
                for (int k = n5; k < n6; k++) {
                    if (this.getBlockState(mutablePos.func_181079_c(i, j, k)).getBlock().getMaterial() == materialIn) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isAABBInMaterial(AxisAlignedBB bb, Material materialIn) {
        int n = MathHelper.floor_double(bb.minX);
        int n2 = MathHelper.floor_double(bb.maxX + 1.0);
        int n3 = MathHelper.floor_double(bb.minY);
        int n4 = MathHelper.floor_double(bb.maxY + 1.0);
        int n5 = MathHelper.floor_double(bb.minZ);
        int n6 = MathHelper.floor_double(bb.maxZ + 1.0);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int i = n; i < n2; i++) {
            for (int j = n3; j < n4; j++) {
                for (int k = n5; k < n6; k++) {
                    IBlockState blockState = this.getBlockState(mutablePos.func_181079_c(i, j, k));
                    Block block = blockState.getBlock();
                    if (block.getMaterial() == materialIn) {
                        int val = blockState.getValue(BlockLiquid.LEVEL);
                        double d = j + 1;
                        if (val < 8) {
                            d = j + 1 - val / 8.0;
                        }

                        if (d >= bb.minY) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public Explosion createExplosion(Entity entityIn, double x, double y, double z, float strength, boolean isSmoking) {
        return this.newExplosion(entityIn, x, y, z, strength, false, isSmoking);
    }

    public Explosion newExplosion(Entity entityIn, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking) {
        Explosion explosion = new Explosion(this, entityIn, x, y, z, strength, isFlaming, isSmoking);
        explosion.doExplosionA();
        explosion.doExplosionB(true);
        return explosion;
    }

    public float getBlockDensity(Vec3 vec, AxisAlignedBB bb) {
        double d = 1.0 / ((bb.maxX - bb.minX) * 2.0 + 1.0);
        double d2 = 1.0 / ((bb.maxY - bb.minY) * 2.0 + 1.0);
        double d3 = 1.0 / ((bb.maxZ - bb.minZ) * 2.0 + 1.0);
        double d4 = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
        double d5 = (1.0 - Math.floor(1.0 / d3) * d3) / 2.0;
        if (d >= 0.0 && d2 >= 0.0 && d3 >= 0.0) {
            int n = 0;
            int n2 = 0;

            for (float i = 0.0F; i <= 1.0F; i = (float)(i + d)) {
                for (float j = 0.0F; j <= 1.0F; j = (float)(j + d2)) {
                    for (float k = 0.0F; k <= 1.0F; k = (float)(k + d3)) {
                        double d6 = bb.minX + (bb.maxX - bb.minX) * i;
                        double d7 = bb.minY + (bb.maxY - bb.minY) * j;
                        double d8 = bb.minZ + (bb.maxZ - bb.minZ) * k;
                        if (this.rayTraceBlocks(new Vec3(d6 + d4, d7, d8 + d5), vec) == null) {
                            n++;
                        }

                        n2++;
                    }
                }
            }

            return (float)n / n2;
        }
        return 0.0F;
    }

    public boolean extinguishFire(EntityPlayer player, BlockPos pos, EnumFacing side) {
        BlockPos pos2 = pos.offset(side);
        if (this.getBlockState(pos2).getBlock() == Blocks.fire) {
            this.playAuxSFXAtEntity(player, 1004, pos2, 0);
            this.setBlockToAir(pos2);
            return true;
        }
        return false;
    }

    public String getDebugLoadedEntities() {
        return "All: " + this.loadedEntityList.size();
    }

    public String getProviderName() {
        return this.chunkProvider.makeString();
    }

    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        if (!this.isValid(pos)) {
            return null;
        }
        TileEntity tileEntity = null;
        if (this.processingLoadedTiles) {
            for (int i = 0; i < this.addedTileEntityList.size(); i++) {
                TileEntity tileEntity2 = this.addedTileEntityList.get(i);
                if (!tileEntity2.isInvalid() && tileEntity2.getPos().equals(pos)) {
                    tileEntity = tileEntity2;
                    break;
                }
            }
        }

        if (tileEntity == null) {
            tileEntity = this.getChunkFromBlockCoords(pos).getTileEntity(pos, EnumCreateEntityType.IMMEDIATE);
        }

        if (tileEntity == null) {
            for (int j = 0; j < this.addedTileEntityList.size(); j++) {
                TileEntity tileEntity3 = this.addedTileEntityList.get(j);
                if (!tileEntity3.isInvalid() && tileEntity3.getPos().equals(pos)) {
                    tileEntity = tileEntity3;
                    break;
                }
            }
        }

        return tileEntity;
    }

    public void setTileEntity(BlockPos pos, TileEntity tileEntityIn) {
        if (tileEntityIn != null && !tileEntityIn.isInvalid()) {
            if (this.processingLoadedTiles) {
                tileEntityIn.setPos(pos);
                var it = this.addedTileEntityList.iterator();

                while (it.hasNext()) {
                    TileEntity tileEntity = (TileEntity)it.next();
                    if (tileEntity.getPos().equals(pos)) {
                        tileEntity.invalidate();
                        it.remove();
                    }
                }

                this.addedTileEntityList.add(tileEntityIn);
            } else {
                this.addTileEntity(tileEntityIn);
                this.getChunkFromBlockCoords(pos).addTileEntity(pos, tileEntityIn);
            }
        }
    }

    public void removeTileEntity(BlockPos pos) {
        TileEntity tileEntity = this.getTileEntity(pos);
        if (tileEntity != null && this.processingLoadedTiles) {
            tileEntity.invalidate();
            this.addedTileEntityList.remove(tileEntity);
        } else {
            if (tileEntity != null) {
                this.addedTileEntityList.remove(tileEntity);
                this.loadedTileEntityList.remove(tileEntity);
                this.tickableTileEntities.remove(tileEntity);
            }

            this.getChunkFromBlockCoords(pos).removeTileEntity(pos);
        }
    }

    public void markTileEntityForRemoval(TileEntity tileEntityIn) {
        this.tileEntitiesToBeRemoved.add(tileEntityIn);
    }

    public boolean isBlockFullCube(BlockPos pos) {
        IBlockState blockState = this.getBlockState(pos);
        AxisAlignedBB collisionBoundingBox = blockState.getBlock().getCollisionBoundingBox(this, pos, blockState);
        return collisionBoundingBox != null && collisionBoundingBox.getAverageEdgeLength() >= 1.0;
    }

    public static boolean doesBlockHaveSolidTopSurface(IBlockAccess blockAccess, BlockPos pos) {
        IBlockState blockState = blockAccess.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.getMaterial().isOpaque() && block.isFullCube()
            ? true
            : (
                block instanceof BlockStairs
                    ? blockState.getValue(BlockStairs.HALF) == EnumHalf.TOP
                    : (
                        block instanceof BlockSlab
                            ? blockState.getValue(BlockSlab.HALF) == EnumBlockHalf.TOP
                            : (block instanceof BlockHopper ? true : (block instanceof BlockSnow ? blockState.getValue(BlockSnow.LAYERS) == 7 : false))
                    )
            );
    }

    public boolean isBlockNormalCube(BlockPos pos, boolean _default) {
        if (!this.isValid(pos)) {
            return _default;
        }
        Chunk chunk = this.chunkProvider.provideChunk(pos);
        if (chunk.isEmpty()) {
            return _default;
        }
        Block block = this.getBlockState(pos).getBlock();
        return block.getMaterial().isOpaque() && block.isFullCube();
    }

    public void calculateInitialSkylight() {
        int n = this.calculateSkylightSubtracted(1.0F);
        if (n != this.skylightSubtracted) {
            this.skylightSubtracted = n;
        }
    }

    public void setAllowedSpawnTypes(boolean hostile, boolean peaceful) {
        this.spawnHostileMobs = hostile;
        this.spawnPeacefulMobs = peaceful;
    }

    public void tick() {
        this.updateWeather();
    }

    protected void calculateInitialWeather() {
        if (this.worldInfo.isRaining()) {
            this.rainingStrength = 1.0F;
            if (this.worldInfo.isThundering()) {
                this.thunderingStrength = 1.0F;
            }
        }
    }

    protected void updateWeather() {
        if (!this.provider.getHasNoSky() && !this.isRemote) {
            int cleanWeatherTime = this.worldInfo.getCleanWeatherTime();
            if (cleanWeatherTime > 0) {
                this.worldInfo.setCleanWeatherTime(--cleanWeatherTime);
                this.worldInfo.setThunderTime(this.worldInfo.isThundering() ? 1 : 2);
                this.worldInfo.setRainTime(this.worldInfo.isRaining() ? 1 : 2);
            }

            int thunderTime = this.worldInfo.getThunderTime();
            if (thunderTime <= 0) {
                if (this.worldInfo.isThundering()) {
                    this.worldInfo.setThunderTime(this.rand.nextInt(12000) + 3600);
                } else {
                    this.worldInfo.setThunderTime(this.rand.nextInt(168000) + 12000);
                }
            } else {
                this.worldInfo.setThunderTime(--thunderTime);
                if (thunderTime <= 0) {
                    this.worldInfo.setThundering(!this.worldInfo.isThundering());
                }
            }

            this.prevThunderingStrength = this.thunderingStrength;
            if (this.worldInfo.isThundering()) {
                this.thunderingStrength = (float)(this.thunderingStrength + 0.01);
            } else {
                this.thunderingStrength = (float)(this.thunderingStrength - 0.01);
            }

            this.thunderingStrength = MathHelper.clamp_float(this.thunderingStrength, 0.0F, 1.0F);
            int rainTime = this.worldInfo.getRainTime();
            if (rainTime <= 0) {
                if (this.worldInfo.isRaining()) {
                    this.worldInfo.setRainTime(this.rand.nextInt(12000) + 12000);
                } else {
                    this.worldInfo.setRainTime(this.rand.nextInt(168000) + 12000);
                }
            } else {
                this.worldInfo.setRainTime(--rainTime);
                if (rainTime <= 0) {
                    this.worldInfo.setRaining(!this.worldInfo.isRaining());
                }
            }

            this.prevRainingStrength = this.rainingStrength;
            if (this.worldInfo.isRaining()) {
                this.rainingStrength = (float)(this.rainingStrength + 0.01);
            } else {
                this.rainingStrength = (float)(this.rainingStrength - 0.01);
            }

            this.rainingStrength = MathHelper.clamp_float(this.rainingStrength, 0.0F, 1.0F);
        }
    }

    protected void setActivePlayerChunksAndCheckLight() {
        this.activeChunkSet.clear();
        this.theProfiler.startSection("buildList");

        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayer player = this.playerEntities.get(i);
            int n = MathHelper.floor_double(player.posX / 16.0);
            int n2 = MathHelper.floor_double(player.posZ / 16.0);
            int renderDistanceChunks = this.getRenderDistanceChunks();

            for (int j = -renderDistanceChunks; j <= renderDistanceChunks; j++) {
                for (int k = -renderDistanceChunks; k <= renderDistanceChunks; k++) {
                    this.activeChunkSet.add(new ChunkCoordIntPair(j + n, k + n2));
                }
            }
        }

        this.theProfiler.endSection();
        if (this.ambientTickCountdown > 0) {
            this.ambientTickCountdown--;
        }

        this.theProfiler.startSection("playerCheckLight");
        if (!this.playerEntities.isEmpty()) {
            int n3 = this.rand.nextInt(this.playerEntities.size());
            EntityPlayer player2 = this.playerEntities.get(n3);
            int n4 = MathHelper.floor_double(player2.posX) + this.rand.nextInt(11) - 5;
            int n5 = MathHelper.floor_double(player2.posY) + this.rand.nextInt(11) - 5;
            int n6 = MathHelper.floor_double(player2.posZ) + this.rand.nextInt(11) - 5;
            this.checkLight(new BlockPos(n4, n5, n6));
        }

        this.theProfiler.endSection();
    }

    protected abstract int getRenderDistanceChunks();

    protected void playMoodSoundAndCheckLight(int p_147467_1_, int p_147467_2_, Chunk chunkIn) {
        this.theProfiler.endStartSection("moodSound");
        if (this.ambientTickCountdown == 0 && !this.isRemote) {
            this.updateLCG = this.updateLCG * 3 + 1013904223;
            int n = this.updateLCG >> 2;
            int n2 = n & 15;
            int n3 = n >> 8 & 15;
            int n4 = n >> 16 & 0xFF;
            BlockPos pos = new BlockPos(n2, n4, n3);
            Block block = chunkIn.getBlock(pos);
            int n5 = n2 + p_147467_1_;
            int n6 = n3 + p_147467_2_;
            if (block.getMaterial() == Material.air && this.getLight(pos) <= this.rand.nextInt(8) && this.getLightFor(EnumSkyBlock.SKY, pos) <= 0) {
                EntityPlayer closestPlayer = this.getClosestPlayer(n5 + 0.5, n4 + 0.5, n6 + 0.5, 8.0);
                if (closestPlayer != null && closestPlayer.getDistanceSq(n5 + 0.5, n4 + 0.5, n6 + 0.5) > 4.0) {
                    this.playSoundEffect(n5 + 0.5, n4 + 0.5, n6 + 0.5, "ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
                    this.ambientTickCountdown = this.rand.nextInt(12000) + 6000;
                }
            }
        }

        this.theProfiler.endStartSection("checkLight");
        chunkIn.enqueueRelightChecks();
    }

    public void updateBlocks() {
        this.setActivePlayerChunksAndCheckLight();
    }

    public void forceBlockUpdateTick(Block blockType, BlockPos pos, Random random) {
        this.scheduledUpdatesAreImmediate = true;
        blockType.updateTick(this, pos, this.getBlockState(pos), random);
        this.scheduledUpdatesAreImmediate = false;
    }

    public boolean canBlockFreezeWater(BlockPos pos) {
        return this.canBlockFreeze(pos, false);
    }

    public boolean canBlockFreezeNoWater(BlockPos pos) {
        return this.canBlockFreeze(pos, true);
    }

    public boolean canBlockFreeze(BlockPos pos, boolean noWaterAdj) {
        BiomeGenBase biomeGenForCoords = this.getBiomeGenForCoords(pos);
        float floatTemperature = biomeGenForCoords.getFloatTemperature(pos);
        if (floatTemperature > 0.15F) {
            return false;
        }
        if (pos.getY() >= 0 && pos.getY() < 256 && this.getLightFor(EnumSkyBlock.BLOCK, pos) < 10) {
            IBlockState blockState = this.getBlockState(pos);
            Block block = blockState.getBlock();
            if ((block == Blocks.water || block == Blocks.flowing_water) && blockState.getValue(BlockLiquid.LEVEL) == 0) {
                if (!noWaterAdj) {
                    return true;
                }

                boolean flag = this.isWater(pos.west()) && this.isWater(pos.east()) && this.isWater(pos.north()) && this.isWater(pos.south());
                if (!flag) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isWater(BlockPos pos) {
        return this.getBlockState(pos).getBlock().getMaterial() == Material.water;
    }

    public boolean canSnowAt(BlockPos pos, boolean checkLight) {
        BiomeGenBase biomeGenForCoords = this.getBiomeGenForCoords(pos);
        float floatTemperature = biomeGenForCoords.getFloatTemperature(pos);
        if (floatTemperature > 0.15F) {
            return false;
        }
        if (!checkLight) {
            return true;
        }
        if (pos.getY() >= 0 && pos.getY() < 256 && this.getLightFor(EnumSkyBlock.BLOCK, pos) < 10) {
            Block block = this.getBlockState(pos).getBlock();
            if (block.getMaterial() == Material.air && Blocks.snow_layer.canPlaceBlockAt(this, pos)) {
                return true;
            }
        }

        return false;
    }

    public boolean checkLight(BlockPos pos) {
        boolean flag = false;
        if (!this.provider.getHasNoSky()) {
            flag |= this.checkLightFor(EnumSkyBlock.SKY, pos);
        }

        return flag | this.checkLightFor(EnumSkyBlock.BLOCK, pos);
    }

    private int getRawLight(BlockPos pos, EnumSkyBlock lightType) {
        if (lightType == EnumSkyBlock.SKY && this.canSeeSky(pos)) {
            return 15;
        }
        Block block = this.getBlockState(pos).getBlock();
        int n = lightType == EnumSkyBlock.SKY ? 0 : block.getLightValue();
        int lightOpacity = block.getLightOpacity();
        if (lightOpacity >= 15 && block.getLightValue() > 0) {
            lightOpacity = 1;
        }

        if (lightOpacity < 1) {
            lightOpacity = 1;
        }

        if (lightOpacity >= 15) {
            return 0;
        }
        if (n >= 14) {
            return n;
        }
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos pos2 = pos.offset(facing);
            int n2 = this.getLightFor(lightType, pos2) - lightOpacity;
            if (n2 > n) {
                n = n2;
            }

            if (n >= 14) {
                return n;
            }
        }

        return n;
    }

    public boolean checkLightFor(EnumSkyBlock lightType, BlockPos pos) {
        if (!this.isAreaLoaded(pos, 17, false)) {
            return false;
        }
        int n = 0;
        int n2 = 0;
        this.theProfiler.startSection("getBrightness");
        int lightFor = this.getLightFor(lightType, pos);
        int rawLight = this.getRawLight(pos, lightType);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (rawLight > lightFor) {
            this.lightUpdateBlockList[n2++] = 133152;
        } else if (rawLight < lightFor) {
            this.lightUpdateBlockList[n2++] = 133152 | lightFor << 18;

            while (n < n2) {
                int n3 = this.lightUpdateBlockList[n++];
                int n4 = (n3 & 63) - 32 + x;
                int n5 = (n3 >> 6 & 63) - 32 + y;
                int n6 = (n3 >> 12 & 63) - 32 + z;
                int n7 = n3 >> 18 & 15;
                BlockPos pos2 = new BlockPos(n4, n5, n6);
                int lightFor2 = this.getLightFor(lightType, pos2);
                if (lightFor2 == n7) {
                    this.setLightFor(lightType, pos2, 0);
                    if (n7 > 0) {
                        int n8 = MathHelper.abs_int(n4 - x);
                        int n9 = MathHelper.abs_int(n5 - y);
                        int n10 = MathHelper.abs_int(n6 - z);
                        if (n8 + n9 + n10 < 17) {
                            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

                            for (EnumFacing facing : EnumFacing.values()) {
                                int n11 = n4 + facing.getFrontOffsetX();
                                int n12 = n5 + facing.getFrontOffsetY();
                                int n13 = n6 + facing.getFrontOffsetZ();
                                mutablePos.func_181079_c(n11, n12, n13);
                                int n14 = Math.max(1, this.getBlockState(mutablePos).getBlock().getLightOpacity());
                                lightFor2 = this.getLightFor(lightType, mutablePos);
                                if (lightFor2 == n7 - n14 && n2 < this.lightUpdateBlockList.length) {
                                    this.lightUpdateBlockList[n2++] = n11 - x + 32
                                        | n12 - y + 32 << 6
                                        | n13 - z + 32 << 12
                                        | n7 - n14 << 18;
                                }
                            }
                        }
                    }
                }
            }

            n = 0;
        }

        this.theProfiler.endSection();
        this.theProfiler.startSection("checkedPosition < toCheckCount");

        while (n < n2) {
            int n15 = this.lightUpdateBlockList[n++];
            int n16 = (n15 & 63) - 32 + x;
            int n17 = (n15 >> 6 & 63) - 32 + y;
            int n18 = (n15 >> 12 & 63) - 32 + z;
            BlockPos pos3 = new BlockPos(n16, n17, n18);
            int lightFor3 = this.getLightFor(lightType, pos3);
            int rawLight2 = this.getRawLight(pos3, lightType);
            if (rawLight2 != lightFor3) {
                this.setLightFor(lightType, pos3, rawLight2);
                if (rawLight2 > lightFor3) {
                    int n19 = Math.abs(n16 - x);
                    int n20 = Math.abs(n17 - y);
                    int n21 = Math.abs(n18 - z);
                    boolean flag = n2 < this.lightUpdateBlockList.length - 6;
                    if (n19 + n20 + n21 < 17 && flag) {
                        if (this.getLightFor(lightType, pos3.west()) < rawLight2) {
                            this.lightUpdateBlockList[n2++] = n16 - 1 - x + 32 + (n17 - y + 32 << 6) + (n18 - z + 32 << 12);
                        }

                        if (this.getLightFor(lightType, pos3.east()) < rawLight2) {
                            this.lightUpdateBlockList[n2++] = n16 + 1 - x + 32 + (n17 - y + 32 << 6) + (n18 - z + 32 << 12);
                        }

                        if (this.getLightFor(lightType, pos3.down()) < rawLight2) {
                            this.lightUpdateBlockList[n2++] = n16 - x + 32 + (n17 - 1 - y + 32 << 6) + (n18 - z + 32 << 12);
                        }

                        if (this.getLightFor(lightType, pos3.up()) < rawLight2) {
                            this.lightUpdateBlockList[n2++] = n16 - x + 32 + (n17 + 1 - y + 32 << 6) + (n18 - z + 32 << 12);
                        }

                        if (this.getLightFor(lightType, pos3.north()) < rawLight2) {
                            this.lightUpdateBlockList[n2++] = n16 - x + 32 + (n17 - y + 32 << 6) + (n18 - 1 - z + 32 << 12);
                        }

                        if (this.getLightFor(lightType, pos3.south()) < rawLight2) {
                            this.lightUpdateBlockList[n2++] = n16 - x + 32 + (n17 - y + 32 << 6) + (n18 + 1 - z + 32 << 12);
                        }
                    }
                }
            }
        }

        this.theProfiler.endSection();
        return true;
    }

    public boolean tickUpdates(boolean p_72955_1_) {
        return false;
    }

    public List<NextTickListEntry> getPendingBlockUpdates(Chunk chunkIn, boolean p_72920_2_) {
        return null;
    }

    public List<NextTickListEntry> func_175712_a(StructureBoundingBox structureBB, boolean p_175712_2_) {
        return null;
    }

    public List<Entity> getEntitiesWithinAABBExcludingEntity(Entity entityIn, AxisAlignedBB bb) {
        return this.getEntitiesInAABBexcluding(entityIn, bb, EntitySelectors.NOT_SPECTATING);
    }

    public List<Entity> getEntitiesInAABBexcluding(Entity entityIn, AxisAlignedBB boundingBox, Predicate<? super Entity> predicate) {
        List list = Lists.newArrayList();
        int n = MathHelper.floor_double((boundingBox.minX - 2.0) / 16.0);
        int n2 = MathHelper.floor_double((boundingBox.maxX + 2.0) / 16.0);
        int n3 = MathHelper.floor_double((boundingBox.minZ - 2.0) / 16.0);
        int n4 = MathHelper.floor_double((boundingBox.maxZ + 2.0) / 16.0);

        for (int i = n; i <= n2; i++) {
            for (int j = n3; j <= n4; j++) {
                if (this.isChunkLoaded(i, j, true)) {
                    this.getChunkFromChunkCoords(i, j).getEntitiesWithinAABBForEntity(entityIn, boundingBox, list, predicate);
                }
            }
        }

        return list;
    }

    public <T extends Entity> List<T> getEntities(Class<? extends T> entityType, Predicate<? super T> filter) {
        List list = Lists.newArrayList();

        for (Entity entity : this.loadedEntityList) {
            if (entityType.isAssignableFrom(entity.getClass()) && filter.apply((T)entity)) {
                list.add(entity);
            }
        }

        return list;
    }

    public <T extends Entity> List<T> getPlayers(Class<? extends T> playerType, Predicate<? super T> filter) {
        List list = Lists.newArrayList();

        for (Entity playerEntity : this.playerEntities) {
            if (playerType.isAssignableFrom(playerEntity.getClass()) && filter.apply((T)playerEntity)) {
                list.add(playerEntity);
            }
        }

        return list;
    }

    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> classEntity, AxisAlignedBB bb) {
        return this.getEntitiesWithinAABB(classEntity, bb, EntitySelectors.NOT_SPECTATING);
    }

    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> clazz, AxisAlignedBB aabb, Predicate<? super T> filter) {
        int n = MathHelper.floor_double((aabb.minX - 2.0) / 16.0);
        int n2 = MathHelper.floor_double((aabb.maxX + 2.0) / 16.0);
        int n3 = MathHelper.floor_double((aabb.minZ - 2.0) / 16.0);
        int n4 = MathHelper.floor_double((aabb.maxZ + 2.0) / 16.0);
        List list = Lists.newArrayList();

        for (int i = n; i <= n2; i++) {
            for (int j = n3; j <= n4; j++) {
                if (this.isChunkLoaded(i, j, true)) {
                    this.getChunkFromChunkCoords(i, j).getEntitiesOfTypeWithinAAAB(clazz, aabb, list, filter);
                }
            }
        }

        return list;
    }

    public <T extends Entity> T findNearestEntityWithinAABB(Class<? extends T> entityType, AxisAlignedBB aabb, T closestTo) {
        var entities = this.getEntitiesWithinAABB(entityType, aabb);
        Entity entity = null;
        double MAX_VALUE = Double.MAX_VALUE;

        for (int i = 0; i < entities.size(); i++) {
            Entity entity2 = (Entity)entities.get(i);
            if (entity2 != closestTo && EntitySelectors.NOT_SPECTATING.apply(entity2)) {
                double dist = closestTo.getDistanceSqToEntity(entity2);
                if (dist <= MAX_VALUE) {
                    entity = entity2;
                    MAX_VALUE = dist;
                }
            }
        }

        return (T)entity;
    }

    public Entity getEntityByID(int id) {
        return this.entitiesById.lookup(id);
    }

    public List<Entity> getLoadedEntityList() {
        return this.loadedEntityList;
    }

    public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) {
        if (this.isBlockLoaded(pos)) {
            this.getChunkFromBlockCoords(pos).setChunkModified();
        }
    }

    public int countEntities(Class<?> entityType) {
        int n = 0;

        for (Entity entity : this.loadedEntityList) {
            if ((!(entity instanceof EntityLiving) || !((EntityLiving)entity).isNoDespawnRequired()) && entityType.isAssignableFrom(entity.getClass())) {
                n++;
            }
        }

        return n;
    }

    public void loadEntities(Collection<Entity> entityCollection) {
        this.loadedEntityList.addAll(entityCollection);

        for (Entity entity : entityCollection) {
            this.onEntityAdded(entity);
        }
    }

    public void unloadEntities(Collection<Entity> entityCollection) {
        this.unloadedEntityList.addAll(entityCollection);
    }

    public boolean canBlockBePlaced(Block blockIn, BlockPos pos, boolean p_175716_3_, EnumFacing side, Entity entityIn, ItemStack itemStackIn) {
        Block block = this.getBlockState(pos).getBlock();
        AxisAlignedBB box = p_175716_3_ ? null : blockIn.getCollisionBoundingBox(this, pos, blockIn.getDefaultState());
        return box != null && !this.checkNoEntityCollision(box, entityIn)
            ? false
            : (
                block.getMaterial() == Material.circuits && blockIn == Blocks.anvil
                    ? true
                    : block.getMaterial().isReplaceable() && blockIn.canReplace(this, pos, side, itemStackIn)
            );
    }

    public int func_181545_F() {
        return this.field_181546_a;
    }

    public void func_181544_b(int p_181544_1_) {
        this.field_181546_a = p_181544_1_;
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        IBlockState blockState = this.getBlockState(pos);
        return blockState.getBlock().isProvidingStrongPower(this, pos, blockState, direction);
    }

    @Override
    public WorldType getWorldType() {
        return this.worldInfo.getTerrainType();
    }

    public int getStrongPower(BlockPos pos) {
        int n = 0;
        int n2 = Math.max(n, this.getStrongPower(pos.down(), EnumFacing.DOWN));
        if (n2 >= 15) {
            return n2;
        }
        n = Math.max(n2, this.getStrongPower(pos.up(), EnumFacing.UP));
        if (n >= 15) {
            return n;
        }
        int n3 = Math.max(n, this.getStrongPower(pos.north(), EnumFacing.NORTH));
        if (n3 >= 15) {
            return n3;
        }
        n = Math.max(n3, this.getStrongPower(pos.south(), EnumFacing.SOUTH));
        if (n >= 15) {
            return n;
        }
        int n4 = Math.max(n, this.getStrongPower(pos.west(), EnumFacing.WEST));
        if (n4 >= 15) {
            return n4;
        }
        n = Math.max(n4, this.getStrongPower(pos.east(), EnumFacing.EAST));
        return n >= 15 ? n : n;
    }

    public boolean isSidePowered(BlockPos pos, EnumFacing side) {
        return this.getRedstonePower(pos, side) > 0;
    }

    public int getRedstonePower(BlockPos pos, EnumFacing facing) {
        IBlockState blockState = this.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.isNormalCube() ? this.getStrongPower(pos) : block.isProvidingWeakPower(this, pos, blockState, facing);
    }

    public boolean isBlockPowered(BlockPos pos) {
        return this.getRedstonePower(pos.down(), EnumFacing.DOWN) > 0
            ? true
            : (
                this.getRedstonePower(pos.up(), EnumFacing.UP) > 0
                    ? true
                    : (
                        this.getRedstonePower(pos.north(), EnumFacing.NORTH) > 0
                            ? true
                            : (
                                this.getRedstonePower(pos.south(), EnumFacing.SOUTH) > 0
                                    ? true
                                    : (this.getRedstonePower(pos.west(), EnumFacing.WEST) > 0 ? true : this.getRedstonePower(pos.east(), EnumFacing.EAST) > 0)
                            )
                    )
            );
    }

    public int isBlockIndirectlyGettingPowered(BlockPos pos) {
        int n = 0;

        for (EnumFacing facing : EnumFacing.values()) {
            int redstonePower = this.getRedstonePower(pos.offset(facing), facing);
            if (redstonePower >= 15) {
                return 15;
            }

            if (redstonePower > n) {
                n = redstonePower;
            }
        }

        return n;
    }

    public EntityPlayer getClosestPlayerToEntity(Entity entityIn, double distance) {
        return this.getClosestPlayer(entityIn.posX, entityIn.posY, entityIn.posZ, distance);
    }

    public EntityPlayer getClosestPlayer(double x, double y, double z, double distance) {
        double d = -1.0;
        EntityPlayer player = null;

        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayer player2 = this.playerEntities.get(i);
            if (EntitySelectors.NOT_SPECTATING.apply(player2)) {
                double distanceSq = player2.getDistanceSq(x, y, z);
                if ((distance < 0.0 || distanceSq < distance * distance) && (d == -1.0 || distanceSq < d)) {
                    d = distanceSq;
                    player = player2;
                }
            }
        }

        return player;
    }

    public boolean isAnyPlayerWithinRangeAt(double x, double y, double z, double range) {
        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayer player = this.playerEntities.get(i);
            if (EntitySelectors.NOT_SPECTATING.apply(player)) {
                double distanceSq = player.getDistanceSq(x, y, z);
                if (range < 0.0 || distanceSq < range * range) {
                    return true;
                }
            }
        }

        return false;
    }

    public EntityPlayer getPlayerEntityByName(String name) {
        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayer player = this.playerEntities.get(i);
            if (name.equals(player.getCommandSenderName())) {
                return player;
            }
        }

        return null;
    }

    public EntityPlayer getPlayerEntityByUUID(UUID uuid) {
        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayer player = this.playerEntities.get(i);
            if (uuid.equals(player.getUniqueID())) {
                return player;
            }
        }

        return null;
    }

    public void sendQuittingDisconnectingPacket() {
    }

    public void checkSessionLock() throws MinecraftException {
        this.saveHandler.checkSessionLock();
    }

    public void setTotalWorldTime(long worldTime) {
        this.worldInfo.setWorldTotalTime(worldTime);
    }

    public long getSeed() {
        return this.worldInfo.getSeed();
    }

    public long getTotalWorldTime() {
        return this.worldInfo.getWorldTotalTime();
    }

    public long getWorldTime() {
        return this.worldInfo.getWorldTime();
    }

    public void setWorldTime(long time) {
        this.worldInfo.setWorldTime(time);
    }

    public BlockPos getSpawnPoint() {
        BlockPos pos = new BlockPos(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ());
        if (!this.getWorldBorder().contains(pos)) {
            pos = this.getHeight(new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
        }

        return pos;
    }

    public void setSpawnPoint(BlockPos pos) {
        this.worldInfo.setSpawn(pos);
    }

    public void joinEntityInSurroundings(Entity entityIn) {
        int n = MathHelper.floor_double(entityIn.posX / 16.0);
        int n2 = MathHelper.floor_double(entityIn.posZ / 16.0);
        int n3 = 2;

        for (int i = n - n3; i <= n + n3; i++) {
            for (int j = n2 - n3; j <= n2 + n3; j++) {
                this.getChunkFromChunkCoords(i, j);
            }
        }

        if (!this.loadedEntityList.contains(entityIn)) {
            this.loadedEntityList.add(entityIn);
        }
    }

    public boolean isBlockModifiable(EntityPlayer player, BlockPos pos) {
        return true;
    }

    public void setEntityState(Entity entityIn, byte state) {
    }

    public IChunkProvider getChunkProvider() {
        return this.chunkProvider;
    }

    public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
        blockIn.onBlockEventReceived(this, pos, this.getBlockState(pos), eventID, eventParam);
    }

    public ISaveHandler getSaveHandler() {
        return this.saveHandler;
    }

    public WorldInfo getWorldInfo() {
        return this.worldInfo;
    }

    public GameRules getGameRules() {
        return this.worldInfo.getGameRulesInstance();
    }

    public void updateAllPlayersSleepingFlag() {
    }

    public float getThunderStrength(float delta) {
        return (this.prevThunderingStrength + (this.thunderingStrength - this.prevThunderingStrength) * delta) * this.getRainStrength(delta);
    }

    public void setThunderStrength(float strength) {
        this.prevThunderingStrength = strength;
        this.thunderingStrength = strength;
    }

    public float getRainStrength(float delta) {
        return this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * delta;
    }

    public void setRainStrength(float strength) {
        this.prevRainingStrength = strength;
        this.rainingStrength = strength;
    }

    public boolean isThundering() {
        return this.getThunderStrength(1.0F) > 0.9;
    }

    public boolean isRaining() {
        return this.getRainStrength(1.0F) > 0.2;
    }

    public boolean canLightningStrike(BlockPos strikePosition) {
        if (!this.isRaining()) {
            return false;
        }
        if (!this.canSeeSky(strikePosition)) {
            return false;
        }
        if (this.getPrecipitationHeight(strikePosition).getY() > strikePosition.getY()) {
            return false;
        }
        BiomeGenBase biomeGenForCoords = this.getBiomeGenForCoords(strikePosition);
        return biomeGenForCoords.getEnableSnow() ? false : (this.canSnowAt(strikePosition, false) ? false : biomeGenForCoords.canSpawnLightningBolt());
    }

    public boolean isBlockinHighHumidity(BlockPos pos) {
        BiomeGenBase biomeGenForCoords = this.getBiomeGenForCoords(pos);
        return biomeGenForCoords.isHighHumidity();
    }

    public MapStorage getMapStorage() {
        return this.mapStorage;
    }

    public void setItemData(String dataID, WorldSavedData worldSavedDataIn) {
        this.mapStorage.setData(dataID, worldSavedDataIn);
    }

    public WorldSavedData loadItemData(Class<? extends WorldSavedData> clazz, String dataID) {
        return this.mapStorage.loadData(clazz, dataID);
    }

    public int getUniqueDataId(String key) {
        return this.mapStorage.getUniqueDataId(key);
    }

    public void playBroadcastSound(int p_175669_1_, BlockPos pos, int p_175669_3_) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).broadcastSound(p_175669_1_, pos, p_175669_3_);
        }
    }

    public void playAuxSFX(int p_175718_1_, BlockPos pos, int p_175718_3_) {
        this.playAuxSFXAtEntity((EntityPlayer)null, p_175718_1_, pos, p_175718_3_);
    }

    public void playAuxSFXAtEntity(EntityPlayer player, int sfxType, BlockPos pos, int p_180498_4_) {
        try {
            for (int i = 0; i < this.worldAccesses.size(); i++) {
                this.worldAccesses.get(i).playAuxSFX(player, sfxType, pos, p_180498_4_);
            }
        } catch (Throwable e) {
            CrashReport crashReport = CrashReport.makeCrashReport(e, "Playing level event");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Level event being played");
            crashReportCategory.addCrashSection("Block coordinates", CrashReportCategory.getCoordinateInfo(pos));
            crashReportCategory.addCrashSection("Event source", player);
            crashReportCategory.addCrashSection("Event type", sfxType);
            crashReportCategory.addCrashSection("Event data", p_180498_4_);
            throw new ReportedException(crashReport);
        }
    }

    public int getHeight() {
        return 256;
    }

    public int getActualHeight() {
        return this.provider.getHasNoSky() ? 128 : 256;
    }

    public Random setRandomSeed(int p_72843_1_, int p_72843_2_, int p_72843_3_) {
        long l = p_72843_1_ * 341873128712L + p_72843_2_ * 132897987541L + this.getWorldInfo().getSeed() + p_72843_3_;
        this.rand.setSeed(l);
        return this.rand;
    }

    public BlockPos getStrongholdPos(String name, BlockPos pos) {
        return this.getChunkProvider().getStrongholdGen(this, name, pos);
    }

    @Override
    public boolean extendedLevelsInChunkCache() {
        return false;
    }

    public double getHorizon() {
        return this.worldInfo.getTerrainType() == WorldType.FLAT ? 0.0 : 63.0;
    }

    public CrashReportCategory addWorldInfoToCrashReport(CrashReport report) {
        CrashReportCategory crashReportCategory = report.makeCategoryDepth("Affected level", 1);
        crashReportCategory.addCrashSection("Level name", this.worldInfo == null ? "????" : this.worldInfo.getWorldName());
        crashReportCategory.addCrashSectionCallable("All players", new Callable<String>() {
            public String call() {
                return World.this.playerEntities.size() + " total; " + World.this.playerEntities.toString();
            }
        });
        crashReportCategory.addCrashSectionCallable("Chunk stats", new Callable<String>() {
            public String call() {
                return World.this.chunkProvider.makeString();
            }
        });

        try {
            this.worldInfo.addToCrashReport(crashReportCategory);
        } catch (Throwable e) {
            crashReportCategory.addCrashSectionThrowable("Level Data Unobtainable", e);
        }

        return crashReportCategory;
    }

    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            IWorldAccess iWorldAccess = this.worldAccesses.get(i);
            iWorldAccess.sendBlockBreakProgress(breakerId, pos, progress);
        }
    }

    public Calendar getCurrentDate() {
        if (this.getTotalWorldTime() % 600L == 0L) {
            this.theCalendar.setTimeInMillis(MinecraftServer.getCurrentTimeMillis());
        }

        return this.theCalendar;
    }

    public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, NBTTagCompound compund) {
    }

    public Scoreboard getScoreboard() {
        return this.worldScoreboard;
    }

    public void updateComparatorOutputLevel(BlockPos pos, Block blockIn) {
        for (EnumFacing facing : Plane.HORIZONTAL) {
            BlockPos pos2 = pos.offset(facing);
            if (this.isBlockLoaded(pos2)) {
                IBlockState blockState = this.getBlockState(pos2);
                if (Blocks.unpowered_comparator.isAssociated(blockState.getBlock())) {
                    blockState.getBlock().onNeighborBlockChange(this, pos2, blockState, blockIn);
                } else if (blockState.getBlock().isNormalCube()) {
                    BlockPos pos3 = pos2.offset(facing);
                    blockState = this.getBlockState(pos3);
                    if (Blocks.unpowered_comparator.isAssociated(blockState.getBlock())) {
                        blockState.getBlock().onNeighborBlockChange(this, pos3, blockState, blockIn);
                    }
                }
            }
        }
    }

    public DifficultyInstance getDifficultyForLocation(BlockPos pos) {
        long l = 0L;
        float f = 0.0F;
        if (this.isBlockLoaded(pos)) {
            f = this.getCurrentMoonPhaseFactor();
            l = this.getChunkFromBlockCoords(pos).getInhabitedTime();
        }

        return new DifficultyInstance(this.getDifficulty(), this.getWorldTime(), l, f);
    }

    public EnumDifficulty getDifficulty() {
        return this.getWorldInfo().getDifficulty();
    }

    public int getSkylightSubtracted() {
        return this.skylightSubtracted;
    }

    public void setSkylightSubtracted(int newSkylightSubtracted) {
        this.skylightSubtracted = newSkylightSubtracted;
    }

    public int getLastLightningBolt() {
        return this.lastLightningBolt;
    }

    public void setLastLightningBolt(int lastLightningBoltIn) {
        this.lastLightningBolt = lastLightningBoltIn;
    }

    public boolean isFindingSpawnPoint() {
        return this.findingSpawnPoint;
    }

    public VillageCollection getVillageCollection() {
        return this.villageCollectionObj;
    }

    public WorldBorder getWorldBorder() {
        return this.worldBorder;
    }

    public boolean isSpawnChunk(int x, int z) {
        BlockPos spawnPoint = this.getSpawnPoint();
        int n = x * 16 + 8 - spawnPoint.getX();
        int n2 = z * 16 + 8 - spawnPoint.getZ();
        int n3 = 128;
        return n >= -n3 && n <= n3 && n2 >= -n3 && n2 <= n3;
    }

    public List<EntityPlayer> getPlayerEntities() {
        return this.playerEntities;
    }
}
