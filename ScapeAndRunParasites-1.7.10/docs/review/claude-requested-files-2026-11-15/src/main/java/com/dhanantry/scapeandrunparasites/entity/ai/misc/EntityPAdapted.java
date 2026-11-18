/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.passive.EntityVillager
 *  net.minecraft.entity.passive.EntityWaterMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.fml.common.eventhandler.Event$Result
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class EntityPAdapted
extends EntityPMalleable {
    public EntityPAdapted(World worldIn) {
        super(worldIn);
        this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityPlayer>(this, EntityPlayer.class, 0, SRPConfig.adaptedWalls, false, null, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
        if (SRPConfig.canOrbAttack) {
            this.tasks.addTask(2, (EntityAIBase)new EntityAISkill(this, 80, 4, false, 21));
        }
        if (SRPConfig.mobattacking) {
            this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityLiving>(this, EntityLiving.class, 0, SRPConfig.adaptedWalls, false, entity -> !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager) && !ParasiteEventEntity.checkEntity((EntityLivingBase)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite), SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
        }
        this.experienceValue = SRPAttributes.XP_ADAPTED;
        this.canD = SRPConfig.adapteddespawn;
        this.damageCap = SRPConfig.adaptedCap;
        this.canModRender = 1;
        this.type = (byte)41;
        this.fuseOrb = 16;
        this.orbStartTimer = 15;
        this.foodSteal = SRPConfig.adaptedFoodSteal;
        this.orbItemCool = SRPConfig.adaptedItemOrbCooldown * 20;
        this.pointCap = SRPConfig.adaptedPointCap;
        this.pointReduction = SRPConfig.adaptedPointRed;
        this.chanceLearn = SRPConfig.adaptedChanceLe;
        this.chanceLearnFire = SRPConfig.adaptedChanceLeFire;
        this.DamageTypeCap = SRPConfig.adaptedPointDamCap;
        this.MiniDamage = SRPConfig.adaptedMinDamage;
        this.regen = SRPConfig.adaptedRegen * SRPConfig.globalHealthMultiplier;
        this.oneMindDeathValue = SRPConfig.adaptedOneMindDeathV;
        this.regenEff = 10;
        this.foodRott = SRPConfig.adaptedFoodChance;
        this.foodRootNumber = SRPConfig.adaptedFoodAmount;
        this.cothSpread = SRPConfigSystems.cothAdapted;
        this.valueEvDeath = SRPConfig.adaptedLoosingEPValue;
        this.setScentHPMultiplier(0.75f);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

    @Override
    protected void despawnEntity() {
        if (!SRPConfigSystems.useEvolution) {
            super.despawnEntity();
        } else if (!this.worldObj.isRemote) {
            Event.Result result;
            boolean flag = this.canDespawn();
            if (this.isNoDespawnRequired()) {
                this.entityAge = 0;
            } else if ((this.entityAge & 0x1F) == 31 && (result = ForgeEventFactory.canEntityDespawn((EntityLiving)this)) != Event.Result.DEFAULT) {
                if (result == Event.Result.DENY) {
                    this.entityAge = 0;
                } else {
                    SRPSaveData data = SRPSaveData.get(this.worldObj, 22);
                    if (this.ticksExisted > 10) {
                        data.setTotalKills(this.worldObj.provider.dimensionId, SRPConfigSystems.valueEvolutionDespawn, true, this.worldObj, true, 34);
                    }
                    this.spawnCyst();
                    this.storeBefDes();
                    this.setDead();
                }
            } else {
                EntityPlayer entity = this.worldObj.getClosestPlayerToEntity((Entity)this, -1.0);
                if (entity != null) {
                    SRPSaveData data;
                    double d0 = entity.posX - this.posX;
                    double d1 = entity.posY - this.posY;
                    double d2 = entity.posZ - this.posZ;
                    double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                    if (flag && d3 > 16384.0) {
                        data = SRPSaveData.get(this.worldObj, 23);
                        if (this.ticksExisted > 10) {
                            data.setTotalKills(this.worldObj.provider.dimensionId, SRPConfigSystems.valueEvolutionDespawn, true, this.worldObj, true, 35);
                        }
                        this.spawnCyst();
                        this.storeBefDes();
                        this.setDead();
                    }
                    if (this.entityAge > 600 && this.rand.nextInt(800) == 0 && d3 > 1024.0 && flag) {
                        data = SRPSaveData.get(this.worldObj, 24);
                        if (this.ticksExisted > 10) {
                            data.setTotalKills(this.worldObj.provider.dimensionId, SRPConfigSystems.valueEvolutionDespawn, true, this.worldObj, true, 36);
                        }
                        this.spawnCyst();
                        this.storeBefDes();
                        this.setDead();
                    } else if (d3 < 1024.0) {
                        this.entityAge = 0;
                    }
                }
            }
        }
    }

    @Override
    protected void fearPlayer(EntityLivingBase player) {
    }

    protected void fearPlayer(EntityLivingBase player, float damageDealt) {
        if (player == null || player.worldObj.isRemote) {
            return;
        }
        if (damageDealt <= 8.0f) {
            return;
        }
        int level = 1 + Math.max(0, (int)Math.floor((damageDealt - 8.0f) / 4.0f));
        int cap = 3;
        level = Math.min(level, cap);
        int duration = 300 + 40 * (level - 1);
        duration = MathHelper.clamp_int((int)duration, (int)200, (int)500);
        int amplifier = level - 1;
        player.addPotionEffect(new PotionEffect(SRPPotions.FEAR_E, duration, amplifier, false, true));
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        float afterA;
        float afterH;
        float dealt;
        if (!(entityIn instanceof EntityLivingBase)) {
            return super.attackEntityAsMob(entityIn);
        }
        EntityLivingBase target = (EntityLivingBase)entityIn;
        float beforeH = target.getHealth();
        float beforeA = target.getAbsorptionAmount();
        boolean hit = super.attackEntityAsMob(entityIn);
        if (hit && !this.worldObj.isRemote && (dealt = Math.max(0.0f, beforeH + beforeA - ((afterH = target.getHealth()) + (afterA = target.getAbsorptionAmount())))) > 0.0f) {
            this.fearPlayer(target, dealt);
        }
        return hit;
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        EntityLivingBase attacker;
        if (this.worldObj.isRemote) {
            return super.attackEntityFrom(source, amount);
        }
        if (source.getEntity() instanceof EntityLivingBase && (attacker = (EntityLivingBase)source.getEntity()).isPotionActive(SRPPotions.KILLADA_E)) {
            int amp = Objects.requireNonNull(attacker.getActivePotionEffect(SRPPotions.KILLADA_E)).getAmplifier();
            float totalRed = MathHelper.clamp_float((float)(SRPConfigSystems.parasiteKillingReduction * ((float)amp + 1.0f)), (float)0.0f, (float)0.95f);
            float reduced = amount * (1.0f - totalRed);
            return super.attackEntityFrom(source, Math.max(0.0f, reduced));
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    protected void attackEntityFromEffects(int range, int count) {
        this.particleStatus((byte)52);
        double i1 = MathHelper.floor_double((double)(this.posY + 0.1));
        double l1 = this.posX;
        double i2 = this.posZ;
        int counttt = 0;
        for (int k2 = -1 * range; k2 <= range && SRPConfig.paraGore; ++k2) {
            for (int l2 = -1 * range; l2 <= range; ++l2) {
                double i3 = l1 + (double)k2;
                double l = i2 + (double)l2;
                BlockPos blockpos = new BlockPos(i3, i1, l);
                Block block = BlockStates.get(this.worldObj, blockpos).getBlock();
                Block blockDown = BlockStates.get(this.worldObj, blockpos.down()).getBlock();
                if (block != Blocks.air || blockDown == Blocks.air || !BlockStates.get(this.worldObj, blockpos.down()).isFullBlock() || blockDown == SRPBlocks.InfestedStain || this.worldObj.rand.nextInt(4) != 0) continue;
                BlockStates.set(this.worldObj, blockpos, BlockStates.defaultState(SRPBlocks.goreAda).withProperty(BlockGore.VARIANT, (Comparable)((Object)BlockGore.EnumType.FLAT)));
                if (++counttt < count) continue;
                return;
            }
        }
    }

    @Override
    protected void attackEntityFromCap(int go) {
        for (int i = 0; i < go && SRPConfig.paraGore; ++i) {
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
            double d7 = 0.8 / (d6 / 4.0 + 0.1);
            d4 = d4 * d7 * 2.0;
            EntityGore bomb = new EntityGore(this.worldObj);
            bomb.setType((byte)3);
            bomb.copyLocationAndAnglesFrom((Entity)this);
            bomb.setMotion(d3 *= (d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3f)), d4, d5 *= d7, 0.2, 0.8);
            this.worldObj.spawnEntityInWorld((Entity)bomb);
        }
    }

    @Override
    protected void spawnGore() {
        this.attackEntityFromEffects(3, 100);
        if (BlockStates.get(this.worldObj, EntityQueries.position(this).down()).isFullBlock() && (BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock() instanceof BlockBush || BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock() == Blocks.air)) {
            BlockStates.set(this.worldObj, EntityQueries.position(this), BlockStates.defaultState(SRPBlocks.goreAda).withProperty(BlockGore.VARIANT, (Comparable)((Object)BlockGore.EnumType.BIG)));
            EntityRemain nnn = new EntityRemain(this.worldObj);
            nnn.setLocationAndAngles((double)EntityQueries.position(this).getX() + 0.5, EntityQueries.position(this).getY(), (double)EntityQueries.position(this).getZ() + 0.5, 0.0f, 0.0f);
            nnn.setParasite(Objects.requireNonNull(EntityList.getKey((Entity)this)).toString());
            nnn.setSkin((byte)this.getSkin());
            nnn.setGoal(20 * SRPConfig.adaptedRemainValue);
            this.worldObj.spawnEntityInWorld((Entity)nnn);
        }
        this.attackEntityFromCap(5);
    }

    @Override
    protected boolean onDeathDislo(DamageSource cause) {
        if (!SRPConfigSystems.disloSameVersionDyeing || !this.disloNumberTwentytwo) {
            return super.onDeathDislo(cause);
        }
        boolean flag = super.onDeathDislo(cause);
        if (!flag && cause.getEntity() instanceof EntityLivingBase) {
            int count = SRPSaveData.get(this.worldObj, 25).getCurrentCode(this.worldObj.provider.dimensionId, 22);
            EntityLivingBase target = (EntityLivingBase)cause.getEntity();
            if (count >= 1) {
                target.removeActivePotionEffect(SRPPotions.KILLPRI_E);
                target.removeActivePotionEffect(SRPPotions.KILLPUR_E);
                target.removeActivePotionEffect(SRPPotions.KILLFER_E);
                target.removeActivePotionEffect(SRPPotions.KILLCRU_E);
                target.removeActivePotionEffect(SRPPotions.KILLNEX_E);
                SRPPotions.applyStackPotion(SRPPotions.KILLADA_E, target, 1200, count);
            }
        }
        return flag;
    }

    public void fall(float distance, float damageMultiplier) {
        if (distance >= 60.0f) {
            super.fall(distance, damageMultiplier);
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void spawnEffectsGore() {
        for (int i = 0; i <= 80; ++i) {
            if (i % 3 == 0) {
                this.spawnParticles(SRPEnumParticle.GCLOUD, 200, 200, 0);
            }
            if (i % 5 != 0) continue;
            this.spawnParticles(SRPEnumParticle.GSPLASH, 3, -1, -1);
        }
    }

    @Override
    public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
        return super.scaryOrbEffect(in, mobs);
    }
}

