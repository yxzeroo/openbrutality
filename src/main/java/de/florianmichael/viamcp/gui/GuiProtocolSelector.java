package de.florianmichael.viamcp.gui;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.viamcp.protocolinfo.ProtocolInfo;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

public class GuiProtocolSelector extends GuiScreen {
    private final GuiScreen parent;
    public GuiProtocolSelector.SlotList list;

    public GuiProtocolSelector(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height - 25, 200, 20, "Back"));
        this.list = new GuiProtocolSelector.SlotList(mc, this.width, this.height, 32, this.height - 32);
    }

    @Override
    public void actionPerformed(GuiButton guiButton) throws IOException {
        this.list.actionPerformed(guiButton);
        if (guiButton.id == 1) {
            mc.displayGuiScreen(this.parent);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(this.parent);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        this.list.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0, 2.0, 2.0);
        String str = EnumChatFormatting.BOLD + "ViaMCP";
        this.drawString(this.fontRendererObj, str, (this.width - this.fontRendererObj.getStringWidth(str) * 2) / 4, 5, -1);
        GlStateManager.popMatrix();
        this.drawString(this.fontRendererObj, "by EnZaXD/Flori2007", 1, 1, -1);
        this.drawString(this.fontRendererObj, "Discord: EnZaXD#6257", 1, 11, -1);
        ProtocolInfo protocolInfo = ProtocolInfo.fromProtocolVersion(ViaLoadingBase.getInstance().getTargetVersion());
        String str2 = "Version: " + ViaLoadingBase.getInstance().getTargetVersion().getName() + " - " + protocolInfo.getName();
        String str3 = "Released: " + protocolInfo.getReleaseDate();
        int n = (5 + this.fontRendererObj.FONT_HEIGHT) * 2 + 2;
        this.drawString(
            this.fontRendererObj,
            "" + EnumChatFormatting.GRAY + EnumChatFormatting.BOLD + "Version Information",
            (this.width - this.fontRendererObj.getStringWidth("Version Information")) / 2,
            n,
            -1
        );
        this.drawString(this.fontRendererObj, str2, (this.width - this.fontRendererObj.getStringWidth(str2)) / 2, n + this.fontRendererObj.FONT_HEIGHT, -1);
        this.drawString(
            this.fontRendererObj, str3, (this.width - this.fontRendererObj.getStringWidth(str3)) / 2, n + this.fontRendererObj.FONT_HEIGHT * 2, -1
        );
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    class SlotList extends GuiSlot {
        public SlotList(Minecraft mc, int n, int n2, int n3, int n4) {
            super(mc, n, n2, n3 + 30, n4, 18);
        }

        @Override
        public int getSize() {
            return ViaLoadingBase.getProtocols().size();
        }

        @Override
        public void elementClicked(int i, boolean b, int i1, int i2) {
            ProtocolVersion protocolVersion = ViaLoadingBase.getProtocols().get(i);
            ViaLoadingBase.getInstance().reload(protocolVersion);
        }

        @Override
        public boolean isSelected(int i) {
            return false;
        }

        @Override
        public void drawBackground() {
            GuiProtocolSelector.this.drawDefaultBackground();
        }

        @Override
        public void drawSlot(int i, int i1, int i2, int i3, int i4, int i5) {
            GuiProtocolSelector.this.drawCenteredString(
                this.mc.fontRendererObj,
                (
                        ViaLoadingBase.PROTOCOLS.indexOf(ViaLoadingBase.getInstance().getTargetVersion()) == i
                            ? EnumChatFormatting.GREEN.toString() + EnumChatFormatting.BOLD
                            : EnumChatFormatting.GRAY.toString()
                    )
                    + ViaLoadingBase.getProtocols().get(i).getName(),
                this.width / 2,
                i2 + 2,
                -1
            );
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GuiProtocolSelector.this.drawCenteredString(
                this.mc.fontRendererObj, "PVN: " + ViaLoadingBase.getProtocols().get(i).getVersion(), this.width, (i2 + 2) * 2 + 20, -1
            );
            GlStateManager.popMatrix();
        }
    }
}
