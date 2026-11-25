package org.brutality.module.impl.pit.hypixel.swap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.player.PlayerHelper12;

public class GambleSwapper extends Module {
    private final PitCategory pitCategory = PitCategory.SWAP;
    private final NumberSetting healthToleranceSetting;
    private final BooleanSetting ignoreMirrorSetting;
    private boolean flag = false;
    private static final long timestamp = 1500L;

    @Override
    public void onDisable() {
        this.flag = false;
        PlayerHelper12.handleObject(this);
        super.onDisable();
    }

    private int computeInt() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) {
            return -1;
        }
        int n = -1;
        int n2 = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            int n3 = this.computeInt(stack);
            if (n3 > n2) {
                n2 = n3;
                n = i;
            }
        }

        return n;
    }

    public GambleSwapper() {
        super("Gamble Swapper", 0, Category.PIT);
        this.healthToleranceSetting = new NumberSetting("Health Tolerance", this, 10.0, 1.0, 20.0, 1.0);
        this.ignoreMirrorSetting = new BooleanSetting("Ignore Mirror", this, false);
        this.handleSettingArray(new Setting[]{this.healthToleranceSetting, this.ignoreMirrorSetting});
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate event2) {
                if (event2.getState() != EventState.PRE) {
                    return;
                }

                Minecraft mc = Minecraft.getMinecraft();
                EntityPlayerSP player = mc.thePlayer;
                if (player == null || mc.theWorld == null) {
                    return;
                }

                int n = this.computeInt();
                if (n == -1) {
                    return;
                }

                int n2 = this.computeInt(n);
                if (n2 == -1) {
                    return;
                }

                boolean flag = this.ignoreMirrorSetting.isEnabled() && this.checkState();
                boolean flag2 = flag
                    || player.getHealth() <= this.healthToleranceSetting.computeDouble()
                    || this.flag && player.getHealth() < this.healthToleranceSetting.computeDouble() + 2.0;
                this.flag = flag2;
                this.handleEntityPlayerSP(player, flag2 ? n2 : n);
            }
        }
    }

    private int computeInt(int n) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) {
            return -1;
        }
        for (int i = 0; i < 9; i++) {
            if (i != n) {
                ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
                if (this.matchesItemStack(stack) && this.matchesItemStack2(stack)) {
                    return i;
                }
            }
        }

        for (int j = 0; j < 9; j++) {
            if (j != n) {
                ItemStack stack2 = mc.thePlayer.inventory.mainInventory[j];
                if (this.matchesItemStack(stack2) && this.computeInt(stack2) == -1) {
                    return j;
                }
            }
        }

        return -1;
    }

    private boolean matchesItemStack2(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound.hasKey("pit-mystic-enchants", 10)) {
                NBTTagCompound compoundTag = tagCompound.getCompoundTag("pit-mystic-enchants");
                if (compoundTag.hasKey("melee_heal_on_hit")) {
                    return true;
                }
            }

            String displayName = stack.getDisplayName();
            return displayName != null && displayName.toLowerCase().contains("lifesteal");
        }
        return false;
    }

    private void handleEntityPlayerSP(EntityPlayerSP player, int n) {
        PlayerHelper12.matchesObject(this, n, timestamp);
    }

    private int computeInt(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (!tagCompound.hasKey("pit-mystic-enchants", 10)) {
                return -1;
            }
            NBTTagCompound compoundTag = tagCompound.getCompoundTag("pit-mystic-enchants");
            return !compoundTag.hasKey("melee_gamble") ? -1 : compoundTag.getInteger("melee_gamble");
        }
        return -1;
    }

    private boolean checkState() {
        if (!(KillAura.entityLivingBase2 instanceof EntityPlayer player)) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            ItemStack stack = player.inventory.armorInventory[i];
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                NBTTagCompound tagCompound = stack.getTagCompound();
                if (tagCompound != null && tagCompound.hasKey("display", 10)) {
                    NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
                    if (compoundTag.hasKey("Lore", 9)) {
                        NBTTagList tagList = compoundTag.getTagList("Lore", 8);

                        for (int j = 0; j < tagList.tagCount(); j++) {
                            if (tagList.getStringTagAt(j).contains("Mirror")) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean matchesItemStack(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemSword;
    }
}
