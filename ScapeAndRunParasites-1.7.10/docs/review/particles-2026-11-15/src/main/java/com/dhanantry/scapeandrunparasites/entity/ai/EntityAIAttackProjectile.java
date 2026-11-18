/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.world.World;

public class EntityAIAttackProjectile
extends EntityAIBase {
    private final EntityParasiteBase parentEntity;
    private int attackTimer;
    private int cooldown;
    private int shootingTimes;
    private int shootingUpdate;
    private int tickInterval;
    private int heighA;
    private boolean canShootH;

    public EntityAIAttackProjectile(EntityParasiteBase parent, int cooldown, int tickInter, int shootingTim) {
        this(parent, cooldown, tickInter, shootingTim, false);
    }

    public EntityAIAttackProjectile(EntityParasiteBase parent, int cooldown, int tickInter, int shootingTim, boolean homming) {
        this.parentEntity = parent;
        this.attackTimer = 0;
        this.cooldown = cooldown;
        this.shootingTimes = shootingTim;
        this.shootingUpdate = 0;
        this.tickInterval = tickInter;
        this.canShootH = homming;
    }

    public boolean shouldExecute() {
        return this.parentEntity.getAttackTarget() != null;
    }

    public void resetTask() {
        this.shootingUpdate = 0;
        this.attackTimer = 0;
    }

    public void updateTask() {
        if (this.parentEntity.getAttackTarget() == null || this.parentEntity.getParasiteStatus() >= 3) {
            this.attackTimer = 0;
        } else if (this.parentEntity.getAttackTarget().isDead) {
            this.attackTimer = 0;
        } else {
            EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
            if (this.parentEntity.getParasiteStatus() == 0) {
                this.parentEntity.setParasiteStatus(1);
            }
            if (entitylivingbase.getDistanceSqToEntity((Entity)this.parentEntity) < 4225.0 && this.parentEntity.canEntityBeSeen((Entity)entitylivingbase)) {
                ++this.attackTimer;
                if (this.parentEntity.isPotionActive(SRPPotions.RAGE_E)) {
                    ++this.attackTimer;
                }
                if (this.attackTimer == this.cooldown - 10) {
                    EntityCanShoot shooter = (EntityCanShoot)((Object)this.parentEntity);
                    shooter.playProjSound();
                }
                if (this.attackTimer > this.cooldown) {
                    if (this.shootingTimes > this.shootingUpdate) {
                        if (this.attackTimer % this.tickInterval == 0) {
                            this.shoot(entitylivingbase, this.parentEntity.worldObj);
                            ++this.shootingUpdate;
                        }
                    } else {
                        this.attackTimer = 0;
                        this.shootingUpdate = 0;
                    }
                }
            } else if (this.attackTimer > 0) {
                --this.attackTimer;
            }
        }
    }

    private void shoot(EntityLivingBase entitylivingbase, World world) {
        this.heighA = !entitylivingbase.onGround && this.canShootH ? ++this.heighA : 0;
        if (this.heighA <= 5) {
            double d1 = 4.0;
            Vec3d vec3d = this.parentEntity.getLook(1.0f);
            double d2 = entitylivingbase.posX - (this.parentEntity.posX + vec3d.xCoord);
            double d3 = Boxes.entityBox(entitylivingbase).minY + (double)(entitylivingbase.height / 2.0f) - (0.5 + this.parentEntity.posY + (double)(this.parentEntity.height / 2.0f));
            double d4 = entitylivingbase.posZ - (this.parentEntity.posZ + vec3d.zCoord);
            EntityCanShoot shooter = (EntityCanShoot)((Object)this.parentEntity);
            Entity entitylargefireball = shooter.getProj(d2, d3, d4);
            entitylargefireball.posX = this.parentEntity.posX + vec3d.xCoord;
            entitylargefireball.posY = this.parentEntity.posY + (double)this.parentEntity.getEyeHeight() - 0.2;
            entitylargefireball.posZ = this.parentEntity.posZ + vec3d.zCoord;
            world.spawnEntityInWorld(entitylargefireball);
            this.parentEntity.resetIdleTime();
        }
    }
}

