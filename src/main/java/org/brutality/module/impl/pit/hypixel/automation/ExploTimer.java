package org.brutality.module.impl.pit.hypixel.automation;

import java.util.Arrays;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.player.GetPlayerEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;
import org.brutality.utils.HudDragHelper;
import org.lwjgl.input.Mouse;

public class ExploTimer extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 10.0, 0.0, 1920.0, 1.0);
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 110.0, 0.0, 1080.0, 1.0);
    private final BooleanSetting showLabelSetting = new BooleanSetting("Show Label", this, true);
    private final ModeSetting modeSetting = new ModeSetting("Suffix Mode", this, "#", Arrays.asList("#", "#.#"));
    private long timestamp = 0L;
    private int count = 0;

    @Override
    public void onDisable() {
        super.onDisable();
        this.timestamp = 0L;
        this.count = 0;
    }

    private int computeInt(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            NBTTagCompound nbt = tagCompound.hasKey("ExtraAttributes") ? tagCompound.getCompoundTag("ExtraAttributes") : tagCompound;
            if (nbt.hasKey("CustomEnchants")) {
                NBTTagList tagList = nbt.getTagList("CustomEnchants", 10);

                for (int i = 0; i < tagList.tagCount(); i++) {
                    NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
                    if ("explosive_bow".equals(compoundTagAt.getString("Key"))) {
                        return compoundTagAt.getInteger("Level");
                    }
                }
            }

            if (!tagCompound.hasKey("display", 10)) {
                return 0;
            }
            NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
            if (!compoundTag.hasKey("Lore", 9)) {
                return 0;
            }
            NBTTagList tagList2 = compoundTag.getTagList("Lore", 8);

            for (int j = 0; j < tagList2.tagCount(); j++) {
                String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(tagList2.getStringTagAt(j));
                if (textWithoutFormattingCodes != null) {
                    String str = textWithoutFormattingCodes.toLowerCase();
                    if (str.contains("explosive iii")) {
                        return 3;
                    }

                    if (str.contains("explosive ii")) {
                        return 2;
                    }

                    if (str.contains("explosive")) {
                        return 1;
                    }
                }
            }

            return 0;
        }
        return 0;
    }

    private boolean checkState() {
        for (ItemStack stack : mc.thePlayer.inventory.mainInventory) {
            if (stack != null && stack.getItem() instanceof ItemBow && this.computeInt(stack) > 0) {
                return true;
            }
        }

        return false;
    }

    private long computeLong(int n) {
        switch (n) {
            case 2:
                return 3000L;
            default:
                return 5000L;
        }
    }

    public ExploTimer() {
        super("Explo Timer", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.xPosSetting, this.yPosSetting, this.showLabelSetting, this.modeSetting});
    }

    @Override
    public void onRender() {
        if (mc.thePlayer != null) {
            if (!this.checkState()) {
                HudDragHelper.handleObject(this);
            } else {
                float f = (float)this.xPosSetting.computeDouble();
                float f2 = (float)this.yPosSetting.computeDouble();
                long l = this.timestamp - System.currentTimeMillis();
                String str;
                if (l <= 0L) {
                    str = (this.showLabelSetting.isEnabled() ? "§fExplo: " : "") + "§5Ready";
                } else {
                    double d = l / 1000.0;
                    String str2 = this.modeSetting.matchesString2("#") ? String.format(Locale.ROOT, "%.0f", d) : String.format(Locale.ROOT, "%.1f", d);
                    str = (this.showLabelSetting.isEnabled() ? "§fExplo: " : "") + "§5" + str2;
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
                            computeFontRenderer(ExploTimer.class).getStringWidth(str),
                            computeFontRenderer(ExploTimer.class).FONT_HEIGHT
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

                computeFontRenderer(ExploTimer.class).drawStringWithShadow(str, f, f2, -1);
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventTick && ((EventTick)event).getState() == EventState.PRE) {
                if (GetMysticEnchantLoreHelper.matchesEntityPlayer3(mc.thePlayer)) {
                    this.timestamp = 0L;
                    this.count = 0;
                    return;
                }

                if (mc.thePlayer.isPotionActive(Potion.poison)) {
                    this.timestamp = 0L;
                    this.count = 0;
                    return;
                }
            } else if (event instanceof GetPlayerEvent) {
                if (GetMysticEnchantLoreHelper.matchesEntityPlayer3(mc.thePlayer) || mc.thePlayer.isPotionActive(Potion.poison)) {
                    return;
                }

                ItemStack heldItem = mc.thePlayer.getHeldItem();
                if (heldItem == null || !(heldItem.getItem() instanceof ItemBow)) {
                    return;
                }

                int n = this.computeInt(heldItem);
                if (n > 0 && System.currentTimeMillis() >= this.timestamp) {
                    this.timestamp = System.currentTimeMillis() + this.computeLong(n);
                    this.count = n;
                }
            }
        }
    }
}
