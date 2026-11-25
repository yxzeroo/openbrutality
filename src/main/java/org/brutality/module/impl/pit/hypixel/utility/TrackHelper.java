package org.brutality.module.impl.pit.hypixel.utility;

import com.google.gson.JsonArray;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public final class TrackHelper {
    private static final int count = 10;
    private static final int count2 = 100;
    private final PitDebugger pitDebugger;
    private final Map<UUID, TrackHelper.Entry> lookupMap = new HashMap<>();
    private long timestamp;
    private static final long timestamp2 = 100L;

    private TrackHelper.DDEntry computeDDEntry(EntityPlayer player) {
        return new TrackHelper.DDEntry(
            PitDebugger.tick,
            this.pitDebugger.computeLong(),
            PitDebugger.computeLong(player.posX),
            PitDebugger.computeLong(player.posY),
            PitDebugger.computeLong(player.posZ),
            player.onGround,
            player.isSneaking()
        );
    }

    public void clear() {
        this.lookupMap.clear();
    }

    private TrackHelper.Entry track(EntityPlayer player) {
        return this.lookupMap
            .compute(player.getUniqueID(), (uuid, entry) -> (TrackHelper.Entry)(entry != null && entry.entityPlayer == player ? entry : new TrackHelper.Entry(player)));
    }

    public void handleEntityPlayer2(EntityPlayer player) {
        this.handleEntityPlayer(player, this.track(player));
    }

    public void handleEntityPlayer(EntityPlayer player) {
        TrackHelper.Entry entry = this.track(player);
        this.handleEntityPlayer(player, entry);
        TrackHelper.DDEntry ddEntry = this.computeDDEntry(player);
        if (entry.ddEntry == null) {
            if (entry.deque.size() == 10) {
                entry.deque.removeFirst();
            }

            entry.deque.addLast(ddEntry);
        } else {
            TrackHelper.DDEntry ddEntry2 = entry.ddEntry;
            this.pitDebugger
                .handleString(
                    "launcher_trajectory",
                    "client_entity",
                    false,
                    PitDebugger.computeJsonObject(
                        "player",
                        this.pitDebugger.computeString(player.getUniqueID()),
                        "ride",
                        entry.timestamp,
                        "d",
                        PitDebugger.computeJsonArray(ddEntry.dF - ddEntry2.dF, ddEntry.dG - ddEntry2.dG, ddEntry.dH - ddEntry2.dH),
                        "ground",
                        player.onGround,
                        "mounted",
                        entry.entity != null,
                        "sneaking",
                        ddEntry.fO
                    )
                );
            entry.ddEntry = ddEntry;
            if (entry.entity == null) {
                if (player.onGround) {
                    this.pitDebugger
                        .handleString(
                            "launcher_landing",
                            "grounded_after_dismount",
                            true,
                            PitDebugger.computeJsonObject(
                                "player", this.pitDebugger.computeString(player.getUniqueID()), "ride", entry.timestamp, "p", ddEntry.computeJsonArray()
                            )
                        );
                    entry.ddEntry = null;
                } else if (PitDebugger.tick - entry.timestamp2 > timestamp2) {
                    this.pitDebugger
                        .handleString(
                            "launcher_end",
                            "landing_timeout",
                            true,
                            PitDebugger.computeJsonObject("player", this.pitDebugger.computeString(player.getUniqueID()), "ride", entry.timestamp)
                        );
                    entry.ddEntry = null;
                }
            }
        }
    }

    public void process() {
        this.lookupMap.values().removeIf(entry -> this.pitDebugger.worldClient.getEntityByID(entry.entityPlayer.getEntityId()) != entry.entityPlayer);
    }

    private void handleEntityPlayer(EntityPlayer player, TrackHelper.Entry entry) {
        Entity ridingEntity = player.ridingEntity;
        if (ridingEntity != entry.entity) {
            String str = this.pitDebugger.computeString(player.getUniqueID());
            if (entry.entity != null) {
                entry.timestamp2 = PitDebugger.tick;
                this.pitDebugger
                    .handleString("launcher_dismount", "client_mount_state", false, PitDebugger.computeJsonObject("player", str, "ride", entry.timestamp));
                if (player.isSneaking() || entry.ddEntry != null && entry.ddEntry.fO) {
                    this.pitDebugger
                        .handleString("launcher_sneak_cancel", "sneak_at_dismount", true, PitDebugger.computeJsonObject("player", str, "ride", entry.timestamp));
                }
            }

            entry.entity = ridingEntity;
            if (ridingEntity != null) {
                TrackHelper.DDEntry ddEntry = this.computeDDEntry(player);
                entry.ddEntry = ddEntry;
                entry.timestamp = ++this.timestamp;
                if (!entry.deque.isEmpty()) {
                    JsonArray jsonArray = new JsonArray();

                    for (TrackHelper.DDEntry ddEntry2 : entry.deque) {
                        jsonArray.add(
                            PitDebugger.computeJsonObject(
                                "tick", ddEntry2.dD, "elapsedMs", ddEntry2.dE, "p", ddEntry2.computeJsonArray(), "ground", ddEntry2.fN, "sneaking", ddEntry2.fO
                            )
                        );
                    }

                    this.pitDebugger
                        .handleString(
                            "launcher_approach",
                            "samples_before_mount",
                            true,
                            PitDebugger.computeJsonObject("player", str, "ride", entry.timestamp, "samples", jsonArray)
                        );
                    entry.deque.clear();
                }

                Block block = player.worldObj.getBlockState(new BlockPos(player.posX, player.posY - 0.01, player.posZ)).getBlock();
                this.pitDebugger
                    .handleString(
                        "launcher_takeoff",
                        "mount_in_pit",
                        true,
                        PitDebugger.computeJsonObject(
                            "player",
                            str,
                            "ride",
                            entry.timestamp,
                            "p",
                            ddEntry.computeJsonArray(),
                            "mountType",
                            EntityList.getEntityString(ridingEntity),
                            "blockBelow",
                            String.valueOf(Block.blockRegistry.getNameForObject(block))
                        )
                    );
            }
        }
    }

    public TrackHelper(PitDebugger pitDebugger) {
        this.pitDebugger = pitDebugger;
    }

    public record DDEntry(long dD, long dE, long dF, long dG, long dH, boolean fN, boolean fO) {

        public JsonArray computeJsonArray() {
            return PitDebugger.computeJsonArray(this.dF, this.dG, this.dH);
        }
    }

    public static final class Entry {
        public final EntityPlayer entityPlayer;
        public final Deque<TrackHelper.DDEntry> deque = new ArrayDeque<>(10);
        public Entity entity;
        public TrackHelper.DDEntry ddEntry;
        public long timestamp;
        public long timestamp2;

        public Entry(EntityPlayer entityPlayer) {
            this.entityPlayer = entityPlayer;
        }
    }
}
