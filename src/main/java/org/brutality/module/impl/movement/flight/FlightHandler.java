package org.brutality.module.impl.movement.flight;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import org.brutality.core.CoreHandler2;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.movement.MovementEvent2;
import org.brutality.event.impl.movement.MovementEvent4;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.module.impl.combat.Displace;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.movement.KeepSprint;
import org.brutality.module.impl.movement.MovementFix;
import org.brutality.module.impl.movement.NoSlowDown;
import org.brutality.module.impl.movement.OnMouseHandler;
import org.brutality.module.impl.movement.Scaffold;
import org.brutality.module.impl.pit.hypixel.hud.Cake;
import org.brutality.module.impl.pit.hypixel.minorevent.CarePackage;
import org.brutality.module.impl.pit.hypixel.minorevent.DragonEgg;
import org.brutality.module.impl.player.Autoblockin;
import org.brutality.module.impl.player.ChestAura;
import org.brutality.utils.network.NetworkHelper;
import org.brutality.utils.network.SizeHelper;
import org.brutality.utils.performance.WorldQueryCache;
import org.brutality.utils.player.StartHotbarMovesHelper;
import org.brutality.utils.rotation.OnJumpHelper;

public final class FlightHandler {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final NoSlowDown noSlowDown;
    private final SizeHelper sizeHelper = new SizeHelper((packet, val) -> {
        NetworkManager connection = this.connection;
        EntityPlayerSP entityPlayerSP = this.entityPlayerSP;
        WorldClient worldClient = this.worldClient;
        Runnable task = () -> {
            if (connection != null && this.computeNetworkManager() == connection) {
                boolean flag = val && (packet instanceof C00PacketKeepAlive || packet instanceof C0FPacketConfirmTransaction);
                if (flag || mc.thePlayer == entityPlayerSP && mc.theWorld == worldClient) {
                    if (val) {
                        connection.sendPacketNoEvent(packet);
                    } else {
                        ((net.minecraft.network.Packet)packet).processPacket(mc.getNetHandler());
                    }
                }
            }
        };
        if (mc.isCallingFromMinecraftThread()) {
            task.run();
        } else {
            mc.addScheduledTask(task);
        }
    });
    private volatile EntityPlayerSP entityPlayerSP;
    private volatile WorldClient worldClient;
    private volatile NetworkManager connection;
    private Item item;
    private int slot = -1;
    private int itemDamage;
    private int count;
    private int count2;
    private int count3 = Integer.MIN_VALUE;
    private int count4 = Integer.MIN_VALUE;
    private int count5 = Integer.MIN_VALUE;
    private long timestamp;
    private boolean flag;
    private boolean flag2;
    private float[] floatValueArray;
    private static final long timestamp2 = 250L;

    private NetworkManager computeNetworkManager() {
        return mc.getNetHandler() == null ? null : mc.getNetHandler().getNetworkManager();
    }

    private boolean checkState() {
        return mc.thePlayer == null
            || mc.theWorld == null
            || mc.thePlayer.isDead
            || mc.thePlayer.getHealth() <= 0.0F
            || mc.currentScreen != null
            || mc.thePlayer.isRiding()
            || StartHotbarMovesHelper.flag
            || CoreHandler2.checkState()
            || Scaffold.checkState19()
            || WorldQueryCache.isActive();
    }

    public void handleFlag(boolean flag) {
        if (flag) {
            this.sizeHelper.flush();
        } else {
            this.sizeHelper.process();
        }

        this.entityPlayerSP = null;
        this.worldClient = null;
        this.connection = null;
        this.item = null;
        this.slot = -1;
        this.count = this.count2 = 0;
        this.flag = this.flag2 = false;
        this.floatValueArray = null;
        this.count3 = this.count4 = this.count5 = Integer.MIN_VALUE;
    }

    public void update() {
        if (this.noSlowDown.checkState9() && !this.checkState()) {
            NetworkManager networkManager = this.computeNetworkManager();
            ItemStack heldItem = mc.thePlayer.getHeldItem();
            if (this.entityPlayerSP != mc.thePlayer || this.worldClient != mc.theWorld || this.connection != networkManager) {
                this.handleFlag(false);
                this.entityPlayerSP = mc.thePlayer;
                this.worldClient = mc.theWorld;
                this.connection = networkManager;
            }

            if (heldItem != null && this.noSlowDown.matchesItemStack(heldItem)) {
                if (this.item != heldItem.getItem()
                    || this.slot != this.entityPlayerSP.inventory.currentItem
                    || heldItem.getItem() instanceof ItemPotion && this.itemDamage != heldItem.getItemDamage()) {
                    this.sizeHelper.flush();
                    this.count = 0;
                    this.flag = this.flag2 = false;
                    this.count3 = this.count4 = Integer.MIN_VALUE;
                    this.item = heldItem.getItem();
                    this.slot = this.entityPlayerSP.inventory.currentItem;
                    this.itemDamage = heldItem.getItemDamage();
                    this.floatValueArray = null;
                }

                int ticksExisted = this.entityPlayerSP.ticksExisted;
                if (ticksExisted < this.count3) {
                    this.handleFlag(false);
                } else {
                    if (this.count3 != ticksExisted) {
                        this.count3 = ticksExisted;
                        this.count2 = this.entityPlayerSP.onGround ? this.count2 + 1 : 0;
                        boolean usingItem = this.entityPlayerSP.isUsingItem();
                        if (usingItem && this.matchesItemStack(heldItem) && !this.flag2) {
                            this.count++;
                        } else if (!usingItem && this.flag) {
                            this.count = 0;
                            this.flag2 = false;
                            this.sizeHelper.flush();
                        }

                        this.flag = usingItem;
                    }

                    this.sizeHelper.handleLong(System.currentTimeMillis());
                    if (this.entityPlayerSP.isUsingItem() && this.matchesItemStack(heldItem) && !this.flag2) {
                        if (this.count > this.noSlowDown.computeInt2()) {
                            this.sizeHelper.setTimestamp(System.currentTimeMillis());
                        }

                        if (this.count > this.noSlowDown.computeInt()) {
                            mc.gameSettings.keyBindUseItem.pressed = false;
                            if (mc.playerController != null) {
                                mc.playerController.onStoppedUsingItem(this.entityPlayerSP);
                            } else {
                                NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                this.entityPlayerSP.stopUsingItem();
                            }

                            this.flag2 = true;
                            this.sizeHelper.flush();
                        }
                    }

                    this.process2();
                }
            } else {
                this.handleFlag(true);
            }
        } else {
            this.handleFlag(mc.thePlayer != null && mc.thePlayer == this.entityPlayerSP && mc.theWorld == this.worldClient && !mc.thePlayer.isDead);
        }
    }

    private void process2() {
        ItemStack heldItem = this.entityPlayerSP.getHeldItem();
        if (this.count5 != this.entityPlayerSP.ticksExisted || this.computeFloatArray() == null) {
            this.floatValueArray = null;
            if (heldItem != null
                && this.entityPlayerSP.isUsingItem()
                && this.matchesItemStack(heldItem)
                && !(heldItem.getItem() instanceof ItemBow)
                && !this.flag2
                && (!this.entityPlayerSP.onGround || this.count2 > 2)
                && this.entityPlayerSP.ticksExisted > 5
                && !mc.gameSettings.keyBindLeft.isKeyDown()
                && !mc.gameSettings.keyBindRight.isKeyDown()
                && System.currentTimeMillis() - this.timestamp >= timestamp2
                && !this.checkState2()) {
                float[] lastReportedRotations = this.entityPlayerSP.getLastReportedRotations();
                float f = MathHelper.wrapAngleTo180_float(this.entityPlayerSP.rotationYaw + 45.0F - lastReportedRotations[0]);
                float f2 = MathHelper.clamp_float(this.entityPlayerSP.rotationPitch, -90.0F, 90.0F) - lastReportedRotations[1];
                this.floatValueArray = OnJumpHelper.computeFloatArray(
                    new float[]{lastReportedRotations[0] + MathHelper.clamp_float(f, -10.0F, 10.0F), lastReportedRotations[1] + MathHelper.clamp_float(f2, -10.0F, 10.0F)}, lastReportedRotations, 10.0F
                );
                this.count5 = this.entityPlayerSP.ticksExisted;
            }
        }
    }

    public void process() {
        if (this.checkState3()
            && !this.checkState()
            && this.noSlowDown.checkState5()
            && this.entityPlayerSP.isUsingItem()
            && !this.flag2
            && this.count <= this.noSlowDown.computeInt2()
            && this.entityPlayerSP.movementInput != null
            && !(this.entityPlayerSP.movementInput.moveForward <= 0.0F)
            && !this.entityPlayerSP.movementInput.sneak
            && !this.entityPlayerSP.isCollidedHorizontally
            && !this.entityPlayerSP.isPotionActive(Potion.blindness)
            && (this.entityPlayerSP.getFoodStats().getFoodLevel() > 6 || this.entityPlayerSP.capabilities.allowFlying)
            && !this.checkState5()
            && (KeepSprint.instance == null || !KeepSprint.instance.checkState13())) {
            this.entityPlayerSP.setSprinting(true);
        }
    }

    public void onMovementEvent2(MovementEvent2 movementEvent2) {
        this.update();
        if (this.checkState3() && !this.checkState()) {
            ItemStack heldItem = this.entityPlayerSP.getHeldItem();
            if (heldItem != null && this.noSlowDown.matchesItemStack(heldItem)) {
                if (!(heldItem.getItem() instanceof ItemSword)) {
                    if (this.entityPlayerSP.isUsingItem() && this.count > this.noSlowDown.computeInt2() && !this.flag2) {
                        this.noSlowDown.handleMovementEvent2(movementEvent2, heldItem);
                    }
                } else {
                    KillAura instance = KillAura.instance;
                    boolean flag = instance != null && instance.checkState44() && instance.checkState53() && instance.checkState58();
                    if (this.checkState5() || !this.entityPlayerSP.isUsingItem() && !flag) {
                        return;
                    }

                    if (this.count4 != this.entityPlayerSP.ticksExisted) {
                        this.count4 = this.entityPlayerSP.ticksExisted;
                        if (flag) {
                            if (!instance.checkState13()) {
                                return;
                            }
                        } else {
                            NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                            NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(heldItem));
                        }
                    }

                    this.noSlowDown.handleMovementEvent2(movementEvent2, heldItem);
                }
            }
        }
    }

    private boolean checkState3() {
        return this.noSlowDown.checkState9()
            && mc.thePlayer != null
            && mc.theWorld != null
            && mc.thePlayer == this.entityPlayerSP
            && mc.theWorld == this.worldClient
            && this.computeNetworkManager() == this.connection
            && this.checkState4();
    }

    private boolean checkState2() {
        return MovementFix.isFlag6()
            || KillAura.instance != null && KillAura.instance.checkState44() && KillAura.entityLivingBase2 != null
            || KillAura.checkState48()
            || Scaffold.computeFloatArray() != null
            || Autoblockin.active
            || Displace.instance != null && Displace.instance.checkState6()
            || OnMouseHandler.computeFloatArray() != null
            || OnJumpHelper.checkState()
            || ChestAura.instance != null && ChestAura.instance.checkState2()
            || Cake.instance != null && Cake.instance.checkState()
            || DragonEgg.instance != null && DragonEgg.instance.checkState4()
            || CarePackage.instance != null && CarePackage.instance.checkState2();
    }

    public FlightHandler(NoSlowDown noSlowDown) {
        this.noSlowDown = noSlowDown;
    }

    public void onEvent(Event event) {
        if (this.noSlowDown.checkState9() && event instanceof EventPacketReceive && ((EventPacketReceive)event).getPacket() instanceof S08PacketPlayerPosLook) {
            this.timestamp = System.currentTimeMillis();
            this.floatValueArray = null;
            if (mc.thePlayer == this.entityPlayerSP && mc.theWorld == this.worldClient && this.computeNetworkManager() == this.connection) {
                this.sizeHelper.flush();
            } else {
                this.sizeHelper.process2();
            }
        } else if (this.checkState3() && !this.checkState()) {
            if (event instanceof EventPacketReceive event2) {
                if (!event2.cancel2() && this.sizeHelper.matchesPacket(event2.getPacket(), false, System.currentTimeMillis())) {
                    event2.cancel();
                }
            } else if (event instanceof EventPacketSend event3
                && !event3.isCancelled()
                && this.sizeHelper.matchesPacket(event3.getPacket(), true, System.currentTimeMillis())) {
                event3.setCancelled(true);
            }
        } else {
            if (mc.thePlayer == this.entityPlayerSP && mc.theWorld == this.worldClient && this.computeNetworkManager() == this.connection) {
                this.sizeHelper.flush();
            } else {
                this.sizeHelper.process2();
            }
        }
    }

    public float[] computeFloatArray() {
        return this.checkState3() && !this.checkState() && this.count5 == this.entityPlayerSP.ticksExisted && !this.checkState2() ? this.floatValueArray : null;
    }

    private boolean checkState5() {
        KillAura instance = KillAura.instance;
        ItemStack stack = mc.thePlayer == null ? null : mc.thePlayer.getHeldItem();
        return instance != null && instance.checkState44() && (instance.checkState14() || stack != null && stack.getItem() instanceof ItemSword && instance.checkState22());
    }

    private boolean matchesItemStack(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ItemFood || item instanceof ItemBow || item instanceof ItemPotion && !ItemPotion.isSplash(stack.getItemDamage());
    }

    public void onEvent2(Event event) {
        if (event instanceof MovementEvent4) {
            this.update();
        } else if (event instanceof MovementEvent2) {
            this.onMovementEvent2((MovementEvent2)event);
        } else if (event instanceof EventPreMotion event2 && event2.getState() == EventState.PRE) {
            this.update();
            float[] floats = this.computeFloatArray();
            if (floats != null) {
                event2.handleFloat(floats[0], floats[1]);
            }
        }
    }

    private boolean checkState4() {
        if (this.entityPlayerSP != null && this.entityPlayerSP.inventory != null) {
            ItemStack heldItem = this.entityPlayerSP.getHeldItem();
            return heldItem != null
                && this.entityPlayerSP.inventory.currentItem == this.slot
                && heldItem.getItem() == this.item
                && this.noSlowDown.matchesItemStack(heldItem)
                && (!(this.item instanceof ItemPotion) || heldItem.getItemDamage() == this.itemDamage);
        }
        return false;
    }
}
