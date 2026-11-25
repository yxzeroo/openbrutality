package net.minecraft.client.settings;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.stream.TwitchStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.optifine.ClearWater;
import net.optifine.CustomColors;
import net.optifine.CustomGuis;
import net.optifine.CustomSky;
import net.optifine.DynamicLights;
import net.optifine.Lang;
import net.optifine.NaturalTextures;
import net.optifine.RandomEntities;
import net.optifine.shaders.Shaders;
import net.optifine.util.KeyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brutality.utils.input.KeyMouseHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class GameSettings {
    private static final Logger logger = LogManager.getLogger();
    private static final Gson gson = new Gson();
    private static final ParameterizedType typeListString = new ParameterizedType() {
        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{String.class};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    };
    private static final String[] GUISCALES = new String[]{
        "options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"
    };
    private static final String[] PARTICLES = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
    private static final String[] AMBIENT_OCCLUSIONS = new String[]{"options.ao.off", "options.ao.min", "options.ao.max"};
    private static final String[] STREAM_COMPRESSIONS = new String[]{
        "options.stream.compression.low", "options.stream.compression.medium", "options.stream.compression.high"
    };
    private static final String[] STREAM_CHAT_MODES = new String[]{
        "options.stream.chat.enabled.streaming", "options.stream.chat.enabled.always", "options.stream.chat.enabled.never"
    };
    private static final String[] STREAM_CHAT_FILTER_MODES = new String[]{
        "options.stream.chat.userFilter.all", "options.stream.chat.userFilter.subs", "options.stream.chat.userFilter.mods"
    };
    private static final String[] STREAM_MIC_MODES = new String[]{"options.stream.mic_toggle.mute", "options.stream.mic_toggle.talk"};
    private static final String[] field_181149_aW = new String[]{"options.off", "options.graphics.fast", "options.graphics.fancy"};
    public float mouseSensitivity = 0.5F;
    public boolean invertMouse;
    public int renderDistanceChunks = -1;
    public boolean viewBobbing = true;
    public boolean anaglyph;
    public boolean fboEnable = true;
    public int limitFramerate = 120;
    public int clouds = 2;
    public boolean fancyGraphics = true;
    public int ambientOcclusion = 2;
    public List<String> resourcePacks = Lists.newArrayList();
    public List<String> field_183018_l = Lists.newArrayList();
    public EntityPlayer.EnumChatVisibility chatVisibility = EntityPlayer.EnumChatVisibility.FULL;
    public boolean chatColours = true;
    public boolean chatLinks = true;
    public boolean chatLinksPrompt = true;
    public float chatOpacity = 1.0F;
    public boolean snooperEnabled = true;
    public boolean fullScreen;
    public boolean borderlessFullscreen;
    public boolean enableVsync = true;
    public boolean useVbo = false;
    public boolean allowBlockAlternatives = true;
    public boolean reducedDebugInfo = false;
    public boolean hideServerAddress;
    public boolean advancedItemTooltips;
    public boolean pauseOnLostFocus = true;
    private final Set<EnumPlayerModelParts> setModelParts = Sets.newHashSet(EnumPlayerModelParts.values());
    public boolean touchscreen;
    public int overrideWidth;
    public int overrideHeight;
    public boolean heldItemTooltips = true;
    public float chatScale = 1.0F;
    public float chatWidth = 1.0F;
    public float chatHeightUnfocused = 0.44366196F;
    public float chatHeightFocused = 1.0F;
    public boolean showInventoryAchievementHint = true;
    public int mipmapLevels = 4;
    private Map<SoundCategory, Float> mapSoundLevels = Maps.newEnumMap(SoundCategory.class);
    public float streamBytesPerPixel = 0.5F;
    public float streamMicVolume = 1.0F;
    public float streamGameVolume = 1.0F;
    public float streamKbps = 0.5412844F;
    public float streamFps = 0.31690142F;
    public int streamCompression = 1;
    public boolean streamSendMetadata = true;
    public String streamPreferredServer = "";
    public int streamChatEnabled = 0;
    public int streamChatUserFilter = 0;
    public int streamMicToggleBehavior = 0;
    public boolean field_181150_U = true;
    public boolean field_181151_V = true;
    public boolean field_183509_X = true;
    public KeyBinding keyBindForward = new KeyBinding("key.forward", 17, "key.categories.movement");
    public KeyBinding keyBindLeft = new KeyBinding("key.left", 30, "key.categories.movement");
    public KeyBinding keyBindBack = new KeyBinding("key.back", 31, "key.categories.movement");
    public KeyBinding keyBindRight = new KeyBinding("key.right", 32, "key.categories.movement");
    public KeyBinding keyBindJump = new KeyBinding("key.jump", 57, "key.categories.movement");
    public KeyBinding keyBindSneak = new KeyBinding("key.sneak", 42, "key.categories.movement");
    public KeyBinding keyBindSprint = new KeyBinding("key.sprint", 29, "key.categories.movement");
    public KeyBinding keyBindInventory = new KeyBinding("key.inventory", 18, "key.categories.inventory");
    public KeyBinding keyBindSlotLock = new KeyBinding("key.slotlock", 0, "key.categories.slotlock");
    public KeyBinding keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
    public KeyBinding keyBindDrop = new KeyBinding("key.drop", 16, "key.categories.gameplay");
    public KeyBinding keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
    public KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
    public KeyBinding keyBindChat = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
    public KeyBinding keyBindPlayerList = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
    public KeyBinding keyBindCommand = new KeyBinding("key.command", 53, "key.categories.multiplayer");
    public KeyBinding keyBindScreenshot = new KeyBinding("key.screenshot", 60, "key.categories.misc");
    public KeyBinding keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
    public KeyBinding keyBindSmoothCamera = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
    public KeyBinding keyBindFullscreen = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
    public KeyBinding keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", 0, "key.categories.misc");
    public KeyBinding keyBindStreamStartStop = new KeyBinding("key.streamStartStop", 64, "key.categories.stream");
    public KeyBinding keyBindStreamPauseUnpause = new KeyBinding("key.streamPauseUnpause", 65, "key.categories.stream");
    public KeyBinding keyBindStreamCommercials = new KeyBinding("key.streamCommercial", 0, "key.categories.stream");
    public KeyBinding keyBindStreamToggleMic = new KeyBinding("key.streamToggleMic", 0, "key.categories.stream");
    public KeyBinding[] keyBindsHotbar = new KeyBinding[]{
        new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"),
        new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"),
        new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"),
        new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"),
        new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"),
        new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"),
        new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"),
        new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"),
        new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")
    };
    public KeyBinding[] keyBindings;
    protected Minecraft mc;
    private File optionsFile;
    public EnumDifficulty difficulty;
    public boolean hideGUI;
    public int thirdPersonView;
    public boolean showDebugInfo;
    public boolean showDebugProfilerChart;
    public boolean field_181657_aC;
    public String lastServer;
    public boolean smoothCamera;
    public boolean debugCamEnable;
    public float fovSetting;
    public float gammaSetting;
    public float saturation;
    public int guiScale;
    public int particleSetting;
    public String language;
    public boolean forceUnicodeFont;
    public int ofFogType = 1;
    public float ofFogStart = 0.8F;
    public int ofMipmapType = 0;
    public boolean ofOcclusionFancy = false;
    public boolean ofSmoothFps = false;
    public boolean ofSmoothWorld = Config.isSingleProcessor();
    public boolean ofLazyChunkLoading = Config.isSingleProcessor();
    public boolean ofRenderRegions = false;
    public boolean ofSmartAnimations = false;
    public float ofAoLevel = 1.0F;
    public int ofAaLevel = 0;
    public int ofAfLevel = 1;
    public int ofClouds = 0;
    public float ofCloudsHeight = 0.0F;
    public int ofTrees = 0;
    public int ofRain = 0;
    public int ofDroppedItems = 0;
    public int ofBetterGrass = 3;
    public int ofAutoSaveTicks = 4000;
    public boolean ofLagometer = false;
    public boolean ofProfiler = false;
    public boolean ofShowFps = false;
    public boolean ofWeather = true;
    public boolean ofSky = true;
    public boolean ofStars = true;
    public boolean ofSunMoon = true;
    public int ofVignette = 0;
    public int ofChunkUpdates = 1;
    public boolean ofChunkUpdatesDynamic = false;
    public int ofTime = 0;
    public boolean ofClearWater = false;
    public boolean ofBetterSnow = false;
    public String ofFullscreenMode = "Default";
    public boolean ofSwampColors = true;
    public boolean ofRandomEntities = true;
    public boolean ofSmoothBiomes = true;
    public boolean ofCustomFonts = true;
    public boolean ofCustomColors = true;
    public boolean ofCustomSky = true;
    public boolean ofShowCapes = true;
    public int ofConnectedTextures = 2;
    public boolean ofCustomItems = true;
    public boolean ofNaturalTextures = false;
    public boolean ofEmissiveTextures = true;
    public boolean ofFastMath = false;
    public boolean ofFastRender = false;
    public int ofTranslucentBlocks = 0;
    public boolean ofDynamicFov = true;
    public boolean ofAlternateBlocks = true;
    public int ofDynamicLights = 3;
    public boolean ofCustomEntityModels = true;
    public boolean ofCustomGuis = true;
    public boolean ofShowGlErrors = true;
    public int ofScreenshotSize = 1;
    public int ofAnimatedWater = 0;
    public int ofAnimatedLava = 0;
    public boolean ofAnimatedFire = true;
    public boolean ofAnimatedPortal = true;
    public boolean ofAnimatedRedstone = true;
    public boolean ofAnimatedExplosion = true;
    public boolean ofAnimatedFlame = true;
    public boolean ofAnimatedSmoke = true;
    public boolean ofVoidParticles = true;
    public boolean ofWaterParticles = true;
    public boolean ofRainSplash = true;
    public boolean ofPortalParticles = true;
    public boolean ofPotionParticles = true;
    public boolean ofFireworkParticles = true;
    public boolean ofDrippingWaterLava = true;
    public boolean ofAnimatedTerrain = true;
    public boolean ofAnimatedTextures = true;
    public static final int DEFAULT = 0;
    public static final int FAST = 1;
    public static final int FANCY = 2;
    public static final int OFF = 3;
    public static final int SMART = 4;
    public static final int ANIM_ON = 0;
    public static final int ANIM_GENERATED = 1;
    public static final int ANIM_OFF = 2;
    public static final String DEFAULT_STR = "Default";
    private static final int[] OF_TREES_VALUES = new int[]{0, 1, 4, 2};
    private static final int[] OF_DYNAMIC_LIGHTS = new int[]{3, 1, 2};
    private static final String[] KEYS_DYNAMIC_LIGHTS = new String[]{"options.off", "options.graphics.fast", "options.graphics.fancy"};
    public KeyBinding ofKeyBindZoom;
    private File optionsFileOF;

    public GameSettings(Minecraft mcIn, File p_i46326_2_) {
        this.keyBindings = ArrayUtils.addAll(
            new KeyBinding[]{
                this.keyBindAttack,
                this.keyBindUseItem,
                this.keyBindForward,
                this.keyBindLeft,
                this.keyBindBack,
                this.keyBindRight,
                this.keyBindJump,
                this.keyBindSneak,
                this.keyBindSprint,
                this.keyBindDrop,
                this.keyBindInventory,
                this.keyBindSlotLock,
                this.keyBindChat,
                this.keyBindPlayerList,
                this.keyBindPickBlock,
                this.keyBindCommand,
                this.keyBindScreenshot,
                this.keyBindTogglePerspective,
                this.keyBindSmoothCamera,
                this.keyBindStreamStartStop,
                this.keyBindStreamPauseUnpause,
                this.keyBindStreamCommercials,
                this.keyBindStreamToggleMic,
                this.keyBindFullscreen,
                this.keyBindSpectatorOutlines
            },
            this.keyBindsHotbar
        );
        this.difficulty = EnumDifficulty.NORMAL;
        this.lastServer = "";
        this.fovSetting = 70.0F;
        this.language = "en_US";
        this.forceUnicodeFont = false;
        this.mc = mcIn;
        this.optionsFile = new File(p_i46326_2_, "options.txt");
        if (mcIn.isJava64bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
            GameSettings.Options.RENDER_DISTANCE.setValueMax(32.0F);
            long l = 1000000L;
            if (Runtime.getRuntime().maxMemory() >= 1500L * l) {
                GameSettings.Options.RENDER_DISTANCE.setValueMax(48.0F);
            }

            if (Runtime.getRuntime().maxMemory() >= 2500L * l) {
                GameSettings.Options.RENDER_DISTANCE.setValueMax(64.0F);
            }
        } else {
            GameSettings.Options.RENDER_DISTANCE.setValueMax(16.0F);
        }

        this.renderDistanceChunks = mcIn.isJava64bit() ? 12 : 8;
        this.optionsFileOF = new File(p_i46326_2_, "optionsof.txt");
        this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
        this.ofKeyBindZoom = new KeyBinding("of.key.zoom", 46, "key.categories.misc");
        this.keyBindings = ArrayUtils.add(this.keyBindings, this.ofKeyBindZoom);
        KeyUtils.fixKeyConflicts(this.keyBindings, new KeyBinding[]{this.ofKeyBindZoom});
        this.renderDistanceChunks = 8;
        this.loadOptions();
        Config.initGameSettings(this);
    }

    public GameSettings() {
        this.keyBindings = ArrayUtils.addAll(
            new KeyBinding[]{
                this.keyBindAttack,
                this.keyBindUseItem,
                this.keyBindForward,
                this.keyBindLeft,
                this.keyBindBack,
                this.keyBindRight,
                this.keyBindJump,
                this.keyBindSneak,
                this.keyBindSprint,
                this.keyBindDrop,
                this.keyBindInventory,
                this.keyBindSlotLock,
                this.keyBindChat,
                this.keyBindPlayerList,
                this.keyBindPickBlock,
                this.keyBindCommand,
                this.keyBindScreenshot,
                this.keyBindTogglePerspective,
                this.keyBindSmoothCamera,
                this.keyBindStreamStartStop,
                this.keyBindStreamPauseUnpause,
                this.keyBindStreamCommercials,
                this.keyBindStreamToggleMic,
                this.keyBindFullscreen,
                this.keyBindSpectatorOutlines
            },
            this.keyBindsHotbar
        );
        this.difficulty = EnumDifficulty.NORMAL;
        this.lastServer = "";
        this.fovSetting = 70.0F;
        this.language = "en_US";
        this.forceUnicodeFont = false;
    }

    public static String getKeyDisplayString(int p_74298_0_) {
        return p_74298_0_ < 0
            ? I18n.format("key.mouseButton", p_74298_0_ + 101)
            : (p_74298_0_ < 256 ? Keyboard.getKeyName(p_74298_0_) : String.format("%c", (char)(p_74298_0_ - 256)).toUpperCase());
    }

    public static boolean isKeyDown(KeyBinding p_100015_0_) {
        if (p_100015_0_ == null) {
            return false;
        }
        int keyCode = p_100015_0_.getKeyCode();
        if (keyCode == 0) {
            return false;
        }
        try {
            if (keyCode >= 0) {
                return Keyboard.isKeyDown(keyCode);
            }
            int n = keyCode + 100;
            return n >= 0 && n < 16 && Mouse.isButtonDown(n);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public void setOptionKeyBinding(KeyBinding p_151440_1_, int p_151440_2_) {
        p_151440_1_.setKeyCode(p_151440_2_);
        this.saveOptions();
    }

    public void setOptionFloatValue(GameSettings.Options p_74304_1_, float p_74304_2_) {
        this.setOptionFloatValueOF(p_74304_1_, p_74304_2_);
        if (p_74304_1_ == GameSettings.Options.SENSITIVITY) {
            this.mouseSensitivity = p_74304_2_;
        }

        if (p_74304_1_ == GameSettings.Options.FOV) {
            this.fovSetting = p_74304_2_;
        }

        if (p_74304_1_ == GameSettings.Options.GAMMA) {
            this.gammaSetting = p_74304_2_;
        }

        if (p_74304_1_ == GameSettings.Options.FRAMERATE_LIMIT) {
            this.limitFramerate = (int)p_74304_2_;
            this.enableVsync = false;
            if (this.limitFramerate <= 0) {
                this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                this.enableVsync = true;
            }

            this.updateVSync();
        }

        if (p_74304_1_ == GameSettings.Options.CHAT_OPACITY) {
            this.chatOpacity = p_74304_2_;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (p_74304_1_ == GameSettings.Options.CHAT_HEIGHT_FOCUSED) {
            this.chatHeightFocused = p_74304_2_;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (p_74304_1_ == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED) {
            this.chatHeightUnfocused = p_74304_2_;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (p_74304_1_ == GameSettings.Options.CHAT_WIDTH) {
            this.chatWidth = p_74304_2_;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (p_74304_1_ == GameSettings.Options.CHAT_SCALE) {
            this.chatScale = p_74304_2_;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (p_74304_1_ == GameSettings.Options.MIPMAP_LEVELS) {
            int mipmapLevels = this.mipmapLevels;
            this.mipmapLevels = (int)p_74304_2_;
            if (mipmapLevels != p_74304_2_) {
                this.mc.getTextureMapBlocks().setMipmapLevels(this.mipmapLevels);
                this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                this.mc.getTextureMapBlocks().setBlurMipmapDirect(false, this.mipmapLevels > 0);
                this.mc.scheduleResourcesRefresh();
            }
        }

        if (p_74304_1_ == GameSettings.Options.BLOCK_ALTERNATIVES) {
            this.allowBlockAlternatives = !this.allowBlockAlternatives;
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_74304_1_ == GameSettings.Options.RENDER_DISTANCE) {
            this.renderDistanceChunks = (int)p_74304_2_;
            this.mc.renderGlobal.setDisplayListEntitiesDirty();
        }

        if (p_74304_1_ == GameSettings.Options.STREAM_BYTES_PER_PIXEL) {
            this.streamBytesPerPixel = p_74304_2_;
        }

        if (p_74304_1_ == GameSettings.Options.STREAM_VOLUME_MIC) {
            this.streamMicVolume = p_74304_2_;
            this.mc.getTwitchStream().updateStreamVolume();
        }

        if (p_74304_1_ == GameSettings.Options.STREAM_VOLUME_SYSTEM) {
            this.streamGameVolume = p_74304_2_;
            this.mc.getTwitchStream().updateStreamVolume();
        }

        if (p_74304_1_ == GameSettings.Options.STREAM_KBPS) {
            this.streamKbps = p_74304_2_;
        }

        if (p_74304_1_ == GameSettings.Options.STREAM_FPS) {
            this.streamFps = p_74304_2_;
        }
    }

    public void setOptionValue(GameSettings.Options p_74306_1_, int p_74306_2_) {
        this.setOptionValueOF(p_74306_1_, p_74306_2_);
        if (p_74306_1_ == GameSettings.Options.INVERT_MOUSE) {
            this.invertMouse = !this.invertMouse;
        }

        if (p_74306_1_ == GameSettings.Options.GUI_SCALE) {
            this.guiScale += p_74306_2_;
            if (GuiScreen.isShiftKeyDown()) {
                this.guiScale = 0;
            }

            DisplayMode largestDisplayMode = Config.getLargestDisplayMode();
            int n = largestDisplayMode.getWidth() / 320;
            int n2 = largestDisplayMode.getHeight() / 240;
            int n3 = Math.min(n, n2);
            if (this.guiScale < 0) {
                this.guiScale = n3 - 1;
            }

            if (this.mc.isUnicode() && this.guiScale % 2 != 0) {
                this.guiScale += p_74306_2_;
            }

            if (this.guiScale < 0 || this.guiScale >= n3) {
                this.guiScale = 0;
            }
        }

        if (p_74306_1_ == GameSettings.Options.PARTICLES) {
            this.particleSetting = (this.particleSetting + p_74306_2_) % 3;
        }

        if (p_74306_1_ == GameSettings.Options.VIEW_BOBBING) {
            this.viewBobbing = !this.viewBobbing;
        }

        if (p_74306_1_ == GameSettings.Options.RENDER_CLOUDS) {
            this.clouds = (this.clouds + p_74306_2_) % 3;
        }

        if (p_74306_1_ == GameSettings.Options.FORCE_UNICODE_FONT) {
            this.forceUnicodeFont = !this.forceUnicodeFont;
            this.mc.fontRendererObj.setUnicodeFlag(this.mc.getLanguageManager().isCurrentLocaleUnicode() || this.forceUnicodeFont);
        }

        if (p_74306_1_ == GameSettings.Options.FBO_ENABLE) {
            this.fboEnable = !this.fboEnable;
        }

        if (p_74306_1_ == GameSettings.Options.ANAGLYPH) {
            if (!this.anaglyph && Config.isShaders()) {
                Config.showGuiMessage(Lang.get("of.message.an.shaders1"), Lang.get("of.message.an.shaders2"));
                return;
            }

            this.anaglyph = !this.anaglyph;
            this.mc.refreshResources();
        }

        if (p_74306_1_ == GameSettings.Options.GRAPHICS) {
            this.fancyGraphics = !this.fancyGraphics;
            this.updateRenderClouds();
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_74306_1_ == GameSettings.Options.AMBIENT_OCCLUSION) {
            this.ambientOcclusion = (this.ambientOcclusion + p_74306_2_) % 3;
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_74306_1_ == GameSettings.Options.CHAT_VISIBILITY) {
            this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility((this.chatVisibility.getChatVisibility() + p_74306_2_) % 3);
        }

        if (p_74306_1_ == GameSettings.Options.STREAM_COMPRESSION) {
            this.streamCompression = (this.streamCompression + p_74306_2_) % 3;
        }

        if (p_74306_1_ == GameSettings.Options.STREAM_SEND_METADATA) {
            this.streamSendMetadata = !this.streamSendMetadata;
        }

        if (p_74306_1_ == GameSettings.Options.STREAM_CHAT_ENABLED) {
            this.streamChatEnabled = (this.streamChatEnabled + p_74306_2_) % 3;
        }

        if (p_74306_1_ == GameSettings.Options.STREAM_CHAT_USER_FILTER) {
            this.streamChatUserFilter = (this.streamChatUserFilter + p_74306_2_) % 3;
        }

        if (p_74306_1_ == GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
            this.streamMicToggleBehavior = (this.streamMicToggleBehavior + p_74306_2_) % 2;
        }

        if (p_74306_1_ == GameSettings.Options.CHAT_COLOR) {
            this.chatColours = !this.chatColours;
        }

        if (p_74306_1_ == GameSettings.Options.CHAT_LINKS) {
            this.chatLinks = !this.chatLinks;
        }

        if (p_74306_1_ == GameSettings.Options.CHAT_LINKS_PROMPT) {
            this.chatLinksPrompt = !this.chatLinksPrompt;
        }

        if (p_74306_1_ == GameSettings.Options.SNOOPER_ENABLED) {
            this.snooperEnabled = !this.snooperEnabled;
        }

        if (p_74306_1_ == GameSettings.Options.TOUCHSCREEN) {
            this.touchscreen = !this.touchscreen;
        }

        if (p_74306_1_ == GameSettings.Options.USE_FULLSCREEN) {
            this.fullScreen = !this.fullScreen;
            if (this.mc.isFullScreen() != this.fullScreen) {
                this.mc.toggleFullscreen();
            }
        }

        if (p_74306_1_ == GameSettings.Options.ENABLE_VSYNC) {
            this.enableVsync = !this.enableVsync;
            Display.setVSyncEnabled(this.enableVsync);
        }

        if (p_74306_1_ == GameSettings.Options.USE_VBO) {
            this.useVbo = !this.useVbo;
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_74306_1_ == GameSettings.Options.BLOCK_ALTERNATIVES) {
            this.allowBlockAlternatives = !this.allowBlockAlternatives;
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_74306_1_ == GameSettings.Options.REDUCED_DEBUG_INFO) {
            this.reducedDebugInfo = !this.reducedDebugInfo;
        }

        if (p_74306_1_ == GameSettings.Options.ENTITY_SHADOWS) {
            this.field_181151_V = !this.field_181151_V;
        }

        if (p_74306_1_ == GameSettings.Options.REALMS_NOTIFICATIONS) {
            this.field_183509_X = !this.field_183509_X;
        }

        this.saveOptions();
    }

    public float getOptionFloatValue(GameSettings.Options p_74296_1_) {
        float optionFloatValueOF = this.getOptionFloatValueOF(p_74296_1_);
        return optionFloatValueOF != Float.MAX_VALUE
            ? optionFloatValueOF
            : (
                p_74296_1_ == GameSettings.Options.FOV
                    ? this.fovSetting
                    : (
                        p_74296_1_ == GameSettings.Options.GAMMA
                            ? this.gammaSetting
                            : (
                                p_74296_1_ == GameSettings.Options.SATURATION
                                    ? this.saturation
                                    : (
                                        p_74296_1_ == GameSettings.Options.SENSITIVITY
                                            ? this.mouseSensitivity
                                            : (
                                                p_74296_1_ == GameSettings.Options.CHAT_OPACITY
                                                    ? this.chatOpacity
                                                    : (
                                                        p_74296_1_ == GameSettings.Options.CHAT_HEIGHT_FOCUSED
                                                            ? this.chatHeightFocused
                                                            : (
                                                                p_74296_1_ == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED
                                                                    ? this.chatHeightUnfocused
                                                                    : (
                                                                        p_74296_1_ == GameSettings.Options.CHAT_SCALE
                                                                            ? this.chatScale
                                                                            : (
                                                                                p_74296_1_ == GameSettings.Options.CHAT_WIDTH
                                                                                    ? this.chatWidth
                                                                                    : (
                                                                                        p_74296_1_ == GameSettings.Options.FRAMERATE_LIMIT
                                                                                            ? this.limitFramerate
                                                                                            : (
                                                                                                p_74296_1_ == GameSettings.Options.MIPMAP_LEVELS
                                                                                                    ? this.mipmapLevels
                                                                                                    : (
                                                                                                        p_74296_1_ == GameSettings.Options.RENDER_DISTANCE
                                                                                                            ? this.renderDistanceChunks
                                                                                                            : (
                                                                                                                p_74296_1_
                                                                                                                        == GameSettings.Options.STREAM_BYTES_PER_PIXEL
                                                                                                                    ? this.streamBytesPerPixel
                                                                                                                    : (
                                                                                                                        p_74296_1_
                                                                                                                                == GameSettings.Options.STREAM_VOLUME_MIC
                                                                                                                            ? this.streamMicVolume
                                                                                                                            : (
                                                                                                                                p_74296_1_
                                                                                                                                        == GameSettings.Options.STREAM_VOLUME_SYSTEM
                                                                                                                                    ? this.streamGameVolume
                                                                                                                                    : (
                                                                                                                                        p_74296_1_
                                                                                                                                                == GameSettings.Options.STREAM_KBPS
                                                                                                                                            ? this.streamKbps
                                                                                                                                            : (
                                                                                                                                                p_74296_1_
                                                                                                                                                        == GameSettings.Options.STREAM_FPS
                                                                                                                                                    ? this.streamFps
                                                                                                                                                    : 0.0F
                                                                                                                                            )
                                                                                                                                    )
                                                                                                                            )
                                                                                                                    )
                                                                                                            )
                                                                                                    )
                                                                                            )
                                                                                    )
                                                                            )
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
            );
    }

    public boolean getOptionOrdinalValue(GameSettings.Options p_74308_1_) {
        switch (p_74308_1_) {
            case INVERT_MOUSE:
                return this.invertMouse;
            case VIEW_BOBBING:
                return this.viewBobbing;
            case ANAGLYPH:
                return this.anaglyph;
            case FBO_ENABLE:
                return this.fboEnable;
            case CHAT_COLOR:
                return this.chatColours;
            case CHAT_LINKS:
                return this.chatLinks;
            case CHAT_LINKS_PROMPT:
                return this.chatLinksPrompt;
            case SNOOPER_ENABLED:
                return this.snooperEnabled;
            case USE_FULLSCREEN:
                return this.fullScreen;
            case ENABLE_VSYNC:
                return this.enableVsync;
            case USE_VBO:
                return this.useVbo;
            case TOUCHSCREEN:
                return this.touchscreen;
            case STREAM_SEND_METADATA:
                return this.streamSendMetadata;
            case FORCE_UNICODE_FONT:
                return this.forceUnicodeFont;
            case BLOCK_ALTERNATIVES:
                return this.allowBlockAlternatives;
            case REDUCED_DEBUG_INFO:
                return this.reducedDebugInfo;
            case ENTITY_SHADOWS:
                return this.field_181151_V;
            case REALMS_NOTIFICATIONS:
                return this.field_183509_X;
            default:
                return false;
        }
    }

    private static String getTranslation(String[] p_74299_0_, int p_74299_1_) {
        if (p_74299_1_ < 0 || p_74299_1_ >= p_74299_0_.length) {
            p_74299_1_ = 0;
        }

        return I18n.format(p_74299_0_[p_74299_1_]);
    }

    public String getKeyBinding(GameSettings.Options p_74297_1_) {
        String keyBindingOF = this.getKeyBindingOF(p_74297_1_);
        if (keyBindingOF != null) {
            return keyBindingOF;
        }
        String str = I18n.format(p_74297_1_.getEnumString()) + ": ";
        if (!p_74297_1_.getEnumFloat()) {
            if (p_74297_1_.getEnumBoolean()) {
                boolean optionOrdinalValue = this.getOptionOrdinalValue(p_74297_1_);
                return optionOrdinalValue ? str + I18n.format("options.on") : str + I18n.format("options.off");
            }
            if (p_74297_1_ == GameSettings.Options.GUI_SCALE) {
                return this.guiScale >= GUISCALES.length ? str + this.guiScale + "x" : str + getTranslation(GUISCALES, this.guiScale);
            }
            if (p_74297_1_ == GameSettings.Options.CHAT_VISIBILITY) {
                return str + I18n.format(this.chatVisibility.getResourceKey());
            }
            if (p_74297_1_ == GameSettings.Options.PARTICLES) {
                return str + getTranslation(PARTICLES, this.particleSetting);
            }
            if (p_74297_1_ == GameSettings.Options.AMBIENT_OCCLUSION) {
                return str + getTranslation(AMBIENT_OCCLUSIONS, this.ambientOcclusion);
            }
            if (p_74297_1_ == GameSettings.Options.STREAM_COMPRESSION) {
                return str + getTranslation(STREAM_COMPRESSIONS, this.streamCompression);
            }
            if (p_74297_1_ == GameSettings.Options.STREAM_CHAT_ENABLED) {
                return str + getTranslation(STREAM_CHAT_MODES, this.streamChatEnabled);
            }
            if (p_74297_1_ == GameSettings.Options.STREAM_CHAT_USER_FILTER) {
                return str + getTranslation(STREAM_CHAT_FILTER_MODES, this.streamChatUserFilter);
            }
            if (p_74297_1_ == GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
                return str + getTranslation(STREAM_MIC_MODES, this.streamMicToggleBehavior);
            }
            if (p_74297_1_ == GameSettings.Options.RENDER_CLOUDS) {
                return str + getTranslation(field_181149_aW, this.clouds);
            }
            if (p_74297_1_ == GameSettings.Options.GRAPHICS) {
                if (this.fancyGraphics) {
                    return str + I18n.format("options.graphics.fancy");
                }
                String str2 = "options.graphics.fast";
                return str + I18n.format("options.graphics.fast");
            }
            return str;
        }
        float optionFloatValue = this.getOptionFloatValue(p_74297_1_);
        float f = p_74297_1_.normalizeValue(optionFloatValue);
        return p_74297_1_ == GameSettings.Options.MIPMAP_LEVELS && optionFloatValue >= 4.0
            ? str + Lang.get("of.general.max")
            : (
                p_74297_1_ == GameSettings.Options.SENSITIVITY
                    ? (
                        f == 0.0F
                            ? str + I18n.format("options.sensitivity.min")
                            : (f == 1.0F ? str + I18n.format("options.sensitivity.max") : str + (int)(f * 200.0F) + "%")
                    )
                    : (
                        p_74297_1_ == GameSettings.Options.FOV
                            ? (
                                optionFloatValue == 70.0F
                                    ? str + I18n.format("options.fov.min")
                                    : (optionFloatValue == 110.0F ? str + I18n.format("options.fov.max") : str + (int)optionFloatValue)
                            )
                            : (
                                p_74297_1_ == GameSettings.Options.FRAMERATE_LIMIT
                                    ? (optionFloatValue == p_74297_1_.valueMax ? str + I18n.format("options.framerateLimit.max") : str + (int)optionFloatValue + " fps")
                                    : (
                                        p_74297_1_ == GameSettings.Options.RENDER_CLOUDS
                                            ? (optionFloatValue == p_74297_1_.valueMin ? str + I18n.format("options.cloudHeight.min") : str + ((int)optionFloatValue + 128))
                                            : (
                                                p_74297_1_ == GameSettings.Options.GAMMA
                                                    ? (
                                                        f == 0.0F
                                                            ? str + I18n.format("options.gamma.min")
                                                            : (
                                                                f == 1.0F
                                                                    ? str + I18n.format("options.gamma.max")
                                                                    : str + "+" + (int)(f * 100.0F) + "%"
                                                            )
                                                    )
                                                    : (
                                                        p_74297_1_ == GameSettings.Options.SATURATION
                                                            ? str + (int)(f * 400.0F) + "%"
                                                            : (
                                                                p_74297_1_ == GameSettings.Options.CHAT_OPACITY
                                                                    ? str + (int)(f * 90.0F + 10.0F) + "%"
                                                                    : (
                                                                        p_74297_1_ == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED
                                                                            ? str + GuiNewChat.calculateChatboxHeight(f) + "px"
                                                                            : (
                                                                                p_74297_1_ == GameSettings.Options.CHAT_HEIGHT_FOCUSED
                                                                                    ? str + GuiNewChat.calculateChatboxHeight(f) + "px"
                                                                                    : (
                                                                                        p_74297_1_ == GameSettings.Options.CHAT_WIDTH
                                                                                            ? str + GuiNewChat.calculateChatboxWidth(f) + "px"
                                                                                            : (
                                                                                                p_74297_1_ == GameSettings.Options.RENDER_DISTANCE
                                                                                                    ? str + (int)optionFloatValue + " chunks"
                                                                                                    : (
                                                                                                        p_74297_1_ == GameSettings.Options.MIPMAP_LEVELS
                                                                                                            ? (
                                                                                                                optionFloatValue == 0.0F
                                                                                                                    ? str + I18n.format("options.off")
                                                                                                                    : str + (int)optionFloatValue
                                                                                                            )
                                                                                                            : (
                                                                                                                p_74297_1_ == GameSettings.Options.STREAM_FPS
                                                                                                                    ? str
                                                                                                                        + TwitchStream.formatStreamFps(
                                                                                                                            f
                                                                                                                        )
                                                                                                                        + " fps"
                                                                                                                    : (
                                                                                                                        p_74297_1_
                                                                                                                                == GameSettings.Options.STREAM_KBPS
                                                                                                                            ? str
                                                                                                                                + TwitchStream.formatStreamKbps(
                                                                                                                                    f
                                                                                                                                )
                                                                                                                                + " Kbps"
                                                                                                                            : (
                                                                                                                                p_74297_1_
                                                                                                                                        == GameSettings.Options.STREAM_BYTES_PER_PIXEL
                                                                                                                                    ? str
                                                                                                                                        + String.format(
                                                                                                                                            "%.3f bpp",
                                                                                                                                            TwitchStream.formatStreamBps(
                                                                                                                                                f
                                                                                                                                            )
                                                                                                                                        )
                                                                                                                                    : (
                                                                                                                                        f == 0.0F
                                                                                                                                            ? str
                                                                                                                                                + I18n.format(
                                                                                                                                                    "options.off"
                                                                                                                                                )
                                                                                                                                            : str
                                                                                                                                                + (int)(
                                                                                                                                                    f
                                                                                                                                                        * 100.0F
                                                                                                                                                )
                                                                                                                                                + "%"
                                                                                                                                    )
                                                                                                                            )
                                                                                                                    )
                                                                                                            )
                                                                                                    )
                                                                                            )
                                                                                    )
                                                                            )
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
            );
    }

    public void loadOptions() {
        FileInputStream fileInputStream = null;

        try {
            if (this.optionsFile.exists()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream = new FileInputStream(this.optionsFile)));
                String str = "";
                this.mapSoundLevels.clear();

                while ((str = reader.readLine()) != null) {
                    try {
                        String[] parts = str.split(":");
                        if (parts[0].equals("mouseSensitivity")) {
                            this.mouseSensitivity = this.parseFloat(parts[1]);
                        }

                        if (parts[0].equals("fov")) {
                            this.fovSetting = this.parseFloat(parts[1]) * 40.0F + 70.0F;
                        }

                        if (parts[0].equals("gamma")) {
                            this.gammaSetting = this.parseFloat(parts[1]);
                        }

                        if (parts[0].equals("saturation")) {
                            this.saturation = this.parseFloat(parts[1]);
                        }

                        if (parts[0].equals("invertYMouse")) {
                            this.invertMouse = parts[1].equals("true");
                        }

                        if (parts[0].equals("renderDistance")) {
                            this.renderDistanceChunks = Integer.parseInt(parts[1]);
                        }

                        if (parts[0].equals("guiScale")) {
                            this.guiScale = Integer.parseInt(parts[1]);
                        }

                        if (parts[0].equals("particles")) {
                            this.particleSetting = Integer.parseInt(parts[1]);
                        }

                        if (parts[0].equals("bobView")) {
                            this.viewBobbing = parts[1].equals("true");
                        }

                        if (parts[0].equals("anaglyph3d")) {
                            this.anaglyph = parts[1].equals("true");
                        }

                        if (parts[0].equals("maxFps")) {
                            this.limitFramerate = Integer.parseInt(parts[1]);
                            if (this.enableVsync) {
                                this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                            }

                            if (this.limitFramerate <= 0) {
                                this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                            }
                        }

                        if (parts[0].equals("fboEnable")) {
                            this.fboEnable = parts[1].equals("true");
                        }

                        if (parts[0].equals("difficulty")) {
                            this.difficulty = EnumDifficulty.getDifficultyEnum(Integer.parseInt(parts[1]));
                        }

                        if (parts[0].equals("fancyGraphics")) {
                            this.fancyGraphics = parts[1].equals("true");
                            this.updateRenderClouds();
                        }

                        if (parts[0].equals("ao")) {
                            if (parts[1].equals("true")) {
                                this.ambientOcclusion = 2;
                            } else if (parts[1].equals("false")) {
                                this.ambientOcclusion = 0;
                            } else {
                                this.ambientOcclusion = Integer.parseInt(parts[1]);
                            }
                        }

                        if (parts[0].equals("renderClouds")) {
                            if (parts[1].equals("true")) {
                                this.clouds = 2;
                            } else if (parts[1].equals("false")) {
                                this.clouds = 0;
                            } else if (parts[1].equals("fast")) {
                                this.clouds = 1;
                            }
                        }

                        if (parts[0].equals("resourcePacks")) {
                            this.resourcePacks = gson.fromJson(str.substring(str.indexOf(58) + 1), typeListString);
                            if (this.resourcePacks == null) {
                                this.resourcePacks = Lists.newArrayList();
                            }
                        }

                        if (parts[0].equals("incompatibleResourcePacks")) {
                            this.field_183018_l = gson.fromJson(str.substring(str.indexOf(58) + 1), typeListString);
                            if (this.field_183018_l == null) {
                                this.field_183018_l = Lists.newArrayList();
                            }
                        }

                        if (parts[0].equals("lastServer") && parts.length >= 2) {
                            this.lastServer = str.substring(str.indexOf(58) + 1);
                        }

                        if (parts[0].equals("lang") && parts.length >= 2) {
                            this.language = parts[1];
                        }

                        if (parts[0].equals("chatVisibility")) {
                            this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility(Integer.parseInt(parts[1]));
                        }

                        if (parts[0].equals("chatColors")) {
                            this.chatColours = parts[1].equals("true");
                        }

                        if (parts[0].equals("chatLinks")) {
                            this.chatLinks = parts[1].equals("true");
                        }

                        if (parts[0].equals("chatLinksPrompt")) {
                            this.chatLinksPrompt = parts[1].equals("true");
                        }

                        if (parts[0].equals("chatOpacity")) {
                            this.chatOpacity = this.parseFloat(parts[1]);
                        }

                        if (parts[0].equals("snooperEnabled")) {
                            this.snooperEnabled = parts[1].equals("true");
                        }

                        if (parts[0].equals("fullscreen")) {
                            this.fullScreen = parts[1].equals("true");
                        }

                        if (parts[0].equals("borderlessFullscreen")) {
                            this.borderlessFullscreen = parts[1].equals("true");
                        }

                        if (parts[0].equals("enableVsync")) {
                            this.enableVsync = parts[1].equals("true");
                            if (this.enableVsync) {
                                this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                            }

                            this.updateVSync();
                        }

                        if (parts[0].equals("useVbo")) {
                            this.useVbo = parts[1].equals("true");
                        }

                        if (parts[0].equals("hideServerAddress")) {
                            this.hideServerAddress = parts[1].equals("true");
                        }

                        if (parts[0].equals("advancedItemTooltips")) {
                            this.advancedItemTooltips = parts[1].equals("true");
                        }

                        if (parts[0].equals("pauseOnLostFocus")) {
                            this.pauseOnLostFocus = parts[1].equals("true");
                        }

                        if (parts[0].equals("touchscreen")) {
                            this.touchscreen = parts[1].equals("true");
                        }

                        if (parts[0].equals("overrideHeight")) {
                            this.overrideHeight = Integer.parseInt(parts[1]);
                        }

                        if (parts[0].equals("overrideWidth")) {
                            this.overrideWidth = Integer.parseInt(parts[1]);
                        }

                        if (parts[0].equals("heldItemTooltips")) {
                            this.heldItemTooltips = parts[1].equals("true");
                        }

                        if (parts[0].equals("chatHeightFocused")) {
                            this.chatHeightFocused = this.parseFloat(parts[1]);
                        }

                        if (parts[0].equals("chatHeightUnfocused")) {
                            this.chatHeightUnfocused = this.parseFloat(parts[1]);
                        }

                        if (parts[0].equals("chatScale")) {
                            this.chatScale = this.parseFloat(parts[1]);
                        }

                        if (parts[0].equals("chatWidth")) {
                            this.chatWidth = this.parseFloat(parts[1]);
                        }

                        if (parts[0].equals("showInventoryAchievementHint")) {
                            this.showInventoryAchievementHint = parts[1].equals("true");
                        }

                        if (parts[0].equals("mipmapLevels")) {
                            this.mipmapLevels = Integer.parseInt(parts[1]);
                        }

                        if (parts[0].equals("streamBytesPerPixel")) {
                            this.streamBytesPerPixel = this.parseFloat(parts[1]);
                        }

                        if (parts[0].equals("streamMicVolume")) {
                            this.streamMicVolume = this.parseFloat(parts[1]);
                        }

                        if (parts[0].equals("streamSystemVolume")) {
                            this.streamGameVolume = this.parseFloat(parts[1]);
                        }

                        if (parts[0].equals("streamKbps")) {
                            this.streamKbps = this.parseFloat(parts[1]);
                        }

                        if (parts[0].equals("streamFps")) {
                            this.streamFps = this.parseFloat(parts[1]);
                        }

                        if (parts[0].equals("streamCompression")) {
                            this.streamCompression = Integer.parseInt(parts[1]);
                        }

                        if (parts[0].equals("streamSendMetadata")) {
                            this.streamSendMetadata = parts[1].equals("true");
                        }

                        if (parts[0].equals("streamPreferredServer") && parts.length >= 2) {
                            this.streamPreferredServer = str.substring(str.indexOf(58) + 1);
                        }

                        if (parts[0].equals("streamChatEnabled")) {
                            this.streamChatEnabled = Integer.parseInt(parts[1]);
                        }

                        if (parts[0].equals("streamChatUserFilter")) {
                            this.streamChatUserFilter = Integer.parseInt(parts[1]);
                        }

                        if (parts[0].equals("streamMicToggleBehavior")) {
                            this.streamMicToggleBehavior = Integer.parseInt(parts[1]);
                        }

                        if (parts[0].equals("forceUnicodeFont")) {
                            this.forceUnicodeFont = parts[1].equals("true");
                        }

                        if (parts[0].equals("allowBlockAlternatives")) {
                            this.allowBlockAlternatives = parts[1].equals("true");
                        }

                        if (parts[0].equals("reducedDebugInfo")) {
                            this.reducedDebugInfo = parts[1].equals("true");
                        }

                        if (parts[0].equals("useNativeTransport")) {
                            this.field_181150_U = parts[1].equals("true");
                        }

                        if (parts[0].equals("entityShadows")) {
                            this.field_181151_V = parts[1].equals("true");
                        }

                        if (parts[0].equals("realmsNotifications")) {
                            this.field_183509_X = parts[1].equals("true");
                        }

                        for (KeyBinding keyBinding : this.keyBindings) {
                            if (parts[0].equals("key_" + keyBinding.getKeyDescription())) {
                                Integer integer = KeyMouseHelper.computeInteger(parts[1]);
                                if (integer != null) {
                                    keyBinding.setKeyCode(integer);
                                }
                            }
                        }

                        for (SoundCategory soundCategory : SoundCategory.values()) {
                            if (parts[0].equals("soundCategory_" + soundCategory.getCategoryName())) {
                                this.mapSoundLevels.put(soundCategory, this.parseFloat(parts[1]));
                            }
                        }

                        for (EnumPlayerModelParts enumPlayerModelParts : EnumPlayerModelParts.values()) {
                            if (parts[0].equals("modelPart_" + enumPlayerModelParts.getPartName())) {
                                this.setModelPartEnabled(enumPlayerModelParts, parts[1].equals("true"));
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("Skipping bad option: " + str);
                        e.printStackTrace();
                    }
                }

                KeyBinding.resetKeyBindingArrayAndHash();
                reader.close();
            }
        } catch (Exception e2) {
            logger.error("Failed to load options", (Throwable)e2);
        } finally {
            IOUtils.closeQuietly((InputStream)fileInputStream);
        }

        this.loadOfOptions();
    }

    private float parseFloat(String p_74305_1_) {
        return p_74305_1_.equals("true") ? 1.0F : (p_74305_1_.equals("false") ? 0.0F : Float.parseFloat(p_74305_1_));
    }

    public void saveOptions() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(this.optionsFile));
            writer.println("invertYMouse:" + this.invertMouse);
            writer.println("mouseSensitivity:" + this.mouseSensitivity);
            writer.println("fov:" + (this.fovSetting - 70.0F) / 40.0F);
            writer.println("gamma:" + this.gammaSetting);
            writer.println("saturation:" + this.saturation);
            writer.println("renderDistance:" + this.renderDistanceChunks);
            writer.println("guiScale:" + this.guiScale);
            writer.println("particles:" + this.particleSetting);
            writer.println("bobView:" + this.viewBobbing);
            writer.println("anaglyph3d:" + this.anaglyph);
            writer.println("maxFps:" + this.limitFramerate);
            writer.println("fboEnable:" + this.fboEnable);
            writer.println("difficulty:" + this.difficulty.getDifficultyId());
            writer.println("fancyGraphics:" + this.fancyGraphics);
            writer.println("ao:" + this.ambientOcclusion);
            switch (this.clouds) {
                case 0:
                    writer.println("renderClouds:false");
                    break;
                case 1:
                    writer.println("renderClouds:fast");
                    break;
                case 2:
                    writer.println("renderClouds:true");
            }

            writer.println("resourcePacks:" + gson.toJson(this.resourcePacks));
            writer.println("incompatibleResourcePacks:" + gson.toJson(this.field_183018_l));
            writer.println("lastServer:" + this.lastServer);
            writer.println("lang:" + this.language);
            writer.println("chatVisibility:" + this.chatVisibility.getChatVisibility());
            writer.println("chatColors:" + this.chatColours);
            writer.println("chatLinks:" + this.chatLinks);
            writer.println("chatLinksPrompt:" + this.chatLinksPrompt);
            writer.println("chatOpacity:" + this.chatOpacity);
            writer.println("snooperEnabled:" + this.snooperEnabled);
            writer.println("fullscreen:" + this.fullScreen);
            writer.println("borderlessFullscreen:" + this.borderlessFullscreen);
            writer.println("enableVsync:" + this.enableVsync);
            writer.println("useVbo:" + this.useVbo);
            writer.println("hideServerAddress:" + this.hideServerAddress);
            writer.println("advancedItemTooltips:" + this.advancedItemTooltips);
            writer.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
            writer.println("touchscreen:" + this.touchscreen);
            writer.println("overrideWidth:" + this.overrideWidth);
            writer.println("overrideHeight:" + this.overrideHeight);
            writer.println("heldItemTooltips:" + this.heldItemTooltips);
            writer.println("chatHeightFocused:" + this.chatHeightFocused);
            writer.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
            writer.println("chatScale:" + this.chatScale);
            writer.println("chatWidth:" + this.chatWidth);
            writer.println("showInventoryAchievementHint:" + this.showInventoryAchievementHint);
            writer.println("mipmapLevels:" + this.mipmapLevels);
            writer.println("streamBytesPerPixel:" + this.streamBytesPerPixel);
            writer.println("streamMicVolume:" + this.streamMicVolume);
            writer.println("streamSystemVolume:" + this.streamGameVolume);
            writer.println("streamKbps:" + this.streamKbps);
            writer.println("streamFps:" + this.streamFps);
            writer.println("streamCompression:" + this.streamCompression);
            writer.println("streamSendMetadata:" + this.streamSendMetadata);
            writer.println("streamPreferredServer:" + this.streamPreferredServer);
            writer.println("streamChatEnabled:" + this.streamChatEnabled);
            writer.println("streamChatUserFilter:" + this.streamChatUserFilter);
            writer.println("streamMicToggleBehavior:" + this.streamMicToggleBehavior);
            writer.println("forceUnicodeFont:" + this.forceUnicodeFont);
            writer.println("allowBlockAlternatives:" + this.allowBlockAlternatives);
            writer.println("reducedDebugInfo:" + this.reducedDebugInfo);
            writer.println("useNativeTransport:" + this.field_181150_U);
            writer.println("entityShadows:" + this.field_181151_V);
            writer.println("realmsNotifications:" + this.field_183509_X);

            for (KeyBinding keyBinding : this.keyBindings) {
                writer.println("key_" + keyBinding.getKeyDescription() + ":" + keyBinding.getKeyCode());
            }

            for (SoundCategory soundCategory : SoundCategory.values()) {
                writer.println("soundCategory_" + soundCategory.getCategoryName() + ":" + this.getSoundLevel(soundCategory));
            }

            for (EnumPlayerModelParts enumPlayerModelParts : EnumPlayerModelParts.values()) {
                writer.println("modelPart_" + enumPlayerModelParts.getPartName() + ":" + this.setModelParts.contains(enumPlayerModelParts));
            }

            writer.flush();
            writer.close();
        } catch (Exception e) {
            logger.error("Failed to save options", (Throwable)e);
        }

        this.saveOfOptions();
        this.sendSettingsToServer();
    }

    public float getSoundLevel(SoundCategory p_151438_1_) {
        return this.mapSoundLevels.containsKey(p_151438_1_) ? this.mapSoundLevels.get(p_151438_1_) : 1.0F;
    }

    public void setSoundLevel(SoundCategory p_151439_1_, float p_151439_2_) {
        this.mc.getSoundHandler().setSoundLevel(p_151439_1_, p_151439_2_);
        this.mapSoundLevels.put(p_151439_1_, p_151439_2_);
    }

    public void sendSettingsToServer() {
        if (this.mc.thePlayer != null) {
            int n = 0;

            for (EnumPlayerModelParts setModelPart : this.setModelParts) {
                n |= setModelPart.getPartMask();
            }

            this.mc
                .thePlayer
                .sendQueue
                .addToSendQueue(new C15PacketClientSettings(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColours, n));
        }
    }

    public Set<EnumPlayerModelParts> getModelParts() {
        return ImmutableSet.copyOf(this.setModelParts);
    }

    public void setModelPartEnabled(EnumPlayerModelParts p_178878_1_, boolean p_178878_2_) {
        if (p_178878_2_) {
            this.setModelParts.add(p_178878_1_);
        } else {
            this.setModelParts.remove(p_178878_1_);
        }

        this.sendSettingsToServer();
    }

    public void switchModelPartEnabled(EnumPlayerModelParts p_178877_1_) {
        if (!this.getModelParts().contains(p_178877_1_)) {
            this.setModelParts.add(p_178877_1_);
        } else {
            this.setModelParts.remove(p_178877_1_);
        }

        this.sendSettingsToServer();
    }

    public int func_181147_e() {
        return this.renderDistanceChunks >= 4 ? this.clouds : 0;
    }

    public boolean func_181148_f() {
        return this.field_181150_U;
    }

    private void setOptionFloatValueOF(GameSettings.Options p_setOptionFloatValueOF_1_, float p_setOptionFloatValueOF_2_) {
        if (p_setOptionFloatValueOF_1_ == GameSettings.Options.CLOUD_HEIGHT) {
            this.ofCloudsHeight = p_setOptionFloatValueOF_2_;
            this.mc.renderGlobal.resetClouds();
        }

        if (p_setOptionFloatValueOF_1_ == GameSettings.Options.AO_LEVEL) {
            this.ofAoLevel = p_setOptionFloatValueOF_2_;
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionFloatValueOF_1_ == GameSettings.Options.AA_LEVEL) {
            int n = (int)p_setOptionFloatValueOF_2_;
            if (n > 0 && Config.isShaders()) {
                Config.showGuiMessage(Lang.get("of.message.aa.shaders1"), Lang.get("of.message.aa.shaders2"));
                return;
            }

            int[] ints = new int[]{0, 2, 4, 6, 8, 12, 16};
            this.ofAaLevel = 0;

            for (int i = 0; i < ints.length; i++) {
                if (n >= ints[i]) {
                    this.ofAaLevel = ints[i];
                }
            }

            this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
        }

        if (p_setOptionFloatValueOF_1_ == GameSettings.Options.AF_LEVEL) {
            int n2 = (int)p_setOptionFloatValueOF_2_;
            if (n2 > 1 && Config.isShaders()) {
                Config.showGuiMessage(Lang.get("of.message.af.shaders1"), Lang.get("of.message.af.shaders2"));
                return;
            }

            this.ofAfLevel = 1;

            while (this.ofAfLevel * 2 <= n2) {
                this.ofAfLevel *= 2;
            }

            this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
            this.mc.refreshResources();
        }

        if (p_setOptionFloatValueOF_1_ == GameSettings.Options.MIPMAP_TYPE) {
            int n3 = (int)p_setOptionFloatValueOF_2_;
            this.ofMipmapType = Config.limit(n3, 0, 3);
            this.mc.refreshResources();
        }

        if (p_setOptionFloatValueOF_1_ == GameSettings.Options.FULLSCREEN_MODE) {
            int n4 = (int)p_setOptionFloatValueOF_2_ - 1;
            String[] displayModeNames = Config.getDisplayModeNames();
            if (n4 < 0 || n4 >= displayModeNames.length) {
                this.ofFullscreenMode = "Default";
                return;
            }

            this.ofFullscreenMode = displayModeNames[n4];
        }
    }

    private float getOptionFloatValueOF(GameSettings.Options p_getOptionFloatValueOF_1_) {
        if (p_getOptionFloatValueOF_1_ == GameSettings.Options.CLOUD_HEIGHT) {
            return this.ofCloudsHeight;
        }
        if (p_getOptionFloatValueOF_1_ == GameSettings.Options.AO_LEVEL) {
            return this.ofAoLevel;
        }
        if (p_getOptionFloatValueOF_1_ == GameSettings.Options.AA_LEVEL) {
            return this.ofAaLevel;
        }
        if (p_getOptionFloatValueOF_1_ == GameSettings.Options.AF_LEVEL) {
            return this.ofAfLevel;
        }
        if (p_getOptionFloatValueOF_1_ == GameSettings.Options.MIPMAP_TYPE) {
            return this.ofMipmapType;
        }
        if (p_getOptionFloatValueOF_1_ != GameSettings.Options.FRAMERATE_LIMIT) {
            if (p_getOptionFloatValueOF_1_ == GameSettings.Options.FULLSCREEN_MODE) {
                if (this.ofFullscreenMode.equals("Default")) {
                    return 0.0F;
                }
                List list = Arrays.asList(Config.getDisplayModeNames());
                int n = list.indexOf(this.ofFullscreenMode);
                return n < 0 ? 0.0F : n + 1;
            }
            return Float.MAX_VALUE;
        }
        return this.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() && this.enableVsync ? 0.0F : this.limitFramerate;
    }

    private void setOptionValueOF(GameSettings.Options p_setOptionValueOF_1_, int p_setOptionValueOF_2_) {
        if (p_setOptionValueOF_1_ == GameSettings.Options.FOG_FANCY) {
            switch (this.ofFogType) {
                case 1:
                    this.ofFogType = 2;
                    if (!Config.isFancyFogAvailable()) {
                        this.ofFogType = 3;
                    }
                    break;
                case 2:
                    this.ofFogType = 3;
                    break;
                case 3:
                    this.ofFogType = 1;
                    break;
                default:
                    this.ofFogType = 1;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.FOG_START) {
            this.ofFogStart += 0.2F;
            if (this.ofFogStart > 0.81F) {
                this.ofFogStart = 0.2F;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SMOOTH_FPS) {
            this.ofSmoothFps = !this.ofSmoothFps;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SMOOTH_WORLD) {
            this.ofSmoothWorld = !this.ofSmoothWorld;
            Config.updateThreadPriorities();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CLOUDS) {
            this.ofClouds++;
            if (this.ofClouds > 3) {
                this.ofClouds = 0;
            }

            this.updateRenderClouds();
            this.mc.renderGlobal.resetClouds();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.TREES) {
            this.ofTrees = nextValue(this.ofTrees, OF_TREES_VALUES);
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.DROPPED_ITEMS) {
            this.ofDroppedItems++;
            if (this.ofDroppedItems > 2) {
                this.ofDroppedItems = 0;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.RAIN) {
            this.ofRain++;
            if (this.ofRain > 3) {
                this.ofRain = 0;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_WATER) {
            this.ofAnimatedWater++;
            if (this.ofAnimatedWater == 1) {
                this.ofAnimatedWater++;
            }

            if (this.ofAnimatedWater > 2) {
                this.ofAnimatedWater = 0;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_LAVA) {
            this.ofAnimatedLava++;
            if (this.ofAnimatedLava == 1) {
                this.ofAnimatedLava++;
            }

            if (this.ofAnimatedLava > 2) {
                this.ofAnimatedLava = 0;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_FIRE) {
            this.ofAnimatedFire = !this.ofAnimatedFire;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_PORTAL) {
            this.ofAnimatedPortal = !this.ofAnimatedPortal;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_REDSTONE) {
            this.ofAnimatedRedstone = !this.ofAnimatedRedstone;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_EXPLOSION) {
            this.ofAnimatedExplosion = !this.ofAnimatedExplosion;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_FLAME) {
            this.ofAnimatedFlame = !this.ofAnimatedFlame;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_SMOKE) {
            this.ofAnimatedSmoke = !this.ofAnimatedSmoke;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.VOID_PARTICLES) {
            this.ofVoidParticles = !this.ofVoidParticles;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.WATER_PARTICLES) {
            this.ofWaterParticles = !this.ofWaterParticles;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.PORTAL_PARTICLES) {
            this.ofPortalParticles = !this.ofPortalParticles;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.POTION_PARTICLES) {
            this.ofPotionParticles = !this.ofPotionParticles;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.FIREWORK_PARTICLES) {
            this.ofFireworkParticles = !this.ofFireworkParticles;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.DRIPPING_WATER_LAVA) {
            this.ofDrippingWaterLava = !this.ofDrippingWaterLava;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_TERRAIN) {
            this.ofAnimatedTerrain = !this.ofAnimatedTerrain;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ANIMATED_TEXTURES) {
            this.ofAnimatedTextures = !this.ofAnimatedTextures;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.RAIN_SPLASH) {
            this.ofRainSplash = !this.ofRainSplash;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.LAGOMETER) {
            this.ofLagometer = !this.ofLagometer;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SHOW_FPS) {
            this.ofShowFps = !this.ofShowFps;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.AUTOSAVE_TICKS) {
            short s = 900;
            this.ofAutoSaveTicks = Math.max(this.ofAutoSaveTicks / s * s, s);
            this.ofAutoSaveTicks *= 2;
            if (this.ofAutoSaveTicks > 32 * s) {
                this.ofAutoSaveTicks = s;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.BETTER_GRASS) {
            this.ofBetterGrass++;
            if (this.ofBetterGrass > 3) {
                this.ofBetterGrass = 1;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CONNECTED_TEXTURES) {
            this.ofConnectedTextures++;
            if (this.ofConnectedTextures > 3) {
                this.ofConnectedTextures = 1;
            }

            if (this.ofConnectedTextures == 2) {
                this.mc.renderGlobal.loadRenderers();
            } else {
                this.mc.refreshResources();
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.WEATHER) {
            this.ofWeather = !this.ofWeather;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SKY) {
            this.ofSky = !this.ofSky;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.STARS) {
            this.ofStars = !this.ofStars;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SUN_MOON) {
            this.ofSunMoon = !this.ofSunMoon;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.VIGNETTE) {
            this.ofVignette++;
            if (this.ofVignette > 2) {
                this.ofVignette = 0;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CHUNK_UPDATES) {
            this.ofChunkUpdates++;
            if (this.ofChunkUpdates > 5) {
                this.ofChunkUpdates = 1;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CHUNK_UPDATES_DYNAMIC) {
            this.ofChunkUpdatesDynamic = !this.ofChunkUpdatesDynamic;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.TIME) {
            this.ofTime++;
            if (this.ofTime > 2) {
                this.ofTime = 0;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CLEAR_WATER) {
            this.ofClearWater = !this.ofClearWater;
            this.updateWaterOpacity();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.PROFILER) {
            this.ofProfiler = !this.ofProfiler;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.BETTER_SNOW) {
            this.ofBetterSnow = !this.ofBetterSnow;
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SWAMP_COLORS) {
            this.ofSwampColors = !this.ofSwampColors;
            CustomColors.updateUseDefaultGrassFoliageColors();
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.RANDOM_ENTITIES) {
            this.ofRandomEntities = !this.ofRandomEntities;
            RandomEntities.update();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SMOOTH_BIOMES) {
            this.ofSmoothBiomes = !this.ofSmoothBiomes;
            CustomColors.updateUseDefaultGrassFoliageColors();
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CUSTOM_FONTS) {
            this.ofCustomFonts = !this.ofCustomFonts;
            this.mc.fontRendererObj.onResourceManagerReload(Config.getResourceManager());
            this.mc.standardGalacticFontRenderer.onResourceManagerReload(Config.getResourceManager());
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CUSTOM_COLORS) {
            this.ofCustomColors = !this.ofCustomColors;
            CustomColors.update();
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CUSTOM_ITEMS) {
            this.ofCustomItems = !this.ofCustomItems;
            this.mc.refreshResources();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CUSTOM_SKY) {
            this.ofCustomSky = !this.ofCustomSky;
            CustomSky.update();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SHOW_CAPES) {
            this.ofShowCapes = !this.ofShowCapes;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.NATURAL_TEXTURES) {
            this.ofNaturalTextures = !this.ofNaturalTextures;
            NaturalTextures.update();
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.EMISSIVE_TEXTURES) {
            this.ofEmissiveTextures = !this.ofEmissiveTextures;
            this.mc.refreshResources();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.FAST_MATH) {
            this.ofFastMath = !this.ofFastMath;
            MathHelper.fastMath = this.ofFastMath;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.FAST_RENDER) {
            if (!this.ofFastRender && Config.isShaders()) {
                Config.showGuiMessage(Lang.get("of.message.fr.shaders1"), Lang.get("of.message.fr.shaders2"));
                return;
            }

            this.ofFastRender = !this.ofFastRender;
            if (this.ofFastRender) {
                this.mc.entityRenderer.func_181022_b();
            }

            Config.updateFramebufferSize();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.TRANSLUCENT_BLOCKS) {
            if (this.ofTranslucentBlocks == 0) {
                this.ofTranslucentBlocks = 1;
            } else if (this.ofTranslucentBlocks == 1) {
                this.ofTranslucentBlocks = 2;
            } else if (this.ofTranslucentBlocks == 2) {
                this.ofTranslucentBlocks = 0;
            } else {
                this.ofTranslucentBlocks = 0;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.LAZY_CHUNK_LOADING) {
            this.ofLazyChunkLoading = !this.ofLazyChunkLoading;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.RENDER_REGIONS) {
            this.ofRenderRegions = !this.ofRenderRegions;
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SMART_ANIMATIONS) {
            this.ofSmartAnimations = !this.ofSmartAnimations;
            this.mc.renderGlobal.loadRenderers();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.DYNAMIC_FOV) {
            this.ofDynamicFov = !this.ofDynamicFov;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ALTERNATE_BLOCKS) {
            this.ofAlternateBlocks = !this.ofAlternateBlocks;
            this.mc.refreshResources();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.DYNAMIC_LIGHTS) {
            this.ofDynamicLights = nextValue(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
            DynamicLights.removeLights(this.mc.renderGlobal);
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SCREENSHOT_SIZE) {
            this.ofScreenshotSize++;
            if (this.ofScreenshotSize > 4) {
                this.ofScreenshotSize = 1;
            }

            if (!OpenGlHelper.isFramebufferEnabled()) {
                this.ofScreenshotSize = 1;
            }
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CUSTOM_ENTITY_MODELS) {
            this.ofCustomEntityModels = !this.ofCustomEntityModels;
            this.mc.refreshResources();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.CUSTOM_GUIS) {
            this.ofCustomGuis = !this.ofCustomGuis;
            CustomGuis.update();
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.SHOW_GL_ERRORS) {
            this.ofShowGlErrors = !this.ofShowGlErrors;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.HELD_ITEM_TOOLTIPS) {
            this.heldItemTooltips = !this.heldItemTooltips;
        }

        if (p_setOptionValueOF_1_ == GameSettings.Options.ADVANCED_TOOLTIPS) {
            this.advancedItemTooltips = !this.advancedItemTooltips;
        }
    }

    private String getKeyBindingOF(GameSettings.Options p_getKeyBindingOF_1_) {
        String str = I18n.format(p_getKeyBindingOF_1_.getEnumString()) + ": ";
        if (str == null) {
            str = p_getKeyBindingOF_1_.getEnumString();
        }

        if (p_getKeyBindingOF_1_ == GameSettings.Options.RENDER_DISTANCE) {
            int optionFloatValue = (int)this.getOptionFloatValue(p_getKeyBindingOF_1_);
            String str2 = I18n.format("options.renderDistance.tiny");
            int n = 2;
            if (optionFloatValue >= 4) {
                str2 = I18n.format("options.renderDistance.short");
                n = 4;
            }

            if (optionFloatValue >= 8) {
                str2 = I18n.format("options.renderDistance.normal");
                n = 8;
            }

            if (optionFloatValue >= 16) {
                str2 = I18n.format("options.renderDistance.far");
                n = 16;
            }

            if (optionFloatValue >= 32) {
                str2 = Lang.get("of.options.renderDistance.extreme");
                n = 32;
            }

            if (optionFloatValue >= 48) {
                str2 = Lang.get("of.options.renderDistance.insane");
                n = 48;
            }

            if (optionFloatValue >= 64) {
                str2 = Lang.get("of.options.renderDistance.ludicrous");
                n = 64;
            }

            int n2 = this.renderDistanceChunks - n;
            String str3 = str2;
            if (n2 > 0) {
                str3 = str2 + "+";
            }

            return str + optionFloatValue + " " + str3;
        }
        if (p_getKeyBindingOF_1_ == GameSettings.Options.FOG_FANCY) {
            switch (this.ofFogType) {
                case 1:
                    return str + Lang.getFast();
                case 2:
                    return str + Lang.getFancy();
                case 3:
                    return str + Lang.getOff();
                default:
                    return str + Lang.getOff();
            }
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.FOG_START) {
            return str + this.ofFogStart;
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.MIPMAP_TYPE) {
            switch (this.ofMipmapType) {
                case 0:
                    return str + Lang.get("of.options.mipmap.nearest");
                case 1:
                    return str + Lang.get("of.options.mipmap.linear");
                case 2:
                    return str + Lang.get("of.options.mipmap.bilinear");
                case 3:
                    return str + Lang.get("of.options.mipmap.trilinear");
                default:
                    return str + "of.options.mipmap.nearest";
            }
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.SMOOTH_FPS) {
            return this.ofSmoothFps ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.SMOOTH_WORLD) {
            return this.ofSmoothWorld ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.CLOUDS) {
            switch (this.ofClouds) {
                case 1:
                    return str + Lang.getFast();
                case 2:
                    return str + Lang.getFancy();
                case 3:
                    return str + Lang.getOff();
                default:
                    return str + Lang.getDefault();
            }
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.TREES) {
            switch (this.ofTrees) {
                case 1:
                    return str + Lang.getFast();
                case 2:
                    return str + Lang.getFancy();
                case 3:
                default:
                    return str + Lang.getDefault();
                case 4:
                    return str + Lang.get("of.general.smart");
            }
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.DROPPED_ITEMS) {
            switch (this.ofDroppedItems) {
                case 1:
                    return str + Lang.getFast();
                case 2:
                    return str + Lang.getFancy();
                default:
                    return str + Lang.getDefault();
            }
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.RAIN) {
            switch (this.ofRain) {
                case 1:
                    return str + Lang.getFast();
                case 2:
                    return str + Lang.getFancy();
                case 3:
                    return str + Lang.getOff();
                default:
                    return str + Lang.getDefault();
            }
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_WATER) {
            switch (this.ofAnimatedWater) {
                case 1:
                    return str + Lang.get("of.options.animation.dynamic");
                case 2:
                    return str + Lang.getOff();
                default:
                    return str + Lang.getOn();
            }
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_LAVA) {
            switch (this.ofAnimatedLava) {
                case 1:
                    return str + Lang.get("of.options.animation.dynamic");
                case 2:
                    return str + Lang.getOff();
                default:
                    return str + Lang.getOn();
            }
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_FIRE) {
            return this.ofAnimatedFire ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_PORTAL) {
            return this.ofAnimatedPortal ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_REDSTONE) {
            return this.ofAnimatedRedstone ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_EXPLOSION) {
            return this.ofAnimatedExplosion ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_FLAME) {
            return this.ofAnimatedFlame ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_SMOKE) {
            return this.ofAnimatedSmoke ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.VOID_PARTICLES) {
            return this.ofVoidParticles ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.WATER_PARTICLES) {
            return this.ofWaterParticles ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.PORTAL_PARTICLES) {
            return this.ofPortalParticles ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.POTION_PARTICLES) {
            return this.ofPotionParticles ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.FIREWORK_PARTICLES) {
            return this.ofFireworkParticles ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.DRIPPING_WATER_LAVA) {
            return this.ofDrippingWaterLava ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_TERRAIN) {
            return this.ofAnimatedTerrain ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.ANIMATED_TEXTURES) {
            return this.ofAnimatedTextures ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.RAIN_SPLASH) {
            return this.ofRainSplash ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.LAGOMETER) {
            return this.ofLagometer ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.SHOW_FPS) {
            return this.ofShowFps ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.AUTOSAVE_TICKS) {
            short s = 900;
            return this.ofAutoSaveTicks <= s
                ? str + Lang.get("of.options.save.45s")
                : (
                    this.ofAutoSaveTicks <= 2 * s
                        ? str + Lang.get("of.options.save.90s")
                        : (
                            this.ofAutoSaveTicks <= 4 * s
                                ? str + Lang.get("of.options.save.3min")
                                : (
                                    this.ofAutoSaveTicks <= 8 * s
                                        ? str + Lang.get("of.options.save.6min")
                                        : (
                                            this.ofAutoSaveTicks <= 16 * s
                                                ? str + Lang.get("of.options.save.12min")
                                                : str + Lang.get("of.options.save.24min")
                                        )
                                )
                        )
                );
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.BETTER_GRASS) {
            switch (this.ofBetterGrass) {
                case 1:
                    return str + Lang.getFast();
                case 2:
                    return str + Lang.getFancy();
                default:
                    return str + Lang.getOff();
            }
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.CONNECTED_TEXTURES) {
            switch (this.ofConnectedTextures) {
                case 1:
                    return str + Lang.getFast();
                case 2:
                    return str + Lang.getFancy();
                default:
                    return str + Lang.getOff();
            }
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.WEATHER) {
            return this.ofWeather ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.SKY) {
            return this.ofSky ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.STARS) {
            return this.ofStars ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.SUN_MOON) {
            return this.ofSunMoon ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.VIGNETTE) {
            switch (this.ofVignette) {
                case 1:
                    return str + Lang.getFast();
                case 2:
                    return str + Lang.getFancy();
                default:
                    return str + Lang.getDefault();
            }
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.CHUNK_UPDATES) {
            return str + this.ofChunkUpdates;
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.CHUNK_UPDATES_DYNAMIC) {
            return this.ofChunkUpdatesDynamic ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.TIME) {
            return this.ofTime == 1
                ? str + Lang.get("of.options.time.dayOnly")
                : (this.ofTime == 2 ? str + Lang.get("of.options.time.nightOnly") : str + Lang.getDefault());
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.CLEAR_WATER) {
            return this.ofClearWater ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.AA_LEVEL) {
            String str4 = "";
            if (this.ofAaLevel != Config.getAntialiasingLevel()) {
                str4 = " (" + Lang.get("of.general.restart") + ")";
            }

            return this.ofAaLevel == 0 ? str + Lang.getOff() + str4 : str + this.ofAaLevel + str4;
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.AF_LEVEL) {
            return this.ofAfLevel == 1 ? str + Lang.getOff() : str + this.ofAfLevel;
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.PROFILER) {
            return this.ofProfiler ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.BETTER_SNOW) {
            return this.ofBetterSnow ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.SWAMP_COLORS) {
            return this.ofSwampColors ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.RANDOM_ENTITIES) {
            return this.ofRandomEntities ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.SMOOTH_BIOMES) {
            return this.ofSmoothBiomes ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.CUSTOM_FONTS) {
            return this.ofCustomFonts ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.CUSTOM_COLORS) {
            return this.ofCustomColors ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.CUSTOM_SKY) {
            return this.ofCustomSky ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.SHOW_CAPES) {
            return this.ofShowCapes ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.CUSTOM_ITEMS) {
            return this.ofCustomItems ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.NATURAL_TEXTURES) {
            return this.ofNaturalTextures ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.EMISSIVE_TEXTURES) {
            return this.ofEmissiveTextures ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.FAST_MATH) {
            return this.ofFastMath ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.FAST_RENDER) {
            return this.ofFastRender ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.TRANSLUCENT_BLOCKS) {
            return this.ofTranslucentBlocks == 1 ? str + Lang.getFast() : (this.ofTranslucentBlocks == 2 ? str + Lang.getFancy() : str + Lang.getDefault());
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.LAZY_CHUNK_LOADING) {
            return this.ofLazyChunkLoading ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.RENDER_REGIONS) {
            return this.ofRenderRegions ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.SMART_ANIMATIONS) {
            return this.ofSmartAnimations ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.DYNAMIC_FOV) {
            return this.ofDynamicFov ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.ALTERNATE_BLOCKS) {
            return this.ofAlternateBlocks ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.DYNAMIC_LIGHTS) {
            int n3 = indexOf(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
            return str + getTranslation(KEYS_DYNAMIC_LIGHTS, n3);
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.SCREENSHOT_SIZE) {
            return this.ofScreenshotSize <= 1 ? str + Lang.getDefault() : str + this.ofScreenshotSize + "x";
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.CUSTOM_ENTITY_MODELS) {
            return this.ofCustomEntityModels ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.CUSTOM_GUIS) {
            return this.ofCustomGuis ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.SHOW_GL_ERRORS) {
            return this.ofShowGlErrors ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.FULLSCREEN_MODE) {
            return this.ofFullscreenMode.equals("Default") ? str + Lang.getDefault() : str + this.ofFullscreenMode;
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.HELD_ITEM_TOOLTIPS) {
            return this.heldItemTooltips ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.ADVANCED_TOOLTIPS) {
            return this.advancedItemTooltips ? str + Lang.getOn() : str + Lang.getOff();
        } else if (p_getKeyBindingOF_1_ == GameSettings.Options.FRAMERATE_LIMIT) {
            float optionFloatValue2 = this.getOptionFloatValue(p_getKeyBindingOF_1_);
            return optionFloatValue2 == 0.0F
                ? str + Lang.get("of.options.framerateLimit.vsync")
                : (optionFloatValue2 == p_getKeyBindingOF_1_.valueMax ? str + I18n.format("options.framerateLimit.max") : str + (int)optionFloatValue2 + " fps");
        } else {
            return null;
        }
    }

    public void loadOfOptions() {
        try {
            File optionsFileOF = this.optionsFileOF;
            if (!optionsFileOF.exists()) {
                optionsFileOF = this.optionsFile;
            }

            if (!optionsFileOF.exists()) {
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(optionsFileOF), "UTF-8"));
            String str = "";

            while ((str = reader.readLine()) != null) {
                try {
                    String[] parts = str.split(":");
                    if (parts[0].equals("ofRenderDistanceChunks") && parts.length >= 2) {
                        this.renderDistanceChunks = Integer.valueOf(parts[1]);
                        this.renderDistanceChunks = Config.limit(this.renderDistanceChunks, 2, 1024);
                    }

                    if (parts[0].equals("ofFogType") && parts.length >= 2) {
                        this.ofFogType = Integer.valueOf(parts[1]);
                        this.ofFogType = Config.limit(this.ofFogType, 1, 3);
                    }

                    if (parts[0].equals("ofFogStart") && parts.length >= 2) {
                        this.ofFogStart = Float.valueOf(parts[1]);
                        if (this.ofFogStart < 0.2F) {
                            this.ofFogStart = 0.2F;
                        }

                        if (this.ofFogStart > 0.81F) {
                            this.ofFogStart = 0.8F;
                        }
                    }

                    if (parts[0].equals("ofMipmapType") && parts.length >= 2) {
                        this.ofMipmapType = Integer.valueOf(parts[1]);
                        this.ofMipmapType = Config.limit(this.ofMipmapType, 0, 3);
                    }

                    if (parts[0].equals("ofOcclusionFancy") && parts.length >= 2) {
                        this.ofOcclusionFancy = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofSmoothFps") && parts.length >= 2) {
                        this.ofSmoothFps = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofSmoothWorld") && parts.length >= 2) {
                        this.ofSmoothWorld = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofAoLevel") && parts.length >= 2) {
                        this.ofAoLevel = Float.valueOf(parts[1]);
                        this.ofAoLevel = Config.limit(this.ofAoLevel, 0.0F, 1.0F);
                    }

                    if (parts[0].equals("ofClouds") && parts.length >= 2) {
                        this.ofClouds = Integer.valueOf(parts[1]);
                        this.ofClouds = Config.limit(this.ofClouds, 0, 3);
                        this.updateRenderClouds();
                    }

                    if (parts[0].equals("ofCloudsHeight") && parts.length >= 2) {
                        this.ofCloudsHeight = Float.valueOf(parts[1]);
                        this.ofCloudsHeight = Config.limit(this.ofCloudsHeight, 0.0F, 1.0F);
                    }

                    if (parts[0].equals("ofTrees") && parts.length >= 2) {
                        this.ofTrees = Integer.valueOf(parts[1]);
                        this.ofTrees = limit(this.ofTrees, OF_TREES_VALUES);
                    }

                    if (parts[0].equals("ofDroppedItems") && parts.length >= 2) {
                        this.ofDroppedItems = Integer.valueOf(parts[1]);
                        this.ofDroppedItems = Config.limit(this.ofDroppedItems, 0, 2);
                    }

                    if (parts[0].equals("ofRain") && parts.length >= 2) {
                        this.ofRain = Integer.valueOf(parts[1]);
                        this.ofRain = Config.limit(this.ofRain, 0, 3);
                    }

                    if (parts[0].equals("ofAnimatedWater") && parts.length >= 2) {
                        this.ofAnimatedWater = Integer.valueOf(parts[1]);
                        this.ofAnimatedWater = Config.limit(this.ofAnimatedWater, 0, 2);
                    }

                    if (parts[0].equals("ofAnimatedLava") && parts.length >= 2) {
                        this.ofAnimatedLava = Integer.valueOf(parts[1]);
                        this.ofAnimatedLava = Config.limit(this.ofAnimatedLava, 0, 2);
                    }

                    if (parts[0].equals("ofAnimatedFire") && parts.length >= 2) {
                        this.ofAnimatedFire = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofAnimatedPortal") && parts.length >= 2) {
                        this.ofAnimatedPortal = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofAnimatedRedstone") && parts.length >= 2) {
                        this.ofAnimatedRedstone = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofAnimatedExplosion") && parts.length >= 2) {
                        this.ofAnimatedExplosion = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofAnimatedFlame") && parts.length >= 2) {
                        this.ofAnimatedFlame = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofAnimatedSmoke") && parts.length >= 2) {
                        this.ofAnimatedSmoke = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofVoidParticles") && parts.length >= 2) {
                        this.ofVoidParticles = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofWaterParticles") && parts.length >= 2) {
                        this.ofWaterParticles = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofPortalParticles") && parts.length >= 2) {
                        this.ofPortalParticles = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofPotionParticles") && parts.length >= 2) {
                        this.ofPotionParticles = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofFireworkParticles") && parts.length >= 2) {
                        this.ofFireworkParticles = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofDrippingWaterLava") && parts.length >= 2) {
                        this.ofDrippingWaterLava = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofAnimatedTerrain") && parts.length >= 2) {
                        this.ofAnimatedTerrain = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofAnimatedTextures") && parts.length >= 2) {
                        this.ofAnimatedTextures = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofRainSplash") && parts.length >= 2) {
                        this.ofRainSplash = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofLagometer") && parts.length >= 2) {
                        this.ofLagometer = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofShowFps") && parts.length >= 2) {
                        this.ofShowFps = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofAutoSaveTicks") && parts.length >= 2) {
                        this.ofAutoSaveTicks = Integer.valueOf(parts[1]);
                        this.ofAutoSaveTicks = Config.limit(this.ofAutoSaveTicks, 40, 40000);
                    }

                    if (parts[0].equals("ofBetterGrass") && parts.length >= 2) {
                        this.ofBetterGrass = Integer.valueOf(parts[1]);
                        this.ofBetterGrass = Config.limit(this.ofBetterGrass, 1, 3);
                    }

                    if (parts[0].equals("ofConnectedTextures") && parts.length >= 2) {
                        this.ofConnectedTextures = Integer.valueOf(parts[1]);
                        this.ofConnectedTextures = Config.limit(this.ofConnectedTextures, 1, 3);
                    }

                    if (parts[0].equals("ofWeather") && parts.length >= 2) {
                        this.ofWeather = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofSky") && parts.length >= 2) {
                        this.ofSky = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofStars") && parts.length >= 2) {
                        this.ofStars = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofSunMoon") && parts.length >= 2) {
                        this.ofSunMoon = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofVignette") && parts.length >= 2) {
                        this.ofVignette = Integer.valueOf(parts[1]);
                        this.ofVignette = Config.limit(this.ofVignette, 0, 2);
                    }

                    if (parts[0].equals("ofChunkUpdates") && parts.length >= 2) {
                        this.ofChunkUpdates = Integer.valueOf(parts[1]);
                        this.ofChunkUpdates = Config.limit(this.ofChunkUpdates, 1, 5);
                    }

                    if (parts[0].equals("ofChunkUpdatesDynamic") && parts.length >= 2) {
                        this.ofChunkUpdatesDynamic = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofTime") && parts.length >= 2) {
                        this.ofTime = Integer.valueOf(parts[1]);
                        this.ofTime = Config.limit(this.ofTime, 0, 2);
                    }

                    if (parts[0].equals("ofClearWater") && parts.length >= 2) {
                        this.ofClearWater = Boolean.valueOf(parts[1]);
                        this.updateWaterOpacity();
                    }

                    if (parts[0].equals("ofAaLevel") && parts.length >= 2) {
                        this.ofAaLevel = Integer.valueOf(parts[1]);
                        this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
                    }

                    if (parts[0].equals("ofAfLevel") && parts.length >= 2) {
                        this.ofAfLevel = Integer.valueOf(parts[1]);
                        this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
                    }

                    if (parts[0].equals("ofProfiler") && parts.length >= 2) {
                        this.ofProfiler = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofBetterSnow") && parts.length >= 2) {
                        this.ofBetterSnow = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofSwampColors") && parts.length >= 2) {
                        this.ofSwampColors = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofRandomEntities") && parts.length >= 2) {
                        this.ofRandomEntities = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofSmoothBiomes") && parts.length >= 2) {
                        this.ofSmoothBiomes = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofCustomFonts") && parts.length >= 2) {
                        this.ofCustomFonts = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofCustomColors") && parts.length >= 2) {
                        this.ofCustomColors = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofCustomItems") && parts.length >= 2) {
                        this.ofCustomItems = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofCustomSky") && parts.length >= 2) {
                        this.ofCustomSky = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofShowCapes") && parts.length >= 2) {
                        this.ofShowCapes = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofNaturalTextures") && parts.length >= 2) {
                        this.ofNaturalTextures = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofEmissiveTextures") && parts.length >= 2) {
                        this.ofEmissiveTextures = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofLazyChunkLoading") && parts.length >= 2) {
                        this.ofLazyChunkLoading = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofRenderRegions") && parts.length >= 2) {
                        this.ofRenderRegions = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofSmartAnimations") && parts.length >= 2) {
                        this.ofSmartAnimations = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofDynamicFov") && parts.length >= 2) {
                        this.ofDynamicFov = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofAlternateBlocks") && parts.length >= 2) {
                        this.ofAlternateBlocks = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofDynamicLights") && parts.length >= 2) {
                        this.ofDynamicLights = Integer.valueOf(parts[1]);
                        this.ofDynamicLights = limit(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
                    }

                    if (parts[0].equals("ofScreenshotSize") && parts.length >= 2) {
                        this.ofScreenshotSize = Integer.valueOf(parts[1]);
                        this.ofScreenshotSize = Config.limit(this.ofScreenshotSize, 1, 4);
                    }

                    if (parts[0].equals("ofCustomEntityModels") && parts.length >= 2) {
                        this.ofCustomEntityModels = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofCustomGuis") && parts.length >= 2) {
                        this.ofCustomGuis = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofShowGlErrors") && parts.length >= 2) {
                        this.ofShowGlErrors = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofFullscreenMode") && parts.length >= 2) {
                        this.ofFullscreenMode = parts[1];
                    }

                    if (parts[0].equals("ofFastMath") && parts.length >= 2) {
                        this.ofFastMath = Boolean.valueOf(parts[1]);
                        MathHelper.fastMath = this.ofFastMath;
                    }

                    if (parts[0].equals("ofFastRender") && parts.length >= 2) {
                        this.ofFastRender = Boolean.valueOf(parts[1]);
                    }

                    if (parts[0].equals("ofTranslucentBlocks") && parts.length >= 2) {
                        this.ofTranslucentBlocks = Integer.valueOf(parts[1]);
                        this.ofTranslucentBlocks = Config.limit(this.ofTranslucentBlocks, 0, 2);
                    }

                    if (parts[0].equals("key_" + this.ofKeyBindZoom.getKeyDescription())) {
                        this.ofKeyBindZoom.setKeyCode(Integer.parseInt(parts[1]));
                    }
                } catch (Exception e) {
                    Config.dbg("Skipping bad option: " + str);
                    e.printStackTrace();
                }
            }

            KeyUtils.fixKeyConflicts(this.keyBindings, new KeyBinding[]{this.ofKeyBindZoom});
            KeyBinding.resetKeyBindingArrayAndHash();
            reader.close();
        } catch (Exception e2) {
            Config.warn("Failed to load options");
            e2.printStackTrace();
        }
    }

    public void saveOfOptions() {
        try {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFileOF), "UTF-8"));
            writer.println("ofFogType:" + this.ofFogType);
            writer.println("ofFogStart:" + this.ofFogStart);
            writer.println("ofMipmapType:" + this.ofMipmapType);
            writer.println("ofOcclusionFancy:" + this.ofOcclusionFancy);
            writer.println("ofSmoothFps:" + this.ofSmoothFps);
            writer.println("ofSmoothWorld:" + this.ofSmoothWorld);
            writer.println("ofAoLevel:" + this.ofAoLevel);
            writer.println("ofClouds:" + this.ofClouds);
            writer.println("ofCloudsHeight:" + this.ofCloudsHeight);
            writer.println("ofTrees:" + this.ofTrees);
            writer.println("ofDroppedItems:" + this.ofDroppedItems);
            writer.println("ofRain:" + this.ofRain);
            writer.println("ofAnimatedWater:" + this.ofAnimatedWater);
            writer.println("ofAnimatedLava:" + this.ofAnimatedLava);
            writer.println("ofAnimatedFire:" + this.ofAnimatedFire);
            writer.println("ofAnimatedPortal:" + this.ofAnimatedPortal);
            writer.println("ofAnimatedRedstone:" + this.ofAnimatedRedstone);
            writer.println("ofAnimatedExplosion:" + this.ofAnimatedExplosion);
            writer.println("ofAnimatedFlame:" + this.ofAnimatedFlame);
            writer.println("ofAnimatedSmoke:" + this.ofAnimatedSmoke);
            writer.println("ofVoidParticles:" + this.ofVoidParticles);
            writer.println("ofWaterParticles:" + this.ofWaterParticles);
            writer.println("ofPortalParticles:" + this.ofPortalParticles);
            writer.println("ofPotionParticles:" + this.ofPotionParticles);
            writer.println("ofFireworkParticles:" + this.ofFireworkParticles);
            writer.println("ofDrippingWaterLava:" + this.ofDrippingWaterLava);
            writer.println("ofAnimatedTerrain:" + this.ofAnimatedTerrain);
            writer.println("ofAnimatedTextures:" + this.ofAnimatedTextures);
            writer.println("ofRainSplash:" + this.ofRainSplash);
            writer.println("ofLagometer:" + this.ofLagometer);
            writer.println("ofShowFps:" + this.ofShowFps);
            writer.println("ofAutoSaveTicks:" + this.ofAutoSaveTicks);
            writer.println("ofBetterGrass:" + this.ofBetterGrass);
            writer.println("ofConnectedTextures:" + this.ofConnectedTextures);
            writer.println("ofWeather:" + this.ofWeather);
            writer.println("ofSky:" + this.ofSky);
            writer.println("ofStars:" + this.ofStars);
            writer.println("ofSunMoon:" + this.ofSunMoon);
            writer.println("ofVignette:" + this.ofVignette);
            writer.println("ofChunkUpdates:" + this.ofChunkUpdates);
            writer.println("ofChunkUpdatesDynamic:" + this.ofChunkUpdatesDynamic);
            writer.println("ofTime:" + this.ofTime);
            writer.println("ofClearWater:" + this.ofClearWater);
            writer.println("ofAaLevel:" + this.ofAaLevel);
            writer.println("ofAfLevel:" + this.ofAfLevel);
            writer.println("ofProfiler:" + this.ofProfiler);
            writer.println("ofBetterSnow:" + this.ofBetterSnow);
            writer.println("ofSwampColors:" + this.ofSwampColors);
            writer.println("ofRandomEntities:" + this.ofRandomEntities);
            writer.println("ofSmoothBiomes:" + this.ofSmoothBiomes);
            writer.println("ofCustomFonts:" + this.ofCustomFonts);
            writer.println("ofCustomColors:" + this.ofCustomColors);
            writer.println("ofCustomItems:" + this.ofCustomItems);
            writer.println("ofCustomSky:" + this.ofCustomSky);
            writer.println("ofShowCapes:" + this.ofShowCapes);
            writer.println("ofNaturalTextures:" + this.ofNaturalTextures);
            writer.println("ofEmissiveTextures:" + this.ofEmissiveTextures);
            writer.println("ofLazyChunkLoading:" + this.ofLazyChunkLoading);
            writer.println("ofRenderRegions:" + this.ofRenderRegions);
            writer.println("ofSmartAnimations:" + this.ofSmartAnimations);
            writer.println("ofDynamicFov:" + this.ofDynamicFov);
            writer.println("ofAlternateBlocks:" + this.ofAlternateBlocks);
            writer.println("ofDynamicLights:" + this.ofDynamicLights);
            writer.println("ofScreenshotSize:" + this.ofScreenshotSize);
            writer.println("ofCustomEntityModels:" + this.ofCustomEntityModels);
            writer.println("ofCustomGuis:" + this.ofCustomGuis);
            writer.println("ofShowGlErrors:" + this.ofShowGlErrors);
            writer.println("ofFullscreenMode:" + this.ofFullscreenMode);
            writer.println("ofFastMath:" + this.ofFastMath);
            writer.println("ofFastRender:" + this.ofFastRender);
            writer.println("ofTranslucentBlocks:" + this.ofTranslucentBlocks);
            writer.println("key_" + this.ofKeyBindZoom.getKeyDescription() + ":" + this.ofKeyBindZoom.getKeyCode());
            writer.close();
        } catch (Exception e) {
            Config.warn("Failed to save options");
            e.printStackTrace();
        }
    }

    private void updateRenderClouds() {
        switch (this.ofClouds) {
            case 1:
                this.clouds = 1;
                break;
            case 2:
                this.clouds = 2;
                break;
            case 3:
                this.clouds = 0;
                break;
            default:
                if (this.fancyGraphics) {
                    this.clouds = 2;
                } else {
                    this.clouds = 1;
                }
        }
    }

    public void resetSettings() {
        this.renderDistanceChunks = 8;
        this.viewBobbing = true;
        this.anaglyph = false;
        this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
        this.enableVsync = false;
        this.updateVSync();
        this.mipmapLevels = 4;
        this.fancyGraphics = true;
        this.ambientOcclusion = 2;
        this.clouds = 2;
        this.fovSetting = 70.0F;
        this.gammaSetting = 0.0F;
        this.guiScale = 0;
        this.particleSetting = 0;
        this.heldItemTooltips = true;
        this.useVbo = false;
        this.forceUnicodeFont = false;
        this.ofFogType = 1;
        this.ofFogStart = 0.8F;
        this.ofMipmapType = 0;
        this.ofOcclusionFancy = false;
        this.ofSmartAnimations = false;
        this.ofSmoothFps = false;
        Config.updateAvailableProcessors();
        this.ofSmoothWorld = Config.isSingleProcessor();
        this.ofLazyChunkLoading = false;
        this.ofRenderRegions = false;
        this.ofFastMath = false;
        this.ofFastRender = false;
        this.ofTranslucentBlocks = 0;
        this.ofDynamicFov = true;
        this.ofAlternateBlocks = true;
        this.ofDynamicLights = 3;
        this.ofScreenshotSize = 1;
        this.ofCustomEntityModels = true;
        this.ofCustomGuis = true;
        this.ofShowGlErrors = true;
        this.ofAoLevel = 1.0F;
        this.ofAaLevel = 0;
        this.ofAfLevel = 1;
        this.ofClouds = 0;
        this.ofCloudsHeight = 0.0F;
        this.ofTrees = 0;
        this.ofRain = 0;
        this.ofBetterGrass = 3;
        this.ofAutoSaveTicks = 4000;
        this.ofLagometer = false;
        this.ofShowFps = false;
        this.ofProfiler = false;
        this.ofWeather = true;
        this.ofSky = true;
        this.ofStars = true;
        this.ofSunMoon = true;
        this.ofVignette = 0;
        this.ofChunkUpdates = 1;
        this.ofChunkUpdatesDynamic = false;
        this.ofTime = 0;
        this.ofClearWater = false;
        this.ofBetterSnow = false;
        this.ofFullscreenMode = "Default";
        this.ofSwampColors = true;
        this.ofRandomEntities = true;
        this.ofSmoothBiomes = true;
        this.ofCustomFonts = true;
        this.ofCustomColors = true;
        this.ofCustomItems = true;
        this.ofCustomSky = true;
        this.ofShowCapes = true;
        this.ofConnectedTextures = 2;
        this.ofNaturalTextures = false;
        this.ofEmissiveTextures = true;
        this.ofAnimatedWater = 0;
        this.ofAnimatedLava = 0;
        this.ofAnimatedFire = true;
        this.ofAnimatedPortal = true;
        this.ofAnimatedRedstone = true;
        this.ofAnimatedExplosion = true;
        this.ofAnimatedFlame = true;
        this.ofAnimatedSmoke = true;
        this.ofVoidParticles = true;
        this.ofWaterParticles = true;
        this.ofRainSplash = true;
        this.ofPortalParticles = true;
        this.ofPotionParticles = true;
        this.ofFireworkParticles = true;
        this.ofDrippingWaterLava = true;
        this.ofAnimatedTerrain = true;
        this.ofAnimatedTextures = true;
        Shaders.setShaderPack("OFF");
        Shaders.configAntialiasingLevel = 0;
        Shaders.uninit();
        Shaders.storeConfig();
        this.updateWaterOpacity();
        this.mc.refreshResources();
        this.saveOptions();
    }

    public void updateVSync() {
        Display.setVSyncEnabled(this.enableVsync);
    }

    private void updateWaterOpacity() {
        if (Config.isIntegratedServerRunning()) {
            Config.waterOpacityChanged = true;
        }

        ClearWater.updateWaterOpacity(this, this.mc.theWorld);
    }

    public void setAllAnimations(boolean p_setAllAnimations_1_) {
        int n = p_setAllAnimations_1_ ? 0 : 2;
        this.ofAnimatedWater = n;
        this.ofAnimatedLava = n;
        this.ofAnimatedFire = p_setAllAnimations_1_;
        this.ofAnimatedPortal = p_setAllAnimations_1_;
        this.ofAnimatedRedstone = p_setAllAnimations_1_;
        this.ofAnimatedExplosion = p_setAllAnimations_1_;
        this.ofAnimatedFlame = p_setAllAnimations_1_;
        this.ofAnimatedSmoke = p_setAllAnimations_1_;
        this.ofVoidParticles = p_setAllAnimations_1_;
        this.ofWaterParticles = p_setAllAnimations_1_;
        this.ofRainSplash = p_setAllAnimations_1_;
        this.ofPortalParticles = p_setAllAnimations_1_;
        this.ofPotionParticles = p_setAllAnimations_1_;
        this.ofFireworkParticles = p_setAllAnimations_1_;
        this.particleSetting = p_setAllAnimations_1_ ? 0 : 2;
        this.ofDrippingWaterLava = p_setAllAnimations_1_;
        this.ofAnimatedTerrain = p_setAllAnimations_1_;
        this.ofAnimatedTextures = p_setAllAnimations_1_;
    }

    private static int nextValue(int p_nextValue_0_, int[] p_nextValue_1_) {
        int n = indexOf(p_nextValue_0_, p_nextValue_1_);
        if (n < 0) {
            return p_nextValue_1_[0];
        }
        if (++n >= p_nextValue_1_.length) {
            n = 0;
        }

        return p_nextValue_1_[n];
    }

    private static int limit(int p_limit_0_, int[] p_limit_1_) {
        int n = indexOf(p_limit_0_, p_limit_1_);
        return n < 0 ? p_limit_1_[0] : p_limit_0_;
    }

    private static int indexOf(int p_indexOf_0_, int[] p_indexOf_1_) {
        for (int i = 0; i < p_indexOf_1_.length; i++) {
            if (p_indexOf_1_[i] == p_indexOf_0_) {
                return i;
            }
        }

        return -1;
    }

    public static enum Options {
        INVERT_MOUSE("options.invertMouse", false, true),
        SENSITIVITY("options.sensitivity", true, false),
        FOV("options.fov", true, false, 30.0F, 110.0F, 1.0F),
        GAMMA("options.gamma", true, false),
        SATURATION("options.saturation", true, false),
        RENDER_DISTANCE("options.renderDistance", true, false, 2.0F, 16.0F, 1.0F),
        VIEW_BOBBING("options.viewBobbing", false, true),
        ANAGLYPH("options.anaglyph", false, true),
        FRAMERATE_LIMIT("options.framerateLimit", true, false, 0.0F, 260.0F, 5.0F),
        FBO_ENABLE("options.fboEnable", false, true),
        RENDER_CLOUDS("options.renderClouds", false, false),
        GRAPHICS("options.graphics", false, false),
        AMBIENT_OCCLUSION("options.ao", false, false),
        GUI_SCALE("options.guiScale", false, false),
        PARTICLES("options.particles", false, false),
        CHAT_VISIBILITY("options.chat.visibility", false, false),
        CHAT_COLOR("options.chat.color", false, true),
        CHAT_LINKS("options.chat.links", false, true),
        CHAT_OPACITY("options.chat.opacity", true, false),
        CHAT_LINKS_PROMPT("options.chat.links.prompt", false, true),
        SNOOPER_ENABLED("options.snooper", false, true),
        USE_FULLSCREEN("options.fullscreen", false, true),
        ENABLE_VSYNC("options.vsync", false, true),
        USE_VBO("options.vbo", false, true),
        TOUCHSCREEN("options.touchscreen", false, true),
        CHAT_SCALE("options.chat.scale", true, false),
        CHAT_WIDTH("options.chat.width", true, false),
        CHAT_HEIGHT_FOCUSED("options.chat.height.focused", true, false),
        CHAT_HEIGHT_UNFOCUSED("options.chat.height.unfocused", true, false),
        MIPMAP_LEVELS("options.mipmapLevels", true, false, 0.0F, 4.0F, 1.0F),
        FORCE_UNICODE_FONT("options.forceUnicodeFont", false, true),
        STREAM_BYTES_PER_PIXEL("options.stream.bytesPerPixel", true, false),
        STREAM_VOLUME_MIC("options.stream.micVolumne", true, false),
        STREAM_VOLUME_SYSTEM("options.stream.systemVolume", true, false),
        STREAM_KBPS("options.stream.kbps", true, false),
        STREAM_FPS("options.stream.fps", true, false),
        STREAM_COMPRESSION("options.stream.compression", false, false),
        STREAM_SEND_METADATA("options.stream.sendMetadata", false, true),
        STREAM_CHAT_ENABLED("options.stream.chat.enabled", false, false),
        STREAM_CHAT_USER_FILTER("options.stream.chat.userFilter", false, false),
        STREAM_MIC_TOGGLE_BEHAVIOR("options.stream.micToggleBehavior", false, false),
        BLOCK_ALTERNATIVES("options.blockAlternatives", false, true),
        REDUCED_DEBUG_INFO("options.reducedDebugInfo", false, true),
        ENTITY_SHADOWS("options.entityShadows", false, true),
        REALMS_NOTIFICATIONS("options.realmsNotifications", false, true),
        FOG_FANCY("of.options.FOG_FANCY", false, false),
        FOG_START("of.options.FOG_START", false, false),
        MIPMAP_TYPE("of.options.MIPMAP_TYPE", true, false, 0.0F, 3.0F, 1.0F),
        SMOOTH_FPS("of.options.SMOOTH_FPS", false, false),
        CLOUDS("of.options.CLOUDS", false, false),
        CLOUD_HEIGHT("of.options.CLOUD_HEIGHT", true, false),
        TREES("of.options.TREES", false, false),
        RAIN("of.options.RAIN", false, false),
        ANIMATED_WATER("of.options.ANIMATED_WATER", false, false),
        ANIMATED_LAVA("of.options.ANIMATED_LAVA", false, false),
        ANIMATED_FIRE("of.options.ANIMATED_FIRE", false, false),
        ANIMATED_PORTAL("of.options.ANIMATED_PORTAL", false, false),
        AO_LEVEL("of.options.AO_LEVEL", true, false),
        LAGOMETER("of.options.LAGOMETER", false, false),
        SHOW_FPS("of.options.SHOW_FPS", false, false),
        AUTOSAVE_TICKS("of.options.AUTOSAVE_TICKS", false, false),
        BETTER_GRASS("of.options.BETTER_GRASS", false, false),
        ANIMATED_REDSTONE("of.options.ANIMATED_REDSTONE", false, false),
        ANIMATED_EXPLOSION("of.options.ANIMATED_EXPLOSION", false, false),
        ANIMATED_FLAME("of.options.ANIMATED_FLAME", false, false),
        ANIMATED_SMOKE("of.options.ANIMATED_SMOKE", false, false),
        WEATHER("of.options.WEATHER", false, false),
        SKY("of.options.SKY", false, false),
        STARS("of.options.STARS", false, false),
        SUN_MOON("of.options.SUN_MOON", false, false),
        VIGNETTE("of.options.VIGNETTE", false, false),
        CHUNK_UPDATES("of.options.CHUNK_UPDATES", false, false),
        CHUNK_UPDATES_DYNAMIC("of.options.CHUNK_UPDATES_DYNAMIC", false, false),
        TIME("of.options.TIME", false, false),
        CLEAR_WATER("of.options.CLEAR_WATER", false, false),
        SMOOTH_WORLD("of.options.SMOOTH_WORLD", false, false),
        VOID_PARTICLES("of.options.VOID_PARTICLES", false, false),
        WATER_PARTICLES("of.options.WATER_PARTICLES", false, false),
        RAIN_SPLASH("of.options.RAIN_SPLASH", false, false),
        PORTAL_PARTICLES("of.options.PORTAL_PARTICLES", false, false),
        POTION_PARTICLES("of.options.POTION_PARTICLES", false, false),
        FIREWORK_PARTICLES("of.options.FIREWORK_PARTICLES", false, false),
        PROFILER("of.options.PROFILER", false, false),
        DRIPPING_WATER_LAVA("of.options.DRIPPING_WATER_LAVA", false, false),
        BETTER_SNOW("of.options.BETTER_SNOW", false, false),
        FULLSCREEN_MODE("of.options.FULLSCREEN_MODE", true, false, 0.0F, Config.getDisplayModes().length, 1.0F),
        ANIMATED_TERRAIN("of.options.ANIMATED_TERRAIN", false, false),
        SWAMP_COLORS("of.options.SWAMP_COLORS", false, false),
        RANDOM_ENTITIES("of.options.RANDOM_ENTITIES", false, false),
        SMOOTH_BIOMES("of.options.SMOOTH_BIOMES", false, false),
        CUSTOM_FONTS("of.options.CUSTOM_FONTS", false, false),
        CUSTOM_COLORS("of.options.CUSTOM_COLORS", false, false),
        SHOW_CAPES("of.options.SHOW_CAPES", false, false),
        CONNECTED_TEXTURES("of.options.CONNECTED_TEXTURES", false, false),
        CUSTOM_ITEMS("of.options.CUSTOM_ITEMS", false, false),
        AA_LEVEL("of.options.AA_LEVEL", true, false, 0.0F, 16.0F, 1.0F),
        AF_LEVEL("of.options.AF_LEVEL", true, false, 1.0F, 16.0F, 1.0F),
        ANIMATED_TEXTURES("of.options.ANIMATED_TEXTURES", false, false),
        NATURAL_TEXTURES("of.options.NATURAL_TEXTURES", false, false),
        EMISSIVE_TEXTURES("of.options.EMISSIVE_TEXTURES", false, false),
        HELD_ITEM_TOOLTIPS("of.options.HELD_ITEM_TOOLTIPS", false, false),
        DROPPED_ITEMS("of.options.DROPPED_ITEMS", false, false),
        LAZY_CHUNK_LOADING("of.options.LAZY_CHUNK_LOADING", false, false),
        CUSTOM_SKY("of.options.CUSTOM_SKY", false, false),
        FAST_MATH("of.options.FAST_MATH", false, false),
        FAST_RENDER("of.options.FAST_RENDER", false, false),
        TRANSLUCENT_BLOCKS("of.options.TRANSLUCENT_BLOCKS", false, false),
        DYNAMIC_FOV("of.options.DYNAMIC_FOV", false, false),
        DYNAMIC_LIGHTS("of.options.DYNAMIC_LIGHTS", false, false),
        ALTERNATE_BLOCKS("of.options.ALTERNATE_BLOCKS", false, false),
        CUSTOM_ENTITY_MODELS("of.options.CUSTOM_ENTITY_MODELS", false, false),
        ADVANCED_TOOLTIPS("of.options.ADVANCED_TOOLTIPS", false, false),
        SCREENSHOT_SIZE("of.options.SCREENSHOT_SIZE", false, false),
        CUSTOM_GUIS("of.options.CUSTOM_GUIS", false, false),
        RENDER_REGIONS("of.options.RENDER_REGIONS", false, false),
        SHOW_GL_ERRORS("of.options.SHOW_GL_ERRORS", false, false),
        SMART_ANIMATIONS("of.options.SMART_ANIMATIONS", false, false);

        private final boolean enumFloat;
        private final boolean enumBoolean;
        private final String enumString;
        private final float valueStep;
        float valueMin;
        float valueMax;

        public static GameSettings.Options getEnumOptions(int p_74379_0_) {
            for (GameSettings.Options options : values()) {
                if (options.returnEnumOrdinal() == p_74379_0_) {
                    return options;
                }
            }

            return null;
        }

        private Options(String str, boolean flag, boolean flag2) {
            this(str, flag, flag2, 0.0F, 1.0F, 0.0F);
        }

        private Options(String enumString, boolean enumFloat, boolean enumBoolean, float valueMin, float valueMax, float valueStep) {
            this.enumString = enumString;
            this.enumFloat = enumFloat;
            this.enumBoolean = enumBoolean;
            this.valueMin = valueMin;
            this.valueMax = valueMax;
            this.valueStep = valueStep;
        }

        public boolean getEnumFloat() {
            return this.enumFloat;
        }

        public boolean getEnumBoolean() {
            return this.enumBoolean;
        }

        public int returnEnumOrdinal() {
            return this.ordinal();
        }

        public String getEnumString() {
            return this.enumString;
        }

        public float getValueMax() {
            return this.valueMax;
        }

        public void setValueMax(float p_148263_1_) {
            this.valueMax = p_148263_1_;
        }

        public float normalizeValue(float p_148266_1_) {
            return MathHelper.clamp_float((this.snapToStepClamp(p_148266_1_) - this.valueMin) / (this.valueMax - this.valueMin), 0.0F, 1.0F);
        }

        public float denormalizeValue(float p_148262_1_) {
            return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin) * MathHelper.clamp_float(p_148262_1_, 0.0F, 1.0F));
        }

        public float snapToStepClamp(float p_148268_1_) {
            p_148268_1_ = this.snapToStep(p_148268_1_);
            return MathHelper.clamp_float(p_148268_1_, this.valueMin, this.valueMax);
        }

        protected float snapToStep(float p_148264_1_) {
            if (this.valueStep > 0.0F) {
                p_148264_1_ = this.valueStep * Math.round(p_148264_1_ / this.valueStep);
            }

            return p_148264_1_;
        }
    }
}
