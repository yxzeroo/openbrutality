package org.brutality.module.impl.visual;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ResourceLocation;
import org.brutality.event.Event;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.visual.targethud.TargethudHandler;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.TruceManager;
import org.brutality.utils.WHelper;
import org.brutality.utils.animation.IsFinishedHelper;
import org.brutality.utils.animation.LinearType;
import org.brutality.utils.animations.ForwardsType;
import org.brutality.utils.animations.IsDoneHelper;
import org.brutality.utils.animations.impl.ImplAnimation;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.GetColorHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.brutality.utils.render.GlStateGuard;
import org.brutality.utils.render.RenderHelper8;
import org.brutality.utils.timer.TimerUtil;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class TargetHUD extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final float factor = 120.0F;
    private static final float factor2 = 32.0F;
    private static final DecimalFormat decimalFormat = new DecimalFormat("+0.0;-0.0", new DecimalFormatSymbols(Locale.US));
    private static final Pattern pattern = Pattern.compile("§\\S$");
    private static final Pattern pattern2 = Pattern.compile("(?i)§r");
    private static final Pattern pattern3 = Pattern.compile("(?i)§[k-o]");
    private static TargetHUD instance;
    public TimerUtil timerUtil = new TimerUtil();
    public TimerUtil timerUtil2 = new TimerUtil();
    public TimerUtil timerUtil3 = new TimerUtil();
    public EntityLivingBase entityLivingBase;
    public EntityLivingBase entityLivingBase2;
    public EntityLivingBase entityLivingBase3;
    public EntityLivingBase entityLivingBase4;
    public ResourceLocation resourceLocation;
    public float factor3;
    public float factor4;
    public float factor5;
    private boolean dragging;
    private float factor6;
    private float factor7;
    public IsFinishedHelper isFinishedHelper = new IsFinishedHelper(LinearType.EASE_OUT_SINE, 300L);
    public IsFinishedHelper isFinishedHelper2 = new IsFinishedHelper(LinearType.EASE_OUT_BACK, 600L);
    public IsDoneHelper isDoneHelper = new ImplAnimation(250, 1.0);
    public IsDoneHelper isDoneHelper2 = new ImplAnimation(250, 1.0);
    private final ModeSetting myauSetting = new ModeSetting("Mode", this, "Atmosphere", Arrays.asList("Atmosphere", "Myau"));
    private final ModeSetting arrayListSetting = new ModeSetting("Color", this, "Theme", Arrays.asList("Theme", "ArrayList"));
    private final ModeSetting arrayListSetting2 = new ModeSetting("Myau Color", this, "Default", Arrays.asList("Default", "Theme", "ArrayList"))
        .computeModeSetting(() -> this.myauSetting.matchesString2("Myau"));
    private final BooleanSetting indicatorSetting = new BooleanSetting("Indicator", this, true)
        .computeBooleanSetting(() -> this.myauSetting.matchesString2("Myau"));
    private final BooleanSetting outlineSetting = new BooleanSetting("Outline", this, false)
        .computeBooleanSetting(() -> this.myauSetting.matchesString2("Myau"));
    public TargethudHandler targethudHandler = new TargethudHandler();
    public float factor8 = 120.0F;
    public float factor9 = 32.0F;
    private String text = "Atmosphere";
    private final ModeSetting rightSetting = new ModeSetting("Position X", this, "Middle", Arrays.asList("Left", "Middle", "Right"))
        .computeModeSetting(() -> this.myauSetting.matchesString2("Myau"));
    private final ModeSetting bottomSetting = new ModeSetting("Position Y", this, "Middle", Arrays.asList("Top", "Middle", "Bottom"))
        .computeModeSetting(() -> this.myauSetting.matchesString2("Myau"));
    private final NumberSetting scaleSetting = new NumberSetting("Scale", this, 1.0, 0.5, 1.5, 0.05);
    private final NumberSetting offsetXSetting = new NumberSetting("Offset X", this, 0.0, -255.0, 255.0, 1.0);
    private final NumberSetting offsetYSetting = new NumberSetting("Offset Y", this, 40.0, -255.0, 255.0, 1.0);
    private final NumberSetting backgroundSetting = new NumberSetting("Background", this, 25.0, 0.0, 100.0, 1.0);
    private final BooleanSetting headSetting = new BooleanSetting("Head", this, true);
    private final BooleanSetting showArmorSetting = new BooleanSetting("Show Armor", this, true)
        .computeBooleanSetting(() -> this.myauSetting.matchesString2("Atmosphere"));
    private final BooleanSetting showWLSetting = new BooleanSetting("Show W/L", this, true).computeBooleanSetting(this::z);
    private final BooleanSetting animationsSetting = new BooleanSetting("Animations", this, true);
    private final BooleanSetting shadowSetting = new BooleanSetting("Shadow", this, true);
    private final BooleanSetting killAuraOnlySetting = new BooleanSetting("KillAura Only", this, true);
    private final BooleanSetting chatPreviewSetting = new BooleanSetting("Chat Preview", this, false);
    private final BooleanSetting multipleTargetsSetting = new BooleanSetting("Multiple Targets", this, false);
    private final NumberSetting gridColumnsSetting = new NumberSetting("Grid Columns", this, 2.0, 1.0, 3.0, 1.0)
        .computeNumberSetting(this.multipleTargetsSetting::isEnabled);
    private final NumberSetting maxTargetsSetting = new NumberSetting("Max Targets", this, 4.0, 2.0, 9.0, 1.0)
        .computeNumberSetting(this.multipleTargetsSetting::isEnabled);
    private final NumberSetting gridGapSetting = new NumberSetting("Grid Gap", this, 6.0, 2.0, 20.0, 1.0)
        .computeNumberSetting(this.multipleTargetsSetting::isEnabled);
    private final NumberSetting targetLingerSetting = new NumberSetting("Target Linger", this, 1500.0, 250.0, 5000.0, 50.0)
        .computeNumberSetting(this.multipleTargetsSetting::isEnabled);
    private final LinkedHashMap<EntityLivingBase, Long> linkedHashMap = new LinkedHashMap<>();
    private final Map<EntityLivingBase, TargetHUD.Entry> lookupMap = new HashMap<>();
    private final Map<EntityLivingBase, Long> lookupMap2 = new HashMap<>();
    private final TargetHUD.Entry entry = new TargetHUD.Entry();
    private EntityLivingBase entityLivingBase5;
    private boolean flag;
    private float factor10;
    private float factor11;

    private ResourceLocation computeResourceLocation(EntityLivingBase living) {
        if (!(living instanceof EntityPlayer)) {
            return null;
        }
        if (mc.getNetHandler() != null) {
            NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(living.getUniqueID());
            if (playerInfo == null) {
                playerInfo = mc.getNetHandler().getPlayerInfo(living.getName());
            }

            if (playerInfo != null) {
                return playerInfo.getLocationSkin();
            }

            if (living instanceof AbstractClientPlayer) {
                return ((AbstractClientPlayer)living).getLocationSkin();
            }
        }

        return living.getUniqueID() == null ? DefaultPlayerSkin.getDefaultSkinLegacy() : DefaultPlayerSkin.getDefaultSkin(living.getUniqueID());
    }

    private Color computeColor(EntityLivingBase living) {
        if (this.arrayListSetting2.matchesString2("ArrayList")) {
            return ArrayList.computeColor(this);
        }
        if (this.arrayListSetting2.matchesString2("Theme")) {
            BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
            return brutalityHelper != null ? brutalityHelper.getColor() : Color.WHITE;
        }
        if (living instanceof EntityPlayer) {
            if (DefaultHelper.matchesString(living.getName())) {
                return new Color(5174862);
            }

            if (WHelper.matchesString(living.getName())) {
                return new Color(11141120);
            }

            if (TruceManager.matchesString(living.getName())) {
                return new Color(5147894);
            }

            Team team = ((EntityPlayer)living).getTeam();
            if (team instanceof ScorePlayerTeam) {
                String formatFromString = FontRenderer.getFormatFromString(((ScorePlayerTeam)team).getColorPrefix());

                for (int i = formatFromString.length() - 2; i >= 0; i--) {
                    if (formatFromString.charAt(i) == 167 && "0123456789abcdef".indexOf(Character.toLowerCase(formatFromString.charAt(i + 1))) >= 0) {
                        return new Color(mc.fontRendererObj.getColorCode(Character.toLowerCase(formatFromString.charAt(i + 1))));
                    }
                }
            }
        }

        return Color.WHITE;
    }

    private void process5() {
        if (mc.thePlayer != null && mc.theWorld != null) {
            EntityLivingBase living = this.computeEntityLivingBase();
            this.isFinishedHelper2.handleDouble2(living == null && !(mc.currentScreen instanceof GuiChat) ? 0.0 : 1.0);
            if (living != null) {
                this.entityLivingBase2 = living;
                this.entityLivingBase3 = living;
                if (this.animationsSetting.isEnabled()) {
                    if (this.isDoneHelper.getForwardsType() != ForwardsType.FORWARDS) {
                        this.isDoneHelper.computeIsDoneHelper(ForwardsType.FORWARDS);
                        this.isDoneHelper2.computeIsDoneHelper(ForwardsType.FORWARDS);
                    }
                } else {
                    this.isDoneHelper.process2();
                    this.isDoneHelper2.process2();
                }
            } else if (this.entityLivingBase3 != null) {
                if (this.animationsSetting.isEnabled()) {
                    if (this.isDoneHelper.getForwardsType() != ForwardsType.BACKWARDS) {
                        this.isDoneHelper.computeIsDoneHelper(ForwardsType.BACKWARDS);
                        this.isDoneHelper2.computeIsDoneHelper(ForwardsType.BACKWARDS);
                    }

                    if (this.isDoneHelper.matchesForwardsType(ForwardsType.BACKWARDS)) {
                        this.entityLivingBase3 = null;
                        this.entityLivingBase2 = null;
                    }
                } else {
                    this.entityLivingBase3 = null;
                    this.entityLivingBase2 = null;
                }
            }

            if (this.entityLivingBase3 != null) {
                if (mc.currentScreen instanceof GuiChat) {
                    this.process4();
                } else {
                    this.dragging = false;
                }

                this.handleEntityLivingBase2(this.entityLivingBase3);
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventRender2D) {
                if (!this.myauSetting.getText().equals(this.text)) {
                    this.process();
                    this.text = this.myauSetting.getText();
                }

                if (this.multipleTargetsSetting.isEnabled()) {
                    this.renderGrid();
                } else if (this.myauSetting.matchesString2("Myau")) {
                    this.process3();
                } else {
                    this.process5();
                }
            } else if (event instanceof RenderEvent4) {
                this.process();
            } else if (this.myauSetting.matchesString2("Myau")
                && event instanceof EventPacketSend event2
                && !event2.isCancelled()
                && event2.getPacket() instanceof C02PacketUseEntity) {
                C02PacketUseEntity packet = (C02PacketUseEntity)event2.getPacket();
                if (packet.getAction() == C02PacketUseEntity.Action.ATTACK && mc.theWorld != null) {
                    WorldClient world = mc.theWorld;
                    Runnable task = () -> {
                        if (mc.theWorld == world) {
                            Entity entityFromWorld = packet.getEntityFromWorld(world);
                            if (entityFromWorld instanceof EntityLivingBase && !(entityFromWorld instanceof EntityArmorStand)) {
                                this.entityLivingBase = (EntityLivingBase)entityFromWorld;
                                this.timerUtil.reset();
                            }
                        }
                    };
                    if (mc.isCallingFromMinecraftThread()) {
                        task.run();
                    } else {
                        mc.addScheduledTask(task);
                    }
                }
            }
        }
    }

    private static String computeString(float f, float f2) {
        return "§r" + decimalFormat.format(f - f2) + "§r";
    }

    private void process4() {
        if (!this.flag) {
            ScaledResolution sr = InvalidateHelper.computeScaledResolution();
            int n = Mouse.getX() * sr.getScaledWidth() / mc.displayWidth;
            int n2 = sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / mc.displayHeight - 1;
            float f = this.computeFloat(sr);
            float f2 = this.computeFloat2(sr);
            float f3 = (float)this.scaleSetting.computeDouble();
            if (Mouse.isButtonDown(0)) {
                if (!this.dragging) {
                    if (n >= f * f3 && n <= (f + this.factor8) * f3 && n2 >= f2 * f3 && n2 <= (f2 + this.factor9) * f3) {
                        this.dragging = true;
                        this.factor6 = n - f * f3;
                        this.factor7 = n2 - f2 * f3;
                    }
                } else {
                    this.handleScaledResolution(sr, (n - this.factor6) / f3, (n2 - this.factor7) / f3);
                }
            } else {
                this.dragging = false;
            }
        }
    }

    private Color computeColor2(EntityLivingBase living) {
        if (this.arrayListSetting.matchesString2("ArrayList")) {
            return ArrayList.computeColor(this);
        }
        BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
        return brutalityHelper != null ? brutalityHelper.getColor() : Color.WHITE;
    }

    public static void onAttackDispatched(Packet<?> packet) {
        TargetHUD targetHUD = instance;
        if (targetHUD != null && targetHUD.checkState44() && targetHUD.multipleTargetsSetting.isEnabled() && mc.theWorld != null && packet instanceof C02PacketUseEntity packet2) {
            if (packet2.getAction() == C02PacketUseEntity.Action.ATTACK) {
                WorldClient world = mc.theWorld;
                Runnable task = () -> {
                    if (mc.theWorld == world && targetHUD.checkState44() && targetHUD.multipleTargetsSetting.isEnabled()) {
                        Entity entityFromWorld = packet2.getEntityFromWorld(world);
                        if (entityFromWorld instanceof EntityLivingBase && !(entityFromWorld instanceof EntityArmorStand)) {
                            KillAura instance2 = KillAura.getInstance();
                            if (!targetHUD.killAuraOnlySetting.isEnabled()
                                || instance2 != null && instance2.checkState44() && (KillAura.entityLivingBase2 == entityFromWorld || instance2.entries5.contains(entityFromWorld))) {
                                targetHUD.handleEntityLivingBase3((EntityLivingBase)entityFromWorld);
                            }
                        }
                    }
                };
                if (mc.isCallingFromMinecraftThread()) {
                    task.run();
                } else {
                    mc.addScheduledTask(task);
                }
            }
        }
    }

    private List<EntityLivingBase> computeList() {
        KillAura instance = KillAura.getInstance();
        if (instance != null && instance.checkState44()) {
            for (EntityLivingBase living : new java.util.ArrayList<>(instance.entries5)) {
                if (this.matchesEntityLivingBase(living) && !living.isDead) {
                    this.handleEntityLivingBase3(living);
                }
            }

            if (this.matchesEntityLivingBase(KillAura.entityLivingBase2) && !KillAura.entityLivingBase2.isDead) {
                this.handleEntityLivingBase3(KillAura.entityLivingBase2);
            }
        }

        if (this.chatPreviewSetting.isEnabled() && mc.currentScreen instanceof GuiChat && this.linkedHashMap.isEmpty()) {
            this.handleEntityLivingBase3(mc.thePlayer);
        }

        long now = System.currentTimeMillis();

        for (Map.Entry entry : this.linkedHashMap.entrySet()) {
            if (!this.matchesEntityLivingBase((EntityLivingBase)entry.getKey())
                || ((EntityLivingBase)entry.getKey()).isDead
                || now - (Long)entry.getValue() > this.targetLingerSetting.computeDouble()) {
                this.lookupMap2.putIfAbsent((EntityLivingBase)entry.getKey(), now);
            }
        }

        this.linkedHashMap
            .entrySet()
            .removeIf(
                entry2 -> this.lookupMap2.containsKey(entry2.getKey())
                    && (!this.animationsSetting.isEnabled() || now - this.lookupMap2.get(entry2.getKey()) >= 250L)
            );
        this.lookupMap2.keySet().retainAll(this.linkedHashMap.keySet());
        this.lookupMap.keySet().retainAll(this.linkedHashMap.keySet());
        java.util.ArrayList list = new java.util.ArrayList<>(this.linkedHashMap.keySet());
        return list.subList(0, Math.min(list.size(), (int)this.maxTargetsSetting.computeDouble()));
    }

    private float computeFloat(ScaledResolution sr) {
        if (this.flag) {
            return this.factor10;
        }
        float f = (float)this.offsetXSetting.computeDouble() / (float)this.scaleSetting.computeDouble();
        if (this.rightSetting.matchesString2("Middle")) {
            f += sr.getScaledWidth() / (float)this.scaleSetting.computeDouble() / 2.0F - this.factor8 / 2.0F;
        } else if (this.rightSetting.matchesString2("Right")) {
            f = sr.getScaledWidth() / (float)this.scaleSetting.computeDouble() - this.factor8 - Math.abs(f);
        }

        return f;
    }

    private void handleEntityPlayer(EntityPlayer player, int n, int n2, float f) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            float zLevel = mc.getRenderItem().zLevel;
            GlStateManager.pushMatrix();

            try {
                mc.getRenderItem().zLevel = 0.0F;
                RenderHelper8.process();
                GlStateManager.enableDepth();
                GlStateManager.depthMask(true);
                GlStateManager.depthFunc(GL11.GL_LEQUAL);
                RenderHelper.enableGUIStandardItemLighting();
                GlStateManager.color(1.0F, 1.0F, 1.0F, f);
                GlStateManager.translate((float)n, (float)n2, 0.0F);
                GlStateManager.scale(0.6F, 0.6F, 0.6F);
                int n3 = 0;
                ItemStack heldItem = player.getHeldItem();
                if (heldItem != null) {
                    mc.getRenderItem().renderItemAndEffectIntoGUI(heldItem, n3, 0);
                    n3 += 18;
                }

                if (this.showArmorSetting.isEnabled()) {
                    for (int i = 3; i >= 0; i--) {
                        ItemStack currentArmor = player.getCurrentArmor(i);
                        if (currentArmor != null) {
                            mc.getRenderItem().renderItemAndEffectIntoGUI(currentArmor, n3, 0);
                            n3 += 18;
                        }
                    }
                }
            } finally {
                mc.getRenderItem().zLevel = zLevel;
                GlStateManager.popMatrix();
            }
        }
    }

    private void handleEntityLivingBase(EntityLivingBase entityLivingBase4) {
        float f = entityLivingBase4.getHealth() + entityLivingBase4.getAbsorptionAmount();
        if (entityLivingBase4 != this.entityLivingBase4) {
            this.resourceLocation = null;
            this.timerUtil2.reset();
            this.factor3 = f;
            this.factor4 = f;
            this.factor5 = entityLivingBase4.getMaxHealth() + entityLivingBase4.getAbsorptionAmount();
            this.entityLivingBase4 = entityLivingBase4;
        }

        if (Math.abs(this.factor4 - f) > 0.01F) {
            if (this.flag && f < this.factor4) {
                this.timerUtil3.reset();
            }

            this.factor3 = this.factor4;
            this.factor4 = f;
            this.factor5 = entityLivingBase4.getMaxHealth() + entityLivingBase4.getAbsorptionAmount();
            this.timerUtil2.reset();
        } else if (!this.animationsSetting.isEnabled() || this.timerUtil2.matchesLong10(150L)) {
            this.factor3 = this.factor4;
            this.factor5 = entityLivingBase4.getMaxHealth() + entityLivingBase4.getAbsorptionAmount();
        }

        this.resourceLocation = this.computeResourceLocation(entityLivingBase4);
        ScaledResolution sr = InvalidateHelper.computeScaledResolution();
        FontRenderer fontRenderer = this.computeFontRenderer2();
        String str = this.computeString(entityLivingBase4);
        float f2 = Math.max(120.0F, 32.0F + fontRenderer.getStringWidth(str) + (this.showWLSetting.isEnabled() ? 28.0F : 6.0F));
        this.factor8 = f2;
        this.factor9 = 32.0F;
        float f3 = this.computeFloat(sr);
        float f4 = this.computeFloat2(sr);
        if (this.animationsSetting.isEnabled() && !this.flag) {
            float f5 = (1.0F - (float)this.isDoneHelper2.computeDouble()) * 30.0F;
            if (this.rightSetting.matchesString2("Left")) {
                f3 -= f5;
            } else if (this.rightSetting.matchesString2("Right")) {
                f3 += f5;
            }
        }

        GlStateManager.pushMatrix();
        float f6 = Math.max((float)this.isDoneHelper.computeDouble(), 0.001F);
        float f7 = (float)this.scaleSetting.computeDouble() * (float)(this.flag ? 1.0 : this.isFinishedHelper2.getValue());
        GlStateManager.translate(
            (f3 + f2 / 2.0F) * this.scaleSetting.computeDouble(), (f4 + this.factor9 / 2.0F) * this.scaleSetting.computeDouble(), -450.0
        );
        GlStateManager.scale(f7, f7, 1.0F);
        GlStateManager.translate(-(f3 + f2 / 2.0F), -(f4 + this.factor9 / 2.0F), 0.0F);
        float f8 = (float)this.backgroundSetting.computeDouble() / 100.0F;
        Color color = GetColorHelper.computeColor(new Color(0, 0, 0, 165), f8 * f6);
        int rgb = GetColorHelper.computeColor(Color.WHITE, f6).getRGB();
        Gui.drawRect((int)f3, (int)f4, (int)(f3 + f2), (int)(f4 + 32.0F), color.getRGB());
        if (this.headSetting.isEnabled() && entityLivingBase4 instanceof EntityPlayer && this.resourceLocation != null) {
            RenderHelper8.process();
            float f9 = 0.0F;
            if (!this.timerUtil3.matchesLong10(500L)) {
                f9 = 1.0F - (float)this.timerUtil3.computeLong5() / 500.0F;
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, f6);
            mc.getTextureManager().bindTexture(this.resourceLocation);
            Gui.drawScaledCustomSizeModalRect((int)(f3 + 3.0F), (int)(f4 + 3.0F), 8.0F, 8.0F, 8, 8, 26, 26, 64.0F, 64.0F);
            Gui.drawScaledCustomSizeModalRect((int)(f3 + 3.0F), (int)(f4 + 3.0F), 40.0F, 8.0F, 8, 8, 26, 26, 64.0F, 64.0F);
            if (f9 > 0.0F) {
                Color color2 = new Color(1.0F, 0.0F, 0.0F, f9 * f6);
                Gui.drawRect((int)(f3 + 3.0F), (int)(f4 + 3.0F), (int)(f3 + 29.0F), (int)(f4 + 29.0F), color2.getRGB());
            }
        }

        RenderHelper8.process();
        this.handleFontRenderer(fontRenderer, str, f3 + 32.0F, f4 + 5.0F, rgb);
        float f10 = (mc.thePlayer.getHealth() + mc.thePlayer.getAbsorptionAmount()) / 2.0F;
        float f11 = f / 2.0F;
        float f12 = Math.min(Math.max((f10 - f11 + 1.0F) / 2.0F, 0.0F), 1.0F);
        Color color3 = GetColorHelper.computeColor(f12);
        String str2 = computeString2(f10, f11);
        int textWidth = computeFontRenderer(TargetHUD.class).getStringWidth(str2);
        int rgb2 = GetColorHelper.computeColor(color3, f6).getRGB();
        if (this.showWLSetting.isEnabled()) {
            this.handleFontRenderer(fontRenderer, str2, f3 + f2 - 2.0F - textWidth, f4 + 5.0F, rgb2);
        }

        String str3 = computeString(f10, f11);
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.75F, 0.75F, 0.75F);
        float f13 = (f4 + 16.0F) / 0.75F;
        int rgb3 = GetColorHelper.computeColor(new Color(0.7F, 0.7F, 0.7F), f6).getRGB();
        float f14 = fontRenderer.getStringWidth(str3) * 0.75F;
        float f15 = (f3 + f2 - f14 - 3.0F) / 0.75F;
        this.handleFontRenderer(fontRenderer, str3, f15, f13, rgb3);
        GlStateManager.popMatrix();
        if (entityLivingBase4 instanceof EntityPlayer) {
            this.handleEntityPlayer((EntityPlayer)entityLivingBase4, (int)(f3 + 32.0F), (int)(f4 + 14.0F), f6);
        }

        RenderHelper8.process();
        float f16 = Math.max(85.0F, f2 - 37.0F);
        float f17 = 3.0F;
        float f18 = f3 + 32.0F;
        float f19 = f4 + 26.0F;
        float f20 = (float)Math.min(Math.max(this.timerUtil2.computeLong5(), 0L), 150L);
        float f21 = this.factor5 != 0.0F ? Math.min(Math.max(computeFloat(this.factor4, this.factor3, f20 / 150.0F) / this.factor5, 0.0F), 1.0F) : 1.0F;
        this.isFinishedHelper.handleDouble2(f21);
        float f22 = f16 * (float)this.isFinishedHelper.getValue();
        Color color4 = this.computeColor2(entityLivingBase4);
        Color color5 = GetColorHelper.computeColor(color4, 0.35F);
        Color color6 = GetColorHelper.computeColor(new Color(0, 0, 0, 180), f8 * f6);
        Gui.drawRect((int)f18, (int)f19, (int)(f18 + f16), (int)(f19 + f17), color6.getRGB());
        if (this.factor3 > this.factor4 && f20 < 150.0F) {
            float f23 = this.factor5 != 0.0F ? Math.min(Math.max(this.factor3 / this.factor5, 0.0F), 1.0F) : 1.0F;
            float f24 = f16 * f23;
            Color color7 = GetColorHelper.computeColor(color5, f6);
            Gui.drawRect((int)f18, (int)f19, (int)(f18 + f24), (int)(f19 + f17), color7.getRGB());
        }

        Color color8 = GetColorHelper.computeColor(color4, f6);
        Gui.drawRect((int)f18, (int)f19, (int)(f18 + f22), (int)(f19 + f17), color8.getRGB());
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private boolean matchesEntityLivingBase(EntityLivingBase living) {
        return living != null && mc.theWorld != null && mc.theWorld.getEntityByID(living.getEntityId()) == living;
    }

    private void renderGrid() {
        if (mc.thePlayer != null && mc.theWorld != null) {
            var livings = this.computeList();
            if (!livings.isEmpty()) {
                TargetHUD.Entry entry = this.entry;
                entry.handleTargetHUD2(this);
                float f = 120.0F;
                float f2 = 32.0F;
                FontRenderer fontRenderer = this.computeFontRenderer2();

                for (EntityLivingBase living : livings) {
                    if (this.myauSetting.matchesString2("Myau")) {
                        float f3 = (living.getHealth() + living.getAbsorptionAmount()) / 2.0F;
                        float f4 = (mc.thePlayer.getHealth() + mc.thePlayer.getAbsorptionAmount()) / 2.0F;
                        TargethudHandler.Entry2 entry2 = TargethudHandler.computeEntry2(
                            fontRenderer,
                            this.computeString(living),
                            String.format(Locale.US, "%.1f", f3) + "❤",
                            computeString2(f4, f3),
                            computeString(f4, f3),
                            this.headSetting.isEnabled(),
                            this.indicatorSetting.isEnabled()
                        );
                        f = Math.max(f, entry2.factor);
                        f2 = Math.max(f2, entry2.factor2);
                    } else {
                        f = Math.max(f, (float)(32 + fontRenderer.getStringWidth(this.computeString(living)) + (this.showWLSetting.isEnabled() ? 28 : 6)));
                    }
                }

                int n = Math.min(livings.size(), (int)this.gridColumnsSetting.computeDouble());
                int n2 = (livings.size() + n - 1) / n;
                float f5 = (float)this.gridGapSetting.computeDouble();
                this.factor8 = n * f + (n - 1) * f5;
                this.factor9 = n2 * f2 + (n2 - 1) * f5;
                if (mc.currentScreen instanceof GuiChat) {
                    this.process4();
                } else {
                    this.dragging = false;
                }

                ScaledResolution sr = InvalidateHelper.computeScaledResolution();
                float f6 = this.computeFloat(sr);
                float f7 = this.computeFloat2(sr);

                try {
                    this.flag = true;

                    for (int i = 0; i < livings.size(); i++) {
                        this.entityLivingBase5 = (EntityLivingBase)livings.get(i);
                        TargetHUD.Entry entry3 = this.lookupMap.computeIfAbsent(this.entityLivingBase5, living2 -> new TargetHUD.Entry());
                        entry3.handleTargetHUD(this);
                        float[] floats = computeFloatArray(i, n, f, f2, f5);
                        this.factor10 = f6 + floats[0];
                        this.factor11 = f7 + floats[1];
                        entry3.isFinishedHelper3.handleDouble2(this.lookupMap2.containsKey(this.entityLivingBase5) ? 0.0 : 1.0);
                        float f8 = this.animationsSetting.isEnabled() ? (float)entry3.isFinishedHelper3.getValue() : 1.0F;
                        GlStateManager.pushMatrix();

                        try {
                            float f9 = (this.factor10 + f / 2.0F) * (float)this.scaleSetting.computeDouble();
                            float f10 = (this.factor11 + f2 / 2.0F) * (float)this.scaleSetting.computeDouble();
                            GlStateManager.translate(f9, f10, 0.0F);
                            GlStateManager.scale(Math.max(0.001F, f8), Math.max(0.001F, f8), 1.0F);
                            GlStateManager.translate(-f9, -f10, 0.0F);
                            if (this.myauSetting.matchesString2("Myau")) {
                                this.process3();
                            } else {
                                this.process5();
                            }
                        } finally {
                            GlStateManager.popMatrix();
                            entry3.handleTargetHUD2(this);
                        }
                    }
                } finally {
                    this.flag = false;
                    this.entityLivingBase5 = null;
                    entry.handleTargetHUD(this);
                    entry.process();
                }
            }
        }
    }

    private boolean z() {
        return this.myauSetting.matchesString2("Atmosphere");
    }

    @Override
    public void onDisable() {
        this.process();
        super.onDisable();
    }

    private void handleEntityLivingBase3(EntityLivingBase living) {
        if (living != null) {
            this.linkedHashMap.put(living, System.currentTimeMillis());
            this.lookupMap2.remove(living);

            while (this.linkedHashMap.size() > 16) {
                EntityLivingBase living2 = this.linkedHashMap.keySet().iterator().next();
                this.linkedHashMap.remove(living2);
                this.lookupMap.remove(living2);
            }
        }
    }

    private void handleEntityLivingBase2(EntityLivingBase living) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            RenderHelper8.process();
            this.handleEntityLivingBase(living);
        }
    }

    private void process() {
        this.linkedHashMap.clear();
        this.lookupMap.clear();
        this.lookupMap2.clear();
        this.entityLivingBase5 = null;
        this.flag = false;
        this.targethudHandler.reset();
        this.factor8 = 120.0F;
        this.factor9 = 32.0F;
        this.entityLivingBase = null;
        this.entityLivingBase4 = null;
        this.entityLivingBase2 = null;
        this.entityLivingBase3 = null;
        this.resourceLocation = null;
        this.factor3 = this.factor4 = this.factor5 = 0.0F;
        this.dragging = false;
        this.timerUtil.reset();
        this.timerUtil2.reset();
    }

    public TargetHUD() {
        super("TargetHUD", 0, Category.VISUAL);
        this.process18();
        instance = this;
        this.offsetXSetting.setConditionalVisibility(() -> this.myauSetting.matchesString2("Myau") || this.multipleTargetsSetting.isEnabled());
        this.offsetYSetting.setConditionalVisibility(() -> this.myauSetting.matchesString2("Myau") || this.multipleTargetsSetting.isEnabled());
        this.arrayListSetting.computeModeSetting(() -> !this.myauSetting.matchesString2("Myau"));
        this.handleSettingArray(
            new Setting[]{
                this.myauSetting,
                this.arrayListSetting,
                this.arrayListSetting2,
                this.indicatorSetting,
                this.outlineSetting,
                this.rightSetting,
                this.bottomSetting,
                this.scaleSetting,
                this.offsetXSetting,
                this.offsetYSetting,
                this.multipleTargetsSetting,
                this.gridColumnsSetting,
                this.maxTargetsSetting,
                this.gridGapSetting,
                this.targetLingerSetting,
                this.backgroundSetting,
                this.headSetting,
                this.showArmorSetting,
                this.showWLSetting,
                this.animationsSetting,
                this.shadowSetting,
                this.killAuraOnlySetting,
                this.chatPreviewSetting
            }
        );
    }

    private EntityLivingBase computeEntityLivingBase() {
        if (this.flag) {
            return this.entityLivingBase5;
        }
        KillAura instance = KillAura.getInstance();
        EntityLivingBase entityLivingBase2 = KillAura.entityLivingBase2;
        if (instance != null && instance.checkState44() && this.matchesEntityLivingBase(entityLivingBase2)) {
            this.timerUtil.reset();
            this.entityLivingBase = entityLivingBase2;
            return entityLivingBase2;
        }
        if ((!this.myauSetting.matchesString2("Myau") || !this.killAuraOnlySetting.isEnabled())
            && !this.timerUtil.matchesLong10(1500L)
            && this.matchesEntityLivingBase(this.entityLivingBase)) {
            return this.entityLivingBase;
        }
        return this.chatPreviewSetting.isEnabled() && mc.currentScreen instanceof GuiChat ? mc.thePlayer : null;
    }

    private void handleFontRenderer(FontRenderer fontRenderer, String str, float f, float f2, int n) {
        if (this.shadowSetting.isEnabled()) {
            fontRenderer.drawStringWithShadow(str, f, f2, n);
        } else {
            fontRenderer.drawString(str, f, f2, n, false);
        }
    }

    private void handleScaledResolution(ScaledResolution sr, float f, float f2) {
        float f3 = (float)this.scaleSetting.computeDouble();
        if (this.rightSetting.matchesString2("Left")) {
            this.offsetXSetting.handleDouble2(f * f3);
        } else if (this.rightSetting.matchesString2("Middle")) {
            this.offsetXSetting.handleDouble2((f - sr.getScaledWidth() / f3 / 2.0F + this.factor8 / 2.0F) * f3);
        } else {
            this.offsetXSetting.handleDouble2((sr.getScaledWidth() / f3 - f - this.factor8) * -f3);
        }

        if (this.bottomSetting.matchesString2("Top")) {
            this.offsetYSetting.handleDouble2(f2 * f3);
        } else if (this.bottomSetting.matchesString2("Middle")) {
            this.offsetYSetting.handleDouble2((f2 - sr.getScaledHeight() / f3 / 2.0F + this.factor9 / 2.0F) * f3);
        } else {
            this.offsetYSetting.handleDouble2((sr.getScaledHeight() / f3 - f2 - this.factor9) * -f3);
        }
    }

    private void process3() {
        if (mc.thePlayer != null && mc.theWorld != null) {
            EntityLivingBase living = this.computeEntityLivingBase();
            if (living == null) {
                this.targethudHandler.reset();
                this.entityLivingBase2 = this.entityLivingBase3 = null;
                this.dragging = false;
            } else {
                this.entityLivingBase2 = this.entityLivingBase3 = living;
                ResourceLocation location = this.headSetting.isEnabled() ? this.computeResourceLocation(living) : null;
                FontRenderer fontRenderer = this.computeFontRenderer2();
                if (fontRenderer != null) {
                    float f = (mc.thePlayer.getHealth() + mc.thePlayer.getAbsorptionAmount()) / 2.0F;
                    float f2 = (living.getHealth() + living.getAbsorptionAmount()) / 2.0F;
                    String str = this.computeString(living);
                    String str2 = String.format(Locale.US, "%.1f", f2);
                    String str3 = living.getAbsorptionAmount() > 0.0F ? "§6" : "§c";
                    String str4 = str2;
                    String str5 = "§r§f" + str4 + str3 + "❤§r";
                    String str6 = computeString2(f, f2);
                    String str7 = computeString(f, f2);
                    TargethudHandler.Entry2 entry2 = TargethudHandler.computeEntry2(
                        fontRenderer, str, str5, str6, str7, location != null, this.indicatorSetting.isEnabled()
                    );
                    this.factor8 = entry2.factor;
                    this.factor9 = entry2.factor2;
                    if (mc.currentScreen instanceof GuiChat) {
                        this.process4();
                    } else {
                        this.dragging = false;
                    }

                    float f3 = this.targethudHandler
                        .computeFloat(living, f2, living.getMaxHealth() / 2.0F, System.currentTimeMillis(), this.animationsSetting.isEnabled());
                    Color color = this.computeColor(living);
                    Color color2 = this.arrayListSetting2.matchesString2("Default") ? GetColorHelper.computeColor(f3) : color;
                    Color color3 = GetColorHelper.computeColor(Math.max(0.0F, Math.min(1.0F, (f - f2 + 1.0F) / 2.0F)));
                    ScaledResolution sr = InvalidateHelper.computeScaledResolution();
                    this.targethudHandler
                        .handleEntry2(
                            entry2,
                            fontRenderer,
                            this.computeFloat(sr),
                            this.computeFloat2(sr),
                            (float)this.scaleSetting.computeDouble(),
                            (float)this.backgroundSetting.computeDouble() / 100.0F,
                            location,
                            str,
                            str5,
                            str6,
                            str7,
                            f3,
                            color,
                            color2,
                            color3,
                            this.indicatorSetting.isEnabled(),
                            this.outlineSetting.isEnabled(),
                            this.shadowSetting.isEnabled()
                        );
                }
            }
        }
    }

    private String computeString(EntityLivingBase living) {
        String text = living.getDisplayName().getFormattedText();
        text = pattern.matcher(text).replaceAll("");
        text = pattern2.matcher(text).replaceAll("§f");
        return pattern3.matcher(text).replaceAll("").trim();
    }

    public static float[] computeFloatArray(int n, int n2, float f, float f2, float f3) {
        return new float[]{n % n2 * (f + f3), n / n2 * (f2 + f3)};
    }

    private static float computeFloat(float f, float f2, float f3) {
        return f + (f2 - f) * f3;
    }

    public static TargetHUD getInstance() {
        return instance;
    }

    private float computeFloat2(ScaledResolution sr) {
        if (this.flag) {
            return this.factor11;
        }
        float f = (float)this.offsetYSetting.computeDouble() / (float)this.scaleSetting.computeDouble();
        if (this.bottomSetting.matchesString2("Middle")) {
            f += sr.getScaledHeight() / (float)this.scaleSetting.computeDouble() / 2.0F - this.factor9 / 2.0F;
        } else if (this.bottomSetting.matchesString2("Bottom")) {
            f = sr.getScaledHeight() / (float)this.scaleSetting.computeDouble() - this.factor9 - Math.abs(f);
        }

        return f;
    }

    private static String computeString2(float f, float f2) {
        return "§r§l" + (f2 < f ? "W" : "L") + "§r";
    }

    public static final class Entry {
        public EntityLivingBase entityLivingBase;
        public EntityLivingBase entityLivingBase2;
        public EntityLivingBase entityLivingBase3;
        public EntityLivingBase entityLivingBase4;
        public ResourceLocation resourceLocation;
        public float factor;
        public float factor2;
        public float factor3;
        public float width = 120.0F;
        public float height = 32.0F;
        public TimerUtil timerUtil = new TimerUtil();
        public TimerUtil timerUtil2 = new TimerUtil();
        public TimerUtil timerUtil3 = new TimerUtil();
        public IsFinishedHelper isFinishedHelper = new IsFinishedHelper(LinearType.EASE_OUT_SINE, 300L);
        public IsFinishedHelper isFinishedHelper2 = new IsFinishedHelper(LinearType.EASE_OUT_BACK, 600L);
        public IsDoneHelper isDoneHelper = new ImplAnimation(250, 1.0);
        public IsDoneHelper isDoneHelper2 = new ImplAnimation(250, 1.0);
        public TargethudHandler targethudHandler = new TargethudHandler();
        public final IsFinishedHelper isFinishedHelper3 = new IsFinishedHelper(LinearType.EASE_OUT_SINE, 200L);

        public void process() {
            this.entityLivingBase = this.entityLivingBase2 = this.entityLivingBase3 = this.entityLivingBase4 = null;
            this.resourceLocation = null;
            this.timerUtil = this.timerUtil2 = this.timerUtil3 = null;
            this.isFinishedHelper = this.isFinishedHelper2 = null;
            this.isDoneHelper = this.isDoneHelper2 = null;
            this.targethudHandler = null;
        }

        public void handleTargetHUD2(TargetHUD targetHUD) {
            this.entityLivingBase = targetHUD.entityLivingBase2;
            this.entityLivingBase2 = targetHUD.entityLivingBase3;
            this.entityLivingBase3 = targetHUD.entityLivingBase;
            this.entityLivingBase4 = targetHUD.entityLivingBase4;
            this.resourceLocation = targetHUD.resourceLocation;
            this.factor = targetHUD.factor3;
            this.factor2 = targetHUD.factor4;
            this.factor3 = targetHUD.factor5;
            this.width = targetHUD.factor8;
            this.height = targetHUD.factor9;
            this.timerUtil = targetHUD.timerUtil;
            this.timerUtil2 = targetHUD.timerUtil2;
            this.timerUtil3 = targetHUD.timerUtil3;
            this.isFinishedHelper = targetHUD.isFinishedHelper;
            this.isFinishedHelper2 = targetHUD.isFinishedHelper2;
            this.isDoneHelper = targetHUD.isDoneHelper;
            this.isDoneHelper2 = targetHUD.isDoneHelper2;
            this.targethudHandler = targetHUD.targethudHandler;
        }

        public Entry() {
            this.timerUtil3.setTimestamp3(0L);
        }

        public void handleTargetHUD(TargetHUD targetHUD) {
            targetHUD.entityLivingBase2 = this.entityLivingBase;
            targetHUD.entityLivingBase3 = this.entityLivingBase2;
            targetHUD.entityLivingBase = this.entityLivingBase3;
            targetHUD.entityLivingBase4 = this.entityLivingBase4;
            targetHUD.resourceLocation = this.resourceLocation;
            targetHUD.factor3 = this.factor;
            targetHUD.factor4 = this.factor2;
            targetHUD.factor5 = this.factor3;
            targetHUD.factor8 = this.width;
            targetHUD.factor9 = this.height;
            targetHUD.timerUtil = this.timerUtil;
            targetHUD.timerUtil2 = this.timerUtil2;
            targetHUD.timerUtil3 = this.timerUtil3;
            targetHUD.isFinishedHelper = this.isFinishedHelper;
            targetHUD.isFinishedHelper2 = this.isFinishedHelper2;
            targetHUD.isDoneHelper = this.isDoneHelper;
            targetHUD.isDoneHelper2 = this.isDoneHelper2;
            targetHUD.targethudHandler = this.targethudHandler;
        }
    }
}
