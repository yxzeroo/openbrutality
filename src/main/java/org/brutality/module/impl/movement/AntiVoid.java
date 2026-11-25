package org.brutality.module.impl.movement;

import java.util.Arrays;
import lombok.Generated;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class AntiVoid extends Module {
    private final ModeSetting ncpSetting = new ModeSetting("Mode", this, "Normal", Arrays.asList("Normal", "NCP"));
    private final NumberSetting distanceSetting = new NumberSetting("Distance", this, 5.0, 1.0, 20.0, 0.1);
    public static AntiVoid instance;
    private BlockPos blockPos;
    private boolean flag = false;
    private boolean flag2 = false;

    private boolean checkState() {
        try {
            boolean flag = Brutality.moduleManager.computeModule("Speed").checkState44();
            boolean flag2 = Brutality.moduleManager.computeModule("LongJump").checkState44();
            return flag || flag2;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventPreMotion) {
                    boolean flag = this.checkState2();
                    if (this.ncpSetting.matchesString2("Normal")) {
                        this.handleFlag(flag);
                    } else if (this.ncpSetting.matchesString2("NCP")) {
                        this.handleFlag2(flag);
                    }
                }

                if ((event instanceof EventPacketReceive || event instanceof GetPacketEvent) && this.ncpSetting.matchesString2("NCP")) {
                    this.onEvent2(event);
                }
            }
        }
    }

    private void onEvent2(Event event) {
        if (event instanceof EventPacketReceive event2) {
            if (event2.getPacket() instanceof S08PacketPlayerPosLook) {
                boolean flag = this.checkState2();
                if (mc.thePlayer.fallDistance > this.distanceSetting.computeDouble() && flag && this.blockPos != null && !this.checkState()) {
                    this.flag2 = true;
                }
            }
        } else if (event instanceof GetPacketEvent getPacketEvent && getPacketEvent.getPacket() instanceof S08PacketPlayerPosLook) {
            boolean flag2 = this.checkState2();
            if (mc.thePlayer.fallDistance > this.distanceSetting.computeDouble() && flag2 && this.blockPos != null && !this.checkState()) {
                this.flag2 = true;
            }
        }
    }

    public AntiVoid() {
        super("AntiVoid", 0, Category.MOVEMENT);
        this.handleSettingArray(new Setting[]{this.ncpSetting, this.distanceSetting});
        instance = this;
    }

    @Generated
    public static AntiVoid getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.blockPos = null;
            this.flag = false;
            this.flag2 = false;
        }
    }

    private boolean checkState2() {
        if (mc.thePlayer.posY < 0.0) {
            return true;
        }
        for (int i = 0; i < (int)mc.thePlayer.posY + 2; i += 2) {
            AxisAlignedBB box = mc.thePlayer.getEntityBoundingBox().offset(0.0, -i, 0.0);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, box).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private void handleFlag2(boolean flag) {
        if (mc.thePlayer.onGround) {
            this.blockPos = mc.thePlayer.getPosition();
        }

        if (mc.thePlayer.fallDistance > this.distanceSetting.computeDouble() && flag && this.blockPos != null && !this.checkState()) {
            mc.thePlayer.motionY = -0.09800000190734863;
            this.flag = true;
        } else {
            if (this.flag || this.flag2) {
                mc.thePlayer.motionY = -0.09800000190734863;
                this.blockPos = null;
                this.flag = false;
                this.flag2 = false;
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.blockPos = null;
        this.flag = false;
        this.flag2 = false;
    }

    private void handleFlag(boolean flag) {
        if (!flag && mc.thePlayer.onGround) {
            this.blockPos = mc.thePlayer.getPosition();
        }

        if (mc.thePlayer.fallDistance > this.distanceSetting.computeDouble() && flag && this.blockPos != null) {
            mc.thePlayer.setPosition(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ());
            mc.thePlayer.fallDistance = 0.0F;
        }
    }
}
