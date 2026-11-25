package org.brutality.anticheat.data;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.anticheat.data.DataHandler;

public class CollectSampleHandler {
    private static final Minecraft mc;
    private static final Map<UUID, Deque<DataHandler>> lookupMap;
    private static final Map<UUID, List<DataHandler>> lookupMap2;
    private static final int count;
    private static final int count2;

    private static DataHandler getOrCreateSample(UUID uUID2) {
        List list = lookupMap2.computeIfAbsent(uUID2, uUID -> new ArrayList(10));
        if (!list.isEmpty()) {
            return (DataHandler)list.remove(list.size() - 1);
        }
        return new DataHandler();
    }

    private static void recycleSample(UUID uUID2, DataHandler dataHandler) {
        List list = lookupMap2.computeIfAbsent(uUID2, uUID -> new ArrayList(10));
        if (list.size() < 10) {
            list.add(dataHandler);
        }
    }

    public static DataHandler computeDataHandler(EntityPlayer entityPlayer, int n) {
        long l = CollectSampleHandler.mc.theWorld != null ? CollectSampleHandler.mc.theWorld.getTotalWorldTime() : 0L;
        long l2 = l - (long)n;
        Deque<DataHandler> deque = lookupMap.get(entityPlayer.getUniqueID());
        if (deque != null) {
            for (DataHandler dataHandler : deque) {
                if (dataHandler.tick != l2) continue;
                return dataHandler;
            }
        }
        return null;
    }

    public static List<DataHandler> computeList(EntityPlayer entityPlayer) {
        Deque<DataHandler> deque = lookupMap.get(entityPlayer.getUniqueID());
        return deque != null ? new ArrayList<DataHandler>(deque) : Collections.emptyList();
    }

    public static DataHandler computeDataHandler(EntityPlayer entityPlayer) {
        Deque<DataHandler> deque = lookupMap.get(entityPlayer.getUniqueID());
        return deque != null && !deque.isEmpty() ? deque.getLast() : null;
    }

    public static void collectSample(EntityPlayer entityPlayer) {
        UUID uUID2 = entityPlayer.getUniqueID();
        DataHandler dataHandler = CollectSampleHandler.getOrCreateSample(uUID2);
        dataHandler.posX = entityPlayer.posX;
        dataHandler.posY = entityPlayer.posY;
        dataHandler.posZ = entityPlayer.posZ;
        dataHandler.prevPosX = entityPlayer.prevPosX;
        dataHandler.prevPosY = entityPlayer.prevPosY;
        dataHandler.prevPosZ = entityPlayer.prevPosZ;
        dataHandler.motionX = entityPlayer.motionX;
        dataHandler.motionY = entityPlayer.motionY;
        dataHandler.motionZ = entityPlayer.motionZ;
        dataHandler.yaw = entityPlayer.rotationYaw;
        dataHandler.pitch = entityPlayer.rotationPitch;
        dataHandler.factor = entityPlayer.prevRotationYaw;
        dataHandler.factor2 = entityPlayer.prevRotationPitch;
        dataHandler.onGround = entityPlayer.onGround;
        dataHandler.flag = entityPlayer.isSprinting();
        dataHandler.hurtTime = entityPlayer.hurtTime;
        dataHandler.tick = CollectSampleHandler.mc.theWorld != null ? CollectSampleHandler.mc.theWorld.getTotalWorldTime() : 0L;
        dataHandler.timeStamp = System.currentTimeMillis();
        dataHandler.reset();
        Deque deque = lookupMap.computeIfAbsent(uUID2, uUID -> new ArrayDeque(8));
        deque.addLast(dataHandler);
        if (deque.size() > 8) {
            DataHandler dataHandler2 = (DataHandler)deque.removeFirst();
            CollectSampleHandler.recycleSample(uUID2, dataHandler2);
        }
    }

    static {
        count = 8;
        count2 = 10;
        mc = Minecraft.getMinecraft();
        lookupMap = new ConcurrentHashMap<UUID, Deque<DataHandler>>();
        lookupMap2 = new ConcurrentHashMap<UUID, List<DataHandler>>();
    }

    public static boolean matchesEntityPlayer(EntityPlayer entityPlayer) {
        return lookupMap.containsKey(entityPlayer.getUniqueID());
    }

    public static DataHandler computeDataHandler2(EntityPlayer entityPlayer) {
        Deque<DataHandler> deque = lookupMap.get(entityPlayer.getUniqueID());
        if (deque != null && deque.size() >= 2) {
            Iterator<DataHandler> iterator = deque.iterator();
            DataHandler dataHandler = null;
            while (iterator.hasNext()) {
                dataHandler = iterator.next();
                if (!iterator.hasNext()) {
                    return dataHandler;
                }
                if (!iterator.hasNext()) continue;
                iterator.next();
                return dataHandler;
            }
        }
        return null;
    }

    public static void handleEntityPlayer(EntityPlayer entityPlayer) {
        if (entityPlayer != null) {
            lookupMap.remove(entityPlayer.getUniqueID());
            lookupMap2.remove(entityPlayer.getUniqueID());
        } else {
            lookupMap.clear();
            lookupMap2.clear();
        }
    }
}
