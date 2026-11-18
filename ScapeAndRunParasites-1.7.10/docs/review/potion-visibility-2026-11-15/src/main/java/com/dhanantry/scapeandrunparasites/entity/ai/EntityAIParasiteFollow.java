/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.pathfinding.PathNavigate
 *  net.minecraft.pathfinding.PathNodeType
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.world.World;

public class EntityAIParasiteFollow
extends EntityAIBase {
    private final EntityParasiteBase tameable;
    private EntityLivingBase owner;
    World world;
    private final double followSpeed;
    private final PathNavigate petPathfinder;
    private int timeToRecalcPath;
    double minBlockDistance;
    double maxBlockDistance;
    private float oldWaterCost;
    private boolean canSendOrder;

    public EntityAIParasiteFollow(EntityParasiteBase tameableIn, double followSpeedIn, double mindistance, double maxdistance, boolean send) {
        this.tameable = tameableIn;
        this.world = tameableIn.worldObj;
        this.followSpeed = followSpeedIn;
        this.petPathfinder = tameableIn.getNavigator();
        this.minBlockDistance = mindistance * mindistance;
        this.maxBlockDistance = maxdistance * maxdistance;
        this.canSendOrder = send;
        this.setMutexBits(3);
    }

    public boolean shouldExecute() {
        EntityParasiteBase entitylivingbase = this.tameable.getParasiteFollowing();
        if (entitylivingbase == null) {
            return false;
        }
        if (this.tameable.getDistanceSqToEntity((Entity)entitylivingbase) < this.minBlockDistance) {
            return false;
        }
        this.owner = entitylivingbase;
        return true;
    }

    public boolean continueExecuting() {
        if (!this.owner.isEntityAlive()) {
            this.tameable.setParasiteToFollow(null);
            return false;
        }
        return !this.petPathfinder.noPath() && this.tameable.getDistanceSqToEntity((Entity)this.owner) > this.maxBlockDistance;
    }

    public void startExecuting() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.tameable.getPathPriority(PathNodeType.WATER);
        this.tameable.setPathPriority(PathNodeType.WATER, 0.0f);
    }

    public void resetTask() {
        this.petPathfinder.clearPathEntity();
        this.tameable.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
    }

    public void updateTask() {
        this.tameable.getLookHelper().setLookPositionWithEntity((Entity)this.owner, 10.0f, (float)this.tameable.getVerticalFaceSpeed());
        if (this.tameable.getAttackTarget() != null && this.canSendOrder && this.tameable.getParasiteFollowing() != null && this.tameable.getParasiteFollowing().getAttackTarget() == null && !(this.tameable.getAttackTarget() instanceof EntityParasiteBase)) {
            this.tameable.getParasiteFollowing().setAttackTarget(this.tameable.getAttackTarget());
        }
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (!this.petPathfinder.tryMoveToEntityLiving((Entity)this.owner, this.followSpeed)) {
                this.tameable.setParasiteToFollow(null);
            }
        }
    }
}

