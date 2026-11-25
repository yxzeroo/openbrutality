package org.brutality.module.impl.movement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.Generated;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.potion.Potion;
import net.minecraft.util.Vec3;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.attack.GetDamageEvent;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.IsSprintingEvent;
import org.brutality.event.impl.movement.MovementEvent;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.client.LagbackDebug;
import org.brutality.module.impl.combat.CombatModule;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.util.inventory.MysticItemInfo;
import org.brutality.util.inventory.VenomType;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.player.SomberHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class KeepSprint extends Module {
    private static final Set<String> itemSet = new HashSet<>(
        Arrays.asList(
            "venom",
            "misery",
            "spite",
            "needless_suffering",
            "grim_reaper",
            "nostalgia",
            "hedge_fund",
            "heartripper",
            "sanguisuge",
            "lycanthropy",
            "mind_assault",
            "golden_handcuffs"
        )
    );
    private final ModeSetting watchdog3Setting = new ModeSetting("Mode", this, "Watchdog", Arrays.asList("Normal", "Watchdog", "Watchdog 2", "Watchdog 3"));
    private final BooleanSetting darksOnlySetting = new BooleanSetting("Darks Only", this, false);
    private final BooleanSetting stopOnHurtSetting = new BooleanSetting("Stop On Hurt", this, false);
    private final BooleanSetting disableWhileStandingStillSetting = new BooleanSetting("Disable While Standing Still", this, false);
    private final BooleanSetting keepMotionSetting = new BooleanSetting("Keep Motion", this, true)
        .computeBooleanSetting(() -> this.watchdog3Setting.matchesString2("Normal"));
    private final NumberSetting motionSetting = new NumberSetting("Motion", this, 1.0, 0.1, 1.0, 0.01)
        .computeNumberSetting(() -> this.watchdog3Setting.matchesString2("Normal"));
    private final ModeSetting attackTimeSetting = new ModeSetting("Sprint Check", this, "Tick Start", Arrays.asList("Tick Start", "Attack Time"))
        .computeModeSetting(() -> this.watchdog3Setting.matchesString2("Watchdog"));
    private final BooleanSetting onlyJumpBoostSetting = new BooleanSetting("Only Jump Boost", this, false)
        .computeBooleanSetting(() -> this.watchdog3Setting.matchesString2("Watchdog"));
    private final BooleanSetting airOnlySetting = new BooleanSetting("Air Only", this, true)
        .computeBooleanSetting(() -> this.watchdog3Setting.matchesString2("Watchdog"));
    private final BooleanSetting onlyWhenTargetNotLookingSetting = new BooleanSetting("Only When Target Not Looking", this, false);
    private final BooleanSetting disableAirCancelSetting = new BooleanSetting("Disable Air Cancel", this, false)
        .computeBooleanSetting(() -> this.watchdog3Setting.matchesString2("Watchdog"));
    private final NumberSetting suppressTicksSetting = new NumberSetting("Suppress Ticks", this, 0.0, 0.0, 5.0, 1.0).computeNumberSetting(this::z);
    private final BooleanSetting debugSetting = new BooleanSetting("Debug", this, false)
        .computeBooleanSetting(() -> !this.watchdog3Setting.matchesString2("Normal"));
    public static KeepSprint instance;
    private volatile boolean flag = false;
    private static boolean flag2 = false;
    private volatile boolean flag3 = false;
    private boolean flag4 = false;
    private int count = 0;
    public static volatile boolean flag5 = false;
    private boolean flag6;
    private Entity entity;
    private int count2;
    private int count3 = -1;
    private int count4;
    private boolean flag7;
    private int count5;

    public boolean checkState14() {
        return this.watchdog3Setting.matchesString2("Watchdog 3") && this.checkState13();
    }

    public static boolean matchesEntityPlayer(EntityPlayer player) {
        if (player != null && player.inventory != null) {
            ItemStack stack = player.inventory.armorInventory[1];
            if (stack == null) {
                return false;
            }
            if (SomberHelper.matchesEntityPlayer3(player)) {
                return true;
            }
            try {
                MysticItemInfo mysticItemInfo = new MysticItemInfo(stack);

                for (VenomType venomType : mysticItemInfo.lookupMap2.keySet()) {
                    if (itemSet.contains(venomType.getInternalId())) {
                        return true;
                    }
                }
            } catch (Exception ignored) {
            }

            return false;
        }
        return false;
    }

    public KeepSprint() {
        super("KeepSprint", 0, Category.MOVEMENT);
        this.handleSettingArray(
            new Setting[]{
                this.watchdog3Setting,
                this.darksOnlySetting,
                this.stopOnHurtSetting,
                this.disableWhileStandingStillSetting,
                this.keepMotionSetting,
                this.motionSetting,
                this.onlyJumpBoostSetting,
                this.airOnlySetting,
                this.onlyWhenTargetNotLookingSetting,
                this.disableAirCancelSetting,
                this.suppressTicksSetting,
                this.attackTimeSetting,
                this.debugSetting
            }
        );
        instance = this;
    }

    private void onEvent2(Event event) {
        if (event instanceof EventTick) {
            this.onEventTick((EventTick)event);
        } else if (event instanceof GetDamageEvent) {
            this.onGetDamageEvent((GetDamageEvent)event);
        } else if (event instanceof EventPacketSend) {
            this.onEventPacketSend((EventPacketSend)event);
        }
    }

    private boolean checkState11() {
        boolean flag = mc.thePlayer == null || mc.thePlayer.movementInput == null || mc.gameSettings == null;
        if (!flag && !mc.thePlayer.isRiding() && !mc.thePlayer.movementInput.sneak && !mc.gameSettings.keyBindSneak.isKeyDown() && !this.checkState15()) {
            return false;
        }
        this.flag3 = false;
        this.flag4 = false;
        this.flag = false;
        this.count = 0;
        flag5 = false;
        flag2 = false;
        if (mc.gameSettings != null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
        }

        if (!flag) {
            mc.thePlayer.setSprinting(false);
        }

        return true;
    }

    public static boolean checkState12() {
        return instance != null && instance.checkState44() && instance.watchdog3Setting.matchesString2("Watchdog 2") && instance.debugSetting.isEnabled();
    }

    private void process5() {
        if (this.flag6) {
            this.flag6 = false;
            if (mc.gameSettings != null && (Sprint.instance == null || !Sprint.instance.checkState44())) {
                mc.gameSettings.keyBindSprint.pressed = matchesInt(mc.gameSettings.keyBindSprint.getKeyCode());
            }

            this.handleString("midair sprint hold released");
        }
    }

    private boolean checkState16() {
        if (mc.thePlayer == null || mc.thePlayer.movementInput == null || mc.gameSettings == null) {
            return false;
        }
        if (mc.thePlayer.isRiding() || mc.thePlayer.movementInput.sneak || mc.gameSettings.keyBindSneak.isKeyDown() || this.checkState15()) {
            return false;
        }
        if (this.darksOnlySetting.isEnabled() && !this.checkState5()) {
            return false;
        }
        if (this.stopOnHurtSetting.isEnabled() && mc.thePlayer.hurtTime > 0) {
            return false;
        }
        if (!this.onlyWhenTargetNotLookingSetting.isEnabled()) {
            return true;
        }
        EntityLivingBase entityLivingBase2 = KillAura.entityLivingBase2;
        if (entityLivingBase2 instanceof EntityPlayer && entityLivingBase2 != mc.thePlayer) {
            EntityPlayer player = (EntityPlayer)entityLivingBase2;
            Vec3 positionEyes = player.getPositionEyes(1.0F);
            Vec3 positionEyes2 = mc.thePlayer.getPositionEyes(1.0F);
            Vec3 vec = positionEyes2.subtract(positionEyes).normalize();
            Vec3 vec2 = player.getLookVec().normalize();
            return vec2.dotProduct(vec) < 0.75;
        }
        return true;
    }

    private boolean checkState15() {
        return this.disableWhileStandingStillSetting.isEnabled()
            && mc.thePlayer.movementInput.moveForward == 0.0F
            && mc.thePlayer.movementInput.moveStrafe == 0.0F;
    }

    @Generated
    public static KeepSprint getInstance() {
        return instance;
    }

    private void handleString(String str) {
        if (this.debugSetting.isEnabled() && mc.thePlayer != null) {
            GetCurrentScreenHelper.handleString3("§d[KeepSprint] §f" + str);
        }
    }

    @Override
    public void onDisable() {
        this.process();
        super.onDisable();
        this.process5();
        flag2 = false;
        this.flag = false;
        this.flag3 = false;
        this.flag4 = false;
        if (mc.thePlayer != null && mc.theWorld != null && flag5) {
            flag5 = false;
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), matchesInt(mc.gameSettings.keyBindSprint.getKeyCode()));
        }
    }

    private void process3() {
        if (!this.checkState13()) {
            this.process5();
        } else {
            if (!this.flag6) {
                this.handleString("midair sprint stopped; awaiting server sprint stop");
            }

            this.flag6 = true;
            mc.gameSettings.keyBindSprint.pressed = false;
            mc.thePlayer.resetSprintToggleTimer();
            mc.thePlayer.setSprinting(false);
        }
    }

    private boolean checkState2() {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return false;
        }
        if (!this.checkState16()) {
            return false;
        }
        if (this.onlyJumpBoostSetting.isEnabled() && !this.checkState9()) {
            return false;
        }
        return !this.disableAirCancelSetting.isEnabled() && this.checkState8() && !mc.thePlayer.onGround
            ? true
            : !this.airOnlySetting.isEnabled()
                && mc.thePlayer.isSprinting()
                && Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())
                && mc.thePlayer.onGround;
    }

    public void process4() {
        if (this.entity != null && mc.thePlayer != null) {
            this.count3 = mc.thePlayer.ticksExisted;
        }
    }

    private void onEventTick2(EventTick event) {
        if (mc.currentScreen == null) {
            if (event.getState() == EventState.PRE) {
                this.flag = !mc.thePlayer.isCollidedHorizontally
                    && !mc.thePlayer.isSneaking()
                    && !mc.thePlayer.isRidingHorse()
                    && !mc.thePlayer.isUsingItem()
                    && mc.thePlayer.isSprinting();
            }

            if (event.getState() == EventState.POST && flag2 && mc.thePlayer.onGround) {
                flag2 = false;
                int keyCode = mc.gameSettings.keyBindSprint.getKeyCode();
                KeyBinding.setKeyBindState(keyCode, matchesInt(keyCode));
            }
        }
    }

    private boolean checkState4() {
        return (
                this.watchdog3Setting.matchesString2("Watchdog")
                    || this.watchdog3Setting.matchesString2("Watchdog 2")
                    || this.watchdog3Setting.matchesString2("Watchdog 3")
            )
            && KillAura.instance != null
            && KillAura.instance.checkState14();
    }

    private void onEventPacketSend(EventPacketSend event) {
        if (event.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = (C02PacketUseEntity)event.getPacket();
            if (packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
                try {
                    Entity entityFromWorld = packet.getEntityFromWorld(mc.theWorld);
                    if (!(entityFromWorld instanceof EntityPlayer)) {
                        return;
                    }
                } catch (Exception e) {
                    return;
                }

                if (this.attackTimeSetting.matchesString2("Attack Time")) {
                    this.flag3 = this.checkState2();
                }

                if (this.flag3) {
                    event.setCancelled(true);
                    this.handleString("C02 dropped");
                }
            }
        }
    }

    public boolean checkState() {
        if (this.checkState44() && this.watchdog3Setting.matchesString2("Watchdog 3") && !this.checkState4() && mc.thePlayer != null) {
            this.process6();
            return mc.thePlayer.ticksExisted < this.count4
                || this.entity != null
                    && (
                        this.count3 >= 0
                            || this.flag7
                            || this.count5 < 2
                            || mc.thePlayer.isSprinting()
                            || mc.thePlayer.serverSprintState
                            || mc.thePlayer.hasRecentSprintMovement()
                    );
        }
        return this.watchdog3Setting.matchesString2("Watchdog 2")
            && this.checkState13()
            && (mc.thePlayer.isSprinting() || mc.thePlayer.serverSprintState || mc.thePlayer.hasRecentSprintMovement());
    }

    private void process() {
        this.entity = null;
        this.count3 = -1;
        this.count4 = 0;
        this.count5 = 0;
        this.flag7 = false;
    }

    private void onEvent3(Event event) {
        if (event instanceof EventTick) {
            this.onEventTick2((EventTick)event);
        } else if (event instanceof MovementEvent) {
            this.onMovementEvent((MovementEvent)event);
        }
    }

    public boolean checkState6() {
        return this.checkState44() && this.watchdog3Setting.matchesString2("Watchdog 3") && this.entity != null;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
            if (false) {
                this.handleFlag3(false);
            } else if (mc.thePlayer != null && mc.theWorld != null) {
                this.process6();
                if (checkState12() && event instanceof EventUpdate) {
                    LagbackDebug.onEventUpdate((EventUpdate)event);
                }

                if (this.watchdog3Setting == null
                    || !this.watchdog3Setting.matchesString2("Watchdog 2")
                    || event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                    if (!this.checkState11()) {
                        if (this.checkState4()) {
                            this.flag3 = false;
                            this.flag4 = false;
                            this.count = 0;
                            this.process5();
                            if (flag5) {
                                flag5 = false;
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), matchesInt(mc.gameSettings.keyBindSprint.getKeyCode()));
                            }
                        } else if (!this.watchdog3Setting.matchesString2("Watchdog 2") && !this.watchdog3Setting.matchesString2("Watchdog 3")) {
                            this.process5();
                            if (this.watchdog3Setting.matchesString2("Watchdog")) {
                                this.onEvent2(event);
                            } else {
                                this.onEvent3(event);
                            }
                        } else {
                            this.flag3 = false;
                            flag5 = false;
                            this.count = 0;
                            if ((!(event instanceof EventUpdate) || ((EventUpdate)event).getState() != EventState.PRE)
                                && (
                                    !this.watchdog3Setting.matchesString2("Watchdog 3")
                                        || !(event instanceof EventTick)
                                        || ((EventTick)event).getState() != EventState.PRE
                                )) {
                                if (this.watchdog3Setting.matchesString2("Watchdog 3") && event instanceof IsSprintingEvent && this.checkState13()) {
                                    ((IsSprintingEvent)event).setSprinting(false);
                                    this.process3();
                                }
                            } else {
                                this.process3();
                            }
                        }
                    }
                }
            }
        }
    }

    private void process6() {
        if (this.entity != null) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                int ticksExisted = mc.thePlayer.ticksExisted;
                if (!this.watchdog3Setting.matchesString2("Watchdog 3")
                    || !this.checkState16()
                    || this.checkState4()
                    || mc.currentScreen != null
                    || KillAura.instance == null
                    || !KillAura.instance.checkState44()
                    || KillAura.entityLivingBase2 != this.entity
                    || this.entity.isDead
                    || mc.thePlayer.isGroundedForSprintDecision()
                    || ticksExisted < this.count2
                    || ticksExisted - this.count2 >= 8
                    || this.count3 >= 0 && ticksExisted > this.count3) {
                    this.entity = null;
                    this.count3 = -1;
                    this.count4 = ticksExisted + 2;
                    this.process5();
                }
            } else {
                this.process();
                this.process5();
            }
        }
    }

    public boolean matchesEntity(Entity entity) {
        if (this.checkState44()
            && this.watchdog3Setting.matchesString2("Watchdog 3")
            // VENDOR-DISABLED: && RuntimeAuthGuard.checkState2()
            && true
            && mc.thePlayer != null
            && !this.checkState4()) {
            this.process6();
            if (!this.checkState16() || mc.thePlayer.isGroundedForSprintDecision()) {
                return true;
            }
            if (mc.thePlayer.ticksExisted < this.count4) {
                return false;
            }
            if (this.entity == null) {
                this.entity = entity;
                this.count2 = mc.thePlayer.ticksExisted;
                this.count3 = -1;
                this.flag7 = mc.thePlayer.serverSprintState;
                this.count5 = 0;
                this.process3();
                return false;
            }
            return this.entity == entity && !this.checkState();
        }
        return true;
    }

    private boolean checkState8() {
        return this.attackTimeSetting.matchesString2("Attack Time") ? mc.thePlayer.isSprinting() : this.flag4;
    }

    private void onEventTick(EventTick event) {
        if (!this.checkState11()) {
            if (event.getState() == EventState.PRE) {
                this.flag4 = mc.thePlayer.isSprinting();
                this.flag3 = this.checkState2();
                if (this.count > 0) {
                    this.count--;
                }

                if (!flag5 && this.count == 0) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
                }
            } else if (event.getState() == EventState.POST && flag5 && mc.thePlayer.onGround && this.count == 0) {
                flag5 = false;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), matchesInt(mc.gameSettings.keyBindSprint.getKeyCode()));
                this.handleString("landed, sprint restored");
            }
        }
    }

    private boolean checkState5() {
        return matchesEntityPlayer(mc.thePlayer);
    }

    public static boolean matchesPacket(Packet<?> packet) {
        return instance != null
            && instance.checkState14()
            && packet instanceof C0BPacketEntityAction
            && ((C0BPacketEntityAction)packet).getAction() == Action.START_SPRINTING;
    }

    private boolean z() {
        return this.watchdog3Setting.matchesString2("Watchdog");
    }

    private void onMovementEvent(MovementEvent movementEvent) {
        if (!CombatModule.checkState()) {
            if (this.checkState16()) {
                movementEvent.setFlag(true);
                movementEvent.setFlag2(this.keepMotionSetting.isEnabled());
                if (!this.keepMotionSetting.isEnabled()) {
                    movementEvent.setFactor((float)this.motionSetting.computeDouble());
                }
            }
        }
    }

    @Override
    public void onEnable() {
        this.process();
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            flag2 = false;
            this.flag = false;
            this.flag3 = false;
            this.flag4 = false;
        }
    }

    private void onGetDamageEvent(GetDamageEvent getDamageEvent) {
        if (getDamageEvent.entity == mc.thePlayer) {
            if (getDamageEvent.entity2 instanceof EntityPlayer) {
                boolean flag = this.checkState2();
                this.flag3 = flag;
                if (flag) {
                    getDamageEvent.cancel();
                    if (this.checkState8() && !mc.thePlayer.onGround) {
                        flag5 = true;
                        this.count = (int)this.suppressTicksSetting.computeDouble();
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
                        mc.thePlayer.sprintingTicksLeft = 1;
                        this.handleString("air hit suppressed, sprint reset");
                    } else {
                        flag5 = true;
                        this.count = (int)this.suppressTicksSetting.computeDouble();
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
                        mc.thePlayer.sprintingTicksLeft = 1;
                        this.handleString("ground hit suppressed");
                    }
                }
            }
        }
    }

    public boolean checkState13() {
        if (!this.checkState44() || this.checkState4()) {
            return false;
        }
        if (this.watchdog3Setting.matchesString2("Watchdog 3")) {
            this.process6();
            return this.entity != null;
        }
        return this.checkState44() && this.watchdog3Setting.matchesString2("Watchdog 2")
            ? mc.thePlayer != null
                && !mc.thePlayer.isGroundedForSprintDecision()
                && this.checkState16()
                && KillAura.instance != null
                && KillAura.instance.checkState44()
                && KillAura.entityLivingBase2 != null
            : this.checkState44() && flag5;
    }

    public void handlePacket(Packet<?> packet) {
        if (this.checkState6()) {
            if (packet instanceof C0BPacketEntityAction packet2) {
                if (packet2.getAction() == Action.START_SPRINTING) {
                    this.flag7 = true;
                    this.count5 = 0;
                } else if (packet2.getAction() == Action.STOP_SPRINTING) {
                    this.flag7 = false;
                    this.count5 = 0;
                }
            } else if (packet instanceof C03PacketPlayer) {
                if (this.flag7) {
                    this.count5 = 0;
                } else {
                    this.count5 = Math.min(2, this.count5 + 1);
                }
            } else if (packet instanceof C02PacketUseEntity packet3
                && packet3.getAction() == C02PacketUseEntity.Action.ATTACK
                && packet3.getEntityId() == this.entity.getEntityId()) {
                this.process4();
            }
        }
    }

    @Override
    public void process2() {
        this.process();
        this.process5();
        super.process2();
    }

    private boolean checkState9() {
        return mc.thePlayer.isPotionActive(Potion.jump);
    }

    private static boolean matchesInt(int n) {
        try {
            return n < 0 ? Mouse.isButtonDown(n + 100) : Keyboard.isKeyDown(n);
        } catch (Exception e) {
            return false;
        }
    }
}
