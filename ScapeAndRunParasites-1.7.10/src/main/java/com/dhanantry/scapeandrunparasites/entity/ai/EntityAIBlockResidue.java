/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIBlockResidue
extends EntityAIBase {
    private final EntityParasiteBase parent;
    private int ticks = 0;
    private int range;
    private int blockBreakCounter;

    public EntityAIBlockResidue(EntityParasiteBase ghast, int range) {
        this.parent = ghast;
        this.range = range;
        this.blockBreakCounter = 160;
    }

    public boolean shouldExecute() {
        return this.parent.getAttackTarget() == null && !this.parent.isInWater() && this.parent.getGeneMod(8);
    }

    public void resetTask() {
        this.parent.setParasiteStatus(0);
        this.blockBreakCounter = 160;
    }

    public void updateTask() {
        if (this.blockBreakCounter > 0) {
            if (this.parent.worldObj.rand.nextInt(5) == 0) {
                --this.blockBreakCounter;
            }
        } else {
            --this.blockBreakCounter;
            if (this.blockBreakCounter == -1) {
                this.parent.setParasiteStatus(25);
                this.parent.getNavigator().clearPathEntity();
                com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this.parent, SRPSounds.ADAPTED_V, 2.0f, 1.0f);
            }
            if (this.blockBreakCounter == -40) {
                com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(this.parent, SRPSounds.ADAPTED_V, 2.0f, 1.0f);
            }
            this.parent.particleStatus((byte)13);
            if (this.blockBreakCounter == -60) {
                double i1 = MathHelper.floor_double((double)(this.parent.posY + 0.1));
                double l1 = this.parent.posX;
                double i2 = this.parent.posZ;
                World world = this.parent.worldObj;
                if (this.blockBreakCounter == -63) {
                    // empty if block
                }
                for (int k2 = -1 * this.range; k2 <= 1 * this.range; ++k2) {
                    for (int l2 = -1 * this.range; l2 <= 1 * this.range; ++l2) {
                        double i3 = l1 + (double)k2;
                        double l = i2 + (double)l2;
                        BlockPos blockpos = new BlockPos(i3, i1, l);
                        Block block = BlockStates.get(this.parent.worldObj, blockpos).getBlock();
                        Block blockDown = BlockStates.get(this.parent.worldObj, blockpos.down()).getBlock();
                        if (block != Blocks.air || blockDown == Blocks.air || !BlockStates.get(world, blockpos.down()).isFullBlock() || blockDown == SRPBlocks.InfestedStain || this.parent.worldObj.rand.nextInt(2) != 0) continue;
                        BlockStates.set(this.parent.worldObj, blockpos, BlockStates.fromMeta(SRPBlocks.InfestRemain, 1));
                    }
                }
            }
            if (this.blockBreakCounter == -100) {
                this.parent.setParasiteStatus(0);
                this.blockBreakCounter = 200;
            }
        }
    }
}

