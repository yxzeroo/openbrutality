package org.brutality.alt.altmanager.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.util.Session;
import org.apache.commons.lang3.StringUtils;
import org.brutality.alt.alt.GuiAltLogin;
import org.brutality.alt.alt.cookie.CookieAuth;
import org.brutality.alt.altmanager.Account;
import org.brutality.alt.altmanager.AccountManager;
import org.brutality.alt.altmanager.auth.AccountSession;
import org.brutality.alt.altmanager.auth.SessionHolder;
import org.brutality.alt.altmanager.utils.StatusToast;
import org.brutality.gui.ChangeSkinScreen;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.MenuButtonRenderer;
import org.brutality.gui.NameChangerScreen;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.lwjgl.input.Keyboard;

public class GuiNfasAltManager extends CustomGuiScreen {
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final int count = 58;
    private static CustomFontRenderer drawStringWithShadowComponent;
    private static final int count2 = -15920096;
    private static final int count3 = -15655893;
    private static final int count4 = -2838729;
    private static final int count5 = -10496;
    private static final int count6 = -4626055;
    private static final int count7 = -8144657;
    private static final int count8 = -1;
    private static final int count9 = -6575427;
    private static final int count10 = -7829368;
    private static final int count11 = -14018022;
    private static final int count12 = -12769754;
    private static final int count13 = -13447886;
    private static final int count14 = -48060;
    private final GuiScreen guiScreen;
    public GuiAltButton guiAltButton = null;
    private GuiAltButton guiAltButton2 = null;
    private GuiAltButton guiAltButton3 = null;
    private GuiAltButton guiAltButton4 = null;
    private GuiAltButton guiAltButton5 = null;
    private GuiAltButton guiAltButton6 = null;
    private GuiAltButton guiAltButton7 = null;
    private GuiAltButton guiAltButton8 = null;
    private GuiAltButton guiAltButton9 = null;
    private GuiAltButton guiAltButton10 = null;
    private GuiAltButton guiAltButton11 = null;
    private GuiNfasAltManager.SlotList slotList = null;
    private StatusToast statusToast = null;
    public int count15 = -1;
    public static final ArrayList<NfasAccount> entries = new ArrayList<>();
    private static final File file = new File(Minecraft.getMinecraft().mcDataDir, "/Brutality/nfas_accounts.json");
    private static final File file2 = new File(Minecraft.getMinecraft().mcDataDir, "/Brutality/nfas_settings.json");
    private static boolean flag = false;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private File computeFile() throws Exception {
        FileDialog fileDialog = new FileDialog((Frame)null, "Select Cookie File", 0);
        fileDialog.setFile("*.txt");
        String property = System.getProperty("user.home");
        File file = new File(property, "Downloads");
        if (file.exists()) {
            fileDialog.setDirectory(file.getAbsolutePath());
        }

        fileDialog.setVisible(true);
        String file2 = fileDialog.getFile();
        String directory = fileDialog.getDirectory();
        fileDialog.dispose();
        return file2 != null && directory != null ? new File(directory, file2) : null;
    }

    private void process3() {
        MenuButtonRenderer.handleInt(this.width, this.height, 58, this.height - 112);
    }

    public void handleString(String str, String str2, String str3) {
        NfasAccount nfasAccount = null;

        for (NfasAccount nfasAccount2 : entries) {
            if (nfasAccount2.getUsername().equalsIgnoreCase(str)) {
                nfasAccount = nfasAccount2;
                nfasAccount2.setText(str2);
                nfasAccount2.setAccessToken(str3);
                nfasAccount2.setCrackedMode(NfasAccount.CrackedMode.SESSION);
                break;
            }
        }

        if (nfasAccount == null) {
            nfasAccount = new NfasAccount(str, str2, str3, NfasAccount.CrackedMode.SESSION);
            entries.add(nfasAccount);
        }

        this.process7();
        this.statusToast = new StatusToast(String.format("Added session account: %s", str), 3000L);
        this.handleNfasAccount2(nfasAccount);
    }

    private void process8() {
        String username = SessionHolder.getSession().getUsername();
        String str = "Current Session: " + username;
        int textWidth = computeDrawStringWithShadowComponent2().getStringWidth(str);
        MenuButtonRenderer.handleInt(5, 5, textWidth + 10, 20, -8144657, 0.0F, true);
        computeDrawStringWithShadowComponent2().drawString("Current Session: ", 8, 12, -6575427);
        computeDrawStringWithShadowComponent2().drawString(username, 8 + computeDrawStringWithShadowComponent2().getStringWidth("Current Session: "), 12, -8144657);
    }

    private void handleNfasAccount3(NfasAccount nfasAccount) {
        try {
            SessionHolder.setSession(new Session(nfasAccount.getUsername(), nfasAccount.getText(), nfasAccount.getAccessToken(), "mojang"));
            String str = nfasAccount.getCrackedMode() == NfasAccount.CrackedMode.COOKIE ? "Cookie" : "Session";
            this.statusToast = new StatusToast(String.format("Logged in as %s (%s)", nfasAccount.getUsername(), str), 3000L);
        } catch (Exception e) {
            this.statusToast = new StatusToast("Failed to login with account", 3000L);
            e.printStackTrace();
        }
    }

    public GuiScreen getGuiScreen() {
        return this.guiScreen;
    }

    @Override
    public void handleMouseInput() throws IOException {
        if (this.slotList != null) {
            this.slotList.handleMouseInput();
        }

        super.handleMouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.process3();
        if (this.slotList != null) {
            this.slotList.drawScreen(mouseX, mouseY, partialTicks);
        }

        this.process9();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.process8();
        this.process4();
    }

    private void process6() {
        if (this.guiAltButton11 != null) {
            this.guiAltButton11.displayString = this.computeString();
        }
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.process();
        byte b = 120;
        int n = 24;
        int n2 = 8;
        byte b2 = 75;
        byte b3 = 12;
        int n3 = this.height - b3 - n;
        int n4 = n3 - n2 - n;
        int n5 = n4 - n2 - n;
        int n6 = b2 * 5 + n2 * 4;
        int n7 = this.width / 2 - n6 / 2;
        this.buttonList.add(this.guiAltButton = new GuiAltButton(0, this.width / 2 - (b * 2 + n2) / 2, n5, b, n, "Login", -2838729, -10496));
        this.buttonList.add(this.guiAltButton2 = new GuiAltButton(2, this.width / 2 + n2 / 2, n5, b, n, "Delete", -4626055, -8144657));
        this.buttonList.add(this.guiAltButton4 = new GuiAltButton(1, n7, n4, b2, n, "Add Cracked", -2838729, -10496));
        this.buttonList.add(this.guiAltButton5 = new GuiAltButton(4, n7 + b2 + n2, n4, b2, n, "Cookie", -2838729, -10496));
        this.buttonList.add(new GuiAltButton(12, n7 + (b2 + n2) * 2, n4, b2, n, "Refresh", -2838729, -10496));
        this.buttonList.add(this.guiAltButton6 = new GuiAltButton(5, n7 + (b2 + n2) * 3, n4, b2, n, "Session", -2838729, -10496));
        this.buttonList.add(this.guiAltButton7 = new GuiAltButton(9, n7 + (b2 + n2) * 4, n4, b2, n, "Microsoft", -2838729, -10496));
        this.buttonList.add(this.guiAltButton3 = new GuiAltButton(3, this.width / 2 - b / 2, n3, b, n, "Back", -7829368, -6575427));
        byte b4 = 100;
        byte b5 = 24;
        int n8 = this.width - b4 - 15;
        byte b6 = 66;
        this.buttonList.add(this.guiAltButton8 = new GuiAltButton(6, n8, b6, b4, b5, "Copy IGN", -2838729, -10496));
        this.buttonList.add(this.guiAltButton9 = new GuiAltButton(7, n8, b6 + b5 + 6, b4, b5, "Name Changer", -2838729, -10496));
        this.buttonList.add(this.guiAltButton10 = new GuiAltButton(8, n8, b6 + (b5 + 6) * 2, b4, b5, "Skin Changer", -2838729, -10496));
        this.buttonList
            .add(
                this.guiAltButton11 = new GuiAltButton(
                    10, n8, b6 + (b5 + 6) * 3, b4, b5, this.computeString(), flag ? -13447886 : -7829368, flag ? -8586240 : -6575427
                )
            );
        this.slotList = new GuiNfasAltManager.SlotList(mc);
        this.slotList.registerScrollButtons(11, 12);
        this.updateScreen();
    }

    private boolean isWindows() {
        String property = System.getProperty("os.name");
        return property != null && property.toLowerCase().contains("windows");
    }

    private void process10() {
        try {
            if (file2.getParentFile() != null && !file2.getParentFile().exists()) {
                file2.getParentFile().mkdirs();
            }

            JsonObject json = new JsonObject();
            json.addProperty("autoLogin", flag);

            try (PrintWriter writer = new PrintWriter(new FileWriter(file2))) {
                writer.println(gson.toJson((JsonElement)json));
            }
        } catch (Exception ignored) {
        }
    }

    private void process7() {
        try {
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            JsonArray jsonArray = new JsonArray();

            for (NfasAccount nfasAccount : entries) {
                if (nfasAccount.getCrackedMode() == NfasAccount.CrackedMode.COOKIE || nfasAccount.getCrackedMode() == NfasAccount.CrackedMode.SESSION) {
                    JsonObject json = new JsonObject();
                    json.addProperty("username", nfasAccount.getUsername());
                    json.addProperty("uuid", nfasAccount.getText());
                    json.addProperty("accessToken", nfasAccount.getAccessToken());
                    json.addProperty("type", nfasAccount.getCrackedMode().name());
                    json.addProperty("timestamp", nfasAccount.getTimestamp());
                    jsonArray.add(json);
                }
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(gson.toJson((JsonElement)jsonArray));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GuiNfasAltManager(GuiScreen guiScreen, StatusToast statusToast) {
        this.guiScreen = guiScreen;
        this.statusToast = statusToast;
        this.process11();
        this.process2();
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button != null && button.enabled) {
            switch (button.id) {
                case 0:
                    if (this.count15 >= 0 && this.count15 < entries.size()) {
                        this.handleNfasAccount(entries.get(this.count15));
                    }
                    break;
                case 1:
                    mc.displayGuiScreen(new GuiAltLogin(this));
                    break;
                case 2:
                    if (this.count15 >= 0 && this.count15 < entries.size()) {
                        NfasAccount nfasAccount = entries.get(this.count15);
                        if (nfasAccount.getCrackedMode() == NfasAccount.CrackedMode.CRACKED) {
                            for (int i = 0; i < AccountManager.size(); i++) {
                                AccountSession accountSession = AccountManager.computeAccountSession(i);
                                if (accountSession.getUsername().equals(nfasAccount.getUsername()) && (accountSession.getRefreshToken() == null || accountSession.getRefreshToken().isEmpty())) {
                                    AccountManager.remove(i);
                                    AccountManager.save();
                                    break;
                                }
                            }
                        }

                        entries.remove(this.count15);
                        this.process7();
                        this.count15 = Math.max(0, this.count15 - 1);
                        this.updateScreen();
                    }
                    break;
                case 3:
                    mc.displayGuiScreen(this.guiScreen);
                    break;
                case 4:
                    mc.displayGuiScreen(new GuiCookieLogin(this));
                    break;
                case 5:
                    mc.displayGuiScreen(new GuiTokenLogin(this));
                    break;
                case 6:
                    String username = SessionHolder.getSession().getUsername();
                    setClipboardString(username);
                    this.statusToast = new StatusToast(String.format("Copied IGN to clipboard: %s", username), 3000L);
                    break;
                case 7:
                    mc.displayGuiScreen(new NameChangerScreen(this));
                    break;
                case 8:
                    mc.displayGuiScreen(new ChangeSkinScreen(this));
                    break;
                case 9:
                    mc.displayGuiScreen(new GuiMicrosoftAuth(this));
                    break;
                case 10:
                    flag = !flag;
                    this.process10();
                    this.process6();
                    this.statusToast = new StatusToast(String.format("Auto Login %s", flag ? "enabled" : "disabled"), 2500L);
                    break;
                case 11:
                default:
                    if (this.slotList != null) {
                        this.slotList.actionPerformed(button);
                    }
                    break;
                case 12:
                    mc.displayGuiScreen(new GuiSessionLogin(this));
            }
        }
    }

    private int computeInt(String str) {
        if (str.contains("§a") || str.toLowerCase().contains("successful")) {
            return -13447886;
        }
        return !str.contains("§c") && !str.toLowerCase().contains("error") ? -1 : -48060;
    }

    private void process5() {
        boolean fullScreen = mc.isFullScreen();
        if (fullScreen) {
            mc.toggleFullscreen();
        }

        File file = null;
        if (this.isWindows()) {
            try {
                file = this.computeFile();
            } catch (Exception ignored) {
            }
        }

        if (file == null) {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setDialogTitle("Select Cookie File");
            jFileChooser.setFileSelectionMode(0);
            jFileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File file2) {
                    return file2.isDirectory() || file2.getName().toLowerCase().endsWith(".txt");
                }

                @Override
                public String getDescription() {
                    return "Cookie Files (*.txt)";
                }
            });
            int n = jFileChooser.showOpenDialog(null);
            if (n == 0) {
                file = jFileChooser.getSelectedFile();
            }
        }

        if (fullScreen) {
            mc.toggleFullscreen();
        }

        if (file != null) {
            try {
                Account account = CookieAuth.instance.computeAccount(file);
                if (account != null) {
                    NfasAccount nfasAccount = new NfasAccount(account.username, account.text3, account.text, NfasAccount.CrackedMode.COOKIE);
                    entries.add(nfasAccount);
                    this.process7();
                    this.statusToast = new StatusToast(String.format("Added cookie account: %s", account.username), 3000L);
                } else {
                    this.statusToast = new StatusToast("Failed to add cookie account", 3000L);
                }
            } catch (Exception e) {
                this.statusToast = new StatusToast("Error reading cookie file", 3000L);
                e.printStackTrace();
            }
        }
    }

    private void process() {
        drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent2(20, true);
    }

    private void process2() {
        ArrayList<NfasAccount> nfasAccounts = new ArrayList<>();

        for (int i = 0; i < AccountManager.size(); i++) {
            AccountSession accountSession = AccountManager.computeAccountSession(i);
            if (accountSession.getRefreshToken() == null || accountSession.getRefreshToken().isEmpty()) {
                nfasAccounts.add(new NfasAccount(accountSession, NfasAccount.CrackedMode.CRACKED));
            }
        }

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                JsonElement element = new JsonParser().parse(reader);
                if (element instanceof JsonArray) {
                    for (JsonElement element2 : element.getAsJsonArray()) {
                        JsonObject asJsonObject = element2.getAsJsonObject();
                        String str = asJsonObject.has("username") ? asJsonObject.get("username").getAsString() : "";
                        String str2 = asJsonObject.has("uuid") ? asJsonObject.get("uuid").getAsString() : "";
                        String str3 = asJsonObject.has("accessToken") ? asJsonObject.get("accessToken").getAsString() : "";
                        String str4 = asJsonObject.has("type") ? asJsonObject.get("type").getAsString() : "COOKIE";
                        long l = asJsonObject.has("timestamp") ? asJsonObject.get("timestamp").getAsLong() : System.currentTimeMillis();
                        NfasAccount.CrackedMode crackedMode = NfasAccount.CrackedMode.valueOf(str4);
                        NfasAccount nfasAccount = new NfasAccount(str, str2, str3, crackedMode);
                        nfasAccount.setTimestamp(l);
                        nfasAccounts.add(nfasAccount);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        entries.clear();

        for (NfasAccount nfasAccount2 : nfasAccounts) {
            boolean flag = false;

            for (NfasAccount nfasAccount3 : entries) {
                if (nfasAccount3.getUsername().equalsIgnoreCase(nfasAccount2.getUsername())) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                entries.add(nfasAccount2);
            }
        }
    }

    private void process4() {
        if (this.statusToast != null && !this.statusToast.isExpired()) {
            String message = this.statusToast.getMessage();
            int textWidth = computeDrawStringWithShadowComponent2().getStringWidth(message);
            int n = textWidth + 20;
            byte b = 30;
            int n2 = this.width / 2 - n / 2;
            byte b2 = 60;
            MenuButtonRenderer.handleInt(n2, b2, n, b, false, false, false);
            this.drawCenteredString(computeDrawStringWithShadowComponent2(), message, this.width / 2, b2 + 11, this.computeInt(message));
        }
    }

    private void handleNfasAccount2(NfasAccount nfasAccount) {
        if (flag && nfasAccount != null) {
            this.count15 = entries.indexOf(nfasAccount);
            if (this.count15 < 0) {
                this.count15 = entries.size() - 1;
            }

            this.handleNfasAccount(nfasAccount);
            this.updateScreen();
        }
    }

    public GuiNfasAltManager(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
        this.process11();
        this.process2();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    private String computeString() {
        return flag ? "Auto Login: ON" : "Auto Login: OFF";
    }

    private void process9() {
        Gui.drawRect(0, 0, this.width, 58, -15655893);
        Gui.drawRect(0, 57, this.width, 58, -14141622);
        String str = "NFAS - Non Full Access";
        String str2 = String.format("(%d accounts)", entries.size());
        CustomFontRenderer drawStringWithShadowComponent = computeDrawStringWithShadowComponent();
        byte b = 8;
        this.drawCenteredString(drawStringWithShadowComponent, str, this.width / 2, b, -8144657);
        this.drawCenteredString(computeDrawStringWithShadowComponent2(), str2, this.width / 2, b + drawStringWithShadowComponent.FONT_HEIGHT + 2, -6575427);
    }

    private void process11() {
        if (file2.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file2))) {
                JsonElement element = new JsonParser().parse(reader);
                if (element instanceof JsonObject) {
                    JsonObject asJsonObject = element.getAsJsonObject();
                    if (asJsonObject.has("autoLogin")) {
                        flag = asJsonObject.get("autoLogin").getAsBoolean();
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private static CustomFontRenderer computeDrawStringWithShadowComponent() {
        return drawStringWithShadowComponent != null ? drawStringWithShadowComponent : computeDrawStringWithShadowComponent2();
    }

    public void handleString2(String str, String str2, String str3) {
        NfasAccount nfasAccount = null;

        for (NfasAccount nfasAccount2 : entries) {
            if (nfasAccount2.getUsername().equalsIgnoreCase(str)) {
                nfasAccount = nfasAccount2;
                nfasAccount2.setText(str2);
                nfasAccount2.setAccessToken(str3);
                nfasAccount2.setCrackedMode(NfasAccount.CrackedMode.COOKIE);
                break;
            }
        }

        if (nfasAccount == null) {
            nfasAccount = new NfasAccount(str, str2, str3, NfasAccount.CrackedMode.COOKIE);
            entries.add(nfasAccount);
        }

        this.process7();
        this.statusToast = new StatusToast("Added cookie account: " + str, 3000L);
        this.handleNfasAccount2(nfasAccount);
    }

    private void handleNfasAccount(NfasAccount nfasAccount) {
        if (nfasAccount != null) {
            if (nfasAccount.getCrackedMode() == NfasAccount.CrackedMode.CRACKED) {
                SessionHolder.setSession(new Session(nfasAccount.getUsername(), "", "", "mojang"));
                this.statusToast = new StatusToast(String.format("Logged in as %s (Cracked)", nfasAccount.getUsername()), 3000L);
            } else if (nfasAccount.getCrackedMode() == NfasAccount.CrackedMode.COOKIE || nfasAccount.getCrackedMode() == NfasAccount.CrackedMode.SESSION) {
                this.handleNfasAccount3(nfasAccount);
            }

            nfasAccount.setTimestamp(System.currentTimeMillis());
            if (nfasAccount.getCrackedMode() == NfasAccount.CrackedMode.COOKIE || nfasAccount.getCrackedMode() == NfasAccount.CrackedMode.SESSION) {
                this.process7();
            }
        }
    }

    @Override
    public void updateScreen() {
        if (this.guiAltButton != null && this.guiAltButton2 != null) {
            this.guiAltButton.enabled = this.guiAltButton2.enabled = this.count15 >= 0 && this.count15 < entries.size();
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        switch (keyCode) {
            case 1:
                this.actionPerformed(this.guiAltButton3);
                break;
            case 28:
                this.actionPerformed(this.guiAltButton);
                break;
            case 200:
                if (this.count15 > 0) {
                    this.count15--;
                }
                break;
            case 208:
                if (this.count15 < entries.size() - 1) {
                    this.count15++;
                }
                break;
            case 211:
                this.actionPerformed(this.guiAltButton2);
        }

        if (isKeyComboCtrlC(keyCode) && this.count15 >= 0 && this.count15 < entries.size()) {
            setClipboardString(entries.get(this.count15).getUsername());
        }
    }

    public static CustomFontRenderer computeDrawStringWithShadowComponent2() {
        return FontComponent.computeDrawStringWithShadowComponent();
    }

    public class SlotList extends GuiSlot {
        @Override
        public void elementClicked(int n, boolean flag, int n2, int n3) {
            GuiNfasAltManager.this.count15 = n;
            GuiNfasAltManager.this.updateScreen();
            if (flag) {
                GuiNfasAltManager.this.actionPerformed(GuiNfasAltManager.this.guiAltButton);
            }
        }

        @Override
        public void drawBackground() {
        }

        public SlotList(Minecraft mc) {
            super(mc, GuiNfasAltManager.this.width, GuiNfasAltManager.this.height, 66, GuiNfasAltManager.this.height - 112, 50);
            this.setShowSelectionBox(false);
        }

        @Override
        public int getSize() {
            return GuiNfasAltManager.entries.size();
        }

        @Override
        public void drawSlot(int n, int n2, int n3, int n4, int n5, int n6) {
            if (n < GuiNfasAltManager.entries.size()) {
                NfasAccount nfasAccount = GuiNfasAltManager.entries.get(n);
                boolean flag = SessionHolder.getSession().getUsername().equals(nfasAccount.getUsername());
                boolean flag2 = n5 >= n2 && n5 < n2 + this.getListWidth() && n6 >= n3 && n6 < n3 + n4;
                int n7 = flag ? -9972828 : -8877150;
                MenuButtonRenderer.handleInt(n2 - 2, n3 - 2, this.getListWidth() + 4, n4 + 4, flag2, n7, n7);
                String str = StringUtils.isBlank(nfasAccount.getUsername()) ? "???" : nfasAccount.getUsername();
                int n8 = flag ? -13447886 : -1;
                FontComponent.computeDrawStringWithShadowComponent().drawString((flag ? "● " : "") + str, n2 + 6, n3 + 4, n8);
                String str2 = GuiNfasAltManager.simpleDateFormat.format(new Date(nfasAccount.getTimestamp()));
                FontComponent.computeDrawStringWithShadowComponent().drawString("Last used: " + str2, n2 + 6, n3 + 18, -6575427);
                String str3 = nfasAccount.getCrackedMode() == NfasAccount.CrackedMode.CRACKED
                    ? "Cracked"
                    : (nfasAccount.getCrackedMode() == NfasAccount.CrackedMode.COOKIE ? "Cookie" : "Session");
                String str4 = nfasAccount.getCrackedMode() == NfasAccount.CrackedMode.CRACKED ? "Offline" : "Token-based";
                FontComponent.computeDrawStringWithShadowComponent()
                    .drawString(flag ? "Current session" : str4, n2 + 6, n3 + 32, flag ? -13447886 : -7829368);
                int n9 = nfasAccount.getCrackedMode() == NfasAccount.CrackedMode.CRACKED ? -7829368 : -4626055;
                int n10 = FontComponent.computeDrawStringWithShadowComponent().getStringWidth(str3) + 14;
                int n11 = n2 + this.getListWidth() - n10 - 4;
                MenuButtonRenderer.handleInt(n11, n3 + 2, n10, 15, str3, FontComponent.computeDrawStringWithShadowComponent(), n9, 0.0F, true);
            }
        }

        @Override
        public int getContentHeight() {
            return GuiNfasAltManager.entries.size() * 50;
        }

        @Override
        public boolean isSelected(int n) {
            return n == GuiNfasAltManager.this.count15;
        }

        @Override
        public boolean drawsThemedRows() {
            return false;
        }
    }
}
