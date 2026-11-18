/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

public abstract class NodeProcessor {
    protected IBlockAccess blockaccess;
    protected EntityLiving entity;
    protected final IntHashMap<PathPoint> pointMap = new IntHashMap();
    protected int entitySizeX;
    protected int entitySizeY;
    protected int entitySizeZ;
    protected boolean canEnterDoors;
    protected boolean canOpenDoors;
    protected boolean canSwim;

    public void init(IBlockAccess iBlockAccess, EntityLiving entityLiving) {
        this.blockaccess = iBlockAccess;
        this.entity = entityLiving;
        this.pointMap.clearMap();
        this.entitySizeX = MathHelper.floor(entityLiving.width + 1.0f);
        this.entitySizeY = MathHelper.floor(entityLiving.height + 1.0f);
        this.entitySizeZ = MathHelper.floor(entityLiving.width + 1.0f);
    }

    public void postProcess() {
        this.blockaccess = null;
        this.entity = null;
    }

    protected PathPoint openPoint(int n, int n2, int n3) {
        int n4 = PathPoint.makeHash(n, n2, n3);
        PathPoint pathPoint = this.pointMap.lookup(n4);
        if (pathPoint == null) {
            pathPoint = new PathPoint(n, n2, n3);
            this.pointMap.addKey(n4, pathPoint);
        }
        return pathPoint;
    }

    public abstract PathPoint getStart();

    public abstract PathPoint getPathPointToCoords(double var1, double var3, double var5);

    public abstract int findPathOptions(PathPoint[] var1, PathPoint var2, PathPoint var3, float var4);

    public abstract PathNodeType getPathNodeType(IBlockAccess var1, int var2, int var3, int var4, EntityLiving var5, int var6, int var7, int var8, boolean var9, boolean var10);

    public abstract PathNodeType getPathNodeType(IBlockAccess var1, int var2, int var3, int var4);

    public void setCanEnterDoors(boolean bl) {
        this.canEnterDoors = bl;
    }

    public void setCanOpenDoors(boolean bl) {
        this.canOpenDoors = bl;
    }

    public void setCanSwim(boolean bl) {
        this.canSwim = bl;
    }

    public boolean getCanEnterDoors() {
        return this.canEnterDoors;
    }

    public boolean getCanOpenDoors() {
        return this.canOpenDoors;
    }

    public boolean getCanSwim() {
        return this.canSwim;
    }
}

