package org.brutality.clickgui.newclick;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.brutality.clickgui.discord.ColorUtils;
import org.brutality.clickgui.discord.Inter18ptSemiboldTtfComponent;
import org.brutality.settings.impl.NumberSetting;

public class NewclickComponent8 extends SettingComponent {
    private final NumberSetting numberSetting;
    private boolean dragging;
    private boolean flag;
    private String text = "";

    @Override
    protected void process() {
        this.flag = false;
        this.text = "";
    }

    public NewclickComponent8(NumberSetting numberSetting) {
        super(numberSetting, 22);
        this.numberSetting = numberSetting;
    }

    @Override
    public boolean checkState2() {
        return this.dragging;
    }

    @Override
    public void handleInter18ptSemiboldTtfComponent(Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent, int n, int n2, int n3, int n4, int n5) {
        double d = this.numberSetting.computeDouble();
        double minValue = this.numberSetting.getMinValue();
        double maxValue = this.numberSetting.getMaxValue();
        float f = maxValue <= minValue ? 0.0F : (float)Math.max(0.0, Math.min(1.0, (d - minValue) / (maxValue - minValue)));
        String str;
        if (this.flag) {
            str = this.text.isEmpty() ? "..." : this.text;
            if (System.currentTimeMillis() / 500L % 2L == 0L) {
                str = str + "|";
            }
        } else {
            str = d == Math.floor(d) ? String.valueOf((int)d) : String.format("%.1f", d);
        }

        int n6 = inter18ptSemiboldTtfComponent.computeInt3(str);
        this.handleInter18ptSemiboldTtfComponent(inter18ptSemiboldTtfComponent, n + 3, n2 + 3, n3 - n6 - 10, n4, n5, ColorUtils.color14.getRGB());
        int n7 = this.flag ? ColorUtils.color10.getRGB() : ColorUtils.color15.getRGB();
        inter18ptSemiboldTtfComponent.handleString2(str, n + n3 - n6 - 3, n2 + 3, n7);
        int n8 = n2 + 14;
        byte b = 4;
        int n9 = n3 - 6;
        int n10 = n + 3;
        int rgb = new Color(35, 35, 38, 255).getRGB();
        int rgb2 = new Color(18, 18, 20, 255).getRGB();
        int rgb3 = ColorUtils.color8.getRGB();
        int rgb4 = new Color(245, 245, 245, 255).getRGB();
        Gui.drawRect(n10, n8, n10 + n9, n8 + b, rgb2);
        Gui.drawRect(n10 + 1, n8 + 1, n10 + n9 - 1, n8 + b - 1, rgb);
        int n11 = Math.max(1, (int)((n9 - 2) * f));
        Gui.drawRect(n10 + 1, n8 + 1, n10 + 1 + n11, n8 + b - 1, rgb3);
        int n12 = n10 + 1 + n11 - 2;
        Gui.drawRect(n12, n8 - 2, n12 + 4, n8 + b + 2, rgb4);
    }

    private static boolean matchesInt2(int n, int n2, int n3, int n4, int n5, int n6) {
        return n >= n3 && n <= n3 + n5 && n2 >= n4 && n2 <= n4 + n6;
    }

    private void process2() {
        if (!this.text.isEmpty()) {
            try {
                double d = Double.parseDouble(this.text);
                this.numberSetting.handleDouble(d);
            } catch (NumberFormatException ignored) {
            }
        }
    }

    @Override
    public void handleInt(int n, int n2, int n3, int n4, int n5) {
        if (this.dragging) {
            this.handleInt(n, n3 + 3, n5 - 6);
        }
    }

    @Override
    public boolean checkState() {
        return this.flag;
    }

    @Override
    public boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6) {
        if (this.numberSetting.computeDouble() == Math.floor(this.numberSetting.computeDouble())) {
            String.valueOf((int)this.numberSetting.computeDouble());
        } else {
            String.format("%.1f", this.numberSetting.computeDouble());
        }

        int n7 = n + n3 - 3 - 40;
        if (n6 == 1 && matchesInt2(n4, n5, n7, n2, 40, 12)) {
            NewClickGUI.process10();
            this.process4();
            this.flag = true;
            double d = this.numberSetting.computeDouble();
            this.text = d == (long)d ? String.valueOf((long)d) : String.format("%.2f", d);
            return true;
        }
        if (n6 != 0) {
            return false;
        }
        if (matchesInt2(n4, n5, n, n2, n3, this.height)) {
            this.flag = false;
            this.dragging = true;
            this.handleInt(n4, n + 3, n3 - 6);
            return true;
        }
        return false;
    }

    private void handleInt(int n, int n2, int n3) {
        float f = Math.max(0.0F, Math.min(1.0F, (float)(n - n2) / n3));
        double d = this.numberSetting.getMaxValue() - this.numberSetting.getMinValue();
        double d2 = this.numberSetting.getMinValue() + f * d;
        double increment = this.numberSetting.getIncrement();
        if (increment > 0.0) {
            d2 = Math.round(d2 / increment) * increment;
        }

        this.numberSetting.handleDouble2(Math.max(this.numberSetting.getMinValue(), Math.min(this.numberSetting.getMaxValue(), d2)));
    }

    @Override
    public void mouseReleased(int n, int n2) {
        this.dragging = false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.flag) {
            if (keyCode == 28) {
                this.process2();
                this.flag = false;
                this.text = "";
            } else if (keyCode == 1) {
                this.flag = false;
                this.text = "";
            } else if (keyCode == 14) {
                if (this.text.length() > 0) {
                    this.text = this.text.substring(0, this.text.length() - 1);
                }
            } else if (keyCode == 47 && GuiScreen.isCtrlKeyDown()) {
                String clipboardString = GuiScreen.getClipboardString();
                if (clipboardString != null) {
                    for (char ch : clipboardString.toCharArray()) {
                        if (Character.isDigit(ch) || ch == '.' || ch == '-') {
                            this.text = this.text + ch;
                        }
                    }
                }
            } else {
                if (typedChar >= '0' && typedChar <= '9' || typedChar == '.' || typedChar == '-') {
                    this.text = this.text + typedChar;
                }
            }
        }
    }
}
