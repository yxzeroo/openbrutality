package org.brutality.gui.mainmenu.selector;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.apache.logging.log4j.LogManager;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.gui.mainmenu.base.MainMenuScene;
import org.brutality.gui.mainmenu.effects.EffectsComponent;
import org.brutality.gui.mainmenu.effects.NebulaComponent;
import org.brutality.utils.render.OutlineThicknessHelper;
import org.brutality.utils.render.GlStateGuard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class OpenScreen extends CustomGuiScreen {
    private float factor = 0.0F;
    private long timestamp = 0L;
    private boolean flag = false;
    private boolean flag2;
    private static final long timestamp2 = 280L;
    private static final Map<String, String[]> lookupMap = new LinkedHashMap<>();
    private String text = "All";
    private float[] floatValueArray = new float[0];
    private float factor2 = 0.0F;
    private int count = -1;
    private float factor3 = 0.0F;
    private float factor4 = 0.0F;
    private float[] floatValueArray2 = new float[lookupMap.size()];
    private int scaledWidth;
    private int scaledHeight;
    private CustomFontRenderer drawStringWithShadowComponent;
    private CustomFontRenderer drawStringWithShadowComponent2;
    private CustomFontRenderer drawStringWithShadowComponent3;
    private static final Color color = new Color(4, 5, 9, 245);
    private static final Color color2 = new Color(18, 19, 25, 255);
    private static final Color color3 = new Color(13, 14, 19, 255);
    private static final Color color4 = new Color(255, 255, 255, 18);
    private static final Color color5 = new Color(240, 242, 250);
    private static final Color color6 = new Color(160, 168, 188);
    private static final Color color7 = new Color(110, 116, 138);
    private static final Color color8 = new Color(140, 185, 255);
    private static final Color color9 = new Color(255, 255, 255);

    public float getFactor() {
        return this.factor;
    }

    @Override
    public void initGui() {
        super.initGui();
        ScaledResolution sr = new ScaledResolution(mc);
        this.scaledWidth = sr.getScaledWidth();
        this.scaledHeight = sr.getScaledHeight();

        for (MainMenuScene mainMenuScene : NebulaComponent.getEntries()) {
            mainMenuScene.handleInt(this.scaledWidth, this.scaledHeight);
        }

        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(32, true);
        this.drawStringWithShadowComponent2 = FontComponent.computeDrawStringWithShadowComponent();
        this.drawStringWithShadowComponent3 = FontComponent.computeDrawStringWithShadowComponent3();
        this.process2();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int eventDWheel = Mouse.getEventDWheel();
        if (eventDWheel != 0 && this.factor4 > 0.0F) {
            this.factor3 += eventDWheel > 0 ? 24 : -24;
            this.factor3 = this.clamp(this.factor3, -this.factor4, 0.0F);
        }
    }

    public boolean checkState2() {
        return this.flag && this.factor >= 0.99F;
    }

    public void open() {
        this.flag = true;
        this.timestamp = System.currentTimeMillis();
    }

    public void close() {
        this.flag = false;
        this.timestamp = System.currentTimeMillis();
    }

    private float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, f));
    }

    static {
        lookupMap.put("All", new String[0]);
        lookupMap.put("Planets", new String[]{"Earth", "Mars", "Solar System"});
        lookupMap.put("Space", new String[]{"Solar System", "Aurora", "Black Hole", "Nebula"});
        lookupMap.put("Cities", new String[]{"London", "New York City", "Tokyo", "Rain"});
        lookupMap.put("Nature", new String[]{"Fire", "Ocean", "Alpine", "Sunset", "Sakura"});
        lookupMap.put("Minimal", new String[]{"Normal", "Prism"});
        lookupMap.put("Seasonal", new String[]{"Halloween"});
        lookupMap.put("Neon", new String[]{"Cyberpunk", "Prism"});
    }

    private void handleInt(int n, int n2, int n3, int n4, int n5) {
        if (n > n3) {
            int n6 = n;
            n = n3;
            n3 = n6;
        }

        if (n2 > n4) {
            int n7 = n2;
            n2 = n4;
            n4 = n7;
        }

        float f = (n5 >> 24 & 0xFF) / 255.0F;
        float f2 = (n5 >> 16 & 0xFF) / 255.0F;
        float f3 = (n5 >> 8 & 0xFF) / 255.0F;
        float f4 = ((0xFF | ~n5) - ~n5) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(n, n4);
        GL11.glVertex2f(n3, n4);
        GL11.glVertex2f(n3, n2);
        GL11.glVertex2f(n, n2);
        GL11.glEnd();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                EffectsComponent.handleInt(mc.displayWidth, mc.displayHeight);
                this.process();
                if (!(this.factor <= 0.005F)) {
                    ScaledResolution sr = new ScaledResolution(mc);
                    this.scaledWidth = sr.getScaledWidth();
                    this.scaledHeight = sr.getScaledHeight();
                    float f = this.computeFloat(this.factor);
                    int n = (int)(color.getAlpha() * f);
                    this.handleInt(0, 0, this.scaledWidth, this.scaledHeight, new Color(color.getRed(), color.getGreen(), color.getBlue(), n).getRGB());
                    int n2 = Math.max(20, this.scaledWidth / 22);
                    int n3 = n2;
                    int n4 = this.scaledWidth - n2 * 2;
                    int n5 = this.scaledHeight - n2 * 2;
                    int n6 = Math.max(120, n4 / 7);
                    int n7 = n2 + n6;
                    int n8 = n4 - n6;
                    float f2 = this.computeFloat(this.factor);
                    int n9 = (int)((1.0F - f2) * 30.0F);
                    int n10 = n2 + n9;
                    this.handleInt(n2, n10, n2 + n4, n10 + n5, this.computeColor(color2, f).getRGB());
                    this.handleInt(n2, n10, n2 + n6, n10 + n5, this.computeColor(color3, f).getRGB());
                    this.handleInt(n2 + n6, n10, n2 + n6 + 1, n10 + n5, this.computeColor(color4, f).getRGB());
                    this.handleInt(
                        n2, n10, n2 + n4, n10 + 2, new Color(color8.getRed(), color8.getGreen(), color8.getBlue(), (int)(190.0F * f)).getRGB()
                    );
                    int n11 = n2 + 14;
                    int n12 = n10 + 14;
                    this.drawStringWithShadowComponent.drawString("THEMES", n11, n12, this.computeColor(color5, f).getRGB());
                    this.drawStringWithShadowComponent3
                        .drawString(
                            "Pick your style", n11, n12 + this.drawStringWithShadowComponent.FONT_HEIGHT + 2, this.computeColor(color7, f).getRGB()
                        );
                    int n13 = n12 + this.drawStringWithShadowComponent.FONT_HEIGHT + 24;
                    byte b = 24;
                    byte b2 = 2;
                    int n14 = 0;

                    for (String str : lookupMap.keySet()) {
                        int n15 = n13 + n14 * (b + b2);
                        boolean flag = str.equals(this.text);
                        boolean flag2 = mouseX >= n3 + 6 && mouseX <= n3 + n6 - 6 && mouseY >= n15 && mouseY <= n15 + b;
                        float f3 = !flag && !flag2 ? 0.0F : 1.0F;
                        this.floatValueArray2[n14] = this.floatValueArray2[n14] + (f3 - this.floatValueArray2[n14]) * 0.18F;
                        if (flag) {
                            this.handleInt(
                                n3 + 6,
                                n15,
                                n3 + n6 - 6,
                                n15 + b,
                                new Color(color8.getRed(), color8.getGreen(), color8.getBlue(), (int)(28.0F * f)).getRGB()
                            );
                            this.handleInt(n3 + 6, n15, n3 + 8, n15 + b, this.computeColor(color8, f).getRGB());
                        } else if (this.floatValueArray2[n14] > 0.01F) {
                            this.handleInt(
                                n3 + 6,
                                n15,
                                n3 + n6 - 6,
                                n15 + b,
                                new Color(255, 255, 255, (int)(12.0F * this.floatValueArray2[n14] * f)).getRGB()
                            );
                        }

                        Color color10 = flag ? color5 : (flag2 ? color5 : color6);
                        this.drawStringWithShadowComponent2
                            .drawString(
                                str,
                                n3 + 14,
                                n15 + (b - this.drawStringWithShadowComponent2.FONT_HEIGHT) / 2,
                                this.computeColor(color10, f).getRGB()
                            );
                        int size = this.computeList(str).size();
                        String str2 = String.valueOf(size);
                        int textWidth = this.drawStringWithShadowComponent3.getStringWidth(str2);
                        this.drawStringWithShadowComponent3
                            .drawString(
                                str2,
                                n3 + n6 - 14 - textWidth,
                                n15 + (b - this.drawStringWithShadowComponent3.FONT_HEIGHT) / 2,
                                this.computeColor(flag ? color8 : color7, f).getRGB()
                            );
                        n14++;
                    }

                    String str3 = "ESC to close";
                    this.drawStringWithShadowComponent3
                        .drawString(
                            str3, n3 + 14, n10 + n5 - this.drawStringWithShadowComponent3.FONT_HEIGHT - 12, this.computeColor(color7, f).getRGB()
                        );
                    int n16 = n7 + 16;
                    int n17 = n10 + 14;
                    this.drawStringWithShadowComponent.drawString(this.text, n16, n17, this.computeColor(color5, f).getRGB());
                    var mainMenuScenes = this.computeList(this.text);
                    String str4 = mainMenuScenes.size() + (mainMenuScenes.size() == 1 ? " theme" : " themes");
                    this.drawStringWithShadowComponent3
                        .drawString(str4, n16, n17 + this.drawStringWithShadowComponent.FONT_HEIGHT + 2, this.computeColor(color7, f).getRGB());
                    String str5 = "Active: " + (NebulaComponent.computeMainMenuScene2() != null ? NebulaComponent.computeMainMenuScene2().getName() : "—");
                    int textWidth2 = this.drawStringWithShadowComponent3.getStringWidth(str5);
                    this.drawStringWithShadowComponent3
                        .drawString(
                            str5,
                            n7 + n8 - 16 - textWidth2,
                            n17 + this.drawStringWithShadowComponent.FONT_HEIGHT - this.drawStringWithShadowComponent3.FONT_HEIGHT + 4,
                            this.computeColor(color8, f).getRGB()
                        );
                    this.handleInt(
                        n16,
                        n17 + this.drawStringWithShadowComponent.FONT_HEIGHT + this.drawStringWithShadowComponent3.FONT_HEIGHT + 12,
                        n7 + n8 - 16,
                        n17 + this.drawStringWithShadowComponent.FONT_HEIGHT + this.drawStringWithShadowComponent3.FONT_HEIGHT + 13,
                        this.computeColor(color4, f).getRGB()
                    );
                    this.process2();
                    int n18 = n17 + this.drawStringWithShadowComponent.FONT_HEIGHT + this.drawStringWithShadowComponent3.FONT_HEIGHT + 24;
                    int n19 = n10 + n5 - 16;
                    int n20 = n19 - n18;
                    int n21 = n8 - 32;
                    int n22 = n7 + 16;
                    String str6 = null;
                    int size2 = mainMenuScenes.size();
                    short s = 130;
                    byte b3 = 10;
                    int n23 = Math.max(1, (n21 + b3) / (s + b3));
                    int n24 = Math.max(1, (int)Math.ceil((double)size2 / n23));
                    int n25 = (n21 - b3 * (n23 - 1)) / n23;
                    int n26 = this.drawStringWithShadowComponent2.FONT_HEIGHT + this.drawStringWithShadowComponent2.FONT_HEIGHT + 14;
                    int n27 = n25 + n26;
                    float f4 = Math.max(0.0F, (float)(n24 * (n27 + b3) - n20));
                    this.factor4 = f4;
                    this.factor3 = this.clamp(this.factor3, -f4, 0.0F);
                    int scaleFactor = sr.getScaleFactor();
                    GL11.glEnable(GL11.GL_SCISSOR_TEST);
                    GL11.glScissor(n22 * scaleFactor, (this.scaledHeight - n19) * scaleFactor, n21 * scaleFactor, n20 * scaleFactor);
                    long now = System.currentTimeMillis();
                    if (this.factor2 > 0.0F) {
                        this.factor2 = Math.max(0.0F, this.factor2 - 0.04F);
                    }

                    for (int i = 0; i < size2; i++) {
                        MainMenuScene mainMenuScene = (MainMenuScene)mainMenuScenes.get(i);
                        int n28 = i % n23;
                        int n29 = i / n23;
                        int n30 = n22 + n28 * (n25 + b3);
                        int n31 = n18 + n29 * (n27 + b3) + (int)this.factor3;
                        long l = i * 45L;
                        long l2 = now - this.timestamp - l;
                        float f5 = this.flag ? Math.min(1.0F, Math.max(0.0F, (float)l2 / 320.0F)) : this.factor;
                        float f6 = this.computeFloat(f5);
                        int n32 = (int)((1.0F - f6) * 26.0F);
                        int n33 = n31 + n32;
                        if (n33 + n27 >= n18 && n33 <= n19) {
                            boolean flag3 = NebulaComponent.computeMainMenuScene2() == mainMenuScene;
                            boolean flag4 = mouseX >= n30 && mouseX <= n30 + n25 && mouseY >= n33 && mouseY <= n33 + n27;
                            if (i < this.floatValueArray.length) {
                                float f7 = flag4 ? 1.0F : 0.0F;
                                this.floatValueArray[i] = this.floatValueArray[i] + (f7 - this.floatValueArray[i]) * 0.18F;
                                if (Math.abs(this.floatValueArray[i] - f7) < 0.005F) {
                                    this.floatValueArray[i] = f7;
                                }
                            }

                            float f8 = i < this.floatValueArray.length ? this.floatValueArray[i] : 0.0F;
                            int n34 = (int)(f8 * 6.0F);
                            int n35 = n33 - n34;
                            float f9 = f6 * f;
                            if (f8 > 0.02F) {
                                int n36 = (int)(f8 * 28.0F * f);
                                this.handleInt(
                                    n30 - 1,
                                    n35 - 1,
                                    n30 + n25 + 1,
                                    n35 + n27 + 1,
                                    new Color(color8.getRed(), color8.getGreen(), color8.getBlue(), n36).getRGB()
                                );
                            }

                            this.handleInt(n30, n35, n30 + n25, n35 + n27, new Color(18, 20, 28, (int)(255.0F * f9)).getRGB());
                            int n37 = this.drawStringWithShadowComponent2.FONT_HEIGHT + this.drawStringWithShadowComponent2.FONT_HEIGHT + 14;
                            int n38 = n27 - n37;
                            int n39 = n30 + 1;
                            int n40 = n35 + 1;
                            int n41 = n25 - 2;
                            int n42 = n38 - 1;
                            int n43 = Math.max(n39, n22);
                            int n44 = Math.max(n40, n18);
                            int n45 = Math.min(n39 + n41, n22 + n21);
                            int n46 = Math.min(n40 + n42, n19);
                            int n47 = Math.max(0, n45 - n43);
                            int n48 = Math.max(0, n46 - n44);
                            if (n47 > 0 && n48 > 0) {
                                this.handleMainMenuScene(mainMenuScene, n39, n40, n41, n42, scaleFactor);
                            }

                            GL11.glEnable(GL11.GL_SCISSOR_TEST);
                            GL11.glScissor(n22 * scaleFactor, (this.scaledHeight - n19) * scaleFactor, n21 * scaleFactor, n20 * scaleFactor);
                            this.handleInt(n30, n35 + n38, n30 + n25, n35 + n27, new Color(12, 14, 20, (int)(255.0F * f9)).getRGB());
                            this.handleInt(n30, n35 + n38, n30 + n25, n35 + n38 + 1, new Color(255, 255, 255, (int)(20.0F * f9)).getRGB());
                            if (flag3) {
                                int n49 = (int)(220.0F * f);
                                Color color11 = this.computeColor(color8, f);
                                this.handleInt(n30, n35, n30 + n25, n35 + 1, color11.getRGB());
                                this.handleInt(n30, n35 + n27 - 1, n30 + n25, n35 + n27, color11.getRGB());
                                this.handleInt(n30, n35, n30 + 1, n35 + n27, color11.getRGB());
                                this.handleInt(n30 + n25 - 1, n35, n30 + n25, n35 + n27, color11.getRGB());
                                String str7 = "ACTIVE";
                                int n50 = this.drawStringWithShadowComponent3.getStringWidth(str7) + 10;
                                int n51 = this.drawStringWithShadowComponent3.FONT_HEIGHT + 5;
                                int n52 = n30 + n25 - n50 - 8;
                                int n53 = n35 + 8;
                                this.handleInt(
                                    n52,
                                    n53,
                                    n52 + n50,
                                    n53 + n51,
                                    new Color(color8.getRed(), color8.getGreen(), color8.getBlue(), (int)(200.0F * f)).getRGB()
                                );
                                this.drawStringWithShadowComponent3.drawString(str7, n52 + 5, n53 + 3, new Color(8, 12, 24, n49).getRGB());
                            } else {
                                int n54 = (int)(28.0F * f9);
                                int n55 = (int)(f8 * 72.0F * f);
                                int n56 = Math.min(255, n54 + n55);
                                Color color12 = new Color(
                                    (int)(color8.getRed() * (0.5F + 0.5F * f8)), (int)(color8.getGreen() * (0.5F + 0.5F * f8)), 255, n56
                                );
                                byte b4 = 1;
                                this.handleInt(n30, n35, n30 + n25, n35 + b4, color12.getRGB());
                                this.handleInt(n30, n35 + n27 - b4, n30 + n25, n35 + n27, color12.getRGB());
                                this.handleInt(n30, n35, n30 + b4, n35 + n27, color12.getRGB());
                                this.handleInt(n30 + n25 - b4, n35, n30 + n25, n35 + n27, color12.getRGB());
                            }

                            if (this.count == i && this.factor2 > 0.0F) {
                                int n57 = (int)(this.factor2 * 200.0F);
                                this.handleInt(
                                    n30 - 2,
                                    n35 - 2,
                                    n30 + n25 + 2,
                                    n35 + n27 + 2,
                                    new Color(color9.getRed(), color9.getGreen(), color9.getBlue(), n57).getRGB()
                                );
                            }

                            String name = mainMenuScene.getName();
                            String description = mainMenuScene.getDescription();
                            int n58 = n35 + n38 + 6;
                            this.drawStringWithShadowComponent2
                                .drawString(
                                    this.computeString(name, n25 - 16), n30 + 8, n58, this.computeColor(flag3 ? color8 : color5, f).getRGB()
                                );
                            if (description != null && !description.isEmpty()) {
                                String str8 = this.computeString(description, n25 - 16);
                                this.drawStringWithShadowComponent2
                                    .drawString(
                                        str8,
                                        n30 + 8,
                                        n58 + this.drawStringWithShadowComponent2.FONT_HEIGHT + 3,
                                        this.computeColor(color6, f).getRGB()
                                    );
                                if (!str8.equals(description)
                                    && mouseX >= n30
                                    && mouseX < n30 + n25
                                    && mouseY >= Math.max(n18, n35)
                                    && mouseY < Math.min(n19, n35 + n27)) {
                                    str6 = description;
                                }
                            }
                        }
                    }

                    GL11.glDisable(GL11.GL_SCISSOR_TEST);
                    if (f4 > 0.0F) {
                        int n59 = n22 + n21 + 4;
                        byte b5 = 3;
                        this.handleInt(n59, n18, n59 + b5, n18 + n20, new Color(255, 255, 255, (int)(14.0F * f)).getRGB());
                        float f10 = -this.factor3 / f4;
                        int n60 = Math.max(28, (int)(n20 / (n20 + f4) * n20));
                        int n61 = n18 + (int)(f10 * (n20 - n60));
                        this.handleInt(n59, n61, n59 + b5, n61 + n60, this.computeColor(color8, f).getRGB());
                    }

                    if (str6 != null && this.flag) {
                        this.handleString(str6, mouseX, mouseY, f);
                    }

                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.enableTexture2D();
                    return;
                }
            }

            return;
        } catch (Exception e) {
            if (!this.flag2) {
                this.flag2 = true;
                LogManager.getLogger(OpenScreen.class).warn("Theme selector rendering failed", (Throwable)e);
            }
        }
    }

    private void process2() {
        int size = this.computeList(this.text).size();
        if (this.floatValueArray.length != size) {
            this.floatValueArray = new float[size];
        }
    }

    public void r() {
        if (this.flag) {
            this.close();
        } else {
            this.open();
        }
    }

    private void handleMainMenuScene(MainMenuScene mainMenuScene, int n, int n2, int n3, int n4, int n5) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            try {
                mainMenuScene.handleInt(n, n2, n3, n4, 0.0F);
            } catch (Exception e) {
                int[] ints = this.computeIntArray(mainMenuScene.getName());
                this.handleInt(n, n2, n + n3, n2 + n4, new Color(ints[0], ints[1], ints[2], 255).getRGB());
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (keyCode == 1) {
            this.close();
        }
    }

    private void handleString(String str, int n, int n2, float f) {
        int n3 = Math.min(250, this.scaledWidth - 32);
        ArrayList<String> list = new ArrayList<>();
        String str2 = "";

        for (String str3 : str.split(" ")) {
            if (!str2.isEmpty() && this.drawStringWithShadowComponent3.getStringWidth(str2 + " " + str3) > n3) {
                list.add(str2);
                str2 = "";
            }

            for (int i = 0; i < str3.length(); i++) {
                String str4 = str2 + (i == 0 && !str2.isEmpty() ? " " : "") + str3.charAt(i);
                if (!str2.isEmpty() && this.drawStringWithShadowComponent3.getStringWidth(str4) > n3) {
                    list.add(str2);
                    str2 = "";
                    str4 = Character.toString(str3.charAt(i));
                }

                str2 = str4;
            }
        }

        if (!str2.isEmpty()) {
            list.add(str2);
        }

        int n4 = 0;

        for (String str5 : list) {
            n4 = Math.max(n4, this.drawStringWithShadowComponent3.getStringWidth(str5));
        }

        int n5 = n4 + 20;
        int n6 = list.size() * (this.drawStringWithShadowComponent3.FONT_HEIGHT + 4) + 16;
        int n7 = Math.max(8, Math.min(n + 12, this.scaledWidth - n5 - 8));
        int n8 = n2 + 14;
        if (n8 + n6 > this.scaledHeight - 8) {
            n8 = n2 - n6 - 10;
        }

        int n9 = Math.max(8, n8);
        OutlineThicknessHelper.handleFloat(n7, n9, n5, n6, 6.0F, this.computeColor(new Color(13, 15, 22, 252), f));
        OutlineThicknessHelper.handleFloat(n7, n9, n5, n6, 6.0F, 1.0F, new Color(0, 0, 0, 0), this.computeColor(color8, f));
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        for (String str6 : ((Iterable<String>)(Object)(list))) {
            this.drawStringWithShadowComponent3.drawString(str6, n7 + 10, n9 + 8, this.computeColor(color5, f).getRGB());
            n9 += this.drawStringWithShadowComponent3.FONT_HEIGHT + 4;
        }
    }

    private List<MainMenuScene> computeList(String str) {
        var mainMenuScenes = NebulaComponent.getEntries();
        if ("All".equals(str)) {
            return mainMenuScenes;
        }
        String[] parts = lookupMap.get(str);
        if (parts != null && parts.length != 0) {
            List list = new ArrayList();

            for (MainMenuScene mainMenuScene : mainMenuScenes) {
                for (String str2 : parts) {
                    if (str2.equalsIgnoreCase(mainMenuScene.getName())) {
                        list.add(mainMenuScene);
                        break;
                    }
                }
            }

            return list;
        }
        return new ArrayList<>();
    }

    private String computeString(String str, int n) {
        if (this.drawStringWithShadowComponent2.getStringWidth(str) <= n) {
            return str;
        }
        String str2 = "...";
        if (this.drawStringWithShadowComponent2.getStringWidth(str2) > n) {
            return "";
        }
        int len = str.length();

        while (len > 0 && this.drawStringWithShadowComponent2.getStringWidth(str.substring(0, len) + str2) > n) {
            len--;
        }

        return str.substring(0, len) + str2;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!(this.factor < 0.4F) && mouseButton == 0) {
            ScaledResolution sr = new ScaledResolution(mc);
            this.scaledWidth = sr.getScaledWidth();
            this.scaledHeight = sr.getScaledHeight();
            int n = Math.max(20, this.scaledWidth / 22);
            int n2 = n;
            int n3 = this.scaledWidth - n * 2;
            int n4 = this.scaledHeight - n * 2;
            int n5 = Math.max(120, n3 / 7);
            int n6 = n + n5;
            int n7 = n3 - n5;
            int n8 = n + 14 + this.drawStringWithShadowComponent.FONT_HEIGHT + 24;
            byte b = 24;
            byte b2 = 2;
            int n9 = 0;

            for (String str : lookupMap.keySet()) {
                int n10 = n8 + n9 * (b + b2);
                if (mouseX >= n2 + 6 && mouseX <= n2 + n5 - 6 && mouseY >= n10 && mouseY <= n10 + b) {
                    if (!str.equals(this.text)) {
                        this.text = str;
                        this.factor3 = 0.0F;
                        this.factor4 = 0.0F;
                        this.count = -1;
                        this.factor2 = 0.0F;
                        this.process2();
                    }

                    return;
                }

                n9++;
            }

            var mainMenuScenes = this.computeList(this.text);
            int n11 = n + 14 + this.drawStringWithShadowComponent.FONT_HEIGHT + this.drawStringWithShadowComponent3.FONT_HEIGHT + 24;
            int n12 = n + n4 - 16;
            int n13 = n12 - n11;
            int n14 = n7 - 32;
            int n15 = n6 + 16;
            int size = mainMenuScenes.size();
            short s = 130;
            byte b3 = 10;
            int n16 = Math.max(1, (n14 + b3) / (s + b3));
            int n17 = (n14 - b3 * (n16 - 1)) / n16;
            int n18 = this.drawStringWithShadowComponent2.FONT_HEIGHT + this.drawStringWithShadowComponent2.FONT_HEIGHT + 14;
            int n19 = n17 + n18;

            for (int i = 0; i < size; i++) {
                int n20 = i % n16;
                int n21 = i / n16;
                int n22 = n15 + n20 * (n17 + b3);
                int n23 = n11 + n21 * (n19 + b3) + (int)this.factor3;
                if (n23 + n19 >= n11 && n23 <= n12 && mouseX >= n22 && mouseX <= n22 + n17 && mouseY >= n23 && mouseY <= n23 + n19) {
                    NebulaComponent.handleMainMenuScene2((MainMenuScene)mainMenuScenes.get(i));
                    this.count = i;
                    this.factor2 = 1.0F;
                    return;
                }
            }

            if (mouseX < n2 || mouseX > n2 + n3 || mouseY < n || mouseY > n + n4) {
                this.close();
            }
        }
    }

    private void process() {
        long l = System.currentTimeMillis() - this.timestamp;
        float f = Math.min(1.0F, (float)l / 280.0F);
        this.factor = this.flag ? f : Math.max(0.0F, 1.0F - f);
    }

    public boolean checkState() {
        return !this.flag && this.factor <= 0.01F;
    }

    private int[] computeIntArray(String str) {
        switch (str) {
            case "Fire":
                return new int[]{120, 40, 20};
            case "Normal":
                return new int[]{50, 60, 85};
            case "Ocean":
                return new int[]{20, 70, 95};
            case "Solar System":
                return new int[]{15, 18, 35};
            default:
                return new int[]{30, 32, 42};
        }
    }

    private Color computeColor(Color color, float f) {
        int n = (int)(color.getAlpha() * f);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, n)));
    }

    private float computeFloat(float f) {
        f = this.clamp(f, 0.0F, 1.0F);
        float f2 = f - 1.0F;
        return f2 * f2 * f2 + 1.0F;
    }

    public boolean isVisible() {
        return this.flag || this.factor > 0.01F;
    }
}
