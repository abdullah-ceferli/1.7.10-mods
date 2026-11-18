/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.MobEffects
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanPullMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCrude;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooS;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectilePullball;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;

import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.world.World;

public class EntityLeer
extends EntityParasiteBase
implements EntityCanPullMobs {
    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityLeer.class, (DataSerializer)DataSerializers.BYTE);
    private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey(EntityLeer.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_ENTITY1 = EntityDataManager.createKey(EntityLeer.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_ENTITY2 = EntityDataManager.createKey(EntityLeer.class, (DataSerializer)DataSerializers.VARINT);
    private EntityLivingBase targetedEntity;
    private ArrayList<DataParameter<Integer>> tracking = new ArrayList();
    private int pulling;
    private EntityBody head;
    private int border;
    private boolean skillpulling;

    public EntityLeer(World worldIn) {
        super(worldIn);
        this.moveHelper = new AIMoveControl(this);
        this.setSize(2.1f, 7.1f);
        this.setNoGravity(true);
        this.tracking.add(TARGET_ENTITY);
        this.tracking.add(TARGET_ENTITY1);
        this.tracking.add(TARGET_ENTITY2);
        if (SRPConfigMobs.ombooMaxY != 256) {
            this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightLimits(this, SRPConfigMobs.ombooMaxY, true));
        }
        this.head = new EntityBody(this, 2.6f, 2.5f, 1.0f, 0.0f, 7.5f, -1, 1, false, 0.2f);
        this.ignoreFrustumCheck = true;
    }

    @Override
    public int getParasiteIDRegister() {
        return 328;
    }

    protected void initEntityAI() {
        super.initEntityAI();
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIFlightAttack(this, SRPConfig.pureFollow));
        this.tasks.addTask(2, (EntityAIBase)new EntityAISkill(this, 40, 300, 7, true, 1, true));
        this.tasks.addTask(6, (EntityAIBase)new AIMoveRandom());
        this.tasks.addTask(4, (EntityAIBase)new EntityAIFlightLimits(this, 5, false));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.LEER_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.LEER_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.LEER_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.LEER_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.preeminentFollow);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.head.onUpdate();
        if (!this.worldObj.isRemote) {
            if (this.onGround) {
                this.moveHelper.setMoveTo(this.posX, this.posY + 5.0, this.posZ, 0.5);
            }
            if (this.srpTicks == 10 && (BlockStates.get(this.worldObj, EntityQueries.position(this).down(1)).getBlock() != Blocks.air || BlockStates.get(this.worldObj, EntityQueries.position(this).down(2)).getBlock() != Blocks.air) && this.getAttackTarget() != null) {
                this.motionY = 0.5;
            }
            if (this.hasTargetedEntity()) {
                this.motionY = -0.01;
                this.entityAge = 0;
                for (EntityLivingBase entitylivingbase : this.getTargetedEntityVictims()) {
                    double dis = this.getDistanceSqToEntity((Entity)entitylivingbase);
                    if (this.canEntityBeSeen((Entity)entitylivingbase) && dis > 0.0) {
                        entitylivingbase.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.moveSlowdown, 20, 3, false, false));
                        entitylivingbase.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.digSlowdown, 20, 3, false, false));
                        this.setParasiteStatus(3);
                        ++this.pulling;
                        if (this.pulling > 600) {
                            this.setParasiteStatus(2);
                            this.setTargetedEntity(0, entitylivingbase);
                        }
                        if (!(dis < 4.0)) continue;
                        this.attackEntityAsMob((Entity)entitylivingbase);
                        continue;
                    }
                    this.setParasiteStatus(2);
                    this.setTargetedEntity(0, entitylivingbase);
                }
            } else {
                this.setParasiteStatus(0);
            }
        } else if (this.rand.nextInt(30) == 0) {
            for (int i = 0; i <= 20; ++i) {
                if (i % 5 != 0) continue;
                this.spawnParticlesGoreMouth(SRPEnumParticle.GSPLASH, 0, -1, -1, 0.2, 0.0);
            }
        }
        for (EntityLivingBase entitylivingbase : this.getTargetedEntityVictims()) {
            if (!(this.getDistanceSqToEntity((Entity)entitylivingbase) > 0.0)) continue;
            entitylivingbase.dismountRidingEntity();
            double str = 0.4;
            double deltaX = this.posX - entitylivingbase.posX;
            double deltaY = this.posY - entitylivingbase.posY;
            double deltaZ = this.posZ - entitylivingbase.posZ;
            str = 0.1;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            if (distance == 0.0) {
                return;
            }
            entitylivingbase.motionX += (deltaX /= distance) * str;
            entitylivingbase.motionY += (deltaY /= distance) * str;
            entitylivingbase.motionZ += (deltaZ /= distance) * str;
        }
    }

    public void onUpdate() {
        super.onUpdate();
        this.setNoGravity(true);
    }

    @Override
    public void setTargetedEntity(int entityId) {
        this.pulling = 0;
        for (DataParameter<Integer> mob : this.tracking) {
            if ((Integer)EntityDataManager.of(this).get(mob) != entityId) continue;
            return;
        }
        for (DataParameter<Integer> mob : this.tracking) {
            if ((Integer)EntityDataManager.of(this).get(mob) != 0) continue;
            EntityDataManager.of(this).set(mob, (Object)entityId);
            return;
        }
    }

    public void setTargetedEntity(int entityId, EntityLivingBase sameEntity) {
        this.pulling = 0;
        for (DataParameter<Integer> mob : this.tracking) {
            if (((Integer)EntityDataManager.of(this).get(mob)).intValue() != sameEntity.getEntityId()) continue;
            EntityDataManager.of(this).set(mob, (Object)entityId);
        }
    }

    public void updateTargets() {
        for (DataParameter<Integer> mob : this.tracking) {
            if ((Integer)EntityDataManager.of(this).get(mob) == 0) continue;
            Entity entity = this.worldObj.getEntityByID(((Integer)EntityDataManager.of(this).get(mob)).intValue());
            if (entity == null) {
                EntityDataManager.of(this).set(mob, (Object)0);
                continue;
            }
            if (((EntityLivingBase)entity).isEntityAlive()) continue;
            EntityDataManager.of(this).set(mob, (Object)0);
        }
    }

    @Override
    public boolean hasTargetedEntity() {
        this.updateTargets();
        for (DataParameter<Integer> mob : this.tracking) {
            if ((Integer)EntityDataManager.of(this).get(mob) == 0) continue;
            return true;
        }
        return false;
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingIn) {
        if (this.worldObj.isRemote) {
            return;
        }
        if (this.killcount >= 0.0) {
            if (this.getSkin() != 120) {
                this.killcount += 1.0;
            } else if (this.rand.nextInt(3) == 0) {
                this.killcount += 1.0;
            }
        }
        if (SRPConfigSystems.useEvolution) {
            if (this.isPotionActive(SRPPotions.PIVOT_E)) {
                int amp = this.getActivePotionEffect(SRPPotions.PIVOT_E).getAmplifier();
                SRPSaveData.get(this.worldObj, 35).setTotalKills(this.worldObj.provider.dimensionId, SRPConfigSystems.valueKill * (SRPConfigSystems.pivotPointMultiplier * (amp + 1)), true, this.worldObj, true, 38);
            } else {
                SRPSaveData.get(this.worldObj, 34).setTotalKills(this.worldObj.provider.dimensionId, SRPConfigSystems.valueKill, true, this.worldObj, true, 39);
            }
        }
        if (SRPConfigWorld.originActivated) {
            ParasiteEventWorld.setOriginInHealth(this.worldObj, EntityQueries.position(this), (int)((double)entityLivingIn.getMaxHealth() * SRPConfigWorld.originKillMultiplier), true);
            if (SRPConfigWorld.originTriggerKill > this.worldObj.rand.nextDouble()) {
                ParasiteEventWorld.placeOriginInWorld(this.worldObj, new BlockPos(this.posX, this.posY, this.posZ), SRPConfigWorld.originHealth, SRPConfigWorld.originRadius);
            }
        }
        if (this.isPotionActive(SRPPotions.PARATE_E)) {
            int bonuss = this.getActivePotionEffect(SRPPotions.PARATE_E).getAmplifier() + 1;
            if (entityLivingIn.getEntityAttribute(SharedMonsterAttributes.maxHealth) != null) {
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() + entityLivingIn.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() * (SRPConfigSystems.parateMuch * (double)bonuss));
            }
            if (entityLivingIn.getEntityAttribute(SharedMonsterAttributes.ARMOR) != null) {
                this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(this.getEntityAttribute(SharedMonsterAttributes.ARMOR).getBaseValue() + entityLivingIn.getEntityAttribute(SharedMonsterAttributes.ARMOR).getBaseValue() * (SRPConfigSystems.parateMuch * (double)bonuss));
            }
            if (entityLivingIn.getEntityAttribute(SharedMonsterAttributes.attackDamage) != null) {
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue() + entityLivingIn.getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue() * (SRPConfigSystems.parateMuch * (double)bonuss));
            }
        }
        this.heal(entityLivingIn.getHealth() * this.geneMobHealing);
        this.setWait(10);
        List serverList = EntityQueries.loaded(this.worldObj);
        int parasiteCount = 0;
        for (Entity value : serverList) {
            if (!(value instanceof EntityParasiteBase)) continue;
            ++parasiteCount;
        }
        if (parasiteCount >= SRPConfig.worldMobCap) {
            return;
        }
        entityLivingIn.setDead();
        EntityPCrude mob = new EntityInhooM(this.worldObj);
        if (this.rand.nextBoolean()) {
            mob = new EntityInhooS(this.worldObj);
        }
        mob.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        mob.onInitialSpawn(DifficultyAccess.at(this.worldObj, EntityQueries.position(mob)), null);
        mob.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.field_76444_x, 60, 5));
        this.worldObj.spawnEntityInWorld((Entity)mob);
        mob.particleStatus((byte)7);
    }

    public ArrayList<EntityLivingBase> getTargetedEntityVictims() {
        if (!this.hasTargetedEntity()) {
            return new ArrayList<EntityLivingBase>();
        }
        ArrayList<EntityLivingBase> mobs = new ArrayList<EntityLivingBase>();
        for (DataParameter<Integer> mob : this.tracking) {
            Entity entity;
            if ((Integer)EntityDataManager.of(this).get(mob) == 0 || (entity = this.worldObj.getEntityByID(((Integer)EntityDataManager.of(this).get(mob)).intValue())) == null) continue;
            mobs.add((EntityLivingBase)entity);
        }
        return mobs;
    }

    @Override
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

    @Override
    public void setPStatus(int in) {
        this.setParasiteStatus(in);
    }

    @Override
    public void setPullingMobEffects(EntityLivingBase mob) {
        mob.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(net.minecraft.potion.Potion.weakness, 60, 3, false, false));
    }

    @Override
    public int getAcceleration() {
        return 2;
    }

    @Override
    public boolean checkAttackTarget(EntityLivingBase check) {
        if (check instanceof EntityParasiteBase) {
            return false;
        }
        return !(check instanceof EntityPlayer) || !((EntityPlayer)check).isCreative();
    }

    public void applyEntityCollision(Entity entityIn) {
        for (EntityLivingBase entitylivingbase : this.getTargetedEntityVictims()) {
            if (entitylivingbase != entityIn) continue;
            return;
        }
        super.applyEntityCollision(entityIn);
    }

    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (TARGET_ENTITY.equals(key)) {
            this.targetedEntity = null;
        }
    }

    @Override
    public void setDead() {
        if (this.head != null) {
            this.worldObj.removePlayerEntityDangerously((Entity)this.head);
        }
        super.setDead();
    }

    public void fall(float distance, float damageMultiplier) {
    }

    public float getEyeHeight() {
        return 0.5f;
    }

    public void moveEntity(MoverType type, double x, double y, double z) {
        super.moveEntity(type, x, y, z);
        this.doBlockCollisions();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(VEX_FLAGS, (Object)((byte)0));
        EntityDataManager.of(this).register(TARGET_ENTITY, (Object)0);
        EntityDataManager.of(this).register(TARGET_ENTITY1, (Object)0);
        EntityDataManager.of(this).register(TARGET_ENTITY2, (Object)0);
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

    public boolean isCharging() {
        return this.getVexFlag(1);
    }

    public void setCharging(boolean charging) {
        this.setVexFlag(1, charging);
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 1: {
                return this.skillpulling;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 1: {
                this.skillpulling = in;
                return;
            }
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 1: {
                this.pullingE();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void pullingE() {
        if (this.hasTargetedEntity()) {
            this.skillpulling = true;
            this.border = 0;
            return;
        }
        this.setParasiteStatus(11);
        this.getNavigator().clearPathEntity();
        if (this.border == 0) {
            // empty if block
        }
        if (this.border <= 2) {
            // empty if block
        }
        if (this.ticksExisted % 20 != 0) {
            return;
        }
        ++this.border;
        if (this.getAttackTarget() == null) {
            this.skillpulling = true;
            this.setParasiteStatus(0);
            this.border = 0;
            return;
        }
        if (!this.canEntityBeSeen((Entity)this.getAttackTarget())) {
            this.skillpulling = true;
            this.setParasiteStatus(0);
            this.border = 0;
            return;
        }
        this.shootEntityRed(this.getAttackTarget());
        int count = 2;
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(EntityQueries.position(this.getAttackTarget()).down(2)), 18.0, 8.0, 18.0);
        List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
        for (EntityLivingBase mob : moblist) {
            if (count < 0 || mob == this || !mob.isEntityAlive() || mob instanceof EntityParasiteBase) continue;
            this.shootEntityRed(mob);
            --count;
        }
        if (this.border > 5) {
            this.skillpulling = true;
            this.setParasiteStatus(0);
            this.border = 0;
        }
    }

    public void shootEntityRed(EntityLivingBase entitylivingbase) {
        Vec3d vec3d = this.getLook(1.0f);
        double d2 = entitylivingbase.posX - (this.posX + vec3d.xCoord);
        double d3 = Boxes.entityBox(entitylivingbase).maxY + (double)(entitylivingbase.height + 0.8f) - (0.5 + this.posY + (double)(this.height / 1.0f));
        double d4 = entitylivingbase.posZ - (this.posZ + vec3d.zCoord);
        EntityProjectilePullball entitylargefireball = new EntityProjectilePullball(this.worldObj, this, d2, d3, d4);
        entitylargefireball.posX = this.posX + vec3d.xCoord;
        entitylargefireball.posY = this.posY + (double)this.getEyeHeight() - 0.2;
        entitylargefireball.posZ = this.posZ + vec3d.zCoord;
        this.worldObj.spawnEntityInWorld((Entity)entitylargefireball);
    }

    @Override
    public void resetPullSkill() {
        this.skillpulling = true;
        this.border = 0;
    }

    class AIChargeAttack
    extends EntityAIBase {
        public AIChargeAttack() {
            this.setMutexBits(1);
        }

        public boolean shouldExecute() {
            if (EntityLeer.this.getAttackTarget() != null && EntityLeer.this.rand.nextInt(5) == 0) {
                return EntityLeer.this.getDistanceSqToEntity((Entity)EntityLeer.this.getAttackTarget()) > 4.0;
            }
            return false;
        }

        public boolean continueExecuting() {
            return EntityLeer.this.getMoveHelper().isUpdating() && EntityLeer.this.isCharging() && EntityLeer.this.getAttackTarget() != null && EntityLeer.this.getAttackTarget().isEntityAlive();
        }

        public void startExecuting() {
            EntityLivingBase entitylivingbase = EntityLeer.this.getAttackTarget();
            Vec3d vec3d = entitylivingbase.getPositionEyes(1.0f);
            EntityLeer.this.moveHelper.setMoveTo(vec3d.xCoord, entitylivingbase.posY + 20.0, vec3d.zCoord, 0.2);
            EntityLeer.this.setCharging(true);
        }

        public void resetTask() {
            EntityLeer.this.setCharging(false);
        }

        public void updateTask() {
            EntityLivingBase entitylivingbase = EntityLeer.this.getAttackTarget();
            if (entitylivingbase != null && entitylivingbase.isEntityAlive()) {
                if (Boxes.intersects(Boxes.entityBox(EntityLeer.this), Boxes.entityBox(entitylivingbase))) {
                    EntityLeer.this.attackEntityAsMob((Entity)entitylivingbase);
                    EntityLeer.this.setCharging(false);
                } else {
                    double d0 = EntityLeer.this.getDistanceSqToEntity((Entity)entitylivingbase);
                    if (d0 < 9.0) {
                        Vec3d vec3d = entitylivingbase.getPositionEyes(1.0f);
                        EntityLeer.this.moveHelper.setMoveTo(vec3d.xCoord, entitylivingbase.posY + 20.0, vec3d.zCoord, 1.0);
                    } else {
                        Vec3d vec3d = entitylivingbase.getPositionEyes(1.0f);
                        EntityLeer.this.moveHelper.setMoveTo(vec3d.xCoord, entitylivingbase.posY + 20.0, vec3d.zCoord, 1.1);
                    }
                }
            }
        }
    }

    class AIMoveRandom
    extends EntityAIBase {
        public AIMoveRandom() {
            this.setMutexBits(1);
        }

        public boolean shouldExecute() {
            return !EntityLeer.this.getMoveHelper().isUpdating() && EntityLeer.this.rand.nextInt(7) == 0;
        }

        public boolean continueExecuting() {
            return false;
        }

        public void updateTask() {
            BlockPos blockpos = new BlockPos((Entity)EntityLeer.this);
            int flag = 1;
            double speed = 0.1;
            if (EntityLeer.this.getAttackTarget() != null) {
                if (EntityLeer.this.getDistanceSqToEntity((Entity)EntityLeer.this.getAttackTarget()) > 100.0) {
                    blockpos = new BlockPos((Entity)EntityLeer.this.getAttackTarget());
                    flag = 2;
                    speed += 0.05;
                } else if (EntityLeer.this.getDistanceSqToEntity((Entity)EntityLeer.this.getAttackTarget()) < 36.0) {
                    blockpos = new BlockPos((Entity)EntityLeer.this.getAttackTarget());
                    flag = 3;
                    speed += 0.05;
                }
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.add(EntityLeer.this.rand.nextInt(15) - 7, EntityLeer.this.rand.nextInt(11) - 5, EntityLeer.this.rand.nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.add(EntityLeer.this.rand.nextInt(6) - 2, EntityLeer.this.rand.nextInt(7) - 2, EntityLeer.this.rand.nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.add(EntityLeer.this.rand.nextInt(4) + 3, EntityLeer.this.rand.nextInt(5) + 4, EntityLeer.this.rand.nextInt(4) + 3);
                }
                if (!WorldAccess.isAirBlock(EntityLeer.this.worldObj, blockpos1)) continue;
                EntityLeer.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 1.0, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityLeer.this.getAttackTarget() != null) break;
                EntityLeer.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 1.0, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends EntityMoveHelper {
        public AIMoveControl(EntityLeer vex) {
            super((EntityLiving)vex);
        }

        public void onUpdateMoveHelper() {
            if (this.action == EntityMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - EntityLeer.this.posX;
                double d1 = this.posY - EntityLeer.this.posY;
                double d2 = this.posZ - EntityLeer.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)MathHelper.sqrt_double((double)d3)) < Boxes.entityBox(EntityLeer.this).getAverageEdgeLength()) {
                    this.action = EntityMoveHelper.Action.WAIT;
                    EntityLeer.this.motionX *= 0.5;
                    EntityLeer.this.motionY *= 0.5;
                    EntityLeer.this.motionZ *= 0.5;
                } else {
                    EntityLeer.this.motionX += d0 / d3 * 0.05 * this.speed;
                    EntityLeer.this.motionY += d1 / d3 * 0.05 * this.speed;
                    EntityLeer.this.motionZ += d2 / d3 * 0.05 * this.speed;
                    if (EntityLeer.this.getAttackTarget() == null) {
                        EntityLeer.this.renderYawOffset = EntityLeer.this.rotationYaw = -((float)MathHelper.atan2((double)EntityLeer.this.motionX, (double)EntityLeer.this.motionZ)) * 57.295776f;
                    } else {
                        double d4 = EntityLeer.this.getAttackTarget().posX - EntityLeer.this.posX;
                        double d5 = EntityLeer.this.getAttackTarget().posZ - EntityLeer.this.posZ;
                        EntityLeer.this.renderYawOffset = EntityLeer.this.rotationYaw = -((float)MathHelper.atan2((double)d4, (double)d5)) * 57.295776f;
                    }
                }
            }
        }
    }
}

