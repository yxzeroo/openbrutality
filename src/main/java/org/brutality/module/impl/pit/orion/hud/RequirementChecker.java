package org.brutality.module.impl.pit.orion.hud;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.GuiChat;
import org.brutality.event.Event;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.util.ExecuteCommandHelper;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.network.ServerUtils;
import org.lwjgl.input.Mouse;

public class RequirementChecker extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final NumberSetting xSetting = new NumberSetting("X", this, 10.0, 0.0, 1920.0, 1.0);
    private final NumberSetting ySetting = new NumberSetting("Y", this, 65.0, 0.0, 1080.0, 1.0);
    private static final Pattern pattern = Pattern.compile("GOLD REQUIREMENT!\\s*([0-9,.]+)g\\s*/\\s*([0-9,.]+)g", 2);
    private static final Pattern pattern2 = Pattern.compile("\\+([0-9]+(?:[.,][0-9]+)?)g");
    private static final long timestamp = 100L;
    private double currentGold;
    private double amount;
    private String text = "";
    private long timestamp2;

    private boolean matchesString2(String text) {
        long now = System.currentTimeMillis();
        if (text.equals(this.text) && now - this.timestamp2 < 100L) {
            return true;
        }
        this.text = text;
        this.timestamp2 = now;
        return false;
    }

    private static double computeDouble2(String str) {
        try {
            return Double.parseDouble(str.replace(",", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static String computeString(double d) {
        return String.format(Locale.ROOT, "%,.0f", Math.max(0.0, d));
    }

    private static double computeDouble(String str) {
        try {
            if (str.contains(",") && str.contains(".")) {
                str = str.lastIndexOf(44) > str.lastIndexOf(46) ? str.replace(".", "").replace(',', '.') : str.replace(",", "");
            } else if (str.contains(",")) {
                str = str.replace(',', '.');
            }

            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.reset();
        this.ui();
    }

    @Override
    public void onDisable() {
        HudDragHelper.handleObject(this);
        this.reset();
        super.onDisable();
    }

    private void reset() {
        this.currentGold = 0.0;
        this.amount = 0.0;
        this.text = "";
        this.timestamp2 = 0L;
    }

    public RequirementChecker() {
        super("Requirement Checker", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.xSetting, this.ySetting});
    }

    private boolean checkState() {
        return ServerUtils.ip();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent4) {
                this.reset();
            } else if (event instanceof ChatEvent) {
                if (this.checkState() && ((ChatEvent)event).message != null) {
                    String str = ExecuteCommandHelper.computeString6(((ChatEvent)event).message.getUnformattedText());
                    Matcher matcher = pattern.matcher(str);
                    if (matcher.find()) {
                        this.currentGold = computeDouble2(matcher.group(1));
                        this.amount = computeDouble2(matcher.group(2));
                    } else {
                        String str2 = str.toLowerCase(Locale.ROOT);
                        if (!str2.contains("prestige") || !str2.contains("unlocked") && !str2.contains("prestiged") && !str2.contains("level 120")) {
                            if (str.contains("XP") && matchesString(str) && !this.matchesString2(str)) {
                                Matcher matcher2 = pattern2.matcher(str);
                                if (matcher2.find()) {
                                    this.currentGold = this.currentGold + computeDouble(matcher2.group(1));
                                }
                            }
                        } else {
                            this.currentGold = 0.0;
                            this.amount = 0.0;
                            this.ui();
                        }
                    }
                }
            }
        }
    }

    private static boolean matchesString(String str) {
        String str2 = str.toUpperCase(Locale.ROOT);
        return str2.contains("KILL!")
            || str2.contains("DOUBLE KILL!")
            || str2.contains("TRIPLE KILL!")
            || str2.contains("QUADRA KILL!")
            || str2.contains("PENTA KILL!")
            || str2.contains("MULTI KILL!");
    }

    @Override
    public void onRender() {
        if (this.checkState44() && this.checkState() && mc.thePlayer != null) {
            String str = "Gold: " + computeString(this.currentGold) + "g / " + computeString(this.amount) + "g";
            int n = (int)this.xSetting.computeDouble();
            int n2 = (int)this.ySetting.computeDouble();
            if (mc.currentScreen instanceof GuiChat) {
                int[] ints = HudDragHelper.computeIntArray();
                if (Mouse.isButtonDown(0)
                    && !HudDragHelper.matchesObject(this)
                    && HudDragHelper.matchesInt(
                        ints[0],
                        ints[1],
                        n,
                        n2,
                        computeFontRenderer(RequirementChecker.class).getStringWidth(str),
                        computeFontRenderer(RequirementChecker.class).FONT_HEIGHT
                    )) {
                    HudDragHelper.matchesObject(this, ints[0], ints[1], n, n2);
                } else if (!Mouse.isButtonDown(0)) {
                    HudDragHelper.handleObject(this);
                }

                if (HudDragHelper.matchesObject(this)) {
                    int[] ints2 = HudDragHelper.computeIntArray(this);
                    if (ints2 != null) {
                        this.xSetting.handleDouble2(ints2[0]);
                        this.ySetting.handleDouble2(ints2[1]);
                        n = ints2[0];
                        n2 = ints2[1];
                    }
                }
            } else {
                HudDragHelper.handleObject(this);
            }

            computeFontRenderer(RequirementChecker.class).drawStringWithShadow(str, n, n2, -1);
        }
    }

    private void ui() {
        if (mc.thePlayer != null && this.checkState()) {
            mc.thePlayer.sendChatMessage("/greq");
        }
    }
}
