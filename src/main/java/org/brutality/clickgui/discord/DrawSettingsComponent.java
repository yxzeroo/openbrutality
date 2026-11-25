package org.brutality.clickgui.discord;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import org.brutality.gui.font.atlas.SFProApi;
import org.brutality.module.Module;
import org.lwjgl.opengl.GL11;

public class DrawSettingsComponent {
    private final DiscordGUI discordGUI;
    private final Module module;
    private boolean flag;
    private boolean flag2;
    private int y2;

    public boolean isFlag() {
        return this.flag;
    }

    public int getY() {
        return this.y2;
    }

    public Module getModule() {
        return this.module;
    }

    protected void keyTyped(char typedChar, int keyCode) {
        if (this.flag2) {
            this.module.handleInt4(keyCode == 1 ? 0 : keyCode);
            this.flag2 = false;
        } else if (this.flag) {
            for (IsVisibleComponent isVisibleComponent : this.discordGUI.getDiscordComponent2().computeList(this.module)) {
                isVisibleComponent.keyTyped(typedChar, keyCode);
            }
        }
    }

    public void setY(int y2) {
        this.y2 = y2;
    }

    public void handleInt(int n, int n2) {
        int n3 = this.discordGUI.getCount4();
        int n4 = this.discordGUI.getCount5();
        if (this.matchesInt(n, n2) || this.flag) {
            float f = n4 + this.y2 + SFProApi.Listener7.Entry7.apiApi.getHeight() + 4;
            Gui.drawRect(
                n3 + 45,
                n4 + this.y2 - 4,
                n3 + 45 + 110,
                (int)(f > n4 + this.discordGUI.getHeight() ? MathHelper.clamp_float(f, n4 + this.y2 - 4, n4 + this.discordGUI.getHeight()) : f),
                -13223618
            );
        }

        SFProApi.Listener6.Entry6.apiApi.computeFloat2("#", n3 + 50, n4 + this.y2 - 2, -10461856);
        String str = this.flag2 ? "..." : this.module.getName();
        SFProApi.Listener7.Entry7.apiApi.computeFloat2(str, n3 + 63, n4 + this.y2, this.module.checkState44() ? -1843229 : -7961722);
    }

    public boolean matchesInt(int n, int n2) {
        int n3 = this.discordGUI.getCount4();
        int n4 = this.discordGUI.getCount5();
        return n >= n3 + 45
            && n <= n3 + 45 + 110
            && n2 >= n4 + this.y2 - 4
            && n2 <= n4 + this.y2 + SFProApi.Listener7.Entry7.apiApi.getHeight() + 4;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void drawSettings(int n, int n2) {
        if (this.flag) {
            var isVisibleComponents = this.discordGUI.getDiscordComponent2().computeList(this.module);
            UpType upType = DiscordGUI.computeUpType();
            if (upType != null && !isVisibleComponents.isEmpty()) {
                switch (upType) {
                    case DOWN:
                        if (((IsVisibleComponent)isVisibleComponents.get(isVisibleComponents.size() - 1)).getY() > this.discordGUI.getCount5() + this.discordGUI.getHeight() - 14) {
                            for (IsVisibleComponent isVisibleComponent : isVisibleComponents) {
                                isVisibleComponent.setOffset2(isVisibleComponent.getOffset() - 5);
                            }
                        }
                        break;
                    case UP:
                        if (((IsVisibleComponent)isVisibleComponents.get(0)).getY() < this.discordGUI.getCount5() + 30) {
                            for (IsVisibleComponent isVisibleComponent2 : isVisibleComponents) {
                                isVisibleComponent2.setOffset2(isVisibleComponent2.getOffset() + 5);
                            }
                        }
                }
            }

            SFProApi.Listener7.Entry7.apiApi
                .computeFloat2(this.module.getName() + " Settings", this.discordGUI.getCount4() + 165, this.discordGUI.getCount5() + 6, -1);
            if (isVisibleComponents.isEmpty()) {
                String str = "NO SETTINGS ;(";
                FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
                fontRenderer.drawStringWithShadow(
                    str,
                    this.discordGUI.getCount4() + 150 + (this.discordGUI.getWidth() - fontRenderer.getStringWidth(str)) / 2.0F,
                    this.discordGUI.getCount5() + (this.discordGUI.getHeight() - fontRenderer.FONT_HEIGHT) / 2.0F,
                    -9801351
                );
            } else {
                GL11.glPushMatrix();
                this.discordGUI.process();
                isVisibleComponents.sort((isVisibleComponent3, isVisibleComponent4) -> Boolean.compare(isVisibleComponent3.isFlag(), isVisibleComponent4.isFlag()));

                for (IsVisibleComponent isVisibleComponent5 : isVisibleComponents) {
                    isVisibleComponent5.update();
                    isVisibleComponent5.handleInt(n, n2);
                }

                this.discordGUI.process2();
                GL11.glPopMatrix();
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.flag) {
            for (IsVisibleComponent isVisibleComponent : this.discordGUI.getDiscordComponent2().computeList(this.module)) {
                if (isVisibleComponent.checkState()) {
                    isVisibleComponent.mouseReleased(mouseX, mouseY, state);
                }
            }
        }
    }

    public boolean isFlag2() {
        return this.flag2;
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseY >= this.discordGUI.getCount5() + 20
            && mouseY <= this.discordGUI.getCount5() + this.discordGUI.getHeight()
            && this.matchesInt(mouseX, mouseY)
            && mouseButton == 2) {
            NameComponent nameComponent = this.discordGUI.computeNameComponent();
            if (nameComponent != null) {
                for (DrawSettingsComponent drawSettingsComponent : nameComponent.getEntries()) {
                    drawSettingsComponent.flag2 = false;
                }

                this.flag2 = true;
            }
        }

        if (this.flag) {
            for (IsVisibleComponent isVisibleComponent : this.discordGUI.getDiscordComponent2().computeList(this.module)) {
                if (mouseY >= this.discordGUI.getCount5() + 20
                    && mouseY <= this.discordGUI.getCount5() + this.discordGUI.getHeight()
                    && isVisibleComponent.mouseClicked(mouseX, mouseY, mouseButton)) {
                    return;
                }
            }
        }
    }

    public DrawSettingsComponent(DiscordGUI discordGUI, Module module, int y2) {
        this.discordGUI = discordGUI;
        this.module = module;
        this.y2 = y2;
    }
}
