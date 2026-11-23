package dev.zahin.zalithrender.render;

import dev.zahin.zalithrender.core.FrameAccess;
import org.lwjgl.opengl.Display;

/** Keeps the native tick, input, sound, render, display update and FPS count for every loop. */
public final class FrameHooks {
    private static final FramePacer PACER = new FramePacer();
    private static final FrameStats STATS = new FrameStats();
    private static boolean measuring;
    private static int target;
    private static long statusAt;
    private static String status = "ZalithRender pacing: waiting for a world";

    private FrameHooks() {}
    public static void begin(Object client) {
        target = 0;
        boolean world = false;
        if (client instanceof FrameAccess) {
            FrameAccess access = (FrameAccess) client;
            world = access.zr$world() != null && !access.zr$skip();
            target = FramePacer.target(Settings.enabled && Settings.framePacing && RenderHooks.pacingAllowed(),
                world, access.zr$skip(), access.zr$vsync(), access.zr$limit(), Settings.targetFps);
        }
        if (measuring != world) STATS.reset();
        measuring = world;
        PACER.begin(target);
    }
    public static void vanillaSync(int fps) {
        if (!PACER.active()) Display.sync(fps);
    }
    public static void present() { PACER.end(); }
    public static void end() {
        long now = System.nanoTime();
        if (measuring) STATS.completed(now);
        if (now - statusAt >= 1000000000L || statusAt == 0) {
            status = String.format(java.util.Locale.ROOT, "ZalithRender pacing: %s; %.1f FPS; p95 %.1f ms",
                target > 0 ? "target " + target : "native limiter / VSync", STATS.fps(), STATS.p95Millis());
            statusAt = now;
        }
    }
    public static String status() { return status; }
}
