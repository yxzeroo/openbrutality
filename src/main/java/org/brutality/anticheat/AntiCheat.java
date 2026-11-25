package org.brutality.anticheat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import org.brutality.Brutality;
import org.brutality.anticheat.check.Check;
import org.brutality.anticheat.check.NoSlowCheck;
import org.brutality.anticheat.check.RotationCheck;
import org.brutality.anticheat.check.SprintCheck;
import org.brutality.anticheat.check.VelocityCheck;
import org.brutality.anticheat.data.CollectSampleHandler;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.packet.GetPacketEvent;

public class AntiCheat {
    private static final AntiCheat instance = new AntiCheat();
    private final Minecraft mc = Minecraft.getMinecraft();
    private final List<Check> entries = new ArrayList<>();
    private boolean enabled2 = false;
    private VelocityCheck velocityCheck = new VelocityCheck();
    private RotationCheck rotationCheck = new RotationCheck();
    private NoSlowCheck noSlowCheck = new NoSlowCheck();
    private SprintCheck sprintCheck = new SprintCheck();

    private void onUpdate() {
        if (this.mc.thePlayer != null && this.mc.theWorld != null) {
            try {
                for (Object playerEntity : this.mc.theWorld.playerEntities) {
                    if (playerEntity instanceof EntityPlayer player && player != this.mc.thePlayer && player.isEntityAlive()) {
                        CollectSampleHandler.collectSample(player);

                        for (Check check : this.entries) {
                            if (check.isEnabled()) {
                                check.onUpdate(player);
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private org.brutality.module.impl.misc.AntiCheat computeAntiCheat() {
        try {
            return (org.brutality.module.impl.misc.AntiCheat)Brutality.moduleManager.computeModule("AntiCheat");
        } catch (Exception e) {
            return null;
        }
    }

    private AntiCheat() {
        this.entries.add(this.velocityCheck);
        this.entries.add(this.rotationCheck);
        this.entries.add(this.noSlowCheck);
        this.entries.add(this.sprintCheck);
    }

    public void onEvent(Event event) {
        if (this.enabled2) {
            try {
                org.brutality.module.impl.misc.AntiCheat antiCheat = this.computeAntiCheat();
                if (antiCheat != null) {
                    this.handleAntiCheat(antiCheat);
                }

                if (event instanceof GetPacketEvent getPacketEvent) {
                    this.handlePacket(getPacketEvent.getPacket());
                } else if (event instanceof EventUpdate) {
                    this.onUpdate();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static AntiCheat getInstance() {
        return instance;
    }

    public void disable() {
        this.enabled2 = false;
        CollectSampleHandler.handleEntityPlayer(null);

        for (Check check : this.entries) {
            check.handleEntityPlayer(null);
        }
    }

    private void handlePacket(Packet<?> packet) {
        if (!this.entries.isEmpty()) {
            try {
                for (Check check : this.entries) {
                    if (check.isEnabled()) {
                        check.handlePacket(packet);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void enable() {
        this.enabled2 = true;
    }

    public List<Check> computeList() {
        return new ArrayList<>(this.entries);
    }

    public void handleAntiCheat(org.brutality.module.impl.misc.AntiCheat antiCheat) {
        this.velocityCheck.setEnabled(antiCheat.matchesString("velocity"));
        this.rotationCheck.setEnabled(antiCheat.matchesString("rotation"));
        this.noSlowCheck.setEnabled(antiCheat.matchesString("noslow"));
        this.sprintCheck.setEnabled(antiCheat.matchesString("sprint"));
    }

    public void handleEntityPlayer(EntityPlayer player) {
        if (this.enabled2) {
            try {
                CollectSampleHandler.handleEntityPlayer(player);

                for (Check check : this.entries) {
                    check.handleEntityPlayer(player);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public boolean isEnabled() {
        return this.enabled2;
    }
}
