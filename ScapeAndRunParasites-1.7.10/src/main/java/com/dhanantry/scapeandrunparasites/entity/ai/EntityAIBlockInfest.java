/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

public class EntityAIBlockInfest
extends EntityAIBase {
    private final EntityParasiteBase parentEntity;
    private int ticks = 0;
    private int stage;

    public EntityAIBlockInfest(EntityParasiteBase ghast, int stage) {
        this.parentEntity = ghast;
        this.stage = stage;
    }

    public boolean shouldExecute() {
        return true;
    }

    public void startExecuting() {
    }

    public void resetTask() {
    }

    public void updateTask() {
        ++this.ticks;
        if (this.ticks > 200) {
            this.ticks = 0;
            World world = this.parentEntity.worldObj;
            Block block = BlockStates.get(world, EntityQueries.position(this.parentEntity)).getBlock();
            BlockParasiteSpreading.canInfestBlock(world, EntityQueries.position(this.parentEntity), new Random(), this.stage, true);
        }
    }
}

