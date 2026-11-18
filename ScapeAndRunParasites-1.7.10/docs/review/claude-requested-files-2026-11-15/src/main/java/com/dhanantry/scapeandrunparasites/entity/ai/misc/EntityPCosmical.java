/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.datasync.DataParameter
 *  net.minecraft.network.datasync.DataSerializer
 *  net.minecraft.network.datasync.DataSerializers
 *  net.minecraft.network.datasync.EntityDataManager
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.EnumSkyBlock
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.EntityOrbScary;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataParameter;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializer;
import com.dhanantry.scapeandrunparasites.backport.datasync.DataSerializers;
import com.dhanantry.scapeandrunparasites.backport.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class EntityPCosmical
extends EntityPMalleable {
    private static final DataParameter<Boolean> SHADOW = EntityDataManager.createKey(EntityPCosmical.class, (DataSerializer)DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CLONE = EntityDataManager.createKey(EntityPCosmical.class, (DataSerializer)DataSerializers.BOOLEAN);
    protected int cloneLife;
    protected float hackHeal;
    protected String[] hackEffects;
    private ArrayList<DataParameter<Integer>> victims = new ArrayList();
    private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey(EntityPCosmical.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_ENTITY1 = EntityDataManager.createKey(EntityPCosmical.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_ENTITY2 = EntityDataManager.createKey(EntityPCosmical.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_ENTITY3 = EntityDataManager.createKey(EntityPCosmical.class, (DataSerializer)DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET_ENTITY4 = EntityDataManager.createKey(EntityPCosmical.class, (DataSerializer)DataSerializers.VARINT);
    public int limitClones;
    protected int limitClonesLife;
    protected int limitClonesCooldown;
    protected float shadowDamage;
    protected int shadowDamageTick;
    public float shadowDamageR;
    public int shadowDamageRCooldown;
    protected int shakeee;
    protected int showC;
    private boolean skillHack;
    protected int borderHack;

    public EntityPCosmical(World worldIn) {
        super(worldIn);
        this.victims.add(TARGET_ENTITY);
        this.victims.add(TARGET_ENTITY1);
        this.victims.add(TARGET_ENTITY2);
        this.victims.add(TARGET_ENTITY3);
        this.victims.add(TARGET_ENTITY4);
        this.hackHeal = 0.01f;
        this.cothSpread = 1.0f;
        this.tasks.addTask(2, (EntityAIBase)new EntityAISkill(this, 160, 100, 10, false, 32, true));
        this.tasks.addTask(2, (EntityAIBase)new EntityAISkill(this, 240, 100, 0, false, 33, true));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager.of(this).register(SHADOW, (Object)true);
        EntityDataManager.of(this).register(CLONE, (Object)false);
        EntityDataManager.of(this).register(TARGET_ENTITY, (Object)0);
        EntityDataManager.of(this).register(TARGET_ENTITY1, (Object)0);
        EntityDataManager.of(this).register(TARGET_ENTITY2, (Object)0);
        EntityDataManager.of(this).register(TARGET_ENTITY3, (Object)0);
        EntityDataManager.of(this).register(TARGET_ENTITY4, (Object)0);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("SRPShadow", this.getShadowStatus());
        compound.setBoolean("SRPClone", this.getCloneC());
        compound.setInteger("SRPLimitClones", this.limitClones);
        compound.setInteger("SRPLimitClonesLife", this.limitClonesLife);
        compound.setInteger("SRPLimitClonesCooldown", this.limitClonesCooldown);
        compound.setFloat("SRPShadowDamage", this.shadowDamage);
        compound.setInteger("SRPShadowDamageTick", this.shadowDamageTick);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("SRPShadow")) {
            EntityDataManager.of(this).set(SHADOW, (Object)compound.getBoolean("SRPShadow"));
        }
        if (compound.hasKey("SRPClone")) {
            EntityDataManager.of(this).set(CLONE, (Object)compound.getBoolean("SRPClone"));
        }
        if (compound.hasKey("SRPLimitClones")) {
            this.limitClones = compound.getInteger("SRPLimitClones");
        }
        if (compound.hasKey("SRPLimitClonesLife")) {
            this.limitClonesLife = compound.getInteger("SRPLimitClonesLife");
        }
        if (compound.hasKey("SRPLimitClonesCooldown")) {
            this.limitClonesCooldown = compound.getInteger("SRPLimitClonesCooldown");
        }
        if (compound.hasKey("SRPShadowDamage")) {
            this.shadowDamage = compound.getFloat("SRPShadowDamage");
        }
        if (compound.hasKey("SRPShadowDamageTick")) {
            this.shadowDamageTick = compound.getInteger("SRPShadowDamageTick");
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.shakeee > 0) {
            --this.shakeee;
        }
        if ((float)this.shadowDamageRCooldown > 0.0f) {
            --this.shadowDamageRCooldown;
        }
        if (this.shadowDamageR > 0.0f && this.shadowDamageRCooldown == 0) {
            this.shadowDamageR -= 0.01f;
        }
        if (this.showC > -10) {
            --this.showC;
        }
        if (this.rand.nextInt(10) == 0 && this.showC <= -10) {
            this.showC = this.rand.nextInt(1) * 60;
        }
        if (this.srpTicks == 10 && this.rand.nextInt(10) == 0) {
            this.shadowDamageR = 0.6f;
            this.shadowDamageRCooldown = 40;
            this.worldObj.setEntityState((Entity)this, (byte)42);
        }
        if (!this.worldObj.isRemote) {
            if (this.srpTicks == 5 || this.srpTicks == 15) {
                this.setEffectsTargets(true);
            }
            --this.shadowDamageTick;
            if (this.shadowDamageTick <= 0) {
                this.shadowDamage = 0.0f;
            }
            if (this.srpTicks == 10) {
                this.checkShadow(100);
            }
        }
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        if (this.worldObj.isRemote) {
            return false;
        }
        if (source == DamageSource.outOfWorld || source == DamageSource.inWall) {
            return super.attackEntityFrom(source, amount);
        }
        if (this.getShadowStatus() || this.getCloneC()) {
            if (this.worldObj.getLightBrightness(EntityQueries.position(this)) <= 0.46666667f && this.worldObj.getLightFor(EnumSkyBlock.BLOCK, EntityQueries.position(this)) <= 7) {
                this.shakeee = 15;
                this.shadowDamageR = 0.6f;
                this.shadowDamageRCooldown = 40;
                this.showC = 15;
                this.worldObj.setEntityState((Entity)this, (byte)41);
                boolean flag = super.attackEntityFrom(source, 0.0f);
                if (flag) {
                    EntityDataManager.of(this).set(HIT, (Object)((byte)3));
                }
                return flag;
            }
            this.shadowDamage += amount;
            this.shadowDamageTick = 100;
            if (this.shadowDamage >= this.getMaxHealth() * 0.1f && !this.getCloneC()) {
                this.setShadowStatus(false);
                EntityPCosmical lol = this.getThis();
                if (lol != null) {
                    this.spawnCloneCosmical(lol);
                }
                this.shadowDamageTick = 200;
                this.shadowDamage = 0.0f;
            }
            this.shakeee = 15;
            this.shadowDamageR = 0.6f;
            this.shadowDamageRCooldown = 40;
            this.showC = 15;
            this.worldObj.setEntityState((Entity)this, (byte)41);
            EntityDataManager.of(this).set(HIT, (Object)((byte)3));
            boolean flag = super.attackEntityFrom(source, 0.0f);
            if (flag) {
                EntityDataManager.of(this).set(HIT, (Object)((byte)3));
            }
            return flag;
        }
        boolean flag = super.attackEntityFrom(source, amount);
        if (flag && source.getEntity() instanceof EntityLivingBase) {
            ((EntityLivingBase)source.getEntity()).attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase)this), this.damageAmountReduced / 2.0f);
            this.attackEntityAsMobMinimum((EntityLivingBase)source.getEntity(), this.damageAmountReduced / 2.0f);
        }
        return flag;
    }

    @Override
    public void setAttackTarget(EntityLivingBase entitylivingbaseIn) {
        super.setAttackTarget(entitylivingbaseIn);
    }

    @Override
    public void onDeath(DamageSource cause) {
        Entity flag;
        super.onDeath(cause);
        if (!this.worldObj.isRemote && (flag = this.worldObj.getEntityByID(this.limitClones)) != null && !this.getCloneC()) {
            flag.attackEntityFrom(DamageSource.outOfWorld, 100000.0f);
        }
    }

    protected abstract EntityPCosmical getThis();

    public void fall(float distance, float damageMultiplier) {
    }

    protected void checkShadow(int chance) {
        if (this.getParasiteStatus() >= 3) {
            return;
        }
        if (this.getCloneC()) {
            ++this.limitClonesLife;
            if (this.limitClonesLife > 22) {
                this.particleStatus((byte)7);
                Entity flag = this.worldObj.getEntityByID(this.limitClones);
                if (flag instanceof EntityPCosmical) {
                    EntityPCosmical original = (EntityPCosmical)flag;
                    original.particleStatus((byte)7);
                    original.limitClones = 0;
                    original.setShadowStatus(true);
                    original.limitClonesCooldown = 10;
                }
                this.setDead();
                return;
            }
            return;
        }
        if (this.getShadowStatus()) {
            EntityPCosmical lol;
            --this.limitClonesCooldown;
            if (this.rand.nextInt(chance) == 0 && this.limitClonesCooldown <= 0 && (lol = this.getThis()) != null) {
                this.spawnCloneCosmical(lol);
            }
        } else {
            Entity flag = this.worldObj.getEntityByID(this.limitClones);
            if (flag == null) {
                if (this.shadowDamageTick <= 0) {
                    this.particleStatus((byte)7);
                    this.limitClones = 0;
                    this.setShadowStatus(true);
                    this.limitClonesCooldown = 10;
                }
            } else if (this.getAttackTarget() != null) {
                ((EntityPCosmical)flag).setAttackTarget(this.getAttackTarget());
            }
        }
    }

    protected void spawnCloneCosmical(EntityPCosmical entityout) {
        entityout.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        entityout.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos((Entity)entityout)), null);
        if (this.hasCustomInventoryName()) {
            entityout.setMinecartName("--" + this.getCustomNameTag() + "--");
            entityout.setAlwaysRenderNameTag(this.getAlwaysRenderNameTag());
        }
        this.worldObj.spawnEntityInWorld((Entity)entityout);
        entityout.particleStatus((byte)7);
        this.limitClones = entityout.getEntityId();
        entityout.limitClones = this.getEntityId();
        this.setShadowStatus(false);
        entityout.setCloneC();
        entityout.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(entityout.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue() * 1.33);
        entityout.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(entityout.getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue() * 0.5);
    }

    public int shakingC() {
        return this.shakeee;
    }

    public int showC() {
        return this.showC;
    }

    public boolean getShadowStatus() {
        return (Boolean)EntityDataManager.of(this).get(SHADOW);
    }

    public void setShadowStatus(boolean in) {
        EntityDataManager.of(this).set(SHADOW, (Object)in);
        if (in) {
            this.shadowDamageR = 0.6f;
            this.shadowDamageRCooldown = 40;
            this.worldObj.setEntityState((Entity)this, (byte)41);
        }
    }

    public boolean getCloneC() {
        return (Boolean)EntityDataManager.of(this).get(CLONE);
    }

    public void setCloneC() {
        EntityDataManager.of(this).set(CLONE, (Object)true);
    }

    private void setTargetedEntity(int entityId) {
        for (DataParameter<Integer> mob : this.victims) {
            if ((Integer)EntityDataManager.of(this).get(mob) != entityId) continue;
            return;
        }
        for (DataParameter<Integer> mob : this.victims) {
            if ((Integer)EntityDataManager.of(this).get(mob) != 0) continue;
            EntityDataManager.of(this).set(mob, (Object)entityId);
            return;
        }
    }

    private void updateTargets() {
        for (DataParameter<Integer> mob : this.victims) {
            if ((Integer)EntityDataManager.of(this).get(mob) == 0) continue;
            Entity entity = this.worldObj.getEntityByID(((Integer)EntityDataManager.of(this).get(mob)).intValue());
            if (entity == null) {
                EntityDataManager.of(this).set(mob, (Object)0);
                continue;
            }
            if (!((EntityLivingBase)entity).isEntityAlive()) {
                EntityDataManager.of(this).set(mob, (Object)0);
                return;
            }
            if (!(((EntityLivingBase)entity).getDistanceSqToEntity((Entity)this) > 576.0)) continue;
            EntityDataManager.of(this).set(mob, (Object)0);
            return;
        }
    }

    private boolean hasTargetedEntity() {
        this.updateTargets();
        for (DataParameter<Integer> mob : this.victims) {
            if ((Integer)EntityDataManager.of(this).get(mob) == 0) continue;
            return true;
        }
        return false;
    }

    private boolean fullTargets() {
        this.updateTargets();
        int size = this.victims.size();
        int count = 0;
        for (DataParameter<Integer> mob : this.victims) {
            if ((Integer)EntityDataManager.of(this).get(mob) == 0) continue;
            ++count;
        }
        return size == count;
    }

    private void resetTargets() {
        for (DataParameter<Integer> mob : this.victims) {
            EntityDataManager.of(this).set(mob, (Object)0);
        }
    }

    private void setEffectsTargets(boolean effects) {
        for (DataParameter<Integer> mob : this.victims) {
            String rando;
            String[] stringArray;
            Potion potion;
            if ((Integer)EntityDataManager.of(this).get(mob) == 0) continue;
            Entity entity = this.worldObj.getEntityByID(((Integer)EntityDataManager.of(this).get(mob)).intValue());
            if (entity == null) {
                EntityDataManager.of(this).set(mob, (Object)0);
                continue;
            }
            EntityLivingBase mobis = (EntityLivingBase)entity;
            this.shadowDamageR = 0.6f;
            this.shadowDamageRCooldown = 40;
            this.worldObj.setEntityState((Entity)this, (byte)41);
            if (!mobis.isEntityAlive()) {
                EntityDataManager.of(this).set(mob, (Object)0);
                continue;
            }
            Collection potionsTarget = mobis.getActivePotionEffects();
            ArrayList<Potion> potionsToRemove = new ArrayList<Potion>();
            for (PotionEffect potionEffect : potionsTarget) {
                if (potionEffect.getPotion().isBadEffect()) continue;
                float amp = potionEffect.getAmplifier() + 1;
                potionsToRemove.add(potionEffect.getPotion());
                this.heal(this.getMaxHealth() * (this.hackHeal * amp));
            }
            for (Potion potion2 : potionsToRemove) {
                mobis.removePotionEffect(potion2);
            }
            if (!effects || this.hackEffects.length <= 0 || (potion = Potion.getPotionFromResourceLocation((String)(stringArray = (rando = this.hackEffects[this.rand.nextInt(this.hackEffects.length)]).split(";"))[1])) == null) continue;
            SRPPotions.applyStackPotion(potion, mobis, Integer.parseInt(stringArray[0]), Integer.parseInt(stringArray[2]));
        }
    }

    public ArrayList<EntityLivingBase> getTargetedEntityVictims() {
        if (!this.hasTargetedEntity()) {
            return new ArrayList<EntityLivingBase>();
        }
        ArrayList<EntityLivingBase> mobs = new ArrayList<EntityLivingBase>();
        for (DataParameter<Integer> mob : this.victims) {
            Entity entity;
            if ((Integer)EntityDataManager.of(this).get(mob) == 0 || (entity = this.worldObj.getEntityByID(((Integer)EntityDataManager.of(this).get(mob)).intValue())) == null) continue;
            mobs.add((EntityLivingBase)entity);
        }
        return mobs;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void handleHealthUpdate(byte id) {
        switch (id) {
            case 41: {
                this.shakeee = 15;
                this.shadowDamageR = 0.6f;
                this.shadowDamageRCooldown = 40;
                this.showC = 15;
                break;
            }
            case 42: {
                this.shadowDamageR = 0.6f;
                this.shadowDamageRCooldown = 40;
                break;
            }
            default: {
                super.handleHealthUpdate(id);
            }
        }
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 32: {
                return this.skillOrb;
            }
            case 33: {
                return this.skillHack;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 32: {
                this.skillOrb = in;
                return;
            }
            case 33: {
                this.skillHack = in;
                return;
            }
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 32: {
                this.cosmicOrb();
                return;
            }
            case 33: {
                this.cosmicHacking();
            }
        }
        super.doSpecialSkill(id);
    }

    private void cosmicOrb() {
        if (this.getAttackTarget() == null) {
            this.skillOrb = true;
            this.setParasiteStatus(0);
            this.borderOrb = 0;
            this.limitOrb = 0;
            return;
        }
        if (this.borderOrb == -1 || this.orbVersionCooldown > 0 && this.borderOrb == 0 || !this.getAttackTarget().isEntityAlive() || this.getCloneC()) {
            this.skillOrb = true;
            this.setParasiteStatus(0);
            this.borderOrb = 0;
            this.limitOrb = 0;
            return;
        }
        if (this.borderOrb == 0) {
            AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), (double)(this.fuseOrb / 2 + 1));
            List moblist = EntityQueries.entities(this.worldObj, EntityPMalleable.class, axisalignedbb);
            for (EntityPMalleable mob : moblist) {
                if (mob == this || mob.getParasiteType() > this.getParasiteType()) continue;
                mob.setOrbVersionCooldown(4);
            }
        }
        this.setParasiteStatus(19);
        this.getNavigator().clearPathEntity();
        if (this.ticksExisted % 20 != 0) {
            return;
        }
        ++this.borderOrb;
        if (this.borderOrb < 6) {
            if (this.borderOrb % 2 == 0) {
                return;
            }
            EntityOrbScary ttt = new EntityOrbScary(this.worldObj, this, this.fuseOrb, this.orbStartTimer, false);
            ttt.copyLocationAndAnglesFrom((Entity)this.getAttackTarget());
            ttt.posY -= (double)this.getAttackTarget().getEyeHeight();
            this.worldObj.spawnEntityInWorld((Entity)ttt);
            this.playSound(SRPSounds.ORB_S, 1.0f, 1.0f);
            return;
        }
        if (this.borderOrb > 10) {
            this.skillOrb = true;
            this.setParasiteStatus(0);
            this.borderOrb = 0;
            this.limitOrb = 0;
        }
    }

    private void cosmicHacking() {
        if (this.getAttackTarget() == null || this.getCloneC() || !this.onGround || !this.getShadowStatus()) {
            this.resetTargets();
            this.skillHack = true;
            this.setParasiteStatus(0);
            this.borderHack = 0;
            this.setGlowing(false);
            return;
        }
        this.setParasiteStatus(20);
        this.getNavigator().clearPathEntity();
        if (this.ticksExisted % 20 != 0) {
            return;
        }
        ++this.borderHack;
        if (this.borderHack >= 3) {
            if (!this.fullTargets()) {
                AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0), 24.0);
                List moblist = EntityQueries.entities(this.worldObj, EntityLivingBase.class, axisalignedbb);
                for (EntityLivingBase mob : moblist) {
                    if (mob == this || mob instanceof EntityParasiteBase || !mob.isEntityAlive()) continue;
                    if (mob instanceof EntityPlayer) {
                        if (((EntityPlayer)mob).isCreative()) continue;
                        this.setGlowing(true);
                    }
                    this.setTargetedEntity(mob.getEntityId());
                }
            }
        } else {
            this.shadowDamageR = 0.6f;
            this.shadowDamageRCooldown = 40;
            this.worldObj.setEntityState((Entity)this, (byte)41);
            this.particleStatus((byte)7);
        }
        if (this.borderHack >= 7) {
            this.resetTargets();
            this.skillHack = true;
            this.setParasiteStatus(0);
            this.setGlowing(false);
            this.borderHack = 0;
        }
    }
}

