package org.brutality.system;

import java.util.concurrent.TimeUnit;

public final class HeartbeatLeasePolicy {
    private static final long timestamp = TimeUnit.MINUTES.toNanos(10L);
    private long now;

    public HeartbeatLeasePolicy(long now) {
        this.now = now;
    }

    public boolean matchesAcceptedMode(HeartbeatLeasePolicy.AcceptedMode result, long now) {
        if (result == HeartbeatLeasePolicy.AcceptedMode.REVOKED) {
            return true;
        }
        if (result == HeartbeatLeasePolicy.AcceptedMode.ACCEPTED) {
            this.now = now;
            return false;
        }
        return now - this.now >= timestamp;
    }

    public static enum AcceptedMode {
        ACCEPTED,
        REVOKED,
        UNAVAILABLE;

        private static HeartbeatLeasePolicy.AcceptedMode[] computeAcceptedModeArray() {
            return new HeartbeatLeasePolicy.AcceptedMode[]{ACCEPTED, REVOKED, UNAVAILABLE};
        }
    }
}
