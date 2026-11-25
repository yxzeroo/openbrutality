package org.brutality.module.impl.pit.hypixel.hud;

import java.util.Locale;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;
import org.brutality.utils.combat.CombatHelper;
import org.brutality.utils.player.FindIdentityHelper;

public final class VenomIndicator extends Module {
    private final BooleanSetting autoSwapSetting = new BooleanSetting("Auto Swap", this, false);
    private String status;

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender2D event2) {
            if (this.status != null && mc.thePlayer != null && mc.currentScreen == null) {
                FontRenderer fontRenderer = computeFontRenderer(VenomIndicator.class);
                fontRenderer.drawStringWithShadow(this.status, (event2.computeFloat() - fontRenderer.getStringWidth(this.status)) / 2.0F, event2.computeFloat2() / 2.0F + 34.0F, -1);
            }
        } else if (event instanceof EventUpdate event3 && event3.getState() == EventState.PRE && mc.thePlayer != null && mc.thePlayer.ticksExisted % 5 == 0) {
            this.status = null;
            if (mc.thePlayer.isEntityAlive()) {
                ItemStack currentArmor = mc.thePlayer.getCurrentArmor(1);
                boolean flag = matchesItemStack(currentArmor);
                if (GetMysticEnchantLoreHelper.matchesItemStack3(currentArmor)) {
                    this.status = flag ? "§5Venoms equipped" : "§5Darks equipped";
                }

                int n = FindIdentityHelper.computeInt(GetMysticEnchantLoreHelper::matchesItemStack3, true);
                if (this.status == null && n >= 36) {
                    this.status = "§5Darks in hotbar";
                }

                EntityPlayer player = CombatHelper.computeEntityPlayer(1500L);
                if (player != null && player.isEntityAlive() && matchesEntityPlayer(player)) {
                    int n2 = FindIdentityHelper.computeInt(VenomIndicator::matchesItemStack, true);
                    if (!flag && n2 >= 0) {
                        this.status = "§cSet detected. Equip venoms.";
                        if (this.autoSwapSetting.isEnabled() && FindIdentityHelper.checkState()) {
                            FindIdentityHelper.handleInt(2, n2, false, true, 100L, null);
                        }
                    } else if (!GetMysticEnchantLoreHelper.matchesItemStack3(currentArmor) && n >= 0) {
                        this.status = "§eSet detected. " + (n >= 36 ? "Darks in hotbar." : "Darks in inventory.");
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        this.status = null;
        super.onDisable();
    }

    private static boolean matchesItemStack(ItemStack stack) {
        return FindIdentityHelper.matchesItemStack(stack) && FindIdentityHelper.matchesItemStack(stack, "venom");
    }

    private static boolean matchesEntityPlayer(EntityPlayer player) {
        ItemStack currentArmor = player.getCurrentArmor(0);
        if (currentArmor != null && currentArmor.getItem() == Items.leather_boots) {
            return true;
        }
        ItemStack currentArmor2 = player.getCurrentArmor(1);
        if (currentArmor2 != null) {
            String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(currentArmor2.getDisplayName());
            if (textWithoutFormattingCodes != null && textWithoutFormattingCodes.toLowerCase(Locale.ROOT).contains("rage pants")) {
                return true;
            }
        }

        ItemStack heldItem = player.getHeldItem();
        return heldItem != null
            && heldItem.getItem() instanceof ItemSword
            && heldItem.hasTagCompound()
            && heldItem.getTagCompound().getCompoundTag("ExtraAttributes").getInteger("UpgradeTier") == 3;
    }

    public VenomIndicator() {
        super("Venom Indicator", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.autoSwapSetting});
        this.process18();
    }
}
