/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAITarget
 *  net.minecraft.entity.monster.EntityCreeper
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.passive.EntityWaterMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;

public class EntityAIFlightAttack
extends EntityAIBase {
    private final EntityParasiteBase parent;
    private int delay;
    private double distance;
    private boolean xRay;
    private int fromF;

    public EntityAIFlightAttack(EntityParasiteBase in, double distance) {
        this.parent = in;
        this.delay = 0;
        this.distance = distance * distance;
    }

    public EntityAIFlightAttack(EntityParasiteBase in, double distance, boolean ignoreSee, int floorS) {
        this(in, distance);
        this.xRay = ignoreSee;
        this.fromF = floorS;
    }

    public boolean shouldExecute() {
        return this.parent.getWait() == 0 && this.parent.srpTicks <= 10;
    }

    public void updateTask() {
        this.parent.reduceIdleChance(2, 1);
        if (this.parent.getAttackTarget() != null) {
            if (this.parent.getAttackTarget() instanceof EntityParasiteBase) {
                this.parent.setAttackTarget(null);
                this.delay = 0;
                this.parent.setParasiteStatus(0);
                if (this.parent.getParasiteType() != 61) {
                    this.parent.getMoveHelper().setMoveTo(this.parent.posX, this.parent.posY, this.parent.posZ, 1.0);
                }
                return;
            }
            if (this.parent.worldObj.getEntityByID(this.parent.getAttackTarget().getEntityId()) == null) {
                this.parent.setAttackTarget(null);
                this.delay = 0;
                this.parent.setParasiteStatus(0);
                if (this.parent.getParasiteType() != 61) {
                    this.parent.getMoveHelper().setMoveTo(this.parent.posX, this.parent.posY, this.parent.posZ, 1.0);
                }
                return;
            }
            EntityLivingBase target = this.parent.getAttackTarget();
            if (target instanceof EntityPlayer && !this.canAttackPlayer((EntityPlayer)target)) {
                this.parent.setAttackTarget(null);
                this.delay = 0;
                this.parent.setParasiteStatus(0);
            }
            if (!this.parent.canEntityBeSeen((Entity)target)) {
                if (this.xRay) {
                    if (this.parent.worldObj.rand.nextInt(5) == 0) {
                        ++this.delay;
                    }
                } else {
                    ++this.delay;
                }
            } else {
                this.delay = target.getDistanceSqToEntity((Entity)this.parent) >= this.distance ? ++this.delay : 0;
            }
            if (this.delay >= 6) {
                this.parent.setAttackTarget(null);
                this.parent.setParasiteStatus(0);
                this.delay = 0;
                if (this.parent.getParasiteType() != 61) {
                    this.parent.getMoveHelper().setMoveTo(this.parent.posX, this.parent.posY, this.parent.posZ, 1.0);
                }
            }
        } else {
            this.attackSurr();
        }
    }

    protected void attackSurr() {
        BlockPos posi = EntityQueries.position(this.parent);
        if (this.fromF != 0 && this.parent.worldObj.rand.nextInt(this.fromF) == 0 && (posi = ParasiteEventEntity.getFloor(this.parent.worldObj, posi, 25)) == null) {
            posi = EntityQueries.position(this.parent);
        }
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(posi), this.parent.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue());
        List moblist = EntityQueries.entities(this.parent.worldObj, EntityLivingBase.class, axisalignedbb);
        for (EntityLivingBase mob : moblist) {
            if (mob == null) continue;
            if (mob instanceof EntityPlayer) {
                if (!this.canAttackPlayer((EntityPlayer)mob)) continue;
                this.parent.setAttackTarget(mob);
                return;
            }
            if (!(mob instanceof EntityLiving) || !SRPConfig.mobattacking || ParasiteEventEntity.checkEntity((EntityLivingBase)((EntityLiving)mob), SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite) || this.fromF != 0 && mob.getDistanceSqToEntity((Entity)this.parent) > this.distance || mob == this.parent || !this.canTargetEntity(mob) || !this.parent.canEntityBeSeen((Entity)mob) && !this.xRay || !(mob.getDistanceSqToEntity((Entity)this.parent) < 1024.0) || !mob.isEntityAlive() || !this.parent.canAttackClass(mob.getClass())) continue;
            this.parent.setAttackTarget(mob);
            this.parent.setParasiteStatus(1);
            return;
        }
    }

    private boolean canTargetEntity(EntityLivingBase in) {
        return !(in instanceof EntityParasiteBase) && !(in instanceof EntityAnimal) && !(in instanceof EntityCreeper) && !(in instanceof EntityWaterMob);
    }

    private boolean canAttackPlayer(EntityPlayer in) {
        if (in.capabilities.disableDamage) {
            return false;
        }
        return EntityAITarget.isSuitableTarget((EntityLiving)this.parent, (EntityLivingBase)in, (boolean)false, (boolean)true);
    }
}

