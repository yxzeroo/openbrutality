package org.brutality.lag.queue.node.impl;

import org.brutality.lag.apih.ApihHandler;
import org.brutality.lag.queue.node.api.ApiHandler;

public final class ImplHandler extends ApiHandler {
    private final ApihHandler apihHandler;

    public ImplHandler(ApihHandler apihHandler) {
        this.apihHandler = apihHandler;
    }

    public ApihHandler getApihHandler() {
        return this.apihHandler;
    }
}
