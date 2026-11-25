package org.brutality.module.impl.combat;

import de.florianmichael.viamcp.fixes.AttackOrder;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import lombok.Generated;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.BasicEvent3;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPostMotion;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.movement.KeepSprint;
import org.brutality.module.impl.movement.NoSlowDown;
import org.brutality.module.impl.movement.OnMouseHandler;
import org.brutality.module.impl.movement.Scaffold;
import org.brutality.module.impl.pit.hypixel.automation.AutoAura;
import org.brutality.module.impl.pit.hypixel.automation.AutoEgg;
import org.brutality.module.impl.pit.hypixel.automation.AutoFeast;
import org.brutality.module.impl.pit.hypixel.automation.AutoGrinder;
import org.brutality.module.impl.pit.hypixel.automation.AutoHunt;
import org.brutality.module.impl.pit.hypixel.automation.AutoPotato;
import org.brutality.module.impl.pit.hypixel.automation.AutoSpire;
import org.brutality.module.impl.pit.hypixel.hud.Cake;
import org.brutality.module.impl.pit.hypixel.hud.Squads;
import org.brutality.module.impl.pit.hypixel.hud.TDM;
import org.brutality.module.impl.pit.hypixel.minorevent.CarePackage;
import org.brutality.module.impl.pit.hypixel.minorevent.DragonEgg;
import org.brutality.module.impl.player.AutoHeal;
import org.brutality.module.impl.player.Autoblockin;
import org.brutality.module.impl.player.BedAura;
import org.brutality.module.impl.player.ChestAura;
import org.brutality.module.impl.player.Friends;
import org.brutality.module.impl.player.KOS;
import org.brutality.module.impl.player.Truces;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.TruceManager;
import org.brutality.utils.WHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.modules.CountMovementHelper;
import org.brutality.utils.network.NetworkHelper;
import org.brutality.utils.performance.WorldQueryCache;
import org.brutality.utils.player.IsKeyDownHelper;
import org.brutality.utils.player.PlayerHelper2;
import org.brutality.utils.player.StartHotbarMovesHelper;
import org.brutality.utils.rotation.GetRotationYawHeadHelper;
import org.brutality.utils.rotation.IsMovingHelper;
import org.brutality.utils.rotation.OnJumpHelper;
import org.brutality.utils.timer.TimerUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class KillAura extends Module {
    private static final int count = mc.gameSettings.keyBindUseItem.getKeyCode();
    private static final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, task -> {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        return thread;
    });
    private boolean flag = false;
    private int count2 = 0;
    private float[] floatValueArray = null;
    private int count3 = 0;
    private boolean flag2 = false;
    private boolean flag3 = false;
    private int count4 = 0;
    private int count5 = -1;
    private boolean flag4 = false;
    private int count6 = 0;
    private boolean flag5 = false;
    private int count7 = 0;
    private boolean flag6 = false;
    private boolean flag7 = false;
    private boolean flag8 = false;
    private boolean flag9 = false;
    private int count8 = 0;
    private int count9 = 0;
    private boolean flag10 = false;
    private float[] floatValueArray2;
    private boolean flag11 = false;
    private boolean flag12 = false;
    private int count10 = 0;
    private boolean flag13 = false;
    private int count11 = 0;
    private boolean flag14 = false;
    private float[] floatValueArray3 = null;
    private float[] floatValueArray4 = null;
    private boolean flag15 = false;
    private int count12 = 0;
    private int count13 = 0;
    private int count14 = 0;
    private boolean flag16 = false;
    private int count15 = 2;
    private int count16 = 0;
    private int count17 = 0;
    private boolean flag17 = false;
    private boolean flag18 = false;
    private boolean flag19 = false;
    private boolean flag20;
    private boolean flag21;
    private boolean flag22;
    private boolean flag23 = false;
    private boolean flag24 = false;
    private EntityLivingBase entityLivingBase = null;
    private long timestamp = 0L;
    private int count18 = 0;
    private int count19 = 0;
    private int count20 = 0;
    private int count21 = 0;
    private int count22 = 0;
    private int count23 = 0;
    private float yaw = 0.0F;
    public float[] floatValueArray5 = null;
    private boolean flag25;
    private EntityPlayerSP entityPlayerSP;
    private WorldClient worldClient;
    private float factor;
    private int count24 = 0;
    public int count25;
    private TimerUtil timerUtil = new TimerUtil();
    private int count26 = -1;
    private boolean flag26;
    private Random random = new Random();
    public boolean blocking;
    public boolean flag27;
    public boolean flag28;
    public boolean flag29;
    public boolean flag30;
    private boolean flag31 = false;
    public int count27 = 0;
    public int count28 = 0;
    public int count29;
    public int count30 = 0;
    public int count31 = 0;
    private boolean flag32;
    private int count32;
    private int count33;
    private long timestamp2;
    public final List<Packet<?>> entries = new ArrayList<>();
    private List<EntityLivingBase> entries2 = new ArrayList<>();
    private long[] longValueArray = new long[20];
    private int count34 = 0;
    public boolean flag33 = false;
    private boolean flag34 = false;
    private boolean flag35 = false;
    private boolean flag36 = false;
    private boolean flag37 = false;
    private int count35 = 0;
    private volatile boolean flag38 = false;
    private boolean flag39 = false;
    private boolean flag40;
    private boolean flag41;
    private boolean flag42;
    private List<Entity> entries3 = new ArrayList<>();
    private int count36 = 0;
    private static final int count37 = 1;
    private static final List<String> entries4 = Arrays.asList(
        "Venom",
        "Misery",
        "Spite",
        "Needless Suffering",
        "Grim Reaper",
        "Nostalgia",
        "Hedge Fund",
        "Heartripper",
        "Sanguisuge",
        "Lycanthropy",
        "Mind Assault",
        "Golden Handcuffs"
    );
    public ModeSetting multipleSetting = new ModeSetting("Type", this, "Single", Arrays.asList("Single", "Switch", "Multiple"));
    private final NumberSetting switchDelaySetting = new NumberSetting("Switch Delay", this, 150.0, 50.0, 1000.0, 50.0);
    private final NumberSetting smoothingSetting = new NumberSetting("Smoothing (%)", this, 50.0, 0.0, 100.0, 1.0);
    private final NumberSetting angleStepSetting = new NumberSetting("Angle-Step (%)", this, 50.0, 30.0, 180.0, 1.0);
    public final NumberSetting targetAmountBothSetting = new NumberSetting("Target Amount (Both)", this, 2.0, 1.0, 10.0, 1.0);
    private final NumberSetting blockRangeSetting = new NumberSetting("Block Range", this, 3.0, 3.0, 10.0, 0.1);
    private final NumberSetting rotationRangeSetting = new NumberSetting("Rotation Range", this, 4.0, 3.0, 10.0, 0.1);
    private final NumberSetting swingRangeSetting = new NumberSetting("Swing Range", this, 3.5, 3.0, 6.0, 0.1);
    public final NumberSetting attackRangeSetting = new NumberSetting("Attack Range", this, 3.0, 3.0, 6.0, 0.1);
    public final NumberSetting horizontalAngleSetting = new NumberSetting("Horizontal Angle", this, 180.0, 1.0, 360.0, 5.0);
    public final NumberSetting verticalAngleSetting = new NumberSetting("Vertical Angle", this, 360.0, 1.0, 360.0, 5.0);
    public final ModeSetting hurttimeSetting = new ModeSetting("Sorting", this, "Distance", Arrays.asList("Distance", "Health", "FOV", "Armor", "Hurttime"));
    private final ModeSetting distanceSetting = new ModeSetting("Hurttime Mode", this, "Health", Arrays.asList("Health", "Distance"))
        .computeModeSetting(() -> this.hurttimeSetting.matchesString2("Hurttime"));
    private final ModeSetting highSetting = new ModeSetting("Health Mode", this, "Low", Arrays.asList("Low", "High"))
        .computeModeSetting(() -> this.hurttimeSetting.matchesString2("Health"));
    public final ModeSetting verusSetting = new ModeSetting("Autoblock", this, "None", Arrays.asList("None", "Vanilla", "Fake", "Watchdog", "Grim", "Verus"));
    private final ModeSetting cpS10Setting = (new ModeSetting("Watchdog Mode", this, "7-CPS", Arrays.asList("7-CPS", "8-CPS", "10-CPS")) {
        @Override
        public boolean matchesString(String str) {
            return "Hypixel Adaptive".equalsIgnoreCase(str) || super.matchesString(str);
        }

        @Override
        public void handleString(String str) {
            super.handleString("Hypixel Adaptive".equalsIgnoreCase(str) ? "8-CPS" : str);
        }
    }).computeModeSetting(() -> this.verusSetting.matchesString2("Watchdog"));
    private final ModeSetting cpS10Setting2 = new ModeSetting("Grim Mode", this, "7-CPS", Arrays.asList("7-CPS", "10-CPS"))
        .computeModeSetting(() -> this.verusSetting.matchesString2("Grim"));
    private final CombatHandler2 combatHandler2 = new CombatHandler2();
    private final CombatHandler combatHandler = new CombatHandler();
    private boolean flag43;
    private boolean flag44;
    private boolean flag45;
    private long timestamp3 = Long.MIN_VALUE;
    private boolean flag46;
    private long timestamp4;
    private long timestamp5;
    private long timestamp6;
    private boolean flag47;
    private boolean flag48;
    private boolean flag49;
    private final ModeSetting lookPacketSetting = new ModeSetting("Rotations", this, "None", Arrays.asList("None", "Silent", "Lock view", "Look packet"));
    private final BooleanSetting hideClientRotationsSetting = new BooleanSetting("Hide Client Rotations", this, false)
        .computeBooleanSetting(() -> this.lookPacketSetting.matchesString2("Silent"));
    private final NumberSetting minCPSSetting = new NumberSetting("Min CPS", this, 10.0, 1.0, 30.0, 1.0);
    private final NumberSetting maxCPSSetting = new NumberSetting("Max CPS", this, 10.0, 1.0, 30.0, 1.0);
    private final ModeSetting dynamicSetting = new ModeSetting("CPS Mode", this, "Normal", Arrays.asList("Normal", "Dynamic"));
    private final BooleanSetting pitSpawnSetting = new BooleanSetting("Pit Spawn", this, true);
    private final BooleanSetting throughWallsSetting = new BooleanSetting("Through Walls", this, true);
    private final ModeSetting hitboxVisibleSetting = new ModeSetting("Through Walls Mode", this, "Normal", Arrays.asList("Normal", "Hitbox Visible"))
        .computeModeSetting(this::z);
    private final BooleanSetting raycastSetting = new BooleanSetting("Raycast", this, false);
    private final BooleanSetting teamsSetting = new BooleanSetting("Teams", this, true);
    private final BooleanSetting allyHitSyncSetting = new BooleanSetting("Ally HitSync", this, false);
    private final NumberSetting allyHitSyncRangeSetting = new NumberSetting("Ally HitSync Range", this, 3.0, 1.0, 6.0, 0.5)
        .computeNumberSetting(() -> this.allyHitSyncSetting.isEnabled());
    private final BooleanSetting weaponsOnlySetting = new BooleanSetting("Weapons Only", this, true);
    public final BooleanSetting manualBlockSetting = new BooleanSetting("Manual Block", this, false);
    private final BooleanSetting requirePressSetting = new BooleanSetting("Require Press", this, false);
    private final BooleanSetting disableWhileMiningSetting = new BooleanSetting("Disable While Mining", this, true);
    private final BooleanSetting disableWhenBlockingSetting = new BooleanSetting("Disable When Blocking", this, true);
    private final BooleanSetting disableInGUISetting = new BooleanSetting("Disable In GUI", this, true);
    private final BooleanSetting autoblockInInventorySetting = new BooleanSetting("Autoblock In Inventory", this, false);
    private final BooleanSetting disableWhilePantsSwappingSetting = new BooleanSetting("Disable While Pants Swapping", this, true);
    private final BooleanSetting attackWhileDelayingSetting = new BooleanSetting("Attack while delaying", this, false);
    public final BooleanSetting onlyWhileSneakingSetting = new BooleanSetting("Only While Sneaking", this, false);
    private final BooleanSetting playersSetting = new BooleanSetting("Players", this, true);
    private final BooleanSetting animalsSetting = new BooleanSetting("Animals", this, false);
    private final BooleanSetting mobsSetting = new BooleanSetting("Mobs", this, true);
    private final BooleanSetting golemSetting = new BooleanSetting("Golem", this, false);
    private final BooleanSetting silverfishSetting = new BooleanSetting("Silverfish", this, false);
    private final BooleanSetting invisibleSetting = new BooleanSetting("Invisible", this, true);
    private final BooleanSetting ignoreInLiquidSetting = new BooleanSetting("Ignore In Liquid", this, true);
    private final BooleanSetting ignoreShopSetting = new BooleanSetting("Ignore Shop", this, false);
    private final BooleanSetting ignoreChainSetting = new BooleanSetting("Ignore Chain", this, false);
    private final BooleanSetting ignoreDarksSetting = new BooleanSetting("Ignore Darks", this, false);
    private final BooleanSetting ignoreSetSetting = new BooleanSetting("Ignore Set", this, false);
    private final KeybindListSetting targetsSetting = new KeybindListSetting(
        "Targets",
        this,
        this.playersSetting,
        this.animalsSetting,
        this.mobsSetting,
        this.golemSetting,
        this.silverfishSetting,
        this.invisibleSetting,
        this.ignoreInLiquidSetting,
        this.ignoreShopSetting,
        this.ignoreChainSetting,
        this.ignoreDarksSetting,
        this.ignoreSetSetting
    );
    private final BooleanSetting debugSetting = new BooleanSetting("Debug", this, false);
    private final BooleanSetting noiseSetting = new BooleanSetting("Noise", this, false);
    public static EntityLivingBase entityLivingBase2;
    public static EntityLivingBase entityLivingBase3;
    public static boolean flag50;
    public List<EntityLivingBase> entries5 = new ArrayList<>();
    public static KillAura instance;
    public static boolean y;
    private int count38 = 0;
    public PlayerHelper2 playerHelper2 = new PlayerHelper2();
    private int count39 = 100;
    private int count40 = 0;
    public float factor2;
    public float factor3;
    public float factor4;
    public float factor5;
    public float factor6;
    public float factor7;
    public float factor8;
    public float factor9;
    public boolean flag51 = false;
    private long timestamp7 = System.nanoTime();
    private boolean flag52;
    private final BooleanSetting prioritiseKillAuraSetting = new BooleanSetting("Prioritise KillAura", this, true);
    private double amount = 0.0;
    private int count41 = 0;

    private boolean checkState41() {
        if (!this.checkState27()) {
            return false;
        }
        if (!this.disableWhenBlockingSetting.isEnabled() || !this.checkState33() && !this.flag35) {
            return this.manualBlockSetting.isEnabled() ? this.flag33 : true;
        }
        return false;
    }

    private void process20() {
        this.flag40 = this.g() && this.x();
        this.flag46 = false;
        this.process41();
        OnJumpHelper.disable();
        OnJumpHelper.process5();
        this.entries3.clear();
        this.count36 = 1;
        entityLivingBase3 = null;
        flag50 = false;
        this.flag13 = false;
        this.flag16 = false;
        this.flag52 = false;
        this.flag39 = this.checkState44() && this.flag38;
    }

    private void process11() {
        this.flag25 = false;
        if (!this.checkState52()) {
            if (this.floatValueArray2 != null) {
                OnJumpHelper.handleFloatArray(this.floatValueArray2, 2);
            } else {
                OnJumpHelper.disable();
            }
        }
    }

    private boolean checkState16() {
        return mc.thePlayer.getHeldItem() == null ? false : mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    private List<EntityLivingBase> computeList2(List<EntityLivingBase> livings) {
        String str = this.hurttimeSetting.getText().toLowerCase();

        var comparator = switch (str) {
            case "distance" -> new Comparator<EntityLivingBase>() {
                public int compare(EntityLivingBase living, EntityLivingBase living2) {
                    return Double.compare(OnJumpHelper.computeFloat(living), OnJumpHelper.computeFloat(living2));
                }
            };
            case "health" -> {
                if (this.highSetting.matchesString2("High")) {
                    yield new Comparator<EntityLivingBase>() {
                        public int compare(EntityLivingBase living3, EntityLivingBase living4) {
                            return Float.compare(living4.getHealth(), living3.getHealth());
                        }
                    };
                } else {
                    yield new Comparator<EntityLivingBase>() {
                        public int compare(EntityLivingBase living5, EntityLivingBase living6) {
                            return Float.compare(living5.getHealth(), living6.getHealth());
                        }
                    };
                }
            }
            case "fov" -> new Comparator<EntityLivingBase>() {
                public int compare(EntityLivingBase living7, EntityLivingBase living8) {
                    float[] floats = IsMovingHelper.computeFloatArray(living7);
                    float[] floats2 = IsMovingHelper.computeFloatArray(living8);
                    float f = Math.abs(MathHelper.wrapAngleTo180_float(floats[0] - KillAura.mc.thePlayer.rotationYaw));
                    float f2 = Math.abs(MathHelper.wrapAngleTo180_float(floats2[0] - KillAura.mc.thePlayer.rotationYaw));
                    return Float.compare(f, f2);
                }
            };
            case "armor" -> new Comparator<EntityLivingBase>() {
                public int compare(EntityLivingBase living9, EntityLivingBase living10) {
                    int n = living9 instanceof EntityPlayer ? KillAura.this.computeInt((EntityPlayer)living9) : 0;
                    int n2 = living10 instanceof EntityPlayer ? KillAura.this.computeInt((EntityPlayer)living10) : 0;
                    return Integer.compare(n2, n);
                }
            };
            case "hurttime" -> {
                if (this.distanceSetting.matchesString2("Health")) {
                    yield new Comparator<EntityLivingBase>() {
                        public int compare(EntityLivingBase living11, EntityLivingBase living12) {
                            boolean flag = living11.hurtTime == 0;
                            boolean flag2 = living12.hurtTime == 0;
                            if (flag && !flag2) {
                                return -1;
                            }
                            if (!flag && flag2) {
                                return 1;
                            }
                            return flag ? Float.compare(living11.getHealth(), living12.getHealth()) : Integer.compare(living11.hurtTime, living12.hurtTime);
                        }
                    };
                } else {
                    yield new Comparator<EntityLivingBase>() {
                        public int compare(EntityLivingBase living13, EntityLivingBase living14) {
                            return Integer.compare(living13.hurtTime, living14.hurtTime);
                        }
                    };
                }
            }
            default -> new Comparator<EntityLivingBase>() {
                public int compare(EntityLivingBase living15, EntityLivingBase living16) {
                    return Double.compare(OnJumpHelper.computeFloat(living15), OnJumpHelper.computeFloat(living16));
                }
            };
        };
        List list = new ArrayList(livings);
        Collections.sort(list, comparator);
        return list;
    }

    private List<EntityLivingBase> computeList(List<EntityLivingBase> livings, int n) {
        if (livings.isEmpty()) {
            return Collections.emptyList();
        }
        var livings2 = this.computeList(livings);
        int n2 = Math.min(n, livings2.size());
        return new ArrayList<>(livings2.subList(0, n2));
    }

    private boolean checkState26() {
        if (!this.checkState27()) {
            return false;
        }
        if (!this.checkState16()) {
            return false;
        }
        if (this.checkState40()) {
            return false;
        }
        if (!this.verusSetting.matchesString2("Watchdog") && !this.verusSetting.matchesString2("Grim")) {
            if (this.checkState46()) {
                return false;
            }

            if (this.checkState34()) {
                return false;
            }
        }

        if (this.count10 > 0) {
            return false;
        }
        return this.flag11 ? false : !this.flag12 || this.flag2;
    }

    private boolean checkState11() {
        if (mc.thePlayer.getHeldItem() == null) {
            return false;
        }
        Item item = mc.thePlayer.getHeldItem().getItem();
        return item instanceof ItemSword || item instanceof ItemAxe || item instanceof ItemSpade;
    }

    private boolean checkState24() {
        return this.checkState47() && this.checkState16();
    }

    private void handleItemStack(ItemStack stack) {
        NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(stack));
        mc.thePlayer.setItemInUse(stack, stack.getMaxItemUseDuration());
        this.flag24 = true;
        this.flag13 = true;
    }

    private boolean checkState40() {
        return GetCurrentScreenHelper.isMining() && !this.matchesFlag2(this.checkState27());
    }

    public boolean checkState() {
        return this.checkState44() && this.checkState43() ? entityLivingBase2 != null : false;
    }

    private boolean matchesEntityPlayer5(EntityPlayer player) {
        return Squads.matchesEntityPlayer(player)
            || TDM.matchesEntityPlayer2(player)
            || AutoGrinder.matchesModule2(this) && TDM.matchesEntityPlayer3(player)
            || this.teamsSetting.isEnabled() && this.matchesEntityPlayer4(player);
    }

    public boolean checkState50() {
        return this.checkState44() && this.checkState43() ? this.flag30 || this.flag42 || this.flag41 || this.count30 > 0 : false;
    }

    public static boolean matchesEntityPlayer(EntityPlayer player, ItemStack stack) {
        KillAura killAura = instance;
        return killAura != null
            && killAura.checkState44()
            && killAura.flag38
            && player == mc.thePlayer
            && stack != null
            && stack.getItem() instanceof ItemSword
            && killAura.checkState37();
    }

    public boolean checkState22() {
        return this.checkState44() && this.checkState38();
    }

    private boolean matchesEntityPlayer6(EntityPlayer player) {
        ItemStack stack = player.inventory.armorInventory[0];
        if (stack != null && stack.getItem() instanceof ItemArmor) {
            ItemArmor item = (ItemArmor)stack.getItem();
            if (item.getArmorMaterial() == ArmorMaterial.LEATHER) {
                return true;
            }
        }

        ItemStack stack2 = player.inventory.armorInventory[2];
        if (stack2 != null && stack2.getItem() instanceof ItemArmor) {
            NBTTagCompound tagCompound = stack2.getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("display", 10)) {
                NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
                if (compoundTag.hasKey("Name", 8)) {
                    String str = compoundTag.getString("Name").toLowerCase();
                    if (str.contains("archangel chestplate")) {
                        return true;
                    }
                }
            }
        }

        ItemStack heldItem = player.getHeldItem();
        if (heldItem != null && (heldItem.getItem() instanceof ItemSword || heldItem.getItem() instanceof ItemBow)) {
            NBTTagCompound tagCompound2 = heldItem.getTagCompound();
            if (tagCompound2 != null && tagCompound2.hasKey("display", 10)) {
                NBTTagCompound compoundTag2 = tagCompound2.getCompoundTag("display");
                if (compoundTag2.hasKey("Name", 8)) {
                    String str2 = compoundTag2.getString("Name").toLowerCase();
                    if (str2.contains("tier iii") || str2.contains("tier 3")) {
                        return true;
                    }
                }

                if (compoundTag2.hasKey("Lore", 9)) {
                    NBTTagList tagList = compoundTag2.getTagList("Lore", 8);

                    for (int i = 0; i < tagList.tagCount(); i++) {
                        String str3 = tagList.getStringTagAt(i).toLowerCase();
                        if (str3.contains("tier iii") || str3.contains("tier 3")) {
                            return true;
                        }
                    }
                }
            }
        }

        for (int j = 0; j < 4; j++) {
            ItemStack stack3 = player.inventory.armorInventory[j];
            if (stack3 != null && stack3.getItem() instanceof ItemArmor) {
                NBTTagCompound tagCompound3 = stack3.getTagCompound();
                if (tagCompound3 != null && tagCompound3.hasKey("display", 10)) {
                    NBTTagCompound compoundTag3 = tagCompound3.getCompoundTag("display");
                    if (compoundTag3.hasKey("Lore", 9)) {
                        NBTTagList tagList2 = compoundTag3.getTagList("Lore", 8);

                        for (int k = 0; k < tagList2.tagCount(); k++) {
                            String stringTagAt = tagList2.getStringTagAt(k);
                            if (stringTagAt.contains("Regularity")) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private void process15() {
        if (GetCurrentScreenHelper.checkState6()) {
            if (mc.gameSettings != null) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            }

            if (mc.thePlayer != null && mc.thePlayer.isUsingItem()) {
                mc.thePlayer.stopUsingItem();
            }

            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            this.flag42 = false;
            this.flag41 = false;
        }
    }

    public static boolean matchesEntityLivingBase6(EntityLivingBase living) {
        KillAura killAura = instance;
        return killAura != null
            && killAura.checkState44()
            && killAura.checkState27()
            && living != null
            && living == mc.thePlayer
            && killAura.checkState16()
            && !killAura.verusSetting.matchesString2("None")
            && (killAura.blocking || killAura.flag23 || killAura.flag24 || killAura.flag33);
    }

    public void handleFlag2(boolean flag) {
        this.flag49 = false;
        if (this.combatHandler != null) {
            this.combatHandler.reset();
        }

        if (this.combatHandler2 != null) {
            this.combatHandler2.reset();
        }

        if (this.flag30 || !this.entries.isEmpty()) {
            this.reset();
            this.flag30 = false;
        }

        if (!this.checkState38() && !this.flag43) {
            if (this.count25 == 2 && (this.flag41 || this.flag42)) {
                this.process24();
                this.blocking = false;
            }

            if (this.flag37) {
                NetworkHelper.handlePacket2(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                this.flag37 = false;
            }

            if (this.verusSetting.matchesString2("Watchdog2") && this.count30 == 0 && this.blocking) {
                NetworkHelper.handlePacket2(
                    new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + (Math.random() > 0.5 ? (Math.random() > 0.5 ? 3 : 4) : 2)) % 9)
                );
                if (this.flag41 || this.flag42) {
                    NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                }

                if (this.debugSetting.isEnabled()) {
                    GetCurrentScreenHelper.handleString3("0");
                }

                this.flag37 = true;
                this.blocking = false;
                this.count30++;
            } else {
                this.reset();
                this.count30 = 0;
            }

            if (this.blocking || this.flag42 || this.flag41 || this.flag44) {
                if (flag && (this.flag41 || this.flag42 || this.flag44 || this.checkState53())) {
                    this.process24();
                    this.flag42 = false;
                    this.flag41 = false;
                }

                this.blocking = false;
            }

            if (this.checkState38() || this.flag43) {
                this.flag23 = false;
            }

            if (this.verusSetting.matchesString2("Swap")) {
                if (this.flag24 && flag) {
                    this.process25();
                }

                this.flag23 = false;
            }

            if (this.verusSetting.matchesString2("Fake")) {
                this.flag23 = false;
            }

            if (this.flag32) {
                this.process8();
                this.flag32 = false;
            }

            this.count27 = 0;
            this.count32 = 0;
            this.count33 = 0;
        } else {
            this.count30 = 0;
            this.flag23 = false;
            this.blocking = this.flag42 = this.flag41 = this.flag44;
            if (flag && this.flag44) {
                if (!this.flag45) {
                    this.timestamp3 = Long.MIN_VALUE;
                }

                this.flag45 = true;
                this.process26();
            }
        }
    }

    private boolean matchesFlag(boolean flag) {
        if (flag
            && mc.thePlayer != null
            && mc.theWorld != null
            && !mc.thePlayer.isDead
            && this.smoothingSetting != null
            && !(this.smoothingSetting.computeDouble() <= 0.0)
            && this.lookPacketSetting != null
            && this.lookPacketSetting.matchesString2("Silent")
            && !this.checkState52()) {
            float[] floats = OnJumpHelper.computeFloatArray(
                (float)this.angleStepSetting.computeDouble(), (float)Math.min(99.0, this.smoothingSetting.computeDouble() + 15.0)
            );
            if (floats == null) {
                return false;
            }
            this.floatValueArray5 = floats;
            this.factor = floats[0];
            this.flag25 = true;
            this.entityPlayerSP = mc.thePlayer;
            this.worldClient = mc.theWorld;
            return true;
        }
        return false;
    }

    private boolean checkState23() {
        return this.floatValueArray2 != null
            || this.flag8
            || this.flag11
            || this.count9 > 0
            || mc.playerController != null && mc.playerController.isHittingBlock
            || this.checkState40();
    }

    private void process31() {
        boolean flag = this.floatValueArray5 != null;
        if (this.floatValueArray2 != null && !this.checkState52()) {
            this.floatValueArray5 = (float[])this.floatValueArray2.clone();
            this.factor = this.floatValueArray5[0];
            OnJumpHelper.handleFloatArray(this.floatValueArray5, 2);
        } else if (!this.checkState27()) {
            this.process40();
        } else if (this.lookPacketSetting.getText().equalsIgnoreCase("None")) {
            boolean flag2 = this.floatValueArray5 != null;
            this.floatValueArray5 = null;
            if (flag2) {
                this.process11();
                OnJumpHelper.process5();
            }
        } else if (this.disableWhenBlockingSetting.isEnabled() && this.checkState33()) {
            boolean flag3 = this.floatValueArray5 != null;
            this.floatValueArray5 = null;
            this.factor = mc.thePlayer.rotationYaw;
            if (flag3) {
                this.process11();
                OnJumpHelper.process5();
            }

            if (this.debugSetting.isEnabled()) {
                System.out.println("[Aura] Rotations disabled due to blocking");
            }
        } else {
            if (entityLivingBase2 != null && OnJumpHelper.matchesEntityLivingBase(entityLivingBase2)) {
                OnJumpHelper.process5();
                if (this.debugSetting.isEnabled()) {
                    System.out.println("[Aura] Target changed, reset tracking");
                }
            }

            if (entityLivingBase2 != null) {
                double d = OnJumpHelper.computeFloat(entityLivingBase2);
                if (d <= this.rotationRangeSetting.computeDouble()) {
                    this.flag25 = false;
                    this.entityPlayerSP = null;
                    this.worldClient = null;
                    float f = (float)this.angleStepSetting.computeDouble();
                    float f2 = (float)this.smoothingSetting.computeDouble();
                    boolean flag4 = Displace.instance != null && Displace.instance.matchesEntityLivingBase(entityLivingBase2);
                    OnJumpHelper.handleEntity(
                        entityLivingBase2,
                        this.noiseSetting.isEnabled(),
                        f,
                        f2,
                        flag4,
                        this.checkState6() || this.raycastSetting.isEnabled() && !this.throughWallsSetting.isEnabled(),
                        this.attackRangeSetting.computeDouble()
                    );
                    if (OnJumpHelper.checkState()) {
                        this.floatValueArray5 = (float[])OnJumpHelper.floatValueArray2.clone();
                        if (Displace.instance != null) {
                            Displace.instance.handleEntityLivingBase(entityLivingBase2, this.floatValueArray5);
                        }

                        this.factor = this.floatValueArray5[0];
                        if (this.debugSetting.isEnabled()) {
                            float[] floats = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
                            float f3 = Math.abs(IsMovingHelper.computeFloat(floats[0], this.floatValueArray5[0]));
                            float f4 = Math.abs(IsMovingHelper.computeFloat(floats[1], this.floatValueArray5[1]));
                            PrintStream out = System.out;
                            String str = String.format("%.2f", this.floatValueArray5[0]);
                            String str2 = String.format("%.2f", f3);
                            String str3 = String.format("%.2f", this.floatValueArray5[1]);
                            String str4 = String.format("%.2f", f4);
                            boolean onGround = mc.thePlayer.onGround;
                            boolean sprinting = mc.thePlayer.isSprinting();
                            String str5 = str4;
                            String str6 = str3;
                            String str7 = str2;
                            String str8 = str;
                            out.println(
                                "[Aura] Rotation: yaw="
                                    + str8
                                    + " (Δ"
                                    + str7
                                    + "), pitch="
                                    + str6
                                    + " (Δ"
                                    + str5
                                    + "), sprinting="
                                    + sprinting
                                    + ", onGround="
                                    + onGround
                            );
                        }

                        if (this.lookPacketSetting.getText().equalsIgnoreCase("Lock view")) {
                            mc.thePlayer.rotationYaw = this.floatValueArray5[0];
                            mc.thePlayer.rotationPitch = this.floatValueArray5[1];
                        }

                        if (this.lookPacketSetting.getText().equalsIgnoreCase("Look packet")
                            && !flag4
                            && (Displace.instance == null || !Displace.instance.checkState6())) {
                            mc.thePlayer
                                .sendQueue
                                .addToSendQueue(
                                    new C03PacketPlayer.C05PacketPlayerLook(this.floatValueArray5[0], this.floatValueArray5[1], mc.thePlayer.onGround)
                                );
                        }
                    }
                } else {
                    if (this.matchesFlag(flag)) {
                        return;
                    }

                    boolean flag5 = this.floatValueArray5 != null;
                    this.floatValueArray5 = null;
                    this.factor = mc.thePlayer.rotationYaw;
                    if (flag5) {
                        this.process11();
                        OnJumpHelper.process5();
                    }

                    if (this.debugSetting.isEnabled()) {
                        PrintStream out2 = System.out;
                        double d2 = this.rotationRangeSetting.computeDouble();
                        out2.println("[Aura] Target beyond rotation range: " + d + " > " + d2);
                    }
                }
            } else {
                if (this.matchesFlag(flag)) {
                    return;
                }

                boolean flag6 = this.floatValueArray5 != null;
                this.floatValueArray5 = null;
                this.factor = mc.thePlayer.rotationYaw;
                if (flag6) {
                    this.process11();
                    OnJumpHelper.process5();
                }

                if (this.debugSetting.isEnabled()) {
                    System.out.println("[Aura] No target, rotations cleared");
                }
            }
        }
    }

    private boolean matchesEntityLivingBase(EntityLivingBase living) {
        if (living == null) {
            return false;
        }
        if (this.lookPacketSetting.matchesString2("None")) {
            return true;
        }
        if (!OnJumpHelper.checkState()) {
            return false;
        }
        if (!this.raycastSetting.isEnabled()) {
            return true;
        }
        float collisionBorderSize = living.getCollisionBorderSize();
        AxisAlignedBB box = living.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
        return OnJumpHelper.computeMovingObjectPosition(
                box, OnJumpHelper.floatValueArray2[0], OnJumpHelper.floatValueArray2[1], this.attackRangeSetting.computeDouble()
            )
            != null;
    }

    private Character computeCharacter(String str) {
        if (str == null) {
            return null;
        }
        int n = str.indexOf(93);
        if (n != -1) {
            for (int i = n + 1; i < str.length() && i <= n + 3; i++) {
                if (str.charAt(i) == 167 && i + 1 < str.length()) {
                    char ch = str.charAt(i + 1);
                    if (ch == 'c' || ch == '9') {
                        return ch;
                    }
                    break;
                }
            }
        }

        return str.length() >= 2 && str.charAt(0) == 167 ? str.charAt(1) : null;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent4 || !this.g() || mc.thePlayer != null && mc.thePlayer.isEntityAlive()) {
                if (event instanceof EventPostMotion) {
                    this.process26();
                } else if (event instanceof RenderEvent4 || !AutoGrinder.matchesModule(this)) {
                    if (!(event instanceof RenderEvent4) && WorldQueryCache.isActive()) {
                        process33();
                    } else {
                        this.flag52 = false;
                        if (this.flag38) {
                            if (event instanceof RenderEvent4) {
                                this.process2();
                            } else if (event instanceof BasicEvent3 basicEvent3) {
                                this.onMouse(basicEvent3);
                            } else if (event instanceof EventPacketSend event2) {
                                this.onEventPacketSend(event2);
                            } else if ((event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE || event instanceof EventPreMotion)
                                && !this.checkState27()) {
                                this.process40();
                                if (event instanceof EventPreMotion && this.floatValueArray2 != null && !this.checkState52()) {
                                    OnJumpHelper.handleFloatArray(this.floatValueArray2, 2);
                                    ((EventPreMotion)event).setYaw(this.floatValueArray2[0]);
                                    ((EventPreMotion)event).setPitch(this.floatValueArray2[1]);
                                }
                            } else {
                                if (event instanceof EventUpdate) {
                                    // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
                                    if (false) {
                                        return;
                                    }

                                    if (((EventUpdate)event).getState() == EventState.POST) {
                                        return;
                                    }

                                    if (this.flag39 && mc.thePlayer != null && mc.theWorld != null) {
                                        this.flag39 = false;
                                        this.flag38 = true;
                                        this.flag36 = true;
                                        this.flag2 = false;
                                        this.floatValueArray5 = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
                                        this.factor = mc.thePlayer.rotationYaw;
                                        OnJumpHelper.floatValueArray = mc.thePlayer.getLastReportedRotations();
                                        OnJumpHelper.process5();
                                    }

                                    if (this.disableInGUISetting.isEnabled() && mc.currentScreen != null && !this.autoblockInInventorySetting.isEnabled()) {
                                        this.process7();
                                        return;
                                    }

                                    if (Scaffold.getInstance().checkState44()) {
                                        this.reset();
                                        return;
                                    }

                                    this.count25++;
                                    if (this.count10 > 0) {
                                        this.count10--;
                                        this.process7();
                                        this.flag2 = false;
                                        return;
                                    }

                                    if (this.count9 > 0) {
                                        this.count9--;
                                    }

                                    if (this.count7 > 0) {
                                        this.count7--;
                                    }

                                    if (this.count11 > 0) {
                                        this.count11--;
                                    }

                                    if (this.flag14) {
                                        if (Mouse.isButtonDown(1)) {
                                            return;
                                        }

                                        this.flag14 = false;
                                    }

                                    this.flag6 = false;
                                    this.flag7 = false;
                                    this.process36();
                                    if (this.matchesFlag2(this.checkState27())) {
                                        mc.playerController.resetBlockRemoving();
                                        this.flag11 = this.flag9 = false;
                                        this.floatValueArray2 = null;
                                        this.count9 = 0;
                                    }

                                    this.process3();
                                    if (this.count10 > 0) {
                                        this.process7();
                                        this.flag2 = false;
                                        return;
                                    }

                                    if (this.attackRangeSetting.computeDouble() > this.swingRangeSetting.computeDouble()) {
                                        this.attackRangeSetting.handleDouble2(this.swingRangeSetting.computeDouble());
                                    }

                                    this.process32();
                                    this.process17();
                                    if (this.checkState46()) {
                                        this.handleFlag2(true);
                                        this.process7();
                                        this.flag2 = false;
                                        return;
                                    }

                                    if (!this.checkState30() || !this.checkState56()) {
                                        this.process7();
                                        this.flag2 = false;
                                        return;
                                    }

                                    this.process36();
                                    this.process6();
                                    this.process31();
                                    if (this.entries5.isEmpty() && entityLivingBase2 == null) {
                                        this.handleFlag(this.flag25);
                                        this.flag2 = false;
                                    } else {
                                        this.flag36 = true;
                                        this.flag2 = true;
                                        if (Displace.instance != null && Displace.instance.checkState44() && Displace.instance.checkState8()) {
                                            this.timestamp2 = System.currentTimeMillis() + this.computeLong();
                                        }

                                        boolean flag = false;
                                        if (entityLivingBase2 != null) {
                                            double d = OnJumpHelper.computeFloat(entityLivingBase2);
                                            flag = d <= this.blockRangeSetting.computeDouble()
                                                && (!AutoHunt.matchesKillAura(this) || AutoHunt.matchesKillAura(this, entityLivingBase2));
                                        }

                                        boolean flag2 = false;
                                        if (this.count7 > 0) {
                                            this.handleFlag2(true);
                                        } else if (flag
                                            && (!this.manualBlockSetting.isEnabled() || this.flag33 || this.flag5 && this.count7 == 0)
                                            && this.checkState16()
                                            && this.checkState35()) {
                                            flag2 = true;
                                            this.process37();
                                            if (this.debugSetting.isEnabled()) {
                                                GetCurrentScreenHelper.handleString3(
                                                    "§a[AB] handleAutoBlock fired | manualBlocking=" + this.flag33 + " mode=" + this.verusSetting.getText()
                                                );
                                            }
                                        } else {
                                            if (this.debugSetting.isEnabled() && this.manualBlockSetting.isEnabled() && flag && this.checkState16()) {
                                                boolean flag3 = this.flag33;
                                                boolean flag4 = this.flag2;
                                                int n = this.count10;
                                                int n2 = this.count7;
                                                boolean flag5 = this.checkState35();
                                                boolean buttonDown = Mouse.isButtonDown(1);
                                                boolean flag6 = flag5;
                                                int n3 = n2;
                                                int n4 = n;
                                                boolean flag7 = flag4;
                                                boolean flag8 = flag3;
                                                GetCurrentScreenHelper.handleString3(
                                                    "§c[AB] SKIPPED | manualBlocking="
                                                        + flag8
                                                        + " killauraActive="
                                                        + flag7
                                                        + " extCD="
                                                        + n4
                                                        + " slotCD="
                                                        + n3
                                                        + " canStart="
                                                        + flag6
                                                        + " RMB="
                                                        + buttonDown
                                                );
                                            }

                                            boolean flag9 = this.flag41 || this.flag42 || this.flag24;
                                            this.handleFlag2(true);
                                            if (!flag9) {
                                                this.flag36 = true;
                                            } else if (this.manualBlockSetting.isEnabled() && !this.flag33) {
                                            }
                                        }

                                        boolean flag10 = this.disableInGUISetting.isEnabled()
                                            && mc.currentScreen != null
                                            && this.autoblockInInventorySetting.isEnabled();
                                        if (!flag10
                                            && (
                                                !this.verusSetting.matchesString2("Watchdog")
                                                        && !this.verusSetting.matchesString2("Grim")
                                                        && !this.verusSetting.matchesString2("Watchdog2")
                                                        && !this.verusSetting.matchesString2("Watchdog3")
                                                        && !this.verusSetting.matchesString2("Legit")
                                                    || !flag2 && !this.flag42 && !this.flag45
                                                    || this.manualBlockSetting.isEnabled() && !this.flag33
                                                    || !this.checkState16()
                                            )
                                            && !this.flag7
                                            && this.flag36
                                            && !this.checkState33()
                                            && !this.flag37
                                            && (!this.disableWhilePantsSwappingSetting.isEnabled() || !StartHotbarMovesHelper.flag)
                                            && this.checkState17()) {
                                            this.process5();
                                        }

                                        if (this.flag33) {
                                            this.count27++;
                                        } else {
                                            this.count27 = 0;
                                        }
                                    }

                                    if (this.verusSetting.getText().equalsIgnoreCase("Fake") || this.verusSetting.getText().equalsIgnoreCase("Swap")) {
                                        this.blocking = this.flag23 && entityLivingBase2 != null && this.checkState41() && !this.checkState33();
                                    }
                                } else if (event instanceof EventPreMotion event3) {
                                    if (this.floatValueArray2 != null && !this.checkState52()) {
                                        OnJumpHelper.handleFloatArray(this.floatValueArray2, 2);
                                        event3.setYaw(this.floatValueArray2[0]);
                                        event3.setPitch(this.floatValueArray2[1]);
                                        return;
                                    }

                                    boolean flag11 = entityLivingBase2 != null || this.entries5 != null && !this.entries5.isEmpty();
                                    boolean flag12 = Scaffold.computeFloatArray() != null;
                                    boolean flag13 = !flag12
                                        && (flag11 || this.flag25)
                                        && OnJumpHelper.checkState()
                                        && !this.lookPacketSetting.getText().equalsIgnoreCase("Lock view");
                                    boolean flag14 = flag13 && this.lookPacketSetting.matchesString2("Silent") && this.hideClientRotationsSetting.isEnabled();
                                    GetRotationYawHeadHelper.handleFlag(
                                        flag13 && !flag14,
                                        flag13 && !flag14 ? OnJumpHelper.floatValueArray2[0] : 0.0F,
                                        flag13 && !flag14 ? OnJumpHelper.floatValueArray2[1] : 0.0F,
                                        flag13 && !flag14 ? OnJumpHelper.floatValueArray2[0] : 0.0F,
                                        1
                                    );
                                    if (flag13) {
                                        if (!flag14) {
                                            mc.thePlayer.rotationYawHead = OnJumpHelper.floatValueArray2[0];
                                            mc.thePlayer.rotationPitchHead = OnJumpHelper.floatValueArray2[1];
                                            mc.thePlayer.renderYawOffset = GetRotationYawHeadHelper.getRenderYawOffset();
                                            mc.thePlayer.prevRenderYawOffset = GetRotationYawHeadHelper.getPrevRenderYawOffset();
                                        }

                                        if (this.lookPacketSetting.getText().equalsIgnoreCase("Silent")) {
                                            event3.setYaw(OnJumpHelper.floatValueArray2[0]);
                                            event3.setPitch(OnJumpHelper.floatValueArray2[1]);
                                            this.floatValueArray3 = new float[]{OnJumpHelper.floatValueArray2[0], OnJumpHelper.floatValueArray2[1]};
                                            if (this.floatValueArray4 != null) {
                                                float f = Math.abs(IsMovingHelper.computeFloat(this.floatValueArray3[0], this.floatValueArray4[0]));
                                                float f2 = Math.abs(IsMovingHelper.computeFloat(this.floatValueArray3[1], this.floatValueArray4[1]));
                                                if (f < 2.0F && f2 < 2.0F) {
                                                    this.count3++;
                                                    this.flag15 = this.count3 >= this.count15;
                                                } else {
                                                    this.count3 = 0;
                                                    this.flag15 = false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private int computeInt(int n) {
        for (int i = 0; i < 9; i++) {
            if (i != n) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof ItemSword) {
                    return i;
                }
            }
        }

        return -1;
    }

    public void handlePacket(Packet<?> packet) {
        if (packet instanceof C08PacketPlayerBlockPlacement packet2
            && packet2.getPlacedBlockDirection() == 255
            && packet2.getStack() != null
            && packet2.getStack().getItem() instanceof ItemSword) {
            this.flag46 = true;
        } else if (packet instanceof C09PacketHeldItemChange || packet instanceof C07PacketPlayerDigging packet3 && packet3.getStatus() == Action.RELEASE_USE_ITEM) {
            this.flag46 = false;
        }

        if (this.checkState38() || this.flag44) {
            if (packet instanceof C03PacketPlayer) {
                this.timestamp4++;
                this.flag47 = this.flag48 = false;
            }

            if (packet instanceof C02PacketUseEntity packet4) {
                if (packet4.getAction() == C02PacketUseEntity.Action.ATTACK) {
                    this.flag47 = true;
                    if (this.checkState15() && this.combatHandler2 != null) {
                        this.combatHandler2.process();
                    }
                } else {
                    this.flag48 = true;
                }
            }

            if (this.checkState44()
                && entityLivingBase2 != null
                && this.checkState38()
                && packet instanceof C08PacketPlayerBlockPlacement packet5
                && packet5.getPlacedBlockDirection() == 255
                && packet5.getStack() != null
                && packet5.getStack().getItem() instanceof ItemSword) {
                this.flag44 = true;
                this.timestamp6 = this.timestamp4;
                this.blocking = this.flag42 = this.flag41 = true;
                this.flag13 = true;
            } else if (packet instanceof C09PacketHeldItemChange || packet instanceof C07PacketPlayerDigging packet6 && packet6.getStatus() == Action.RELEASE_USE_ITEM) {
                this.flag44 = false;
                this.flag45 = false;
                this.blocking = this.flag42 = this.flag41 = false;
                this.timestamp5 = this.timestamp4;
            }
        }
    }

    private void process24() {
        if (this.checkState20()) {
            this.flag41 = false;
            this.flag42 = false;
        } else if (this.flag41 || this.flag42 || this.flag44 || this.checkState53()) {
            if (mc.gameSettings != null) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            }

            if (mc.thePlayer != null && mc.thePlayer.isUsingItem()) {
                mc.thePlayer.stopUsingItem();
            }

            this.flag36 = false;
            this.flag7 = true;
            if (mc.getNetHandler() != null) {
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }

            this.flag41 = false;
            this.flag42 = false;
        }
    }

    private boolean b() {
        return WorldQueryCache.isActive();
    }

    public boolean checkState54() {
        if (Scaffold.getInstance().checkState44()) {
            return false;
        }
        if (entityLivingBase2 != null && this.checkState6() && !this.matchesEntityLivingBase5(entityLivingBase2)) {
            return false;
        }
        if (this.raycastSetting.isEnabled() && !this.throughWallsSetting.isEnabled() && entityLivingBase2 != null) {
            MovingObjectPosition hit = OnJumpHelper.computeMovingObjectPosition(entityLivingBase2);
            if (hit != null) {
                return false;
            }
        }

        return !this.verusSetting.getText().equalsIgnoreCase("Watchdog")
            && !this.verusSetting.getText().equalsIgnoreCase("Grim")
            && !this.verusSetting.getText().equalsIgnoreCase("NCP")
            && (!this.disableWhenBlockingSetting.isEnabled() || !this.checkState33());
    }

    private void process35() {
        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
        this.flag42 = true;
        this.flag41 = true;
        this.flag13 = true;
    }

    private boolean checkState51() {
        return this.verusSetting.matchesString2("Fake") && this.checkState37();
    }

    private void process40() {
        entityLivingBase2 = null;
        this.entries5.clear();
        this.process7();
        this.flag36 = false;
    }

    public static void process33() {
        KillAura instance = getInstance();
        if (instance != null && instance.checkState44()) {
            if (!instance.flag52 && mc.thePlayer != null) {
                instance.flag52 = true;
                EntityLivingBase living = entityLivingBase2;
                List list = new ArrayList<>(instance.entries5);
                long l = instance.timestamp2;
                instance.process7();
                entityLivingBase2 = living;
                instance.entries5.clear();
                instance.entries5.addAll(list);
                instance.timestamp2 = l;
                GetRotationYawHeadHelper.reset();
            }
        }
    }

    private boolean checkState5() {
        return BedAura.getInstance() != null && BedAura.getInstance().checkState44();
    }

    private List<EntityLivingBase> computeList(List<EntityLivingBase> livings, double d) {
        List list = new ArrayList();

        for (EntityLivingBase living : livings) {
            if (OnJumpHelper.computeFloat(living) <= d) {
                list.add(living);
            }
        }

        return list;
    }

    public boolean checkState14() {
        return (this.verusSetting.matchesString2("Watchdog") || this.verusSetting.matchesString2("Grim")) && this.checkState50();
    }

    private Character computeCharacter(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        Character character = this.computeCharacter(player.getDisplayName().getFormattedText());
        if (character != null) {
            return character;
        }
        try {
            NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(player.getUniqueID());
            if (playerInfo != null && playerInfo.getDisplayName() != null) {
                return this.computeCharacter(playerInfo.getDisplayName().getFormattedText());
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private void process7() {
        this.handleFlag(this.matchesFlag(this.floatValueArray5 != null));
    }

    @Override
    public void process2() {
        this.process20();
        if (mc.gameSettings != null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
        }

        if (mc.thePlayer != null && mc.thePlayer.isUsingItem()) {
            mc.thePlayer.stopUsingItem();
        }
    }

    public boolean g() {
        return this.requirePressSetting != null && this.requirePressSetting.isEnabled();
    }

    private boolean checkState17() {
        if (!WorldQueryCache.checkState2()) {
            return false;
        }
        if (KeepSprint.instance != null && KeepSprint.instance.checkState()) {
            return false;
        }
        if (!this.checkState27()) {
            return false;
        }
        if (this.checkState46()) {
            return false;
        }
        if (this.checkState34()) {
            return false;
        }
        if (this.count10 > 0) {
            return false;
        }
        if (this.flag11) {
            return false;
        }
        if (this.count9 > 0) {
            return false;
        }
        if (mc.playerController.isHittingBlock) {
            return false;
        }
        if (this.checkState40()) {
            return false;
        }
        return this.flag12 ? false : !mc.thePlayer.isUsingItem();
    }

    private boolean matchesLong(long l) {
        return !this.prioritiseKillAuraSetting.isEnabled()
            && this.timestamp2 - System.currentTimeMillis() >= l
            && !this.checkState53()
            && !this.checkState50()
            && !this.flag30
            && !this.flag45;
    }

    protected boolean checkState27() {
        if (!this.g()) {
            return true;
        }
        if (mc.thePlayer == null || !mc.thePlayer.isEntityAlive()) {
            return false;
        }
        if (y) {
            return true;
        }
        boolean flag = this.x();
        if (!flag) {
            this.flag40 = false;
        }

        return flag && !this.flag40;
    }

    public boolean checkState36() {
        return this.verusSetting.matchesString2("Grim")
            || this.verusSetting.matchesString2("Watchdog") && (!this.checkState38() || this.checkState15())
            || this.verusSetting.matchesString2("Watchdog2")
            || this.verusSetting.matchesString2("Watchdog3")
            || this.verusSetting.matchesString2("Watchdog8")
            || this.verusSetting.matchesString2("Watchdog10")
            || this.verusSetting.matchesString2("Watchdog12");
    }

    private boolean matchesEntityPlayer3(EntityPlayer player) {
        for (int i = 0; i < 4; i++) {
            ItemStack stack = player.inventory.armorInventory[i];
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                ItemArmor item = (ItemArmor)stack.getItem();
                if (item.getArmorMaterial() == ArmorMaterial.DIAMOND) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean matchesEntityLivingBase2(EntityLivingBase living, boolean flag) {
        if (living == null || matchesEntity(living)) {
            return false;
        }
        if (Autoblockin.active) {
            return false;
        }
        if (KeepSprint.instance != null && KeepSprint.instance.checkState()) {
            return false;
        }
        if (Displace.instance != null && Displace.instance.checkState44() && Displace.instance.checkState2()) {
            return false;
        }
        if (!this.checkState27()) {
            return false;
        }
        if (!this.checkState28() && !this.checkState23() && !this.checkState39()) {
            if (living instanceof EntityPlayer) {
                boolean flag2 = this.matchesEntityPlayer5((EntityPlayer)living);
                TDM.handleEntityPlayer((EntityPlayer)living, "attack-team-check", this.teamsSetting.isEnabled(), flag2);
                if (flag2) {
                    return false;
                }
            }

            if (this.checkState46()) {
                return false;
            }
            if (this.count10 > 0) {
                return false;
            }
            if (this.count9 > 0) {
                return false;
            }
            if (this.flag12) {
                return false;
            }
            if (this.flag11) {
                return false;
            }
            if (mc.playerController.isHittingBlock) {
                return false;
            }
            if (!Velocity.instance.flag2 || this.attackWhileDelayingSetting.isEnabled()) {
                double d = OnJumpHelper.computeFloat(living);
                if (d <= this.attackRangeSetting.computeDouble()) {
                    if (mc.thePlayer.isBlocking() && this.disableWhenBlockingSetting.isEnabled()) {
                        return false;
                    }

                    float[] floats = OnJumpHelper.checkState()
                        ? OnJumpHelper.floatValueArray2
                        : new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
                    boolean flag3 = false;
                    if (this.raycastSetting.isEnabled() && !this.lookPacketSetting.matchesString2("None")) {
                        float collisionBorderSize = living.getCollisionBorderSize();
                        AxisAlignedBB box = living.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                        MovingObjectPosition hit = OnJumpHelper.computeMovingObjectPosition(
                            box, floats[0], floats[1], this.attackRangeSetting.computeDouble()
                        );
                        flag3 = hit != null;
                    } else {
                        flag3 = true;
                    }

                    if (flag3 && (!this.checkState6() || this.matchesEntityLivingBase5(living))) {
                        if (KeepSprint.instance != null && !KeepSprint.instance.matchesEntity(living)) {
                            return false;
                        }

                        if (Displace.instance != null && Displace.instance.checkState44() && !Displace.instance.matchesEntityLivingBase2(living)) {
                            return false;
                        }

                        if (flag && this.raycastSetting.isEnabled() && (!this.throughWallsSetting.isEnabled() || this.checkState6())) {
                            MovingObjectPosition hit2 = OnJumpHelper.rayTrace(this.attackRangeSetting.computeDouble(), 1.0F);
                            if (hit2 != null
                                && hit2.typeOfHit == MovingObjectType.ENTITY
                                && hit2.entityHit == living
                                && (!this.checkState6() || this.matchesEntityLivingBase5(living))) {
                                Vec3 hitVec = hit2.hitVec;
                                hitVec = new Vec3(hitVec.xCoord - living.posX, hitVec.yCoord - living.posY, hitVec.zCoord - living.posZ);
                                mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(living, hitVec));
                            }
                        }

                        AttackOrder.sendFixedAttack(mc.thePlayer, living);
                        return true;
                    }

                    mc.thePlayer.swingItem();
                    return false;
                }

                if (d <= this.swingRangeSetting.computeDouble() && (!mc.thePlayer.isBlocking() || !this.disableWhenBlockingSetting.isEnabled())) {
                    boolean flag4 = false;
                    if (this.raycastSetting.isEnabled() && !this.lookPacketSetting.matchesString2("None")) {
                        float[] floats2 = OnJumpHelper.checkState()
                            ? OnJumpHelper.floatValueArray2
                            : new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
                        float collisionBorderSize2 = living.getCollisionBorderSize();
                        AxisAlignedBB box2 = living.getEntityBoundingBox().expand(collisionBorderSize2, collisionBorderSize2, collisionBorderSize2);
                        MovingObjectPosition hit3 = OnJumpHelper.computeMovingObjectPosition(box2, floats2[0], floats2[1], this.swingRangeSetting.computeDouble());
                        flag4 = hit3 != null;
                    } else {
                        flag4 = true;
                    }

                    if (flag4) {
                        mc.thePlayer.swingItem();
                    }
                }
            }

            return false;
        }
        return false;
    }

    private boolean checkState34() {
        if (mc.thePlayer == null) {
            return false;
        }
        int ticksExisted = mc.thePlayer.ticksExisted;
        if (this.matchesObject(AutoEgg.instance, ticksExisted)) {
            return true;
        }
        if (this.matchesObject(AutoFeast.instance, ticksExisted)) {
            return true;
        }
        if (this.matchesObject(AutoAura.instance, ticksExisted)) {
            return true;
        }
        return this.matchesObject(AutoPotato.instance, ticksExisted) ? true : this.matchesObject(AutoSpire.instance, ticksExisted);
    }

    private void handleDouble(double d, boolean flag, boolean flag2, boolean flag3) {
        if (!this.checkState5() || this.checkState57()) {
            if (AntiFireball.getInstance() != null
                && AntiFireball.getInstance().checkState44()
                && AntiFireball.getInstance().getFireball2() != null
                && AntiFireball.getInstance().isAttacking()) {
                AttackOrder.sendFixedAttack(mc.thePlayer, AntiFireball.getInstance().getFireball2());
                if (flag2) {
                    mc.thePlayer
                        .sendQueue
                        .addToSendQueue(new C02PacketUseEntity(AntiFireball.getInstance().getFireball2(), C02PacketUseEntity.Action.INTERACT));
                }
            } else {
                this.process5();
                this.handleFlag(flag, flag2, false, false);
            }
        }
    }

    @Override
    public void onDisable() {
        this.flag46 = false;
        if (this.combatHandler != null) {
            this.combatHandler.reset();
        }

        if (this.combatHandler2 != null) {
            this.combatHandler2.reset();
        }

        this.flag43 = false;
        if (!this.checkState7()) {
            boolean flag = this.matchesFlag(this.floatValueArray5 != null);
            float[] floats = flag ? (float[])this.floatValueArray5.clone() : null;
            this.flag38 = false;
            this.flag39 = false;
            this.flag30 = false;
            if (!this.entries.isEmpty()) {
                ArrayList list;
                synchronized (this.entries) {
                    list = new ArrayList<>(this.entries);
                    this.entries.clear();
                }

                for (Packet packet : ((Iterable<Packet>)(Object)(list))) {
                    mc.thePlayer.sendQueue.addToSendQueue(packet);
                }
            }

            this.process24();
            if (this.flag24) {
                this.process25();
            }

            this.count11 = 5;
            if (this.debugSetting.isEnabled() && this.flag13) {
                GetCurrentScreenHelper.handleString3("§c[KA] onDisable: unblocked, cooldown=5");
            }

            this.process41();
            if (flag) {
                this.floatValueArray5 = floats;
                this.factor = floats[0];
                this.flag25 = true;
                this.entityPlayerSP = mc.thePlayer;
                this.worldClient = mc.theWorld;
                OnJumpHelper.handleFloatArray(floats, 2);
                OnJumpHelper.floatValue = floats[0];
                OnJumpHelper.floatValue2 = floats[1];
            }

            this.entries3.clear();
            this.count36 = 0;
            super.onDisable();
        }
    }

    public static boolean matchesEntityLivingBase(EntityLivingBase living, double d) {
        return OnJumpHelper.matchesEntity(living, d, IsMovingHelper.computeFloatArray(living));
    }

    private boolean matchesObject(Object obj, int n) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof AutoEgg autoEgg) {
            return this.matchesFlag(autoEgg.checkState44(), n, autoEgg.getCount2());
        }
        if (obj instanceof AutoFeast autoFeast) {
            return this.matchesFlag(autoFeast.checkState44(), n, autoFeast.getCount2());
        }
        if (obj instanceof AutoAura autoAura) {
            return this.matchesFlag(autoAura.checkState44(), n, autoAura.getCount2());
        }
        if (obj instanceof AutoPotato autoPotato) {
            return this.matchesFlag(autoPotato.checkState44(), n, autoPotato.getCount2());
        }
        return obj instanceof AutoSpire autoSpire ? this.matchesFlag(autoSpire.checkState44(), n, autoSpire.getCount2()) : false;
    }

    private void process3() {
        boolean flag = Mouse.isButtonDown(0) && this.checkState40();
        boolean buttonDown = Mouse.isButtonDown(1);
        boolean flag2 = !this.computeList(this.computeDouble()).isEmpty();
        int currentItem = mc.thePlayer.inventory.currentItem;
        if (this.count5 != -1 && currentItem != this.count5) {
            this.flag4 = true;
            this.count6 = 6;
            boolean flag3 = !this.verusSetting.matchesString2("None");
            if (flag3 && (this.flag33 || this.flag24 || this.flag42 || this.flag41)) {
                this.flag5 = true;
                if ((this.flag24 || this.flag42 || this.flag41) && !this.checkState20()) {
                    if (mc.gameSettings != null) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                    }

                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    this.flag6 = true;
                    mc.thePlayer.stopUsingItem();
                    this.flag24 = false;
                    this.flag42 = false;
                    this.flag41 = false;
                    this.blocking = false;
                    this.flag23 = false;
                    this.flag33 = false;
                }

                this.count7 = 4;
            }
        }

        this.count5 = currentItem;
        if (this.count6 > 0) {
            this.count6--;
            if (this.count6 == 0) {
                this.flag4 = false;
                this.flag5 = false;
            }
        }

        if (!this.manualBlockSetting.isEnabled()) {
            if (!this.flag12 && buttonDown && !this.flag2 && !flag2) {
                this.flag12 = true;
                this.count10 = 5;
            }

            if (this.flag12 && !buttonDown) {
                this.flag12 = false;
                this.count10 = 5;
            }
        } else {
            this.flag12 = false;
        }

        boolean isHittingBlock = mc.playerController.isHittingBlock;
        if (this.flag9 && !isHittingBlock) {
            this.count9 = 0;
        }

        this.flag9 = isHittingBlock;
        if (!this.flag11 && flag) {
            this.flag11 = true;
            this.count10 = 0;
        }

        if (this.flag11 && !flag) {
            this.flag11 = false;
            mc.playerController.resetBlockRemoving();
            this.count10 = 0;
            this.count9 = 0;
        }
    }

    private void process5() {
        if (!this.checkState46()) {
            if (this.count38 > 0) {
                this.count38--;
            } else if (!this.disableWhenBlockingSetting.isEnabled() || !this.checkState33()) {
                if (this.count10 <= 0) {
                    if (this.count9 <= 0) {
                        if (!this.flag12) {
                            if (!this.flag11) {
                                this.process42();
                                if (!this.entries5.isEmpty()) {
                                    for (EntityLivingBase living : this.entries5) {
                                        if (living != null && !living.isDead) {
                                            this.matchesEntityLivingBase(living, false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void handleEntityPlayerSP(EntityPlayerSP player) {
        KillAura killAura = instance;
        if (killAura != null && player == mc.thePlayer && killAura.checkState44() && killAura.g()) {
            killAura.process20();
        }
    }

    private boolean checkState43() {
        String str = this.verusSetting.getText().toLowerCase();
        return !str.equals("none") && !str.equals("fake");
    }

    private boolean checkState47() {
        return AutoHunt.matchesKillAura(this) ? AutoHunt.matchesKillAura(this, entityLivingBase2) : Mouse.isCreated() && Mouse.isButtonDown(1);
    }

    public boolean checkState55() {
        return this.matchesLong(125L);
    }

    private void process17() {
        boolean flag = this.checkState33();
        this.flag35 = this.disableWhenBlockingSetting.isEnabled() && flag;
        this.count14 = flag ? 0 : this.count14 + 1;
        this.flag34 = flag;
    }

    public List<EntityLivingBase> computeList() {
        if (!this.multipleSetting.matchesString2("Switch")) {
            return Collections.emptyList();
        }
        var livings = this.computeList(this.computeDouble());
        if (KOS.getInstance().checkState44()) {
            List list = new ArrayList();

            for (EntityLivingBase living : livings) {
                if (living instanceof EntityPlayer && WHelper.matchesString(living.getName())) {
                    list.add(living);
                }
            }

            if (!list.isEmpty()) {
                livings = list;
            }
        }

        var livings2 = this.computeList((List<EntityLivingBase>)livings);
        int n = Math.min((int)this.targetAmountBothSetting.computeDouble(), livings2.size());
        return (List<EntityLivingBase>)(n <= 0 ? Collections.emptyList() : new ArrayList<>(livings2.subList(0, n)));
    }

    private boolean checkState6() {
        return this.throughWallsSetting.isEnabled() && this.hitboxVisibleSetting.matchesString2("Hitbox Visible");
    }

    public void process12() {
        this.handleFlag2(true);
        this.process7();
        entityLivingBase2 = null;
        this.entries5.clear();
        this.process11();
    }

    public void process14() {
        if (this.checkState58() && !this.checkState53()) {
            if (this.checkState38()) {
                this.process21();
            } else {
                this.process35();
            }
        }
    }

    private boolean checkState15() {
        return this.verusSetting != null
            && this.verusSetting.matchesString2("Watchdog")
            && this.cpS10Setting != null
            && this.cpS10Setting.matchesString2("8-CPS");
    }

    private void process25() {
        if (this.flag24) {
            if (this.checkState20()) {
                this.flag24 = false;
            } else {
                if (mc.gameSettings != null) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                }

                NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                mc.thePlayer.stopUsingItem();
                this.flag24 = false;
            }
        }
    }

    public void reset() {
        this.flag30 = false;
        ArrayList list;
        synchronized (this.entries) {
            list = new ArrayList<>(this.entries);
            this.entries.clear();
        }

        for (Packet packet : ((Iterable<Packet>)(Object)(list))) {
            mc.thePlayer.sendQueue.addToSendQueue(packet);
        }
    }

    private int computeInt2(int n) {
        for (int i = 0; i < 9; i++) {
            if (i != n) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack == null) {
                    return i;
                }
            }
        }

        for (int j = 0; j < 9; j++) {
            if (j != n) {
                ItemStack stack2 = mc.thePlayer.inventory.getStackInSlot(j);
                if (stack2 != null && !stack2.hasDisplayName()) {
                    return j;
                }
            }
        }

        return Math.floorMod(n - 1, 9);
    }

    public static boolean matchesEntityLivingBase2(EntityLivingBase living, double d) {
        double d2 = living.posX - mc.thePlayer.posX;
        double d3 = living.posZ - mc.thePlayer.posZ;
        double d4 = living.posY + living.height * 0.5 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double d5 = -Math.toDegrees(Math.atan2(d4, Math.sqrt(d2 * d2 + d3 * d3)));
        return Math.abs(d5 - mc.thePlayer.rotationPitch) <= d / 2.0;
    }

    private boolean r() {
        return (this.verusSetting.matchesString2("Fake") || this.verusSetting.matchesString2("None"))
            && this.checkState16()
            && (this.flag20 || this.flag21 || this.flag22);
    }

    @Override
    public boolean checkState7() {
        return AutoGrinder.matchesModule2(this);
    }

    private boolean matchesEntityLivingBase2(EntityLivingBase living) {
        if (living == null) {
            return false;
        }
        float collisionBorderSize = living.getCollisionBorderSize();
        AxisAlignedBB box = living.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
        if (this.lookPacketSetting.matchesString2("None")) {
            Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
            Vec3 look = mc.thePlayer.getLook(1.0F);
            double d = this.attackRangeSetting.computeDouble();
            Vec3 vec = positionEyes.addVector(look.xCoord * d, look.yCoord * d, look.zCoord * d);
            return box.calculateIntercept(positionEyes, vec) != null;
        }
        return !OnJumpHelper.checkState()
            ? false
            : OnJumpHelper.computeMovingObjectPosition(
                    box, OnJumpHelper.floatValueArray2[0], OnJumpHelper.floatValueArray2[1], this.attackRangeSetting.computeDouble()
                )
                != null;
    }

    private boolean checkState18() {
        if (this.entries5.isEmpty()) {
            return false;
        }
        for (EntityLivingBase living : this.entries5) {
            if (OnJumpHelper.computeFloat(living) <= this.blockRangeSetting.computeDouble()) {
                return true;
            }
        }

        return false;
    }

    private List<EntityLivingBase> computeList(double d, int n) {
        List<EntityLivingBase> livings = this.computeList(d);
        if (livings.isEmpty()) {
            this.entityLivingBase = null;
            this.count24 = 0;
            this.count20 = 0;
            return Collections.emptyList();
        }
        List<EntityLivingBase> livings2 = new ArrayList<>();
        if (KOS.getInstance().checkState44()) {
            for (EntityLivingBase living : livings) {
                if (living instanceof EntityPlayer && WHelper.matchesString(living.getName())) {
                    livings2.add(living);
                }
            }

            if (livings2.isEmpty()) {
                livings2 = livings;
            }
        } else {
            livings2 = livings;
        }

        var livings3 = this.computeList2((List<EntityLivingBase>)livings2);
        if (livings3.size() > n) {
            livings3 = livings3.subList(0, n);
        }

        int n2 = Math.max(1, (int)(this.switchDelaySetting.computeDouble() / 50.0));
        boolean flag = this.count20 >= n2;
        boolean flag2 = this.entityLivingBase != null && !this.entityLivingBase.isDead && livings3.contains(this.entityLivingBase);
        if (flag2 && !flag) {
            List list = new ArrayList();
            list.add(this.entityLivingBase);
            return list;
        }
        if (!flag2 || flag) {
            this.count20 = 0;
            if (!livings3.isEmpty()) {
                if (this.count24 >= livings3.size()) {
                    this.count24 = 0;
                }

                this.entityLivingBase = (EntityLivingBase)livings3.get(this.count24);
                this.count24 = (this.count24 + 1) % livings3.size();
            } else {
                this.entityLivingBase = null;
                this.count24 = 0;
            }
        }

        List list2 = new ArrayList();
        if (this.entityLivingBase != null && livings3.contains(this.entityLivingBase)) {
            list2.add(this.entityLivingBase);
        }

        return list2;
    }

    private boolean checkState28() {
        return this.disableWhenBlockingSetting != null && this.disableWhenBlockingSetting.isEnabled() && this.checkState33();
    }

    private boolean checkState37() {
        return this.checkState27()
            && (this.verusSetting.matchesString2("Fake") || this.verusSetting.matchesString2("None"))
            && this.checkState16()
            && !this.checkState28()
            && (entityLivingBase2 != null || this.flag2 || this.entries5 != null && !this.entries5.isEmpty());
    }

    private void process23() {
        if (this.flag45) {
            this.process26();
        } else if (entityLivingBase2 == null) {
            this.handleFlag2(true);
        } else if ((!this.manualBlockSetting.isEnabled() || this.flag33 || this.flag5)
            && this.checkState16()
            && !this.flag11
            && !this.checkState40()
            && (LagRange.instance == null || !LagRange.instance.flag)
            && !CountMovementHelper.getInstance().isFlag()) {
            if (this.flag30 || !this.entries.isEmpty()) {
                this.handleFlag2(true);
            } else if (this.count30 == 0 && !this.flag44) {
                this.flag23 = true;
                this.process21();
                this.count30 = 2;
            } else if (this.flag49 && !this.flag47 && !this.flag44) {
                this.process21();
                this.combatHandler.reset();
                this.count30 = 2;
            } else if (this.checkState15()) {
                this.process13();
            } else {
                CombatHandler.NoneMode noneMode = this.combatHandler.computeNoneMode(mc.thePlayer.ticksExisted, this.flag44);
                if (noneMode != CombatHandler.NoneMode.NONE) {
                    if (noneMode == CombatHandler.NoneMode.RELEASE) {
                        this.flag23 = true;
                        if (!this.flag44 || this.timestamp4 > this.timestamp6) {
                            if (this.flag44) {
                                NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                            }

                            this.blocking = this.flag42 = this.flag41 = this.flag44;
                            this.count30 = 1;
                        }
                    } else {
                        this.flag23 = true;
                        if (!this.flag44 && this.timestamp4 > this.timestamp5) {
                            if (entityLivingBase2 != null
                                && this.matchesEntityLivingBase(entityLivingBase2)
                                && OnJumpHelper.computeFloat(entityLivingBase2) <= this.swingRangeSetting.computeDouble()) {
                                this.matchesEntityLivingBase2(entityLivingBase2, false);
                            }

                            if (this.checkState41()) {
                                this.process21();
                            }

                            this.count30 = 2;
                        } else {
                            this.combatHandler.reset();
                        }
                    }
                }
            }
        } else {
            this.handleFlag2(true);
        }
    }

    private void process36() {
        this.count36++;
        if (this.count36 >= 1) {
            this.count36 = 0;
            if (mc.theWorld != null && mc.thePlayer != null) {
                try {
                    this.entries3 = new ArrayList<>(mc.theWorld.loadedEntityList);
                } catch (Exception e) {
                    this.entries3 = new ArrayList<>();
                }
            } else {
                this.entries3 = new ArrayList<>();
            }
        }
    }

    public EntityLivingBase computeEntityLivingBase() {
        if (this.checkState44() && this.flag38 && this.checkState30() && this.checkState56()) {
            EntityLivingBase living = entityLivingBase2;
            if (living == null && !this.entries5.isEmpty()) {
                living = this.entries5.get(0);
            }

            return living != null && living.isEntityAlive() && !living.isDead && OnJumpHelper.computeFloat(living) <= this.computeDouble() ? living : null;
        }
        return null;
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        ItemStack stack = player.inventory.armorInventory[2];
        ItemStack stack2 = player.inventory.armorInventory[0];
        boolean flag = false;
        boolean flag2 = false;
        if (stack != null && stack.getItem() instanceof ItemArmor) {
            ItemArmor item = (ItemArmor)stack.getItem();
            if (item.getArmorMaterial() == ArmorMaterial.DIAMOND) {
                flag = true;
            }
        }

        if (stack2 != null && stack2.getItem() instanceof ItemArmor) {
            ItemArmor item2 = (ItemArmor)stack2.getItem();
            if (item2.getArmorMaterial() == ArmorMaterial.DIAMOND) {
                flag2 = true;
            }
        }

        return flag || flag2;
    }

    private void process41() {
        this.flag45 = false;
        this.timestamp3 = Long.MIN_VALUE;
        this.flag25 = false;
        this.entityPlayerSP = null;
        this.worldClient = null;
        if (this.combatHandler != null) {
            this.combatHandler.reset();
        }

        if (this.combatHandler2 != null) {
            this.combatHandler2.reset();
        }

        this.flag44 = false;
        this.timestamp4 = this.timestamp5 = 0L;
        this.flag47 = this.flag48 = this.flag49 = false;
        this.floatValueArray2 = null;
        this.flag20 = this.flag21 = this.flag22 = false;
        entityLivingBase2 = null;
        this.entityLivingBase = null;
        if (this.entries5 != null) {
            this.entries5.clear();
        }

        this.floatValueArray5 = null;
        this.factor = 0.0F;
        OnJumpHelper.process5();
        this.flag32 = false;
        this.count32 = 0;
        this.count33 = 0;
        this.count31 = 0;
        this.count24 = 0;
        this.count18 = 0;
        this.count19 = 0;
        this.count27 = 0;
        this.count25 = 0;
        this.flag36 = false;
        this.count30 = 0;
        this.flag41 = false;
        this.flag42 = false;
        this.blocking = false;
        this.flag30 = false;
        this.flag33 = false;
        this.timestamp2 = 0L;
        this.flag29 = false;
        this.flag27 = false;
        this.flag28 = false;
        this.flag34 = false;
        this.flag35 = false;
        this.count14 = 0;
        this.count35 = 0;
        this.count38 = 0;
        this.flag24 = false;
        this.flag23 = false;
        synchronized (this.entries) {
            this.entries.clear();
        }

        this.flag11 = false;
        this.flag12 = false;
        this.count10 = 0;
        this.count9 = 0;
        this.flag8 = false;
        this.count7 = 0;
        this.flag5 = false;
        this.flag4 = false;
        this.count6 = 0;
        this.flag6 = false;
        this.flag7 = false;
        this.flag2 = false;
        this.count11 = 0;
        this.flag14 = false;
        this.flag3 = false;
        this.count4 = 0;
        entityLivingBase3 = null;
        flag50 = false;
    }

    public void handleFloat(float f) {
        EntityLivingBase living = entityLivingBase2;
        if (living == null && this.entries5 != null && !this.entries5.isEmpty()) {
            living = this.entries5.get(0);
        }

        if (living != null) {
            Entity renderViewEntity = mc.getRenderViewEntity();
            if (renderViewEntity != null) {
                Vec3 positionEyes = renderViewEntity.getPositionEyes(f);
                Vec3 look = renderViewEntity.getLook(f);
                double d = this.attackRangeSetting.computeDouble();
                Vec3 vec = positionEyes.addVector(look.xCoord * d, look.yCoord * d, look.zCoord * d);
                float collisionBorderSize = living.getCollisionBorderSize();
                AxisAlignedBB box = living.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                MovingObjectPosition hit = box.calculateIntercept(positionEyes, vec);
                boolean vecInside = box.isVecInside(positionEyes);
                if (vecInside || hit != null) {
                    Vec3 vec2 = vecInside ? (hit == null ? positionEyes : hit.hitVec) : hit.hitVec;
                    mc.objectMouseOver = new MovingObjectPosition(living, vec2);
                    mc.pointedEntity = living;
                }
            }
        }
    }

    public MovingObjectPosition computeMovingObjectPosition() {
        if (entityLivingBase2 != null && !entityLivingBase2.isDead && mc.theWorld != null) {
            float[] floats = OnJumpHelper.checkState() ? OnJumpHelper.floatValueArray2 : new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
            AxisAlignedBB entityBoundingBox = entityLivingBase2.getEntityBoundingBox();
            MovingObjectPosition hit = OnJumpHelper.computeMovingObjectPosition(entityBoundingBox, floats[0], floats[1], this.attackRangeSetting.computeDouble());
            if (hit == null) {
                return null;
            }
            Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
            MovingObjectPosition hit2 = mc.theWorld.rayTraceBlocks(positionEyes, hit.hitVec, false, false, false);
            return hit2 != null && hit2.typeOfHit == MovingObjectType.BLOCK ? null : new MovingObjectPosition(entityLivingBase2, hit.hitVec);
        }
        return null;
    }

    private boolean checkState35() {
        if (!this.verusSetting.matchesString2("Watchdog") && !this.verusSetting.matchesString2("Grim")) {
            if (this.checkState46()) {
                return false;
            }

            if (this.checkState34()) {
                return false;
            }
        }

        if (this.count10 > 0) {
            return false;
        }
        if (this.flag11) {
            return false;
        }
        if (this.flag12 && !this.flag2) {
            return false;
        }
        return Mouse.isButtonDown(1) && !this.flag2 ? false : !mc.thePlayer.isUsingItem() || this.flag2;
    }

    public boolean w() {
        return KeepSprint.instance != null && KeepSprint.instance.checkState13();
    }

    private boolean matchesEntityPlayer2(EntityPlayer player) {
        if (this.ignoreShopSetting.isEnabled() && this.matchesEntityPlayer(player)) {
            return true;
        }
        if (this.ignoreChainSetting.isEnabled() && this.matchesEntityPlayer3(player)) {
            return true;
        }
        return this.ignoreDarksSetting.isEnabled() && this.matchesEntityPlayer7(player)
            ? true
            : this.ignoreSetSetting.isEnabled() && this.matchesEntityPlayer6(player);
    }

    protected boolean x() {
        if (mc.gameSettings != null && mc.gameSettings.keyBindAttack != null) {
            int keyCode = mc.gameSettings.keyBindAttack.getKeyCode();
            if (keyCode < 0 && !Mouse.isCreated()) {
                return false;
            }
            return keyCode >= 0 && !Keyboard.isCreated() ? false : IsKeyDownHelper.matchesInt(keyCode);
        }
        return false;
    }

    public boolean y() {
        return this.checkState44()
            && this.flag38
            && mc.thePlayer != null
            && mc.theWorld != null
            && this.checkState27()
            && entityLivingBase2 != null
            && !entityLivingBase2.isDead
            && mc.theWorld.getEntityByID(entityLivingBase2.getEntityId()) == entityLivingBase2
            && OnJumpHelper.computeFloat(entityLivingBase2) <= this.attackRangeSetting.computeDouble();
    }

    private List<EntityLivingBase> computeList2(double d, int n) {
        List<EntityLivingBase> livings = this.computeList(d);
        if (livings.isEmpty()) {
            return Collections.emptyList();
        }
        List<EntityLivingBase> livings2 = new ArrayList<>();
        if (KOS.getInstance().checkState44()) {
            for (EntityLivingBase living : livings) {
                if (living instanceof EntityPlayer && WHelper.matchesString(living.getName())) {
                    livings2.add(living);
                }
            }

            if (livings2.isEmpty()) {
                livings2 = livings;
            }
        } else {
            livings2 = livings;
        }

        var livings3 = this.computeList2((List<EntityLivingBase>)livings2);
        return livings3.size() <= n ? livings3 : livings3.subList(0, n);
    }

    public static boolean matchesEntity(Entity entity) {
        return entity instanceof EntityPlayer
            && Truces.getInstance() != null
            && Truces.getInstance().checkState44()
            && TruceManager.matchesString(entity.getName());
    }

    private boolean z() {
        return this.throughWallsSetting.isEnabled();
    }

    public KillAura() {
        super("Kill Aura", 0, Category.COMBAT);
        this.handleSettingArray(
            new Setting[]{
                this.multipleSetting,
                this.switchDelaySetting,
                this.smoothingSetting,
                this.angleStepSetting,
                this.targetAmountBothSetting,
                this.blockRangeSetting,
                this.rotationRangeSetting,
                this.swingRangeSetting,
                this.attackRangeSetting,
                this.horizontalAngleSetting,
                this.verticalAngleSetting,
                this.hurttimeSetting,
                this.distanceSetting,
                this.highSetting,
                this.verusSetting,
                this.cpS10Setting,
                this.cpS10Setting2,
                this.lookPacketSetting,
                this.hideClientRotationsSetting,
                this.minCPSSetting,
                this.maxCPSSetting,
                this.dynamicSetting,
                this.pitSpawnSetting,
                this.throughWallsSetting,
                this.hitboxVisibleSetting,
                this.raycastSetting,
                this.teamsSetting,
                this.allyHitSyncSetting,
                this.allyHitSyncRangeSetting,
                this.weaponsOnlySetting,
                this.manualBlockSetting,
                this.requirePressSetting,
                this.disableWhileMiningSetting,
                this.disableWhenBlockingSetting,
                this.disableInGUISetting,
                this.autoblockInInventorySetting,
                this.disableWhilePantsSwappingSetting,
                this.attackWhileDelayingSetting,
                this.onlyWhileSneakingSetting,
                this.targetsSetting,
                this.debugSetting,
                this.noiseSetting,
                this.prioritiseKillAuraSetting
            }
        );
        instance = this;
        this.factor = 0.0F;
    }

    private List<EntityLivingBase> computeList(double d) {
        List list = new ArrayList();
        if (mc.theWorld != null && mc.thePlayer != null) {
            BooleanSetting boolSetting = (BooleanSetting)this.targetsSetting.computeSetting("Players");
            BooleanSetting boolSetting2 = (BooleanSetting)this.targetsSetting.computeSetting("Animals");
            BooleanSetting boolSetting3 = (BooleanSetting)this.targetsSetting.computeSetting("Mobs");
            BooleanSetting boolSetting4 = (BooleanSetting)this.targetsSetting.computeSetting("Golem");
            BooleanSetting boolSetting5 = (BooleanSetting)this.targetsSetting.computeSetting("Silverfish");
            BooleanSetting boolSetting6 = (BooleanSetting)this.targetsSetting.computeSetting("Invisible");
            if (!boolSetting.isEnabled() && !boolSetting2.isEnabled() && !boolSetting3.isEnabled() && !boolSetting4.isEnabled() && !boolSetting5.isEnabled()) {
                return list;
            }
            for (Entity entity : this.entries3) {
                if (entity instanceof EntityLivingBase living
                    && living != mc.thePlayer
                    && !living.isDead
                    && !matchesEntity(living)
                    && !(OnJumpHelper.computeFloat(living) > d)) {
                    boolean flag = false;
                    if (boolSetting.isEnabled() && living instanceof EntityPlayer) {
                        flag = true;
                    } else if (boolSetting2.isEnabled() && living instanceof EntityAnimal) {
                        flag = true;
                    } else if (boolSetting3.isEnabled() && living instanceof EntityMob) {
                        flag = true;
                    } else if (boolSetting4.isEnabled() || boolSetting5.isEnabled()) {
                        String str = living.getClass().getSimpleName().toLowerCase();
                        if (boolSetting4.isEnabled() && str.contains("golem")) {
                            flag = true;
                        } else if (boolSetting5.isEnabled() && str.contains("silverfish")) {
                            flag = true;
                        }
                    }

                    if (flag
                        && (!(living instanceof EntityPlayer) || !AntiBot.matchesEntity(living))
                        && !(this.ignoreInLiquidSetting.isEnabled() & living.isInLava())
                        && !(living instanceof EntityPlayer player && this.matchesEntityPlayer5(player))
                        && !(
                            living instanceof EntityPlayer player2
                                && (
                                    Friends.getInstance().checkState44()
                                            && DefaultHelper.matchesString(player2.getName())
                                            && !HitTickStealer.checkState()
                                            && !CombatModule.checkState2()
                                        || this.matchesEntityPlayer2(player2)
                                )
                        )
                        && (!this.weaponsOnlySetting.isEnabled() || this.checkState11())
                        && OnJumpHelper.matchesEntityLivingBase(living, (float)this.horizontalAngleSetting.computeDouble())
                        && (this.verticalAngleSetting == null || matchesEntityLivingBase2(living, this.verticalAngleSetting.computeDouble()))) {
                        if (this.raycastSetting.isEnabled() && !this.throughWallsSetting.isEnabled()) {
                            MovingObjectPosition hit = OnJumpHelper.computeMovingObjectPosition(living);
                            if (hit != null) {
                                continue;
                            }
                        }

                        if ((!this.checkState6() || matchesEntityLivingBase3(living, d))
                            && (
                                !this.pitSpawnSetting.isEnabled()
                                    || !FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)
                                        && (!(living instanceof EntityPlayer) || !FreshHelper.matchesWorld3(mc.theWorld, (EntityPlayer)living))
                            )) {
                            list.add(living);
                        }
                    }
                }
            }

            return list;
        }
        return list;
    }

    private EntityLivingBase computeEntityLivingBase(double d) {
        var livings = this.computeList(d);
        if (livings.isEmpty()) {
            return null;
        }
        if (KOS.getInstance().checkState44()) {
            this.entries2.clear();

            for (EntityLivingBase living : livings) {
                if (living instanceof EntityPlayer && WHelper.matchesString(living.getName())) {
                    this.entries2.add(living);
                }
            }

            if (!this.entries2.isEmpty()) {
                var livings2 = this.computeList2(this.entries2);
                return (EntityLivingBase)livings2.get(0);
            }
        }

        var livings3 = this.computeList2(livings);
        return (EntityLivingBase)livings3.get(0);
    }

    public boolean matchesEntityLivingBase4(EntityLivingBase living) {
        return this.checkState44()
            && living == entityLivingBase2
            && this.checkState27()
            && !living.isDead
            && this.flag36
            && !this.flag7
            && !this.flag37
            && (!this.disableWhilePantsSwappingSetting.isEnabled() || !StartHotbarMovesHelper.flag)
            && !this.checkState28()
            && !this.checkState23()
            && !this.checkState46()
            && this.count10 <= 0
            && this.count9 <= 0
            && !this.flag12
            && !this.flag11
            && !mc.playerController.isHittingBlock
            && !this.checkState33()
            && (KeepSprint.instance == null || !KeepSprint.instance.checkState())
            && OnJumpHelper.computeFloat(living) <= this.attackRangeSetting.computeDouble()
            && (!(living instanceof EntityPlayer) || !this.matchesEntityPlayer5((EntityPlayer)living));
    }

    private boolean checkState46() {
        AutoHeal instance = AutoHeal.getInstance();
        return instance != null && instance.checkState44() && instance.isCount();
    }

    @Generated
    public static KillAura getInstance() {
        return instance;
    }

    public boolean checkState58() {
        return this.checkState44()
            && mc.thePlayer != null
            && entityLivingBase2 != null
            && !entityLivingBase2.isDead
            && entityLivingBase2.getHealth() > 0.0F
            && !this.verusSetting.matchesString2("None")
            && !this.verusSetting.matchesString2("Fake")
            && this.checkState26()
            && (!this.manualBlockSetting.isEnabled() || this.flag33)
            && mc.thePlayer.getDistanceToEntity(entityLivingBase2) <= this.blockRangeSetting.computeDouble();
    }

    private void process34() {
        mc.playerController.syncCurrentPlayItem();
        this.handleItemStack(mc.thePlayer.getHeldItem());
    }

    @Override
    public void onEnable() {
        this.flag45 = false;
        this.timestamp3 = Long.MIN_VALUE;
        this.flag46 = false;
        if (this.combatHandler != null) {
            this.combatHandler.reset();
        }

        if (this.combatHandler2 != null) {
            this.combatHandler2.reset();
        }

        this.flag43 = false;
        this.flag44 = false;
        this.timestamp4 = this.timestamp5 = 0L;
        this.flag47 = this.flag48 = this.flag49 = false;
        this.floatValueArray2 = null;
        this.flag20 = this.flag21 = this.flag22 = false;
        super.onEnable();
        this.flag38 = true;
        this.flag39 = false;
        this.flag40 = false;
        this.process41();
        if (mc.thePlayer != null) {
            this.floatValueArray5 = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
        } else {
            this.floatValueArray5 = null;
        }

        this.entries5 = new ArrayList<>();
        this.factor = mc.thePlayer != null ? mc.thePlayer.rotationYaw : 0.0F;
        this.timerUtil.reset();
        this.timestamp2 = 0L;
        this.count36 = 0;
        this.entries3 = new ArrayList<>();
        OnJumpHelper.process4();
        if (mc.thePlayer != null) {
            OnJumpHelper.floatValueArray = mc.thePlayer.getLastReportedRotations();
            OnJumpHelper.process5();
        }

        this.flag12 = false;
        this.flag11 = false;
        this.count10 = 0;
        this.count9 = 0;
        this.flag8 = false;
        this.flag14 = Mouse.isButtonDown(1);
        this.flag13 = false;
        if (mc.thePlayer.isUsingItem() && this.checkState16()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            if (this.debugSetting.isEnabled()) {
                GetCurrentScreenHelper.handleString3("§c[KA] onEnable: C07 sent (clearing any blocking state)");
            }

            mc.thePlayer.stopUsingItem();
        }

        if (Mouse.isButtonDown(1) && !this.checkState20()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
        }

        this.blocking = false;
        this.flag24 = false;
        this.flag23 = false;
        this.flag42 = false;
        this.flag41 = false;
        this.flag33 = false;
        this.flag31 = false;
        this.count30 = 0;
        this.count27 = 0;
    }

    public boolean checkState53() {
        if (this.verusSetting == null) {
            return this.flag42 || this.flag41 || this.flag44 || this.blocking;
        }
        if (!this.verusSetting.matchesString2("Fake") && !this.verusSetting.matchesString2("None")) {
            if ((this.verusSetting.matchesString2("Watchdog") || this.verusSetting.matchesString2("Grim")) && !this.checkState38()) {
                return this.checkState44() && this.flag46;
            }
            return this.checkState38() ? this.flag44 : this.flag42 || this.flag41 || this.blocking;
        }
        return false;
    }

    public boolean isFlag2() {
        return this.flag2;
    }

    public static boolean matchesEntityLivingBase3(EntityLivingBase living, double d) {
        if (mc.thePlayer != null && mc.theWorld != null && living != null) {
            AxisAlignedBB box = living.getEntityBoundingBox()
                .expand(living.getCollisionBorderSize(), living.getCollisionBorderSize(), living.getCollisionBorderSize());
            Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
            Vec3 vec = new Vec3(
                MathHelper.clamp_double(positionEyes.xCoord, box.minX, box.maxX),
                MathHelper.clamp_double(positionEyes.yCoord, box.minY, box.maxY),
                MathHelper.clamp_double(positionEyes.zCoord, box.minZ, box.maxZ)
            );
            if (positionEyes.distanceTo(vec) <= d && mc.theWorld.rayTraceBlocks(positionEyes, vec, false, true, false) == null) {
                return true;
            }
            double[] doubles = new double[]{box.minX, (box.minX + box.maxX) * 0.5, box.maxX};
            double[] doubles2 = new double[]{box.minY, (box.minY + box.maxY) * 0.5, box.maxY};
            double[] doubles3 = new double[]{box.minZ, (box.minZ + box.maxZ) * 0.5, box.maxZ};

            for (double d2 : doubles) {
                for (double d3 : doubles2) {
                    for (double d4 : doubles3) {
                        Vec3 vec2 = new Vec3(d2, d3, d4);
                        if (positionEyes.distanceTo(vec2) <= d && mc.theWorld.rayTraceBlocks(positionEyes, vec2, false, true, false) == null) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }
        return false;
    }

    private void process8() {
        if (this.flag32) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            this.flag32 = false;
        }
    }

    public boolean checkState13() {
        if (this.checkState58() && this.checkState53() && !this.checkState14()) {
            NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            this.blocking = false;
            this.flag42 = false;
            this.flag41 = false;
            this.flag13 = false;
            this.process35();
            return true;
        }
        return false;
    }

    public boolean checkState9() {
        return this.verusSetting.matchesString2("Watchdog") && this.checkState38() && !this.checkState15();
    }

    private boolean checkState20() {
        return mc.thePlayer != null && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBow;
    }

    public static void process27() {
        KillAura killAura = instance;
        if (killAura != null && killAura.flag25) {
            boolean flag = mc.thePlayer != null
                && mc.thePlayer == killAura.entityPlayerSP
                && mc.theWorld != null
                && mc.theWorld == killAura.worldClient
                && !mc.thePlayer.isDead;
            if (!flag || !killAura.matchesFlag(true)) {
                killAura.flag25 = false;
                killAura.entityPlayerSP = null;
                killAura.worldClient = null;
                killAura.floatValueArray5 = null;
                killAura.factor = mc.thePlayer == null ? 0.0F : mc.thePlayer.rotationYaw;
                if (!killAura.checkState52()) {
                    OnJumpHelper.disable();
                }
            }
        }
    }

    private boolean checkState31() {
        String str = this.verusSetting.getText().toLowerCase();
        return str.equals("legit")
            || str.equals("ncp")
            || str.equals("watchdog")
            || str.equals("grim")
            || str.equals("watchdog2")
            || str.equals("watchdog3");
    }

    private void process21() {
        if (this.flag47 && !this.flag48) {
            MovingObjectPosition hit = this.computeMovingObjectPosition();
            if (hit != null) {
                Vec3 vec = hit.hitVec.subtract(new Vec3(entityLivingBase2.posX, entityLivingBase2.posY, entityLivingBase2.posZ));
                NetworkHelper.handlePacket2(new C02PacketUseEntity(entityLivingBase2, vec));
                NetworkHelper.handlePacket2(new C02PacketUseEntity(entityLivingBase2, C02PacketUseEntity.Action.INTERACT));
            }

            if (!this.flag48) {
                this.flag49 = true;
                if (this.debugSetting.isEnabled()) {
                    GetCurrentScreenHelper.handleString3("[Autoblock] C08 deferred: no accepted entity interaction after attack");
                }

                return;
            }
        }

        NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
        this.flag49 = !this.flag44;
        if (this.debugSetting.isEnabled()) {
            String text = this.verusSetting.getText();
            String str = this.verusSetting.matchesString2("Grim") ? this.cpS10Setting2.getText() : this.cpS10Setting.getText();
            long l = this.timestamp4;
            boolean flag = this.flag44;
            String str2 = str;
            String str3 = text;
            GetCurrentScreenHelper.handleString3("[Autoblock " + str3 + " " + str2 + "] C08 attempted; outgoing block=" + flag + ", movement=" + l);
        }
    }

    private void process37() {
        boolean flag = this.checkState38();
        if (this.flag43 != flag) {
            this.handleFlag2(true);
            this.flag43 = flag;
        }

        if (!this.checkState26()) {
            this.handleFlag2(true);
        } else if (!this.disableWhenBlockingSetting.isEnabled() || !this.checkState33() && !this.flag35) {
            if (!this.checkState41()) {
                this.handleFlag2(true);
            } else {
                String text = this.verusSetting.getText();
                if (this.verusSetting.getText().equalsIgnoreCase("None") && this.manualBlockSetting.isEnabled()) {
                    text = "Fake";
                }

                String str = text.toLowerCase();
                if (str.equals("legit") || str.equals("ncp")) {
                    this.process42();
                }

                String str2 = text.toLowerCase();
                switch (str2) {
                    case "none":
                        this.blocking = false;
                        break;
                    case "vanilla":
                        if (this.checkState41()) {
                            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                            this.blocking = true;
                            this.flag13 = true;
                        }
                        break;
                    case "verus":
                        if (this.blocking) {
                            NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        }

                        if (this.checkState41()) {
                            NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                            this.blocking = true;
                            this.flag13 = true;
                        }
                        break;
                    case "ncp":
                        if (this.manualBlockSetting.isEnabled() && !this.flag33 && !this.flag5) {
                            this.handleFlag2(true);
                            return;
                        }

                        if (this.count30 == 0) {
                            if (this.checkState41()) {
                                NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                this.blocking = true;
                                this.flag13 = true;
                            }

                            this.count30++;
                        } else if (this.count30 == 1) {
                            NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                            this.blocking = true;
                            this.count30++;
                        } else if (this.count30 == 2) {
                            if (entityLivingBase2 instanceof EntityLivingBase) {
                                if (!this.matchesEntityLivingBase(entityLivingBase2, false)) {
                                    NetworkHelper.handlePacket2(new C0APacketAnimation());
                                }
                            } else {
                                NetworkHelper.handlePacket2(new C0APacketAnimation());
                            }

                            this.count30++;
                        } else if (this.count30 == 3) {
                            if (this.checkState41()) {
                                NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                this.blocking = true;
                                this.flag13 = true;
                            }

                            this.count30 = 0;
                        }
                        break;
                    case "legit":
                        if (this.manualBlockSetting.isEnabled() && !this.flag33 && !this.flag5) {
                            this.count30 = 0;
                        } else {
                            if (!this.checkState16()) {
                                this.handleFlag2(true);
                                return;
                            }

                            if (this.flag11 || this.checkState40()) {
                                this.count30 = 0;
                            } else if (this.count30 == 0) {
                                if (this.checkState41()) {
                                    NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                    this.blocking = true;
                                    this.flag42 = true;
                                    this.flag41 = true;
                                    this.flag13 = true;
                                }

                                this.count30++;
                            } else if (this.count30 == 1) {
                                if (this.flag42 || this.flag41) {
                                    NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                }

                                this.blocking = false;
                                this.flag42 = false;
                                this.flag41 = false;
                                if (entityLivingBase2 instanceof EntityLivingBase) {
                                    this.matchesEntityLivingBase(entityLivingBase2, true);
                                }

                                if (this.checkState41()) {
                                    NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                    this.blocking = true;
                                    this.flag42 = true;
                                    this.flag41 = true;
                                    this.flag13 = true;
                                }

                                this.count30 = 0;
                            }
                        }
                        break;
                    case "swap":
                        if (this.manualBlockSetting.isEnabled() && !this.flag33 && !this.flag5) {
                            this.handleFlag2(true);
                            return;
                        }

                        if (!this.flag11 && !this.checkState40()) {
                            switch (this.count30) {
                                case 0:
                                    int n = this.computeInt(mc.thePlayer.inventory.currentItem);
                                    if (n != -1) {
                                        this.count29 = n;
                                        this.flag37 = true;
                                    }

                                    this.count30 = 1;
                                    break;
                                case 1:
                                    int n2 = this.computeInt(mc.thePlayer.inventory.currentItem);
                                    if (n2 == -1) {
                                        this.count30 = 0;
                                    } else if (!this.checkState42()) {
                                        this.flag37 = true;
                                    } else if (this.count18 >= this.count19 - 1) {
                                        NetworkHelper.handlePacket2(new C09PacketHeldItemChange(n2));
                                        ItemStack stack = mc.thePlayer.inventory.getStackInSlot(n2);
                                        if (stack != null) {
                                            this.handleItemStack(stack);
                                        }

                                        NetworkHelper.handlePacket2(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                                        this.flag36 = false;
                                        this.count30 = 0;
                                    }
                                    break;
                                default:
                                    this.count30 = 0;
                            }

                            this.flag23 = true;
                        } else {
                            this.count30 = 0;
                        }
                        break;
                    case "grim":
                    case "watchdog":
                        if (this.checkState38()) {
                            this.process23();
                        } else if (this.manualBlockSetting.isEnabled() && !this.flag33 && !this.flag5) {
                            this.count30 = 0;
                        } else if (this.count30 == 0) {
                            this.count30++;
                        } else if (this.count30 == 1) {
                            if (OnJumpHelper.computeFloat(entityLivingBase2) <= this.swingRangeSetting.computeDouble()) {
                                this.matchesEntityLivingBase2(entityLivingBase2, true);
                            }

                            this.blocking = false;
                            this.count30++;
                        } else if (this.count30 == 2) {
                            if (this.checkState41() && !LagRange.instance.flag && !CountMovementHelper.getInstance().isFlag()) {
                                NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                this.blocking = true;
                                this.flag42 = true;
                                this.flag41 = true;
                                this.flag13 = true;
                            }

                            this.reset();
                            this.flag30 = true;
                            this.count25 = 1;
                            this.count30++;
                        } else if (this.count30 == 3) {
                            if (this.flag42 || this.flag41) {
                                NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                            }

                            this.blocking = false;
                            this.flag42 = false;
                            this.flag41 = false;
                            this.count30 = 1;
                        }
                        break;
                    case "watchdog2":
                        if (this.manualBlockSetting.isEnabled() && !this.flag33 && !this.flag5) {
                            this.count30 = 0;
                        } else {
                            if (!this.checkState16()) {
                                this.handleFlag2(true);
                                return;
                            }

                            if (!this.flag11 && !this.checkState40()) {
                                if (LagRange.instance.flag || CountMovementHelper.getInstance().isFlag()) {
                                    this.handleFlag2(true);
                                    return;
                                }

                                if (this.count30 == 0) {
                                    this.count30++;
                                } else if (this.count30 == 1) {
                                    if (OnJumpHelper.computeFloat(entityLivingBase2) <= this.swingRangeSetting.computeDouble()) {
                                        this.matchesEntityLivingBase2(entityLivingBase2, true);
                                    }

                                    this.blocking = false;
                                    this.count30++;
                                } else if (this.count30 == 2) {
                                    if (this.checkState41() && !LagRange.instance.flag && !CountMovementHelper.getInstance().isFlag()) {
                                        NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                        this.blocking = true;
                                        this.flag42 = true;
                                        this.flag41 = true;
                                        this.flag13 = true;
                                    }

                                    this.reset();
                                    this.flag30 = true;
                                    this.count25 = 1;
                                    this.count30++;
                                } else if (this.count30 == 3) {
                                    this.count30++;
                                } else if (this.count30 == 4) {
                                    if (this.flag42 || this.flag41) {
                                        NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                    }

                                    this.blocking = false;
                                    this.flag42 = false;
                                    this.flag41 = false;
                                    this.count30 = 1;
                                }
                                break;
                            }

                            this.count30 = 0;
                        }
                        break;
                    case "watchdog3":
                        if (this.manualBlockSetting.isEnabled() && !this.flag33 && !this.flag5) {
                            this.count30 = 0;
                        } else {
                            if (!this.checkState16()) {
                                this.handleFlag2(true);
                                return;
                            }

                            if (!this.flag11 && !this.checkState40()) {
                                if (LagRange.instance.flag || CountMovementHelper.getInstance().isFlag()) {
                                    this.handleFlag2(true);
                                    return;
                                }

                                if (this.count30 == 0) {
                                    this.count30 = 1;
                                } else if (this.count30 == 1) {
                                    if (OnJumpHelper.computeFloat(entityLivingBase2) <= this.swingRangeSetting.computeDouble()) {
                                        this.matchesEntityLivingBase2(entityLivingBase2, true);
                                    }

                                    this.blocking = false;
                                    this.count30 = 2;
                                } else if (this.count30 == 2) {
                                    if (this.checkState41() && !LagRange.instance.flag && !CountMovementHelper.getInstance().isFlag()) {
                                        NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                        this.blocking = true;
                                        this.flag42 = true;
                                        this.flag41 = true;
                                        this.flag13 = true;
                                    }

                                    this.reset();
                                    this.flag30 = true;
                                    this.count25 = 1;
                                    this.count30 = 3;
                                } else if (this.count30 == 3) {
                                    if (this.flag42 || this.flag41) {
                                        NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                    }

                                    this.blocking = false;
                                    this.flag42 = false;
                                    this.flag41 = false;
                                    this.count30 = 4;
                                } else if (this.count30 == 4) {
                                    if (OnJumpHelper.computeFloat(entityLivingBase2) <= this.swingRangeSetting.computeDouble()) {
                                        this.matchesEntityLivingBase2(entityLivingBase2, true);
                                    }

                                    if (this.checkState41() && !LagRange.instance.flag && !CountMovementHelper.getInstance().isFlag()) {
                                        NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                        this.blocking = true;
                                        this.flag42 = true;
                                        this.flag41 = true;
                                        this.flag13 = true;
                                    }

                                    this.reset();
                                    this.flag30 = true;
                                    this.count25 = 1;
                                    this.count30 = 3;
                                }
                                break;
                            }

                            this.count30 = 0;
                        }
                        break;
                    case "watchdog8":
                        if (this.manualBlockSetting.isEnabled() && !this.flag33 && !this.flag5) {
                            this.count30 = 0;
                        } else {
                            if (!this.checkState16()) {
                                this.handleFlag2(true);
                                return;
                            }

                            if (!this.flag11 && !this.checkState40()) {
                                if (LagRange.instance.flag || CountMovementHelper.getInstance().isFlag()) {
                                    this.handleFlag2(true);
                                    return;
                                }

                                if (this.count30 == 0) {
                                    this.count30 = 1;
                                } else if (this.count30 == 1) {
                                    if (OnJumpHelper.computeFloat(entityLivingBase2) <= this.swingRangeSetting.computeDouble()) {
                                        this.matchesEntityLivingBase2(entityLivingBase2, true);
                                    }

                                    this.blocking = false;
                                    this.count30 = 2;
                                } else if (this.count30 == 2) {
                                    if (this.checkState41() && !LagRange.instance.flag && !CountMovementHelper.getInstance().isFlag()) {
                                        NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                        this.blocking = true;
                                        this.flag42 = true;
                                        this.flag41 = true;
                                        this.flag13 = true;
                                    }

                                    this.reset();
                                    this.flag30 = true;
                                    this.count25 = 1;
                                    this.count30 = 3;
                                } else if (this.count30 == 3) {
                                    if (this.flag42 || this.flag41) {
                                        NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                    }

                                    this.blocking = false;
                                    this.flag42 = false;
                                    this.flag41 = false;
                                    this.count30 = 4;
                                } else if (this.count30 == 4) {
                                    if (OnJumpHelper.computeFloat(entityLivingBase2) <= this.swingRangeSetting.computeDouble()) {
                                        this.matchesEntityLivingBase2(entityLivingBase2, true);
                                    }

                                    this.blocking = false;
                                    if (this.checkState41() && !LagRange.instance.flag && !CountMovementHelper.getInstance().isFlag()) {
                                        NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                        this.blocking = true;
                                        this.flag42 = true;
                                        this.flag41 = true;
                                        this.flag13 = true;
                                    }

                                    this.reset();
                                    this.flag30 = true;
                                    this.count25 = 1;
                                    this.count30 = 5;
                                } else if (this.count30 == 5) {
                                    if (this.flag42 || this.flag41) {
                                        NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                    }

                                    this.blocking = false;
                                    this.flag42 = false;
                                    this.flag41 = false;
                                    this.count30 = 1;
                                }
                                break;
                            }

                            this.count30 = 0;
                        }
                        break;
                    case "watchdog10":
                        if (this.manualBlockSetting.isEnabled() && !this.flag33 && !this.flag5) {
                            this.count30 = 0;
                        } else {
                            if (!this.checkState16()) {
                                this.handleFlag2(true);
                                return;
                            }

                            if (!this.flag11 && !this.checkState40()) {
                                if (LagRange.instance.flag || CountMovementHelper.getInstance().isFlag()) {
                                    this.handleFlag2(true);
                                    return;
                                }

                                if (this.count30 == 0) {
                                    this.count30 = 1;
                                } else if (this.count30 == 1) {
                                    if (OnJumpHelper.computeFloat(entityLivingBase2) <= this.swingRangeSetting.computeDouble()) {
                                        this.matchesEntityLivingBase2(entityLivingBase2, true);
                                    }

                                    this.blocking = false;
                                    if (this.checkState41() && !LagRange.instance.flag && !CountMovementHelper.getInstance().isFlag()) {
                                        NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                        this.blocking = true;
                                        this.flag42 = true;
                                        this.flag41 = true;
                                        this.flag13 = true;
                                    }

                                    this.reset();
                                    this.flag30 = true;
                                    this.count25 = 1;
                                    this.count30 = 2;
                                } else if (this.count30 == 2) {
                                    if (this.flag42 || this.flag41) {
                                        NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                    }

                                    this.blocking = false;
                                    this.flag42 = false;
                                    this.flag41 = false;
                                    this.count30 = 1;
                                }
                                break;
                            }

                            this.count30 = 0;
                        }
                        break;
                    case "watchdog12":
                        if (this.manualBlockSetting.isEnabled() && !this.flag33 && !this.flag5) {
                            this.count30 = 0;
                        } else {
                            if (!this.checkState16()) {
                                this.handleFlag2(true);
                                return;
                            }

                            if (!this.flag11 && !this.checkState40()) {
                                if (LagRange.instance.flag || CountMovementHelper.getInstance().isFlag()) {
                                    this.handleFlag2(true);
                                    return;
                                }

                                if (this.count30 == 0) {
                                    this.count30 = 1;
                                } else if (this.count30 == 1) {
                                    if (OnJumpHelper.computeFloat(entityLivingBase2) <= this.swingRangeSetting.computeDouble()) {
                                        this.matchesEntityLivingBase2(entityLivingBase2, true);
                                    }

                                    this.blocking = false;
                                    this.count30 = 2;
                                } else if (this.count30 == 2) {
                                    if (this.checkState41() && !LagRange.instance.flag && !CountMovementHelper.getInstance().isFlag()) {
                                        NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                        this.blocking = true;
                                        this.flag42 = true;
                                        this.flag41 = true;
                                        this.flag13 = true;
                                    }

                                    this.reset();
                                    this.flag30 = true;
                                    this.count25 = 1;
                                    this.count30 = 3;
                                } else if (this.count30 == 3) {
                                    if (this.flag42 || this.flag41) {
                                        NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                    }

                                    this.blocking = false;
                                    this.flag42 = false;
                                    this.flag41 = false;
                                    if (OnJumpHelper.computeFloat(entityLivingBase2) <= this.swingRangeSetting.computeDouble()) {
                                        this.matchesEntityLivingBase2(entityLivingBase2, true);
                                    }

                                    this.count30 = 1;
                                }
                                break;
                            }

                            this.count30 = 0;
                        }
                        break;
                    case "strictorder":
                        if (this.manualBlockSetting.isEnabled() && !this.flag33 && !this.flag5) {
                            this.count30 = 0;
                        } else if (this.count30 == 0) {
                            if (!this.flag42 && !this.flag41 && this.checkState41()) {
                                NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                this.blocking = true;
                                this.flag42 = true;
                                this.flag41 = true;
                                this.flag13 = true;
                            }

                            this.count30 = 1;
                        } else if (this.count30 == 1) {
                            if (OnJumpHelper.computeFloat(entityLivingBase2) <= this.swingRangeSetting.computeDouble()) {
                                this.matchesEntityLivingBase2(entityLivingBase2, true);
                            }

                            this.blocking = false;
                            this.count30 = 2;
                        } else if (this.count30 == 2) {
                            this.count30 = 3;
                        } else if (this.count30 == 3) {
                            if (this.checkState41()) {
                                NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                this.blocking = true;
                                this.flag42 = true;
                                this.flag41 = true;
                            }

                            this.count30 = 4;
                        } else if (this.count30 == 4) {
                            this.count30 = 5;
                        } else if (this.count30 == 5) {
                            if (this.flag42 || this.flag41) {
                                NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                            }

                            this.blocking = false;
                            this.flag42 = false;
                            this.flag41 = false;
                            this.count30 = 6;
                        } else if (this.count30 == 6) {
                            this.count30 = 1;
                        }
                        break;
                    case "packetinterval":
                        if (this.manualBlockSetting.isEnabled() && !this.flag33 && !this.flag5) {
                            this.count30 = 0;
                        } else if (this.count30 == 0) {
                            if (!this.flag42 && !this.flag41 && this.checkState41()) {
                                NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                this.blocking = true;
                                this.flag42 = true;
                                this.flag41 = true;
                            }

                            this.count23 = 0;
                            this.count30 = 1;
                        } else if (this.count30 == 1) {
                            this.count23++;
                            if (this.count23 >= 2) {
                                if (this.flag42 || this.flag41) {
                                    NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                }

                                this.blocking = false;
                                this.flag42 = false;
                                this.flag41 = false;
                                if (OnJumpHelper.computeFloat(entityLivingBase2) <= this.swingRangeSetting.computeDouble()) {
                                    this.matchesEntityLivingBase2(entityLivingBase2, true);
                                }

                                this.count30 = 2;
                            }
                        } else if (this.count30 == 2) {
                            if (this.checkState41()) {
                                NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                this.blocking = true;
                                this.flag42 = true;
                                this.flag41 = true;
                            }

                            this.count23 = 0;
                            this.count30 = 1;
                        }
                        break;
                    case "fake":
                        if (this.checkState41()) {
                            this.blocking = true;
                            this.flag23 = true;
                        } else {
                            this.blocking = false;
                            this.flag23 = false;
                        }
                }

                boolean flag2 = this.verusSetting.matchesString2("Swap");
                if (this.flag37 && flag2) {
                    this.process34();
                    this.blocking = true;
                    this.flag37 = false;
                }
            }
        } else {
            this.handleFlag2(true);
        }
    }

    private void process42() {
        this.count18++;
    }

    private boolean matchesEntityPlayer7(EntityPlayer player) {
        for (int i = 0; i < 4; i++) {
            ItemStack stack = player.inventory.armorInventory[i];
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                NBTTagCompound tagCompound = stack.getTagCompound();
                if (tagCompound != null && tagCompound.hasKey("display", 10)) {
                    NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
                    if (compoundTag.hasKey("Lore", 9)) {
                        NBTTagList tagList = compoundTag.getTagList("Lore", 8);

                        for (int j = 0; j < tagList.tagCount(); j++) {
                            String stringTagAt = tagList.getStringTagAt(j);

                            for (String str : entries4) {
                                if (stringTagAt.contains(str)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private int computeInt2() {
        int currentItem = mc.thePlayer.inventory.currentItem;
        int n = -1;
        if (currentItem < 8) {
            n = currentItem + 1;
        } else {
            n = currentItem - 1;
        }

        return n;
    }

    private void process32() {
        if (!this.checkState16()) {
            this.flag33 = this.flag23 = this.blocking = false;
        } else {
            boolean flag = entityLivingBase2 != null || this.entries5 != null && !this.entries5.isEmpty();
            if (this.checkState28()) {
                this.flag33 = this.flag23 = this.blocking = false;
            } else if (this.verusSetting.matchesString2("None")) {
                if (this.checkState37()) {
                    this.checkState39();
                }

                this.flag33 = this.flag23 = this.blocking = false;
                if (this.checkState37()) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                }
            } else if (this.checkState51()) {
                this.checkState39();
                this.flag33 = this.manualBlockSetting.isEnabled() && this.checkState47();
                if (this.manualBlockSetting.isEnabled()) {
                    this.flag23 = this.blocking = this.flag33;
                }

                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                if (mc.thePlayer.isUsingItem()) {
                    mc.thePlayer.stopUsingItem();
                }
            } else if (this.manualBlockSetting.isEnabled() && !this.checkState16()) {
                this.flag33 = false;
                this.flag23 = false;
                this.blocking = false;
            } else if (this.manualBlockSetting.isEnabled() && flag) {
                boolean flag2 = this.checkState47() && this.checkState16();
                this.flag33 = flag2;
                this.count35 = flag2 ? this.count35 + 1 : 0;
                this.flag23 = flag2;
                this.blocking = flag2;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                if (mc.thePlayer.isUsingItem()) {
                    mc.thePlayer.stopUsingItem();
                }
            } else {
                if (this.manualBlockSetting.isEnabled()) {
                    if (this.count7 <= 0 && this.count11 <= 0) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), Mouse.isCreated() && Mouse.isButtonDown(1));
                    } else {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                        if (mc.thePlayer.isUsingItem()) {
                            mc.thePlayer.stopUsingItem();
                        }
                    }
                }

                boolean flag3 = this.flag33;
                if (this.manualBlockSetting.isEnabled() && entityLivingBase2 != null && this.checkState16()) {
                    this.flag33 = this.checkState47() && this.flag2 && this.count10 == 0;
                    if (this.debugSetting.isEnabled() && this.checkState47() && !this.flag33) {
                        GetCurrentScreenHelper.handleString3(
                            "§e[MB] RMB down but manualBlocking=false | killauraActive=" + this.flag2 + " extCD=" + this.count10
                        );
                    }

                    if (!flag3 && this.flag33) {
                        this.count35 = 0;
                    }

                    if (this.flag33) {
                        this.count35++;
                    }
                } else {
                    this.flag33 = false;
                    this.count35 = 0;
                }
            }
        }
    }

    private void process6() {
        if (this.b()) {
            entityLivingBase2 = null;
            this.entries5.clear();
        } else if (!flag50) {
            if (entityLivingBase3 != null) {
                if (entityLivingBase3.isEntityAlive() && !entityLivingBase3.isDead) {
                    this.entries5.clear();
                    this.entries5.add(entityLivingBase3);
                    entityLivingBase2 = entityLivingBase3;
                    return;
                }

                entityLivingBase3 = null;
            }

            if (this.weaponsOnlySetting.isEnabled() && !this.checkState11()) {
                boolean flag = this.floatValueArray5 != null;
                entityLivingBase2 = null;
                this.entries5.clear();
                if (flag) {
                    this.process11();
                    OnJumpHelper.process5();
                }
            } else {
                double d = this.computeDouble();
                var livings = this.computeList(d);
                if (this.manualBlockSetting.isEnabled()
                    && Mouse.isButtonDown(1)
                    && !livings.isEmpty()
                    && mc.thePlayer.isUsingItem()
                    && this.checkState16()
                    && !this.flag2) {
                    if (mc.gameSettings != null) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                    }

                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    mc.thePlayer.stopUsingItem();
                    this.count10 = 1;
                    entityLivingBase2 = null;
                    this.entries5.clear();
                } else {
                    this.entries5.clear();
                    List list = this.computeList(livings, this.attackRangeSetting.computeDouble());
                    var livings2 = this.computeList(livings, this.swingRangeSetting.computeDouble());
                    if (!list.isEmpty()) {
                        if (this.multipleSetting.getText().equalsIgnoreCase("Multiple")) {
                            this.entries5.addAll(this.computeList2(this.attackRangeSetting.computeDouble(), (int)this.targetAmountBothSetting.computeDouble()));
                        } else if (this.multipleSetting.getText().equalsIgnoreCase("Switch")) {
                            this.count20++;
                            this.entries5.addAll(this.computeList(this.attackRangeSetting.computeDouble(), (int)this.targetAmountBothSetting.computeDouble()));
                        } else {
                            EntityLivingBase living = this.computeEntityLivingBase(this.attackRangeSetting.computeDouble());
                            if (living != null) {
                                this.entries5.add(living);
                            }
                        }
                    } else if (!livings2.isEmpty()) {
                        if (this.multipleSetting.getText().equalsIgnoreCase("Multiple")) {
                            this.entries5.addAll(this.computeList2(this.swingRangeSetting.computeDouble(), (int)this.targetAmountBothSetting.computeDouble()));
                        } else if (this.multipleSetting.getText().equalsIgnoreCase("Switch")) {
                            this.count20++;
                            this.entries5.addAll(this.computeList(this.swingRangeSetting.computeDouble(), (int)this.targetAmountBothSetting.computeDouble()));
                        } else {
                            EntityLivingBase living2 = this.computeEntityLivingBase(this.swingRangeSetting.computeDouble());
                            if (living2 != null) {
                                this.entries5.add(living2);
                            }
                        }
                    } else {
                        double d2 = Math.max(this.rotationRangeSetting.computeDouble(), this.blockRangeSetting.computeDouble());
                        var livings3 = this.computeList(livings, d2);
                        if (!livings3.isEmpty()) {
                            EntityLivingBase living3 = this.computeEntityLivingBase(d2);
                            if (living3 != null) {
                                this.entries5.add(living3);
                            }
                        }
                    }

                    entityLivingBase2 = !this.entries5.isEmpty() ? this.entries5.get(0) : null;
                    if (OnJumpHelper.matchesEntityLivingBase(entityLivingBase2)) {
                        OnJumpHelper.process5();
                    }
                }
            }
        } else {
            if (entityLivingBase3 != null && entityLivingBase3.isEntityAlive() && !entityLivingBase3.isDead) {
                this.entries5.clear();
                this.entries5.add(entityLivingBase3);
                entityLivingBase2 = entityLivingBase3;
            } else {
                entityLivingBase2 = null;
                this.entries5.clear();
            }
        }
    }

    public int computeInt(EntityPlayer player) {
        int n = 0;

        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                n += ((ItemArmor)stack.getItem()).damageReduceAmount;
            }
        }

        return n;
    }

    private boolean checkState39() {
        if ((this.verusSetting.matchesString2("Fake") || this.verusSetting.matchesString2("None")) && this.checkState16()) {
            if (this.flag20 || mc.thePlayer.isBlocking()) {
                this.flag20 = false;
                this.flag22 = true;
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                mc.thePlayer.stopUsingItem();
            }

            return this.r();
        }
        return false;
    }

    private void process() {
        if (entityLivingBase2 != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entityLivingBase2, C02PacketUseEntity.Action.INTERACT));
            this.handleItemStack(mc.thePlayer.getHeldItem());
        }
    }

    private boolean checkState33() {
        return mc.thePlayer.isUsingItem() && this.checkState16();
    }

    private boolean checkState42() {
        return (mc.thePlayer.isUsingItem() || this.flag24) && this.checkState16();
    }

    private int computeInt() {
        int n = -1;
        int currentItem = mc.thePlayer.inventory.currentItem;

        for (int i = 0; i < 9; i++) {
            if (i != currentItem) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack == null || stack.getUnlocalizedName().toLowerCase().contains("compass")) {
                    n = i;
                    break;
                }
            }
        }

        return n;
    }

    private boolean checkState45() {
        return (!this.manualBlockSetting.isEnabled() || this.verusSetting.getText().equalsIgnoreCase("None") || this.checkState47()) && this.checkState16();
    }

    private boolean checkState57() {
        return BedAura.getInstance() != null && BedAura.getInstance().checkState44();
    }

    public boolean checkState4() {
        return this.checkState44()
            && this.flag38
            && entityLivingBase2 != null
            && !entityLivingBase2.isDead
            && this.checkState27()
            && mc.currentScreen == null
            && !AutoGrinder.matchesModule(this);
    }

    private boolean matchesEntityLivingBase(EntityLivingBase living, boolean flag) {
        if (KeepSprint.instance != null && KeepSprint.instance.checkState()) {
            return false;
        }
        if (Displace.instance != null && Displace.instance.checkState44() && Displace.instance.checkState2()) {
            return false;
        }
        if (System.currentTimeMillis() < this.timestamp2) {
            return false;
        }
        double d = OnJumpHelper.computeFloat(living);
        double d2 = BackTrack.computeDouble(living);
        if (d2 >= 0.0 && d2 < d) {
            d = d2;
        }

        if (d > this.swingRangeSetting.computeDouble()) {
            return false;
        }
        if (d > this.attackRangeSetting.computeDouble()) {
            mc.thePlayer.swingItem();
            this.timestamp2 = System.currentTimeMillis() + this.computeLong();
            return false;
        }
        if (!this.matchesEntityLivingBase(living)) {
            mc.thePlayer.swingItem();
            return false;
        }
        if (this.allyHitSyncSetting.isEnabled() && living instanceof EntityPlayer) {
            double d3 = this.allyHitSyncRangeSetting.computeDouble();
            if (mc.theWorld != null) {
                for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                    if (playerEntity != mc.thePlayer
                        && DefaultHelper.matchesString(playerEntity.getName())
                        && playerEntity.getDistanceToEntity(living) <= d3
                        && playerEntity.swingProgressInt > 0
                        && living.hurtTime > 0) {
                        mc.thePlayer.swingItem();
                        this.timestamp2 = System.currentTimeMillis() + this.computeLong();
                        return false;
                    }
                }
            }
        }

        if (this.matchesEntityLivingBase2(living, flag)) {
            this.timestamp2 = System.currentTimeMillis() + this.computeLong();
            return true;
        }
        if (KeepSprint.instance == null || !KeepSprint.instance.checkState6()) {
            this.timestamp2 = System.currentTimeMillis() + this.computeLong();
        }

        return false;
    }

    private void onEventPacketSend(EventPacketSend event) {
        Packet packet = event.getPacket();
        if (this.flag45 && packet instanceof C08PacketPlayerBlockPlacement packet3 && packet3.getStack() != null && packet3.getStack().getItem() instanceof ItemSword) {
            event.setCancelled(true);
        } else {
            if (packet instanceof C03PacketPlayer) {
                this.flag8 = false;
                this.floatValueArray2 = null;
                this.flag21 = this.flag22 = false;
            }

            if (packet instanceof C07PacketPlayerDigging) {
                Action status = ((C07PacketPlayerDigging)packet).getStatus();
                if (status == Action.START_DESTROY_BLOCK || status == Action.STOP_DESTROY_BLOCK || status == Action.ABORT_DESTROY_BLOCK) {
                    this.flag8 = true;
                }
            }

            if (packet instanceof C02PacketUseEntity && this.flag6 && !this.checkState20()) {
                event.setCancelled(true);
            } else if (packet instanceof C02PacketUseEntity && ((C02PacketUseEntity)packet).getAction() == C02PacketUseEntity.Action.ATTACK && this.checkState23()) {
                event.setCancelled(true);
            } else if (!this.verusSetting.matchesString2("None") && !this.verusSetting.matchesString2("Fake")
                || !this.checkState37()
                || (
                        !(packet instanceof C08PacketPlayerBlockPlacement)
                            || ((C08PacketPlayerBlockPlacement)packet).getStack() == null
                            || !(((C08PacketPlayerBlockPlacement)packet).getStack().getItem() instanceof ItemSword)
                    )
                    && (!(packet instanceof C02PacketUseEntity) || ((C02PacketUseEntity)packet).getAction() == C02PacketUseEntity.Action.ATTACK)) {
                if (packet instanceof C02PacketUseEntity && ((C02PacketUseEntity)packet).getAction() == C02PacketUseEntity.Action.ATTACK && this.checkState28()) {
                    event.setCancelled(true);
                } else {
                    if (this.verusSetting.matchesString2("Fake") || this.verusSetting.matchesString2("None")) {
                        if (packet instanceof C02PacketUseEntity
                            && ((C02PacketUseEntity)packet).getAction() != C02PacketUseEntity.Action.ATTACK
                            && this.checkState51()) {
                            event.setCancelled(true);
                            return;
                        }

                        if (packet instanceof C08PacketPlayerBlockPlacement packet4) {
                            boolean flag = packet4.getStack() != null && packet4.getStack().getItem() instanceof ItemSword;
                            if (flag && this.checkState51()) {
                                event.setCancelled(true);
                                return;
                            }

                            this.flag21 = true;
                            if (flag) {
                                this.flag20 = true;
                            }
                        }

                        if (packet instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging)packet).getStatus() == Action.RELEASE_USE_ITEM) {
                            this.flag20 = false;
                            this.flag22 = true;
                        }

                        if (packet instanceof C02PacketUseEntity && ((C02PacketUseEntity)packet).getAction() == C02PacketUseEntity.Action.ATTACK && this.r()) {
                            event.setCancelled(true);
                            return;
                        }
                    }

                    if (packet instanceof C07PacketPlayerDigging packet5) {
                        Action status2 = packet5.getStatus();
                        if (status2 == Action.RELEASE_USE_ITEM && !this.flag2 && !this.checkState20()) {
                            this.flag6 = true;
                        }

                        if (!this.flag2
                            && !this.checkState20()
                            && (status2 == Action.START_DESTROY_BLOCK || status2 == Action.STOP_DESTROY_BLOCK || status2 == Action.RELEASE_USE_ITEM)) {
                            this.count10 = 1;
                        }
                    }

                    if (packet instanceof C08PacketPlayerBlockPlacement && !this.flag2) {
                        C08PacketPlayerBlockPlacement packet2 = (C08PacketPlayerBlockPlacement)packet;
                        this.count10 = 5;
                    }

                    if (packet instanceof C02PacketUseEntity && !this.flag2) {
                        this.count10 = 5;
                    }

                    if (packet instanceof C09PacketHeldItemChange) {
                        this.flag20 = false;
                        if (!this.verusSetting.matchesString2("None") && (this.flag33 || this.flag24 || this.flag42 || this.flag41)) {
                            this.flag5 = true;
                            this.count6 = 6;
                            this.count7 = 4;
                        }

                        this.flag24 = false;
                        this.flag42 = false;
                        this.flag41 = false;
                        if (this.flag33 || this.flag23) {
                            mc.thePlayer.stopUsingItem();
                            this.blocking = false;
                            this.flag23 = false;
                            this.flag33 = false;
                        }

                        if (this.manualBlockSetting.isEnabled()
                            && Mouse.isButtonDown(1)
                            && this.checkState16()
                            && (entityLivingBase2 != null || this.flag2 || this.entries5 != null && !this.entries5.isEmpty())) {
                            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                            if (mc.thePlayer.isUsingItem() && this.checkState16()) {
                                mc.thePlayer.stopUsingItem();
                                this.flag6 = true;
                            }

                            this.count7 = 4;
                        }
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    private long computeLong() {
        double d = this.minCPSSetting.computeDouble();
        double d2 = this.maxCPSSetting.computeDouble();
        if (d > d2) {
            double d3 = d;
            d = d2;
            d2 = d3;
        }

        double d4;
        if (this.dynamicSetting.matchesString2("Dynamic")) {
            this.count41++;
            if (this.count41 >= 5 + this.random.nextInt(15) || this.amount == 0.0) {
                this.amount = d + this.random.nextDouble() * (d2 - d);
                this.count41 = 0;
            }

            double d5 = (this.random.nextDouble() - 0.5) * 1.0;
            d4 = Math.max(d, Math.min(d2, this.amount + d5));
        } else {
            d4 = d + this.random.nextDouble() * (d2 - d);
        }

        d4 = Math.max(1.0, Math.min(30.0, d4));
        int n = (int)(1000.0 / d4);
        int n2 = n + (this.random.nextInt(21) - 10);
        long l = Math.max(33, Math.min(1000, n2));
        this.count19 = Math.max(1, (int)Math.round(l / 50.0));
        return l;
    }

    private void handleFlag(boolean flag) {
        this.flag49 = false;
        if (this.combatHandler != null) {
            this.combatHandler.reset();
        }

        if (this.combatHandler2 != null) {
            this.combatHandler2.reset();
        }

        boolean flag2 = this.floatValueArray5 != null;
        if (!flag) {
            this.flag25 = false;
            this.floatValueArray5 = null;
            this.factor = mc.thePlayer.rotationYaw;
        }

        if (flag2 && !flag) {
            this.process11();
            OnJumpHelper.process5();
        }

        if (this.flag30 || !this.entries.isEmpty()) {
            this.reset();
        }

        if (this.flag29) {
            this.flag29 = false;
        }

        if (!this.checkState53() && !this.flag41 && !this.flag42 && !this.flag24 && !this.flag44) {
            this.blocking = false;
            this.flag41 = false;
            this.flag42 = false;
            this.flag24 = false;
            this.flag23 = false;
            this.count30 = 0;
            this.count27 = 0;
        } else {
            this.handleFlag2(true);
        }

        this.flag30 = false;
        this.flag31 = false;
        synchronized (this.entries) {
            this.entries.clear();
        }

        this.count27 = 0;
        this.count30 = 0;
        this.count25 = 0;
        this.count32 = 0;
        this.count33 = 0;
        this.flag32 = false;
        this.flag37 = false;
        this.count31 = 0;
        this.count20 = 0;
        this.count18 = 0;
        this.count19 = 0;
        this.flag33 = false;
        this.flag35 = false;
        this.count14 = 0;
        this.count35 = 0;
        this.count26 = -1;
        this.flag26 = false;
        this.timestamp2 = 0L;
        this.flag11 = false;
        this.flag12 = false;
        this.count10 = 0;
        this.count9 = 0;
        this.count7 = 0;
        this.flag5 = false;
        this.flag4 = false;
        this.count6 = 0;
        this.flag6 = false;
        this.flag7 = false;
        this.flag2 = false;
        this.count11 = 0;
        this.flag14 = false;
    }

    public boolean matchesEntity2(Entity entity) {
        if (entity != null && mc.thePlayer != null && mc.theWorld != null) {
            Vec3 vec = WorldQueryCache.computeVec3();
            float[] floats = WorldQueryCache.computeFloatArray();
            Vec3 vec2 = OnJumpHelper.computeVec3(floats[1], floats[0]);
            Vec3 vec3 = vec.addVector(
                vec2.xCoord * this.attackRangeSetting.computeDouble(),
                vec2.yCoord * this.attackRangeSetting.computeDouble(),
                vec2.zCoord * this.attackRangeSetting.computeDouble()
            );
            MovingObjectPosition hit = entity.getEntityBoundingBox().calculateIntercept(vec, vec3);
            if (hit == null && !entity.getEntityBoundingBox().isVecInside(vec)) {
                return false;
            }
            MovingObjectPosition hit2 = mc.theWorld.rayTraceBlocks(vec, vec3, false, false, false);
            return hit2 == null || hit != null && vec.squareDistanceTo(hit.hitVec) < vec.squareDistanceTo(hit2.hitVec);
        }
        return false;
    }

    public boolean checkState12() {
        if (this.checkState44()
            && this.flag38
            // VENDOR-DISABLED: && RuntimeAuthGuard.checkState2()
            && true
            && this.checkState30()
            && this.checkState27()
            && mc.currentScreen == null
            && !this.checkState46()
            && this.count10 <= 0
            && (!this.weaponsOnlySetting.isEnabled() || this.checkState11() || BedAura.checkState())
            && !AutoGrinder.matchesModule(this)) {
            this.process36();
            this.process6();
            return this.y();
        }
        return false;
    }

    public void handleString(String str) {
        if (!this.cpS10Setting.matchesString2(str)) {
            this.cpS10Setting.handleString(str);
        }
    }

    public boolean checkState8() {
        return this.matchesLong(75L);
    }

    private boolean matchesEntityLivingBase5(EntityLivingBase living) {
        return matchesEntityLivingBase3(living, this.attackRangeSetting.computeDouble());
    }

    public static boolean matchesBlockPos(BlockPos pos) {
        KillAura killAura = instance;
        if (killAura != null && pos != null && killAura.matchesFlag2(killAura.checkState27())) {
            return true;
        }
        if (killAura != null
            && killAura.checkState44()
            && killAura.flag38
            && pos != null
            && mc.thePlayer != null
            && mc.playerController != null
            && killAura.requirePressSetting != null
            && killAura.requirePressSetting.isEnabled()
            && !y
            && !killAura.flag40
            && mc.thePlayer.isEntityAlive()
            && !AutoGrinder.matchesModule(killAura)
            && killAura.lookPacketSetting != null
            && !killAura.lookPacketSetting.matchesString2("None")
            && !killAura.checkState52()) {
            float[] floats = OnJumpHelper.checkState() && killAura.floatValueArray5 != null
                ? OnJumpHelper.floatValueArray2
                : new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
            if (floats == null) {
                return false;
            }
            float[] floatValueArray = OnJumpHelper.floatValueArray;
            AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
            if (!box.isVecInside(mc.thePlayer.getPositionEyes(1.0F))
                && OnJumpHelper.computeMovingObjectPosition(box, floats[0], floats[1], mc.playerController.getBlockReachDistance()) == null) {
                return true;
            }
            killAura.floatValueArray2 = (float[])floats.clone();
            return floatValueArray != null
                && !box.isVecInside(mc.thePlayer.getPositionEyes(1.0F))
                && OnJumpHelper.computeMovingObjectPosition(box, floatValueArray[0], floatValueArray[1], mc.playerController.getBlockReachDistance()) == null;
        }
        return false;
    }

    private void process4() {
        int n = this.computeInt2();
        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(n));
        this.flag32 = true;
        this.flag42 = false;
        this.flag41 = false;
    }

    public void process16() {
        if (this.checkState44() && mc.thePlayer != null && this.disableInGUISetting.isEnabled() && !this.autoblockInInventorySetting.isEnabled()) {
            this.process7();
        }
    }

    private boolean matchesFlag2(boolean flag) {
        if (!this.checkState44()
            || !this.flag38
            || this.requirePressSetting == null
            || !this.requirePressSetting.isEnabled()
            || !flag
            || y
            || mc.thePlayer == null
            || mc.theWorld == null
            || mc.playerController == null
            || mc.currentScreen != null
            || mc.thePlayer.isDead
            || this.targetsSetting == null
            || this.attackRangeSetting == null
            || this.entries3 == null
            || this.checkState52()
            || AutoGrinder.matchesModule(this)) {
            return false;
        }
        if (this.disableWhilePantsSwappingSetting != null && this.disableWhilePantsSwappingSetting.isEnabled() && StartHotbarMovesHelper.flag) {
            return false;
        }
        if (this.onlyWhileSneakingSetting != null && this.onlyWhileSneakingSetting.isEnabled() && !mc.thePlayer.isSneaking()) {
            return false;
        }
        for (EntityLivingBase living : this.computeList(this.attackRangeSetting.computeDouble())) {
            if (KOS.getInstance() == null || !KOS.getInstance().checkState44() || WHelper.matchesString(living.getName())) {
                return true;
            }
        }

        return false;
    }

    public static boolean checkState19() {
        KeepSprint instance2 = KeepSprint.instance;
        if (instance2 != null && instance2.checkState()) {
            return true;
        }
        KillAura killAura = instance;
        return killAura != null
            && killAura.checkState44()
            && killAura.flag38
            && !AutoGrinder.matchesModule(killAura)
            && (killAura.checkState28() || killAura.checkState23() || killAura.checkState39());
    }

    private List<EntityLivingBase> computeList(List<EntityLivingBase> livings) {
        List list = new ArrayList(livings);
        Collections.sort(list, new Comparator<EntityLivingBase>() {
            public int compare(EntityLivingBase living, EntityLivingBase living2) {
                return Double.compare(OnJumpHelper.computeFloat(living), OnJumpHelper.computeFloat(living2));
            }
        });
        return list;
    }

    private boolean matchesEntityLivingBase3(EntityLivingBase living) {
        if (living != null && !matchesEntity(living)) {
            BooleanSetting boolSetting = (BooleanSetting)this.targetsSetting.computeSetting("Players");
            BooleanSetting boolSetting2 = (BooleanSetting)this.targetsSetting.computeSetting("Animals");
            BooleanSetting boolSetting3 = (BooleanSetting)this.targetsSetting.computeSetting("Mobs");
            BooleanSetting boolSetting4 = (BooleanSetting)this.targetsSetting.computeSetting("Golem");
            BooleanSetting boolSetting5 = (BooleanSetting)this.targetsSetting.computeSetting("Silverfish");
            BooleanSetting boolSetting6 = (BooleanSetting)this.targetsSetting.computeSetting("Invisible");
            if (living instanceof EntityPlayer && boolSetting.isEnabled()) {
                return true;
            }
            if (living instanceof EntityAnimal && boolSetting2.isEnabled()) {
                return true;
            }
            if (living instanceof EntityMob && boolSetting3.isEnabled()) {
                return true;
            }
            String str = living.getClass().getSimpleName().toLowerCase();
            return boolSetting4.isEnabled() && str.contains("golem") ? true : boolSetting5.isEnabled() && str.contains("silverfish");
        }
        return false;
    }

    static {
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
    }

    private void process10() {
        int n = this.computeInt();
        if (n != -1) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(n));
            this.flag32 = true;
        }
    }

    private float computeFloat(float f, float f2) {
        return f2 + (((f - f2 + 180.0F) % 360.0F + 360.0F) % 360.0F - 180.0F);
    }

    private boolean checkState30() {
        return !GetCurrentScreenHelper.checkState2() ? false : !mc.thePlayer.isDead;
    }

    private boolean matchesEntityPlayer(EntityPlayer player, EntityPlayer player2) {
        if (player != null && player2 != null) {
            Character character = this.computeCharacter(player);
            Character character2 = this.computeCharacter(player2);
            return character != null && character2 != null ? character.toString().equalsIgnoreCase(character2.toString()) : false;
        }
        return false;
    }

    private void process13() {
        boolean flag = entityLivingBase2 != null
            && this.matchesEntityLivingBase(entityLivingBase2)
            && OnJumpHelper.computeFloat(entityLivingBase2) <= this.attackRangeSetting.computeDouble();
        CombatHandler2.NoneMode noneMode = this.combatHandler2.computeNoneMode(mc.thePlayer.ticksExisted, this.flag44, flag);
        this.flag23 = true;
        if (noneMode == CombatHandler2.NoneMode.RELEASE) {
            if (this.flag44 && this.timestamp4 > this.timestamp6) {
                NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }

            this.blocking = this.flag42 = this.flag41 = this.flag44;
            this.count30 = 1;
        } else {
            if (noneMode == CombatHandler2.NoneMode.ATTACK_BLOCK && !this.flag44 && this.timestamp4 > this.timestamp5) {
                this.matchesEntityLivingBase2(entityLivingBase2, false);
                if (this.checkState41()) {
                    this.process21();
                }

                this.count30 = 2;
            } else if (noneMode == CombatHandler2.NoneMode.REBLOCK && this.checkState41()) {
                this.process21();
                this.count30 = 2;
            }
        }
    }

    private boolean matchesFlag(boolean flag, int n, int n2) {
        int n3 = n - n2;
        return flag && n3 >= 0 && n3 <= 2;
    }

    private void process26() {
        if (this.flag45) {
            if (!this.flag44) {
                this.flag45 = false;
            } else if (mc.thePlayer != null && mc.getNetHandler() != null && this.timestamp4 > this.timestamp6 && this.timestamp3 != this.timestamp4) {
                this.timestamp3 = this.timestamp4;
                if (this.checkState16() && mc.thePlayer.isUsingItem()) {
                    mc.thePlayer.stopUsingItem();
                }

                NetworkHelper.handlePacket2(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                this.blocking = this.flag42 = this.flag41 = this.flag44;
            }
        }
    }

    public double computeDouble2() {
        return this.attackRangeSetting.computeDouble();
    }

    private boolean checkState38() {
        return this.verusSetting != null
            && (
                this.verusSetting.matchesString2("Watchdog")
                        && this.cpS10Setting != null
                        && (this.cpS10Setting.matchesString2("10-CPS") || this.cpS10Setting.matchesString2("8-CPS"))
                    || this.verusSetting.matchesString2("Grim") && this.cpS10Setting2 != null && this.cpS10Setting2.matchesString2("10-CPS")
            );
    }

    public static boolean checkState32() {
        KillAura killAura = instance;
        return killAura != null && killAura.checkState44() && killAura.flag38 && !AutoGrinder.matchesModule(killAura) && killAura.checkState23();
    }

    public static boolean checkState48() {
        return instance != null && instance.flag25;
    }

    private boolean checkState56() {
        if (!this.checkState27()) {
            return false;
        }
        if (this.weaponsOnlySetting.isEnabled() && !this.checkState11()) {
            return false;
        }
        if (!this.disableWhileMiningSetting.isEnabled() || !this.checkState40() && !mc.playerController.isHittingBlock) {
            if (this.disableInGUISetting.isEnabled() && mc.currentScreen != null) {
                return false;
            }
            return this.disableWhilePantsSwappingSetting.isEnabled() && StartHotbarMovesHelper.flag
                ? false
                : !this.onlyWhileSneakingSetting.isEnabled() || mc.thePlayer.isSneaking();
        }
        return false;
    }

    public String computeString() {
        return this.cpS10Setting.getText();
    }

    private boolean checkState52() {
        if (Autoblockin.computeFloatArray() != null) {
            return true;
        }
        if (OnMouseHandler.computeFloatArray() != null) {
            return true;
        }
        if (Scaffold.computeFloatArray() != null) {
            return true;
        }
        if (BedAura.computeFloatArray() != null) {
            return true;
        }
        if (NoSlowDown.computeFloatArray() != null) {
            return true;
        }
        if (Cake.instance != null && Cake.instance.checkState44() && Cake.instance.checkState()) {
            return true;
        }
        if (DragonEgg.instance != null && DragonEgg.instance.checkState44() && DragonEgg.instance.checkState4()) {
            return true;
        }
        if (CarePackage.instance != null && CarePackage.instance.checkState44() && CarePackage.instance.checkState2()) {
            return true;
        }
        if (ChestAura.instance != null && ChestAura.instance.checkState44() && ChestAura.instance.checkState2()) {
            return true;
        }
        BedAura instance = BedAura.getInstance();
        return instance != null && instance.checkState44() && (instance.rotating || instance.bedPos != null);
    }

    public boolean checkState2() {
        if (this.checkState44() && GetCurrentScreenHelper.checkState2()) {
            EntityLivingBase living = entityLivingBase2;
            if (living == null && this.entries5 != null && !this.entries5.isEmpty()) {
                living = this.entries5.get(0);
            }

            return living != null && !living.isDead ? OnJumpHelper.computeFloat(living) <= this.swingRangeSetting.computeDouble() : false;
        }
        return false;
    }

    private EntityLivingBase computeEntityLivingBase(List<EntityLivingBase> livings, double d) {
        if (livings.isEmpty()) {
            return null;
        }
        EntityLivingBase living = null;
        double MAX_VALUE = Double.MAX_VALUE;

        for (EntityLivingBase living2 : livings) {
            double d2 = OnJumpHelper.computeFloat(living2);
            if (d2 <= d && d2 < MAX_VALUE) {
                living = living2;
                MAX_VALUE = d2;
            }
        }

        return living;
    }

    public void onMouse(BasicEvent3 basicEvent3) {
        if (basicEvent3.count6 >= 0 && !basicEvent3.flag) {
            if (this.requirePressSetting != null
                && this.requirePressSetting.isEnabled()
                && !y
                && mc.gameSettings != null
                && mc.gameSettings.keyBindAttack.getKeyCode() == basicEvent3.count6 - 100) {
                this.flag40 = false;
                this.process40();
            }
        } else if (basicEvent3.count6 >= 0 && this.flag38) {
            if (basicEvent3.count6 != 1 || !this.checkState28()) {
                if (basicEvent3.count6 == 1 && this.checkState37()) {
                    basicEvent3.cancel();
                } else {
                    if (basicEvent3.count6 == 0 || basicEvent3.count6 == 1) {
                        if (!this.checkState11() && this.weaponsOnlySetting.isEnabled()) {
                            return;
                        }

                        if (entityLivingBase2 == null && this.entries5.isEmpty()) {
                            return;
                        }

                        if (!this.checkState56()) {
                            return;
                        }

                        if (this.count10 > 0) {
                            return;
                        }

                        if (this.flag11) {
                            return;
                        }

                        if (!this.flag2) {
                            return;
                        }

                        if (this.manualBlockSetting.isEnabled() && basicEvent3.count6 == 1 && entityLivingBase2 == null && this.entries5.isEmpty()) {
                            return;
                        }

                        basicEvent3.cancel();
                    }
                }
            }
        }
    }

    private double computeDouble() {
        return Math.max(
            Math.max(this.attackRangeSetting.computeDouble(), this.swingRangeSetting.computeDouble()),
            Math.max(this.rotationRangeSetting.computeDouble(), this.blockRangeSetting.computeDouble())
        );
    }

    public void process22() {
        float f = OnJumpHelper.floatValueArray[0];
        float f2 = this.computeFloat(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw), f);
        mc.thePlayer.rotationYaw = f2;
        mc.thePlayer.prevRotationYaw = f2;
    }

    private void handleFlag(boolean flag, boolean flag2, boolean flag3, boolean flag4) {
        if (entityLivingBase2 != null) {
            boolean flag5 = false;
            float[] floats = OnJumpHelper.checkState() ? OnJumpHelper.floatValueArray2 : new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
            boolean flag6 = false;
            if (this.raycastSetting.isEnabled()) {
                float collisionBorderSize = entityLivingBase2.getCollisionBorderSize();
                AxisAlignedBB box = entityLivingBase2.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                MovingObjectPosition hit = OnJumpHelper.computeMovingObjectPosition(box, floats[0], floats[1], this.attackRangeSetting.computeDouble());
                flag6 = hit != null;
            } else {
                flag6 = true;
            }

            if (flag && flag6 && OnJumpHelper.checkState()) {
                if (this.raycastSetting.isEnabled() && (!this.throughWallsSetting.isEnabled() || this.checkState6())) {
                    MovingObjectPosition hit2 = OnJumpHelper.rayTrace(this.attackRangeSetting.computeDouble(), 1.0F);
                    if (hit2 != null
                        && hit2.typeOfHit == MovingObjectType.ENTITY
                        && hit2.entityHit == entityLivingBase2
                        && (!this.checkState6() || this.matchesEntityLivingBase5(entityLivingBase2))) {
                        Vec3 hitVec = hit2.hitVec;
                        Vec3 vec = new Vec3(
                            hitVec.xCoord - entityLivingBase2.posX, hitVec.yCoord - entityLivingBase2.posY, hitVec.zCoord - entityLivingBase2.posZ
                        );
                        if (!flag3) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entityLivingBase2, vec));
                        } else {
                            NetworkHelper.handlePacket2(new C02PacketUseEntity(entityLivingBase2, vec));
                        }

                        flag5 = true;
                    }
                } else {
                    Vec3 vec2 = new Vec3(0.5, 0.5, 0.5);
                    if (!flag3) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entityLivingBase2, vec2));
                    } else {
                        NetworkHelper.handlePacket2(new C02PacketUseEntity(entityLivingBase2, vec2));
                    }

                    flag5 = true;
                }
            }

            if (!flag4 || flag5) {
                if (flag2) {
                    if (!flag3) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entityLivingBase2, C02PacketUseEntity.Action.INTERACT));
                    } else {
                        NetworkHelper.handlePacket2(new C02PacketUseEntity(entityLivingBase2, C02PacketUseEntity.Action.INTERACT));
                    }
                }
            }
        }
    }

    private List<EntityLivingBase> computeList2(List<EntityLivingBase> livings, int n) {
        if (livings.isEmpty()) {
            this.entityLivingBase = null;
            this.count24 = 0;
            this.count20 = 0;
            return Collections.emptyList();
        }
        var livings2 = this.computeList(livings);
        List list = livings2.size() <= n ? livings2 : livings2.subList(0, n);
        int n2 = Math.max(1, (int)(this.switchDelaySetting.computeDouble() / 50.0));
        boolean flag = this.count20 >= n2;
        boolean flag2 = this.entityLivingBase != null && !this.entityLivingBase.isDead && list.contains(this.entityLivingBase);
        if (flag2 && !flag) {
            List list2 = new ArrayList();
            list2.add(this.entityLivingBase);
            return list2;
        }
        if (!flag2 || flag) {
            this.count20 = 0;
            if (!list.isEmpty()) {
                if (this.count24 >= list.size()) {
                    this.count24 = 0;
                }

                this.entityLivingBase = (EntityLivingBase)list.get(this.count24);
                this.count24 = (this.count24 + 1) % list.size();
            } else {
                this.entityLivingBase = null;
                this.count24 = 0;
            }
        }

        List list3 = new ArrayList();
        if (this.entityLivingBase != null && list.contains(this.entityLivingBase)) {
            list3.add(this.entityLivingBase);
        }

        return list3;
    }

    public void process38() {
        if (!this.flag38 && this.checkState44()) {
            this.onEnable();
        }
    }

    private boolean matchesEntityPlayer4(EntityPlayer player) {
        boolean flag;
        String str;
        if (Squads.matchesEntityPlayer2(player)) {
            flag = Squads.matchesEntityPlayer(player);
            str = "filter:squads";
        } else if (this.matchesEntityPlayer(mc.thePlayer, player)) {
            flag = true;
            str = "filter:name-color";
        } else {
            flag = GetCurrentScreenHelper.matchesEntity2(player);
            str = "filter:vanilla-team";
        }

        TDM.handleEntityPlayer(player, str, true, flag);
        return flag;
    }

    public static float[] computeFloatArray(Entity entity, float f) {
        KillAura killAura = instance;
        return entity == mc.thePlayer
                && killAura != null
                && (killAura.flag25 || killAura.checkState44() && killAura.flag38)
                && killAura.lookPacketSetting != null
                && !killAura.lookPacketSetting.matchesString2("None")
                && killAura.floatValueArray5 != null
                && OnJumpHelper.checkState()
                && (entityLivingBase2 != null || killAura.flag25 || killAura.entries5 != null && !killAura.entries5.isEmpty())
                && (!killAura.lookPacketSetting.matchesString2("Silent") || killAura.hideClientRotationsSetting == null || !killAura.hideClientRotationsSetting.isEnabled())
            ? GetRotationYawHeadHelper.computeFloatArray(entity, f)
            : null;
    }
}
