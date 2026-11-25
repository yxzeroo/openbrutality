package org.brutality.module.impl.pit.hypixel.hud;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCake;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemSword;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.pit.hypixel.majorevent.TileClayHardenedStainedHandler;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.performance.WorldQueryCache;
import org.brutality.utils.player.PlayerApi;
import org.brutality.utils.render.GlStateGuard;
import org.brutality.utils.render.ResetColorHelper;
import org.brutality.utils.rotation.OnJumpHelper;
import org.brutality.utils.timer.TimerUtil;
import org.lwjgl.input.Mouse;

public class Cake extends Module implements PlayerApi {
    private final BooleanSetting auraSetting = new BooleanSetting("Aura", this, true);
    private final BooleanSetting espSetting = new BooleanSetting("ESP", this, true);
    private final TileClayHardenedStainedHandler tileClayHardenedStainedHandler;
    private boolean flag;
    private final PitCategory pitCategory = PitCategory.MINOR_EVENT;
    public static Cake instance;
    private final ModeSetting switchValueSetting;
    private final ModeSetting healthSetting = new ModeSetting("Sorting", this, "Distance", Arrays.asList("Distance", "Health"));
    private final ModeSetting packetSetting;
    private final NumberSetting packetSpamsSetting;
    private final NumberSetting circleSpeedSetting;
    private final NumberSetting switchDelaySetting;
    private final NumberSetting maxTargetsSetting;
    private final NumberSetting rangeSetting;
    private final NumberSetting minCPSSetting;
    private final NumberSetting maxCPSSetting;
    private final BooleanSetting cherriesSetting;
    private final BooleanSetting chocChipsSetting;
    private final BooleanSetting cakesSetting;
    private final BooleanSetting cakesNearCherriesSetting;
    private final BooleanSetting cakesNearChocChipsSetting;
    private final BooleanSetting renderSetting;
    private final BooleanSetting hudSetting;
    private final NumberSetting hudxSetting;
    private final NumberSetting hudySetting;
    private final NumberSetting hudFontSizeSetting;
    private final BooleanSetting swingSetting;
    private final ModeSetting noneSetting;
    private final BooleanSetting rightClickOnlySetting;
    private final BooleanSetting ignoreSwordsSetting;
    private final BooleanSetting debugSetting;
    private final TimerUtil timerUtil = new TimerUtil();
    private final Random random = new Random();
    private Cake.Entry entry = null;
    private float[] floatValueArray = null;
    private long timestamp = 100L;
    private long timestamp2;
    private List<Cake.Entry> entries = new ArrayList<>();
    private int count = 0;
    private int count2 = 0;
    private float factor = 0.0F;
    private float factor2 = 25.0F;
    private long timestamp3;
    private boolean flag2;
    private static final long timestamp4 = -4781395479244046337L;

    public Cake() {
        super("Cake", 0, Category.PIT);
        this.process18();
        this.switchValueSetting = new ModeSetting("Type", this, "Single", Arrays.asList("Single", "Switch"));
        this.packetSetting = new ModeSetting("Eat Mode", this, "Normal", Arrays.asList("Normal", "Legit", "Packet"));
        this.packetSpamsSetting = new NumberSetting("Packet Spams", this, 5.0, 1.0, 20.0, 1.0)
            .computeNumberSetting(() -> this.packetSetting.matchesString2("Packet"));
        this.circleSpeedSetting = new NumberSetting("Circle Speed", this, 8.0, 0.5, 30.0, 0.5)
            .computeNumberSetting(() -> this.packetSetting.matchesString2("Legit"));
        this.switchDelaySetting = new NumberSetting("Switch Delay", this, 150.0, 50.0, 2000.0, 50.0)
            .computeNumberSetting(() -> this.switchValueSetting.matchesString2("Switch"));
        this.noneSetting = new ModeSetting("Rotation", this, "Silent", Arrays.asList("Silent", "None"))
            .computeModeSetting(() -> !this.packetSetting.matchesString2("Legit"));
        this.maxTargetsSetting = new NumberSetting("Max Targets", this, 4.0, 1.0, 10.0, 1.0)
            .computeNumberSetting(() -> this.switchValueSetting.matchesString2("Switch") || this.noneSetting.matchesString2("None"));
        this.rangeSetting = new NumberSetting("Range", this, 3.5, 1.0, 6.0, 0.5);
        this.minCPSSetting = new NumberSetting("Min CPS", this, 10.0, 1.0, 200.0, 1.0).computeNumberSetting(() -> !this.packetSetting.matchesString2("Packet"));
        this.maxCPSSetting = new NumberSetting("Max CPS", this, 20.0, 1.0, 200.0, 1.0).computeNumberSetting(this::z);
        this.cherriesSetting = new BooleanSetting("Cherries", this, true);
        this.chocChipsSetting = new BooleanSetting("Choc Chips", this, true);
        this.cakesSetting = new BooleanSetting("Cakes", this, true);
        this.cakesNearCherriesSetting = new BooleanSetting("Cakes Near Cherries", this, false);
        this.cakesNearChocChipsSetting = new BooleanSetting("Cakes Near Choc Chips", this, false);
        this.renderSetting = new BooleanSetting("Render", this, true).computeBooleanSetting(() -> !this.packetSetting.matchesString2("Legit"));
        this.hudSetting = new BooleanSetting("HUD", this, true);
        this.hudxSetting = new NumberSetting("HUD X", this, 10.0, 0.0, 1920.0, 1.0);
        this.hudySetting = new NumberSetting("HUD Y", this, 80.0, 0.0, 1080.0, 1.0);
        this.hudFontSizeSetting = new NumberSetting("HUD Font Size", this, 18.0, 8.0, 48.0, 1.0);
        this.swingSetting = new BooleanSetting("Swing", this, true);
        this.rightClickOnlySetting = new BooleanSetting("Right Click Only", this, true);
        this.ignoreSwordsSetting = new BooleanSetting("Ignore Swords", this, true);
        this.debugSetting = new BooleanSetting("Debug", this, false);
        this.handleSettingArray(
            new Setting[]{
                this.switchValueSetting,
                this.healthSetting,
                this.packetSetting,
                this.packetSpamsSetting,
                this.circleSpeedSetting,
                this.switchDelaySetting,
                this.maxTargetsSetting,
                this.rangeSetting,
                this.minCPSSetting,
                this.maxCPSSetting,
                this.cherriesSetting,
                this.chocChipsSetting,
                this.cakesSetting,
                this.cakesNearCherriesSetting,
                this.cakesNearChocChipsSetting,
                this.renderSetting,
                this.hudSetting,
                this.hudxSetting,
                this.hudySetting,
                this.hudFontSizeSetting,
                this.swingSetting,
                this.noneSetting,
                this.rightClickOnlySetting,
                this.ignoreSwordsSetting,
                this.debugSetting
            }
        );
        this.handleSettingArray(new Setting[]{this.auraSetting, this.espSetting});
        this.tileClayHardenedStainedHandler = new TileClayHardenedStainedHandler(this);
        instance = this;
    }

    private boolean matchesBlockPos(BlockPos pos) {
        boolean flag = this.cakesNearCherriesSetting.isEnabled() || this.cakesNearChocChipsSetting.isEnabled();
        if (!flag) {
            return true;
        }
        if (!this.flag2) {
            return true;
        }
        for (EnumFacing facing : EnumFacing.values()) {
            IBlockState blockState = mc.theWorld.getBlockState(pos.offset(facing));
            if (blockState.getBlock() == Blocks.stained_hardened_clay) {
                int metaFromState = blockState.getBlock().getMetaFromState(blockState);
                if (metaFromState == EnumDyeColor.RED.getMetadata() && this.cakesNearCherriesSetting.isEnabled() && this.cherriesSetting.isEnabled()) {
                    return true;
                }

                if (metaFromState == EnumDyeColor.BLACK.getMetadata() && this.cakesNearChocChipsSetting.isEnabled() && this.chocChipsSetting.isEnabled()) {
                    return true;
                }
            }
        }

        return false;
    }

    private long computeLong() {
        double d = this.minCPSSetting.computeDouble();
        double d2 = this.maxCPSSetting.computeDouble();
        double d3 = d + this.random.nextDouble() * (d2 - d);
        return Math.max(1L, (long)(1000.0 / d3));
    }

    private double computeDouble(double d, double d2, double d3, double d4, double d5, double d6) {
        double d7 = d4 - d;
        double d8 = d5 - d2;
        double d9 = d6 - d3;
        return d7 * d7 + d8 * d8 + d9 * d9;
    }

    private BlockPos computeBlockPos(BlockPos pos) {
        double x = mc.thePlayer.posX;
        double d = mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
        double z = mc.thePlayer.posZ;
        double d2 = this.computeDouble(x, d, z, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos pos2 = pos.offset(facing);
            if (mc.theWorld.getBlockState(pos2).getBlock() == Blocks.cake && this.matchesBlockPos(pos2)) {
                double d3 = this.computeDouble(x, d, z, pos2.getX() + 0.5, pos2.getY() + 0.5, pos2.getZ() + 0.5);
                if (d3 < d2) {
                    return pos2;
                }
            }
        }

        return null;
    }

    private Cake.CakeMode computeCakeMode(BlockPos pos) {
        IBlockState blockState = mc.theWorld.getBlockState(pos);
        Block block = blockState.getBlock();
        if (block == Blocks.cake && this.cakesSetting.isEnabled()) {
            return this.matchesBlockPos(pos) ? Cake.CakeMode.CAKE : null;
        }
        if (block == Blocks.stained_hardened_clay) {
            int metaFromState = block.getMetaFromState(blockState);
            if (metaFromState == EnumDyeColor.RED.getMetadata() && this.cherriesSetting.isEnabled()) {
                return this.computeBlockPos(pos) != null ? null : Cake.CakeMode.CHERRY;
            }

            if (metaFromState == EnumDyeColor.BLACK.getMetadata() && this.chocChipsSetting.isEnabled()) {
                return this.computeBlockPos(pos) != null ? null : Cake.CakeMode.CHOCOLATE_CHIP;
            }
        }

        return null;
    }

    public BlockPos computeBlockPos() {
        return this.entry != null ? this.entry.blockPos : null;
    }

    @Override
    public void onUpdate() {
        if (this.auraSetting != null && !this.auraSetting.isEnabled()) {
            if (this.entry != null || this.floatValueArray != null) {
                this.process3();
            }
        } else if (!this.checkState2()) {
            if (this.entry != null || this.floatValueArray != null) {
                this.process3();
            }
        } else {
            if (this.minCPSSetting.computeDouble() > this.maxCPSSetting.computeDouble()) {
                this.minCPSSetting.handleDouble2(this.maxCPSSetting.computeDouble());
            }

            if (this.rightClickOnlySetting.isEnabled() && !Mouse.isButtonDown(1)) {
                this.process3();
            } else if (this.ignoreSwordsSetting.isEnabled() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                this.process3();
            } else if (this.packetSetting.matchesString2("Legit")) {
                if (!WorldQueryCache.matchesModule(this)) {
                    this.process4();
                } else if (this.timerUtil.matchesLong(this.timestamp, false)) {
                    this.timestamp = this.computeLong();
                    this.timerUtil.reset();
                    if (mc.objectMouseOver != null
                        && mc.objectMouseOver.getBlockPos() != null
                        && mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() == Blocks.cake) {
                        this.handleBlockPos(mc.objectMouseOver.getBlockPos());
                    }
                }
            } else {
                var list = this.computeList();
                if (list.isEmpty()) {
                    this.process3();
                } else {
                    if (!this.switchValueSetting.matchesString2("Switch") && !this.noneSetting.matchesString2("None")) {
                        this.entry = (Cake.Entry)list.get(0);
                    } else {
                        this.count2++;
                        this.entry = this.computeEntry(list);
                    }

                    if (this.entry == null) {
                        this.process3();
                    } else if (!WorldQueryCache.matchesModule(this)) {
                        this.process4();
                    } else {
                        KillAura.process33();
                        this.process();
                        if (!this.packetSetting.matchesString2("Packet")) {
                            if (this.timerUtil.matchesLong(this.timestamp, false)) {
                                this.timestamp = this.computeLong();
                                this.timerUtil.reset();
                                if (this.noneSetting.matchesString2("None")) {
                                    int n = Math.min(list.size(), (int)this.maxTargetsSetting.computeDouble());

                                    for (int i = 0; i < n; i++) {
                                        this.handleBlockPos(((Cake.Entry)list.get(i)).blockPos);
                                    }
                                } else {
                                    this.handleBlockPos(this.entry.blockPos);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean matchesBlockPos(BlockPos pos, int n) {
        if (this.cakesNearCherriesSetting.isEnabled() && this.cherriesSetting.isEnabled()
            || this.cakesNearChocChipsSetting.isEnabled() && this.chocChipsSetting.isEnabled()) {
            for (int i = -n; i <= n; i++) {
                for (int j = ~(n - 1); j <= n; j++) {
                    for (int k = -n; k <= n; k++) {
                        BlockPos pos2 = pos.add(i, j, k);
                        if (!(mc.thePlayer.getDistance(pos2.getX() + 0.5, pos2.getY() + 0.5, pos2.getZ() + 0.5) > this.rangeSetting.computeDouble())) {
                            IBlockState blockState = mc.theWorld.getBlockState(pos2);
                            if (blockState.getBlock() == Blocks.stained_hardened_clay) {
                                int metaFromState = blockState.getBlock().getMetaFromState(blockState);
                                if (metaFromState == EnumDyeColor.RED.getMetadata() && this.cakesNearCherriesSetting.isEnabled() && this.cherriesSetting.isEnabled()) {
                                    return true;
                                }

                                if (metaFromState == EnumDyeColor.BLACK.getMetadata() && this.cakesNearChocChipsSetting.isEnabled() && this.chocChipsSetting.isEnabled()
                                    )
                                 {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }

            return false;
        }
        return false;
    }

    public float[] getFloatValueArray() {
        return this.floatValueArray;
    }

    private void process3() {
        this.entry = null;
        this.process6();
    }

    private void process() {
        if (!this.noneSetting.matchesString2("Silent")) {
            this.process6();
        } else {
            OnJumpHelper.setReach((float)this.rangeSetting.computeDouble());
            float[] floats = this.computeFloatArray(this.entry.blockPos);
            if (floats != null) {
                this.floatValueArray = floats;
                OnJumpHelper.handleFloat(floats[0], floats[1], 2);
                OnJumpHelper.floatValueArray2 = floats;
            }
        }
    }

    private String computeString() {
        if (mc.theWorld.getScoreboard() == null) {
            return "0";
        }
        for (ScoreObjective scoreObjective : mc.theWorld.getScoreboard().getScoreObjectives()) {
            for (Score score : mc.theWorld.getScoreboard().getSortedScores(scoreObjective)) {
                String str = score.getPlayerName().toLowerCase(Locale.ROOT);
                if (str.contains("gold")) {
                    return String.valueOf(score.getScorePoints());
                }
            }
        }

        return "0";
    }

    public boolean checkState4() {
        return this.auraSetting.isEnabled() && this.entry != null && this.checkState2();
    }

    private void onEventPreMotion(EventPreMotion event) {
        if (WorldQueryCache.matchesModule2(this)) {
            if (this.checkState2()) {
                if (event.getState() == EventState.PRE) {
                    if (this.packetSetting.matchesString2("Legit")) {
                        this.factor = (this.factor + (float)this.circleSpeedSetting.computeDouble()) % 360.0F;
                        mc.thePlayer.rotationYaw = this.factor;
                        mc.thePlayer.rotationYawHead = this.factor;
                        mc.thePlayer.renderYawOffset = this.factor;
                        mc.thePlayer.rotationPitch = this.factor2;
                        event.setYaw(this.factor);
                        event.setPitch(this.factor2);
                        return;
                    }

                    if (this.noneSetting.matchesString2("Silent") && OnJumpHelper.checkState()) {
                        mc.thePlayer.rotationYawHead = OnJumpHelper.floatValueArray2[0];
                        mc.thePlayer.rotationPitchHead = OnJumpHelper.floatValueArray2[1];
                        mc.thePlayer.renderYawOffset = OnJumpHelper.floatValueArray2[0];
                        event.setYaw(OnJumpHelper.floatValueArray2[0]);
                        event.setPitch(OnJumpHelper.floatValueArray2[1]);
                    }

                    if (!this.packetSetting.matchesString2("Packet")) {
                        return;
                    }

                    var list = this.computeList();
                    if (list.isEmpty()) {
                        return;
                    }

                    mc.playerController.blockHitDelay = 0;
                    int n = (int)this.packetSpamsSetting.computeDouble();

                    for (Cake.Entry entry : list) {
                        for (int i = 0; i < n; i++) {
                            WorldQueryCache.matchesModule(this, entry.blockPos, this.swingSetting.isEnabled() && i == 0, false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        if (this.tileClayHardenedStainedHandler != null) {
            this.tileClayHardenedStainedHandler.onEnable();
        }

        this.flag = this.espSetting != null && this.espSetting.isEnabled();
        this.entry = null;
        this.floatValueArray = null;
        this.timestamp = this.computeLong();
        this.timerUtil.reset();
        this.timestamp3 = System.currentTimeMillis();
        this.entries = new ArrayList<>();
        this.count = 0;
        this.count2 = 0;
        if (GetCurrentScreenHelper.checkState2()) {
            this.factor = mc.thePlayer.rotationYaw;
            this.factor2 = mc.thePlayer.rotationPitch;
        }
    }

    private Cake.Entry computeEntry(List<Cake.Entry> list) {
        int n = Math.min(list.size(), (int)this.maxTargetsSetting.computeDouble());
        var list2 = list.subList(0, n);
        if (this.entry != null) {
            Cake.Entry entry = (Cake.Entry)list2.get(0);
            if (entry.cakeMode.priority > this.entry.cakeMode.priority) {
                this.entries = new ArrayList<>(list2);
                this.count = 1 % this.entries.size();
                this.count2 = 0;
                return entry;
            }
        }

        boolean flag = this.entry != null && this.matchesBlockPos2(this.entry.blockPos) && this.matchesList(list2, this.entry.blockPos);
        int n2 = Math.max(1, (int)(this.switchDelaySetting.computeDouble() / 50.0));
        boolean flag2 = this.count2 >= n2;
        if (flag && !flag2) {
            return this.entry;
        }
        this.count2 = 0;
        this.entries = new ArrayList<>(list2);
        if (this.count >= this.entries.size()) {
            this.count = 0;
        }

        Cake.Entry entry2 = this.entries.get(this.count);
        this.count = (this.count + 1) % this.entries.size();
        return entry2;
    }

    private float[] computeFloatArray(BlockPos pos) {
        double d = pos.getX() + 0.5 - mc.thePlayer.posX;
        double d2 = pos.getY() + 0.5 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double d3 = pos.getZ() + 0.5 - mc.thePlayer.posZ;
        double dist = Math.sqrt(d * d + d3 * d3);
        float f = (float)(Math.atan2(d3, d) * 180.0 / Math.PI) - 90.0F;
        float f2 = (float)(-(Math.atan2(d2, dist) * 180.0 / Math.PI));
        return new float[]{f, f2};
    }

    private boolean matchesBlockPos2(BlockPos pos) {
        if (pos == null) {
            return false;
        }
        Cake.CakeMode cakeMode = this.computeCakeMode(pos);
        return cakeMode != null;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (this.tileClayHardenedStainedHandler != null && this.espSetting != null) {
                if (this.flag != this.espSetting.isEnabled()) {
                    if (this.espSetting.isEnabled()) {
                        this.tileClayHardenedStainedHandler.onEnable();
                    } else {
                        this.tileClayHardenedStainedHandler.onDisable();
                    }

                    this.flag = this.espSetting.isEnabled();
                }

                if (this.espSetting.isEnabled()) {
                    if (this.auraSetting.isEnabled() && event instanceof EventRender3D) {
                        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                            this.tileClayHardenedStainedHandler.onEvent(event);
                        }
                    } else {
                        this.tileClayHardenedStainedHandler.onEvent(event);
                    }
                }
            }

            if (this.auraSetting != null && !this.auraSetting.isEnabled()) {
                if (this.entry != null || this.floatValueArray != null) {
                    this.process3();
                }
            } else if (this.checkState2()) {
                if (event instanceof EventUpdate) {
                    if (((EventUpdate)event).getState() == EventState.POST) {
                        return;
                    }

                    this.onUpdate();
                } else if (event instanceof EventPreMotion) {
                    if (((EventPreMotion)event).getState() == EventState.PRE) {
                        this.onEventPreMotion((EventPreMotion)event);
                    }
                } else if (event instanceof EventRender3D && this.renderSetting.isEnabled()) {
                    this.process5();
                }
            } else {
                if (this.entry != null || this.floatValueArray != null) {
                    this.process3();
                }
            }
        }
    }

    private int computeInt() {
        int n = 0;
        int n2 = (int)Math.ceil(this.rangeSetting.computeDouble());
        BlockPos pos = mc.thePlayer.getPosition();

        for (int i = ~n2 + 1; i <= n2; i++) {
            for (int j = -n2; j <= n2; j++) {
                for (int k = ~n2 + 1; k <= n2; k++) {
                    if (mc.theWorld.getBlockState(pos.add(i, j, k)).getBlock() == Blocks.cake) {
                        n++;
                    }
                }
            }
        }

        return n;
    }

    @Override
    public void process2() {
        this.onDisable();
    }

    private void process5() {
        if (this.entry != null) {
            ResetColorHelper.handleBlockPos2(this.entry.blockPos, switch (this.entry.cakeMode) {
                case CHERRY -> new Color(220, 50, 50, 130);
                case CHOCOLATE_CHIP -> new Color(40, 20, 10, 130);
                default -> this.entry.amount <= this.rangeSetting.computeDouble() * 0.7 ? new Color(0, 200, 0, 100) : new Color(255, 165, 0, 100);
            }, true, true, 1.0F);
            if (this.switchValueSetting.matchesString2("Switch") && this.entries != null) {
                for (Cake.Entry entry : this.entries) {
                    if (!entry.blockPos.equals(this.entry.blockPos)) {
                        ResetColorHelper.handleBlockPos2(entry.blockPos, new Color(100, 100, 255, 60), false, true, 0.5F);
                    }
                }
            }
        }
    }

    private void handleBlockPos(BlockPos pos) {
        if (this.checkState2()) {
            WorldQueryCache.matchesModule(this, pos, this.swingSetting.isEnabled(), false);
        }
    }

    private boolean checkState2() {
        return mc.theWorld != null
            && mc.thePlayer != null
            && FreshHelper.flag
            && !mc.thePlayer.isDead
            && mc.thePlayer.getHealth() > 0.0F
            && !FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer);
    }

    @Override
    public void process4() {
        this.process6();
    }

    private List<Cake.Entry> computeList() {
        List list = new ArrayList();
        HashSet set = new HashSet();
        int n = (int)Math.ceil(this.rangeSetting.computeDouble());
        BlockPos pos = mc.thePlayer.getPosition();
        this.flag2 = this.matchesBlockPos(pos, n);

        for (int i = -n; i <= n; i++) {
            for (int j = ~(n - 1); j <= n; j++) {
                for (int k = -n; k <= n; k++) {
                    BlockPos pos2 = pos.add(i, j, k);
                    double dist = mc.thePlayer.getDistance(pos2.getX() + 0.5, pos2.getY() + 0.5, pos2.getZ() + 0.5);
                    if (!(dist > this.rangeSetting.computeDouble())) {
                        IBlockState blockState = mc.theWorld.getBlockState(pos2);
                        Block block = blockState.getBlock();
                        if (block == Blocks.stained_hardened_clay) {
                            int metaFromState = block.getMetaFromState(blockState);
                            boolean flag = metaFromState == EnumDyeColor.RED.getMetadata() && this.cherriesSetting.isEnabled();
                            boolean flag2 = metaFromState == EnumDyeColor.BLACK.getMetadata() && this.chocChipsSetting.isEnabled();
                            if (flag || flag2) {
                                BlockPos pos3 = this.computeBlockPos(pos2);
                                if (pos3 != null && !set.contains(pos3)) {
                                    double dist2 = mc.thePlayer.getDistance(pos3.getX() + 0.5, pos3.getY() + 0.5, pos3.getZ() + 0.5);
                                    list.add(new Cake.Entry(pos3, Cake.CakeMode.CAKE, dist2));
                                    set.add(pos3);
                                }

                                if (pos3 != null) {
                                    continue;
                                }
                            }
                        }

                        Cake.CakeMode cakeMode = this.computeCakeMode(pos2);
                        if (cakeMode != null) {
                            list.add(new Cake.Entry(pos2, cakeMode, dist));
                        }
                    }
                }
            }
        }

        ((List<Cake.Entry>)list).sort((entry, entry2) -> {
            int n2 = Integer.compare(entry2.cakeMode.priority, entry.cakeMode.priority);
            if (n2 == 0 && this.healthSetting.matchesString2("Health") && entry.cakeMode == Cake.CakeMode.CAKE) {
                n2 = Integer.compare(this.computeInt(entry.blockPos), this.computeInt(entry2.blockPos));
            }

            return n2 != 0 ? n2 : Double.compare(entry.amount, entry2.amount);
        });
        this.handleList(list);
        return list;
    }

    @Override
    public void onRender() {
        if (this.auraSetting == null || this.auraSetting.isEnabled()) {
            if (this.hudSetting.isEnabled() && this.checkState2()) {
                int n = this.computeInt();
                long l = Math.max(0L, 30L - (System.currentTimeMillis() - this.timestamp3) / 1000L);
                String str = this.computeString();
                long l2 = l;
                int n2 = n;
                String str2 = "§6Cake §f" + n2 + " §7| §eTimer: §f" + l2 + "s §7| §aGold: §f" + str;
                FontRenderer fontRenderer = this.computeFontRenderer2((float)this.hudFontSizeSetting.computeDouble());
                (fontRenderer != null ? fontRenderer : computeFontRenderer(Cake.class))
                    .drawStringWithShadow(str2, (float)this.hudxSetting.computeDouble(), (float)this.hudySetting.computeDouble(), (int)timestamp4);
            }
        }
    }

    private int computeInt(BlockPos pos) {
        IBlockState blockState = mc.theWorld.getBlockState(pos);
        return blockState.getBlock() == Blocks.cake ? 7 - blockState.getValue(BlockCake.BITES) : 7;
    }

    private boolean matchesList(List<Cake.Entry> list, BlockPos pos) {
        for (Cake.Entry entry : list) {
            if (entry.blockPos.equals(pos)) {
                return true;
            }
        }

        return false;
    }

    private void process6() {
        float[] floatValueArray = this.floatValueArray;
        this.floatValueArray = null;
        if (floatValueArray != null && OnJumpHelper.floatValueArray2 == floatValueArray) {
            OnJumpHelper.disable();
        }
    }

    private boolean z() {
        return !this.packetSetting.matchesString2("Packet");
    }

    private void handleList(List<Cake.Entry> list) {
        if (this.debugSetting.isEnabled() && mc.thePlayer != null && System.currentTimeMillis() - this.timestamp2 >= 1000L) {
            this.timestamp2 = System.currentTimeMillis();
            int n = 0;
            int n2 = 0;
            int n3 = 0;
            StringBuilder sb = new StringBuilder();
            int n4 = (int)Math.ceil(this.rangeSetting.computeDouble());
            BlockPos pos = mc.thePlayer.getPosition();

            for (int i = -n4; i <= n4; i++) {
                for (int j = ~n4 + 1; j <= n4; j++) {
                    for (int k = ~n4 + 1; k <= n4; k++) {
                        BlockPos pos2 = pos.add(i, j, k);
                        Block block = mc.theWorld.getBlockState(pos2).getBlock();
                        if (block == Blocks.cake) {
                            n++;
                        }

                        if (block == Blocks.stained_hardened_clay) {
                            int metaFromState = block.getMetaFromState(mc.theWorld.getBlockState(pos2));
                            if (metaFromState == EnumDyeColor.RED.getMetadata() || metaFromState == EnumDyeColor.BLACK.getMetadata()) {
                                if (metaFromState == EnumDyeColor.RED.getMetadata()) {
                                    n2++;
                                } else {
                                    n3++;
                                }

                                StringBuilder sb2 = new StringBuilder();

                                for (EnumFacing facing : EnumFacing.values()) {
                                    if (mc.theWorld.getBlockState(pos2.offset(facing)).getBlock() == Blocks.cake) {
                                        if (sb2.length() > 0) {
                                            sb2.append(',');
                                        }

                                        sb2.append(facing.name());
                                    }
                                }

                                sb.append(' ')
                                    .append(metaFromState == EnumDyeColor.RED.getMetadata() ? "Cherry" : "Choc")
                                    .append('@')
                                    .append(pos2.getX())
                                    .append(',')
                                    .append(pos2.getY())
                                    .append(',')
                                    .append(pos2.getZ())
                                    .append(" cakes[")
                                    .append((CharSequence)sb2)
                                    .append("];");
                            }
                        }
                    }
                }
            }

            EntityPlayerSP player = mc.thePlayer;
            int size = list.size();
            player.addChatMessage(new ChatComponentText("§e[Cake] cakes=" + n + " cherries=" + n2 + " choc=" + n3 + " targets=" + size));
            if (sb.length() > 0) {
                mc.thePlayer.addChatMessage(new ChatComponentText("§7[Cake]" + sb));
            }

            if (this.entry != null) {
                player = mc.thePlayer;
                String label = this.entry.cakeMode.label;
                int x = this.entry.blockPos.getX();
                int z = this.entry.blockPos.getZ();
                int y = this.entry.blockPos.getY();
                int n5 = x;
                String str = label;
                player.addChatMessage(new ChatComponentText("§b[Cake] target=" + str + " @ " + n5 + "," + y + "," + z));
            }
        }
    }

    @Override
    public void onDisable() {
        if (this.tileClayHardenedStainedHandler != null) {
            this.tileClayHardenedStainedHandler.onDisable();
        }

        this.flag = false;
        this.process3();
        this.timerUtil.reset();
        this.entries = new ArrayList<>();
        this.count = 0;
        this.count2 = 0;
    }

    public boolean checkState() {
        return this.auraSetting != null && !this.auraSetting.isEnabled()
            ? false
            : this.noneSetting.matchesString2("Silent") && this.entry != null && this.floatValueArray != null;
    }

    public static enum CakeMode {
        CAKE(0, "Cake"),
        CHOCOLATE_CHIP(1, "Choc Chip"),
        CHERRY(2, "Cherry");

        public final int priority;
        public final String label;

        private CakeMode(int priority, String label) {
            this.priority = priority;
            this.label = label;
        }

        private static Cake.CakeMode[] computeCakeModeArray() {
            return new Cake.CakeMode[]{CAKE, CHOCOLATE_CHIP, CHERRY};
        }
    }

    public static class Entry {
        public final BlockPos blockPos;
        public final Cake.CakeMode cakeMode;
        public final double amount;

        public Entry(BlockPos blockPos, Cake.CakeMode cakeMode, double amount) {
            this.blockPos = blockPos;
            this.cakeMode = cakeMode;
            this.amount = amount;
        }
    }
}
