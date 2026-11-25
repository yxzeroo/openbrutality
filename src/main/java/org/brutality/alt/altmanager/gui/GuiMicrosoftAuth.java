package org.brutality.alt.altmanager.gui;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.brutality.alt.altmanager.AccountManager;
import org.brutality.alt.altmanager.auth.AccountSession;
import org.brutality.alt.altmanager.auth.MicrosoftAuthenticator;
import org.brutality.alt.altmanager.auth.SessionHolder;
import org.brutality.alt.altmanager.utils.StatusToast;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.StyledGuiButton;
import org.brutality.gui.OceanComponent;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;

public class GuiMicrosoftAuth extends CustomGuiScreen {
    private final GuiScreen guiScreen;
    private GuiButton guiButton = null;
    private GuiButton guiButton2 = null;
    private String status = null;
    private String cause = null;
    private ExecutorService executor = null;
    private CompletableFuture<Void> completableFuture = null;
    private static final long timestamp = 5000L;

    @Override
    public void initGui() {
        this.buttonList.clear();
        int n = this.height / 2 + computeDrawStringWithShadowComponent().FONT_HEIGHT / 2 + computeDrawStringWithShadowComponent().FONT_HEIGHT;
        this.buttonList.add(this.guiButton = new StyledGuiButton(0, this.width / 2 - 100, n, "Back"));
        this.buttonList.add(this.guiButton2 = new StyledGuiButton(1, this.width / 2 - 100, n + 24, "Retry"));
        this.guiButton2.visible = this.cause != null;
        if (this.completableFuture == null) {
            if (this.executor == null) {
                this.executor = Executors.newSingleThreadExecutor();
            }

            AtomicReference atomicReference = new AtomicReference<>("");
            AtomicReference atomicReference2 = new AtomicReference<>("");
            this.status = "§fCheck your browser to continue...§r";
            this.completableFuture = MicrosoftAuthenticator.computeCompletableFuture(this.executor)
                .thenComposeAsync(str -> {
                    this.status = "§fAcquiring Microsoft access tokens§r";
                    return MicrosoftAuthenticator.acquireMSAccessTokens(str, this.executor);
                })
                .thenComposeAsync(map -> {
                    this.status = "§fAcquiring Xbox access token§r";
                    atomicReference.set(map.get("refresh_token"));
                    return MicrosoftAuthenticator.acquireXboxAccessToken(map.get("access_token"), this.executor);
                })
                .thenComposeAsync(str2 -> {
                    this.status = "§fAcquiring Xbox XSTS token§r";
                    return MicrosoftAuthenticator.acquireXboxXstsToken(str2, this.executor);
                })
                .thenComposeAsync(map2 -> {
                    this.status = "§fAcquiring Minecraft access token§r";
                    return MicrosoftAuthenticator.acquireMCAccessToken(map2.get("Token"), map2.get("uhs"), this.executor);
                })
                .thenComposeAsync(str3 -> {
                    this.status = "§fFetching your Minecraft profile§r";
                    atomicReference2.set(str3);
                    return MicrosoftAuthenticator.login(str3, this.executor);
                })
                .thenAccept(
                    session -> {
                        SessionHolder.setSession(session);
                        mc.addScheduledTask(
                            () -> {
                                StatusToast statusToast = new StatusToast(String.format("§aSuccessful login! (%s)§r", session.getUsername()), timestamp);
                                if (this.guiScreen instanceof GuiNfasAltManager gui) {
                                    gui.handleString(session.getUsername(), session.getPlayerID(), (String)atomicReference2.get());
                                    mc.displayGuiScreen(new GuiNfasAltManager(gui.getGuiScreen(), statusToast));
                                } else {
                                    AccountManager.handleAccountSession(
                                        new AccountSession((String)atomicReference.get(), (String)atomicReference2.get(), session.getUsername(), System.currentTimeMillis())
                                    );
                                    AccountManager.save();
                                    mc.displayGuiScreen(new GuiAltManager(this.guiScreen, statusToast));
                                }
                            }
                        );
                    }
                )
                .exceptionally(t -> {
                    Throwable t2 = t.getCause() != null ? t.getCause() : t;
                    this.status = String.format("§c%s§r", t.getMessage());
                    this.cause = String.format("§c%s§r", t2.getMessage());
                    mc.addScheduledTask(() -> {
                        if (this.guiButton2 != null) {
                            this.guiButton2.visible = true;
                        }
                    });
                    return null;
                });
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            this.actionPerformed(this.guiButton);
        }
    }

    private static CustomFontRenderer computeDrawStringWithShadowComponent() {
        return FontComponent.computeDrawStringWithShadowComponent();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        OceanComponent.handleGuiScreen(this, mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(
            computeDrawStringWithShadowComponent(),
            "Microsoft Authentication",
            this.width / 2,
            this.height / 2 - computeDrawStringWithShadowComponent().FONT_HEIGHT / 2 - computeDrawStringWithShadowComponent().FONT_HEIGHT * 2,
            16777215
        );
        if (this.status != null) {
            this.drawCenteredString(
                computeDrawStringWithShadowComponent(),
                this.status,
                this.width / 2,
                this.height / 2 - computeDrawStringWithShadowComponent().FONT_HEIGHT / 2,
                -1
            );
        }

        if (this.cause != null) {
            String cause = this.cause;
            Gui.drawRect(
                0,
                this.height - 2 - computeDrawStringWithShadowComponent().FONT_HEIGHT - 3,
                3 + computeDrawStringWithShadowComponent().getStringWidth(cause) + 3,
                this.height,
                1677721600
            );
            this.drawString(computeDrawStringWithShadowComponent(), this.cause, 3, this.height - 2 - computeDrawStringWithShadowComponent().FONT_HEIGHT, -1);
        }
    }

    @Override
    public void onGuiClosed() {
        if (this.completableFuture != null && !this.completableFuture.isDone()) {
            this.completableFuture.cancel(true);
            this.executor.shutdownNow();
        }
    }

    public GuiMicrosoftAuth(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button != null && button.id == 0) {
            if (this.guiScreen instanceof GuiNfasAltManager) {
                mc.displayGuiScreen(this.guiScreen);
            } else {
                mc.displayGuiScreen(new GuiAltManager(this.guiScreen));
            }
        } else if (button != null && button.id == 1) {
            this.completableFuture = null;
            this.status = null;
            this.cause = null;
            this.executor = null;
            if (this.guiButton2 != null) {
                this.guiButton2.visible = false;
            }

            this.initGui();
        }
    }
}
