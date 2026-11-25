package org.brutality.module.impl.visual;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.performance.EntityCache;
import org.brutality.utils.player.PlayerUtils;
import org.brutality.utils.render.ResetColorHelper;

public class BedAlert extends Module {
    private final NumberSetting nearbyRangeSetting = new NumberSetting("Nearby Range", this, 10.0, 3.0, 30.0, 1.0);
    private final NumberSetting alertRangeSetting = new NumberSetting("Alert Range", this, 50.0, 10.0, 100.0, 1.0);
    private final NumberSetting minSpeedSetting = new NumberSetting("Min Speed", this, 0.05, 0.01, 1.0, 0.01);
    private final NumberSetting maxAlertsEnemySetting = new NumberSetting("Max Alerts/Enemy", this, 3.0, 1.0, 10.0, 1.0);
    private final BooleanSetting notifyPartySetting = new BooleanSetting("Notify Party", this, false);
    private final NumberSetting notifyIntervalSetting = new NumberSetting("Notify Interval", this, 3.0, 1.0, 30.0, 1.0);
    private final BooleanSetting ignoreTeammatesSetting = new BooleanSetting("Ignore Teammates", this, true);
    private final BooleanSetting showHUDSetting = new BooleanSetting("Show HUD", this, true);
    private BlockPos blockPos;
    private final Map<String, Integer> lookupMap = new HashMap<>();
    private long timestamp;
    private int count;
    private int count2 = Integer.MAX_VALUE;

    private BlockPos computeBlockPos(BlockPos pos, int n) {
        BlockPos pos2 = null;
        double MAX_VALUE = Double.MAX_VALUE;

        for (int i = ~(n - 1); i <= n; i++) {
            for (int j = -10; j <= 10; j++) {
                for (int k = -n; k <= n; k++) {
                    BlockPos pos3 = pos.add(i, j, k);
                    if (mc.theWorld.getBlockState(pos3).getBlock() == Blocks.bed) {
                        double d = pos3.distanceSq(pos);
                        if (d < MAX_VALUE) {
                            MAX_VALUE = d;
                            pos2 = pos3;
                        }
                    }
                }
            }
        }

        return pos2;
    }

    @Override
    public void onEnable() {
        this.lookupMap.clear();
        if (mc.thePlayer != null) {
            this.blockPos = this.computeBlockPos(mc.thePlayer.getPosition(), 30);
        }
    }

    private void process() {
        this.count = 0;
        this.count2 = Integer.MAX_VALUE;
        if (this.blockPos == null) {
            this.blockPos = this.computeBlockPos(mc.thePlayer.getPosition(), 40);
            if (this.blockPos == null) {
                return;
            }
        }

        double d = this.alertRangeSetting.computeDouble() * this.alertRangeSetting.computeDouble();
        double d2 = this.nearbyRangeSetting.computeDouble() * this.nearbyRangeSetting.computeDouble();
        double d3 = this.minSpeedSetting.computeDouble();

        for (EntityPlayer player : EntityCache.getEntries()) {
            if (player != mc.thePlayer
                && player.isEntityAlive()
                && (!this.ignoreTeammatesSetting.isEnabled() || !PlayerUtils.matchesEntityPlayer(player))
                && !DefaultHelper.matchesString(player.getName())) {
                double d4 = Math.hypot(player.motionX, player.motionZ);
                if (!(d4 < d3)) {
                    double distanceSqToCenter = player.getDistanceSqToCenter(this.blockPos);
                    if (!(distanceSqToCenter > d)) {
                        int dist = (int)Math.sqrt(distanceSqToCenter);
                        if (dist < this.count2) {
                            this.count2 = dist;
                        }

                        if (distanceSqToCenter <= d2) {
                            this.count++;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() && mc.thePlayer != null && mc.theWorld != null) {
            if (event instanceof EventUpdate && EventState.onEvent(event)) {
                this.process();
                this.process3();
            } else if (event instanceof EventRender2D && this.showHUDSetting.isEnabled()) {
                this.process4();
            }
        }
    }

    public BedAlert() {
        super("BedAlert", 0, Category.VISUAL);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.nearbyRangeSetting,
                this.alertRangeSetting,
                this.minSpeedSetting,
                this.maxAlertsEnemySetting,
                this.notifyPartySetting,
                this.notifyIntervalSetting,
                this.ignoreTeammatesSetting,
                this.showHUDSetting
            }
        );
    }

    private void process4() {
        if (this.count > 0 && this.count2 != Integer.MAX_VALUE) {
            ScaledResolution sr = InvalidateHelper.computeScaledResolution(mc);
            FontRenderer fontRenderer = this.computeFontRenderer2();
            String str = "BED INC";
            String str2 = this.count + (this.count == 1 ? " enemy" : " enemies") + " • " + this.count2 + "m from bed";
            float f2 = 6.0F;
            float f3 = fontRenderer.getStringWidth(str) + 14.0F;
            float f4 = Math.max(16.0F, fontRenderer.FONT_HEIGHT + 4.0F);
            float f5 = f2 + f3 + 8.0F + fontRenderer.getStringWidth(str2) + f2;
            float f6 = Math.max(24.0F, fontRenderer.FONT_HEIGHT + 12.0F);
            float f7 = sr.getScaledWidth() / 2.0F - f5 / 2.0F;
            float f8 = sr.getScaledHeight() - 80.0F;
            ResetColorHelper.handleFloat2(f7, f8, f7 + f5, f8 + f6, f6 / 2.0F, -535818222);
            ResetColorHelper.handleFloat2(
                f7 + f2, f8 + (f6 - f4) / 2.0F, f7 + f2 + f3, f8 + (f6 + f4) / 2.0F, f4 / 2.0F, -15066594
            );
            fontRenderer.drawString(str, f7 + f2 + 7.0F, f8 + (f6 - fontRenderer.FONT_HEIGHT) / 2.0F, -1, false);
            fontRenderer.drawString(str2, f7 + f2 + f3 + 8.0F, f8 + (f6 - fontRenderer.FONT_HEIGHT) / 2.0F, -1513240, false);
        }
    }

    private void process3() {
        if (this.count > 0 && this.count2 != Integer.MAX_VALUE) {
            long now = System.currentTimeMillis();
            if (!(now - this.timestamp < this.notifyIntervalSetting.computeDouble() * 1000.0)) {
                for (EntityPlayer player : EntityCache.getEntries()) {
                    if (player != mc.thePlayer && (!this.ignoreTeammatesSetting.isEnabled() || !PlayerUtils.matchesEntityPlayer(player))) {
                        double distanceSqToCenter = player.getDistanceSqToCenter(this.blockPos);
                        if (!(distanceSqToCenter > this.alertRangeSetting.computeDouble() * this.alertRangeSetting.computeDouble())) {
                            String name = player.getName();
                            int orDefault = this.lookupMap.getOrDefault(name, 0);
                            if (!(orDefault >= this.maxAlertsEnemySetting.computeDouble())) {
                                this.lookupMap.put(name, orDefault + 1);
                                if (this.notifyPartySetting.isEnabled()) {
                                    mc.thePlayer.sendChatMessage("/pc " + name + " approaching bed (" + (int)Math.sqrt(distanceSqToCenter) + "m)");
                                }
                            }
                        }
                    }
                }

                this.timestamp = now;
            }
        }
    }
}
