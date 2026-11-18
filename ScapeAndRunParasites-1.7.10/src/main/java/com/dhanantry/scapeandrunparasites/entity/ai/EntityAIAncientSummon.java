/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.ai.EntityAIBase
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIAncientSummon
extends EntityAIBase {
    private final EntityParasiteBase parentEntity;
    private int limit = 0;
    private int sCooldown;
    private int sLimit;
    private String[] sMobs;
    private int attackTimer = 0;
    private int attacking = 0;
    private double targetX;
    private double targetY;
    private double targetZ;

    public EntityAIAncientSummon(EntityParasiteBase ancient, int cooldown, int limit, String[] mobList) {
        this.parentEntity = ancient;
        this.sCooldown = cooldown;
        this.sLimit = limit;
        this.sMobs = mobList;
    }

    public boolean shouldExecute() {
        return this.parentEntity.getAttackTarget() != null || this.attacking >= 1;
    }

    public void startExecuting() {
    }

    public void resetTask() {
        this.attackTimer = 0;
        this.limit = 0;
    }

    public void updateTask() {
        if (this.attacking >= 1) {
            ++this.attacking;
            if (this.attacking == 2) {
                com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this.parentEntity, SRPSounds.ANCIENT_POD, 5.0f, 1.0f);
            }
            if (this.attacking % 20 == 0 && this.attacking >= 40 && ParasiteSummon.SummonM(this.parentEntity, this.sMobs, 10, this.targetX, this.targetY, this.targetZ, this.parentEntity.getAttackTarget(), true)) {
                ++this.limit;
            }
            if (this.limit >= this.sLimit) {
                this.attacking = 0;
                this.attackTimer = 0;
                this.limit = 0;
            }
        } else if (this.parentEntity.getAttackTarget() == null) {
            this.attackTimer = 0;
        } else if (this.parentEntity.getAttackTarget().isDead) {
            this.attackTimer = 0;
        } else {
            EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
            if (this.parentEntity.canEntityBeSeen((Entity)entitylivingbase) && this.parentEntity.getDistanceSqToEntity((Entity)entitylivingbase) < 2500.0) {
                if (entitylivingbase.onGround || this.parentEntity.worldObj.rand.nextBoolean()) {
                    ++this.attackTimer;
                }
                if (this.attackTimer >= this.sCooldown && this.attacking == 0) {
                    ++this.attacking;
                    this.targetX = entitylivingbase.posX;
                    this.targetY = this.parentEntity.posY + 25.0;
                    this.targetZ = entitylivingbase.posZ;
                }
            } else {
                this.attackTimer = 0;
            }
        }
    }
}

