package org.brutality.clickgui.discord;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.Gui;
import org.brutality.gui.font.atlas.SFProApi;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.lwjgl.opengl.GL11;

public class NameComponent {
    private final DiscordGUI discordGUI;
    private final Category category;
    private final List<DrawSettingsComponent> entries = new ArrayList<>();
    private final int count;
    private boolean flag;
    private int count2 = 5;

    protected void keyTyped(char typedChar, int keyCode) {
        for (DrawSettingsComponent drawSettingsComponent : this.entries) {
            drawSettingsComponent.keyTyped(typedChar, keyCode);
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        for (DrawSettingsComponent drawSettingsComponent : this.entries) {
            drawSettingsComponent.mouseReleased(mouseX, mouseY, state);
        }
    }

    public boolean isFlag() {
        return this.flag;
    }

    public NameComponent(DiscordGUI discordGUI, Category category, int count) {
        this.discordGUI = discordGUI;
        this.category = category;
        this.count = count;
        int n = 30;

        for (Module module : ModuleManager.getEntries2()) {
            if (module.getCategory() == category && !ModuleManager.matchesModule(module)) {
                this.entries.add(new DrawSettingsComponent(discordGUI, module, n));
                n += 18;
            }
        }
    }

    public Category getCategory() {
        return this.category;
    }

    private boolean matchesInt2(int n, int n2) {
        return n >= this.discordGUI.getCount4() + 45
            && n <= this.discordGUI.getCount4() + 45 + 110
            && n2 >= this.discordGUI.getCount5() + 22
            && n2 <= this.discordGUI.getCount5() + this.discordGUI.getHeight();
    }

    public void handleFlag(boolean flag) {
        this.flag = flag;
        if (!flag) {
            for (IsVisibleComponent isVisibleComponent : this.discordGUI.getDiscordComponent2().computeList()) {
                isVisibleComponent.setFlag(false);
                isVisibleComponent.setFocused(false);
            }
        }
    }

    public List<DrawSettingsComponent> getEntries() {
        return this.entries;
    }

    public boolean matchesInt(int n, int n2) {
        return n >= this.discordGUI.getCount4() + 8
            && n <= this.discordGUI.getCount4() + 35
            && n2 >= this.discordGUI.getCount5() + this.count - 15
            && n2 <= this.discordGUI.getCount5() + this.count + 15;
    }

    private String computeString() {
        switch (this.category) {
            case COMBAT:
                return "D";
            case MOVEMENT:
                return "A";
            case PLAYER:
                return "B";
            case VISUAL:
                return "C";
            case MISCELLANEOUS:
                return "F";
            case EXPLOIT:
                return "G";
            default:
                return null;
        }
    }

    public void drawScreen(int n, int n2) {
        int n3 = this.discordGUI.getCount4();
        int n4 = this.discordGUI.getCount5();
        if (this.flag && this.matchesInt2(n, n2)) {
            UpType upType = DiscordGUI.computeUpType();
            if (upType != null && !this.entries.isEmpty()) {
                switch (upType) {
                    case DOWN:
                        if (this.entries.get(this.entries.size() - 1).getY() > this.discordGUI.getHeight() - 14) {
                            for (DrawSettingsComponent drawSettingsComponent : this.entries) {
                                drawSettingsComponent.setY(drawSettingsComponent.getY() - 7);
                            }
                        }
                        break;
                    case UP:
                        if (this.entries.get(0).getY() < 30) {
                            for (DrawSettingsComponent drawSettingsComponent2 : this.entries) {
                                drawSettingsComponent2.setY(drawSettingsComponent2.getY() + 7);
                            }
                        }
                }
            }
        }

        int n5 = this.discordGUI.computeInt();
        boolean flag = this.matchesInt(n, n2);
        String str = this.name();
        if (flag) {
            Gui.drawRect(n3, n4 + this.count - (this.flag ? this.count2 : 5), n3 + 2, n4 + this.count + (this.flag ? this.count2 : 5), -1);
            DiscordComponent.handleFloat(
                n3 - SFProApi.Listener7.Entry3.apiApi.computeInt(str) - 12,
                n4 + this.count - 6,
                SFProApi.Listener7.Entry3.apiApi.computeInt(str) + 7,
                11.0F,
                5.0F,
                -13684945
            );
            SFProApi.Listener7.Entry3.apiApi.computeFloat2(str, n3 - SFProApi.Listener7.Entry3.apiApi.computeInt(str) - 9, n4 + this.count - 3, -1);
        } else if (this.flag) {
            Gui.drawRect(n3, n4 + this.count - this.count2, n3 + 2, n4 + this.count + this.count2, -1);
        }

        if (this.flag && this.count2 <= 10) {
            this.count2++;
        } else if (!this.flag) {
            this.count2 = 5;
        }

        DiscordComponent.handleFloat(n3 + 22, n4 + this.count, 15.0F, -13223617);
        String str2 = this.computeString();
        int n6 = flag ? n5 : -1;
        if (str2 == null) {
            SFProApi.Listener6.Entry4.apiApi.computeFloat(str.substring(0, 1).toUpperCase(Locale.ROOT), n3 + 22, n4 + this.count - 4, n6);
        } else {
            SFProApi.Listener.Entry3.apiApi.computeFloat(str2, n3 + 22, n4 + this.count - 5, n6);
        }

        if (this.flag) {
            if (str2 == null) {
                SFProApi.Listener6.Entry2.apiApi.computeFloat2(str.substring(0, 1).toUpperCase(Locale.ROOT), n3 + 50, n4 + 7, -1025);
            } else {
                SFProApi.Listener.Entry2.apiApi.computeFloat2(str2, n3 + (str2.equals("D") ? 49 : 50), n4 + 7, -1025);
            }

            SFProApi.Listener7.Entry7.apiApi.computeFloat2(str, n3 + 63, n4 + 7, -1025);
            GL11.glPushMatrix();
            this.discordGUI.process();
            var drawSettingsComponents = new ArrayList<>(this.entries);
            drawSettingsComponents.sort((drawSettingsComponent3, drawSettingsComponent4) -> Boolean.compare(drawSettingsComponent3.isFlag(), drawSettingsComponent4.isFlag()));

            for (DrawSettingsComponent drawSettingsComponent5 : drawSettingsComponents) {
                drawSettingsComponent5.handleInt(n, n2);
            }

            this.discordGUI.process2();
            GL11.glPopMatrix();

            for (DrawSettingsComponent drawSettingsComponent6 : this.entries) {
                drawSettingsComponent6.drawSettings(n, n2);
            }
        }
    }

    public String name() {
        String str = this.category.name();
        return str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1).toLowerCase(Locale.ROOT);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (DrawSettingsComponent drawSettingsComponent : this.entries) {
            drawSettingsComponent.mouseClicked(mouseX, mouseY, mouseButton);
        }

        boolean flag = mouseY >= this.discordGUI.getCount5() + 20 && mouseY <= this.discordGUI.getCount5() + this.discordGUI.getHeight();
        if (flag) {
            switch (mouseButton) {
                case 0:
                    for (DrawSettingsComponent drawSettingsComponent2 : this.entries) {
                        if (drawSettingsComponent2.matchesInt(mouseX, mouseY)) {
                            drawSettingsComponent2.getModule().process28();
                            break;
                        }
                    }
                    break;
                case 1:
                    for (DrawSettingsComponent drawSettingsComponent3 : this.entries) {
                        if (drawSettingsComponent3.matchesInt(mouseX, mouseY)) {
                            for (DrawSettingsComponent drawSettingsComponent4 : this.entries) {
                                if (drawSettingsComponent4 != drawSettingsComponent3) {
                                    drawSettingsComponent4.setFlag(false);
                                }
                            }

                            drawSettingsComponent3.setFlag(!drawSettingsComponent3.isFlag());
                            break;
                        }
                    }
            }
        }
    }
}
