package org.brutality.module.impl.pit.hypixel.utility;

import java.util.Arrays;
import java.util.Locale;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.core.ConnectToServerHandler;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.timer.TimerUtil;

public class SafetyMeasures extends Module {
    private static SafetyMeasures instance;
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private final BooleanSetting combatDetectorSetting = new BooleanSetting("Combat Detector", this, true);
    private final BooleanSetting blockChatWhileFightingSetting = new BooleanSetting("Block Chat While Fighting", this, true);
    private final BooleanSetting blockDisconnectWhileFightingSetting = new BooleanSetting("Block Disconnect While Fighting", this, true);
    private final BooleanSetting autoPLeaveSetting = new BooleanSetting("Auto /p leave", this, false);
    private final BooleanSetting showWarningTitleSetting = new BooleanSetting("Show Warning Title", this, true);
    private final ModeSetting solidSetting = new ModeSetting("Title Style", this, "Flashing", Arrays.asList("Flashing", "Solid"));
    private final BooleanSetting spirePerkReminderSetting = new BooleanSetting("Spire Perk Reminder", this, true);
    private boolean flag = false;
    private boolean flag2 = false;
    private final TimerUtil timerUtil = new TimerUtil();
    private boolean flag3 = true;
    private static final long timestamp = 800L;

    private static String stripColor(String str) {
        return str.replaceAll("§[0-9a-fklmnor]", "");
    }

    private void handleString(String str, String str2) {
        try {
            mc.ingameGUI.displayTitle(null, null, 10, 60, 20);
            mc.ingameGUI.displayTitle(str, null, -1, -1, -1);
            mc.ingameGUI.displayTitle(null, str2, -1, -1, -1);
        } catch (Exception ignored) {
        }
    }

    private boolean checkState2() {
        try {
            Scoreboard scoreboard = mc.theWorld.getScoreboard();
            if (scoreboard == null) {
                return false;
            }

            ScoreObjective objectiveInDisplaySlot = scoreboard.getObjectiveInDisplaySlot(1);
            if (objectiveInDisplaySlot == null) {
                return false;
            }

            for (Score score : scoreboard.getSortedScores(objectiveInDisplaySlot)) {
                String playerName = score.getPlayerName();
                if (!playerName.startsWith("#")) {
                    String str = stripColor(ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(playerName), playerName));
                    String str2 = str.trim().toLowerCase(Locale.ROOT);
                    if (str2.startsWith("status:")) {
                        str2 = str2.substring(7).trim();
                    }

                    if (str2.equals("fighting") || str2.startsWith("fighting ") || str2.startsWith("fighting!")) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    private void process3() {
        this.handleString(
            "" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "⚠ COMBAT DETECTED", EnumChatFormatting.YELLOW + "DON'T LEAVE — still fighting!"
        );
    }

    private boolean matchesString(String str) {
        if (str != null && !str.isEmpty()) {
            String str2 = str.toLowerCase().trim();
            return str2.startsWith("/hub")
                || str2.startsWith("/lobby")
                || str2.startsWith("/dc")
                || str2.startsWith("/disconnect")
                || str2.startsWith("/leave")
                || str2.startsWith("/housing ")
                || str2.startsWith("/play ")
                || str2.startsWith("/l ")
                || str2.equals("/l")
                || str2.startsWith("/quit");
        }
        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.thePlayer != null) {
            this.process();
        }

        this.flag = false;
        this.flag2 = false;
    }

    public static boolean checkState() {
        SafetyMeasures safetyMeasures = instance;
        if (safetyMeasures == null
            || !safetyMeasures.checkState44()
            || !safetyMeasures.combatDetectorSetting.isEnabled()
            || !safetyMeasures.blockDisconnectWhileFightingSetting.isEnabled()
            || mc.thePlayer == null
            || mc.theWorld == null
            || !ConnectToServerHandler.checkState2()) {
            return false;
        }
        if (!FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
            return false;
        }
        if (!safetyMeasures.checkState2()) {
            return false;
        }
        MessageUtils.x("§cDisconnect blocked while you are in combat.");
        return true;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (mc.thePlayer != null && mc.theWorld != null) {
                    if (event instanceof ChatEvent && this.spirePerkReminderSetting.isEnabled()) {
                        ChatEvent chatEvent = (ChatEvent)event;
                        String str = chatEvent.message != null ? chatEvent.message.getUnformattedText() : "";
                        String str2 = stripColor(str).toUpperCase();
                        if (str2.contains("PIT EVENT ENDED") && str2.contains("SPIRE")) {
                            MessageUtils.x(MessageUtils.computeString("⚠ SPIRE ENDED — remember to change your perks!", MessageUtils.text7));
                            this.handleString(
                                "" + EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + "SPIRE ENDED",
                                EnumChatFormatting.YELLOW + "Remember to change your perks!"
                            );
                            mc.thePlayer.playSound("random.orb", 1.0F, 1.0F);
                        }
                    }

                    if (event instanceof EventUpdate) {
                        if (!this.combatDetectorSetting.isEnabled()) {
                            return;
                        }

                        boolean flag = this.flag;
                        boolean flag2 = this.checkState2();
                        boolean flag3 = FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer);
                        this.flag = flag2 && flag3;
                        if (!flag && this.flag) {
                            this.flag2 = false;
                            MessageUtils.x(MessageUtils.computeString("⚠ COMBAT DETECTED IN SPAWN — DO NOT LEAVE!", MessageUtils.text13));
                            if (this.showWarningTitleSetting.isEnabled()) {
                                this.process3();
                            }

                            if (this.autoPLeaveSetting.isEnabled() && !this.flag2) {
                                mc.thePlayer.sendChatMessage("/p leave");
                                this.flag2 = true;
                            }
                        }

                        if (flag && !this.flag) {
                            if (!flag2) {
                                MessageUtils.x(MessageUtils.computeString("✔ Combat cleared — safe to leave.", MessageUtils.text11));
                            }

                            this.process();
                        }

                        if (this.flag
                            && this.showWarningTitleSetting.isEnabled()
                            && this.solidSetting.matchesString2("Flashing")
                            && this.timerUtil.matchesLong4(timestamp)) {
                            this.timerUtil.reset();
                            this.flag3 = !this.flag3;
                            if (this.flag3) {
                                this.process3();
                            } else {
                                this.process();
                            }
                        }
                    }

                    if (event instanceof EventPacketSend
                        && this.blockChatWhileFightingSetting.isEnabled()
                        && this.flag
                        && FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                        EventPacketSend event2 = (EventPacketSend)event;
                        if (event2.getPacket() instanceof C01PacketChatMessage) {
                            C01PacketChatMessage packet = (C01PacketChatMessage)event2.getPacket();
                            String message = packet.getMessage();
                            if (this.matchesString(message)) {
                                event2.setCancelled(true);
                                MessageUtils.x(
                                    MessageUtils.computeString("⚠ Blocked: ", MessageUtils.text13)
                                        + MessageUtils.computeString(message, MessageUtils.text7)
                                        + MessageUtils.computeString(" — still in combat!", MessageUtils.text13)
                                );
                            }
                        }
                    }
                }
            }
        }
    }

    public SafetyMeasures() {
        super("Safety Measures", 0, Category.PIT);
        instance = this;
        this.handleSettingArray(
            new Setting[]{
                this.combatDetectorSetting,
                this.blockChatWhileFightingSetting,
                this.blockDisconnectWhileFightingSetting,
                this.autoPLeaveSetting,
                this.showWarningTitleSetting,
                this.solidSetting,
                this.spirePerkReminderSetting
            }
        );
    }

    private void process() {
        try {
            mc.ingameGUI.displayTitle(null, null, -1, -1, -1);
        } catch (Exception ignored) {
        }
    }
}
