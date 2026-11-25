package org.brutality.clickgui.newclick;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import net.minecraft.client.gui.Gui;
import org.brutality.clickgui.discord.ColorUtils;
import org.brutality.clickgui.discord.Inter18ptSemiboldTtfComponent;
import org.brutality.settings.impl.StringSetting;
import org.lwjgl.input.Keyboard;

public class GetClipboardStringComponent extends SettingComponent {
    private final StringSetting stringSetting;
    private boolean flag;
    private int count = -1;
    private int selectionEnd = -1;

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.flag) {
            String str = this.stringSetting.getValue() == null ? "" : this.stringSetting.getValue();
            if (keyCode != 1 && keyCode != 28) {
                boolean flag = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
                if (flag && keyCode == 30) {
                    this.count = 0;
                    this.selectionEnd = str.length();
                } else if (flag && keyCode == 46 && this.checkState3()) {
                    try {
                        String str2 = str.substring(Math.min(this.count, this.selectionEnd), Math.max(this.count, this.selectionEnd));
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(str2), null);
                    } catch (Exception ignored) {
                    }
                } else if (flag && keyCode == 47) {
                    String clipboardString = getClipboardString();
                    if (clipboardString != null && !clipboardString.isEmpty()) {
                        if (this.checkState3()) {
                            int n = Math.min(this.count, this.selectionEnd);
                            int n2 = Math.max(this.count, this.selectionEnd);
                            this.stringSetting.setValue(str.substring(0, n) + clipboardString + str.substring(n2));
                            this.count = n + clipboardString.length();
                            this.selectionEnd = this.count;
                        } else {
                            this.stringSetting.setValue(str + clipboardString);
                        }
                    }
                } else if (flag && keyCode == 45 && this.checkState3()) {
                    try {
                        int n3 = Math.min(this.count, this.selectionEnd);
                        int n4 = Math.max(this.count, this.selectionEnd);
                        String str3 = str.substring(n3, n4);
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(str3), null);
                        this.stringSetting.setValue(str.substring(0, n3) + str.substring(n4));
                        this.count = -1;
                        this.selectionEnd = -1;
                    } catch (Exception ignored2) {
                    }
                } else if (keyCode == 14) {
                    if (this.checkState3()) {
                        int n5 = Math.min(this.count, this.selectionEnd);
                        int n6 = Math.max(this.count, this.selectionEnd);
                        this.stringSetting.setValue(str.substring(0, n5) + str.substring(n6));
                        this.count = -1;
                        this.selectionEnd = -1;
                    } else if (!str.isEmpty()) {
                        this.stringSetting.setValue(str.substring(0, str.length() - 1));
                    }
                } else if (keyCode == 211) {
                    if (this.checkState3()) {
                        int n7 = Math.min(this.count, this.selectionEnd);
                        int n8 = Math.max(this.count, this.selectionEnd);
                        this.stringSetting.setValue(str.substring(0, n7) + str.substring(n8));
                        this.count = -1;
                        this.selectionEnd = -1;
                    }
                } else {
                    if (typedChar >= ' ' && typedChar != 167) {
                        if (this.checkState3()) {
                            int n9 = Math.min(this.count, this.selectionEnd);
                            int n10 = Math.max(this.count, this.selectionEnd);
                            this.stringSetting.setValue(str.substring(0, n9) + typedChar + str.substring(n10));
                            this.count = n9 + 1;
                            this.selectionEnd = this.count;
                        } else {
                            this.stringSetting.setValue(str + typedChar);
                        }
                    }
                }
            } else {
                this.flag = false;
                this.count = -1;
                this.selectionEnd = -1;
            }
        }
    }

    private static boolean matchesInt2(int n, int n2, int n3, int n4, int n5, int n6) {
        return n >= n3 && n <= n3 + n5 && n2 >= n4 && n2 <= n4 + n6;
    }

    @Override
    public boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6) {
        if (n6 != 0) {
            return false;
        }
        this.flag = matchesInt2(n4, n5, n + 1, n2 + 12, n3 - 2, 13);
        if (this.flag) {
            NewClickGUI.process10();
            this.process4();
            this.count = -1;
            this.selectionEnd = -1;
        }

        return this.flag;
    }

    @Override
    public boolean checkState() {
        return this.flag;
    }

    private static String getClipboardString() {
        try {
            Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String)contents.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    @Override
    protected void process() {
        this.flag = false;
        this.count = -1;
        this.selectionEnd = -1;
    }

    @Override
    public void handleInter18ptSemiboldTtfComponent(Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent, int n, int n2, int n3, int n4, int n5) {
        String str = this.stringSetting.getValue() == null ? "" : this.stringSetting.getValue();
        int n6 = n2 + 12;
        int n7 = n2 + 25;
        Gui.drawRect(n + 1, n6, n + n3 - 1, n7, new Color(0, 0, 0, 50).getRGB());
        String str2 = this.flag ? str + (System.currentTimeMillis() / 400L % 2L == 0L ? "|" : "") : str;
        inter18ptSemiboldTtfComponent.handleString2(this.stringSetting.getName(), n + 3, n2 + 2, ColorUtils.color15.getRGB());
        inter18ptSemiboldTtfComponent.handleString2(inter18ptSemiboldTtfComponent.computeString(str2, n3 - 6), n + 4, n6 + 3, ColorUtils.color14.getRGB());
    }

    private boolean checkState3() {
        return this.count >= 0 && this.selectionEnd >= 0 && this.count != this.selectionEnd;
    }

    public GetClipboardStringComponent(StringSetting stringSetting) {
        super(stringSetting, 28);
        this.stringSetting = stringSetting;
    }
}
