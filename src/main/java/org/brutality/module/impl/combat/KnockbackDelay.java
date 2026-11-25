package org.brutality.module.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.event.impl.player.EventTick;
import org.brutality.lag.apih.ApihHandler;
import org.brutality.lag.apih.InboundType;
import org.brutality.lag.timeout.TimeoutHandler2;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.exploit.Blink;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.GetCurrentScreenHelper;
import org.lwjgl.input.Mouse;

public class KnockbackDelay extends Module {
    private final NumberSetting distanceToTargetSetting = new NumberSetting("Distance to target", this, 6.0, 3.0, 12.0, 0.1);
    private final NumberSetting chanceSetting = new NumberSetting("Chance", this, 100.0, 0.0, 100.0, 1.0);
    private final NumberSetting maximumDelaySetting = new NumberSetting("Maximum delay", this, 200.0, 50.0, 1000.0, 10.0);
    private final BooleanSetting inAirSetting = new BooleanSetting("In air", this, true);
    private final BooleanSetting lookingAtPlayerSetting = new BooleanSetting("Looking at player", this, false);
    private final BooleanSetting requireLeftMouseSetting = new BooleanSetting("Require Left mouse", this, false);
    private final BooleanSetting restrictHeldItemSetting = new BooleanSetting("Restrict held item", this, false);
    private ApihHandler apihHandler;

    private void onReceivePacket(EventPacketReceive event) {
        if (GetCurrentScreenHelper.checkState2() && mc.thePlayer != null && mc.theWorld != null) {
            if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                this.process();
            } else if (event.getPacket() instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity packet = (S12PacketEntityVelocity)event.getPacket();
                if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                    if (this.computeString() == null) {
                        if (!(this.chanceSetting.computeDouble() < 100.0) || !(Math.random() * 100.0 >= this.chanceSetting.computeDouble())) {
                            if (!this.checkState2()) {
                                if (Brutality.getLagHandler() != null) {
                                    this.apihHandler = new ApihHandler(InboundType.ONLY_INBOUND, new TimeoutHandler2(this));
                                    Brutality.getLagHandler().handleApihHandler(this.apihHandler);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventPacketReceive) {
                    this.onReceivePacket((EventPacketReceive)event);
                } else if (event instanceof EventTick && ((EventTick)event).getState() == EventState.PRE) {
                    this.onTick();
                }
            }
        }
    }

    private void process() {
        if (this.apihHandler != null) {
            this.apihHandler.getTimeoutHandler().process();
            this.apihHandler = null;
        }
    }

    private String computeString() {
        double d = this.distanceToTargetSetting.computeDouble() * this.distanceToTargetSetting.computeDouble();
        if (this.computeEntityPlayer(d) == null) {
            return "no target in range";
        }
        if (this.inAirSetting.isEnabled() && mc.thePlayer.onGround) {
            return "not in air";
        }
        if (this.lookingAtPlayerSetting.isEnabled() && !this.matchesDouble(d)) {
            return "not looking at player";
        }
        if (this.requireLeftMouseSetting.isEnabled() && !Mouse.isButtonDown(0)) {
            return "LMB not held";
        }
        if (this.restrictHeldItemSetting.isEnabled()) {
            ItemStack heldItem = mc.thePlayer.getHeldItem();
            if (heldItem == null) {
                return "held item missing";
            }
        }

        return checkState() ? "inbound conflict" : null;
    }

    private boolean matchesDouble(double d) {
        if (mc.objectMouseOver == null) {
            return false;
        }
        Entity entityHit = mc.objectMouseOver.entityHit;
        return !(entityHit instanceof EntityPlayer) ? false : mc.thePlayer.getDistanceSqToEntity(entityHit) <= d;
    }

    private void onTick() {
        if (!GetCurrentScreenHelper.checkState2() || mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.isDead) {
            this.process();
        } else if (this.checkState2()) {
            if (this.computeString() != null) {
                this.process();
            } else {
                if (Brutality.getLagHandler() != null) {
                    Brutality.getLagHandler().handleInboundType(InboundType.INBOUND, (long)this.maximumDelaySetting.computeDouble());
                }
            }
        }
    }

    private static boolean checkState() {
        Blink instance = Blink.getInstance();
        return instance != null && instance.checkState44();
    }

    private boolean checkState2() {
        return this.apihHandler != null && !this.apihHandler.getTimeoutHandler().checkState2();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.process();
    }

    public KnockbackDelay() {
        super("Knockback Delay", 0, Category.COMBAT);
        this.handleSettingArray(
            new Setting[]{
                this.distanceToTargetSetting,
                this.chanceSetting,
                this.maximumDelaySetting,
                this.inAirSetting,
                this.lookingAtPlayerSetting,
                this.requireLeftMouseSetting,
                this.restrictHeldItemSetting
            }
        );
    }

    private EntityPlayer computeEntityPlayer(double d) {
        EntityPlayer player = null;
        double d2 = d;

        for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
            if (playerEntity != mc.thePlayer && !playerEntity.isDead && playerEntity.isEntityAlive()) {
                double dist = mc.thePlayer.getDistanceSqToEntity(playerEntity);
                if (dist <= d2) {
                    d2 = dist;
                    player = playerEntity;
                }
            }
        }

        return player;
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.apihHandler = null;
        }
    }
}
