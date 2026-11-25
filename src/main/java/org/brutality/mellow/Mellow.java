package org.brutality.mellow;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import org.brutality.event.Event;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.mellow.api.ApiHandler;
import org.brutality.mellow.api.ERRORHandler;
import org.brutality.mellow.api.PingHandler;
import org.brutality.mellow.api.StripColorHandler;
import org.brutality.mellow.api.SuccessHandler;
import org.brutality.mellow.cache.ClearCacheHandler;
import org.brutality.mellow.commands.HelpHandler;
import org.brutality.mellow.config.IsPregameStatsHandler;
import org.brutality.mellow.data.GetPlayerNameHandler;
import org.brutality.mellow.hud.GetEnabledHandler;
import org.brutality.mellow.hud.GetShowTotalHandler;
import org.brutality.mellow.util.RemoveHelper;
import org.brutality.mellow.util.UtilHelper;
import org.brutality.mellow.util.PingLookupCache;
import org.brutality.mellow.util.UtilHelper3;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;

public class Mellow extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static Mellow instance;
    public static IsPregameStatsHandler isPregameStatsHandler;
    public static ClearCacheHandler clearCacheHandler;
    public static RemoveHelper removeHelper;
    public static UtilHelper utilHelper;
    public static UtilHelper3 utilHelper3;
    public static PingLookupCache utilHelper2;
    public static GetShowTotalHandler getShowTotalHandler;
    public static GetEnabledHandler getEnabledHandler;
    public static final Map<String, GetPlayerNameHandler> lookupMap = new ConcurrentHashMap<>();
    private final BooleanSetting enabledSetting = new BooleanSetting("Enabled", this, true);
    private final BooleanSetting tabStatsSetting = new BooleanSetting("Tab Stats", this, true);
    private final BooleanSetting showTagsSetting = new BooleanSetting("Show Tags", this, false);
    private final BooleanSetting autoWhoSetting = new BooleanSetting("Auto /who", this, true);
    private final BooleanSetting printStatsSetting = new BooleanSetting("Print Stats", this, false);
    private final BooleanSetting pregameStatsSetting = new BooleanSetting("Pregame Stats", this, true);
    private final BooleanSetting autoSkinDenickSetting = new BooleanSetting("Auto Skin Denick", this, true);
    private final NumberSetting minFKDRSetting = new NumberSetting("Min FKDR", this, -1.0, -1.0, 500.0, 1.0);
    private final ModeSetting abyssSetting = new ModeSetting("Stats Provider", this, "Nadeshiko", Arrays.asList("Nadeshiko", "Abyss"));
    private final BooleanSetting urchinEnabledSetting = new BooleanSetting("Urchin Enabled", this, false);
    private final BooleanSetting showUrchinTagsSetting = new BooleanSetting("Show Urchin Tags", this, true);
    private final StringSetting urchinKeySetting = new StringSetting("Urchin Key", this, "");
    private final BooleanSetting seraphEnabledSetting = new BooleanSetting("Seraph Enabled", this, false);
    private final BooleanSetting showSeraphTagsSetting = new BooleanSetting("Show Seraph Tags", this, true);
    private final StringSetting seraphKeySetting = new StringSetting("Seraph Key", this, "");
    private final ModeSetting urchinSetting = new ModeSetting("Ping Provider", this, "None", Arrays.asList("None", "Polsu", "Urchin"));
    private final StringSetting polsuKeySetting = new StringSetting("Polsu Key", this, "");
    private final BooleanSetting showStarsSetting = new BooleanSetting("Show Stars", this, true);
    private final BooleanSetting showFKDRSetting = new BooleanSetting("Show FKDR", this, true);
    private final BooleanSetting showWinstreakSetting = new BooleanSetting("Show Winstreak", this, true);
    private final BooleanSetting showWLRSetting = new BooleanSetting("Show WLR", this, false);
    private final BooleanSetting showFinalsSetting = new BooleanSetting("Show Finals", this, false);
    private final BooleanSetting showBedsSetting = new BooleanSetting("Show Beds", this, false);
    private final BooleanSetting statDot34Setting = new BooleanSetting("Stat Dot 3-4", this, true);
    private final BooleanSetting statDot45Setting = new BooleanSetting("Stat Dot 4-5", this, true);
    private final BooleanSetting statDot56Setting = new BooleanSetting("Stat Dot 5-6", this, true);
    private final BooleanSetting numberDenickerSetting = new BooleanSetting("Number Denicker", this, false);
    private final StringSetting auroraKeySetting = new StringSetting("Aurora Key", this, "");
    private final BooleanSetting fuzzyMatchSetting = new BooleanSetting("Fuzzy Match", this, true);
    private boolean flag = false;
    private boolean flag2 = false;
    private ProtectYourBedHandler protectYourBedHandler;
    private Path configPath;
    private ExecutorService executor;
    private int tickCounter = 0;
    private boolean flag3 = false;

    public Mellow() {
        super("Mellow", 0, Category.MISCELLANEOUS);
        this.handleSettingArray(
            new Setting[]{
                this.enabledSetting,
                this.tabStatsSetting,
                this.showTagsSetting,
                this.autoWhoSetting,
                this.printStatsSetting,
                this.pregameStatsSetting,
                this.autoSkinDenickSetting,
                this.minFKDRSetting,
                this.abyssSetting,
                this.urchinEnabledSetting,
                this.showUrchinTagsSetting,
                this.urchinKeySetting,
                this.seraphEnabledSetting,
                this.showSeraphTagsSetting,
                this.seraphKeySetting,
                this.urchinSetting,
                this.polsuKeySetting,
                this.showStarsSetting,
                this.showFKDRSetting,
                this.showWinstreakSetting,
                this.showWLRSetting,
                this.showFinalsSetting,
                this.showBedsSetting,
                this.statDot34Setting,
                this.statDot45Setting,
                this.statDot56Setting,
                this.numberDenickerSetting,
                this.auroraKeySetting,
                this.fuzzyMatchSetting
            }
        );
        instance = this;
    }

    public BooleanSetting getStatDot45Setting() {
        return this.statDot45Setting;
    }

    public BooleanSetting getShowTagsSetting() {
        return this.showTagsSetting;
    }

    public BooleanSetting getAutoSkinDenickSetting() {
        return this.autoSkinDenickSetting;
    }

    @Override
    public void onEnable() {
        this.executor = Executors.newFixedThreadPool(4);
        this.configPath = Paths.get("brutality/mellow.json");
        isPregameStatsHandler = IsPregameStatsHandler.computeIsPregameStatsHandler(this);
        removeHelper = new RemoveHelper();
        removeHelper.load();
        StripColorHandler stripColorHandler = new StripColorHandler();
        ERRORHandler errorHandler = new ERRORHandler(stripColorHandler);
        SuccessHandler successHandler = new SuccessHandler(stripColorHandler);
        PingHandler pingHandler = new PingHandler(stripColorHandler);
        ApiHandler apiHandler = new ApiHandler(stripColorHandler);
        utilHelper2 = new PingLookupCache();
        utilHelper3 = new UtilHelper3(pingHandler, apiHandler);
        clearCacheHandler = new ClearCacheHandler(stripColorHandler, errorHandler, successHandler, pingHandler, apiHandler, utilHelper3, isPregameStatsHandler);
        utilHelper = new UtilHelper(clearCacheHandler, isPregameStatsHandler);
        this.protectYourBedHandler = new ProtectYourBedHandler(isPregameStatsHandler, utilHelper, clearCacheHandler, removeHelper);
        getShowTotalHandler = new GetShowTotalHandler();
        getEnabledHandler = new GetEnabledHandler();
        HelpHandler.setMellow(this);
        super.onEnable();
    }

    public BooleanSetting getTabStatsSetting() {
        return this.tabStatsSetting;
    }

    public BooleanSetting getEnabledSetting() {
        return this.enabledSetting;
    }

    public boolean isFlag2() {
        return this.flag2;
    }

    public StringSetting getAuroraKeySetting() {
        return this.auroraKeySetting;
    }

    public StringSetting getUrchinKeySetting() {
        return this.urchinKeySetting;
    }

    public StringSetting getSeraphKeySetting() {
        return this.seraphKeySetting;
    }

    public BooleanSetting getShowUrchinTagsSetting() {
        return this.showUrchinTagsSetting;
    }

    public static BooleanSetting computeBooleanSetting2() {
        return instance != null ? instance.tabStatsSetting : null;
    }

    private void handleString3(String str) {
        try {
            if (str.contains("ONLINE:")) {
                String str2 = str.substring(str.indexOf("ONLINE:") + 8);
                String[] parts = str2.split(",");

                for (String str3 : parts) {
                    String str4 = str3.trim();
                    if (!str4.isEmpty() && !str4.equals(mc.thePlayer.getName())) {
                        this.handleString(str4);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void requestStats(String str) {
        this.executor.submit(() -> clearCacheHandler.handleString2(str));
    }

    public BooleanSetting getUrchinEnabledSetting() {
        return this.urchinEnabledSetting;
    }

    public BooleanSetting getFuzzyMatchSetting() {
        return this.fuzzyMatchSetting;
    }

    public StringSetting getPolsuKeySetting() {
        return this.polsuKeySetting;
    }

    private void loadConfig() {
        try {
            if (Files.exists(this.configPath)) {
                String str = new String(Files.readAllBytes(this.configPath));
                JsonObject json = new Gson().fromJson(str, JsonObject.class);
                if (json.has("urchinKey")) {
                    this.urchinKeySetting.setValue(json.get("urchinKey").getAsString());
                }

                if (json.has("seraphKey")) {
                    this.seraphKeySetting.setValue(json.get("seraphKey").getAsString());
                }

                if (json.has("polsuKey")) {
                    this.polsuKeySetting.setValue(json.get("polsuKey").getAsString());
                }

                if (json.has("auroraKey")) {
                    this.auroraKeySetting.setValue(json.get("auroraKey").getAsString());
                }

                if (json.has("statsProvider")) {
                    this.abyssSetting.handleString(json.get("statsProvider").getAsString());
                }

                if (json.has("minFkdr")) {
                    this.minFKDRSetting.handleDouble2(json.get("minFkdr").getAsDouble());
                }
            }
        } catch (Exception ignored) {
        }
    }

    public BooleanSetting getShowFinalsSetting() {
        return this.showFinalsSetting;
    }

    public BooleanSetting getStatDot56Setting() {
        return this.statDot56Setting;
    }

    public BooleanSetting getShowWinstreakSetting() {
        return this.showWinstreakSetting;
    }

    public NumberSetting getMinFKDRSetting() {
        return this.minFKDRSetting;
    }

    public BooleanSetting getSeraphEnabledSetting() {
        return this.seraphEnabledSetting;
    }

    public BooleanSetting getPregameStatsSetting() {
        return this.pregameStatsSetting;
    }

    private void process4() {
        if (mc.thePlayer != null && mc.ingameGUI != null) {
            if (getShowTotalHandler != null) {
                getShowTotalHandler.render();
            }

            if (getEnabledHandler != null) {
                getEnabledHandler.render();
            }
        }
    }

    public GetPlayerNameHandler computeGetPlayerNameHandler(String str) {
        return lookupMap.get(str);
    }

    public BooleanSetting getShowWLRSetting() {
        return this.showWLRSetting;
    }

    public static BooleanSetting computeBooleanSetting() {
        return instance != null ? instance.enabledSetting : null;
    }

    private void handleString2(String str) {
    }

    public ModeSetting getAbyssSetting() {
        return this.abyssSetting;
    }

    public BooleanSetting getShowFKDRSetting() {
        return this.showFKDRSetting;
    }

    public ModeSetting getUrchinSetting() {
        return this.urchinSetting;
    }

    public BooleanSetting getShowBedsSetting() {
        return this.showBedsSetting;
    }

    public static Mellow getInstance() {
        return instance;
    }

    public UtilHelper3 computeUtilHelper3() {
        return utilHelper3;
    }

    public BooleanSetting getNumberDenickerSetting() {
        return this.numberDenickerSetting;
    }

    public BooleanSetting getAutoWhoSetting() {
        return this.autoWhoSetting;
    }

    private void process3() {
        if (this.flag && !this.flag3 && this.autoWhoSetting.isEnabled() && mc.thePlayer != null) {
            mc.thePlayer.sendChatMessage("/who");
            this.flag3 = true;
        }

        if (!this.flag) {
            this.flag3 = false;
        }
    }

    public BooleanSetting getShowSeraphTagsSetting() {
        return this.showSeraphTagsSetting;
    }

    public BooleanSetting getPrintStatsSetting() {
        return this.printStatsSetting;
    }

    public boolean isFlag() {
        return this.flag;
    }

    @Override
    public void onEvent(Event event) {
        if (this.enabledSetting.isEnabled()) {
            if (event instanceof EventPacketReceive) {
                this.onEventPacketReceive((EventPacketReceive)event);
            } else if (event instanceof EventRender2D) {
                this.process4();
            } else if (event instanceof EventTick) {
                this.process3();
            }
        }
    }

    private void onEventPacketReceive(EventPacketReceive event) {
        if (this.protectYourBedHandler != null) {
            this.protectYourBedHandler.onEvent(event);
        }
    }

    public BooleanSetting getShowStarsSetting() {
        return this.showStarsSetting;
    }

    private void handleString(String str) {
        if (mc.getNetHandler() != null && this.executor != null) {
            this.executor.submit(() -> {
                try {
                    if (!clearCacheHandler.matchesString2(str)) {
                        clearCacheHandler.handleString2(str);
                    }

                    if (this.urchinEnabledSetting.isEnabled()) {
                        utilHelper3.handleString(str);
                    }

                    if (this.seraphEnabledSetting.isEnabled()) {
                        utilHelper3.handleString2(str);
                    }

                    if (this.urchinSetting.getText().equals("Urchin")) {
                        utilHelper2.handleString(str);
                    }
                } catch (Exception ignored) {
                }
            });
        }
    }

    @Override
    public void onDisable() {
        if (this.executor != null) {
            this.executor.shutdown();
        }

        this.process();
        super.onDisable();
    }

    public BooleanSetting getStatDot34Setting() {
        return this.statDot34Setting;
    }

    public void process() {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("enabled", this.enabledSetting.isEnabled());
            json.addProperty("tabStats", this.tabStatsSetting.isEnabled());
            json.addProperty("showTags", this.showTagsSetting.isEnabled());
            json.addProperty("autoWho", this.autoWhoSetting.isEnabled());
            json.addProperty("printStats", this.printStatsSetting.isEnabled());
            json.addProperty("pregameStats", this.pregameStatsSetting.isEnabled());
            json.addProperty("autoSkinDenick", this.autoSkinDenickSetting.isEnabled());
            json.addProperty("minFkdr", this.minFKDRSetting.computeDouble());
            json.addProperty("statsProvider", this.abyssSetting.getText());
            json.addProperty("urchinEnabled", this.urchinEnabledSetting.isEnabled());
            json.addProperty("showUrchinTags", this.showUrchinTagsSetting.isEnabled());
            json.addProperty("urchinKey", this.urchinKeySetting.getValue());
            json.addProperty("seraphEnabled", this.seraphEnabledSetting.isEnabled());
            json.addProperty("showSeraphTags", this.showSeraphTagsSetting.isEnabled());
            json.addProperty("seraphKey", this.seraphKeySetting.getValue());
            json.addProperty("pingProvider", this.urchinSetting.getText());
            json.addProperty("polsuKey", this.polsuKeySetting.getValue());
            json.addProperty("numberDenicker", this.numberDenickerSetting.isEnabled());
            json.addProperty("auroraKey", this.auroraKeySetting.getValue());
            Files.write(this.configPath, new Gson().toJson((JsonElement)json).getBytes());
        } catch (Exception ignored) {
        }
    }
}
