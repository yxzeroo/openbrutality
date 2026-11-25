package org.brutality.module;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import org.brutality.Brutality;
import org.brutality.clickgui.newclick.HypixelPitType;
import org.brutality.extensions.APPDATAHandler;
import org.brutality.mellow.Mellow;
import org.brutality.mint.ResourceTracker;
import org.brutality.module.impl.client.AutoDisable;
import org.brutality.module.impl.client.Cape;
import org.brutality.module.impl.client.ClickGUI;
import org.brutality.module.impl.client.ClientSettings;
import org.brutality.module.impl.client.DiscordRPC;
// VENDOR-DISABLED: import org.brutality.module.impl.client.IRC;
import org.brutality.module.impl.client.LagbackDebug;
import org.brutality.module.impl.client.PotionEffectDebug;
// VENDOR-DISABLED: import org.brutality.module.impl.client.Stafftracker;
import org.brutality.module.impl.combat.AimAssist;
import org.brutality.module.impl.combat.AntiBot;
import org.brutality.module.impl.combat.AntiFireball;
import org.brutality.module.impl.combat.AutoClicker;
import org.brutality.module.impl.combat.AutoRod;
import org.brutality.module.impl.combat.BackTrack;
import org.brutality.module.impl.combat.BackwardsKnockback;
import org.brutality.module.impl.combat.CombatModule;
import org.brutality.module.impl.combat.ComboOneHit;
import org.brutality.module.impl.combat.Criticals;
import org.brutality.module.impl.combat.DamageBoost;
import org.brutality.module.impl.combat.Displace;
import org.brutality.module.impl.combat.FastBow;
import org.brutality.module.impl.combat.FightBot;
import org.brutality.module.impl.combat.GunAimbot;
import org.brutality.module.impl.combat.HitSelect;
import org.brutality.module.impl.combat.HitTickStealer;
import org.brutality.module.impl.combat.JumpReset;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.combat.KnockbackDelay;
import org.brutality.module.impl.combat.LagRange;
import org.brutality.module.impl.combat.MoreKB;
import org.brutality.module.impl.combat.Reach;
import org.brutality.module.impl.combat.RightClicker;
import org.brutality.module.impl.combat.Velocity;
import org.brutality.module.impl.exploit.APC;
import org.brutality.module.impl.exploit.AntiAim;
import org.brutality.module.impl.exploit.AntiAura;
import org.brutality.module.impl.exploit.AntiDesync;
import org.brutality.module.impl.exploit.AntiFreeze;
import org.brutality.module.impl.exploit.AntiPotion;
import org.brutality.module.impl.exploit.AntiVanish;
import org.brutality.module.impl.exploit.AnticheatDetector;
import org.brutality.module.impl.exploit.Blink;
import org.brutality.module.impl.exploit.ClientSpoofer;
import org.brutality.module.impl.exploit.Crasher;
import org.brutality.module.impl.exploit.Desync;
import org.brutality.module.impl.exploit.Disabler;
import org.brutality.module.impl.exploit.FakeLag;
import org.brutality.module.impl.exploit.NoRotate;
import org.brutality.module.impl.exploit.OnRenderModule;
import org.brutality.module.impl.exploit.PingSpoof;
import org.brutality.module.impl.exploit.RatProtection;
import org.brutality.module.impl.exploit.Regeneration;
import org.brutality.module.impl.exploit.SelfDestruction;
import org.brutality.module.impl.exploit.SelfPotion;
import org.brutality.module.impl.exploit.TickBase;
import org.brutality.module.impl.exploit.TimerRange;
import org.brutality.module.impl.misc.AntiAFK;
import org.brutality.module.impl.misc.AntiCheat;
import org.brutality.module.impl.misc.AntiTabComplete;
import org.brutality.module.impl.misc.AntiTrap;
import org.brutality.module.impl.misc.AutoLogin;
import org.brutality.module.impl.misc.AutoRespawn;
import org.brutality.module.impl.misc.AutoStrip;
import org.brutality.module.impl.misc.BanTracker;
import org.brutality.module.impl.misc.Begger;
import org.brutality.module.impl.misc.BlinkDetection;
import org.brutality.module.impl.misc.ChatBypass;
import org.brutality.module.impl.misc.ChatColorizer;
import org.brutality.module.impl.misc.ChatMute;
import org.brutality.module.impl.misc.Dummy;
import org.brutality.module.impl.misc.GUIDebugger;
import org.brutality.module.impl.misc.GhostHand;
import org.brutality.module.impl.misc.Insults;
import org.brutality.module.impl.misc.LightningTracker;
import org.brutality.module.impl.misc.MiscModule;
import org.brutality.module.impl.misc.ModuleSound;
import org.brutality.module.impl.misc.MurderMystery;
import org.brutality.module.impl.misc.SkinDenicker;
import org.brutality.module.impl.misc.SoundDebugger;
import org.brutality.module.impl.misc.SoundPlayer;
import org.brutality.module.impl.misc.Spammer;
import org.brutality.module.impl.misc.TPATrap;
import org.brutality.module.impl.misc.TPDeath;
import org.brutality.module.impl.misc.WhoGotDogged;
import org.brutality.module.impl.misc.WorldDownloader;
import org.brutality.module.impl.misc.WorldExporter;
import org.brutality.module.impl.movement.AntiVoid;
import org.brutality.module.impl.movement.AutoHeadHitter;
import org.brutality.module.impl.movement.AutoSneak;
import org.brutality.module.impl.movement.AutoWalk;
import org.brutality.module.impl.movement.BoatFly;
import org.brutality.module.impl.movement.Dolphin;
import org.brutality.module.impl.movement.FastLadder;
import org.brutality.module.impl.movement.Flight;
import org.brutality.module.impl.movement.FreeCam;
import org.brutality.module.impl.movement.InstantStop;
import org.brutality.module.impl.movement.InventoryMove;
import org.brutality.module.impl.movement.Jesus;
import org.brutality.module.impl.movement.JetPack;
import org.brutality.module.impl.movement.KeepSprint;
import org.brutality.module.impl.movement.LegitScaffold;
import org.brutality.module.impl.movement.Longjump;
import org.brutality.module.impl.movement.MovementFix;
import org.brutality.module.impl.movement.NoFall;
import org.brutality.module.impl.movement.NoSlowDown;
import org.brutality.module.impl.movement.Parkour;
import org.brutality.module.impl.movement.Phase;
import org.brutality.module.impl.movement.RightClickBlockPlace;
import org.brutality.module.impl.movement.Safewalk;
import org.brutality.module.impl.movement.Scaffold;
import org.brutality.module.impl.movement.Speed;
import org.brutality.module.impl.movement.Spider;
import org.brutality.module.impl.movement.Sprint;
import org.brutality.module.impl.movement.Stasis;
import org.brutality.module.impl.movement.Step;
import org.brutality.module.impl.movement.TargetStrafe;
import org.brutality.module.impl.movement.Timer;
import org.brutality.module.impl.movement.WTap;
import org.brutality.module.impl.pit.classic.utility.ClassicVanishDebug;
import org.brutality.module.impl.pit.harrys.minorevent.Gamble;
import org.brutality.module.impl.pit.harrys.minorevent.MeteoriteTracker;
import org.brutality.module.impl.pit.hypit.AutoEnchant;
import org.brutality.module.impl.pit.hypixel.automation.AssassinESP;
import org.brutality.module.impl.pit.hypixel.automation.Auction;
import org.brutality.module.impl.pit.hypixel.automation.AuraTimer;
import org.brutality.module.impl.pit.hypixel.automation.AutoAura;
import org.brutality.module.impl.pit.hypixel.automation.AutoBulletTime;
import org.brutality.module.impl.pit.hypixel.automation.AutoCake;
import org.brutality.module.impl.pit.hypixel.automation.AutoDenicker;
import org.brutality.module.impl.pit.hypixel.automation.AutoEgg;
import org.brutality.module.impl.pit.hypixel.automation.AutoFeast;
import org.brutality.module.impl.pit.hypixel.automation.AutoFish;
import org.brutality.module.impl.pit.hypixel.automation.AutoGrinder;
import org.brutality.module.impl.pit.hypixel.automation.AutoHunt;
import org.brutality.module.impl.pit.hypixel.automation.AutoKings;
import org.brutality.module.impl.pit.hypixel.automation.AutoMaths;
import org.brutality.module.impl.pit.hypixel.automation.AutoOof;
import org.brutality.module.impl.pit.hypixel.automation.AutoParty;
import org.brutality.module.impl.pit.hypixel.automation.AutoPit;
import org.brutality.module.impl.pit.hypixel.automation.AutoPitSpawn;
import org.brutality.module.impl.pit.hypixel.automation.AutoPotato;
import org.brutality.module.impl.pit.hypixel.automation.AutoRefill;
import org.brutality.module.impl.pit.hypixel.automation.AutoReport;
import org.brutality.module.impl.pit.hypixel.automation.AutoSpire;
import org.brutality.module.impl.pit.hypixel.automation.AutoTacticalInsertion;
import org.brutality.module.impl.pit.hypixel.automation.AutoTelebow;
import org.brutality.module.impl.pit.hypixel.automation.AutoVolley;
import org.brutality.module.impl.pit.hypixel.automation.BedrockAlerts;
import org.brutality.module.impl.pit.hypixel.automation.ChickenTimer;
import org.brutality.module.impl.pit.hypixel.automation.CombatDetection;
import org.brutality.module.impl.pit.hypixel.automation.EggTimer;
import org.brutality.module.impl.pit.hypixel.automation.EnderChestCooldown;
import org.brutality.module.impl.pit.hypixel.automation.Events;
import org.brutality.module.impl.pit.hypixel.automation.ExploTimer;
import org.brutality.module.impl.pit.hypixel.automation.ExplosionRadius;
import org.brutality.module.impl.pit.hypixel.automation.FakeContractHUD;
import org.brutality.module.impl.pit.hypixel.automation.FeastTimer;
import org.brutality.module.impl.pit.hypixel.automation.FeatherCountHUD;
import org.brutality.module.impl.pit.hypixel.automation.HologramDebugger;
import org.brutality.module.impl.pit.hypixel.automation.JumpPotionHUD;
import org.brutality.module.impl.pit.hypixel.automation.LavaFill;
import org.brutality.module.impl.pit.hypixel.automation.LowLifeWarning;
import org.brutality.module.impl.pit.hypixel.automation.MidCounter;
import org.brutality.module.impl.pit.hypixel.automation.MindAssaultDamage;
import org.brutality.module.impl.pit.hypixel.automation.MirrorDamage;
import org.brutality.module.impl.pit.hypixel.automation.MysticDropCounter;
import org.brutality.module.impl.pit.hypixel.automation.MysticESP;
import org.brutality.module.impl.pit.hypixel.automation.PainDamage;
import org.brutality.module.impl.pit.hypixel.automation.PerunCounter;
import org.brutality.module.impl.pit.hypixel.automation.PheonixHUD;
import org.brutality.module.impl.pit.hypixel.automation.PinTimer;
import org.brutality.module.impl.pit.hypixel.automation.PitChat;
import org.brutality.module.impl.pit.hypixel.automation.PlayerList;
import org.brutality.module.impl.pit.hypixel.hud.Beast;
import org.brutality.module.impl.pit.hypixel.hud.Blockhead;
import org.brutality.module.impl.pit.hypixel.hud.Cake;
import org.brutality.module.impl.pit.hypixel.hud.Pizza;
import org.brutality.module.impl.pit.hypixel.hud.PodESP;
import org.brutality.module.impl.pit.hypixel.hud.PodHUD;
import org.brutality.module.impl.pit.hypixel.hud.PrestigeList;
import org.brutality.module.impl.pit.hypixel.hud.RGMDamage;
import org.brutality.module.impl.pit.hypixel.hud.RegenHUD;
import org.brutality.module.impl.pit.hypixel.hud.ResistanceHUD;
import org.brutality.module.impl.pit.hypixel.hud.RingTemplate;
import org.brutality.module.impl.pit.hypixel.hud.Robbery;
import org.brutality.module.impl.pit.hypixel.hud.SharkDamage;
import org.brutality.module.impl.pit.hypixel.hud.SolitudeReduction;
import org.brutality.module.impl.pit.hypixel.hud.SoulripperDamage;
import org.brutality.module.impl.pit.hypixel.hud.SpawnPoints;
import org.brutality.module.impl.pit.hypixel.hud.SpawnTimer;
import org.brutality.module.impl.pit.hypixel.hud.SpeedHUD;
import org.brutality.module.impl.pit.hypixel.hud.Squads;
import org.brutality.module.impl.pit.hypixel.hud.StreakInfo;
import org.brutality.module.impl.pit.hypixel.hud.StrengthHUD;
import org.brutality.module.impl.pit.hypixel.hud.TDM;
import org.brutality.module.impl.pit.hypixel.hud.TelebowTimer;
import org.brutality.module.impl.pit.hypixel.hud.ThinkHelper;
import org.brutality.module.impl.pit.hypixel.hud.ToxicInfo;
import org.brutality.module.impl.pit.hypixel.hud.VenomIndicator;
import org.brutality.module.impl.pit.hypixel.hud.VenomedTimer;
import org.brutality.module.impl.pit.hypixel.minorevent.CarePackage;
import org.brutality.module.impl.pit.hypixel.minorevent.DragonEgg;
import org.brutality.module.impl.pit.hypixel.swap.AntiVenomSwap;
import org.brutality.module.impl.pit.hypixel.swap.ArmaSwap;
import org.brutality.module.impl.pit.hypixel.swap.AutoLifesteal;
import org.brutality.module.impl.pit.hypixel.swap.DarkSwap;
import org.brutality.module.impl.pit.hypixel.swap.DivineSwap;
import org.brutality.module.impl.pit.hypixel.swap.FrenchSwap;
import org.brutality.module.impl.pit.hypixel.swap.GambleSwapper;
import org.brutality.module.impl.pit.hypixel.swap.InventoryShortcuts;
import org.brutality.module.impl.pit.hypixel.swap.PantsSwapper;
import org.brutality.module.impl.pit.hypixel.swap.PaparazziSwap;
import org.brutality.module.impl.pit.hypixel.swap.PheonixSwap;
import org.brutality.module.impl.pit.hypixel.swap.PitSwap;
import org.brutality.module.impl.pit.hypixel.swap.PodSwap;
import org.brutality.module.impl.pit.hypixel.swap.RightClickSwap;
import org.brutality.module.impl.pit.hypixel.utility.AntiLauncher;
import org.brutality.module.impl.pit.hypixel.utility.AntiVenom;
import org.brutality.module.impl.pit.hypixel.utility.Botting;
import org.brutality.module.impl.pit.hypixel.utility.BowStreak;
import org.brutality.module.impl.pit.hypixel.utility.ChatLog;
import org.brutality.module.impl.pit.hypixel.utility.CombatLogger;
import org.brutality.module.impl.pit.hypixel.utility.DeadLobbyFinder;
import org.brutality.module.impl.pit.hypixel.utility.DivineDetector;
import org.brutality.module.impl.pit.hypixel.utility.JumpBoostDetector;
import org.brutality.module.impl.pit.hypixel.utility.LauncherDebugger;
import org.brutality.module.impl.pit.hypixel.utility.LobbyStalker;
import org.brutality.module.impl.pit.hypixel.utility.LobbySwapper;
import org.brutality.module.impl.pit.hypixel.utility.MapDetection;
import org.brutality.module.impl.pit.hypixel.utility.MiddleClickHistory;
import org.brutality.module.impl.pit.hypixel.utility.PitAlerts;
import org.brutality.module.impl.pit.hypixel.utility.PitDebugger;
import org.brutality.module.impl.pit.hypixel.utility.PitNPC;
import org.brutality.module.impl.pit.hypixel.utility.PitUtils;
import org.brutality.module.impl.pit.hypixel.utility.PodDetection;
import org.brutality.module.impl.pit.hypixel.utility.RingMiner;
import org.brutality.module.impl.pit.hypixel.utility.SafetyMeasures;
import org.brutality.module.impl.pit.hypixel.utility.ScammerAlerts;
import org.brutality.module.impl.pit.hypixel.utility.ScoSwap;
import org.brutality.module.impl.pit.hypixel.utility.SlotSwap;
import org.brutality.module.impl.pit.hypixel.utility.SogeSwap;
import org.brutality.module.impl.pit.hypixel.utility.SoundRemover;
import org.brutality.module.impl.pit.hypixel.utility.SpireExporter;
import org.brutality.module.impl.pit.hypixel.utility.SpireFix;
import org.brutality.module.impl.pit.hypixel.utility.SwapMacros;
import org.brutality.module.impl.pit.hypixel.utility.TelebowESP;
import org.brutality.module.impl.pit.hypixel.utility.TelebowFix;
import org.brutality.module.impl.pit.hypixel.utility.UtilityModule;
import org.brutality.module.impl.pit.hypixel.utility.VenomSwap;
import org.brutality.module.impl.pit.hypixel.utility.VenomedESP;
import org.brutality.module.impl.pit.hypixel.utility.WipeAlert;
import org.brutality.module.impl.pit.orion.hud.RequirementChecker;
import org.brutality.module.impl.pit.orion.utility.AutoCompleteChecker;
import org.brutality.module.impl.pit.orion.utility.OrionAntiVanish;
import org.brutality.module.impl.pit.weasel.automation.WeaselGrinder;
import org.brutality.module.impl.pit.weasel.utility.Macro1;
import org.brutality.module.impl.pit.weasel.utility.XCarry;
import org.brutality.module.impl.player.AutoDrop;
import org.brutality.module.impl.player.AutoGG;
import org.brutality.module.impl.player.AutoHeal;
import org.brutality.module.impl.player.AutoHotbar;
import org.brutality.module.impl.player.AutoHypixel;
import org.brutality.module.impl.player.AutoPolicies;
import org.brutality.module.impl.player.AutoPot;
import org.brutality.module.impl.player.AutoTool;
import org.brutality.module.impl.player.Autoblockin;
import org.brutality.module.impl.player.BedAura;
import org.brutality.module.impl.player.ChestAura;
import org.brutality.module.impl.player.ChestStealer;
import org.brutality.module.impl.player.Clutch;
import org.brutality.module.impl.player.DelayRemovers;
import org.brutality.module.impl.player.FastPlace;
import org.brutality.module.impl.player.FreeLook;
import org.brutality.module.impl.player.Friends;
import org.brutality.module.impl.player.InventoryManager;
import org.brutality.module.impl.player.ItemAlerts;
import org.brutality.module.impl.player.KOS;
import org.brutality.module.impl.player.MiddleClickFriends;
import org.brutality.module.impl.player.NoDropItem;
import org.brutality.module.impl.player.QuickBuy;
import org.brutality.module.impl.player.Replays;
import org.brutality.module.impl.player.SlotLock;
import org.brutality.module.impl.player.SpeedMine;
import org.brutality.module.impl.player.Truces;
import org.brutality.module.impl.visual.Ambience;
import org.brutality.module.impl.visual.Animations;
import org.brutality.module.impl.visual.ArmorHUD;
import org.brutality.module.impl.visual.ArrayList;
import org.brutality.module.impl.visual.AspectRatio;
import org.brutality.module.impl.visual.BPSGraph;
import org.brutality.module.impl.visual.BedESP;
import org.brutality.module.impl.visual.BlockCounter;
import org.brutality.module.impl.visual.BlockCounterHUD;
import org.brutality.module.impl.visual.BlockESP;
import org.brutality.module.impl.visual.BlockOverlay;
import org.brutality.module.impl.visual.Breadcrumbs;
import org.brutality.module.impl.visual.BrightPlayers;
import org.brutality.module.impl.visual.Brightness;
import org.brutality.module.impl.visual.Chams;
import org.brutality.module.impl.visual.Chat;
import org.brutality.module.impl.visual.ChestESP;
import org.brutality.module.impl.visual.ChinaHat;
import org.brutality.module.impl.visual.ClientInfo;
import org.brutality.module.impl.visual.CollisionBox;
import org.brutality.module.impl.visual.ConsumeProgress;
import org.brutality.module.impl.visual.Crosshair;
import org.brutality.module.impl.visual.CustomModel;
import org.brutality.module.impl.visual.DamageParticles;
import org.brutality.module.impl.visual.DamageTags;
import org.brutality.module.impl.visual.DisplaceHUD;
import org.brutality.module.impl.visual.DragonWings;
import org.brutality.module.impl.visual.ESP;
import org.brutality.module.impl.visual.EnchantGlint;
import org.brutality.module.impl.visual.ExposedHitboxESP;
import org.brutality.module.impl.visual.ExtendedCamera;
import org.brutality.module.impl.visual.FOVView;
import org.brutality.module.impl.visual.FPSBooster;
import org.brutality.module.impl.visual.FallView;
import org.brutality.module.impl.visual.Focus;
import org.brutality.module.impl.visual.Glint;
import org.brutality.module.impl.visual.HUD;
import org.brutality.module.impl.visual.HealTracker;
import org.brutality.module.impl.visual.Health;
import org.brutality.module.impl.visual.HiddenTabHUD;
import org.brutality.module.impl.visual.HitBubbles;
import org.brutality.module.impl.visual.HitParticles;
import org.brutality.module.impl.visual.HitVector;
import org.brutality.module.impl.visual.Hotbar;
import org.brutality.module.impl.visual.InterfaceType;
import org.brutality.module.impl.visual.InventoryGUI;
import org.brutality.module.impl.visual.ItemESP;
import org.brutality.module.impl.visual.ItemPhysics;
import org.brutality.module.impl.visual.KeepSprintHUD;
import org.brutality.module.impl.visual.KillEffects;
import org.brutality.module.impl.visual.MellowHUD;
import org.brutality.module.impl.visual.MotionBlur;
import org.brutality.module.impl.visual.NameTags;
import org.brutality.module.impl.visual.NickHider;
import org.brutality.module.impl.visual.NoHurtCam;
import org.brutality.module.impl.visual.Notifications;
import org.brutality.module.impl.visual.PlayerFOV;
import org.brutality.module.impl.visual.PlayerHUD;
import org.brutality.module.impl.visual.PostProcessing;
import org.brutality.module.impl.visual.PotionHUD;
import org.brutality.module.impl.visual.Projectiles;
import org.brutality.module.impl.visual.Scoreboard;
import org.brutality.module.impl.visual.Session;
import org.brutality.module.impl.visual.TNTHelper;
import org.brutality.module.impl.visual.TabGUI;
import org.brutality.module.impl.visual.TargetESP;
import org.brutality.module.impl.visual.TargetHUD;
import org.brutality.module.impl.visual.Tracers;
import org.brutality.module.impl.visual.VelocityHUD;
import org.brutality.module.impl.visual.Watermark;
import org.brutality.module.impl.visual.XRay;
import org.brutality.utils.EditableModulesHelper;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.render.GlStateGuard;

public class ModuleManager {
    private static final List<Module> entries = new CopyOnWriteArrayList<>();
    private static final List<Module> entries2 = Collections.unmodifiableList(entries);
    private static volatile Module[] moduleArray = new Module[0];
    public static final List<Module> entries3 = new CopyOnWriteArrayList<>();
    private static final Map<Integer, List<Module>> lookupMap = new HashMap<>();
    private static final Map<String, Module> lookupMap2 = new HashMap<>();
    private static final Set<String> itemSet = new HashSet<>();
    private static boolean flag = false;

    public ModuleManager() {
        handleModule3(new KillAura());
        handleModule3(new AutoRod());
        handleModule3(new AimAssist());
        handleModule3(new AutoClicker());
        handleModule3(new Reach());
        handleModule3(new Velocity());
        handleModule3(new FastBow());
        handleModule3(new RightClicker());
        handleModule3(new HitSelect());
        handleModule3(new Criticals());
        handleModule3(new KnockbackDelay());
        handleModule3(new BackwardsKnockback());
        handleModule3(new JumpReset());
        handleModule3(new MoreKB());
        handleModule3(new LagRange());
        handleModule3(new BackTrack());
        handleModule3(new AntiBot());
        handleModule3(new AntiFireball());
        handleModule3(new ComboOneHit());
        handleModule3(new DamageBoost());
        handleModule3(new Displace());
        handleModule3(new FightBot());
        handleModule3(new GunAimbot());
        handleModule3(new HitTickStealer());
        handleModule3(new CombatModule());
        handleModule3(new Sprint());
        handleModule3(new MovementFix());
        handleModule3(new KeepSprint());
        handleModule3(new Flight());
        handleModule3(new BoatFly());
        handleModule3(new Speed());
        handleModule3(new Timer());
        handleModule3(new NoSlowDown());
        handleModule3(new NoFall());
        handleModule3(new Safewalk());
        handleModule3(new Stasis());
        handleModule3(new InstantStop());
        handleModule3(new InventoryMove());
        handleModule3(new Jesus());
        handleModule3(new FastLadder());
        handleModule3(new Spider());
        handleModule3(new Step());
        handleModule3(new Dolphin());
        handleModule3(new Longjump());
        handleModule3(new JetPack());
        handleModule3(new AntiVoid());
        handleModule3(new AutoSneak());
        handleModule3(new AutoWalk());
        handleModule3(new AutoHeadHitter());
        handleModule3(new Parkour());
        handleModule3(new Phase());
        handleModule3(new Scaffold());
        handleModule3(new RightClickBlockPlace());
        handleModule3(new LegitScaffold());
        handleModule3(new TargetStrafe());
        handleModule3(new WTap());
        handleModule3(new FreeCam());
        handleModule3(new FreeLook());
        handleModule3(new AutoHeal());
        handleModule3(new AutoPot());
        handleModule3(new AutoHotbar());
        handleModule3(new AutoGG());
        handleModule3(new AutoHypixel());
        handleModule3(new AutoPolicies());
        handleModule3(new AutoDrop());
        handleModule3(new Autoblockin());
        handleModule3(new BedAura());
        handleModule3(new ChestStealer());
        handleModule3(new ChestAura());
        handleModule3(new Clutch());
        handleModule3(new DeadLobbyFinder());
        handleModule3(new DelayRemovers());
        handleModule3(new FastPlace());
        handleModule3(new SpeedMine());
        handleModule3(new Friends());
        handleModule3(new Truces());
        handleModule3(new InventoryManager());
        handleModule3(new ItemAlerts());
        handleModule3(new KOS());
        handleModule3(new LobbyStalker());
        handleModule3(new LobbySwapper());
        handleModule3(new MiddleClickFriends());
        handleModule3(new NoDropItem());
        handleModule3(new QuickBuy());
        handleModule3(new Replays());
        handleModule3(new SlotLock());
        handleModule3(new AutoTool());
        handleModule3(new HUD());
        handleModule3(new ArrayList());
        handleModule3(new Notifications());
        handleModule3(new Hotbar());
        handleModule3(new XRay());
        handleModule3(new BlockCounter());
        handleModule3(new BlockCounterHUD());
        handleModule3(new HealTracker());
        handleModule3(new BlockESP());
        handleModule3(new ESP());
        handleModule3(new BlockOverlay());
        handleModule3(new ChestESP());
        handleModule3(new BedESP());
        handleModule3(new ItemESP());
        handleModule3(new Tracers());
        handleModule3(new Chams());
        handleModule3(new TargetHUD());
        handleModule3(new TargetESP());
        handleModule3(new NameTags());
        handleModule3(new PlayerHUD());
        handleModule3(new ArmorHUD());
        handleModule3(new PotionHUD());
        handleModule3(new Health());
        handleModule3(new VelocityHUD());
        handleModule3(new BPSGraph());
        handleModule3(new PrestigeList());
        handleModule3(new Session());
        handleModule3(new Crosshair());
        handleModule3(new Scoreboard());
        handleModule3(new Chat());
        handleModule3(new CustomModel());
        handleModule3(new Animations());
        handleModule3(new Ambience());
        handleModule3(new Brightness());
        handleModule3(new AspectRatio());
        handleModule3(new BrightPlayers());
        handleModule3(new ChinaHat());
        handleModule3(new DragonWings());
        handleModule3(new KillEffects());
        handleModule3(new EnchantGlint());
        handleModule3(new Glint());
        handleModule3(new ItemPhysics());
        handleModule3(new Projectiles());
        handleModule3(new NoHurtCam());
        handleModule3(new FPSBooster());
        handleModule3(new ExtendedCamera());
        handleModule3(new FOVView());
        handleModule3(new PlayerFOV());
        handleModule3(new FallView());
        handleModule3(new Focus());
        handleModule3(new HitParticles());
        handleModule3(new HitBubbles());
        handleModule3(new HitVector());
        handleModule3(new ExposedHitboxESP());
        handleModule3(new DamageParticles());
        handleModule3(new DamageTags());
        handleModule3(new Breadcrumbs());
        handleModule3(new CollisionBox());
        handleModule3(new ConsumeProgress());
        handleModule3(new TNTHelper());
        handleModule3(new NickHider());
        handleModule3(new PostProcessing());
        handleModule3(new MotionBlur());
        handleModule3(new InterfaceType());
        handleModule3(new TabGUI());
        handleModule3(new Watermark());
        handleModule3(new MellowHUD());
        handleModule3(new ClientInfo());
        handleModule3(new InventoryGUI());
        handleModule3(new HiddenTabHUD());
        handleModule3(new ModuleSound());
        handleModule3(new SoundPlayer());
        handleModule3(new SoundDebugger());
        handleModule3(new GUIDebugger());
        handleModule3(new ChatBypass());
        handleModule3(new ChatColorizer());
        handleModule3(new ChatMute());
        handleModule3(new Spammer());
        handleModule3(new Insults());
        handleModule3(new Begger());
        handleModule3(new AntiCheat());
        handleModule3(new AntiAFK());
        handleModule3(new AntiFreeze());
        handleModule3(new AntiPotion());
        handleModule3(new AntiTrap());
        handleModule3(new AntiVanish());
        handleModule3(new AntiDesync());
        handleModule3(new AntiTabComplete());
        handleModule3(new AutoStrip());
        handleModule3(new AutoLogin());
        handleModule3(new AutoReport());
        handleModule3(new AutoRespawn());
        handleModule3(new BanTracker());
        handleModule3(new BlinkDetection());
        handleModule3(new GhostHand());
        handleModule3(new LightningTracker());
        handleModule3(new MiscModule());
        handleModule3(new MurderMystery());
        handleModule3(new SkinDenicker());
        handleModule3(new TPDeath());
        handleModule3(new WhoGotDogged());
        handleModule3(new WorldExporter());
        handleModule3(new WorldDownloader());
        handleModule3(new Dummy());
        handleModule3(new Blink());
        handleModule3(new FakeLag());
        handleModule3(new TickBase());
        handleModule3(new TimerRange());
        handleModule3(new Disabler());
        handleModule3(new Crasher());
        handleModule3(new RatProtection());
        handleModule3(new XCarry());
        handleModule3(new Macro1());
        handleModule3(new AntiAim());
        handleModule3(new AntiAura());
        handleModule3(new AnticheatDetector());
        handleModule3(new APC());
        handleModule3(new ClientSpoofer());
        handleModule3(new Desync());
        handleModule3(new NoRotate());
        handleModule3(new PingSpoof());
        handleModule3(new Regeneration());
        handleModule3(new OnRenderModule());
        handleModule3(new SelfPotion());
        handleModule3(new SelfDestruction());
        handleModule3(new TPATrap());
        handleModule3(new ClickGUI());
        handleModule3(new KeepSprintHUD());
        handleModule3(new DisplaceHUD());
        handleModule3(new ClientSettings());
        handleModule3(new AutoDisable());
        handleModule3(new LagbackDebug());
        handleModule3(new PotionEffectDebug());
        handleModule3(new Cape());
        handleModule3(new DiscordRPC());
        // VENDOR-DISABLED: handleModule3(new IRC());
        // VENDOR-DISABLED: handleModule3(new Stafftracker());
        handleModule(new AutoGrinder(), HypixelPitType.HYPIXEL_PIT, PitCategory.PREMIUM);
        handleModule(new AutoHunt(), HypixelPitType.HYPIXEL_PIT, PitCategory.PREMIUM);
        handleModule(new AutoAura(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoPit(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoPitSpawn(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoMaths(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoEgg(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoFish(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoFeast(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoCake(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoKings(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoPotato(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoRefill(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoSpire(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoTacticalInsertion(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoBulletTime(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoVolley(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoTelebow(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoDenicker(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoParty(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new BedrockAlerts(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoOof(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new LavaFill(), HypixelPitType.HYPIXEL_PIT, PitCategory.AUTOMATION);
        handleModule(new DragonEgg(), HypixelPitType.HYPIXEL_PIT, PitCategory.MINOR_EVENT);
        handleModule(new RingMiner(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new JumpBoostDetector(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new PodDetection(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new WipeAlert(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new UtilityModule(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new TelebowESP(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new WeaselGrinder(), HypixelPitType.WEASEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoCompleteChecker(), HypixelPitType.WEASEL_PIT, PitCategory.AUTOMATION);
        handleModule(new AutoEnchant(), HypixelPitType.HYPIT, PitCategory.AUTOMATION);
        handleModule(new SpawnTimer(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new AuraTimer(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new TelebowTimer(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new PerunCounter(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new FeastTimer(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new EggTimer(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new PinTimer(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new StreakInfo(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new PlayerList(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new ThinkHelper(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new ToxicInfo(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new VenomedTimer(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new VenomedESP(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new CombatDetection(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new RegenHUD(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new SpeedHUD(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new VenomIndicator(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new ExploTimer(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new ChickenTimer(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new RequirementChecker(), HypixelPitType.ORION_PIT, PitCategory.HUD);
        handleModule(new OrionAntiVanish(), HypixelPitType.ORION_PIT, PitCategory.UTILITY);
        handleModule(new ClassicVanishDebug(), HypixelPitType.CLASSIC_PIT, PitCategory.UTILITY);
        handleModule(new ExplosionRadius(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new HologramDebugger(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new PodHUD(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new PheonixHUD(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new MindAssaultDamage(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new MirrorDamage(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new PainDamage(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new RGMDamage(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new SharkDamage(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new SolitudeReduction(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new SoulripperDamage(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new LowLifeWarning(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new EnderChestCooldown(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new RingTemplate(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new SpawnPoints(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new PitChat(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new MysticDropCounter(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new MidCounter(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new JumpPotionHUD(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new ResistanceHUD(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new StrengthHUD(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new FeatherCountHUD(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new FakeContractHUD(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new Events(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new AssassinESP(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new MysticESP(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new PodESP(), HypixelPitType.HYPIXEL_PIT, PitCategory.HUD);
        handleModule(new GambleSwapper(), HypixelPitType.HYPIXEL_PIT, PitCategory.SWAP);
        handleModule(new AutoLifesteal(), HypixelPitType.HYPIXEL_PIT, PitCategory.SWAP);
        handleModule(new PantsSwapper(), HypixelPitType.HYPIXEL_PIT, PitCategory.SWAP);
        handleModule(new PitSwap(), HypixelPitType.HYPIXEL_PIT, PitCategory.SWAP);
        handleModule(new SlotSwap(), HypixelPitType.HYPIXEL_PIT, PitCategory.SWAP);
        handleModule(new SogeSwap(), HypixelPitType.HYPIXEL_PIT, PitCategory.SWAP);
        handleModule(new ArmaSwap(), HypixelPitType.HYPIXEL_PIT, PitCategory.SWAP);
        handleModule(new DarkSwap(), HypixelPitType.HYPIXEL_PIT, PitCategory.SWAP);
        handleModule(new InventoryShortcuts(), HypixelPitType.HYPIXEL_PIT, PitCategory.SWAP);
        handleModule(new SwapMacros(), HypixelPitType.HYPIXEL_PIT, PitCategory.SWAP);
        handleModule(new ScoSwap(), HypixelPitType.HYPIXEL_PIT, PitCategory.SWAP);
        handleModule(new PaparazziSwap(), HypixelPitType.HYPIXEL_PIT, PitCategory.SWAP);
        handleModule(new PheonixSwap(), HypixelPitType.HYPIXEL_PIT, PitCategory.SWAP);
        handleModule(new PodSwap(), HypixelPitType.HYPIXEL_PIT, PitCategory.SWAP);
        handleModule(new RightClickSwap(), HypixelPitType.HYPIXEL_PIT, PitCategory.SWAP);
        handleModule(new VenomSwap(), HypixelPitType.HYPIXEL_PIT, PitCategory.SWAP);
        handleModule(new DivineSwap(), HypixelPitType.HYPIXEL_PIT, PitCategory.SWAP);
        handleModule(new AntiVenomSwap(), HypixelPitType.HYPIXEL_PIT, PitCategory.SWAP);
        handleModule(new FrenchSwap(), HypixelPitType.HYPIXEL_PIT, PitCategory.SWAP);
        handleModule(new AntiLauncher(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new LauncherDebugger(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new BowStreak(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new MapDetection(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new MiddleClickHistory(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new PitNPC(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new PitAlerts(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new SafetyMeasures(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new ScammerAlerts(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new SoundRemover(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new PitUtils(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new DivineDetector(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new CombatLogger(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new PitDebugger(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new ChatLog(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new Botting(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new TelebowFix(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new SpireFix(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new SpireExporter(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new AntiVenom(), HypixelPitType.HYPIXEL_PIT, PitCategory.UTILITY);
        handleModule(new Beast(), HypixelPitType.HYPIXEL_PIT, PitCategory.MAJOR_EVENT);
        handleModule(new Pizza(), HypixelPitType.HYPIXEL_PIT, PitCategory.MAJOR_EVENT);
        handleModule(new Robbery(), HypixelPitType.HYPIXEL_PIT, PitCategory.MAJOR_EVENT);
        handleModule(new Squads(), HypixelPitType.HYPIXEL_PIT, PitCategory.MAJOR_EVENT);
        handleModule(new Blockhead(), HypixelPitType.HYPIXEL_PIT, PitCategory.MAJOR_EVENT);
        handleModule(new TDM(), HypixelPitType.HYPIXEL_PIT, PitCategory.MAJOR_EVENT);
        handleModule(new Auction(), HypixelPitType.HYPIXEL_PIT, PitCategory.MINOR_EVENT);
        handleModule(new CarePackage(), HypixelPitType.HYPIXEL_PIT, PitCategory.MINOR_EVENT);
        handleModule(new Cake(), HypixelPitType.HYPIXEL_PIT, PitCategory.MINOR_EVENT);
        handleModule(new Gamble(), HypixelPitType.HARRYS_PIT, PitCategory.MINOR_EVENT);
        handleModule(new MeteoriteTracker(), HypixelPitType.HARRYS_PIT, PitCategory.MINOR_EVENT);
        handleModule3(new ResourceTracker());
        handleModule3(new Mellow());
        Module module = this.computeModule("Slot Lock");
        if (module != null) {
            module.process30();
        }

        process();
    }

    private static void handleModule(Module module, HypixelPitType hypixelPitType, PitCategory pitCategory) {
        module.handleHypixelPitType(hypixelPitType, pitCategory);
        handleModule3(module);
    }

    public static void handleModule3(Module m) {
        entries.add(m);
        process2();
        lookupMap2.put(m.getName(), m);
        if (m.getKey() != 0) {
            addToKeyMap(m.getKey(), m);
        }
    }

    public static void handleModule(Module m) {
        entries.remove(m);
        process2();
        lookupMap2.remove(m.getName());
        if (m.getKey() != 0) {
            removeFromKeyMap(m.getKey(), m);
        }

        entries3.remove(m);
    }

    public static List<Module> getEntries2() {
        return entries2;
    }

    public static Module[] getModuleArray() {
        return moduleArray;
    }

    private static void process2() {
        moduleArray = entries.toArray(new Module[entries.size()]);
    }

    public static void onRender() {
        if (!EditableModulesHelper.isActive()) {
            for (Module module : entries3.toArray(new Module[0])) {
                if (!module.checkState44()) {
                    entries3.remove(module);
                } else if (module.checkState29() && !EditableModulesHelper.matchesModule(module)) {
                    try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                        module.onRender();
                    }
                }
            }
        }
    }

    public static void handleInt(int k) {
        if (!TabGUI.matchesInt(k)) {
            if (Minecraft.getMinecraft() == null
                || Minecraft.getMinecraft().thePlayer == null
                || Brutality.instance == null
                || Brutality.appdataHandler == null
                || !Brutality.appdataHandler.matchesInt(k)) {
                var modules = lookupMap.get(k);
                if (modules != null && !modules.isEmpty()) {
                    for (Module module : new LinkedHashSet<>(modules)) {
                        if (module.getKey() == k) {
                            module.process28();
                        }
                    }
                }
            }
        }
    }

    public static List<Module> computeList(int keyCode) {
        if (keyCode == 0) {
            return null;
        }
        var modules = lookupMap.get(keyCode);
        return modules != null && !modules.isEmpty() ? modules : null;
    }

    public static boolean matchesInt(int keyCode) {
        return TabGUI.matchesInt2(keyCode) || computeList(keyCode) != null || SwapMacros.matchesInt(keyCode);
    }

    public static void handleString(String message) {
        MessageUtils.x(message);
    }

    public Module computeModule(String moduleName) {
        return lookupMap2.get(!"MiserySwap".equalsIgnoreCase(moduleName) && !"VenomGuard".equalsIgnoreCase(moduleName) ? moduleName : "AntiVenomSwap");
    }

    public static void addToKeyMap(int key, Module module) {
        if (key != 0 && module != null) {
            var modules = lookupMap.computeIfAbsent(key, integer -> new java.util.ArrayList<>());
            if (!modules.contains(module)) {
                modules.add(module);
            }
        }
    }

    public static void removeFromKeyMap(int key, Module module) {
        var modules = lookupMap.get(key);
        if (modules != null) {
            modules.removeIf(module2 -> module2 == module);
            if (modules.isEmpty()) {
                lookupMap.remove(key);
            }
        }
    }

    public static void handleInt(int oldKey, int newKey, Module module) {
        if (oldKey != 0) {
            removeFromKeyMap(oldKey, module);
        }

        if (newKey != 0) {
            addToKeyMap(newKey, module);
        }
    }

    private static void process() {
        if (!flag) {
            File file = new File(Minecraft.getMinecraft().mcDataDir, "Brutality/hidden.txt");
            if (file.exists()) {
                String str;
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    while ((str = reader.readLine()) != null) {
                        String str2 = str.trim();
                        if (!str2.isEmpty()) {
                            itemSet.add(str2);
                        }
                    }
                } catch (IOException ignored) {
                }
            }

            flag = true;
        }
    }

    public static boolean matchesModule(Module module) {
        return module == null ? false : matchesString(module.getName());
    }

    public static boolean matchesString(String moduleName) {
        return moduleName != null && !moduleName.trim().isEmpty() ? itemSet.contains(moduleName.trim()) : false;
    }

    public static void handleModule2(Module module) {
        if (module != null) {
            handleString3(module.getName());
        }
    }

    public static void handleString3(String moduleName) {
        if (moduleName != null && !moduleName.trim().isEmpty()) {
            String str = moduleName.trim();
            if (itemSet.contains(str)) {
                handleString2(str);
            } else {
                handleString4(str);
            }

            APPDATAHandler.process4();
        }
    }

    private static void handleString4(String str) {
        try {
            File file = new File(Minecraft.getMinecraft().mcDataDir, "Brutality");
            if (!file.exists()) {
                file.mkdirs();
            }

            File file2 = new File(file, "hidden.txt");
            itemSet.add(str);

            try (PrintWriter writer = new PrintWriter(new FileWriter(file2))) {
                for (String str2 : itemSet) {
                    writer.println(str2);
                }
            }
        } catch (IOException ignored) {
        }
    }

    private static void handleString2(String str) {
        try {
            itemSet.remove(str);
            File file = new File(Minecraft.getMinecraft().mcDataDir, "Brutality/hidden.txt");
            if (!file.exists()) {
                return;
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (String str2 : itemSet) {
                    writer.println(str2);
                }
            }
        } catch (IOException ignored) {
        }
    }
}
