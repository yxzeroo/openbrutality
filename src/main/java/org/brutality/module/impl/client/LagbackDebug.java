package org.brutality.module.impl.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Locale;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.movement.KeepSprint;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.player.FailedSimulationHelper;
import org.brutality.utils.rotation.OnJumpHelper;

public final class LagbackDebug extends Module {
    private static LagbackDebug instance;
    private final NumberSetting minCorrectionSetting = new NumberSetting("Min Correction", this, 0.75, 0.1, 5.0, 0.05);
    private final NumberSetting maxCorrectionSetting = new NumberSetting("Max Correction", this, 4.0, 1.0, 15.0, 0.25);
    private final NumberSetting cooldownSetting = new NumberSetting("Cooldown", this, 1.0, 0.1, 10.0, 0.1);
    private final BooleanSetting chatSetting = new BooleanSetting("Chat", this, true);
    private final BooleanSetting saveLogSetting = new BooleanSetting("Save Log", this, true);
    private final FailedSimulationHelper failedSimulationHelper = new FailedSimulationHelper();
    private EntityPlayerSP entityPlayerSP;
    private Object object;
    private int count;
    private boolean flag;
    private long timestamp = -1L;

    public static void handleString(String str, double d, double d2, double d3) {
        LagbackDebug lagbackDebug = instance;
        if (lagbackDebug != null && (lagbackDebug.checkState44() || KeepSprint.checkState12()) && lagbackDebug.checkState()) {
            lagbackDebug.failedSimulationHelper.handleLong(System.currentTimeMillis(), String.format(Locale.ROOT, "%s motion=%.5f/%.5f/%.5f", str, d, d2, d3));
        }
    }

    @Override
    public void process2() {
        this.failedSimulationHelper.reset();
        this.entityPlayerSP = null;
        this.object = null;
        this.timestamp = -1L;
    }

    public static void handleS08PacketPlayerPosLook(S08PacketPlayerPosLook packet, boolean flag) {
        LagbackDebug lagbackDebug = instance;
        if (lagbackDebug != null && (lagbackDebug.checkState44() || KeepSprint.checkState12()) && lagbackDebug.checkState()) {
            EntityPlayerSP player = mc.thePlayer;
            double d = packet.getX() + (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X) ? player.posX : 0.0);
            double d2 = packet.getY() + (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y) ? player.posY : 0.0);
            double d3 = packet.getZ() + (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Z) ? player.posZ : 0.0);
            lagbackDebug.failedSimulationHelper
                .handleLong(
                    System.currentTimeMillis(),
                    String.format(
                        Locale.ROOT,
                        "S08 target=%.5f/%.5f/%.5f yaw=%.5f pitch=%.5f flags=%s terrainReady=%s",
                        d,
                        d2,
                        d3,
                        packet.getYaw(),
                        packet.getPitch(),
                        packet.func_179834_f(),
                        flag
                    )
                );
            if (lagbackDebug.checkState44()) {
                FailedSimulationHelper.IVEntry ivEntry = lagbackDebug.failedSimulationHelper
                    .computeIVEntry(
                        System.currentTimeMillis(),
                        flag,
                        player.posX,
                        player.posY,
                        player.posZ,
                        d,
                        d2,
                        d3,
                        lagbackDebug.minCorrectionSetting.computeDouble(),
                        Math.max(lagbackDebug.minCorrectionSetting.computeDouble(), lagbackDebug.maxCorrectionSetting.computeDouble()),
                        (long)(lagbackDebug.cooldownSetting.computeDouble() * 1000.0)
                    );
                if (ivEntry != null) {
                    lagbackDebug.handleIVEntry(ivEntry, packet, d, d2, d3);
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (!KeepSprint.checkState12() && event instanceof EventUpdate event2 && event2.getState() == EventState.POST && this.checkState()) {
            EntityPlayerSP player = mc.thePlayer;
            this.failedSimulationHelper.handleLong(System.currentTimeMillis(), player.posX, player.posY, player.posZ, this.computeString());
        }
    }

    public LagbackDebug() {
        super("LagbackDebug", 0, Category.CLIENT);
        this.handleSettingArray(
            new Setting[]{this.minCorrectionSetting, this.maxCorrectionSetting, this.cooldownSetting, this.chatSetting, this.saveLogSetting}
        );
        instance = this;
    }

    private void handleString(String str) {
        if (this.saveLogSetting.isEnabled()) {
            try {
                Path path = mc.mcDataDir.toPath().resolve("Brutality/debug/lagbacks.log");
                Files.createDirectories(path.getParent());
                Files.writeString(path, str, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                LogManager.getLogger(LagbackDebug.class).warn("Could not save lagback report", (Throwable)e);
                if (!this.flag) {
                    this.flag = true;
                    MessageUtils.x("LagbackDebug: could not save log: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.process2();
        this.count = 0;
        this.flag = false;
        MessageUtils.x("LagbackDebug enabled. Reports: Brutality/debug/lagbacks.log");
    }

    public static void handleString2(String str) {
        LagbackDebug lagbackDebug = instance;
        if (lagbackDebug != null
            && (lagbackDebug.checkState44() || KeepSprint.checkState12())
            && mc.thePlayer != null
            && FailedSimulationHelper.matchesString(str, mc.thePlayer.getName())
            && lagbackDebug.checkState()) {
            long now = System.currentTimeMillis();
            if (lagbackDebug.timestamp < 0L || now - lagbackDebug.timestamp >= 1000L) {
                lagbackDebug.timestamp = now;
                FailedSimulationHelper.IXEntry ixEntry = lagbackDebug.failedSimulationHelper.computeIXEntry(now);
                StringBuilder sb = new StringBuilder("\n=== ")
                    .append(Instant.ofEpochMilli(now))
                    .append(" Server Simulation alert ===\n")
                    .append(str)
                    .append("\nAt alert receipt (server check may refer to earlier movement): ")
                    .append(lagbackDebug.computeString())
                    .append("\nMovement history (ms before receipt):\n");

                for (FailedSimulationHelper.IWEntry iwEntry : ixEntry.Q()) {
                    sb.append(now - iwEntry.iW())
                        .append("ms ")
                        .append(iwEntry.iO())
                        .append(String.format(Locale.ROOT, " pos=%.5f/%.5f/%.5f\n", iwEntry.i(), iwEntry.j(), iwEntry.k()));
                }

                sb.append("Dispatched packets (ms before receipt):\n");

                for (FailedSimulationHelper.IUEntry iuEntry : ixEntry.R()) {
                    sb.append(now - iuEntry.iU()).append("ms ").append(iuEntry.iN()).append('\n');
                }

                lagbackDebug.handleString(sb.toString());
            }
        }
    }

    private void handleIVEntry(FailedSimulationHelper.IVEntry ivEntry, S08PacketPlayerPosLook packet, double d, double d2, double d3) {
        int n = ++this.count;
        String str = String.format(
            Locale.ROOT,
            "Possible lagback #%d: %.2f blocks (%s), sprint=%s collision=%s use=%s",
            n,
            ivEntry.h(),
            ivEntry.iJ() ? "backwards" : "position correction",
            mc.thePlayer.isSprinting(),
            mc.thePlayer.isCollidedHorizontally,
            mc.thePlayer.isUsingItem()
        );
        if (this.chatSetting.isEnabled()) {
            MessageUtils.x(str);
        }

        StringBuilder sb = new StringBuilder("\n=== ")
            .append(Instant.ofEpochMilli(ivEntry.iV()))
            .append(" ")
            .append(str)
            .append(" ===\n")
            .append(
                String.format(
                    Locale.ROOT, "delta=%.4f/%.4f/%.4f target=%.4f/%.4f/%.4f flags=%s\n", ivEntry.e(), ivEntry.f(), ivEntry.g(), d, d2, d3, packet.func_179834_f()
                )
            )
            .append("At correction: ")
            .append(this.computeString())
            .append("\nEnabled modules: ");

        for (Module module : ModuleManager.getModuleArray()) {
            if (module.checkState44()) {
                sb.append(module.getName()).append(", ");
            }
        }

        KillAura instance = KillAura.getInstance();
        if (instance != null) {
            sb.append("\nKA enabled=")
                .append(instance.checkState44())
                .append(" autoblock=")
                .append(instance.verusSetting.getText())
                .append(" target=")
                .append(KillAura.entityLivingBase2 == null ? "none" : KillAura.entityLivingBase2.getName());
        }

        sb.append("\nMovement history (ms before correction):\n");

        for (FailedSimulationHelper.IWEntry iwEntry : ivEntry.O()) {
            sb.append(ivEntry.iV() - iwEntry.iW())
                .append("ms ")
                .append(String.format(Locale.ROOT, "pos=%.4f/%.4f/%.4f ", iwEntry.i(), iwEntry.j(), iwEntry.k()))
                .append(iwEntry.iO())
                .append('\n');
        }

        sb.append("Dispatched packets (ms before correction):\n");

        for (FailedSimulationHelper.IUEntry iuEntry : ivEntry.P()) {
            sb.append(ivEntry.iV() - iuEntry.iU()).append("ms ").append(iuEntry.iN()).append('\n');
        }

        this.handleString(sb.toString());
    }

    @Override
    public void onDisable() {
        this.process2();
        super.onDisable();
    }

    private String computeString() {
        EntityPlayerSP player = mc.thePlayer;
        String str = player.getHeldItem() == null ? "empty" : player.getHeldItem().getUnlocalizedName();
        String unlocalizedName = mc.theWorld.getBlockState(new BlockPos(player.posX, player.getEntityBoundingBox().minY - 0.05, player.posZ)).getBlock().getUnlocalizedName();
        float[] lastReportedRotations = player.getLastReportedRotations();
        KillAura instance = KillAura.getInstance();
        return String.format(
            Locale.ROOT,
            "tick=%d input=%.2f/%.2f jump=%s sneak=%s sprint=%s reportedSprint=%s use=%s useKey=%s attackKey=%s ground=%s horizontalCollision=%s verticalCollision=%s block=%s step=%.2f water=%s lava=%s hurt=%d motion=%.4f/%.4f/%.4f yaw=%.2f reportedYaw=%.2f timer=%.3f fastMath=%s held=%s slot=%d auraBlockFlags=%s rotationActive=%s gui=%s groundAtMovementStart=%s airAcceleration=%.5f moveSpeed=%.5f sprintHold=%s holdAttack=%s auraMode=%s auraBlink=%s target=%s knockbackEnchant=%d recentSprintMovement=%s",
            player.ticksExisted,
            player.movementInput.moveForward,
            player.movementInput.moveStrafe,
            player.movementInput.jump,
            player.movementInput.sneak,
            player.isSprinting(),
            player.serverSprintState,
            player.isUsingItem(),
            mc.gameSettings.keyBindUseItem.isKeyDown(),
            mc.gameSettings.keyBindAttack.isKeyDown(),
            player.onGround,
            player.isCollidedHorizontally,
            player.isCollidedVertically,
            unlocalizedName,
            player.stepHeight,
            player.isInWater(),
            player.isInLava(),
            player.hurtTime,
            player.motionX,
            player.motionY,
            player.motionZ,
            player.rotationYaw,
            lastReportedRotations[0],
            mc.timer.timerSpeed,
            mc.gameSettings.ofFastMath,
            str,
            player.inventory.currentItem,
            instance != null && instance.checkState53(),
            OnJumpHelper.checkState(),
            mc.currentScreen == null ? "none" : mc.currentScreen.getClass().getSimpleName(),
            player.isGroundedForSprintDecision(),
            player.jumpMovementFactor,
            player.getAIMoveSpeed(),
            KeepSprint.instance != null && KeepSprint.instance.checkState13(),
            KeepSprint.instance != null && KeepSprint.instance.checkState(),
            instance == null ? "none" : instance.verusSetting.getText(),
            instance != null && instance.flag30,
            KillAura.entityLivingBase2 == null ? "none" : KillAura.entityLivingBase2.getEntityId(),
            EnchantmentHelper.getKnockbackModifier(player),
            player.hasRecentSprintMovement()
        );
    }

    public static void handleNetworkManager(NetworkManager networkManager, Packet<?> packet) {
        LagbackDebug lagbackDebug = instance;
        if (lagbackDebug != null && (lagbackDebug.checkState44() || KeepSprint.checkState12()) && mc.getNetHandler() != null && mc.getNetHandler().getNetworkManager() == networkManager) {
            String str;
            if (packet instanceof C03PacketPlayer packet2) {
                str = String.format(
                    Locale.ROOT,
                    "%s ground=%s moving=%s look=%s pos=%.5f/%.5f/%.5f yaw=%.5f pitch=%.5f",
                    packet.getClass().getSimpleName(),
                    packet2.isOnGround(),
                    packet2.isMoving(),
                    packet2.getRotating(),
                    packet2.getPositionX(),
                    packet2.getPositionY(),
                    packet2.getPositionZ(),
                    packet2.getYaw(),
                    packet2.getPitch()
                );
            } else if (packet instanceof C07PacketPlayerDigging packet3) {
                str = "C07 " + packet3.getStatus();
            } else if (packet instanceof C08PacketPlayerBlockPlacement packet4) {
                str = "C08 face=" + packet4.getPlacedBlockDirection() + " item=" + (packet4.getStack() == null ? "empty" : packet4.getStack().getUnlocalizedName());
            } else if (packet instanceof C09PacketHeldItemChange packet5) {
                str = "C09 slot=" + packet5.getSlotId();
            } else if (packet instanceof C0BPacketEntityAction packet6) {
                str = "C0B " + packet6.getAction();
            } else {
                if (!(packet instanceof C02PacketUseEntity packet7)) {
                    return;
                }

                str = "C02 " + packet7.getAction();
            }

            lagbackDebug.failedSimulationHelper.handleLong(System.currentTimeMillis(), str);
        }
    }

    public static void onEventUpdate(EventUpdate event) {
        LagbackDebug lagbackDebug = instance;
        if (lagbackDebug != null && KeepSprint.checkState12() && lagbackDebug.checkState()) {
            EntityPlayerSP player = mc.thePlayer;
            lagbackDebug.failedSimulationHelper.handleLong(System.currentTimeMillis(), player.posX, player.posY, player.posZ, event.getState() + " " + lagbackDebug.computeString());
        }
    }

    private boolean checkState() {
        if (mc.thePlayer == null || mc.theWorld == null) {
            this.process2();
            return false;
        }
        if (mc.thePlayer.movementInput != null && mc.gameSettings != null && mc.timer != null) {
            if (this.entityPlayerSP != mc.thePlayer || this.object != mc.theWorld) {
                this.failedSimulationHelper.reset();
                this.entityPlayerSP = mc.thePlayer;
                this.object = mc.theWorld;
                this.timestamp = -1L;
            }

            return true;
        }
        return false;
    }
}
