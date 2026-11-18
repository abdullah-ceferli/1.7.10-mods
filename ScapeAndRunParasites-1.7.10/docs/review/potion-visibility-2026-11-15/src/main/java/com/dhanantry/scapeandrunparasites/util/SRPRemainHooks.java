/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.effect.EntityLightningBolt
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.event.entity.EntityJoinWorldEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockInfestedRemain;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.effect.EntityLightningBolt;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public final class SRPRemainHooks {
    private SRPRemainHooks() {
    }

    @SubscribeEvent
    public static void onLightningSpawn(EntityJoinWorldEvent e) {
        if (!(e.getEntity() instanceof EntityLightningBolt)) {
            return;
        }
        World w = e.getWorld();
        if (w.isRemote) {
            return;
        }
        BlockPos strike = e.getEntity().getPosition();
        int r = 5;
        BlockPos.MutableBlockPos cur = new BlockPos.MutableBlockPos();
        for (int dx = -r; dx <= r; ++dx) {
            for (int dy = -r; dy <= r; ++dy) {
                for (int dz = -r; dz <= r; ++dz) {
                    cur.setPos(strike.getX() + dx, strike.getY() + dy, strike.getZ() + dz);
                    IBlockState s = BlockStates.get(w, (BlockPos)cur);
                    if (!(s.getBlock() instanceof BlockInfestedRemain)) continue;
                    WorldAccess.setBlockToAir(w, (BlockPos)cur);
                }
            }
        }
    }
}

