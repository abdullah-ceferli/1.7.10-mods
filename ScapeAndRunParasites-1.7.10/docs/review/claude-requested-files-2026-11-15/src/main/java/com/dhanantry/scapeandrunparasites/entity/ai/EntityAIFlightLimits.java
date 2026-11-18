/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;

public class EntityAIFlightLimits
extends EntityAIBase {
    private final EntityParasiteBase parent;
    private int limit;
    private boolean fLimit;

    public EntityAIFlightLimits(EntityParasiteBase in, int limit, boolean flighLimit) {
        this.parent = in;
        this.limit = limit;
        this.fLimit = flighLimit;
    }

    public boolean shouldExecute() {
        return true;
    }

    public void updateTask() {
        if (this.fLimit) {
            if (this.parent.getAttackTarget() != null) {
                if (this.parent.getAttackTarget().posY + (double)this.limit > this.parent.posY) {
                    this.parent.motionY -= 0.04;
                }
            } else {
                boolean ground = this.howMuchNeg(EntityQueries.position(this.parent).down(1), 1);
                if (ground) {
                    this.parent.motionY -= 0.04;
                }
            }
        } else {
            boolean ground = this.howMuchPos(EntityQueries.position(this.parent).down(1), 1);
            if (ground) {
                this.parent.motionY += 0.04;
            }
        }
    }

    private boolean howMuchNeg(BlockPos y, int count) {
        if (y.getY() < 1) {
            return false;
        }
        if (count <= this.limit) {
            if (BlockStates.get(this.parent.worldObj, y).getBlock() == Blocks.air) {
                return this.howMuchNeg(y.down(), ++count);
            }
            return false;
        }
        if (count > this.limit) {
            return true;
        }
        return true;
    }

    private boolean howMuchPos(BlockPos y, int count) {
        if (y.getY() < 1) {
            return false;
        }
        if (count <= this.limit) {
            if (BlockStates.get(this.parent.worldObj, y).getBlock() == Blocks.air) {
                return this.howMuchPos(y.down(), ++count);
            }
            return true;
        }
        return count <= this.limit;
    }
}

