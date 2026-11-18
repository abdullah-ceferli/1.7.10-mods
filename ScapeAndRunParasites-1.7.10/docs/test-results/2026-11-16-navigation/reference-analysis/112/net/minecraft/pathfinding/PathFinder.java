/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.pathfinding;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathHeap;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class PathFinder {
    private final PathHeap path = new PathHeap();
    private final Set<PathPoint> closedSet = Sets.newHashSet();
    private final PathPoint[] pathOptions = new PathPoint[32];
    private final NodeProcessor nodeProcessor;

    public PathFinder(NodeProcessor nodeProcessor) {
        this.nodeProcessor = nodeProcessor;
    }

    @Nullable
    public Path findPath(IBlockAccess iBlockAccess, EntityLiving entityLiving, Entity entity, float f) {
        return this.findPath(iBlockAccess, entityLiving, entity.posX, entity.getEntityBoundingBox().minY, entity.posZ, f);
    }

    @Nullable
    public Path findPath(IBlockAccess iBlockAccess, EntityLiving entityLiving, BlockPos blockPos, float f) {
        return this.findPath(iBlockAccess, entityLiving, (float)blockPos.getX() + 0.5f, (float)blockPos.getY() + 0.5f, (float)blockPos.getZ() + 0.5f, f);
    }

    @Nullable
    private Path findPath(IBlockAccess iBlockAccess, EntityLiving entityLiving, double d, double d2, double d3, float f) {
        this.path.clearPath();
        this.nodeProcessor.init(iBlockAccess, entityLiving);
        PathPoint pathPoint = this.nodeProcessor.getStart();
        PathPoint pathPoint2 = this.nodeProcessor.getPathPointToCoords(d, d2, d3);
        Path path = this.findPath(pathPoint, pathPoint2, f);
        this.nodeProcessor.postProcess();
        return path;
    }

    @Nullable
    private Path findPath(PathPoint pathPoint, PathPoint pathPoint2, float f) {
        Object object;
        pathPoint.totalPathDistance = 0.0f;
        pathPoint.distanceToTarget = pathPoint.distanceToNext = pathPoint.distanceManhattan(pathPoint2);
        this.path.clearPath();
        this.closedSet.clear();
        this.path.addPoint(pathPoint);
        Object object2 = pathPoint;
        int n = 0;
        while (!this.path.isPathEmpty() && ++n < 200) {
            object = this.path.dequeue();
            if (((PathPoint)object).equals(pathPoint2)) {
                object2 = pathPoint2;
                break;
            }
            if (((PathPoint)object).distanceManhattan(pathPoint2) < ((PathPoint)object2).distanceManhattan(pathPoint2)) {
                object2 = object;
            }
            ((PathPoint)object).visited = true;
            int n2 = this.nodeProcessor.findPathOptions(this.pathOptions, (PathPoint)object, pathPoint2, f);
            for (int i = 0; i < n2; ++i) {
                PathPoint pathPoint3 = this.pathOptions[i];
                float f2 = ((PathPoint)object).distanceManhattan(pathPoint3);
                pathPoint3.distanceFromOrigin = ((PathPoint)object).distanceFromOrigin + f2;
                pathPoint3.cost = f2 + pathPoint3.costMalus;
                float f3 = ((PathPoint)object).totalPathDistance + pathPoint3.cost;
                if (!(pathPoint3.distanceFromOrigin < f) || pathPoint3.isAssigned() && !(f3 < pathPoint3.totalPathDistance)) continue;
                pathPoint3.previous = object;
                pathPoint3.totalPathDistance = f3;
                pathPoint3.distanceToNext = pathPoint3.distanceManhattan(pathPoint2) + pathPoint3.costMalus;
                if (pathPoint3.isAssigned()) {
                    this.path.changeDistance(pathPoint3, pathPoint3.totalPathDistance + pathPoint3.distanceToNext);
                    continue;
                }
                pathPoint3.distanceToTarget = pathPoint3.totalPathDistance + pathPoint3.distanceToNext;
                this.path.addPoint(pathPoint3);
            }
        }
        if (object2 == pathPoint) {
            return null;
        }
        object = this.createPath(pathPoint, (PathPoint)object2);
        return object;
    }

    private Path createPath(PathPoint pathPoint, PathPoint pathPoint2) {
        int n = 1;
        PathPoint pathPoint3 = pathPoint2;
        while (pathPoint3.previous != null) {
            ++n;
            pathPoint3 = pathPoint3.previous;
        }
        PathPoint[] pathPointArray = new PathPoint[n];
        pathPoint3 = pathPoint2;
        pathPointArray[--n] = pathPoint3;
        while (pathPoint3.previous != null) {
            pathPoint3 = pathPoint3.previous;
            pathPointArray[--n] = pathPoint3;
        }
        return new Path(pathPointArray);
    }
}

