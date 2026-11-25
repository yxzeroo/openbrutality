package org.brutality.alt.altmanager.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.brutality.alt.altmanager.shop.GuiLocaltsShop;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.gui.mainmenu.base.MainMenuScene;
import org.brutality.gui.mainmenu.effects.NebulaComponent;
import org.lwjgl.opengl.GL11;

public class GuiAccountSelector extends CustomGuiScreen {
    private static final int count = -1;
    private static final int count2 = -3355444;
    private static final int count3 = -7829368;
    private final GuiScreen guiScreen;
    private GuiAltButton guiAltButton;
    private GuiAltButton guiAltButton2;
    private GuiAltButton guiAltButton3;
    private final CustomFontRenderer drawStringWithShadowComponent;
    private List<GuiAccountSelector.Entry> entries = new ArrayList<>();
    private Random random = new Random();

    private void updateFireParticles() {
        this.entries.removeIf(entry -> {
            entry.update();
            return entry.checkState();
        });
        if (System.currentTimeMillis() % 100L < 50L && this.entries.size() < 50) {
            this.entries.add(new GuiAccountSelector.Entry(this.random.nextFloat() * this.width, this.height + this.random.nextFloat() * 50.0F, this.random));
        }
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        int n = 200;
        byte b = 30;
        byte b2 = 20;
        int n2 = b * 4 + b2 * 3;
        int n3 = (this.height - n2) / 2;
        int[] ints = this.computeIntArray();
        int rgb = new Color(ints[0], ints[1], ints[2]).getRGB();
        int rgb2 = new Color(Math.min(255, ints[0] + 40), Math.min(255, ints[1] + 40), Math.min(255, ints[2] + 40)).getRGB();
        this.buttonList.add(this.guiAltButton = new GuiAltButton(0, this.width / 2 - n / 2, n3, n, b, "MFAS - Full Access", rgb, rgb2));
        this.buttonList
            .add(
                this.guiAltButton2 = new GuiAltButton(
                    1,
                    this.width / 2 - n / 2,
                    n3 + b + b2,
                    n,
                    b,
                    "NFAS - Non Full Access",
                    new Color(200, 50, 20).getRGB(),
                    new Color(220, 80, 40).getRGB()
                )
            );
        this.buttonList.add(new GuiAltButton(3, this.width / 2 - n / 2, n3 + (b + b2) * 2, n, b, "Localts Shop", rgb, rgb2));
        this.buttonList
            .add(
                this.guiAltButton3 = new GuiAltButton(
                    2, this.width / 2 - n / 2, n3 + (b + b2) * 3, n, b, "Back to Main Menu", -7829368, -3355444
                )
            );
        this.process();
    }

    @Override
    public void drawGradientRect(int n, int n2, int n3, int n4, int n5, int n6) {
        float f = (n5 >> 24 & 0xFF) / 255.0F;
        float f2 = (n5 >> 16 & 0xFF) / 255.0F;
        float f3 = (n5 >> 8 & 0xFF) / 255.0F;
        float f4 = (n5 & 0xFF) / 255.0F;
        float f5 = ((0xFF | ~(n6 >> 24)) - ~(n6 >> 24)) / 255.0F;
        float f6 = (n6 >> 16 & 0xFF) / 255.0F;
        float f7 = (n6 >> 8 & 0xFF) / 255.0F;
        float f8 = (n6 & 0xFF) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(n3, n2, 0.0).func_181666_a(f2, f3, f4, f).func_181675_d();
        worldRenderer.func_181662_b(n, n2, 0.0).func_181666_a(f2, f3, f4, f).func_181675_d();
        worldRenderer.func_181662_b(n, n4, 0.0).func_181666_a(f6, f7, f8, f5).func_181675_d();
        worldRenderer.func_181662_b(n3, n4, 0.0).func_181666_a(f6, f7, f8, f5).func_181675_d();
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    private void process2() {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();

        for (GuiAccountSelector.Entry entry : this.entries) {
            entry.render();
        }

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int[] ints = this.computeIntArray();
        Color color = new Color(ints[0], ints[1], ints[2]);

        try {
            MainMenuScene mainMenuScene = NebulaComponent.computeMainMenuScene2();
            if (mainMenuScene != null) {
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                mainMenuScene.handleInt(this.width, this.height, 0.0F, mouseX, mouseY);
                GlStateManager.disableBlend();
            }
        } catch (Exception ignored) {
        }

        drawRect(0, 0, this.width, this.height, new Color(0, 0, 0, 130).getRGB());
        drawRect(0, 0, this.width, 1, new Color(ints[0], ints[1], ints[2], 220).getRGB());
        String str = "Account Manager";
        int n = this.width / 2 - this.drawStringWithShadowComponent.getStringWidth(str) / 2;
        this.drawStringWithShadowComponent.drawStringWithShadow(str, n, 30.0F, color.brighter().getRGB());
        String str2 = "Choose your account management type";
        int n2 = this.width / 2 - this.drawStringWithShadowComponent.getStringWidth(str2) / 2;
        this.drawStringWithShadowComponent.drawStringWithShadow(str2, n2, 55.0F, -3355444);

        for (GuiButton button : this.buttonList) {
            button.drawButton(mc, mouseX, mouseY);
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(new GuiAltManager(this));
                break;
            case 1:
                mc.displayGuiScreen(new GuiNfasAltManager(this));
                break;
            case 2:
                mc.displayGuiScreen(this.guiScreen);
                break;
            case 3:
                mc.displayGuiScreen(new GuiLocaltsShop(this));
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public GuiAccountSelector(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
        this.process();
    }

    private int[] computeIntArray() {
        int n = NebulaComponent.computeInt(NebulaComponent.computeMainMenuScene2());
        if (n < 0) {
            n = 0;
        }

        int[][] intsArray = new int[][]{
            {255, 100, 50},
            {160, 180, 255},
            {50, 200, 230},
            {220, 180, 100},
            {80, 230, 150},
            {255, 50, 200},
            {120, 200, 255},
            {225, 110, 60},
            {245, 140, 70},
            {255, 200, 70},
            {160, 60, 255}
        };
        if (n >= intsArray.length) {
            n = 0;
        }

        return intsArray[n];
    }

    private void process() {
        this.entries.clear();

        for (int i = 0; i < 30; i++) {
            this.entries.add(new GuiAccountSelector.Entry(this.random.nextFloat() * this.width, this.height + this.random.nextFloat() * 100.0F, this.random));
        }
    }

    public class Entry {
        private float x;
        private float y;
        private float factor;
        private float factor2;
        private float factor3;
        private float factor4;
        private int color;
        private float factor5;

        public void render() {
            if (this.factor3 > 0.0F) {
                Gui.drawRect(
                    (int)(this.x - this.factor5 / 2.0F),
                    (int)(this.y - this.factor5 / 2.0F),
                    (int)(this.x + this.factor5 / 2.0F),
                    (int)(this.y + this.factor5 / 2.0F),
                    this.color
                );
            }
        }

        public boolean checkState() {
            return this.factor3 <= 0.0F || this.y < -10.0F;
        }

        public Entry(float f, float f2, Random random) {
            this.x = f;
            this.y = f2;
            this.factor = (random.nextFloat() - 0.5F) * 2.0F;
            this.factor2 = -random.nextFloat() * 3.0F - 1.0F;
            this.factor4 = this.factor3 = 50.0F + random.nextFloat() * 100.0F;
            this.factor5 = 1.0F + random.nextFloat() * 3.0F;
            float f3 = this.factor3 / this.factor4;
            if (f3 > 0.7F) {
                this.color = -256;
            } else if (f3 > 0.4F) {
                this.color = -32768;
            } else {
                this.color = -65536;
            }
        }

        public void update() {
            this.x = this.x + this.factor;
            this.y = this.y + this.factor2;
            this.factor3--;
            this.factor = (float)(this.factor + (Math.random() - 0.5) * 0.1);
            this.factor2 = (float)(this.factor2 + (Math.random() - 0.5) * 0.1);
            float f = this.factor3 / this.factor4;
            if (f > 0.7F) {
                this.color = -256;
            } else if (f > 0.4F) {
                this.color = -32768;
            } else {
                this.color = -65536;
            }

            int n = (int)(255.0F * f);
            this.color = n << 24 | this.color & 16777215;
        }
    }
}
