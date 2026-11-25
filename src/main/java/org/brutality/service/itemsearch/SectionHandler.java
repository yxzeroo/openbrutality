package org.brutality.service.itemsearch;

import java.util.HashMap;
import java.util.Map;
import org.brutality.module.Module;
// VENDOR-DISABLED: import org.brutality.module.impl.client.Stafftracker;
import org.brutality.module.impl.combat.AimAssist;
import org.brutality.module.impl.combat.Displace;
import org.brutality.module.impl.combat.FastBow;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.combat.LagRange;
import org.brutality.module.impl.combat.Reach;
import org.brutality.module.impl.combat.Velocity;
import org.brutality.module.impl.exploit.Blink;
import org.brutality.module.impl.exploit.Disabler;
import org.brutality.module.impl.exploit.SelfPotion;
import org.brutality.module.impl.misc.Insults;
import org.brutality.module.impl.movement.Flight;
import org.brutality.module.impl.movement.KeepSprint;
import org.brutality.module.impl.movement.NoSlowDown;
import org.brutality.module.impl.movement.Scaffold;
import org.brutality.module.impl.movement.Speed;
import org.brutality.module.impl.movement.Timer;
import org.brutality.module.impl.pit.hypixel.automation.AutoBuy;
import org.brutality.module.impl.pit.hypixel.automation.AutoGrinder;
import org.brutality.module.impl.pit.hypixel.automation.AutoHunt;
import org.brutality.module.impl.pit.hypixel.automation.AutoParty;
import org.brutality.module.impl.pit.hypixel.automation.AutoRefill;
import org.brutality.module.impl.pit.hypixel.automation.Events;
import org.brutality.module.impl.pit.hypixel.automation.FakeContractHUD;
import org.brutality.module.impl.pit.hypixel.automation.LowLifeWarning;
import org.brutality.module.impl.pit.hypixel.automation.MindAssaultDamage;
import org.brutality.module.impl.pit.hypixel.automation.PerunCounter;
import org.brutality.module.impl.pit.hypixel.automation.PinTimer;
import org.brutality.module.impl.pit.hypixel.automation.PlayerList;
import org.brutality.module.impl.pit.hypixel.hud.Cake;
import org.brutality.module.impl.pit.hypixel.hud.Pizza;
import org.brutality.module.impl.pit.hypixel.hud.PrestigeList;
import org.brutality.module.impl.pit.hypixel.hud.SpawnPoints;
import org.brutality.module.impl.pit.hypixel.hud.SpawnTimer;
import org.brutality.module.impl.pit.hypixel.hud.TelebowTimer;
import org.brutality.module.impl.pit.hypixel.hud.VenomedTimer;
import org.brutality.module.impl.pit.hypixel.minorevent.DragonEgg;
import org.brutality.module.impl.pit.hypixel.utility.BowStreak;
import org.brutality.module.impl.pit.hypixel.utility.PitAlerts;
import org.brutality.module.impl.pit.hypixel.utility.PitNPC;
import org.brutality.module.impl.pit.hypixel.utility.TelebowESP;
import org.brutality.module.impl.pit.hypixel.utility.VenomSwap;
import org.brutality.module.impl.pit.weasel.automation.WeaselGrinder;
import org.brutality.module.impl.player.BedAura;
import org.brutality.module.impl.player.ItemAlerts;
import org.brutality.module.impl.player.NoDropItem;
import org.brutality.module.impl.player.QuickBuy;
import org.brutality.module.impl.visual.Animations;
import org.brutality.module.impl.visual.ArrayList;
import org.brutality.module.impl.visual.BedESP;
import org.brutality.module.impl.visual.BlockOverlay;
import org.brutality.module.impl.visual.Chat;
import org.brutality.module.impl.visual.DamageTags;
import org.brutality.module.impl.visual.FOVView;
import org.brutality.module.impl.visual.FPSBooster;
import org.brutality.module.impl.visual.HUD;
import org.brutality.module.impl.visual.HitVector;
import org.brutality.module.impl.visual.InterfaceType;
import org.brutality.module.impl.visual.NameTags;
import org.brutality.module.impl.visual.PotionHUD;
import org.brutality.module.impl.visual.Scoreboard;
import org.brutality.module.impl.visual.TargetESP;
import org.brutality.module.impl.visual.TargetHUD;
import org.brutality.module.impl.visual.Tracers;
import org.brutality.module.impl.visual.XRay;

public final class SectionHandler {
    private static final Map<Class<? extends Module>, Map<String, String>> lookupMap = new HashMap<>();

    private SectionHandler() {
    }

    public static String computeString(Class<? extends Module> clazz, String str) {
        Map map = lookupMap.get(clazz);
        // VENDOR-DISABLED: Stafftracker.class section lookup
        return map == null ? null : (String)map.get(str);
    }

    private static void section(Class<? extends Module> clazz, String str, String... parts) {
        Map map = lookupMap.computeIfAbsent(clazz, clazz2 -> new HashMap<>());

        for (String str2 : parts) {
            map.put(str2, str);
        }
    }

    static {
        section(AimAssist.class, "Rotations", "Horizontal Speed", "Vertical Speed", "Enable Vertical");
        section(
            AimAssist.class,
            "Targeting",
            "Distance",
            "Max Angle",
            "Sorting",
            "Health Mode",
            "Hurttime Mode",
            "Players",
            "Mobs",
            "Animals",
            "Invisible",
            "Targets"
        );
        section(AimAssist.class, "Conditions", "Click Aim", "Check Block Break", "Through Walls");
        section(Displace.class, "Debug", "Debug");
        section(Displace.class, "Direction", "Mode", "Angle", "Render arrow");
        section(Displace.class, "Timing", "Cooldown", "Hide rotations", "Override attacks", "Hittable hurt time");
        section(
            Displace.class,
            "Activation",
            "Only knockback items",
            "Players only",
            "Manual",
            "AutoClicker",
            "KillAura",
            "Only When Venomed",
            "Only Displace If Not Looking",
            "Looking Angle",
            "Only If Turned Around",
            "Stop On Hurt",
            "Only Darks",
            "Disable In PIT"
        );
        section(FastBow.class, "Shooting", "Mode", "Packets", "Auto Shoot");
        section(FastBow.class, "Draw Timing", "Delay", "Min Draw Time", "Smart Delay", "Smart Delay Min", "Smart Delay Max");
        section(FastBow.class, "Conditions", "Only On Ground", "Require Full Health", "Allow Creative");
        section(
            KillAura.class,
            "Target Selection",
            "Type",
            "Switch Delay",
            "Target Amount (Both)",
            "Horizontal Angle",
            "Vertical Angle",
            "Sorting",
            "Hurttime Mode",
            "Health Mode",
            "Teams",
            "Ally HitSync",
            "Ally HitSync Range"
        );
        section(KillAura.class, "Rotations", "Smoothing (%)", "Angle-Step (%)", "Rotations", "Hide Client Rotations", "Noise");
        section(KillAura.class, "Ranges", "Block Range", "Rotation Range", "Swing Range", "Attack Range");
        section(KillAura.class, "Autoblock", "Autoblock", "Watchdog Mode", "Grim Mode", "Manual Block", "Autoblock In Inventory");
        section(KillAura.class, "Attack Timing", "Min CPS", "Max CPS", "CPS Mode", "Attack while delaying");
        section(
            KillAura.class,
            "Conditions",
            "Pit Spawn",
            "Through Walls",
            "Through Walls Mode",
            "Raycast",
            "Weapons Only",
            "Require Press",
            "Disable While Mining",
            "Disable When Blocking",
            "Disable In GUI",
            "Disable While Pants Swapping",
            "Only While Sneaking",
            "Players",
            "Animals",
            "Mobs",
            "Golem",
            "Silverfish",
            "Invisible",
            "Ignore In Liquid",
            "Ignore Shop",
            "Ignore Chain",
            "Ignore Darks",
            "Ignore Set",
            "Targets"
        );
        section(KillAura.class, "Debug", "Debug");
        section(LagRange.class, "Range And Delay", "Range", "Maximum delay");
        section(LagRange.class, "Activation", "Sprint reset", "Block sword", "Used splash potion", "Holding a weapon");
        section(
            LagRange.class,
            "Position Indicator",
            "Real position indicator",
            "Show in first person",
            "Indicator color",
            "Indicator line width",
            "Indicator filled"
        );
        section(Reach.class, "Reach Distance", "Min Reach", "Max Reach", "Through Walls", "Walls Reach");
        section(
            Reach.class,
            "Conditions",
            "Weapon Only",
            "Sprint Only",
            "Sneaking Only",
            "Jumping Only",
            "Moving Only",
            "Players Only",
            "Combat Only",
            "Legit Check"
        );
        section(Reach.class, "Hit Selection", "Randomize", "Random Amount", "Hit Select", "Hit Chance");
        section(Reach.class, "Sprint", "Keep-Sprint");
        section(Velocity.class, "Knockback", "Mode", "Horizontal", "Vertical", "Air Velocity");
        section(Velocity.class, "Activation Timing", "Chance", "Delay", "Ticks");
        section(Velocity.class, "Friction", "Sprint Friction", "Walk Friction");
        section(Velocity.class, "Conditions", "Only while moving", "Staff check");
        section(Velocity.class, "Release", "Release delay on ground", "Release when attack");
        section(Velocity.class, "HUD", "Show HUD");
        section(Blink.class, "Blink Controls", "Shift Teleport", "Right Click Blink", "Freeze Motion");
        section(Blink.class, "Position Rendering", "Show Original Position", "Render Mode", "Custom Color", "Scale");
        section(
            Blink.class,
            "Packet Buffer",
            "Mode",
            "Packet Direction",
            "Maximum Seconds (Outbound)",
            "Max Seconds (Outbound)",
            "Maximum Ticks (Inbound)",
            "Max Ticks (Inbound)"
        );
        section(Blink.class, "Proximity", "Stop If Player Near", "Proximity Distance");
        section(Disabler.class, "Mode", "Mode");
        section(
            Disabler.class,
            "Packet Selection",
            "Packets",
            "Cancel C0B",
            "Cancel C0F",
            "Cancel C00",
            "Cancel C03",
            "Cancel C07",
            "Cancel C08",
            "Cancel C0A",
            "Cancel C0C",
            "Cancel C0D",
            "Cancel C0E",
            "Cancel C13",
            "Cancel C16",
            "Cancel C17",
            "Cancel C18",
            "Cancel C19"
        );
        section(Disabler.class, "Transaction Timing", "Transaction Delay (ms)");
        section(Disabler.class, "Position", "Y Offset");
        section(
            SelfPotion.class,
            "Potion Effects",
            "Speed",
            "Speed Level",
            "Strength",
            "Strength Level",
            "Jump Boost",
            "Jump Level",
            "Regeneration",
            "Regen Level",
            "Resistance",
            "Resistance Level",
            "Haste",
            "Haste Level",
            "Fire Resistance",
            "Night Vision",
            "Invisibility",
            "Absorption",
            "Absorption Level"
        );
        section(SelfPotion.class, "Reapplication", "Reapply Ticks");
        section(Insults.class, "Messages", "Mode", "Pit Group", "Custom Message", "Custom Text", "Custom Prefix");
        section(Insults.class, "Recipients", "Include Assists", "KOS Only");
        section(Insults.class, "Message Timing", "Delay", "Auto Delay");
        section(Insults.class, "Debug", "Debug Mode");
        section(Flight.class, "Flight Mode", "Mode");
        section(Flight.class, "Teleport", "Teleport Delay", "Teleport Length");
        section(Flight.class, "Timer", "Timer Amount");
        section(Flight.class, "Movement", "Horizontal Speed", "Vertical Speed", "Speed", "Motion Y", "Custom Speed", "Speed Amount");
        section(Flight.class, "View", "View Bobbing");
        section(Flight.class, "Safety", "Anti-kick");
        section(Flight.class, "Damage", "Damage", "Damage Mode");
        section(Flight.class, "Boat Fly", "Boat Fly: Maximum Boats", "Boat Fly: Horizontal Speed", "Boat Fly: Vertical Speed", "Boat Fly: Flight Time");
        section(KeepSprint.class, "Sprint Motion", "Mode", "Keep Motion", "Motion", "Disable Air Cancel", "Suppress Ticks");
        section(
            KeepSprint.class,
            "Activation",
            "Darks Only",
            "Stop On Hurt",
            "Disable While Standing Still",
            "Only Jump Boost",
            "Air Only",
            "Only When Target Not Looking"
        );
        section(KeepSprint.class, "Debug", "Debug");
        section(NoSlowDown.class, "Modes", "Sword Mode", "Item Mode");
        section(NoSlowDown.class, "Sword Slowdown", "Swords", "Sword Forward", "Sword Strafe", "blocking only", "Sword only");
        section(NoSlowDown.class, "Food Slowdown", "Food", "Food Forward", "Food Strafe");
        section(NoSlowDown.class, "Bow Slowdown", "Bow", "Bow Forward", "Bow Strafe");
        section(NoSlowDown.class, "Potion Slowdown", "Potions", "Potions Forward", "Potions Strafe");
        section(Scaffold.class, "Placement", "Mode", "No Hit Check", "Search Range", "Intelligent Picker");
        section(Scaffold.class, "Towering", "Tower", "Tower Center", "Tower When", "Tower Mode", "Tower Speed");
        section(Scaffold.class, "Smooth", "Smooth Precision", "Smooth Turn Min", "Smooth Turn Max", "Smooth Hold Aim", "Smooth Startup");
        section(Scaffold.class, "Telly", "Telly Mode", "Telly After Place No Hit Check");
        section(
            Scaffold.class,
            "Rotations",
            "Rotation Mode",
            "Raycast Speed",
            "Smooth Rots",
            "Max Rotation Speed",
            "Min Rotation Speed",
            "Pre Rotation Lead",
            "Snap Rotation",
            "No Place when Snapping"
        );
        section(Scaffold.class, "Timers", "Custom Timer", "Tower Timer Speed", "Normal Timer Speed");
        section(Scaffold.class, "Movement", "KeepY", "KeepY Only Speed", "No Sprint", "Edge Safe", "Down");
        section(Scaffold.class, "Conditions", "Jump Boost Only", "Check Pit Spawn");
        section(Scaffold.class, "HUD", "Block Counter");
        section(Timer.class, "Timer Speed", "Mode", "Speed", "Min Speed", "Max Speed");
        section(Speed.class, "Speed", "Mode", "Maximize Strafe", "Stop Motion", "Anti RGM", "Speed", "Timer");
        section(Speed.class, "Watchdog Sim", "Flying", "Holding blocks", "Holding bow", "Holding throwable", "Holding rod", "In liquid", "Targeting");
        section(Timer.class, "Timing", "Randomize Interval", "Activation Delay", "Maximum Ticks", "Max Ticks");
        section(Timer.class, "Activation", "Horizontal Only", "Vertical Only", "On Ground Only", "Ignore In Liquid", "Only While Sprinting", "Pause On Hurt");
        section(AutoBuy.class, "Debug", "Debug");
        section(
            AutoBuy.class,
            "Shop Purchases",
            "Shop",
            "Diamond Sword",
            "Diamond ChestPlate",
            "Diamond Leggings",
            "Diamond Boots",
            "FirstAid Egg",
            "Combat Spade",
            "Iron Pack",
            "Obsidian (8)",
            "Obsidian (64)"
        );
        section(
            AutoGrinder.class,
            "Grinding",
            "Disconnect While Waiting",
            "Join Before Minutes",
            "Streak Mode",
            "Auto Biome Mode",
            "Use Fresh Pants",
            "Fresh Pants Mode",
            "Fresh Pants Delay",
            "Fresh Pants Safe Mode",
            "Mystic Pickup Distance",
            "Attack Range",
            "Max Target PIT Range",
            "Target Switch Delay",
            "FOV",
            "Jump Chance Per Tick",
            "Strafe Chance Per Tick",
            "Min Players",
            "Min Lobby Players",
            "Min Pit Players To Run",
            "AFK Chance On Spawn",
            "Disable After Mins",
            "Take Scheduled Breaks",
            "Break Every Mins",
            "Break Length Mins",
            "Ban AFK Length Mins",
            "Ban Response Min Delay",
            "Ban Response Max Delay",
            "Auto Perk Frequency",
            "Window Click Delay",
            "Auto Buy",
            "Auto Buy Mode",
            "Auto Buy Every Mins",
            "What To Auto Buy",
            "Diamond Sword",
            "Diamond Chestplate",
            "Diamond Leggings",
            "Diamond Boots",
            "First-Aid Egg",
            "Combat Spade",
            "Iron Pack",
            "Obsidian (8)",
            "Obsidian (64)",
            "Spawn Y Level",
            "Limit Render Distance",
            "FPS Limit",
            "Limit FOV",
            "Config",
            "Auto Self Check",
            "On Level 120",
            "On Ban",
            "Auto Contract",
            "On Contracts Ran Out",
            "Oof On Bad Path",
            "Oof On No Hit",
            "Oof No Hit Seconds",
            "Combat Mode",
            "HUD",
            "HUD X",
            "HUD Y",
            "Allow Swap Lobbies",
            "Ignore Diamonds",
            "Chase Out of PIT",
            "Diamond If Looking",
            "Ignore Friends",
            "Drop Down",
            "Don't Attack While Falling",
            "Ignore While Riding",
            "Low Life Check",
            "Sound Alerts",
            "Safe Jump",
            "Pick Up Mystic Drops",
            "Do Rage Pit Event",
            "Do TDM Event",
            "Do Beast Event",
            "Do Robbery Event",
            "Do Spire Event",
            "Do Raffle Event",
            "Do Blockhead Event",
            "Do Squads Event",
            "Oof When Contract Completed",
            "Random AFK When Waiting",
            "Do Kill Contracts",
            "Do Chain Kill Contracts",
            "Do Kill Streak Contracts",
            "Do Sneak Kills Contracts",
            "Do Naked Kills Contracts",
            "Do Diamond Sword Kills Contracts",
            "Do Claim Bounties Contracts",
            "Do Gold Contracts",
            "Auto Perk",
            "Smart Perk Cooldown",
            "Perk 1",
            "Perk 2",
            "Perk 3",
            "Perk 4",
            "Spire Perks",
            "Spire Perk 1",
            "Spire Perk 2",
            "Spire Perk 3",
            "Spire Perk 4",
            "Raffle Max Tickets",
            "Buy XP Boost",
            "Buy Gold Boost",
            "Buy Damage Boost",
            "Buy Damage Reduction",
            "Buy El Gato",
            "Buy Killstreaks",
            "Killstreak 1",
            "Killstreak 2",
            "Killstreak 3",
            "Debug Overlay",
            "Schedule",
            "Schedule Start Hour",
            "Schedule Stop Hour",
            "Debug Manual State",
            "Debug Target",
            "Do KOTH",
            "Disable On Launch",
            "Enable Performance Settings",
            "Cancel Rendering",
            "Skip Render World",
            "Cancel Sound",
            "Combat",
            "Movement",
            "Grinding",
            "Contracts",
            "Perks And Killstreaks",
            "Lobby And Safety",
            "Performance",
            "HUD And Debug",
            "Major Events",
            "Safety And Recovery",
            "Respond To Name",
            "Response Min Delay (sec)",
            "Response Max Delay (sec)",
            "Response Message",
            "Custom Response",
            "When Mentioned",
            "Chat Alerts",
            "Check For Teleports",
            "On Teleport",
            "Teleport Check",
            "Pause On Orion Vanish",
            "When Vanished Player Found",
            "Orion Vanish Checker"
        );
        section(AutoGrinder.class, "Rotations", "Smooth Camera", "Yaw Speed", "Pitch Speed", "Aim Mode");
        section(
            AutoHunt.class,
            "Hunting",
            "Hunt Mode",
            "Target",
            "Fight Enemies",
            "Fight Enemy Distance",
            "Megastreaks Only",
            "Bounty Threshold",
            "Bounty Filter",
            "Chase Out Of PIT",
            "Use Focus + KOS",
            "Fight Range",
            "Swing Range",
            "Combat Mode",
            "Spawn",
            "Drop Down",
            "Drop Location",
            "Jump Pot",
            "Min Jump Distance",
            "Jump Chance Per Tick",
            "Strafe Range",
            "Strafe Chance",
            "Perk Click Delay",
            "Don't KeepSprint In PIT",
            "KeepSprint When Unwatched",
            "Kung Fu",
            "ESP",
            "HUD",
            "HUD X",
            "HUD Y",
            "Rage Pit",
            "TDM",
            "Beast",
            "Robbery",
            "Spire",
            "Raffle",
            "Blockhead",
            "Squads",
            "KOTH",
            "After Kill",
            "After Combat",
            "Lobby Swap",
            "Auto Buy",
            "Auto Buy Mode",
            "Auto Buy Every Mins",
            "What To Auto Buy",
            "Diamond Sword",
            "Diamond Chestplate",
            "Diamond Leggings",
            "Diamond Boots",
            "First-Aid Egg",
            "Combat Spade",
            "Iron Pack",
            "Obsidian (8)",
            "Obsidian (64)",
            "Auto Perk",
            "Auto Perk Frequency",
            "Auto Bounty Hunter Perk",
            "Bounty Hunter Slot",
            "Perk 1",
            "Perk 2",
            "Perk 3",
            "Perk 4",
            "Mine Obsidian When Stuck",
            "Auto Buy Click Delay",
            "Targeting",
            "Combat",
            "Movement",
            "Automation",
            "Perks",
            "Major Events",
            "HUD And Visuals"
        );
        section(AutoHunt.class, "Rotations", "Yaw Speed", "Pitch Speed", "Smooth Camera");
        section(AutoParty.class, "Party Commands", "Mode", "Send /g party");
        section(AutoParty.class, "Invite Timing", "Interval (ticks)", "Randomize");
        section(AutoParty.class, "Warping", "Auto warp", "Auto Warp Interval (ticks)");
        section(AutoParty.class, "Player Information", "Send Location", "Show In Spawn", "Show HP", "Show HP Below");
        section(AutoRefill.class, "Inventory Controls", "Auto Open Inventory", "Auto Close Inventory", "Delay");
        section(AutoRefill.class, "Obsidian", "Obsidian Threshold", "Refill Obsidian", "Limit Obsidian Slots", "Max Obsidian Slots");
        section(AutoRefill.class, "Jump Potions", "Refill Jump Potion", "Limit Jump Potion Slots", "Max Jump Potion Slots");
        section(AutoRefill.class, "Refill Behavior", "Smart Refill", "Disable on Manual Interaction", "Auto Toggle Off");
        section(
            FakeContractHUD.class,
            "Contract Selection",
            "Contract Type",
            "Contract Count",
            "Contract Filters",
            "Kill Contracts",
            "Streak Contracts",
            "Bow Contracts",
            "Lava Contracts",
            "Grass Contracts",
            "Obsidian Contracts",
            "Sneaking Contracts",
            "Gold Contracts",
            "Apple Contracts",
            "Bounty Contracts",
            "Chain Contracts",
            "Fishing Contracts",
            "Kung Fu Contracts",
            "No Armor Contracts",
            "Progress Settings",
            "Show Progress",
            "Random Progress",
            "Progress Percentage",
            "Simulate Progress",
            "Progress Speed",
            "Custom Contracts",
            "Enable Custom",
            "Custom Contract 1",
            "Custom Contract 2",
            "Custom Contract 3",
            "Display Settings",
            "Show Contract Label",
            "Show Timer Icon",
            "Timer Format",
            "Color Code Timer"
        );
        section(FakeContractHUD.class, "Refresh", "Randomize on Enable", "Auto Refresh", "Refresh Interval (min)");
        section(FakeContractHUD.class, "Contract Timer", "Timer Mode", "Static Time (min)", "Countdown Time (min)", "Show Seconds");
        section(
            LowLifeWarning.class,
            "Warning Display",
            "X",
            "Y",
            "Item Spacing",
            "Show List Display",
            "Color Inventory",
            "Show Item Name",
            "Show Lives Count",
            "Critical Lives",
            "Warning Lives",
            "Safe Lives",
            "Auto Spawn",
            "Auto Spawn On Leave",
            "Show Warning Title",
            "Display Settings",
            "Threshold Settings",
            "Auto Spawn Settings"
        );
        section(PinTimer.class, "HUD Position", "X Pos", "Y Pos");
        section(PinTimer.class, "World Timer", "Show 3D Timer", "3D Text Scale");
        section(PinTimer.class, "Pin Detection", "Show My Pins");
        section(PinTimer.class, "Text", "Show Label", "Show Seconds Suffix", "Time Format", "Bold Text", "Custom Font");
        section(
            PlayerList.class,
            "Player Lists",
            "X",
            "Y",
            "Friends X",
            "Friends Y",
            "Enemies X",
            "Enemies Y",
            "Darks X",
            "Darks Y",
            "Regularity X",
            "Regularity Y",
            "TNT X",
            "TNT Y",
            "Truce X",
            "Bounties X",
            "Bounties Y",
            "Truce Y",
            "Player Spacing",
            "Scale",
            "Show Venomed",
            "Show Pinned",
            "Show Lobby Info",
            "Custom Lobby Text",
            "KOS Mark Position",
            "Separate Lists",
            "Auto Stack Lists",
            "Show Darks List",
            "Show Regularity List",
            "Show Enemies List",
            "Show Friends List",
            "Show Nicked List",
            "Show TNT List",
            "Show Bounties List",
            "Show Truce List",
            "Use Brackets",
            "Bracket Type",
            "Bracket Color",
            "Bold Brackets",
            "Show Location",
            "Show Distance",
            "Combine Location/Distance",
            "Bold Location",
            "Location Color",
            "Show Health",
            "Show KOS",
            "Show Friends",
            "Show Armor",
            "Show Enchants",
            "Show AFK",
            "Show Bedrock",
            "Bold Enchants",
            "Detect Streaking Set",
            "Detect Sweaty Set",
            "Detect Gold Set",
            "Display Settings",
            "List Organization",
            "Separate List Positions",
            "Style Settings",
            "Show Settings",
            "Pit Enchants"
        );
        section(PrestigeList.class, "HUD Position", "Y Pos", "X Pos");
        section(
            PrestigeList.class,
            "Prestige Filters",
            "Show Prestige 0",
            "Show Prestige 1-4",
            "Show Prestige 5-9",
            "Show Prestige 10-14",
            "Show Prestige 15-19",
            "Show Prestige 20-24",
            "Show Prestige 25-29",
            "Show Prestige 30-34",
            "Show Prestige 35-39"
        );
        section(PrestigeList.class, "Display Conditions", "Show Location", "Only Show On Pit");
        section(
            SpawnPoints.class,
            "Spawn Markers",
            "Show Box",
            "Show Beacon",
            "Show Text",
            "Show Arrow",
            "Pulse Effect",
            "Custom Font",
            "Box Color",
            "Beacon Color",
            "Arrow Color",
            "Box Size",
            "Beacon Height",
            "Line Width",
            "Text Scale",
            "Arrow Size",
            "Arrow Speed",
            "Arrow Distance",
            "Fill Opacity",
            "Outline Opacity",
            "Beacon Opacity",
            "Visual",
            "Colors",
            "Size",
            "Animation",
            "Opacity"
        );
        section(SpawnTimer.class, "HUD Position", "X Pos", "Y Pos");
        section(SpawnTimer.class, "Timer Appearance", "Show Label", "Show Seconds Suffix", "Time Format", "Bold Text", "Custom Font");
        section(SpawnTimer.class, "Chat", "Show In Chat");
        section(SpawnTimer.class, "Other Players", "Detect Others", "KOS Only", "Body Scale");
        section(PerunCounter.class, "HUD Position", "X Pos", "Y Pos");
        section(PerunCounter.class, "Counter", "Show Timer", "Hide When Idle", "Only Holding Perun");
        section(TelebowTimer.class, "HUD Position", "Y Pos", "X Pos");
        section(Events.class, "HUD Position", "X Position", "Y Position");
        section(Events.class, "Event List", "Event Count", "Night Quest");
        section(TelebowTimer.class, "Self Timer", "Show In Chat", "Warn on Dark Pants");
        section(TelebowTimer.class, "Appearance", "Show Label", "Show Seconds Suffix", "Time Format", "Bold Text", "Custom Font");
        section(
            TelebowTimer.class,
            "Other Players",
            "Detect Others",
            "Others On HUD",
            "Estimate Other Hits",
            "Others In Chat",
            "Show On Nametags",
            "Show Telebow Tier",
            "KOS Only"
        );
        section(VenomedTimer.class, "HUD Position", "X Pos", "Y Pos");
        section(VenomedTimer.class, "Detection", "Detect for Others", "Show Only My Venoms", "Venomed KOS Only", "Show Only Victims");
        section(VenomedTimer.class, "Combo Tracking", "Hit Count", "Hit Count Mode", "2 Hit Sound", "Show Hit Count", "Show Combo Timer");
        section(VenomedTimer.class, "Appearance", "Show Hunters And Victims", "Show Label", "Show Seconds Suffix", "Time Format", "Bold Text", "Custom Font");
        section(
            VenomedTimer.class,
            "Announcements",
            "Announce Venom",
            "Guild Chat",
            "Party Chat",
            "Announce KOS Only",
            "Announce HP",
            "Announce HP Below",
            "Announce Location"
        );
        section(Pizza.class, "Villagers", "ESP", "ESP Labels");
        section(Pizza.class, "Top Wallets", "Top Player ESP", "Top Player Tracers", "Top Players");
        section(Pizza.class, "Pizza HUD", "Pizza HUD", "HUD X", "HUD Y");
        section(Cake.class, "Features", "Aura", "ESP");
        section(Cake.class, "ESP", "ESP Cakes", "ESP Cherries", "ESP Choc Chips", "Outline Opacity", "Fill Opacity");
        section(DragonEgg.class, "Features", "Aura", "ESP");
        section(Cake.class, "Eating", "Type", "Sorting", "Eat Mode", "Swing");
        section(Cake.class, "Click Timing", "Packet Spams", "Switch Delay", "Min CPS", "Max CPS");
        section(Cake.class, "Rotations", "Circle Speed", "Rotation");
        section(Cake.class, "Targets", "Max Targets", "Range", "Cherries", "Choc Chips", "Cakes", "Cakes Near Cherries", "Cakes Near Choc Chips");
        section(Cake.class, "HUD And Rendering", "Render", "HUD", "HUD X", "HUD Y", "HUD Font Size");
        section(Cake.class, "Conditions", "Right Click Only", "Ignore Swords");
        section(Cake.class, "Debug", "Debug");
        section(DragonEgg.class, "Interaction", "Range", "Swing");
        section(DragonEgg.class, "Click Timing", "Min CPS", "Max CPS");
        section(DragonEgg.class, "HUD And Rendering", "Render", "HUD", "HUD X", "HUD Y", "HUD Font Size");
        section(DragonEgg.class, "Rotations", "Rotation");
        section(DragonEgg.class, "Conditions", "Ignore Swords");
        section(DragonEgg.class, "Egg Rendering", "Outline", "Fill", "Tracers", "Show Distance", "Fill Opacity");
        section(DragonEgg.class, "Movement Alerts", "Move Notification", "Move Tracer", "Notification Duration (s)");
        section(DragonEgg.class, "Colors", "Color Mode", "Egg Color", "Tracer Color", "Move Tracer Color");
        section(VenomSwap.class, "Swap Behavior", "Mode", "Delay", "Safe Mode", "Use Inventory", "Alternative Pants");
        section(
            VenomSwap.class,
            "Victim Swaps",
            "Swap When Venomed (Victim)",
            "Swap Back (Victim)",
            "Swap Spade (Victim)",
            "Spade Slot",
            "Swap Egg (Victim)",
            "Egg Slot",
            "Swap Bedrock (Victim)",
            "Bedrock Slot"
        );
        section(VenomSwap.class, "Hunter Swaps", "Swap To Venom (Hunter)", "Swap Back Venom (Hunter)", "Swap On Venom Proc (Hunter)");
        section(VenomSwap.class, "Alternative Pants", "Diamond Leggings", "Iron Leggings", "Fresh Pants", "Chainmail Leggings");
        section(BowStreak.class, "Rotations", "Aim", "Prediction", "Yaw Speed %", "Pitch Speed %");
        section(BowStreak.class, "Shooting", "Always Shoot", "Charge Delay", "Random Stop", "Stop Chance %");
        section(BowStreak.class, "Target Selection", "Nons Only", "Radius", "Target Sort");
        section(BowStreak.class, "Alerts", "Alerts");
        section(
            PitNPC.class,
            "NPC Detection",
            "NPC Color",
            "Items",
            "Upgrades",
            "Quest Master",
            "Stats",
            "Prestige",
            "The Keeper",
            "Leaderboard Picker",
            "Spawn NPCs",
            "The King",
            "Baker",
            "Hay (Salesman)",
            "Bread (Salesman)",
            "Fish (Salesman)",
            "Kings Map NPCs",
            "Angel",
            "Demon",
            "Genesis Map NPCs"
        );
        section(
            PitAlerts.class,
            "Alerts",
            "Dark Enchant Detection",
            "Rage Enchant Detection",
            "Gamble Detection",
            "KOS Detection",
            "KOS Only",
            "Don't Alert If Looking",
            "Ignore Friends",
            "Detect Lobby",
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
            "Golden Handcuffs",
            "Ace of Spades",
            "Brakes!",
            "Combo: Breaching Charge",
            "Do it like the French",
            "Heigh-Ho",
            "New Deal",
            "Really Toxic",
            "Sybil",
            "Regularity",
            "Think of the People",
            "Auto-Swap on Darks",
            "Auto-Swap on Gamble",
            "Swap To",
            "Swap Delay (ms)",
            "Auto Swap-Back",
            "Swap-Back Delay (s)",
            "Dark Enchant Color",
            "Rage Enchant Color",
            "Gamble Color",
            "KOS Color",
            "Detection Modes",
            "Dark Enchants",
            "Rage Enchants",
            "Auto-Swap",
            "Colors"
        );
        section(PitAlerts.class, "Distance And Timing", "Distance (Blocks)", "Cooldown (Secs)");
        section(PitAlerts.class, "Display", "Text Scale", "Sound Enabled");
        section(WeaselGrinder.class, "Targeting", "Aim Reach", "Attack Reach", "Distance Sq", "Switch Target Range", "Skip Aim Range", "Lock Target");
        section(WeaselGrinder.class, "Rotations", "Yaw Smoothness", "Pitch Smoothness");
        section(WeaselGrinder.class, "Movement", "Jump Chance", "Strafe Chance", "Sprint Reset Ticks", "Wait Chance");
        section(WeaselGrinder.class, "Safety", "Session Stop (min)", "Quit on Panic");
        section(WeaselGrinder.class, "HUD", "HUD X", "HUD Y", "Show HUD");
        section(WeaselGrinder.class, "Debug", "Debug");
        section(WeaselGrinder.class, "Automation", "AutoXCarry", "XCarry Health", "AutoBuy", "AutoUber", "AutoSword", "Sword Slot");
        section(BedAura.class, "Breaking", "Range", "Break Delay", "Break Speed", "Surroundings", "Whitelist");
        section(BedAura.class, "Rotations", "Only S/S Rotate");
        section(BedAura.class, "Progress Bar", "Render Progress", "Bar Width", "Bar Height", "Y Offset", "Bar Outline");
        section(BedAura.class, "Appearance", "Custom Font", "Font Size", "Custom Color", "Font");
        section(ItemAlerts.class, "Alerts", "Chat Alerts", "HUD Alerts", "HUD Alerts Duration", "Delay (s)");
        section(ItemAlerts.class, "Detection", "Sharpness", "Protection", "Show Teammates");
        section(ItemAlerts.class, "Display", "Show Distance");
        section(ItemAlerts.class, "Debug", "Debug");
        section(
            ItemAlerts.class,
            "Tracked Items",
            "Chainmail Armor",
            "Iron Armor",
            "Diamond Armor",
            "Iron Sword",
            "Diamond Sword",
            "Diamond Pickaxe",
            "Ender Pearl",
            "Bridge Egg",
            "Fireball",
            "Bow",
            "Obsidian",
            "TNT",
            "Block Zapper",
            "Speed Potion",
            "Jump Boost Potion",
            "Invisibility Potion",
            "Iron Golem",
            "Machine Gun Bow",
            "Charlie the Unicorn",
            "Ice Bridge",
            "Sleeping Dust",
            "Unstable Teleportation Device",
            "Devastator Bow",
            "Miracle of the Stars",
            "Mystic Mirror"
        );
        section(NoDropItem.class, "Drop Timing", "Double Tap Delay");
        section(
            NoDropItem.class,
            "Protected Equipment",
            "Swords",
            "Wooden Sword",
            "Stone Sword",
            "Iron Sword",
            "Golden Sword",
            "Diamond Sword",
            "Helmets",
            "Leather Helmet",
            "Chainmail Helmet",
            "Iron Helmet",
            "Golden Helmet",
            "Diamond Helmet",
            "Chestplates",
            "Leather Chestplate",
            "Chainmail Chestplate",
            "Iron Chestplate",
            "Golden Chestplate",
            "Diamond Chestplate",
            "Leggings",
            "Leather Leggings",
            "Chainmail Leggings",
            "Iron Leggings",
            "Golden Leggings",
            "Diamond Leggings",
            "Boots",
            "Leather Boots",
            "Chainmail Boots",
            "Iron Boots",
            "Golden Boots",
            "Diamond Boots"
        );
        section(QuickBuy.class, "Purchase Timing", "Purchase Delay");
        section(QuickBuy.class, "Debug", "Debug");
        section(
            QuickBuy.class,
            "Purchases",
            "Weapons",
            "Stone Sword Key",
            "Stone Sword Slot",
            "Stone Sword Turbo",
            "Iron Sword Key",
            "Iron Sword Slot",
            "Iron Sword Turbo",
            "Diamond Sword Key",
            "Diamond Sword Slot",
            "Diamond Sword Turbo",
            "Knockback Stick Key",
            "Knockback Stick Slot",
            "Knockback Stick Turbo",
            "Tools",
            "Pickaxe Key",
            "Pickaxe Slot",
            "Pickaxe Turbo",
            "Axe Key",
            "Axe Slot",
            "Axe Turbo",
            "Shears Key",
            "Shears Slot",
            "Shears Turbo",
            "Armor",
            "Chainmail Armor Key",
            "Chainmail Armor Slot",
            "Chainmail Armor Turbo",
            "Iron Armor Key",
            "Iron Armor Slot",
            "Iron Armor Turbo",
            "Diamond Armor Key",
            "Diamond Armor Slot",
            "Diamond Armor Turbo",
            "Blocks",
            "Wool Key",
            "Wool Slot",
            "Wool Turbo",
            "Consumables",
            "Golden Apple Key",
            "Golden Apple Slot",
            "Golden Apple Turbo",
            "Fireball Key",
            "Fireball Slot",
            "Fireball Turbo",
            "TNT Key",
            "TNT Slot",
            "TNT Turbo",
            "Ender Pearl Key",
            "Ender Pearl Slot",
            "Ender Pearl Turbo",
            "Arrows Key",
            "Arrows Slot",
            "Arrows Turbo",
            "Upgrades",
            "Sharpness Key",
            "Protection Key",
            "Mining Fatigue Key",
            "Haste Key",
            "Feather Falling Key"
        );
        section(Animations.class, "Animation Modes", "Animations", "Block Animation", "All Items", "Block Only", "3rd Person");
        section(Animations.class, "Item Scale", "Custom Scale", "Item Scale", "Scale X", "Scale Y", "Scale Z");
        section(Animations.class, "Item Position", "Custom Position", "Position X", "Position Y", "Position Z");
        section(Animations.class, "Item Rotations", "Custom Rotation", "Rotation X", "Rotation Y", "Rotation Z");
        section(
            Animations.class,
            "Animation Speed",
            "Custom Speed",
            "Animation Speed",
            "Slow Attack",
            "Attack Speed",
            "Swing Duration",
            "Cancel Re-Equip",
            "Rotationless Drink"
        );
        section(Animations.class, "Spinning", "Spin X Axis", "Spin Y Axis", "Spin Z Axis", "Spin Speed");
        section(
            ArrayList.class,
            "Appearance",
            "HUD Style",
            "Exclude Combat",
            "Exclude Movement",
            "Exclude Player",
            "Exclude Visual",
            "Exclude Miscellaneous",
            "Exclude Exploit",
            "Exclude Scripts",
            "Exclude Pit",
            "Category Exclusion"
        );
        section(ArrayList.class, "Colors", "Color Mode", "Color 1", "Color 2", "Direction", "Wave Speed", "Color Theme", "Color Speed");
        section(
            ArrayList.class,
            "Background And Outline",
            "Prime BG Alpha",
            "Background",
            "Background Alpha",
            "Outline",
            "Outline Width",
            "Background Opacity",
            "Accent Bar",
            "Accent Bar Mode"
        );
        section(ArrayList.class, "Animations", "Animation Mode", "Animation Speed", "Smooth Animations");
        section(
            ArrayList.class, "Text", "Font", "Font Size", "Custom Font", "Lowercase", "Show Suffix", "Suffix Mode", "Typeface", "Text Shadow", "Suffix Style"
        );
        section(
            ArrayList.class,
            "Layout",
            "Scale",
            "Spacing",
            "X Offset",
            "Y Offset",
            "Horizontal Position",
            "Vertical Position",
            "X Padding",
            "Y Padding",
            "Row Spacing"
        );
        section(ArrayList.class, "Text Glow", "Text Glow", "Glow Intensity", "Glow Alpha");
        section(BedESP.class, "Colors", "Color Mode", "Color", "Color 2", "Gradient Speed", "Exposed Outline Color");
        section(BedESP.class, "Scanning", "Range", "Scan Speed");
        section(BedESP.class, "Bed Rendering", "Only Render First Bed", "Render Full Block", "Exposed Outline", "Show Own Bed");
        section(
            BedESP.class,
            "Defense Labels",
            "Show Defense Layers",
            "Show Break Tools",
            "Show Defense Counts",
            "Defense Height",
            "Defense Scale",
            "Defense Auto Scale"
        );
        section(
            BlockOverlay.class,
            "Overlay",
            "Outline",
            "Filled",
            "Sync Color",
            "Color Mode",
            "Custom Color",
            "Color Settings",
            "Rendering Options",
            "Through Walls",
            "Outline Width",
            "Rainbow Settings",
            "Enable",
            "Rainbow Speed",
            "Saturation Cycling",
            "Overlay Modes",
            "Standard",
            "Animated",
            "Pulsing",
            "Breathing",
            "Animation Settings",
            "Animation Speed",
            "Pulse Style",
            "Smooth Animation"
        );
        section(Chat.class, "Animations", "Animations", "Smooth Chat");
        section(Chat.class, "Layout", "Chat Scale", "Chat Width", "Chat Height", "Focused Height", "Chat X", "Chat Y", "Default X & Y");
        section(Chat.class, "Appearance", "Chat Opacity", "Background Opacity", "Chat Shadow", "Custom Font", "Font");
        section(Chat.class, "Chat Behavior", "Infinite Chat");
        section(
            Chat.class,
            "Mention Highlighting",
            "Highlight Mentions",
            "Mention Style",
            "Mention Red",
            "Mention Green",
            "Mention Blue",
            "Mention Alpha",
            "Mention Sound",
            "Case Sensitive"
        );
        section(Scoreboard.class, "Appearance", "Background Alpha", "Alpha Value", "Custom Font", "Font");
        section(Scoreboard.class, "Layout", "Scale", "Alignment", "Y Offset", "Position X", "Position Y");
        section(Scoreboard.class, "Scoreboard Content", "Filter Lines", "Bottom Text", "Custom Text");
        section(Scoreboard.class, "Debug", "Debug Scoreboard");
        section(DamageTags.class, "Damage Display", "Style", "Show Healing", "Show Damage", "Mode");
        section(DamageTags.class, "Appearance", "Color", "Box", "Shadow");
        section(DamageTags.class, "Tag Position", "Scale", "Y Offset", "Depth");
        section(DamageTags.class, "Lifetime", "Duration");
        section(
            DamageTags.class,
            "New Damage Tags",
            "Length",
            "Tag scale",
            "Tag theme",
            "Show heal",
            "Show hurt",
            "Show invis",
            "Show mobs",
            "Traditional health color",
            "Health as hearts",
            "Heart symbol"
        );
        section(FOVView.class, "Geometry", "FOV Range", "View Distance", "Line Width");
        section(FOVView.class, "View Indicator", "Opacity", "Render Mode");
        section(FOVView.class, "Colors", "Color Mode", "Color", "Gradient Color", "Color Speed");
        section(FOVView.class, "Rotations", "Follow Yaw");
        section(FPSBooster.class, "Players", "Pit Spawn", "Spawn KOS Only", "Hide Far Away", "Distance", "Hide AFK", "AFK Distance", "AFK Threshold");
        section(FPSBooster.class, "Dropped Items", "Hide Items", "Item Distance", "Item Age");
        section(FPSBooster.class, "XP Orbs", "Hide XP", "XP Distance");
        section(FPSBooster.class, "Arrows", "Hide Arrows", "Arrow Time", "Hide Player Arrows");
        section(FPSBooster.class, "Mobs And Holograms", "Hide Animals", "Hide Mobs", "Mob Distance", "Hide Holograms");
        section(FPSBooster.class, "Particles", "Limit Particles", "Particle Limit", "Cull Offscreen Particles", "Limit Particle Distance", "Particle Distance");
        section(FPSBooster.class, "Custom Items", "Cache Custom Items", "Simple Distant Items", "Custom Item Distance");
        section(TelebowESP.class, "Other Players", "Detect Others", "Live Aim Preview");
        section(MindAssaultDamage.class, "Damage Display", "Best Damage Spot", "Show Damage on Player");
        section(HitVector.class, "Target Detection", "Range", "Players Only", "Through Walls");
        section(HitVector.class, "Circle", "Circle Size", "Circle Quality");
        section(HitVector.class, "Colors", "Color Mode", "Custom Color", "Theme");
        section(HitVector.class, "Animation", "Animate", "Animation Speed");
        section(HUD.class, "Text", "Custom Font", "Font", "Font Size");
        section(HUD.class, "Click Counter", "CPS Enabled", "Show Left CPS", "Show Right CPS", "CPS X", "CPS Y");
        section(HUD.class, "FPS Counter", "FPS Enabled", "FPS X", "FPS Y");
        section(HUD.class, "Server Address", "IP Enabled", "IP X", "IP Y");
        section(HUD.class, "Movement Speed", "BPS Enabled", "BPS X", "BPS Y");
        section(HUD.class, "Ping", "Ping Enabled", "Ping X", "Ping Y");
        section(InterfaceType.class, "Watermark", "Mode");
        section(
            InterfaceType.class, "Colors", "HUD Theme Color", "Watermark Color", "Follow Theme", "Color 1", "Color 2", "Color 3", "Chroma", "Gradient Speed"
        );
        section(InterfaceType.class, "Layout", "X", "Y", "Scale");
        section(InterfaceType.class, "Text", "Text", "Custom Font", "Font", "Font Size");
        section(NameTags.class, "Size", "Scale", "Auto Scale");
        section(
            NameTags.class,
            "Tag Content",
            "Show Pit Enchants",
            "Enchants Above",
            "Show Enchant Levels",
            "Show Distance",
            "Show Venomed Timer",
            "Show Held Item",
            "Targets",
            "Players",
            "Mobs",
            "Animals",
            "Bots",
            "Invisible",
            "Self",
            "Pit Enchants"
        );
        section(NameTags.class, "Background", "Background", "Opacity");
        section(PotionHUD.class, "Layout", "Position X", "Position Y", "Layout", "Scale");
        section(PotionHUD.class, "Appearance", "Style", "Background");
        section(
            PotionHUD.class,
            "Effect Display",
            "Show Icon",
            "Show Name",
            "Show Duration",
            "Show Amplifier",
            "Speed",
            "Slowness",
            "Haste",
            "Mining Fatigue",
            "Strength",
            "Jump Boost",
            "Nausea",
            "Regeneration",
            "Resistance",
            "Fire Resistance",
            "Water Breathing",
            "Invisibility",
            "Blindness",
            "Night Vision",
            "Hunger",
            "Weakness",
            "Poison",
            "Wither",
            "Health Boost",
            "Absorption",
            "Saturation",
            "Effect Filters"
        );
        section(PotionHUD.class, "Expiry Warning", "Blink Expiring", "Blink Threshold (s)");
        section(TargetESP.class, "Target Highlight", "Mode", "Multiple Target ESP", "Switch Target ESP", "Opacity");
        section(TargetESP.class, "Colors", "Color Mode", "Custom Color", "Custom Color 2", "Color Speed", "Hurt Color", "Normal Color");
        section(
            TargetHUD.class,
            "HUD Appearance",
            "Mode",
            "Color",
            "Myau Color",
            "Indicator",
            "Outline",
            "Background",
            "Head",
            "Show Armor",
            "Show W/L",
            "Animations",
            "Shadow"
        );
        section(TargetHUD.class, "Layout", "Position X", "Position Y", "Scale", "Offset X", "Offset Y");
        section(TargetHUD.class, "Visibility", "KillAura Only", "Chat Preview");
        section(
            Tracers.class,
            "Player Filters",
            "Show Invisible",
            "KOS Only",
            "Ignore Friends",
            "Show KOS",
            "Show Friends",
            "Show Dark Pants",
            "Show Neutral",
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
            "Golden Handcuffs",
            "Sync Theme",
            "Color",
            "Player Types",
            "Dark Detection"
        );
        section(XRay.class, "Tracked Blocks", "Diamond ore", "Gold ore", "Iron ore", "Coal ore", "Redstone ore", "Emerald ore", "Lapis ore", "Sugar cane");
        section(XRay.class, "Scanning", "Scan interval", "Scan range", "Exposure mode", "Marker cap");
        section(XRay.class, "ESP Rendering", "Box ESP", "Tracers", "Alpha");
        section(Blink.class, "Position Rendering", "Theme");
        section(BedAura.class, "Appearance", "Theme");
        section(ArrayList.class, "Colors", "Theme");
        section(BlockOverlay.class, "Appearance", "Theme");
        section(Chat.class, "Appearance", "Theme");
        section(Scoreboard.class, "Appearance", "Theme");
        section(FOVView.class, "Colors", "Theme");
        section(HUD.class, "Text", "Theme");
        section(TargetESP.class, "Colors", "Theme");
        section(PlayerList.class, "HUD Position", "X", "Y");
        section(LowLifeWarning.class, "HUD Position", "X", "Y");
    }
}
