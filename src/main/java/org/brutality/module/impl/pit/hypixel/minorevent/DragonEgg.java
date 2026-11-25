package org.brutality.module.impl.pit.hypixel.minorevent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.movement.MovementFix;
import org.brutality.module.impl.movement.Scaffold;
import org.brutality.module.impl.pit.hypixel.minorevent.TickScanHandler;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.performance.WorldQueryCache;
import org.brutality.utils.player.PlayerApi;
import org.brutality.utils.render.GlStateGuard;
import org.brutality.utils.render.ResetColorHelper;
import org.brutality.utils.rotation.OnJumpHelper;
import org.brutality.utils.timer.TimerUtil;

public class DragonEgg
extends Module
implements PlayerApi {
    private final BooleanSetting auraSetting = new BooleanSetting("Aura", this, true);
    private final BooleanSetting espSetting = new BooleanSetting("ESP", this, true);
    private final TickScanHandler tickScanHandler;
    private boolean flag;
    private final PitCategory pitCategory = PitCategory.MINOR_EVENT;
    public static DragonEgg instance;
    private final NumberSetting rangeSetting;
    private final NumberSetting minCPSSetting;
    private final NumberSetting maxCPSSetting;
    private final BooleanSetting renderSetting;
    private final BooleanSetting hudSetting;
    private final NumberSetting hudxSetting;
    private final NumberSetting hudySetting;
    private final NumberSetting hudFontSizeSetting;
    private final BooleanSetting swingSetting;
    public final ModeSetting noneSetting;
    private final BooleanSetting ignoreSwordsSetting;
    private final TimerUtil timerUtil = new TimerUtil();
    private final Random random = new Random();
    private Entry entry = null;
    private float[] floatValueArray = null;
    private long timestamp = timestamp3;
    private static final long timestamp2;
    private static final long timestamp3;

    @Override
    public void onDisable() {
        if (this.tickScanHandler != null) {
            this.tickScanHandler.onDisable();
        }
        this.flag = false;
        this.process5();
        this.timerUtil.reset();
    }

    private boolean matchesBlockPos(BlockPos blockPos) {
        IBlockState iBlockState = DragonEgg.mc.theWorld.getBlockState(blockPos);
        return iBlockState.getBlock() == Blocks.dragon_egg;
    }

    private void handleBlockPos(BlockPos blockPos) {
        if (!this.checkState2()) {
            return;
        }
        WorldQueryCache.matchesModule(this, blockPos, this.swingSetting.isEnabled(), false);
    }

    private void process6() {
        if (!this.noneSetting.matchesString2("Silent")) {
            this.process3();
            return;
        }
        if (Scaffold.instance != null && Scaffold.instance.checkState44() && Scaffold.instance.checkState27() && OnJumpHelper.checkState()) {
            this.process3();
            return;
        }
        OnJumpHelper.setReach((float)this.rangeSetting.computeDouble());
        float[] fArray = this.computeFloatArray(this.entry.blockPos);
        if (fArray != null) {
            this.floatValueArray = fArray;
            OnJumpHelper.handleFloat(fArray[0], fArray[1], 2);
            OnJumpHelper.floatValueArray2 = fArray;
            if (MovementFix.isEnabled()) {
                MovementFix.handleFloat(fArray[0], fArray[1]);
            }
        }
    }

    private void process() {
        if (this.entry == null) {
            return;
        }
        Color color = this.entry.amount <= this.rangeSetting.computeDouble() * 0.7 ? new Color(148, 0, 211, 130) : new Color(75, 0, 130, 130);
        ResetColorHelper.handleBlockPos2(this.entry.blockPos, color, true, true, 1.0f);
    }

    @Override
    public void onUpdate() {
        List<Entry> list;
        if (this.auraSetting != null && !this.auraSetting.isEnabled()) {
            if (this.entry != null || this.floatValueArray != null) {
                this.process5();
            }
            return;
        }
        if (!this.checkState2()) {
            if (this.entry != null || this.floatValueArray != null) {
                this.process5();
            }
            return;
        }
        if (this.ignoreSwordsSetting.isEnabled() && DragonEgg.mc.thePlayer.getHeldItem() != null && DragonEgg.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
            this.process5();
            return;
        }
        if (this.minCPSSetting.computeDouble() > this.maxCPSSetting.computeDouble()) {
            this.minCPSSetting.handleDouble2(this.maxCPSSetting.computeDouble());
        }
        if ((list = this.scanAll()).isEmpty()) {
            this.process5();
            return;
        }
        this.entry = list.get(0);
        if (this.entry == null) {
            this.process5();
            return;
        }
        if (!WorldQueryCache.matchesModule(this)) {
            this.process4();
            return;
        }
        KillAura.process33();
        this.process6();
        if (!this.timerUtil.matchesLong(this.timestamp, false)) {
            return;
        }
        this.timestamp = this.computeLong();
        this.timerUtil.reset();
        this.handleBlockPos(this.entry.blockPos);
    }

    @Override
    public void process2() {
        this.onDisable();
    }

    public boolean checkState4() {
        if (this.auraSetting != null && !this.auraSetting.isEnabled()) {
            return false;
        }
        return this.noneSetting.matchesString2("Silent") && this.entry != null && this.floatValueArray != null;
    }

    private void process3() {
        float[] fArray = this.floatValueArray;
        this.floatValueArray = null;
        if (fArray == null || OnJumpHelper.floatValueArray2 != fArray) {
            return;
        }
        OnJumpHelper.disable();
        if (MovementFix.isEnabled()) {
            MovementFix.process3();
        }
    }

    public float[] getFloatValueArray() {
        return this.floatValueArray;
    }

    @Override
    public void process4() {
        this.process3();
    }

    @Override
    public void onEvent(Event event) {
        if (!this.checkState44()) {
            return;
        }
        if (this.tickScanHandler != null && this.espSetting != null) {
            if (this.flag != this.espSetting.isEnabled()) {
                if (this.espSetting.isEnabled()) {
                    this.tickScanHandler.onEnable();
                } else {
                    this.tickScanHandler.onDisable();
                }
                this.flag = this.espSetting.isEnabled();
            }
            if (this.espSetting.isEnabled()) {
                if (this.auraSetting.isEnabled() && event instanceof EventRender3D) {
                    try (GlStateGuard renderHelper7 = new GlStateGuard();){
                        this.tickScanHandler.onEvent(event);
                    }
                } else {
                    this.tickScanHandler.onEvent(event);
                }
            }
        }
        if (this.auraSetting != null && !this.auraSetting.isEnabled()) {
            if (this.entry != null || this.floatValueArray != null) {
                this.process5();
            }
            return;
        }
        if (!this.checkState2()) {
            if (this.entry != null || this.floatValueArray != null) {
                this.process5();
            }
            return;
        }
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
            this.process();
        }
    }

    private long computeLong() {
        double d = this.minCPSSetting.computeDouble();
        double d2 = this.maxCPSSetting.computeDouble();
        double d3 = d + this.random.nextDouble() * (d2 - d);
        return Math.max(1L, (long)(1000.0 / d3));
    }

    @Override
    public void onRender() {
        if (this.auraSetting != null && !this.auraSetting.isEnabled()) {
            return;
        }
        if (!this.hudSetting.isEnabled() || !this.checkState2()) {
            return;
        }
        String string = "\u00a75Eggs \u00a7f" + this.computeInt() + " \u00a77| \u00a7eCPS: \u00a7f" + String.format(Locale.ROOT, "%.0f-%.0f", this.minCPSSetting.computeDouble(), this.maxCPSSetting.computeDouble());
        FontRenderer fontRenderer = this.computeFontRenderer2((float)this.hudFontSizeSetting.computeDouble());
        (fontRenderer != null ? fontRenderer : DragonEgg.computeFontRenderer(DragonEgg.class)).drawStringWithShadow(string, (float)this.hudxSetting.computeDouble(), (float)this.hudySetting.computeDouble(), (int)timestamp2);
    }

    private int computeInt() {
        int n = 0;
        int n2 = (int)Math.ceil(this.rangeSetting.computeDouble());
        BlockPos blockPos = DragonEgg.mc.thePlayer.getPosition();
        for (int i = -n2; i <= n2; ++i) {
            for (int j = (n2 ^ 0xFFFFFFFF) + 1; j <= n2; ++j) {
                for (int k = (n2 ^ 0xFFFFFFFF) + 1; k <= n2; ++k) {
                    if (DragonEgg.mc.theWorld.getBlockState(blockPos.add(i, j, k)).getBlock() != Blocks.dragon_egg) continue;
                    ++n;
                }
            }
        }
        return n;
    }

    private boolean checkState2() {
        return DragonEgg.mc.theWorld != null && DragonEgg.mc.thePlayer != null && FreshHelper.flag && !DragonEgg.mc.thePlayer.isDead && DragonEgg.mc.thePlayer.getHealth() > 0.0f && !FreshHelper.matchesWorld3(DragonEgg.mc.theWorld, DragonEgg.mc.thePlayer);
    }

    @Override
    public void onEnable() {
        if (this.tickScanHandler != null) {
            this.tickScanHandler.onEnable();
        }
        this.flag = this.espSetting != null && this.espSetting.isEnabled();
        this.entry = null;
        this.floatValueArray = null;
        this.timestamp = this.computeLong();
        this.timerUtil.reset();
    }

    private List<Entry> scanAll() {
        ArrayList<Entry> arrayList = new ArrayList<Entry>();
        int n = (int)Math.ceil(this.rangeSetting.computeDouble());
        BlockPos blockPos = DragonEgg.mc.thePlayer.getPosition();
        for (int i = -n; i <= n; ++i) {
            for (int j = -n; j <= n; ++j) {
                for (int k = -n; k <= n; ++k) {
                    BlockPos blockPos2 = blockPos.add(i, j, k);
                    double d = DragonEgg.mc.thePlayer.getDistance((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5);
                    if (d > this.rangeSetting.computeDouble() || !this.matchesBlockPos(blockPos2)) continue;
                    arrayList.add(new Entry(blockPos2, d));
                }
            }
        }
        arrayList.sort((entry, entry2) -> Double.compare(entry.amount, entry2.amount));
        return arrayList;
    }

    public DragonEgg() {
        super("Dragon Egg", 0, Category.PIT);
        this.process18();
        this.rangeSetting = new NumberSetting("Range", this, 3.5, 1.0, 5.0, 0.5);
        this.minCPSSetting = new NumberSetting("Min CPS", this, 8.0, 1.0, 20.0, 1.0);
        this.maxCPSSetting = new NumberSetting("Max CPS", this, 14.0, 1.0, 20.0, 1.0);
        this.renderSetting = new BooleanSetting("Render", this, true);
        this.hudSetting = new BooleanSetting("HUD", this, true);
        this.hudxSetting = new NumberSetting("HUD X", this, 10.0, 0.0, 1920.0, 1.0);
        this.hudySetting = new NumberSetting("HUD Y", this, 100.0, 0.0, 1080.0, 1.0);
        this.hudFontSizeSetting = new NumberSetting("HUD Font Size", this, 18.0, 8.0, 48.0, 1.0);
        this.swingSetting = new BooleanSetting("Swing", this, true);
        this.noneSetting = new ModeSetting("Rotation", (Module)this, "Silent", Arrays.asList("Silent", "None"));
        this.ignoreSwordsSetting = new BooleanSetting("Ignore Swords", this, true);
        this.handleSettingArray(this.rangeSetting, this.minCPSSetting, this.maxCPSSetting, this.renderSetting, this.hudSetting, this.hudxSetting, this.hudySetting, this.hudFontSizeSetting, this.swingSetting, this.noneSetting, this.ignoreSwordsSetting);
        this.handleSettingArray(this.auraSetting, this.espSetting);
        this.tickScanHandler = new TickScanHandler(this);
        instance = this;
    }

    static {
        timestamp2 = 407016935214022655L;
        timestamp3 = 100L;
    }

    private void process5() {
        this.entry = null;
        this.process3();
    }

    private void onEventPreMotion(EventPreMotion eventPreMotion) {
        if (!WorldQueryCache.matchesModule2(this)) {
            return;
        }
        if (!this.checkState2()) {
            return;
        }
        if (!this.noneSetting.matchesString2("Silent")) {
            return;
        }
        if (Scaffold.instance != null && Scaffold.instance.checkState44() && Scaffold.instance.checkState27()) {
            return;
        }
        if (!OnJumpHelper.checkState()) {
            return;
        }
        DragonEgg.mc.thePlayer.rotationYawHead = OnJumpHelper.floatValueArray2[0];
        DragonEgg.mc.thePlayer.rotationPitchHead = OnJumpHelper.floatValueArray2[1];
        DragonEgg.mc.thePlayer.renderYawOffset = OnJumpHelper.floatValueArray2[0];
        eventPreMotion.setYaw(OnJumpHelper.floatValueArray2[0]);
        eventPreMotion.setPitch(OnJumpHelper.floatValueArray2[1]);
    }

    private float[] computeFloatArray(BlockPos blockPos) {
        double d = (double)blockPos.getX() + 0.5 - DragonEgg.mc.thePlayer.posX;
        double d2 = (double)blockPos.getY() + 0.5 - (DragonEgg.mc.thePlayer.posY + (double)DragonEgg.mc.thePlayer.getEyeHeight());
        double d3 = (double)blockPos.getZ() + 0.5 - DragonEgg.mc.thePlayer.posZ;
        double d4 = Math.sqrt(d * d + d3 * d3);
        float f = MathHelper.wrapAngleTo180_float((float)(Math.atan2(d3, d) * 180.0 / Math.PI) - 90.0f);
        float f2 = MathHelper.clamp_float((float)(-(Math.atan2(d2, d4) * 180.0 / Math.PI)), -90.0f, 90.0f);
        return new float[]{f, f2};
    }

    public boolean checkState() {
        return this.auraSetting.isEnabled() && this.entry != null && this.checkState2();
    }

    public BlockPos computeBlockPos() {
        return this.entry != null ? this.entry.blockPos : null;
    }

    public static class Entry {
        public final BlockPos blockPos;
        public final double amount;

        public Entry(BlockPos blockPos, double d) {
            this.blockPos = blockPos;
            this.amount = d;
        }
    }
}
