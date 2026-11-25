package org.brutality.module.impl.combat;

public final class CombatHandler2 {
    private static final int count = 1;
    private static final int count2 = 1;
    private int count3 = Integer.MIN_VALUE;
    private int count4;
    private int count5;
    private Boolean booleanValue;

    public void reset() {
        this.count3 = Integer.MIN_VALUE;
        this.booleanValue = null;
        this.count5 = 0;
    }

    public void process() {
        this.count5 = Math.max(0, this.count5 - 20);
    }

    public CombatHandler2.NoneMode computeNoneMode(int n, boolean booleanValue, boolean flag) {
        if (n == this.count3) {
            return CombatHandler2.NoneMode.NONE;
        }
        if (n < this.count3) {
            this.reset();
        }

        if (this.count3 != Integer.MIN_VALUE) {
            this.count5 = Math.min(27, this.count5 + 8);
        }

        this.count3 = n;
        if (this.booleanValue == null || this.booleanValue != booleanValue) {
            this.booleanValue = booleanValue;
            this.count4 = n - 1;
        }

        int n2 = n - this.count4;
        if (booleanValue) {
            return flag && n2 >= 1 && this.count5 + 8 >= 20 ? CombatHandler2.NoneMode.RELEASE : CombatHandler2.NoneMode.NONE;
        }
        if (n2 < 1) {
            return CombatHandler2.NoneMode.NONE;
        }
        if (flag && this.count5 >= 20) {
            return CombatHandler2.NoneMode.ATTACK_BLOCK;
        }
        return !flag && n2 > 1 ? CombatHandler2.NoneMode.REBLOCK : CombatHandler2.NoneMode.NONE;
    }

    public static enum NoneMode {
        NONE,
        RELEASE,
        ATTACK_BLOCK,
        REBLOCK;

        private static CombatHandler2.NoneMode[] computeNoneModeArray() {
            return new CombatHandler2.NoneMode[]{NONE, RELEASE, ATTACK_BLOCK, REBLOCK};
        }
    }
}
