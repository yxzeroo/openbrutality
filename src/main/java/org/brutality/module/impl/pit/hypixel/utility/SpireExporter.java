package org.brutality.module.impl.pit.hypixel.utility;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.pit.hypixel.utility.spire.SizeHelper;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;

public class SpireExporter extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final BooleanSetting autoExportSetting = new BooleanSetting("Auto Export", this, true);
    private final NumberSetting settleTimeSSetting = new NumberSetting("Settle Time (s)", this, 8.0, 3.0, 60.0, 1.0);
    private final BooleanSetting exportNowSetting = new BooleanSetting("Export Now", this, false);
    private final StringSetting ignoreBlocksSetting = new StringSetting(
        "Ignore Blocks",
        this,
        "obsidian, prismarine, planks, log, log2, wooden_slab, double_wooden_slab, oak_stairs, spruce_stairs, birch_stairs, jungle_stairs, dark_oak_stairs, acacia_stairs, fence, fence_gate, water, flowing_water, lava, flowing_lava, fire"
    );
    private long timestamp;
    private boolean flag;

    public SpireExporter() {
        super("SpireExporter", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.autoExportSetting, this.settleTimeSSetting, this.exportNowSetting, this.ignoreBlocksSetting});
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() && mc.theWorld != null) {
            if (event instanceof EventPacketReceive) {
                Packet packet = ((EventPacketReceive)event).getPacket();
                if (packet instanceof S23PacketBlockChange || packet instanceof S22PacketMultiBlockChange) {
                    SizeHelper.computeList2(((EventPacketReceive)event).getPacket());
                }
            } else if (event instanceof EventTick && ((EventTick)event).getState() == EventState.PRE) {
                SizeHelper.handleString(SpireFix.computeString(this.ignoreBlocksSetting));
                SizeHelper.tick();
                if (this.exportNowSetting.isEnabled()) {
                    this.exportNowSetting.setEnabled(false);
                    this.export(true);
                } else if (this.autoExportSetting.isEnabled() && SizeHelper.size() != 0) {
                    long timestamp2 = SizeHelper.getTimestamp2();
                    if (timestamp2 != 0L && timestamp2 != this.timestamp) {
                        boolean flag = System.currentTimeMillis() - timestamp2 >= (long)(this.settleTimeSSetting.computeDouble() * 1000.0);
                        boolean flag2 = !SizeHelper.isActive() && SizeHelper.getTimestamp3() > 0L;
                        if (flag || flag2) {
                            this.export(false);
                        }
                    }
                }
            }
        }
    }

    private void handleString(String str) {
        if (mc.thePlayer != null) {
            EnumChatFormatting DARK_GRAY = EnumChatFormatting.DARK_GRAY;
            EnumChatFormatting LIGHT_PURPLE = EnumChatFormatting.LIGHT_PURPLE;
            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GRAY + "[" + LIGHT_PURPLE + "SpireExporter" + DARK_GRAY + "] " + str));
        }
    }

    private void export(boolean flag) {
        if (!this.flag) {
            if (SizeHelper.size() == 0) {
                if (flag) {
                    this.handleString(EnumChatFormatting.RED + "No spire blocks captured yet. Stay in the lobby while the Spire spawns.");
                }
            } else {
                this.flag = true;
                this.timestamp = SizeHelper.getTimestamp2();
                int size = SizeHelper.size();
                CompletableFuture.<File>supplyAsync(() -> {
                        try {
                            return SizeHelper.computeFile();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .whenComplete(
                        (file, t) -> mc.addScheduledTask(
                            () -> {
                                this.flag = false;
                                if (t != null) {
                                    this.handleString(EnumChatFormatting.RED + "Spire export failed: " + t.getCause());
                                } else if (file != null) {
                                    EnumChatFormatting GREEN = EnumChatFormatting.GREEN;
                                    EnumChatFormatting WHITE = EnumChatFormatting.WHITE;
                                    String name = file.getName();
                                    EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
                                    String str = name;
                                    EnumChatFormatting enumChatFormatting = WHITE;
                                    this.handleString(
                                        GREEN + "Exported spire (" + size + " blocks) to " + enumChatFormatting + "Brutality/spire-exports/" + str + GRAY + " (+ .json)"
                                    );
                                }
                            }
                        )
                    );
            }
        }
    }
}
