/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.pathfinding.PathNavigate
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.focused;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIBlockResidue;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanClimb;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPFocused;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.PathNavigateClimberStatus;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.DamageSource;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityShycoFocused
extends EntityPFocused
implements EntityCutomAttack,
EntityBodyParts,
EntityCanClimb {
    private float attackTimer;
    private boolean up;
    private double extraDamage;
    private double currentDamage;
    private double hpLeft;
    private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(EntityShycoFocused.class, (DataSerializer)DataSerializers.BYTE);

    public EntityShycoFocused(World worldIn) {
        super(worldIn);
        this.setSize(0.901f, 3.5f);
        this.stepHeight = 1.0f;
        this.extraDamage = this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue();
    }

    @Override
    public int getParasiteIDRegister() {
        return 83;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimmingDiving((EntityLiving)this, 0.08));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatusAOE(this, 1.3, false, 8.0, 4.0));
        this.tasks.addTask(9, (EntityAIBase)new EntityAIBlockResidue(this, 2));
        this.tasks.addTask(6, (EntityAIBase)new EntityAIGetFollowers(this, 3, 32));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIEvade(this, 25, 10, 4.0));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.SHYCO_HEALTH + SRPAttributes.SHYCO_A_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.SHYCO_ARMOR + SRPAttributes.SHYCO_A_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.SHYCO_KD_RESISTANCE + SRPAttributes.SHYCO_A_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.SHYCO_ATTACK_DAMAGE + SRPAttributes.SHYCO_A_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.adaptedFollow);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.rand.nextDouble() < this.hpLeft && this.worldObj.isRemote) {
            this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 106, 0);
        }
        if (this.up) {
            this.attackTimer = (float)((double)this.attackTimer + 0.2);
            if ((double)this.attackTimer > 1.2) {
                this.up = false;
            }
        } else {
            this.attackTimer = (float)((double)this.attackTimer - 0.1);
        }
        if (!this.worldObj.isRemote) {
            this.setBesideClimbableBlock(this.isCollidedHorizontally);
        }
    }

    @Override
    public byte climbStatus() {
        return (Byte)EntityDataManager.of(this).get(CLIMBING);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(CLIMBING, (Object)((byte)0));
    }

    public boolean isBesideClimbableBlock() {
        return ((Byte)EntityDataManager.of(this).get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = (Byte)EntityDataManager.of(this).get(CLIMBING);
        b0 = climbing ? (byte)(b0 | 1) : (byte)(b0 & 0xFFFFFFFE);
        EntityDataManager.of(this).set(CLIMBING, (Object)b0);
    }

    public boolean isOnLadder() {
        return this.isBesideClimbableBlock();
    }

    protected PathNavigate createNavigator(World worldIn) {
        return new PathNavigateClimberStatus((EntityLiving)this, worldIn);
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        boolean flag = super.attackEntityFrom(source, amount);
        if (flag) {
            this.currentDamage = this.extraDamage * (1.0 - (double)(this.getHealth() / this.getMaxHealth()) * SRPAttributes.SHYCO_A_I_DAMAGE);
        }
        this.hpLeft = 1.0f - this.getHealth() / this.getMaxHealth();
        return flag;
    }

    @Override
    public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
        if (this.worldObj.isRemote) {
            return false;
        }
        boolean flag = this.attackEntityFrom(source, amount);
        if (!flag) {
            return false;
        }
        return flag;
    }

    @Override
    public void setBodyPartDead(int id) {
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        boolean flag = super.attackEntityAsMob(entityIn);
        if (flag && entityIn instanceof EntityLivingBase) {
            entityIn.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase)this), (float)this.currentDamage);
        }
        this.hpLeft = 1.0f - this.getHealth() / this.getMaxHealth();
        return flag;
    }

    public float getEyeHeight() {
        return 3.3f;
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.ASHYCO_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.ASHYCO_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.ASHYCO_DEATH;
    }

    @Override
    public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SRPSounds.MONSTER_STEP, 0.15f, 1.0f);
    }

    @Override
    public void setDead() {
        super.setDead();
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        this.up = true;
        this.attackTimer = 0.0f;
        this.worldObj.setEntityState((Entity)this, (byte)12);
        boolean flag = false;
        this.playSound(SRPSounds.SWIPE, 2.0f, 1.0f);
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(entityIn.posX, entityIn.posY, entityIn.posZ, entityIn.posX + 1.0, entityIn.posY + 1.0, entityIn.posZ + 1.0), 3.0);
        List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
        for (EntityLivingBase mob : moblist) {
            if (mob instanceof EntityParasiteBase) {
                if (this.getAttackTarget() != mob) continue;
                this.setAttackTarget(null);
                return false;
            }
            if (mob == this || !this.canEntityBeSeen((Entity)mob) || !this.attackEntityAsMob((Entity)mob)) continue;
            flag = true;
        }
        return flag;
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
        if (this.worldObj.rand.nextInt(3) == 0) {
            this.setSkin(1);
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue((SRPAttributes.SHYCO_HEALTH + SRPAttributes.SHYCO_A_HEALTH) * 0.5);
            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue((SRPAttributes.SHYCO_ATTACK_DAMAGE + SRPAttributes.SHYCO_A_ATTACK_DAMAGE) * 2.0);
            this.setHealth((float)this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue());
        }
        return floo;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
    }

    @SideOnly(value=Side.CLIENT)
    public float getAttackTimer() {
        return this.attackTimer;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void handleHealthUpdate(byte id) {
        if (id == 12) {
            this.up = true;
            this.attackTimer = 0.0f;
        } else {
            super.handleHealthUpdate(id);
        }
    }

    @Override
    protected EntityPCosmical getThis() {
        return new EntityShycoFocused(this.worldObj);
    }
}

