package net.minecraft.client.gui;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brutality.gui.chat.ChatComponent;
import org.brutality.proxy.ProxyAccountStore;
import org.brutality.proxy.ProxyConfig;
import org.brutality.proxy.ProxyManager;
import org.brutality.utils.render.GlStateGuard;
import org.lwjgl.opengl.GL11;

public class ServerListEntryNormal implements IGuiListEntry {
    private static final Logger logger = LogManager.getLogger();
    private static final ThreadPoolExecutor field_148302_b = new ScheduledThreadPoolExecutor(
        5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).build()
    );
    private static final ResourceLocation UNKNOWN_SERVER = new ResourceLocation("textures/misc/unknown_server.png");
    private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
    final GuiMultiplayer field_148303_c;
    private final Minecraft mc;
    final ServerData field_148301_e;
    private final ResourceLocation field_148306_i;
    private String field_148299_g;
    private DynamicTexture field_148305_h;
    private long field_148298_f;

    protected ServerListEntryNormal(GuiMultiplayer p_i45048_1_, ServerData p_i45048_2_) {
        this.field_148303_c = p_i45048_1_;
        this.field_148301_e = p_i45048_2_;
        this.mc = Minecraft.getMinecraft();
        this.field_148306_i = new ResourceLocation("servers/" + p_i45048_2_.serverIP + "/icon");
        this.field_148305_h = (DynamicTexture)this.mc.getTextureManager().getTexture(this.field_148306_i);
    }

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        if (!this.field_148301_e.field_78841_f) {
            this.field_148301_e.field_78841_f = true;
            this.field_148301_e.pingToServer = -2L;
            this.field_148301_e.serverMOTD = "";
            this.field_148301_e.populationInfo = "";
            field_148302_b.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        ServerListEntryNormal.this.field_148303_c.getOldServerPinger().ping(ServerListEntryNormal.this.field_148301_e);
                    } catch (UnknownHostException e) {
                        ServerListEntryNormal.this.field_148301_e.pingToServer = -1L;
                        ServerListEntryNormal.this.field_148301_e.serverMOTD = EnumChatFormatting.DARK_RED + "Can't resolve hostname";
                    } catch (Exception e2) {
                        ServerListEntryNormal.this.field_148301_e.pingToServer = -1L;
                        ServerListEntryNormal.this.field_148301_e.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
                    }
                }
            });
        }

        boolean flag = this.field_148301_e.version > 47;
        boolean flag2 = this.field_148301_e.version < 47;
        boolean flag3 = flag || flag2;
        FontRenderer fontRenderer = ChatComponent.computeFontRenderer(this.mc);
        fontRenderer.drawString(this.field_148301_e.serverName, x + 32 + 3, y + 1, 16777215);
        ProxyConfig proxyConfig = ProxyManager.computeProxyConfig(this.field_148301_e.serverIP);
        if (proxyConfig != null) {
            List list = ProxyAccountStore.computeList(proxyConfig);
            boolean flag4 = list.size() > 1;
            String str = "LOCKED PROXY";
            if (flag4) {
                str = str + " · SHARED: " + String.join(", ", list);
            } else if (list.size() == 1) {
                str = str + " · " + (String)list.get(0);
            }

            int n = x + 32 + 3 + fontRenderer.getStringWidth(this.field_148301_e.serverName) + 6;
            int n2 = Math.max(40, listWidth - (n - x) - 90);
            if (fontRenderer.getStringWidth(str) > n2) {
                while (str.length() > 1 && fontRenderer.getStringWidth(str + "...") > n2) {
                    str = str.substring(0, str.length() - 1);
                }

                str = str + "...";
            }

            int n3 = flag4 ? 1440781866 : 1429848186;
            int n4 = flag4 ? -2058710 : -12992390;
            int n5 = flag4 ? -671110 : -7343944;
            int n6 = flag4 ? -9747952 : -14983622;
            int n7 = flag4 ? -270152 : -4196906;
            int n8 = fontRenderer.getStringWidth(str) + 20;
            Gui.drawRect(n, y, n + n8, y + fontRenderer.FONT_HEIGHT + 1, n3);
            Gui.drawRect(n, y, n + 1, y + fontRenderer.FONT_HEIGHT + 1, n4);
            int n9 = n + 5;
            int n10 = y + 1;
            Gui.drawRect(n9 + 1, n10, n9 + 5, n10 + 1, n5);
            Gui.drawRect(n9, n10 + 1, n9 + 1, n10 + 4, n5);
            Gui.drawRect(n9 + 5, n10 + 1, n9 + 6, n10 + 4, n5);
            Gui.drawRect(n9 - 1, n10 + 4, n9 + 7, n10 + 9, n5);
            Gui.drawRect(n9 + 2, n10 + 5, n9 + 4, n10 + 7, n6);
            fontRenderer.drawString(str, n + 15, y + 1, n7);
        }

        List list2 = ChatComponent.computeFontRenderer(this.mc).listFormattedStringToWidth(this.field_148301_e.serverMOTD, listWidth - 32 - 2);

        for (int i = 0; i < Math.min(list2.size(), 2); i++) {
            fontRenderer.drawString((String)list2.get(i), x + 32 + 3, y + 12 + fontRenderer.FONT_HEIGHT * i, 8421504);
        }

        String str2 = flag3 ? EnumChatFormatting.DARK_RED + this.field_148301_e.gameVersion : this.field_148301_e.populationInfo;
        int textWidth = fontRenderer.getStringWidth(str2);
        fontRenderer.drawString(str2, x + listWidth - textWidth - 15 - 2, y + 1, 8421504);
        byte b = 0;
        String str3 = null;
        int n11;
        String str4;
        if (flag3) {
            n11 = 5;
            str4 = flag ? "Client out of date!" : "Server out of date!";
            str3 = this.field_148301_e.playerList;
        } else if (this.field_148301_e.field_78841_f && this.field_148301_e.pingToServer != -2L) {
            if (this.field_148301_e.pingToServer < 0L) {
                n11 = 5;
            } else if (this.field_148301_e.pingToServer < 150L) {
                n11 = 0;
            } else if (this.field_148301_e.pingToServer < 300L) {
                n11 = 1;
            } else if (this.field_148301_e.pingToServer < 600L) {
                n11 = 2;
            } else if (this.field_148301_e.pingToServer < 1000L) {
                n11 = 3;
            } else {
                n11 = 4;
            }

            if (this.field_148301_e.pingToServer < 0L) {
                str4 = "(no connection)";
            } else {
                str4 = this.field_148301_e.pingToServer + "ms";
                str3 = this.field_148301_e.playerList;
            }
        } else {
            b = 1;
            n11 = (int)(Minecraft.getSystemTime() / 100L + slotIndex * 2 & 7L);
            if (n11 > 4) {
                n11 = 8 - n11;
            }

            str4 = "Pinging...";
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(Gui.icons);
        Gui.drawModalRectWithCustomSizedTexture(x + listWidth - 15, y, b * 10, 176 + n11 * 8, 10, 8, 256.0F, 256.0F);
        if (this.field_148301_e.getBase64EncodedIconData() != null && !this.field_148301_e.getBase64EncodedIconData().equals(this.field_148299_g)) {
            this.field_148299_g = this.field_148301_e.getBase64EncodedIconData();
            this.prepareServerIcon();
            this.field_148303_c.getServerList().saveServerList();
        }

        if (this.field_148305_h != null) {
            this.func_178012_a(x, y, this.field_148306_i);
        } else {
            this.func_178012_a(x, y, UNKNOWN_SERVER);
        }

        int n12 = mouseX - x;
        int n13 = mouseY - y;
        if (n12 >= listWidth - 15 && n12 <= listWidth - 5 && n13 >= 0 && n13 <= 8) {
            this.field_148303_c.setHoveringText(str4);
        } else if (n12 >= listWidth - textWidth - 15 - 2 && n12 <= listWidth - 15 - 2 && n13 >= 0 && n13 <= 8) {
            this.field_148303_c.setHoveringText(str3);
        }

        if (this.mc.gameSettings.touchscreen || isSelected) {
            try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                Gui.drawRect(x + 32, y, x + listWidth, y + 32, -1601138544);
                this.mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
                GlStateManager.enableTexture2D();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                int n14 = mouseX - x;
                int n15 = mouseY - y;
                if (this.func_178013_b()) {
                    if (n14 < 32 && n14 > 16) {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    } else {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }

                if (this.field_148303_c.func_175392_a(this, slotIndex)) {
                    if (n14 < 16 && n15 < 16) {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    } else {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }

                if (this.field_148303_c.func_175394_b(this, slotIndex)) {
                    if (n14 < 16 && n15 > 16) {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    } else {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }
            }
        }
    }

    protected void func_178012_a(int p_178012_1_, int p_178012_2_, ResourceLocation p_178012_3_) {
        this.mc.getTextureManager().bindTexture(p_178012_3_);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(p_178012_1_, p_178012_2_, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
        GlStateManager.disableBlend();
    }

    private boolean func_178013_b() {
        return true;
    }

    private void prepareServerIcon() {
        if (this.field_148301_e.getBase64EncodedIconData() == null) {
            this.mc.getTextureManager().deleteTexture(this.field_148306_i);
            this.field_148305_h = null;
        } else {
            ByteBuf buf = Unpooled.copiedBuffer(this.field_148301_e.getBase64EncodedIconData(), Charsets.UTF_8);
            ByteBuf buf2 = Base64.decode(buf);

            BufferedImage image;
            label63: {
                try {
                    image = TextureUtil.readBufferedImage(new ByteBufInputStream(buf2));
                    Validate.validState(image.getWidth() == 64, "Must be 64 pixels wide");
                    Validate.validState(image.getHeight() == 64, "Must be 64 pixels high");
                    break label63;
                } catch (Throwable e) {
                    logger.error("Invalid icon for server " + this.field_148301_e.serverName + " (" + this.field_148301_e.serverIP + ")", e);
                    this.field_148301_e.setBase64EncodedIconData((String)null);
                } finally {
                    buf.release();
                    buf2.release();
                }

                return;
            }

            if (this.field_148305_h == null) {
                this.field_148305_h = new DynamicTexture(image.getWidth(), image.getHeight());
                this.mc.getTextureManager().loadTexture(this.field_148306_i, this.field_148305_h);
            }

            image.getRGB(0, 0, image.getWidth(), image.getHeight(), this.field_148305_h.getTextureData(), 0, image.getWidth());
            this.field_148305_h.updateDynamicTexture();
        }
    }

    @Override
    public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
        if (p_148278_5_ <= 32) {
            if (p_148278_5_ < 32 && p_148278_5_ > 16 && this.func_178013_b()) {
                this.field_148303_c.selectServer(slotIndex);
                this.field_148303_c.connectToSelected();
                return true;
            }

            if (p_148278_5_ < 16 && p_148278_6_ < 16 && this.field_148303_c.func_175392_a(this, slotIndex)) {
                this.field_148303_c.func_175391_a(this, slotIndex, GuiScreen.isShiftKeyDown());
                return true;
            }

            if (p_148278_5_ < 16 && p_148278_6_ > 16 && this.field_148303_c.func_175394_b(this, slotIndex)) {
                this.field_148303_c.func_175393_b(this, slotIndex, GuiScreen.isShiftKeyDown());
                return true;
            }
        }

        this.field_148303_c.selectServer(slotIndex);
        if (Minecraft.getSystemTime() - this.field_148298_f < 250L) {
            this.field_148303_c.connectToSelected();
        }

        this.field_148298_f = Minecraft.getSystemTime();
        return false;
    }

    @Override
    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
    }

    @Override
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
    }

    public ServerData getServerData() {
        return this.field_148301_e;
    }
}
