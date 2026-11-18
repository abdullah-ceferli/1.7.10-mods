/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster.derived;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.client.SRPClientParticles;
import com.dhanantry.scapeandrunparasites.entity.EntityOrbVoid;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIKirinBlink;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDerived;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileKirinSlash;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import com.dhanantry.scapeandrunparasites.backport.particle.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class EntityKirin
extends EntityPDerived {
    private int voidCool;
    private int voidCoolTotal;
    private int floatBob;
    private final ArrayList<KirinPendingSlash> pendingJudgementCuts = new ArrayList();
    private static final int FLOAT_GROUND_SCAN = 24;
    private static final double FLOAT_HOVER_HEIGHT = 0.35;
    private static final double FLOAT_BOB_AMPLITUDE = 0.06;
    private int noGroundTicks = 0;
    private static final double FLOAT_UP_MAX = 0.16;
    private static final double FLOAT_DOWN_MAX = -0.16;
    private static final DataParameter<BlockPos> BLINK_POS = EntityDataManager.createKey(EntityKirin.class, (DataSerializer)DataSerializers.BLOCK_POS);
    private static final DataParameter<Integer> BLINK_TICKS = EntityDataManager.createKey(EntityKirin.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> JUDGEMENT_CUT_CHARGE_TICKS = EntityDataManager.createKey(EntityKirin.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> JUDGEMENT_CUT_AURA_END_TICKS = EntityDataManager.createKey(EntityKirin.class, (DataSerializer)DataSerializers.VARINT);
    private int limit;
    private int border;
    private boolean skillSummon;
    private boolean skillJudgementCut;
    private int judgementCutSkillTicks;
    private boolean judgementCutQueued;

    public EntityKirin(World worldIn) {
        super(worldIn);
        this.setSize(2.1271334f, 8.85f);
        this.stepHeight = 1.0f;
        this.tasks.removeTask((EntityAIBase)this.folow);
        this.voidCool = this.voidCoolTotal = 5;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(BLINK_POS, (Object)BlockPos.ORIGIN);
        EntityDataManager.of(this).register(BLINK_TICKS, (Object)0);
        EntityDataManager.of(this).register(JUDGEMENT_CUT_CHARGE_TICKS, (Object)0);
        EntityDataManager.of(this).register(JUDGEMENT_CUT_AURA_END_TICKS, (Object)0);
    }

    public void setBlinkCharge(BlockPos pos, int ticks) {
        if (pos == null) {
            pos = BlockPos.ORIGIN;
        }
        EntityDataManager.of(this).set(BLINK_POS, (Object)pos);
        EntityDataManager.of(this).set(BLINK_TICKS, (Object)Math.max(0, ticks));
    }

    public void clearBlinkCharge() {
        EntityDataManager.of(this).set(BLINK_POS, (Object)BlockPos.ORIGIN);
        EntityDataManager.of(this).set(BLINK_TICKS, (Object)0);
    }

    public void setJudgementCutChargeTicks(int ticks) {
        EntityDataManager.of(this).set(JUDGEMENT_CUT_CHARGE_TICKS, (Object)Math.max(0, ticks));
    }

    public int getJudgementCutChargeTicks() {
        return (Integer)EntityDataManager.of(this).get(JUDGEMENT_CUT_CHARGE_TICKS);
    }

    public void setJudgementCutAuraEndTicks(int ticks) {
        EntityDataManager.of(this).set(JUDGEMENT_CUT_AURA_END_TICKS, (Object)Math.max(0, ticks));
    }

    public int getJudgementCutAuraEndTicks() {
        return (Integer)EntityDataManager.of(this).get(JUDGEMENT_CUT_AURA_END_TICKS);
    }

    public boolean isChargingJudgementCut() {
        return this.getJudgementCutChargeTicks() > 0 || this.getJudgementCutAuraEndTicks() > 0;
    }

    protected String getLivingSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.KIRIN_GROWL;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        if (this.rand.nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.KIRIN_HURT;
    }

    protected String getDeathSound() {
        return SRPSounds.KIRIN_DEATH;
    }

    public void onUpdate() {
        BlockPos p;
        int ticks;
        super.onUpdate();
        if (this.worldObj.isRemote && (ticks = ((Integer)EntityDataManager.of(this).get(BLINK_TICKS)).intValue()) > 0 && (p = (BlockPos)EntityDataManager.of(this).get(BLINK_POS)) != null && !Objects.equals(p, BlockPos.ORIGIN)) {
            float t = 1.0f - (float)ticks / 60.0f;
            double y = (double)p.getY() + 1.5 + (double)t;
            double cx = (double)p.getX() + 0.5;
            double cz = (double)p.getZ() + 0.5;
            float sizeBig = 6.0f;
            float sizeSmall = 5.5f;
            double base = 0.35;
            double accel = 1.25;
            double spd = base + (double)t * accel;
            float angSmall = (float)((double)this.ticksExisted * spd % (Math.PI * 2));
            float angBig = (float)((double)this.ticksExisted * -spd % (Math.PI * 2));
            SRPClientParticles.spawnKirinWarning(this.worldObj, cx, y, cz, sizeSmall, angSmall, 1);
            SRPClientParticles.spawnKirinWarning(this.worldObj, cx, y, cz, sizeBig, angBig, 1);
        }
    }

    @Override
    public int getParasiteIDRegister() {
        return 67;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(0, (EntityAIBase)new EntityAISwimmingDiving((EntityLiving)this, 0.08));
        this.tasks.addTask(2, (EntityAIBase)new EntityAIKirinBlink(this));
        this.tasks.addTask(3, (EntityAIBase)new EntityAIAttackMeleeStatus(this, 1.0, true, 0.0));
        this.tasks.addTask(2, (EntityAIBase)new EntityAISkill(this, 80, 35, true, 1));
        this.tasks.addTask(2, (EntityAIBase)new EntityAISkill(this, 80, 35, true, 2));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.KIRIN_HEALTH);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.KIRIN_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.24);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(SRPAttributes.KIRIN_KD_RESISTANCE);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.KIRIN_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.derivedFollow);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote) {
            int auraEndTicks;
            this.updateFloating();
            this.updatePendingJudgementCuts();
            int chargeTicks = this.getJudgementCutChargeTicks();
            if (chargeTicks > 0) {
                this.setJudgementCutChargeTicks(chargeTicks - 1);
                if (chargeTicks - 1 <= 0) {
                    this.setJudgementCutAuraEndTicks(24);
                }
            }
            if ((auraEndTicks = this.getJudgementCutAuraEndTicks()) > 0) {
                this.setJudgementCutAuraEndTicks(auraEndTicks - 1);
            }
        }
        if (this.worldObj.isRemote) {
            for (int i = 0; i < 4; ++i) {
                com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(this.worldObj, EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5) * ((double)this.width * 3.0), this.posY + this.rand.nextDouble() * (double)this.height - 0.25, this.posZ + (this.rand.nextDouble() - 0.5) * ((double)this.width * 3.0), (this.rand.nextDouble() - 0.5) * 2.0, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5) * 2.0, new int[0]);
            }
            if (this.isChargingJudgementCut()) {
                this.spawnJudgementCutChargeParticles();
            }
        }
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        boolean flag = super.attackEntityFrom(source, amount);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        boolean flag = super.attackEntityAsMob(entityIn);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    public float getEyeHeight() {
        return 5.7f;
    }

    @Override
    public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
    }

    protected float getSoundVolume() {
        return 5.0f;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
    }

    @Override
    protected EntityPCosmical getThis() {
        return new EntityKirin(this.worldObj);
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 1: {
                return this.skillSummon;
            }
            case 2: {
                return this.skillJudgementCut;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 1: {
                this.skillSummon = in;
                return;
            }
            case 2: {
                this.skillJudgementCut = in;
                if (!in) {
                    this.judgementCutSkillTicks = 0;
                    this.judgementCutQueued = false;
                }
                return;
            }
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 1: {
                this.summon();
                return;
            }
            case 2: {
                this.summonJudgementCutsOnly();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void summonJudgementCutsOnly() {
        this.motionY = 0.0;
        this.posY = this.prevPosY;
        this.setParasiteStatus(30);
        this.getNavigator().clearPathEntity();
        EntityLivingBase target = this.getAttackTarget();
        if (this.getCloneC() || target == null || !target.isEntityAlive()) {
            this.skillJudgementCut = true;
            this.setParasiteStatus(0);
            this.judgementCutSkillTicks = 0;
            this.judgementCutQueued = false;
            return;
        }
        if (!this.judgementCutQueued) {
            this.spawnJudgementCuts(target);
            this.judgementCutQueued = true;
        }
        ++this.judgementCutSkillTicks;
        if (this.judgementCutSkillTicks > 80) {
            this.skillJudgementCut = true;
            this.setParasiteStatus(0);
            this.judgementCutSkillTicks = 0;
            this.judgementCutQueued = false;
        }
    }

    public void spawnJudgementCuts(EntityLivingBase target) {
        if (this.worldObj.isRemote || target == null || target.isDead) {
            return;
        }
        com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this.worldObj, null, this.posX, this.posY + (double)this.height * 0.55, this.posZ, SRPSounds.KIRIN_PROJECTILE_CHARGE, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.HOSTILE, 4.0f, 0.95f + this.rand.nextFloat() * 0.08f);
        int chargeDelayTicks = 60;
        this.setJudgementCutChargeTicks(chargeDelayTicks);
        this.setJudgementCutAuraEndTicks(0);
        boolean targetIsPlayer = target instanceof EntityPlayer;
        int count = 42;
        float damage = targetIsPlayer ? 8.0f : 10.0f;
        for (int i = 0; i < count; ++i) {
            double verticalOffset;
            float heightRoll;
            double sideOffset;
            float closeRoll;
            double passAngle = this.rand.nextDouble() * Math.PI * 2.0;
            double dirX = Math.cos(passAngle);
            double dirZ = Math.sin(passAngle);
            double beforeTarget = 22.0 + this.rand.nextDouble() * 16.0;
            if (targetIsPlayer) {
                closeRoll = this.rand.nextFloat();
                sideOffset = closeRoll < 0.55f ? this.getRandomSignedRange(2.4, 5.2) : (closeRoll < 0.88f ? this.getRandomSignedRange(5.5, 10.0) : this.getRandomSignedRange(10.0, 22.0));
                heightRoll = this.rand.nextFloat();
                verticalOffset = heightRoll < 0.62f ? (this.rand.nextDouble() - 0.5) * 2.2 : (heightRoll < 0.9f ? (this.rand.nextDouble() - 0.5) * 5.0 : (this.rand.nextDouble() - 0.5) * 9.0);
            } else {
                closeRoll = this.rand.nextFloat();
                sideOffset = closeRoll < 0.7f ? (this.rand.nextDouble() - 0.5) * 1.2 : (closeRoll < 0.92f ? (this.rand.nextDouble() - 0.5) * 3.0 : this.getRandomSignedRange(4.0, 8.0));
                heightRoll = this.rand.nextFloat();
                verticalOffset = heightRoll < 0.75f ? (this.rand.nextDouble() - 0.5) * Math.max(1.0, (double)target.height * 0.45) : (heightRoll < 0.94f ? (this.rand.nextDouble() - 0.5) * Math.max(2.0, (double)target.height * 0.8) : (this.rand.nextDouble() - 0.5) * Math.max(3.0, (double)target.height * 1.2));
            }
            float yaw = (float)(Math.atan2(dirX, dirZ) * 180.0 / Math.PI);
            float pitch = targetIsPlayer ? (this.rand.nextFloat() < 0.1f ? -60.0f + this.rand.nextFloat() * 120.0f : -14.0f + this.rand.nextFloat() * 28.0f) : (this.rand.nextFloat() < 0.08f ? -35.0f + this.rand.nextFloat() * 70.0f : -8.0f + this.rand.nextFloat() * 16.0f);
            float roll = (float)(this.rand.nextDouble() * 360.0);
            float length = 110.0f + this.rand.nextFloat() * 75.0f;
            int delayTicks = chargeDelayTicks + i + this.rand.nextInt(5);
            int growTicks = 4 + this.rand.nextInt(8);
            int lifeTicks = 55 + this.rand.nextInt(18);
            this.pendingJudgementCuts.add(new KirinPendingSlash(target.getEntityId(), delayTicks, dirX, dirZ, beforeTarget, sideOffset, verticalOffset, yaw, pitch, roll, length, damage, growTicks, lifeTicks));
        }
    }

    private void updatePendingJudgementCuts() {
        if (this.worldObj.isRemote || this.pendingJudgementCuts.isEmpty()) {
            return;
        }
        Iterator<KirinPendingSlash> iterator = this.pendingJudgementCuts.iterator();
        while (iterator.hasNext()) {
            KirinPendingSlash slash = iterator.next();
            --slash.delay;
            if (slash.delay > 0) continue;
            Entity entity = this.worldObj.getEntityByID(slash.targetId);
            if (!(entity instanceof EntityLivingBase) || entity.isDead) {
                iterator.remove();
                continue;
            }
            EntityLivingBase target = (EntityLivingBase)entity;
            double cx = target.posX;
            double cy = Boxes.entityBox(target).minY + (double)target.height * 0.55;
            double cz = target.posZ;
            double sideX = -slash.dirZ;
            double sideZ = slash.dirX;
            double x = cx - slash.dirX * slash.beforeTarget + sideX * slash.sideOffset;
            double y = cy + slash.verticalOffset;
            double z = cz - slash.dirZ * slash.beforeTarget + sideZ * slash.sideOffset;
            EntityProjectileKirinSlash entitySlash = new EntityProjectileKirinSlash(this.worldObj, (EntityLivingBase)this, x, y, z, slash.yaw, slash.pitch, slash.roll, slash.length, slash.damage, 0, slash.growTicks, slash.lifeTicks);
            this.worldObj.spawnEntityInWorld((Entity)entitySlash);
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this.worldObj, null, x, y, z, SRPSounds.KIRIN_PROJECTILE_SUMMON, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.HOSTILE, 0.85f, 0.9f + this.rand.nextFloat() * 0.25f);
            iterator.remove();
        }
    }

    private void spawnJudgementCutChargeParticles() {
        int chargeTicks = this.getJudgementCutChargeTicks();
        float progress = 1.0f - Math.max(0.0f, Math.min((float)chargeTicks / 60.0f, 1.0f));
        int count = 4 + (int)(progress * 6.0f);
        double targetX = this.posX;
        double targetY = this.posY + (double)this.height * 0.58;
        double targetZ = this.posZ;
        for (int i = 0; i < count; ++i) {
            double angle = this.rand.nextDouble() * Math.PI * 2.0;
            double outerRadius = 12.0 + this.rand.nextDouble() * 8.0;
            double innerRadius = 2.0 + this.rand.nextDouble() * 2.0;
            double radius = outerRadius + (innerRadius - outerRadius) * (double)progress;
            double spawnX = this.posX + Math.cos(angle) * radius;
            double spawnY = this.posY + 0.5 + this.rand.nextDouble() * ((double)this.height + 3.0);
            double spawnZ = this.posZ + Math.sin(angle) * radius;
            com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(this.worldObj, EnumParticleTypes.REDSTONE, spawnX, spawnY, spawnZ, 1.0, 1.0, 1.0, new int[0]);
        }
        if (this.rand.nextInt(2) == 0) {
            com.dhanantry.scapeandrunparasites.backport.particle.ParticleAccess.spawn(this.worldObj, EnumParticleTypes.REDSTONE, targetX + (this.rand.nextDouble() - 0.5) * ((double)this.width * 1.6), targetY + (this.rand.nextDouble() - 0.5) * 1.4, targetZ + (this.rand.nextDouble() - 0.5) * ((double)this.width * 1.6), 1.0, 1.0, 1.0, new int[0]);
        }
    }

    private void summon() {
        this.motionY = 0.0;
        this.posY = this.prevPosY;
        this.setParasiteStatus(30);
        this.getNavigator().clearPathEntity();
        if (this.ticksExisted % 20 != 0) {
            return;
        }
        ++this.border;
        this.worldObj.setEntityState((Entity)this, (byte)100);
        if (this.getCloneC() || this.getAttackTarget() == null) {
            this.skillSummon = true;
            this.setParasiteStatus(0);
            this.border = 0;
            this.limit = 0;
        } else if (!this.getAttackTarget().isEntityAlive()) {
            ++this.border;
        }
        if (this.border == 3) {
            EntityOrbVoid orb = new EntityOrbVoid(this.worldObj, this, 8, 80, true);
            orb.copyLocationAndAnglesFrom((Entity)this);
            double off = 10.0;
            orb.posY += (double)this.height + off;
            orb.offsetOrb = off;
            this.worldObj.spawnEntityInWorld((Entity)orb);
            com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this, SRPSounds.KIRIN_SHOOT, this.getSoundVolume() * 2.0f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
            this.spawnJudgementCuts(this.getAttackTarget());
        }
        if (this.border > 12) {
            this.skillSummon = true;
            this.setParasiteStatus(0);
            this.border = 0;
            this.limit = 0;
        }
    }

    private double getRandomSignedRange(double min, double max) {
        double value = min + this.rand.nextDouble() * (max - min);
        return this.rand.nextBoolean() ? value : -value;
    }

    private void updateFloating() {
        this.fallDistance = 0.0f;
        ++this.floatBob;
        double bob = Math.sin((double)(this.ticksExisted + this.floatBob) * 0.12) * 0.06;
        BlockPos base = new BlockPos(this.posX, this.posY + 0.1, this.posZ);
        BlockPos ground = null;
        for (int i = 0; i <= 24; ++i) {
            BlockPos p = base.down(i);
            if (!BlockStates.get(this.worldObj, p).isFullCube()) continue;
            ground = p;
            break;
        }
        if (ground == null) {
            this.motionY = 0.0;
            ++this.noGroundTicks;
            if (!this.worldObj.isRemote && this.noGroundTicks >= 40 && EntityAIKirinBlink.tryBlinkToNearbyLand(this, 48, 20)) {
                this.noGroundTicks = 0;
            }
            return;
        }
        this.noGroundTicks = 0;
        double targetY = (double)ground.getY() + 1.0 + 0.35 + bob;
        double dy = targetY - this.posY;
        double accel = dy > 0.0 ? dy * 0.12 : dy * 0.06;
        this.motionY += accel;
        if (this.motionY > 0.16) {
            this.motionY = 0.16;
        }
        if (this.motionY < -0.16) {
            this.motionY = -0.16;
        }
    }

    private static class KirinPendingSlash {
        int targetId;
        int delay;
        double dirX;
        double dirZ;
        double beforeTarget;
        double sideOffset;
        double verticalOffset;
        float yaw;
        float pitch;
        float roll;
        float length;
        float damage;
        int growTicks;
        int lifeTicks;

        KirinPendingSlash(int targetId, int delay, double dirX, double dirZ, double beforeTarget, double sideOffset, double verticalOffset, float yaw, float pitch, float roll, float length, float damage, int growTicks, int lifeTicks) {
            this.targetId = targetId;
            this.delay = delay;
            this.dirX = dirX;
            this.dirZ = dirZ;
            this.beforeTarget = beforeTarget;
            this.sideOffset = sideOffset;
            this.verticalOffset = verticalOffset;
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
            this.length = length;
            this.damage = damage;
            this.growTicks = growTicks;
            this.lifeTicks = lifeTicks;
        }
    }
}

