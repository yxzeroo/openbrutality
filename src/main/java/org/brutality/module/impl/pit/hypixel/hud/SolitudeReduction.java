package org.brutality.module.impl.pit.hypixel.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.performance.EntityCache;
import org.lwjgl.input.Mouse;

public class SolitudeReduction extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 90.0, 0.0, 1200.0, 1.0);
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 5.0, 0.0, 1200.0, 1.0);
    private final BooleanSetting showInChatSetting = new BooleanSetting("Show In Chat", this, false);
    private final BooleanSetting showPercentageSetting = new BooleanSetting("Show Percentage", this, true);
    private final BooleanSetting showPlayerCountSetting = new BooleanSetting("Show Player Count", this, true);

    @Override
    public void onDisable() {
        super.onDisable();
        HudDragHelper.handleObject(this);
    }

    private String computeString(int n, int n2) {
        if (n2 == 0) {
            return "Disabled";
        }
        boolean flag = false;
        String str = "";
        switch (n2) {
            case 1:
                flag = n <= 1;
                str = this.showPercentageSetting.isEnabled() ? "-40%" : "Active";
                break;
            case 2:
                flag = n <= 2;
                str = this.showPercentageSetting.isEnabled() ? "-50%" : "Active";
                break;
            case 3:
                flag = n <= 2;
                str = this.showPercentageSetting.isEnabled() ? "-60%" : "Active";
        }

        if (flag) {
            return str;
        }
        return this.showPlayerCountSetting.isEnabled() ? n + " Players" : "Inactive";
    }

    @Override
    public void onRender() {
        Minecraft mc = Minecraft.getMinecraft();
        float f = (float)this.xPosSetting.computeDouble();
        float f2 = (float)this.yPosSetting.computeDouble();
        boolean flag = this.matchesEntityPlayerSP(mc.thePlayer);
        boolean flag2 = mc.currentScreen instanceof GuiChat;
        boolean flag3 = flag || flag2 && this.showInChatSetting.isEnabled();
        if (!flag3) {
            HudDragHelper.handleObject(this);
        } else {
            if (flag2) {
                int[] ints = HudDragHelper.computeIntArray();
                int n = ints[0];
                int n2 = ints[1];
                if (Mouse.isButtonDown(0)) {
                    if (!HudDragHelper.matchesObject(this)) {
                        String str = this.computeString();
                        int textWidth = computeFontRenderer(SolitudeReduction.class).getStringWidth(str);
                        int FONT_HEIGHT = computeFontRenderer(SolitudeReduction.class).FONT_HEIGHT;
                        if (HudDragHelper.matchesInt(n, n2, f, f2, textWidth, FONT_HEIGHT)) {
                            HudDragHelper.matchesObject(this, n, n2, (int)f, (int)f2);
                        }
                    }
                } else {
                    HudDragHelper.handleObject(this);
                }

                if (HudDragHelper.matchesObject(this)) {
                    int[] ints2 = HudDragHelper.computeIntArray(this);
                    if (ints2 != null) {
                        this.xPosSetting.handleDouble2(ints2[0]);
                        this.yPosSetting.handleDouble2(ints2[1]);
                        f = ints2[0];
                        f2 = ints2[1];
                    }
                }
            } else {
                HudDragHelper.handleObject(this);
            }

            if (flag) {
                this.handleMinecraft(mc, f, f2);
            } else if (flag2 && this.showInChatSetting.isEnabled()) {
                computeFontRenderer(SolitudeReduction.class).drawStringWithShadow("Solitude: ", f, f2, 16777215);
                computeFontRenderer(SolitudeReduction.class)
                    .drawStringWithShadow("Disabled", f + computeFontRenderer(SolitudeReduction.class).getStringWidth("Solitude: "), f2, 16733525);
            }
        }
    }

    private int computeInt() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null && mc.thePlayer != null) {
            int n = 0;

            for (EntityPlayer player : EntityCache.getEntries()) {
                if (player != mc.thePlayer && mc.thePlayer.getDistanceToEntity(player) <= 7.0) {
                    n++;
                }
            }

            return n;
        }
        return 0;
    }

    private void handleMinecraft(Minecraft mc, float f, float f2) {
        computeFontRenderer(SolitudeReduction.class).drawStringWithShadow("Solitude: ", f, f2, 16777215);
        int n = this.computeInt();
        int n2 = this.computeInt(mc.thePlayer);
        String str = this.computeString(n, n2);
        int n3 = this.computeInt(n, n2);
        computeFontRenderer(SolitudeReduction.class)
            .drawStringWithShadow(str, f + computeFontRenderer(SolitudeReduction.class).getStringWidth("Solitude: "), f2, n3);
    }

    private boolean matchesEntityPlayerSP(EntityPlayerSP player) {
        ItemStack[] armorInventory = player.inventory.armorInventory;
        if (armorInventory[1] != null) {
            ItemStack stack = armorInventory[1];
            if (stack.hasTagCompound()) {
                NBTTagCompound tagCompound = stack.getTagCompound();
                return tagCompound.toString().contains("Solitude");
            }
        }

        return false;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }

    private int computeInt(EntityPlayerSP player) {
        ItemStack[] armorInventory = player.inventory.armorInventory;
        if (armorInventory[1] != null) {
            ItemStack stack = armorInventory[1];
            if (stack.hasTagCompound()) {
                NBTTagCompound tagCompound = stack.getTagCompound();
                String str = tagCompound.toString();
                if (str.contains("Solitude III")) {
                    return 3;
                }

                if (str.contains("Solitude II")) {
                    return 2;
                }

                if (str.contains("Solitude")) {
                    return 1;
                }
            }
        }

        return 0;
    }

    public SolitudeReduction() {
        super("SolitudeReduction", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{this.xPosSetting, this.yPosSetting, this.showInChatSetting, this.showPercentageSetting, this.showPlayerCountSetting}
        );
    }

    private int computeInt(int n, int n2) {
        if (n2 == 0) {
            return 16733525;
        }
        boolean flag = false;
        switch (n2) {
            case 1:
                flag = n <= 1;
                break;
            case 2:
            case 3:
                flag = n <= 2;
        }

        return flag ? 5635925 : 16733525;
    }

    private String computeString() {
        Minecraft mc = Minecraft.getMinecraft();
        if (!this.matchesEntityPlayerSP(mc.thePlayer)) {
            return "Solitude: Disabled";
        }
        int n = this.computeInt();
        int n2 = this.computeInt(mc.thePlayer);
        String str = this.computeString(n, n2);
        return "Solitude: " + str;
    }
}
