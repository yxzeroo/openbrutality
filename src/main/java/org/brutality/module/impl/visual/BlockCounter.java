package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.lwjgl.input.Mouse;

public class BlockCounter extends Module {
    private final ModeSetting adjustSetting = new ModeSetting("Mode", this, "Phase", Arrays.asList("Phase", "Box", "Adjust"));
    private final NumberSetting xSetting = new NumberSetting("X", this, 20.0, 0.0, 2000.0, 1.0);
    private final NumberSetting ySetting = new NumberSetting("Y", this, 70.0, 0.0, 2000.0, 1.0);
    private boolean dragging;
    private float factor;
    private float factor2;
    private int count;
    private int count2;
    private static float factor3 = 90.0F;
    private static float factor4 = 40.0F;
    private static final long timestamp = -7995145629389846273L;

    private boolean checkState() {
        return mc.currentScreen instanceof GuiChat;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (mc.thePlayer != null) {
                if (event instanceof EventRender2D event2) {
                    this.count2 = this.computeInt();
                    if (this.count < this.count2) {
                        this.count = this.count2;
                    }

                    if (this.checkState()) {
                        this.onEventRender2D(event2);
                        if (mc.currentScreen instanceof GuiChat) {
                            this.handleScaledResolution(event2.getScaledResolution());
                        }
                    }
                }
            }
        }
    }

    public BlockCounter() {
        super("BlockCounter", 0, Category.VISUAL);
        this.process18();
    }

    private void onEventRender2D(EventRender2D event) {
        float f = (float)this.xSetting.computeDouble();
        float f2 = (float)this.ySetting.computeDouble();
        Color color = new Color((int)timestamp);
        String text = this.adjustSetting.getText();
        switch (text) {
            case "Phase":
                factor3 = 90.0F;
                factor4 = 40.0F;
                Gui.drawRect((int)f, (int)f2, (int)(f + factor3), (int)(f2 + factor4), new Color(20, 20, 20, 200).getRGB());
                ItemStack stack = this.computeItemStack();
                if (stack != null) {
                    mc.getRenderItem().renderItemIntoGUI(stack, (int)f, (int)(f2 + 1.0F));
                }

                computeFontRenderer(BlockCounter.class).drawStringWithShadow("Blocks", f + 38.0F, f2 + 4.0F, color.getRGB());
                computeFontRenderer(BlockCounter.class).drawStringWithShadow(String.valueOf(this.count2), f + 38.0F, f2 + 15.0F, Color.WHITE.getRGB());
                break;
            case "Box":
                factor3 = 90.0F;
                factor4 = 20.0F;
                float f3 = factor3 - 4.0F;
                float f4 = f3 * ((float)this.count2 / Math.max(1, this.count));
                Gui.drawRect((int)f, (int)f2, (int)(f + factor3), (int)(f2 + factor4), new Color(20, 20, 20, 200).getRGB());
                Gui.drawRect((int)(f + 2.0F), (int)(f2 + 14.0F), (int)(f + 2.0F + f3), (int)(f2 + 17.0F), new Color(20, 20, 20, 50).getRGB());
                Gui.drawRect((int)(f + 2.0F), (int)(f2 + 14.0F), (int)(f + 2.0F + f4), (int)(f2 + 17.0F), color.getRGB());
                computeFontRenderer(BlockCounter.class).drawStringWithShadow("Blocks", f + 1.0F, f2 + 1.0F, -1);
                computeFontRenderer(BlockCounter.class)
                    .drawStringWithShadow(
                        String.valueOf(this.count2),
                        f + factor3 - computeFontRenderer(BlockCounter.class).getStringWidth(String.valueOf(this.count2)) - 4.0F,
                        f2 + 1.0F,
                        -1
                    );
                break;
            case "Adjust":
                String str = this.count2 + " blocks";
                float textWidth = computeFontRenderer(BlockCounter.class).getStringWidth(str);
                int scaledWidth = event.getScaledResolution().getScaledWidth();
                computeFontRenderer(BlockCounter.class).drawStringWithShadow(str, scaledWidth / 2.0F - textWidth / 2.0F, f2 - 83.0F, color.getRGB());
        }

        if (this.dragging && mc.currentScreen instanceof GuiChat) {
            Gui.drawRect((int)(f - 1.0F), (int)(f2 - 1.0F), (int)(f + factor3 + 1.0F), (int)(f2 + 1.0F), color.getRGB());
            Gui.drawRect((int)(f - 1.0F), (int)(f2 + factor4), (int)(f + factor3 + 1.0F), (int)(f2 + factor4 + 1.0F), color.getRGB());
            Gui.drawRect((int)(f - 1.0F), (int)f2, (int)(f + 1.0F), (int)(f2 + factor4), color.getRGB());
            Gui.drawRect((int)(f + factor3), (int)f2, (int)(f + factor3 + 1.0F), (int)(f2 + factor4), color.getRGB());
        }
    }

    private ItemStack computeItemStack() {
        ItemStack stack = null;

        for (int i = 0; i < 9; i++) {
            ItemStack stack2 = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack2 != null && stack2.getItem() instanceof ItemBlock && (stack == null || stack2.stackSize > stack.stackSize)) {
                stack = stack2;
            }
        }

        return stack;
    }

    private int computeInt() {
        int n = 0;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemBlock) {
                n += stack.stackSize;
            }
        }

        return n;
    }

    private void handleScaledResolution(ScaledResolution sr) {
        int n = Mouse.getX() * sr.getScaledWidth() / mc.displayWidth;
        int n2 = sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / mc.displayHeight - 1;
        if (Mouse.isButtonDown(0)) {
            if (!this.dragging
                && n >= this.xSetting.computeDouble()
                && n <= this.xSetting.computeDouble() + factor3
                && n2 >= this.ySetting.computeDouble()
                && n2 <= this.ySetting.computeDouble() + factor4) {
                this.dragging = true;
                this.factor = n - (float)this.xSetting.computeDouble();
                this.factor2 = n2 - (float)this.ySetting.computeDouble();
            }

            if (this.dragging) {
                this.xSetting.handleDouble2(n - this.factor);
                this.ySetting.handleDouble2(n2 - this.factor2);
            }
        } else {
            this.dragging = false;
        }
    }
}
