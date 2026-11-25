package org.brutality.module.impl.pit.hypixel.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.WHelper;
import org.brutality.utils.hypixel.pit.DOUBLEHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.player.FindIdentityHelper;

public final class DivineDetector extends Module {
    private String text;
    private Map<String, Integer> lookupMap = Map.of();
    private long timestamp;
    private boolean flag;
    private int count;

    private static void handleMap(Map<String, Integer> map, ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            NBTTagCompound compoundTag = stack.getTagCompound().getCompoundTag("ExtraAttributes");
            if (compoundTag.hasKey("Lives", 99) && (compoundTag.hasKey("Nonce") || compoundTag.hasKey("nonce"))) {
                map.put(FindIdentityHelper.computeString(stack), compoundTag.getInteger("Lives"));
            }
        }
    }

    public static Map<String, Integer> computeMap2(EntityPlayer player) {
        Map map = computeMap(player);

        for (ItemStack stack : player.inventory.mainInventory) {
            handleMap(map, stack);
        }

        return map;
    }

    private void reset() {
        this.text = null;
        this.lookupMap = Map.of();
        this.timestamp = 0L;
        this.count = 0;
        this.flag = false;
    }

    @Override
    public void onDisable() {
        this.reset();
        super.onDisable();
    }

    public static Map<String, Integer> computeMap(EntityPlayer player) {
        HashMap map = new HashMap();
        handleMap(map, player.getHeldItem());

        for (ItemStack stack : player.inventory.armorInventory) {
            handleMap(map, stack);
        }

        return map;
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof RenderEvent4) && mc.thePlayer != null && mc.theWorld != null) {
            if (event instanceof ChatEvent chatEvent && chatEvent.byteVal != 2 && chatEvent.message != null) {
                String text = chatEvent.message.getUnformattedText();
                String str = DOUBLEHelper.computeString2(text);
                String str2 = str == null ? DOUBLEHelper.computeString(text) : null;
                if (this.text != null
                    && System.currentTimeMillis() - this.timestamp < 5000L
                    && (this.text.equalsIgnoreCase(str) || this.text.equalsIgnoreCase(str2) && WHelper.matchesString(str2))) {
                    this.flag = true;
                    this.timestamp = System.currentTimeMillis();
                    this.count = 0;
                }
            } else if (event instanceof EventTick event2 && event2.getState() == EventState.PRE && mc.thePlayer.ticksExisted % 3 == 0) {
                long now = System.currentTimeMillis();
                if (this.flag) {
                    if (now - this.timestamp > 12000L || this.lookupMap.isEmpty()) {
                        this.reset();
                        return;
                    }

                    EntityPlayer playerEntityByName = mc.theWorld.getPlayerEntityByName(this.text);
                    if (playerEntityByName == null || !playerEntityByName.isEntityAlive() || !FreshHelper.matchesWorld3(mc.theWorld, playerEntityByName)) {
                        this.count = 0;
                        return;
                    }

                    int n = computeInt(this.lookupMap, computeMap(playerEntityByName));
                    if (n < 0 || n > 0 && ++this.count >= 4) {
                        String str3 = n < 0 ? " did not divine." : " appears to have divined.";
                        String text2 = this.text;
                        MessageUtils.x("§b§lDIVINE! §r" + text2 + str3);
                        this.reset();
                    } else if (n == 0) {
                        this.count = 0;
                    }
                } else if (KillAura.entityLivingBase2 instanceof EntityPlayer player
                    && player.isEntityAlive()
                    && !FreshHelper.matchesWorld3(mc.theWorld, player)
                    && player != mc.thePlayer
                    && player.worldObj == mc.theWorld) {
                    this.text = player.getName();
                    this.lookupMap = computeMap(player);
                    this.timestamp = now;
                }
            }
        } else {
            this.reset();
        }
    }

    public DivineDetector() {
        super("Divine Detector", 0, Category.PIT);
    }

    public static int computeInt(Map<String, Integer> map, Map<String, Integer> map2) {
        if (map.isEmpty()) {
            return 0;
        }
        boolean flag = true;

        for (Entry entry : map.entrySet()) {
            Integer integer = (Integer)map2.get(entry.getKey());
            if (integer != null && integer < (Integer)entry.getValue()) {
                return -1;
            }

            if (!((Integer)entry.getValue()).equals(integer)) {
                flag = false;
            }
        }

        return flag ? 1 : 0;
    }
}
