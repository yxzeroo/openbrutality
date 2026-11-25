package org.brutality.module.impl.pit.hypixel.automation;

import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.util.StopwatchTimer;
import org.brutality.utils.render.ResetColorHelper;

public class AutomationModule extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private static final int count = 12;
    private final StopwatchTimer utilHelper8 = new StopwatchTimer();
    private final ArrayList<BlockPos> entries = new ArrayList<>();
    private static final String text = "AutoSewer";
    private static final long timestamp = 1000L;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (event instanceof EventRender3D) {
                    if (this.utilHelper8.matchesLong(timestamp)) {
                        this.process();
                        this.utilHelper8.reset();
                    }

                    for (BlockPos pos : this.entries) {
                        ResetColorHelper.handleBlockPos2(pos, new Color(255, 180, 60, 200), true, false, 1.5F);
                    }
                }
            }
        }
    }

    private void process() {
        this.entries.clear();
        int x = (int)mc.thePlayer.posX;
        int y = (int)mc.thePlayer.posY;
        int z = (int)mc.thePlayer.posZ;

        for (int i = x - 12; i <= x + 12; i++) {
            for (int j = y - 12; j <= y + 12; j++) {
                for (int k = z - 12; k <= z + 12; k++) {
                    BlockPos pos = new BlockPos(i, j, k);
                    if (mc.theWorld.isBlockLoaded(pos)) {
                        Block block = mc.theWorld.getBlockState(pos).getBlock();
                        if (block == Blocks.chest || block == Blocks.trapped_chest) {
                            this.entries.add(pos);
                        }
                    }
                }
            }
        }
    }

    public AutomationModule() {
        super(text, 0, Category.PIT);
    }
}
