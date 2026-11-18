/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanMelt;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityMes;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPReference;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityInfPlayer
extends EntityPInfected
implements EntityCanMelt {
    private static final DataParameter<Float> HEIGH = EntityDataManager.createKey(EntityInfPlayer.class, (DataSerializer)DataSerializers.FLOAT);
    private static final DataParameter<Boolean> MELTING = EntityDataManager.createKey(EntityInfPlayer.class, (DataSerializer)DataSerializers.BOOLEAN);
    private float aSize;
    private int sound;
    private static final DataParameter<Boolean> HELM = EntityDataManager.createKey(EntityInfPlayer.class, (DataSerializer)DataSerializers.BOOLEAN);

    public EntityInfPlayer(World worldIn) {
        super(worldIn);
        this.setSize(0.6f, 1.95f);
        this.aSize = 1.0f;
        this.sound = 0;
        this.canModRender = 1;
        this.type = (byte)11;
        this.thisMelting = true;
    }

    @Override
    public int getParasiteIDRegister() {
        return 40;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infhumanCanSpawnAssimilatedNat;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimmingDiving((EntityLiving)this, 0.08));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatus(this, 1.0, false, 0.0));
        this.tasks.addTask(8, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
        this.tasks.addTask(6, (EntityAIBase)new EntityAIGetFollowers(this, 1, 16));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.INFADVENTURER_HEALTH);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.INFADVENTURER_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.230000004172325);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.INFADVENTURER_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.INFADVENTURER_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.infectedFollow);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(HEIGH, (Object)Float.valueOf(0.0f));
        EntityDataManager.of(this).register(MELTING, (Object)false);
        EntityDataManager.of(this).register(HELM, (Object)false);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.melting();
        if (!this.worldObj.isRemote && this.srpTicks == 10 && this.killcount > (double)SRPConfigMobs.infadventurerThrall && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityMes(this.worldObj), true, true);
        }
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.ASSIM_ADVENTURER_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.ASSIM_ADVENTURER_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.ASSIM_ADVENTURER_DEATH;
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        if (!this.worldObj.isRemote && this.killcount > (double)SRPConfigMobs.infadventurerThrall && !this.isDead && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityMes(this.worldObj), true, true);
        }
    }

    @Override
    protected void onDeathUpdate() {
        super.onDeathUpdate();
        if (this.getTHeigh() < 1.57f && !this.worldObj.isRemote) {
            this.setTHeigh(0.17f);
        }
        if (this.deathTime == 20 && !this.worldObj.isRemote && this.rand.nextDouble() <= SRPAttributes.INFADVENTURER_HEADCHANCE) {
            ParasiteSummon.spawnM(this, new String[]{"srparasites:sim_adventurerhead;1;1"}, 0, false, this.getCustomNameTag());
        }
    }

    @Override
    public void melt() {
        this.setWait(1000);
        EntityDataManager.of(this).set(HEIGH, (Object)Float.valueOf(1.95f));
        EntityDataManager.of(this).set(MELTING, (Object)true);
    }

    @Override
    public void melting() {
        if (this.isMelting()) {
            if (this.sound % 20 == 0) {
                this.playSound(SRPSounds.INFECTED_MELT, 1.0f, 1.0f);
            }
            ++this.sound;
            if ((double)this.getTHeigh() > 0.7) {
                this.setaSize(-0.005f);
                this.setTHeigh(-0.01f);
                this.setSize(this.width, this.getTHeigh());
            }
            if (!this.worldObj.isRemote) {
                if ((double)this.getTHeigh() <= 0.7 || this.sound >= 127) {
                    EntityLesh out = new EntityLesh(this.worldObj);
                    out.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
                    if (this.getCustomNameTag() != null) {
                        out.setMinecartName(this.getCustomNameTag());
                    }
                    this.setDead();
                    this.worldObj.spawnEntityInWorld((Entity)out);
                    out.setLegs(SRPAttributes.INFADVENTURER_V, false);
                }
            } else {
                this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 106, 0);
                this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
            }
        }
    }

    public boolean helmetSlot() {
        return (Boolean)EntityDataManager.of(this).get(HELM);
    }

    public void setHelmetSlot(boolean in) {
        EntityDataManager.of(this).set(HELM, (Object)in);
    }

    @Override
    public boolean isMelting() {
        return (Boolean)EntityDataManager.of(this).get(MELTING);
    }

    @Override
    public float getTHeigh() {
        return ((Float)EntityDataManager.of(this).get(HEIGH)).floatValue();
    }

    @Override
    public void setTHeigh(float in) {
        EntityDataManager.of(this).set(HEIGH, (Object)Float.valueOf(in += this.getTHeigh()));
    }

    @Override
    public float getaSize() {
        return this.aSize;
    }

    @Override
    public void setaSize(float in) {
        this.aSize += in;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public float getSelfeFlashIntensity2() {
        return this.aSize;
    }

    @Override
    protected void selfExplode() {
        super.selfExplode();
        ParasiteSummon.spawnM(this, new String[]{SRPConfigMobs.infadventurermob}, 0, false, this.getCustomNameTag());
    }

    public float getEyeHeight() {
        return 1.73f;
    }

    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
    }

    protected String getStepSound() {
        return SoundEvents.ENTITY_ZOMBIE_STEP;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
        SRPReference.setSimPlayerName(this);
        return floo;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("helmslot", this.helmetSlot());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("helmslot", 99)) {
            this.setHelmetSlot(compound.getBoolean("helmslot"));
        }
    }
}

