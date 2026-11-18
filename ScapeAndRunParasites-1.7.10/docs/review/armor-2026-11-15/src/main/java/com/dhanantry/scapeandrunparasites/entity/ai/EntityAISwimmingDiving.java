/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.pathfinding.PathNavigateFlying
 *  net.minecraft.pathfinding.PathNavigateGround
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;

public class EntityAISwimmingDiving
extends EntityAIBase {
    private final EntityLiving parent;
    private double yMotion;

    public EntityAISwimmingDiving(EntityLiving parentIn, double y) {
        this.parent = parentIn;
        this.setMutexBits(4);
        this.yMotion = y;
        if (parentIn.getNavigator() instanceof PathNavigateGround) {
            ((PathNavigateGround)parentIn.getNavigator()).setCanSwim(true);
        } else if (parentIn.getNavigator() instanceof PathNavigateFlying) {
            ((PathNavigateFlying)parentIn.getNavigator()).setCanFloat(true);
        }
    }

    public boolean shouldExecute() {
        if (this.parent.isInWater() || this.parent.isInLava()) {
            EntityLivingBase target;
            if (this.parent.getAttackTarget() != null && ((target = this.parent.getAttackTarget()).isInWater() || target.isInLava()) && target.getDistanceSq(this.parent.posX, target.posY, this.parent.posZ) < 25.0 && target.posY - this.parent.posY < -1.0) {
                this.parent.motionY -= this.yMotion;
                return false;
            }
            return true;
        }
        return false;
    }

    public void updateTask() {
        if (this.parent.getRNG().nextFloat() < 0.8f) {
            this.parent.getJumpHelper().setJumping();
        }
    }
}

