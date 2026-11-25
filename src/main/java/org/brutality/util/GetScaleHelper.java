package org.brutality.util;

import net.minecraft.entity.boss.BossStatus;

public final class GetScaleHelper {
    public static float getScale() {
        return BossStatus.healthScale;
    }

    public static int getValue() {
        return BossStatus.statusBarTime;
    }

    public static String computeString(boolean flag) {
        String bossName = BossStatus.bossName;
        if (bossName == null) {
            return null;
        }
        if (BossStatus.statusBarTime <= 0) {
            return null;
        }
        return flag ? ExecuteCommandHelper.computeString6(bossName) : bossName;
    }

    private GetScaleHelper() {
    }
}
