package org.brutality.gui.chat;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.OceanComponent;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.gui.mainmenu.effects.MainMenuIconRenderer;
import org.lwjgl.input.Mouse;

public class NoPitPandaDataScreen extends CustomGuiScreen {
    private static final int count = 12;
    private static final int count2 = 18;
    private static final String[] stringArray = new String[]{"Overview", "Inventory", "Ender Chest", "Stats"};
    private final GuiScreen guiScreen;
    private final String text;
    private final String text2;
    private final CompletableFuture<PitPandaPlayerProfile> completableFuture;
    private int count3;
    private int count4;
    private int count5;
    private int count6;
    private int count7;
    private int count8;
    private int count9;
    private final Rectangle rectangle = new Rectangle();
    private final Rectangle[] rectangleArray = new Rectangle[stringArray.length];
    private final float[] floatValueArray = new float[stringArray.length];
    private float factor;
    private List<String> entries;
    private PitPandaPlayerProfile.Entry2 entry2;
    private PitPandaPlayerProfile.Entry2 entry22;
    private CompletableFuture<FetchComponent.Entry> completableFuture2;
    private final Rectangle rectangle2 = new Rectangle();
    private float factor2;
    private long timestamp;
    private int count10;
    private int count11;
    private final long timestamp2 = System.nanoTime();
    private long timestamp3 = System.nanoTime();
    private static final long timestamp4 = 60L;

    private void handleEntry2(PitPandaPlayerProfile.Entry2 entry2, int n, int n2) {
        drawRect(n, n2, n + 18, n2 + 18, 1426063360);
        drawRect(n, n2, n + 18, n2 + 1, 352321535);
        ItemStack stack = entry2 == null ? null : entry2.computeItemStack();
        if (stack != null) {
            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableDepth();
            RenderHelper.enableGUIStandardItemLighting();
            this.itemRender.renderItemAndEffectIntoGUI(stack, n + 1, n2 + 1);
            if (this.fontRendererObj != null) {
                this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, stack, n + 1, n2 + 1, null);
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.entry22 != null) {
            if (mouseButton == 0 && this.rectangle2.contains(mouseX, mouseY)) {
                this.process();
            }
        } else if (mouseButton == 0 && this.entry2 != null && this.entry2.timestamp > 0L) {
            this.entry22 = this.entry2;
            this.completableFuture2 = FetchComponent.fetch(this.entry2.timestamp);
            this.timestamp = System.nanoTime();
            this.factor2 = 0.0F;
        } else if (mouseButton == 0) {
            if (this.rectangle.contains(mouseX, mouseY)) {
                mc.displayGuiScreen(this.guiScreen);
            } else {
                for (int i = 0; i < stringArray.length; i++) {
                    if (this.rectangleArray[i].contains(mouseX, mouseY)) {
                        this.count3 = i;
                        return;
                    }
                }
            }
        }
    }

    private void handleList(List<PitPandaPlayerProfile.Entry2> entry2s, int n, int n2, int n3, int n4, int n5) {
        for (int i = 0; i < n; i++) {
            this.handleEntry2(computeEntry2(entry2s, i), n2 + i * 20, n3, n4, n5);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        long now = System.nanoTime();
        float f = 1.0F - (float)Math.exp(-16.0F * Math.min(0.05F, (float)(now - this.timestamp3) / 1.0E9F));
        this.timestamp3 = now;
        this.entries = null;
        this.entry2 = null;
        boolean flag = this.entry22 != null;
        if (flag) {
            mouseX = -1;
            mouseY = -1;
        }

        float f2 = AvatarComponent.computeFloat(this.timestamp2);
        drawRect(0, 0, this.width, this.height, (int)(140.0F * f2) << 24);
        AvatarComponent.handleFloat(f2, this.count5 + this.count7 / 2, this.count6 + this.count8 / 2);
        AvatarComponent.handleInt(this.count5, this.count6, this.count7, this.count8, f2);
        CustomFontRenderer drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(15, false);
        CustomFontRenderer drawStringWithShadowComponent2 = FontComponent.computeDrawStringWithShadowComponent2(17, false);
        PitPandaPlayerProfile fetchComponent2 = this.completableFuture.isDone() ? this.completableFuture.getNow(null) : null;
        int n = this.count5 + 12;
        int n2 = this.count6 + 12;
        AvatarComponent.handleString(this.text, n, n2, 28);
        GlStateManager.enableTexture2D();
        String str = fetchComponent2 != null && !fetchComponent2.formattedName.isEmpty() ? fetchComponent2.formattedName : "§f" + this.text;
        computeDrawStringWithShadowComponent().drawStringWithShadow(str, n + 36, n2 + 3, -1);
        String str2 = fetchComponent2 == null
            ? (this.completableFuture.isDone() ? "No PitPanda data" : "Loading PitPanda...")
            : (fetchComponent2.flag ? "Online now" : "Last seen " + PitPandaPlayerProfile.computeString(fetchComponent2.timestamp3))
                + (fetchComponent2.text3.isEmpty() ? "" : "  ·  " + computeString(fetchComponent2.text3));
        if (fetchComponent2 != null && !fetchComponent2.formattedLevel.isEmpty()) {
            computeDrawStringWithShadowComponent().drawStringWithShadow(fetchComponent2.formattedLevel, n + 36, n2 + 16, -1);
            drawStringWithShadowComponent.drawString(str2, n + 40 + computeDrawStringWithShadowComponent().getStringWidth(fetchComponent2.formattedLevel), n2 + 17, -7497816);
        } else {
            drawStringWithShadowComponent.drawString((this.text2.isEmpty() ? "" : "[" + this.text2 + "]  ") + str2, n + 36, n2 + 17, -7497816);
        }

        this.factor = this.factor + ((this.rectangle.contains(mouseX, mouseY) ? 1.0F : 0.0F) - this.factor) * f;
        AvatarComponent.handleInt(this.rectangle.x, this.rectangle.y, this.rectangle.width, this.rectangle.height, "‹  Back", drawStringWithShadowComponent, this.factor, false, false);

        for (int i = 0; i < stringArray.length; i++) {
            this.floatValueArray[i] = this.floatValueArray[i]
                + ((this.rectangleArray[i].contains(mouseX, mouseY) ? 1.0F : 0.0F) - this.floatValueArray[i]) * f;
            AvatarComponent.handleInt(
                this.rectangleArray[i].x,
                this.rectangleArray[i].y,
                this.rectangleArray[i].width,
                this.rectangleArray[i].height,
                stringArray[i],
                drawStringWithShadowComponent,
                this.floatValueArray[i],
                this.count3 == i,
                false
            );
        }

        drawRect(n, this.count9 - 5, this.count5 + this.count7 - 12, this.count9 - 4, 419430399);
        if (fetchComponent2 == null) {
            String str3 = this.completableFuture.isDone() ? "PitPanda has no profile for " + this.text + "." : "Fetching " + this.text + "'s profile...";
            drawStringWithShadowComponent2.drawString(str3, this.count5 + (this.count7 - drawStringWithShadowComponent2.getStringWidth(str3)) / 2, this.count9 + 60, -7497816);
        } else if (this.count3 == 0) {
            this.handleFetchComponent22(fetchComponent2, drawStringWithShadowComponent, mouseX, mouseY);
        } else if (this.count3 == 1) {
            this.handleFetchComponent23(fetchComponent2, drawStringWithShadowComponent, mouseX, mouseY);
        } else if (this.count3 == 2) {
            this.handleFetchComponent24(fetchComponent2, drawStringWithShadowComponent, mouseX, mouseY);
        } else {
            this.handleFetchComponent2(fetchComponent2, drawStringWithShadowComponent, mouseX, mouseY);
        }

        GlStateManager.popMatrix();
        if (flag) {
            this.handleInt(Mouse.getX() * this.width / mc.displayWidth, this.height - Mouse.getY() * this.height / mc.displayHeight - 1, f);
        } else if (this.entries != null) {
            this.handleList(this.entries, this.count10, this.count11);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private static CustomFontRenderer computeDrawStringWithShadowComponent() {
        return FontComponent.computeDrawStringWithShadowComponent2(16, false);
    }

    public NoPitPandaDataScreen(GuiScreen guiScreen, String text, String str) {
        this.guiScreen = guiScreen;
        this.text = text;
        this.text2 = str == null ? "" : str;
        this.completableFuture = PitPandaPlayerProfile.fetch(text);
    }

    private void handleFetchComponent23(PitPandaPlayerProfile fetchComponent2, CustomFontRenderer drawStringWithShadowComponent, int n, int n2) {
        short s = 180;
        int n3 = this.count5 + (this.count7 - s - 18 - 16) / 2;
        int n4 = this.count9 + 6;

        for (int i = 0; i < 4; i++) {
            this.handleEntry2(computeEntry2(fetchComponent2.entries2, i), n3, n4 + i * 20, n, n2);
        }

        int n5 = n3 + 18 + 16;
        drawStringWithShadowComponent.drawString("INVENTORY", n5, n4 - 4, -7695194);

        for (int j = 0; j < 3; j++) {
            for (int k = 0; k < 9; k++) {
                this.handleEntry2(computeEntry2(fetchComponent2.entries, 9 + j * 9 + k), n5 + k * 20, n4 + 8 + j * 20, n, n2);
            }
        }

        drawStringWithShadowComponent.drawString("HOTBAR", n5, n4 + 8 + 60 + 4, -7695194);

        for (int idx = 0; idx < 9; idx++) {
            this.handleEntry2(computeEntry2(fetchComponent2.entries, idx), n5 + idx * 20, n4 + 8 + 60 + 16, n, n2);
        }
    }

    private void handleFetchComponent22(PitPandaPlayerProfile fetchComponent2, CustomFontRenderer drawStringWithShadowComponent, int n, int n2) {
        int n3 = this.count5 + 12;
        int n4 = this.count9;
        drawStringWithShadowComponent.drawString("ARMOR", n3, n4, -7695194);

        for (int i = 0; i < 4; i++) {
            this.handleEntry2(computeEntry2(fetchComponent2.entries2, i), n3, n4 + 12 + i * 20, n, n2);
        }

        int n5 = n3 + 18 + 16;
        drawStringWithShadowComponent.drawString("PERKS", n5, n4, -7695194);
        this.handleList(fetchComponent2.entries4, 4, n5, n4 + 12, n, n2);
        drawStringWithShadowComponent.drawString("KILLSTREAKS", n5, n4 + 40, -7695194);
        this.handleList(fetchComponent2.entries5, Math.max(3, fetchComponent2.entries5.size()), n5, n4 + 52, n, n2);
        drawStringWithShadowComponent.drawString("UPGRADES", n5, n4 + 80, -7695194);
        this.handleList(fetchComponent2.entries6, Math.max(7, fetchComponent2.entries6.size()), n5, n4 + 92, n, n2);
        int n6 = n5 + 140 + 14;
        int n7 = this.count5 + this.count7 - 12 - n6;
        String[][] partsArray = new String[][]{
            {"Gold", PitPandaPlayerProfile.computeString2(fetchComponent2.amount)},
            {"Playtime", fetchComponent2.timestamp2 / timestamp4 + "h"},
            {"Kills", PitPandaPlayerProfile.computeString2(fetchComponent2.timestamp4)},
            {"Deaths", PitPandaPlayerProfile.computeString2(fetchComponent2.timestamp5)},
            {"K/D", String.format(Locale.ROOT, "%.2f", fetchComponent2.amount2)},
            {"Assists", PitPandaPlayerProfile.computeString2(fetchComponent2.timestamp9)},
            {"Best streak", Long.toString(fetchComponent2.timestamp8)},
            {"Bounty", fetchComponent2.timestamp6 + "g"},
            {"Renown", Long.toString(fetchComponent2.timestamp7)},
            {"Seen", fetchComponent2.flag ? "Online" : PitPandaPlayerProfile.computeString(fetchComponent2.timestamp3)}
        };
        int n8 = (n7 - 6) / 2;

        for (int j = 0; j < partsArray.length; j++) {
            int n9 = n6 + j % 2 * (n8 + 6);
            int n10 = n4 + j / 2 * 22;
            drawRect(n9, n10, n9 + n8, n10 + 19, 805306368);
            GlStateManager.enableTexture2D();
            drawStringWithShadowComponent.drawString(partsArray[j][0], n9 + 5, n10 + 2, -8550246);
            drawStringWithShadowComponent.drawString(partsArray[j][1], n9 + 5, n10 + 10, -1446413);
        }

        int n11 = n4 + 110 + 6;
        this.handleDrawStringWithShadowComponent(drawStringWithShadowComponent, "Prestige XP", fetchComponent2.text4, fetchComponent2.factor, n6, n11, n7);
        this.handleDrawStringWithShadowComponent(drawStringWithShadowComponent, "Prestige gold", fetchComponent2.text5, fetchComponent2.factor2, n6, n11 + 22, n7);
        this.handleDrawStringWithShadowComponent(drawStringWithShadowComponent, "Renown", fetchComponent2.text6, fetchComponent2.factor3, n6, n11 + 44, n7);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            if (this.entry22 != null) {
                this.process();
            } else {
                mc.displayGuiScreen(this.guiScreen);
            }
        }
    }

    private void handleInt(int n, int n2, float f) {
        CustomFontRenderer drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(15, false);
        CustomFontRenderer drawStringWithShadowComponent2 = FontComponent.computeDrawStringWithShadowComponent2(18, true);
        FetchComponent.Entry entry = this.completableFuture2.isDone() ? this.completableFuture2.getNow(null) : null;
        int n3 = entry == null ? 1 : Math.max(1, entry.entries.size() + (entry.stringArray != null ? 2 : 0));
        int n4 = Math.min(280, this.count7 - 40);
        int n5 = 44 + n3 * 13 + 12 + 18 + 12;
        int n6 = this.count5 + (this.count7 - n4) / 2;
        int n7 = this.count6 + Math.max(8, (this.count8 - n5) / 2);
        float f2 = AvatarComponent.computeFloat(this.timestamp);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 300.0F);
        drawRect(this.count5, this.count6, this.count5 + this.count7, this.count6 + this.count8, (int)(120.0F * f2) << 24);
        AvatarComponent.handleFloat(f2, n6 + n4 / 2, n7 + n5 / 2);
        AvatarComponent.handleInt(n6, n7, n4, n5, f2);
        this.handleEntry2(this.entry22, n6 + 12, n7 + 12);
        GlStateManager.enableTexture2D();
        computeDrawStringWithShadowComponent().drawStringWithShadow(this.entry22.text, n6 + 12 + 18 + 8, n7 + 13, -1);
        drawStringWithShadowComponent.drawString("Item history", n6 + 12 + 18 + 8, n7 + 24, -7497816);
        int n8 = n7 + 12 + 22 + 10;
        if (entry == null) {
            drawStringWithShadowComponent.drawString("Looking up owners on PitPanda...", n6 + 12, n8, -7497816);
        } else if (entry.error != null && entry.entries.isEmpty()) {
            drawStringWithShadowComponent.drawString(entry.error, n6 + 12, n8, -2065798);
        } else {
            for (int i = 0; i < entry.entries.size(); i++) {
                String[] parts = entry.entries.get(i);
                drawStringWithShadowComponent.drawString(i + 1 + ".", n6 + 12, n8, -9669242);
                drawStringWithShadowComponent.drawString(parts[0], n6 + 28, n8, i == 0 ? -920327 : -3551270);
                drawStringWithShadowComponent.drawString(parts[1], n6 + n4 - 12 - drawStringWithShadowComponent.getStringWidth(parts[1]), n8, -8550246);
                n8 += 13;
            }

            if (entry.stringArray != null) {
                drawRect(n6 + 12, n8 + 4, n6 + n4 - 12, n8 + 5, 536870911);
                n8 += 13;
                GlStateManager.enableTexture2D();
                drawStringWithShadowComponent.drawString("Earliest: " + entry.stringArray[0], n6 + 12, n8, -997772);
                drawStringWithShadowComponent.drawString(entry.stringArray[1], n6 + n4 - 12 - drawStringWithShadowComponent.getStringWidth(entry.stringArray[1]), n8, -8550246);
            }
        }

        this.rectangle2.setBounds(n6 + 12, n7 + n5 - 12 - 18, 64, 18);
        this.factor2 = this.factor2 + ((this.rectangle2.contains(n, n2) ? 1.0F : 0.0F) - this.factor2) * f;
        AvatarComponent.handleInt(
            this.rectangle2.x, this.rectangle2.y, this.rectangle2.width, this.rectangle2.height, "‹  Back", drawStringWithShadowComponent, this.factor2, false, false
        );
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    private void process() {
        this.entry22 = null;
        this.completableFuture2 = null;
    }

    @Override
    public void initGui() {
        this.count7 = Math.min(440, this.width - 12);
        this.count8 = Math.min(300, this.height - 8);
        this.count5 = (this.width - this.count7) / 2;
        this.count6 = (this.height - this.count8) / 2;
        this.rectangle.setBounds(this.count5 + this.count7 - 12 - 56, this.count6 + 12 + 4, 56, 16);
        CustomFontRenderer drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(15, false);
        int n = this.count5 + 12;
        int n2 = this.count6 + 12 + 36;

        for (int i = 0; i < stringArray.length; i++) {
            int n3 = drawStringWithShadowComponent.getStringWidth(stringArray[i]) + 18;
            this.rectangleArray[i] = new Rectangle(n, n2, n3, 16);
            n += n3 + 4;
        }

        this.count9 = n2 + 16 + 10;
    }

    private void handleDrawStringWithShadowComponent(CustomFontRenderer drawStringWithShadowComponent, String str, String str2, float f, int n, int n2, int n3) {
        drawStringWithShadowComponent.drawString(str, n, n2, -8550246);
        drawStringWithShadowComponent.drawString(str2, n + n3 - drawStringWithShadowComponent.getStringWidth(str2), n2, -3551270);
        drawRect(n, n2 + 11, n + n3, n2 + 14, 822083583);
        int n4 = (int)(n3 * Math.max(0.0F, Math.min(1.0F, f)));
        drawRect(n, n2 + 11, n + n4, n2 + 14, MainMenuIconRenderer.computeColor(OceanComponent.computeColor(), 230).getRGB());
        GlStateManager.enableTexture2D();
    }

    private static PitPandaPlayerProfile.Entry2 computeEntry2(List<PitPandaPlayerProfile.Entry2> entry2s, int n) {
        return n >= 0 && n < entry2s.size() ? (PitPandaPlayerProfile.Entry2)entry2s.get(n) : null;
    }

    private void handleList(List<String> list, int n, int n2) {
        CustomFontRenderer drawStringWithShadowComponent = computeDrawStringWithShadowComponent();
        int n3 = 0;

        for (String str : list) {
            n3 = Math.max(n3, drawStringWithShadowComponent.getStringWidth(str));
        }

        int n4 = drawStringWithShadowComponent.FONT_HEIGHT + 2;
        int n5 = list.size() * n4 + 8 + (list.size() > 1 ? 3 : 0);
        int n6 = n + 12;
        int n7 = n2 - 12;
        if (n6 + n3 + 12 > this.width) {
            n6 = n - n3 - 16;
        }

        if (n7 + n5 + 4 > this.height) {
            n7 = this.height - n5 - 4;
        }

        if (n7 < 4) {
            n7 = 4;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 400.0F);
        MainMenuIconRenderer.handleInt(n6, n7, n3 + 12, n5, 4.0F, 1.3F);
        GlStateManager.enableTexture2D();
        int n8 = n7 + 5;

        for (int i = 0; i < list.size(); i++) {
            drawStringWithShadowComponent.drawStringWithShadow((String)list.get(i), n6 + 6, n8, -5592406);
            n8 += n4 + (i == 0 ? 3 : 0);
        }

        GlStateManager.popMatrix();
    }

    private void handleEntry2(PitPandaPlayerProfile.Entry2 entry2, int n, int n2, int n3, int n4) {
        boolean flag = n3 >= n && n3 < n + 18 && n4 >= n2 && n4 < n2 + 18;
        this.handleEntry2(entry2, n, n2);
        if (flag) {
            drawRect(n + 1, n2 + 1, n + 18 - 1, n2 + 18 - 1, 1090519039);
            if (entry2 != null) {
                this.entries = entry2.computeList();
                this.count10 = n3;
                this.count11 = n4;
                this.entry2 = entry2;
            }
        }
    }

    private void handleFetchComponent2(PitPandaPlayerProfile fetchComponent2, CustomFontRenderer drawStringWithShadowComponent, int n, int n2) {
        int n3 = this.count5 + 12;
        int n4 = this.count9;
        List generalStats = fetchComponent2.generalStats;

        for (int i = 0; i < generalStats.size(); i++) {
            int n5 = n4 + i * 22;
            Rectangle rect = new Rectangle(n3, n5, 120, 18);
            if (rect.contains(n, n2)) {
                this.count4 = i;
            }

            if (i == this.count4) {
                drawRect(n3 - 2, n5 - 1, n3 + 120, n5 + 18 + 1, MainMenuIconRenderer.computeColor(OceanComponent.computeColor(), 40).getRGB());
            }

            this.handleEntry2((PitPandaPlayerProfile.Entry2)generalStats.get(i), n3, n5);
            PitPandaPlayerProfile.Entry2 entry2 = (PitPandaPlayerProfile.Entry2)generalStats.get(i);
            if (entry2 != null) {
                computeDrawStringWithShadowComponent().drawStringWithShadow(entry2.text, n3 + 18 + 6, n5 + 5, -1);
            }
        }

        if (!generalStats.isEmpty() && this.count4 < generalStats.size() && generalStats.get(this.count4) != null) {
            PitPandaPlayerProfile.Entry2 entry22 = (PitPandaPlayerProfile.Entry2)generalStats.get(this.count4);
            int n6 = n3 + 132;
            int n7 = this.count5 + this.count7 - 12 - n6;
            drawRect(n6, n4, n6 + n7, this.count6 + this.count8 - 12, 805306368);
            computeDrawStringWithShadowComponent().drawStringWithShadow(entry22.text, n6 + 8, n4 + 7, -1);
            int n8 = n4 + 22;

            for (String str : entry22.entries) {
                if (n8 > this.count6 + this.count8 - 12 - 10) {
                    break;
                }

                computeDrawStringWithShadowComponent().drawStringWithShadow(str, n6 + 8, n8, -1);
                n8 += 10;
            }
        }
    }

    private static String computeString(String str) {
        return str.isEmpty() ? str : str.charAt(0) + str.substring(1).toLowerCase(Locale.ROOT);
    }

    private void handleFetchComponent24(PitPandaPlayerProfile fetchComponent2, CustomFontRenderer drawStringWithShadowComponent, int n, int n2) {
        short s = 180;
        int n3 = this.count5 + (this.count7 - s) / 2;
        int n4 = this.count9 + 10;
        drawStringWithShadowComponent.drawString("ENDER CHEST", n3, n4 - 4, -7695194);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.handleEntry2(computeEntry2(fetchComponent2.entries3, i * 9 + j), n3 + j * 20, n4 + 8 + i * 20, n, n2);
            }
        }
    }
}
