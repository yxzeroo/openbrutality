package org.brutality.module.impl.misc;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;

public class AntiCheat extends Module {
    private static final double amount = 4.42;
    private static final double amount2 = 5.72;
    private static final double amount3 = 1.35;
    private static final int count = 45;
    private static final double amount4 = 0.6;
    private static final int count2 = 15;
    private static final long timestamp = 1000L;
    private static final int count3 = 6;
    private static final long timestamp2 = 350L;
    private static final double amount5 = 6.0;
    private static final int count4 = 7;
    private static final long timestamp3 = 250L;
    private static final long timestamp4 = 250L;
    private static final double amount6 = 0.1;
    private final BooleanSetting speedSetting = new BooleanSetting("Speed", this, true);
    private final BooleanSetting flySetting = new BooleanSetting("Fly", this, true);
    private final BooleanSetting stepSetting = new BooleanSetting("Step", this, true);
    private final BooleanSetting highCPSSetting = new BooleanSetting("High CPS", this, true);
    private final BooleanSetting reachSetting = new BooleanSetting("Reach", this, true);
    private final BooleanSetting fastBreakSetting = new BooleanSetting("Fast break", this, true);
    private final NumberSetting maxReachSetting = new NumberSetting("Max reach", this, 4.4, 3.0, 6.0, 0.1);
    private final NumberSetting notifyCooldownSetting = new NumberSetting("Notify cooldown", this, 1.5, 0.0, 10.0, 0.1);
    private final Map<UUID, AntiCheat.Entry2> lookupMap = new HashMap<>();
    private final Map<Integer, UUID> lookupMap2 = new HashMap<>();
    private final ConcurrentLinkedQueue<AntiCheat.Entry> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
    private long timestamp5 = 0L;
    private int count5 = 0;

    private int computeInt(Deque<Long> deque, long l, long l2) {
        int n = 0;

        for (Long val : deque) {
            if (l - val <= l2) {
                n++;
            }
        }

        return n;
    }

    private void handleEntry(AntiCheat.Entry entry, long l) {
        if (mc.theWorld != null) {
            switch (entry.swingMode) {
                case SWING:
                    this.handleInt3(entry.count, l);
                    break;
                case BREAK:
                    this.handleInt2(entry.count, l);
                    break;
                case HURT:
                    this.handleInt(entry.count, l);
                    break;
                case DESTROY:
                    this.handleInt(entry.count);
            }
        }
    }

    private void onGetPacketEvent(GetPacketEvent getPacketEvent) {
        long now = System.currentTimeMillis();
        if (getPacketEvent.getPacket() instanceof S0BPacketAnimation) {
            S0BPacketAnimation packet = getPacketEvent.getPacket();
            if (packet.getAnimationType() == 0) {
                this.concurrentLinkedQueue.add(new AntiCheat.Entry(packet.getEntityID(), now, AntiCheat.SwingMode.SWING));
            }
        } else if (getPacketEvent.getPacket() instanceof S25PacketBlockBreakAnim) {
            S25PacketBlockBreakAnim packet2 = getPacketEvent.getPacket();
            this.concurrentLinkedQueue.add(new AntiCheat.Entry(packet2.getBreakerId(), now, AntiCheat.SwingMode.BREAK));
        } else if (getPacketEvent.getPacket() instanceof S19PacketEntityStatus) {
            S19PacketEntityStatus packet3 = getPacketEvent.getPacket();
            if (packet3.getOpCode() == 2) {
                Entity entity = packet3.getEntity(mc.theWorld);
                if (entity != null) {
                    this.concurrentLinkedQueue.add(new AntiCheat.Entry(entity.getEntityId(), now, AntiCheat.SwingMode.HURT));
                }
            }
        } else if (getPacketEvent.getPacket() instanceof S13PacketDestroyEntities) {
            S13PacketDestroyEntities packet4 = getPacketEvent.getPacket();

            for (int entityID : packet4.getEntityIDs()) {
                this.concurrentLinkedQueue.add(new AntiCheat.Entry(entityID, now, AntiCheat.SwingMode.DESTROY));
            }
        }
    }

    private static Vec3 getVectorForRotation(float f, float f2) {
        float cos = MathHelper.cos(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
        float sin = MathHelper.sin(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
        float f3 = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
        float sin2 = MathHelper.sin(-f * (float) (Math.PI / 180.0));
        return new Vec3(sin * f3, sin2, cos * f3);
    }

    private void handleInt(int n) {
        UUID uuid = this.lookupMap2.remove(n);
        if (uuid != null) {
            this.lookupMap.remove(uuid);
        }
    }

    private void cleanup(long l) {
        this.lookupMap.entrySet().removeIf(entry -> mc.theWorld.getPlayerEntityByUUID(entry.getKey()) == null);

        for (AntiCheat.Entry2 entry2 : this.lookupMap.values()) {
            this.handleDeque(entry2.deque, l, 2000L);
            this.handleDeque(entry2.deque2, l, 500L);
        }
    }

    private Entity computeEntity(EntityPlayer player) {
        double d = (player.capabilities.isCreativeMode ? 6.0 : this.maxReachSetting.computeDouble()) + 1.0;
        Vec3 positionEyes = player.getPositionEyes(1.0F);
        Vec3 vectorForRotation = getVectorForRotation(player.rotationPitch, player.rotationYaw);
        Vec3 vec = positionEyes.addVector(vectorForRotation.xCoord * d, vectorForRotation.yCoord * d, vectorForRotation.zCoord * d);
        Entity entity = null;
        double MAX_VALUE = Double.MAX_VALUE;

        for (Entity entity2 : mc.theWorld.getLoadedEntityList()) {
            if (entity2 != player && entity2 != mc.thePlayer && entity2 instanceof EntityLivingBase) {
                AxisAlignedBB box = entity2.getEntityBoundingBox().expand(0.1, 0.1, 0.1);
                MovingObjectPosition hit = box.calculateIntercept(positionEyes, vec);
                if (hit != null) {
                    double d2 = positionEyes.distanceTo(hit.hitVec);
                    if (d2 < MAX_VALUE) {
                        MAX_VALUE = d2;
                        entity = entity2;
                    }
                }
            }
        }

        return entity;
    }

    private void handleString(String str) {
        long now = System.currentTimeMillis();
        long l = (long)(this.notifyCooldownSetting.computeDouble() * 1000.0);
        if (now - this.timestamp5 >= l) {
            this.timestamp5 = now;
            MessageUtils.x("&cNCP: &f " + str);
        }
    }

    private void handleEntityPlayer(EntityPlayer player) {
        Entity entity = this.computeEntity(player);
        if (entity != null) {
            double dist = player.getDistanceToEntity(entity);
            double d = player.capabilities.isCreativeMode ? 6.0 : this.maxReachSetting.computeDouble();
            if (dist > d) {
                String name = player.getName();
                String str = this.computeString(dist);
                String name2 = entity.getName();
                String str2 = str;
                this.handleString(name + " swing reach: " + str2 + "m to " + name2 + " (max " + d + "m).");
            }
        }
    }

    private String computeString(double d) {
        return String.format("%.2f", d);
    }

    private void handleEntityPlayer2(EntityPlayer player, AntiCheat.Entry2 entry2, double d) {
        if (player.hurtTime > 0) {
            entry2.count2 = 0;
        } else {
            double d2 = player.isSprinting() ? 5.72 : 4.42;
            if (player.isInWater()) {
                d2 *= 1.35;
            }

            double d3 = d2 * 1.35;
            if (d > d3) {
                entry2.count2++;
                if (entry2.count2 >= 6) {
                    this.handleString(player.getName() + " speed: " + this.computeString(d) + " BPS (limit " + this.computeString(d3) + ").");
                    entry2.count2 = 0;
                }
            } else {
                entry2.count2 = Math.max(0, entry2.count2 - 1);
            }
        }
    }

    private void handleEntityPlayer(EntityPlayer player, AntiCheat.Entry2 entry2, double d) {
        if (!player.capabilities.isFlying && !player.capabilities.isCreativeMode) {
            if (entry2.count > 45) {
                this.handleString(player.getName() + " fly: airborne " + entry2.count + " ticks (limit 45).");
                entry2.count = 35;
            }

            if (!player.onGround && d > 0.6) {
                this.handleString(player.getName() + " fly: vertical +" + this.computeString(d) + " blocks/tick.");
            }
        } else {
            entry2.count = 0;
        }
    }

    public boolean checkState2() {
        return true;
    }

    @Override
    public void onDisable() {
        this.lookupMap.clear();
        this.lookupMap2.clear();
        this.concurrentLinkedQueue.clear();
    }

    private void handleDeque(Deque<Long> deque, long l, long l2) {
        while (!deque.isEmpty() && l - deque.peekFirst() > l2) {
            deque.pollFirst();
        }
    }

    private void handleInt2(int n, long l) {
        Entity entityByID = mc.theWorld.getEntityByID(n);
        if (entityByID instanceof EntityPlayer && entityByID != mc.thePlayer) {
            EntityPlayer player = (EntityPlayer)entityByID;
            AntiCheat.Entry2 data = this.getData(player);
            data.deque2.addLast(l);
            this.handleDeque(data.deque2, l, 250L);
            if (this.fastBreakSetting.isEnabled()) {
                int size = data.deque2.size();
                if (size > 7) {
                    this.handleString(player.getName() + " fast break: " + size + " starts/250ms (limit 7).");
                }
            }
        }
    }

    private void handleEntityPlayer(EntityPlayer player, double d) {
        if (!(d <= 0.6)) {
            if (player.onGround) {
                this.handleString(player.getName() + " step: +" + this.computeString(d) + " blocks (max 0.6).");
            }
        }
    }

    public AntiCheat() {
        super("AntiCheat", 0, Category.MISCELLANEOUS);
        this.handleSettingArray(
            new Setting[]{
                this.speedSetting,
                this.flySetting,
                this.stepSetting,
                this.highCPSSetting,
                this.reachSetting,
                this.fastBreakSetting,
                this.maxReachSetting,
                this.notifyCooldownSetting
            }
        );
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (event instanceof EventUpdate) {
                    this.tick();
                } else if (event instanceof GetPacketEvent getPacketEvent && getPacketEvent.getDirection() == PacketDirection.INCOMING) {
                    this.onGetPacketEvent(getPacketEvent);
                }
            }
        }
    }

    private EntityPlayer computeEntityPlayer(Entity entity, long l) {
        EntityPlayer player = null;
        double MAX_VALUE = Double.MAX_VALUE;

        for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
            if (playerEntity != entity && playerEntity != mc.thePlayer) {
                AntiCheat.Entry2 entry2 = this.lookupMap.get(playerEntity.getUniqueID());
                if (entry2 != null && l - entry2.r <= 250L) {
                    double dist = playerEntity.getDistanceToEntity(entity);
                    if (dist < MAX_VALUE) {
                        MAX_VALUE = dist;
                        player = playerEntity;
                    }
                }
            }
        }

        return player;
    }

    public boolean checkState() {
        return true;
    }

    private void tick() {
        long now = System.currentTimeMillis();

        AntiCheat.Entry entry;
        while ((entry = this.concurrentLinkedQueue.poll()) != null) {
            this.handleEntry(entry, now);
        }

        for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
            if (playerEntity != mc.thePlayer) {
                AntiCheat.Entry2 data = this.getData(playerEntity);
                if (data.firstUpdate) {
                    data.firstUpdate = false;
                    data.amount = playerEntity.posX;
                    data.amount2 = playerEntity.posY;
                    data.amount3 = playerEntity.posZ;
                    data.flag = playerEntity.onGround;
                } else {
                    double d = playerEntity.posX - data.amount;
                    double d2 = playerEntity.posY - data.amount2;
                    double d3 = playerEntity.posZ - data.amount3;
                    double d4 = Math.hypot(d, d3);
                    double d5 = d4 * 20.0;
                    if (playerEntity.onGround) {
                        data.count = 0;
                    } else if (d2 >= -0.1) {
                        data.count++;
                    }

                    if (this.speedSetting.isEnabled()) {
                        this.handleEntityPlayer2(playerEntity, data, d5);
                    }

                    if (this.stepSetting.isEnabled()) {
                        this.handleEntityPlayer(playerEntity, d2);
                    }

                    if (this.flySetting.isEnabled()) {
                        this.handleEntityPlayer(playerEntity, data, d2);
                    }

                    data.amount = playerEntity.posX;
                    data.amount2 = playerEntity.posY;
                    data.amount3 = playerEntity.posZ;
                    data.flag = playerEntity.onGround;
                }
            }
        }

        if (++this.count5 >= 20) {
            this.count5 = 0;
            this.cleanup(now);
        }
    }

    private void handleInt(int n, long l) {
        Entity entityByID = mc.theWorld.getEntityByID(n);
        if (entityByID != null && entityByID != mc.thePlayer) {
            EntityPlayer player = this.computeEntityPlayer(entityByID, l);
            if (player != null) {
                double dist = player.getDistanceToEntity(entityByID);
                double d = player.capabilities.isCreativeMode ? 6.0 : this.maxReachSetting.computeDouble();
                if (dist > d) {
                    String name = player.getName();
                    String str = this.computeString(dist);
                    String name2 = entityByID.getName();
                    String str2 = str;
                    this.handleString(name + " reach: " + str2 + "m on " + name2 + " (max " + d + "m).");
                }
            }
        }
    }

    public boolean matchesString(String str) {
        return true;
    }

    private void handleInt3(int n, long l) {
        Entity entityByID = mc.theWorld.getEntityByID(n);
        if (entityByID instanceof EntityPlayer && entityByID != mc.thePlayer) {
            EntityPlayer player = (EntityPlayer)entityByID;
            AntiCheat.Entry2 data = this.getData(player);
            data.r = l;
            data.deque.addLast(l);
            this.handleDeque(data.deque, l, 1000L);
            if (this.highCPSSetting.isEnabled()) {
                int size = data.deque.size();
                if (size > 15) {
                    this.handleString(player.getName() + " high CPS: " + size + " (limit 15).");
                }

                int n2 = this.computeInt(data.deque, l, 350L);
                if (n2 > 6) {
                    this.handleString(player.getName() + " high CPS (short-term): " + n2 + " swings/350ms.");
                }
            }

            if (this.reachSetting.isEnabled()) {
                this.handleEntityPlayer(player);
            }
        }
    }

    private AntiCheat.Entry2 getData(EntityPlayer player) {
        this.lookupMap2.put(player.getEntityId(), player.getUniqueID());
        return this.lookupMap.computeIfAbsent(player.getUniqueID(), uuid -> new AntiCheat.Entry2());
    }

    public static class Entry {
        public final int count;
        private final long timestamp;
        public final AntiCheat.SwingMode swingMode;

        public Entry(int count, long timestamp, AntiCheat.SwingMode swingMode) {
            this.count = count;
            this.timestamp = timestamp;
            this.swingMode = swingMode;
        }
    }

    public static class Entry2 {
        public boolean firstUpdate = true;
        public double amount;
        public double amount2;
        public double amount3;
        public boolean flag;
        public int count;
        public int count2;
        public long r;
        public final Deque<Long> deque = new ArrayDeque<>();
        public final Deque<Long> deque2 = new ArrayDeque<>();
    }

    public static enum SwingMode {
        SWING,
        BREAK,
        HURT,
        DESTROY;

        private static AntiCheat.SwingMode[] computeSwingModeArray() {
            return new AntiCheat.SwingMode[]{SWING, BREAK, HURT, DESTROY};
        }
    }
}
