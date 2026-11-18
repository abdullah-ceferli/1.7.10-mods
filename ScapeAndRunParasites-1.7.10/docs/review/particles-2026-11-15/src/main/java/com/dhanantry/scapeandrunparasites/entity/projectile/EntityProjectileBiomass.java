/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.projectile.EntityFireball
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityBiomass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityProjectileBiomass
extends EntityFireball {
    private EntityLivingBase target;
    private String parasite;
    private int point;
    private int kin;

    public EntityProjectileBiomass(World worldIn) {
        super(worldIn);
        this.setSize(0.3f, 0.3f);
    }

    public EntityProjectileBiomass(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
        super(worldIn, shooter, accelX, accelY, accelZ);
        this.setSize(0.3f, 0.3f);
    }

    public void setParasite(String in, int points, int type) {
        this.parasite = in;
        this.point = points;
        this.target = ((EntityParasiteBase)this.shootingEntity).getAttackTarget();
        this.kin = type;
    }

    public boolean isBurning() {
        return false;
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    protected EnumParticleTypes getParticleType() {
        return EnumParticleTypes.EXPLOSION_NORMAL;
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    protected void onImpact(RayTraceResult result) {
        if (!this.worldObj.isRemote) {
            if (this.shootingEntity != null && this.shootingEntity.isEntityAlive() && this.shootingEntity instanceof EntityParasiteBase) {
                EntityBiomass entityout = new EntityBiomass(this.worldObj, (EntityParasiteBase)this.shootingEntity, this.target);
                entityout.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
                entityout.setFuse(80);
                entityout.setParasite(this.parasite, this.point);
                entityout.setSkin(this.kin);
                EntityCanSummon father = (EntityCanSummon)this.shootingEntity;
                father.setActualParasites(this.point);
                father.addID(entityout.getEntityId(), this.point);
                this.worldObj.spawnEntityInWorld((Entity)entityout);
            }
            this.setDead();
        }
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted > 140) {
            this.setDead();
        }
    }
}

