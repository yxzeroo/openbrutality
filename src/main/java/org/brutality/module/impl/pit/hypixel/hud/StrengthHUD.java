package org.brutality.module.impl.pit.hypixel.hud;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.lwjgl.input.Mouse;

public class StrengthHUD extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting xSetting = new NumberSetting("X", this, 10.0, 0.0, 1920.0, 1.0);
    private final NumberSetting ySetting = new NumberSetting("Y", this, 96.0, 0.0, 1080.0, 1.0);
    private static final Pattern pattern = Pattern.compile("(?i)\\bStrength\\s*:?\\s*([IV]+)\\s*\\(\\s*(\\d+(?:\\.\\d+)?)\\s*s\\s*\\)");

    @Override
    public void onEvent(Event event) {
    }

    public StrengthHUD() {
        super("StrengthHUD", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.xSetting, this.ySetting});
    }

    private String[] computeStringArray() {
        if (this.mc.theWorld != null && this.mc.thePlayer != null) {
            Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
            ScoreObjective objective = scoreboard == null ? null : scoreboard.getObjectiveInDisplaySlot(1);
            if (objective == null) {
                return null;
            }
            for (Score score : scoreboard.getSortedScores(objective)) {
                String str = ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(score.getPlayerName()), score.getPlayerName());
                String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(str);
                if (textWithoutFormattingCodes != null) {
                    Matcher matcher = pattern.matcher(textWithoutFormattingCodes);
                    if (matcher.find()) {
                        return new String[]{matcher.group(1).toUpperCase(), matcher.group(2)};
                    }
                }
            }

            return null;
        }
        return null;
    }

    @Override
    public void onRender() {
        if (this.mc.thePlayer != null) {
            String[] parts = this.computeStringArray();
            if (parts == null) {
                if (!(this.mc.currentScreen instanceof GuiChat)) {
                    HudDragHelper.handleObject(this);
                    return;
                }

                parts = new String[]{"V", "5"};
            }

            String str = parts[0];
            String str2 = parts[1];
            EnumChatFormatting RED = EnumChatFormatting.RED;
            String str3 = str;
            String str4 = EnumChatFormatting.WHITE + "Strgth-" + str3 + ": " + RED + str2;
            float f = (float)this.xSetting.computeDouble();
            float f2 = (float)this.ySetting.computeDouble();
            if (this.mc.currentScreen instanceof GuiChat) {
                int[] ints = HudDragHelper.computeIntArray();
                if (Mouse.isButtonDown(0)
                    && !HudDragHelper.matchesObject(this)
                    && HudDragHelper.matchesInt(
                        ints[0],
                        ints[1],
                        f,
                        f2,
                        computeFontRenderer(StrengthHUD.class).getStringWidth(str4),
                        computeFontRenderer(StrengthHUD.class).FONT_HEIGHT
                    )) {
                    HudDragHelper.matchesObject(this, ints[0], ints[1], (int)f, (int)f2);
                }

                if (!Mouse.isButtonDown(0)) {
                    HudDragHelper.handleObject(this);
                }

                int[] ints2 = HudDragHelper.computeIntArray(this);
                if (ints2 != null) {
                    this.xSetting.handleDouble2(ints2[0]);
                    this.ySetting.handleDouble2(ints2[1]);
                    f = ints2[0];
                    f2 = ints2[1];
                }
            } else {
                HudDragHelper.handleObject(this);
            }

            computeFontRenderer(StrengthHUD.class).drawStringWithShadow(str4, f, f2, -1);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        HudDragHelper.handleObject(this);
    }
}
