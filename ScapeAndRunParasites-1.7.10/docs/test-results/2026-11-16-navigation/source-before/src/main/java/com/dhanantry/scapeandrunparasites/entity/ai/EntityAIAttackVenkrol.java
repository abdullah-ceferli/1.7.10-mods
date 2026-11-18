/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.pathfinding.Path
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class EntityAIAttackVenkrol
extends EntityAIBase {
    World world;
    protected EntityParasiteBase attacker;
    protected int attackTick;
    double speedTowardsTarget;
    boolean longMemory;
    Path path;
    private int delayCounter;
    private double targetX;
    private double targetY;
    private double targetZ;
    protected final int attackInterval = 20;
    private int failedPathFindingPenalty = 0;
    private boolean canPenalize = false;

    public EntityAIAttackVenkrol(EntityParasiteBase creature, double speedIn, boolean useLongMemory) {
        this.attacker = creature;
        this.world = creature.worldObj;
        this.speedTowardsTarget = speedIn;
        this.longMemory = useLongMemory;
        this.setMutexBits(3);
    }

    public boolean shouldExecute() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        if (!this.attacker.shouldWorkTask()) {
            return false;
        }
        if (entitylivingbase == null) {
            return false;
        }
        if (!entitylivingbase.isEntityAlive()) {
            return false;
        }
        if (this.canPenalize) {
            if (--this.delayCounter <= 0) {
                this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);
                return this.path != null;
            }
            return true;
        }
        if (this.path != null) {
            return true;
        }
        return this.getAttackReachSqr(entitylivingbase) >= this.attacker.getDistanceSq(entitylivingbase.posX, Boxes.entityBox(entitylivingbase).minY, entitylivingbase.posZ);
    }

    public boolean continueExecuting() {
        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
        if (!this.attacker.shouldWorkTask()) {
            return false;
        }
        if (entitylivingbase == null) {
            return false;
        }
        if (!entitylivingbase.isEntityAlive()) {
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
        this.attacker.setParasiteStatus(1);
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
        double d0 = this.attacker.getDistanceSq(entitylivingbase.posX, Boxes.entityBox(entitylivingbase).minY, entitylivingbase.posZ);
        --this.delayCounter;
        if ((this.longMemory || this.attacker.getEntitySenses().canSee((Entity)entitylivingbase)) && this.delayCounter <= 0 && (this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0 || entitylivingbase.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0 || this.attacker.getRNG().nextFloat() < 0.05f)) {
            this.targetX = entitylivingbase.posX;
            this.targetY = Boxes.entityBox(entitylivingbase).minY;
            this.targetZ = entitylivingbase.posZ;
            this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);
            if (this.canPenalize) {
                this.delayCounter += this.failedPathFindingPenalty;
                this.failedPathFindingPenalty += 10;
            }
            if (d0 > 1024.0) {
                this.delayCounter += 10;
            } else if (d0 > 256.0) {
                this.delayCounter += 5;
            }
            this.attacker.setParasiteStatus(1);
        }
        this.attacker.setParasiteStatus(1);
        this.attackTick = Math.max(this.attackTick - 1, 0);
        this.checkAndPerformAttack(entitylivingbase, d0);
    }

    protected void checkAndPerformAttack(EntityLivingBase target, double distance) {
        double d0 = this.getAttackReachSqr(target);
        if (distance <= d0 && this.attackTick <= 0) {
            this.attackTick = 20;
            this.attacker.swingArm(EnumHand.MAIN_HAND);
            this.attacker.attackEntityAsMob((Entity)target);
        }
    }

    protected double getAttackReachSqr(EntityLivingBase attackTarget) {
        return this.attacker.width * 2.0f * this.attacker.width * 2.0f + attackTarget.width;
    }
}

