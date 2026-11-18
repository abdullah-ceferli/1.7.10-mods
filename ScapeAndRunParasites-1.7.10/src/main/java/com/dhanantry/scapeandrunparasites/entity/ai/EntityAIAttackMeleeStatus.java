/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.pathfinding.Path
 *  net.minecraft.pathfinding.PathPoint
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class EntityAIAttackMeleeStatus
extends EntityAIBase {
    World world;
    protected EntityParasiteBase attacker;
    protected int attackTick;
    double speedTowardsTarget;
    boolean longMemory;
    PathEntity path;
    private int delayCounter;
    private double targetX;
    private double targetY;
    private double targetZ;
    private double blockDistance;
    private int failedPathFindingPenalty = 0;
    private boolean canPenalize = false;

    public EntityAIAttackMeleeStatus(EntityParasiteBase creature, double speedIn, boolean useLongMemory, double runningD) {
        this.attacker = creature;
        this.world = creature.worldObj;
        this.speedTowardsTarget = speedIn;
        this.longMemory = useLongMemory;
        this.blockDistance = runningD * runningD;
        this.setMutexBits(3);
    }

    public boolean shouldExecute() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        if (entitylivingbase == null) {
            return false;
        }
        if (!entitylivingbase.isEntityAlive() || !this.attacker.shouldWorkTask()) {
            return false;
        }
        if (this.canPenalize) {
            if (--this.delayCounter <= 0) {
                this.path = this.attacker.getNavigator().getPathToEntityLiving((Entity)entitylivingbase);
                this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);
                return this.path != null;
            }
            return true;
        }
        this.path = this.attacker.getNavigator().getPathToEntityLiving((Entity)entitylivingbase);
        if (this.path != null) {
            return true;
        }
        return this.getAttackReachSqr(entitylivingbase) >= this.attacker.getDistanceSq(entitylivingbase.posX, Boxes.entityBox(entitylivingbase).minY, entitylivingbase.posZ);
    }

    public boolean continueExecuting() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        if (entitylivingbase == null) {
            return false;
        }
        if (!entitylivingbase.isEntityAlive() || !this.attacker.shouldWorkTask()) {
            return false;
        }
        if (!this.longMemory) {
            return !this.attacker.getNavigator().noPath();
        }
        if (!this.attacker.isWithinHomeDistanceFromPosition(new BlockPos((Entity)entitylivingbase))) {
            return false;
        }
        return !(entitylivingbase instanceof EntityPlayer) || !((EntityPlayer)entitylivingbase).isSpectator() && !((EntityPlayer)entitylivingbase).isCreative();
    }

    public void startExecuting() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        if (this.attacker.getParasiteStatus() <= 2 && this.attacker.getLLeap() < 1) {
            if ((this.attacker.getDistanceSqToEntity((Entity)entitylivingbase) > this.blockDistance || this.attacker.getAttackCooldownAni() == 0) && this.attacker.getGeneMod(3)) {
                this.attacker.getNavigator().setPath(this.path, this.speedTowardsTarget);
                this.attacker.setParasiteStatus(2);
                this.attacker.setAttackCooldownAni(0);
            } else {
                this.attacker.getNavigator().setPath(this.path, 1.0);
                this.attacker.setParasiteStatus(1);
            }
        }
        this.delayCounter = 0;
    }

    public void resetTask() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        if (entitylivingbase instanceof EntityPlayer && (((EntityPlayer)entitylivingbase).isSpectator() || ((EntityPlayer)entitylivingbase).isCreative())) {
            this.attacker.setAttackTarget(null);
        }
        this.attacker.getNavigator().clearPathEntity();
    }

    public void updateTask() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        if (entitylivingbase == null) {
            return;
        }
        this.attacker.getLookHelper().setLookPositionWithEntity((Entity)entitylivingbase, 30.0f, 30.0f);
        double d0 = this.attacker.getDistanceSq(entitylivingbase.posX, Boxes.entityBox(entitylivingbase).minY, entitylivingbase.posZ);
        --this.delayCounter;
        if ((this.longMemory || this.attacker.getEntitySenses().canSee((Entity)entitylivingbase)) && this.delayCounter <= 0 && (this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0 || entitylivingbase.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0 || this.attacker.getRNG().nextFloat() < 0.05f)) {
            this.targetX = entitylivingbase.posX;
            this.targetY = Boxes.entityBox(entitylivingbase).minY;
            this.targetZ = entitylivingbase.posZ;
            this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);
            if (this.canPenalize) {
                PathPoint finalPathPoint;
                this.delayCounter += this.failedPathFindingPenalty;
                this.failedPathFindingPenalty = this.attacker.getNavigator().getPath() != null ? ((finalPathPoint = this.attacker.getNavigator().getPath().getFinalPathPoint()) != null && entitylivingbase.getDistanceSq((double)finalPathPoint.xCoord, (double)finalPathPoint.yCoord, (double)finalPathPoint.zCoord) < 1.0 ? 0 : (this.failedPathFindingPenalty += 10)) : (this.failedPathFindingPenalty += 10);
            }
            if (d0 > 1024.0) {
                this.delayCounter += 10;
            } else if (d0 > 256.0) {
                this.delayCounter += 5;
            }
            if (this.attacker.getParasiteStatus() <= 2 && this.attacker.getLLeap() < 1) {
                if ((this.attacker.getDistanceSqToEntity((Entity)entitylivingbase) > this.blockDistance || this.attacker.getAttackCooldownAni() == 0) && this.attacker.getGeneMod(3)) {
                    if (!this.attacker.getNavigator().tryMoveToEntityLiving((Entity)entitylivingbase, this.speedTowardsTarget)) {
                        this.delayCounter += 15;
                    }
                    this.attacker.setParasiteStatus(2);
                    this.attacker.setAttackCooldownAni(0);
                } else {
                    if (!this.attacker.getNavigator().tryMoveToEntityLiving((Entity)entitylivingbase, 1.0)) {
                        this.delayCounter += 15;
                    }
                    this.attacker.setParasiteStatus(1);
                }
            }
        }
        --this.attackTick;
        if (this.attackTick > 0 && this.attacker.isPotionActive(SRPPotions.PIVOT_E)) {
            this.attackTick -= this.attacker.getActivePotionEffect(SRPPotions.PIVOT_E).getAmplifier() * 2;
        }
        this.checkAndPerformAttack(entitylivingbase, d0);
    }

    protected void checkAndPerformAttack(EntityLivingBase target, double distance) {
        double d0 = this.getAttackReachSqr(target);
        if (distance <= d0 && this.attackTick <= 0 && this.attacker.canEntityBeSeen((Entity)target)) {
            this.attackTick = this.attacker.getAttackSpeed();
            this.attacker.attackEntityAsMob((Entity)target);
        }
    }

    protected double getAttackReachSqr(EntityLivingBase attackTarget) {
        return this.attacker.width * 2.0f * this.attacker.width * 2.0f + attackTarget.width;
    }

    protected boolean isTargetParasite(EntityLivingBase target) {
        if (SRPConfigSystems.useOneMind && target instanceof EntityParasiteBase) {
            if (((EntityParasiteBase)target).getAttackTarget() instanceof EntityParasiteBase) {
                this.attacker.setAttackTarget(null);
                return true;
            }
            if (SRPPotions.applySense((EntityLivingBase)this.attacker, 200, this.attacker.getDistanceSqToEntity((Entity)target), SRPConfigSystems.oneMinRangeCap)) {
                this.attacker.setAttackTarget(((EntityParasiteBase)target).getAttackTarget());
            } else {
                this.attacker.setAttackTarget(null);
            }
            return true;
        }
        return false;
    }
}

