package org.brutality.module.impl.movement;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.MovementEvent5;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;

public final class FreeCam extends Module {
    public final NumberSetting speedSetting = new NumberSetting("Speed", this, 2.5, 0.5, 10.0, 0.5);
    private final BooleanSetting disableOnDamageSetting = new BooleanSetting("Disable on damage", this, true);
    public static EntityOtherPlayerMP entityOtherPlayerMP;
    private int count;

    @Override
    public void onDisable() {
        if (entityOtherPlayerMP != null) {
            mc.gameSettings.thirdPersonView = this.count;
        }

        entityOtherPlayerMP = null;
        mc.setRenderViewEntity(mc.thePlayer);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent4) {
                this.handleFlag3(false);
            } else if (entityOtherPlayerMP != null && mc.thePlayer != null) {
                if (event instanceof MovementEvent5 movementEvent5) {
                    movementEvent5.setFactor(0.0F);
                    movementEvent5.setFactor2(0.0F);
                    movementEvent5.setJump(false);
                    movementEvent5.setSneak(false);
                }

                if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                    if (this.disableOnDamageSetting.isEnabled() && mc.thePlayer.hurtTime > 0) {
                        this.handleFlag3(false);
                        return;
                    }

                    entityOtherPlayerMP.lastTickPosX = entityOtherPlayerMP.prevPosX = entityOtherPlayerMP.posX;
                    entityOtherPlayerMP.lastTickPosY = entityOtherPlayerMP.prevPosY = entityOtherPlayerMP.posY;
                    entityOtherPlayerMP.lastTickPosZ = entityOtherPlayerMP.prevPosZ = entityOtherPlayerMP.posZ;
                    entityOtherPlayerMP.prevRotationYaw = entityOtherPlayerMP.rotationYaw;
                    entityOtherPlayerMP.prevRotationPitch = entityOtherPlayerMP.rotationPitch;
                    double d = 0.0;
                    double d2 = 0.0;
                    double d3 = 0.0;
                    if (mc.currentScreen == null) {
                        d = (mc.gameSettings.keyBindForward.isKeyDown() ? 1 : 0) - (mc.gameSettings.keyBindBack.isKeyDown() ? 1 : 0);
                        d2 = (mc.gameSettings.keyBindRight.isKeyDown() ? 1 : 0) - (mc.gameSettings.keyBindLeft.isKeyDown() ? 1 : 0);
                        d3 = (mc.gameSettings.keyBindJump.isKeyDown() ? 1 : 0) - (mc.gameSettings.keyBindSneak.isKeyDown() ? 1 : 0);
                    }

                    double d4 = Math.max(1.0, Math.sqrt(d * d + d2 * d2 + d3 * d3));
                    double d5 = 0.215 * this.speedSetting.computeDouble() / d4;
                    double rad = Math.toRadians(entityOtherPlayerMP.rotationYaw);
                    entityOtherPlayerMP.setPosition(
                        entityOtherPlayerMP.posX + (-Math.sin(rad) * d + Math.cos(rad) * d2) * d5,
                        entityOtherPlayerMP.posY + d3 * d5,
                        entityOtherPlayerMP.posZ + (Math.cos(rad) * d + Math.sin(rad) * d2) * d5
                    );
                    mc.thePlayer.setSprinting(false);
                }

                if (event instanceof EventPacketSend) {
                    Packet packet = ((EventPacketSend)event).getPacket();
                    if (packet instanceof C03PacketPlayer
                        || packet instanceof C07PacketPlayerDigging
                        || packet instanceof C08PacketPlayerBlockPlacement
                        || packet instanceof C02PacketUseEntity) {
                        event.cancel();
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        if (mc.theWorld != null && mc.thePlayer != null) {
            entityOtherPlayerMP = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
            entityOtherPlayerMP.copyLocationAndAnglesFrom(mc.thePlayer);
            entityOtherPlayerMP.lastTickPosX = entityOtherPlayerMP.prevPosX = entityOtherPlayerMP.posX;
            entityOtherPlayerMP.lastTickPosY = entityOtherPlayerMP.prevPosY = entityOtherPlayerMP.posY;
            entityOtherPlayerMP.lastTickPosZ = entityOtherPlayerMP.prevPosZ = entityOtherPlayerMP.posZ;
            entityOtherPlayerMP.prevRotationYaw = entityOtherPlayerMP.rotationYaw;
            entityOtherPlayerMP.prevRotationPitch = entityOtherPlayerMP.rotationPitch;
            entityOtherPlayerMP.setInvisible(true);
            this.count = mc.gameSettings.thirdPersonView;
            mc.gameSettings.thirdPersonView = 0;
            mc.setRenderViewEntity(entityOtherPlayerMP);
        } else {
            this.handleFlag3(false);
        }
    }

    public FreeCam() {
        super("Free Cam", 0, Category.MOVEMENT);
        this.handleSettingArray(new Setting[]{this.speedSetting, this.disableOnDamageSetting});
    }
}
