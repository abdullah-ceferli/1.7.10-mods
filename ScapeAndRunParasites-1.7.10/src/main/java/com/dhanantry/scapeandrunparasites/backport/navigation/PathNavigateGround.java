package com.dhanantry.scapeandrunparasites.backport.navigation;

import net.minecraft.pathfinding.PathEntity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class PathNavigateGround
extends PathNavigate {
    private boolean shouldAvoidSun;

    public PathNavigateGround(EntityLiving entityLiving, World world) {
        super(entityLiving, world);
    }

    @Override
    protected PathFinder getPathFinder() {
        this.nodeProcessor = new WalkNodeProcessor();
        this.nodeProcessor.setCanEnterDoors(true);
        return new PathFinder(this.nodeProcessor);
    }

    @Override
    protected boolean canNavigate() {
        return this.entity.onGround || this.getCanSwim() && this.isInLiquid() || this.entity.isRiding();
    }

    @Override
    protected Vec3 getEntityPosition() {
        return Vec3.createVectorHelper(this.entity.posX, this.getPathablePosY(), this.entity.posZ);
    }

    @Override
    public PathEntity getPathToPos(BlockPos blockPos) {
        BlockPos blockPos2;
        if (NavigationBlocks.state(this.world, blockPos).getMaterial() == Material.air) {
            blockPos2 = blockPos.down();
            while (blockPos2.getY() > 0 && NavigationBlocks.state(this.world, blockPos2).getMaterial() == Material.air) {
                blockPos2 = blockPos2.down();
            }
            if (blockPos2.getY() > 0) {
                return super.getPathToPos(blockPos2.up());
            }
            while (blockPos2.getY() < this.world.getHeight() && NavigationBlocks.state(this.world, blockPos2).getMaterial() == Material.air) {
                blockPos2 = blockPos2.up();
            }
            blockPos = blockPos2;
        }
        if (NavigationBlocks.state(this.world, blockPos).getMaterial().isSolid()) {
            blockPos2 = blockPos.up();
            while (blockPos2.getY() < this.world.getHeight() && NavigationBlocks.state(this.world, blockPos2).getMaterial().isSolid()) {
                blockPos2 = blockPos2.up();
            }
            return super.getPathToPos(blockPos2);
        }
        return super.getPathToPos(blockPos);
    }

    @Override
    public PathEntity getPathToEntityLiving(Entity entity) {
        return this.getPathToPos(new BlockPos(entity));
    }

    private int getPathablePosY() {
        if (!this.entity.isInWater() || !this.getCanSwim()) {
            return (int)(this.entity.boundingBox.minY + 0.5);
        }
        int n = (int)this.entity.boundingBox.minY;
        Block block = NavigationBlocks.state(this.world, new BlockPos(MathHelper.floor_double(this.entity.posX), n, MathHelper.floor_double(this.entity.posZ))).getBlock();
        int n2 = 0;
        while (block == Blocks.flowing_water || block == Blocks.water) {
            block = NavigationBlocks.state(this.world, new BlockPos(MathHelper.floor_double(this.entity.posX), ++n, MathHelper.floor_double(this.entity.posZ))).getBlock();
            if (++n2 <= 16) continue;
            return (int)this.entity.boundingBox.minY;
        }
        return n;
    }

    @Override
    protected void removeSunnyPath() {
        super.removeSunnyPath();
        if (this.shouldAvoidSun) {
            if (NavigationBlocks.canSeeSky(this.world, new BlockPos(MathHelper.floor_double(this.entity.posX), (int)(this.entity.boundingBox.minY + 0.5), MathHelper.floor_double(this.entity.posZ)))) {
                return;
            }
            for (int i = 0; i < this.currentPath.getCurrentPathLength(); ++i) {
                net.minecraft.pathfinding.PathPoint pathPoint = this.currentPath.getPathPointFromIndex(i);
                if (!NavigationBlocks.canSeeSky(this.world, new BlockPos(pathPoint.xCoord, pathPoint.yCoord, pathPoint.zCoord))) continue;
                this.currentPath.setCurrentPathLength(i - 1);
                return;
            }
        }
    }

    @Override
    protected boolean isDirectPathBetweenPoints(Vec3 vec3d, Vec3 vec3d2, int n, int n2, int n3) {
        int n4 = MathHelper.floor_double(vec3d.xCoord);
        int n5 = MathHelper.floor_double(vec3d.zCoord);
        double d = vec3d2.xCoord - vec3d.xCoord;
        double d2 = vec3d2.zCoord - vec3d.zCoord;
        double d3 = d * d + d2 * d2;
        if (d3 < 1.0E-8) {
            return false;
        }
        double d4 = 1.0 / Math.sqrt(d3);
        if (!this.isSafeToStandAt(n4, (int)vec3d.yCoord, n5, n += 2, n2, n3 += 2, vec3d, d *= d4, d2 *= d4)) {
            return false;
        }
        n -= 2;
        n3 -= 2;
        double d5 = 1.0 / Math.abs(d);
        double d6 = 1.0 / Math.abs(d2);
        double d7 = (double)n4 - vec3d.xCoord;
        double d8 = (double)n5 - vec3d.zCoord;
        if (d >= 0.0) {
            d7 += 1.0;
        }
        if (d2 >= 0.0) {
            d8 += 1.0;
        }
        d7 /= d;
        d8 /= d2;
        int n6 = d < 0.0 ? -1 : 1;
        int n7 = d2 < 0.0 ? -1 : 1;
        int n8 = MathHelper.floor_double(vec3d2.xCoord);
        int n9 = MathHelper.floor_double(vec3d2.zCoord);
        int n10 = n8 - n4;
        int n11 = n9 - n5;
        while (n10 * n6 > 0 || n11 * n7 > 0) {
            if (d7 < d8) {
                d7 += d5;
                n10 = n8 - (n4 += n6);
            } else {
                d8 += d6;
                n11 = n9 - (n5 += n7);
            }
            if (this.isSafeToStandAt(n4, (int)vec3d.yCoord, n5, n, n2, n3, vec3d, d, d2)) continue;
            return false;
        }
        return true;
    }

    private boolean isSafeToStandAt(int n, int n2, int n3, int n4, int n5, int n6, Vec3 vec3d, double d, double d2) {
        int n7 = n - n4 / 2;
        int n8 = n3 - n6 / 2;
        if (!this.isPositionClear(n7, n2, n8, n4, n5, n6, vec3d, d, d2)) {
            return false;
        }
        for (int i = n7; i < n7 + n4; ++i) {
            for (int j = n8; j < n8 + n6; ++j) {
                double d3 = (double)i + 0.5 - vec3d.xCoord;
                double d4 = (double)j + 0.5 - vec3d.zCoord;
                if (d3 * d + d4 * d2 < 0.0) continue;
                PathNodeType pathNodeType = this.nodeProcessor.getPathNodeType(this.world, i, n2 - 1, j, this.entity, n4, n5, n6, true, true);
                if (pathNodeType == PathNodeType.WATER) {
                    return false;
                }
                if (pathNodeType == PathNodeType.LAVA) {
                    return false;
                }
                if (pathNodeType == PathNodeType.OPEN) {
                    return false;
                }
                pathNodeType = this.nodeProcessor.getPathNodeType(this.world, i, n2, j, this.entity, n4, n5, n6, true, true);
                float f = NavigationAccess.priority(this.entity, pathNodeType);
                if (f < 0.0f || f >= 8.0f) {
                    return false;
                }
                if (pathNodeType != PathNodeType.DAMAGE_FIRE && pathNodeType != PathNodeType.DANGER_FIRE && pathNodeType != PathNodeType.DAMAGE_OTHER) continue;
                return false;
            }
        }
        return true;
    }

    private boolean isPositionClear(int n, int n2, int n3, int n4, int n5, int n6, Vec3 vec3d, double d, double d2) {
        for (BlockPos blockPos : BlockPos.getAllInBox(new BlockPos(n, n2, n3), new BlockPos(n + n4 - 1, n2 + n5 - 1, n3 + n6 - 1))) {
            Block block;
            double d3;
            double d4 = (double)blockPos.getX() + 0.5 - vec3d.xCoord;
            if (d4 * d + (d3 = (double)blockPos.getZ() + 0.5 - vec3d.zCoord) * d2 < 0.0 || NavigationBlocks.passable(this.world, blockPos)) continue;
            return false;
        }
        return true;
    }

    public void setBreakDoors(boolean bl) {
        this.nodeProcessor.setCanOpenDoors(bl);
    }

    public void setEnterDoors(boolean bl) {
        this.nodeProcessor.setCanEnterDoors(bl);
    }

    public boolean getEnterDoors() {
        return this.nodeProcessor.getCanEnterDoors();
    }

    public void setCanSwim(boolean bl) {
        this.nodeProcessor.setCanSwim(bl);
    }

    public boolean getCanSwim() {
        return this.nodeProcessor.getCanSwim();
    }

    public void setAvoidSun(boolean bl) {
        this.shouldAvoidSun = bl;
    }
}

