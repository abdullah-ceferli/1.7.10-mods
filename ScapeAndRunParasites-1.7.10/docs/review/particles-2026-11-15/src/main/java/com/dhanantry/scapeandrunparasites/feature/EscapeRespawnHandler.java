/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.PlayerEvent$PlayerRespawnEvent
 */
package com.dhanantry.scapeandrunparasites.feature;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.feature.EscapeOnDeathHandler;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayerMP;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class EscapeRespawnHandler {
    private static final String PERSIST_TAG = "PlayerPersisted";
    private static final String PENDING_TAG = "srp_escape_pending";

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if (e.player.worldObj.isRemote) {
            return;
        }
        if (!SRPConfigWorld.escapeEnabled) {
            return;
        }
        EntityPlayerMP p = (EntityPlayerMP)e.player;
        boolean pending = p.getEntityData().getCompoundTag(PERSIST_TAG).getBoolean(PENDING_TAG);
        if (!pending) {
            return;
        }
        p.getEntityData().getCompoundTag(PERSIST_TAG).setBoolean(PENDING_TAG, false);
        EscapeOnDeathHandler.clearOffer(p);
        BlockPos origin = EntityQueries.position(p);
        int min = Math.max(0, SRPConfigWorld.escapeMinDistance);
        int max = Math.max(min, SRPConfigWorld.escapeMaxDistance);
        BlockPos target = EscapeRespawnHandler.findSafeRandom((WorldServer)p.worldObj, origin, min, max, 24);
        if (target != null) {
            p.playerNetServerHandler.setPlayerLocation((double)target.getX() + 0.5, (double)target.getY(), (double)target.getZ() + 0.5, p.rotationYaw, p.rotationPitch);
        }
    }

    private static BlockPos findSafeRandom(WorldServer world, BlockPos origin, int min, int max, int tries) {
        Random r = world.rand;
        for (int i = 0; i < tries; ++i) {
            int dz;
            double ang = r.nextDouble() * Math.PI * 2.0;
            int dist = min + r.nextInt(Math.max(1, max - min + 1));
            int dx = origin.getX() + (int)Math.round(Math.cos(ang) * (double)dist);
            BlockPos top = WorldAccess.getTopSolidOrLiquidBlock(world, new BlockPos(dx, 0, dz = origin.getZ() + (int)Math.round(Math.sin(ang) * (double)dist)));
            BlockPos solid = EscapeRespawnHandler.descendToSolid(world, top);
            if (solid == null || !EscapeRespawnHandler.isSafe(world, solid)) continue;
            return solid.up();
        }
        return null;
    }

    private static BlockPos descendToSolid(WorldServer w, BlockPos start) {
        BlockPos pos = start;
        for (int i = 0; i < 16; ++i) {
            Material m = BlockStates.get(w, pos).getMaterial();
            if (m.isSolid()) {
                return pos;
            }
            if ((pos = pos.down()).getY() <= 4) break;
        }
        return null;
    }

    private static boolean isSafe(WorldServer w, BlockPos solid) {
        if (solid == null) {
            return false;
        }
        Material m = BlockStates.get(w, solid).getMaterial();
        if (!m.isSolid() || m == Material.leaves) {
            return false;
        }
        BlockPos feet = solid.up();
        BlockPos head = feet.up();
        if (!WorldAccess.isAirBlock(w, feet) || !WorldAccess.isAirBlock(w, head)) {
            return false;
        }
        if (BlockStates.get(w, feet).getMaterial().isLiquid()) {
            return false;
        }
        return !BlockStates.get(w, head).getMaterial().isLiquid();
    }
}

