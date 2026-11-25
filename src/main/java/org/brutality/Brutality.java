package org.brutality;

import de.florianmichael.viamcp.ViaMCP;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.network.NetHandlerPlayClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brutality.alt.altmanager.AccountStatusStore;
import org.brutality.alt.altmanager.backup.PitPandaBackup;
import org.brutality.alt.altmanager.gui.GuiAltManager;
import org.brutality.buttons.DisableCrasher;
import org.brutality.clickgui.discord.DiscordGUI;
import org.brutality.clickgui.newclick.NewClickGUI;
import org.brutality.command.CmdManager;
import org.brutality.extensions.APPDATAHandler;
import org.brutality.extensions.DiscordRP;
import org.brutality.gui.font.GetMcComponent;
import org.brutality.gui.font.api.GlyphFonts;
import org.brutality.gui.font.atlas.SFProApi;
import org.brutality.lag.handler.LagHandler;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.client.DiscordRPC;
import org.brutality.proxy.ProxyConfig;
import org.brutality.proxy.ProxyManager;
import org.brutality.script.ScriptManager;
import org.brutality.settings.SettingsManager;
// VENDOR-DISABLED: import org.brutality.system.AuthCredentials;
// VENDOR-DISABLED: import org.brutality.system.AuthHandler;
// VENDOR-DISABLED: import org.brutality.system.AuthLoginDialog;
// VENDOR-DISABLED: import org.brutality.system.BackendAuthService;
import org.brutality.system.ClientMetaManager;
// VENDOR-DISABLED: import org.brutality.system.HardwareIdGenerator;
// VENDOR-DISABLED: import org.brutality.system.JarIntegrityVerifier;
// VENDOR-DISABLED: import org.brutality.system.LicenseValidator;
// VENDOR-DISABLED: import org.brutality.system.ProximityTracker;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
// VENDOR-DISABLED: import org.brutality.utils.AntiDecompilerGuard;
import org.brutality.utils.BrutalityUserHelper;
import org.brutality.utils.OnChatReceivedHelper;
import org.brutality.utils.chat.GetPersistentLineCountHelper;
import org.brutality.utils.hypixel.BanDetector;
import org.brutality.utils.hypixel.BanManager;
import org.brutality.utils.hypixel.HypixelBan;
import org.brutality.utils.network.PingerUtils;
import org.brutality.utils.notifications.GetReasonHelper;
import org.brutality.utils.player.HotbarHelper;
import org.brutality.utils.player.PlayerHelper7;
// VENDOR-DISABLED: import org.brutality.utils.player.PlaytimeTelemetry;
import org.brutality.utils.render.UpdateAnimationsHelper;
import org.brutality.utils.rotation.OnJumpHelper;
import org.lwjgl.opengl.Display;

public class Brutality implements BanDetector.Listener {
    private static final Logger logger = LogManager.getLogger("BrutalityStartup");
    public static String name = "Brutality";
    public static String version = "2.6";
    public static String text = "Brutality Team";
    public static Brutality instance = new Brutality();
    public static SettingsManager settingsManager;
    public static ModuleManager moduleManager;
    private PingerUtils pingerUtils;
    public static DiscordRP discordRP = null;
    public static CmdManager cmdManager;
    public static NewClickGUI newClickGUI;
    public static DiscordGUI discordGUI;
    public static GlyphFonts glyphFonts;
    public static APPDATAHandler appdataHandler;
    public static DisableCrasher disableCrasher;
    public static OnChatReceivedHelper onChatReceivedHelper;
    public static UpdateAnimationsHelper updateAnimationsHelper;
    public static GetPersistentLineCountHelper getPersistentLineCountHelper;
    public static BrutalityUserHelper brutalityUserHelper;
    public static GetMcComponent getMcComponent;
    public static OnJumpHelper onJumpHelper;
    public static LagHandler lagHandler;
    // VENDOR-DISABLED: public static ProximityTracker proximityTracker; public static AuthHandler authHandler;
    public static BanDetector banDetector;
    public static BanManager banManager;
    public static ScriptManager scriptManager;
    public static PitPandaBackup pitPandaBackup;
    public static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
    private static final List<GetReasonHelper> entries = new CopyOnWriteArrayList<>();
    private static final boolean flag = Boolean.parseBoolean(System.getProperty("brutality.skipAuth", "true"));

    public static void startClient() {
        // VENDOR-DISABLED: LicenseValidator, AntiDecompilerGuard, AuthHandler, AuthLoginDialog, HardwareIdGenerator, and RuntimeAuthGuard startup checks
        initializeClient();
    }

    private static String computeString(Throwable failure) {
        Throwable t = failure;

        for (int i = 0; i < 8 && t.getCause() != null && t.getCause() != t; i++) {
            t = t.getCause();
        }

        return t.getClass().getName();
    }

    private static void handleString(String title, String detail) {
        logger.error("[Brutality] Startup stopped: {}. {}", title, detail == null ? "" : detail);

        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.exit(1);
    }

    private static void initializeClient() {
        try {
            try {
                ViaMCP.create();
            } catch (LinkageError | Exception e) {
                logger.error("[Brutality] ViaVersion initialization failed ({}).", computeString(e));
            }

            ClientMetaManager.init();
            brutalityUserHelper = new BrutalityUserHelper();
            onChatReceivedHelper = new OnChatReceivedHelper();
            updateAnimationsHelper = new UpdateAnimationsHelper();
            getPersistentLineCountHelper = new GetPersistentLineCountHelper();
            settingsManager = new SettingsManager();
            moduleManager = new ModuleManager();
            cmdManager = new CmdManager();
            appdataHandler = new APPDATAHandler();
            newClickGUI = new NewClickGUI();
            glyphFonts = SFProApi.glyphFonts;
            discordGUI = new DiscordGUI();
            scheduledExecutorService.schedule(() -> newClickGUI.process13(), 500L, TimeUnit.MILLISECONDS);
            // VENDOR-DISABLED: proximityTracker = new ProximityTracker();
            scriptManager = new ScriptManager();
            getMcComponent = new GetMcComponent();
            getMcComponent.init();
            appdataHandler.init();
            PlayerHelper7.process2();
            HotbarHelper.init();
            // VENDOR-DISABLED: PlaytimeTelemetry.init();
            lagHandler = new LagHandler();
            onJumpHelper = new OnJumpHelper();
            scriptManager.process2();
            ProxyManager.load();
            banManager = new BanManager();
            banManager.init();
            pitPandaBackup = new PitPandaBackup();
            pitPandaBackup.init();
            banDetector = new BanDetector(instance);
            banDetector.process3();
            process();
            // VENDOR-DISABLED: process5(); startIntegrityDaemon(); authHandler.getUsername()
            String str = "Developer";
            handleString(str);
            logger.info("[Brutality] Client initialization completed.");
        } catch (Exception e2) {
            logger.error("[Brutality] Client initialization failed ({}).", computeString(e2));
            e2.printStackTrace();
        }
    }

    private static void process() {
    }

    private static void handleString(String username) {
        try {
            Display.setTitle("Minecraft 1.8.9");
        } catch (Exception ignored) {
        }
    }

    @Override
    public void handleHypixelBan2(HypixelBan ban, String banId, String reason, BanDetector.TemporaryBanMode banType) {
        if (banType == BanDetector.TemporaryBanMode.TEMPORARY_BAN) {
            flagBannedConnectionProxy();
        }

        if (!ban.isUnbanDate()) {
        }

        String str = computeString();
        if (banManager != null) {
            switch (banType) {
                case TEMPORARY_BAN:
                    banManager.handleString(str, ban, banId, reason);
                    break;
                case ACCOUNT_BLOCKED:
                    banManager.handleString(str, banId);
                    break;
                case RECOVERY_PHASE:
                    String str2 = ban.getUnbanDate() == 0L ? "Permanent" : ban.computeString();
                    banManager.handleString(str, str2, reason);
            }

            handleString3(str);
        }

        try {
            AccountStatusStore.handleString(str);
        } catch (Exception ignored) {
        }

        handleString(str, ban, banId, reason, banType);
        handleHypixelBan(ban, banId, reason, banType);
        if (DiscordRPC.getInstance() != null) {
            handleHypixelBan(ban, banType);
        }
    }

    @Override
    public void handleString2(String blockId) {
        String str = computeString();
        if (banManager != null) {
            banManager.handleString(str, blockId);
            handleString3(str);
        }

        try {
            AccountStatusStore.handleString(str);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void handleString2(String duration, String reason) {
        String str = computeString();
        if (banManager != null) {
            banManager.handleString(str, duration, reason);
        }
    }

    @Override
    public void handleString(String liftDateText, long liftEpoch) {
        flagBannedConnectionProxy();
        String str = computeString();

        try {
            AccountStatusStore.handleString(str, liftEpoch, liftDateText);
        } catch (Exception ignored) {
        }

        handleString3(str);
    }

    @Override
    public void process3() {
        DiscordRPC instance = DiscordRPC.getInstance();
        if (instance != null && instance.isActive()) {
            instance.handleString("Playing", "Connected to Hypixel");
        }
    }

    private static void flagBannedConnectionProxy() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null) {
            NetHandlerPlayClient netHandler = mc.getNetHandler();
            ProxyConfig proxyConfig = mc.currentScreen instanceof GuiDisconnected gui
                ? gui.getConnectionProxy()
                : (netHandler == null ? null : netHandler.getNetworkManager().getConnectionProxy());
            if (proxyConfig != null) {
                mc.addScheduledTask(() -> ProxyManager.handleProxyConfig3(proxyConfig));
            }
        }
    }

    private static String computeString() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc != null && mc.thePlayer != null && mc.thePlayer.getName() != null) {
                return mc.thePlayer.getName();
            }

            if (mc != null && mc.getSession() != null && mc.getSession().getUsername() != null) {
                return mc.getSession().getUsername();
            }

            // VENDOR-DISABLED: authHandler.getUsername() fallback
        } catch (Exception ignored) {
        }

        return "Player";
    }

    private static void handleHypixelBan(HypixelBan ban, String banId, String reason, BanDetector.TemporaryBanMode banType) {
        try {
            String str = String.format(
                "[%s] %s DETECTED - ID: %s, Reason: %s, Duration: %s",
                new Date(),
                banType.getDisplayName().toUpperCase(),
                banId,
                reason,
                ban.isUnbanDate() ? "Permanent" : ban.computeString()
            );
        } catch (Exception ignored) {
        }
    }

    private static void handleHypixelBan(HypixelBan ban, BanDetector.TemporaryBanMode banType) {
        try {
            if (!DiscordRPC.checkState()) {
                return;
            }

            DiscordRPC instance = DiscordRPC.getInstance();
            if (instance != null && instance.isActive()) {
                instance.handleString("Hypixel Status", switch (banType) {
                    case TEMPORARY_BAN -> "Temporarily Banned (" + ban.computeString() + ")";
                    case ACCOUNT_BLOCKED -> "Permanently Banned";
                    case RECOVERY_PHASE -> "Recovery Phase (" + ban.computeString() + ")";
                    default -> "Banned from Hypixel";
                });
            }
        } catch (Exception ignored) {
        }
    }

    private static void handleString3(String username) {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc != null && mc.currentScreen instanceof GuiAltManager gui) {
                gui.handleString3(username);
            }
        } catch (Exception ignored) {
        }
    }

    private static void process5() {
        // VENDOR-DISABLED: RuntimeAuthGuard and JarIntegrityVerifier runtime checks
    }

    private static void startIntegrityDaemon() {
        // VENDOR-DISABLED: GC-Monitor RuntimeAuthGuard / JarIntegrityVerifier / AuthCredentials background tamper-strike daemon
    }

    public static boolean checkState() {
        // VENDOR-DISABLED: RuntimeAuthGuard.checkState5() && RuntimeAuthGuard.checkState4() && !RuntimeAuthGuard.checkState()
        return true;
    }

    private static void handleString(String username, HypixelBan ban, String banId, String reason, BanDetector.TemporaryBanMode banType) {
        try {
            GetReasonHelper getReasonHelper = new GetReasonHelper(username, ban, banId, reason, banType);
            getReasonHelper.process();
            entries.add(getReasonHelper);
        } catch (Exception ignored) {
        }
    }

    public static void renderNotifications() {
        if (!entries.isEmpty()) {
            try {
                entries.removeIf(getReasonHelper -> !getReasonHelper.isActive());

                for (GetReasonHelper getReasonHelper2 : entries) {
                    getReasonHelper2.render();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static void process4() {
        entries.clear();
    }

    public static void process2() {
        // VENDOR-DISABLED: PlaytimeTelemetry.shutdown();

        if (banDetector != null) {
            try {
                banDetector.shutdown();
            } catch (Exception ignored2) {
            }
        }

        // VENDOR-DISABLED: authHandler.close();

        if (discordRP != null) {
            try {
                discordRP.shutdown();
            } catch (Exception ignored4) {
            }
        }
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(Brutality::process2));
        startClient();
    }

    // VENDOR-DISABLED: public static AuthHandler getAuthHandler2()

    @Generated
    public void setPingerUtils(PingerUtils pingerUtils) {
        this.pingerUtils = pingerUtils;
    }

    @Generated
    public static String getName() {
        return name;
    }

    @Generated
    public static Brutality getInstance() {
        return instance;
    }

    @Generated
    public static SettingsManager getSettingsManager() {
        return settingsManager;
    }

    @Generated
    public static ModuleManager getModuleManager() {
        return moduleManager;
    }

    @Generated
    public PingerUtils getPingerUtils() {
        return this.pingerUtils;
    }

    @Generated
    public static DiscordRP getDiscordRP() {
        return discordRP;
    }

    @Generated
    public static CmdManager getCmdManager() {
        return cmdManager;
    }

    @Generated
    public static NewClickGUI getNewClickGUI() {
        return newClickGUI;
    }

    @Generated
    public static DiscordGUI getDiscordGUI() {
        return discordGUI;
    }

    @Generated
    public static GlyphFonts getGlyphFonts() {
        return glyphFonts;
    }

    @Generated
    public static LagHandler getLagHandler() {
        return lagHandler;
    }

    // VENDOR-DISABLED: public static AuthHandler getAuthHandler()

    @Generated
    public static BanDetector getBanDetector() {
        return banDetector;
    }

    @Generated
    public static BanManager getBanManager() {
        return banManager;
    }

    @Generated
    public static ScriptManager getScriptManager() {
        return scriptManager;
    }

    @Generated
    public static PitPandaBackup getPitPandaBackup() {
        return pitPandaBackup;
    }
}
