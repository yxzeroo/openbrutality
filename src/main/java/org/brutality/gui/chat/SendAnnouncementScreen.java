package org.brutality.gui.chat;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.OceanComponent;
import org.brutality.gui.SearchField;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.gui.mainmenu.effects.MainMenuIconRenderer;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.TruceManager;
import org.brutality.utils.WHelper;
import org.lwjgl.input.Keyboard;

public class SendAnnouncementScreen extends CustomGuiScreen {
    private static final int count = 12;
    private static final int count2 = 16;
    private static final int count3 = 4;
    private final ParseIntComponent.Entry entry;
    private final boolean flag;
    private final boolean flag2;
    private final CompletableFuture<PitPandaPlayerProfile> completableFuture;
    private SearchField searchField;
    private SearchField searchField2;
    private boolean flag3 = true;
    private boolean flag4;
    private boolean flag5;
    private boolean flag6;
    private final List<SendAnnouncementScreen.Entry> entries = new ArrayList<>();
    private int count4;
    private int count5;
    private int count6;
    private int count7;
    private int count8;
    private int count9;
    private int count10;
    private int count11;
    private int count12;
    private List<String> entries2 = new ArrayList<>();
    private final long timestamp = System.nanoTime();
    private long timestamp2 = System.nanoTime();
    private boolean flag7;
    private boolean flag8;
    private boolean flag9;

    private CustomFontRenderer computeDrawStringWithShadowComponent() {
        return FontComponent.computeDrawStringWithShadowComponent2(15, false);
    }

    private void sendAnnouncement() {
        String str = this.searchField.getText().trim();
        if (!str.isEmpty()) {
            ArrayList<String> list = new ArrayList<>();
            if (this.flag3) {
                list.add("/gc " + str);
                if (this.flag6) {
                    list.add("/g party");
                }
            }

            if (this.flag4) {
                list.add("/pc " + str);
            }

            if (this.flag5) {
                String str2 = this.searchField2.getText().trim();
                if (!str2.matches("[A-Za-z0-9_]{1,16}")) {
                    this.handleString2(EnumChatFormatting.RED + "Enter a valid username to message.");
                    return;
                }

                list.add("/msg " + str2 + " " + str);
            }

            if (list.isEmpty()) {
                this.handleString2(EnumChatFormatting.RED + "Pick at least one: Guild, Party or Message.");
            } else {
                mc.displayGuiScreen(null);
                Thread thread = new Thread(() -> {
                    for (String str3 : list) {
                        mc.addScheduledTask(() -> {
                            if (mc.thePlayer != null) {
                                mc.thePlayer.sendChatMessage(str3);
                            }
                        });

                        try {
                            Thread.sleep(450L);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                }, "Brutality-ChatAnnounce");
                thread.setDaemon(true);
                thread.start();
            }
        }
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
        AvatarComponent.handleFloat(f2, this.count4 + this.count6 / 2, this.count5 + this.count7 / 2);
        AvatarComponent.handleInt(this.count4, this.count5, this.count6, this.count7, f2);
        CustomFontRenderer drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(22, true);
        CustomFontRenderer drawStringWithShadowComponent2 = this.computeDrawStringWithShadowComponent();
        int n = this.count4 + 12;
        int n2 = this.count6 - 24;
        int n3 = this.count5 + 12;
        AvatarComponent.handleString(this.entry.text, n, n3, 36);
        int n4 = n + 44;
        GlStateManager.enableTexture2D();
        String str = this.flag2 ? this.computeString() : this.computeString2();
        drawStringWithShadowComponent.drawString(str, n4, n3, -788999);
        int n5 = n4 + drawStringWithShadowComponent.getStringWidth(str) + 8;
        n5 = this.computeInt(drawStringWithShadowComponent2, n5, n3 + 2, "KOS", WHelper.matchesString(this.entry.text), 14703194);
        n5 = this.computeInt(drawStringWithShadowComponent2, n5, n3 + 2, "FRIEND", DefaultHelper.matchesString(this.entry.text), 6279820);
        int n6 = this.computeInt(drawStringWithShadowComponent2, n5, n3 + 2, "TRUCE", TruceManager.matchesString(this.entry.text), 7252720);
        if (this.entry.flag) {
            this.computeInt(drawStringWithShadowComponent2, n6, n3 + 2, "UBER", true, 16099910);
        }

        this.handleDrawStringWithShadowComponent(drawStringWithShadowComponent2, n4, n3 + 22, this.count4 + this.count6 - 12 - n4);
        if (!this.flag2) {
            drawRect(n, this.count8, n + n2, this.count8 + this.count9, 1073741824);
        }

        if (!this.flag2) {
            drawRect(n, this.count8, n + 2, this.count8 + this.count9, MainMenuIconRenderer.computeColor(OceanComponent.computeColor(), 200).getRGB());
        }

        GlStateManager.enableTexture2D();

        for (int i = 0; i < this.entries2.size(); i++) {
            drawStringWithShadowComponent2.drawString(this.entries2.get(i), n + 9, this.count8 + 6 + i * (drawStringWithShadowComponent2.FONT_HEIGHT + 2), -3551270);
        }

        if (this.flag) {
            AvatarComponent.handleDrawStringWithShadowComponent(drawStringWithShadowComponent2, this.entry.flag ? "Announce uber" : "Announce", n, this.count10, n2);
            this.searchField.drawTextBox();
            if (this.flag5) {
                this.searchField2.drawTextBox();
            }
        }

        AvatarComponent.handleDrawStringWithShadowComponent(drawStringWithShadowComponent2, "Normal", n, this.count11, n2);
        AvatarComponent.handleDrawStringWithShadowComponent(drawStringWithShadowComponent2, "Brutality", n, this.count12, n2);

        for (SendAnnouncementScreen.Entry entry : this.entries) {
            if (entry.booleanSupplier2.getAsBoolean()) {
                boolean flag = entry.rectangle.contains(mouseX, mouseY);
                entry.factor = entry.factor + ((flag ? 1.0F : 0.0F) - entry.factor) * f;
                AvatarComponent.handleInt(
                    entry.rectangle.x,
                    entry.rectangle.y,
                    entry.rectangle.width,
                    entry.rectangle.height,
                    entry.supplier.get(),
                    drawStringWithShadowComponent2,
                    entry.factor,
                    entry.booleanSupplier.getAsBoolean(),
                    entry.flag
                );
            }
        }

        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private String computeString() {
        PitPandaPlayerProfile fetchComponent2 = this.completableFuture.isDone() ? this.completableFuture.getNow(null) : null;
        if (fetchComponent2 == null) {
            return this.computeString2();
        }
        String str = fetchComponent2.formattedName.isEmpty() ? this.entry.text : fetchComponent2.formattedName;
        return fetchComponent2.formattedLevel.isEmpty() ? str : fetchComponent2.formattedLevel + " " + str;
    }

    private int computeInt(CustomFontRenderer drawStringWithShadowComponent, int n, int n2, String str, boolean flag, int n3) {
        if (!flag) {
            return n;
        }
        int n4 = drawStringWithShadowComponent.getStringWidth(str) + 10;
        int n5 = drawStringWithShadowComponent.FONT_HEIGHT + 3;
        Color color = new Color(n3);
        MainMenuIconRenderer.handleInt(n, n2, n4, n5, color, true);
        GlStateManager.enableTexture2D();
        drawStringWithShadowComponent.drawString(str, n + 5, n2 + 2, 0xFF000000 | color.brighter().getRGB());
        return n + n4 + 4;
    }

    private void handleString(String str) {
        mc.displayGuiScreen(null);
        if (mc.thePlayer != null) {
            mc.thePlayer.sendChatMessage(str);
        }
    }

    public SendAnnouncementScreen(ParseIntComponent.Entry entry) {
        this.entry = entry;
        this.flag = entry.streakMode != ParseIntComponent.StreakMode.PLAYER && entry.streakMode != ParseIntComponent.StreakMode.VIEW;
        this.flag2 = entry.streakMode == ParseIntComponent.StreakMode.VIEW;
        this.completableFuture = PitPandaPlayerProfile.fetch(entry.text);
    }

    private String computeString2() {
        if (mc.getNetHandler() != null && mc.ingameGUI != null) {
            NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(this.entry.text);
            if (playerInfo != null) {
                String playerName = mc.ingameGUI.getTabList().getPlayerName(playerInfo);
                if (playerName != null && !playerName.trim().isEmpty()) {
                    return playerName.trim();
                }
            }
        }

        PitPandaPlayerProfile fetchComponent2 = this.completableFuture.isDone() ? this.completableFuture.getNow(null) : null;
        return fetchComponent2 != null && !fetchComponent2.formattedName.isEmpty() ? fetchComponent2.formattedName : this.entry.text;
    }

    private void handleString2(String str) {
        if (mc.thePlayer != null) {
            EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
            EnumChatFormatting DARK_GRAY = EnumChatFormatting.DARK_GRAY;
            EnumChatFormatting LIGHT_PURPLE = EnumChatFormatting.LIGHT_PURPLE;
            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GRAY + "[" + LIGHT_PURPLE + "Brutality" + DARK_GRAY + "] " + GRAY + str));
        }
    }

    private CustomFontRenderer computeDrawStringWithShadowComponent2() {
        return FontComponent.computeDrawStringWithShadowComponent2(17, false);
    }

    private void send(String str) {
        if (mc.thePlayer != null) {
            mc.thePlayer.sendChatMessage(str);
        }
    }

    private static List<String> computeList(CustomFontRenderer drawStringWithShadowComponent, String str, int n, int n2) {
        List list = new ArrayList();
        StringBuilder sb = new StringBuilder();

        for (String str2 : str.split(" ")) {
            String str3 = sb.length() == 0 ? str2 : sb + " " + str2;
            if (drawStringWithShadowComponent.getStringWidth(str3) > n && sb.length() > 0) {
                list.add(sb.toString());
                sb = new StringBuilder(str2);
                if (list.size() == n2) {
                    break;
                }
            } else {
                sb = new StringBuilder(str3);
            }
        }

        if (list.size() < n2 && sb.length() > 0) {
            list.add(sb.toString());
        }

        if (list.size() == n2 && drawStringWithShadowComponent.getStringWidth((String)list.get(n2 - 1)) > n - 10) {
            String str4 = (String)list.get(n2 - 1);

            while (str4.length() > 1 && drawStringWithShadowComponent.getStringWidth(str4 + "...") > n) {
                str4 = str4.substring(0, str4.length() - 1);
            }

            list.set(n2 - 1, str4 + "...");
        }

        if (list.isEmpty()) {
            list.add(str);
        }

        return list;
    }

    @Override
    public void updateScreen() {
        if (this.searchField != null) {
            this.searchField.updateCursorCounter();
        }

        if (this.searchField2 != null) {
            this.searchField2.updateCursorCounter();
        }
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.entries.clear();
        String str = this.searchField == null ? this.entry.text4 : this.searchField.getText();
        String str2 = this.searchField2 == null ? "" : this.searchField2.getText();
        this.count6 = Math.min(360, this.width - 16);
        int n = this.count6 - 24;
        this.entries2 = (List<String>)(this.flag2
            ? new ArrayList<>()
            : computeList(this.computeDrawStringWithShadowComponent(), "\"" + this.entry.text5 + "\"", n - 16, 2));
        this.count9 = this.flag2 ? -10 : this.entries2.size() * (this.computeDrawStringWithShadowComponent().FONT_HEIGHT + 2) + 10;
        byte b = 74;
        byte b2 = 54;
        int n2 = this.flag ? 110 : 0;
        this.count7 = 56 + this.count9 + 10 + n2 + b + 6 + b2 + 12 - 4;
        this.count4 = (this.width - this.count6) / 2;
        this.count5 = Math.max(4, (this.height - this.count7) / 2);
        int n3 = this.count4 + 12;
        int n4 = this.count5 + 12 + 36 + 8;
        this.count8 = n4;
        int n5 = n4 + this.count9 + 10;
        if (this.flag) {
            this.count10 = n5;
            n4 = n5 + 14;
            this.searchField = new SearchField(1, n3, n4, n, 18);
            this.searchField.setMaxStringLength(100);
            this.searchField.setText(str);
            n4 += 24;
            int n6 = (n - 8) / 3;
            this.entries
                .add(
                    new SendAnnouncementScreen.Entry(
                        new Rectangle(n3, n4, n6, 16), () -> "Guild  /gc", () -> this.flag3 = !this.flag3, () -> this.flag3, false, () -> true
                    )
                );
            this.entries
                .add(
                    new SendAnnouncementScreen.Entry(
                        new Rectangle(n3 + n6 + 4, n4, n6, 16), () -> "Party  /pc", () -> this.flag4 = !this.flag4, () -> this.flag4, false, () -> true
                    )
                );
            this.entries
                .add(
                    new SendAnnouncementScreen.Entry(
                        new Rectangle(n3 + (n6 + 4) * 2, n4, n - (n6 + 4) * 2, 16),
                        () -> "Message  /msg",
                        () -> this.flag5 = !this.flag5,
                        () -> this.flag5,
                        false,
                        () -> true
                    )
                );
            n4 += 22;
            this.entries
                .add(
                    new SendAnnouncementScreen.Entry(
                        new Rectangle(n3, n4, n6, 16), () -> "+ /g party", () -> this.flag6 = !this.flag6, () -> this.flag6, false, () -> this.flag3
                    )
                );
            this.searchField2 = new SearchField(2, n3 + n6 + 4, n4, n - n6 - 4, 16).setText2("Message who? (username)");
            this.searchField2.setMaxStringLength(16);
            this.searchField2.setText(str2);
            n4 += 22;
            this.entries
                .add(
                    new SendAnnouncementScreen.Entry(
                        new Rectangle(n3, n4, n, 18), () -> "Send announcement", this::sendAnnouncement, () -> false, true, () -> true
                    )
                );
            n5 = n4 + 28;
        }

        String text = this.entry.text;
        this.count11 = n5;
        n4 = n5 + 14;
        byte b3 = 3;
        int n7 = (n - 4 * (b3 - 1)) / b3;
        Object[][] objsArray = new Object[][]{
            {(Supplier<String>)() -> "Message", (Runnable)() -> mc.displayGuiScreen(new MessageToScreen(this, text)), (BooleanSupplier)() -> false},
            {(Supplier<String>)() -> this.flag7 ? "Invite sent  ·  Undo" : "Party invite", (Runnable)() -> {
                this.send(this.flag7 ? "/p leave" : "/p " + text);
                this.flag7 = !this.flag7;
            }, (BooleanSupplier)() -> this.flag7},
            {(Supplier<String>)() -> this.flag8 ? "Request sent  ·  Undo" : "Friend request", (Runnable)() -> {
                this.send(this.flag8 ? "/friend remove " + text : "/f " + text);
                this.flag8 = !this.flag8;
            }, (BooleanSupplier)() -> this.flag8},
            {(Supplier<String>)() -> this.flag9 ? "Ignored  ·  Undo" : "Ignore", (Runnable)() -> {
                this.send(this.flag9 ? "/ignore remove " + text : "/ignore add " + text);
                this.flag9 = !this.flag9;
            }, (BooleanSupplier)() -> this.flag9},
            {(Supplier<String>)() -> "Chat report", (Runnable)() -> this.handleString("/cr " + text), (BooleanSupplier)() -> false},
            {(Supplier<String>)() -> "Report", (Runnable)() -> this.handleString("/report " + text), (BooleanSupplier)() -> false},
            {(Supplier<String>)() -> "Copy name", (Runnable)() -> {
                GuiScreen.setClipboardString(text);
                this.handleString2("Copied " + text);
            }, (BooleanSupplier)() -> false}
        };

        for (int i = 0; i < objsArray.length; i++) {
            var supplier = (Supplier)objsArray[i][0];
            int n8 = i == objsArray.length - 1 ? n : n7;
            this.entries
                .add(
                    new SendAnnouncementScreen.Entry(
                        new Rectangle(n3 + i % b3 * (n7 + 4), n4 + i / b3 * 20, n8, 16),
                        supplier,
                        (Runnable)objsArray[i][1],
                        (BooleanSupplier)objsArray[i][2],
                        false,
                        () -> true
                    )
                );
        }

        n4 += 66;
        this.count12 = n4;
        n4 += 14;
        int n9 = (n - 4) / 2;
        this.entries
            .add(
                new SendAnnouncementScreen.Entry(
                    new Rectangle(n3, n4, n9, 16), () -> "(Brutality) " + (DefaultHelper.matchesString(text) ? "Remove friend" : "Add friend"), () -> {
                        if (DefaultHelper.matchesString(text)) {
                            DefaultHelper.handleString(text);
                        } else {
                            DefaultHelper.handleString2(text);
                        }
                    }, () -> DefaultHelper.matchesString(text), false, () -> true
                )
            );
        this.entries
            .add(
                new SendAnnouncementScreen.Entry(
                    new Rectangle(n3 + n9 + 4, n4, n - n9 - 4, 16),
                    () -> "(Brutality) " + (WHelper.matchesString(text) ? "Remove KOS" : "Add KOS"),
                    () -> {
                        if (WHelper.matchesString(text)) {
                            WHelper.handleString(text);
                        } else {
                            WHelper.w(text);
                        }
                    },
                    () -> WHelper.matchesString(text),
                    false,
                    () -> true
                )
            );
        n4 += 20;
        this.entries
            .add(
                new SendAnnouncementScreen.Entry(
                    new Rectangle(n3, n4, n9, 16),
                    () -> "(Brutality) " + (TruceManager.matchesString(text) ? "Remove truce" : "Add truce"),
                    () -> TruceManager.matchesString2(text),
                    () -> TruceManager.matchesString(text),
                    false,
                    () -> true
                )
            );
        this.entries
            .add(
                new SendAnnouncementScreen.Entry(
                    new Rectangle(n3 + n9 + 4, n4, n - n9 - 4, 16),
                    () -> "PitPanda profile  ›",
                    () -> mc.displayGuiScreen(new NoPitPandaDataScreen(this, text, this.entry.text2)),
                    () -> false,
                    true,
                    () -> true
                )
            );
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.flag) {
            this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
            if (this.flag5) {
                this.searchField2.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }

        if (mouseButton == 0) {
            if (!new Rectangle(this.count4, this.count5, this.count6, this.count7).contains(mouseX, mouseY)) {
                mc.displayGuiScreen(null);
            } else {
                for (SendAnnouncementScreen.Entry entry : this.entries) {
                    if (entry.booleanSupplier2.getAsBoolean() && entry.rectangle.contains(mouseX, mouseY)) {
                        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                        entry.runnable.run();
                        return;
                    }
                }
            }
        }
    }

    private void handleDrawStringWithShadowComponent(CustomFontRenderer drawStringWithShadowComponent, int n, int n2, int n3) {
        PitPandaPlayerProfile fetchComponent2 = this.completableFuture.isDone() ? this.completableFuture.getNow(null) : null;
        if (!this.completableFuture.isDone()) {
            drawStringWithShadowComponent.drawString("Loading PitPanda...", n, n2, -8550246);
        } else if (fetchComponent2 == null) {
            drawStringWithShadowComponent.drawString("No PitPanda data", n, n2, -8550246);
        } else {
            int n4 = n;
            if (!fetchComponent2.formattedLevel.isEmpty() && !this.flag2) {
                CustomFontRenderer drawStringWithShadowComponent2 = FontComponent.computeDrawStringWithShadowComponent2(16, false);
                drawStringWithShadowComponent2.drawStringWithShadow(fetchComponent2.formattedLevel, n, n2, -1);
                n4 = n + drawStringWithShadowComponent2.getStringWidth(fetchComponent2.formattedLevel) + 6;
            }

            String str = PitPandaPlayerProfile.computeString2(fetchComponent2.amount);
            String str2 = String.format(Locale.ROOT, "%.2f", fetchComponent2.amount2);
            String str3 = PitPandaPlayerProfile.computeString2(fetchComponent2.timestamp4);
            long l = fetchComponent2.timestamp2 / 60L;
            String str4 = str3;
            String str5 = str2;
            String str6 = str + " gold  ·  K/D " + str5 + "  ·  " + str4 + " kills  ·  " + l + "h";
            if (drawStringWithShadowComponent.getStringWidth(str6) > n3 - (n4 - n)) {
                str6 = PitPandaPlayerProfile.computeString2(fetchComponent2.amount) + " gold  ·  K/D " + String.format(Locale.ROOT, "%.2f", fetchComponent2.amount2);
            }

            GlStateManager.enableTexture2D();
            drawStringWithShadowComponent.drawString(str6, n4, n2 + 1, -5590078);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(null);
        } else {
            if (this.flag) {
                if (this.searchField.isFocused()) {
                    if (keyCode == 28) {
                        this.sendAnnouncement();
                    } else {
                        this.searchField.textboxKeyTyped(typedChar, keyCode);
                    }

                    return;
                }

                if (this.flag5 && this.searchField2.isFocused()) {
                    this.searchField2.textboxKeyTyped(typedChar, keyCode);
                    return;
                }

                if (keyCode == 28) {
                    this.sendAnnouncement();
                }
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public final class Entry {
        public final Rectangle rectangle;
        public final Supplier<String> supplier;
        public final Runnable runnable;
        public final BooleanSupplier booleanSupplier;
        public final boolean flag;
        public final BooleanSupplier booleanSupplier2;
        public float factor;

        public Entry(Rectangle rectangle, Supplier<String> supplier, Runnable runnable, BooleanSupplier booleanSupplier, boolean flag, BooleanSupplier booleanSupplier2) {
            this.rectangle = rectangle;
            this.supplier = supplier;
            this.runnable = runnable;
            this.booleanSupplier = booleanSupplier;
            this.flag = flag;
            this.booleanSupplier2 = booleanSupplier2;
        }
    }
}
