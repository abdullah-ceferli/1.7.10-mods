/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nullable
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.entity.ai.EntityAISwimming
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.passive.EntityVillager
 *  net.minecraft.entity.passive.EntityWaterMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAvoidEntityStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAvoidOrAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityCruxA;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityCruxB
extends EntityParasiteBase {
    private float tSize;
    private float aSize;
    private boolean st1;
    private float tSpeed;
    private int growing;
    private int currentgrow;
    private static final DataParameter<Float> SIZE = EntityDataManager.createKey(EntityCruxB.class, (DataSerializer)DataSerializers.FLOAT);

    public EntityCruxB(World worldIn) {
        super(worldIn);
        int atm;
        this.setSize(1.31f, 1.1f);
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityPlayer>(this, EntityPlayer.class, 0, false, false, null, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
        if (SRPConfig.mobattacking) {
            this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus<EntityLiving>(this, EntityLiving.class, 0, false, false, new Predicate<EntityLiving>(){

                public boolean apply(@Nullable EntityLiving entity) {
                    return !(entity instanceof EntityWaterMob) && !ParasiteEventEntity.checkEntity((EntityLivingBase)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
        }
        if ((atm = SRPConfigMobs.cruxMaxGrowTime - SRPConfigMobs.cruxMinGrowTime + 1) <= 0 || SRPConfigMobs.cruxMaxGrowTime <= SRPConfigMobs.cruxMinGrowTime) {
            atm = SRPConfigMobs.cruxMinGrowTime;
        }
        this.growing = this.rand.nextInt(atm) + SRPConfigMobs.cruxMinGrowTime;
        this.st1 = false;
        this.tSize = 1.0f;
        this.aSize = 1.0f;
        this.fuseTime = 70;
        this.killcount = -10.0;
        this.type = (byte)5;
    }

    @Override
    public int getParasiteIDRegister() {
        return 320;
    }

    protected void initEntityAI() {
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
        this.tasks.addTask(8, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatus(this, 1.0, false, -1.0));
        this.tasks.addTask(4, (EntityAIBase)new EntityAIAvoidOrAttack(this, 0.0f, 10, 2));
        this.tasks.addTask(5, new EntityAIAvoidEntityStatus<EntityLiving>(this, EntityLiving.class, new Predicate<EntityLiving>(){

            public boolean apply(@Nullable EntityLiving entity) {
                return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityParasiteBase) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager);
            }
        }, 8.0f, 1.3));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(SIZE, (Object)Float.valueOf(this.tSize));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.CRUXA_HEALTH * 0.3);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.CRUXA_ARMOR * 0.3);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.CRUXA_KD_RESISTANCE * 0.3);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.CRUXA_ATTACK_DAMAGE * 0.3);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.28);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.primitiveFollow);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.gettSize() > this.getaSize()) {
            this.setaSize(0.01f);
            this.setSize(0.7f + (this.getaSize() - 1.0f), 0.5f + (this.getaSize() - 1.0f));
        }
        ++this.currentgrow;
        if (this.currentgrow > this.growing * 20) {
            this.setSelfeState(1);
        }
        if (!this.worldObj.isRemote && !this.isDead && this.getHealth() > 0.0f && this.getHealth() < this.getMaxHealth()) {
            this.setHealth(this.getHealth() + 0.007f);
        }
    }

    public void onUpdate() {
        if (this.isEntityAlive()) {
            this.lastActiveTime = this.timeSinceIgnited;
            this.dyingBurst(false, 2);
        }
        super.onUpdate();
    }

    @Override
    protected void dyingBurst(boolean fromDeath, int value) {
        int i = this.getSelfeState();
        if (i > 0 && this.timeSinceIgnited == 0) {
            this.playSound(SRPSounds.FLESH_GROW, 10.0f, 1.0f);
        }
        this.timeSinceIgnited += i * value;
        if (this.timeSinceIgnited < 0) {
            this.timeSinceIgnited = 0;
        }
        if (this.timeSinceIgnited >= this.fuseTime) {
            this.timeSinceIgnited = this.fuseTime;
            this.selfExplode();
        }
    }

    @Override
    protected void selfExplode() {
        if (!this.worldObj.isRemote) {
            ParasiteEventEntity.spawnNext(this, new EntityCruxA(this.worldObj), true, false);
            this.playSound(SRPSounds.FLESH_PRIMITIVE, 1.0f, 1.0f);
        } else {
            this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
        }
    }

    public float getEyeHeight() {
        return 0.3f;
    }

    public float gettSize() {
        return ((Float)EntityDataManager.of(this).get(SIZE)).floatValue();
    }

    public void settSize(float in) {
        this.tSize += in;
        EntityDataManager.of(this).set(SIZE, (Object)Float.valueOf(this.tSize));
    }

    public void settSpeed(float in) {
        this.tSpeed = in;
    }

    public float getaSize() {
        return this.aSize;
    }

    public void setaSize(float in) {
        this.aSize += in;
    }

    protected String getLivingSound() {
        return SRPSounds.FLESH_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.FLESH_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.FLESH_DEATH;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setFloat("leshtotalsize", this.tSize);
        compound.setFloat("leshactualsize", this.aSize);
        compound.setFloat("leshspeed", this.tSpeed);
        compound.setInteger("growing", this.growing);
        compound.setInteger("currentg", this.currentgrow);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("leshtotalsize", 99)) {
            this.tSize = compound.getFloat("leshtotalsize");
            EntityDataManager.of(this).set(SIZE, (Object)Float.valueOf(compound.getFloat("leshtotalsize")));
        }
        if (compound.hasKey("leshspeed", 99)) {
            this.tSpeed = compound.getFloat("leshspeed");
        }
        if (compound.hasKey("growing", 99)) {
            this.growing = compound.getInteger("growing");
        }
        if (compound.hasKey("currentg", 99)) {
            this.currentgrow = compound.getInteger("currentg");
        }
    }

    @SideOnly(value=Side.CLIENT)
    public float getSelfeFlashIntensityS() {
        return this.aSize;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void handleHealthUpdate(byte id) {
        if (id == 4) {
            this.tSize += 0.3f;
        } else {
            super.handleHealthUpdate(id);
        }
    }
}

