package org.brutality.module.impl.movement.flight;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.brutality.core.CoreHandler2;
import org.brutality.event.Event;
import org.brutality.event.impl.movement.EventPostMotion;
import org.brutality.event.impl.movement.MovementEvent2;
import org.brutality.event.impl.movement.MovementEvent4;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.module.impl.movement.NoSlowDown;
import org.brutality.module.impl.movement.Scaffold;
import org.brutality.utils.network.NetworkHelper;
import org.brutality.utils.performance.WorldQueryCache;
import org.brutality.utils.player.StartHotbarMovesHelper;

public final class FlightHandler2 {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final NoSlowDown noSlowDown;
    private EntityPlayerSP entityPlayerSP;
    private Object object;
    private NetworkManager connection;
    private ItemStack itemStack;
    private int slot = -1;
    private int count = Integer.MIN_VALUE;
    private volatile boolean flag;
    private volatile boolean flag2;
    private volatile long timestamp;
    private long timestamp2;
    private boolean flag3;

    public void onMovementEvent2(MovementEvent2 movementEvent2) {
        if (this.checkState2()) {
            movementEvent2.handleFloat(0.2F);
        } else {
            movementEvent2.handleFloat(1.0F);
        }
    }

    public void onEvent(Event event) {
        if (this.checkState3() && !this.noSlowDown.checkState12()) {
            if (event instanceof MovementEvent4) {
                if (this.count == this.entityPlayerSP.ticksExisted) {
                    return;
                }

                this.count = this.entityPlayerSP.ticksExisted;
                if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
                    this.flag3 = false;
                    return;
                }

                if (!this.flag3 && this.entityPlayerSP.isUsingItem() || this.flag) {
                    this.flag3 = true;
                    this.timestamp2 = this.timestamp;
                    NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    if (!this.flag) {
                        this.entityPlayerSP.stopUsingItem();
                    }
                }
            } else if (event instanceof EventPacketSend event2
                && this.flag3
                && event2.getPacket() instanceof C08PacketPlayerBlockPlacement packet
                && packet.getPlacedBlockDirection() == 255
                && packet.getStack() != null
                && packet.getStack().getItem() instanceof ItemSword) {
                event2.setCancelled(true);
            } else if (event instanceof EventPostMotion
                && this.flag3
                && !this.flag
                && this.timestamp > this.timestamp2
                && mc.gameSettings.keyBindUseItem.isKeyDown()) {
                this.entityPlayerSP.setSprinting(false);
                if (this.entityPlayerSP.serverSprintState || this.flag2) {
                    NetworkHelper.handlePacket2(
                        new C0BPacketEntityAction(this.entityPlayerSP, net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SPRINTING)
                    );
                    if (this.flag2) {
                        return;
                    }

                    this.entityPlayerSP.serverSprintState = false;
                }

                this.flag3 = false;
                ItemStack heldItem = this.entityPlayerSP.getHeldItem();
                NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(heldItem));
                if (this.flag) {
                    this.entityPlayerSP.setItemInUse(heldItem, heldItem.getMaxItemUseDuration());
                }
            }
        } else {
            this.flag3 = false;
        }
    }

    public FlightHandler2(NoSlowDown noSlowDown) {
        this.noSlowDown = noSlowDown;
    }

    public void reset() {
        this.entityPlayerSP = null;
        this.object = null;
        this.connection = null;
        this.itemStack = null;
        this.slot = -1;
        this.flag = this.flag2 = false;
        this.timestamp = 0L;
        this.flag3 = false;
        this.count = Integer.MIN_VALUE;
    }

    private boolean checkState3() {
        return this.noSlowDown.checkState4()
            && this.checkState()
            && this.itemStack != null
            && this.itemStack.getItem() instanceof ItemSword
            && !this.entityPlayerSP.isDead
            && this.entityPlayerSP.getHealth() > 0.0F
            && !this.entityPlayerSP.isRiding()
            && mc.currentScreen == null
            && !StartHotbarMovesHelper.flag
            && !CoreHandler2.checkState()
            && !Scaffold.checkState19()
            && !WorldQueryCache.isActive();
    }

    private boolean checkState() {
        return this.entityPlayerSP == mc.thePlayer
            && this.object == mc.theWorld
            && mc.getNetHandler() != null
            && this.connection == mc.getNetHandler().getNetworkManager()
            && this.entityPlayerSP != null
            && this.entityPlayerSP.inventory.currentItem == this.slot
            && this.itemStack != null
            && this.entityPlayerSP.getHeldItem() != null
            && this.entityPlayerSP.getHeldItem().getItem() == this.itemStack.getItem();
    }

    public void process() {
        this.flag3 = false;
        this.count = Integer.MIN_VALUE;
    }

    public void handlePacket(Packet<?> packet) {
        if (mc.thePlayer != null && mc.theWorld != null && mc.getNetHandler() != null) {
            if (!this.checkState()) {
                this.reset();
                this.entityPlayerSP = mc.thePlayer;
                this.object = mc.theWorld;
                this.connection = mc.getNetHandler().getNetworkManager();
                this.slot = this.entityPlayerSP.inventory.currentItem;
                this.itemStack = this.entityPlayerSP.getHeldItem();
                this.flag2 = this.entityPlayerSP.serverSprintState;
            }

            if (packet instanceof C03PacketPlayer) {
                this.timestamp++;
            } else if (packet instanceof C0BPacketEntityAction packet2) {
                if (packet2.getAction() == net.minecraft.network.play.client.C0BPacketEntityAction.Action.START_SPRINTING) {
                    this.flag2 = true;
                } else if (packet2.getAction() == net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SPRINTING) {
                    this.flag2 = false;
                }
            } else if (packet instanceof C08PacketPlayerBlockPlacement packet3
                && packet3.getPlacedBlockDirection() == 255
                && packet3.getStack() != null
                && packet3.getStack().getItem() instanceof ItemSword) {
                this.flag = true;
            } else if (packet instanceof C09PacketHeldItemChange || packet instanceof C07PacketPlayerDigging packet4 && packet4.getStatus() == Action.RELEASE_USE_ITEM) {
                this.flag = false;
            }
        }
    }

    public boolean checkState2() {
        return this.checkState() && this.flag;
    }
}
