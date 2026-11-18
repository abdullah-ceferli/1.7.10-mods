/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.projectile.EntityFireball
 *  net.minecraft.util.DamageSource
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class EntitySRPProjectile
extends EntityFireball {
    public EntitySRPProjectile(World worldIn) {
        super(worldIn);
        this.setSize(0.3f, 0.3f);
    }

    public EntitySRPProjectile(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
        super(worldIn, shooter, accelX, accelY, accelZ);
        this.setSize(0.3f, 0.3f);
    }

    public boolean isBurning() {
        return false;
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    protected boolean attackEntityAsMobMinimum(Entity entityIn, EntityParasiteBase attacker) {
        if (entityIn == null || attacker == null) {
            return false;
        }
        if (!attacker.isEntityAlive()) {
            return false;
        }
        if (entityIn instanceof EntityLivingBase && !(entityIn instanceof EntityParasiteBase)) {
            return attacker.attackEntityAsMobMinimum((EntityLivingBase)entityIn, attacker.getMiniDamage());
        }
        return false;
    }
}

