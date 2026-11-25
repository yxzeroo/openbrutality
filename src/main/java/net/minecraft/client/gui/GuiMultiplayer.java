package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.viamcp.ViaMCP;
import de.florianmichael.viamcp.gui.GuiProtocolSelector;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.network.LanServerDetector.LanServer;
import net.minecraft.client.network.LanServerDetector.LanServerList;
import net.minecraft.client.network.LanServerDetector.ThreadLanServerFind;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brutality.Brutality;
import org.brutality.gui.ChangeSkinScreen;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.StyledGuiButton;
import org.brutality.gui.NameChangerScreen;
import org.brutality.gui.OceanComponent;
import org.brutality.gui.SearchField;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.module.Module;
import org.brutality.proxy.ProxyAccountStore;
import org.brutality.proxy.ProxyConfig;
import org.brutality.proxy.ProxyManager;
import org.brutality.proxy.gui.GuiProxyWarning;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiMultiplayer extends CustomGuiScreen implements GuiYesNoCallback {
    private static final Logger logger = LogManager.getLogger();
    private final OldServerPinger oldServerPinger = new OldServerPinger();
    private GuiScreen parentScreen;
    private ServerSelectionList serverListSelector;
    private ServerList savedServerList;
    private GuiButton btnEditServer;
    private GuiButton btnSelectServer;
    private GuiButton btnDeleteServer;
    private GuiButton viaVersionButton;
    private SearchField searchField;
    private static final int LIST_TOP = 56;
    private boolean deletingServer;
    private boolean addingServer;
    private boolean editingServer;
    private boolean directConnect;
    private String hoveringText;
    private ServerData selectedServer;
    private int dragSourceIndex = -1;
    private int dragCurrentY = 0;
    private int dragCurrentX;
    private int dragOffsetX;
    private int dragStartX;
    private int dragStartY;
    private long dragLastFrameNanos;
    private double dragScrollRemainder;
    private int dragOffsetY = 0;
    private boolean isDraggingServer = false;
    private LanServerList lanServerList;
    private ThreadLanServerFind lanServerDetector;
    private boolean initialized;

    public GuiMultiplayer(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        this.isDraggingServer = false;
        this.dragSourceIndex = -1;
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        if (!this.initialized) {
            this.initialized = true;
            this.savedServerList = new ServerList(mc);
            ProxyAccountStore.load();
            this.savedServerList.loadServerList();
            this.lanServerList = new LanServerList();

            try {
                this.lanServerDetector = new ThreadLanServerFind(this.lanServerList);
                this.lanServerDetector.start();
            } catch (Exception e) {
                logger.warn("Unable to start LAN server detection: " + e.getMessage());
            }

            this.serverListSelector = new ServerSelectionList(this, mc, this.width, this.height, 56, this.height - 64, 36);
            this.serverListSelector.func_148195_a(this.savedServerList);
        } else {
            this.serverListSelector.setDimensions(this.width, this.height, 56, this.height - 64);
        }

        this.createButtons();
        String str = this.searchField == null ? "" : this.searchField.getText();
        boolean flag = this.searchField != null && this.searchField.isFocused();
        int n = Math.min(220, Math.max(120, this.width - 240));
        this.searchField = new SearchField(77, this.width / 2 - n / 2, 30, n, 20).setText2("Search servers  (Ctrl+F)");
        this.searchField.setMaxStringLength(64);
        this.searchField.setText(str);
        this.searchField.setFocused(flag);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if (this.dragSourceIndex >= 0 && Mouse.isButtonDown(0)) {
            this.updateServerDrag(Mouse.getX() * this.width / mc.displayWidth, this.height - Mouse.getY() * this.height / mc.displayHeight - 1);
        }

        if (this.dragSourceIndex < 0 && !this.isDraggingServer) {
            this.serverListSelector.handleMouseInput();
        }
    }

    private void updateServerDrag(int mouseX, int mouseY) {
        this.dragCurrentX = mouseX;
        this.dragCurrentY = mouseY;
        if (Math.abs(mouseX - this.dragStartX) > 3 || Math.abs(mouseY - this.dragStartY) > 3) {
            this.isDraggingServer = true;
        }
    }

    public boolean isDraggingServerEntry(int index) {
        return this.isDraggingServer && index == this.dragSourceIndex;
    }

    public void createButtons() {
        this.buttonList.add(this.btnEditServer = new StyledGuiButton(7, this.width / 2 - 154, this.height - 28, 70, 20, I18n.format("selectServer.edit")));
        this.buttonList.add(this.btnDeleteServer = new StyledGuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20, I18n.format("selectServer.delete")));
        this.buttonList.add(this.btnSelectServer = new StyledGuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("selectServer.select")));
        this.buttonList.add(new StyledGuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("selectServer.direct")));
        this.buttonList.add(new StyledGuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.format("selectServer.add")));
        this.buttonList.add(new StyledGuiButton(8, this.width / 2 + 4, this.height - 28, 70, 20, I18n.format("selectServer.refresh")));
        this.buttonList.add(new StyledGuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.format("gui.cancel")));
        this.buttonList.add(new StyledGuiButton(8484, 5, 5, 110, 20, "Disable Crasher"));
        if (ViaMCP.INSTANCE != null) {
            this.buttonList.add(this.viaVersionButton = new StyledGuiButton(1001, 120, 5, 155, 20, getViaVersionLabel()));
        }

        this.buttonList.add(new StyledGuiButton(800, this.width - 105, 5, 100, 20, "Name Changer"));
        this.buttonList.add(new StyledGuiButton(801, this.width - 105, 30, 100, 20, "Skin Changer"));
        this.buttonList.add(new StyledGuiButton(802, this.width - 105, 55, 100, 20, "Lock Proxy"));
        this.selectServer(this.serverListSelector.func_148193_k());
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (this.searchField != null) {
            this.searchField.updateCursorCounter();
        }

        if (this.viaVersionButton != null) {
            this.viaVersionButton.displayString = getViaVersionLabel();
        }

        if (this.lanServerList.getWasUpdated()) {
            List lanServers = this.lanServerList.getLanServers();
            this.lanServerList.setWasNotUpdated();
            this.serverListSelector.func_148194_a(lanServers);
        }

        this.oldServerPinger.pingPendingNetworks();
    }

    private static String getViaVersionLabel() {
        ViaLoadingBase instance = ViaLoadingBase.getInstance();
        return "Version: " + (instance == null ? "1.8.9" : instance.getTargetVersion().getName());
    }

    @Override
    public void onGuiClosed() {
        this.isDraggingServer = false;
        this.dragSourceIndex = -1;
        Keyboard.enableRepeatEvents(false);
        if (this.lanServerDetector != null) {
            this.lanServerDetector.interrupt();
            this.lanServerDetector = null;
        }

        this.oldServerPinger.clearPendingNetworks();
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            IGuiListEntry iGuiListEntry = this.serverListSelector.func_148193_k() < 0
                ? null
                : this.serverListSelector.getListEntry(this.serverListSelector.func_148193_k());
            if (button.id == 2 && iGuiListEntry instanceof ServerListEntryNormal) {
                String serverName = ((ServerListEntryNormal)iGuiListEntry).getServerData().serverName;
                if (serverName != null) {
                    this.deletingServer = true;
                    String str = I18n.format("selectServer.deleteQuestion");
                    String str2 = "'" + serverName + "' " + I18n.format("selectServer.deleteWarning");
                    String str3 = I18n.format("selectServer.deleteButton");
                    String str4 = I18n.format("gui.cancel");
                    GuiYesNo gui = new GuiYesNo(this, str, str2, str3, str4, this.serverListSelector.func_148193_k());
                    mc.displayGuiScreen(gui);
                }
            } else if (button.id == 1) {
                this.connectToSelected();
            } else if (button.id == 4) {
                this.directConnect = true;
                mc.displayGuiScreen(new GuiScreenServerList(this, this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false)));
            } else if (button.id == 3) {
                this.addingServer = true;
                mc.displayGuiScreen(new GuiScreenAddServer(this, this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false)));
            } else if (button.id == 7 && iGuiListEntry instanceof ServerListEntryNormal) {
                this.editingServer = true;
                ServerData serverData = ((ServerListEntryNormal)iGuiListEntry).getServerData();
                this.selectedServer = new ServerData(serverData.serverName, serverData.serverIP, false);
                this.selectedServer.copyFrom(serverData);
                mc.displayGuiScreen(new GuiScreenAddServer(this, this.selectedServer));
            } else if (button.id == 0) {
                mc.displayGuiScreen(this.parentScreen);
            } else if (button.id == 8) {
                this.refreshServerList();
            } else if (button.id == 8484) {
                Module module = Brutality.moduleManager.computeModule("Crasher");
                if (module != null && module.checkState44()) {
                    module.handleFlag3(false);
                }

                button.displayString = "Disable Crasher";
            } else if (button.id == 1001) {
                mc.displayGuiScreen(new GuiProtocolSelector(this));
            } else if (button.id == 800) {
                mc.displayGuiScreen(new NameChangerScreen(this));
            } else if (button.id == 801) {
                mc.displayGuiScreen(new ChangeSkinScreen(this));
            } else if (button.id == 802 && iGuiListEntry instanceof ServerListEntryNormal) {
                ServerData serverData2 = ((ServerListEntryNormal)iGuiListEntry).getServerData();
                if (ProxyManager.computeProxyConfig(serverData2.serverIP) != null) {
                    ProxyManager.handleString(serverData2.serverIP);
                    button.displayString = "Lock Proxy";
                } else {
                    ProxyConfig proxyConfig = ProxyManager.getProxyConfig();
                    if (proxyConfig == null) {
                        ChatComponentText chatComponentText = new ChatComponentText("Enable a proxy first, then select a server and click Lock Proxy.");
                        mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText);
                    } else {
                        ProxyManager.handleString(serverData2.serverIP, proxyConfig);
                        button.displayString = "Unlock Proxy";
                    }
                }
            }
        }
    }

    private void refreshServerList() {
        mc.displayGuiScreen(new GuiMultiplayer(this.parentScreen));
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        IGuiListEntry iGuiListEntry = this.serverListSelector.func_148193_k() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.func_148193_k());
        if (this.deletingServer) {
            this.deletingServer = false;
            if (result && iGuiListEntry instanceof ServerListEntryNormal) {
                int n = this.savedIndexOf(((ServerListEntryNormal)iGuiListEntry).getServerData());
                if (n >= 0) {
                    this.savedServerList.removeServerData(n);
                }

                this.savedServerList.saveServerList();
                this.serverListSelector.setSelectedSlotIndex(-1);
                this.serverListSelector.func_148195_a(this.savedServerList);
            }

            mc.displayGuiScreen(this);
        } else if (this.directConnect) {
            this.directConnect = false;
            if (result) {
                this.connectToServer(this.selectedServer);
            } else {
                mc.displayGuiScreen(this);
            }
        } else if (this.addingServer) {
            this.addingServer = false;
            if (result) {
                this.savedServerList.addServerData(this.selectedServer);
                this.savedServerList.saveServerList();
                this.serverListSelector.setSelectedSlotIndex(-1);
                this.serverListSelector.func_148195_a(this.savedServerList);
            }

            mc.displayGuiScreen(this);
        } else if (this.editingServer) {
            this.editingServer = false;
            if (result && iGuiListEntry instanceof ServerListEntryNormal) {
                ServerData serverData = ((ServerListEntryNormal)iGuiListEntry).getServerData();
                serverData.serverName = this.selectedServer.serverName;
                serverData.serverIP = this.selectedServer.serverIP;
                serverData.copyFrom(this.selectedServer);
                this.savedServerList.saveServerList();
                this.serverListSelector.func_148195_a(this.savedServerList);
            }

            mc.displayGuiScreen(this);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 33 && isCtrlKeyDown()) {
            this.searchField.setFocused(true);
        } else if (this.searchField.isFocused() && keyCode != 1 && keyCode != 200 && keyCode != 208 && keyCode != 28 && keyCode != 156) {
            String text = this.searchField.getText();
            this.searchField.textboxKeyTyped(typedChar, keyCode);
            if (!text.equals(this.searchField.getText())) {
                this.applySearch();
            }
        } else {
            int n = this.serverListSelector.func_148193_k();
            IGuiListEntry iGuiListEntry = n < 0 ? null : this.serverListSelector.getListEntry(n);
            if (keyCode == 63) {
                this.refreshServerList();
            } else if (n >= 0) {
                if (keyCode == 200) {
                    if (isShiftKeyDown() && !this.serverListSelector.isFiltered()) {
                        if (n > 0 && iGuiListEntry instanceof ServerListEntryNormal) {
                            this.savedServerList.swapServers(n, n - 1);
                            this.selectServer(this.serverListSelector.func_148193_k() - 1);
                            this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                            this.serverListSelector.func_148195_a(this.savedServerList);
                        }
                    } else if (n > 0) {
                        this.selectServer(this.serverListSelector.func_148193_k() - 1);
                        this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                        if (this.serverListSelector.getListEntry(this.serverListSelector.func_148193_k()) instanceof ServerListEntryLanScan) {
                            if (this.serverListSelector.func_148193_k() > 0) {
                                this.selectServer(this.serverListSelector.getSize() - 1);
                                this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                            } else {
                                this.selectServer(-1);
                            }
                        }
                    } else {
                        this.selectServer(-1);
                    }
                } else if (keyCode == 208) {
                    if (isShiftKeyDown() && !this.serverListSelector.isFiltered()) {
                        if (n < this.savedServerList.countServers() - 1) {
                            this.savedServerList.swapServers(n, n + 1);
                            this.selectServer(n + 1);
                            this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                            this.serverListSelector.func_148195_a(this.savedServerList);
                        }
                    } else if (n < this.serverListSelector.getSize()) {
                        this.selectServer(this.serverListSelector.func_148193_k() + 1);
                        this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                        if (this.serverListSelector.getListEntry(this.serverListSelector.func_148193_k()) instanceof ServerListEntryLanScan) {
                            if (this.serverListSelector.func_148193_k() < this.serverListSelector.getSize() - 1) {
                                this.selectServer(this.serverListSelector.getSize() + 1);
                                this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                            } else {
                                this.selectServer(-1);
                            }
                        }
                    } else {
                        this.selectServer(-1);
                    }
                } else if (keyCode != 28 && keyCode != 156) {
                    super.keyTyped(typedChar, keyCode);
                } else {
                    this.actionPerformed(this.buttonList.get(2));
                }
            } else {
                super.keyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public void drawDefaultBackground() {
        OceanComponent.handleGuiScreen(this, 0, 0, 0.0F);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.hoveringText = null;
        this.drawDefaultBackground();
        if (this.dragSourceIndex >= 0 && Mouse.isButtonDown(0)) {
            this.updateServerDrag(mouseX, mouseY);
        }

        long now = System.nanoTime();
        if (this.isDraggingServer) {
            double d = this.dragLastFrameNanos == 0L ? 0.0 : Math.min(0.05, (now - this.dragLastFrameNanos) / 1.0E9);
            int n = mouseY < this.serverListSelector.getTop() + 16 ? -1 : (mouseY > this.serverListSelector.getBottom() - 16 ? 1 : 0);
            if (n == 0) {
                this.dragScrollRemainder = 0.0;
            } else {
                this.dragScrollRemainder += n * d * 180.0;
                int dragScrollRemainder = (int)this.dragScrollRemainder;
                if (dragScrollRemainder != 0) {
                    this.serverListSelector.scrollBy(dragScrollRemainder);
                }

                this.dragScrollRemainder -= dragScrollRemainder;
            }
        }

        this.dragLastFrameNanos = now;
        this.serverListSelector.drawScreen(this.isDraggingServer ? -9999 : mouseX, mouseY, partialTicks);
        if (this.isDraggingServer && this.dragSourceIndex >= 0 && this.dragSourceIndex < this.savedServerList.countServers()) {
            int slotHeight = this.serverListSelector.getSlotHeight();
            int n2 = this.dragCurrentY - this.dragOffsetY;
            int n3 = this.dragCurrentX - this.dragOffsetX;
            int entryX = this.serverListSelector.getEntryX();
            int listWidth = this.serverListSelector.getListWidth();
            IGuiListEntry listEntry = this.serverListSelector.getListEntry(this.dragSourceIndex);
            if (listEntry instanceof ServerListEntryNormal) {
                drawRect(n3 + 2, n2 + 2, n3 + listWidth + 2, n2 + slotHeight + 2, 1610612736);
                drawRect(n3 - 1, n2 - 1, n3 + listWidth + 1, n2 + slotHeight, -14342098);
                ((ServerListEntryNormal)listEntry).drawEntry(this.dragSourceIndex, n3, n2, listWidth, slotHeight - 4, -9999, -9999, false);
                drawRect(n3, n2 + slotHeight - 1, n3 + listWidth, n2 + slotHeight, -11162881);
            }

            int serverDropIndex = this.getServerDropIndex(this.dragCurrentY);
            int n4 = this.serverListSelector.getEntryY(serverDropIndex) + (serverDropIndex >= this.dragSourceIndex ? slotHeight : 0);
            if (n4 >= this.serverListSelector.getTop() && n4 <= this.serverListSelector.getBottom()) {
                drawRect(entryX, n4 - 1, entryX + listWidth, n4 + 1, -11162881);
            }
        }

        String str = I18n.format("multiplayer.title");
        CustomFontRenderer drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(22, true);
        drawStringWithShadowComponent.drawString(str, this.width / 2 - drawStringWithShadowComponent.getStringWidth(str) / 2, 9, -1117448);
        this.searchField.drawTextBox();
        if (this.serverListSelector.isFiltered() && this.serverListSelector.countServerEntries() == 0) {
            CustomFontRenderer drawStringWithShadowComponent2 = FontComponent.computeDrawStringWithShadowComponent2(17, false);
            String str2 = "No servers match \"" + this.searchField.getText() + "\"";
            drawStringWithShadowComponent2.drawString(str2, this.width / 2 - drawStringWithShadowComponent2.getStringWidth(str2) / 2, 76, -7497816);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.hoveringText != null) {
            this.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.hoveringText)), mouseX, mouseY);
        }
    }

    public void connectToSelected() {
        IGuiListEntry iGuiListEntry = this.serverListSelector.func_148193_k() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.func_148193_k());
        if (iGuiListEntry instanceof ServerListEntryNormal) {
            this.connectToServer(((ServerListEntryNormal)iGuiListEntry).getServerData());
        } else if (iGuiListEntry instanceof ServerListEntryLanDetected) {
            LanServer lanServer = ((ServerListEntryLanDetected)iGuiListEntry).getLanServer();
            this.connectToServer(new ServerData(lanServer.getServerMotd(), lanServer.getServerIpPort(), true));
        }
    }

    private void connectToServer(ServerData server) {
        ProxyManager.load();
        ProxyConfig proxyConfig = server == null ? null : ProxyManager.computeProxyConfig(server.serverIP);
        if (proxyConfig != null) {
            ProxyManager.handleProxyConfig(proxyConfig);
        }

        ProxyAccountStore.load();
        String str = "";

        try {
            if (mc.getSession() != null) {
                str = mc.getSession().getUsername();
            }
        } catch (Exception ignored) {
        }

        boolean flag = server != null && server.serverIP != null && server.serverIP.toLowerCase().contains("liquidproxy");
        if (!str.isEmpty() && !flag) {
            ProxyAccountStore.NoneMode noneMode = ProxyAccountStore.computeNoneMode(str);
            if (noneMode != ProxyAccountStore.NoneMode.NONE) {
                mc.displayGuiScreen(new GuiProxyWarning(this, server, noneMode, str));
                return;
            }
        }

        mc.displayGuiScreen(new GuiConnecting(this, mc, server));
    }

    public void selectServer(int index) {
        this.serverListSelector.setSelectedSlotIndex(index);
        IGuiListEntry iGuiListEntry = index < 0 ? null : this.serverListSelector.getListEntry(index);
        this.btnSelectServer.enabled = false;
        this.btnEditServer.enabled = false;
        this.btnDeleteServer.enabled = false;
        if (iGuiListEntry != null && !(iGuiListEntry instanceof ServerListEntryLanScan)) {
            this.btnSelectServer.enabled = true;
            if (iGuiListEntry instanceof ServerListEntryNormal) {
                this.btnEditServer.enabled = true;
                this.btnDeleteServer.enabled = true;
            }
        }

        GuiButton button = null;

        for (Object obj : this.buttonList) {
            if (obj instanceof GuiButton && ((GuiButton)obj).id == 802) {
                button = (GuiButton)obj;
            }
        }

        if (button != null) {
            button.enabled = iGuiListEntry instanceof ServerListEntryNormal;
            button.displayString = iGuiListEntry instanceof ServerListEntryNormal
                ? (ProxyManager.computeProxyConfig(((ServerListEntryNormal)iGuiListEntry).getServerData().serverIP) != null ? "Unlock Proxy" : "Lock Proxy")
                : "Lock Proxy";
        }
    }

    public OldServerPinger getOldServerPinger() {
        return this.oldServerPinger;
    }

    public void setHoveringText(String p_146793_1_) {
        this.hoveringText = p_146793_1_;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 1
            && mouseX >= this.searchField.xPosition
            && mouseX < this.searchField.xPosition + this.searchField.getCount2()
            && mouseY >= this.searchField.yPosition
            && mouseY < this.searchField.yPosition + this.searchField.height) {
            this.searchField.setText("");
            this.applySearch();
        } else {
            if (mouseButton == 0 && !this.serverListSelector.isFiltered() && mouseY >= this.serverListSelector.getTop() && mouseY < this.serverListSelector.getBottom()) {
                int slotIndexFromScreenCoords = this.serverListSelector.getSlotIndexFromScreenCoords(mouseX, mouseY);
                if (slotIndexFromScreenCoords >= 0 && slotIndexFromScreenCoords < this.savedServerList.countServers()) {
                    this.dragSourceIndex = slotIndexFromScreenCoords;
                    this.dragOffsetY = mouseY - this.serverListSelector.getEntryY(slotIndexFromScreenCoords);
                    this.dragOffsetX = mouseX - this.serverListSelector.getEntryX();
                    this.dragStartX = this.dragCurrentX = mouseX;
                    this.dragStartY = mouseY;
                    this.dragCurrentY = mouseY;
                    this.isDraggingServer = false;
                }
            }

            super.mouseClicked(mouseX, mouseY, mouseButton);
            if (!this.isDraggingServer) {
                this.serverListSelector.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0 && this.isDraggingServer && this.dragSourceIndex >= 0) {
            int serverDropIndex = this.getServerDropIndex(mouseY);
            if (serverDropIndex != this.dragSourceIndex) {
                int n;
                for (n = this.dragSourceIndex; n < serverDropIndex; n++) {
                    this.savedServerList.swapServers(n, n + 1);
                }

                while (n > serverDropIndex) {
                    this.savedServerList.swapServers(n, n - 1);
                    n--;
                }

                this.savedServerList.saveServerList();
                this.serverListSelector.func_148195_a(this.savedServerList);
                this.selectServer(serverDropIndex);
            }

            this.isDraggingServer = false;
            this.dragSourceIndex = -1;
            this.serverListSelector.mouseReleased(mouseX, mouseY, state);
        } else {
            this.isDraggingServer = false;
            this.dragSourceIndex = -1;
            super.mouseReleased(mouseX, mouseY, state);
            this.serverListSelector.mouseReleased(mouseX, mouseY, state);
        }
    }

    private int getServerDropIndex(int mouseY) {
        int n = mouseY - this.serverListSelector.getEntryY(0);
        return Math.max(0, Math.min(this.savedServerList.countServers() - 1, Math.floorDiv(n, this.serverListSelector.getSlotHeight())));
    }

    public ServerList getServerList() {
        return this.savedServerList;
    }

    public boolean func_175392_a(ServerListEntryNormal p_175392_1_, int p_175392_2_) {
        return p_175392_2_ > 0 && !this.serverListSelector.isFiltered();
    }

    public boolean func_175394_b(ServerListEntryNormal p_175394_1_, int p_175394_2_) {
        return p_175394_2_ < this.savedServerList.countServers() - 1 && !this.serverListSelector.isFiltered();
    }

    private int savedIndexOf(ServerData data) {
        for (int i = 0; i < this.savedServerList.countServers(); i++) {
            if (this.savedServerList.getServerData(i) == data) {
                return i;
            }
        }

        return -1;
    }

    private void applySearch() {
        this.serverListSelector.setFilter(this.searchField.getText());
        this.serverListSelector.setSelectedSlotIndex(-1);
        this.serverListSelector.func_148195_a(this.savedServerList);
        this.selectServer(-1);
    }

    public void func_175391_a(ServerListEntryNormal p_175391_1_, int p_175391_2_, boolean p_175391_3_) {
        int n = p_175391_3_ ? 0 : p_175391_2_ - 1;
        this.savedServerList.swapServers(p_175391_2_, n);
        if (this.serverListSelector.func_148193_k() == p_175391_2_) {
            this.selectServer(n);
        }

        this.serverListSelector.func_148195_a(this.savedServerList);
    }

    public void func_175393_b(ServerListEntryNormal p_175393_1_, int p_175393_2_, boolean p_175393_3_) {
        int n = p_175393_3_ ? this.savedServerList.countServers() - 1 : p_175393_2_ + 1;
        this.savedServerList.swapServers(p_175393_2_, n);
        if (this.serverListSelector.func_148193_k() == p_175393_2_) {
            this.selectServer(n);
        }

        this.serverListSelector.func_148195_a(this.savedServerList);
    }
}
