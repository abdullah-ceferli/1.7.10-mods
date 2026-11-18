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
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.AxisAlignedBB;

public class EntityAIGetFollowers
extends EntityAIBase {
    private final EntityParasiteBase parent;
    private int version;
    private int searchRange;

    public EntityAIGetFollowers(EntityParasiteBase in, int mobVersion, int range) {
        this.parent = in;
        this.version = mobVersion;
        this.searchRange = range;
    }

    public boolean shouldExecute() {
        return this.parent.ticksExisted % 20 == 0 && this.parent.getParasiteFollowing() == null && this.parent.getAttackTarget() == null;
    }

    public void updateTask() {
        AxisAlignedBB axisalignedbb = Boxes.grow(Boxes.create(this.parent.posX, this.parent.posY, this.parent.posZ, this.parent.posX + 1.0, this.parent.posY + 1.0, this.parent.posZ + 1.0), (double)this.searchRange, 2.0, (double)this.searchRange);
        List moblist = EntityQueries.entities(this.parent.worldObj, EntityParasiteBase.class, axisalignedbb);
        block0 : switch (this.version) {
            case 1: {
                for (EntityParasiteBase mob : moblist) {
                    if (mob == this.parent || !this.parent.canEntityBeSeen((Entity)mob) || !mob.isEntityAlive() || mob.getParasiteType() >= 31) continue;
                    if (mob.getParasiteFollowing() == null) {
                        mob.setParasiteToFollow(this.parent);
                        break block0;
                    }
                    if (mob.getParasiteFollowing().getParasiteType() > 10) continue;
                    mob.setParasiteToFollow(this.parent);
                    break block0;
                }
                break;
            }
            case 2: {
                for (EntityParasiteBase mob : moblist) {
                    if (mob == this.parent || !this.parent.canEntityBeSeen((Entity)mob) || !mob.isEntityAlive() || mob.getParasiteType() >= 41) continue;
                    if (mob.getParasiteFollowing() == null) {
                        mob.setParasiteToFollow(this.parent);
                        break block0;
                    }
                    if (mob.getParasiteFollowing().getParasiteType() > 30) continue;
                    mob.setParasiteToFollow(this.parent);
                    break block0;
                }
                break;
            }
            case 3: {
                for (EntityParasiteBase mob : moblist) {
                    if (mob == this.parent || !this.parent.canEntityBeSeen((Entity)mob) || !mob.isEntityAlive() || mob.getParasiteType() >= 41) continue;
                    if (mob.getParasiteFollowing() == null) {
                        mob.setParasiteToFollow(this.parent);
                        break block0;
                    }
                    if (mob.getParasiteFollowing().getParasiteType() > 40) continue;
                    mob.setParasiteToFollow(this.parent);
                    break block0;
                }
                break;
            }
            case 4: {
                for (EntityParasiteBase mob : moblist) {
                    if (mob == this.parent || !this.parent.canEntityBeSeen((Entity)mob) || !mob.isEntityAlive() || mob.getParasiteType() >= 61) continue;
                    if (mob.getParasiteFollowing() == null) {
                        mob.setParasiteToFollow(this.parent);
                        break block0;
                    }
                    if (mob.getParasiteFollowing().getParasiteType() > 60) continue;
                    mob.setParasiteToFollow(this.parent);
                    break block0;
                }
                break;
            }
        }
    }
}

