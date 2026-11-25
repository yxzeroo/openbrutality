package org.brutality.module.impl.pit.hypixel.automation;

import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;

public class AutoFish extends Module {
    private final BooleanSetting alertsSetting = new BooleanSetting("Alerts", this, true);
    private final NumberSetting failChanceSetting = new NumberSetting("Fail Chance %", this, 3.0, 0.0, 100.0, 1.0);
    private final BooleanSetting randomMoveSetting = new BooleanSetting("Random Move", this, false);
    private final BooleanSetting sneakMoveSetting = new BooleanSetting("Sneak Move", this, true);
    private final NumberSetting minMoveCooldownSetting = new NumberSetting("Min Move Cooldown", this, 60000.0, 10000.0, 300000.0, 5000.0);
    private final NumberSetting maxMoveCooldownSetting = new NumberSetting("Max Move Cooldown", this, 150000.0, 20000.0, 600000.0, 5000.0);
    private final NumberSetting moveDurationSetting = new NumberSetting("Move Duration", this, 250.0, 50.0, 2000.0, 50.0);
    private boolean flag;
    private long timestamp;
    private long timestamp2;
    private long timestamp3;
    private boolean flag2 = true;
    private final Random random = new Random();
    private volatile long timestamp4;
    private ScheduledFuture<?> scheduledFuture;
    private ScheduledFuture<?> scheduledFuture2;

    @Override
    public void onEnable() {
        this.timestamp4++;
        this.process3();
        this.timestamp2 = System.currentTimeMillis() + (long)this.minMoveCooldownSetting.computeDouble();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (mc.thePlayer != null) {
                if (event instanceof EventTick) {
                    if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() == Items.fishing_rod) {
                        long now = System.currentTimeMillis();
                        if (this.randomMoveSetting.isEnabled() && mc.currentScreen == null) {
                            if (now >= this.timestamp2) {
                                this.timestamp2 = now
                                    + (long)this.minMoveCooldownSetting.computeDouble()
                                    + this.random.nextInt((int)(this.maxMoveCooldownSetting.computeDouble() - this.minMoveCooldownSetting.computeDouble()));
                                this.timestamp3 = now + (long)this.moveDurationSetting.computeDouble();
                                this.flag2 = !this.flag2;
                            }

                            if (now < this.timestamp3) {
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
                                if (this.sneakMoveSetting.isEnabled()) {
                                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
                                }

                                KeyBinding.setKeyBindState(
                                    this.flag2 ? mc.gameSettings.keyBindLeft.getKeyCode() : mc.gameSettings.keyBindRight.getKeyCode(), true
                                );
                            } else if (now < this.timestamp3 + 100L) {
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
                            }
                        }

                        EntityFishHook fishEntity = mc.thePlayer.fishEntity;
                        if (fishEntity != null && fishEntity.isInWater()) {
                            boolean fishBiting = fishEntity.isFishBiting();
                            if (!fishBiting) {
                                this.flag = false;
                            } else {
                                if (fishBiting && !this.flag) {
                                    this.flag = true;
                                    if (now - this.timestamp < 2000L) {
                                        return;
                                    }

                                    this.timestamp = now;
                                    int n = 40 + this.random.nextInt(100);
                                    if (this.random.nextInt(100) < (int)this.failChanceSetting.computeDouble()) {
                                        n += 750 + this.random.nextInt(600);
                                    }

                                    long l = this.timestamp4;
                                    int entityId = fishEntity.getEntityId();
                                    this.scheduledFuture = Brutality.scheduledExecutorService
                                        .schedule(
                                            () -> mc.addScheduledTask(
                                                () -> {
                                                    if (this.checkState44()
                                                        && l == this.timestamp4
                                                        && mc.thePlayer != null
                                                        && mc.thePlayer.fishEntity != null
                                                        && mc.thePlayer.fishEntity.getEntityId() == entityId) {
                                                        int keyCode = mc.gameSettings.keyBindUseItem.getKeyCode();
                                                        KeyBinding.setKeyBindState(keyCode, true);
                                                        KeyBinding.onTick(keyCode);
                                                        this.scheduledFuture2 = Brutality.scheduledExecutorService.schedule(() -> mc.addScheduledTask(() -> {
                                                            if (l == this.timestamp4 || !this.checkState44()) {
                                                                KeyBinding.setKeyBindState(keyCode, false);
                                                            }
                                                        }), 50L, TimeUnit.MILLISECONDS);
                                                    }
                                                }
                                            ),
                                            (long)n,
                                            TimeUnit.MILLISECONDS
                                        );
                                }
                            }
                        } else {
                            this.process3();
                        }
                    } else {
                        this.process3();
                    }
                }
            }
        }
    }

    private void process() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
    }

    @Override
    public void onDisable() {
        this.timestamp4++;
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(false);
        }

        if (this.scheduledFuture2 != null) {
            this.scheduledFuture2.cancel(false);
        }

        this.scheduledFuture = this.scheduledFuture2 = null;
        this.process();
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
        this.process3();
    }

    private void process3() {
        this.flag = false;
    }

    public AutoFish() {
        super("AutoFish", 0, Category.PIT);
        this.handleSettingArray(
            new Setting[]{
                this.alertsSetting,
                this.failChanceSetting,
                this.randomMoveSetting,
                this.sneakMoveSetting,
                this.minMoveCooldownSetting,
                this.maxMoveCooldownSetting,
                this.moveDurationSetting
            }
        );
    }
}
