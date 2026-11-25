package org.brutality.module.impl.pit.hypixel.utility;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.DataWatcher.WatchableObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.brutality.utils.player.PollHelper;

public final class ArrowSpawnHelper {
    private final PitDebugger pitDebugger;
    private final PollHelper pollHelper = new PollHelper();
    private final Map<Integer, ArrowSpawnHelper.Entry> lookupMap = new LinkedHashMap<>();
    private static final long dz = 30000L;

    public void clear() {
        this.pollHelper.clear();
        this.lookupMap.clear();
    }

    private void handleS0EPacketSpawnObject(S0EPacketSpawnObject packet) {
        EntityPlayer player = this.pitDebugger.computeEntityPlayer(packet.func_149009_m());
        if (player != null) {
            int entityID = packet.getEntityID();
            long l = this.pitDebugger.computeLong();
            if (this.lookupMap.size() >= 256) {
                this.lookupMap.remove(this.lookupMap.keySet().iterator().next());
            }

            this.lookupMap.put(entityID, new ArrowSpawnHelper.Entry(player.getUniqueID(), l));
            this.pollHelper.handleInt(entityID, player.getUniqueID(), true, l);
            this.pitDebugger
                .handleString(
                    "arrow_spawn",
                    "S0E_type_60",
                    false,
                    PitDebugger.computeJsonObject("shooter", this.pitDebugger.computeString(player.getUniqueID()), "projectile", entityID)
                );
            if (player != this.pitDebugger.computeEntityPlayer()) {
                this.pitDebugger
                    .handleString(
                        "bow_release",
                        "arrow_spawn_with_shooter",
                        true,
                        PitDebugger.computeJsonObject("player", this.pitDebugger.computeString(player.getUniqueID()), "projectile", entityID)
                    );
            }
        }
    }

    public ArrowSpawnHelper(PitDebugger pitDebugger) {
        this.pitDebugger = pitDebugger;
    }

    private void handleEntityArrow(EntityArrow entity, ArrowSpawnHelper.Entry entry, long l) {
        if (entity.inGround) {
            this.pollHelper.handleInt(entity.getEntityId(), null, l);
        } else {
            Vec3 vec = new Vec3(entity.prevPosX, entity.prevPosY, entity.prevPosZ);
            Vec3 vec2 = new Vec3(entity.posX + entity.motionX, entity.posY + entity.motionY, entity.posZ + entity.motionZ);
            double d = vec.squareDistanceTo(vec2);
            if (!(d < 1.0E-4) && !(d > 144.0)) {
                MovingObjectPosition hit = this.pitDebugger.worldClient.rayTraceBlocks(vec, vec2, false, true, false);
                if (hit != null) {
                    vec2 = hit.hitVec;
                }

                UUID uuid = null;

                for (Entity entity2 : this.pitDebugger
                    .worldClient
                    .getEntitiesWithinAABBExcludingEntity(
                        entity,
                        entity.getEntityBoundingBox()
                            .offset(vec.xCoord - entity.posX, vec.yCoord - entity.posY, vec.zCoord - entity.posZ)
                            .addCoord(vec2.xCoord - vec.xCoord, vec2.yCoord - vec.yCoord, vec2.zCoord - vec.zCoord)
                            .expand(1.0, 1.0, 1.0)
                    )) {
                    if (entity2 instanceof EntityPlayer player
                        && this.pitDebugger.matchesEntityPlayer(player)
                        && !player.isDead
                        && !entry.uuid.equals(player.getUniqueID())) {
                        AxisAlignedBB box = player.getEntityBoundingBox().expand(0.3, 0.3, 0.3);
                        if (box.calculateIntercept(vec, vec2) != null || box.isVecInside(vec)) {
                            if (uuid != null) {
                                this.pollHelper.handleInt(entity.getEntityId(), null, l);
                                return;
                            }

                            uuid = player.getUniqueID();
                        }
                    }
                }

                if (uuid != null) {
                    this.pollHelper.handleInt(entity.getEntityId(), uuid, l);
                }
            }
        }
    }

    public void handlePacket(Packet<?> packet) {
        if (packet instanceof S04PacketEntityEquipment packet2) {
            EntityPlayer player = this.pitDebugger.computeEntityPlayer(packet2.getEntityID());
            if (player != null) {
                this.pitDebugger.handleEntityPlayer(player, packet2.getEquipmentSlot(), packet2.getItemStack());
            }
        } else if (packet instanceof S0BPacketAnimation packet3 && packet3.getAnimationType() == 0) {
            EntityPlayer player2 = this.pitDebugger.computeEntityPlayer(packet3.getEntityID());
            if (player2 != null) {
                this.pitDebugger.handleString("melee_swing", "S0B_swing_animation", false, this.pitDebugger.computeJsonObject(player2));
            }
        } else if (packet instanceof S1CPacketEntityMetadata packet4) {
            EntityPlayer player3 = this.pitDebugger.computeEntityPlayer(packet4.getEntityId());
            if (player3 == null || packet4.func_149376_c() == null) {
                return;
            }

            for (WatchableObject watchableObject : packet4.func_149376_c()) {
                if (watchableObject.getDataValueId() == 6 && watchableObject.getObject() instanceof Float val) {
                    this.pitDebugger.handleEntityPlayer(player3, val, "S1C_health");
                }
            }
        } else if (packet instanceof S06PacketUpdateHealth packet5) {
            EntityPlayer player4 = this.pitDebugger.computeEntityPlayer();
            if (player4 != null && this.pitDebugger.matchesEntityPlayer(player4)) {
                this.pitDebugger.handleEntityPlayer(player4, packet5.getHealth(), "S06_health");
            }
        } else if (packet instanceof S19PacketEntityStatus packet6) {
            EntityPlayer player5 = this.pitDebugger.computeEntityPlayer(packet6.getEntityId());
            if (player5 == null) {
                return;
            }

            if (packet6.getOpCode() == 2) {
                this.pollHelper.handleUUID(player5.getUniqueID(), this.pitDebugger.computeLong());
                this.pitDebugger.handleString("hurt", "S19_status_2", false, this.pitDebugger.computeJsonObject(player5));
            } else if (packet6.getOpCode() == 3 && this.pitDebugger.itemSet.add(player5.getUniqueID())) {
                this.pitDebugger.handleString("death", "S19_status_3", false, this.pitDebugger.computeJsonObject(player5));
            }
        } else if (packet instanceof S0CPacketSpawnPlayer packet7) {
            if (this.pitDebugger.itemSet.remove(packet7.getPlayer())) {
                this.pitDebugger
                    .handleString("respawn", "S0C_after_death", true, PitDebugger.computeJsonObject("player", this.pitDebugger.computeString(packet7.getPlayer())));
            }
        } else if (packet instanceof S0EPacketSpawnObject packet8 && packet8.getType() == 60) {
            this.handleS0EPacketSpawnObject(packet8);
        } else if (packet instanceof S13PacketDestroyEntities packet9) {
            for (int entityID : packet9.getEntityIDs()) {
                ArrowSpawnHelper.Entry entry = this.lookupMap.remove(entityID);
                if (entry != null) {
                    long l = this.pitDebugger.computeLong();
                    if (entry.entityArrow != null) {
                        this.handleEntityArrow(entry.entityArrow, entry, l);
                    }

                    this.pollHelper.handleInt(entityID, l);
                }
            }
        } else if (packet instanceof S2BPacketChangeGameState packet10 && packet10.getGameState() == 6) {
            EntityPlayer player6 = this.pitDebugger.computeEntityPlayer();
            if (player6 != null && this.pitDebugger.matchesEntityPlayer(player6)) {
                this.pitDebugger
                    .handleString(
                        "arrow_hit",
                        "S2B_ranged_hit_feedback",
                        false,
                        PitDebugger.computeJsonObject("shooter", this.pitDebugger.computeString(player6.getUniqueID()))
                    );
            }
        } else if (packet instanceof S25PacketBlockBreakAnim packet11) {
            EntityPlayer player7 = this.pitDebugger.computeEntityPlayer(packet11.getBreakerId());
            if (player7 != null) {
                BlockPos pos = packet11.getPosition();
                this.pitDebugger
                    .handleString(
                        "block_break_animation",
                        "S25",
                        false,
                        PitDebugger.computeJsonObject(
                            "player",
                            this.pitDebugger.computeString(player7.getUniqueID()),
                            "block",
                            PitDebugger.computeJsonArray(pos.getX(), pos.getY(), pos.getZ()),
                            "progress",
                            packet11.getProgress()
                        )
                    );
            }
        }
    }

    public void process() {
        long l = this.pitDebugger.computeLong();
        this.lookupMap.entrySet().removeIf(entry -> {
            ArrowSpawnHelper.Entry val = entry.getValue();
            if (l - val.timestamp > dz) {
                return true;
            }
            if (this.pitDebugger.worldClient.getEntityByID(entry.getKey()) instanceof EntityArrow entity) {
                val.entityArrow = entity;
                this.handleEntityArrow(entity, val, l);
            }

            return false;
        });

        for (PollHelper.Entry2 entry2 : this.pollHelper.poll(l)) {
            if (this.pitDebugger.computeEntityPlayer() == null || !entry2.uuid.equals(this.pitDebugger.computeEntityPlayer().getUniqueID())) {
                this.pitDebugger
                    .handleString(
                        "arrow_hit",
                        "arrow_path_hurt_and_removal",
                        true,
                        PitDebugger.computeJsonObject(
                            "shooter", this.pitDebugger.computeString(entry2.uuid), "victim", this.pitDebugger.computeString(entry2.uuid2)
                        )
                    );
            }
        }
    }

    public static final class Entry {
        public final UUID uuid;
        public final long timestamp;
        public EntityArrow entityArrow;

        public Entry(UUID uuid, long timestamp) {
            this.uuid = uuid;
            this.timestamp = timestamp;
        }
    }
}
