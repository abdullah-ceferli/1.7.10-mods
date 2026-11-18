/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.BlockPos
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHuman;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;

public class EntityAISoundEaterStalk
extends EntityAIBase {
    private final EntityInfHuman mob;
    private final double hearingRangeSq;
    private int ticksSinceNoise = 0;
    private static final int QUIET_THRESHOLD_TICKS = 100;

    public EntityAISoundEaterStalk(EntityInfHuman mob, double hearingRange) {
        this.mob = mob;
        this.hearingRangeSq = hearingRange * hearingRange;
        this.setMutexBits(0);
    }

    public boolean shouldExecute() {
        return this.mob.getSkin() == 111;
    }

    public boolean continueExecuting() {
        return this.mob.getSkin() == 111 && !this.mob.isDead;
    }

    public void startExecuting() {
        this.ticksSinceNoise = 0;
    }

    public void resetTask() {
        this.ticksSinceNoise = 0;
    }

    public void updateTask() {
        BlockPos soundPos;
        if (this.mob.worldObj.isRemote) {
            return;
        }
        this.mob.tickSoundMemory();
        EntityPlayer nearest = this.mob.worldObj.getClosestPlayerToEntity((Entity)this.mob, Math.sqrt(this.hearingRangeSq));
        if (nearest != null && (nearest.isSpectator() || nearest.capabilities.isCreativeMode)) {
            nearest = null;
        }
        boolean noisy = false;
        boolean canSee = false;
        if (nearest != null && nearest.isEntityAlive() && this.mob.getDistanceSqToEntity((Entity)nearest) <= this.hearingRangeSq) {
            noisy = this.isTargetNoisy((EntityLivingBase)nearest);
            canSee = this.mob.canEntityBeSeen((Entity)nearest);
        }
        if (nearest == null || !nearest.isEntityAlive() || this.mob.getDistanceSqToEntity((Entity)nearest) > this.hearingRangeSq) {
            ++this.ticksSinceNoise;
            if (this.ticksSinceNoise >= 100) {
                this.forceDropAggro();
            }
        } else if (noisy) {
            this.ticksSinceNoise = 0;
            soundPos = new BlockPos(nearest.posX, nearest.posY, nearest.posZ);
            this.mob.notifyHeardSound(soundPos, 100);
            if (canSee) {
                if (this.mob.getAttackTarget() != nearest) {
                    this.mob.setAttackTarget((EntityLivingBase)nearest);
                }
            } else if (this.mob.getAttackTarget() == nearest) {
                this.mob.setAttackTarget(null);
                this.mob.getNavigator().clearPathEntity();
            }
        } else if (!canSee) {
            if (this.mob.getAttackTarget() instanceof EntityPlayer) {
                this.mob.setAttackTarget(null);
                this.mob.getNavigator().clearPathEntity();
            }
            ++this.ticksSinceNoise;
            if (this.ticksSinceNoise >= 100) {
                this.forceDropAggro();
            }
        } else {
            this.ticksSinceNoise = 0;
        }
        if (this.mob.getAttackTarget() == null && (soundPos = this.mob.getHeardSoundPos()) != null) {
            double sz;
            double sy;
            double sx = (double)soundPos.getX() + 0.5;
            if (this.mob.getDistanceSq(sx, sy = (double)soundPos.getY(), sz = (double)soundPos.getZ() + 0.5) < 2.0) {
                this.mob.clearHeardSound();
            } else {
                this.mob.getNavigator().tryMoveToXYZ(sx, sy, sz, 1.2);
            }
        }
    }

    private void forceDropAggro() {
        this.mob.setAttackTarget(null);
        this.mob.setRevengeTarget(null);
        this.mob.getNavigator().clearPathEntity();
        this.ticksSinceNoise = 0;
    }

    private boolean isTargetNoisy(EntityLivingBase t) {
        if (!(t instanceof EntityPlayer)) {
            return false;
        }
        EntityPlayer p = (EntityPlayer)t;
        if (p.isSpectator() || p.capabilities.isCreativeMode) {
            return false;
        }
        if (p.isSneaking()) {
            return false;
        }
        if (p.isSprinting()) {
            return true;
        }
        if (p.isHandActive()) {
            return true;
        }
        if (p.isSwingInProgress || p.swingProgressInt > 0) {
            return true;
        }
        if (Math.abs(p.motionY) > 0.08) {
            return true;
        }
        double dx = p.motionX;
        double dz = p.motionZ;
        double speedSq = dx * dx + dz * dz;
        return speedSq > 0.003;
    }
}

