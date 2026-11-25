package org.brutality.alt.altmanager.shop;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.alt.alt.GuiPasswordField;
import org.brutality.alt.altmanager.AccountManager;
import org.brutality.alt.altmanager.auth.AccountSession;
import org.brutality.alt.altmanager.auth.SessionHolder;
import org.brutality.alt.altmanager.shop.LocaltsApiClient;
import org.brutality.alt.altmanager.shop.LocaltsCookieImporter;
import org.brutality.alt.altmanager.shop.LocaltsOrderStore;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.MenuButtonRenderer;
import org.brutality.gui.OceanComponent;
import org.brutality.gui.SearchField;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.brutality.utils.render.GlStateGuard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public final class GuiLocaltsShop
extends CustomGuiScreen {
    private static final ExecutorService executorService;
    private static final AtomicBoolean atomicBoolean;
    private final GuiScreen guiScreen;
    private final CustomFontRenderer drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
    private GuiPasswordField guiPasswordField;
    private GuiTextField guiTextField;
    private GuiTextField searchField;
    private List<LocaltsApiClient.Lll1I1Entry> entries = Collections.emptyList();
    private boolean flag = true;
    private int count;
    private String text = "";
    private static final int count2;
    private static final int count3;
    private LocaltsOrderStore localtsOrderStore;
    private List<LocaltsApiClient.Lll1I1Entry> entries2 = Collections.emptyList();
    private LocaltsApiClient.O0oO0OEntry o0oO0OEntry;
    private String text2 = "";
    private String status = "Enter your Localts API key and press Refresh.";
    private int count4 = 1;
    private int count5;
    private int left;
    private int right;
    private int count6;
    private int count7;
    private int count8;
    private double amount;
    private long timestamp;
    private float scale = 1.0f;
    private float offsetX;
    private float offsetY;
    private boolean flag2;
    private static final long timestamp2;

    @Override
    public void drawScreen(int n, int n2, float f) {
        OceanComponent.handleGuiScreen(this, n, n2, f);
        int n3 = this.computeInt2(n);
        int n4 = this.computeInt(n2);
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.offsetX, this.offsetY, 0.0f);
        GlStateManager.scale(this.scale, this.scale, 1.0f);
        try {
            this.drawStringWithShadowComponent.drawString("Localts Account Shop", 18, 18, -1511690);
            MenuButtonRenderer.handleInt(16, 46, this.left, this.count6 - 46, false, false, false);
            MenuButtonRenderer.handleInt(this.right, 46, this.count7 - this.right - 16, this.count6 - 46, false, false, false);
            this.drawStringWithShadowComponent.drawString("Customer API key", 28, 59, -6575427);
            this.guiPasswordField.drawTextBox();
            this.guiTextField.drawTextBox();
            this.drawStringWithShadowComponent.drawString(this.computeString((String)(this.o0oO0OEntry == null ? "Not connected" : "Signed in as " + this.o0oO0OEntry.o0oO0O()), this.left - 24), 28, 137, -1511690);
            this.drawStringWithShadowComponent.drawString((String)(this.o0oO0OEntry == null ? "Balance: --" : "Balance: " + GuiLocaltsShop.computeString(this.o0oO0OEntry.l1l1()) + " credits"), 28, 155, OceanComponent.computeColor().getRGB());
            LocaltsApiClient.Lll1I1Entry lll1I1Entry = this.computeLll1I1Entry();
            String string = this.count4 + "x" + (String)(lll1I1Entry == null ? "" : " \u00b7 " + GuiLocaltsShop.computeString(lll1I1Entry.computeBigDecimal(this.count4)) + " credits");
            this.drawStringWithShadowComponent.computeInt(this.computeString(string, this.left - 100), 16.0f + (float)this.left / 2.0f, 187.0f, -1511690);
            this.drawStringWithShadowComponent.drawString("Order ID (saved for recovery)", 28, 246, -6575427);
            this.drawStringWithShadowComponent.drawString("Products (" + this.entries.size() + " / " + this.entries2.size() + ")", this.right + 12, 59, -1511690);
            this.searchField.drawTextBox();
            if (this.searchField.getText().isEmpty() && !this.searchField.isFocused()) {
                this.fontRendererObj.drawString("Search products...", this.right + 17, 87, -6575427);
            }
            this.process2();
            int n5 = (int)Math.round(this.amount);
            try (GlStateGuard renderHelper7 = new GlStateGuard();){
                this.process5();
                for (int i = n5 / 68; i < this.entries.size(); ++i) {
                    int n6 = 140 + i * 68 - n5;
                    if (n6 >= this.count6 - 8) {
                        break;
                    }
                    LocaltsApiClient.Lll1I1Entry lll1I1Entry2 = this.entries.get(i);
                    int n7 = this.count7 - this.right - 40;
                    MenuButtonRenderer.handleInt(this.right + 12, n6, n7, 60, n3 >= this.right + 12 && n3 < this.right + 12 + n7 && this.matchesInt(n4) && n4 >= n6 && n4 < n6 + 60, lll1I1Entry2.lll1I1().equals(this.text2), false);
                    this.drawStringWithShadowComponent.drawString(this.computeString(lll1I1Entry2.III1Il(), n7 - 20), this.right + 22, n6 + 9, -1511690);
                    this.drawStringWithShadowComponent.drawString(this.computeString(GuiLocaltsShop.computeString(lll1I1Entry2.Il1I()) + " credits \u00b7 " + lll1I1Entry2.bpf() + " in stock", n7 - 20), this.right + 22, n6 + 27, OceanComponent.computeColor().getRGB());
                    String string2 = lll1I1Entry2.oo0oO0().isEmpty() ? String.join((CharSequence)" \u00b7 ", lll1I1Entry2.O00O()) : lll1I1Entry2.oo0oO0();
                    this.drawStringWithShadowComponent.drawString(this.computeString(string2, n7 - 20), this.right + 22, n6 + 44, -6575427);
                }
            }
            if (this.entries.isEmpty()) {
                this.drawStringWithShadowComponent.drawString(this.entries2.isEmpty() ? "Refresh to load available accounts." : "No products match your filters.", this.right + 12, 149, -6575427);
            }
            this.drawStringWithShadowComponent.drawString(this.computeString(this.status, this.count7 - 32), 16, this.count8 - 23, -1511690);
            boolean bl = atomicBoolean.get();
            for (GuiButton guiButton : this.buttonList) {
                boolean bl2 = guiButton.enabled = guiButton.id == 5 || !bl;
                if (guiButton.id == 3) {
                    guiButton.enabled = guiButton.enabled & (!this.flag2 && this.o0oO0OEntry != null && lll1I1Entry != null && lll1I1Entry.bpf() >= this.count4 && this.o0oO0OEntry.l1l1().compareTo(lll1I1Entry.computeBigDecimal(this.count4)) >= 0);
                }
                if (guiButton.id == 4) {
                    guiButton.enabled = guiButton.enabled & !this.guiTextField.getText().trim().isEmpty();
                }
                if (guiButton.id == 1) {
                    guiButton.enabled = guiButton.enabled & this.count4 > 1;
                }
                if (guiButton.id == 2) {
                    guiButton.enabled = guiButton.enabled & (lll1I1Entry != null && this.count4 < lll1I1Entry.bpf());
                }
                boolean bl3 = n3 >= guiButton.xPosition && n3 < guiButton.xPosition + guiButton.width && n4 >= guiButton.yPosition && n4 < guiButton.yPosition + guiButton.height;
                MenuButtonRenderer.handleInt(guiButton.xPosition, guiButton.yPosition, guiButton.width, guiButton.height, guiButton.displayString, this.drawStringWithShadowComponent, OceanComponent.computeColor().getRGB(), bl3 ? 1.0f : 0.0f, guiButton.enabled);
            }
        }
        finally {
            GlStateManager.popMatrix();
        }
    }

    private String computeString() {
        return (new String[]{"Price: Cheapest first", "Price: Highest first", "Name: A-Z"})[this.count];
    }

    private void handleString(String string) {
        this.handleRunnable(() -> {
            this.status = string;
        });
    }

    private int computeInt() {
        return Math.max(0, this.entries.size() * 68 - (this.count6 - 8 - 140));
    }

    private int computeInt2(int n) {
        return (int)(((float)n - this.offsetX) / this.scale);
    }

    private boolean matchesInt(int n) {
        return n >= 140 && n < this.count6 - 8;
    }

    @Override
    public void keyTyped(char c, int n) throws IOException {
        if (n == 1) {
            mc.displayGuiScreen(this.guiScreen);
            return;
        }
        if (!atomicBoolean.get()) {
            this.guiPasswordField.textboxKeyTyped(c, n);
            this.guiTextField.textboxKeyTyped(c, n);
            this.searchField.textboxKeyTyped(c, n);
            if (!this.searchField.getText().equals(this.text)) {
                this.process3();
            }
        }
    }

    private static String computeString(BigDecimal bigDecimal) {
        return bigDecimal.stripTrailingZeros().toPlainString();
    }

    private void handleListener(Listener listener) {
        if (!atomicBoolean.compareAndSet(false, true)) {
            return;
        }
        String string = this.guiPasswordField.getText();
        executorService.execute(() -> {
            try {
                listener.run(new LocaltsApiClient(string));
            }
            catch (LocaltsApiClient.Entry entry) {
                this.handleRunnable(() -> {
                    this.flag2 = true;
                    this.status = "Purchase outcome unknown. Check your Localts orders before buying again.";
                });
            }
            catch (IllegalArgumentException illegalArgumentException) {
                this.handleString("Enter a valid API key and select a product.");
            }
            catch (Exception exception) {
                String string2;
                String string3 = string2 = exception instanceof IOException ? exception.getMessage() : "Localts action failed. Saved orders can be resumed.";
                this.handleString(string2 == null ? "Localts action failed." : (string.isEmpty() ? string2 : string2.replace(string, "[hidden]")));
            }
            finally {
                this.handleRunnable(() -> atomicBoolean.set(false));
            }
        });
    }

    public GuiLocaltsShop(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
    }

    private void process2() {
        long l = System.nanoTime();
        double d = this.timestamp == 0L ? 0.0 : Math.min(0.05, (double)(l - this.timestamp) / 1.0E9);
        this.timestamp = l;
        this.count5 = Math.max(0, Math.min(this.count5, this.computeInt()));
        this.amount = Math.min(this.amount, (double)this.computeInt());
        this.amount += ((double)this.count5 - this.amount) * (1.0 - Math.exp(-16.0 * d));
    }

    private void process4() {
        LocaltsApiClient.Lll1I1Entry lll1I1Entry = this.computeLll1I1Entry();
        if (lll1I1Entry == null || this.flag2) {
            return;
        }
        int n = this.count4;
        this.status = "Checking price, balance and stock...";
        this.handleListener(localtsApiClient -> {
            this.localtsOrderStore.process();
            LocaltsApiClient.O0oO0OEntry o0oO0OEntry = localtsApiClient.computeO0oO0OEntry();
            List<LocaltsApiClient.Lll1I1Entry> list = localtsApiClient.computeList();
            LocaltsApiClient.Lll1I1Entry lll1I1Entry3 = list.stream().filter(lll1I1Entry2 -> lll1I1Entry2.lll1I1().equals(lll1I1Entry.lll1I1())).findFirst().orElseThrow(() -> new IOException("Product is no longer available."));
            this.handleRunnable(() -> {
                this.o0oO0OEntry = o0oO0OEntry;
                this.entries2 = list;
                this.process3();
            });
            if (lll1I1Entry3.computeBigDecimal(n).compareTo(lll1I1Entry.computeBigDecimal(n)) != 0) {
                throw new IOException("Price changed. Review the updated total and press Buy again.");
            }
            if (lll1I1Entry3.bpf() < n) {
                throw new IOException("Not enough stock. Select a smaller quantity.");
            }
            if (o0oO0OEntry.l1l1().compareTo(lll1I1Entry3.computeBigDecimal(n)) < 0) {
                throw new IOException("Insufficient credits.");
            }
            String string = lll1I1Entry3.III1Il() + " " + lll1I1Entry3.ll1llI() + " " + lll1I1Entry3.oo0oO0() + " " + String.join((CharSequence)" ", lll1I1Entry3.O00O());
            String string2 = localtsApiClient.computeString(lll1I1Entry3.lll1I1(), n);
            this.handleRunnable(() -> {
                this.guiTextField.setText(string2);
                this.status = "Purchase created. Waiting for delivery...";
            });
            this.localtsOrderStore.handleString(string2, string, "");
            this.handleLocaltsApiClient(localtsApiClient, string2, string);
            LocaltsApiClient.O0oO0OEntry o0oO0OEntry2 = localtsApiClient.computeO0oO0OEntry();
            this.handleRunnable(() -> {
                this.o0oO0OEntry = o0oO0OEntry2;
            });
        });
    }

    private String computeString(String string, int n) {
        if (this.drawStringWithShadowComponent.getStringWidth(string = string.replaceAll("[\\r\\n\\t]", " ").replace("\u00a7", "")) <= n) {
            return string;
        }
        int n2 = 0;
        int n3 = string.length();
        while (n2 < n3) {
            int n4 = (n2 + n3 + 1) / 2;
            if (this.drawStringWithShadowComponent.getStringWidth(string.substring(0, n4) + "...") <= n) {
                n2 = n4;
                continue;
            }
            n3 = n4 - 1;
        }
        return string.substring(0, n2) + "...";
    }

    private String getFlag() {
        return "Hide Out of Stock: " + (this.flag ? "On" : "Off");
    }

    @Override
    public void initGui() {
        String string = this.searchField == null ? "" : this.searchField.getText();
        String string2 = this.guiPasswordField == null ? "" : this.guiPasswordField.getText();
        String string3 = this.guiTextField == null ? "" : this.guiTextField.getText();
        this.count7 = Math.max(640, this.width);
        this.count8 = Math.max(360, this.height);
        this.scale = Math.min((float)this.width / (float)this.count7, (float)this.height / (float)this.count8);
        this.offsetX = ((float)this.width - (float)this.count7 * this.scale) / 2.0f;
        this.offsetY = ((float)this.height - (float)this.count8 * this.scale) / 2.0f;
        this.left = Math.max(220, Math.min(310, this.count7 / 3));
        this.right = this.left + 32;
        this.count6 = this.count8 - 36;
        this.localtsOrderStore = new LocaltsOrderStore(GuiLocaltsShop.mc.mcDataDir.toPath());
        this.guiPasswordField = new GuiPasswordField(this.fontRendererObj, 28, 76, this.left - 24, 22);
        this.guiPasswordField.setMaxStringLength(512);
        this.guiPasswordField.setText(string2);
        this.guiTextField = new SearchField(1, 28, 261, this.left - 24, 22);
        this.guiTextField.setMaxStringLength(256);
        this.guiTextField.setText(string3.isEmpty() ? this.localtsOrderStore.computeString() : string3);
        this.searchField = new SearchField(2, this.right + 12, 80, this.count7 - this.right - 40, 22);
        this.searchField.setMaxStringLength(128);
        this.searchField.setText(string);
        this.process3();
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, 28, 104, this.left - 24, 24, "Refresh"));
        this.buttonList.add(new GuiButton(1, 28, 179, 30, 24, "-"));
        this.buttonList.add(new GuiButton(2, this.left - 26, 179, 30, 24, "+"));
        this.buttonList.add(new GuiButton(3, 28, 211, this.left - 24, 28, "Buy & Login"));
        this.buttonList.add(new GuiButton(4, 28, 291, this.left - 24, 24, "Resume Order & Login"));
        this.buttonList.add(new GuiButton(5, this.count7 - 100, 13, 84, 24, "Back"));
        int n = (this.count7 - this.right - 46) / 2;
        this.buttonList.add(new GuiButton(6, this.right + 12, 108, n, 22, this.computeString()));
        this.buttonList.add(new GuiButton(7, this.right + 18 + n, 108, n, 22, this.getFlag()));
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int n = Mouse.getEventDWheel();
        if (n == 0) {
            return;
        }
        int n2 = this.computeInt2(Mouse.getEventX() * this.width / GuiLocaltsShop.mc.displayWidth);
        int n3 = this.computeInt(this.height - Mouse.getEventY() * this.height / GuiLocaltsShop.mc.displayHeight - 1);
        if (n2 >= this.right && n2 < this.count7 - 16 && this.matchesInt(n3)) {
            this.count5 = Math.max(0, Math.min(this.computeInt(), this.count5 + (n > 0 ? -68 : 68)));
        }
    }

    private void process() {
        String string = this.guiTextField.getText().trim();
        if (string.isEmpty()) {
            return;
        }
        this.status = "Resuming delivery...";
        this.handleListener(localtsApiClient -> this.handleLocaltsApiClient(localtsApiClient, string, this.localtsOrderStore.computeString(string)));
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) throws IOException {
        int n4;
        int n5;
        int n6 = this.computeInt2(n);
        int n7 = this.computeInt(n2);
        if (!atomicBoolean.get()) {
            this.guiPasswordField.mouseClicked(n6, n7, n3);
            this.guiTextField.mouseClicked(n6, n7, n3);
            this.searchField.mouseClicked(n6, n7, n3);
        }
        if (n3 != 0) {
            return;
        }
        for (GuiButton guiButton : this.buttonList) {
            if (!guiButton.enabled || n6 < guiButton.xPosition || n6 >= guiButton.xPosition + guiButton.width || n7 < guiButton.yPosition || n7 >= guiButton.yPosition + guiButton.height) continue;
            this.actionPerformed(guiButton);
            return;
        }
        if (!atomicBoolean.get() && n6 >= this.right + 12 && n6 < this.count7 - 28 && this.matchesInt(n7) && (n5 = (n4 = n7 - 140 + (int)Math.round(this.amount)) / 68) < this.entries.size() && n4 % 68 < 60) {
            this.text2 = this.entries.get(n5).lll1I1();
            this.count4 = Math.max(1, Math.min(this.count4, this.entries.get(n5).bpf()));
        }
    }

    private void process3() {
        Object object;
        this.text = this.searchField == null ? "" : this.searchField.getText();
        String string = this.text.trim().toLowerCase(Locale.ROOT);
        ArrayList<LocaltsApiClient.Lll1I1Entry> arrayList = new ArrayList<LocaltsApiClient.Lll1I1Entry>();
        for (LocaltsApiClient.Lll1I1Entry object22 : this.entries2) {
            object = (object22.III1Il() + " " + object22.oo0oO0() + " " + object22.ll1llI() + " " + String.join((CharSequence)" ", object22.O00O())).toLowerCase(Locale.ROOT);
            if (this.flag && object22.bpf() <= 0 || !((String)object).contains(string)) continue;
            arrayList.add(object22);
        }
        Comparator<LocaltsApiClient.Lll1I1Entry> comparator = Comparator.comparing(LocaltsApiClient.Lll1I1Entry::III1Il, String.CASE_INSENSITIVE_ORDER);
        Comparator<LocaltsApiClient.Lll1I1Entry> comparator2 = this.count == 2 ? comparator : (this.count == 1 ? Comparator.comparing(LocaltsApiClient.Lll1I1Entry::Il1I).reversed() : Comparator.comparing(LocaltsApiClient.Lll1I1Entry::Il1I)).thenComparing(comparator);
        arrayList.sort(comparator2);
        this.entries = arrayList;
        this.count5 = 0;
        this.amount = 0;
        if (arrayList.stream().noneMatch(lll1I1Entry -> lll1I1Entry.lll1I1().equals(this.text2))) {
            this.text2 = arrayList.isEmpty() ? "" : ((LocaltsApiClient.Lll1I1Entry)arrayList.get(0)).lll1I1();
            object = this.computeLll1I1Entry();
            this.count4 = object == null ? 1 : Math.max(1, Math.min(this.count4, ((LocaltsApiClient.Lll1I1Entry)object).bpf()));
        }
    }

    private LocaltsApiClient.Lll1I1Entry computeLll1I1Entry() {
        return this.entries2.stream().filter(lll1I1Entry -> lll1I1Entry.lll1I1().equals(this.text2)).findFirst().orElse(null);
    }

    static {
        count3 = 68;
        count2 = 140;
        timestamp2 = 30L;
        executorService = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "Localts orders");
            thread.setDaemon(true);
            return thread;
        });
        atomicBoolean = new AtomicBoolean();
    }

    private void process5() {
        int n = new ScaledResolution(mc).getScaleFactor();
        int n2 = Math.round((this.offsetY + 140.0f * this.scale) * (float)n);
        int n3 = Math.round((this.offsetY + (float)(this.count6 - 8) * this.scale) * (float)n);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(0, GuiLocaltsShop.mc.displayHeight - n3, GuiLocaltsShop.mc.displayWidth, n3 - n2);
    }

    private void handleLocaltsApiClient(LocaltsApiClient localtsApiClient, String string, String string2) throws Exception {
        LocaltsApiClient.OO0Oo0Entry oO0Oo0Entry = localtsApiClient.computeOO0Oo0Entry(string, this::handleString);
        this.localtsOrderStore.handleString(string, string2, oO0Oo0Entry.l1l1().toString());
        int n = 0;
        int n2 = 0;
        for (LocaltsApiClient.LlI1lIEntry llI1lIEntry : oO0Oo0Entry.Il11()) {
            try {
                this.handleString("Logging in delivered account " + (n + n2 + 1) + "/" + oO0Oo0Entry.Il11().size() + "...");
                LocaltsCookieImporter.Il1IEntry il1IEntry = LocaltsCookieImporter.computeIl1IEntry(llI1lIEntry.IIl1lI(), string2);
                boolean bl = n == 0;
                mc.addScheduledTask(() -> {
                    AccountSession accountSession2 = AccountManager.computeArrayList().stream().filter(accountSession -> accountSession.getUsername().equalsIgnoreCase(il1IEntry.Il1I().getUsername())).findFirst().orElse(null);
                    if (accountSession2 == null) {
                        accountSession2 = new AccountSession(il1IEntry.II1llI(), il1IEntry.Il1I().getToken(), il1IEntry.Il1I().getUsername(), System.currentTimeMillis());
                        AccountManager.handleAccountSession(accountSession2);
                    } else {
                        accountSession2.setRefreshToken2(il1IEntry.II1llI());
                        accountSession2.setAccessToken(il1IEntry.Il1I().getToken());
                        accountSession2.setTimestamp(System.currentTimeMillis());
                    }
                    AccountManager.save();
                    if (bl && GuiLocaltsShop.mc.currentScreen == this) {
                        SessionHolder.setSession(il1IEntry.Il1I());
                        AccountManager.setAccountSession(accountSession2);
                    }
                }).get(timestamp2, TimeUnit.SECONDS);
                ++n;
            }
            catch (Exception exception) {
                ++n2;
            }
        }
        this.handleString(n + " account(s) saved" + (String)(n2 == 0 ? "." : "; " + n2 + " need manual login. Delivery saved in Brutality/localts/orders."));
    }

    @Override
    public void actionPerformed(GuiButton guiButton) {
        switch (guiButton.id) {
            case 0: {
                this.process6();
                break;
            }
            case 1: {
                this.count4 = Math.max(1, this.count4 - 1);
                break;
            }
            case 2: {
                LocaltsApiClient.Lll1I1Entry lll1I1Entry = this.computeLll1I1Entry();
                if (lll1I1Entry == null) break;
                this.count4 = Math.min(lll1I1Entry.bpf(), this.count4 + 1);
                break;
            }
            case 3: {
                this.process4();
                break;
            }
            case 4: {
                this.process();
                break;
            }
            case 5: {
                mc.displayGuiScreen(this.guiScreen);
                break;
            }
            case 6: {
                this.count = (this.count + 1) % 3;
                guiButton.displayString = this.computeString();
                this.process3();
                break;
            }
            case 7: {
                this.flag = !this.flag;
                guiButton.displayString = this.getFlag();
                this.process3();
            }
        }
    }

    private void process6() {
        this.status = "Connecting...";
        this.handleListener(localtsApiClient -> {
            LocaltsApiClient.O0oO0OEntry o0oO0OEntry = localtsApiClient.computeO0oO0OEntry();
            List<LocaltsApiClient.Lll1I1Entry> list = localtsApiClient.computeList();
            this.handleRunnable(() -> {
                this.o0oO0OEntry = o0oO0OEntry;
                this.entries2 = list;
                this.process3();
                this.status = this.flag2 ? "Connected. Check the previous purchase in Localts orders." : "Connected. Select a product to buy.";
            });
        });
    }

    private int computeInt(int n) {
        return (int)(((float)n - this.offsetY) / this.scale);
    }

    @Override
    public void updateScreen() {
        this.guiPasswordField.updateCursorCounter();
        this.guiTextField.updateCursorCounter();
        this.searchField.updateCursorCounter();
    }

    private void handleRunnable(Runnable runnable) {
        mc.addScheduledTask(runnable);
    }

    public static interface Listener {
        public void run(LocaltsApiClient client) throws Exception;
    }
}
