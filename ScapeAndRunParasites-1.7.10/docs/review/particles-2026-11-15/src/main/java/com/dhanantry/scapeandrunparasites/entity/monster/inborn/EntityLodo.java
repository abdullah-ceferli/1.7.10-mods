/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIAvoidEntity
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAISwimming
 *  net.minecraft.entity.monster.EntityCreeper
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.passive.EntityWaterMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityLodo
extends EntityParasiteBase {
    private int totalGrowtime = 10;
    private int actualGrowtime = 0;
    protected double buried;

    public EntityLodo(World worldIn) {
        super(worldIn);
        this.setSize(0.5f, 0.3f);
        this.experienceValue = SRPAttributes.XP_LiTTLE;
        this.totalGrowtime = this.rand.nextInt(60) + 60;
        this.killcount = -10.0;
        this.type = 1;
        this.buried = -1.0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 5;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAvoidEntity((EntityCreature)this, EntityLivingBase.class, (Predicate)new Predicate<EntityLivingBase>(){

            public boolean apply(@Nullable EntityLivingBase entity) {
                return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityCreeper) && !(entity instanceof EntityParasiteBase) && !(entity instanceof EntityAnimal);
            }
        }, 8.0f, 1.0, 1.0));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.LODO_HEALTH);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.LODO_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.LODO_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.LODO_KD_RESISTANCE);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.growTimer();
        this.growStage();
        this.buried();
    }

    public boolean buried() {
        if (this.buried == 1.0) {
            this.worldObj.playSound(this.x() + 0.5, this.y() + 0.5, this.z() + 0.5, SRPSounds.LODO_EMERGE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
        }
        if (this.buried >= 0.0) {
            this.getNavigator().clearPathEntity();
            this.posX = this.prevPosX;
            this.posZ = this.prevPosZ;
            int id = BlockStates.stateId((IBlockState)BlockStates.get(this.worldObj, new BlockPos(this.posX, this.posY, this.posZ).down()));
            this.buried -= 0.02;
            for (int i = 0; i < 2; ++i) {
                com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(this.worldObj, EnumParticleTypes.BLOCK_CRACK, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, this.posY, this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, this.rand.nextGaussian() * 0.02, this.rand.nextGaussian() * 0.02, this.rand.nextGaussian() * 0.02, new int[]{id});
            }
            return true;
        }
        if (this.getParasiteStatus() == 3) {
            this.setParasiteStatus(0);
        }
        return false;
    }

    private double x() {
        return this.posX;
    }

    private double y() {
        return this.posY;
    }

    private double z() {
        return this.posZ;
    }

    @Override
    protected void collideWithEntity(Entity entityIn) {
        super.collideWithEntity(entityIn);
        if (entityIn instanceof EntityLivingBase && this.ticksExisted % 20 == 0) {
            SRPPotions.applyStackPotion(SRPPotions.COTH_E, (EntityLivingBase)entityIn, 100, 0);
        }
    }

    protected void growStage() {
        if (!this.worldObj.isRemote && (this.actualGrowtime > this.totalGrowtime && ParasiteEventEntity.canSpawnNext || this.killcount > 1000.0)) {
            this.playSound(SRPSounds.LODO_MUDO, 1.0f, 1.0f);
            ParasiteEventEntity.spawnNext(this, new EntityMudo(this.worldObj), true, false);
        }
    }

    protected void growTimer() {
        if (!this.worldObj.isRemote && this.ticksExisted % 20 == 0) {
            ++this.actualGrowtime;
        }
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        return super.processInteract(player, hand);
    }

    public float getEyeHeight() {
        return 0.3f;
    }

    @Override
    protected boolean onDeathDislo(DamageSource cause) {
        return false;
    }

    protected String getLivingSound() {
        return SRPSounds.LODO_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.LODO_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.LODO_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(this.getStepSound(), this.getSoundVolume(), this.getSoundPitch());
    }

    protected String getStepSound() {
        return SRPSounds.MOBSILENCE;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("ruptergrow", this.actualGrowtime);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("ruptergrow", 99)) {
            this.actualGrowtime = compound.getInteger("ruptergrow");
        }
    }

    public void setFloorTimer() {
        this.buried = 1.0;
    }

    @SideOnly(value=Side.CLIENT)
    public double getFloorTimer() {
        return this.buried;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void handleHealthUpdate(byte id) {
        if (id == 50) {
            this.buried = 1.0;
        } else {
            super.handleHealthUpdate(id);
        }
    }
}

