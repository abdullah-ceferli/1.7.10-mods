/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
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
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityOrbBoom
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
    private double str;
    public double offsetOrb;
    private double poosX;
    private double poosY;
    private double poosZ;
    private static final DataParameter<Integer> SELFE = EntityDataManager.createKey(EntityOrbBoom.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> FUSE = EntityDataManager.createKey(EntityOrbBoom.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> WAITSTART = EntityDataManager.createKey(EntityOrbBoom.class, (DataSerializer)DataSerializers.VARINT);
    public float alpha;

    public EntityOrbBoom(World worldIn) {
        super(worldIn);
        this.setSize(0.5f, 0.5f);
        this.ignoreFrustumCheck = true;
        this.isImmuneToFire = true;
        this.lastActiveTime = 0;
        this.timeSinceIgnited = 0;
        this.setFuseState(7);
        this.setStartState(40);
        com.dhanantry.scapeandrunparasites.backport.entity.movement.GravityAccess.set(this, false);
        this.alpha = 1.0f;
        this.str = 0.2;
    }

    public EntityOrbBoom(World worldIn, EntityPMalleable in, int fuse, int waitStart) {
        this(worldIn);
        if (in != null) {
            this.father = in;
            this.prevRenderYawOffset = in.prevRenderYawOffset;
            this.renderYawOffset = in.renderYawOffset;
            this.prevRotationYawHead = in.prevRotationYawHead;
            this.rotationYawHead = in.rotationYawHead;
            this.prevLimbSwingAmount = in.prevLimbSwingAmount;
            this.limbSwingAmount = in.limbSwingAmount;
            this.limbSwing = in.limbSwing;
        }
        this.setFuseState(fuse);
        this.setStartState(waitStart);
    }

    public EntityOrbBoom(World worldIn, EntityPMalleable in, int fuse, int waitStart, boolean stayPY) {
        this(worldIn, in, fuse, waitStart);
    }

    protected void entityInit() {
        EntityDataManager.of(this).register(SELFE, (Object)(-1));
        EntityDataManager.of(this).register(FUSE, (Object)(-1));
        EntityDataManager.of(this).register(WAITSTART, (Object)(-1));
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted > this.getStartState()) {
            this.orbDoing();
            this.setSelfeState(1);
            this.dyingBurst(true, 1);
            if (this.worldObj.isRemote) {
                if (this.father != null) {
                    this.prevRenderYawOffset = this.father.prevRenderYawOffset;
                    this.renderYawOffset = this.father.renderYawOffset;
                    this.prevRotationYawHead = this.father.prevRotationYawHead;
                    this.rotationYawHead = this.father.rotationYawHead;
                }
                this.spawnOrbEffects(4);
                return;
            }
            this.posX = this.poosX;
            this.posY = this.poosY - this.rand.nextDouble() * 0.1;
            this.posZ = this.poosZ;
        } else {
            if (this.worldObj.isRemote) {
                this.spawnOrbEffects(4);
                return;
            }
            this.poosX = this.posX;
            this.poosY = this.posY;
            this.poosZ = this.posZ;
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
            this.setSize(this.width + 1.0f, this.height + 0.4f);
        }
    }

    protected void selfExplode() {
        this.setSelfeState(2);
        if (this.getSelfeState() == 2) {
            ++this.timerDDD;
            if (this.timerDDD > 1) {
                this.alpha = Math.max(this.alpha - 0.2f, 0.0f);
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
                    double offsetX = (this.worldObj.rand.nextDouble() - 0.5) * (double)this.width;
                    double offsetY = (this.worldObj.rand.nextDouble() - 0.5) * (double)this.height;
                    double offsetZ = (this.worldObj.rand.nextDouble() - 0.5) * (double)this.width;
                    double particleX = this.posX + offsetX;
                    double particleY = this.posY + offsetY;
                    double particleZ = this.posZ + offsetZ;
                    double particleVX = -this.motionX + this.worldObj.rand.nextGaussian() * 0.05;
                    double particleVY = -this.motionY + this.worldObj.rand.nextGaussian() * 0.05;
                    double particleVZ = -this.motionZ + this.worldObj.rand.nextGaussian() * 0.05;
                    com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(this.worldObj, EnumParticleTypes.EXPLOSION_HUGE, particleX, particleY, particleZ, particleVX, particleVY, particleVZ, new int[0]);
                }
                com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this, SRPSounds.ORB_E, 1.0f, 1.0f);
                if (this.timerDDD > 5) {
                    this.setDead();
                }
            }
        }
    }

    private void orbDoing() {
        if (this.worldObj.isRemote) {
            return;
        }
        if (this.ticksExisted % 10 != 0) {
            return;
        }
        float f = this.width / 2.0f;
        float f1 = this.height;
        AxisAlignedBB axisalignedbb = Boxes.create(this.posX - (double)f, this.posY - (double)f1, this.posZ - (double)f, this.posX + (double)f, this.posY + (double)f1, this.posZ + (double)f);
        List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
        if (this.father == null) {
            for (EntityLivingBase mob : moblist) {
                mob.attackEntityFrom(DamageSource.magic, 10.0f);
            }
            return;
        }
        for (EntityLivingBase mob : moblist) {
            if (mob instanceof EntityPCosmical) continue;
            this.father.attackEntityAsMob((Entity)mob);
        }
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
    public void spawnParticles(EnumParticleTypes particleType) {
        double d0 = this.rand.nextGaussian() * 0.02;
        double d1 = this.rand.nextGaussian() * 0.02;
        double d2 = this.rand.nextGaussian() * 0.02;
        com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(this.worldObj, particleType, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, this.posY + 0.5 + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, d0, d1, d2, new int[0]);
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
                if (i <= -2 || i >= 2 || j != -1) continue;
                j = 2;
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 65536.0;
    }

    @SideOnly(value=Side.CLIENT)
    public float getSelfeFlashIntensity(float p_70831_1_) {
        return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * p_70831_1_ * 5.0f) / (float)(this.getFuseState() - 2);
    }
}

