package org.brutality.clickgui.discord;

import net.minecraft.client.gui.Gui;
import org.brutality.gui.font.atlas.SFProApi;

public class GetOffsetComponent {
    private final DiscordGUI discordGUI;
    private final String name;
    private boolean flag;
    private int y2;
    private int offset2 = 30;

    public String getName() {
        return this.name;
    }

    public GetOffsetComponent(DiscordGUI discordGUI, String name) {
        this.discordGUI = discordGUI;
        this.name = name;
    }

    public boolean checkState() {
        int n = this.discordGUI.getCount5();
        return this.y2 < n + 30 || this.y2 > n + this.discordGUI.getHeight() - 9;
    }

    public boolean isFlag() {
        return this.flag;
    }

    public int getOffset() {
        return this.offset2;
    }

    public int getY() {
        return this.y2;
    }

    public boolean matchesInt(int n, int n2) {
        int n3 = this.discordGUI.getCount4();
        int width = this.discordGUI.getWidth();
        return n >= n3 + 45 + 110
            && n <= n3 + 45 + 110 + width
            && n2 >= this.y2 - 6
            && n2 <= this.y2 + SFProApi.Listener7.Entry4.apiApi.getHeight() + 5;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void handleInt(int n, int n2) {
        int n3 = this.discordGUI.getCount4();
        int width = this.discordGUI.getWidth();
        if (this.matchesInt(n, n2)) {
            Gui.drawRect(n3 + 45 + 110, this.y2 - 6, n3 + 45 + 105 + width, this.y2 + SFProApi.Listener7.Entry4.apiApi.getHeight() + 5, -13684426);
        }

        SFProApi.Listener7.Entry4.apiApi.computeFloat(this.name, n3 + 45 + 110 + width / 2.0F, this.y2, this.flag ? -1 : -7961722);
    }

    public void update() {
        this.y2 = this.discordGUI.getCount5() + this.offset2 + this.discordGUI.getEntries2().indexOf(this) * 20;
    }

    public void setOffset2(int offset2) {
        this.offset2 = offset2;
    }
}
