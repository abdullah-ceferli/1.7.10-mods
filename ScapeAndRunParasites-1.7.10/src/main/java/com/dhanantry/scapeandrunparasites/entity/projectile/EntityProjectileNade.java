/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityNade;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityProjectileNade
extends EntitySRPProjectile {
    private int duration;
    private int fuse;

    public EntityProjectileNade(World worldIn) {
        super(worldIn);
        this.setSize(0.3f, 0.3f);
    }

    public EntityProjectileNade(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ, int fuse, int duration) {
        super(worldIn, shooter, accelX, accelY, accelZ);
        this.setSize(0.3f, 0.3f);
        this.fuse = fuse;
        this.duration = duration;
    }

    protected EnumParticleTypes getParticleType() {
        return EnumParticleTypes.SLIME;
    }

    protected void onImpact(RayTraceResult result) {
        if (!this.worldObj.isRemote) {
            if (this.shootingEntity == null) {
                return;
            }
            if (!this.shootingEntity.isEntityAlive()) {
                return;
            }
            EntityNade nade = new EntityNade(this.worldObj, this.fuse, this.duration);
            if (this.shootingEntity instanceof EntityParasiteBase) {
                nade.setFatherS((EntityParasiteBase)this.shootingEntity);
            }
            nade.copyLocationAndAnglesFrom((Entity)this);
            this.worldObj.spawnEntityInWorld((Entity)nade);
            this.setDead();
        }
    }
}

