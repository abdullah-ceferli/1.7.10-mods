package com.dhanantry.scapeandrunparasites.backport.navigation;

import net.minecraft.pathfinding.PathEntity;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.navigation.NavigationBlocks.State;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

public abstract class PathNavigate extends net.minecraft.pathfinding.PathNavigate {
    protected EntityLiving entity;
    protected World world;
    @Nullable
    protected PathEntity currentPath;
    protected double speed;
    private final IAttributeInstance pathSearchRange;
    protected int totalTicks;
    private int ticksAtLastPos;
    private Vec3 lastPosCheck = Vec3.createVectorHelper(0,0,0);
    private Vec3 timeoutCachedNode = Vec3.createVectorHelper(0,0,0);
    private long timeoutTimer;
    private long lastTimeoutCheck;
    private double timeoutLimit;
    protected float maxDistanceToWaypoint = 0.5f;
    protected boolean tryUpdatePath;
    private long lastTimeUpdated;
    protected NodeProcessor nodeProcessor;
    private BlockPos targetPos;
    private final PathFinder pathFinder;

    public PathNavigate(EntityLiving entityLiving, World world) {
        super(entityLiving, world);
        this.entity = entityLiving;
        this.world = world;
        this.pathSearchRange = entityLiving.getEntityAttribute(SharedMonsterAttributes.followRange);
        this.pathFinder = this.getPathFinder();
    }

    protected abstract PathFinder getPathFinder();

    public void setSpeed(double d) {
        this.speed = d;
    }

    public float getPathSearchRange() {
        return (float)this.pathSearchRange.getAttributeValue();
    }

    public boolean canUpdatePathOnTimeout() {
        return this.tryUpdatePath;
    }

    public void updatePath() {
        if (this.world.getTotalWorldTime() - this.lastTimeUpdated > 20L) {
            if (this.targetPos != null) {
                this.currentPath = null;
                this.currentPath = this.getPathToPos(this.targetPos);
                this.lastTimeUpdated = this.world.getTotalWorldTime();
                this.tryUpdatePath = false;
            }
        } else {
            this.tryUpdatePath = true;
        }
    }

    @Nullable
    public final PathEntity getPathToXYZ(double d, double d2, double d3) {
        return this.getPathToPos(new BlockPos(d, d2, d3));
    }

    @Nullable
    public PathEntity getPathToPos(BlockPos blockPos) {
        if (!this.canNavigate()) {
            return null;
        }
        if (this.currentPath != null && !this.currentPath.isFinished() && blockPos.equals(this.targetPos)) {
            return this.currentPath;
        }
        this.targetPos = blockPos;
        float f = this.getPathSearchRange();
        this.world.theProfiler.startSection("pathfind");
        BlockPos blockPos2 = new BlockPos(this.entity);
        int n = (int)(f + 8.0f);
        ChunkCache chunkCache = NavigationBlocks.cache(this.world, blockPos2, n);
        PathEntity path = this.pathFinder.findPath(chunkCache, this.entity, this.targetPos, f);
        this.world.theProfiler.endSection();
        return path;
    }

    @Nullable
    public PathEntity getPathToEntityLiving(Entity entity) {
        if (!this.canNavigate()) {
            return null;
        }
        BlockPos blockPos = new BlockPos(entity);
        if (this.currentPath != null && !this.currentPath.isFinished() && blockPos.equals(this.targetPos)) {
            return this.currentPath;
        }
        this.targetPos = blockPos;
        float f = this.getPathSearchRange();
        this.world.theProfiler.startSection("pathfind");
        BlockPos blockPos2 = new BlockPos(this.entity).up();
        int n = (int)(f + 16.0f);
        ChunkCache chunkCache = NavigationBlocks.cache(this.world, blockPos2, n);
        PathEntity path = this.pathFinder.findPath(chunkCache, this.entity, entity, f);
        this.world.theProfiler.endSection();
        return path;
    }

    public boolean tryMoveToXYZ(double d, double d2, double d3, double d4) {
        return this.setPath(this.getPathToXYZ(d, d2, d3), d4);
    }

    public boolean tryMoveToEntityLiving(Entity entity, double d) {
        PathEntity path = this.getPathToEntityLiving(entity);
        return path != null && this.setPath(path, d);
    }

    public boolean setPath(@Nullable PathEntity path, double d) {
        if (path == null) {
            this.clearPath();
            return false;
        }
        if (!path.isSamePath(this.currentPath)) {
            this.currentPath = path;
        }
        this.removeSunnyPath();
        if (this.currentPath.getCurrentPathLength() <= 0) {
            return false;
        }
        this.speed = d;
        Vec3 vec3d = this.getEntityPosition();
        this.ticksAtLastPos = this.totalTicks;
        this.lastPosCheck = vec3d;
        return true;
    }

    @Nullable
    public PathEntity getPath() {
        return this.currentPath;
    }

    public void onUpdateNavigation() {
        Object object;
        Vec3 vec3d;
        ++this.totalTicks;
        if (this.tryUpdatePath) {
            this.updatePath();
        }
        if (this.noPath()) {
            return;
        }
        if (this.canNavigate()) {
            this.pathFollow();
        } else if (this.currentPath != null && this.currentPath.getCurrentPathIndex() < this.currentPath.getCurrentPathLength()) {
            vec3d = this.getEntityPosition();
            object = this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex());
            if (vec3d.yCoord > ((Vec3)object).yCoord && !this.entity.onGround && MathHelper.floor_double(vec3d.xCoord) == MathHelper.floor_double(((Vec3)object).xCoord) && MathHelper.floor_double(vec3d.zCoord) == MathHelper.floor_double(((Vec3)object).zCoord)) {
                this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
            }
        }
        this.debugPathFinding();
        if (this.noPath()) {
            return;
        }
        vec3d = this.currentPath.getPosition(this.entity);
        object = NavigationAccess.position(vec3d).down();
        AxisAlignedBB axisAlignedBB = NavigationBlocks.state(this.world, (BlockPos)object).getBoundingBox(this.world, (BlockPos)object);
        vec3d = vec3d.addVector(0.0, axisAlignedBB.maxY - 1.0, 0.0);
        this.entity.getMoveHelper().setMoveTo(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord, this.speed);
    }

    protected void debugPathFinding() {
    }

    protected void pathFollow() {
        Vec3 vec3d = this.getEntityPosition();
        int n = this.currentPath.getCurrentPathLength();
        for (int i = this.currentPath.getCurrentPathIndex(); i < this.currentPath.getCurrentPathLength(); ++i) {
            if ((double)this.currentPath.getPathPointFromIndex((int)i).yCoord == Math.floor(vec3d.yCoord)) continue;
            n = i;
            break;
        }
        this.maxDistanceToWaypoint = this.entity.width > 0.75f ? this.entity.width / 2.0f : 0.75f - this.entity.width / 2.0f;
        Vec3 vec3d2 = NavigationAccess.currentPos(this.currentPath);
        if (MathHelper.abs((float)(this.entity.posX - (vec3d2.xCoord + 0.5))) < this.maxDistanceToWaypoint && MathHelper.abs((float)(this.entity.posZ - (vec3d2.zCoord + 0.5))) < this.maxDistanceToWaypoint && Math.abs(this.entity.posY - vec3d2.yCoord) < 1.0) {
            this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
        }
        int n2 = MathHelper.ceiling_float_int(this.entity.width);
        int n3 = MathHelper.ceiling_float_int(this.entity.height);
        int n4 = n2;
        for (int i = n - 1; i >= this.currentPath.getCurrentPathIndex(); --i) {
            if (!this.isDirectPathBetweenPoints(vec3d, this.currentPath.getVectorFromIndex(this.entity, i), n2, n3, n4)) continue;
            this.currentPath.setCurrentPathIndex(i);
            break;
        }
        this.checkForStuck(vec3d);
    }

    protected void checkForStuck(Vec3 vec3d) {
        if (this.totalTicks - this.ticksAtLastPos > 100) {
            if (vec3d.squareDistanceTo(this.lastPosCheck) < 2.25) {
                this.clearPath();
            }
            this.ticksAtLastPos = this.totalTicks;
            this.lastPosCheck = vec3d;
        }
        if (this.currentPath != null && !this.currentPath.isFinished()) {
            Vec3 vec3d2 = NavigationAccess.currentPos(this.currentPath);
            if (vec3d2.squareDistanceTo(this.timeoutCachedNode) == 0.0) {
                this.timeoutTimer += System.currentTimeMillis() - this.lastTimeoutCheck;
            } else {
                this.timeoutCachedNode = vec3d2;
                double d = vec3d.distanceTo(this.timeoutCachedNode);
                double d2 = this.timeoutLimit = this.entity.getAIMoveSpeed() > 0.0f ? d / (double)this.entity.getAIMoveSpeed() * 1000.0 : 0.0;
            }
            if (this.timeoutLimit > 0.0 && (double)this.timeoutTimer > this.timeoutLimit * 3.0) {
                this.timeoutCachedNode = Vec3.createVectorHelper(0,0,0);
                this.timeoutTimer = 0L;
                this.timeoutLimit = 0.0;
                this.clearPath();
            }
            this.lastTimeoutCheck = System.currentTimeMillis();
        }
    }

    public boolean noPath() {
        return this.currentPath == null || this.currentPath.isFinished();
    }

    public void clearPath() {
        this.currentPath = null;
        this.tryUpdatePath = false;
    }

    /** Native notifications have coordinates, but no previous block state. */
    void onWorldBlockChange(int x0,int y0,int z0,int x1,int y1,int z1) {
        if (tryUpdatePath || targetPos == null || noPath()) return;
        net.minecraft.pathfinding.PathPoint end = currentPath.getFinalPathPoint();
        double cx = (end.xCoord + entity.posX) * 0.5;
        double cy = (end.yCoord + entity.posY) * 0.5;
        double cz = (end.zCoord + entity.posZ) * 0.5;
        double dx = Math.max(x0, Math.min(x1 + 1.0, cx)) - cx;
        double dy = Math.max(y0, Math.min(y1 + 1.0, cy)) - cy;
        double dz = Math.max(z0, Math.min(z1 + 1.0, cz)) - cz;
        // Include a smoothed final segment even when just one path node remains.
        double radius = Math.max(currentPath.getCurrentPathLength() - currentPath.getCurrentPathIndex(),
                Math.sqrt(entity.getDistanceSq(end.xCoord,end.yCoord,end.zCoord)) * 0.5)
                + Math.max(entity.width,entity.height) + 1.0;
        if (dx*dx + dy*dy + dz*dz <= radius*radius) tryUpdatePath = true;
    }

    protected abstract Vec3 getEntityPosition();

    protected abstract boolean canNavigate();

    protected boolean isInLiquid() {
        return this.entity.isInWater() || this.entity.handleLavaMovement();
    }

    protected void removeSunnyPath() {
        currentPath = NavigationAccess.raiseCauldrons(world, currentPath);
    }

    protected abstract boolean isDirectPathBetweenPoints(Vec3 var1, Vec3 var2, int var3, int var4, int var5);

    public boolean canEntityStandOnPos(BlockPos blockPos) {
        return NavigationBlocks.state(this.world, blockPos.down()).isFullBlock();
    }

    public NodeProcessor getNodeProcessor() {
        return this.nodeProcessor;
    }

    @Override public void clearPathEntity() { clearPath(); }
    @Override public void setAvoidsWater(boolean avoid) { NavigationAccess.priority(entity, PathNodeType.WATER, avoid ? -1.0f : 0.0f); }
    @Override public boolean getAvoidsWater() { return NavigationAccess.priority(entity, PathNodeType.WATER) < 0.0f; }
    @Override public void setBreakDoors(boolean value) { nodeProcessor.setCanOpenDoors(value); }
    @Override public boolean getCanBreakDoors() { return nodeProcessor.getCanOpenDoors(); }
    @Override public void setEnterDoors(boolean value) { nodeProcessor.setCanEnterDoors(value); }
    @Override public void setCanSwim(boolean value) { nodeProcessor.setCanSwim(value); }
    @Override public void setAvoidSun(boolean value) { avoidSun = value; }
    protected boolean avoidSun;

    /** Three-dimensional following keeps vertical arrival and the swept body in the same frame. */
    protected void updateVolumeNavigation(boolean water) {
        ++totalTicks;
        if(tryUpdatePath) updatePath();
        if(noPath() || !canNavigate()) return;
        pathFollow();
        if(noPath()) return;
        Vec3 point=NavigationAccess.volumePoint(currentPath,entity,currentPath.getCurrentPathIndex());
        entity.getMoveHelper().setMoveTo(point.xCoord,point.yCoord,point.zCoord,speed);
    }

    protected void followVolumePath(boolean water) {
        Vec3 from=Vec3.createVectorHelper(entity.posX,entity.posY,entity.posZ);
        int index=currentPath.getCurrentPathIndex();
        Vec3 point=NavigationAccess.volumePoint(currentPath,entity,index);
        double arrival=Math.min(0.25,entity.width*0.5);
        if(from.squareDistanceTo(point)<arrival*arrival) {
            if(index+1==currentPath.getCurrentPathLength() || NavigationBlocks.clearSegment(world,entity,from,NavigationAccess.volumePoint(currentPath,entity,index+1),water))currentPath.incrementPathIndex();
        }
        if(!noPath()) {
            int last=water?Math.min(currentPath.getCurrentPathIndex()+6,currentPath.getCurrentPathLength()-1):currentPath.getCurrentPathLength()-1;
            for(int i=last;i>currentPath.getCurrentPathIndex();i--) {
                Vec3 candidate=NavigationAccess.volumePoint(currentPath,entity,i);
                if(water&&from.squareDistanceTo(candidate)>36)continue;
                if(NavigationBlocks.clearSegment(world,entity,from,candidate,water)){currentPath.setCurrentPathIndex(i);break;}
            }
        }
        checkForStuck(from);
    }
}
