package org.brutality.module.impl.pit.hypixel.hud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.event.impl.pit.PitEvent14;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.player.GetEntityEvent;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.pit.hypixel.utility.VenomedESP;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.WHelper;
import org.brutality.utils.hypixel.HypixelHelper;
import org.brutality.utils.hypixel.pit.DOUBLEHelper;
import org.brutality.utils.hypixel.pit.DiedWhileVenomedHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.hypixel.pit.PitHelper2;
import org.brutality.utils.performance.EntityCache;
import org.brutality.utils.player.SomberHelper;
import org.brutality.utils.render.GlStateGuard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class VenomedTimer extends Module {
    public static final long timestamp = 3500L;
    private final PitCategory pitCategory = PitCategory.HUD;
    private static VenomedTimer instance;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 10.0, 0.0, 1200.0, 1.0);
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 20.0, 0.0, 1200.0, 1.0);
    private final HypixelHelper hypixelHelper = new HypixelHelper();
    private EntityPlayerSP entityPlayerSP;
    private Object object;
    private final Map<String, Integer> lookupMap = new HashMap<>();
    private final BooleanSetting detectForOthersSetting = new BooleanSetting("Detect for Others", this, true);
    private final BooleanSetting hitCountSetting = new BooleanSetting("Hit Count", this, false);
    private final ModeSetting totalHitsSetting = new ModeSetting("Hit Count Mode", this, "Hits Until Venom", Arrays.asList("Hits Until Venom", "Total Hits"));
    private final BooleanSetting showOnlyMyVenomsSetting = new BooleanSetting("Show Only My Venoms", this, false);
    private final BooleanSetting showLabelSetting = new BooleanSetting("Show Label", this, true);
    private final ModeSetting modeSetting = new ModeSetting("Suffix Mode", this, "#", Arrays.asList("#", "#.#"));
    private final BooleanSetting hitSound2Setting = new BooleanSetting("2 Hit Sound", this, true);
    private final BooleanSetting showHitCountSetting = new BooleanSetting("Show Hit Count", this, true);
    private final BooleanSetting showComboTimerSetting = new BooleanSetting("Show Combo Timer", this, false);
    private final BooleanSetting announceVenomSetting = new BooleanSetting("Announce Venom", this, false);
    private final BooleanSetting guildChatSetting = new BooleanSetting("Guild Chat", this, true);
    private final BooleanSetting partyChatSetting = new BooleanSetting("Party Chat", this, false);
    private final BooleanSetting announceHPSetting = new BooleanSetting("Announce HP", this, false);
    private final NumberSetting announceHPBelowSetting = new NumberSetting("Announce HP Below", this, 10.0, 1.0, 20.0, 0.5);
    private final BooleanSetting venomedKOSOnlySetting = new BooleanSetting("Venomed KOS Only", this, false);
    private final BooleanSetting showHuntersSetting = new BooleanSetting("Show Hunters", this, true);
    private final BooleanSetting showVictimsSetting = new BooleanSetting("Show Victims", this, true);
    private final Map<String, Long> lookupMap2 = new HashMap<>();
    private final Map<String, Long> lookupMap3 = new HashMap<>();
    private final Map<String, Long> lookupMap4 = new HashMap<>();
    private final Set<String> itemSet = new HashSet<>();
    private final Map<String, List<VenomedTimer.Entry>> lookupMap5 = new HashMap<>();
    private final Map<String, Long> lookupMap6 = new HashMap<>();
    private final Map<Integer, Long> lookupMap7 = new HashMap<>();
    private EntityLivingBase entityLivingBase;
    private int count;
    private int count2 = 0;
    private String text;
    private long timestamp2;
    private int count3;
    private final Set<String> itemSet2 = new HashSet<>();
    private String text2;
    private int count4;
    private long timestamp3;
    private static final List<String> entries = Arrays.asList(
        "Venom",
        "Misery",
        "Spite",
        "Needless Suffering",
        "Grim Reaper",
        "Nostalgia",
        "Hedge Fund",
        "Heartripper",
        "Sanguisuge",
        "Lycanthropy",
        "Mind Assault",
        "Golden Handcuffs"
    );

    private void handleString(String str) {
        if (this.text2 != null && System.currentTimeMillis() - this.timestamp3 <= 2500L) {
            if (str.toLowerCase().contains("cannot say the same message")) {
                this.count4++;
                this.handleString(this.text2, this.count4);
                this.text2 = null;
            }
        }
    }

    private void handleString3(String str) {
        String str2 = DiedWhileVenomedHelper.computeString(str);
        if (str2 != null && (this.mc.thePlayer == null || !str2.equalsIgnoreCase(this.mc.thePlayer.getName()))) {
            if (this.text == null || !str2.equalsIgnoreCase(this.text)) {
                EntityPlayer player = this.computeEntityPlayer(str2);
                if (player != null
                    && player.isEntityAlive()
                    && !this.matchesEntityPlayer2(player)
                    && !matchesEntityPlayer(player)
                    && !FreshHelper.matchesWorld3(this.mc.theWorld, player)) {
                    this.lookupMap2.put(player.getName(), System.currentTimeMillis() + 12000L);
                    this.itemSet.remove(player.getName());
                }
            }
        }
    }

    public static void handleEntityPlayer(EntityPlayer player) {
        VenomedTimer venomedTimer = instance;
        if (venomedTimer != null
            && (venomedTimer.checkState44() || VenomedESP.isActive())
            && venomedTimer.mc.thePlayer != null
            && venomedTimer.mc.theWorld != null
            && player != venomedTimer.mc.thePlayer) {
            venomedTimer.process4();
            if (venomedTimer.matchesEntityPlayer3(venomedTimer.mc.thePlayer)) {
                int n = venomedTimer.recordHit(venomedTimer.mc.thePlayer.getName(), player.getName(), player);
                if (n > 0) {
                    long now = System.currentTimeMillis();
                    venomedTimer.hypixelHelper.handleString(player.getName(), now);
                    venomedTimer.text = player.getName();
                    venomedTimer.timestamp2 = now;
                    venomedTimer.process();
                }
            }
        }
    }

    private boolean matchesString2(String str) {
        Long val = this.lookupMap2.get(str);
        if (val == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        return now < val;
    }

    private EntityPlayer computeEntityPlayer(String str) {
        if (this.mc.theWorld != null && str != null) {
            for (EntityPlayer playerEntity : this.mc.theWorld.playerEntities) {
                if (str.equalsIgnoreCase(playerEntity.getName())) {
                    return playerEntity;
                }
            }

            return null;
        }
        return null;
    }

    public int computeInt3(String str) {
        Long val = this.lookupMap2.get(str);
        if (val == null) {
            return 0;
        }
        long now = System.currentTimeMillis();
        return (int)Math.max(0L, (val - now) / 1000L);
    }

    @Override
    public void onRender() {
        EntityPlayerSP player = this.mc.thePlayer;
        if (player != null) {
            float f = (float)this.xPosSetting.computeDouble();
            float f2 = (float)this.yPosSetting.computeDouble();
            long now = System.currentTimeMillis();
            this.lookupMap2.entrySet().removeIf(entry -> entry.getValue() < now);
            this.cleanupAnnouncementState(now);
            this.cleanupOldHits(now);
            boolean potionActive = player.isPotionActive(Potion.poison);
            boolean flag = this.mc.currentScreen instanceof GuiChat;
            int n = (int)this.lookupMap2.keySet().stream().filter(this::matchesString3).count();
            boolean flag2 = n > 0;
            boolean flag3 = this.computeInt() > 0;
            if (!potionActive && !flag && !flag2 && !flag3) {
                HudDragHelper.handleObject(this);
            } else {
                FontRenderer fontRenderer = computeFontRenderer(VenomedTimer.class);
                var str = this.showLabelSetting.isEnabled() ? "Venomed: " : "";
                String str2;
                if (potionActive) {
                    PotionEffect activePotionEffect = player.getActivePotionEffect(Potion.poison);
                    double d = activePotionEffect.getDuration() / 20.0;
                    str2 = EnumChatFormatting.WHITE + str + EnumChatFormatting.DARK_PURPLE + this.computeString(d);
                } else {
                    str2 = EnumChatFormatting.WHITE + str + EnumChatFormatting.DARK_PURPLE + this.computeString(0.0);
                }

                int FONT_HEIGHT = computeFontRenderer(VenomedTimer.class).FONT_HEIGHT;
                if (flag2) {
                    FONT_HEIGHT += n * (computeFontRenderer(VenomedTimer.class).FONT_HEIGHT + 2) + 5;
                }

                if (flag) {
                    this.handleFloat(f, f2, str2, FONT_HEIGHT);
                    f = (float)this.xPosSetting.computeDouble();
                    f2 = (float)this.yPosSetting.computeDouble();
                }

                if (potionActive || flag) {
                    fontRenderer.drawStringWithShadow(str2, f, f2, 16777215);
                }

                int n2 = this.computeInt();
                if (n2 > 0) {
                    if (this.showHitCountSetting.isEnabled()) {
                        EnumChatFormatting DARK_GREEN = EnumChatFormatting.DARK_GREEN;
                        String str3 = EnumChatFormatting.GREEN + "Hits: " + DARK_GREEN + n2 + "/3";
                        fontRenderer.drawStringWithShadow(str3, f, f2 + fontRenderer.FONT_HEIGHT + 2.0F, 16777215);
                        f2 += fontRenderer.FONT_HEIGHT + 2;
                    }

                    if (this.showComboTimerSetting.isEnabled()) {
                        long l = this.computeLong();
                        EnumChatFormatting GREEN = EnumChatFormatting.GREEN;
                        EnumChatFormatting RED = EnumChatFormatting.RED;
                        String str4 = this.computeString(l / 1000.0);
                        EnumChatFormatting enumChatFormatting = RED;
                        String str5 = GREEN + "Combo: " + enumChatFormatting + str4;
                        fontRenderer.drawStringWithShadow(str5, f, f2 + fontRenderer.FONT_HEIGHT + 2.0F, 16777215);
                        f2 += fontRenderer.FONT_HEIGHT + 2;
                    }
                }

                if ((this.showHuntersSetting.isEnabled() || this.showVictimsSetting.isEnabled()) && (flag2 || flag)) {
                    float f3 = f2 + fontRenderer.FONT_HEIGHT + 5.0F;
                    ArrayList<String> list = new ArrayList<>();
                    ArrayList<String> list2 = new ArrayList<>();

                    for (Map.Entry entry2 : this.lookupMap2.entrySet()) {
                        var key = (String)entry2.getKey();
                        long val = (Long)entry2.getValue();
                        double d2 = Math.max(0.0, (val - now) / 1000.0);
                        if (!(d2 <= 0.0) && this.matchesString3((String)key)) {
                            EnumChatFormatting GOLD = EnumChatFormatting.GOLD;
                            EnumChatFormatting DARK_PURPLE = EnumChatFormatting.DARK_PURPLE;
                            String str6 = this.computeString(d2);
                            EnumChatFormatting enumChatFormatting2 = DARK_PURPLE;
                            String str7 = GOLD + key + ": " + enumChatFormatting2 + str6;
                            if (this.itemSet.contains(key)) {
                                list.add(str7);
                            } else {
                                list2.add(str7);
                            }
                        }
                    }

                    if (list.isEmpty() && list2.isEmpty()) {
                        if (flag) {
                            if (this.showHuntersSetting.isEnabled()) {
                                fontRenderer.drawStringWithShadow(EnumChatFormatting.RED + "Hunters: " + EnumChatFormatting.GRAY + "None", f, f3, 16777215);
                                f3 += fontRenderer.FONT_HEIGHT + 2;
                            }

                            if (this.showVictimsSetting.isEnabled()) {
                                fontRenderer.drawStringWithShadow(
                                    EnumChatFormatting.DARK_PURPLE + "Victims: " + EnumChatFormatting.GRAY + "None", f, f3, 16777215
                                );
                            }
                        }
                    } else {
                        if (!list.isEmpty()) {
                            fontRenderer.drawStringWithShadow(EnumChatFormatting.RED + "Hunters:", f, f3, 16733525);
                            f3 += fontRenderer.FONT_HEIGHT + 2;

                            for (String str8 : list) {
                                fontRenderer.drawStringWithShadow(str8, f, f3, 16777215);
                                f3 += fontRenderer.FONT_HEIGHT + 2;
                            }
                        }

                        if (!list2.isEmpty()) {
                            fontRenderer.drawStringWithShadow(EnumChatFormatting.DARK_PURPLE + "Victims:", f, f3, 11141290);
                            float f4 = f3 + (fontRenderer.FONT_HEIGHT + 2);

                            for (String str9 : list2) {
                                fontRenderer.drawStringWithShadow(str9, f, f4, 16777215);
                                f4 += fontRenderer.FONT_HEIGHT + 2;
                            }
                        }
                    }
                }
            }
        }
    }

    @Generated
    public static VenomedTimer getInstance() {
        return instance;
    }

    private void handleInt2(int n, int n2) {
        long now = System.currentTimeMillis();
        String str = this.hypixelHelper.computeString(n, n2, now);
        if (str == null && PitHelper2.matchesInt(n, n2)) {
            EntityPlayer player = this.computeEntityPlayer();
            if (player != null) {
                this.hypixelHelper.handleString(player.getName(), now);
                str = this.hypixelHelper.computeString(n2, n2, now);
            }
        }

        this.count3 = n2;
        if (this.text != null && now - this.timestamp2 > 1000L) {
            this.text = null;
        }

        if (str != null && this.matchesEntityPlayer3(this.mc.thePlayer)) {
            EntityPlayer player2 = this.computeEntityPlayer(str);
            if (player2 != null
                && player2.isEntityAlive()
                && !this.matchesEntityPlayer2(player2)
                && !matchesEntityPlayer(player2)
                && !FreshHelper.matchesWorld3(this.mc.theWorld, player2)
                && !FreshHelper.matchesWorld3(this.mc.theWorld, this.mc.thePlayer)) {
                this.text = null;
                this.lookupMap2.put(str, now + 12000L);
                this.itemSet.remove(str);
                this.lookupMap2.put(this.mc.thePlayer.getName(), now + n2 * 50L);
                this.itemSet.add(this.mc.thePlayer.getName());
                this.itemSet2.add(str);
                this.lookupMap5.remove(this.mc.thePlayer.getName());
                if (this.checkState44()) {
                    this.handleString2(str);
                }
            }
        }
    }

    public VenomedTimer() {
        super("Venomed Timer", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.xPosSetting,
                this.yPosSetting,
                this.detectForOthersSetting,
                this.hitCountSetting,
                this.totalHitsSetting,
                this.showOnlyMyVenomsSetting,
                this.showLabelSetting,
                this.modeSetting,
                this.hitSound2Setting,
                this.showHitCountSetting,
                this.showComboTimerSetting,
                this.announceVenomSetting,
                this.guildChatSetting,
                this.partyChatSetting,
                this.announceHPSetting,
                this.announceHPBelowSetting,
                this.venomedKOSOnlySetting,
                this.showHuntersSetting,
                this.showVictimsSetting
            }
        );
        instance = this;
    }

    public int computeInt(String str) {
        return this.computeInt(str, System.currentTimeMillis());
    }

    private void handleVenomedCombatMessage(String str) {
        String str2 = DOUBLEHelper.computeString2(str);
        boolean flag = false;
        if (str2 == null) {
            str2 = DOUBLEHelper.computeString(str);
            flag = str2 != null;
        }

        if (str2 != null) {
            boolean flag2 = this.matchesString6(str2);
            String str3 = str2;
            this.lookupMap2.keySet().removeIf(str4 -> str4.equalsIgnoreCase(str3));
            this.clearPlayerState(str2);
            if (this.checkState44()
                && matchesString5(str2)
                && flag2
                && this.announceVenomSetting.isEnabled()
                && (this.guildChatSetting.isEnabled() || this.partyChatSetting.isEnabled())
                && this.mc.thePlayer != null) {
                long now = System.currentTimeMillis();
                String str5 = (flag ? "assist:" : "kill:") + str2.toLowerCase(Locale.ROOT);
                Long val = this.lookupMap4.put(str5, now);
                if (val == null || now - val >= 2500L) {
                    String str6 = flag ? " died while venomed! I assisted." : " died while venomed! I got the kill.";
                    String str7 = str2 + str6;
                    if (this.guildChatSetting.isEnabled()) {
                        this.mc.thePlayer.sendChatMessage("/gc " + str7);
                    }

                    if (this.partyChatSetting.isEnabled()) {
                        this.mc.thePlayer.sendChatMessage("/pc " + str7);
                    }
                }
            }
        }
    }

    public EntityLivingBase computeEntityLivingBase() {
        if (this.mc.objectMouseOver != null
            && this.mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY
            && this.mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
            this.entityLivingBase = (EntityLivingBase)this.mc.objectMouseOver.entityHit;
            this.count = 0;
            return (EntityLivingBase)this.mc.objectMouseOver.entityHit;
        }
        if (this.entityLivingBase != null) {
            if (++this.count <= 10) {
                return this.entityLivingBase;
            }

            this.entityLivingBase = null;
        }

        return null;
    }

    public boolean matchesString(String str) {
        return this.itemSet.contains(str);
    }

    private void process() {
        PotionEffect activePotionEffect = this.mc.thePlayer.getActivePotionEffect(Potion.poison);
        int n = activePotionEffect == null ? 0 : activePotionEffect.getDuration();
        this.handleInt2(this.count3, n);
    }

    public long computeLong() {
        if (this.mc.thePlayer == null) {
            return 0L;
        }
        var list = this.lookupMap5.get(this.mc.thePlayer.getName());
        if (list != null && !list.isEmpty()) {
            long timestamp = ((VenomedTimer.Entry)list.get(list.size() - 1)).timestamp;
            long l = 3500L - (System.currentTimeMillis() - timestamp);
            return Math.max(0L, l);
        }
        return 0L;
    }

    private String computeString(double d) {
        return this.modeSetting.matchesString2("#")
            ? String.format(Locale.ROOT, "%.0f", Math.max(0.0, d))
            : String.format(Locale.ROOT, "%.1f", Math.max(0.0, d));
    }

    private int recordHit(String str, String str2, EntityPlayer player) {
        if (player != null && player.isEntityAlive() && !matchesEntityPlayer(player) && !this.matchesEntityPlayer2(player)) {
            long now = System.currentTimeMillis();
            String str3 = str + "-" + str2;
            Long val = this.lookupMap6.get(str3);
            if (val != null && now - val < 100L) {
                return 0;
            }
            this.lookupMap6.put(str3, now);
            if (this.mc.theWorld != null && FreshHelper.matchesWorld3(this.mc.theWorld, player)) {
                return 0;
            }
            if (this.mc.theWorld != null) {
                for (EntityPlayer player2 : EntityCache.getEntries()) {
                    if (player2.getName().equals(str)) {
                        if (FreshHelper.matchesWorld3(this.mc.theWorld, player2)) {
                            return 0;
                        }
                        break;
                    }
                }
            }

            if (this.matchesString2(str2)) {
                return 0;
            }
            if (this.matchesEntityPlayer2(player)) {
                return 0;
            }
            var list = this.lookupMap5.computeIfAbsent(str, str4 -> new ArrayList<>());
            if (!list.isEmpty() && now - ((VenomedTimer.Entry)list.get(list.size() - 1)).timestamp > 3500L) {
                list.clear();
            }

            list.add(new VenomedTimer.Entry(str2, now));
            int size = list.size();
            if (size >= 3) {
                String text = ((VenomedTimer.Entry)list.get(list.size() - 1)).text;
                if (str.equals(this.mc.thePlayer.getName())) {
                    if (this.text == null) {
                        this.text = text;
                        this.timestamp2 = now;
                    }
                } else {
                    this.lookupMap2.put(text, now + 12000L);
                    this.itemSet.remove(text);
                    this.lookupMap2.put(str, now + 24000L);
                    this.itemSet.add(str);
                }

                list.clear();
            }

            return size;
        }
        return 0;
    }

    private void handleDouble(double d, double d2, double d3, String str) {
        double d4 = d - this.mc.getRenderManager().viewerPosX;
        double d5 = d2 - this.mc.getRenderManager().viewerPosY;
        double d6 = d3 - this.mc.getRenderManager().viewerPosZ;
        double dist = Math.sqrt(d4 * d4 + d5 * d5 + d6 * d6);
        float f = Math.max(0.1F, (float)(dist * 0.015F));

        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(d4, d5, d6);
            GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(this.mc.getRenderManager().playerViewX * (this.mc.gameSettings.thirdPersonView == 2 ? -1 : 1), 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-f, -f, f);
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.disableCull();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            computeFontRenderer(VenomedTimer.class).drawStringWithShadow(str, -computeFontRenderer(VenomedTimer.class).getStringWidth(str) / 2.0F, 0.0F, -1);
            GlStateManager.popMatrix();
        }
    }

    private void handleFloat(float f, float f2, String str, int n) {
        int[] ints = HudDragHelper.computeIntArray();
        int n2 = ints[0];
        int n3 = ints[1];
        if (Mouse.isButtonDown(0)) {
            if (!HudDragHelper.matchesObject(this)) {
                int textWidth = computeFontRenderer(VenomedTimer.class).getStringWidth(str);
                if (HudDragHelper.matchesInt(n2, n3, f, f2, textWidth, n)) {
                    HudDragHelper.matchesObject(this, n2, n3, (int)f, (int)f2);
                }
            }
        } else {
            HudDragHelper.handleObject(this);
        }

        if (HudDragHelper.matchesObject(this)) {
            int[] ints2 = HudDragHelper.computeIntArray(this);
            if (ints2 != null) {
                this.xPosSetting.handleDouble2(ints2[0]);
                this.yPosSetting.handleDouble2(ints2[1]);
            }
        }
    }

    private boolean matchesEntityPlayer3(EntityPlayer player) {
        if (player != null && player.inventory != null) {
            ItemStack stack = player.inventory.armorInventory[1];
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                NBTTagCompound tagCompound = stack.getTagCompound();
                if (tagCompound != null) {
                    NBTTagCompound nbt = tagCompound.hasKey("ExtraAttributes", 10) ? tagCompound.getCompoundTag("ExtraAttributes") : tagCompound;
                    NBTTagList tagList = nbt.getTagList("CustomEnchants", 10);

                    for (int i = 0; i < tagList.tagCount(); i++) {
                        NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
                        if ("venom".equalsIgnoreCase(compoundTagAt.getString("Key")) && compoundTagAt.getInteger("Level") > 0) {
                            return true;
                        }
                    }
                }

                if (tagCompound != null && tagCompound.hasKey("display", 10)) {
                    NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
                    if (compoundTag.hasKey("Lore", 9)) {
                        NBTTagList tagList2 = compoundTag.getTagList("Lore", 8);

                        for (int j = 0; j < tagList2.tagCount(); j++) {
                            String stringTagAt = tagList2.getStringTagAt(j);
                            if (stringTagAt.contains("Venom")) {
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        }
        return false;
    }

    private void handleString2(String str) {
        if (this.announceVenomSetting.isEnabled()) {
            this.handleString5(str);
        }
    }

    private void process4() {
        if (this.entityPlayerSP != this.mc.thePlayer || this.object != this.mc.theWorld) {
            this.process2();
            this.entityPlayerSP = this.mc.thePlayer;
            this.object = this.mc.theWorld;
            PotionEffect activePotionEffect = this.mc.thePlayer.getActivePotionEffect(Potion.poison);
            this.count3 = activePotionEffect == null ? 0 : activePotionEffect.getDuration();
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() || VenomedESP.isActive()) {
            if (event instanceof RenderEvent4) {
                this.process2();
            } else {
                if (event instanceof EventPacketSend && this.mc.thePlayer != null && this.mc.theWorld != null) {
                    Packet packet = ((EventPacketSend)event).getPacket();
                    if (packet instanceof C02PacketUseEntity && ((C02PacketUseEntity)packet).getAction() == C02PacketUseEntity.Action.ATTACK) {
                        Entity entityFromWorld = ((C02PacketUseEntity)packet).getEntityFromWorld(this.mc.theWorld);
                        if (entityFromWorld instanceof EntityPlayer && entityFromWorld != this.mc.thePlayer) {
                            this.lookupMap7.put(entityFromWorld.getEntityId(), System.currentTimeMillis());
                        }
                    }
                }

                if (event instanceof PitEvent14 && this.mc.thePlayer != null) {
                    String name = this.mc.thePlayer.getName();
                    this.lookupMap2.remove(name);
                    this.itemSet.remove(name);
                    this.hypixelHelper.clear();
                    this.lookupMap.clear();
                    this.entityLivingBase = null;
                    this.count = 0;
                    this.text = null;
                    this.count3 = 0;
                    this.itemSet2.remove(name);
                    this.lookupMap5.remove(name);

                    for (List list : this.lookupMap5.values()) {
                        list.removeIf(obj -> ((VenomedTimer.Entry)obj).text.equals(name));
                    }

                    this.lookupMap6.keySet().removeIf(str -> str.startsWith(name + "-") || str.endsWith("-" + name));
                    this.count2 = 0;
                } else {
                    if (event instanceof EventTick && ((EventTick)event).getState() == EventState.PRE) {
                        if (this.mc.thePlayer == null || this.mc.theWorld == null) {
                            return;
                        }

                        this.process4();
                        this.cleanupAnnouncementState(System.currentTimeMillis());
                        if (!this.matchesEntityPlayer3(this.mc.thePlayer)) {
                            this.hypixelHelper.clear();
                        }

                        if (this.detectForOthersSetting.isEnabled()) {
                            for (EntityPlayer player : EntityCache.getEntries()) {
                                if (player != this.mc.thePlayer && this.matchesEntityPlayer3(player)) {
                                    EntityLivingBase living = this.computeEntityLivingBase(player);
                                    if (living != null
                                        && living instanceof EntityPlayer player2
                                        && player2.hurtTime >= 9
                                        && player2.hurtTime > this.lookupMap.getOrDefault(player2.getName(), 0)) {
                                        this.recordHit(player.getName(), player2.getName(), player2);
                                    }
                                }
                            }
                        }

                        for (EntityPlayer playerEntity : this.mc.theWorld.playerEntities) {
                            this.lookupMap.put(playerEntity.getName(), playerEntity.hurtTime);
                        }

                        this.process();
                        int n = this.computeInt();
                        if (this.checkState44() && this.hitSound2Setting.isEnabled() && n == 2 && this.count2 != 2) {
                            this.mc.thePlayer.playSound("random.orb", 1.0F, 1.0F);
                        }

                        this.count2 = n;
                        this.process3();
                    } else if (event instanceof GetEntityEvent getEntityEvent) {
                        if (getEntityEvent.getEntity() instanceof EntityPlayer) {
                            EntityPlayer entity = (EntityPlayer)getEntityEvent.getEntity();
                            String name2 = entity.getName();
                            String str2 = name2.toLowerCase(Locale.ROOT);
                            if (this.matchesString6(name2)) {
                                this.lookupMap3.put(str2, System.currentTimeMillis() + 5000L);
                            }

                            if (entity == this.mc.thePlayer) {
                                this.hypixelHelper.clear();
                            }

                            this.itemSet2.remove(name2);
                            if (name2.equals(this.text)) {
                                this.text = null;
                            }

                            this.lookupMap2.remove(name2);
                            this.itemSet.remove(name2);

                            for (List list2 : this.lookupMap5.values()) {
                                list2.removeIf(obj2 -> ((VenomedTimer.Entry)obj2).text.equals(name2));
                            }

                            this.lookupMap5.remove(name2);
                            this.lookupMap6.entrySet().removeIf(entry -> entry.getKey().startsWith(name2 + "-") || entry.getKey().endsWith("-" + name2));
                        }
                    } else if (event instanceof EventRender3D) {
                        if (this.checkState44() && this.hitCountSetting.isEnabled()) {
                            this.process5();
                        }
                    } else if (event instanceof ChatEvent chatEvent) {
                        if (chatEvent.message == null) {
                            return;
                        }

                        String text = chatEvent.message.getUnformattedText();
                        this.handleString(text);
                        this.handleString3(text);
                        this.handleVenomedCombatMessage(text);
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.process2();
    }

    private EntityLivingBase computeEntityLivingBase(EntityPlayer player) {
        if (this.mc.theWorld == null) {
            return null;
        }
        EntityPlayer player2 = null;
        double d = 4.0;

        for (EntityPlayer player3 : EntityCache.getEntries()) {
            if (player3 != player && player3.isEntityAlive() && player3.hurtTime >= 9 && !this.matchesEntityPlayer2(player3) && !matchesEntityPlayer(player3)) {
                double dist = player.getDistanceToEntity(player3);
                if (dist <= d) {
                    d = dist;
                    player2 = player3;
                }
            }
        }

        return player2;
    }

    private void process5() {
        if (this.mc.theWorld != null && this.mc.thePlayer != null) {
            long now = System.currentTimeMillis();
            this.cleanupOldHits(now);

            for (EntityPlayer player : EntityCache.getEntries()) {
                if (player != this.mc.thePlayer) {
                    String name = player.getName();
                    if (!this.matchesString2(name) && !this.matchesEntityPlayer2(player)) {
                        int n = this.computeInt(name, now);
                        if (n != 0) {
                            int n2;
                            if (this.totalHitsSetting.matchesString2("Total Hits")) {
                                n2 = n;
                            } else {
                                n2 = 3 - n;
                            }

                            if (n2 > 0) {
                                double d = player.lastTickPosX + (player.posX - player.lastTickPosX) * this.mc.timer.renderPartialTicks;
                                double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * this.mc.timer.renderPartialTicks + player.height * 0.5;
                                double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * this.mc.timer.renderPartialTicks;
                                this.handleDouble(d, d2, d3, String.valueOf(n2));
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean matchesString(String str, String str2) {
        return this.mc.thePlayer != null && str2.equals(this.mc.thePlayer.getName()) && this.itemSet2.contains(str);
    }

    public int computeInt2(String str) {
        if (str == null) {
            return 0;
        }
        this.cleanupOldHits(System.currentTimeMillis());
        List list = this.lookupMap5.get(str);
        return list != null && !list.isEmpty() ? list.size() : 0;
    }

    private boolean matchesString3(String str) {
        if (this.itemSet.contains(str) ? this.showHuntersSetting.isEnabled() : this.showVictimsSetting.isEnabled()) {
            return this.venomedKOSOnlySetting.isEnabled() && !WHelper.matchesString(str)
                ? false
                : !this.showOnlyMyVenomsSetting.isEnabled() || this.mc.thePlayer != null && this.matchesString(str, this.mc.thePlayer.getName());
        }
        return false;
    }

    public static void handleInt(int n, int n2) {
        VenomedTimer venomedTimer = instance;
        if (venomedTimer != null && (venomedTimer.checkState44() || VenomedESP.isActive()) && venomedTimer.mc.thePlayer != null && venomedTimer.mc.theWorld != null) {
            venomedTimer.process4();
            venomedTimer.handleInt2(n, n2);
        }
    }

    public static void handleEntityPlayer3(EntityPlayer player) {
        VenomedTimer venomedTimer = instance;
        if (venomedTimer != null
            && (venomedTimer.checkState44() || VenomedESP.isActive())
            && venomedTimer.mc.thePlayer != null
            && venomedTimer.mc.theWorld != null
            && player != venomedTimer.mc.thePlayer) {
            venomedTimer.process4();
            Long val = venomedTimer.lookupMap7.remove(player.getEntityId());
            if (val != null && System.currentTimeMillis() - val <= 750L && venomedTimer.matchesEntityPlayer3(venomedTimer.mc.thePlayer)) {
                int n = venomedTimer.recordHit(venomedTimer.mc.thePlayer.getName(), player.getName(), player);
                if (n > 0) {
                    long now = System.currentTimeMillis();
                    venomedTimer.hypixelHelper.handleString(player.getName(), now);
                    venomedTimer.text = player.getName();
                    venomedTimer.timestamp2 = now;
                    venomedTimer.process();
                }

                venomedTimer.lookupMap.put(player.getName(), player.hurtTime);
            } else {
                venomedTimer.handleEntityPlayer2(player);
                venomedTimer.lookupMap.put(player.getName(), player.hurtTime);
            }
        }
    }

    private void cleanupAnnouncementState(long l) {
        this.lookupMap2.entrySet().removeIf(entry -> entry.getValue() <= l);
        this.itemSet.retainAll(this.lookupMap2.keySet());
        this.itemSet2.retainAll(this.lookupMap2.keySet());
        this.lookupMap3.entrySet().removeIf(entry2 -> entry2.getValue() <= l);
        this.lookupMap4.entrySet().removeIf(entry3 -> l - entry3.getValue() > 5000L);
        this.lookupMap7.entrySet().removeIf(entry4 -> l - entry4.getValue() > 1000L);
    }

    public static void handleString4(String str) {
        VenomedTimer venomedTimer = instance;
        if (venomedTimer != null && str != null && venomedTimer.mc.thePlayer != null && venomedTimer.mc.theWorld != null && (venomedTimer.checkState44() || VenomedESP.isActive())) {
            venomedTimer.process4();
            EntityPlayer player = venomedTimer.computeEntityPlayer(str);
            if (player != null
                && player != venomedTimer.mc.thePlayer
                && player.isEntityAlive()
                && venomedTimer.matchesEntityPlayer3(venomedTimer.mc.thePlayer)
                && !venomedTimer.matchesEntityPlayer2(player)
                && !matchesEntityPlayer(player)) {
                long now = System.currentTimeMillis();
                venomedTimer.hypixelHelper.handleString(player.getName(), now);
                venomedTimer.text = player.getName();
                venomedTimer.timestamp2 = now;
                venomedTimer.process();
            }
        }
    }

    private boolean matchesString6(String str) {
        if (str == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        Long val = this.lookupMap2.get(str);
        if (val != null && val > now) {
            return true;
        }
        String str2 = str.toLowerCase(Locale.ROOT);
        Long val2 = this.lookupMap3.get(str2);
        return val2 != null && val2 > now;
    }

    public int computeInt() {
        return this.mc.thePlayer == null ? 0 : this.computeInt2(this.mc.thePlayer.getName());
    }

    private static boolean matchesEntityPlayer(EntityPlayer player) {
        return player.isInWater() || player.isInLava();
    }

    private void handleString(String str, int n) {
        if (this.mc.thePlayer != null && this.checkState44() && this.announceVenomSetting.isEnabled() && this.matchesString2(str) && matchesString5(str)) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < n; i++) {
                sb.append('/');
            }

            String str2 = "";
            if (this.announceHPSetting.isEnabled() && this.mc.theWorld != null) {
                EntityPlayer playerEntityByName = this.mc.theWorld.getPlayerEntityByName(str);
                if (playerEntityByName != null && playerEntityByName.getHealth() <= this.announceHPBelowSetting.computeDouble()) {
                    str2 = " (" + Math.round(playerEntityByName.getHealth()) + "hp)";
                }
            }

            EntityPlayer player = this.computeEntityPlayer(str);
            String str3;
            if (player == null) {
                str3 = "";
            } else {
                str3 = StringUtils.stripControlCodes(FreshHelper.computeString(player, this.mc.theWorld));
                int n2 = (int)Math.floor(player.posX);
                int n3 = (int)Math.floor(player.posY);
                int n4 = (int)Math.floor(player.posZ);
                int n5 = n3;
                int n6 = n2;
                String str4 = str3;
                str3 = " at " + str4 + " (X: " + n6 + " Y: " + n5 + " Z: " + n4 + ")";
            }

            String str5 = str3;
            if (this.guildChatSetting.isEnabled()) {
                this.mc.thePlayer.sendChatMessage("/gc " + str + " - is venomed!" + str2 + str5 + sb);
            }

            if (this.partyChatSetting.isEnabled()) {
                this.mc.thePlayer.sendChatMessage("/pc " + str + " - is venomed!" + str2 + str5 + sb);
            }
        }
    }

    private boolean matchesEntityPlayer2(EntityPlayer player) {
        if (SomberHelper.matchesEntityPlayer3(player)) {
            return true;
        }
        ItemStack stack = player.inventory.armorInventory[1];
        if (stack != null && stack.getItem() instanceof ItemArmor) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("display", 10)) {
                NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
                if (compoundTag.hasKey("Lore", 9)) {
                    NBTTagList tagList = compoundTag.getTagList("Lore", 8);

                    for (int i = 0; i < tagList.tagCount(); i++) {
                        String stringTagAt = tagList.getStringTagAt(i);

                        for (String str : entries) {
                            if (stringTagAt.contains(str)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private void process3() {
        if (this.mc.theWorld != null) {
            HashSet<String> set = new HashSet<>(this.lookupMap2.keySet());
            set.addAll(this.lookupMap5.keySet());

            for (List list : this.lookupMap5.values()) {
                for (VenomedTimer.Entry entry : ((Iterable<VenomedTimer.Entry>)(Object)(list))) {
                    set.add(entry.text);
                }
            }

            for (String str : set) {
                EntityPlayer player = this.computeEntityPlayer(str);
                boolean flag = player == null && this.mc.getNetHandler() != null && this.mc.getNetHandler().getPlayerInfo(str) == null;
                if (flag || player != null && (!player.isEntityAlive() || FreshHelper.matchesWorld2(this.mc.theWorld, player))) {
                    this.clearPlayerState(str);
                }
            }
        }
    }

    public BooleanSetting getDetectForOthersSetting() {
        return this.detectForOthersSetting;
    }

    private EntityPlayer computeEntityPlayer() {
        if (this.mc.objectMouseOver != null
            && this.mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY
            && this.mc.objectMouseOver.entityHit instanceof EntityPlayer player
            && player.hurtTime > 0) {
            return player;
        }
        EntityLivingBase entityLivingBase2 = KillAura.entityLivingBase2;
        return entityLivingBase2 instanceof EntityPlayer && entityLivingBase2.hurtTime > 0 ? (EntityPlayer)entityLivingBase2 : null;
    }

    private void clearPlayerState(String str) {
        this.lookupMap2.remove(str);
        this.itemSet.remove(str);
        this.itemSet2.remove(str);
        this.lookupMap5.remove(str);
        this.lookupMap.remove(str);

        for (List list : this.lookupMap5.values()) {
            list.removeIf(obj -> ((VenomedTimer.Entry)obj).text.equals(str));
        }

        this.lookupMap6.keySet().removeIf(str2 -> str2.startsWith(str + "-") || str2.endsWith("-" + str));
        if (str.equals(this.text)) {
            this.text = null;
        }
    }

    private static boolean matchesString5(String str) {
        return WHelper.matchesString(str) && !DefaultHelper.matchesString(str);
    }

    public Set<String> computeSet(String str) {
        HashSet set = new HashSet();
        if (str == null) {
            return set;
        }
        this.cleanupOldHits(System.currentTimeMillis());
        var list = this.lookupMap5.get(str);
        if (list != null) {
            for (VenomedTimer.Entry entry : list) {
                set.add(entry.text);
            }
        }

        return set;
    }

    private void cleanupOldHits(long l) {
        this.lookupMap5.entrySet().removeIf(entry -> {
            var val = entry.getValue();
            return val.isEmpty() || l - ((VenomedTimer.Entry)val.get(val.size() - 1)).timestamp > 3500L;
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.process2();
    }

    private void handleString5(String str) {
        if (matchesString5(str)) {
            if (this.mc.thePlayer != null) {
                this.text2 = str;
                this.count4 = 0;
                this.timestamp3 = System.currentTimeMillis();
                this.handleString(str, 0);
            }
        }
    }

    public boolean matchesString4(String str) {
        Long val = this.lookupMap2.get(str);
        return val != null && val > System.currentTimeMillis();
    }

    private void handleEntityPlayer2(EntityPlayer player) {
        if (this.detectForOthersSetting.isEnabled() && !matchesEntityPlayer(player) && !this.matchesEntityPlayer2(player)) {
            for (EntityPlayer player2 : EntityCache.getEntries()) {
                if (player2 != this.mc.thePlayer && this.matchesEntityPlayer3(player2) && this.computeEntityLivingBase(player2) == player) {
                    this.recordHit(player2.getName(), player.getName(), player);
                }
            }
        }
    }

    @Override
    public void process2() {
        this.lookupMap2.clear();
        this.itemSet.clear();
        this.lookupMap5.clear();
        this.lookupMap6.clear();
        this.hypixelHelper.clear();
        this.lookupMap.clear();
        this.itemSet2.clear();
        this.lookupMap3.clear();
        this.lookupMap4.clear();
        this.text = null;
        this.count3 = 0;
        this.lookupMap7.clear();
        this.entityLivingBase = null;
        this.count = 0;
        this.count2 = 0;
        this.text2 = null;
        this.entityPlayerSP = null;
        this.object = null;
    }

    private int computeInt(String str, long l) {
        long l2 = 0L;

        for (List list : this.lookupMap5.values()) {
            for (VenomedTimer.Entry entry : ((Iterable<VenomedTimer.Entry>)(Object)(list))) {
                if (entry.text.equals(str) && entry.timestamp > l2) {
                    l2 = entry.timestamp;
                }
            }
        }

        if (l2 != 0L && l - l2 <= 3500L) {
            int n = 0;

            for (List list2 : this.lookupMap5.values()) {
                for (VenomedTimer.Entry entry2 : ((Iterable<VenomedTimer.Entry>)(Object)(list2))) {
                    if (entry2.text.equals(str) && l2 - entry2.timestamp <= 3500L) {
                        n++;
                    }
                }
            }

            return n;
        }
        return 0;
    }

    public String computeString(String str) {
        Long val = this.lookupMap2.get(str);
        if (val == null) {
            return "0";
        }
        double d = Math.max(0.0, (val - System.currentTimeMillis()) / 1000.0);
        return this.modeSetting.matchesString2("#") ? String.format(Locale.ROOT, "%.0f", d) : String.format(Locale.ROOT, "%.1f", d);
    }

    public static class Entry {
        public final String text;
        public final long timestamp;

        public Entry(String text, long timestamp) {
            this.text = text;
            this.timestamp = timestamp;
        }
    }
}
