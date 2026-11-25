package org.brutality.module.impl.pit.hypixel.automation;

import java.awt.Color;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.brutality.Brutality;
import org.brutality.automation.grinding.movement.ObsidianPlacerHandler;
import org.brutality.automation.grinding.movement.MovementHandler;
import org.brutality.automation.grinding.movement.MovementHandler2;
import org.brutality.automation.grinding.purchases.HasItemHandler;
import org.brutality.automation.grinding.targeting.OverdriveType;
import org.brutality.automation.grinding.targeting.ReleaseHandler;
import org.brutality.automation.grinding.targeting.SelectRankedHandler;
import org.brutality.automation.hunting.HuntingHandler;
import org.brutality.automation.mining.FenceHandler;
import org.brutality.automation.mining.MiningHandler2;
import org.brutality.automation.navigation.ClearPathHandler;
import org.brutality.core.ConnectToServerHandler;
import org.brutality.core.ExtraAttributesHandler;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.attack.AttackEvent;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.client.AutoDisable;
import org.brutality.module.impl.combat.AutoClicker;
import org.brutality.module.impl.combat.Displace;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.movement.KeepSprint;
import org.brutality.module.impl.pit.hypixel.utility.JumpBoostDetector;
import org.brutality.module.impl.player.Friends;
import org.brutality.module.impl.player.KOS;
import org.brutality.module.impl.player.Truces;
import org.brutality.module.impl.visual.Focus;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;
// VENDOR-DISABLED: import org.brutality.system.EntitlementState;
import org.brutality.util.ExecuteCommandHelper;
import org.brutality.util.GetDisplayedTitleHelper;
import org.brutality.util.GetOrderedTeamsHelper;
import org.brutality.util.PizzaType;
import org.brutality.util.KeybindStateUtil;
import org.brutality.util.UtilHelper6;
import org.brutality.util.StopwatchTimer;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;
import org.brutality.util.inventory.GoldenHeadsType;
import org.brutality.util.math.BuildTargetComparatorHelper;
import org.brutality.util.math.MathHelper;
import org.brutality.util.render.RenderHelper2;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.TruceManager;
import org.brutality.utils.WHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.hypixel.pit.PruneExpiredCombatHelper;
import org.brutality.utils.hypixel.pit.StripColorHelper;
import org.brutality.utils.player.PlayerHelper13;
import org.brutality.utils.player.SomberHelper;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class AutoHunt extends Module {
    private static AutoHunt instance;
    private final PitCategory pitCategory = PitCategory.PREMIUM;
    private final ModeSetting warSetting = new ModeSetting("Hunt Mode", this, "Target", Arrays.asList("Target", "Bhunt", "War"));
    private final StringSetting targetSetting = new StringSetting("Target", this, "").computeStringSetting(() -> this.warSetting.matchesString2("Target"));
    private final BooleanSetting fightEnemiesSetting = new BooleanSetting("Fight Enemies", this, true)
        .computeBooleanSetting(() -> this.warSetting.matchesString2("Target"));
    private final NumberSetting fightEnemyDistanceSetting = new NumberSetting("Fight Enemy Distance", this, 7.0, 1.0, 30.0, 1.0)
        .computeNumberSetting(() -> this.warSetting.matchesString2("Target") && this.fightEnemiesSetting.isEnabled());
    private final BooleanSetting darksSetting = new BooleanSetting("Darks", this, true);
    private final BooleanSetting shopsSetting = new BooleanSetting("Shops", this, true);
    private final KeybindListSetting targetModesSetting = new KeybindListSetting("Target Modes", this, this.darksSetting, this.shopsSetting);
    private final ModeSetting smartSetting = new ModeSetting("War Sorting", this, "Smart", Arrays.asList("Distance", "Smart"))
        .computeModeSetting(() -> this.warSetting.matchesString2("War"));
    private final ModeSetting ironPackSetting = new ModeSetting("Kit", this, "Iron Pack", Arrays.asList("Iron Pack"))
        .computeModeSetting(() -> this.warSetting.matchesString2("War"));
    private final BooleanSetting targetJumpBoostSetting = new BooleanSetting("Target Jump Boost", this, false)
        .computeBooleanSetting(() -> this.warSetting.matchesString2("War"));
    private final BooleanSetting antiLauncherSetting = new BooleanSetting("AntiLauncher", this, false);
    private final BooleanSetting displaceSetting = new BooleanSetting("Displace", this, false);
    private final BooleanSetting displaceIfTurnedAroundSetting = new BooleanSetting("Displace If Turned Around", this, true).computeBooleanSetting(this::z);
    private final BooleanSetting displaceIfNotLookingSetting = new BooleanSetting("Displace If Not Looking", this, false)
        .computeBooleanSetting(() -> this.displaceSetting.isEnabled());
    private final BooleanSetting autoblockSetting = new BooleanSetting("Autoblock", this, false).computeBooleanSetting(this::checkState14);
    private final NumberSetting autoblockHealthHeartsSetting = new NumberSetting("Autoblock Health (Hearts)", this, 6.0, 0.5, 20.0, 0.5)
        .computeNumberSetting(() -> this.checkState14() && this.autoblockSetting.isEnabled());
    private ReleaseHandler releaseHandler;
    private final BooleanSetting pathESPSetting = new BooleanSetting("Path ESP", this, true);
    private final BooleanSetting destinationESPSetting = new BooleanSetting("Destination ESP", this, true);
    private boolean flag;
    private boolean flag2;
    private boolean flag3;
    private boolean flag4;
    private boolean flag5;
    private boolean flag6;
    private boolean flag7;
    private final Set<String> itemSet = new HashSet<>();
    private final BooleanSetting megastreaksOnlySetting = new BooleanSetting("Megastreaks Only", this, false)
        .computeBooleanSetting(() -> this.warSetting.matchesString2("Bhunt"));
    private final ModeSetting overdriveSetting = this.computeModeSetting("1st Priority", "Overdrive");
    private final ModeSetting highlanderSetting = this.computeModeSetting("2nd Priority", "Highlander");
    private final ModeSetting beastmodeSetting = this.computeModeSetting("3rd Priority", "Beastmode");
    private final ModeSetting hermitSetting = this.computeModeSetting("4th Priority", "Hermit");
    private final ModeSetting toTheMoonSetting = this.computeModeSetting("5th Priority", "To the Moon");
    private final ModeSetting[] modeSettingArray = new ModeSetting[]{
        this.overdriveSetting, this.highlanderSetting, this.beastmodeSetting, this.hermitSetting, this.toTheMoonSetting
    };
    private final NumberSetting bountyThresholdSetting = new NumberSetting("Bounty Threshold", this, 1000.0, 1.0, 5000.0, 100.0)
        .computeNumberSetting(() -> this.warSetting.matchesString2("Bhunt") && !this.megastreaksOnlySetting.isEnabled());
    private final ModeSetting lowestArmorSetting = new ModeSetting("Bounty Filter", this, "Lowest Health", Arrays.asList("Lowest Health", "Lowest Armor"))
        .computeModeSetting(() -> this.warSetting.matchesString2("Bhunt"));
    private final BooleanSetting chaseOutOfPITSetting = new BooleanSetting("Chase Out Of PIT", this, false)
        .computeBooleanSetting(() -> this.warSetting.matchesString2("Bhunt"));
    private final BooleanSetting useFocusKOSSetting = new BooleanSetting("Use Focus + KOS", this, false);
    private final NumberSetting fightRangeSetting = new NumberSetting("Fight Range", this, 8.0, 1.0, 30.0, 1.0);
    private final NumberSetting swingRangeSetting = new NumberSetting("Swing Range", this, 5.0, 1.0, 10.0, 1.0);
    private final ModeSetting killAuraSetting = new ModeSetting("Combat Mode", this, "AutoClicker", Arrays.asList("AutoClicker", "KillAura"));
    private final BooleanSetting spawnSetting = new BooleanSetting("Spawn", this, true);
    private final BooleanSetting dropDownSetting = new BooleanSetting("Drop Down", this, true);
    private final ModeSetting targetLocationSetting = new ModeSetting("Drop Location", this, "PIT", Arrays.asList("PIT", "Target Location"))
        .computeModeSetting(() -> this.dropDownSetting.isEnabled());
    private final ModeSetting alwaysSetting = new ModeSetting("Jump Pot", this, "Only When Dropping", Arrays.asList("Off", "Only When Dropping", "Always"));
    private final NumberSetting yawSpeedSetting = new NumberSetting("Yaw Speed", this, 40.0, 1.0, 100.0, 1.0);
    private final NumberSetting pitchSpeedSetting = new NumberSetting("Pitch Speed", this, 20.0, 1.0, 100.0, 1.0);
    private final BooleanSetting smoothCameraSetting = new BooleanSetting("Smooth Camera", this, true);
    private final NumberSetting minJumpDistanceSetting = new NumberSetting("Min Jump Distance", this, 5.0, 1.0, 100.0, 1.0);
    private final NumberSetting jumpChancePerTickSetting = new NumberSetting("Jump Chance Per Tick", this, 75.0, 0.0, 100.0, 1.0);
    private final NumberSetting strafeRangeSetting = new NumberSetting("Strafe Range", this, 9.0, 1.0, 100.0, 1.0);
    private final NumberSetting strafeChanceSetting = new NumberSetting("Strafe Chance", this, 70.0, 0.0, 100.0, 1.0);
    private final NumberSetting perkClickDelaySetting = new NumberSetting("Perk Click Delay", this, 250.0, 100.0, 1000.0, 25.0);
    private final BooleanSetting donTKeepSprintInPITSetting = new BooleanSetting("Don't KeepSprint In PIT", this, false);
    private final BooleanSetting keepSprintWhenUnwatchedSetting = new BooleanSetting("KeepSprint When Unwatched", this, false);
    private boolean flag8;
    private boolean flag9;
    private final BooleanSetting kungFuSetting = new BooleanSetting("Kung Fu", this, false);
    private final BooleanSetting espSetting = new BooleanSetting("ESP", this, true);
    private final BooleanSetting hudSetting = new BooleanSetting("HUD", this, true);
    private final NumberSetting hudxSetting = new NumberSetting("HUD X", this, 5.0, 0.0, 1920.0, 1.0);
    private final NumberSetting hudySetting = new NumberSetting("HUD Y", this, 5.0, 0.0, 1080.0, 1.0);
    private final KeybindListSetting majorEventsSetting;
    private final KeybindListSetting targetingSetting;
    private final KeybindListSetting combatSetting;
    private final KeybindListSetting movementSetting;
    private final KeybindListSetting automationSetting;
    private final KeybindListSetting perksSetting;
    private final KeybindListSetting hudAndVisualsSetting;
    private final BooleanSetting ragePitSetting = new BooleanSetting("Rage Pit", this, true);
    private final BooleanSetting tdmSetting = new BooleanSetting("TDM", this, true);
    private final BooleanSetting beastSetting = new BooleanSetting("Beast", this, true);
    private final BooleanSetting robberySetting = new BooleanSetting("Robbery", this, true);
    private final BooleanSetting spireSetting = new BooleanSetting("Spire", this, true);
    private final BooleanSetting raffleSetting = new BooleanSetting("Raffle", this, true);
    private final BooleanSetting blockheadSetting = new BooleanSetting("Blockhead", this, true);
    private final BooleanSetting squadsSetting = new BooleanSetting("Squads", this, true);
    private final BooleanSetting kothSetting = new BooleanSetting("KOTH", this, true);
    private final ModeSetting outskirtsSetting = new ModeSetting("After Kill", this, "Continue", Arrays.asList("Continue", "Outskirts"));
    private final ModeSetting afkSpawnSetting = new ModeSetting("After Combat", this, "Off", Arrays.asList("Off", "Spawn", "Lobby", "AFK Spawn"));
    private final BooleanSetting lobbySwapSetting = new BooleanSetting("Lobby Swap", this, false);
    private final BooleanSetting autoBuySetting = new BooleanSetting("Auto Buy", this, false);
    private final ModeSetting everyMinsSetting = new ModeSetting("Auto Buy Mode", this, "Every Death", Arrays.asList("Every Death", "Every Mins"))
        .computeModeSetting(() -> this.autoBuySetting.isEnabled());
    private final NumberSetting autoBuyEveryMinsSetting = new NumberSetting("Auto Buy Every Mins", this, 15.0, 1.0, 120.0, 1.0)
        .computeNumberSetting(() -> this.autoBuySetting.isEnabled() && this.everyMinsSetting.matchesString2("Every Mins"));
    private final KeybindListSetting obsidian64Setting = new KeybindListSetting(
        "What To Auto Buy",
        this,
        new BooleanSetting("Diamond Sword", this, false),
        new BooleanSetting("Diamond Chestplate", this, true),
        new BooleanSetting("Diamond Leggings", this, true),
        new BooleanSetting("Diamond Boots", this, true),
        new BooleanSetting("First-Aid Egg", this, false),
        new BooleanSetting("Combat Spade", this, false),
        new BooleanSetting("Iron Pack", this, false),
        new BooleanSetting("Obsidian (8)", this, false),
        new BooleanSetting("Obsidian (64)", this, false)
    );
    private final BooleanSetting autoPerkSetting = new BooleanSetting("Auto Perk", this, false);
    private final NumberSetting autoPerkFrequencySetting = new NumberSetting("Auto Perk Frequency", this, 15.0, 1.0, 120.0, 1.0)
        .computeNumberSetting(() -> this.autoPerkSetting.isEnabled());
    private final BooleanSetting autoBountyHunterPerkSetting = new BooleanSetting("Auto Bounty Hunter Perk", this, true)
        .computeBooleanSetting(() -> this.warSetting.matchesString2("Bhunt"));
    private final NumberSetting bountyHunterSlotSetting = new NumberSetting("Bounty Hunter Slot", this, 3.0, 1.0, 4.0, 1.0)
        .computeNumberSetting(() -> this.warSetting.matchesString2("Bhunt") && this.autoBountyHunterPerkSetting.isEnabled());
    private static final List<String> entries = Arrays.stream(GoldenHeadsType.values())
        .filter(GoldenHeadsType::isSelectable)
        .map(Enum::name)
        .collect(Collectors.toList());
    private final ModeSetting goldenheadsSetting = new ModeSetting("Perk 1", this, "GOLDEN_HEADS", entries)
        .computeModeSetting(() -> this.autoPerkSetting.isEnabled() || this.warSetting.matchesString2("Bhunt"));
    private final ModeSetting strengthchainingSetting = new ModeSetting("Perk 2", this, "STRENGTH_CHAINING", entries)
        .computeModeSetting(() -> this.autoPerkSetting.isEnabled() || this.warSetting.matchesString2("Bhunt"));
    private final ModeSetting safetyfirstSetting = new ModeSetting("Perk 3", this, "SAFETY_FIRST", entries)
        .computeModeSetting(() -> this.autoPerkSetting.isEnabled() || this.warSetting.matchesString2("Bhunt"));
    private final ModeSetting gladiatorSetting = new ModeSetting("Perk 4", this, "GLADIATOR", entries)
        .computeModeSetting(() -> this.autoPerkSetting.isEnabled() || this.warSetting.matchesString2("Bhunt"));
    private final BooleanSetting mineObsidianWhenStuckSetting = new BooleanSetting("Mine Obsidian When Stuck", this, false);
    private EntityPlayer entityPlayer;
    private boolean flag10;
    private double amount = -1.0;
    private final StopwatchTimer utilHelper8 = new StopwatchTimer();
    private final StopwatchTimer utilHelper82 = new StopwatchTimer();
    private final StopwatchTimer utilHelper83 = new StopwatchTimer();
    private final StopwatchTimer utilHelper84 = new StopwatchTimer();
    private final StopwatchTimer utilHelper85 = new StopwatchTimer();
    private BlockPos blockPos;
    private MovementHandler.MidMode midMode;
    private final ClearPathHandler clearPathHandler = new ClearPathHandler(8, 0, 0, 3000, 1.3, true);
    private final ClearPathHandler clearPathHandler2 = new ClearPathHandler();
    private final RenderHelper2 renderHelper2 = new RenderHelper2(0.9F, 0.4F, 0.35F, 1.0F);
    private int count;
    private double amount2;
    private String text;
    private long timestamp;
    private boolean flag11;
    private boolean flag12;
    private boolean flag13;
    private final Map<UUID, String> lookupMap = new HashMap<>();
    private final Random random = new Random();
    private BlockPos blockPos2;
    private final StopwatchTimer utilHelper86 = new StopwatchTimer();
    private final NumberSetting autoBuyClickDelaySetting = new NumberSetting("Auto Buy Click Delay", this, 500.0, 0.0, 5000.0, 50.0)
        .computeNumberSetting(() -> this.autoBuySetting.isEnabled());
    private final HasItemHandler hasItemHandler = new HasItemHandler(
        this.autoBuySetting,
        this.everyMinsSetting,
        this.autoBuyEveryMinsSetting,
        this.obsidian64Setting,
        this.autoBuyClickDelaySetting,
        this.yawSpeedSetting,
        this.pitchSpeedSetting
    );
    private final HasItemHandler hasItemHandler2 = new HasItemHandler(
        this.autoBuySetting,
        this.everyMinsSetting,
        this.autoBuyEveryMinsSetting,
        new KeybindListSetting("War Kit Items", this, new BooleanSetting("Iron Pack", this, true)),
        this.autoBuyClickDelaySetting,
        this.yawSpeedSetting,
        this.pitchSpeedSetting
    );
    private boolean flag14;
    private final StopwatchTimer utilHelper87 = new StopwatchTimer(0L);
    private boolean flag15;
    private boolean flag16;
    private final StopwatchTimer utilHelper88 = new StopwatchTimer();
    private boolean flag17;
    private String text2;
    private boolean flag18;
    private final StopwatchTimer utilHelper89 = new StopwatchTimer();
    private String[] stringArray;
    private boolean flag19;
    private long timestamp2 = 12000L;
    private final StopwatchTimer utilHelper810 = new StopwatchTimer();
    private String text3;
    private boolean flag20;
    private boolean flag21;
    private boolean flag22;
    private boolean flag23;
    private EntityLivingBase entityLivingBase;
    private boolean flag24;
    private boolean flag25;
    private boolean flag26;
    private boolean flag27;
    private boolean flag28;
    private Vec3 vec3;
    private BlockPos blockPos3;
    private int count2 = -1;
    private int count3 = -1;
    private static final Random random2 = new Random();
    private static ObsidianPlacerHandler.LeftMode leftMode = ObsidianPlacerHandler.LeftMode.getInstance();
    private static final StopwatchTimer utilHelper811 = new StopwatchTimer();
    private static int count4 = 2500;
    private static final StopwatchTimer utilHelper812 = new StopwatchTimer();

    private void handleFlag(boolean flag) {
        Module module = Brutality.moduleManager.computeModule("AntiLauncher");
        if (module != null) {
            if (!this.flag3) {
                this.flag4 = module.checkState44();
                this.flag3 = true;
            }

            handleModule(module, flag);
        }
    }

    private boolean checkState9() {
        return !this.utilHelper84.matchesLong(400L);
    }

    private boolean checkState2() {
        return mc.thePlayer.isPotionActive(8);
    }

    private void refreshBountyTarget() {
        EntityPlayer entityPlayer = this.entityPlayer;
        if (entityPlayer != null && (entityPlayer.isDead || entityPlayer.getHealth() <= 0.0F)) {
            this.flag22 = this.killAuraSetting.matchesString2("KillAura");
            this.handleUUID(entityPlayer.getUniqueID());
        }

        Comparator<EntityPlayer> comparator = this.lowestArmorSetting.matchesString2("Lowest Armor")
            ? Comparator.comparingInt(EntityPlayer::getTotalArmorValue).thenComparingDouble((EntityPlayer player) -> player.getHealth() + player.getAbsorptionAmount())
            : Comparator.comparingDouble((EntityPlayer player2) -> player2.getHealth() + player2.getAbsorptionAmount());
        comparator = comparator.thenComparingDouble(player3 -> mc.thePlayer.getDistanceSqToEntity(player3));
        EntityPlayer player4 = this.megastreaksOnlySetting.isEnabled()
            ? SelectRankedHandler.selectRankedOrdered(
                mc.theWorld.playerEntities,
                player5 -> {
                    if (player5 == null
                        || player5 == mc.thePlayer
                        || player5.isDead
                        || player5.getHealth() <= 0.0F
                        || player5.isSpectator()
                        || GetDisplayedTitleHelper.matchesEntityLivingBase3(player5)) {
                        return false;
                    }
                    if (mc.getNetHandler() == null || mc.getNetHandler().getPlayerInfo(player5.getUniqueID()) == null) {
                        return false;
                    }
                    if (Friends.getInstance() != null && Friends.getInstance().checkState44() && DefaultHelper.matchesString(player5.getName())) {
                        return false;
                    }
                    if (Truces.getInstance() != null && Truces.getInstance().checkState44() && TruceManager.matchesString(player5.getName())) {
                        return false;
                    }
                    return this.megastreaksOnlySetting.isEnabled()
                        ? OverdriveType.computeOverdriveType(player5.getDisplayName().getUnformattedText(), player5.getName()) != null
                        : PruneExpiredCombatHelper.computeDouble(player5) >= this.bountyThresholdSetting.computeDouble();
                },
                GetDisplayedTitleHelper::matchesEntityPlayer,
                comparator,
                this.chaseOutOfPITSetting.isEnabled(),
                player6 -> {
                    OverdriveType overdriveType = OverdriveType.computeOverdriveType(player6.getDisplayName().getUnformattedText(), player6.getName());
                    if (overdriveType == null) {
                        return Integer.MAX_VALUE;
                    }
                    for (int i = 0; i < this.modeSettingArray.length; i++) {
                        if (this.modeSettingArray[i].matchesString2(overdriveType.getLabel())) {
                            return i;
                        }
                    }

                    return this.modeSettingArray.length;
                }
            )
            : SelectRankedHandler.computeObject(
                mc.theWorld.playerEntities,
                player7 -> {
                    if (player7 == null
                        || player7 == mc.thePlayer
                        || player7.isDead
                        || player7.getHealth() <= 0.0F
                        || player7.isSpectator()
                        || GetDisplayedTitleHelper.matchesEntityLivingBase3(player7)) {
                        return false;
                    }
                    if (mc.getNetHandler() == null || mc.getNetHandler().getPlayerInfo(player7.getUniqueID()) == null) {
                        return false;
                    }
                    if (Friends.getInstance() != null && Friends.getInstance().checkState44() && DefaultHelper.matchesString(player7.getName())) {
                        return false;
                    }
                    if (Truces.getInstance() != null && Truces.getInstance().checkState44() && TruceManager.matchesString(player7.getName())) {
                        return false;
                    }
                    return this.megastreaksOnlySetting.isEnabled()
                        ? OverdriveType.computeOverdriveType(player7.getDisplayName().getUnformattedText(), player7.getName()) != null
                        : PruneExpiredCombatHelper.computeDouble(player7) >= this.bountyThresholdSetting.computeDouble();
                },
                GetDisplayedTitleHelper::matchesEntityPlayer,
                comparator,
                this.chaseOutOfPITSetting.isEnabled()
            );
        if (player4 != this.entityPlayer) {
            if (player4 == null) {
                this.process5();
            } else if ("KillAura".equalsIgnoreCase(this.text3)) {
                KillAura.entityLivingBase3 = player4;
                KillAura.flag50 = true;
            }

            this.entityPlayer = player4;
            this.amount = -1.0;
            this.clearPathHandler.process2();
            this.clearPathHandler2.process2();
            this.flag12 = false;
        }

        this.flag10 = player4 != null;
    }

    private int computeInt2() {
        for (int i = 0; i < 9; i++) {
            if (GetMysticEnchantLoreHelper.matchesItemStack7(mc.thePlayer.inventory.getStackInSlot(i))) {
                return i;
            }
        }

        return -1;
    }

    private void process6() {
        if (this.flag26) {
            WHelper.handleString2(null);
            Focus instance = Focus.getInstance();
            KOS instance2 = KOS.getInstance();
            if (instance != null) {
                instance.setText(null);
                instance.handleFlag3(this.flag27);
            }

            if (instance2 != null) {
                instance2.handleFlag3(this.flag28);
            }

            this.flag26 = false;
        }
    }

    private void process4() {
        this.itemSet.clear();

        for (String str : WHelper.computeMap().keySet()) {
            this.itemSet.add(str.toLowerCase(Locale.ROOT));
        }

        JumpBoostDetector instance = JumpBoostDetector.getInstance();
        if (instance != null) {
            if (!this.flag5) {
                this.flag6 = instance.checkState44();
                this.flag7 = instance.checkState();
                this.flag5 = true;
            }

            handleModule(instance, true);
            instance.handleFlag(true);
        }

        EntityPlayer player = this.selectWarTarget();
        if (player != this.entityPlayer) {
            this.process5();
            this.entityPlayer = player;
            this.amount = -1.0;
            this.clearPathHandler.process2();
            this.clearPathHandler2.process2();
            this.flag12 = false;
        }

        this.flag10 = player != null;
    }

    private double computeDouble(EntityPlayer player) {
        double dist = mc.thePlayer.getDistanceToEntity(player);
        if (this.smartSetting.matchesString2("Distance")) {
            return dist;
        }
        double d = dist <= 24.0 && !BuildTargetComparatorHelper.matchesEntityPlayer(mc.thePlayer, player) ? 12.0 : 0.0;
        return dist
            + d
            + Math.abs(player.posY - mc.thePlayer.posY) * 0.75
            + (player.getHealth() + player.getAbsorptionAmount()) * 0.35
            + player.getTotalArmorValue() * 0.5;
    }

    private void process24() {
        if (this.flag && Brutality.moduleManager != null) {
            handleModule(Brutality.moduleManager.computeModule("Displace"), this.flag2);
        }

        this.flag = false;
    }

    private void process23() {
        if (mc != null && mc.gameSettings != null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
        }

        if (mc.thePlayer != null && this.count3 >= 0) {
            mc.thePlayer.inventory.currentItem = this.count3;
        }

        this.blockPos3 = null;
        this.count2 = -1;
        this.count3 = -1;
    }

    private void onGetPacketEvent2(GetPacketEvent getPacketEvent) {
        if (getPacketEvent.getPacket() instanceof S02PacketChat) {
            String str = ExecuteCommandHelper.computeString6(getPacketEvent.<S02PacketChat>getPacket().getChatComponent().getUnformattedText());
            this.handleString(str);
            if (this.flag11 && str.contains("COULDN'T JOIN!") && str.contains("left the pit too recently")) {
                this.flag18 = true;
                this.utilHelper89.reset();
            } else {
                int n = StripColorHelper.computeInt(str);
                if (n >= 0) {
                    this.timestamp2 = Math.max(1, n) * 1000L;
                    this.utilHelper810.reset();
                    this.flag19 = true;
                }
            }
        }
    }

    private static MovementHandler.MidMode computeMidMode(boolean flag, double d, double d2) {
        return flag ? MovementHandler.MidMode.MID : MovementHandler.computeMidMode(d, d2, 19.0);
    }

    private void process21() {
        AutoGrinder autoGrinder = this.computeAutoGrinder();
        if (autoGrinder != null && this.flag15) {
            autoGrinder.process45();
            this.handleAutoGrinder(autoGrinder);
        }

        this.flag15 = false;
        this.flag17 = false;
    }

    private void process16() {
        boolean flag = mc.thePlayer != null
            && mc.theWorld != null
            && this.dropDownSetting.isEnabled()
            && this.entityPlayer != null
            && !this.entityPlayer.isDead
            && this.entityPlayer.getHealth() > 0.0F
            && !GetDisplayedTitleHelper.matchesEntityLivingBase3(this.entityPlayer)
            && GetDisplayedTitleHelper.checkState4();
        this.handleFlag(this.antiLauncherSetting.isEnabled() && !flag);
    }

    private void process15() {
        if (this.flag3 && Brutality.moduleManager != null) {
            handleModule(Brutality.moduleManager.computeModule("AntiLauncher"), this.flag4);
        }

        this.flag3 = false;
    }

    private void handleDouble(double d, double d2, double d3) {
        this.vec3 = new Vec3(d, d2, d3);
        BuildTargetComparatorHelper.handleDouble(d, d2, d3, (int)this.yawSpeedSetting.computeDouble(), (int)this.pitchSpeedSetting.computeDouble());
    }

    public static boolean matchesEntity(Entity entity) {
        AutoHunt autoHunt = instance;
        return autoHunt != null && autoHunt.checkState44() && autoHunt.flag
            ? autoHunt.displaceSetting.isEnabled()
                && entity != null
                && entity == autoHunt.entityPlayer
                && entity instanceof EntityLivingBase
                && mc.thePlayer != null
                && mc.theWorld != null
                && !GetDisplayedTitleHelper.checkState4()
                && !entity.isDead
                && (!autoHunt.displaceIfTurnedAroundSetting.isEnabled() || HuntingHandler.matchesEntityLivingBase(mc.thePlayer, (EntityLivingBase)entity))
                && (!autoHunt.displaceIfNotLookingSetting.isEnabled() || HuntingHandler.computeDouble(mc.thePlayer, (EntityLivingBase)entity) > 45.0)
            : true;
    }

    private static void handleKeepSprint(KeepSprint keepSprint, boolean flag) {
        if (keepSprint.checkState44() != flag) {
            keepSprint.handleFlag3(flag);
            if (keepSprint.checkState44() == flag) {
                ;
            }
        }
    }

    private boolean z() {
        return this.displaceSetting.isEnabled();
    }

    private void process13() {
        KeepSprint instance = KeepSprint.instance;
        if (this.flag8 && instance != null) {
            handleKeepSprint(instance, this.flag9);
        }

        this.flag8 = false;
    }

    private boolean checkState12() {
        if (!ConnectToServerHandler.checkState6()) {
            return false;
        }
        PizzaType pizzaType = ConnectToServerHandler.getPizzaType();
        if (pizzaType == null) {
            return false;
        }
        switch (pizzaType) {
            case RAGE_PIT:
                return !this.ragePitSetting.isEnabled();
            case TDM:
                return !this.tdmSetting.isEnabled();
            case BEAST:
                return !this.beastSetting.isEnabled();
            case ROBBERY:
                return !this.robberySetting.isEnabled();
            case SPIRE:
                return !this.spireSetting.isEnabled();
            case RAFFLE:
                return !this.raffleSetting.isEnabled();
            case BLOCKHEAD:
                return !this.blockheadSetting.isEnabled();
            case SQUADS:
                return !this.squadsSetting.isEnabled();
            case PIZZA:
                return !this.kothSetting.isEnabled();
            default:
                return false;
        }
    }

    private void process10() {
        int n = this.computeInt2();
        if (n != -1) {
            if (!this.checkState9()) {
                mc.thePlayer.inventory.currentItem = n;
            }

            if (GetMysticEnchantLoreHelper.matchesItemStack7(mc.thePlayer.getHeldItem())) {
                this.handleFlag2(true);
                this.utilHelper84.reset();
            }
        }
    }

    @Override
    protected boolean checkState25() {
        return true;
    }

    private boolean matchesDouble(double d, boolean flag, boolean flag2) {
        return !(d <= this.fightRangeSetting.computeDouble())
            || !flag
            || flag2
            || this.entityPlayer == null
            || !MiningHandler2.matchesBlockPos(this.entityPlayer.getPosition());
    }

    private boolean checkState11() {
        return this.entityPlayer != null
            && mc.thePlayer.getDistanceToEntity(this.entityPlayer) < this.swingRangeSetting.computeDouble() + (this.text3 != null ? 0.35 : 0.0);
    }

    public AutoHunt() {
        super("AutoHunt", 0, Category.PIT);
        this.process18();
        instance = this;
        this.targetModesSetting.computeKeybindListSetting(() -> this.warSetting.matchesString2("War"));
        this.ironPackSetting.computeModeSetting(() -> this.warSetting.matchesString2("War") && this.autoBuySetting.isEnabled());
        this.obsidian64Setting.computeKeybindListSetting(() -> this.autoBuySetting.isEnabled() && !this.warSetting.matchesString2("War"));
        this.targetLocationSetting.computeModeSetting(() -> this.dropDownSetting.isEnabled() && !this.warSetting.matchesString2("War"));
        this.strafeRangeSetting.computeNumberSetting(() -> this.strafeChanceSetting.computeDouble() > 0.0);
        this.minJumpDistanceSetting.computeNumberSetting(() -> this.jumpChancePerTickSetting.computeDouble() > 0.0);
        this.hudxSetting.computeNumberSetting(() -> this.hudSetting.isEnabled());
        this.hudySetting.computeNumberSetting(() -> this.hudSetting.isEnabled());
        this.perkClickDelaySetting
            .computeNumberSetting(
                () -> this.autoPerkSetting.isEnabled() || this.warSetting.matchesString2("Bhunt") && this.autoBountyHunterPerkSetting.isEnabled()
            );
        this.goldenheadsSetting.computeModeSetting(() -> this.autoPerkSetting.isEnabled());
        this.strengthchainingSetting.computeModeSetting(() -> this.autoPerkSetting.isEnabled());
        this.safetyfirstSetting.computeModeSetting(() -> this.autoPerkSetting.isEnabled());
        this.gladiatorSetting.computeModeSetting(() -> this.autoPerkSetting.isEnabled());
        this.targetingSetting = new KeybindListSetting(
            "Targeting",
            this,
            this.warSetting,
            this.targetSetting,
            this.fightEnemiesSetting,
            this.fightEnemyDistanceSetting,
            this.megastreaksOnlySetting,
            this.overdriveSetting,
            this.highlanderSetting,
            this.beastmodeSetting,
            this.hermitSetting,
            this.toTheMoonSetting,
            this.bountyThresholdSetting,
            this.lowestArmorSetting,
            this.chaseOutOfPITSetting,
            this.useFocusKOSSetting,
            this.targetModesSetting,
            this.smartSetting,
            this.ironPackSetting,
            this.targetJumpBoostSetting
        );
        this.combatSetting = new KeybindListSetting(
            "Combat",
            this,
            this.killAuraSetting,
            this.fightRangeSetting,
            this.swingRangeSetting,
            this.keepSprintWhenUnwatchedSetting,
            this.donTKeepSprintInPITSetting,
            this.displaceSetting,
            this.displaceIfTurnedAroundSetting,
            this.displaceIfNotLookingSetting,
            this.antiLauncherSetting,
            this.autoblockSetting,
            this.autoblockHealthHeartsSetting
        );
        this.movementSetting = new KeybindListSetting(
            "Movement",
            this,
            this.spawnSetting,
            this.dropDownSetting,
            this.targetLocationSetting,
            this.alwaysSetting,
            this.yawSpeedSetting,
            this.pitchSpeedSetting,
            this.smoothCameraSetting,
            this.minJumpDistanceSetting,
            this.jumpChancePerTickSetting,
            this.strafeRangeSetting,
            this.strafeChanceSetting,
            this.kungFuSetting,
            this.mineObsidianWhenStuckSetting
        );
        this.automationSetting = new KeybindListSetting(
            "Automation",
            this,
            this.outskirtsSetting,
            this.afkSpawnSetting,
            this.lobbySwapSetting,
            this.autoBuySetting,
            this.everyMinsSetting,
            this.autoBuyEveryMinsSetting,
            this.obsidian64Setting,
            this.autoBuyClickDelaySetting
        );
        this.perksSetting = new KeybindListSetting(
            "Perks",
            this,
            this.autoPerkSetting,
            this.autoPerkFrequencySetting,
            this.perkClickDelaySetting,
            this.autoBountyHunterPerkSetting,
            this.bountyHunterSlotSetting,
            this.goldenheadsSetting,
            this.strengthchainingSetting,
            this.safetyfirstSetting,
            this.gladiatorSetting
        );
        this.majorEventsSetting = new KeybindListSetting(
            "Major Events",
            this,
            this.ragePitSetting,
            this.tdmSetting,
            this.beastSetting,
            this.robberySetting,
            this.spireSetting,
            this.raffleSetting,
            this.blockheadSetting,
            this.squadsSetting,
            this.kothSetting
        );
        this.majorEventsSetting.computeKeybindListSetting(() -> !this.warSetting.matchesString2("Bhunt"));
        this.hudAndVisualsSetting = new KeybindListSetting(
            "HUD And Visuals", this, this.hudSetting, this.hudxSetting, this.hudySetting, this.espSetting, this.pathESPSetting, this.destinationESPSetting
        );
        this.handleSettingArray(
            new Setting[]{
                this.targetingSetting,
                this.combatSetting,
                this.movementSetting,
                this.automationSetting,
                this.perksSetting,
                this.majorEventsSetting,
                this.hudAndVisualsSetting
            }
        );
    }

    private boolean checkState6() {
        if (mc.thePlayer == null) {
            return false;
        }
        if (GetMysticEnchantLoreHelper.matchesEntityPlayer9(mc.thePlayer)) {
            return true;
        }
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.stackSize > 0 && stack.getItem() == Items.golden_leggings) {
                return true;
            }
        }

        return false;
    }

    private void process8() {
        if (!this.flag12 && this.flag13 && !this.checkState()) {
            if (!this.afkSpawnSetting.matchesString2("Off")) {
                this.flag12 = true;
                if (this.afkSpawnSetting.matchesString2("Lobby")) {
                    mc.thePlayer.sendChatMessage("/lobby");
                } else {
                    mc.thePlayer.sendChatMessage("/spawn");
                }

                mc.thePlayer.playSound("random.levelup", 1.0F, 1.0F);
                if (this.afkSpawnSetting.matchesString2("AFK Spawn")) {
                    KeybindStateUtil.process();
                }

                this.flag13 = false;
            }
        }
    }

    private void process5() {
        if (this.text3 == null && !this.flag20 && !this.flag21 && !this.flag23) {
            if (this.releaseHandler != null) {
                this.releaseHandler.release();
            }
        } else {
            if (this.releaseHandler != null) {
                this.releaseHandler.release();
            }

            String str = this.text3 == null ? this.killAuraSetting.getText() : this.text3;
            this.process11();
            if ("KillAura".equalsIgnoreCase(str)) {
                KillAura.y = false;
                KillAura instance = KillAura.getInstance();
                boolean flag = this.flag22 || this.entityPlayer != null && this.entityPlayer.isDead || mc.thePlayer != null && mc.thePlayer.isDead;
                if (this.flag20 && instance != null && instance.checkState44() && !flag) {
                    handleModule(instance, false);
                }

                if (flag) {
                    this.flag20 = false;
                } else {
                    this.process22();
                    this.flag20 = false;
                }
            } else {
                AutoClicker.y = false;
                AutoClicker instance2 = AutoClicker.instance;
                if (this.flag21 && instance2 != null && instance2.checkState44()) {
                    instance2.handleFlag3(false);
                }

                this.flag21 = false;
            }

            this.text3 = null;
        }
    }

    private String computeString() {
        if (!this.checkState44()) {
            return "Off";
        }
        if (this.flag18) {
            return "Switching lobby";
        }
        if (this.flag15) {
            return "Selecting perks";
        }
        if (this.warSetting.matchesString2("Bhunt") && this.flag16) {
            return "Preparing Bhunt";
        }
        if (this.hasItemHandler2.isActive()) {
            return this.hasItemHandler2.getStatus();
        }
        if (this.hasItemHandler.isActive()) {
            return this.hasItemHandler.getStatus();
        }
        if (ConnectToServerHandler.checkState10()) {
            return "Waiting for Pit";
        }
        if (this.checkState12()) {
            return "Paused for event";
        }
        if (this.entityPlayer == null) {
            return this.warSetting.matchesString2("Bhunt") ? "Finding bounty" : "Finding target";
        }
        if (GetDisplayedTitleHelper.matchesEntityLivingBase3(this.entityPlayer)) {
            return "Target in spawn";
        }
        if (GetDisplayedTitleHelper.checkState4()) {
            return "Dropping down";
        }
        return this.checkState11() ? "Fighting" : "Chasing";
    }

    private void handleUUID(UUID uuid) {
        String str = this.lookupMap.remove(uuid);
        if (str != null) {
            if (!this.warSetting.matchesString2("Target") || str.equalsIgnoreCase(this.targetSetting.getValue().trim())) {
                this.count++;
                this.flag12 = false;
                if (this.outskirtsSetting.matchesString2("Outskirts")) {
                    this.blockPos2 = this.computeBlockPos();
                }
            }
        }
    }

    private void handleEntityPlayer(EntityPlayer player, double d, double d2, boolean flag) {
        double d3 = this.strafeChanceSetting.computeDouble() / 2000.0;
        if (Math.random() < d3 && utilHelper811.matchesLong(2000L)) {
            utilHelper811.reset();
            count4 = 500 + random2.nextInt(1501);
        }

        if (utilHelper812.matchesLong(700L)) {
            leftMode = ObsidianPlacerHandler.LeftMode.getInstance();
            utilHelper812.reset();
        }

        boolean flag2 = flag && ObsidianPlacerHandler.matchesEntityPlayer2(player) && this.matchesEntityPlayer(player, 16.0, 0.3);
        if (ObsidianPlacerHandler.matchesEntityPlayer(player) && flag2 && flag) {
            leftMode.process();
            ObsidianPlacerHandler.handleFlag(false);
        }

        if (UtilHelper6.matchesEntityPlayer(player, 27.0F, 27.0F) && flag2 && ObsidianPlacerHandler.matchesEntityPlayer2(player)) {
            leftMode.process();
            ObsidianPlacerHandler.handleFlag(false);
        }

        if (!utilHelper811.matchesLong(count4) && d <= this.strafeRangeSetting.computeDouble() && Math.abs(d2) < 90.0 && flag) {
            leftMode.process();
            if (d <= 4.5) {
                ObsidianPlacerHandler.handleFlag(false);
            }
        }
    }

    public static boolean checkState13() {
        return instance != null && instance.checkState44();
    }

    private boolean checkState5() {
        if (this.mineObsidianWhenStuckSetting.isEnabled() && this.entityPlayer != null && mc.thePlayer != null && this.clearPathHandler.checkState()) {
            boolean flag = BuildTargetComparatorHelper.matchesEntityPlayer(mc.thePlayer, this.entityPlayer, 0.1, 0.1);
            if (!flag && this.clearPathHandler.checkState()) {
                if (this.blockPos3 == null) {
                    Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
                    Vec3 vec = new Vec3(this.entityPlayer.posX, this.entityPlayer.posY + this.entityPlayer.getEyeHeight() * 0.5, this.entityPlayer.posZ);
                    MovingObjectPosition hit = mc.theWorld.rayTraceBlocks(positionEyes, vec, false, true, false);
                    if (hit == null || hit.getBlockPos() == null || mc.theWorld.getBlockState(hit.getBlockPos()).getBlock() != Blocks.obsidian) {
                        return false;
                    }

                    this.blockPos3 = hit.getBlockPos();
                    this.count3 = mc.thePlayer.inventory.currentItem;
                    this.count2 = this.computeInt();
                    if (this.count2 < 0) {
                        this.blockPos3 = null;
                        return false;
                    }
                }

                Block block = mc.theWorld.getBlockState(this.blockPos3).getBlock();
                if (block != Blocks.bedrock && block == Blocks.obsidian) {
                    mc.thePlayer.inventory.currentItem = this.count2;
                    this.handleDouble(this.blockPos3.getX() + 0.5, this.blockPos3.getY() + 0.5, this.blockPos3.getZ() + 0.5);
                    float[] floats = MathHelper.computeFloatArray(this.vec3.xCoord, this.vec3.yCoord, this.vec3.zCoord);
                    boolean flag2 = Math.abs(net.minecraft.util.MathHelper.wrapAngleTo180_float(floats[0] - mc.thePlayer.rotationYaw)) <= 5.0F
                        && Math.abs(floats[1] - mc.thePlayer.rotationPitch) <= 5.0F;
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), flag2);
                    return true;
                }
                this.process23();
                this.clearPathHandler.process();
                return false;
            }
            this.process23();
            return false;
        }
        this.process23();
        return false;
    }

    private ModeSetting computeModeSetting(String str, String str2) {
        return new ModeSetting(str, this, str2, Arrays.asList("None", "Overdrive", "Highlander", "Beastmode", "Hermit", "To the Moon", "Uberstreak"))
            .computeModeSetting(() -> this.warSetting.matchesString2("Bhunt") && this.megastreaksOnlySetting.isEnabled());
    }

    private void process3() {
        if (!this.autoBuySetting.isEnabled()) {
            if (this.hasItemHandler.isActive()) {
                this.hasItemHandler.cancel();
            }

            if (this.hasItemHandler2.isActive()) {
                this.hasItemHandler2.cancel();
            }
        }
    }

    private void onEventRender3D2(EventRender3D event) {
        if (mc.thePlayer != null && this.entityPlayer != null) {
            if (this.destinationESPSetting.isEnabled()) {
                ResetColorHelper.handleBlockPos2(this.entityPlayer.getPosition().down(), new Color(70, 220, 255, 90), true, true, 1.0F);
            }

            if (this.espSetting.isEnabled()) {
                double d = mc.thePlayer.posX - mc.getRenderManager().viewerPosX;
                double d2 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() * 0.7 - mc.getRenderManager().viewerPosY;
                double d3 = mc.thePlayer.posZ - mc.getRenderManager().viewerPosZ;
                double d4 = this.entityPlayer.posX - mc.getRenderManager().viewerPosX;
                double d5 = this.entityPlayer.posY + this.entityPlayer.getEyeHeight() * 0.7 - mc.getRenderManager().viewerPosY;
                double d6 = this.entityPlayer.posZ - mc.getRenderManager().viewerPosZ;
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glLineWidth(2.0F);
                GL11.glColor4f(0.25F, 0.9F, 1.0F, 0.85F);
                GL11.glBegin(GL11.GL_LINE_STRIP);
                GL11.glVertex3d(d, d2, d3);
                GL11.glVertex3d(d4, d5, d6);
                GL11.glEnd();
                GL11.glPopAttrib();
            }
        }
    }

    private void onGetPacketEvent(GetPacketEvent getPacketEvent) {
        if (getPacketEvent.getPacket() instanceof S38PacketPlayerListItem && mc.theWorld != null) {
            S38PacketPlayerListItem packet = getPacketEvent.getPacket();
            if (packet.getAction() == S38PacketPlayerListItem.Action.REMOVE_PLAYER) {
                for (S38PacketPlayerListItem.AddPlayerData addPlayerData : packet.getEntries()) {
                    if (addPlayerData.getProfile() != null) {
                        UUID id = addPlayerData.getProfile().getId();
                        if (this.lookupMap.containsKey(id)) {
                            if (this.entityPlayer != null && this.entityPlayer.getUniqueID().equals(id)) {
                                this.flag22 = this.killAuraSetting.matchesString2("KillAura");
                                if (this.flag22) {
                                    KillAura.y = false;
                                    this.process11();
                                }
                            }

                            this.handleUUID(id);
                        }
                    }
                }
            }
        }
    }

    private void handleAutoGrinder(AutoGrinder autoGrinder) {
        if (this.stringArray != null && autoGrinder != null) {
            autoGrinder.handleStringArray(this.stringArray);
            this.stringArray = null;
        }
    }

    private boolean matchesEntityPlayer(EntityPlayer player, double d, double d2) {
        return player != null
            && mc.thePlayer != null
            && mc.thePlayer.getDistanceToEntity(player) <= d
            && BuildTargetComparatorHelper.matchesEntityPlayer(mc.thePlayer, player, 0.15, d2);
    }

    public static boolean matchesKillAura(KillAura killAura, EntityLivingBase living) {
        AutoHunt autoHunt = instance;
        return matchesKillAura(killAura)
            && mc.thePlayer != null
            && mc.theWorld != null
            && mc.currentScreen == null
            && !mc.thePlayer.isRiding()
            && living != null
            && living == autoHunt.entityPlayer
            && !GetDisplayedTitleHelper.checkState4()
            && !GetDisplayedTitleHelper.matchesEntityLivingBase3(living)
            && HuntingHandler.matchesEntityLivingBase(mc.thePlayer, living, autoHunt.autoblockHealthHeartsSetting.computeDouble());
    }

    private boolean checkState() {
        if (mc.theWorld != null && mc.theWorld.getScoreboard() != null) {
            for (String str : GetOrderedTeamsHelper.computeList(mc.theWorld.getScoreboard())) {
                String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(str);
                if (textWithoutFormattingCodes != null && textWithoutFormattingCodes.toLowerCase(Locale.ROOT).contains("status: fighting")) {
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        if (player != null
            && player != mc.thePlayer
            && player.isEntityAlive()
            && !player.isSpectator()
            && !GetDisplayedTitleHelper.matchesEntityLivingBase3(player)
            && this.itemSet.contains(player.getName().toLowerCase(Locale.ROOT))
            && !DefaultHelper.matchesString(player.getName())
            && !KillAura.matchesEntity(player)) {
            JumpBoostDetector instance = JumpBoostDetector.getInstance();
            return this.targetJumpBoostSetting.isEnabled() || instance == null || !instance.matchesString(player.getName());
        }
        return false;
    }

    private void handleString(String text) {
        long now = System.currentTimeMillis();
        if (!text.equals(this.text) || now - this.timestamp >= 100L) {
            this.text = text;
            this.timestamp = now;

            try {
                double d = ExecuteCommandHelper.computeDouble4(text);
                if (Double.isFinite(d) && d > 0.0) {
                    this.amount2 += d;
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private boolean checkState14() {
        return this.killAuraSetting.matchesString2("KillAura");
    }

    public static boolean matchesKillAura(KillAura killAura) {
        AutoHunt autoHunt = instance;
        return autoHunt != null
            && autoHunt.checkState44()
            && autoHunt.releaseHandler != null
            && autoHunt.releaseHandler.matchesKillAura(killAura)
            && autoHunt.autoblockSetting.isEnabled()
            && autoHunt.killAuraSetting.matchesString2("KillAura");
    }

    private int computeInt() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() == Items.golden_pickaxe) {
                return i;
            }
        }

        int n = -1;

        for (int j = 9; j < 36; j++) {
            ItemStack stack2 = mc.thePlayer.inventory.getStackInSlot(j);
            if (stack2 != null && stack2.getItem() == Items.golden_pickaxe) {
                n = j;
                break;
            }
        }

        if (n >= 0 && mc.thePlayer.openContainer != null) {
            int n2 = -1;

            for (int k = 0; k < 9; k++) {
                ItemStack stack3 = mc.thePlayer.inventory.getStackInSlot(k);
                if (stack3 == null || !(stack3.getItem() instanceof ItemSword) && !(stack3.getItem() instanceof ItemAxe) && !(stack3.getItem() instanceof ItemBow)) {
                    n2 = k;
                    break;
                }
            }

            if (n2 < 0) {
                return -1;
            }
            mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, n, n2, 2, mc.thePlayer);
            return n2;
        }
        return -1;
    }

    private void process() {
        KeepSprint instance = KeepSprint.instance;
        if (instance != null && this.displaceSetting.isEnabled() && AutoDisable.matchesModule(instance, Displace.instance)) {
            if (!this.flag8) {
                this.flag9 = instance.checkState44();
                this.flag8 = true;
            }

            handleKeepSprint(instance, false);
        } else if (instance != null
            && this.donTKeepSprintInPITSetting.isEnabled()
            && mc.thePlayer != null
            && mc.theWorld != null
            && GetDisplayedTitleHelper.matchesEntityPlayer(mc.thePlayer)
            && !GetDisplayedTitleHelper.checkState4()) {
            if (!this.flag8) {
                this.flag9 = instance.checkState44();
                this.flag8 = true;
            }

            handleKeepSprint(instance, false);
        } else if (this.keepSprintWhenUnwatchedSetting.isEnabled()
            && instance != null
            && mc.thePlayer != null
            && mc.theWorld != null
            && this.entityPlayer != null
            && !this.entityPlayer.isDead
            && !(this.entityPlayer.getHealth() <= 0.0F)
            && !GetDisplayedTitleHelper.checkState4()
            && !ConnectToServerHandler.checkState6()) {
            if (!this.flag8) {
                this.flag9 = instance.checkState44();
                this.flag8 = true;
            }

            Vec3 vec = mc.thePlayer.getPositionEyes(1.0F).subtract(this.entityPlayer.getPositionEyes(1.0F)).normalize();
            boolean flag = this.entityPlayer.getLook(1.0F).dotProduct(vec) > 0.70710678;
            handleKeepSprint(instance, !flag);
        } else {
            this.process13();
        }
    }

    @Override
    public void onRender() {
        if (this.hudSetting.isEnabled() && mc.thePlayer != null && mc.theWorld != null) {
            FontRenderer fontRenderer = computeFontRenderer(AutoHunt.class);
            EntityPlayer entityPlayer = this.entityPlayer;
            String str = this.computeString(mc.thePlayer);
            String str2 = "Set".equals(str)
                ? "§c"
                : (
                    "Iron Pack".equals(str)
                        ? "§f"
                        : ("Shop".equals(str) ? "§b" : ("Darks".equals(str) ? "§5" : ("Bhunt".equals(str) ? "§6" : ("Naked".equals(str) ? "§8" : "§7"))))
                );
            String[] parts = new String[]{
                "§c§lAuto Hunt §7[§f" + this.computeString() + "§7]",
                "§7Mode: §f" + this.warSetting.getText(),
                "§7Gold Gained: §6" + String.format(Locale.ROOT, "%.2f", this.amount2) + "g",
                "§7Target: §f" + (entityPlayer == null ? "None" : entityPlayer.getName()),
                "§7Target Kills: §f" + this.count,
                "§7Kit: " + str2 + str,
                "§7Location: §f" + FreshHelper.computeString(mc.thePlayer, mc.theWorld),
                "§7Target Location: §f" + (entityPlayer == null ? "Unknown" : FreshHelper.computeString(entityPlayer, mc.theWorld)),
                "§7Target Health: §f" + (entityPlayer == null ? "Unknown" : String.format("%.1f", entityPlayer.getHealth()))
            };
            int n = 0;

            for (String str3 : parts) {
                n = Math.max(n, fontRenderer.getStringWidth(str3));
            }

            int n2 = (int)this.hudxSetting.computeDouble();
            int n3 = (int)this.hudySetting.computeDouble();
            Gui.drawRect(n2 - 3, n3 - 2, n2 + n + 3, n3 + parts.length * (fontRenderer.FONT_HEIGHT + 1) + 2, 2013265920);

            for (int i = 0; i < parts.length; i++) {
                fontRenderer.drawStringWithShadow(parts[i], n2, n3 + i * (fontRenderer.FONT_HEIGHT + 1), -1);
            }
        }
    }

    private void handleKillAura(KillAura killAura) {
        boolean flag = this.entityPlayer != null
            && !this.entityPlayer.isDead
            && killAura.checkState44()
            && killAura.g()
            && mc.thePlayer != null
            && mc.theWorld != null
            && !GetDisplayedTitleHelper.checkState4();
        int keyCode = mc.gameSettings.keyBindAttack.getKeyCode();
        if (flag) {
            KeyBinding.setKeyBindState(keyCode, true);
            this.flag23 = true;
        } else {
            this.process11();
        }
    }

    private void process25() {
        Module module = Brutality.moduleManager.computeModule("Displace");
        if (module != null) {
            if (!this.flag) {
                this.flag2 = module.checkState44();
                this.flag = true;
            }

            boolean enabled = this.displaceSetting.isEnabled();
            handleModule(module, enabled);
        }
    }

    @Override
    public void onEvent(Event event) {
        // VENDOR-DISABLED: if (!EntitlementState.checkState()) {
        if (false) {
            this.handleFlag3(false);
        } else if (this.checkState44()) {
            if (event instanceof RenderEvent4) {
                if (this.flag18) {
                    this.utilHelper89.reset();
                } else {
                    this.flag11 = false;
                }

                this.process13();
                this.process24();
                this.process17();
                this.process15();
                this.hasItemHandler2.cancel();
                this.hasItemHandler.cancel();
                this.process5();
                this.entityPlayer = null;
                this.clearPathHandler.process2();
                PlayerHelper13.reset();
                this.lookupMap.clear();
                this.blockPos = null;
                this.midMode = null;
                this.clearPathHandler2.clearPath();
            } else if (event instanceof AttackEvent attackEvent) {
                if (attackEvent.getEntityPlayer() == mc.thePlayer && attackEvent.getEntityPlayer2() instanceof EntityPlayer) {
                    EntityPlayer entityPlayer2 = attackEvent.getEntityPlayer2();
                    boolean flag = !this.warSetting.matchesString2("Target")
                        ? this.entityPlayer != null && this.entityPlayer.getUniqueID().equals(entityPlayer2.getUniqueID())
                        : entityPlayer2.getName().equalsIgnoreCase(this.targetSetting.getValue().trim());
                    if (flag) {
                        this.lookupMap.put(entityPlayer2.getUniqueID(), entityPlayer2.getName());
                    }
                }
            } else if (event instanceof GetPacketEvent getPacketEvent) {
                this.onGetPacketEvent(getPacketEvent);
                this.onGetPacketEvent2(getPacketEvent);
            } else if (event instanceof EventRender3D) {
                this.onEventRender3D((EventRender3D)event);
                this.onEventRender3D2((EventRender3D)event);
            } else if (event instanceof EventTick) {
                if (((EventTick)event).getState() == EventState.PRE) {
                    if (this.warSetting.matchesString2("Bhunt") && ConnectToServerHandler.checkState6()) {
                        this.process21();
                        this.process13();
                        this.process5();
                        KeybindStateUtil.process();
                        this.process16();
                    } else {
                        this.process14();
                        this.vec3 = null;
                        if (mc.thePlayer != null && mc.theWorld != null) {
                            if (!this.warSetting.getText().equals(this.text2)) {
                                this.process13();
                                this.process24();
                                this.process17();
                                this.process15();
                                this.hasItemHandler2.cancel();
                                this.hasItemHandler2.process2();
                                this.process5();
                                this.entityPlayer = null;
                                this.amount = -1.0;
                                this.clearPathHandler.process2();
                                this.process21();
                                this.text2 = this.warSetting.getText();
                                this.flag16 = this.warSetting.matchesString2("Bhunt") && this.autoBountyHunterPerkSetting.isEnabled() && !this.checkState6();
                                this.utilHelper88.reset();
                                this.utilHelper87.clear();
                            }

                            if (this.flag18) {
                                KeybindStateUtil.process();
                                this.process5();
                                if (this.utilHelper89.matchesLong(5000L)) {
                                    mc.thePlayer.sendChatMessage("/play pit");
                                    this.flag18 = false;
                                    this.flag14 = false;
                                    this.utilHelper86.reset();
                                }
                            } else if (ConnectToServerHandler.checkState10()) {
                                KeybindStateUtil.process();
                            } else {
                                this.process3();
                                boolean flag2 = GetDisplayedTitleHelper.checkState4();
                                this.process16();
                                if (flag2) {
                                    this.flag19 = false;
                                }

                                if (this.flag19 && this.utilHelper810.matchesLong(this.timestamp2)) {
                                    this.flag19 = false;
                                    mc.thePlayer.sendChatMessage("/spawn");
                                    this.utilHelper83.reset();
                                }

                                if (flag2 && !this.flag14) {
                                    this.hasItemHandler.process2();
                                    this.hasItemHandler2.process2();
                                    this.utilHelper88.reset();
                                    if (this.warSetting.matchesString2("Bhunt")) {
                                        this.flag16 = this.autoBountyHunterPerkSetting.isEnabled() && !this.checkState6();
                                    }
                                }

                                this.flag14 = flag2;
                                if (!this.matchesFlag(flag2)) {
                                    if (this.autoBuySetting.isEnabled() && this.warSetting.matchesString2("War") && flag2 && this.hasItemHandler2.checkState2()) {
                                        this.process5();
                                        KeybindStateUtil.process();
                                        this.hasItemHandler2.tick();
                                    } else if (this.autoBuySetting.isEnabled()
                                        && !this.warSetting.matchesString2("War")
                                        && flag2
                                        && this.hasItemHandler.checkState2()) {
                                        this.process5();
                                        KeybindStateUtil.process();
                                        this.hasItemHandler.tick();
                                    } else if (!this.checkState5()) {
                                        if (this.utilHelper8.matchesLong(250L)) {
                                            this.utilHelper8.reset();
                                            this.process7();
                                        }

                                        this.process();
                                        this.process25();
                                        this.process14();
                                        if (GetMysticEnchantLoreHelper.checkState5()) {
                                            KeybindStateUtil.process();
                                        } else if (!this.checkState12() && (!this.warSetting.matchesString2("Bhunt") || !ConnectToServerHandler.checkState6())) {
                                            if (this.lobbySwapSetting.isEnabled()
                                                && this.warSetting.matchesString2("Bhunt")
                                                && this.utilHelper86.matchesLong(15000L)
                                                && !this.checkState8()) {
                                                this.process20();
                                                if (this.flag18) {
                                                    return;
                                                }
                                            }

                                            if (this.entityPlayer != null
                                                && !this.entityPlayer.isDead
                                                && !(this.entityPlayer.getHealth() <= 0.0F)
                                                && !GetDisplayedTitleHelper.matchesEntityLivingBase3(this.entityPlayer)) {
                                                boolean flag3 = GetDisplayedTitleHelper.checkState4()
                                                    && !this.alwaysSetting.matchesString2("Off")
                                                    && !this.checkState2()
                                                    && this.computeInt2() != -1;
                                                if (flag3) {
                                                    this.process10();
                                                    KeybindStateUtil.process();
                                                } else if (mc.thePlayer.isRiding()) {
                                                    KeybindStateUtil.process();
                                                } else {
                                                    this.process16();
                                                    double dist = mc.thePlayer.getDistanceToEntity(this.entityPlayer);
                                                    this.flag13 = true;
                                                    boolean flag4 = this.matchesEntityPlayer(
                                                        this.entityPlayer, Math.max(10.0, this.fightRangeSetting.computeDouble()), 0.7
                                                    );
                                                    if (!GetDisplayedTitleHelper.checkState4()
                                                        && this.alwaysSetting.matchesString2("Always")
                                                        && !this.checkState2()
                                                        && (!(dist <= 10.0) || !flag4)
                                                        && this.computeInt2() != -1) {
                                                        this.process10();
                                                    }

                                                    if (this.checkState11()) {
                                                        this.process12();
                                                    } else {
                                                        this.process5();
                                                    }

                                                    KeybindStateUtil.process();
                                                    if (!this.checkState9() && this.utilHelper82.matchesLong(250L)) {
                                                        this.utilHelper82.reset();
                                                        this.handleFlag2(false);
                                                        if (this.kungFuSetting.isEnabled()) {
                                                            ExtraAttributesHandler.process();
                                                        } else {
                                                            ExtraAttributesHandler.handleEntityPlayer(
                                                                this.entityPlayer,
                                                                GetMysticEnchantLoreHelper.matchesEntityPlayer3(mc.thePlayer),
                                                                GetDisplayedTitleHelper.checkState()
                                                            );
                                                        }
                                                    }

                                                    float f = net.minecraft.util.MathHelper.wrapAngleTo180_float(
                                                        MathHelper.computeFloatArray(this.entityPlayer.posX, this.entityPlayer.posY, this.entityPlayer.posZ)[0]
                                                            - mc.thePlayer.rotationYaw
                                                    );
                                                    boolean flag5 = UtilHelper6.matchesEntityPlayer(this.entityPlayer, 40.0F, 40.0F);
                                                    if (flag2) {
                                                        if (!this.dropDownSetting.isEnabled()) {
                                                            this.process16();
                                                        } else {
                                                            MovementHandler.MidMode midMode = computeMidMode(
                                                                !this.warSetting.matchesString2("War") && this.targetLocationSetting.matchesString2("PIT"),
                                                                this.entityPlayer.posX,
                                                                this.entityPlayer.posZ
                                                            );
                                                            if (midMode == MovementHandler.MidMode.MID) {
                                                                this.process16();
                                                                if (this.midMode != midMode) {
                                                                    this.clearPathHandler2.clearPath();
                                                                }

                                                                this.midMode = midMode;
                                                                this.blockPos = null;
                                                                this.handleDouble(0.0, mc.thePlayer.posY + 1.0, 0.0);
                                                                AutoGrinder.process46();
                                                                this.clearPathHandler2.handleFlag2(false);
                                                            } else {
                                                                if (this.midMode != midMode || this.utilHelper85.matchesLong(5000L)) {
                                                                    this.clearPathHandler2.clearPath();
                                                                    this.blockPos = MovementHandler2.computeBlockPos2(
                                                                        mc.theWorld, midMode.getSearchX(), midMode.getSearchZ()
                                                                    );
                                                                    this.midMode = midMode;
                                                                    this.utilHelper85.reset();
                                                                }

                                                                this.process16();
                                                                if (this.blockPos != null) {
                                                                    this.clearPathHandler2
                                                                        .handleBlockPos(
                                                                            this.blockPos,
                                                                            true,
                                                                            (int)this.yawSpeedSetting.computeDouble(),
                                                                            (int)this.pitchSpeedSetting.computeDouble(),
                                                                            this.computeDouble()
                                                                        );
                                                                    this.clearPathHandler2.handleFlag2(this.pathESPSetting.isEnabled());
                                                                } else {
                                                                    this.handleDouble(0.0, mc.thePlayer.posY + 1.0, 0.0);
                                                                    AutoGrinder.process46();
                                                                    this.clearPathHandler2.handleFlag2(false);
                                                                }
                                                            }

                                                            ObsidianPlacerHandler.handleFlag(Math.random() < this.computeDouble());
                                                        }
                                                    } else {
                                                        boolean flag6 = this.entityPlayer.getPosition().getY() - mc.thePlayer.getPosition().getY() > 4;
                                                        if (!this.matchesDouble(dist, flag4, flag6)) {
                                                            BuildTargetComparatorHelper.handleEntity(
                                                                this.entityPlayer,
                                                                (int)this.yawSpeedSetting.computeDouble(),
                                                                (int)this.pitchSpeedSetting.computeDouble()
                                                            );
                                                            this.clearPathHandler.handleFlag2(false);
                                                            this.clearPathHandler.process();
                                                            this.clearPathHandler2.handleFlag2(false);
                                                            AutoGrinder.process46();
                                                            boolean flag7 = dist >= this.minJumpDistanceSetting.computeDouble() || !flag5;
                                                            ObsidianPlacerHandler.handleFlag(flag7 && Math.random() < this.computeDouble());
                                                            this.handleEntityPlayer(this.entityPlayer, dist, f, flag5);
                                                            if (FenceHandler.checkState()) {
                                                                ObsidianPlacerHandler.handleFlag(true);
                                                            }
                                                        } else {
                                                            boolean flag8 = dist >= this.minJumpDistanceSetting.computeDouble() || !flag5;
                                                            this.handleEntityPlayer(this.entityPlayer, dist, f, flag5);
                                                            this.clearPathHandler
                                                                .handleEntity2(
                                                                    this.entityPlayer,
                                                                    (int)(this.yawSpeedSetting.computeDouble() * 0.6),
                                                                    (int)(this.pitchSpeedSetting.computeDouble() * 0.7),
                                                                    flag8 ? this.computeDouble() : 0.0
                                                                );
                                                            this.clearPathHandler.handleFlag2(this.pathESPSetting.isEnabled());
                                                            this.clearPathHandler2.handleFlag2(false);
                                                        }
                                                    }

                                                    this.amount = dist;
                                                }
                                            } else {
                                                KeybindStateUtil.process();
                                                this.process5();
                                                boolean flag9 = this.count > 0 && this.outskirtsSetting.matchesString2("Outskirts") && this.blockPos2 != null;
                                                if (!flag9
                                                    && !this.flag19
                                                    && !GetDisplayedTitleHelper.matchesEntityLivingBase3(mc.thePlayer)
                                                    && this.spawnSetting.isEnabled()
                                                    && this.utilHelper83.matchesLong(4000L)) {
                                                    this.utilHelper83.reset();
                                                    MessageUtils.x(EnumChatFormatting.YELLOW + "Spawning since there is no target or they are in spawn!");
                                                    mc.thePlayer.sendChatMessage("/spawn");
                                                }

                                                this.process16();
                                                this.process8();
                                                if (this.lobbySwapSetting.isEnabled()
                                                    && (this.warSetting.matchesString2("Bhunt") || !this.targetSetting.getValue().trim().isEmpty())) {
                                                    this.process20();
                                                    if (this.flag18) {
                                                        return;
                                                    }
                                                }

                                                if (this.blockPos2 != null && this.outskirtsSetting.matchesString2("Outskirts") && !flag2) {
                                                    this.clearPathHandler
                                                        .handleBlockPos(
                                                            this.blockPos2,
                                                            (int)this.yawSpeedSetting.computeDouble(),
                                                            (int)this.pitchSpeedSetting.computeDouble(),
                                                            this.computeDouble()
                                                        );
                                                    this.clearPathHandler.handleFlag2(this.pathESPSetting.isEnabled());
                                                }
                                            }
                                        } else {
                                            KeybindStateUtil.process();
                                            this.process5();
                                            this.process16();
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

    private void process22() {
        if (this.releaseHandler != null) {
            this.releaseHandler.release();
        }

        if (this.flag25) {
            KillAura.entityLivingBase3 = this.entityLivingBase;
            KillAura.flag50 = this.flag24;
            this.entityLivingBase = null;
            this.flag25 = false;
        }
    }

    private void process7() {
        if (this.warSetting.matchesString2("War")) {
            this.process4();
        } else if (this.warSetting.matchesString2("Bhunt")) {
            this.refreshBountyTarget();
        } else {
            int n = (int)this.fightEnemyDistanceSetting.computeDouble();
            boolean flag = false;
            EntityPlayer player = null;
            boolean flag2 = false;
            EntityPlayer player2 = null;
            double MAX_VALUE = Double.MAX_VALUE;
            String str = this.targetSetting.getValue().trim();

            for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                if (!str.isEmpty() && playerEntity.getName().equalsIgnoreCase(str)) {
                    flag2 = GetDisplayedTitleHelper.matchesEntityLivingBase3(playerEntity);
                    flag = true;
                    player = playerEntity;
                }
            }

            if (this.fightEnemiesSetting.isEnabled() && !GetDisplayedTitleHelper.checkState4()) {
                for (EntityPlayer playerEntity2 : mc.theWorld.playerEntities) {
                    if (playerEntity2 != mc.thePlayer
                        && playerEntity2 != player
                        && WHelper.matchesString(playerEntity2.getName())
                        && !GetDisplayedTitleHelper.matchesEntityLivingBase3(playerEntity2)
                        && playerEntity2.getPosition().getY() - mc.thePlayer.getPosition().getY() <= 15) {
                        double dist = mc.thePlayer.getDistanceToEntity(playerEntity2);
                        if (dist < MAX_VALUE && dist <= n) {
                            MAX_VALUE = dist;
                            player2 = playerEntity2;
                        }
                    }
                }
            }

            this.flag10 = flag;
            if (!GetDisplayedTitleHelper.checkState4() && flag2 && player2 != null) {
                this.entityPlayer = player2;
            } else if (!flag && player2 == null) {
                this.entityPlayer = null;
            } else if (!flag) {
                this.entityPlayer = player2;
            } else if (player2 == null) {
                this.entityPlayer = player;
            } else if (mc.thePlayer.getDistanceToEntity(player) > n) {
                this.entityPlayer = player2;
            } else {
                this.entityPlayer = player;
            }
        }
    }

    private void process20() {
        if (!this.warSetting.matchesString2("Bhunt") || !this.checkState8()) {
            if (mc.thePlayer != null && !this.checkState() && !this.flag19 && !this.flag18 && this.utilHelper86.matchesLong(15000L)) {
                this.utilHelper86.reset();
                this.process5();
                this.entityPlayer = null;
                this.clearPathHandler.process2();
                this.clearPathHandler2.process2();
                this.hasItemHandler.cancel();
                mc.thePlayer.sendChatMessage("/lobby");
                this.utilHelper89.reset();
                this.flag18 = true;
                this.flag11 = true;
            }
        }
    }

    @Override
    public void onEnable() {
        PlayerHelper13.reset();
        // VENDOR-DISABLED: if (!EntitlementState.checkState()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            this.clearPathHandler.clearPath();
            this.clearPathHandler2.clearPath();
            this.blockPos = null;
            this.midMode = null;
            this.count = 0;
            this.amount2 = 0.0;
            this.text = null;
            this.flag11 = false;
            this.flag12 = false;
            this.lookupMap.clear();
            this.text3 = null;
            this.flag20 = false;
            this.flag21 = false;
            this.flag22 = false;
            this.flag25 = false;
            this.vec3 = null;
            this.entityPlayer = null;
            this.amount = -1.0;
            this.text2 = this.warSetting.getText();
            this.flag16 = this.warSetting.matchesString2("Bhunt") && this.autoBountyHunterPerkSetting.isEnabled() && !this.checkState6();
            this.utilHelper88.reset();
            this.flag15 = false;
            this.flag14 = false;
            this.hasItemHandler.process2();
            this.hasItemHandler2.process2();
            this.utilHelper87.clear();
            this.flag18 = false;
            this.utilHelper86.reset();
            this.process14();
        }
    }

    public static boolean checkState4() {
        return instance != null && instance.checkState44() && instance.smoothCameraSetting.isEnabled();
    }

    private void process17() {
        if (this.flag5) {
            JumpBoostDetector instance = JumpBoostDetector.getInstance();
            if (instance != null) {
                instance.handleFlag(this.flag7);
            }

            handleModule(instance, this.flag6);
        }

        this.flag5 = false;
    }

    private boolean matchesFlag(boolean flag) {
        if (this.warSetting.matchesString2("Bhunt") && ConnectToServerHandler.checkState6()) {
            if (this.flag15) {
                this.process21();
            }

            return false;
        }
        if (!this.autoBountyHunterPerkSetting.isEnabled()) {
            this.flag16 = false;
            if (this.flag15 && this.flag17 && !this.autoPerkSetting.isEnabled()) {
                this.process21();
            }
        }

        if (!flag && this.flag15) {
            this.process21();
        }

        if (this.warSetting.matchesString2("Bhunt") && this.flag16 && this.checkState6()) {
            this.flag16 = false;
        }

        if (!flag) {
            if (this.warSetting.matchesString2("Bhunt") && this.flag16) {
                this.process5();
                KeybindStateUtil.process();
                if (!this.flag19 && !this.checkState() && this.utilHelper83.matchesLong(4000L)) {
                    mc.thePlayer.sendChatMessage("/spawn");
                    this.utilHelper83.reset();
                }

                return true;
            }
            return false;
        }
        boolean flag2 = this.warSetting.matchesString2("Bhunt") && this.flag16;
        boolean flag3 = this.autoPerkSetting.isEnabled() && this.utilHelper87.matchesLong((long)this.autoPerkFrequencySetting.computeDouble() * 60000L);
        if (!this.flag15 && !flag2 && !flag3) {
            return false;
        }
        if (!this.flag15 && flag2 && !this.utilHelper88.matchesLong(1000L)) {
            this.process5();
            KeybindStateUtil.process();
            return true;
        }
        AutoGrinder autoGrinder = this.computeAutoGrinder();
        if (autoGrinder == null) {
            this.flag16 = false;
            this.utilHelper87.reset();
            MessageUtils.x("AutoHunt: perk automation is unavailable.");
            return false;
        }
        this.process5();
        KeybindStateUtil.process();
        if (!this.flag15) {
            this.stringArray = autoGrinder.computeStringArray();
            String[] parts = new String[]{
                this.goldenheadsSetting.getText(),
                this.strengthchainingSetting.getText(),
                this.safetyfirstSetting.getText(),
                this.gladiatorSetting.getText()
            };
            if (this.warSetting.matchesString2("Bhunt") && this.autoBountyHunterPerkSetting.isEnabled()) {
                int n = net.minecraft.util.MathHelper.clamp_int((int)this.bountyHunterSlotSetting.computeDouble() - 1, 0, parts.length - 1);
                String str = parts[n];

                for (int i = 0; i < parts.length; i++) {
                    if (i != n && "BOUNTY_HUNTER".equals(parts[i])) {
                        parts[i] = "BOUNTY_HUNTER".equals(str) ? "SAFETY_FIRST" : str;
                    }
                }

                parts[n] = "BOUNTY_HUNTER";
            }

            autoGrinder.handleStringArray(parts);
            int n2 = this.warSetting.matchesString2("Bhunt")
                    && this.autoBountyHunterPerkSetting.isEnabled()
                    && !this.autoPerkSetting.isEnabled()
                ? (int)this.bountyHunterSlotSetting.computeDouble() - 1
                : -1;
            autoGrinder.handleInt(n2, (long)this.perkClickDelaySetting.computeDouble());
            this.flag17 = this.warSetting.matchesString2("Bhunt");
            this.flag15 = true;
        }

        if (!autoGrinder.checkState16()) {
            if (this.flag17) {
                this.flag16 = false;
            }

            this.flag15 = false;
            this.flag17 = false;
            this.utilHelper87.reset();
            this.handleAutoGrinder(autoGrinder);
        }

        return true;
    }

    private AutoGrinder computeAutoGrinder() {
        Module module = Brutality.moduleManager == null ? null : Brutality.moduleManager.computeModule("AutoGrinder");
        return module instanceof AutoGrinder ? (AutoGrinder)module : null;
    }

    @Override
    public void onDisable() {
        this.hasItemHandler2.cancel();
        this.process24();
        this.process17();
        this.process13();
        PlayerHelper13.reset();
        this.lookupMap.clear();
        this.process6();
        KeybindStateUtil.process();
        this.process5();
        this.process22();
        this.hasItemHandler.cancel();
        if ((Brutality.moduleManager == null ? null : Brutality.moduleManager.computeModule("AutoGrinder")) instanceof AutoGrinder autoGrinder) {
            autoGrinder.process45();
            this.handleAutoGrinder(autoGrinder);
        }

        this.flag15 = false;
        this.flag16 = false;
        this.flag18 = false;
        this.flag19 = false;
        this.clearPathHandler2.handleFlag2(false);
        this.clearPathHandler.handleFlag2(false);
        this.handleFlag2(false);
        this.process23();
        this.process15();
        this.vec3 = null;
    }

    private void onEventRender3D(EventRender3D event) {
        this.clearPathHandler.handleFlag2(this.pathESPSetting.isEnabled());
        this.clearPathHandler2.handleFlag2(this.pathESPSetting.isEnabled());
        if (this.pathESPSetting.isEnabled()) {
            this.clearPathHandler.onEventRender3D(event);
            this.clearPathHandler2.onEventRender3D(event);
            this.hasItemHandler.onEventRender3D(event);
            this.hasItemHandler2.onEventRender3D(event);
        }
    }

    private static void handleModule(Module module, boolean flag) {
        if (module != null && module.checkState44() != flag) {
            module.handleFlag3(flag);
            if (module.checkState44() == flag) {
                ;
            }
        }
    }

    private double computeDouble() {
        return this.jumpChancePerTickSetting.computeDouble() / 100.0;
    }

    private void process14() {
        if (!this.useFocusKOSSetting.isEnabled()) {
            this.process6();
        } else {
            Focus instance = Focus.getInstance();
            KOS instance2 = KOS.getInstance();
            if (instance != null && instance2 != null) {
                if (!this.flag26) {
                    this.flag27 = instance.checkState44();
                    this.flag28 = instance2.checkState44();
                    this.flag26 = true;
                }

                instance.handleFlag3(true);
                instance2.handleFlag3(true);
                String str = this.entityPlayer != null && this.entityPlayer.worldObj == mc.theWorld && !this.entityPlayer.isDead
                    ? this.entityPlayer.getName()
                    : (!this.warSetting.matchesString2("Target") ? null : this.targetSetting.getValue().trim());
                if (str != null && str.isEmpty()) {
                    str = null;
                }

                WHelper.handleString2(str);
                instance.setText(str);
            }
        }
    }

    private boolean checkState8() {
        return mc.theWorld != null
            && mc.theWorld
                .playerEntities
                .stream()
                .anyMatch(
                    player -> {
                        if (player == null
                            || player == mc.thePlayer
                            || player.isDead
                            || player.getHealth() <= 0.0F
                            || player.isSpectator()
                            || GetDisplayedTitleHelper.matchesEntityLivingBase3(player)) {
                            return false;
                        }
                        if (mc.getNetHandler() == null || mc.getNetHandler().getPlayerInfo(player.getUniqueID()) == null) {
                            return false;
                        }
                        if (Friends.getInstance() != null && Friends.getInstance().checkState44() && DefaultHelper.matchesString(player.getName())) {
                            return false;
                        }
                        if (Truces.getInstance() != null && Truces.getInstance().checkState44() && TruceManager.matchesString(player.getName())) {
                            return false;
                        }
                        return this.megastreaksOnlySetting.isEnabled()
                            ? OverdriveType.computeOverdriveType(player.getDisplayName().getUnformattedText(), player.getName()) != null
                            : PruneExpiredCombatHelper.computeDouble(player) >= this.bountyThresholdSetting.computeDouble();
                    }
                );
    }

    private ReleaseHandler computeReleaseHandler() {
        if (this.releaseHandler == null) {
            this.releaseHandler = new ReleaseHandler();
        }

        return this.releaseHandler;
    }

    private void process11() {
        if (this.flag23 && mc.gameSettings != null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), Mouse.isButtonDown(0));
            this.flag23 = false;
        }
    }

    private void handleFlag2(boolean flag) {
        int keyCode = mc.gameSettings.keyBindUseItem.getKeyCode();
        KeyBinding.setKeyBindState(keyCode, flag);
        if (flag) {
            KeyBinding.onTick(keyCode);
        }
    }

    private void process12() {
        String text = this.killAuraSetting.getText();
        if (this.text3 != null && !this.text3.equalsIgnoreCase(text)) {
            this.process5();
        }

        this.flag22 = false;
        if (this.killAuraSetting.matchesString2("KillAura")) {
            AutoClicker.y = false;
            KillAura.y = true;
            KillAura instance = KillAura.getInstance();
            if (instance != null) {
                if (!this.flag25) {
                    this.entityLivingBase = KillAura.entityLivingBase3;
                    this.flag24 = KillAura.flag50;
                    this.flag25 = true;
                }

                if (!instance.checkState44()) {
                    handleModule(instance, true);
                    this.flag20 = instance.checkState44();
                }

                this.computeReleaseHandler().handleKillAura(instance, this.autoblockSetting.isEnabled());
                KillAura.entityLivingBase3 = this.entityPlayer;
                KillAura.flag50 = true;
                this.handleKillAura(instance);
            }
        } else {
            this.process22();
            KillAura.y = false;
            this.process11();
            AutoClicker.y = true;
            AutoClicker instance2 = AutoClicker.instance;
            if (instance2 != null && !instance2.checkState44()) {
                instance2.handleFlag3(true);
                this.flag21 = true;
            }
        }

        this.text3 = text;
    }

    private BlockPos computeBlockPos() {
        double d = this.random.nextDouble() * Math.PI * 2.0;
        double d2 = 28.0 + this.random.nextDouble() * 12.0;
        int n = (int)Math.round(Math.cos(d) * d2);
        int n2 = (int)Math.round(Math.sin(d) * d2);
        return new BlockPos(n, mc.thePlayer.getPosition().getY(), n2);
    }

    private EntityPlayer selectWarTarget() {
        var players = mc.theWorld.playerEntities.stream().filter(this::matchesEntityPlayer).collect(Collectors.toList());
        var players2 = players.stream().filter(player -> {
            if (!this.matchesEntityPlayer(player)) {
                return false;
            }
            boolean flag = SomberHelper.matchesEntityPlayer3(player) || GetMysticEnchantLoreHelper.matchesEntityPlayer3(player);
            return this.darksSetting.isEnabled() && flag || this.shopsSetting.isEnabled() && HuntingHandler.matchesEntityPlayer(player);
        }).collect(Collectors.toList());
        List<EntityPlayer> players3 = players2.isEmpty() ? players : players2;
        IdentityHashMap<EntityPlayer, Double> identityHashMap = new IdentityHashMap<>();

        for (EntityPlayer player2 : players3) {
            identityHashMap.put(player2, this.computeDouble(player2));
        }

        EntityPlayer player3 = players3.stream()
            .min(Comparator.<EntityPlayer>comparingDouble(player4 -> (Double)identityHashMap.get(player4)).thenComparingInt(Entity::getEntityId))
            .orElse(null);
        return player3 != null
                && this.entityPlayer != null
                && identityHashMap.containsKey(this.entityPlayer)
                && this.smartSetting.matchesString2("Smart")
                && identityHashMap.get(this.entityPlayer) <= identityHashMap.get(player3) * 1.1
            ? this.entityPlayer
            : player3;
    }

    private String computeString(EntityPlayer player) {
        ItemStack[] armorInventory = player.inventory.armorInventory;
        int n = 0;
        int n2 = 0;
        boolean flag = false;
        boolean flag2 = false;
        boolean flag3 = false;
        boolean flag4 = false;

        for (int i = 0; i < armorInventory.length; i++) {
            ItemStack stack = armorInventory[i];
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                n++;
                ItemArmor item = (ItemArmor)stack.getItem();
                switch (item.getArmorMaterial()) {
                    case DIAMOND:
                        flag = true;
                        break;
                    case IRON:
                        n2++;
                        break;
                    case CHAIN:
                        flag2 = true;
                        break;
                    case LEATHER:
                        if (i == 1) {
                            flag3 = true;
                        }

                        if (i == 0) {
                            flag4 = true;
                        }
                }
            }
        }

        if (n == 0) {
            return "Naked";
        }
        if (GetMysticEnchantLoreHelper.matchesEntityPlayer9(player)) {
            return "Bhunt";
        }
        if (flag && GetMysticEnchantLoreHelper.matchesEntityPlayer3(player)) {
            return "Darks";
        }
        if (flag) {
            return "Shop";
        }
        if (flag3 && flag4) {
            return "Set";
        }
        if (n2 >= 3) {
            return "Iron Pack";
        }
        return flag2 ? "Chain" : "Shop";
    }
}
