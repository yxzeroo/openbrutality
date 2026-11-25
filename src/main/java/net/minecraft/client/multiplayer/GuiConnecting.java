package net.minecraft.client.multiplayer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brutality.gui.mainmenu.base.MainMenuScene;
import org.brutality.gui.mainmenu.effects.NebulaComponent;
import org.lwjgl.opengl.GL11;

public class GuiConnecting extends GuiScreen {
    private static final AtomicInteger CONNECTION_ID = new AtomicInteger(0);
    static final Logger logger = LogManager.getLogger();
    NetworkManager networkManager;
    boolean cancel;
    final GuiScreen previousGuiScreen;

    public GuiConnecting(GuiScreen p_i1181_1_, Minecraft mcIn, ServerData p_i1181_3_) {
        mc = mcIn;
        this.previousGuiScreen = p_i1181_1_;
        ServerAddress serverAddress = ServerAddress.func_78860_a(p_i1181_3_.serverIP);
        mcIn.loadWorld((WorldClient)null);
        mcIn.setServerData(p_i1181_3_);
        this.connect(serverAddress.getIP(), serverAddress.getPort());
    }

    public GuiConnecting(GuiScreen p_i1182_1_, Minecraft mcIn, String hostName, int port) {
        mc = mcIn;
        this.previousGuiScreen = p_i1182_1_;
        mcIn.loadWorld((WorldClient)null);
        this.connect(hostName, port);
    }

    private void connect(final String ip, final int port) {
        logger.info("Connecting to " + ip + ", " + port);
        (new Thread("Server Connector #" + CONNECTION_ID.incrementAndGet()) {
                @Override
                public void run() {
                    InetAddress inetAddress = null;

                    try {
                        if (GuiConnecting.this.cancel) {
                            return;
                        }

                        inetAddress = InetAddress.getByName(ip);
                        GuiConnecting.this.networkManager = NetworkManager.createNetworkManagerAndConnect(
                            inetAddress, port, GuiConnecting.mc.gameSettings.func_181148_f()
                        );
                        GuiConnecting.this.networkManager
                            .setNetHandler(new NetHandlerLoginClient(GuiConnecting.this.networkManager, GuiConnecting.mc, GuiConnecting.this.previousGuiScreen));
                        GuiConnecting.this.networkManager.sendPacket(new C00Handshake(47, ip, port, EnumConnectionState.LOGIN));
                        GuiConnecting.this.networkManager.sendPacket(new C00PacketLoginStart(GuiConnecting.mc.getSession().getProfile()));
                    } catch (UnknownHostException e) {
                        if (GuiConnecting.this.cancel) {
                            return;
                        }

                        GuiConnecting.logger.error("Couldn't connect to server", (Throwable)e);
                        GuiConnecting.mc
                            .displayGuiScreen(
                                new GuiDisconnected(
                                    GuiConnecting.this.previousGuiScreen,
                                    "connect.failed",
                                    new ChatComponentTranslation("disconnect.genericReason", "Unknown host")
                                )
                            );
                    } catch (Exception e2) {
                        if (GuiConnecting.this.cancel) {
                            return;
                        }

                        GuiConnecting.logger.error("Couldn't connect to server", (Throwable)e2);
                        String str = e2.toString();
                        if (inetAddress != null) {
                            String str2 = inetAddress.toString() + ":" + port;
                            str = str.replaceAll(str2, "");
                        }

                        GuiConnecting.mc
                            .displayGuiScreen(
                                new GuiDisconnected(
                                    GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", str)
                                )
                            );
                    }
                }
            })
            .start();
    }

    @Override
    public void updateScreen() {
        if (this.networkManager != null) {
            if (this.networkManager.isChannelOpen()) {
                this.networkManager.processReceivedPackets();
            } else {
                this.networkManager.checkDisconnected();
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.cancel = true;
            if (this.networkManager != null) {
                this.networkManager.closeChannel(new ChatComponentText("Aborted"));
            }

            mc.displayGuiScreen(this.previousGuiScreen);
        }
    }

    @Override
    public void drawDefaultBackground() {
        try {
            MainMenuScene mainMenuScene = NebulaComponent.computeMainMenuScene2();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            mainMenuScene.handleInt(this.width, this.height, 0.0F, 0, 0);
            drawRect(0, 0, this.width, this.height, -1728053248);
            GlStateManager.disableBlend();
        } catch (Exception e) {
            super.drawDefaultBackground();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        if (this.networkManager == null) {
            this.drawCenteredString(this.fontRendererObj, I18n.format("connect.connecting"), this.width / 2, this.height / 2 - 50, 16777215);
        } else {
            this.drawCenteredString(this.fontRendererObj, I18n.format("connect.authorizing"), this.width / 2, this.height / 2 - 50, 16777215);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
