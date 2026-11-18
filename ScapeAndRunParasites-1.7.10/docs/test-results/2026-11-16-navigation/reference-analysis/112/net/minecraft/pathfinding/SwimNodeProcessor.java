/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.pathfinding;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

public class SwimNodeProcessor
extends NodeProcessor {
    @Override
    public PathPoint getStart() {
        return this.openPoint(MathHelper.floor(this.entity.getEntityBoundingBox().minX), MathHelper.floor(this.entity.getEntityBoundingBox().minY + 0.5), MathHelper.floor(this.entity.getEntityBoundingBox().minZ));
    }

    @Override
    public PathPoint getPathPointToCoords(double d, double d2, double d3) {
        return this.openPoint(MathHelper.floor(d - (double)(this.entity.width / 2.0f)), MathHelper.floor(d2 + 0.5), MathHelper.floor(d3 - (double)(this.entity.width / 2.0f)));
    }

    @Override
    public int findPathOptions(PathPoint[] pathPointArray, PathPoint pathPoint, PathPoint pathPoint2, float f) {
        int n = 0;
        for (EnumFacing enumFacing : EnumFacing.values()) {
            PathPoint pathPoint3 = this.getWaterNode(pathPoint.x + enumFacing.getXOffset(), pathPoint.y + enumFacing.getYOffset(), pathPoint.z + enumFacing.getZOffset());
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
    private PathPoint getWaterNode(int n, int n2, int n3) {
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
                    IBlockState iBlockState = this.blockaccess.getBlockState(mutableBlockPos.setPos(i, j, k));
                    if (iBlockState.getMaterial() == Material.WATER) continue;
                    return PathNodeType.BLOCKED;
                }
            }
        }
        return PathNodeType.WATER;
    }
}

