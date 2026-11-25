package org.brutality.module.impl.pit.hypixel.automation;

import java.awt.Color;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.Brutality;
import org.brutality.automation.grinding.inventory.ClearPathHandler;
import org.brutality.automation.grinding.inventory.OnSpireFloorChangeHandler;
import org.brutality.automation.grinding.inventory.SilentHandler;
import org.brutality.automation.grinding.lifecycle.CancelHandler;
import org.brutality.automation.grinding.lifecycle.InvalidateHandler;
import org.brutality.automation.grinding.lifecycle.LifecycleHandler;
import org.brutality.automation.grinding.lifecycle.LifecycleHandler2;
import org.brutality.automation.grinding.lifecycle.OtherType;
import org.brutality.automation.grinding.movement.ChooseTargetPositionHandler;
import org.brutality.automation.grinding.movement.ObsidianPlacerHandler;
import org.brutality.automation.grinding.movement.ClearPathHandler3;
import org.brutality.automation.grinding.movement.MovementHandler;
import org.brutality.automation.grinding.movement.MovementHandler2;
import org.brutality.automation.grinding.purchases.HasItemHandler;
import org.brutality.automation.grinding.targeting.TargetingHandler;
import org.brutality.automation.mining.FenceHandler;
import org.brutality.core.ConnectToServerHandler;
import org.brutality.core.CoreHandler;
import org.brutality.core.CoreHandler2;
import org.brutality.core.CoreHandler4;
import org.brutality.core.CoreHandler5;
import org.brutality.core.DisplayNameHandler2;
import org.brutality.core.ExtraAttributesHandler;
import org.brutality.core.OnClientChatReceivedHandler;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.attack.AttackEvent;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.pit.MajorFinishedEvent;
import org.brutality.event.impl.pit.PitEvent;
import org.brutality.event.impl.pit.PitEvent10;
import org.brutality.event.impl.pit.PitEvent11;
import org.brutality.event.impl.pit.PitEvent12;
import org.brutality.event.impl.pit.PitEvent13;
import org.brutality.event.impl.pit.PitEvent16;
import org.brutality.event.impl.pit.PitEvent17;
import org.brutality.event.impl.pit.PitEvent2;
import org.brutality.event.impl.pit.PitEvent3;
import org.brutality.event.impl.pit.PitEvent4;
import org.brutality.event.impl.pit.PitEvent5;
import org.brutality.event.impl.pit.PitEvent6;
import org.brutality.event.impl.pit.PitEvent7;
import org.brutality.event.impl.pit.PitEvent8;
import org.brutality.event.impl.pit.PitEvent9;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.player.GetEntityEvent;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.event.impl.render.GetEntityEvent3;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.combat.AutoClicker;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.pit.hypixel.hud.TDM;
import org.brutality.module.impl.pit.orion.utility.OrionAntiVanish;
import org.brutality.module.impl.player.AutoHeal;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;
// VENDOR-DISABLED: import org.brutality.system.EntitlementState;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.util.BEASTHelper;
import org.brutality.util.ComputeScheduledFutureHelper;
import org.brutality.util.ExecuteCommandHelper;
import org.brutality.util.GetDisplayedTitleHelper;
import org.brutality.util.GetNonBotPlayersHelper;
import org.brutality.util.PartyWarpType;
import org.brutality.util.PizzaType;
import org.brutality.util.PlaySoundHelper;
import org.brutality.util.UtilHelper;
import org.brutality.util.KeybindStateUtil;
import org.brutality.util.UtilHelper4;
import org.brutality.util.UtilHelper5;
import org.brutality.util.UtilHelper7;
import org.brutality.util.StopwatchTimer;
import org.brutality.util.contract.Contract;
import org.brutality.util.contract.IsVileContractChoiceHelper;
import org.brutality.util.contract.KillsType;
import org.brutality.util.inventory.GetHoveredSlotHelper;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;
import org.brutality.util.inventory.GoldenHeadsType;
import org.brutality.util.inventory.HelmetHelper;
import org.brutality.util.inventory.MysticItemInfo;
import org.brutality.util.inventory.NoSwapsNeededHelper;
import org.brutality.util.inventory.OnClientTickHelper;
import org.brutality.util.inventory.SecondGappleType;
import org.brutality.util.math.BuildTargetComparatorHelper;
import org.brutality.util.module.FourSeasonsType;
import org.brutality.util.render.RenderHelper;
import org.brutality.util.render.RenderHelper2;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.network.ServerUtils;
import org.brutality.utils.player.PlayerHelper12;
import org.brutality.utils.player.PlayerHelper13;
import org.lwjgl.input.Mouse;

public class AutoGrinder extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static AutoGrinder instance;
    private Module module;
    private final BooleanSetting smoothCameraSetting;
    public final PitCategory pitCategory = PitCategory.PREMIUM;
    private final ModeSetting pitOutskirtsSetting;
    private final BooleanSetting stopAfterDeathSetting = new BooleanSetting("Stop After Death", this, false);
    private final NumberSetting deathStopStreakSetting = new NumberSetting("Death Stop Streak", this, 500.0, 1.0, 10000.0, 1.0)
        .computeNumberSetting(() -> this.stopAfterDeathSetting.isEnabled());
    private final LifecycleHandler lifecycleHandler = new LifecycleHandler();
    private boolean flag;
    private InvalidateHandler invalidateHandler;
    private boolean flag2 = true;
    private final LifecycleHandler2 lifecycleHandler2 = new LifecycleHandler2(new Random());
    private EntityItem entityItem;
    private long timestamp;
    private long timestamp2;
    private long timestamp3;
    private double amount;
    private final Set<Integer> itemSet = new HashSet<>();
    private final ModeSetting randomSetting;
    private final BooleanSetting scheduleSetting;
    private final NumberSetting scheduleStartHourSetting;
    private final NumberSetting scheduleStopHourSetting;
    private final NumberSetting autoPerkFrequencySetting;
    private final BooleanSetting allowSwapLobbiesSetting;
    private int count;
    private final ModeSetting majoreventsonlySetting;
    private final BooleanSetting debugManualStateSetting;
    public final ModeSetting fightorflightSetting;
    private final BooleanSetting disconnectWhileWaitingSetting = new BooleanSetting("Disconnect While Waiting", this, false);
    private final NumberSetting joinBeforeMinutesSetting = new NumberSetting("Join Before Minutes", this, 2.0, 0.0, 15.0, 0.5);
    private final CancelHandler cancelHandler = new CancelHandler();
    private final BooleanSetting randomAFKWhenWaitingSetting;
    private final NumberSetting banAFKLengthMinsSetting;
    private final NumberSetting minLobbyPlayersSetting;
    private boolean flag3;
    public final BooleanSetting buyDamageReductionSetting;
    public final BooleanSetting doBeastEventSetting;
    private BlockPos blockPos;
    private final StopwatchTimer utilHelper8;
    private boolean flag4;
    private final StopwatchTimer utilHelper82;
    private final StopwatchTimer utilHelper83;
    private final NumberSetting fpsLimitSetting;
    private final BooleanSetting ignoreDiamondsSetting;
    private final BooleanSetting chaseOutOfPITSetting;
    public final BooleanSetting diamondIfLookingSetting;
    private static final Map<String, String> lookupMap = DisplayNameHandler2.lookupMap;
    private final BooleanSetting debugOverlaySetting;
    private final BooleanSetting smartPerkCooldownSetting;
    private final StopwatchTimer utilHelper84;
    private int count2;
    public final UtilHelper7 utilHelper7;
    private final StopwatchTimer utilHelper85;
    public final ModeSetting goldenheadsSetting;
    public final BooleanSetting buyGoldBoostSetting;
    private final BooleanSetting doKOTHSetting;
    public final BooleanSetting doRobberyEventSetting;
    private final StopwatchTimer utilHelper86;
    private final NumberSetting minPitPlayersToRunSetting;
    private int count3;
    public final ModeSetting gladiatorSetting;
    private Vec3 vec3;
    private int count4;
    private int count5;
    private final StopwatchTimer utilHelper87;
    private int count6;
    private final StopwatchTimer utilHelper88;
    private final BooleanSetting safeJumpSetting;
    private boolean dy;
    private float factor;
    private final NumberSetting minPlayersSetting;
    public final NumberSetting yawSpeedSetting;
    private final BooleanSetting dropDownSetting;
    private final BooleanSetting donTAttackWhileFallingSetting;
    private final BooleanSetting ignoreWhileRidingSetting;
    private final ModeSetting onlynearbhunterSetting;
    private final StopwatchTimer utilHelper89;
    private final StopwatchTimer utilHelper810;
    private final StopwatchTimer utilHelper811;
    private final NumberSetting limitFOVSetting;
    public final BooleanSetting doRagePitEventSetting;
    private final BooleanSetting doKillContractsSetting;
    private final StopwatchTimer utilHelper812;
    private final StopwatchTimer utilHelper813;
    private final BooleanSetting takeScheduledBreaksSetting;
    private final NumberSetting breakEveryMinsSetting;
    private final NumberSetting breakLengthMinsSetting;
    private boolean dz;
    private long timestamp4;
    private long timestamp5;
    private boolean flag5;
    private String text;
    public final StopwatchTimer utilHelper814;
    public final BooleanSetting doBlockheadEventSetting;
    public final BooleanSetting doSquadsEventSetting;
    private final StopwatchTimer utilHelper815;
    private boolean flag6;
    private final StopwatchTimer utilHelper816;
    private static final StopwatchTimer utilHelper817 = new StopwatchTimer();
    private final BooleanSetting cancelSoundSetting;
    private final StopwatchTimer utilHelper818;
    private final ModeSetting huntenemiesSetting;
    private final StopwatchTimer utilHelper819;
    private final StopwatchTimer utilHelper820;
    private OtherType otherType;
    private final RenderHelper2 renderHelper2;
    private final StopwatchTimer utilHelper821;
    public final NumberSetting strafeChancePerTickSetting;
    private int count7;
    private final StopwatchTimer utilHelper822;
    private final StopwatchTimer utilHelper823;
    private final BooleanSetting doSneakKillsContractsSetting;
    private final BooleanSetting cancelRenderingSetting;
    private EntityPlayer player;
    private boolean flag7;
    private final BooleanSetting enablePerformanceSettingsSetting;
    private final BooleanSetting doDiamondSwordKillsContractsSetting;
    private final BooleanSetting autoPerkSetting;
    public final NumberSetting jumpChancePerTickSetting;
    public final ModeSetting safetyfirstSetting;
    private final StopwatchTimer utilHelper824;
    private final StopwatchTimer utilHelper825;
    private final StopwatchTimer utilHelper826;
    private final StopwatchTimer utilHelper827;
    private OtherType otherType2;
    public final NumberSetting pitchSpeedSetting;
    public final BooleanSetting buyElGatoSetting;
    public final BooleanSetting buyKillstreaksSetting;
    private final Queue<Integer> queue;
    private final NumberSetting afkChanceOnSpawnSetting;
    private int count8;
    private final ClearPathHandler clearPathHandler;
    private final ModeSetting disableanddisconnectSetting;
    private final UtilHelper utilHelper;
    private final BooleanSetting debugTargetSetting;
    public final ModeSetting feastSetting;
    public final BooleanSetting buyDamageBoostSetting;
    public boolean flag8;
    private boolean flag9;
    private final StopwatchTimer utilHelper828;
    private final StopwatchTimer utilHelper829;
    private final StopwatchTimer utilHelper830;
    private final BooleanSetting skipRenderWorldSetting;
    public final BooleanSetting disableOnLaunchSetting;
    private int count9;
    private EntityPlayer entityPlayer;
    private final BooleanSetting lowLifeCheckSetting;
    private BlockPos blockPos2;
    private final StopwatchTimer utilHelper831;
    private final NumberSetting spawnYLevelSetting;
    public final BooleanSetting doSpireEventSetting;
    public final BooleanSetting doRaffleEventSetting;
    private final BooleanSetting spirePerksSetting;
    private final ModeSetting goldenheadsSetting2;
    private final ModeSetting strengthchainingSetting;
    private final ModeSetting safetyfirstSetting2;
    private final ModeSetting gladiatorSetting2;
    private final OnSpireFloorChangeHandler onSpireFloorChangeHandler;
    private final StopwatchTimer utilHelper832;
    private final BooleanSetting pickUpMysticDropsSetting;
    private final ModeSetting disableanddisconnectSetting2;
    private boolean flag10;
    private final StopwatchTimer utilHelper833;
    private final NumberSetting disableAfterMinsSetting;
    private final NumberSetting maxTargetPITRangeSetting;
    private final ModeSetting disableanddisconnectSetting3;
    public final BooleanSetting ignoreFriendsSetting;
    private final StopwatchTimer utilHelper834;
    private static final double amount2 = 4.0;
    private static final int count10 = 2;
    private static final long timestamp6 = 20000L;
    private static final long timestamp7 = 10000L;
    private static final Pattern pattern = Pattern.compile("MEGASTREAK!.*?\\[\\d+\\]\\s+(\\S+)\\s+activated\\s+MAGNUM\\s+OPUS");
    private final BooleanSetting doChainKillContractsSetting;
    private final NumberSetting limitRenderDistanceSetting;
    private Vec3 vec32;
    private Vec3 vec33;
    private GuiScreen previousScreen;
    private final StopwatchTimer utilHelper835;
    private final BooleanSetting oofOnBadPathSetting;
    private final BooleanSetting oofOnNoHitSetting;
    private final NumberSetting oofNoHitSecondsSetting;
    public final BooleanSetting doTDMEventSetting;
    private final StopwatchTimer utilHelper836;
    private final StopwatchTimer utilHelper837;
    private final StopwatchTimer utilHelper838 = new StopwatchTimer();
    private final StopwatchTimer utilHelper839;
    private final StopwatchTimer utilHelper840;
    private final StopwatchTimer utilHelper841;
    private final StopwatchTimer utilHelper842;
    private final StopwatchTimer utilHelper843;
    private final StopwatchTimer utilHelper844;
    private int count11;
    private MovementHandler.MidMode midMode;
    private boolean flag11;
    private final org.brutality.automation.grinding.purchases.CancelHandler cancelHandler2;
    private final ModeSetting onalwaysdropSetting;
    private final NumberSetting mysticPickupDistanceSetting;
    private final NumberSetting banResponseMaxDelaySetting;
    private final ModeSetting killAuraSetting;
    private final BooleanSetting hudSetting;
    private final NumberSetting hudxSetting;
    private final NumberSetting hudySetting;
    private int count12;
    private int count13;
    private int count14;
    private int count15;
    private int count16;
    private String text2 = "";
    private long timestamp8;
    private float factor2;
    private float factor3;
    private final NumberSetting raffleMaxTicketsSetting;
    private int count17;
    private int count18;
    private int count19;
    private int count20;
    private final org.brutality.automation.navigation.ClearPathHandler clearPathHandler2 = new org.brutality.automation.navigation.ClearPathHandler(0.4);
    private boolean flag12;
    private boolean flag13;
    private boolean flag14;
    private String text3;
    private String text4;
    private String text5;
    private String text6;
    public final BooleanSetting buyXPBoostSetting;
    private final StopwatchTimer utilHelper845;
    private final BooleanSetting doNakedKillsContractsSetting;
    public final NumberSetting windowClickDelaySetting;
    private final BooleanSetting autoBuySetting;
    private final ModeSetting everyMinsSetting;
    private final NumberSetting autoBuyEveryMinsSetting;
    private final KeybindListSetting obsidian64Setting;
    private final HasItemHandler hasItemHandler;
    public final ModeSetting expliciousSetting;
    private final BooleanSetting doKillStreakContractsSetting;
    private final NumberSetting attackRangeSetting;
    private int count21;
    private final BooleanSetting useFreshPantsSetting;
    private final ModeSetting silentSetting;
    private final NumberSetting freshPantsDelaySetting;
    private final BooleanSetting freshPantsSafeModeSetting;
    public final ModeSetting strengthchainingSetting2;
    private int count22;
    private final NumberSetting banResponseMinDelaySetting;
    private final BooleanSetting oofWhenContractCompletedSetting;
    private final SilentHandler silentHandler;
    private boolean flag15;
    private final BooleanSetting soundAlertsSetting;
    private final BooleanSetting doClaimBountiesContractsSetting;
    private final BooleanSetting doGoldContractsSetting;
    private final StopwatchTimer utilHelper846;
    private final Map<PizzaType, BooleanSetting> lookupMap2;
    private final NumberSetting targetSwitchDelaySetting;
    private final NumberSetting fovSetting;
    private final KeybindListSetting chatAlertsSetting;
    private final BooleanSetting respondToNameSetting;
    private final NumberSetting responseMinDelaySecSetting;
    private final NumberSetting responseMaxDelaySecSetting;
    private final ModeSetting customSetting;
    private final StringSetting sBroAmDoinSum1Setting;
    private final ModeSetting disconnectSetting;
    private final KeybindListSetting teleportCheckSetting;
    private final BooleanSetting checkForTeleportsSetting;
    private final ModeSetting disconnectSetting2;
    private final KeybindListSetting orionVanishCheckerSetting;
    private final BooleanSetting pauseOnOrionVanishSetting;
    private final ModeSetting oofSetting;
    private final StopwatchTimer utilHelper847;
    private boolean flag16;
    private double amount3;
    private double amount4;
    private double amount5;
    private boolean flag17;
    private long timestamp9;
    private final KeybindListSetting combatSetting;
    private final KeybindListSetting movementSetting;
    private final KeybindListSetting grindingSetting;
    private final KeybindListSetting contractsSetting;
    private final KeybindListSetting perksAndKillstreaksSetting;
    private final KeybindListSetting lobbyAndSafetySetting;
    private final KeybindListSetting performanceSetting;
    private final KeybindListSetting hudAndDebugSetting;
    private final KeybindListSetting majorEventsSetting;
    private final KeybindListSetting safetyAndRecoverySetting;
    private static final List<String> entries = Arrays.stream(GoldenHeadsType.values())
        .filter(goldenHeadsType -> goldenHeadsType != GoldenHeadsType.NO_PERK_SLOT && goldenHeadsType != GoldenHeadsType.GO_BACK_SLOT)
        .map(Enum::name)
        .collect(Collectors.toList());
    private static final List<String> entries2 = Arrays.stream(SecondGappleType.values())
        .filter(secondGappleType -> secondGappleType != SecondGappleType.NO_KILLSTREAK_BUTTON && secondGappleType != SecondGappleType.GO_BACK_BUTTON)
        .map(Enum::name)
        .collect(Collectors.toList());
    private final List<String> entries3 = new ArrayList<>();
    private final StopwatchTimer utilHelper848 = new StopwatchTimer();
    private boolean flag18;
    private boolean flag19 = false;
    private int count23 = 0;

    public void onGetEntityEvent3(GetEntityEvent3 getEntityEvent3) {
    }

    @Override
    public void onRender() {
        if (this.hudSetting.isEnabled()) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                boolean flag = mc.currentScreen instanceof GuiChat;
                FontRenderer fontRenderer = computeFontRenderer(AutoGrinder.class);
                double d = this.hudxSetting.computeDouble();
                double d2 = this.hudySetting.computeDouble();
                int n = fontRenderer.FONT_HEIGHT + 1;
                String str = this.checkState44() ? "§a" + this.otherType.name().replace('_', ' ') : "§cOff";
                String str2 = "§c§lAutoGrinder §7[" + str + "§7]";
                String str3 = "§7Location: §f" + FreshHelper.computeString(mc.thePlayer, mc.theWorld);
                String str4 = "§7Mode: §f" + this.killAuraSetting.getText();
                String str5 = "§7Sort: §f" + this.huntenemiesSetting.getText();
                String str6 = "§7Target: §f" + (this.player != null ? this.player.getName() : "None");
                int n2 = this.count15;
                int n3 = this.count16;
                String str7 = "§7Kills: §f" + n2;
                String str8 = "§7Assists: §f" + n3;
                String str9 = "§7XP: §f" + String.format("%.0f", this.factor2);
                String str10 = "§7Gold: §f" + String.format("%.1f", this.factor3);
                String str11 = "§7Mystics: §f" + this.count13;
                String str12 = "§7Opus: §f" + this.count14;
                String str13 = "§7Total Collects: §f" + this.count17;
                String str14 = "§7Total Deposited: §f" + this.count18;
                String str15 = "§7Total Lost: §f" + this.count19;
                long l = this.utilHelper812.computeLong();
                long l2 = l / 1000L % 60L;
                long l3 = l / 60000L % 60L;
                long l4 = l / 3600000L;
                String str16 = "§7Duration: §f" + String.format("%02d:%02d:%02d", l4, l3, l2);
                String str17;
                if (!this.takeScheduledBreaksSetting.isEnabled()) {
                    str17 = "§7Breaks: §cOff";
                } else if (this.dz) {
                    long l5 = Math.max(0L, this.timestamp4 - System.currentTimeMillis());
                    str17 = "§7Break remaining: §b" + computeString(l5);
                } else {
                    long l6 = (long)this.breakEveryMinsSetting.computeDouble() * 60000L;
                    long l7 = Math.max(0L, l6 - this.utilHelper813.computeLong());
                    str17 = "§7Next break: §b" + computeString(l7);
                }

                String[] parts = this.checkState8()
                    ? new String[]{str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, str13, str14, str15, str16, str17}
                    : new String[]{str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, str16, str17};
                int n4 = 0;

                for (String str18 : parts) {
                    int textWidth = fontRenderer.getStringWidth(str18);
                    if (textWidth > n4) {
                        n4 = textWidth;
                    }
                }

                int n5 = parts.length * n;
                if (flag) {
                    int[] ints = HudDragHelper.computeIntArray();
                    int n6 = ints[0];
                    int n7 = ints[1];
                    if (Mouse.isButtonDown(0)) {
                        if (!HudDragHelper.matchesObject(this) && HudDragHelper.matchesInt(n6, n7, d, d2, n4, n5)) {
                            HudDragHelper.matchesObject(this, n6, n7, (int)d, (int)d2);
                        }
                    } else {
                        HudDragHelper.handleObject(this);
                    }

                    if (HudDragHelper.matchesObject(this)) {
                        int[] ints2 = HudDragHelper.computeIntArray(this);
                        if (ints2 != null) {
                            this.hudxSetting.handleDouble2(ints2[0]);
                            this.hudySetting.handleDouble2(ints2[1]);
                        }
                    }
                } else {
                    HudDragHelper.handleObject(this);
                }

                Gui.drawRect((int)d - 2, (int)d2 - 2, (int)d + n4 + 2, (int)d2 + n5 + 2, new Color(0, 0, 0, 120).getRGB());

                for (int i = 0; i < parts.length; i++) {
                    fontRenderer.drawStringWithShadow(parts[i], (float)d, (float)(d2 + i * n), 16777215);
                }
            }
        }
    }

    private void scanMysticDrops(long l) {
        if (mc.theWorld != null && mc.thePlayer != null) {
            this.itemSet.removeIf(integer -> mc.theWorld.getEntityByID(integer) == null);
            this.handleLong(l);
            if (this.entityItem == null && this.pickUpMysticDropsSetting.isEnabled() && !GetMysticEnchantLoreHelper.checkState3()) {
                EntityItem entity = GetNonBotPlayersHelper.computeList3()
                    .stream()
                    .filter(entity2 -> this.matchesEntityItem(entity2, l))
                    .filter(entity3 -> entity3.getDistanceToEntity(mc.thePlayer) <= this.mysticPickupDistanceSetting.computeDouble())
                    .filter(entity4 -> BuildTargetComparatorHelper.matchesEntityPlayer(mc.thePlayer, entity4.getPositionVector()))
                    .min(Comparator.comparingDouble(entity5 -> entity5.getDistanceToEntity(mc.thePlayer)))
                    .orElse(null);
                if (entity != null) {
                    this.entityItem = entity;
                    this.timestamp2 = this.timestamp3 = l;
                    this.amount = entity.getDistanceToEntity(mc.thePlayer);
                    this.vec32 = entity.getPositionVector();
                }
            }
        } else {
            this.process44();
        }
    }

    public void process31() {
        this.utilHelper85.reset();
        this.utilHelper89.reset();
        mc.thePlayer.closeScreen();
        this.count2 = BEASTHelper.computeInt2();
    }

    public void onEventRender3D(EventRender3D event) {
        if (mc.thePlayer != null) {
            if (this.checkState17()) {
                BlockPos blockPos = this.getBlockPos();
                if (blockPos != null) {
                    float x = blockPos.getX();
                    float y = blockPos.getY();
                    float z = blockPos.getZ();
                    UtilHelper4.handleDouble(x, y, z, 1.0F, event.getFactor(), this.renderHelper2);
                    UtilHelper4.handleDouble(x, y, z, event.getFactor(), this.renderHelper2);
                }
            }
        }
    }

    private static boolean checkState32() {
        return hasSelfCheckoutEnchant(mc.thePlayer.inventory.armorInventory[1]);
    }

    public void onPlayerKilled(PitEvent12 pitEvent12) {
        this.utilHelper7.process();
    }

    private boolean checkState26() {
        Contract contract = OnClientChatReceivedHandler.computeContract();
        if (contract == null) {
            return false;
        }
        if (this.matchesContract(contract)) {
            return contract.getKillsType() == KillsType.COLLECT_GOLD_INGOTS ? true : contract.isFlag();
        }
        return false;
    }

    private void handleContractSelection() {
        if (!this.checkState12()) {
            AutoGrinder.OffMode offMode = AutoGrinder.OffMode.valueOf(this.onalwaysdropSetting.getText());
            if (offMode == AutoGrinder.OffMode.OFF) {
                this.flag15 = true;
                this.handleString5("Auto contract is disabled, not picking a contract.");
            } else {
                if (offMode == AutoGrinder.OffMode.ON_ONLY_DROP_FOR_DOABLE_CONTRACTS && OnClientChatReceivedHandler.checkState()) {
                    if (mc.currentScreen != null) {
                        mc.thePlayer.closeScreen();
                    }

                    KeybindStateUtil.process();
                    if (!this.checkState26()) {
                        this.process35();
                        if (this.randomAFKWhenWaitingSetting.isEnabled()) {
                            this.clearPathHandler.handleFlag(this.checkState18());
                            return;
                        }

                        return;
                    }

                    this.flag15 = true;
                }

                if (OnClientChatReceivedHandler.checkState()) {
                    if (mc.currentScreen != null) {
                        mc.thePlayer.closeScreen();
                    }

                    this.flag15 = true;
                    this.handleString5("Already have an active contract, skipping picking a new one.");
                } else if (!this.utilHelper811.matchesLong(3600000L)) {
                    this.flag15 = true;
                    this.handleString5("Skipping going to contractor, recently detected daily contracts have run out.");
                } else if (!this.utilHelper824.matchesLong(3600000L)) {
                    this.flag15 = true;
                    this.handleString5("Skipping going to contractor, recently we are too low level.");
                } else if (!this.utilHelper829.matchesLong(600000L)) {
                    this.flag15 = true;
                    this.handleString5("Skipping going to contractor, recently detected we are too broke for contracts.");
                } else {
                    BlockPos pos = GetNonBotPlayersHelper.computeBlockPos3();
                    if (pos == null) {
                        this.flag15 = true;
                        this.handleString5("Failed to find contract villager, skipping contract.");
                    } else {
                        double dist = mc.thePlayer.getDistance(pos.getX(), pos.getY(), pos.getZ());
                        if (mc.currentScreen == null) {
                            ClearPathHandler3.handleFlag(this.checkState18());
                        }

                        if (!(dist > 3.5)) {
                            if (mc.currentScreen == null) {
                                process27();
                                this.utilHelper810.reset();
                            } else {
                                KeybindStateUtil.process();
                                if (this.utilHelper810.matchesLong(750L)) {
                                    if (!GetHoveredSlotHelper.matchesGuiScreen7(mc.currentScreen)) {
                                        if (GetHoveredSlotHelper.matchesGuiScreen6(mc.currentScreen)) {
                                            MessageUtils.x(
                                                String.valueOf(new StringBuilder("2.").append(EnumChatFormatting.YELLOW).append(" Confirming contract..."))
                                            );
                                            int n = GetHoveredSlotHelper.computeInt("confirm");
                                            if (n != -1) {
                                                HelmetHelper.handleInt2(n);
                                                this.utilHelper810.reset();
                                            }
                                        } else if (!GetHoveredSlotHelper.matchesGuiScreen11(mc.currentScreen)) {
                                            if (GetHoveredSlotHelper.matchesGuiScreen9(mc.currentScreen)) {
                                                MessageUtils.x(
                                                    String.valueOf(
                                                        new StringBuilder("4.").append(EnumChatFormatting.YELLOW).append(" Final confirming contract...")
                                                    )
                                                );
                                                int n2 = GetHoveredSlotHelper.computeInt("accept contract");
                                                if (n2 != -1) {
                                                    HelmetHelper.handleInt2(n2);
                                                    this.utilHelper810.reset();
                                                    this.utilHelper89.reset();
                                                    this.handleString5("Done picking a contract.");
                                                    if (offMode == AutoGrinder.OffMode.ON_ALWAYS_DROP) {
                                                        this.flag15 = true;
                                                    }

                                                    if (offMode == AutoGrinder.OffMode.ON_ONLY_DROP_FOR_DOABLE_CONTRACTS && this.checkState26()) {
                                                        this.flag15 = true;
                                                    }

                                                    this.clearPathHandler.clearPath();
                                                    this.clearPathHandler.process();
                                                }
                                            }
                                        } else if (this.utilHelper810.matchesLong(3500L)) {
                                            MessageUtils.x(
                                                String.valueOf(new StringBuilder("3.").append(EnumChatFormatting.YELLOW).append(" Selecting a contract..."))
                                            );
                                            var contracts = IsVileContractChoiceHelper.computeList4(mc.currentScreen)
                                                .stream()
                                                .filter(this::matchesContract)
                                                .collect(Collectors.toList());
                                            Contract contract;
                                            if (contracts.isEmpty()) {
                                                contract = IsVileContractChoiceHelper.computeList3(mc.currentScreen)
                                                    .stream()
                                                    .filter(this::matchesContract)
                                                    .min(Comparator.comparingInt(Contract::getPriority))
                                                    .orElse(null);
                                                if (contract == null) {
                                                    contract = IsVileContractChoiceHelper.computeContract(mc.currentScreen);
                                                }

                                                MessageUtils.x(
                                                    String.valueOf(
                                                        new StringBuilder("No doable vile contracts found, picking best non-vile: ")
                                                            .append(contract != null ? contract.getText() : "null")
                                                    )
                                                );
                                            } else {
                                                contract = (Contract)contracts.get(0);
                                            }

                                            if (contract == null) {
                                                MessageUtils.x("Contract to do is null, cannot pick a contract.");
                                            } else {
                                                int slotId = contract.getSlotId();
                                                if (slotId != -1) {
                                                    OnClientChatReceivedHandler.handleContract(contract);
                                                    Contract contract2 = contract;
                                                    ComputeScheduledFutureHelper.processRunnable(
                                                        () -> OnClientChatReceivedHandler.handleContract(contract2), 200L, TimeUnit.MILLISECONDS
                                                    );
                                                    HelmetHelper.handleInt2(slotId);
                                                    this.utilHelper810.reset();
                                                }
                                            }
                                        }
                                    } else {
                                        boolean flag = GetHoveredSlotHelper.computeInt("active contract") != -1;
                                        boolean flag2 = IsVileContractChoiceHelper.matchesGuiScreen2(mc.currentScreen);
                                        boolean flag3 = IsVileContractChoiceHelper.matchesGuiScreen3(mc.currentScreen);
                                        var integers = IsVileContractChoiceHelper.computeList2(mc.currentScreen);
                                        if (this.count22 < integers.size()) {
                                            int n3 = (Integer)integers.get(this.count22);
                                            if (n3 != -1) {
                                                HelmetHelper.handleInt2(n3);
                                                this.utilHelper810.reset();
                                                MessageUtils.x(
                                                    String.valueOf(
                                                        new StringBuilder()
                                                            .append(this.count22 + 1)
                                                            .append(".")
                                                            .append(EnumChatFormatting.YELLOW)
                                                            .append(" Clicking on quest paper...")
                                                    )
                                                );
                                            }

                                            this.count22++;
                                        } else if (flag) {
                                            this.flag15 = true;
                                            this.handleString5("Already have an active contract, skipping picking a new one.");
                                            mc.thePlayer.closeScreen();
                                            this.utilHelper89.reset();
                                        } else if (flag2) {
                                            this.flag15 = true;
                                            this.utilHelper829.reset();
                                            this.handleString5("Not enough gold to buy a contract, skipping and avoiding the contractor for 10 minutes.");
                                            mc.thePlayer.closeScreen();
                                            this.utilHelper89.reset();
                                        } else if (IsVileContractChoiceHelper.matchesGuiScreen(mc.currentScreen)) {
                                            this.flag15 = true;
                                            this.handleString5("Contracts have run out for today, skipping picking a new one.");
                                            mc.thePlayer.closeScreen();
                                            this.utilHelper89.reset();
                                            this.utilHelper811.reset();
                                            AutoGrinder.ContinueMode continueMode = AutoGrinder.ContinueMode.valueOf(this.disableanddisconnectSetting.getText());
                                            String name = this.disableanddisconnectSetting.getName();
                                            switch (continueMode) {
                                                case DISABLE:
                                                    MessageUtils.x(
                                                        String.valueOf(
                                                            new StringBuilder("Daily contracts have run out, disabling AutoGrinder because the ")
                                                                .append(name)
                                                                .append(" setting is set to ")
                                                                .append(continueMode)
                                                        )
                                                    );
                                                    this.process32();
                                                    break;
                                                case DISABLE_AND_DISCONNECT:
                                                    this.process32();
                                                    ConnectToServerHandler.handleString(
                                                        String.valueOf(
                                                            new StringBuilder()
                                                                .append(EnumChatFormatting.AQUA)
                                                                .append("[AutoGrinder] Disconnected due to daily contracts running out at ")
                                                                .append(EnumChatFormatting.YELLOW)
                                                                .append(new Date())
                                                        )
                                                    );
                                            }
                                        } else if (flag3) {
                                            this.flag15 = true;
                                            this.handleString5("Too low level to buy a contract, skipping.");
                                            mc.thePlayer.closeScreen();
                                            this.utilHelper89.reset();
                                            this.utilHelper824.reset();
                                        } else {
                                            MessageUtils.x(
                                                String.valueOf(new StringBuilder("1.").append(EnumChatFormatting.YELLOW).append(" Picking a contract..."))
                                            );
                                            int n4 = GetHoveredSlotHelper.computeInt("big time contract");
                                            int n5 = n4;
                                            if (n4 == -1) {
                                                n5 = GetHoveredSlotHelper.computeInt("pending choice");
                                            }

                                            if (n5 != -1) {
                                                HelmetHelper.handleInt2(n5);
                                                this.utilHelper810.reset();
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

    public void onContractStarted(PitEvent6 pitEvent6) {
        if (mc.thePlayer != null) {
            if (mc.theWorld == null) {
                return;
            }

            this.handleString(String.valueOf(new StringBuilder("Started contract: ").append(pitEvent6.contract.getText())), Color.CYAN);
        }
    }

    private void process20() {
        if (!this.checkState20()) {
            this.midMode = MovementHandler.MidMode.MID;
            this.flag11 = false;
        } else if (this.pitOutskirtsSetting.matchesString2("PIT + Outskirts") && this.randomSetting.matchesString2("Random")) {
            if (this.flag2) {
                this.flag2 = false;
                boolean flag = this.lifecycleHandler2.checkState();
                List list = new ArrayList<>(MovementHandler.computeSet());
                this.handleMidMode(flag ? MovementHandler.MidMode.MID : (MovementHandler.MidMode)list.get(ThreadLocalRandom.current().nextInt(list.size())));
                this.flag11 = false;
            }
        } else if (this.utilHelper841.matchesLong(2000L)) {
            this.utilHelper841.reset();
            if (this.player == null) {
                var midModes = this.computeSet();
                if (this.randomSetting.matchesString2("Random")) {
                    this.handleSet2(midModes);
                } else {
                    this.handleSet(midModes);
                }
            }
        }
    }

    private void process15() {
        this.process12();
    }

    public void onKoth(PitEvent7 pitEvent7) {
        this.utilHelper823.reset();
        this.process26();
    }

    public BlockPos getBlockPos2() {
        return this.blockPos2;
    }

    public String[] computeStringArray() {
        return new String[]{
            this.goldenheadsSetting.getText(), this.strengthchainingSetting2.getText(), this.safetyfirstSetting.getText(), this.gladiatorSetting.getText()
        };
    }

    @Override
    public void onDisable() {
        this.computeInvalidateHandler().invalidate();
        this.process8();
        this.cancelHandler.cancel();
        this.process44();
        PlayerHelper13.reset();
        this.process3();
        this.flag13 = false;
        this.clearPathHandler2.clearPath();
        this.process12();
        mc.skipRenderWorld = false;
        this.hasItemHandler.cancel();
        this.dz = false;
        this.flag5 = false;
        this.text = null;
        long l = this.utilHelper812.computeLong();
        long l2 = l / 1000L % 60L;
        long l3 = l / 60000L % 60L;
        long l4 = l / 3600000L;
        String str = String.format("%02d:%02d:%02d", l4, l3, l2);
        int n = this.count15;
        int n2 = this.count16;
        String str2 = String.format("%.0f", this.factor2);
        String str3 = String.format("%.1f", this.factor3);
        int n3 = this.count14;
        int n4 = this.count13;
        String str4 = str3;
        String str5 = str2;
        int n5 = n2;
        int n6 = n;
        String str6 = str;
        MessageUtils.x(
            "§fSession: §c"
                + str6
                + " §7| Kills: §c"
                + n6
                + " §7| Assists: §c"
                + n5
                + " §7| XP: §c"
                + str5
                + " §7| Gold: §c"
                + str4
                + " §7| Mystics: §c"
                + n4
                + " §7| Opus: §c"
                + n3
        );
        this.count12 = 0;
        this.count13 = 0;
        this.count14 = 0;
        this.count15 = 0;
        this.count16 = 0;
        this.factor2 = 0.0F;
        this.factor3 = 0.0F;
        this.count17 = 0;
        this.count18 = 0;
        this.count19 = 0;
        this.count20 = 0;
        this.clearPathHandler2.clearPath();
        this.flag12 = false;
        this.player = null;
        this.entityPlayer = null;
        this.dy = false;
        this.midMode = MovementHandler.MidMode.MID;
        this.utilHelper841.clear();
        this.utilHelper842.clear();
        KeybindStateUtil.process();
        this.utilHelper.clear();
        this.process22();
        if (this.count3 != -1) {
            if (!this.flag4) {
                mc.gameSettings.renderDistanceChunks = this.count3;
            }

            this.count3 = -1;
        }

        if (this.flag4) {
            mc.gameSettings.fovSetting = this.factor;
            mc.gameSettings.renderDistanceChunks = this.count9;
            mc.gameSettings.limitFramerate = this.count4;
            mc.skipRenderWorld = false;
        }

        this.flag4 = false;
    }

    public boolean checkState24() {
        return ConnectToServerHandler.checkState6() ? this.matchesPizzaType(ConnectToServerHandler.getPizzaType()) : false;
    }

    public static boolean checkState15() {
        return instance != null
            && instance.checkState44()
            && !instance.flag18
            && instance.module != null
            && instance.checkState5()
            && !ObsidianPlacerHandler.flag2
            && !instance.checkState9()
            && !instance.checkState34()
            && !CoreHandler2.checkState();
    }

    private void handleString6(String str) {
        if (!ConnectToServerHandler.checkState8()) {
            if (this.utilHelper8.matchesLong(6500L)) {
                this.utilHelper8.reset();
                MessageUtils.x(str);
                PartyWarpType.HUB.handleInt(1000);
                this.handleString5("Hubbing...");
            }
        }
    }

    public void setOtherType(OtherType otherType) {
        this.otherType = otherType;
    }

    private boolean checkState12() {
        if (GetMysticEnchantLoreHelper.checkState4()) {
            return true;
        }
        if (GetMysticEnchantLoreHelper.checkState2()) {
            return true;
        }
        if (GetMysticEnchantLoreHelper.checkState10()) {
            return true;
        }
        return !GetHoveredSlotHelper.checkState2() ? GetHoveredSlotHelper.checkState() : true;
    }

    public void process10() {
        this.process33();
    }

    public BlockPos getBlockPos() {
        if (!this.utilHelper836.matchesLong(15000L)) {
            return this.blockPos;
        }
        boolean flag = this.utilHelper831.matchesLong(600000L);
        if (!flag && this.blockPos != null) {
            return this.blockPos;
        }
        this.utilHelper836.reset();
        int n = ConnectToServerHandler.computeInt4();
        BlockPos pos = new BlockPos(-200, n - 10, -200);
        BlockPos pos2 = new BlockPos(200, n + 30, 200);
        BlockPos.MutableBlockPos mutablePos = null;

        for (BlockPos.MutableBlockPos mutablePos2 : BlockPos.getAllInBoxMutable(pos, pos2)) {
            if (mutablePos2 != null) {
                IBlockState blockState = mc.theWorld.getBlockState(mutablePos2);
                if (blockState.getBlock() == Blocks.stained_glass) {
                    EnumDyeColor val = blockState.getValue(BlockStainedGlass.COLOR);
                    if (val == EnumDyeColor.BLUE || val == EnumDyeColor.LIGHT_BLUE || val == EnumDyeColor.CYAN) {
                        BlockPos[] posArray = new BlockPos[]{mutablePos2.north(4), mutablePos2.south(4), mutablePos2.east(4), mutablePos2.west(4)};
                        boolean flag2 = true;

                        for (BlockPos pos3 : posArray) {
                            if (!matchesBlockPos(pos3)) {
                                flag2 = false;
                                break;
                            }
                        }

                        if (flag2) {
                            mutablePos = mutablePos2;
                            break;
                        }
                    }
                }
            }
        }

        if (mutablePos != null) {
            this.utilHelper831.reset();
            this.blockPos = mutablePos;
            this.blockPos2 = MovementHandler2.computeBlockPos2(mc.theWorld, this.blockPos.getX(), this.blockPos.getZ());
        }

        this.handleString4(
            String.valueOf(new StringBuilder("Cached KOTH Block: ").append(this.blockPos).append(" Launcher Block: ").append(this.blockPos2))
        );
        return this.blockPos;
    }

    public static boolean hasSelfCheckoutEnchant(ItemStack stack) {
        return stack == null ? false : GetMysticEnchantLoreHelper.getMysticEnchantLore(stack).stream().anyMatch(str -> str.contains("Self-checkout"));
    }

    private MovementHandler.MidMode computeMidMode(EntityPlayer player) {
        return MovementHandler.computeMidMode(player.posX, player.posZ, 19.0);
    }

    public static void process5() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
    }

    private void process() {
        this.process24();
        if (this.checkState19()) {
            if (this.player != null) {
                this.player = null;
                this.dy = false;
                this.process22();
            }
        } else {
            boolean flag = this.player == null;
            if (!flag) {
                boolean flag2 = BuildTargetComparatorHelper.matchesEntityPlayer(mc.thePlayer, this.player);
                flag = this.utilHelper830.matchesLong((long)this.targetSwitchDelaySetting.computeDouble()) && flag2
                    || TDM.matchesEntityPlayer3(this.player)
                    || this.player.isDead
                    || ServerUtils.checkState5() && !this.player.getName().toUpperCase(Locale.ROOT).contains("CIT")
                    || this.matchesEntityPlayer(this.player)
                    || !mc.theWorld.playerEntities.contains(this.player)
                    || this.player.isInvisible()
                    || this.player.posY > mc.thePlayer.posY + 2.5
                    || this.ignoreDiamondsSetting.isEnabled() && GetMysticEnchantLoreHelper.matchesEntityPlayer10(this.player)
                    || this.ignoreFriendsSetting.isEnabled() && DefaultHelper.matchesString(this.player.getName())
                    || !this.matchesEntityPlayer2(this.player)
                    || !this.checkState20() && !this.chaseOutOfPITSetting.isEnabled() && !GetDisplayedTitleHelper.matchesEntityPlayer(this.player, 3.0F);
            }

            if (flag && this.utilHelper825.matchesLong(100L)) {
                this.utilHelper825.reset();
                this.utilHelper830.reset();
                EntityPlayer player = this.player;
                int n = (int)this.spawnYLevelSetting.computeDouble();
                int n2;
                if (this.debugManualStateSetting.isEnabled() && n > 0) {
                    n2 = n;
                } else if (ConnectToServerHandler.getFourSeasonsType() == FourSeasonsType.UNKNOWN) {
                    n2 = mc.thePlayer.getPosition().getY();
                } else {
                    n2 = ConnectToServerHandler.computeInt4();
                }

                this.player = BuildTargetComparatorHelper.selectTarget(
                    this,
                    AutoGrinder.YawMode.valueOf(this.huntenemiesSetting.getText()),
                    this.ignoreFriendsSetting.isEnabled(),
                    (int)this.maxTargetPITRangeSetting.computeDouble(),
                    this.attackRangeSetting.computeDouble(),
                    0.0,
                    n2,
                    0.0,
                    this.ignoreDiamondsSetting.isEnabled(),
                    (int)this.fovSetting.computeDouble()
                );
                if (this.checkState13()) {
                    process5();
                }

                if (this.player != player && (this.player == null || player == null || this.player.getDistanceSqToEntity(player) > 16.0)) {
                    ObsidianPlacerHandler.clearPath();
                }

                if (this.debugTargetSetting.isEnabled() && this.player != player) {
                    if (this.player == null) {
                        MessageUtils.x("Target: none eligible");
                    } else {
                        int n3 = Math.round(mc.thePlayer.getDistanceToEntity(this.player));
                        MessageUtils.x("Target: " + this.player.getName() + " (" + n3 + " blocks)");
                    }
                }

                if (this.player != null) {
                    this.utilHelper839.reset();
                }
            }
        }
    }

    public void onChatMention(PitEvent3 pitEvent3) {
        if (this.soundAlertsSetting.isEnabled()) {
            PlaySoundHelper.playSound("note.pling", 2.0F, 2.0F);
            PlaySoundHelper.playSound("note.pling", 2.0F, 1.75F);
            PlaySoundHelper.playSound("note.pling", 2.0F, 1.5F);
        }

        this.handleString(String.valueOf(new StringBuilder("Name mentioned: ").append(pitEvent3.text)), Color.PINK);
        if (this.respondToNameSetting.isEnabled() && mc.thePlayer != null) {
            long now = System.currentTimeMillis();
            boolean flag = now - this.timestamp9 < 15000L;
            this.timestamp9 = now;
            int n = (int)Math.min(this.responseMinDelaySecSetting.computeDouble(), this.responseMaxDelaySecSetting.computeDouble());
            int n2 = (int)Math.max(this.responseMinDelaySecSetting.computeDouble(), this.responseMaxDelaySecSetting.computeDouble());
            long l = ThreadLocalRandom.current().nextLong(n, n2 + 1L);
            String str = this.customSetting.matchesString2("CUSTOM") ? this.sBroAmDoinSum1Setting.getValue() : (flag ? "1s bruh" : "1s");
            Brutality.scheduledExecutorService.schedule(() -> mc.addScheduledTask(() -> {
                if (this.checkState44() && mc.thePlayer != null) {
                    mc.thePlayer.sendChatMessage(str);
                }
            }), l, TimeUnit.SECONDS);
            this.handleString(this.disconnectSetting.getText());
        }
    }

    public static boolean checkState6() {
        return instance != null && instance.checkState44() && instance.killAuraSetting.matchesString2("AutoClicker");
    }

    public double computeDouble2() {
        return this.jumpChancePerTickSetting.computeDouble() / 100.0;
    }

    private Map<MovementHandler.MidMode, Integer> computeMap() {
        EnumMap enumMap = new EnumMap<>(MovementHandler.MidMode.class);

        for (MovementHandler.MidMode midMode : MovementHandler.MidMode.values()) {
            enumMap.put(midMode, 0);
        }

        if (mc.theWorld == null) {
            return enumMap;
        }
        for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
            if (playerEntity != null
                && playerEntity != mc.thePlayer
                && !playerEntity.isDead
                && !playerEntity.isInvisible()
                && !GetMysticEnchantLoreHelper.matchesEntityPlayer10(playerEntity)
                && !BEASTHelper.matchesEntityPlayer2(playerEntity)
                && (!this.ignoreFriendsSetting.isEnabled() || !DefaultHelper.matchesString(playerEntity.getName()))
                && !this.matchesEntityPlayer(playerEntity)) {
                MovementHandler.MidMode midMode2 = this.computeMidMode(playerEntity);
                enumMap.put(midMode2, (Integer)enumMap.get(midMode2) + 1);
            }
        }

        return enumMap;
    }

    private boolean matchesPizzaType(PizzaType pizzaType) {
        return this.lookupMap2.containsKey(pizzaType) ? this.lookupMap2.get(pizzaType).isEnabled() : false;
    }

    private void process44() {
        this.entityItem = null;
        this.vec32 = null;
    }

    private boolean matchesInt(int n) {
        return n != -1 && n < this.minLobbyPlayersSetting.computeDouble() ? this.allowSwapLobbiesSetting.isEnabled() : false;
    }

    private boolean z() {
        return !this.pitOutskirtsSetting.matchesString2("PIT");
    }

    public void process41() {
        if (this.checkState44()
            && mc.thePlayer != null
            && mc.theWorld != null
            && !mc.thePlayer.isDead
            && !(mc.thePlayer.getHealth() <= 0.0F)
            && (!this.killAuraSetting.matchesString2("AutoClicker") || this.checkState5())
            && !FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)
            && !this.checkState38()
            && !this.utilHelper.checkState()) {
            this.flag18 = false;
            var module = this.killAuraSetting.matchesString2("KillAura") ? KillAura.instance : AutoClicker.instance;
            if (this.module != module) {
                this.process17();
            }

            if (module != null) {
                if (!((Module)module).checkState44()) {
                    ((Module)module).handleFlag3(true);
                }

                if (((Module)module).checkState44()) {
                    this.module = (Module)module;
                    if (module instanceof KillAura) {
                        KillAura.y = true;
                        ((KillAura)module).process38();
                    } else {
                        AutoClicker.y = true;
                    }
                }
            }
        } else {
            this.process22();
        }
    }

    private void handleString4(String str) {
        if (this.debugOverlaySetting.isEnabled()) {
            MessageUtils.x(str);
        }
    }

    public boolean checkState() {
        return this.checkState20() && this.flag11;
    }

    public void process37() {
        if (this.soundAlertsSetting.isEnabled()) {
            if (!this.utilHelper821.matchesLong(1500L)) {
                return;
            }

            this.utilHelper821.reset();
            PlaySoundHelper.playSound("mob.villager.no", 1.0F, 0.8F);
            PlaySoundHelper.playSound("mob.villager.no", 1.0F, 0.6F);
        }
    }

    private void process36() {
        if (OnClientChatReceivedHandler.computeKillsType() != KillsType.KILLS_DIAMOND_SWORD) {
            this.flag19 = false;
            this.count23 = 0;
        } else if (!this.flag19) {
            if (this.checkState40()) {
                this.flag19 = true;
            } else if (mc.currentScreen != null) {
                GuiContainer screen = (GuiContainer)mc.currentScreen;

                for (Object inventorySlot : screen.inventorySlots.inventorySlots) {
                    Slot slot = (Slot)inventorySlot;
                    if (slot != null && slot.getHasStack() && slot.getStack().getItem() == Items.diamond_sword) {
                        HelmetHelper.handleInt2(slot.slotNumber);
                        this.count23 = mc.thePlayer.ticksExisted;
                        return;
                    }
                }

                mc.thePlayer.closeScreen();
            } else if (this.count23 <= 0 || mc.thePlayer.ticksExisted - this.count23 >= 3) {
                BlockPos pos = GetNonBotPlayersHelper.computeBlockPos();
                if (pos != null) {
                    double distanceSq = mc.thePlayer.getDistanceSq(pos);
                    if (distanceSq > 4.0) {
                        org.brutality.automation.navigation.ClearPathHandler clearPathHandler = new org.brutality.automation.navigation.ClearPathHandler();
                        clearPathHandler.handleBlockPos(pos, (int)this.yawSpeedSetting.computeDouble(), (int)this.pitchSpeedSetting.computeDouble(), 0.3);
                    } else {
                        KeybindStateUtil.process();
                        KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
                    }
                }
            }
        }
    }

    private boolean checkState38() {
        this.process24();
        return this.checkState19() ? true : this.donTAttackWhileFallingSetting.isEnabled() && this.flag6;
    }

    private void runRaffleAutomationTick() {
        if (mc.thePlayer != null && mc.theWorld != null) {
            this.process43();
            int n = this.computeInt3();
            if (n == 0) {
                this.flag14 = false;
            }

            EntityItem entity = GetNonBotPlayersHelper.computeList3()
                .stream()
                .filter(
                    entity2 -> !entity2.isDead
                        && entity2.getEntityItem() != null
                        && entity2.getEntityItem().stackSize > 0
                        && entity2.getEntityItem().hasDisplayName()
                        && entity2.getEntityItem().getDisplayName().toLowerCase(Locale.ROOT).contains("ticket")
                )
                .min(Comparator.comparingDouble(entity3 -> entity3.getDistanceSqToEntity(mc.thePlayer)))
                .orElse(null);
            if (n > 0 && (n >= (int)this.raffleMaxTicketsSetting.computeDouble() || entity == null)) {
                this.flag14 = true;
            }

            if (this.flag14) {
                BlockPos pos = this.computeBlockPos2();
                if (pos == null) {
                    this.clearPathHandler2.clearPath();
                    KeybindStateUtil.process();
                } else {
                    Vec3 vec = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
                    MovingObjectPosition hit = mc.theWorld.rayTraceBlocks(positionEyes, vec);
                    if (!(positionEyes.distanceTo(vec) > 3.0) && hit != null && pos.equals(hit.getBlockPos())) {
                        this.clearPathHandler2.clearPath();
                        KeybindStateUtil.process();
                        BuildTargetComparatorHelper.handleDouble(
                            vec.xCoord, vec.yCoord, vec.zCoord, (int)this.yawSpeedSetting.computeDouble(), (int)this.pitchSpeedSetting.computeDouble()
                        );
                        if (this.utilHelper838.matchesLong(400L)) {
                            this.utilHelper838.reset();
                            mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, hit.sideHit, hit.hitVec);
                        }
                    } else {
                        BlockPos pos2 = this.computeBlockPos(pos);
                        KeybindStateUtil.process();
                        if (pos2 != null) {
                            this.clearPathHandler2
                                .handleBlockPos(pos2, (int)this.yawSpeedSetting.computeDouble(), (int)this.pitchSpeedSetting.computeDouble(), 0.0);
                        } else {
                            this.clearPathHandler2.clearPath();
                        }
                    }
                }
            } else {
                if (entity != null) {
                    Vec3 vec2 = new Vec3(entity.posX, entity.posY + 0.1, entity.posZ);
                    double d = Math.hypot(entity.posX - mc.thePlayer.posX, entity.posZ - mc.thePlayer.posZ);
                    if (d <= 2.0 && Math.abs(entity.posY - mc.thePlayer.posY) < 1.0 && BuildTargetComparatorHelper.matchesEntityPlayer(mc.thePlayer, vec2)) {
                        this.clearPathHandler2.clearPath();
                        KeybindStateUtil.process();
                        if (d > 0.25) {
                            BuildTargetComparatorHelper.handleDouble(
                                entity.posX,
                                mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - 0.55,
                                entity.posZ,
                                (int)this.yawSpeedSetting.computeDouble(),
                                (int)this.pitchSpeedSetting.computeDouble()
                            );
                            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                        }
                    } else {
                        KeybindStateUtil.process();
                        this.clearPathHandler2.handleEntity(entity, (int)this.yawSpeedSetting.computeDouble(), (int)this.pitchSpeedSetting.computeDouble(), 0.0);
                    }
                } else {
                    this.clearPathHandler2.clearPath();
                    KeybindStateUtil.process();
                }
            }
        }
    }

    public boolean matchesString(String str) {
        String str2 = str.trim().toLowerCase(Locale.ROOT);
        if (str2.equals("team deathmatch")) {
            str2 = "tdm";
        }

        for (PizzaType pizzaType : PizzaType.values()) {
            if (pizzaType.getIdentifier().equals(str2)) {
                return this.matchesPizzaType(pizzaType);
            }
        }

        return false;
    }

    public boolean checkState36() {
        if (!this.cancelRenderingSetting.isEnabled()) {
            return false;
        }
        return this.enablePerformanceSettingsSetting.isEnabled() ? this.checkState44() : false;
    }

    public static boolean matchesModule2(Module module) {
        return instance != null
            && instance.checkState44()
            && module != null
            && (instance.killAuraSetting.matchesString2("KillAura") ? module == KillAura.instance : module == AutoClicker.instance)
            // VENDOR-DISABLED: && RuntimeAuthGuard.checkState2();
            && true;
    }

    private int computeInt3() {
        if (mc.thePlayer != null && mc.thePlayer.inventory != null) {
            int n = 0;

            for (int i = 0; i < mc.thePlayer.inventory.mainInventory.length; i++) {
                ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
                if (stack != null && stack.hasDisplayName() && stack.getDisplayName().toLowerCase(Locale.ROOT).contains("ticket")) {
                    n += stack.stackSize;
                }
            }

            return n;
        }
        return 0;
    }

    public void handleString(String str, Color color) {
    }

    private void process26() {
        if (this.doKOTHSetting.isEnabled() && mc.gameSettings.renderDistanceChunks < 6) {
            if (this.blockPos == null) {
                this.count3 = mc.gameSettings.renderDistanceChunks;
                mc.gameSettings.renderDistanceChunks = 6;
                this.utilHelper835.reset();
                MessageUtils.x("Boosted render distance for KOTH");
            }
        }
    }

    public static boolean checkState30() {
        return checkState6()
            && mc.objectMouseOver != null
            && mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY
            && mc.objectMouseOver.entityHit == instance.player
            && checkState2();
    }

    private void handleSet2(Set<MovementHandler.MidMode> midModes) {
        if (this.utilHelper843.matchesLong(this.count11)) {
            this.count11 = 15000 + ThreadLocalRandom.current().nextInt(30001);
            this.utilHelper843.reset();
            List list = new ArrayList(midModes);
            MovementHandler.MidMode midMode = (MovementHandler.MidMode)list.get(ThreadLocalRandom.current().nextInt(list.size()));
            this.handleMidMode(midMode);
            this.flag11 = this.computeMap().getOrDefault(midMode, 0).intValue() < this.minPlayersSetting.computeDouble();
        }
    }

    public void onContractFailed(PitEvent5 pitEvent5) {
        if (mc.thePlayer != null) {
            if (mc.theWorld == null) {
                return;
            }

            OtherType otherType = this.otherType;
            if ((otherType == OtherType.DOWN_MID || otherType == OtherType.DOWN_OUTSIDE) && this.oofWhenContractCompletedSetting.isEnabled()) {
                MessageUtils.x(
                    String.valueOf(
                        new StringBuilder().append(EnumChatFormatting.RED).append("Contract failed, ").append(EnumChatFormatting.DARK_RED).append("oofing.")
                    )
                );
                PartyWarpType.OOF.handleInt(3000);
            }
        }
    }

    private static boolean matchesBlockPos(BlockPos pos) {
        return pos != null && mc.theWorld.getBlockState(pos).getBlock() == Blocks.diamond_block;
    }

    private void process21() {
        if (this.checkForTeleportsSetting.isEnabled() && mc.thePlayer != null && !(mc.thePlayer.getHealth() <= 0.0F) && !mc.thePlayer.isDead) {
            double x = mc.thePlayer.posX;
            double y = mc.thePlayer.posY;
            double z = mc.thePlayer.posZ;
            if (this.amount3 != 0.0 && !this.flag17 && Math.abs(x - this.amount3) + Math.abs(y - this.amount4) + Math.abs(z - this.amount5) > 8.0) {
                this.flag17 = true;
                this.handleString3("Teleport detected");
            }

            if (this.flag17 && Math.abs(x - this.amount3) + Math.abs(y - this.amount4) + Math.abs(z - this.amount5) < 2.0) {
                this.flag17 = false;
            }

            this.amount3 = x;
            this.amount4 = y;
            this.amount5 = z;
        }
    }

    private boolean checkState35() {
        return this.utilHelper89.matchesLong(this.computeInt());
    }

    public void onEventRender2D(EventRender2D event) {
        if (mc.thePlayer != null) {
            if (mc.theWorld == null) {
                return;
            }

            if (!this.debugOverlaySetting.isEnabled()) {
                return;
            }

            RenderHelper.handleSortSettingsHandler(
                null, String.valueOf(new StringBuilder().append(EnumChatFormatting.RED).append("AutoGrinder Debug Info")), 0, 0
            );
            RenderHelper.handleSortSettingsHandler(null, String.valueOf(new StringBuilder("State: ").append(this.otherType.name())), 0, 10);
            RenderHelper.handleSortSettingsHandler(
                null, String.valueOf(new StringBuilder("Stuck time: ").append(this.utilHelper89.computeLong()).append("ms")), 0, 20
            );
            RenderHelper.handleSortSettingsHandler(
                null, String.valueOf(new StringBuilder("No target time: ").append(this.utilHelper839.computeLong()).append("ms")), 0, 30
            );
            RenderHelper.handleSortSettingsHandler(
                null, String.valueOf(new StringBuilder("No hit time: ").append(this.utilHelper820.computeLong()).append("ms")), 0, 40
            );
            RenderHelper.handleSortSettingsHandler(
                null, String.valueOf(new StringBuilder("PIT: ").append(GetDisplayedTitleHelper.matchesEntityPlayer(mc.thePlayer))), 0, 50
            );
            RenderHelper.handleSortSettingsHandler(
                null, String.valueOf(new StringBuilder("PIT tight: ").append(GetDisplayedTitleHelper.matchesEntityPlayer2(mc.thePlayer))), 0, 60
            );
            RenderHelper.handleSortSettingsHandler(null, "Pit: " + ConnectToServerHandler.checkState2(), 0, 70);
            RenderHelper.handleSortSettingsHandler(null, "Map: " + ConnectToServerHandler.getFourSeasonsType().name(), 0, 80);
            RenderHelper.handleSortSettingsHandler(null, "Spawn: " + FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer), 0, 90);
            RenderHelper.handleSortSettingsHandler(null, "Join wait: " + ConnectToServerHandler.computeInt6(), 0, 100);
        }
    }

    public void onMegastreakStarted(PitEvent8 pitEvent8) {
        if (pitEvent8.text.equals(mc.thePlayer.getName())) {
            this.handleString(String.valueOf(new StringBuilder("Hit megastreak: ").append(pitEvent8.overdriveType.getDisplayName())), Color.YELLOW);
        }
    }

    public boolean checkState20() {
        return !this.pitOutskirtsSetting.matchesString2("PIT") && mc.theWorld != null && !this.checkState24() && !this.checkState17();
    }

    public void onMysticItemDrop(PitEvent9 pitEvent9) {
        this.handleString("Mystic drop!", Color.MAGENTA);
        this.utilHelper846.reset();
        this.timestamp = System.currentTimeMillis();
    }

    private void process14() {
        if (this.spirePerksSetting.isEnabled() && !this.flag12) {
            this.text3 = this.goldenheadsSetting.getText();
            this.text4 = this.strengthchainingSetting2.getText();
            this.text5 = this.safetyfirstSetting.getText();
            this.text6 = this.gladiatorSetting.getText();
            this.goldenheadsSetting.handleString(this.goldenheadsSetting2.getText());
            this.strengthchainingSetting2.handleString(this.strengthchainingSetting.getText());
            this.safetyfirstSetting.handleString(this.safetyfirstSetting2.getText());
            this.gladiatorSetting.handleString(this.gladiatorSetting2.getText());
            this.flag12 = true;
        }
    }

    public void handleInt(int n) {
        this.utilHelper.handleInt(n);
    }

    public boolean checkState17() {
        boolean flag = this.checkState33();
        if (!this.doKOTHSetting.isEnabled()) {
            return false;
        }
        return flag ? this.getBlockPos() != null : false;
    }

    private Set<MovementHandler.MidMode> computeSet() {
        var midModes = MovementHandler.computeSet();
        if (this.pitOutskirtsSetting.matchesString2("PIT + Outskirts")) {
            midModes.add(MovementHandler.MidMode.MID);
        }

        return midModes;
    }

    private int computeInt2() {
        return ConnectToServerHandler.getFourSeasonsType() == FourSeasonsType.GENESIS ? 850 : 700;
    }

    private void process11() {
        int n = ThreadLocalRandom.current()
            .nextInt((int)this.banResponseMinDelaySetting.computeDouble(), (int)this.banResponseMaxDelaySetting.computeDouble() + 1);
        String str = String.format("%.2f", n / 1000.0);
        Brutality.scheduledExecutorService.schedule(this.utilHelper834::reset, (long)n, TimeUnit.MILLISECONDS);
        this.handleString5(EnumChatFormatting.RED + "BAN WAVE DETECTED! " + EnumChatFormatting.GRAY + "responding in " + str + " seconds.");
        this.handleString("Ban wave detected in lobby!", Color.RED);
    }

    public double getAverageMidPlayerCount() {
        return this.queue.isEmpty() ? FreshHelper.computeInt2() : this.queue.stream().mapToInt(integer -> integer).average().orElse(0.0);
    }

    private void process8() {
        this.player = null;
        this.entityPlayer = null;
        this.dy = false;
        this.flag6 = false;
        this.flag3 = false;
        this.process44();
        PlayerHelper13.reset();
        ObsidianPlacerHandler.process();
        org.brutality.automation.grinding.movement.ClearPathHandler.clearPath();
        ClearPathHandler3.reset();
        ChooseTargetPositionHandler.reset();
        org.brutality.automation.grinding.purchases.ClearPathHandler.clearPath();
        if (this.clearPathHandler != null) {
            this.clearPathHandler.clearPath();
        }

        if (this.clearPathHandler2 != null) {
            this.clearPathHandler2.clearPath();
        }

        if (this.utilHelper845 != null) {
            this.utilHelper845.reset();
        }

        if (this.utilHelper89 != null) {
            this.utilHelper89.reset();
        }

        if (this.utilHelper839 != null) {
            this.utilHelper839.reset();
        }

        if (this.utilHelper820 != null) {
            this.utilHelper820.reset();
        }

        if (mc.gameSettings != null) {
            KeybindStateUtil.process();
        }

        if (mc.thePlayer != null && mc.theWorld != null && mc.thePlayer.worldObj == mc.theWorld) {
            mc.thePlayer.resetSprintToggleTimer();
            if (mc.thePlayer.isSprinting()) {
                mc.thePlayer.setSprinting(false);
            }
        }
    }

    public static void handleString2(String str) {
        MessageUtils.x(str);
    }

    @Override
    public void onEnable() {
        this.computeInvalidateHandler().invalidate();
        PlayerHelper13.reset();
        // VENDOR-DISABLED: if (!EntitlementState.checkState()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.process8();
            this.lifecycleHandler.reset();
            this.flag = false;
            this.process44();
            this.itemSet.clear();
            this.timestamp = 0L;
            this.flag2 = true;
            this.lifecycleHandler2.reset();
            ClearPathHandler3.clearPath();
            this.clearPathHandler.clearPath();
            org.brutality.automation.grinding.purchases.ClearPathHandler.clearPath();
            ObsidianPlacerHandler.process4();
            ObsidianPlacerHandler.clearPath();
            this.process6();
            this.silentHandler.reset();
            mc.gameSettings.pauseOnLostFocus = false;
            mc.gameSettings.saveOptions();
            this.player = null;
            this.entityPlayer = null;
            this.dy = false;
            this.count2 = -1;
            this.utilHelper87.clear();
            this.cancelHandler2.reset();
            this.utilHelper85.clear();
            this.utilHelper812.reset();
            this.utilHelper813.reset();
            this.dz = false;
            this.timestamp4 = 0L;
            this.timestamp5 = 0L;
            this.flag5 = false;
            this.text = null;
            this.flag3 = false;
            this.count6 = 0;
            this.flag10 = false;
            this.count22 = 0;
            org.brutality.automation.grinding.purchases.ClearPathHandler.process();
            this.flag8 = false;
            this.flag15 = false;
            this.utilHelper811.clear();
            this.utilHelper824.clear();
            this.utilHelper829.clear();
            this.utilHelper834.clear();
            this.process42();
            this.otherType2 = OtherType.SPAWN;
            this.otherType = OtherType.SPAWN;
            this.hasItemHandler.process2();
            this.utilHelper839.reset();
            this.utilHelper89.reset();
            this.utilHelper820.reset();
            this.count12 = 0;
            this.count13 = 0;
            this.count14 = 0;
            this.count15 = 0;
            this.count16 = 0;
            this.factor2 = 0.0F;
            this.factor3 = 0.0F;
            if (this.enablePerformanceSettingsSetting.isEnabled()) {
                this.factor = mc.gameSettings.fovSetting;
                this.count9 = mc.gameSettings.renderDistanceChunks;
                this.count4 = mc.gameSettings.limitFramerate;
                mc.gameSettings.fovSetting = Math.min(this.factor, (float)((int)this.limitFOVSetting.computeDouble()));
                mc.gameSettings.renderDistanceChunks = (int)this.limitRenderDistanceSetting.computeDouble();
                mc.gameSettings.limitFramerate = (int)this.fpsLimitSetting.computeDouble();
                this.flag4 = true;
            }
        }
    }

    private void process43() {
        if (this.checkState8()) {
            int n = this.computeInt3();
            if (n > this.count20) {
                this.count17 = this.count17 + (n - this.count20);
            }

            this.count20 = n;
        }
    }

    public void onContractCompleted(PitEvent4 pitEvent4) {
        if (mc.thePlayer != null) {
            if (mc.theWorld == null) {
                return;
            }

            this.handleString(
                String.valueOf(new StringBuilder("Completed a contract (").append(OnClientChatReceivedHandler.getCount()).append(" remaining)")), Color.CYAN
            );
            OtherType otherType = this.otherType;
            if ((otherType == OtherType.DOWN_MID || otherType == OtherType.DOWN_OUTSIDE) && this.oofWhenContractCompletedSetting.isEnabled()) {
                MessageUtils.x(
                    String.valueOf(
                        new StringBuilder()
                            .append(EnumChatFormatting.GREEN)
                            .append("Contract completed, ")
                            .append(EnumChatFormatting.DARK_RED)
                            .append("oofing.")
                    )
                );
                PartyWarpType.OOF.handleInt(3000);
            }
        }
    }

    private InvalidateHandler computeInvalidateHandler() {
        if (this.invalidateHandler == null) {
            this.invalidateHandler = new InvalidateHandler();
        }

        return this.invalidateHandler;
    }

    private static boolean matchesItemStack(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        return GetMysticEnchantLoreHelper.matchesItemStack5(stack)
            || item == Items.coal
            || item == Items.apple
            || item == Items.arrow
            || item == Items.minecart;
    }

    private boolean checkState11() {
        return this.computeIronMode().emergencyOofAllowed;
    }

    private void process24() {
        if (this.flag6 && mc.thePlayer != null && (mc.thePlayer.onGround || this.utilHelper816.matchesLong(10000L))) {
            this.flag6 = false;
        }
    }

    public void handleInt(int n, long l) {
        this.cancelHandler2.handleInt(n, l);
    }

    public void process22() {
        this.process12();
        var module = this.killAuraSetting.matchesString2("KillAura") ? KillAura.instance : AutoClicker.instance;
        if (this.checkState44() && module != null) {
            boolean flag = this.flag18 && this.module == module;
            if (this.module != module) {
                this.process17();
            }

            this.module = (Module)module;
            if (module instanceof KillAura) {
                if (((Module)module).checkState44() && !flag) {
                    ((KillAura)module).process12();
                }

                KillAura.entityLivingBase3 = null;
            }

            this.flag18 = true;
        } else {
            this.flag18 = false;
            this.process17();
            if (this.killAuraSetting.matchesString2("KillAura")) {
                KillAura.y = false;
            } else {
                AutoClicker.y = false;
            }

            if (!this.utilHelper.checkState()) {
                if (this.killAuraSetting.matchesString2("KillAura")) {
                    if (KillAura.instance != null) {
                        KillAura.entityLivingBase3 = null;
                        KillAura.instance.process38();
                    }
                } else {
                    AutoClicker.y = false;
                    if (AutoClicker.instance != null && AutoClicker.instance.checkState44()) {
                        AutoClicker.instance.handleFlag3(false);
                    }
                }
            }
        }
    }

    public String computeString() {
        String str = EnumChatFormatting.RED + "AutoGrinder Enabled";
        if ((int)this.disableAfterMinsSetting.computeDouble() > 0) {
            long l = Math.max(0L, (long)this.disableAfterMinsSetting.computeDouble() * 60000L - this.utilHelper812.computeLong());
            EnumChatFormatting WHITE = EnumChatFormatting.WHITE;
            long l2 = TimeUnit.MILLISECONDS.toMinutes(l);
            EnumChatFormatting RED = EnumChatFormatting.RED;
            long l3 = l2;
            EnumChatFormatting enumChatFormatting = WHITE;
            str = str + " - Disabling after " + enumChatFormatting + l3 + RED + " minutes";
        }

        return str;
    }

    public void onClientChatReceived(ChatEvent chatEvent) {
        if (chatEvent != null && chatEvent.message != null) {
            String str = ExecuteCommandHelper.computeString6(chatEvent.message.getUnformattedText());
            long now = System.currentTimeMillis();
            if (!str.equals(this.text2) || now - this.timestamp8 >= 100L) {
                this.text2 = str;
                this.timestamp8 = now;
                if (this.checkState8()) {
                    if (str.contains("REGISTERED!")) {
                        Matcher matcher = Pattern.compile("\\+(\\d+)\\s+Tickets!", 2).matcher(str);
                        if (matcher.find()) {
                            this.count18 = this.count18 + Integer.parseInt(matcher.group(1));
                            this.count20 = this.computeInt3();
                        }
                    }

                    if (LifecycleHandler.matchesString(str)) {
                        this.count19 = this.count19 + this.computeInt3();
                        this.count20 = 0;
                    }
                }

                if (LifecycleHandler.matchesString(str)) {
                    this.process4();
                } else {
                    if (str.startsWith("MAJOR EVENT!")) {
                        if (str.contains("SQUADS")) {
                            this.handleString5("Squads event starting - AFKing until it ends.");
                            if (FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                                this.otherType = OtherType.SPAWN_SQUADS;
                            } else {
                                this.otherType = OtherType.DOWN_SQUADS;
                            }

                            KeybindStateUtil.process();
                        }

                        if (str.contains("SPIRE")) {
                            if (str.contains("min")) {
                                this.utilHelper83.reset();
                            }

                            if (str.contains("starting now")) {
                                this.utilHelper83.reset();
                                if (FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                                    this.otherType = OtherType.SPAWN_SPIRE;
                                } else {
                                    this.otherType = OtherType.DOWN_SPIRE;
                                }
                            }
                        }
                    }

                    if (str.startsWith("You must be level 10 to access this!")) {
                        this.utilHelper87.reset();
                    }

                    if (str.contains("A player has been removed from your game.")) {
                        this.utilHelper848.reset();
                    }

                    if (str.startsWith("MYSTIC ITEM!") && str.contains("dropped") && !str.contains(":")) {
                        this.count13++;
                        this.utilHelper846.reset();
                        this.timestamp = now;
                    }

                    Matcher matcher2 = pattern.matcher(str);
                    if (matcher2.find()) {
                        String str2 = matcher2.group(1);
                        if (mc.thePlayer != null && str2.equalsIgnoreCase(mc.thePlayer.getName())) {
                            this.count14++;
                        }
                    }

                    boolean flag = ServerUtils.checkState2() || ServerUtils.ip() || ServerUtils.checkState5();
                    this.handleString(str, flag);
                }
            }
        }
    }

    private BlockPos computeBlockPos(BlockPos pos) {
        BlockPos pos2 = null;
        double MAX_VALUE = Double.MAX_VALUE;
        Vec3 vec = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        for (int i = -2; i <= 2; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -2; k <= 2; k++) {
                    BlockPos pos3 = pos.add(i, j, k);
                    if (FenceHandler.matchesBlockPos7(pos3)) {
                        Vec3 vec2 = new Vec3(pos3.getX() + 0.5, pos3.getY() + mc.thePlayer.getEyeHeight(), pos3.getZ() + 0.5);
                        if (!(vec2.distanceTo(vec) > 3.0)) {
                            MovingObjectPosition hit = mc.theWorld.rayTraceBlocks(vec2, vec);
                            if (hit != null && pos.equals(hit.getBlockPos())) {
                                double distanceSqToCenter = mc.thePlayer.getDistanceSqToCenter(pos3);
                                if (distanceSqToCenter < MAX_VALUE) {
                                    pos2 = pos3;
                                    MAX_VALUE = distanceSqToCenter;
                                }
                            }
                        }
                    }
                }
            }
        }

        return pos2;
    }

    private int computeInt() {
        return this.computeIronMode().stuckTimeoutMillis;
    }

    public boolean matchesEntityPlayer2(EntityPlayer player) {
        return this.checkState20() && player != null ? this.computeMidMode(player) == this.midMode : true;
    }

    private boolean checkState19() {
        return this.ignoreWhileRidingSetting.isEnabled() && mc.thePlayer != null && mc.thePlayer.isRiding();
    }

    private void process16() {
        if (!this.pauseOnOrionVanishSetting.isEnabled()) {
            this.flag16 = false;
        } else if (this.utilHelper847.matchesLong(1000L)) {
            this.utilHelper847.reset();
            Module module = Brutality.moduleManager.computeModule("Orion AntiVanish");
            boolean flag = module instanceof OrionAntiVanish && module.checkState44() && ((OrionAntiVanish)module).checkState();
            if (!flag) {
                this.flag16 = false;
            } else {
                if (this.oofSetting.matchesString2("AFK")) {
                    this.handleInt(1500);
                } else if (this.oofSetting.matchesString2("OOF") && !this.flag16) {
                    this.flag16 = true;
                    MessageUtils.x(EnumChatFormatting.YELLOW + "Vanished player detected, oofing.");
                    PartyWarpType.OOF.handleInt(1000);
                }
            }
        }
    }

    private void handleString(String str) {
        if ("AFK".equals(str)) {
            this.handleInt(60000);
        } else if ("HUB".equals(str)) {
            this.handleString6("AutoGrinder safety action: hubbing.");
        } else if ("DISCONNECT".equals(str)) {
            ConnectToServerHandler.handleString("AutoGrinder safety action triggered.");
        }
    }

    private void handleString3(String str) {
        this.handleInt(60000);
        if (this.soundAlertsSetting.isEnabled()) {
            PlaySoundHelper.playSound("note.pling", 2.0F, 1.0F);
            PlaySoundHelper.playSound("note.pling", 2.0F, 1.5F);
            PlaySoundHelper.playSound("note.pling", 2.0F, 2.0F);
        }

        this.handleString5(str + ": AutoGrinder paused.");
    }

    private void process12() {
        if (this.flag7 && mc.gameSettings != null) {
            int keyCode = mc.gameSettings.keyBindAttack.getKeyCode();
            KeyBinding.setKeyBindState(keyCode, Mouse.isButtonDown(0));
            this.flag7 = false;
        }
    }

    private static String computeString(long l) {
        long l2 = Math.max(0L, l) / 1000L;
        long l3 = l2 / 3600L;
        long l4 = l2 % 3600L / 60L;
        long l5 = l2 % 60L;
        return String.format("%02d:%02d:%02d", l3, l4, l5);
    }

    private boolean checkState8() {
        return this.otherType == OtherType.SPAWN_RAFFLE
            || this.otherType == OtherType.DOWN_RAFFLE
            || ConnectToServerHandler.checkState6() && ConnectToServerHandler.getPizzaType() == PizzaType.RAFFLE;
    }

    public AutoGrinder() {
        super("AutoGrinder", 0, Category.PIT);
        this.process18();
        instance = this;
        this.smoothCameraSetting = new BooleanSetting("Smooth Camera", this, true);
        this.pitOutskirtsSetting = new ModeSetting("Streak Mode", this, "PIT", Arrays.asList("PIT", "Outskirts", "PIT + Outskirts"));
        this.randomSetting = new ModeSetting("Auto Biome Mode", this, "Most Chains", Arrays.asList("Most Chains", "Random")).computeModeSetting(this::z);
        this.useFreshPantsSetting = new BooleanSetting("Use Fresh Pants", this, true);
        this.silentSetting = new ModeSetting("Fresh Pants Mode", this, "Normal", Arrays.asList("Normal", "Silent"))
            .computeModeSetting(() -> this.useFreshPantsSetting.isEnabled());
        this.freshPantsDelaySetting = new NumberSetting("Fresh Pants Delay", this, 150.0, 0.0, 1000.0, 10.0)
            .computeNumberSetting(() -> this.useFreshPantsSetting.isEnabled());
        this.freshPantsSafeModeSetting = new BooleanSetting("Fresh Pants Safe Mode", this, true)
            .computeBooleanSetting(() -> this.useFreshPantsSetting.isEnabled());
        this.mysticPickupDistanceSetting = new NumberSetting("Mystic Pickup Distance", this, 15.0, 5.0, 20.0, 1.0);
        this.attackRangeSetting = new NumberSetting("Attack Range", this, 3.7, 1.0, 6.0, 0.1);
        this.maxTargetPITRangeSetting = new NumberSetting("Max Target PIT Range", this, 12.0, 0.0, 128.0, 1.0);
        this.targetSwitchDelaySetting = new NumberSetting("Target Switch Delay", this, 1000.0, 0.0, 10000.0, 50.0);
        this.fovSetting = new NumberSetting("FOV", this, 180.0, 120.0, 180.0, 10.0) {
            @Override
            public void handleDouble(double d5) {
                super.handleDouble(Math.min(d5, this.getMaxValue()));
            }
        };
        this.yawSpeedSetting = new NumberSetting("Yaw Speed", this, 40.0, 1.0, 100.0, 1.0);
        this.pitchSpeedSetting = new NumberSetting("Pitch Speed", this, 20.0, 1.0, 100.0, 1.0);
        this.jumpChancePerTickSetting = new NumberSetting("Jump Chance Per Tick", this, 75.0, 0.0, 100.0, 1.0);
        this.strafeChancePerTickSetting = new NumberSetting("Strafe Chance Per Tick", this, 10.0, 0.0, 100.0, 1.0);
        this.minPlayersSetting = new NumberSetting("Min Players", this, 5.0, 0.0, 25.0, 1.0);
        this.minLobbyPlayersSetting = new NumberSetting("Min Lobby Players", this, 45.0, 0.0, 81.0, 1.0);
        this.minPitPlayersToRunSetting = new NumberSetting("Min Pit Players To Run", this, 0.0, 0.0, 500.0, 1.0);
        this.afkChanceOnSpawnSetting = new NumberSetting("AFK Chance On Spawn", this, 7.0, 0.0, 100.0, 1.0);
        this.disableAfterMinsSetting = new NumberSetting("Disable After Mins", this, 99999.0, 0.0, 999999.0, 1.0);
        this.takeScheduledBreaksSetting = new BooleanSetting("Take Scheduled Breaks", this, true);
        this.breakEveryMinsSetting = new NumberSetting("Break Every Mins", this, 120.0, 1.0, 720.0, 1.0);
        this.breakLengthMinsSetting = new NumberSetting("Break Length Mins", this, 30.0, 1.0, 180.0, 1.0);
        this.banAFKLengthMinsSetting = new NumberSetting("Ban AFK Length Mins", this, 15.0, 0.0, 120.0, 1.0);
        this.banResponseMinDelaySetting = new NumberSetting("Ban Response Min Delay", this, 5000.0, 0.0, 300000.0, 100.0);
        this.banResponseMaxDelaySetting = new NumberSetting("Ban Response Max Delay", this, 25000.0, 0.0, 300000.0, 100.0);
        this.autoPerkFrequencySetting = new NumberSetting("Auto Perk Frequency", this, 15.0, 1.0, 120.0, 1.0);
        this.windowClickDelaySetting = new NumberSetting("Window Click Delay", this, 500.0, 0.0, 5000.0, 50.0);
        this.autoBuySetting = new BooleanSetting("Auto Buy", this, false);
        this.everyMinsSetting = new ModeSetting("Auto Buy Mode", this, "Every Death", Arrays.asList("Every Death", "Every Mins"))
            .computeModeSetting(() -> this.autoBuySetting.isEnabled());
        this.autoBuyEveryMinsSetting = new NumberSetting("Auto Buy Every Mins", this, 15.0, 1.0, 120.0, 1.0)
            .computeNumberSetting(() -> this.autoBuySetting.isEnabled() && this.everyMinsSetting.matchesString2("Every Mins"));
        this.obsidian64Setting = new KeybindListSetting(
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
        this.spawnYLevelSetting = new NumberSetting("Spawn Y Level", this, 100.0, -64.0, 320.0, 1.0);
        this.limitRenderDistanceSetting = new NumberSetting("Limit Render Distance", this, 2.0, 0.0, 8.0, 1.0);
        this.fpsLimitSetting = new NumberSetting("FPS Limit", this, 30.0, 20.0, 240.0, 5.0);
        this.limitFOVSetting = new NumberSetting("Limit FOV", this, 30.0, 0.0, 180.0, 5.0);
        this.majoreventsonlySetting = new ModeSetting("Config", this, "IRON", Arrays.asList("IRON", "DIAMOND", "MYSTICS", "MAJOR_EVENTS_ONLY"));
        this.onlynearbhunterSetting = new ModeSetting("Auto Self Check", this, "ON", Arrays.asList("OFF", "ON", "ONLY_NEAR_BHUNTER"));
        this.huntenemiesSetting = new ModeSetting(
            "Aim Mode", this, "HEALTH_ONLY_HITTABLE", Arrays.asList("YAW", "HEALTH", "HEALTH_ONLY_HITTABLE", "HIGHEST_HEALTH", "HURT_TIME", "HUNT_ENEMIES")
        );
        this.disableanddisconnectSetting3 = new ModeSetting(
            "On Level 120", this, "CONTINUE", Arrays.asList("CONTINUE", "PRESTIGE", "DISABLE", "DISABLE_AND_DISCONNECT")
        );
        this.disableanddisconnectSetting2 = new ModeSetting(
            "On Ban", this, "HUB_AND_AFK", Arrays.asList("DO_NOTHING", "SWAP_LOBBY", "OOF_AND_AFK", "HUB_AND_AFK", "HUB_AND_DISABLE", "DISABLE_AND_DISCONNECT")
        );
        this.onalwaysdropSetting = new ModeSetting(
            "Auto Contract", this, "ON_ALWAYS_DROP", Arrays.asList("OFF", "ON_ONLY_DROP_FOR_DOABLE_CONTRACTS", "ON_ALWAYS_DROP")
        );
        this.disableanddisconnectSetting = new ModeSetting(
            "On Contracts Ran Out", this, "CONTINUE", Arrays.asList("CONTINUE", "DISABLE", "DISABLE_AND_DISCONNECT")
        );
        this.oofOnBadPathSetting = new BooleanSetting("Oof On Bad Path", this, true);
        this.oofOnNoHitSetting = new BooleanSetting("Oof On No Hit", this, false);
        this.oofNoHitSecondsSetting = new NumberSetting("Oof No Hit Seconds", this, 12.0, 1.0, 60.0, 1.0)
            .computeNumberSetting(() -> this.oofOnNoHitSetting.isEnabled());
        this.killAuraSetting = new ModeSetting("Combat Mode", this, "AutoClicker", Arrays.asList("AutoClicker", "KillAura"));
        this.hudSetting = new BooleanSetting("HUD", this, true);
        this.hudxSetting = new NumberSetting("HUD X", this, 5.0, 0.0, 1920.0, 1.0);
        this.hudySetting = new NumberSetting("HUD Y", this, 5.0, 0.0, 1080.0, 1.0);
        this.allowSwapLobbiesSetting = new BooleanSetting("Allow Swap Lobbies", this, true);
        this.ignoreDiamondsSetting = new BooleanSetting("Ignore Diamonds", this, false);
        this.chaseOutOfPITSetting = new BooleanSetting("Chase Out of PIT", this, false);
        this.diamondIfLookingSetting = new BooleanSetting("Diamond If Looking", this, false);
        this.ignoreFriendsSetting = new BooleanSetting("Ignore Friends", this, true);
        this.dropDownSetting = new BooleanSetting("Drop Down", this, true);
        this.donTAttackWhileFallingSetting = new BooleanSetting("Don't Attack While Falling", this, true);
        this.ignoreWhileRidingSetting = new BooleanSetting("Ignore While Riding", this, true);
        this.lowLifeCheckSetting = new BooleanSetting("Low Life Check", this, true);
        this.soundAlertsSetting = new BooleanSetting("Sound Alerts", this, true);
        this.safeJumpSetting = new BooleanSetting("Safe Jump", this, true);
        this.pickUpMysticDropsSetting = new BooleanSetting("Pick Up Mystic Drops", this, true);
        this.doRagePitEventSetting = new BooleanSetting("Do Rage Pit Event", this, true);
        this.doTDMEventSetting = new BooleanSetting("Do TDM Event", this, true);
        this.doBeastEventSetting = new BooleanSetting("Do Beast Event", this, true);
        this.doRobberyEventSetting = new BooleanSetting("Do Robbery Event", this, true);
        this.doSpireEventSetting = new BooleanSetting("Do Spire Event", this, true);
        this.doRaffleEventSetting = new BooleanSetting("Do Raffle Event", this, true);
        this.doBlockheadEventSetting = new BooleanSetting("Do Blockhead Event", this, false);
        this.doSquadsEventSetting = new BooleanSetting("Do Squads Event", this, true);
        this.oofWhenContractCompletedSetting = new BooleanSetting("Oof When Contract Completed", this, false);
        this.randomAFKWhenWaitingSetting = new BooleanSetting("Random AFK When Waiting", this, true);
        this.doKillContractsSetting = new BooleanSetting("Do Kill Contracts", this, true);
        this.doChainKillContractsSetting = new BooleanSetting("Do Chain Kill Contracts", this, true);
        this.doKillStreakContractsSetting = new BooleanSetting("Do Kill Streak Contracts", this, true);
        this.doSneakKillsContractsSetting = new BooleanSetting("Do Sneak Kills Contracts", this, true);
        this.doNakedKillsContractsSetting = new BooleanSetting("Do Naked Kills Contracts", this, true);
        this.doDiamondSwordKillsContractsSetting = new BooleanSetting("Do Diamond Sword Kills Contracts", this, true);
        this.doClaimBountiesContractsSetting = new BooleanSetting("Do Claim Bounties Contracts", this, false);
        this.doGoldContractsSetting = new BooleanSetting("Do Gold Contracts", this, true);
        this.autoPerkSetting = new BooleanSetting("Auto Perk", this, true);
        this.smartPerkCooldownSetting = new BooleanSetting("Smart Perk Cooldown", this, true);
        this.goldenheadsSetting = new ModeSetting("Perk 1", this, "GOLDEN_HEADS", entries);
        this.strengthchainingSetting2 = new ModeSetting("Perk 2", this, "STRENGTH_CHAINING", entries);
        this.safetyfirstSetting = new ModeSetting("Perk 3", this, "SAFETY_FIRST", entries);
        this.gladiatorSetting = new ModeSetting("Perk 4", this, "GLADIATOR", entries);
        this.spirePerksSetting = new BooleanSetting("Spire Perks", this, false);
        this.goldenheadsSetting2 = new ModeSetting("Spire Perk 1", this, "GOLDEN_HEADS", entries).computeModeSetting(() -> this.spirePerksSetting.isEnabled());
        this.strengthchainingSetting = new ModeSetting("Spire Perk 2", this, "STRENGTH_CHAINING", entries)
            .computeModeSetting(() -> this.spirePerksSetting.isEnabled());
        this.safetyfirstSetting2 = new ModeSetting("Spire Perk 3", this, "SAFETY_FIRST", entries).computeModeSetting(() -> this.spirePerksSetting.isEnabled());
        this.gladiatorSetting2 = new ModeSetting("Spire Perk 4", this, "GLADIATOR", entries).computeModeSetting(() -> this.spirePerksSetting.isEnabled());
        this.raffleMaxTicketsSetting = new NumberSetting("Raffle Max Tickets", this, 10.0, 1.0, 64.0, 1.0);
        this.buyXPBoostSetting = new BooleanSetting("Buy XP Boost", this, true);
        this.buyGoldBoostSetting = new BooleanSetting("Buy Gold Boost", this, true);
        this.buyDamageBoostSetting = new BooleanSetting("Buy Damage Boost", this, true);
        this.buyDamageReductionSetting = new BooleanSetting("Buy Damage Reduction", this, false);
        this.buyElGatoSetting = new BooleanSetting("Buy El Gato", this, true);
        this.buyKillstreaksSetting = new BooleanSetting("Buy Killstreaks", this, true);
        this.feastSetting = new ModeSetting("Killstreak 1", this, "FEAST", entries2);
        this.fightorflightSetting = new ModeSetting("Killstreak 2", this, "FIGHT_OR_FLIGHT", entries2);
        this.expliciousSetting = new ModeSetting("Killstreak 3", this, "EXPLICIOUS", entries2);
        this.debugOverlaySetting = new BooleanSetting("Debug Overlay", this, false);
        this.scheduleSetting = new BooleanSetting("Schedule", this, false);
        this.scheduleStartHourSetting = new NumberSetting("Schedule Start Hour", this, 0.0, 0.0, 23.0, 1.0);
        this.scheduleStopHourSetting = new NumberSetting("Schedule Stop Hour", this, 0.0, 0.0, 23.0, 1.0);
        this.debugManualStateSetting = new BooleanSetting("Debug Manual State", this, false);
        this.debugTargetSetting = new BooleanSetting("Debug Target", this, false);
        this.doKOTHSetting = new BooleanSetting("Do KOTH", this, true);
        this.disableOnLaunchSetting = new BooleanSetting("Disable On Launch", this, true);
        this.enablePerformanceSettingsSetting = new BooleanSetting("Enable Performance Settings", this, true);
        this.cancelRenderingSetting = new BooleanSetting("Cancel Rendering", this, false);
        this.skipRenderWorldSetting = new BooleanSetting("Skip Render World", this, false);
        this.cancelSoundSetting = new BooleanSetting("Cancel Sound", this, true);
        this.combatSetting = new KeybindListSetting(
            "Combat",
            this,
            this.killAuraSetting,
            this.majoreventsonlySetting,
            this.onlynearbhunterSetting,
            this.huntenemiesSetting,
            this.attackRangeSetting,
            this.maxTargetPITRangeSetting,
            this.targetSwitchDelaySetting,
            this.fovSetting,
            this.yawSpeedSetting,
            this.pitchSpeedSetting,
            this.ignoreFriendsSetting,
            this.diamondIfLookingSetting,
            this.chaseOutOfPITSetting,
            this.ignoreDiamondsSetting
        );
        this.movementSetting = new KeybindListSetting(
            "Movement",
            this,
            this.jumpChancePerTickSetting,
            this.strafeChancePerTickSetting,
            this.dropDownSetting,
            this.smoothCameraSetting,
            this.donTAttackWhileFallingSetting,
            this.ignoreWhileRidingSetting,
            this.safeJumpSetting,
            this.minPlayersSetting,
            this.randomAFKWhenWaitingSetting,
            this.afkChanceOnSpawnSetting
        );
        this.grindingSetting = new KeybindListSetting(
            "Grinding",
            this,
            this.pitOutskirtsSetting,
            this.randomSetting,
            this.useFreshPantsSetting,
            this.silentSetting,
            this.freshPantsDelaySetting,
            this.freshPantsSafeModeSetting,
            this.mysticPickupDistanceSetting,
            this.pickUpMysticDropsSetting,
            this.minPitPlayersToRunSetting,
            this.autoBuySetting,
            this.everyMinsSetting,
            this.autoBuyEveryMinsSetting,
            this.obsidian64Setting,
            this.windowClickDelaySetting
        );
        this.contractsSetting = new KeybindListSetting(
            "Contracts",
            this,
            this.onalwaysdropSetting,
            this.disableanddisconnectSetting,
            this.doKillContractsSetting,
            this.doChainKillContractsSetting,
            this.doKillStreakContractsSetting,
            this.doSneakKillsContractsSetting,
            this.doNakedKillsContractsSetting,
            this.doDiamondSwordKillsContractsSetting,
            this.doClaimBountiesContractsSetting,
            this.doGoldContractsSetting,
            this.oofWhenContractCompletedSetting
        );
        this.perksAndKillstreaksSetting = new KeybindListSetting(
            "Perks And Killstreaks",
            this,
            this.autoPerkSetting,
            this.smartPerkCooldownSetting,
            this.autoPerkFrequencySetting,
            this.goldenheadsSetting,
            this.strengthchainingSetting2,
            this.safetyfirstSetting,
            this.gladiatorSetting,
            this.spirePerksSetting,
            this.goldenheadsSetting2,
            this.strengthchainingSetting,
            this.safetyfirstSetting2,
            this.gladiatorSetting2,
            this.buyXPBoostSetting,
            this.buyGoldBoostSetting,
            this.buyDamageBoostSetting,
            this.buyDamageReductionSetting,
            this.buyElGatoSetting,
            this.buyKillstreaksSetting,
            this.feastSetting,
            this.fightorflightSetting,
            this.expliciousSetting
        );
        this.lobbyAndSafetySetting = new KeybindListSetting(
            "Lobby And Safety",
            this,
            this.minLobbyPlayersSetting,
            this.disableanddisconnectSetting2,
            this.disableanddisconnectSetting3,
            this.allowSwapLobbiesSetting,
            this.banAFKLengthMinsSetting,
            this.banResponseMinDelaySetting,
            this.banResponseMaxDelaySetting,
            this.disableAfterMinsSetting,
            this.scheduleSetting,
            this.scheduleStartHourSetting,
            this.scheduleStopHourSetting,
            this.takeScheduledBreaksSetting,
            this.breakEveryMinsSetting,
            this.breakLengthMinsSetting
        );
        this.performanceSetting = new KeybindListSetting(
            "Performance",
            this,
            this.enablePerformanceSettingsSetting,
            this.limitRenderDistanceSetting,
            this.fpsLimitSetting,
            this.limitFOVSetting,
            this.cancelRenderingSetting,
            this.skipRenderWorldSetting,
            this.cancelSoundSetting
        );
        this.hudAndDebugSetting = new KeybindListSetting(
            "HUD And Debug",
            this,
            this.hudSetting,
            this.hudxSetting,
            this.hudySetting,
            this.soundAlertsSetting,
            this.debugOverlaySetting,
            this.debugManualStateSetting,
            this.debugTargetSetting,
            this.spawnYLevelSetting
        );
        this.majorEventsSetting = new KeybindListSetting(
            "Major Events",
            this,
            this.doRagePitEventSetting,
            this.doTDMEventSetting,
            this.doBeastEventSetting,
            this.doRobberyEventSetting,
            this.doSpireEventSetting,
            this.doRaffleEventSetting,
            this.doBlockheadEventSetting,
            this.doSquadsEventSetting,
            this.doKOTHSetting,
            this.raffleMaxTicketsSetting,
            this.disconnectWhileWaitingSetting,
            this.joinBeforeMinutesSetting
        );
        this.safetyAndRecoverySetting = new KeybindListSetting(
            "Safety And Recovery",
            this,
            this.stopAfterDeathSetting,
            this.deathStopStreakSetting,
            this.lowLifeCheckSetting,
            this.oofOnBadPathSetting,
            this.oofOnNoHitSetting,
            this.oofNoHitSecondsSetting,
            this.disableOnLaunchSetting
        );
        this.respondToNameSetting = new BooleanSetting("Respond To Name", this, true);
        this.responseMinDelaySecSetting = new NumberSetting("Response Min Delay (sec)", this, 3.0, 1.0, 10.0, 1.0)
            .computeNumberSetting(() -> this.respondToNameSetting.isEnabled());
        this.responseMaxDelaySecSetting = new NumberSetting("Response Max Delay (sec)", this, 10.0, 1.0, 10.0, 1.0)
            .computeNumberSetting(() -> this.respondToNameSetting.isEnabled());
        this.customSetting = new ModeSetting("Response Message", this, "1S", Arrays.asList("1S", "CUSTOM"))
            .computeModeSetting(() -> this.respondToNameSetting.isEnabled());
        this.sBroAmDoinSum1Setting = new StringSetting("Custom Response", this, "1s bro am doin sum")
            .computeStringSetting(() -> this.respondToNameSetting.isEnabled() && this.customSetting.matchesString2("CUSTOM"));
        this.disconnectSetting = new ModeSetting("When Mentioned", this, "CONTINUE", Arrays.asList("CONTINUE", "AFK", "HUB", "DISCONNECT"))
            .computeModeSetting(() -> this.respondToNameSetting.isEnabled());
        this.chatAlertsSetting = new KeybindListSetting(
            "Chat Alerts",
            this,
            this.respondToNameSetting,
            this.responseMinDelaySecSetting,
            this.responseMaxDelaySecSetting,
            this.customSetting,
            this.sBroAmDoinSum1Setting,
            this.disconnectSetting
        );
        this.checkForTeleportsSetting = new BooleanSetting("Check For Teleports", this, true);
        this.disconnectSetting2 = new ModeSetting("On Teleport", this, "PAUSE", Arrays.asList("CONTINUE", "PAUSE", "AFK", "HUB", "DISCONNECT"))
            .computeModeSetting(() -> this.checkForTeleportsSetting.isEnabled());
        this.teleportCheckSetting = new KeybindListSetting("Teleport Check", this, this.checkForTeleportsSetting, this.disconnectSetting2);
        this.pauseOnOrionVanishSetting = new BooleanSetting("Pause On Orion Vanish", this, false);
        this.oofSetting = new ModeSetting("When Vanished Player Found", this, "AFK", Arrays.asList("CONTINUE", "AFK", "OOF"))
            .computeModeSetting(() -> this.pauseOnOrionVanishSetting.isEnabled());
        this.orionVanishCheckerSetting = new KeybindListSetting("Orion Vanish Checker", this, this.pauseOnOrionVanishSetting, this.oofSetting);
        this.handleSettingArray(
            new Setting[]{
                this.grindingSetting,
                this.combatSetting,
                this.movementSetting,
                this.contractsSetting,
                this.perksAndKillstreaksSetting,
                this.lobbyAndSafetySetting,
                this.majorEventsSetting,
                this.safetyAndRecoverySetting,
                this.performanceSetting,
                this.hudAndDebugSetting,
                this.chatAlertsSetting,
                this.teleportCheckSetting,
                this.orionVanishCheckerSetting
            }
        );
        this.lookupMap2 = new AutoGrinder.Entry();
        this.otherType = OtherType.HUB;
        this.otherType2 = OtherType.HUB;
        this.previousScreen = null;
        this.count4 = 0;
        this.count9 = 0;
        this.factor = 0.0F;
        this.flag4 = false;
        this.utilHelper813 = new StopwatchTimer();
        this.dz = false;
        this.timestamp4 = 0L;
        this.timestamp5 = 0L;
        this.flag5 = false;
        this.text = null;
        this.utilHelper847 = new StopwatchTimer();
        this.flag16 = false;
        this.flag17 = false;
        this.timestamp9 = 0L;
        this.utilHelper825 = new StopwatchTimer();
        this.utilHelper830 = new StopwatchTimer();
        this.entityPlayer = null;
        this.player = null;
        this.utilHelper839 = new StopwatchTimer();
        this.utilHelper814 = new StopwatchTimer();
        this.utilHelper7 = new UtilHelper7();
        this.utilHelper820 = new StopwatchTimer();
        this.dy = false;
        this.vec3 = null;
        this.utilHelper89 = new StopwatchTimer();
        this.utilHelper828 = new StopwatchTimer();
        this.utilHelper837 = new StopwatchTimer();
        this.utilHelper818 = new StopwatchTimer();
        this.flag9 = false;
        this.vec32 = null;
        this.utilHelper819 = new StopwatchTimer();
        this.utilHelper846 = new StopwatchTimer(0L);
        this.utilHelper82 = new StopwatchTimer();
        this.utilHelper8 = new StopwatchTimer();
        this.utilHelper827 = new StopwatchTimer();
        this.utilHelper821 = new StopwatchTimer();
        this.count6 = 0;
        this.flag10 = false;
        this.utilHelper833 = new StopwatchTimer();
        this.flag8 = false;
        this.count8 = HelmetHelper.HelmetMode.HELMET.getContainerSlot();
        this.count5 = HelmetHelper.HelmetMode.BOOTS.getContainerSlot();
        this.count = this.count8;
        this.utilHelper84 = new StopwatchTimer();
        this.clearPathHandler = new ClearPathHandler();
        this.count22 = 0;
        this.flag15 = false;
        this.utilHelper810 = new StopwatchTimer();
        this.utilHelper811 = new StopwatchTimer(0L);
        this.utilHelper829 = new StopwatchTimer(0L);
        this.utilHelper824 = new StopwatchTimer(0L);
        this.utilHelper822 = new StopwatchTimer(0L);
        this.utilHelper83 = new StopwatchTimer(0L);
        this.onSpireFloorChangeHandler = new OnSpireFloorChangeHandler(this);
        this.utilHelper834 = new StopwatchTimer(0L);
        this.flag3 = false;
        this.utilHelper832 = new StopwatchTimer(0L);
        this.count21 = 4000;
        this.utilHelper826 = new StopwatchTimer(0L);
        this.count7 = 0;
        this.utilHelper845 = new StopwatchTimer(0L);
        this.utilHelper86 = new StopwatchTimer();
        this.utilHelper812 = new StopwatchTimer();
        this.cancelHandler2 = new org.brutality.automation.grinding.purchases.CancelHandler(this);
        this.hasItemHandler = new HasItemHandler(
            this.autoBuySetting,
            this.everyMinsSetting,
            this.autoBuyEveryMinsSetting,
            this.obsidian64Setting,
            this.windowClickDelaySetting,
            this.yawSpeedSetting,
            this.pitchSpeedSetting
        );
        this.utilHelper85 = new StopwatchTimer(0L);
        this.utilHelper87 = new StopwatchTimer(0L);
        this.count2 = -1;
        this.utilHelper815 = new StopwatchTimer(0L);
        this.flag6 = false;
        this.utilHelper816 = new StopwatchTimer(0L);
        this.renderHelper2 = new RenderHelper2(0.0F, 1.0F, 1.0F, 0.75F);
        this.blockPos = null;
        this.blockPos2 = null;
        this.utilHelper836 = new StopwatchTimer();
        this.utilHelper831 = new StopwatchTimer();
        this.count3 = -1;
        this.utilHelper835 = new StopwatchTimer(0L);
        this.utilHelper823 = new StopwatchTimer(0L);
        this.silentHandler = new SilentHandler();
        this.utilHelper840 = new StopwatchTimer();
        this.queue = new ArrayDeque<>(8);
        this.utilHelper841 = new StopwatchTimer(0L);
        this.utilHelper842 = new StopwatchTimer(0L);
        this.utilHelper843 = new StopwatchTimer(0L);
        this.utilHelper844 = new StopwatchTimer(0L);
        this.count11 = 20000;
        this.midMode = MovementHandler.MidMode.MID;
        this.utilHelper = new UtilHelper();
        this.utilHelper88 = new StopwatchTimer(0L);
    }

    public void handleString5(String str) {
        MessageUtils.x(str);
    }

    @Override
    public void onEvent(Event event) {
        // VENDOR-DISABLED: if (!EntitlementState.checkState()) {
        if (false) {
            this.handleFlag3(false);
        } else if (event instanceof RenderEvent4) {
            this.computeInvalidateHandler().invalidate();
            this.process8();
            this.lifecycleHandler.reset();
            this.flag = false;
            this.process44();
            this.itemSet.clear();
            this.timestamp = 0L;
            this.flag2 = true;
        } else if (event instanceof GetEntityEvent && ((GetEntityEvent)event).getEntity() == mc.thePlayer) {
            this.process4();
        } else {
            if (event instanceof EventTick) {
                this.onClientTick((EventTick)event);
            } else if (event instanceof ChatEvent) {
                this.onClientChatReceived((ChatEvent)event);
            } else if (event instanceof EventRender3D) {
                this.onEventRender3D((EventRender3D)event);
            } else if (event instanceof EventRender2D) {
                this.onEventRender2D((EventRender2D)event);
            } else if (event instanceof GetEntityEvent3) {
                this.onGetEntityEvent3((GetEntityEvent3)event);
            } else if (event instanceof AttackEvent) {
                this.onAttackEntity((AttackEvent)event);
            } else if (event instanceof PitEvent) {
                this.onBanDuration((PitEvent)event);
            } else if (event instanceof PitEvent2) {
                this.onBountyClaim((PitEvent2)event);
            } else if (event instanceof PitEvent3) {
                this.onChatMention((PitEvent3)event);
            } else if (event instanceof PitEvent4) {
                this.onContractCompleted((PitEvent4)event);
            } else if (event instanceof PitEvent5) {
                this.onContractFailed((PitEvent5)event);
            } else if (event instanceof PitEvent6) {
                this.onContractStarted((PitEvent6)event);
            } else if (event instanceof PitEvent7) {
                this.onKoth((PitEvent7)event);
            } else if (event instanceof MajorFinishedEvent) {
                this.onMajorEventFinished((MajorFinishedEvent)event);
            } else if (event instanceof PitEvent8) {
                this.onMegastreakStarted((PitEvent8)event);
            } else if (event instanceof PitEvent9) {
                this.onMysticItemDrop((PitEvent9)event);
            } else if (event instanceof PitEvent10) {
                this.onPitLevelUp((PitEvent10)event);
            } else if (event instanceof PitEvent11) {
                this.onPitStateUpdate((PitEvent11)event);
            } else if (event instanceof PitEvent12) {
                this.onPlayerKilled((PitEvent12)event);
            } else if (event instanceof PitEvent17) {
                this.onVileTradeCompleted((PitEvent17)event);
            } else if (event instanceof PitEvent16) {
                this.onSpireFloorChangeHandler.onSpireFloorChange((PitEvent16)event);
            }
        }
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        if (player == null || player.worldObj == null) {
            return false;
        }
        if (FreshHelper.matchesWorld3(player.worldObj, player)) {
            return true;
        }
        if (!ServerUtils.checkState5()) {
            return false;
        }
        int n = (int)this.spawnYLevelSetting.computeDouble();
        return n > 0
            && player.posX > -23.0
            && player.posX < 23.0
            && player.posZ > -23.0
            && player.posZ < 23.0
            && player.posY > n - 2.0
            && player.posY < n + 12.0;
    }

    private BlockPos computeBlockPos2() {
        if (mc.theWorld == null) {
            return null;
        }
        int n = ConnectToServerHandler.computeInt4();
        BlockPos pos = null;
        double MAX_VALUE = Double.MAX_VALUE;

        for (int i = -6; i <= 6; i++) {
            for (int j = n - 1; j <= n + 3; j++) {
                for (int k = -6; k <= 6; k++) {
                    BlockPos pos2 = new BlockPos(i, j, k);
                    if (mc.theWorld.getBlockState(pos2).getBlock() == Blocks.noteblock) {
                        double distanceSq = mc.thePlayer.getDistanceSq(pos2.getX() + 0.5, pos2.getY() + 0.5, pos2.getZ() + 0.5);
                        if (distanceSq < MAX_VALUE) {
                            MAX_VALUE = distanceSq;
                            pos = pos2;
                        }
                    }
                }
            }
        }

        return pos;
    }

    private void process6() {
        this.blockPos = null;
        ComputeScheduledFutureHelper.processRunnable(() -> {
            if (this.checkState44()) {
                if (this.checkState33()) {
                    this.process26();
                }
            }
        }, 10000L, TimeUnit.MILLISECONDS);
    }

    private boolean checkState4() {
        boolean flag = mc.thePlayer != null && mc.theWorld != null;
        boolean flag2 = flag && (mc.thePlayer.isDead || mc.thePlayer.getHealth() <= 0.0F);
        boolean flag3 = flag && !flag2 && this.matchesEntityPlayer(mc.thePlayer);
        InvalidateHandler.NoneMode noneMode = this.computeInvalidateHandler().computeNoneMode(mc.thePlayer, mc.theWorld, flag2, flag3);
        if (noneMode != InvalidateHandler.NoneMode.NONE) {
            this.process8();
            if (flag && this.killAuraSetting != null) {
                this.process22();
            }
        }

        if (flag2) {
            this.process4();
        }

        boolean flag4 = flag && mc.theWorld.isBlockLoaded(new BlockPos(mc.thePlayer.posX, 0.0, mc.thePlayer.posZ));
        if (flag && !flag2 && flag4) {
            return false;
        }
        if (mc.gameSettings != null) {
            KeybindStateUtil.process();
        }

        return true;
    }

    private boolean checkState40() {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() == Items.diamond_sword) {
                return true;
            }
        }

        return false;
    }

    private void handleMidMode(MovementHandler.MidMode midMode) {
        if (midMode != null && midMode != this.midMode) {
            this.midMode = midMode;
            this.player = null;
            this.utilHelper830.clear();
            this.utilHelper839.reset();
            ObsidianPlacerHandler.clearPath();
            this.handleString5("Auto biome: heading to " + midMode.getDisplayName() + ".");
        }
    }

    private void handleSet(Set<MovementHandler.MidMode> midModes) {
        var map = this.computeMap();
        MovementHandler.MidMode midMode = null;
        int n = -1;

        for (MovementHandler.MidMode midMode2 : midModes) {
            int orDefault = map.getOrDefault(midMode2, 0);
            if (orDefault > n) {
                n = orDefault;
                midMode = midMode2;
            }
        }

        if (midMode == null) {
            midMode = MovementHandler.MidMode.MID;
            n = 0;
        }

        int orDefault2 = map.getOrDefault(this.midMode, 0);
        boolean flag = midModes.contains(this.midMode);
        boolean flag2 = n < this.minPlayersSetting.computeDouble();
        if (flag2) {
            if (midMode != this.midMode) {
                this.midMode = midMode;
                this.utilHelper844.reset();
            }

            this.flag11 = true;
            this.utilHelper842.reset();
        } else {
            this.flag11 = false;
            boolean flag3 = midMode != this.midMode && (!flag || n > orDefault2 + 2 && this.utilHelper844.matchesLong(20000L));
            if (flag3) {
                this.handleMidMode(midMode);
                this.utilHelper844.reset();
            }

            this.utilHelper842.reset();
        }
    }

    public void onBountyClaim(PitEvent2 pitEvent2) {
        if (pitEvent2.text.equals(mc.thePlayer.getName())) {
            this.handleString(String.valueOf(new StringBuilder("Claimed bounty on ").append(pitEvent2.text2).append(": ").append(pitEvent2.count).append("g")), Color.RED);
        }
    }

    public static boolean checkState37() {
        return instance != null && instance.checkState44() && instance.smoothCameraSetting.isEnabled();
    }

    private void process4() {
        if (!this.flag) {
            this.flag = true;
            this.computeInvalidateHandler().invalidate();
            this.process8();
            this.flag2 = true;
            this.player = null;
            this.process44();
            this.process22();
            if (this.lifecycleHandler.matchesFlag(this.stopAfterDeathSetting.isEnabled(), (int)this.deathStopStreakSetting.computeDouble())) {
                this.handleString5("Stopped after death at streak " + this.lifecycleHandler.getCount() + ".");
                KeybindStateUtil.process();
                this.process32();
            }

            this.lifecycleHandler.reset();
        }
    }

    public static void process46() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
    }

    private boolean checkState33() {
        return !this.utilHelper823.matchesLong(62000L);
    }

    public void onMajorEventFinished(MajorFinishedEvent majorFinishedEvent) {
        this.process3();
        this.flag13 = false;
        if (this.checkState8()) {
            this.clearPathHandler2.clearPath();
            KeybindStateUtil.process();
        }

        this.handleString(
            String.valueOf(new StringBuilder("Major event (").append(majorFinishedEvent.computeString2()).append(") rewards: ").append(majorFinishedEvent.computeString())), Color.MAGENTA
        );
        if (this.checkState11()
            && !FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)
            && (
                this.majoreventsonlySetting.matchesString2("MAJOR_EVENTS_ONLY")
                    || AutoGrinder.OffMode.valueOf(this.onalwaysdropSetting.getText()) == AutoGrinder.OffMode.ON_ONLY_DROP_FOR_DOABLE_CONTRACTS
            )) {
            int n = (int)(Math.random() * 4000.0 + 2000.0);
            MessageUtils.x(
                String.valueOf(
                    new StringBuilder().append(EnumChatFormatting.GREEN).append("Major event finished, ").append(EnumChatFormatting.DARK_RED).append("oofing.")
                )
            );
            PartyWarpType.OOF.handleInt(n);
        }
    }

    public BlockPos computeBlockPos() {
        if (this.checkState20() && this.midMode != MovementHandler.MidMode.MID && !this.flag11) {
            int n = mc.thePlayer == null ? ConnectToServerHandler.computeInt4() : (int)Math.floor(mc.thePlayer.posY);
            return new BlockPos(this.midMode.getSearchX(), n, this.midMode.getSearchZ());
        }
        return null;
    }

    private AutoGrinder.IronMode computeIronMode() {
        return AutoGrinder.IronMode.valueOf(this.majoreventsonlySetting.getText());
    }

    public void process40() {
        this.utilHelper.clear();
    }

    private boolean onEventTick(EventTick event) {
        if (this.dz) {
            if (event.getState() != EventState.POST) {
                return true;
            }
            long now = System.currentTimeMillis();
            if (now < this.timestamp4) {
                return true;
            }
            this.dz = false;
            this.flag5 = false;
            this.text = null;
            this.utilHelper813.reset();
            if (mc.thePlayer != null && mc.theWorld != null) {
                mc.thePlayer.sendChatMessage("/play pit");
            }

            MessageUtils.x(EnumChatFormatting.GREEN + "[AutoGrinder] Break finished. Rejoining the Pit.");
            return true;
        }
        if (!this.takeScheduledBreaksSetting.isEnabled()) {
            if (event.getState() == EventState.POST) {
                this.utilHelper813.reset();
            }

            return false;
        }
        if (event.getState() == EventState.POST && mc.thePlayer != null && mc.theWorld != null) {
            long l = (long)this.breakEveryMinsSetting.computeDouble() * 60000L;
            if (!this.utilHelper813.matchesLong(l)) {
                return false;
            }
            long l2 = (long)this.breakLengthMinsSetting.computeDouble() * 60000L;
            this.timestamp4 = System.currentTimeMillis() + l2;
            this.flag5 = false;
            this.dz = true;
            this.player = null;
            this.entityPlayer = null;
            KeybindStateUtil.process();
            this.process22();
            int n = (int)this.breakLengthMinsSetting.computeDouble();
            mc.thePlayer.sendChatMessage("/lobby");
            MessageUtils.x(EnumChatFormatting.AQUA + "[AutoGrinder] Taking a " + n + " minute break in the lobby to lower suspicion.");
            return true;
        }
        return false;
    }

    public void onClientTick(EventTick event) {
        if (this.checkState44()) {
            if (this.checkState4()) {
                return;
            }

            if (event.getState() == EventState.PRE && mc.thePlayer != null && mc.theWorld != null) {
                if (!this.flag) {
                    this.process34();
                }

                this.handleLong(System.currentTimeMillis());
                if (mc.thePlayer.isEntityAlive() && !this.matchesEntityPlayer(mc.thePlayer)) {
                    this.flag = false;
                }
            }

            this.process21();
            this.process16();
            if (this.onEventTick(event)) {
                return;
            }

            if (this.scheduleSetting.isEnabled()) {
                int hour = LocalTime.now().getHour();
                int n = (int)this.scheduleStartHourSetting.computeDouble();
                int n2 = (int)this.scheduleStopHourSetting.computeDouble();
                if (n < n2) {
                    if (hour < n || hour >= n2) {
                        return;
                    }
                } else if (n > n2 && hour < n && hour >= n2) {
                    return;
                }
            }

            if (mc.thePlayer != null && mc.theWorld != null) {
                this.process33();
                if (event.getState() == EventState.POST) {
                    if (this.count3 != -1 && this.utilHelper835.matchesLong(30000L)) {
                        mc.gameSettings.renderDistanceChunks = this.count3;
                        this.count3 = -1;
                        MessageUtils.x("Reverted render distance after KOTH");
                    }

                    boolean flag = ConnectToServerHandler.checkState2();
                    if (!ServerUtils.checkState4() && !flag && !ConnectToServerHandler.checkState10() && !ConnectToServerHandler.checkState4()) {
                        this.otherType = OtherType.OTHER;
                    } else if (ConnectToServerHandler.checkState4()) {
                        this.otherType = OtherType.LIMBO;
                    } else if (!flag) {
                        this.otherType = OtherType.HUB;
                    } else {
                        label1020: {
                            if (this.otherType == OtherType.SPAWN_SQUADS || this.otherType == OtherType.DOWN_SQUADS) {
                                boolean flag2 = this.checkState24() && ConnectToServerHandler.getPizzaType() == PizzaType.SQUADS;
                                if (flag2) {
                                    break label1020;
                                }

                                this.otherType = FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer) ? OtherType.SPAWN : OtherType.DOWN_MID;
                                this.handleString5("Squads event ended - resuming normal grinding.");
                            }

                            PizzaType pizzaType = ConnectToServerHandler.getPizzaType();
                            if (this.checkState24()) {
                                switch (pizzaType) {
                                    case SPIRE:
                                        if (FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                                            this.otherType = OtherType.SPAWN_SPIRE;
                                        } else {
                                            this.otherType = OtherType.DOWN_SPIRE;
                                        }
                                        break label1020;
                                    case BLOCKHEAD:
                                        if (FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                                            this.otherType = OtherType.SPAWN_BLOCKHEAD;
                                        } else {
                                            this.otherType = OtherType.DOWN_BLOCKHEAD;
                                        }
                                        break label1020;
                                    case RAFFLE:
                                        this.process23();
                                        this.otherType = FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer) ? OtherType.SPAWN_RAFFLE : OtherType.DOWN_RAFFLE;
                                        break label1020;
                                    case SQUADS:
                                        if (FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                                            this.otherType = OtherType.SPAWN_SQUADS;
                                        } else {
                                            this.otherType = OtherType.DOWN_SQUADS;
                                        }
                                        break label1020;
                                }
                            }

                            if (FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                                boolean flag3 = OnClientChatReceivedHandler.computeKillsType() == KillsType.KILLS_NAKED;
                                int n3 = SilentHandler.computeInt();
                                boolean flag4 = SilentHandler.checkState();
                                boolean flag5 = this.silentHandler.checkState3();
                                boolean flag6 = SilentHandler.checkState2();
                                if (this.useFreshPantsSetting.isEnabled() && !flag3 && (n3 != -1 || flag5) && (!flag4 || flag5) && (flag6 || flag5)) {
                                    this.otherType = OtherType.SPAWN_EQUIPPING_MYSTIC_PANTS;
                                } else if (this.count6 < 9) {
                                    this.otherType = OtherType.SPAWN_MOVING_MYSTICS;
                                } else if (GetNonBotPlayersHelper.computeBlockPos5() != null
                                    && AutoGrinder.ContinueMode2.valueOf(this.disableanddisconnectSetting3.getText()) == AutoGrinder.ContinueMode2.PRESTIGE
                                    && BEASTHelper.checkState2()
                                    && !this.flag8
                                    && !org.brutality.automation.grinding.purchases.ClearPathHandler.checkState()) {
                                    this.otherType = OtherType.SPAWN_PRESTIGING;
                                } else {
                                    boolean flag7 = this.matchesInt(GetNonBotPlayersHelper.computeInt()) || this.matchesDouble(this.getAverageMidPlayerCount());
                                    if (AutoGrinder.OffMode.valueOf(this.onalwaysdropSetting.getText()) != AutoGrinder.OffMode.OFF
                                        && this.count >= this.count8
                                        && this.count <= this.count5
                                        && !this.majoreventsonlySetting.matchesString2("MYSTICS")
                                        && OnClientChatReceivedHandler.computeKillsType() == KillsType.KILLS_NAKED
                                        && !flag7) {
                                        this.otherType = OtherType.SPAWN_TAKING_OFF_ARMOR;
                                    } else {
                                        boolean flag8;
                                        if (ConnectToServerHandler.checkState6()) {
                                            flag8 = false;
                                        } else {
                                            int n4 = BEASTHelper.computeInt2();
                                            boolean flag9 = n4 - this.count2 >= 10 && n4 > 25 && this.smartPerkCooldownSetting.isEnabled();
                                            long l = (long)this.autoPerkFrequencySetting.computeDouble() * 60L * 1000L;
                                            boolean flag10 = this.utilHelper85.matchesLong(l) || flag9;
                                            boolean flag11 = this.utilHelper87.matchesLong(180000L);
                                            flag8 = flag10 && this.autoPerkSetting.isEnabled() && flag11;
                                        }

                                        if (flag8) {
                                            this.otherType = OtherType.SPAWN_PICKING_PERKS;
                                        } else if (this.hasItemHandler.checkState2()) {
                                            this.otherType = OtherType.SPAWN_BUYING;
                                        } else {
                                            AutoGrinder.OffMode offMode = AutoGrinder.OffMode.valueOf(this.onalwaysdropSetting.getText());
                                            AutoGrinder.IronMode ironMode = this.computeIronMode();
                                            BlockPos pos = GetNonBotPlayersHelper.computeBlockPos3();
                                            boolean flag12 = OnClientChatReceivedHandler.checkState();
                                            BlockPos pos2;
                                            if ((
                                                    ironMode == AutoGrinder.IronMode.MAJOR_EVENTS_ONLY
                                                        ? false
                                                        : (
                                                            pos == null
                                                                ? false
                                                                : (
                                                                    (
                                                                            (pos2 = GetNonBotPlayersHelper.computeBlockPos3()) == null
                                                                                ? true
                                                                                : !BuildTargetComparatorHelper.matchesEntityPlayer(
                                                                                    mc.thePlayer,
                                                                                    new Vec3(pos2.getX() + 0.5, pos2.getY() + 1.0, pos2.getZ() + 0.5)
                                                                                )
                                                                        )
                                                                        ? false
                                                                        : (
                                                                            offMode == AutoGrinder.OffMode.OFF
                                                                                ? false
                                                                                : (
                                                                                    this.flag15
                                                                                        ? false
                                                                                        : (
                                                                                            offMode == AutoGrinder.OffMode.ON_ONLY_DROP_FOR_DOABLE_CONTRACTS
                                                                                                    && flag12
                                                                                                ? (
                                                                                                    OnClientChatReceivedHandler.computeContract() == null
                                                                                                        ? false
                                                                                                        : !this.checkState26()
                                                                                                )
                                                                                                : (
                                                                                                    offMode == AutoGrinder.OffMode.ON_ALWAYS_DROP && !flag12
                                                                                                        ? true
                                                                                                        : true
                                                                                                )
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                                && !ConnectToServerHandler.checkState6()) {
                                                this.otherType = OtherType.SPAWN_PICKING_CONTRACT;
                                            } else {
                                                this.otherType = OtherType.SPAWN;
                                            }
                                        }
                                    }
                                }
                            } else if (this.vec32 == null
                                ? false
                                : (
                                    GetMysticEnchantLoreHelper.checkState3()
                                        ? false
                                        : (
                                            !BuildTargetComparatorHelper.matchesEntityPlayer(mc.thePlayer, this.vec32)
                                                ? false
                                                : (
                                                    this.utilHelper846.matchesLong(30000L)
                                                        ? false
                                                        : (
                                                            !this.pickUpMysticDropsSetting.isEnabled()
                                                                ? false
                                                                : this.vec32.distanceTo(mc.thePlayer.getPositionVector())
                                                                    <= this.mysticPickupDistanceSetting.computeDouble()
                                                        )
                                                )
                                        )
                                )) {
                                if (GetDisplayedTitleHelper.matchesEntityPlayer2(mc.thePlayer)) {
                                    this.otherType = OtherType.DOWN_MID;
                                } else {
                                    this.otherType = OtherType.DOWN_OUTSIDE;
                                }
                            } else if (GetDisplayedTitleHelper.matchesEntityPlayer2(mc.thePlayer)) {
                                this.otherType = OtherType.DOWN_MID;
                            } else {
                                this.otherType = OtherType.DOWN_OUTSIDE;
                            }
                        }
                    }

                    if (this.otherType == OtherType.OTHER) {
                        return;
                    }

                    if (ConnectToServerHandler.checkState11()) {
                        this.player = null;
                        this.cancelHandler2.cancel();
                        KeybindStateUtil.process();
                        this.process22();
                        this.utilHelper89.reset();
                        this.utilHelper839.reset();
                        this.utilHelper820.reset();
                        return;
                    }

                    if (this.otherType2 != OtherType.SPAWN_PICKING_PERKS && this.otherType == OtherType.SPAWN_PICKING_PERKS) {
                        this.cancelHandler2.process2();
                    } else if (this.otherType2 == OtherType.SPAWN_PICKING_PERKS && this.otherType != OtherType.SPAWN_PICKING_PERKS) {
                        this.cancelHandler2.cancel();
                    }

                    boolean usesMovement = this.otherType.usesMovement;
                    boolean usesMovement2 = this.otherType2.usesMovement;
                    if (this.otherType2 != OtherType.HUB && this.otherType == OtherType.HUB) {
                        this.player = null;
                        KeybindStateUtil.process();
                        this.process22();
                        this.handleString4("onHub triggered");
                    }

                    if (!usesMovement2 && usesMovement) {
                        this.hasItemHandler.process2();
                        ObsidianPlacerHandler.flag2 = false;
                        this.silentHandler.reset();
                        KeybindStateUtil.process();
                        org.brutality.automation.grinding.movement.ClearPathHandler.process();
                        ClearPathHandler3.clearPath();
                        this.clearPathHandler.clearPath();
                        this.process22();
                        this.utilHelper89.reset();
                        this.count6 = 0;
                        this.flag10 = false;
                        this.flag8 = false;
                        this.flag15 = false;
                        this.count22 = 0;
                        this.player = null;
                        this.cancelHandler2.reset();
                        this.utilHelper845.reset();
                        if (AutoGrinder.OffMode.valueOf(this.onalwaysdropSetting.getText()) != AutoGrinder.OffMode.OFF) {
                            this.process42();
                        }

                        if (Math.random() < this.afkChanceOnSpawnSetting.computeDouble() / 100.0 && !this.flag3) {
                            this.utilHelper832.reset();
                            this.count21 = (int)(4000L + (long)((Math.random() * 0.6 - 0.3) * 4000.0));
                            this.handleString5(String.valueOf(new StringBuilder("AFKing in spawn for ").append(this.count21 / 1000).append(" seconds.")));
                            ChooseTargetPositionHandler.chooseTargetPosition();
                            this.flag3 = true;
                        }

                        if (this.computeIronMode().pauseAfterSpawn) {
                            this.utilHelper826.reset();
                            this.count7 = (int)(500.0 + Math.random() * 750.0);
                            this.handleString5(String.valueOf(new StringBuilder("Pausing in spawn for ").append(this.count7 / 1000.0).append(" seconds.")));
                        } else {
                            this.count7 = 0;
                        }

                        this.handleString4("onSpawn triggered");
                    }

                    if (usesMovement2 && this.otherType == OtherType.DOWN_MID) {
                        long timestamp = this.computeInvalidateHandler().getTimestamp();
                        EntityPlayerSP player = mc.thePlayer;
                        WorldClient world = mc.theWorld;
                        ComputeScheduledFutureHelper.processRunnable(
                            () -> {
                                if (this.checkState44()
                                    && this.computeInvalidateHandler().matchesLong(timestamp, mc.thePlayer, mc.theWorld)
                                    && mc.thePlayer == player
                                    && mc.theWorld == world
                                    && !mc.thePlayer.isDead
                                    && !(mc.thePlayer.getHealth() <= 0.0F)
                                    && !this.matchesEntityPlayer(mc.thePlayer)) {
                                    boolean flag13 = GetDisplayedTitleHelper.computeDouble(mc.thePlayer) <= UtilHelper5.computeInt() + 9;
                                    if (this.player != null && flag13) {
                                        this.process41();
                                    }
                                }
                            },
                            this.computeInt2(),
                            TimeUnit.MILLISECONDS
                        );
                        this.utilHelper815.reset();
                        this.flag6 = true;
                        this.utilHelper816.reset();
                        this.process22();
                        this.utilHelper820.reset();
                        this.utilHelper89.reset();
                        this.utilHelper839.reset();
                        this.flag3 = false;
                        if (GetMysticEnchantLoreHelper.checkState5()) {
                            mc.thePlayer.closeScreen();
                        }

                        this.handleString4("onDropDown triggered");
                    }

                    if (this.utilHelper828.matchesLong(650L)) {
                        this.utilHelper828.reset();
                        Vec3 positionVector = mc.thePlayer.getPositionVector();
                        Vec3 vec3 = this.vec3;
                        byte b;
                        if (this.vec3 == null) {
                            b = 1;
                        } else {
                            double d = Math.abs(positionVector.xCoord - vec3.xCoord);
                            double d2 = Math.abs(positionVector.zCoord - vec3.zCoord);
                            b = (byte)(!(d > 0.05) && !(d2 > 0.05) ? 0 : 1);
                        }

                        if (b != 0) {
                            this.utilHelper89.reset();
                        }

                        this.vec3 = mc.thePlayer.getPositionVector();
                    }

                    if (this.otherType != OtherType.SPAWN && !this.utilHelper846.matchesLong(30000L) && this.utilHelper819.matchesLong(250L)) {
                        this.utilHelper819.reset();
                        this.scanMysticDrops(System.currentTimeMillis());
                        this.vec33 = GetNonBotPlayersHelper.computeList3()
                            .stream()
                            .filter(entity -> Item.getIdFromItem(entity.getEntityItem().getItem()) == 266)
                            .map(entity2 -> new Vec3(entity2.posX, entity2.posY, entity2.posZ))
                            .min(Comparator.comparingDouble(vec -> vec.distanceTo(mc.thePlayer.getPositionVector())))
                            .orElse(null);
                    }

                    if (ConnectToServerHandler.checkState2() && this.utilHelper840.matchesLong(600L)) {
                        this.utilHelper840.reset();
                        int n5 = FreshHelper.computeInt2();
                        if (this.queue.size() < 5) {
                            this.queue.add(n5);
                        } else {
                            this.queue.poll();
                            this.queue.add(n5);
                        }
                    }

                    if (this.utilHelper812.matchesLong((long)this.disableAfterMinsSetting.computeDouble() * 60L * 1000L)) {
                        this.handleString5(
                            String.valueOf(
                                new StringBuilder("Disabling AutoGrinder because the ")
                                    .append(this.disableAfterMinsSetting.getName())
                                    .append(" setting is set to ")
                                    .append(this.disableAfterMinsSetting.computeDouble())
                                    .append(" minutes")
                            )
                        );
                        this.process32();
                        this.handleString(
                            String.valueOf(
                                new StringBuilder("AutoGrinder auto-disabled after ").append(this.disableAfterMinsSetting.computeDouble()).append(" minutes")
                            ),
                            Color.LIGHT_GRAY
                        );
                    } else {
                        int n6;
                        if ((n6 = CoreHandler5.computeInt()) < this.minPitPlayersToRunSetting.computeDouble() && n6 != -1) {
                            if (this.utilHelper88.matchesLong(60000L)) {
                                String str = String.valueOf(
                                    new StringBuilder()
                                        .append(EnumChatFormatting.YELLOW)
                                        .append("Only ")
                                        .append(n6)
                                        .append(" players in the pit, waiting for at least ")
                                        .append(this.minPitPlayersToRunSetting.computeDouble())
                                        .append(" to run AutoGrinder.")
                                );
                                MessageUtils.x(str);
                                this.utilHelper88.reset();
                            }

                            KeybindStateUtil.process();
                            this.utilHelper89.reset();
                        } else {
                            if (FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                            }

                            AutoGrinder.DoNothingMode doNothingMode = AutoGrinder.DoNothingMode.valueOf(this.disableanddisconnectSetting2.getText());
                            boolean flag14;
                            if (this.utilHelper834.matchesLong(60000L * (int)this.banAFKLengthMinsSetting.computeDouble())) {
                                flag14 = false;
                            } else {
                                switch (doNothingMode) {
                                    case SWAP_LOBBY:
                                        if (ConnectToServerHandler.checkState2()) {
                                            this.handleString6("Ban detected, swapping lobbies...");
                                        } else {
                                            this.utilHelper834.clear();
                                        }
                                        break;
                                    case HUB_AND_AFK:
                                        if (ConnectToServerHandler.checkState2()) {
                                            this.handleString6("Ban detected, going to hub and afking...");
                                        }
                                        break;
                                    case OOF_AND_AFK:
                                        if (ConnectToServerHandler.checkState2() && !FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                                            ObsidianPlacerHandler.matchesFlag(this.soundAlertsSetting.isEnabled(), "Ban detected, oofing and afking...");
                                        }
                                        break;
                                    case HUB_AND_DISABLE:
                                        if (ConnectToServerHandler.checkState2()) {
                                            this.handleString6("Ban detected, going to hub and disabling AutoGrinder...");
                                        } else {
                                            this.process32();
                                            this.handleString("AutoGrinder disabled due to ban in hub", Color.LIGHT_GRAY);
                                        }
                                        break;
                                    case DISABLE_AND_DISCONNECT:
                                        if (mc.thePlayer != null) {
                                            this.process22();
                                            this.process32();
                                            Date date = new Date();
                                            ConnectToServerHandler.handleString(
                                                String.valueOf(
                                                    new StringBuilder()
                                                        .append(EnumChatFormatting.RED)
                                                        .append("[AutoGrinder] Disconnected due to a ban in the lobby at ")
                                                        .append(EnumChatFormatting.YELLOW)
                                                        .append(date)
                                                )
                                            );
                                            this.utilHelper834.clear();
                                            this.handleString("AutoGrinder disabled due to ban in hub", Color.LIGHT_GRAY);
                                        }
                                        break;
                                    case DO_NOTHING:
                                        String str2 = String.valueOf(
                                            new StringBuilder("Ban detected, but not doing anything because the on-ban setting is on ")
                                                .append(AutoGrinder.DoNothingMode.DO_NOTHING)
                                        );
                                        MessageUtils.x(str2);
                                        this.utilHelper834.clear();
                                }

                                flag14 = true;
                            }

                            if (!flag14) {
                                if (this.otherType == OtherType.LIMBO) {
                                    this.handleString6("In Limbo, hubbing...");
                                } else if (this.otherType == OtherType.HUB) {
                                    if (flag) {
                                        KeybindStateUtil.process();
                                        this.utilHelper89.reset();
                                    } else if (!this.checkState12()
                                        && this.dropDownSetting.isEnabled()
                                        && !ConnectToServerHandler.checkState8()
                                        && this.utilHelper8.matchesLong(6500L)) {
                                        if (mc.currentScreen != null) {
                                            mc.thePlayer.closeScreen();
                                        } else {
                                            this.utilHelper8.reset();
                                            PartyWarpType.PLAY_PIT.handleInt(1000);
                                            this.handleString5("Joining a new Pit lobby");
                                        }
                                    }
                                } else {
                                    AutoGrinder.ContinueMode2 continueMode2 = AutoGrinder.ContinueMode2.valueOf(this.disableanddisconnectSetting3.getText());
                                    if (!BEASTHelper.checkState2()) {
                                        flag14 = false;
                                    } else {
                                        label775: {
                                            String name = this.disableanddisconnectSetting3.getName();
                                            switch (continueMode2) {
                                                case CONTINUE:
                                                    flag14 = false;
                                                    break label775;
                                                case PRESTIGE:
                                                    flag14 = false;
                                                    break label775;
                                                case DISABLE:
                                                    String str3 = String.valueOf(
                                                        new StringBuilder("Hit level 120, disabling AutoGrinder because the ")
                                                            .append(name)
                                                            .append(" setting is set to ")
                                                            .append(AutoGrinder.ContinueMode2.DISABLE)
                                                    );
                                                    MessageUtils.x(str3);
                                                    this.process32();
                                                    break;
                                                case DISABLE_AND_DISCONNECT:
                                                    String str4 = String.valueOf(
                                                        new StringBuilder("Hit level 120, disconnecting and disabling AutoGrinder because the ")
                                                            .append(name)
                                                            .append(" setting is set to ")
                                                            .append(AutoGrinder.ContinueMode2.DISABLE_AND_DISCONNECT)
                                                    );
                                                    MessageUtils.x(str4);
                                                    this.process32();
                                                    Date date2 = new Date();
                                                    ConnectToServerHandler.handleString(
                                                        String.valueOf(
                                                            new StringBuilder()
                                                                .append(EnumChatFormatting.AQUA)
                                                                .append("[AutoGrinder] Disconnected due to hitting level 120 at ")
                                                                .append(EnumChatFormatting.YELLOW)
                                                                .append(date2)
                                                        )
                                                    );
                                            }

                                            flag14 = true;
                                        }
                                    }

                                    if (!flag14) {
                                        if (GetHoveredSlotHelper.checkState2() || GetHoveredSlotHelper.checkState()) {
                                            KeybindStateUtil.process();
                                            this.utilHelper89.reset();
                                        } else if (this.otherType == OtherType.SPAWN_EQUIPPING_MYSTIC_PANTS) {
                                            this.silentHandler
                                                .handleString(
                                                    this.silentSetting.getText(),
                                                    (long)this.freshPantsDelaySetting.computeDouble(),
                                                    this.freshPantsSafeModeSetting.isEnabled()
                                                );
                                            this.utilHelper89.reset();
                                        } else if (this.otherType != OtherType.SPAWN_MOVING_MYSTICS) {
                                            if (this.otherType == OtherType.SPAWN_PRESTIGING) {
                                                if (!this.checkState12()) {
                                                    org.brutality.automation.grinding.purchases.ClearPathHandler.handleAutoGrinder(this);
                                                }
                                            } else if (this.otherType == OtherType.SPAWN_TAKING_OFF_ARMOR) {
                                                assert this.count >= this.count8 && this.count <= this.count5;

                                                KeybindStateUtil.process();
                                                if (this.utilHelper84.matchesLong(400L)) {
                                                    if (!(mc.currentScreen instanceof GuiInventory)) {
                                                        if (mc.currentScreen != null) {
                                                            mc.thePlayer.closeScreen();
                                                        } else {
                                                            mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
                                                            this.utilHelper84.reset();
                                                        }
                                                    } else {
                                                        this.utilHelper84.reset();
                                                        HelmetHelper.handleInt(this.count);
                                                        this.count++;
                                                        if (this.count > this.count5) {
                                                            this.utilHelper89.reset();
                                                            Brutality.scheduledExecutorService.schedule(() -> mc.addScheduledTask(() -> {
                                                                if (mc.thePlayer != null && mc.currentScreen != null) {
                                                                    mc.thePlayer.closeScreen();
                                                                }
                                                            }), 200L, TimeUnit.MILLISECONDS);
                                                        }
                                                    }
                                                }
                                            } else if (this.otherType == OtherType.SPAWN_BUYING) {
                                                this.hasItemHandler.tick();
                                            } else if (this.otherType == OtherType.SPAWN_PICKING_PERKS) {
                                                if (!this.checkState12()) {
                                                    this.cancelHandler2.tick();
                                                }
                                            } else if (this.otherType == OtherType.SPAWN_PICKING_CONTRACT) {
                                                this.handleContractSelection();
                                            } else if (this.otherType == OtherType.SPAWN) {
                                                this.process20();
                                                this.process36();
                                                this.process13();
                                                this.utilHelper820.reset();
                                                boolean flag15 = this.checkState24();
                                                if (this.checkState12()) {
                                                    this.utilHelper89.reset();
                                                } else {
                                                    if (this.utilHelper86.matchesLong(250L)) {
                                                        this.utilHelper86.reset();
                                                        if (GetMysticEnchantLoreHelper.checkState5()) {
                                                            mc.thePlayer.closeScreen();
                                                        }
                                                    }

                                                    if (!this.utilHelper826.matchesLong(this.count7)) {
                                                        KeybindStateUtil.process();
                                                        this.utilHelper89.reset();
                                                    } else if (!this.utilHelper832.matchesLong(this.count21)) {
                                                        this.utilHelper89.reset();
                                                        ChooseTargetPositionHandler.handleInt(45, 20);
                                                    } else {
                                                        label1025: {
                                                            PizzaType pizzaType2 = ConnectToServerHandler.getPizzaType();
                                                            boolean flag16;
                                                            if ((flag16 = ConnectToServerHandler.checkState6()) && !this.matchesPizzaType(pizzaType2)) {
                                                                flag14 = false;
                                                            } else if (!this.utilHelper83.matchesLong(61000L)) {
                                                                flag14 = false;
                                                            } else {
                                                                long l2;
                                                                long l3;
                                                                boolean flag17 = (l2 = CoreHandler.getTimestamp()) <= 110000L
                                                                        && ((l3 = l2 - -1L) == 0L ? 0 : (l3 < 0L ? -1 : 1)) != 0
                                                                    || CoreHandler4.checkState();
                                                                flag14 = !(this.majoreventsonlySetting.matchesString2("MAJOR_EVENTS_ONLY") && !flag17 && !flag16)
                                                                    && this.dropDownSetting.isEnabled() && !GetMysticEnchantLoreHelper.checkState5();
                                                            }

                                                            if (flag14) {
                                                                boolean flag18;
                                                                if (!this.lowLifeCheckSetting.isEnabled()) {
                                                                    flag18 = false;
                                                                } else if (!this.utilHelper82.matchesLong(1000L)) {
                                                                    flag18 = false;
                                                                } else {
                                                                    this.utilHelper82.reset();
                                                                    int n7 = 0;

                                                                    while (true) {
                                                                        if (n7 >= 40) {
                                                                            flag18 = false;
                                                                            break;
                                                                        }

                                                                        ItemStack stack;
                                                                        if ((stack = mc.thePlayer.inventory.getStackInSlot(n7)) != null) {
                                                                            String displayName = stack.getDisplayName();
                                                                            MysticItemInfo mysticItemInfo = new MysticItemInfo(stack);
                                                                            if (!displayName.contains("Fresh")
                                                                                && !displayName.contains("Mystic Sword")
                                                                                && !displayName.contains("Mystic Bow")) {
                                                                                int n8 = GetMysticEnchantLoreHelper.computeIntArray(stack)[0];
                                                                                if (mysticItemInfo.count3 >= 3 && n8 != 0 && n8 != -1 && n8 < 3) {
                                                                                    String str5 = String.valueOf(
                                                                                        new StringBuilder("Low life item (")
                                                                                            .append(displayName)
                                                                                            .append(EnumChatFormatting.GRAY)
                                                                                            .append(") detected, disabling AutoGrinder!")
                                                                                    );
                                                                                    MessageUtils.x(str5);
                                                                                    this.process32();
                                                                                    PlaySoundHelper.process12();
                                                                                    KeybindStateUtil.process();
                                                                                    flag18 = true;
                                                                                    break;
                                                                                }
                                                                            }
                                                                        }

                                                                        n7++;
                                                                    }
                                                                }

                                                                if (!flag18) {
                                                                    if (this.allowSwapLobbiesSetting.isEnabled()
                                                                        && !flag15
                                                                        && this.utilHelper83.matchesLong(61000L)
                                                                        && OnClientChatReceivedHandler.computeKillsType() != KillsType.COLLECT_GOLD_INGOTS) {
                                                                        double averageMidPlayerCount = this.getAverageMidPlayerCount();
                                                                        int n9 = GetNonBotPlayersHelper.computeInt();
                                                                        if (this.matchesDouble(averageMidPlayerCount)) {
                                                                            this.handleString6(
                                                                                String.valueOf(
                                                                                    new StringBuilder("Only ")
                                                                                        .append(averageMidPlayerCount)
                                                                                        .append(" non-diamond players in PIT on average, swapping...")
                                                                                )
                                                                            );
                                                                            KeybindStateUtil.process();
                                                                            break label1025;
                                                                        }

                                                                        if (this.matchesInt(n9)) {
                                                                            this.handleString6(
                                                                                String.valueOf(
                                                                                    new StringBuilder("Only ")
                                                                                        .append(n9)
                                                                                        .append(" players in lobby, swapping...")
                                                                                )
                                                                            );
                                                                            KeybindStateUtil.process();
                                                                            break label1025;
                                                                        }
                                                                    }

                                                                    AutoGrinder.IronMode ironMode2 = this.computeIronMode();
                                                                    boolean flag19 = ConnectToServerHandler.checkState6();
                                                                    if (ironMode2 == AutoGrinder.IronMode.MAJOR_EVENTS_ONLY && !flag19) {
                                                                        KeybindStateUtil.process();
                                                                        this.utilHelper89.reset();
                                                                        if (this.disconnectWhileWaitingSetting.isEnabled()) {
                                                                            this.cancelHandler
                                                                                .handleAutoGrinder(
                                                                                    this, (long)(this.joinBeforeMinutesSetting.computeDouble() * 60000.0)
                                                                                );
                                                                        }
                                                                    } else {
                                                                        this.utilHelper839.reset();
                                                                        if (this.utilHelper89.matchesLong((long)(this.computeInt() * 3.5F))) {
                                                                            this.handleString6("Stuck in spawn! Swapping...");
                                                                            KeybindStateUtil.process();
                                                                        } else {
                                                                            org.brutality.automation.grinding.movement.ClearPathHandler.handleAutoGrinder(
                                                                                this, this.checkState18()
                                                                            );
                                                                        }
                                                                    }
                                                                    break label1025;
                                                                }
                                                            }

                                                            KeybindStateUtil.process();
                                                            this.utilHelper89.reset();
                                                        }
                                                    }
                                                }
                                            } else if (this.otherType == OtherType.SPAWN_SPIRE) {
                                                if (this.utilHelper86.matchesLong(250L)) {
                                                    this.utilHelper86.reset();
                                                    if (GetMysticEnchantLoreHelper.checkState5()) {
                                                        mc.thePlayer.closeScreen();
                                                    }
                                                }

                                                org.brutality.automation.grinding.movement.ClearPathHandler.handleAutoGrinder(this, this.checkState18());
                                            } else if (this.otherType == OtherType.SPAWN_BLOCKHEAD) {
                                                if (this.utilHelper86.matchesLong(250L)) {
                                                    this.utilHelper86.reset();
                                                    if (GetMysticEnchantLoreHelper.checkState5()) {
                                                        mc.thePlayer.closeScreen();
                                                    }
                                                }

                                                org.brutality.automation.grinding.movement.ClearPathHandler.handleAutoGrinder(this, this.checkState18());
                                            } else if (this.otherType == OtherType.SPAWN_RAFFLE) {
                                                if (this.utilHelper86.matchesLong(250L)) {
                                                    this.utilHelper86.reset();
                                                    if (GetMysticEnchantLoreHelper.checkState5()) {
                                                        mc.thePlayer.closeScreen();
                                                    }
                                                }

                                                this.player = null;
                                                this.process22();
                                                org.brutality.automation.grinding.movement.ClearPathHandler.handleFlag(this.checkState18());
                                            } else if (this.otherType == OtherType.SPAWN_SQUADS) {
                                                if (this.utilHelper86.matchesLong(250L)) {
                                                    this.utilHelper86.reset();
                                                    if (GetMysticEnchantLoreHelper.checkState5()) {
                                                        mc.thePlayer.closeScreen();
                                                    }
                                                }

                                                KeybindStateUtil.process();
                                            } else if (GetMysticEnchantLoreHelper.checkState5()) {
                                                if (!GetMysticEnchantLoreHelper.matchesGuiScreen(this.previousScreen)) {
                                                    KeybindStateUtil.process();
                                                }

                                                this.utilHelper839.reset();
                                                this.utilHelper89.reset();
                                                this.utilHelper820.reset();
                                            } else {
                                                switch (this.otherType) {
                                                    case DOWN_MID:
                                                    case DOWN_OUTSIDE:
                                                        if (OnClientChatReceivedHandler.computeKillsType() == KillsType.COLLECT_GOLD_INGOTS) {
                                                            if (this.vec33 != null) {
                                                                BuildTargetComparatorHelper.handleDouble(
                                                                    this.vec33.xCoord,
                                                                    this.vec33.yCoord + 0.1,
                                                                    this.vec33.zCoord,
                                                                    (int)this.yawSpeedSetting.computeDouble(),
                                                                    (int)this.pitchSpeedSetting.computeDouble()
                                                                );
                                                                process46();
                                                            } else {
                                                                KeybindStateUtil.process();
                                                            }
                                                        } else if (this.vec32 != null
                                                            && this.pickUpMysticDropsSetting.isEnabled()
                                                            && this.vec32.distanceTo(mc.thePlayer.getPositionVector())
                                                                <= this.mysticPickupDistanceSetting.computeDouble()) {
                                                            BuildTargetComparatorHelper.handleDouble(
                                                                this.vec32.xCoord,
                                                                this.vec32.yCoord + 0.5,
                                                                this.vec32.zCoord,
                                                                (int)this.yawSpeedSetting.computeDouble(),
                                                                (int)this.pitchSpeedSetting.computeDouble()
                                                            );
                                                            process46();
                                                        } else {
                                                            this.checkState22();
                                                            this.process25();
                                                        }
                                                        break;
                                                    case DOWN_SPIRE:
                                                        this.process();
                                                        this.checkState22();
                                                        this.onSpireFloorChangeHandler.process();
                                                        break;
                                                    case DOWN_RAFFLE:
                                                        this.player = null;
                                                        this.process22();
                                                        this.runRaffleAutomationTick();
                                                        break;
                                                    case DOWN_BLOCKHEAD:
                                                        this.process();
                                                        this.checkState22();
                                                        TargetingHandler.handleAutoGrinder(this);
                                                        break;
                                                    case DOWN_SQUADS:
                                                        KeybindStateUtil.process();
                                                }
                                            }
                                        } else {
                                            assert this.count6 >= 0 && this.count6 < 9;

                                            KeybindStateUtil.process();
                                            if (this.utilHelper833.matchesLong(350L)) {
                                                boolean flag20 = false;
                                                int n10 = 0;

                                                for (int i = 0; i < 9; i++) {
                                                    ItemStack stack2;
                                                    if ((stack2 = mc.thePlayer.inventory.getStackInSlot(i)) != null && matchesItemStack(stack2)) {
                                                        if (stack2.getItem() == Items.golden_sword) {
                                                            if (flag20) {
                                                                n10++;
                                                            } else {
                                                                flag20 = true;
                                                            }
                                                        } else {
                                                            n10++;
                                                        }
                                                    }
                                                }

                                                if (n10 < 2 && this.count6 == 0) {
                                                    this.count6 = 9;
                                                    if (mc.currentScreen != null) {
                                                        mc.thePlayer.closeScreen();
                                                    }
                                                } else if (!(mc.currentScreen instanceof GuiInventory)) {
                                                    mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
                                                    this.utilHelper833.reset();
                                                } else {
                                                    ItemStack stack3;
                                                    if (matchesItemStack(stack3 = mc.thePlayer.inventory.getStackInSlot(this.count6))) {
                                                        this.utilHelper833.reset();
                                                        if (this.flag10 || stack3.getItem() != Items.golden_sword) {
                                                            int n11 = this.count6;

                                                            assert n11 >= 0 && n11 < 9;

                                                            HelmetHelper.handleInt(n11 + 36);
                                                        }

                                                        if (stack3.getItem() == Items.golden_sword) {
                                                            this.flag10 = true;
                                                        }
                                                    }

                                                    this.count6++;
                                                    if (this.count6 == 9) {
                                                        if (mc.thePlayer != null) {
                                                            mc.thePlayer.closeScreen();
                                                        }

                                                        this.handleString5("Done moving fresh mystics from hotbar.");
                                                        this.utilHelper845.reset();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (this.entityPlayer != null && this.player == null) {
                        this.utilHelper814.reset();
                        this.process22();
                        this.handleString4("onLostTarget triggered");
                    }

                    if (this.entityPlayer == null && this.player != null) {
                        boolean flag21 = this.checkState5();
                        if (!ObsidianPlacerHandler.flag2 && flag21) {
                            this.process41();
                        }

                        this.handleString4(String.valueOf(new StringBuilder("onFoundTarget triggered, isTargetInSwingRange: ").append(flag21)));
                    }

                    boolean flag22 = this.checkState5();
                    if (this.player != null) {
                        if (!this.dy && flag22) {
                            if (!this.checkState9()) {
                                this.process41();
                            }
                        } else if (this.dy && !flag22) {
                            this.process22();
                        }
                    }

                    this.dy = flag22;
                    this.otherType2 = this.otherType;
                    this.previousScreen = mc.currentScreen;
                    this.entityPlayer = this.player;
                }
            }
        }
    }

    private boolean matchesDouble(double d) {
        if (this.checkState20()) {
            return false;
        }
        if (d >= this.minPlayersSetting.computeDouble()) {
            return false;
        }
        return !this.checkState17() ? this.allowSwapLobbiesSetting.isEnabled() : false;
    }

    public void process38() {
        this.cancelHandler2.process2();
    }

    public static boolean checkState28() {
        return instance != null && instance.checkState44();
    }

    private void process35() {
        if (this.utilHelper822.matchesLong(60000L)) {
            this.handleString5(
                String.valueOf(
                    new StringBuilder("Current contract ")
                        .append(OnClientChatReceivedHandler.computeContract() != null ? OnClientChatReceivedHandler.computeContract().getText() : "?")
                        .append(" is not doable by AutoGrinder OR doesn't have vile, waiting until it expires.")
                )
            );
            this.handleString5(
                String.valueOf(
                    new StringBuilder("If you want to continue dropping instead of waiting, change the ")
                        .append(this.onalwaysdropSetting.getName())
                        .append(" setting to ")
                        .append(EnumChatFormatting.LIGHT_PURPLE)
                        .append(AutoGrinder.OffMode.ON_ALWAYS_DROP.name())
                        .append(EnumChatFormatting.DARK_GRAY)
                        .append(".")
                )
            );
            this.utilHelper822.reset();
        }
    }

    public static boolean checkState23() {
        Module module = Brutality.moduleManager == null ? null : Brutality.moduleManager.computeModule("AutoGrinder");
        return module != null && module.checkState44();
    }

    private void process33() {
        if (mc != null
            && this.checkState44()
            && this.enablePerformanceSettingsSetting.isEnabled()
            && this.skipRenderWorldSetting.isEnabled()
            && mc.thePlayer != null
            && mc.theWorld != null) {
            mc.skipRenderWorld = !(mc.currentScreen instanceof GuiChat) && !GetMysticEnchantLoreHelper.checkState2();
        } else {
            mc.skipRenderWorld = false;
        }
    }

    public boolean checkState16() {
        this.cancelHandler2.tick();
        return this.cancelHandler2.isFlag();
    }

    private void handleLong(long l) {
        if (this.entityItem == null) {
            this.vec32 = null;
        } else if (this.pickUpMysticDropsSetting.isEnabled() && !GetMysticEnchantLoreHelper.checkState3() && this.matchesEntityItem(this.entityItem, l)) {
            double dist = this.entityItem.getDistanceToEntity(mc.thePlayer);
            if (dist < this.amount - 0.2) {
                this.amount = dist;
                this.timestamp3 = l;
            }

            if (l - this.timestamp2 <= 6000L && l - this.timestamp3 <= 2000L && !(dist > this.mysticPickupDistanceSetting.computeDouble())) {
                this.vec32 = this.entityItem.getPositionVector();
            } else {
                this.itemSet.add(this.entityItem.getEntityId());
                this.process44();
            }
        } else {
            this.process44();
        }
    }

    public static boolean matchesModule(Module module) {
        return instance != null && instance.checkState44() && instance.module == module && instance.flag18;
    }

    private boolean checkState13() {
        return this.computeIronMode().obsidianPathingAllowed;
    }

    private void process25() {
        boolean flag = OnClientChatReceivedHandler.computeKillsType() == KillsType.KILLS_SNEAKING;
        if (KillAura.instance != null) {
            BooleanSetting onlyWhileSneakingSetting = KillAura.instance.onlyWhileSneakingSetting;
            if (onlyWhileSneakingSetting != null && onlyWhileSneakingSetting.isEnabled() != flag) {
                onlyWhileSneakingSetting.setEnabled(flag);
            }
        }

        this.process20();
        this.process();
        var module = this.killAuraSetting.matchesString2("KillAura") ? KillAura.instance : AutoClicker.instance;
        if (this.module != null && this.module != module) {
            this.process17();
        }

        if (this.checkState5()
            && !ObsidianPlacerHandler.flag2
            && !this.checkState9()
            && !this.checkState34()
            && !CoreHandler2.checkState()
            && !GetMysticEnchantLoreHelper.checkState5()) {
            this.process41();
        }

        this.process15();
        if (this.utilHelper818.matchesLong(250L)) {
            this.utilHelper818.reset();
            boolean flag2 = false;

            for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                if (playerEntity != null
                    && playerEntity != mc.thePlayer
                    && (!this.ignoreFriendsSetting.isEnabled() || !DefaultHelper.matchesString(playerEntity.getName()))
                    && GetMysticEnchantLoreHelper.matchesEntityPlayer9(playerEntity)
                    && playerEntity.getDistanceSqToEntity(mc.thePlayer) <= 36.0) {
                    flag2 = true;
                    break;
                }
            }

            this.flag9 = flag2;
        }

        if (mc.thePlayer.getHealth() < 9.0F && this.soundAlertsSetting.isEnabled() && this.utilHelper827.matchesLong(1000L)) {
            this.utilHelper827.reset();
            PlaySoundHelper.playSound("random.orb", 2.0F, 0.5F);
            PlaySoundHelper.playSound("random.orb", 2.0F, 0.3F);
        }

        boolean flag3 = ConnectToServerHandler.checkState6();
        boolean flag4 = this.matchesPizzaType(ConnectToServerHandler.getPizzaType());
        if (this.checkState11() && flag3 && !flag4) {
            ObsidianPlacerHandler.matchesFlag(this.soundAlertsSetting.isEnabled(), "Oofing due to major event active.");
            KeybindStateUtil.process();
        } else {
            if (!this.checkState24()
                && this.utilHelper83.matchesLong(61000L)
                && OnClientChatReceivedHandler.computeKillsType() != KillsType.COLLECT_GOLD_INGOTS
                && ConnectToServerHandler.getPizzaType() != PizzaType.SPIRE) {
                boolean flag5 = this.utilHelper820.matchesLong(4000L);
                double averageMidPlayerCount = this.getAverageMidPlayerCount();
                if (this.checkState11()
                    && !this.checkState17()
                    && !this.checkState20()
                    && averageMidPlayerCount < this.minPlayersSetting.computeDouble()
                    && averageMidPlayerCount <= 2.0
                    && (this.player == null || flag5)) {
                    KeybindStateUtil.process();
                    ObsidianPlacerHandler.matchesFlag(this.soundAlertsSetting.isEnabled(), "Oofing due to low players in PIT on average.");
                }
            }

            if (this.oofOnNoHitSetting.isEnabled()
                && this.checkState11()
                && !this.checkState20()
                && this.utilHelper820.matchesLong((long)this.oofNoHitSecondsSetting.computeDouble() * 1000L)) {
                KeybindStateUtil.process();
                ObsidianPlacerHandler.matchesFlag(
                    this.soundAlertsSetting.isEnabled(),
                    "Oofing due to not hitting anyone for " + (int)this.oofNoHitSecondsSetting.computeDouble() + " seconds."
                );
            }
            if (switch (AutoGrinder.OffMode2.valueOf(this.onlynearbhunterSetting.getText())) {
                case OFF -> false;
                case ON -> true;
                case ONLY_NEAR_BHUNTER -> this.flag9;
                default -> false;
            } && !this.checkState12() && !ConnectToServerHandler.checkState6() && this.utilHelper837.matchesLong(7500L)) {
                this.utilHelper837.reset();
                int n = BEASTHelper.computeInt2(mc.thePlayer);
                if (n >= 5000 && !checkState32() && !OnClientTickHelper.checkState()) {
                    for (int i = 0; i < 36; i++) {
                        ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                        if (hasSelfCheckoutEnchant(stack)) {
                            triggerSelfCheckoutSwap(stack);
                            break;
                        }
                    }
                }
            }

            AutoGrinder.IronMode ironMode = this.computeIronMode();
            ObsidianPlacerHandler.handleAutoGrinder(
                this,
                this.player,
                (int)this.yawSpeedSetting.computeDouble(),
                (int)this.pitchSpeedSetting.computeDouble(),
                this.computeDouble2(),
                this.ignoreDiamondsSetting.isEnabled(),
                this.checkState11(),
                this.checkState35(),
                this.utilHelper89.matchesLong((long)(this.computeInt() * 1.5F)),
                this.soundAlertsSetting.isEnabled(),
                this.oofOnNoHitSetting.isEnabled()
                    && !this.checkState20()
                    && this.utilHelper839.matchesLong((long)this.oofNoHitSecondsSetting.computeDouble() * 1000L),
                this.safeJumpSetting.isEnabled(),
                this.checkState13(),
                false,
                !this.utilHelper820.matchesLong(ironMode.stuckTimeoutMillis)
            );
        }
    }

    public void onBanDuration(PitEvent pitEvent) {
        this.handleString(String.valueOf(new StringBuilder("You are ").append(pitEvent.getDescription())), Color.RED);
    }

    private boolean checkState9() {
        return !this.utilHelper815.matchesLong(this.computeInt2());
    }

    private void handleString(String str, boolean flag) {
        boolean flag2 = ExecuteCommandHelper.matchesString2(str) && (flag ? str.contains("XP") : str.contains("on "));
        boolean flag3 = ExecuteCommandHelper.computeInt2(str) != 0;
        if (flag2 || flag3) {
            if (flag2) {
                this.count15++;
            }

            if (flag3) {
                this.count16++;
            }

            double d = ExecuteCommandHelper.computeDouble5(str);
            double d2 = ExecuteCommandHelper.computeDouble4(str);
            if (d > 0.0) {
                this.factor2 += (float)d;
            }

            if (d2 > 0.0) {
                this.factor3 += (float)d2;
            }
        }
    }

    public void onPitStateUpdate(PitEvent11 pitEvent11) {
        this.process6();
        this.queue.clear();
        this.midMode = MovementHandler.MidMode.MID;
        this.utilHelper841.clear();
        this.utilHelper842.clear();
    }

    private void process23() {
        if (!this.flag13) {
            this.flag13 = true;
            this.flag14 = false;
            this.count17 = 0;
            this.count18 = 0;
            this.count19 = 0;
            this.count20 = this.computeInt3();
            this.clearPathHandler2.clearPath();
        }
    }

    private void process17() {
        Module module = this.module;
        this.module = null;
        if (module instanceof KillAura) {
            KillAura.y = false;
        }

        if (module instanceof AutoClicker) {
            AutoClicker.y = false;
        }

        if (module instanceof KillAura && !this.checkState44()) {
            KillAura.entityLivingBase3 = null;
            ((KillAura)module).process38();
        } else {
            if (module != null && module.checkState44()) {
                module.handleFlag3(false);
            }
        }
    }

    public void onVileTradeCompleted(PitEvent17 pitEvent17) {
        this.handleString("Completed night quest!", Color.BLUE);
    }

    public static boolean matchesEntityPlayer3(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        BlockPos pos = BEASTHelper.computeBlockPos(player);
        if (matchesBlockPos(pos.down())) {
            return true;
        }
        return !matchesBlockPos(pos.down(2)) ? matchesBlockPos(pos.down(3)) : true;
    }

    public boolean checkState5() {
        if (mc.thePlayer != null
            && mc.theWorld != null
            && this.player != null
            && !mc.thePlayer.isDead
            && !(mc.thePlayer.getHealth() <= 0.0F)
            && !TDM.matchesEntityPlayer3(this.player)
            && !this.player.isDead
            && !(this.player.getHealth() <= 0.0F)
            && mc.theWorld.playerEntities.contains(this.player)) {
            double d = this.killAuraSetting.matchesString2("AutoClicker") ? Math.min(4.0, this.attackRangeSetting.computeDouble()) : 4.0;
            return mc.thePlayer.getDistanceToEntity(this.player) <= d;
        }
        return false;
    }

    private void process13() {
        if (mc.getNetHandler() != null) {
            var playerInfoMap = mc.getNetHandler().getPlayerInfoMap();
            if (playerInfoMap != null) {
                if (!this.utilHelper848.matchesLong(1500L)) {
                    for (String str : this.entries3) {
                        boolean flag = false;

                        for (NetworkPlayerInfo playerInfo : playerInfoMap) {
                            if (playerInfo != null && playerInfo.getGameProfile() != null && str.equals(playerInfo.getGameProfile().getName())) {
                                flag = true;
                                break;
                            }
                        }

                        if (!flag) {
                            this.process11();
                            break;
                        }
                    }
                }

                this.entries3.clear();

                for (NetworkPlayerInfo playerInfo2 : playerInfoMap) {
                    if (playerInfo2 != null && playerInfo2.getGameProfile() != null && playerInfo2.getGameProfile().getName() != null) {
                        this.entries3.add(playerInfo2.getGameProfile().getName());
                    }
                }
            }
        }
    }

    public boolean checkState14() {
        return this.checkState44();
    }

    @Override
    protected boolean checkState25() {
        return true;
    }

    private boolean matchesContract(Contract contract) {
        if (contract == null) {
            return false;
        }
        KillsType killsType = contract.getKillsType();
        if (killsType == KillsType.KILLS && this.doKillContractsSetting.isEnabled()) {
            return true;
        }
        if (killsType == KillsType.CHAIN_KILLS && this.doChainKillContractsSetting.isEnabled()) {
            return true;
        }
        if (killsType == KillsType.KILL_STREAK && this.doKillStreakContractsSetting.isEnabled()) {
            return true;
        }
        if (killsType == KillsType.KILLS_SNEAKING && this.doSneakKillsContractsSetting.isEnabled()) {
            return true;
        }
        if (killsType == KillsType.KILLS_NAKED && this.doNakedKillsContractsSetting.isEnabled()) {
            return true;
        }
        if (killsType == KillsType.KILLS_DIAMOND_SWORD && this.doDiamondSwordKillsContractsSetting.isEnabled()) {
            return true;
        }
        if (killsType == KillsType.CLAIM_BOUNTIES) {
            return this.doClaimBountiesContractsSetting.isEnabled();
        }
        return killsType == KillsType.COLLECT_GOLD_INGOTS ? this.doGoldContractsSetting.isEnabled() : false;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }

    public static boolean checkState2() {
        return checkState6()
            && !instance.flag18
            && instance.module == AutoClicker.instance
            && instance.checkState5()
            && !ObsidianPlacerHandler.flag2
            && !instance.checkState9()
            && !instance.checkState38()
            && !instance.utilHelper.checkState()
            && !instance.checkState34()
            && !CoreHandler2.checkState();
    }

    public double computeDouble() {
        return this.strafeChancePerTickSetting.computeDouble() / 100.0;
    }

    public void onPitLevelUp(PitEvent10 pitEvent10) {
        this.handleString(String.valueOf(new StringBuilder("Leveled up [").append(pitEvent10.count2).append("] -> [").append(pitEvent10.count).append("]!")), Color.CYAN);
    }

    public void process7() {
        this.utilHelper89.reset();
    }

    public boolean checkState39() {
        return this.checkState44()
            && this.disconnectWhileWaitingSetting.isEnabled()
            && this.majoreventsonlySetting.matchesString2("MAJOR_EVENTS_ONLY")
            && !ConnectToServerHandler.checkState6();
    }

    private boolean checkState34() {
        AutoHeal instance = AutoHeal.getInstance();
        if (instance != null && instance.checkState44() && instance.isCount()) {
            return true;
        }
        AutoFeast instance2 = AutoFeast.getInstance();
        if (instance2 != null && instance2.checkState44() && instance2.isCount()) {
            return true;
        }
        AutoAura instance3 = AutoAura.getInstance();
        if (instance3 != null && instance3.checkState44() && instance3.isCount()) {
            return true;
        }
        AutoSpire instance4 = AutoSpire.instance;
        if (instance4 != null && instance4.checkState44() && instance4.isCount()) {
            return true;
        }
        AutoPotato instance5 = AutoPotato.instance;
        if (instance5 != null && instance5.checkState44() && instance5.isCount()) {
            return true;
        }
        AutoEgg instance6 = AutoEgg.instance;
        return instance6 != null && instance6.checkState44() && instance6.isCount();
    }

    public boolean checkState31() {
        if (!this.cancelSoundSetting.isEnabled()) {
            return false;
        }
        return this.enablePerformanceSettingsSetting.isEnabled() ? this.checkState44() : false;
    }

    private void process3() {
        if (this.flag12) {
            if (this.text3 != null) {
                this.goldenheadsSetting.handleString(this.text3);
            }

            if (this.text4 != null) {
                this.strengthchainingSetting2.handleString(this.text4);
            }

            if (this.text5 != null) {
                this.safetyfirstSetting.handleString(this.text5);
            }

            if (this.text6 != null) {
                this.gladiatorSetting.handleString(this.text6);
            }

            this.flag12 = false;
        }
    }

    public void onAttackEntity(AttackEvent attackEvent) {
        if (attackEvent.getEntityPlayer2() instanceof EntityPlayer) {
            this.utilHelper820.reset();
            if (FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                this.process22();
            }
        }
    }

    public void process45() {
        this.cancelHandler2.cancel();
    }

    private void process42() {
        this.count = this.count8;
    }

    public void handleStringArray(String[] parts) {
        if (parts != null && parts.length >= 4) {
            this.goldenheadsSetting.handleString(parts[0]);
            this.strengthchainingSetting2.handleString(parts[1]);
            this.safetyfirstSetting.handleString(parts[2]);
            this.gladiatorSetting.handleString(parts[3]);
        }
    }

    public boolean checkState27() {
        return this.checkState11() ? this.oofOnBadPathSetting.isEnabled() : false;
    }

    public void onPlayerRemoved(PitEvent13 pitEvent13) {
    }

    private void process34() {
        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        ScoreObjective objectiveInDisplaySlot = scoreboard.getObjectiveInDisplaySlot(1);
        if (objectiveInDisplaySlot != null) {
            for (Score score : scoreboard.getSortedScores(objectiveInDisplaySlot)) {
                String playerName = score.getPlayerName();
                if (playerName != null && !playerName.startsWith("#")) {
                    this.lifecycleHandler.handleString(ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(playerName), playerName));
                }
            }
        }
    }

    public static void triggerSelfCheckoutSwap(ItemStack stack) {
        String str = HelmetHelper.computeString(mc.thePlayer.inventory.armorInventory[1]);
        int n = HelmetHelper.computeInt(8);
        OnClientTickHelper.handleIterable(
            NoSwapsNeededHelper.computeList(stack, HelmetHelper.HelmetMode.LEGGINGS.getContainerSlot(), 150, n, true, "Sco Swap")
        );
        MessageUtils.x(
            String.valueOf(
                new StringBuilder()
                    .append(EnumChatFormatting.GRAY)
                    .append("Auto ")
                    .append(EnumChatFormatting.YELLOW)
                    .append("self-checkout")
                    .append(EnumChatFormatting.GRAY)
                    .append(" triggered!")
            )
        );
        Brutality.scheduledExecutorService
            .schedule(
                () -> mc.addScheduledTask(
                    () -> {
                        if (!checkState32()) {
                            ItemStack stack2 = mc.thePlayer.inventory.armorInventory[1];
                            if (stack2 == null || stack2.getItem() != Items.iron_leggings) {
                                return;
                            }
                        }

                        OnClientTickHelper.handleIterable(
                            NoSwapsNeededHelper.computeList(str, HelmetHelper.HelmetMode.LEGGINGS.getContainerSlot(), 150, n, true, "Sco Swap Back")
                        );
                    }
                ),
                1250L,
                TimeUnit.MILLISECONDS
            );
    }

    private boolean checkState22() {
        if (GetMysticEnchantLoreHelper.checkState5()) {
            return false;
        }
        if (ObsidianPlacerHandler.flag2) {
            return false;
        }
        if (CoreHandler2.checkState()) {
            return false;
        }
        if (this.checkState34()) {
            return false;
        }
        if (!PlayerHelper12.checkState()) {
            return false;
        }
        if (PlayerHelper12.checkState3()) {
            return false;
        }
        ItemStack currentEquippedItem = mc.thePlayer.getCurrentEquippedItem();
        boolean flag = GetMysticEnchantLoreHelper.matchesEntityPlayer3(mc.thePlayer);
        boolean flag2 = GetDisplayedTitleHelper.checkState();
        int n;
        if (OnClientChatReceivedHandler.computeKillsType() != KillsType.KILLS_DIAMOND_SWORD) {
            n = ExtraAttributesHandler.computeInt(this.player, flag, flag2);
        } else {
            int n2 = 0;

            while (true) {
                if (n2 >= 9) {
                    n = ExtraAttributesHandler.computeInt(this.player, flag, flag2);
                    break;
                }

                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(n2);
                if (stack != null && stack.getItem() == Items.diamond_sword) {
                    n = n2;
                    break;
                }

                n2++;
            }
        }

        if (currentEquippedItem != null && mc.thePlayer.inventory.currentItem == n) {
            return false;
        }
        mc.thePlayer.inventory.currentItem = n;
        mc.playerController.syncCurrentPlayItem();
        MessageUtils.x(
            String.valueOf(
                new StringBuilder()
                    .append(EnumChatFormatting.RED)
                    .append(EnumChatFormatting.BOLD)
                    .append("SWAP!")
                    .append(EnumChatFormatting.GRAY)
                    .append(" Swapped to best hotbar weapon: ")
                    .append(EnumChatFormatting.YELLOW)
                    .append(mc.thePlayer.getCurrentEquippedItem() != null ? mc.thePlayer.getCurrentEquippedItem().getDisplayName() : "None")
            )
        );
        if (this.checkState13()) {
            process5();
        }

        if (this.checkState9()) {
            return true;
        }
        this.process41();
        return true;
    }

    private boolean checkState18() {
        return !this.utilHelper845.matchesLong(400 + this.count7);
    }

    private void process32() {
        this.handleFlag3(false);
    }

    private boolean matchesEntityItem(EntityItem entity, long l) {
        if (entity != null
            && !entity.isDead
            && mc.theWorld != null
            && mc.thePlayer != null
            && mc.theWorld.getEntityByID(entity.getEntityId()) == entity
            && !this.itemSet.contains(entity.getEntityId())
            && this.timestamp != 0L
            && l - this.timestamp <= 30000L) {
            ItemStack entityItem = entity.getEntityItem();
            if (entityItem != null && entityItem.stackSize > 0) {
                String owner = entity.getOwner();
                String thrower = entity.getThrower();
                if (owner != null && !owner.isEmpty() && !owner.equalsIgnoreCase(mc.thePlayer.getName())) {
                    return false;
                }
                if (thrower != null && !thrower.isEmpty() && !thrower.equalsIgnoreCase(mc.thePlayer.getName())) {
                    return false;
                }
                String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(entityItem.getDisplayName());
                return textWithoutFormattingCodes != null && (textWithoutFormattingCodes.contains("Fresh Pants") || textWithoutFormattingCodes.contains("Mystic Sword") || textWithoutFormattingCodes.contains("Mystic Bow"));
            }
            return false;
        }
        return false;
    }

    public static void process27() {
        if (utilHelper817.matchesLong(200L)) {
            KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
            utilHelper817.reset();
        }
    }

    public static enum ContinueMode {
        CONTINUE,
        DISABLE,
        DISABLE_AND_DISCONNECT;

        private static AutoGrinder.ContinueMode[] computeContinueModeArray() {
            return new AutoGrinder.ContinueMode[]{CONTINUE, DISABLE, DISABLE_AND_DISCONNECT};
        }
    }

    public static enum ContinueMode2 {
        CONTINUE,
        PRESTIGE,
        DISABLE,
        DISABLE_AND_DISCONNECT;

        private static AutoGrinder.ContinueMode2[] computeContinueMode2Array() {
            return new AutoGrinder.ContinueMode2[]{CONTINUE, PRESTIGE, DISABLE, DISABLE_AND_DISCONNECT};
        }
    }

    public static enum DoNothingMode {
        DO_NOTHING,
        SWAP_LOBBY,
        OOF_AND_AFK,
        HUB_AND_AFK,
        HUB_AND_DISABLE,
        DISABLE_AND_DISCONNECT;

        private static AutoGrinder.DoNothingMode[] computeDoNothingModeArray() {
            return new AutoGrinder.DoNothingMode[]{DO_NOTHING, SWAP_LOBBY, OOF_AND_AFK, HUB_AND_AFK, HUB_AND_DISABLE, DISABLE_AND_DISCONNECT};
        }
    }

    public class Entry extends HashMap<PizzaType, BooleanSetting> {
        public Entry() {
            this.put(PizzaType.RAGE_PIT, AutoGrinder.this.doRagePitEventSetting);
            this.put(PizzaType.TDM, AutoGrinder.this.doTDMEventSetting);
            this.put(PizzaType.BEAST, AutoGrinder.this.doBeastEventSetting);
            this.put(PizzaType.ROBBERY, AutoGrinder.this.doRobberyEventSetting);
            this.put(PizzaType.SPIRE, AutoGrinder.this.doSpireEventSetting);
            this.put(PizzaType.RAFFLE, AutoGrinder.this.doRaffleEventSetting);
            this.put(PizzaType.BLOCKHEAD, AutoGrinder.this.doBlockheadEventSetting);
            this.put(PizzaType.SQUADS, AutoGrinder.this.doSquadsEventSetting);
        }
    }

    public static enum IronMode {
        IRON(1000, 1250, true, false, false),
        DIAMOND(1250, 2000, true, true, true),
        MYSTICS(1250, 2000, false, true, true),
        MAJOR_EVENTS_ONLY(1000, 1250, true, true, false);

        private final int noTargetTimeoutMillis;
        public final boolean emergencyOofAllowed;
        public final int stuckTimeoutMillis;
        public final boolean pauseAfterSpawn;
        public final boolean obsidianPathingAllowed;

        private IronMode(int noTargetTimeoutMillis, int stuckTimeoutMillis, boolean emergencyOofAllowed, boolean pauseAfterSpawn, boolean obsidianPathingAllowed) {
            this.noTargetTimeoutMillis = noTargetTimeoutMillis;
            this.stuckTimeoutMillis = stuckTimeoutMillis;
            this.emergencyOofAllowed = emergencyOofAllowed;
            this.pauseAfterSpawn = pauseAfterSpawn;
            this.obsidianPathingAllowed = obsidianPathingAllowed;
        }

        private static AutoGrinder.IronMode[] computeIronModeArray() {
            return new AutoGrinder.IronMode[]{IRON, DIAMOND, MYSTICS, MAJOR_EVENTS_ONLY};
        }
    }

    public static enum OffMode {
        OFF,
        ON_ONLY_DROP_FOR_DOABLE_CONTRACTS,
        ON_ALWAYS_DROP;

        private static AutoGrinder.OffMode[] computeOffModeArray() {
            return new AutoGrinder.OffMode[]{OFF, ON_ONLY_DROP_FOR_DOABLE_CONTRACTS, ON_ALWAYS_DROP};
        }
    }

    public static enum OffMode2 {
        OFF,
        ON,
        ONLY_NEAR_BHUNTER;

        private static AutoGrinder.OffMode2[] computeOffMode2Array() {
            return new AutoGrinder.OffMode2[]{OFF, ON, ONLY_NEAR_BHUNTER};
        }
    }

    public static enum YawMode {
        YAW,
        HEALTH,
        HEALTH_ONLY_HITTABLE,
        HIGHEST_HEALTH,
        HURT_TIME,
        HUNT_ENEMIES;

        private static AutoGrinder.YawMode[] computeYawModeArray() {
            return new AutoGrinder.YawMode[]{YAW, HEALTH, HEALTH_ONLY_HITTABLE, HIGHEST_HEALTH, HURT_TIME, HUNT_ENEMIES};
        }
    }
}
