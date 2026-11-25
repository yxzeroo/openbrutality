package org.brutality.utils.combat;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.brutality.event.impl.attack.AttackEvent;
import org.brutality.event.impl.attack.GetDamageEvent;
import org.brutality.module.impl.pit.hypixel.hud.VenomedTimer;
import org.brutality.utils.hypixel.pit.AttackHelper2;

public final class CombatHelper {
    private static final AttackHelper2 attackHelper2 = new AttackHelper2();
    private static final Map<String, ItemStack> lookupMap = new HashMap<>();
    private static Object object;
    private static Object object2;
    private static EntityPlayer entityPlayer;
    private static long timestamp;
    private static Entity entity;
    private static long timestamp2;
    private static Entity entity2;
    private static long timestamp3;

    public static void onGetDamageEvent(GetDamageEvent getDamageEvent) {
        if (getDamageEvent != null && getDamageEvent.getEntity() == Minecraft.getMinecraft().thePlayer) {
            process();
            entity = getDamageEvent.getEntity2();
            if (getDamageEvent.getEntity2() instanceof EntityPlayer) {
                String str = getDamageEvent.getEntity2().getName().toLowerCase(Locale.ROOT);
                attackHelper2.attack(str, System.currentTimeMillis());
                ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();
                lookupMap.put(str, heldItem == null ? null : heldItem.copy());
            }

            timestamp2 = System.currentTimeMillis();
            if (getDamageEvent.getEntity2() == Minecraft.getMinecraft().thePlayer) {
                entity2 = getDamageEvent.getEntity();
                timestamp3 = timestamp2;
            }
        }
    }

    public static void handleInt(byte b, String str) {
        process();
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null && mc.thePlayer != null) {
            if (!mc.thePlayer.isDead && !(mc.thePlayer.getHealth() <= 0.0F)) {
                if (b == 2) {
                    VenomedTimer.handleString4(AttackHelper2.computeString(str));
                }

                String str2 = attackHelper2.computeString(b, str, System.currentTimeMillis());
                if (str2 != null) {
                    for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                        if (playerEntity != mc.thePlayer && playerEntity.getName().equalsIgnoreCase(str2)) {
                            entityPlayer = playerEntity;
                            timestamp = System.currentTimeMillis();
                            ItemStack stack = lookupMap.remove(str2.toLowerCase(Locale.ROOT));
                            VenomedTimer.handleEntityPlayer(playerEntity);
                            new AttackEvent(mc.thePlayer, playerEntity, stack).process();
                            return;
                        }
                    }
                }
            } else {
                reset();
            }
        }
    }

    public static void process2() {
        process();
        attackHelper2.setTimestamp2(System.currentTimeMillis());
    }

    public static Entity getEntity2() {
        return entity2;
    }

    private CombatHelper() {
    }

    public static boolean matchesLong2(long l) {
        return entity2 != null && System.currentTimeMillis() - timestamp3 <= l;
    }

    public static void reset() {
        attackHelper2.clear();
        lookupMap.clear();
        entityPlayer = null;
        timestamp = 0L;
        object2 = null;
        object = null;
        entity = null;
        timestamp2 = 0L;
        entity2 = null;
        timestamp3 = 0L;
    }

    public static boolean matchesLong(long l) {
        return entity != null && System.currentTimeMillis() - timestamp2 <= l;
    }

    private static void process() {
        Minecraft mc = Minecraft.getMinecraft();
        if (object != mc.theWorld || object2 != mc.thePlayer) {
            reset();
            object = mc.theWorld;
            object2 = mc.thePlayer;
        }
    }

    public static Entity getEntity() {
        return entity;
    }

    public static long getTimestamp2() {
        return timestamp2;
    }

    public static EntityPlayer computeEntityPlayer(long l) {
        process();
        Minecraft mc = Minecraft.getMinecraft();
        return entityPlayer != null
                && System.currentTimeMillis() - timestamp <= l
                && mc.theWorld != null
                && mc.theWorld.playerEntities.contains(entityPlayer)
            ? entityPlayer
            : null;
    }
}
