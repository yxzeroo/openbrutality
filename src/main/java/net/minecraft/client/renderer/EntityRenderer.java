package net.minecraft.client.renderer;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.src.Config;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.CustomColors;
import net.optifine.CustomItems;
import net.optifine.GlErrors;
import net.optifine.Lagometer;
import net.optifine.RandomEntities;
import net.optifine.gui.GuiChatOF;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;
import net.optifine.util.TextureUtils;
import net.optifine.util.TimedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brutality.Brutality;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.event.impl.render.GetPartialTicksEvent;
import org.brutality.event.impl.render.RenderEvent;
import org.brutality.gui.ChangelogScreen;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.misc.GhostHand;
import org.brutality.module.impl.movement.FreeCam;
import org.brutality.module.impl.visual.AspectRatio;
import org.brutality.module.impl.visual.MotionBlur;
import org.brutality.proxy.ProxyBanToast;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.player.GetPitchHelper;
import org.brutality.utils.player.PlayerHelper13;
import org.brutality.utils.render.FontResourceLoader;
import org.brutality.utils.render.GlStateGuard;
import org.brutality.utils.rotation.OnJumpHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;
import org.lwjgl.opengl.GL12;

public class EntityRenderer implements IResourceManagerReloadListener {
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation locationRainPng = new ResourceLocation("textures/environment/rain.png");
    private static final ResourceLocation locationSnowPng = new ResourceLocation("textures/environment/snow.png");
    public static boolean anaglyphEnable;
    public static int anaglyphField;
    Minecraft mc;
    private final IResourceManager resourceManager;
    private Random random = new Random();
    private float farPlaneDistance;
    public ItemRenderer itemRenderer;
    private final MapItemRenderer theMapItemRenderer;
    private int rendererUpdateCount;
    private Entity pointedEntity;
    private MouseFilter mouseFilterXAxis = new MouseFilter();
    private MouseFilter mouseFilterYAxis = new MouseFilter();
    public float thirdPersonDistance = 4.0F;
    private float thirdPersonDistanceTemp = 4.0F;
    private float smoothCamYaw;
    private float smoothCamPitch;
    private float smoothCamFilterX;
    private float smoothCamFilterY;
    private float smoothCamPartialTicks;
    private float fovModifierHand;
    private float fovModifierHandPrev;
    private float bossColorModifier;
    private float bossColorModifierPrev;
    private boolean cloudFog;
    private boolean renderHand = true;
    private boolean drawBlockOutline = true;
    private long prevFrameTime = Minecraft.getSystemTime();
    private long renderEndNanoTime;
    private final DynamicTexture lightmapTexture;
    private final int[] lightmapColors;
    private final ResourceLocation locationLightMap;
    private boolean lightmapUpdateNeeded;
    private float torchFlickerX;
    private float torchFlickerDX;
    private int rainSoundCounter;
    private float[] rainXCoords = new float[1024];
    private float[] rainYCoords = new float[1024];
    private FloatBuffer fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
    public float fogColorRed;
    public float fogColorGreen;
    public float fogColorBlue;
    private float fogColor2;
    private float fogColor1;
    private int debugViewDirection = 0;
    private boolean debugView = false;
    private double cameraZoom = 1.0;
    private double cameraYaw;
    private double cameraPitch;
    private ShaderGroup theShaderGroup;
    private static final ResourceLocation[] shaderResourceLocations = new ResourceLocation[]{
        new ResourceLocation("shaders/post/notch.json"),
        new ResourceLocation("shaders/post/fxaa.json"),
        new ResourceLocation("shaders/post/art.json"),
        new ResourceLocation("shaders/post/bumpy.json"),
        new ResourceLocation("shaders/post/blobs2.json"),
        new ResourceLocation("shaders/post/pencil.json"),
        new ResourceLocation("shaders/post/color_convolve.json"),
        new ResourceLocation("shaders/post/deconverge.json"),
        new ResourceLocation("shaders/post/flip.json"),
        new ResourceLocation("shaders/post/invert.json"),
        new ResourceLocation("shaders/post/ntsc.json"),
        new ResourceLocation("shaders/post/outline.json"),
        new ResourceLocation("shaders/post/phosphor.json"),
        new ResourceLocation("shaders/post/scan_pincushion.json"),
        new ResourceLocation("shaders/post/sobel.json"),
        new ResourceLocation("shaders/post/bits.json"),
        new ResourceLocation("shaders/post/desaturate.json"),
        new ResourceLocation("shaders/post/green.json"),
        new ResourceLocation("shaders/post/blur.json"),
        new ResourceLocation("shaders/post/wobble.json"),
        new ResourceLocation("shaders/post/blobs.json"),
        new ResourceLocation("shaders/post/antialias.json"),
        new ResourceLocation("shaders/post/creeper.json"),
        new ResourceLocation("shaders/post/spider.json")
    };
    public static final int shaderCount = shaderResourceLocations.length;
    private int shaderIndex;
    private boolean useShader;
    public int frameCount;
    private boolean initialized = false;
    private World updatedWorld = null;
    private boolean showDebugInfo = false;
    public boolean fogStandard = false;
    private float clipDistance = 128.0F;
    private long lastServerTime = 0L;
    private int lastServerTicks = 0;
    private int serverWaitTime = 0;
    private int serverWaitTimeCurrent = 0;
    private float avgServerTimeDiff = 0.0F;
    private float avgServerTickDiff = 0.0F;
    private ShaderGroup[] fxaaShaders = new ShaderGroup[10];
    private boolean loadVisibleChunks = false;

    public EntityRenderer(Minecraft mcIn, IResourceManager resourceManagerIn) {
        this.shaderIndex = shaderCount;
        this.useShader = false;
        this.frameCount = 0;
        this.mc = mcIn;
        this.resourceManager = resourceManagerIn;
        this.itemRenderer = mcIn.getItemRenderer();
        this.theMapItemRenderer = new MapItemRenderer(mcIn.getTextureManager());
        this.lightmapTexture = new DynamicTexture(16, 16);
        this.locationLightMap = mcIn.getTextureManager().getDynamicTextureLocation("lightMap", this.lightmapTexture);
        this.lightmapColors = this.lightmapTexture.getTextureData();
        this.theShaderGroup = null;

        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                float f = j - 16;
                float f2 = i - 16;
                float f3 = MathHelper.sqrt_float(f * f + f2 * f2);
                this.rainXCoords[i << 5 | j] = -f2 / f3;
                this.rainYCoords[i << 5 | j] = f / f3;
            }
        }
    }

    public boolean isShaderActive() {
        return OpenGlHelper.shadersSupported && this.theShaderGroup != null;
    }

    public void func_181022_b() {
        if (this.theShaderGroup != null) {
            this.theShaderGroup.deleteShaderGroup();
        }

        this.theShaderGroup = null;
        this.shaderIndex = shaderCount;
    }

    public void switchUseShader() {
        this.useShader = !this.useShader;
    }

    public void loadEntityShader(Entity entityIn) {
        if (OpenGlHelper.shadersSupported) {
            if (this.theShaderGroup != null) {
                this.theShaderGroup.deleteShaderGroup();
            }

            this.theShaderGroup = null;
            if (entityIn instanceof EntityCreeper) {
                this.loadShader(new ResourceLocation("shaders/post/creeper.json"));
            } else if (entityIn instanceof EntitySpider) {
                this.loadShader(new ResourceLocation("shaders/post/spider.json"));
            } else if (entityIn instanceof EntityEnderman) {
                this.loadShader(new ResourceLocation("shaders/post/invert.json"));
            } else if (Reflector.ForgeHooksClient_loadEntityShader.exists()) {
                Reflector.call(Reflector.ForgeHooksClient_loadEntityShader, entityIn, this);
            }
        }
    }

    public void activateNextShader() {
        if (OpenGlHelper.shadersSupported && this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            if (this.theShaderGroup != null) {
                this.theShaderGroup.deleteShaderGroup();
            }

            this.shaderIndex = (this.shaderIndex + 1) % (shaderResourceLocations.length + 1);
            if (this.shaderIndex != shaderCount) {
                this.loadShader(shaderResourceLocations[this.shaderIndex]);
            } else {
                this.theShaderGroup = null;
            }
        }
    }

    private void loadShader(ResourceLocation resourceLocationIn) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            try {
                this.theShaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), resourceLocationIn);
                this.theShaderGroup.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
                this.useShader = true;
            } catch (IOException e) {
                logger.warn("Failed to load shader: " + resourceLocationIn, (Throwable)e);
                this.shaderIndex = shaderCount;
                this.useShader = false;
            } catch (JsonSyntaxException e2) {
                logger.warn("Failed to load shader: " + resourceLocationIn, (Throwable)e2);
                this.shaderIndex = shaderCount;
                this.useShader = false;
            }
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        if (this.theShaderGroup != null) {
            this.theShaderGroup.deleteShaderGroup();
        }

        this.theShaderGroup = null;
        if (this.shaderIndex != shaderCount) {
            this.loadShader(shaderResourceLocations[this.shaderIndex]);
        } else {
            this.loadEntityShader(this.mc.getRenderViewEntity());
        }
    }

    public void updateRenderer() {
        if (OpenGlHelper.shadersSupported && ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
            ShaderLinkHelper.setNewStaticShaderLinkHelper();
        }

        this.updateFovModifierHand();
        this.updateTorchFlicker();
        this.fogColor2 = this.fogColor1;
        this.thirdPersonDistanceTemp = this.thirdPersonDistance;
        if (this.mc.gameSettings.smoothCamera) {
            float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f2 = f * f * f * 8.0F;
            this.smoothCamFilterX = this.mouseFilterXAxis.smooth(this.smoothCamYaw, 0.05F * f2);
            this.smoothCamFilterY = this.mouseFilterYAxis.smooth(this.smoothCamPitch, 0.05F * f2);
            this.smoothCamPartialTicks = 0.0F;
            this.smoothCamYaw = 0.0F;
            this.smoothCamPitch = 0.0F;
        } else {
            this.smoothCamFilterX = 0.0F;
            this.smoothCamFilterY = 0.0F;
            this.mouseFilterXAxis.reset();
            this.mouseFilterYAxis.reset();
        }

        if (this.mc.getRenderViewEntity() == null) {
            this.mc.setRenderViewEntity(this.mc.thePlayer);
        }

        Entity renderViewEntity = this.mc.getRenderViewEntity();
        double x = renderViewEntity.posX;
        double d = renderViewEntity.posY + renderViewEntity.getEyeHeight();
        double z = renderViewEntity.posZ;
        float lightBrightness = this.mc.theWorld.getLightBrightness(new BlockPos(x, d, z));
        float f3 = this.mc.gameSettings.renderDistanceChunks / 16.0F;
        f3 = MathHelper.clamp_float(f3, 0.0F, 1.0F);
        float f4 = lightBrightness * (1.0F - f3) + f3;
        this.fogColor1 = this.fogColor1 + (f4 - this.fogColor1) * 0.1F;
        this.rendererUpdateCount++;
        this.itemRenderer.updateEquippedItem();
        this.addRainParticles();
        this.bossColorModifierPrev = this.bossColorModifier;
        if (BossStatus.hasColorModifier) {
            this.bossColorModifier += 0.05F;
            if (this.bossColorModifier > 1.0F) {
                this.bossColorModifier = 1.0F;
            }

            BossStatus.hasColorModifier = false;
        } else if (this.bossColorModifier > 0.0F) {
            this.bossColorModifier -= 0.0125F;
        }
    }

    public ShaderGroup getShaderGroup() {
        return this.theShaderGroup;
    }

    public void updateShaderGroupSize(int width, int height) {
        if (OpenGlHelper.shadersSupported) {
            if (this.theShaderGroup != null) {
                this.theShaderGroup.createBindFramebuffers(width, height);
            }

            this.mc.renderGlobal.createBindEntityOutlineFbs(width, height);
        }
    }

    public void getMouseOver(float partialTicks) {
        Entity renderViewEntity = this.mc.getRenderViewEntity();
        if (renderViewEntity != null && this.mc.theWorld != null) {
            boolean flag = OnJumpHelper.checkState() && renderViewEntity == this.mc.thePlayer;
            this.mc.mcProfiler.startSection("pick");
            this.mc.pointedEntity = null;
            double blockReachDistance = this.mc.playerController.getBlockReachDistance();
            this.mc.objectMouseOver = flag ? OnJumpHelper.rayTrace(OnJumpHelper.getReach(), partialTicks) : renderViewEntity.rayTrace(blockReachDistance, partialTicks);
            double d = blockReachDistance;
            Vec3 positionEyes = renderViewEntity.getPositionEyes(partialTicks);
            boolean flag2 = false;
            if (this.mc.playerController.extendedReach()) {
                blockReachDistance = 6.0;
                d = 6.0;
            } else if (blockReachDistance > OnJumpHelper.getReach()) {
                flag2 = true;
            }

            if (this.mc.objectMouseOver != null) {
                d = this.mc.objectMouseOver.hitVec.distanceTo(positionEyes);
            }

            Vec3 look = renderViewEntity.getLook(partialTicks);
            if (flag) {
                Vec3 look2 = OnJumpHelper.getLook(partialTicks);
                if (look2 != null) {
                    look = look2;
                }
            }

            Vec3 vec = positionEyes.addVector(look.xCoord * blockReachDistance, look.yCoord * blockReachDistance, look.zCoord * blockReachDistance);
            this.pointedEntity = null;
            Vec3 vec2 = null;
            float f = 1.0F;
            List entitiesInAABBexcluding = this.mc
                .theWorld
                .getEntitiesInAABBexcluding(
                    renderViewEntity,
                    renderViewEntity.getEntityBoundingBox().addCoord(look.xCoord * blockReachDistance, look.yCoord * blockReachDistance, look.zCoord * blockReachDistance).expand(f, f, f),
                    Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
                        public boolean apply(Entity p_apply_1_) {
                            return p_apply_1_.canBeCollidedWith();
                        }
                    })
                );
            double d2 = d;

            for (int i = 0; i < entitiesInAABBexcluding.size(); i++) {
                Entity entity = (Entity)entitiesInAABBexcluding.get(i);
                float collisionBorderSize = entity.getCollisionBorderSize();
                AxisAlignedBB box = entity.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                MovingObjectPosition hit = box.calculateIntercept(positionEyes, vec);
                if (box.isVecInside(positionEyes)) {
                    if (d2 >= 0.0) {
                        this.pointedEntity = entity;
                        vec2 = hit == null ? positionEyes : hit.hitVec;
                        d2 = 0.0;
                    }
                } else if (hit != null) {
                    double d3 = positionEyes.distanceTo(hit.hitVec);
                    if (d3 < d2 || d2 == 0.0) {
                        boolean flag3 = false;
                        if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                            flag3 = Reflector.callBoolean(entity, Reflector.ForgeEntity_canRiderInteract);
                        }

                        if (flag3 || entity != renderViewEntity.ridingEntity) {
                            this.pointedEntity = entity;
                            vec2 = hit.hitVec;
                            d2 = d3;
                        } else if (d2 == 0.0) {
                            this.pointedEntity = entity;
                            vec2 = hit.hitVec;
                        }
                    }
                }
            }

            if (this.pointedEntity != null && flag2 && positionEyes.distanceTo(vec2) > 3.0) {
                this.pointedEntity = null;
                this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectType.MISS, vec2, (EnumFacing)null, new BlockPos(vec2));
            }

            if (this.pointedEntity != null && (d2 < d || this.mc.objectMouseOver == null)) {
                this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, vec2);
                if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
                    this.mc.pointedEntity = this.pointedEntity;
                }
            }

            if (KillAura.instance != null && KillAura.instance.checkState2()) {
                KillAura.instance.handleFloat(partialTicks);
            }

            GhostHand instance = GhostHand.getInstance();
            if (instance != null && instance.checkState()) {
                this.pointedEntity = null;
                this.mc.pointedEntity = null;
            }

            this.mc.mcProfiler.endSection();
        }
    }

    private void updateFovModifierHand() {
        float f = 1.0F;
        if (this.mc.getRenderViewEntity() instanceof AbstractClientPlayer) {
            AbstractClientPlayer renderViewEntity = (AbstractClientPlayer)this.mc.getRenderViewEntity();
            f = renderViewEntity.getFovModifier();
        }

        this.fovModifierHandPrev = this.fovModifierHand;
        this.fovModifierHand = this.fovModifierHand + (f - this.fovModifierHand) * 0.5F;
        if (this.fovModifierHand > 1.5F) {
            this.fovModifierHand = 1.5F;
        }

        if (this.fovModifierHand < 0.1F) {
            this.fovModifierHand = 0.1F;
        }
    }

    private float getFOVModifier(float partialTicks, boolean p_78481_2_) {
        if (this.debugView) {
            return 90.0F;
        }
        Entity renderViewEntity = this.mc.getRenderViewEntity();
        float f = 70.0F;
        if (p_78481_2_) {
            f = this.mc.gameSettings.fovSetting;
            if (Config.isDynamicFov()) {
                f *= this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * partialTicks;
            }
        }

        boolean flag = false;
        if (this.mc.currentScreen == null) {
            GameSettings settings = this.mc.gameSettings;
            flag = GameSettings.isKeyDown(this.mc.gameSettings.ofKeyBindZoom);
        }

        if (flag) {
            if (!Config.zoomMode) {
                Config.zoomMode = true;
                this.mc.gameSettings.smoothCamera = true;
                this.mc.renderGlobal.displayListEntitiesDirty = true;
            }

            if (Config.zoomMode) {
                f /= 4.0F;
            }
        } else if (Config.zoomMode) {
            Config.zoomMode = false;
            this.mc.gameSettings.smoothCamera = false;
            this.mouseFilterXAxis = new MouseFilter();
            this.mouseFilterYAxis = new MouseFilter();
            this.mc.renderGlobal.displayListEntitiesDirty = true;
        }

        if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).getHealth() <= 0.0F) {
            float f2 = ((EntityLivingBase)renderViewEntity).deathTime + partialTicks;
            f /= (1.0F - 500.0F / (f2 + 500.0F)) * 2.0F + 1.0F;
        }

        Block blockAtEntityViewpoint = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, renderViewEntity, partialTicks);
        if (blockAtEntityViewpoint.getMaterial() == Material.water) {
            f = f * 60.0F / 70.0F;
        }

        return Reflector.ForgeHooksClient_getFOVModifier.exists()
            ? Reflector.callFloat(Reflector.ForgeHooksClient_getFOVModifier, this, renderViewEntity, blockAtEntityViewpoint, partialTicks, f)
            : f;
    }

    private void hurtCameraEffect(float partialTicks) {
        if (FreeCam.entityOtherPlayerMP == null) {
            RenderEvent renderEvent = new RenderEvent();
            renderEvent.process();
            if (!renderEvent.cancel2()) {
                if (this.mc.getRenderViewEntity() instanceof EntityLivingBase) {
                    EntityLivingBase renderViewEntity = (EntityLivingBase)this.mc.getRenderViewEntity();
                    float f = renderViewEntity.hurtTime - partialTicks;
                    if (renderViewEntity.getHealth() <= 0.0F) {
                        float f2 = renderViewEntity.deathTime + partialTicks;
                        GlStateManager.rotate(renderEvent.getFactor2() - renderEvent.getFactor3() / (f2 + renderEvent.getFactor4()), 0.0F, 0.0F, 1.0F);
                    }

                    if (f < 0.0F) {
                        return;
                    }

                    f /= renderViewEntity.maxHurtTime;
                    f = MathHelper.sin(f * f * f * f * (float) Math.PI) * renderEvent.getFactor();
                    float attackedAtYaw = renderViewEntity.attackedAtYaw;
                    GlStateManager.rotate(-attackedAtYaw, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(-f * renderEvent.getFactor5(), 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(attackedAtYaw, 0.0F, 1.0F, 0.0F);
                }
            }
        }
    }

    private void setupViewBobbing(float partialTicks) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            EntityPlayer renderViewEntity = (EntityPlayer)this.mc.getRenderViewEntity();
            float f = renderViewEntity.distanceWalkedModified - renderViewEntity.prevDistanceWalkedModified;
            float f2 = -(renderViewEntity.distanceWalkedModified + f * partialTicks);
            float f3 = renderViewEntity.prevCameraYaw + (renderViewEntity.cameraYaw - renderViewEntity.prevCameraYaw) * partialTicks;
            float f4 = renderViewEntity.prevCameraPitch + (renderViewEntity.cameraPitch - renderViewEntity.prevCameraPitch) * partialTicks;
            GlStateManager.translate(MathHelper.sin(f2 * (float) Math.PI) * f3 * 0.5F, -Math.abs(MathHelper.cos(f2 * (float) Math.PI) * f3), 0.0F);
            GlStateManager.rotate(MathHelper.sin(f2 * (float) Math.PI) * f3 * 3.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(Math.abs(MathHelper.cos(f2 * (float) Math.PI - 0.2F) * f3) * 5.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(f4, 1.0F, 0.0F, 0.0F);
        }
    }

    public void orientCamera(float partialTicks) {
        Entity renderViewEntity = this.mc.getRenderViewEntity();
        float f = PlayerHelper13.computeFloat2(renderViewEntity, renderViewEntity.rotationYaw);
        float f2 = PlayerHelper13.computeFloat(renderViewEntity, renderViewEntity.rotationPitch);
        float f3 = PlayerHelper13.computeFloat2(renderViewEntity, renderViewEntity.prevRotationYaw + (renderViewEntity.rotationYaw - renderViewEntity.prevRotationYaw) * partialTicks);
        float f4 = PlayerHelper13.computeFloat(renderViewEntity, renderViewEntity.prevRotationPitch + (renderViewEntity.rotationPitch - renderViewEntity.prevRotationPitch) * partialTicks);
        float eyeHeight = renderViewEntity.getEyeHeight();
        double d = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * partialTicks;
        double d2 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * partialTicks + eyeHeight;
        double d3 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * partialTicks;
        if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPlayerSleeping()) {
            eyeHeight = (float)(eyeHeight + 1.0);
            GlStateManager.translate(0.0F, 0.3F, 0.0F);
            if (!this.mc.gameSettings.debugCamEnable) {
                BlockPos pos = new BlockPos(renderViewEntity);
                IBlockState blockState = this.mc.theWorld.getBlockState(pos);
                Block block = blockState.getBlock();
                if (Reflector.ForgeHooksClient_orientBedCamera.exists()) {
                    Reflector.callVoid(Reflector.ForgeHooksClient_orientBedCamera, this.mc.theWorld, pos, blockState, renderViewEntity);
                } else if (block == Blocks.bed) {
                    int horizontalIndex = blockState.getValue(BlockBed.FACING).getHorizontalIndex();
                    GlStateManager.rotate(horizontalIndex * 90, 0.0F, 1.0F, 0.0F);
                }

                GlStateManager.rotate(renderViewEntity.prevRotationYaw + (renderViewEntity.rotationYaw - renderViewEntity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, -1.0F, 0.0F);
                GlStateManager.rotate(renderViewEntity.prevRotationPitch + (renderViewEntity.rotationPitch - renderViewEntity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
            }
        } else if (this.mc.gameSettings.thirdPersonView > 0) {
            double d4 = this.thirdPersonDistanceTemp + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * partialTicks;
            if (this.mc.gameSettings.debugCamEnable) {
                GlStateManager.translate(0.0F, 0.0F, (float)(-d4));
            } else {
                float f5 = f2;
                if (this.mc.gameSettings.thirdPersonView == 2) {
                    f5 = f2 + 180.0F;
                }

                double d5 = -MathHelper.sin(f / 180.0F * (float) Math.PI) * MathHelper.cos(f5 / 180.0F * (float) Math.PI) * d4;
                double d6 = MathHelper.cos(f / 180.0F * (float) Math.PI) * MathHelper.cos(f5 / 180.0F * (float) Math.PI) * d4;
                double d7 = -MathHelper.sin(f5 / 180.0F * (float) Math.PI) * d4;

                for (int i = 0; i < 8; i++) {
                    float f6 = (i & 1) * 2 - 1;
                    float f7 = (i >> 1 & 1) * 2 - 1;
                    float f8 = (i >> 2 & 1) * 2 - 1;
                    f6 *= 0.1F;
                    f7 *= 0.1F;
                    f8 *= 0.1F;
                    MovingObjectPosition hit = this.mc
                        .theWorld
                        .rayTraceBlocks(
                            new Vec3(d + f6, d2 + f7, d3 + f8),
                            new Vec3(d - d5 + f6 + f8, d2 - d7 + f7, d3 - d6 + f8)
                        );
                    if (hit != null) {
                        double d8 = hit.hitVec.distanceTo(new Vec3(d, d2, d3));
                        if (d8 < d4) {
                            d4 = d8;
                        }
                    }
                }

                if (this.mc.gameSettings.thirdPersonView == 2) {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                GlStateManager.rotate(f2 - f5, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(f - f, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, (float)(-d4));
                GlStateManager.rotate(f - f, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(f5 - f2, 1.0F, 0.0F, 0.0F);
            }
        } else {
            GlStateManager.translate(0.0F, 0.0F, -0.1F);
        }

        if (Reflector.EntityViewRenderEvent_CameraSetup_Constructor.exists()) {
            if (!this.mc.gameSettings.debugCamEnable) {
                float f9 = f3 + 180.0F;
                float f10 = 0.0F;
                if (renderViewEntity instanceof EntityAnimal entity) {
                    f9 = entity.prevRotationYawHead + (entity.rotationYawHead - entity.prevRotationYawHead) * partialTicks + 180.0F;
                }

                Block blockAtEntityViewpoint = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, renderViewEntity, partialTicks);
                Object obj = Reflector.newInstance(Reflector.EntityViewRenderEvent_CameraSetup_Constructor, this, renderViewEntity, blockAtEntityViewpoint, partialTicks, f9, f4, f10);
                Reflector.postForgeBusEvent(obj);
                f10 = Reflector.getFieldValueFloat(obj, Reflector.EntityViewRenderEvent_CameraSetup_roll, f10);
                float fieldValueFloat = Reflector.getFieldValueFloat(obj, Reflector.EntityViewRenderEvent_CameraSetup_pitch, f4);
                f9 = Reflector.getFieldValueFloat(obj, Reflector.EntityViewRenderEvent_CameraSetup_yaw, f9);
                GlStateManager.rotate(f10, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(fieldValueFloat, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(f9, 0.0F, 1.0F, 0.0F);
            }
        } else if (!this.mc.gameSettings.debugCamEnable) {
            if (GetPitchHelper.flag) {
                GlStateManager.rotate(GetPitchHelper.cameraPitch, 1.0F, 0.0F, 0.0F);
            } else {
                GlStateManager.rotate(f4, 1.0F, 0.0F, 0.0F);
            }

            if (renderViewEntity instanceof EntityAnimal entity2) {
                GlStateManager.rotate(entity2.prevRotationYawHead + (entity2.rotationYawHead - entity2.prevRotationYawHead) * partialTicks + 180.0F, 0.0F, 1.0F, 0.0F);
            } else if (GetPitchHelper.flag) {
                GlStateManager.rotate(GetPitchHelper.cameraYaw, 0.0F, 1.0F, 0.0F);
            } else {
                GlStateManager.rotate(f3 + 180.0F, 0.0F, 1.0F, 0.0F);
            }
        }

        GlStateManager.translate(0.0F, -eyeHeight, 0.0F);
        d = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * partialTicks;
        d2 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * partialTicks + eyeHeight;
        d3 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * partialTicks;
        this.cloudFog = this.mc.renderGlobal.hasCloudFog(d, d2, d3, partialTicks);
    }

    public void setupCameraTransform(float partialTicks, int pass) {
        this.farPlaneDistance = this.mc.gameSettings.renderDistanceChunks * 16;
        if (Config.isFogFancy()) {
            this.farPlaneDistance *= 0.95F;
        }

        if (Config.isFogFast()) {
            this.farPlaneDistance *= 0.83F;
        }

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        float f = 0.07F;
        if (this.mc.gameSettings.anaglyph) {
            GlStateManager.translate(-(pass * 2 - 1) * f, 0.0F, 0.0F);
        }

        this.clipDistance = this.farPlaneDistance * 2.0F;
        if (this.clipDistance < 173.0F) {
            this.clipDistance = 173.0F;
        }

        if (this.cameraZoom != 1.0) {
            GlStateManager.translate((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
            GlStateManager.scale(this.cameraZoom, this.cameraZoom, 1.0);
        }

        Project.gluPerspective(
            this.getFOVModifier(partialTicks, true), AspectRatio.computeFloat((float)this.mc.displayWidth / this.mc.displayHeight), 0.05F, this.clipDistance
        );
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        if (this.mc.gameSettings.anaglyph) {
            GlStateManager.translate((pass * 2 - 1) * 0.1F, 0.0F, 0.0F);
        }

        this.hurtCameraEffect(partialTicks);
        if (this.mc.gameSettings.viewBobbing) {
            this.setupViewBobbing(partialTicks);
        }

        float f2 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;
        if (f2 > 0.0F) {
            int n = 20;
            if (this.mc.thePlayer.isPotionActive(Potion.confusion)) {
                n = 7;
            }

            float f3 = 5.0F / (f2 * f2 + 5.0F) - f2 * 0.04F;
            f3 *= f3;
            GlStateManager.rotate((this.rendererUpdateCount + partialTicks) * n, 0.0F, 1.0F, 1.0F);
            GlStateManager.scale(1.0F / f3, 1.0F, 1.0F);
            GlStateManager.rotate(-(this.rendererUpdateCount + partialTicks) * n, 0.0F, 1.0F, 1.0F);
        }

        this.orientCamera(partialTicks);
        if (this.debugView) {
            switch (this.debugViewDirection) {
                case 0:
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case 1:
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case 2:
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case 3:
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                    break;
                case 4:
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }
        }
    }

    private void renderHand(float partialTicks, int xOffset) {
        this.renderHand(partialTicks, xOffset, true, true, false);
    }

    public void renderHand(float p_renderHand_1_, int p_renderHand_2_, boolean p_renderHand_3_, boolean p_renderHand_4_, boolean p_renderHand_5_) {
        if (FreeCam.entityOtherPlayerMP == null) {
            if (!this.debugView) {
                GlStateManager.matrixMode(GL11.GL_PROJECTION);
                GlStateManager.loadIdentity();
                float f = 0.07F;
                if (this.mc.gameSettings.anaglyph) {
                    GlStateManager.translate(-(p_renderHand_2_ * 2 - 1) * f, 0.0F, 0.0F);
                }

                if (Config.isShaders()) {
                    Shaders.applyHandDepth();
                }

                Project.gluPerspective(
                    this.getFOVModifier(p_renderHand_1_, false),
                    AspectRatio.computeFloat((float)this.mc.displayWidth / this.mc.displayHeight),
                    0.05F,
                    this.farPlaneDistance * 2.0F
                );
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                GlStateManager.loadIdentity();
                if (this.mc.gameSettings.anaglyph) {
                    GlStateManager.translate((p_renderHand_2_ * 2 - 1) * 0.1F, 0.0F, 0.0F);
                }

                boolean flag = false;
                if (p_renderHand_3_) {
                    GlStateManager.pushMatrix();
                    this.hurtCameraEffect(p_renderHand_1_);
                    if (this.mc.gameSettings.viewBobbing) {
                        this.setupViewBobbing(p_renderHand_1_);
                    }

                    flag = this.mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping();
                    boolean flag2 = !ReflectorForge.renderFirstPersonHand(this.mc.renderGlobal, p_renderHand_1_, p_renderHand_2_);
                    if (flag2 && this.mc.gameSettings.thirdPersonView == 0 && !flag && !this.mc.gameSettings.hideGUI && !this.mc.playerController.isSpectator()) {
                        this.enableLightmap();
                        if (Config.isShaders()) {
                            ShadersRender.renderItemFP(this.itemRenderer, p_renderHand_1_, p_renderHand_5_);
                        } else {
                            this.itemRenderer.renderItemInFirstPerson(p_renderHand_1_);
                        }

                        this.disableLightmap();
                    }

                    GlStateManager.popMatrix();
                }

                if (!p_renderHand_4_) {
                    return;
                }

                this.disableLightmap();
                if (this.mc.gameSettings.thirdPersonView == 0 && !flag) {
                    this.itemRenderer.renderOverlays(p_renderHand_1_);
                    this.hurtCameraEffect(p_renderHand_1_);
                }

                if (this.mc.gameSettings.viewBobbing) {
                    this.setupViewBobbing(p_renderHand_1_);
                }
            }
        }
    }

    public void disableLightmap() {
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        if (Config.isShaders()) {
            Shaders.disableLightmap();
        }
    }

    public void enableLightmap() {
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.loadIdentity();
        float f = 0.00390625F;
        GlStateManager.scale(f, f, f);
        GlStateManager.translate(8.0F, 8.0F, 8.0F);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        this.mc.getTextureManager().bindTexture(this.locationLightMap);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        if (Config.isShaders()) {
            Shaders.enableLightmap();
        }
    }

    private void updateTorchFlicker() {
        this.torchFlickerDX = (float)(this.torchFlickerDX + (Math.random() - Math.random()) * Math.random() * Math.random());
        this.torchFlickerDX = (float)(this.torchFlickerDX * 0.9);
        this.torchFlickerX = this.torchFlickerX + (this.torchFlickerDX - this.torchFlickerX) * 1.0F;
        this.lightmapUpdateNeeded = true;
    }

    private void updateLightmap(float partialTicks) {
        if (this.lightmapUpdateNeeded) {
            this.mc.mcProfiler.startSection("lightTex");
            WorldClient world = this.mc.theWorld;
            if (world != null) {
                if (Config.isCustomColors()
                    && CustomColors.updateLightmap(world, this.torchFlickerX, this.lightmapColors, this.mc.thePlayer.isPotionActive(Potion.nightVision), partialTicks)) {
                    this.lightmapTexture.updateDynamicTexture();
                    this.lightmapUpdateNeeded = false;
                    this.mc.mcProfiler.endSection();
                    return;
                }

                float sunBrightness = world.getSunBrightness(1.0F);
                float f = sunBrightness * 0.95F + 0.05F;

                for (int i = 0; i < 256; i++) {
                    float f2 = world.provider.getLightBrightnessTable()[i / 16] * f;
                    float f3 = world.provider.getLightBrightnessTable()[i % 16] * (this.torchFlickerX * 0.1F + 1.5F);
                    if (world.getLastLightningBolt() > 0) {
                        f2 = world.provider.getLightBrightnessTable()[i / 16];
                    }

                    float f4 = f2 * (sunBrightness * 0.65F + 0.35F);
                    float f5 = f2 * (sunBrightness * 0.65F + 0.35F);
                    float f6 = f3 * ((f3 * 0.6F + 0.4F) * 0.6F + 0.4F);
                    float f7 = f3 * (f3 * f3 * 0.6F + 0.4F);
                    float f8 = f4 + f3;
                    float f9 = f5 + f6;
                    float f10 = f2 + f7;
                    float f11 = f8 * 0.96F + 0.03F;
                    float f12 = f9 * 0.96F + 0.03F;
                    float f13 = f10 * 0.96F + 0.03F;
                    if (this.bossColorModifier > 0.0F) {
                        float f14 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
                        f11 = f11 * (1.0F - f14) + f11 * 0.7F * f14;
                        f12 = f12 * (1.0F - f14) + f12 * 0.6F * f14;
                        f13 = f13 * (1.0F - f14) + f13 * 0.6F * f14;
                    }

                    if (world.provider.getDimensionId() == 1) {
                        f11 = 0.22F + f3 * 0.75F;
                        f12 = 0.28F + f6 * 0.75F;
                        f13 = 0.25F + f7 * 0.75F;
                    }

                    if (this.mc.thePlayer.isPotionActive(Potion.nightVision)) {
                        float nightVisionBrightness = this.getNightVisionBrightness(this.mc.thePlayer, partialTicks);
                        float f15 = 1.0F / f11;
                        if (f15 > 1.0F / f12) {
                            f15 = 1.0F / f12;
                        }

                        if (f15 > 1.0F / f13) {
                            f15 = 1.0F / f13;
                        }

                        f11 = f11 * (1.0F - nightVisionBrightness) + f11 * f15 * nightVisionBrightness;
                        f12 = f12 * (1.0F - nightVisionBrightness) + f12 * f15 * nightVisionBrightness;
                        f13 = f13 * (1.0F - nightVisionBrightness) + f13 * f15 * nightVisionBrightness;
                    }

                    if (f11 > 1.0F) {
                        f11 = 1.0F;
                    }

                    if (f12 > 1.0F) {
                        f12 = 1.0F;
                    }

                    if (f13 > 1.0F) {
                        f13 = 1.0F;
                    }

                    float gammaSetting = this.mc.gameSettings.gammaSetting;
                    float f16 = 1.0F - f11;
                    float f17 = 1.0F - f12;
                    float f18 = 1.0F - f13;
                    f16 = 1.0F - f16 * f16 * f16 * f16;
                    f17 = 1.0F - f17 * f17 * f17 * f17;
                    f18 = 1.0F - f18 * f18 * f18 * f18;
                    f8 = f11 * (1.0F - gammaSetting) + f16 * gammaSetting;
                    f9 = f12 * (1.0F - gammaSetting) + f17 * gammaSetting;
                    f10 = f13 * (1.0F - gammaSetting) + f18 * gammaSetting;
                    float f19 = f8 * 0.96F + 0.03F;
                    float f20 = f9 * 0.96F + 0.03F;
                    float f21 = f10 * 0.96F + 0.03F;
                    if (f19 > 1.0F) {
                        f19 = 1.0F;
                    }

                    if (f20 > 1.0F) {
                        f20 = 1.0F;
                    }

                    if (f21 > 1.0F) {
                        f21 = 1.0F;
                    }

                    if (f19 < 0.0F) {
                        f19 = 0.0F;
                    }

                    if (f20 < 0.0F) {
                        f20 = 0.0F;
                    }

                    if (f21 < 0.0F) {
                        f21 = 0.0F;
                    }

                    short s = 255;
                    int n = (int)(f19 * 255.0F);
                    int n2 = (int)(f20 * 255.0F);
                    int n3 = (int)(f21 * 255.0F);
                    this.lightmapColors[i] = s << 24 | n << 16 | n2 << 8 | n3;
                }

                this.lightmapTexture.updateDynamicTexture();
                this.lightmapUpdateNeeded = false;
                this.mc.mcProfiler.endSection();
            }
        }
    }

    public float getNightVisionBrightness(EntityLivingBase entitylivingbaseIn, float partialTicks) {
        int duration = entitylivingbaseIn.getActivePotionEffect(Potion.nightVision).getDuration();
        return duration > 200 ? 1.0F : 0.7F + MathHelper.sin((duration - partialTicks) * (float) Math.PI * 0.2F) * 0.3F;
    }

    public void func_181560_a(float partialTicks, long p_181560_2_) {
        CustomItems.beginRenderFrame();
        Config.renderPartialTicks = partialTicks;
        this.frameInit();
        boolean active = Display.isActive();
        if (!active && this.mc.gameSettings.pauseOnLostFocus && (!this.mc.gameSettings.touchscreen || !Mouse.isButtonDown(1))) {
            if (Minecraft.getSystemTime() - this.prevFrameTime > 500L) {
                this.mc.displayInGameMenu();
            }
        } else {
            this.prevFrameTime = Minecraft.getSystemTime();
        }

        this.mc.mcProfiler.startSection("mouse");
        if (active && Minecraft.isRunningOnMac && this.mc.inGameHasFocus && !Mouse.isInsideWindow()) {
            Mouse.setGrabbed(false);
            Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
            Mouse.setGrabbed(true);
        }

        if (this.mc.inGameHasFocus && active) {
            this.mc.mouseHelper.mouseXYChange();
            float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f2 = f * f * f * 8.0F;
            float f3 = this.mc.mouseHelper.deltaX * f2;
            float f4 = this.mc.mouseHelper.deltaY * f2;
            byte b = 1;
            if (this.mc.gameSettings.invertMouse) {
                b = -1;
            }

            if (this.mc.gameSettings.smoothCamera) {
                this.smoothCamYaw += f3;
                this.smoothCamPitch += f4;
                float f5 = partialTicks - this.smoothCamPartialTicks;
                this.smoothCamPartialTicks = partialTicks;
                f3 = this.smoothCamFilterX * f5;
                f4 = this.smoothCamFilterY * f5;
                if (GetPitchHelper.flag) {
                    GetPitchHelper.handleFloat(f3, f4 * b);
                } else {
                    ((AbstractClientPlayer)(FreeCam.entityOtherPlayerMP != null ? FreeCam.entityOtherPlayerMP : this.mc.thePlayer))
                        .setAngles(f3, f4 * b);
                }
            } else {
                if (GetPitchHelper.flag) {
                    GetPitchHelper.handleFloat(f3, f4 * b);
                } else {
                    ((AbstractClientPlayer)(FreeCam.entityOtherPlayerMP != null ? FreeCam.entityOtherPlayerMP : this.mc.thePlayer))
                        .setAngles(f3, f4 * b);
                }

                this.smoothCamYaw = 0.0F;
                this.smoothCamPitch = 0.0F;
            }
        }

        this.mc.mcProfiler.endSection();
        if (!this.mc.skipRenderWorld) {
            anaglyphEnable = this.mc.gameSettings.anaglyph;
            final ScaledResolution sr = InvalidateHelper.computeScaledResolution(this.mc);
            int scaledWidth = sr.getScaledWidth();
            int scaledHeight = sr.getScaledHeight();
            final int n = Mouse.getX() * scaledWidth / this.mc.displayWidth;
            final int n2 = scaledHeight - Mouse.getY() * scaledHeight / this.mc.displayHeight - 1;
            int limitFramerate = this.mc.gameSettings.limitFramerate;
            if (this.mc.theWorld != null) {
                this.mc.mcProfiler.startSection("level");
                int n3 = Math.min(Minecraft.getDebugFPS(), limitFramerate);
                n3 = Math.max(n3, 60);
                long l = System.nanoTime() - p_181560_2_;
                long l2 = Math.max(1000000000 / n3 / 4 - l, 0L);
                this.renderWorld(partialTicks, System.nanoTime() + l2);
                if (OpenGlHelper.shadersSupported) {
                    this.mc.renderGlobal.renderEntityOutlineFramebuffer();
                    if (this.theShaderGroup != null && this.useShader) {
                        GlStateManager.matrixMode(GL11.GL_TEXTURE);
                        GlStateManager.pushMatrix();
                        GlStateManager.loadIdentity();
                        this.theShaderGroup.loadShaderGroup(partialTicks);
                        GlStateManager.popMatrix();
                    }

                    this.mc.getFramebuffer().bindFramebuffer(true);
                }

                MotionBlur.handleFloat2(partialTicks);
                this.renderEndNanoTime = System.nanoTime();
                this.mc.mcProfiler.endStartSection("gui");
                if (!this.mc.gameSettings.hideGUI || this.mc.currentScreen != null) {
                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
                    this.mc.ingameGUI.renderGameOverlay(partialTicks);
                    if (this.mc.gameSettings.ofShowFps && !this.mc.gameSettings.showDebugInfo) {
                        Config.drawFps();
                    }

                    if (this.mc.gameSettings.showDebugInfo) {
                        Lagometer.showLagometer(sr);
                    }
                }

                this.mc.mcProfiler.endSection();
            } else {
                GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
                GlStateManager.matrixMode(GL11.GL_PROJECTION);
                GlStateManager.loadIdentity();
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                GlStateManager.loadIdentity();
                this.setupOverlayRendering();
                this.renderEndNanoTime = System.nanoTime();
                TileEntityRendererDispatcher.instance.renderEngine = this.mc.getTextureManager();
                TileEntityRendererDispatcher.instance.fontRenderer = this.mc.fontRendererObj;
            }

            if (this.mc.currentScreen != null) {
                GlStateManager.clear(256);

                try (org.brutality.utils.render.RenderHelper renderHelper = org.brutality.utils.render.RenderHelper.matchesGuiScreen(this.mc.currentScreen)
                        ? org.brutality.utils.render.RenderHelper.computeRenderHelper2(this.mc.currentScreen)
                        : null) {
                    int n4 = renderHelper == null ? n : Mouse.getX() * this.mc.currentScreen.width / this.mc.displayWidth;
                    int n5 = renderHelper == null ? n2 : this.mc.currentScreen.height - Mouse.getY() * this.mc.currentScreen.height / this.mc.displayHeight - 1;
                    if (Reflector.ForgeHooksClient_drawScreen.exists()) {
                        Reflector.callVoid(Reflector.ForgeHooksClient_drawScreen, this.mc.currentScreen, n4, n5, partialTicks);
                    } else {
                        this.mc.currentScreen.drawScreen(n4, n5, partialTicks);
                    }
                } catch (Throwable e) {
                    CrashReport crashReport = CrashReport.makeCrashReport(e, "Rendering screen");
                    CrashReportCategory crashReportCategory = crashReport.makeCategory("Screen render details");
                    crashReportCategory.addCrashSectionCallable("Screen name", new Callable<String>() {
                        public String call() throws Exception {
                            return EntityRenderer.this.mc.currentScreen.getClass().getCanonicalName();
                        }
                    });
                    crashReportCategory.addCrashSectionCallable("Mouse location", new Callable<String>() {
                        public String call() throws Exception {
                            return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", n, n2, Mouse.getX(), Mouse.getY());
                        }
                    });
                    crashReportCategory.addCrashSectionCallable(
                        "Screen size",
                        new Callable<String>() {
                            public String call() throws Exception {
                                return String.format(
                                    "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d",
                                    sr.getScaledWidth(),
                                    sr.getScaledHeight(),
                                    EntityRenderer.this.mc.displayWidth,
                                    EntityRenderer.this.mc.displayHeight,
                                    sr.getScaleFactor()
                                );
                            }
                        }
                    );
                    throw new ReportedException(crashReport);
                }
            }
        }

        try {
            Brutality.renderNotifications();
        } catch (Exception e2) {
            System.err.println("[EntityRenderer] Error rendering ban notifications: " + e2.getMessage());
        }

        GlStateGuard renderHelper7 = new GlStateGuard();

        try {
            this.setupOverlayRendering();
            ProxyBanToast.render();
        } catch (Throwable e3) {
            try {
                renderHelper7.close();
            } catch (Throwable e4) {
                e3.addSuppressed(e4);
            }

            throw e3;
        }

        renderHelper7.close();
        this.frameFinish();
        this.waitForServerThread();
        Lagometer.updateLagometer();
        if (this.mc.gameSettings.ofProfiler) {
            this.mc.gameSettings.showDebugProfilerChart = true;
        }
    }

    public void renderStreamIndicator(float partialTicks) {
        this.setupOverlayRendering();
        this.mc.ingameGUI.renderStreamIndicator(InvalidateHelper.computeScaledResolution(this.mc));
    }

    private boolean isDrawBlockOutline() {
        if (!this.drawBlockOutline) {
            return false;
        }
        Entity renderViewEntity = this.mc.getRenderViewEntity();
        boolean flag = renderViewEntity instanceof EntityPlayer && !this.mc.gameSettings.hideGUI;
        if (flag && !((EntityPlayer)renderViewEntity).capabilities.allowEdit) {
            ItemStack currentEquippedItem = ((EntityPlayer)renderViewEntity).getCurrentEquippedItem();
            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
                BlockPos blockPos = this.mc.objectMouseOver.getBlockPos();
                IBlockState blockState = this.mc.theWorld.getBlockState(blockPos);
                Block block = blockState.getBlock();
                if (this.mc.playerController.getCurrentGameType() == GameType.SPECTATOR) {
                    flag = ReflectorForge.blockHasTileEntity(blockState) && this.mc.theWorld.getTileEntity(blockPos) instanceof IInventory;
                } else {
                    flag = currentEquippedItem != null && (currentEquippedItem.canDestroy(block) || currentEquippedItem.canPlaceOn(block));
                }
            }
        }

        return flag;
    }

    private void renderWorldDirections(float partialTicks) {
        if (this.mc.gameSettings.showDebugInfo
            && !this.mc.gameSettings.hideGUI
            && !this.mc.thePlayer.hasReducedDebug()
            && !this.mc.gameSettings.reducedDebugInfo) {
            Entity renderViewEntity = this.mc.getRenderViewEntity();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glLineWidth(1.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.pushMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            this.orientCamera(partialTicks);
            GlStateManager.translate(0.0F, renderViewEntity.getEyeHeight(), 0.0F);
            RenderGlobal.func_181563_a(new AxisAlignedBB(0.0, 0.0, 0.0, 0.005, 1.0E-4, 1.0E-4), 255, 0, 0, 255);
            RenderGlobal.func_181563_a(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0E-4, 1.0E-4, 0.005), 0, 0, 255, 255);
            RenderGlobal.func_181563_a(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0E-4, 0.0033, 1.0E-4), 0, 255, 0, 255);
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }

    public void renderWorld(float partialTicks, long finishTimeNano) {
        this.updateLightmap(partialTicks);
        if (this.mc.getRenderViewEntity() == null) {
            this.mc.setRenderViewEntity(this.mc.thePlayer);
        }

        PlayerHelper13.handleMinecraft(this.mc);
        this.getMouseOver(partialTicks);
        if (Config.isShaders()) {
            Shaders.beginRender(this.mc, partialTicks, finishTimeNano);
        }

        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        this.mc.mcProfiler.startSection("center");
        if (this.mc.gameSettings.anaglyph) {
            anaglyphField = 0;
            GlStateManager.colorMask(false, true, true, false);
            this.renderWorldPass(0, partialTicks, finishTimeNano);
            anaglyphField = 1;
            GlStateManager.colorMask(true, false, false, false);
            this.renderWorldPass(1, partialTicks, finishTimeNano);
            GlStateManager.colorMask(true, true, true, false);
        } else {
            this.renderWorldPass(2, partialTicks, finishTimeNano);
        }

        this.mc.mcProfiler.endSection();
    }

    private void renderWorldPass(int pass, float partialTicks, long finishTimeNano) {
        FontResourceLoader.process();

        try {
            boolean shaders = Config.isShaders();
            if (shaders) {
                Shaders.beginRenderPass(pass, partialTicks, finishTimeNano);
            }

            RenderGlobal renderGlobal = this.mc.renderGlobal;
            EffectRenderer effectRenderer = this.mc.effectRenderer;
            boolean drawBlockOutline = this.isDrawBlockOutline();
            GlStateManager.enableCull();
            this.mc.mcProfiler.endStartSection("clear");
            if (shaders) {
                Shaders.setViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
            } else {
                GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
            }

            this.updateFogColor(partialTicks);
            GlStateManager.clear(16640);
            if (shaders) {
                Shaders.clearRenderBuffer();
            }

            this.mc.mcProfiler.endStartSection("camera");
            this.setupCameraTransform(partialTicks, pass);
            if (shaders) {
                Shaders.setCamera(partialTicks);
            }

            ActiveRenderInfo.updateRenderInfo(this.mc.thePlayer, this.mc.gameSettings.thirdPersonView == 2);
            this.mc.mcProfiler.endStartSection("frustum");
            ClippingHelper instance = ClippingHelperImpl.getInstance();
            this.mc.mcProfiler.endStartSection("culling");
            instance.disabled = Config.isShaders() && !Shaders.isFrustumCulling();
            Frustum frustum = new Frustum(instance);
            Entity renderViewEntity = this.mc.getRenderViewEntity();
            double d = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * partialTicks;
            double d2 = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * partialTicks;
            double d3 = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * partialTicks;
            if (shaders) {
                ShadersRender.setFrustrumPosition(frustum, d, d2, d3);
            } else {
                frustum.setPosition(d, d2, d3);
            }

            if ((Config.isSkyEnabled() || Config.isSunMoonEnabled() || Config.isStarsEnabled()) && !Shaders.isShadowPass) {
                this.setupFog(-1, partialTicks);
                this.mc.mcProfiler.endStartSection("sky");
                GlStateManager.matrixMode(GL11.GL_PROJECTION);
                GlStateManager.loadIdentity();
                Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance);
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                if (shaders) {
                    Shaders.beginSky();
                }

                renderGlobal.renderSky(partialTicks, pass);
                if (shaders) {
                    Shaders.endSky();
                }

                GlStateManager.matrixMode(GL11.GL_PROJECTION);
                GlStateManager.loadIdentity();
                Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance);
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            } else {
                GlStateManager.disableBlend();
            }

            this.setupFog(0, partialTicks);
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            if (renderViewEntity.posY + renderViewEntity.getEyeHeight() < 128.0 + this.mc.gameSettings.ofCloudsHeight * 128.0F) {
                this.renderCloudsCheck(renderGlobal, partialTicks, pass);
            }

            float renderPartialTicks = this.mc.timer.renderPartialTicks;
            this.mc.mcProfiler.endStartSection("prepareterrain");
            this.setupFog(0, partialTicks);
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            RenderHelper.disableStandardItemLighting();
            this.mc.mcProfiler.endStartSection("terrain_setup");
            this.checkLoadVisibleChunks(renderViewEntity, partialTicks, frustum, this.mc.thePlayer.isSpectator());
            if (shaders) {
                ShadersRender.setupTerrain(renderGlobal, renderViewEntity, partialTicks, frustum, this.frameCount++, this.mc.thePlayer.isSpectator());
            } else {
                renderGlobal.setupTerrain(renderViewEntity, partialTicks, frustum, this.frameCount++, this.mc.thePlayer.isSpectator());
            }

            if (pass == 0 || pass == 2) {
                this.mc.mcProfiler.endStartSection("updatechunks");
                Lagometer.timerChunkUpload.start();
                this.mc.renderGlobal.updateChunks(finishTimeNano);
                Lagometer.timerChunkUpload.end();
            }

            this.mc.mcProfiler.endStartSection("terrain");
            Lagometer.timerTerrain.start();
            if (this.mc.gameSettings.ofSmoothFps && pass > 0) {
                this.mc.mcProfiler.endStartSection("finish");
                GL11.glFinish();
                this.mc.mcProfiler.endStartSection("terrain");
            }

            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            if (shaders) {
                ShadersRender.beginTerrainSolid();
            }

            renderGlobal.renderBlockLayer(EnumWorldBlockLayer.SOLID, partialTicks, pass, renderViewEntity);
            GlStateManager.enableAlpha();
            if (shaders) {
                ShadersRender.beginTerrainCutoutMipped();
            }

            this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, this.mc.gameSettings.mipmapLevels > 0);
            renderGlobal.renderBlockLayer(EnumWorldBlockLayer.CUTOUT_MIPPED, partialTicks, pass, renderViewEntity);
            this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
            this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
            if (shaders) {
                ShadersRender.beginTerrainCutout();
            }

            renderGlobal.renderBlockLayer(EnumWorldBlockLayer.CUTOUT, partialTicks, pass, renderViewEntity);
            this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
            if (shaders) {
                ShadersRender.endTerrain();
            }

            Lagometer.timerTerrain.end();
            GlStateManager.shadeModel(GL11.GL_FLAT);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            if (!this.debugView) {
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                RenderHelper.enableStandardItemLighting();
                this.mc.mcProfiler.endStartSection("entities");
                if (Reflector.ForgeHooksClient_setRenderPass.exists()) {
                    Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, 0);
                }

                renderGlobal.renderEntities(renderViewEntity, frustum, partialTicks);
                if (Reflector.ForgeHooksClient_setRenderPass.exists()) {
                    Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, -1);
                }

                RenderHelper.disableStandardItemLighting();
                this.disableLightmap();
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                if (this.mc.objectMouseOver != null && renderViewEntity.isInsideOfMaterial(Material.water) && drawBlockOutline) {
                    EntityPlayer player = (EntityPlayer)renderViewEntity;
                    GlStateManager.disableAlpha();
                    this.mc.mcProfiler.endStartSection("outline");
                    renderGlobal.drawSelectionBox(player, this.mc.objectMouseOver, 0, partialTicks);
                    GlStateManager.enableAlpha();
                }
            }

            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.popMatrix();
            if (drawBlockOutline && this.mc.objectMouseOver != null && !renderViewEntity.isInsideOfMaterial(Material.water)) {
                EntityPlayer player2 = (EntityPlayer)renderViewEntity;
                GlStateManager.disableAlpha();
                this.mc.mcProfiler.endStartSection("outline");
                if ((
                        !Reflector.ForgeHooksClient_onDrawBlockHighlight.exists()
                            || !Reflector.callBoolean(
                                Reflector.ForgeHooksClient_onDrawBlockHighlight, renderGlobal, player2, this.mc.objectMouseOver, 0, player2.getHeldItem(), partialTicks
                            )
                    )
                    && !this.mc.gameSettings.hideGUI) {
                    renderGlobal.drawSelectionBox(player2, this.mc.objectMouseOver, 0, partialTicks);
                }

                GlStateManager.enableAlpha();
            }

            if (!renderGlobal.damagedBlocks.isEmpty()) {
                this.mc.mcProfiler.endStartSection("destroyProgress");
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
                this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
                renderGlobal.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getWorldRenderer(), renderViewEntity, partialTicks);
                this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
                GlStateManager.disableBlend();
            }

            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.disableBlend();
            if (!this.debugView) {
                this.enableLightmap();
                this.mc.mcProfiler.endStartSection("litParticles");
                if (shaders) {
                    Shaders.beginLitParticles();
                }

                effectRenderer.renderLitParticles(renderViewEntity, partialTicks);
                RenderHelper.disableStandardItemLighting();
                this.setupFog(0, partialTicks);
                this.mc.mcProfiler.endStartSection("particles");
                if (shaders) {
                    Shaders.beginParticles();
                }

                effectRenderer.renderParticles(renderViewEntity, partialTicks);
                if (shaders) {
                    Shaders.endParticles();
                }

                this.disableLightmap();
            }

            GlStateManager.depthMask(false);
            if (Config.isShaders()) {
                GlStateManager.depthMask(Shaders.isRainDepth());
            }

            GlStateManager.enableCull();
            this.mc.mcProfiler.endStartSection("weather");
            if (shaders) {
                Shaders.beginWeather();
            }

            this.renderRainSnow(partialTicks);
            if (shaders) {
                Shaders.endWeather();
            }

            GlStateManager.depthMask(true);
            renderGlobal.renderWorldBorder(renderViewEntity, partialTicks);
            if (shaders) {
                ShadersRender.renderHand0(this, partialTicks, pass);
                Shaders.preWater();
            }

            GlStateManager.disableBlend();
            GlStateManager.enableCull();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            this.setupFog(0, partialTicks);
            GlStateManager.enableBlend();
            GlStateManager.depthMask(false);
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            this.mc.mcProfiler.endStartSection("translucent");
            if (shaders) {
                Shaders.beginWater();
            }

            renderGlobal.renderBlockLayer(EnumWorldBlockLayer.TRANSLUCENT, partialTicks, pass, renderViewEntity);
            if (shaders) {
                Shaders.endWater();
            }

            if (Reflector.ForgeHooksClient_setRenderPass.exists() && !this.debugView) {
                RenderHelper.enableStandardItemLighting();
                this.mc.mcProfiler.endStartSection("entities");
                Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, 1);
                this.mc.renderGlobal.renderEntities(renderViewEntity, frustum, partialTicks);
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, -1);
                RenderHelper.disableStandardItemLighting();
            }

            GlStateManager.shadeModel(GL11.GL_FLAT);
            GlStateManager.depthMask(true);
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.disableFog();
            if (renderViewEntity.posY + renderViewEntity.getEyeHeight() >= 128.0 + this.mc.gameSettings.ofCloudsHeight * 128.0F) {
                this.mc.mcProfiler.endStartSection("aboveClouds");
                this.renderCloudsCheck(renderGlobal, partialTicks, pass);
            }

            if (Reflector.ForgeHooksClient_dispatchRenderLast.exists()) {
                this.mc.mcProfiler.endStartSection("forge_render_last");
                Reflector.callVoid(Reflector.ForgeHooksClient_dispatchRenderLast, renderGlobal, partialTicks);
            }

            ScaledResolution sr = InvalidateHelper.computeScaledResolution(this.mc);
            GetPartialTicksEvent getPartialTicksEvent = new GetPartialTicksEvent(sr, this.mc.timer.renderPartialTicks);
            getPartialTicksEvent.process();
            if (!getPartialTicksEvent.cancel2()) {
            }

            EventRender3D event = new EventRender3D(renderPartialTicks);
            event.process();
            if (!event.cancel2()) {
            }

            this.mc.mcProfiler.endStartSection("hand");
            if (this.renderHand && !Shaders.isShadowPass && FreeCam.entityOtherPlayerMP == null) {
                if (shaders) {
                    ShadersRender.renderHand1(this, partialTicks, pass);
                    Shaders.renderCompositeFinal();
                }

                GlStateManager.clear(256);
                if (shaders) {
                    ShadersRender.renderFPOverlay(this, partialTicks, pass);
                } else {
                    this.renderHand(partialTicks, pass);
                }

                this.renderWorldDirections(partialTicks);
            }

            if (shaders) {
                Shaders.endRender();
            }
        } finally {
            FontResourceLoader.process2();
        }
    }

    private void renderCloudsCheck(RenderGlobal renderGlobalIn, float partialTicks, int pass) {
        if (this.mc.gameSettings.renderDistanceChunks >= 4 && !Config.isCloudsOff() && Shaders.shouldRenderClouds(this.mc.gameSettings)) {
            this.mc.mcProfiler.endStartSection("clouds");
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance * 4.0F);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.pushMatrix();
            this.setupFog(0, partialTicks);
            renderGlobalIn.renderClouds(partialTicks, pass);
            GlStateManager.disableFog();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }
    }

    private void addRainParticles() {
        float rainStrength = this.mc.theWorld.getRainStrength(1.0F);
        if (!Config.isRainFancy()) {
            rainStrength /= 2.0F;
        }

        if (rainStrength != 0.0F && Config.isRainSplash()) {
            this.random.setSeed(this.rendererUpdateCount * 312987231L);
            Entity renderViewEntity = this.mc.getRenderViewEntity();
            WorldClient world = this.mc.theWorld;
            BlockPos pos = new BlockPos(renderViewEntity);
            int n = 10;
            double d = 0.0;
            double d2 = 0.0;
            double d3 = 0.0;
            int n2 = 0;
            int n3 = (int)(100.0F * rainStrength * rainStrength);
            if (this.mc.gameSettings.particleSetting == 1) {
                n3 >>= 1;
            } else if (this.mc.gameSettings.particleSetting == 2) {
                n3 = 0;
            }

            for (int i = 0; i < n3; i++) {
                BlockPos precipitationHeight = world.getPrecipitationHeight(
                    pos.add(this.random.nextInt(n) - this.random.nextInt(n), 0, this.random.nextInt(n) - this.random.nextInt(n))
                );
                BiomeGenBase biomeGenForCoords = world.getBiomeGenForCoords(precipitationHeight);
                BlockPos pos2 = precipitationHeight.down();
                Block block = world.getBlockState(pos2).getBlock();
                if (precipitationHeight.getY() <= pos.getY() + n
                    && precipitationHeight.getY() >= pos.getY() - n
                    && biomeGenForCoords.canSpawnLightningBolt()
                    && biomeGenForCoords.getFloatTemperature(precipitationHeight) >= 0.15F) {
                    double d4 = this.random.nextDouble();
                    double d5 = this.random.nextDouble();
                    if (block.getMaterial() == Material.lava) {
                        this.mc
                            .theWorld
                            .spawnParticle(
                                EnumParticleTypes.SMOKE_NORMAL,
                                precipitationHeight.getX() + d4,
                                precipitationHeight.getY() + 0.1F - block.getBlockBoundsMinY(),
                                precipitationHeight.getZ() + d5,
                                0.0,
                                0.0,
                                0.0,
                                new int[0]
                            );
                    } else if (block.getMaterial() != Material.air) {
                        block.setBlockBoundsBasedOnState(world, pos2);
                        if (this.random.nextInt(++n2) == 0) {
                            d = pos2.getX() + d4;
                            d2 = pos2.getY() + 0.1F + block.getBlockBoundsMaxY() - 1.0;
                            d3 = pos2.getZ() + d5;
                        }

                        this.mc
                            .theWorld
                            .spawnParticle(
                                EnumParticleTypes.WATER_DROP,
                                pos2.getX() + d4,
                                pos2.getY() + 0.1F + block.getBlockBoundsMaxY(),
                                pos2.getZ() + d5,
                                0.0,
                                0.0,
                                0.0,
                                new int[0]
                            );
                    }
                }
            }

            if (n2 > 0 && this.random.nextInt(3) < this.rainSoundCounter++) {
                this.rainSoundCounter = 0;
                if (d2 > pos.getY() + 1 && world.getPrecipitationHeight(pos).getY() > MathHelper.floor_float(pos.getY())) {
                    this.mc.theWorld.playSound(d, d2, d3, "ambient.weather.rain", 0.1F, 0.5F, false);
                } else {
                    this.mc.theWorld.playSound(d, d2, d3, "ambient.weather.rain", 0.2F, 1.0F, false);
                }
            }
        }
    }

    protected void renderRainSnow(float partialTicks) {
        if (Reflector.ForgeWorldProvider_getWeatherRenderer.exists()) {
            WorldProvider provider = this.mc.theWorld.provider;
            Object obj = Reflector.call(provider, Reflector.ForgeWorldProvider_getWeatherRenderer);
            if (obj != null) {
                Reflector.callVoid(obj, Reflector.IRenderHandler_render, partialTicks, this.mc.theWorld, this.mc);
                return;
            }
        }

        float rainStrength = this.mc.theWorld.getRainStrength(partialTicks);
        if (rainStrength > 0.0F) {
            if (Config.isRainOff()) {
                return;
            }

            this.enableLightmap();
            Entity renderViewEntity = this.mc.getRenderViewEntity();
            WorldClient world = this.mc.theWorld;
            int n = MathHelper.floor_double(renderViewEntity.posX);
            int n2 = MathHelper.floor_double(renderViewEntity.posY);
            int n3 = MathHelper.floor_double(renderViewEntity.posZ);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            GlStateManager.disableCull();
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            double d = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * partialTicks;
            double d2 = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * partialTicks;
            double d3 = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * partialTicks;
            int n4 = MathHelper.floor_double(d2);
            byte b = 5;
            if (Config.isRainFancy()) {
                b = 10;
            }

            int n5 = -1;
            float f = this.rendererUpdateCount + partialTicks;
            worldRenderer.setTranslation(-d, -d2, -d3);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

            for (int i = n3 - b; i <= n3 + b; i++) {
                for (int j = n - b; j <= n + b; j++) {
                    int n6 = (i - n3 + 16) * 32 + j - n + 16;
                    double d4 = this.rainXCoords[n6] * 0.5;
                    double d5 = this.rainYCoords[n6] * 0.5;
                    mutablePos.func_181079_c(j, 0, i);
                    BiomeGenBase biomeGenForCoords = world.getBiomeGenForCoords(mutablePos);
                    if (biomeGenForCoords.canSpawnLightningBolt() || biomeGenForCoords.getEnableSnow()) {
                        int y = world.getPrecipitationHeight(mutablePos).getY();
                        int n7 = n2 - b;
                        int n8 = n2 + b;
                        if (n7 < y) {
                            n7 = y;
                        }

                        if (n8 < y) {
                            n8 = y;
                        }

                        int n9 = y;
                        if (y < n4) {
                            n9 = n4;
                        }

                        if (n7 != n8) {
                            this.random.setSeed(j * j * 3121 + j * 45238971 ^ i * i * 418711 + i * 13761);
                            mutablePos.func_181079_c(j, n7, i);
                            float floatTemperature = biomeGenForCoords.getFloatTemperature(mutablePos);
                            if (world.getWorldChunkManager().getTemperatureAtHeight(floatTemperature, y) >= 0.15F) {
                                if (n5 != 0) {
                                    if (n5 >= 0) {
                                        tessellator.draw();
                                    }

                                    n5 = 0;
                                    this.mc.getTextureManager().bindTexture(locationRainPng);
                                    worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181704_d);
                                }

                                double d6 = (
                                        (double)(
                                                this.rendererUpdateCount + j * j * 3121 + j * 45238971 + i * i * 418711 + i * 13761
                                                    & 31
                                            )
                                            + partialTicks
                                    )
                                    / 32.0
                                    * (3.0 + this.random.nextDouble());
                                double d7 = j + 0.5F - renderViewEntity.posX;
                                double d8 = i + 0.5F - renderViewEntity.posZ;
                                float f2 = MathHelper.sqrt_double(d7 * d7 + d8 * d8) / b;
                                float f3 = ((1.0F - f2 * f2) * 0.5F + 0.5F) * rainStrength;
                                mutablePos.func_181079_c(j, n9, i);
                                int combinedLight = world.getCombinedLight(mutablePos, 0);
                                int n10 = combinedLight >> 16 & 65535;
                                int n11 = combinedLight & 65535;
                                worldRenderer.func_181662_b(j - d4 + 0.5, n7, i - d5 + 0.5)
                                    .func_181673_a(0.0, n7 * 0.25 + d6)
                                    .func_181666_a(1.0F, 1.0F, 1.0F, f3)
                                    .func_181671_a(n10, n11)
                                    .func_181675_d();
                                worldRenderer.func_181662_b(j + d4 + 0.5, n7, i + d5 + 0.5)
                                    .func_181673_a(1.0, n7 * 0.25 + d6)
                                    .func_181666_a(1.0F, 1.0F, 1.0F, f3)
                                    .func_181671_a(n10, n11)
                                    .func_181675_d();
                                worldRenderer.func_181662_b(j + d4 + 0.5, n8, i + d5 + 0.5)
                                    .func_181673_a(1.0, n8 * 0.25 + d6)
                                    .func_181666_a(1.0F, 1.0F, 1.0F, f3)
                                    .func_181671_a(n10, n11)
                                    .func_181675_d();
                                worldRenderer.func_181662_b(j - d4 + 0.5, n8, i - d5 + 0.5)
                                    .func_181673_a(0.0, n8 * 0.25 + d6)
                                    .func_181666_a(1.0F, 1.0F, 1.0F, f3)
                                    .func_181671_a(n10, n11)
                                    .func_181675_d();
                            } else {
                                if (n5 != 1) {
                                    if (n5 >= 0) {
                                        tessellator.draw();
                                    }

                                    n5 = 1;
                                    this.mc.getTextureManager().bindTexture(locationSnowPng);
                                    worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181704_d);
                                }

                                double d9 = ((this.rendererUpdateCount & 511) + partialTicks) / 512.0F;
                                double d10 = this.random.nextDouble() + f * 0.01 * (float)this.random.nextGaussian();
                                double d11 = this.random.nextDouble() + f * (float)this.random.nextGaussian() * 0.001;
                                double d12 = j + 0.5F - renderViewEntity.posX;
                                double d13 = i + 0.5F - renderViewEntity.posZ;
                                float f4 = MathHelper.sqrt_double(d12 * d12 + d13 * d13) / b;
                                float f5 = ((1.0F - f4 * f4) * 0.3F + 0.5F) * rainStrength;
                                mutablePos.func_181079_c(j, n9, i);
                                int n12 = (world.getCombinedLight(mutablePos, 0) * 3 + 15728880) / 4;
                                int n13 = n12 >> 16 & 65535;
                                int n14 = n12 & 65535;
                                worldRenderer.func_181662_b(j - d4 + 0.5, n7, i - d5 + 0.5)
                                    .func_181673_a(0.0 + d10, n7 * 0.25 + d9 + d11)
                                    .func_181666_a(1.0F, 1.0F, 1.0F, f5)
                                    .func_181671_a(n13, n14)
                                    .func_181675_d();
                                worldRenderer.func_181662_b(j + d4 + 0.5, n7, i + d5 + 0.5)
                                    .func_181673_a(1.0 + d10, n7 * 0.25 + d9 + d11)
                                    .func_181666_a(1.0F, 1.0F, 1.0F, f5)
                                    .func_181671_a(n13, n14)
                                    .func_181675_d();
                                worldRenderer.func_181662_b(j + d4 + 0.5, n8, i + d5 + 0.5)
                                    .func_181673_a(1.0 + d10, n8 * 0.25 + d9 + d11)
                                    .func_181666_a(1.0F, 1.0F, 1.0F, f5)
                                    .func_181671_a(n13, n14)
                                    .func_181675_d();
                                worldRenderer.func_181662_b(j - d4 + 0.5, n8, i - d5 + 0.5)
                                    .func_181673_a(0.0 + d10, n8 * 0.25 + d9 + d11)
                                    .func_181666_a(1.0F, 1.0F, 1.0F, f5)
                                    .func_181671_a(n13, n14)
                                    .func_181675_d();
                            }
                        }
                    }
                }
            }

            if (n5 >= 0) {
                tessellator.draw();
            }

            worldRenderer.setTranslation(0.0, 0.0, 0.0);
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            this.disableLightmap();
        }
    }

    public void setupOverlayRendering() {
        ScaledResolution sr = InvalidateHelper.computeScaledResolution(this.mc);
        GlStateManager.clear(256);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
    }

    private void updateFogColor(float partialTicks) {
        WorldClient world = this.mc.theWorld;
        Entity renderViewEntity = this.mc.getRenderViewEntity();
        float f = 0.25F + 0.75F * this.mc.gameSettings.renderDistanceChunks / 32.0F;
        float f2 = 1.0F - (float)Math.pow(f, 0.25);
        Vec3 skyColor = world.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
        skyColor = CustomColors.getWorldSkyColor(skyColor, world, this.mc.getRenderViewEntity(), partialTicks);
        float xCoord = (float)skyColor.xCoord;
        float yCoord = (float)skyColor.yCoord;
        float zCoord = (float)skyColor.zCoord;
        Vec3 fogColor = world.getFogColor(partialTicks);
        fogColor = CustomColors.getWorldFogColor(fogColor, world, this.mc.getRenderViewEntity(), partialTicks);
        this.fogColorRed = (float)fogColor.xCoord;
        this.fogColorGreen = (float)fogColor.yCoord;
        this.fogColorBlue = (float)fogColor.zCoord;
        if (this.mc.gameSettings.renderDistanceChunks >= 4) {
            double d = -1.0;
            Vec3 vec = MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) > 0.0F ? new Vec3(d, 0.0, 0.0) : new Vec3(1.0, 0.0, 0.0);
            float f3 = (float)renderViewEntity.getLook(partialTicks).dotProduct(vec);
            if (f3 < 0.0F) {
                f3 = 0.0F;
            }

            if (f3 > 0.0F) {
                float[] floats = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);
                if (floats != null) {
                    f3 *= floats[3];
                    this.fogColorRed = this.fogColorRed * (1.0F - f3) + floats[0] * f3;
                    this.fogColorGreen = this.fogColorGreen * (1.0F - f3) + floats[1] * f3;
                    this.fogColorBlue = this.fogColorBlue * (1.0F - f3) + floats[2] * f3;
                }
            }
        }

        this.fogColorRed = this.fogColorRed + (xCoord - this.fogColorRed) * f2;
        this.fogColorGreen = this.fogColorGreen + (yCoord - this.fogColorGreen) * f2;
        this.fogColorBlue = this.fogColorBlue + (zCoord - this.fogColorBlue) * f2;
        float rainStrength = world.getRainStrength(partialTicks);
        if (rainStrength > 0.0F) {
            float f4 = 1.0F - rainStrength * 0.5F;
            float f5 = 1.0F - rainStrength * 0.4F;
            this.fogColorRed *= f4;
            this.fogColorGreen *= f4;
            this.fogColorBlue *= f5;
        }

        float thunderStrength = world.getThunderStrength(partialTicks);
        if (thunderStrength > 0.0F) {
            float f6 = 1.0F - thunderStrength * 0.5F;
            this.fogColorRed *= f6;
            this.fogColorGreen *= f6;
            this.fogColorBlue *= f6;
        }

        Block blockAtEntityViewpoint = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, renderViewEntity, partialTicks);
        if (this.cloudFog) {
            Vec3 cloudColour = world.getCloudColour(partialTicks);
            this.fogColorRed = (float)cloudColour.xCoord;
            this.fogColorGreen = (float)cloudColour.yCoord;
            this.fogColorBlue = (float)cloudColour.zCoord;
        } else if (blockAtEntityViewpoint.getMaterial() == Material.water) {
            float f7 = EnchantmentHelper.getRespiration(renderViewEntity) * 0.2F;
            float f8 = Config.limit(f7, 0.0F, 0.6F);
            if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPotionActive(Potion.waterBreathing)) {
                f8 = f8 * 0.3F + 0.6F;
            }

            this.fogColorRed = 0.02F + f8;
            this.fogColorGreen = 0.02F + f8;
            this.fogColorBlue = 0.2F + f8;
            Vec3 underwaterColor = CustomColors.getUnderwaterColor(
                this.mc.theWorld, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0, this.mc.getRenderViewEntity().posZ
            );
            if (underwaterColor != null) {
                this.fogColorRed = (float)underwaterColor.xCoord;
                this.fogColorGreen = (float)underwaterColor.yCoord;
                this.fogColorBlue = (float)underwaterColor.zCoord;
            }
        } else if (blockAtEntityViewpoint.getMaterial() == Material.lava) {
            this.fogColorRed = 0.6F;
            this.fogColorGreen = 0.1F;
            this.fogColorBlue = 0.0F;
            Vec3 underlavaColor = CustomColors.getUnderlavaColor(
                this.mc.theWorld, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0, this.mc.getRenderViewEntity().posZ
            );
            if (underlavaColor != null) {
                this.fogColorRed = (float)underlavaColor.xCoord;
                this.fogColorGreen = (float)underlavaColor.yCoord;
                this.fogColorBlue = (float)underlavaColor.zCoord;
            }
        }

        float f9 = this.fogColor2 + (this.fogColor1 - this.fogColor2) * partialTicks;
        this.fogColorRed *= f9;
        this.fogColorGreen *= f9;
        this.fogColorBlue *= f9;
        double d2 = (renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * partialTicks) * world.provider.getVoidFogYFactor();
        if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPotionActive(Potion.blindness)) {
            int duration = ((EntityLivingBase)renderViewEntity).getActivePotionEffect(Potion.blindness).getDuration();
            if (duration < 20) {
                d2 *= 1.0F - duration / 20.0F;
            } else {
                d2 = 0.0;
            }
        }

        if (d2 < 1.0) {
            if (d2 < 0.0) {
                d2 = 0.0;
            }

            d2 *= d2;
            this.fogColorRed = (float)(this.fogColorRed * d2);
            this.fogColorGreen = (float)(this.fogColorGreen * d2);
            this.fogColorBlue = (float)(this.fogColorBlue * d2);
        }

        if (this.bossColorModifier > 0.0F) {
            float f10 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
            this.fogColorRed = this.fogColorRed * (1.0F - f10) + this.fogColorRed * 0.7F * f10;
            this.fogColorGreen = this.fogColorGreen * (1.0F - f10) + this.fogColorGreen * 0.6F * f10;
            this.fogColorBlue = this.fogColorBlue * (1.0F - f10) + this.fogColorBlue * 0.6F * f10;
        }

        if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPotionActive(Potion.nightVision)) {
            float nightVisionBrightness = this.getNightVisionBrightness((EntityLivingBase)renderViewEntity, partialTicks);
            float f11 = 1.0F / this.fogColorRed;
            if (f11 > 1.0F / this.fogColorGreen) {
                f11 = 1.0F / this.fogColorGreen;
            }

            if (f11 > 1.0F / this.fogColorBlue) {
                f11 = 1.0F / this.fogColorBlue;
            }

            if (Float.isInfinite(f11)) {
                f11 = Math.nextAfter(f11, 0.0);
            }

            this.fogColorRed = this.fogColorRed * (1.0F - nightVisionBrightness) + this.fogColorRed * f11 * nightVisionBrightness;
            this.fogColorGreen = this.fogColorGreen * (1.0F - nightVisionBrightness) + this.fogColorGreen * f11 * nightVisionBrightness;
            this.fogColorBlue = this.fogColorBlue * (1.0F - nightVisionBrightness) + this.fogColorBlue * f11 * nightVisionBrightness;
        }

        if (this.mc.gameSettings.anaglyph) {
            float f12 = (this.fogColorRed * 30.0F + this.fogColorGreen * 59.0F + this.fogColorBlue * 11.0F) / 100.0F;
            float f13 = (this.fogColorRed * 30.0F + this.fogColorGreen * 70.0F) / 100.0F;
            float f14 = (this.fogColorRed * 30.0F + this.fogColorBlue * 70.0F) / 100.0F;
            this.fogColorRed = f12;
            this.fogColorGreen = f13;
            this.fogColorBlue = f14;
        }

        if (Reflector.EntityViewRenderEvent_FogColors_Constructor.exists()) {
            Object obj = Reflector.newInstance(
                Reflector.EntityViewRenderEvent_FogColors_Constructor, this, renderViewEntity, blockAtEntityViewpoint, partialTicks, this.fogColorRed, this.fogColorGreen, this.fogColorBlue
            );
            Reflector.postForgeBusEvent(obj);
            this.fogColorRed = Reflector.getFieldValueFloat(obj, Reflector.EntityViewRenderEvent_FogColors_red, this.fogColorRed);
            this.fogColorGreen = Reflector.getFieldValueFloat(obj, Reflector.EntityViewRenderEvent_FogColors_green, this.fogColorGreen);
            this.fogColorBlue = Reflector.getFieldValueFloat(obj, Reflector.EntityViewRenderEvent_FogColors_blue, this.fogColorBlue);
        }

        Shaders.setClearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
    }

    private void setupFog(int p_78468_1_, float partialTicks) {
        this.fogStandard = false;
        Entity renderViewEntity = this.mc.getRenderViewEntity();
        boolean flag = false;
        if (renderViewEntity instanceof EntityPlayer) {
            flag = ((EntityPlayer)renderViewEntity).capabilities.isCreativeMode;
        }

        GL11.glFog(2918, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
        GL11.glNormal3f(0.0F, -1.0F, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Block blockAtEntityViewpoint = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, renderViewEntity, partialTicks);
        float f = -1.0F;
        if (Reflector.ForgeHooksClient_getFogDensity.exists()) {
            f = Reflector.callFloat(Reflector.ForgeHooksClient_getFogDensity, this, renderViewEntity, blockAtEntityViewpoint, partialTicks, 0.1F);
        }

        if (f >= 0.0F) {
            GlStateManager.setFogDensity(f);
        } else if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPotionActive(Potion.blindness)) {
            float f2 = 5.0F;
            int duration = ((EntityLivingBase)renderViewEntity).getActivePotionEffect(Potion.blindness).getDuration();
            if (duration < 20) {
                f2 = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - duration / 20.0F);
            }

            GlStateManager.setFog(9729);
            if (p_78468_1_ == -1) {
                GlStateManager.setFogStart(0.0F);
                GlStateManager.setFogEnd(f2 * 0.8F);
            } else {
                GlStateManager.setFogStart(f2 * 0.25F);
                GlStateManager.setFogEnd(f2);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance && Config.isFogFancy()) {
                GL11.glFogi(34138, 34139);
            }
        } else if (this.cloudFog) {
            GlStateManager.setFog(2048);
            GlStateManager.setFogDensity(0.1F);
        } else if (blockAtEntityViewpoint.getMaterial() == Material.water) {
            GlStateManager.setFog(2048);
            float f3 = Config.isClearWater() ? 0.02F : 0.1F;
            if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPotionActive(Potion.waterBreathing)) {
                GlStateManager.setFogDensity(0.01F);
            } else {
                float f4 = 0.1F - EnchantmentHelper.getRespiration(renderViewEntity) * 0.03F;
                GlStateManager.setFogDensity(Config.limit(f4, 0.0F, f3));
            }
        } else if (blockAtEntityViewpoint.getMaterial() == Material.lava) {
            GlStateManager.setFog(2048);
            GlStateManager.setFogDensity(2.0F);
        } else {
            float farPlaneDistance = this.farPlaneDistance;
            this.fogStandard = true;
            GlStateManager.setFog(9729);
            if (p_78468_1_ == -1) {
                GlStateManager.setFogStart(0.0F);
                GlStateManager.setFogEnd(farPlaneDistance);
            } else {
                GlStateManager.setFogStart(farPlaneDistance * Config.getFogStart());
                GlStateManager.setFogEnd(farPlaneDistance);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance) {
                if (Config.isFogFancy()) {
                    GL11.glFogi(34138, 34139);
                }

                if (Config.isFogFast()) {
                    GL11.glFogi(34138, 34140);
                }
            }

            if (this.mc.theWorld.provider.doesXZShowFog((int)renderViewEntity.posX, (int)renderViewEntity.posZ)) {
                GlStateManager.setFogStart(farPlaneDistance * 0.05F);
                GlStateManager.setFogEnd(farPlaneDistance);
            }

            if (Reflector.ForgeHooksClient_onFogRender.exists()) {
                Reflector.callVoid(Reflector.ForgeHooksClient_onFogRender, this, renderViewEntity, blockAtEntityViewpoint, partialTicks, p_78468_1_, farPlaneDistance);
            }
        }

        GlStateManager.enableColorMaterial();
        GlStateManager.enableFog();
        GlStateManager.colorMaterial(1028, 4608);
    }

    private FloatBuffer setFogColorBuffer(float red, float green, float blue, float alpha) {
        if (Config.isShaders()) {
            Shaders.setFogColor(red, green, blue);
        }

        this.fogColorBuffer.clear();
        this.fogColorBuffer.put(red).put(green).put(blue).put(alpha);
        this.fogColorBuffer.flip();
        return this.fogColorBuffer;
    }

    public MapItemRenderer getMapItemRenderer() {
        return this.theMapItemRenderer;
    }

    private void waitForServerThread() {
        this.serverWaitTimeCurrent = 0;
        if (!Config.isSmoothWorld() || !Config.isSingleProcessor()) {
            this.lastServerTime = 0L;
            this.lastServerTicks = 0;
        } else if (this.mc.isIntegratedServerRunning()) {
            IntegratedServer integratedServer = this.mc.getIntegratedServer();
            if (integratedServer != null) {
                boolean gamePaused = this.mc.isGamePaused();
                if (!gamePaused && !(this.mc.currentScreen instanceof GuiDownloadTerrain)) {
                    if (this.serverWaitTime > 0) {
                        Lagometer.timerServer.start();
                        Config.sleep(this.serverWaitTime);
                        Lagometer.timerServer.end();
                        this.serverWaitTimeCurrent = this.serverWaitTime;
                    }

                    long l = System.nanoTime() / 1000000L;
                    if (this.lastServerTime != 0L && this.lastServerTicks != 0) {
                        long l2 = l - this.lastServerTime;
                        if (l2 < 0L) {
                            this.lastServerTime = l;
                            l2 = 0L;
                        }

                        if (l2 >= 50L) {
                            this.lastServerTime = l;
                            int tickCounter = integratedServer.getTickCounter();
                            int n = tickCounter - this.lastServerTicks;
                            if (n < 0) {
                                this.lastServerTicks = tickCounter;
                                n = 0;
                            }

                            if (n < 1 && this.serverWaitTime < 100) {
                                this.serverWaitTime += 2;
                            }

                            if (n > 1 && this.serverWaitTime > 0) {
                                this.serverWaitTime--;
                            }

                            this.lastServerTicks = tickCounter;
                        }
                    } else {
                        this.lastServerTime = l;
                        this.lastServerTicks = integratedServer.getTickCounter();
                        this.avgServerTickDiff = 1.0F;
                        this.avgServerTimeDiff = 50.0F;
                    }
                } else {
                    if (this.mc.currentScreen instanceof GuiDownloadTerrain) {
                        Config.sleep(20L);
                    }

                    this.lastServerTime = 0L;
                    this.lastServerTicks = 0;
                }
            }
        }
    }

    private void frameInit() {
        GlErrors.frameStart();
        if (!this.initialized) {
            TextureUtils.registerResourceListener();
            if (Config.getBitsOs() == 64 && Config.getBitsJre() == 32) {
                Config.setNotify64BitJava(true);
            }

            this.initialized = true;
        }

        Config.checkDisplayMode();
        WorldClient world = this.mc.theWorld;
        if (world != null) {
            if (Config.getNewRelease() != null) {
                String str = "HD_U".replace("HD_U", "HD Ultra").replace("L", "Light");
                String str2 = str + " " + Config.getNewRelease();
                ChatComponentText chatComponentText = new ChatComponentText(I18n.format("of.message.newVersion", "§n" + str2 + "§r"));
                chatComponentText.setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(Action.OPEN_URL, "https://optifine.net/downloads")));
                this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText);
                Config.setNewRelease((String)null);
            }

            if (Config.isNotify64BitJava()) {
                Config.setNotify64BitJava(false);
                ChatComponentText chatComponentText2 = new ChatComponentText(I18n.format("of.message.java64Bit"));
                this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText2);
            }
        }

        if (this.mc.currentScreen instanceof ChangelogScreen) {
            this.updateMainMenu((ChangelogScreen)this.mc.currentScreen);
        }

        if (this.updatedWorld != world) {
            RandomEntities.worldChanged(this.updatedWorld, world);
            Config.updateThreadPriorities();
            this.lastServerTime = 0L;
            this.lastServerTicks = 0;
            this.updatedWorld = world;
        }

        if (!this.setFxaaShader(Shaders.configAntialiasingLevel)) {
            Shaders.configAntialiasingLevel = 0;
        }

        if (this.mc.currentScreen != null && this.mc.currentScreen.getClass() == GuiChat.class) {
            this.mc.displayGuiScreen(new GuiChatOF((GuiChat)this.mc.currentScreen));
        }
    }

    private void frameFinish() {
        if (this.mc.theWorld != null && Config.isShowGlErrors() && TimedEvent.isActive("CheckGlErrorFrameFinish", 10000L)) {
            int n = GlStateManager.glGetError();
            if (n != 0 && GlErrors.isEnabled(n)) {
                String glErrorString = Config.getGlErrorString(n);
                ChatComponentText chatComponentText = new ChatComponentText(I18n.format("of.message.openglError", n, glErrorString));
                this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText);
            }
        }
    }

    private void updateMainMenu(ChangelogScreen p_updateMainMenu_1_) {
        try {
            String str = null;
            Calendar instance = Calendar.getInstance();
            instance.setTime(new Date());
            int n = instance.get(5);
            int n2 = instance.get(2) + 1;
            if (n == 8 && n2 == 4) {
                str = "Happy birthday, OptiFine!";
            }

            if (n == 14 && n2 == 8) {
                str = "Happy birthday, sp614x!";
            }

            if (str == null) {
                return;
            }

            Reflector.setFieldValue(p_updateMainMenu_1_, Reflector.GuiMainMenu_splashText, str);
        } catch (Throwable ignored) {
        }
    }

    public boolean setFxaaShader(int p_setFxaaShader_1_) {
        if (!OpenGlHelper.isFramebufferEnabled()) {
            return false;
        }
        if (this.theShaderGroup != null && this.theShaderGroup != this.fxaaShaders[2] && this.theShaderGroup != this.fxaaShaders[4]) {
            return true;
        }
        if (p_setFxaaShader_1_ != 2 && p_setFxaaShader_1_ != 4) {
            if (this.theShaderGroup == null) {
                return true;
            }
            this.theShaderGroup.deleteShaderGroup();
            this.theShaderGroup = null;
            return true;
        }
        if (this.theShaderGroup != null && this.theShaderGroup == this.fxaaShaders[p_setFxaaShader_1_]) {
            return true;
        }
        if (this.mc.theWorld == null) {
            return true;
        }
        this.loadShader(new ResourceLocation("shaders/post/fxaa_of_" + p_setFxaaShader_1_ + "x.json"));
        this.fxaaShaders[p_setFxaaShader_1_] = this.theShaderGroup;
        return this.useShader;
    }

    private void checkLoadVisibleChunks(Entity p_checkLoadVisibleChunks_1_, float p_checkLoadVisibleChunks_2_, ICamera p_checkLoadVisibleChunks_3_, boolean p_checkLoadVisibleChunks_4_) {
        int n = 201435902;
        if (this.loadVisibleChunks) {
            this.loadVisibleChunks = false;
            this.loadAllVisibleChunks(p_checkLoadVisibleChunks_1_, p_checkLoadVisibleChunks_2_, p_checkLoadVisibleChunks_3_, p_checkLoadVisibleChunks_4_);
            this.mc.ingameGUI.getChatGUI().deleteChatLine(n);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_F3) && Keyboard.isKeyDown(Keyboard.KEY_L)) {
            if (this.mc.currentScreen != null) {
                return;
            }

            this.loadVisibleChunks = true;
            ChatComponentText chatComponentText = new ChatComponentText(I18n.format("of.message.loadingVisibleChunks"));
            this.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(chatComponentText, n);
        }
    }

    private void loadAllVisibleChunks(Entity p_loadAllVisibleChunks_1_, double p_loadAllVisibleChunks_2_, ICamera p_loadAllVisibleChunks_4_, boolean p_loadAllVisibleChunks_5_) {
        int ofChunkUpdates = this.mc.gameSettings.ofChunkUpdates;
        boolean ofLazyChunkLoading = this.mc.gameSettings.ofLazyChunkLoading;

        try {
            this.mc.gameSettings.ofChunkUpdates = 1000;
            this.mc.gameSettings.ofLazyChunkLoading = false;
            RenderGlobal renderGlobal = Config.getRenderGlobal();
            int countLoadedChunks = renderGlobal.getCountLoadedChunks();
            long now = System.currentTimeMillis();
            Config.dbg("Loading visible chunks");
            long l = System.currentTimeMillis() + 5000L;
            int n = 0;
            boolean flag = false;

            do {
                flag = false;

                for (int i = 0; i < 100; i++) {
                    renderGlobal.displayListEntitiesDirty = true;
                    renderGlobal.setupTerrain(p_loadAllVisibleChunks_1_, p_loadAllVisibleChunks_2_, p_loadAllVisibleChunks_4_, this.frameCount++, p_loadAllVisibleChunks_5_);
                    if (!renderGlobal.hasNoChunkUpdates()) {
                        flag = true;
                    }

                    int n2 = n + renderGlobal.getCountChunksToUpdate();

                    while (!renderGlobal.hasNoChunkUpdates()) {
                        renderGlobal.updateChunks(System.nanoTime() + 1000000000L);
                    }

                    n = n2 - renderGlobal.getCountChunksToUpdate();
                    if (!flag) {
                        break;
                    }
                }

                if (renderGlobal.getCountLoadedChunks() != countLoadedChunks) {
                    flag = true;
                    countLoadedChunks = renderGlobal.getCountLoadedChunks();
                }

                if (System.currentTimeMillis() > l) {
                    Config.log("Chunks loaded: " + n);
                    l = System.currentTimeMillis() + 5000L;
                }
            } while (flag);

            Config.log("Chunks loaded: " + n);
            Config.log("Finished loading visible chunks");
            RenderChunk.renderChunksUpdated = 0;
        } finally {
            this.mc.gameSettings.ofChunkUpdates = ofChunkUpdates;
            this.mc.gameSettings.ofLazyChunkLoading = ofLazyChunkLoading;
        }
    }

    public void setThirdPersonDistance(float distance) {
        this.thirdPersonDistance = distance;
        this.thirdPersonDistanceTemp = distance;
    }

    public void resetThirdPersonDistance() {
        this.thirdPersonDistance = 4.0F;
        this.thirdPersonDistanceTemp = 4.0F;
    }

    public float getThirdPersonDistance() {
        return this.thirdPersonDistance;
    }
}
