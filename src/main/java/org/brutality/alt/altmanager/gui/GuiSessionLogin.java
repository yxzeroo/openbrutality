package org.brutality.alt.altmanager.gui;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.Session;
import org.brutality.alt.altmanager.auth.MicrosoftAuthenticator;
import org.brutality.alt.altmanager.auth.SessionHolder;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.SearchField;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.lwjgl.input.Keyboard;

public class GuiSessionLogin extends CustomGuiScreen {
    private final GuiScreen guiScreen;
    private final CustomFontRenderer drawStringWithShadowComponent;
    private GuiTextField guiTextField;
    private String status;
    private ExecutorService executor;
    private CompletableFuture<MicrosoftAuthenticator.Entry> completableFuture;
    private static final long timestamp = 1712867420473567914L;

    public GuiSessionLogin(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.guiTextField = new SearchField(1, this.width / 2 - 100, this.height / 2, 200, 20);
        this.guiTextField.setMaxStringLength(32767);
        this.guiTextField.setFocused(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + 100, "Cancel"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 + 25, "Log In"));
    }

    @Override
    public void onGuiClosed() {
        if (this.executor != null && !this.executor.isShutdown()) {
            this.executor.shutdownNow();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawStringWithShadowComponent.drawStringWithShadow("Refresh Token Login", this.width / 2 - 50, this.height / 2 - 40, -1);
        this.drawStringWithShadowComponent.drawStringWithShadow("Paste Microsoft refresh token:", this.width / 2 - 100, this.height / 2 - 20, (int)timestamp);
        this.guiTextField.drawTextBox();
        if (this.status != null) {
            this.drawStringWithShadowComponent.drawStringWithShadow(this.status, this.width / 2 - 100, this.height / 2 - 10, -1);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button != null) {
            if (button.id == 0) {
                mc.displayGuiScreen(this.guiScreen);
            } else {
                if (button.id == 1) {
                    if (this.executor != null && !this.executor.isShutdown()) {
                        this.executor.shutdownNow();
                    }

                    String str = this.guiTextField.getText().trim();
                    if (str.isEmpty()) {
                        this.status = "§cPlease paste a refresh token";
                        return;
                    }

                    this.executor = Executors.newSingleThreadExecutor();
                    this.status = "§eRefreshing token...";
                    this.completableFuture = MicrosoftAuthenticator.loginWithRefreshToken(str, this.executor);
                    this.completableFuture.thenAccept(entry -> {
                        Session session = entry.session;
                        SessionHolder.setSession(session);
                        if (this.guiScreen instanceof GuiNfasAltManager) {
                            ((GuiNfasAltManager)this.guiScreen).handleString(session.getUsername(), session.getPlayerID(), session.getToken());
                        }

                        this.status = "§2Logged in as " + session.getUsername();
                    }).exceptionally(t -> {
                        String str2 = t.getCause() != null ? t.getCause().getMessage() : t.getMessage();
                        this.status = "§c" + (str2 != null ? str2 : "Authentication failed");
                        return null;
                    });
                }
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        this.guiTextField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == 1) {
            mc.displayGuiScreen(this.guiScreen);
        }
    }
}
