package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.stream.GuiTwitchUserMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityList;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent.Action;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brutality.gui.OceanComponent;
import org.brutality.gui.chat.ChatComponent;
import org.brutality.gui.chat.ParseIntComponent;
import org.brutality.module.impl.pit.hypixel.utility.MiddleClickHistory;
import org.brutality.utils.UtilsHelper;
import org.brutality.utils.player.StartHotbarMovesHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tv.twitch.chat.ChatUserInfo;

public abstract class GuiScreen extends Gui implements GuiYesNoCallback {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<String> PROTOCOLS = Sets.newHashSet("http", "https");
    private static final Splitter NEWLINE_SPLITTER = Splitter.on('\n');
    public static Minecraft mc;
    protected RenderItem itemRender;
    public int width;
    public int height;
    public List<GuiButton> buttonList = Lists.newArrayList();
    protected List<GuiLabel> labelList = Lists.newArrayList();
    public boolean allowUserInput;
    public FontRenderer fontRendererObj;
    private GuiButton selectedButton;
    private int eventButton;
    private long lastMouseEvent;
    private int touchValue;
    private URI clickedLinkURI;

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (int i = 0; i < this.buttonList.size(); i++) {
            this.buttonList.get(i).drawButton(mc, mouseX, mouseY);
        }

        for (int j = 0; j < this.labelList.size(); j++) {
            this.labelList.get(j).drawLabel(mc, mouseX, mouseY);
        }
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen((GuiScreen)null);
            if (mc.currentScreen == null) {
                mc.setIngameFocus();
            }
        }
    }

    public static String getClipboardString() {
        try {
            Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String)contents.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (Exception ignored) {
        }

        return "";
    }

    public static void setClipboardString(String copyText) {
        if (!StringUtils.isEmpty(copyText)) {
            try {
                StringSelection stringSelection = new StringSelection(copyText);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, (ClipboardOwner)null);
            } catch (Exception ignored) {
            }
        }
    }

    public void renderToolTip(ItemStack stack, int x, int y) {
        List tooltip = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
        MiddleClickHistory.handleItemStack2(stack, tooltip);

        for (int i = 0; i < tooltip.size(); i++) {
            if (i == 0) {
                tooltip.set(i, stack.getRarity().rarityColor + (String)tooltip.get(i));
            } else {
                tooltip.set(i, EnumChatFormatting.GRAY + (String)tooltip.get(i));
            }
        }

        this.drawHoveringText(tooltip, x, y);
    }

    public void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
        this.drawHoveringText(Arrays.asList(tabName), mouseX, mouseY);
    }

    protected void drawHoveringText(List<String> textLines, int x, int y) {
        if (!textLines.isEmpty()) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int n = 0;

            for (String str : textLines) {
                int textWidth = this.fontRendererObj.getStringWidth(str);
                if (textWidth > n) {
                    n = textWidth;
                }
            }

            int n2 = x + 12;
            int n3 = y - 12;
            int n4 = 8;
            if (textLines.size() > 1) {
                n4 += 2 + (textLines.size() - 1) * 10;
            }

            if (n2 + n > this.width) {
                n2 -= 28 + n;
            }

            if (n3 + n4 + 6 > this.height) {
                n3 = this.height - n4 - 6;
            }

            this.zLevel = 300.0F;
            this.itemRender.zLevel = 300.0F;
            int n5 = -267386864;
            this.drawGradientRect(n2 - 3, n3 - 4, n2 + n + 3, n3 - 3, n5, n5);
            this.drawGradientRect(n2 - 3, n3 + n4 + 3, n2 + n + 3, n3 + n4 + 4, n5, n5);
            this.drawGradientRect(n2 - 3, n3 - 3, n2 + n + 3, n3 + n4 + 3, n5, n5);
            this.drawGradientRect(n2 - 4, n3 - 3, n2 - 3, n3 + n4 + 3, n5, n5);
            this.drawGradientRect(n2 + n + 3, n3 - 3, n2 + n + 4, n3 + n4 + 3, n5, n5);
            int n6 = 1347420415;
            int n7 = (n6 & 16711422) >> 1 | n6 & 0xFF000000;
            this.drawGradientRect(n2 - 3, n3 - 3 + 1, n2 - 3 + 1, n3 + n4 + 3 - 1, n6, n7);
            this.drawGradientRect(n2 + n + 2, n3 - 3 + 1, n2 + n + 3, n3 + n4 + 3 - 1, n6, n7);
            this.drawGradientRect(n2 - 3, n3 - 3, n2 + n + 3, n3 - 3 + 1, n6, n6);
            this.drawGradientRect(n2 - 3, n3 + n4 + 2, n2 + n + 3, n3 + n4 + 3, n7, n7);

            for (int i = 0; i < textLines.size(); i++) {
                String str2 = (String)textLines.get(i);
                this.fontRendererObj.drawStringWithShadow(str2, n2, n3, -1);
                if (i == 0) {
                    n3 += 2;
                }

                n3 += 10;
            }

            this.zLevel = 0.0F;
            this.itemRender.zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    protected void handleComponentHover(IChatComponent p_175272_1_, int p_175272_2_, int p_175272_3_) {
        if (p_175272_1_ != null && p_175272_1_.getChatStyle().getChatHoverEvent() != null) {
            HoverEvent chatHoverEvent = p_175272_1_.getChatStyle().getChatHoverEvent();
            if (chatHoverEvent.getAction() == Action.SHOW_ITEM) {
                ItemStack stack = null;

                try {
                    NBTTagCompound tagFromJson = JsonToNBT.getTagFromJson(chatHoverEvent.getValue().getUnformattedText());
                    if (tagFromJson instanceof NBTTagCompound) {
                        stack = ItemStack.loadItemStackFromNBT(tagFromJson);
                    }
                } catch (NBTException ignored) {
                }

                if (stack != null) {
                    this.renderToolTip(stack, p_175272_2_, p_175272_3_);
                } else {
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Item!", p_175272_2_, p_175272_3_);
                }
            } else if (chatHoverEvent.getAction() == Action.SHOW_ENTITY) {
                if (mc.gameSettings.advancedItemTooltips) {
                    try {
                        NBTTagCompound tagFromJson2 = JsonToNBT.getTagFromJson(chatHoverEvent.getValue().getUnformattedText());
                        if (tagFromJson2 instanceof NBTTagCompound) {
                            ArrayList list = Lists.newArrayList();
                            NBTTagCompound nbt = tagFromJson2;
                            list.add(nbt.getString("name"));
                            if (nbt.hasKey("type", 8)) {
                                String str = nbt.getString("type");
                                list.add("Type: " + str + " (" + EntityList.getIDFromString(str) + ")");
                            }

                            list.add(nbt.getString("id"));
                            this.drawHoveringText(list, p_175272_2_, p_175272_3_);
                        } else {
                            this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", p_175272_2_, p_175272_3_);
                        }
                    } catch (NBTException e) {
                        this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", p_175272_2_, p_175272_3_);
                    }
                }
            } else if (chatHoverEvent.getAction() == Action.SHOW_TEXT) {
                this.drawHoveringText(NEWLINE_SPLITTER.splitToList(chatHoverEvent.getValue().getFormattedText()), p_175272_2_, p_175272_3_);
            } else if (chatHoverEvent.getAction() == Action.SHOW_ACHIEVEMENT) {
                StatBase oneShotStat = StatList.getOneShotStat(chatHoverEvent.getValue().getUnformattedText());
                if (oneShotStat != null) {
                    IChatComponent statName = oneShotStat.getStatName();
                    ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("stats.tooltip.type." + (oneShotStat.isAchievement() ? "achievement" : "statistic"));
                    chatComponentTranslation.getChatStyle().setItalic(true);
                    String str2 = oneShotStat instanceof Achievement ? ((Achievement)oneShotStat).getDescription() : null;
                    ArrayList list2 = Lists.newArrayList(statName.getFormattedText(), chatComponentTranslation.getFormattedText());
                    if (str2 != null) {
                        list2.addAll(this.fontRendererObj.listFormattedStringToWidth(str2, 150));
                    }

                    this.drawHoveringText(list2, p_175272_2_, p_175272_3_);
                } else {
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid statistic/achievement!", p_175272_2_, p_175272_3_);
                }
            }

            GlStateManager.disableLighting();
        }
    }

    protected void setText(String newChatText, boolean shouldOverwrite) {
    }

    protected boolean handleComponentClick(IChatComponent p_175276_1_) {
        if (p_175276_1_ == null) {
            return false;
        }
        ClickEvent chatClickEvent = p_175276_1_.getChatStyle().getChatClickEvent();
        if (chatClickEvent != null
            && chatClickEvent.getAction() == net.minecraft.event.ClickEvent.Action.RUN_COMMAND
            && !isShiftKeyDown()
            && ParseIntComponent.matchesString(chatClickEvent.getValue())) {
            return true;
        }
        if (isShiftKeyDown()) {
            if (p_175276_1_.getChatStyle().getInsertion() != null) {
                this.setText(p_175276_1_.getChatStyle().getInsertion(), false);
            }
        } else if (chatClickEvent != null) {
            if (chatClickEvent.getAction() == net.minecraft.event.ClickEvent.Action.OPEN_URL) {
                if (!mc.gameSettings.chatLinks) {
                    return false;
                }

                try {
                    URI uri = new URI(chatClickEvent.getValue());
                    String scheme = uri.getScheme();
                    if (scheme == null) {
                        throw new URISyntaxException(chatClickEvent.getValue(), "Missing protocol");
                    }

                    if (!PROTOCOLS.contains(scheme.toLowerCase())) {
                        throw new URISyntaxException(chatClickEvent.getValue(), "Unsupported protocol: " + scheme.toLowerCase());
                    }

                    if (mc.gameSettings.chatLinksPrompt) {
                        this.clickedLinkURI = uri;
                        mc.displayGuiScreen(new GuiConfirmOpenLink(this, chatClickEvent.getValue(), 31102009, false));
                    } else {
                        this.openWebLink(uri);
                    }
                } catch (URISyntaxException e) {
                    LOGGER.error("Can't open url for " + chatClickEvent, (Throwable)e);
                }
            } else if (chatClickEvent.getAction() == net.minecraft.event.ClickEvent.Action.OPEN_FILE) {
                URI uri2 = new File(chatClickEvent.getValue()).toURI();
                this.openWebLink(uri2);
            } else if (chatClickEvent.getAction() == net.minecraft.event.ClickEvent.Action.SUGGEST_COMMAND) {
                this.setText(chatClickEvent.getValue(), true);
            } else if (chatClickEvent.getAction() == net.minecraft.event.ClickEvent.Action.RUN_COMMAND) {
                this.sendChatMessage(chatClickEvent.getValue(), false);
            } else if (chatClickEvent.getAction() == net.minecraft.event.ClickEvent.Action.TWITCH_USER_INFO) {
                ChatUserInfo chatUserInfo = mc.getTwitchStream().func_152926_a(chatClickEvent.getValue());
                if (chatUserInfo != null) {
                    mc.displayGuiScreen(new GuiTwitchUserMode(mc.getTwitchStream(), chatUserInfo));
                } else {
                    LOGGER.error("Tried to handle twitch user but couldn't find them!");
                }
            } else {
                LOGGER.error("Don't know how to handle " + chatClickEvent);
            }

            return true;
        }

        return false;
    }

    public void sendChatMessage(String msg) {
        this.sendChatMessage(msg, true);
    }

    public void sendChatMessage(String msg, boolean addToChat) {
        if (addToChat) {
            mc.ingameGUI.getChatGUI().addToSentMessages(msg);
        }

        mc.thePlayer.sendChatMessage(msg);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            for (int i = 0; i < this.buttonList.size(); i++) {
                GuiButton button = this.buttonList.get(i);
                if (button.mousePressed(mc, mouseX, mouseY)) {
                    this.selectedButton = button;
                    button.playPressSound(mc.getSoundHandler());
                    this.actionPerformed(button);
                }
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.selectedButton != null && state == 0) {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }

    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
    }

    public void actionPerformed(GuiButton button) throws IOException {
    }

    public void setWorldAndResolution(Minecraft mc2, int width, int height) {
        mc = mc2;
        this.itemRender = mc2.getRenderItem();
        this.width = width;
        this.height = height;
        if (ChatComponent.matchesGuiScreen(this)) {
            ChatComponent.handleGuiScreen(this);
            this.fontRendererObj = ChatComponent.computeDrawStringWithShadowComponent();
        } else {
            this.fontRendererObj = mc2.fontRendererObj;
        }

        this.buttonList.clear();
        boolean flag = org.brutality.utils.render.RenderHelper.matchesGuiScreen(this);
        boolean active = org.brutality.utils.render.RenderHelper.isActive();

        try (org.brutality.utils.render.RenderHelper renderHelper = org.brutality.utils.render.RenderHelper.computeRenderHelper(this)) {
            if (flag || active) {
                ScaledResolution sr = new ScaledResolution(mc2);
                this.width = sr.getScaledWidth();
                this.height = sr.getScaledHeight();
            }

            this.initGui();
        }
    }

    public void func_183500_a(int p_183500_1_, int p_183500_2_) {
        this.width = p_183500_1_;
        this.height = p_183500_2_;
    }

    public void initGui() {
    }

    public void handleInput() throws IOException {
        if (org.brutality.utils.render.RenderHelper.matchesGuiScreen(this)) {
            try (org.brutality.utils.render.RenderHelper renderHelper = org.brutality.utils.render.RenderHelper.computeRenderHelper(this)) {
                this.handleInputInternal();
            }
        } else {
            this.handleInputInternal();
        }
    }

    private void handleInputInternal() throws IOException {
        if (Mouse.isCreated()) {
            while (Mouse.next()) {
                this.handleMouseInput();
            }
        }

        if (Keyboard.isCreated()) {
            while (Keyboard.next()) {
                this.handleKeyboardInput();
            }
        }
    }

    public void handleMouseInput() throws IOException {
        if (!StartHotbarMovesHelper.flag) {
            int n = Mouse.getEventX() * this.width / mc.displayWidth;
            int n2 = this.height - Mouse.getEventY() * this.height / mc.displayHeight - 1;
            int eventButton = Mouse.getEventButton();
            if (Mouse.getEventButtonState()) {
                if (mc.gameSettings.touchscreen && this.touchValue++ > 0) {
                    return;
                }

                this.eventButton = eventButton;
                this.lastMouseEvent = Minecraft.getSystemTime();

                try (UtilsHelper.Entry entry = UtilsHelper.computeEntry(eventButton)) {
                    this.mouseClicked(n, n2, this.eventButton);
                }
            } else if (eventButton != -1) {
                if (mc.gameSettings.touchscreen && --this.touchValue > 0) {
                    return;
                }

                this.eventButton = -1;
                this.mouseReleased(n, n2, eventButton);
            } else if (this.eventButton != -1 && this.lastMouseEvent > 0L) {
                long l = Minecraft.getSystemTime() - this.lastMouseEvent;
                this.mouseClickMove(n, n2, this.eventButton, l);
            }
        }
    }

    public void handleKeyboardInput() throws IOException {
        if (Keyboard.getEventKeyState()) {
            this.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
        }

        mc.dispatchKeypresses();
    }

    public void updateScreen() {
    }

    public void onGuiClosed() {
    }

    public void drawDefaultBackground() {
        this.drawWorldBackground(0);
    }

    public void drawWorldBackground(int tint) {
        if (mc.theWorld != null) {
            this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            this.drawBackground(tint);
        }
    }

    public void drawBackground(int tint) {
        if (mc != null && mc.theWorld == null) {
            OceanComponent.handleGuiScreen(this, 0, 0, 0.0F);
        } else {
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            mc.getTextureManager().bindTexture(optionsBackground);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            worldRenderer.func_181662_b(0.0, this.height, 0.0).func_181673_a(0.0, this.height / 32.0F + tint).func_181669_b(64, 64, 64, 255).func_181675_d();
            worldRenderer.func_181662_b(this.width, this.height, 0.0)
                .func_181673_a(this.width / 32.0F, this.height / 32.0F + tint)
                .func_181669_b(64, 64, 64, 255)
                .func_181675_d();
            worldRenderer.func_181662_b(this.width, 0.0, 0.0).func_181673_a(this.width / 32.0F, tint).func_181669_b(64, 64, 64, 255).func_181675_d();
            worldRenderer.func_181662_b(0.0, 0.0, 0.0).func_181673_a(0.0, tint).func_181669_b(64, 64, 64, 255).func_181675_d();
            tessellator.draw();
        }
    }

    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if (id == 31102009) {
            if (result) {
                this.openWebLink(this.clickedLinkURI);
            }

            this.clickedLinkURI = null;
            mc.displayGuiScreen(this);
        }
    }

    private void openWebLink(URI p_175282_1_) {
        try {
            Class clazz = Class.forName("java.awt.Desktop");
            Object obj = clazz.getMethod("getDesktop").invoke(null);
            clazz.getMethod("browse", URI.class).invoke(obj, p_175282_1_);
        } catch (Throwable e) {
            LOGGER.error("Couldn't open link", e);
        }
    }

    public static boolean isCtrlKeyDown() {
        return Minecraft.isRunningOnMac ? Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA) : Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
    }

    public static boolean isShiftKeyDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }

    public static boolean isAltKeyDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
    }

    public static boolean isKeyComboCtrlX(int p_175277_0_) {
        return p_175277_0_ == 45 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlV(int p_175279_0_) {
        return p_175279_0_ == 47 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlC(int p_175280_0_) {
        return p_175280_0_ == 46 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlA(int p_175278_0_) {
        return p_175278_0_ == 30 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public void onResize(Minecraft mcIn, int p_175273_2_, int p_175273_3_) {
        this.setWorldAndResolution(mcIn, p_175273_2_, p_175273_3_);
    }
}
