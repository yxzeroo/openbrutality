package org.brutality.module.impl.combat;

import java.util.Arrays;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.timer.TimerUtil;

public class FastBow extends Module {
    private final ModeSetting vanillaSetting = new ModeSetting("Mode", this, "Packet", Arrays.asList("Packet", "Vanilla"));
    private final NumberSetting packetsSetting = new NumberSetting("Packets", this, 20.0, 1.0, 50.0, 1.0);
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 0.0, 0.0, 1000.0, 10.0);
    private final NumberSetting minDrawTimeSetting = new NumberSetting("Min Draw Time", this, 3.0, 1.0, 20.0, 1.0);
    private final BooleanSetting onlyOnGroundSetting = new BooleanSetting("Only On Ground", this, true);
    private final BooleanSetting requireFullHealthSetting = new BooleanSetting("Require Full Health", this, false);
    private final BooleanSetting autoShootSetting = new BooleanSetting("Auto Shoot", this, false);
    private final BooleanSetting allowCreativeSetting = new BooleanSetting("Allow Creative", this, true);
    private final BooleanSetting smartDelaySetting = new BooleanSetting("Smart Delay", this, false);
    private final NumberSetting smartDelayMinSetting = new NumberSetting("Smart Delay Min", this, 50.0, 10.0, 500.0, 10.0);
    private final NumberSetting smartDelayMaxSetting = new NumberSetting("Smart Delay Max", this, 150.0, 50.0, 1000.0, 10.0);
    private final TimerUtil timerUtil = new TimerUtil();
    private final TimerUtil timerUtil2 = new TimerUtil();
    private boolean isDrawing = false;
    private int count = 0;

    public String getDisplayName() {
        return this.vanillaSetting.matchesString2("Packet")
            ? this.getName() + " §7[" + (int)this.packetsSetting.computeDouble() + "]"
            : this.getName() + " §7[" + this.vanillaSetting.getText() + "]";
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.timerUtil.reset();
            this.timerUtil2.reset();
            this.isDrawing = false;
            this.count = 0;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.isDrawing = false;
        this.count = 0;
        if (mc.thePlayer != null && mc.thePlayer.isUsingItem()) {
            mc.thePlayer.stopUsingItem();
        }
    }

    private void process3() {
        if (mc.thePlayer.isUsingItem()) {
            mc.thePlayer.stopUsingItem();
            mc.thePlayer
                .inventory
                .getCurrentItem()
                .getItem()
                .onPlayerStoppedUsing(mc.thePlayer.inventory.getCurrentItem(), mc.theWorld, mc.thePlayer, (int)this.minDrawTimeSetting.computeDouble());
        }
    }

    private long computeLong() {
        long l = (long)(
            this.smartDelayMinSetting.computeDouble() + Math.random() * (this.smartDelayMaxSetting.computeDouble() - this.smartDelayMinSetting.computeDouble())
        );
        long l2 = l + this.count * 10;
        return Math.min(l2, (long)this.smartDelayMaxSetting.computeDouble());
    }

    public FastBow() {
        super("FastBow", 0, Category.COMBAT);
        this.handleSettingArray(
            new Setting[]{
                this.vanillaSetting,
                this.packetsSetting,
                this.delaySetting,
                this.minDrawTimeSetting,
                this.onlyOnGroundSetting,
                this.requireFullHealthSetting,
                this.autoShootSetting,
                this.allowCreativeSetting,
                this.smartDelaySetting,
                this.smartDelayMinSetting,
                this.smartDelayMaxSetting
            }
        );
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventUpdate) {
                    if (!this.checkState44()) {
                        return;
                    }

                    long l = this.smartDelaySetting.isEnabled() ? this.computeLong() : (long)this.delaySetting.computeDouble();
                    if (!this.timerUtil.matchesLong(l, false)) {
                        return;
                    }

                    if (mc.thePlayer.getHealth() <= 0.0F) {
                        return;
                    }

                    if (mc.thePlayer.inventory.getCurrentItem() == null) {
                        return;
                    }

                    if (!(mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBow)) {
                        this.isDrawing = false;
                        return;
                    }

                    if (this.onlyOnGroundSetting.isEnabled()
                        && !mc.thePlayer.onGround
                        && (!this.allowCreativeSetting.isEnabled() || !mc.thePlayer.capabilities.isCreativeMode)) {
                        return;
                    }

                    if (this.requireFullHealthSetting.isEnabled() && mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth()) {
                        return;
                    }

                    boolean flag = mc.gameSettings.keyBindUseItem.pressed || this.autoShootSetting.isEnabled();
                    if (flag) {
                        if (!this.isDrawing) {
                            this.isDrawing = true;
                            this.timerUtil2.reset();
                            return;
                        }

                        if (!this.timerUtil2.matchesLong((long)(this.minDrawTimeSetting.computeDouble() * 50.0), false)) {
                            return;
                        }

                        String text = this.vanillaSetting.getText();
                        switch (text) {
                            case "Packet":
                                this.process();
                                break;
                            case "Vanilla":
                                this.process3();
                        }

                        this.isDrawing = false;
                        this.count++;
                        this.timerUtil.reset();
                    } else {
                        this.isDrawing = false;
                    }
                }
            }
        }
    }

    private void process() {
        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem());
        mc.thePlayer.inventory.getCurrentItem().getItem().onItemRightClick(mc.thePlayer.inventory.getCurrentItem(), mc.theWorld, mc.thePlayer);
        int n = (int)this.packetsSetting.computeDouble();

        for (int i = 0; i < n; i++) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(false));
        }

        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
        mc.thePlayer.inventory.getCurrentItem().getItem().onPlayerStoppedUsing(mc.thePlayer.inventory.getCurrentItem(), mc.theWorld, mc.thePlayer, n);
    }
}
