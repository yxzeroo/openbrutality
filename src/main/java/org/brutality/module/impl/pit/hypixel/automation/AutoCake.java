package org.brutality.module.impl.pit.hypixel.automation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.lwjgl.input.Mouse;

public class AutoCake
extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private final BooleanSetting showNeededSetting = new BooleanSetting("Show Needed", this, true);
    private final NumberSetting scaleSetting = new NumberSetting("Scale", this, 1.0, 0.5, 2.0, 0.1);
    private int posX = 10;
    private int posY = 10;
    private int width = 150;
    private int height = 80;
    private final List<Entry> entries = new ArrayList<Entry>();
    private boolean flag = false;

    public AutoCake() {
        super("Auto Cake", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(this.showNeededSetting, this.scaleSetting);
        this.entries.add(new Entry("Milk Bucket", 2));
        this.entries.add(new Entry("Egg", 6));
        this.entries.add(new Entry("Sugar Extract", 12));
        this.entries.add(new Entry("Hay Bale", 40));
        this.entries.add(new Entry("Cherry", 1));
    }

    private void handleInt(int n, int n2, int n3, int n4, int n5, int n6) {
        Gui.drawRect(n, n2, n + n3, n2 + n5, n6);
        Gui.drawRect(n, n2 + n4 - n5, n + n3, n2 + n4, n6);
        Gui.drawRect(n, n2, n + n5, n2 + n4, n6);
        Gui.drawRect(n + n3 - n5, n2, n + n3, n2 + n4, n6);
    }

    @Override
    public void onEvent(Event event) {
        if (!this.checkState44()) {
            return;
        }
        if (event instanceof EventUpdate) {
            this.process();
        } else if (event instanceof EventRender2D && this.showNeededSetting.isEnabled()) {
            this.process4();
        }
    }

    private void process() {
        String string;
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        if (AutoCake.mc.thePlayer != null && AutoCake.mc.thePlayer.inventory != null) {
            for (ItemStack entry2 : AutoCake.mc.thePlayer.inventory.mainInventory) {
                if (entry2 == null) continue;
                string = entry2.getDisplayName().replaceAll("\u00a7.", "");
                hashMap.put(string, hashMap.getOrDefault(string, 0) + entry2.stackSize);
            }
        }
        for (Entry entry : this.entries) {
            entry.count2 = 0;
            for (Map.Entry entry2 : hashMap.entrySet()) {
                string = (String)entry2.getKey();
                if (!this.matchesString(string, entry.name)) continue;
                entry.count2 += ((Integer)entry2.getValue()).intValue();
            }
        }
    }

    private boolean matchesString(String string, String string2) {
        String string3;
        String string4 = string.toLowerCase().trim();
        if (string4.equals(string3 = string2.toLowerCase().trim())) {
            return true;
        }
        if (string4.contains(string3) || string3.contains(string4)) {
            return true;
        }
        if (string3.contains("milk") && string4.contains("milk")) {
            return true;
        }
        if (string3.contains("egg") && string4.contains("egg")) {
            return true;
        }
        if (string3.contains("sugar") && string4.contains("sugar")) {
            return true;
        }
        if (string3.contains("hay") && string4.contains("hay")) {
            return true;
        }
        return string3.contains("cherry") && string4.contains("cherry");
    }

    @Override
    public void onDisable() {
        if (HudDragHelper.matchesObject(this)) {
            HudDragHelper.handleObject(this);
        }
        super.onDisable();
    }

    private void process3() {
        if (AutoCake.mc.currentScreen instanceof GuiChat) {
            int[] nArray;
            int[] nArray2 = HudDragHelper.computeIntArray();
            int n = nArray2[0];
            int n2 = nArray2[1];
            boolean bl = Mouse.isButtonDown(0);
            float f = (float)this.scaleSetting.computeDouble();
            int n3 = (int)((float)this.width * f);
            int n4 = (int)((float)this.height * f);
            if (bl && !this.flag) {
                if (HudDragHelper.matchesInt(n, n2, this.posX, this.posY, n3, n4)) {
                    HudDragHelper.matchesObject(this, n, n2, this.posX, this.posY);
                }
            } else if (!bl && this.flag) {
                HudDragHelper.handleObject(this);
            }
            if (HudDragHelper.matchesObject(this) && (nArray = HudDragHelper.computeIntArray(this)) != null) {
                this.posX = nArray[0];
                this.posY = nArray[1];
            }
            this.flag = bl;
        } else {
            if (HudDragHelper.matchesObject(this)) {
                HudDragHelper.handleObject(this);
            }
            this.flag = false;
        }
    }

    private void process4() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        this.process3();
        float f = (float)this.scaleSetting.computeDouble();
        int n = (int)((float)this.width * f);
        int n2 = (int)((float)(20 + this.entries.size() * 12) * f);
        this.height = 20 + this.entries.size() * 12;
        this.posX = Math.max(0, Math.min(this.posX, scaledResolution.getScaledWidth() - n));
        this.posY = Math.max(0, Math.min(this.posY, scaledResolution.getScaledHeight() - n2));
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.posX, this.posY, 0.0f);
        GlStateManager.scale(f, f, 1.0f);
        Gui.drawRect(0, 0, this.width, this.height, new Color(0, 0, 0, 150).getRGB());
        this.handleInt(0, 0, this.width, this.height, 2, new Color(100, 100, 100, 200).getRGB());
        String string = "Auto Cake";
        AutoCake.computeFontRenderer(AutoCake.class).drawStringWithShadow(string, 4.0f, 4.0f, 0xFFFFFF);
        int n3 = 16;
        for (Entry entry : this.entries) {
            String string2 = entry.count2 >= entry.count ? "\u2714" : "\u2716";
            int n4 = this.computeInt(entry.count2, entry.count);
            String string3 = string2 + " " + entry.count2 + "/" + entry.count + " " + entry.name;
            AutoCake.computeFontRenderer(AutoCake.class).drawStringWithShadow(string3, 4.0f, n3, n4);
            n3 += 12;
        }
        GlStateManager.popMatrix();
    }

    private int computeInt(int n, int n2) {
        if (n >= n2) {
            return 65280;
        }
        float f = (float)n / (float)n2;
        if ((double)f >= 0.75) {
            return 0xFFFF00;
        }
        if ((double)f >= 0.5) {
            return 16753920;
        }
        if ((double)f >= 0.25) {
            return 16747520;
        }
        return 0xFF0000;
    }

    public static class Entry {
        public String name;
        public int count;
        public int count2;

        public Entry(String string, int n) {
            this.name = string;
            this.count = n;
            this.count2 = 0;
        }
    }
}
