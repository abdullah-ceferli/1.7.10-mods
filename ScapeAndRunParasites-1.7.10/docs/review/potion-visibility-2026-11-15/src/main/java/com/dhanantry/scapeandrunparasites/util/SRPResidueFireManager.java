/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.event.world.WorldEvent$Unload
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.common.gameevent.TickEvent$WorldTickEvent
 */
package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public final class SRPResidueFireManager {
    private static final Map<Integer, Map<BlockPos, Integer>> TRACK = new HashMap<Integer, Map<BlockPos, Integer>>();

    private SRPResidueFireManager() {
    }

    public static void lightAndTrack(World w, BlockPos pos, int ttl) {
        if (w.isRemote) {
            return;
        }
        if (ttl <= 0) {
            return;
        }
        if (WorldAccess.isAirBlock(w, pos)) {
            BlockStates.set(w, pos, BlockStates.defaultState(Blocks.fire), 3);
        }
        TRACK.computeIfAbsent(w.provider.dimensionId, d -> new HashMap()).put(pos.toImmutable(), ttl);
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent e) {
        if (e.phase != TickEvent.Phase.END || e.world.isRemote) {
            return;
        }
        int dim = e.world.provider.dimensionId;
        Map<BlockPos, Integer> m = TRACK.get(dim);
        if (m == null || m.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<BlockPos, Integer>> it = m.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Integer> en = it.next();
            int left = en.getValue() - 1;
            if (left <= 0) {
                BlockPos p = en.getKey();
                if (BlockStates.get(e.world, p).getBlock() == Blocks.fire) {
                    WorldAccess.setBlockToAir(e.world, p);
                }
                it.remove();
                continue;
            }
            en.setValue(left);
        }
        if (m.isEmpty()) {
            TRACK.remove(dim);
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload e) {
        if (e.getWorld().isRemote) {
            return;
        }
        TRACK.remove(e.getWorld().provider.dimensionId);
    }
}

