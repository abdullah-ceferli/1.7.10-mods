/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityNogla;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;

public class EntityAIDiveBomb
extends EntityAIBase {
    private final EntityLiving host;
    private final int cooldownTicks;
    private final int hoverTicksMax;
    private final double diveSpeed;
    private final float explosionPower;
    private static final double ASCEND_HEIGHT = 20.0;
    private static final double ASCEND_STEP_MAX = 0.6;
    private static final int MAX_ASCEND_TICKS = 120;
    private Phase phase = Phase.IDLE;
    private long nextAllowedTick = 0L;
    private int hoverTicks = 0;
    private int diveTicks = 0;
    private int ascendTicks = 0;
    private double ascendStartY = 0.0;
    private double ascendTargetY = 0.0;
    private double hoverY = 0.0;
    private Vec3d lockedTargetPos = Vec3d.ZERO;

    public EntityAIDiveBomb(EntityLiving host, int cooldownTicks, int hoverTicks, double diveSpeed, float explosionPower) {
        this.host = host;
        this.cooldownTicks = cooldownTicks;
        this.hoverTicksMax = hoverTicks;
        this.diveSpeed = diveSpeed;
        this.explosionPower = explosionPower;
        this.setMutexBits(7);
    }

    public boolean shouldExecute() {
        if (this.host.worldObj.isRemote) {
            return false;
        }
        if (this.host.worldObj.getTotalWorldTime() < this.nextAllowedTick) {
            return false;
        }
        if (!(this.host instanceof EntityNogla)) {
            return false;
        }
        if (!((EntityNogla)this.host).isRicardoVariant()) {
            return false;
        }
        EntityLivingBase tgt = this.host.getAttackTarget();
        return tgt != null && tgt.isEntityAlive();
    }

    public boolean continueExecuting() {
        if (!(this.host instanceof EntityNogla)) {
            return false;
        }
        if (!((EntityNogla)this.host).isRicardoVariant()) {
            return false;
        }
        return this.phase != Phase.IDLE;
    }

    public void startExecuting() {
        this.phase = Phase.ASCEND;
        this.hoverTicks = 0;
        this.diveTicks = 0;
        this.ascendTicks = 0;
        this.ascendStartY = this.host.posY;
        this.ascendTargetY = this.ascendStartY + 20.0;
        this.host.getNavigator().clearPathEntity();
        this.host.fallDistance = 0.0f;
        com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityAccess.set(this.host, true);
        this.host.motionZ = 0.0;
        this.host.motionY = 0.0;
        this.host.motionX = 0.0;
    }

    public void updateTask() {
        if (this.host.worldObj.isRemote) {
            return;
        }
        switch (this.phase) {
            case ASCEND: {
                com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityAccess.set(this.host, true);
                ++this.ascendTicks;
                double remaining = this.ascendTargetY - this.host.posY;
                if (remaining <= 0.05 || this.ascendTicks >= 120) {
                    this.hoverY = this.host.posY;
                    this.enterHover();
                    break;
                }
                Vec3d start = new Vec3d(this.host.posX, this.host.posY, this.host.posZ);
                double step = Math.min(0.6, remaining);
                Vec3d end = new Vec3d(this.host.posX, this.host.posY + step, this.host.posZ);
                RayTraceResult up = this.host.worldObj.rayTraceBlocks(start, end, false, true, false);
                if (up != null && up.typeOfHit == RayTraceResult.Type.BLOCK) {
                    double yAtHit = up.hitVec.yCoord - 0.05;
                    this.host.setPositionAndUpdate(this.host.posX, yAtHit, this.host.posZ);
                    this.hoverY = this.host.posY;
                    this.enterHover();
                    break;
                }
                this.host.setPositionAndUpdate(this.host.posX, this.host.posY + step, this.host.posZ);
                this.host.motionZ = 0.0;
                this.host.motionY = 0.0;
                this.host.motionX = 0.0;
                this.host.fallDistance = 0.0f;
                EntityLivingBase tA = this.host.getAttackTarget();
                if (tA == null) break;
                this.faceTowards(tA.posX, tA.posY + (double)tA.getEyeHeight() * 0.5, tA.posZ);
                break;
            }
            case HOVER: {
                com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityAccess.set(this.host, true);
                double dy = this.hoverY - this.host.posY;
                if (Math.abs(dy) > 0.05) {
                    double nudge = Math.copySign(Math.min(0.2, Math.abs(dy)), dy);
                    this.host.setPositionAndUpdate(this.host.posX, this.host.posY + nudge, this.host.posZ);
                }
                this.host.motionZ = 0.0;
                this.host.motionY = 0.0;
                this.host.motionX = 0.0;
                this.host.fallDistance = 0.0f;
                EntityLivingBase tH = this.host.getAttackTarget();
                if (tH == null || !tH.isEntityAlive()) {
                    this.finish(false);
                    return;
                }
                this.faceTowards(tH.posX, tH.posY + (double)tH.getEyeHeight() * 0.5, tH.posZ);
                ++this.hoverTicks;
                int need = 20;
                if (this.hoverTicks < need) break;
                this.lockedTargetPos = new Vec3d(tH.posX, tH.posY + (double)tH.getEyeHeight() * 0.5, tH.posZ);
                this.phase = Phase.DIVE;
                com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityAccess.set(this.host, true);
                this.diveTicks = 0;
                this.applyDiveVector(this.lockedTargetPos, 1.6);
                break;
            }
            case DIVE: {
                this.host.getNavigator().clearPathEntity();
                com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityAccess.set(this.host, true);
                this.host.fallDistance = 0.0f;
                ++this.diveTicks;
                Vec3d start = new Vec3d(this.host.posX, this.host.posY, this.host.posZ);
                Vec3d next = new Vec3d(this.host.posX + this.host.motionX, this.host.posY + this.host.motionY, this.host.posZ + this.host.motionZ);
                RayTraceResult r = this.host.worldObj.rayTraceBlocks(start, next, false, true, false);
                if (r != null && r.typeOfHit == RayTraceResult.Type.BLOCK) {
                    this.explodeAndFinish();
                    return;
                }
                EntityLivingBase tD = this.host.getAttackTarget();
                if (tD != null && tD.isEntityAlive()) {
                    if (Boxes.intersects(Boxes.entityBox(this.host), Boxes.entityBox(tD))) {
                        this.explodeAndFinish();
                        return;
                    }
                    if (this.host.getDistanceSqToEntity((Entity)tD) < 2.25) {
                        this.explodeAndFinish();
                        return;
                    }
                    Vec3d aim = new Vec3d(tD.posX, tD.posY + (double)tD.getEyeHeight() * 0.5, tD.posZ);
                    double spd = this.computeDiveSpeed();
                    this.applyDiveVector(aim, spd);
                } else {
                    double spd = this.computeDiveSpeed();
                    this.applyDiveVector(this.lockedTargetPos, spd);
                }
                if (this.diveTicks <= 80) break;
                this.explodeAndFinish();
                break;
            }
        }
    }

    private void enterHover() {
        this.phase = Phase.HOVER;
        this.hoverTicks = 0;
        this.host.motionZ = 0.0;
        this.host.motionY = 0.0;
        this.host.motionX = 0.0;
        this.host.velocityChanged = true;
        this.host.fallDistance = 0.0f;
    }

    private double computeDiveSpeed() {
        double base = this.diveSpeed <= 0.0 ? 2.8 : this.diveSpeed;
        double accel = 0.35 * (double)this.diveTicks;
        double cap = Math.max(base, 4.5);
        return Math.min(base + accel, cap);
    }

    private void applyDiveVector(Vec3d target, double speed) {
        Vec3d from = new Vec3d(this.host.posX, this.host.posY, this.host.posZ);
        Vec3d dir = target.subtract(from).normalize();
        this.host.motionX = dir.xCoord * speed;
        this.host.motionY = dir.yCoord * speed;
        this.host.motionZ = dir.zCoord * speed;
        this.host.velocityChanged = true;
        this.faceTowards(this.host.posX + this.host.motionX, this.host.posY + this.host.motionY, this.host.posZ + this.host.motionZ);
    }

    private void explodeAndFinish() {
        this.host.worldObj.newExplosion((Entity)this.host, this.host.posX, this.host.posY, this.host.posZ, this.explosionPower, false, false);
        this.finish(true);
    }

    private void finish(boolean setCd) {
        com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityAccess.set(this.host, false);
        this.host.motionZ = 0.0;
        this.host.motionY = 0.0;
        this.host.motionX = 0.0;
        this.host.velocityChanged = true;
        this.host.fallDistance = 0.0f;
        this.phase = Phase.IDLE;
        if (setCd) {
            this.nextAllowedTick = this.host.worldObj.getTotalWorldTime() + (long)this.cooldownTicks;
        }
    }

    public void resetTask() {
        this.finish(true);
    }

    private void faceTowards(double x, double y, double z) {
        float yaw;
        double dx = x - this.host.posX;
        double dz = z - this.host.posZ;
        this.host.rotationYaw = yaw = (float)(com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)dz, (double)dx) * 57.29577951308232) - 90.0f;
        this.host.rotationYawHead = yaw;
        this.host.renderYawOffset = yaw;
    }

    private static enum Phase {
        IDLE,
        ASCEND,
        HOVER,
        DIVE;

    }
}

