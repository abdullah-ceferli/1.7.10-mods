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
package com.dhanantry.scapeandrunparasites.entity.monster.awakened;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAncient;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
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

public class EntityOroncoAW
extends EntityPAncient {
    private static final DataParameter<Boolean> URTEN = EntityDataManager.createKey(EntityOroncoAW.class, (DataSerializer)DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ULTEN = EntityDataManager.createKey(EntityOroncoAW.class, (DataSerializer)DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> RATEN = EntityDataManager.createKey(EntityOroncoAW.class, (DataSerializer)DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LATEN = EntityDataManager.createKey(EntityOroncoAW.class, (DataSerializer)DataSerializers.BOOLEAN);
    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityOroncoAW.class, (DataSerializer)DataSerializers.BYTE);
    private boolean health80;
    private boolean health60;
    private boolean health40;
    private boolean health20;
    private float damageR = SRPAttributes.ORONCO_ATTACK_DAMAGE;
    private int maxY = 20;
    private int minY = 7;
    private EntityAIFlightLimits flightLimit = new EntityAIFlightLimits(this, this.maxY, true);
    private final BossInfoServer bossInfo = (BossInfoServer)new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS).setDarkenSky(false);

    public EntityOroncoAW(World worldIn) {
        super(worldIn);
        this.setSize(4.0f, 4.0f);
        this.type = (byte)62;
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.tasks.removeTask((EntityAIBase)this.aiWander);
        this.ignoreFrustumCheck = true;
        this.moveHelper = new AIMoveControl(this);
        this.setNoGravity(true);
        this.health80 = true;
        this.health60 = true;
        this.health40 = true;
        this.health20 = true;
    }

    @Override
    public int getParasiteIDRegister() {
        return 68;
    }

    protected void initEntityAI() {
        this.tasks.addTask(2, (EntityAIBase)new AIMoveRandom());
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(4, (EntityAIBase)new EntityAIFlightLimits(this, this.minY, false));
        this.tasks.addTask(5, (EntityAIBase)new EntityAIFlightAttack(this, 64.0));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.ORONCO_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.ORONCO_ARMOR);
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
        if (!flag || source != DamageSource.outOfWorld) {
            // empty if block
        }
        return flag;
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

    class AIMoveRandom
    extends EntityAIBase {
        public AIMoveRandom() {
            this.setMutexBits(1);
        }

        public boolean shouldExecute() {
            double d2;
            double d1;
            EntityMoveHelper entitymovehelper = EntityOroncoAW.this.getMoveHelper();
            if (!entitymovehelper.isUpdating()) {
                return true;
            }
            double d0 = entitymovehelper.getX() - EntityOroncoAW.this.posX;
            double d3 = d0 * d0 + (d1 = entitymovehelper.getY() - EntityOroncoAW.this.posY) * d1 + (d2 = entitymovehelper.getZ() - EntityOroncoAW.this.posZ) * d2;
            return d3 < 1.0 || d3 > 3600.0;
        }

        public boolean continueExecuting() {
            return false;
        }

        public void updateTask() {
            if (EntityOroncoAW.this.getAttackTarget() != null) {
                BlockPos blockpos = new BlockPos((Entity)EntityOroncoAW.this);
                int flag = 1;
                double speed = 0.3;
                if (EntityOroncoAW.this.getDistanceSqToEntity((Entity)EntityOroncoAW.this.getAttackTarget()) > 400.0) {
                    blockpos = new BlockPos((Entity)EntityOroncoAW.this.getAttackTarget());
                    flag = 2;
                    speed += 0.1;
                } else if (EntityOroncoAW.this.getDistanceSqToEntity((Entity)EntityOroncoAW.this.getAttackTarget()) < 100.0) {
                    blockpos = new BlockPos((Entity)EntityOroncoAW.this.getAttackTarget());
                    flag = 3;
                    speed += 0.15;
                }
                for (int i = 0; i < 3; ++i) {
                    BlockPos blockpos1 = blockpos.add(EntityOroncoAW.this.rand.nextInt(15) - 7, EntityOroncoAW.this.rand.nextInt(11) - 5, EntityOroncoAW.this.rand.nextInt(15) - 7);
                    if (flag == 2) {
                        blockpos1 = blockpos.add(EntityOroncoAW.this.rand.nextInt(12) - 2, EntityOroncoAW.this.rand.nextInt(10) - 2, EntityOroncoAW.this.rand.nextInt(12) - 2);
                    } else if (flag == 3) {
                        blockpos1 = blockpos.add(EntityOroncoAW.this.rand.nextInt(8) + 3, EntityOroncoAW.this.rand.nextInt(7) + 4, EntityOroncoAW.this.rand.nextInt(8) + 3);
                    }
                    if (!WorldAccess.isAirBlock(EntityOroncoAW.this.worldObj, blockpos1)) continue;
                    EntityOroncoAW.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                    if (EntityOroncoAW.this.getAttackTarget() == null) {
                        EntityOroncoAW.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                    }
                    break;
                }
            } else {
                Random random = EntityOroncoAW.this.getRNG();
                double d0 = EntityOroncoAW.this.posX + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                double d1 = EntityOroncoAW.this.posY + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                double d2 = EntityOroncoAW.this.posZ + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                EntityOroncoAW.this.getMoveHelper().setMoveTo(d0, d1, d2, 0.3);
            }
        }
    }

    class AIMoveControl
    extends EntityMoveHelper {
        public AIMoveControl(EntityOroncoAW vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == EntityMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityOroncoAW.this.posX;
                double d1 = this.posY - EntityOroncoAW.this.posY;
                double d2 = this.posZ - EntityOroncoAW.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityOroncoAW.this).getAverageEdgeLength()) {
                    this.action = EntityMoveHelper.Action.WAIT;
                    EntityOroncoAW.this.motionX *= 0.5;
                    EntityOroncoAW.this.motionY *= 0.5;
                    EntityOroncoAW.this.motionZ *= 0.5;
                } else {
                    EntityOroncoAW.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityOroncoAW.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityOroncoAW.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityOroncoAW.this.getAttackTarget() == null) {
                        EntityOroncoAW.this.renderYawOffset = EntityOroncoAW.this.rotationYaw = -((float)MathHelper.atan2((double)EntityOroncoAW.this.motionX, (double)EntityOroncoAW.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityOroncoAW.this.getAttackTarget().posX - EntityOroncoAW.this.posX;
                        double d5 = EntityOroncoAW.this.getAttackTarget().posZ - EntityOroncoAW.this.posZ;
                        EntityOroncoAW.this.renderYawOffset = EntityOroncoAW.this.rotationYaw = -((float)MathHelper.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}

