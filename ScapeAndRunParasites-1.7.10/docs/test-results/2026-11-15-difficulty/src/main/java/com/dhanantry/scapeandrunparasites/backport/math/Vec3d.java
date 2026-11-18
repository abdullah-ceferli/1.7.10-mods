package com.dhanantry.scapeandrunparasites.backport.math;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
/** Immutable vectors retaining original Minecraft float-sqrt and trig-table behavior. */
public final class Vec3d {
    public static final Vec3d ZERO=new Vec3d(0,0,0);
    public final double xCoord,yCoord,zCoord;
    public Vec3d(double x,double y,double z){xCoord=x==0?0:x;yCoord=y==0?0:y;zCoord=z==0?0:z;}
    public Vec3d(Vec3i p){this(p.getX(),p.getY(),p.getZ());}
    public Vec3d subtract(Vec3d v){return subtract(v.xCoord,v.yCoord,v.zCoord);}public Vec3d subtract(double x,double y,double z){return addVector(-x,-y,-z);}
    public Vec3d add(Vec3d v){return addVector(v.xCoord,v.yCoord,v.zCoord);}public Vec3d addVector(double x,double y,double z){return new Vec3d(xCoord+x,yCoord+y,zCoord+z);}
    public Vec3d scale(double n){return new Vec3d(xCoord*n,yCoord*n,zCoord*n);}
    public double lengthSquared(){return xCoord*xCoord+yCoord*yCoord+zCoord*zCoord;}
    public double lengthVector(){return MathHelper.sqrt_double(lengthSquared());}
    public Vec3d normalize(){double length=lengthVector();return length<1.0e-4?new Vec3d(0,0,0):new Vec3d(xCoord/length,yCoord/length,zCoord/length);}
    public double dotProduct(Vec3d v){return xCoord*v.xCoord+yCoord*v.yCoord+zCoord*v.zCoord;}
    public Vec3d crossProduct(Vec3d v){return new Vec3d(yCoord*v.zCoord-zCoord*v.yCoord,zCoord*v.xCoord-xCoord*v.zCoord,xCoord*v.yCoord-yCoord*v.xCoord);}
    public double squareDistanceTo(Vec3d v){double x=xCoord-v.xCoord,y=yCoord-v.yCoord,z=zCoord-v.zCoord;return x*x+y*y+z*z;}
    public double distanceTo(Vec3d v){return MathHelper.sqrt_double(squareDistanceTo(v));}
    public Vec3d rotatePitch(float angle){float c=MathHelper.cos(angle),s=MathHelper.sin(angle);return new Vec3d(xCoord,yCoord*c+zCoord*s,zCoord*c-yCoord*s);}
    public Vec3d rotateYaw(float angle){float c=MathHelper.cos(angle),s=MathHelper.sin(angle);return new Vec3d(xCoord*c+zCoord*s,yCoord,zCoord*c-xCoord*s);}
    public Vec3 toLegacy(){return Vec3.createVectorHelper(xCoord,yCoord,zCoord);}public static Vec3d fromLegacy(Vec3 v){return new Vec3d(v.xCoord,v.yCoord,v.zCoord);}
    @Override public boolean equals(Object other){if(this==other)return true;if(!(other instanceof Vec3d))return false;Vec3d v=(Vec3d)other;return Double.compare(xCoord,v.xCoord)==0&&Double.compare(yCoord,v.yCoord)==0&&Double.compare(zCoord,v.zCoord)==0;}
    @Override public int hashCode(){long x=Double.doubleToLongBits(xCoord),y=Double.doubleToLongBits(yCoord),z=Double.doubleToLongBits(zCoord);int h=(int)(x^(x>>>32));h=31*h+(int)(y^(y>>>32));return 31*h+(int)(z^(z>>>32));}
    @Override public String toString(){return "("+xCoord+", "+yCoord+", "+zCoord+")";}
}
