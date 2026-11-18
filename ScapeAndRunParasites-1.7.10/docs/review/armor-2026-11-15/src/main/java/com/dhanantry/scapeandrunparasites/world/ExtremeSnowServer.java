/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockSnow
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.common.gameevent.TickEvent$WorldTickEvent
 *  net.minecraftforge.fml.relauncher.Side
 */
package com.dhanantry.scapeandrunparasites.world;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.world.ExtremeSnowData;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.BlockSnow;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid="srparasites", value={Side.SERVER})
public final class ExtremeSnowServer {
    private ExtremeSnowServer() {
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        World w = e.world;
        ExtremeSnowData data = ExtremeSnowData.get(w);
        if (!data.isEnabled()) {
            return;
        }
        List players = w.getMinecraftServer() != null ? w.getMinecraftServer().getPlayerList().getPlayers() : Collections.emptyList();
        int triesPerPlayer = (int)(12.0f + 48.0f * data.getIntensity());
        int radius = 18;
        for (EntityPlayerMP p : players) {
            if (p.worldObj != w) continue;
            for (int i = 0; i < triesPerPlayer; ++i) {
                int x = (int)(p.posX + (double)(w.rand.nextInt(radius * 2 + 1) - radius));
                int z = (int)(p.posZ + (double)(w.rand.nextInt(radius * 2 + 1) - radius));
                BlockPos base = new BlockPos(x, (int)p.posY, z);
                BlockPos hit = WorldAccess.getPrecipitationHeight(w, base);
                boolean force = ExtremeSnowData.get(w).isForceAnywhere();
                if (!WorldAccess.canSeeSky(w, hit) || !force && !WorldAccess.canSnowAt(w, hit, false)) continue;
                IBlockState stateAt = BlockStates.get(w, hit);
                if (stateAt.getBlock() == Blocks.snow_layer) {
                    int layers = (Integer)stateAt.getValue((IProperty)BlockSnow.LAYERS);
                    if (layers >= 8) continue;
                    BlockStates.set(w, hit, stateAt.withProperty((IProperty)BlockSnow.LAYERS, (Comparable)Integer.valueOf(layers + 1)), 2);
                    continue;
                }
                if (!stateAt.getBlock().isAir(stateAt, (IBlockAccess)w, hit)) continue;
                BlockStates.set(w, hit, BlockStates.defaultState(Blocks.snow_layer).withProperty((IProperty)BlockSnow.LAYERS, (Comparable)Integer.valueOf(1)), 2);
            }
        }
    }
}

