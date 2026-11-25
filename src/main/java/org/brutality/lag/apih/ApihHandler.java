package org.brutality.lag.apih;

import java.util.Set;
import org.brutality.lag.timeout.TimeoutHandler;

public final class ApihHandler {
    private final Set<InboundType> itemSet;
    private final TimeoutHandler timeoutHandler;

    public TimeoutHandler getTimeoutHandler() {
        return this.timeoutHandler;
    }

    public ApihHandler(Set<InboundType> itemSet, TimeoutHandler timeoutHandler) {
        this.itemSet = itemSet;
        this.timeoutHandler = timeoutHandler;
    }

    public Set<InboundType> getItemSet() {
        return this.itemSet;
    }
}
