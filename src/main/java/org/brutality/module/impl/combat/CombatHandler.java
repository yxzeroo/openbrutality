package org.brutality.module.impl.combat;

public final class CombatHandler {
    private int count = Integer.MIN_VALUE;
    private boolean flag;

    public CombatHandler.NoneMode computeNoneMode(int count, boolean flag) {
        if (count == this.count) {
            return CombatHandler.NoneMode.NONE;
        }
        if (this.count != Integer.MIN_VALUE && count < this.count) {
            this.reset();
        }

        this.count = count;
        if (this.flag && !flag) {
            this.flag = false;
            return CombatHandler.NoneMode.ATTACK_BLOCK;
        }
        this.flag = true;
        return CombatHandler.NoneMode.RELEASE;
    }

    public void reset() {
        this.count = Integer.MIN_VALUE;
        this.flag = false;
    }

    public static enum NoneMode {
        NONE,
        RELEASE,
        ATTACK_BLOCK;

        private static CombatHandler.NoneMode[] computeNoneModeArray() {
            return new CombatHandler.NoneMode[]{NONE, RELEASE, ATTACK_BLOCK};
        }
    }
}
