/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockEndGateway
 *  net.minecraft.block.BlockEndPortalFrame
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.BlockPortal
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.EnumSkyBlock
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndGateway;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class EntityAIBlockLight
extends EntityAIBase {
    private final EntityParasiteBase parent;
    private int ticks = 0;
    private int range;
    private int lightTrigger;
    private BlockPos target;
    private World world;
    private int progressB;
    private int idle;
    private int neededTime;
    private Block block;
    private int disss;
    private int prevProgressB = -1;
    private ArrayList<Long> cant;

    public EntityAIBlockLight(EntityParasiteBase in, int range, int lightLook) {
        this.parent = in;
        this.world = this.parent.worldObj;
        this.range = range;
        this.lightTrigger = lightLook;
        this.cant = new ArrayList();
        this.setMutexBits(3);
    }

    public boolean shouldExecute() {
        ++this.ticks;
        if (!this.parent.getGeneMod(7)) {
            return false;
        }
        if (this.ticks < 40) {
            return false;
        }
        this.ticks = 0;
        if (!this.parent.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing") || this.parent.getAttackTarget() != null) {
            return false;
        }
        BlockPos ll = this.findSource();
        if (ll != null) {
            this.target = ll;
            return true;
        }
        return false;
    }

    public boolean continueExecuting() {
        return this.target != null && BlockStates.get(this.world, this.target).getBlock() == this.block && this.parent.getAttackTarget() == null;
    }

    public void startExecuting() {
        this.progressB = 0;
        this.idle = 0;
        this.block = BlockStates.get(this.world, this.target).getBlock();
        float multiplier = 10.0f;
        IBlockState state = BlockStates.get(this.world, this.target);
        this.neededTime = (int)(state.getBlockHardness(this.world, this.target) * Math.max(0.0f, multiplier));
    }

    public void resetTask() {
        this.parent.getNavigator().clearPathEntity();
        this.prevProgressB = -1;
        this.target = null;
    }

    public void updateTask() {
        double r = 5.0;
        double distance = this.realdistanceSq(this.target, this.parent.posX, this.parent.posY, this.parent.posZ);
        if (distance > r) {
            this.parent.getNavigator().tryMoveToXYZ((double)this.target.getX(), (double)this.target.getY(), (double)this.target.getZ(), 1.1);
        }
        if (this.parent.isRiding()) {
            this.resetTask();
            return;
        }
        ++this.idle;
        this.idle = (int)Math.round(distance) == this.disss ? ++this.idle : 0;
        if (this.idle == 120) {
            this.parent.skillBreakBlocks();
        }
        if (this.idle >= 240) {
            boolean canAdd = true;
            for (int p = 0; p < this.cant.size(); ++p) {
                if (this.cant.get(p).longValue() != this.target.toLong()) continue;
                canAdd = false;
            }
            if (canAdd) {
                this.cant.add(this.target.toLong());
            }
            this.resetTask();
            this.ticks += 30;
            return;
        }
        this.disss = (int)Math.round(distance);
        if (distance <= r && this.target != null) {
            this.parent.getNavigator().tryMoveToXYZ((double)this.target.getX(), (double)this.target.getY(), (double)this.target.getZ(), 0.0);
            ++this.progressB;
            this.idle = 0;
            int i = (int)((float)this.progressB / (float)this.neededTime);
            if (i != this.prevProgressB) {
                this.world.sendBlockBreakProgress(this.parent.getEntityId(), this.target, i);
                this.prevProgressB = i;
            }
            if (this.progressB >= this.neededTime) {
                WorldAccess.destroyBlock(this.world, this.target, true);
                this.progressB = 0;
                this.ticks += 30;
            }
        }
    }

    private BlockPos findSource() {
        if (this.world.getLightFor(EnumSkyBlock.BLOCK, EntityQueries.position(this.parent)) < this.lightTrigger && this.world.rand.nextInt(3) != 0) {
            return null;
        }
        double distanceM = 40000.0;
        BlockPos light = null;
        if (this.parent.isRiding()) {
            return light;
        }
        ArrayList<BlockPos> sources = new ArrayList<BlockPos>();
        for (int i = -this.range; i <= this.range; ++i) {
            int j = -4;
            while ((float)j <= this.parent.height) {
                for (int k = -this.range; k <= this.range; ++k) {
                    BlockPos atm = new BlockPos(this.parent.posX + (double)i, this.parent.posY + (double)j, this.parent.posZ + (double)k);
                    IBlockState state = BlockStates.get(this.world, atm);
                    Block block = state.getBlock();
                    if (block instanceof BlockLiquid || block instanceof BlockPortal || block instanceof BlockEndGateway || block instanceof BlockEndPortalFrame || block instanceof BlockBase || state.getMaterial() == Material.portal || ParasiteEventEntity.checkName(RegistryNames.get(block).toString(), SRPConfig.parasiteGriefingBlackList, SRPConfig.parasiteGriefingWhite) || state.getLightValue() < this.lightTrigger && state.getMaterial() != Material.circuits) continue;
                    if (this.cant.size() == 0) {
                        sources.add(atm);
                        continue;
                    }
                    boolean canAdd = true;
                    for (int p = 0; p < this.cant.size(); ++p) {
                        if (this.cant.get(p).longValue() != atm.toLong()) continue;
                        canAdd = false;
                        break;
                    }
                    if (!canAdd) continue;
                    sources.add(atm);
                }
                ++j;
            }
        }
        for (BlockPos pos : sources) {
            double distance = pos.distanceSq(this.parent.posX, this.parent.posY, this.parent.posZ);
            if (!(distance < distanceM)) continue;
            light = pos;
            distanceM = distance;
        }
        return light;
    }

    private double realdistanceSq(BlockPos pos, double toX, double toY, double toZ) {
        double d0 = (double)pos.getX() + 0.5 - toX;
        double d1 = (double)pos.getY() + 0.5 - toY;
        double d2 = (double)pos.getZ() + 0.5 - toZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }
}

