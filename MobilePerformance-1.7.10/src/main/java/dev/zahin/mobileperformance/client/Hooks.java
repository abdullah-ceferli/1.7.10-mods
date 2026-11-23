package dev.zahin.mobileperformance.client;

/** Referenced by transformed vanilla methods; installation is client-side only. */
public final class Hooks {
    public static volatile ClientRuntime runtime;
    public static long entityCalls,tileCalls,particleCalls,animationCalls;
    public static boolean entity(Object value){entityCalls++;ClientRuntime r=runtime;return r==null||r.entity(value);}
    public static boolean tile(Object value){tileCalls++;ClientRuntime r=runtime;return r==null||r.tile(value);}
    public static boolean particle(Object value){particleCalls++;ClientRuntime r=runtime;return r==null||r.particle(value);}
    public static boolean animation(){animationCalls++;ClientRuntime r=runtime;return r==null||r.animation();}
    private Hooks(){}
}
