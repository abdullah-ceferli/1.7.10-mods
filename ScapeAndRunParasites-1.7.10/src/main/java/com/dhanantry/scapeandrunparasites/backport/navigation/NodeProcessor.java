package com.dhanantry.scapeandrunparasites.backport.navigation;

import net.minecraft.pathfinding.PathEntity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public abstract class NodeProcessor {
    protected IBlockAccess blockaccess;
    protected EntityLiving entity;
    protected final IntHashMap pointMap = new IntHashMap();
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
        this.entitySizeX = MathHelper.floor_double(entityLiving.width + 1.0f);
        this.entitySizeY = MathHelper.floor_double(entityLiving.height + 1.0f);
        this.entitySizeZ = MathHelper.floor_double(entityLiving.width + 1.0f);
    }

    public void postProcess() {
        this.blockaccess = null;
        this.entity = null;
    }

    protected SearchPoint openPoint(int n, int n2, int n3) {
        int n4 = SearchPoint.makeHash(n, n2, n3);
        SearchPoint pathPoint = (SearchPoint)this.pointMap.lookup(n4);
        if (pathPoint == null) {
            pathPoint = new SearchPoint(n, n2, n3);
            this.pointMap.addKey(n4, pathPoint);
        }
        return pathPoint;
    }

    public abstract SearchPoint getStart();

    public abstract SearchPoint getPathPointToCoords(double var1, double var3, double var5);

    public abstract int findPathOptions(SearchPoint[] var1, SearchPoint var2, SearchPoint var3, float var4);

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

