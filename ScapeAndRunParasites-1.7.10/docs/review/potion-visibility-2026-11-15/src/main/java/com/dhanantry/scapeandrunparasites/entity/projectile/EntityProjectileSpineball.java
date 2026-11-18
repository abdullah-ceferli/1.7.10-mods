/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.MobEffects
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityEmanaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityEmana;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityProjectileSpineball
extends EntitySRPProjectile {
    private float damage;
    private int duration;
    private int amp;
    private double item;

    public EntityProjectileSpineball(World worldIn) {
        super(worldIn);
        this.setSize(0.3f, 0.3f);
    }

    public EntityProjectileSpineball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ, float projDamage) {
        super(worldIn, shooter, accelX, accelY, accelZ);
        this.setSize(0.3f, 0.3f);
        this.damage = projDamage;
    }

    protected EnumParticleTypes getParticleType() {
        return EnumParticleTypes.SLIME;
    }

    public void setDurationAmplifier(int duration, int amplifier) {
        this.duration = duration * 20;
        this.amp = amplifier - 1;
    }

    public void setGearDamage(double in) {
        this.item = in;
    }

    protected void onImpact(RayTraceResult result) {
        if (!this.worldObj.isRemote) {
            if (result.entityHit != null && result.entityHit instanceof EntityLivingBase) {
                EntityLivingBase target = (EntityLivingBase)result.entityHit;
                if (target instanceof EntityParasiteBase && !(target instanceof EntityNak)) {
                    this.setDead();
                    return;
                }
                boolean primitive = false;
                EntityParasiteBase shooter = (EntityParasiteBase)this.shootingEntity;
                if (shooter instanceof EntityEmana) {
                    primitive = true;
                }
                DamageSource damagesource = this.shootingEntity == null ? DamageSource.causeThrownDamage((Entity)this, (Entity)this) : DamageSource.causeThrownDamage((Entity)this, (Entity)this.shootingEntity);
                target.attackEntityFrom(damagesource, this.damage);
                target.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.poison, this.duration, this.amp));
                this.attackEntityAsMobMinimum((Entity)target, (EntityParasiteBase)this.shootingEntity);
                this.damageArmor(target, this.item);
                if (target.getHealth() <= 0.0f && primitive && shooter.isEntityAlive()) {
                    double k = shooter.getKillC();
                    shooter.setKillC(k += 1.0);
                    shooter.particleStatus((byte)5);
                    if (k > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
                        ParasiteEventEntity.spawnNext(shooter, new EntityEmanaAdapted(this.worldObj), true, true);
                    }
                }
            }
            this.setDead();
        }
    }

    private void damageArmor(EntityLivingBase target, double percen) {
        ArrayList<ItemStack> off = new ArrayList<ItemStack>();
        Iterable gear = target.getArmorInventoryList();
        if (gear != null && !gear.equals(Collections.emptyList())) {
            for (ItemStack part : gear) {
                if (part.isEmpty() || !part.isItemStackDamageable()) continue;
                off.add(part);
            }
            if (!off.isEmpty()) {
                for (ItemStack part : off) {
                    if (!((double)part.getMaxDamage() * SRPConfigSystems.corrNot < (double)(part.getMaxDamage() - part.getItemDamageForDisplay()))) continue;
                    part.damageItem((int)((double)part.getMaxDamage() * percen), target);
                }
            }
        }
    }
}

