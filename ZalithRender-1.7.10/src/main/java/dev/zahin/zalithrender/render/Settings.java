package dev.zahin.zalithrender.render;

public final class Settings {
    public static boolean enabled = true;
    public static boolean debugOverlay = true;
    public static int cacheBytes = 32 * 1024 * 1024;
    public static int maxMeshBytes = 2 * 1024 * 1024;
    public static int maxMeshes = 4096;
    public static int uploadsPerFrame = 8;
    public static long uploadBudgetNanos = 2000000L;
    public static boolean framePacing = true;
    public static int targetFps = 60;
    private Settings() {}
}
