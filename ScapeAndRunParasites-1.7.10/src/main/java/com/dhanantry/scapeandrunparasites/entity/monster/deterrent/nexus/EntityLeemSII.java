/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINexusGrow;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPRooter;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityLeem;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import java.util.Arrays;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import com.dhanantry.scapeandrunparasites.backport.entity.ai.EntityAIHurtByTarget;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityLeemSII
extends EntityPRooter {
    public EntityLeemSII(World worldIn) {
        super(worldIn);
        this.setSize(1.2f, 5.2f);
        this.buriedT = 2.6;
        this.ignoreFrustumCheck = true;
        this.experienceValue = SRPAttributes.XP_PRIMITIVE * 2;
        this.buried = 0.1;
        this.setParasiteStatus(3);
        this.damageCap = SRPConfig.nexussiCap;
        this.pointCap = SRPConfig.nexussiiPointCap;
        this.pointReduction = SRPConfig.nexussiiPointRed;
        this.chanceLearn = SRPConfig.nexussiiChanceLe;
        this.chanceLearnFire = SRPConfig.nexussiiChanceLeFire;
        this.DamageTypeCap = SRPConfig.nexussiiPointDamCap;
        this.stage = (byte)2;
        this.totalP = SRPConfigMobs.leemsiilimit;
        this.mobID = new int[SRPConfigMobs.leemsiilimit];
        this.mobPT = new int[SRPConfigMobs.leemsiilimit];
        this.leemRange = SRPConfigMobs.leemsiiRange;
        this.leemRangeEffect = SRPConfigMobs.leemsiiRangeEffect;
        this.leemBalls = SRPConfigMobs.leemsiilimit;
        this.leemCooldownReset = SRPConfigMobs.leemsiiCooldown;
        Arrays.fill(this.mobID, -777);
        this.neededTime = this.setGT(SRPConfig.nexusMinGrowTime, SRPConfig.nexusMaxGrowTime);
        this.valueEvDeath = SRPConfig.nexussiiLoosingEPValue;
        this.rangeB = 2;
    }

    @Override
    public int getParasiteIDRegister() {
        return 311;
    }

    protected void initEntityAI() {
        this.targetTasks.addTask(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.tasks.addTask(4, (EntityAIBase)new EntityAINexusGrow(this, 2, 3));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(SRPAttributes.VENKROLSII_HEALTH);
        this.getEntityAttribute(com.dhanantry.scapeandrunparasites.backport.entity.ModernAttributes.ARMOR).setBaseValue(SRPAttributes.VENKROLSII_ARMOR);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(SRPAttributes.VENKROLSII_ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(SRPConfig.nexussiiFollow);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

    public float getEyeHeight() {
        return 1.4f;
    }

    protected String getLivingSound() {
        return SRPSounds.MOBSILENCE;
    }

    protected String getHurtSound(DamageSource damageSourceIn) {
        if (this.rand.nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE;
        }
        return SRPSounds.MOBSILENCE;
    }

    protected String getDeathSound() {
        return SRPSounds.MOBSILENCE;
    }

    @Override
    public float getBombDamage() {
        return (float)SRPAttributes.VENKROL_ATTACK_DAMAGE;
    }

    @Override
    public void onDeath(DamageSource cause) {
        if (!this.worldObj.isRemote) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.worldObj) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.worldObj, cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityLeem(this.worldObj), true, false);
                } else {
                    super.onDeath(cause);
                }
            } else {
                super.onDeath(cause);
            }
        }
    }

    @Override
    protected boolean onDeathDislo(DamageSource cause) {
        ParasiteEventWorld.setDisloWorldPhase(this.worldObj, SRPAttributes.EVENTPARANEXUSIID, SRPConfigSystems.chanceEventParaNexusIID, 0, null);
        return super.onDeathDislo(cause);
    }
}

