package com.dhanantry.scapeandrunparasites.backport.particle;
/** Restrict the native factory bypass to calls already filtered by the modern dispatcher. */
public final class ParticleRenderContext {
    private static final ThreadLocal<Boolean> ACTIVE=new ThreadLocal<Boolean>();
    private ParticleRenderContext(){}
    public static boolean enter(){boolean previous=Boolean.TRUE.equals(ACTIVE.get());ACTIVE.set(Boolean.TRUE);return previous;}
    public static void leave(boolean previous){if(previous)ACTIVE.set(Boolean.TRUE);else ACTIVE.remove();}
    public static int setting(int nativeValue){return Boolean.TRUE.equals(ACTIVE.get())?0:nativeValue;}
    public static double range(double nativeValue){return Boolean.TRUE.equals(ACTIVE.get())?Double.POSITIVE_INFINITY:nativeValue;}
}
