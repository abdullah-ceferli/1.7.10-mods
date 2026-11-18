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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIAttackSwell
extends EntityAIBase {
    EntityParasiteBase swellingParasite;
    EntityLivingBase creeperAttackTarget;
    double distance;

    public EntityAIAttackSwell(EntityParasiteBase in) {
        this.swellingParasite = in;
        this.setMutexBits(1);
        this.distance = 9.0;
    }

    public EntityAIAttackSwell(EntityParasiteBase in, double dis) {
        this(in);
        this.distance = dis;
        this.setMutexBits(0);
    }

    public boolean shouldExecute() {
        EntityLivingBase entitylivingbase = this.swellingParasite.getAttackTarget();
        return this.swellingParasite.getSelfeState() > 0 || entitylivingbase != null && this.swellingParasite.getDistanceSqToEntity((Entity)entitylivingbase) < this.distance;
    }

    public void startExecuting() {
        this.swellingParasite.getNavigator().clearPathEntity();
        this.creeperAttackTarget = this.swellingParasite.getAttackTarget();
    }

    public void resetTask() {
        this.creeperAttackTarget = null;
    }

    public void updateTask() {
        if (this.creeperAttackTarget == null) {
            this.swellingParasite.setSelfeState(-1);
        } else if (this.swellingParasite.getDistanceSqToEntity((Entity)this.creeperAttackTarget) > 49.0) {
            this.swellingParasite.setSelfeState(-1);
        } else if (!this.swellingParasite.getEntitySenses().canSee((Entity)this.creeperAttackTarget)) {
            this.swellingParasite.setSelfeState(-1);
        } else {
            this.swellingParasite.setSelfeState(1);
        }
    }
}

