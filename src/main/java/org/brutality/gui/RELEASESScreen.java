package org.brutality.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.Brutality;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.gui.mainmenu.base.MainMenuScene;
import org.brutality.gui.mainmenu.effects.MainMenuIconRenderer;
import org.brutality.gui.mainmenu.effects.NebulaComponent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class RELEASESScreen extends CustomGuiScreen {
    private static final Color color = new Color(10, 2, 2);
    private static final Color color2 = new Color(18, 4, 4);
    private static final Color color3 = new Color(15, 24, 38, 210);
    private static final Color color4 = new Color(22, 35, 51, 200);
    private static final Color color5 = new Color(31, 48, 65, 220);
    private Color color6 = OceanComponent.computeColor();
    private static final Color color7 = new Color(238, 244, 250);
    private static final Color color8 = new Color(167, 184, 202);
    private static final Color color9 = new Color(111, 134, 157);
    private static final Color color10 = new Color(80, 126, 160, 150);
    private static final int count = -1;
    private static final int count2 = -11751600;
    private static final int count3 = -14575885;
    private static final int count4 = -6543440;
    private static final int count5 = -26624;
    private static final int count6 = -769226;
    private final GuiScreen guiScreen;
    private final CustomFontRenderer drawStringWithShadowComponent;
    private ChangeLog.ReleaseMode releaseMode = null;
    private final String[] stringArray = new String[]{"ALL", "RELEASES", "UPDATES", "HOTFIXES", "BETAS", "ALPHAS"};
    private final ChangeLog.ReleaseMode[] releaseModeArray = new ChangeLog.ReleaseMode[]{
        null,
        ChangeLog.ReleaseMode.RELEASE,
        ChangeLog.ReleaseMode.UPDATE,
        ChangeLog.ReleaseMode.HOTFIX,
        ChangeLog.ReleaseMode.BETA,
        ChangeLog.ReleaseMode.ALPHA
    };
    private final int[] intValueArray = new int[]{-1, -11751600, -14575885, -6543440, -26624, -769226};
    private int count7 = 0;
    private int count8 = 0;
    private int count9 = 0;
    private int count10;
    private int count11;
    private int count12;
    private int count13;
    private int count14;
    private int count15;
    private int count16;
    private int count17;
    private int count18;
    private int count19;
    private int count20;
    private int count21;
    private List<ChangeLog.Entry> entries = new ArrayList<>();
    private static final int count22 = 40;

    public RELEASESScreen(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            if (mouseX >= this.count18 && mouseX <= this.count18 + this.count20 && mouseY >= this.count19 && mouseY <= this.count19 + this.count21) {
                mc.displayGuiScreen(this.guiScreen);
            } else {
                int len = this.stringArray.length;
                int n = this.count12 - 20;
                int n2 = n / len;
                int n3 = this.count10 + 10;

                for (int i = 0; i < len; i++) {
                    int n4 = n3 + i * n2;
                    if (mouseX >= n4 && mouseX < n4 + n2 && mouseY >= this.count14 && mouseY < this.count14 + this.count15) {
                        this.releaseMode = this.releaseModeArray[i];
                        this.process2();
                        return;
                    }
                }

                if (mouseX >= this.count10 + 8 && mouseX <= this.count10 + this.count12 - 8 && mouseY >= this.count16 && mouseY <= this.count16 + this.count17) {
                    int n5 = this.count16 - this.count7;

                    for (int j = 0; j < this.entries.size(); j++) {
                        if (mouseY >= Math.max(n5, this.count16) && mouseY < Math.min(n5 + 40, this.count16 + this.count17)) {
                            mc.displayGuiScreen(new RemadeScreen(this, this.entries.get(j)));
                            return;
                        }

                        n5 += 46;
                    }
                }
            }
        }
    }

    private void process() {
        int n = this.count10 + this.count12 - 6;
        int n2 = this.count16;
        int n3 = this.count17;
        drawRect(n, n2, n + 4, n2 + n3, new Color(30, 10, 10, 100).getRGB());
        float f = (float)this.count17 / (this.count9 + 10);
        int n4 = Math.max(20, (int)(n3 * f));
        int n5 = n2 + (this.count8 > 0 ? (int)((float)this.count7 / this.count8 * (n3 - n4)) : 0);
        drawRect(n, n5, n + 4, n5 + n4, new Color(this.color6.getRed(), this.color6.getGreen(), this.color6.getBlue(), 150).getRGB());
    }

    private void handleInt(int n, int n2) {
        int n3 = this.count16 - this.count7;

        for (int i = 0; i < this.entries.size(); i++) {
            if (n3 + 40 >= this.count16 && n3 <= this.count16 + this.count17) {
                ChangeLog.Entry entry = this.entries.get(i);
                boolean flag = n >= this.count10 + 8
                    && n <= this.count10 + this.count12 - 8
                    && n2 >= Math.max(n3, this.count16)
                    && n2 <= Math.min(n3 + 40, this.count16 + this.count17);
                this.handleEntry(entry, this.count10 + 8, n3, this.count12 - 16, 40, flag);
                n3 += 46;
            } else {
                n3 += 46;
            }
        }
    }

    private void process2() {
        this.entries = this.releaseMode == null ? ChangeLog.computeList() : ChangeLog.computeList(this.releaseMode);
        this.count7 = 0;
        this.process4();
    }

    @Override
    public void initGui() {
        int n = Math.max(20, this.width / 12);
        int n2 = Math.max(15, this.height / 15);
        this.count10 = n;
        this.count11 = n2;
        this.count12 = this.width - n * 2;
        this.count13 = this.height - n2 * 2;
        this.count14 = this.count11 + 38;
        this.count15 = 22;
        this.count16 = this.count14 + this.count15 + 8;
        this.count17 = this.count11 + this.count13 - this.count16 - 35;
        this.count20 = 100;
        this.count21 = 20;
        this.count18 = this.width / 2 - this.count20 / 2;
        this.count19 = this.count11 + this.count13 - 28;
        this.count7 = 0;
        this.buttonList.clear();
        this.process2();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(this.guiScreen);
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.color6 = OceanComponent.computeColor();
        this.process3();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        MainMenuIconRenderer.handleInt(this.count10, this.count11, this.count12, this.count13, 12.0F, 1.1F);
        GlStateManager.enableTexture2D();
        String str = "CHANGELOG";
        int textWidth = this.drawStringWithShadowComponent.getStringWidth(str);
        this.drawStringWithShadowComponent.drawStringWithShadow(str, this.count10 + this.count12 / 2 - textWidth / 2, this.count11 + 8, this.color6.getRGB());
        String version = Brutality.version;
        int textWidth2 = this.drawStringWithShadowComponent.getStringWidth(version);
        this.drawStringWithShadowComponent.drawStringWithShadow(version, this.count10 + this.count12 / 2 - textWidth2 / 2, this.count11 + 20, -26624);
        String str2 = ChangeLog.computeString();
        this.drawStringWithShadowComponent
            .drawStringWithShadow(
                str2, this.count10 + this.count12 - this.drawStringWithShadowComponent.getStringWidth(str2) - 10, this.count11 + 10, color8.getRGB()
            );
        String str3 = this.entries.size() + " entries";
        this.drawStringWithShadowComponent.drawStringWithShadow(str3, this.count10 + 10, this.count11 + 10, color9.getRGB());
        this.handleInt2(mouseX, mouseY);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        ScaledResolution sr = new ScaledResolution(mc);
        int scaleFactor = sr.getScaleFactor();
        GL11.glScissor(this.count10 * scaleFactor, (this.height - this.count16 - this.count17) * scaleFactor, this.count12 * scaleFactor, this.count17 * scaleFactor);
        this.handleInt(mouseX, mouseY);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        if (this.count8 > 0) {
            this.process();
        }

        boolean flag = mouseX >= this.count18 && mouseX <= this.count18 + this.count20 && mouseY >= this.count19 && mouseY <= this.count19 + this.count21;
        int n = flag ? new Color(60, 20, 10, 200).getRGB() : new Color(35, 10, 8, 180).getRGB();
        drawRect(this.count18, this.count19, this.count18 + this.count20, this.count19 + this.count21, n);
        drawRect(this.count18, this.count19, this.count18 + this.count20, this.count19 + 1, flag ? this.color6.getRGB() : color10.getRGB());
        drawRect(
            this.count18,
            this.count19 + this.count21 - 1,
            this.count18 + this.count20,
            this.count19 + this.count21,
            flag ? this.color6.getRGB() : color10.getRGB()
        );
        String str4 = "< Back";
        int textWidth3 = this.drawStringWithShadowComponent.getStringWidth(str4);
        int n2 = flag ? this.color6.getRGB() : color7.getRGB();
        this.drawStringWithShadowComponent
            .drawStringWithShadow(
                str4, this.count18 + this.count20 / 2 - textWidth3 / 2, this.count19 + this.count21 / 2 - this.drawStringWithShadowComponent.FONT_HEIGHT / 2, n2
            );
        GlStateManager.disableBlend();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void process3() {
        try {
            MainMenuScene mainMenuScene = NebulaComponent.computeMainMenuScene2();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            mainMenuScene.handleInt(this.width, this.height, 0.0F, 0, 0);
            drawRect(0, 0, this.width, this.height, 1207959552);
            GlStateManager.disableBlend();
        } catch (Exception e) {
            this.drawGradientRect(0, 0, this.width, this.height, color.getRGB(), color2.getRGB());
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
    }

    private void process4() {
        this.count9 = this.entries.size() * 46;
        this.count8 = Math.max(0, this.count9 - this.count17 + 10);
    }

    private void handleInt2(int n, int n2) {
        int len = this.stringArray.length;
        int n3 = this.count12 - 20;
        int n4 = n3 / len;
        int n5 = this.count10 + 10;

        for (int i = 0; i < len; i++) {
            int n6 = n5 + i * n4;
            boolean flag = this.releaseMode == this.releaseModeArray[i];
            boolean flag2 = n >= n6 && n < n6 + n4 && n2 >= this.count14 && n2 < this.count14 + this.count15;
            Color color;
            if (flag) {
                color = new Color(
                    (0xFF | ~(this.intValueArray[i] >> 16)) - ~(this.intValueArray[i] >> 16),
                    this.intValueArray[i] >> 8 & 0xFF,
                    this.intValueArray[i] & 0xFF,
                    60
                );
            } else if (flag2) {
                color = new Color(50, 20, 15, 120);
            } else {
                color = new Color(25, 8, 8, 100);
            }

            drawRect(n6, this.count14, n6 + n4 - 2, this.count14 + this.count15, color.getRGB());
            if (flag) {
                drawRect(n6, this.count14 + this.count15 - 2, n6 + n4 - 2, this.count14 + this.count15, this.intValueArray[i]);
            }

            int n7 = flag ? this.intValueArray[i] : (flag2 ? color7.getRGB() : color8.getRGB());
            String str = this.stringArray[i];
            int textWidth = this.drawStringWithShadowComponent.getStringWidth(str);
            this.drawStringWithShadowComponent.drawStringWithShadow(str, n6 + (n4 - 2) / 2 - textWidth / 2, this.count14 + (this.count15 - 8) / 2, n7);
        }
    }

    private void handleEntry(ChangeLog.Entry entry, int n, int n2, int n3, int n4, boolean flag) {
        Color color = flag ? color5 : color4;
        drawRect(n, n2, n + n3, n2 + n4, color.getRGB());
        int n5 = flag ? new Color(255, 130, 82, 175).getRGB() : new Color(255, 255, 255, 20).getRGB();
        drawRect(n + 3, n2, n + n3, n2 + 1, n5);
        drawRect(n + n3 - 1, n2, n + n3, n2 + n4, n5);
        drawRect(n + 3, n2 + n4 - 1, n + n3, n2 + n4, n5);
        int color2 = entry.releaseMode.color;
        drawRect(n, n2, n + 3, n2 + n4, color2);
        String str = entry.releaseMode.displayName.toUpperCase();
        int n6 = this.drawStringWithShadowComponent.getStringWidth(str) + 8;
        int n7 = n + 10;
        int n8 = n2 + 6;
        drawRect(n7, n8, n7 + n6, n8 + 12, color2 & 1090519039);
        drawRect(n7, n8, n7 + n6, n8 + 1, color2);
        this.drawStringWithShadowComponent.drawStringWithShadow(str, n7 + 4, n8 + 2, color2);
        String str2 = "v" + entry.text;
        this.drawStringWithShadowComponent.drawStringWithShadow(str2, n7 + n6 + 8, n8 + 2, color7.getRGB());
        this.drawStringWithShadowComponent
            .drawStringWithShadow(entry.text2, n + n3 - this.drawStringWithShadowComponent.getStringWidth(entry.text2) - 24, n8 + 2, color9.getRGB());
        String str3 = flag ? ">" : "»";
        this.drawStringWithShadowComponent
            .drawStringWithShadow(
                str3,
                n + n3 - this.drawStringWithShadowComponent.getStringWidth(str3) - 8,
                n2 + n4 / 2 - this.drawStringWithShadowComponent.FONT_HEIGHT / 2,
                this.color6.getRGB()
            );
        String str4 = entry.computeInt() + " Changes - " + entry.text3;
        int n9 = n3 - this.drawStringWithShadowComponent.getStringWidth(str3) - 30;
        if (this.drawStringWithShadowComponent.getStringWidth(str4) > n9) {
            while (this.drawStringWithShadowComponent.getStringWidth(str4 + "...") > n9 && str4.length() > 3) {
                str4 = str4.substring(0, str4.length() - 1);
            }

            str4 = str4 + "...";
        }

        this.drawStringWithShadowComponent.drawStringWithShadow(str4, n + 10, n2 + 24, color8.getRGB());
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dWheel = Mouse.getDWheel();
        if (dWheel != 0) {
            this.count7 = dWheel > 0 ? Math.max(0, this.count7 - 30) : Math.min(this.count8, this.count7 + 30);
        }
    }
}
