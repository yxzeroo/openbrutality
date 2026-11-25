package org.brutality.util;

import java.util.ArrayDeque;
import java.util.Deque;

public class UtilHelper7 {
    private final Deque<Long> deque = new ArrayDeque<>();
    private static final long timestamp = 1000L;

    public int computeInt(int n) {
        long now = System.currentTimeMillis();
        long l = n * timestamp;
        long l2 = now - l;

        while (!this.deque.isEmpty() && (this.deque.peekFirst() == l2 ? 0 : (this.deque.peekFirst() < l2 ? -1 : 1)) < 0) {
            this.deque.removeFirst();
        }

        return this.deque.size();
    }

    public int computeInt(long l) {
        long now = System.currentTimeMillis();
        long l2 = now - l;

        while (!this.deque.isEmpty() && (this.deque.peekFirst() == l2 ? 0 : (this.deque.peekFirst() < l2 ? -1 : 1)) < 0) {
            this.deque.removeFirst();
        }

        return this.deque.size();
    }

    public void process() {
        long now = System.currentTimeMillis();
        this.deque.addLast(now);
    }
}
