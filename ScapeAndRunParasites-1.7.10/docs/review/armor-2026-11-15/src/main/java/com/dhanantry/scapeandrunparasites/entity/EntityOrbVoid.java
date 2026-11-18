/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityOrbVoid
extends Entity {
    public float prevRenderYawOffset;
    public float renderYawOffset;
    public float prevRotationYawHead;
    public float rotationYawHead;
    public float prevLimbSwingAmount;
    public float limbSwingAmount;
    public float limbSwing;
    public int hurtTime;
    public int deathTime;
    protected int lastActiveTime;
    protected int timeSinceIgnited;
    protected int timerDDD;
    EntityPMalleable father;
    private boolean followF;
    private int rad;
    private double str;
    public double offsetOrb;
    private double poosX;
    private double poosY;
    private double poosZ;
    private static final DataParameter<Integer> SELFE = EntityDataManager.createKey(EntityOrbVoid.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> FUSE = EntityDataManager.createKey(EntityOrbVoid.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> WAITSTART = EntityDataManager.createKey(EntityOrbVoid.class, (DataSerializer)DataSerializers.VARINT);
    private ArrayList<DataParameter<Integer>> tracking = new ArrayList();
    private static final DataParameter<Integer> TARGET_ENTITY1 = EntityDataManager.createKey(EntityOrbVoid.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_ENTITY2 = EntityDataManager.createKey(EntityOrbVoid.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_ENTITY3 = EntityDataManager.createKey(EntityOrbVoid.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_ENTITY4 = EntityDataManager.createKey(EntityOrbVoid.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_ENTITY5 = EntityDataManager.createKey(EntityOrbVoid.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_ENTITY6 = EntityDataManager.createKey(EntityOrbVoid.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_ENTITY7 = EntityDataManager.createKey(EntityOrbVoid.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_ENTITY8 = EntityDataManager.createKey(EntityOrbVoid.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_ENTITY9 = EntityDataManager.createKey(EntityOrbVoid.class, (DataSerializer)DataSerializers.VARINT);

    public EntityOrbVoid(World worldIn) {
        super(worldIn);
        this.setSize(0.5f, 0.5f);
        this.ignoreFrustumCheck = true;
        this.isImmuneToFire = true;
        this.lastActiveTime = 0;
        this.timeSinceIgnited = 0;
        this.followF = false;
        this.setFuseState(7);
        this.setStartState(40);
        this.tracking.add(TARGET_ENTITY1);
        this.tracking.add(TARGET_ENTITY2);
        this.tracking.add(TARGET_ENTITY3);
        this.tracking.add(TARGET_ENTITY4);
        this.tracking.add(TARGET_ENTITY5);
        this.tracking.add(TARGET_ENTITY6);
        this.tracking.add(TARGET_ENTITY7);
        this.tracking.add(TARGET_ENTITY8);
        this.tracking.add(TARGET_ENTITY9);
        this.setNoGravity(false);
        this.rad = 40;
        this.str = 0.2;
    }

    public EntityOrbVoid(World worldIn, EntityPMalleable in, int fuse, int waitStart) {
        this(worldIn);
        this.father = in;
        this.prevRenderYawOffset = in.prevRenderYawOffset;
        this.renderYawOffset = in.renderYawOffset;
        this.prevRotationYawHead = in.prevRotationYawHead;
        this.rotationYawHead = in.rotationYawHead;
        this.prevLimbSwingAmount = in.prevLimbSwingAmount;
        this.limbSwingAmount = in.limbSwingAmount;
        this.limbSwing = in.limbSwing;
        this.setFuseState(fuse);
        this.setStartState(waitStart);
    }

    public EntityOrbVoid(World worldIn, EntityPMalleable in, int fuse, int waitStart, boolean stayPY) {
        this(worldIn, in, fuse, waitStart);
        this.followF = stayPY;
    }

    protected void entityInit() {
        EntityDataManager.of(this).register(SELFE, (Object)(-1));
        EntityDataManager.of(this).register(FUSE, (Object)(-1));
        EntityDataManager.of(this).register(WAITSTART, (Object)(-1));
        EntityDataManager.of(this).register(TARGET_ENTITY1, (Object)0);
        EntityDataManager.of(this).register(TARGET_ENTITY2, (Object)0);
        EntityDataManager.of(this).register(TARGET_ENTITY3, (Object)0);
        EntityDataManager.of(this).register(TARGET_ENTITY4, (Object)0);
        EntityDataManager.of(this).register(TARGET_ENTITY5, (Object)0);
        EntityDataManager.of(this).register(TARGET_ENTITY6, (Object)0);
        EntityDataManager.of(this).register(TARGET_ENTITY7, (Object)0);
        EntityDataManager.of(this).register(TARGET_ENTITY8, (Object)0);
        EntityDataManager.of(this).register(TARGET_ENTITY9, (Object)0);
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted > this.getStartState()) {
            this.orbDoing();
            this.setSelfeState(1);
            this.dyingBurst(true, 1);
            if (this.worldObj.isRemote) {
                for (int i = 0; i < 4; ++i) {
                    this.worldObj.spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5) * ((double)this.width * 3.0), this.posY + this.rand.nextDouble() * (double)this.height - 0.25, this.posZ + (this.rand.nextDouble() - 0.5) * ((double)this.width * 3.0), (this.rand.nextDouble() - 0.5) * 2.0, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5) * 2.0, new int[0]);
                }
                if (this.father != null) {
                    this.prevRenderYawOffset = this.father.prevRenderYawOffset;
                    this.renderYawOffset = this.father.renderYawOffset;
                    this.prevRotationYawHead = this.father.prevRotationYawHead;
                    this.rotationYawHead = this.father.rotationYawHead;
                }
                this.spawnOrbEffects(4);
                return;
            }
            if (this.father != null) {
                if (this.father.isEntityAlive() && this.followF) {
                    this.posX = this.father.posX;
                    this.posY = this.father.posY - this.rand.nextDouble() * 0.1 + (double)this.father.height + this.offsetOrb;
                    this.posZ = this.father.posZ;
                } else {
                    this.posX = this.poosX;
                    this.posY = this.poosY - this.rand.nextDouble() * 0.1;
                    this.posZ = this.poosZ;
                    if (this.followF) {
                        this.setDead();
                    }
                }
            } else {
                this.posX = this.poosX;
                this.posY = this.poosY - this.rand.nextDouble() * 0.1;
                this.posZ = this.poosZ;
            }
        } else {
            if (this.worldObj.isRemote) {
                this.spawnOrbEffects(4);
                return;
            }
            if (this.father != null) {
                if (this.father.isEntityAlive() && this.followF) {
                    this.poosX = this.father.posX;
                    this.poosY = this.father.posY + (double)this.father.height + this.offsetOrb;
                    this.poosZ = this.father.posZ;
                    this.posX = this.father.posX;
                    this.posY = this.father.posY - this.rand.nextDouble() * 0.1 + (double)this.father.height + this.offsetOrb;
                    this.posZ = this.father.posZ;
                } else {
                    if (this.followF) {
                        this.setDead();
                    }
                    this.poosX = this.posX;
                    this.poosY = this.posY;
                    this.poosZ = this.posZ;
                }
            } else {
                this.poosX = this.posX;
                this.poosY = this.posY;
                this.poosZ = this.posZ;
            }
        }
    }

    public int getStartState() {
        return (Integer)EntityDataManager.of(this).get(WAITSTART);
    }

    public void setStartState(int state) {
        EntityDataManager.of(this).set(WAITSTART, (Object)state);
    }

    public int getFuseState() {
        return (Integer)EntityDataManager.of(this).get(FUSE);
    }

    public void setFuseState(int state) {
        EntityDataManager.of(this).set(FUSE, (Object)state);
    }

    public int getSelfeState() {
        return (Integer)EntityDataManager.of(this).get(SELFE);
    }

    public void setSelfeState(int state) {
        EntityDataManager.of(this).set(SELFE, (Object)state);
    }

    protected void dyingBurst(boolean fromDeath, int value) {
        int i = this.getSelfeState();
        this.timeSinceIgnited += i * value;
        if (this.timeSinceIgnited < 0) {
            this.timeSinceIgnited = 0;
        }
        if (this.timeSinceIgnited >= this.getFuseState()) {
            this.timeSinceIgnited = this.getFuseState();
            this.selfExplode();
        } else {
            this.setSize(this.width + 0.8f, this.height + 0.32f);
        }
    }

    protected void selfExplode() {
        this.setSelfeState(2);
        if (this.getSelfeState() == 2) {
            ++this.timerDDD;
            if (this.timerDDD > 80) {
                this.setSize(Math.max(0.1f, this.width - 0.8f), Math.max(0.1f, this.height - 0.32f));
                if (!this.worldObj.isRemote) {
                    if (this.father != null) {
                        float f = this.width / 2.0f;
                        float f1 = this.height;
                        AxisAlignedBB axisalignedbb = Boxes.create(this.posX - (double)f, this.posY - (double)f1, this.posZ - (double)f, this.posX + (double)f, this.posY + (double)f1, this.posZ + (double)f);
                        List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
                        for (EntityLivingBase mob : moblist) {
                            if (mob instanceof EntityParasiteBase) continue;
                            this.father.attackEntityAsMobMinimum(mob, this.father.getMiniDamage() * 5.0f);
                        }
                    }
                } else {
                    int par = this.getFuseState();
                    par += par / 2;
                    for (int i = 0; i <= par; ++i) {
                        this.worldObj.spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5) * ((double)this.width * 2.0), this.posY + this.rand.nextDouble() * 2.0 * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5) * ((double)this.width * 2.0), this.rand.nextGaussian(), 0.0, this.rand.nextGaussian(), new int[]{0, 0, 0});
                    }
                }
                this.playSound(SRPSounds.ORB_E, 1.0f, 1.0f);
                if (this.timerDDD > 90) {
                    this.setDead();
                }
            }
        }
    }

    private void orbDoing() {
        for (EntityLivingBase target : this.getTargetedEntityVictims()) {
            this.pullEntity(target);
        }
        if (this.worldObj.isRemote) {
            return;
        }
        float f = this.width / 2.0f;
        float f1 = this.height;
        if (!this.worldObj.isRemote) {
            this.resetTargetedEntity();
            AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX - (double)f, this.posY - (double)f1, this.posZ - (double)f, this.posX + (double)f, this.posY + (double)f1, this.posZ + (double)f), (double)this.rad);
            List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
            for (EntityLivingBase target : moblist) {
                if (this.father != null && this.father == target || target instanceof EntityPCosmical || target instanceof EntityPStationary || target instanceof EntityPPreeminent) continue;
                if (target instanceof EntityPlayer) {
                    EntityPlayer pla = (EntityPlayer)target;
                    if (((EntityPlayer)target).capabilities.disableDamage) continue;
                    this.setTargetedEntity(target.getEntityId());
                    continue;
                }
                this.pullEntity(target);
            }
        }
    }

    public void pullEntity(EntityLivingBase target) {
        double ti = target.getDistanceSqToEntity((Entity)this);
        target.dismountRidingEntity();
        if (ti < 4.0) {
            target.posX = this.posX;
            target.posY = this.posY;
            target.posZ = this.posZ;
            target.motionX = 0.0;
            target.motionY = 0.0;
            target.motionZ = 0.0;
        } else {
            double deltaX = this.posX - target.posX;
            double deltaY = this.posY - target.posY;
            double deltaZ = this.posZ - target.posZ;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            if (distance == 0.0) {
                return;
            }
            target.motionX += (deltaX /= distance) * this.str;
            target.motionY += (deltaY /= distance) * this.str;
            target.motionZ += (deltaZ /= distance) * this.str;
        }
        if (ti < 25.0 && this.father != null) {
            this.father.attackEntityAsMobMinimum(target, this.father.getMiniDamage() / 10.0f);
            target.attackEntityFrom(DamageSource.magic, 10.0f);
        }
    }

    public void resetTargetedEntity() {
        for (DataParameter<Integer> mob : this.tracking) {
            EntityDataManager.of(this).set(mob, (Object)0);
        }
    }

    public void setTargetedEntity(int entityId) {
        for (DataParameter<Integer> mob : this.tracking) {
            if ((Integer)EntityDataManager.of(this).get(mob) != 0) continue;
            EntityDataManager.of(this).set(mob, (Object)entityId);
            return;
        }
    }

    public ArrayList<EntityLivingBase> getTargetedEntityVictims() {
        ArrayList<EntityLivingBase> mobs = new ArrayList<EntityLivingBase>();
        for (DataParameter<Integer> mob : this.tracking) {
            Entity entity;
            if ((Integer)EntityDataManager.of(this).get(mob) == 0 || (entity = this.worldObj.getEntityByID(((Integer)EntityDataManager.of(this).get(mob)).intValue())) == null) continue;
            mobs.add((EntityLivingBase)entity);
        }
        return mobs;
    }

    public void applyEntityCollision(Entity entityIn) {
    }

    public AxisAlignedBB getBoundingBox() {
        return Boxes.create(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    protected void readEntityFromNBT(NBTTagCompound compound) {
    }

    protected void writeEntityToNBT(NBTTagCompound compound) {
    }

    @SideOnly(value=Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 65536.0;
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnParticles(EnumParticleTypes particleType) {
        double d0 = this.rand.nextGaussian() * 0.02;
        double d1 = this.rand.nextGaussian() * 0.02;
        double d2 = this.rand.nextGaussian() * 0.02;
        this.worldObj.spawnParticle(particleType, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, this.posY + 0.5 + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, d0, d1, d2, new int[0]);
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnParticles(SRPEnumParticle particleType, int r, int g, int b) {
        double d0 = this.rand.nextGaussian() * 0.02;
        double d1 = this.rand.nextGaussian() * 0.02;
        double d2 = this.rand.nextGaussian() * 0.02;
        ParticleSpawner.spawnParticle(particleType, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, this.posY + 0.5 + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, d0, d1, d2, r, g, b);
    }

    @SideOnly(value=Side.CLIENT)
    public void spawnOrbEffects(int cap1) {
        for (int i = -cap1; i <= cap1; ++i) {
            for (int j = -cap1; j <= cap1; ++j) {
                if (i > -2 && i < 2 && j == -1) {
                    j = 2;
                }
                if (this.rand.nextInt(16) != 0) continue;
                for (int k = 0; k <= 5; ++k) {
                    this.worldObj.spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5) * ((double)this.width * 2.0), this.posY + this.rand.nextDouble() * 2.0 * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5) * ((double)this.width * 2.0), (double)((float)i + this.rand.nextFloat()) - 0.5, (double)((float)k - this.rand.nextFloat() - 1.0f), (double)((float)j + this.rand.nextFloat()) - 0.5, new int[]{0, 0, 0});
                }
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public float getSelfeFlashIntensity(float p_70831_1_) {
        return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * p_70831_1_ * 5.0f) / (float)(this.getFuseState() - 2);
    }
}

