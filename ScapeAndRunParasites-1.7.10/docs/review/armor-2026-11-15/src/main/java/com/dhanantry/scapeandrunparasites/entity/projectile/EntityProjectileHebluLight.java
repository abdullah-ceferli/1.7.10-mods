/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.MoverType
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 */
package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityProjectileHebluLight
extends Entity {
    private EntityLivingBase owner;
    private EntityLivingBase target;
    private static long lastSpeedSoundTick = -100L;
    private static long lastImpactSoundTick = -100L;
    public static boolean DEBUG_IGNORE_CREATIVE_AND_SPECTATOR_PLAYERS = true;
    private static final int PARRY_IMMUNITY_TICKS = 25;
    private static final Map<UUID, Long> PLAYER_PARRY_IMMUNITY = new HashMap<UUID, Long>();
    private static final DataParameter<Boolean> SYNC_PARRIED = EntityDataManager.createKey(EntityProjectileHebluLight.class, (DataSerializer)DataSerializers.BOOLEAN);
    private static final DataParameter<Float> SYNC_DANGER = EntityDataManager.createKey(EntityProjectileHebluLight.class, (DataSerializer)DataSerializers.FLOAT);
    private int age;
    private int parryAge;
    private boolean parried;
    private double arcX;
    private double arcY;
    private double arcZ;
    private double stopHomingDistance;

    public EntityProjectileHebluLight(World worldIn) {
        super(worldIn);
        this.setSize(0.65f, 0.65f);
        this.noClip = false;
        this.ignoreFrustumCheck = true;
    }

    public EntityProjectileHebluLight(World worldIn, EntityLivingBase ownerIn, EntityLivingBase targetIn, double x, double y, double z) {
        this(worldIn, ownerIn, targetIn, x, y, z, false);
    }

    public EntityProjectileHebluLight(World worldIn, EntityLivingBase ownerIn, EntityLivingBase targetIn, double x, double y, double z, boolean forceTarget) {
        super(worldIn);
        Vec3d toTarget;
        this.owner = ownerIn;
        this.target = targetIn;
        if (!forceTarget && this.target instanceof EntityPlayer && EntityProjectileHebluLight.shouldIgnorePlayer((EntityPlayer)this.target)) {
            this.target = null;
        }
        if (this.target == null) {
            this.target = this.findFallbackPlayerTarget(x, y, z);
        }
        this.setSize(0.65f, 0.65f);
        this.setPosition(x, y, z);
        if (this.target != null) {
            toTarget = new Vec3d(this.target.posX - x, this.target.posY + (double)this.target.getEyeHeight() * 0.5 - y, this.target.posZ - z);
            if (toTarget.lengthVector() < 1.0E-4) {
                toTarget = new Vec3d(0.0, 0.0, 1.0);
            }
            toTarget = toTarget.normalize();
        } else {
            toTarget = new Vec3d(this.rand.nextDouble() - 0.5, this.rand.nextDouble() - 0.2, this.rand.nextDouble() - 0.5).normalize();
        }
        Vec3d side = toTarget.crossProduct(new Vec3d(0.0, 1.0, 0.0));
        if (side.lengthVector() < 1.0E-4) {
            side = new Vec3d(1.0, 0.0, 0.0);
        }
        side = side.normalize();
        Vec3d up = side.crossProduct(toTarget).normalize();
        double angle = this.rand.nextDouble() * Math.PI * 2.0;
        double sideAmount = Math.cos(angle);
        double upAmount = Math.sin(angle);
        Vec3d outward = side.scale(sideAmount).add(up.scale(upAmount)).add(toTarget.scale(-0.25));
        if (outward.lengthVector() < 1.0E-4) {
            outward = side;
        }
        outward = outward.normalize();
        this.arcX = outward.xCoord;
        this.arcY = outward.yCoord;
        this.arcZ = outward.zCoord;
        double startSpeed = 1.65 + this.rand.nextDouble() * 1.15;
        this.motionX = outward.xCoord * startSpeed;
        this.motionY = outward.yCoord * startSpeed;
        this.motionZ = outward.zCoord * startSpeed;
        this.stopHomingDistance = 5.0 + this.rand.nextDouble() * 5.0;
    }

    protected void entityInit() {
        EntityDataManager.of(this).register(SYNC_PARRIED, (Object)false);
        EntityDataManager.of(this).register(SYNC_DANGER, (Object)Float.valueOf(0.0f));
    }

    public void onUpdate() {
        super.onUpdate();
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (!this.worldObj.isRemote && (this.owner == null || this.owner.isDead)) {
            this.setDead();
            return;
        }
        ++this.age;
        this.updateSyncedRenderState();
        if (this.age > 95) {
            this.pop();
            return;
        }
        if (this.parried) {
            this.updateParriedMotion();
        } else {
            this.updateLightMotion();
        }
        Vec3d from = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d to = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult blockHit = this.worldObj.rayTraceBlocks(from, to, false, true, false);
        if (blockHit != null) {
            if (!this.worldObj.isRemote) {
                this.pop();
            }
            return;
        }
        this.moveEntity(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        if (!this.worldObj.isRemote) {
            this.checkEntityImpact();
        }
        this.spawnClientParticles();
    }

    private void updateLightMotion() {
        if (this.age < 5) {
            this.motionX *= 0.995;
            this.motionY *= 0.995;
            this.motionZ *= 0.995;
            return;
        }
        if (this.age < 10) {
            this.motionX *= 0.88;
            this.motionY *= 0.88;
            this.motionZ *= 0.88;
            return;
        }
        if (this.age == 10 && !this.worldObj.isRemote) {
            this.worldObj.setEntityState((Entity)this, (byte)4);
        }
        if (this.target == null || this.target.isDead) {
            this.motionX *= 0.99;
            this.motionY *= 0.99;
            this.motionZ *= 0.99;
            return;
        }
        Vec3d targetPos = new Vec3d(this.target.posX, Boxes.entityBox(this.target).minY + (double)this.target.height * 0.55, this.target.posZ);
        Vec3d toTarget = targetPos.subtract(this.posX, this.posY, this.posZ);
        double dist = toTarget.lengthVector();
        if (dist <= this.stopHomingDistance) {
            this.target = null;
            this.motionX *= 1.12;
            this.motionY *= 1.12;
            this.motionZ *= 1.12;
            this.limitSpeed(2.65);
            return;
        }
        int homingEnd = 54;
        if (this.age >= homingEnd) {
            this.target = null;
            this.motionX *= 1.04;
            this.motionY *= 1.04;
            this.motionZ *= 1.04;
            this.limitSpeed(2.45);
            return;
        }
        if (dist < 1.0E-4) {
            return;
        }
        double progress = MathHelper.clamp_double((double)((double)(this.age - 10) / 44.0), (double)0.0, (double)1.0);
        Vec3d desired = toTarget.normalize();
        double arcStrength = (1.0 - progress) * (1.0 - progress) * 1.05;
        desired = desired.addVector(this.arcX * arcStrength, this.arcY * arcStrength, this.arcZ * arcStrength).normalize();
        double targetSpeed = 0.95 + progress * 2.15;
        double turnStrength = 0.25 + progress * 0.17;
        this.motionX += (desired.xCoord * targetSpeed - this.motionX) * turnStrength;
        this.motionY += (desired.yCoord * targetSpeed - this.motionY) * turnStrength;
        this.motionZ += (desired.zCoord * targetSpeed - this.motionZ) * turnStrength;
        this.limitSpeed(targetSpeed + 0.55);
    }

    private void updateParriedMotion() {
        ++this.parryAge;
        if (this.target == null || this.target.isDead) {
            this.motionX *= 0.985;
            this.motionY *= 0.985;
            this.motionZ *= 0.985;
            if (this.parryAge > 35) {
                this.pop();
            }
            return;
        }
        if (this.parryAge < 13) {
            this.motionX *= 0.975;
            this.motionY *= 0.975;
            this.motionZ *= 0.975;
            return;
        }
        Vec3d targetPos = new Vec3d(this.target.posX, Boxes.entityBox(this.target).minY + (double)this.target.height * 0.55, this.target.posZ);
        Vec3d toTarget = targetPos.subtract(this.posX, this.posY, this.posZ);
        if (toTarget.lengthVector() < 1.0E-4) {
            return;
        }
        Vec3d desired = toTarget.normalize();
        double targetSpeed = 2.25;
        double turnStrength = 0.28;
        this.motionX += (desired.xCoord * targetSpeed - this.motionX) * turnStrength;
        this.motionY += (desired.yCoord * targetSpeed - this.motionY) * turnStrength;
        this.motionZ += (desired.zCoord * targetSpeed - this.motionZ) * turnStrength;
        this.limitSpeed(2.65);
    }

    private void limitSpeed(double max) {
        double speed = Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        if (speed > max && speed > 1.0E-4) {
            double scale = max / speed;
            this.motionX *= scale;
            this.motionY *= scale;
            this.motionZ *= scale;
        }
    }

    private static void giveParryImmunity(EntityLivingBase entity, World world) {
        if (!(entity instanceof EntityPlayer)) {
            return;
        }
        PLAYER_PARRY_IMMUNITY.put(entity.getUniqueID(), world.getTotalWorldTime() + 25L);
    }

    private static boolean hasParryImmunity(EntityLivingBase entity, World world) {
        if (!(entity instanceof EntityPlayer)) {
            return false;
        }
        Long endTime = PLAYER_PARRY_IMMUNITY.get(entity.getUniqueID());
        if (endTime == null) {
            return false;
        }
        if (world.getTotalWorldTime() > endTime) {
            PLAYER_PARRY_IMMUNITY.remove(entity.getUniqueID());
            return false;
        }
        return true;
    }

    private void checkEntityImpact() {
        AxisAlignedBB box = Boxes.grow(Boxes.entityBox(this), 0.45);
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, box);
        for (Entity entity : list) {
            if (entity == this.owner && !this.parried || entity instanceof EntityProjectileHebluLight || !entity.canBeCollidedWith()) continue;
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase living = (EntityLivingBase)entity;
                if (!this.parried && living == this.owner) continue;
                if (!this.parried && living instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer)living;
                    if (EntityProjectileHebluLight.shouldIgnorePlayer(player) || EntityProjectileHebluLight.hasParryImmunity(living, this.worldObj)) continue;
                    living.attackEntityFrom((DamageSource)new DamageSourceHebluLight(this, (Entity)this.owner), 10.0f);
                    living.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(SRPPotions.DISTORTED_ENLIGHTENMENT_E, 400, 0, false, true));
                    this.pop();
                    return;
                }
                if (this.parried) {
                    if (living != this.target) continue;
                    living.attackEntityFrom((DamageSource)new DamageSourceHebluLightNeutral(), 24.0f);
                    this.pop();
                    return;
                }
                living.attackEntityFrom((DamageSource)new DamageSourceHebluLightNeutral(), 7.0f);
                this.pop();
                return;
            }
            this.pop();
            return;
        }
    }

    private EntityLivingBase findParryTarget(Entity attacker) {
        AxisAlignedBB box = Boxes.grow(Boxes.entityBox(this), 32.0, 18.0, 32.0);
        List list = EntityQueries.entities(this.worldObj, EntityLivingBase.class, box);
        EntityLivingBase best = null;
        double bestDist = Double.MAX_VALUE;
        for (EntityLivingBase living : list) {
            double dist;
            String className;
            if (living == null || living.isDead || living == this.owner || living == attacker || living instanceof EntityPlayer || !(className = living.getClass().getName()).startsWith("com.dhanantry.scapeandrunparasites.entity.monster.") || !((dist = living.getDistanceSqToEntity((Entity)this)) < bestDist)) continue;
            bestDist = dist;
            best = living;
        }
        return best;
    }

    private EntityLivingBase getThrower() {
        return this.owner;
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.worldObj.isRemote || this.isDead) {
            return true;
        }
        Entity trueSource = source.getEntity();
        if (trueSource instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)trueSource;
            if (!EntityProjectileHebluLight.isSentientParryWeapon(attacker)) {
                return false;
            }
            this.parryNearbyLights(attacker, this);
            return true;
        }
        this.pop();
        return true;
    }

    private void parryNearbyLights(EntityLivingBase attacker, EntityProjectileHebluLight mainLight) {
        double radiusXZ = 6.0;
        double radiusY = 4.0;
        AxisAlignedBB box = Boxes.grow(Boxes.entityBox(mainLight), radiusXZ, radiusY, radiusXZ);
        List nearby = EntityQueries.entities(this.worldObj, EntityProjectileHebluLight.class, box);
        boolean playedEffects = false;
        for (EntityProjectileHebluLight light : nearby) {
            if (light == null || light.isDead || light.parried) continue;
            light.parryFrom(attacker, !playedEffects);
            playedEffects = true;
        }
        if (!(playedEffects || mainLight == null || mainLight.isDead || mainLight.parried)) {
            mainLight.parryFrom(attacker, true);
        }
    }

    private void parryFrom(EntityLivingBase attacker, boolean playEffects) {
        Vec3d randomSpread;
        Vec3d look = attacker.getLookVec();
        if (look == null || look.lengthVector() < 1.0E-4) {
            look = new Vec3d(this.posX - attacker.posX, this.posY - attacker.posY, this.posZ - attacker.posZ).normalize();
        }
        if ((randomSpread = new Vec3d(this.rand.nextDouble() - 0.5, this.rand.nextDouble() * 0.75 - 0.15, this.rand.nextDouble() - 0.5)).lengthVector() < 1.0E-4) {
            randomSpread = new Vec3d(0.0, 0.3, 1.0);
        }
        randomSpread = randomSpread.normalize();
        Vec3d bounce = look.scale(0.75).add(randomSpread.scale(1.25));
        if (bounce.lengthVector() < 1.0E-4) {
            bounce = look;
        }
        bounce = bounce.normalize();
        this.parried = true;
        EntityDataManager.of(this).set(SYNC_PARRIED, (Object)true);
        EntityDataManager.of(this).set(SYNC_DANGER, (Object)Float.valueOf(0.0f));
        this.parryAge = 0;
        this.age = 20;
        this.target = this.findParryTarget((Entity)attacker);
        this.motionX = bounce.xCoord * (1.85 + this.rand.nextDouble() * 0.65);
        this.motionY = bounce.yCoord * (1.85 + this.rand.nextDouble() * 0.65) + 0.12;
        this.motionZ = bounce.zCoord * (1.85 + this.rand.nextDouble() * 0.65);
        EntityProjectileHebluLight.giveParryImmunity(attacker, this.worldObj);
        if (playEffects) {
            this.playSound(SRPSounds.HEBLU_PARRY, 1.2f, 0.95f + this.rand.nextFloat() * 0.12f);
            if (this.worldObj instanceof WorldServer) {
                ((WorldServer)this.worldObj).spawnParticle(EnumParticleTypes.SWEEP_ATTACK, true, attacker.posX + look.xCoord * 1.2, attacker.posY + (double)attacker.getEyeHeight() * 0.65, attacker.posZ + look.zCoord * 1.2, 1, 0.15, 0.15, 0.15, 0.0, new int[0]);
                ((WorldServer)this.worldObj).spawnParticle(EnumParticleTypes.SWEEP_ATTACK, true, attacker.posX - look.zCoord * 0.75, attacker.posY + (double)attacker.getEyeHeight() * 0.7, attacker.posZ + look.xCoord * 0.75, 1, 0.1, 0.1, 0.1, 0.0, new int[0]);
                for (int i = 0; i < 12; ++i) {
                    ((WorldServer)this.worldObj).spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, true, attacker.posX + look.xCoord * 1.1, attacker.posY + (double)attacker.getEyeHeight() * 0.65, attacker.posZ + look.zCoord * 1.1, 1, 0.45, 0.28, 0.45, 0.09, new int[0]);
                }
            }
        }
    }

    private void pop() {
        if (!this.worldObj.isRemote) {
            this.worldObj.setEntityState((Entity)this, (byte)3);
            this.setDead();
        }
    }

    public void handleHealthUpdate(byte id) {
        if (id == 3) {
            long time = this.worldObj.getTotalWorldTime();
            if (time - lastImpactSoundTick > 2L) {
                lastImpactSoundTick = time;
                this.worldObj.playSound(this.posX, this.posY, this.posZ, SRPSounds.HEBLU_LIGHT_IMPACT, this.getSoundCategory(), 0.48f, 0.92f + this.rand.nextFloat() * 0.16f, false);
            }
            for (int i = 0; i < 8; ++i) {
                this.worldObj.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, this.posX, this.posY, this.posZ, (this.rand.nextDouble() - 0.5) * 0.22, (this.rand.nextDouble() - 0.5) * 0.22, (this.rand.nextDouble() - 0.5) * 0.22, new int[0]);
            }
            this.worldObj.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_FIREWORK_BLAST, this.getSoundCategory(), 0.22f, 1.8f + this.rand.nextFloat() * 0.25f, false);
        } else if (id == 4) {
            int i;
            for (i = 0; i < 10; ++i) {
                this.worldObj.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, this.posX, this.posY, this.posZ, (this.rand.nextDouble() - 0.5) * 0.35, (this.rand.nextDouble() - 0.5) * 0.35, (this.rand.nextDouble() - 0.5) * 0.35, new int[0]);
            }
            for (i = 0; i < 4; ++i) {
                this.worldObj.spawnParticle(EnumParticleTypes.END_ROD, this.posX, this.posY, this.posZ, -this.motionX * 0.12 + (this.rand.nextDouble() - 0.5) * 0.08, -this.motionY * 0.12 + (this.rand.nextDouble() - 0.5) * 0.08, -this.motionZ * 0.12 + (this.rand.nextDouble() - 0.5) * 0.08, new int[0]);
            }
            this.worldObj.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_FIREWORK_BLAST, this.getSoundCategory(), 0.18f, 2.0f + this.rand.nextFloat() * 0.2f, false);
        } else {
            super.handleHealthUpdate(id);
        }
    }

    private void spawnClientParticles() {
        if (!this.worldObj.isRemote) {
            return;
        }
        if (this.ticksExisted % 3 != 0) {
            return;
        }
        this.worldObj.spawnParticle(EnumParticleTypes.END_ROD, this.posX - this.motionX * 0.25, this.posY - this.motionY * 0.25, this.posZ - this.motionZ * 0.25, -this.motionX * 0.015, -this.motionY * 0.015, -this.motionZ * 0.015, new int[0]);
    }

    public boolean isParriedLight() {
        return (Boolean)EntityDataManager.of(this).get(SYNC_PARRIED);
    }

    public float getPlayerDangerColorAmount() {
        return ((Float)EntityDataManager.of(this).get(SYNC_DANGER)).floatValue();
    }

    private static boolean shouldIgnorePlayer(EntityPlayer player) {
        if (!DEBUG_IGNORE_CREATIVE_AND_SPECTATOR_PLAYERS) {
            return false;
        }
        return player.isCreative() || player.isSpectator();
    }

    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.age = compound.getInteger("Age");
        this.parryAge = compound.getInteger("ParryAge");
        this.parried = compound.getBoolean("Parried");
        this.arcX = compound.getDouble("ArcX");
        this.arcY = compound.getDouble("ArcY");
        this.arcZ = compound.getDouble("ArcZ");
        this.stopHomingDistance = compound.getDouble("StopHomingDistance");
    }

    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("Age", this.age);
        compound.setInteger("ParryAge", this.parryAge);
        compound.setBoolean("Parried", this.parried);
        compound.setDouble("ArcX", this.arcX);
        compound.setDouble("ArcY", this.arcY);
        compound.setDouble("ArcZ", this.arcZ);
        compound.setDouble("StopHomingDistance", this.stopHomingDistance);
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean canAttackWithItem() {
        return true;
    }

    public float getCollisionBorderSize() {
        return 1.15f;
    }

    private static boolean isSentientParryWeapon(EntityLivingBase attacker) {
        if (!(attacker instanceof EntityPlayer)) {
            return false;
        }
        EntityPlayer player = (EntityPlayer)attacker;
        return EntityProjectileHebluLight.isSentientParryWeaponStack(player.getHeldItemMainhand()) || EntityProjectileHebluLight.isSentientParryWeaponStack(player.getHeldItemOffhand());
    }

    private static boolean isSentientParryWeaponStack(ItemStack stack) {
        if (stack == null || stack.isEmpty() || stack.getItem() == null) {
            return false;
        }
        ResourceLocation id = RegistryNames.get(stack.getItem());
        if (id == null || !"srparasites".equals(id.getResourceDomain())) {
            return false;
        }
        String path = id.getResourcePath();
        return "weapon_scythe_sentient".equals(path) || "weapon_axe_sentient".equals(path) || "weapon_sword_sentient".equals(path) || "weapon_cleaver_sentient".equals(path) || "weapon_maul_sentient".equals(path) || "weapon_lance_sentient".equals(path);
    }

    private void updateSyncedRenderState() {
        if (this.worldObj.isRemote) {
            return;
        }
        EntityDataManager.of(this).set(SYNC_PARRIED, (Object)this.parried);
        if (this.parried || this.target == null || this.target.isDead || !(this.target instanceof EntityPlayer)) {
            EntityDataManager.of(this).set(SYNC_DANGER, (Object)Float.valueOf(0.0f));
            return;
        }
        double dist = this.getDistanceToEntity((Entity)this.target);
        float danger = dist >= 18.0 ? 0.0f : (dist <= 5.0 ? 1.0f : (float)((18.0 - dist) / 13.0));
        EntityDataManager.of(this).set(SYNC_DANGER, (Object)Float.valueOf(danger));
    }

    private EntityLivingBase findFallbackPlayerTarget(double x, double y, double z) {
        EntityPlayer best = null;
        double bestDist = Double.MAX_VALUE;
        for (EntityPlayer player : EntityQueries.players(this.worldObj)) {
            double dist;
            if (player == null || player.isDead || player.isSpectator() || EntityProjectileHebluLight.shouldIgnorePlayer(player) || !((dist = player.getDistanceSq(x, y, z)) < bestDist)) continue;
            bestDist = dist;
            best = player;
        }
        return best;
    }

    public static class DamageSourceHebluLightNeutral
    extends DamageSource {
        public DamageSourceHebluLightNeutral() {
            super("hebluLightNeutral");
            this.setMagicDamage();
        }
    }

    public static class DamageSourceHebluLight
    extends DamageSource {
        private final Entity projectile;
        private final Entity owner;

        public DamageSourceHebluLight(Entity projectile, Entity owner) {
            super("hebluLight");
            this.projectile = projectile;
            this.owner = owner;
            this.setMagicDamage();
        }

        public Entity getSourceOfDamage() {
            return this.projectile;
        }

        public Entity getEntity() {
            return this.owner;
        }
    }
}

