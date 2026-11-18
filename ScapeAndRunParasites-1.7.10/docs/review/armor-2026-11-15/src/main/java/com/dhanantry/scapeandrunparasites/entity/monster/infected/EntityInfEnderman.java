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
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.ai.attributes.IAttributeInstance
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.MobEffects
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EntityDamageSourceIndirect
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.living.EnderTeleportEvent
 *  net.minecraftforge.fml.common.eventhandler.Event
 */
package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.EntityHitbox;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanMelt;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPFeral;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerEnderman;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
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
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import cpw.mods.fml.common.eventhandler.Event;

public class EntityInfEnderman
extends EntityPInfected {
    private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final AttributeModifier ATTACKING_SPEED_BOOST = new AttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", (double)0.15f, 0).setSaved(false);
    private static final DataParameter<Boolean> SCREAMING = EntityDataManager.createKey(EntityInfEnderman.class, (DataSerializer)DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CRAWLING = EntityDataManager.createKey(EntityInfEnderman.class, (DataSerializer)DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> TEXTURE_VARIANT = EntityDataManager.createKey(EntityInfEnderman.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Boolean> ARIRAL = EntityDataManager.createKey(EntityInfEnderman.class, (DataSerializer)DataSerializers.BOOLEAN);
    private int lastCreepySound;
    private int targetChangeTime;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int ally;
    private EntityParasiteBase toTele;
    private int toTeleCool;
    private int spotCool;
    private String tpPlayerName;
    private EntityHitbox head;
    private int waterHurtCooldown = 0;

    public EntityInfEnderman(World worldIn) {
        super(worldIn);
        this.setSize(0.6f, 2.3f);
        this.canModRender = 0;
        this.type = (byte)14;
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.killcount = -10.0;
        this.stepHeight = 1.0f;
        this.ally = 0;
        this.tpPlayerName = null;
        this.head = new EntityHitbox((EntityLiving)this, 0.0f, 0.0f, 2.3f, 0.6f, 0.6f, 1.25f);
        this.hitboxes = new EntityHitbox[]{this.head};
    }

    @Override
    public int getParasiteIDRegister() {
        return 59;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infendermanCanSpawnAssimilatedNat;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimmingDiving((EntityLiving)this, 0.08));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatus(this, 1.2, false, 0.0));
        this.tasks.addTask(8, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.INFENDERMAN_HEALTH);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.INFENDERMAN_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue((double)0.3f);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.INFENDERMAN_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.INFENDERMAN_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(64.0);
    }

    @Override
    public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn) {
        boolean flag = this.getAttackTarget() == null;
        super.setAttackTarget(entitylivingbaseIn);
        IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        if (entitylivingbaseIn == null) {
            this.targetChangeTime = 0;
            EntityDataManager.of(this).set(SCREAMING, (Object)false);
            iattributeinstance.removeModifier(ATTACKING_SPEED_BOOST);
        } else {
            this.targetChangeTime = this.ticksExisted;
            EntityDataManager.of(this).set(SCREAMING, (Object)true);
            if (flag) {
                this.spotCool = SRPConfigMobs.infendermansaw;
                if (entitylivingbaseIn instanceof EntityPlayer) {
                    if (this.tpPlayerName != null) {
                        if (!this.tpPlayerName.equals(entitylivingbaseIn.getCommandSenderName())) {
                            this.tpPlayerName = entitylivingbaseIn.getCommandSenderName();
                            this.worldObj.playSound(null, entitylivingbaseIn.prevPosX, entitylivingbaseIn.prevPosY, entitylivingbaseIn.prevPosZ, SRPSounds.INFECTEDENDERMAN_PORTAL, this.getSoundCategory(), 0.3f, 1.0f);
                        }
                    } else {
                        this.tpPlayerName = entitylivingbaseIn.getCommandSenderName();
                        this.worldObj.playSound(null, entitylivingbaseIn.prevPosX, entitylivingbaseIn.prevPosY, entitylivingbaseIn.prevPosZ, SRPSounds.INFECTEDENDERMAN_PORTAL, this.getSoundCategory(), 0.3f, 1.0f);
                    }
                } else {
                    this.tpPlayerName = null;
                }
            }
            if (!iattributeinstance.hasModifier(ATTACKING_SPEED_BOOST)) {
                iattributeinstance.applyModifier(ATTACKING_SPEED_BOOST);
            }
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(TEXTURE_VARIANT, (Object)0);
        EntityDataManager.of(this).register(SCREAMING, (Object)false);
        EntityDataManager.of(this).register(CRAWLING, (Object)false);
        EntityDataManager.of(this).register(ARIRAL, (Object)false);
    }

    public int getTextureVariant() {
        return (Integer)EntityDataManager.of(this).get(TEXTURE_VARIANT);
    }

    public void setTextureVariant(int variant) {
        EntityDataManager.of(this).set(TEXTURE_VARIANT, (Object)variant);
    }

    public void playEndermanSound() {
        if (this.ticksExisted >= this.lastCreepySound + 400) {
            this.lastCreepySound = this.ticksExisted;
            if (!this.isSilent()) {
                this.worldObj.playSound(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, SoundEvents.ENTITY_ENDERMEN_STARE, this.getSoundCategory(), 2.5f, 1.0f, false);
            }
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.isCrawling()) {
            this.setSize(0.95f, 1.25f);
        }
        if (this.worldObj.isRemote) {
            for (int i = 0; i < 2; ++i) {
                this.worldObj.spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height - 0.25, this.posZ + (this.rand.nextDouble() - 0.5) * (double)this.width, (this.rand.nextDouble() - 0.5) * 2.0, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5) * 2.0, new int[0]);
            }
        } else {
            if (SRPConfigMobs.infendermanWaterDamageEnabled && this.isInWater()) {
                if (this.waterHurtCooldown-- <= 0) {
                    this.attackEntityFrom(DamageSource.drown, 2.0f);
                    this.waterHurtCooldown = 20;
                    this.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.weakness, 20, 1, false, true));
                }
            } else {
                this.waterHurtCooldown = 0;
            }
            if (this.getAttackTarget() != null && this.ticksExisted % 20 == 0 && this.getDistanceSqToEntity((Entity)this.getAttackTarget()) > Math.pow(SRPAttributes.INFENDERMAN_TP_DIST / 2.0, 2.0) && this.rand.nextInt(SRPConfigMobs.infendermantelefreq) == 0 && !this.teleportAllies()) {
                this.teleportRandomly();
            }
        }
        if (!this.worldObj.isRemote) {
            if (this.ally > 0) {
                ++this.ally;
                this.teleportAlly();
            }
            if (this.spotCool >= 0) {
                --this.spotCool;
            }
            if (this.toTeleCool >= 0) {
                --this.toTeleCool;
            }
            if (this.srpTicks == 10 && this.isPotionActive(SRPPotions.RAGE_E)) {
                this.spotCool = 0;
                this.toTeleCool = 0;
            }
        }
        this.isJumping = false;
    }

    @Override
    public void updateHitboxes() {
        super.updateHitboxes();
        if (this.head != null) {
            boolean isCrawling = this.isCrawling();
            this.head.setRadius(isCrawling ? 0.9f : 0.0f);
            this.head.setAngle(isCrawling ? 1.6f : 0.0f);
            this.head.setPositionAndUpdate(this.head.posX, this.head.posY - (double)(isCrawling ? 1.625f : 0.0f), this.head.posZ);
        }
    }

    protected boolean teleportAllies() {
        if (this.ally > 0 || !SRPConfigMobs.infendermanteleally || this.toTeleCool > 0 || this.spotCool > 0) {
            return false;
        }
        EntityLivingBase target = this.getAttackTarget();
        if (target == null) {
            return false;
        }
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 64.0);
        List moblist = EntityQueries.entities(this.worldObj, EntityPInfected.class, axisalignedbb);
        for (EntityParasiteBase mob : moblist) {
            if (mob == this || mob.getAttackTarget() != null || mob.getParasiteType() > 15 || mob instanceof EntityCanMelt && ((EntityCanMelt)((Object)mob)).isMelting() || mob.getHealth() - SRPConfigMobs.infendermanTeleDamage < 2.0f || !this.teleportToEntity((Entity)mob, 1.0)) continue;
            this.setCoordTarget(target.posX, target.posY, target.posZ);
            this.toTele = mob;
            this.setWorkTask(false);
            this.ally = 1;
            return true;
        }
        for (EntityParasiteBase mob : moblist) {
            if (!(mob instanceof EntityMudo) || mob.getAttackTarget() != null || mob.getHealth() - SRPConfigMobs.infendermanTeleDamage < 2.0f || !this.teleportToEntity((Entity)mob, 1.0)) continue;
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
                if (this.isBurning() && this.rand.nextInt(4) != 0) {
                    this.toTele.setFire(8);
                }
                this.worldObj.playSound((EntityPlayer)null, this.toTele.prevPosX, this.toTele.prevPosY, this.toTele.prevPosZ, SRPSounds.INFECTEDENDERMAN_PORTAL, this.getSoundCategory(), 1.0f, 1.0f);
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
        this.toTeleCool = SRPConfigMobs.infendermanallyCool;
    }

    protected boolean teleportRandomly() {
        if (this.spotCool > 0) {
            return false;
        }
        double d0 = this.posX + (this.rand.nextDouble() - 0.5) * 64.0;
        double d1 = this.posY + (double)(this.rand.nextInt(64) - 32);
        double d2 = this.posZ + (this.rand.nextDouble() - 0.5) * 64.0;
        if (this.getAttackTarget() != null && this.getAttackTarget().getDistance(d0, d1, d2) < Math.pow(SRPAttributes.INFENDERMAN_TP_DIST, 2.0)) {
            return false;
        }
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
        if (this.getAttackTarget() != null && this.getAttackTarget().getDistance(d1, d2, d3) < 10.0) {
            return false;
        }
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
            this.worldObj.playSound((EntityPlayer)null, this.prevPosX, this.prevPosY, this.prevPosZ, SRPSounds.INFECTEDENDERMAN_PORTAL, this.getSoundCategory(), 1.0f, 1.0f);
            this.playSound(SRPSounds.INFECTEDENDERMAN_PORTAL, 1.0f, 1.0f);
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
            world.spawnParticle(EnumParticleTypes.PORTAL, d3, d4, d5, (double)f, (double)f1, (double)f2, new int[0]);
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
            in.worldObj.playSound((EntityPlayer)null, in.prevPosX, in.prevPosY, in.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, in.getSoundCategory(), 1.0f, 1.0f);
            in.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0f, 1.0f);
        }
        return flag;
    }

    private void setCoordTarget(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
    }

    @Override
    protected void onDeathUpdate() {
        super.onDeathUpdate();
        if (this.deathTime == 20 && !this.worldObj.isRemote && this.rand.nextDouble() <= SRPAttributes.INFENDERMAN_HEADCHANCE) {
            ParasiteSummon.spawnM(this, new String[]{"srparasites:sim_endermanhead;1;1"}, 0, false, this.getCustomNameTag());
        }
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        this.spotCool = 0;
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
        if (source.isUnblockable() && this.rand.nextInt(SRPConfigMobs.infendermantelefreq) == 0 && !this.teleportAllies()) {
            this.teleportRandomly();
        }
        return flag;
    }

    @Override
    public void cycleManualVariant() {
        if (this.isAriral()) {
            this.setAriral(false);
            this.setTextureVariant(0);
            return;
        }
        if (this.getTextureVariant() == 0) {
            this.setTextureVariant(1);
            this.setAriral(false);
            return;
        }
        this.setTextureVariant(0);
        this.setAriral(true);
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        boolean flag = super.attackEntityAsMob(entityIn);
        if (flag) {
            if (this.rand.nextDouble() < (double)SRPConfig.infectedBleedingChance && entityIn instanceof EntityLivingBase) {
                SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (EntityLivingBase)entityIn, 100, 0);
            }
            if (this.rand.nextInt(SRPConfigMobs.infendermantelefreq) == 0 && !this.teleportAllies()) {
                this.teleportRandomly();
            }
        }
        return flag;
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        this.tpPlayerName = null;
    }

    public boolean isScreaming() {
        return (Boolean)EntityDataManager.of(this).get(SCREAMING);
    }

    public boolean isCrawling() {
        return (Boolean)EntityDataManager.of(this).get(CRAWLING);
    }

    public void setCrawling(boolean in) {
        EntityDataManager.of(this).set(CRAWLING, (Object)in);
    }

    public float getEyeHeight() {
        return this.height * 0.88f;
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.INFECTEDENDERMAN_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.INFECTEDENDERMAN_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.INFECTEDENDERMAN_DEATH;
    }

    protected String getStepSound() {
        return SoundEvents.ENTITY_ZOMBIE_STEP;
    }

    @Override
    public EntityPFeral getFeral(World in) {
        return new EntityFerEnderman(in);
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("SRPTextureVariant", this.getTextureVariant());
        compound.setBoolean("thiscancrawl", this.isCrawling());
        compound.setBoolean("SRPAriral", this.isAriral());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("SRPAriral")) {
            this.setAriral(compound.getBoolean("SRPAriral"));
        }
        if (compound.hasKey("SRPTextureVariant")) {
            this.setTextureVariant(compound.getInteger("SRPTextureVariant"));
        }
        if (compound.hasKey("thiscancrawl", 99)) {
            this.setCrawling(compound.getBoolean("thiscancrawl"));
            if (this.isCrawling()) {
                // empty if block
            }
        }
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        this.setTextureVariant(this.rand.nextBoolean() ? 1 : 0);
        if (this.rand.nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant) {
            this.setCrawling(true);
        }
        return livingdata;
    }

    public boolean isAriral() {
        return (Boolean)EntityDataManager.of(this).get(ARIRAL);
    }

    public void setAriral(boolean value) {
        EntityDataManager.of(this).set(ARIRAL, (Object)value);
    }
}

