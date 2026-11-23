package dev.zahin.zalithrender.render;

import java.util.concurrent.locks.LockSupport;
import java.util.function.LongSupplier;

/** Sleeps after drawing and before display update. Late frames never accumulate a timing debt. */
public final class FramePacer {
    public interface Waiter { void park(long nanos); }
    private final LongSupplier clock;
    private final Waiter waiter;
    private boolean active, scheduled;
    private long period, deadline, lastEnd;

    public FramePacer() { this(System::nanoTime, LockSupport::parkNanos); }
    public FramePacer(LongSupplier clock, Waiter waiter) { this.clock = clock; this.waiter = waiter; }

    public void begin(int fps) {
        if (fps <= 0) { reset(); return; }
        long now = clock.getAsLong(), nextPeriod = 1000000000L / fps;
        if (!scheduled || period != nextPeriod || now - lastEnd > 250000000L || now - lastEnd < 0) {
            deadline = now + nextPeriod;
        }
        period = nextPeriod;
        active = true;
    }

    public void end() {
        if (!active) return;
        long now = clock.getAsLong();
        boolean renderWasOnTime = deadline - now > 0;
        // Bound spurious-wakeup retries; never busy-spin if a launcher parks incorrectly.
        for (int retries = 0; deadline - now > 0 && retries < 32; retries++) {
            if (Thread.currentThread().isInterrupted()) { reset(); return; }
            waiter.park(deadline - now);
            now = clock.getAsLong();
        }
        lastEnd = now;
        // Compensate small sleep overshoots, but never catch up after expensive render work.
        long overshoot = now - deadline;
        deadline = renderWasOnTime && overshoot >= 0 && overshoot < period / 2 ? deadline + period : now + period;
        scheduled = true;
    }

    public boolean active() { return active; }
    public void reset() { active = scheduled = false; }

    public static int target(boolean enabled, boolean world, boolean skip, boolean vsync, int nativeLimit, int target) {
        return enabled && world && !skip && !vsync && target > 0 && nativeLimit >= target ? target : 0;
    }
}
