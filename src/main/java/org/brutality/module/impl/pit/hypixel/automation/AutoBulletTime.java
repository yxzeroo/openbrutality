package org.brutality.module.impl.pit.hypixel.automation;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;

public class AutoBulletTime extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private final List<Block> entries = Arrays.asList(Blocks.enchanting_table, Blocks.ender_chest, Blocks.chest);
    private final BooleanSetting swapBackSetting = new BooleanSetting("Swap Back", this, true);
    private final BooleanSetting skipWhileKillAuraBlockingSetting = new BooleanSetting("Skip While KillAura Blocking", this, true);
    private int count = -1;
    private boolean flag;
    private boolean flag2;

    public AutoBulletTime() {
        super("Auto Bullet Time", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.swapBackSetting, this.skipWhileKillAuraBlockingSetting});
    }

    @Override
    public void onDisable() {
        this.count = -1;
        this.flag = false;
    }

    private int computeInt() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null) {
                for (String str : stack.getTooltip(mc.thePlayer, false)) {
                    if (str.replaceAll("§[0-9a-fk-or]", "").contains("Bullet Time")) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    @Override
    public void onEnable() {
        this.count = -1;
        this.flag = false;
    }

    private boolean checkState2() {
        return KillAura.instance != null && KillAura.entityLivingBase2 != null && KillAura.instance.manualBlockSetting.isEnabled();
    }

    private boolean checkState() {
        if (mc.objectMouseOver == null) {
            return false;
        }
        if (mc.objectMouseOver.entityHit instanceof EntityVillager) {
            return true;
        }
        if (mc.objectMouseOver.typeOfHit != MovingObjectType.BLOCK) {
            return false;
        }
        return mc.objectMouseOver.getBlockPos() == null
            ? false
            : this.entries.contains(mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock());
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (event instanceof EventTick) {
                    ItemStack heldItem = mc.thePlayer.getHeldItem();
                    if (heldItem == null) {
                        this.flag2 = false;
                        return;
                    }

                    if (this.checkState()) {
                        this.flag2 = false;
                        return;
                    }

                    if (this.skipWhileKillAuraBlockingSetting.isEnabled() && this.checkState2()) {
                        if (this.flag && this.count != -1) {
                            mc.thePlayer.inventory.currentItem = this.count;
                            this.count = -1;
                            this.flag = false;
                        }

                        this.flag2 = false;
                        return;
                    }

                    boolean flag = heldItem.getItem() instanceof ItemSword || heldItem.getItem() == Items.diamond_shovel;
                    boolean keyDown = mc.gameSettings.keyBindUseItem.isKeyDown();
                    boolean flag2 = flag && keyDown;
                    if (!flag2 && this.flag2 && this.flag && this.swapBackSetting.isEnabled() && this.count != -1) {
                        mc.thePlayer.inventory.currentItem = this.count;
                        this.count = -1;
                        this.flag = false;
                    }

                    if (flag2 && !this.flag) {
                        int n = this.computeInt();
                        if (n != -1 && n != mc.thePlayer.inventory.currentItem) {
                            this.count = mc.thePlayer.inventory.currentItem;
                            mc.thePlayer.inventory.currentItem = n;
                            this.flag = true;
                        }
                    }

                    this.flag2 = flag2;
                }
            }
        }
    }
}
