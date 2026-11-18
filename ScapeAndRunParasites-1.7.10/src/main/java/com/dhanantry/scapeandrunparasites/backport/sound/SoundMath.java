package com.dhanantry.scapeandrunparasites.backport.sound;
public final class SoundMath {
    private SoundMath(){}
    public static int coordinate(double position){return (int)(position*8.0);}
    public static double coordinate(int encoded){return (float)encoded/8.0F;}
    public static float volume(float sound,float entry,float category){return clamp((sound*entry)*category,0,1);}
    public static float pitch(float sound,float entry){return clamp(sound*entry,0.5F,2);}
    private static float clamp(float x,float min,float max){return x<min?min:x>max?max:x;}
    public static int delay(double distanceSquared,boolean delayed){return delayed&&distanceSquared>100.0?(int)(Math.sqrt(distanceSquared)/40.0*20.0):0;}
    public static boolean inRange(double px,double py,double pz,double x,double y,double z,float volume){double dx=px-x,dy=py-y,dz=pz-z;double radius=volume>1?(double)(16.0F*volume):16;return dx*dx+dy*dy+dz*dz<radius*radius;}
}
