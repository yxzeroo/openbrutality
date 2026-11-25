package org.brutality.clickgui.classic;

import java.io.IOException;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.brutality.gui.CustomGuiScreen;

public final class ConfirmScreen extends CustomGuiScreen {
    private final String text;
    private final String text2;
    private final Consumer<String> consumer;
    private final GuiScreen guiScreen;
    private GuiTextField guiTextField;
    private GuiButton guiButton;
    private static final long timestamp = 5314818309927993343L;

    public static void handleString(String str, String str2, Consumer<String> callback, GuiScreen screen) {
        Minecraft.getMinecraft().displayGuiScreen(new ConfirmScreen(str, str2, callback, screen));
    }

    @Override
    public void updateScreen() {
        this.guiTextField.updateCursorCounter();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        this.guiTextField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button == this.guiButton && this.consumer != null) {
            this.consumer.accept(this.guiTextField.getText());
        }

        mc.displayGuiScreen(this.guiScreen);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.guiTextField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private ConfirmScreen(String text, String str, Consumer<String> consumer, GuiScreen guiScreen) {
        this.text = text;
        this.text2 = str;
        this.consumer = consumer;
        this.guiScreen = guiScreen;
    }

    @Override
    public void initGui() {
        int n = this.width / 2;
        int n2 = this.height / 2;
        this.guiTextField = new GuiTextField(0, this.fontRendererObj, n - 100, n2 - 10, 200, 20);
        this.guiTextField.setText(this.text2);
        this.guiTextField.setFocused(true);
        this.buttonList.add(this.guiButton = new GuiButton(0, n - 100, n2 + 20, 95, 20, "Confirm"));
        this.buttonList.add(new GuiButton(1, n + 5, n2 + 20, 95, 20, "Cancel"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.text, this.width / 2, this.height / 2 - 35, (int)timestamp);
        this.guiTextField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
