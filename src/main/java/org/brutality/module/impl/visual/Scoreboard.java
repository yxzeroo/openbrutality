package org.brutality.module.impl.visual;

import java.awt.Font;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.BasicEvent3;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.FontResourceLoader;
import org.brutality.utils.render.IsChoiceHelper;
import org.brutality.utils.render.ModuleSpecificHelper;

public class Scoreboard extends Module {
    private final BooleanSetting backgroundAlphaSetting = new BooleanSetting("Background Alpha", this, false);
    private final NumberSetting alphaValueSetting = new NumberSetting("Alpha Value", this, 50.0, 0.0, 100.0, 1.0);
    private final NumberSetting scaleSetting = new NumberSetting("Scale", this, 1.0, 0.5, 2.0, 0.1);
    private final ModeSetting rightSetting = new ModeSetting("Alignment", this, "Right", Arrays.asList("Left", "Center", "Right"));
    private final NumberSetting yOffsetSetting = new NumberSetting("Y Offset", this, 0.0, -500.0, 500.0, 1.0);
    private final BooleanSetting filterLinesSetting = new BooleanSetting("Filter Lines", this, true);
    private final ModeSetting noneSetting = new ModeSetting("Bottom Text", this, "None", Arrays.asList("Custom", "Brutality", "None"));
    private final StringSetting wwwHypixelNetSetting = new StringSetting("Custom Text", this, "www.hypixel.net");
    private final NumberSetting positionXSetting = new NumberSetting("Position X", this, 500.0, -1000.0, 2500.0, 20.0);
    private final NumberSetting positionYSetting = new NumberSetting("Position Y", this, 500.0, -1000.0, 2500.0, 20.0);
    private final ModeSetting modeSetting = ModuleSpecificHelper.computeModeSetting(this);
    private final BooleanSetting customFontSetting = new BooleanSetting("Custom Font", this, false);
    private final ModeSetting fontSetting = IsChoiceHelper.computeModeSetting("Font", this)
        .computeModeSetting(() -> this.customFontSetting.isEnabled() && !ModuleSpecificHelper.matchesModeSetting(this.modeSetting));
    private final BooleanSetting debugScoreboardSetting = new BooleanSetting("Debug Scoreboard", this, false);
    private static final Pattern pattern = Pattern.compile("[^\\x20-\\x7E]");
    private boolean flag;
    private long timestamp;
    private Object object;
    private CustomFontRenderer drawStringWithShadowComponent;
    private String text = "";
    private long lastUpdateTime = System.currentTimeMillis();
    private int count = 0;
    private int count2 = 0;
    private long timestamp2 = 0L;
    private boolean flag2 = false;
    private int count3 = 0;
    private static final String text2 = "brutality.lol";
    private static final EnumChatFormatting enumChatFormatting = EnumChatFormatting.DARK_RED;
    private static final EnumChatFormatting enumChatFormatting2 = EnumChatFormatting.RED;
    private static final EnumChatFormatting enumChatFormatting3 = EnumChatFormatting.GOLD;
    private static final int count4 = 150;
    private static final int count5 = 200;
    public static Scoreboard instance;

    public String computeString2() {
        if (this.noneSetting.matchesString2("Custom")) {
            return EnumChatFormatting.YELLOW + this.wwwHypixelNetSetting.getValue();
        }
        return this.noneSetting.matchesString2("Brutality") ? this.computeString5() : "";
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate && ((EventUpdate)event).getState() != EventState.POST) {
                if (!this.debugScoreboardSetting.isEnabled()) {
                    this.flag = false;
                } else if (mc.theWorld != null
                    && mc.thePlayer != null
                    && (!this.flag || this.object != mc.theWorld || System.currentTimeMillis() - this.timestamp >= 15000L)) {
                    this.flag = true;
                    this.object = mc.theWorld;
                    this.timestamp = System.currentTimeMillis();
                    MessageUtils.x("Scoreboard Debug (cleaned):");

                    for (String str : computeList(mc.theWorld.getScoreboard())) {
                        MessageUtils.x("Scoreboard Debug: " + str);
                    }
                }
            }

            if (event instanceof BasicEvent3 && mc.currentScreen instanceof GuiChat) {
                BasicEvent3 basicEvent3 = (BasicEvent3)event;
                if (basicEvent3.count6 == 0) {
                    int[] ints = HudDragHelper.computeIntArray();
                    int n = ints[0];
                    int n2 = ints[1];
                    if (basicEvent3.flag) {
                        HudDragHelper.matchesObject(this, n, n2, (int)this.getPosX(), (int)this.getPosY());
                    } else {
                        HudDragHelper.handleObject(this);
                    }
                }
            }

            if (HudDragHelper.matchesObject(this) && mc.currentScreen instanceof GuiChat) {
                int[] ints2 = HudDragHelper.computeIntArray(this);
                if (ints2 != null) {
                    this.handleDouble(ints2[0]);
                    this.handleDouble2(ints2[1]);
                }
            }
        }
    }

    public double getPosY() {
        return this.positionYSetting.computeDouble();
    }

    private static String computeString(String str) {
        return str == null ? "" : pattern.matcher(StringUtils.stripControlCodes(str)).replaceAll("").trim();
    }

    public boolean checkState() {
        if (this.checkState44() && this.customFontSetting.isEnabled()) {
            this.process();
            return this.drawStringWithShadowComponent != null;
        }
        return false;
    }

    public void handleDouble2(double d) {
        this.positionYSetting.handleDouble2(d);
    }

    public double computeDouble() {
        return this.scaleSetting.computeDouble();
    }

    public String computeString() {
        return this.noneSetting.getText();
    }

    public double getPosX() {
        return this.positionXSetting.computeDouble();
    }

    public boolean checkState2() {
        return this.backgroundAlphaSetting.isEnabled();
    }

    public boolean checkState4() {
        return !this.noneSetting.matchesString2("None");
    }

    public String computeString3() {
        return this.rightSetting.getText();
    }

    public CustomFontRenderer computeDrawStringWithShadowComponent() {
        this.process();
        return this.drawStringWithShadowComponent;
    }

    public void handleDouble(double d) {
        this.positionXSetting.handleDouble2(d);
    }

    private String computeString5() {
        long now = System.currentTimeMillis();
        if (this.flag2) {
            if (now - this.timestamp2 >= 200L) {
                this.count3++;
                if (this.count3 >= 4) {
                    this.flag2 = false;
                    this.count3 = 0;
                    this.count2 = 0;
                    this.count = 0;
                    this.lastUpdateTime = now;
                } else {
                    this.timestamp2 = now;
                }
            }

            EnumChatFormatting enumChatFormatting4 = this.count3 % 2 == 0 ? enumChatFormatting2 : enumChatFormatting;
            return enumChatFormatting4 + "brutality.lol";
        }
        if (now - this.lastUpdateTime >= 150L) {
            this.count++;
            if (this.count > "brutality.lol".length()) {
                this.count = 0;
                this.count2++;
                if (this.count2 >= 2) {
                    this.flag2 = true;
                    this.timestamp2 = now;
                    this.count3 = 0;
                    this.count2 = 0;
                    EnumChatFormatting enumChatFormatting5 = this.count3 % 2 == 0 ? enumChatFormatting2 : enumChatFormatting;
                    return enumChatFormatting5 + "brutality.lol";
                }
            }

            this.lastUpdateTime = now;
        }

        StringBuilder sb = new StringBuilder();
        EnumChatFormatting enumChatFormatting6;
        EnumChatFormatting enumChatFormatting7;
        if (this.count2 == 0) {
            enumChatFormatting6 = enumChatFormatting;
            enumChatFormatting7 = enumChatFormatting2;
        } else {
            enumChatFormatting6 = enumChatFormatting2;
            enumChatFormatting7 = enumChatFormatting;
        }

        for (int i = 0; i < "brutality.lol".length(); i++) {
            if (i == this.count && this.count < "brutality.lol".length()) {
                sb.append(enumChatFormatting3).append("brutality.lol".charAt(i));
            } else if (i < this.count) {
                sb.append(enumChatFormatting6).append("brutality.lol".charAt(i));
            } else {
                sb.append(enumChatFormatting7).append("brutality.lol".charAt(i));
            }
        }

        return sb.toString();
    }

    private static List<String> computeList(net.minecraft.scoreboard.Scoreboard scoreboard) {
        java.util.ArrayList list = new java.util.ArrayList();
        if (scoreboard == null) {
            return list;
        }
        ScoreObjective objectiveInDisplaySlot = scoreboard.getObjectiveInDisplaySlot(1);
        if (mc.thePlayer != null && mc.thePlayer.getTeam() instanceof ScorePlayerTeam) {
            int colorIndex = ((ScorePlayerTeam)mc.thePlayer.getTeam()).getChatFormat().getColorIndex();
            ScoreObjective objective = colorIndex < 0 ? null : scoreboard.getObjectiveInDisplaySlot(3 + colorIndex);
            if (objective != null) {
                objectiveInDisplaySlot = objective;
            }
        }

        if (objectiveInDisplaySlot == null) {
            list.add("No sidebar objective");
            return list;
        }
        list.add("Objective " + computeString(objectiveInDisplaySlot.getName()) + ": " + computeString(objectiveInDisplaySlot.getDisplayName()));
        java.util.ArrayList list2 = new java.util.ArrayList();

        for (Score score : scoreboard.getSortedScores(objectiveInDisplaySlot)) {
            if (score.getPlayerName() != null && !score.getPlayerName().startsWith("#")) {
                list2.add(score);
            }
        }

        for (int i = list2.size() - 1; i >= Math.max(0, list2.size() - 15); i--) {
            String playerName = ((Score)list2.get(i)).getPlayerName();
            String str = computeString(ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(playerName), playerName));
            if (!str.isEmpty()) {
                list.add(str);
            }
        }

        return list;
    }

    private void process() {
        if (!this.customFontSetting.isEnabled()) {
            this.drawStringWithShadowComponent = null;
            this.text = "";
        } else {
            String str;
            if (ModuleSpecificHelper.matchesModeSetting(this.modeSetting)) {
                BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
                str = brutalityHelper != null ? brutalityHelper.getText() : "Brutality";
            } else {
                str = this.fontSetting.getText();
            }

            float f = computeFloat();
            String str2 = str + "@" + f;
            if (this.drawStringWithShadowComponent == null || !str2.equalsIgnoreCase(this.text)) {
                this.text = str2;

                try {
                    this.drawStringWithShadowComponent = FontResourceLoader.computeDrawStringWithShadowComponent2(str, f);
                } catch (Exception e) {
                    try {
                        this.drawStringWithShadowComponent = new CustomFontRenderer(new Font("Arial", 0, 14), true, true);
                    } catch (Exception e2) {
                        this.drawStringWithShadowComponent = null;
                    }
                }
            }
        }
    }

    public static float computeFloat() {
        return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * 2.0F;
    }

    @Generated
    public static Scoreboard getInstance() {
        return instance;
    }

    public double getYOffset() {
        return this.yOffsetSetting.computeDouble();
    }

    public boolean checkState6() {
        return HudDragHelper.matchesObject(this);
    }

    public static int computeInt() {
        return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
    }

    public String computeString4() {
        if (this.noneSetting.matchesString2("Brutality")) {
            return "brutality.lol";
        }
        return this.noneSetting.matchesString2("Custom") ? this.wwwHypixelNetSetting.getValue() : "";
    }

    public double computeDouble2() {
        return this.alphaValueSetting.computeDouble();
    }

    public Scoreboard() {
        super("Scoreboard", 0, Category.VISUAL);
        this.handleSettingArray(
            new Setting[]{
                this.backgroundAlphaSetting,
                this.alphaValueSetting,
                this.scaleSetting,
                this.rightSetting,
                this.yOffsetSetting,
                this.filterLinesSetting,
                this.noneSetting,
                this.wwwHypixelNetSetting,
                this.positionXSetting,
                this.positionYSetting,
                this.modeSetting,
                this.customFontSetting,
                this.fontSetting,
                this.debugScoreboardSetting
            }
        );
        instance = this;
    }

    public boolean checkState5() {
        return this.filterLinesSetting.isEnabled();
    }
}
