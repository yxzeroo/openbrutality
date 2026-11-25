package org.brutality.clickgui.discord;

import java.awt.Color;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.MathHelper;
import org.brutality.gui.font.atlas.SFProApi;
import org.brutality.module.Module;
import org.brutality.module.impl.client.ClickGUI;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ColorThemeSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.MultiBoolSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;
import org.brutality.utils.timer.TimerUtil;
import org.lwjgl.input.Keyboard;

public class IsVisibleComponent {
    private static final int count = 70;
    private static final int count2 = 25;
    private final DiscordGUI discordGUI;
    private final Setting setting;
    private final Module module;
    private final GroupType groupType;
    private final String text;
    private final BooleanSetting booleanSetting;
    private final ModeSetting modeSetting;
    private final MultiBoolSetting multiBoolSetting;
    private final NumberSetting numberSetting;
    private final StringSetting stringSetting;
    private final ColorThemeSetting colorThemeSetting;
    private final ColorSetting colorSetting;
    private final TimerUtil timerUtil = new TimerUtil();
    private final TimerUtil timerUtil2 = new TimerUtil();
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private int x;
    private int y2;
    private int offset2 = 30;
    private int right = 5;
    private IsVisibleComponent isVisibleComponent;
    private boolean flag;
    private boolean dragging;
    private boolean focused;
    private IsVisibleComponent.HueMode hueMode = IsVisibleComponent.HueMode.HUE;
    private float factor;
    private float factor2;
    private float factor3;

    public IsVisibleComponent(DiscordGUI discordGUI, Setting setting, GroupType groupType) {
        this.discordGUI = discordGUI;
        this.setting = setting;
        this.module = setting.getParent();
        this.groupType = groupType;
        this.text = setting.getName();
        this.booleanSetting = setting instanceof BooleanSetting ? (BooleanSetting)setting : null;
        this.modeSetting = setting instanceof ModeSetting ? (ModeSetting)setting : null;
        this.multiBoolSetting = setting instanceof MultiBoolSetting ? (MultiBoolSetting)setting : null;
        this.numberSetting = setting instanceof NumberSetting ? (NumberSetting)setting : null;
        this.stringSetting = setting instanceof StringSetting ? (StringSetting)setting : null;
        this.colorThemeSetting = setting instanceof ColorThemeSetting ? (ColorThemeSetting)setting : null;
        this.colorSetting = setting instanceof ColorSetting ? (ColorSetting)setting : null;
        this.decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        if (groupType == GroupType.COLOR_PICKER) {
            float[] floats = this.computeFloatArray();
            this.factor = floats[0] * 70.0F;
            this.factor2 = floats[1] * 70.0F;
            this.factor3 = floats[2] * 70.0F;
        }
    }

    public boolean isVisible() {
        return this.setting.isVisible() && (this.isVisibleComponent == null || this.isVisibleComponent.flag && this.isVisibleComponent.isVisible());
    }

    private static boolean matchesInt(int n) {
        switch (n) {
            case 15:
            case 29:
            case 42:
            case 54:
            case 56:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 70:
            case 87:
            case 88:
            case 157:
            case 184:
            case 197:
            case 199:
            case 200:
            case 201:
            case 203:
            case 205:
            case 207:
            case 208:
            case 209:
            case 210:
            case 211:
            case 221:
                return false;
            default:
                return true;
        }
    }

    public int getOffset() {
        return this.offset2;
    }

    public boolean isFocused() {
        return this.focused;
    }

    public void handleInt(int n, int n2) {
        int n3 = this.computeInt();
        int n4 = -13684945;
        switch (this.groupType) {
            case GROUP:
                DiscordComponent.handleFloat(this.x - 3, this.y2 - 4, this.right + 3, this.y2 + 13, -13684945);
                SFProApi.Listener7.Entry4.apiApi.computeFloat2(this.text, this.x, this.y2, n3);
                SFProApi.Listener7.Entry4.apiApi.computeFloat2(this.flag ? "-" : "+", this.right - 8, this.y2, n3);
                break;
            case COMBOBOX:
                SFProApi.Listener7.Entry4.apiApi.computeFloat2(this.text, this.x, this.y2, -1);
                DiscordComponent.handleFloat(this.right - 70, this.y2 - 2, this.right, this.y2 + 8, 1.0F, this.matchesInt(n, n2) ? n3 : 1677721600, n4);
                SFProApi.Listener7.Entry3.apiApi.computeFloat(this.modeSetting.getText(), this.right - 35, this.y2, -1);
                if (this.flag) {
                    List list = this.modeSetting.computeList2();
                    DiscordComponent.handleFloat(this.right - 70, this.y2 + 10, this.right, this.y2 + 10 + list.size() * 11, 1.0F, 1677721600, n4);

                    for (int i = 0; i < list.size(); i++) {
                        String str = (String)list.get(i);
                        SFProApi.Listener7.Entry3.apiApi
                            .computeFloat(str, this.right - 35, this.y2 + 13 + i * 11, str.equalsIgnoreCase(this.modeSetting.getText()) ? n3 : -1);
                    }
                }
                break;
            case SELECTBOX:
                SFProApi.Listener7.Entry4.apiApi.computeFloat2(this.text, this.x, this.y2, -1);
                DiscordComponent.handleFloat(this.right - 70, this.y2 - 2, this.right, this.y2 + 8, 1.0F, this.matchesInt(n, n2) ? n3 : 1677721600, n4);
                ArrayList list2 = new ArrayList<>(this.multiBoolSetting.computeSet());
                if (list2.isEmpty()) {
                    SFProApi.Listener7.Entry3.apiApi.computeFloat("...", this.right - 35, this.y2, -1);
                } else {
                    SFProApi.Listener7.Entry3.apiApi
                        .computeFloat(
                            (CharSequence)(list2.size() > 1 ? (String)list2.get(0) + "..." : (CharSequence)list2.get(0)), this.right - 35, this.y2, -1
                        );
                }

                if (this.flag) {
                    List list3 = this.multiBoolSetting.getEntries();
                    DiscordComponent.handleFloat(this.right - 70, this.y2 + 10, this.right, this.y2 + 10 + list3.size() * 11, 1.0F, 1677721600, n4);

                    for (int j = 0; j < list3.size(); j++) {
                        String str2 = (String)list3.get(j);
                        SFProApi.Listener7.Entry3.apiApi
                            .computeFloat(str2, this.right - 35, this.y2 + 13 + j * 11, this.multiBoolSetting.matchesString(str2) ? n3 : -1);
                    }
                }
                break;
            case CHECKBOX:
                DiscordComponent.handleFloat(this.right - 10, this.y2 - 2, this.right, this.y2 + 8, 1.0F, 1677721600, n4);
                if (this.booleanSetting.isEnabled()) {
                    DiscordComponent.handleDouble(this.right - 8, this.y2 + 2, 2.0F, n3);
                }

                SFProApi.Listener7.Entry4.apiApi.computeFloat2(this.text, this.x, this.y2, -1);
                break;
            case SLIDER:
                double minValue = this.numberSetting.getMinValue();
                double maxValue = this.numberSetting.getMaxValue();
                double d = maxValue - minValue == 0.0 ? 0.0 : (this.numberSetting.getValue2() - minValue) / (maxValue - minValue);
                DiscordComponent.handleFloat(this.right - 70, this.y2 + 2, this.right, this.y2 + 4, 1.0F, 1677721600, n4);
                DiscordComponent.handleFloat(this.right - 70, this.y2 + 2, (float)(this.right - 70 + d * 70.0), this.y2 + 4, n3);
                DiscordComponent.handleFloat((float)(this.right - 70 + d * 70.0), this.y2 + 3, 2.0F, -1);
                SFProApi.Listener7.Entry4.apiApi.computeFloat2(this.text, this.x, this.y2, -1);
                SFProApi.Listener7.Entry2.apiApi
                    .computeFloat(this.decimalFormat.format(this.computeDouble()), (float)(this.right - 70 + 70.0 * d), this.y2 - 5, -1);
                if (this.dragging) {
                    double d2 = maxValue - minValue;
                    double d3 = minValue + MathHelper.clamp_double((n - (this.right - 70)) / 70.0, 0.0, 1.0) * d2;
                    this.numberSetting.handleDouble4(this.computeDouble(d3));
                }
                break;
            case TEXTBOX:
                String str3 = this.stringSetting.getValue() == null ? "" : this.stringSetting.getValue();
                if (this.focused && Keyboard.isKeyDown(Keyboard.KEY_BACK) && this.timerUtil.matchesLong4(100L) && !str3.isEmpty()) {
                    this.stringSetting.setValue(str3.substring(0, str3.length() - 1));
                    this.timerUtil.reset();
                    str3 = this.stringSetting.getValue();
                }

                SFProApi.Listener7.Entry4.apiApi.computeFloat2(this.text, this.x, this.y2, -1);
                DiscordComponent.handleFloat(this.right - 70, this.y2 - 2, this.right, this.y2 + 8, 1.0F, this.focused ? n3 : 1677721600, n4);
                if (str3.isEmpty()) {
                    if (this.focused && this.timerUtil2.matchesLong4(500L)) {
                        SFProApi.Listener7.Entry3.apiApi.computeFloat2("|", this.right - 68, (float)(this.y2 - 0.5), 1694498815);
                        if (this.timerUtil2.matchesLong4(1000L)) {
                            this.timerUtil2.reset();
                        }
                    }

                    SFProApi.Listener7.Entry3.apiApi.computeFloat2(this.text, this.right - 65, this.y2, 1694498815);
                } else if (SFProApi.Listener7.Entry3.apiApi.computeInt(str3) > 65) {
                    SFProApi.Listener7.Entry3.apiApi
                        .computeFloat2(SFProApi.Listener7.Entry3.apiApi.computeString(str3, 60, true), this.right - 68, this.y2, -1);
                } else {
                    SFProApi.Listener7.Entry3.apiApi.computeFloat2(str3, this.right - 68, this.y2, -1);
                }
                break;
            case COLOR_PICKER:
                SFProApi.Listener7.Entry4.apiApi.computeFloat2(this.text, this.x, this.y2, -1);
                DiscordComponent.handleFloat(this.right - 70, this.y2 - 2, this.right, this.y2 + 8, 1.0F, this.matchesInt(n, n2) ? n3 : 1677721600, n4);
                float[] floats = this.computeFloatArray();
                if (this.dragging) {
                    double d4 = MathHelper.clamp_double(n - this.right + 70, 0.35, 70.0);
                    float f = (float)(d4 / 70.0);
                    switch (this.hueMode) {
                        case HUE:
                            this.factor = (int)d4;
                            this.handleFloat(f, floats[1], floats[2]);
                            break;
                        case SATURATION:
                            this.factor2 = (int)d4;
                            this.handleFloat(floats[0], f, floats[2]);
                            break;
                        case BRIGHTNESS:
                            this.factor3 = (int)d4;
                            this.handleFloat(floats[0], floats[1], f);
                    }

                    floats = this.computeFloatArray();
                }

                for (int k = 0; k < 70; k++) {
                    DiscordComponent.handleFloat(this.right - 70 + k, this.y2 - 2, this.right - 70 + 1 + k, this.y2 + 8, switch (this.hueMode) {
                        case SATURATION -> Color.getHSBColor(floats[0], k / 70.0F, floats[2]).getRGB();
                        case BRIGHTNESS -> Color.getHSBColor(floats[0], floats[1], k / 70.0F).getRGB();
                        default -> Color.getHSBColor(k / 70.0F, floats[1], floats[2]).getRGB();
                    });
                }

                float f2 = this.hueMode == IsVisibleComponent.HueMode.SATURATION
                    ? this.factor2
                    : (this.hueMode == IsVisibleComponent.HueMode.BRIGHTNESS ? this.factor3 : this.factor);
                DiscordComponent.handleFloat(this.right - 70 + f2, this.y2 - 2, this.right - 70 + 1 + f2, this.y2 + 8, -16777216);
        }
    }

    private void handleFloat(float f, float f2, float f3) {
        if (this.colorSetting != null) {
            this.colorSetting.handleFloat3(f);
            this.colorSetting.handleFloat4(f2);
            this.colorSetting.handleFloat2(f3);
        } else if (this.colorThemeSetting != null) {
            this.colorThemeSetting.setDefaultColor(Color.getHSBColor(f, f2, f3).getRGB());
        }
    }

    private boolean matchesInt(int n, int n2) {
        switch (this.groupType) {
            case COMBOBOX:
            case SELECTBOX:
            case TEXTBOX:
            case COLOR_PICKER:
                return n >= this.right - 70 && n <= this.right && n2 >= this.y2 - 2 && n2 <= this.y2 + 8;
            case CHECKBOX:
                return n >= this.right - 10 && n <= this.right && n2 >= this.y2 - 2 && n2 <= this.y2 + 8;
            case SLIDER:
                return n >= this.right - 70 && n <= this.right && n2 >= this.y2 + 2 && n2 <= this.y2 + 8;
            default:
                return false;
        }
    }

    public void update() {
        this.x = this.discordGUI.getCount4() + 168;
        this.y2 = this.discordGUI.getCount5() + this.offset2 + this.discordGUI.getDiscordComponent2().computeList(this.module).indexOf(this) * 25;
        this.right = this.discordGUI.getCount4() + 45 + 105 + this.discordGUI.getWidth() - 18;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    private double computeDouble() {
        return this.computeDouble(this.numberSetting.getValue2());
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.dragging = false;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public int getY() {
        return this.y2;
    }

    public void setIsVisibleComponent(IsVisibleComponent isVisibleComponent) {
        this.isVisibleComponent = isVisibleComponent;
    }

    private boolean matchesInt2(int n, int n2) {
        if (!this.matchesInt(n, n2)) {
            return false;
        }
        if (this.flag = !this.flag) {
            for (IsVisibleComponent isVisibleComponent : this.discordGUI.getDiscordComponent2().computeList()) {
                if (isVisibleComponent != this && (isVisibleComponent.groupType == GroupType.COMBOBOX || isVisibleComponent.groupType == GroupType.SELECTBOX)) {
                    isVisibleComponent.flag = false;
                }
            }
        }

        return true;
    }

    private double computeDouble(double d) {
        double increment = this.numberSetting.getIncrement();
        return increment <= 0.0 ? d : Math.round(d / increment) * increment;
    }

    public boolean checkState() {
        return this.y2 <= this.discordGUI.getCount5() + this.discordGUI.getHeight() - 10 && this.y2 >= this.discordGUI.getCount5() + 23;
    }

    public Module getModule() {
        return this.module;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public void setOffset2(int offset2) {
        this.offset2 = offset2;
    }

    public boolean mouseClicked(int n, int n2, int n3) {
        if (!this.isVisible()) {
            return false;
        }
        if (this.groupType == GroupType.GROUP
            && (n3 == 0 || n3 == 1)
            && n >= this.x - 3
            && n <= this.right + 3
            && n2 >= this.y2 - 4
            && n2 <= this.y2 + 13) {
            this.flag = !this.flag;

            for (IsVisibleComponent isVisibleComponent : this.discordGUI.getDiscordComponent2().computeList2(this.module)) {
                isVisibleComponent.offset2 = this.offset2;
                if (!isVisibleComponent.isVisible()) {
                    isVisibleComponent.focused = false;
                    isVisibleComponent.dragging = false;
                    if (isVisibleComponent.groupType != GroupType.GROUP) {
                        isVisibleComponent.flag = false;
                    }
                }
            }

            return true;
        }
        if (n3 == 0) {
            switch (this.groupType) {
                case COMBOBOX:
                    if (!this.matchesInt2(n, n2) && this.flag && n >= this.right - 70 && n <= this.right) {
                        List list = this.modeSetting.computeList2();

                        for (int i = 0; i < list.size(); i++) {
                            int n4 = this.y2 + 10 + i * 11;
                            if (n2 >= n4 && n2 <= n4 + 11) {
                                this.modeSetting.handleString((String)list.get(i));
                                this.flag = false;
                                return true;
                            }
                        }
                    }
                    break;
                case SELECTBOX:
                    if (!this.matchesInt2(n, n2) && this.flag && n >= this.right - 70 && n <= this.right) {
                        List list2 = this.multiBoolSetting.getEntries();

                        for (int j = 0; j < list2.size(); j++) {
                            int n5 = this.y2 + 10 + j * 11;
                            if (n2 >= n5 && n2 <= n5 + 11) {
                                this.multiBoolSetting.handleString((String)list2.get(j));
                                return true;
                            }
                        }
                    }
                    break;
                case CHECKBOX:
                    if (this.matchesInt(n, n2)) {
                        this.booleanSetting.r();
                        return true;
                    }
                    break;
                case SLIDER:
                    if (this.matchesInt(n, n2)) {
                        this.dragging = true;
                        return true;
                    }
                    break;
                case TEXTBOX:
                    if (this.matchesInt(n, n2)) {
                        this.focused = !this.focused;
                        return true;
                    }

                    this.focused = false;
                    break;
                case COLOR_PICKER:
                    if (this.matchesInt(n, n2)) {
                        this.dragging = true;
                        return true;
                    }
            }
        } else if (n3 == 1 && this.groupType == GroupType.COLOR_PICKER && this.matchesInt(n, n2)) {
            IsVisibleComponent.HueMode[] hueModes = IsVisibleComponent.HueMode.values();
            this.hueMode = hueModes[(this.hueMode.ordinal() + 1) % hueModes.length];
            return true;
        }

        return false;
    }

    private int computeInt() {
        ClickGUI instance = ClickGUI.getInstance();
        return instance != null && instance.computeBrutalityHelper() != null ? instance.computeBrutalityHelper().getColor().getRGB() : -8225316;
    }

    public GroupType getGroupType() {
        return this.groupType;
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (this.groupType == GroupType.TEXTBOX) {
            if (!this.focused) {
                return;
            }

            if (keyCode == 1) {
                this.focused = false;
            } else if (keyCode != 14 && matchesInt(keyCode)) {
                this.stringSetting.setValue((this.stringSetting.getValue() == null ? "" : this.stringSetting.getValue()) + typedChar);
            }
        } else if (keyCode == 1 && this.flag) {
            this.flag = false;
        }
    }

    public boolean isFlag() {
        return this.flag;
    }

    public Setting getSetting() {
        return this.setting;
    }

    private float[] computeFloatArray() {
        if (this.colorSetting != null) {
            return new float[]{this.colorSetting.getFactor(), this.colorSetting.getSaturation(), this.colorSetting.getFactor4()};
        }
        if (this.colorThemeSetting != null) {
            int defaultColor = this.colorThemeSetting.getDefaultColor();
            return Color.RGBtoHSB((0xFF | ~(defaultColor >> 16)) - ~(defaultColor >> 16), defaultColor >> 8 & 0xFF, (0xFF | ~defaultColor) - ~defaultColor, new float[3]);
        }
        return new float[3];
    }

    public static enum HueMode {
        HUE,
        SATURATION,
        BRIGHTNESS;

        private static IsVisibleComponent.HueMode[] computeHueModeArray() {
            return new IsVisibleComponent.HueMode[]{HUE, SATURATION, BRIGHTNESS};
        }
    }
}
