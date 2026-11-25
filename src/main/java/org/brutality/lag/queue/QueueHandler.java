package org.brutality.lag.queue;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.Packet;
import org.brutality.lag.apih.ApihHandler;
import org.brutality.lag.apih.InboundType;
import org.brutality.lag.handler.HandlerHandler;
import org.brutality.lag.queue.node.api.ApiHandler;
import org.brutality.lag.queue.node.impl.ImplHandler;
import org.brutality.lag.queue.node.impl.ImplHandler2;

public final class QueueHandler {
    private final QueueHandler.Entry entry;
    private final QueueHandler.Entry entry2;

    public boolean matchesPacket(Packet<?> packet, InboundType inboundType) {
        if (packet == null != (inboundType == null)) {
            throw new NullPointerException();
        }
        if (inboundType == null) {
            this.entry.matchesPacket(null, null);
            this.entry2.matchesPacket(null, null);
            return false;
        }
        switch (inboundType) {
            case INBOUND:
                return this.entry.matchesPacket(packet, inboundType);
            case OUTBOUND:
                return this.entry2.matchesPacket(packet, inboundType);
            default:
                return false;
        }
    }

    public void handleInboundType(InboundType inboundType, long l) {
        switch (inboundType) {
            case INBOUND:
                this.entry.handleLong(l);
                break;
            case OUTBOUND:
                this.entry2.handleLong(l);
        }
    }

    public QueueHandler(HandlerHandler handlerHandler) {
        this.entry = new QueueHandler.Entry(new ArrayList<>(), handlerHandler);
        this.entry2 = new QueueHandler.Entry(new ArrayList<>(), handlerHandler);
    }

    public void handleApihHandler(ApihHandler apihHandler) {
        for (InboundType inboundType : apihHandler.getItemSet()) {
            switch (inboundType) {
                case INBOUND:
                    this.entry.handleApihHandler(apihHandler);
                    break;
                case OUTBOUND:
                    this.entry2.handleApihHandler(apihHandler);
            }
        }
    }

    public void clear() {
        this.entry.clear();
        this.entry2.clear();
    }

    public static final class Entry {
        private final List<ApiHandler> entries;
        private final HandlerHandler handlerHandler;
        private ApihHandler apihHandler = null;

        public synchronized boolean matchesPacket(Packet<?> packet, InboundType inboundType) {
            if (!this.entries.isEmpty() || this.apihHandler != null && !this.apihHandler.getTimeoutHandler().checkState2()) {
                if (packet != null) {
                    this.entries.add(new ImplHandler2(packet, inboundType));
                }

                ApihHandler apihHandler = this.apihHandler;

                try {
                    while (apihHandler == null || apihHandler.getTimeoutHandler().checkState2()) {
                        ApiHandler apiHandler = this.computeApiHandler();
                        if (apiHandler == null) {
                            apihHandler = null;
                            break;
                        }

                        if (apiHandler instanceof ImplHandler2) {
                            ((ImplHandler2)apiHandler).handleHandlerHandler(this.handlerHandler);
                        } else if (apiHandler instanceof ImplHandler) {
                            apihHandler = ((ImplHandler)apiHandler).getApihHandler();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                this.apihHandler = apihHandler;
                return true;
            }
            this.apihHandler = null;
            return false;
        }

        public synchronized void clear() {
            this.entries.clear();
            this.apihHandler = null;
        }

        private ApiHandler computeApiHandler() {
            return this.entries.isEmpty() ? null : this.entries.remove(0);
        }

        public synchronized void handleLong(long l) {
            long l2 = System.currentTimeMillis() - l;
            ArrayList<ImplHandler2> implHandler2s = new ArrayList<>();

            for (ApiHandler apiHandler : new ArrayList<>(this.entries)) {
                if (apiHandler instanceof ImplHandler2 implHandler22 && implHandler22.getTimestamp() <= l2) {
                    implHandler2s.add(implHandler22);
                }
            }

            if (!implHandler2s.isEmpty()) {
                this.entries.removeAll(implHandler2s);

                for (ImplHandler2 implHandler2 : implHandler2s) {
                    implHandler2.handleHandlerHandler(this.handlerHandler);
                }
            }
        }

        public synchronized void handleApihHandler(ApihHandler apihHandler) {
            this.entries.add(new ImplHandler(apihHandler));
        }

        public Entry(List<ApiHandler> entries, HandlerHandler handlerHandler) {
            this.entries = entries;
            this.handlerHandler = handlerHandler;
        }
    }
}
