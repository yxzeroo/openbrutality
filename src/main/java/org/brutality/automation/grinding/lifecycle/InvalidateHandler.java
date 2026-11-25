package org.brutality.automation.grinding.lifecycle;

public final class InvalidateHandler {
    private Object object;
    private Object object2;
    private boolean dead;
    private boolean flag;
    private long timestamp;

    public InvalidateHandler.NoneMode computeNoneMode(Object object, Object object2, boolean dead, boolean flag) {
        InvalidateHandler.NoneMode noneMode = this.object != object || this.object2 != object2
            ? InvalidateHandler.NoneMode.CONTEXT
            : (
                dead && !this.dead
                    ? InvalidateHandler.NoneMode.DEATH
                    : (
                        !dead && this.dead
                            ? InvalidateHandler.NoneMode.RESPAWN
                            : (flag && !this.flag ? InvalidateHandler.NoneMode.ENTER_SPAWN : InvalidateHandler.NoneMode.NONE)
                    )
            );
        this.object = object;
        this.object2 = object2;
        this.dead = dead;
        this.flag = flag;
        if (noneMode != InvalidateHandler.NoneMode.NONE) {
            this.timestamp++;
        }

        return noneMode;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public boolean matchesLong(long l, Object obj, Object obj2) {
        return l == this.timestamp && this.object == obj && this.object2 == obj2 && obj != null && obj2 != null && !this.dead;
    }

    public void invalidate() {
        this.timestamp++;
        this.object = null;
        this.object2 = null;
        this.dead = false;
        this.flag = false;
    }

    public static enum NoneMode {
        NONE,
        CONTEXT,
        DEATH,
        RESPAWN,
        ENTER_SPAWN;

        private static InvalidateHandler.NoneMode[] computeNoneModeArray() {
            return new InvalidateHandler.NoneMode[]{NONE, CONTEXT, DEATH, RESPAWN, ENTER_SPAWN};
        }
    }
}
