package net.minecraft.client;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import de.florianmichael.viamcp.fixes.AttackOrder;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.imageio.ImageIO;
import lombok.Generated;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.stream.GuiStreamUnavailable;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.stream.IStream;
import net.minecraft.client.stream.NullStream;
import net.minecraft.client.stream.TwitchStream;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.profiler.Profiler.Result;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MinecraftError;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Util.EnumOS;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brutality.Brutality;
import org.brutality.command.impl.BindCommand;
import org.brutality.core.ConnectToServerHandler;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.BasicEvent;
import org.brutality.event.impl.basic.BasicEvent2;
import org.brutality.event.impl.basic.BasicEvent3;
import org.brutality.event.impl.basic.BasicEvent4;
import org.brutality.event.impl.player.EventKey;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.player.PlayerEvent3;
import org.brutality.event.impl.player.PlayerEvent4;
import org.brutality.event.impl.render.RenderEvent3;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.gui.ChangelogScreen;
import org.brutality.gui.FinishScreen;
import org.brutality.gui.macro.OnKeyPressedComponent;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.client.DiscordRPC;
import org.brutality.module.impl.combat.AutoClicker;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.movement.KeepSprint;
import org.brutality.module.impl.movement.MovementFix;
import org.brutality.module.impl.movement.Scaffold;
import org.brutality.module.impl.pit.hypixel.utility.PitDebugger;
import org.brutality.module.impl.player.BedAura;
import org.brutality.system.ClientMetaManager;
import org.brutality.system.ItemNameManager;
import org.brutality.utils.UtilsHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.misc.ColorMathUtil;
import org.brutality.utils.modules.CountMovementHelper;
import org.brutality.utils.performance.EntityCache;
import org.brutality.utils.performance.WorldQueryCache;
import org.brutality.utils.performance.PrintCopyPasteReportHelper;
import org.brutality.utils.player.HotbarHelper;
import org.brutality.utils.player.StartHotbarMovesHelper;
import org.brutality.utils.render.BorderlessWindow;
import org.brutality.utils.render.RenderHelper;
import org.brutality.utils.rotation.OnJumpHelper;
import org.brutality.utils.world.WorldDownloadController;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

public class Minecraft implements IPlayerUsage, IThreadListener {
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation locationMojangPng = new ResourceLocation("textures/gui/title/mojang.png");
    public static final boolean isRunningOnMac = Util.getOSType() == EnumOS.OSX;
    public static byte[] memoryReserve = new byte[10485760];
    private static final List<DisplayMode> macDisplayModes = Lists.newArrayList(new DisplayMode(2560, 1600), new DisplayMode(2880, 1800));
    private final File fileResourcepacks;
    private final PropertyMap twitchDetails;
    private final PropertyMap field_181038_N;
    private ServerData currentServerData;
    private TextureManager renderEngine;
    private static Minecraft theMinecraft;
    public PlayerControllerMP playerController;
    private boolean fullscreen;
    private final BorderlessWindow borderlessWindow = new BorderlessWindow();
    private boolean enableGLErrorChecking = true;
    private boolean hasCrashed;
    private CrashReport crashReporter;
    public int displayWidth;
    public int displayHeight;
    private boolean field_181541_X = false;
    public Timer timer = new Timer(20.0F);
    private PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("client", this, MinecraftServer.getCurrentTimeMillis());
    public WorldClient theWorld;
    public RenderGlobal renderGlobal;
    private RenderManager renderManager;
    private RenderItem renderItem;
    private ItemRenderer itemRenderer;
    public EntityPlayerSP thePlayer;
    private Entity renderViewEntity;
    public Entity pointedEntity;
    public EffectRenderer effectRenderer;
    public Session session;
    private boolean isGamePaused;
    private boolean brutalityCommandChatKeyDown;
    public FontRenderer fontRendererObj;
    public FontRenderer standardGalacticFontRenderer;
    public GuiScreen currentScreen;
    public LoadingScreenRenderer loadingScreen;
    public EntityRenderer entityRenderer;
    public int leftClickCounter;
    private int tempDisplayWidth;
    private int tempDisplayHeight;
    private IntegratedServer theIntegratedServer;
    public GuiAchievement guiAchievement;
    public GuiIngame ingameGUI;
    public boolean skipRenderWorld;
    public MovingObjectPosition objectMouseOver;
    public GameSettings gameSettings;
    public MouseHelper mouseHelper;
    public final File mcDataDir;
    private final File fileAssets;
    final String launchedVersion;
    private final Proxy proxy;
    private ISaveFormat saveLoader;
    private static int debugFPS;
    public int rightClickDelayTimer;
    private final boolean[] hotbarPreviousStates = new boolean[9];
    private String serverName;
    private int serverPort;
    public boolean inGameHasFocus;
    long systemTime = getSystemTime();
    private int joinPlayerCounter;
    public final FrameTimer field_181542_y = new FrameTimer();
    long field_181543_z = System.nanoTime();
    private final boolean jvm64bit;
    private final boolean isDemo;
    private NetworkManager myNetworkManager;
    private boolean integratedServerIsRunning;
    public final Profiler mcProfiler = new Profiler();
    private long debugCrashKeyPressTime = -1L;
    private IReloadableResourceManager mcResourceManager;
    private final IMetadataSerializer metadataSerializer_ = new IMetadataSerializer();
    private final List<IResourcePack> defaultResourcePacks = Lists.newArrayList();
    private final DefaultResourcePack mcDefaultResourcePack;
    private ResourcePackRepository mcResourcePackRepository;
    LanguageManager mcLanguageManager;
    private IStream stream;
    private Framebuffer framebufferMc;
    private TextureMap textureMapBlocks;
    private SoundHandler mcSoundHandler;
    private MusicTicker mcMusicTicker;
    private ResourceLocation mojangLogo;
    private final MinecraftSessionService sessionService;
    private SkinManager skinManager;
    private final Queue<FutureTask<?>> scheduledTasks = Queues.newArrayDeque();
    private long field_175615_aJ = 0L;
    private final Thread mcThread = Thread.currentThread();
    private ModelManager modelManager;
    private BlockRendererDispatcher blockRenderDispatcher;
    volatile boolean running = true;
    public String debug = "";
    public boolean field_175613_B = false;
    public boolean field_175614_C = false;
    public boolean field_175611_D = false;
    public boolean renderChunksMany = true;
    long debugUpdateTime = getSystemTime();
    int fpsCounter;
    long prevFrameTime = -1L;
    private String debugProfilerName = "root";
    private boolean launcherGrinderStarted;
    private long launcherPitJoinAttempt;
    private int launcherPitJoinRetries;

    public Minecraft(GameConfiguration gameConfig) {
        theMinecraft = this;
        this.mcDataDir = gameConfig.folderInfo.mcDataDir;
        this.fileAssets = gameConfig.folderInfo.assetsDir;
        this.fileResourcepacks = gameConfig.folderInfo.resourcePacksDir;
        this.launchedVersion = gameConfig.gameInfo.version;
        this.twitchDetails = gameConfig.userInfo.userProperties;
        this.field_181038_N = gameConfig.userInfo.field_181172_c;
        this.mcDefaultResourcePack = new DefaultResourcePack(new ResourceIndex(gameConfig.folderInfo.assetsDir, gameConfig.folderInfo.assetIndex).getResourceMap());
        this.proxy = gameConfig.userInfo.proxy == null ? Proxy.NO_PROXY : gameConfig.userInfo.proxy;
        this.sessionService = new YggdrasilAuthenticationService(gameConfig.userInfo.proxy, UUID.randomUUID().toString()).createMinecraftSessionService();
        this.session = gameConfig.userInfo.session;
        logger.info("Setting user: " + this.session.getUsername());
        logger.info("Minecraft session initialized for " + this.session.getUsername());
        this.isDemo = gameConfig.gameInfo.isDemo;
        this.displayWidth = gameConfig.displayInfo.width > 0 ? gameConfig.displayInfo.width : 1;
        this.displayHeight = gameConfig.displayInfo.height > 0 ? gameConfig.displayInfo.height : 1;
        this.tempDisplayWidth = gameConfig.displayInfo.width;
        this.tempDisplayHeight = gameConfig.displayInfo.height;
        this.fullscreen = gameConfig.displayInfo.fullscreen;
        this.jvm64bit = isJvm64bit();
        this.theIntegratedServer = new IntegratedServer(this);
        if (gameConfig.serverInfo.serverName != null) {
            this.serverName = gameConfig.serverInfo.serverName;
            this.serverPort = gameConfig.serverInfo.serverPort;
        }

        ImageIO.setUseCache(false);
        Bootstrap.register();
    }

    public static GameSettings getGameSettings() {
        return getMinecraft().gameSettings;
    }

    public static FontRenderer getFontRenderer() {
        return getMinecraft().fontRendererObj;
    }

    public void run() {
        this.running = true;

        try {
            this.startGame();
        } catch (Throwable e) {
            CrashReport crashReport = CrashReport.makeCrashReport(e, "Initializing game");
            crashReport.makeCategory("Initialization");
            this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(crashReport));
            return;
        }

        try {
            while (this.running) {
                if (this.hasCrashed && this.crashReporter != null) {
                    this.displayCrashReport(this.crashReporter);
                } else {
                    try {
                        this.runGameLoop();
                    } catch (OutOfMemoryError e2) {
                        this.freeMemory();
                        this.displayGuiScreen(new GuiMemoryErrorScreen());
                        System.gc();
                    }
                }
            }

            return;
        } catch (MinecraftError ignored) {
        } catch (ReportedException e3) {
            this.addGraphicsAndWorldToCrashReport(e3.getCrashReport());
            this.freeMemory();
            logger.fatal("Reported exception thrown!", (Throwable)e3);
            this.displayCrashReport(e3.getCrashReport());
        } catch (Throwable e4) {
            CrashReport crashReport2 = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", e4));
            this.freeMemory();
            logger.fatal("Unreported exception thrown!", e4);
            this.displayCrashReport(crashReport2);
        } finally {
            this.shutdownMinecraftApplet();
        }
    }

    private void startGame() throws LWJGLException, IOException {
        this.gameSettings = new GameSettings(this, this.mcDataDir);
        this.defaultResourcePacks.add(this.mcDefaultResourcePack);
        this.startTimerHackThread();
        if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0) {
            this.displayWidth = this.gameSettings.overrideWidth;
            this.displayHeight = this.gameSettings.overrideHeight;
        }

        logger.info("LWJGL Version: " + Sys.getVersion());
        this.setWindowIcon();
        this.setInitialDisplayMode();
        this.createDisplay();
        OpenGlHelper.initializeTextures();
        logger.info("[Brutality] Preparing authentication system...");
        this.framebufferMc = new Framebuffer(this.displayWidth, this.displayHeight, true);
        this.framebufferMc.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        this.registerMetadataSerializers();
        this.mcResourcePackRepository = new ResourcePackRepository(
            this.fileResourcepacks, new File(this.mcDataDir, "server-resource-packs"), this.mcDefaultResourcePack, this.metadataSerializer_, this.gameSettings
        );
        this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
        this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
        this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
        this.refreshResources();
        this.renderEngine = new TextureManager(this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.renderEngine);
        this.drawSplashScreen(this.renderEngine);
        this.initStream();
        this.skinManager = new SkinManager(this.renderEngine, new File(this.fileAssets, "skins"), this.sessionService);
        this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"));
        this.mcSoundHandler = new SoundHandler(this.mcResourceManager, this.gameSettings);
        this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
        this.mcMusicTicker = new MusicTicker(this);
        this.fontRendererObj = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);
        if (this.gameSettings.language != null) {
            this.fontRendererObj.setUnicodeFlag(this.isUnicode());
            this.fontRendererObj.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
        }

        this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
        this.mcResourceManager.registerReloadListener(this.fontRendererObj);
        this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
        this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
        this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
        AchievementList.openInventory.setStatStringFormatter(new IStatStringFormat() {
            @Override
            public String formatString(String p_74535_1_) {
                try {
                    return String.format(p_74535_1_, GameSettings.getKeyDisplayString(Minecraft.this.gameSettings.keyBindInventory.getKeyCode()));
                } catch (Exception e) {
                    return "Error: " + e.getLocalizedMessage();
                }
            }
        });
        this.mouseHelper = new MouseHelper();
        this.checkGLError("Pre startup");
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.clearDepth(1.0);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.cullFace(GL11.GL_BACK);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        this.checkGLError("Startup");
        this.textureMapBlocks = new TextureMap("textures");
        this.textureMapBlocks.setMipmapLevels(this.gameSettings.mipmapLevels);
        this.renderEngine.loadTickableTexture(TextureMap.locationBlocksTexture, this.textureMapBlocks);
        this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        this.textureMapBlocks.setBlurMipmapDirect(false, this.gameSettings.mipmapLevels > 0);
        this.modelManager = new ModelManager(this.textureMapBlocks);
        this.mcResourceManager.registerReloadListener(this.modelManager);
        this.renderItem = new RenderItem(this.renderEngine, this.modelManager);
        this.renderManager = new RenderManager(this.renderEngine, this.renderItem);
        this.itemRenderer = new ItemRenderer(this);
        this.mcResourceManager.registerReloadListener(this.renderItem);
        this.entityRenderer = new EntityRenderer(this, this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.entityRenderer);
        this.blockRenderDispatcher = new BlockRendererDispatcher(this.modelManager.getBlockModelShapes(), this.gameSettings);
        this.mcResourceManager.registerReloadListener(this.blockRenderDispatcher);
        this.renderGlobal = new RenderGlobal(this);
        this.mcResourceManager.registerReloadListener(this.renderGlobal);
        this.guiAchievement = new GuiAchievement(this);
        GlStateManager.viewport(0, 0, this.displayWidth, this.displayHeight);
        this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);
        this.checkGLError("Post startup");
        this.ingameGUI = new GuiIngame(this);
        logger.info("[BrutalityDebug] Entering Brutality.startClient()");
        Brutality.startClient();
        logger.info("[Brutality] Authentication initialised.");
        logger.info("[BrutalityDebug] Brutality.startClient() returned; selecting initial screen");
        if (this.serverName != null) {
            this.displayGuiScreen(new GuiConnecting(new ChangelogScreen(), this, this.serverName, this.serverPort));
        } else if (ClientMetaManager.checkState()) {
            this.displayGuiScreen(new FinishScreen(new ChangelogScreen()));
        } else {
            logger.info("[BrutalityDebug] Displaying MainMenu");
            this.displayGuiScreen(new ChangelogScreen());
            logger.info("[BrutalityDebug] MainMenu assigned");
        }

        try {
            logger.info(
                "[BrutalityDebug] Display state: created="
                    + Display.isCreated()
                    + ", visible="
                    + Display.isVisible()
                    + ", active="
                    + Display.isActive()
                    + ", closeRequested="
                    + Display.isCloseRequested()
                    + ", size="
                    + Display.getWidth()
                    + "x"
                    + Display.getHeight()
                    + ", fullscreen="
                    + Display.isFullscreen()
            );
        } catch (Exception e2) {
            logger.warn("[BrutalityDebug] Could not restore the game window", (Throwable)e2);
        }

        this.renderEngine.deleteTexture(this.mojangLogo);
        this.mojangLogo = null;
        this.loadingScreen = new LoadingScreenRenderer(this);
        if (this.gameSettings.fullScreen && !this.fullscreen) {
            this.toggleFullscreen();
        }

        try {
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
        } catch (OpenGLException e3) {
            this.gameSettings.enableVsync = false;
            this.gameSettings.saveOptions();
        }

        this.renderGlobal.makeEntityOutlineShader();
    }

    private void registerMetadataSerializers() {
        this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
    }

    private void initStream() {
        try {
            this.stream = new TwitchStream(this, Iterables.getFirst(this.twitchDetails.get("twitch_access_token"), null));
        } catch (Throwable e) {
            this.stream = new NullStream(e);
            logger.error("Couldn't initialize twitch stream");
        }
    }

    private void createDisplay() throws LWJGLException {
        boolean flag = Util.getOSType() == EnumOS.OSX;
        if (!flag) {
            Display.setResizable(!this.isBorderlessFullscreen());
        }

        Display.setTitle("Loading Brutality");

        try {
            Display.create(new PixelFormat().withDepthBits(24));
        } catch (LWJGLException e) {
            logger.error("Couldn't set pixel format", (Throwable)e);

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ignored) {
            }

            if (this.fullscreen) {
                this.updateDisplayMode();
            }

            Display.create();
        }

        if (flag) {
            try {
                Display.setResizable(true);
            } catch (Exception e2) {
                logger.warn("Could not set window resizable: " + e2.getMessage());
            }
        }

        if (!this.fullscreen && (Display.getWidth() <= 0 || Display.getHeight() <= 0)) {
            int n = Math.max(854, this.displayWidth);
            int n2 = Math.max(480, this.displayHeight);
            logger.warn("Invalid LWJGL display size " + Display.getWidth() + "x" + Display.getHeight() + "; restoring " + n + "x" + n2);
            Display.setDisplayMode(new DisplayMode(n + 1, n2 + 1));
            Display.setDisplayMode(new DisplayMode(n, n2));
            this.displayWidth = n;
            this.displayHeight = n2;
        }
    }

    private void setInitialDisplayMode() throws LWJGLException {
        if (this.fullscreen && this.gameSettings.borderlessFullscreen && !isRunningOnMac) {
            this.borderlessWindow.handleInt(this.displayWidth, this.displayHeight);
            this.displayWidth = Display.getDisplayMode().getWidth();
            this.displayHeight = Display.getDisplayMode().getHeight();
        } else {
            if (this.fullscreen) {
                Display.setFullscreen(true);
                DisplayMode displayMode = Display.getDisplayMode();
                this.displayWidth = Math.max(1, displayMode.getWidth());
                this.displayHeight = Math.max(1, displayMode.getHeight());
            } else {
                Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
            }
        }
    }

    private void setWindowIcon() {
        EnumOS osType = Util.getOSType();
        if (osType != EnumOS.OSX) {
            InputStream in = null;
            InputStream in2 = null;

            try {
                in = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
                in2 = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));
                if (in != null && in2 != null) {
                    Display.setIcon(new ByteBuffer[]{this.readImageToBuffer(in), this.readImageToBuffer(in2)});
                }
            } catch (IOException e) {
                logger.error("Couldn't set icon", (Throwable)e);
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(in2);
            }
        }
    }

    private static boolean isJvm64bit() {
        String[] parts = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

        for (String str : parts) {
            String property = System.getProperty(str);
            if (property != null && property.contains("64")) {
                return true;
            }
        }

        return false;
    }

    public Framebuffer getFramebuffer() {
        return this.framebufferMc;
    }

    public String getVersion() {
        return this.launchedVersion;
    }

    private void startTimerHackThread() {
        Thread thread = new Thread("Timer hack thread") {
            @Override
            public void run() {
                while (Minecraft.this.running) {
                    try {
                        Thread.sleep(2147483647L);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public void crashed(CrashReport crash) {
        this.hasCrashed = true;
        this.crashReporter = crash;
    }

    public void displayCrashReport(CrashReport crashReportIn) {
        File file = new File(getMinecraft().mcDataDir, "crash-reports");
        File file2 = new File(file, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-client.txt");
        Bootstrap.printToSYSOUT(crashReportIn.getCompleteReport());
        if (crashReportIn.getFile() != null) {
            Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReportIn.getFile());
            System.exit(-1);
        } else if (crashReportIn.saveToFile(file2)) {
            Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
            System.exit(-1);
        } else {
            Bootstrap.printToSYSOUT("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            System.exit(-2);
        }
    }

    public boolean isUnicode() {
        return this.mcLanguageManager != null && this.mcLanguageManager.isCurrentLocaleUnicode()
            || this.gameSettings != null && this.gameSettings.forceUnicodeFont;
    }

    public void refreshResources() {
        List list = Lists.newArrayList(this.defaultResourcePacks);

        for (Entry repositoryEntry : this.mcResourcePackRepository.getRepositoryEntries()) {
            list.add(repositoryEntry.getResourcePack());
        }

        if (this.mcResourcePackRepository.getResourcePackInstance() != null) {
            list.add(this.mcResourcePackRepository.getResourcePackInstance());
        }

        try {
            this.mcResourceManager.reloadResources(list);
        } catch (RuntimeException e) {
            logger.info("Caught error stitching, removing all assigned resourcepacks", (Throwable)e);
            list.clear();
            list.addAll(this.defaultResourcePacks);
            this.mcResourcePackRepository.setRepositories(Collections.emptyList());
            this.mcResourceManager.reloadResources(list);
            this.gameSettings.resourcePacks.clear();
            this.gameSettings.field_183018_l.clear();
            this.gameSettings.saveOptions();
        }

        this.mcLanguageManager.parseLanguageMetadata(list);
        if (this.renderGlobal != null) {
            this.renderGlobal.loadRenderers();
        }
    }

    private ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException {
        BufferedImage image = ImageIO.read(imageStream);
        int[] rgb = image.getRGB(0, 0, image.getWidth(), image.getHeight(), (int[])null, 0, image.getWidth());
        ByteBuffer buffer = ByteBuffer.allocate(4 * rgb.length);

        for (int n : rgb) {
            buffer.putInt(n << 8 | n >> 24 & 0xFF);
        }

        buffer.flip();
        return buffer;
    }

    private void updateDisplayMode() throws LWJGLException {
        if (!this.isBorderlessFullscreen()) {
            HashSet<DisplayMode> displayModes = Sets.newHashSet();
            Collections.addAll(displayModes, Display.getAvailableDisplayModes());
            DisplayMode desktopDisplayMode = Display.getDesktopDisplayMode();
            if (!displayModes.contains(desktopDisplayMode) && Util.getOSType() == EnumOS.OSX) {
                for (DisplayMode macDisplayMode : macDisplayModes) {
                    boolean flag = true;

                    for (DisplayMode displayMode : displayModes) {
                        if (displayMode.getBitsPerPixel() == 32 && displayMode.getWidth() == macDisplayMode.getWidth() && displayMode.getHeight() == macDisplayMode.getHeight()) {
                            flag = false;
                            break;
                        }
                    }

                    if (!flag) {
                        for (DisplayMode displayMode2 : ((Iterable<DisplayMode>)(Object)(displayModes))) {
                            if (displayMode2.getBitsPerPixel() == 32 && displayMode2.getWidth() == macDisplayMode.getWidth() / 2 && displayMode2.getHeight() == macDisplayMode.getHeight() / 2) {
                                desktopDisplayMode = displayMode2;
                                break;
                            }
                        }
                    }
                }
            }

            Display.setDisplayMode(desktopDisplayMode);
            this.displayWidth = desktopDisplayMode.getWidth();
            this.displayHeight = desktopDisplayMode.getHeight();
        }
    }

    private void drawSplashScreen(TextureManager textureManagerInstance) throws LWJGLException {
        ScaledResolution sr = new ScaledResolution(this);
        int scaleFactor = sr.getScaleFactor();
        Framebuffer framebuffer = new Framebuffer(sr.getScaledWidth() * scaleFactor, sr.getScaledHeight() * scaleFactor, true);
        framebuffer.bindFramebuffer(false);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, sr.getScaledWidth(), sr.getScaledHeight(), 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        InputStream in = null;

        try {
            in = this.mcDefaultResourcePack.getInputStream(locationMojangPng);
            this.mojangLogo = textureManagerInstance.getDynamicTextureLocation("logo", new DynamicTexture(ImageIO.read(in)));
            textureManagerInstance.bindTexture(this.mojangLogo);
        } catch (IOException e) {
            logger.error("Unable to load logo: " + locationMojangPng, (Throwable)e);
        } finally {
            IOUtils.closeQuietly(in);
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
        worldRenderer.func_181662_b(0.0, this.displayHeight, 0.0).func_181673_a(0.0, 0.0).func_181669_b(255, 255, 255, 255).func_181675_d();
        worldRenderer.func_181662_b(this.displayWidth, this.displayHeight, 0.0).func_181673_a(0.0, 0.0).func_181669_b(255, 255, 255, 255).func_181675_d();
        worldRenderer.func_181662_b(this.displayWidth, 0.0, 0.0).func_181673_a(0.0, 0.0).func_181669_b(255, 255, 255, 255).func_181675_d();
        worldRenderer.func_181662_b(0.0, 0.0, 0.0).func_181673_a(0.0, 0.0).func_181669_b(255, 255, 255, 255).func_181675_d();
        tessellator.draw();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        short s = 256;
        short s2 = 256;
        this.func_181536_a((sr.getScaledWidth() - s) / 2, (sr.getScaledHeight() - s2) / 2, 0, 0, s, s2, 255, 255, 255, 255);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(sr.getScaledWidth() * scaleFactor, sr.getScaledHeight() * scaleFactor);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        this.updateDisplay();
    }

    public void func_181536_a(int p_181536_1_, int p_181536_2_, int p_181536_3_, int p_181536_4_, int p_181536_5_, int p_181536_6_, int p_181536_7_, int p_181536_8_, int p_181536_9_, int p_181536_10_) {
        float f = 0.00390625F;
        float f2 = 0.00390625F;
        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
        worldRenderer.func_181662_b(p_181536_1_, p_181536_2_ + p_181536_6_, 0.0).func_181673_a(p_181536_3_ * f, (p_181536_4_ + p_181536_6_) * f2).func_181669_b(p_181536_7_, p_181536_8_, p_181536_9_, p_181536_10_).func_181675_d();
        worldRenderer.func_181662_b(p_181536_1_ + p_181536_5_, p_181536_2_ + p_181536_6_, 0.0)
            .func_181673_a((p_181536_3_ + p_181536_5_) * f, (p_181536_4_ + p_181536_6_) * f2)
            .func_181669_b(p_181536_7_, p_181536_8_, p_181536_9_, p_181536_10_)
            .func_181675_d();
        worldRenderer.func_181662_b(p_181536_1_ + p_181536_5_, p_181536_2_, 0.0).func_181673_a((p_181536_3_ + p_181536_5_) * f, p_181536_4_ * f2).func_181669_b(p_181536_7_, p_181536_8_, p_181536_9_, p_181536_10_).func_181675_d();
        worldRenderer.func_181662_b(p_181536_1_, p_181536_2_, 0.0).func_181673_a(p_181536_3_ * f, p_181536_4_ * f2).func_181669_b(p_181536_7_, p_181536_8_, p_181536_9_, p_181536_10_).func_181675_d();
        Tessellator.getInstance().draw();
    }

    public ISaveFormat getSaveLoader() {
        return this.saveLoader;
    }

    public void displayGuiScreen(GuiScreen guiScreenIn) {
        if (this.currentScreen != null) {
            this.currentScreen.onGuiClosed();
        }

        if (guiScreenIn == null && this.theWorld == null) {
            guiScreenIn = new ChangelogScreen();
        } else if (guiScreenIn == null && this.thePlayer.getHealth() <= 0.0F) {
            guiScreenIn = new GuiGameOver();
        }

        if (guiScreenIn instanceof ChangelogScreen) {
            this.gameSettings.showDebugInfo = false;
            this.ingameGUI.getChatGUI().clearChatMessages();
        }

        this.currentScreen = (GuiScreen)guiScreenIn;
        if (guiScreenIn != null) {
            this.setIngameNotInFocus();
            KillAura instance = KillAura.instance;
            if (instance != null && this.thePlayer != null) {
                instance.process16();
            }

            ScaledResolution sr = new ScaledResolution(this);
            int scaledWidth = sr.getScaledWidth();
            int scaledHeight = sr.getScaledHeight();
            ((GuiScreen)guiScreenIn).setWorldAndResolution(this, scaledWidth, scaledHeight);
            this.skipRenderWorld = false;
        } else {
            this.mcSoundHandler.resumeSounds();
            this.setIngameFocus();
        }
    }

    private void checkGLError(String message) {
        if (this.enableGLErrorChecking) {
            int n = GL11.glGetError();
            if (n != 0) {
                String str = GLU.gluErrorString(n);
                logger.error("########## GL ERROR ##########");
                logger.error("@ " + message);
                logger.error(n + ": " + str);
            }
        }
    }

    public void shutdownMinecraftApplet() {
        PitDebugger.process5();
        WorldDownloadController.shutdown();

        try {
            this.stream.shutdownStream();
            logger.info("Stopping!");

            try {
                this.loadWorld((WorldClient)null);
            } catch (Throwable ignored) {
            }

            this.mcSoundHandler.unloadSounds();
        } finally {
            Display.destroy();
            if (!this.hasCrashed) {
                System.exit(0);
            }
        }

        System.gc();
    }

    private void runGameLoop() throws IOException {
        if (PrintCopyPasteReportHelper.isEnabled()) {
            PrintCopyPasteReportHelper.process3();
        }

        long now = System.nanoTime();
        this.mcProfiler.startSection("root");
        if (Display.isCreated() && Display.isCloseRequested()) {
            this.shutdown();
        }

        if (this.isGamePaused && this.theWorld != null) {
            float renderPartialTicks = this.timer.renderPartialTicks;
            this.timer.updateTimer();
            this.timer.renderPartialTicks = renderPartialTicks;
        } else {
            this.timer.updateTimer();
        }

        this.mcProfiler.startSection("scheduledExecutables");
        synchronized (this.scheduledTasks) {
            while (!this.scheduledTasks.isEmpty()) {
                Util.func_181617_a(this.scheduledTasks.poll(), logger);
            }
        }

        this.mcProfiler.endSection();
        long now2 = System.nanoTime();
        this.mcProfiler.startSection("tick");

        for (int i = 0; i < this.timer.elapsedTicks; i++) {
            this.runTick();
        }

        this.mcProfiler.endStartSection("preRenderErrors");
        long l = System.nanoTime() - now2;
        this.checkGLError("Pre render");
        this.mcProfiler.endStartSection("sound");
        this.mcSoundHandler.setListener(this.thePlayer, this.timer.renderPartialTicks);
        this.mcProfiler.endSection();
        this.mcProfiler.startSection("render");
        GlStateManager.pushMatrix();
        GlStateManager.clear(16640);
        this.framebufferMc.bindFramebuffer(true);
        this.mcProfiler.startSection("display");
        GlStateManager.enableTexture2D();
        if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock()) {
            this.gameSettings.thirdPersonView = 0;
        }

        this.mcProfiler.endSection();
        if (!this.skipRenderWorld) {
            this.mcProfiler.endStartSection("gameRenderer");
            this.entityRenderer.func_181560_a(this.timer.renderPartialTicks, now);
            this.mcProfiler.endSection();
        }

        this.mcProfiler.endSection();
        if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart && !this.gameSettings.hideGUI) {
            if (!this.mcProfiler.profilingEnabled) {
                this.mcProfiler.clearProfiling();
            }

            this.mcProfiler.profilingEnabled = true;
            this.displayDebugInfo(l);
        } else {
            this.mcProfiler.profilingEnabled = false;
            this.prevFrameTime = System.nanoTime();
        }

        this.guiAchievement.updateAchievementWindow();
        this.framebufferMc.unbindFramebuffer();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.framebufferMc.framebufferRender(this.displayWidth, this.displayHeight);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.entityRenderer.renderStreamIndicator(this.timer.renderPartialTicks);
        GlStateManager.popMatrix();
        if (AutoClicker.instance != null && AutoClicker.instance.checkState5()) {
            new RenderEvent3().process();
        }

        this.mcProfiler.startSection("root");
        this.updateDisplay();
        Thread.yield();
        this.mcProfiler.startSection("stream");
        this.mcProfiler.startSection("update");
        this.stream.func_152935_j();
        this.mcProfiler.endStartSection("submit");
        this.stream.func_152922_k();
        this.mcProfiler.endSection();
        this.mcProfiler.endSection();
        this.checkGLError("Post render");
        if (PrintCopyPasteReportHelper.isEnabled()) {
            PrintCopyPasteReportHelper.process4();
        }

        this.fpsCounter++;
        this.isGamePaused = this.isSingleplayer()
            && this.currentScreen != null
            && this.currentScreen.doesGuiPauseGame()
            && !this.theIntegratedServer.getPublic();
        long now3 = System.nanoTime();
        this.field_181542_y.func_181747_a(now3 - this.field_181543_z);
        this.field_181543_z = now3;

        while (getSystemTime() >= this.debugUpdateTime + 1000L) {
            debugFPS = this.fpsCounter;
            this.debug = String.format(
                "%d fps (%d chunk update%s) T: %s%s%s%s%s",
                debugFPS,
                RenderChunk.renderChunksUpdated,
                RenderChunk.renderChunksUpdated != 1 ? "s" : "",
                this.gameSettings.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() ? "inf" : this.gameSettings.limitFramerate,
                this.gameSettings.enableVsync ? " vsync" : "",
                this.gameSettings.fancyGraphics ? "" : " fast",
                this.gameSettings.clouds == 0 ? "" : (this.gameSettings.clouds == 1 ? " fast-clouds" : " fancy-clouds"),
                OpenGlHelper.useVbo() ? " vbo" : ""
            );
            RenderChunk.renderChunksUpdated = 0;
            this.debugUpdateTime += 1000L;
            this.fpsCounter = 0;
            this.usageSnooper.addMemoryStatsToSnooper();
            if (!this.usageSnooper.isSnooperRunning()) {
                this.usageSnooper.startSnooper();
            }
        }

        if (this.isFramerateLimitBelowMax()) {
            this.mcProfiler.startSection("fpslimit_wait");
            Display.sync(this.getLimitFramerate());
            this.mcProfiler.endSection();
        }

        this.mcProfiler.endSection();
    }

    public void updateDisplay() {
        this.mcProfiler.startSection("display_update");
        Display.update();
        this.mcProfiler.endSection();
        this.checkWindowResize();
    }

    protected void checkWindowResize() {
        if (!this.fullscreen && Display.wasResized()) {
            if (Display.getWidth() <= 0 || Display.getHeight() <= 0) {
                return;
            }

            int width = this.displayWidth;
            int height = this.displayHeight;
            this.displayWidth = Display.getWidth();
            this.displayHeight = Display.getHeight();
            if (this.displayWidth != width || this.displayHeight != height) {
                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }

                this.resize(this.displayWidth, this.displayHeight);
            }
        }
    }

    public int getLimitFramerate() {
        return this.theWorld == null && this.currentScreen != null ? 60 : this.gameSettings.limitFramerate;
    }

    public boolean isFramerateLimitBelowMax() {
        return this.getLimitFramerate() < GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
    }

    public void freeMemory() {
        try {
            memoryReserve = new byte[0];
            this.renderGlobal.deleteAllDisplayLists();
        } catch (Throwable ignored) {
        }

        try {
            System.gc();
            this.loadWorld((WorldClient)null);
        } catch (Throwable ignored2) {
        }

        System.gc();
    }

    private void updateDebugProfilerName(int keyCount) {
        List profilingData = this.mcProfiler.getProfilingData(this.debugProfilerName);
        if (profilingData != null && !profilingData.isEmpty()) {
            Result result = (Result)profilingData.remove(0);
            if (keyCount == 0) {
                if (result.field_76331_c.length() > 0) {
                    int n = this.debugProfilerName.lastIndexOf(".");
                    if (n >= 0) {
                        this.debugProfilerName = this.debugProfilerName.substring(0, n);
                    }
                }
            } else {
                keyCount--;
                if (keyCount < profilingData.size() && !((Result)profilingData.get(keyCount)).field_76331_c.equals("unspecified")) {
                    if (this.debugProfilerName.length() > 0) {
                        this.debugProfilerName = this.debugProfilerName + ".";
                    }

                    this.debugProfilerName = this.debugProfilerName + ((Result)profilingData.get(keyCount)).field_76331_c;
                }
            }
        }
    }

    private void displayDebugInfo(long elapsedTicksTime) {
        if (this.mcProfiler.profilingEnabled) {
            List profilingData = this.mcProfiler.getProfilingData(this.debugProfilerName);
            Result result = (Result)profilingData.remove(0);
            GlStateManager.clear(256);
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.enableColorMaterial();
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0, this.displayWidth, this.displayHeight, 0.0, 1000.0, 3000.0);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -2000.0F);
            GL11.glLineWidth(1.0F);
            GlStateManager.disableTexture2D();
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            short s = 160;
            int n = this.displayWidth - s - 10;
            int n2 = this.displayHeight - s * 2;
            GlStateManager.enableBlend();
            worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
            worldRenderer.func_181662_b(n - s * 1.1F, n2 - s * 0.6F - 16.0F, 0.0).func_181669_b(200, 0, 0, 0).func_181675_d();
            worldRenderer.func_181662_b(n - s * 1.1F, n2 + s * 2, 0.0).func_181669_b(200, 0, 0, 0).func_181675_d();
            worldRenderer.func_181662_b(n + s * 1.1F, n2 + s * 2, 0.0).func_181669_b(200, 0, 0, 0).func_181675_d();
            worldRenderer.func_181662_b(n + s * 1.1F, n2 - s * 0.6F - 16.0F, 0.0).func_181669_b(200, 0, 0, 0).func_181675_d();
            tessellator.draw();
            GlStateManager.disableBlend();
            double d = 0.0;

            for (int i = 0; i < profilingData.size(); i++) {
                Result result2 = (Result)profilingData.get(i);
                int n3 = MathHelper.floor_double(result2.field_76332_a / 4.0) + 1;
                worldRenderer.func_181668_a(6, DefaultVertexFormats.field_181706_f);
                int n4 = result2.func_76329_a();
                int n5 = n4 >> 16 & 0xFF;
                int n6 = n4 >> 8 & 0xFF;
                int n7 = n4 & 0xFF;
                worldRenderer.func_181662_b(n, n2, 0.0).func_181669_b(n5, n6, n7, 255).func_181675_d();

                for (int j = n3; j >= 0; j--) {
                    float f = (float)((d + result2.field_76332_a * j / n3) * Math.PI * 2.0 / 100.0);
                    float f2 = MathHelper.sin(f) * s;
                    float f3 = MathHelper.cos(f) * s * 0.5F;
                    worldRenderer.func_181662_b(n + f2, n2 - f3, 0.0).func_181669_b(n5, n6, n7, 255).func_181675_d();
                }

                tessellator.draw();
                worldRenderer.func_181668_a(5, DefaultVertexFormats.field_181706_f);

                for (int k = n3; k >= 0; k--) {
                    float f4 = (float)((d + result2.field_76332_a * k / n3) * Math.PI * 2.0 / 100.0);
                    float f5 = MathHelper.sin(f4) * s;
                    float f6 = MathHelper.cos(f4) * s * 0.5F;
                    worldRenderer.func_181662_b(n + f5, n2 - f6, 0.0).func_181669_b(n5 >> 1, n6 >> 1, n7 >> 1, 255).func_181675_d();
                    worldRenderer.func_181662_b(n + f5, n2 - f6 + 10.0F, 0.0).func_181669_b(n5 >> 1, n6 >> 1, n7 >> 1, 255).func_181675_d();
                }

                tessellator.draw();
                d += result2.field_76332_a;
            }

            DecimalFormat decimalFormat = new DecimalFormat("##0.00");
            GlStateManager.enableTexture2D();
            String str = "";
            if (!result.field_76331_c.equals("unspecified")) {
                str = str + "[0] ";
            }

            if (result.field_76331_c.length() == 0) {
                str = str + "ROOT ";
            } else {
                str = str + result.field_76331_c + " ";
            }

            int n8 = 16777215;
            this.fontRendererObj.drawStringWithShadow(str, n - s, n2 - s / 2 - 16, n8);
            this.fontRendererObj
                .drawStringWithShadow(
                    str = decimalFormat.format(result.field_76330_b) + "%", n + s - this.fontRendererObj.getStringWidth(str), n2 - s / 2 - 16, n8
                );

            for (int idx = 0; idx < profilingData.size(); idx++) {
                Result result3 = (Result)profilingData.get(idx);
                String str2 = "";
                if (result3.field_76331_c.equals("unspecified")) {
                    str2 = str2 + "[?] ";
                } else {
                    str2 = str2 + "[" + (idx + 1) + "] ";
                }

                str2 = str2 + result3.field_76331_c;
                this.fontRendererObj.drawStringWithShadow(str2, n - s, n2 + s / 2 + idx * 8 + 20, result3.func_76329_a());
                this.fontRendererObj
                    .drawStringWithShadow(
                        str2 = decimalFormat.format(result3.field_76332_a) + "%",
                        n + s - 50 - this.fontRendererObj.getStringWidth(str2),
                        n2 + s / 2 + idx * 8 + 20,
                        result3.func_76329_a()
                    );
                this.fontRendererObj
                    .drawStringWithShadow(
                        str2 = decimalFormat.format(result3.field_76330_b) + "%",
                        n + s - this.fontRendererObj.getStringWidth(str2),
                        n2 + s / 2 + idx * 8 + 20,
                        result3.func_76329_a()
                    );
            }
        }
    }

    public void shutdown() {
        Brutality.process2();
        this.running = false;
    }

    public void setIngameFocus() {
        if (Display.isActive() && !this.inGameHasFocus) {
            this.inGameHasFocus = true;
            this.mouseHelper.grabMouseCursor();
            this.displayGuiScreen((GuiScreen)null);
            this.leftClickCounter = 10000;
            KeyBinding.setKeyBindState(this.gameSettings.keyBindSprint.getKeyCode(), GameSettings.isKeyDown(this.gameSettings.keyBindSprint));
            KeyBinding.setKeyBindState(this.gameSettings.keyBindForward.getKeyCode(), GameSettings.isKeyDown(this.gameSettings.keyBindForward));
            KeyBinding.setKeyBindState(this.gameSettings.keyBindBack.getKeyCode(), GameSettings.isKeyDown(this.gameSettings.keyBindBack));
            KeyBinding.setKeyBindState(this.gameSettings.keyBindLeft.getKeyCode(), GameSettings.isKeyDown(this.gameSettings.keyBindLeft));
            KeyBinding.setKeyBindState(this.gameSettings.keyBindRight.getKeyCode(), GameSettings.isKeyDown(this.gameSettings.keyBindRight));
            KeyBinding.setKeyBindState(this.gameSettings.keyBindJump.getKeyCode(), GameSettings.isKeyDown(this.gameSettings.keyBindJump));
            KeyBinding.setKeyBindState(this.gameSettings.keyBindSneak.getKeyCode(), GameSettings.isKeyDown(this.gameSettings.keyBindSneak));
            KeyBinding.setKeyBindState(this.gameSettings.keyBindUseItem.getKeyCode(), GameSettings.isKeyDown(this.gameSettings.keyBindUseItem));
        }
    }

    public void setIngameNotInFocus() {
        if (this.inGameHasFocus) {
            KeyBinding.unPressAllKeys();
            this.inGameHasFocus = false;
            this.mouseHelper.ungrabMouseCursor();
        }
    }

    public void displayInGameMenu() {
        if (this.currentScreen == null) {
            this.displayGuiScreen(new GuiIngameMenu());
            if (this.isSingleplayer() && !this.theIntegratedServer.getPublic()) {
                this.mcSoundHandler.pauseSounds();
            }
        }
    }

    private void sendClickBlockToController(boolean leftClick) {
        if (!Scaffold.checkState19()) {
            if (!BedAura.checkState()) {
                if (!leftClick) {
                    this.leftClickCounter = 0;
                }

                if (this.leftClickCounter <= 0 && !this.thePlayer.isUsingItem()) {
                    if (leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
                        BlockPos blockPos = this.objectMouseOver.getBlockPos();
                        if (this.theWorld.getBlockState(blockPos).getBlock().getMaterial() != Material.air
                            && this.playerController.onPlayerDamageBlock(blockPos, this.objectMouseOver.sideHit)) {
                            BasicEvent basicEvent = new BasicEvent();
                            basicEvent.process();
                            if (basicEvent.cancel2()) {
                                return;
                            }

                            this.effectRenderer.addBlockHitEffects(blockPos, this.objectMouseOver.sideHit);
                            this.thePlayer.swingItem();
                        }
                    } else {
                        this.playerController.resetBlockRemoving();
                    }
                }
            }
        }
    }

    private boolean consumeModuleHotbarKey(int slot, KeyBinding binding) {
        if (this.currentScreen == null && ModuleManager.matchesInt(binding.getKeyCode())) {
            binding.pressTime = 0;
            binding.pressed = false;
            this.hotbarPreviousStates[slot] = true;
            return true;
        }
        return false;
    }

    private void continueUseAfterHotbarChange(int previousSlot, boolean useHeld) {
        if (this.thePlayer != null && previousSlot != this.thePlayer.inventory.currentItem) {
            if (this.thePlayer.getItemInUse() != null && this.thePlayer.getItemInUse() != this.thePlayer.getHeldItem()) {
                this.thePlayer.clearItemInUse();
            }

            KillAura instance = KillAura.instance;
            boolean flag = this.thePlayer.getHeldItem() != null
                && this.thePlayer.getHeldItem().getItem() instanceof ItemSword
                && instance != null
                && instance.checkState();
            if (useHeld && this.currentScreen == null && !StartHotbarMovesHelper.flag && !flag) {
                KeyBinding.setKeyBindState(this.gameSettings.keyBindUseItem.getKeyCode(), true);
                this.rightClickDelayTimer = 0;
            }
        }
    }

    public void clickMouse() {
        try (UtilsHelper.Entry entry = UtilsHelper.computeEntry(0)) {
            this.clickMouseInternal();
        }
    }

    private void clickMouseInternal() {
        if (this.objectMouseOver == null || this.objectMouseOver.typeOfHit != MovingObjectType.ENTITY || !WorldQueryCache.isActive()) {
            if (this.objectMouseOver == null
                || this.objectMouseOver.typeOfHit != MovingObjectType.ENTITY
                || KeepSprint.instance == null
                || !KeepSprint.instance.checkState()) {
                if (this.leftClickCounter <= 0) {
                    BasicEvent2 basicEvent2 = new BasicEvent2();
                    basicEvent2.process();
                    if (basicEvent2.cancel2()) {
                        return;
                    }

                    AttackOrder.sendConditionalSwing(this.objectMouseOver);
                    if (this.objectMouseOver == null) {
                        logger.error("Null returned as 'hitResult', this shouldn't happen!");
                        if (this.playerController.isNotCreative()) {
                            this.leftClickCounter = 10;
                        }
                    } else {
                        switch (this.objectMouseOver.typeOfHit) {
                            case ENTITY:
                                AttackOrder.sendFixedAttack(this.thePlayer, this.objectMouseOver.entityHit);
                                break;
                            case BLOCK:
                                BlockPos blockPos = this.objectMouseOver.getBlockPos();
                                if (this.theWorld.getBlockState(blockPos).getBlock().getMaterial() != Material.air) {
                                    this.playerController.clickBlock(blockPos, this.objectMouseOver.sideHit);
                                    break;
                                }
                            case MISS:
                            default:
                                if (this.playerController.isNotCreative()) {
                                    this.leftClickCounter = 10;
                                }
                        }
                    }
                }
            }
        }
    }

    public void rightClickMouse() {
        try (UtilsHelper.Entry entry = UtilsHelper.computeEntry(1)) {
            this.rightClickMouseInternal();
        }
    }

    private void rightClickMouseInternal() {
        if (!this.playerController.func_181040_m()) {
            BasicEvent4 basicEvent4 = new BasicEvent4();
            basicEvent4.process();
            if (basicEvent4.cancel2()) {
                return;
            }

            this.rightClickDelayTimer = 4;
            boolean flag = true;
            ItemStack currentItem = this.thePlayer.inventory.getCurrentItem();
            if (KillAura.matchesEntityPlayer(this.thePlayer, currentItem)) {
                return;
            }

            if (this.objectMouseOver == null) {
                logger.warn("Null returned as 'hitResult', this shouldn't happen!");
            } else {
                switch (this.objectMouseOver.typeOfHit) {
                    case ENTITY:
                        if (this.playerController.func_178894_a(this.thePlayer, this.objectMouseOver.entityHit, this.objectMouseOver)) {
                            flag = false;
                        } else if (this.playerController.interactWithEntitySendPacket(this.thePlayer, this.objectMouseOver.entityHit)) {
                            flag = false;
                        }
                        break;
                    case BLOCK:
                        BlockPos blockPos = this.objectMouseOver.getBlockPos();
                        if (this.theWorld.getBlockState(blockPos).getBlock().getMaterial() != Material.air) {
                            int n = currentItem != null ? currentItem.stackSize : 0;
                            if (this.playerController
                                .onPlayerRightClick(this.thePlayer, this.theWorld, currentItem, blockPos, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec)) {
                                flag = false;
                                this.thePlayer.swingItem();
                            }

                            if (currentItem == null) {
                                return;
                            }

                            if (currentItem.stackSize == 0) {
                                this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
                            } else if (currentItem.stackSize != n || this.playerController.isInCreativeMode()) {
                                this.entityRenderer.itemRenderer.resetEquippedProgress();
                            }
                        }
                }
            }

            if (flag) {
                ItemStack currentItem2 = this.thePlayer.inventory.getCurrentItem();
                if (currentItem2 != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, currentItem2)) {
                    this.entityRenderer.itemRenderer.resetEquippedProgress2();
                }
            }
        }
    }

    public void toggleFullscreen() {
        try {
            this.fullscreen = !this.fullscreen;
            this.gameSettings.fullScreen = this.fullscreen;
            if (this.fullscreen && this.gameSettings.borderlessFullscreen && !isRunningOnMac) {
                this.borderlessWindow.handleInt(this.tempDisplayWidth, this.tempDisplayHeight);
                this.displayWidth = Display.getDisplayMode().getWidth();
                this.displayHeight = Display.getDisplayMode().getHeight();
            } else if (!this.fullscreen && this.borderlessWindow.isActive()) {
                this.borderlessWindow.process();
                this.displayWidth = Display.getWidth();
                this.displayHeight = Display.getHeight();
            } else if (this.fullscreen) {
                this.updateDisplayMode();
                this.displayWidth = Display.getDisplayMode().getWidth();
                this.displayHeight = Display.getDisplayMode().getHeight();
                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }
            } else {
                Display.setDisplayMode(new DisplayMode(this.tempDisplayWidth, this.tempDisplayHeight));
                this.displayWidth = this.tempDisplayWidth;
                this.displayHeight = this.tempDisplayHeight;
                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }
            }

            if (this.currentScreen != null) {
                this.resize(this.displayWidth, this.displayHeight);
            } else {
                this.updateFramebufferSize();
            }

            Display.setFullscreen(this.fullscreen && !this.borderlessWindow.isActive());
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
            this.updateDisplay();
        } catch (Exception e) {
            this.fullscreen = Display.isFullscreen() || this.isBorderlessFullscreen();
            this.gameSettings.fullScreen = this.fullscreen;
            logger.error("Couldn't toggle fullscreen", (Throwable)e);
        }
    }

    public boolean isBorderlessFullscreen() {
        return this.borderlessWindow != null && this.borderlessWindow.isActive();
    }

    public void setBorderlessFullscreen(boolean enabled) {
        boolean fullscreen = this.fullscreen;
        if (fullscreen) {
            this.toggleFullscreen();
        }

        this.gameSettings.borderlessFullscreen = enabled;
        if (fullscreen) {
            this.toggleFullscreen();
        }

        this.gameSettings.saveOptions();
    }

    private void resize(int width, int height) {
        this.displayWidth = Math.max(1, width);
        this.displayHeight = Math.max(1, height);
        if (this.currentScreen != null) {
            ScaledResolution sr = new ScaledResolution(this);
            this.currentScreen.onResize(this, sr.getScaledWidth(), sr.getScaledHeight());
        }

        this.loadingScreen = new LoadingScreenRenderer(this);
        this.updateFramebufferSize();
    }

    private void updateFramebufferSize() {
        this.framebufferMc.createBindFramebuffer(this.displayWidth, this.displayHeight);
        if (this.entityRenderer != null) {
            this.entityRenderer.updateShaderGroupSize(this.displayWidth, this.displayHeight);
        }
    }

    public MusicTicker func_181535_r() {
        return this.mcMusicTicker;
    }

    public void runTick() throws IOException {
        PitDebugger.process6();
        WorldDownloadController.tick();
        DiscordRPC.process3();
        if (!this.launcherGrinderStarted
            && Boolean.getBoolean("brutality.autogrinder")
            && this.thePlayer != null
            && this.theWorld != null
            && this.getCurrentServerData() != null
            && this.getCurrentServerData().serverIP.toLowerCase(Locale.ROOT).contains("hypixel.net")
            && !ConnectToServerHandler.checkState2()
            && this.launcherPitJoinRetries < 3
            && System.currentTimeMillis() - this.launcherPitJoinAttempt > 15000L) {
            this.thePlayer.sendChatMessage("/play pit");
            this.launcherPitJoinAttempt = System.currentTimeMillis();
            this.launcherPitJoinRetries++;
        }

        if (!this.launcherGrinderStarted
            && Boolean.getBoolean("brutality.autogrinder")
            && this.thePlayer != null
            && this.theWorld != null
            && Brutality.moduleManager != null
            && ConnectToServerHandler.checkState2()) {
            Module module = Brutality.moduleManager.computeModule("AutoGrinder");
            if (module != null) {
                if (!module.checkState44()) {
                    module.process28();
                }

                this.launcherGrinderStarted = true;
            }
        }

        if (this.theWorld != this.theWorld && Brutality.moduleManager.computeModule("Disabler").checkState44()) {
            Brutality.moduleManager.computeModule("Disabler").onDisable();
            Brutality.moduleManager.computeModule("Disabler").process28();
            Brutality.moduleManager.computeModule("Disabler").process28();
        }

        EventTick event = new EventTick(EventState.PRE);
        if (this.thePlayer != null && this.theWorld != null) {
            event.process();
        }

        if (!event.cancel2()) {
            if (this.thePlayer != null && this.theWorld != null) {
                try {
                    FreshHelper.process();
                } catch (Exception ignored) {
                }

                try {
                    ItemNameManager.handleEntityPlayer(this.thePlayer);
                } catch (Exception ignored2) {
                }
            }

            BindCommand.getInstance().onTick(event);
            OnJumpHelper.onTick();
            if (this.rightClickDelayTimer > 0) {
                this.rightClickDelayTimer--;
            }

            this.mcProfiler.startSection("gui");
            if (!this.isGamePaused) {
                this.ingameGUI.updateTick();
            }

            this.mcProfiler.endSection();
            this.entityRenderer.getMouseOver(1.0F);
            this.mcProfiler.startSection("gameMode");
            if (!this.isGamePaused && this.theWorld != null) {
                this.playerController.updateController();
            }

            this.mcProfiler.endStartSection("textures");
            if (!this.isGamePaused) {
                this.renderEngine.tick();
            }

            if (this.currentScreen == null && this.thePlayer != null) {
                if (this.thePlayer.getHealth() <= 0.0F) {
                    this.displayGuiScreen((GuiScreen)null);
                } else if (this.thePlayer.isPlayerSleeping() && this.theWorld != null) {
                    this.displayGuiScreen(new GuiSleepMP());
                }
            } else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.thePlayer.isPlayerSleeping()) {
                this.displayGuiScreen((GuiScreen)null);
            }

            if (this.currentScreen != null) {
                this.leftClickCounter = 10000;
            }

            if (this.currentScreen != null) {
                try {
                    this.currentScreen.handleInput();
                } catch (Throwable e) {
                    CrashReport crashReport = CrashReport.makeCrashReport(e, "Updating screen events");
                    CrashReportCategory crashReportCategory = crashReport.makeCategory("Affected screen");
                    crashReportCategory.addCrashSectionCallable("Screen name", new Callable<String>() {
                        public String call() throws Exception {
                            return Minecraft.this.currentScreen.getClass().getCanonicalName();
                        }
                    });
                    throw new ReportedException(crashReport);
                }

                if (this.currentScreen != null) {
                    try (RenderHelper renderHelper = RenderHelper.matchesGuiScreen(this.currentScreen) ? RenderHelper.computeRenderHelper(this.currentScreen) : null) {
                        this.currentScreen.updateScreen();
                    } catch (Throwable e2) {
                        CrashReport crashReport2 = CrashReport.makeCrashReport(e2, "Ticking screen");
                        CrashReportCategory crashReportCategory2 = crashReport2.makeCategory("Affected screen");
                        crashReportCategory2.addCrashSectionCallable("Screen name", new Callable<String>() {
                            public String call() throws Exception {
                                return Minecraft.this.currentScreen.getClass().getCanonicalName();
                            }
                        });
                        throw new ReportedException(crashReport2);
                    }
                }
            }

            if (this.currentScreen == null || this.currentScreen.allowUserInput) {
                this.mcProfiler.endStartSection("mouse");

                while (Mouse.next()) {
                    BindCommand.getInstance().handleInt2(Mouse.getEventButton(), Mouse.getEventButtonState());
                    if (this.currentScreen == null && Mouse.getEventButtonState() && Mouse.getEventButton() >= 0) {
                        OnKeyPressedComponent.onKeyPressed(Mouse.getEventButton() - 100);
                    }

                    BasicEvent3 basicEvent3 = new BasicEvent3();
                    basicEvent3.process();
                    if (!basicEvent3.cancel2()) {
                        int eventButton = Mouse.getEventButton();
                        KeyBinding.setKeyBindState(eventButton - 100, Mouse.getEventButtonState());
                        if (Mouse.getEventButtonState()) {
                            if (this.thePlayer.isSpectator() && eventButton == 2) {
                                this.ingameGUI.getSpectatorGui().func_175261_b();
                            } else {
                                KeyBinding.onTick(eventButton - 100);
                            }
                        }

                        long l = getSystemTime() - this.systemTime;
                        if (l <= 200L) {
                            int eventDWheel = Mouse.getEventDWheel();
                            if (eventDWheel != 0) {
                                if (this.thePlayer.isSpectator()) {
                                    int n = eventDWheel < 0 ? -1 : 1;
                                    if (this.ingameGUI.getSpectatorGui().func_175262_a()) {
                                        this.ingameGUI.getSpectatorGui().func_175259_b(-n);
                                    } else {
                                        float f = MathHelper.clamp_float(this.thePlayer.capabilities.getFlySpeed() + n * 0.005F, 0.0F, 0.2F);
                                        this.thePlayer.capabilities.setFlySpeed(f);
                                    }
                                } else {
                                    int currentItem = this.thePlayer.inventory.currentItem;
                                    this.thePlayer.inventory.changeCurrentItem(eventDWheel);
                                    this.continueUseAfterHotbarChange(currentItem, GameSettings.isKeyDown(this.gameSettings.keyBindUseItem));
                                }
                            }

                            if (this.currentScreen == null) {
                                if (!this.inGameHasFocus && Mouse.getEventButtonState()) {
                                    this.setIngameFocus();
                                }
                            } else if (this.currentScreen != null) {
                                this.currentScreen.handleMouseInput();
                            }
                        }
                    }
                }

                if (this.leftClickCounter > 0) {
                    this.leftClickCounter--;
                }

                this.mcProfiler.endStartSection("keyboard");

                label690:
                while (true) {
                    int n2;
                    while (true) {
                        if (!Keyboard.next()) {
                            for (int i = 0; i < 9; i++) {
                                if (this.currentScreen != null && this.currentScreen instanceof GuiContainer) {
                                    this.hotbarPreviousStates[i] = false;
                                } else {
                                    KeyBinding keyBinding = this.gameSettings.keyBindsHotbar[i];
                                    int keyCode = keyBinding.getKeyCode();
                                    if (!this.consumeModuleHotbarKey(i, keyBinding)) {
                                        if (keyCode == 0) {
                                            this.hotbarPreviousStates[i] = false;
                                        } else {
                                            boolean flag;
                                            if (keyCode >= 0) {
                                                flag = Keyboard.isKeyDown(keyCode);
                                            } else {
                                                int n3 = keyCode + 100;
                                                flag = n3 >= 0 && n3 < 16 && Mouse.isButtonDown(n3);
                                            }

                                            boolean pressed = keyBinding.isPressed();
                                            if (pressed || flag && !this.hotbarPreviousStates[i]) {
                                                PlayerEvent4 playerEvent4 = new PlayerEvent4(i, 0);
                                                playerEvent4.process();
                                                if (!playerEvent4.cancel2()) {
                                                    if (this.thePlayer.isSpectator()) {
                                                        this.ingameGUI.getSpectatorGui().func_175260_a(i);
                                                    } else {
                                                        int currentItem2 = this.thePlayer.inventory.currentItem;
                                                        this.thePlayer.inventory.currentItem = i;
                                                        this.continueUseAfterHotbarChange(currentItem2, GameSettings.isKeyDown(this.gameSettings.keyBindUseItem));
                                                    }
                                                }
                                            }

                                            this.hotbarPreviousStates[i] = flag;
                                            keyBinding.pressTime = 0;
                                        }
                                    }
                                }
                            }

                            new PlayerEvent3().process();
                            boolean flag2 = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;

                            while (this.gameSettings.keyBindInventory.isPressed()) {
                                if (this.playerController.isRidingHorse()) {
                                    this.thePlayer.sendHorseInventory();
                                } else {
                                    this.getNetHandler().addToSendQueue(new C16PacketClientStatus(EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                                    this.displayGuiScreen(new GuiInventory(this.thePlayer));
                                }
                            }

                            while (this.gameSettings.keyBindDrop.isPressed()) {
                                if (!this.thePlayer.isSpectator()) {
                                    if (HotbarHelper.checkState()) {
                                        HotbarHelper.process2();
                                    } else {
                                        this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
                                    }
                                }
                            }

                            while (this.gameSettings.keyBindChat.isPressed() && flag2) {
                                this.displayGuiScreen(new GuiChat());
                            }

                            if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed() && flag2) {
                                this.displayGuiScreen(new GuiChat("/"));
                            }

                            if (this.currentScreen == null && flag2) {
                                boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_PERIOD);
                                if (keyDown && !this.brutalityCommandChatKeyDown) {
                                    this.brutalityCommandChatKeyDown = true;
                                    this.displayGuiScreen(new GuiChat("."));
                                } else if (!keyDown) {
                                    this.brutalityCommandChatKeyDown = false;
                                }
                            } else {
                                this.brutalityCommandChatKeyDown = false;
                            }

                            if (this.thePlayer.isUsingItem()) {
                                if (!this.gameSettings.keyBindUseItem.isKeyDown()) {
                                    this.playerController.onStoppedUsingItem(this.thePlayer);
                                }

                                while (this.gameSettings.keyBindAttack.isPressed()) {
                                    UtilsHelper.process2();
                                }

                                while (this.gameSettings.keyBindUseItem.isPressed()) {
                                    UtilsHelper.process();
                                }

                                while (this.gameSettings.keyBindPickBlock.isPressed()) {
                                }
                            } else {
                                while (this.gameSettings.keyBindAttack.isPressed()) {
                                    this.clickMouse();
                                }

                                while (this.gameSettings.keyBindUseItem.isPressed()) {
                                    this.rightClickMouse();
                                }

                                while (this.gameSettings.keyBindPickBlock.isPressed()) {
                                    this.middleClickMouse();
                                }
                            }

                            if (this.gameSettings.keyBindUseItem.isKeyDown() && this.rightClickDelayTimer == 0 && !this.thePlayer.isUsingItem()) {
                                this.rightClickMouse();
                            }

                            this.sendClickBlockToController(this.currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() && this.inGameHasFocus);
                            break label690;
                        }

                        n2 = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
                        if (this.currentScreen == null && Keyboard.getEventKeyState() && !Keyboard.isRepeatEvent()) {
                            OnKeyPressedComponent.onKeyPressed(n2);
                        }

                        boolean flag3 = this.currentScreen == null && ModuleManager.matchesInt(n2);
                        KeyBinding.setKeyBindState(n2, !flag3 && Keyboard.getEventKeyState());
                        if (Keyboard.getEventKeyState() && !flag3) {
                            KeyBinding.onTick(n2);
                        }

                        if (this.debugCrashKeyPressTime > 0L) {
                            if (getSystemTime() - this.debugCrashKeyPressTime >= 6000L) {
                                throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
                            }

                            if (!Keyboard.isKeyDown(Keyboard.KEY_C) || !Keyboard.isKeyDown(Keyboard.KEY_F3)) {
                                this.debugCrashKeyPressTime = -1L;
                            }
                        } else if (Keyboard.isKeyDown(Keyboard.KEY_C) && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
                            this.debugCrashKeyPressTime = getSystemTime();
                        }

                        if (this.currentScreen != null || ModuleManager.computeList(n2) == null) {
                            this.dispatchKeypresses();
                        }

                        if (Keyboard.getEventKeyState()) {
                            if (n2 == 62 && this.entityRenderer != null) {
                                this.entityRenderer.switchUseShader();
                            }

                            if (this.currentScreen != null) {
                                if (n2 != 15 || !Brutality.cmdManager.matchesInt(n2)) {
                                    this.currentScreen.handleKeyboardInput();
                                    break;
                                }
                            } else {
                                ModuleManager.handleInt(n2);
                                EventKey event2 = new EventKey(n2);
                                event2.process();
                                if (!event2.cancel2()) {
                                    if (n2 == 1) {
                                        this.displayInGameMenu();
                                    }

                                    if (n2 == 32 && Keyboard.isKeyDown(Keyboard.KEY_F3) && this.ingameGUI != null) {
                                        this.ingameGUI.getChatGUI().clearChatMessages();
                                    }

                                    if (n2 == 31 && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
                                        this.refreshResources();
                                    }

                                    if (n2 == 20 && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
                                        this.refreshResources();
                                    }

                                    if (n2 == 33 && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
                                        this.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, GuiScreen.isShiftKeyDown() ? -1 : 1);
                                    }

                                    if (n2 == 30 && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
                                        this.renderGlobal.loadRenderers();
                                    }

                                    if (n2 == 35 && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
                                        this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
                                        this.gameSettings.saveOptions();
                                    }

                                    if (n2 == 48 && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
                                        this.renderManager.setDebugBoundingBox(!this.renderManager.isDebugBoundingBox());
                                    }

                                    if (n2 == 25 && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
                                        this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
                                        this.gameSettings.saveOptions();
                                    }

                                    if (n2 == 59) {
                                        this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
                                    }

                                    if (n2 == 61) {
                                        this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
                                        this.gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown();
                                        this.gameSettings.field_181657_aC = GuiScreen.isAltKeyDown();
                                    }

                                    if (this.gameSettings.keyBindTogglePerspective.isPressed()) {
                                        this.gameSettings.thirdPersonView++;
                                        if (this.gameSettings.thirdPersonView > 2) {
                                            this.gameSettings.thirdPersonView = 0;
                                        }

                                        if (this.gameSettings.thirdPersonView == 0) {
                                            this.entityRenderer.loadEntityShader(this.getRenderViewEntity());
                                        } else if (this.gameSettings.thirdPersonView == 1) {
                                            this.entityRenderer.loadEntityShader((Entity)null);
                                        }

                                        this.renderGlobal.setDisplayListEntitiesDirty();
                                    }

                                    if (this.gameSettings.keyBindSmoothCamera.isPressed()) {
                                        this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart) {
                        if (n2 == 11) {
                            this.updateDebugProfilerName(0);
                        }

                        for (int j = 0; j < 9; j++) {
                            if (n2 == 2 + j) {
                                this.updateDebugProfilerName(j + 1);
                            }
                        }
                    }
                }
            }

            if (this.theWorld != null) {
                if (this.thePlayer != null) {
                    this.joinPlayerCounter++;
                    if (this.joinPlayerCounter == 30) {
                        this.joinPlayerCounter = 0;
                        this.theWorld.joinEntityInSurroundings(this.thePlayer);
                    }
                }

                this.mcProfiler.endStartSection("gameRenderer");
                if (!this.isGamePaused) {
                    this.entityRenderer.updateRenderer();
                }

                this.mcProfiler.endStartSection("levelRenderer");
                if (!this.isGamePaused) {
                    this.renderGlobal.updateClouds();
                }

                this.mcProfiler.endStartSection("level");
                if (!this.isGamePaused) {
                    if (this.theWorld.getLastLightningBolt() > 0) {
                        this.theWorld.setLastLightningBolt(this.theWorld.getLastLightningBolt() - 1);
                    }

                    this.theWorld.updateEntities();
                }
            } else if (this.entityRenderer.isShaderActive()) {
                this.entityRenderer.func_181022_b();
            }

            if (!this.isGamePaused) {
                this.mcMusicTicker.update();
                this.mcSoundHandler.update();
            }

            if (this.theWorld != null) {
                if (!this.isGamePaused) {
                    this.theWorld.setAllowedSpawnTypes(this.theWorld.getDifficulty() != EnumDifficulty.PEACEFUL, true);

                    try {
                        this.theWorld.tick();
                    } catch (Throwable e3) {
                        CrashReport crashReport3 = CrashReport.makeCrashReport(e3, "Exception in world tick");
                        if (this.theWorld == null) {
                            CrashReportCategory crashReportCategory3 = crashReport3.makeCategory("Affected level");
                            crashReportCategory3.addCrashSection("Problem", "Level is null!");
                        } else {
                            this.theWorld.addWorldInfoToCrashReport(crashReport3);
                        }

                        throw new ReportedException(crashReport3);
                    }
                }

                this.mcProfiler.endStartSection("animateTick");
                if (!this.isGamePaused && this.theWorld != null) {
                    this.theWorld
                        .doVoidFogParticles(
                            MathHelper.floor_double(this.thePlayer.posX),
                            MathHelper.floor_double(this.thePlayer.posY),
                            MathHelper.floor_double(this.thePlayer.posZ)
                        );
                }

                this.mcProfiler.endStartSection("particles");
                if (!this.isGamePaused) {
                    this.effectRenderer.updateEffects();
                }
            } else if (this.myNetworkManager != null) {
                this.mcProfiler.endStartSection("pendingConnection");
                this.myNetworkManager.processReceivedPackets();
            }

            this.mcProfiler.endSection();
            this.systemTime = getSystemTime();
            EventTick event3 = new EventTick(EventState.POST);
            event3.process();
            PitDebugger.process7();
        }
    }

    public void launchIntegratedServer(String folderName, String worldName, WorldSettings worldSettingsIn) {
        this.loadWorld((WorldClient)null);
        System.gc();
        ISaveHandler saveLoader = this.saveLoader.getSaveLoader(folderName, false);
        WorldInfo worldInfo = saveLoader.loadWorldInfo();
        if (worldInfo == null && worldSettingsIn != null) {
            worldInfo = new WorldInfo(worldSettingsIn, folderName);
            saveLoader.saveWorldInfo(worldInfo);
        }

        if (worldSettingsIn == null) {
            worldSettingsIn = new WorldSettings(worldInfo);
        }

        try {
            this.theIntegratedServer = new IntegratedServer(this, folderName, worldName, worldSettingsIn);
            this.theIntegratedServer.startServerThread();
            this.integratedServerIsRunning = true;
        } catch (Throwable e) {
            CrashReport crashReport = CrashReport.makeCrashReport(e, "Starting integrated server");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Starting integrated server");
            crashReportCategory.addCrashSection("Level ID", folderName);
            crashReportCategory.addCrashSection("Level Name", worldName);
            throw new ReportedException(crashReport);
        }

        this.loadingScreen.displaySavingString(I18n.format("menu.loadingLevel"));

        while (!this.theIntegratedServer.serverIsInRunLoop()) {
            String userMessage = this.theIntegratedServer.getUserMessage();
            if (userMessage != null) {
                this.loadingScreen.displayLoadingString(I18n.format(userMessage));
            } else {
                this.loadingScreen.displayLoadingString("");
            }

            try {
                Thread.sleep(200L);
            } catch (InterruptedException ignored) {
            }
        }

        this.displayGuiScreen((GuiScreen)null);
        SocketAddress socketAddress = this.theIntegratedServer.getNetworkSystem().addLocalEndpoint();
        NetworkManager networkManager = NetworkManager.provideLocalClient(socketAddress);
        networkManager.setNetHandler(new NetHandlerLoginClient(networkManager, this, (GuiScreen)null));
        networkManager.sendPacket(new C00Handshake(47, socketAddress.toString(), 0, EnumConnectionState.LOGIN));
        networkManager.sendPacket(new C00PacketLoginStart(this.getSession().getProfile()));
        this.myNetworkManager = networkManager;
        DiscordRPC instance = DiscordRPC.getInstance();
        if (instance != null && instance.isActive()) {
            instance.handleString("Playing on brutality.lol", "Best pit client");
        }
    }

    public void loadWorld(WorldClient worldClientIn) {
        this.loadWorld(worldClientIn, "");
    }

    public void loadWorld(WorldClient worldClientIn, String loadingMessage) {
        if (this.theWorld != worldClientIn) {
            StartHotbarMovesHelper.process3();
        }

        if (worldClientIn == null) {
            NetHandlerPlayClient netHandler = this.getNetHandler();
            if (netHandler != null) {
                netHandler.cleanup();
            }

            if (this.theIntegratedServer != null && this.theIntegratedServer.isAnvilFileSet()) {
                this.theIntegratedServer.initiateShutdown();
                this.theIntegratedServer.setStaticInstance();
            }

            this.theIntegratedServer = null;
            this.guiAchievement.clearAchievements();
            this.entityRenderer.getMapItemRenderer().clearLoadedMaps();
        }

        this.renderViewEntity = null;
        this.myNetworkManager = null;
        if (this.loadingScreen != null) {
            this.loadingScreen.resetProgressAndMessage(loadingMessage);
            this.loadingScreen.displayLoadingString("");
        }

        if (worldClientIn == null && this.theWorld != null) {
            this.mcResourcePackRepository.func_148529_f();
            this.ingameGUI.func_181029_i();
            this.setServerData((ServerData)null);
            this.integratedServerIsRunning = false;
        }

        this.mcSoundHandler.stopSounds();
        WorldClient world = this.theWorld;
        this.theWorld = worldClientIn;
        EntityCache.clear();

        for (Module module : ModuleManager.getModuleArray()) {
            module.process2();
        }

        this.resetPlayerMovementState();
        new RenderEvent4(world, worldClientIn).process();
        if (worldClientIn != null) {
            if (this.renderGlobal != null) {
                this.renderGlobal.setWorldAndLoadRenderers(worldClientIn);
            }

            if (this.effectRenderer != null) {
                this.effectRenderer.clearEffects(worldClientIn);
            }

            if (this.thePlayer == null) {
                this.thePlayer = this.playerController.func_178892_a(worldClientIn, new StatFileWriter());
                this.playerController.flipPlayer(this.thePlayer);
            }

            this.thePlayer.preparePlayerToSpawn();
            worldClientIn.spawnEntityInWorld(this.thePlayer);
            this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
            this.playerController.setPlayerCapabilities(this.thePlayer);
            this.renderViewEntity = this.thePlayer;
        } else {
            this.saveLoader.flushCache();
            this.thePlayer = null;
        }

        System.gc();
        this.systemTime = 0L;
    }

    private void resetPlayerMovementState() {
        ColorMathUtil.process();
        if (this.thePlayer != null) {
            this.thePlayer.resetSprintForPlayerReplacement();
        }

        MovementFix.process5();
        OnJumpHelper.disable();
        OnJumpHelper.process5();
        CountMovementHelper.getInstance().process();
        if (Brutality.lagHandler != null) {
            Brutality.lagHandler.process3();
        }

        if (this.timer != null) {
            this.timer.timerSpeed = 1.0F;
        }
    }

    public void setDimensionAndSpawnPlayer(int dimension) {
        KillAura instance = KillAura.getInstance();
        if (instance != null) {
            instance.process2();
        }

        this.resetPlayerMovementState();
        this.theWorld.setInitialSpawnLocation();
        this.theWorld.removeAllEntities();
        int n = 0;
        String str = null;
        if (this.thePlayer != null) {
            n = this.thePlayer.getEntityId();
            this.theWorld.removeEntity(this.thePlayer);
            str = this.thePlayer.getClientBrand();
        }

        this.renderViewEntity = null;
        EntityPlayerSP player = this.thePlayer;
        this.thePlayer = this.playerController.func_178892_a(this.theWorld, this.thePlayer == null ? new StatFileWriter() : this.thePlayer.getStatFileWriter());
        this.thePlayer.getDataWatcher().updateWatchedObjectsFromList(player.getDataWatcher().getAllWatched());
        this.thePlayer.dimension = dimension;
        this.renderViewEntity = this.thePlayer;
        this.thePlayer.preparePlayerToSpawn();
        this.thePlayer.setClientBrand(str);
        this.theWorld.spawnEntityInWorld(this.thePlayer);
        this.playerController.flipPlayer(this.thePlayer);
        this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
        this.thePlayer.setEntityId(n);
        this.playerController.setPlayerCapabilities(this.thePlayer);
        this.thePlayer.setReducedDebug(player.hasReducedDebug());
        if (this.currentScreen instanceof GuiGameOver) {
            this.displayGuiScreen((GuiScreen)null);
        }
    }

    public final boolean isDemo() {
        return this.isDemo;
    }

    public NetHandlerPlayClient getNetHandler() {
        return this.thePlayer != null ? this.thePlayer.sendQueue : null;
    }

    public static boolean isGuiEnabled() {
        return theMinecraft == null || !theMinecraft.gameSettings.hideGUI;
    }

    public static boolean isFancyGraphicsEnabled() {
        return theMinecraft != null && theMinecraft.gameSettings.fancyGraphics;
    }

    public static boolean isAmbientOcclusionEnabled() {
        return theMinecraft != null && theMinecraft.gameSettings.ambientOcclusion != 0;
    }

    private void middleClickMouse() {
        if (this.objectMouseOver != null) {
            boolean isCreativeMode = this.thePlayer.capabilities.isCreativeMode;
            int n = 0;
            boolean flag = false;
            TileEntity tileEntity = null;
            Item item;
            if (this.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
                BlockPos blockPos = this.objectMouseOver.getBlockPos();
                Block block = this.theWorld.getBlockState(blockPos).getBlock();
                if (block.getMaterial() == Material.air) {
                    return;
                }

                item = block.getItem(this.theWorld, blockPos);
                if (item == null) {
                    return;
                }

                if (isCreativeMode && GuiScreen.isCtrlKeyDown()) {
                    tileEntity = this.theWorld.getTileEntity(blockPos);
                }

                Block block2 = item instanceof ItemBlock && !block.isFlowerPot() ? Block.getBlockFromItem((Item)item) : block;
                n = block2.getDamageValue(this.theWorld, blockPos);
                flag = item.getHasSubtypes();
            } else {
                if (this.objectMouseOver.typeOfHit != MovingObjectType.ENTITY || this.objectMouseOver.entityHit == null || !isCreativeMode) {
                    return;
                }

                if (this.objectMouseOver.entityHit instanceof EntityPainting) {
                    item = Items.painting;
                } else if (this.objectMouseOver.entityHit instanceof EntityLeashKnot) {
                    item = Items.lead;
                } else if (this.objectMouseOver.entityHit instanceof EntityItemFrame entity) {
                    ItemStack displayedItem = entity.getDisplayedItem();
                    if (displayedItem == null) {
                        item = Items.item_frame;
                    } else {
                        item = displayedItem.getItem();
                        n = displayedItem.getMetadata();
                        flag = true;
                    }
                } else if (this.objectMouseOver.entityHit instanceof EntityMinecart entity2) {
                    switch (entity2.getMinecartType()) {
                        case FURNACE:
                            item = Items.furnace_minecart;
                            break;
                        case CHEST:
                            item = Items.chest_minecart;
                            break;
                        case TNT:
                            item = Items.tnt_minecart;
                            break;
                        case HOPPER:
                            item = Items.hopper_minecart;
                            break;
                        case COMMAND_BLOCK:
                            item = Items.command_block_minecart;
                            break;
                        default:
                            item = Items.minecart;
                    }
                } else if (this.objectMouseOver.entityHit instanceof EntityBoat) {
                    item = Items.boat;
                } else if (this.objectMouseOver.entityHit instanceof EntityArmorStand) {
                    item = Items.armor_stand;
                } else {
                    item = Items.spawn_egg;
                    n = EntityList.getEntityID(this.objectMouseOver.entityHit);
                    flag = true;
                    if (!EntityList.entityEggs.containsKey(n)) {
                        return;
                    }
                }
            }

            InventoryPlayer inventory = this.thePlayer.inventory;
            if (tileEntity == null) {
                inventory.setCurrentItem((Item)item, n, flag, isCreativeMode);
            } else {
                ItemStack stack = this.func_181036_a((Item)item, n, tileEntity);
                inventory.setInventorySlotContents(inventory.currentItem, stack);
            }

            if (isCreativeMode) {
                int n2 = this.thePlayer.inventoryContainer.inventorySlots.size() - 9 + inventory.currentItem;
                this.playerController.sendSlotPacket(inventory.getStackInSlot(inventory.currentItem), n2);
            }
        }
    }

    private ItemStack func_181036_a(Item p_181036_1_, int p_181036_2_, TileEntity p_181036_3_) {
        ItemStack stack = new ItemStack(p_181036_1_, 1, p_181036_2_);
        NBTTagCompound nbt = new NBTTagCompound();
        p_181036_3_.writeToNBT(nbt);
        if (p_181036_1_ == Items.skull && nbt.hasKey("Owner")) {
            NBTTagCompound compoundTag = nbt.getCompoundTag("Owner");
            NBTTagCompound nbt2 = new NBTTagCompound();
            nbt2.setTag("SkullOwner", compoundTag);
            stack.setTagCompound(nbt2);
            return stack;
        }
        stack.setTagInfo("BlockEntityTag", nbt);
        NBTTagCompound nbt3 = new NBTTagCompound();
        NBTTagList tagList = new NBTTagList();
        tagList.appendTag(new NBTTagString("(+NBT)"));
        nbt3.setTag("Lore", tagList);
        stack.setTagInfo("display", nbt3);
        return stack;
    }

    public CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash) {
        theCrash.getCategory().addCrashSectionCallable("Launched Version", new Callable<String>() {
            public String call() {
                return Minecraft.this.launchedVersion;
            }
        });
        theCrash.getCategory().addCrashSectionCallable("LWJGL", new Callable<String>() {
            public String call() {
                return Sys.getVersion();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("OpenGL", new Callable<String>() {
            public String call() {
                return GL11.glGetString(7937) + " GL version " + GL11.glGetString(7938) + ", " + GL11.glGetString(7936);
            }
        });
        theCrash.getCategory().addCrashSectionCallable("GL Caps", new Callable<String>() {
            public String call() {
                return OpenGlHelper.getLogText();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Using VBOs", new Callable<String>() {
            public String call() {
                return Minecraft.this.gameSettings.useVbo ? "Yes" : "No";
            }
        });
        theCrash.getCategory()
            .addCrashSectionCallable(
                "Is Modded",
                new Callable<String>() {
                    public String call() {
                        String clientModName = ClientBrandRetriever.getClientModName();
                        return !clientModName.equals("vanilla")
                            ? "Definitely; Client brand changed to '" + clientModName + "'"
                            : (
                                Minecraft.class.getSigners() == null
                                    ? "Very likely; Jar signature invalidated"
                                    : "Probably not. Jar signature remains and client brand is untouched."
                            );
                    }
                }
            );
        theCrash.getCategory().addCrashSectionCallable("Type", new Callable<String>() {
            public String call() {
                return "Client (map_client.txt)";
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Resource Packs", new Callable<String>() {
            public String call() {
                StringBuilder sb = new StringBuilder();

                for (String resourcePack : Minecraft.this.gameSettings.resourcePacks) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }

                    sb.append(resourcePack);
                    if (Minecraft.this.gameSettings.field_183018_l.contains(resourcePack)) {
                        sb.append(" (incompatible)");
                    }
                }

                return sb.toString();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Current Language", new Callable<String>() {
            public String call() {
                return Minecraft.this.mcLanguageManager.getCurrentLanguage().toString();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Profiler Position", new Callable<String>() {
            public String call() {
                return Minecraft.this.mcProfiler.profilingEnabled ? Minecraft.this.mcProfiler.getNameOfLastSection() : "N/A (disabled)";
            }
        });
        theCrash.getCategory().addCrashSectionCallable("CPU", new Callable<String>() {
            public String call() {
                return OpenGlHelper.func_183029_j();
            }
        });
        if (this.theWorld != null) {
            this.theWorld.addWorldInfoToCrashReport(theCrash);
        }

        return theCrash;
    }

    public static Minecraft getMinecraft() {
        return theMinecraft;
    }

    public ListenableFuture<Object> scheduleResourcesRefresh() {
        return this.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                Minecraft.this.refreshResources();
            }
        });
    }

    @Override
    public void addServerStatsToSnooper(PlayerUsageSnooper playerSnooper) {
        playerSnooper.addClientStat("fps", debugFPS);
        playerSnooper.addClientStat("vsync_enabled", this.gameSettings.enableVsync);
        playerSnooper.addClientStat("display_frequency", Display.getDisplayMode().getFrequency());
        playerSnooper.addClientStat("display_type", this.fullscreen ? "fullscreen" : "windowed");
        playerSnooper.addClientStat("run_time", (MinecraftServer.getCurrentTimeMillis() - playerSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L);
        playerSnooper.addClientStat("current_action", this.func_181538_aA());
        String str = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
        playerSnooper.addClientStat("endianness", str);
        playerSnooper.addClientStat("resource_packs", this.mcResourcePackRepository.getRepositoryEntries().size());
        int n = 0;

        for (Entry repositoryEntry : this.mcResourcePackRepository.getRepositoryEntries()) {
            playerSnooper.addClientStat("resource_pack[" + n++ + "]", repositoryEntry.getResourcePackName());
        }

        if (this.theIntegratedServer != null && this.theIntegratedServer.getPlayerUsageSnooper() != null) {
            playerSnooper.addClientStat("snooper_partner", this.theIntegratedServer.getPlayerUsageSnooper().getUniqueID());
        }
    }

    private String func_181538_aA() {
        return this.theIntegratedServer != null
            ? (this.theIntegratedServer.getPublic() ? "hosting_lan" : "singleplayer")
            : (this.currentServerData != null ? (this.currentServerData.isOnLAN() ? "playing_lan" : "multiplayer") : "out_of_game");
    }

    @Override
    public void addServerTypeToSnooper(PlayerUsageSnooper playerSnooper) {
        playerSnooper.addStatToSnooper("opengl_version", GL11.glGetString(7938));
        playerSnooper.addStatToSnooper("opengl_vendor", GL11.glGetString(7936));
        playerSnooper.addStatToSnooper("client_brand", ClientBrandRetriever.getClientModName());
        playerSnooper.addStatToSnooper("launched_version", this.launchedVersion);
        ContextCapabilities capabilities = GLContext.getCapabilities();
        playerSnooper.addStatToSnooper("gl_caps[ARB_multisample]", capabilities.GL_ARB_multisample);
        playerSnooper.addStatToSnooper("gl_caps[ARB_multitexture]", capabilities.GL_ARB_multitexture);
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_buffer_object]", capabilities.GL_ARB_vertex_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_objects]", capabilities.GL_ARB_shader_objects);
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_shader]", capabilities.GL_ARB_fragment_shader);
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_texture]", capabilities.GL_ARB_depth_texture);
        playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_object]", capabilities.GL_ARB_framebuffer_object);
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_object]", capabilities.GL_EXT_framebuffer_object);
        playerSnooper.addStatToSnooper("gl_max_texture_size", getGLMaximumTextureSize());
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_uniforms]", GL11.glGetInteger(35658));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_fragment_uniforms]", GL11.glGetInteger(35657));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_attribs]", GL11.glGetInteger(34921));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_texture_image_units]", GL11.glGetInteger(35660));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(34930));
        GL11.glGetError();
    }

    public static int getGLMaximumTextureSize() {
        for (int i = 16384; i > 0; i >>= 1) {
            GL11.glTexImage2D(32868, 0, GL11.GL_RGBA, i, i, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
            int n = GL11.glGetTexLevelParameteri(32868, 0, 4096);
            if (n != 0) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public boolean isSnooperEnabled() {
        return this.gameSettings.snooperEnabled;
    }

    public void setServerData(ServerData serverDataIn) {
        this.currentServerData = serverDataIn;
    }

    public ServerData getCurrentServerData() {
        return this.currentServerData;
    }

    public boolean isIntegratedServerRunning() {
        return this.integratedServerIsRunning;
    }

    public boolean isSingleplayer() {
        return this.integratedServerIsRunning && this.theIntegratedServer != null;
    }

    public IntegratedServer getIntegratedServer() {
        return this.theIntegratedServer;
    }

    public static void stopIntegratedServer() {
        if (theMinecraft != null) {
            IntegratedServer integratedServer = theMinecraft.getIntegratedServer();
            if (integratedServer != null) {
                integratedServer.stopServer();
            }
        }
    }

    public PlayerUsageSnooper getPlayerUsageSnooper() {
        return this.usageSnooper;
    }

    public static long getSystemTime() {
        return Sys.getTime() * 1000L / Sys.getTimerResolution();
    }

    public boolean isFullScreen() {
        return this.fullscreen;
    }

    public Session getSession() {
        return this.session;
    }

    public PropertyMap getTwitchDetails() {
        return this.twitchDetails;
    }

    public PropertyMap func_181037_M() {
        if (this.field_181038_N.isEmpty()) {
            GameProfile gameProfile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
            this.field_181038_N.putAll(gameProfile.getProperties());
        }

        return this.field_181038_N;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public TextureManager getTextureManager() {
        return this.renderEngine;
    }

    public IResourceManager getResourceManager() {
        return this.mcResourceManager;
    }

    public ResourcePackRepository getResourcePackRepository() {
        return this.mcResourcePackRepository;
    }

    public LanguageManager getLanguageManager() {
        return this.mcLanguageManager;
    }

    public TextureMap getTextureMapBlocks() {
        return this.textureMapBlocks;
    }

    public boolean isJava64bit() {
        return this.jvm64bit;
    }

    public boolean isGamePaused() {
        return this.isGamePaused;
    }

    public SoundHandler getSoundHandler() {
        return this.mcSoundHandler;
    }

    public MusicType getAmbientMusicType() {
        return this.thePlayer != null
            ? (
                this.thePlayer.worldObj.provider instanceof WorldProviderHell
                    ? MusicType.NETHER
                    : (
                        this.thePlayer.worldObj.provider instanceof WorldProviderEnd
                            ? (BossStatus.bossName != null && BossStatus.statusBarTime > 0 ? MusicType.END_BOSS : MusicType.END)
                            : (this.thePlayer.capabilities.isCreativeMode && this.thePlayer.capabilities.allowFlying ? MusicType.CREATIVE : MusicType.GAME)
                    )
            )
            : MusicType.MENU;
    }

    public IStream getTwitchStream() {
        return this.stream;
    }

    public void dispatchKeypresses() {
        int n = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();
        if (n != 0
            && !Keyboard.isRepeatEvent()
            && (!(this.currentScreen instanceof GuiControls) || ((GuiControls)this.currentScreen).time <= getSystemTime() - 20L)) {
            if (Keyboard.getEventKeyState()) {
                if (n == this.gameSettings.keyBindStreamStartStop.getKeyCode()) {
                    if (this.getTwitchStream().isBroadcasting()) {
                        this.getTwitchStream().stopBroadcasting();
                    } else if (this.getTwitchStream().isReadyToBroadcast()) {
                        this.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback() {
                            @Override
                            public void confirmClicked(boolean result, int id) {
                                if (result) {
                                    Minecraft.this.getTwitchStream().func_152930_t();
                                }

                                Minecraft.this.displayGuiScreen((GuiScreen)null);
                            }
                        }, I18n.format("stream.confirm_start"), "", 0));
                    } else if (!this.getTwitchStream().func_152928_D() || !this.getTwitchStream().func_152936_l()) {
                        GuiStreamUnavailable.func_152321_a(this.currentScreen);
                    } else if (this.theWorld != null) {
                        this.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("Not ready to start streaming yet!"));
                    }
                } else if (n == this.gameSettings.keyBindStreamPauseUnpause.getKeyCode()) {
                    if (this.getTwitchStream().isBroadcasting()) {
                        if (this.getTwitchStream().isPaused()) {
                            this.getTwitchStream().unpause();
                        } else {
                            this.getTwitchStream().pause();
                        }
                    }
                } else if (n == this.gameSettings.keyBindStreamCommercials.getKeyCode()) {
                    if (this.getTwitchStream().isBroadcasting()) {
                        this.getTwitchStream().requestCommercial();
                    }
                } else if (n == this.gameSettings.keyBindStreamToggleMic.getKeyCode()) {
                    this.stream.muteMicrophone(true);
                } else if (n == this.gameSettings.keyBindFullscreen.getKeyCode()) {
                    this.toggleFullscreen();
                } else if (n == this.gameSettings.keyBindScreenshot.getKeyCode()) {
                    this.ingameGUI
                        .getChatGUI()
                        .printChatMessage(ScreenShotHelper.saveScreenshot(this.mcDataDir, this.displayWidth, this.displayHeight, this.framebufferMc));
                }
            } else if (n == this.gameSettings.keyBindStreamToggleMic.getKeyCode()) {
                this.stream.muteMicrophone(false);
            }
        }
    }

    public static GuiScreen getCurrentScreen() {
        return getMinecraft().currentScreen;
    }

    public MinecraftSessionService getSessionService() {
        return this.sessionService;
    }

    public SkinManager getSkinManager() {
        return this.skinManager;
    }

    public Entity getRenderViewEntity() {
        return this.renderViewEntity;
    }

    public void setRenderViewEntity(Entity viewingEntity) {
        this.renderViewEntity = viewingEntity;
        this.entityRenderer.loadEntityShader(viewingEntity);
    }

    public <V> ListenableFuture<V> addScheduledTask(Callable<V> callableToSchedule) {
        Validate.notNull(callableToSchedule);
        if (!this.isCallingFromMinecraftThread()) {
            ListenableFutureTask listenableFutureTask = ListenableFutureTask.create(callableToSchedule);
            synchronized (this.scheduledTasks) {
                this.scheduledTasks.add(listenableFutureTask);
                return listenableFutureTask;
            }
        } else {
            try {
                return Futures.immediateFuture((V)callableToSchedule.call());
            } catch (Exception e) {
                return Futures.immediateFailedFuture(e);
            }
        }
    }

    @Override
    public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
        Validate.notNull(runnableToSchedule);
        return this.addScheduledTask(Executors.callable(runnableToSchedule));
    }

    @Override
    public boolean isCallingFromMinecraftThread() {
        return Thread.currentThread() == this.mcThread;
    }

    public BlockRendererDispatcher getBlockRendererDispatcher() {
        return this.blockRenderDispatcher;
    }

    public RenderManager getRenderManager() {
        return this.renderManager;
    }

    public RenderItem getRenderItem() {
        return this.renderItem;
    }

    public ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }

    public static int getDebugFPS() {
        return debugFPS;
    }

    public FrameTimer func_181539_aj() {
        return this.field_181542_y;
    }

    public static Map<String, String> getSessionInfo() {
        HashMap map = Maps.newHashMap();
        map.put("X-Minecraft-Username", getMinecraft().getSession().getUsername());
        map.put("X-Minecraft-UUID", getMinecraft().getSession().getPlayerID());
        map.put("X-Minecraft-Version", "1.8.9");
        return map;
    }

    public boolean func_181540_al() {
        return this.field_181541_X;
    }

    public void func_181537_a(boolean p_181537_1_) {
        this.field_181541_X = p_181537_1_;
    }

    public static EntityRenderer getEntityRenderer() {
        return getMinecraft().entityRenderer;
    }

    @Generated
    public void setSession(Session session) {
        this.session = session;
    }
}
