package org.brutality.module.impl.pit.hypixel.hud;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.EditableModulesHelper;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.hypixel.pit.ActiveEffectLinesHelper;
import org.brutality.utils.render.GlStateGuard;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.opengl.GL11;

public final class Blockhead extends Module {
    private final PitCategory pitCategory = PitCategory.MAJOR_EVENT;
    private final BooleanSetting showInfoSetting = new BooleanSetting("Show Info", this, true);
    private final BooleanSetting blockESPSetting = new BooleanSetting("Block ESP", this, true);
    private final BooleanSetting powerupESPSetting = new BooleanSetting("Powerup ESP", this, true);
    private final BooleanSetting availablePowerupsSetting = new BooleanSetting("Available Powerups", this, true);
    private final BooleanSetting claimedPowerupsSetting = new BooleanSetting("Claimed Powerups", this, true);
    private final BooleanSetting powerupLabelsSetting = new BooleanSetting("Powerup Labels", this, true);
    private final NumberSetting labelScaleSetting = new NumberSetting("Label Scale", this, 1.0, 0.5, 2.0, 0.1)
        .computeNumberSetting(this.powerupLabelsSetting::isEnabled);
    private final BooleanSetting scaleWithDistanceSetting = new BooleanSetting("Scale With Distance", this, false)
        .computeBooleanSetting(this.powerupLabelsSetting::isEnabled);
    private final BooleanSetting superHealSetting = new BooleanSetting("Super Heal", this, true);
    private final BooleanSetting diamondArmorSetting = new BooleanSetting("Diamond Armor", this, true);
    private final BooleanSetting quicktrailSetting = new BooleanSetting("Quicktrail", this, true);
    private final BooleanSetting combatBoostSetting = new BooleanSetting("Combat Boost", this, true);
    private final BooleanSetting blockFillSetting = new BooleanSetting("Block Fill", this, false);
    private final BooleanSetting blockOutlineSetting = new BooleanSetting("Block Outline", this, true);
    private final ModeSetting allColorsSetting = new ModeSetting("Block Selection", this, "Own Block", Arrays.asList("Own Block", "All Colors"));
    private final ModeSetting chromaSetting = new ModeSetting("Block Color Mode", this, "Block", Arrays.asList("Block", "Custom", "Chroma"));
    private final ColorSetting blockColorSetting = new ColorSetting("Block Color", this, 0.33F)
        .computeColorSetting(() -> this.chromaSetting.matchesString2("Custom"));
    private final NumberSetting blockESPRangeSetting = new NumberSetting("Block ESP Range", this, 24.0, 4.0, 64.0, 1.0);
    private final NumberSetting verticalRangeSetting = new NumberSetting("Vertical Range", this, 8.0, 2.0, 24.0, 1.0);
    private final NumberSetting blockESPLimitSetting = new NumberSetting("Block ESP Limit", this, 500.0, 50.0, 3000.0, 50.0);
    private final NumberSetting powerupRangeSetting = new NumberSetting("Powerup Range", this, 96.0, 8.0, 192.0, 1.0);
    private final NumberSetting hudxSetting = new NumberSetting("HUD X", this, 10.0, 0.0, 1920.0, 1.0);
    private final NumberSetting hudySetting = new NumberSetting("HUD Y", this, 85.0, 0.0, 1080.0, 1.0);
    private final ActiveEffectLinesHelper activeEffectLinesHelper = new ActiveEffectLinesHelper();
    private final Set<BlockPos> itemSet = new LinkedHashSet<>();
    private final Map<String, Blockhead.Entry> lookupMap = new LinkedHashMap<>();
    private Object object;
    private BlockPos blockPos;
    private int count;
    private int count2;
    private int count3;
    private String text = "";
    private long timestamp;

    private boolean matchesString(String str) {
        switch (str) {
            case "Super Heal":
                return this.superHealSetting.isEnabled();
            case "Diamond Armor":
                return this.diamondArmorSetting.isEnabled();
            case "Quicktrail":
                return this.quicktrailSetting.isEnabled();
            case "Combat Boost":
                return this.combatBoostSetting.isEnabled();
            default:
                return true;
        }
    }

    private void handleLong(long l) {
        List list = new ArrayList();
        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        if (scoreboard != null) {
            ScoreObjective objectiveInDisplaySlot = scoreboard.getObjectiveInDisplaySlot(1);
            if (mc.thePlayer.getTeam() instanceof ScorePlayerTeam) {
                int colorIndex = ((ScorePlayerTeam)mc.thePlayer.getTeam()).getChatFormat().getColorIndex();
                ScoreObjective objective = colorIndex < 0 ? null : scoreboard.getObjectiveInDisplaySlot(3 + colorIndex);
                if (objective != null) {
                    objectiveInDisplaySlot = objective;
                }
            }

            if (objectiveInDisplaySlot != null) {
                list.add(objectiveInDisplaySlot.getDisplayName());

                for (Score score : scoreboard.getSortedScores(objectiveInDisplaySlot)) {
                    String playerName = score.getPlayerName();
                    if (playerName != null && !playerName.startsWith("#")) {
                        list.add(ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(playerName), playerName));
                    }
                }
            }
        }

        this.activeEffectLinesHelper.handleList(list, l);
    }

    private void reset() {
        HudDragHelper.handleObject(this);
        this.activeEffectLinesHelper.reset();
        this.itemSet.clear();
        this.lookupMap.clear();
        this.blockPos = null;
        this.count = 0;
        this.object = null;
        this.text = "";
        this.timestamp = 0L;
    }

    private void handleEntry(Blockhead.Entry entry, long l, double d, double d2, double d3, int n) {
        GlStateManager.pushMatrix();

        try {
            FontRenderer fontRenderer = this.computeFontRenderer2();
            float dist = (float)mc.thePlayer.getDistance(entry.x, entry.y, entry.z);
            float f = this.scaleWithDistanceSetting.isEnabled() ? 1.0F + dist * 0.0125F : 1.0F;
            float f2 = 0.025F * (1.0F + dist * 0.035F) * (float)this.labelScaleSetting.computeDouble() * f;
            GlStateManager.translate(entry.x - d, entry.amount + 0.25 + (fontRenderer.FONT_HEIGHT + 2) * f2 - d2, entry.z - d3);
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-f2, -f2, f2);
            GlStateManager.enableTexture2D();
            String str = entry.entry.computeString(l);
            fontRenderer.drawStringWithShadow(str, -fontRenderer.getStringWidth(str) / 2.0F, 0.0F, n);
        } finally {
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void onDisable() {
        this.reset();
        super.onDisable();
    }

    private int computeInt() {
        if (this.chromaSetting.matchesString2("Custom")) {
            return this.blockColorSetting.computeColor().getRGB();
        }
        if (this.chromaSetting.matchesString2("Chroma")) {
            return Color.HSBtoRGB((float)(System.currentTimeMillis() % 5000L) / 5000.0F, 0.8F, 1.0F);
        }
        IBlockState blockState = computeIBlockState(this.activeEffectLinesHelper.text);
        return blockState == null || blockState.getBlock() != Blocks.wool && blockState.getBlock() != Blocks.stained_hardened_clay && blockState.getBlock() != Blocks.stained_glass
            ? -11141291
            : 0xFF000000 | EnumDyeColor.byMetadata(blockState.getBlock().getMetaFromState(blockState)).getMapColor().colorValue;
    }

    private boolean matchesIBlockState(IBlockState blockState, IBlockState blockState2) {
        return blockState.getBlock() == blockState2.getBlock()
            && (this.allColorsSetting.matchesString2("All Colors") || blockState.getBlock().getMetaFromState(blockState) == blockState2.getBlock().getMetaFromState(blockState2));
    }

    private static int computeInt(int n) {
        return n % 2 == 0 ? n / 2 : -(n + 1) / 2;
    }

    public Blockhead() {
        super("Blockhead", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.showInfoSetting,
                this.blockESPSetting,
                this.allColorsSetting,
                this.blockESPRangeSetting,
                this.verticalRangeSetting,
                this.blockESPLimitSetting,
                this.blockFillSetting,
                this.blockOutlineSetting,
                this.chromaSetting,
                this.blockColorSetting,
                this.powerupESPSetting,
                this.availablePowerupsSetting,
                this.claimedPowerupsSetting,
                this.powerupLabelsSetting,
                this.labelScaleSetting,
                this.scaleWithDistanceSetting,
                this.powerupRangeSetting,
                this.superHealSetting,
                this.diamondArmorSetting,
                this.quicktrailSetting,
                this.combatBoostSetting,
                this.hudxSetting,
                this.hudySetting
            }
        );
    }

    private void process3() {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glLineWidth(1.5F);
            double viewerPosX = mc.getRenderManager().viewerPosX;
            double viewerPosY = mc.getRenderManager().viewerPosY;
            double viewerPosZ = mc.getRenderManager().viewerPosZ;
            int n = this.computeInt();
            if (this.blockESPSetting.isEnabled()) {
                for (BlockPos pos : this.itemSet) {
                    handleAxisAlignedBB(
                        new AxisAlignedBB(pos, pos.add(1, 1, 1)).offset(-viewerPosX, -viewerPosY, -viewerPosZ),
                        n,
                        this.blockFillSetting.isEnabled(),
                        this.blockOutlineSetting.isEnabled()
                    );
                }
            }

            long now = System.currentTimeMillis();
            if (this.powerupESPSetting.isEnabled()) {
                for (Blockhead.Entry entry : this.lookupMap.values()) {
                    if (now - entry.entry.timestamp2 <= 1500L
                        && this.matchesString(entry.entry.text)
                        && (entry.entry.available ? this.availablePowerupsSetting.isEnabled() : this.claimedPowerupsSetting.isEnabled())) {
                        int n2 = entry.entry.available ? -11141291 : -43691;
                        handleAxisAlignedBB(
                            new AxisAlignedBB(
                                entry.x - 0.45 - viewerPosX,
                                entry.y - viewerPosY,
                                entry.z - 0.45 - viewerPosZ,
                                entry.x + 0.45 - viewerPosX,
                                entry.y + 0.9 - viewerPosY,
                                entry.z + 0.45 - viewerPosZ
                            ),
                            n2,
                            true,
                            true
                        );
                        if (this.powerupLabelsSetting.isEnabled()) {
                            this.handleEntry(entry, now, viewerPosX, viewerPosY, viewerPosZ, n2);
                        }
                    }
                }
            }
        }
    }

    private void scanPowerups(long l) {
        HashMap<String, List<EntityArmorStand>> map = new HashMap<>();
        double d = this.powerupRangeSetting.computeDouble() * this.powerupRangeSetting.computeDouble();

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityArmorStand && !entity.isDead && entity.getDistanceSqToEntity(mc.thePlayer) <= d) {
                EntityArmorStand entity2 = (EntityArmorStand)entity;
                String str = Math.round(entity2.posX * 2.0) + ":" + Math.round(entity2.posZ * 2.0);
                map.computeIfAbsent(str, str2 -> new ArrayList<>()).add(entity2);
            }
        }

        for (Map.Entry entry : ((Iterable<Map.Entry>)(Object)(map.entrySet()))) {
            for (EntityArmorStand entity3 : ((Iterable<EntityArmorStand>)(Object)((List)entry.getValue()))) {
                String str3 = ActiveEffectLinesHelper.computeString(entity3.getCustomNameTag());
                if (str3 != null) {
                    boolean flag = false;
                    EntityArmorStand entity4 = null;
                    EntityArmorStand entity5 = null;
                    double d2 = entity3.posY + entity3.height + 0.5;

                    for (EntityArmorStand entity6 : ((Iterable<EntityArmorStand>)(Object)((List)entry.getValue()))) {
                        if (!(Math.abs(entity6.posY - entity3.posY) > 2.5)) {
                            String str4 = ActiveEffectLinesHelper.computeString2(entity6.getCustomNameTag());
                            if (str4.equalsIgnoreCase("POWERUP")) {
                                flag = true;
                                d2 = Math.max(d2, entity6.posY + entity6.height + 0.5);
                            }

                            if (ActiveEffectLinesHelper.matchesString(str4) || ActiveEffectLinesHelper.computeInt(str4) >= 0) {
                                entity4 = entity6;
                                d2 = Math.max(d2, entity6.posY + entity6.height + 0.5);
                            }

                            if (entity6.getEquipmentInSlot(4) != null && (entity5 == null || entity6.posY < entity5.posY)) {
                                entity5 = entity6;
                            }
                        }
                    }

                    if (flag && entity4 != null) {
                        String str5 = (String)entry.getKey() + ":" + Math.round(entity3.posY);
                        Blockhead.Entry entry2 = this.lookupMap.get(str5);
                        if (entry2 == null || !entry2.entry.text.equals(str3)) {
                            entry2 = new Blockhead.Entry(str3);
                            this.lookupMap.put(str5, entry2);
                        }

                        entry2.entry.handleString(entity4.getCustomNameTag(), l);
                        entry2.x = entity3.posX;
                        entry2.z = entity3.posZ;
                        entry2.y = entity5 == null ? entity4.posY + entity4.height - 0.6 : entity5.posY + entity5.getEyeHeight() - 0.45;
                        entry2.amount = d2;
                    }
                }
            }
        }

        this.lookupMap.entrySet().removeIf(entry3 -> l - entry3.getValue().entry.timestamp2 > 1500L);
    }

    public static IBlockState computeIBlockState(String str) {
        String str2 = ActiveEffectLinesHelper.computeString2(str).toLowerCase(Locale.ROOT).replace("stained ", "").replace("hardened ", "");
        var block = str2.endsWith("wool")
            ? Blocks.wool
            : (str2.endsWith("clay") ? Blocks.stained_hardened_clay : (str2.endsWith("glass") ? Blocks.stained_glass : null));
        if (block != null) {
            for (EnumDyeColor enumDyeColor : EnumDyeColor.values()) {
                String str3 = enumDyeColor.getName().replace('_', ' ');
                if (str2.startsWith(str3 + " ") || str2.startsWith(enumDyeColor.getUnlocalizedName().toLowerCase(Locale.ROOT) + " ")) {
                    return ((Block)block).getStateFromMeta(enumDyeColor.getMetadata());
                }
            }
        }

        if (str2.equals("stone")) {
            return Blocks.stone.getDefaultState();
        }
        if (str2.equals("cobblestone")) {
            return Blocks.cobblestone.getDefaultState();
        }
        return str2.equals("iron ore") ? Blocks.iron_ore.getDefaultState() : null;
    }

    private static void handleAxisAlignedBB(AxisAlignedBB box, int n, boolean flag, boolean flag2) {
        GlStateManager.disableTexture2D();
        if (flag) {
            GlStateManager.color(((0xFF | ~(n >> 16)) - ~(n >> 16)) / 255.0F, (n >> 8 & 0xFF) / 255.0F, ((0xFF | ~n) - ~n) / 255.0F, 0.14F);
            ResetColorHelper.handleAxisAlignedBB4(box);
        }

        if (flag2) {
            GlStateManager.color((n >> 16 & 0xFF) / 255.0F, (n >> 8 & 0xFF) / 255.0F, (n & 0xFF) / 255.0F, 0.9F);
            RenderGlobal.func_181561_a(box);
        }
    }

    private void process() {
        FontRenderer fontRenderer = this.computeFontRenderer2();
        int n = Math.max(12, fontRenderer.FONT_HEIGHT + 2);
        String[] parts = new String[]{
            "§f§lBlockhead Info:", "§fRemaining: §b" + this.activeEffectLinesHelper.computeString(System.currentTimeMillis()), null, null, null, null
        };
        String str = this.activeEffectLinesHelper.count >= 10 ? "§a" : (this.activeEffectLinesHelper.count > 0 ? "§6" : "§7");
        int count = this.activeEffectLinesHelper.count;
        String str2 = str;
        parts[2] = "§fKills: " + str2 + count + "§f/10";
        parts[3] = "§fBlock: §a" + this.activeEffectLinesHelper.text;
        str = (String)(this.activeEffectLinesHelper.count2 < 0 ? "--" : this.activeEffectLinesHelper.count2);
        String str3 = this.activeEffectLinesHelper.text2.isEmpty() ? "" : " §7(" + this.activeEffectLinesHelper.text2 + ")";
        Object obj = str;
        parts[4] = "§fBlocks: §b" + obj + str3;
        parts[5] = "§fPlace: §e" + (this.activeEffectLinesHelper.count3 > 0 ? "#" + this.activeEffectLinesHelper.count3 : "--");
        String[] parts2 = parts;
        ArrayList<String> list = new ArrayList<>(Arrays.asList(parts2));
        list.addAll(this.activeEffectLinesHelper.activeEffectLines(System.currentTimeMillis()));
        int n2 = 0;

        for (String str4 : list) {
            n2 = Math.max(n2, fontRenderer.getStringWidth(str4));
        }

        HudDragHelper.handleObject(this, this.hudxSetting, this.hudySetting, n2, list.size() * n);
        int n3 = (int)this.hudySetting.computeDouble();
        int n4 = (int)this.hudxSetting.computeDouble();

        for (String str5 : ((Iterable<String>)(Object)(list))) {
            fontRenderer.drawStringWithShadow(str5, n4, n3, -1);
            n3 += n;
        }
    }

    private void process4() {
        IBlockState blockState = computeIBlockState(this.activeEffectLinesHelper.text);
        if (blockState == null) {
            this.itemSet.clear();
            this.blockPos = null;
        } else {
            int n = (int)this.blockESPRangeSetting.computeDouble();
            int n2 = (int)this.verticalRangeSetting.computeDouble();
            String str = this.activeEffectLinesHelper.text + ":" + this.allColorsSetting.getText();
            BlockPos pos = mc.thePlayer.getPosition();
            if (this.blockPos == null || this.blockPos.distanceSq(pos) > 16.0 || n != this.count2 || n2 != this.count3 || !this.text.equals(str)) {
                this.itemSet.clear();
                this.blockPos = pos;
                this.count = 0;
                this.count2 = n;
                this.count3 = n2;
                this.text = str;
            }

            double d = n * n;
            int n3 = (int)this.blockESPLimitSetting.computeDouble();
            this.itemSet
                .removeIf(
                    pos2 -> !mc.theWorld.isBlockLoaded(pos2)
                        || mc.thePlayer.getDistanceSq(pos2.getX() + 0.5, pos2.getY() + 0.5, pos2.getZ() + 0.5) > d
                        || !this.matchesIBlockState(mc.theWorld.getBlockState(pos2), blockState)
                );

            while (this.itemSet.size() > n3) {
                this.itemSet.remove(this.itemSet.iterator().next());
            }

            int n4 = n * 2 + 1;
            int n5 = n2 * 2 + 1;
            int n6 = n4 * n4 * n5;

            for (int i = 0; i < Math.min(2048, n6); i++) {
                int n7 = this.count++ % n6;
                BlockPos pos3 = this.blockPos.add(computeInt(n7 % n4), computeInt(n7 / (n4 * n4)), computeInt(n7 / n4 % n4));
                if (this.itemSet.size() < n3
                    && !(mc.thePlayer.getDistanceSq(pos3.getX() + 0.5, pos3.getY() + 0.5, pos3.getZ() + 0.5) > d)
                    && mc.theWorld.isBlockLoaded(pos3)
                    && this.matchesIBlockState(mc.theWorld.getBlockState(pos3), blockState)) {
                    this.itemSet.add(pos3);
                }
            }

            if (this.count >= n6) {
                this.count %= n6;
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent4) {
                this.reset();
            } else if (mc.theWorld != null && mc.thePlayer != null) {
                if (this.object != mc.theWorld) {
                    this.reset();
                    this.object = mc.theWorld;
                }

                if (event instanceof ChatEvent chatEvent) {
                    if (chatEvent.byteVal != 2 && chatEvent.message != null) {
                        this.activeEffectLinesHelper.handleString(chatEvent.message.getUnformattedText(), System.currentTimeMillis());
                    }
                } else if (event instanceof EventUpdate && ((EventUpdate)event).getState() != EventState.POST) {
                    long now = System.currentTimeMillis();
                    this.handleLong(now);
                    if (!this.activeEffectLinesHelper.active) {
                        this.itemSet.clear();
                        this.lookupMap.clear();
                        this.blockPos = null;
                        return;
                    }

                    if (this.blockESPSetting.isEnabled()) {
                        this.process4();
                    } else {
                        this.itemSet.clear();
                        this.blockPos = null;
                    }

                    if (this.powerupESPSetting.isEnabled() && now - this.timestamp >= 250L) {
                        this.timestamp = now;
                        this.scanPowerups(now);
                    }

                    if (!this.powerupESPSetting.isEnabled()) {
                        this.lookupMap.clear();
                    }
                } else if ((this.activeEffectLinesHelper.active || EditableModulesHelper.isActive())
                    && event instanceof EventRender2D
                    && this.showInfoSetting.isEnabled()) {
                    this.process();
                } else if (this.activeEffectLinesHelper.active && event instanceof EventRender3D) {
                    this.process3();
                }
            } else {
                this.reset();
            }
        }
    }

    public static final class Entry {
        public final ActiveEffectLinesHelper.Entry entry;
        public double x;
        public double y;
        public double z;
        public double amount;

        public Entry(String str) {
            this.entry = new ActiveEffectLinesHelper.Entry(str);
        }
    }
}
