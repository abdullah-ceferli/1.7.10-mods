package dev.zahin.zalithrender.test;

import dev.zahin.zalithrender.render.FramePacer;
import dev.zahin.zalithrender.render.FrameStats;
import dev.zahin.zalithrender.render.UploadBudget;
import java.util.function.LongSupplier;

public final class FrameChecks {
    private static final class Clock implements LongSupplier, FramePacer.Waiter {
        long now = 1000, parked;
        int calls;
        public long getAsLong() { return now; }
        public void park(long nanos) { Checks.check(nanos > 0, "Pacer requested a nonpositive wait"); now += nanos; parked += nanos; calls++; }
    }
    public static void run() {
        for (int fps : new int[]{60, 70}) {
            Clock clock = new Clock(); FramePacer pacer = new FramePacer(clock, clock); long previous = clock.now;
            for (int frame = 0; frame < 240; frame++) {
                pacer.begin(fps); clock.now += 4000000L + (frame % 7) * 1000000L; pacer.end();
                Checks.check(clock.now - previous == 1000000000L / fps, "Frame cadence changed with varying render cost"); previous = clock.now;
            }
        }
        Clock clock = new Clock(); FramePacer pacer = new FramePacer(clock, clock);
        for (int i = 0; i < 20; i++) { pacer.begin(60); clock.now += 66666667L; pacer.end(); }
        Checks.check(clock.calls == 0, "A 15 FPS workload must never be delayed to chase 60 FPS");
        pacer.begin(60); clock.now += 5000000L; pacer.end();
        Checks.check(clock.calls == 1, "Pacing must resume without a catch-up burst after a slow frame");
        pacer.begin(70); long start = clock.now; clock.now += 1000000L; pacer.end();
        Checks.check(clock.now - start == 1000000000L / 70, "Changing targets retained an old deadline");
        pacer.begin(0); int calls = clock.calls; pacer.end();
        Checks.check(!pacer.active() && clock.calls == calls, "Disabled pacing must not park");
        clock.now += 500000000L; pacer.begin(60); start = clock.now; pacer.end();
        Checks.check(clock.now - start == 1000000000L / 60, "Resume after suspension retained stale timing");
        clock.now -= 1000000000L; pacer.begin(60); start = clock.now; pacer.end();
        Checks.check(clock.now - start == 1000000000L / 60, "Clock discontinuity retained stale timing");
        Clock wrap = new Clock(); wrap.now = Long.MAX_VALUE - 1000000L;
        FramePacer wrapping = new FramePacer(wrap, wrap); start = wrap.now; wrapping.begin(60); wrapping.end();
        Checks.check(wrap.now - start == 1000000000L / 60, "nanoTime wrap broke a frame deadline");
        Clock oversleep = new Clock();
        FramePacer oversleepPacer = new FramePacer(oversleep, nanos -> { oversleep.now += nanos + 1000000L; });
        start = oversleep.now;
        for (int i = 0; i < 120; i++) { oversleepPacer.begin(60); oversleep.now += 5000000L; oversleepPacer.end(); }
        Checks.check(oversleep.now - start == 120L * (1000000000L / 60) + 1000000L, "Sleep overshoot accumulated into a lower frame rate");
        final int[] retries = {0};
        FramePacer spurious = new FramePacer(() -> 1L, nanos -> retries[0]++); spurious.begin(60); spurious.end();
        Checks.check(retries[0] == 32, "Broken park implementations can busy-spin indefinitely");
        Clock interrupted = new Clock(); FramePacer interruptible = new FramePacer(interrupted, interrupted); interruptible.begin(60);
        Thread.currentThread().interrupt();
        try { interruptible.end(); Checks.check(interrupted.calls == 0 && Thread.currentThread().isInterrupted(), "Pacing swallowed interruption"); }
        finally { Thread.interrupted(); }
        Checks.check(FramePacer.target(true, true, false, false, 260, 60) == 60, "Unlimited mode was not paced");
        Checks.check(FramePacer.target(true, true, false, false, 60, 60) == 60, "Equal native cap must use a single pacer");
        Checks.check(FramePacer.target(true, true, false, false, 30, 60) == 0, "Lower native cap was overridden");
        Checks.check(FramePacer.target(true, true, false, true, 260, 60) == 0, "VSync was double-capped");
        Checks.check(FramePacer.target(true, false, false, false, 260, 60) == 0, "Menus were paced as gameplay");
        Checks.check(FramePacer.target(true, true, true, false, 260, 60) == 0, "Skipped world renders were paced as frames");
        Checks.check(FramePacer.target(false, true, false, false, 260, 60) == 0, "Disabled setting still paced");
        UploadBudget budget = new UploadBudget(); budget.reset(8, 2000000L);
        Checks.check(budget.available(), "A new frame had no cache allowance"); budget.charge(1999999L);
        Checks.check(budget.available(), "Budget stopped below its allowance"); budget.charge(1);
        Checks.check(!budget.available(), "Time-exhausted cache work was allowed");
        budget.reset(1, 0); budget.charge(Long.MAX_VALUE); Checks.check(budget.available(), "Disabled time budget still applied");
        budget.uploaded(); Checks.check(!budget.available(), "Upload-count bound was lost");
        budget.reset(8, 2000000L); budget.charge(Long.MAX_VALUE); budget.charge(1);
        Checks.check(!budget.available(), "An elapsed-time overflow reopened the cache budget");
        budget.reset(8, 2000000L); budget.charge(-1); Checks.check(budget.available(), "Clock discontinuity exhausted an untouched budget");
        FrameStats stats = new FrameStats(); stats.completed(1);
        for (int i = 1; i <= 120; i++) stats.completed(1L + i * 50000000L);
        Checks.check(Math.abs(stats.fps() - 20) < 0.001 && stats.p95Millis() == 50, "Statistics reported the target instead of actual completed-frame intervals");
        stats.completed(10000000000L); Checks.check(stats.fps() == 0, "Pause gap contaminated frame samples");
        stats.reset(); stats.completed(1);
        for (int i = 1; i <= 20; i++) stats.completed(i * 10000000L + 1);
        stats.completed(210000001L + 100000000L);
        Checks.check(stats.p95Millis() == 10, "p95 was not computed from observed intervals");
    }
}
