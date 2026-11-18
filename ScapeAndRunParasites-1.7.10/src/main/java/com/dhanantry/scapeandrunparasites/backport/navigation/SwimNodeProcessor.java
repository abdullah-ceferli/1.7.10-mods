package com.dhanantry.scapeandrunparasites.backport.navigation;

import net.minecraft.pathfinding.PathEntity;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.navigation.NavigationBlocks.State;
import net.minecraft.entity.EntityLiving;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class SwimNodeProcessor
extends NodeProcessor {
    @Override
    public SearchPoint getStart() {
        return this.openPoint(MathHelper.floor_double(this.entity.boundingBox.minX), MathHelper.floor_double(this.entity.boundingBox.minY + 0.5), MathHelper.floor_double(this.entity.boundingBox.minZ));
    }

    @Override
    public SearchPoint getPathPointToCoords(double d, double d2, double d3) {
        return this.openPoint(MathHelper.floor_double(d - (double)(this.entity.width / 2.0f)), MathHelper.floor_double(d2 + 0.5), MathHelper.floor_double(d3 - (double)(this.entity.width / 2.0f)));
    }

    @Override
    public int findPathOptions(SearchPoint[] pathPointArray, SearchPoint pathPoint, SearchPoint pathPoint2, float f) {
        int n = 0;
        for (EnumFacing enumFacing : EnumFacing.values()) {
            SearchPoint pathPoint3 = this.getWaterNode(pathPoint.x + enumFacing.getFrontOffsetX(), pathPoint.y + enumFacing.getFrontOffsetY(), pathPoint.z + enumFacing.getFrontOffsetZ());
            if (pathPoint3 == null || pathPoint3.visited || !(pathPoint3.distanceTo(pathPoint2) < f)) continue;
            pathPointArray[n++] = pathPoint3;
        }
        return n;
    }

    @Override
    public PathNodeType getPathNodeType(IBlockAccess iBlockAccess, int n, int n2, int n3, EntityLiving entityLiving, int n4, int n5, int n6, boolean bl, boolean bl2) {
        return PathNodeType.WATER;
    }

    @Override
    public PathNodeType getPathNodeType(IBlockAccess iBlockAccess, int n, int n2, int n3) {
        return PathNodeType.WATER;
    }

    @Nullable
    private SearchPoint getWaterNode(int n, int n2, int n3) {
        PathNodeType pathNodeType = this.isFree(n, n2, n3);
        if (pathNodeType == PathNodeType.WATER) {
            return this.openPoint(n, n2, n3);
        }
        return null;
    }

    private PathNodeType isFree(int n, int n2, int n3) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = n; i < n + this.entitySizeX; ++i) {
            for (int j = n2; j < n2 + this.entitySizeY; ++j) {
                for (int k = n3; k < n3 + this.entitySizeZ; ++k) {
                    State iBlockState = NavigationBlocks.state(this.blockaccess, mutableBlockPos.setPos(i, j, k));
                    if (iBlockState.getMaterial() == Material.water) continue;
                    return PathNodeType.BLOCKED;
                }
            }
        }
        return PathNodeType.WATER;
    }
}

