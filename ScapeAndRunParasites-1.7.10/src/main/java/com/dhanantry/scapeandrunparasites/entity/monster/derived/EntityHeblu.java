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
 *  net.minecraft.entity.IRangedAttackMob
 *  net.minecraft.entity.MoverType
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityMoveHelper
 *  net.minecraft.entity.ai.EntityMoveHelper$Action
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.derived;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeRangeSwitch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackRangedStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDerived;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileAlafhaBall;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileHebluLight;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import com.dhanantry.scapeandrunparasites.backport.entity.movement.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.sound.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityHeblu
extends EntityPDerived
implements EntityCutomAttack,
EntityBodyParts,
IRangedAttackMob,
EntityCanFly {
    protected static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityHeblu.class, (DataSerializer)DataSerializers.BOOLEAN);
    private int flying;
    private float aaa;
    private float sss;
    private int hebluLightBarrageSoundTimer = 0;
    private EntityBody leftTendril;
    private EntityBody rightTendril;
    private EntityBody head;
    private float leftTendrilHealth;
    private float rightTendrilHealth;
    private float headlHealth;
    public int vomit;
    private BlockPos vomitPos;
    public boolean raining;
    private int rainingOrbs = 0;
    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityHeblu.class, (DataSerializer)DataSerializers.BYTE);
    private static final DataParameter<Boolean> ATTACKING = EntityDataManager.createKey(EntityHeblu.class, (DataSerializer)DataSerializers.BOOLEAN);
    private int limit;
    private boolean skillFlame;
    private double tttX;
    private double tttY;
    private double tttZ;
    private double tttH;
    private double tttHH;

    public EntityHeblu(World worldIn) {
        super(worldIn);
        this.setSize(2.4f, 3.8f);
        this.stepHeight = 1.0f;
        this.canModRender = 0;
        this.type = (byte)14;
        this.killcount = -10.0;
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.flying = 0;
        this.skillFlame = false;
        this.leftTendril = new EntityBody(this, 3.3f, 2.5f, 1.0f, 4.1f, 3.3f, 1, 1, true);
        this.rightTendril = new EntityBody(this, 3.3f, 2.5f, 1.0f, 4.1f, 3.3f, -1, 2, true);
        this.head = new EntityBody(this, 2.2f, 2.2f, 1.0f, 4.0f, 2.0f, -1, 3, false, 0.2f);
        this.leftTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.rightTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.headlHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.ignoreFrustumCheck = true;
        this.moveHelper = new AIMoveControl(this);
    }

    @Override
    public int getParasiteIDRegister() {
        return 309;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimmingDiving((EntityLiving)this, 0.08));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatusAOE(this, 1.3, false, 8.0, 9.0));
        this.tasks.addTask(5, (EntityAIBase)new AIMoveRandom());
        this.tasks.addTask(6, (EntityAIBase)new AIFireballAttack(this));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightAttack(this, SRPConfig.derivedFollow, true, 3));
        this.tasks.addTask(6, (EntityAIBase)new EntityAIAttackMeleeRangeSwitch(this, 12.0f));
        this.tasks.addTask(4, (EntityAIBase)new EntityAIAttackRangedStatus(this, 1.3, 100, 40.0f, false));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.HEBLU_HEALTH);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.HEBLU_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.27);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.HEBLU_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.HEBLU_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.derivedFollow);
    }

    @Override
    public void onLivingUpdate() {
        if (this.isAIDisabled()) {
            return;
        }
        super.onLivingUpdate();
        this.killcount = -10.0;
        if (!this.worldObj.isRemote && this.hebluLightBarrageSoundTimer > 0) {
            --this.hebluLightBarrageSoundTimer;
            if (this.hebluLightBarrageSoundTimer == 0) {
                com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this.worldObj, null, this.posX, this.posY + (double)this.height * 0.6, this.posZ, SRPSounds.HEBLU_LIGHT_IMPACT, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.HOSTILE, 1.2f, 1.45f + this.rand.nextFloat() * 0.18f);
            }
        }
        if (this.headlHealth > 0.0f) {
            this.head.onUpdate();
        }
        if (this.leftTendrilHealth > 0.0f) {
            this.leftTendril.onUpdate();
        }
        if (this.rightTendrilHealth > 0.0f) {
            this.rightTendril.onUpdate();
        }
        if (!this.worldObj.isRemote && this.srpTicks == 10) {
            if ((BlockStates.get(this.worldObj, EntityQueries.position(this).down(1)).getBlock() != Blocks.air || BlockStates.get(this.worldObj, EntityQueries.position(this).down(2)).getBlock() != Blocks.air) && this.getFlyingState() && this.rand.nextInt(3) == 0) {
                this.motionY = 0.5;
            }
            if (this.rainingOrbs > 0) {
                --this.rainingOrbs;
                if (this.rainingOrbs <= 15) {
                    double radius = 10.0;
                    double x = this.posX + (this.rand.nextDouble() * 2.0 - 1.0) * radius;
                    double y = this.posY + 20.0;
                    double z = this.posZ + (this.rand.nextDouble() * 2.0 - 1.0) * radius;
                    if (this.getAttackTarget() != null && this.rand.nextBoolean()) {
                        x = this.getAttackTarget().posX + (this.rand.nextDouble() * 2.0 - 1.0) * radius;
                        z = this.getAttackTarget().posZ + (this.rand.nextDouble() * 2.0 - 1.0) * radius;
                    }
                    BlockPos pos = new BlockPos(x, y, z);
                    EntityProjectileAlafhaBall entitylargefireball = new EntityProjectileAlafhaBall(this.worldObj, (EntityLivingBase)this, 0.0, -10.0, 0.0);
                    entitylargefireball.posX = x;
                    entitylargefireball.posY = y;
                    entitylargefireball.posZ = z;
                    this.worldObj.spawnEntityInWorld((Entity)entitylargefireball);
                }
            }
            if (this.rand.nextInt(25) == 0 && !this.getFlyingState() && this.vomit <= 0) {
                this.changeStateTo(true);
                return;
            }
        }
        if (this.vomit > 0) {
            if (!this.worldObj.isRemote) {
                this.lookAt(this.vomitPos.getX(), this.vomitPos.getY(), this.vomitPos.getZ());
            }
            --this.vomit;
            if (this.worldObj.isRemote) {
                for (int i = 0; i < 19; ++i) {
                    Vec3d vec3d = this.getLook(1.0f);
                    double bon = 8.2;
                    double offsetX = this.posX + vec3d.xCoord * bon;
                    double offsetY = this.posY + (double)this.getEyeHeight() + 2.2;
                    double offsetZ = this.posZ + vec3d.zCoord * bon;
                    if (this.raining) {
                        if (this.getFlyingState()) {
                            bon = 4.3;
                            offsetX = this.posX + vec3d.xCoord * bon;
                            offsetY = this.posY + (double)this.getEyeHeight() + 7.5;
                            offsetZ = this.posZ + vec3d.zCoord * bon;
                        } else {
                            bon = 6.1;
                            offsetX = this.posX + vec3d.xCoord * bon;
                            offsetY = this.posY + (double)this.getEyeHeight() + 7.2;
                            offsetZ = this.posZ + vec3d.zCoord * bon;
                        }
                    }
                    double motionX = (double)(-MathHelper.sin((float)(this.rotationYaw * (float)Math.PI / 180.0f))) * 1.4;
                    double motionZ = (double)MathHelper.cos((float)(this.rotationYaw * (float)Math.PI / 180.0f)) * 1.4;
                    double motionY = -0.55 + this.rand.nextDouble() * 0.5;
                    double spreadFactor = 0.55;
                    motionX += (this.rand.nextDouble() - 0.5) * spreadFactor;
                    motionZ += (this.rand.nextDouble() - 0.5) * spreadFactor;
                    double rain = 1.0;
                    if (this.raining) {
                        motionY = 4.5 + this.rand.nextDouble() * 0.3;
                        spreadFactor = 0.2;
                        motionX += (this.rand.nextDouble() - 0.5) * spreadFactor;
                        motionZ += (this.rand.nextDouble() - 0.5) * spreadFactor;
                        rain = 0.2;
                    }
                    this.spawnParticles(EnumParticleTypes.FLAME, offsetX, offsetY, offsetZ, motionX * rain, motionY * rain, motionZ * rain);
                    this.spawnParticles(SRPEnumParticle.GCLOUD, -255, 0, 0, offsetX, offsetY, offsetZ, motionX * rain, motionY * rain, motionZ * rain);
                }
            }
        } else {
            this.raining = false;
        }
        if (this.flying >= 1) {
            ++this.flying;
        }
        if (this.getFlyingState()) {
            this.aaa += 0.08f;
            this.sss += 0.782f;
            if (this.sss >= 24.0f) {
                com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this, SoundEvents.ENTITY_ENDERDRAGON_FLAP, 5.0f, 0.8f + this.rand.nextFloat() * 0.3f);
                this.sss = 0.0f;
            }
            if (this.onGround && !this.worldObj.isRemote && this.flying > 40) {
                this.changeStateTo(false);
            }
        } else {
            this.aaa = 0.08f;
            this.sss = 0.0f;
        }
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.getFlyingState()) {
            com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityAccess.set(this, true);
        } else {
            com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityAccess.set(this, false);
        }
    }

    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        this.entityAge = 0;
        this.vomitPos = EntityQueries.position(target);
        if (target.posY > this.posY + 5.0 || target.posY < this.posY) {
            double d1 = 4.0;
            Vec3d vec3d = this.getLook(1.0f);
            double d2 = target.posX - (this.posX + vec3d.xCoord * 4.0);
            double d3 = Boxes.entityBox(target).minY + (double)(target.height / 2.0f) - (0.5 + this.posY + (double)(this.height / 2.0f));
            double d4 = target.posZ - (this.posZ + vec3d.zCoord * 4.0);
            this.worldObj.playEvent((EntityPlayer)null, 1016, EntityQueries.position(this), 0);
            EntityProjectileAlafhaBall entitylargefireball = new EntityProjectileAlafhaBall(this.worldObj, (EntityLivingBase)this, d2, d3, d4);
            entitylargefireball.posX = this.posX + vec3d.xCoord * 4.0;
            entitylargefireball.posY = this.posY + (double)(this.height / 2.0f) + 0.5;
            entitylargefireball.posZ = this.posZ + vec3d.zCoord * 4.0;
            this.worldObj.spawnEntityInWorld((Entity)entitylargefireball);
            for (int i = 0; i <= 2; ++i) {
                com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(this.worldObj, EnumParticleTypes.FLAME, this.posX + vec3d.xCoord * 4.0, this.posY + (double)(this.height / 2.0f) + 0.5, this.posZ + vec3d.zCoord * 4.0, 0.0, -1.0, 0.0, new int[0]);
            }
            return;
        }
        this.vomit = 40;
        if (this.rand.nextBoolean()) {
            this.raining = true;
            this.rainingOrbs = 19;
            this.worldObj.setEntityState((Entity)this, (byte)100);
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this, SRPSounds.HEBLU_SHOOT, this.getSoundVolume() * 2.0f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
        } else {
            this.worldObj.setEntityState((Entity)this, (byte)101);
        }
        if (!this.raining) {
            Vec3d vec3d = this.getLook(1.0f);
            double bon = 12.5;
            float rad = 2.0f;
            for (int i = 0; i < 3; ++i) {
                EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(this.worldObj, this.posX + vec3d.xCoord * bon, Math.max(this.posY, target.posY), this.posZ + vec3d.zCoord * bon);
                entityareaeffectcloud.setRadius(rad + 1.0f, 0.9f);
                entityareaeffectcloud.setDuration(100);
                entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
                entityareaeffectcloud.setOwner(this);
                this.worldObj.setEntityState((Entity)entityareaeffectcloud, (byte)77);
                entityareaeffectcloud.addEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.COTH_E, 300, 0, false, true));
                this.worldObj.spawnEntityInWorld((Entity)entityareaeffectcloud);
                if (i == 1) {
                    bon += 4.0;
                }
                bon += 7.5 + (double)i;
                rad += 2.0f;
            }
            this.vomitPos = new BlockPos(this.posX + vec3d.xCoord * bon, this.posY, this.posZ + vec3d.zCoord * bon);
        }
        this.setWait(80);
    }

    public void spawnLightBarrage(EntityLivingBase target) {
        this.spawnLightBarrage(target, false);
    }

    public void spawnLightBarrage(EntityLivingBase target, boolean forceTarget) {
        if (this.worldObj.isRemote) {
            return;
        }
        if (!forceTarget && target == null) {
            return;
        }
        if (!forceTarget && EntityHeblu.isInvalidLightBarrageTarget(target)) {
            return;
        }
        Vec3d look = this.getLook(1.0f);
        double startX = this.posX + look.xCoord * 2.5;
        double startY = this.posY + (double)this.height * 0.7 + 1.2;
        double startZ = this.posZ + look.zCoord * 2.5;
        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this.worldObj, null, this.posX, this.posY + (double)this.height * 0.6, this.posZ, SRPSounds.HEBLU_BELL_CHARGEUP, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.HOSTILE, 5.0f, 0.75f + this.rand.nextFloat() * 0.08f);
        this.hebluLightBarrageSoundTimer = 10;
        int count = Math.max(1, SRPConfigMobs.hebluLightBarrageCount);
        for (int i = 0; i < count; ++i) {
            double x = startX + (this.rand.nextDouble() - 0.5) * 3.5;
            double y = startY + (this.rand.nextDouble() - 0.5) * 2.5;
            double z = startZ + (this.rand.nextDouble() - 0.5) * 3.5;
            EntityProjectileHebluLight shard = new EntityProjectileHebluLight(this.worldObj, (EntityLivingBase)this, target, x, y, z, forceTarget);
            this.worldObj.spawnEntityInWorld((Entity)shard);
        }
    }

    public void setSwingingArms(boolean swingingArms) {
    }

    public float getaaa() {
        return this.aaa;
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        boolean flag = super.attackEntityFrom(source, amount);
        if (flag && this.rand.nextInt(12) == 0 && !this.getFlyingState()) {
            this.changeStateTo(true);
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
        return flag;
    }

    @Override
    public void setBodyPartDead(int id) {
        if (this.leftTendril.getId() == id) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.leftTendril);
        } else if (this.rightTendril.getId() == id) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.rightTendril);
        } else if (this.head.getId() == id) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.head);
        }
    }

    @Override
    public void setDead() {
        if (this.head != null) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.head);
        }
        if (this.leftTendril != null) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.leftTendril);
        }
        if (this.rightTendril != null) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.rightTendril);
        }
        super.setDead();
    }

    @Override
    protected void spawnCloneCosmical(EntityPCosmical entityout) {
        entityout.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        entityout.onInitialSpawn(DifficultyAccess.at(this.worldObj, EntityQueries.position(entityout)), null);
        if (this.hasCustomInventoryName()) {
            entityout.setMinecartName("--" + this.getCustomNameTag() + "--");
            entityout.setAlwaysRenderNameTag(this.getAlwaysRenderNameTag());
        }
        this.worldObj.spawnEntityInWorld((Entity)entityout);
        entityout.particleStatus((byte)7);
        this.limitClones = entityout.getEntityId();
        entityout.limitClones = this.getEntityId();
        this.setShadowStatus(false);
        entityout.setCloneC();
        entityout.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(entityout.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue() * 1.33);
        entityout.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(entityout.getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue() * 0.5);
        if (this.getFlyingState()) {
            ((EntityHeblu)entityout).changeStateTo(true);
        }
    }

    public void changeStateTo(boolean fly) {
        if (this.limit >= 1) {
            return;
        }
        if (fly) {
            if (!this.getFlyingState()) {
                if (this.leftTendrilHealth <= 0.0f || this.rightTendrilHealth <= 0.0f) {
                    return;
                }
                this.moveHelper = new AIMoveControl(this);
                this.setParasiteStatus(3);
                EntityDataManager.of(this).set(FLYING, (Object)true);
                this.motionY = 0.5;
                this.aaa += 0.08f;
                this.flying = 1;
                this.sss = 19.85f;
            }
        } else if (this.getFlyingState()) {
            this.moveHelper = new EntityMoveHelper((EntityLiving)this);
            this.setParasiteStatus(0);
            EntityDataManager.of(this).set(FLYING, (Object)false);
            this.flying = 0;
            this.aaa = 0.0f;
            this.sss = 0.0f;
        }
    }

    public float getEyeHeight() {
        return 1.75f;
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        return super.attackEntityAsMob(entityIn);
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        return this.attackEntityAsMob(entityIn);
    }

    @Override
    protected void selfExplode() {
    }

    @Override
    protected void spawnGore() {
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.HEBLU_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.HEBLU_HURT;
    }

    protected float getSoundVolume() {
        return 5.0f;
    }

    protected String getDeathSound() {
        return SRPSounds.HEBLU_DEATH;
    }

    protected String getStepSound() {
        return SRPSounds.HEAVY_STEPS;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this, this.getStepSound(), 0.15f, 1.0f);
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
        return floo;
    }

    public boolean getFlyingState() {
        return (Boolean)EntityDataManager.of(this).get(FLYING);
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

    @SideOnly(value=Side.CLIENT)
    public float getLeft() {
        return this.leftTendrilHealth;
    }

    @SideOnly(value=Side.CLIENT)
    public float getRight() {
        return this.rightTendrilHealth;
    }

    @SideOnly(value=Side.CLIENT)
    public float getHead() {
        return this.headlHealth;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void handleHealthUpdate(byte id) {
        if (id == 11) {
            this.leftTendrilHealth = 0.0f;
        } else if (id == 22) {
            this.rightTendrilHealth = 0.0f;
        } else if (id == 33) {
            this.headlHealth = 0.0f;
        } else if (id == 100) {
            this.vomit = 40;
            this.raining = true;
        } else if (id == 101) {
            this.vomit = 40;
        } else {
            super.handleHealthUpdate(id);
        }
    }

    @Override
    protected EntityPCosmical getThis() {
        return new EntityHeblu(this.worldObj);
    }

    public void moveEntity(MoverType type, double x, double y, double z) {
        super.moveEntity(type, x, y, z);
        com.dhanantry.scapeandrunparasites.backport.entity.movement.MovementAccess.blocks(this);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(VEX_FLAGS, (Object)((byte)0));
        EntityDataManager.of(this).register(FLYING, (Object)true);
        EntityDataManager.of(this).register(ATTACKING, (Object)false);
    }

    private boolean getVexFlag(int mask) {
        byte i = (Byte)EntityDataManager.of(this).get(VEX_FLAGS);
        return (i & mask) != 0;
    }

    private void setVexFlag(int mask, boolean value) {
        int i = ((Byte)EntityDataManager.of(this).get(VEX_FLAGS)).byteValue();
        i = value ? (i |= mask) : (i &= ~mask);
        EntityDataManager.of(this).set(VEX_FLAGS, (Object)((byte)(i & 0xFF)));
    }

    @SideOnly(value=Side.CLIENT)
    public boolean isAttacking() {
        return (Boolean)EntityDataManager.of(this).get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        EntityDataManager.of(this).set(ATTACKING, (Object)attacking);
    }

    private static boolean isInvalidLightBarrageTarget(EntityLivingBase target) {
        if (target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)target;
            return player.isCreative() || player.isSpectator();
        }
        return false;
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 1: {
                return this.skillFlame;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 1: {
                this.skillFlame = in;
                return;
            }
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 1: {
                this.flame();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void flame() {
        if (this.getFlyingState() || this.headlHealth <= 0.0f) {
            this.skillFlame = true;
            this.limit = 0;
            return;
        }
        if (this.limit == 0) {
            EntityLivingBase entitylivingbase = this.getAttackTarget();
            if (entitylivingbase == null) {
                this.skillFlame = true;
                this.limit = 0;
                return;
            }
            this.tttX = entitylivingbase.posX;
            this.tttY = entitylivingbase.posY;
            this.tttZ = entitylivingbase.posZ;
            this.tttH = Boxes.entityBox(entitylivingbase).minY;
            this.tttHH = entitylivingbase.height;
        }
        ++this.limit;
        this.setParasiteStatus(10);
        this.getNavigator().tryMoveToXYZ(this.tttX, this.tttY, this.tttZ, 0.0);
        this.resetIdleTime();
        if (this.ticksExisted % 10 != 0) {
            return;
        }
        double d1 = 4.0;
        Vec3d vec3d = this.getLook(1.0f);
        double d2 = this.tttX - (this.posX + vec3d.xCoord * 4.0);
        double d3 = this.tttH + this.tttHH / 4.0 - (0.5 + this.posY + (double)(this.height / 4.0f));
        double d4 = this.tttZ - (this.posZ + vec3d.zCoord * 4.0);
        this.worldObj.playEvent((EntityPlayer)null, 1016, new BlockPos((Entity)this), 0);
        EntityProjectileAlafhaBall entitylargefireball = new EntityProjectileAlafhaBall(this.worldObj, (EntityLivingBase)this, d2, d3, d4);
        entitylargefireball.posX = this.posX + vec3d.xCoord * 4.0;
        entitylargefireball.posY = this.posY + (double)(this.height / 2.0f) + 0.5;
        entitylargefireball.posZ = this.posZ + vec3d.zCoord * 4.0;
        this.worldObj.spawnEntityInWorld((Entity)entitylargefireball);
        if (this.limit >= 60) {
            this.skillFlame = true;
            this.setParasiteStatus(0);
            this.limit = 0;
        }
    }

    static class AIFireballAttack
    extends EntityAIBase {
        private final EntityHeblu parentEntity;
        public int attackTimer;

        public AIFireballAttack(EntityHeblu ghast) {
            this.parentEntity = ghast;
        }

        public boolean shouldExecute() {
            return this.parentEntity.getAttackTarget() != null && this.parentEntity.getFlyingState() && this.parentEntity.headlHealth > 0.0f;
        }

        public void startExecuting() {
            this.attackTimer = 0;
        }

        public void resetTask() {
            this.parentEntity.setAttacking(false);
        }

        public void updateTask() {
            EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
            double d0 = 64.0;
            if (entitylivingbase == null) {
                return;
            }
            if (entitylivingbase.getDistanceSqToEntity((Entity)this.parentEntity) < 4096.0 && this.parentEntity.canEntityBeSeen((Entity)entitylivingbase)) {
                World world = this.parentEntity.worldObj;
                ++this.attackTimer;
                if (this.parentEntity.isPotionActive(SRPPotions.RAGE_E)) {
                    ++this.attackTimer;
                }
                this.parentEntity.resetIdleTime();
                if (this.attackTimer == 10) {
                    // empty if block
                }
                if (this.attackTimer == 20) {
                    int specialAttack = this.parentEntity.worldObj.rand.nextInt(2);
                    if (specialAttack == 0 && entitylivingbase.onGround) {
                        this.parentEntity.vomitPos = EntityQueries.position(entitylivingbase);
                        this.parentEntity.vomit = 40;
                        this.parentEntity.raining = true;
                        this.parentEntity.rainingOrbs = 19;
                        world.setEntityState((Entity)this.parentEntity, (byte)100);
                        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this.parentEntity, SRPSounds.HEBLU_SHOOT, this.parentEntity.getSoundVolume() * 2.0f, (this.parentEntity.worldObj.rand.nextFloat() - this.parentEntity.worldObj.rand.nextFloat()) * 0.2f + 1.0f);
                        this.attackTimer = -60;
                        return;
                    }
                    if (specialAttack == 1 && !EntityHeblu.isInvalidLightBarrageTarget(entitylivingbase)) {
                        this.parentEntity.spawnLightBarrage(entitylivingbase);
                        this.attackTimer = -85;
                        return;
                    }
                    double d1 = 4.0;
                    Vec3d vec3d = this.parentEntity.getLook(1.0f);
                    double d2 = entitylivingbase.posX - (this.parentEntity.posX + vec3d.xCoord * 4.0);
                    double d3 = Boxes.entityBox(entitylivingbase).minY + (double)(entitylivingbase.height / 2.0f) - (0.5 + this.parentEntity.posY + (double)(this.parentEntity.height / 2.0f));
                    double d4 = entitylivingbase.posZ - (this.parentEntity.posZ + vec3d.zCoord * 4.0);
                    world.playEvent((EntityPlayer)null, 1016, new BlockPos((Entity)this.parentEntity), 0);
                    EntityProjectileAlafhaBall entitylargefireball = new EntityProjectileAlafhaBall(world, (EntityLivingBase)this.parentEntity, d2, d3, d4);
                    entitylargefireball.posX = this.parentEntity.posX + vec3d.xCoord * 4.0;
                    entitylargefireball.posY = this.parentEntity.posY + (double)(this.parentEntity.height / 2.0f) + 0.5;
                    entitylargefireball.posZ = this.parentEntity.posZ + vec3d.zCoord * 4.0;
                    world.spawnEntityInWorld((Entity)entitylargefireball);
                    this.parentEntity.entityAge = 0;
                    this.attackTimer = -45;
                    for (int i = 0; i <= 2; ++i) {
                        com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(this.parentEntity.worldObj, EnumParticleTypes.FLAME, this.parentEntity.posX + vec3d.xCoord * 4.0, this.parentEntity.posY + (double)(this.parentEntity.height / 2.0f) + 0.5, this.parentEntity.posZ + vec3d.zCoord * 4.0, 0.0, -1.0, 0.0, new int[0]);
                    }
                }
            } else if (this.attackTimer > 0) {
                --this.attackTimer;
            }
            this.parentEntity.setAttacking(this.attackTimer > 10);
        }
    }

    class AIMoveRandom
    extends EntityAIBase {
        public AIMoveRandom() {
            this.setMutexBits(1);
        }

        public boolean shouldExecute() {
            return !EntityHeblu.this.getMoveHelper().isUpdating() && EntityHeblu.this.rand.nextInt(5) == 0 && EntityHeblu.this.getFlyingState();
        }

        public boolean continueExecuting() {
            return false;
        }

        public void updateTask() {
            BlockPos blockpos = new BlockPos((Entity)EntityHeblu.this);
            int flag = 1;
            double speed = 0.5;
            if (EntityHeblu.this.getAttackTarget() != null) {
                if (EntityHeblu.this.getDistanceSqToEntity((Entity)EntityHeblu.this.getAttackTarget()) > 100.0) {
                    blockpos = new BlockPos((Entity)EntityHeblu.this.getAttackTarget());
                    flag = 2;
                    speed += 0.25;
                } else if (EntityHeblu.this.getDistanceSqToEntity((Entity)EntityHeblu.this.getAttackTarget()) < 36.0) {
                    blockpos = new BlockPos((Entity)EntityHeblu.this.getAttackTarget());
                    flag = 3;
                    speed += 0.25;
                }
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.add(EntityHeblu.this.rand.nextInt(15) - 7, EntityHeblu.this.rand.nextInt(11) - 5, EntityHeblu.this.rand.nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.add(EntityHeblu.this.rand.nextInt(6) - 2, EntityHeblu.this.rand.nextInt(7) - 2, EntityHeblu.this.rand.nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.add(EntityHeblu.this.rand.nextInt(4) + 3, EntityHeblu.this.rand.nextInt(5) + 4, EntityHeblu.this.rand.nextInt(4) + 3);
                }
                if (!WorldAccess.isAirBlock(EntityHeblu.this.worldObj, blockpos1)) continue;
                EntityHeblu.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityHeblu.this.getAttackTarget() != null) break;
                EntityHeblu.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper {
        public AIMoveControl(EntityHeblu vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityHeblu.this.posX;
                double d1 = this.posY - EntityHeblu.this.posY;
                double d2 = this.posZ - EntityHeblu.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityHeblu.this).getAverageEdgeLength()) {
                    this.action = com.dhanantry.scapeandrunparasites.backport.entity.ai.FlightMoveHelper.Action.WAIT;
                    EntityHeblu.this.motionX *= 0.5;
                    EntityHeblu.this.motionY *= 0.5;
                    EntityHeblu.this.motionZ *= 0.5;
                } else {
                    EntityHeblu.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityHeblu.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityHeblu.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityHeblu.this.getAttackTarget() == null) {
                        EntityHeblu.this.renderYawOffset = EntityHeblu.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)EntityHeblu.this.motionX, (double)EntityHeblu.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityHeblu.this.getAttackTarget().posX - EntityHeblu.this.posX;
                        double d5 = EntityHeblu.this.getAttackTarget().posZ - EntityHeblu.this.posZ;
                        EntityHeblu.this.renderYawOffset = EntityHeblu.this.rotationYaw = -((float)com.dhanantry.scapeandrunparasites.backport.math.ModernMath.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}

