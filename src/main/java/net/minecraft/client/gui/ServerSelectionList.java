package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector.LanServer;
import net.minecraft.util.EnumChatFormatting;

public class ServerSelectionList extends GuiListExtended {
    private final GuiMultiplayer owner;
    private final List<ServerListEntryNormal> field_148198_l = Lists.newArrayList();
    private final List<ServerListEntryLanDetected> field_148199_m = Lists.newArrayList();
    private final IGuiListEntry lanScanEntry = new ServerListEntryLanScan();
    private int selectedSlotIndex = -1;
    private String filter = "";

    public ServerSelectionList(GuiMultiplayer ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.owner = ownerIn;
    }

    @Override
    protected boolean usesThemedStyle() {
        return true;
    }

    @Override
    public IGuiListEntry getListEntry(int index) {
        if (index < this.field_148198_l.size()) {
            return this.field_148198_l.get(index);
        }
        index -= this.field_148198_l.size();
        return index == 0 ? this.lanScanEntry : this.field_148199_m.get(--index);
    }

    @Override
    protected int getSize() {
        return this.field_148198_l.size() + 1 + this.field_148199_m.size();
    }

    public void setSelectedSlotIndex(int selectedSlotIndexIn) {
        this.selectedSlotIndex = selectedSlotIndexIn;
    }

    @Override
    protected boolean isSelected(int slotIndex) {
        return slotIndex == this.selectedSlotIndex && !this.owner.isDraggingServerEntry(slotIndex);
    }

    @Override
    protected void drawSlot(int index, int x, int y, int height, int mouseX, int mouseY) {
        if (this.owner.isDraggingServerEntry(index)) {
            Gui.drawRect(x, Math.max(y, this.top), x + this.getListWidth(), Math.min(y + height, this.bottom), 570425344);
        } else {
            super.drawSlot(index, x, y, height, mouseX, mouseY);
        }
    }

    public int getEntryX() {
        return this.left + this.width / 2 - this.getListWidth() / 2 + 2;
    }

    public int getEntryY(int index) {
        return this.top + 4 + this.headerPadding + index * this.slotHeight - this.getAmountScrolled();
    }

    public int getTop() {
        return this.top;
    }

    public int getBottom() {
        return this.bottom;
    }

    public int func_148193_k() {
        return this.selectedSlotIndex;
    }

    public void setFilter(String filter) {
        this.filter = filter == null ? "" : filter.trim().toLowerCase(Locale.ROOT);
    }

    public int countServerEntries() {
        return this.field_148198_l.size();
    }

    public boolean isFiltered() {
        return !this.filter.isEmpty();
    }

    public void func_148195_a(ServerList p_148195_1_) {
        this.field_148198_l.clear();

        for (int i = 0; i < p_148195_1_.countServers(); i++) {
            ServerData serverData = p_148195_1_.getServerData(i);
            if (this.isFiltered()) {
                String str = serverData.serverName == null ? "" : EnumChatFormatting.getTextWithoutFormattingCodes(serverData.serverName).toLowerCase(Locale.ROOT);
                String str2 = serverData.serverIP == null ? "" : serverData.serverIP.toLowerCase(Locale.ROOT);
                if (!str.contains(this.filter) && !str2.contains(this.filter)) {
                    continue;
                }
            }

            this.field_148198_l.add(new ServerListEntryNormal(this.owner, serverData));
        }
    }

    public void func_148194_a(List<LanServer> p_148194_1_) {
        this.field_148199_m.clear();

        for (LanServer lanServer : p_148194_1_) {
            this.field_148199_m.add(new ServerListEntryLanDetected(this.owner, lanServer));
        }
    }

    @Override
    public int getScrollBarX() {
        return super.getScrollBarX() + 30;
    }

    @Override
    public int getListWidth() {
        return super.getListWidth() + 85;
    }

    public int getLeft() {
        return this.left;
    }

    public int getRight() {
        return this.right;
    }
}
