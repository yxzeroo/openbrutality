package org.brutality.clickgui.swap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.brutality.module.impl.pit.hypixel.utility.SwapMacros;

public final class InventorySwapMacrosScreen extends GuiScreen {
    private GuiTextField guiTextField;
    private GuiTextField guiTextField2;
    private GuiTextField guiTextField3;
    private int count;
    private String status = "Hold an item, enter a name, key and destination. Hotbar: 1-9; armor: pants/boots/chest/helmet.";
    private final List<SwapMacros.Entry> entries = new ArrayList<>();

    @Override
    public void initGui() {
        int n = this.width / 2 - 220;
        this.guiTextField = new GuiTextField(0, this.fontRendererObj, n, 55, 130, 20);
        this.guiTextField.setMaxStringLength(32);
        this.guiTextField2 = new GuiTextField(1, this.fontRendererObj, n + 140, 55, 80, 20);
        this.guiTextField2.setMaxStringLength(12);
        this.guiTextField3 = new GuiTextField(2, this.fontRendererObj, n + 230, 55, 95, 20);
        this.guiTextField3.setMaxStringLength(12);
        this.process();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 15) {
            if (this.guiTextField.isFocused()) {
                this.guiTextField.setFocused(false);
                this.guiTextField2.setFocused(true);
            } else if (this.guiTextField2.isFocused()) {
                this.guiTextField2.setFocused(false);
                this.guiTextField3.setFocused(true);
            } else {
                this.guiTextField3.setFocused(false);
                this.guiTextField.setFocused(true);
            }
        } else if (!this.guiTextField.textboxKeyTyped(typedChar, keyCode)
            && !this.guiTextField2.textboxKeyTyped(typedChar, keyCode)
            && !this.guiTextField3.textboxKeyTyped(typedChar, keyCode)) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void updateScreen() {
        this.guiTextField.updateCursorCounter();
        this.guiTextField2.updateCursorCounter();
        this.guiTextField3.updateCursorCounter();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.guiTextField.mouseClicked(mouseX, mouseY, mouseButton);
        this.guiTextField2.mouseClicked(mouseX, mouseY, mouseButton);
        this.guiTextField3.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        int n = this.width / 2 - 220;
        this.drawCenteredString(this.fontRendererObj, "Inventory Swap Macros", this.width / 2, 18, 16777215);
        this.fontRendererObj.drawString("Name", n, 43, 11184810);
        this.fontRendererObj.drawString("Key / Mouse4", n + 140, 43, 11184810);
        this.fontRendererObj.drawString("Destination", n + 230, 43, 11184810);
        this.guiTextField.drawTextBox();
        this.guiTextField2.drawTextBox();
        this.guiTextField3.drawTextBox();

        for (int i = 0; i < this.entries.size(); i++) {
            SwapMacros.Entry entry = this.entries.get(i);
            String str = entry.count >= 36 ? "slot " + (entry.count - 35) : new String[]{"helmet", "chest", "pants", "boots"}[entry.count - 5];
            this.fontRendererObj
                .drawString(
                    this.fontRendererObj.trimStringToWidth(entry.name + " [" + SwapMacros.computeString(entry.key) + "] -> " + str, 275),
                    n,
                    108 + i * 24,
                    16777215
                );
        }

        this.fontRendererObj.drawSplitString(this.status, n, this.height - 62, 440, 12303291);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void process() {
        this.buttonList.clear();
        this.entries.clear();
        int n = this.width / 2 - 220;
        SwapMacros instance = SwapMacros.getInstance();
        if (instance != null) {
            int n2 = Math.max(1, Math.min(8, (this.height - 160) / 24));
            this.count = Math.max(0, Math.min(this.count, Math.max(0, (instance.entries().size() - 1) / n2)));
            int n3 = this.count * n2;
            this.entries.addAll(instance.entries().subList(n3, Math.min(n3 + n2, instance.entries().size())));
            this.buttonList.add(new GuiButton(0, n + 335, 55, 105, 20, "Add held item"));
            this.buttonList.add(new GuiButton(1, n, this.height - 30, 95, 20, "Previous"));
            this.buttonList.add(new GuiButton(2, n + 105, this.height - 30, 95, 20, "Next"));
            this.buttonList.add(new GuiButton(3, n + 210, this.height - 30, 130, 20, instance.checkState44() ? "Macros: ON" : "Macros: OFF"));
            this.buttonList.add(new GuiButton(4, n + 350, this.height - 30, 90, 20, "Done"));

            for (int i = 0; i < this.entries.size(); i++) {
                this.buttonList.add(new GuiButton(100 + i, n + 285, 102 + i * 24, 80, 20, this.entries.get(i).enabled ? "Enabled" : "Disabled"));
                this.buttonList.add(new GuiButton(200 + i, n + 370, 102 + i * 24, 70, 20, "Delete"));
            }
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        SwapMacros instance = SwapMacros.getInstance();
        if (instance != null) {
            try {
                if (button.id == 0) {
                    instance.handleString(
                        this.guiTextField.getText(), SwapMacros.computeInt(this.guiTextField2.getText()), SwapMacros.computeInt2(this.guiTextField3.getText())
                    );
                    this.status = "Macro saved.";
                } else if (button.id == 1) {
                    this.count--;
                } else if (button.id == 2) {
                    this.count++;
                } else if (button.id == 3) {
                    instance.process28();
                } else {
                    if (button.id == 4) {
                        mc.displayGuiScreen(null);
                        return;
                    }

                    if (button.id >= 200) {
                        instance.handleEntry2(this.entries.get(button.id - 200));
                    } else if (button.id >= 100) {
                        SwapMacros.Entry entry = this.entries.get(button.id - 100);
                        instance.handleEntry(entry, !entry.enabled);
                    }
                }
            } catch (Exception e) {
                this.status = "§c" + e.getMessage();
            }

            this.process();
        }
    }
}
