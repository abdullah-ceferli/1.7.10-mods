/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAISwimming
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.pathfinding.PathNodeType
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.adapted;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFollowBodies;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanHaveBodies;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityWymoAdapted
extends EntityPAdapted
implements EntityCanHaveBodies,
EntityCutomAttack {
    private UUID following;
    private boolean canF;
    private BlockPos digTarget;
    public float diggingModel;
    public final float heightTwo = 2.4f;
    private int bodiesT;
    private int bodiesTOut;
    private float attackTimer;
    private boolean up;
    private static final DataParameter<Byte> PART = EntityDataManager.createKey(EntityWymoAdapted.class, (DataSerializer)DataSerializers.BYTE);
    private static final DataParameter<Boolean> TAIL = EntityDataManager.createKey(EntityWymoAdapted.class, (DataSerializer)DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DIG = EntityDataManager.createKey(EntityWymoAdapted.class, (DataSerializer)DataSerializers.BOOLEAN);

    public EntityWymoAdapted(World worldIn) {
        super(worldIn);
        this.setSize(1.321f, 1.2f);
        this.ignoreFrustumCheck = true;
        this.stepHeight = 1.0f;
        this.canModRender = 1;
        this.borderOrb = -1;
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.setPathPriority(PathNodeType.WATER, -1.0f);
        this.diggingModel = 0.0f;
        this.canF = true;
        this.bodiesT = 0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 57;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(PART, (Object)((byte)0));
        EntityDataManager.of(this).register(TAIL, (Object)false);
        EntityDataManager.of(this).register(DIG, (Object)false);
    }

    protected void initEntityAI() {
        this.tasks.addTask(1, (EntityAIBase)new EntityAIFollowBodies(this, 1.9f, 0.32, 5, 10000.0, 140));
        this.targetTasks.addTask(2, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatusAOE(this, 1.3, false, 0.0, 4.0));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.WYMO_HEALTH + SRPAttributes.WYMO_A_HEALTH);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.WYMO_ARMOR + SRPAttributes.WYMO_A_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.32);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.WYMO_ATTACK_DAMAGE + SRPAttributes.WYMO_A_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.adaptedFollow);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.handleDigging();
        if (this.up) {
            this.attackTimer = (float)((double)this.attackTimer + 0.3);
            if ((double)this.attackTimer > 1.5) {
                this.up = false;
            }
        } else {
            this.attackTimer = (float)((double)this.attackTimer - 0.2);
        }
    }

    @Override
    public boolean canDespawn() {
        return super.canDespawn() && this.following == null;
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        if (this.getDigging()) {
            return false;
        }
        boolean flag = super.attackEntityAsMob(entityIn);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        this.up = true;
        this.attackTimer = 0.0f;
        this.worldObj.setEntityState((Entity)this, (byte)12);
        boolean flag = false;
        this.playSound(SRPSounds.SWIPE, 2.0f, 1.0f);
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(entityIn.posX, entityIn.posY, entityIn.posZ, entityIn.posX + 1.0, entityIn.posY + 1.0, entityIn.posZ + 1.0), 1.5);
        List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
        for (EntityLivingBase mob : moblist) {
            if (mob instanceof EntityParasiteBase || !this.canEntityBeSeen((Entity)mob) || !this.attackEntityAsMob((Entity)mob)) continue;
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        this.madeRng = 0;
        if (source == DamageSource.inWall) {
            return false;
        }
        if (this.bodiesT - 1 >= this.getBodyNumber()) {
            return false;
        }
        boolean flag = super.attackEntityFrom(source, amount);
        if (flag && this.following != null) {
            Entity follo = this.getFather(this.following);
            if (follo == null) {
                this.following = null;
                return flag;
            }
            if (!follo.isEntityAlive()) {
                this.following = null;
                return flag;
            }
            follo.attackEntityFrom(source, amount * 0.5f);
        }
        return flag;
    }

    @Override
    protected void attackEntityFromCap(int go) {
        if (this.getBodyNumber() != 0) {
            return;
        }
        super.attackEntityFromCap(go);
    }

    public void applyEntityCollision(Entity entityIn) {
        if (this.getDigging()) {
            return;
        }
        super.applyEntityCollision(entityIn);
    }

    public float getEyeHeight() {
        return 1.0f;
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.AWYMO_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        if (this.rand.nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.AWYMO_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.AWYMO_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SRPSounds.MOBSILENCE, 0.15f, 1.0f);
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
        return floo;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void spawnEffectsGore() {
        for (int i = 0; i <= 60; ++i) {
            if (i % 4 == 0) {
                this.spawnParticles(SRPEnumParticle.GCLOUD, 150, 0, 0);
            }
            if (i % 5 != 0) continue;
            this.spawnParticles(SRPEnumParticle.GSPLASH, 2, -1, -1);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (this.following != null) {
            compound.setUniqueId("wormhead", this.following);
        }
        compound.setBoolean("wormcanf", this.canF);
        compound.setByte("wormpart", this.getBodyNumber());
        compound.setBoolean("wormtail", this.getBodyTail());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasUniqueId("wormhead")) {
            this.following = compound.getUniqueId("wormhead");
        }
        if (compound.hasKey("wormcanf", 99)) {
            this.canF = compound.getBoolean("wormcanf");
        }
        if (compound.hasKey("wormpart", 99)) {
            this.setBodyNumber(compound.getByte("wormpart"));
        }
        if (compound.hasKey("wormtail", 99)) {
            this.setBodyTail(compound.getBoolean("wormtail"));
        }
    }

    @Override
    public void skillBreakBlocks() {
        if (this.getBodyNumber() == 0) {
            super.skillBreakBlocks();
        }
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn) {
        if (this.getBodyNumber() == 0) {
            super.onKillEntity(entityLivingIn);
            return;
        }
        EntityParasiteBase head = this.getHead();
        if (head != null) {
            head.setKillC(1.0);
        }
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
    public void setAttackTarget(EntityLivingBase entitylivingbaseIn) {
        if (this.getBodyNumber() == 0) {
            super.setAttackTarget(entitylivingbaseIn);
        }
    }

    @Override
    public void setTargetPos(BlockPos in) {
        this.digTarget = in;
    }

    @Override
    public BlockPos getTargetPos() {
        return this.digTarget;
    }

    @Override
    public boolean getDigging() {
        return (Boolean)EntityDataManager.of(this).get(DIG);
    }

    @Override
    public void setDigging(boolean in) {
        if (in) {
            this.playSound(SRPSounds.AWYMO_DIG, 2.0f, this.getSoundPitch());
        }
        EntityDataManager.of(this).set(DIG, (Object)in);
    }

    @Override
    public float getDigModel() {
        return this.diggingModel;
    }

    @Override
    public int getBodiesT() {
        return this.bodiesT;
    }

    @Override
    public void setBodiesT(int in) {
        this.bodiesT = in;
    }

    @Override
    public byte getBodyNumber() {
        return (Byte)EntityDataManager.of(this).get(PART);
    }

    @Override
    public void setBodyNumber(int texture) {
        EntityDataManager.of(this).set(PART, (Object)((byte)texture));
    }

    @Override
    public boolean getBodyTail() {
        return (Boolean)EntityDataManager.of(this).get(TAIL);
    }

    @Override
    public void setBodyTail(boolean in) {
        EntityDataManager.of(this).set(TAIL, (Object)in);
    }

    @Override
    public void setFollowing(EntityCanHaveBodies in) {
        if (in == null) {
            return;
        }
        this.following = in.getEntity().getUniqueID();
    }

    @Override
    public UUID getFollowing() {
        return this.following;
    }

    @Override
    public int getBodyLength() {
        return 4;
    }

    @Override
    public EntityCanHaveBodies getAnotherBody(World in) {
        return new EntityWymoAdapted(in);
    }

    @Override
    public void copyCopy(EntityCanHaveBodies in) {
        this.copyLocationAndAnglesFrom(in.getEntity());
        this.posY += 0.5;
    }

    @Override
    public Entity getEntity() {
        return this;
    }

    @Override
    public void onSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        this.onInitialSpawn(difficulty, livingdata);
    }

    @Override
    public void setCanF(boolean in) {
        this.canF = in;
    }

    @Override
    public boolean getCanF() {
        return this.canF;
    }

    @Override
    public void bodyPartEffect() {
        if (this.getDigging()) {
            return;
        }
        if (this.getBodyNumber() >= 1 && this.getBodyNumber() <= 3) {
            AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(EntityQueries.position(this)), 5.0);
            List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
            for (EntityLivingBase mob : moblist) {
                EntityPlayer player;
                if (mob instanceof EntityParasiteBase || mob instanceof EntityPlayer && (player = (EntityPlayer)mob).isCreative() || !this.attackEntityAsMobAOE((Entity)mob)) continue;
                return;
            }
        }
    }

    private void spawnGroundParticles() {
        IBlockState state = BlockStates.get(this.worldObj, EntityQueries.position(this).down());
        if (state.getBlock() != Blocks.air) {
            int id = BlockStates.stateId((IBlockState)state);
            for (int i = 0; i < 15; ++i) {
                com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(this.worldObj, EnumParticleTypes.BLOCK_CRACK, this.posX + (double)(this.rand.nextFloat() * this.width * 3.0f) - (double)this.width, this.posY, this.posZ + (double)(this.rand.nextFloat() * this.width * 3.0f) - (double)this.width, this.rand.nextGaussian() * 0.02, this.rand.nextGaussian() * 0.02, this.rand.nextGaussian() * 0.02, new int[]{id});
            }
        }
    }

    @Override
    public double getKillPoints() {
        return this.killcount;
    }

    @Override
    public EntityParasiteBase getEvolution(World in) {
        return null;
    }

    @Override
    public EntityParasiteBase getHead() {
        if (this.following != null) {
            EntityCanHaveBodies follo = (EntityCanHaveBodies)this.getFather(this.following);
            if (follo == null) {
                return null;
            }
            if (!follo.getEntity().isEntityAlive()) {
                return null;
            }
            return follo.getHead();
        }
        return this;
    }

    @Override
    public Entity getFather(UUID uuid) {
        List serverList = EntityQueries.loaded(this.worldObj);
        for (int x = 0; x < serverList.size(); ++x) {
            if (!uuid.equals(((Entity)serverList.get(x)).getUniqueID())) continue;
            return (Entity)serverList.get(x);
        }
        return null;
    }

    @Override
    public void handleDigging() {
        if (this.getDigging()) {
            this.bodiesTOut = 0;
            if (this.getBodyNumber() == 0) {
                this.spawnGroundParticles();
                if (this.height > 0.25f) {
                    this.setSize(this.width, this.height - 0.15f);
                }
                if (this.diggingModel < 2.4f) {
                    this.diggingModel += 0.08f;
                }
            } else {
                if (this.srpTicks == 10) {
                    ++this.bodiesT;
                }
                if (this.bodiesT >= this.getBodyNumber()) {
                    this.spawnGroundParticles();
                    if (this.height > 0.25f) {
                        this.setSize(this.width, this.height - 0.15f);
                    }
                    if (this.diggingModel < 2.4f) {
                        this.diggingModel += 0.08f;
                    }
                }
            }
        } else {
            this.bodiesT = 0;
            if (this.getBodyNumber() == 0) {
                if (this.height < 1.2f) {
                    this.setSize(this.width, this.height + 0.13f);
                    this.spawnGroundParticles();
                }
                if (this.diggingModel >= 0.0f) {
                    this.diggingModel -= 0.08f;
                }
            } else if (this.posX != this.prevPosX && this.posZ != this.prevPosZ) {
                if (this.height < 1.2f) {
                    this.setSize(this.width, this.height + 0.13f);
                    this.spawnGroundParticles();
                }
                if (this.diggingModel >= 0.0f) {
                    this.diggingModel -= 0.08f;
                }
            }
        }
    }
}

