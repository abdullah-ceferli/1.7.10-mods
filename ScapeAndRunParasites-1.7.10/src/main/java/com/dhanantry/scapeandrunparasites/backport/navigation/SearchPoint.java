package com.dhanantry.scapeandrunparasites.backport.navigation;

import net.minecraft.pathfinding.PathEntity;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.MathHelper;

public class SearchPoint {
    public final int x;
    public final int y;
    public final int z;
    private final int hash;
    public int index = -1;
    public float totalPathDistance;
    public float distanceToNext;
    public float distanceToTarget;
    public SearchPoint previous;
    public boolean visited;
    public float distanceFromOrigin;
    public float cost;
    public float costMalus;
    public PathNodeType nodeType = PathNodeType.BLOCKED;

    public SearchPoint(int n, int n2, int n3) {
        this.x = n;
        this.y = n2;
        this.z = n3;
        this.hash = SearchPoint.makeHash(n, n2, n3);
    }

    public SearchPoint cloneMove(int n, int n2, int n3) {
        SearchPoint pathPoint = new SearchPoint(n, n2, n3);
        pathPoint.index = this.index;
        pathPoint.totalPathDistance = this.totalPathDistance;
        pathPoint.distanceToNext = this.distanceToNext;
        pathPoint.distanceToTarget = this.distanceToTarget;
        pathPoint.previous = this.previous;
        pathPoint.visited = this.visited;
        pathPoint.distanceFromOrigin = this.distanceFromOrigin;
        pathPoint.cost = this.cost;
        pathPoint.costMalus = this.costMalus;
        pathPoint.nodeType = this.nodeType;
        return pathPoint;
    }

    public static int makeHash(int n, int n2, int n3) {
        return n2 & 0xFF | (n & Short.MAX_VALUE) << 8 | (n3 & Short.MAX_VALUE) << 24 | (n < 0 ? Integer.MIN_VALUE : 0) | (n3 < 0 ? 32768 : 0);
    }

    public float distanceTo(SearchPoint pathPoint) {
        float f = pathPoint.x - this.x;
        float f2 = pathPoint.y - this.y;
        float f3 = pathPoint.z - this.z;
        return MathHelper.sqrt_float(f * f + f2 * f2 + f3 * f3);
    }

    public float distanceToSquared(SearchPoint pathPoint) {
        float f = pathPoint.x - this.x;
        float f2 = pathPoint.y - this.y;
        float f3 = pathPoint.z - this.z;
        return f * f + f2 * f2 + f3 * f3;
    }

    public float distanceManhattan(SearchPoint pathPoint) {
        float f = Math.abs(pathPoint.x - this.x);
        float f2 = Math.abs(pathPoint.y - this.y);
        float f3 = Math.abs(pathPoint.z - this.z);
        return f + f2 + f3;
    }

    public boolean equals(Object object) {
        if (object instanceof SearchPoint) {
            SearchPoint pathPoint = (SearchPoint)object;
            return this.hash == pathPoint.hash && this.x == pathPoint.x && this.y == pathPoint.y && this.z == pathPoint.z;
        }
        return false;
    }

    public int hashCode() {
        return this.hash;
    }

    public boolean isAssigned() {
        return this.index >= 0;
    }

    public String toString() {
        return this.x + ", " + this.y + ", " + this.z;
    }

    public static SearchPoint createFromBuffer(PacketBuffer packetBuffer) {
        SearchPoint pathPoint = new SearchPoint(packetBuffer.readInt(), packetBuffer.readInt(), packetBuffer.readInt());
        pathPoint.distanceFromOrigin = packetBuffer.readFloat();
        pathPoint.cost = packetBuffer.readFloat();
        pathPoint.costMalus = packetBuffer.readFloat();
        pathPoint.visited = packetBuffer.readBoolean();
        pathPoint.nodeType = PathNodeType.values()[packetBuffer.readInt()];
        pathPoint.distanceToTarget = packetBuffer.readFloat();
        return pathPoint;
    }
}

