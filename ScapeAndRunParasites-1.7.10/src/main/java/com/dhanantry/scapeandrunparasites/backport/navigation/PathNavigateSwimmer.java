package com.dhanantry.scapeandrunparasites.backport.navigation;

import net.minecraft.pathfinding.PathEntity;

import net.minecraft.entity.EntityLiving;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class PathNavigateSwimmer
extends PathNavigate {
    public PathNavigateSwimmer(EntityLiving entityLiving, World world) {
        super(entityLiving, world);
    }

    @Override
    protected PathFinder getPathFinder() {
        this.nodeProcessor = new SwimNodeProcessor();
        return new PathFinder(this.nodeProcessor);
    }

    @Override
    protected boolean canNavigate() {
        return this.isInLiquid();
    }

    @Override
    protected Vec3 getEntityPosition() {
        return Vec3.createVectorHelper(this.entity.posX, this.entity.posY + (double)this.entity.height * 0.5, this.entity.posZ);
    }

    @Override public void onUpdateNavigation() { updateVolumeNavigation(true); }

    @Override protected void pathFollow() { followVolumePath(true); }

    @Override
    protected boolean isDirectPathBetweenPoints(Vec3 vec3d, Vec3 vec3d2, int n, int n2, int n3) {
        return NavigationBlocks.clearSegment(world, entity, vec3d.addVector(0, -entity.height * 0.5, 0), vec3d2, true);
    }

    @Override
    public boolean canEntityStandOnPos(BlockPos blockPos) {
        return !NavigationBlocks.state(this.world, blockPos).isFullBlock();
    }
}

