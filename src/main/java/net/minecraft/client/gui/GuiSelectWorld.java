package net.minecraft.client.gui;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.StyledGuiButton;
import org.brutality.gui.OceanComponent;

public class GuiSelectWorld extends CustomGuiScreen implements GuiYesNoCallback {
    private static final Logger logger = LogManager.getLogger();
    final DateFormat field_146633_h = new SimpleDateFormat();
    protected GuiScreen parentScreen;
    protected String field_146628_f = "Select world";
    private boolean field_146634_i;
    int field_146640_r;
    java.util.List<SaveFormatComparator> field_146639_s;
    private GuiSelectWorld.List field_146638_t;
    String field_146637_u;
    String field_146636_v;
    String[] field_146635_w = new String[4];
    private boolean field_146643_x;
    GuiButton deleteButton;
    GuiButton selectButton;
    GuiButton renameButton;
    GuiButton recreateButton;

    public GuiSelectWorld(GuiScreen parentScreenIn) {
        this.parentScreen = parentScreenIn;
    }

    @Override
    public void initGui() {
        this.field_146628_f = I18n.format("selectWorld.title");

        try {
            this.func_146627_h();
        } catch (AnvilConverterException e) {
            logger.error("Couldn't load level list", (Throwable)e);
            mc.displayGuiScreen(new GuiErrorScreen("Unable to load worlds", e.getMessage()));
            return;
        }

        this.field_146637_u = I18n.format("selectWorld.world");
        this.field_146636_v = I18n.format("selectWorld.conversion");
        this.field_146635_w[GameType.SURVIVAL.getID()] = I18n.format("gameMode.survival");
        this.field_146635_w[GameType.CREATIVE.getID()] = I18n.format("gameMode.creative");
        this.field_146635_w[GameType.ADVENTURE.getID()] = I18n.format("gameMode.adventure");
        this.field_146635_w[GameType.SPECTATOR.getID()] = I18n.format("gameMode.spectator");
        this.field_146638_t = new GuiSelectWorld.List(mc);
        this.field_146638_t.registerScrollButtons(4, 5);
        this.func_146618_g();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.field_146638_t.handleMouseInput();
    }

    private void func_146627_h() throws AnvilConverterException {
        ISaveFormat saveLoader = mc.getSaveLoader();
        this.field_146639_s = saveLoader.getSaveList();
        Collections.sort(this.field_146639_s);
        this.field_146640_r = -1;
    }

    protected String func_146621_a(int p_146621_1_) {
        return this.field_146639_s.get(p_146621_1_).getFileName();
    }

    protected String func_146614_d(int p_146614_1_) {
        String displayName = this.field_146639_s.get(p_146614_1_).getDisplayName();
        if (StringUtils.isEmpty(displayName)) {
            displayName = I18n.format("selectWorld.world") + " " + (p_146614_1_ + 1);
        }

        return displayName;
    }

    public void func_146618_g() {
        this.buttonList.add(this.selectButton = new StyledGuiButton(1, this.width / 2 - 154, this.height - 52, 150, 20, I18n.format("selectWorld.select")));
        this.buttonList.add(new StyledGuiButton(3, this.width / 2 + 4, this.height - 52, 150, 20, I18n.format("selectWorld.create")));
        this.buttonList.add(this.renameButton = new StyledGuiButton(6, this.width / 2 - 154, this.height - 28, 72, 20, I18n.format("selectWorld.rename")));
        this.buttonList.add(this.deleteButton = new StyledGuiButton(2, this.width / 2 - 76, this.height - 28, 72, 20, I18n.format("selectWorld.delete")));
        this.buttonList.add(this.recreateButton = new StyledGuiButton(7, this.width / 2 + 4, this.height - 28, 72, 20, I18n.format("selectWorld.recreate")));
        this.buttonList.add(new StyledGuiButton(0, this.width / 2 + 82, this.height - 28, 72, 20, I18n.format("gui.cancel")));
        this.selectButton.enabled = false;
        this.deleteButton.enabled = false;
        this.renameButton.enabled = false;
        this.recreateButton.enabled = false;
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 2) {
                String str = this.func_146614_d(this.field_146640_r);
                if (str != null) {
                    this.field_146643_x = true;
                    GuiYesNo gui = func_152129_a(this, str, this.field_146640_r);
                    mc.displayGuiScreen(gui);
                }
            } else if (button.id == 1) {
                this.func_146615_e(this.field_146640_r);
            } else if (button.id == 3) {
                mc.displayGuiScreen(new GuiCreateWorld(this));
            } else if (button.id == 6) {
                mc.displayGuiScreen(new GuiRenameWorld(this, this.func_146621_a(this.field_146640_r)));
            } else if (button.id == 0) {
                mc.displayGuiScreen(this.parentScreen);
            } else if (button.id == 7) {
                GuiCreateWorld gui2 = new GuiCreateWorld(this);
                ISaveHandler saveLoader = mc.getSaveLoader().getSaveLoader(this.func_146621_a(this.field_146640_r), false);
                WorldInfo worldInfo = saveLoader.loadWorldInfo();
                saveLoader.flush();
                gui2.func_146318_a(worldInfo);
                mc.displayGuiScreen(gui2);
            } else {
                this.field_146638_t.actionPerformed(button);
            }
        }
    }

    public void func_146615_e(int p_146615_1_) {
        mc.displayGuiScreen((GuiScreen)null);
        if (!this.field_146634_i) {
            this.field_146634_i = true;
            String str = this.func_146621_a(p_146615_1_);
            if (str == null) {
                str = "World" + p_146615_1_;
            }

            String str2 = this.func_146614_d(p_146615_1_);
            if (str2 == null) {
                str2 = "World" + p_146615_1_;
            }

            if (mc.getSaveLoader().canLoadWorld(str)) {
                mc.launchIntegratedServer(str, str2, (WorldSettings)null);
            }
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if (this.field_146643_x) {
            this.field_146643_x = false;
            if (result) {
                ISaveFormat saveLoader = mc.getSaveLoader();
                saveLoader.flushCache();
                saveLoader.deleteWorldDirectory(this.func_146621_a(id));

                try {
                    this.func_146627_h();
                } catch (AnvilConverterException e) {
                    logger.error("Couldn't load level list", (Throwable)e);
                }
            }

            mc.displayGuiScreen(this);
        }
    }

    @Override
    public void drawDefaultBackground() {
        OceanComponent.handleGuiScreen(this, 0, 0, 0.0F);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.field_146638_t.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, this.field_146628_f, this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public static GuiYesNo func_152129_a(GuiYesNoCallback p_152129_0_, String p_152129_1_, int p_152129_2_) {
        String str = I18n.format("selectWorld.deleteQuestion");
        String str2 = "'" + p_152129_1_ + "' " + I18n.format("selectWorld.deleteWarning");
        String str3 = I18n.format("selectWorld.deleteButton");
        String str4 = I18n.format("gui.cancel");
        return new GuiYesNo(p_152129_0_, str, str2, str3, str4, p_152129_2_);
    }

    class List extends GuiSlot {
        public List(Minecraft mc) {
            super(mc, GuiSelectWorld.this.width, GuiSelectWorld.this.height, 32, GuiSelectWorld.this.height - 64, 36);
        }

        @Override
        protected int getSize() {
            return GuiSelectWorld.this.field_146639_s.size();
        }

        @Override
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
            GuiSelectWorld.this.field_146640_r = slotIndex;
            boolean flag = GuiSelectWorld.this.field_146640_r >= 0 && GuiSelectWorld.this.field_146640_r < this.getSize();
            GuiSelectWorld.this.selectButton.enabled = flag;
            GuiSelectWorld.this.deleteButton.enabled = flag;
            GuiSelectWorld.this.renameButton.enabled = flag;
            GuiSelectWorld.this.recreateButton.enabled = flag;
            if (isDoubleClick && flag) {
                GuiSelectWorld.this.func_146615_e(slotIndex);
            }
        }

        @Override
        protected boolean isSelected(int slotIndex) {
            return slotIndex == GuiSelectWorld.this.field_146640_r;
        }

        @Override
        protected int getContentHeight() {
            return GuiSelectWorld.this.field_146639_s.size() * 36;
        }

        @Override
        protected void drawBackground() {
        }

        @Override
        protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn) {
            SaveFormatComparator saveFormatComparator = GuiSelectWorld.this.field_146639_s.get(entryID);
            String displayName = saveFormatComparator.getDisplayName();
            if (StringUtils.isEmpty(displayName)) {
                displayName = GuiSelectWorld.this.field_146637_u + " " + (entryID + 1);
            }

            String fileName = saveFormatComparator.getFileName();
            fileName = fileName + " (" + GuiSelectWorld.this.field_146633_h.format(new Date(saveFormatComparator.getLastTimePlayed()));
            String str = fileName + ")";
            String str2 = "";
            if (saveFormatComparator.requiresConversion()) {
                str2 = GuiSelectWorld.this.field_146636_v + " " + str2;
            } else {
                str2 = GuiSelectWorld.this.field_146635_w[saveFormatComparator.getEnumGameType().getID()];
                if (saveFormatComparator.isHardcoreModeEnabled()) {
                    str2 = EnumChatFormatting.DARK_RED + I18n.format("gameMode.hardcore") + EnumChatFormatting.RESET;
                }

                if (saveFormatComparator.getCheatsEnabled()) {
                    str2 = str2 + ", " + I18n.format("selectWorld.cheats");
                }
            }

            GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, displayName, p_180791_2_ + 2, p_180791_3_ + 1, 16777215);
            GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, str, p_180791_2_ + 2, p_180791_3_ + 12, 8421504);
            GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, str2, p_180791_2_ + 2, p_180791_3_ + 22, 8421504);
        }
    }
}
