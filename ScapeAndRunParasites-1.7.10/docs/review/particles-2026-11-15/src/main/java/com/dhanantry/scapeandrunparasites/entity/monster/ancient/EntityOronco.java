/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.MoverType
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityMoveHelper
 *  net.minecraft.entity.ai.EntityMoveHelper$Action
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.entity.projectile.EntityFireball
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.BossInfo$Color
 *  net.minecraft.world.BossInfo$Overlay
 *  net.minecraft.world.BossInfoServer
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster.ancient;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.EntityBodyModel;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAncientSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAncient;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOroncoTen;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileAncientball;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;

public class EntityOronco
extends EntityPAncient
implements EntityCanShoot {
    private static final DataParameter<Boolean> URTEN = EntityDataManager.createKey(EntityOronco.class, (DataSerializer)DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ULTEN = EntityDataManager.createKey(EntityOronco.class, (DataSerializer)DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> RATEN = EntityDataManager.createKey(EntityOronco.class, (DataSerializer)DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LATEN = EntityDataManager.createKey(EntityOronco.class, (DataSerializer)DataSerializers.BOOLEAN);
    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityOronco.class, (DataSerializer)DataSerializers.BYTE);
    private boolean health80;
    private boolean health60;
    private boolean health40;
    private boolean health20;
    private float damageR = SRPAttributes.ORONCO_ATTACK_DAMAGE;
    private int maxY = 20;
    private int minY = 7;
    private EntityAIFlightLimits flightLimit = new EntityAIFlightLimits(this, this.maxY, true);
    private final BossInfoServer bossInfo = (BossInfoServer)new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS).setDarkenSky(false);
    private EntityBodyModel leftTendril;
    private EntityBodyModel rightTendril;

    public EntityOronco(World worldIn) {
        super(worldIn);
        this.setSize(4.0f, 4.0f);
        this.type = (byte)62;
        this.borderOrb = -1;
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.tasks.removeTask((EntityAIBase)this.aiWander);
        this.ignoreFrustumCheck = true;
        this.moveHelper = new AIMoveControl(this);
        this.setNoGravity(true);
        this.health80 = true;
        this.health60 = true;
        this.health40 = true;
        this.health20 = true;
        this.leftTendril = new EntityBodyModel(this, 0.6f, 2.0f, 1.0f, 2.17f, 1.4f, 1, 1, true);
        this.rightTendril = new EntityBodyModel(this, 0.6f, 2.0f, 1.0f, 2.17f, 1.4f, -1, 2, true);
        this.leftTendril.setSkin(1);
        this.rightTendril.setSkin(1);
    }

    @Override
    public int getParasiteIDRegister() {
        return 24;
    }

    protected void initEntityAI() {
        this.tasks.addTask(2, (EntityAIBase)new AIMoveRandom());
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(5, (EntityAIBase)new EntityAIAttackProjectile(this, 60, 20, 3, true));
        this.tasks.addTask(4, (EntityAIBase)new EntityAIAncientSummon(this, 20 * SRPConfigMobs.oroncoPodCooldown, SRPConfigMobs.oroncoPodNumber, new String[]{"srparasites:ancientpod;1;1"}));
        this.tasks.addTask(4, (EntityAIBase)new EntityAIFlightLimits(this, this.minY, false));
        this.tasks.addTask(5, (EntityAIBase)new EntityAIFlightAttack(this, 64.0));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.ORONCO_HEALTH);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.ORONCO_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(2.0);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.ancientFollow);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(URTEN, (Object)true);
        EntityDataManager.of(this).register(ULTEN, (Object)true);
        EntityDataManager.of(this).register(RATEN, (Object)true);
        EntityDataManager.of(this).register(LATEN, (Object)true);
        EntityDataManager.of(this).register(VEX_FLAGS, (Object)((byte)0));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.rightTendril != null) {
            this.rightTendril.onUpdate();
        }
        if (this.leftTendril != null) {
            this.leftTendril.onUpdate();
        }
        if (this.onGround && !this.worldObj.isRemote) {
            this.moveHelper.setMoveTo(this.posX, this.posY + 5.0, this.posZ, 0.5);
        }
        if (!this.worldObj.isRemote && this.getAttackTarget() != null && this.ticksExisted % 30 == 0) {
            for (EntityLivingBase entitylivingbase : EntityQueries.entities(this.worldObj, EntityLivingBase.class, Boxes.grow(Boxes.entityBox(this), 3.0, 3.0, 3.0))) {
                if (entitylivingbase == this || entitylivingbase instanceof EntityParasiteBase) continue;
                float f = (float)MathHelper.atan2((double)(entitylivingbase.posZ - this.posZ), (double)(entitylivingbase.posX - this.posX));
                EntityDamage damage = new EntityDamage(this.worldObj, entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, f, (EntityLivingBase)this, 1.0f, false, 2.5f);
                this.worldObj.spawnEntityInWorld((Entity)damage);
            }
        }
    }

    public void onUpdate() {
        super.onUpdate();
        this.setNoGravity(true);
    }

    @Override
    public void setDead() {
        if (this.leftTendril != null) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.leftTendril);
            this.leftTendril = null;
        }
        if (this.rightTendril != null) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.rightTendril);
            this.rightTendril = null;
        }
        super.setDead();
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }

    public void setMinecartName(String name) {
        super.setMinecartName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    public void addTrackingPlayer(EntityPlayerMP player) {
    }

    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        boolean flag = super.attackEntityFrom(source, amount);
        if (flag && source != DamageSource.outOfWorld) {
            if (source.getEntity() instanceof EntityPlayerMP) {
                this.bossInfo.addPlayer((EntityPlayerMP)source.getEntity());
            }
            if ((double)this.getMaxHealth() * 0.8 >= (double)this.getHealth() && this.health80) {
                this.damageBody(this.rand.nextInt(4));
                this.health80 = false;
                this.setParasiteStatus(77);
                this.setWait(30);
            } else if ((double)this.getMaxHealth() * 0.6 >= (double)this.getHealth() && this.health60) {
                this.damageBody(this.rand.nextInt(4));
                this.health60 = false;
                this.setParasiteStatus(77);
                this.setWait(30);
            } else if ((double)this.getMaxHealth() * 0.4 >= (double)this.getHealth() && this.health40) {
                this.damageBody(this.rand.nextInt(4));
                this.health40 = false;
                this.setParasiteStatus(77);
                this.setWait(30);
            } else if ((double)this.getMaxHealth() * 0.2 >= (double)this.getHealth() && this.health20) {
                this.damageBody(this.rand.nextInt(4));
                this.health20 = false;
                this.setParasiteStatus(77);
                this.setWait(30);
            }
        }
        return flag;
    }

    private void damageBody(int in) {
        if (!(this.livingTENUR() || this.livingTENUL() || this.livingTENRA() || this.livingTENLA())) {
            return;
        }
        if (in == 0 && this.livingTENUR()) {
            this.livingTENUR(false);
            EntityOroncoTen ten = new EntityOroncoTen(this.worldObj);
            ten.copyLocationAndAnglesFrom((Entity)this);
            this.worldObj.spawnEntityInWorld((Entity)ten);
            ten.setSkin(1);
        } else if (in == 1 && this.livingTENUL()) {
            this.livingTENUL(false);
            EntityOroncoTen ten = new EntityOroncoTen(this.worldObj);
            ten.copyLocationAndAnglesFrom((Entity)this);
            this.worldObj.spawnEntityInWorld((Entity)ten);
            ten.setSkin(1);
        } else if (in == 2 && this.livingTENRA()) {
            this.livingTENRA(false);
            EntityOroncoTen ten = new EntityOroncoTen(this.worldObj);
            ten.copyLocationAndAnglesFrom((Entity)this);
            this.worldObj.spawnEntityInWorld((Entity)ten);
        } else if (in == 3 && this.livingTENLA()) {
            this.livingTENLA(false);
            EntityOroncoTen ten = new EntityOroncoTen(this.worldObj);
            ten.copyLocationAndAnglesFrom((Entity)this);
            this.worldObj.spawnEntityInWorld((Entity)ten);
        } else {
            in = (in + 1) % 4;
            this.damageBody(in);
        }
    }

    public int getHorizontalFaceSpeed() {
        return 3;
    }

    public float getDamageR() {
        return this.damageR;
    }

    public float getEyeHeight() {
        return 2.1f;
    }

    protected String getLivingSound() {
        return SRPSounds.ORONCO_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        if (this.rand.nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.ORONCO_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.ORONCO_DEATH;
    }

    @Override
    public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    protected float getSoundVolume() {
        return 5.0f;
    }

    public boolean livingTENUR() {
        return (Boolean)EntityDataManager.of(this).get(URTEN);
    }

    public boolean livingTENUL() {
        return (Boolean)EntityDataManager.of(this).get(ULTEN);
    }

    public boolean livingTENRA() {
        return (Boolean)EntityDataManager.of(this).get(RATEN);
    }

    public boolean livingTENLA() {
        return (Boolean)EntityDataManager.of(this).get(LATEN);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("urten", ((Boolean)EntityDataManager.of(this).get(URTEN)).booleanValue());
        compound.setBoolean("ulten", ((Boolean)EntityDataManager.of(this).get(ULTEN)).booleanValue());
        compound.setBoolean("raten", ((Boolean)EntityDataManager.of(this).get(RATEN)).booleanValue());
        compound.setBoolean("laten", ((Boolean)EntityDataManager.of(this).get(LATEN)).booleanValue());
        compound.setBoolean("healtheight", this.health80);
        compound.setBoolean("healthsix", this.health60);
        compound.setBoolean("healthfour", this.health40);
        compound.setBoolean("healthtwo", this.health20);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("urten", 99)) {
            this.livingTENUR(compound.getBoolean("urten"));
        }
        if (compound.hasKey("ulten", 99)) {
            this.livingTENUL(compound.getBoolean("ulten"));
        }
        if (compound.hasKey("raten", 99)) {
            this.livingTENRA(compound.getBoolean("raten"));
        }
        if (compound.hasKey("laten", 99)) {
            this.livingTENLA(compound.getBoolean("laten"));
        }
        if (compound.hasKey("healtheight", 99)) {
            this.health80 = compound.getBoolean("healtheight");
        }
        if (compound.hasKey("healthsix", 99)) {
            this.health60 = compound.getBoolean("healthsix");
        }
        if (compound.hasKey("healthfour", 99)) {
            this.health40 = compound.getBoolean("healthfour");
        }
        if (compound.hasKey("healthtwo", 99)) {
            this.health20 = compound.getBoolean("healthtwo");
        }
    }

    public void livingTENUR(boolean in) {
        EntityDataManager.of(this).set(URTEN, (Object)in);
    }

    public void livingTENUL(boolean in) {
        EntityDataManager.of(this).set(ULTEN, (Object)in);
    }

    public void livingTENRA(boolean in) {
        EntityDataManager.of(this).set(RATEN, (Object)in);
    }

    public void livingTENLA(boolean in) {
        EntityDataManager.of(this).set(LATEN, (Object)in);
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    public boolean isOnLadder() {
        return false;
    }

    public void moveEntity(MoverType type, double x, double y, double z) {
        super.moveEntity(type, x, y, z);
        this.doBlockCollisions();
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

    public EntityFireball getProj(double accelX, double accelY, double accelZ) {
        this.playSound(SRPSounds.ORONCO_SHOOTINGREAL, 2.0f, 1.0f);
        return new EntityProjectileAncientball(this.worldObj, (EntityLivingBase)this, accelX, accelY, accelZ);
    }

    @Override
    public void playProjSound() {
        this.playSound(SRPSounds.ORONCO_SHOOTING, 4.0f, 1.0f);
    }

    class AIMoveRandom
    extends EntityAIBase {
        public AIMoveRandom() {
            this.setMutexBits(1);
        }

        public boolean shouldExecute() {
            double d2;
            double d1;
            EntityMoveHelper entitymovehelper = EntityOronco.this.getMoveHelper();
            if (!entitymovehelper.isUpdating()) {
                return true;
            }
            double d0 = entitymovehelper.getX() - EntityOronco.this.posX;
            double d3 = d0 * d0 + (d1 = entitymovehelper.getY() - EntityOronco.this.posY) * d1 + (d2 = entitymovehelper.getZ() - EntityOronco.this.posZ) * d2;
            return d3 < 1.0 || d3 > 3600.0;
        }

        public boolean continueExecuting() {
            return false;
        }

        public void updateTask() {
            if (EntityOronco.this.getAttackTarget() != null) {
                BlockPos blockpos = new BlockPos((Entity)EntityOronco.this);
                int flag = 1;
                double speed = 0.3;
                if (EntityOronco.this.getDistanceSqToEntity((Entity)EntityOronco.this.getAttackTarget()) > 400.0) {
                    blockpos = new BlockPos((Entity)EntityOronco.this.getAttackTarget());
                    flag = 2;
                    speed += 0.1;
                } else if (EntityOronco.this.getDistanceSqToEntity((Entity)EntityOronco.this.getAttackTarget()) < 100.0) {
                    blockpos = new BlockPos((Entity)EntityOronco.this.getAttackTarget());
                    flag = 3;
                    speed += 0.15;
                }
                for (int i = 0; i < 3; ++i) {
                    BlockPos blockpos1 = blockpos.add(EntityOronco.this.rand.nextInt(15) - 7, EntityOronco.this.rand.nextInt(11) - 5, EntityOronco.this.rand.nextInt(15) - 7);
                    if (flag == 2) {
                        blockpos1 = blockpos.add(EntityOronco.this.rand.nextInt(12) - 2, EntityOronco.this.rand.nextInt(10) - 2, EntityOronco.this.rand.nextInt(12) - 2);
                    } else if (flag == 3) {
                        blockpos1 = blockpos.add(EntityOronco.this.rand.nextInt(8) + 3, EntityOronco.this.rand.nextInt(7) + 4, EntityOronco.this.rand.nextInt(8) + 3);
                    }
                    if (!WorldAccess.isAirBlock(EntityOronco.this.worldObj, blockpos1)) continue;
                    EntityOronco.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                    if (EntityOronco.this.getAttackTarget() == null) {
                        EntityOronco.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                    }
                    break;
                }
            } else {
                Random random = EntityOronco.this.getRNG();
                double d0 = EntityOronco.this.posX + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                double d1 = EntityOronco.this.posY + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                double d2 = EntityOronco.this.posZ + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                EntityOronco.this.getMoveHelper().setMoveTo(d0, d1, d2, 0.3);
            }
        }
    }

    class AIMoveControl
    extends EntityMoveHelper {
        public AIMoveControl(EntityOronco vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == EntityMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityOronco.this.posX;
                double d1 = this.posY - EntityOronco.this.posY;
                double d2 = this.posZ - EntityOronco.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityOronco.this).getAverageEdgeLength()) {
                    this.action = EntityMoveHelper.Action.WAIT;
                    EntityOronco.this.motionX *= 0.5;
                    EntityOronco.this.motionY *= 0.5;
                    EntityOronco.this.motionZ *= 0.5;
                } else {
                    EntityOronco.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityOronco.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityOronco.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityOronco.this.getAttackTarget() == null) {
                        EntityOronco.this.renderYawOffset = EntityOronco.this.rotationYaw = -((float)MathHelper.atan2((double)EntityOronco.this.motionX, (double)EntityOronco.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityOronco.this.getAttackTarget().posX - EntityOronco.this.posX;
                        double d5 = EntityOronco.this.getAttackTarget().posZ - EntityOronco.this.posZ;
                        EntityOronco.this.renderYawOffset = EntityOronco.this.rotationYaw = -((float)MathHelper.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}

