/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.util.DamageSource
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanHaveBodies;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.DamageSource;

public class EntityAIFollowBodies
extends EntityAIBase {
    private final EntityParasiteBase parent;
    private final EntityCanHaveBodies worm;
    private int digCool;
    private int digTicks;
    private double push;
    private double movementV;
    private boolean check;
    private byte checkMove;
    private double evolutionNeed;
    private int digSkillCooldown;
    private int digSkill;
    EntityCanHaveBodies follo;
    EntityLivingBase follo2;

    public EntityAIFollowBodies(EntityParasiteBase in, double pushDistance, double movement, int digCooldown, double evolution, int digSkillCool) {
        this.parent = in;
        this.worm = (EntityCanHaveBodies)((Object)in);
        this.digCool = digCooldown;
        this.push = pushDistance;
        this.movementV = movement;
        this.check = false;
        this.checkMove = 0;
        this.evolutionNeed = evolution;
        this.digSkillCooldown = digSkillCool;
    }

    public boolean shouldExecute() {
        return this.parent.isEntityAlive();
    }

    public void updateTask() {
        ++this.digSkill;
        if (this.worm.getFollowing() != null) {
            this.followHead();
        } else {
            this.diggingTeleport();
        }
    }

    private void followHead() {
        EntityCanHaveBodies follo = (EntityCanHaveBodies)this.getFather(this.worm.getFollowing());
        if (follo == null) {
            this.worm.setFollowing(null);
            this.parent.setDead();
            return;
        }
        EntityLivingBase follo2 = (EntityLivingBase)follo.getEntity();
        if (!follo2.isEntityAlive()) {
            this.parent.attackEntityFrom(DamageSource.generic, 1000000.0f);
            return;
        }
        this.parent.setKillC(follo.getKillPoints());
        if (this.parent.getKillC() > this.evolutionNeed && ParasiteEventEntity.canSpawnNext) {
            this.parent.setDead();
        }
        this.parent.targetNewCool = 10;
        this.parent.aiWander.setExecutionValues(999999, 2.0f);
        this.parent.setParasiteStatus(-77);
        double dis = this.parent.getDistanceSqToEntity((Entity)follo2);
        double checkDis = this.push;
        boolean flag = follo.getDigging();
        if (this.parent.srpTicks == 10) {
            this.worm.setDigging(flag);
            this.worm.bodyPartEffect();
        }
        if (this.worm.getBodiesT() - 1 >= this.worm.getBodyNumber()) {
            checkDis = 0.2;
        }
        if (dis > checkDis * checkDis) {
            double deltaZ;
            double deltaY;
            double deltaX;
            double distance;
            double str = 0.19;
            if (flag) {
                if (dis > 0.25) {
                    this.parent.lookAt((Entity)follo2);
                }
            } else {
                this.parent.lookAt((Entity)follo2);
            }
            if (dis > 9.0) {
                this.parent.copyLocationAndAnglesFrom((Entity)follo2);
            }
            if ((distance = Math.sqrt((deltaX = follo2.posX - this.parent.posX) * deltaX + (deltaY = follo2.posY - this.parent.posY) * deltaY + (deltaZ = follo2.posZ - this.parent.posZ) * deltaZ)) == 0.0) {
                return;
            }
            this.parent.motionX += (deltaX /= distance) * str;
            this.parent.motionY += (deltaY /= distance) * str;
            this.parent.motionZ += (deltaZ /= distance) * str;
            this.parent.setWorkTask(false);
        }
    }

    private void diggingTeleport() {
        if (this.parent.getKillC() > this.evolutionNeed && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this.parent, this.worm.getEvolution(this.parent.worldObj), true, true);
        }
        if (this.parent.srpTicks == 10) {
            if (this.digCool > 0) {
                --this.digCool;
                if (this.digCool == 3) {
                    this.parent.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(this.movementV);
                    this.digSkill = 0;
                }
                return;
            }
            if (this.parent.posX == this.parent.prevPosX && this.parent.posZ == this.parent.prevPosZ) {
                this.checkMove = (byte)(this.checkMove + 1);
                if (this.checkMove >= 2 && !this.worm.getDigging()) {
                    this.parent.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(this.movementV);
                }
            }
            if (this.worm.getDigging()) {
                this.parent.getNavigator().clearPathEntity();
                this.parent.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0);
                this.parent.setParasiteStatus(-5);
                this.worm.setDigging(true);
                ++this.digTicks;
                if (this.digTicks > this.worm.getBodyLength() + 2) {
                    if (this.worm.getTargetPos() != null) {
                        if (this.parent.getAttackTarget() != null) {
                            this.worm.setTargetPos(EntityQueries.position(this.parent.getAttackTarget()));
                        }
                        if (ParasiteEventEntity.teleportDigging(this.parent, 10.0f, this.worm.getTargetPos(), 4, 1)) {
                            this.worm.setTargetPos(null);
                        }
                    }
                    if (this.digTicks > this.worm.getBodyLength() * 2 + 2) {
                        this.digTicks = 0;
                        this.digCool = 5;
                        this.worm.setDigging(false);
                        this.parent.setParasiteStatus(0);
                    }
                }
            } else if (this.parent.getAttackTarget() != null && this.parent.onGround) {
                double distanceEn = this.parent.getDistanceSqToEntity((Entity)this.parent.getAttackTarget());
                if ((distanceEn > 196.0 || !this.parent.canEntityBeSeen((Entity)this.parent.getAttackTarget()) && distanceEn > 49.0) && this.digSkill > this.digSkillCooldown) {
                    this.digSkill = 0;
                    this.worm.setDigging(true);
                    this.worm.setTargetPos(EntityQueries.position(this.parent.getAttackTarget()));
                }
            } else {
                this.worm.setDigging(false);
                this.worm.setTargetPos(null);
            }
        }
    }

    private Entity getFather(UUID uuid) {
        List serverList = EntityQueries.loaded(this.parent.worldObj);
        for (int x = 0; x < serverList.size(); ++x) {
            if (!uuid.equals(((Entity)serverList.get(x)).getUniqueID())) continue;
            return (Entity)serverList.get(x);
        }
        return null;
    }
}

