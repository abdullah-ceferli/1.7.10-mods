/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockRailBase
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLiving$SpawnPlacementType
 *  net.minecraft.entity.EntitySpawnPlacementRegistry
 *  net.minecraft.entity.IEntityLivingData
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.server.management.PlayerChunkMapEntry
 *  net.minecraft.util.EntitySelectors
 *  net.minecraft.util.WeightedRandom
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraft.world.biome.Biome$SpawnListEntry
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.fml.common.eventhandler.Event$Result
 */
package com.dhanantry.scapeandrunparasites.world;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSpawning;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.WeightedRandom;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ForgeEventFactory;
import cpw.mods.fml.common.eventhandler.Event;

public class SRPWorldParasiteSpawner {
    private static final Set<ChunkPos> eligibleChunksForSpawning = Sets.newHashSet();
    private static int lock = 0;
    private static int originC = 0;
    public static boolean triggerSPAWNING = false;
    public static int choiceNUMBER = 1;

    public static int findChunksForSpawning(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate) {
        if (!SRPConfigWorld.originActivated) {
            return SRPWorldParasiteSpawner.findChunksForSpawningVanilla(worldServerIn, spawnHostileMobs, spawnPeacefulMobs, spawnOnSetTickRate);
        }
        return SRPWorldParasiteSpawner.findChunksForSpawningOrigin(worldServerIn, spawnHostileMobs, spawnPeacefulMobs, spawnOnSetTickRate);
    }

    public static int findChunksForSpawningVanilla(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate) {
        if (!spawnHostileMobs && !spawnPeacefulMobs) {
            return 0;
        }
        if (!SRPSpawning.totalParasites) {
            if (++lock > 40) {
                SRPSpawning.totalParasites = true;
                lock = 0;
            }
            return 0;
        }
        eligibleChunksForSpawning.clear();
        for (EntityPlayer entityplayer : EntityQueries.players(worldServerIn)) {
            if (entityplayer.isSpectator()) continue;
            int j = MathHelper.floor_double((double)(entityplayer.posX / 16.0));
            int k = MathHelper.floor_double((double)(entityplayer.posZ / 16.0));
            for (int i1 = -8; i1 <= 8; ++i1) {
                for (int j1 = -8; j1 <= 8; ++j1) {
                    PlayerChunkMapEntry playerchunkmapentry;
                    boolean flag = i1 == -8 || i1 == 8 || j1 == -8 || j1 == 8;
                    ChunkPos chunkpos = new ChunkPos(i1 + j, j1 + k);
                    if (eligibleChunksForSpawning.contains(chunkpos) || flag || !worldServerIn.getWorldBorder().contains(chunkpos) || (playerchunkmapentry = worldServerIn.getPlayerChunkMap().getEntry(chunkpos.chunkXPos, chunkpos.chunkZPos)) == null || !playerchunkmapentry.isSentToPlayers()) continue;
                    eligibleChunksForSpawning.add(chunkpos);
                }
            }
        }
        int j4 = 0;
        BlockPos blockpos1 = worldServerIn.getSpawnPoint();
        ArrayList shuffled = Lists.newArrayList(eligibleChunksForSpawning);
        Collections.shuffle(shuffled);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        block5: for (ChunkPos chunkpos1 : shuffled) {
            BlockPos blockpos = SRPWorldParasiteSpawner.getRandomChunkPosition((World)worldServerIn, chunkpos1.chunkXPos, chunkpos1.chunkZPos);
            int k1 = blockpos.getX();
            int l1 = blockpos.getY();
            int i2 = blockpos.getZ();
            IBlockState iblockstate = BlockStates.get(worldServerIn, blockpos);
            if (iblockstate.isNormalCube()) continue;
            int j2 = 0;
            block6: for (int k2 = 0; k2 < 3; ++k2) {
                int l2 = k1;
                int i3 = l1;
                int j3 = i2;
                Biome.SpawnListEntry biome$spawnlistentry = null;
                IEntityLivingData ientitylivingdata = null;
                int l3 = MathHelper.ceiling_double_int((double)(Math.random() * 4.0));
                for (int i4 = 0; i4 < l3; ++i4) {
                    EntityParasiteBase entityliving;
                    EntityPlayer closest;
                    blockpos$mutableblockpos.setPos(l2 += worldServerIn.rand.nextInt(6) - worldServerIn.rand.nextInt(6), i3 += worldServerIn.rand.nextInt(1) - worldServerIn.rand.nextInt(1), j3 += worldServerIn.rand.nextInt(6) - worldServerIn.rand.nextInt(6));
                    float f = (float)l2 + 0.5f;
                    float f1 = (float)j3 + 0.5f;
                    if (worldServerIn.isAnyPlayerWithinRangeAt((double)f, (double)i3, (double)f1, 24.0) || !(blockpos1.distanceSq((double)f, (double)i3, (double)f1) >= 576.0)) continue;
                    if (biome$spawnlistentry == null && (biome$spawnlistentry = SRPWorldParasiteSpawner.getSpawnListEntryForTypeAt(worldServerIn, (BlockPos)blockpos$mutableblockpos)) == null) continue block6;
                    EntityLiving.SpawnPlacementType ground = EntitySpawnPlacementRegistry.getPlacementForEntity((Class)biome$spawnlistentry.entityClass);
                    if (ground == EntityLiving.SpawnPlacementType.IN_AIR && (closest = SRPWorldParasiteSpawner.getClosestPlayer(f, i3, f1, 24.0, (World)worldServerIn)) != null) {
                        double base = closest.posY;
                        double randomOff = worldServerIn.rand.nextInt(21) - 10;
                        base = Math.max(base, (double)worldServerIn.getWorldInfo().getTerrainType().getMinimumSpawnHeight((World)worldServerIn) / 2.0);
                        base = Math.min(base, (double)SRPConfigWorld.spawnerSKYLimitUp);
                        blockpos$mutableblockpos.setPos((double)l2, base + randomOff, (double)j3);
                    }
                    if (!SRPWorldParasiteSpawner.canCreatureTypeSpawnAtLocation(ground, (World)worldServerIn, (BlockPos)blockpos$mutableblockpos)) continue;
                    try {
                        entityliving = (EntityParasiteBase)biome$spawnlistentry.newInstance((World)worldServerIn);
                        entityliving.canSpawnSpawn = true;
                    }
                    catch (Exception exception) {
                        return j4;
                    }
                    entityliving.setLocationAndAngles(f, i3, f1, worldServerIn.rand.nextFloat() * 360.0f, 0.0f);
                    Event.Result canSpawn = ForgeEventFactory.canEntitySpawn((EntityLiving)entityliving, (World)worldServerIn, (float)f, (float)i3, (float)f1, (boolean)false);
                    if (canSpawn == Event.Result.ALLOW || canSpawn == Event.Result.DEFAULT && entityliving.getCanSpawnHere()) {
                        if (!ForgeEventFactory.doSpecialSpawn((EntityLiving)entityliving, (World)worldServerIn, (float)f, (float)i3, (float)f1)) {
                            ientitylivingdata = entityliving.onInitialSpawn(DifficultyAccess.at(worldServerIn, EntityQueries.position(entityliving)), ientitylivingdata);
                        }
                        if (entityliving.handleLavaMovement()) {
                            ++j2;
                            worldServerIn.spawnEntityInWorld((Entity)entityliving);
                        } else {
                            entityliving.setDead();
                        }
                        if (j2 >= ForgeEventFactory.getMaxSpawnPackSize((EntityLiving)entityliving)) continue block5;
                    }
                    j4 += j2;
                }
            }
        }
        return j4;
    }

    public static int findChunksForSpawningOrigin(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate) {
        if (!spawnHostileMobs && !spawnPeacefulMobs) {
            return 0;
        }
        if (!SRPSpawning.totalParasites) {
            if (++lock > 7) {
                SRPSpawning.totalParasites = true;
                lock = 0;
            }
            return 0;
        }
        eligibleChunksForSpawning.clear();
        SRPWorldData worldData = SRPWorldData.get((World)worldServerIn);
        SRPSaveData saveData225 = SRPSaveData.get((World)worldServerIn, 225);
        SRPSaveData saveData72 = SRPSaveData.get((World)worldServerIn, 72);
        boolean originsExist = worldData != null && !worldData.getorigins("x").isEmpty();
        boolean originlessAllowed = saveData225 != null && saveData225.getDeveLevel() >= SRPConfigSystems.deveOriginlessUse;
        for (EntityPlayer entityplayer : EntityQueries.players(worldServerIn)) {
            if (entityplayer.isSpectator()) continue;
            int j = MathHelper.floor_double((double)(entityplayer.posX / 16.0));
            int k = MathHelper.floor_double((double)(entityplayer.posZ / 16.0));
            for (int i1 = -8; i1 <= 8; ++i1) {
                for (int j1 = -8; j1 <= 8; ++j1) {
                    PlayerChunkMapEntry playerchunkmapentry;
                    boolean flag = i1 == -8 || i1 == 8 || j1 == -8 || j1 == 8;
                    ChunkPos chunkpos = new ChunkPos(i1 + j, j1 + k);
                    if (eligibleChunksForSpawning.contains(chunkpos) || flag || !worldServerIn.getWorldBorder().contains(chunkpos) || (playerchunkmapentry = worldServerIn.getPlayerChunkMap().getEntry(chunkpos.chunkXPos, chunkpos.chunkZPos)) == null || !playerchunkmapentry.isSentToPlayers()) continue;
                    eligibleChunksForSpawning.add(chunkpos);
                }
            }
        }
        SRPWorldParasiteSpawner.filterEligibleChunksForOrigin(worldData, originsExist, originlessAllowed);
        if (eligibleChunksForSpawning.isEmpty()) {
            return 0;
        }
        int j4 = 0;
        BlockPos blockpos1 = worldServerIn.getSpawnPoint();
        ArrayList shuffled = Lists.newArrayList(eligibleChunksForSpawning);
        Collections.shuffle(shuffled);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        block5: for (ChunkPos chunkpos1 : shuffled) {
            BlockPos blockpos = SRPWorldParasiteSpawner.getRandomChunkPosition((World)worldServerIn, chunkpos1.chunkXPos, chunkpos1.chunkZPos);
            int k1 = blockpos.getX();
            int l1 = blockpos.getY();
            int i2 = blockpos.getZ();
            IBlockState iblockstate = BlockStates.get(worldServerIn, blockpos);
            if (iblockstate.isNormalCube()) continue;
            int j2 = 0;
            block6: for (int k2 = 0; k2 < 3; ++k2) {
                int l2 = k1;
                int i3 = l1;
                int j3 = i2;
                Biome.SpawnListEntry biome$spawnlistentry = null;
                IEntityLivingData ientitylivingdata = null;
                int l3 = MathHelper.ceiling_double_int((double)(Math.random() * 4.0));
                for (int i4 = 0; i4 < l3; ++i4) {
                    EntityParasiteBase entityliving;
                    blockpos$mutableblockpos.setPos(l2 += worldServerIn.rand.nextInt(6) - worldServerIn.rand.nextInt(6), i3 += worldServerIn.rand.nextInt(1) - worldServerIn.rand.nextInt(1), j3 += worldServerIn.rand.nextInt(6) - worldServerIn.rand.nextInt(6));
                    float f = (float)l2 + 0.5f;
                    float f1 = (float)j3 + 0.5f;
                    if (worldServerIn.isAnyPlayerWithinRangeAt((double)f, (double)i3, (double)f1, 24.0) || !(blockpos1.distanceSq((double)f, (double)i3, (double)f1) >= 576.0)) continue;
                    if (biome$spawnlistentry == null && (biome$spawnlistentry = SRPWorldParasiteSpawner.getSpawnListEntryForTypeAtOrigin(worldServerIn, (BlockPos)blockpos$mutableblockpos, saveData72, worldData, originsExist, originlessAllowed)) == null) continue block6;
                    EntityLiving.SpawnPlacementType ground = EntitySpawnPlacementRegistry.getPlacementForEntity((Class)biome$spawnlistentry.entityClass);
                    if (ground == EntityLiving.SpawnPlacementType.IN_AIR) {
                        if (worldServerIn.rand.nextDouble() <= 0.7) continue;
                        EntityPlayer closest = SRPWorldParasiteSpawner.getClosestPlayer(f, i3, f1, 24.0, (World)worldServerIn);
                        if (closest != null) {
                            double base = closest.posY;
                            double randomOff = worldServerIn.rand.nextInt(21) - 10;
                            base = Math.max(base, (double)worldServerIn.getWorldInfo().getTerrainType().getMinimumSpawnHeight((World)worldServerIn) / 2.0);
                            base = Math.min(base, (double)SRPConfigWorld.spawnerSKYLimitUp);
                            blockpos$mutableblockpos.setPos((double)l2, base + randomOff, (double)j3);
                        }
                    }
                    if (!SRPWorldParasiteSpawner.canCreatureTypeSpawnAtLocation(ground, (World)worldServerIn, (BlockPos)blockpos$mutableblockpos)) continue;
                    try {
                        entityliving = (EntityParasiteBase)biome$spawnlistentry.newInstance((World)worldServerIn);
                        entityliving.canSpawnSpawn = true;
                    }
                    catch (Exception exception) {
                        return j4;
                    }
                    entityliving.setLocationAndAngles(f, i3, f1, worldServerIn.rand.nextFloat() * 360.0f, 0.0f);
                    entityliving.setLocationAndAngles(f, blockpos$mutableblockpos.getY(), f1, worldServerIn.rand.nextFloat() * 360.0f, 0.0f);
                    Event.Result canSpawn = ForgeEventFactory.canEntitySpawn((EntityLiving)entityliving, (World)worldServerIn, (float)f, (float)i3, (float)f1, (boolean)false);
                    if (canSpawn == Event.Result.ALLOW || canSpawn == Event.Result.DEFAULT && entityliving.getCanSpawnHere()) {
                        if (!ForgeEventFactory.doSpecialSpawn((EntityLiving)entityliving, (World)worldServerIn, (float)f, (float)i3, (float)f1)) {
                            ientitylivingdata = entityliving.onInitialSpawn(DifficultyAccess.at(worldServerIn, EntityQueries.position(entityliving)), ientitylivingdata);
                        }
                        if (entityliving.handleLavaMovement()) {
                            ++j2;
                            worldServerIn.spawnEntityInWorld((Entity)entityliving);
                        } else {
                            entityliving.setDead();
                        }
                        if (j2 >= ForgeEventFactory.getMaxSpawnPackSize((EntityLiving)entityliving)) continue block5;
                    }
                    j4 += j2;
                }
            }
        }
        return j4;
    }

    @Nullable
    private static EntityPlayer getClosestPlayer(double x, double y, double z, double distance, World world) {
        double d0 = -1.0;
        EntityPlayer entityplayer = null;
        List list = EntityQueries.players(world);
        for (EntityPlayer entityPlayer : list) {
            double d1;
            if (!EntitySelectors.NOT_SPECTATING.apply((Object)entityPlayer) || !((d1 = entityPlayer.getDistanceSq(x, y, z)) > distance * distance) || d0 != -1.0 && !(d1 < d0)) continue;
            d0 = d1;
            entityplayer = entityPlayer;
        }
        return entityplayer;
    }

    private static BlockPos getRandomChunkPosition(World worldIn, int x, int z) {
        Chunk chunk = worldIn.getChunkFromChunkCoords(x, z);
        int i = x * 16 + worldIn.rand.nextInt(16);
        int j = z * 16 + worldIn.rand.nextInt(16);
        int k = MathHelper.roundUp((int)(WorldAccess.getHeight(chunk, new BlockPos(i, 0, j)) + 1), (int)16);
        int l = worldIn.rand.nextInt(Math.max(1, k > 0 ? k : chunk.getTopFilledSegment() + 16 - 1));
        return new BlockPos(i, l, j);
    }

    public static boolean canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType spawnPlacementTypeIn, World worldIn, BlockPos pos) {
        if (!worldIn.getWorldBorder().contains(pos)) {
            return false;
        }
        return SRPWorldParasiteSpawner.canCreatureTypeSpawnBody(spawnPlacementTypeIn, worldIn, pos);
    }

    public static boolean canCreatureTypeSpawnBody(EntityLiving.SpawnPlacementType spawnPlacementTypeIn, World worldIn, BlockPos pos) {
        IBlockState iblockstate = BlockStates.get(worldIn, pos);
        if (spawnPlacementTypeIn == EntityLiving.SpawnPlacementType.IN_WATER) {
            return iblockstate.getMaterial() == Material.water && BlockStates.get(worldIn, pos.down()).getMaterial() == Material.water && !BlockStates.get(worldIn, pos.up()).isNormalCube();
        }
        if (spawnPlacementTypeIn == EntityLiving.SpawnPlacementType.IN_AIR) {
            return iblockstate.getBlock() == Blocks.air && BlockStates.get(worldIn, pos.down()).getBlock() == Blocks.air && BlockStates.get(worldIn, pos.up()).getBlock() == Blocks.air;
        }
        BlockPos blockpos = pos.down();
        IBlockState state = BlockStates.get(worldIn, blockpos);
        if (!state.getBlock().canCreatureSpawn(state, (IBlockAccess)worldIn, blockpos, spawnPlacementTypeIn)) {
            return false;
        }
        Block block = BlockStates.get(worldIn, blockpos).getBlock();
        boolean flag = block != Blocks.bedrock && block != Blocks.BARRIER;
        return flag && SRPWorldParasiteSpawner.isValidEmptySpawnBlock(iblockstate) && SRPWorldParasiteSpawner.isValidEmptySpawnBlock(BlockStates.get(worldIn, pos.up()));
    }

    public static boolean isValidEmptySpawnBlock(IBlockState state) {
        if (state.isBlockNormalCube()) {
            return false;
        }
        if (state.canProvidePower()) {
            return false;
        }
        if (state.getMaterial().isLiquid()) {
            return false;
        }
        return !BlockRailBase.isRailBlock((IBlockState)state);
    }

    private static void filterEligibleChunksForOrigin(SRPWorldData data, boolean originsExist, boolean originlessAllowed) {
        if (!originsExist || originlessAllowed || data == null || eligibleChunksForSpawning.isEmpty()) {
            return;
        }
        ArrayList<Integer> originsX = data.getorigins("x");
        ArrayList<Integer> originsZ = data.getorigins("z");
        ArrayList<Integer> originsA = data.getorigins("a");
        int originCount = Math.min(originsX.size(), Math.min(originsZ.size(), originsA.size()));
        ArrayList<Integer> coloniesX = data.getColonies("x");
        ArrayList<Integer> coloniesY = data.getColonies("y");
        ArrayList<Integer> coloniesZ = data.getColonies("z");
        int colonyCount = Math.min(coloniesX.size(), Math.min(coloniesY.size(), coloniesZ.size()));
        int[] colonyRadii = new int[colonyCount];
        for (int i = 0; i < colonyCount; ++i) {
            BlockPos colonyPos = new BlockPos(coloniesX.get(i).intValue(), coloniesY.get(i).intValue(), coloniesZ.get(i).intValue());
            colonyRadii[i] = Math.max(0, data.getColonyDistanceSpreadByPosition(colonyPos, false));
        }
        Iterator<ChunkPos> chunkIterator = eligibleChunksForSpawning.iterator();
        while (chunkIterator.hasNext()) {
            int i;
            ChunkPos chunkPos = chunkIterator.next();
            boolean inRange = false;
            for (i = 0; i < originCount; ++i) {
                if (!SRPWorldParasiteSpawner.chunkIntersectsRadius2D(chunkPos.chunkXPos, chunkPos.chunkZPos, originsX.get(i), originsZ.get(i), originsA.get(i))) continue;
                inRange = true;
                break;
            }
            if (!inRange) {
                for (i = 0; i < colonyCount; ++i) {
                    if (!SRPWorldParasiteSpawner.chunkIntersectsRadius2D(chunkPos.chunkXPos, chunkPos.chunkZPos, coloniesX.get(i), coloniesZ.get(i), colonyRadii[i])) continue;
                    inRange = true;
                    break;
                }
            }
            if (inRange) continue;
            chunkIterator.remove();
        }
    }

    private static boolean chunkIntersectsRadius2D(int chunkX, int chunkZ, int centerX, int centerZ, int radius) {
        long radiusSq;
        int closestZ;
        long dz;
        if (radius <= 0) {
            return false;
        }
        int minX = chunkX << 4;
        int maxX = minX + 15;
        int minZ = chunkZ << 4;
        int maxZ = minZ + 15;
        int closestX = Math.max(minX, Math.min(centerX, maxX));
        long dx = (long)centerX - (long)closestX;
        return dx * dx + (dz = (long)centerZ - (long)(closestZ = Math.max(minZ, Math.min(centerZ, maxZ)))) * dz <= (radiusSq = (long)radius * (long)radius);
    }

    @Nullable
    public static Biome.SpawnListEntry getSpawnListEntryForTypeAt(WorldServer worldServerIn, BlockPos pos) {
        SRPSaveData dat = SRPSaveData.get((World)worldServerIn, 72);
        int id = worldServerIn.provider.dimensionId;
        if (dat == null) {
            return null;
        }
        if (SRPConfigWorld.originActivated) {
            SRPWorldData data = SRPWorldData.get((World)worldServerIn);
            boolean originsExist = data != null && !data.getorigins("x").isEmpty();
            SRPSaveData saveData225 = SRPSaveData.get((World)worldServerIn, 225);
            boolean originlessAllowed = saveData225 != null && saveData225.getDeveLevel() >= SRPConfigSystems.deveOriginlessUse;
            return SRPWorldParasiteSpawner.getSpawnListEntryForTypeAtOrigin(worldServerIn, pos, dat, data, originsExist, originlessAllowed);
        }
        List<Biome.SpawnListEntry> list = SRPSpawning.getSpawns((World)worldServerIn, id, dat.getEvolutionPhase(id), dat);
        Biome.SpawnListEntry chosen = list != null && !list.isEmpty() ? (Biome.SpawnListEntry)WeightedRandom.getRandomItem((Random)worldServerIn.rand, list) : null;
        return chosen;
    }

    @Nullable
    private static Biome.SpawnListEntry getSpawnListEntryForTypeAtOrigin(WorldServer worldServerIn, BlockPos pos, SRPSaveData dat, SRPWorldData data, boolean originsExist, boolean originlessAllowed) {
        int id = worldServerIn.provider.dimensionId;
        if (!SRPWorldParasiteSpawner.isPosWithinOrigin((World)worldServerIn, pos, data, originsExist, originlessAllowed)) {
            return null;
        }
        byte phase = dat.getEvolutionPhase(id);
        if (phase == -1) {
            if (!originsExist) {
                return null;
            }
            List<Biome.SpawnListEntry> list = SRPSpawning.getSpawns((World)worldServerIn, id, phase, dat);
            Biome.SpawnListEntry chosen = list != null && !list.isEmpty() ? (Biome.SpawnListEntry)WeightedRandom.getRandomItem((Random)worldServerIn.rand, list) : null;
            return chosen;
        }
        byte phaseToUse = originsExist ? phase : (byte)0;
        List<Biome.SpawnListEntry> list = SRPSpawning.getSpawns((World)worldServerIn, id, phaseToUse, dat);
        Biome.SpawnListEntry chosen = list != null && !list.isEmpty() ? (Biome.SpawnListEntry)WeightedRandom.getRandomItem((Random)worldServerIn.rand, list) : null;
        return chosen;
    }

    private static boolean isPosWithinOrigin(World world, BlockPos pos, SRPWorldData data, boolean originsExist, boolean originlessAllowed) {
        if (world.getBiome(pos) instanceof BiomeParasiteBase) {
            return true;
        }
        if (data == null) {
            return false;
        }
        int a = data.nearestInfectionValue(pos, false);
        if (data.nearestColonyPosition(pos, true) != null) {
            return true;
        }
        return a > 0 || originsExist && originlessAllowed || !originsExist && !data.getTriggerMet();
    }

    private static String posToString(BlockPos pos) {
        return "(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
    }
}

