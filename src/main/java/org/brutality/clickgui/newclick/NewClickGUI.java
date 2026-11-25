package org.brutality.clickgui.newclick;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.brutality.Brutality;
import org.brutality.clickgui.discord.CLIENTComponent;
import org.brutality.clickgui.discord.DiscordComponent3;
import org.brutality.clickgui.discord.ColorUtils;
import org.brutality.clickgui.discord.Inter18ptSemiboldTtfComponent;
import org.brutality.gui.CustomGuiScreen;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.client.ClickGUI;
import org.brutality.module.impl.client.ClientSettings;
import org.brutality.script.ScriptModule;
import org.brutality.utils.render.BrutalityHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class NewClickGUI extends CustomGuiScreen {
    private static final ResourceLocation resourceLocation = new ResourceLocation("brutality/clickgui/themes.png");
    private static final ResourceLocation resourceLocation2 = new ResourceLocation("brutality/clickgui/staff.png");
    private static final ResourceLocation resourceLocation3 = new ResourceLocation("brutality/clickgui/scripts.png");
    private static final ResourceLocation resourceLocation4 = new ResourceLocation("brutality/icons/config.png");
    private static final ResourceLocation resourceLocation5 = new ResourceLocation("brutality/clickgui/pit.png");
    private static final Deque<int[]> deque = new ArrayDeque<>();
    private final List<CLIENTComponent> entries = new CopyOnWriteArrayList<>();
    private final Map<String, List<CLIENTComponent>> lookupMap = new ConcurrentHashMap<>();
    private final Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent = new Inter18ptSemiboldTtfComponent();
    private final Map<HypixelPitType, DiscordComponent3> lookupMap2 = new EnumMap<>(HypixelPitType.class);
    private boolean initialized;
    private boolean flag;
    private HypixelPitType hypixelPitType = HypixelPitType.HYPIXEL_PIT;
    private String text = "";
    private boolean flag2;
    private Module module;

    private List<CLIENTComponent> computeList() {
        ArrayList list = new ArrayList();
        float f = 12.0F;

        for (CombatType combatType : CombatType.forPage(this.flag ? CombatType.MainMode.PIT : CombatType.MainMode.MAIN)) {
            list.add(new CLIENTComponent(this, combatType, f, 26.0F));
            f += 104.0F;
        }

        return list;
    }

    private int computeInt4() {
        return this.computeInt3() - 34 - 6;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.module != null) {
            if (keyCode == 1) {
                this.module.handleInt4(0);
            } else {
                this.module.handleInt4(keyCode);
            }

            this.module = null;
        } else if (this.checkState()) {
            this.flag2 = false;

            for (CLIENTComponent clientComponent : this.entries) {
                clientComponent.keyTyped(typedChar, keyCode);
            }
        } else if (this.flag2) {
            if (keyCode == 1) {
                this.flag2 = false;
            } else if (keyCode == 28) {
                this.flag2 = false;
            } else if (keyCode == 14) {
                if (!this.text.isEmpty()) {
                    this.text = this.text.substring(0, this.text.length() - 1);
                }
            } else if (keyCode == 211) {
                this.text = "";
            } else {
                if (typedChar >= ' ' && typedChar != 167) {
                    this.text = this.text + typedChar;
                }
            }
        } else if (keyCode == 1 && !this.checkState3()) {
            mc.displayGuiScreen(null);
            if (mc.currentScreen == null) {
                mc.setIngameFocus();
            }
        } else {
            for (CLIENTComponent clientComponent2 : this.entries) {
                clientComponent2.keyTyped(typedChar, keyCode);
            }
        }
    }

    private void handleInt(int n, int n2, int n3, int n4, String str) {
        int n5 = n2 + n4 - 12;
        drawRect(n + 1, n5, n + n3 - 1, n2 + n4 - 1, new Color(12, 12, 16, 230).getRGB());
        String str2 = this.computeString(str, n3 - 14, this.inter18ptSemiboldTtfComponent.drawStringWithShadowComponent3);
        this.inter18ptSemiboldTtfComponent.handleString2(str2, n + 7, n5 + 2, ColorUtils.color15.getRGB());
    }

    private String getFlag() {
        return this.flag ? "Pit tools & live status" : "Check Pit tools and wipes";
    }

    private void handleInt5(int n, int n2) {
        byte b = 118;
        byte b2 = 34;
        int n3 = 8;
        int n4 = this.height - b2 - 8;
        boolean flag = matchesInt(n, n2, n3, n4, b, b2);
        int n5 = flag ? new Color(38, 38, 46, 245).getRGB() : new Color(22, 22, 26, 235).getRGB();
        drawRect(n3, n4, n3 + b, n4 + b2, new Color(50, 50, 58, 220).getRGB());
        drawRect(n3 + 1, n4 + 1, n3 + b - 1, n4 + b2 - 1, n5);
        this.inter18ptSemiboldTtfComponent.handleString("Edit GUI", n3 + 10, n4 + 6, ColorUtils.color14.getRGB());
        this.handleInt(n3, n4, b, b2, "Arrange HUD overlays");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.entries.isEmpty()) {
            this.process7();
        }

        ScaledResolution sr = new ScaledResolution(mc);
        deque.clear();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(0, 0, 0, 120).getRGB());
        GL11.glPushMatrix();

        for (CLIENTComponent clientComponent : this.entries) {
            clientComponent.handleInt(mouseX, mouseY, this.inter18ptSemiboldTtfComponent);
            clientComponent.handleInt(mouseX, mouseY);
        }

        if (this.flag) {
            this.handleInt8(mouseX, mouseY);
        }

        this.handleInt6(mouseX, mouseY);
        this.handleInt4(mouseX, mouseY);
        this.handleInt2(mouseX, mouseY);
        this.handleInt(mouseX, mouseY);
        this.handleInt7(mouseX, mouseY);
        this.handleInt3(mouseX, mouseY);
        this.handleInt9(mouseX, mouseY);
        this.handleInt5(mouseX, mouseY);
        GL11.glPopMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private boolean matchesInt4(int n, int n2, int n3) {
        if (n3 != 0) {
            return false;
        }
        byte b = 118;
        int n4 = 34;
        int n5 = this.width - b - 8;
        int n6 = this.computeInt();
        if (matchesInt(n, n2, n5, n6, b, n4)) {
            this.process3();
            return true;
        }
        return false;
    }

    private boolean checkState() {
        for (CLIENTComponent clientComponent : this.entries) {
            if (clientComponent.checkState()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (!this.matchesInt7(mouseX, mouseY, mouseButton)) {
            if (!this.matchesInt4(mouseX, mouseY, mouseButton)) {
                if (!this.matchesInt3(mouseX, mouseY, mouseButton)) {
                    if (!this.matchesInt(mouseX, mouseY)) {
                        this.flag2 = false;
                    }

                    if (this.module != null) {
                        byte b;
                        switch (mouseButton) {
                            case 0:
                                b = -100;
                                break;
                            case 1:
                                b = -99;
                                break;
                            case 2:
                                b = -98;
                                break;
                            case 3:
                                b = -97;
                                break;
                            case 4:
                                b = -96;
                                break;
                            default:
                                return;
                        }

                        this.module.handleInt4(b);
                        this.module = null;
                    } else if (!this.matchesInt8(mouseX, mouseY, mouseButton)) {
                        if (!this.matchesInt2(mouseX, mouseY, mouseButton)) {
                            if (!this.matchesInt5(mouseX, mouseY, mouseButton)) {
                                if (!this.matchesInt3(mouseX, mouseY, mouseButton)) {
                                    if (!this.matchesInt(mouseX, mouseY, mouseButton)) {
                                        if (!this.matchesInt6(mouseX, mouseY, mouseButton)) {
                                            if (!this.flag || mouseButton != 0 || !this.matchesInt2(mouseX, mouseY)) {
                                                for (CLIENTComponent clientComponent : this.entries) {
                                                    clientComponent.mouseClicked(mouseX, mouseY, mouseButton);
                                                }

                                                super.mouseClicked(mouseX, mouseY, mouseButton);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleInt9(int n, int n2) {
        byte b = 118;
        byte b2 = 34;
        int n3 = this.width - b - 8;
        int height = this.getHeight();
        boolean flag = matchesInt(n, n2, n3, height, b, b2);
        BrutalityHelper brutalityHelper = ClickGUI.instance != null && ClickGUI.instance.computeBrutalityHelper() != null
            ? ClickGUI.instance.computeBrutalityHelper()
            : BrutalityHelper.getInstance();
        String name = brutalityHelper.getName();
        Color color = brutalityHelper.getColor();
        int n4 = flag ? new Color(32, 32, 38, 245).getRGB() : new Color(22, 22, 26, 235).getRGB();
        drawRect(n3, height, n3 + b, height + b2, new Color(50, 50, 58, 220).getRGB());
        drawRect(n3 + 1, height + 1, n3 + b - 1, height + b2 - 1, n4);
        CLIENTComponent.handleResourceLocation(resourceLocation, n3 + 4, height + 2, 16);
        this.inter18ptSemiboldTtfComponent.handleString("Themes", n3 + 28, height + 6, ColorUtils.color14.getRGB());
        int n5 = height + b2 - 12;
        drawRect(n3 + 1, n5, n3 + b - 1, height + b2 - 1, new Color(12, 12, 16, 230).getRGB());
        drawRect(n3 + 6, n5 + 3, n3 + 14, n5 + 9, color.getRGB());
        this.inter18ptSemiboldTtfComponent.handleString2(name, n3 + 18, n5 + 2, ColorUtils.color15.getRGB());
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private int computeInt3() {
        return this.getHeight() - 34 - 6;
    }

    private boolean matchesInt(int n, int n2, int n3) {
        if (n3 != 0) {
            return false;
        }
        byte b = 118;
        int n4 = 34;
        int n5 = this.width - b - 8;
        int height = this.getHeight();
        if (matchesInt(n, n2, n5, height, b, n4)) {
            mc.displayGuiScreen(new NewclickScreen(this));
            return true;
        }
        return false;
    }

    public String getText() {
        return this.text;
    }

    private static boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6) {
        return n >= n3 && n <= n3 + n5 && n2 >= n4 && n2 <= n4 + n6;
    }

    private String computeString() {
        return Brutality.appdataHandler != null && Brutality.appdataHandler.getText() != null
            ? "Active: " + Brutality.appdataHandler.getText()
            : "No config loaded";
    }

    public static void process11() {
        if (deque.isEmpty()) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else {
            deque.pop();
            if (deque.isEmpty()) {
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            } else {
                int[] ints = deque.peek();
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                GL11.glScissor(ints[0], ints[1], ints[2], ints[3]);
            }
        }
    }

    public void process3() {
        // VENDOR-DISABLED: mc.displayGuiScreen(new GuiCloudBrowser(this));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        for (CLIENTComponent clientComponent : this.entries) {
            clientComponent.mouseReleased(mouseX, mouseY, state);
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    public static void process4() {
        process11();
    }

    public NewClickGUI() {
        for (HypixelPitType hypixelPitType : HypixelPitType.values()) {
            this.lookupMap2.put(hypixelPitType, new DiscordComponent3(0.0F, 0.25F));
        }
    }

    public void process() {
        mc.displayGuiScreen(new LatestWipeScreen(this));
    }

    private boolean matchesInt7(int n, int n2, int n3) {
        short s = 160;
        int n4 = 16;
        byte b = 8;
        int n5 = 8;
        boolean flag = matchesInt(n, n2, b, n5, s, n4);
        if (n3 == 0) {
            this.flag2 = flag;
            return flag;
        }
        if (n3 == 1 && flag) {
            this.text = "";
            this.flag2 = true;
            return true;
        }
        return false;
    }

    public HypixelPitType getHypixelPitType() {
        return this.hypixelPitType;
    }

    private String computeString(String str, int n, CustomFontRenderer drawStringWithShadowComponent) {
        if (drawStringWithShadowComponent != null && str != null && !str.isEmpty()) {
            String str2 = "...";
            int textWidth = drawStringWithShadowComponent.getStringWidth(str2);
            if (drawStringWithShadowComponent.getStringWidth(str) <= n) {
                return str;
            }
            int n2 = 0;
            int len = str.length();
            String str3 = str;

            while (n2 < len) {
                int n3 = (n2 + len + 1) / 2;
                String str4 = str.substring(0, n3) + str2;
                int textWidth2 = drawStringWithShadowComponent.getStringWidth(str4);
                if (textWidth2 <= n) {
                    str3 = str4;
                    n2 = n3;
                } else {
                    len = n3 - 1;
                }
            }

            return drawStringWithShadowComponent.getStringWidth(str3) > n && textWidth <= n ? str2 : str3;
        }
        return str != null ? str : "";
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void process6() {
        // VENDOR-DISABLED: mc.displayGuiScreen(new GuiCloudUpload(this));
    }

    private void process7() {
        String str = this.computeString2();
        List list = this.text.isEmpty() ? this.lookupMap.get(str) : null;
        if (list != null) {
            this.entries.clear();
            this.entries.addAll(list);
            this.initialized = true;
        } else {
            List list2 = this.computeList();
            if (this.text.isEmpty()) {
                this.lookupMap.put(str, list2);
            }

            this.entries.clear();
            this.entries.addAll(list2);
            this.initialized = true;
        }
    }

    public void process5() {
        this.flag = true;
        this.process8();
        this.process7();
    }

    public static void process10() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen instanceof NewClickGUI) {
            ((NewClickGUI)mc.currentScreen).flag2 = false;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        if (!this.initialized) {
            this.process7();
        }
    }

    private void handleInt8(int n, int n2) {
        byte b = 90;
        byte b2 = 14;
        int n3 = 4;
        int n4 = Math.max(6, this.width - b - 8);
        int n5 = 26;

        for (HypixelPitType hypixelPitType : HypixelPitType.values()) {
            boolean flag = matchesInt(n, n2, n4, n5, b, b2);
            DiscordComponent3 discordComponent3 = this.lookupMap2.get(hypixelPitType);
            discordComponent3.setFactor2(flag ? 1.0F : 0.0F);
            float f = discordComponent3.computeFloat();
            int n6 = hypixelPitType == this.hypixelPitType ? new Color(175, 35, 35, 240).getRGB() : new Color(26, 26, 30, 225).getRGB();
            drawRect(n4, n5, n4 + b, n5 + b2, n6);
            this.inter18ptSemiboldTtfComponent.handleString2(hypixelPitType.getLabel(), n4 + 5, n5 + 4, -1);
            if (f > 0.01F) {
                int n7 = (int)(120.0F * f);
                drawRect(n4 - 3 - n7, n5, n4 - 3, n5 + b2, new Color(35, 35, 40, (int)(220.0F * f)).getRGB());
                if (n7 > 45) {
                    this.inter18ptSemiboldTtfComponent
                        .handleString2("Click to switch page", n4 - n7 + 4, n5 + 4, new Color(230, 230, 230, (int)(255.0F * f)).getRGB());
                }
            }

            n5 += b2 + n3;
        }
    }

    private int computeInt5() {
        return this.computeInt2() - 34 - 6;
    }

    private boolean matchesInt(int n, int n2) {
        return matchesInt(n, n2, 8, 8, 160, 16);
    }

    @Override
    public void onGuiClosed() {
        deque.clear();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        this.process8();
        this.module = null;
        ClickGUI instance = ClickGUI.getInstance();
        if (instance != null) {
            instance.onGuiClosed();
        }

        super.onGuiClosed();
    }

    private String computeString2() {
        return (this.flag ? "pit:" : "main:") + this.hypixelPitType.name();
    }

    private void process8() {
        this.text = "";
        this.flag2 = false;
    }

    public static void handleInt(int n, int n2, int n3, int n4) {
        if (n4 > 0) {
            Minecraft mc = Minecraft.getMinecraft();
            int scaleFactor = new ScaledResolution(mc).getScaleFactor();
            int n5 = n * scaleFactor;
            int n6 = mc.displayHeight - (n2 + n4) * scaleFactor;
            int n7 = n3 * scaleFactor;
            int n8 = n4 * scaleFactor;
            if (!deque.isEmpty()) {
                int[] ints = deque.peek();
                int n9 = ints[0];
                int n10 = ints[1];
                int n11 = ints[0] + ints[2];
                int n12 = ints[1] + ints[3];
                int n13 = n5 + n7;
                int n14 = n6 + n8;
                int n15 = Math.max(n9, n5);
                int n16 = Math.max(n10, n6);
                int n17 = Math.min(n11, n13);
                int n18 = Math.min(n12, n14);
                if (n17 > n15 && n18 > n16) {
                    n5 = n15;
                    n6 = n16;
                    n7 = n17 - n15;
                    n8 = n18 - n16;
                } else {
                    n5 = n9;
                    n6 = n10;
                    n7 = 0;
                    n8 = 0;
                }
            }

            deque.push(new int[]{n5, n6, n7, n8});
            if (n8 > 0) {
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                GL11.glScissor(n5, n6, n7, n8);
            }
        }
    }

    private String computeString3() {
        int n = 0;
        if (Brutality.moduleManager != null) {
            for (Module module : ModuleManager.getEntries2()) {
                if (module instanceof ScriptModule && module.checkState44()) {
                    n++;
                }
            }
        }

        return n + " toggled scripts";
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int eventDWheel = Mouse.getEventDWheel();
        if (eventDWheel != 0) {
            int n = Mouse.getX() * this.width / mc.displayWidth;
            int n2 = this.height - Mouse.getY() * this.height / mc.displayHeight - 1;
            int n3 = eventDWheel > 0 ? 18 : -18;

            for (CLIENTComponent clientComponent : this.entries) {
                if (clientComponent.matchesInt2(n, n2)) {
                    clientComponent.handleInt(n3);
                    return;
                }
            }
        }
    }

    private void handleInt6(int n, int n2) {
        short s = 160;
        byte b = 16;
        int n3 = 8;
        byte b2 = 8;
        boolean flag = matchesInt(n, n2, n3, b2, s, b);
        int n4 = this.flag2 ? new Color(28, 28, 32, 235).getRGB() : (flag ? new Color(26, 26, 30, 225).getRGB() : new Color(22, 22, 25, 215).getRGB());
        int n5 = this.flag2 ? ColorUtils.color8.getRGB() : new Color(60, 60, 68, 210).getRGB();
        drawRect(n3, b2, n3 + s, b2 + b, n5);
        drawRect(n3 + 1, b2 + 1, n3 + s - 1, b2 + b - 1, n4);
        boolean flag2 = !this.text.isEmpty();
        String str;
        if (flag2) {
            str = this.text;
        } else {
            int n6 = (int)(System.currentTimeMillis() / 420L % 4L);
            StringBuilder sb = new StringBuilder("Search modules");

            for (int i = 0; i < n6; i++) {
                sb.append('.');
            }

            str = sb.toString();
        }

        int n7 = flag2 ? ColorUtils.color14.getRGB() : ColorUtils.color16.getRGB();
        if (this.flag2 && flag2 && System.currentTimeMillis() / 500L % 2L == 0L) {
            str = str + "|";
        }

        int n8 = s - 8;
        int textWidth = this.inter18ptSemiboldTtfComponent.drawStringWithShadowComponent2.getStringWidth(str);
        if (textWidth > n8) {
            str = this.computeString(str, n8, this.inter18ptSemiboldTtfComponent.drawStringWithShadowComponent2);
        }

        this.inter18ptSemiboldTtfComponent.handleString(str, n3 + 4, b2 + 3, n7);
    }

    public void process9() {
        mc.displayGuiScreen(new RefreshScreen(this));
    }

    public static void handleInt2(int n, int n2, int n3, int n4) {
        handleInt(n, n2, n3, n4);
    }

    public boolean checkState4() {
        return false;
    }

    public void process13() {
        String text = this.text;
        boolean flag = this.flag;
        HypixelPitType hypixelPitType = this.hypixelPitType;
        CompletableFuture.runAsync(() -> {
            if (text.isEmpty()) {
                this.lookupMap.put("main:" + hypixelPitType.name(), this.computeList(text, false, hypixelPitType));

                for (HypixelPitType hypixelPitType2 : HypixelPitType.values()) {
                    this.lookupMap.put("pit:" + hypixelPitType2.name(), this.computeList(text, true, hypixelPitType2));
                }
            }
        });
        if (mc != null) {
            mc.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    CLIENTComponent.process2();
                }
            });
        }
    }

    public void process12() {
        mc.displayGuiScreen(new OpenApiDocsScreen(this));
    }

    public static boolean checkState2() {
        if (!ClientSettings.checkState()) {
            return false;
        }
        Minecraft mc = Minecraft.getMinecraft();
        return !(mc.currentScreen instanceof NewClickGUI newClickGUI) ? false : newClickGUI.isFlag2() || newClickGUI.checkState();
    }

    private int computeInt() {
        return this.computeInt4() - 34 - 6;
    }

    public boolean isFlag2() {
        return this.flag2;
    }

    private boolean matchesInt8(int n, int n2, int n3) {
        if (n3 != 0) {
            return false;
        }
        byte b = 118;
        int n4 = 34;
        int n5 = this.width - b - 8;
        int n6 = this.computeInt3();
        if (matchesInt(n, n2, n5, n6, b, n4)) {
            this.process6();
            return true;
        }
        return false;
    }

    public boolean matchesModule(Module module) {
        return this.module == module;
    }

    private List<CLIENTComponent> computeList(String str, boolean flag, HypixelPitType hypixelPitType) {
        ArrayList list = new ArrayList();
        float f = 12.0F;

        for (CombatType combatType : CombatType.forPage(flag ? CombatType.MainMode.PIT : CombatType.MainMode.MAIN)) {
            list.add(new CLIENTComponent(this, combatType, f, 26.0F, str, flag, hypixelPitType));
            f += 104.0F;
        }

        return list;
    }

    private void handleInt4(int n, int n2) {
        byte b = 118;
        byte b2 = 34;
        int n3 = this.width - b - 8;
        int n4 = this.computeInt();
        boolean flag = matchesInt(n, n2, n3, n4, b, b2);
        drawRect(n3, n4, n3 + b, n4 + b2, new Color(50, 50, 58, 220).getRGB());
        drawRect(n3 + 1, n4 + 1, n3 + b - 1, n4 + b2 - 1, flag ? new Color(32, 32, 38, 245).getRGB() : new Color(22, 22, 26, 235).getRGB());
        this.inter18ptSemiboldTtfComponent.handleString("Cloud", n3 + 10, n4 + 6, ColorUtils.color14.getRGB());
        this.handleInt(n3, n4, b, b2, "Browse configs & scripts");
    }

    private boolean matchesInt2(int n, int n2, int n3) {
        if (n3 != 0) {
            return false;
        }
        byte b = 118;
        int n4 = 34;
        int n5 = this.width - b - 8;
        int n6 = this.computeInt2();
        if (matchesInt(n, n2, n5, n6, b, n4)) {
            this.process12();
            return true;
        }
        return false;
    }

    public int computeInt(int n, int n2) {
        int n3 = Math.max(90, this.height - n - 14 - 15);
        return Math.min(n2, n3);
    }

    private boolean matchesInt2(int n, int n2) {
        byte b = 90;
        byte b2 = 14;
        int n3 = 4;
        int n4 = Math.max(6, this.width - b - 8);
        int n5 = 26;

        for (HypixelPitType hypixelPitType : HypixelPitType.values()) {
            if (matchesInt(n, n2, n4, n5, b, b2)) {
                this.hypixelPitType = hypixelPitType;
                this.process7();
                return true;
            }

            n5 += b2 + n3;
        }

        return false;
    }

    private int getHeight() {
        return this.height - 34 - 8;
    }

    private void handleInt7(int n, int n2) {
        byte b = 118;
        byte b2 = 34;
        int n3 = this.width - b - 8;
        int n4 = this.computeInt2();
        boolean flag = matchesInt(n, n2, n3, n4, b, b2);
        int n5 = flag ? new Color(32, 32, 38, 245).getRGB() : new Color(22, 22, 26, 235).getRGB();
        drawRect(n3, n4, n3 + b, n4 + b2, new Color(50, 50, 58, 220).getRGB());
        drawRect(n3 + 1, n4 + 1, n3 + b - 1, n4 + b2 - 1, n5);
        CLIENTComponent.handleResourceLocation(resourceLocation3, n3 + 8, n4 + 3, 14);
        this.inter18ptSemiboldTtfComponent.handleString("Scripts", n3 + 28, n4 + 6, ColorUtils.color14.getRGB());
        this.handleInt(n3, n4, b, b2, this.computeString3());
    }

    private void handleInt(int n, int n2) {
        byte b = 118;
        byte b2 = 34;
        int n3 = this.width - b - 8;
        int n4 = this.computeInt3();
        boolean flag = matchesInt(n, n2, n3, n4, b, b2);
        int n5 = flag ? new Color(32, 32, 38, 245).getRGB() : new Color(22, 22, 26, 235).getRGB();
        drawRect(n3, n4, n3 + b, n4 + b2, new Color(50, 50, 58, 220).getRGB());
        drawRect(n3 + 1, n4 + 1, n3 + b - 1, n4 + b2 - 1, n5);
        CLIENTComponent.handleResourceLocation(resourceLocation2, n3 + 8, n4 + 3, 14);
        this.inter18ptSemiboldTtfComponent.handleString("Staff Tracker", n3 + 28, n4 + 6, ColorUtils.color14.getRGB());
        int n6 = n4 + b2 - 12;
        drawRect(n3 + 1, n6, n3 + b - 1, n4 + b2 - 1, new Color(12, 12, 16, 230).getRGB());
        this.inter18ptSemiboldTtfComponent.handleString2("Hypixel staff", n3 + 8, n6 + 2, ColorUtils.color15.getRGB());
    }

    private boolean matchesInt6(int n, int n2, int n3) {
        if (n3 != 0) {
            return false;
        }
        byte b = 118;
        int n4 = 34;
        byte b2 = 8;
        int n5 = this.height - n4 - 8;
        if (matchesInt(n, n2, b2, n5, b, n4)) {
            mc.displayGuiScreen(new HudEditorGui());
            return true;
        }
        return false;
    }

    private boolean checkState3() {
        return this.flag2 || this.checkState();
    }

    private boolean matchesInt3(int n, int n2, int n3) {
        if (n3 != 0) {
            return false;
        }
        byte b = 118;
        int n4 = 34;
        int n5 = this.width - b - 8;
        int n6 = this.computeInt4();
        if (matchesInt(n, n2, n5, n6, b, n4)) {
            this.process();
            return true;
        }
        return false;
    }

    private int computeInt2() {
        return this.computeInt3() - 34 - 6;
    }

    public void clearCache() {
        this.entries.clear();
        this.lookupMap.clear();
        this.initialized = false;
        this.process8();
        this.flag = false;
        this.hypixelPitType = HypixelPitType.HYPIXEL_PIT;
    }

    private void handleInt2(int n, int n2) {
        byte b = 118;
        byte b2 = 34;
        int n3 = this.width - b - 8;
        int n4 = this.computeInt4();
        boolean flag = matchesInt(n, n2, n3, n4, b, b2);
        int n5 = flag ? new Color(32, 32, 38, 245).getRGB() : new Color(22, 22, 26, 235).getRGB();
        drawRect(n3, n4, n3 + b, n4 + b2, new Color(50, 50, 58, 220).getRGB());
        drawRect(n3 + 1, n4 + 1, n3 + b - 1, n4 + b2 - 1, n5);
        CLIENTComponent.handleResourceLocation(resourceLocation5, n3 + 8, n4 + 3, 14);
        this.inter18ptSemiboldTtfComponent.handleString("Pit", n3 + 28, n4 + 6, ColorUtils.color14.getRGB());
        this.handleInt(n3, n4, b, b2, this.getFlag());
    }

    public void process2() {
        this.flag = false;
        this.process8();
        this.process7();
    }

    public void setModule(Module module) {
        this.module = module;
    }

    private void handleInt3(int n, int n2) {
        byte b = 118;
        byte b2 = 34;
        int n3 = this.width - b - 8;
        int n4 = this.computeInt5();
        boolean flag = matchesInt(n, n2, n3, n4, b, b2);
        int n5 = flag ? new Color(32, 32, 38, 245).getRGB() : new Color(22, 22, 26, 235).getRGB();
        drawRect(n3, n4, n3 + b, n4 + b2, new Color(50, 50, 58, 220).getRGB());
        drawRect(n3 + 1, n4 + 1, n3 + b - 1, n4 + b2 - 1, n5);
        CLIENTComponent.handleResourceLocation(resourceLocation4, n3 + 8, n4 + 3, 14);
        this.inter18ptSemiboldTtfComponent.handleString("Configs", n3 + 28, n4 + 6, ColorUtils.color14.getRGB());
        this.handleInt(n3, n4, b, b2, this.computeString());
    }

    private boolean matchesInt5(int n, int n2, int n3) {
        if (n3 != 0) {
            return false;
        }
        byte b = 118;
        int n4 = 34;
        int n5 = this.width - b - 8;
        int n6 = this.computeInt5();
        if (matchesInt(n, n2, n5, n6, b, n4)) {
            this.process9();
            return true;
        }
        return false;
    }
}
