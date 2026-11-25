package org.brutality.alt.altmanager.gui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.brutality.alt.altmanager.auth.MinecraftTokenAuth;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.SearchField;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.gui.font.FontComponent;
import org.lwjgl.input.Keyboard;

public class GuiTokenLogin extends CustomGuiScreen {
    private final GuiScreen guiScreen;
    private final CustomFontRenderer drawStringWithShadowComponent;
    private GuiButton guiButton = null;
    private GuiButton guiButton2 = null;
    private GuiTextField guiTextField = null;
    private String status = null;
    private String cause = null;
    private ExecutorService executor = null;
    private CompletableFuture<Void> completableFuture = null;

    @Override
    public void onGuiClosed() {
        if (this.completableFuture != null && !this.completableFuture.isDone()) {
            this.completableFuture.cancel(true);
            this.executor.shutdownNow();
        }
    }

    public GuiTokenLogin(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
        this.drawStringWithShadowComponent = FontComponent.computeDrawStringWithShadowComponent();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.guiTextField.drawTextBox();
        if (this.status != null) {
            this.drawStringWithShadowComponent.drawStringWithShadow(this.status, this.width / 2 - 100, this.height / 2 - 20, -1);
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button != null && button.id == 0) {
            mc.displayGuiScreen(this.guiScreen);
        }

        if (button != null && button.id == 1) {
            String text = this.guiTextField.getText();
            if (text == null || text.trim().isEmpty()) {
                this.status = "§4Enter a session or access token";
                return;
            }

            this.status = "§eValidating session...";
            new Thread(() -> {
                MinecraftTokenAuth.setConsumer(str -> this.status = "§e" + str);

                try {
                    MinecraftTokenAuth.Entry entry = MinecraftTokenAuth.computeEntry2(text);
                    if (entry.flag) {
                        if (this.guiScreen instanceof GuiNfasAltManager) {
                            ((GuiNfasAltManager)this.guiScreen).handleString(entry.username, entry.text, entry.text2);
                        }

                        this.status = "§2Logged in as " + entry.username;
                        return;
                    }

                    this.status = "§4" + (entry.error != null ? entry.error : "Invalid token");
                } finally {
                    MinecraftTokenAuth.setConsumer(null);
                }
            }, "Session-Token-Login").start();
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        this.guiTextField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == 1) {
            this.actionPerformed(this.guiButton);
        }
    }

    public static String[] computeStringArray(String str) throws IOException {
        String[] parts;
        try (CloseableHttpClient closeableHttpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet("https://api.minecraftservices.com/minecraft/profile");
            httpGet.setHeader("Authorization", "Bearer " + str);

            try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet)) {
                String str2 = EntityUtils.toString(closeableHttpResponse.getEntity(), StandardCharsets.UTF_8);
                JsonObject asJsonObject = new JsonParser().parse(str2).getAsJsonObject();
                if (!asJsonObject.has("name") || !asJsonObject.has("id")) {
                    throw new IOException("Invalid token or unauthorized response: " + str2);
                }

                String asString = asJsonObject.get("name").getAsString();
                String asString2 = asJsonObject.get("id").getAsString();
                parts = new String[]{asString, asString2};
            }
        }

        return parts;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.guiTextField = new SearchField(1, this.width / 2 - 100, this.height / 2, 200, 20);
        this.guiTextField.setMaxStringLength(32767);
        this.guiTextField.setFocused(true);
        this.buttonList.clear();
        this.buttonList.add(this.guiButton = new GuiButton(0, this.width / 2 - 100, this.height / 2 + 100, "Cancel"));
        this.buttonList.add(this.guiButton2 = new GuiButton(1, this.width / 2 - 100, this.height / 2 + 25, "Log In"));
    }
}
