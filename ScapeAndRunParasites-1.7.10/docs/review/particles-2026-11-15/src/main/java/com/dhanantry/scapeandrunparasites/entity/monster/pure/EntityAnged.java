/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.IRangedAttackMob
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAISwimming
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.pure;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeRangeSwitch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackRangedStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPure;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityTendril;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileAngedball;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.SRPPacketEntityBodyDead;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityAnged
extends EntityPPure
implements IRangedAttackMob,
EntityBodyParts {
    private EntityBody leftTendril;
    private EntityBody rightTendril;
    private float leftTendrilHealth;
    private float rightTendrilHealth;

    public EntityAnged(World worldIn) {
        super(worldIn);
        this.setSize(1.6f, 3.1f);
        this.type = (byte)51;
        this.stepHeight = 1.0f;
        this.leftTendril = new EntityBody(this, 0.7f, 0.9f, 1.0f, 1.1f, 2.3f, 1, 1, true);
        this.rightTendril = new EntityBody(this, 0.7f, 0.9f, 1.0f, 1.1f, 2.3f, -1, 2, true);
        this.leftTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.rightTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
    }

    @Override
    public int getParasiteIDRegister() {
        return 25;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
        this.tasks.addTask(6, (EntityAIBase)new EntityAIAttackMeleeRangeSwitch(this, 5.0f));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIAttackMeleeStatus(this, 1.5, false, 0.0));
        this.tasks.addTask(4, (EntityAIBase)new EntityAIAttackRangedStatus(this, 1.5, 20, (float)SRPConfig.pureFollow / 2.0f, false));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.ANGED_HEALTH);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.ANGED_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.ANGED_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.ANGED_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.pureFollow);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.leftTendrilHealth > 0.0f) {
            this.leftTendril.onUpdate();
        }
        if (this.rightTendrilHealth > 0.0f) {
            this.rightTendril.onUpdate();
        }
    }

    public float getEyeHeight() {
        return 3.0f;
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        boolean flag = super.attackEntityAsMob(entityIn);
        if (flag && entityIn instanceof EntityLivingBase) {
            ((EntityLivingBase)entityIn).knockBack(entityIn, 1.0f, this.posX - entityIn.posX, this.posZ - entityIn.posZ);
        }
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
        if (this.leftTendril.getId() == id) {
            this.leftTendrilHealth -= amount;
            if (this.leftTendrilHealth <= 0.0f) {
                EntityTendril tendril = new EntityTendril(this.worldObj);
                tendril.setSkin(6);
                tendril.copyLocationAndAnglesFrom(this.leftTendril);
                this.worldObj.spawnEntityInWorld((Entity)tendril);
                this.worldObj.removePlayerEntityDangerously((Entity)this.leftTendril);
                this.worldObj.setEntityState((Entity)this, (byte)11);
                this.cutResistances(SRPConfig.purePointDamCap / 2);
                SRPMain.network.sendToAll((IMessage)new SRPPacketEntityBodyDead(this.getEntityId(), id));
            }
        } else if (this.rightTendril.getId() == id) {
            this.rightTendrilHealth -= amount;
            if (this.rightTendrilHealth <= 0.0f) {
                EntityTendril tendril = new EntityTendril(this.worldObj);
                tendril.setSkin(6);
                tendril.copyLocationAndAnglesFrom(this.rightTendril);
                this.worldObj.spawnEntityInWorld((Entity)tendril);
                this.worldObj.removePlayerEntityDangerously((Entity)this.rightTendril);
                this.worldObj.setEntityState((Entity)this, (byte)22);
                this.cutResistances(SRPConfig.purePointDamCap / 2);
                SRPMain.network.sendToAll((IMessage)new SRPPacketEntityBodyDead(this.getEntityId(), id));
            }
        }
        return flag;
    }

    @Override
    public void setBodyPartDead(int id) {
        if (this.leftTendril.getId() == id) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.leftTendril);
        } else if (this.rightTendril.getId() == id) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.rightTendril);
        }
    }

    protected String getLivingSound() {
        return SRPSounds.ANGED_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        if (this.rand.nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.ANGED_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.ANGED_DEATH;
    }

    @Override
    public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.angedOrbEffects, mobs);
        }
        return flag;
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
        if (this.rand.nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant || this.canChangeVariant) {
            switch (this.rand.nextInt(1)) {
                case 0: {
                    this.setSkin(7);
                }
            }
        }
        return floo;
    }

    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        Vec3d vec3d = this.getLook(1.0f);
        double d2 = target.posX - (this.posX + vec3d.xCoord);
        double d3 = Boxes.entityBox(target).minY + (double)(target.height / 2.0f) - (0.5 + this.posY + (double)(this.height / 2.0f));
        double d4 = target.posZ - (this.posZ + vec3d.zCoord);
        this.playSound(SRPSounds.EMANA_SHOOTING, 2.0f, 1.0f);
        d3 = Boxes.entityBox(target).minY + (double)(target.height / 4.0f) - (1.0 + this.posY + (double)(this.height / 2.0f));
        EntityProjectileAngedball entitylargefireball = new EntityProjectileAngedball(this.worldObj, (EntityLivingBase)this, d2, d3, d4);
        entitylargefireball.posX = this.posX + vec3d.xCoord;
        entitylargefireball.posY = this.posY + (double)this.getEyeHeight() - 0.2;
        entitylargefireball.posZ = this.posZ + vec3d.zCoord;
        this.worldObj.spawnEntityInWorld((Entity)entitylargefireball);
    }

    public void setSwingingArms(boolean swingingArms) {
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setFloat("parasiteleftTendril", this.leftTendrilHealth);
        compound.setFloat("parasiterightTendril", this.rightTendrilHealth);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("parasiteleftTendril", 99)) {
            this.leftTendrilHealth = compound.getFloat("parasiteleftTendril");
            if (this.leftTendrilHealth <= 0.0f) {
                this.worldObj.setEntityState((Entity)this, (byte)11);
            }
        }
        if (compound.hasKey("parasiterightTendril", 99)) {
            this.rightTendrilHealth = compound.getFloat("parasiterightTendril");
            if (this.rightTendrilHealth <= 0.0f) {
                this.worldObj.setEntityState((Entity)this, (byte)22);
            }
        }
    }

    @Override
    public void setDead() {
        if (this.leftTendril != null) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.leftTendril);
        }
        if (this.rightTendril != null) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.rightTendril);
        }
        super.setDead();
    }

    @SideOnly(value=Side.CLIENT)
    public float getLeft() {
        return this.leftTendrilHealth;
    }

    @SideOnly(value=Side.CLIENT)
    public float getRight() {
        return this.rightTendrilHealth;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void handleHealthUpdate(byte id) {
        if (id == 11) {
            this.leftTendrilHealth = 0.0f;
        } else if (id == 22) {
            this.rightTendrilHealth = 0.0f;
        } else {
            super.handleHealthUpdate(id);
        }
    }
}

