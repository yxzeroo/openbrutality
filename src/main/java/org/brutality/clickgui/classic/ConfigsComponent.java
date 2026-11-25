package org.brutality.clickgui.classic;

import java.io.File;
import java.util.List;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ChatAllowedCharacters;
import org.brutality.Brutality;
import org.brutality.clickgui.discord.DiscordComponent;
import org.brutality.clickgui.discord.DiscordGUI;
import org.brutality.clickgui.discord.GetOffsetComponent;
import org.brutality.clickgui.discord.UpType;
import org.brutality.gui.font.atlas.SFProApi;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;
import org.brutality.utils.timer.TimerUtil;
import org.lwjgl.input.Keyboard;

public class ConfigsComponent {
    private final DiscordGUI discordGUI;
    private final TimerUtil timerUtil = new TimerUtil();
    private String message = "";
    private boolean flag;
    private int count = 5;
    private static final long timestamp = 100L;

    public boolean matchesInt3(int n, int n2) {
        return this.matchesInt(n, n2, 26, 40);
    }

    public ConfigsComponent(DiscordGUI discordGUI) {
        this.discordGUI = discordGUI;
    }

    public boolean matchesInt6(int n, int n2) {
        return this.matchesInt(n, n2, 80, 94);
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 63) {
            this.discordGUI.initConfigs();
        } else if (this.flag) {
            if (keyCode == 1) {
                this.flag = false;
            } else if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                this.message = this.message + typedChar;
            }
        }
    }

    private void handleString(String str) {
        File file = new File(Brutality.appdataHandler.computeFile(), str + ".json");
        if (file.exists()) {
            file.delete();
        }
    }

    public boolean matchesInt(int n, int n2) {
        int n3 = this.computeInt();
        return n >= this.discordGUI.getCount4() + 8
            && n <= this.discordGUI.getCount4() + 35
            && n2 >= this.discordGUI.getCount5() + n3 - 15
            && n2 <= this.discordGUI.getCount5() + n3 + 15;
    }

    public boolean isFlag() {
        return this.flag;
    }

    public boolean matchesInt4(int n, int n2) {
        return this.matchesInt(n, n2, 62, 76);
    }

    public boolean matchesInt5(int n, int n2) {
        return this.matchesInt(n, n2, 44, 58);
    }

    private boolean matchesInt(int n, int n2, int n3, int n4) {
        return n >= this.discordGUI.getCount4() + 44
            && n <= this.discordGUI.getCount4() + 45 + 110
            && n2 >= this.discordGUI.getCount5() + n3
            && n2 <= this.discordGUI.getCount5() + n4;
    }

    public void handleInt(int n, int n2) {
        int n3 = this.discordGUI.getCount4();
        int n4 = this.discordGUI.getCount5();
        int n5 = this.computeInt();
        var getOffsetComponents = this.discordGUI.getEntries2();
        UpType upType = DiscordGUI.computeUpType();
        if (upType != null && this.discordGUI.isFlag() && !getOffsetComponents.isEmpty()) {
            switch (upType) {
                case DOWN:
                    if (((GetOffsetComponent)getOffsetComponents.get(getOffsetComponents.size() - 1)).getY() > n4 + this.discordGUI.getHeight() - 14) {
                        for (GetOffsetComponent getOffsetComponent : getOffsetComponents) {
                            getOffsetComponent.setOffset2(getOffsetComponent.getOffset() - 7);
                        }
                    }
                    break;
                case UP:
                    if (((GetOffsetComponent)getOffsetComponents.get(0)).getOffset() < 30) {
                        for (GetOffsetComponent getOffsetComponent2 : getOffsetComponents) {
                            getOffsetComponent2.setOffset2(getOffsetComponent2.getOffset() + 7);
                        }
                    }
            }
        }

        if (this.discordGUI.isFlag() && this.count <= 10) {
            this.count++;
        } else if (!this.discordGUI.isFlag()) {
            this.count = 5;
        }

        boolean flag = this.matchesInt(n, n2);
        if (this.discordGUI.isFlag()) {
            Gui.drawRect(n3, n4 + n5 - this.count, n3 + 2, n4 + n5 + this.count, -1);
        } else if (flag) {
            Gui.drawRect(n3, n4 + n5 - 5, n3 + 2, n4 + n5 + 5, -1);
        }

        DiscordComponent.handleFloat(n3 + 22, n4 + n5, 15.0F, -13223617);
        SFProApi.Listener.Entry3.apiApi.computeFloat2("E", n3 + 16, n4 + n5 - 6, flag ? this.discordGUI.computeInt() : -1);
        if (this.discordGUI.isFlag()) {
            boolean flag2 = this.checkState();
            if (flag2 && this.matchesInt3(n, n2)) {
                Gui.drawRect(n3 + 44, n4 + 26, n3 + 45 + 110, n4 + 40, -13223618);
            } else if (flag2 && this.matchesInt5(n, n2)) {
                Gui.drawRect(n3 + 44, n4 + 44, n3 + 45 + 110, n4 + 58, -13223618);
            } else if (this.matchesInt4(n, n2)) {
                Gui.drawRect(n3 + 44, n4 + 62, n3 + 45 + 110, n4 + 76, -13223618);
            } else if (this.matchesInt6(n, n2)) {
                Gui.drawRect(n3 + 44, n4 + 80, n3 + 45 + 110, n4 + 94, -13223618);
            }

            SFProApi.Listener.Entry2.apiApi.computeFloat2("I", n3 + 50, n4 + 7, -1);
            SFProApi.Listener7.Entry7.apiApi.computeFloat2("Configs", n3 + 63, n4 + 7, -1);
            int n6 = flag2 ? -7961722 : -11646130;
            SFProApi.Listener6.Entry6.apiApi.computeFloat2("#", n3 + 50, n4 + 28, n6);
            SFProApi.Listener6.Entry6.apiApi.computeFloat2("#", n3 + 50, n4 + 46, n6);
            SFProApi.Listener6.Entry6.apiApi.computeFloat2("#", n3 + 50, n4 + 64, -7961722);
            SFProApi.Listener6.Entry6.apiApi.computeFloat2("#", n3 + 50, n4 + 82, -7961722);
            SFProApi.Listener7.Entry7.apiApi.computeFloat2("Load", n3 + 63, n4 + 30, n6);
            SFProApi.Listener7.Entry7.apiApi.computeFloat2("Delete", n3 + 63, n4 + 48, n6);
            SFProApi.Listener7.Entry7.apiApi.computeFloat2("Save", n3 + 63, n4 + 66, -7961722);
            SFProApi.Listener7.Entry7.apiApi.computeFloat2("Refresh", n3 + 63, n4 + 84, -7961722);
            if (this.flag && Keyboard.isKeyDown(Keyboard.KEY_BACK) && this.timerUtil.matchesLong4(timestamp) && !this.message.isEmpty()) {
                this.message = this.message.substring(0, this.message.length() - 1);
                this.timerUtil.reset();
            }

            DiscordComponent.handleFloat(
                n3 + 50, n4 + 100, n3 + 120, n4 + 110, 1.0F, !this.matchesInt2(n, n2) && !this.flag ? 1677721600 : -8225316, -13684945
            );
            if (this.message.isEmpty()) {
                SFProApi.Listener7.Entry3.apiApi.computeFloat2("Config name", n3 + 52, n4 + 102, 1694498815);
            } else if (SFProApi.Listener7.Entry3.apiApi.computeInt(this.message) > 65) {
                SFProApi.Listener7.Entry3.apiApi
                    .computeFloat2(SFProApi.Listener7.Entry3.apiApi.computeString(this.message, 60, true), n3 + 52, n4 + 102, -1);
            } else {
                SFProApi.Listener7.Entry3.apiApi.computeFloat2(this.message, n3 + 52, n4 + 102, -1);
            }

            for (GetOffsetComponent getOffsetComponent3 : getOffsetComponents) {
                getOffsetComponent3.update();
                if (!getOffsetComponent3.checkState()) {
                    getOffsetComponent3.handleInt(n, n2);
                }
            }
        }
    }

    public boolean matchesInt2(int n, int n2) {
        return n >= this.discordGUI.getCount4() + 50
            && n <= this.discordGUI.getCount4() + 120
            && n2 >= this.discordGUI.getCount5() + 100
            && n2 <= this.discordGUI.getCount5() + 110;
    }

    private int computeInt() {
        return this.discordGUI.getHeight() - 26;
    }

    private boolean checkState() {
        for (GetOffsetComponent getOffsetComponent : this.discordGUI.getEntries2()) {
            if (getOffsetComponent.isFlag()) {
                return true;
            }
        }

        return false;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            var getOffsetComponents = this.discordGUI.getEntries2();
            boolean flag = false;

            for (GetOffsetComponent getOffsetComponent : getOffsetComponents) {
                if (getOffsetComponent.matchesInt(mouseX, mouseY)) {
                    for (GetOffsetComponent getOffsetComponent2 : getOffsetComponents) {
                        getOffsetComponent2.setFlag(false);
                    }

                    getOffsetComponent.setFlag(true);
                } else if (getOffsetComponent.isFlag()) {
                    if (this.matchesInt3(mouseX, mouseY)) {
                        Brutality.appdataHandler.matchesString(getOffsetComponent.getName());
                        this.discordGUI.initConfigs();
                    } else if (this.matchesInt5(mouseX, mouseY)) {
                        this.handleString(getOffsetComponent.getName());
                        this.discordGUI.initConfigs();
                    } else if (this.matchesInt4(mouseX, mouseY)) {
                        Brutality.appdataHandler.matchesString2(getOffsetComponent.getName());
                        this.discordGUI.initConfigs();
                        flag = true;
                    }
                }
            }

            if (!flag && this.matchesInt4(mouseX, mouseY)) {
                if (this.message.length() > 25) {
                    NotificationFeedHandler.handleString("§cConfig name is too long§f.");
                } else if (!this.message.isEmpty()) {
                    Brutality.appdataHandler.matchesString2(this.message);
                    this.discordGUI.initConfigs();
                }
            } else if (this.matchesInt6(mouseX, mouseY)) {
                this.discordGUI.initConfigs();
            }

            if (this.matchesInt2(mouseX, mouseY)) {
                this.flag = !this.flag;
            } else if (this.flag) {
                this.flag = false;
            }
        }
    }
}
