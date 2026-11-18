/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.MobEffects
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumHand
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.deterrent;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDispatcher;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.network.MsgQlipShake;
import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityNak
extends EntityPStationary {
    private float attackTimer;
    private boolean up2;
    private EntityPDispatcher father;
    private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey(EntityNak.class, (DataSerializer)DataSerializers.VARINT);
    private EntityLivingBase targetedEntity;
    private int tak;

    public EntityNak(World worldIn) {
        super(worldIn);
        this.setSize(0.7f, 2.5f);
        this.experienceValue = 0;
        this.type = (byte)40;
        this.buriedT = 4.0;
        this.damageCap = SRPConfig.turretCap;
        this.pointCap = SRPConfig.turretPointCap;
        this.pointReduction = SRPConfig.turretPointRed;
        this.chanceLearn = SRPConfig.turretChanceLe;
        this.chanceLearnFire = SRPConfig.turretChanceLeFire;
        this.DamageTypeCap = SRPConfig.turretPointDamCap;
        this.MiniDamage = SRPConfig.turretMinDamage;
        this.regen = SRPConfig.turretRegen;
        this.valueEvDeath = 0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 72;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(8, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(TARGET_ENTITY, (Object)0);
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.NAK_HEALTH);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.NAK_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.NAK_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(6.0);
    }

    @Override
    public void onLivingUpdate() {
        double dis;
        super.onLivingUpdate();
        if (this.isAIDisabled()) {
            return;
        }
        if (this.up2) {
            this.attackTimer += 0.15f;
            if (this.attackTimer > 1.0f) {
                this.up = false;
            }
        } else {
            this.attackTimer -= 0.2f;
        }
        if (this.up || this.buried()) {
            return;
        }
        if (!this.worldObj.isRemote) {
            if (this.getAttackTarget() != null) {
                if (this.getAttackTarget() instanceof EntityPlayer) {
                    EntityPlayer pa = (EntityPlayer)this.getAttackTarget();
                    if (pa.capabilities.isCreativeMode || pa.capabilities.disableDamage) {
                        this.setAttackTarget(null);
                        this.setTargetedEntity(0);
                        this.setParasiteStatus(0);
                        return;
                    }
                }
                if (!this.getAttackTarget().isEntityAlive()) {
                    this.setAttackTarget(null);
                    this.setTargetedEntity(0);
                    this.setParasiteStatus(0);
                } else {
                    this.tak = 0;
                    if (this.canEntityBeSeen((Entity)this.getAttackTarget()) && this.getDistanceSqToEntity((Entity)this.getAttackTarget()) < 25.0) {
                        this.setParasiteStatus(3);
                        this.getAttackTarget().addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.moveSlowdown, 80, 2, false, false));
                        this.setTargetedEntity(this.getAttackTarget().getEntityId());
                        this.getNavigator().tryMoveToEntityLiving((Entity)this.getAttackTarget(), 0.0);
                        this.lookAt((Entity)this.getAttackTarget());
                        if (this.srpTicks == 10 && this.getAttackTarget() instanceof EntityPlayerMP) {
                            SRPNetwork.CHANNEL.sendTo((IMessage)new MsgQlipShake(250, 0, true, false, 4.0f), (EntityPlayerMP)this.getAttackTarget());
                        }
                    } else {
                        this.setParasiteStatus(2);
                        this.setTargetedEntity(0);
                    }
                }
            } else {
                ++this.tak;
                if (this.tak >= 60) {
                    this.worldObj.setEntityState((Entity)this, (byte)51);
                    this.up = true;
                }
                this.setParasiteStatus(0);
                this.setTargetedEntity(0);
            }
        }
        if (this.getTargetedEntity() != null && (dis = this.getDistanceSqToEntity((Entity)this.getTargetedEntity())) < 25.0 && dis > 1.0) {
            EntityLivingBase target = this.getTargetedEntity();
            target.dismountRidingEntity();
            double str = 0.5;
            target.motionX += (Math.signum(this.posX - target.posX) * str - target.motionX) * str;
            target.motionZ += (Math.signum(this.posZ - target.posZ) * str - target.motionZ) * str;
            if (!this.worldObj.isRemote) {
                target.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.moveSlowdown, 20, 2, false, false));
            }
        }
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        if (source.getSourceOfDamage() instanceof EntitySRPProjectile) {
            EntityLivingBase target = this.getTargetedEntity();
            if (target != null) {
                target.attackEntityFrom(source, amount * 2.0f);
            }
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (this.worldObj.isRemote) {
            return true;
        }
        Item wea = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem();
        if (wea != SRPItems.itembase) {
            // empty if block
        }
        if (this.father != null) {
            this.father.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(MobEffects.GLOWING, 100, 1, false, false));
        }
        return super.processInteract(player, hand);
    }

    private void setTargetedEntity(int entityId) {
        EntityDataManager.of(this).set(TARGET_ENTITY, (Object)entityId);
    }

    public boolean hasTargetedEntity() {
        return (Integer)EntityDataManager.of(this).get(TARGET_ENTITY) != 0;
    }

    public EntityPDispatcher getFather() {
        return this.father;
    }

    public void setFather(EntityPDispatcher in) {
        this.father = in;
    }

    @Nullable
    public EntityLivingBase getTargetedEntity() {
        if (!this.hasTargetedEntity()) {
            return null;
        }
        if (this.worldObj.isRemote) {
            if (this.targetedEntity != null) {
                return this.targetedEntity;
            }
            Entity entity = this.worldObj.getEntityByID(((Integer)EntityDataManager.of(this).get(TARGET_ENTITY)).intValue());
            if (entity instanceof EntityLivingBase) {
                this.targetedEntity = (EntityLivingBase)entity;
                return this.targetedEntity;
            }
            return null;
        }
        return this.getAttackTarget();
    }

    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (TARGET_ENTITY.equals(key)) {
            this.targetedEntity = null;
        }
    }

    public float getEyeHeight() {
        return 1.8f;
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
}

