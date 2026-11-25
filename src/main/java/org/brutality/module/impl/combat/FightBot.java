package org.brutality.module.impl.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.event.Event;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.player.PlayerHelper5;
import org.brutality.utils.rotation.OnJumpHelper;
import org.brutality.utils.timer.TimerUtil;

public final class FightBot extends Module {
    private final List<EntityLivingBase> entries = new ArrayList<>();
    public static EntityLivingBase entityLivingBase;
    private final NumberSetting minCPSSetting = new NumberSetting("Min CPS", this, 10.0, 1.0, 20.0, 1.0);
    private final NumberSetting maxCPSSetting = new NumberSetting("Max CPS", this, 12.0, 1.0, 20.0, 1.0);
    private final NumberSetting reachSetting = new NumberSetting("Reach", this, 4.0, 3.0, 6.0, 0.1);
    private final BooleanSetting playersSetting = new BooleanSetting("Players", this, true);
    private final BooleanSetting mobsSetting = new BooleanSetting("Mobs", this, false);
    private final BooleanSetting animalsSetting = new BooleanSetting("Animals", this, false);
    private final TimerUtil timerUtil = new TimerUtil();

    public FightBot() {
        super("FightBot", 0, Category.COMBAT);
        this.handleSettingArray(
            new Setting[]{this.minCPSSetting, this.maxCPSSetting, this.reachSetting, this.playersSetting, this.mobsSetting, this.animalsSetting}
        );
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventPreMotion event2) {
                    if (this.minCPSSetting.computeDouble() > this.maxCPSSetting.computeDouble()) {
                        this.minCPSSetting.handleDouble2(this.minCPSSetting.computeDouble() - 1.0);
                    }

                    mc.gameSettings.keyBindForward.pressed = entityLivingBase != null
                        && !(mc.thePlayer.getDistanceToEntity(entityLivingBase) <= this.reachSetting.computeDouble());
                    mc.gameSettings.keyBindJump.pressed = mc.thePlayer.isCollidedHorizontally || mc.thePlayer.isInWater();
                    this.process();
                    if (!this.entries.isEmpty()) {
                        entityLivingBase = this.entries.get(0);
                        float[] floats = OnJumpHelper.computeFloatArray(entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ);
                        mc.thePlayer.rotationYaw = floats[0];
                        mc.thePlayer.rotationPitch = floats[1];
                        if (mc.thePlayer.getDistanceToEntity(entityLivingBase) <= this.reachSetting.computeDouble()
                            && this.timerUtil
                                .matchesDouble2(1000.0 / PlayerHelper5.computeDouble2(this.minCPSSetting.computeDouble(), this.maxCPSSetting.computeDouble()))) {
                            mc.playerController.attackEntity(mc.thePlayer, entityLivingBase);
                            mc.thePlayer.swingItem();
                            this.timerUtil.reset();
                        }
                    } else {
                        entityLivingBase = null;
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        entityLivingBase = null;
        mc.gameSettings.keyBindForward.pressed = false;
        mc.gameSettings.keyBindJump.pressed = false;
        this.entries.clear();
        super.onDisable();
    }

    private boolean matchesEntityLivingBase(EntityLivingBase living) {
        if (living == mc.thePlayer) {
            return false;
        }
        if (!living.isDead && !(living.getHealth() <= 0.0F)) {
            if (mc.thePlayer.getDistanceToEntity(living) > this.reachSetting.computeDouble() + 2.0) {
                return false;
            }
            if (living instanceof EntityPlayer && this.playersSetting.isEnabled()) {
                return true;
            }
            return living instanceof EntityMob && this.mobsSetting.isEnabled() ? true : living instanceof EntityAnimal && this.animalsSetting.isEnabled();
        }
        return false;
    }

    private void process() {
        this.entries.clear();

        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase living && this.matchesEntityLivingBase(living)) {
                this.entries.add(living);
            }
        }

        this.entries.sort(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity));
    }
}
