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
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 */
package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityProjectileKirinSlash
extends Entity {
    private EntityLivingBase owner;
    private static final DataParameter<Float> SYNC_YAW = EntityDataManager.createKey(EntityProjectileKirinSlash.class, (DataSerializer)DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_PITCH = EntityDataManager.createKey(EntityProjectileKirinSlash.class, (DataSerializer)DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_ROLL = EntityDataManager.createKey(EntityProjectileKirinSlash.class, (DataSerializer)DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_LENGTH = EntityDataManager.createKey(EntityProjectileKirinSlash.class, (DataSerializer)DataSerializers.FLOAT);
    private static final DataParameter<Integer> SYNC_DELAY = EntityDataManager.createKey(EntityProjectileKirinSlash.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> SYNC_GROW_TICKS = EntityDataManager.createKey(EntityProjectileKirinSlash.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> SYNC_LIFE = EntityDataManager.createKey(EntityProjectileKirinSlash.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Boolean> SYNC_FADING = EntityDataManager.createKey(EntityProjectileKirinSlash.class, (DataSerializer)DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SYNC_HIT_POP = EntityDataManager.createKey(EntityProjectileKirinSlash.class, (DataSerializer)DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> SYNC_HIT_POP_AGE = EntityDataManager.createKey(EntityProjectileKirinSlash.class, (DataSerializer)DataSerializers.VARINT);
    private int age;
    private int delayTicks = 0;
    private int growTicks = 5;
    private int life = 60;
    private float damage = 12.0f;
    private float maxLength = 80.0f;
    private int targetEntityId = -1;
    private double dirX;
    private double dirZ;
    private double beforeTarget;
    private double sideOffset;
    private double verticalOffset;
    private boolean startPositionFixed = false;
    private double startX;
    private double startY;
    private double startZ;
    private boolean fading = false;
    private int fadeAge = 0;
    private int fadeTicks = 10;
    private boolean hitPop = false;
    private int hitPopAge = 0;
    private int hitPopTicks = 6;
    private final Set<Integer> hitEntities = new HashSet<Integer>();

    public EntityProjectileKirinSlash(World worldIn) {
        super(worldIn);
        this.setSize(0.25f, 0.25f);
        this.noClip = true;
        this.ignoreFrustumCheck = true;
    }

    public EntityProjectileKirinSlash(World worldIn, EntityLivingBase ownerIn, double x, double y, double z, float yaw, float pitch, float roll, float length, float damageIn) {
        this(worldIn, ownerIn, x, y, z, yaw, pitch, roll, length, damageIn, 0, 5, 60);
    }

    public EntityProjectileKirinSlash(World worldIn, EntityLivingBase ownerIn, double x, double y, double z, float yaw, float pitch, float roll, float length, float damageIn, int delayTicksIn, int growTicksIn, int lifeIn) {
        this(worldIn);
        this.owner = ownerIn;
        this.damage = damageIn;
        this.maxLength = length;
        this.delayTicks = Math.max(0, delayTicksIn);
        this.growTicks = Math.max(1, growTicksIn);
        this.life = Math.max(10, lifeIn);
        this.startX = x;
        this.startY = y;
        this.startZ = z;
        this.startPositionFixed = true;
        this.setPosition(x, y, z);
        EntityDataManager.of(this).set(SYNC_YAW, (Object)Float.valueOf(yaw));
        EntityDataManager.of(this).set(SYNC_PITCH, (Object)Float.valueOf(pitch));
        EntityDataManager.of(this).set(SYNC_ROLL, (Object)Float.valueOf(roll));
        EntityDataManager.of(this).set(SYNC_LENGTH, (Object)Float.valueOf(length));
        EntityDataManager.of(this).set(SYNC_DELAY, (Object)this.delayTicks);
        EntityDataManager.of(this).set(SYNC_GROW_TICKS, (Object)this.growTicks);
        EntityDataManager.of(this).set(SYNC_LIFE, (Object)this.life);
        EntityDataManager.of(this).set(SYNC_FADING, (Object)false);
        EntityDataManager.of(this).set(SYNC_HIT_POP, (Object)false);
        EntityDataManager.of(this).set(SYNC_HIT_POP_AGE, (Object)0);
    }

    public EntityProjectileKirinSlash(World worldIn, EntityLivingBase ownerIn, EntityLivingBase targetIn, double dirXIn, double dirZIn, double beforeTargetIn, double sideOffsetIn, double verticalOffsetIn, float yaw, float pitch, float roll, float length, float damageIn, int delayTicksIn, int growTicksIn, int lifeIn) {
        this(worldIn);
        this.owner = ownerIn;
        if (targetIn != null) {
            this.targetEntityId = targetIn.getEntityId();
        }
        this.dirX = dirXIn;
        this.dirZ = dirZIn;
        this.beforeTarget = beforeTargetIn;
        this.sideOffset = sideOffsetIn;
        this.verticalOffset = verticalOffsetIn;
        this.damage = damageIn;
        this.maxLength = length;
        this.delayTicks = Math.max(0, delayTicksIn);
        this.growTicks = Math.max(1, growTicksIn);
        this.life = Math.max(10, lifeIn);
        this.updateStartFromTarget(true);
        EntityDataManager.of(this).set(SYNC_YAW, (Object)Float.valueOf(yaw));
        EntityDataManager.of(this).set(SYNC_PITCH, (Object)Float.valueOf(pitch));
        EntityDataManager.of(this).set(SYNC_ROLL, (Object)Float.valueOf(roll));
        EntityDataManager.of(this).set(SYNC_LENGTH, (Object)Float.valueOf(length));
        EntityDataManager.of(this).set(SYNC_DELAY, (Object)this.delayTicks);
        EntityDataManager.of(this).set(SYNC_GROW_TICKS, (Object)this.growTicks);
        EntityDataManager.of(this).set(SYNC_LIFE, (Object)this.life);
        EntityDataManager.of(this).set(SYNC_FADING, (Object)false);
        EntityDataManager.of(this).set(SYNC_HIT_POP, (Object)false);
        EntityDataManager.of(this).set(SYNC_HIT_POP_AGE, (Object)0);
    }

    protected void entityInit() {
        EntityDataManager.of(this).register(SYNC_YAW, (Object)Float.valueOf(0.0f));
        EntityDataManager.of(this).register(SYNC_PITCH, (Object)Float.valueOf(0.0f));
        EntityDataManager.of(this).register(SYNC_ROLL, (Object)Float.valueOf(0.0f));
        EntityDataManager.of(this).register(SYNC_LENGTH, (Object)Float.valueOf(80.0f));
        EntityDataManager.of(this).register(SYNC_DELAY, (Object)0);
        EntityDataManager.of(this).register(SYNC_GROW_TICKS, (Object)5);
        EntityDataManager.of(this).register(SYNC_LIFE, (Object)60);
        EntityDataManager.of(this).register(SYNC_FADING, (Object)false);
        EntityDataManager.of(this).register(SYNC_HIT_POP, (Object)false);
        EntityDataManager.of(this).register(SYNC_HIT_POP_AGE, (Object)0);
    }

    public void onUpdate() {
        super.onUpdate();
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        ++this.age;
        if (this.getVisibleAge() < 0 && this.targetEntityId != -1) {
            this.updateStartFromTarget(false);
        } else if (!this.startPositionFixed) {
            this.startX = this.posX;
            this.startY = this.posY;
            this.startZ = this.posZ;
            this.startPositionFixed = true;
        }
        this.setPosition(this.startX, this.startY, this.startZ);
        if (this.hitPop) {
            ++this.hitPopAge;
            if (!this.worldObj.isRemote) {
                EntityDataManager.of(this).set(SYNC_HIT_POP, (Object)true);
                EntityDataManager.of(this).set(SYNC_HIT_POP_AGE, (Object)this.hitPopAge);
            }
            if (this.hitPopAge >= this.hitPopTicks) {
                this.setDead();
                return;
            }
        }
        if (this.fading) {
            ++this.fadeAge;
            if (!this.worldObj.isRemote) {
                EntityDataManager.of(this).set(SYNC_FADING, (Object)true);
            }
            if (this.fadeAge >= this.fadeTicks) {
                this.setDead();
                return;
            }
        }
        if (!this.worldObj.isRemote) {
            if (this.owner == null || this.owner.isDead) {
                this.setDead();
                return;
            }
            if (!this.fading && !this.hitPop && this.getVisibleAge() > this.life) {
                this.setDead();
                return;
            }
            if (!this.fading && !this.hitPop && this.getVisibleAge() >= 0) {
                this.checkEntityImpact();
            }
        }
    }

    private void updateStartFromTarget(boolean forcePosition) {
        Entity entity = this.worldObj.getEntityByID(this.targetEntityId);
        if (!(entity instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase target = (EntityLivingBase)entity;
        double cx = target.posX;
        double cy = Boxes.entityBox(target).minY + (double)target.height * 0.55;
        double cz = target.posZ;
        double sideX = -this.dirZ;
        double sideZ = this.dirX;
        this.startX = cx - this.dirX * this.beforeTarget + sideX * this.sideOffset;
        this.startY = cy + this.verticalOffset;
        this.startZ = cz - this.dirZ * this.beforeTarget + sideZ * this.sideOffset;
        this.startPositionFixed = true;
        if (forcePosition) {
            this.setPosition(this.startX, this.startY, this.startZ);
        }
    }

    private int getVisibleAge() {
        return this.age - this.delayTicks;
    }

    public float getGrowth(float partialTicks) {
        float visibleAge = (float)this.ticksExisted + partialTicks - (float)this.getDelayTicks();
        if (visibleAge <= 0.0f) {
            return 0.0f;
        }
        float raw = MathHelper.clamp_float((float)(visibleAge / (float)this.getGrowTicks()), (float)0.0f, (float)1.0f);
        return 1.0f - (1.0f - raw) * (1.0f - raw);
    }

    private float getServerGrowth() {
        int visibleAge = this.getVisibleAge();
        if (visibleAge <= 0) {
            return 0.0f;
        }
        float raw = MathHelper.clamp_float((float)((float)visibleAge / (float)this.growTicks), (float)0.0f, (float)1.0f);
        return 1.0f - (1.0f - raw) * (1.0f - raw);
    }

    private void checkEntityImpact() {
        float growth = this.getServerGrowth();
        if (growth <= 0.0f) {
            return;
        }
        float currentLength = Math.max(1.0f, this.maxLength * growth);
        AxisAlignedBB searchBox = Boxes.grow(Boxes.entityBox(this), (double)currentLength, Math.max(4.0, (double)currentLength * 0.25), (double)currentLength);
        List list = EntityQueries.entities(this.worldObj, EntityLivingBase.class, searchBox);
        Vec3d start = new Vec3d(this.startX, this.startY, this.startZ);
        Vec3d dir = this.getSlashDirection();
        Vec3d a = start;
        Vec3d b = start.add(dir.scale((double)currentLength));
        for (EntityLivingBase living : list) {
            AxisAlignedBB hitBox;
            EntityPlayer player;
            if (living == null || living.isDead || living == this.owner || this.hitEntities.contains(living.getEntityId()) || EntityProjectileKirinSlash.isSRParasitesMob(living) || living instanceof EntityPlayer && (player = (EntityPlayer)living).isSpectator() || Boxes.calculateIntercept((hitBox = Boxes.grow(Boxes.entityBox(living), 0.35, 0.25, 0.35)), a, b) == null && !Boxes.contains(hitBox, a) && !Boxes.contains(hitBox, b)) continue;
            this.onSliceTouched(living);
            return;
        }
    }

    private void onSliceTouched(EntityLivingBase living) {
        EntityPlayer player;
        if (living == null || living.isDead) {
            return;
        }
        if (this.hitEntities.contains(living.getEntityId())) {
            return;
        }
        if (living instanceof EntityPlayer && (player = (EntityPlayer)living).isSpectator()) {
            return;
        }
        this.hitEntities.add(living.getEntityId());
        this.spawnContactSmoke(living);
        this.worldObj.playSound(null, living.posX, living.posY + (double)living.height * 0.5, living.posZ, SRPSounds.KIRIN_PROJECTILE_IMPACT, SoundCategory.HOSTILE, 0.45f, 0.92f + this.rand.nextFloat() * 0.16f);
        if (living instanceof EntityPlayer) {
            player = (EntityPlayer)living;
            if (!player.isCreative()) {
                this.forceExactHealthDamage((EntityLivingBase)player, 2.0f);
            }
        } else {
            living.attackEntityFrom((DamageSource)new DamageSourceKirinSlash(this, (Entity)this.owner), this.damage);
        }
        this.beginHitPop();
    }

    private void forceExactHealthDamage(EntityLivingBase living, float amount) {
        if (living == null || living.isDead || amount <= 0.0f) {
            return;
        }
        float newHealth = Math.max(0.0f, living.getHealth() - amount);
        living.setHealth(newHealth);
        if (newHealth <= 0.0f) {
            living.onDeath((DamageSource)new DamageSourceKirinSlash(this, (Entity)this.owner));
        }
    }

    private void spawnContactSmoke(EntityLivingBase living) {
        if (this.worldObj.isRemote || !(this.worldObj instanceof WorldServer) || living == null) {
            return;
        }
        WorldServer worldServer = (WorldServer)this.worldObj;
        double x = living.posX;
        double y = Boxes.entityBox(living).minY + 0.05;
        double z = living.posZ;
        worldServer.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, x, y, z, 4, 0.25, 0.05, 0.25, 0.012, new int[0]);
        worldServer.spawnParticle(EnumParticleTypes.CLOUD, true, x, y, z, 2, 0.18, 0.03, 0.18, 0.006, new int[0]);
    }

    private void beginHitPop() {
        if (this.hitPop) {
            return;
        }
        this.hitPop = true;
        this.hitPopAge = 0;
        this.fading = false;
        if (!this.worldObj.isRemote) {
            EntityDataManager.of(this).set(SYNC_HIT_POP, (Object)true);
            EntityDataManager.of(this).set(SYNC_HIT_POP_AGE, (Object)0);
            EntityDataManager.of(this).set(SYNC_FADING, (Object)false);
        }
    }

    private Vec3d getSlashDirection() {
        double z;
        double y;
        float yawRad = this.getSlashYaw() * ((float)Math.PI / 180);
        float pitchRad = this.getSlashPitch() * ((float)Math.PI / 180);
        double x = MathHelper.sin((float)yawRad) * MathHelper.cos((float)pitchRad);
        Vec3d dir = new Vec3d(x, y = (double)(-MathHelper.sin((float)pitchRad)), z = (double)(MathHelper.cos((float)yawRad) * MathHelper.cos((float)pitchRad)));
        if (dir.lengthVector() < 1.0E-4) {
            return new Vec3d(0.0, 0.0, 1.0);
        }
        return dir.normalize();
    }

    private static double distancePointToSegmentSq(Vec3d p, Vec3d a, Vec3d b) {
        Vec3d ab = b.subtract(a);
        double abLenSq = ab.dotProduct(ab);
        if (abLenSq <= 1.0E-4) {
            return p.squareDistanceTo(a);
        }
        double t = p.subtract(a).dotProduct(ab) / abLenSq;
        t = MathHelper.clamp_double((double)t, (double)0.0, (double)1.0);
        Vec3d closest = a.add(ab.scale(t));
        return p.squareDistanceTo(closest);
    }

    private static boolean isSRParasitesMob(EntityLivingBase living) {
        String className = living.getClass().getName();
        return className.startsWith("com.dhanantry.scapeandrunparasites.entity.monster.");
    }

    public float getSlashYaw() {
        return ((Float)EntityDataManager.of(this).get(SYNC_YAW)).floatValue();
    }

    public float getSlashPitch() {
        return ((Float)EntityDataManager.of(this).get(SYNC_PITCH)).floatValue();
    }

    public float getSlashRoll() {
        return ((Float)EntityDataManager.of(this).get(SYNC_ROLL)).floatValue();
    }

    public float getSlashLength() {
        return ((Float)EntityDataManager.of(this).get(SYNC_LENGTH)).floatValue();
    }

    public int getDelayTicks() {
        return (Integer)EntityDataManager.of(this).get(SYNC_DELAY);
    }

    public int getGrowTicks() {
        return Math.max(1, (Integer)EntityDataManager.of(this).get(SYNC_GROW_TICKS));
    }

    public int getLife() {
        return Math.max(1, (Integer)EntityDataManager.of(this).get(SYNC_LIFE));
    }

    public boolean isFading() {
        return (Boolean)EntityDataManager.of(this).get(SYNC_FADING);
    }

    public boolean isHitPopping() {
        return (Boolean)EntityDataManager.of(this).get(SYNC_HIT_POP);
    }

    public int getHitPopAge() {
        return (Integer)EntityDataManager.of(this).get(SYNC_HIT_POP_AGE);
    }

    public int getHitPopTicks() {
        return this.hitPopTicks;
    }

    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.age = compound.getInteger("Age");
        this.life = compound.getInteger("Life");
        this.damage = compound.getFloat("Damage");
        this.maxLength = compound.getFloat("SlashLength");
        this.growTicks = compound.getInteger("GrowTicks");
        this.delayTicks = compound.getInteger("DelayTicks");
        this.startX = compound.getDouble("StartX");
        this.startY = compound.getDouble("StartY");
        this.startZ = compound.getDouble("StartZ");
        this.startPositionFixed = true;
        this.targetEntityId = compound.getInteger("TargetEntityId");
        this.dirX = compound.getDouble("DirX");
        this.dirZ = compound.getDouble("DirZ");
        this.beforeTarget = compound.getDouble("BeforeTarget");
        this.sideOffset = compound.getDouble("SideOffset");
        this.verticalOffset = compound.getDouble("VerticalOffset");
        this.fading = compound.getBoolean("Fading");
        this.fadeAge = compound.getInteger("FadeAge");
        this.hitPop = compound.getBoolean("HitPop");
        this.hitPopAge = compound.getInteger("HitPopAge");
        if (this.life <= 0) {
            this.life = 60;
        }
        if (this.growTicks <= 0) {
            this.growTicks = 5;
        }
        if (this.maxLength <= 0.0f) {
            this.maxLength = 80.0f;
        }
        EntityDataManager.of(this).set(SYNC_YAW, (Object)Float.valueOf(compound.getFloat("SlashYaw")));
        EntityDataManager.of(this).set(SYNC_PITCH, (Object)Float.valueOf(compound.getFloat("SlashPitch")));
        EntityDataManager.of(this).set(SYNC_ROLL, (Object)Float.valueOf(compound.getFloat("SlashRoll")));
        EntityDataManager.of(this).set(SYNC_LENGTH, (Object)Float.valueOf(this.maxLength));
        EntityDataManager.of(this).set(SYNC_DELAY, (Object)this.delayTicks);
        EntityDataManager.of(this).set(SYNC_GROW_TICKS, (Object)this.growTicks);
        EntityDataManager.of(this).set(SYNC_LIFE, (Object)this.life);
        EntityDataManager.of(this).set(SYNC_FADING, (Object)this.fading);
        EntityDataManager.of(this).set(SYNC_HIT_POP, (Object)this.hitPop);
        EntityDataManager.of(this).set(SYNC_HIT_POP_AGE, (Object)this.hitPopAge);
    }

    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("Age", this.age);
        compound.setInteger("Life", this.life);
        compound.setFloat("Damage", this.damage);
        compound.setFloat("SlashLength", this.maxLength);
        compound.setInteger("GrowTicks", this.growTicks);
        compound.setInteger("DelayTicks", this.delayTicks);
        compound.setDouble("StartX", this.startX);
        compound.setDouble("StartY", this.startY);
        compound.setDouble("StartZ", this.startZ);
        compound.setInteger("TargetEntityId", this.targetEntityId);
        compound.setDouble("DirX", this.dirX);
        compound.setDouble("DirZ", this.dirZ);
        compound.setDouble("BeforeTarget", this.beforeTarget);
        compound.setDouble("SideOffset", this.sideOffset);
        compound.setDouble("VerticalOffset", this.verticalOffset);
        compound.setBoolean("Fading", this.fading);
        compound.setInteger("FadeAge", this.fadeAge);
        compound.setBoolean("HitPop", this.hitPop);
        compound.setInteger("HitPopAge", this.hitPopAge);
        compound.setFloat("SlashYaw", this.getSlashYaw());
        compound.setFloat("SlashPitch", this.getSlashPitch());
        compound.setFloat("SlashRoll", this.getSlashRoll());
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public boolean canAttackWithItem() {
        return false;
    }

    public static class DamageSourceKirinSlash
    extends DamageSource {
        private final Entity slash;
        private final Entity owner;

        public DamageSourceKirinSlash(Entity slashIn, Entity ownerIn) {
            super("kirinSlash");
            this.slash = slashIn;
            this.owner = ownerIn;
            this.setMagicDamage();
        }

        public Entity getSourceOfDamage() {
            return this.slash;
        }

        public Entity getEntity() {
            return this.owner;
        }
    }
}

