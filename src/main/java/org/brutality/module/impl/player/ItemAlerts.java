package org.brutality.module.impl.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.scoreboard.Scoreboard;
import org.brutality.event.Event;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;

public final class ItemAlerts extends Module {
    private final BooleanSetting chatAlertsSetting = new BooleanSetting("Chat Alerts", this, true);
    private final BooleanSetting hudAlertsSetting = new BooleanSetting("HUD Alerts", this, true);
    private final NumberSetting hudAlertsDurationSetting = new NumberSetting("HUD Alerts Duration", this, 5.0, 0.0, 10.0, 1.0);
    private final BooleanSetting sharpnessSetting = new BooleanSetting("Sharpness", this, true);
    private final BooleanSetting protectionSetting = new BooleanSetting("Protection", this, true);
    private final BooleanSetting showTeammatesSetting = new BooleanSetting("Show Teammates", this, false);
    private final BooleanSetting showDistanceSetting = new BooleanSetting("Show Distance", this, true);
    private final NumberSetting delaySSetting = new NumberSetting("Delay (s)", this, 15.0, 0.0, 60.0, 1.0);
    private final BooleanSetting debugSetting = new BooleanSetting("Debug", this, false);
    private final BooleanSetting chainmailArmorSetting = new BooleanSetting("Chainmail Armor", this, true);
    private final BooleanSetting ironArmorSetting = new BooleanSetting("Iron Armor", this, true);
    private final BooleanSetting diamondArmorSetting = new BooleanSetting("Diamond Armor", this, true);
    private final BooleanSetting ironSwordSetting = new BooleanSetting("Iron Sword", this, true);
    private final BooleanSetting diamondSwordSetting = new BooleanSetting("Diamond Sword", this, true);
    private final BooleanSetting diamondPickaxeSetting = new BooleanSetting("Diamond Pickaxe", this, true);
    private final BooleanSetting enderPearlSetting = new BooleanSetting("Ender Pearl", this, true);
    private final BooleanSetting bridgeEggSetting = new BooleanSetting("Bridge Egg", this, true);
    private final BooleanSetting fireballSetting = new BooleanSetting("Fireball", this, true);
    private final BooleanSetting bowSetting = new BooleanSetting("Bow", this, true);
    private final BooleanSetting obsidianSetting = new BooleanSetting("Obsidian", this, true);
    private final BooleanSetting tntSetting = new BooleanSetting("TNT", this, true);
    private final BooleanSetting blockZapperSetting = new BooleanSetting("Block Zapper", this, true);
    private final BooleanSetting speedPotionSetting = new BooleanSetting("Speed Potion", this, true);
    private final BooleanSetting jumpBoostPotionSetting = new BooleanSetting("Jump Boost Potion", this, true);
    private final BooleanSetting invisibilityPotionSetting = new BooleanSetting("Invisibility Potion", this, true);
    private final BooleanSetting ironGolemSetting = new BooleanSetting("Iron Golem", this, true);
    private final BooleanSetting machineGunBowSetting = new BooleanSetting("Machine Gun Bow", this, true);
    private final BooleanSetting charlieTheUnicornSetting = new BooleanSetting("Charlie the Unicorn", this, true);
    private final BooleanSetting iceBridgeSetting = new BooleanSetting("Ice Bridge", this, true);
    private final BooleanSetting sleepingDustSetting = new BooleanSetting("Sleeping Dust", this, true);
    private final BooleanSetting unstableTeleportationDeviceSetting = new BooleanSetting("Unstable Teleportation Device", this, true);
    private final BooleanSetting devastatorBowSetting = new BooleanSetting("Devastator Bow", this, true);
    private final BooleanSetting miracleOfTheStarsSetting = new BooleanSetting("Miracle of the Stars", this, true);
    private final BooleanSetting mysticMirrorSetting = new BooleanSetting("Mystic Mirror", this, true);
    public static ItemAlerts instance;
    private final Map<String, Map<String, Object>> lookupMap = new HashMap<>();
    private final HashSet<String> itemSet = new HashSet<>();
    private final Map<String, String> lookupMap2 = new HashMap<>();
    private final HashSet<String> itemSet2 = new HashSet<>();
    private final HashSet<String> itemSet3 = new HashSet<>();
    private final HashSet<String> itemSet4 = new HashSet<>();
    private final List<Map<String, Object>> entries = new ArrayList<>();
    private String text = "";
    private String text2 = "";
    private final String[] stringArray = new String[]{"c", "9", "a", "e", "b", "f", "d", "8"};
    private int status = -1;
    private int count;
    private int tickCounter;
    private static final long timestamp = 6537343534826011288L;

    @Override
    public void onDisable() {
        super.onDisable();
        this.lookupMap.clear();
        this.itemSet.clear();
        this.entries.clear();
    }

    private void process3() {
        this.itemSet3.clear();
        this.itemSet4.clear();
        this.itemSet2.clear();
        if (this.ironSwordSetting.isEnabled()) {
            this.itemSet3.add("iron_sword");
        }

        if (this.diamondSwordSetting.isEnabled()) {
            this.itemSet3.add("diamond_sword");
        }

        if (this.diamondPickaxeSetting.isEnabled()) {
            this.itemSet3.add("diamond_pickaxe");
        }

        if (this.enderPearlSetting.isEnabled()) {
            this.itemSet3.add("ender_pearl");
        }

        if (this.bridgeEggSetting.isEnabled()) {
            this.itemSet3.add("egg");
        }

        if (this.fireballSetting.isEnabled()) {
            this.itemSet3.add("fire_charge");
        }

        if (this.tntSetting.isEnabled()) {
            this.itemSet3.add("tnt");
        }

        if (this.blockZapperSetting.isEnabled()) {
            this.itemSet3.add("prismarine_shard");
        }

        if (this.obsidianSetting.isEnabled()) {
            this.itemSet3.add("obsidian");
        }

        if (this.bowSetting.isEnabled()) {
            this.itemSet4.add("Bow");
        }

        if (this.speedPotionSetting.isEnabled()) {
            this.itemSet4.add("Speed II Potion (45 seconds)");
        }

        if (this.jumpBoostPotionSetting.isEnabled()) {
            this.itemSet4.add("Jump V Potion (45 seconds)");
        }

        if (this.invisibilityPotionSetting.isEnabled()) {
            this.itemSet4.add("Invisibility Potion (30 seconds)");
        }

        if (this.ironGolemSetting.isEnabled()) {
            this.itemSet4.add("§cDream Defender");
        }

        if (this.machineGunBowSetting.isEnabled()) {
            this.itemSet4.add("Machine Gun Bow");
        }

        if (this.charlieTheUnicornSetting.isEnabled()) {
            this.itemSet4.add("Charlie the Unicorn");
        }

        if (this.iceBridgeSetting.isEnabled()) {
            this.itemSet4.add("Ice Bridge");
        }

        if (this.sleepingDustSetting.isEnabled()) {
            this.itemSet4.add("Sleeping Dust");
        }

        if (this.unstableTeleportationDeviceSetting.isEnabled()) {
            this.itemSet4.add("Unstable Teleportation Device");
        }

        if (this.devastatorBowSetting.isEnabled()) {
            this.itemSet4.add("Devastator Bow");
        }

        if (this.miracleOfTheStarsSetting.isEnabled()) {
            this.itemSet4.add("Miracle of the Stars");
        }

        if (this.mysticMirrorSetting.isEnabled()) {
            this.itemSet4.add("Mystic Mirror");
        }

        if (this.chainmailArmorSetting.isEnabled()) {
            this.itemSet2.add("chainmail_leggings");
        }

        if (this.ironArmorSetting.isEnabled()) {
            this.itemSet2.add("iron_leggings");
        }

        if (this.diamondArmorSetting.isEnabled()) {
            this.itemSet2.add("diamond_leggings");
        }
    }

    private void onEventPacketSend(EventPacketSend event) {
        if (this.debugSetting.isEnabled()) {
            ;
        }
    }

    public ItemAlerts() {
        super("ItemAlerts", 0, Category.PLAYER);
        this.count = (int)timestamp;
        this.tickCounter = 0;
        this.handleSettingArray(
            new Setting[]{
                this.chatAlertsSetting,
                this.hudAlertsSetting,
                this.hudAlertsDurationSetting,
                this.sharpnessSetting,
                this.protectionSetting,
                this.showTeammatesSetting,
                this.showDistanceSetting,
                this.delaySSetting,
                this.debugSetting,
                this.chainmailArmorSetting,
                this.ironArmorSetting,
                this.diamondArmorSetting,
                this.ironSwordSetting,
                this.diamondSwordSetting,
                this.diamondPickaxeSetting,
                this.enderPearlSetting,
                this.bridgeEggSetting,
                this.fireballSetting,
                this.bowSetting,
                this.obsidianSetting,
                this.tntSetting,
                this.blockZapperSetting,
                this.speedPotionSetting,
                this.jumpBoostPotionSetting,
                this.invisibilityPotionSetting,
                this.ironGolemSetting,
                this.machineGunBowSetting,
                this.charlieTheUnicornSetting,
                this.iceBridgeSetting,
                this.sleepingDustSetting,
                this.unstableTeleportationDeviceSetting,
                this.devastatorBowSetting,
                this.miracleOfTheStarsSetting,
                this.mysticMirrorSetting
            }
        );
        instance = this;
        this.process4();
    }

    private void handleString(String str, String str2, int n, String str3) {
        HashMap map = new HashMap();
        map.put("title", str);
        map.put("message", str2);
        map.put("duration", n);
        map.put("command", str3);
        this.entries.add(map);
    }

    private void onEventPacketReceive(EventPacketReceive event) {
        if (event.getPacket() instanceof S04PacketEntityEquipment) {
            S04PacketEntityEquipment packet = (S04PacketEntityEquipment)event.getPacket();
            if (mc.theWorld.getEntityByID(packet.getEntityID()) instanceof EntityPlayer player) {
                this.handleEntityPlayer(player, packet.getItemStack(), packet.getEquipmentSlot());
            }
        }
    }

    private void process() {
        if (this.status == 3 && !mc.thePlayer.capabilities.allowFlying) {
            String text = mc.thePlayer.getDisplayName().getUnformattedText();

            for (String str : this.stringArray) {
                if (text.startsWith("§" + str)) {
                    this.text2 = str;
                    break;
                }
            }
        }
    }

    private int computeInt() {
        if (mc.theWorld == null) {
            return -1;
        }
        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        return scoreboard == null ? -1 : 3;
    }

    public void onTick() {
        this.tickCounter++;
        if (mc.thePlayer != null && this.tickCounter % 100 == 0) {
            this.status = this.computeInt();
            this.text = mc.thePlayer.getName();
            this.process();
            this.process3();
            this.count = (int)(this.delaySSetting.computeDouble() * 1000.0);
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventPacketReceive) {
                this.onEventPacketReceive((EventPacketReceive)event);
            } else if (event instanceof EventPacketSend) {
                this.onEventPacketSend((EventPacketSend)event);
            } else if (event instanceof EventTick) {
                this.onTick();
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.lookupMap.clear();
        this.itemSet.clear();
        this.process3();
        if (mc.theWorld != null) {
            this.status = this.computeInt();
        }
    }

    private void process4() {
        this.lookupMap2.put("chainmail_leggings", "§fChainmail Armor");
        this.lookupMap2.put("iron_leggings", "§fIron Armor");
        this.lookupMap2.put("diamond_leggings", "§bDiamond Armor");
        this.lookupMap2.put("iron_sword", "§fIron Sword");
        this.lookupMap2.put("diamond_sword", "§bDiamond Sword");
        this.lookupMap2.put("diamond_pickaxe", "§bDiamond Pickaxe");
        this.lookupMap2.put("ender_pearl", "§3Ender Pearl");
        this.lookupMap2.put("egg", "§eBridge Egg");
        this.lookupMap2.put("fire_charge", "§6Fireball");
        this.lookupMap2.put("Bow", "§2Bow");
        this.lookupMap2.put("obsidian", "§5Obsidian");
        this.lookupMap2.put("tnt", "§cT§fN§cT");
        this.lookupMap2.put("prismarine_shard", "§3Block Zapper");
        this.lookupMap2.put("Speed II Potion (45 seconds)", "§bSpeed Potion");
        this.lookupMap2.put("Jump V Potion (45 seconds)", "§aJump Boost Potion");
        this.lookupMap2.put("Invisibility Potion (30 seconds)", "§fInvisibility Potion");
        this.lookupMap2.put("§cDream Defender", "§fIron Golem");
        this.lookupMap2.put("Machine Gun Bow", "§4Machine Gun Bow");
        this.lookupMap2.put("Charlie the Unicorn", "§dCharlie the Unicorn");
        this.lookupMap2.put("Ice Bridge", "§bIce Bridge");
        this.lookupMap2.put("Sleeping Dust", "§cSleeping Dust");
        this.lookupMap2.put("Unstable Teleportation Device", "§eUnstable Teleportation Device");
        this.lookupMap2.put("Devastator Bow", "§2Devastator Bow");
        this.lookupMap2.put("Miracle of the Stars", "§eMiracle of the Stars");
        this.lookupMap2.put("Mystic Mirror", "§dMystic Mirror");
    }

    private void handleEntityPlayer(EntityPlayer player, ItemStack stack, int n) {
        if (player != null && player != mc.thePlayer && this.status == 3) {
            if (!player.isDead) {
                long now = System.currentTimeMillis();
                String text = player.getDisplayName().getUnformattedText();
                if (this.showTeammatesSetting.isEnabled() || !text.startsWith("§" + this.text2)) {
                    String str = player.getUniqueID().toString();
                    String str2 = text.length() >= 2 ? text.substring(1, 2) : "";
                    String str3 = this.computeString(str2);
                    if (str3 != null) {
                        String str4 = stack == null ? "" : stack.getUnlocalizedName();
                        String str5 = stack != null && stack.hasDisplayName() ? stack.getDisplayName() : "";
                        Map orDefault = this.lookupMap.getOrDefault(str, new HashMap<>());
                        if (stack != null) {
                            boolean flag = this.itemSet3.contains(str4);
                            boolean flag2 = this.itemSet4.contains(str5);
                            boolean flag3 = str4.contains("sword");
                            boolean itemEnchanted = stack.isItemEnchanted();
                            if (n == 0 && (flag || flag2)) {
                                String str6 = orDefault.getOrDefault("lastitem", "").toString();
                                String str7 = flag ? str4 : str5;
                                long l = Long.parseLong(orDefault.getOrDefault(str7, "0").toString());
                                if (now > l && !str6.equals(str4)) {
                                    String str8 = "§" + str2 + player.getName();
                                    String str9 = this.lookupMap2.get(str7);
                                    if (str9 == null) {
                                        str9 = str7;
                                    }

                                    String str10 = "§eAlert: " + str8 + " §7is holding " + str9 + "§r§7";
                                    if (this.showDistanceSetting.isEnabled()) {
                                        double dist = mc.thePlayer.getDistanceToEntity(player);
                                        str10 = str10 + " §7(§d" + (int)dist + "m§7)";
                                    }

                                    String str11 = str8 + " §7has " + str9 + "§7.";
                                    if (this.hudAlertsSetting.isEnabled()) {
                                        this.handleString("§lItem Alerts", str11, (int)(this.hudAlertsDurationSetting.computeDouble() * 1000.0), "");
                                    }

                                    if (this.chatAlertsSetting.isEnabled()) {
                                        MessageUtils.x(str10);
                                    }

                                    orDefault.put(str7, now + this.count);
                                }
                            }

                            if (itemEnchanted && (n == 2 || n == 0 && flag3)) {
                                String str12 = n == 2 ? "protection" + str2 : "sharpness" + str2;
                                String str13 = n == 2 ? "Reinforced Armor" : "Sharpened Swords";
                                String str14 = "§b" + str13;
                                if (!this.itemSet.contains(str12)) {
                                    this.itemSet.add(str12);
                                    String str15 = "§eAlert: " + str3 + " §7purchased " + str14;
                                    String str16 = str3 + " §7has " + str14 + "§7.";
                                    if (this.hudAlertsSetting.isEnabled()) {
                                        this.handleString("§lItem Alerts", str16, (int)(this.hudAlertsDurationSetting.computeDouble() * 1000.0), "");
                                    }

                                    if (this.chatAlertsSetting.isEnabled()) {
                                        MessageUtils.x(str15);
                                    }
                                }
                            }

                            if (n == 2 && this.itemSet2.contains(str4)) {
                                String str17 = orDefault.getOrDefault("armorpiece", "").toString();
                                if (!str17.equals(str4)) {
                                    String str18 = "§" + str2 + player.getName();
                                    String str19 = this.lookupMap2.get(str4);
                                    if (str19 == null) {
                                        str19 = str4;
                                    }

                                    String str20 = "§eAlert: " + str18 + " §7purchased " + str19 + "§r§7";
                                    if (this.showDistanceSetting.isEnabled()) {
                                        double dist2 = mc.thePlayer.getDistanceToEntity(player);
                                        str20 = str20 + " §7(§d" + (int)dist2 + "m§7)";
                                    }

                                    String str21 = str18 + " §7has " + str19 + "§7.";
                                    if (this.hudAlertsSetting.isEnabled()) {
                                        this.handleString("§lItem Alerts", str21, (int)(this.hudAlertsDurationSetting.computeDouble() * 1000.0), "");
                                    }

                                    if (this.chatAlertsSetting.isEnabled()) {
                                        MessageUtils.x(str20);
                                    }
                                }

                                orDefault.put("armorpiece", str4);
                            }
                        }

                        if (n == 0) {
                            orDefault.put("lastitem", str4);
                        }

                        this.lookupMap.put(str, orDefault);
                    }
                }
            }
        }
    }

    @Generated
    public static ItemAlerts getInstance() {
        return instance;
    }

    private String computeString(String str) {
        switch (str) {
            case "c":
                return "§cRed Team§r";
            case "9":
                return "§9Blue Team§r";
            case "a":
                return "§aGreen Team§r";
            case "e":
                return "§eYellow Team§r";
            case "b":
                return "§bAqua Team§r";
            case "f":
                return "§fWhite Team§r";
            case "d":
                return "§dPink Team§r";
            case "8":
                return "§8Gray Team§r";
            default:
                return null;
        }
    }
}
