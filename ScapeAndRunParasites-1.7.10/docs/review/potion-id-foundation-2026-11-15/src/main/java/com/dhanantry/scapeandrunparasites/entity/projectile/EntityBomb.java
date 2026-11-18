/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.MoverType
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
 *  net.minecraft.util.EntitySelectors
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.event.ForgeEventFactory
 */
package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityBomb
extends Entity {
    private static final DataParameter<Integer> FUSE = EntityDataManager.createKey(EntityBomb.class, (DataSerializer)DataSerializers.VARINT);
    @Nullable
    private EntityParasiteBase tntPlacedBy;
    private int fuse = 80;
    private float str = 4.0f;
    private float damage;
    private boolean grief;
    private int rangeRad;
    private static final DataParameter<Byte> SKIN = EntityDataManager.createKey(EntityBomb.class, (DataSerializer)DataSerializers.BYTE);

    public EntityBomb(World worldIn) {
        super(worldIn);
        this.preventEntitySpawning = true;
        this.isImmuneToFire = true;
        this.setSize(0.68f, 0.68f);
    }

    public EntityBomb(World worldIn, EntityParasiteBase igniter, boolean canGrief) {
        this(worldIn);
        this.tntPlacedBy = igniter;
        this.setPosition(igniter.posX, igniter.posY + (double)igniter.getEyeHeight() - (double)0.1f, igniter.posZ);
        this.grief = canGrief;
    }

    public EntityBomb(World worldIn, double x, double y, double z, EntityParasiteBase igniter, float stren) {
        this(worldIn);
        this.setPosition(x, y, z);
        float f = (float)(Math.random() * (Math.PI * 2));
        this.motionX = -((float)Math.sin(f)) * 0.02f;
        this.motionY = 0.2f;
        this.motionZ = -((float)Math.cos(f)) * 0.02f;
        this.setFuse(80);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.tntPlacedBy = igniter;
        this.str = stren;
    }

    protected void entityInit() {
        EntityDataManager.of(this).register(FUSE, (Object)80);
        EntityDataManager.of(this).register(SKIN, (Object)((byte)0));
    }

    public void setInWeb() {
    }

    protected boolean canTriggerWalking() {
        return false;
    }

    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
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
        --this.fuse;
        if (this.fuse <= 0) {
            this.explode();
        } else {
            this.handleWaterMovement();
        }
        this.onLivingUpdate();
    }

    public void onLivingUpdate() {
        this.collideWithNearbyEntities();
    }

    protected void collideWithNearbyEntities() {
        List list = this.worldObj.getEntitiesInAABBexcluding((Entity)this, Boxes.entityBox(this), EntitySelectors.getTeamCollisionPredicate((Entity)this));
        if (!list.isEmpty()) {
            int i = this.worldObj.getGameRules().getInt("maxEntityCramming");
            if (i > 0 && list.size() > i - 1 && this.rand.nextInt(4) == 0) {
                int j = 0;
                for (int k = 0; k < list.size(); ++k) {
                    if (((Entity)list.get(k)).isRiding()) continue;
                    ++j;
                }
                if (j > i - 1) {
                    this.attackEntityFrom(DamageSource.CRAMMING, 6.0f);
                }
            }
            for (int l = 0; l < list.size(); ++l) {
                Entity entity = (Entity)list.get(l);
                this.collideWithEntity(entity);
            }
        }
    }

    protected void collideWithEntity(Entity entityIn) {
        entityIn.applyEntityCollision((Entity)this);
    }

    public void onEntityUpdate() {
        super.onEntityUpdate();
    }

    private void explode() {
        if (this.str > 0.0f) {
            boolean flag = ForgeEventFactory.getMobGriefingEvent((World)this.worldObj, (Entity)this) && this.grief;
            ParasiteEventEntity.createExplosion(this.worldObj, this, this.posX, this.posY, this.posZ, this.str, flag);
        }
        if (this.worldObj.isRemote) {
            return;
        }
        this.worldObj.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 0.5f, (1.0f + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2f) * 0.7f);
        float f = 4.0f;
        if (this.tntPlacedBy != null) {
            AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), (double)this.rangeRad);
            List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
            for (EntityLivingBase mob : moblist) {
                if (mob instanceof EntityParasiteBase || !mob.canEntityBeSeen((Entity)this)) continue;
                mob.attackEntityFrom(DamageSource.causeThrownDamage((Entity)this, (Entity)this.tntPlacedBy), this.damage);
                SRPPotions.applyStackPotion(SRPPotions.VIRA_E, mob, 300, 0);
                if (!this.tntPlacedBy.isEntityAlive()) continue;
                this.tntPlacedBy.attackEntityAsMobMinimum(mob, this.tntPlacedBy.getMiniDamage() * 3.0f);
            }
        }
        if (this.getSkin() == 2) {
            ParasiteEventEntity.spawnFromList(this, SRPConfigMobs.jinjoMobs, null);
        }
        EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(this.worldObj, this.posX, this.posY, this.posZ);
        entityareaeffectcloud.setRadius(this.rangeRad, 0.5f);
        entityareaeffectcloud.setWaitTime(5);
        entityareaeffectcloud.setDuration(60);
        entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
        entityareaeffectcloud.addEffect(new PotionEffect(net.minecraft.potion.Potion.poison, 300, 0));
        entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
        entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.VIRA_E, 3600, 0, false, false));
        this.worldObj.spawnEntityInWorld((Entity)entityareaeffectcloud);
        this.setDead();
    }

    public void setMotion(double xSpeedIn, double ySpeedIn, double zSpeedIn, double capX, double capY) {
        xSpeedIn = Math.min(xSpeedIn, capX);
        ySpeedIn = Math.min(ySpeedIn, capY);
        zSpeedIn = Math.min(zSpeedIn, capX);
        this.motionX = xSpeedIn * (Math.random() * 2.0 - 1.0);
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn * (Math.random() * 2.0 - 1.0);
    }

    public void shoot(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy) {
        float f = -MathHelper.sin((float)(rotationYawIn * ((float)Math.PI / 180))) * MathHelper.cos((float)(rotationPitchIn * ((float)Math.PI / 180)));
        float f1 = -MathHelper.sin((float)((rotationPitchIn + pitchOffset) * ((float)Math.PI / 180)));
        float f2 = MathHelper.cos((float)(rotationYawIn * ((float)Math.PI / 180))) * MathHelper.cos((float)(rotationPitchIn * ((float)Math.PI / 180)));
        this.shootTwo(f, f1, f2, velocity, inaccuracy);
        this.motionX += entityThrower.motionX;
        this.motionZ += entityThrower.motionZ;
        if (!entityThrower.onGround) {
            this.motionY += entityThrower.motionY;
        }
    }

    public void shootTwo(double x, double y, double z, float velocity, float inaccuracy) {
        float f = MathHelper.sqrt_double((double)(x * x + y * y + z * z));
        x /= (double)f;
        y /= (double)f;
        z /= (double)f;
        x += this.rand.nextGaussian() * (double)0.0075f * (double)inaccuracy;
        y += this.rand.nextGaussian() * (double)0.0075f * (double)inaccuracy;
        z += this.rand.nextGaussian() * (double)0.0075f * (double)inaccuracy;
        this.motionX = x *= (double)velocity;
        this.motionY = y *= (double)velocity;
        this.motionZ = z *= (double)velocity;
        float f1 = MathHelper.sqrt_double((double)(x * x + z * z));
        this.rotationYaw = (float)(MathHelper.atan2((double)x, (double)z) * 57.29577951308232);
        this.rotationPitch = (float)(MathHelper.atan2((double)y, (double)f1) * 57.29577951308232);
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setShort("Fuse", (short)this.getFuse());
        compound.setInteger("parasitetype", (int)this.getSkin());
        compound.setFloat("stren", this.str);
        compound.setBoolean("cangrief", this.grief);
    }

    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.setFuse(compound.getShort("Fuse"));
        if (compound.hasKey("parasitetype", 99)) {
            this.setSkin(compound.getInteger("parasitetype"));
        }
        if (compound.hasKey("stren", 99)) {
            this.str = compound.getFloat("stren");
        }
        if (compound.hasKey("cangrief", 99)) {
            this.grief = compound.getBoolean("cangrief");
        }
    }

    @Nullable
    public EntityLivingBase getTntPlacedBy() {
        return this.tntPlacedBy;
    }

    public float getEyeHeight() {
        return 0.0f;
    }

    public void setFuse(int fuseIn) {
        EntityDataManager.of(this).set(FUSE, (Object)fuseIn);
        this.fuse = fuseIn;
    }

    public void setStren(float in) {
        this.str = in;
    }

    public void updateSTR() {
        this.worldObj.setEntityState((Entity)this, (byte)this.str);
    }

    public void notifyDataManagerChange(DataParameter<?> key) {
        if (FUSE.equals(key)) {
            this.fuse = this.getFuseDataManager();
        }
    }

    public int getFuseDataManager() {
        return (Integer)EntityDataManager.of(this).get(FUSE);
    }

    public int getFuse() {
        return this.fuse;
    }

    public byte getSkin() {
        return (Byte)EntityDataManager.of(this).get(SKIN);
    }

    public void setSkin(int texture) {
        EntityDataManager.of(this).set(SKIN, (Object)((byte)texture));
    }

    public void setDamage(float in, int radius) {
        this.damage = in;
        this.rangeRad = radius;
    }

    public void handleHealthUpdate(byte id) {
        this.str = id;
        if (id >= 2) {
            this.grief = true;
        }
    }
}

