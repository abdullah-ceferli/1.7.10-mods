/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAILeapAtTarget
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.entity.ai.EntityAINearestAttackableTarget
 *  net.minecraft.entity.ai.EntityAISwimming
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.ai.attributes.IAttributeInstance
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EntityDamageSourceIndirect
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.living.EnderTeleportEvent
 *  net.minecraftforge.fml.common.eventhandler.Event
 */
package com.dhanantry.scapeandrunparasites.entity.monster.infected.head;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanMelt;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfEnderman;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundEvents;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import cpw.mods.fml.common.eventhandler.Event;

public class EntityInfEndermanHead
extends EntityPInfected {
    private static final DataParameter<Boolean> SCREAMING = EntityDataManager.createKey(EntityInfEndermanHead.class, (DataSerializer)DataSerializers.BOOLEAN);
    private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final AttributeModifier ATTACKING_SPEED_BOOST = new AttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", (double)0.15f, 0).setSaved(false);
    private int lastCreepySound;
    private int targetChangeTime;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int ally;
    private EntityParasiteBase toTele;

    public EntityInfEndermanHead(World worldIn) {
        super(worldIn);
        this.setSize(0.7f, 0.9f);
        this.killcount = -10.0;
        this.attackSpeedT = 15;
    }

    @Override
    public int getParasiteIDRegister() {
        return 69;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infendermanCanSpawnAssimilatedNat;
    }

    protected void initEntityAI() {
        this.tasks.addTask(0, (EntityAIBase)new EntityAISkill(this, 40, 100, 3, true, 14));
        this.setskillLeapValues(0.7f, 2.5, 0);
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, false, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
        this.tasks.addTask(8, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
        this.tasks.addTask(2, (EntityAIBase)new EntityAILeapAtTarget((EntityLiving)this, 0.4f));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatus(this, 1.3, false, -1.0));
        this.targetTasks.addTask(5, (EntityAIBase)new EntityAINearestAttackableTarget((EntityCreature)this, EntityInhooM.class, true));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.INFENDERMAN_HEADHEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.4);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.INFENDERMAN_HEADDAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(32.0);
    }

    @Override
    public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn) {
        super.setAttackTarget(entitylivingbaseIn);
        IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        if (entitylivingbaseIn == null) {
            this.targetChangeTime = 0;
            EntityDataManager.of(this).set(SCREAMING, (Object)false);
            iattributeinstance.removeModifier(ATTACKING_SPEED_BOOST);
        } else {
            this.targetChangeTime = this.ticksExisted;
            EntityDataManager.of(this).set(SCREAMING, (Object)true);
            if (!iattributeinstance.hasModifier(ATTACKING_SPEED_BOOST)) {
                iattributeinstance.applyModifier(ATTACKING_SPEED_BOOST);
            }
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(SCREAMING, (Object)false);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.worldObj.isRemote) {
            for (int i = 0; i < 2; ++i) {
                com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(this.worldObj, EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height - 0.25, this.posZ + (this.rand.nextDouble() - 0.5) * (double)this.width, (this.rand.nextDouble() - 0.5) * 2.0, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5) * 2.0, new int[0]);
            }
        } else {
            if (this.getAttackTarget() != null && this.ticksExisted % 20 == 0 && this.getDistanceSqToEntity((Entity)this.getAttackTarget()) > 4.0 && this.rand.nextInt(SRPConfigMobs.infendermantelefreq * 2) == 0 && !this.teleportAllies()) {
                this.teleportRandomly();
            }
            if (SRPConfigSystems.disloGiveBodies && this.isEntityAlive() && this.srpTicks == 10 && SRPSaveData.get(this.worldObj, 44).getCurrentCode(this.worldObj.provider.dimensionId, 20) >= 1) {
                ParasiteEventEntity.spawnNext(this, new EntityInfEnderman(this.worldObj), true, false);
                return;
            }
        }
        if (this.ally > 0 && !this.worldObj.isRemote) {
            ++this.ally;
            this.teleportAlly();
        }
        this.isJumping = false;
    }

    protected boolean teleportAllies() {
        if (this.ally > 0 || !SRPConfigMobs.infendermanteleally) {
            return false;
        }
        EntityLivingBase target = this.getAttackTarget();
        if (target == null) {
            return false;
        }
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 64.0);
        List moblist = EntityQueries.entities(this.worldObj, EntityParasiteBase.class, axisalignedbb);
        for (EntityParasiteBase mob : moblist) {
            if (!(mob instanceof EntityMudo) || mob.getAttackTarget() != null || !this.teleportToEntity((Entity)mob, 1.0)) continue;
            this.setCoordTarget(target.posX, target.posY, target.posZ);
            this.toTele = mob;
            this.setWorkTask(false);
            this.ally = 1;
            return true;
        }
        for (EntityParasiteBase mob : moblist) {
            if (!(mob instanceof EntityPInfected) || mob.width > 0.7f || mob.height > 0.9f || mob == this || mob.getAttackTarget() != null || mob.getParasiteType() > 15 || mob instanceof EntityCanMelt && ((EntityCanMelt)((Object)mob)).isMelting() || !this.teleportToEntity((Entity)mob, 1.0)) continue;
            this.setCoordTarget(target.posX, target.posY, target.posZ);
            this.toTele = mob;
            this.setWorkTask(false);
            this.ally = 1;
            return true;
        }
        return false;
    }

    private void teleportAlly() {
        block8: {
            block7: {
                EntityLivingBase target;
                if (this.ally < 8) {
                    return;
                }
                if (this.toTele == null) break block7;
                if (!this.toTele.isEntityAlive()) {
                    this.toTele = null;
                    this.ally = 0;
                    this.setWorkTask(true);
                    boolean flag2 = false;
                    for (int lag2 = 10; !flag2 && lag2 > 0; --lag2) {
                        flag2 = this.teleportToPos(this.targetX, this.targetY, this.targetZ, 8.0);
                    }
                    return;
                }
                boolean flag1 = false;
                for (int lag1 = 10; !flag1 && lag1 > 0 && !(flag1 = this.teleportToPos(this.targetX, this.targetY, this.targetZ, 8.0)); --lag1) {
                }
                if (!flag1) break block8;
                boolean flag2 = false;
                int lag2 = 12;
                this.toTele.copyLocationAndAnglesFrom((Entity)this);
                if (this.toTele.getParasiteIDRegister() != 59 && this.toTele.getParasiteIDRegister() != 69) {
                    this.toTele.attackEntityFrom(DamageSource.fall, SRPConfigMobs.infendermanTeleDamage);
                }
                if (this.isBurning() && this.rand.nextBoolean()) {
                    this.toTele.setFire(8);
                }
                com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this.worldObj, (EntityPlayer)null, this.toTele.prevPosX, this.toTele.prevPosY, this.toTele.prevPosZ, SRPSounds.INFECTEDENDERMAN_PORTAL, com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.category(this), 1.0f, 1.0f);
                flag2 = true;
                if (!flag2 || (target = this.getAttackTarget()) == null || !target.isEntityAlive()) break block8;
                this.toTele.setAttackTarget(target);
                break block8;
            }
            boolean flag2 = false;
            for (int lag2 = 10; !flag2 && lag2 > 0; --lag2) {
                flag2 = this.teleportToPos(this.targetX, this.targetY, this.targetZ, 8.0);
            }
        }
        this.setWorkTask(true);
        this.ally = 0;
        this.toTele = null;
    }

    protected boolean teleportRandomly() {
        double d0 = this.posX + (this.rand.nextDouble() - 0.5) * 64.0;
        double d1 = this.posY + (double)(this.rand.nextInt(64) - 32);
        double d2 = this.posZ + (this.rand.nextDouble() - 0.5) * 64.0;
        return this.teleportTo(d0, d1, d2);
    }

    protected boolean teleportToEntity(Entity in, double dis) {
        double d1 = in.posX + (this.rand.nextDouble() - 0.5) * dis;
        double d2 = in.posY + (double)(this.rand.nextInt(16) - 8) * dis;
        double d3 = in.posZ + (this.rand.nextDouble() - 0.5) * dis;
        return this.teleportTo(d1, d2, d3);
    }

    protected boolean teleportToPos(double x, double y, double z, double dis) {
        double d1 = x + (this.rand.nextDouble() - 0.5) * dis;
        double d2 = y + (double)(this.rand.nextInt(16) - 8) * dis;
        double d3 = z + (this.rand.nextDouble() - 0.5) * dis;
        return this.teleportTo(d1, d2, d3);
    }

    protected boolean teleportEntityTo(EntityParasiteBase in, double x, double y, double z, double dis) {
        double d1 = x + (this.rand.nextDouble() - 0.5) * dis;
        double d2 = y + (double)(this.rand.nextInt(16) - 8) * dis;
        double d3 = z + (this.rand.nextDouble() - 0.5) * dis;
        return this.teleportTo(in, d1, d2, d3);
    }

    private boolean teleportTo(double x, double y, double z) {
        EnderTeleportEvent event = new EnderTeleportEvent((EntityLivingBase)this, x, y, z, 0.0f);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return false;
        }
        boolean flag = this.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ());
        if (flag) {
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this.worldObj, (EntityPlayer)null, this.prevPosX, this.prevPosY, this.prevPosZ, SRPSounds.INFECTEDENDERMAN_PORTAL, com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.category(this), 1.0f, 1.0f);
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this, SRPSounds.INFECTEDENDERMAN_PORTAL, 1.0f, 1.0f);
        }
        return flag;
    }

    public boolean attemptTeleport(double x, double y, double z) {
        double d0 = this.posX;
        double d1 = this.posY;
        double d2 = this.posZ;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        boolean flag = false;
        BlockPos blockpos = new BlockPos((Entity)this);
        World world = this.worldObj;
        Random random = this.getRNG();
        if (WorldAccess.isBlockLoaded(world, blockpos)) {
            boolean flag1 = false;
            while (!flag1 && blockpos.getY() > 0) {
                BlockPos blockpos1 = blockpos.down();
                IBlockState iblockstate = BlockStates.get(world, blockpos1);
                if (iblockstate.getMaterial().blocksMovement() || iblockstate.getBlock() instanceof BlockLiquid) {
                    flag1 = true;
                    continue;
                }
                this.posY -= 1.0;
                blockpos = blockpos1;
            }
            if (flag1) {
                this.setPositionAndUpdate(this.posX, this.posY, this.posZ);
                if (world.getCollisionBoxes((Entity)this, Boxes.entityBox(this)).isEmpty() && !world.isAnyLiquid(Boxes.entityBox(this))) {
                    flag = true;
                }
            }
        }
        if (!flag) {
            this.setPositionAndUpdate(d0, d1, d2);
            return false;
        }
        int i = 128;
        for (int j = 0; j < 128; ++j) {
            double d6 = (double)j / 127.0;
            float f = (random.nextFloat() - 0.5f) * 0.2f;
            float f1 = (random.nextFloat() - 0.5f) * 0.2f;
            float f2 = (random.nextFloat() - 0.5f) * 0.2f;
            double d3 = d0 + (this.posX - d0) * d6 + (random.nextDouble() - 0.5) * (double)this.width * 2.0;
            double d4 = d1 + (this.posY - d1) * d6 + random.nextDouble() * (double)this.height;
            double d5 = d2 + (this.posZ - d2) * d6 + (random.nextDouble() - 0.5) * (double)this.width * 2.0;
            com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(world, EnumParticleTypes.PORTAL, d3, d4, d5, (double)f, (double)f1, (double)f2, new int[0]);
        }
        if (this instanceof EntityCreature) {
            this.getNavigator().clearPathEntity();
        }
        return true;
    }

    private boolean teleportTo(EntityParasiteBase in, double x, double y, double z) {
        EnderTeleportEvent event = new EnderTeleportEvent((EntityLivingBase)this, x, y, z, 0.0f);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return false;
        }
        boolean flag = in.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ());
        if (flag) {
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(in.worldObj, (EntityPlayer)null, in.prevPosX, in.prevPosY, in.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.category(in), 1.0f, 1.0f);
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(in, SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0f, 1.0f);
        }
        return flag;
    }

    private void setCoordTarget(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) {
            return false;
        }
        if (source instanceof EntityDamageSourceIndirect) {
            for (int i = 0; i < 64; ++i) {
                if (!this.teleportRandomly()) continue;
                return true;
            }
            return false;
        }
        boolean flag = super.attackEntityFrom(source, amount);
        if (source.isUnblockable() && this.rand.nextInt(SRPConfigMobs.infendermantelefreq * 2) == 0 && !this.teleportAllies()) {
            this.teleportRandomly();
        }
        return flag;
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        if (entityIn instanceof EntityInhooM && entityIn.isEntityAlive() && this.isEntityAlive()) {
            ParasiteEventEntity.spawnNext(this, new EntityInfEnderman(this.worldObj), true, false);
            ((EntityParasiteBase)entityIn).particleStatus((byte)7);
            entityIn.setDead();
            return true;
        }
        boolean flag = super.attackEntityAsMob(entityIn);
        if (flag) {
            if (this.rand.nextDouble() < (double)SRPConfig.infectedBleedingChance && entityIn instanceof EntityLivingBase) {
                SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (EntityLivingBase)entityIn, 100, 0);
            }
            if (this.rand.nextInt(SRPConfigMobs.infendermantelefreq * 2) == 0 && !this.teleportAllies()) {
                this.teleportRandomly();
            }
        }
        return flag;
    }

    public void setInWeb() {
    }

    public float getEyeHeight() {
        return 0.8f;
    }

    @Override
    public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
        if (cls == EntityPlayer.class || cls == EntityPlayerMP.class) {
            return true;
        }
        String name = null;
        try {
            name = EntityList.getKey(cls).toString();
        }
        catch (Exception e) {
            return true;
        }
        if (name == null) {
            return true;
        }
        if (name.contains("srparasites") && cls != EntityInhooM.class) {
            return false;
        }
        return !SRPConfig.mobAttackingFull || !ParasiteEventEntity.checkName(name, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
    }

    public boolean isScreaming() {
        return (Boolean)EntityDataManager.of(this).get(SCREAMING);
    }

    public void fall(float distance, float damageMultiplier) {
        super.fall(distance, damageMultiplier * 0.3f);
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this, this.getStepSound(), this.getSoundVolume(), this.getSoundPitch());
    }

    protected String getStepSound() {
        return SRPSounds.SMALL_STEPS;
    }
}

