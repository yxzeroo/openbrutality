package org.brutality.alt.alt;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.opengl.GL11;

public class GuiPasswordField extends Gui {
    private final FontRenderer fontRenderer;
    private final int count;
    private final int count2;
    private final int count3;
    private final int count4;
    private String text = "";
    private int maxStringLength2 = 50;
    private int cursorCounter;
    private boolean enableBackgroundDrawing2 = true;
    private boolean canLoseFocus2 = true;
    public boolean focused = false;
    private final boolean flag = true;
    private int count5 = 0;
    private int cursorPosition2 = 0;
    private int selectionEnd2 = 0;
    private int enabledColor = 14737632;
    private final int count6 = 7368816;
    private boolean flag2 = true;

    public boolean getEnableBackgroundDrawing() {
        return this.enableBackgroundDrawing2;
    }

    public String computeString() {
        int n = this.cursorPosition2 < this.selectionEnd2 ? this.cursorPosition2 : this.selectionEnd2;
        int n2 = this.cursorPosition2 < this.selectionEnd2 ? this.selectionEnd2 : this.cursorPosition2;
        return this.text.substring(n, n2);
    }

    private void drawCursorVertical(int n, int n2, int n3, int n4) {
        if (n < n3) {
            int n5 = n;
            n = n3;
            n3 = n5;
        }

        if (n2 < n4) {
            int n6 = n2;
            n2 = n4;
            n4 = n6;
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GL11.glColor4f(0.0F, 0.0F, 255.0F, 255.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glLogicOp(5387);
        worldRenderer.func_181668_a(7, worldRenderer.getVertexFormat());
        worldRenderer.func_181662_b(n, n4, 0.0);
        worldRenderer.func_181662_b(n3, n4, 0.0);
        worldRenderer.func_181662_b(n3, n2, 0.0);
        worldRenderer.func_181662_b(n, n2, 0.0);
        worldRenderer.finishDrawing();
        GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public void handleInt2(int n) {
        this.setCursorPosition(this.selectionEnd2 + n);
    }

    public void handleInt3(int selectionEnd2) {
        int len = this.text.length();
        if (selectionEnd2 > len) {
            selectionEnd2 = len;
        }

        if (selectionEnd2 < 0) {
            selectionEnd2 = 0;
        }

        this.selectionEnd2 = selectionEnd2;
        if (this.fontRenderer != null) {
            if (this.count5 > len) {
                this.count5 = len;
            }

            int width = this.getWidth();
            String str = this.fontRenderer.trimStringToWidth(this.text.substring(this.count5), width);
            int n = str.length() + this.count5;
            if (selectionEnd2 == this.count5) {
                this.count5 = this.count5 - this.fontRenderer.trimStringToWidth(this.text, width, true).length();
            }

            if (selectionEnd2 > n) {
                this.count5 += selectionEnd2 - n;
            } else if (selectionEnd2 <= this.count5) {
                this.count5 = this.count5 - (this.count5 - selectionEnd2);
            }

            if (this.count5 < 0) {
                this.count5 = 0;
            }

            if (this.count5 > len) {
                this.count5 = len;
            }
        }
    }

    public void setCursorPosition(int cursorPosition2) {
        this.cursorPosition2 = cursorPosition2;
        int len = this.text.length();
        if (this.cursorPosition2 < 0) {
            this.cursorPosition2 = 0;
        }

        if (this.cursorPosition2 > len) {
            this.cursorPosition2 = len;
        }

        this.handleInt3(this.cursorPosition2);
    }

    public boolean isFocused() {
        return this.focused;
    }

    public void updateCursorCounter() {
        this.cursorCounter++;
    }

    public void setFocused(boolean focused) {
        if (focused && !this.focused) {
            this.cursorCounter = 0;
        }

        this.focused = focused;
    }

    public void deleteFromCursor(int n) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd2 != this.cursorPosition2) {
                this.writeText("");
            } else {
                boolean flag = n < 0;
                int n2 = flag ? this.cursorPosition2 + n : this.cursorPosition2;
                int n3 = flag ? this.cursorPosition2 : this.cursorPosition2 + n;
                String str = "";
                if (n2 >= 0) {
                    str = this.text.substring(0, n2);
                }

                if (n3 < this.text.length()) {
                    str = String.valueOf(str) + this.text.substring(n3);
                }

                this.text = str;
                if (flag) {
                    this.handleInt2(n);
                }
            }
        }
    }

    public GuiPasswordField(FontRenderer fontRenderer, int count, int n, int n2, int n3) {
        this.fontRenderer = fontRenderer;
        this.count = count;
        this.count2 = n;
        this.count3 = n2;
        this.count4 = n3;
    }

    public boolean textboxKeyTyped(char ch, int n) {
        if (this.flag && this.focused) {
            switch (ch) {
                case '\u0001':
                    this.setCursorPositionEnd();
                    this.handleInt3(0);
                    return true;
                case '\u0003':
                    GuiScreen.setClipboardString(this.computeString());
                    return true;
                case '\u0016':
                    this.writeText(GuiScreen.getClipboardString());
                    return true;
                case '\u0018':
                    GuiScreen.setClipboardString(this.computeString());
                    this.writeText("");
                    return true;
                default:
                    switch (n) {
                        case 14:
                            if (GuiScreen.isCtrlKeyDown()) {
                                this.handleInt(-1);
                            } else {
                                this.deleteFromCursor(-1);
                            }

                            return true;
                        case 199:
                            if (GuiScreen.isShiftKeyDown()) {
                                this.handleInt3(0);
                            } else {
                                this.setCursorPositionZero();
                            }

                            return true;
                        case 203:
                            if (GuiScreen.isShiftKeyDown()) {
                                if (GuiScreen.isCtrlKeyDown()) {
                                    this.handleInt3(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                                } else {
                                    this.handleInt3(this.getSelectionEnd() - 1);
                                }
                            } else if (GuiScreen.isCtrlKeyDown()) {
                                this.setCursorPosition(this.getNthWordFromCursor(-1));
                            } else {
                                this.handleInt2(-1);
                            }

                            return true;
                        case 205:
                            if (GuiScreen.isShiftKeyDown()) {
                                if (GuiScreen.isCtrlKeyDown()) {
                                    this.handleInt3(this.getNthWordFromPos(1, this.getSelectionEnd()));
                                } else {
                                    this.handleInt3(this.getSelectionEnd() + 1);
                                }
                            } else if (GuiScreen.isCtrlKeyDown()) {
                                this.setCursorPosition(this.getNthWordFromCursor(1));
                            } else {
                                this.handleInt2(1);
                            }

                            return true;
                        case 207:
                            if (GuiScreen.isShiftKeyDown()) {
                                this.handleInt3(this.text.length());
                            } else {
                                this.setCursorPositionEnd();
                            }

                            return true;
                        case 211:
                            if (GuiScreen.isCtrlKeyDown()) {
                                this.handleInt(1);
                            } else {
                                this.deleteFromCursor(1);
                            }

                            return true;
                        default:
                            if (ChatAllowedCharacters.isAllowedCharacter(ch)) {
                                this.writeText(Character.toString(ch));
                                return true;
                            } else {
                                return false;
                            }
                    }
            }
        } else {
            return false;
        }
    }

    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    public int getNthWordFromCursor(int n) {
        return this.getNthWordFromPos(n, this.getCursorPosition());
    }

    public int computeInt(int n, int n2, boolean flag) {
        int n3 = n2;
        boolean flag2 = n < 0;
        int n4 = Math.abs(n);

        for (int i = 0; i < n4; i++) {
            if (!flag2) {
                int len = this.text.length();
                n3 = this.text.indexOf(32, n3);
                if (n3 == -1) {
                    n3 = len;
                } else {
                    while (flag && n3 < len && this.text.charAt(n3) == ' ') {
                        n3++;
                    }
                }
            } else {
                while (flag && n3 > 0 && this.text.charAt(n3 - 1) == ' ') {
                    n3--;
                }

                while (n3 > 0 && this.text.charAt(n3 - 1) != ' ') {
                    n3--;
                }
            }
        }

        return n3;
    }

    public int getCursorPosition() {
        return this.cursorPosition2;
    }

    public void setText(String text) {
        if (text.length() > this.maxStringLength2) {
            this.text = text.substring(0, this.maxStringLength2);
        } else {
            this.text = text;
        }

        this.setCursorPositionEnd();
    }

    public int getSelectionEnd() {
        return this.selectionEnd2;
    }

    public void setEnabledColor(int enabledColor) {
        this.enabledColor = enabledColor;
    }

    public void drawTextBox() {
        if (this.isFlag2()) {
            if (this.getEnableBackgroundDrawing()) {
                Gui.drawRect(this.count - 1, this.count2 - 1, this.count + this.count3 + 1, this.count2 + this.count4 + 1, -6250336);
                Gui.drawRect(this.count, this.count2, this.count + this.count3, this.count2 + this.count4, -16777216);
            }

            int n = this.flag ? this.enabledColor : this.count6;
            int n2 = this.cursorPosition2 - this.count5;
            int n3 = this.selectionEnd2 - this.count5;
            String str = this.fontRenderer.trimStringToWidth(this.text.substring(this.count5), this.getWidth());
            boolean flag = n2 >= 0 && n2 <= str.length();
            boolean flag2 = this.focused && this.cursorCounter / 6 % 2 == 0 && flag;
            int n4 = this.enableBackgroundDrawing2 ? this.count + 4 : this.count;
            int n5 = this.enableBackgroundDrawing2 ? this.count2 + (this.count4 - 8) / 2 : this.count2;
            int n6 = n4;
            if (n3 > str.length()) {
                n3 = str.length();
            }

            if (!str.isEmpty()) {
                if (flag) {
                    str.substring(0, n2);
                }

                n6 = Minecraft.getFontRenderer().drawStringWithShadow(this.text.replaceAll("(?s).", "*"), n4, n5, n);
            }

            boolean flag3 = this.cursorPosition2 < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int n7 = n6;
            if (!flag) {
                n7 = n2 > 0 ? n4 + this.count3 : n4;
            } else if (flag3) {
                n7 = n6 - 1;
                n6--;
            }

            if (!str.isEmpty() && flag && n2 < str.length()) {
                Minecraft.getFontRenderer().drawStringWithShadow(str.substring(n2), n6, n5, n);
            }

            if (flag2) {
                if (flag3) {
                    Gui.drawRect(n7, n5 - 1, n7 + 1, n5 + 1 + this.fontRenderer.FONT_HEIGHT, -3092272);
                } else {
                    Minecraft.getFontRenderer().drawStringWithShadow("_", n7, n5, n);
                }
            }

            if (n3 != n2) {
                int n8 = n4 + this.fontRenderer.getStringWidth(str.substring(0, n3));
                this.drawCursorVertical(n7, n5 - 1, n8 - 1, n5 + 1 + this.fontRenderer.FONT_HEIGHT);
            }
        }
    }

    public void setCanLoseFocus(boolean canLoseFocus2) {
        this.canLoseFocus2 = canLoseFocus2;
    }

    public int getWidth() {
        return this.getEnableBackgroundDrawing() ? this.count3 - 8 : this.count3;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean flag = mouseX >= this.count && mouseX < this.count + this.count3 && mouseY >= this.count2 && mouseY < this.count2 + this.count4;
        if (this.canLoseFocus2) {
            this.setFocused(this.flag && flag);
        }

        if (this.focused && mouseButton == 0) {
            int n = mouseX - this.count;
            if (this.enableBackgroundDrawing2) {
                n -= 4;
            }

            String str = this.fontRenderer.trimStringToWidth(this.text.substring(this.count5), this.getWidth());
            this.setCursorPosition(this.fontRenderer.trimStringToWidth(str, n).length() + this.count5);
        }
    }

    public boolean isFlag2() {
        return this.flag2;
    }

    public void setMaxStringLength(int maxStringLength2) {
        this.maxStringLength2 = maxStringLength2;
        if (this.text.length() > maxStringLength2) {
            this.text = this.text.substring(0, maxStringLength2);
        }
    }

    public int getMaxStringLength() {
        return this.maxStringLength2;
    }

    public void setFlag2(boolean flag) {
        this.flag2 = flag;
    }

    public int getNthWordFromPos(int n, int n2) {
        return this.computeInt(n, this.getCursorPosition(), true);
    }

    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }

    public void setEnableBackgroundDrawing(boolean enableBackgroundDrawing2) {
        this.enableBackgroundDrawing2 = enableBackgroundDrawing2;
    }

    public void handleInt(int n) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd2 != this.cursorPosition2) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(n) - this.cursorPosition2);
            }
        }
    }

    public void writeText(String str) {
        String str2 = "";
        String str3 = ChatAllowedCharacters.filterAllowedCharacters(str);
        int n = this.cursorPosition2 < this.selectionEnd2 ? this.cursorPosition2 : this.selectionEnd2;
        int n2 = this.cursorPosition2 < this.selectionEnd2 ? this.selectionEnd2 : this.cursorPosition2;
        int n3 = this.maxStringLength2 - this.text.length() - (n - this.selectionEnd2);
        if (!this.text.isEmpty()) {
            str2 = String.valueOf(str2) + this.text.substring(0, n);
        }

        int n4;
        if (n3 < str3.length()) {
            str2 = String.valueOf(str2) + str3.substring(0, n3);
            n4 = n3;
        } else {
            str2 = String.valueOf(str2) + str3;
            n4 = str3.length();
        }

        if (!this.text.isEmpty() && n2 < this.text.length()) {
            str2 = String.valueOf(str2) + this.text.substring(n2);
        }

        this.text = str2.replaceAll(" ", "");
        this.handleInt2(n - this.selectionEnd2 + n4);
    }

    public String getText() {
        return this.text.replaceAll(" ", "");
    }
}
