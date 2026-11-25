package org.brutality.module.impl.movement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.BasicEvent3;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.movement.MovementEvent2;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.movement.flight.FlightHandler;
import org.brutality.module.impl.movement.flight.FlightHandler2;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.modules.CountMovementHelper;
import org.brutality.utils.modules.NoneType;
import org.brutality.utils.network.NetworkHelper;
import org.brutality.utils.player.GetSpeedHelper;

public class NoSlowDown extends Module {
    public final ModeSetting fakeSetting = new ModeSetting(
        "Sword Mode",
        this,
        "Vanilla",
        Arrays.asList("Vanilla", "Watchdog", "Watchdog Sim", "Watchdog2", "Watchdog3", "Watchdog4", "Switch", "NCP", "Polar", "Grim", "Fake")
    );
    private final ModeSetting fakeSetting2 = new ModeSetting(
        "Item Mode",
        this,
        "Vanilla",
        Arrays.asList("Vanilla", "1.9+", "Watchdog Sim", "Watchdog", "Watchdog2", "Watchdog3", "Switch", "NCP", "Polar", "Grim", "Fake")
    );
    private final BooleanSetting swordsSetting = new BooleanSetting("Swords", this, true);
    private final NumberSetting swordForwardSetting = new NumberSetting("Sword Forward", this, 1.0, 0.0, 1.0, 0.01);
    private final NumberSetting swordStrafeSetting = new NumberSetting("Sword Strafe", this, 1.0, 0.0, 1.0, 0.01);
    private final BooleanSetting foodSetting = new BooleanSetting("Food", this, false);
    private final NumberSetting foodForwardSetting = new NumberSetting("Food Forward", this, 1.0, 0.0, 1.0, 0.01);
    private final NumberSetting foodStrafeSetting = new NumberSetting("Food Strafe", this, 1.0, 0.0, 1.0, 0.01);
    private final BooleanSetting bowSetting = new BooleanSetting("Bow", this, true);
    private final NumberSetting bowForwardSetting = new NumberSetting("Bow Forward", this, 1.0, 0.0, 1.0, 0.01);
    private final NumberSetting bowStrafeSetting = new NumberSetting("Bow Strafe", this, 1.0, 0.0, 1.0, 0.01);
    private final BooleanSetting potionsSetting = new BooleanSetting("Potions", this, true);
    private final NumberSetting potionsForwardSetting = new NumberSetting("Potions Forward", this, 1.0, 0.0, 1.0, 0.01);
    private final NumberSetting potionsStrafeSetting = new NumberSetting("Potions Strafe", this, 1.0, 0.0, 1.0, 0.01);
    private final BooleanSetting blockingOnlySetting = new BooleanSetting("blocking only", this, false);
    private final BooleanSetting swordOnlySetting = new BooleanSetting("Sword only", this, false);
    public int count;
    public boolean flag;
    public static NoSlowDown instance;
    private int count2 = 0;
    private int count3 = 0;
    private int count4 = 0;
    private boolean flag2 = false;
    private boolean flag3;
    private int count5 = 0;
    private boolean flag4 = false;
    private int count6 = 0;
    private int count7 = 0;
    private int count8 = 0;
    private boolean flag5 = false;
    private boolean flag6;
    private int count9 = 0;
    private boolean flag7 = false;
    public boolean flag8 = false;
    private boolean flag9 = false;
    private boolean flag10 = false;
    private boolean flag11 = false;
    private boolean flag12 = false;
    private boolean blocking = false;
    private boolean flag13 = false;
    private boolean flag14 = false;
    private boolean flag15 = false;
    private boolean flag16 = false;
    private List<Packet<?>> entries = new ArrayList<>();
    private boolean flag17 = false;
    private boolean flag18 = false;
    private final NumberSetting maxPingSpoofSetting = new NumberSetting("Max Ping Spoof", this, 8.0, 0.0, 30.0, 1.0)
        .computeNumberSetting(() -> this.fakeSetting2.matchesString2("Watchdog Sim"));
    private final NumberSetting finishEatingSetting = new NumberSetting("Finish Eating", this, 30.0, 20.0, 36.0, 1.0)
        .computeNumberSetting(() -> this.fakeSetting2.matchesString2("Watchdog Sim"));
    private final BooleanSetting nonBlinkSprintSetting = new BooleanSetting("Non-Blink Sprint", this, true)
        .computeBooleanSetting(() -> this.fakeSetting.matchesString2("Watchdog Sim") || this.fakeSetting2.matchesString2("Watchdog Sim"));
    private final FlightHandler flightHandler = new FlightHandler(this);
    private final FlightHandler2 flightHandler2 = new FlightHandler2(this);

    public static boolean checkState6() {
        return instance != null && instance.checkState44() && instance.flag6 && instance.fakeSetting2.matchesString2("1.9+");
    }

    public static void process6() {
        if (instance != null && instance.flightHandler != null) {
            instance.flightHandler.update();
        }
    }

    public boolean matchesItemStack(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack.getItem() instanceof ItemSword) {
            return this.fakeSetting.matchesString2("Watchdog Sim") && this.swordsSetting.isEnabled();
        }
        return !this.swordOnlySetting.isEnabled() && this.fakeSetting2.matchesString2("Watchdog Sim")
            ? stack.getItem() instanceof ItemFood && this.foodSetting.isEnabled()
                || stack.getItem() instanceof ItemBow && this.bowSetting.isEnabled()
                || stack.getItem() instanceof ItemPotion && !ItemPotion.isSplash(stack.getItemDamage()) && this.potionsSetting.isEnabled()
            : false;
    }

    public static void process5() {
        if (instance != null && instance.flightHandler != null) {
            instance.flightHandler.process();
        }
    }

    public static boolean checkState11() {
        return instance != null && instance.flightHandler2 != null && instance.checkState4() && instance.flightHandler2.checkState2();
    }

    private void process() {
        int currentItem = mc.thePlayer.inventory.currentItem;
        NetworkHelper.handlePacket(new C09PacketHeldItemChange(currentItem - 1 < 0 ? 8 : currentItem - 1));
        NetworkHelper.handlePacket(new C09PacketHeldItemChange(currentItem));
    }

    private void handleItemStack(ItemStack stack) {
        String str = this.fakeSetting2.getText().toLowerCase();
        switch (str) {
            case "polar":
                this.count6++;
                if (this.count6 >= 20) {
                    mc.thePlayer.stopUsingItem();
                    mc.thePlayer.setItemInUse(stack, stack.getMaxItemUseDuration());
                    this.count6 = 0;
                }
                break;
            case "switch":
                NetworkHelper.handlePacket2(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9));
                NetworkHelper.handlePacket2(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                break;
            case "ncp":
                if (this.count == 1) {
                    NetworkHelper.handlePacket2(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9));
                    NetworkHelper.handlePacket2(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 0, mc.thePlayer.getHeldItem(), 0.0F, 0.0F, 0.0F));
                }
                break;
            case "grim":
                if (this.count8 % 5 == 0) {
                    NetworkHelper.handlePacket(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9));
                    NetworkHelper.handlePacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }

                this.count8++;
                if (this.count8 >= 20) {
                    this.count8 = 0;
                }
                break;
            case "watchdog":
                this.count9++;
                if (this.count9 >= 13) {
                    if (!this.flag7) {
                        this.flag7 = true;
                    }

                    if (!this.flag6) {
                        this.flag17 = true;
                        this.flag6 = true;
                    }
                }

                if (this.count9 > 37) {
                    if (this.flag6) {
                        this.reset();
                        this.flag6 = false;
                    }

                    this.count9 = 0;
                    this.flag7 = false;
                }
            case "fake":
            case "1.9+":
        }
    }

    public boolean checkState9() {
        return this.checkState44()
            // VENDOR-DISABLED: && RuntimeAuthGuard.checkState2()
            && true
            && (this.fakeSetting.matchesString2("Watchdog Sim") || this.fakeSetting2.matchesString2("Watchdog Sim"));
    }

    private boolean checkState8() {
        ItemStack currentEquippedItem = mc.thePlayer.getCurrentEquippedItem();
        return currentEquippedItem != null && currentEquippedItem.getItem() instanceof ItemSword;
    }

    public boolean checkState12() {
        KillAura instance = KillAura.instance;
        return mc.thePlayer != null
            && this.checkState8()
            && instance != null
            && instance.checkState44()
            && (
                instance.checkState58()
                    || instance.checkState14()
                    || instance.checkState53()
                    || instance.verusSetting.matchesString2("Fake") && KillAura.entityLivingBase2 != null
            );
    }

    @Override
    public void onDisable() {
        this.flightHandler.handleFlag(true);
        this.flightHandler2.process();
        if (this.flag18 && mc.thePlayer != null) {
            NetworkHelper.handlePacket(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }

        if (this.flag3 || this.flag6) {
            CountMovementHelper.getInstance().matchesFlag(false, NoneType.NO_SLOW);
        }

        this.process3();
        if (mc.thePlayer != null && this.flag16) {
            mc.thePlayer.stopUsingItem();
        }
    }

    @Generated
    public static NoSlowDown getInstance() {
        return instance;
    }

    private boolean checkState() {
        return mc.objectMouseOver != null
            && mc.objectMouseOver.getBlockPos() != null
            && mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock().isCollidable();
    }

    @Override
    public void process2() {
        this.flightHandler.handleFlag(false);
        this.flightHandler2.reset();
    }

    public static float[] computeFloatArray() {
        return instance != null && instance.flightHandler != null ? instance.flightHandler.computeFloatArray() : null;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (mc.thePlayer != null && mc.theWorld != null) {
                    if (!this.checkState12()
                        || this.matchesItemStack(mc.thePlayer.getHeldItem())
                            && !KillAura.instance.verusSetting.matchesString2("Fake")
                            && !KillAura.instance.checkState14()) {
                        if (this.checkState4()) {
                            this.flightHandler2.onEvent(event);
                            if (event instanceof MovementEvent2 movementEvent2) {
                                this.flightHandler2.onMovementEvent2(movementEvent2);
                            }
                        } else {
                            if (this.flightHandler != null) {
                                if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                                    this.flightHandler.update();
                                }

                                this.flightHandler.onEvent2(event);
                                if (mc.thePlayer != null && this.matchesItemStack(mc.thePlayer.getHeldItem())) {
                                    return;
                                }
                            }

                            if (this.fakeSetting.matchesString2("Fake")
                                && mc.thePlayer != null
                                && KillAura.matchesEntityPlayer(mc.thePlayer, mc.thePlayer.getHeldItem())) {
                                this.flag8 = this.flag9 = this.flag15 = this.flag16 = false;
                            } else {
                                if (event instanceof BasicEvent3 basicEvent3) {
                                    if (this.fakeSetting.getText().equalsIgnoreCase("fake") && this.checkState8() && basicEvent3.count6 == 1) {
                                        EntityLivingBase living = this.computeEntityLivingBase(4.0);
                                        if (basicEvent3.flag) {
                                            if (living == null && !this.checkState()) {
                                                basicEvent3.cancel();
                                                this.flag15 = true;
                                                if (!this.blockingOnlySetting.isEnabled() || this.blockingOnlySetting.isEnabled() && this.flag10) {
                                                    this.flag8 = true;
                                                    this.flag9 = true;
                                                    this.flag10 = true;
                                                    this.flag16 = true;
                                                    mc.thePlayer
                                                        .setItemInUse(
                                                            mc.thePlayer.getCurrentEquippedItem(),
                                                            mc.thePlayer.getCurrentEquippedItem().getMaxItemUseDuration()
                                                        );
                                                }
                                            } else {
                                                this.flag15 = false;
                                                this.flag11 = false;
                                                this.flag10 = true;
                                                this.flag9 = false;
                                            }
                                        } else if (this.flag9) {
                                            this.flag15 = false;
                                            this.flag8 = false;
                                            this.flag9 = false;
                                            this.flag16 = false;
                                            mc.thePlayer.stopUsingItem();
                                        }
                                    }

                                    if (basicEvent3.count6 == 1 && basicEvent3.flag && this.flag14) {
                                        this.flag14 = false;
                                        basicEvent3.cancel();
                                    }
                                } else if (event instanceof EventPacketSend event2) {
                                    if (this.fakeSetting.getText().equalsIgnoreCase("fake") && this.flag9 && this.checkState8()) {
                                        if (event2.getPacket() instanceof C08PacketPlayerBlockPlacement) {
                                            C08PacketPlayerBlockPlacement packet = (C08PacketPlayerBlockPlacement)event2.getPacket();
                                            if (packet.getStack() != null && packet.getStack().getItem() instanceof ItemSword) {
                                                event2.cancel();
                                            }
                                        }

                                        if (event2.getPacket() instanceof C07PacketPlayerDigging) {
                                            C07PacketPlayerDigging packet2 = (C07PacketPlayerDigging)event2.getPacket();
                                            if (packet2.getStatus() == Action.RELEASE_USE_ITEM) {
                                                event2.cancel();
                                            }
                                        }
                                    }

                                    if (this.fakeSetting2.matchesString2("Watchdog")
                                        && this.count9 > 0
                                        && ((EventPacketSend)event).getPacket() instanceof C03PacketPlayer) {
                                        ((C03PacketPlayer)((EventPacketSend)event).getPacket()).y += 1.0E-5;
                                    }

                                    if (this.flag17) {
                                        this.entries.add(((EventPacketSend)event).getPacket());
                                        ((EventPacketSend)event).setCancelled(true);
                                    }
                                } else if (event instanceof EventPacketReceive event3) {
                                    if ((this.fakeSetting.matchesString2("Watchdog3") || this.fakeSetting2.matchesString2("Watchdog3"))
                                        && mc.thePlayer != null
                                        && mc.thePlayer.isUsingItem()
                                        && event3.getPacket() instanceof S30PacketWindowItems) {
                                        event3.cancel();
                                    }
                                } else if (event instanceof EventUpdate event4) {
                                    if (event4.getState() != EventState.PRE) {
                                        return;
                                    }

                                    boolean flag = this.fakeSetting2.matchesString2("1.9+")
                                        && this.foodSetting.isEnabled()
                                        && mc.thePlayer.isUsingItem()
                                        && mc.thePlayer.getHeldItem() != null
                                        && mc.thePlayer.getHeldItem().getItem() instanceof ItemFood;
                                    if (flag && !this.flag6) {
                                        CountMovementHelper instance = CountMovementHelper.getInstance();
                                        if (instance.getNoneType() == NoneType.NONE || instance.getNoneType() == NoneType.NO_SLOW) {
                                            instance.matchesFlag(true, NoneType.NO_SLOW);
                                            this.flag6 = true;
                                        }
                                    } else if (!flag && this.flag6 && this.fakeSetting2.matchesString2("1.9+")) {
                                        CountMovementHelper.getInstance().matchesFlag(false, NoneType.NO_SLOW);
                                        this.flag6 = false;
                                    }

                                    boolean flag2 = this.fakeSetting.matchesString2("Watchdog2")
                                        || this.fakeSetting.matchesString2("Watchdog3")
                                        || this.fakeSetting2.matchesString2("Watchdog2")
                                        || this.fakeSetting2.matchesString2("Watchdog3");
                                    boolean flag3 = mc.thePlayer.isUsingItem()
                                        && mc.thePlayer.getHeldItem() != null
                                        && mc.thePlayer.getHeldItem().getItem() instanceof ItemBow;
                                    if (flag2 && this.flag18 && !flag3) {
                                        NetworkHelper.handlePacket(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                    }

                                    this.flag18 = flag3;
                                    EntityLivingBase living2 = this.computeEntityLivingBase(4.0);
                                    if (this.fakeSetting.getText().equalsIgnoreCase("fake") && this.checkState8()) {
                                        if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
                                            if (this.flag9) {
                                                this.flag8 = false;
                                                this.flag9 = false;
                                                this.flag15 = false;
                                                this.flag16 = false;
                                                mc.thePlayer.stopUsingItem();
                                            }
                                        } else if (living2 == null && !this.checkState()) {
                                            if (!this.flag9 && (!this.blockingOnlySetting.isEnabled() || this.blockingOnlySetting.isEnabled() && this.flag10)) {
                                                this.flag8 = true;
                                                this.flag9 = true;
                                                this.flag15 = true;
                                                this.flag16 = true;
                                                mc.thePlayer
                                                    .setItemInUse(
                                                        mc.thePlayer.getCurrentEquippedItem(), mc.thePlayer.getCurrentEquippedItem().getMaxItemUseDuration()
                                                    );
                                            }
                                        } else {
                                            if (this.flag9) {
                                                this.flag8 = false;
                                                this.flag9 = false;
                                                this.flag15 = false;
                                                this.flag16 = false;
                                                mc.thePlayer.stopUsingItem();
                                            }

                                            this.flag10 = true;
                                        }
                                    }

                                    this.flag12 = false;
                                    if (!mc.thePlayer.isUsingItem() && !this.flag9) {
                                        return;
                                    }

                                    if (this.fakeSetting2.getText().equalsIgnoreCase("pre")) {
                                        if (mc.thePlayer.ticksExisted % 3 == 0) {
                                            NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                        }
                                    } else if (this.fakeSetting2.getText().equalsIgnoreCase("post")) {
                                        this.flag12 = true;
                                    }

                                    if (this.fakeSetting.matchesString2("Watchdog2") && this.checkState8()) {
                                        this.process();
                                    }
                                } else if (event instanceof EventPreMotion event5) {
                                    if (this.fakeSetting2.getText().equalsIgnoreCase("float")
                                        && (mc.thePlayer.isUsingItem() || this.flag9)
                                        && this.checkState2()) {
                                        if (!mc.thePlayer.onGround) {
                                            event5.setY(event5.getY() - 0.001);
                                        } else {
                                            event5.setY(event5.getY() + 0.001);
                                        }
                                    }

                                    ItemStack currentEquippedItem = mc.thePlayer.getCurrentEquippedItem();
                                    if (!mc.thePlayer.isUsingItem() && !this.flag9) {
                                        this.count = 0;
                                        this.process4();
                                    } else {
                                        if (GetSpeedHelper.isMoving()
                                            && !mc.thePlayer.isCollidedHorizontally
                                            && !mc.thePlayer.isSneaking()
                                            && mc.gameSettings.keyBindForward.isKeyDown()) {
                                            mc.thePlayer.setSprinting(true);
                                        }

                                        if (this.checkState8()) {
                                            this.handleItemStack2(currentEquippedItem);
                                        }

                                        if (this.checkState2() && !this.flag9) {
                                            this.handleItemStack(currentEquippedItem);
                                        }

                                        this.count++;
                                    }

                                    this.flag = false;
                                    if (this.flag12 && this.fakeSetting2.getText().equalsIgnoreCase("post")) {
                                        if (mc.thePlayer.ticksExisted % 3 == 0) {
                                            NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                        }

                                        this.flag12 = false;
                                    }

                                    if (event5.getState() == EventState.PRE
                                        && (this.fakeSetting.matchesString2("Watchdog3") || this.fakeSetting2.matchesString2("Watchdog3"))
                                        && GetSpeedHelper.isMoving()
                                        && mc.thePlayer.isUsingItem()) {
                                        this.process();
                                    }
                                } else if (event instanceof MovementEvent2 movementEvent22) {
                                    ItemStack currentEquippedItem2 = mc.thePlayer.getCurrentEquippedItem();
                                    KillAura instance2 = KillAura.instance;
                                    boolean flag4 = currentEquippedItem2 != null
                                        && currentEquippedItem2.getItem() instanceof ItemSword
                                        && instance2 != null
                                        && instance2.checkState44()
                                        && instance2.checkState53();
                                    if (currentEquippedItem2 == null || !mc.thePlayer.isUsingItem() && !this.flag9 && !flag4 || !GetSpeedHelper.isMoving()) {
                                        return;
                                    }

                                    boolean flag5 = true;
                                    if (this.swordsSetting.isEnabled()
                                        && currentEquippedItem2.getItem() instanceof ItemSword
                                        && this.fakeSetting.getText().equalsIgnoreCase("watchdog")
                                        && this.count5 >= 6) {
                                        flag5 = false;
                                    }

                                    if (this.checkState2() && !this.flag9 && this.fakeSetting2.getText().equalsIgnoreCase("watchdog") && this.count9 >= 12) {
                                        flag5 = false;
                                    }

                                    if (!flag5) {
                                        if (this.swordsSetting.isEnabled()
                                            && currentEquippedItem2.getItem() instanceof ItemSword
                                            && (!this.swordOnlySetting.isEnabled() || this.swordOnlySetting.isEnabled() && this.checkState8())
                                            && (!this.blockingOnlySetting.isEnabled() || this.flag10 || this.flag9 || flag4)) {
                                            movementEvent22.setFactor2((float)this.swordForwardSetting.computeDouble());
                                            movementEvent22.setFactor((float)this.swordStrafeSetting.computeDouble());
                                        }

                                        if (this.foodSetting.isEnabled() && currentEquippedItem2.getItem() instanceof ItemFood) {
                                            movementEvent22.setFactor2((float)this.foodForwardSetting.computeDouble());
                                            movementEvent22.setFactor((float)this.foodStrafeSetting.computeDouble());
                                        }

                                        if (this.bowSetting.isEnabled() && currentEquippedItem2.getItem() instanceof ItemBow) {
                                            movementEvent22.setFactor2((float)this.bowForwardSetting.computeDouble());
                                            movementEvent22.setFactor((float)this.bowStrafeSetting.computeDouble());
                                        }

                                        if (this.potionsSetting.isEnabled()
                                            && currentEquippedItem2.getItem() instanceof ItemPotion
                                            && !ItemPotion.isSplash(currentEquippedItem2.getItemDamage())) {
                                            movementEvent22.setFactor2((float)this.potionsForwardSetting.computeDouble());
                                            movementEvent22.setFactor((float)this.potionsStrafeSetting.computeDouble());
                                        }
                                    } else if (!this.fakeSetting.getText().equalsIgnoreCase("watchdog")
                                        && !this.fakeSetting2.getText().equalsIgnoreCase("watchdog")) {
                                        if (this.swordsSetting.isEnabled()
                                            && currentEquippedItem2.getItem() instanceof ItemSword
                                            && (!this.swordOnlySetting.isEnabled() || this.swordOnlySetting.isEnabled() && this.checkState8())
                                            && (!this.blockingOnlySetting.isEnabled() || this.flag10 || this.flag9 || flag4)) {
                                            movementEvent22.setFactor2((float)this.swordForwardSetting.computeDouble());
                                            movementEvent22.setFactor((float)this.swordStrafeSetting.computeDouble());
                                        }

                                        if (this.foodSetting.isEnabled() && currentEquippedItem2.getItem() instanceof ItemFood) {
                                            movementEvent22.setFactor2((float)this.foodForwardSetting.computeDouble());
                                            movementEvent22.setFactor((float)this.foodStrafeSetting.computeDouble());
                                        }

                                        if (this.bowSetting.isEnabled() && currentEquippedItem2.getItem() instanceof ItemBow) {
                                            movementEvent22.setFactor2((float)this.bowForwardSetting.computeDouble());
                                            movementEvent22.setFactor((float)this.bowStrafeSetting.computeDouble());
                                        }

                                        if (this.potionsSetting.isEnabled()
                                            && currentEquippedItem2.getItem() instanceof ItemPotion
                                            && !ItemPotion.isSplash(currentEquippedItem2.getItemDamage())) {
                                            movementEvent22.setFactor2((float)this.potionsForwardSetting.computeDouble());
                                            movementEvent22.setFactor((float)this.potionsStrafeSetting.computeDouble());
                                        }
                                    }

                                    if (this.fakeSetting.matchesString2("Watchdog2") && currentEquippedItem2.getItem() instanceof ItemSword
                                        || this.fakeSetting2.matchesString2("Watchdog2") && this.checkState2()) {
                                        movementEvent22.cancel();
                                    }
                                }
                            }
                        }
                    } else {
                        if (this.flag3) {
                            this.flag3 = false;
                            if (!this.flag6) {
                                CountMovementHelper.getInstance().matchesFlag(false, NoneType.NO_SLOW);
                            }
                        }

                        this.count5 = 0;
                        this.flag4 = false;
                        this.count2 = this.count3 = this.count4 = 0;
                        this.flag2 = false;
                        if (this.flightHandler2 != null) {
                            this.flightHandler2.onEvent(event);
                        }

                        if (event instanceof MovementEvent2 movementEvent23 && this.swordsSetting.isEnabled()) {
                            if (this.fakeSetting.matchesString2("Watchdog4")) {
                                this.flightHandler2.onMovementEvent2(movementEvent23);
                            } else if (!this.fakeSetting.matchesString2("Watchdog Sim") || KillAura.instance.verusSetting.matchesString2("Fake")) {
                                this.handleMovementEvent2(movementEvent23, mc.thePlayer.getHeldItem());
                            }
                        }
                    }
                }
            }
        }
    }

    private void reset() {
        this.flag17 = false;
        synchronized (this.entries) {
            for (Packet packet : this.entries) {
                NetworkHelper.handlePacket2(packet);
            }

            this.entries.clear();
        }
    }

    public static void handlePacket(Packet<?> packet) {
        if (instance != null && instance.flightHandler2 != null) {
            instance.flightHandler2.handlePacket(packet);
        }
    }

    public int computeInt() {
        return (int)this.finishEatingSetting.computeDouble();
    }

    private EntityLivingBase computeEntityLivingBase(double d) {
        return GetCurrentScreenHelper.computeEntityLivingBase((int)d);
    }

    public boolean checkState4() {
        return this.checkState44()
            // VENDOR-DISABLED: && RuntimeAuthGuard.checkState2()
            && true
            && this.swordsSetting.isEnabled()
            && this.fakeSetting.matchesString2("Watchdog4")
            && mc.thePlayer != null
            && mc.thePlayer.getHeldItem() != null
            && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    private boolean checkState2() {
        ItemStack currentEquippedItem = mc.thePlayer.getCurrentEquippedItem();
        return currentEquippedItem == null
            ? false
            : currentEquippedItem.getItem() instanceof ItemFood
                || currentEquippedItem.getItem() instanceof ItemBow
                || currentEquippedItem.getItem() instanceof ItemPotion && !ItemPotion.isSplash(currentEquippedItem.getItemDamage());
    }

    private void handleItemStack2(ItemStack stack) {
        if (this.fakeSetting.getText().equalsIgnoreCase("fake")) {
            EntityLivingBase living = this.computeEntityLivingBase(4.0);
            if (living == null && !this.checkState()) {
                this.flag11 = true;
                if ((!this.blockingOnlySetting.isEnabled() || this.blockingOnlySetting.isEnabled() && this.flag10) && !mc.thePlayer.isUsingItem()) {
                    mc.thePlayer.setItemInUse(stack, stack.getMaxItemUseDuration());
                }
            } else {
                this.flag11 = false;
                this.flag10 = true;
                if (this.flag9) {
                    this.flag8 = false;
                    this.flag9 = false;
                    mc.thePlayer.stopUsingItem();
                }
            }
        } else {
            String str = this.fakeSetting.getText().toLowerCase();
            switch (str) {
                case "polar":
                    this.count2++;
                    if (this.count2 >= 20) {
                        mc.thePlayer.stopUsingItem();
                        mc.thePlayer.setItemInUse(stack, stack.getMaxItemUseDuration());
                        this.count2 = 0;
                    }
                    break;
                case "switch":
                    NetworkHelper.handlePacket2(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9));
                    NetworkHelper.handlePacket2(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    break;
                case "ncp":
                    if (this.count == 1) {
                        NetworkHelper.handlePacket2(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9));
                        NetworkHelper.handlePacket2(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        NetworkHelper.handlePacket2(
                            new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 0, mc.thePlayer.getHeldItem(), 0.0F, 0.0F, 0.0F)
                        );
                    }
                    break;
                case "grim":
                    this.count4++;
                    if (this.count4 >= 10 && this.count4 <= 12) {
                        if (!this.flag2) {
                            NetworkHelper.handlePacket(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                            this.flag2 = true;
                        }
                    } else if (this.count4 >= 13 && this.count4 <= 14) {
                        if (this.flag2) {
                            NetworkHelper.handlePacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                            this.flag2 = false;
                        }
                    } else if (this.count4 >= 20) {
                        this.count4 = 0;
                    }
                    break;
                case "watchdog":
                    this.count5++;
                    if (this.count5 >= 7) {
                        if (!this.flag4) {
                            this.flag4 = true;
                        }

                        if (!this.flag3) {
                            CountMovementHelper.getInstance().matchesFlag(true, NoneType.NO_SLOW);
                            this.flag3 = true;
                        }
                    }

                    if (this.count5 > 44) {
                        if (this.flag3) {
                            CountMovementHelper.getInstance().matchesFlag(false, NoneType.NO_SLOW);
                            this.flag3 = false;
                        }

                        this.count5 = 0;
                        this.flag4 = false;
                    }
            }
        }
    }

    private void process3() {
        this.count2 = 0;
        this.count3 = 0;
        this.count4 = 0;
        this.flag2 = false;
        this.flag3 = false;
        this.count5 = 0;
        this.flag4 = false;
        this.count6 = 0;
        this.count7 = 0;
        this.count8 = 0;
        this.flag5 = false;
        this.flag6 = false;
        this.count9 = 0;
        this.flag7 = false;
        this.flag8 = false;
        this.flag9 = false;
        this.flag10 = false;
        this.flag11 = false;
        this.flag12 = false;
        this.blocking = false;
        this.flag13 = false;
        this.flag14 = false;
        this.flag15 = false;
        this.flag16 = false;
        this.flag18 = false;
    }

    public static void onEvent2(Event event) {
        if (instance != null && instance.flightHandler != null && instance.checkState9()) {
            instance.flightHandler.onEvent(event);
        }
    }

    public void handleMovementEvent2(MovementEvent2 movementEvent2, ItemStack stack) {
        if (stack.getItem() instanceof ItemSword) {
            movementEvent2.setFactor2((float)this.swordForwardSetting.computeDouble());
            movementEvent2.setFactor((float)this.swordStrafeSetting.computeDouble());
        } else if (stack.getItem() instanceof ItemBow) {
            movementEvent2.setFactor2((float)this.bowForwardSetting.computeDouble());
            movementEvent2.setFactor((float)this.bowStrafeSetting.computeDouble());
        } else if (stack.getItem() instanceof ItemPotion) {
            movementEvent2.setFactor2((float)this.potionsForwardSetting.computeDouble());
            movementEvent2.setFactor((float)this.potionsStrafeSetting.computeDouble());
        } else {
            movementEvent2.setFactor2((float)this.foodForwardSetting.computeDouble());
            movementEvent2.setFactor((float)this.foodStrafeSetting.computeDouble());
        }
    }

    public NoSlowDown() {
        super("NoSlowDown", 0, Category.MOVEMENT);
        this.handleSettingArray(
            new Setting[]{
                this.fakeSetting,
                this.fakeSetting2,
                this.swordsSetting,
                this.swordForwardSetting,
                this.swordStrafeSetting,
                this.foodSetting,
                this.foodForwardSetting,
                this.foodStrafeSetting,
                this.bowSetting,
                this.bowForwardSetting,
                this.bowStrafeSetting,
                this.potionsSetting,
                this.potionsForwardSetting,
                this.potionsStrafeSetting,
                this.blockingOnlySetting,
                this.swordOnlySetting,
                this.maxPingSpoofSetting,
                this.finishEatingSetting,
                this.nonBlinkSprintSetting
            }
        );
        instance = this;
        this.fakeSetting.setCallback(() -> {
            this.flightHandler.handleFlag(true);
            this.flightHandler2.process();
            if (this.flag3) {
                this.flag3 = false;
                if (!this.flag6) {
                    CountMovementHelper.getInstance().matchesFlag(false, NoneType.NO_SLOW);
                }
            }

            this.count5 = 0;
            this.flag4 = false;
        });
        this.fakeSetting2.setCallback(() -> {
            this.flightHandler.handleFlag(true);
            if (this.flag6 && CountMovementHelper.getInstance().getNoneType() == NoneType.NO_SLOW) {
                CountMovementHelper.getInstance().matchesFlag(false, NoneType.NO_SLOW);
                this.flag6 = false;
            }

            if (this.flag6) {
                this.flag6 = false;
                if (!this.flag3) {
                    CountMovementHelper.getInstance().matchesFlag(false, NoneType.NO_SLOW);
                }
            }

            this.count9 = 0;
            this.flag7 = false;
        });
    }

    private void process4() {
        if (this.flag3) {
            this.reset();
            this.flag3 = false;
        }

        if (this.flag6) {
            this.reset();
            this.flag6 = false;
        }

        this.count5 = 0;
        this.flag4 = false;
        this.count9 = 0;
        this.flag7 = false;
        if (!this.flag9) {
            this.flag8 = false;
            this.flag10 = false;
            this.flag11 = false;
            this.blocking = false;
        }

        this.flag13 = false;
        this.flag14 = false;
    }

    public int computeInt2() {
        return (int)this.maxPingSpoofSetting.computeDouble();
    }

    public boolean checkState5() {
        return this.nonBlinkSprintSetting.isEnabled();
    }
}
