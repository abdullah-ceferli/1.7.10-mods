/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IRangedAttackMob
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.util.math.MathHelper
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;

public class EntityAIAttackRangedStatus
extends EntityAIBase {
    private final EntityParasiteBase entityHost;
    private final IRangedAttackMob rangedAttackEntityHost;
    private EntityLivingBase attackTarget;
    private int rangedAttackTime = -1;
    private final double entityMoveSpeed;
    private int seeTime;
    private final int maxRangedAttackTime;
    private final float attackRadius;
    private final float maxAttackDistance;
    private boolean pullAway;

    public EntityAIAttackRangedStatus(IRangedAttackMob attacker, double movespeed, int maxAttackTime, float maxAttackDistanceIn, boolean away) {
        if (!(attacker instanceof EntityLivingBase)) {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        this.rangedAttackEntityHost = attacker;
        this.entityHost = (EntityParasiteBase)attacker;
        this.entityMoveSpeed = movespeed;
        this.maxRangedAttackTime = maxAttackTime;
        this.attackRadius = maxAttackDistanceIn;
        this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
        this.pullAway = away;
        this.setMutexBits(3);
    }

    public boolean shouldExecute() {
        if (!this.entityHost.onGround) {
            return false;
        }
        EntityLivingBase entitylivingbase = this.entityHost.getAttackTarget();
        if (entitylivingbase == null || this.entityHost.shouldWorkTask()) {
            return false;
        }
        this.attackTarget = entitylivingbase;
        return true;
    }

    public boolean continueExecuting() {
        return (this.shouldExecute() || !this.entityHost.getNavigator().noPath()) && !this.entityHost.shouldWorkTask();
    }

    public void resetTask() {
    }

    public void updateTask() {
        if (this.entityHost.srpTicks == 10) {
            if (this.entityHost.getGeneMod(3)) {
                this.entityHost.setParasiteStatus(2);
            } else {
                this.entityHost.setParasiteStatus(1);
            }
        }
        if (this.attackTarget == null) {
            this.resetTask();
        } else if (!this.attackTarget.isEntityAlive()) {
            this.resetTask();
        } else {
            double follo;
            boolean flag2;
            double d0 = this.entityHost.getDistanceSq(this.attackTarget.posX, Boxes.entityBox(this.attackTarget).minY, this.attackTarget.posZ);
            boolean flag = this.entityHost.getEntitySenses().canSee((Entity)this.attackTarget);
            boolean bl = flag2 = d0 <= (double)this.maxAttackDistance;
            this.seeTime = flag ? ++this.seeTime : 0;
            if (this.entityHost.srpTicks == 10 && d0 > (follo = this.entityHost.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue()) * follo) {
                this.entityHost.setAttackTarget(null);
                return;
            }
            if (flag2 && this.seeTime >= 10) {
                this.entityHost.getNavigator().clearPathEntity();
            } else {
                this.entityHost.getNavigator().tryMoveToEntityLiving((Entity)this.attackTarget, this.entityMoveSpeed);
            }
            if (d0 <= 25.0 && this.pullAway) {
                double deltaX = this.attackTarget.posX - this.entityHost.posX;
                double deltaY = this.attackTarget.posY - this.entityHost.posY;
                double deltaZ = this.attackTarget.posZ - this.entityHost.posZ;
                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                if (distance == 0.0) {
                    return;
                }
                double strength = 0.08;
                this.entityHost.motionX -= (deltaX /= distance) * strength;
                this.entityHost.motionY -= (deltaY /= distance) * strength;
                this.entityHost.motionZ -= (deltaZ /= distance) * strength;
            }
            this.entityHost.getLookHelper().setLookPositionWithEntity((Entity)this.attackTarget, 30.0f, 30.0f);
            if (this.entityHost.isPotionActive(SRPPotions.RAGE_E)) {
                --this.rangedAttackTime;
            }
            if (--this.rangedAttackTime <= 0) {
                if (!flag) {
                    return;
                }
                if (flag && flag2) {
                    float f = MathHelper.sqrt_double((double)d0) / this.attackRadius;
                    float lvt_5_1_ = MathHelper.clamp_float((float)f, (float)0.1f, (float)1.0f);
                    this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget, lvt_5_1_);
                    this.entityHost.resetIdleTime();
                    this.rangedAttackTime = this.maxRangedAttackTime;
                }
            } else if (this.rangedAttackTime < 0) {
                // empty if block
            }
        }
    }
}

