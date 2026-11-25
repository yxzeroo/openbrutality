package org.brutality.module.impl.movement;

import java.util.Arrays;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.brutality.event.Event;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.player.PlayerEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.network.NetworkHelper;

public class NoFall extends Module {
    private final ModeSetting ncpSetting = new ModeSetting("Bypass", this, "Packet", Arrays.asList("Packet", "Watchdog A", "Watchdog B", "AAC", "NCP"));
    private boolean flag;
    private double amount;
    private boolean flag2;
    private double amount2;
    private double amount3;
    private boolean flag3;
    private double amount4 = 1.0;

    public NoFall() {
        super("No fall", 0, Category.MOVEMENT);
        this.handleSettingArray(new Setting[]{this.ncpSetting});
    }

    private void process4() {
        if (this.checkState()) {
            this.process3();
        } else {
            if (mc.thePlayer.fallDistance >= 3.0F) {
                this.flag3 = true;
            }

            if (this.flag3) {
                double d = mc.thePlayer.posY + mc.thePlayer.motionY;
                double d2 = this.amount2 - d;
                if (mc.thePlayer.motionY < -2.0) {
                    this.amount3 = 4.0;
                } else {
                    this.amount3 = 3.0;
                }

                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    if (mc.thePlayer.motionY < -0.01) {
                        this.amount4 = 0.64;
                    }

                    if (mc.thePlayer.motionY < -1.0) {
                        this.amount4 = 0.53;
                    }

                    if (mc.thePlayer.motionY < -1.6) {
                        this.amount4 = 0.46;
                    }

                    if (mc.thePlayer.motionY < -2.1) {
                        this.amount4 = 0.41;
                    }

                    mc.timer.timerSpeed = (float)this.amount4;
                }

                if (d2 >= this.amount3) {
                    NetworkHelper.handlePacket2(new C03PacketPlayer(true));
                    this.amount2 = mc.thePlayer.posY;
                }
            }
        }
    }

    private void process3() {
        mc.timer.timerSpeed = 1.0F;
        this.amount2 = mc.thePlayer.posY;
        this.flag3 = false;
        this.amount4 = 1.0;
        this.flag2 = false;
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            this.amount2 = mc.thePlayer != null ? mc.thePlayer.posY : 0.0;
            this.flag3 = false;
            this.amount4 = 1.0;
            this.flag2 = false;
            super.onEnable();
        }
    }

    private boolean checkState() {
        if (mc.thePlayer.onGround) {
            return true;
        }
        return mc.thePlayer.isInWater()
                || mc.thePlayer.isInLava()
                || mc.thePlayer.isInWeb
                || mc.thePlayer.isOnLadder()
                || mc.thePlayer.capabilities.isFlying
                || mc.thePlayer.capabilities.isCreativeMode
            ? true
            : mc.thePlayer.motionY > -0.0784;
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
        super.onDisable();
    }

    private void process() {
        if (mc.thePlayer.fallDistance > 3.5 && !this.checkState2()) {
            this.flag2 = true;
        } else if (this.flag2 && mc.thePlayer.onGround) {
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.1, mc.thePlayer.posZ);
            mc.timer.timerSpeed = 1.0F;
            this.flag2 = false;
        }
    }

    private boolean checkState2() {
        for (int i = (int)mc.thePlayer.posY; i > 0; i--) {
            if (!mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX, (double)i, mc.thePlayer.posZ))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventPreMotion) {
                    if (mc.theWorld == null || mc.thePlayer == null) {
                        return;
                    }

                    if (this.ncpSetting.getText().equals("AAC")
                        && mc.thePlayer.onGround
                        && !mc.thePlayer.isOnLadder()
                        && !mc.thePlayer.isInWater()
                        && !mc.thePlayer.isInLava()) {
                        mc.thePlayer.motionY = -6.0;
                    }

                    if (this.ncpSetting.getText().equals("Packet") && mc.thePlayer.fallDistance >= 3.0F) {
                        NetworkHelper.handlePacket2(new C03PacketPlayer(true));
                    }

                    if (this.ncpSetting.getText().equals("Watchdog A")) {
                        this.amount = mc.thePlayer.onGround
                            ? 0.0
                            : (this.amount = this.amount + (float)Math.max(mc.thePlayer.lastTickPosY - mc.thePlayer.posY, 0.0));
                        if (this.amount >= 3.0 && mc.thePlayer.fallDistance >= 3.0F && mc.thePlayer.motionY < 0.0) {
                            NetworkHelper.handlePacket2(new C03PacketPlayer(true));
                            mc.timer.timerSpeed = 0.5F;
                            this.flag = true;
                            this.amount = 0.0;
                        } else if (this.flag) {
                            mc.timer.timerSpeed = 1.0F;
                            this.flag = false;
                        }
                    }

                    if (this.ncpSetting.getText().equals("Watchdog B")) {
                        this.process4();
                    }

                    if (this.ncpSetting.getText().equals("NCP")) {
                        this.process();
                    }
                }

                if (event instanceof PlayerEvent && this.ncpSetting.matchesString2("Verus") && mc.thePlayer.fallDistance > 2.0F) {
                    AxisAlignedBB box = AxisAlignedBB.fromBounds(-5.0, -1.0, -5.0, 5.0, 1.0, 5.0)
                        .offset(((PlayerEvent)event).getBlockPos().getX(), ((PlayerEvent)event).getBlockPos().getY(), ((PlayerEvent)event).getBlockPos().getZ());
                    ((PlayerEvent)event).setBoundingBox(box);
                }
            }
        }
    }
}
