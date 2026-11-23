package dev.zahin.zalithrender.render;

import java.util.Arrays;

/** Counts completed display-update intervals, independently of the requested FPS. */
public final class FrameStats {
    private final long[] intervals = new long[120];
    private long previous;
    private int count, cursor;
    private boolean started;

    public void completed(long now) {
        if (started) {
            long elapsed = now - previous;
            if (elapsed > 0 && elapsed <= 1000000000L) {
                intervals[cursor] = elapsed;
                cursor = (cursor + 1) % intervals.length;
                count = Math.min(count + 1, intervals.length);
            } else { count = cursor = 0; }
        }
        previous = now;
        started = true;
    }
    public double fps() {
        long total = 0;
        for (int i = 0; i < count; i++) total += intervals[i];
        return total == 0 ? 0 : count * 1000000000.0 / total;
    }
    public double p95Millis() {
        if (count == 0) return 0;
        long[] ordered = Arrays.copyOf(intervals, count);
        Arrays.sort(ordered);
        return ordered[(count * 95 + 99) / 100 - 1] / 1000000.0;
    }
    public void reset() { count = cursor = 0; started = false; }
}
