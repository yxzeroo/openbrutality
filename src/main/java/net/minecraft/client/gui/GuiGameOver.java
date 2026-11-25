package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.gui.ChangelogScreen;

public class GuiGameOver extends GuiScreen implements GuiYesNoCallback {
    private int enableButtonsTimer;
    private boolean field_146346_f = false;

    @Override
    public void initGui() {
        this.buttonList.clear();
        if (mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
            if (mc.isIntegratedServerRunning()) {
                this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, I18n.format("deathScreen.deleteWorld")));
            } else {
                this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, I18n.format("deathScreen.leaveServer")));
            }
        } else {
            this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 72, I18n.format("deathScreen.respawn")));
            this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, I18n.format("deathScreen.titleScreen")));
            if (mc.getSession() == null) {
                this.buttonList.get(1).enabled = false;
            }
        }

        for (GuiButton button : this.buttonList) {
            button.enabled = false;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                mc.thePlayer.respawnPlayer();
                mc.displayGuiScreen((GuiScreen)null);
                break;
            case 1:
                if (mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
                    mc.displayGuiScreen(new ChangelogScreen());
                } else {
                    GuiYesNo gui = new GuiYesNo(
                        this, I18n.format("deathScreen.quit.confirm"), "", I18n.format("deathScreen.titleScreen"), I18n.format("deathScreen.respawn"), 0
                    );
                    mc.displayGuiScreen(gui);
                    gui.setButtonDelay(20);
                }
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if (result) {
            mc.theWorld.sendQuittingDisconnectingPacket();
            mc.loadWorld((WorldClient)null);
            mc.displayGuiScreen(new ChangelogScreen());
        } else {
            mc.thePlayer.respawnPlayer();
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawGradientRect(0, 0, this.width, this.height, 1615855616, -1602211792);
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        boolean hardcoreModeEnabled = mc.theWorld.getWorldInfo().isHardcoreModeEnabled();
        String str = hardcoreModeEnabled ? I18n.format("deathScreen.title.hardcore") : I18n.format("deathScreen.title");
        this.drawCenteredString(this.fontRendererObj, str, this.width / 2 / 2, 30, 16777215);
        GlStateManager.popMatrix();
        if (hardcoreModeEnabled) {
            this.drawCenteredString(this.fontRendererObj, I18n.format("deathScreen.hardcoreInfo"), this.width / 2, 144, 16777215);
        }

        this.drawCenteredString(
            this.fontRendererObj, I18n.format("deathScreen.score") + ": " + EnumChatFormatting.YELLOW + mc.thePlayer.getScore(), this.width / 2, 100, 16777215
        );
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.enableButtonsTimer++;
        if (this.enableButtonsTimer == 20) {
            for (GuiButton button : this.buttonList) {
                button.enabled = true;
            }
        }
    }
}
