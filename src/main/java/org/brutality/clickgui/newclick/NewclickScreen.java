package org.brutality.clickgui.newclick;

import java.io.IOException;
import org.brutality.clickgui.discord.Inter18ptSemiboldTtfComponent;
import org.brutality.gui.CustomGuiScreen;
import org.lwjgl.input.Mouse;

public class NewclickScreen extends CustomGuiScreen {
    private final NewClickGUI newClickGUI;
    private final ThemePickerPopup rComponent = new ThemePickerPopup();
    private final Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent = new Inter18ptSemiboldTtfComponent();

    public NewclickScreen(NewClickGUI newClickGUI) {
        this.newClickGUI = newClickGUI;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int eventDWheel = Mouse.getEventDWheel();
        if (eventDWheel != 0) {
            int n = eventDWheel > 0 ? 18 : -18;
            this.rComponent.handleInt(n, Math.min(340, this.height - 68));
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.rComponent.matchesInt2(this.width, this.height, mouseX, mouseY, mouseButton)) {
            mc.displayGuiScreen(this.newClickGUI);
        } else {
            this.rComponent.matchesInt(this.width, this.height, mouseX, mouseY, mouseButton);
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.rComponent.setOpen2(true);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(this.newClickGUI);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.rComponent.matchesInt(this.width, this.height, mouseX, mouseY, this.inter18ptSemiboldTtfComponent);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
