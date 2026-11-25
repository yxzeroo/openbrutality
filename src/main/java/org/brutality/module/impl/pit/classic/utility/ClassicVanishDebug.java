package org.brutality.module.impl.pit.classic.utility;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.movement.flight.PruneHandler;
import org.brutality.utils.MessageUtils;

public final class ClassicVanishDebug extends Module {
    private InfoHelper infoHelper;
    private final ArrayDeque<Packet<?>> arrayDeque = new ArrayDeque<>();
    private final PruneHandler pruneHandler = new PruneHandler();
    private final Map<UUID, Long> lookupMap = new LinkedHashMap<>();
    private WorldClient worldClient;
    private long timestamp;
    private long timestamp2;
    private int count;
    private int count2;
    private int count3;
    private int count4;
    private int count5;
    private boolean flag;

    @Override
    public synchronized void onEvent(Event event) {
        if (this.checkState44() && this.flag) {
            if (event instanceof RenderEvent4) {
                this.reset();
            } else if (event instanceof GetPacketEvent getPacketEvent) {
                if (getPacketEvent.getDirection() == PacketDirection.INCOMING && !getPacketEvent.cancel2()) {
                    Packet packet = getPacketEvent.getPacket();
                    if (packet instanceof S14PacketEntity
                        || packet instanceof S18PacketEntityTeleport
                        || packet instanceof S13PacketDestroyEntities
                        || packet instanceof S38PacketPlayerListItem
                        || packet instanceof S0CPacketSpawnPlayer
                        || packet instanceof S0EPacketSpawnObject
                        || packet instanceof S0FPacketSpawnMob) {
                        if (this.arrayDeque.size() >= 512) {
                            this.count2++;
                        } else {
                            this.arrayDeque.addLast(packet);
                        }
                    }
                }
            } else if (event instanceof EventTick && ((EventTick)event).getState() == EventState.PRE) {
                if (mc.theWorld != this.worldClient) {
                    this.reset();
                    this.worldClient = mc.theWorld;
                    this.infoHelper.info("[ClassicVanishDebug] world changed; cleared evidence, 3-second warmup");
                }

                if (this.worldClient != null && mc.getNetHandler() != null) {
                    long l = System.nanoTime() / 1000000L;

                    for (int i = 0; i < 256 && !this.arrayDeque.isEmpty(); i++) {
                        this.handlePacket(this.arrayDeque.removeFirst(), l);
                    }

                    this.pruneHandler.prune(l);
                    this.lookupMap.values().removeIf(val -> l - val > 30000L);
                    if (l - this.timestamp2 >= 30000L) {
                        this.infoHelper
                            .info(
                                "[ClassicVanishDebug] summary: packets={}, trackedUnknown={}, entityReports={}, missingTabUpdates={}, dropped={}; not a vanish verdict",
                                this.count,
                                this.pruneHandler.size(),
                                this.count3,
                                this.count4,
                                this.count2
                            );
                        this.timestamp2 = l;
                        this.count5 = 0;
                    }
                }
            }
        }
    }

    private void reset() {
        this.arrayDeque.clear();
        this.pruneHandler.clear();
        this.lookupMap.clear();
        this.worldClient = null;
        this.timestamp = this.timestamp2 = System.nanoTime() / 1000000L;
        this.count = this.count2 = this.count3 = this.count4 = this.count5 = 0;
    }

    private void handlePacket(Packet<?> packet, long l) {
        this.count++;
        if (packet instanceof S13PacketDestroyEntities) {
            for (int entityID : ((S13PacketDestroyEntities)packet).getEntityIDs()) {
                this.pruneHandler.handleInt(entityID, l);
            }
        } else if (packet instanceof S0CPacketSpawnPlayer) {
            this.pruneHandler.handleInt(((S0CPacketSpawnPlayer)packet).getEntityID());
        } else if (packet instanceof S0EPacketSpawnObject) {
            this.pruneHandler.handleInt(((S0EPacketSpawnObject)packet).getEntityID());
        } else if (packet instanceof S0FPacketSpawnMob) {
            this.pruneHandler.handleInt(((S0FPacketSpawnMob)packet).getEntityID());
        } else if (l - this.timestamp >= 3000L) {
            if (!(packet instanceof S38PacketPlayerListItem packet2)) {
                String str = "relative/rotation only; absolute position unknown";
                int n;
                if (packet instanceof S14PacketEntity) {
                    n = ((S14PacketEntity)packet).getEntityId();
                } else {
                    if (!(packet instanceof S18PacketEntityTeleport packet3)) {
                        return;
                    }

                    n = packet3.getEntityId();
                    double d = packet3.getX() / 32.0;
                    double d2 = packet3.getZ() / 32.0;
                    double d3 = packet3.getY() / 32.0;
                    double d4 = d;
                    str = "absolute=" + d4 + "," + d3 + "," + d2;
                }

                if (this.worldClient.getEntityByID(n) != null) {
                    this.pruneHandler.handleInt(n);
                } else {
                    if (this.pruneHandler.matchesInt(n, l)) {
                        this.count3++;
                        if (this.count5++ < 20) {
                            this.infoHelper
                                .info(
                                    "[ClassicVanishDebug] repeated unknown entity id={}, packet={}, {}; entity type/name unknown; NOT confirmed vanish",
                                    n,
                                    packet.getClass().getSimpleName(),
                                    str
                                );
                        }
                    }
                }
            } else if (packet2.getAction() == S38PacketPlayerListItem.Action.UPDATE_LATENCY) {
                for (S38PacketPlayerListItem.AddPlayerData addPlayerData : packet2.getEntries()) {
                    UUID id = addPlayerData.getProfile().getId();
                    if (id != null && mc.getNetHandler().getPlayerInfo(id) == null) {
                        this.count4++;
                        Long val = this.lookupMap.get(id);
                        if (val == null || l - val >= 10000L) {
                            if (this.lookupMap.size() >= 256) {
                                this.lookupMap.remove(this.lookupMap.keySet().iterator().next());
                            }

                            this.lookupMap.put(id, l);
                            if (this.count5++ < 20) {
                                this.infoHelper
                                    .info("[ClassicVanishDebug] latency update for UUID absent from tab: {}; could be tab filtering or packet ordering", id);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public synchronized void onDisable() {
        this.flag = false;
        if (this.infoHelper != null) {
            this.infoHelper.info("[ClassicVanishDebug] disabled; reports={}, dropped={}", this.count3, this.count2);
            this.infoHelper.close();
            this.infoHelper = null;
        }

        this.reset();
        super.onDisable();
    }

    public ClassicVanishDebug() {
        super("Classic Vanish Debug", 0, Category.PIT);
    }

    @Override
    public synchronized void onEnable() {
        super.onEnable();
        this.reset();
        this.flag = false;
        if (this.infoHelper != null) {
            this.infoHelper.close();
            this.infoHelper = null;
        }

        try {
            this.infoHelper = new InfoHelper(mc.mcDataDir.toPath());
        } catch (SecurityException | IOException e) {
            MessageUtils.x("[Classic Vanish Debug] Could not open Brutality/logs: " + e.getMessage());
            return;
        }

        this.flag = true;
        this.worldClient = mc.theWorld;
        this.infoHelper.info("[ClassicVanishDebug] enabled; passive diagnostics, findings are NOT proof of vanished players");
        MessageUtils.x("[Classic Vanish Debug] Logging to Brutality/logs/classic-vanish-debug-0.log. Unknown entities are not confirmed staff.");
    }
}
