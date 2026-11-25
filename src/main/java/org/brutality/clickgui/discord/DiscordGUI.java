package org.brutality.clickgui.discord;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import org.brutality.Brutality;
import org.brutality.clickgui.classic.ConfigsComponent;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.font.atlas.SFProApi.Listener.Entry3;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.client.ClickGUI;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class DiscordGUI extends CustomGuiScreen {
    private static final String text = "https://discord.gg/brutality";
    private static final int count = 35;
    private static final int count2 = 63;
    private final List<NameComponent> entries = new ArrayList<>();
    private final List<GetOffsetComponent> entries2 = new CopyOnWriteArrayList<>();
    private final DiscordComponent2 discordComponent2 = new DiscordComponent2(this);
    private final ConfigsComponent configsComponent = new ConfigsComponent(this);
    private final int count3;
    private int count4 = 100;
    private int count5 = 100;
    private int width2 = 190;
    private int height2 = 300;
    private boolean flag;
    private int count6;
    private int count7;
    private int count8;
    private int count9;
    private boolean dragging;
    private boolean flag2;

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        NameComponent nameComponent = this.computeNameComponent();
        if (keyCode == 1 && !this.matchesNameComponent(nameComponent) && !this.configsComponent.isFlag()) {
            mc.displayGuiScreen(null);
            if (mc.currentScreen == null) {
                mc.setIngameFocus();
            }
        } else {
            if (nameComponent != null) {
                nameComponent.keyTyped(typedChar, keyCode);
            }

            this.configsComponent.keyTyped(typedChar, keyCode);
        }
    }

    private void process3() {
        try {
            Desktop.getDesktop().browse(new URI("https://discord.gg/brutality"));
        } catch (Throwable ignored) {
        }
    }

    public static UpType computeUpType() {
        int dWheel = Mouse.getDWheel();
        if (dWheel > 0) {
            return UpType.UP;
        }
        return dWheel < 0 ? UpType.DOWN : null;
    }

    @Override
    public void onGuiClosed() {
        for (IsVisibleComponent isVisibleComponent : this.discordComponent2.computeList()) {
            isVisibleComponent.setDragging(false);
            isVisibleComponent.setFlag(false);
            isVisibleComponent.setFocused(false);
        }

        ClickGUI instance = ClickGUI.getInstance();
        if (instance != null) {
            instance.onGuiClosed();
        }
    }

    public void initConfigs() {
        this.entries2.clear();
        File file = Brutality.appdataHandler == null ? null : Brutality.appdataHandler.computeFile();
        if (file != null) {
            File[] files = file.listFiles((file2, str) -> str.endsWith(".json"));
            if (files != null) {
                for (File file3 : files) {
                    this.entries2.add(new GetOffsetComponent(this, file3.getName().substring(0, file3.getName().length() - 5)));
                }
            }
        }
    }

    public boolean isFlag() {
        return this.flag;
    }

    public NameComponent computeNameComponent() {
        for (NameComponent nameComponent : this.entries) {
            if (nameComponent.isFlag()) {
                return nameComponent;
            }
        }

        return null;
    }

    private boolean checkState() {
        for (NameComponent nameComponent : this.entries) {
            if (nameComponent.isFlag()) {
                return true;
            }
        }

        return false;
    }

    public int getWidth() {
        return this.width2;
    }

    private boolean matchesInt3(int n, int n2) {
        return n >= this.count4 + 7 && n <= this.count4 + 37 && n2 >= this.count5 + 5 && n2 <= this.count5 + 35;
    }

    public int getHeight() {
        return this.height2;
    }

    public DiscordComponent2 getDiscordComponent2() {
        return this.discordComponent2;
    }

    public int computeInt() {
        ClickGUI instance = ClickGUI.getInstance();
        return instance != null && instance.computeBrutalityHelper() != null ? instance.computeBrutalityHelper().getColor().getRGB() : -8225316;
    }

    private static List<Category> computeList() {
        ArrayList list = new ArrayList();

        for (Category category : Arrays.asList(
            Category.COMBAT,
            Category.MOVEMENT,
            Category.PLAYER,
            Category.VISUAL,
            Category.MISCELLANEOUS,
            Category.EXPLOIT,
            Category.PIT,
            Category.CLIENT,
            Category.SCRIPTS
        )) {
            for (Module module : ModuleManager.getEntries2()) {
                if (module.getCategory() == category && !ModuleManager.matchesModule(module)) {
                    list.add(category);
                    break;
                }
            }
        }

        return list;
    }

    private boolean matchesInt(int n, int n2) {
        return n >= this.count4 + 45 + 105 + this.width2 - 5
            && n <= this.count4 + 45 + 105 + this.width2
            && n2 >= this.count5 + this.height2 - 5
            && n2 <= this.count5 + this.height2;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.dragging) {
            this.count4 = this.count6 + mouseX;
            this.count5 = this.count7 + mouseY;
        }

        if (this.flag2) {
            if (this.count8 + mouseX > 190) {
                this.width2 = this.count8 + mouseX;
            }

            if (this.count9 + mouseY > this.count3) {
                this.height2 = this.count9 + mouseY;
            }
        }

        DiscordComponent.handleFloat(this.count4, this.count5 - 10, 150 + this.width2, this.count5 + 5, 8.0F, -14671323);
        DiscordComponent.handleFloat(this.count4, this.count5, 49.0F, this.height2, 6.0F, -14671323);
        drawRect(this.count4, this.count5, this.count4 + 10, this.count5 + 4, -14671323);
        DiscordComponent.handleFloat(this.count4 + 45 + 105, this.count5, this.width2, this.height2, 4.0F, -13223618);
        DiscordComponent.handleFloat(this.count4 + 7, this.count5 + 40, 31.0F, 3.0F, 0.0F, -13684426);
        drawRect(this.count4 + 44, this.count5, this.count4 + 45 + 110, this.count5 + this.height2, -13684426);
        drawRect(this.count4 + 44, this.count5 + 20, this.count4 + 45 + 105 + this.width2, this.count5 + 21, -14671323);
        if (!this.checkState() && !this.flag) {
            int n = -9801351;
            FontRenderer fontRenderer = mc.fontRendererObj;
            fontRenderer.drawStringWithShadow("<------------", this.count4 + 59, this.count5 + 65, n);
            fontRenderer.drawStringWithShadow("Select one of", this.count4 + 67, this.count5 + 75, n);
            fontRenderer.drawStringWithShadow("these", this.count4 + 85, this.count5 + 85, n);
            fontRenderer.drawStringWithShadow("-------------", this.count4 + 59, this.count5 + 95, n);
            fontRenderer.drawStringWithShadow("Enjoy the Brutality", this.count4 + 54, this.count5 + 105, n);
            fontRenderer.drawStringWithShadow("Experience", this.count4 + 70, this.count5 + 117, n);
            fontRenderer.drawStringWithShadow("B R U T A L I T Y", this.count4 + 64, this.count5 + 7, n);
            fontRenderer.drawStringWithShadow(
                "Build " + Brutality.version,
                this.count4 + 45 + 105 + (this.width2 / 2.0F - fontRenderer.getStringWidth("Build " + Brutality.version) / 2.0F),
                this.count5 + 7,
                n
            );
        }

        for (NameComponent nameComponent : this.entries) {
            nameComponent.drawScreen(mouseX, mouseY);
        }

        this.configsComponent.handleInt(mouseX, mouseY);
        DiscordComponent.handleFloat(this.count4 + 22, this.count5 + 20, 15.0F, -13223617);
        Entry3.apiApi.computeFloat2("?", this.count4 + 14, this.count5 + 14, -1);
        if (this.matchesInt3(mouseX, mouseY)) {
            int n2 = org.brutality.gui.font.atlas.SFProApi.Listener7.Entry3.apiApi.computeInt("Discord Server");
            DiscordComponent.handleFloat(this.count4 - n2 - 12, this.count5 + 14, n2 + 7, 10.0F, 5.0F, -13684945);
            org.brutality.gui.font.atlas.SFProApi.Listener7.Entry3.apiApi.computeFloat2("Discord Server", this.count4 - n2 - 10, this.count5 + 16, -1);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public DiscordGUI() {
        int n = 63;

        for (Category category : computeList()) {
            this.entries.add(new NameComponent(this, category, n));
            n += 35;
        }

        this.count3 = Math.max(300, n - 35 + 66);
        this.height2 = this.count3;
    }

    public int getCount4() {
        return this.count4;
    }

    public int getCount5() {
        return this.count5;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            for (NameComponent nameComponent : this.entries) {
                if (nameComponent.matchesInt(mouseX, mouseY)) {
                    for (NameComponent nameComponent2 : this.entries) {
                        nameComponent2.handleFlag(false);
                    }

                    nameComponent.handleFlag(true);
                    this.flag = false;
                }
            }

            if (this.matchesInt2(mouseX, mouseY)) {
                this.count6 = this.count4 - mouseX;
                this.count7 = this.count5 - mouseY;
                this.dragging = true;
            }

            if (this.matchesInt(mouseX, mouseY)) {
                this.count8 = this.width2 - mouseX;
                this.count9 = this.height2 - mouseY;
                this.flag2 = true;
            }

            if (this.configsComponent.matchesInt(mouseX, mouseY)) {
                if (!this.flag) {
                    this.initConfigs();
                }

                for (NameComponent nameComponent3 : this.entries) {
                    nameComponent3.handleFlag(false);
                }

                this.flag = true;
            }

            if (this.matchesInt3(mouseX, mouseY)) {
                this.process3();
            }
        }

        NameComponent nameComponent4 = this.computeNameComponent();
        if (nameComponent4 != null) {
            nameComponent4.mouseClicked(mouseX, mouseY, mouseButton);
        }

        if (this.flag) {
            this.configsComponent.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public void process2() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public List<GetOffsetComponent> getEntries2() {
        return this.entries2;
    }

    private boolean matchesNameComponent(NameComponent nameComponent) {
        if (nameComponent != null) {
            for (DrawSettingsComponent drawSettingsComponent : nameComponent.getEntries()) {
                if (drawSettingsComponent.isFlag2()) {
                    return true;
                }
            }
        }

        for (IsVisibleComponent isVisibleComponent : this.discordComponent2.computeList()) {
            if (isVisibleComponent.isVisible() && (isVisibleComponent.getGroupType() != GroupType.GROUP && isVisibleComponent.isFlag() || isVisibleComponent.isFocused())) {
                return true;
            }
        }

        return false;
    }

    private boolean matchesInt2(int n, int n2) {
        return n >= this.count4 && n <= this.count4 + 45 + 105 + this.width2 && n2 >= this.count5 - 7 && n2 <= this.count5 + 20;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void process() {
        int scaleFactor = new ScaledResolution(mc).getScaleFactor();
        GL11.glScissor(0, mc.displayHeight - (this.count5 + this.height2) * scaleFactor, mc.displayWidth, Math.max(0, (this.height2 - 21) * scaleFactor));
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            this.dragging = false;
            this.flag2 = false;
        }

        NameComponent nameComponent = this.computeNameComponent();
        if (nameComponent != null) {
            nameComponent.mouseReleased(mouseX, mouseY, state);
        }
    }
}
