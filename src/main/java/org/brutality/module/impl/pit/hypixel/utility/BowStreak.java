package org.brutality.module.impl.pit.hypixel.utility;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import org.brutality.core.ConnectToServerHandler;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.util.UtilHelper6;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;

public class BowStreak extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private final BooleanSetting aimSetting = new BooleanSetting("Aim", this, true);
    private final BooleanSetting predictionSetting = new BooleanSetting("Prediction", this, false);
    private final BooleanSetting alwaysShootSetting = new BooleanSetting("Always Shoot", this, false);
    private final BooleanSetting nonsOnlySetting = new BooleanSetting("Nons Only", this, true);
    private final BooleanSetting alertsSetting = new BooleanSetting("Alerts", this, true);
    private final NumberSetting radiusSetting = new NumberSetting("Radius", this, 16.0, 8.0, 50.0, 2.0);
    private final NumberSetting chargeDelaySetting = new NumberSetting("Charge Delay", this, 24.0, 15.0, 30.0, 1.0);
    private final NumberSetting yawSpeedSetting = new NumberSetting("Yaw Speed %", this, 100.0, 10.0, 200.0, 5.0);
    private final NumberSetting pitchSpeedSetting = new NumberSetting("Pitch Speed %", this, 100.0, 10.0, 200.0, 5.0);
    private final ModeSetting nearbySetting = new ModeSetting("Target Sort", this, "Nearby", Arrays.asList("Closest", "Nearby"));
    private final BooleanSetting randomStopSetting = new BooleanSetting("Random Stop", this, true);
    private final NumberSetting stopChanceSetting = new NumberSetting("Stop Chance %", this, 5.0, 0.0, 100.0, 1.0);
    private EntityPlayer entityPlayer;
    private boolean flag;
    private int count;
    private long timestamp;
    private long timestamp2;
    private long timestamp3;
    private final Random random = new Random();

    private boolean checkState() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBow;
    }

    @Override
    public void onEnable() {
        this.entityPlayer = null;
        this.flag = false;
        this.count = 0;
    }

    private void process() {
        if (this.flag) {
            mc.thePlayer.clearItemInUse();
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
        }

        this.flag = false;
        this.count = 0;
    }

    private int computeInt(EntityPlayer player) {
        int n = 0;

        for (Object playerEntity : mc.theWorld.playerEntities) {
            EntityPlayer player2 = (EntityPlayer)playerEntity;
            if (player2 != player && !player2.isDead && !player2.isInvisible() && player2.getDistanceSqToEntity(player) <= 16.0) {
                n++;
            }
        }

        return n;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (FreshHelper.computeString(mc.theWorld) != null) {
                    if (event instanceof EventUpdate) {
                        if (GetMysticEnchantLoreHelper.checkState7()) {
                            this.process();
                            return;
                        }

                        if (GetMysticEnchantLoreHelper.computeInt2(Items.arrow) <= 0) {
                            this.process();
                            return;
                        }

                        if (FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                            this.process();
                            return;
                        }

                        if (!this.checkState()) {
                            this.process();
                            return;
                        }

                        long now = System.currentTimeMillis();
                        if (this.randomStopSetting.isEnabled()) {
                            if (now >= this.timestamp3 && this.random.nextInt(100) < (int)this.stopChanceSetting.computeDouble()) {
                                this.timestamp3 = now + 200L + this.random.nextInt(800);
                            }

                            if (now < this.timestamp3) {
                                return;
                            }
                        }

                        if (this.entityPlayer == null || this.matchesLong(now)) {
                            this.entityPlayer = this.computeEntityPlayer();
                            if (this.entityPlayer != null) {
                                this.timestamp2 = now;
                            }
                        }

                        if (this.entityPlayer == null) {
                            this.process();
                            if (!this.alwaysShootSetting.isEnabled()) {
                                return;
                            }

                            if (this.alertsSetting.isEnabled() && now - this.timestamp > 20000L) {
                                this.timestamp = now;
                            }

                            return;
                        }

                        this.timestamp = now;
                        if (this.aimSetting.isEnabled()) {
                            this.process3();
                        }

                        if (!this.flag) {
                            ItemStack heldItem = mc.thePlayer.getHeldItem();
                            if (heldItem != null && heldItem.getItem() instanceof ItemBow) {
                                mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, heldItem);
                                this.flag = true;
                                this.count = 0;
                            }

                            return;
                        }

                        this.count++;
                        if (this.count >= (int)this.chargeDelaySetting.computeDouble()) {
                            ItemStack heldItem2 = mc.thePlayer.getHeldItem();
                            if (heldItem2 != null && heldItem2.getItem() instanceof ItemBow) {
                                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                heldItem2.getItem().onPlayerStoppedUsing(heldItem2, mc.theWorld, mc.thePlayer, 0);
                            }

                            this.flag = false;
                            this.count = 0;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
    }

    private void process3() {
        EntityPlayer entityPlayer = this.entityPlayer;
        double x = entityPlayer.posX;
        double z = entityPlayer.posZ;
        if (this.predictionSetting.isEnabled()) {
            x += (entityPlayer.posX - entityPlayer.lastTickPosX) * 2.0;
            z += (entityPlayer.posZ - entityPlayer.lastTickPosZ) * 2.0;
        }

        double d = x - mc.thePlayer.posX;
        double d2 = z - mc.thePlayer.posZ;
        double d3 = entityPlayer.posY + entityPlayer.getEyeHeight() - 0.4 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dist = Math.sqrt(d * d + d2 * d2);
        float f = (float)(Math.atan2(d2, d) * (180.0 / Math.PI)) - 90.0F;
        double d4 = 0.006;
        double d5 = 1.0 - d4 * (d4 * dist * dist + 2.0 * d3);
        float f2;
        if (d5 >= 0.0 && dist > 0.01) {
            f2 = (float)(-Math.toDegrees(Math.atan((1.0 - Math.sqrt(d5)) / (d4 * dist))));
        } else {
            f2 = (float)(-Math.toDegrees(Math.atan2(d3, dist)));
        }

        float f3 = UtilHelper6.computeFloat(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, f, f2);
        float f4 = (float)(0.67F * (this.yawSpeedSetting.computeDouble() / 100.0) * (1.0F + f3 / 90.0F * 2.5F));
        float f5 = (float)(0.45F * (this.pitchSpeedSetting.computeDouble() / 100.0) * (1.0F + f3 / 30.0F));
        UtilHelper6.handleFloat(f, f2, f4, f5);
        mc.thePlayer.rotationPitch = MathHelper.clamp_float(mc.thePlayer.rotationPitch, -90.0F, 90.0F);
    }

    public BowStreak() {
        super("BowStreak", 0, Category.PIT);
        this.handleSettingArray(
            new Setting[]{
                this.aimSetting,
                this.predictionSetting,
                this.alwaysShootSetting,
                this.nonsOnlySetting,
                this.alertsSetting,
                this.radiusSetting,
                this.chargeDelaySetting,
                this.yawSpeedSetting,
                this.pitchSpeedSetting,
                this.nearbySetting,
                this.randomStopSetting,
                this.stopChanceSetting
            }
        );
    }

    private boolean matchesLong(long l) {
        return this.entityPlayer.isDead
            || this.entityPlayer.getHealth() <= 0.0F
            || FreshHelper.matchesWorld3(mc.theWorld, this.entityPlayer)
            || !UtilHelper6.matchesEntityPlayer(this.entityPlayer)
            || this.entityPlayer.posY > ConnectToServerHandler.computeInt4() + 4
            || mc.thePlayer.getDistanceToEntity(this.entityPlayer) > this.radiusSetting.computeDouble() * 1.5
            || l - this.timestamp2 > 5000L;
    }

    private EntityPlayer computeEntityPlayer() {
        EntityPlayer player = null;
        double MAX_VALUE = Double.MAX_VALUE;
        int n = ConnectToServerHandler.computeInt4();

        for (Object playerEntity : mc.theWorld.playerEntities) {
            EntityPlayer player2 = (EntityPlayer)playerEntity;
            if (player2 != mc.thePlayer
                && !player2.isDead
                && !(player2.getHealth() <= 0.0F)
                && !player2.isInvisible()
                && !FreshHelper.matchesWorld3(mc.theWorld, player2)
                && !(player2.posY > n + 4)
                && UtilHelper6.matchesEntityPlayer(player2)) {
                double dist = mc.thePlayer.getDistanceToEntity(player2);
                if (!(dist > this.radiusSetting.computeDouble())
                    && (!this.nonsOnlySetting.isEnabled() || !GetMysticEnchantLoreHelper.matchesEntityPlayer10(player2))) {
                    if (this.nearbySetting.getText().equals("Nearby")) {
                        int n2 = this.computeInt(player2);
                        double d = ~n2 + 1;
                        if (d < MAX_VALUE) {
                            MAX_VALUE = d;
                            player = player2;
                        }
                    } else if (dist < MAX_VALUE) {
                        MAX_VALUE = dist;
                        player = player2;
                    }
                }
            }
        }

        return player;
    }
}
