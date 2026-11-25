package org.brutality.module.impl.movement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPostMotion;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.movement.MovementEvent5;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.movement.flight.FlightHandler4;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.network.NetworkHelper;

public final class BoatFly extends Module {
    private static final int count = 0;
    private static final int count2 = 1;
    private static final int count3 = 2;
    private static final int count4 = 3;
    private static final int count5 = 4;
    private static final int count6 = 5;
    private final NumberSetting maximumBoatsSetting = new NumberSetting("Maximum Boats", this, 9.0, 1.0, 9.0, 1.0);
    private final NumberSetting speedSetting = new NumberSetting("Speed", this, 1.0, 0.1, 15.0, 0.1);
    private final NumberSetting flightTimeSetting = new NumberSetting("Flight Time", this, 15.0, 1.0, 15.0, 1.0);
    private final FlightHandler4 flightHandler4 = new FlightHandler4();
    private final List<Packet<?>> entries = new ArrayList<>();
    private final Set<Integer> itemSet = new HashSet<>();
    private final Set<Integer> itemSet2 = new HashSet<>();
    private volatile int count7 = 0;
    private volatile boolean flag;
    private volatile boolean flag2;
    private volatile boolean flag3;
    private volatile boolean flag4;
    private int count8 = -1;
    private int count9 = -1;
    private int count10 = -1;
    private int count11 = -1;
    private int count12;
    private int count13;
    private int count14;
    private int count15;
    private long timestamp;
    private long timestamp2;
    private long timestamp3;
    private long timestamp4;
    private long timestamp5;
    private BlockPos blockPos;
    private float factor;
    private float factor2;
    private boolean flag5;
    private boolean flag6;

    private boolean matchesItemStack(ItemStack stack) {
        return stack != null && stack.getItem() == Items.boat;
    }

    @Override
    public void onDisable() {
        this.handleFlag(true);
    }

    private int computeInt2() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemSword) {
                return i;
            }
        }

        return this.count9;
    }

    @Override
    public void onEnable() {
        this.handleFlag(false);
        if (mc.thePlayer != null && mc.theWorld != null) {
            if (mc.thePlayer.onGround && !mc.thePlayer.isDead && mc.thePlayer.ridingEntity == null) {
                int n = 0;

                for (int i = 0; i < 9; i++) {
                    if (this.matchesItemStack(mc.thePlayer.inventory.getStackInSlot(i))) {
                        n++;
                    }
                }

                if (n == 0) {
                    this.stop("Put at least one boat in your hotbar.");
                } else {
                    this.count9 = this.count10 = mc.thePlayer.inventory.currentItem;
                    this.count11 = mc.thePlayer.getEntityId();
                    this.count12 = mc.thePlayer.ticksExisted;
                    this.timestamp4 = (long)(this.flightTimeSetting.computeDouble() * 1000.0);
                    this.count14 = Math.min(n, (int)this.maximumBoatsSetting.computeDouble());

                    for (Object obj : mc.theWorld.loadedEntityList) {
                        if (obj instanceof EntityBoat) {
                            this.itemSet.add(((EntityBoat)obj).getEntityId());
                        }
                    }

                    this.count7 = 1;
                    this.timestamp = this.timestamp5 = System.currentTimeMillis();
                    this.process4();
                    if (this.count7 != 0) {
                        this.handleString("Placing up to " + this.count14 + " boats.");
                    }
                }
            } else {
                this.stop("Stand on the ground outside a vehicle before enabling.");
            }
        } else {
            this.handleFlag3(false);
        }
    }

    private void handleInt(int n) {
        if (n >= 0 && n <= 8 && mc.thePlayer != null) {
            mc.thePlayer.inventory.currentItem = n;
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(n));
            this.count10 = n;
        }
    }

    private void process4() {
        int n = -1;

        for (int i = 0; i < 9; i++) {
            if (!this.itemSet2.contains(i) && this.matchesItemStack(mc.thePlayer.inventory.getStackInSlot(i))) {
                n = i;
                break;
            }
        }

        if (n < 0) {
            this.stop("No more boats available in the hotbar.");
        } else {
            this.handleInt(n);
            float yaw = mc.thePlayer.rotationYaw;
            float pitch = mc.thePlayer.rotationPitch;
            BlockPos pos = null;
            float f = yaw;
            float[] floats = new float[]{0.0F, 45.0F, -45.0F, 90.0F, -90.0F, 180.0F};

            for (int j = 0; j < floats.length && pos == null; j++) {
                mc.thePlayer.rotationYaw = yaw + floats[(j + this.count13) % floats.length];
                mc.thePlayer.rotationPitch = 60.0F;
                Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
                Vec3 look = mc.thePlayer.getLook(1.0F);
                Vec3 vec = positionEyes.addVector(look.xCoord * 4.5, look.yCoord * 4.5, look.zCoord * 4.5);
                MovingObjectPosition hit = mc.theWorld.rayTraceBlocks(positionEyes, vec, true, false, false);
                if (hit != null && hit.typeOfHit == MovingObjectType.BLOCK && hit.sideHit == EnumFacing.UP) {
                    BlockPos blockPos = hit.getBlockPos();
                    double d = blockPos.getX() + 0.5 - mc.thePlayer.posX;
                    double d2 = blockPos.getZ() + 0.5 - mc.thePlayer.posZ;
                    if (d * d + d2 * d2 >= 0.81 && d * d + d2 * d2 <= 12.25) {
                        pos = blockPos;
                        f = mc.thePlayer.rotationYaw;
                    }
                }
            }

            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
            if (pos == null) {
                this.stop("Look at an open block top within boat placement range.");
            } else {
                this.blockPos = pos;
                this.factor = f;
                this.factor2 = 60.0F;
                this.flag5 = true;
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() && this.count7 != 0) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                    this.update();
                } else if (event instanceof EventPreMotion && ((EventPreMotion)event).getState() == EventState.PRE && this.count7 == 1 && this.flag5) {
                    ((EventPreMotion)event).setYaw(this.factor);
                    ((EventPreMotion)event).setPitch(this.factor2);
                } else if (event instanceof EventPostMotion) {
                    this.process5();
                } else if (event instanceof MovementEvent5) {
                    this.onMovementEvent5((MovementEvent5)event);
                } else if (event instanceof EventPacketSend) {
                    this.onEventPacketSend((EventPacketSend)event);
                } else if (event instanceof EventPacketReceive) {
                    this.onEventPacketReceive((EventPacketReceive)event);
                }
            } else {
                this.handleFlag(false);
            }
        }
    }

    private void process5() {
        if (this.flag5 && this.count7 == 1 && mc.thePlayer != null) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(this.count10);
            if (!this.matchesItemStack(stack)) {
                this.flag5 = false;
            } else {
                this.handleInt(this.count10);
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(stack));
                this.itemSet2.add(this.count10);
                this.count13++;
                this.flag5 = false;
                this.timestamp5 = System.currentTimeMillis() + 700L;
            }
        }
    }

    private void process3() {
        if (this.count9 >= 0 && mc.thePlayer != null && mc.thePlayer.inventory.currentItem == this.count10) {
            this.handleInt(this.count9);
        }

        this.count9 = this.count10 = -1;
    }

    private void onMovementEvent5(MovementEvent5 movementEvent5) {
        if (this.count7 > 0 && this.count7 < 5) {
            movementEvent5.setFactor(0.0F);
            movementEvent5.setFactor2(0.0F);
            movementEvent5.setJump(false);
            movementEvent5.setSneak(false);
        } else if (this.count7 == 5 && movementEvent5.isSneak()) {
            movementEvent5.setFactor(movementEvent5.getFactor() / 0.3F);
            movementEvent5.setFactor2(movementEvent5.getFactor2() / 0.3F);
            movementEvent5.setSneak(false);
        }
    }

    private EntityBoat computeEntityBoat() {
        if (this.count13 != 0 && this.blockPos != null) {
            EntityBoat entity = null;
            double d = 16.0;

            for (Object obj : mc.theWorld.loadedEntityList) {
                if (obj instanceof EntityBoat entity2
                    && !this.itemSet.contains(entity2.getEntityId())
                    && !entity2.isDead
                    && (entity2.riddenByEntity == null || entity2.riddenByEntity == mc.thePlayer)) {
                    double d2 = entity2.posX - (this.blockPos.getX() + 0.5);
                    double d3 = entity2.posY - (this.blockPos.getY() + 1);
                    double d4 = entity2.posZ - (this.blockPos.getZ() + 0.5);
                    if (!(d2 * d2 + d3 * d3 + d4 * d4 > 4.0)) {
                        double dist = entity2.getDistanceSqToEntity(mc.thePlayer);
                        if (dist < d) {
                            d = dist;
                            entity = entity2;
                        }
                    }
                }
            }

            return entity;
        }
        return null;
    }

    private void onEventPacketSend(EventPacketSend event) {
        if (event.getPacket() instanceof C0FPacketConfirmTransaction && this.flag) {
            synchronized (this.entries) {
                if (this.entries.size() >= 512) {
                    this.flag2 = true;
                } else {
                    this.entries.add(event.getPacket());
                    event.setCancelled(true);
                }
            }
        }
    }

    private void stop(String str) {
        this.handleFlag(true);
        this.handleString(str);
        this.handleFlag3(false);
    }

    public BoatFly() {
        super("BoatFly", 0, Category.MOVEMENT);
        this.handleSettingArray(new Setting[]{this.maximumBoatsSetting, this.speedSetting, this.flightTimeSetting});
    }

    private int computeInt() {
        synchronized (this.entries) {
            return this.entries.size();
        }
    }

    private void handleString(String str) {
        NotificationFeedHandler.handleString("§e[BoatFly] §7" + str);
    }

    private void handleFlag(boolean flag) {
        int n = this.count7;
        this.count7 = 0;
        if (flag) {
            this.process();
        } else {
            this.flag = false;
            synchronized (this.entries) {
                this.entries.clear();
            }
        }

        if (flag) {
            this.process3();
        } else {
            this.count9 = this.count10 = -1;
        }

        if (flag && mc.thePlayer != null && !this.flag4 && n >= 4) {
            mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0.0;
        }

        this.count8 = this.count11 = -1;
        this.count13 = this.count14 = this.count15 = 0;
        this.flag5 = this.flag6 = this.flag3 = this.flag4 = this.flag2 = false;
        this.blockPos = null;
        this.itemSet.clear();
        this.itemSet2.clear();
    }

    private void process() {
        synchronized (this.entries) {
            this.flag = false;
            if (mc.thePlayer != null && mc.thePlayer.sendQueue != null && !this.flag4) {
                for (Packet packet : this.entries) {
                    NetworkHelper.handlePacket(packet);
                }
            }

            this.entries.clear();
        }
    }

    private void onEventPacketReceive(EventPacketReceive event) {
        Packet packet = event.getPacket();
        if (packet instanceof S01PacketJoinGame || packet instanceof S07PacketRespawn || packet instanceof S40PacketDisconnect) {
            this.flag4 = true;
        } else if (this.flag && packet instanceof S08PacketPlayerPosLook) {
            this.flag3 = true;
        }
    }

    private void update() {
        EntityPlayerSP player = mc.thePlayer;
        long now = System.currentTimeMillis();
        if (!this.flag4 && player.getEntityId() == this.count11 && player.ticksExisted >= this.count12) {
            this.count12 = player.ticksExisted;
            if (player.isDead) {
                this.stop("Stopped: player died.");
            } else if (this.flag3) {
                this.stop("Stopped: server corrected your position.");
            } else if (this.flag2) {
                this.stop("Stopped: transaction queue limit reached.");
            } else if (this.flag && now - this.timestamp2 >= 19000L) {
                this.stop("Stopped: transaction hold expired.");
            } else if (this.count7 <= 3 && now - this.timestamp > 5000L) {
                this.stop("Boat setup timed out.");
            } else {
                if (this.count7 == 1) {
                    EntityBoat entity = this.computeEntityBoat();
                    if (entity != null) {
                        this.count8 = entity.getEntityId();
                        this.count7 = 2;
                        this.timestamp = now;
                        this.timestamp5 = now;
                        this.flag5 = false;
                        this.handleString("Boat placed; mounting it.");
                    } else if (!this.flag5 && now >= this.timestamp5 && this.count13 < this.count14) {
                        this.process4();
                    }
                }

                if (this.count7 == 2) {
                    Entity ridingEntity = player.ridingEntity;
                    Entity entityByID = mc.theWorld.getEntityByID(this.count8);
                    if (ridingEntity != null && ridingEntity.getEntityId() == this.count8) {
                        this.handleInt(this.computeInt2());
                        this.count7 = 3;
                        this.timestamp = now;
                        this.timestamp5 = now + 100L;
                        this.handleString("Mounted. Breaking the boat.");
                    } else if (entityByID instanceof EntityBoat && !entityByID.isDead) {
                        if (ridingEntity != null || entityByID.getDistanceSqToEntity(player) > 16.0) {
                            this.stop("Could not mount the placed boat.");
                        } else if (now >= this.timestamp5) {
                            player.sendQueue.addToSendQueue(new C02PacketUseEntity(entityByID, C02PacketUseEntity.Action.INTERACT));
                            this.timestamp5 = now + 300L;
                        }
                    } else {
                        this.stop("Boat disappeared before mounting.");
                    }
                }

                if (this.count7 == 3) {
                    Entity entityByID2 = mc.theWorld.getEntityByID(this.count8);
                    if (entityByID2 != null && !entityByID2.isDead) {
                        if (player.ridingEntity == null || player.ridingEntity.getEntityId() != this.count8) {
                            this.stop("Dismounted before the boat broke.");
                        } else if (now >= this.timestamp5) {
                            if (!this.flag) {
                                this.flag = true;
                                this.timestamp2 = now;
                            }

                            player.swingItem();
                            player.sendQueue.addToSendQueue(new C02PacketUseEntity(entityByID2, C02PacketUseEntity.Action.ATTACK));
                            this.timestamp5 = now + 150L;
                        }
                    } else {
                        if (player.ridingEntity != null) {
                            return;
                        }

                        if (!this.flag) {
                            this.stop("Boat disappeared before acknowledgements were captured.");
                            return;
                        }

                        this.count7 = 4;
                        this.timestamp = now;
                        this.process3();
                        player.motionX = player.motionY = player.motionZ = 0.0;
                        this.handleString("Boat gone. Flight starts in 1 second; " + this.computeInt() + " transactions held.");
                    }
                }

                if (this.count7 == 4) {
                    player.motionX = player.motionY = player.motionZ = 0.0;
                    if (player.ridingEntity != null) {
                        this.stop("Stopped: mounted another entity.");
                        return;
                    }

                    if (now - this.timestamp >= 1000L) {
                        if (this.computeInt() == 0) {
                            this.stop("No acknowledgements captured; flight cancelled.");
                            return;
                        }

                        this.count7 = 5;
                        this.timestamp3 = now;
                        this.count15 = 0;
                        this.flag6 = false;
                        this.handleString("Flight active. Jump up; sneak down.");
                    }
                }

                if (this.count7 == 5) {
                    if (now - this.timestamp3 >= this.timestamp4) {
                        this.stop("Flight finished.");
                        return;
                    }

                    if (player.ridingEntity != null) {
                        this.stop("Stopped: mounted another entity.");
                        return;
                    }

                    if (!player.onGround) {
                        this.flag6 = true;
                    }

                    this.count15 = player.onGround && this.flag6 ? this.count15 + 1 : 0;
                    if (this.count15 >= 3) {
                        this.stop("Landed.");
                        return;
                    }

                    this.flightHandler4.handleNumberSetting(this.speedSetting);
                }
            }
        } else {
            this.handleFlag(false);
            this.handleString("Stopped: world changed.");
        }
    }
}
