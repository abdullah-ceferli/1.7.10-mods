/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.FlyingNodeProcessor;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
    protected Vec3d getEntityPosition() {
        return new Vec3d(this.entity.posX, this.entity.posY, this.entity.posZ);
    }

    @Override
    public Path getPathToEntityLiving(Entity entity) {
        return this.getPathToPos(new BlockPos(entity));
    }

    @Override
    public void onUpdateNavigation() {
        Vec3d vec3d;
        ++this.totalTicks;
        if (this.tryUpdatePath) {
            this.updatePath();
        }
        if (this.noPath()) {
            return;
        }
        if (this.canNavigate()) {
            this.pathFollow();
        } else if (this.currentPath != null && this.currentPath.getCurrentPathIndex() < this.currentPath.getCurrentPathLength()) {
            vec3d = this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex());
            if (MathHelper.floor(this.entity.posX) == MathHelper.floor(vec3d.x) && MathHelper.floor(this.entity.posY) == MathHelper.floor(vec3d.y) && MathHelper.floor(this.entity.posZ) == MathHelper.floor(vec3d.z)) {
                this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
            }
        }
        this.debugPathFinding();
        if (this.noPath()) {
            return;
        }
        vec3d = this.currentPath.getPosition(this.entity);
        this.entity.getMoveHelper().setMoveTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
    }

    @Override
    protected boolean isDirectPathBetweenPoints(Vec3d vec3d, Vec3d vec3d2, int n, int n2, int n3) {
        int n4 = MathHelper.floor(vec3d.x);
        int n5 = MathHelper.floor(vec3d.y);
        int n6 = MathHelper.floor(vec3d.z);
        double d = vec3d2.x - vec3d.x;
        double d2 = vec3d2.y - vec3d.y;
        double d3 = vec3d2.z - vec3d.z;
        double d4 = d * d + d2 * d2 + d3 * d3;
        if (d4 < 1.0E-8) {
            return false;
        }
        double d5 = 1.0 / Math.sqrt(d4);
        double d6 = 1.0 / Math.abs(d *= d5);
        double d7 = 1.0 / Math.abs(d2 *= d5);
        double d8 = 1.0 / Math.abs(d3 *= d5);
        double d9 = (double)n4 - vec3d.x;
        double d10 = (double)n5 - vec3d.y;
        double d11 = (double)n6 - vec3d.z;
        if (d >= 0.0) {
            d9 += 1.0;
        }
        if (d2 >= 0.0) {
            d10 += 1.0;
        }
        if (d3 >= 0.0) {
            d11 += 1.0;
        }
        d9 /= d;
        d10 /= d2;
        d11 /= d3;
        int n7 = d < 0.0 ? -1 : 1;
        int n8 = d2 < 0.0 ? -1 : 1;
        int n9 = d3 < 0.0 ? -1 : 1;
        int n10 = MathHelper.floor(vec3d2.x);
        int n11 = MathHelper.floor(vec3d2.y);
        int n12 = MathHelper.floor(vec3d2.z);
        int n13 = n10 - n4;
        int n14 = n11 - n5;
        int n15 = n12 - n6;
        while (n13 * n7 > 0 || n14 * n8 > 0 || n15 * n9 > 0) {
            if (d9 < d11 && d9 <= d10) {
                d9 += d6;
                n13 = n10 - (n4 += n7);
                continue;
            }
            if (d10 < d9 && d10 <= d11) {
                d10 += d7;
                n14 = n11 - (n5 += n8);
                continue;
            }
            d11 += d8;
            n15 = n12 - (n6 += n9);
        }
        return true;
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
        return this.world.getBlockState(blockPos).isTopSolid();
    }
}

