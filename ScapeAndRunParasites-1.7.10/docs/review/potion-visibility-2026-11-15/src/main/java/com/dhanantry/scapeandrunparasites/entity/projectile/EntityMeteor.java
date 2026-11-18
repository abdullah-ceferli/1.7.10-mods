/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.entity.projectile.ProjectileHelper
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.World
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.entity.EntityOrbBoom;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.network.MsgQlipShake;
import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteMeteorCrash;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityMeteor
extends Entity {
    private int ticksAlive;
    private int ticksInAir;
    public double accelerationX;
    public double accelerationY;
    public double accelerationZ;
    public float prevRenderYawOffset;
    public float renderYawOffset;
    public float prevRotationYawHead;
    public float rotationYawHead;
    public float prevLimbSwingAmount;
    public float limbSwingAmount;
    public float limbSwing;
    public int hurtTime;
    public int deathTime;
    public static final DataParameter<Boolean> FATHER = EntityDataManager.createKey(EntityMeteor.class, (DataSerializer)DataSerializers.BOOLEAN);

    public EntityMeteor(World worldIn) {
        super(worldIn);
        this.setSize(4.5f, 4.5f);
        this.ignoreFrustumCheck = true;
        this.setRoot(true);
    }

    public EntityMeteor(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
        this(worldIn);
        this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
        this.setPosition(x, y, z);
        double d0 = MathHelper.sqrt_double((double)(accelX * accelX + accelY * accelY + accelZ * accelZ));
        this.accelerationX = accelX / d0 * 0.1;
        this.accelerationY = accelY / d0 * 0.1;
        this.accelerationZ = accelZ / d0 * 0.1;
    }

    public void setDead() {
        super.setDead();
    }

    protected void entityInit() {
        EntityDataManager.of(this).register(FATHER, (Object)false);
    }

    @SideOnly(value=Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 65536.0;
    }

    public void onUpdate() {
        super.onUpdate();
        if (!this.worldObj.isRemote && this.getRoot() && this.ticksExisted % 20 == 0) {
            List players = EntityQueries.players(this.worldObj);
            for (EntityPlayer mob : players) {
                float str = EntityMeteor.getDistancePack(EntityQueries.position(this), EntityQueries.position(mob), 150);
                if (!(str > 0.0f)) continue;
                SRPNetwork.CHANNEL.sendTo((IMessage)new MsgQlipShake(20, 0, false, true, str * 2.0f), (EntityPlayerMP)mob);
            }
            if (this.rand.nextInt(2) == 0) {
                double spread = 0.9;
                double vx = this.motionX + (this.worldObj.rand.nextDouble() - 0.5) * spread;
                double vy = this.motionY + (this.worldObj.rand.nextDouble() - 0.5) * spread;
                double vz = this.motionZ + (this.worldObj.rand.nextDouble() - 0.5) * spread;
                EntityMeteor fragment = new EntityMeteor(this.worldObj, this.posX, this.posY, this.posZ, vx, vy, vz);
                fragment.setRoot(false);
                this.worldObj.spawnEntityInWorld((Entity)fragment);
            }
        }
        ++this.ticksInAir;
        RayTraceResult raytraceresult = ProjectileHelper.forwardsRaycast((Entity)this, (boolean)true, (this.ticksInAir >= 25 ? 1 : 0) != 0, (Entity)this);
        if (raytraceresult != null && !ForgeEventFactory.onProjectileImpact((Entity)this, (RayTraceResult)raytraceresult)) {
            this.onImpact();
        }
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        ProjectileHelper.rotateTowardsMovement((Entity)this, (float)0.2f);
        float f = this.getMotionFactor();
        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                float f1 = 0.25f;
                this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25, this.posY - this.motionY * 0.25, this.posZ - this.motionZ * 0.25, this.motionX, this.motionY, this.motionZ, new int[0]);
            }
            f = 0.8f;
        }
        this.motionX += this.accelerationX;
        this.motionY += this.accelerationY;
        this.motionZ += this.accelerationZ;
        this.motionX *= (double)f;
        this.motionY *= (double)f;
        this.motionZ *= (double)f;
        this.worldObj.spawnParticle(this.getParticleType(), this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0, new int[0]);
        this.setPosition(this.posX, this.posY, this.posZ);
        if (this.ticksInAir > 1200 || this.posY <= 0.0) {
            this.onImpact();
        }
        if (this.worldObj.isRemote) {
            for (int i = 0; i < 5; ++i) {
                double offsetX = (this.worldObj.rand.nextDouble() - 0.5) * (double)this.width;
                double offsetY = (this.worldObj.rand.nextDouble() - 0.5) * (double)this.height * 4.0;
                double offsetZ = (this.worldObj.rand.nextDouble() - 0.5) * (double)this.width;
                double particleX = this.posX + offsetX;
                double particleY = this.posY + offsetY + (double)this.height * 0.5 * 2.0;
                double particleZ = this.posZ + offsetZ;
                double particleVX = -this.motionX + this.worldObj.rand.nextGaussian() * 0.05;
                double particleVY = -this.motionY + this.worldObj.rand.nextGaussian() * 0.05;
                double particleVZ = -this.motionZ + this.worldObj.rand.nextGaussian() * 0.05;
                this.worldObj.spawnParticle(EnumParticleTypes.FLAME, particleX, particleY, particleZ, particleVX, particleVY, particleVZ, new int[0]);
                if (!this.getRoot()) {
                    this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, particleX, particleY, particleZ, particleVX, particleVY, particleVZ, new int[0]);
                    continue;
                }
                this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, particleX, particleY, particleZ, particleVX, particleVY, particleVZ, new int[0]);
            }
        }
    }

    protected void onImpact() {
        if (!this.worldObj.isRemote) {
            if (this.getRoot()) {
                ArrayList mobs = Lists.newArrayList();
                mobs.addAll(EntityQueries.loaded(this.worldObj));
                if (mobs.size() != 0) {
                    for (Entity mob : mobs) {
                        float str;
                        if (!(mob instanceof EntityLivingBase)) continue;
                        if (mob instanceof EntityPlayer && (str = EntityMeteor.getDistancePack(EntityQueries.position(this), EntityQueries.position(mob), 400)) > 0.0f) {
                            SRPNetwork.CHANNEL.sendTo((IMessage)new MsgQlipShake(150, 0, false, true, str * 8.0f), (EntityPlayerMP)mob);
                        }
                        if ((str = EntityMeteor.getDistancePack(EntityQueries.position(this), EntityQueries.position(mob), SRPConfigWorld.meteorDamage)) > 0.0f) {
                            mob.attackEntityFrom(DamageSource.fallingBlock, str * 450.0f);
                        }
                        if (!((double)(str = EntityMeteor.getDistancePack(EntityQueries.position(this), EntityQueries.position(mob), 800)) > 0.5)) continue;
                        ((EntityLivingBase)mob).addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.COTH_E, 1200, 0, false, false));
                    }
                }
            }
            int rad = 40;
            if (!this.getRoot()) {
                rad = 8;
            }
            EntityOrbBoom orb = new EntityOrbBoom(this.worldObj, null, rad, 1);
            orb.copyLocationAndAnglesFrom(this);
            this.worldObj.spawnEntityInWorld((Entity)orb);
            WorldGenParasiteMeteorCrash ccc = new WorldGenParasiteMeteorCrash(false, this.getRoot() ? 5 : 1);
            ccc.generate(this.worldObj, new Random(), EntityQueries.position(this));
            if (this.getRoot() && SRPConfigWorld.originActivated) {
                ParasiteEventWorld.placeOriginInWorld(this.worldObj, new BlockPos(this.posX, this.posY, this.posZ), SRPConfigWorld.originHealth, SRPConfigWorld.originRadius);
            }
            this.setDead();
        }
    }

    protected EnumParticleTypes getParticleType() {
        return EnumParticleTypes.SMOKE_NORMAL;
    }

    protected float getMotionFactor() {
        return 0.95f;
    }

    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setTag("direction", (NBTBase)this.newDoubleNBTList(new double[]{this.motionX, this.motionY, this.motionZ}));
        compound.setTag("power", (NBTBase)this.newDoubleNBTList(new double[]{this.accelerationX, this.accelerationY, this.accelerationZ}));
        compound.setInteger("life", this.ticksAlive);
        compound.setBoolean("bigmet", this.getRoot());
    }

    public void readEntityFromNBT(NBTTagCompound compound) {
        NBTTagList nbttaglist;
        if (compound.hasKey("bigmet")) {
            this.setRoot(compound.getBoolean("bigmet"));
        }
        if (compound.hasKey("power", 9) && (nbttaglist = compound.getTagList("power", 6)).tagCount() == 3) {
            this.accelerationX = nbttaglist.getDoubleAt(0);
            this.accelerationY = nbttaglist.getDoubleAt(1);
            this.accelerationZ = nbttaglist.getDoubleAt(2);
        }
        this.ticksAlive = compound.getInteger("life");
        if (compound.hasKey("direction", 9) && compound.getTagList("direction", 6).tagCount() == 3) {
            NBTTagList nbttaglist1 = compound.getTagList("direction", 6);
            this.motionX = nbttaglist1.getDoubleAt(0);
            this.motionY = nbttaglist1.getDoubleAt(1);
            this.motionZ = nbttaglist1.getDoubleAt(2);
        }
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public float getCollisionBorderSize() {
        return 1.0f;
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) {
            return false;
        }
        this.setBeenAttacked();
        return true;
    }

    public float getBrightness() {
        return 1.0f;
    }

    public void lookAt(Entity in) {
        this.lookAt(in.posX, in.posY, in.posZ);
    }

    public void lookAt(double x, double y, double z) {
        double dx = x - this.posX;
        double dy = y - (this.posY + (double)this.getEyeHeight());
        double dz = z - this.posZ;
        double yaw = Math.atan2(dz, dx) * 57.29577951308232 - 90.0;
        double distance = Math.sqrt(dx * dx + dz * dz);
        double pitch = -Math.atan2(dy, distance) * 57.29577951308232;
        this.rotationYaw = (float)yaw;
        this.rotationPitch = (float)pitch;
    }

    @SideOnly(value=Side.CLIENT)
    public int getBrightnessForRender() {
        return 0xF000F0;
    }

    public void setRoot(boolean in) {
        EntityDataManager.of(this).set(FATHER, (Object)in);
    }

    public boolean getRoot() {
        return (Boolean)EntityDataManager.of(this).get(FATHER);
    }

    public static float getDistancePack(BlockPos pos1, BlockPos pos2, int maxDistance) {
        if (maxDistance <= 0) {
            return 0.0f;
        }
        double maxDistSq = maxDistance * maxDistance;
        double distSq = pos1.distanceSq((Vec3i)pos2);
        double value = 1.0 - distSq / maxDistSq;
        return (float)Math.max(0.0, Math.min(1.0, value));
    }
}

