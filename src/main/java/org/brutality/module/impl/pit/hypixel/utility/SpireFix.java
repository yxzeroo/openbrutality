package org.brutality.module.impl.pit.hypixel.utility;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S22PacketMultiBlockChange.BlockUpdateData;
import net.minecraft.util.BlockPos;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.pit.hypixel.utility.spire.SizeHelper;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;

public class SpireFix extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static final String text = "obsidian, prismarine, planks, log, log2, wooden_slab, double_wooden_slab, oak_stairs, spruce_stairs, birch_stairs, jungle_stairs, dark_oak_stairs, acacia_stairs, fence, fence_gate, water, flowing_water, lava, flowing_lava, fire";
    private final ModeSetting throttleSetting = new ModeSetting("Mode", this, "Hide", Arrays.asList("Hide", "Throttle"));
    private final StringSetting ignoreBlocksSetting = new StringSetting(
        "Ignore Blocks",
        this,
        "obsidian, prismarine, planks, log, log2, wooden_slab, double_wooden_slab, oak_stairs, spruce_stairs, birch_stairs, jungle_stairs, dark_oak_stairs, acacia_stairs, fence, fence_gate, water, flowing_water, lava, flowing_lava, fire"
    );
    private final NumberSetting hideDurationSSetting = new NumberSetting("Hide Duration (s)", this, 5.0, 1.0, 15.0, 1.0);
    private final BooleanSetting debugSetting = new BooleanSetting("Debug", this, false);
    private long timestamp = 0L;
    private int count;
    private int count2;
    private int count3;
    private int count4;
    private final Queue<Packet<?>> queue = new ArrayDeque<>();
    private static final long timestamp2 = 50L;

    private String computeString(String str) {
        int n = str.indexOf("Spawning in");
        if (n == -1) {
            return "";
        }
        int n2 = n + "Spawning in".length();
        int n3 = str.indexOf("quarter", n2);
        if (n3 == -1) {
            n3 = str.length();
        }

        return str.substring(n2, n3).trim();
    }

    private void process() {
        if (!this.queue.isEmpty() && mc.getNetHandler() != null) {
            int n = System.currentTimeMillis() >= this.timestamp ? 2 : 0;

            while (n-- > 0 && !this.queue.isEmpty()) {
                Packet packet = this.queue.poll();

                try {
                    packet.processPacket(mc.getNetHandler());
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void restoreHiddenSpire() {
        if (this.throttleSetting.getText().equals("Hide") && SizeHelper.size() != 0) {
            List list = new ArrayList<>(SizeHelper.computeMap().entrySet());
            Thread thread = new Thread(
                () -> {
                    for (short i = 0; i < list.size(); i += 300) {
                        short s = i;
                        int n = Math.min(list.size(), i + 300);
                        mc.addScheduledTask(
                            () -> {
                                if (mc.theWorld != null) {
                                    for (int j = s; j < n; j++) {
                                        mc.theWorld
                                            .invalidateRegionAndSetBlock(
                                                (BlockPos)((Entry)list.get(j)).getKey(),
                                                ((org.brutality.module.impl.pit.hypixel.utility.spire.SizeHelper.Entry)((Entry)list.get(j)).getValue()).iBlockState
                                            );
                                    }
                                }
                            }
                        );

                        try {
                            Thread.sleep(timestamp2);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                },
                "Brutality-SpireRestore"
            );
            thread.setDaemon(true);
            thread.start();
        }
    }

    private boolean matchesBlockUpdateDataArray(BlockUpdateData[] blocks) {
        if (blocks == null) {
            return false;
        }
        for (BlockUpdateData block : blocks) {
            if (block != null && this.matchesBlockPos(block.getPos())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.restoreHiddenSpire();
    }

    private int[] computeIntArray(String str) {
        if (str == null) {
            return null;
        }
        String str2 = str.toLowerCase();
        if (str2.contains("water") || str2.contains("summer") || str2.contains("seaweed") || str2.contains("jungle") || str2.contains("badlands")) {
            return new int[]{-200, 0, -200, 0};
        }
        if (str2.contains("sky") || str2.contains("spring") || str2.contains("shipwreck") || str2.contains("port") || str2.contains("palace")) {
            return new int[]{-200, 0, 0, 200};
        }
        if (str2.contains("mountain") || str2.contains("autumn") || str2.contains("temple") || str2.contains("farm") || str2.contains("fortress")) {
            return new int[]{0, 200, -200, 0};
        }
        return !str2.contains("lava") && !str2.contains("winter") && !str2.contains("geyser") && !str2.contains("city") && !str2.contains("garden")
            ? null
            : new int[]{0, 200, 0, 200};
    }

    public SpireFix() {
        super("SpireFix", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.throttleSetting, this.ignoreBlocksSetting, this.hideDurationSSetting, this.debugSetting});
        this.hideDurationSSetting.computeNumberSetting(() -> this.throttleSetting.getText().equals("Throttle"));
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (mc.theWorld != null && mc.thePlayer != null) {
                if (event instanceof RenderEvent4) {
                    this.timestamp = 0L;
                    this.queue.clear();
                } else if (event instanceof EventTick && ((EventTick)event).getState() == EventState.PRE) {
                    SizeHelper.handleString(computeString(this.ignoreBlocksSetting));
                    SizeHelper.tick();
                    this.process();
                } else if (event instanceof EventPacketReceive event2) {
                    if (this.throttleSetting.getText().equals("Hide")) {
                        this.hideSpirePacket(event2);
                    } else if (event2.getPacket() instanceof S02PacketChat) {
                        S02PacketChat packet = (S02PacketChat)event2.getPacket();
                        String text = packet.getChatComponent().getUnformattedText();
                        if (text.contains("SPIRE") && text.contains("Spawning in") && text.contains("quarter")) {
                            int[] ints = this.computeIntArray(this.computeString(text));
                            if (ints != null) {
                                this.count = ints[0];
                                this.count2 = ints[1];
                                this.count3 = ints[2];
                                this.count4 = ints[3];
                                this.timestamp = System.currentTimeMillis() + (long)(this.hideDurationSSetting.computeDouble() * 1000.0);
                                if (this.debugSetting.isEnabled()) {
                                    PrintStream out = System.out;
                                    String str = this.computeString(text);
                                    long l = (long)this.hideDurationSSetting.computeDouble();
                                    int n = this.count4;
                                    int n2 = this.count3;
                                    int n3 = this.count2;
                                    int count = this.count;
                                    long l2 = l;
                                    String str2 = str;
                                    out.println(
                                        "[SpireFix] Hiding blocks in "
                                            + str2
                                            + " for "
                                            + l2
                                            + "s (x:"
                                            + count
                                            + ".."
                                            + n3
                                            + ", z:"
                                            + n2
                                            + ".."
                                            + n
                                            + ")"
                                    );
                                }
                            }
                        }
                    } else if (System.currentTimeMillis() < this.timestamp) {
                        if (event2.getPacket() instanceof S23PacketBlockChange) {
                            S23PacketBlockChange packet2 = (S23PacketBlockChange)event2.getPacket();
                            if (this.matchesBlockPos(packet2.getBlockPosition())) {
                                this.queue.offer(packet2);
                                event2.cancel();
                            }
                        } else if (event2.getPacket() instanceof S22PacketMultiBlockChange) {
                            S22PacketMultiBlockChange packet3 = (S22PacketMultiBlockChange)event2.getPacket();
                            BlockUpdateData[] changedBlocks = packet3.getChangedBlocks();
                            if (this.matchesBlockUpdateDataArray(changedBlocks)) {
                                this.queue.offer(packet3);
                                event2.cancel();
                            }
                        } else if (event2.getPacket() instanceof S21PacketChunkData) {
                            S21PacketChunkData packet4 = (S21PacketChunkData)event2.getPacket();
                            if (this.matchesBlockPos(new BlockPos(packet4.getChunkX() << 4, 0, packet4.getChunkZ() << 4))) {
                                this.queue.offer(packet4);
                                event2.cancel();
                            }
                        }
                    }
                }
            }
        }
    }

    public static String computeString(StringSetting stringSetting) {
        String str = stringSetting == null ? null : stringSetting.getValue();
        return str != null && !str.trim().isEmpty()
            ? str
            : "obsidian, prismarine, planks, log, log2, wooden_slab, double_wooden_slab, oak_stairs, spruce_stairs, birch_stairs, jungle_stairs, dark_oak_stairs, acacia_stairs, fence, fence_gate, water, flowing_water, lava, flowing_lava, fire";
    }

    private boolean hideSpirePacket(EventPacketReceive event) {
        Packet packet = event.getPacket();
        if (!(packet instanceof S23PacketBlockChange) && !(packet instanceof S22PacketMultiBlockChange)) {
            return false;
        }
        var list = SizeHelper.computeList2(packet);
        if (list.isEmpty()) {
            return false;
        }
        event.cancel();
        if (packet instanceof S22PacketMultiBlockChange) {
            HashSet set = new HashSet(list);
            ArrayList<BlockUpdateData> blocks = new ArrayList<>();

            for (BlockUpdateData changedBlock : ((S22PacketMultiBlockChange)packet).getChangedBlocks()) {
                if (changedBlock != null && !set.contains(changedBlock.getPos())) {
                    blocks.add(changedBlock);
                }
            }

            if (!blocks.isEmpty()) {
                mc.addScheduledTask(() -> {
                    if (mc.theWorld != null) {
                        for (BlockUpdateData block : blocks) {
                            mc.theWorld.invalidateRegionAndSetBlock(block.getPos(), block.getBlockState());
                        }
                    }
                });
            }
        }

        if (this.debugSetting.isEnabled() && SizeHelper.size() % 500 == 0) {
            System.out.println("[SpireFix] Hidden spire blocks so far: " + SizeHelper.size());
        }

        return true;
    }

    private boolean matchesBlockPos(BlockPos pos) {
        return pos.getX() >= this.count && pos.getX() <= this.count2 && pos.getZ() >= this.count3 && pos.getZ() <= this.count4;
    }
}
