package org.brutality.module.impl.player;

import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.timer.TimerUtil;

public class AutoPot extends Module {
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 2000.0, 300.0, 5000.0, 50.0);
    private final NumberSetting healHPSetting = new NumberSetting("Heal HP", this, 14.0, 0.5, 20.0, 1.0);
    private final BooleanSetting healSetting = new BooleanSetting("Heal", this, true);
    private final BooleanSetting regenSetting = new BooleanSetting("Regen", this, false);
    private final BooleanSetting jumpSetting = new BooleanSetting("Jump", this, false);
    private final BooleanSetting speedSetting = new BooleanSetting("Speed", this, false);
    private final BooleanSetting fireSetting = new BooleanSetting("Fire", this, true);
    private final TimerUtil timerUtil = new TimerUtil();
    private int count = -1;

    private int computeInt() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemPotion && ItemPotion.isSplash(stack.getItemDamage())) {
                ItemPotion item = (ItemPotion)stack.getItem();
                if (item.getEffects(stack) != null && !item.getEffects(stack).isEmpty()) {
                    PotionEffect effect = item.getEffects(stack).get(0);
                    int potionID = effect.getPotionID();
                    boolean flag = this.healSetting.isEnabled() && potionID == 6
                        || this.regenSetting.isEnabled() && potionID == 10
                        || this.jumpSetting.isEnabled() && potionID == 8
                        || this.speedSetting.isEnabled() && potionID == 1
                        || this.fireSetting.isEnabled() && potionID == 12;
                    if (flag && this.matchesInt(potionID, effect)) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventTick event2) {
                if (mc.thePlayer != null && mc.theWorld != null) {
                    if (EventState.onEvent(event2)) {
                        if (mc.thePlayer.onGround) {
                            if (!(mc.thePlayer.getHealth() >= this.healHPSetting.computeDouble())) {
                                if (this.timerUtil.matchesLong10((long)this.delaySetting.computeDouble())) {
                                    int n = this.computeInt();
                                    if (n != -1) {
                                        this.count = mc.thePlayer.inventory.currentItem;
                                        mc.thePlayer.inventory.currentItem = n;
                                        mc.playerController.syncCurrentPlayItem();
                                        float pitch = mc.thePlayer.rotationPitch;
                                        mc.getNetHandler()
                                            .addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, 85.0F, mc.thePlayer.onGround));
                                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                        mc.getNetHandler()
                                            .addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, pitch, mc.thePlayer.onGround));
                                        mc.thePlayer.inventory.currentItem = this.count;
                                        mc.playerController.syncCurrentPlayItem();
                                        this.count = -1;
                                        this.timerUtil.reset();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public AutoPot() {
        super("AutoPot", 0, Category.PLAYER);
        this.handleSettingArray(
            new Setting[]{this.delaySetting, this.healHPSetting, this.healSetting, this.regenSetting, this.jumpSetting, this.speedSetting, this.fireSetting}
        );
    }

    private boolean matchesInt(int n, PotionEffect effect) {
        if (n == 6 || n == 10) {
            return mc.thePlayer.getHealth() < this.healHPSetting.computeDouble();
        }
        if (n == 12) {
            return mc.thePlayer.isBurning();
        }
        if (!mc.thePlayer.isPotionActive(n)) {
            return true;
        }
        PotionEffect activePotionEffect = mc.thePlayer.getActivePotionEffect(Potion.potionTypes[n]);
        return activePotionEffect == null || activePotionEffect.getAmplifier() < effect.getAmplifier();
    }
}
