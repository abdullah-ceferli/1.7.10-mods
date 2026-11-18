/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.init.MobEffects
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.deterrent;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;

import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityRof
extends EntityPStationary {
    private String[] mobL;
    public EntityLivingBase targetScent;
    public int minmob;
    public int maxmob;
    private float attackTimer;
    private boolean upT;

    public EntityRof(World worldIn) {
        super(worldIn);
        this.setSize(1.5f, 4.6f);
        this.experienceValue = 0;
        this.type = (byte)40;
        this.buriedT = 5.7;
        this.damageCap = SRPConfig.turretCap;
        this.pointCap = SRPConfig.turretPointCap;
        this.pointReduction = SRPConfig.turretPointRed;
        this.chanceLearn = SRPConfig.turretChanceLe;
        this.chanceLearnFire = SRPConfig.turretChanceLeFire;
        this.DamageTypeCap = SRPConfig.turretPointDamCap;
        this.MiniDamage = SRPConfig.turretMinDamage;
        this.regen = SRPConfig.turretRegen;
        this.valueEvDeath = 0;
        this.delayBuried = 40;
    }

    @Override
    public int getParasiteIDRegister() {
        return 308;
    }

    protected void initEntityAI() {
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100.0);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.upT) {
            this.attackTimer += 0.2f;
            if (this.attackTimer > 3.0f) {
                this.upT = false;
            }
        } else {
            this.attackTimer -= 0.1f;
        }
        if (!this.worldObj.isRemote) {
            if (this.getAttackTarget() != null && this.getAttackTarget().getDistanceSqToEntity((Entity)this) > 256.0) {
                this.worldObj.setEntityState((Entity)this, (byte)51);
                this.up = true;
            }
            if (this.ticksExisted > 160 && this.targetScent == null) {
                this.worldObj.setEntityState((Entity)this, (byte)51);
                this.up = true;
            }
            if (this.ticksExisted == 100) {
                this.upT = true;
                this.attackTimer = 0.0f;
                this.worldObj.setEntityState((Entity)this, (byte)112);
            }
            if (!this.up && this.ticksExisted > 120 && !this.buried() && this.targetScent != null) {
                for (int i = 0; i <= this.maxmob; ++i) {
                    this.spawnWaves();
                    if (i < this.minmob || this.rand.nextInt(3) != 0) continue;
                    this.targetScent = null;
                }
                this.playSound(SRPSounds.ROF_SPIT_OUT, 1.2f, this.getSoundPitch());
                this.targetScent = null;
            }
        }
    }

    @Override
    protected void retreat(boolean dead) {
        if (this.up) {
            this.playSound(SRPSounds.ROF_EMERGE, 1.0f, this.getSoundPitch() - 0.1f);
            this.setParasiteStatus(3);
            this.buried += this.getBuriedSpeed();
            if (this.buried > this.buriedT + 0.7 && !this.worldObj.isRemote) {
                this.setDead();
            }
        }
    }

    @Override
    public double getBuriedSpeed() {
        return 0.12;
    }

    public void setMob(String[] in) {
        this.mobL = in;
    }

    private EntityLiving getMobFromList() {
        String mob = this.mobL[this.rand.nextInt(this.mobL.length)];
        return (EntityLiving)EntityList.createEntityByIDFromName((ResourceLocation)new ResourceLocation(mob), (World)this.worldObj);
    }

    public int spawnWaves() {
        if (this.mobL == null) {
            return 0;
        }
        EntityLiving out = this.getMobFromList();
        if (out == null) {
            return 0;
        }
        DifficultyAccess.initialSpawn(out, DifficultyAccess.at(this.worldObj, EntityQueries.position(out)), null);
        out.setAttackTarget(this.getAttackTarget());
        double d0 = (float)this.posX + this.worldObj.rand.nextFloat();
        double d1 = (float)this.posY + this.worldObj.rand.nextFloat();
        double d2 = (float)this.posZ + this.worldObj.rand.nextFloat();
        double d3 = d0 - this.posX;
        double d4 = d1 - this.posY;
        double d5 = d2 - this.posZ;
        double d6 = MathHelper.sqrt_double((double)(d3 * d3 + d4 * d4 + d5 * d5));
        d3 /= d6;
        d4 /= d6;
        d5 /= d6;
        double d7 = 0.5 / (d6 / 4.0 + 0.1);
        d4 = d4 * d7 * 6.0;
        out.copyLocationAndAnglesFrom((Entity)this);
        out.posY = this.posY + (double)this.height + 0.5;
        this.setMotion((EntityLivingBase)out, d3 *= (d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3f)), d4, d5 *= d7, 0.03, 1.2);
        this.worldObj.spawnEntityInWorld((Entity)out);
        out.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.RAGE_E, 1200, 1, false, false));
        out.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.field_76444_x, 100, 15, false, false));
        return 1;
    }

    public void setMotion(EntityLivingBase in, double xSpeedIn, double ySpeedIn, double zSpeedIn, double capX, double capY) {
        xSpeedIn = Math.min(xSpeedIn, capX);
        ySpeedIn = Math.min(ySpeedIn, capY);
        zSpeedIn = Math.min(zSpeedIn, capX);
        in.motionX = xSpeedIn * (Math.random() * 2.0 - 1.0);
        in.motionY = ySpeedIn;
        in.motionZ = zSpeedIn * (Math.random() * 2.0 - 1.0);
    }

    public float getEyeHeight() {
        return 1.8f;
    }

    @SideOnly(value=Side.CLIENT)
    public float getAttackTimer() {
        return this.attackTimer;
    }

    public void setminMax(int minn, int maxx) {
        this.minmob = minn;
        this.maxmob = maxx;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void handleHealthUpdate(byte id) {
        if (id == 112) {
            this.upT = true;
            this.attackTimer = 0.0f;
        } else {
            super.handleHealthUpdate(id);
        }
    }
}

