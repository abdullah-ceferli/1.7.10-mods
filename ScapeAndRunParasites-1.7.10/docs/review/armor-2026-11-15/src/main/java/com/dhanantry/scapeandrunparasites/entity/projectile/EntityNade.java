/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.MoverType
 *  net.minecraft.entity.SharedMonsterAttributes
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
package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
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

public class EntityNade
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
    private EntityParasiteBase father;
    private double poosX;
    private double poosY;
    private double poosZ;
    private static final DataParameter<Integer> SELFE = EntityDataManager.createKey(EntityNade.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> FUSE = EntityDataManager.createKey(EntityNade.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> WAITSTART = EntityDataManager.createKey(EntityNade.class, (DataSerializer)DataSerializers.VARINT);

    public EntityNade(World worldIn) {
        super(worldIn);
        this.setSize(0.5f, 0.5f);
        this.ignoreFrustumCheck = true;
        this.isImmuneToFire = true;
        this.lastActiveTime = 0;
        this.timeSinceIgnited = 0;
        this.setFuseState(3);
        this.setStartState(10);
    }

    public EntityNade(World worldIn, int fuse, int waitStart) {
        this(worldIn);
        this.setFuseState(fuse);
        this.setStartState(waitStart);
    }

    protected void entityInit() {
        EntityDataManager.of(this).register(SELFE, (Object)(-1));
        EntityDataManager.of(this).register(FUSE, (Object)(-1));
        EntityDataManager.of(this).register(WAITSTART, (Object)(-1));
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted > 3) {
            this.setSelfeState(1);
            this.dyingBurst(true, 1);
            if (this.worldObj.isRemote) {
                int i;
                for (i = 0; i < 5; ++i) {
                    this.spawnParticles(EnumParticleTypes.SMOKE_NORMAL);
                }
                for (i = 0; i < 2; ++i) {
                    this.spawnParticles(EnumParticleTypes.SMOKE_LARGE);
                }
                return;
            }
            this.posX = this.poosX;
            if (this.onGround) {
                this.posY += this.rand.nextDouble() * 0.01;
            }
            this.posZ = this.poosZ;
        } else {
            if (this.worldObj.isRemote) {
                int i;
                for (i = 0; i < 5; ++i) {
                    this.spawnParticles(EnumParticleTypes.SMOKE_NORMAL);
                }
                for (i = 0; i < 2; ++i) {
                    this.spawnParticles(EnumParticleTypes.SMOKE_LARGE);
                }
                return;
            }
            this.poosX = this.posX;
            this.poosZ = this.posZ;
            if (this.ticksExisted == 2) {
                this.playSound(SRPSounds.NADE_S, 1.0f, 1.0f);
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
        if (i <= 0 || this.timeSinceIgnited == 0) {
            // empty if block
        }
        this.timeSinceIgnited += i * value;
        if (this.timeSinceIgnited < 0) {
            this.timeSinceIgnited = 0;
        }
        if (this.timeSinceIgnited >= this.getFuseState()) {
            this.timeSinceIgnited = this.getFuseState();
            this.selfExplode();
            this.prevPosX = this.posX;
            if (this.onGround) {
                this.prevPosY = this.posY;
            }
            this.prevPosZ = this.posZ;
            if (!this.hasNoGravity()) {
                this.motionY -= (double)0.04f;
            }
            this.moveEntity(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)0.98f;
            this.motionY *= (double)0.98f;
            this.motionZ *= (double)0.98f;
            if (this.onGround) {
                this.motionX *= (double)0.7f;
                this.motionZ *= (double)0.7f;
                this.motionY *= -0.5;
            }
        } else {
            this.setSize(this.width + 0.8f, this.height + 0.32f);
        }
    }

    protected void selfExplode() {
        this.setSelfeState(2);
        if (this.getSelfeState() == 2) {
            ++this.timerDDD;
            if (!this.worldObj.isRemote) {
                if (this.father != null && this.father.isEntityAlive()) {
                    float f = this.width / 2.0f;
                    float f1 = this.height;
                    AxisAlignedBB axisalignedbb = Boxes.create(this.posX - (double)f, this.posY, this.posZ - (double)f, this.posX + (double)f, this.posY + (double)f1, this.posZ + (double)f);
                    List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
                    for (EntityLivingBase mob : moblist) {
                        if (mob instanceof EntityParasiteBase) continue;
                        mob.attackEntityFrom(DamageSource.generic, (float)this.father.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
                        this.father.attackEntityAsMobMinimum(mob, this.father.getMiniDamage());
                    }
                }
            } else {
                int par = this.getFuseState();
                int n = par + par / 2;
            }
            if (this.timerDDD > this.getStartState()) {
                this.setDead();
            }
        }
    }

    public void applyEntityCollision(Entity entityIn) {
        if (entityIn instanceof EntityPlayer && ((EntityPlayer)entityIn).capabilities.isCreativeMode) {
            return;
        }
    }

    public AxisAlignedBB getBoundingBox() {
        return Boxes.create(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    public void setFatherS(EntityParasiteBase in) {
        this.father = in;
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
                    ParticleSpawner.spawnParticle(SRPEnumParticle.EEN, this.posX + (this.rand.nextDouble() - 0.5) * ((double)this.width * 2.0), this.posY + this.rand.nextDouble() * 2.0 * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5) * ((double)this.width * 2.0), (double)((float)i + this.rand.nextFloat()) - 0.5, (float)k - this.rand.nextFloat() - 1.0f, (double)((float)j + this.rand.nextFloat()) - 0.5, 0, 0, 0);
                }
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public float getSelfeFlashIntensity(float p_70831_1_) {
        return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * p_70831_1_ * 5.0f) / (float)(this.getFuseState() - 2);
    }
}

