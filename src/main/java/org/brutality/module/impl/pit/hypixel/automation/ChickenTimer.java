package org.brutality.module.impl.pit.hypixel.automation;

import java.util.Arrays;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.lwjgl.input.Mouse;

public class ChickenTimer extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 10.0, 0.0, 1920.0, 1.0);
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 130.0, 0.0, 1080.0, 1.0);
    private final BooleanSetting showLabelSetting = new BooleanSetting("Show Label", this, true);
    private static final long timestamp = 120000L;
    private final ModeSetting modeSetting = new ModeSetting("Suffix Mode", this, "#", Arrays.asList("#", "#.#"));
    private long timestamp2 = 0L;
    private boolean flag = false;

    private boolean checkState() {
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (heldItem == null) {
            return false;
        }
        int idFromItem = Item.getIdFromItem(heldItem.getItem());
        if (idFromItem != 365 && idFromItem != 366) {
            String displayName = heldItem.getDisplayName();
            return displayName != null && displayName.toLowerCase().contains("chicken");
        }
        return true;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.timestamp2 = 0L;
        this.flag = false;
    }

    public ChickenTimer() {
        super("Chicken Timer", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.xPosSetting, this.yPosSetting, this.showLabelSetting, this.modeSetting});
    }

    @Override
    public void onRender() {
        if (mc.thePlayer != null) {
            if (!this.checkState2()) {
                HudDragHelper.handleObject(this);
            } else {
                float f = (float)this.xPosSetting.computeDouble();
                float f2 = (float)this.yPosSetting.computeDouble();
                long l = this.timestamp2 - System.currentTimeMillis();
                String str;
                if (l <= 0L) {
                    str = (this.showLabelSetting.isEnabled() ? "§fChicken: " : "") + "§eReady";
                } else {
                    double d = l / 1000.0;
                    String str2 = this.modeSetting.matchesString2("#") ? String.format(Locale.ROOT, "%.0f", d) : String.format(Locale.ROOT, "%.1f", d);
                    str = (this.showLabelSetting.isEnabled() ? "§fChicken: " : "") + "§e" + str2;
                }

                if (!(mc.currentScreen instanceof GuiChat)) {
                    HudDragHelper.handleObject(this);
                }

                if (mc.currentScreen instanceof GuiChat && Mouse.isButtonDown(0)) {
                    int[] ints = HudDragHelper.computeIntArray();
                    if (!HudDragHelper.matchesObject(this)
                        && HudDragHelper.matchesInt(
                            ints[0],
                            ints[1],
                            f,
                            f2,
                            computeFontRenderer(ChickenTimer.class).getStringWidth(str),
                            computeFontRenderer(ChickenTimer.class).FONT_HEIGHT
                        )) {
                        HudDragHelper.matchesObject(this, ints[0], ints[1], (int)f, (int)f2);
                    }
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

                computeFontRenderer(ChickenTimer.class).drawStringWithShadow(str, f, f2, -1);
            }
        }
    }

    private boolean matchesItemStack(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        int idFromItem = Item.getIdFromItem(stack.getItem());
        if (idFromItem != 365 && idFromItem != 366) {
            String displayName = stack.getDisplayName();
            return displayName != null && displayName.toLowerCase().contains("chicken");
        }
        return true;
    }

    private boolean checkState2() {
        for (ItemStack stack : mc.thePlayer.inventory.mainInventory) {
            if (this.matchesItemStack(stack)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent4) {
                this.timestamp2 = 0L;
                this.flag = false;
            } else {
                if (event instanceof EventTick && ((EventTick)event).getState() == EventState.PRE) {
                    if (mc.thePlayer == null) {
                        return;
                    }

                    boolean usingItem = mc.thePlayer.isUsingItem();
                    if (usingItem && !this.flag && this.checkState() && System.currentTimeMillis() >= this.timestamp2) {
                        this.timestamp2 = System.currentTimeMillis() + 120000L;
                    }

                    this.flag = usingItem;
                }
            }
        }
    }
}
