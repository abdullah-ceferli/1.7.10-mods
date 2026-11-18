/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class PathNavigateClimber
extends PathNavigateGround {
    private BlockPos targetPosition;

    public PathNavigateClimber(EntityLiving entityLiving, World world) {
        super(entityLiving, world);
    }

    @Override
    public Path getPathToPos(BlockPos blockPos) {
        this.targetPosition = blockPos;
        return super.getPathToPos(blockPos);
    }

    @Override
    public Path getPathToEntityLiving(Entity entity) {
        this.targetPosition = new BlockPos(entity);
        return super.getPathToEntityLiving(entity);
    }

    @Override
    public boolean tryMoveToEntityLiving(Entity entity, double d) {
        Path path = this.getPathToEntityLiving(entity);
        if (path != null) {
            return this.setPath(path, d);
        }
        this.targetPosition = new BlockPos(entity);
        this.speed = d;
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public void onUpdateNavigation() {
        block5: {
            block4: {
                if (!this.noPath()) {
                    super.onUpdateNavigation();
                    return;
                }
                if (this.targetPosition == null) return;
                double d = this.entity.width * this.entity.width;
                if (this.entity.getDistanceSqToCenter(this.targetPosition) < d) break block4;
                if (!(this.entity.posY > (double)this.targetPosition.getY())) break block5;
                BlockPos blockPos = new BlockPos(this.targetPosition.getX(), MathHelper.floor(this.entity.posY), this.targetPosition.getZ());
                if (!(this.entity.getDistanceSqToCenter(blockPos) < d)) break block5;
            }
            this.targetPosition = null;
            return;
        }
        this.entity.getMoveHelper().setMoveTo(this.targetPosition.getX(), this.targetPosition.getY(), this.targetPosition.getZ(), this.speed);
    }
}

