package org.brutality.module.impl.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.performance.InvalidateHelper;
import org.lwjgl.input.Mouse;

public class ArmorHUD extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final BooleanSetting helmetSetting = new BooleanSetting("Helmet", this, true);
    private final BooleanSetting chestplateSetting = new BooleanSetting("Chestplate", this, true);
    private final BooleanSetting leggingsSetting = new BooleanSetting("Leggings", this, true);
    private final BooleanSetting bootsSetting = new BooleanSetting("Boots", this, true);
    private final BooleanSetting heldItemSetting = new BooleanSetting("HeldItem", this, true);
    private final NumberSetting xPositionSetting = new NumberSetting("XPosition", this, 500.0, 0.0, 5000.0, 1.0);
    private final NumberSetting yPositionSetting = new NumberSetting("YPosition", this, 500.0, 0.0, 5000.0, 1.0);

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }

    @Override
    public void onRender() {
        ScaledResolution sr = InvalidateHelper.computeScaledResolution(this.mc);
        int n = sr.getScaledWidth() - this.computeInt();
        int n2 = sr.getScaledHeight() - this.computeInt2();
        float f = Math.max(0.0F, Math.min((float)this.xPositionSetting.computeDouble(), (float)n));
        float f2 = Math.max(0.0F, Math.min((float)this.yPositionSetting.computeDouble(), (float)n2));
        ItemStack[] armorInventory = this.mc.thePlayer.inventory.armorInventory;
        boolean flag = this.helmetSetting.isEnabled() && armorInventory[3] != null
            || this.chestplateSetting.isEnabled() && armorInventory[2] != null
            || this.leggingsSetting.isEnabled() && armorInventory[1] != null
            || this.bootsSetting.isEnabled() && armorInventory[0] != null
            || this.heldItemSetting.isEnabled() && this.mc.thePlayer.getHeldItem() != null;
        if (!flag) {
            HudDragHelper.handleObject(this);
        } else {
            if (this.mc.currentScreen instanceof GuiChat) {
                int[] ints = HudDragHelper.computeIntArray();
                int n3 = ints[0];
                int n4 = ints[1];
                int n5 = this.computeInt();
                int n6 = this.computeInt2();
                if (Mouse.isButtonDown(0)) {
                    if (!HudDragHelper.matchesObject(this) && HudDragHelper.matchesInt(n3, n4, f, f2, n5, n6)) {
                        HudDragHelper.matchesObject(this, n3, n4, (int)f, (int)f2);
                    }
                } else {
                    HudDragHelper.handleObject(this);
                }

                if (HudDragHelper.matchesObject(this)) {
                    int[] ints2 = HudDragHelper.computeIntArray(this);
                    if (ints2 != null) {
                        int n7 = Math.max(0, Math.min(ints2[0], n));
                        int n8 = Math.max(0, Math.min(ints2[1], n2));
                        this.xPositionSetting.handleDouble2(n7);
                        this.yPositionSetting.handleDouble2(n8);
                        f = n7;
                        f2 = n8;
                    }
                }
            } else {
                HudDragHelper.handleObject(this);
            }

            if (flag) {
                this.handleInt((int)f, (int)f2);
            }
        }
    }

    public ArmorHUD() {
        super("Armor HUD", 0, Category.VISUAL);
        this.handleSettingArray(
            new Setting[]{
                this.helmetSetting,
                this.chestplateSetting,
                this.leggingsSetting,
                this.bootsSetting,
                this.heldItemSetting,
                this.xPositionSetting,
                this.yPositionSetting
            }
        );
    }

    @Override
    public void onDisable() {
        super.onDisable();
        HudDragHelper.handleObject(this);
    }

    private void handleItemStack(ItemStack stack, int n, int n2) {
        this.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, n, n2);
    }

    private void handleInt(int n, int n2) {
        int n3 = 0;
        ItemStack[] armorInventory = this.mc.thePlayer.inventory.armorInventory;
        if (this.helmetSetting.isEnabled() && armorInventory[3] != null) {
            this.handleItemStack(armorInventory[3], n + n3, n2);
            n3 += 16;
        }

        if (this.chestplateSetting.isEnabled() && armorInventory[2] != null) {
            this.handleItemStack(armorInventory[2], n + n3, n2);
            n3 += 16;
        }

        if (this.leggingsSetting.isEnabled() && armorInventory[1] != null) {
            this.handleItemStack(armorInventory[1], n + n3, n2);
            n3 += 16;
        }

        if (this.bootsSetting.isEnabled() && armorInventory[0] != null) {
            this.handleItemStack(armorInventory[0], n + n3, n2);
            n3 += 16;
        }

        if (this.heldItemSetting.isEnabled() && this.mc.thePlayer.getHeldItem() != null) {
            this.handleItemStack(this.mc.thePlayer.getHeldItem(), n + n3, n2);
        }
    }

    private int computeInt() {
        int n = 0;
        ItemStack[] armorInventory = this.mc.thePlayer.inventory.armorInventory;
        if (this.helmetSetting.isEnabled() && armorInventory[3] != null) {
            n += 16;
        }

        if (this.chestplateSetting.isEnabled() && armorInventory[2] != null) {
            n += 16;
        }

        if (this.leggingsSetting.isEnabled() && armorInventory[1] != null) {
            n += 16;
        }

        if (this.bootsSetting.isEnabled() && armorInventory[0] != null) {
            n += 16;
        }

        if (this.heldItemSetting.isEnabled() && this.mc.thePlayer.getHeldItem() != null) {
            n += 16;
        }

        return Math.max(n, 16);
    }

    private int computeInt2() {
        return 16;
    }
}
