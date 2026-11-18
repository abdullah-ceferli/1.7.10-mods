/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
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
 *  net.minecraft.init.MobEffects
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanColony;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityFlam;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.google.common.base.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class EntityPPreeminent
extends EntityPMalleable
implements EntityCanSummon,
EntityCanColony {
    protected int totalP;
    protected int actualP;
    protected int[] mobID;
    protected int[] mobPT;
    private int ggg;
    protected boolean canTeleportToo;

    public EntityPPreeminent(World worldIn) {
        super(worldIn);
        this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityPlayer>(this, EntityPlayer.class, 0, SRPConfig.preeminentWalls, false, null, SRPConfig.preeminentSneakPen, SRPConfig.preeminentInviPen));
        if (SRPConfig.mobattacking) {
            this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityLiving>(this, EntityLiving.class, 0, SRPConfig.preeminentWalls, false, new Predicate<EntityLiving>(){

                public boolean apply(@Nullable EntityLiving entity) {
                    return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager) && !ParasiteEventEntity.checkEntity((EntityLivingBase)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.preeminentSneakPen, SRPConfig.preeminentInviPen));
        }
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.borderOrb = -1;
        this.canModRender = 0;
        this.experienceValue = SRPAttributes.XP_PREE;
        this.canD = SRPConfig.preeminentdespawn;
        this.damageCap = SRPConfig.preeminentCap;
        this.fuseTime = 70;
        this.type = (byte)61;
        this.killcount = 10.0;
        this.fuseOrb = 13;
        this.orbStartTimer = 15;
        this.foodSteal = SRPConfig.preeminentFoodSteal;
        this.orbItemCool = SRPConfig.preeminentItemOrbCooldown * 20;
        this.pointCap = SRPConfig.preeminentPointCap;
        this.pointReduction = SRPConfig.preeminentPointRed;
        this.chanceLearn = SRPConfig.preeminentChanceLe;
        this.chanceLearnFire = SRPConfig.preeminentChanceLeFire;
        this.DamageTypeCap = SRPConfig.preeminentPointDamCap;
        this.MiniDamage = SRPConfig.preeminentMinDamage;
        this.regen = SRPConfig.preeminentRegen * SRPConfig.globalHealthMultiplier;
        this.oneMindDeathValue = SRPConfig.preeminentOneMindDeathV;
        this.regenEff = 25;
        this.foodRott = SRPConfig.preeminentFoodChance;
        this.foodRootNumber = SRPConfig.preeminentFoodAmount;
        this.cothSpread = SRPConfigSystems.cothPure;
        this.totalP = SRPConfig.preeminentFlamTotal;
        this.mobID = new int[this.totalP];
        this.mobPT = new int[this.totalP];
        for (int i = 0; i < this.mobID.length; ++i) {
            this.mobID[i] = -777;
        }
        this.valueEvDeath = SRPConfig.preeminentLoosingEPValue;
        this.setScentHPMultiplier(0.25f);
    }

    @Override
    public boolean onlySpawnInside() {
        return false;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        ++this.ggg;
        if (this.ggg > 80) {
            this.ggg = 0;
        }
        if (this.getAttackTarget() != null) {
            this.summonFlam(this.getAttackTarget());
        }
    }

    @Override
    protected void fearPlayer(EntityLivingBase player) {
        try {
            if (player == null) {
                return;
            }
            if (!this.canEntityBeSeen((Entity)player)) {
                return;
            }
            if (!player.isPotionActive(SRPPotions.FEAR_E)) {
                player.addPotionEffect(new PotionEffect(SRPPotions.FEAR_E, 300, 2, false, false));
            } else if (player.getActivePotionEffect(SRPPotions.FEAR_E).getAmplifier() < 2) {
                player.addPotionEffect(new PotionEffect(SRPPotions.FEAR_E, 300, 2, false, false));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        boolean flag = super.attackEntityFrom(source, amount);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    public void fall(float distance, float damageMultiplier) {
        if (distance >= 150.0f) {
            super.fall(distance, damageMultiplier);
        }
    }

    protected boolean summonFlam(EntityLivingBase in) {
        if (this.worldObj.isRemote || !SRPConfigMobs.flamEnabled) {
            return false;
        }
        if (this.ggg != 40) {
            return false;
        }
        double dis = this.getDistanceSqToEntity((Entity)in);
        this.checkID();
        if (this.getActualParasites() < this.getTotalParasites()) {
            this.resetIdleTime();
            EntityFlam wa = new EntityFlam(this.worldObj);
            float f19 = MathHelper.sin((float)(this.rotationYaw * ((float)Math.PI / 180) - this.randomYawVelocity * 0.01f));
            float f14 = 0.17453292f;
            float f16 = MathHelper.cos((float)f14);
            float f4 = MathHelper.cos((float)(this.rotationYaw * ((float)Math.PI / 180) - this.randomYawVelocity * 0.01f));
            wa.setPositionAndUpdate(this.posX + -1.0 * (double)(f19 * 4.0f * f16), this.posY + (double)this.getEyeHeight(), this.posZ - -1.0 * (double)(f4 * 4.0f * f16));
            this.setActualParasites(1);
            this.addID(wa.getEntityId(), 1);
            wa.setDamageATT(this);
            this.worldObj.spawnEntityInWorld((Entity)wa);
            wa.particleStatus((byte)8);
            wa.particleStatus((byte)8);
            wa.particleStatus((byte)8);
            wa.particleStatus((byte)8);
            wa.setAttackTarget(this.getAttackTarget());
            byte typeF = (byte)(this.rand.nextInt(3) + 1);
            if (typeF == 3) {
                if (this.getAttackTarget().getDistanceSqToEntity((Entity)this) < 100.0 || !this.getAttackTarget().onGround || this.canTeleportToo) {
                    typeF = (byte)(this.rand.nextInt(2) + 1);
                } else {
                    this.canTeleportToo = true;
                }
            }
            wa.setFatherTo(this, typeF);
            if (this.isPotionActive(net.minecraft.potion.Potion.invisibility)) {
                wa.addPotionEffect(new PotionEffect(net.minecraft.potion.Potion.invisibility, 60, 0, false, false));
            }
            this.ggg -= 100;
            return true;
        }
        return false;
    }

    public void setTeleFlam(boolean in) {
        this.canTeleportToo = in;
    }

    public boolean getTeleFlam() {
        return this.canTeleportToo;
    }

    @Override
    protected void spawnGore() {
        int range = 4;
        double i1 = MathHelper.floor_double((double)(this.posY + 0.1));
        double l1 = this.posX;
        double i2 = this.posZ;
        for (int k2 = -1 * range; k2 <= 1 * range && SRPConfig.paraGore; ++k2) {
            for (int l2 = -1 * range; l2 <= 1 * range; ++l2) {
                double i3 = l1 + (double)k2;
                double l = i2 + (double)l2;
                BlockPos blockpos = new BlockPos(i3, i1, l);
                Block block = BlockStates.get(this.worldObj, blockpos).getBlock();
                Block blockDown = BlockStates.get(this.worldObj, blockpos.down()).getBlock();
                if (block != Blocks.air || blockDown == Blocks.air || !BlockStates.get(this.worldObj, blockpos.down()).isFullBlock() || blockDown == SRPBlocks.InfestedStain || this.worldObj.rand.nextInt(4) != 0) continue;
                BlockStates.set(this.worldObj, blockpos, BlockStates.defaultState(SRPBlocks.gorePur).withProperty(BlockGore.VARIANT, (Comparable)((Object)BlockGore.EnumType.FLAT)));
            }
        }
        if (BlockStates.get(this.worldObj, EntityQueries.position(this).down()).isFullBlock() && (BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock() instanceof BlockBush || BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock() == Blocks.air)) {
            BlockStates.set(this.worldObj, EntityQueries.position(this), BlockStates.defaultState(SRPBlocks.gorePur).withProperty(BlockGore.VARIANT, (Comparable)((Object)BlockGore.EnumType.BIG)));
            EntityRemain nnn = new EntityRemain(this.worldObj);
            nnn.setLocationAndAngles((double)EntityQueries.position(this).getX() + 0.5, EntityQueries.position(this).getY(), (double)EntityQueries.position(this).getZ() + 0.5, 0.0f, 0.0f);
            nnn.setParasite(EntityList.getKey((Entity)this).toString());
            nnn.setSkin((byte)this.getSkin());
            nnn.setGoal(20 * SRPConfig.pureRemainValue);
            this.worldObj.spawnEntityInWorld((Entity)nnn);
        }
        for (int i = 0; i < 7 && SRPConfig.paraGore; ++i) {
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
            d4 = d4 * d7 * 2.0;
            EntityGore bomb = new EntityGore(this.worldObj);
            bomb.setType((byte)4);
            bomb.copyLocationAndAnglesFrom((Entity)this);
            bomb.setMotion(d3 *= (d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3f)), d4, d5 *= d7, 0.25, 0.65);
            this.worldObj.spawnEntityInWorld((Entity)bomb);
        }
    }

    @Override
    public int getTotalParasites() {
        return this.totalP;
    }

    @Override
    public int getActualParasites() {
        return this.actualP;
    }

    @Override
    public void setActualParasites(int i) {
        this.actualP += i;
    }

    @Override
    public void addID(int id, int points) {
        for (int i = 0; i < this.mobID.length; ++i) {
            if (this.mobID[i] != -777) continue;
            this.mobID[i] = id;
            this.mobPT[i] = points;
            return;
        }
    }

    @Override
    public int IDable() {
        int flag = 0;
        for (int i = 0; i < this.mobID.length; ++i) {
            if (this.mobID[i] != -777) continue;
            ++flag;
        }
        if (flag > this.totalP) {
            flag = this.totalP;
        }
        return flag;
    }

    @Override
    public void checkID() {
        for (int i = 0; i < this.mobID.length; ++i) {
            Entity flag;
            if (this.mobID[i] <= 0 || (flag = this.worldObj.getEntityByID(this.mobID[i])) != null) continue;
            this.mobID[i] = -777;
            int negative = this.mobPT[i] * -1;
            this.setActualParasites(negative);
        }
    }

    @Override
    public int[] getIDList() {
        return this.mobID;
    }

    @Override
    public int[] getPointList() {
        return this.mobPT;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void spawnEffectsGore() {
    }

    @Override
    public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag && in instanceof EntityPlayer) {
            EntityPlayer playerIn = (EntityPlayer)in;
            if (playerIn.experienceLevel > 0) {
                playerIn.addExperience(-SRPConfig.preeminentExpSteal);
                if (playerIn.experience < 0.0f) {
                    --playerIn.experienceLevel;
                    playerIn.experience = 1.0f;
                }
            } else {
                playerIn.addExperience(-SRPConfig.preeminentExpSteal);
                if (playerIn.experience < 0.0f) {
                    playerIn.experience = 0.0f;
                }
            }
        }
        return flag;
    }
}

