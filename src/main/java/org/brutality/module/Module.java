package org.brutality.module;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.Packet;
import net.minecraft.util.ChatComponentText;
import org.brutality.Brutality;
import org.brutality.clickgui.newclick.HypixelPitType;
import org.brutality.event.Event;
import org.brutality.event.impl.render.SetShaderTypeEvent;
import org.brutality.extensions.APPDATAHandler;
import org.brutality.module.impl.client.AutoDisable;
import org.brutality.module.impl.client.ClientSettings;
import org.brutality.module.impl.misc.ModuleSound;
import org.brutality.module.impl.visual.Notifications;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
// VENDOR-DISABLED: import org.brutality.system.EntitlementState;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.animations.ForwardsType;
import org.brutality.utils.animations.IsDoneHelper;
import org.brutality.utils.animations.impl.ImplAnimation;
import org.brutality.utils.hypixel.PhaseHelper;
import org.brutality.utils.mcp.McpApi3;
import org.brutality.utils.mcp.McpApi4;
import org.brutality.utils.render.FontResourceLoader;
import org.brutality.utils.render.IsChoiceHelper;

public abstract class Module implements McpApi3, McpApi4 {
    private static final ClassValue<Boolean> classValue = new ClassValue<Boolean>() {
        protected Boolean computeValue(Class<?> clazz) {
            try {
                return clazz.getMethod("onRender").getDeclaringClass() != Module.class;
            } catch (SecurityException | ReflectiveOperationException e) {
                return true;
            }
        }
    };
    protected static Minecraft mc = Minecraft.getMinecraft();
    public String name2;
    public int key2;
    public boolean toggled;
    private Category category;
    private HypixelPitType hypixelPitType;
    private PitCategory pitCategory;
    private boolean requiresInGame = true;
    private String suffix2;
    private ModeSetting modeSetting;
    private static final Map<Class<?>, Module> lookupMap = new ConcurrentHashMap<>();
    private final IsDoneHelper animation = new ImplAnimation(250, 1.0).computeIsDoneHelper(ForwardsType.BACKWARDS);

    public final boolean checkState29() {
        return classValue.get(this.getClass());
    }

    public boolean onBlurMode(SetShaderTypeEvent.BlurMode type) {
        return this.getCategory() == Category.SCRIPTS;
    }

    protected final void process18() {
        if (this.modeSetting == null) {
            this.modeSetting = IsChoiceHelper.computeModeSetting("Font", this);
            this.handleSettingArray(this.modeSetting);
            lookupMap.put(this.getClass(), this);
        }
    }

    public final FontRenderer computeFontRenderer2() {
        return this.computeFontRenderer2(18.0F);
    }

    public final FontRenderer computeFontRenderer(float size) {
        if (this.modeSetting != null
            && !IsChoiceHelper.matchesString(this.modeSetting.getText())
            && mc != null
            && mc.fontRendererObj != null
            && mc.getTextureManager() != null) {
            return FontResourceLoader.computeDrawStringWithShadowComponent5(this.modeSetting.getText(), size);
        }
        return mc == null ? null : mc.fontRendererObj;
    }

    public final FontRenderer computeFontRenderer2(float size) {
        if (this.modeSetting != null
            && !IsChoiceHelper.matchesString(this.modeSetting.getText())
            && mc != null
            && mc.fontRendererObj != null
            && mc.getTextureManager() != null) {
            return FontResourceLoader.computeDrawStringWithShadowComponent2(this.modeSetting.getText(), size);
        }
        return mc == null ? null : mc.fontRendererObj;
    }

    protected static FontRenderer computeFontRenderer(Class<? extends Module> type) {
        Module module = lookupMap.get(type);
        return module == null ? mc.fontRendererObj : module.computeFontRenderer2();
    }

    public Module(String nm, int k, Category c) {
        this.name2 = nm;
        this.key2 = k;
        this.category = c;
        this.toggled = false;
        this.process9();
    }

    public void handleHypixelPitType(HypixelPitType hypixelPitType, PitCategory pitCategory) {
        this.hypixelPitType = hypixelPitType;
        this.pitCategory = pitCategory;
    }

    public HypixelPitType getHypixelPitType() {
        return this.hypixelPitType;
    }

    public PitCategory getPitCategory() {
        return this.pitCategory;
    }

    public void process28() {
        // VENDOR-DISABLED: EntitlementState.checkState() Premium Pit entitlement gate
        if (false) {
            this.handleFlag3(false);
            NotificationFeedHandler.handleString(this.name2 + " §cis unavailable without Premium Pit§f.");
        } else if (this.requiresInGame && !this.checkState49()) {
            NotificationFeedHandler.handleString(this.name2 + " §ccan only be used in-game§f.");
        } else if (PhaseHelper.matchesModule(this) && !this.checkState44() && !this.checkState21()) {
            PhaseHelper.handleModule(this);
        } else {
            if (PhaseHelper.matchesModule(this) && !this.checkState44() && this.checkState21()) {
                NotificationFeedHandler.handleString("⚠ Bypassed blacklist for " + this.name2);
            }

            boolean flag = this.checkState44();
            if (!flag || !this.checkState3() && !this.checkState7()) {
                boolean flag2 = !flag;
                this.handleFlag3(flag2);
                // VENDOR-DISABLED: RuntimeAuthGuard.process2();

                Notifications instance = Notifications.getInstance();
                if (ClientSettings.checkState5() && instance != null && instance.checkState44()) {
                    if (instance.checkState9()) {
                        if (mc.thePlayer != null) {
                            String str = this.checkState44() ? "§a§lON" : "§c§lOFF";
                            mc.thePlayer.addChatMessage(new ChatComponentText("§8[§4B§8] §f" + this.name2 + ": " + str));
                        }
                    } else {
                        NotificationFeedHandler.handleString(this.name2 + (this.checkState44() ? " §aEnabled§f." : " §cDisabled§f."));
                    }
                }

                if (ModuleSound.getInstance() != null && ModuleSound.getInstance().checkState44()) {
                    ModuleSound.getInstance().handleFlag(this.checkState44());
                }

                APPDATAHandler.process4();
            }
        }
    }

    protected boolean checkState49() {
        return mc != null && mc.thePlayer != null && mc.theWorld != null;
    }

    private boolean checkState21() {
        return GuiScreen.isCtrlKeyDown();
    }

    private boolean checkState10() {
        // VENDOR-DISABLED: RuntimeAuthGuard.checkState5() && RuntimeAuthGuard.checkState4()
        return true;
    }

    public void setRequiresInGame(boolean requiresInGame) {
        this.requiresInGame = requiresInGame;
    }

    public boolean isRequiresInGame() {
        return this.requiresInGame;
    }

    public void onEnable() {
        if (this.requiresInGame && !this.checkState49()) {
            this.handleFlag3(false);
        }
    }

    public void onDisable() {
    }

    public void onUpdate() {
        if (this.checkState44() && this.requiresInGame && !this.checkState49()) {
            this.handleFlag3(false);
            NotificationFeedHandler.handleString(this.name2 + " §cDisabled - Not in game§f.");
        } else {
            // VENDOR-DISABLED: EntitlementState.checkState() gate and RuntimeAuthGuard tamper-strike random module disable
        }
    }

    public void onRender() {
    }

    public void process19() {
    }

    public void process9() {
    }

    public void handleSettingArray(Setting... settings) {
        for (Setting setting : settings) {
            Brutality.settingsManager.addSetting(setting);
        }
    }

    public void process2() {
    }

    public abstract void onEvent(Event e);

    public Minecraft getMc() {
        return mc;
    }

    public void handleMinecraft2(Minecraft mc2) {
        mc = mc2;
    }

    public String getName() {
        return this.name2;
    }

    public void setName(String name) {
        this.name2 = name;
    }

    public int getKey() {
        return this.key2;
    }

    public void handleInt4(int key) {
        if (this.key2 != key) {
            ModuleManager.handleInt(this.key2, key, this);
            this.key2 = key;
            APPDATAHandler.process4();
        }
    }

    public boolean checkState44() {
        return this.toggled;
    }

    public boolean checkState7() {
        return false;
    }

    public void handleFlag3(boolean toggled) {
        if (toggled || !this.checkState7()) {
            if (this.toggled != toggled) {
                if (toggled) {
                    if (!ModuleManager.entries3.contains(this)) {
                        ModuleManager.entries3.add(this);
                    }
                } else {
                    ModuleManager.entries3.remove(this);
                }

                this.toggled = toggled;
                if (toggled) {
                    AutoDisable.handleModule(this);
                }

                if (toggled) {
                    this.onEnable();
                } else {
                    this.onDisable();
                }
            }
        }
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    protected EntityPlayerSP computeEntityPlayerSP() {
        return mc.thePlayer;
    }

    protected PlayerControllerMP computePlayerControllerMP() {
        return mc.playerController;
    }

    protected WorldClient computeWorldClient() {
        return mc.theWorld;
    }

    protected void sendPacket(Packet p) {
        this.computeEntityPlayerSP().sendQueue.addToSendQueue(p);
    }

    public boolean checkState3() {
        return false;
    }

    protected boolean checkState25() {
        return false;
    }

    public void process29() {
        if (!this.requiresInGame || this.checkState49()) {
            try {
                this.handleFlag3(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void process30() {
        try {
            this.handleFlag3(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void process39() {
        if (!this.checkState7()) {
            try {
                this.handleFlag3(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isSuffix2() {
        return this.suffix2 != null;
    }

    public String getSuffix() {
        return this.suffix2;
    }

    @Generated
    public IsDoneHelper getAnimation() {
        return this.animation;
    }
}
