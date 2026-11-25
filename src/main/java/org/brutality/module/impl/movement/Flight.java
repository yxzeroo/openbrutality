package org.brutality.module.impl.movement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.AxisAlignedBB;
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
import org.brutality.event.impl.player.PlayerEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.movement.flight.FlightHandler3;
import org.brutality.module.impl.movement.flight.FlightHandler4;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.network.NetworkHelper;
import org.brutality.utils.player.GetSpeedHelper;
import org.brutality.utils.player.PlayerHelper3;
import org.brutality.utils.player.SwapHelper;
import org.brutality.utils.timer.TimerUtil;

public class Flight extends Module {
    private final ModeSetting boatFlySetting = new ModeSetting(
        "Mode",
        this,
        "Vanilla",
        Arrays.asList(
            "Vanilla",
            "Creative",
            "Zonecraft",
            "Watchdog",
            "AirWalk",
            "Viper",
            "Verus",
            "Minemen",
            "Old NCP",
            "Slime",
            "Custom",
            "Packet",
            "Minemora",
            "Vulcan",
            "Boat Fly"
        )
    );
    private final NumberSetting teleportDelaySetting = new NumberSetting("Teleport Delay", this, 5.0, 1.0, 20.0, 1.0);
    private final NumberSetting teleportLengthSetting = new NumberSetting("Teleport Length", this, 5.0, 1.0, 20.0, 1.0);
    private final NumberSetting timerAmountSetting = new NumberSetting("Timer Amount", this, 1.0, 0.1, 3.0, 0.1);
    private final NumberSetting horizontalSpeedSetting = new NumberSetting("Horizontal Speed", this, 2.0, 0.0, 5.0, 0.1);
    private final NumberSetting verticalSpeedSetting = new NumberSetting("Vertical Speed", this, 1.0, 0.0, 5.0, 0.1);
    private final NumberSetting speedSetting = new NumberSetting("Speed", this, 1.0, 0.1, 15.0, 0.1);
    private final BooleanSetting viewBobbingSetting = new BooleanSetting("View Bobbing", this, true);
    private final BooleanSetting antiKickSetting = new BooleanSetting("Anti-kick", this, false);
    private final BooleanSetting damageSetting = new BooleanSetting("Damage", this, false);
    private final ModeSetting ncpSetting = new ModeSetting("Damage Mode", this, "Vanilla", Arrays.asList("Vanilla", "Suffocate", "NCP"));
    private final NumberSetting motionYSetting = new NumberSetting("Motion Y", this, 0.0, -0.3, 0.3, 0.01);
    private final BooleanSetting customSpeedSetting = new BooleanSetting("Custom Speed", this, false);
    private final NumberSetting speedAmountSetting = new NumberSetting("Speed Amount", this, 0.2, 0.05, 9.0, 0.1);
    private final NumberSetting boatFlyMaximumBoatsSetting = new NumberSetting("Boat Fly: Maximum Boats", this, 9.0, 1.0, 9.0, 1.0)
        .computeNumberSetting(() -> this.boatFlySetting.matchesString2("Boat Fly"));
    private final NumberSetting boatFlyHorizontalSpeedSetting = new NumberSetting("Boat Fly: Horizontal Speed", this, 0.5, 0.1, 3.0, 0.05)
        .computeNumberSetting(() -> this.boatFlySetting.matchesString2("Boat Fly"));
    private final NumberSetting boatFlyVerticalSpeedSetting = new NumberSetting("Boat Fly: Vertical Speed", this, 0.3, 0.1, 1.0, 0.05)
        .computeNumberSetting(() -> this.boatFlySetting.matchesString2("Boat Fly"));
    private final NumberSetting boatFlyFlightTimeSetting = new NumberSetting("Boat Fly: Flight Time", this, 15.0, 1.0, 15.0, 1.0)
        .computeNumberSetting(() -> this.boatFlySetting.matchesString2("Boat Fly"));
    private final FlightHandler4 flightHandler4 = new FlightHandler4();
    private final FlightHandler3 flightHandler3 = new FlightHandler3();
    private final TimerUtil timerUtil = new TimerUtil();
    private final CopyOnWriteArrayList<Packet> entries = new CopyOnWriteArrayList<>();
    private final List<Packet<?>> entries2 = new ArrayList<>();
    private static final int count = 0;
    private static final int count2 = 1;
    private static final int count3 = 2;
    private static final int count4 = 3;
    private static final int count5 = 4;
    private static final int count6 = 5;
    private volatile int count7 = 0;
    private volatile boolean flag;
    private volatile boolean flag2;
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
    private boolean flag3;
    private boolean flag4;
    private boolean flag5;
    private boolean flag6;
    private final Set<Integer> itemSet = new HashSet<>();
    private final Set<Integer> itemSet2 = new HashSet<>();
    private final Set<BlockPos> itemSet3 = new HashSet<>();
    private int count16;
    private int count17;
    private int count18;
    private int count19;
    private int slot;
    private int count20;
    private double moveSpeed;
    private double amount;
    private double x;
    private double y;
    private double z;
    private double amount2;
    private double amount3;
    private double amount4;
    private boolean flag7;
    private boolean flag8;
    private boolean flag9;
    private boolean flag10;
    private boolean flag11;
    private float factor3;
    private final Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void onDisable() {
        if (this.mc.thePlayer != null) {
            this.mc.thePlayer.capabilities.isFlying = false;
        }

        if (this.mc.timer != null) {
            this.mc.timer.timerSpeed = 1.0F;
        }

        this.handleFlag(true);
        this.entries.forEach(NetworkHelper::handlePacket);
        this.entries.clear();
        if (this.mc.thePlayer == null
            || !this.boatFlySetting.matchesString2("Vanilla")
                && !this.boatFlySetting.matchesString2("Minemen")
                && !this.boatFlySetting.matchesString2("Old NCP")
                && !this.boatFlySetting.matchesString2("Watchdog")
                && !this.boatFlySetting.matchesString2("Creative")) {
            if (this.mc.thePlayer != null && this.boatFlySetting.matchesString2("Slime")) {
                this.mc.thePlayer.inventory.currentItem = this.slot;
            }
        } else {
            this.mc.thePlayer.motionX = 0.0;
            this.mc.thePlayer.motionY = 0.0;
            this.mc.thePlayer.motionZ = 0.0;
        }

        super.onDisable();
    }

    private void process() {
        if (this.mc.thePlayer != null && this.mc.theWorld != null) {
            this.handleFlag(false);
            if (this.mc.thePlayer.onGround && !this.mc.thePlayer.isDead && this.mc.thePlayer.ridingEntity == null) {
                int n = 0;

                for (int i = 0; i < 9; i++) {
                    if (this.matchesItemStack(this.mc.thePlayer.inventory.getStackInSlot(i))) {
                        n++;
                    }
                }

                if (n == 0) {
                    NotificationFeedHandler.handleString("§e[Boat Fly] §cPut a boat in your hotbar first.");
                } else {
                    this.count9 = this.mc.thePlayer.inventory.currentItem;
                    this.count10 = this.count9;
                    this.count11 = this.mc.thePlayer.getEntityId();
                    this.count15 = this.mc.thePlayer.ticksExisted;
                    this.timestamp4 = (long)(this.boatFlyFlightTimeSetting.computeDouble() * 1000.0);
                    this.count13 = Math.min(n, (int)this.boatFlyMaximumBoatsSetting.computeDouble());

                    for (Object obj : this.mc.theWorld.loadedEntityList) {
                        if (obj instanceof EntityBoat) {
                            this.itemSet.add(((EntityBoat)obj).getEntityId());
                        }
                    }

                    this.count7 = 1;
                    this.timestamp = System.currentTimeMillis();
                    this.process4();
                }
            } else {
                NotificationFeedHandler.handleString("§e[Boat Fly] §cStand on the ground outside a vehicle first.");
            }
        }
    }

    private int computeInt() {
        synchronized (this.entries2) {
            return this.entries2.size();
        }
    }

    private void process3() {
        if (this.flag3 && this.count7 == 1 && this.mc.thePlayer != null) {
            ItemStack stack = this.mc.thePlayer.inventory.getStackInSlot(this.count10);
            if (!this.matchesItemStack(stack)) {
                this.flag3 = false;
            } else {
                this.handleInt(this.count10);
                this.mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(stack));
                this.count12++;
                this.itemSet2.add(this.count10);
                this.flag3 = false;
                this.timestamp5 = System.currentTimeMillis() + 700L;
            }
        }
    }

    private void onEvent2(Event event) {
        if (this.mc.thePlayer != null && this.mc.theWorld != null) {
            if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                this.process7();
            } else if (event instanceof EventPreMotion && ((EventPreMotion)event).getState() == EventState.PRE && this.count7 == 1 && this.flag3) {
                EventPreMotion event2 = (EventPreMotion)event;
                event2.setYaw(this.factor);
                event2.setPitch(this.factor2);
            } else if (event instanceof EventPostMotion) {
                this.process3();
            } else if (event instanceof MovementEvent5 movementEvent5) {
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
            } else if (event instanceof EventPacketSend event3) {
                if (this.flag && event3.getPacket() instanceof C0FPacketConfirmTransaction) {
                    synchronized (this.entries2) {
                        if (this.entries2.size() >= 512) {
                            this.flag2 = true;
                        } else {
                            this.entries2.add(event3.getPacket());
                            event3.setCancelled(true);
                        }
                    }
                }
            } else if (event instanceof EventPacketReceive) {
                Packet packet = ((EventPacketReceive)event).getPacket();
                if (packet instanceof S01PacketJoinGame || packet instanceof S07PacketRespawn || packet instanceof S40PacketDisconnect) {
                    this.flag6 = true;
                } else if (this.flag && packet instanceof S08PacketPlayerPosLook) {
                    this.flag5 = true;
                }
            }
        } else {
            this.handleFlag(false);
        }
    }

    private void handleFlag(boolean flag) {
        this.count7 = 0;
        if (flag) {
            this.process5();
        } else {
            this.flag = false;
            synchronized (this.entries2) {
                this.entries2.clear();
            }
        }

        if (flag) {
            this.process6();
        } else {
            this.count9 = this.count10 = -1;
        }

        this.count8 = this.count11 = -1;
        this.count12 = this.count13 = this.count14 = 0;
        this.flag3 = this.flag4 = this.flag5 = this.flag6 = this.flag2 = false;
        this.blockPos = null;
        this.itemSet.clear();
        this.itemSet2.clear();
    }

    private int computeInt2() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = this.mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemSword) {
                return i;
            }
        }

        return this.count9;
    }

    private void process4() {
        EntityPlayerSP player = this.mc.thePlayer;
        int n = -1;

        for (int i = 0; i < 9; i++) {
            if (!this.itemSet2.contains(i) && this.matchesItemStack(player.inventory.getStackInSlot(i))) {
                n = i;
                break;
            }
        }

        if (n < 0) {
            this.handleString("No more boats available in the hotbar.");
        } else {
            this.handleInt(n);
            float yaw = player.rotationYaw;
            float pitch = player.rotationPitch;
            BlockPos pos = null;
            float f = yaw;
            float f2 = 60.0F;
            float[] floats = new float[]{0.0F, 45.0F, -45.0F, 90.0F, -90.0F, 180.0F};

            for (int j = 0; j < floats.length && pos == null; j++) {
                player.rotationYaw = yaw + floats[(j + this.count12) % floats.length];
                player.rotationPitch = 60.0F;
                Vec3 positionEyes = player.getPositionEyes(1.0F);
                Vec3 look = player.getLook(1.0F);
                MovingObjectPosition hit = this.mc
                    .theWorld
                    .rayTraceBlocks(positionEyes, positionEyes.addVector(look.xCoord * 4.5, look.yCoord * 4.5, look.zCoord * 4.5), true, false, false);
                if (hit != null && hit.typeOfHit == MovingObjectType.BLOCK && hit.sideHit == EnumFacing.UP) {
                    BlockPos blockPos = hit.getBlockPos();
                    double d = blockPos.getX() + 0.5 - player.posX;
                    double d2 = blockPos.getZ() + 0.5 - player.posZ;
                    if (d * d + d2 * d2 >= 0.81 && d * d + d2 * d2 <= 12.25) {
                        pos = blockPos;
                        f = player.rotationYaw;
                    }
                }
            }

            player.rotationYaw = yaw;
            player.rotationPitch = pitch;
            if (pos == null) {
                this.handleString("Look at an open block top within boat placement range.");
            } else {
                this.blockPos = pos;
                this.factor = f;
                this.factor2 = f2;
                this.flag3 = true;
            }
        }
    }

    private boolean matchesItemStack(ItemStack stack) {
        return stack != null && stack.getItem() == Items.boat;
    }

    private void process5() {
        synchronized (this.entries2) {
            this.flag = false;
            if (this.mc.thePlayer != null && this.mc.thePlayer.sendQueue != null && !this.flag6) {
                for (Packet packet : this.entries2) {
                    NetworkHelper.handlePacket(packet);
                }
            }

            this.entries2.clear();
        }
    }

    private void process6() {
        if (this.count9 >= 0 && this.mc.thePlayer != null && this.mc.thePlayer.inventory.currentItem == this.count10) {
            this.handleInt(this.count9);
        }

        this.count9 = this.count10 = -1;
    }

    private void process7() {
        long now = System.currentTimeMillis();
        if (this.count7 != 0) {
            if (!this.flag6 && this.mc.thePlayer.getEntityId() == this.count11 && this.mc.thePlayer.ticksExisted >= this.count15) {
                this.count15 = this.mc.thePlayer.ticksExisted;
                if (this.flag5) {
                    this.handleString("Stopped because the server corrected your position.");
                } else if (this.flag2) {
                    this.handleString("Stopped because the transaction queue reached its 512-packet limit.");
                } else if (this.flag && now - this.timestamp2 >= 19000L) {
                    this.handleString("Transaction hold timed out; queued packets released.");
                } else if (this.count7 <= 3 && now - this.timestamp > 5000L) {
                    this.handleString("Boat setup timed out.");
                } else {
                    if (this.count7 == 1) {
                        EntityBoat entity = this.computeEntityBoat();
                        if (entity != null) {
                            this.count8 = entity.getEntityId();
                            this.count7 = 2;
                            this.timestamp = now;
                            this.timestamp5 = now;
                        } else if (!this.flag3 && this.count12 < this.count13 && now >= this.timestamp5) {
                            this.process4();
                        } else if (this.count12 >= this.count13 && now >= this.timestamp5) {
                            this.handleString("No placed boat appeared.");
                        }
                    } else if (this.count7 == 2) {
                        Entity ridingEntity = this.mc.thePlayer.ridingEntity;
                        Entity entityByID = this.mc.theWorld.getEntityByID(this.count8);
                        if (ridingEntity != null && ridingEntity.getEntityId() == this.count8) {
                            this.handleInt(this.computeInt2());
                            this.count7 = 3;
                            this.timestamp = now;
                            this.timestamp5 = now + 100L;
                        } else if (entityByID instanceof EntityBoat && !entityByID.isDead) {
                            if (ridingEntity != null || entityByID.getDistanceSqToEntity(this.mc.thePlayer) > 16.0) {
                                this.handleString("Could not mount the placed boat.");
                            } else if (now >= this.timestamp5) {
                                this.mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entityByID, C02PacketUseEntity.Action.INTERACT));
                                this.timestamp5 = now + 300L;
                            }
                        } else {
                            this.handleString("Boat disappeared before mounting.");
                        }
                    } else if (this.count7 == 3) {
                        Entity entityByID2 = this.mc.theWorld.getEntityByID(this.count8);
                        if (entityByID2 != null && !entityByID2.isDead) {
                            if (this.mc.thePlayer.ridingEntity == null || this.mc.thePlayer.ridingEntity.getEntityId() != this.count8) {
                                this.handleString("Dismounted before the boat broke.");
                            } else if (now >= this.timestamp5) {
                                if (!this.flag) {
                                    this.flag = true;
                                    this.timestamp2 = now;
                                }

                                this.mc.thePlayer.swingItem();
                                this.mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entityByID2, C02PacketUseEntity.Action.ATTACK));
                                this.timestamp5 = now + 150L;
                            }
                        } else {
                            if (this.mc.thePlayer.ridingEntity != null) {
                                return;
                            }

                            if (!this.flag) {
                                this.handleString("Boat broke before transaction capture started.");
                                return;
                            }

                            this.count7 = 4;
                            this.timestamp = now;
                            this.process6();
                            this.mc.thePlayer.motionX = this.mc.thePlayer.motionY = this.mc.thePlayer.motionZ = 0.0;
                        }
                    } else if (this.count7 == 4) {
                        this.mc.thePlayer.motionX = this.mc.thePlayer.motionY = this.mc.thePlayer.motionZ = 0.0;
                        if (this.mc.thePlayer.ridingEntity != null) {
                            this.handleString("Mounted another entity during flight setup.");
                            return;
                        }

                        if (now - this.timestamp >= 1000L) {
                            if (this.computeInt() == 0) {
                                this.handleString("No acknowledgements captured; flight cancelled.");
                                return;
                            }

                            this.count7 = 5;
                            this.timestamp3 = now;
                            this.flag4 = false;
                            this.count14 = 0;
                        }
                    } else if (this.count7 == 5) {
                        if (now - this.timestamp3 >= this.timestamp4) {
                            this.handleString("Flight time ended.");
                            return;
                        }

                        if (this.mc.thePlayer.ridingEntity != null) {
                            this.handleString("Mounted another entity.");
                            return;
                        }

                        if (!this.mc.thePlayer.onGround) {
                            this.flag4 = true;
                        }

                        this.count14 = this.mc.thePlayer.onGround && this.flag4 ? this.count14 + 1 : 0;
                        if (this.count14 >= 3) {
                            this.handleString("Landed.");
                            return;
                        }

                        double d = this.mc.currentScreen == null && this.mc.gameSettings.keyBindJump.isKeyDown()
                            ? this.boatFlyVerticalSpeedSetting.computeDouble()
                            : 0.0;
                        if (this.mc.currentScreen == null && this.mc.gameSettings.keyBindSneak.isKeyDown()) {
                            d -= this.boatFlyVerticalSpeedSetting.computeDouble();
                        }

                        this.mc.thePlayer.motionY = d;
                        if (this.mc.currentScreen == null && GetSpeedHelper.isMoving()) {
                            GetSpeedHelper.setSpeed(this.boatFlyHorizontalSpeedSetting.computeDouble());
                        }
                    }
                }
            } else {
                this.handleFlag(false);
            }
        }
    }

    private void handleInt(int n) {
        if (n >= 0 && n <= 8 && this.mc.thePlayer != null) {
            this.mc.thePlayer.inventory.currentItem = n;
            this.mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(n));
            this.count10 = n;
        }
    }

    private EntityBoat computeEntityBoat() {
        if (this.count12 != 0 && this.blockPos != null) {
            EntityBoat entity = null;
            double d = 16.0;

            for (Object obj : this.mc.theWorld.loadedEntityList) {
                if (obj instanceof EntityBoat entity2
                    && !this.itemSet.contains(entity2.getEntityId())
                    && !entity2.isDead
                    && (entity2.riddenByEntity == null || entity2.riddenByEntity == this.mc.thePlayer)) {
                    double d2 = entity2.posX - (this.blockPos.getX() + 0.5);
                    double d3 = entity2.posY - (this.blockPos.getY() + 1);
                    double d4 = entity2.posZ - (this.blockPos.getZ() + 0.5);
                    if (!(d2 * d2 + d3 * d3 + d4 * d4 > 4.0)) {
                        double dist = entity2.getDistanceSqToEntity(this.mc.thePlayer);
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

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            this.flag10 = false;
            this.flag7 = false;
            this.count17 = 0;
            this.count16 = 0;
            this.count18 = 0;
            this.count19 = 0;
            this.factor3 = 0.0F;
            this.flag8 = false;
            this.entries.clear();
            this.timerUtil.reset();
            this.moveSpeed = 0.0;
            this.flag9 = false;
            this.count20 = 0;
            if (this.mc.thePlayer != null) {
                this.amount2 = this.mc.thePlayer.posX;
                this.amount3 = this.mc.thePlayer.posY;
                this.amount4 = this.mc.thePlayer.posZ;
                this.y = 0.0;
                this.slot = this.mc.thePlayer.inventory.currentItem;
                if (this.boatFlySetting.matchesString2("Boat Fly")) {
                    this.process();
                } else if (this.boatFlySetting.matchesString2("Creative")) {
                    this.mc.thePlayer.capabilities.isFlying = true;
                } else if (this.boatFlySetting.matchesString2("Old NCP")) {
                    this.moveSpeed = 1.6;
                } else if (this.boatFlySetting.matchesString2("Slime")) {
                    int n = SwapHelper.computeInt2(Blocks.slime_block);
                    if (n == -1) {
                        NotificationFeedHandler.handleString("§e[Flight] §cNo slime block found in hotbar!");
                        this.process28();
                        return;
                    }

                    this.mc.thePlayer.inventory.currentItem = n;
                }
            }

            super.onEnable();
        }
    }

    public Flight() {
        super("Flight", 0, Category.MOVEMENT);
        this.handleSettingArray(
            new Setting[]{
                this.boatFlySetting,
                this.speedSetting,
                this.teleportDelaySetting,
                this.teleportLengthSetting,
                this.timerAmountSetting,
                this.horizontalSpeedSetting,
                this.verticalSpeedSetting,
                this.viewBobbingSetting,
                this.antiKickSetting,
                this.damageSetting,
                this.ncpSetting,
                this.motionYSetting,
                this.customSpeedSetting,
                this.speedAmountSetting,
                this.boatFlyMaximumBoatsSetting,
                this.boatFlyHorizontalSpeedSetting,
                this.boatFlyVerticalSpeedSetting,
                this.boatFlyFlightTimeSetting
            }
        );
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (this.boatFlySetting.matchesString2("Boat Fly")) {
                    this.onEvent2(event);
                } else {
                    if (this.count7 != 0) {
                        this.handleFlag(true);
                    }

                    if (event instanceof EventUpdate) {
                        if (this.viewBobbingSetting.isEnabled()) {
                            this.mc.thePlayer.cameraYaw = this.mc.thePlayer.cameraPitch = 0.08F;
                        }

                        this.mc.timer.timerSpeed = (float)this.timerAmountSetting.computeDouble();
                        double d = this.mc.thePlayer.posX - this.mc.thePlayer.prevPosX;
                        double d2 = this.mc.thePlayer.posZ - this.mc.thePlayer.prevPosZ;
                        this.amount = Math.hypot(d, d2);
                        if (this.checkState44()) {
                            String text = this.boatFlySetting.getText();
                            switch (text) {
                                case "Creative":
                                    this.flightHandler3.handleNumberSetting(this.speedSetting);
                                    break;
                                case "Vanilla":
                                    this.flightHandler4.handleNumberSetting(this.speedSetting);
                                    if (this.antiKickSetting.isEnabled()) {
                                        this.mc.thePlayer.motionY = -0.0625;
                                    }
                                    break;
                                case "Watchdog":
                                    this.mc.thePlayer.motionY = 0.0;
                                    this.count16++;
                                    if (this.count16 == 1) {
                                        double d3 = this.mc.thePlayer.posX + -Math.sin(Math.toRadians(this.mc.thePlayer.rotationYaw)) * 7.99;
                                        double d4 = this.mc.thePlayer.posY - 1.75;
                                        double d5 = this.mc.thePlayer.posZ + Math.cos(Math.toRadians(this.mc.thePlayer.rotationYaw)) * 7.99;
                                        if (this.mc.theWorld.getBlockState(new BlockPos(d3, d4, d5)).getBlock() == Blocks.air) {
                                            NetworkHelper.handlePacket(new C03PacketPlayer.C04PacketPlayerPosition(d3, d4, d5, false));
                                            this.mc.thePlayer.setPosition(d3, d4, d5);
                                        }
                                    }
                                    break;
                                case "Vulcan":
                                    if (this.count16 == 0 && this.mc.thePlayer.onGround) {
                                        this.mc.thePlayer.jump();
                                        GetSpeedHelper.handleFloat2(0.5F);
                                        this.count16 = 1;
                                    } else if (this.count16 == 1 && this.mc.thePlayer.offGroundTicks == 8) {
                                        this.mc.thePlayer.jump();
                                        GetSpeedHelper.handleFloat2(0.35F);
                                        this.count16 = 2;
                                    } else if (this.count16 == 2 && this.mc.thePlayer.offGroundTicks == 17) {
                                        this.mc.thePlayer.jump();
                                        GetSpeedHelper.handleFloat2(0.3F);
                                        this.count16 = 3;
                                    } else if (this.count16 == 3 && this.mc.thePlayer.offGroundTicks == 30) {
                                        this.mc.thePlayer.motionY = 0.0;
                                    } else if (this.count16 == 3 && this.mc.thePlayer.onGround) {
                                        this.process28();
                                    }
                                    break;
                                case "Minemora":
                                    if (this.count16 < 3) {
                                        if (this.mc.thePlayer.onGround) {
                                            this.mc.thePlayer.jump();
                                            this.count16++;
                                        }
                                    } else {
                                        if (this.mc.thePlayer.hurtTime > 0 && !this.flag10) {
                                            this.flag10 = true;
                                        }

                                        if (this.flag10) {
                                            this.mc.thePlayer.motionY = -0.005;
                                            GetSpeedHelper.handleFloat2((float)(GetSpeedHelper.computeDouble2() * 1.5));
                                        }
                                    }
                                    break;
                                case "Zonecraft":
                                    this.count16++;
                                    if (this.count16 == 1) {
                                        GetSpeedHelper.handleFloat2(0.55F);
                                    }

                                    this.mc.thePlayer.motionY = 0.0;
                                    this.mc.thePlayer.posY += 0.1;
                                    break;
                                case "Verus":
                                    if (!this.mc.gameSettings.keyBindJump.isKeyDown()) {
                                        if (this.mc.thePlayer.onGround) {
                                            this.mc.thePlayer.motionY = 0.42F;
                                            this.flag11 = true;
                                        } else if (this.flag11) {
                                            if (!this.mc.thePlayer.isCollidedHorizontally) {
                                                this.mc.thePlayer.motionY = -0.0784000015258789;
                                            }

                                            this.flag11 = false;
                                        }
                                    } else if (this.mc.thePlayer.ticksExisted % 3 == 0) {
                                        this.mc.thePlayer.motionY = 0.42F;
                                    }

                                    GetSpeedHelper.handleFloat2(this.mc.gameSettings.keyBindJump.isKeyDown() ? 0.0F : 0.33F);
                                case "AirWalk":
                                default:
                                    break;
                                case "Viper":
                                    this.mc.thePlayer.motionY = this.mc.gameSettings.keyBindJump.isKeyDown()
                                        ? 1.0
                                        : (this.mc.gameSettings.keyBindSneak.isKeyDown() ? -1.0 : 0.0);
                                    GetSpeedHelper.handleFloat2(0.3F);
                                    break;
                                case "Old NCP":
                                    if (this.flag10) {
                                        double d6 = GetSpeedHelper.computeDouble2();
                                        if (!GetSpeedHelper.isMoving() || this.mc.thePlayer.isCollidedHorizontally) {
                                            this.moveSpeed = d6;
                                        }

                                        if (this.moveSpeed > d6) {
                                            this.moveSpeed = this.moveSpeed - this.moveSpeed / 159.0;
                                        }

                                        this.moveSpeed = Math.max(d6, this.moveSpeed);
                                        this.mc.timer.timerSpeed = 1.0F;
                                        if (GetSpeedHelper.isMoving()) {
                                            GetSpeedHelper.handleFloat2((float)this.moveSpeed);
                                        }

                                        this.mc.thePlayer.motionY = 0.0;
                                        this.mc.thePlayer.posY -= 1.0E-10;
                                    } else if (this.mc.thePlayer.onGround) {
                                        PlayerHelper3.handleWatchdogupMode(PlayerHelper3.WatchdogupMode.WATCHDOGUP);
                                        this.mc.thePlayer.jump();
                                        this.flag10 = true;
                                    }
                                    break;
                                case "Slime":
                                    this.count16++;
                                    switch (this.count16) {
                                        case 1:
                                            if (this.mc.thePlayer.onGround) {
                                                this.mc.thePlayer.jump();
                                            }
                                            break;
                                        case 7:
                                            BlockPos pos = new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 2.0, this.mc.thePlayer.posZ);
                                            this.mc.thePlayer.rotationPitchHead = 90.0F;
                                            if (this.mc
                                                .playerController
                                                .onPlayerRightClick(
                                                    this.mc.thePlayer, this.mc.theWorld, this.mc.thePlayer.getHeldItem(), pos, EnumFacing.UP, new Vec3(pos)
                                                )) {
                                                this.mc.thePlayer.swingItem();
                                            }
                                    }

                                    if (this.count16 > 8) {
                                        GetSpeedHelper.handleFloat2(0.3F);
                                        this.mc.thePlayer.motionY = 0.0;
                                    }
                                    break;
                                case "Custom":
                                    this.count16++;
                                    if (this.count16 == 1 && this.damageSetting.isEnabled()) {
                                        PlayerHelper3.handleWatchdogupMode(PlayerHelper3.WatchdogupMode.valueOf(this.ncpSetting.getText().toUpperCase()));
                                    }

                                    this.mc.thePlayer.motionY = this.motionYSetting.computeDouble();
                                    if (this.customSpeedSetting.isEnabled()) {
                                        GetSpeedHelper.handleFloat2((float)this.speedAmountSetting.computeDouble());
                                    }
                                    break;
                                case "Packet":
                                    this.mc.thePlayer.motionY = 0.0;
                                    if (GetSpeedHelper.isMoving() && this.mc.thePlayer.ticksExisted % this.teleportDelaySetting.computeDouble() == 0.0) {
                                        double d7 = this.mc.thePlayer.posX
                                            + -Math.sin(Math.toRadians(this.mc.thePlayer.rotationYaw)) * this.teleportLengthSetting.computeDouble();
                                        double d8 = this.mc.thePlayer.posZ
                                            + Math.cos(Math.toRadians(this.mc.thePlayer.rotationYaw)) * this.teleportLengthSetting.computeDouble();
                                        NetworkHelper.handlePacket(new C03PacketPlayer.C04PacketPlayerPosition(d7, this.mc.thePlayer.posY, d8, false));
                                        this.mc.thePlayer.setPosition(d7, this.mc.thePlayer.posY, d8);
                                    }
                            }
                        }
                    } else if (event instanceof EventPacketSend) {
                        if (this.mc.isSingleplayer() || this.mc.thePlayer == null) {
                            return;
                        }

                        EventPacketSend event2 = (EventPacketSend)event;
                        if (this.boatFlySetting.matchesString2("Slime") && this.count16 > 7 && NetworkHelper.matchesPacket(event2.getPacket())) {
                            event2.cancel();
                            this.entries.add(event2.getPacket());
                        }

                        if (this.boatFlySetting.matchesString2("Watchdog") && event2.getPacket() instanceof C03PacketPlayer) {
                            event2.cancel();
                        }
                    } else if (event instanceof EventPacketReceive event3) {
                        if (event3.getPacket() instanceof S08PacketPlayerPosLook && !this.flag9) {
                            this.flag9 = true;
                        }
                    } else if (event instanceof PlayerEvent playerEvent
                        && (this.boatFlySetting.matchesString2("AirWalk") || this.boatFlySetting.matchesString2("Verus"))) {
                        AxisAlignedBB box = AxisAlignedBB.fromBounds(-5.0, -1.0, -5.0, 5.0, 1.0, 5.0)
                            .offset(playerEvent.getBlockPos().getX(), playerEvent.getBlockPos().getY(), playerEvent.getBlockPos().getZ());
                        playerEvent.setBoundingBox(box);
                    }
                }
            }
        }
    }

    private void handleString(String str) {
        this.handleFlag(true);
        NotificationFeedHandler.handleString("§e[Boat Fly] §7" + str);
    }
}
