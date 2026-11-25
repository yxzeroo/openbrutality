package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VboChunkFactory;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Matrix4f;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vector3d;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.optifine.CustomColors;
import net.optifine.CustomSky;
import net.optifine.DynamicLights;
import net.optifine.Lagometer;
import net.optifine.RandomEntities;
import net.optifine.SmartAnimations;
import net.optifine.model.BlockModelUtils;
import net.optifine.reflect.Reflector;
import net.optifine.render.ChunkVisibility;
import net.optifine.render.CloudRenderer;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;
import net.optifine.shaders.ShadowUtils;
import net.optifine.util.ChunkUtils;
import net.optifine.util.RenderChunkUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brutality.module.impl.visual.Glint;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class RenderGlobal implements IResourceManagerReloadListener, IWorldAccess {
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation locationMoonPhasesPng = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation locationCloudsPng = new ResourceLocation("textures/environment/clouds.png");
    private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation locationForcefieldPng = new ResourceLocation("textures/misc/forcefield.png");
    public final Minecraft mc;
    private final TextureManager renderEngine;
    private final RenderManager renderManager;
    private WorldClient theWorld;
    private Set<RenderChunk> chunksToUpdate = Sets.newLinkedHashSet();
    private List<RenderGlobal.ContainerLocalRenderInformation> renderInfos = Lists.newArrayListWithCapacity(69696);
    private final Set<TileEntity> field_181024_n = Sets.newHashSet();
    private ViewFrustum viewFrustum;
    private int starGLCallList = -1;
    private int glSkyList = -1;
    private int glSkyList2 = -1;
    private VertexFormat vertexBufferFormat;
    private VertexBuffer starVBO;
    private VertexBuffer skyVBO;
    private VertexBuffer sky2VBO;
    private int cloudTickCounter;
    public final Map<Integer, DestroyBlockProgress> damagedBlocks = Maps.newHashMap();
    private final Map<BlockPos, ISound> mapSoundPositions = Maps.newHashMap();
    private final TextureAtlasSprite[] destroyBlockIcons = new TextureAtlasSprite[10];
    private Framebuffer entityOutlineFramebuffer;
    private ShaderGroup entityOutlineShader;
    private double frustumUpdatePosX = Double.MIN_VALUE;
    private double frustumUpdatePosY = Double.MIN_VALUE;
    private double frustumUpdatePosZ = Double.MIN_VALUE;
    private int frustumUpdatePosChunkX = Integer.MIN_VALUE;
    private int frustumUpdatePosChunkY = Integer.MIN_VALUE;
    private int frustumUpdatePosChunkZ = Integer.MIN_VALUE;
    private double lastViewEntityX = Double.MIN_VALUE;
    private double lastViewEntityY = Double.MIN_VALUE;
    private double lastViewEntityZ = Double.MIN_VALUE;
    private double lastViewEntityPitch = Double.MIN_VALUE;
    private double lastViewEntityYaw = Double.MIN_VALUE;
    private final ChunkRenderDispatcher renderDispatcher = new ChunkRenderDispatcher();
    private ChunkRenderContainer renderContainer;
    private int renderDistanceChunks = -1;
    private int renderEntitiesStartupCounter = 2;
    private int countEntitiesTotal;
    private int countEntitiesRendered;
    private int countEntitiesHidden;
    private boolean debugFixTerrainFrustum = false;
    private ClippingHelper debugFixedClippingHelper;
    private final Vector4f[] debugTerrainMatrix = new Vector4f[8];
    private final Vector3d debugTerrainFrustumPosition = new Vector3d();
    private boolean vboEnabled = false;
    IRenderChunkFactory renderChunkFactory;
    private double prevRenderSortX;
    private double prevRenderSortY;
    private double prevRenderSortZ;
    public boolean displayListEntitiesDirty = true;
    private CloudRenderer cloudRenderer;
    public Entity renderedEntity;
    public Set chunksToResortTransparency = new LinkedHashSet();
    public Set chunksToUpdateForced = new LinkedHashSet();
    private Deque visibilityDeque = new ArrayDeque();
    private List renderInfosEntities = new ArrayList(1024);
    private List renderInfosTileEntities = new ArrayList(1024);
    private List renderInfosNormal = new ArrayList(1024);
    private List renderInfosEntitiesNormal = new ArrayList(1024);
    private List renderInfosTileEntitiesNormal = new ArrayList(1024);
    private List renderInfosShadow = new ArrayList(1024);
    private List renderInfosEntitiesShadow = new ArrayList(1024);
    private List renderInfosTileEntitiesShadow = new ArrayList(1024);
    private int renderDistance = 0;
    private int renderDistanceSq = 0;
    private static final Set SET_ALL_FACINGS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(EnumFacing.VALUES)));
    private int countTileEntitiesRendered;
    private IChunkProvider worldChunkProvider = null;
    private LongHashMap worldChunkProviderMap = null;
    private int countLoadedChunksPrev = 0;
    private RenderEnv renderEnv = new RenderEnv(Blocks.air.getDefaultState(), new BlockPos(0, 0, 0));
    public boolean renderOverlayDamaged = false;
    public boolean renderOverlayEyes = false;
    private boolean firstWorldLoad = false;
    private static int renderEntitiesCounter = 0;

    public RenderGlobal(Minecraft mcIn) {
        try {
            this.cloudRenderer = new CloudRenderer(mcIn);
        } catch (Exception e) {
            System.err.println("[Brutality] Failed to initialize CloudRenderer (likely macOS Apple Silicon issue): " + e.getMessage());
            this.cloudRenderer = null;
        }

        this.mc = mcIn;
        this.renderManager = mcIn.getRenderManager();
        this.renderEngine = mcIn.getTextureManager();
        this.renderEngine.bindTexture(locationForcefieldPng);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GlStateManager.bindTexture(0);
        this.updateDestroyBlockIcons();
        this.vboEnabled = OpenGlHelper.useVbo();
        if (this.vboEnabled) {
            this.renderContainer = new VboRenderList();
            this.renderChunkFactory = new VboChunkFactory();
        } else {
            this.renderContainer = new RenderList();
            this.renderChunkFactory = new ListChunkFactory();
        }

        this.vertexBufferFormat = new VertexFormat();
        this.vertexBufferFormat.func_181721_a(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.POSITION, 3));
        this.generateStars();
        this.generateSky();
        this.generateSky2();
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.updateDestroyBlockIcons();
    }

    private void updateDestroyBlockIcons() {
        TextureMap textureMapBlocks = this.mc.getTextureMapBlocks();

        for (int i = 0; i < this.destroyBlockIcons.length; i++) {
            this.destroyBlockIcons[i] = textureMapBlocks.getAtlasSprite("minecraft:blocks/destroy_stage_" + i);
        }
    }

    public void makeEntityOutlineShader() {
        if (OpenGlHelper.shadersSupported) {
            if (ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
                ShaderLinkHelper.setNewStaticShaderLinkHelper();
            }

            ResourceLocation location = new ResourceLocation("shaders/post/entity_outline.json");

            try {
                this.entityOutlineShader = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), location);
                this.entityOutlineShader.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
                this.entityOutlineFramebuffer = this.entityOutlineShader.getFramebufferRaw("final");
            } catch (JsonSyntaxException | IOException e) {
                logger.warn("Failed to load shader: " + location, (Throwable)e);
                this.entityOutlineShader = null;
                this.entityOutlineFramebuffer = null;
            }
        } else {
            this.entityOutlineShader = null;
            this.entityOutlineFramebuffer = null;
        }
    }

    public void renderEntityOutlineFramebuffer() {
        if (this.isRenderEntityOutlines()) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
            this.entityOutlineFramebuffer.framebufferRenderExt(this.mc.displayWidth, this.mc.displayHeight, false);
            GlStateManager.disableBlend();
        }
    }

    protected boolean isRenderEntityOutlines() {
        return !Config.isFastRender()
            && !Config.isShaders()
            && !Config.isAntialiasing()
            && this.entityOutlineFramebuffer != null
            && this.entityOutlineShader != null
            && this.mc.thePlayer != null
            && (this.mc.thePlayer.isSpectator() && this.mc.gameSettings.keyBindSpectatorOutlines.isKeyDown() || Glint.getInstance().checkState44());
    }

    private void generateSky2() {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        if (this.sky2VBO != null) {
            this.sky2VBO.deleteGlBuffers();
        }

        if (this.glSkyList2 >= 0) {
            GLAllocation.deleteDisplayLists(this.glSkyList2);
            this.glSkyList2 = -1;
        }

        if (this.vboEnabled) {
            this.sky2VBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderSky(worldRenderer, -16.0F, true);
            worldRenderer.finishDrawing();
            worldRenderer.reset();
            this.sky2VBO.func_181722_a(worldRenderer.getByteBuffer());
        } else {
            this.glSkyList2 = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(this.glSkyList2, 4864);
            this.renderSky(worldRenderer, -16.0F, true);
            tessellator.draw();
            GL11.glEndList();
        }
    }

    private void generateSky() {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        if (this.skyVBO != null) {
            this.skyVBO.deleteGlBuffers();
        }

        if (this.glSkyList >= 0) {
            GLAllocation.deleteDisplayLists(this.glSkyList);
            this.glSkyList = -1;
        }

        if (this.vboEnabled) {
            this.skyVBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderSky(worldRenderer, 16.0F, false);
            worldRenderer.finishDrawing();
            worldRenderer.reset();
            this.skyVBO.func_181722_a(worldRenderer.getByteBuffer());
        } else {
            this.glSkyList = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(this.glSkyList, 4864);
            this.renderSky(worldRenderer, 16.0F, false);
            tessellator.draw();
            GL11.glEndList();
        }
    }

    private void renderSky(WorldRenderer worldRendererIn, float p_174968_2_, boolean p_174968_3_) {
        worldRendererIn.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        int n2 = (this.renderDistance / 64 + 1) * 64 + 64;

        for (int i = -n2; i <= n2; i += 64) {
            for (int j = -n2; j <= n2; j += 64) {
                float f = i;
                float f2 = i + 64;
                if (p_174968_3_) {
                    f2 = i;
                    f = i + 64;
                }

                worldRendererIn.func_181662_b(f, p_174968_2_, j).func_181675_d();
                worldRendererIn.func_181662_b(f2, p_174968_2_, j).func_181675_d();
                worldRendererIn.func_181662_b(f2, p_174968_2_, j + 64).func_181675_d();
                worldRendererIn.func_181662_b(f, p_174968_2_, j + 64).func_181675_d();
            }
        }
    }

    private void generateStars() {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        if (this.starVBO != null) {
            this.starVBO.deleteGlBuffers();
        }

        if (this.starGLCallList >= 0) {
            GLAllocation.deleteDisplayLists(this.starGLCallList);
            this.starGLCallList = -1;
        }

        if (this.vboEnabled) {
            this.starVBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderStars(worldRenderer);
            worldRenderer.finishDrawing();
            worldRenderer.reset();
            this.starVBO.func_181722_a(worldRenderer.getByteBuffer());
        } else {
            this.starGLCallList = GLAllocation.generateDisplayLists(1);
            GlStateManager.pushMatrix();
            GL11.glNewList(this.starGLCallList, 4864);
            this.renderStars(worldRenderer);
            tessellator.draw();
            GL11.glEndList();
            GlStateManager.popMatrix();
        }
    }

    private void renderStars(WorldRenderer worldRendererIn) {
        Random random = new Random(10842L);
        worldRendererIn.func_181668_a(7, DefaultVertexFormats.field_181705_e);

        for (int i = 0; i < 1500; i++) {
            double d = random.nextFloat() * 2.0F - 1.0F;
            double d2 = random.nextFloat() * 2.0F - 1.0F;
            double d3 = random.nextFloat() * 2.0F - 1.0F;
            double d4 = 0.15F + random.nextFloat() * 0.1F;
            double d5 = d * d + d2 * d2 + d3 * d3;
            if (d5 < 1.0 && d5 > 0.01) {
                d5 = 1.0 / Math.sqrt(d5);
                d *= d5;
                d2 *= d5;
                d3 *= d5;
                double d6 = d * 100.0;
                double d7 = d2 * 100.0;
                double d8 = d3 * 100.0;
                double angle = Math.atan2(d, d3);
                double sin = Math.sin(angle);
                double cos = Math.cos(angle);
                double angle2 = Math.atan2(Math.sqrt(d * d + d3 * d3), d2);
                double sin2 = Math.sin(angle2);
                double cos2 = Math.cos(angle2);
                double d9 = random.nextDouble() * Math.PI * 2.0;
                double sin3 = Math.sin(d9);
                double cos3 = Math.cos(d9);

                for (int j = 0; j < 4; j++) {
                    double d11 = ((j & 2) - 1) * d4;
                    double d12 = ((j + 1 & 2) - 1) * d4;
                    double d14 = d11 * cos3 - d12 * sin3;
                    double d15 = d12 * cos3 + d11 * sin3;
                    double d16 = d14 * sin2 + 0.0 * cos2;
                    double d17 = 0.0 * sin2 - d14 * cos2;
                    double d18 = d17 * sin - d15 * cos;
                    double d19 = d15 * sin + d17 * cos;
                    worldRendererIn.func_181662_b(d6 + d18, d7 + d16, d8 + d19).func_181675_d();
                }
            }
        }
    }

    public void setWorldAndLoadRenderers(WorldClient worldClientIn) {
        if (this.theWorld != null) {
            this.theWorld.removeWorldAccess(this);
        }

        this.frustumUpdatePosX = Double.MIN_VALUE;
        this.frustumUpdatePosY = Double.MIN_VALUE;
        this.frustumUpdatePosZ = Double.MIN_VALUE;
        this.frustumUpdatePosChunkX = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkY = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkZ = Integer.MIN_VALUE;
        this.renderManager.set(worldClientIn);
        this.theWorld = worldClientIn;
        if (Config.isDynamicLights()) {
            DynamicLights.clear();
        }

        ChunkVisibility.reset();
        this.worldChunkProvider = null;
        this.worldChunkProviderMap = null;
        this.renderEnv.reset((IBlockState)null, (BlockPos)null);
        Shaders.checkWorldChanged(this.theWorld);
        if (worldClientIn != null) {
            worldClientIn.addWorldAccess(this);
            this.loadRenderers();
        } else {
            this.chunksToUpdate.clear();
            this.clearRenderInfos();
            if (this.viewFrustum != null) {
                this.viewFrustum.deleteGlResources();
            }

            this.viewFrustum = null;
        }
    }

    public void loadRenderers() {
        if (this.theWorld != null) {
            this.displayListEntitiesDirty = true;
            Blocks.leaves.setGraphicsLevel(Config.isTreesFancy());
            Blocks.leaves2.setGraphicsLevel(Config.isTreesFancy());
            BlockModelRenderer.updateAoLightValue();
            if (Config.isDynamicLights()) {
                DynamicLights.clear();
            }

            SmartAnimations.update();
            this.renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;
            this.renderDistance = this.renderDistanceChunks * 16;
            this.renderDistanceSq = this.renderDistance * this.renderDistance;
            boolean vboEnabled = this.vboEnabled;
            this.vboEnabled = OpenGlHelper.useVbo();
            if (vboEnabled && !this.vboEnabled) {
                this.renderContainer = new RenderList();
                this.renderChunkFactory = new ListChunkFactory();
            } else if (!vboEnabled && this.vboEnabled) {
                this.renderContainer = new VboRenderList();
                this.renderChunkFactory = new VboChunkFactory();
            }

            this.generateStars();
            this.generateSky();
            this.generateSky2();
            if (this.viewFrustum != null) {
                this.viewFrustum.deleteGlResources();
            }

            this.stopChunkUpdates();
            synchronized (this.field_181024_n) {
                this.field_181024_n.clear();
            }

            this.viewFrustum = new ViewFrustum(this.theWorld, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory);
            if (this.theWorld != null) {
                Entity renderViewEntity = this.mc.getRenderViewEntity();
                if (renderViewEntity != null) {
                    this.viewFrustum.updateChunkPositions(renderViewEntity.posX, renderViewEntity.posZ);
                }
            }

            this.renderEntitiesStartupCounter = 2;
        }

        if (this.mc.thePlayer == null) {
            this.firstWorldLoad = true;
        }
    }

    protected void stopChunkUpdates() {
        this.chunksToUpdate.clear();
        this.renderDispatcher.stopChunkUpdates();
    }

    public void createBindEntityOutlineFbs(int p_72720_1_, int p_72720_2_) {
        if (OpenGlHelper.shadersSupported && this.entityOutlineShader != null) {
            this.entityOutlineShader.createBindFramebuffers(p_72720_1_, p_72720_2_);
        }
    }

    public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks) {
        int n = 0;
        if (Reflector.MinecraftForgeClient_getRenderPass.exists()) {
            n = Reflector.callInt(Reflector.MinecraftForgeClient_getRenderPass);
        }

        if (this.renderEntitiesStartupCounter > 0) {
            if (n > 0) {
                return;
            }

            this.renderEntitiesStartupCounter--;
        } else {
            double d = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * partialTicks;
            double d2 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * partialTicks;
            double d3 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * partialTicks;
            this.theWorld.theProfiler.startSection("prepare");
            TileEntityRendererDispatcher.instance
                .cacheActiveRenderInfo(this.theWorld, this.mc.getTextureManager(), this.mc.fontRendererObj, this.mc.getRenderViewEntity(), partialTicks);
            this.renderManager
                .cacheActiveRenderInfo(this.theWorld, this.mc.fontRendererObj, this.mc.getRenderViewEntity(), this.mc.pointedEntity, this.mc.gameSettings, partialTicks);
            renderEntitiesCounter++;
            if (n == 0) {
                this.countEntitiesTotal = 0;
                this.countEntitiesRendered = 0;
                this.countEntitiesHidden = 0;
                this.countTileEntitiesRendered = 0;
            }

            Entity renderViewEntity2 = this.mc.getRenderViewEntity();
            double d4 = renderViewEntity2.lastTickPosX + (renderViewEntity2.posX - renderViewEntity2.lastTickPosX) * partialTicks;
            double d5 = renderViewEntity2.lastTickPosY + (renderViewEntity2.posY - renderViewEntity2.lastTickPosY) * partialTicks;
            double d6 = renderViewEntity2.lastTickPosZ + (renderViewEntity2.posZ - renderViewEntity2.lastTickPosZ) * partialTicks;
            TileEntityRendererDispatcher.staticPlayerX = d4;
            TileEntityRendererDispatcher.staticPlayerY = d5;
            TileEntityRendererDispatcher.staticPlayerZ = d6;
            this.renderManager.setRenderPosition(d4, d5, d6);
            this.mc.entityRenderer.enableLightmap();
            this.theWorld.theProfiler.endStartSection("global");
            var entities = this.theWorld.getLoadedEntityList();
            if (n == 0) {
                this.countEntitiesTotal = entities.size();
            }

            if (Config.isFogOff() && this.mc.entityRenderer.fogStandard) {
                GlStateManager.disableFog();
            }

            boolean flag = Reflector.ForgeEntity_shouldRenderInPass.exists();
            boolean flag2 = Reflector.ForgeTileEntity_shouldRenderInPass.exists();

            for (int i = 0; i < this.theWorld.weatherEffects.size(); i++) {
                Entity entity = this.theWorld.weatherEffects.get(i);
                if (!flag || Reflector.callBoolean(entity, Reflector.ForgeEntity_shouldRenderInPass, n)) {
                    this.countEntitiesRendered++;
                    if (entity.isInRangeToRender3d(d, d2, d3)) {
                        this.renderManager.renderEntitySimple(entity, partialTicks);
                    }
                }
            }

            if (this.isRenderEntityOutlines()) {
                GlStateManager.depthFunc(GL11.GL_ALWAYS);
                GlStateManager.disableFog();
                this.entityOutlineFramebuffer.framebufferClear();
                this.entityOutlineFramebuffer.bindFramebuffer(false);
                this.theWorld.theProfiler.endStartSection("entityOutlines");
                RenderHelper.disableStandardItemLighting();
                this.renderManager.setRenderOutlines(true);

                for (int j = 0; j < entities.size(); j++) {
                    Entity entity2 = (Entity)entities.get(j);
                    boolean flag3 = this.mc.getRenderViewEntity() instanceof EntityLivingBase
                        && ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping();
                    boolean flag4 = entity2.isInRangeToRender3d(d, d2, d3)
                        && (entity2.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(entity2.getEntityBoundingBox()) || entity2.riddenByEntity == this.mc.thePlayer)
                        && entity2 instanceof EntityPlayer;
                    if ((entity2 != this.mc.getRenderViewEntity() || this.mc.gameSettings.thirdPersonView != 0 || flag3) && flag4) {
                        this.renderManager.renderEntitySimple(entity2, partialTicks);
                    }
                }

                this.renderManager.setRenderOutlines(false);
                RenderHelper.enableStandardItemLighting();
                GlStateManager.depthMask(false);
                this.entityOutlineShader.loadShaderGroup(partialTicks);
                GlStateManager.enableLighting();
                GlStateManager.depthMask(true);
                this.mc.getFramebuffer().bindFramebuffer(false);
                GlStateManager.enableFog();
                GlStateManager.enableBlend();
                GlStateManager.enableColorMaterial();
                GlStateManager.depthFunc(GL11.GL_LEQUAL);
                GlStateManager.enableDepth();
                GlStateManager.enableAlpha();
            }

            this.theWorld.theProfiler.endStartSection("entities");
            boolean shaders = Config.isShaders();
            if (shaders) {
                Shaders.beginEntities();
            }

            RenderItemFrame.updateItemRenderDistance();
            boolean fancyGraphics = this.mc.gameSettings.fancyGraphics;
            this.mc.gameSettings.fancyGraphics = Config.isDroppedItemsFancy();

            for (Object renderInfosEntity : this.renderInfosEntities) {
                RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation = (RenderGlobal.ContainerLocalRenderInformation)renderInfosEntity;
                Chunk chunk = containerLocalRenderInformation.renderChunk.getChunk();
                ClassInheritanceMultiMap classInheritanceMultiMap = chunk.getEntityLists()[containerLocalRenderInformation.renderChunk.getPosition().getY() / 16];
                if (!classInheritanceMultiMap.isEmpty()) {
                    for (Entity entity3 : ((Iterable<Entity>)(Object)(classInheritanceMultiMap))) {
                        if (!flag || Reflector.callBoolean(entity3, Reflector.ForgeEntity_shouldRenderInPass, n)) {
                            boolean flag5 = this.renderManager.shouldRender(entity3, camera, d, d2, d3) || entity3.riddenByEntity == this.mc.thePlayer;
                            if (flag5) {
                                boolean flag6 = this.mc.getRenderViewEntity() instanceof EntityLivingBase
                                    ? ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping()
                                    : false;
                                if (entity3 == this.mc.getRenderViewEntity() && this.mc.gameSettings.thirdPersonView == 0 && !flag6
                                    || !(entity3.posY < 0.0) && !(entity3.posY >= 256.0) && !this.theWorld.isBlockLoaded(new BlockPos(entity3))) {
                                    continue;
                                }

                                this.countEntitiesRendered++;
                                this.renderedEntity = entity3;
                                if (shaders) {
                                    Shaders.nextEntity(entity3);
                                }

                                this.renderManager.renderEntitySimple(entity3, partialTicks);
                                this.renderedEntity = null;
                            }

                            if (!flag5
                                && entity3 instanceof EntityWitherSkull
                                && (!flag || Reflector.callBoolean(entity3, Reflector.ForgeEntity_shouldRenderInPass, n))) {
                                this.renderedEntity = entity3;
                                if (shaders) {
                                    Shaders.nextEntity(entity3);
                                }

                                this.mc.getRenderManager().renderWitherSkull(entity3, partialTicks);
                                this.renderedEntity = null;
                            }
                        }
                    }
                }
            }

            this.mc.gameSettings.fancyGraphics = fancyGraphics;
            if (shaders) {
                Shaders.endEntities();
                Shaders.beginBlockEntities();
            }

            this.theWorld.theProfiler.endStartSection("blockentities");
            RenderHelper.enableStandardItemLighting();
            if (Reflector.ForgeTileEntity_hasFastRenderer.exists()) {
                TileEntityRendererDispatcher.instance.preDrawBatch();
            }

            TileEntitySignRenderer.updateTextRenderDistance();

            for (Object renderInfosTileEntity : this.renderInfosTileEntities) {
                RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation2 = (RenderGlobal.ContainerLocalRenderInformation)renderInfosTileEntity;
                var tileEntities = containerLocalRenderInformation2.renderChunk.getCompiledChunk().getTileEntities();
                if (!tileEntities.isEmpty()) {
                    for (TileEntity tileEntity : tileEntities) {
                        if (flag2) {
                            if (!Reflector.callBoolean(tileEntity, Reflector.ForgeTileEntity_shouldRenderInPass, n)) {
                                continue;
                            }

                            AxisAlignedBB box = (AxisAlignedBB)Reflector.call(tileEntity, Reflector.ForgeTileEntity_getRenderBoundingBox);
                            if (box != null && !camera.isBoundingBoxInFrustum(box)) {
                                continue;
                            }
                        }

                        if (shaders) {
                            Shaders.nextBlockEntity(tileEntity);
                        }

                        TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, partialTicks, -1);
                        this.countTileEntitiesRendered++;
                    }
                }
            }

            synchronized (this.field_181024_n) {
                for (TileEntity tileEntity2 : this.field_181024_n) {
                    if (!flag2 || Reflector.callBoolean(tileEntity2, Reflector.ForgeTileEntity_shouldRenderInPass, n)) {
                        if (shaders) {
                            Shaders.nextBlockEntity(tileEntity2);
                        }

                        TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity2, partialTicks, -1);
                    }
                }
            }

            if (Reflector.ForgeTileEntity_hasFastRenderer.exists()) {
                TileEntityRendererDispatcher.instance.drawBatch(n);
            }

            this.renderOverlayDamaged = true;
            this.preRenderDamagedBlocks();

            for (DestroyBlockProgress destroyBlockProgress : this.damagedBlocks.values()) {
                BlockPos pos = destroyBlockProgress.getPosition();
                TileEntity tileEntity3 = this.theWorld.getTileEntity(pos);
                if (tileEntity3 instanceof TileEntityChest tileEntityChest) {
                    if (tileEntityChest.adjacentChestXNeg != null) {
                        pos = pos.offset(EnumFacing.WEST);
                        tileEntity3 = this.theWorld.getTileEntity(pos);
                    } else if (tileEntityChest.adjacentChestZNeg != null) {
                        pos = pos.offset(EnumFacing.NORTH);
                        tileEntity3 = this.theWorld.getTileEntity(pos);
                    }
                }

                Block block = this.theWorld.getBlockState(pos).getBlock();
                boolean flag7;
                if (flag2) {
                    flag7 = false;
                    if (tileEntity3 != null
                        && Reflector.callBoolean(tileEntity3, Reflector.ForgeTileEntity_shouldRenderInPass, n)
                        && Reflector.callBoolean(tileEntity3, Reflector.ForgeTileEntity_canRenderBreaking)) {
                        AxisAlignedBB box2 = (AxisAlignedBB)Reflector.call(tileEntity3, Reflector.ForgeTileEntity_getRenderBoundingBox);
                        if (box2 != null) {
                            flag7 = camera.isBoundingBoxInFrustum(box2);
                        }
                    }
                } else {
                    flag7 = tileEntity3 != null
                        && (block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockSign || block instanceof BlockSkull);
                }

                if (flag7) {
                    if (shaders) {
                        Shaders.nextBlockEntity(tileEntity3);
                    }

                    TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity3, partialTicks, destroyBlockProgress.getPartialBlockDamage());
                }
            }

            this.postRenderDamagedBlocks();
            this.renderOverlayDamaged = false;
            if (shaders) {
                Shaders.endBlockEntities();
            }

            renderEntitiesCounter--;
            this.mc.entityRenderer.disableLightmap();
            this.mc.mcProfiler.endSection();
        }
    }

    public String getDebugInfoRenders() {
        int len = this.viewFrustum.renderChunks.length;
        int n = 0;

        for (RenderGlobal.ContainerLocalRenderInformation renderInfo : this.renderInfos) {
            CompiledChunk compiledChunk = renderInfo.renderChunk.compiledChunk;
            if (compiledChunk != CompiledChunk.DUMMY && !compiledChunk.isEmpty()) {
                n++;
            }
        }

        return String.format(
            "C: %d/%d %sD: %d, %s", n, len, this.mc.renderChunksMany ? "(s) " : "", this.renderDistanceChunks, this.renderDispatcher.getDebugInfo()
        );
    }

    public String getDebugInfoEntities() {
        return "E: "
            + this.countEntitiesRendered
            + "/"
            + this.countEntitiesTotal
            + ", B: "
            + this.countEntitiesHidden
            + ", I: "
            + (this.countEntitiesTotal - this.countEntitiesHidden - this.countEntitiesRendered)
            + ", "
            + Config.getVersionDebug();
    }

    public void setupTerrain(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator) {
        if (this.mc.gameSettings.renderDistanceChunks != this.renderDistanceChunks) {
            this.loadRenderers();
        }

        this.theWorld.theProfiler.startSection("camera");
        double d = viewEntity.posX - this.frustumUpdatePosX;
        double d2 = viewEntity.posY - this.frustumUpdatePosY;
        double d3 = viewEntity.posZ - this.frustumUpdatePosZ;
        if (this.frustumUpdatePosChunkX != viewEntity.chunkCoordX
            || this.frustumUpdatePosChunkY != viewEntity.chunkCoordY
            || this.frustumUpdatePosChunkZ != viewEntity.chunkCoordZ
            || d * d + d2 * d2 + d3 * d3 > 16.0) {
            this.frustumUpdatePosX = viewEntity.posX;
            this.frustumUpdatePosY = viewEntity.posY;
            this.frustumUpdatePosZ = viewEntity.posZ;
            this.frustumUpdatePosChunkX = viewEntity.chunkCoordX;
            this.frustumUpdatePosChunkY = viewEntity.chunkCoordY;
            this.frustumUpdatePosChunkZ = viewEntity.chunkCoordZ;
            this.viewFrustum.updateChunkPositions(viewEntity.posX, viewEntity.posZ);
        }

        if (Config.isDynamicLights()) {
            DynamicLights.update(this);
        }

        this.theWorld.theProfiler.endStartSection("renderlistcamera");
        double d4 = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
        double d5 = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
        double d6 = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;
        this.renderContainer.initialize(d4, d5, d6);
        this.theWorld.theProfiler.endStartSection("cull");
        if (this.debugFixedClippingHelper != null) {
            Frustum frustum = new Frustum(this.debugFixedClippingHelper);
            frustum.setPosition(
                this.debugTerrainFrustumPosition.field_181059_a,
                this.debugTerrainFrustumPosition.field_181060_b,
                this.debugTerrainFrustumPosition.field_181061_c
            );
            camera = frustum;
        }

        this.mc.mcProfiler.endStartSection("culling");
        BlockPos pos = new BlockPos(d4, d5 + viewEntity.getEyeHeight(), d6);
        RenderChunk renderChunk = this.viewFrustum.getRenderChunk(pos);
        new BlockPos(MathHelper.floor_double(d4 / 16.0) * 16, MathHelper.floor_double(d5 / 16.0) * 16, MathHelper.floor_double(d6 / 16.0) * 16);
        this.displayListEntitiesDirty = this.displayListEntitiesDirty
            || !this.chunksToUpdate.isEmpty()
            || viewEntity.posX != this.lastViewEntityX
            || viewEntity.posY != this.lastViewEntityY
            || viewEntity.posZ != this.lastViewEntityZ
            || viewEntity.rotationPitch != this.lastViewEntityPitch
            || viewEntity.rotationYaw != this.lastViewEntityYaw;
        this.lastViewEntityX = viewEntity.posX;
        this.lastViewEntityY = viewEntity.posY;
        this.lastViewEntityZ = viewEntity.posZ;
        this.lastViewEntityPitch = viewEntity.rotationPitch;
        this.lastViewEntityYaw = viewEntity.rotationYaw;
        boolean flag = this.debugFixedClippingHelper != null;
        this.mc.mcProfiler.endStartSection("update");
        Lagometer.timerVisibility.start();
        int countLoadedChunks = this.getCountLoadedChunks();
        if (countLoadedChunks != this.countLoadedChunksPrev) {
            this.countLoadedChunksPrev = countLoadedChunks;
            this.displayListEntitiesDirty = true;
        }

        int n = 256;
        if (!ChunkVisibility.isFinished()) {
            this.displayListEntitiesDirty = true;
        }

        if (!flag && this.displayListEntitiesDirty && Config.isIntegratedServerRunning()) {
            n = ChunkVisibility.getMaxChunkY(this.theWorld, viewEntity, this.renderDistanceChunks);
        }

        RenderChunk renderChunk2 = this.viewFrustum.getRenderChunk(new BlockPos(viewEntity.posX, viewEntity.posY, viewEntity.posZ));
        if (Shaders.isShadowPass) {
            this.renderInfos = this.renderInfosShadow;
            this.renderInfosEntities = this.renderInfosEntitiesShadow;
            this.renderInfosTileEntities = this.renderInfosTileEntitiesShadow;
            if (!flag && this.displayListEntitiesDirty) {
                this.clearRenderInfos();
                if (renderChunk2 != null && renderChunk2.getPosition().getY() > n) {
                    this.renderInfosEntities.add(renderChunk2.getRenderInfo());
                }

                var it = ShadowUtils.makeShadowChunkIterator(this.theWorld, partialTicks, viewEntity, this.renderDistanceChunks, this.viewFrustum);

                while (it.hasNext()) {
                    RenderChunk renderChunk3 = (RenderChunk)it.next();
                    if (renderChunk3 != null && renderChunk3.getPosition().getY() <= n) {
                        RenderGlobal.ContainerLocalRenderInformation renderInfo = renderChunk3.getRenderInfo();
                        if (!renderChunk3.compiledChunk.isEmpty() || renderChunk3.isNeedsUpdate()) {
                            this.renderInfos.add(renderInfo);
                        }

                        if (ChunkUtils.hasEntities(renderChunk3.getChunk())) {
                            this.renderInfosEntities.add(renderInfo);
                        }

                        if (renderChunk3.getCompiledChunk().getTileEntities().size() > 0) {
                            this.renderInfosTileEntities.add(renderInfo);
                        }
                    }
                }
            }
        } else {
            this.renderInfos = this.renderInfosNormal;
            this.renderInfosEntities = this.renderInfosEntitiesNormal;
            this.renderInfosTileEntities = this.renderInfosTileEntitiesNormal;
        }

        if (!flag && this.displayListEntitiesDirty && !Shaders.isShadowPass) {
            this.displayListEntitiesDirty = false;
            this.clearRenderInfos();
            this.visibilityDeque.clear();
            Deque visibilityDeque = this.visibilityDeque;
            boolean renderChunksMany = this.mc.renderChunksMany;
            if (renderChunk != null && renderChunk.getPosition().getY() <= n) {
                boolean flag2 = false;
                RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation = new RenderGlobal.ContainerLocalRenderInformation(renderChunk, (EnumFacing)null, 0);
                var set = SET_ALL_FACINGS;
                if (set.size() == 1) {
                    Vector3f viewVector = this.getViewVector(viewEntity, partialTicks);
                    EnumFacing opposite = EnumFacing.getFacingFromVector(viewVector.x, viewVector.y, viewVector.z).getOpposite();
                    set.remove(opposite);
                }

                if (set.isEmpty()) {
                    flag2 = true;
                }

                if (flag2 && !playerSpectator) {
                    this.renderInfos.add(containerLocalRenderInformation);
                } else {
                    if (playerSpectator && this.theWorld.getBlockState(pos).getBlock().isOpaqueCube()) {
                        renderChunksMany = false;
                    }

                    renderChunk.setFrameIndex(frameCount);
                    visibilityDeque.add(containerLocalRenderInformation);
                }
            } else {
                int n2 = pos.getY() > 0 ? Math.min(n, 248) : 8;
                if (renderChunk2 != null) {
                    this.renderInfosEntities.add(renderChunk2.getRenderInfo());
                }

                for (int i = -this.renderDistanceChunks; i <= this.renderDistanceChunks; i++) {
                    for (int j = -this.renderDistanceChunks; j <= this.renderDistanceChunks; j++) {
                        RenderChunk renderChunk4 = this.viewFrustum.getRenderChunk(new BlockPos((i << 4) + 8, n2, (j << 4) + 8));
                        if (renderChunk4 != null && renderChunk4.isBoundingBoxInFrustum((ICamera)camera, frameCount)) {
                            renderChunk4.setFrameIndex(frameCount);
                            RenderGlobal.ContainerLocalRenderInformation renderInfo2 = renderChunk4.getRenderInfo();
                            renderInfo2.initialize((EnumFacing)null, 0);
                            visibilityDeque.add(renderInfo2);
                        }
                    }
                }
            }

            this.mc.mcProfiler.startSection("iteration");
            boolean fogOn = Config.isFogOn();

            while (!visibilityDeque.isEmpty()) {
                RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation2 = (RenderGlobal.ContainerLocalRenderInformation)visibilityDeque.poll();
                RenderChunk renderChunk5 = containerLocalRenderInformation2.renderChunk;
                EnumFacing facing = containerLocalRenderInformation2.facing;
                CompiledChunk compiledChunk = renderChunk5.compiledChunk;
                if (!compiledChunk.isEmpty() || renderChunk5.isNeedsUpdate()) {
                    this.renderInfos.add(containerLocalRenderInformation2);
                }

                if (ChunkUtils.hasEntities(renderChunk5.getChunk())) {
                    this.renderInfosEntities.add(containerLocalRenderInformation2);
                }

                if (compiledChunk.getTileEntities().size() > 0) {
                    this.renderInfosTileEntities.add(containerLocalRenderInformation2);
                }

                for (EnumFacing facing2 : renderChunksMany ? ChunkVisibility.getFacingsNotOpposite(containerLocalRenderInformation2.setFacing) : EnumFacing.VALUES) {
                    if (!renderChunksMany || facing == null || compiledChunk.isVisible(facing.getOpposite(), facing2)) {
                        RenderChunk renderChunkOffset = this.getRenderChunkOffset(pos, renderChunk5, facing2, fogOn, n);
                        if (renderChunkOffset != null && renderChunkOffset.setFrameIndex(frameCount) && renderChunkOffset.isBoundingBoxInFrustum((ICamera)camera, frameCount)) {
                            int n3 = containerLocalRenderInformation2.setFacing | 1 << facing2.ordinal();
                            RenderGlobal.ContainerLocalRenderInformation renderInfo3 = renderChunkOffset.getRenderInfo();
                            renderInfo3.initialize(facing2, n3);
                            visibilityDeque.add(renderInfo3);
                        }
                    }
                }
            }

            this.mc.mcProfiler.endSection();
        }

        this.mc.mcProfiler.endStartSection("captureFrustum");
        if (this.debugFixTerrainFrustum) {
            this.fixTerrainFrustum(d4, d5, d6);
            this.debugFixTerrainFrustum = false;
        }

        Lagometer.timerVisibility.end();
        if (Shaders.isShadowPass) {
            Shaders.mcProfilerEndSection();
        } else {
            this.mc.mcProfiler.endStartSection("rebuildNear");
            this.renderDispatcher.clearChunkUpdates();
            var chunksToUpdate = this.chunksToUpdate;
            this.chunksToUpdate = Sets.newLinkedHashSet();
            Lagometer.timerChunkUpdate.start();

            for (RenderGlobal.ContainerLocalRenderInformation renderInfo4 : this.renderInfos) {
                RenderChunk renderChunk6 = renderInfo4.renderChunk;
                if (renderChunk6.isNeedsUpdate() || chunksToUpdate.contains(renderChunk6)) {
                    this.displayListEntitiesDirty = true;
                    BlockPos pos2 = renderChunk6.getPosition();
                    boolean flag3 = pos.distanceSq(pos2.getX() + 8, pos2.getY() + 8, pos2.getZ() + 8) < 768.0;
                    if (!flag3) {
                        this.chunksToUpdate.add(renderChunk6);
                    } else if (!renderChunk6.isPlayerUpdate()) {
                        this.chunksToUpdateForced.add(renderChunk6);
                    } else {
                        this.mc.mcProfiler.startSection("build near");
                        this.renderDispatcher.updateChunkNow(renderChunk6);
                        renderChunk6.setNeedsUpdate(false);
                        this.mc.mcProfiler.endSection();
                    }
                }
            }

            Lagometer.timerChunkUpdate.end();
            this.chunksToUpdate.addAll(chunksToUpdate);
            this.mc.mcProfiler.endSection();
        }
    }

    private boolean isPositionInRenderChunk(BlockPos pos, RenderChunk renderChunkIn) {
        BlockPos pos2 = renderChunkIn.getPosition();
        return MathHelper.abs_int(pos.getX() - pos2.getX()) > 16
            ? false
            : (MathHelper.abs_int(pos.getY() - pos2.getY()) > 16 ? false : MathHelper.abs_int(pos.getZ() - pos2.getZ()) <= 16);
    }

    private Set<EnumFacing> getVisibleFacings(BlockPos pos) {
        VisGraph visGraph = new VisGraph();
        BlockPos pos2 = new BlockPos(pos.getX() >> 4 << 4, pos.getY() >> 4 << 4, pos.getZ() >> 4 << 4);
        Chunk chunkFromBlockCoords = this.theWorld.getChunkFromBlockCoords(pos2);

        for (BlockPos.MutableBlockPos mutablePos : BlockPos.getAllInBoxMutable(pos2, pos2.add(15, 15, 15))) {
            if (chunkFromBlockCoords.getBlock(mutablePos).isOpaqueCube()) {
                visGraph.func_178606_a(mutablePos);
            }
        }

        return visGraph.func_178609_b(pos);
    }

    private RenderChunk getRenderChunkOffset(BlockPos p_getRenderChunkOffset_1_, RenderChunk p_getRenderChunkOffset_2_, EnumFacing p_getRenderChunkOffset_3_, boolean p_getRenderChunkOffset_4_, int p_getRenderChunkOffset_5_) {
        RenderChunk renderChunkNeighbour = p_getRenderChunkOffset_2_.getRenderChunkNeighbour(p_getRenderChunkOffset_3_);
        if (renderChunkNeighbour == null) {
            return null;
        }
        if (renderChunkNeighbour.getPosition().getY() > p_getRenderChunkOffset_5_) {
            return null;
        }
        if (p_getRenderChunkOffset_4_) {
            BlockPos pos = renderChunkNeighbour.getPosition();
            int n = p_getRenderChunkOffset_1_.getX() - pos.getX();
            int n2 = p_getRenderChunkOffset_1_.getZ() - pos.getZ();
            int n3 = n * n + n2 * n2;
            if (n3 > this.renderDistanceSq) {
                return null;
            }
        }

        return renderChunkNeighbour;
    }

    private void fixTerrainFrustum(double x, double y, double z) {
        this.debugFixedClippingHelper = new ClippingHelperImpl();
        ((ClippingHelperImpl)this.debugFixedClippingHelper).init();
        Matrix4f matrix4f = new Matrix4f(this.debugFixedClippingHelper.modelviewMatrix);
        matrix4f.transpose();
        Matrix4f matrix4f2 = new Matrix4f(this.debugFixedClippingHelper.projectionMatrix);
        matrix4f2.transpose();
        Matrix4f matrix4f3 = new Matrix4f();
        Matrix4f.mul(matrix4f2, matrix4f, matrix4f3);
        matrix4f3.invert();
        this.debugTerrainFrustumPosition.field_181059_a = x;
        this.debugTerrainFrustumPosition.field_181060_b = y;
        this.debugTerrainFrustumPosition.field_181061_c = z;
        this.debugTerrainMatrix[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
        this.debugTerrainMatrix[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
        this.debugTerrainMatrix[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.debugTerrainMatrix[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);

        for (int i = 0; i < 8; i++) {
            Matrix4f.transform(matrix4f3, this.debugTerrainMatrix[i], this.debugTerrainMatrix[i]);
            this.debugTerrainMatrix[i].x = this.debugTerrainMatrix[i].x / this.debugTerrainMatrix[i].w;
            this.debugTerrainMatrix[i].y = this.debugTerrainMatrix[i].y / this.debugTerrainMatrix[i].w;
            this.debugTerrainMatrix[i].z = this.debugTerrainMatrix[i].z / this.debugTerrainMatrix[i].w;
            this.debugTerrainMatrix[i].w = 1.0F;
        }
    }

    protected Vector3f getViewVector(Entity entityIn, double partialTicks) {
        float f = (float)(entityIn.prevRotationPitch + (entityIn.rotationPitch - entityIn.prevRotationPitch) * partialTicks);
        float f2 = (float)(entityIn.prevRotationYaw + (entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks);
        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) {
            f += 180.0F;
        }

        float cos = MathHelper.cos(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
        float sin = MathHelper.sin(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
        float f3 = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
        float sin2 = MathHelper.sin(-f * (float) (Math.PI / 180.0));
        return new Vector3f(sin * f3, sin2, cos * f3);
    }

    public int renderBlockLayer(EnumWorldBlockLayer blockLayerIn, double partialTicks, int pass, Entity entityIn) {
        RenderHelper.disableStandardItemLighting();
        if (blockLayerIn == EnumWorldBlockLayer.TRANSLUCENT && !Shaders.isShadowPass) {
            this.mc.mcProfiler.startSection("translucent_sort");
            double d = entityIn.posX - this.prevRenderSortX;
            double d2 = entityIn.posY - this.prevRenderSortY;
            double d3 = entityIn.posZ - this.prevRenderSortZ;
            if (d * d + d2 * d2 + d3 * d3 > 1.0) {
                this.prevRenderSortX = entityIn.posX;
                this.prevRenderSortY = entityIn.posY;
                this.prevRenderSortZ = entityIn.posZ;
                int n = 0;
                this.chunksToResortTransparency.clear();

                for (RenderGlobal.ContainerLocalRenderInformation renderInfo : this.renderInfos) {
                    if (renderInfo.renderChunk.compiledChunk.isLayerStarted(blockLayerIn) && n++ < 15) {
                        this.chunksToResortTransparency.add(renderInfo.renderChunk);
                    }
                }
            }

            this.mc.mcProfiler.endSection();
        }

        this.mc.mcProfiler.startSection("filterempty");
        int n2 = 0;
        boolean flag = blockLayerIn == EnumWorldBlockLayer.TRANSLUCENT;
        int n3 = flag ? this.renderInfos.size() - 1 : 0;
        int n4 = flag ? -1 : this.renderInfos.size();
        int n5 = flag ? -1 : 1;

        for (int i = n3; i != n4; i += n5) {
            RenderChunk renderChunk = this.renderInfos.get(i).renderChunk;
            if (!renderChunk.getCompiledChunk().isLayerEmpty(blockLayerIn)) {
                n2++;
                this.renderContainer.addRenderChunk(renderChunk, blockLayerIn);
            }
        }

        if (n2 == 0) {
            this.mc.mcProfiler.endSection();
            return n2;
        }
        if (Config.isFogOff() && this.mc.entityRenderer.fogStandard) {
            GlStateManager.disableFog();
        }

        this.mc.mcProfiler.endStartSection("render_" + blockLayerIn);
        this.renderBlockLayer(blockLayerIn);
        this.mc.mcProfiler.endSection();
        return n2;
    }

    private void renderBlockLayer(EnumWorldBlockLayer blockLayerIn) {
        this.mc.entityRenderer.enableLightmap();
        if (OpenGlHelper.useVbo()) {
            GL11.glEnableClientState(32884);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glEnableClientState(32888);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glEnableClientState(32888);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glEnableClientState(32886);
        }

        if (Config.isShaders()) {
            ShadersRender.preRenderChunkLayer(blockLayerIn);
        }

        this.renderContainer.renderChunkLayer(blockLayerIn);
        if (Config.isShaders()) {
            ShadersRender.postRenderChunkLayer(blockLayerIn);
        }

        if (OpenGlHelper.useVbo()) {
            for (VertexFormatElement element : DefaultVertexFormats.BLOCK.getElements()) {
                EnumUsage usage = element.getUsage();
                int index = element.getIndex();
                switch (usage) {
                    case POSITION:
                        GL11.glDisableClientState(32884);
                        break;
                    case UV:
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + index);
                        GL11.glDisableClientState(32888);
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                        break;
                    case COLOR:
                        GL11.glDisableClientState(32886);
                        GlStateManager.resetColor();
                }
            }
        }

        this.mc.entityRenderer.disableLightmap();
    }

    private void cleanupDamagedBlocks(Iterator<DestroyBlockProgress> iteratorIn) {
        while (iteratorIn.hasNext()) {
            DestroyBlockProgress destroyBlockProgress = (DestroyBlockProgress)iteratorIn.next();
            int creationCloudUpdateTick = destroyBlockProgress.getCreationCloudUpdateTick();
            if (this.cloudTickCounter - creationCloudUpdateTick > 400) {
                iteratorIn.remove();
            }
        }
    }

    public void updateClouds() {
        if (Config.isShaders() && Keyboard.isKeyDown(Keyboard.KEY_F3) && Keyboard.isKeyDown(Keyboard.KEY_R)) {
            Shaders.uninit();
            Shaders.loadShaderPack();
        }

        this.cloudTickCounter++;
        if (this.cloudTickCounter % 20 == 0) {
            this.cleanupDamagedBlocks(this.damagedBlocks.values().iterator());
        }
    }

    private void renderSkyEnd() {
        if (Config.isSkyEnabled()) {
            GlStateManager.disableFog();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.depthMask(false);
            this.renderEngine.bindTexture(locationEndSkyPng);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();

            for (int i = 0; i < 6; i++) {
                GlStateManager.pushMatrix();
                if (i == 1) {
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (i == 2) {
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (i == 3) {
                    GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                }

                if (i == 4) {
                    GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                }

                if (i == 5) {
                    GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                }

                worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
                int n = 40;
                int n2 = 40;
                int n3 = 40;
                if (Config.isCustomColors()) {
                    Vec3 vec = new Vec3(n / 255.0, n2 / 255.0, n3 / 255.0);
                    vec = CustomColors.getWorldSkyColor(vec, this.theWorld, this.mc.getRenderViewEntity(), 0.0F);
                    n = (int)(vec.xCoord * 255.0);
                    n2 = (int)(vec.yCoord * 255.0);
                    n3 = (int)(vec.zCoord * 255.0);
                }

                worldRenderer.func_181662_b(-100.0, -100.0, -100.0).func_181673_a(0.0, 0.0).func_181669_b(n, n2, n3, 255).func_181675_d();
                worldRenderer.func_181662_b(-100.0, -100.0, 100.0).func_181673_a(0.0, 16.0).func_181669_b(n, n2, n3, 255).func_181675_d();
                worldRenderer.func_181662_b(100.0, -100.0, 100.0).func_181673_a(16.0, 16.0).func_181669_b(n, n2, n3, 255).func_181675_d();
                worldRenderer.func_181662_b(100.0, -100.0, -100.0).func_181673_a(16.0, 0.0).func_181669_b(n, n2, n3, 255).func_181675_d();
                tessellator.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
        }
    }

    public void renderSky(float partialTicks, int pass) {
        if (Reflector.ForgeWorldProvider_getSkyRenderer.exists()) {
            WorldProvider provider = this.mc.theWorld.provider;
            var obj = Reflector.call(provider, Reflector.ForgeWorldProvider_getSkyRenderer);
            if (obj != null) {
                Reflector.callVoid(obj, Reflector.IRenderHandler_render, partialTicks, this.theWorld, this.mc);
                return;
            }
        }

        if (this.mc.theWorld.provider.getDimensionId() == 1) {
            this.renderSkyEnd();
        } else if (this.mc.theWorld.provider.isSurfaceWorld()) {
            GlStateManager.disableTexture2D();
            boolean shaders = Config.isShaders();
            if (shaders) {
                Shaders.disableTexture2D();
            }

            Vec3 skyColor = this.theWorld.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
            Vec3 skyColor2 = CustomColors.getSkyColor(
                skyColor, this.mc.theWorld, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0, this.mc.getRenderViewEntity().posZ
            );
            if (shaders) {
                Shaders.setSkyColor(skyColor2);
            }

            float xCoord = (float)skyColor2.xCoord;
            float yCoord = (float)skyColor2.yCoord;
            float zCoord = (float)skyColor2.zCoord;
            if (pass != 2) {
                float f = (xCoord * 30.0F + yCoord * 59.0F + zCoord * 11.0F) / 100.0F;
                float f2 = (xCoord * 30.0F + yCoord * 70.0F) / 100.0F;
                float f3 = (xCoord * 30.0F + zCoord * 70.0F) / 100.0F;
                xCoord = f;
                yCoord = f2;
                zCoord = f3;
            }

            GlStateManager.color(xCoord, yCoord, zCoord);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            GlStateManager.depthMask(false);
            GlStateManager.enableFog();
            if (shaders) {
                Shaders.enableFog();
            }

            GlStateManager.color(xCoord, yCoord, zCoord);
            if (shaders) {
                Shaders.preSkyList();
            }

            if (Config.isSkyEnabled()) {
                if (this.vboEnabled) {
                    this.skyVBO.bindBuffer();
                    GL11.glEnableClientState(32884);
                    GL11.glVertexPointer(3, 5126, 12, 0L);
                    this.skyVBO.drawArrays(7);
                    this.skyVBO.unbindBuffer();
                    GL11.glDisableClientState(32884);
                } else {
                    GlStateManager.callList(this.glSkyList);
                }
            }

            GlStateManager.disableFog();
            if (shaders) {
                Shaders.disableFog();
            }

            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            RenderHelper.disableStandardItemLighting();
            float[] floats = this.theWorld.provider.calcSunriseSunsetColors(this.theWorld.getCelestialAngle(partialTicks), partialTicks);
            if (floats != null && Config.isSunMoonEnabled()) {
                GlStateManager.disableTexture2D();
                if (shaders) {
                    Shaders.disableTexture2D();
                }

                GlStateManager.shadeModel(GL11.GL_SMOOTH);
                GlStateManager.pushMatrix();
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(MathHelper.sin(this.theWorld.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                float f4 = floats[0];
                float f5 = floats[1];
                float f6 = floats[2];
                if (pass != 2) {
                    float f7 = (f4 * 30.0F + f5 * 59.0F + f6 * 11.0F) / 100.0F;
                    float f8 = (f4 * 30.0F + f5 * 70.0F) / 100.0F;
                    float f9 = (f4 * 30.0F + f6 * 70.0F) / 100.0F;
                    f4 = f7;
                    f5 = f8;
                    f6 = f9;
                }

                worldRenderer.func_181668_a(6, DefaultVertexFormats.field_181706_f);
                worldRenderer.func_181662_b(0.0, 100.0, 0.0).func_181666_a(f4, f5, f6, floats[3]).func_181675_d();

                for (int i = 0; i <= 16; i++) {
                    float f10 = i * (float) Math.PI * 2.0F / 16.0F;
                    float sin = MathHelper.sin(f10);
                    float cos = MathHelper.cos(f10);
                    worldRenderer.func_181662_b(sin * 120.0F, cos * 120.0F, -cos * 40.0F * floats[3])
                        .func_181666_a(floats[0], floats[1], floats[2], 0.0F)
                        .func_181675_d();
                }

                tessellator.draw();
                GlStateManager.popMatrix();
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }

            GlStateManager.enableTexture2D();
            if (shaders) {
                Shaders.enableTexture2D();
            }

            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.pushMatrix();
            float f11 = 1.0F - this.theWorld.getRainStrength(partialTicks);
            GlStateManager.color(1.0F, 1.0F, 1.0F, f11);
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            CustomSky.renderSky(this.theWorld, this.renderEngine, partialTicks);
            if (shaders) {
                Shaders.preCelestialRotate();
            }

            GlStateManager.rotate(this.theWorld.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
            if (shaders) {
                Shaders.postCelestialRotate();
            }

            float f12 = 30.0F;
            if (Config.isSunTexture()) {
                this.renderEngine.bindTexture(locationSunPng);
                worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
                worldRenderer.func_181662_b(-f12, 100.0, -f12).func_181673_a(0.0, 0.0).func_181675_d();
                worldRenderer.func_181662_b(f12, 100.0, -f12).func_181673_a(1.0, 0.0).func_181675_d();
                worldRenderer.func_181662_b(f12, 100.0, f12).func_181673_a(1.0, 1.0).func_181675_d();
                worldRenderer.func_181662_b(-f12, 100.0, f12).func_181673_a(0.0, 1.0).func_181675_d();
                tessellator.draw();
            }

            f12 = 20.0F;
            if (Config.isMoonTexture()) {
                this.renderEngine.bindTexture(locationMoonPhasesPng);
                int moonPhase = this.theWorld.getMoonPhase();
                int n = moonPhase % 4;
                int n2 = moonPhase / 4 % 2;
                float f13 = (n + 0) / 4.0F;
                float f14 = (n2 + 0) / 2.0F;
                float f15 = (n + 1) / 4.0F;
                float f16 = (n2 + 1) / 2.0F;
                worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
                worldRenderer.func_181662_b(-f12, -100.0, f12).func_181673_a(f15, f16).func_181675_d();
                worldRenderer.func_181662_b(f12, -100.0, f12).func_181673_a(f13, f16).func_181675_d();
                worldRenderer.func_181662_b(f12, -100.0, -f12).func_181673_a(f13, f14).func_181675_d();
                worldRenderer.func_181662_b(-f12, -100.0, -f12).func_181673_a(f15, f14).func_181675_d();
                tessellator.draw();
            }

            GlStateManager.disableTexture2D();
            if (shaders) {
                Shaders.disableTexture2D();
            }

            float f17 = this.theWorld.getStarBrightness(partialTicks) * f11;
            if (f17 > 0.0F && Config.isStarsEnabled() && !CustomSky.hasSkyLayers(this.theWorld)) {
                GlStateManager.color(f17, f17, f17, f17);
                if (this.vboEnabled) {
                    this.starVBO.bindBuffer();
                    GL11.glEnableClientState(32884);
                    GL11.glVertexPointer(3, 5126, 12, 0L);
                    this.starVBO.drawArrays(7);
                    this.starVBO.unbindBuffer();
                    GL11.glDisableClientState(32884);
                } else {
                    GlStateManager.callList(this.starGLCallList);
                }
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableFog();
            if (shaders) {
                Shaders.enableFog();
            }

            GlStateManager.popMatrix();
            GlStateManager.disableTexture2D();
            if (shaders) {
                Shaders.disableTexture2D();
            }

            GlStateManager.color(0.0F, 0.0F, 0.0F);
            double d = this.mc.thePlayer.getPositionEyes(partialTicks).yCoord - this.theWorld.getHorizon();
            if (d < 0.0) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, 12.0F, 0.0F);
                if (this.vboEnabled) {
                    this.sky2VBO.bindBuffer();
                    GL11.glEnableClientState(32884);
                    GL11.glVertexPointer(3, 5126, 12, 0L);
                    this.sky2VBO.drawArrays(7);
                    this.sky2VBO.unbindBuffer();
                    GL11.glDisableClientState(32884);
                } else {
                    GlStateManager.callList(this.glSkyList2);
                }

                GlStateManager.popMatrix();
                float f19 = -((float)(d + 65.0));
                float f20 = -1.0F;
                worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
                worldRenderer.func_181662_b(-1.0, f19, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(1.0, f19, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(1.0, -1.0, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(-1.0, -1.0, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(-1.0, -1.0, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(1.0, -1.0, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(1.0, f19, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(-1.0, f19, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(1.0, -1.0, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(1.0, -1.0, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(1.0, f19, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(1.0, f19, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(-1.0, f19, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(-1.0, f19, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(-1.0, -1.0, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(-1.0, -1.0, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(-1.0, -1.0, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(-1.0, -1.0, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(1.0, -1.0, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(1.0, -1.0, -1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                tessellator.draw();
            }

            if (this.theWorld.provider.isSkyColored()) {
                GlStateManager.color(xCoord * 0.2F + 0.04F, yCoord * 0.2F + 0.04F, zCoord * 0.6F + 0.1F);
            } else {
                GlStateManager.color(xCoord, yCoord, zCoord);
            }

            if (this.mc.gameSettings.renderDistanceChunks <= 4) {
                GlStateManager.color(this.mc.entityRenderer.fogColorRed, this.mc.entityRenderer.fogColorGreen, this.mc.entityRenderer.fogColorBlue);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, -((float)(d - 16.0)), 0.0F);
            if (Config.isSkyEnabled()) {
                if (this.vboEnabled) {
                    this.sky2VBO.bindBuffer();
                    GlStateManager.glEnableClientState(32884);
                    GlStateManager.glVertexPointer(3, 5126, 12, 0);
                    this.sky2VBO.drawArrays(7);
                    this.sky2VBO.unbindBuffer();
                    GlStateManager.glDisableClientState(32884);
                } else {
                    GlStateManager.callList(this.glSkyList2);
                }
            }

            GlStateManager.popMatrix();
            GlStateManager.enableTexture2D();
            if (shaders) {
                Shaders.enableTexture2D();
            }

            GlStateManager.depthMask(true);
        }
    }

    public void renderClouds(float partialTicks, int pass) {
        if (!Config.isCloudsOff()) {
            if (Reflector.ForgeWorldProvider_getCloudRenderer.exists()) {
                WorldProvider provider = this.mc.theWorld.provider;
                var obj = Reflector.call(provider, Reflector.ForgeWorldProvider_getCloudRenderer);
                if (obj != null) {
                    Reflector.callVoid(obj, Reflector.IRenderHandler_render, partialTicks, this.theWorld, this.mc);
                    return;
                }
            }

            if (this.mc.theWorld.provider.isSurfaceWorld()) {
                if (Config.isShaders()) {
                    Shaders.beginClouds();
                }

                if (Config.isCloudsFancy()) {
                    this.renderCloudsFancy(partialTicks, pass);
                } else {
                    partialTicks = 0.0F;
                    GlStateManager.disableCull();
                    float f = (float)(
                        this.mc.getRenderViewEntity().lastTickPosY + (this.mc.getRenderViewEntity().posY - this.mc.getRenderViewEntity().lastTickPosY) * partialTicks
                    );
                    Tessellator tessellator = Tessellator.getInstance();
                    WorldRenderer worldRenderer = tessellator.getWorldRenderer();
                    this.renderEngine.bindTexture(locationCloudsPng);
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                    Vec3 cloudColour = this.theWorld.getCloudColour(partialTicks);
                    float xCoord = (float)cloudColour.xCoord;
                    float yCoord = (float)cloudColour.yCoord;
                    float zCoord = (float)cloudColour.zCoord;
                    if (this.cloudRenderer != null) {
                        this.cloudRenderer.prepareToRender(false, this.cloudTickCounter, partialTicks, cloudColour);
                        if (this.cloudRenderer.shouldUpdateGlList()) {
                            this.cloudRenderer.startUpdateGlList();
                            if (pass != 2) {
                                float f2 = (xCoord * 30.0F + yCoord * 59.0F + zCoord * 11.0F) / 100.0F;
                                float f3 = (xCoord * 30.0F + yCoord * 70.0F) / 100.0F;
                                float f4 = (xCoord * 30.0F + zCoord * 70.0F) / 100.0F;
                                xCoord = f2;
                                yCoord = f3;
                                zCoord = f4;
                            }

                            double d = this.cloudTickCounter + partialTicks;
                            double d2 = this.mc.getRenderViewEntity().prevPosX
                                + (this.mc.getRenderViewEntity().posX - this.mc.getRenderViewEntity().prevPosX) * partialTicks
                                + d * 0.03F;
                            double d3 = this.mc.getRenderViewEntity().prevPosZ
                                + (this.mc.getRenderViewEntity().posZ - this.mc.getRenderViewEntity().prevPosZ) * partialTicks;
                            int n2 = MathHelper.floor_double(d2 / 2048.0);
                            int n3 = MathHelper.floor_double(d3 / 2048.0);
                            d2 -= n2 * 2048;
                            d3 -= n3 * 2048;
                            float f6 = this.theWorld.provider.getCloudHeight() - f + 0.33F;
                            float f7 = f6 + this.mc.gameSettings.ofCloudsHeight * 128.0F;
                            float f8 = (float)(d2 * 4.8828125E-4);
                            float f9 = (float)(d3 * 4.8828125E-4);
                            worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);

                            for (int i = -256; i < 256; i += 32) {
                                for (int j = -256; j < 256; j += 32) {
                                    worldRenderer.func_181662_b(i + 0, f7, j + 32)
                                        .func_181673_a((i + 0) * 4.8828125E-4F + f8, (j + 32) * 4.8828125E-4F + f9)
                                        .func_181666_a(xCoord, yCoord, zCoord, 0.8F)
                                        .func_181675_d();
                                    worldRenderer.func_181662_b(i + 32, f7, j + 32)
                                        .func_181673_a((i + 32) * 4.8828125E-4F + f8, (j + 32) * 4.8828125E-4F + f9)
                                        .func_181666_a(xCoord, yCoord, zCoord, 0.8F)
                                        .func_181675_d();
                                    worldRenderer.func_181662_b(i + 32, f7, j + 0)
                                        .func_181673_a((i + 32) * 4.8828125E-4F + f8, (j + 0) * 4.8828125E-4F + f9)
                                        .func_181666_a(xCoord, yCoord, zCoord, 0.8F)
                                        .func_181675_d();
                                    worldRenderer.func_181662_b(i + 0, f7, j + 0)
                                        .func_181673_a((i + 0) * 4.8828125E-4F + f8, (j + 0) * 4.8828125E-4F + f9)
                                        .func_181666_a(xCoord, yCoord, zCoord, 0.8F)
                                        .func_181675_d();
                                }
                            }

                            tessellator.draw();
                            this.cloudRenderer.endUpdateGlList();
                        }

                        this.cloudRenderer.renderGlList();
                    }

                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.disableBlend();
                    GlStateManager.enableCull();
                }

                if (Config.isShaders()) {
                    Shaders.endClouds();
                }
            }
        }
    }

    public boolean hasCloudFog(double x, double y, double z, float partialTicks) {
        return false;
    }

    private void renderCloudsFancy(float partialTicks, int pass) {
        partialTicks = 0.0F;
        GlStateManager.disableCull();
        float f = (float)(
            this.mc.getRenderViewEntity().lastTickPosY + (this.mc.getRenderViewEntity().posY - this.mc.getRenderViewEntity().lastTickPosY) * partialTicks
        );
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        double d = this.cloudTickCounter + partialTicks;
        double d2 = (
                this.mc.getRenderViewEntity().prevPosX + (this.mc.getRenderViewEntity().posX - this.mc.getRenderViewEntity().prevPosX) * partialTicks + d * 0.03F
            )
            / 12.0;
        double d3 = (this.mc.getRenderViewEntity().prevPosZ + (this.mc.getRenderViewEntity().posZ - this.mc.getRenderViewEntity().prevPosZ) * partialTicks) / 12.0
            + 0.33F;
        float f4 = this.theWorld.provider.getCloudHeight() - f + 0.33F;
        float f5 = f4 + this.mc.gameSettings.ofCloudsHeight * 128.0F;
        int n = MathHelper.floor_double(d2 / 2048.0);
        int n2 = MathHelper.floor_double(d3 / 2048.0);
        double d4 = d2 - n * 2048;
        double d5 = d3 - n2 * 2048;
        this.renderEngine.bindTexture(locationCloudsPng);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        Vec3 cloudColour = this.theWorld.getCloudColour(partialTicks);
        float xCoord = (float)cloudColour.xCoord;
        float yCoord = (float)cloudColour.yCoord;
        float zCoord = (float)cloudColour.zCoord;
        if (this.cloudRenderer != null) {
            this.cloudRenderer.prepareToRender(true, this.cloudTickCounter, partialTicks, cloudColour);
            if (pass != 2) {
                float f6 = (xCoord * 30.0F + yCoord * 59.0F + zCoord * 11.0F) / 100.0F;
                float f7 = (xCoord * 30.0F + yCoord * 70.0F) / 100.0F;
                float f8 = (xCoord * 30.0F + zCoord * 70.0F) / 100.0F;
                xCoord = f6;
                yCoord = f7;
                zCoord = f8;
            }

            float f9 = xCoord * 0.9F;
            float f10 = yCoord * 0.9F;
            float f11 = zCoord * 0.9F;
            float f12 = xCoord * 0.7F;
            float f13 = yCoord * 0.7F;
            float f14 = zCoord * 0.7F;
            float f15 = xCoord * 0.8F;
            float f16 = yCoord * 0.8F;
            float f17 = zCoord * 0.8F;
            float f19 = MathHelper.floor_double(d4) * 0.00390625F;
            float f20 = MathHelper.floor_double(d5) * 0.00390625F;
            float f21 = (float)(d4 - MathHelper.floor_double(d4));
            float f22 = (float)(d5 - MathHelper.floor_double(d5));
            GlStateManager.scale(12.0F, 1.0F, 12.0F);

            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    GlStateManager.colorMask(false, false, false, false);
                } else {
                    switch (pass) {
                        case 0:
                            GlStateManager.colorMask(false, true, true, true);
                            break;
                        case 1:
                            GlStateManager.colorMask(true, false, false, true);
                            break;
                        case 2:
                            GlStateManager.colorMask(true, true, true, true);
                    }
                }

                this.cloudRenderer.renderGlList();
            }

            if (this.cloudRenderer != null && this.cloudRenderer.shouldUpdateGlList()) {
                this.cloudRenderer.startUpdateGlList();

                for (int j = -3; j <= 4; j++) {
                    for (int k = -3; k <= 4; k++) {
                        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181712_l);
                        float f24 = j * 8;
                        float f25 = k * 8;
                        float f26 = f24 - f21;
                        float f27 = f25 - f22;
                        if (f5 > -5.0F) {
                            worldRenderer.func_181662_b(f26 + 0.0F, f5 + 0.0F, f27 + 8.0F)
                                .func_181673_a((f24 + 0.0F) * 0.00390625F + f19, (f25 + 8.0F) * 0.00390625F + f20)
                                .func_181666_a(f12, f13, f14, 0.8F)
                                .func_181663_c(0.0F, -1.0F, 0.0F)
                                .func_181675_d();
                            worldRenderer.func_181662_b(f26 + 8.0F, f5 + 0.0F, f27 + 8.0F)
                                .func_181673_a((f24 + 8.0F) * 0.00390625F + f19, (f25 + 8.0F) * 0.00390625F + f20)
                                .func_181666_a(f12, f13, f14, 0.8F)
                                .func_181663_c(0.0F, -1.0F, 0.0F)
                                .func_181675_d();
                            worldRenderer.func_181662_b(f26 + 8.0F, f5 + 0.0F, f27 + 0.0F)
                                .func_181673_a((f24 + 8.0F) * 0.00390625F + f19, (f25 + 0.0F) * 0.00390625F + f20)
                                .func_181666_a(f12, f13, f14, 0.8F)
                                .func_181663_c(0.0F, -1.0F, 0.0F)
                                .func_181675_d();
                            worldRenderer.func_181662_b(f26 + 0.0F, f5 + 0.0F, f27 + 0.0F)
                                .func_181673_a((f24 + 0.0F) * 0.00390625F + f19, (f25 + 0.0F) * 0.00390625F + f20)
                                .func_181666_a(f12, f13, f14, 0.8F)
                                .func_181663_c(0.0F, -1.0F, 0.0F)
                                .func_181675_d();
                        }

                        if (f5 <= 5.0F) {
                            worldRenderer.func_181662_b(f26 + 0.0F, f5 + 4.0F - 9.765625E-4F, f27 + 8.0F)
                                .func_181673_a((f24 + 0.0F) * 0.00390625F + f19, (f25 + 8.0F) * 0.00390625F + f20)
                                .func_181666_a(xCoord, yCoord, zCoord, 0.8F)
                                .func_181663_c(0.0F, 1.0F, 0.0F)
                                .func_181675_d();
                            worldRenderer.func_181662_b(f26 + 8.0F, f5 + 4.0F - 9.765625E-4F, f27 + 8.0F)
                                .func_181673_a((f24 + 8.0F) * 0.00390625F + f19, (f25 + 8.0F) * 0.00390625F + f20)
                                .func_181666_a(xCoord, yCoord, zCoord, 0.8F)
                                .func_181663_c(0.0F, 1.0F, 0.0F)
                                .func_181675_d();
                            worldRenderer.func_181662_b(f26 + 8.0F, f5 + 4.0F - 9.765625E-4F, f27 + 0.0F)
                                .func_181673_a((f24 + 8.0F) * 0.00390625F + f19, (f25 + 0.0F) * 0.00390625F + f20)
                                .func_181666_a(xCoord, yCoord, zCoord, 0.8F)
                                .func_181663_c(0.0F, 1.0F, 0.0F)
                                .func_181675_d();
                            worldRenderer.func_181662_b(f26 + 0.0F, f5 + 4.0F - 9.765625E-4F, f27 + 0.0F)
                                .func_181673_a((f24 + 0.0F) * 0.00390625F + f19, (f25 + 0.0F) * 0.00390625F + f20)
                                .func_181666_a(xCoord, yCoord, zCoord, 0.8F)
                                .func_181663_c(0.0F, 1.0F, 0.0F)
                                .func_181675_d();
                        }

                        if (j > -1) {
                            for (int idx = 0; idx < 8; idx++) {
                                worldRenderer.func_181662_b(f26 + idx + 0.0F, f5 + 0.0F, f27 + 8.0F)
                                    .func_181673_a((f24 + idx + 0.5F) * 0.00390625F + f19, (f25 + 8.0F) * 0.00390625F + f20)
                                    .func_181666_a(f9, f10, f11, 0.8F)
                                    .func_181663_c(-1.0F, 0.0F, 0.0F)
                                    .func_181675_d();
                                worldRenderer.func_181662_b(f26 + idx + 0.0F, f5 + 4.0F, f27 + 8.0F)
                                    .func_181673_a((f24 + idx + 0.5F) * 0.00390625F + f19, (f25 + 8.0F) * 0.00390625F + f20)
                                    .func_181666_a(f9, f10, f11, 0.8F)
                                    .func_181663_c(-1.0F, 0.0F, 0.0F)
                                    .func_181675_d();
                                worldRenderer.func_181662_b(f26 + idx + 0.0F, f5 + 4.0F, f27 + 0.0F)
                                    .func_181673_a((f24 + idx + 0.5F) * 0.00390625F + f19, (f25 + 0.0F) * 0.00390625F + f20)
                                    .func_181666_a(f9, f10, f11, 0.8F)
                                    .func_181663_c(-1.0F, 0.0F, 0.0F)
                                    .func_181675_d();
                                worldRenderer.func_181662_b(f26 + idx + 0.0F, f5 + 0.0F, f27 + 0.0F)
                                    .func_181673_a((f24 + idx + 0.5F) * 0.00390625F + f19, (f25 + 0.0F) * 0.00390625F + f20)
                                    .func_181666_a(f9, f10, f11, 0.8F)
                                    .func_181663_c(-1.0F, 0.0F, 0.0F)
                                    .func_181675_d();
                            }
                        }

                        if (j <= 1) {
                            for (int idx2 = 0; idx2 < 8; idx2++) {
                                worldRenderer.func_181662_b(f26 + idx2 + 1.0F - 9.765625E-4F, f5 + 0.0F, f27 + 8.0F)
                                    .func_181673_a((f24 + idx2 + 0.5F) * 0.00390625F + f19, (f25 + 8.0F) * 0.00390625F + f20)
                                    .func_181666_a(f9, f10, f11, 0.8F)
                                    .func_181663_c(1.0F, 0.0F, 0.0F)
                                    .func_181675_d();
                                worldRenderer.func_181662_b(f26 + idx2 + 1.0F - 9.765625E-4F, f5 + 4.0F, f27 + 8.0F)
                                    .func_181673_a((f24 + idx2 + 0.5F) * 0.00390625F + f19, (f25 + 8.0F) * 0.00390625F + f20)
                                    .func_181666_a(f9, f10, f11, 0.8F)
                                    .func_181663_c(1.0F, 0.0F, 0.0F)
                                    .func_181675_d();
                                worldRenderer.func_181662_b(f26 + idx2 + 1.0F - 9.765625E-4F, f5 + 4.0F, f27 + 0.0F)
                                    .func_181673_a((f24 + idx2 + 0.5F) * 0.00390625F + f19, (f25 + 0.0F) * 0.00390625F + f20)
                                    .func_181666_a(f9, f10, f11, 0.8F)
                                    .func_181663_c(1.0F, 0.0F, 0.0F)
                                    .func_181675_d();
                                worldRenderer.func_181662_b(f26 + idx2 + 1.0F - 9.765625E-4F, f5 + 0.0F, f27 + 0.0F)
                                    .func_181673_a((f24 + idx2 + 0.5F) * 0.00390625F + f19, (f25 + 0.0F) * 0.00390625F + f20)
                                    .func_181666_a(f9, f10, f11, 0.8F)
                                    .func_181663_c(1.0F, 0.0F, 0.0F)
                                    .func_181675_d();
                            }
                        }

                        if (k > -1) {
                            for (int idx3 = 0; idx3 < 8; idx3++) {
                                worldRenderer.func_181662_b(f26 + 0.0F, f5 + 4.0F, f27 + idx3 + 0.0F)
                                    .func_181673_a((f24 + 0.0F) * 0.00390625F + f19, (f25 + idx3 + 0.5F) * 0.00390625F + f20)
                                    .func_181666_a(f15, f16, f17, 0.8F)
                                    .func_181663_c(0.0F, 0.0F, -1.0F)
                                    .func_181675_d();
                                worldRenderer.func_181662_b(f26 + 8.0F, f5 + 4.0F, f27 + idx3 + 0.0F)
                                    .func_181673_a((f24 + 8.0F) * 0.00390625F + f19, (f25 + idx3 + 0.5F) * 0.00390625F + f20)
                                    .func_181666_a(f15, f16, f17, 0.8F)
                                    .func_181663_c(0.0F, 0.0F, -1.0F)
                                    .func_181675_d();
                                worldRenderer.func_181662_b(f26 + 8.0F, f5 + 0.0F, f27 + idx3 + 0.0F)
                                    .func_181673_a((f24 + 8.0F) * 0.00390625F + f19, (f25 + idx3 + 0.5F) * 0.00390625F + f20)
                                    .func_181666_a(f15, f16, f17, 0.8F)
                                    .func_181663_c(0.0F, 0.0F, -1.0F)
                                    .func_181675_d();
                                worldRenderer.func_181662_b(f26 + 0.0F, f5 + 0.0F, f27 + idx3 + 0.0F)
                                    .func_181673_a((f24 + 0.0F) * 0.00390625F + f19, (f25 + idx3 + 0.5F) * 0.00390625F + f20)
                                    .func_181666_a(f15, f16, f17, 0.8F)
                                    .func_181663_c(0.0F, 0.0F, -1.0F)
                                    .func_181675_d();
                            }
                        }

                        if (k <= 1) {
                            for (int idx4 = 0; idx4 < 8; idx4++) {
                                worldRenderer.func_181662_b(f26 + 0.0F, f5 + 4.0F, f27 + idx4 + 1.0F - 9.765625E-4F)
                                    .func_181673_a((f24 + 0.0F) * 0.00390625F + f19, (f25 + idx4 + 0.5F) * 0.00390625F + f20)
                                    .func_181666_a(f15, f16, f17, 0.8F)
                                    .func_181663_c(0.0F, 0.0F, 1.0F)
                                    .func_181675_d();
                                worldRenderer.func_181662_b(f26 + 8.0F, f5 + 4.0F, f27 + idx4 + 1.0F - 9.765625E-4F)
                                    .func_181673_a((f24 + 8.0F) * 0.00390625F + f19, (f25 + idx4 + 0.5F) * 0.00390625F + f20)
                                    .func_181666_a(f15, f16, f17, 0.8F)
                                    .func_181663_c(0.0F, 0.0F, 1.0F)
                                    .func_181675_d();
                                worldRenderer.func_181662_b(f26 + 8.0F, f5 + 0.0F, f27 + idx4 + 1.0F - 9.765625E-4F)
                                    .func_181673_a((f24 + 8.0F) * 0.00390625F + f19, (f25 + idx4 + 0.5F) * 0.00390625F + f20)
                                    .func_181666_a(f15, f16, f17, 0.8F)
                                    .func_181663_c(0.0F, 0.0F, 1.0F)
                                    .func_181675_d();
                                worldRenderer.func_181662_b(f26 + 0.0F, f5 + 0.0F, f27 + idx4 + 1.0F - 9.765625E-4F)
                                    .func_181673_a((f24 + 0.0F) * 0.00390625F + f19, (f25 + idx4 + 0.5F) * 0.00390625F + f20)
                                    .func_181666_a(f15, f16, f17, 0.8F)
                                    .func_181663_c(0.0F, 0.0F, 1.0F)
                                    .func_181675_d();
                            }
                        }

                        tessellator.draw();
                    }
                }

                this.cloudRenderer.endUpdateGlList();
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }

    public void updateChunks(long finishTimeNano) {
        finishTimeNano = (long)(finishTimeNano + 1.0E8);
        this.displayListEntitiesDirty = this.displayListEntitiesDirty | this.renderDispatcher.runChunkUploads(finishTimeNano);
        if (this.chunksToUpdateForced.size() > 0) {
            var it = this.chunksToUpdateForced.iterator();

            while (it.hasNext()) {
                RenderChunk renderChunk = (RenderChunk)it.next();
                if (!this.renderDispatcher.updateChunkLater(renderChunk)) {
                    break;
                }

                renderChunk.setNeedsUpdate(false);
                it.remove();
                this.chunksToUpdate.remove(renderChunk);
                this.chunksToResortTransparency.remove(renderChunk);
            }
        }

        if (this.chunksToResortTransparency.size() > 0) {
            var it2 = this.chunksToResortTransparency.iterator();
            if (it2.hasNext()) {
                RenderChunk renderChunk2 = (RenderChunk)it2.next();
                if (this.renderDispatcher.updateTransparencyLater(renderChunk2)) {
                    it2.remove();
                }
            }
        }

        double d = 0.0;
        int updatesPerFrame = Config.getUpdatesPerFrame();
        if (!this.chunksToUpdate.isEmpty()) {
            var it3 = this.chunksToUpdate.iterator();

            while (it3.hasNext()) {
                RenderChunk renderChunk3 = (RenderChunk)it3.next();
                boolean chunkRegionEmpty = renderChunk3.isChunkRegionEmpty();
                boolean flag;
                if (chunkRegionEmpty) {
                    flag = this.renderDispatcher.updateChunkNow(renderChunk3);
                } else {
                    flag = this.renderDispatcher.updateChunkLater(renderChunk3);
                }

                if (!flag) {
                    break;
                }

                renderChunk3.setNeedsUpdate(false);
                it3.remove();
                if (!chunkRegionEmpty) {
                    double d2 = 2.0 * RenderChunkUtils.getRelativeBufferSize(renderChunk3);
                    d += d2;
                    if (d > updatesPerFrame) {
                        break;
                    }
                }
            }
        }
    }

    public void renderWorldBorder(Entity p_180449_1_, float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        WorldBorder worldBorder = this.theWorld.getWorldBorder();
        double d = this.mc.gameSettings.renderDistanceChunks * 16;
        if (p_180449_1_.posX >= worldBorder.maxX() - d || p_180449_1_.posX <= worldBorder.minX() + d || p_180449_1_.posZ >= worldBorder.maxZ() - d || p_180449_1_.posZ <= worldBorder.minZ() + d) {
            double d2 = 1.0 - worldBorder.getClosestDistance(p_180449_1_) / d;
            d2 = Math.pow(d2, 4.0);
            double d3 = p_180449_1_.lastTickPosX + (p_180449_1_.posX - p_180449_1_.lastTickPosX) * partialTicks;
            double d4 = p_180449_1_.lastTickPosY + (p_180449_1_.posY - p_180449_1_.lastTickPosY) * partialTicks;
            double d5 = p_180449_1_.lastTickPosZ + (p_180449_1_.posZ - p_180449_1_.lastTickPosZ) * partialTicks;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
            this.renderEngine.bindTexture(locationForcefieldPng);
            GlStateManager.depthMask(false);
            GlStateManager.pushMatrix();
            int id = worldBorder.getStatus().getID();
            float f = (id >> 16 & 0xFF) / 255.0F;
            float f2 = (id >> 8 & 0xFF) / 255.0F;
            float f3 = (id & 0xFF) / 255.0F;
            GlStateManager.color(f, f2, f3, (float)d2);
            GlStateManager.doPolygonOffset(-3.0F, -3.0F);
            GlStateManager.enablePolygonOffset();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            GlStateManager.enableAlpha();
            GlStateManager.disableCull();
            float f4 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F;
            worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
            worldRenderer.setTranslation(-d3, -d4, -d5);
            double d6 = Math.max((double)MathHelper.floor_double(d5 - d), worldBorder.minZ());
            double d7 = Math.min((double)MathHelper.ceiling_double_int(d5 + d), worldBorder.maxZ());
            if (d3 > worldBorder.maxX() - d) {
                float f8 = 0.0F;

                for (double i = d6; i < d7; f8 += 0.5F) {
                    double d8 = Math.min(1.0, d7 - i);
                    float f9 = (float)d8 * 0.5F;
                    worldRenderer.func_181662_b(worldBorder.maxX(), 256.0, i).func_181673_a(f4 + f8, f4 + 0.0F).func_181675_d();
                    worldRenderer.func_181662_b(worldBorder.maxX(), 256.0, i + d8).func_181673_a(f4 + f9 + f8, f4 + 0.0F).func_181675_d();
                    worldRenderer.func_181662_b(worldBorder.maxX(), 0.0, i + d8).func_181673_a(f4 + f9 + f8, f4 + 128.0F).func_181675_d();
                    worldRenderer.func_181662_b(worldBorder.maxX(), 0.0, i).func_181673_a(f4 + f8, f4 + 128.0F).func_181675_d();
                    i++;
                }
            }

            if (d3 < worldBorder.minX() + d) {
                float f10 = 0.0F;

                for (double j = d6; j < d7; f10 += 0.5F) {
                    double d9 = Math.min(1.0, d7 - j);
                    float f11 = (float)d9 * 0.5F;
                    worldRenderer.func_181662_b(worldBorder.minX(), 256.0, j).func_181673_a(f4 + f10, f4 + 0.0F).func_181675_d();
                    worldRenderer.func_181662_b(worldBorder.minX(), 256.0, j + d9).func_181673_a(f4 + f11 + f10, f4 + 0.0F).func_181675_d();
                    worldRenderer.func_181662_b(worldBorder.minX(), 0.0, j + d9).func_181673_a(f4 + f11 + f10, f4 + 128.0F).func_181675_d();
                    worldRenderer.func_181662_b(worldBorder.minX(), 0.0, j).func_181673_a(f4 + f10, f4 + 128.0F).func_181675_d();
                    j++;
                }
            }

            d6 = Math.max((double)MathHelper.floor_double(d3 - d), worldBorder.minX());
            d7 = Math.min((double)MathHelper.ceiling_double_int(d3 + d), worldBorder.maxX());
            if (d5 > worldBorder.maxZ() - d) {
                float f12 = 0.0F;

                for (double k = d6; k < d7; f12 += 0.5F) {
                    double d10 = Math.min(1.0, d7 - k);
                    float f13 = (float)d10 * 0.5F;
                    worldRenderer.func_181662_b(k, 256.0, worldBorder.maxZ()).func_181673_a(f4 + f12, f4 + 0.0F).func_181675_d();
                    worldRenderer.func_181662_b(k + d10, 256.0, worldBorder.maxZ()).func_181673_a(f4 + f13 + f12, f4 + 0.0F).func_181675_d();
                    worldRenderer.func_181662_b(k + d10, 0.0, worldBorder.maxZ()).func_181673_a(f4 + f13 + f12, f4 + 128.0F).func_181675_d();
                    worldRenderer.func_181662_b(k, 0.0, worldBorder.maxZ()).func_181673_a(f4 + f12, f4 + 128.0F).func_181675_d();
                    k++;
                }
            }

            if (d5 < worldBorder.minZ() + d) {
                float f14 = 0.0F;

                for (double idx = d6; idx < d7; f14 += 0.5F) {
                    double d11 = Math.min(1.0, d7 - idx);
                    float f15 = (float)d11 * 0.5F;
                    worldRenderer.func_181662_b(idx, 256.0, worldBorder.minZ()).func_181673_a(f4 + f14, f4 + 0.0F).func_181675_d();
                    worldRenderer.func_181662_b(idx + d11, 256.0, worldBorder.minZ()).func_181673_a(f4 + f15 + f14, f4 + 0.0F).func_181675_d();
                    worldRenderer.func_181662_b(idx + d11, 0.0, worldBorder.minZ()).func_181673_a(f4 + f15 + f14, f4 + 128.0F).func_181675_d();
                    worldRenderer.func_181662_b(idx, 0.0, worldBorder.minZ()).func_181673_a(f4 + f14, f4 + 128.0F).func_181675_d();
                    idx++;
                }
            }

            tessellator.draw();
            worldRenderer.setTranslation(0.0, 0.0, 0.0);
            GlStateManager.enableCull();
            GlStateManager.disableAlpha();
            GlStateManager.doPolygonOffset(0.0F, 0.0F);
            GlStateManager.disablePolygonOffset();
            GlStateManager.enableAlpha();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
        }
    }

    private void preRenderDamagedBlocks() {
        GlStateManager.tryBlendFuncSeparate(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
        GlStateManager.doPolygonOffset(-1.0F, -10.0F);
        GlStateManager.enablePolygonOffset();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        if (Config.isShaders()) {
            ShadersRender.beginBlockDamage();
        }
    }

    private void postRenderDamagedBlocks() {
        GlStateManager.disableAlpha();
        GlStateManager.doPolygonOffset(0.0F, 0.0F);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
        if (Config.isShaders()) {
            ShadersRender.endBlockDamage();
        }
    }

    public void drawBlockDamageTexture(Tessellator tessellatorIn, WorldRenderer worldRendererIn, Entity entityIn, float partialTicks) {
        double d = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
        double d2 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
        double d3 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
        if (!this.damagedBlocks.isEmpty()) {
            this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            this.preRenderDamagedBlocks();
            worldRendererIn.func_181668_a(7, DefaultVertexFormats.BLOCK);
            worldRendererIn.setTranslation(-d, -d2, -d3);
            worldRendererIn.markDirty();
            var it = this.damagedBlocks.values().iterator();

            while (it.hasNext()) {
                DestroyBlockProgress destroyBlockProgress = (DestroyBlockProgress)it.next();
                BlockPos pos = destroyBlockProgress.getPosition();
                double d4 = pos.getX() - d;
                double d5 = pos.getY() - d2;
                double d6 = pos.getZ() - d3;
                Block block = this.theWorld.getBlockState(pos).getBlock();
                boolean flag;
                if (Reflector.ForgeTileEntity_canRenderBreaking.exists()) {
                    boolean flag2 = block instanceof BlockChest
                        || block instanceof BlockEnderChest
                        || block instanceof BlockSign
                        || block instanceof BlockSkull;
                    if (!flag2) {
                        TileEntity tileEntity = this.theWorld.getTileEntity(pos);
                        if (tileEntity != null) {
                            flag2 = Reflector.callBoolean(tileEntity, Reflector.ForgeTileEntity_canRenderBreaking);
                        }
                    }

                    flag = !flag2;
                } else {
                    flag = !(block instanceof BlockChest)
                        && !(block instanceof BlockEnderChest)
                        && !(block instanceof BlockSign)
                        && !(block instanceof BlockSkull);
                }

                if (flag) {
                    if (d4 * d4 + d5 * d5 + d6 * d6 > 1024.0) {
                        it.remove();
                    } else {
                        IBlockState blockState = this.theWorld.getBlockState(pos);
                        if (blockState.getBlock().getMaterial() != Material.air) {
                            int partialBlockDamage = destroyBlockProgress.getPartialBlockDamage();
                            TextureAtlasSprite textureAtlasSprite = this.destroyBlockIcons[partialBlockDamage];
                            BlockRendererDispatcher blockRendererDispatcher = this.mc.getBlockRendererDispatcher();
                            blockRendererDispatcher.renderBlockDamage(blockState, pos, textureAtlasSprite, this.theWorld);
                        }
                    }
                }
            }

            tessellatorIn.draw();
            worldRendererIn.setTranslation(0.0, 0.0, 0.0);
            this.postRenderDamagedBlocks();
        }
    }

    public void drawSelectionBox(EntityPlayer player, MovingObjectPosition movingObjectPositionIn, int p_72731_3_, float partialTicks) {
        if (p_72731_3_ == 0 && movingObjectPositionIn.typeOfHit == MovingObjectType.BLOCK) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
            GL11.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();
            if (Config.isShaders()) {
                Shaders.disableTexture2D();
            }

            GlStateManager.depthMask(false);
            BlockPos blockPos = movingObjectPositionIn.getBlockPos();
            Block block = this.theWorld.getBlockState(blockPos).getBlock();
            if (block.getMaterial() != Material.air && this.theWorld.getWorldBorder().contains(blockPos)) {
                block.setBlockBoundsBasedOnState(this.theWorld, blockPos);
                double d = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
                double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
                double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
                AxisAlignedBB selectedBoundingBox = block.getSelectedBoundingBox(this.theWorld, blockPos);
                Block.EnumOffsetType offsetType = block.getOffsetType();
                if (offsetType != Block.EnumOffsetType.NONE) {
                    selectedBoundingBox = BlockModelUtils.getOffsetBoundingBox(selectedBoundingBox, offsetType, blockPos);
                }

                func_181561_a(selectedBoundingBox.expand(0.002F, 0.002F, 0.002F).offset(-d, -d2, -d3));
            }

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            if (Config.isShaders()) {
                Shaders.enableTexture2D();
            }

            GlStateManager.disableBlend();
        }
    }

    public static void func_181561_a(AxisAlignedBB p_181561_0_) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(3, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).func_181675_d();
        worldRenderer.func_181662_b(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).func_181675_d();
        worldRenderer.func_181662_b(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).func_181675_d();
        worldRenderer.func_181662_b(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).func_181675_d();
        worldRenderer.func_181662_b(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).func_181675_d();
        tessellator.draw();
        worldRenderer.func_181668_a(3, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).func_181675_d();
        worldRenderer.func_181662_b(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).func_181675_d();
        worldRenderer.func_181662_b(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).func_181675_d();
        worldRenderer.func_181662_b(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).func_181675_d();
        worldRenderer.func_181662_b(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).func_181675_d();
        tessellator.draw();
        worldRenderer.func_181668_a(1, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).func_181675_d();
        worldRenderer.func_181662_b(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).func_181675_d();
        worldRenderer.func_181662_b(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).func_181675_d();
        worldRenderer.func_181662_b(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).func_181675_d();
        worldRenderer.func_181662_b(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).func_181675_d();
        worldRenderer.func_181662_b(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).func_181675_d();
        worldRenderer.func_181662_b(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).func_181675_d();
        worldRenderer.func_181662_b(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).func_181675_d();
        tessellator.draw();
    }

    public static void func_181563_a(AxisAlignedBB p_181563_0_, int p_181563_1_, int p_181563_2_, int p_181563_3_, int p_181563_4_) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        worldRenderer.func_181662_b(p_181563_0_.minX, p_181563_0_.minY, p_181563_0_.minZ).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
        worldRenderer.func_181662_b(p_181563_0_.maxX, p_181563_0_.minY, p_181563_0_.minZ).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
        worldRenderer.func_181662_b(p_181563_0_.maxX, p_181563_0_.minY, p_181563_0_.maxZ).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
        worldRenderer.func_181662_b(p_181563_0_.minX, p_181563_0_.minY, p_181563_0_.maxZ).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
        worldRenderer.func_181662_b(p_181563_0_.minX, p_181563_0_.minY, p_181563_0_.minZ).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
        tessellator.draw();
        worldRenderer.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        worldRenderer.func_181662_b(p_181563_0_.minX, p_181563_0_.maxY, p_181563_0_.minZ).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
        worldRenderer.func_181662_b(p_181563_0_.maxX, p_181563_0_.maxY, p_181563_0_.minZ).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
        worldRenderer.func_181662_b(p_181563_0_.maxX, p_181563_0_.maxY, p_181563_0_.maxZ).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
        worldRenderer.func_181662_b(p_181563_0_.minX, p_181563_0_.maxY, p_181563_0_.maxZ).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
        worldRenderer.func_181662_b(p_181563_0_.minX, p_181563_0_.maxY, p_181563_0_.minZ).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
        tessellator.draw();
        worldRenderer.func_181668_a(1, DefaultVertexFormats.field_181706_f);
        worldRenderer.func_181662_b(p_181563_0_.minX, p_181563_0_.minY, p_181563_0_.minZ).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
        worldRenderer.func_181662_b(p_181563_0_.minX, p_181563_0_.maxY, p_181563_0_.minZ).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
        worldRenderer.func_181662_b(p_181563_0_.maxX, p_181563_0_.minY, p_181563_0_.minZ).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
        worldRenderer.func_181662_b(p_181563_0_.maxX, p_181563_0_.maxY, p_181563_0_.minZ).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
        worldRenderer.func_181662_b(p_181563_0_.maxX, p_181563_0_.minY, p_181563_0_.maxZ).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
        worldRenderer.func_181662_b(p_181563_0_.maxX, p_181563_0_.maxY, p_181563_0_.maxZ).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
        worldRenderer.func_181662_b(p_181563_0_.minX, p_181563_0_.minY, p_181563_0_.maxZ).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
        worldRenderer.func_181662_b(p_181563_0_.minX, p_181563_0_.maxY, p_181563_0_.maxZ).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
        tessellator.draw();
    }

    private void markBlocksForUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.viewFrustum.markBlocksForUpdate(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public void markBlockForUpdate(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        this.markBlocksForUpdate(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
    }

    @Override
    public void notifyLightSet(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        this.markBlocksForUpdate(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
    }

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.markBlocksForUpdate(x1 - 1, y1 - 1, z1 - 1, x2 + 1, y2 + 1, z2 + 1);
    }

    @Override
    public void playRecord(String recordName, BlockPos blockPosIn) {
        ISound iSound = this.mapSoundPositions.get(blockPosIn);
        if (iSound != null) {
            this.mc.getSoundHandler().stopSound(iSound);
            this.mapSoundPositions.remove(blockPosIn);
        }

        if (recordName != null) {
            ItemRecord item = ItemRecord.getRecord(recordName);
            if (item != null) {
                this.mc.ingameGUI.setRecordPlayingMessage(item.getRecordNameLocal());
            }

            PositionedSoundRecord positionedSoundRecord = PositionedSoundRecord.create(new ResourceLocation(recordName), blockPosIn.getX(), blockPosIn.getY(), blockPosIn.getZ());
            this.mapSoundPositions.put(blockPosIn, positionedSoundRecord);
            this.mc.getSoundHandler().playSound(positionedSoundRecord);
        }
    }

    @Override
    public void playSound(String soundName, double x, double y, double z, float volume, float pitch) {
    }

    @Override
    public void playSoundToNearExcept(EntityPlayer except, String soundName, double x, double y, double z, float volume, float pitch) {
    }

    @Override
    public void spawnParticle(
        int particleID, boolean ignoreRange, final double xCoord, final double yCoord, final double zCoord, double xOffset, double yOffset, double zOffset, int... p_180442_15_
    ) {
        try {
            this.spawnEntityFX(particleID, ignoreRange, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, p_180442_15_);
        } catch (Throwable e) {
            CrashReport crashReport = CrashReport.makeCrashReport(e, "Exception while adding particle");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Particle being added");
            crashReportCategory.addCrashSection("ID", particleID);
            if (p_180442_15_ != null) {
                crashReportCategory.addCrashSection("Parameters", p_180442_15_);
            }

            crashReportCategory.addCrashSectionCallable("Position", new Callable<String>() {
                public String call() throws Exception {
                    return CrashReportCategory.getCoordinateInfo(xCoord, yCoord, zCoord);
                }
            });
            throw new ReportedException(crashReport);
        }
    }

    private void spawnParticle(EnumParticleTypes particleIn, double p_174972_2_, double p_174972_4_, double p_174972_6_, double p_174972_8_, double p_174972_10_, double p_174972_12_, int... p_174972_14_) {
        this.spawnParticle(particleIn.getParticleID(), particleIn.getShouldIgnoreRange(), p_174972_2_, p_174972_4_, p_174972_6_, p_174972_8_, p_174972_10_, p_174972_12_, p_174972_14_);
    }

    private EntityFX spawnEntityFX(int p_174974_1_, boolean ignoreRange, double p_174974_3_, double p_174974_5_, double p_174974_7_, double p_174974_9_, double p_174974_11_, double p_174974_13_, int... p_174974_15_) {
        if (this.mc != null && this.mc.getRenderViewEntity() != null && this.mc.effectRenderer != null) {
            int particleSetting = this.mc.gameSettings.particleSetting;
            if (particleSetting == 1 && this.theWorld.rand.nextInt(3) == 0) {
                particleSetting = 2;
            }

            double d = this.mc.getRenderViewEntity().posX - p_174974_3_;
            double d2 = this.mc.getRenderViewEntity().posY - p_174974_5_;
            double d3 = this.mc.getRenderViewEntity().posZ - p_174974_7_;
            if (p_174974_1_ == EnumParticleTypes.EXPLOSION_HUGE.getParticleID() && !Config.isAnimatedExplosion()) {
                return null;
            }
            if (p_174974_1_ == EnumParticleTypes.EXPLOSION_LARGE.getParticleID() && !Config.isAnimatedExplosion()) {
                return null;
            }
            if (p_174974_1_ == EnumParticleTypes.EXPLOSION_NORMAL.getParticleID() && !Config.isAnimatedExplosion()) {
                return null;
            }
            if (p_174974_1_ == EnumParticleTypes.SUSPENDED.getParticleID() && !Config.isWaterParticles()) {
                return null;
            }
            if (p_174974_1_ == EnumParticleTypes.SUSPENDED_DEPTH.getParticleID() && !Config.isVoidParticles()) {
                return null;
            }
            if (p_174974_1_ == EnumParticleTypes.SMOKE_NORMAL.getParticleID() && !Config.isAnimatedSmoke()) {
                return null;
            }
            if (p_174974_1_ == EnumParticleTypes.SMOKE_LARGE.getParticleID() && !Config.isAnimatedSmoke()) {
                return null;
            }
            if (p_174974_1_ == EnumParticleTypes.SPELL_MOB.getParticleID() && !Config.isPotionParticles()) {
                return null;
            }
            if (p_174974_1_ == EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID() && !Config.isPotionParticles()) {
                return null;
            }
            if (p_174974_1_ == EnumParticleTypes.SPELL.getParticleID() && !Config.isPotionParticles()) {
                return null;
            }
            if (p_174974_1_ == EnumParticleTypes.SPELL_INSTANT.getParticleID() && !Config.isPotionParticles()) {
                return null;
            }
            if (p_174974_1_ == EnumParticleTypes.SPELL_WITCH.getParticleID() && !Config.isPotionParticles()) {
                return null;
            }
            if (p_174974_1_ == EnumParticleTypes.PORTAL.getParticleID() && !Config.isPortalParticles()) {
                return null;
            }
            if (p_174974_1_ == EnumParticleTypes.FLAME.getParticleID() && !Config.isAnimatedFlame()) {
                return null;
            }
            if (p_174974_1_ == EnumParticleTypes.REDSTONE.getParticleID() && !Config.isAnimatedRedstone()) {
                return null;
            }
            if (p_174974_1_ == EnumParticleTypes.DRIP_WATER.getParticleID() && !Config.isDrippingWaterLava()) {
                return null;
            } else if (p_174974_1_ == EnumParticleTypes.DRIP_LAVA.getParticleID() && !Config.isDrippingWaterLava()) {
                return null;
            } else if (p_174974_1_ == EnumParticleTypes.FIREWORKS_SPARK.getParticleID() && !Config.isFireworkParticles()) {
                return null;
            } else {
                if (!ignoreRange) {
                    double d4 = 1024.0;
                    if (p_174974_1_ == EnumParticleTypes.CRIT.getParticleID()) {
                        d4 = 38416.0;
                    }

                    if (d * d + d2 * d2 + d3 * d3 > d4) {
                        return null;
                    }

                    if (particleSetting > 1) {
                        return null;
                    }
                }

                EntityFX entity = this.mc.effectRenderer.spawnEffectParticle(p_174974_1_, p_174974_3_, p_174974_5_, p_174974_7_, p_174974_9_, p_174974_11_, p_174974_13_, p_174974_15_);
                if (entity == null) {
                    return null;
                } else {
                    if (p_174974_1_ == EnumParticleTypes.WATER_BUBBLE.getParticleID()) {
                        CustomColors.updateWaterFX(entity, this.theWorld, p_174974_3_, p_174974_5_, p_174974_7_, this.renderEnv);
                    }

                    if (p_174974_1_ == EnumParticleTypes.WATER_SPLASH.getParticleID()) {
                        CustomColors.updateWaterFX(entity, this.theWorld, p_174974_3_, p_174974_5_, p_174974_7_, this.renderEnv);
                    }

                    if (p_174974_1_ == EnumParticleTypes.WATER_DROP.getParticleID()) {
                        CustomColors.updateWaterFX(entity, this.theWorld, p_174974_3_, p_174974_5_, p_174974_7_, this.renderEnv);
                    }

                    if (p_174974_1_ == EnumParticleTypes.TOWN_AURA.getParticleID()) {
                        CustomColors.updateMyceliumFX(entity);
                    }

                    if (p_174974_1_ == EnumParticleTypes.PORTAL.getParticleID()) {
                        CustomColors.updatePortalFX(entity);
                    }

                    if (p_174974_1_ == EnumParticleTypes.REDSTONE.getParticleID()) {
                        CustomColors.updateReddustFX(entity, this.theWorld, p_174974_3_, p_174974_5_, p_174974_7_);
                    }

                    return entity;
                }
            }
        }
        return null;
    }

    @Override
    public void onEntityAdded(Entity entityIn) {
        RandomEntities.entityLoaded(entityIn, this.theWorld);
        if (Config.isDynamicLights()) {
            DynamicLights.entityAdded(entityIn, this);
        }
    }

    @Override
    public void onEntityRemoved(Entity entityIn) {
        RandomEntities.entityUnloaded(entityIn, this.theWorld);
        if (Config.isDynamicLights()) {
            DynamicLights.entityRemoved(entityIn, this);
        }
    }

    public void deleteAllDisplayLists() {
    }

    @Override
    public void broadcastSound(int p_180440_1_, BlockPos p_180440_2_, int p_180440_3_) {
        switch (p_180440_1_) {
            case 1013:
            case 1018:
                if (this.mc.getRenderViewEntity() != null) {
                    double d = p_180440_2_.getX() - this.mc.getRenderViewEntity().posX;
                    double d2 = p_180440_2_.getY() - this.mc.getRenderViewEntity().posY;
                    double d3 = p_180440_2_.getZ() - this.mc.getRenderViewEntity().posZ;
                    double dist = Math.sqrt(d * d + d2 * d2 + d3 * d3);
                    double x = this.mc.getRenderViewEntity().posX;
                    double y = this.mc.getRenderViewEntity().posY;
                    double z = this.mc.getRenderViewEntity().posZ;
                    if (dist > 0.0) {
                        x += d / dist * 2.0;
                        y += d2 / dist * 2.0;
                        z += d3 / dist * 2.0;
                    }

                    if (p_180440_1_ == 1013) {
                        this.theWorld.playSound(x, y, z, "mob.wither.spawn", 1.0F, 1.0F, false);
                    } else {
                        this.theWorld.playSound(x, y, z, "mob.enderdragon.end", 5.0F, 1.0F, false);
                    }
                }
        }
    }

    @Override
    public void playAuxSFX(EntityPlayer player, int sfxType, BlockPos blockPosIn, int p_180439_4_) {
        Random rand = this.theWorld.rand;
        switch (sfxType) {
            case 1000:
                this.theWorld.playSoundAtPos(blockPosIn, "random.click", 1.0F, 1.0F, false);
                break;
            case 1001:
                this.theWorld.playSoundAtPos(blockPosIn, "random.click", 1.0F, 1.2F, false);
                break;
            case 1002:
                this.theWorld.playSoundAtPos(blockPosIn, "random.bow", 1.0F, 1.2F, false);
                break;
            case 1003:
                this.theWorld.playSoundAtPos(blockPosIn, "random.door_open", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 1004:
                this.theWorld.playSoundAtPos(blockPosIn, "random.fizz", 0.5F, 2.6F + (rand.nextFloat() - rand.nextFloat()) * 0.8F, false);
                break;
            case 1005:
                if (Item.getItemById(p_180439_4_) instanceof ItemRecord) {
                    this.theWorld.playRecord(blockPosIn, "records." + ((ItemRecord)Item.getItemById(p_180439_4_)).recordName);
                } else {
                    this.theWorld.playRecord(blockPosIn, (String)null);
                }
                break;
            case 1006:
                this.theWorld.playSoundAtPos(blockPosIn, "random.door_close", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 1007:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.ghast.charge", 10.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1008:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.ghast.fireball", 10.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1009:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.ghast.fireball", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1010:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.wood", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1011:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.metal", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1012:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.woodbreak", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1014:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.wither.shoot", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1015:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.bat.takeoff", 0.05F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1016:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.infect", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1017:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.unfect", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1020:
                this.theWorld.playSoundAtPos(blockPosIn, "random.anvil_break", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 1021:
                this.theWorld.playSoundAtPos(blockPosIn, "random.anvil_use", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 1022:
                this.theWorld.playSoundAtPos(blockPosIn, "random.anvil_land", 0.3F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 2000:
                int n = p_180439_4_ % 3 - 1;
                int n2 = p_180439_4_ / 3 % 3 - 1;
                double d = blockPosIn.getX() + n * 0.6 + 0.5;
                double d2 = blockPosIn.getY() + 0.5;
                double d3 = blockPosIn.getZ() + n2 * 0.6 + 0.5;

                for (int i = 0; i < 10; i++) {
                    double d4 = rand.nextDouble() * 0.2 + 0.01;
                    double d5 = d + n * 0.01 + (rand.nextDouble() - 0.5) * n2 * 0.5;
                    double d6 = d2 + (rand.nextDouble() - 0.5) * 0.5;
                    double d7 = d3 + n2 * 0.01 + (rand.nextDouble() - 0.5) * n * 0.5;
                    double d8 = n * d4 + rand.nextGaussian() * 0.01;
                    double d9 = -0.03 + rand.nextGaussian() * 0.01;
                    double d10 = n2 * d4 + rand.nextGaussian() * 0.01;
                    this.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d5, d6, d7, d8, d9, d10);
                }

                return;
            case 2001:
                Block blockById = Block.getBlockById(p_180439_4_ & 4095);
                if (blockById.getMaterial() != Material.air) {
                    this.mc
                        .getSoundHandler()
                        .playSound(
                            new PositionedSoundRecord(
                                new ResourceLocation(blockById.stepSound.getBreakSound()),
                                (blockById.stepSound.getVolume() + 1.0F) / 2.0F,
                                blockById.stepSound.getFrequency() * 0.8F,
                                blockPosIn.getX() + 0.5F,
                                blockPosIn.getY() + 0.5F,
                                blockPosIn.getZ() + 0.5F
                            )
                        );
                }

                this.mc.effectRenderer.addBlockDestroyEffects(blockPosIn, blockById.getStateFromMeta(p_180439_4_ >> 12 & 0xFF));
                break;
            case 2002:
                double x = blockPosIn.getX();
                double y = blockPosIn.getY();
                double z = blockPosIn.getZ();

                for (int j = 0; j < 8; j++) {
                    this.spawnParticle(
                        EnumParticleTypes.ITEM_CRACK,
                        x,
                        y,
                        z,
                        rand.nextGaussian() * 0.15,
                        rand.nextDouble() * 0.2,
                        rand.nextGaussian() * 0.15,
                        Item.getIdFromItem(Items.potionitem),
                        p_180439_4_
                    );
                }

                int colorFromDamage = Items.potionitem.getColorFromDamage(p_180439_4_);
                float f = (colorFromDamage >> 16 & 0xFF) / 255.0F;
                float f2 = (colorFromDamage >> 8 & 0xFF) / 255.0F;
                float f3 = (colorFromDamage >> 0 & 0xFF) / 255.0F;
                EnumParticleTypes SPELL = EnumParticleTypes.SPELL;
                if (Items.potionitem.isEffectInstant(p_180439_4_)) {
                    SPELL = EnumParticleTypes.SPELL_INSTANT;
                }

                for (int k = 0; k < 100; k++) {
                    double d11 = rand.nextDouble() * 4.0;
                    double d12 = rand.nextDouble() * Math.PI * 2.0;
                    double d13 = Math.cos(d12) * d11;
                    double d14 = 0.01 + rand.nextDouble() * 0.5;
                    double d15 = Math.sin(d12) * d11;
                    EntityFX entity = this.spawnEntityFX(
                        SPELL.getParticleID(), SPELL.getShouldIgnoreRange(), x + d13 * 0.1, y + 0.3, z + d15 * 0.1, d13, d14, d15
                    );
                    if (entity != null) {
                        float f4 = 0.75F + rand.nextFloat() * 0.25F;
                        entity.setRBGColorF(f * f4, f2 * f4, f3 * f4);
                        entity.multiplyVelocity((float)d11);
                    }
                }

                this.theWorld.playSoundAtPos(blockPosIn, "game.potion.smash", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 2003:
                double d16 = blockPosIn.getX() + 0.5;
                double y2 = blockPosIn.getY();
                double d17 = blockPosIn.getZ() + 0.5;

                for (int idx = 0; idx < 8; idx++) {
                    this.spawnParticle(
                        EnumParticleTypes.ITEM_CRACK,
                        d16,
                        y2,
                        d17,
                        rand.nextGaussian() * 0.15,
                        rand.nextDouble() * 0.2,
                        rand.nextGaussian() * 0.15,
                        Item.getIdFromItem(Items.ender_eye)
                    );
                }

                for (double idx2 = 0.0; idx2 < Math.PI * 2; idx2 += Math.PI / 20) {
                    this.spawnParticle(
                        EnumParticleTypes.PORTAL,
                        d16 + Math.cos(idx2) * 5.0,
                        y2 - 0.4,
                        d17 + Math.sin(idx2) * 5.0,
                        Math.cos(idx2) * -5.0,
                        0.0,
                        Math.sin(idx2) * -5.0
                    );
                    this.spawnParticle(
                        EnumParticleTypes.PORTAL,
                        d16 + Math.cos(idx2) * 5.0,
                        y2 - 0.4,
                        d17 + Math.sin(idx2) * 5.0,
                        Math.cos(idx2) * -7.0,
                        0.0,
                        Math.sin(idx2) * -7.0
                    );
                }

                return;
            case 2004:
                for (int idx3 = 0; idx3 < 20; idx3++) {
                    double d18 = blockPosIn.getX() + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
                    double d19 = blockPosIn.getY() + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
                    double d20 = blockPosIn.getZ() + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
                    this.theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d18, d19, d20, 0.0, 0.0, 0.0, new int[0]);
                    this.theWorld.spawnParticle(EnumParticleTypes.FLAME, d18, d19, d20, 0.0, 0.0, 0.0, new int[0]);
                }

                return;
            case 2005:
                ItemDye.spawnBonemealParticles(this.theWorld, blockPosIn, p_180439_4_);
        }
    }

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
        if (progress >= 0 && progress < 10) {
            DestroyBlockProgress destroyBlockProgress = this.damagedBlocks.get(breakerId);
            if (destroyBlockProgress == null
                || destroyBlockProgress.getPosition().getX() != pos.getX()
                || destroyBlockProgress.getPosition().getY() != pos.getY()
                || destroyBlockProgress.getPosition().getZ() != pos.getZ()) {
                destroyBlockProgress = new DestroyBlockProgress(breakerId, pos);
                this.damagedBlocks.put(breakerId, destroyBlockProgress);
            }

            destroyBlockProgress.setPartialBlockDamage(progress);
            destroyBlockProgress.setCloudUpdateTick(this.cloudTickCounter);
        } else {
            this.damagedBlocks.remove(breakerId);
        }
    }

    public void setDisplayListEntitiesDirty() {
        this.displayListEntitiesDirty = true;
    }

    public boolean hasNoChunkUpdates() {
        return this.chunksToUpdate.isEmpty() && this.renderDispatcher.hasChunkUpdates();
    }

    public void resetClouds() {
        if (this.cloudRenderer != null) {
            this.cloudRenderer.reset();
        }
    }

    public int getCountRenderers() {
        return this.viewFrustum.renderChunks.length;
    }

    public int getCountActiveRenderers() {
        return this.renderInfos.size();
    }

    public int getCountEntitiesRendered() {
        return this.countEntitiesRendered;
    }

    public int getCountTileEntitiesRendered() {
        return this.countTileEntitiesRendered;
    }

    public int getCountLoadedChunks() {
        if (this.theWorld == null) {
            return 0;
        }
        IChunkProvider chunkProvider = this.theWorld.getChunkProvider();
        if (chunkProvider == null) {
            return 0;
        }
        if (chunkProvider != this.worldChunkProvider) {
            this.worldChunkProvider = chunkProvider;
            this.worldChunkProviderMap = (LongHashMap)Reflector.getFieldValue(chunkProvider, Reflector.ChunkProviderClient_chunkMapping);
        }

        return this.worldChunkProviderMap == null ? 0 : this.worldChunkProviderMap.getNumHashElements();
    }

    public int getCountChunksToUpdate() {
        return this.chunksToUpdate.size();
    }

    public RenderChunk getRenderChunk(BlockPos p_getRenderChunk_1_) {
        return this.viewFrustum.getRenderChunk(p_getRenderChunk_1_);
    }

    public WorldClient getWorld() {
        return this.theWorld;
    }

    private void clearRenderInfos() {
        if (renderEntitiesCounter > 0) {
            this.renderInfos = new ArrayList<>(this.renderInfos.size() + 16);
            this.renderInfosEntities = new ArrayList(this.renderInfosEntities.size() + 16);
            this.renderInfosTileEntities = new ArrayList(this.renderInfosTileEntities.size() + 16);
        } else {
            this.renderInfos.clear();
            this.renderInfosEntities.clear();
            this.renderInfosTileEntities.clear();
        }
    }

    public void onPlayerPositionSet() {
        if (this.firstWorldLoad) {
            this.loadRenderers();
            this.firstWorldLoad = false;
        }
    }

    public void pauseChunkUpdates() {
        if (this.renderDispatcher != null) {
            this.renderDispatcher.pauseChunkUpdates();
        }
    }

    public void resumeChunkUpdates() {
        if (this.renderDispatcher != null) {
            this.renderDispatcher.resumeChunkUpdates();
        }
    }

    public void func_181023_a(Collection<TileEntity> p_181023_1_, Collection<TileEntity> p_181023_2_) {
        synchronized (this.field_181024_n) {
            this.field_181024_n.removeAll(p_181023_1_);
            this.field_181024_n.addAll(p_181023_2_);
        }
    }

    public static class ContainerLocalRenderInformation {
        final RenderChunk renderChunk;
        EnumFacing facing;
        int setFacing;

        public ContainerLocalRenderInformation(RenderChunk p_i2_1_, EnumFacing p_i2_2_, int p_i2_3_) {
            this.renderChunk = p_i2_1_;
            this.facing = p_i2_2_;
            this.setFacing = p_i2_3_;
        }

        public void setFacingBit(byte p_setFacingBit_1_, EnumFacing p_setFacingBit_2_) {
            this.setFacing = this.setFacing | p_setFacingBit_1_ | 1 << p_setFacingBit_2_.ordinal();
        }

        public boolean isFacingBit(EnumFacing p_isFacingBit_1_) {
            return (this.setFacing & 1 << p_isFacingBit_1_.ordinal()) > 0;
        }

        void initialize(EnumFacing p_initialize_1_, int p_initialize_2_) {
            this.facing = p_initialize_1_;
            this.setFacing = p_initialize_2_;
        }
    }
}
