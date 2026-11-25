package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brutality.gui.chat.ParseIntComponent;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.impl.misc.ChatMute;
import org.brutality.module.impl.visual.Chat;
import org.brutality.utils.chat.GetPersistentLineCountHelper;
import org.brutality.utils.chat.MessageCopiedHelper;
import org.brutality.utils.player.ApplyHelper;
import org.brutality.utils.player.PlayerHelper6;
import org.brutality.utils.player.PlayerHelper8;
import org.brutality.utils.render.RenderHelper9;
import org.brutality.utils.render.UpdateAnimationsHelper;
import org.lwjgl.input.Mouse;

public class GuiNewChat extends Gui {
    private static final Logger logger = LogManager.getLogger();
    public static int chatPos = 17;
    private final Minecraft mc;
    private final List<String> sentMessages = Lists.newArrayList();
    private final List<ChatLine> chatLines = Lists.newArrayList();
    private final List<ChatLine> field_146253_i = Lists.newArrayList();
    private int scrollPos;
    private boolean isScrolled;
    private final Set<String> animatedMessages = new HashSet<>();
    private static long messageIdCounter = 0L;
    private int hoveredLineIndex = -1;
    private int lastHoveredLineIndex = -1;
    private int copyButtonX = -1;
    private int copyButtonY = -1;
    private int lastCopyButtonX = -1;
    private int lastCopyButtonY = -1;
    private boolean copyButtonHovered = false;
    private static final int COPY_BUTTON_WIDTH = 14;
    private static final int COPY_BUTTON_HEIGHT = 10;
    private static final int COPY_BUTTON_GAP = 6;
    private static final int COPY_BUTTON_HIT_PADDING = 3;
    private String searchFilter = "";
    private String cachedSearchFilter = "";
    private List<Integer> cachedFilteredIndices = null;
    private int cachedFilteredLineCount = -1;
    private final Map<ChatLine, ChatLine> displayMessageSources = new IdentityHashMap<>();
    private final Map<ChatLine, String> normalizedDisplayMessages = new IdentityHashMap<>();
    private ChatLine refreshingSource;

    public void setSearchFilter(String filter) {
        String str = filter != null ? filter.toLowerCase(Locale.ROOT) : "";
        if (!str.equals(this.searchFilter)) {
            this.searchFilter = str;
            this.invalidateFilteredIndices();
        }
    }

    private void invalidateFilteredIndices() {
        this.cachedFilteredIndices = null;
        this.cachedFilteredLineCount = -1;
    }

    private List<Integer> getFilteredIndices() {
        if (this.cachedFilteredIndices == null
            || !this.cachedSearchFilter.equals(this.searchFilter)
            || this.cachedFilteredLineCount != this.field_146253_i.size()) {
            this.cachedSearchFilter = this.searchFilter;
            this.cachedFilteredLineCount = this.field_146253_i.size();
            this.cachedFilteredIndices = Lists.newArrayList();

            for (int i = 0; i < this.field_146253_i.size(); i++) {
                ChatLine chatLine = this.field_146253_i.get(i);
                if (chatLine != null
                    && this.normalizedDisplayMessages
                        .computeIfAbsent(chatLine, chatLine2 -> chatLine2.getChatComponent().getUnformattedText().toLowerCase(Locale.ROOT))
                        .contains(this.searchFilter)) {
                    this.cachedFilteredIndices.add(i);
                }
            }
        }

        return this.cachedFilteredIndices;
    }

    public String getSearchFilter() {
        return this.searchFilter;
    }

    public GuiNewChat(Minecraft mcIn) {
        this.mc = mcIn;
        this.restorePersistentChat();
    }

    private static String generateMessageId() {
        return "msg_" + System.currentTimeMillis() + "_" + ++messageIdCounter;
    }

    private void restorePersistentChat() {
        if (GetPersistentLineCountHelper.isEnabled()) {
            var chatLines = GetPersistentLineCountHelper.computeList2();

            for (int i = chatLines.size() - 1; i >= 0; i--) {
                ChatLine chatLine = (ChatLine)chatLines.get(i);
                this.chatLines.add(chatLine);
                String str = "restored_" + chatLine.getUpdatedCounter() + "_" + chatLine.getChatLineID();
                this.animatedMessages.add(str);
            }

            var chatLines2 = GetPersistentLineCountHelper.computeList();

            for (int j = chatLines2.size() - 1; j >= 0; j--) {
                ChatLine chatLine2 = (ChatLine)chatLines2.get(j);
                this.field_146253_i.add(chatLine2);
            }
        }
    }

    public void drawChat(int p_146230_1_) {
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int lineCount = this.getLineCount();
            boolean flag = false;
            int n = 0;
            int size = this.field_146253_i.size();
            Chat instance = Chat.getInstance();
            float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
            if (size > 0) {
                if (this.getChatOpen()) {
                    flag = true;
                }

                UpdateAnimationsHelper.updateAnimations();
                float chatScale = this.getChatScale();
                int n2 = MathHelper.ceiling_float_int(this.getChatWidth() / chatScale);
                GlStateManager.pushMatrix();
                float f2 = instance != null && instance.checkState44() ? instance.computeFloat() : 2.0F;
                float f3 = instance != null && instance.checkState44() ? instance.computeFloat2() : 20.0F;
                GlStateManager.translate(f2, f3, 0.0F);
                GlStateManager.scale(chatScale, chatScale, 1.0F);
                this.handleCopyFunctionality();
                int n3 = Math.min(lineCount + Math.max(0, this.scrollPos), size);
                boolean flag2 = this.searchFilter != null && !this.searchFilter.isEmpty();
                List list = null;
                if (flag2) {
                    list = this.getFilteredIndices();
                }

                int n4 = flag2 ? list.size() : this.field_146253_i.size();

                for (int i = 0; i + this.scrollPos < n4 && i < n3; i++) {
                    int n5 = i + this.scrollPos;
                    int n6 = flag2 ? (Integer)list.get(n5) : n5;
                    ChatLine chatLine = this.field_146253_i.get(n6);
                    if (chatLine != null) {
                        int n7 = p_146230_1_ - chatLine.getUpdatedCounter();
                        if (n7 < 200 || flag) {
                            double d = n7 / 200.0;
                            d = 1.0 - d;
                            d *= 10.0;
                            d = MathHelper.clamp_double(d, 0.0, 1.0);
                            double d2 = d * d;
                            if (UpdateAnimationsHelper.isEnabled()) {
                                float f4 = this.mc.timer == null ? 0.0F : this.mc.timer.renderPartialTicks;
                                d2 = RenderHelper9.computeFloat3((long)((200.0 - n7 - f4) * 50.0));
                            }

                            int n8 = (int)(255.0 * d2);
                            if (flag) {
                                n8 = 255;
                            }

                            int n9 = (int)(n8 * f);
                            n++;
                            if (n9 > 3) {
                                byte b = 0;
                                int n10 = -i * 9;
                                PlayerHelper8 playerHelper8 = PlayerHelper6.computePlayerHelper8(chatLine);
                                boolean flag3 = playerHelper8 != null && flag && n5 == this.hoveredLineIndex;
                                IChatComponent component = flag3 ? playerHelper8.iChatComponent : chatLine.getChatComponent();
                                String text = component.getFormattedText();
                                UpdateAnimationsHelper.Entry2 entry2 = UpdateAnimationsHelper.computeEntry2(component);
                                float f5 = b;
                                int n11 = 16777215 + (n9 << 24);
                                if (entry2 != null && entry2.flag) {
                                    f5 = b + entry2.offsetX;
                                    n11 = UpdateAnimationsHelper.computeInt(16777215 + (n9 << 24), entry2);
                                    n9 = n11 >>> 24;
                                    if (n9 < 4) {
                                        continue;
                                    }
                                }

                                int n12 = n9 / 2;
                                if (instance != null && instance.checkState44() && instance.checkState11()) {
                                    n12 = (int)(n9 * instance.computeFloat3());
                                }

                                boolean flag4 = instance != null && instance.checkState9() && instance.matchesString(component.getUnformattedText());
                                if (!flag4) {
                                    if (n5 == this.hoveredLineIndex && flag) {
                                        n12 = Math.max(n12, 64);
                                        drawRect(b, n10 - 9, b + n2 + 4, n10, n12 << 24 | 4491519);
                                    } else {
                                        drawRect(b, n10 - 9, b + n2 + 4, n10, n12 << 24);
                                    }
                                } else {
                                    String str = instance.computeString();
                                    if (!str.equals("Background") && !str.equals("Both")) {
                                        drawRect(b, n10 - 9, b + n2 + 4, n10, n12 << 24);
                                    } else {
                                        drawRect(b, n10 - 9, b + n2 + 4, n10, n12 << 24 | 11175936);
                                    }

                                    if (str.equals("Text Color") || str.equals("Both")) {
                                        n11 = n9 << 24 | 16777045;
                                    }
                                }

                                GlStateManager.enableBlend();
                                if (instance != null && instance.checkState44() && instance.checkState8()) {
                                    CustomFontRenderer drawStringWithShadowComponent = instance.computeDrawStringWithShadowComponent();
                                    if (drawStringWithShadowComponent != null) {
                                        if (instance.checkState6()) {
                                            drawStringWithShadowComponent.drawStringWithShadow(text, f5, n10 - 8, n11);
                                        } else {
                                            drawStringWithShadowComponent.computeInt3(text, f5, n10 - 8, n11);
                                        }
                                    } else {
                                        this.mc.fontRendererObj.drawStringWithShadow(text, f5, n10 - 8, n11);
                                    }
                                } else if (instance == null || !instance.checkState44()) {
                                    this.mc.fontRendererObj.drawStringWithShadow(text, f5, n10 - 8, n11);
                                } else if (instance.checkState6()) {
                                    this.mc.fontRendererObj.drawStringWithShadow(text, f5, n10 - 8, n11);
                                } else {
                                    this.mc.fontRendererObj.drawString(text, f5, n10 - 8, n11, false);
                                }

                                if (ParseIntComponent.matchesIChatComponent(component)) {
                                    ParseIntComponent.handleInt(b + n2 - 8, n10 - 8, n9);
                                    GlStateManager.enableBlend();
                                }

                                if (playerHelper8 != null && flag3 && flag) {
                                    ChatMute instance2 = ChatMute.getInstance();
                                    if (instance2 == null || instance2.checkState()) {
                                        this.drawMagnifyIcon(b + n2 + 8, n10 - 8);
                                    }
                                }

                                GlStateManager.disableAlpha();
                                GlStateManager.disableBlend();
                            }
                        }
                    }
                }

                if (flag) {
                    int FONT_HEIGHT = this.mc.fontRendererObj.FONT_HEIGHT;
                    GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                    int n13 = size * FONT_HEIGHT + size;
                    int n14 = n * FONT_HEIGHT + n;
                    int n15 = this.scrollPos * n14 / size;
                    int n16 = n14 * n14 / n13;
                    if (n13 != n14) {
                        int n17 = n15 > 0 ? 170 : 96;
                        int n18 = this.isScrolled ? 13382451 : 3355562;
                        drawRect(0, -n15, 2, -n15 - n16, n18 + (n17 << 24));
                        drawRect(2, -n15, 1, -n15 - n16, 13421772 + (n17 << 24));
                    }
                }

                GlStateManager.popMatrix();
            }
        }
    }

    private void handleCopyFunctionality() {
        if (!this.getChatOpen()) {
            this.hoveredLineIndex = -1;
            this.lastHoveredLineIndex = -1;
            this.copyButtonHovered = false;
        } else {
            int x = Mouse.getX();
            int y = Mouse.getY();
            this.copyButtonHovered = this.isMouseOverLastCopyButton(x, y);
            if (this.copyButtonHovered) {
                this.hoveredLineIndex = this.lastHoveredLineIndex;
            } else {
                int hoveredLineIndex = this.getHoveredLineIndex(x, y);
                this.hoveredLineIndex = hoveredLineIndex;
                if (hoveredLineIndex != -1) {
                    this.lastHoveredLineIndex = hoveredLineIndex;
                }
            }
        }
    }

    private int[] getChatMouseCoords(int rawMouseX, int rawMouseY) {
        Chat instance = Chat.getInstance();
        float f = instance != null && instance.checkState44() ? instance.computeFloat() : 2.0F;
        float f2 = instance != null && instance.checkState44() ? instance.computeFloat2() : 20.0F;
        float chatScale = this.getChatScale();
        ScaledResolution sr = new ScaledResolution(this.mc);
        int scaleFactor = sr.getScaleFactor();
        int n = rawMouseX / scaleFactor - ((int)f + 1);
        int n2 = rawMouseY / scaleFactor - (47 - (int)f2);
        n = MathHelper.floor_float(n / chatScale);
        n2 = MathHelper.floor_float(n2 / chatScale);
        return new int[]{n, n2};
    }

    private List<Integer> buildFilteredIndices() {
        return this.getFilteredIndices();
    }

    private int resolveDisplayLineIndex(int lineIndex) {
        if (this.searchFilter != null && !this.searchFilter.isEmpty()) {
            var filteredIndices = this.getFilteredIndices();
            return lineIndex >= 0 && lineIndex < filteredIndices.size() ? (Integer)filteredIndices.get(lineIndex) : -1;
        }
        return lineIndex;
    }

    private int getHoveredLineIndex(int rawMouseX, int rawMouseY) {
        int[] chatMouseCoords = this.getChatMouseCoords(rawMouseX, rawMouseY);
        int n = chatMouseCoords[0];
        int n2 = chatMouseCoords[1];
        if (n >= 0 && n2 >= 0) {
            int n3 = Math.min(this.getLineCount(), this.field_146253_i.size());
            boolean flag = this.searchFilter != null && !this.searchFilter.isEmpty();
            int n4 = flag ? this.getFilteredIndices().size() : this.field_146253_i.size();
            int n5 = MathHelper.floor_float(this.getChatWidth() / this.getChatScale());
            int n6 = n5 + 6 + 14 + 3;
            if (n > n6) {
                return -1;
            }
            if (n2 >= this.mc.fontRendererObj.FONT_HEIGHT * n3 + n3) {
                return -1;
            }
            int n7 = n2 / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;
            return n7 >= 0 && n7 < n4 ? n7 : -1;
        }
        return -1;
    }

    private void drawMagnifyIcon(int x, int y) {
        drawRect(x, y, x + 12, y + 8, -1441129950);
        drawRect(x + 1, y + 1, x + 11, y + 7, -1436825345);
        drawRect(x + 3, y + 2, x + 7, y + 6, -15724520);
        drawRect(x + 7, y + 4, x + 10, y + 5, -10761985);
        drawRect(x + 8, y + 5, x + 9, y + 6, -10761985);
    }

    private void drawCopyButton(int x, int y, boolean hovered) {
        int n = hovered ? -868386072 : -1155652560;
        int n2 = hovered ? -6568705 : -11905688;
        int n3 = hovered ? -7363388 : -9733484;
        int n4 = hovered ? -1 : -1512206;
        int n5 = hovered ? -722949 : -2235414;
        drawRect(x, y, x + 14, y + 10, n2);
        drawRect(x + 1, y + 1, x + 14 - 1, y + 10 - 1, n);
        drawRect(x + 3, y + 3, x + 11, y + 9, n3);
        drawRect(x + 2, y + 4, x + 10, y + 9, n4);
        drawRect(x + 3, y + 5, x + 9, y + 8, n5);
        drawRect(x + 4, y + 2, x + 8, y + 4, n4);
        drawRect(x + 5, y + 2, x + 7, y + 3, n5);
    }

    private boolean isMouseOverCopyButtonAt(int btnX, int btnY, int rawMouseX, int rawMouseY) {
        if (btnX >= 0 && btnY >= 0) {
            int[] chatMouseCoords = this.getChatMouseCoords(rawMouseX, rawMouseY);
            int n = chatMouseCoords[0];
            int n2 = chatMouseCoords[1];
            return n >= btnX - 3 && n <= btnX + 14 + 3 && n2 >= btnY - 3 && n2 <= btnY + 10 + 3;
        }
        return false;
    }

    private boolean isMouseOverLastCopyButton(int rawMouseX, int rawMouseY) {
        return this.isMouseOverCopyButtonAt(this.lastCopyButtonX, this.lastCopyButtonY, rawMouseX, rawMouseY);
    }

    public boolean tryHandleCopyClick(int mouseButton) {
        if (!this.getChatOpen()) {
            return false;
        }
        int x = Mouse.getX();
        int y = Mouse.getY();
        int hoveredLineIndex = this.getHoveredLineIndex(x, y);
        if (hoveredLineIndex == -1 && this.isMouseOverLastCopyButton(x, y)) {
            hoveredLineIndex = this.lastHoveredLineIndex;
        }

        if (hoveredLineIndex == -1) {
            return false;
        }
        if (mouseButton == 1) {
            this.copyMessageToClipboard(hoveredLineIndex);
            return true;
        }
        if (mouseButton == 0 && this.isMouseOverLastCopyButton(x, y)) {
            this.copyMessageToClipboard(hoveredLineIndex);
            return true;
        }
        return false;
    }

    private void copyMessageToClipboard(int lineIndex) {
        int n = this.resolveDisplayLineIndex(lineIndex);
        if (n >= 0 && n < this.field_146253_i.size()) {
            ChatLine chatLine = this.field_146253_i.get(n);
            String completeMessageFromLine = this.getCompleteMessageFromLine(chatLine);
            String str = completeMessageFromLine.replaceAll("§.", "");

            try {
                StringSelection stringSelection = new StringSelection(str);
                Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                systemClipboard.setContents(stringSelection, null);
                MessageCopiedHelper.handleString(str);
            } catch (Exception e) {
                logger.error("Failed to copy message to clipboard", (Throwable)e);
            }
        }
    }

    private String getCompleteMessageFromLine(ChatLine targetLine) {
        ChatLine chatLine = this.displayMessageSources.get(targetLine);
        if (chatLine != null) {
            return chatLine.getChatComponent().getUnformattedText();
        }
        int updatedCounter = targetLine.getUpdatedCounter();
        int chatLineID = targetLine.getChatLineID();

        for (ChatLine chatLine2 : this.chatLines) {
            if (chatLine2.getUpdatedCounter() == updatedCounter && chatLine2.getChatLineID() == chatLineID) {
                return chatLine2.getChatComponent().getUnformattedText();
            }
        }

        return targetLine.getChatComponent().getUnformattedText();
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        return this.tryHandleCopyClick(mouseButton);
    }

    public void clearChatMessages() {
        this.field_146253_i.clear();
        this.chatLines.clear();
        this.sentMessages.clear();
        this.animatedMessages.clear();
        this.displayMessageSources.clear();
        this.normalizedDisplayMessages.clear();
        this.invalidateFilteredIndices();
    }

    public void clearAllChatMessages() {
        this.field_146253_i.clear();
        this.chatLines.clear();
        this.sentMessages.clear();
        this.animatedMessages.clear();
        this.displayMessageSources.clear();
        this.normalizedDisplayMessages.clear();
        this.invalidateFilteredIndices();
        GetPersistentLineCountHelper.process();
    }

    public void printChatMessage(IChatComponent p_146227_1_) {
        this.printChatMessageWithOptionalDeletion(p_146227_1_, 0);
    }

    public void printChatMessageWithOptionalDeletion(IChatComponent p_146234_1_, int p_146234_2_) {
        IChatComponent component = ApplyHelper.apply(p_146234_1_);
        if (component != null) {
            this.setChatLine(component, p_146234_2_, this.mc.ingameGUI.getUpdateCounter(), false);
            logger.info("[CHAT] " + component.getUnformattedText());
            Chat instance = Chat.getInstance();
            if (instance != null && instance.checkState9() && instance.checkState4() && instance.matchesString(component.getUnformattedText()) && this.mc.thePlayer != null) {
                this.mc.thePlayer.playSound("note.pling", 1.0F, 1.0F);
            }
        }
    }

    private void setChatLine(IChatComponent p_146237_1_, int p_146237_2_, int p_146237_3_, boolean p_146237_4_) {
        if (p_146237_2_ != 0) {
            this.deleteChatLine(p_146237_2_);
        }

        PlayerHelper8 playerHelper8 = PlayerHelper6.computePlayerHelper8();
        int n = MathHelper.floor_float(this.getChatWidth() / this.getChatScale());
        var components = GuiUtilRenderComponents.func_178908_a(p_146237_1_, n, this.mc.fontRendererObj, false, false);
        boolean chatOpen = this.getChatOpen();
        String str = generateMessageId();
        boolean flag = !p_146237_4_;
        if (flag) {
            this.animatedMessages.add(str);
        }

        for (IChatComponent component : components) {
            if (chatOpen && this.scrollPos > 0) {
                this.isScrolled = true;
                this.scroll(1);
            }

            if (flag) {
                UpdateAnimationsHelper.handleIChatComponent3(component);
            }

            ChatLine chatLine = new ChatLine(p_146237_3_, component, p_146237_2_);
            this.normalizedDisplayMessages.put(chatLine, component.getUnformattedText().toLowerCase(Locale.ROOT));
            ChatLine chatLine2 = p_146237_4_ ? this.refreshingSource : new ChatLine(p_146237_3_, p_146237_1_, p_146237_2_);
            if (chatLine2 != null) {
                this.displayMessageSources.put(chatLine, chatLine2);
            }

            if (playerHelper8 != null) {
                PlayerHelper6.handleChatLine(chatLine, playerHelper8);
            }

            this.field_146253_i.add(0, chatLine);
            if (!p_146237_4_) {
                GetPersistentLineCountHelper.handleChatLine(chatLine);
            }
        }

        int n2 = 50000;

        while (this.field_146253_i.size() > n2) {
            ChatLine chatLine3 = this.field_146253_i.remove(this.field_146253_i.size() - 1);
            this.displayMessageSources.remove(chatLine3);
            this.normalizedDisplayMessages.remove(chatLine3);
        }

        this.invalidateFilteredIndices();
        if (!p_146237_4_) {
            ChatLine chatLine4 = new ChatLine(p_146237_3_, p_146237_1_, p_146237_2_);
            this.chatLines.add(0, chatLine4);
            GetPersistentLineCountHelper.handleChatLine2(chatLine4);

            while (this.chatLines.size() > n2) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }

    public void refreshChat() {
        this.field_146253_i.clear();
        this.displayMessageSources.clear();
        this.normalizedDisplayMessages.clear();
        this.invalidateFilteredIndices();
        this.resetScroll();

        for (int i = this.chatLines.size() - 1; i >= 0; i--) {
            ChatLine chatLine = this.chatLines.get(i);
            this.refreshingSource = chatLine;
            this.setChatLine(chatLine.getChatComponent(), chatLine.getChatLineID(), chatLine.getUpdatedCounter(), true);
        }

        this.refreshingSource = null;
    }

    public List<String> getSentMessages() {
        return this.sentMessages;
    }

    public void addToSentMessages(String p_146239_1_) {
        if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(p_146239_1_)) {
            this.sentMessages.add(p_146239_1_);
        }
    }

    public void resetScroll() {
        this.scrollPos = 0;
        this.isScrolled = false;
    }

    public void scroll(int p_146229_1_) {
        this.scrollPos += p_146229_1_;
        int size = this.field_146253_i.size();
        int lineCount = this.getLineCount();
        if (this.scrollPos > size - lineCount) {
            this.scrollPos = Math.max(0, size - lineCount);
        }

        if (this.scrollPos <= 0) {
            this.scrollPos = 0;
            this.isScrolled = false;
        } else {
            this.isScrolled = true;
        }
    }

    public IChatComponent getChatComponent(int p_146236_1_, int p_146236_2_) {
        if (!this.getChatOpen()) {
            return null;
        }
        int[] chatMouseCoords = this.getChatMouseCoords(p_146236_1_, p_146236_2_);
        int n = chatMouseCoords[0];
        int n2 = chatMouseCoords[1];
        if (n >= 0 && n2 >= 0) {
            int n3 = Math.min(this.getLineCount(), this.field_146253_i.size());
            boolean flag = this.searchFilter != null && !this.searchFilter.isEmpty();
            int n4 = flag ? this.buildFilteredIndices().size() : this.field_146253_i.size();
            if (n > MathHelper.floor_float(this.getChatWidth() / this.getChatScale())) {
                return null;
            }
            if (n2 >= this.mc.fontRendererObj.FONT_HEIGHT * n3 + n3) {
                return null;
            }
            int n5 = n2 / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;
            if (n5 >= 0 && n5 < n4) {
                int n6 = this.resolveDisplayLineIndex(n5);
                if (n6 >= 0 && n6 < this.field_146253_i.size()) {
                    ChatLine chatLine = this.field_146253_i.get(n6);
                    int n7 = 0;
                    IChatComponent component = null;

                    for (IChatComponent component2 : chatLine.getChatComponent()) {
                        if (component2 instanceof ChatComponentText) {
                            n7 += this.mc
                                .fontRendererObj
                                .getStringWidth(
                                    GuiUtilRenderComponents.func_178909_a(((ChatComponentText)component2).getChatComponentText_TextValue(), false)
                                );
                            if (n7 > n) {
                                return component2;
                            }
                        }

                        component = component2;
                    }

                    int n8 = MathHelper.floor_float(this.getChatWidth() / this.getChatScale());
                    return component != null && n >= n8 - 12 && ParseIntComponent.matchesIChatComponent(chatLine.getChatComponent()) ? component : null;
                }
                return null;
            }
            return null;
        }
        return null;
    }

    public boolean getChatOpen() {
        return this.mc.currentScreen instanceof GuiChat;
    }

    public void deleteChatLine(int p_146242_1_) {
        var it = this.field_146253_i.iterator();

        while (it.hasNext()) {
            ChatLine chatLine = (ChatLine)it.next();
            if (chatLine.getChatLineID() == p_146242_1_) {
                it.remove();
                this.displayMessageSources.remove(chatLine);
                this.normalizedDisplayMessages.remove(chatLine);
            }
        }

        this.invalidateFilteredIndices();
        it = this.chatLines.iterator();

        while (it.hasNext()) {
            ChatLine chatLine2 = (ChatLine)it.next();
            if (chatLine2.getChatLineID() == p_146242_1_) {
                it.remove();
                break;
            }
        }
    }

    public int getChatWidth() {
        return calculateChatboxWidth(this.mc.gameSettings.chatWidth);
    }

    public int getChatHeight() {
        return calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
    }

    public float getChatScale() {
        return this.mc.gameSettings.chatScale;
    }

    public static int calculateChatboxWidth(float p_146233_0_) {
        int n = 320;
        int n2 = 40;
        return MathHelper.floor_float(p_146233_0_ * (n - n2) + n2);
    }

    public static int calculateChatboxHeight(float p_146243_0_) {
        int n = 180;
        int n2 = 20;
        return MathHelper.floor_float(p_146243_0_ * (n - n2) + n2);
    }

    public int getLineCount() {
        return this.getChatHeight() / 9;
    }

    public int getPersistentLineCount() {
        return GetPersistentLineCountHelper.getPersistentLineCount();
    }

    public List<ChatLine> getChatLines() {
        return this.chatLines;
    }

    public List<ChatLine> getDisplayLines() {
        return this.field_146253_i;
    }

    public int getScrollPos() {
        return this.scrollPos;
    }
}
