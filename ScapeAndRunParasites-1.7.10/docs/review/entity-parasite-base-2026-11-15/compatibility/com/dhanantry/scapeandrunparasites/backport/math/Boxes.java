package com.dhanantry.scapeandrunparasites.backport.math;
import net.minecraft.entity.Entity;import net.minecraft.util.AxisAlignedBB;
/** Immutable modern operations at the legacy engine boundary. Native physics retains its mutable box API. */
public final class Boxes {
    private Boxes(){}
    public static AxisAlignedBB create(double x1,double y1,double z1,double x2,double y2,double z2){return AxisAlignedBB.getBoundingBox(Math.min(x1,x2),Math.min(y1,y2),Math.min(z1,z2),Math.max(x1,x2),Math.max(y1,y2),Math.max(z1,z2));}
    public static AxisAlignedBB create(BlockPos pos){return create(pos.getX(),pos.getY(),pos.getZ(),pos.getX()+1,pos.getY()+1,pos.getZ()+1);}
    public static AxisAlignedBB create(BlockPos a,BlockPos b){return create(a.getX(),a.getY(),a.getZ(),b.getX(),b.getY(),b.getZ());}
    public static AxisAlignedBB create(Vec3d a,Vec3d b){return create(a.xCoord,a.yCoord,a.zCoord,b.xCoord,b.yCoord,b.zCoord);}
    public static AxisAlignedBB entityBox(Entity entity){return entity.boundingBox.copy();}
    public static void setEntityBox(Entity entity,AxisAlignedBB box){entity.boundingBox.setBB(box);}
    public static AxisAlignedBB grow(AxisAlignedBB b,double value){return grow(b,value,value,value);}
    public static AxisAlignedBB grow(AxisAlignedBB b,double x,double y,double z){return create(b.minX-x,b.minY-y,b.minZ-z,b.maxX+x,b.maxY+y,b.maxZ+z);}
    public static AxisAlignedBB shrink(AxisAlignedBB b,double value){return grow(b,-value);}
    public static AxisAlignedBB expand(AxisAlignedBB b,double x,double y,double z){return create(x<0?b.minX+x:b.minX,y<0?b.minY+y:b.minY,z<0?b.minZ+z:b.minZ,x>0?b.maxX+x:b.maxX,y>0?b.maxY+y:b.maxY,z>0?b.maxZ+z:b.maxZ);}
    public static AxisAlignedBB contract(AxisAlignedBB b,double x,double y,double z){return create(x<0?b.minX-x:b.minX,y<0?b.minY-y:b.minY,z<0?b.minZ-z:b.minZ,x>0?b.maxX-x:b.maxX,y>0?b.maxY-y:b.maxY,z>0?b.maxZ-z:b.maxZ);}
    public static AxisAlignedBB offset(AxisAlignedBB b,double x,double y,double z){return create(b.minX+x,b.minY+y,b.minZ+z,b.maxX+x,b.maxY+y,b.maxZ+z);}
    public static AxisAlignedBB offset(AxisAlignedBB b,BlockPos pos){return offset(b,pos.getX(),pos.getY(),pos.getZ());}
    public static AxisAlignedBB offset(AxisAlignedBB b,Vec3d pos){return offset(b,pos.xCoord,pos.yCoord,pos.zCoord);}
    public static AxisAlignedBB union(AxisAlignedBB a,AxisAlignedBB b){return create(Math.min(a.minX,b.minX),Math.min(a.minY,b.minY),Math.min(a.minZ,b.minZ),Math.max(a.maxX,b.maxX),Math.max(a.maxY,b.maxY),Math.max(a.maxZ,b.maxZ));}
    public static AxisAlignedBB intersect(AxisAlignedBB a,AxisAlignedBB b){return create(Math.max(a.minX,b.minX),Math.max(a.minY,b.minY),Math.max(a.minZ,b.minZ),Math.min(a.maxX,b.maxX),Math.min(a.maxY,b.maxY),Math.min(a.maxZ,b.maxZ));}
    public static boolean intersects(AxisAlignedBB a,AxisAlignedBB b){return intersects(a,b.minX,b.minY,b.minZ,b.maxX,b.maxY,b.maxZ);}
    public static boolean intersects(AxisAlignedBB a,double minX,double minY,double minZ,double maxX,double maxY,double maxZ){return a.minX<maxX&&a.maxX>minX&&a.minY<maxY&&a.maxY>minY&&a.minZ<maxZ&&a.maxZ>minZ;}
    public static boolean contains(AxisAlignedBB b,Vec3d v){return v.xCoord>b.minX&&v.xCoord<b.maxX&&v.yCoord>b.minY&&v.yCoord<b.maxY&&v.zCoord>b.minZ&&v.zCoord<b.maxZ;}
    public static Vec3d center(AxisAlignedBB b){return new Vec3d(b.minX+(b.maxX-b.minX)*0.5,b.minY+(b.maxY-b.minY)*0.5,b.minZ+(b.maxZ-b.minZ)*0.5);}
    public static AxisAlignedBB setMaxY(AxisAlignedBB b,double y){return create(b.minX,b.minY,b.minZ,b.maxX,y,b.maxZ);}
    public static boolean hasNaN(AxisAlignedBB b){return Double.isNaN(b.minX)||Double.isNaN(b.minY)||Double.isNaN(b.minZ)||Double.isNaN(b.maxX)||Double.isNaN(b.maxY)||Double.isNaN(b.maxZ);}
    public static RayTraceResult calculateIntercept(AxisAlignedBB b,Vec3d start,Vec3d end){return RayTraceResult.fromLegacy(b.calculateIntercept(start.toLegacy(),end.toLegacy()));}
}
