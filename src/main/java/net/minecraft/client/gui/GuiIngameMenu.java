package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import org.brutality.gui.ChangelogScreen;
import org.brutality.module.impl.pit.hypixel.utility.SafetyMeasures;

public class GuiIngameMenu extends GuiScreen {
    private int field_146445_a;
    private int field_146444_f;

    @Override
    public void initGui() {
        this.field_146445_a = 0;
        this.buttonList.clear();
        byte b = -16;
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + b, I18n.format("menu.returnToMenu")));
        if (!mc.isIntegratedServerRunning()) {
            this.buttonList.get(0).displayString = I18n.format("menu.disconnect");
        }

        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 + b, I18n.format("menu.returnToGame")));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + b, 98, 20, I18n.format("menu.options")));
        GuiButton button;
        this.buttonList.add(button = new GuiButton(7, this.width / 2 + 2, this.height / 4 + 96 + b, 98, 20, I18n.format("menu.shareToLan")));
        this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + b, 98, 20, I18n.format("gui.achievements")));
        this.buttonList.add(new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 + b, 98, 20, I18n.format("gui.stats")));
        button.enabled = mc.isSingleplayer() && !mc.getIntegratedServer().getPublic();
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                break;
            case 1:
                if (SafetyMeasures.checkState()) {
                    return;
                }

                boolean integratedServerRunning = mc.isIntegratedServerRunning();
                boolean flag = mc.func_181540_al();
                button.enabled = false;
                mc.theWorld.sendQuittingDisconnectingPacket();
                mc.loadWorld((WorldClient)null);
                if (integratedServerRunning) {
                    mc.displayGuiScreen(new ChangelogScreen());
                } else if (flag) {
                    RealmsBridge realmsBridge = new RealmsBridge();
                    realmsBridge.switchToRealms(new ChangelogScreen());
                } else {
                    mc.displayGuiScreen(new GuiMultiplayer(new ChangelogScreen()));
                }
            case 2:
            case 3:
            default:
                break;
            case 4:
                mc.displayGuiScreen((GuiScreen)null);
                mc.setIngameFocus();
                break;
            case 5:
                mc.displayGuiScreen(new GuiAchievements(this, mc.thePlayer.getStatFileWriter()));
                break;
            case 6:
                mc.displayGuiScreen(new GuiStats(this, mc.thePlayer.getStatFileWriter()));
                break;
            case 7:
                mc.displayGuiScreen(new GuiShareToLan(this));
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.field_146444_f++;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("menu.game"), this.width / 2, 40, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
