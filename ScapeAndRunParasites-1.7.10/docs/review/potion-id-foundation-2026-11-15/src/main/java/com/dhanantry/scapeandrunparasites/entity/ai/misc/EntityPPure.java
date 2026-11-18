/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.passive.EntityWaterMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.MobEffects
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityDodT;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;

import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class EntityPPure
extends EntityPMalleable
implements EntityCanSummon {
    protected int totalP;
    protected int actualP;
    protected int[] mobID;
    protected int[] mobPT;
    private int ggg;

    public EntityPPure(World worldIn) {
        super(worldIn);
        this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityPlayer>(this, EntityPlayer.class, 0, SRPConfig.pureWalls, false, null, SRPConfig.pureSneakPen, SRPConfig.pureInviPen));
        if (SRPConfig.canOrbAttack) {
            this.tasks.addTask(2, (EntityAIBase)new EntityAISkill(this, 60, 5, false, 21));
        }
        if (SRPConfig.mobattacking) {
            this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityLiving>(this, EntityLiving.class, 0, SRPConfig.pureWalls, false, new Predicate<EntityLiving>(){

                public boolean apply(@Nullable EntityLiving entity) {
                    return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !ParasiteEventEntity.checkEntity((EntityLivingBase)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.pureSneakPen, SRPConfig.pureInviPen));
        }
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.experienceValue = SRPAttributes.XP_ADAPTED;
        this.canD = SRPConfig.puredespawn;
        this.damageCap = SRPConfig.pureCap;
        this.canModRender = 1;
        this.fuseTime = 70;
        this.type = (byte)51;
        this.killcount = 10.0;
        this.fuseOrb = 23;
        this.orbStartTimer = 5;
        this.foodSteal = SRPConfig.pureFoodSteal;
        this.orbItemCool = SRPConfig.pureItemOrbCooldown * 20;
        this.pointCap = SRPConfig.purePointCap;
        this.pointReduction = SRPConfig.purePointRed;
        this.chanceLearn = SRPConfig.pureChanceLe;
        this.chanceLearnFire = SRPConfig.pureChanceLeFire;
        this.DamageTypeCap = SRPConfig.purePointDamCap;
        this.MiniDamage = SRPConfig.pureMinDamage;
        this.regen = SRPConfig.pureRegen * SRPConfig.globalHealthMultiplier;
        this.oneMindDeathValue = SRPConfig.pureOneMindDeathV;
        this.regenEff = 15;
        this.foodRott = SRPConfig.pureFoodChance;
        this.foodRootNumber = SRPConfig.pureFoodAmount;
        this.cothSpread = SRPConfigSystems.cothPure;
        this.totalP = SRPConfig.pureSeiZ;
        this.mobID = new int[this.totalP];
        this.mobPT = new int[this.totalP];
        for (int i = 0; i < this.mobID.length; ++i) {
            this.mobID[i] = -777;
        }
        this.valueEvDeath = SRPConfig.pureLoosingEPValue;
        this.setScentHPMultiplier(1.0f);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        ++this.ggg;
        if (this.ggg > 60) {
            this.ggg = 0;
        }
        if (this.getAttackTarget() != null) {
            this.summonTentacles(this.getAttackTarget());
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
        EntityLivingBase attacker;
        if (this.worldObj.isRemote) {
            return super.attackEntityFrom(source, amount);
        }
        if (source.getEntity() instanceof EntityLivingBase && (attacker = (EntityLivingBase)source.getEntity()).isPotionActive(SRPPotions.KILLPUR_E)) {
            int amp = attacker.getActivePotionEffect(SRPPotions.KILLPUR_E).getAmplifier();
            float totalRed = MathHelper.clamp_float((float)(SRPConfigSystems.parasiteKillingReduction * ((float)amp + 1.0f)), (float)0.0f, (float)0.95f);
            float reduced = amount * (1.0f - totalRed);
            boolean flag = super.attackEntityFrom(source, Math.max(0.0f, reduced));
            if (flag) {
                this.summonTentacles((EntityLivingBase)source.getEntity());
            }
            return flag;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    protected void attackEntityFromEffects(int range, int count) {
        this.particleStatus((byte)51);
        double i1 = MathHelper.floor_double((double)(this.posY + 0.1));
        double l1 = this.posX;
        double i2 = this.posZ;
        int counttt = 0;
        for (int k2 = -1 * range; k2 <= 1 * range && SRPConfig.paraGore; ++k2) {
            for (int l2 = -1 * range; l2 <= 1 * range; ++l2) {
                double i3 = l1 + (double)k2;
                double l = i2 + (double)l2;
                BlockPos blockpos = new BlockPos(i3, i1, l);
                Block block = BlockStates.get(this.worldObj, blockpos).getBlock();
                Block blockDown = BlockStates.get(this.worldObj, blockpos.down()).getBlock();
                if (block != Blocks.air || blockDown == Blocks.air || !BlockStates.get(this.worldObj, blockpos.down()).isFullBlock() || blockDown == SRPBlocks.InfestedStain || this.worldObj.rand.nextInt(4) != 0) continue;
                BlockStates.set(this.worldObj, blockpos, BlockStates.defaultState(SRPBlocks.gorePur).withProperty(BlockGore.VARIANT, (Comparable)((Object)BlockGore.EnumType.FLAT)));
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
            bomb.setType((byte)4);
            bomb.copyLocationAndAnglesFrom((Entity)this);
            bomb.setMotion(d3 *= (d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3f)), d4, d5 *= d7, 0.2, 0.8);
            this.worldObj.spawnEntityInWorld((Entity)bomb);
        }
    }

    @Override
    protected void spawnGore() {
        this.attackEntityFromEffects(3, 100);
        if (BlockStates.get(this.worldObj, EntityQueries.position(this).down()).isFullBlock() && (BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock() instanceof BlockBush || BlockStates.get(this.worldObj, EntityQueries.position(this)).getBlock() == Blocks.air)) {
            BlockStates.set(this.worldObj, EntityQueries.position(this), BlockStates.defaultState(SRPBlocks.gorePur).withProperty(BlockGore.VARIANT, (Comparable)((Object)BlockGore.EnumType.BIG)));
            EntityRemain nnn = new EntityRemain(this.worldObj);
            nnn.setLocationAndAngles((double)EntityQueries.position(this).getX() + 0.5, EntityQueries.position(this).getY(), (double)EntityQueries.position(this).getZ() + 0.5, 0.0f, 0.0f);
            nnn.setParasite(EntityList.getKey((Entity)this).toString());
            nnn.setSkin((byte)this.getSkin());
            nnn.setGoal(20 * SRPConfig.pureRemainValue);
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
            int count = SRPSaveData.get(this.worldObj, 28).getCurrentCode(this.worldObj.provider.dimensionId, 22);
            EntityLivingBase target = (EntityLivingBase)cause.getEntity();
            if (count >= 1) {
                target.removeActivePotionEffect(SRPPotions.KILLPRI_E);
                target.removeActivePotionEffect(SRPPotions.KILLADA_E);
                target.removeActivePotionEffect(SRPPotions.KILLFER_E);
                target.removeActivePotionEffect(SRPPotions.KILLCRU_E);
                target.removeActivePotionEffect(SRPPotions.KILLNEX_E);
                SRPPotions.applyStackPotion(SRPPotions.KILLPUR_E, target, 1200, count);
            }
        }
        return flag;
    }

    public void fall(float distance, float damageMultiplier) {
        if (distance >= 80.0f) {
            super.fall(distance, damageMultiplier);
        }
    }

    protected void summonTentacles(EntityLivingBase in) {
        boolean flag;
        if (this.ggg != 20) {
            return;
        }
        if (SRPConfigSystems.useEvolution && ParasiteEventEntity.getRSchance(this.worldObj) == 0.0) {
            return;
        }
        double dis = this.getDistanceSqToEntity((Entity)in);
        boolean bl = flag = !this.canEntityBeSeen((Entity)in);
        if (dis > 64.0 && flag && this.rand.nextInt(3) == 0 || this.rand.nextInt(10) == 0) {
            this.checkID();
            if (this.getActualParasites() < this.getTotalParasites() && this.spawnT(in, 5, 3, 2, 2)) {
                this.addPotionEffect(new PotionEffect(net.minecraft.potion.Potion.moveSlowdown, 150, 120, false, false));
            }
        } else {
            if (in.isPotionActive(net.minecraft.potion.Potion.moveSlowdown) && in.getActivePotionEffect(net.minecraft.potion.Potion.moveSlowdown).getAmplifier() == 2) {
                return;
            }
            this.checkID();
            if (this.getActualParasites() >= this.getTotalParasites() || this.spawnT(in, 3, 1, 16, 1)) {
                // empty if block
            }
        }
    }

    protected boolean spawnT(EntityLivingBase in, int range2, int mini2, int check, int type) {
        int range = range2;
        int mini = mini2;
        Random rand = new Random();
        double x = in.posX;
        double y = in.posY;
        double z = in.posZ;
        double randomx = rand.nextInt(range) + mini;
        double randomz = rand.nextInt(range) + mini;
        double negative = rand.nextInt(2);
        if (negative == 0.0) {
            randomx *= -1.0;
        }
        if ((negative = (double)rand.nextInt(2)) == 0.0) {
            randomz *= -1.0;
        }
        int limit = 0;
        boolean flag = true;
        EntityPStationary entityout = new EntityDodT(this.worldObj);
        ((EntityDodT)entityout).setTele(this);
        if (type == 1) {
            List serverList = EntityQueries.loaded(this.worldObj);
            int count = 0;
            for (int k = 0; k < serverList.size(); ++k) {
                if (!(serverList.get(k) instanceof EntityNak)) continue;
                ++count;
            }
            if (count >= 7) {
                entityout.setDead();
                return false;
            }
            entityout = new EntityNak(this.worldObj);
        }
        while (flag) {
            if (limit >= 5) {
                entityout.setDead();
                return false;
            }
            BlockPos pos = new BlockPos(x + randomx, y, z + randomz);
            if ((pos = ParasiteEventEntity.getFloor(this.worldObj, pos, 5)) != null) {
                IBlockState state = BlockStates.get(this.worldObj, pos);
                if (BlockStates.get(this.worldObj, pos.down()).isFullCube()) {
                    AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)), (double)check);
                    List moblist = EntityQueries.entities(this.worldObj, EntityNak.class, axisalignedbb);
                    if (moblist.size() > 10) {
                        return false;
                    }
                    axisalignedbb = Boxes.grow(Boxes.create((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)), 2.0);
                    moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
                    if (moblist.isEmpty()) {
                        entityout.setLocationAndAngles(x + randomx, y, z + randomz, this.rotationYaw, this.rotationPitch);
                        if (!this.worldObj.getCollisionBoxes((Entity)entityout, Boxes.entityBox(entityout)).isEmpty()) {
                            randomx = rand.nextInt(range) + mini;
                            randomz = rand.nextInt(range) + mini;
                            negative = rand.nextInt(2);
                            if (negative == 0.0) {
                                randomx *= -1.0;
                            }
                            if ((negative = (double)rand.nextInt(2)) == 0.0) {
                                randomz *= -1.0;
                            }
                            ++limit;
                            continue;
                        }
                        entityout.onInitialSpawn(DifficultyAccess.at(this.worldObj, EntityQueries.position(entityout)), null);
                        entityout.setAttackTarget(in);
                        this.setActualParasites(1);
                        this.addID(entityout.getEntityId(), 1);
                        entityout.setBuried();
                        this.worldObj.spawnEntityInWorld((Entity)entityout);
                        this.worldObj.setEntityState((Entity)entityout, (byte)50);
                        flag = false;
                        return true;
                    }
                }
            }
            randomx = rand.nextInt(range) + mini;
            randomz = rand.nextInt(range) + mini;
            negative = rand.nextInt(2);
            if (negative == 0.0) {
                randomx *= -1.0;
            }
            if ((negative = (double)rand.nextInt(2)) == 0.0) {
                randomz *= -1.0;
            }
            ++limit;
        }
        return false;
    }

    private EntityDodT getTentacle() {
        for (int i = 0; i < this.mobID.length; ++i) {
            if (this.mobID[i] <= 0 || !(this.worldObj.getEntityByID(this.mobID[i]) instanceof EntityDodT)) continue;
            EntityDodT flag = (EntityDodT)this.worldObj.getEntityByID(this.mobID[i]);
            if (flag == null) {
                this.mobID[i] = -777;
                int negative = this.mobPT[i] * -1;
                this.setActualParasites(negative);
                continue;
            }
            if (!flag.isEntityAlive() || flag.getTele() != null || flag.buried()) continue;
            return flag;
        }
        return null;
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
                playerIn.addExperience(-SRPConfig.pureExpSteal);
                if (playerIn.experience < 0.0f) {
                    --playerIn.experienceLevel;
                    playerIn.experience = 1.0f;
                }
            } else {
                playerIn.addExperience(-SRPConfig.pureExpSteal);
                if (playerIn.experience < 0.0f) {
                    playerIn.experience = 0.0f;
                }
            }
        }
        return flag;
    }
}

