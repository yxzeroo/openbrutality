package org.brutality.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.gui.mainmenu.base.MainMenuScene;
import org.brutality.gui.mainmenu.effects.MainMenuIconRenderer;
import org.brutality.gui.mainmenu.effects.NebulaComponent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class RemadeScreen extends CustomGuiScreen {
    private Color color = OceanComponent.computeColor();
    private static final Color color2 = new Color(238, 244, 250);
    private static final Color color3 = new Color(167, 184, 202);
    private static final Color color4 = new Color(111, 134, 157);
    private static final Color color5 = new Color(15, 24, 38, 210);
    private final GuiScreen guiScreen;
    private final ChangeLog.Entry entry;
    private final CustomFontRenderer drawStringWithShadowComponent;
    private final CustomFontRenderer drawStringWithShadowComponent2;
    private int count = 0;
    private int count2 = 0;
    private int count3 = 0;
    private int count4;
    private int count5;
    private int count6;
    private int count7;
    private int count8;
    private int count9;
    private CustomFontRenderer drawStringWithShadowComponent3;

    private String computeString(String str) {
        String str2 = str.toLowerCase();
        if (str2.startsWith("remade") || str2.startsWith("rewrote")) {
            return "REMADE";
        }
        if (str2.startsWith("added") || str2.startsWith("add ")) {
            return "ADDED";
        }
        if (str2.startsWith("improved") || str2.startsWith("improve ")) {
            return "IMPROVED";
        }
        if (str2.startsWith("fixed") || str2.startsWith("fix ")) {
            return "FIXED";
        }
        if (str2.startsWith("removed") || str2.startsWith("remove ")) {
            return "REMOVED";
        }
        return str2.startsWith("unpatched") ? "UNPATCHED" : "";
    }

    private void process3() {
        int n = this.count4 + this.count6 - 8;
        int n2 = this.count8;
        int n3 = this.count9;
        drawRect(n, n2, n + 4, n2 + n3, new Color(30, 10, 10, 100).getRGB());
        float f = (float)this.count9 / (this.count3 + 10);
        int n4 = Math.max(20, (int)(n3 * f));
        int n5 = n2 + (this.count2 > 0 ? (int)((float)this.count / this.count2 * (n3 - n4)) : 0);
        drawRect(n, n5, n + 4, n5 + n4, new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), 180).getRGB());
    }

    private void process2() {
        int n = this.count8 - this.count;
        String str = "";
        boolean flag = this.entry.entries.stream().anyMatch(ChangeLog::matchesString);

        for (String str2 : this.entry.entries) {
            boolean flag2 = ChangeLog.matchesString(str2);
            String str3 = flag2 ? str2 : (flag ? str : this.computeString(str2));
            if (!str3.equals(str)) {
                str = str3;
                if (n + 16 >= this.count8 && n < this.count8 + this.count9) {
                    int n2 = this.computeInt(str3);
                    if (!str3.isEmpty()) {
                        drawRect(this.count4 + 14, n + 6, this.count4 + 14 + 3, n + 14, n2);
                        this.drawStringWithShadowComponent.drawStringWithShadow(str3, this.count4 + 22, n + 6, n2);
                    }
                }

                n += 16;
            }

            if (!flag2) {
                List list = this.computeList(str2, this.count6 - 70);

                for (int i = 0; i < list.size(); i++) {
                    if (n >= this.count8 && n < this.count8 + this.count9) {
                        String str4 = i == 0 ? "• " : "  ";
                        int n3 = i == 0 ? color3.getRGB() : color4.getRGB();
                        this.drawStringWithShadowComponent.drawString(str4 + (String)list.get(i), this.count4 + 22, n, n3);
                    }

                    n += 12;
                }

                n += 4;
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(this.guiScreen);
        }

        super.keyTyped(typedChar, keyCode);
    }

    private List<String> computeList(String str, int n) {
        ArrayList list = new ArrayList();
        if (this.drawStringWithShadowComponent.getStringWidth(str) <= n) {
            list.add(str);
            return list;
        }
        String[] parts = str.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String str2 : parts) {
            String str3 = sb.length() == 0 ? str2 : sb + " " + str2;
            if (this.drawStringWithShadowComponent.getStringWidth(str3) > n) {
                if (sb.length() > 0) {
                    list.add(sb.toString());
                    sb = new StringBuilder(str2);
                } else {
                    list.add(str2);
                }
            } else {
                if (sb.length() > 0) {
                    sb.append(" ");
                }

                sb.append(str2);
            }
        }

        if (sb.length() > 0) {
            list.add(sb.toString());
        }

        return list;
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            mc.displayGuiScreen(this.guiScreen);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.color = OceanComponent.computeColor();

        try {
            MainMenuScene mainMenuScene = NebulaComponent.computeMainMenuScene2();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            mainMenuScene.handleInt(this.width, this.height, 0.0F, 0, 0);
            drawRect(0, 0, this.width, this.height, 1207959552);
            GlStateManager.disableBlend();
        } catch (Exception e) {
            this.drawGradientRect(0, 0, this.width, this.height, -16121342, -15596540);
        }

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        MainMenuIconRenderer.handleInt(this.count4, this.count5, this.count6, this.count7, 12.0F, 1.12F);
        GlStateManager.enableTexture2D();
        String str = this.entry.releaseMode.displayName.toUpperCase();
        int n = this.drawStringWithShadowComponent.getStringWidth(str) + 10;
        drawRect(
            this.count4 + 14,
            this.count5 + 10,
            this.count4 + 14 + n,
            this.count5 + 26,
            (1090519039 | ~this.entry.releaseMode.color) - ~this.entry.releaseMode.color
        );
        drawRect(this.count4 + 14, this.count5 + 10, this.count4 + 14 + n, this.count5 + 11, this.entry.releaseMode.color);
        this.drawStringWithShadowComponent.drawString(str, this.count4 + 18, this.count5 + 15, this.entry.releaseMode.color);
        String str2 = "v" + this.entry.text;
        this.drawStringWithShadowComponent.drawString(str2, this.count4 + 14 + n + 10, this.count5 + 15, color2.getRGB());
        CustomFontRenderer drawStringWithShadowComponent = this.drawStringWithShadowComponent3 != null
            ? this.drawStringWithShadowComponent3
            : this.drawStringWithShadowComponent2;
        int textWidth = drawStringWithShadowComponent.getStringWidth(this.entry.text3);
        drawStringWithShadowComponent.drawStringWithShadow(this.entry.text3, this.count4 + this.count6 / 2 - textWidth / 2, this.count5 + 26, this.color.getRGB());
        String str3 = this.entry.text2 + "  |  " + this.entry.computeInt() + " Changes";
        int textWidth2 = this.drawStringWithShadowComponent.getStringWidth(str3);
        this.drawStringWithShadowComponent.drawString(str3, this.count4 + this.count6 / 2 - textWidth2 / 2, this.count5 + 56, color4.getRGB());
        drawRect(this.count4 + 10, this.count5 + 70, this.count4 + this.count6 - 10, this.count5 + 71, new Color(80, 126, 160, 150).getRGB());
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        ScaledResolution sr = new ScaledResolution(mc);
        int scaleFactor = sr.getScaleFactor();
        GL11.glScissor(this.count4 * scaleFactor, (this.height - this.count8 - this.count9) * scaleFactor, this.count6 * scaleFactor, this.count9 * scaleFactor);
        this.process2();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        if (this.count2 > 0) {
            this.process3();
        }

        GlStateManager.disableBlend();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public RemadeScreen(GuiScreen guiScreen, ChangeLog.Entry entry) {
        this.guiScreen = guiScreen;
        this.entry = entry;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
        this.drawStringWithShadowComponent2 = FontComponent.computeDrawStringWithShadowComponent2();
    }

    @Override
    public void initGui() {
        int n = Math.max(30, this.width / 10);
        int n2 = Math.max(20, this.height / 12);
        this.count4 = n;
        this.count5 = n2;
        this.count6 = this.width - n * 2;
        this.count7 = this.height - n2 * 2;
        this.drawStringWithShadowComponent3 = FontComponent.computeDrawStringWithShadowComponent2(20, true);
        this.count8 = this.count5 + 76;
        this.count9 = this.count5 + this.count7 - this.count8 - 50;
        this.count = 0;
        this.process();
        byte b = 120;
        byte b2 = 22;
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.count4 + 10, this.count5 + this.count7 - b2 - 10, b, b2, "< Back"));
    }

    private void process() {
        this.count3 = 0;
        String str = "";
        boolean flag = this.entry.entries.stream().anyMatch(ChangeLog::matchesString);

        for (String str2 : this.entry.entries) {
            boolean flag2 = ChangeLog.matchesString(str2);
            String str3 = flag2 ? str2 : (flag ? str : this.computeString(str2));
            if (!str3.equals(str)) {
                str = str3;
                this.count3 += 16;
            }

            if (!flag2) {
                List list = this.computeList(str2, this.count6 - 70);
                this.count3 = this.count3 + list.size() * 12 + 4;
            }
        }

        this.count2 = Math.max(0, this.count3 - this.count9 + 20);
    }

    private int computeInt(String str) {
        switch (str) {
            case "REMADE":
                return new Color(255, 200, 50).getRGB();
            case "ADDED":
                return new Color(100, 220, 100).getRGB();
            case "IMPROVED":
            case "IMPROVEMENTS":
                return new Color(100, 180, 255).getRGB();
            case "FIXED":
            case "FIXES":
                return new Color(180, 130, 255).getRGB();
            case "REMOVED":
                return new Color(255, 100, 100).getRGB();
            case "UNPATCHED":
                return new Color(255, 180, 50).getRGB();
            default:
                return color4.getRGB();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dWheel = Mouse.getDWheel();
        if (dWheel != 0) {
            this.count = dWheel > 0 ? Math.max(0, this.count - 25) : Math.min(this.count2, this.count + 25);
        }
    }
}
