/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.CooldownTracker
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
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityOrbScary
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
    private double poosX;
    private double poosY;
    private double poosZ;
    private static final DataParameter<Integer> SELFE = EntityDataManager.createKey(EntityOrbScary.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> FUSE = EntityDataManager.createKey(EntityOrbScary.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> WAITSTART = EntityDataManager.createKey(EntityOrbScary.class, (DataSerializer)DataSerializers.VARINT);

    public EntityOrbScary(World worldIn) {
        super(worldIn);
        this.setSize(0.5f, 0.5f);
        this.ignoreFrustumCheck = true;
        this.isImmuneToFire = true;
        this.lastActiveTime = 0;
        this.timeSinceIgnited = 0;
        this.followF = false;
        this.setFuseState(7);
        this.setStartState(40);
        this.setNoGravity(false);
    }

    public EntityOrbScary(World worldIn, EntityPMalleable in, int fuse, int waitStart) {
        this(worldIn);
        this.father = in;
        this.followF = true;
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

    public EntityOrbScary(World worldIn, EntityPMalleable in, int fuse, int waitStart, boolean stayPY) {
        this(worldIn, in, fuse, waitStart);
        this.followF = stayPY;
    }

    protected void entityInit() {
        EntityDataManager.of(this).register(SELFE, (Object)(-1));
        EntityDataManager.of(this).register(FUSE, (Object)(-1));
        EntityDataManager.of(this).register(WAITSTART, (Object)(-1));
    }

    public void onUpdate() {
        super.onUpdate();
        if (!this.worldObj.isRemote && this.ticksExisted == 1) {
            this.playSound(SRPSounds.ORB_SUMMON, 1.0f, 1.0f);
            this.playSound(SRPSounds.ORB_IDLE, 1.0f, 1.0f);
        }
        if (this.ticksExisted > this.getStartState()) {
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
            if (this.father != null) {
                if (this.father.isEntityAlive() && this.followF) {
                    this.posX = this.father.posX;
                    this.posY = this.father.posY - this.rand.nextDouble() * 0.1;
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
                    this.poosY = this.father.posY;
                    this.poosZ = this.father.posZ;
                    this.posX = this.father.posX;
                    this.posY = this.father.posY - this.rand.nextDouble() * 0.1;
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
        this.orbDoing();
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
            this.setSize(this.width + 0.8f, this.height + 0.482f);
        }
    }

    protected void selfExplode() {
        this.setSelfeState(2);
        if (this.getSelfeState() == 2) {
            ++this.timerDDD;
            if (this.timerDDD > 35) {
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
                    this.spawnParticles(EnumParticleTypes.EXPLOSION_LARGE);
                }
                this.playSound(SRPSounds.ORB_E, 1.0f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
                if (this.timerDDD > 45) {
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
        if (this.father == null) {
            List moblist = EntityQueries.entities(this.worldObj, EntityPlayer.class, axisalignedbb);
            for (EntityPlayer player : moblist) {
                if (player.capabilities.isCreativeMode) continue;
                int ccc = 100;
                CooldownTracker track = player.getCooldownTracker();
                for (int i = 0; i < player.inventory.mainInventory.size(); ++i) {
                    if (track.getCooldown(((ItemStack)player.inventory.mainInventory.get(i)).getItem(), 1.0f) != 0.0f) continue;
                    track.setCooldown(((ItemStack)player.inventory.mainInventory.get(i)).getItem(), ccc);
                }
                if (track.getCooldown(player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).getItem(), 1.0f) != 0.0f) continue;
                track.setCooldown(player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).getItem(), ccc);
            }
            return;
        }
        List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
        for (EntityLivingBase mob : moblist) {
            this.father.scaryOrbEffect(mob, moblist.size());
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
                    ParticleSpawner.spawnParticle(SRPEnumParticle.EEN, this.posX + (this.rand.nextDouble() - 0.5) * ((double)this.width * 2.0), this.posY + this.rand.nextDouble() * 2.0 * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5) * ((double)this.width * 2.0), (double)((float)i + this.rand.nextFloat()) - 0.5, (float)k - this.rand.nextFloat() - 1.0f, (double)((float)j + this.rand.nextFloat()) - 0.5, 255, 0, 0);
                }
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public float getSelfeFlashIntensity(float p_70831_1_) {
        return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * p_70831_1_ * 5.0f) / (float)(this.getFuseState() - 2);
    }
}

