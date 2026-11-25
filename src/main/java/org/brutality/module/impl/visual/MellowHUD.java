package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.StringUtils;
import org.brutality.event.Event;
import org.brutality.event.impl.render.SetShaderTypeEvent;
import org.brutality.mellow.Mellow;
import org.brutality.mellow.data.GetPlayerNameHandler;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.render.ArrayListHelper;
import org.brutality.utils.render.RenderHelper2;
import org.lwjgl.opengl.GL11;

public final class MellowHUD extends Module {
    private final BooleanSetting dockBelowTabGUISetting = new BooleanSetting("Dock Below TabGUI", this, true);
    private final BooleanSetting blurSetting = new BooleanSetting("Blur", this, true);
    private final BooleanSetting glowSetting = new BooleanSetting("Glow", this, false);
    private final BooleanSetting distanceSetting = new BooleanSetting("Distance", this, true);
    private final BooleanSetting healthSetting = new BooleanSetting("Health", this, true);
    private final BooleanSetting starsSetting = new BooleanSetting("Stars", this, false);
    private final BooleanSetting fkdrSetting = new BooleanSetting("FKDR", this, true);
    private final BooleanSetting winstreakSetting = new BooleanSetting("Winstreak", this, true);
    private final BooleanSetting wlrSetting = new BooleanSetting("WLR", this, true);
    private final BooleanSetting teamSetting = new BooleanSetting("Team", this, true);
    private final BooleanSetting kdSetting = new BooleanSetting("KD", this, true);
    private final BooleanSetting killsSetting = new BooleanSetting("Kills", this, true);
    private final BooleanSetting finalsSetting = new BooleanSetting("Finals", this, false);
    private final BooleanSetting bedsSetting = new BooleanSetting("Beds", this, false);
    private final ModeSetting nameSetting = new ModeSetting("Sorting", this, "Distance", Arrays.asList("Distance", "FKDR", "Name"));
    private final ModeSetting arrayListSetting = new ModeSetting("Color", this, "Theme", Arrays.asList("Theme", "ArrayList"));
    private final NumberSetting maxRowsSetting = new NumberSetting("Max Rows", this, 8.0, 1.0, 20.0, 1.0);
    private final NumberSetting scaleSetting = new NumberSetting("Scale", this, 0.65, 0.45, 1.25, 0.05);
    private final NumberSetting xSetting = new NumberSetting("X", this, 6.0, 0.0, 1920.0, 1.0);
    private final NumberSetting ySetting = new NumberSetting("Y", this, 190.0, 0.0, 1080.0, 1.0);
    private List<MellowHUD.Entry2> entries = Collections.emptyList();
    private long timestamp;
    private Object object;
    private static final int count = 15;
    private static final long timestamp2 = 250L;

    @Override
    public void process2() {
        this.entries = Collections.emptyList();
        this.timestamp = 0L;
        this.object = null;
    }

    public static double computeDouble(String str) {
        if (str == null) {
            return -1.0;
        }
        try {
            double d = Double.parseDouble(StringUtils.stripControlCodes(str).replace(",", ""));
            return Double.isFinite(d) ? d : -1.0;
        } catch (NumberFormatException e) {
            return -1.0;
        }
    }

    private MellowHUD.Entry computeEntry() {
        this.refresh();
        float f = (float)this.scaleSetting.computeDouble();
        RenderHelper2 renderHelper2 = new RenderHelper2(this, 18.0F, f);
        var list = this.computeList();
        int[] ints = new int[list.size()];
        int n = Math.max(3, Math.round(6.0F * f));
        int n2 = Math.max(6, Math.round(10.0F * f));
        int n3 = n2 + Math.max(2, Math.round(4.0F * f));

        for (int i = 0; i < ints.length; i++) {
            ints[i] = i == 0
                ? Math.max(Math.round(134.0F * f), n3 + renderHelper2.computeInt("Player") + n)
                : Math.max(Math.round(36.0F * f), renderHelper2.computeInt((String)list.get(i)) + n * 2);

            for (int j = 0; j < Math.min(this.entries.size(), (int)this.maxRowsSetting.computeDouble()); j++) {
                int n4 = renderHelper2.computeInt(this.computeString(this.entries.get(j), (String)list.get(i)));
                ints[i] = Math.max(ints[i], Math.min(Math.round((i == 0 ? 160 : 76) * f), n4 + (i == 0 ? n3 + n : n * 2)));
            }
        }

        ScaledResolution sr = InvalidateHelper.computeScaledResolution(mc);
        int n5 = (int)this.xSetting.computeDouble();
        int n6 = (int)this.ySetting.computeDouble();
        if (this.dockBelowTabGUISetting.isEnabled() && TabGUI.instance != null && TabGUI.instance.checkState44()) {
            n5 = TabGUI.instance.computeInt3();
            n6 = TabGUI.instance.computeInt2() + 6;
        }

        int n7 = n * 2;

        for (int n8 : ints) {
            n7 += n8;
        }

        n7 = Math.min(n7, Math.max(120, sr.getScaledWidth() - 12));
        int n9 = Math.max(Math.round(15.0F * f), (int)Math.ceil(renderHelper2.computeFloat()) + Math.max(3, Math.round(4.0F * f)));
        int n10 = n9 + Math.max(2, Math.round(3.0F * f));
        int n11 = Math.min(
            this.entries.size(),
            Math.min((int)this.maxRowsSetting.computeDouble(), Math.max(0, (sr.getScaledHeight() - n6 - n10 - n9 - 24) / n9))
        );
        int n12 = n10 + n9 + Math.max(1, n11) * n9 + Math.max(2, Math.round(3.0F * f));
        n5 = Math.max(0, Math.min(n5, sr.getScaledWidth() - n7));
        n6 = Math.max(0, Math.min(n6, sr.getScaledHeight() - n12));
        return new MellowHUD.Entry(n5, n6, n7, n12, n11, ints, list, f, n9, n10, n9, n, n2, n3);
    }

    private String computeString(MellowHUD.Entry2 entry2, String str) {
        GetPlayerNameHandler getPlayerNameHandler = entry2.getPlayerNameHandler;
        switch (str) {
            case "Player":
                return entry2.text;
            case "Dist":
                return Double.isFinite(entry2.amount) ? (int)entry2.amount + "m" : "--";
            case "HP":
                return entry2.factor >= 0.0F ? (int)entry2.factor + "%" : "--";
            case "Team":
                return entry2.text2;
            case "K/D":
                return getPlayerNameHandler == null ? "--" : computeString(getPlayerNameHandler.getText9());
            case "Kills":
                return getPlayerNameHandler == null ? "--" : computeString(getPlayerNameHandler.getText10());
            case "Stars":
                return getPlayerNameHandler == null ? "--" : computeString(getPlayerNameHandler.getText());
            case "FKDR":
                return getPlayerNameHandler == null ? "--" : computeString(getPlayerNameHandler.getText2());
            case "WS":
                return getPlayerNameHandler == null ? "--" : computeString(getPlayerNameHandler.getText3());
            case "W/L":
                return getPlayerNameHandler == null ? "--" : computeString(getPlayerNameHandler.getText4());
            case "Finals":
                return getPlayerNameHandler == null ? "--" : computeString(getPlayerNameHandler.getText8());
            case "Beds":
                return getPlayerNameHandler == null ? "--" : computeString(getPlayerNameHandler.getText7());
            default:
                return "--";
        }
    }

    @Override
    public void onRender() {
        if (this.checkState44() && mc.thePlayer != null && this.computeFontRenderer2() != null) {
            MellowHUD.Entry entry = this.computeEntry();
            if (!this.dockBelowTabGUISetting.isEnabled() || TabGUI.instance == null || !TabGUI.instance.checkState44()) {
                HudDragHelper.handleObject(this, this.xSetting, this.ySetting, entry.count3, entry.count4);
                entry = this.computeEntry();
            }

            float factor = entry.factor;
            RenderHelper2 renderHelper2 = new RenderHelper2(this, 18.0F, factor);
            Color color = ArrayListHelper.computeColor(this, this.arrayListSetting.getText());
            ArrayListHelper.handleFloat(entry.count, entry.count2, entry.count3, entry.count4, 4.0F * factor);
            ArrayListHelper.handleFloat(
                entry.count + 4.0F * factor,
                entry.count2 + factor,
                entry.count3 - 8.0F * factor,
                factor,
                0.0F,
                color,
                ArrayListHelper.computeColor2(this, this.arrayListSetting.getText())
            );
            renderHelper2.handleString("Players", entry.count + entry.count9, entry.count2 + 2, entry.count7 - 2, color.getRGB());
            String str = entry.count5 + " / " + this.entries.size();
            renderHelper2.handleString(str, entry.count + entry.count3 - entry.count9 - renderHelper2.computeFloat(str), entry.count2 + 2, entry.count7 - 2, -4866351);

            for (int i = 0; i < entry.entries.size() && entry.computeInt(i) + entry.intValueArray[i] <= entry.count + entry.count3 - entry.count9; i++) {
                String str2 = entry.entries.get(i);
                renderHelper2.handleString(str2, entry.computeFloat(renderHelper2, i, str2), entry.count2 + entry.count7, entry.count8, -4866351);
            }

            if (entry.count5 == 0) {
                renderHelper2.handleString(
                    this.checkState() ? "Waiting for lobby players..." : "Enable Mellow to load stats",
                    entry.count + entry.count9,
                    entry.computeInt(),
                    entry.count6,
                    -5326644
                );
            } else {
                for (int j = 0; j < entry.count5; j++) {
                    MellowHUD.Entry2 entry2 = this.entries.get(j);
                    int n = entry.computeInt() + j * entry.count6;
                    if ((j & 1) == 0) {
                        Gui.drawRect(entry.count + 2, n, entry.count + entry.count3 - 2, n + entry.count6, 385875967);
                    }

                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    mc.getTextureManager().bindTexture(entry2.networkPlayerInfo.getLocationSkin());
                    int n2 = n + (entry.count6 - entry.count10) / 2;
                    Gui.drawScaledCustomSizeModalRect(entry.count + entry.count9, n2, 8.0F, 8.0F, 8, 8, entry.count10, entry.count10, 64.0F, 64.0F);

                    for (int k = 0;
                        k < entry.entries.size() && entry.computeInt(k) + entry.intValueArray[k] <= entry.count + entry.count3 - entry.count9;
                        k++
                    ) {
                        String str3 = entry.entries.get(k);
                        String str4 = this.computeString(entry2, str3);
                        int n3 = k == 0 ? -986376 : -3156510;
                        if (str3.equals("HP") && entry2.factor >= 0.0F) {
                            n3 = entry2.factor < 35.0F ? -36470 : (entry2.factor < 65.0F ? -10120 : -7479365);
                        }

                        str4 = renderHelper2.computeString(str4, entry.intValueArray[k] - (k == 0 ? entry.count11 + entry.count9 : entry.count9 * 2));
                        renderHelper2.handleString(str4, entry.computeFloat(renderHelper2, k, str4), n, entry.count6, n3);
                    }
                }
            }
        }
    }

    private List<String> computeList() {
        java.util.ArrayList list = new java.util.ArrayList();
        list.add("Player");
        if (this.distanceSetting.isEnabled()) {
            list.add("Dist");
        }

        if (this.healthSetting.isEnabled()) {
            list.add("HP");
        }

        if (this.teamSetting.isEnabled()) {
            list.add("Team");
        }

        if (this.winstreakSetting.isEnabled()) {
            list.add("WS");
        }

        if (this.fkdrSetting.isEnabled()) {
            list.add("FKDR");
        }

        if (this.wlrSetting.isEnabled()) {
            list.add("W/L");
        }

        if (this.kdSetting.isEnabled()) {
            list.add("K/D");
        }

        if (this.killsSetting.isEnabled()) {
            list.add("Kills");
        }

        if (this.starsSetting.isEnabled()) {
            list.add("Stars");
        }

        if (this.finalsSetting.isEnabled()) {
            list.add("Finals");
        }

        if (this.bedsSetting.isEnabled()) {
            list.add("Beds");
        }

        return list;
    }

    public MellowHUD() {
        super("MellowHUD", 0, Category.VISUAL);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.dockBelowTabGUISetting,
                this.arrayListSetting,
                this.scaleSetting,
                this.blurSetting,
                this.glowSetting,
                this.maxRowsSetting,
                this.nameSetting,
                this.distanceSetting,
                this.healthSetting,
                this.teamSetting,
                this.winstreakSetting,
                this.fkdrSetting,
                this.wlrSetting,
                this.kdSetting,
                this.killsSetting,
                this.starsSetting,
                this.finalsSetting,
                this.bedsSetting,
                this.xSetting,
                this.ySetting
            }
        );
    }

    private void refresh() {
        NetHandlerPlayClient netHandler = mc.getNetHandler();
        long now = System.currentTimeMillis();
        if (this.object != netHandler || now - this.timestamp >= timestamp2) {
            this.timestamp = now;
            this.object = netHandler;
            if (netHandler != null && mc.theWorld != null && this.checkState()) {
                HashMap map = new HashMap();

                for (Map.Entry entry : Mellow.lookupMap.entrySet()) {
                    if (entry.getKey() != null && entry.getValue() != null) {
                        map.put(((String)entry.getKey()).toLowerCase(Locale.ROOT), (GetPlayerNameHandler)entry.getValue());
                    }
                }

                java.util.ArrayList list = new java.util.ArrayList();

                for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                    if (playerInfo.getGameProfile() != null && playerInfo.getGameProfile().getName() != null) {
                        EntityPlayer playerEntityByUUID = mc.theWorld.getPlayerEntityByUUID(playerInfo.getGameProfile().getId());
                        list.add(
                            new MellowHUD.Entry2(
                                playerInfo, (GetPlayerNameHandler)map.get(playerInfo.getGameProfile().getName().toLowerCase(Locale.ROOT)), playerEntityByUUID, mc.thePlayer
                            )
                        );
                    }
                }

                Comparator<MellowHUD.Entry2> comparator = this.nameSetting.matchesString2("FKDR")
                    ? Comparator.<MellowHUD.Entry2>comparingDouble(
                            entry2 -> computeDouble(entry2.getPlayerNameHandler == null ? null : entry2.getPlayerNameHandler.getText2())
                        )
                        .reversed()
                    : (
                        this.nameSetting.matchesString2("Name")
                            ? Comparator.comparing(entry22 -> entry22.text, String.CASE_INSENSITIVE_ORDER)
                            : Comparator.comparingDouble(entry23 -> entry23.amount)
                    );
                list.sort(comparator.thenComparing(entry24 -> entry24.text, String.CASE_INSENSITIVE_ORDER));
                this.entries = list;
            } else {
                this.entries = Collections.emptyList();
            }
        }
    }

    private static String computeString(String str) {
        return str != null && !str.isEmpty() ? str.substring(0, Math.min(40, str.length())) : "--";
    }

    @Override
    public boolean onBlurMode(SetShaderTypeEvent.BlurMode blurMode) {
        return blurMode == SetShaderTypeEvent.BlurMode.BLUR
            ? this.blurSetting.isEnabled()
            : blurMode == SetShaderTypeEvent.BlurMode.GLOW && this.glowSetting.isEnabled();
    }

    public static String computeString(ScorePlayerTeam team) {
        if (team == null) {
            return "--";
        }
        String str = StringUtils.stripControlCodes(team.getColorPrefix()).trim();
        if (str.matches("[RGBYWPAS]")) {
            return team.getColorPrefix();
        }
        String str2 = team.getRegisteredName().toLowerCase(Locale.ROOT);

        for (String str3 : new String[]{"red", "blue", "green", "yellow", "aqua", "white", "pink", "gray", "grey"}) {
            if (str2.equals(str3) || str2.endsWith("_" + str3)) {
                return !str3.equals("gray") && !str3.equals("grey") ? Character.toUpperCase(str3.charAt(0)) + str3.substring(1) : "Gray";
            }
        }

        return "--";
    }

    private boolean checkState() {
        return Mellow.getInstance() != null && Mellow.getInstance().checkState44();
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof SetShaderTypeEvent && this.checkState44() && mc.thePlayer != null && this.computeFontRenderer2() != null) {
            MellowHUD.Entry entry = this.computeEntry();
            SetShaderTypeEvent setShaderTypeEvent = (SetShaderTypeEvent)event;
            if (setShaderTypeEvent.getShaderType() == SetShaderTypeEvent.BlurMode.BLUR && this.blurSetting.isEnabled()) {
                ArrayListHelper.handleFloat(entry.count, entry.count2, entry.count3, entry.count4, 4.0F * entry.factor, Color.WHITE);
            } else if (setShaderTypeEvent.getShaderType() == SetShaderTypeEvent.BlurMode.GLOW && this.glowSetting.isEnabled()) {
                ArrayListHelper.handleFloat(
                    entry.count + 4.0F * entry.factor,
                    entry.count2,
                    entry.count3 - 8.0F * entry.factor,
                    entry.factor,
                    0.0F,
                    ArrayListHelper.computeColor(this, this.arrayListSetting.getText())
                );
            }
        }
    }

    @Override
    public void onDisable() {
        this.process2();
        HudDragHelper.handleObject(this);
    }

    public static final class Entry {
        public final int count;
        public final int count2;
        public final int count3;
        public final int count4;
        public final int count5;
        public final int count6;
        public final int count7;
        public final int count8;
        public final int count9;
        public final int count10;
        public final int count11;
        public final float factor;
        public final int[] intValueArray;
        public final List<String> entries;

        public int computeInt() {
            return this.count2 + this.count7 + this.count8;
        }

        public int computeInt(int n) {
            int n2 = this.count + this.count9;

            for (int i = 0; i < n; i++) {
                n2 += this.intValueArray[i];
            }

            return n2;
        }

        public Entry(
            int count,
            int n,
            int n2,
            int n3,
            int n4,
            int[] intValueArray,
            List<String> entries,
            float factor,
            int n5,
            int n6,
            int n7,
            int n8,
            int n9,
            int n10
        ) {
            this.count = count;
            this.count2 = n;
            this.count3 = n2;
            this.count4 = n3;
            this.count5 = n4;
            this.intValueArray = intValueArray;
            this.entries = entries;
            this.factor = factor;
            this.count6 = n5;
            this.count7 = n6;
            this.count8 = n7;
            this.count9 = n8;
            this.count10 = n9;
            this.count11 = n10;
        }

        public float computeFloat(RenderHelper2 renderHelper2, int n, String str) {
            return renderHelper2.computeFloat(this.computeInt(n) + (n == 0 ? this.count11 : (this.intValueArray[n] - renderHelper2.computeFloat(str)) / 2.0F));
        }
    }

    public static final class Entry2 {
        public final NetworkPlayerInfo networkPlayerInfo;
        public final String text;
        public final GetPlayerNameHandler getPlayerNameHandler;
        public final double amount;
        public final float factor;
        public final String text2;

        public Entry2(NetworkPlayerInfo networkPlayerInfo, GetPlayerNameHandler getPlayerNameHandler, EntityPlayer player, EntityPlayer player2) {
            this.networkPlayerInfo = networkPlayerInfo;
            this.text2 = MellowHUD.computeString(MellowHUD.mc.theWorld != null && MellowHUD.mc.theWorld.getScoreboard() != null ? networkPlayerInfo.getPlayerTeam() : null);
            this.text = networkPlayerInfo.getGameProfile().getName();
            this.getPlayerNameHandler = getPlayerNameHandler;
            this.amount = player != null && player2 != null ? player.getDistanceToEntity(player2) : Double.POSITIVE_INFINITY;
            this.factor = player == null ? -1.0F : (player.getHealth() + player.getAbsorptionAmount()) / Math.max(1.0F, player.getMaxHealth()) * 100.0F;
        }
    }
}
