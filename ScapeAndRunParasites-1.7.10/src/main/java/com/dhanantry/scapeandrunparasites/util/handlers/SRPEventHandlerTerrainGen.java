/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.IGrowable
 *  net.minecraftforge.event.terraingen.SaplingGrowTreeEvent
 *  net.minecraftforge.fml.common.eventhandler.Event$Result
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import net.minecraft.block.IGrowable;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class SRPEventHandlerTerrainGen {
    @SubscribeEvent
    public void allowTreeGrowthTick(SaplingGrowTreeEvent event) {
        if (!(BlockStates.get(event.getWorld(), event.getPos()).getBlock() instanceof IGrowable)) {
            return;
        }
        if (BlockStates.get(event.getWorld(), event.getPos()).getBlock() == SRPBlocks.ParasiteSapling) {
            return;
        }
        int heart = ParasiteEventWorld.canBiomeStillExist(event.getWorld(), event.getPos(), false);
        switch (heart) {
            case 1: {
                if (!(event.getWorld().rand.nextDouble() < (double)SRPConfigWorld.nodeCropStopNodeOne)) break;
                event.setResult(Event.Result.DENY);
                break;
            }
            case 2: {
                if (!(event.getWorld().rand.nextDouble() < (double)SRPConfigWorld.nodeCropStopNodeTwo)) break;
                event.setResult(Event.Result.DENY);
                break;
            }
            case 3: {
                if (!(event.getWorld().rand.nextDouble() < (double)SRPConfigWorld.nodeCropStopNodeThree)) break;
                event.setResult(Event.Result.DENY);
                break;
            }
        }
    }
}

