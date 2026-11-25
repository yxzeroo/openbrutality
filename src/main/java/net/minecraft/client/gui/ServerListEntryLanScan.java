package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.resources.I18n;
import org.brutality.gui.chat.ChatComponent;

public class ServerListEntryLanScan implements IGuiListEntry {
    private final Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        int n = y + slotHeight / 2 - ChatComponent.computeFontRenderer(this.mc).FONT_HEIGHT / 2;
        ChatComponent.computeFontRenderer(this.mc)
            .drawString(
                I18n.format("lanServer.scanning"),
                this.mc.currentScreen.width / 2 - ChatComponent.computeFontRenderer(this.mc).getStringWidth(I18n.format("lanServer.scanning")) / 2,
                n,
                16777215
            );

        String str = switch ((int)(Minecraft.getSystemTime() / 300L % 4L)) {
            default -> "O o o";
            case 1, 3 -> "o O o";
            case 2 -> "o o O";
        };
        ChatComponent.computeFontRenderer(this.mc)
            .drawString(
                str,
                this.mc.currentScreen.width / 2 - ChatComponent.computeFontRenderer(this.mc).getStringWidth(str) / 2,
                n + ChatComponent.computeFontRenderer(this.mc).FONT_HEIGHT,
                8421504
            );
    }

    @Override
    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
    }

    @Override
    public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
        return false;
    }

    @Override
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
    }
}
