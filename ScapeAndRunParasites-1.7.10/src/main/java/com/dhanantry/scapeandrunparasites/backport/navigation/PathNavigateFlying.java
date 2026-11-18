package com.dhanantry.scapeandrunparasites.backport.navigation;

import net.minecraft.pathfinding.PathEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class PathNavigateFlying
extends PathNavigate {
    public PathNavigateFlying(EntityLiving entityLiving, World world) {
        super(entityLiving, world);
    }

    @Override
    protected PathFinder getPathFinder() {
        this.nodeProcessor = new FlyingNodeProcessor();
        this.nodeProcessor.setCanEnterDoors(true);
        return new PathFinder(this.nodeProcessor);
    }

    @Override
    protected boolean canNavigate() {
        return this.canFloat() && this.isInLiquid() || !this.entity.isRiding();
    }

    @Override
    protected Vec3 getEntityPosition() {
        return Vec3.createVectorHelper(this.entity.posX, this.entity.posY, this.entity.posZ);
    }

    @Override
    public PathEntity getPathToEntityLiving(Entity entity) {
        return this.getPathToPos(new BlockPos(entity));
    }

    @Override protected void pathFollow() { followVolumePath(false); }

    @Override public void onUpdateNavigation() { updateVolumeNavigation(false); }

    @Override
    protected boolean isDirectPathBetweenPoints(Vec3 vec3d, Vec3 vec3d2, int n, int n2, int n3) {
        return vec3d.squareDistanceTo(vec3d2) >= 1.0E-8 && NavigationBlocks.clearSegment(world, entity, vec3d, vec3d2, false);
    }

    public void setCanOpenDoors(boolean bl) {
        this.nodeProcessor.setCanOpenDoors(bl);
    }

    public void setCanEnterDoors(boolean bl) {
        this.nodeProcessor.setCanEnterDoors(bl);
    }

    public void setCanFloat(boolean bl) {
        this.nodeProcessor.setCanSwim(bl);
    }

    public boolean canFloat() {
        return this.nodeProcessor.getCanSwim();
    }

    @Override
    public boolean canEntityStandOnPos(BlockPos blockPos) {
        return NavigationBlocks.state(this.world, blockPos).isTopSolid();
    }
}

