/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.MobEffects
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.monster.deterrent;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;
import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDispatcher;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class EntityDodT
extends EntityPStationary {
    private String paraToRelo;
    private EntityPDispatcher father;
    private EntityParasiteBase tele;
    private int stage;

    public EntityDodT(World worldIn) {
        super(worldIn);
        this.setSize(0.7f, 2.5f);
        this.experienceValue = 0;
        this.type = (byte)40;
        this.buriedT = 4.0;
        this.damageCap = SRPConfig.turretCap;
        this.pointCap = SRPConfig.turretPointCap;
        this.pointReduction = SRPConfig.turretPointRed;
        this.chanceLearn = SRPConfig.turretChanceLe;
        this.chanceLearnFire = SRPConfig.turretChanceLeFire;
        this.DamageTypeCap = SRPConfig.turretPointDamCap;
        this.MiniDamage = SRPConfig.turretMinDamage;
        this.regen = SRPConfig.turretRegen;
        this.valueEvDeath = 0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 74;
    }

    protected void initEntityAI() {
        this.tasks.addTask(8, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote) {
            if (this.getAttackTarget() != null && this.getAttackTarget().getDistanceSqToEntity((Entity)this) > 256.0) {
                this.worldObj.setEntityState((Entity)this, (byte)51);
                this.up = true;
            }
            if (this.ticksExisted > 120 && this.paraToRelo == null && this.tele == null) {
                this.worldObj.setEntityState((Entity)this, (byte)51);
                this.up = true;
            }
            if (this.srpTicks == 10 && this.father != null && this.father.getAttackTarget() != null) {
                this.lookAt((Entity)this.father.getAttackTarget());
            }
            if (!this.up && this.ticksExisted > 60 && !this.buried()) {
                if (this.paraToRelo != null) {
                    EntityParasiteBase entityout = (EntityParasiteBase)EntityList.createEntityByIDFromName((ResourceLocation)new ResourceLocation(this.paraToRelo), (World)this.worldObj);
                    if (entityout != null) {
                        entityout.copyLocationAndAnglesFrom((Entity)this);
                        entityout.onInitialSpawn(DifficultyAccess.at(this.worldObj, EntityQueries.position(entityout)), null);
                        this.worldObj.spawnEntityInWorld((Entity)entityout);
                        this.applyEffectsDod(entityout);
                        this.paraToRelo = null;
                        entityout.particleStatus((byte)8);
                        entityout.particleStatus((byte)8);
                        if (this.isBurning()) {
                            entityout.setHealth(entityout.getMaxHealth() * 0.5f);
                            entityout.setFire(8);
                        }
                        if (this.father != null && this.father.isEntityAlive()) {
                            entityout.setDodFatherID(this.father.getEntityId());
                        }
                        this.particleStatus((byte)8);
                        this.particleStatus((byte)8);
                        this.particleStatus((byte)8);
                    }
                } else if (this.tele != null) {
                    this.tele.copyLocationAndAnglesFrom((Entity)this);
                    this.tele.particleStatus((byte)7);
                    this.particleStatus((byte)7);
                    this.tele = null;
                }
            }
        }
    }

    @Override
    protected void retreat(boolean dead) {
        if (this.up) {
            this.buried += 0.08;
            if (this.buried > this.buriedT && !this.worldObj.isRemote) {
                this.setDead();
            }
        }
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (this.father != null && this.father.isEntityAlive()) {
            this.father.addParaBack(this.paraToRelo);
        }
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (this.worldObj.isRemote) {
            return true;
        }
        Item wea = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem();
        if (wea != SRPItems.itembase) {
            // empty if block
        }
        if (this.father != null) {
            this.father.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(MobEffects.GLOWING, 100, 1, false, false));
        }
        return super.processInteract(player, hand);
    }

    public void setTele(String in, int s) {
        this.paraToRelo = in;
        this.stage = s;
    }

    public void setTele(EntityParasiteBase in) {
        this.tele = in;
    }

    public String getTele() {
        return this.paraToRelo;
    }

    protected void applyEffectsDod(EntityParasiteBase in) {
        String[] pots = SRPConfigMobs.dodsiEffects;
        switch (this.stage) {
            case 2: {
                pots = SRPConfigMobs.dodsiiEffects;
                break;
            }
            case 3: {
                pots = SRPConfigMobs.dodsiiiEffects;
                break;
            }
            case 4: {
                pots = SRPConfigMobs.dodsivEffects;
            }
        }
        if (pots.length == 0) {
            return;
        }
        String[] here = new String[3];
        for (String i : pots) {
            here = i.split(";");
            int ticks = Integer.parseInt(here[0]);
            int amp = Integer.parseInt(here[2]);
            Potion potion = Potion.getPotionFromResourceLocation((String)here[1]);
            if (potion == null) continue;
            in.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(potion, ticks, amp));
        }
    }

    public EntityPDispatcher getFather() {
        return this.father;
    }

    public void setFather(EntityPDispatcher in) {
        this.father = in;
    }

    public float getEyeHeight() {
        return 1.8f;
    }
}

