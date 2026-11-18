/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityProjectileElviaBall
extends EntitySRPProjectile {
    public EntityProjectileElviaBall(World worldIn) {
        super(worldIn);
        this.setSize(0.3f, 0.3f);
    }

    public EntityProjectileElviaBall(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
        super(worldIn, shooter, accelX, accelY, accelZ);
        this.setSize(0.3f, 0.3f);
    }

    protected EnumParticleTypes getParticleType() {
        return EnumParticleTypes.EXPLOSION_NORMAL;
    }

    protected void onImpact(RayTraceResult result) {
        if (!this.worldObj.isRemote) {
            if (result.entityHit != null && result.entityHit instanceof EntityLivingBase) {
                if (result.entityHit instanceof EntityParasiteBase && !(result.entityHit instanceof EntityNak)) {
                    this.setDead();
                    return;
                }
                DamageSource damagesource = this.shootingEntity == null ? DamageSource.causeThrownDamage((Entity)this, (Entity)this) : DamageSource.causeThrownDamage((Entity)this, (Entity)this.shootingEntity);
                result.entityHit.attackEntityFrom(damagesource, (float)SRPAttributes.ELVIA_ATTACK_DAMAGE);
                this.attackEntityAsMobMinimum(result.entityHit, (EntityParasiteBase)this.shootingEntity);
            }
            this.setDead();
        }
    }
}

