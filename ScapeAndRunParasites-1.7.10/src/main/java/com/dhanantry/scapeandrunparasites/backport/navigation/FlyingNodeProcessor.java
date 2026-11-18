package com.dhanantry.scapeandrunparasites.backport.navigation;

import net.minecraft.pathfinding.PathEntity;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.HashSet;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class FlyingNodeProcessor
extends WalkNodeProcessor {
    @Override
    public void init(IBlockAccess iBlockAccess, EntityLiving entityLiving) {
        super.init(iBlockAccess, entityLiving);
        this.avoidsWater = NavigationAccess.priority(entityLiving, PathNodeType.WATER);
    }

    @Override
    public void postProcess() {
        NavigationAccess.priority(this.entity, PathNodeType.WATER, this.avoidsWater);
        super.postProcess();
    }

    @Override
    public SearchPoint getStart() {
        Object object;
        BlockPos blockPos;
        int n;
        if (this.getCanSwim() && this.entity.isInWater()) {
            n = (int)this.entity.boundingBox.minY;
            blockPos = new BlockPos.MutableBlockPos(MathHelper.floor_double(this.entity.posX), n, MathHelper.floor_double(this.entity.posZ));
            object = NavigationBlocks.state(this.blockaccess, blockPos).getBlock();
            while (object == Blocks.flowing_water || object == Blocks.water) {
                ((BlockPos.MutableBlockPos)blockPos).setPos(MathHelper.floor_double(this.entity.posX), ++n, MathHelper.floor_double(this.entity.posZ));
                object = NavigationBlocks.state(this.blockaccess, blockPos).getBlock();
            }
        } else {
            n = MathHelper.floor_double(this.entity.boundingBox.minY + 0.5);
        }
        if (NavigationAccess.priority(this.entity, (PathNodeType)((Object)(object = this.getPathNodeType(this.entity, (blockPos = new BlockPos(this.entity)).getX(), n, blockPos.getZ())))) < 0.0f) {
            HashSet<BlockPos> hashSet = Sets.newHashSet();
            hashSet.add(new BlockPos(this.entity.boundingBox.minX, (double)n, this.entity.boundingBox.minZ));
            hashSet.add(new BlockPos(this.entity.boundingBox.minX, (double)n, this.entity.boundingBox.maxZ));
            hashSet.add(new BlockPos(this.entity.boundingBox.maxX, (double)n, this.entity.boundingBox.minZ));
            hashSet.add(new BlockPos(this.entity.boundingBox.maxX, (double)n, this.entity.boundingBox.maxZ));
            for (BlockPos blockPos2 : hashSet) {
                PathNodeType pathNodeType = this.getPathNodeType(this.entity, blockPos2);
                if (!(NavigationAccess.priority(this.entity, pathNodeType) >= 0.0f)) continue;
                return super.openPoint(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
            }
        }
        return super.openPoint(blockPos.getX(), n, blockPos.getZ());
    }

    @Override
    public SearchPoint getPathPointToCoords(double d, double d2, double d3) {
        return super.openPoint(MathHelper.floor_double(d), MathHelper.floor_double(d2), MathHelper.floor_double(d3));
    }

    @Override
    public int findPathOptions(SearchPoint[] pathPointArray, SearchPoint pathPoint, SearchPoint pathPoint2, float f) {
        SearchPoint pathPoint3;
        boolean bl;
        int n = 0;
        SearchPoint pathPoint4 = this.openPoint(pathPoint.x, pathPoint.y, pathPoint.z + 1);
        SearchPoint pathPoint5 = this.openPoint(pathPoint.x - 1, pathPoint.y, pathPoint.z);
        SearchPoint pathPoint6 = this.openPoint(pathPoint.x + 1, pathPoint.y, pathPoint.z);
        SearchPoint pathPoint7 = this.openPoint(pathPoint.x, pathPoint.y, pathPoint.z - 1);
        SearchPoint pathPoint8 = this.openPoint(pathPoint.x, pathPoint.y + 1, pathPoint.z);
        SearchPoint pathPoint9 = this.openPoint(pathPoint.x, pathPoint.y - 1, pathPoint.z);
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
        if (pathPoint8 != null && !pathPoint8.visited && pathPoint8.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint8;
        }
        if (pathPoint9 != null && !pathPoint9.visited && pathPoint9.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint9;
        }
        boolean bl2 = pathPoint7 == null || pathPoint7.costMalus != 0.0f;
        boolean bl3 = pathPoint4 == null || pathPoint4.costMalus != 0.0f;
        boolean bl4 = pathPoint6 == null || pathPoint6.costMalus != 0.0f;
        boolean bl5 = pathPoint5 == null || pathPoint5.costMalus != 0.0f;
        boolean bl6 = pathPoint8 == null || pathPoint8.costMalus != 0.0f;
        boolean bl7 = bl = pathPoint9 == null || pathPoint9.costMalus != 0.0f;
        if (bl2 && bl5 && (pathPoint3 = this.openPoint(pathPoint.x - 1, pathPoint.y, pathPoint.z - 1)) != null && !pathPoint3.visited && pathPoint3.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint3;
        }
        if (bl2 && bl4 && (pathPoint3 = this.openPoint(pathPoint.x + 1, pathPoint.y, pathPoint.z - 1)) != null && !pathPoint3.visited && pathPoint3.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint3;
        }
        if (bl3 && bl5 && (pathPoint3 = this.openPoint(pathPoint.x - 1, pathPoint.y, pathPoint.z + 1)) != null && !pathPoint3.visited && pathPoint3.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint3;
        }
        if (bl3 && bl4 && (pathPoint3 = this.openPoint(pathPoint.x + 1, pathPoint.y, pathPoint.z + 1)) != null && !pathPoint3.visited && pathPoint3.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint3;
        }
        if (bl2 && bl6 && (pathPoint3 = this.openPoint(pathPoint.x, pathPoint.y + 1, pathPoint.z - 1)) != null && !pathPoint3.visited && pathPoint3.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint3;
        }
        if (bl3 && bl6 && (pathPoint3 = this.openPoint(pathPoint.x, pathPoint.y + 1, pathPoint.z + 1)) != null && !pathPoint3.visited && pathPoint3.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint3;
        }
        if (bl4 && bl6 && (pathPoint3 = this.openPoint(pathPoint.x + 1, pathPoint.y + 1, pathPoint.z)) != null && !pathPoint3.visited && pathPoint3.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint3;
        }
        if (bl5 && bl6 && (pathPoint3 = this.openPoint(pathPoint.x - 1, pathPoint.y + 1, pathPoint.z)) != null && !pathPoint3.visited && pathPoint3.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint3;
        }
        if (bl2 && bl && (pathPoint3 = this.openPoint(pathPoint.x, pathPoint.y - 1, pathPoint.z - 1)) != null && !pathPoint3.visited && pathPoint3.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint3;
        }
        if (bl3 && bl && (pathPoint3 = this.openPoint(pathPoint.x, pathPoint.y - 1, pathPoint.z + 1)) != null && !pathPoint3.visited && pathPoint3.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint3;
        }
        if (bl4 && bl && (pathPoint3 = this.openPoint(pathPoint.x + 1, pathPoint.y - 1, pathPoint.z)) != null && !pathPoint3.visited && pathPoint3.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint3;
        }
        if (bl5 && bl && (pathPoint3 = this.openPoint(pathPoint.x - 1, pathPoint.y - 1, pathPoint.z)) != null && !pathPoint3.visited && pathPoint3.distanceTo(pathPoint2) < f) {
            pathPointArray[n++] = pathPoint3;
        }
        return n;
    }

    @Override
    @Nullable
    protected SearchPoint openPoint(int n, int n2, int n3) {
        SearchPoint pathPoint = null;
        PathNodeType pathNodeType = this.getPathNodeType(this.entity, n, n2, n3);
        float f = NavigationAccess.priority(this.entity, pathNodeType);
        if (f >= 0.0f) {
            pathPoint = super.openPoint(n, n2, n3);
            pathPoint.nodeType = pathNodeType;
            pathPoint.costMalus = Math.max(pathPoint.costMalus, f);
            if (pathNodeType == PathNodeType.WALKABLE) {
                pathPoint.costMalus += 1.0f;
            }
        }
        if (pathNodeType == PathNodeType.OPEN || pathNodeType == PathNodeType.WALKABLE) {
            return pathPoint;
        }
        return pathPoint;
    }

    @Override
    public PathNodeType getPathNodeType(IBlockAccess iBlockAccess, int n, int n2, int n3, EntityLiving entityLiving, int n4, int n5, int n6, boolean bl, boolean bl2) {
        EnumSet<PathNodeType> enumSet = EnumSet.noneOf(PathNodeType.class);
        PathNodeType pathNodeType = PathNodeType.BLOCKED;
        BlockPos blockPos = new BlockPos(entityLiving);
        pathNodeType = this.getPathNodeType(iBlockAccess, n, n2, n3, n4, n5, n6, bl, bl2, enumSet, pathNodeType, blockPos);
        if (enumSet.contains((Object)PathNodeType.FENCE)) {
            return PathNodeType.FENCE;
        }
        PathNodeType pathNodeType2 = PathNodeType.BLOCKED;
        for (PathNodeType pathNodeType3 : enumSet) {
            if (NavigationAccess.priority(entityLiving, pathNodeType3) < 0.0f) {
                return pathNodeType3;
            }
            if (!(NavigationAccess.priority(entityLiving, pathNodeType3) >= NavigationAccess.priority(entityLiving, pathNodeType2))) continue;
            pathNodeType2 = pathNodeType3;
        }
        if (pathNodeType == PathNodeType.OPEN && NavigationAccess.priority(entityLiving, pathNodeType2) == 0.0f) {
            return PathNodeType.OPEN;
        }
        return pathNodeType2;
    }

    @Override
    public PathNodeType getPathNodeType(IBlockAccess iBlockAccess, int n, int n2, int n3) {
        PathNodeType pathNodeType = this.getPathNodeTypeRaw(iBlockAccess, n, n2, n3);
        if (pathNodeType == PathNodeType.OPEN && n2 >= 1) {
            Block block = NavigationBlocks.state(iBlockAccess, new BlockPos(n, n2 - 1, n3)).getBlock();
            PathNodeType pathNodeType2 = this.getPathNodeTypeRaw(iBlockAccess, n, n2 - 1, n3);
            pathNodeType = pathNodeType2 == PathNodeType.DAMAGE_FIRE || pathNodeType2 == PathNodeType.LAVA ? PathNodeType.DAMAGE_FIRE : (pathNodeType2 == PathNodeType.DAMAGE_CACTUS ? PathNodeType.DAMAGE_CACTUS : (pathNodeType2 == PathNodeType.WALKABLE || pathNodeType2 == PathNodeType.OPEN || pathNodeType2 == PathNodeType.WATER ? PathNodeType.OPEN : PathNodeType.WALKABLE));
        }
        pathNodeType = this.checkNeighborBlocks(iBlockAccess, n, n2, n3, pathNodeType);
        return pathNodeType;
    }

    private PathNodeType getPathNodeType(EntityLiving entityLiving, BlockPos blockPos) {
        return this.getPathNodeType(entityLiving, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    private PathNodeType getPathNodeType(EntityLiving entityLiving, int n, int n2, int n3) {
        return this.getPathNodeType(this.blockaccess, n, n2, n3, entityLiving, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.getCanOpenDoors(), this.getCanEnterDoors());
    }
}

