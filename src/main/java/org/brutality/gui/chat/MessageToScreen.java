package org.brutality.gui.chat;

import java.awt.Rectangle;
import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.SearchField;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.lwjgl.input.Keyboard;

public class MessageToScreen extends CustomGuiScreen {
    private static final int count = 12;
    private static final String[] stringArray = new String[]{"gg", "truce?", "party?", "nice streak", "wanna 1v1?"};
    private final GuiScreen guiScreen;
    private final String text;
    private SearchField searchField;
    private final Rectangle rectangle = new Rectangle();
    private final Rectangle rectangle2 = new Rectangle();
    private final Rectangle[] rectangleArray = new Rectangle[stringArray.length];
    private final float[] floatValueArray = new float[stringArray.length];
    private float factor;
    private float factor2;
    private int count2;
    private int count3;
    private int count4;
    private int count5;
    private final long timestamp = System.nanoTime();
    private long timestamp2 = System.nanoTime();

    private void process() {
        String str = this.searchField.getText().trim();
        if (!str.isEmpty()) {
            mc.displayGuiScreen(null);
            if (mc.thePlayer != null) {
                mc.thePlayer.sendChatMessage("/msg " + this.text + " " + str);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            if (this.rectangle2.contains(mouseX, mouseY)) {
                mc.displayGuiScreen(this.guiScreen);
            } else if (this.rectangle.contains(mouseX, mouseY)) {
                this.process();
            } else {
                for (int i = 0; i < stringArray.length; i++) {
                    if (this.rectangleArray[i].contains(mouseX, mouseY)
                        && this.rectangleArray[i].x + this.rectangleArray[i].width <= this.count2 + this.count4 - 12) {
                        this.searchField.setText(stringArray[i]);
                        this.searchField.setFocused(true);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void updateScreen() {
        this.searchField.updateCursorCounter();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(this.guiScreen);
        } else if (keyCode != 28 && keyCode != 156) {
            this.searchField.setFocused(true);
            this.searchField.textboxKeyTyped(typedChar, keyCode);
        } else {
            this.process();
        }
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        String str = this.searchField == null ? "" : this.searchField.getText();
        this.count4 = Math.min(340, this.width - 16);
        this.count5 = 132;
        this.count2 = (this.width - this.count4) / 2;
        this.count3 = (this.height - this.count5) / 2;
        int n = this.count2 + 12;
        int n2 = this.count4 - 24;
        this.searchField = new SearchField(1, n, this.count3 + 12 + 34, n2, 20).setText2("Message to " + this.text + "...");
        this.searchField.setMaxStringLength(Math.max(20, 100 - ("/msg " + this.text + " ").length()));
        this.searchField.setText(str);
        this.searchField.setFocused(true);
        CustomFontRenderer drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(15, false);
        int n3 = n;
        int n4 = this.count3 + 12 + 34 + 20 + 8;

        for (int i = 0; i < stringArray.length; i++) {
            int n5 = drawStringWithShadowComponent.getStringWidth(stringArray[i]) + 14;
            this.rectangleArray[i] = new Rectangle(n3, n4, n5, 16);
            n3 += n5 + 4;
        }

        int n6 = this.count3 + this.count5 - 12 - 18;
        this.rectangle2.setBounds(n, n6, 70, 18);
        this.rectangle.setBounds(n + n2 - 90, n6, 90, 18);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public MessageToScreen(GuiScreen guiScreen, String text) {
        this.guiScreen = guiScreen;
        this.text = text;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        long now = System.nanoTime();
        float f = 1.0F - (float)Math.exp(-16.0F * Math.min(0.05F, (float)(now - this.timestamp2) / 1.0E9F));
        this.timestamp2 = now;
        float f2 = AvatarComponent.computeFloat(this.timestamp);
        drawRect(0, 0, this.width, this.height, (int)(140.0F * f2) << 24);
        AvatarComponent.handleFloat(f2, this.count2 + this.count4 / 2, this.count3 + this.count5 / 2);
        AvatarComponent.handleInt(this.count2, this.count3, this.count4, this.count5, f2);
        CustomFontRenderer drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(20, true);
        CustomFontRenderer drawStringWithShadowComponent2 = FontComponent.computeDrawStringWithShadowComponent2(15, false);
        int n = this.count2 + 12;
        AvatarComponent.handleString(this.text, n, this.count3 + 12, 24);
        GlStateManager.enableTexture2D();
        drawStringWithShadowComponent.drawString("Message " + this.text, n + 32, this.count3 + 12 + 1, -920327);
        drawStringWithShadowComponent2.drawString("Sends /msg " + this.text + "  ·  Enter to send", n + 32, this.count3 + 12 + 16, -8550246);
        this.searchField.drawTextBox();

        for (int i = 0; i < stringArray.length; i++) {
            if (this.rectangleArray[i].x + this.rectangleArray[i].width <= this.count2 + this.count4 - 12) {
                this.floatValueArray[i] = this.floatValueArray[i]
                    + ((this.rectangleArray[i].contains(mouseX, mouseY) ? 1.0F : 0.0F) - this.floatValueArray[i]) * f;
                AvatarComponent.handleInt(
                    this.rectangleArray[i].x,
                    this.rectangleArray[i].y,
                    this.rectangleArray[i].width,
                    this.rectangleArray[i].height,
                    stringArray[i],
                    drawStringWithShadowComponent2,
                    this.floatValueArray[i],
                    false,
                    false
                );
            }
        }

        this.factor2 = this.factor2 + ((this.rectangle2.contains(mouseX, mouseY) ? 1.0F : 0.0F) - this.factor2) * f;
        this.factor = this.factor + ((this.rectangle.contains(mouseX, mouseY) ? 1.0F : 0.0F) - this.factor) * f;
        AvatarComponent.handleInt(
            this.rectangle2.x, this.rectangle2.y, this.rectangle2.width, this.rectangle2.height, "‹  Back", drawStringWithShadowComponent2, this.factor2, false, false
        );
        AvatarComponent.handleInt(this.rectangle.x, this.rectangle.y, this.rectangle.width, this.rectangle.height, "Send", drawStringWithShadowComponent2, this.factor, false, true);
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
