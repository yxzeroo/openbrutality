package org.brutality.clickgui.newclick;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.Brutality;
import org.brutality.clickgui.discord.Inter18ptSemiboldTtfComponent;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.OceanComponent;
import org.brutality.gui.SearchField;
import org.brutality.gui.chat.AvatarComponent;
import org.brutality.gui.mainmenu.effects.MainMenuIconRenderer;
import org.brutality.notification.old.ModuleEnabledHandler;
import org.brutality.system.cloud.BrutalityCloudClient;
import org.brutality.utils.render.OutlineThicknessHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiCloudBrowser extends CustomGuiScreen {
    private static final int count = 320;
    private static final int count2 = 36;
    private final GuiScreen guiScreen;
    private final Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent = new Inter18ptSemiboldTtfComponent();
    private BrutalityCloudClient.ConfigMode configMode = BrutalityCloudClient.ConfigMode.CONFIG;
    private BrutalityCloudClient.PopularMode popularMode = BrutalityCloudClient.PopularMode.POPULAR;
    private boolean flag;
    private GuiCloudBrowser.BrowseMode browseMode = GuiCloudBrowser.BrowseMode.BROWSE;
    private final List<BrutalityCloudClient.Entry3> entries = new CopyOnWriteArrayList<>();
    private String text = "Loading…";
    private boolean loading;
    private String text2 = "";
    private GuiTextField searchField;
    private int count3;
    private int count4;
    private BrutalityCloudClient.Entry3 entry3;
    private final List<BrutalityCloudClient.Entry4> entries2 = new CopyOnWriteArrayList<>();
    private int count5;
    private int count6;
    private GuiTextField guiTextField;
    private final List<String> entries3 = new ArrayList<>();
    private int count7;
    private GuiTextField nameField;
    private GuiTextField guiTextField2;
    private String text3 = "";
    private boolean flag2;
    private boolean flag3;
    private boolean flag4;
    private float factor;
    private int count8;
    private int count9;
    private int count10;
    private int count11;
    private int count12;
    private int count13;

    private void handleInt9(int n, int n2) {
        int n3 = this.count9 + 28;
        if (matchesInt(n, n2, this.count8 + 8, n3, 50, 18)) {
            this.browseMode = GuiCloudBrowser.BrowseMode.DETAIL;
            this.flag2 = false;
        } else if (this.entry3 != null) {
            this.nameField.mouseClicked(n, n2, 0);
            this.guiTextField2.mouseClicked(n, n2, 0);
            if (matchesInt(n, n2, this.count8 + 8, n3 + 108, 120, 18)) {
                this.flag3 = !this.flag3;
            } else {
                if (this.flag3) {
                    if (matchesInt(n, n2, this.count8 + 320, n3 + 108, 24, 18) && !this.entries3.isEmpty()) {
                        this.count7 = (this.count7 - 1 + this.entries3.size()) % this.entries3.size();
                        return;
                    }

                    if (matchesInt(n, n2, this.count8 + 348, n3 + 108, 24, 18) && !this.entries3.isEmpty()) {
                        this.count7 = (this.count7 + 1) % this.entries3.size();
                        return;
                    }
                }

                if (matchesInt(n, n2, this.count8 + 8, n3 + 140, 90, 20)) {
                    String str = this.nameField.getText().trim();
                    if (str.isEmpty()) {
                        this.text3 = "Enter a display name";
                        this.handleString3("Edit", "Enter a display name");
                    } else {
                        File file = null;
                        if (this.flag3) {
                            if (this.entries3.isEmpty()) {
                                this.handleString3("Edit", "No local file selected");
                                return;
                            }

                            String str2 = this.entries3.get(this.count7);
                            file = this.entry3.kind == BrutalityCloudClient.ConfigMode.CONFIG
                                ? new File(Brutality.appdataHandler.computeFile(), str2)
                                : new File("Brutality/Scripts", str2);
                        }

                        this.text3 = "Saving…";
                        this.handleString("Cloud", "Saving changes…");
                        BrutalityCloudClient.updateAsync(
                            this.entry3.id,
                            str,
                            this.guiTextField2.getText().trim(),
                            file,
                            entry -> mc.addScheduledTask(
                                () -> {
                                    if (entry.ok) {
                                        this.text3 = "Saved!";
                                        this.handleString2("Updated", str + " saved on Cloud");
                                        this.entry3 = new BrutalityCloudClient.Entry3(
                                            this.entry3.id,
                                            this.entry3.code,
                                            this.entry3.kind,
                                            str,
                                            this.guiTextField2.getText().trim(),
                                            this.entry3.author,
                                            this.entry3.avgRating,
                                            this.entry3.ratingCount,
                                            this.entry3.downloads,
                                            this.entry3.createdAt,
                                            this.entry3.fileName
                                        );
                                        this.browseMode = GuiCloudBrowser.BrowseMode.DETAIL;
                                        this.process3();
                                    } else {
                                        this.text3 = "Failed: " + entry.error;
                                        this.handleString3("Update Failed", entry.error != null ? entry.error : "Could not save");
                                    }
                                }
                            )
                        );
                    }
                } else {
                    if (matchesInt(n, n2, this.count8 + 106, n3 + 140, 70, 20)) {
                        this.handleEntry34(this.entry3);
                    }
                }
            }
        }
    }

    private void handleEntry34(BrutalityCloudClient.Entry3 entry3) {
        if (!this.flag2) {
            this.flag2 = true;
            this.handleString("Delete", "Click Delete again to confirm");
        } else {
            this.flag2 = false;
            this.handleString("Cloud", "Deleting " + entry3.name + "…");
            BrutalityCloudClient.deleteAsync(entry3.id, entry -> mc.addScheduledTask(() -> {
                if (entry.ok) {
                    this.handleString2("Deleted", entry3.name + " removed from Cloud");
                    this.entry3 = null;
                    this.browseMode = GuiCloudBrowser.BrowseMode.BROWSE;
                    this.process3();
                } else {
                    this.handleString3("Delete Failed", entry.error != null ? entry.error : "Could not delete");
                }
            }));
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            if (this.flag4) {
                this.flag4 = false;
            } else if (this.browseMode != GuiCloudBrowser.BrowseMode.BROWSE) {
                this.browseMode = GuiCloudBrowser.BrowseMode.BROWSE;
            } else {
                mc.displayGuiScreen(this.guiScreen);
            }
        } else if (this.browseMode == GuiCloudBrowser.BrowseMode.BROWSE && this.searchField.textboxKeyTyped(typedChar, keyCode)) {
            this.text2 = this.searchField.getText();
            if (keyCode == 28) {
                this.process3();
            }
        } else if (this.browseMode != GuiCloudBrowser.BrowseMode.DETAIL || !this.guiTextField.textboxKeyTyped(typedChar, keyCode)) {
            if (this.browseMode == GuiCloudBrowser.BrowseMode.UPLOAD || this.browseMode == GuiCloudBrowser.BrowseMode.EDIT) {
                if (this.nameField.textboxKeyTyped(typedChar, keyCode)) {
                    return;
                }

                if (this.guiTextField2.textboxKeyTyped(typedChar, keyCode)) {
                    return;
                }
            }

            super.keyTyped(typedChar, keyCode);
        }
    }

    private void openUpload() {
        this.browseMode = GuiCloudBrowser.BrowseMode.UPLOAD;
        this.flag4 = false;
        this.text3 = "";
        this.entries3.clear();
        this.count7 = 0;
        if (this.configMode == BrutalityCloudClient.ConfigMode.CONFIG) {
            File file = Brutality.appdataHandler != null ? Brutality.appdataHandler.computeFile() : new File("Brutality/Configs");
            File[] files = file.listFiles((file2, str) -> str.endsWith(".json"));
            if (files != null) {
                for (File file3 : files) {
                    this.entries3.add(file3.getName());
                }
            }
        } else if (Brutality.scriptManager != null) {
            this.entries3.addAll(Brutality.scriptManager.getAvailableScriptFiles());
        }

        if (this.nameField != null) {
            this.nameField.setText("");
        }

        if (this.guiTextField2 != null) {
            this.guiTextField2.setText("");
        }
    }

    private void handleEntry3(BrutalityCloudClient.Entry3 entry3) {
        String str = entry3.computeString();
        if (str != null && !str.isEmpty()) {
            GuiScreen.setClipboardString(str);
            this.handleString2("Copied ID", str);
            this.handleString("random.orb", 0.5F, 1.5F);
        } else {
            this.handleString3("Copy ID", "This item has no id yet");
        }
    }

    private boolean matchesInt(int n, int n2) {
        int n3 = this.width - 320;
        int n4 = 60;
        int n5 = this.height - 80;
        if (matchesInt(n, n2, n3 + 12, n4 + n5 - 28, 80, 18)) {
            this.flag4 = false;
            return true;
        }
        if (matchesInt(n, n2, n3 + 10, n4 + 48, 296, 20)) {
            if (!BrutalityCloudClient.checkState()) {
                this.handleString3("My Uploads", "Sign in to see your uploads");
                return true;
            }
            this.flag = !this.flag;
            this.process3();
            return true;
        }
        int n6 = n4 + 92;

        for (BrutalityCloudClient.PopularMode popularMode : BrutalityCloudClient.PopularMode.values()) {
            if (matchesInt(n, n2, n3 + 10, n6, 296, 20)) {
                if (this.popularMode != popularMode) {
                    this.popularMode = popularMode;
                    this.process3();
                }

                this.flag4 = false;
                return true;
            }

            n6 += 24;
        }

        return matchesInt(n, n2, n3, n4, 320, n5);
    }

    private void handleInt6(int n, int n2) {
        int n3 = this.count9 + 28;
        if (matchesInt(n, n2, this.count8 + 8, n3, 50, 18)) {
            this.browseMode = GuiCloudBrowser.BrowseMode.BROWSE;
            this.entry3 = null;
            this.flag2 = false;
        } else {
            int n4 = n3 + 80;
            if (matchesInt(n, n2, this.count8 + 8, n4, 90, 20) && this.entry3 != null) {
                this.handleEntry32(this.entry3);
            } else if (matchesInt(n, n2, this.count8 + 104, n4, 70, 20) && this.entry3 != null) {
                this.handleEntry3(this.entry3);
            } else {
                if (this.entry3 != null && BrutalityCloudClient.matchesEntry3(this.entry3)) {
                    if (matchesInt(n, n2, this.count8 + 180, n4, 56, 20)) {
                        this.openEdit(this.entry3);
                        return;
                    }

                    if (matchesInt(n, n2, this.count8 + 242, n4, 56, 20)) {
                        this.handleEntry34(this.entry3);
                        return;
                    }
                }

                int n5 = this.count8 + 40;

                for (int i = 1; i <= 5; i++) {
                    if (matchesInt(n, n2, n5 + (i - 1) * 14, n4 + 26, 12, 16)) {
                        this.count6 = i;
                        return;
                    }
                }

                if (!matchesInt(n, n2, this.count8 + 120, n4 + 24, 60, 20) || this.entry3 == null) {
                    this.guiTextField.mouseClicked(n, n2, 0);
                } else if (this.count6 < 1) {
                    this.handleString3("Review", "Pick a star rating first");
                } else {
                    int n6 = this.count6;
                    String text = this.guiTextField.getText();
                    String str = BrutalityCloudClient.b() != null ? BrutalityCloudClient.b() : "You";
                    BrutalityCloudClient.rateAsync(this.entry3.id, n6, text, entry -> mc.addScheduledTask(() -> {
                        if (entry.ok) {
                            this.handleString2("Review Submitted", n6 + "/5 from " + str);
                            this.entries2.add(0, new BrutalityCloudClient.Entry4(str, n6, text == null ? "" : text, System.currentTimeMillis()));
                            BrutalityCloudClient.reviewsAsync(this.entry3.id, entry4s -> mc.addScheduledTask(() -> {
                                this.entries2.clear();
                                this.entries2.addAll(entry4s);
                            }));
                            this.process3();
                        } else {
                            this.handleString3("Review Failed", entry.error != null ? entry.error : "Could not submit");
                        }
                    }));
                }
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void handleString3(String str, String str2) {
        mc.addScheduledTask(() -> {
            ModuleEnabledHandler.handleString2(str, str2, 3500L);
            this.handleString("mob.villager.no", 0.6F, 1.0F);
        });
    }

    private void process() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.process2();
        float f = this.flag4 ? 1.0F : 0.0F;
        this.factor = this.factor + (f - this.factor) * 0.25F;
        if (Math.abs(f - this.factor) < 0.01F) {
            this.factor = f;
        }

        this.handleInt3(mouseX, mouseY);
        AvatarComponent.handleFloat(this.count8, this.count9, this.count10, this.count11, 8.0F);
        AvatarComponent.handleInt(this.count8, this.count9, this.count10, this.count11, 1.0F);
        GlStateManager.enableTexture2D();
        this.inter18ptSemiboldTtfComponent
            .handleString3(
                "Brutality Cloud",
                this.count8 + (this.count10 - this.inter18ptSemiboldTtfComponent.computeInt("Brutality Cloud")) / 2,
                this.count9 + 6,
                16777215
            );
        String str = BrutalityCloudClient.computeString();
        String str2 = BrutalityCloudClient.checkState() && str != null ? "Signed in as " + str : "Not signed in";
        this.inter18ptSemiboldTtfComponent.handleString2(str2, this.count8 + 8, this.count9 + 10, 11184810);
        this.handleInt(
            this.count8 + this.count10 - 52, this.count9 + 4, 44, 16, "Done", matchesInt(mouseX, mouseY, this.count8 + this.count10 - 52, this.count9 + 4, 44, 16)
        );
        if (this.browseMode == GuiCloudBrowser.BrowseMode.BROWSE) {
            this.handleInt10(mouseX, mouseY);
        } else if (this.browseMode == GuiCloudBrowser.BrowseMode.DETAIL) {
            this.handleInt8(mouseX, mouseY);
        } else if (this.browseMode == GuiCloudBrowser.BrowseMode.EDIT) {
            this.handleInt2(mouseX, mouseY);
        } else {
            this.handleInt(mouseX, mouseY);
        }

        if (this.factor > 0.01F) {
            this.handleInt5(mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void handleString2(String str, String str2) {
        mc.addScheduledTask(() -> {
            ModuleEnabledHandler.handleString(str, str2, 3500L);
            this.handleString("random.levelup", 0.55F, 1.15F);
        });
    }

    private void handleString(String str, float f, float f2) {
        if (mc.thePlayer != null) {
            mc.thePlayer.playSound(str, f, f2);
        }
    }

    private void handleInt4(int n, int n2) {
        int n3 = this.count9 + 28;
        this.searchField.mouseClicked(n, n2, 0);
        if (matchesInt(n, n2, this.count8 + this.count10 - 186, n3, 54, 18)) {
            this.flag4 = !this.flag4;
        } else if (matchesInt(n, n2, this.count8 + this.count10 - 126, n3, 54, 18)) {
            this.openUpload();
        } else if (matchesInt(n, n2, this.count8 + this.count10 - 66, n3, 58, 18)) {
            this.process3();
        } else {
            this.count12 = n3 + 34;
            this.count13 = this.count9 + this.count11 - 8;
            int n4 = this.count12 - this.count3;

            for (BrutalityCloudClient.Entry3 entry3 : this.entries) {
                if (n4 + 36 >= this.count12 && n4 <= this.count13 && matchesInt(n, n2, this.count8 + 8, n4, this.count10 - 16, 32)) {
                    this.handleEntry33(entry3);
                    return;
                }

                n4 += 36;
            }
        }
    }

    private void handleEntry33(BrutalityCloudClient.Entry3 entry3) {
        this.entry3 = entry3;
        this.browseMode = GuiCloudBrowser.BrowseMode.DETAIL;
        this.flag4 = false;
        this.flag2 = false;
        this.entries2.clear();
        this.count6 = 0;
        this.count5 = 0;
        if (this.guiTextField != null) {
            this.guiTextField.setText("");
        }

        BrutalityCloudClient.reviewsAsync(entry3.id, entry4s -> mc.addScheduledTask(() -> {
            this.entries2.clear();
            this.entries2.addAll(entry4s);
        }));
    }

    private void handleInt3(int n, int n2) {
        byte b = 26;
        String str = "CONFIG";
        String str2 = "SCRIPT";
        int n3 = 14;
        int n4 = this.inter18ptSemiboldTtfComponent.computeInt2(str);
        int n5 = this.inter18ptSemiboldTtfComponent.computeInt2(str2);
        int n6 = this.inter18ptSemiboldTtfComponent.computeInt2(" | ");
        int n7 = n4 + n3 + n6 + n3 + n5;
        int n8 = (this.width - n7) / 2;
        boolean flag = matchesInt(n, n2, n8 - 4, b - 4, n4 + 8, 18);
        boolean flag2 = matchesInt(n, n2, n8 + n4 + n3 + n6 + n3 - 4, b - 4, n5 + 8, 18);
        int n9 = this.configMode == BrutalityCloudClient.ConfigMode.CONFIG ? computeInt() : (flag ? -86 : -5592406);
        int n10 = this.configMode == BrutalityCloudClient.ConfigMode.SCRIPT ? computeInt() : (flag2 ? -86 : -5592406);
        if (this.configMode == BrutalityCloudClient.ConfigMode.CONFIG) {
            drawRect(n8 - 2, b + 14, n8 + n4 + 2, b + 15, computeInt());
        } else {
            int n11 = n8 + n4 + n3 + n6 + n3;
            drawRect(n11 - 2, b + 14, n11 + n5 + 2, b + 15, computeInt());
        }

        this.inter18ptSemiboldTtfComponent.handleString(str, n8, b, n9);
        this.inter18ptSemiboldTtfComponent.handleString(" | ", n8 + n4 + n3, b, -10066330);
        this.inter18ptSemiboldTtfComponent.handleString(str2, n8 + n4 + n3 + n6 + n3, b, n10);
    }

    private void handleInt(int n, int n2, int n3, int n4, String str, boolean flag) {
        OutlineThicknessHelper.handleFloat(n, n2, n3, n4, 3.0F, new Color(30, 31, 38, flag ? 200 : 150));
        OutlineThicknessHelper.handleFloat(
            n,
            n2,
            n3,
            n4,
            3.0F,
            0.5F,
            new Color(0, 0, 0, 0),
            flag ? MainMenuIconRenderer.computeColor(OceanComponent.computeColor(), 190) : new Color(255, 255, 255, 26)
        );
        GlStateManager.enableTexture2D();
        String str2 = this.inter18ptSemiboldTtfComponent.computeString(str, n3 - 8);
        int n5 = this.inter18ptSemiboldTtfComponent.computeInt3(str2);
        this.inter18ptSemiboldTtfComponent.handleString2(str2, n + (n3 - n5) / 2, n2 + (n4 - 8) / 2, flag ? 16777215 : 14737632);
    }

    public GuiCloudBrowser(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int eventDWheel = Mouse.getEventDWheel();
        if (eventDWheel != 0 && this.browseMode == GuiCloudBrowser.BrowseMode.BROWSE && !this.flag4) {
            this.count3 = Math.max(0, Math.min(this.count4, this.count3 - Integer.signum(eventDWheel) * 24));
        }

        if (this.browseMode == GuiCloudBrowser.BrowseMode.DETAIL && this.entry3 != null) {
            this.process2();
            int n = this.count9 + 28 + 80;
            int n2 = this.count8 + 40;
            int n3 = Mouse.getEventX() * this.width / mc.displayWidth;
            int n4 = this.height - Mouse.getEventY() * this.height / mc.displayHeight - 1;
            this.count5 = 0;

            for (int i = 1; i <= 5; i++) {
                if (matchesInt(n3, n4, n2 + (i - 1) * 14, n + 26, 12, 16)) {
                    this.count5 = i;
                    break;
                }
            }
        }
    }

    private boolean matchesInt2(int n, int n2) {
        byte b = 26;
        String str = "CONFIG";
        String str2 = "SCRIPT";
        int n3 = 14;
        int n4 = this.inter18ptSemiboldTtfComponent.computeInt2(str);
        int n5 = this.inter18ptSemiboldTtfComponent.computeInt2(str2);
        int n6 = this.inter18ptSemiboldTtfComponent.computeInt2(" | ");
        int n7 = n4 + n3 + n6 + n3 + n5;
        int n8 = (this.width - n7) / 2;
        if (matchesInt(n, n2, n8 - 4, b - 4, n4 + 8, 18) && this.configMode != BrutalityCloudClient.ConfigMode.CONFIG) {
            this.configMode = BrutalityCloudClient.ConfigMode.CONFIG;
            if (this.browseMode != GuiCloudBrowser.BrowseMode.BROWSE) {
                this.browseMode = GuiCloudBrowser.BrowseMode.BROWSE;
            }

            this.process3();
            return true;
        }
        if (matchesInt(n, n2, n8 + n4 + n3 + n6 + n3 - 4, b - 4, n5 + 8, 18)
            && this.configMode != BrutalityCloudClient.ConfigMode.SCRIPT) {
            this.configMode = BrutalityCloudClient.ConfigMode.SCRIPT;
            if (this.browseMode != GuiCloudBrowser.BrowseMode.BROWSE) {
                this.browseMode = GuiCloudBrowser.BrowseMode.BROWSE;
            }

            this.process3();
            return true;
        }
        return false;
    }

    private void handleString(String str, int n, int n2, int n3, int n4) {
        String[] parts = str.split(" ");
        StringBuilder sb = new StringBuilder();
        int n5 = n2;

        for (String str2 : parts) {
            String str3 = sb.length() == 0 ? str2 : sb + " " + str2;
            if (this.inter18ptSemiboldTtfComponent.computeInt3(str3) > n3 && sb.length() > 0) {
                this.inter18ptSemiboldTtfComponent.handleString2(sb.toString(), n, n5, n4);
                n5 += 11;
                sb = new StringBuilder(str2);
            } else {
                sb = new StringBuilder(str3);
            }
        }

        if (sb.length() > 0) {
            this.inter18ptSemiboldTtfComponent.handleString2(sb.toString(), n, n5, n4);
        }
    }

    private static int computeInt() {
        return 0xFF000000 | OceanComponent.computeColor().getRGB() & 16777215;
    }

    @Override
    public void updateScreen() {
        if (this.searchField != null) {
            this.searchField.updateCursorCounter();
        }

        if (this.guiTextField != null) {
            this.guiTextField.updateCursorCounter();
        }

        if (this.nameField != null) {
            this.nameField.updateCursorCounter();
        }

        if (this.guiTextField2 != null) {
            this.guiTextField2.updateCursorCounter();
        }
    }

    private static boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6) {
        return n >= n3 && n2 >= n4 && n < n3 + n5 && n2 < n4 + n6;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton != 0) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } else {
            this.process2();
            if (this.flag4 && this.factor > 0.5F) {
                if (this.matchesInt(mouseX, mouseY)) {
                    return;
                }

                int n = this.width - 320;
                if (mouseX < n) {
                    this.flag4 = false;
                    return;
                }
            }

            if (!this.matchesInt2(mouseX, mouseY)) {
                if (matchesInt(mouseX, mouseY, this.count8 + this.count10 - 52, this.count9 + 4, 44, 16)) {
                    mc.displayGuiScreen(this.guiScreen);
                } else {
                    if (this.browseMode == GuiCloudBrowser.BrowseMode.BROWSE) {
                        this.handleInt4(mouseX, mouseY);
                    } else if (this.browseMode == GuiCloudBrowser.BrowseMode.DETAIL) {
                        this.handleInt6(mouseX, mouseY);
                    } else if (this.browseMode == GuiCloudBrowser.BrowseMode.EDIT) {
                        this.handleInt9(mouseX, mouseY);
                    } else {
                        this.handleInt7(mouseX, mouseY);
                    }
                }
            }
        }
    }

    private void handleString(String str, String str2) {
        mc.addScheduledTask(() -> {
            ModuleEnabledHandler.handleString3(str, str2, 3000L);
            this.handleString("random.orb", 0.45F, 1.3F);
        });
    }

    private void handleInt8(int n, int n2) {
        if (this.entry3 == null) {
            this.browseMode = GuiCloudBrowser.BrowseMode.BROWSE;
        } else {
            boolean flag = BrutalityCloudClient.matchesEntry3(this.entry3);
            int n3 = this.count9 + 28;
            this.handleInt(this.count8 + 8, n3, 50, 18, "Back", matchesInt(n, n2, this.count8 + 8, n3, 50, 18));
            this.inter18ptSemiboldTtfComponent.handleString(this.entry3.name, this.count8 + 66, n3 + 3, 16777215);
            String str = this.entry3.computeString();
            if (str != null && !str.isEmpty()) {
                this.inter18ptSemiboldTtfComponent
                    .handleString2(str, this.count8 + 66 + this.inter18ptSemiboldTtfComponent.computeInt2(this.entry3.name) + 6, n3 + 5, -9539986);
            }

            Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent = this.inter18ptSemiboldTtfComponent;
            String author = this.entry3.author;
            String str2 = String.format("%.1f* (%d)", this.entry3.avgRating, this.entry3.ratingCount);
            String str3 = flag ? "  ·  yours" : "";
            int downloads = this.entry3.downloads;
            String str4 = str2;
            String str5 = author;
            inter18ptSemiboldTtfComponent.handleString2("by " + str5 + "  ·  " + str4 + "  ·  " + downloads + " downloads" + str3, this.count8 + 8, n3 + 24, 8947848);
            String str6 = this.entry3.description != null && !this.entry3.description.isEmpty() ? this.entry3.description : "No description.";
            this.handleString(str6, this.count8 + 8, n3 + 40, this.count10 - 16, 13421772);
            int n4 = n3 + 80;
            this.handleInt(this.count8 + 8, n4, 90, 20, "Download", matchesInt(n, n2, this.count8 + 8, n4, 90, 20));
            this.handleInt(this.count8 + 104, n4, 70, 20, "Copy ID", matchesInt(n, n2, this.count8 + 104, n4, 70, 20));
            if (flag) {
                this.handleInt(this.count8 + 180, n4, 56, 20, "Edit", matchesInt(n, n2, this.count8 + 180, n4, 56, 20));
                String str7 = this.flag2 ? "Sure?" : "Delete";
                this.handleInt(this.count8 + 242, n4, 56, 20, str7, matchesInt(n, n2, this.count8 + 242, n4, 56, 20));
            }

            this.inter18ptSemiboldTtfComponent.handleString2("Rate:", this.count8 + 8, n4 + 28, 11184810);
            int n5 = this.count8 + 40;

            for (int i = 1; i <= 5; i++) {
                boolean flag2 = i <= (this.count5 > 0 ? this.count5 : this.count6);
                boolean flag3 = matchesInt(n, n2, n5 + (i - 1) * 14, n4 + 26, 12, 16);
                this.inter18ptSemiboldTtfComponent.handleString(flag2 ? "*" : "-", n5 + (i - 1) * 14, n4 + 27, !flag2 && !flag3 ? -11184811 : -2840008);
            }

            this.handleInt(this.count8 + 120, n4 + 24, 60, 20, "Submit", matchesInt(n, n2, this.count8 + 120, n4 + 24, 60, 20));
            this.guiTextField.xPosition = this.count8 + 8;
            this.guiTextField.yPosition = n4 + 50;
            this.guiTextField.drawTextBox();
            this.inter18ptSemiboldTtfComponent.handleString2("Reviews", this.count8 + 8, n4 + 74, 11184810);
            int n6 = n4 + 88;
            if (this.entries2.isEmpty()) {
                this.inter18ptSemiboldTtfComponent.handleString2("No reviews yet.", this.count8 + 8, n6, 6710886);
            } else {
                for (BrutalityCloudClient.Entry4 entry4 : this.entries2) {
                    if (n6 > this.count9 + this.count11 - 12) {
                        break;
                    }

                    String str8 = entry4.comment != null && !entry4.comment.isEmpty() ? " — " + entry4.comment : "";
                    String author2 = entry4.author;
                    String str9 = entry4.stars + "*  " + author2 + str8;
                    this.inter18ptSemiboldTtfComponent
                        .handleString2(this.inter18ptSemiboldTtfComponent.computeString(str9, this.count10 - 20), this.count8 + 8, n6, 14540253);
                    n6 += 12;
                }
            }
        }
    }

    private void handleInt5(int n, int n2) {
        short s = 320;
        int n3 = this.width - (int)(s * this.factor);
        byte b = 60;
        int n4 = this.height - 80;
        drawRect(0, 0, n3, this.height, (int)(102.0F * this.factor) << 24);
        AvatarComponent.handleFloat(n3, b, this.width - n3 + 8, n4, 8.0F);
        AvatarComponent.handleInt(n3, b, this.width - n3 + 8, n4, this.factor);
        GlStateManager.enableTexture2D();
        this.inter18ptSemiboldTtfComponent.handleString("Filter", n3 + 12, b + 12, 16777215);
        this.inter18ptSemiboldTtfComponent.handleString2("Show", n3 + 12, b + 34, 11184810);
        boolean flag = matchesInt(n, n2, n3 + 10, b + 48, s - 24, 20);
        drawRect(n3 + 10, b + 48, n3 + s - 14, b + 68, this.flag ? computeInt(70) : (flag ? 687865855 : 318767103));
        this.inter18ptSemiboldTtfComponent
            .handleString2((this.flag ? "[x] " : "[ ] ") + "My Uploads", n3 + 16, b + 53, this.flag ? computeInt() : -2236963);
        this.inter18ptSemiboldTtfComponent.handleString2("Sort by", n3 + 12, b + 78, 11184810);
        int n5 = b + 92;

        for (BrutalityCloudClient.PopularMode popularMode : BrutalityCloudClient.PopularMode.values()) {
            boolean flag2 = this.popularMode == popularMode;
            boolean flag3 = matchesInt(n, n2, n3 + 10, n5, s - 24, 20);
            drawRect(n3 + 10, n5, n3 + s - 14, n5 + 20, flag2 ? computeInt(70) : (flag3 ? 687865855 : 318767103));
            String str = flag2 ? "> " : "  ";
            this.inter18ptSemiboldTtfComponent.handleString2(str + popularMode.label, n3 + 16, n5 + 5, flag2 ? computeInt() : -2236963);
            n5 += 24;
        }

        this.handleInt(n3 + 12, b + n4 - 28, 80, 18, "Close", matchesInt(n, n2, n3 + 12, b + n4 - 28, 80, 18));
    }

    private void process3() {
        this.loading = true;
        this.text = "Loading…";
        this.entries.clear();
        this.count3 = 0;
        BrutalityCloudClient.listAsync(
            this.configMode,
            this.text2,
            this.popularMode,
            this.flag,
            entry5 -> mc.addScheduledTask(
                () -> {
                    this.loading = false;
                    if (!entry5.ok) {
                        this.text = entry5.error != null ? entry5.error : "Failed to load";
                        if (this.text.toLowerCase().contains("http")
                            || this.text.toLowerCase().contains("network")
                            || this.text.contains("404")
                            || this.text.contains("502")) {
                            this.text = "Cloud offline — backend not ready yet";
                        }

                        this.entries.clear();
                    } else {
                        this.entries.clear();
                        this.entries.addAll(entry5.entries);
                        String str = this.flag ? "your uploads · " : "";
                        this.text = this.entries.isEmpty()
                            ? (this.flag ? "No uploads from you yet" : "No results")
                            : str + this.entries.size() + " result" + (this.entries.size() == 1 ? "" : "s");
                    }
                }
            )
        );
    }

    private void openEdit(BrutalityCloudClient.Entry3 entry3) {
        this.entry3 = entry3;
        this.browseMode = GuiCloudBrowser.BrowseMode.EDIT;
        this.flag4 = false;
        this.flag2 = false;
        this.flag3 = false;
        this.text3 = "";
        this.entries3.clear();
        this.count7 = 0;
        if (entry3.kind == BrutalityCloudClient.ConfigMode.CONFIG) {
            File file = Brutality.appdataHandler != null ? Brutality.appdataHandler.computeFile() : new File("Brutality/Configs");
            File[] files = file.listFiles((file2, str) -> str.endsWith(".json"));
            if (files != null) {
                for (File file3 : files) {
                    this.entries3.add(file3.getName());
                }
            }
        } else if (Brutality.scriptManager != null) {
            this.entries3.addAll(Brutality.scriptManager.getAvailableScriptFiles());
        }

        if (this.nameField != null) {
            this.nameField.setText(entry3.name != null ? entry3.name : "");
        }

        if (this.guiTextField2 != null) {
            this.guiTextField2.setText(entry3.description != null ? entry3.description : "");
        }
    }

    private void handleInt(int n, int n2, int n3, int n4) {
        ScaledResolution sr = new ScaledResolution(mc);
        int scaleFactor = sr.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(n * scaleFactor, mc.displayHeight - (n2 + n4) * scaleFactor, Math.max(0, n3 * scaleFactor), Math.max(0, n4 * scaleFactor));
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.searchField = new SearchField(1, 0, 0, 280, 18);
        this.searchField.setMaxStringLength(64);
        this.searchField.setText(this.text2);
        this.searchField.setEnableBackgroundDrawing(true);
        this.guiTextField = new SearchField(2, 0, 0, 400, 18);
        this.guiTextField.setMaxStringLength(200);
        this.guiTextField.setEnableBackgroundDrawing(true);
        this.nameField = new SearchField(3, 0, 0, 220, 18);
        this.nameField.setMaxStringLength(64);
        this.nameField.setEnableBackgroundDrawing(true);
        this.guiTextField2 = new SearchField(4, 0, 0, 220, 18);
        this.guiTextField2.setMaxStringLength(160);
        this.guiTextField2.setEnableBackgroundDrawing(true);
        this.process3();
    }

    private void handleConfigMode(BrutalityCloudClient.ConfigMode configMode, String str, boolean flag) {
        if (configMode == BrutalityCloudClient.ConfigMode.CONFIG) {
            ModuleEnabledHandler.handleString("Cloud Download", "Saved " + str + ".json to configs", 3200L);
            this.handleString("random.levelup", 0.55F, 1.1F);
            if (flag) {
                ModuleEnabledHandler.handleString("Config Loaded", "Auto-loaded " + str, 3200L);
                this.handleString("note.pling", 0.7F, 1.4F);
            } else {
                ModuleEnabledHandler.handleString5("Config Saved", "Saved but failed to auto-load " + str, 3500L);
                this.handleString("note.bass", 0.5F, 0.8F);
            }
        } else {
            ModuleEnabledHandler.handleString("Cloud Download", "Saved " + str + ".java to scripts", 3200L);
            this.handleString("random.levelup", 0.55F, 1.1F);
            if (flag) {
                ModuleEnabledHandler.handleString("Script Loaded", "Auto-loaded " + str, 3200L);
                this.handleString("note.pling", 0.7F, 1.4F);
            } else {
                ModuleEnabledHandler.handleString5("Script Saved", "Saved but failed to auto-load " + str, 3500L);
                this.handleString("note.bass", 0.5F, 0.8F);
            }
        }
    }

    private void handleInt(int n, int n2) {
        int n3 = this.count9 + 28;
        this.handleInt(this.count8 + 8, n3, 50, 18, "Back", matchesInt(n, n2, this.count8 + 8, n3, 50, 18));
        this.inter18ptSemiboldTtfComponent
            .handleString("Upload " + (this.configMode == BrutalityCloudClient.ConfigMode.CONFIG ? "Config" : "Script"), this.count8 + 66, n3 + 3, 16777215);
        if (!BrutalityCloudClient.checkState()) {
            String str = "You must be signed in to upload.";
            this.inter18ptSemiboldTtfComponent
                .handleString(str, this.width / 2 - this.inter18ptSemiboldTtfComponent.computeInt2(str) / 2, n3 + 50, computeInt());
        } else {
            this.inter18ptSemiboldTtfComponent.handleString2("File:", this.count8 + 8, n3 + 36, 11184810);
            String str2 = this.entries3.isEmpty() ? "(none found)" : this.entries3.get(Math.max(0, Math.min(this.count7, this.entries3.size() - 1)));
            this.handleInt(this.count8 + 40, n3 + 32, 200, 18, str2, matchesInt(n, n2, this.count8 + 40, n3 + 32, 200, 18));
            this.handleInt(this.count8 + 246, n3 + 32, 24, 18, "<", matchesInt(n, n2, this.count8 + 246, n3 + 32, 24, 18));
            this.handleInt(this.count8 + 274, n3 + 32, 24, 18, ">", matchesInt(n, n2, this.count8 + 274, n3 + 32, 24, 18));
            this.inter18ptSemiboldTtfComponent.handleString2("Name:", this.count8 + 8, n3 + 64, 11184810);
            this.nameField.xPosition = this.count8 + 50;
            this.nameField.yPosition = n3 + 60;
            this.nameField.drawTextBox();
            this.inter18ptSemiboldTtfComponent.handleString2("Desc:", this.count8 + 8, n3 + 92, 11184810);
            this.guiTextField2.xPosition = this.count8 + 50;
            this.guiTextField2.yPosition = n3 + 88;
            this.guiTextField2.drawTextBox();
            this.handleInt(this.count8 + 8, n3 + 120, 90, 20, "Publish", matchesInt(n, n2, this.count8 + 8, n3 + 120, 90, 20));
            if (this.text3 != null && !this.text3.isEmpty()) {
                this.inter18ptSemiboldTtfComponent.handleString2(this.text3, this.count8 + 110, n3 + 126, 11184810);
            }
        }
    }

    private void handleInt7(int n, int n2) {
        int n3 = this.count9 + 28;
        if (matchesInt(n, n2, this.count8 + 8, n3, 50, 18)) {
            this.browseMode = GuiCloudBrowser.BrowseMode.BROWSE;
        } else if (BrutalityCloudClient.checkState()) {
            if (matchesInt(n, n2, this.count8 + 246, n3 + 32, 24, 18) && !this.entries3.isEmpty()) {
                this.count7 = (this.count7 - 1 + this.entries3.size()) % this.entries3.size();
            } else if (matchesInt(n, n2, this.count8 + 274, n3 + 32, 24, 18) && !this.entries3.isEmpty()) {
                this.count7 = (this.count7 + 1) % this.entries3.size();
            } else {
                this.nameField.mouseClicked(n, n2, 0);
                this.guiTextField2.mouseClicked(n, n2, 0);
                if (matchesInt(n, n2, this.count8 + 8, n3 + 120, 90, 20)) {
                    if (this.entries3.isEmpty()) {
                        this.text3 = "No local files to upload";
                        this.handleString3("Upload", "No local files to upload");
                        return;
                    }

                    String str = this.nameField.getText().trim();
                    if (str.isEmpty()) {
                        this.text3 = "Enter a display name";
                        this.handleString3("Upload", "Enter a display name");
                        return;
                    }

                    String str2 = this.entries3.get(this.count7);
                    File file = this.configMode == BrutalityCloudClient.ConfigMode.CONFIG
                        ? new File(Brutality.appdataHandler.computeFile(), str2)
                        : new File("Brutality/Scripts", str2);
                    this.text3 = "Uploading…";
                    String str3 = this.configMode == BrutalityCloudClient.ConfigMode.CONFIG ? "Config" : "Script";
                    this.handleString("Cloud Upload", "Publishing " + str3.toLowerCase() + " " + str + "…");
                    BrutalityCloudClient.uploadAsync(this.configMode, str, this.guiTextField2.getText().trim(), file, entry -> mc.addScheduledTask(() -> {
                        if (entry.ok) {
                            String str4 = entry.message != null ? entry.message : "";
                            this.text3 = str4.isEmpty() ? "Published!" : "Published · " + str4;
                            this.handleString2("Uploaded", str + (str4.isEmpty() ? " " + str3 + " is on Cloud" : " · " + str4));
                            if (!str4.isEmpty()) {
                                GuiScreen.setClipboardString(str4);
                                String str5 = this.configMode == BrutalityCloudClient.ConfigMode.CONFIG ? ".config load " : ".script load ";
                                this.handleString("Cloud ID", str4 + " copied — " + str5 + str4);
                                this.handleString("note.pling", 0.65F, 1.35F);
                            }

                            this.browseMode = GuiCloudBrowser.BrowseMode.BROWSE;
                            this.process3();
                        } else {
                            this.text3 = "Failed: " + entry.error;
                            this.handleString3("Upload Failed", entry.error != null ? entry.error : "Unknown error");
                        }
                    }));
                }
            }
        }
    }

    private void handleInt10(int n, int n2) {
        int n3 = this.count9 + 28;
        this.searchField.xPosition = this.count8 + 8;
        this.searchField.yPosition = n3;
        this.searchField.drawTextBox();
        this.handleInt(
            this.count8 + this.count10 - 186, n3, 54, 18, "Filter", matchesInt(n, n2, this.count8 + this.count10 - 186, n3, 54, 18) || this.flag4
        );
        this.handleInt(this.count8 + this.count10 - 126, n3, 54, 18, "Upload", matchesInt(n, n2, this.count8 + this.count10 - 126, n3, 54, 18));
        this.handleInt(this.count8 + this.count10 - 66, n3, 58, 18, "Refresh", matchesInt(n, n2, this.count8 + this.count10 - 66, n3, 58, 18));
        this.inter18ptSemiboldTtfComponent
            .handleString2(this.text + (this.flag ? "  ·  My Uploads" : "") + "  ·  sorted by " + this.popularMode.label, this.count8 + 8, n3 + 22, 8947848);
        this.count12 = n3 + 34;
        this.count13 = this.count9 + this.count11 - 8;
        int n4 = this.count13 - this.count12;
        int n5 = this.entries.size() * 36;
        this.count4 = Math.max(0, n5 - n4);
        this.count3 = Math.max(0, Math.min(this.count3, this.count4));
        this.handleInt(this.count8 + 4, this.count12, this.count10 - 8, n4);
        int n6 = this.count12 - this.count3;

        for (BrutalityCloudClient.Entry3 entry3 : this.entries) {
            if (n6 + 36 >= this.count12 && n6 <= this.count13) {
                boolean flag = matchesInt(n, n2, this.count8 + 8, n6, this.count10 - 16, 32);
                OutlineThicknessHelper.handleFloat(this.count8 + 8, n6, this.count10 - 16, 32.0F, 4.0F, new Color(255, 255, 255, flag ? 26 : 12));
                OutlineThicknessHelper.handleFloat(
                    this.count8 + 8, n6 + 4, 2.0F, 24.0F, 1.0F, flag ? OceanComponent.computeColor() : new Color(255, 255, 255, 40)
                );
                GlStateManager.enableTexture2D();
                this.inter18ptSemiboldTtfComponent.handleString(entry3.name, this.count8 + 16, n6 + 4, 16777215);
                String str = entry3.computeString();
                if (str != null && !str.isEmpty()) {
                    int n7 = this.count8 + 16 + this.inter18ptSemiboldTtfComponent.computeInt2(entry3.name) + 6;
                    int n8 = this.count8 + this.count10 - 90 - n7;
                    if (n8 > 20) {
                        this.inter18ptSemiboldTtfComponent
                            .handleString2(this.inter18ptSemiboldTtfComponent.computeString(str, n8), n7, n6 + 6, -9539986);
                    }
                }

                this.inter18ptSemiboldTtfComponent
                    .handleString2("by " + (entry3.author != null && !entry3.author.isEmpty() ? entry3.author : "?"), this.count8 + 16, n6 + 18, 8947848);
                String str2 = String.format("%.1f*  %d dl", entry3.avgRating, entry3.downloads);
                int n9 = this.inter18ptSemiboldTtfComponent.computeInt3(str2);
                this.inter18ptSemiboldTtfComponent.handleString2(str2, this.count8 + this.count10 - 16 - n9, n6 + 10, -2840008);
                if (flag) {
                    String str3 = "Click to open";
                    this.inter18ptSemiboldTtfComponent
                        .handleString2(str3, this.count8 + this.count10 - 16 - this.inter18ptSemiboldTtfComponent.computeInt3(str3), n6 + 20, -11141291);
                }
            }

            n6 += 36;
        }

        this.process();
        if (this.loading) {
            this.inter18ptSemiboldTtfComponent
                .handleString("Loading…", this.width / 2 - this.inter18ptSemiboldTtfComponent.computeInt2("Loading…") / 2, this.count12 + n4 / 2, 11184810);
        } else if (this.entries.isEmpty()) {
            this.inter18ptSemiboldTtfComponent
                .handleString(this.text, this.width / 2 - this.inter18ptSemiboldTtfComponent.computeInt2(this.text) / 2, this.count12 + 24, 11184810);
            String str4 = "Configs & scripts will show here when the backend is up.";
            this.inter18ptSemiboldTtfComponent
                .handleString2(str4, this.width / 2 - this.inter18ptSemiboldTtfComponent.computeInt3(str4) / 2, this.count12 + 42, 6710886);
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    private void handleEntry32(BrutalityCloudClient.Entry3 entry3) {
        String str = BrutalityCloudClient.computeString2(entry3.computeString());
        File file;
        String str2;
        if (entry3.kind == BrutalityCloudClient.ConfigMode.CONFIG) {
            File file2 = Brutality.appdataHandler != null ? Brutality.appdataHandler.computeFile() : new File("Brutality/Configs");
            file = new File(file2, str + ".json");
            str2 = str;
        } else {
            File file3 = Brutality.scriptManager != null ? new File(Brutality.scriptManager.text) : new File("Brutality/Scripts");
            if (!file3.exists()) {
                file3.mkdirs();
            }

            file = new File(file3, str + ".java");
            str2 = str;
        }

        this.text = "Downloading…";
        this.handleString("Cloud", "Downloading " + entry3.name + " [" + entry3.computeString() + "]…");
        File file4 = file;
        String str3 = str2;
        BrutalityCloudClient.handleEntry3(entry3, file4, entry -> mc.addScheduledTask(() -> {
            if (!entry.ok) {
                this.handleString3("Download Failed", entry.error != null ? entry.error : "Unknown error");
                this.text = "Download failed";
            } else {
                if (entry3.kind == BrutalityCloudClient.ConfigMode.CONFIG) {
                    boolean flag = false;
                    if (Brutality.appdataHandler != null) {
                        flag = Brutality.appdataHandler.matchesString(str3);
                    }

                    this.handleConfigMode(BrutalityCloudClient.ConfigMode.CONFIG, str3, flag);
                    this.text = flag ? "Loaded " + str3 : "Saved " + str3;
                } else {
                    boolean flag2 = false;
                    if (Brutality.scriptManager != null) {
                        try {
                            flag2 = Brutality.scriptManager.matchesFile2(file4);
                        } catch (Exception e) {
                            flag2 = false;
                        }
                    }

                    this.handleConfigMode(BrutalityCloudClient.ConfigMode.SCRIPT, str3, flag2);
                    this.text = flag2 ? "Loaded " + str3 : "Saved " + str3;
                }
            }
        }));
    }

    private void process2() {
        this.count10 = Math.min(520, this.width - 40);
        this.count11 = Math.min(280, this.height - 90);
        this.count8 = (this.width - this.count10) / 2;
        this.count9 = 70;
        this.count12 = this.count9 + 52;
        this.count13 = this.count9 + this.count11 - 10;
    }

    private void handleInt2(int n, int n2) {
        if (this.entry3 == null) {
            this.browseMode = GuiCloudBrowser.BrowseMode.BROWSE;
        } else {
            int n3 = this.count9 + 28;
            this.handleInt(this.count8 + 8, n3, 50, 18, "Back", matchesInt(n, n2, this.count8 + 8, n3, 50, 18));
            this.inter18ptSemiboldTtfComponent
                .handleString(
                    "Edit " + (this.entry3.kind == BrutalityCloudClient.ConfigMode.CONFIG ? "Config" : "Script"), this.count8 + 66, n3 + 3, 16777215
                );
            this.inter18ptSemiboldTtfComponent.handleString2("Name:", this.count8 + 8, n3 + 36, 11184810);
            this.nameField.xPosition = this.count8 + 50;
            this.nameField.yPosition = n3 + 32;
            this.nameField.drawTextBox();
            this.inter18ptSemiboldTtfComponent.handleString2("Desc:", this.count8 + 8, n3 + 64, 11184810);
            this.guiTextField2.xPosition = this.count8 + 50;
            this.guiTextField2.yPosition = n3 + 60;
            this.guiTextField2.drawTextBox();
            this.inter18ptSemiboldTtfComponent.handleString2("Replace file (optional):", this.count8 + 8, n3 + 92, 11184810);
            boolean flag = matchesInt(n, n2, this.count8 + 8, n3 + 108, 120, 18);
            this.handleInt(this.count8 + 8, n3 + 108, 120, 18, this.flag3 ? "Replacing ON" : "Keep current", flag || this.flag3);
            if (this.flag3) {
                String str = this.entries3.isEmpty() ? "(none found)" : this.entries3.get(Math.max(0, Math.min(this.count7, this.entries3.size() - 1)));
                this.handleInt(this.count8 + 136, n3 + 108, 180, 18, str, matchesInt(n, n2, this.count8 + 136, n3 + 108, 180, 18));
                this.handleInt(this.count8 + 320, n3 + 108, 24, 18, "<", matchesInt(n, n2, this.count8 + 320, n3 + 108, 24, 18));
                this.handleInt(this.count8 + 348, n3 + 108, 24, 18, ">", matchesInt(n, n2, this.count8 + 348, n3 + 108, 24, 18));
            }

            this.handleInt(this.count8 + 8, n3 + 140, 90, 20, "Save", matchesInt(n, n2, this.count8 + 8, n3 + 140, 90, 20));
            String str2 = this.flag2 ? "Sure?" : "Delete";
            this.handleInt(this.count8 + 106, n3 + 140, 70, 20, str2, matchesInt(n, n2, this.count8 + 106, n3 + 140, 70, 20));
            if (this.text3 != null && !this.text3.isEmpty()) {
                this.inter18ptSemiboldTtfComponent.handleString2(this.text3, this.count8 + 186, n3 + 146, 11184810);
            }
        }
    }

    private static int computeInt(int n) {
        return n << 24 | OceanComponent.computeColor().getRGB() & 16777215;
    }

    public static enum BrowseMode {
        BROWSE,
        DETAIL,
        UPLOAD,
        EDIT;

        private static GuiCloudBrowser.BrowseMode[] computeBrowseModeArray() {
            return new GuiCloudBrowser.BrowseMode[]{BROWSE, DETAIL, UPLOAD, EDIT};
        }
    }
}
