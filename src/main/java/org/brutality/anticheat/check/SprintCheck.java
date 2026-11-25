package org.brutality.anticheat.check;

import net.minecraft.entity.player.EntityPlayer;
import org.brutality.anticheat.data.DataHandler;

public class SprintCheck extends Check {
    private static final long timestamp = 1721799938825781260L;

    public SprintCheck() {
        super("Sprint", "Detects omnisprint and invalid sprinting");
        this.count = (int)timestamp;
    }

    @Override
    public void onUpdate(EntityPlayer player) {
        if (player != mc.thePlayer) {
            DataHandler dataHandler = this.computeDataHandler(player);
            if (dataHandler != null) {
                if (player.isSprinting()) {
                    double d = dataHandler.computeDouble2();
                    double d2 = dataHandler.computeDouble3();
                    if (!(Math.abs(d) < 1.0E-8) || !(Math.abs(d2) < 1.0E-8)) {
                        float yaw = dataHandler.yaw;
                        float f = this.computeFloat(d, d2, yaw);
                        if (Math.abs(f) > 45.0F) {
                            double d3 = Math.abs(f) / 180.0;
                            double d4 = 1.0 + d3 * 2.0;
                            this.handleEntityPlayer(player, d4, String.format("omnisprint | angle=%.1f° | expected=forward", f));
                        } else if (this.computeDouble(player) > 0.0) {
                            this.handleEntityPlayer(player, 0.5);
                        }
                    }
                }
            }
        }
    }
}
