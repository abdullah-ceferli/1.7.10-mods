/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.SwimNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PathNavigateSwimmer
extends PathNavigate {
    public PathNavigateSwimmer(EntityLiving entityLiving, World world) {
        super(entityLiving, world);
    }

    @Override
    protected PathFinder getPathFinder() {
        return new PathFinder(new SwimNodeProcessor());
    }

    @Override
    protected boolean canNavigate() {
        return this.isInLiquid();
    }

    @Override
    protected Vec3d getEntityPosition() {
        return new Vec3d(this.entity.posX, this.entity.posY + (double)this.entity.height * 0.5, this.entity.posZ);
    }

    @Override
    protected void pathFollow() {
        Vec3d vec3d = this.getEntityPosition();
        float f = this.entity.width * this.entity.width;
        int n = 6;
        if (vec3d.squareDistanceTo(this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex())) < (double)f) {
            this.currentPath.incrementPathIndex();
        }
        for (int i = Math.min(this.currentPath.getCurrentPathIndex() + 6, this.currentPath.getCurrentPathLength() - 1); i > this.currentPath.getCurrentPathIndex(); --i) {
            Vec3d vec3d2 = this.currentPath.getVectorFromIndex(this.entity, i);
            if (vec3d2.squareDistanceTo(vec3d) > 36.0 || !this.isDirectPathBetweenPoints(vec3d, vec3d2, 0, 0, 0)) continue;
            this.currentPath.setCurrentPathIndex(i);
            break;
        }
        this.checkForStuck(vec3d);
    }

    @Override
    protected boolean isDirectPathBetweenPoints(Vec3d vec3d, Vec3d vec3d2, int n, int n2, int n3) {
        RayTraceResult rayTraceResult = this.world.rayTraceBlocks(vec3d, new Vec3d(vec3d2.x, vec3d2.y + (double)this.entity.height * 0.5, vec3d2.z), false, true, false);
        return rayTraceResult == null || rayTraceResult.typeOfHit == RayTraceResult.Type.MISS;
    }

    @Override
    public boolean canEntityStandOnPos(BlockPos blockPos) {
        return !this.world.getBlockState(blockPos).isFullBlock();
    }
}

