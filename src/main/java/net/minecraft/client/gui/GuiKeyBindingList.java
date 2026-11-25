package net.minecraft.client.gui;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.ArrayUtils;
import org.brutality.gui.chat.ChatComponent;

public class GuiKeyBindingList extends GuiListExtended {
    final GuiControls field_148191_k;
    final Minecraft mc;
    private final IGuiListEntry[] listEntries;
    int maxListLabelWidth = 0;

    public GuiKeyBindingList(GuiControls controls, Minecraft mcIn) {
        super(mcIn, controls.width, controls.height, 63, controls.height - 32, 20);
        this.field_148191_k = controls;
        this.mc = mcIn;
        KeyBinding[] keyBindings = ArrayUtils.clone(mcIn.gameSettings.keyBindings);
        this.listEntries = new IGuiListEntry[keyBindings.length + KeyBinding.getKeybinds().size()];
        Arrays.sort(keyBindings);
        int n = 0;
        String str = null;

        for (KeyBinding keyBinding : keyBindings) {
            String keyCategory = keyBinding.getKeyCategory();
            if (!keyCategory.equals(str)) {
                str = keyCategory;
                this.listEntries[n++] = new GuiKeyBindingList.CategoryEntry(keyCategory);
            }

            int textWidth = ChatComponent.computeFontRenderer(mcIn).getStringWidth(I18n.format(keyBinding.getKeyDescription()));
            if (textWidth > this.maxListLabelWidth) {
                this.maxListLabelWidth = textWidth;
            }

            this.listEntries[n++] = new GuiKeyBindingList.KeyEntry(keyBinding);
        }
    }

    @Override
    protected int getSize() {
        return this.listEntries.length;
    }

    @Override
    public IGuiListEntry getListEntry(int index) {
        return this.listEntries[index];
    }

    @Override
    public int getScrollBarX() {
        return super.getScrollBarX() + 15;
    }

    @Override
    public int getListWidth() {
        return super.getListWidth() + 32;
    }

    public class CategoryEntry implements IGuiListEntry {
        private final String labelText;
        private final int labelWidth;

        public CategoryEntry(String str) {
            this.labelText = I18n.format(str);
            this.labelWidth = ChatComponent.computeFontRenderer(GuiKeyBindingList.this.mc).getStringWidth(this.labelText);
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
            FontRenderer fontRenderer = ChatComponent.computeFontRenderer(GuiKeyBindingList.this.mc);
            fontRenderer.drawString(
                this.labelText, GuiKeyBindingList.this.mc.currentScreen.width / 2 - this.labelWidth / 2, y + slotHeight - fontRenderer.FONT_HEIGHT - 1, 16777215
            );
        }

        @Override
        public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
            return false;
        }

        @Override
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
        }

        @Override
        public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
        }
    }

    public class KeyEntry implements IGuiListEntry {
        private final KeyBinding keybinding;
        private final String keyDesc;
        private final GuiButton btnChangeKeyBinding;
        private final GuiButton btnReset;

        KeyEntry(KeyBinding keybinding) {
            this.keybinding = keybinding;
            this.keyDesc = I18n.format(keybinding.getKeyDescription());
            this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 75, 20, I18n.format(keybinding.getKeyDescription()));
            this.btnReset = new GuiButton(0, 0, 0, 50, 20, I18n.format("controls.reset"));
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
            boolean flag = GuiKeyBindingList.this.field_148191_k.buttonId == this.keybinding;
            FontRenderer fontRenderer = ChatComponent.computeFontRenderer(GuiKeyBindingList.this.mc);
            fontRenderer.drawString(this.keyDesc, x + 90 - GuiKeyBindingList.this.maxListLabelWidth, y + slotHeight / 2 - fontRenderer.FONT_HEIGHT / 2, 16777215);
            this.btnReset.xPosition = x + 190;
            this.btnReset.yPosition = y;
            this.btnReset.enabled = this.keybinding.getKeyCode() != this.keybinding.getKeyCodeDefault();
            this.btnReset.drawButton(GuiKeyBindingList.this.mc, mouseX, mouseY);
            this.btnChangeKeyBinding.xPosition = x + 105;
            this.btnChangeKeyBinding.yPosition = y;
            this.btnChangeKeyBinding.displayString = GameSettings.getKeyDisplayString(this.keybinding.getKeyCode());
            boolean flag2 = false;
            if (this.keybinding.getKeyCode() != 0) {
                for (KeyBinding keyBinding : GuiKeyBindingList.this.mc.gameSettings.keyBindings) {
                    if (keyBinding != this.keybinding && keyBinding.getKeyCode() == this.keybinding.getKeyCode()) {
                        flag2 = true;
                        break;
                    }
                }
            }

            if (flag) {
                this.btnChangeKeyBinding.displayString = EnumChatFormatting.WHITE
                    + "> "
                    + EnumChatFormatting.YELLOW
                    + this.btnChangeKeyBinding.displayString
                    + EnumChatFormatting.WHITE
                    + " <";
            } else if (flag2) {
                this.btnChangeKeyBinding.displayString = EnumChatFormatting.RED + this.btnChangeKeyBinding.displayString;
            }

            this.btnChangeKeyBinding.drawButton(GuiKeyBindingList.this.mc, mouseX, mouseY);
        }

        @Override
        public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
            if (this.btnChangeKeyBinding.mousePressed(GuiKeyBindingList.this.mc, p_148278_2_, p_148278_3_)) {
                GuiKeyBindingList.this.field_148191_k.buttonId = this.keybinding;
                return true;
            }
            if (this.btnReset.mousePressed(GuiKeyBindingList.this.mc, p_148278_2_, p_148278_3_)) {
                GuiKeyBindingList.this.mc.gameSettings.setOptionKeyBinding(this.keybinding, this.keybinding.getKeyCodeDefault());
                KeyBinding.resetKeyBindingArrayAndHash();
                return true;
            }
            return false;
        }

        @Override
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            this.btnChangeKeyBinding.mouseReleased(x, y);
            this.btnReset.mouseReleased(x, y);
        }

        @Override
        public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
        }
    }
}
