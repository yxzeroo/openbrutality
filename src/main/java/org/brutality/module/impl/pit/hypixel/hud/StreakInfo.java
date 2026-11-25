package org.brutality.module.impl.pit.hypixel.hud;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.util.ExecuteCommandHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.hypixel.pit.UTF8Helper;
import org.brutality.utils.network.ServerUtils;
import org.brutality.utils.render.FontResourceLoader;
import org.brutality.utils.render.IsChoiceHelper;
import org.brutality.utils.timer.TimerUtil;

public class StreakInfo extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final BooleanSetting customFontSetting = new BooleanSetting("Custom Font", this, true);
    private final ModeSetting fontSetting = IsChoiceHelper.computeModeSetting("Font", this).computeModeSetting(() -> this.customFontSetting.isEnabled());
    public Double[] doubleTypeArray = new Double[]{
        10000.0,
        20000.0,
        20000.0,
        20000.0,
        30000.0,
        35000.0,
        40000.0,
        45000.0,
        50000.0,
        60000.0,
        70000.0,
        80000.0,
        90000.0,
        100000.0,
        125000.0,
        150000.0,
        175000.0,
        200000.0,
        250000.0,
        300000.0,
        350000.0,
        400000.0,
        500000.0,
        600000.0,
        700000.0,
        800000.0,
        900000.0,
        1000000.0,
        1000000.0,
        1000000.0,
        1000000.0,
        1000000.0,
        1000000.0,
        1000000.0,
        1000000.0,
        2000000.0,
        2000000.0,
        2000000.0,
        2000000.0,
        2000000.0,
        2000000.0,
        2000000.0,
        2000000.0,
        2000000.0,
        2000000.0,
        2000000.0,
        2000000.0,
        2000000.0,
        2000000.0,
        2000000.0,
        0.0
    };
    public Double doubleValue = 0.0;
    public int prestige = 0;
    public boolean flag = false;
    public TimerUtil timerUtil = new TimerUtil();
    public DecimalFormat decimalFormat;
    public int count = 0;
    public int count2 = 0;
    public float factor = 0.0F;
    public float factor2 = 0.0F;
    public int count3 = 0;
    public String text = "";
    public int count4 = 0;
    public int status = 0;
    public TimerUtil timerUtil2 = new TimerUtil();
    private String text2 = "";
    private long timestamp = 0L;
    private static final long timestamp2 = 100L;
    public String text3 = "";
    public String text4 = "0.00";
    public String text5 = "";
    public String text6 = "";
    public String text7 = "";
    private static final int count5 = 10;

    @Override
    public void onDisable() {
        super.onDisable();
        this.count = 0;
        this.count2 = 0;
        this.factor = 0.0F;
        this.factor2 = 0.0F;
        this.count3 = 0;
        this.doubleValue = 0.0;
        this.status = 0;
        this.text = "";
        this.count4 = 0;
        this.text2 = "";
        this.timestamp = 0L;
        this.text3 = "";
        this.text4 = "0.00";
        this.text5 = "";
        this.text6 = "";
        this.text7 = "";
        this.timerUtil2.reset();
        this.timerUtil.reset();
    }

    public void handleInt(int prestige) {
        this.prestige = prestige;
        this.process3();
    }

    private Float computeFloatType2(String str) {
        try {
            if (str != null && str.length() >= 3) {
                String str2;
                if (str.contains("9(")) {
                    String[] parts = str.split("g");
                    if (parts.length <= 0) {
                        return null;
                    }

                    String str3 = parts[0];
                    if (str3.length() <= 2) {
                        return null;
                    }

                    str2 = str3.substring(2);
                } else {
                    if (str.length() <= 3) {
                        return null;
                    }

                    str2 = str.substring(2, str.length() - 1);
                }

                return this.decimalFormat.parse(str2).floatValue();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private Float computeFloatType3(String str) {
        try {
            Pattern pattern = Pattern.compile("\\+(\\d+(?:[.,]\\d+)?)g");
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                return this.decimalFormat.parse(matcher.group(1)).floatValue();
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private FontRenderer computeFontRenderer() {
        return (FontRenderer)(this.customFontSetting.isEnabled()
            ? FontResourceLoader.computeDrawStringWithShadowComponent2(this.fontSetting.getText(), 18.0F)
            : mc.fontRendererObj);
    }

    private void onChatEvent(ChatEvent chatEvent) {
        String text = chatEvent.message.getUnformattedText();
        if (!this.matchesString(text)) {
            try {
                boolean flag = ExecuteCommandHelper.matchesString(text);
                if (flag || text.contains("ASSIST!")) {
                    boolean flag2 = FreshHelper.matchesWorld2(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer);
                    if (this.status != 1 && !flag2) {
                        if (this.status == 0 || this.status == 2) {
                            this.process();
                        }

                        this.status = 1;
                    }

                    if (flag) {
                        this.count++;
                    }

                    if (text.contains("ASSIST!")) {
                        this.count2++;
                    }

                    if ("genesis".equals(FreshHelper.computeString(Minecraft.getMinecraft().theWorld))) {
                        this.handleString(text, chatEvent);
                    }

                    String[] parts = text.split("\\+");
                    if (parts.length >= 3) {
                        try {
                            String str = parts[1];
                            String str2 = parts[2];
                            if (str != null && str2 != null) {
                                Float val = this.computeFloatType2(str2);
                                Float val2 = this.computeFloatType(str);
                                if (val != null && val2 != null) {
                                    this.factor = this.factor + val2;
                                    this.factor2 = this.factor2 + val;
                                    this.doubleValue = this.doubleValue + val.floatValue();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (text.contains("SELF-CHECKOUT!")) {
                    if (text.contains("3,000g")) {
                        this.doubleValue = this.doubleValue + 3000.0;
                        this.factor2 += 3000.0F;
                    }

                    if (text.contains("5,000g")) {
                        this.doubleValue = this.doubleValue + 5000.0;
                        this.factor2 += 5000.0F;
                    }

                    if (text.contains("2,000g")) {
                        this.doubleValue = this.doubleValue + 2000.0;
                        this.factor2 += 2000.0F;
                    }
                }

                if (text.contains("DEATH!") && this.status == 1) {
                    this.status = 2;
                }

                if (text.contains("HIGHLANDER! Earned")) {
                    try {
                        int n = text.indexOf("+");
                        int n2 = text.indexOf("g ");
                        if (n != -1 && n2 != -1 && n2 > n) {
                            String str3 = text.substring(n + 1, n2);
                            Float val3 = this.decimalFormat.parse(str3).floatValue();
                            this.doubleValue = this.doubleValue + val3.floatValue();
                            this.factor2 = this.factor2 + val3;
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }

                if (text.contains("unlocked prestige") && text.contains(Minecraft.getMinecraft().thePlayer.getName())) {
                    this.doubleValue = 0.0;
                    this.prestige++;
                }

                this.process3();
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
    }

    private void handleScaledResolution(ScaledResolution sr) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontRenderer = this.computeFontRenderer();
        boolean flag = this.status == 1 || this.status == 2;
        boolean flag2 = mc.currentScreen instanceof GuiChat;
        if (flag || flag2) {
            StreakInfo.Entry entry = this.computeEntry(sr, fontRenderer);
            if (entry != null) {
                String[] parts = this.computeStringArray();
                String str = this.computeString();
                if (flag) {
                    int n = entry.count3 - (parts.length + 1) * fontRenderer.FONT_HEIGHT + 87;
                    int n2 = sr.getScaledWidth() - entry.count4 - 3;
                    int n3 = sr.getScaledWidth() - 3;
                    int n4 = (parts.length + 1) * fontRenderer.FONT_HEIGHT;
                    this.drawRect(n2 - 2, n, n3 + 2, n + n4, 1342177280);
                    int n5 = n2 + (n3 - n2) / 2 - fontRenderer.getStringWidth(str) / 2;
                    fontRenderer.drawString(str, n5, n + 1, -1);
                    int n6 = n + fontRenderer.FONT_HEIGHT;

                    for (String str2 : parts) {
                        if (!str2.isEmpty()) {
                            fontRenderer.drawString(str2, n2, n6, -1);
                        }

                        n6 += fontRenderer.FONT_HEIGHT;
                    }
                }
            }
        }
    }

    private boolean matchesString(String str) {
        long now = System.currentTimeMillis();
        if (str.equals(this.text2) && now - this.timestamp < 100L) {
            return true;
        }
        this.text2 = str;
        this.timestamp = now;
        return false;
    }

    private boolean isOnPit() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.theWorld == null ? false : FreshHelper.computeString(mc.theWorld) != null;
    }

    private Float computeFloatType4(String str) {
        try {
            Pattern pattern = Pattern.compile("\\+(\\d+(?:[.,]\\d+)?)\\s*XP");
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                return this.decimalFormat.parse(matcher.group(1)).floatValue();
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private String computeString() {
        if (this.status == 1) {
            return "Streak (" + EnumChatFormatting.GREEN + "Active" + EnumChatFormatting.WHITE + ")";
        }
        return this.status == 2 ? "Streak (" + EnumChatFormatting.YELLOW + "Last" + EnumChatFormatting.WHITE + ")" : "Streak";
    }

    private StreakInfo.Entry computeEntry(ScaledResolution sr, FontRenderer fontRenderer) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) {
            return null;
        }
        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        ScoreObjective objective = null;
        ScorePlayerTeam playersTeam = scoreboard.getPlayersTeam(mc.thePlayer.getName());
        if (playersTeam != null) {
            int colorIndex = playersTeam.getChatFormat().getColorIndex();
            if (colorIndex >= 0) {
                objective = scoreboard.getObjectiveInDisplaySlot(3 + colorIndex);
            }
        }

        if (objective == null) {
            objective = scoreboard.getObjectiveInDisplaySlot(1);
        }

        if (objective == null) {
            return null;
        }
        var sortedScores = scoreboard.getSortedScores(objective);
        java.util.List list = Lists.newArrayList(Iterables.filter(sortedScores, new Predicate<Score>() {
            public boolean apply(Score score) {
                return score.getPlayerName() != null && !score.getPlayerName().startsWith("#");
            }
        }));
        java.util.List list2;
        if (list.size() > 15) {
            list2 = Lists.newArrayList(Iterables.skip(list, sortedScores.size() - 15));
        } else {
            list2 = list;
        }

        ArrayList<Score> scores = (ArrayList<Score>)(Object)(Lists.newArrayList());

        for (Score score2 : ((Iterable<Score>)(Object)(list2))) {
            if (score2 != null && score2.getPlayerName() != null) {
                try {
                    ScorePlayerTeam playersTeam2 = scoreboard.getPlayersTeam(score2.getPlayerName());
                    String str = ScorePlayerTeam.formatPlayerName(playersTeam2, score2.getPlayerName());
                    if (!shouldRemoveLine(score2.getPlayerName()) && !shouldRemoveLine(str)) {
                        scores.add(score2);
                    }
                } catch (Exception e) {
                    scores.add(score2);
                }
            }
        }

        int textWidth = fontRenderer.getStringWidth(objective.getDisplayName());

        for (Score score3 : scores) {
            ScorePlayerTeam playersTeam3 = scoreboard.getPlayersTeam(score3.getPlayerName());
            String str2 = ScorePlayerTeam.formatPlayerName(playersTeam3, score3.getPlayerName());
            textWidth = Math.max(textWidth, fontRenderer.getStringWidth(str2));
        }

        String str3 = this.text3 != null && !this.text3.isEmpty() ? this.text3 : "1000";
        String str4 = this.text4 != null && !this.text4.isEmpty() ? this.text4 : "75";
        EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
        EnumChatFormatting GREEN = EnumChatFormatting.GREEN;
        EnumChatFormatting GRAY2 = EnumChatFormatting.GRAY;
        EnumChatFormatting GOLD = EnumChatFormatting.GOLD;
        String str5 = "Req: " + GOLD + str3 + "g" + GRAY2 + " (" + GREEN + str4 + "%" + GRAY + ")";
        textWidth = Math.max(textWidth, fontRenderer.getStringWidth(str5));
        int n = scores.size() + 1;
        int n2 = n * fontRenderer.FONT_HEIGHT;
        int n3 = sr.getScaledHeight() / 2 + n2 / 3;
        return new StreakInfo.Entry(sr.getScaledWidth() - 3, 0, n3, textWidth);
    }

    private Float computeFloatType(String str) {
        try {
            if (str != null && str.length() >= 5) {
                String str2 = str.substring(0, str.length() - 5);
                return this.decimalFormat.parse(str2).floatValue();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String[] computeStringArray() {
        if (this.status == 0 && Minecraft.getMinecraft().currentScreen instanceof GuiChat) {
            return new String[]{
                "",
                "Kills: " + EnumChatFormatting.GREEN + "0",
                "Assists: " + EnumChatFormatting.GREEN + "0",
                "",
                "Gold: " + EnumChatFormatting.GOLD + "0g",
                "XP: " + EnumChatFormatting.AQUA + "0",
                "",
                "Duration: " + EnumChatFormatting.YELLOW + "0s"
            };
        }
        String str = "";
        if (this.count3 > 0 && !this.checkState()) {
            int n = this.count3;
            String text = this.text;
            EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
            str = " " + GRAY + "(§" + text + n + "pts§7)";
        }

        String[] parts = new String[]{"", null, null, null, null, null, null, null};
        int count = this.count;
        EnumChatFormatting GREEN = EnumChatFormatting.GREEN;
        parts[1] = "Kills: " + GREEN + count + str;
        int n2 = this.count2;
        EnumChatFormatting GREEN2 = EnumChatFormatting.GREEN;
        parts[2] = "Assists: " + GREEN2 + n2;
        parts[3] = "";
        String str2 = this.text5;
        EnumChatFormatting GOLD = EnumChatFormatting.GOLD;
        parts[4] = "Gold: " + GOLD + str2 + "g";
        String str3 = this.text6;
        EnumChatFormatting AQUA = EnumChatFormatting.AQUA;
        parts[5] = "XP: " + AQUA + str3;
        parts[6] = "";
        String str4 = this.text7;
        EnumChatFormatting YELLOW = EnumChatFormatting.YELLOW;
        parts[7] = "Duration: " + YELLOW + str4;
        return parts;
    }

    private void drawRect(int n, int n2, int n3, int n4, int n5) {
        Gui.drawRect(n, n2, n3, n4, n5);
    }

    private void process() {
        this.count = 0;
        this.count2 = 0;
        this.factor = 0.0F;
        this.factor2 = 0.0F;
        this.count3 = 0;
        this.text = "";
        this.timerUtil2.reset();
        this.text7 = "0s";
        this.process3();
    }

    public void process4() {
        long l = this.timerUtil2.computeLong7();
        long l2 = l / 1000L;
        long l3 = l2 / 60L;
        long l4 = l3 / 60L;
        if (l4 % 24L > 0L) {
            long l5 = l4 % 24L;
            long l6 = l3 % 60L;
            long l7 = l2 % 60L;
            long l8 = l6;
            this.text7 = l5 + "h " + l8 + "m " + l7 + "s";
        } else if (l3 % 60L > 0L) {
            this.text7 = l3 % 60L + "m " + l2 % 60L + "s";
        } else {
            this.text7 = l2 % 60L + "s";
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (this.isOnPit() || this.checkState()) {
                if (event instanceof EventRender2D) {
                    if (!this.checkState44()) {
                        return;
                    }

                    this.handleScaledResolution(((EventRender2D)event).getScaledResolution());
                }

                if (event instanceof EventTick && ((EventTick)event).getState() == EventState.PRE && this.status == 1) {
                    this.count4++;
                    if (this.count4 == 20) {
                        this.count4 = 0;
                        this.process4();
                    }

                    Minecraft mc = Minecraft.getMinecraft();
                    if (mc.theWorld != null && mc.thePlayer != null && FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                        this.status = 2;
                    }
                }

                if (event instanceof ChatEvent) {
                    if (this.checkState()) {
                        this.onChatEvent2((ChatEvent)event);
                    } else {
                        this.onChatEvent((ChatEvent)event);
                    }
                }

                if (event instanceof RenderEvent4) {
                    if (this.status == 1) {
                        this.status = 2;
                    }

                    if (!this.checkState() && this.timerUtil.matchesLong4(60000L)) {
                        this.timerUtil.reset();
                        UTF8Helper utF8Helper = new UTF8Helper();
                        Thread thread = new Thread(utF8Helper);
                        thread.start();
                    }
                }
            }
        }
    }

    private void onChatEvent2(ChatEvent chatEvent) {
        String text = chatEvent.message.getUnformattedText();
        if (!this.matchesString(text)) {
            String str = text.replaceAll("§[0-9a-fk-or]", "");

            try {
                boolean flag = ExecuteCommandHelper.matchesString(str);
                boolean flag2 = str.contains("ASSIST!");
                if (flag || flag2) {
                    boolean flag3 = FreshHelper.matchesWorld2(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer);
                    if (this.status != 1 && !flag3) {
                        if (this.status == 0 || this.status == 2) {
                            this.process();
                        }

                        this.status = 1;
                    }

                    if (flag) {
                        this.count++;
                    }

                    if (flag2) {
                        this.count2++;
                    }

                    Float val = this.computeFloatType4(str);
                    Float val2 = this.computeFloatType3(str);
                    if (val != null) {
                        this.factor = this.factor + val;
                    }

                    if (val2 != null) {
                        this.factor2 = this.factor2 + val2;
                        this.doubleValue = this.doubleValue + val2.floatValue();
                    }

                    this.process3();
                }

                if (str.contains("SELF-CHECKOUT!")) {
                    if (str.contains("3,000g") || str.contains("3000g")) {
                        this.doubleValue = this.doubleValue + 3000.0;
                        this.factor2 += 3000.0F;
                    }

                    if (str.contains("5,000g") || str.contains("5000g")) {
                        this.doubleValue = this.doubleValue + 5000.0;
                        this.factor2 += 5000.0F;
                    }

                    if (str.contains("2,000g") || str.contains("2000g")) {
                        this.doubleValue = this.doubleValue + 2000.0;
                        this.factor2 += 2000.0F;
                    }

                    this.process3();
                }

                if (str.contains("DEATH!") && this.status == 1) {
                    this.status = 2;
                }

                if (str.contains("HIGHLANDER! Earned")) {
                    Float val3 = this.computeFloatType3(str);
                    if (val3 != null) {
                        this.doubleValue = this.doubleValue + val3.floatValue();
                        this.factor2 = this.factor2 + val3;
                        this.process3();
                    }
                }

                if (str.contains("unlocked prestige") && str.contains(Minecraft.getMinecraft().thePlayer.getName())) {
                    this.doubleValue = 0.0;
                    this.prestige++;
                    this.process3();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleString(String str, ChatEvent chatEvent) {
        try {
            Pattern pattern = Pattern.compile("\\+(\\d+)pts");
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                int n = Integer.parseInt(matcher.group(1));
                this.count3 += n;
                if (str.contains("KILL!") && !str.contains("ASSIST!")) {
                    this.text = "c";
                } else {
                    this.text = "b";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean shouldRemoveLine(String str) {
        if (str != null && !str.trim().isEmpty()) {
            String str2 = str.replaceAll("§.", "").trim();
            Pattern pattern = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{2,4}");
            Pattern pattern2 = Pattern.compile("\\b[A-Z]+\\d+[A-Z]*\\b");
            if (pattern.matcher(str2).find()) {
                return true;
            }
            return pattern2.matcher(str2).find() ? true : str2.matches("^\\d+$");
        }
        return false;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (!this.flag && !this.checkState()) {
            this.flag = true;
            UTF8Helper utF8Helper = new UTF8Helper();
            Thread thread = new Thread(utF8Helper);
            thread.start();
        }
    }

    public StreakInfo() {
        super("StreakInfo", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.customFontSetting, this.fontSetting});
        if (!this.checkState()) {
            UTF8Helper utF8Helper = new UTF8Helper();
            Thread thread = new Thread(utF8Helper);
            thread.start();
        }

        this.flag = true;
        this.decimalFormat = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("es-ES")));
    }

    public void handleDoubleType(Double doubleValue) {
        this.doubleValue = doubleValue;
        this.process3();
    }

    private void process3() {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0", new DecimalFormatSymbols(new Locale("es-ES")));
        DecimalFormat decimalFormat2 = new DecimalFormat("0");
        DecimalFormat decimalFormat3 = new DecimalFormat("#,##0", new DecimalFormatSymbols(new Locale("es-ES")));
        double d = this.doubleTypeArray[this.prestige] - this.doubleValue;
        this.text3 = decimalFormat.format(d > 0.0 ? d : 0.0);
        this.text4 = decimalFormat2.format(this.doubleValue / this.doubleTypeArray[this.prestige] * 100.0);
        this.text5 = decimalFormat3.format(this.factor2);
        this.text6 = Integer.toString(Math.round(this.factor));
    }

    private boolean checkState() {
        return ServerUtils.checkState2() || ServerUtils.ip() || ServerUtils.checkState5();
    }

    public static class Entry {
        public final int count;
        public final int count2;
        public final int count3;
        public final int count4;

        public Entry(int count, int n, int n2, int n3) {
            this.count = count;
            this.count2 = n;
            this.count3 = n2;
            this.count4 = n3;
        }
    }
}
