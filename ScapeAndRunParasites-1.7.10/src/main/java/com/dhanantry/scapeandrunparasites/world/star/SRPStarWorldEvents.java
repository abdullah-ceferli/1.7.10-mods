/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 *  net.minecraftforge.event.world.WorldEvent$Load
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.common.gameevent.TickEvent$WorldTickEvent
 */
package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import com.dhanantry.scapeandrunparasites.world.star.SRPStarWorldData;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class SRPStarWorldEvents {
    private static boolean creatingWorld = false;
    private static int pendingCreationStarType = 0;
    private static boolean pendingCreationMushroomTrees = false;
    private static boolean pendingCreationFracturedTerrain = false;

    public static void markCreatingWorld() {
        SRPStarWorldEvents.markCreatingWorld(SRPWorldEntitySpawner.starType, false, false);
    }

    public static void markCreatingWorld(int starType) {
        SRPStarWorldEvents.markCreatingWorld(starType, false, false);
    }

    public static void markCreatingWorld(int starType, boolean mushroomTreesEnabled) {
        SRPStarWorldEvents.markCreatingWorld(starType, mushroomTreesEnabled, false);
    }

    public static void markCreatingWorld(int starType, boolean mushroomTreesEnabled, boolean fracturedTerrainEnabled) {
        creatingWorld = true;
        pendingCreationStarType = SRPStarWorldEvents.sanitizeStarType(starType);
        pendingCreationMushroomTrees = pendingCreationStarType == 1 && mushroomTreesEnabled;
        pendingCreationFracturedTerrain = pendingCreationStarType == 1 && fracturedTerrainEnabled;
        SRPWorldEntitySpawner.starType = pendingCreationStarType;
    }

    public static int getActiveStarTypeForGeneration() {
        if (creatingWorld) {
            return pendingCreationStarType;
        }
        return SRPStarWorldEvents.sanitizeStarType(SRPWorldEntitySpawner.starType);
    }

    private static int sanitizeStarType(int starType) {
        if (starType < 0 || starType > 2) {
            return 0;
        }
        return starType;
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        World world = event.getWorld();
        if (world == null || world.isRemote || world.provider == null || world.provider.dimensionId != 0) {
            return;
        }
        SRPStarWorldData data = SRPStarWorldData.get(world);
        if (creatingWorld) {
            data.setStarType(pendingCreationStarType);
            data.setMushroomTreesEnabled(pendingCreationStarType == 1 && pendingCreationMushroomTrees);
            data.setFracturedTerrainEnabled(pendingCreationFracturedTerrain);
            SRPWorldEntitySpawner.starType = pendingCreationStarType;
            creatingWorld = false;
            pendingCreationMushroomTrees = false;
            pendingCreationFracturedTerrain = false;
        } else {
            SRPWorldEntitySpawner.starType = data.getStarType();
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        World world = event.world;
        if (world == null || world.isRemote || world.provider == null || world.provider.dimensionId != 0) {
            return;
        }
        if (creatingWorld) {
            SRPWorldEntitySpawner.starType = pendingCreationStarType;
            return;
        }
        SRPStarWorldData data = SRPStarWorldData.get(world);
        if (data.getStarType() != SRPWorldEntitySpawner.starType) {
            SRPWorldEntitySpawner.starType = data.getStarType();
        }
    }
}

