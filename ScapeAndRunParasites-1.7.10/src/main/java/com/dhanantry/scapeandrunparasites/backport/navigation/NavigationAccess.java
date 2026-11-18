package com.dhanantry.scapeandrunparasites.backport.navigation;

import java.util.*;
import net.minecraft.entity.*;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;

public final class NavigationAccess {
    private NavigationAccess() {}
    private static final Map<EntityLiving,EnumMap<PathNodeType,Float>> priorities=Collections.synchronizedMap(new WeakHashMap<EntityLiving,EnumMap<PathNodeType,Float>>());
    public interface Factory { net.minecraft.pathfinding.PathNavigate srp$createNavigator(World world); }
    public static net.minecraft.pathfinding.PathNavigate create(EntityLiving entity,World world) {
        net.minecraft.pathfinding.PathNavigate result=entity instanceof Factory ? ((Factory)entity).srp$createNavigator(world) : new net.minecraft.pathfinding.PathNavigate(entity,world);
        if(result==null)throw new IllegalStateException("Navigator factory returned null for "+entity.getClass().getName());
        if(result instanceof PathNavigate) NavigationWorldListener.attach(world);
        return result;
    }
    public static float priority(EntityLiving entity,PathNodeType type) {
        synchronized(priorities) { Map<PathNodeType,Float> map=priorities.get(entity);Float value=map==null?null:map.get(type);return value==null?type.getPriority():value; }
    }
    public static void priority(EntityLiving entity,PathNodeType type,float value) {
        synchronized(priorities) { EnumMap<PathNodeType,Float> map=priorities.get(entity);if(map==null){map=new EnumMap<PathNodeType,Float>(PathNodeType.class);priorities.put(entity,map);}map.put(type,value); }
    }
    public static BlockPos position(Vec3 vec) { return new BlockPos(vec.xCoord,vec.yCoord,vec.zCoord); }
    public static double distanceToCenter(Entity entity,BlockPos pos) { return entity.getDistanceSq(pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5); }
    public static Vec3 currentPos(PathEntity path) { PathPoint p=path.getPathPointFromIndex(path.getCurrentPathIndex());return Vec3.createVectorHelper(p.xCoord,p.yCoord,p.zCoord); }
    public static PathEntity path(SearchPoint[] search) {
        PathPoint[] points=new PathPoint[search.length];for(int i=0;i<points.length;i++)points[i]=new PathPoint(search[i].x,search[i].y,search[i].z);
        return new PathEntity(points);
    }
    public static PathEntity raiseCauldrons(World world,PathEntity path) {
        if(path==null)return null;
        PathPoint[] replacement=null;
        for(int i=0;i<path.getCurrentPathLength();i++) {
            PathPoint p=replacement==null?path.getPathPointFromIndex(i):replacement[i];
            if(world.getBlock(p.xCoord,p.yCoord,p.zCoord)!=Blocks.cauldron)continue;
            if(replacement==null){replacement=new PathPoint[path.getCurrentPathLength()];for(int j=0;j<replacement.length;j++)replacement[j]=path.getPathPointFromIndex(j);}
            replacement[i]=new PathPoint(p.xCoord,p.yCoord+1,p.zCoord);
            if(i+1<replacement.length && p.yCoord>=replacement[i+1].yCoord) {PathPoint next=replacement[i+1];replacement[i+1]=new PathPoint(next.xCoord,p.yCoord+1,next.zCoord);}
        }
        if(replacement==null)return path;
        PathEntity result=new PathEntity(replacement);result.setCurrentPathIndex(path.getCurrentPathIndex());return result;
    }

    /** Center the body vertically inside the processor's reserved voxel volume. */
    public static double verticalMargin(EntityLiving entity) { return ((int)(entity.height + 1.0f) - entity.height) * 0.5; }
    public static Vec3 volumePoint(PathEntity path, EntityLiving entity, int index) { return path.getVectorFromIndex(entity,index).addVector(0,verticalMargin(entity),0); }
}
