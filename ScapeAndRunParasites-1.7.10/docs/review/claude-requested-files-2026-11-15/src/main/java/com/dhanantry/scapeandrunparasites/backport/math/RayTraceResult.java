package com.dhanantry.scapeandrunparasites.backport.math;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
/** Hit data with explicit conversion at the Minecraft 1.7 ray-trace boundary. */
public final class RayTraceResult {
    public enum Type { MISS,BLOCK,ENTITY }
    public Type typeOfHit;public Vec3d hitVec;public EnumFacing sideHit;public Entity entityHit;private final BlockPos blockPos;
    public RayTraceResult(Entity entity){this(entity,new Vec3d(entity.posX,entity.posY,entity.posZ));}
    public RayTraceResult(Entity entity,Vec3d hit){typeOfHit=Type.ENTITY;entityHit=entity;hitVec=hit;blockPos=null;}
    public RayTraceResult(Vec3d hit,EnumFacing side,BlockPos pos){this(Type.BLOCK,hit,side,pos);}
    public RayTraceResult(Type type,Vec3d hit,EnumFacing side,BlockPos pos){typeOfHit=type;hitVec=hit;sideHit=side;blockPos=pos.toImmutable();}
    public BlockPos getBlockPos(){return blockPos;}
    public static RayTraceResult fromLegacy(MovingObjectPosition hit){if(hit==null)return null;Vec3d v=Vec3d.fromLegacy(hit.hitVec);if(hit.typeOfHit==MovingObjectPosition.MovingObjectType.ENTITY)return new RayTraceResult(hit.entityHit,v);return new RayTraceResult(hit.typeOfHit==MovingObjectPosition.MovingObjectType.BLOCK?Type.BLOCK:Type.MISS,v,EnumFacing.getFront(hit.sideHit),new BlockPos(hit.blockX,hit.blockY,hit.blockZ));}
}
