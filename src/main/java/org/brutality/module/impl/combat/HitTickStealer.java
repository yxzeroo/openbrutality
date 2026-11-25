package org.brutality.module.impl.combat;

import java.util.ArrayList;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.player.StartHotbarMovesHelper;

public class HitTickStealer extends Module {
    public static HitTickStealer instance;
    private int count = -1;

    public static boolean checkState() {
        return instance != null
            && instance.checkState44()
            && mc.theWorld != null
            && mc.thePlayer != null
            && !FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()
            && event instanceof EventUpdate
            && ((EventUpdate)event).getState() == EventState.PRE
            && mc.thePlayer != null
            && !StartHotbarMovesHelper.flag
            && mc.thePlayer.ticksExisted != this.count) {
            int currentItem = mc.thePlayer.inventory.currentItem;
            int n = this.computeInt(currentItem);
            if (n != -1) {
                this.count = mc.thePlayer.ticksExisted;
                ArrayList list = new ArrayList();
                list.add(new StartHotbarMovesHelper.Entry(36 + n, currentItem));
                StartHotbarMovesHelper.startHotbarMoves(list, true, 0L, null);
            }
        }
    }

    private float computeFloat(ItemStack stack) {
        double d = 0.0;

        for (AttributeModifier attributeModifier : stack.getAttributeModifiers().get(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName())) {
            d += attributeModifier.getAmount();
        }

        return (float)d;
    }

    public HitTickStealer() {
        super("Hit Tick Stealer", 0, Category.COMBAT);
        instance = this;
    }

    private int computeInt(int n) {
        int n2 = -1;
        float MAX_VALUE = Float.MAX_VALUE;

        for (int i = 0; i < 9; i++) {
            if (i != n) {
                ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
                if (stack != null) {
                    float f = this.computeFloat(stack);
                    if (f < MAX_VALUE) {
                        MAX_VALUE = f;
                        n2 = i;
                    }

                    Item item = stack.getItem();
                    if (!(item instanceof ItemSword) && !(item instanceof ItemPickaxe) && !(item instanceof ItemAxe)) {
                        return i;
                    }
                }
            }
        }

        return n2;
    }
}
