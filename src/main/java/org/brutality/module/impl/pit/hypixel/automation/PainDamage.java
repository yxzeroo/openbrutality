package org.brutality.module.impl.pit.hypixel.automation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
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

public class PainDamage extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 5.0, 0.0, 1200.0, 1.0);
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1.0);
    private final BooleanSetting showInChatSetting = new BooleanSetting("Show In Chat", this, false);
    private String text = "Pain: 0%";

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
                        String str = flag ? "Pain: " + this.text : "Pain: 0%";
                        int textWidth = computeFontRenderer(PainDamage.class).getStringWidth(str);
                        int FONT_HEIGHT = computeFontRenderer(PainDamage.class).FONT_HEIGHT;
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
                computeFontRenderer(PainDamage.class).drawStringWithShadow("Pain: ", f, f2, 16777215);
                computeFontRenderer(PainDamage.class)
                    .drawStringWithShadow(this.text, f + computeFontRenderer(PainDamage.class).getStringWidth("Pain: "), f2, 11141120);
            } else if (flag2 && this.showInChatSetting.isEnabled()) {
                computeFontRenderer(PainDamage.class).drawStringWithShadow("Pain: ", f, f2, 16777215);
                computeFontRenderer(PainDamage.class)
                    .drawStringWithShadow("0%", f + computeFontRenderer(PainDamage.class).getStringWidth("Pain: "), f2, 11141120);
            }
        }
    }

    private boolean matchesEntityPlayerSP(EntityPlayerSP player) {
        ItemStack heldItem = player.getHeldItem();
        if (heldItem != null && heldItem.hasDisplayName()) {
            NBTTagCompound tagCompound = heldItem.getTagCompound();
            if (tagCompound != null) {
                return tagCompound.toString().contains("Pain Focus");
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

    public PainDamage() {
        super("PainDamage", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.xPosSetting, this.yPosSetting, this.showInChatSetting});
    }

    @Override
    public void onDisable() {
        super.onDisable();
        HudDragHelper.handleObject(this);
    }

    private void handleEntityPlayerSP(EntityPlayerSP player) {
        ItemStack heldItem = player.getHeldItem();
        if (heldItem != null && heldItem.hasDisplayName()) {
            NBTTagCompound tagCompound = heldItem.getTagCompound();
            if (tagCompound != null) {
                byte b = 1;
                if (tagCompound.toString().contains("Pain Focus III")) {
                    b = 3;
                } else if (tagCompound.toString().contains("Pain Focus II")) {
                    b = 2;
                }

                int n = (int)((player.getMaxHealth() - player.getHealth()) / 2.0F);
                int n2 = 0;
                if (b == 1) {
                    n2 = n;
                } else if (b == 2) {
                    n2 = 2 * n;
                } else {
                    n2 = 5 * n;
                }

                this.text = "§4" + n2 + "%";
            } else {
                this.text = "§40%";
            }
        } else {
            this.text = "§40%";
        }
    }
}
