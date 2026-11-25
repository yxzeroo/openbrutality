package org.brutality.module.impl.pit.hypixel.swap;

import java.util.ArrayList;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.util.KeybindStateUtil;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;
import org.brutality.utils.player.StartHotbarMovesHelper;

public class PantsSwapper extends Module {
    private final PitCategory pitCategory = PitCategory.SWAP;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 150.0, 1.0, 500.0, 50.0);
    private final BooleanSetting includeBootsSetting = new BooleanSetting("Include Boots", this, false);
    private final BooleanSetting safeModeSetting = new BooleanSetting("Safe Mode", this, true);
    private static PantsSwapper instance;
    private static final long timestamp = 40L;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            StartHotbarMovesHelper.onEvent3(event);
        }
    }

    private int computeInt(Predicate<ItemStack> predicate) {
        if (mc.thePlayer == null) {
            return -1;
        }
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (this.matchesItemStack2(stack) && predicate.test(stack)) {
                return 36 + i;
            }
        }

        for (int j = 9; j < 36; j++) {
            ItemStack stack2 = mc.thePlayer.inventory.mainInventory[j];
            if (this.matchesItemStack2(stack2) && predicate.test(stack2)) {
                return j;
            }
        }

        return -1;
    }

    private boolean matchesItemStack2(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemArmor && ((ItemArmor)stack.getItem()).armorType == 2;
    }

    private int computeInt() {
        int n = this.computeInt(GetMysticEnchantLoreHelper::matchesItemStack3);
        if (n != -1) {
            return n;
        }
        int n2 = this.computeInt(stack -> !this.matchesItemStack(stack));
        return n2 != -1 ? n2 : StartHotbarMovesHelper.computeInt2(2);
    }

    public PantsSwapper() {
        super("PantsSwapper", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.delaySetting, this.includeBootsSetting, this.safeModeSetting});
        instance = this;
    }

    private boolean matchesItemStack(ItemStack stack) {
        if (!this.matchesItemStack2(stack)) {
            return false;
        }
        return stack.getDisplayName() != null && stack.getDisplayName().toLowerCase().contains("escape")
            ? true
            : stack.hasTagCompound() && stack.getTagCompound().toString().toLowerCase().contains("escape");
    }

    @Generated
    public static PantsSwapper getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.thePlayer == null || mc.theWorld == null) {
            this.process28();
        } else if (StartHotbarMovesHelper.flag) {
            this.process28();
        } else {
            ArrayList list = new ArrayList();
            int n = this.computeInt();
            if (n == -1) {
                mc.thePlayer.playSound("mob.villager.no", 1.0F, 0.7F);
                this.process28();
            } else {
                list.add(new StartHotbarMovesHelper.Entry3(2, n));
                if (this.includeBootsSetting.isEnabled()) {
                    boolean flag = mc.thePlayer.inventory.armorInventory[0] != null;
                    if (flag) {
                        int n2 = StartHotbarMovesHelper.computeInt2(3);
                        if (n2 != -1) {
                            list.add(new StartHotbarMovesHelper.Entry3(3, n2));
                        }
                    }
                }

                if (this.safeModeSetting.isEnabled()) {
                    KeybindStateUtil.process();
                }

                StartHotbarMovesHelper.handleList(
                    list, this.safeModeSetting.isEnabled(), Math.max(timestamp, (long)this.delaySetting.computeDouble()), true, true, () -> {
                        KeybindStateUtil.process2();
                        if (this.checkState44()) {
                            this.process28();
                        }
                    }
                );
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
