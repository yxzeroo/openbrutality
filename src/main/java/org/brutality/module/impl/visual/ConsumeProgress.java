package org.brutality.module.impl.visual;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;

public class ConsumeProgress extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private static final float factor = 429.0F;
    private static final float factor2 = 225.0F;
    private static final float factor3 = 5.0F;
    private static final float factor4 = 100.0F;
    private static final float factor5 = 1.0F;
    private static final Color color = new Color(204, 202, 197);
    private static final Color color2 = new Color(0, 0, 79);
    private boolean flag = false;
    private boolean flag2 = false;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventRender2D) {
                this.process();
                if (this.flag) {
                    this.process3();
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.flag = false;
        this.flag2 = false;
    }

    private void process() {
        EntityPlayerSP player = this.mc.thePlayer;
        if (player == null) {
            this.flag = false;
            this.flag2 = false;
        } else {
            boolean flag = player.isUsingItem()
                && player.getItemInUse() != null
                && (
                    player.getItemInUse().getItem().getItemUseAction(player.getItemInUse()) == EnumAction.EAT
                        || player.getItemInUse().getItem().getItemUseAction(player.getItemInUse()) == EnumAction.DRINK
                );
            if (flag) {
                this.flag = true;
                this.flag2 = true;
            } else if (this.flag2) {
                this.flag = false;
                this.flag2 = false;
            }
        }
    }

    private float computeFloat(EntityPlayer player) {
        if (player != null && player.isUsingItem()) {
            ItemStack itemInUse = player.getItemInUse();
            if (itemInUse == null) {
                return 0.0F;
            }
            int maxItemUseDuration = itemInUse.getMaxItemUseDuration();
            int itemInUseCount = player.getItemInUseCount();
            int n = maxItemUseDuration - itemInUseCount;
            float f = 1.0F - (float)n / maxItemUseDuration;
            return Math.max(0.0F, Math.min(1.0F, f));
        }
        return 0.0F;
    }

    private void process3() {
        EntityPlayerSP player = this.mc.thePlayer;
        if (player != null) {
            float f = this.computeFloat(player);
            this.handleFloat(429.0F, 225.0F, f * 100.0F, 5.0F, 100.0F, 1.0F, color, color2);
        }
    }

    private void handleFloat(float f, float f2, float f3, float f4, int n) {
        Gui.drawRect((int)f, (int)f2, (int)f3, (int)f4, n);
    }

    private void handleFloat(float f, float f2, float f3, float f4, float f5, float f6, Color color, Color color2) {
        this.handleFloat(f, f2, f + f5, f2 + f4, new Color(35, 36, 35).getRGB());
        this.handleFloat(f - f6, f2 - f6, f + f5 + f6, f2, Color.BLACK.getRGB());
        this.handleFloat(f - f6, f2 + f4, f + f5 + f6, f2 + f4 + f6, Color.BLACK.getRGB());
        this.handleFloat(f - f6, f2, f, f2 + f4, Color.BLACK.getRGB());
        this.handleFloat(f + f5, f2, f + f5 + f6, f2 + f4, Color.BLACK.getRGB());

        for (int i = 0; i < f3; i++) {
            float f7 = i / f3;
            int n = (int)(color.getRed() * (1.0F - f7) + color2.getRed() * f7);
            int n2 = (int)(color.getGreen() * (1.0F - f7) + color2.getGreen() * f7);
            int n3 = (int)(color.getBlue() * (1.0F - f7) + color2.getBlue() * f7);
            this.handleFloat(f + i, f2, f + i + 1.0F, f2 + f4, new Color(n, n2, n3).getRGB());
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.flag = false;
        this.flag2 = false;
    }

    public ConsumeProgress() {
        super("ConsumeProgress", 0, Category.VISUAL);
    }
}
