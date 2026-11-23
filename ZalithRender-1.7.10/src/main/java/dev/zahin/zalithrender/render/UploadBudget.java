package dev.zahin.zalithrender.render;

/** Stops starting optional cache work once its per-frame allowance is consumed. */
public final class UploadBudget {
    private long spent, nanos;
    private int uploads, limit;
    public void reset(int limit, long nanos) { this.limit = limit; this.nanos = nanos; spent = 0; uploads = 0; }
    public boolean available() { return uploads < limit && (nanos <= 0 || spent < nanos); }
    public void charge(long elapsed) {
        if (elapsed > 0) spent = elapsed > Long.MAX_VALUE - spent ? Long.MAX_VALUE : spent + elapsed;
    }
    public void uploaded() { uploads++; }
}
