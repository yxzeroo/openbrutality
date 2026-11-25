package org.brutality.module.impl.misc;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.RandomUtils;
import org.brutality.event.Event;
import org.brutality.event.impl.attack.GetDamageEvent;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.WHelper;
import org.brutality.utils.hypixel.pit.DOUBLEHelper;
import org.brutality.utils.performance.EntityCache;
import org.brutality.utils.timer.TimerUtil;

public class Insults extends Module {
    private final ModeSetting blocksmcSetting = new ModeSetting("Mode", this, "Hypixel", Arrays.asList("Hypixel Pit", "Hypixel", "Blocksmc"));
    private final ModeSetting yutesSetting = new ModeSetting(
        "Pit Group",
        this,
        "HoppaPit",
        Arrays.asList(
            "HoppaPit",
            "Korea Pit",
            "Regularity Pit",
            "#DARK",
            "GWAP GETTAHS",
            "Squire",
            "Mafia",
            "DPM",
            "VenomPit",
            "PG PIT",
            "Rxse Pit",
            "Japan Pit",
            "140ER ER ER",
            "Soge",
            "Smurf Pit",
            "XRP",
            "OWO",
            "United Pit",
            "Hydration",
            "RNG",
            "YUTES"
        )
    );
    private final BooleanSetting customMessageSetting = new BooleanSetting("Custom Message", this, false);
    private final StringSetting customTextSetting = new StringSetting("Custom Text", this, "");
    private final BooleanSetting includeAssistsSetting = new BooleanSetting("Include Assists", this, false)
        .computeBooleanSetting(() -> this.blocksmcSetting.matchesString2("Hypixel Pit"));
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 1000.0, 500.0, 5000.0, 1.0);
    private final BooleanSetting autoDelaySetting = new BooleanSetting("Auto Delay", this, true);
    private final StringSetting customPrefixSetting = new StringSetting("Custom Prefix", this, "");
    private final BooleanSetting kosOnlySetting = new BooleanSetting("KOS Only", this, false);
    private final BooleanSetting debugModeSetting = new BooleanSetting("Debug Mode", this, true);
    private List<EntityPlayer> entries = new CopyOnWriteArrayList<>();
    private final TimerUtil timerUtil = new TimerUtil();
    private boolean flag = false;
    private final String[] stringArray = new String[]{
        "Get BRUTALIZED! Buy Brutality at brutality.lol",
        "Another victim falls to Brutality! Get yours at brutality@.lol",
        "BRUTALITY just sent you to the shadow realm! brutality.lol",
        "You got demolished by Brutality - available at brutality.lol",
        "Brutality doesn't show mercy! Get it at brutality.lol",
        "Experience true BRUTALITY! Purchase at brutality.lol",
        "Brutality: Making legends and destroying noobs! brutality.lol",
        "You've been BRUTALIZED beyond recognition! brutality.lol",
        "Brutality just exposed your lack of skill! Get it at brutality.lol",
        "Welcome to the Brutality hall of shame! brutality.lol",
        "Brutality turns good players into gods! Available at brutality.lol",
        "Your defeat was brought to you by Brutality - brutality.lol",
        "Brutality doesn't just win, it obliterates! brutality.lol",
        "Get schooled by Brutality's superior technology! brutality.lol",
        "Brutality: Where champions are made! Get yours at brutality.lol",
        "You just got outplayed by Brutality! Available at brutality.lol",
        "Brutality just made you its personal punching bag! brutality.lol",
        "Embrace the BRUTALITY of defeat! Purchase at brutality.lol",
        "Brutality sent you straight to spectator mode! brutality.lol",
        "You couldn't handle the BRUTALITY! Get it at brutality.lol",
        "Brutality just ended your PvP career! brutality.lol",
        "Another kill secured by Brutality! Available at brutality.lol",
        "Brutality: The ultimate PvP client! Get yours at brutality.lol",
        "You got absolutely destroyed by Brutality! brutality.lol",
        "Brutality just gave you a free PvP lesson! brutality.lol",
        "Welcome to the Brutality experience! Purchase at brutality.lol",
        "Brutality doesn't miss! Get it at brutality.lol",
        "You've been officially BRUTALIZED! Available at brutality.lol",
        "Brutality: Dominating servers since day one! brutality.lol",
        "Get rekt by Brutality! Purchase your copy at brutality.lol"
    };

    private int computeInt2() {
        int n = this.computeInt();
        return (int)(n - MathHelper.clamp_float((float)(this.timerUtil.computeLong3() - this.timerUtil.getTimestamp()), 0.0F, n));
    }

    private void handleString(String str) {
        String str2 = this.stringArray[RandomUtils.nextInt(0, this.stringArray.length)];
        String str3 = str2.replace("%s", str);
        if (!this.customPrefixSetting.getValue().isEmpty()) {
            str3 = this.customPrefixSetting.getValue() + " " + str3;
        }

        this.debug("Sending killsult: " + str3);
        mc.thePlayer.sendChatMessage(str3);
    }

    private void process() {
        this.entries.clear();
        this.flag = false;
        this.debug("WorldEvent tick — targets refreshed (timer untouched)");
    }

    private void handleString(String str, String str2) {
        boolean flag = false;
        String str3 = null;
        if (str2.contains("was slain by " + mc.thePlayer.getName().toLowerCase())
            || str2.contains("was killed by " + mc.thePlayer.getName().toLowerCase())
            || str2.contains(mc.thePlayer.getName().toLowerCase() + " killed")
            || str2.contains("eliminated by " + mc.thePlayer.getName().toLowerCase())
            || str2.contains(mc.thePlayer.getName().toLowerCase() + " eliminated")) {
            flag = true;
            String[] parts = str2.split(" ");
            if (parts.length > 0) {
                str3 = parts[0];
            }

            this.debug("=== KILL message intercepted [Hypixel standard] ===");
            this.debug("  Message        : " + str);
            this.debug("  Extracted name : " + (str3 != null ? str3 : "<null>"));
        } else if (str.contains("by " + mc.thePlayer.getName()) && !str.contains("BOUNTY CLAIMED!")) {
            this.debug("=== KILL message intercepted [Hypixel generic 'by' pattern] ===");
            this.debug("  Message : " + str);
            Pattern pattern = Pattern.compile("\\b(killed|on)\\b\\s+\\[?\\d*\\]?\\s*([\\w]+?)(?:\\s+for|$)");
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                str3 = matcher.group(2).trim();
                flag = true;
                this.debug("  Extracted name : " + str3 + " (general pattern matched)");
            } else {
                this.debug("  Extracted name : <null — general pattern did not match>");
            }
        }

        if (flag && str3 != null) {
            this.debug("  KOS check : " + WHelper.matchesString(str3) + " (KOS Only: " + this.kosOnlySetting.isEnabled() + ")");
            if (!this.timerUtil.matchesInt(this.computeInt())) {
                this.debug("  RESULT: Timer not ready — skipping");
            } else if (!this.matchesString(str3)) {
                this.debug("  RESULT: Not insulting — player not on KOS list");
            } else {
                EntityPlayer player = this.computeEntityPlayer(str3);
                if (player != null) {
                    this.debug("  RESULT: Found victim in world — sending killsult to " + str3);
                    this.handleEntityPlayer(player);
                    this.entries.remove(player);
                } else {
                    this.debug("  RESULT: Victim not in world — sending name-only killsult for " + str3);
                    this.handleString(str3);
                }

                this.timerUtil.reset();
            }
        }
    }

    private void onChatEvent(ChatEvent chatEvent) {
        if (chatEvent.message != null) {
            String text = chatEvent.message.getUnformattedText();
            String str = text.toLowerCase();
            String text2 = this.blocksmcSetting.getText();
            switch (text2) {
                case "Hypixel Pit":
                    this.handleString3(text);
                    break;
                case "Hypixel":
                    this.handleString(text, str);
                    break;
                case "Blocksmc":
                    this.handleString4(text);
            }
        }
    }

    private void onGetPacketEvent(GetPacketEvent getPacketEvent) {
        if (this.blocksmcSetting.matchesString2("Hypixel")) {
            if (getPacketEvent.getPacket() instanceof S38PacketPlayerListItem) {
                S38PacketPlayerListItem packet = getPacketEvent.getPacket();
                if (packet.getAction() == S38PacketPlayerListItem.Action.REMOVE_PLAYER) {
                    for (S38PacketPlayerListItem.AddPlayerData addPlayerData : packet.getEntries()) {
                        if (addPlayerData.getProfile() != null) {
                            EntityPlayer playerEntityByUUID = mc.theWorld.getPlayerEntityByUUID(addPlayerData.getProfile().getId());
                            if (playerEntityByUUID != null && !playerEntityByUUID.equals(mc.thePlayer) && this.entries.contains(playerEntityByUUID)) {
                                this.debug("--- Tab-list removal detected ---");
                                this.debug("  Player : " + playerEntityByUUID.getName());
                                this.debug("  KOS    : " + WHelper.matchesString(playerEntityByUUID.getName()));
                                if (!this.timerUtil.matchesLong10(this.computeInt())) {
                                    this.debug("  RESULT: Timer not elapsed — skipping");
                                } else {
                                    if (this.matchesString(playerEntityByUUID.getName())) {
                                        this.debug("  RESULT: Sending killsult");
                                        this.handleEntityPlayer(playerEntityByUUID);
                                    } else {
                                        this.debug("  RESULT: Not on KOS list — skipping");
                                    }

                                    this.entries.remove(playerEntityByUUID);
                                    this.timerUtil.reset();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean matchesString(String str) {
        if (!this.kosOnlySetting.isEnabled()) {
            this.debug("KOS Only is OFF — will insult " + str);
            return true;
        }
        boolean flag = WHelper.matchesString(str);
        this.debug("KOS Only is ON — " + str + " in KOS list: " + flag);
        return flag;
    }

    private String computeString(String str) {
        String str2 = DOUBLEHelper.computeString2(str);
        this.debug("  [extract] Parsed Pit kill victim -> " + (str2 == null ? "<none>" : str2));
        return str2;
    }

    private void handleString2(String str) {
        String str2;
        if (this.customMessageSetting.isEnabled() && !this.customTextSetting.getValue().isEmpty()) {
            str2 = this.customTextSetting.getValue() + " " + str;
        } else {
            str2 = str + " #" + this.yutesSetting.getText();
        }

        if (!this.customPrefixSetting.getValue().isEmpty()) {
            str2 = this.customPrefixSetting.getValue() + " " + str2;
        }

        this.debug("Sending chat: " + str2);
        mc.thePlayer.sendChatMessage(str2);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventPreMotion) {
                    this.handlePreMotion();
                } else if (event instanceof GetDamageEvent) {
                    this.onGetDamageEvent((GetDamageEvent)event);
                } else if (event instanceof RenderEvent4) {
                    this.process();
                } else if (event instanceof GetPacketEvent) {
                    this.onGetPacketEvent((GetPacketEvent)event);
                } else if (event instanceof ChatEvent) {
                    this.onChatEvent((ChatEvent)event);
                }
            }
        }
    }

    private void onGetDamageEvent(GetDamageEvent getDamageEvent) {
        if (getDamageEvent.getEntity2() instanceof EntityPlayer player && !this.entries.contains(player)) {
            this.entries.add(player);
            this.debug("Added attack target: " + player.getName());
        }
    }

    private void handleString4(String str) {
        String str2 = str.replaceAll("§[0-9a-fk-or]", "");
        if (str2.toUpperCase().contains("KILL")) {
            this.debug("=== KILL message intercepted [Blocksmc — not yet implemented] ===");
            this.debug("  Raw   : " + str);
            this.debug("  Clean : " + str2);
            this.debug("  (implement pattern here once real message format is known)");
        }
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            this.entries.clear();
            this.timerUtil.reset();
            this.debug("Module enabled — targets cleared, timer reset");
        }
    }

    public Insults() {
        super("Insults", 0, Category.MISCELLANEOUS);
        this.handleSettingArray(
            new Setting[]{
                this.blocksmcSetting,
                this.yutesSetting,
                this.customMessageSetting,
                this.customTextSetting,
                this.includeAssistsSetting,
                this.delaySetting,
                this.autoDelaySetting,
                this.customPrefixSetting,
                this.kosOnlySetting,
                this.debugModeSetting
            }
        );
    }

    private EntityPlayer computeEntityPlayer(String str) {
        for (EntityPlayer player : EntityCache.getEntries()) {
            if (player.getName().equalsIgnoreCase(str)) {
                return player;
            }
        }

        return null;
    }

    private void handlePreMotion() {
        this.entries.removeIf(player -> !EntityCache.getEntries().contains(player));
    }

    @Override
    public void onDisable() {
        this.entries.clear();
        this.flag = false;
        this.debug("Module disabled");
    }

    private String computeString2(String str) {
        String str2 = DOUBLEHelper.computeString(str);
        this.debug("  [extract-assist] Parsed Pit assist victim -> " + (str2 == null ? "<none>" : str2));
        return str2;
    }

    private int computeInt() {
        if (!this.autoDelaySetting.isEnabled()) {
            return (int)this.delaySetting.computeDouble();
        }
        String text = mc.thePlayer.getDisplayName().getFormattedText();
        return !text.contains("VIP") && !text.contains("MVP") ? 3100 : 1100;
    }

    private void handleString3(String str) {
        String str2 = str.replaceAll("§[0-9a-fk-or]", "");
        boolean flag = str2.contains("KILL!");
        boolean flag2 = this.includeAssistsSetting.isEnabled() && str2.contains("ASSIST!");
        if (flag || flag2) {
            this.debug("=== " + (flag2 ? "ASSIST" : "KILL") + " message intercepted [Pit] ===");
            this.debug("  Raw     : " + str);
            this.debug("  Clean   : " + str2);
            String str3 = flag2 ? this.computeString2(str2) : this.computeString(str2);
            this.debug("  Extracted name : " + (str3 != null ? str3 : "<null — no pattern matched>"));
            if (str3 == null) {
                this.debug("  RESULT: Could not extract player name — skipping");
            } else {
                this.debug("  KOS check : " + WHelper.matchesString(str3) + " (KOS Only: " + this.kosOnlySetting.isEnabled() + ")");
                if (!this.timerUtil.matchesInt(this.computeInt())) {
                    this.debug("  RESULT: Timer not ready — skipping");
                } else if (!this.matchesString(str3)) {
                    this.debug("  RESULT: Not insulting — player not on KOS list");
                } else {
                    this.debug("  RESULT: Sending pit group message for " + str3);
                    this.handleString2(str3);
                    this.timerUtil.reset();
                }
            }
        }
    }

    private void handleEntityPlayer(EntityPlayer player) {
        this.handleString(player.getName());
    }

    private void debug(String str) {
        if (this.debugModeSetting.isEnabled()) {
            System.out.println("[Insults DEBUG] " + str);
        }
    }
}
