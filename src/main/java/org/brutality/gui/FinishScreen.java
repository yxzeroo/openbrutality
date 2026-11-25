package org.brutality.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.gui.mainmenu.effects.MainMenuIconRenderer;
import org.brutality.system.ClientMetaManager;
import org.brutality.utils.render.OutlineThicknessHelper;
import org.brutality.utils.render.GlStateGuard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class FinishScreen extends CustomGuiScreen {
    private static final Color color = new Color(239, 245, 252);
    private static final Color color2 = new Color(159, 178, 196);
    private static final Color color3 = new Color(0, 0, 0, 0);
    private static final String[] stringArray = new String[]{"ADDED", "IMPROVED", "FIXED", "OTHER"};
    private final GuiScreen guiScreen;
    private List<ChangeLog.Entry> entries = new ArrayList<>();
    private final List<FinishScreen.Entry> entries2 = new ArrayList<>();
    private boolean initialized;
    private int count;
    private int count2;
    private int count3;
    private int count4;
    private int count5;
    private int count6;
    private int count7;
    private int count8;
    private int count9;
    private int count10 = -1;
    private int count11 = -1;
    private long timestamp;
    private float[] floatValueArray = new float[3];
    private CustomFontRenderer drawStringWithShadowComponent;
    private CustomFontRenderer drawStringWithShadowComponent2;
    private CustomFontRenderer drawStringWithShadowComponent3;
    private CustomFontRenderer drawStringWithShadowComponent4;
    private final int[][] intValueArray = new int[3][4];

    private void handleColor(Color color3) {
        boolean flag = this.entries.size() > 1;
        int n = this.count2 + this.count4 / 2;
        int n2 = this.count4 - 48;
        String str = flag ? "You missed " + this.entries.size() + " updates" : "One fresh update landed since your last launch.";
        if (flag && ClientMetaManager.computeLong() > 0L) {
            str = str + " over the last " + ClientMetaManager.computeString();
        }

        if (flag) {
            str = str + ".";
        }

        var list = computeList(str, n2, this.drawStringWithShadowComponent3);
        int n3 = this.drawStringWithShadowComponent4.FONT_HEIGHT
            + 14
            + this.drawStringWithShadowComponent.FONT_HEIGHT
            + 20
            + list.size() * (this.drawStringWithShadowComponent3.FONT_HEIGHT + 4)
            + 14
            + this.drawStringWithShadowComponent4.FONT_HEIGHT
            + 12;
        int n4 = this.count3 + Math.max(12, (this.count5 - 56 - n3) / 2);
        this.handleDrawStringWithShadowComponent(this.drawStringWithShadowComponent4, flag ? "WELCOME BACK" : "HEADS UP", n, n4, color3);
        n4 += this.drawStringWithShadowComponent4.FONT_HEIGHT + 14;
        this.handleDrawStringWithShadowComponent(this.drawStringWithShadowComponent, flag ? "Where have you been?" : "This is what's new!", n, n4, color);
        n4 += this.drawStringWithShadowComponent.FONT_HEIGHT + 10;
        int n5 = Math.min(n2 / 2, 210);
        MainMenuIconRenderer.handleFloat(n - n5 / 2, n4, n5, 1.0F, color3);
        int n6 = n4 + 12;

        for (String str2 : list) {
            this.handleDrawStringWithShadowComponent(this.drawStringWithShadowComponent3, str2, n, n6, color2);
            n6 += this.drawStringWithShadowComponent3.FONT_HEIGHT + 4;
        }

        n4 = n6 + 10;
        int n7 = Math.min(6, this.entries.size());
        int n8 = 8;

        while (true) {
            int n9 = 0;

            for (int i = 0; i < n7; i++) {
                n9 += this.drawStringWithShadowComponent4.getStringWidth("v" + this.entries.get(i).text) + 18 + (i > 0 ? n8 : 0);
            }

            if (n7 < this.entries.size()) {
                n9 += n8 + this.drawStringWithShadowComponent4.getStringWidth("+" + (this.entries.size() - n7)) + 18;
            }

            if (n9 <= n2 || n7 <= 1) {
                int n10 = n - n9 / 2;

                for (int j = 0; j < n7 + (n7 < this.entries.size() ? 1 : 0); j++) {
                    String str3 = j < n7 ? "v" + this.entries.get(j).text : "+" + (this.entries.size() - n7);
                    int textWidth = this.drawStringWithShadowComponent4.getStringWidth(str3);
                    MainMenuIconRenderer.handleInt(n10, n4, textWidth + 18, this.drawStringWithShadowComponent4.FONT_HEIGHT + 8, color3, j == 0);
                    this.handleDrawStringWithShadowComponent2(this.drawStringWithShadowComponent4, str3, n10 + 9, n4 + 4, j < n7 ? color : color3);
                    n10 += textWidth + 18 + n8;
                }

                return;
            }

            n7--;
        }
    }

    @Override
    public void initGui() {
        if (!this.initialized) {
            this.entries = ClientMetaManager.getEntries();
            this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(49, true);
            this.drawStringWithShadowComponent2 = FontComponent.computeDrawStringWithShadowComponent2(25, true);
            this.drawStringWithShadowComponent3 = FontComponent.computeDrawStringWithShadowComponent2(20, false);
            this.drawStringWithShadowComponent4 = FontComponent.computeDrawStringWithShadowComponent2(17, false);
            this.initialized = true;
        }

        this.count4 = Math.max(260, Math.min(620, (int)(this.width * 0.85F)));
        this.count5 = Math.max(170, (int)(this.height * 0.74F));
        this.count2 = (this.width - this.count4) / 2;
        this.count3 = (this.height - this.count5) / 2;
        this.count6 = this.count3 + 88;
        this.count7 = this.count3 + this.count5 - 56;
        int n = this.count3 + this.count5 - 42;
        this.handleInt(0, this.count2 + 18, n, 70, 28);
        this.handleInt(1, this.count2 + this.count4 - 118, n, 100, 28);
        this.handleInt(2, this.count2 + this.count4 - 199, n, 73, 28);
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(this.count4 < 380 ? 38 : 49, true);
        this.count11 = -1;
        this.timestamp = System.nanoTime();
    }

    private void handleDrawStringWithShadowComponent2(CustomFontRenderer drawStringWithShadowComponent, String str, int n, int n2, Color color) {
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawStringWithShadowComponent.drawString(str, n, n2, color.getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            for (int i = 0; i < 3; i++) {
                if (matchesIntArray(this.intValueArray[i], mouseX, mouseY) && (i != 0 || this.count > 0)) {
                    mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                    if (i == 2) {
                        this.finish();
                    } else {
                        this.handleInt(i == 0 ? -1 : 1);
                    }

                    return;
                }
            }
        }
    }

    private void handleInt(int n) {
        if (n > 0 && this.count == this.entries.size()) {
            this.finish();
        } else {
            this.count = Math.max(0, Math.min(this.entries.size(), this.count + n));
            this.count8 = 0;
        }
    }

    private void finish() {
        mc.displayGuiScreen(this.guiScreen);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.finish();
        } else if (keyCode == 28 || keyCode == 57 || keyCode == 205) {
            this.handleInt(1);
        } else if (keyCode == 203) {
            this.handleInt(-1);
        } else if (keyCode != 208 && keyCode != 209) {
            if (keyCode == 200 || keyCode == 201) {
                this.count8 = Math.max(0, this.count8 - (keyCode == 201 ? this.count7 - this.count6 : 24));
            }
        } else {
            this.count8 = Math.min(this.count9, this.count8 + (keyCode == 209 ? this.count7 - this.count6 : 24));
        }
    }

    private void handleInt(int n, int n2, int n3, int n4, int n5) {
        this.intValueArray[n] = new int[]{n2, n3, n4, n5};
    }

    private int computeInt(FinishScreen.Entry entry) {
        return entry.flag ? this.drawStringWithShadowComponent4.FONT_HEIGHT + 15 : (entry.y.isEmpty() ? 7 : this.drawStringWithShadowComponent3.FONT_HEIGHT + 5);
    }

    private static boolean matchesIntArray(int[] ints, int n, int n2) {
        return n >= ints[0] && n < ints[0] + ints[2] && n2 >= ints[1] && n2 < ints[1] + ints[3];
    }

    private void handleInt(int n, String str, Color color) {
        int[] ints = this.intValueArray[n];
        GlStateManager.enableTexture2D();
        MainMenuIconRenderer.handleInt(ints[0], ints[1], ints[2], ints[3], str, this.drawStringWithShadowComponent4, color, this.floatValueArray[n], n == 1);
    }

    private static Color computeColor(int n) {
        return n == 0
            ? new Color(112, 215, 167)
            : (n == 1 ? new Color(116, 185, 240) : (n == 2 ? new Color(237, 186, 115) : new Color(191, 157, 229)));
    }

    private static List<String> computeList(String str, int n, CustomFontRenderer drawStringWithShadowComponent) {
        List list = new ArrayList();
        String str2 = "";

        for (String str3 : str.split(" ")) {
            if (!str2.isEmpty() && drawStringWithShadowComponent.getStringWidth(str2 + " " + str3) > n) {
                list.add(str2);
                str2 = "";
            }

            for (int i = 0; i < str3.length(); i++) {
                String str4 = str2 + (i == 0 && !str2.isEmpty() ? " " : "") + str3.charAt(i);
                if (!str2.isEmpty() && drawStringWithShadowComponent.getStringWidth(str4) > n) {
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

        return list;
    }

    public FinishScreen(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
    }

    private static int computeInt(String str) {
        String str2 = str.toLowerCase(Locale.ROOT);
        return str2.startsWith("added") ? 0 : (!str2.startsWith("improved") && !str2.startsWith("remade") ? (str2.startsWith("fixed") ? 2 : 3) : 1);
    }

    private static String computeString(String str, int n, CustomFontRenderer drawStringWithShadowComponent) {
        if (drawStringWithShadowComponent.getStringWidth(str) <= n) {
            return str;
        }
        while (!str.isEmpty() && drawStringWithShadowComponent.getStringWidth(str + "...") > n) {
            str = str.substring(0, str.length() - 1);
        }

        return str + "...";
    }

    private void handleDrawStringWithShadowComponent(CustomFontRenderer drawStringWithShadowComponent, String str, int n, int n2, Color color) {
        this.handleDrawStringWithShadowComponent2(drawStringWithShadowComponent, str, n - drawStringWithShadowComponent.getStringWidth(str) / 2, n2, color);
    }

    private void handleColor2(Color color2) {
        ChangeLog.Entry entry = this.entries.get(this.count - 1);
        String str = "v" + entry.text;
        this.handleDrawStringWithShadowComponent2(this.drawStringWithShadowComponent2, str, this.count2 + 20, this.count3 + 42, color);
        String str2 = entry.releaseMode.displayName.toUpperCase(Locale.ROOT);
        int n = this.count2 + 32 + this.drawStringWithShadowComponent2.getStringWidth(str);
        int n2 = this.drawStringWithShadowComponent4.getStringWidth(str2) + 16;
        this.handleDrawStringWithShadowComponent2(
            this.drawStringWithShadowComponent4, str2, n + 8, this.count3 + 45, new Color(entry.releaseMode.color, true)
        );
        this.handleDrawStringWithShadowComponent2(
            this.drawStringWithShadowComponent4,
            computeString(entry.text3, this.count4 - 40, this.drawStringWithShadowComponent4),
            this.count2 + 20,
            this.count3 + 65,
            color2
        );
        int n3 = this.count4 - 64;
        if (this.count10 != this.count || this.count11 != n3) {
            this.entries2.clear();

            for (int i = 0; i < stringArray.length; i++) {
                boolean flag = false;

                for (String str3 : entry.entries) {
                    if (computeInt(str3) == i) {
                        if (!flag) {
                            this.entries2.add(new FinishScreen.Entry(stringArray[i], i, true));
                            flag = true;
                        }

                        for (String str4 : computeList(str3, n3, this.drawStringWithShadowComponent3)) {
                            this.entries2.add(new FinishScreen.Entry(str4, i, false));
                        }

                        this.entries2.add(new FinishScreen.Entry("", i, false));
                    }
                }
            }

            int n4 = 0;

            for (FinishScreen.Entry entry2 : this.entries2) {
                n4 += this.computeInt(entry2);
            }

            this.count9 = Math.max(0, n4 - (this.count7 - this.count6));
            this.count8 = Math.min(this.count8, this.count9);
            this.count10 = this.count;
            this.count11 = n3;
        }

        ScaledResolution sr = new ScaledResolution(mc);
        int scaleFactor = sr.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((this.count2 + 18) * scaleFactor, (this.height - this.count7) * scaleFactor, (this.count4 - 36) * scaleFactor, (this.count7 - this.count6) * scaleFactor);

        try {
            int n5 = this.count6 - this.count8;

            for (FinishScreen.Entry entry3 : this.entries2) {
                if (n5 + this.computeInt(entry3) >= this.count6 && n5 < this.count7) {
                    if (entry3.flag) {
                        OutlineThicknessHelper.handleFloat(
                            this.count2 + 20, n5 + 3, 3.0F, this.drawStringWithShadowComponent4.FONT_HEIGHT, 1.5F, computeColor(entry3.count)
                        );
                        this.handleDrawStringWithShadowComponent2(
                            this.drawStringWithShadowComponent4, entry3.y, this.count2 + 30, n5 + 3, computeColor(entry3.count)
                        );
                    } else {
                        this.handleDrawStringWithShadowComponent2(this.drawStringWithShadowComponent3, entry3.y, this.count2 + 24, n5, color);
                    }
                }

                n5 += this.computeInt(entry3);
            }
        } finally {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }

        if (this.count9 > 0) {
            int n6 = this.count7 - this.count6;
            int n7 = Math.max(18, n6 * n6 / (n6 + this.count9));
            int n8 = this.count6 + (n6 - n7) * this.count8 / this.count9;
            OutlineThicknessHelper.handleFloat(this.count2 + this.count4 - 12, this.count6, 3.0F, n6, 1.5F, new Color(255, 255, 255, 15));
            OutlineThicknessHelper.handleFloat(this.count2 + this.count4 - 12, n8, 3.0F, n7, 1.5F, color2);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.entries.isEmpty()) {
            this.finish();
        } else {
            try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                GlStateManager.disableDepth();
                GlStateManager.depthMask(false);
                GlStateManager.disableLighting();
                GlStateManager.disableFog();
                GlStateManager.disableCull();
                OceanComponent.handleGuiScreen(this, mouseX, mouseY, partialTicks);
                GlStateManager.enableBlend();
                GlStateManager.disableAlpha();
                Color color = OceanComponent.computeColor();
                long now = System.nanoTime();
                float f = 1.0F - (float)Math.exp(-14.0 * Math.min(0.05, (now - this.timestamp) / 1.0E9));
                this.timestamp = now;
                MainMenuIconRenderer.handleInt(this.count2, this.count3, this.count4, this.count5, 14.0F, 1.12F);
                if (this.count > 0) {
                    MainMenuIconRenderer.handleInt(this.count2 + 18, this.count3 + 34, this.count4 - 36, 30);
                }

                if (this.count > 0) {
                    this.handleDrawStringWithShadowComponent2(
                        this.drawStringWithShadowComponent4, "UPDATE " + this.count + " OF " + this.entries.size(), this.count2 + 20, this.count3 + 18, color
                    );
                    String str = this.entries.get(this.count - 1).text2;
                    this.handleDrawStringWithShadowComponent2(
                        this.drawStringWithShadowComponent4,
                        str,
                        this.count2 + this.count4 - 20 - this.drawStringWithShadowComponent4.getStringWidth(str),
                        this.count3 + 18,
                        color2
                    );
                }

                if (this.count == 0) {
                    this.handleColor(color);
                } else {
                    this.handleColor2(color);
                }

                MainMenuIconRenderer.handleInt(this.count2 + 18, this.count3 + this.count5 - 52, this.count4 - 36, 26);

                for (int i = 0; i < 3; i++) {
                    this.floatValueArray[i] = this.floatValueArray[i]
                        + ((matchesIntArray(this.intValueArray[i], mouseX, mouseY) ? 1.0F : 0.0F) - this.floatValueArray[i]) * f;
                    if (i != 0 || this.count != 0) {
                        this.handleInt(i, i == 0 ? "Back" : (i == 2 ? "Skip" : (this.count == this.entries.size() ? "Finish" : "Next")), color);
                    }
                }

                if (this.count4 >= 380) {
                    int n = Math.min(90, this.count4 - 310);
                    OutlineThicknessHelper.handleFloat(this.count2 + 100, this.count3 + this.count5 - 29, n, 3.0F, 1.5F, new Color(255, 255, 255, 24));
                    OutlineThicknessHelper.handleFloat(
                        this.count2 + 100,
                        this.count3 + this.count5 - 29,
                        Math.max(3.0F, n * (this.count + 1.0F) / (this.entries.size() + 1.0F)),
                        3.0F,
                        1.5F,
                        color
                    );
                }
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int eventDWheel = Mouse.getEventDWheel();
        if (this.count > 0 && eventDWheel != 0) {
            this.count8 = Math.max(0, Math.min(this.count9, this.count8 - Integer.signum(eventDWheel) * 32));
        }
    }

    public static final class Entry {
        public final String y;
        public final int count;
        public final boolean flag;

        public Entry(String str, int count, boolean flag) {
            this.y = str;
            this.count = count;
            this.flag = flag;
        }
    }
}
