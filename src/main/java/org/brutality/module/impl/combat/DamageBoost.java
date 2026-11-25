package org.brutality.module.impl.combat;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.player.GetSpeedHelper;

public final class DamageBoost extends Module {
    private final NumberSetting speedSetting = new NumberSetting("Speed", this, 2.2, 1.1, 3.0, 0.1);
    private final BooleanSetting strafeSetting = new BooleanSetting("Strafe", this, true);
    private final NumberSetting strafeTicksSetting = new NumberSetting("Strafe Ticks", this, 3.0, 2.0, 10.0, 1.0);
    private final BooleanSetting speedOnlySetting = new BooleanSetting("Speed Only", this, false);
    private int count = 0;
    private int count2 = 0;
    private boolean flag = false;
    private double amount = 0.0;
    private boolean flag2 = false;
    private int count3 = 0;

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            this.count = 0;
            this.count2 = 0;
            this.flag = false;
            this.amount = 0.0;
            this.flag2 = false;
            this.count3 = 0;
            super.onEnable();
            Module module = Brutality.moduleManager == null ? null : Brutality.moduleManager.computeModule("Velocity");
            if (module != null && module.checkState44()) {
                module.process28();
                NotificationFeedHandler.handleString("§8[§eDamageBoost§8] §cVelocity §fdisabled");
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (!this.speedOnlySetting.isEnabled() || this.checkState()) {
                    if (event instanceof GetPacketEvent getPacketEvent && getPacketEvent.getPacket() instanceof S12PacketEntityVelocity) {
                        S12PacketEntityVelocity packet = getPacketEvent.getPacket();
                        if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                            this.count2 = this.count + 2;
                            this.amount = packet.getMotionY() / 8000.0;
                            this.flag = true;
                        }
                    }

                    if (event instanceof EventTick) {
                        this.count++;
                        if (this.flag2) {
                            this.count3++;
                            if (this.count3 >= 2 && this.count3 <= this.strafeTicksSetting.computeDouble()) {
                                double dist = Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
                                GetSpeedHelper.handleFloat2((float)dist);
                            }

                            if (this.count3 == 10) {
                                this.count3 = 0;
                                this.flag2 = false;
                            }
                        }

                        if (this.flag && this.amount > 0.2 && this.count == this.count2) {
                            double d = GetSpeedHelper.computeDouble2();
                            double d2 = d * this.speedSetting.computeDouble();
                            GetSpeedHelper.handleFloat2((float)d2);
                            if (this.strafeSetting.isEnabled()) {
                                this.flag2 = true;
                                this.count3 = 0;
                            }

                            this.flag = false;
                        }
                    }
                }
            }
        }
    }

    public DamageBoost() {
        super("DamageBoost", 0, Category.COMBAT);
        this.handleSettingArray(new Setting[]{this.speedSetting, this.strafeSetting, this.strafeTicksSetting, this.speedOnlySetting});
    }

    private boolean checkState() {
        Module module = Brutality.moduleManager == null ? null : Brutality.moduleManager.computeModule("Speed");
        return module != null && module.checkState44();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
