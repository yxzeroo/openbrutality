package org.brutality.module.impl.visual;

import java.util.Arrays;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.EditableModulesHelper;
import org.brutality.utils.HudDragHelper;
import org.lwjgl.input.Mouse;

public final class BlockCounterHUD extends Module {
    private final ModeSetting currentTotalSetting = new ModeSetting(
        "Count Mode", this, "Current + Total", Arrays.asList("Current", "Hotbar Total", "Current + Total")
    );
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 0.0, -2000.0, 2000.0, 1.0);
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 20.0, -2000.0, 2000.0, 1.0);

    @Override
    public void onDisable() {
        HudDragHelper.handleObject(this);
        super.onDisable();
    }

    @Override
    public void onRender() {
        if (mc.thePlayer == null) {
            HudDragHelper.handleObject(this);
        } else {
            ItemStack heldItem = mc.thePlayer.getHeldItem();
            boolean flag = heldItem != null && heldItem.getItem() instanceof ItemBlock && heldItem.stackSize > 0;
            boolean active = EditableModulesHelper.isActive();
            if (!flag && !active) {
                HudDragHelper.handleObject(this);
            } else {
                int n = flag ? heldItem.stackSize : 38;
                int n2 = flag ? computeInt(mc.thePlayer.inventory) : 128;
                String str = computeString(this.currentTotalSetting.getText(), n, n2);
                FontRenderer fontRenderer = this.computeFontRenderer2();
                if (fontRenderer == null) {
                    fontRenderer = computeFontRenderer(BlockCounterHUD.class);
                }

                ScaledResolution sr = new ScaledResolution(mc);
                int textWidth = fontRenderer.getStringWidth(str);
                int FONT_HEIGHT = fontRenderer.FONT_HEIGHT;
                float f = (sr.getScaledWidth() - textWidth) / 2.0F;
                float f2 = (sr.getScaledHeight() - FONT_HEIGHT) / 2.0F;
                float f3 = f + (float)this.xPosSetting.computeDouble();
                float f4 = f2 + (float)this.yPosSetting.computeDouble();
                if (active) {
                    int[] ints = HudDragHelper.computeIntArray();
                    if (Mouse.isButtonDown(0)) {
                        if (!HudDragHelper.matchesObject(this) && HudDragHelper.matchesInt(ints[0], ints[1], f3, f4, textWidth, FONT_HEIGHT)) {
                            HudDragHelper.matchesObject(this, ints[0], ints[1], (int)f3, (int)f4);
                        }

                        int[] ints2 = HudDragHelper.computeIntArray(this);
                        if (ints2 != null) {
                            f3 = Math.max(0, Math.min(ints2[0], sr.getScaledWidth() - textWidth));
                            f4 = Math.max(0, Math.min(ints2[1], sr.getScaledHeight() - FONT_HEIGHT));
                            this.xPosSetting.handleDouble2(f3 - f);
                            this.yPosSetting.handleDouble2(f4 - f2);
                        }
                    } else {
                        HudDragHelper.handleObject(this);
                    }
                } else {
                    HudDragHelper.handleObject(this);
                }

                fontRenderer.drawStringWithShadow(str, f3, f4, -1);
            }
        }
    }

    public static int computeInt(InventoryPlayer inventoryPlayer) {
        int n = 0;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventoryPlayer.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemBlock) {
                n += Math.max(0, stack.stackSize);
            }
        }

        return n;
    }

    public BlockCounterHUD() {
        super("BlockCounterHUD", 0, Category.VISUAL);
        this.process18();
        this.handleSettingArray(new Setting[]{this.currentTotalSetting, this.xPosSetting, this.yPosSetting});
    }

    @Override
    public void onEvent(Event event) {
    }

    public static String computeString(String str, int n, int n2) {
        if ("Hotbar Total".equals(str)) {
            return n2 + " blocks";
        }
        return "Current + Total".equals(str) ? n + " (" + n2 + ") blocks" : n + " blocks";
    }
}
