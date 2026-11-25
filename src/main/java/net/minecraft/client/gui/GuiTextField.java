package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;

public class GuiTextField extends Gui {
    private final int id;
    private final FontRenderer fontRendererInstance;
    public int xPosition;
    public int yPosition;
    private final int width;
    public final int height;
    private String text = "";
    private int maxStringLength = 32;
    private int cursorCounter;
    private boolean enableBackgroundDrawing = true;
    private boolean canLoseFocus = true;
    private boolean isFocused;
    private boolean isEnabled = true;
    private int lineScrollOffset;
    private int cursorPosition;
    private int selectionEnd;
    private int enabledColor = 14737632;
    private int disabledColor = 7368816;
    private boolean visible = true;
    private GuiResponder field_175210_x;
    private Predicate<String> field_175209_y = Predicates.alwaysTrue();

    public GuiTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
        this.id = componentId;
        this.fontRendererInstance = fontrendererObj;
        this.xPosition = x;
        this.yPosition = y;
        this.width = par5Width;
        this.height = par6Height;
    }

    public void func_175207_a(GuiResponder p_175207_1_) {
        this.field_175210_x = p_175207_1_;
    }

    public void updateCursorCounter() {
        this.cursorCounter++;
    }

    public void setText(String p_146180_1_) {
        if (this.field_175209_y.apply(p_146180_1_)) {
            if (p_146180_1_.length() > this.maxStringLength) {
                this.text = p_146180_1_.substring(0, this.maxStringLength);
            } else {
                this.text = p_146180_1_;
            }

            this.setCursorPositionEnd();
        }
    }

    public String getText() {
        return this.text;
    }

    public String getSelectedText() {
        int n = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int n2 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(n, n2);
    }

    public void func_175205_a(Predicate<String> p_175205_1_) {
        this.field_175209_y = p_175205_1_;
    }

    public void writeText(String p_146191_1_) {
        String str = "";
        String str2 = ChatAllowedCharacters.filterAllowedCharacters(p_146191_1_);
        int n = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int n2 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        int n3 = this.maxStringLength - this.text.length() - (n - n2);
        int n4 = 0;
        if (this.text.length() > 0) {
            str = str + this.text.substring(0, n);
        }

        if (n3 < str2.length()) {
            str = str + str2.substring(0, n3);
            n4 = n3;
        } else {
            str = str + str2;
            n4 = str2.length();
        }

        if (this.text.length() > 0 && n2 < this.text.length()) {
            str = str + this.text.substring(n2);
        }

        if (this.field_175209_y.apply(str)) {
            this.text = str;
            this.moveCursorBy(n - this.selectionEnd + n4);
            if (this.field_175210_x != null) {
                this.field_175210_x.func_175319_a(this.id, this.text);
            }
        }
    }

    public void deleteWords(int p_146177_1_) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(p_146177_1_) - this.cursorPosition);
            }
        }
    }

    public void deleteFromCursor(int p_146175_1_) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean flag = p_146175_1_ < 0;
                int n = flag ? this.cursorPosition + p_146175_1_ : this.cursorPosition;
                int n2 = flag ? this.cursorPosition : this.cursorPosition + p_146175_1_;
                String str = "";
                if (n >= 0) {
                    str = this.text.substring(0, n);
                }

                if (n2 < this.text.length()) {
                    str = str + this.text.substring(n2);
                }

                if (this.field_175209_y.apply(str)) {
                    this.text = str;
                    if (flag) {
                        this.moveCursorBy(p_146175_1_);
                    }

                    if (this.field_175210_x != null) {
                        this.field_175210_x.func_175319_a(this.id, this.text);
                    }
                }
            }
        }
    }

    public int getId() {
        return this.id;
    }

    public int getNthWordFromCursor(int p_146187_1_) {
        return this.getNthWordFromPos(p_146187_1_, this.getCursorPosition());
    }

    public int getNthWordFromPos(int p_146183_1_, int p_146183_2_) {
        return this.func_146197_a(p_146183_1_, p_146183_2_, true);
    }

    public int func_146197_a(int p_146197_1_, int p_146197_2_, boolean p_146197_3_) {
        int n = p_146197_2_;
        boolean flag = p_146197_1_ < 0;
        int n2 = Math.abs(p_146197_1_);

        for (int i = 0; i < n2; i++) {
            if (!flag) {
                int len = this.text.length();
                n = this.text.indexOf(32, n);
                if (n == -1) {
                    n = len;
                } else {
                    while (p_146197_3_ && n < len && this.text.charAt(n) == ' ') {
                        n++;
                    }
                }
            } else {
                while (p_146197_3_ && n > 0 && this.text.charAt(n - 1) == ' ') {
                    n--;
                }

                while (n > 0 && this.text.charAt(n - 1) != ' ') {
                    n--;
                }
            }
        }

        return n;
    }

    public void moveCursorBy(int p_146182_1_) {
        this.setCursorPosition(this.selectionEnd + p_146182_1_);
    }

    public void setCursorPosition(int p_146190_1_) {
        this.cursorPosition = p_146190_1_;
        int len = this.text.length();
        this.cursorPosition = MathHelper.clamp_int(this.cursorPosition, 0, len);
        this.setSelectionPos(this.cursorPosition);
    }

    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }

    public boolean textboxKeyTyped(char p_146201_1_, int p_146201_2_) {
        if (!this.isFocused) {
            return false;
        }
        if (GuiScreen.isKeyComboCtrlA(p_146201_2_)) {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
        }
        if (GuiScreen.isKeyComboCtrlC(p_146201_2_)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            return true;
        }
        if (GuiScreen.isKeyComboCtrlV(p_146201_2_)) {
            if (this.isEnabled) {
                this.writeText(GuiScreen.getClipboardString());
            }

            return true;
        }
        if (GuiScreen.isKeyComboCtrlX(p_146201_2_)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            if (this.isEnabled) {
                this.writeText("");
            }

            return true;
        }
        switch (p_146201_2_) {
            case 14:
                if (GuiScreen.isCtrlKeyDown()) {
                    if (this.isEnabled) {
                        this.deleteWords(-1);
                    }
                } else if (this.isEnabled) {
                    this.deleteFromCursor(-1);
                }

                return true;
            case 199:
                if (GuiScreen.isShiftKeyDown()) {
                    this.setSelectionPos(0);
                } else {
                    this.setCursorPositionZero();
                }

                return true;
            case 203:
                if (GuiScreen.isShiftKeyDown()) {
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                    } else {
                        this.setSelectionPos(this.getSelectionEnd() - 1);
                    }
                } else if (GuiScreen.isCtrlKeyDown()) {
                    this.setCursorPosition(this.getNthWordFromCursor(-1));
                } else {
                    this.moveCursorBy(-1);
                }

                return true;
            case 205:
                if (GuiScreen.isShiftKeyDown()) {
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                    } else {
                        this.setSelectionPos(this.getSelectionEnd() + 1);
                    }
                } else if (GuiScreen.isCtrlKeyDown()) {
                    this.setCursorPosition(this.getNthWordFromCursor(1));
                } else {
                    this.moveCursorBy(1);
                }

                return true;
            case 207:
                if (GuiScreen.isShiftKeyDown()) {
                    this.setSelectionPos(this.text.length());
                } else {
                    this.setCursorPositionEnd();
                }

                return true;
            case 211:
                if (GuiScreen.isCtrlKeyDown()) {
                    if (this.isEnabled) {
                        this.deleteWords(1);
                    }
                } else if (this.isEnabled) {
                    this.deleteFromCursor(1);
                }

                return true;
            default:
                if (ChatAllowedCharacters.isAllowedCharacter(p_146201_1_)) {
                    if (this.isEnabled) {
                        this.writeText(Character.toString(p_146201_1_));
                    }

                    return true;
                } else {
                    return false;
                }
        }
    }

    public void mouseClicked(int p_146192_1_, int p_146192_2_, int p_146192_3_) {
        boolean flag = p_146192_1_ >= this.xPosition && p_146192_1_ < this.xPosition + this.width && p_146192_2_ >= this.yPosition && p_146192_2_ < this.yPosition + this.height;
        if (this.canLoseFocus) {
            this.setFocused(flag);
        }

        if (this.isFocused && flag && p_146192_3_ == 0) {
            int n = p_146192_1_ - this.xPosition;
            if (this.enableBackgroundDrawing) {
                n -= 4;
            }

            String str = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            this.setCursorPosition(this.fontRendererInstance.trimStringToWidth(str, n).length() + this.lineScrollOffset);
        }
    }

    public void drawTextBox(int highlightStart, int highlightEnd, int highlightColor) {
        if (this.getVisible()) {
            if (highlightStart < 0 || highlightEnd <= highlightStart) {
                this.drawTextBox();
                return;
            }

            if (this.getEnableBackgroundDrawing()) {
                drawRect(this.xPosition - 1, this.yPosition - 1, this.xPosition + this.width + 1, this.yPosition + this.height + 1, -6250336);
                drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, -16777216);
            }

            int n = this.isEnabled ? this.enabledColor : this.disabledColor;
            int n2 = this.cursorPosition - this.lineScrollOffset;
            int n3 = this.selectionEnd - this.lineScrollOffset;
            String str = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            boolean flag = n2 >= 0 && n2 <= str.length();
            boolean flag2 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
            int n4 = this.enableBackgroundDrawing ? this.xPosition + 4 : this.xPosition;
            int n5 = this.enableBackgroundDrawing ? this.yPosition + (this.height - 8) / 2 : this.yPosition;
            int n6 = n4;
            if (n3 > str.length()) {
                n3 = str.length();
            }

            if (str.length() > 0) {
                String str2 = flag ? str.substring(0, n2) : str;
                n6 = this.drawColoredSubstring(str2, this.lineScrollOffset, n4, n5, n, highlightStart, highlightEnd, highlightColor);
            }

            boolean flag3 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int n7 = n6;
            if (!flag) {
                if (this.text.isEmpty()) {
                    n7 = n4;
                } else {
                    n7 = n2 > 0 ? n4 + this.getWidth() : n4;
                }
            } else if (flag3) {
                n7 = n6 - 1;
                n6--;
            }

            if (str.length() > 0 && flag && n2 < str.length()) {
                n6 = this.drawColoredSubstring(str.substring(n2), this.lineScrollOffset + n2, n6, n5, n, highlightStart, highlightEnd, highlightColor);
            }

            if (flag2) {
                if (flag3) {
                    Gui.drawRect(n7, n5 - 1, n7 + 1, n5 + 1 + this.fontRendererInstance.FONT_HEIGHT, -3092272);
                } else {
                    this.fontRendererInstance.drawStringWithShadow("_", n7, n5, n);
                }
            }

            if (n3 != n2) {
                int n8 = n4 + this.fontRendererInstance.getStringWidth(str.substring(0, n3));
                this.drawCursorVertical(n7, n5 - 1, n8 - 1, n5 + 1 + this.fontRendererInstance.FONT_HEIGHT);
            }
        }
    }

    private int drawColoredSubstring(String text, int textStartOffset, int x, int y, int defaultColor, int highlightStart, int highlightEnd, int highlightColor) {
        int n = x;
        int n2 = 0;

        while (n2 < text.length()) {
            int n3 = textStartOffset + n2;

            int n4;
            for (n4 = n2 + 1; n4 < text.length(); n4++) {
                int n5 = textStartOffset + n4;
                boolean flag = n3 >= highlightStart && n3 < highlightEnd;
                boolean flag2 = n5 >= highlightStart && n5 < highlightEnd;
                if (flag != flag2) {
                    break;
                }
            }

            String str = text.substring(n2, n4);
            int n6 = n3 >= highlightStart && n3 < highlightEnd ? highlightColor : defaultColor;
            n = this.fontRendererInstance.drawStringWithShadow(str, n, y, n6);
            n2 = n4;
        }

        return n;
    }

    public void drawTextBox() {
        if (this.getVisible()) {
            if (this.getEnableBackgroundDrawing()) {
                drawRect(this.xPosition - 1, this.yPosition - 1, this.xPosition + this.width + 1, this.yPosition + this.height + 1, -6250336);
                drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, -16777216);
            }

            int n = this.isEnabled ? this.enabledColor : this.disabledColor;
            int n2 = this.cursorPosition - this.lineScrollOffset;
            int n3 = this.selectionEnd - this.lineScrollOffset;
            String str = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            boolean flag = n2 >= 0 && n2 <= str.length();
            boolean flag2 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
            int n4 = this.enableBackgroundDrawing ? this.xPosition + 4 : this.xPosition;
            int n5 = this.enableBackgroundDrawing ? this.yPosition + (this.height - 8) / 2 : this.yPosition;
            int n6 = n4;
            if (n3 > str.length()) {
                n3 = str.length();
            }

            if (str.length() > 0) {
                String str2 = flag ? str.substring(0, n2) : str;
                n6 = this.fontRendererInstance.drawStringWithShadow(str2, n4, n5, n);
            }

            boolean flag3 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int n7 = n6;
            if (!flag) {
                if (this.text.isEmpty()) {
                    n7 = n4;
                } else {
                    n7 = n2 > 0 ? n4 + this.getWidth() : n4;
                }
            } else if (flag3) {
                n7 = n6 - 1;
                n6--;
            }

            if (str.length() > 0 && flag && n2 < str.length()) {
                n6 = this.fontRendererInstance.drawStringWithShadow(str.substring(n2), n6, n5, n);
            }

            if (flag2) {
                if (flag3) {
                    Gui.drawRect(n7, n5 - 1, n7 + 1, n5 + 1 + this.fontRendererInstance.FONT_HEIGHT, -3092272);
                } else {
                    this.fontRendererInstance.drawStringWithShadow("_", n7, n5, n);
                }
            }

            if (n3 != n2) {
                int n8 = n4 + this.fontRendererInstance.getStringWidth(str.substring(0, n3));
                this.drawCursorVertical(n7, n5 - 1, n8 - 1, n5 + 1 + this.fontRendererInstance.FONT_HEIGHT);
            }
        }
    }

    private void drawCursorVertical(int p_146188_1_, int p_146188_2_, int p_146188_3_, int p_146188_4_) {
        if (p_146188_1_ < p_146188_3_) {
            int n = p_146188_1_;
            p_146188_1_ = p_146188_3_;
            p_146188_3_ = n;
        }

        if (p_146188_2_ < p_146188_4_) {
            int n2 = p_146188_2_;
            p_146188_2_ = p_146188_4_;
            p_146188_4_ = n2;
        }

        if (p_146188_3_ > this.xPosition + this.width) {
            p_146188_3_ = this.xPosition + this.width;
        }

        if (p_146188_1_ > this.xPosition + this.width) {
            p_146188_1_ = this.xPosition + this.width;
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5387);
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(p_146188_1_, p_146188_4_, 0.0).func_181675_d();
        worldRenderer.func_181662_b(p_146188_3_, p_146188_4_, 0.0).func_181675_d();
        worldRenderer.func_181662_b(p_146188_3_, p_146188_2_, 0.0).func_181675_d();
        worldRenderer.func_181662_b(p_146188_1_, p_146188_2_, 0.0).func_181675_d();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    public void setMaxStringLength(int p_146203_1_) {
        this.maxStringLength = p_146203_1_;
        if (this.text.length() > p_146203_1_) {
            this.text = this.text.substring(0, p_146203_1_);
        }
    }

    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public int getCursorPosition() {
        return this.cursorPosition;
    }

    public boolean getEnableBackgroundDrawing() {
        return this.enableBackgroundDrawing;
    }

    public void setEnableBackgroundDrawing(boolean p_146185_1_) {
        this.enableBackgroundDrawing = p_146185_1_;
    }

    public void setTextColor(int p_146193_1_) {
        this.enabledColor = p_146193_1_;
    }

    public void setDisabledTextColour(int p_146204_1_) {
        this.disabledColor = p_146204_1_;
    }

    public void setFocused(boolean p_146195_1_) {
        if (p_146195_1_ && !this.isFocused) {
            this.cursorCounter = 0;
        }

        this.isFocused = p_146195_1_;
    }

    public boolean isFocused() {
        return this.isFocused;
    }

    public void setEnabled(boolean p_146184_1_) {
        this.isEnabled = p_146184_1_;
    }

    public int getSelectionEnd() {
        return this.selectionEnd;
    }

    public int getWidth() {
        return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
    }

    public void setSelectionPos(int p_146199_1_) {
        int len = this.text.length();
        if (p_146199_1_ > len) {
            p_146199_1_ = len;
        }

        if (p_146199_1_ < 0) {
            p_146199_1_ = 0;
        }

        this.selectionEnd = p_146199_1_;
        if (this.fontRendererInstance != null) {
            if (this.lineScrollOffset > len) {
                this.lineScrollOffset = len;
            }

            int width = this.getWidth();
            String str = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), width);
            int n = str.length() + this.lineScrollOffset;
            if (p_146199_1_ == this.lineScrollOffset) {
                this.lineScrollOffset = this.lineScrollOffset - this.fontRendererInstance.trimStringToWidth(this.text, width, true).length();
            }

            if (p_146199_1_ > n) {
                this.lineScrollOffset += p_146199_1_ - n;
            } else if (p_146199_1_ <= this.lineScrollOffset) {
                this.lineScrollOffset = this.lineScrollOffset - (this.lineScrollOffset - p_146199_1_);
            }

            this.lineScrollOffset = MathHelper.clamp_int(this.lineScrollOffset, 0, len);
        }
    }

    public void setCanLoseFocus(boolean p_146205_1_) {
        this.canLoseFocus = p_146205_1_;
    }

    public boolean getVisible() {
        return this.visible;
    }

    public void setVisible(boolean p_146189_1_) {
        this.visible = p_146189_1_;
    }
}
