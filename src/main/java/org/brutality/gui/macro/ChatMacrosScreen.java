package org.brutality.gui.macro;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.OceanComponent;
import org.brutality.gui.SearchField;
import org.brutality.gui.chat.AvatarComponent;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ChatMacrosScreen extends CustomGuiScreen {
    private static final int count = 10;
    private static final int count2 = 16;
    private static final int count3 = 5;
    private static final int count4 = 8;
    private final GuiScreen guiScreen;
    private final List<ChatMacrosScreen.Entry> entries = new ArrayList<>();
    private int count5;
    private int count6;
    private int count7;
    private int count8;
    private int count9;
    private int count10;
    private int count11;
    private int count12;
    private ChatMacrosScreen.Entry entry;
    private boolean flag;
    private Rectangle rectangle;
    private Rectangle rectangle2;
    private float factor;
    private float factor2;
    private final long timestamp = System.nanoTime();
    private long timestamp2 = System.nanoTime();

    public ChatMacrosScreen(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
    }

    private boolean matchesRectangle(Rectangle rect) {
        return rect.y >= this.count9 - 1 && rect.y + rect.height <= this.count10 + 1;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.entry != null) {
            this.handleInt(keyCode != 1 && keyCode != 14 && keyCode != 211 ? keyCode : 0);
        } else {
            for (ChatMacrosScreen.Entry entry : this.entries) {
                if (entry.searchField3.isFocused()) {
                    boolean flag = keyCode == 14 || keyCode == 211 || keyCode == 203 || keyCode == 205 || keyCode == 199 || keyCode == 207 || GuiScreen.isCtrlKeyDown();
                    if (keyCode == 1) {
                        entry.searchField3.setFocused(false);
                        return;
                    }

                    if (Character.isDigit(typedChar) || flag) {
                        entry.searchField3.textboxKeyTyped(typedChar, keyCode);
                    }

                    return;
                }

                if (entry.searchField.isFocused() || entry.searchField2.isFocused()) {
                    if (keyCode == 1) {
                        entry.searchField.setFocused(false);
                        entry.searchField2.setFocused(false);
                        return;
                    }
                    (entry.searchField.isFocused() ? entry.searchField : entry.searchField2).textboxKeyTyped(typedChar, keyCode);
                    return;
                }
            }

            if (keyCode == 1) {
                mc.displayGuiScreen(this.guiScreen);
            }
        }
    }

    private static void handleSearchField(SearchField searchField, int n, int n2, int n3) {
        searchField.xPosition = n;
        searchField.yPosition = n2;
        searchField.handleInt(n3);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.entry != null && this.flag) {
            this.handleInt(mouseButton - 100);
        } else {
            this.entry = null;

            for (ChatMacrosScreen.Entry entry : this.entries) {
                if (!this.matchesRectangle(entry.rectangle)) {
                    for (SearchField searchField : entry.computeSearchFieldArray()) {
                        searchField.setFocused(false);
                    }
                } else {
                    for (SearchField searchField2 : entry.computeSearchFieldArray()) {
                        searchField2.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
            }

            if (mouseButton == 0) {
                if (this.rectangle.contains(mouseX, mouseY)) {
                    this.process3();
                    OnKeyPressedComponent.Entry entry2 = new OnKeyPressedComponent.Entry();
                    OnKeyPressedComponent.computeList().add(entry2);
                    OnKeyPressedComponent.save();
                    this.entries.add(new ChatMacrosScreen.Entry(entry2));
                    this.count11 = Integer.MAX_VALUE;
                } else if (this.rectangle2.contains(mouseX, mouseY)) {
                    mc.displayGuiScreen(this.guiScreen);
                } else {
                    for (ChatMacrosScreen.Entry entry3 : new ArrayList<>(this.entries)) {
                        if (this.matchesRectangle(entry3.rectangle)) {
                            if (entry3.rectangle.contains(mouseX, mouseY)) {
                                for (ChatMacrosScreen.Entry entry4 : this.entries) {
                                    for (SearchField searchField3 : entry4.computeSearchFieldArray()) {
                                        searchField3.setFocused(false);
                                    }
                                }

                                this.entry = entry3;
                                this.flag = false;
                                return;
                            }

                            if (entry3.rectangle2.contains(mouseX, mouseY)) {
                                this.process3();
                                OnKeyPressedComponent.computeList().remove(entry3.entry);
                                this.entries.remove(entry3);
                                OnKeyPressedComponent.save();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleInt(int n) {
        this.entry.entry.key = n;
        this.entry = null;
        this.process3();
        OnKeyPressedComponent.save();
    }

    private void process3() {
        for (ChatMacrosScreen.Entry entry : this.entries) {
            entry.process();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (this.entry != null) {
            this.flag = true;
        }
    }

    private void process2() {
        this.count7 = Math.min(440, this.width - 16);
        int n = Math.max(1, Math.min(8, this.entries.size()));
        int n2 = this.entries.isEmpty() ? 34 : n * 21 - 5;
        this.count8 = Math.min(this.height - 12, 54 + n2 + 12 + 18 + 10);
        this.count5 = (this.width - this.count7) / 2;
        this.count6 = (this.height - this.count8) / 2;
        this.count9 = this.count6 + 10 + 30 + 14;
        this.count10 = this.count6 + this.count8 - 10 - 18 - 12;
        this.rectangle = new Rectangle(this.count5 + 10, this.count6 + this.count8 - 10 - 18, 92, 18);
        this.rectangle2 = new Rectangle(this.count5 + this.count7 - 10 - 64, this.count6 + this.count8 - 10 - 18, 64, 18);
    }

    private void process() {
        int n = this.count5 + 10;
        int n2 = this.count7 - 20;
        byte b = 82;
        byte b2 = 62;
        byte b3 = 42;
        byte b4 = 16;
        byte b5 = 16;
        byte b6 = 4;
        int n3 = n2 - b - b2 - b3 - b4 - b5 - b6 * 4;
        this.count12 = Math.max(0, this.entries.size() * 21 - 5 - (this.count10 - this.count9));
        this.count11 = Math.max(0, Math.min(this.count11, this.count12));

        for (int i = 0; i < this.entries.size(); i++) {
            ChatMacrosScreen.Entry entry = this.entries.get(i);
            int n4 = this.count9 + i * 21 - this.count11;
            handleSearchField(entry.searchField, n, n4, b);
            int n5 = n + b + b6;
            handleSearchField(entry.searchField2, n5, n4, n3);
            n5 += n3 + b6;
            entry.rectangle.setBounds(n5, n4, b2, 16);
            n5 += b2 + b6;
            handleSearchField(entry.searchField3, n5, n4, b3);
            n5 += b3 + b4 + b6;
            entry.rectangle2.setBounds(n5, n4, b5, 16);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        long now = System.nanoTime();
        float f = 1.0F - (float)Math.exp(-16.0F * Math.min(0.05F, (float)(now - this.timestamp2) / 1.0E9F));
        this.timestamp2 = now;
        this.process2();
        this.process();
        float f2 = AvatarComponent.computeFloat(this.timestamp);
        if (mc.theWorld == null) {
            OceanComponent.handleGuiScreen(this, mouseX, mouseY, partialTicks);
        }

        drawRect(0, 0, this.width, this.height, (int)(120.0F * f2) << 24);
        AvatarComponent.handleFloat(f2, this.count5 + this.count7 / 2, this.count6 + this.count8 / 2);
        AvatarComponent.handleInt(this.count5, this.count6, this.count7, this.count8, f2);
        CustomFontRenderer drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(20, true);
        CustomFontRenderer drawStringWithShadowComponent2 = FontComponent.computeDrawStringWithShadowComponent2(15, false);
        GlStateManager.enableTexture2D();
        drawStringWithShadowComponent.drawString("Chat Macros", this.count5 + 10, this.count6 + 10, -920327);
        drawStringWithShadowComponent2.drawString("Press the key in game to send once after the delay. Esc while binding = None.", this.count5 + 10, this.count6 + 10 + 16, -8550246);
        int n = this.count9 - 11;
        drawStringWithShadowComponent2.drawString("NAME", this.count5 + 10 + 2, n, -9669242);
        if (!this.entries.isEmpty()) {
            ChatMacrosScreen.Entry entry = this.entries.get(0);
            drawStringWithShadowComponent2.drawString("MESSAGE", entry.searchField2.xPosition + 2, n, -9669242);
            drawStringWithShadowComponent2.drawString("KEY", entry.rectangle.x + 2, n, -9669242);
            drawStringWithShadowComponent2.drawString("DELAY", entry.searchField3.xPosition + 2, n, -9669242);
        }

        if (this.entries.isEmpty()) {
            String str = "No macros yet - add one to send things like /play pit with a key.";
            drawStringWithShadowComponent2.drawString(str, this.count5 + (this.count7 - drawStringWithShadowComponent2.getStringWidth(str)) / 2, this.count9 + 12, -7497816);
        }

        for (ChatMacrosScreen.Entry entry2 : this.entries) {
            if (this.matchesRectangle(entry2.rectangle)) {
                for (SearchField searchField : entry2.computeSearchFieldArray()) {
                    searchField.drawTextBox();
                }

                GlStateManager.enableTexture2D();
                drawStringWithShadowComponent2.drawString("ms", entry2.searchField3.xPosition + entry2.searchField3.getCount2() + 3, entry2.searchField3.yPosition + 5, -9669242);
                entry2.factor = entry2.factor + ((entry2.rectangle.contains(mouseX, mouseY) ? 1.0F : 0.0F) - entry2.factor) * f;
                String str2 = this.entry == entry2 ? "Press..." : OnKeyPressedComponent.computeString(entry2.entry.key);
                AvatarComponent.handleInt(
                    entry2.rectangle.x, entry2.rectangle.y, entry2.rectangle.width, entry2.rectangle.height, str2, drawStringWithShadowComponent2, entry2.factor, this.entry == entry2, false
                );
                entry2.factor2 = entry2.factor2 + ((entry2.rectangle2.contains(mouseX, mouseY) ? 1.0F : 0.0F) - entry2.factor2) * f;
                AvatarComponent.handleInt(
                    entry2.rectangle2.x, entry2.rectangle2.y, entry2.rectangle2.width, entry2.rectangle2.height, "x", drawStringWithShadowComponent2, entry2.factor2, false, false
                );
            }
        }

        if (this.count12 > 0) {
            int n2 = this.count10 - this.count9;
            int n3 = Math.max(14, n2 * n2 / (n2 + this.count12));
            int n4 = this.count9 + (n2 - n3) * this.count11 / this.count12;
            drawRect(this.count5 + this.count7 - 5, n4, this.count5 + this.count7 - 3, n4 + n3, -1593835521);
        }

        this.factor = this.factor + ((this.rectangle.contains(mouseX, mouseY) ? 1.0F : 0.0F) - this.factor) * f;
        this.factor2 = this.factor2 + ((this.rectangle2.contains(mouseX, mouseY) ? 1.0F : 0.0F) - this.factor2) * f;
        AvatarComponent.handleInt(
            this.rectangle.x, this.rectangle.y, this.rectangle.width, this.rectangle.height, "+ Add macro", drawStringWithShadowComponent2, this.factor, false, true
        );
        AvatarComponent.handleInt(this.rectangle2.x, this.rectangle2.y, this.rectangle2.width, this.rectangle2.height, "Done", drawStringWithShadowComponent2, this.factor2, false, false);
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        for (ChatMacrosScreen.Entry entry : this.entries) {
            for (SearchField searchField : entry.computeSearchFieldArray()) {
                searchField.updateCursorCounter();
            }
        }
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        for (ChatMacrosScreen.Entry entry : this.entries) {
            entry.process();
        }

        this.entries.clear();

        for (OnKeyPressedComponent.Entry entry2 : OnKeyPressedComponent.computeList()) {
            this.entries.add(new ChatMacrosScreen.Entry(entry2));
        }

        this.process2();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        this.process3();
        OnKeyPressedComponent.save();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int eventDWheel = Mouse.getEventDWheel();
        if (eventDWheel != 0) {
            this.count11 = Math.max(0, Math.min(this.count12, this.count11 - Integer.signum(eventDWheel) * 21));
        }
    }

    public final class Entry {
        public final OnKeyPressedComponent.Entry entry;
        public final SearchField searchField;
        public final SearchField searchField2;
        public final SearchField searchField3;
        public Rectangle rectangle = new Rectangle();
        public Rectangle rectangle2 = new Rectangle();
        public float factor;
        public float factor2;
        private static final long timestamp = -1175423516875363744L;

        public Entry(OnKeyPressedComponent.Entry entry) {
            this.entry = entry;
            this.searchField = new SearchField(0, 0, 0, 10, 16).setText2("Name");
            this.searchField.setMaxStringLength(24);
            this.searchField.setText(entry.name);
            this.searchField2 = new SearchField(1, 0, 0, 10, 16).setText2("/play pit");
            this.searchField2.setMaxStringLength(100);
            this.searchField2.setText(entry.message);
            this.searchField3 = new SearchField(2, 0, 0, 10, 16).setText2("0");
            this.searchField3.setMaxStringLength(5);
            this.searchField3.setText(Integer.toString(entry.count));
        }

        public void process() {
            this.entry.name = this.searchField.getText().trim().isEmpty() ? "Macro" : this.searchField.getText().trim();
            this.entry.message = this.searchField2.getText();

            try {
                this.entry.count = Math.max(0, Math.min((int)timestamp, Integer.parseInt(this.searchField3.getText().trim())));
            } catch (NumberFormatException e) {
                this.entry.count = 0;
            }
        }

        public SearchField[] computeSearchFieldArray() {
            return new SearchField[]{this.searchField, this.searchField2, this.searchField3};
        }
    }
}
