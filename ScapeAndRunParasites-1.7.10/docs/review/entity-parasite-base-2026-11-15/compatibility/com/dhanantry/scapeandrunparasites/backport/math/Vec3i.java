package com.dhanantry.scapeandrunparasites.backport.math;
import net.minecraft.util.MathHelper;
/** Integer coordinates with the original y/z/x ordering and distance semantics. */
public class Vec3i implements Comparable<Vec3i> {
    public static final Vec3i NULL_VECTOR = new Vec3i(0, 0, 0);
    protected int x, y, z;
    public Vec3i(int x, int y, int z) { this.x=x; this.y=y; this.z=z; }
    public Vec3i(double x, double y, double z) { this(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z)); }
    public int getX() { return x; } public int getY() { return y; } public int getZ() { return z; }
    @Override public boolean equals(Object other) { if(this==other)return true; if(!(other instanceof Vec3i))return false;Vec3i p=(Vec3i)other;return getX()==p.getX()&&getY()==p.getY()&&getZ()==p.getZ(); }
    @Override public int hashCode() { return (getY()+getZ()*31)*31+getX(); }
    public int compareTo(Vec3i p) { return getY()!=p.getY()?getY()-p.getY():getZ()!=p.getZ()?getZ()-p.getZ():getX()-p.getX(); }
    public Vec3i crossProduct(Vec3i p) { return new Vec3i(getY()*p.getZ()-getZ()*p.getY(),getZ()*p.getX()-getX()*p.getZ(),getX()*p.getY()-getY()*p.getX()); }
    public double distanceSq(double x, double y, double z) { double dx=getX()-x,dy=getY()-y,dz=getZ()-z;return dx*dx+dy*dy+dz*dz; }
    public double distanceSq(Vec3i p) { return distanceSq(p.getX(),p.getY(),p.getZ()); }
    public double distanceSqToCenter(double x, double y, double z) { double dx=getX()+0.5-x,dy=getY()+0.5-y,dz=getZ()+0.5-z;return dx*dx+dy*dy+dz*dz; }
    public double getDistance(int x,int y,int z) { return MathHelper.sqrt_double(distanceSq(x,y,z)); }
    @Override public String toString() { return "Vec3i{x="+getX()+", y="+getY()+", z="+getZ()+"}"; }
}
