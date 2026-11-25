package org.brutality.automation.mining;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.util.UtilHelper4;
import org.brutality.util.StopwatchTimer;
import org.brutality.util.render.RenderHelper2;

public class MiningHandler {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final RenderHelper2 renderHelper2 = new RenderHelper2(1.0F, 0.0F, 0.0F, 0.35F);
    private static final RenderHelper2 renderHelper22 = new RenderHelper2(0.0F, 1.0F, 0.0F, 0.15F);
    private Vec3 vec3 = null;
    private final List<Vec3> entries = new ArrayList<>();
    private final StopwatchTimer utilHelper8 = new StopwatchTimer();
    private boolean enabled = true;
    private static final long timestamp = 5000L;

    public void onEventRender3D(EventRender3D event) {
        if (mc.thePlayer != null) {
            if (!this.utilHelper8.matchesLong(timestamp)) {
                if (this.enabled) {
                    if (this.vec3 != null) {
                        UtilHelper4.handleVec3(this.vec3, event.getFactor(), renderHelper2);
                    }

                    Iterator it = this.entries.iterator();
                    int n = 128;

                    while (it.hasNext() && n-- > 0) {
                        UtilHelper4.handleVec3((Vec3)it.next(), event.getFactor(), renderHelper22);
                    }
                }
            }
        }
    }

    public void handleBlockPos(BlockPos pos) {
        this.utilHelper8.reset();
        if (pos == null) {
            this.vec3 = null;
        } else {
            this.vec3 = new Vec3(pos);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void handleList(List<BlockPos> list) {
        this.utilHelper8.reset();
        if (list == null) {
            this.entries.clear();
        } else {
            this.entries.clear();
            Iterator it = list.iterator();

            while (it.hasNext()) {
                this.entries.add(new Vec3((Vec3i)it.next()));
            }
        }
    }
}
