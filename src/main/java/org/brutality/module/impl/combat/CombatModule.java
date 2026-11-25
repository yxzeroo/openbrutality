package org.brutality.module.impl.combat;

import java.util.Collections;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.attack.GetDamageEvent;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.player.StartHotbarMovesHelper;
import org.brutality.utils.player.StartHotbarMovesHelper.Entry;

public class CombatModule extends Module {
    public static CombatModule instance;
    private int count = -1;
    private long timestamp;
    private static final String text = "Boost";
    private static final long timestamp2 = 250L;

    public CombatModule() {
        super(text, 0, Category.COMBAT);
        instance = this;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() && mc.thePlayer != null && mc.theWorld != null) {
            if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                if (FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer) || StartHotbarMovesHelper.flag) {
                    return;
                }

                EntityPlayer player = KillAura.entityLivingBase2 instanceof EntityPlayer ? (EntityPlayer)KillAura.entityLivingBase2 : null;
                if (player != null && DefaultHelper.matchesString(player.getName()) && mc.thePlayer.ticksExisted != this.count) {
                    int n = this.computeInt();
                    if (n >= 0 && n != mc.thePlayer.inventory.currentItem) {
                        this.count = mc.thePlayer.ticksExisted;
                        StartHotbarMovesHelper.startHotbarMoves(
                            Collections.singletonList(new Entry(36 + n, mc.thePlayer.inventory.currentItem)), true, 0L, null
                        );
                    }
                }
            } else if (event instanceof GetDamageEvent getDamageEvent
                && getDamageEvent.entity == mc.thePlayer
                && getDamageEvent.entity2 instanceof EntityPlayer
                && DefaultHelper.matchesString(getDamageEvent.entity2.getName())
                && !FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                this.timestamp = System.currentTimeMillis() + timestamp2;
                mc.thePlayer.setSprinting(false);
                mc.thePlayer.setSprinting(true);
            }
        }
    }

    private int computeInt() {
        int n = -1;
        float MAX_VALUE = Float.MAX_VALUE;

        for (int i = 0; i < 9; i++) {
            if (i != mc.thePlayer.inventory.currentItem) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack != null) {
                    Item item = stack.getItem();
                    float f = 0.0F;

                    for (Object obj : stack.getAttributeModifiers().get(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName())) {
                        if (obj instanceof AttributeModifier) {
                            f += (float)((AttributeModifier)obj).getAmount();
                        }
                    }

                    boolean flag = !(item instanceof ItemSword) && !(item instanceof ItemPickaxe) && !(item instanceof ItemAxe);
                    if (flag) {
                        return i;
                    }

                    if (f < MAX_VALUE) {
                        MAX_VALUE = f;
                        n = i;
                    }
                }
            }
        }

        return n;
    }

    public static boolean checkState() {
        return instance != null && instance.checkState44() && System.currentTimeMillis() < instance.timestamp;
    }

    public static boolean checkState2() {
        return instance != null
            && instance.checkState44()
            && mc.thePlayer != null
            && mc.theWorld != null
            && !FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer);
    }
}
