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
 *  net.minecraft.entity.passive.EntityWaterMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.nbt.NBTTagCompound
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
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSpawn;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityAta;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
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
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class EntityPFeral
extends EntityParasiteBase
implements EntityCanSpawn {
    protected float regen;
    protected int regenEff;
    protected int regenUse;

    public EntityPFeral(World worldIn) {
        super(worldIn);
        this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityPlayer>(this, EntityPlayer.class, 0, true, false, null, SRPConfig.feralSneakPen, SRPConfig.feralInviPen));
        if (SRPConfig.mobattacking) {
            this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityLiving>(this, EntityLiving.class, 0, true, false, entity -> !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !ParasiteEventEntity.checkEntity((EntityLivingBase)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite), SRPConfig.feralSneakPen, SRPConfig.feralInviPen));
        }
        this.tasks.addTask(2, (EntityAIBase)new EntityAIEvade(this, 30, 5, 3.0));
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.experienceValue = SRPAttributes.XP_FERAL;
        this.damageCap = SRPConfig.feralCap;
        this.canD = SRPConfig.feraldespawn;
        this.MiniDamage = SRPConfig.feralMinDamage;
        this.oneMindDeathValue = SRPConfig.feralOneMindDeathV;
        this.foodSteal = SRPConfig.feralFoodSteal;
        this.regen = SRPConfig.feralRegen * SRPConfig.globalHealthMultiplier;
        this.regenEff = 10;
        this.cothSpread = SRPConfigSystems.cothFeral;
        this.valueEvDeath = SRPConfig.feralLoosingEPValue;
        this.attackSpeedT = 14;
        this.setScentHPMultiplier(1.5f);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.worldObj.isRemote) {
            if (this.rand.nextInt(25) == 0) {
                for (int i = 0; i <= 1; ++i) {
                    this.spawnParticlesGoreBox(SRPEnumParticle.GSPLASH, 0, -1, -1, 0.1, 0.0);
                }
            }
        } else if (this.srpTicks == 10 && !this.isDead && this.getHealth() > 0.0f && !this.isBurning() && this.regen > 0.0f && this.killcount > 1.0 && this.getHealth() < this.getMaxHealth()) {
            this.setHealth(this.getHealth() + this.regen);
            --this.regenUse;
            if (this.regenUse <= 0) {
                this.killcount -= 1.0;
                this.regenUse = this.regenEff;
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
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        EntityLivingBase attacker;
        if (this.worldObj.isRemote) {
            return super.attackEntityFrom(source, amount);
        }
        if (source.getEntity() instanceof EntityLivingBase && (attacker = (EntityLivingBase)source.getEntity()).isPotionActive(SRPPotions.KILLFER_E)) {
            int amp = Objects.requireNonNull(attacker.getActivePotionEffect(SRPPotions.KILLFER_E)).getAmplifier();
            float totalRed = MathHelper.clamp_float((float)(SRPConfigSystems.parasiteKillingReduction * ((float)amp + 1.0f)), (float)0.0f, (float)0.95f);
            float reduced = amount * (1.0f - totalRed);
            return super.attackEntityFrom(source, Math.max(0.0f, reduced));
        }
        boolean tookDamage = super.attackEntityFrom(source, amount);
        if (tookDamage && source.getEntity() instanceof EntityLivingBase && this.rand.nextDouble() < SRPConfig.feralMult) {
            double d2;
            double d5;
            double d1;
            double d4;
            double d0 = (float)this.posX + this.worldObj.rand.nextFloat();
            double d3 = d0 - this.posX;
            double d6 = MathHelper.sqrt_double((double)(d3 * d3 + (d4 = (d1 = (double)((float)this.posY + this.worldObj.rand.nextFloat())) - this.posY) * d4 + (d5 = (d2 = (double)((float)this.posZ + this.worldObj.rand.nextFloat())) - this.posZ) * d5));
            if (d6 < 1.0E-4) {
                d6 = 1.0E-4;
            }
            d3 /= d6;
            d4 /= d6;
            d5 /= d6;
            double d7 = 0.8 / (d6 / 4.0 + 0.1);
            d3 = d3 * (d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3f)) * 0.5;
            d4 = d4 * d7 * 1.0;
            d5 = d5 * d7 * 0.5;
            EntityGore bomb = new EntityGore(this.worldObj);
            bomb.setType((byte)111);
            bomb.entityName = EntityList.getKey((Entity)this) != null ? Objects.requireNonNull(EntityList.getKey((Entity)this)).toString() : "unknown";
            bomb.copyLocationAndAnglesFrom((Entity)this);
            bomb.setMotion(d3, d4, d5, 0.2, 0.5);
            this.worldObj.spawnEntityInWorld((Entity)bomb);
        }
        return tookDamage;
    }

    @Override
    protected void attackEntityFromEffects(int range, int count) {
        this.particleStatus((byte)51);
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
                BlockStates.set(this.worldObj, blockpos, BlockStates.defaultState(SRPBlocks.goreFer).withProperty(BlockGore.VARIANT, (Comparable)((Object)BlockGore.EnumType.FLAT)));
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
            bomb.setType((byte)1);
            bomb.copyLocationAndAnglesFrom((Entity)this);
            bomb.setMotion(d3 *= (d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3f)), d4, d5 *= d7, 0.2, 0.8);
            this.worldObj.spawnEntityInWorld((Entity)bomb);
        }
    }

    @Override
    protected void spawnGore() {
        this.attackEntityFromEffects(2, 100);
        List serverList = EntityQueries.loaded(this.worldObj);
        int count = 0;
        for (Entity entity : serverList) {
            if (!(entity instanceof EntityParasiteBase)) continue;
            ++count;
        }
        if (count < SRPConfig.worldMobCap) {
            EntityAta ata = new EntityAta(this.worldObj);
            ata.copyLocationAndAnglesFrom((Entity)this);
            this.worldObj.spawnEntityInWorld((Entity)ata);
        }
        if (BlockStates.get(this.worldObj, EntityQueries.position(this).down()).isFullBlock() && (BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock() instanceof BlockBush || BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock() == Blocks.air)) {
            BlockStates.set(this.worldObj, EntityQueries.position(this), BlockStates.defaultState(SRPBlocks.goreFer).withProperty(BlockGore.VARIANT, (Comparable)((Object)BlockGore.EnumType.BIG)));
            EntityRemain nnn = new EntityRemain(this.worldObj);
            nnn.setLocationAndAngles((double)EntityQueries.position(this).getX() + 0.5, EntityQueries.position(this).getY(), (double)EntityQueries.position(this).getZ() + 0.5, 0.0f, 0.0f);
            nnn.setParasite(Objects.requireNonNull(EntityList.getKey((Entity)this)).toString());
            nnn.setSkin((byte)this.getSkin());
            nnn.setGoal(20 * SRPConfig.feralRemainValue);
            this.worldObj.spawnEntityInWorld((Entity)nnn);
        }
        this.attackEntityFromCap(3);
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        float after;
        float dealt;
        if (!(entityIn instanceof EntityLivingBase)) {
            return super.attackEntityAsMob(entityIn);
        }
        EntityLivingBase target = (EntityLivingBase)entityIn;
        float before = target.getHealth() + target.getAbsorptionAmount();
        boolean hit = super.attackEntityAsMob(entityIn);
        if (hit && !this.worldObj.isRemote && (dealt = Math.max(0.0f, before - (after = target.getHealth() + target.getAbsorptionAmount()))) > 0.0f) {
            this.fearPlayer(target, dealt);
        }
        return hit;
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn) {
        super.onKillEntity(entityLivingIn);
    }

    @Override
    protected boolean onDeathDislo(DamageSource cause) {
        if (!SRPConfigSystems.disloSameVersionDyeing || !this.disloNumberTwentytwo) {
            return super.onDeathDislo(cause);
        }
        boolean flag = super.onDeathDislo(cause);
        if (!flag && cause.getEntity() instanceof EntityLivingBase) {
            int count = SRPSaveData.get(this.worldObj, 31).getCurrentCode(this.worldObj.provider.dimensionId, 22);
            EntityLivingBase target = (EntityLivingBase)cause.getEntity();
            if (count >= 1) {
                target.removeActivePotionEffect(SRPPotions.KILLPRI_E);
                target.removeActivePotionEffect(SRPPotions.KILLADA_E);
                target.removeActivePotionEffect(SRPPotions.KILLPUR_E);
                target.removeActivePotionEffect(SRPPotions.KILLCRU_E);
                target.removeActivePotionEffect(SRPPotions.KILLNEX_E);
                SRPPotions.applyStackPotion(SRPPotions.KILLFER_E, target, 1200, count);
            }
        }
        return flag;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void spawnEffectsGore() {
        int i;
        for (i = 0; i <= 100; ++i) {
            if (i % 5 != 0) continue;
            this.spawnParticlesGore(SRPEnumParticle.GSPLASH, 0, -1, -1);
        }
        for (i = 0; i <= 20; ++i) {
            if (i % 5 == 0) {
                this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
            }
            if (i % 5 != 0) continue;
            this.spawnParticles(SRPEnumParticle.GSPLASH, 0, -1, -1);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
    }
}

