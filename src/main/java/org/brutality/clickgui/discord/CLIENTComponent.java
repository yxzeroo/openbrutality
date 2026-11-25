package org.brutality.clickgui.discord;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.brutality.clickgui.newclick.CombatType;
import org.brutality.clickgui.newclick.HypixelPitType;
import org.brutality.clickgui.newclick.NewClickGUI;
import org.brutality.clickgui.newclick.NewclickComponent5;
import org.brutality.module.Category;
import org.brutality.module.Module;

public class CLIENTComponent {
    public static final int count = 101;
    public static final int count2 = 15;
    private final NewClickGUI newClickGUI;
    private final CombatType combatType;
    private final List<NewclickComponent5> entries = new ArrayList<>();
    private float x2;
    private float y2;
    private float factor;
    private float factor2;
    public boolean flag = true;
    private String text = "";
    private boolean flag2;
    private HypixelPitType hypixelPitType;
    private static final Map<String, ResourceLocation> lookupMap = new HashMap<>();

    public static ResourceLocation computeResourceLocation(Category category) {
        ResourceLocation location = lookupMap.get(category.name());
        return location == null ? lookupMap.get("CLIENT") : location;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.matchesInt(mouseX, mouseY) && mouseButton == 1) {
            this.flag = !this.flag;
        } else if (this.matchesInt(mouseX, mouseY) && mouseButton == 0 && this.combatType.isNavigationOnly()) {
            if (this.combatType.checkState2()) {
                this.newClickGUI.process5();
            }

            if (this.combatType.checkState3()) {
                this.newClickGUI.process3();
            }

            if (this.combatType.checkState()) {
                this.newClickGUI.process2();
            }
        } else {
            if (this.flag && !this.combatType.isNavigationOnly()) {
                int n = (int)this.y2 + 15 + (int)this.factor;

                for (NewclickComponent5 newclickComponent5 : this.entries) {
                    if (newclickComponent5.matchesInt((int)this.x2, n, 100, mouseX, mouseY, mouseButton)) {
                        return;
                    }

                    n += newclickComponent5.count5;
                }
            }
        }
    }

    public static void process2() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.getTextureManager() != null) {
            for (ResourceLocation location : lookupMap.values()) {
                if (location != null) {
                    try {
                        mc.getTextureManager().bindTexture(location);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    public CLIENTComponent(NewClickGUI newClickGUI, CombatType combatType, float f, float f2) {
        this(newClickGUI, combatType, f, f2, newClickGUI.getText(), newClickGUI.isFlag(), newClickGUI.getHypixelPitType());
    }

    public float getX() {
        return this.x2;
    }

    public boolean matchesInt(int n, int n2) {
        return n >= this.x2 && n2 >= this.y2 && n <= this.x2 + 101.0F && n2 <= this.y2 + 15.0F;
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        for (NewclickComponent5 newclickComponent5 : this.entries) {
            newclickComponent5.mouseReleased(mouseX, mouseY, state);
        }
    }

    static {
        lookupMap.put("COMBAT", new ResourceLocation("brutality/clickgui/combat.png"));
        lookupMap.put("MOVEMENT", new ResourceLocation("brutality/clickgui/movement.png"));
        lookupMap.put("PLAYER", new ResourceLocation("brutality/clickgui/player.png"));
        lookupMap.put("VISUAL", new ResourceLocation("brutality/clickgui/visual.png"));
        lookupMap.put("EXPLOIT", new ResourceLocation("brutality/clickgui/exploit.png"));
        lookupMap.put("MISCELLANEOUS", new ResourceLocation("brutality/clickgui/miscellaneous.png"));
        lookupMap.put("PIT", new ResourceLocation("brutality/clickgui/pit.png"));
        lookupMap.put("CLOUD", new ResourceLocation("brutality/clickgui/cloud.png"));
        lookupMap.put("PIT_AUTOMATION", new ResourceLocation("brutality/clickgui/pit/auto.png"));
        lookupMap.put("PIT_HUD", new ResourceLocation("brutality/clickgui/pit/hud.png"));
        lookupMap.put("PIT_SWAP", new ResourceLocation("brutality/clickgui/pit/swap.png"));
        lookupMap.put("PIT_UTILITY", new ResourceLocation("brutality/clickgui/pit/utility.png"));
        lookupMap.put("PIT_MAJOR_EVENT", new ResourceLocation("brutality/clickgui/pit/major.png"));
        lookupMap.put("PIT_MINOR_EVENT", new ResourceLocation("brutality/clickgui/pit/minor.png"));
        lookupMap.put("PIT_PREMIUM", new ResourceLocation("brutality/clickgui/pit/auto.png"));
        lookupMap.put("CLIENT", new ResourceLocation("brutality/clickgui/client.png"));
        lookupMap.put("BACK", new ResourceLocation("brutality/clickgui/config.png"));
    }

    public CombatType getCombatType() {
        return this.combatType;
    }

    private void handleString(String str, int n, int n2) {
        handleResourceLocation(lookupMap.get(str), n, n2, 8);
    }

    private static float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, f));
    }

    public NewClickGUI getNewClickGUI() {
        return this.newClickGUI;
    }

    public void handleInt(int n, int n2, Inter18ptSemiboldTtfComponent inter18ptSemiboldTtfComponent) {
        this.process3();
        boolean navigationOnly = this.combatType.isNavigationOnly();
        boolean flag = this.matchesInt(n, n2);
        int n3 = navigationOnly ? (flag ? new Color(175, 35, 35, 255).getRGB() : new Color(120, 30, 30, 255).getRGB()) : new Color(29, 29, 29, 255).getRGB();
        Gui.drawRect((int)this.x2 - 1, (int)this.y2, (int)this.x2 + 101, (int)this.y2 + 15, n3);
        if (this.combatType.checkState3()) {
            handleResourceLocation(lookupMap.get(this.combatType.name()), (int)this.x2 + 2, (int)this.y2 + 1, 13);
            inter18ptSemiboldTtfComponent.handleString2(this.combatType.getLabel(), (int)this.x2 + 17, (int)this.y2 + 4, -1);
        } else if (this.combatType == CombatType.PIT_MINOR_EVENT) {
            handleResourceLocation(lookupMap.get(this.combatType.name()), (int)this.x2 + 1, (int)this.y2 + 1, 13);
            inter18ptSemiboldTtfComponent.handleString2(this.combatType.getLabel(), (int)this.x2 + 16, (int)this.y2 + 4, -1);
        } else {
            this.handleString(this.combatType.name(), (int)this.x2 + 3, (int)this.y2 + 3);
            inter18ptSemiboldTtfComponent.handleString2(this.combatType.getLabel(), (int)this.x2 + 14, (int)this.y2 + 4, -1);
        }

        inter18ptSemiboldTtfComponent.handleString2(
            navigationOnly ? "PAGE >" : (this.flag ? "-" : "+"), (int)this.x2 + 101 - (navigationOnly ? 33 : 10), (int)this.y2 + 4, ColorUtils.color15.getRGB()
        );
        if (this.flag && !this.combatType.isNavigationOnly()) {
            int n4 = this.computeInt();
            int n5 = this.newClickGUI.computeInt((int)this.y2, n4);
            Gui.drawRect((int)this.x2 - 1, (int)this.y2 + 15, (int)this.x2 + 101, (int)this.y2 + 15 + n5 + 1, new Color(29, 29, 29, 255).getRGB());
            float f = Math.min(0.0F, (float)(n5 - n4));
            this.factor2 = clamp(this.factor2, f, 0.0F);
            this.factor = this.factor + (this.factor2 - this.factor) * 0.28F;
            if (Math.abs(this.factor2 - this.factor) < 0.5F) {
                this.factor = this.factor2;
            }

            NewClickGUI.handleInt2((int)this.x2 - 1, (int)this.y2 + 15, 103, n5 + 1);
            int n6 = (int)this.y2 + 15 + (int)this.factor;
            int n7 = (int)this.y2 + 15;
            int n8 = n7 + n5;

            for (NewclickComponent5 newclickComponent5 : this.entries) {
                int n9 = newclickComponent5.getCount5();
                if (n6 + n9 >= n7 && n6 <= n8) {
                    n9 = newclickComponent5.computeInt((int)this.x2, n6, 100, n, n2, inter18ptSemiboldTtfComponent);
                }

                n6 += n9;
            }

            NewClickGUI.process4();
        }

        if (navigationOnly) {
            int n10 = flag ? new Color(35, 35, 35, 240).getRGB() : new Color(24, 24, 24, 220).getRGB();
            Gui.drawRect((int)this.x2 - 1, (int)this.y2 + 15, (int)this.x2 + 101, (int)this.y2 + 15 + 12, n10);
            String str = this.combatType.checkState() ? "Click to return" : "Click to open page";
            inter18ptSemiboldTtfComponent.handleString2(str, (int)this.x2 + 4, (int)this.y2 + 15 + 3, ColorUtils.color15.getRGB());
        }
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }

    public void handleNewclickComponent5(NewclickComponent5 newclickComponent5) {
        for (NewclickComponent5 newclickComponent52 : this.entries) {
            if (newclickComponent52 != newclickComponent5) {
                newclickComponent52.flag = false;
            }
        }
    }

    public void process() {
        this.handleString(this.newClickGUI.getText(), this.newClickGUI.isFlag(), this.newClickGUI.getHypixelPitType());
    }

    public void keyTyped(char typedChar, int keyCode) {
        for (NewclickComponent5 newclickComponent5 : this.entries) {
            newclickComponent5.keyTyped(typedChar, keyCode);
        }
    }

    public void setY2(float y2) {
        this.y2 = y2;
    }

    public boolean matchesInt2(int n, int n2) {
        if (this.flag && !this.combatType.isNavigationOnly()) {
            int n3 = this.newClickGUI.computeInt((int)this.y2, this.computeInt());
            return n >= this.x2 - 1.0F && n <= this.x2 + 101.0F && n2 >= this.y2 + 15.0F && n2 <= this.y2 + 15.0F + n3;
        }
        return false;
    }

    private void handleString(String str, boolean flag, HypixelPitType hypixelPitType) {
        var modules = this.combatType.getModules(str, flag, hypixelPitType);
        HashMap map = new HashMap();

        for (NewclickComponent5 newclickComponent5 : this.entries) {
            map.put(newclickComponent5.getModule(), newclickComponent5);
        }

        this.entries.clear();

        for (Module module : modules) {
            NewclickComponent5 newclickComponent52 = (NewclickComponent5)map.get(module);
            if (newclickComponent52 == null) {
                newclickComponent52 = new NewclickComponent5(module, this);
            }

            this.entries.add(newclickComponent52);
        }
    }

    public float getY() {
        return this.y2;
    }

    public boolean checkState() {
        for (NewclickComponent5 newclickComponent5 : this.entries) {
            if (newclickComponent5.checkState()) {
                return true;
            }
        }

        return false;
    }

    public void handleInt(int n, int n2) {
        if (this.flag && !this.combatType.isNavigationOnly()) {
            int n3 = (int)this.y2 + 15 + (int)this.factor;

            for (NewclickComponent5 newclickComponent5 : this.entries) {
                newclickComponent5.handleInt(n, n2, (int)this.x2, n3, 100);
                n3 += newclickComponent5.count5;
            }
        }
    }

    public static void handleResourceLocation(ResourceLocation location, int n, int n2, int n3) {
        if (location != null && n3 > 0) {
            try {
                Minecraft mc = Minecraft.getMinecraft();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(location);
                Gui.drawModalRectWithCustomSizedTexture(n, n2, 0.0F, 0.0F, n3, n3, n3, n3);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            } catch (Exception e) {
                Gui.drawRect(n, n2, n + n3, n2 + n3, 1728053247);
            }
        }
    }

    private void process3() {
        String text = this.newClickGUI.getText();
        boolean flag = this.newClickGUI.isFlag();
        HypixelPitType hypixelPitType = this.newClickGUI.getHypixelPitType();
        if (!text.equals(this.text) || flag != this.flag2 || hypixelPitType != this.hypixelPitType) {
            this.text = text;
            this.flag2 = flag;
            this.hypixelPitType = hypixelPitType;
            this.process();
        }
    }

    public void handleInt(int n) {
        if (this.flag && !this.combatType.isNavigationOnly()) {
            this.factor2 += n;
        }
    }

    public int computeInt() {
        if (this.flag && !this.combatType.isNavigationOnly()) {
            int n = 0;

            for (NewclickComponent5 newclickComponent5 : this.entries) {
                n += newclickComponent5.count5;
            }

            return n;
        }
        return 0;
    }

    public CLIENTComponent(NewClickGUI newClickGUI, CombatType combatType, float x2, float y2, String str, boolean flag, HypixelPitType hypixelPitType) {
        this.newClickGUI = newClickGUI;
        this.combatType = combatType;
        this.x2 = x2;
        this.y2 = y2;
        this.handleString(str, flag, hypixelPitType);
    }
}
