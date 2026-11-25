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
import org.lwjgl.input.Mouse;

public class SharkDamage extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1.0);
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 5.0, 0.0, 1200.0, 1.0);
    private final BooleanSetting showInChatSetting = new BooleanSetting("Show In Chat", this, false);
    private String text = "Shark: 0%";

    private int computeInt(EntityPlayerSP player) {
        int n = 0;

        for (EntityPlayer playerEntity : Minecraft.getMinecraft().theWorld.playerEntities) {
            if (playerEntity != player && playerEntity.getDistanceToEntity(player) <= 12.0F && playerEntity.getHealth() < 12.0F) {
                n++;
            }
        }

        return n;
    }

    private void handleEntityPlayerSP(EntityPlayerSP player) {
        ItemStack heldItem = player.getHeldItem();
        if (heldItem != null && heldItem.hasDisplayName()) {
            NBTTagCompound tagCompound = heldItem.getTagCompound();
            if (tagCompound != null) {
                byte b = 0;
                if (tagCompound.toString().contains("Shark III")) {
                    b = 3;
                } else if (tagCompound.toString().contains("Shark II")) {
                    b = 2;
                } else if (tagCompound.toString().contains("Shark")) {
                    b = 1;
                }

                if (b > 0) {
                    int n = this.computeInt(player);
                    int n2 = 0;
                    if (b == 1) {
                        n2 = 2 * n;
                    } else if (b == 2) {
                        n2 = 4 * n;
                    } else {
                        n2 = 7 * n;
                    }

                    this.text = n2 + "%";
                } else {
                    this.text = "0%";
                }
            } else {
                this.text = "0%";
            }
        } else {
            this.text = "0%";
        }
    }

    private boolean matchesEntityPlayerSP(EntityPlayerSP player) {
        ItemStack heldItem = player.getHeldItem();
        if (heldItem != null && heldItem.hasDisplayName()) {
            NBTTagCompound tagCompound = heldItem.getTagCompound();
            if (tagCompound != null) {
                return tagCompound.toString().contains("Shark");
            }
        }

        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        HudDragHelper.handleObject(this);
    }

    public SharkDamage() {
        super("SharkDamage", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.xPosSetting, this.yPosSetting, this.showInChatSetting});
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
                        String str = flag ? "Shark: " + this.text : "Shark: 0%";
                        int textWidth = computeFontRenderer(SharkDamage.class).getStringWidth(str);
                        int FONT_HEIGHT = computeFontRenderer(SharkDamage.class).FONT_HEIGHT;
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
                this.handleEntityPlayerSP(mc.thePlayer);
                computeFontRenderer(SharkDamage.class).drawStringWithShadow("Shark: ", f, f2, 16777215);
                computeFontRenderer(SharkDamage.class)
                    .drawStringWithShadow(this.text, f + computeFontRenderer(SharkDamage.class).getStringWidth("Shark: "), f2, 16733525);
            } else if (flag2 && this.showInChatSetting.isEnabled()) {
                computeFontRenderer(SharkDamage.class).drawStringWithShadow("Shark: ", f, f2, 16777215);
                computeFontRenderer(SharkDamage.class)
                    .drawStringWithShadow("0%", f + computeFontRenderer(SharkDamage.class).getStringWidth("Shark: "), f2, 16733525);
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }
}
