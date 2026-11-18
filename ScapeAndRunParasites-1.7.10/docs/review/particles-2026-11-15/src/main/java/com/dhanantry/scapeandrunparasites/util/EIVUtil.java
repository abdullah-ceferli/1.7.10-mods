/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import net.minecraft.entity.player.EntityPlayer;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class EIVUtil {
    public static void createRandomOrigin(World worldIn, int min, int max) {
        if (!SRPConfigWorld.originActivated) {
            return;
        }
        if (SRPWorldEntitySpawner.triggerSPAWNING) {
            return;
        }
        SRPWorldData data = SRPWorldData.get(worldIn);
        int number = data.getorigins("x").size();
        if (number == 0 ? worldIn.rand.nextInt(SRPConfigWorld.originCreatingRandZero) != 0 : worldIn.rand.nextInt(SRPConfigWorld.originCreatingRand) != 0) {
            return;
        }
        if (EntityQueries.players(worldIn).isEmpty()) {
            return;
        }
        EntityPlayer player = (EntityPlayer)EntityQueries.players(worldIn).get(worldIn.rand.nextInt(EntityQueries.players(worldIn).size()));
        double distance = max <= min ? (double)min : (double)min + worldIn.rand.nextDouble() * (double)(max - min);
        double angle = worldIn.rand.nextDouble() * Math.PI * 2.0;
        int dx = (int)Math.round(Math.cos(angle) * distance);
        int dz = (int)Math.round(Math.sin(angle) * distance);
        BlockPos pos = EntityQueries.position(player).add(dx, 64, dz);
        int key = data.setOrigin(worldIn, pos.getX(), pos.getY(), pos.getZ(), SRPConfigWorld.originHealth, SRPConfigWorld.originRadius);
        if (key == 1 && !SRPConfigWorld.originNewMess.isEmpty()) {
            ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigWorld.originNewMess, 400);
        }
        if (key == 2 && !SRPConfigWorld.originNewOutbreakMess.isEmpty()) {
            ParasiteEventEntity.alertAllPlayerSer(worldIn, SRPConfigWorld.originNewOutbreakMess, 401);
        }
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        double trueDistance = player.getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
        SRPMain.logger.debug("[EIV DEBUG] RANDOM_ORIGIN result. Player={} playerPos={} originPos={} dx={} dz={} horizontalDistance={} trueDistance={} min={} max={} health={} radius={} resultKey={} totalOrigins={}", new Object[]{player.getCommandSenderName(), EntityQueries.position(player), pos, dx, dz, String.format("%.2f", horizontalDistance), String.format("%.2f", trueDistance), min, max, SRPConfigWorld.originHealth, SRPConfigWorld.originRadius, key, data.getorigins("x").size()});
    }
}

