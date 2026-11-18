package com.dhanantry.scapeandrunparasites.backport.navigation;

import net.minecraft.pathfinding.PathEntity;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class PathFinder {
    private final PathHeap path = new PathHeap();
    private final Set<SearchPoint> closedSet = Sets.newHashSet();
    private final SearchPoint[] pathOptions = new SearchPoint[32];
    private final NodeProcessor nodeProcessor;

    public PathFinder(NodeProcessor nodeProcessor) {
        this.nodeProcessor = nodeProcessor;
    }

    @Nullable
    public PathEntity findPath(IBlockAccess iBlockAccess, EntityLiving entityLiving, Entity entity, float f) {
        return this.findPath(iBlockAccess, entityLiving, entity.posX, entity.boundingBox.minY, entity.posZ, f);
    }

    @Nullable
    public PathEntity findPath(IBlockAccess iBlockAccess, EntityLiving entityLiving, BlockPos blockPos, float f) {
        return this.findPath(iBlockAccess, entityLiving, (float)blockPos.getX() + 0.5f, (float)blockPos.getY() + 0.5f, (float)blockPos.getZ() + 0.5f, f);
    }

    @Nullable
    private PathEntity findPath(IBlockAccess iBlockAccess, EntityLiving entityLiving, double d, double d2, double d3, float f) {
        this.path.clearPath();
        this.nodeProcessor.init(iBlockAccess, entityLiving);
        try {
        SearchPoint pathPoint = this.nodeProcessor.getStart();
        SearchPoint pathPoint2 = this.nodeProcessor.getPathPointToCoords(d, d2, d3);
        PathEntity path = this.findPath(pathPoint, pathPoint2, f);
        return path;
        } finally { this.nodeProcessor.postProcess(); }
    }

    @Nullable
    private PathEntity findPath(SearchPoint pathPoint, SearchPoint pathPoint2, float f) {
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
            if (((SearchPoint)object).equals(pathPoint2)) {
                object2 = pathPoint2;
                break;
            }
            if (((SearchPoint)object).distanceManhattan(pathPoint2) < ((SearchPoint)object2).distanceManhattan(pathPoint2)) {
                object2 = object;
            }
            ((SearchPoint)object).visited = true;
            int n2 = this.nodeProcessor.findPathOptions(this.pathOptions, (SearchPoint)object, pathPoint2, f);
            for (int i = 0; i < n2; ++i) {
                SearchPoint pathPoint3 = this.pathOptions[i];
                if (nodeProcessor instanceof FlyingNodeProcessor || nodeProcessor instanceof SwimNodeProcessor) {
                    SearchPoint previous = (SearchPoint)object;
                    double offset = (int)(nodeProcessor.entity.width + 1.0f) * 0.5;
                    net.minecraft.util.Vec3 from = net.minecraft.util.Vec3.createVectorHelper(previous.x + offset, previous.y + NavigationAccess.verticalMargin(nodeProcessor.entity), previous.z + offset);
                    net.minecraft.util.Vec3 to = net.minecraft.util.Vec3.createVectorHelper(pathPoint3.x + offset, pathPoint3.y + NavigationAccess.verticalMargin(nodeProcessor.entity), pathPoint3.z + offset);
                    if (!NavigationBlocks.clearSegment(nodeProcessor.entity.worldObj, nodeProcessor.entity, from, to, nodeProcessor instanceof SwimNodeProcessor)) continue;
                }
                float f2 = ((SearchPoint)object).distanceManhattan(pathPoint3);
                pathPoint3.distanceFromOrigin = ((SearchPoint)object).distanceFromOrigin + f2;
                pathPoint3.cost = f2 + pathPoint3.costMalus;
                float f3 = ((SearchPoint)object).totalPathDistance + pathPoint3.cost;
                if (!(pathPoint3.distanceFromOrigin < f) || pathPoint3.isAssigned() && !(f3 < pathPoint3.totalPathDistance)) continue;
                pathPoint3.previous = (SearchPoint)object;
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
        object = this.createPath(pathPoint, (SearchPoint)object2);
        return (PathEntity)object;
    }

    private PathEntity createPath(SearchPoint pathPoint, SearchPoint pathPoint2) {
        int n = 1;
        SearchPoint pathPoint3 = pathPoint2;
        while (pathPoint3.previous != null) {
            ++n;
            pathPoint3 = pathPoint3.previous;
        }
        SearchPoint[] pathPointArray = new SearchPoint[n];
        pathPoint3 = pathPoint2;
        pathPointArray[--n] = pathPoint3;
        while (pathPoint3.previous != null) {
            pathPoint3 = pathPoint3.previous;
            pathPointArray[--n] = pathPoint3;
        }
        return NavigationAccess.path(pathPointArray);
    }
}

