package org.brutality.module.impl.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPostMotion;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.movement.MovementFix;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.player.GetSpeedHelper;

public class Velocity extends Module {
    public static Velocity instance;
    private final Minecraft mc = Minecraft.getMinecraft();
    private ModeSetting jumpSetting = new ModeSetting(
        "Mode", this, "Packet", Arrays.asList("Packet", "Motion", "Cancel", "Matrix", "Watchdog Old", "Delay", "Grim", "Vulcan", "Reduce", "Jump")
    ) {
        @Override
        public boolean matchesString(String str) {
            return "Watchdog".equalsIgnoreCase(str) || super.matchesString(str);
        }

        @Override
        public void handleString(String str) {
            super.handleString("Watchdog".equalsIgnoreCase(str) ? "Watchdog Old" : str);
        }
    };
    public BooleanSetting onlyWhileTargetingSetting = new BooleanSetting("Only While Targeting", this, false)
        .computeBooleanSetting(() -> this.jumpSetting.matchesString2("Jump"));
    public NumberSetting minimumReductionSetting = new NumberSetting("Minimum Reduction", this, 3.0, 1.0, 50.0, 1.0)
        .computeNumberSetting(() -> this.jumpSetting.matchesString2("Jump"));
    private EntityPlayerSP entityPlayerSP;
    private int count;
    public NumberSetting horizontalSetting = new NumberSetting("Horizontal", this, 0.0, 0.0, 100.0, 1.0)
        .computeNumberSetting(() -> this.jumpSetting.matchesString2("Packet") || this.jumpSetting.matchesString2("Motion"));
    public NumberSetting verticalSetting = new NumberSetting("Vertical", this, 0.0, 0.0, 100.0, 1.0)
        .computeNumberSetting(() -> this.jumpSetting.matchesString2("Packet") || this.jumpSetting.matchesString2("Motion"));
    public NumberSetting chanceSetting = new NumberSetting("Chance", this, 100.0, 0.0, 100.0, 1.0)
        .computeNumberSetting(
            () -> !this.jumpSetting.matchesString2("Delay") && !this.jumpSetting.matchesString2("Grim") && !this.jumpSetting.matchesString2("Motion")
        );
    public NumberSetting delaySetting = new NumberSetting("Delay", this, 400.0, 0.0, 1000.0, 50.0)
        .computeNumberSetting(() -> this.jumpSetting.matchesString2("Delay"));
    public NumberSetting ticksSetting = new NumberSetting("Ticks", this, 3.0, 1.0, 3.0, 1.0).computeNumberSetting(this::z);
    public NumberSetting sprintFrictionSetting = new NumberSetting("Sprint Friction", this, 86.0, 85.0, 95.0, 1.0)
        .computeNumberSetting(() -> this.jumpSetting.matchesString2("Grim"));
    public NumberSetting walkFrictionSetting = new NumberSetting("Walk Friction", this, 88.0, 85.0, 95.0, 1.0)
        .computeNumberSetting(() -> this.jumpSetting.matchesString2("Grim"));
    public BooleanSetting onlyWhileMovingSetting = new BooleanSetting("Only while moving", this, false)
        .computeBooleanSetting(() -> this.jumpSetting.matchesString2("Grim"));
    public BooleanSetting onlyWhileMovingSetting2 = new BooleanSetting("Only while moving", this, false)
        .computeBooleanSetting(() -> !this.jumpSetting.matchesString2("Delay") && !this.jumpSetting.matchesString2("Grim"));
    public BooleanSetting airVelocitySetting = new BooleanSetting("Air Velocity", this, false)
        .computeBooleanSetting(() -> this.jumpSetting.matchesString2("Watchdog Old"));
    public BooleanSetting staffCheckSetting = new BooleanSetting("Staff check", this, false)
        .computeBooleanSetting(() -> this.jumpSetting.matchesString2("Packet"));
    private BooleanSetting releaseDelayOnGroundSetting = new BooleanSetting("Release delay on ground", this, false)
        .computeBooleanSetting(() -> this.jumpSetting.matchesString2("Delay"));
    private BooleanSetting releaseWhenAttackSetting = new BooleanSetting("Release when attack", this, false)
        .computeBooleanSetting(() -> this.jumpSetting.matchesString2("Delay"));
    private BooleanSetting showHUDSetting = new BooleanSetting("Show HUD", this, false).computeBooleanSetting(() -> this.jumpSetting.matchesString2("Delay"));
    private final List<Velocity.Entry> entries = new ArrayList<>();
    private long timestamp;
    private long timestamp2;
    private int count2 = 0;
    private int count3 = 0;
    private boolean flag = false;
    private double amount = 0.0;
    public boolean flag2 = false;
    private boolean flag3 = false;
    private boolean flag4;
    public int count4;
    public boolean flag5;
    public int count5 = 0;
    private int count6 = -1;
    private int count7 = 0;

    private void onGetPacketEvent5(GetPacketEvent getPacketEvent) {
        String text = this.jumpSetting.getText();
        switch (text) {
            case "Packet":
                this.onGetPacketEvent3(getPacketEvent);
                break;
            case "Cancel":
                this.onGetPacketEvent7(getPacketEvent);
                break;
            case "Matrix":
                this.onGetPacketEvent4(getPacketEvent);
                break;
            case "Watchdog Old":
                this.onGetPacketEvent2(getPacketEvent);
                break;
            case "Grim":
                if (getPacketEvent.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = getPacketEvent.getPacket();
                    if (this.mc.thePlayer == null) {
                        return;
                    }

                    if (packet.getEntityID() != this.mc.thePlayer.getEntityId()) {
                        return;
                    }

                    if (this.onlyWhileMovingSetting.isEnabled() && !GetSpeedHelper.isMoving()) {
                        return;
                    }

                    this.count6 = this.count2;
                    this.count7 = (int)this.ticksSetting.computeDouble();
                }
        }
    }

    private boolean z() {
        return this.jumpSetting.matchesString2("Grim");
    }

    private boolean onGetPacketEvent6(GetPacketEvent getPacketEvent) {
        if (this.staffCheckSetting.isEnabled() && System.currentTimeMillis() - this.timestamp > 500L) {
            if (System.currentTimeMillis() - this.timestamp2 > 250L) {
                this.timestamp2 = System.currentTimeMillis();
            }

            return true;
        }
        if (this.horizontalSetting.computeDouble() == 0.0 && this.verticalSetting.computeDouble() == 0.0) {
            getPacketEvent.cancel();
            return true;
        }
        return false;
    }

    public static boolean matchesEntityPlayerSP(EntityPlayerSP player) {
        Velocity velocity = instance;
        if (velocity == null) {
            return false;
        }
        boolean flag = velocity.entityPlayerSP == player;
        velocity.entityPlayerSP = null;
        if (!flag
            || !velocity.checkState44()
            // VENDOR-DISABLED: || !RuntimeAuthGuard.checkState2()
            || false
            || !velocity.jumpSetting.matchesString2("Jump")
            || player != velocity.mc.thePlayer
            || velocity.mc.theWorld == null
            || velocity.mc.currentScreen != null
            || player.ticksExisted < velocity.count
            || player.ticksExisted - velocity.count > 1
            || !player.onGround
            || !player.isSprinting()
            || player.jumpTicks != 0
            || player.isDead
            || player.isRiding()
            || player.isInWater()
            || player.isInLava()
            || player.isOnLadder()
            || player.capabilities.isFlying
            || player.isPotionActive(Potion.jump)) {
            return false;
        }
        if (velocity.onlyWhileMovingSetting2.isEnabled() && !GetSpeedHelper.isMoving()) {
            return false;
        }
        if (!velocity.onlyWhileTargetingSetting.isEnabled()
            || KillAura.entityLivingBase2 != null && KillAura.instance != null && KillAura.instance.checkState44()
            || velocity.mc.objectMouseOver != null && velocity.mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
            if (Math.random() * 100.0 >= velocity.chanceSetting.computeDouble()) {
                return false;
            }
            Float val = MovementFix.computeFloatType3();
            float f = val != null && Float.isFinite(val) ? val : player.rotationYaw;
            return matchesDouble(player.motionX, player.motionZ, f, velocity.minimumReductionSetting.computeDouble());
        }
        return false;
    }

    private void process3() {
        synchronized (this.entries) {
            ArrayList<Velocity.Entry> list = new ArrayList<>(this.entries);
            this.entries.clear();

            for (Velocity.Entry entry : list) {
                if (entry == null) {
                    return;
                }

                this.handlePacket(entry.packet);
            }
        }

        this.flag2 = false;
    }

    private void process8() {
        if (this.count7 > 0) {
            if (this.mc.thePlayer != null) {
                if (!this.mc.thePlayer.onGround) {
                    this.count7 = 0;
                } else if (this.count2 > this.count6) {
                    double d;
                    if (this.mc.thePlayer.isSprinting()) {
                        d = this.sprintFrictionSetting.computeDouble() / 100.0;
                    } else {
                        d = this.walkFrictionSetting.computeDouble() / 100.0;
                    }

                    this.mc.thePlayer.motionX *= d;
                    this.mc.thePlayer.motionZ *= d;
                    this.count7--;
                }
            }
        }
    }

    private void onGetPacketEvent3(GetPacketEvent getPacketEvent) {
        if (getPacketEvent.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = getPacketEvent.getPacket();
            if (this.mc.thePlayer != null && packet.getEntityID() == this.mc.thePlayer.getEntityId()) {
                if (this.onGetPacketEvent6(getPacketEvent)) {
                    return;
                }

                packet.motionX = (int)(packet.motionX * (this.horizontalSetting.computeDouble() / 100.0));
                packet.motionZ = (int)(packet.motionZ * (this.horizontalSetting.computeDouble() / 100.0));
                packet.motionY = (int)(packet.motionY * (this.verticalSetting.computeDouble() / 100.0));
            }
        } else if (getPacketEvent.getPacket() instanceof S27PacketExplosion) {
            if (this.onGetPacketEvent6(getPacketEvent)) {
                return;
            }

            S27PacketExplosion packet2 = getPacketEvent.getPacket();
            packet2.field_149152_f = packet2.field_149152_f * (float)(this.horizontalSetting.computeDouble() / 100.0);
            packet2.field_149153_g = packet2.field_149153_g * (float)(this.horizontalSetting.computeDouble() / 100.0);
            packet2.field_149159_h = packet2.field_149159_h * (float)(this.verticalSetting.computeDouble() / 100.0);
        }
    }

    private void process6() {
        if (!this.checkState2() && !this.flag4 && !this.flag3) {
            long now = System.currentTimeMillis();
            long l = (long)this.delaySetting.computeDouble();
            this.count5 = (int)l;
            synchronized (this.entries) {
                ArrayList<Velocity.Entry> list = new ArrayList<>();

                for (Velocity.Entry entry : this.entries) {
                    if (now - entry.timestamp >= l) {
                        list.add(entry);
                    } else {
                        long l2 = l - (now - entry.timestamp);
                        this.count5 = (int)l2;
                    }
                }

                for (Velocity.Entry entry2 : list) {
                    this.entries.remove(entry2);
                    this.handlePacket(entry2.packet);
                }
            }

            if (this.entries.isEmpty()) {
                this.count5 = 0;
            }

            this.flag3 = false;
        } else {
            this.process3();
            this.flag4 = false;
            this.flag3 = false;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (this.jumpSetting.matchesString2("Delay")) {
            this.process3();
        }

        this.process7();
    }

    private void onGetPacketEvent4(GetPacketEvent getPacketEvent) {
        if (getPacketEvent.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = getPacketEvent.getPacket();
            if (this.mc.thePlayer != null && packet.getEntityID() == this.mc.thePlayer.getEntityId()) {
                packet.motionX = (int)(packet.motionX * 0.05);
                packet.motionZ = (int)(packet.motionZ * 0.05);
                packet.motionY = (int)(packet.motionY * 1.0);
            }
        }
    }

    private void onEventRender2D(EventRender2D event) {
        if (this.mc.thePlayer != null && this.mc.theWorld != null) {
            ScaledResolution scaledResolution = event.getScaledResolution();
            int n = scaledResolution.getScaledWidth() / 2;
            int n2 = scaledResolution.getScaledHeight() / 2 - 35;
            String str = this.count5 + "ms";
            int n3 = 16777215;
            if (this.count5 > 0) {
                n3 = 16733525;
            } else {
                n3 = 5635925;
            }

            computeFontRenderer(Velocity.class).drawStringWithShadow(str, n - computeFontRenderer(Velocity.class).getStringWidth(str) / 2, n2, n3);
        }
    }

    private void process5() {
        this.count2++;
        if (this.flag && this.count2 > this.count3 + 10) {
            this.flag = false;
            this.amount = 0.0;
        }

        if (this.flag5 && this.jumpSetting.matchesString2("Delay")) {
            this.mc.gameSettings.keyBindJump.setPressed(GameSettings.isKeyDown(this.mc.gameSettings.keyBindJump));
            this.flag5 = false;
        }
    }

    public Velocity() {
        super("Velocity", 0, Category.COMBAT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.jumpSetting,
                this.horizontalSetting,
                this.verticalSetting,
                this.chanceSetting,
                this.delaySetting,
                this.ticksSetting,
                this.sprintFrictionSetting,
                this.walkFrictionSetting,
                this.onlyWhileMovingSetting,
                this.onlyWhileMovingSetting2,
                this.airVelocitySetting,
                this.staffCheckSetting,
                this.releaseDelayOnGroundSetting,
                this.releaseWhenAttackSetting,
                this.showHUDSetting
            }
        );
        instance = this;
        this.handleSettingArray(new Setting[]{this.onlyWhileTargetingSetting, this.minimumReductionSetting});
    }

    public static void process4() {
        Velocity velocity = instance;
        if (velocity != null) {
            velocity.entityPlayerSP = null;
            if (velocity.checkState44()
                // VENDOR-DISABLED: && RuntimeAuthGuard.checkState2()
                && true
                && velocity.jumpSetting.matchesString2("Jump")
                && velocity.mc.thePlayer != null
                && velocity.mc.theWorld != null) {
                velocity.entityPlayerSP = velocity.mc.thePlayer;
                velocity.count = velocity.mc.thePlayer.ticksExisted;
            }
        }
    }

    private void handlePacket(Packet packet) {
        packet.processPacket(this.mc.getNetHandler());
    }

    private void onGetPacketEvent9(GetPacketEvent getPacketEvent) {
        if (getPacketEvent.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = getPacketEvent.getPacket();
            if (this.mc.thePlayer != null && packet.getEntityID() == this.mc.thePlayer.getEntityId()) {
                this.count3 = this.count2 + 2;
                this.amount = packet.getMotionY() / 8000.0;
                this.flag = true;
                this.timestamp = System.currentTimeMillis();
            }
        } else if (getPacketEvent.getPacket() instanceof S19PacketEntityStatus) {
            S19PacketEntityStatus packet2 = getPacketEvent.getPacket();
            if (this.mc.thePlayer != null && packet2.getEntityId() == this.mc.thePlayer.getEntityId() && packet2.getOpCode() == 2) {
                this.timestamp = System.currentTimeMillis();
                this.flag = true;
                this.count3 = this.count2 + 2;
            }
        }
    }

    private void onGetPacketEvent7(GetPacketEvent getPacketEvent) {
        if (getPacketEvent.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = getPacketEvent.getPacket();
            if (this.mc.thePlayer != null && packet.getEntityID() == this.mc.thePlayer.getEntityId()) {
                getPacketEvent.cancel();
            }
        } else if (getPacketEvent.getPacket() instanceof S27PacketExplosion) {
            getPacketEvent.cancel();
        }
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.process7();
        }
    }

    private void onEventUpdate(EventUpdate event) {
        if (this.jumpSetting.matchesString2("Motion")) {
            this.process();
        }

        this.count4--;
    }

    private boolean checkState4() {
        if (this.mc.thePlayer == null) {
            return false;
        }
        synchronized (this.entries) {
            int entityId = this.mc.thePlayer.getEntityId();

            for (Velocity.Entry entry : this.entries) {
                if (entry.packet instanceof S12PacketEntityVelocity packet && packet.getEntityID() == entityId) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean matchesDouble(double d, double d2, float f, double d3) {
        double d4 = Math.hypot(d, d2);
        if (Double.isFinite(d4) && d4 != 0.0 && Float.isFinite(f)) {
            float f2 = f * (float) (Math.PI / 180.0);
            double d5 = Math.hypot(d - MathHelper.sinVanilla(f2) * 0.2F, d2 + MathHelper.cosVanilla(f2) * 0.2F);
            return (d4 - d5) / d4 * 100.0 >= d3;
        }
        return false;
    }

    private boolean checkState2() {
        if (this.mc.thePlayer == null) {
            return true;
        }
        if (this.mc.thePlayer.onGround) {
            return true;
        }
        return this.mc.thePlayer.capabilities.isFlying ? true : !this.checkState4();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof GetPacketEvent) {
                    this.onGetPacketEvent8((GetPacketEvent)event);
                } else if (event instanceof EventTick) {
                    this.process5();
                } else if (event instanceof EventUpdate) {
                    this.onEventUpdate((EventUpdate)event);
                }

                if (event instanceof EventPostMotion && this.jumpSetting.matchesString2("Delay")) {
                    this.process6();
                }

                if (event instanceof EventPostMotion && this.jumpSetting.matchesString2("Grim")) {
                    this.process8();
                }

                if (event instanceof EventRender2D && this.showHUDSetting.isEnabled() && this.jumpSetting.matchesString2("Delay")) {
                    this.onEventRender2D((EventRender2D)event);
                }
            }
        }
    }

    private boolean checkState() {
        if (this.mc.thePlayer == null) {
            return false;
        }
        if (this.mc.thePlayer.capabilities.isFlying) {
            return false;
        }
        if (this.mc.thePlayer.isCollidedHorizontally) {
            return false;
        }
        return this.mc.thePlayer.ticksExisted < 20
            ? false
            : !this.releaseWhenAttackSetting.isEnabled()
                || KillAura.entityLivingBase2 == null
                || !(this.mc.thePlayer.getDistanceToEntity(KillAura.entityLivingBase2) < 1.8)
                || this.flag2;
    }

    private void process() {
        if (GetCurrentScreenHelper.checkState2() && this.mc.thePlayer != null) {
            if (this.chanceSetting.computeDouble() != 0.0) {
                if (this.chanceSetting.computeDouble() != 100.0) {
                    double d = Math.random();
                    if (d >= this.chanceSetting.computeDouble() / 100.0) {
                        return;
                    }
                }

                if (!this.onlyWhileMovingSetting2.isEnabled() || GetSpeedHelper.isMoving()) {
                    if (this.horizontalSetting.computeDouble() == 0.0 && this.verticalSetting.computeDouble() == 0.0) {
                        this.mc.thePlayer.motionX = 0.0;
                        this.mc.thePlayer.motionY = 0.0;
                        this.mc.thePlayer.motionZ = 0.0;
                    } else {
                        if (this.horizontalSetting.computeDouble() != 100.0) {
                            this.mc.thePlayer.motionX = this.mc.thePlayer.motionX * (this.horizontalSetting.computeDouble() / 100.0);
                            this.mc.thePlayer.motionZ = this.mc.thePlayer.motionZ * (this.horizontalSetting.computeDouble() / 100.0);
                        }

                        if (this.verticalSetting.computeDouble() != 100.0) {
                            this.mc.thePlayer.motionY = this.mc.thePlayer.motionY * (this.verticalSetting.computeDouble() / 100.0);
                        }
                    }
                }
            }
        }
    }

    private void process7() {
        this.entityPlayerSP = null;
        this.count2 = 0;
        this.count3 = 0;
        this.flag = false;
        this.amount = 0.0;
        this.entries.clear();
        this.flag2 = false;
        this.flag3 = false;
        this.count4 = 0;
        this.flag5 = false;
        this.count6 = -1;
        this.count7 = 0;
    }

    private void onGetPacketEvent2(GetPacketEvent getPacketEvent) {
        if (getPacketEvent.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = getPacketEvent.getPacket();
            if (packet.getEntityID() == this.mc.thePlayer.getEntityId()) {
                if (this.airVelocitySetting.isEnabled() && !this.mc.thePlayer.onGround) {
                    getPacketEvent.cancel();
                    return;
                }

                getPacketEvent.cancel();
                this.mc.thePlayer.motionY = packet.getMotionY() / 8000.0;
            }
        }
    }

    private void onGetPacketEvent8(GetPacketEvent getPacketEvent) {
        if (!this.jumpSetting.matchesString2("Jump")) {
            if (getPacketEvent.getDirection() == PacketDirection.INCOMING) {
                if (this.jumpSetting.matchesString2("Delay")) {
                    if (!(getPacketEvent.getPacket() instanceof S08PacketPlayerPosLook)
                        && !(getPacketEvent.getPacket() instanceof C02PacketUseEntity)
                        && !(getPacketEvent.getPacket() instanceof S12PacketEntityVelocity)) {
                        return;
                    }
                } else if (!(getPacketEvent.getPacket() instanceof S12PacketEntityVelocity)
                    && !(getPacketEvent.getPacket() instanceof S19PacketEntityStatus)
                    && !(getPacketEvent.getPacket() instanceof S27PacketExplosion)) {
                    return;
                }

                if (this.jumpSetting.matchesString2("Reduce")) {
                    if (getPacketEvent.getPacket() instanceof S12PacketEntityVelocity) {
                        S12PacketEntityVelocity packet = getPacketEvent.getPacket();
                        if (this.mc.thePlayer != null && packet.getEntityID() == this.mc.thePlayer.getEntityId()) {
                            this.count4 = 4;
                        }
                    }
                } else if (this.jumpSetting.matchesString2("Delay")) {
                    this.onGetPacketEvent(getPacketEvent);
                } else {
                    this.onGetPacketEvent9(getPacketEvent);
                    if (this.jumpSetting.matchesString2("Grim")
                        || (!this.onlyWhileMovingSetting2.isEnabled() || GetSpeedHelper.isMoving())
                            && (this.chanceSetting.computeDouble() == 100.0 || !(Math.random() * 100.0 > this.chanceSetting.computeDouble()))) {
                        this.onGetPacketEvent5(getPacketEvent);
                    }
                }
            }
        }
    }

    @Generated
    public static Velocity getInstance() {
        return instance;
    }

    @Override
    public void process2() {
        this.entityPlayerSP = null;
        super.process2();
    }

    private void onGetPacketEvent(GetPacketEvent getPacketEvent) {
        if (getPacketEvent.getPacket() instanceof S08PacketPlayerPosLook) {
            this.flag3 = true;
        } else if (getPacketEvent.getPacket() instanceof C02PacketUseEntity) {
            this.flag4 = true;
        } else if (getPacketEvent.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = getPacketEvent.getPacket();
            if (this.mc.thePlayer != null && packet.getEntityID() == this.mc.thePlayer.getEntityId()) {
                if (this.checkState()) {
                    this.flag2 = true;
                    this.count3 = this.count2 + 2;
                    this.amount = packet.getMotionY() / 8000.0;
                    this.flag = true;
                    this.timestamp = System.currentTimeMillis();
                    synchronized (this.entries) {
                        this.entries.add(new Velocity.Entry(getPacketEvent.getPacket(), System.currentTimeMillis()));
                    }

                    getPacketEvent.cancel();
                }
            }
        }
    }

    public static class Entry {
        public final Packet packet;
        public final long timestamp;

        public Entry(Packet packet, long timestamp) {
            this.packet = packet;
            this.timestamp = timestamp;
        }
    }
}
