package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brutality.Brutality;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.Module;
// VENDOR-DISABLED: import org.brutality.module.impl.client.IRC;
import org.brutality.utils.chat.ListConfigNamesHelper;
import org.brutality.utils.render.FontResourceLoader;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiChat extends GuiScreen {
    private static final Logger logger = LogManager.getLogger();
    private String historyBuffer = "";
    private int sentHistoryCursor = -1;
    private boolean playerNamesFound;
    private boolean waitingOnAutocomplete;
    private int autocompleteIndex;
    private List<String> foundPlayerNames = Lists.newArrayList();
    protected GuiTextField inputField;
    private String defaultInputFieldText = "";
    private static final String[] CHANNELS = new String[]{"ALL", "PARTY", "GUILD", "PM", "IRC"};
    private static final String[] CHANNEL_PREFIXES = new String[]{"", "/pc ", "/gc ", "/msg "};
    private static final int CHANNEL_IRC = 4;
    private static int selectedChannel = 0;
    private static final int CHANNEL_HEIGHT = 12;
    private static final int SEARCH_WIDTH = 100;
    private static final int CHANNEL_ROW_LIFT = 2;
    private GuiTextField searchField;
    private List<String> commandMatches = Lists.newArrayList();
    private int commandSelectIndex = 0;
    private int commandListScroll = 0;
    private String lastCommandInputForSuggestions = "";

    public GuiChat() {
    }

    public GuiChat(String defaultText) {
        this.defaultInputFieldText = defaultText;
    }

    public GuiTextField getInputField() {
        return this.inputField;
    }

    private int channelRowY() {
        return this.height - 25 - 2;
    }

    private int searchRowY() {
        return this.height - 26 - 2;
    }

    private int searchX() {
        return this.width - 100 - 4;
    }

    private String getIrcChatTrigger() {
        // VENDOR-DISABLED: IRC module chat trigger lookup

        return "#";
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.sentHistoryCursor = mc.ingameGUI.getChatGUI().getSentMessages().size();
        this.inputField = new GuiTextField(0, this.fontRendererObj, 4, this.height - 12, this.width - 4, 12);
        this.inputField.setMaxStringLength(100);
        this.inputField.setEnableBackgroundDrawing(false);
        this.inputField.setFocused(true);
        this.inputField.setText(this.defaultInputFieldText);
        this.inputField.setCanLoseFocus(true);
        this.searchField = new GuiTextField(1, this.fontRendererObj, this.searchX() + 2, this.searchRowY(), 100, 12);
        this.searchField.setMaxStringLength(50);
        this.searchField.setEnableBackgroundDrawing(false);
        this.searchField.setFocused(false);
        this.searchField.setCanLoseFocus(true);
        this.refreshCommandSuggestions();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        mc.ingameGUI.getChatGUI().resetScroll();
        mc.ingameGUI.getChatGUI().setSearchFilter("");
    }

    @Override
    public void updateScreen() {
        this.inputField.updateCursorCounter();
        this.searchField.updateCursorCounter();
        this.refreshCommandSuggestions();
    }

    private boolean isClientCommandInput() {
        String text = this.inputField.getText();
        return text != null && text.startsWith(".");
    }

    private boolean showingCommandHelper() {
        return this.isClientCommandInput() && !this.commandMatches.isEmpty() && !this.searchField.isFocused();
    }

    private void refreshCommandSuggestions() {
        if (!this.isClientCommandInput()) {
            this.commandMatches = Lists.newArrayList();
            this.commandSelectIndex = 0;
            this.commandListScroll = 0;
            this.lastCommandInputForSuggestions = "";
        } else {
            String text = this.inputField.getText();
            if (!text.equals(this.lastCommandInputForSuggestions)) {
                this.commandSelectIndex = 0;
                this.lastCommandInputForSuggestions = text;
            }

            this.commandMatches = ListConfigNamesHelper.computeList2(text);
            if (this.commandSelectIndex >= this.commandMatches.size()) {
                this.commandSelectIndex = Math.max(0, this.commandMatches.size() - 1);
            }

            if (ListConfigNamesHelper.computeString(text, this.commandMatches, this.commandSelectIndex) == null && !this.commandMatches.isEmpty()) {
                this.commandSelectIndex = 0;
            }

            if (this.commandListScroll > Math.max(0, this.commandMatches.size() - this.getVisibleCommandRows())) {
                this.commandListScroll = Math.max(0, this.commandMatches.size() - this.getVisibleCommandRows());
            }
        }
    }

    private int getVisibleCommandRows() {
        return 8;
    }

    private void autocompleteClientCommand() {
        if (!this.commandMatches.isEmpty()) {
            if (this.commandMatches.size() == 1) {
                this.inputField.setText(this.commandMatches.get(0) + " ");
                this.inputField.setCursorPositionEnd();
                this.refreshCommandSuggestions();
            } else {
                String str = ListConfigNamesHelper.computeString(this.commandMatches);
                String text = this.inputField.getText();
                if (str.length() > text.length()) {
                    this.inputField.setText(str);
                    this.inputField.setCursorPositionEnd();
                    this.refreshCommandSuggestions();
                } else {
                    String str2 = this.commandMatches.get(this.commandSelectIndex % this.commandMatches.size());
                    this.inputField.setText(str2 + " ");
                    this.inputField.setCursorPositionEnd();
                    this.refreshCommandSuggestions();
                }
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        this.waitingOnAutocomplete = false;
        if (this.searchField.isFocused()) {
            if (keyCode == 1) {
                mc.displayGuiScreen(null);
            } else if (keyCode != 28 && keyCode != 156) {
                this.searchField.textboxKeyTyped(typedChar, keyCode);
                mc.ingameGUI.getChatGUI().setSearchFilter(this.searchField.getText());
            } else {
                this.searchField.setFocused(false);
                this.inputField.setFocused(true);
            }
        } else {
            if (this.showingCommandHelper()) {
                if (keyCode == 200) {
                    if (this.commandSelectIndex > 0) {
                        this.commandSelectIndex--;
                    }

                    if (this.commandSelectIndex < this.commandListScroll) {
                        this.commandListScroll = this.commandSelectIndex;
                    }

                    return;
                }

                if (keyCode == 208) {
                    if (this.commandSelectIndex < this.commandMatches.size() - 1) {
                        this.commandSelectIndex++;
                    }

                    int n = Math.max(0, this.commandMatches.size() - this.getVisibleCommandRows());
                    if (this.commandSelectIndex >= this.commandListScroll + this.getVisibleCommandRows()) {
                        this.commandListScroll = Math.min(n, this.commandSelectIndex - this.getVisibleCommandRows() + 1);
                    }

                    return;
                }

                if (keyCode == 15) {
                    this.autocompleteClientCommand();
                    return;
                }
            }

            if (keyCode == 15) {
                if (this.isClientCommandInput()) {
                    this.autocompleteClientCommand();
                } else {
                    this.autocompletePlayerNames();
                }
            } else {
                this.playerNamesFound = false;
            }

            if (keyCode == 1) {
                mc.displayGuiScreen(null);
            } else if (keyCode == 28 || keyCode == 156) {
                String str = this.inputField.getText().trim();
                if (str.length() > 0) {
                    if (!str.startsWith("/") && !str.startsWith(".")) {
                        if (selectedChannel == 4) {
                            String ircChatTrigger = this.getIrcChatTrigger();
                            if (!str.startsWith(ircChatTrigger)) {
                                str = ircChatTrigger + str;
                            }
                        } else if (selectedChannel > 0) {
                            str = CHANNEL_PREFIXES[selectedChannel] + str;
                        }
                    }

                    this.sendChatMessage(str);
                }

                mc.displayGuiScreen(null);
            } else if (keyCode == 200) {
                this.getSentHistory(-1);
            } else if (keyCode == 208) {
                this.getSentHistory(1);
            } else if (keyCode == 201) {
                mc.ingameGUI.getChatGUI().scroll(mc.ingameGUI.getChatGUI().getLineCount() - 1);
            } else if (keyCode == 209) {
                mc.ingameGUI.getChatGUI().scroll(-mc.ingameGUI.getChatGUI().getLineCount() + 1);
            } else {
                this.inputField.textboxKeyTyped(typedChar, keyCode);
                this.refreshCommandSuggestions();
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int eventDWheel = Mouse.getEventDWheel();
        if (eventDWheel != 0) {
            if (eventDWheel > 1) {
                eventDWheel = 1;
            }

            if (eventDWheel < -1) {
                eventDWheel = -1;
            }

            if (!isShiftKeyDown()) {
                eventDWheel *= 7;
            }

            mc.ingameGUI.getChatGUI().scroll(eventDWheel);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        GuiNewChat chatGUI = mc.ingameGUI.getChatGUI();
        if (mouseButton == 0) {
            CustomFontRenderer drawStringWithShadowComponent = FontResourceLoader.computeDrawStringWithShadowComponent2("inter.18pt-semibold", 14.0F);
            int n = this.channelRowY();
            int n2 = 4;

            for (int i = 0; i < CHANNELS.length; i++) {
                int n3 = (drawStringWithShadowComponent != null ? drawStringWithShadowComponent.getStringWidth(CHANNELS[i]) : this.fontRendererObj.getStringWidth(CHANNELS[i])) + 8;
                if (mouseX >= n2 && mouseX <= n2 + n3 && mouseY >= n && mouseY <= n + 12) {
                    selectedChannel = i;
                    return;
                }

                n2 += n3 + 2;
            }

            if (chatGUI.tryHandleCopyClick(0)) {
                return;
            }

            IChatComponent chatComponent = chatGUI.getChatComponent(Mouse.getX(), Mouse.getY());
            if (this.handleComponentClick(chatComponent)) {
                return;
            }
        } else if (mouseButton == 1 && chatGUI.tryHandleCopyClick(1)) {
            return;
        }

        this.inputField.mouseClicked(mouseX, mouseY, mouseButton);
        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.searchField.isFocused()) {
            this.inputField.setFocused(false);
        } else if (!this.inputField.isFocused()) {
            this.inputField.setFocused(true);
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void setText(String newChatText, boolean shouldOverwrite) {
        if (shouldOverwrite) {
            this.inputField.setText(newChatText);
        } else {
            this.inputField.writeText(newChatText);
        }

        this.refreshCommandSuggestions();
    }

    public void autocompletePlayerNames() {
        if (this.playerNamesFound) {
            this.inputField
                .deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
            if (this.autocompleteIndex >= this.foundPlayerNames.size()) {
                this.autocompleteIndex = 0;
            }
        } else {
            int n = this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false);
            this.foundPlayerNames.clear();
            this.autocompleteIndex = 0;
            String str = this.inputField.getText().substring(n).toLowerCase();
            String str2 = this.inputField.getText().substring(0, this.inputField.getCursorPosition());
            this.sendAutocompleteRequest(str2, str);
            if (this.foundPlayerNames.isEmpty()) {
                return;
            }

            this.playerNamesFound = true;
            this.inputField.deleteFromCursor(n - this.inputField.getCursorPosition());
        }

        if (this.foundPlayerNames.size() > 1) {
            StringBuilder sb = new StringBuilder();

            for (String foundPlayerName : this.foundPlayerNames) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }

                sb.append(foundPlayerName);
            }

            mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(sb.toString()), 1);
        }

        this.inputField.writeText(this.foundPlayerNames.get(this.autocompleteIndex++));
    }

    private void sendAutocompleteRequest(String p_146405_1_, String p_146405_2_) {
        if (p_146405_1_.length() >= 1) {
            BlockPos pos = null;
            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
                pos = mc.objectMouseOver.getBlockPos();
            }

            mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(p_146405_1_, pos));
            this.waitingOnAutocomplete = true;
        }
    }

    public void getSentHistory(int msgPos) {
        int n = this.sentHistoryCursor + msgPos;
        int size = mc.ingameGUI.getChatGUI().getSentMessages().size();
        int n2 = MathHelper.clamp_int(n, 0, size);
        if (n2 != this.sentHistoryCursor) {
            if (n2 == size) {
                this.sentHistoryCursor = size;
                this.inputField.setText(this.historyBuffer);
            } else {
                if (this.sentHistoryCursor == size) {
                    this.historyBuffer = this.inputField.getText();
                }

                this.inputField.setText(mc.ingameGUI.getChatGUI().getSentMessages().get(n2));
                this.sentHistoryCursor = n2;
            }

            this.refreshCommandSuggestions();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
        if (this.isClientCommandInput()) {
            float f = (float)(Math.sin(System.currentTimeMillis() / 380.0) * 0.5 + 0.5);
            int n = (int)(170.0F + 85.0F * f);
            int n2 = 0xFF000000 | n << 16;
            int n3 = 1;
            int n4 = this.height - 15;
            int n5 = this.width - 1;
            int n6 = this.height - 1;
            drawRect(n3, n4, n5, n4 + 1, n2);
            drawRect(n3, n6 - 1, n5, n6, n2);
            drawRect(n3, n4, n3 + 1, n6, n2);
            drawRect(n5 - 1, n4, n5, n6, n2);
        }

        this.drawChatInputField();
        this.drawAutocompleteHint();
        CustomFontRenderer drawStringWithShadowComponent = FontResourceLoader.computeDrawStringWithShadowComponent2("inter.18pt-semibold", 14.0F);
        int n7 = this.channelRowY();
        int n8 = 4;

        for (int i = 0; i < CHANNELS.length; i++) {
            int n9 = (drawStringWithShadowComponent != null ? drawStringWithShadowComponent.getStringWidth(CHANNELS[i]) : this.fontRendererObj.getStringWidth(CHANNELS[i])) + 8;
            int n10 = i == selectedChannel ? -1440586070 : -1439485133;
            drawRect(n8, n7, n8 + n9, n7 + 12, n10);
            int n11 = i == selectedChannel ? -1 : -5592406;
            if (drawStringWithShadowComponent != null) {
                drawStringWithShadowComponent.drawStringWithShadow(CHANNELS[i], n8 + 4, n7 + 2, n11);
            } else {
                this.fontRendererObj.drawStringWithShadow(CHANNELS[i], n8 + 4, n7 + 1, n11);
            }

            n8 += n9 + 2;
        }

        int n12 = this.searchX();
        int n13 = this.searchRowY();
        drawRect(n12 - 1, n13, n12 + 100 + 1, n13 + 12, -1439485133);
        String text = this.searchField.getText();
        if (text.isEmpty() && !this.searchField.isFocused()) {
            int n14 = (int)(System.currentTimeMillis() / 400L % 4L);
            String str = "§7Search" + ".".repeat(n14);
            if (drawStringWithShadowComponent != null) {
                drawStringWithShadowComponent.drawStringWithShadow(str, n12 + 2, n13 + 2, -7829368);
            } else {
                this.fontRendererObj.drawStringWithShadow(str, n12 + 2, n13 + 1, -7829368);
            }
        }

        this.searchField.yPosition = n13;
        this.searchField.xPosition = n12 + 2;
        this.searchField.drawTextBox();
        this.drawCommandHelper(drawStringWithShadowComponent);
        IChatComponent chatComponent = mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
        if (chatComponent != null && chatComponent.getChatStyle().getChatHoverEvent() != null) {
            this.handleComponentHover(chatComponent, mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawCommandHelper(CustomFontRenderer interFont) {
        if (this.showingCommandHelper()) {
            byte b = 11;
            byte b2 = 13;
            int visibleCommandRows = this.getVisibleCommandRows();
            byte b3 = 118;
            int n = b2 + visibleCommandRows * b + 6;
            int n2 = 4;
            int n3 = this.channelRowY() - n - 4;
            drawRect(n2, n3, n2 + b3, n3 + n, -535818220);
            drawRect(n2, n3, n2 + b3, n3 + 1, -14013902);
            String str = "Commands";
            if (!this.commandMatches.isEmpty()) {
                String str2 = this.commandMatches.get(0);
                if (str2.indexOf(32) > 1) {
                    str = "Options";
                }
            }

            if (interFont != null) {
                interFont.drawStringWithShadow(str, n2 + 5, n3 + 3, -1);
            } else {
                this.fontRendererObj.drawStringWithShadow(str, n2 + 5, n3 + 3, -1);
            }

            int n4 = n3 + b2;

            for (int i = 0; i < visibleCommandRows; i++) {
                int n5 = this.commandListScroll + i;
                if (n5 >= this.commandMatches.size()) {
                    break;
                }

                String str3 = this.commandMatches.get(n5);
                boolean flag = n5 == this.commandSelectIndex;
                if (flag) {
                    drawRect(n2 + 2, n4 + i * b - 1, n2 + b3 - 2, n4 + i * b + b - 1, -14011056);
                }

                int n6 = flag ? -1 : -5197648;
                if (interFont != null) {
                    interFont.drawStringWithShadow(str3, n2 + 5, n4 + i * b, n6);
                } else {
                    this.fontRendererObj.drawStringWithShadow(str3, n2 + 5, n4 + i * b, n6);
                }
            }
        }
    }

    public void onAutocompleteResponse(String[] p_146406_1_) {
        if (this.waitingOnAutocomplete) {
            this.playerNamesFound = false;
            this.foundPlayerNames.clear();

            for (String str : p_146406_1_) {
                if (str.length() > 0) {
                    this.foundPlayerNames.add(str);
                }
            }

            String str2 = this.inputField.getText().substring(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false));
            String commonPrefix = StringUtils.getCommonPrefix(p_146406_1_);
            if (commonPrefix.length() > 0 && !str2.equalsIgnoreCase(commonPrefix)) {
                this.inputField
                    .deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
                this.inputField.writeText(commonPrefix);
            } else if (this.foundPlayerNames.size() > 0) {
                this.playerNamesFound = true;
                this.autocompletePlayerNames();
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void drawChatInputField() {
        String text = this.inputField.getText();
        if (!this.isClientCommandInput()) {
            this.inputField.drawTextBox();
        } else {
            ListConfigNamesHelper.Entry entry = ListConfigNamesHelper.computeEntry(text);
            if (entry != null && entry.checkState() && !entry.in) {
                this.inputField.drawTextBox(entry.count, entry.count2, -43691);
            } else {
                ListConfigNamesHelper.Entry2 entry2 = ListConfigNamesHelper.computeEntry2(text);
                if (entry2 != null && entry2.flag) {
                    this.inputField.drawTextBox(entry2.count, entry2.count2, -43691);
                } else {
                    this.inputField.drawTextBox();
                }
            }
        }
    }

    private void drawAutocompleteHint() {
        if (this.isClientCommandInput() && !this.commandMatches.isEmpty()) {
            String text = this.inputField.getText();
            String str = ListConfigNamesHelper.computeString(text, this.commandMatches, this.commandSelectIndex);
            if (str != null) {
                String str2 = str.substring(text.length());
                if (!str2.isEmpty()) {
                    int n = this.inputField.xPosition + this.fontRendererObj.getStringWidth(text);
                    int yPosition = this.inputField.yPosition;
                    this.fontRendererObj.drawStringWithShadow("§7" + str2, n, yPosition, -5592406);
                }
            }
        }
    }
}
