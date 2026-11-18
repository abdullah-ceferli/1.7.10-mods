/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.util.math.AxisAlignedBB
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.AxisAlignedBB;

public class EntityAIAvoidOrAttack
extends EntityAIBase {
    private final EntityParasiteBase parent;
    private int tickss;
    private float light;
    private int rangexz;
    private int rangey;

    public EntityAIAvoidOrAttack(EntityParasiteBase in, float lightR, int xz, int y) {
        this.parent = in;
        this.tickss = 20;
        this.light = lightR;
        this.rangexz = xz;
        this.rangey = y;
        this.parent.setWorkTask(false);
    }

    public boolean shouldExecute() {
        --this.tickss;
        if (this.tickss < 0) {
            this.tickss = 20;
            return this.parent.getParasiteFollowing() == null;
        }
        return false;
    }

    public void updateTask() {
        if (this.parent.isPotionActive(SRPPotions.RAGE_E)) {
            this.parent.setWorkTask(true);
            return;
        }
        if (this.parent.getBrightness() >= this.light) {
            boolean flag = true;
            AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.parent.posX, this.parent.posY, this.parent.posZ, this.parent.posX + 1.0, this.parent.posY + 1.0, this.parent.posZ + 1.0), (double)this.rangexz, (double)this.rangey, (double)this.rangexz);
            List moblist = EntityQueries.entities(this.parent.worldObj, EntityParasiteBase.class, axisalignedbb);
            for (EntityParasiteBase mob : moblist) {
                if (mob == this.parent || !this.parent.canEntityBeSeen((Entity)mob) || !mob.isEntityAlive()) continue;
                if (flag) {
                    flag = false;
                    this.parent.setWorkTask(true);
                }
                if (this.parent.getParasiteFollowing() == null) {
                    if (mob.getParasiteFollowing() == null) {
                        this.parent.setParasiteToFollow(mob);
                    } else if (mob.getParasiteFollowing().getParasiteType() >= 11) {
                        this.parent.setParasiteToFollow(mob.getParasiteFollowing());
                    }
                }
                mob.setWorkTask(true);
            }
            if (flag) {
                this.parent.setWorkTask(false);
            }
        } else {
            this.parent.setWorkTask(true);
        }
    }
}

