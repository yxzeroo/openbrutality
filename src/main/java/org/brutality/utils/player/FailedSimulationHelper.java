package org.brutality.utils.player;

import java.util.ArrayDeque;
import java.util.List;
import net.minecraft.util.StringUtils;

public final class FailedSimulationHelper {
    private final ArrayDeque<FailedSimulationHelper.IWEntry> arrayDeque = new ArrayDeque<>();
    private final ArrayDeque<FailedSimulationHelper.IUEntry> arrayDeque2 = new ArrayDeque<>();
    private long timestamp = -1L;

    public static boolean matchesString(String str, String str2) {
        return str != null && str2 != null && !str2.isEmpty() && StringUtils.stripControlCodes(str).startsWith("Grim > " + str2 + " failed Simulation ");
    }

    public synchronized FailedSimulationHelper.IXEntry computeIXEntry(long l) {
        this.handleLong(l);
        return new FailedSimulationHelper.IXEntry(l, List.copyOf(this.arrayDeque), List.copyOf(this.arrayDeque2));
    }

    public synchronized FailedSimulationHelper.IVEntry computeIVEntry(
        long timestamp, boolean flag, double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8, long l
    ) {
        this.handleLong(timestamp);
        if (flag && this.arrayDeque.size() >= 2 && timestamp - this.arrayDeque.peekLast().iW() <= 250L && (this.timestamp < 0L || timestamp - this.timestamp >= l)) {
            double d9 = d4 - d;
            double d10 = d5 - d2;
            double d11 = d6 - d3;
            double dist = Math.sqrt(d9 * d9 + d10 * d10 + d11 * d11);
            if (Double.isFinite(dist) && !(dist < d7) && !(dist > d8)) {
                FailedSimulationHelper.IWEntry iwEntry = this.arrayDeque.peekFirst();

                for (FailedSimulationHelper.IWEntry iwEntry2 : this.arrayDeque) {
                    if (timestamp - iwEntry2.iW() < 250L) {
                        break;
                    }

                    iwEntry = iwEntry2;
                }

                boolean flag2 = d9 * (d - iwEntry.i()) + d10 * (d2 - iwEntry.j()) + d11 * (d3 - iwEntry.k()) < -0.001;
                this.timestamp = timestamp;
                return new FailedSimulationHelper.IVEntry(timestamp, d9, d10, d11, dist, flag2, List.copyOf(this.arrayDeque), List.copyOf(this.arrayDeque2));
            }
            return null;
        }
        return null;
    }

    private void handleLong(long l) {
        while (!this.arrayDeque.isEmpty() && l - this.arrayDeque.peekFirst().iW() > 2000L) {
            this.arrayDeque.removeFirst();
        }

        while (!this.arrayDeque2.isEmpty() && l - this.arrayDeque2.peekFirst().iU() > 2000L) {
            this.arrayDeque2.removeFirst();
        }
    }

    public synchronized void handleLong(long l, String str) {
        this.arrayDeque2.addLast(new FailedSimulationHelper.IUEntry(l, str));

        while (this.arrayDeque2.size() > 80) {
            this.arrayDeque2.removeFirst();
        }

        this.handleLong(l);
    }

    public synchronized void handleLong(long l, double d, double d2, double d3, String str) {
        this.arrayDeque.addLast(new FailedSimulationHelper.IWEntry(l, d, d2, d3, str));

        while (this.arrayDeque.size() > 40) {
            this.arrayDeque.removeFirst();
        }

        this.handleLong(l);
    }

    public synchronized void reset() {
        this.arrayDeque.clear();
        this.arrayDeque2.clear();
        this.timestamp = -1L;
    }

    public record IUEntry(long iU, String iN) {
    }

    public record IVEntry(
        long iV, double e, double f, double g2, double h, boolean iJ, List<FailedSimulationHelper.IWEntry> O, List<FailedSimulationHelper.IUEntry> P
    ) {
        public double g() {
            return this.g2;
        }
    }

    public record IWEntry(long iW, double i, double j, double k, String iO) {
    }

    public record IXEntry(long iX, List<FailedSimulationHelper.IWEntry> Q, List<FailedSimulationHelper.IUEntry> R) {
    }
}
