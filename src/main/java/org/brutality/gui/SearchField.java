package org.brutality.gui;

import java.awt.Color;
import java.nio.IntBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.gui.mainmenu.effects.MainMenuIconRenderer;
import org.brutality.utils.render.OutlineThicknessHelper;
import org.brutality.utils.render.GlStateGuard;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class SearchField extends GuiTextField {
    private static final int count = 7;
    private int count2;
    private boolean canLoseFocus = true;
    private String text = "";
    private boolean flag;
    private int count3;
    private float factor;
    private long timestamp = System.nanoTime();
    private long timestamp2 = System.currentTimeMillis();
    private int count4 = -1;
    private boolean flag2;
    private final IntBuffer intBuffer = BufferUtils.createIntBuffer(16);

    private void process() {
        if (this.flag2) {
            GL11.glScissor(this.intBuffer.get(0), this.intBuffer.get(1), this.intBuffer.get(2), this.intBuffer.get(3));
        } else {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    public int getCount2() {
        return this.count2;
    }

    @Override
    public void drawTextBox() {
        if (this.getVisible()) {
            try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                GlStateManager.disableDepth();
                GlStateManager.disableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                long now = System.nanoTime();
                float f = Math.min(0.05F, (float)(now - this.timestamp) / 1.0E9F);
                this.timestamp = now;
                this.factor = this.factor + ((this.isFocused() ? 1.0F : 0.0F) - this.factor) * (1.0F - (float)Math.exp(-14.0F * f));
                Color color = OceanComponent.computeColor();
                float f2 = Math.min(6.0F, this.height / 2.0F);
                if (this.getEnableBackgroundDrawing()) {
                    if (OpenGlHelper.shadersSupported && OutlineThicknessHelper.shaderEffect2 != null) {
                        OutlineThicknessHelper.handleFloat(
                            this.xPosition, this.yPosition, this.count2, this.height, f2, new Color(8, 9, 14, 185 + (int)(30.0F * this.factor))
                        );
                        Color color2 = new Color(
                            (int)(255.0F + (color.getRed() - 255) * this.factor),
                            (int)(255.0F + (color.getGreen() - 255) * this.factor),
                            (int)(255.0F + (color.getBlue() - 255) * this.factor),
                            30 + (int)(130.0F * this.factor)
                        );
                        OutlineThicknessHelper.handleFloat(this.xPosition, this.yPosition, this.count2, this.height, f2, 0.5F, new Color(0, 0, 0, 0), color2);
                    } else {
                        Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.count2, this.yPosition + this.height, -804779504);
                    }
                }

                CustomFontRenderer drawStringWithShadowComponent = this.computeDrawStringWithShadowComponent();
                String str = this.computeString();
                int n = Math.max(4, this.count2 - 14);
                int n2 = Math.min(this.getCursorPosition(), str.length());
                int n3 = Math.min(this.getSelectionEnd(), str.length());
                if (n2 != this.count4) {
                    this.count4 = n2;
                    this.timestamp2 = System.currentTimeMillis();
                }

                int n4 = this.computeInt(drawStringWithShadowComponent, str, n2);
                int textWidth = drawStringWithShadowComponent.getStringWidth(str);
                if (n4 - this.count3 > n) {
                    this.count3 = n4 - n;
                }

                if (n4 < this.count3) {
                    this.count3 = n4;
                }

                this.count3 = Math.max(0, Math.min(this.count3, Math.max(0, textWidth - n + 1)));
                int n5 = this.xPosition + 7 - this.count3;
                int n6 = this.yPosition + (this.height - drawStringWithShadowComponent.FONT_HEIGHT) / 2 + 1;
                this.handleInt(this.xPosition + 7 - 1, this.yPosition, n + 2, this.height);

                try {
                    if (str.isEmpty() && !this.text.isEmpty() && !this.isFocused()) {
                        drawStringWithShadowComponent.drawString(this.text, this.xPosition + 7, n6, -9471863);
                    } else if (str.isEmpty() && !this.text.isEmpty()) {
                        drawStringWithShadowComponent.drawString(this.text, this.xPosition + 7, n6, -2140178295);
                    }

                    if (n3 != n2) {
                        int n7 = n5 + this.computeInt(drawStringWithShadowComponent, str, Math.min(n3, n2));
                        int n8 = n5 + this.computeInt(drawStringWithShadowComponent, str, Math.max(n3, n2));
                        Gui.drawRect(n7, this.yPosition + 4, n8, this.yPosition + this.height - 4, MainMenuIconRenderer.computeColor(color, 95).getRGB());
                    }

                    GlStateManager.enableTexture2D();
                    drawStringWithShadowComponent.drawString(str, n5, n6, -1511947);
                    boolean flag = (System.currentTimeMillis() - this.timestamp2) / 530L % 2L == 0L;
                    if (this.isFocused() && flag) {
                        int n9 = n5 + n4;
                        Gui.drawRect(n9, this.yPosition + 5, n9 + 1, this.yPosition + this.height - 5, -854534);
                    }
                } finally {
                    this.process();
                }
            }
        }
    }

    private int computeInt(CustomFontRenderer drawStringWithShadowComponent, String str, int n) {
        return drawStringWithShadowComponent.getStringWidth(str.substring(0, Math.max(0, Math.min(n, str.length()))));
    }

    private CustomFontRenderer computeDrawStringWithShadowComponent() {
        return FontComponent.computeDrawStringWithShadowComponent2(Math.max(14, Math.min(20, this.height - 2)), false);
    }

    @Override
    public void setCanLoseFocus(boolean canLoseFocus) {
        super.setCanLoseFocus(canLoseFocus);
        this.canLoseFocus = canLoseFocus;
    }

    public SearchField(int n, int n2, int n3, int n4, int n5) {
        super(n, Minecraft.getMinecraft().fontRendererObj, n2, n3, n4, n5);
        this.count2 = n4;
    }

    private void handleInt(int n, int n2, int n3, int n4) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        int scaleFactor = sr.getScaleFactor();
        this.flag2 = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
        this.intBuffer.clear();
        GL11.glGetInteger(GL11.GL_SCISSOR_BOX, this.intBuffer);
        int n5 = n * scaleFactor;
        int n6 = mc.displayHeight - (n2 + n4) * scaleFactor;
        int n7 = n5 + n3 * scaleFactor;
        int n8 = n6 + n4 * scaleFactor;
        if (this.flag2) {
            n5 = Math.max(n5, this.intBuffer.get(0));
            n6 = Math.max(n6, this.intBuffer.get(1));
            n7 = Math.min(n7, this.intBuffer.get(0) + this.intBuffer.get(2));
            n8 = Math.min(n8, this.intBuffer.get(1) + this.intBuffer.get(3));
        }

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(n5, n6, Math.max(0, n7 - n5), Math.max(0, n8 - n6));
    }

    public void handleInt(int n) {
        this.count2 = Math.max(10, n);
    }

    private String computeString() {
        String text = this.getText();
        if (!this.flag) {
            return text;
        }
        StringBuilder sb = new StringBuilder(text.length());

        for (int i = 0; i < text.length(); i++) {
            sb.append('*');
        }

        return sb.toString();
    }

    public SearchField setFlag(boolean flag) {
        this.flag = flag;
        return this;
    }

    public SearchField setText2(String str) {
        this.text = str == null ? "" : str;
        return this;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean flag = mouseX >= this.xPosition && mouseX < this.xPosition + this.count2 && mouseY >= this.yPosition && mouseY < this.yPosition + this.height;
        if (this.canLoseFocus) {
            this.setFocused(flag);
        }

        if (this.isFocused() && flag && mouseButton == 0) {
            CustomFontRenderer drawStringWithShadowComponent = this.computeDrawStringWithShadowComponent();
            String str = this.computeString();
            float f = mouseX - (this.xPosition + 7) + this.count3;
            int n = 0;
            float MAX_VALUE = Float.MAX_VALUE;

            for (int i = 0; i <= str.length(); i++) {
                float f2 = Math.abs(this.computeInt(drawStringWithShadowComponent, str, i) - f);
                if (f2 < MAX_VALUE) {
                    MAX_VALUE = f2;
                    n = i;
                }
            }

            this.setCursorPosition(n);
            this.timestamp2 = System.currentTimeMillis();
        }
    }
}
