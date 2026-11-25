package org.brutality.utils.player;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.brutality.module.impl.pit.hypixel.hud.TelebowTimer;

public final class TickHelper {
    public static final long timestamp = 3000L;
    private static final Deque<TickHelper.Entry2> deque = new ArrayDeque<>();
    private static final Map<UUID, TickHelper.Entry3> lookupMap = new HashMap<>();
    private static final Map<Integer, TickHelper.Entry> lookupMap2 = new LinkedHashMap<>();
    private static final PollHelper pollHelper = new PollHelper();
    private static Object object;
    private static Object object2;

    public static void confirmedSelfHit() {
        if (active()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (SomberHelper.matchesEntityPlayer(mc.thePlayer)) {
                long now = System.currentTimeMillis();
                pruneReleases(now);
                if (!deque.isEmpty() && !deque.stream().anyMatch(entry2 -> !entry2.flag)) {
                    if (ObserveAttemptHelper.instance.matchesObject(mc.theWorld, mc.thePlayer.getUniqueID(), now, 3000L)) {
                        TelebowTimer.getInstance().setTimestamp(ObserveAttemptHelper.instance.computeLong(mc.theWorld, mc.thePlayer.getUniqueID(), now));
                    }
                }
            }
        }
    }

    public static void handleEntityPlayer(EntityPlayer player, ItemStack stack, int n) {
        if (active() && player == Minecraft.getMinecraft().thePlayer && n >= 3 && stack != null && stack.getItem() instanceof ItemBow) {
            long now = System.currentTimeMillis();
            pruneReleases(now);
            if (deque.size() >= 64) {
                deque.removeFirst();
            }

            deque.addLast(new TickHelper.Entry2(now, TelebowTimer.computeInt2(stack) > 0 && SomberHelper.matchesEntityPlayer(player)));
        }
    }

    public static void handleInt(int n) {
        if (active() && TelebowTimer.getInstance().checkState()) {
            TickHelper.Entry entry = lookupMap2.get(n);
            if (entry != null && entry.timestamp2 < 0L) {
                long now = System.currentTimeMillis();
                if (!entry.entityArrow.inGround) {
                    handleEntry(entry, now);
                } else {
                    pollHelper.handleInt(n, null, now);
                }

                entry.timestamp2 = now;
                pollHelper.handleInt(n, now);
                handleLong(now);
            }
        }
    }

    public static void tick() {
        if (!active()) {
            clear();
        } else {
            Minecraft mc = Minecraft.getMinecraft();
            long now = System.currentTimeMillis();
            pruneReleases(now);
            if (!SomberHelper.matchesEntityPlayer(mc.thePlayer)) {
                deque.clear();
            }

            if (!TelebowTimer.getInstance().checkState()) {
                lookupMap.clear();
                lookupMap2.clear();
                pollHelper.clear();
            } else {
                for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                    ItemStack heldItem = playerEntity.getHeldItem();
                    if (heldItem != null && heldItem.getItem() instanceof ItemBow) {
                        lookupMap.put(playerEntity.getUniqueID(), new TickHelper.Entry3(now, TelebowTimer.computeInt2(heldItem) > 0));
                    }
                }

                lookupMap.entrySet().removeIf(entry -> now - entry.getValue().timestamp > 2000L);

                for (Entity entity : mc.theWorld.loadedEntityList) {
                    if (entity instanceof EntityArrow && entity.ticksExisted <= 3) {
                        handleEntityArrow((EntityArrow)entity);
                    }
                }

                Iterator it = lookupMap2.values().iterator();

                while (it.hasNext()) {
                    TickHelper.Entry entry2 = (TickHelper.Entry)it.next();
                    if (now - entry2.timestamp <= 30000L && (entry2.timestamp2 < 0L || now - entry2.timestamp2 <= 700L)) {
                        if (entry2.timestamp2 < 0L && entry2.entityArrow.inGround) {
                            pollHelper.handleInt(entry2.entityArrow.getEntityId(), null, now);
                        } else if (entry2.timestamp2 < 0L && !entry2.entityArrow.isDead) {
                            handleEntry(entry2, now);
                        }
                    } else {
                        it.remove();
                    }
                }

                handleLong(now);
            }
        }
    }

    private static void pruneReleases(long l) {
        deque.removeIf(entry2 -> l - entry2.timestamp > 30000L);
    }

    public static void clear() {
        deque.clear();
        lookupMap.clear();
        lookupMap2.clear();
        pollHelper.clear();
        object2 = null;
        object = null;
    }

    private TickHelper() {
    }

    private static boolean active() {
        Minecraft mc = Minecraft.getMinecraft();
        TelebowTimer instance = TelebowTimer.getInstance();
        if (instance != null
            && instance.checkState44()
            && mc != null
            && mc.thePlayer != null
            && mc.theWorld != null
            && !mc.thePlayer.isDead
            && !(mc.thePlayer.getHealth() <= 0.0F)
            && SomberHelper.checkState()) {
            if (object != mc.theWorld || object2 != mc.thePlayer) {
                clear();
                object = mc.theWorld;
                object2 = mc.thePlayer;
            }

            return true;
        }
        return false;
    }

    private static void handleEntry(TickHelper.Entry entry, long l) {
        EntityArrow entityArrow = entry.entityArrow;
        Vec3 vec = new Vec3(entry.x, entry.y, entry.z);
        Vec3 vec2 = new Vec3(entityArrow.posX + entityArrow.motionX, entityArrow.posY + entityArrow.motionY, entityArrow.posZ + entityArrow.motionZ);
        entry.x = entityArrow.posX;
        entry.y = entityArrow.posY;
        entry.z = entityArrow.posZ;
        if (!(vec.squareDistanceTo(vec2) > 144.0) && !(vec.squareDistanceTo(vec2) < 1.0E-4)) {
            MovingObjectPosition hit = entityArrow.worldObj.rayTraceBlocks(vec, vec2, false, true, false);
            if (hit != null) {
                vec2 = hit.hitVec;
            }

            UUID uuid = null;

            for (Entity entity : entityArrow.worldObj
                .getEntitiesWithinAABBExcludingEntity(
                    entityArrow,
                    entityArrow.getEntityBoundingBox()
                        .offset(vec.xCoord - entityArrow.posX, vec.yCoord - entityArrow.posY, vec.zCoord - entityArrow.posZ)
                        .addCoord(vec2.xCoord - vec.xCoord, vec2.yCoord - vec.yCoord, vec2.zCoord - vec.zCoord)
                        .expand(1.0, 1.0, 1.0)
                )) {
                if (entity instanceof EntityPlayer && entity != entityArrow.shootingEntity && !entity.isDead) {
                    MovingObjectPosition hit2 = entity.getEntityBoundingBox().expand(0.3, 0.3, 0.3).calculateIntercept(vec, vec2);
                    if (hit2 != null || entity.getEntityBoundingBox().isVecInside(vec)) {
                        if (uuid != null && !uuid.equals(entity.getUniqueID())) {
                            pollHelper.handleInt(entityArrow.getEntityId(), null, l);
                            return;
                        }

                        uuid = entity.getUniqueID();
                    }
                }
            }

            if (uuid != null) {
                pollHelper.handleInt(entityArrow.getEntityId(), uuid, l);
            }
        }
    }

    public static void handleEntityArrow(EntityArrow entity) {
        if (active() && TelebowTimer.getInstance().checkState() && !lookupMap2.containsKey(entity.getEntityId()) && entity.shootingEntity instanceof EntityPlayer) {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer shootingEntity = (EntityPlayer)entity.shootingEntity;
            long now = System.currentTimeMillis();
            TickHelper.Entry3 entry3 = lookupMap.get(shootingEntity.getUniqueID());
            ItemStack heldItem = shootingEntity.getHeldItem();
            if (entry3 == null || now - entry3.timestamp > 2000L) {
                entry3 = heldItem != null && heldItem.getItem() instanceof ItemBow ? new TickHelper.Entry3(now, TelebowTimer.computeInt2(heldItem) > 0) : null;
            }

            boolean flag = entry3 != null && entry3.flag && SomberHelper.matchesEntityPlayer(shootingEntity);
            if (lookupMap2.size() >= 256) {
                lookupMap2.remove(lookupMap2.keySet().iterator().next());
            }

            TickHelper.Entry entry = new TickHelper.Entry(entity, shootingEntity.getUniqueID(), now);
            lookupMap2.put(entity.getEntityId(), entry);
            pollHelper.handleInt(entity.getEntityId(), shootingEntity.getUniqueID(), flag, now);
            handleEntry(entry, now);
        }
    }

    private static void handleLong(long l) {
        Minecraft mc = Minecraft.getMinecraft();

        for (PollHelper.Entry2 entry2 : pollHelper.poll(l)) {
            if (!entry2.uuid.equals(mc.thePlayer.getUniqueID())) {
                EntityPlayer playerEntityByUUID = mc.theWorld.getPlayerEntityByUUID(entry2.uuid);
                if (playerEntityByUUID != null && SomberHelper.matchesEntityPlayer(playerEntityByUUID)) {
                    ObserveAttemptHelper.instance.matchesObject2(mc.theWorld, entry2.uuid, l, 3000L);
                }
            }
        }
    }

    public static void handleEntityPlayer(EntityPlayer player) {
        if (active() && TelebowTimer.getInstance().checkState()) {
            long now = System.currentTimeMillis();
            pollHelper.handleUUID(player.getUniqueID(), now);
            handleLong(now);
        }
    }

    public static final class Entry {
        public final EntityArrow entityArrow;
        public final UUID uuid;
        public final long timestamp;
        public double x;
        public double y;
        public double z;
        public long timestamp2;
        private static final long timestamp3 = -1L;

        public Entry(EntityArrow entityArrow, UUID uuid, long timestamp) {
            this.timestamp2 = timestamp3;
            this.entityArrow = entityArrow;
            this.uuid = uuid;
            this.timestamp = timestamp;
            this.x = entityArrow.posX;
            this.y = entityArrow.posY;
            this.z = entityArrow.posZ;
        }
    }

    public static final class Entry2 {
        public final long timestamp;
        public final boolean flag;

        public Entry2(long timestamp, boolean flag) {
            this.timestamp = timestamp;
            this.flag = flag;
        }
    }

    public static final class Entry3 {
        public final long timestamp;
        public final boolean flag;

        public Entry3(long timestamp, boolean flag) {
            this.timestamp = timestamp;
            this.flag = flag;
        }
    }
}
