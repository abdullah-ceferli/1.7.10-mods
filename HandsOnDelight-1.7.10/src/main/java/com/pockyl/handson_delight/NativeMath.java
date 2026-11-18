package com.pockyl.handson_delight;

/** Math names used by the upstream cooking state machines. */
public final class NativeMath {
    public static final float PI=(float)Math.PI;
    public static final float RAD_TO_DEG=180F/(float)Math.PI, DEG_TO_RAD=(float)Math.PI/180F;
    public static int floor(float f){return (int)Math.floor(f);}
    public static float clamp(float f,float a,float b){return Math.max(a,Math.min(b,f));}
    public static double clamp(double f,double a,double b){return Math.max(a,Math.min(b,f));}
    public static float wrapDegrees(float f){f%=360;return f>=180?f-360:f< -180?f+360:f;}
    public static float lerp(float t,float a,float b){return a+t*(b-a);}
    public static float sin(float x){return (float)Math.sin(x);}
    public static float cos(float x){return (float)Math.cos(x);}
    private NativeMath(){}
}
