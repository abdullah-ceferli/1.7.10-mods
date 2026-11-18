/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.EntityOrbBoom;
import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityHeblu;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityProjectileAlafhaBall
extends EntitySRPProjectile {
    public EntityProjectileAlafhaBall(World worldIn) {
        super(worldIn);
        this.setSize(0.3f, 0.3f);
    }

    public EntityProjectileAlafhaBall(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
        super(worldIn, shooter, accelX, accelY, accelZ);
        this.setSize(0.3f, 0.3f);
    }

    protected EnumParticleTypes getParticleType() {
        return EnumParticleTypes.EXPLOSION_NORMAL;
    }

    protected void onImpact(RayTraceResult result) {
        if (!this.worldObj.isRemote) {
            DamageSource damagesource = this.shootingEntity == null ? DamageSource.causeThrownDamage((Entity)this, (Entity)this) : DamageSource.causeThrownDamage((Entity)this, (Entity)this.shootingEntity);
            for (EntityLivingBase entitylivingbase : EntityQueries.entities(this.worldObj, EntityLivingBase.class, Boxes.grow(Boxes.entityBox(this), 3.0, 3.0, 3.0))) {
                if (entitylivingbase instanceof EntityParasiteBase) continue;
                SRPPotions.applyStackPotion(SRPPotions.DLER_E, entitylivingbase, 300, 0);
                entitylivingbase.attackEntityFrom(damagesource, SRPAttributes.ALAFHA_ATTACK_DAMAGE);
                this.attackEntityAsMobMinimum((Entity)entitylivingbase, (EntityParasiteBase)this.shootingEntity);
            }
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this.worldObj, (EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.BLOCKS, 4.0f, (1.0f + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2f) * 0.7f);
            this.spawnLingeringCloud();
            this.setDead();
        }
    }

    private void spawnLingeringCloud() {
        if (this.shootingEntity != null && this.shootingEntity instanceof EntityHeblu) {
            EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(this.worldObj, this.posX, this.posY, this.posZ);
            entityareaeffectcloud.setRadius(5.0f, 0.9f);
            entityareaeffectcloud.setDuration(60);
            entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
            entityareaeffectcloud.setOwner((EntityParasiteBase)this.shootingEntity);
            entityareaeffectcloud.addEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.COTH_E, 300, 0, false, true));
            this.worldObj.spawnEntityInWorld((Entity)entityareaeffectcloud);
            this.worldObj.setEntityState((Entity)entityareaeffectcloud, (byte)77);
            EntityOrbBoom orb = new EntityOrbBoom(this.worldObj, (EntityPMalleable)this.shootingEntity, 15, 1);
            orb.copyLocationAndAnglesFrom(entityareaeffectcloud);
            this.worldObj.spawnEntityInWorld((Entity)orb);
            return;
        }
        EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(this.worldObj, this.posX, this.posY, this.posZ);
        entityareaeffectcloud.setRadius(2.0f, 0.5f);
        entityareaeffectcloud.setRadiusOnUse(-0.5f);
        entityareaeffectcloud.setWaitTime(30);
        entityareaeffectcloud.setDuration(60);
        entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
        entityareaeffectcloud.addEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.DLER_E, 360, 0, false, false));
        this.worldObj.spawnEntityInWorld((Entity)entityareaeffectcloud);
    }
}

