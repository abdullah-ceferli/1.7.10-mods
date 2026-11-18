/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.pathfinding;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

public abstract class PathNavigate {
    protected EntityLiving entity;
    protected World world;
    @Nullable
    protected Path currentPath;
    protected double speed;
    private final IAttributeInstance pathSearchRange;
    protected int totalTicks;
    private int ticksAtLastPos;
    private Vec3d lastPosCheck = Vec3d.ZERO;
    private Vec3d timeoutCachedNode = Vec3d.ZERO;
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
        this.entity = entityLiving;
        this.world = world;
        this.pathSearchRange = entityLiving.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
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
    public final Path getPathToXYZ(double d, double d2, double d3) {
        return this.getPathToPos(new BlockPos(d, d2, d3));
    }

    @Nullable
    public Path getPathToPos(BlockPos blockPos) {
        if (!this.canNavigate()) {
            return null;
        }
        if (this.currentPath != null && !this.currentPath.isFinished() && blockPos.equals(this.targetPos)) {
            return this.currentPath;
        }
        this.targetPos = blockPos;
        float f = this.getPathSearchRange();
        this.world.profiler.startSection("pathfind");
        BlockPos blockPos2 = new BlockPos(this.entity);
        int n = (int)(f + 8.0f);
        ChunkCache chunkCache = new ChunkCache(this.world, blockPos2.add(-n, -n, -n), blockPos2.add(n, n, n), 0);
        Path path = this.pathFinder.findPath(chunkCache, this.entity, this.targetPos, f);
        this.world.profiler.endSection();
        return path;
    }

    @Nullable
    public Path getPathToEntityLiving(Entity entity) {
        if (!this.canNavigate()) {
            return null;
        }
        BlockPos blockPos = new BlockPos(entity);
        if (this.currentPath != null && !this.currentPath.isFinished() && blockPos.equals(this.targetPos)) {
            return this.currentPath;
        }
        this.targetPos = blockPos;
        float f = this.getPathSearchRange();
        this.world.profiler.startSection("pathfind");
        BlockPos blockPos2 = new BlockPos(this.entity).up();
        int n = (int)(f + 16.0f);
        ChunkCache chunkCache = new ChunkCache(this.world, blockPos2.add(-n, -n, -n), blockPos2.add(n, n, n), 0);
        Path path = this.pathFinder.findPath(chunkCache, this.entity, entity, f);
        this.world.profiler.endSection();
        return path;
    }

    public boolean tryMoveToXYZ(double d, double d2, double d3, double d4) {
        return this.setPath(this.getPathToXYZ(d, d2, d3), d4);
    }

    public boolean tryMoveToEntityLiving(Entity entity, double d) {
        Path path = this.getPathToEntityLiving(entity);
        return path != null && this.setPath(path, d);
    }

    public boolean setPath(@Nullable Path path, double d) {
        if (path == null) {
            this.currentPath = null;
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
        Vec3d vec3d = this.getEntityPosition();
        this.ticksAtLastPos = this.totalTicks;
        this.lastPosCheck = vec3d;
        return true;
    }

    @Nullable
    public Path getPath() {
        return this.currentPath;
    }

    public void onUpdateNavigation() {
        Object object;
        Vec3d vec3d;
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
            if (vec3d.y > ((Vec3d)object).y && !this.entity.onGround && MathHelper.floor(vec3d.x) == MathHelper.floor(((Vec3d)object).x) && MathHelper.floor(vec3d.z) == MathHelper.floor(((Vec3d)object).z)) {
                this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
            }
        }
        this.debugPathFinding();
        if (this.noPath()) {
            return;
        }
        vec3d = this.currentPath.getPosition(this.entity);
        object = new BlockPos(vec3d).down();
        AxisAlignedBB axisAlignedBB = this.world.getBlockState((BlockPos)object).getBoundingBox(this.world, (BlockPos)object);
        vec3d = vec3d.subtract(0.0, 1.0 - axisAlignedBB.maxY, 0.0);
        this.entity.getMoveHelper().setMoveTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
    }

    protected void debugPathFinding() {
    }

    protected void pathFollow() {
        Vec3d vec3d = this.getEntityPosition();
        int n = this.currentPath.getCurrentPathLength();
        for (int i = this.currentPath.getCurrentPathIndex(); i < this.currentPath.getCurrentPathLength(); ++i) {
            if ((double)this.currentPath.getPathPointFromIndex((int)i).y == Math.floor(vec3d.y)) continue;
            n = i;
            break;
        }
        this.maxDistanceToWaypoint = this.entity.width > 0.75f ? this.entity.width / 2.0f : 0.75f - this.entity.width / 2.0f;
        Vec3d vec3d2 = this.currentPath.getCurrentPos();
        if (MathHelper.abs((float)(this.entity.posX - (vec3d2.x + 0.5))) < this.maxDistanceToWaypoint && MathHelper.abs((float)(this.entity.posZ - (vec3d2.z + 0.5))) < this.maxDistanceToWaypoint && Math.abs(this.entity.posY - vec3d2.y) < 1.0) {
            this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
        }
        int n2 = MathHelper.ceil(this.entity.width);
        int n3 = MathHelper.ceil(this.entity.height);
        int n4 = n2;
        for (int i = n - 1; i >= this.currentPath.getCurrentPathIndex(); --i) {
            if (!this.isDirectPathBetweenPoints(vec3d, this.currentPath.getVectorFromIndex(this.entity, i), n2, n3, n4)) continue;
            this.currentPath.setCurrentPathIndex(i);
            break;
        }
        this.checkForStuck(vec3d);
    }

    protected void checkForStuck(Vec3d vec3d) {
        if (this.totalTicks - this.ticksAtLastPos > 100) {
            if (vec3d.squareDistanceTo(this.lastPosCheck) < 2.25) {
                this.clearPath();
            }
            this.ticksAtLastPos = this.totalTicks;
            this.lastPosCheck = vec3d;
        }
        if (this.currentPath != null && !this.currentPath.isFinished()) {
            Vec3d vec3d2 = this.currentPath.getCurrentPos();
            if (vec3d2.equals(this.timeoutCachedNode)) {
                this.timeoutTimer += System.currentTimeMillis() - this.lastTimeoutCheck;
            } else {
                this.timeoutCachedNode = vec3d2;
                double d = vec3d.distanceTo(this.timeoutCachedNode);
                double d2 = this.timeoutLimit = this.entity.getAIMoveSpeed() > 0.0f ? d / (double)this.entity.getAIMoveSpeed() * 1000.0 : 0.0;
            }
            if (this.timeoutLimit > 0.0 && (double)this.timeoutTimer > this.timeoutLimit * 3.0) {
                this.timeoutCachedNode = Vec3d.ZERO;
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
    }

    protected abstract Vec3d getEntityPosition();

    protected abstract boolean canNavigate();

    protected boolean isInLiquid() {
        return this.entity.isInWater() || this.entity.isInLava();
    }

    protected void removeSunnyPath() {
        if (this.currentPath == null) {
            return;
        }
        for (int i = 0; i < this.currentPath.getCurrentPathLength(); ++i) {
            PathPoint pathPoint = this.currentPath.getPathPointFromIndex(i);
            PathPoint pathPoint2 = i + 1 < this.currentPath.getCurrentPathLength() ? this.currentPath.getPathPointFromIndex(i + 1) : null;
            IBlockState iBlockState = this.world.getBlockState(new BlockPos(pathPoint.x, pathPoint.y, pathPoint.z));
            Block block = iBlockState.getBlock();
            if (block != Blocks.CAULDRON) continue;
            this.currentPath.setPoint(i, pathPoint.cloneMove(pathPoint.x, pathPoint.y + 1, pathPoint.z));
            if (pathPoint2 == null || pathPoint.y < pathPoint2.y) continue;
            this.currentPath.setPoint(i + 1, pathPoint2.cloneMove(pathPoint2.x, pathPoint.y + 1, pathPoint2.z));
        }
    }

    protected abstract boolean isDirectPathBetweenPoints(Vec3d var1, Vec3d var2, int var3, int var4, int var5);

    public boolean canEntityStandOnPos(BlockPos blockPos) {
        return this.world.getBlockState(blockPos.down()).isFullBlock();
    }

    public NodeProcessor getNodeProcessor() {
        return this.nodeProcessor;
    }
}

