package org.brutality.clickgui.newclick;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.gui.ScaledResolution;
import org.brutality.clickgui.discord.ColorUtils;
import org.brutality.clickgui.discord.Inter18ptSemiboldTtfComponent;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.system.stafftracker.StaffTrackerService;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiCloudUpload extends CustomGuiScreen {
    private static final int count = new Color(80, 200, 110).getRGB();
    private static final int count2 = new Color(220, 70, 70).getRGB();
    private static final int count3 = new Color(220, 220, 225).getRGB();
    private static final int count4 = new Color(120, 120, 128).getRGB();
    private static final int count5 = new Color(18, 18, 22, 245).getRGB();
    private static final int count6 = new Color(48, 48, 56, 255).getRGB();
    private final NewClickGUI newClickGUI;
    private final Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent = new Inter18ptSemiboldTtfComponent();
    private final List<StaffTrackerService.Entry2> entries = new CopyOnWriteArrayList<>();
    private volatile boolean loading;
    private volatile String text = "Loading…";
    private volatile long timestamp;
    private long timestamp2;
    private float factor;
    private int count7;
    private int count8;
    private int count9;
    private int count10;
    private int count11;
    private int count12;

    private static int computeInt(StaffTrackerService.OnlineMode onlineMode) {
        if (onlineMode == StaffTrackerService.OnlineMode.ONLINE) {
            return count;
        }
        return onlineMode != StaffTrackerService.OnlineMode.OFFLINE && onlineMode != StaffTrackerService.OnlineMode.NOT_FOUND ? count2 : count2;
    }

    private void handleEntry2(StaffTrackerService.Entry2 entry2, int n, int n2, int n3) {
        int n4 = computeInt(entry2.onlineMode);
        this.inter18ptSemiboldTtfComponent.handleString(entry2.text, n, n2, count3);
        int n5 = n + this.inter18ptSemiboldTtfComponent.computeInt2(entry2.text);
        String str = " | ";
        this.inter18ptSemiboldTtfComponent.handleString(str, n5, n2, count4);
        int n6 = n5 + this.inter18ptSemiboldTtfComponent.computeInt2(str);
        String str2 = entry2.computeString();
        int n7 = n3 - (n6 - n);
        if (this.inter18ptSemiboldTtfComponent.computeInt2(str2) > n7 && n7 > 20) {
            str2 = this.inter18ptSemiboldTtfComponent.computeString(str2, n7);
            this.inter18ptSemiboldTtfComponent.handleString2(str2, n6, n2 + 1, n4);
        } else {
            this.inter18ptSemiboldTtfComponent.handleString(str2, n6, n2, n4);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.process2();
        if (this.entries.isEmpty() || System.currentTimeMillis() - this.timestamp2 > 30000L) {
            this.process3();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void process() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.process2();
        this.drawDefaultBackground();
        if (!this.loading && System.currentTimeMillis() - this.timestamp2 > 300000L) {
            this.process3();
        }

        drawRect(this.count7 - 1, this.count8 - 1, this.count7 + this.count9 + 1, this.count8 + this.count10 + 1, count6);
        drawRect(this.count7, this.count8, this.count7 + this.count9, this.count8 + this.count10, count5);
        this.inter18ptSemiboldTtfComponent.handleString3("Staff Tracker", this.count7 + 14, this.count8 + 10, ColorUtils.color14.getRGB());
        byte b = 64;
        byte b2 = 16;
        int n = this.count7 + this.count9 - b - 12;
        int n2 = this.count8 + 12;
        boolean flag = matchesInt(mouseX, mouseY, n, n2, b, b2);
        drawRect(n, n2, n + b, n2 + b2, flag ? new Color(40, 40, 48, 255).getRGB() : new Color(28, 28, 34, 255).getRGB());
        this.inter18ptSemiboldTtfComponent.handleString2(this.loading ? "…" : "Refresh", n + 14, n2 + 4, ColorUtils.color15.getRGB());
        this.inter18ptSemiboldTtfComponent.handleString2("ESC to close", this.count7 + 14, this.count8 + 30, ColorUtils.color16.getRGB());
        int n3 = this.count7 + 10;
        int n4 = this.count11;
        int n5 = this.count9 - 20;
        int n6 = this.count12 - this.count11;
        drawRect(n3, n4, n3 + n5, n4 + n6, new Color(12, 12, 14, 255).getRGB());
        drawRect(n3, n4, n3 + n5, n4 + 1, new Color(35, 35, 42).getRGB());
        this.handleInt(n3, n4 + 4, n5, n6 - 8);
        byte b3 = 14;
        byte b4 = 28;
        int n7 = this.entries.isEmpty() ? 1 : this.entries.size();
        int n8 = b4 + n7 * b3;
        int n9 = n6 - 12;
        float f = Math.max(0, n8 - n9);
        if (this.factor > f) {
            this.factor = f;
        }

        if (this.factor < 0.0F) {
            this.factor = 0.0F;
        }

        int n10 = n4 + 8 - (int)this.factor;
        this.inter18ptSemiboldTtfComponent.handleString("Staff Checker", n3 + 10, n10, ColorUtils.color14.getRGB());
        int n11 = n10 + 18;
        if (this.loading && this.entries.isEmpty()) {
            this.inter18ptSemiboldTtfComponent.handleString2("Loading…", n3 + 10, n11, ColorUtils.color16.getRGB());
        } else if (!this.entries.isEmpty() && !"API FAILED".equals(this.text)) {
            for (StaffTrackerService.Entry2 entry2 : this.entries) {
                this.handleEntry2(entry2, n3 + 10, n11, n5 - 20);
                n11 += b3;
            }
        } else {
            this.inter18ptSemiboldTtfComponent.handleString("API FAILED", n3 + 10, n11, count2);
        }

        this.process();
        String str;
        if (this.text != null && !this.entries.isEmpty()) {
            str = this.text;
        } else if (this.text != null && this.entries.isEmpty() && !this.loading) {
            str = "";
        } else if (this.timestamp > 0L) {
            str = "Last update: " + computeString(this.timestamp);
        } else {
            str = "";
        }

        this.inter18ptSemiboldTtfComponent.handleString2(str, this.count7 + 14, this.count8 + this.count10 - 22, ColorUtils.color16.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void process3() {
        if (!this.loading) {
            this.loading = true;
            this.text = "Refreshing…";
            StaffTrackerService.handleConsumer(entry -> mc.addScheduledTask(() -> this.handleEntry(entry)));
        }
    }

    private void handleInt(int n, int n2, int n3, int n4) {
        ScaledResolution sr = new ScaledResolution(mc);
        int scaleFactor = sr.getScaleFactor();
        int n5 = n * scaleFactor;
        int n6 = (sr.getScaledHeight() - (n2 + n4)) * scaleFactor;
        int n7 = n3 * scaleFactor;
        int n8 = n4 * scaleFactor;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(n5, n6, Math.max(0, n7), Math.max(0, n8));
    }

    private void handleEntry(StaffTrackerService.Entry entry) {
        this.loading = false;
        this.timestamp2 = System.currentTimeMillis();
        if (entry != null && entry.ok) {
            this.entries.clear();
            this.entries.addAll(entry.entries);
            this.timestamp = entry.updatedAtMs > 0L ? entry.updatedAtMs : this.timestamp2;
            if (this.entries.isEmpty()) {
                this.text = "API FAILED";
            } else if (matchesList(this.entries)) {
                this.text = "API FAILED";
            } else {
                this.text = null;
            }

            this.factor = 0.0F;
        } else {
            this.entries.clear();
            this.text = "API FAILED";
        }
    }

    public GuiCloudUpload(NewClickGUI newClickGUI) {
        this.newClickGUI = newClickGUI;
    }

    private void process2() {
        ScaledResolution sr = new ScaledResolution(mc);
        this.count9 = Math.min(380, Math.max(300, sr.getScaledWidth() - 80));
        this.count10 = Math.min(440, Math.max(280, sr.getScaledHeight() - 60));
        this.count7 = (sr.getScaledWidth() - this.count9) / 2;
        this.count8 = (sr.getScaledHeight() - this.count10) / 2;
        this.count11 = this.count8 + 52;
        this.count12 = this.count8 + this.count10 - 36;
    }

    private static String computeString(long l) {
        long l2 = Math.max(0L, System.currentTimeMillis() - l) / 1000L;
        if (l2 < 60L) {
            return l2 + " seconds ago";
        }
        if (l2 < 3600L) {
            return l2 / 60L + " minutes ago";
        }
        return l2 < 86400L ? l2 / 3600L + " hours ago" : l2 / 86400L + " days ago";
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int eventDWheel = Mouse.getEventDWheel();
        if (eventDWheel != 0) {
            this.factor -= eventDWheel > 0 ? 22.0F : -22.0F;
        }
    }

    private static boolean matchesList(List<StaffTrackerService.Entry2> entry2s) {
        if (entry2s.isEmpty()) {
            return true;
        }
        for (StaffTrackerService.Entry2 entry2 : entry2s) {
            if (entry2.onlineMode != StaffTrackerService.OnlineMode.ERROR && entry2.onlineMode != StaffTrackerService.OnlineMode.UNKNOWN) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            byte b = 64;
            byte b2 = 16;
            int n = this.count7 + this.count9 - b - 12;
            int n2 = this.count8 + 12;
            if (matchesInt(mouseX, mouseY, n, n2, b, b2)) {
                this.process3();
                return;
            }

            if (mouseX < this.count7 || mouseX > this.count7 + this.count9 || mouseY < this.count8 || mouseY > this.count8 + this.count10) {
                mc.displayGuiScreen(this.newClickGUI);
                return;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private static boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6) {
        return n >= n3 && n <= n3 + n5 && n2 >= n4 && n2 <= n4 + n6;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(this.newClickGUI);
        } else if (keyCode != 19 || !Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && !Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
            super.keyTyped(typedChar, keyCode);
        } else {
            this.process3();
        }
    }
}
