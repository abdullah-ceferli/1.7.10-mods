/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityProjectileEffects
extends EntitySRPProjectile {
    private ArrayList<PotionEffect> eff;

    public EntityProjectileEffects(World worldIn) {
        super(worldIn);
        this.setSize(0.3f, 0.3f);
        this.eff = new ArrayList();
    }

    public EntityProjectileEffects(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
        super(worldIn, shooter, accelX, accelY, accelZ);
        this.setSize(0.3f, 0.3f);
        this.eff = new ArrayList();
    }

    protected EnumParticleTypes getParticleType() {
        return EnumParticleTypes.EXPLOSION_NORMAL;
    }

    public void addEffect(PotionEffect in) {
        this.eff.add(in);
    }

    public void onUpdate() {
        super.onUpdate();
        if (!this.worldObj.isRemote) {
            this.motionY -= 0.1;
        }
    }

    protected void onImpact(RayTraceResult result) {
        if (!this.worldObj.isRemote) {
            EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(this.worldObj, this.posX, this.posY - 1.0, this.posZ);
            entityareaeffectcloud.setRadius(2.3f, 0.5f);
            entityareaeffectcloud.setRadiusOnUse(-0.5f);
            entityareaeffectcloud.setWaitTime(10);
            entityareaeffectcloud.setDuration(100);
            entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
            for (PotionEffect in : this.eff) {
                entityareaeffectcloud.addEffect(in);
            }
            this.worldObj.spawnEntityInWorld((Entity)entityareaeffectcloud);
            this.setDead();
        }
    }
}

