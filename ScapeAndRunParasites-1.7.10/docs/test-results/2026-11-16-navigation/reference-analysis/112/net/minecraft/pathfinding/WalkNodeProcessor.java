/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.pathfinding;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.HashSet;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

public class WalkNodeProcessor
extends NodeProcessor {
    protected float avoidsWater;

    @Override
    public void init(IBlockAccess iBlockAccess, EntityLiving entityLiving) {
        super.init(iBlockAccess, entityLiving);
        this.avoidsWater = entityLiving.getPathPriority(PathNodeType.WATER);
    }

    @Override
    public void postProcess() {
        this.entity.setPathPriority(PathNodeType.WATER, this.avoidsWater);
        super.postProcess();
    }

    @Override
    public PathPoint getStart() {
        Object object;
        BlockPos blockPos;
        int n;
        if (this.getCanSwim() && this.entity.isInWater()) {
            n = (int)this.entity.getEntityBoundingBox().minY;
            blockPos = new BlockPos.MutableBlockPos(MathHelper.floor(this.entity.posX), n, MathHelper.floor(this.entity.posZ));
            object = this.blockaccess.getBlockState(blockPos).getBlock();
            while (object == Blocks.FLOWING_WATER || object == Blocks.WATER) {
                ((BlockPos.MutableBlockPos)blockPos).setPos(MathHelper.floor(this.entity.posX), ++n, MathHelper.floor(this.entity.posZ));
                object = this.blockaccess.getBlockState(blockPos).getBlock();
            }
        } else if (this.entity.onGround) {
            n = MathHelper.floor(this.entity.getEntityBoundingBox().minY + 0.5);
        } else {
            blockPos = new BlockPos(this.entity);
            while ((this.blockaccess.getBlockState(blockPos).getMaterial() == Material.AIR || this.blockaccess.getBlockState(blockPos).getBlock().isPassable(this.blockaccess, blockPos)) && blockPos.getY() > 0) {
                blockPos = blockPos.down();
            }
            n = blockPos.up().getY();
        }
        blockPos = new BlockPos(this.entity);
        object = this.getPathNodeType(this.entity, blockPos.getX(), n, blockPos.getZ());
        if (this.entity.getPathPriority((PathNodeType)((Object)object)) < 0.0f) {
            HashSet hashSet = Sets.newHashSet();
            hashSet.add(new BlockPos(this.entity.getEntityBoundingBox().minX, (double)n, this.entity.getEntityBoundingBox().minZ));
            hashSet.add(new BlockPos(this.entity.getEntityBoundingBox().minX, (double)n, this.entity.getEntityBoundingBox().maxZ));
            hashSet.add(new BlockPos(this.entity.getEntityBoundingBox().maxX, (double)n, this.entity.getEntityBoundingBox().minZ));
            hashSet.add(new BlockPos(this.entity.getEntityBoundingBox().maxX, (double)n, this.entity.getEntityBoundingBox().maxZ));
            for (BlockPos blockPos2 : hashSet) {
                PathNodeType pathNodeType = this.getPathNodeType(this.entity, blockPos2);
                if (!(this.entity.getPathPriority(pathNodeType) >= 0.0f)) continue;
                return this.openPoint(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
            }
        }
        return this.openPoint(blockPos.getX(), n, blockPos.getZ());
    }

    @Override
    public PathPoint getPathPointToCoords(double d, double d2, double d3) {
        return this.openPoint(MathHelper.floor(d), MathHelper.floor(d2), MathHelper.floor(d3));
    }

    @Override
    public int findPathOptions(PathPoint[] pathPointArray, PathPoint pathPoint, PathPoint pathPoint2, float f) {
        PathPoint pathPoint3;
        boolean bl;
        int n = 0;
        int n2 = 0;
        PathNodeType pathNodeType = this.getPathNodeType(this.entity, pathPoint.x, pathPoint.y + 1, pathPoint.z);
        if (this.entity.getPathPriority(pathNodeType) >= 0.0f) {
            n2 = MathHelper.floor(Math.max(1.0f, this.entity.stepHeight));
        }
        BlockPos blockPos = new BlockPos(pathPoint.x, pathPoint.y, pathPoint.z).down();
        double d = (double)pathPoint.y - (1.0 - this.blockaccess.getBlockState((BlockPos)blockPos).getBoundingBox((IBlockAccess)this.blockaccess, (BlockPos)blockPos).maxY);
        PathPoint pathPoint4 = this.getSafePoint(pathPoint.x, pathPoint.y, pathPoint.z + 1, n2, d, EnumFacing.SOUTH);
        PathPoint pathPoint5 = this.getSafePoint(pathPoint.x - 1, pathPoint.y, pathPoint.z, n2, d, EnumFacing.WEST);
        PathPoint pathPoint6 = this.getSafePoint(pathPoint.x + 1, pathPoint.y, pathPoint.z, n2, d, EnumFacing.EAST);
        PathPoint pathPoint7 = this.getSafePoint(pathPoint.x, pathPoint.y, pathPoint.z - 1, n2, d, EnumFacing.NORTH);
        if (pathPoint4 != null && !pathPoint4.visited && pathPoint4.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint4;
        }
        if (pathPoint5 != null && !pathPoint5.visited && pathPoint5.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint5;
        }
        if (pathPoint6 != null && !pathPoint6.visited && pathPoint6.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint6;
        }
        if (pathPoint7 != null && !pathPoint7.visited && pathPoint7.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint7;
        }
        boolean bl2 = pathPoint7 == null || pathPoint7.nodeType == PathNodeType.OPEN || pathPoint7.costMalus != 0.0f;
        boolean bl3 = pathPoint4 == null || pathPoint4.nodeType == PathNodeType.OPEN || pathPoint4.costMalus != 0.0f;
        boolean bl4 = pathPoint6 == null || pathPoint6.nodeType == PathNodeType.OPEN || pathPoint6.costMalus != 0.0f;
        boolean bl5 = bl = pathPoint5 == null || pathPoint5.nodeType == PathNodeType.OPEN || pathPoint5.costMalus != 0.0f;
        if (bl2 && bl && (pathPoint3 = this.getSafePoint(pathPoint.x - 1, pathPoint.y, pathPoint.z - 1, n2, d, EnumFacing.NORTH)) != null && !pathPoint3.visited && pathPoint3.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint3;
        }
        if (bl2 && bl4 && (pathPoint3 = this.getSafePoint(pathPoint.x + 1, pathPoint.y, pathPoint.z - 1, n2, d, EnumFacing.NORTH)) != null && !pathPoint3.visited && pathPoint3.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint3;
        }
        if (bl3 && bl && (pathPoint3 = this.getSafePoint(pathPoint.x - 1, pathPoint.y, pathPoint.z + 1, n2, d, EnumFacing.SOUTH)) != null && !pathPoint3.visited && pathPoint3.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint3;
        }
        if (bl3 && bl4 && (pathPoint3 = this.getSafePoint(pathPoint.x + 1, pathPoint.y, pathPoint.z + 1, n2, d, EnumFacing.SOUTH)) != null && !pathPoint3.visited && pathPoint3.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint3;
        }
        return n;
    }

    @Nullable
    private PathPoint getSafePoint(int n, int n2, int n3, int n4, double d, EnumFacing enumFacing) {
        PathPoint pathPoint = null;
        BlockPos blockPos = new BlockPos(n, n2, n3);
        BlockPos blockPos2 = blockPos.down();
        double d2 = (double)n2 - (1.0 - this.blockaccess.getBlockState((BlockPos)blockPos2).getBoundingBox((IBlockAccess)this.blockaccess, (BlockPos)blockPos2).maxY);
        if (d2 - d > 1.125) {
            return null;
        }
        PathNodeType pathNodeType = this.getPathNodeType(this.entity, n, n2, n3);
        float f = this.entity.getPathPriority(pathNodeType);
        double d3 = (double)this.entity.width / 2.0;
        if (f >= 0.0f) {
            pathPoint = this.openPoint(n, n2, n3);
            pathPoint.nodeType = pathNodeType;
            pathPoint.costMalus = Math.max(pathPoint.costMalus, f);
        }
        if (pathNodeType == PathNodeType.WALKABLE) {
            return pathPoint;
        }
        if (pathPoint == null && n4 > 0 && pathNodeType != PathNodeType.FENCE && pathNodeType != PathNodeType.TRAPDOOR && (pathPoint = this.getSafePoint(n, n2 + 1, n3, n4 - 1, d, enumFacing)) != null && (pathPoint.nodeType == PathNodeType.OPEN || pathPoint.nodeType == PathNodeType.WALKABLE) && this.entity.width < 1.0f) {
            double d4 = (double)(n - enumFacing.getXOffset()) + 0.5;
            double d5 = (double)(n3 - enumFacing.getZOffset()) + 0.5;
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(d4 - d3, (double)n2 + 0.001, d5 - d3, d4 + d3, (float)n2 + this.entity.height, d5 + d3);
            AxisAlignedBB axisAlignedBB2 = this.blockaccess.getBlockState(blockPos).getBoundingBox(this.blockaccess, blockPos);
            AxisAlignedBB axisAlignedBB3 = axisAlignedBB.expand(0.0, axisAlignedBB2.maxY - 0.002, 0.0);
            if (this.entity.world.collidesWithAnyBlock(axisAlignedBB3)) {
                pathPoint = null;
            }
        }
        if (pathNodeType == PathNodeType.OPEN) {
            PathNodeType pathNodeType2;
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB((double)n - d3 + 0.5, (double)n2 + 0.001, (double)n3 - d3 + 0.5, (double)n + d3 + 0.5, (float)n2 + this.entity.height, (double)n3 + d3 + 0.5);
            if (this.entity.world.collidesWithAnyBlock(axisAlignedBB)) {
                return null;
            }
            if (this.entity.width >= 1.0f && (pathNodeType2 = this.getPathNodeType(this.entity, n, n2 - 1, n3)) == PathNodeType.BLOCKED) {
                pathPoint = this.openPoint(n, n2, n3);
                pathPoint.nodeType = PathNodeType.WALKABLE;
                pathPoint.costMalus = Math.max(pathPoint.costMalus, f);
                return pathPoint;
            }
            int n5 = 0;
            while (n2 > 0 && pathNodeType == PathNodeType.OPEN) {
                --n2;
                if (n5++ >= this.entity.getMaxFallHeight()) {
                    return null;
                }
                pathNodeType = this.getPathNodeType(this.entity, n, n2, n3);
                f = this.entity.getPathPriority(pathNodeType);
                if (pathNodeType != PathNodeType.OPEN && f >= 0.0f) {
                    pathPoint = this.openPoint(n, n2, n3);
                    pathPoint.nodeType = pathNodeType;
                    pathPoint.costMalus = Math.max(pathPoint.costMalus, f);
                    break;
                }
                if (!(f < 0.0f)) continue;
                return null;
            }
        }
        return pathPoint;
    }

    @Override
    public PathNodeType getPathNodeType(IBlockAccess iBlockAccess, int n, int n2, int n3, EntityLiving entityLiving, int n4, int n5, int n6, boolean bl, boolean bl2) {
        EnumSet<PathNodeType> enumSet = EnumSet.noneOf(PathNodeType.class);
        PathNodeType pathNodeType = PathNodeType.BLOCKED;
        double d = (double)entityLiving.width / 2.0;
        BlockPos blockPos = new BlockPos(entityLiving);
        pathNodeType = this.getPathNodeType(iBlockAccess, n, n2, n3, n4, n5, n6, bl, bl2, enumSet, pathNodeType, blockPos);
        if (enumSet.contains((Object)PathNodeType.FENCE)) {
            return PathNodeType.FENCE;
        }
        PathNodeType pathNodeType2 = PathNodeType.BLOCKED;
        for (PathNodeType pathNodeType3 : enumSet) {
            if (entityLiving.getPathPriority(pathNodeType3) < 0.0f) {
                return pathNodeType3;
            }
            if (!(entityLiving.getPathPriority(pathNodeType3) >= entityLiving.getPathPriority(pathNodeType2))) continue;
            pathNodeType2 = pathNodeType3;
        }
        if (pathNodeType == PathNodeType.OPEN && entityLiving.getPathPriority(pathNodeType2) == 0.0f) {
            return PathNodeType.OPEN;
        }
        return pathNodeType2;
    }

    public PathNodeType getPathNodeType(IBlockAccess iBlockAccess, int n, int n2, int n3, int n4, int n5, int n6, boolean bl, boolean bl2, EnumSet<PathNodeType> enumSet, PathNodeType pathNodeType, BlockPos blockPos) {
        for (int i = 0; i < n4; ++i) {
            for (int j = 0; j < n5; ++j) {
                for (int k = 0; k < n6; ++k) {
                    int n7 = i + n;
                    int n8 = j + n2;
                    int n9 = k + n3;
                    PathNodeType pathNodeType2 = this.getPathNodeType(iBlockAccess, n7, n8, n9);
                    if (pathNodeType2 == PathNodeType.DOOR_WOOD_CLOSED && bl && bl2) {
                        pathNodeType2 = PathNodeType.WALKABLE;
                    }
                    if (pathNodeType2 == PathNodeType.DOOR_OPEN && !bl2) {
                        pathNodeType2 = PathNodeType.BLOCKED;
                    }
                    if (pathNodeType2 == PathNodeType.RAIL && !(iBlockAccess.getBlockState(blockPos).getBlock() instanceof BlockRailBase) && !(iBlockAccess.getBlockState(blockPos.down()).getBlock() instanceof BlockRailBase)) {
                        pathNodeType2 = PathNodeType.FENCE;
                    }
                    if (i == 0 && j == 0 && k == 0) {
                        pathNodeType = pathNodeType2;
                    }
                    enumSet.add(pathNodeType2);
                }
            }
        }
        return pathNodeType;
    }

    private PathNodeType getPathNodeType(EntityLiving entityLiving, BlockPos blockPos) {
        return this.getPathNodeType(entityLiving, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    private PathNodeType getPathNodeType(EntityLiving entityLiving, int n, int n2, int n3) {
        return this.getPathNodeType(this.blockaccess, n, n2, n3, entityLiving, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.getCanOpenDoors(), this.getCanEnterDoors());
    }

    @Override
    public PathNodeType getPathNodeType(IBlockAccess iBlockAccess, int n, int n2, int n3) {
        PathNodeType pathNodeType = this.getPathNodeTypeRaw(iBlockAccess, n, n2, n3);
        if (pathNodeType == PathNodeType.OPEN && n2 >= 1) {
            Block block = iBlockAccess.getBlockState(new BlockPos(n, n2 - 1, n3)).getBlock();
            PathNodeType pathNodeType2 = this.getPathNodeTypeRaw(iBlockAccess, n, n2 - 1, n3);
            PathNodeType pathNodeType3 = pathNodeType = pathNodeType2 == PathNodeType.WALKABLE || pathNodeType2 == PathNodeType.OPEN || pathNodeType2 == PathNodeType.WATER || pathNodeType2 == PathNodeType.LAVA ? PathNodeType.OPEN : PathNodeType.WALKABLE;
            if (pathNodeType2 == PathNodeType.DAMAGE_FIRE || block == Blocks.MAGMA) {
                pathNodeType = PathNodeType.DAMAGE_FIRE;
            }
            if (pathNodeType2 == PathNodeType.DAMAGE_CACTUS) {
                pathNodeType = PathNodeType.DAMAGE_CACTUS;
            }
        }
        pathNodeType = this.checkNeighborBlocks(iBlockAccess, n, n2, n3, pathNodeType);
        return pathNodeType;
    }

    public PathNodeType checkNeighborBlocks(IBlockAccess iBlockAccess, int n, int n2, int n3, PathNodeType pathNodeType) {
        BlockPos.PooledMutableBlockPos pooledMutableBlockPos = BlockPos.PooledMutableBlockPos.retain();
        if (pathNodeType == PathNodeType.WALKABLE) {
            for (int i = -1; i <= 1; ++i) {
                for (int j = -1; j <= 1; ++j) {
                    if (i == 0 && j == 0) continue;
                    Block block = iBlockAccess.getBlockState(pooledMutableBlockPos.setPos(i + n, n2, j + n3)).getBlock();
                    if (block == Blocks.CACTUS) {
                        pathNodeType = PathNodeType.DANGER_CACTUS;
                        continue;
                    }
                    if (block != Blocks.FIRE) continue;
                    pathNodeType = PathNodeType.DANGER_FIRE;
                }
            }
        }
        pooledMutableBlockPos.release();
        return pathNodeType;
    }

    protected PathNodeType getPathNodeTypeRaw(IBlockAccess iBlockAccess, int n, int n2, int n3) {
        BlockPos blockPos = new BlockPos(n, n2, n3);
        IBlockState iBlockState = iBlockAccess.getBlockState(blockPos);
        Block block = iBlockState.getBlock();
        Material material = iBlockState.getMaterial();
        if (material == Material.AIR) {
            return PathNodeType.OPEN;
        }
        if (block == Blocks.TRAPDOOR || block == Blocks.IRON_TRAPDOOR || block == Blocks.WATERLILY) {
            return PathNodeType.TRAPDOOR;
        }
        if (block == Blocks.FIRE) {
            return PathNodeType.DAMAGE_FIRE;
        }
        if (block == Blocks.CACTUS) {
            return PathNodeType.DAMAGE_CACTUS;
        }
        if (block instanceof BlockDoor && material == Material.WOOD && !iBlockState.getValue(BlockDoor.OPEN).booleanValue()) {
            return PathNodeType.DOOR_WOOD_CLOSED;
        }
        if (block instanceof BlockDoor && material == Material.IRON && !iBlockState.getValue(BlockDoor.OPEN).booleanValue()) {
            return PathNodeType.DOOR_IRON_CLOSED;
        }
        if (block instanceof BlockDoor && iBlockState.getValue(BlockDoor.OPEN).booleanValue()) {
            return PathNodeType.DOOR_OPEN;
        }
        if (block instanceof BlockRailBase) {
            return PathNodeType.RAIL;
        }
        if (block instanceof BlockFence || block instanceof BlockWall || block instanceof BlockFenceGate && !iBlockState.getValue(BlockFenceGate.OPEN).booleanValue()) {
            return PathNodeType.FENCE;
        }
        if (material == Material.WATER) {
            return PathNodeType.WATER;
        }
        if (material == Material.LAVA) {
            return PathNodeType.LAVA;
        }
        if (block.isPassable(iBlockAccess, blockPos)) {
            return PathNodeType.OPEN;
        }
        return PathNodeType.BLOCKED;
    }
}

