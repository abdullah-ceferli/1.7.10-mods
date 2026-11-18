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
 *  net.minecraft.entity.MoverType
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityMoveHelper
 *  net.minecraft.entity.ai.EntityMoveHelper$Action
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.BossInfo$Color
 *  net.minecraft.world.BossInfo$Overlay
 *  net.minecraft.world.BossInfoServer
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityTendril;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfDragonEHead;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileDragonE;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.SRPPacketEntityBodyDead;
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
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyInstance;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityInfDragonE
extends EntityPInfected
implements EntityCutomAttack,
EntityBodyParts {
    protected static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityInfDragonE.class, (DataSerializer)DataSerializers.BOOLEAN);
    private int flying;
    private float aaa;
    private float sss;
    private final BossInfoServer bossInfo = (BossInfoServer)new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS).setDarkenSky(false);
    private EntityBody leftTendril;
    private EntityBody rightTendril;
    private EntityBody head;
    private float leftTendrilHealth;
    private float rightTendrilHealth;
    private float headlHealth;
    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityInfDragonE.class, (DataSerializer)DataSerializers.BYTE);
    private static final DataParameter<Boolean> ATTACKING = EntityDataManager.createKey(EntityInfDragonE.class, (DataSerializer)DataSerializers.BOOLEAN);
    private int limit;
    private boolean skillFlame;
    private double tttX;
    private double tttY;
    private double tttZ;
    private double tttH;
    private double tttHH;

    public EntityInfDragonE(World worldIn) {
        super(worldIn);
        this.setSize(1.9f, 3.8f);
        this.stepHeight = 1.0f;
        this.canModRender = 0;
        this.type = (byte)14;
        this.killcount = -10.0;
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.flying = 0;
        this.skillFlame = false;
        this.leftTendril = new EntityBody(this, 2.6f, 2.5f, 1.0f, 3.1f, 2.8f, 1, 1, true);
        this.rightTendril = new EntityBody(this, 2.6f, 2.5f, 1.0f, 3.1f, 2.8f, -1, 2, true);
        this.head = new EntityBody(this, 2.2f, 2.2f, 1.0f, 4.0f, 2.0f, -1, 3, false, 0.2f);
        this.leftTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.rightTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.headlHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.ignoreFrustumCheck = true;
        this.moveHelper = new AIMoveControl(this);
    }

    @Override
    public int getParasiteIDRegister() {
        return 64;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infdragoneCanSpawnAssimilatedNat;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimmingDiving((EntityLiving)this, 0.08));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatusAOE(this, 1.9, false, 8.0, 6.0));
        this.tasks.addTask(6, (EntityAIBase)new AIMoveRandom());
        this.tasks.addTask(6, (EntityAIBase)new AIFireballAttack(this));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightAttack(this, 64.0));
        this.tasks.addTask(2, (EntityAIBase)new EntityAISkill(this, 60, 32, 7, true, 1));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.INFDRAGONE_HEALTH);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.INFDRAGONE_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.27);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.INFDRAGONE_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.INFDRAGONE_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(64.0);
    }

    @Override
    public void onLivingUpdate() {
        if (this.isAIDisabled()) {
            return;
        }
        super.onLivingUpdate();
        this.killcount = -10.0;
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
            if (this.rand.nextInt(9) == 0 && !this.getFlyingState()) {
                this.changeStateTo(true);
                return;
            }
        }
        if (this.flying >= 1) {
            ++this.flying;
        }
        if (this.getFlyingState()) {
            this.aaa += 0.08f;
            this.sss += 0.782f;
            if (this.sss >= 24.0f) {
                this.playSound(SoundEvents.ENTITY_ENDERDRAGON_FLAP, 5.0f, 0.8f + this.rand.nextFloat() * 0.3f);
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
            this.setNoGravity(true);
        } else {
            this.setNoGravity(false);
        }
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

    public float getaaa() {
        return this.aaa;
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        boolean flag = super.attackEntityFrom(source, amount);
        if (flag) {
            if (source.getEntity() != null && source.getEntity() instanceof EntityPlayerMP) {
                this.bossInfo.addPlayer((EntityPlayerMP)source.getEntity());
            }
            if (this.rand.nextInt(12) == 0 && !this.getFlyingState()) {
                this.changeStateTo(true);
            }
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
                tendril.setSkin(7);
                tendril.copyLocationAndAnglesFrom(this.leftTendril);
                this.worldObj.spawnEntityInWorld((Entity)tendril);
                this.worldObj.removePlayerEntityDangerously((Entity)this.leftTendril);
                this.worldObj.setEntityState((Entity)this, (byte)11);
                SRPMain.network.sendToAll((IMessage)new SRPPacketEntityBodyDead(this.getEntityId(), id));
                this.changeStateTo(false);
            }
        } else if (this.rightTendril.getId() == id) {
            this.rightTendrilHealth -= amount;
            if (this.rightTendrilHealth <= 0.0f) {
                EntityTendril tendril = new EntityTendril(this.worldObj);
                tendril.setSkin(8);
                tendril.copyLocationAndAnglesFrom(this.rightTendril);
                this.worldObj.spawnEntityInWorld((Entity)tendril);
                this.worldObj.removePlayerEntityDangerously((Entity)this.rightTendril);
                this.worldObj.setEntityState((Entity)this, (byte)22);
                SRPMain.network.sendToAll((IMessage)new SRPPacketEntityBodyDead(this.getEntityId(), id));
                this.changeStateTo(false);
            }
        } else if (this.head.getId() == id) {
            this.headlHealth -= amount;
            if (this.headlHealth <= 0.0f) {
                EntityInfDragonEHead tendril = new EntityInfDragonEHead(this.worldObj);
                tendril.setSkin(5);
                tendril.copyLocationAndAnglesFrom(this.head);
                this.worldObj.spawnEntityInWorld((Entity)tendril);
                this.worldObj.removePlayerEntityDangerously((Entity)this.head);
                this.worldObj.setEntityState((Entity)this, (byte)33);
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

    public void fall(float distance, float damageMultiplier) {
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
        return SRPSounds.MOBSILENCE;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_ENDERDRAGON_HURT;
    }

    protected float getSoundVolume() {
        return 5.0f;
    }

    protected String getDeathSound() {
        return SRPSounds.MOBSILENCE;
    }

    protected String getStepSound() {
        return SoundEvents.ENTITY_SPIDER_STEP;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
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
        } else {
            super.handleHealthUpdate(id);
        }
    }

    public void moveEntity(MoverType type, double x, double y, double z) {
        super.moveEntity(type, x, y, z);
        this.doBlockCollisions();
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
        EntityProjectileDragonE entitylargefireball = new EntityProjectileDragonE(this.worldObj, (EntityLivingBase)this, d2, d3, d4);
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
        private final EntityInfDragonE parentEntity;
        public int attackTimer;

        public AIFireballAttack(EntityInfDragonE ghast) {
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
                    double d1 = 4.0;
                    Vec3d vec3d = this.parentEntity.getLook(1.0f);
                    double d2 = entitylivingbase.posX - (this.parentEntity.posX + vec3d.xCoord * 4.0);
                    double d3 = Boxes.entityBox(entitylivingbase).minY + (double)(entitylivingbase.height / 2.0f) - (0.5 + this.parentEntity.posY + (double)(this.parentEntity.height / 2.0f));
                    double d4 = entitylivingbase.posZ - (this.parentEntity.posZ + vec3d.zCoord * 4.0);
                    world.playEvent((EntityPlayer)null, 1016, new BlockPos((Entity)this.parentEntity), 0);
                    EntityProjectileDragonE entitylargefireball = new EntityProjectileDragonE(world, (EntityLivingBase)this.parentEntity, d2, d3, d4);
                    entitylargefireball.posX = this.parentEntity.posX + vec3d.xCoord * 4.0;
                    entitylargefireball.posY = this.parentEntity.posY + (double)(this.parentEntity.height / 2.0f) + 0.5;
                    entitylargefireball.posZ = this.parentEntity.posZ + vec3d.zCoord * 4.0;
                    world.spawnEntityInWorld((Entity)entitylargefireball);
                    this.parentEntity.entityAge = 0;
                    this.attackTimer = -5;
                    for (int i = 0; i <= 2; ++i) {
                        this.parentEntity.worldObj.spawnParticle(EnumParticleTypes.FLAME, this.parentEntity.posX + vec3d.xCoord * 4.0, this.parentEntity.posY + (double)(this.parentEntity.height / 2.0f) + 0.5, this.parentEntity.posZ + vec3d.zCoord * 4.0, 0.0, -1.0, 0.0, new int[0]);
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
            return EntityInfDragonE.this.rand.nextInt(7) == 0 && EntityInfDragonE.this.getFlyingState();
        }

        public boolean continueExecuting() {
            return false;
        }

        public void updateTask() {
            BlockPos blockpos = new BlockPos((Entity)EntityInfDragonE.this);
            int flag = 1;
            double speed = 0.5;
            if (EntityInfDragonE.this.getAttackTarget() != null) {
                if (EntityInfDragonE.this.getDistanceSqToEntity((Entity)EntityInfDragonE.this.getAttackTarget()) > 100.0) {
                    blockpos = new BlockPos((Entity)EntityInfDragonE.this.getAttackTarget());
                    flag = 2;
                } else if (EntityInfDragonE.this.getDistanceSqToEntity((Entity)EntityInfDragonE.this.getAttackTarget()) < 36.0) {
                    blockpos = new BlockPos((Entity)EntityInfDragonE.this.getAttackTarget());
                    flag = 3;
                    speed += 0.25;
                }
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.add(EntityInfDragonE.this.rand.nextInt(15) - 7, EntityInfDragonE.this.rand.nextInt(11) - 5, EntityInfDragonE.this.rand.nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.add(EntityInfDragonE.this.rand.nextInt(6) - 2, EntityInfDragonE.this.rand.nextInt(7) - 2, EntityInfDragonE.this.rand.nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.add(EntityInfDragonE.this.rand.nextInt(4) + 3, EntityInfDragonE.this.rand.nextInt(5) + 4, EntityInfDragonE.this.rand.nextInt(4) + 3);
                }
                if (!WorldAccess.isAirBlock(EntityInfDragonE.this.worldObj, blockpos1)) continue;
                EntityInfDragonE.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityInfDragonE.this.getAttackTarget() != null) break;
                EntityInfDragonE.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends EntityMoveHelper {
        public AIMoveControl(EntityInfDragonE vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == EntityMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityInfDragonE.this.posX;
                double d1 = this.posY - EntityInfDragonE.this.posY;
                double d2 = this.posZ - EntityInfDragonE.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityInfDragonE.this).getAverageEdgeLength()) {
                    this.action = EntityMoveHelper.Action.WAIT;
                    EntityInfDragonE.this.motionX *= 0.5;
                    EntityInfDragonE.this.motionY *= 0.5;
                    EntityInfDragonE.this.motionZ *= 0.5;
                } else {
                    EntityInfDragonE.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityInfDragonE.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityInfDragonE.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityInfDragonE.this.getAttackTarget() == null) {
                        EntityInfDragonE.this.renderYawOffset = EntityInfDragonE.this.rotationYaw = -((float)MathHelper.atan2((double)EntityInfDragonE.this.motionX, (double)EntityInfDragonE.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityInfDragonE.this.getAttackTarget().posX - EntityInfDragonE.this.posX;
                        double d5 = EntityInfDragonE.this.getAttackTarget().posZ - EntityInfDragonE.this.posZ;
                        EntityInfDragonE.this.renderYawOffset = EntityInfDragonE.this.rotationYaw = -((float)MathHelper.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}

