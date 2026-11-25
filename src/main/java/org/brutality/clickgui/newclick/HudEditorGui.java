package org.brutality.clickgui.newclick;

import java.io.IOException;
import net.minecraft.client.gui.GuiChat;
import org.brutality.module.impl.visual.Notifications;
import org.brutality.notification.modern.ModernHandler2;
import org.brutality.utils.EditableModulesHelper;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.render.RenderHelper;

public class HudEditorGui extends GuiChat {
    private static final long timestamp = 6500583679964741632L;

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        HudDragHelper.process();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        super.initGui();
        EditableModulesHelper.process();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, this.width, this.height, (int)timestamp);

        try (RenderHelper renderHelper = new RenderHelper()) {
            EditableModulesHelper.handleFloat(partialTicks);
            EditableModulesHelper.process2();
            Notifications instance = Notifications.getInstance();
            if (instance != null && instance.checkState44()) {
                instance.process();
            }

            ModernHandler2.render();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onGuiClosed() {
        EditableModulesHelper.end();
        super.onGuiClosed();
    }
}
