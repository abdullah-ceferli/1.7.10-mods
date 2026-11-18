/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.pathfinding.Path
 *  net.minecraft.pathfinding.PathNavigateGround
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathEntity;
import com.dhanantry.scapeandrunparasites.backport.navigation.PathNavigateGround;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class PathNavigateClimberStatus
extends PathNavigateGround {
    private BlockPos targetPosition;

    public PathNavigateClimberStatus(EntityLiving entityLivingIn, World worldIn) {
        super(entityLivingIn, worldIn);
    }

    public PathEntity getPathToPos(BlockPos pos) {
        this.targetPosition = pos;
        return super.getPathToPos(pos);
    }

    public PathEntity getPathToEntityLiving(Entity entityIn) {
        this.targetPosition = new BlockPos(entityIn);
        return super.getPathToEntityLiving(entityIn);
    }

    public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn) {
        PathEntity path = this.getPathToEntityLiving(entityIn);
        if (path != null) {
            return this.setPath(path, speedIn);
        }
        this.targetPosition = new BlockPos(entityIn);
        this.speed = speedIn;
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    public void onUpdateNavigation() {
        block4: {
            block5: {
                if (!this.noPath()) {
                    super.onUpdateNavigation();
                    return;
                }
                if (this.targetPosition == null) return;
                if (((EntityParasiteBase)this.entity).getParasiteStatus() > 2) return;
                double d0 = this.entity.width * this.entity.width;
                if (!(com.dhanantry.scapeandrunparasites.backport.navigation.NavigationAccess.distanceToCenter(this.entity, this.targetPosition) >= d0)) break block4;
                if (this.entity.posY <= (double)this.targetPosition.getY()) break block5;
                BlockPos blockPos = new BlockPos(this.targetPosition.getX(), MathHelper.floor_double((double)this.entity.posY), this.targetPosition.getZ());
                if (!(com.dhanantry.scapeandrunparasites.backport.navigation.NavigationAccess.distanceToCenter(this.entity, blockPos) >= d0)) break block4;
            }
            this.entity.getMoveHelper().setMoveTo((double)this.targetPosition.getX(), (double)this.targetPosition.getY(), (double)this.targetPosition.getZ(), this.speed);
            return;
        }
        this.targetPosition = null;
    }
}

