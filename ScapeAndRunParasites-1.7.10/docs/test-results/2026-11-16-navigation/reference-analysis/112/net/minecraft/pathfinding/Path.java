/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.pathfinding;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.Vec3d;

public class Path {
    private final PathPoint[] points;
    private PathPoint[] openSet = new PathPoint[0];
    private PathPoint[] closedSet = new PathPoint[0];
    private PathPoint target;
    private int currentPathIndex;
    private int pathLength;

    public Path(PathPoint[] pathPointArray) {
        this.points = pathPointArray;
        this.pathLength = pathPointArray.length;
    }

    public void incrementPathIndex() {
        ++this.currentPathIndex;
    }

    public boolean isFinished() {
        return this.currentPathIndex >= this.pathLength;
    }

    @Nullable
    public PathPoint getFinalPathPoint() {
        if (this.pathLength > 0) {
            return this.points[this.pathLength - 1];
        }
        return null;
    }

    public PathPoint getPathPointFromIndex(int n) {
        return this.points[n];
    }

    public void setPoint(int n, PathPoint pathPoint) {
        this.points[n] = pathPoint;
    }

    public int getCurrentPathLength() {
        return this.pathLength;
    }

    public void setCurrentPathLength(int n) {
        this.pathLength = n;
    }

    public int getCurrentPathIndex() {
        return this.currentPathIndex;
    }

    public void setCurrentPathIndex(int n) {
        this.currentPathIndex = n;
    }

    public Vec3d getVectorFromIndex(Entity entity, int n) {
        double d = (double)this.points[n].x + (double)((int)(entity.width + 1.0f)) * 0.5;
        double d2 = this.points[n].y;
        double d3 = (double)this.points[n].z + (double)((int)(entity.width + 1.0f)) * 0.5;
        return new Vec3d(d, d2, d3);
    }

    public Vec3d getPosition(Entity entity) {
        return this.getVectorFromIndex(entity, this.currentPathIndex);
    }

    public Vec3d getCurrentPos() {
        PathPoint pathPoint = this.points[this.currentPathIndex];
        return new Vec3d(pathPoint.x, pathPoint.y, pathPoint.z);
    }

    public boolean isSamePath(Path path) {
        if (path == null) {
            return false;
        }
        if (path.points.length != this.points.length) {
            return false;
        }
        for (int i = 0; i < this.points.length; ++i) {
            if (this.points[i].x == path.points[i].x && this.points[i].y == path.points[i].y && this.points[i].z == path.points[i].z) continue;
            return false;
        }
        return true;
    }

    public PathPoint[] getOpenSet() {
        return this.openSet;
    }

    public PathPoint[] getClosedSet() {
        return this.closedSet;
    }

    public PathPoint getTarget() {
        return this.target;
    }

    public static Path read(PacketBuffer packetBuffer) {
        int n = packetBuffer.readInt();
        PathPoint pathPoint = PathPoint.createFromBuffer(packetBuffer);
        PathPoint[] pathPointArray = new PathPoint[packetBuffer.readInt()];
        for (int i = 0; i < pathPointArray.length; ++i) {
            pathPointArray[i] = PathPoint.createFromBuffer(packetBuffer);
        }
        PathPoint[] pathPointArray2 = new PathPoint[packetBuffer.readInt()];
        for (int i = 0; i < pathPointArray2.length; ++i) {
            pathPointArray2[i] = PathPoint.createFromBuffer(packetBuffer);
        }
        PathPoint[] pathPointArray3 = new PathPoint[packetBuffer.readInt()];
        for (int i = 0; i < pathPointArray3.length; ++i) {
            pathPointArray3[i] = PathPoint.createFromBuffer(packetBuffer);
        }
        Path path = new Path(pathPointArray);
        path.openSet = pathPointArray2;
        path.closedSet = pathPointArray3;
        path.target = pathPoint;
        path.currentPathIndex = n;
        return path;
    }
}

