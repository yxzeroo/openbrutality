package org.brutality.alt.altmanager.gui;

import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.Session;
import org.brutality.alt.alt.cookie.CookieAuth;
import org.brutality.alt.altmanager.Account;
import org.brutality.alt.altmanager.AccountManager;
import org.brutality.alt.altmanager.auth.AccountSession;
import org.brutality.alt.altmanager.auth.SessionHolder;
import org.brutality.alt.altmanager.utils.StatusToast;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.lwjgl.input.Keyboard;

public class GuiCookieLogin extends CustomGuiScreen {
    private static final int count = -11716;
    private static final int count2 = -29696;
    private static final int count3 = -8701952;
    private static final int count4 = -7829368;
    private final GuiScreen guiScreen;
    private final CustomFontRenderer drawStringWithShadowComponent;
    private volatile File file;
    private volatile String status = null;
    private volatile boolean flag = false;
    private static final long timestamp = 5000L;

    private void handleFile2(File file) {
        this.file = file.isFile() && file.canRead() ? file : null;
        this.status = this.file != null ? "Cookie file ready - press Login" : "Couldn't read cookies from that file";
    }

    @Override
    public void onGuiClosed() {
        CookieFilePicker.close();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        short s = 170;
        int n = this.width / 2 - s / 2;
        int n2 = this.height / 2 - 20;
        this.buttonList.add(new GuiAltButton(0, n, n2, s, 20, "Select Cookie File", -8701952, -29696));
        this.buttonList.add(new GuiAltButton(1, n, n2 + 26, s, 20, "Drag & Drop", -8701952, -29696));
        this.buttonList.add(new GuiAltButton(2, n, n2 + 52, s, 20, "Login", -8701952, -29696));
        this.buttonList.add(new GuiAltButton(3, n, n2 + 86, s, 20, "Back", -7829368, -11716));
    }

    private void handleAccount(Account account) {
        try {
            if (mc.currentScreen == this) {
                StatusToast statusToast = new StatusToast("§aSuccessful cookie login! (" + account.username + ")§r", timestamp);
                SessionHolder.setSession(new Session(account.username, account.text3, account.text, "mojang"));
                if (this.guiScreen instanceof GuiNfasAltManager gui) {
                    gui.handleString2(account.username, account.text3, account.text);
                    mc.displayGuiScreen(new GuiNfasAltManager(gui.getGuiScreen(), statusToast));
                } else {
                    AccountManager.handleAccountSession(new AccountSession("", account.text, account.username, System.currentTimeMillis()));
                    AccountManager.save();
                    mc.displayGuiScreen(new GuiAltManager(this.guiScreen, statusToast));
                }

                return;
            }
        } catch (LinkageError | Exception e) {
            this.status = "Could not save or apply the cookie login";
            return;
        } finally {
            this.flag = false;
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button != null) {
            if (!this.flag || button.id == 3) {
                switch (button.id) {
                    case 0:
                        this.process();
                        break;
                    case 1:
                        this.process3();
                        break;
                    case 2:
                        this.process2();
                        break;
                    case 3:
                        mc.displayGuiScreen(this.guiScreen);
                }
            }
        }
    }

    private void process() {
        boolean fullScreen = mc.isFullScreen();
        if (fullScreen) {
            mc.toggleFullscreen();
        }

        this.status = "Choosing a cookie file...";
        EventQueue.invokeLater(() -> {
            FileDialog fileDialog = null;

            try {
                fileDialog = new FileDialog((Frame)null, "Select Cookie File");
                fileDialog.setMode(0);
                fileDialog.setVisible(true);
                String file = fileDialog.getFile();
                String directory = fileDialog.getDirectory();
                if (file != null && directory != null) {
                    this.handleFile2(new File(directory, file));
                } else {
                    this.status = null;
                }
            } catch (LinkageError | Exception e) {
                this.status = "Error loading cookie file";
            } finally {
                if (fileDialog != null) {
                    fileDialog.dispose();
                }

                if (fullScreen) {
                    mc.addScheduledTask(() -> {
                        if (mc.currentScreen == this && !mc.isFullScreen()) {
                            mc.toggleFullscreen();
                        }
                    });
                }
            }
        });
    }

    private void process2() {
        File file = this.file;
        if (file == null) {
            this.status = "No cookie file loaded";
        } else if (!this.flag) {
            this.flag = true;
            this.status = "Logging in with cookies...";
            Thread thread = new Thread(() -> this.handleFile(file), "Cookie-Login-Thread");
            thread.setDaemon(true);
            thread.start();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        String str = "Cookie Login";
        this.drawStringWithShadowComponent
            .drawStringWithShadow(str, this.width / 2.0F - this.drawStringWithShadowComponent.getStringWidth(str) / 2.0F, this.height / 2.0F - 60.0F, -11716);
        String str2 = this.file == null ? "Select or drag in a cookies file to log in" : "Cookie file loaded - press Login";
        this.drawStringWithShadowComponent
            .drawStringWithShadow(str2, this.width / 2.0F - this.drawStringWithShadowComponent.getStringWidth(str2) / 2.0F, this.height / 2.0F - 45.0F, -29696);
        String status = this.status;
        if (status != null) {
            this.drawStringWithShadowComponent
                .drawStringWithShadow(
                    status, this.width / 2.0F - this.drawStringWithShadowComponent.getStringWidth(status) / 2.0F, this.height / 2.0F - 32.0F, -11716
                );
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void handleFile(File file) {
        try {
            Account account = CookieAuth.instance.computeAccount2(file);
            mc.addScheduledTask(() -> this.handleAccount(account));
        } catch (IOException e) {
            this.status = e.getMessage();
            this.flag = false;
        } catch (LinkageError | Exception e2) {
            this.status = "Cookie login could not start";
            this.flag = false;
        }
    }

    public GuiCookieLogin(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            mc.displayGuiScreen(this.guiScreen);
        }
    }

    private void process3() {
        this.status = "Drop your cookies file on the window";
        CookieFilePicker.open(this::handleFile2);
    }
}
