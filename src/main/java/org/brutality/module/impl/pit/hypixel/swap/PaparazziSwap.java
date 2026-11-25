package org.brutality.module.impl.pit.hypixel.swap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.util.FloorHelper;
import org.brutality.util.PizzaType;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.player.ActiveHelper;
import org.brutality.utils.player.FindIdentityHelper;
import org.brutality.utils.player.StartHotbarMovesHelper;

public class PaparazziSwap extends Module {
    private final PitCategory pitCategory = PitCategory.SWAP;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Pattern pattern = Pattern.compile("Remaining:\\s*(\\d{1,2}):(\\d{2})", 2);
    private static final Pattern pattern2 = Pattern.compile("#([0-9]+)");
    private final ModeSetting silentSetting = new ModeSetting("Mode", this, "Normal", Arrays.asList("Normal", "Silent"));
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 200.0, 0.0, 500.0, 10.0);
    private final NumberSetting swapAtSSetting = new NumberSetting("Swap At (s)", this, 4.0, 1.0, 30.0, 1.0);
    private final BooleanSetting safeModeSetting = new BooleanSetting("Safe Mode", this, true);
    private final BooleanSetting alertsSetting = new BooleanSetting("Alerts", this, true);
    private final BooleanSetting autoEquipSetting = new BooleanSetting("Auto Equip", this, true);
    private final ModeSetting atLeast2RenownSetting = new ModeSetting(
        "Auto Equip When", this, "At Least 1 Renown", Arrays.asList("Always", "At Least 1 Renown", "At Least 2 Renown")
    );
    private final ActiveHelper activeHelper = new ActiveHelper();
    private boolean flag;
    private int count;
    private int count2 = -1;
    private PizzaType pizzaType;
    private int count3 = -1;

    public PaparazziSwap() {
        super("PaparazziSwap", 0, Category.PIT);
        this.handleSettingArray(
            new Setting[]{
                this.silentSetting,
                this.delaySetting,
                this.swapAtSSetting,
                this.safeModeSetting,
                this.alertsSetting,
                this.autoEquipSetting,
                this.atLeast2RenownSetting
            }
        );
    }

    private void reset() {
        this.flag = false;
        this.count = 0;
        this.count2 = -1;
    }

    public static int computeInt(PizzaType pizzaType, int n) {
        return pizzaType != null && n >= 1 ? FloorHelper.computeInt(pizzaType, n) : -1;
    }

    @Override
    public void onDisable() {
        this.activeHelper.process4();
    }

    @Override
    public void onEvent(Event event) {
        StartHotbarMovesHelper.onEvent3(event);
        if (event instanceof RenderEvent4) {
            this.activeHelper.clear();
            this.reset();
        } else if (event instanceof ChatEvent) {
            String text = ((ChatEvent)event).message.getUnformattedText();
            if (text.contains("PIT EVENT ENDED:")) {
                this.activeHelper.process4();
                this.reset();
            }
        } else if (event instanceof EventTick && ((EventTick)event).getState() == EventState.PRE && mc.thePlayer != null && mc.theWorld != null) {
            int n = this.computeInt();
            if (n >= 0 && this.pizzaType != null) {
                if (this.count2 >= 0 && n > this.count2 + 30) {
                    this.activeHelper.process4();
                    this.reset();
                }

                this.count2 = n;
                int n2 = FindIdentityHelper.computeInt(
                    stack -> FindIdentityHelper.matchesItemStack(stack) && FindIdentityHelper.matchesItemStack(stack, "paparazzi"), true
                );
                if (n2 >= 0 && !this.activeHelper.active()) {
                    int[] ints = new int[]{8, 6, 4};
                    boolean flag = false;

                    for (int i = 0; i < ints.length; i++) {
                        if (n <= ints[i] && (this.count & 1 << i) == 0) {
                            this.count |= 1 << i;
                            flag = true;
                        }
                    }

                    if (flag && this.alertsSetting.isEnabled()) {
                        MessageUtils.x("§6[PaparazziSwap] §fMajor ending in §e" + n + "§f seconds!");
                    }

                    if (this.autoEquipSetting.isEnabled() && !this.flag && !(n > this.swapAtSSetting.computeDouble()) && FindIdentityHelper.checkState()) {
                        int n3 = computeInt(this.pizzaType, this.count3);
                        if (this.atLeast2RenownSetting.matchesString2("Always")
                            || n3 >= (this.atLeast2RenownSetting.matchesString2("At Least 2 Renown") ? 2 : 1)) {
                            this.activeHelper
                                .handleList(
                                    Collections.singletonList(new StartHotbarMovesHelper.Entry3(2, n2)),
                                    Collections.emptyList(),
                                    this.silentSetting.matchesString2("Silent"),
                                    this.safeModeSetting.isEnabled(),
                                    (long)this.delaySetting.computeDouble()
                                );
                            this.flag = this.activeHelper.active();
                        }
                    }
                }
            } else {
                if (this.count2 >= 0) {
                    this.activeHelper.process4();
                    this.reset();
                }
            }
        }
    }

    private int computeInt() {
        this.pizzaType = null;
        this.count3 = -1;
        int n = -1;
        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        ScoreObjective objectiveInDisplaySlot = scoreboard.getObjectiveInDisplaySlot(1);
        if (objectiveInDisplaySlot == null) {
            return -1;
        }
        for (Score score : scoreboard.getSortedScores(objectiveInDisplaySlot)) {
            String playerName = score.getPlayerName();
            if (playerName != null && !playerName.startsWith("#")) {
                String str = ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(playerName), playerName).replaceAll("(?i)§[0-9A-FK-OR]", "");
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    n = Integer.parseInt(matcher.group(1)) * 60 + Integer.parseInt(matcher.group(2));
                }

                Matcher matcher2 = pattern2.matcher(str);
                if (matcher2.find()) {
                    this.count3 = Integer.parseInt(matcher2.group(1));
                }

                if (str.toLowerCase(Locale.ROOT).startsWith("event:")) {
                    String str2 = str.substring(str.indexOf(58) + 1).trim().replace(' ', '_').toUpperCase(Locale.ROOT);

                    try {
                        this.pizzaType = PizzaType.valueOf(str2);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        }

        return n;
    }
}
