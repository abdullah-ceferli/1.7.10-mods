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
 *  net.minecraft.init.MobEffects
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.server.management.PlayerChunkMapEntry
 *  net.minecraft.util.WeightedRandom
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraft.world.biome.Biome$SpawnListEntry
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.fml.common.eventhandler.Event$Result
 */
package com.dhanantry.scapeandrunparasites.world;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.SRPMain;
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
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.WeightedRandom;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.MathHelper;
import com.dhanantry.scapeandrunparasites.backport.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.ForgeEventFactory;
import cpw.mods.fml.common.eventhandler.Event;

public class SRPWorldEntitySpawner {
    private static final Set<ChunkPos> eligibleChunksForSpawning = Sets.newHashSet();
    private static int lock = 0;
    public static boolean triggerSPAWNING = false;
    public static int choiceNUMBER = 1;
    public static final int STAR_NORMAL = 0;
    public static final int STAR_COLD = 1;
    public static final int STAR_WARM = 2;
    public static int starType = 0;

    private static void debugSpawn(String msg) {
        if (SRPConfigSystems.debugSpawner) {
            SRPMain.logger.info("[SRP Spawner] " + msg);
        }
    }

    private static String posToString(BlockPos pos) {
        return "(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
    }

    @Nullable
    private static String getSpawnFailureReason(EntityLiving.SpawnPlacementType spawnPlacementTypeIn, World worldIn, BlockPos pos) {
        if (!worldIn.getWorldBorder().contains(pos)) {
            return "outside world border";
        }
        IBlockState iblockstate = BlockStates.get(worldIn, pos);
        if (spawnPlacementTypeIn == EntityLiving.SpawnPlacementType.IN_WATER) {
            if (iblockstate.getMaterial() != Material.water) {
                return "spawn block is not water";
            }
            if (BlockStates.get(worldIn, pos.down()).getMaterial() != Material.water) {
                return "block below is not water";
            }
            if (BlockStates.get(worldIn, pos.up()).getMaterial() != Material.water) {
                return "block above is not water";
            }
            return null;
        }
        if (spawnPlacementTypeIn == EntityLiving.SpawnPlacementType.IN_AIR) {
            if (iblockstate.getBlock() != Blocks.air) {
                return "spawn block is not air";
            }
            if (BlockStates.get(worldIn, pos.up()).isNormalCube()) {
                return "block above is solid";
            }
            return null;
        }
        BlockPos blockpos = pos.down();
        IBlockState state = BlockStates.get(worldIn, blockpos);
        if (!state.getBlock().canCreatureSpawn(state, (IBlockAccess)worldIn, blockpos, spawnPlacementTypeIn)) {
            return "block below does not allow creature spawn: " + RegistryNames.get(state.getBlock());
        }
        Block block = state.getBlock();
        if (block == Blocks.bedrock || block == Blocks.BARRIER) {
            return "block below is forbidden: " + RegistryNames.get(block);
        }
        if (!SRPWorldEntitySpawner.isValidEmptySpawnBlock(iblockstate)) {
            return "spawn block is not empty/valid";
        }
        if (!SRPWorldEntitySpawner.isValidEmptySpawnBlock(BlockStates.get(worldIn, pos.up()))) {
            return "block above is not empty/valid";
        }
        return null;
    }

    public static int findChunksForSpawning(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate) {
        if (!SRPConfigWorld.originActivated) {
            return SRPWorldEntitySpawner.findChunksForSpawningVanilla(worldServerIn, spawnHostileMobs, spawnPeacefulMobs, spawnOnSetTickRate);
        }
        return SRPWorldEntitySpawner.findChunksForSpawningOrigin(worldServerIn, spawnHostileMobs, spawnPeacefulMobs, spawnOnSetTickRate);
    }

    public static int findChunksForSpawningVanilla(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate) {
        if (!spawnHostileMobs && !spawnPeacefulMobs) {
            return 0;
        }
        if (!SRPSpawning.totalParasites) {
            if (++lock > 30) {
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
            BlockPos blockpos = SRPWorldEntitySpawner.getRandomChunkPosition((World)worldServerIn, chunkpos1.chunkXPos, chunkpos1.chunkZPos);
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
                    boolean bypassForgeDenyForFloating;
                    EntityParasiteBase entityliving;
                    BlockPos beforeAdjust;
                    blockpos$mutableblockpos.setPos(l2 += worldServerIn.rand.nextInt(6) - worldServerIn.rand.nextInt(6), i3 += worldServerIn.rand.nextInt(5) - worldServerIn.rand.nextInt(5), j3 += worldServerIn.rand.nextInt(6) - worldServerIn.rand.nextInt(6));
                    float f = (float)l2 + 0.5f;
                    float f1 = (float)j3 + 0.5f;
                    if (worldServerIn.isAnyPlayerWithinRangeAt((double)f, (double)i3, (double)f1, 24.0) || !(blockpos1.distanceSq((double)f, (double)i3, (double)f1) >= 576.0)) continue;
                    if (biome$spawnlistentry == null && (biome$spawnlistentry = SRPWorldEntitySpawner.getSpawnListEntryForTypeAt(worldServerIn, (BlockPos)blockpos$mutableblockpos)) == null) continue block6;
                    EntityLiving.SpawnPlacementType ground = EntitySpawnPlacementRegistry.getPlacementForEntity((Class)biome$spawnlistentry.entityClass);
                    if (ground == EntityLiving.SpawnPlacementType.IN_AIR) {
                        beforeAdjust = blockpos$mutableblockpos.toImmutable();
                        SRPWorldEntitySpawner.adjustAirSpawnPos((World)worldServerIn, blockpos$mutableblockpos);
                        if (!beforeAdjust.equals((Object)blockpos$mutableblockpos)) {
                            SRPWorldEntitySpawner.debugSpawn("VANILLA adjusted air spawn pos from " + SRPWorldEntitySpawner.posToString(beforeAdjust) + " to " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " for " + biome$spawnlistentry.entityClass.getSimpleName());
                        }
                    } else if (ground == EntityLiving.SpawnPlacementType.IN_WATER) {
                        beforeAdjust = blockpos$mutableblockpos.toImmutable();
                        SRPWorldEntitySpawner.adjustWaterSpawnPos((World)worldServerIn, blockpos$mutableblockpos);
                        if (!beforeAdjust.equals((Object)blockpos$mutableblockpos)) {
                            SRPWorldEntitySpawner.debugSpawn("VANILLA adjusted water spawn pos from " + SRPWorldEntitySpawner.posToString(beforeAdjust) + " to " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " for " + biome$spawnlistentry.entityClass.getSimpleName());
                        }
                    } else if (ground == EntityLiving.SpawnPlacementType.ON_GROUND) {
                        beforeAdjust = blockpos$mutableblockpos.toImmutable();
                        SRPWorldEntitySpawner.adjustGroundSpawnPos((World)worldServerIn, blockpos$mutableblockpos);
                        if (!beforeAdjust.equals((Object)blockpos$mutableblockpos)) {
                            SRPWorldEntitySpawner.debugSpawn("VANILLA adjusted ground spawn pos from " + SRPWorldEntitySpawner.posToString(beforeAdjust) + " to " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " for " + biome$spawnlistentry.entityClass.getSimpleName());
                        }
                    }
                    String failureReason = SRPWorldEntitySpawner.getSpawnFailureReason(ground, (World)worldServerIn, (BlockPos)blockpos$mutableblockpos);
                    if (failureReason != null) {
                        SRPWorldEntitySpawner.debugSpawn("VANILLA rejected location for " + biome$spawnlistentry.entityClass.getSimpleName() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " | reason=" + failureReason);
                        continue;
                    }
                    try {
                        entityliving = (EntityParasiteBase)biome$spawnlistentry.newInstance((World)worldServerIn);
                        entityliving.canSpawnSpawn = true;
                    }
                    catch (Exception exception) {
                        SRPWorldEntitySpawner.debugSpawn("VANILLA failed to create entity instance for " + biome$spawnlistentry.entityClass.getName() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " | reason=" + exception.getClass().getSimpleName() + ": " + exception.getMessage());
                        return j4;
                    }
                    float spawnX = (float)blockpos$mutableblockpos.getX() + 0.5f;
                    float spawnY = blockpos$mutableblockpos.getY();
                    float spawnZ = (float)blockpos$mutableblockpos.getZ() + 0.5f;
                    entityliving.setLocationAndAngles(spawnX, spawnY, spawnZ, worldServerIn.rand.nextFloat() * 360.0f, 0.0f);
                    Event.Result canSpawn = ForgeEventFactory.canEntitySpawn((EntityLiving)entityliving, (World)worldServerIn, (float)spawnX, (float)spawnY, (float)spawnZ, (boolean)false);
                    boolean canSpawnHere = entityliving.getCanSpawnHere();
                    boolean bl = bypassForgeDenyForFloating = canSpawn == Event.Result.DENY && (ground == EntityLiving.SpawnPlacementType.IN_WATER || ground == EntityLiving.SpawnPlacementType.IN_AIR) && canSpawnHere;
                    if (canSpawn == Event.Result.DENY || canSpawn == Event.Result.DENY && !bypassForgeDenyForFloating) {
                        SRPWorldEntitySpawner.debugSpawn("VANILLA denied by Forge canEntitySpawn for " + entityliving.getCommandSenderName() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                    } else if (canSpawn == Event.Result.ALLOW || bypassForgeDenyForFloating || canSpawn == Event.Result.DEFAULT && canSpawnHere) {
                        if (!ForgeEventFactory.doSpecialSpawn((EntityLiving)entityliving, (World)worldServerIn, (float)spawnX, (float)spawnY, (float)spawnZ)) {
                            ientitylivingdata = entityliving.onInitialSpawn(DifficultyAccess.at(worldServerIn, EntityQueries.position(entityliving)), ientitylivingdata);
                        }
                        if (entityliving.handleLavaMovement()) {
                            worldServerIn.spawnEntityInWorld((Entity)entityliving);
                            SRPWorldEntitySpawner.applyDebugGlow((EntityLiving)entityliving);
                            SRPWorldEntitySpawner.debugSpawn("VANILLA spawned " + entityliving.getCommandSenderName() + " at " + SRPWorldEntitySpawner.posToString(EntityQueries.position(entityliving)) + " | packCount=" + ++j2 + " | glow=" + SRPConfigSystems.debugSpawner);
                        } else {
                            SRPWorldEntitySpawner.debugSpawn("VANILLA failed collision check for " + entityliving.getCommandSenderName() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                            entityliving.setDead();
                        }
                        if (j2 >= ForgeEventFactory.getMaxSpawnPackSize((EntityLiving)entityliving)) {
                            SRPWorldEntitySpawner.debugSpawn("VANILLA reached max pack size for " + entityliving.getCommandSenderName() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                            continue block5;
                        }
                    } else {
                        SRPWorldEntitySpawner.debugSpawn("VANILLA getCanSpawnHere returned false | selected=" + biome$spawnlistentry.entityClass.getName() + " | actualClass=" + entityliving.getClass().getName() + " | actualName=" + entityliving.getCommandSenderName() + " | pos=" + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                    }
                    j4 += j2;
                }
            }
        }
        return j4;
    }

    private static void adjustGroundSpawnPos(World worldIn, BlockPos.MutableBlockPos pos) {
        BlockPos adjusted = SRPWorldEntitySpawner.findGroundSpawnPos(worldIn, pos.toImmutable());
        pos.setPos((Vec3i)adjusted);
    }

    private static void adjustAirSpawnPos(World worldIn, BlockPos.MutableBlockPos pos) {
        BlockPos adjusted = SRPWorldEntitySpawner.findAirSpawnPos(worldIn, pos.toImmutable());
        pos.setPos((Vec3i)adjusted);
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
        SRPWorldEntitySpawner.filterEligibleChunksForOrigin(worldData, originsExist, originlessAllowed);
        if (eligibleChunksForSpawning.isEmpty()) {
            return 0;
        }
        int j4 = 0;
        BlockPos blockpos1 = worldServerIn.getSpawnPoint();
        ArrayList shuffled = Lists.newArrayList(eligibleChunksForSpawning);
        Collections.shuffle(shuffled, worldServerIn.rand);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        block5: for (ChunkPos chunkpos1 : shuffled) {
            BlockPos blockpos = SRPWorldEntitySpawner.getRandomChunkPosition((World)worldServerIn, chunkpos1.chunkXPos, chunkpos1.chunkZPos);
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
                int l3 = 1 + worldServerIn.rand.nextInt(4);
                for (int i4 = 0; i4 < l3; ++i4) {
                    EntityParasiteBase entityliving;
                    BlockPos beforeAdjust;
                    blockpos$mutableblockpos.setPos(l2 += worldServerIn.rand.nextInt(6) - worldServerIn.rand.nextInt(6), i3 += worldServerIn.rand.nextInt(5) - worldServerIn.rand.nextInt(5), j3 += worldServerIn.rand.nextInt(6) - worldServerIn.rand.nextInt(6));
                    float f = (float)l2 + 0.5f;
                    float f1 = (float)j3 + 0.5f;
                    if (worldServerIn.isAnyPlayerWithinRangeAt((double)f, (double)i3, (double)f1, 24.0) || !(blockpos1.distanceSq((double)f, (double)i3, (double)f1) >= 576.0)) continue;
                    if (biome$spawnlistentry == null && (biome$spawnlistentry = SRPWorldEntitySpawner.getSpawnListEntryForTypeAtOrigin(worldServerIn, (BlockPos)blockpos$mutableblockpos, saveData72, worldData, originsExist, originlessAllowed)) == null) {
                        SRPWorldEntitySpawner.debugSpawn("ORIGIN no spawn entry available at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                        continue block6;
                    }
                    EntityLiving.SpawnPlacementType ground = EntitySpawnPlacementRegistry.getPlacementForEntity((Class)biome$spawnlistentry.entityClass);
                    if (ground == EntityLiving.SpawnPlacementType.IN_AIR) {
                        beforeAdjust = SRPConfigSystems.debugSpawner ? blockpos$mutableblockpos.toImmutable() : null;
                        SRPWorldEntitySpawner.adjustAirSpawnPos((World)worldServerIn, blockpos$mutableblockpos);
                        if (beforeAdjust != null && !beforeAdjust.equals((Object)blockpos$mutableblockpos)) {
                            SRPWorldEntitySpawner.debugSpawn("ORIGIN adjusted air spawn pos from " + SRPWorldEntitySpawner.posToString(beforeAdjust) + " to " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " for " + biome$spawnlistentry.entityClass.getSimpleName());
                        }
                    } else if (ground == EntityLiving.SpawnPlacementType.IN_WATER) {
                        beforeAdjust = SRPConfigSystems.debugSpawner ? blockpos$mutableblockpos.toImmutable() : null;
                        SRPWorldEntitySpawner.adjustWaterSpawnPos((World)worldServerIn, blockpos$mutableblockpos);
                        if (beforeAdjust != null && !beforeAdjust.equals((Object)blockpos$mutableblockpos)) {
                            SRPWorldEntitySpawner.debugSpawn("ORIGIN adjusted water spawn pos from " + SRPWorldEntitySpawner.posToString(beforeAdjust) + " to " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " for " + biome$spawnlistentry.entityClass.getSimpleName());
                        }
                    } else if (ground == EntityLiving.SpawnPlacementType.ON_GROUND) {
                        beforeAdjust = SRPConfigSystems.debugSpawner ? blockpos$mutableblockpos.toImmutable() : null;
                        SRPWorldEntitySpawner.adjustGroundSpawnPos((World)worldServerIn, blockpos$mutableblockpos);
                        if (beforeAdjust != null && !beforeAdjust.equals((Object)blockpos$mutableblockpos)) {
                            SRPWorldEntitySpawner.debugSpawn("ORIGIN adjusted ground spawn pos from " + SRPWorldEntitySpawner.posToString(beforeAdjust) + " to " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " for " + biome$spawnlistentry.entityClass.getSimpleName());
                        }
                    }
                    String failureReason = SRPWorldEntitySpawner.getSpawnFailureReason(ground, (World)worldServerIn, (BlockPos)blockpos$mutableblockpos);
                    if (failureReason != null) {
                        SRPWorldEntitySpawner.debugSpawn("ORIGIN rejected location for " + biome$spawnlistentry.entityClass.getSimpleName() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " | reason=" + failureReason);
                        continue;
                    }
                    boolean withinOrigin = SRPWorldEntitySpawner.isPosWithinOrigin((World)worldServerIn, (BlockPos)blockpos$mutableblockpos, worldData, originsExist, originlessAllowed);
                    if (!withinOrigin) {
                        SRPWorldEntitySpawner.debugSpawn("ORIGIN adjusted position outside origin for " + biome$spawnlistentry.entityClass.getSimpleName() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                        continue;
                    }
                    try {
                        entityliving = (EntityParasiteBase)biome$spawnlistentry.newInstance((World)worldServerIn);
                        entityliving.canSpawnSpawn = true;
                    }
                    catch (Exception exception) {
                        SRPWorldEntitySpawner.debugSpawn("ORIGIN failed to create entity instance for " + biome$spawnlistentry.entityClass.getName() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " | reason=" + exception.getClass().getSimpleName() + ": " + exception.getMessage());
                        return j4;
                    }
                    float spawnX = (float)blockpos$mutableblockpos.getX() + 0.5f;
                    float spawnY = blockpos$mutableblockpos.getY();
                    float spawnZ = (float)blockpos$mutableblockpos.getZ() + 0.5f;
                    entityliving.setLocationAndAngles(spawnX, spawnY, spawnZ, worldServerIn.rand.nextFloat() * 360.0f, 0.0f);
                    Event.Result canSpawn = ForgeEventFactory.canEntitySpawn((EntityLiving)entityliving, (World)worldServerIn, (float)spawnX, (float)spawnY, (float)spawnZ, (boolean)false);
                    boolean canSpawnHere = entityliving.getCanSpawnHere();
                    if (canSpawn == Event.Result.DENY) {
                        SRPWorldEntitySpawner.debugSpawn("ORIGIN denied by Forge canEntitySpawn for " + entityliving.getCommandSenderName() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                    } else if (canSpawn == Event.Result.ALLOW || canSpawn == Event.Result.DEFAULT && canSpawnHere) {
                        if (!ForgeEventFactory.doSpecialSpawn((EntityLiving)entityliving, (World)worldServerIn, (float)spawnX, (float)spawnY, (float)spawnZ)) {
                            ientitylivingdata = entityliving.onInitialSpawn(DifficultyAccess.at(worldServerIn, EntityQueries.position(entityliving)), ientitylivingdata);
                        }
                        if (entityliving.handleLavaMovement()) {
                            worldServerIn.spawnEntityInWorld((Entity)entityliving);
                            SRPWorldEntitySpawner.applyDebugGlow((EntityLiving)entityliving);
                            SRPWorldEntitySpawner.debugSpawn("ORIGIN spawned " + entityliving.getCommandSenderName() + " at " + SRPWorldEntitySpawner.posToString(EntityQueries.position(entityliving)) + " | packCount=" + ++j2 + " | glow=" + SRPConfigSystems.debugSpawner);
                        } else {
                            SRPWorldEntitySpawner.debugSpawn("ORIGIN failed collision check for " + entityliving.getCommandSenderName() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                            entityliving.setDead();
                        }
                        if (j2 >= ForgeEventFactory.getMaxSpawnPackSize((EntityLiving)entityliving)) {
                            SRPWorldEntitySpawner.debugSpawn("ORIGIN reached max pack size for " + entityliving.getCommandSenderName() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                            continue block5;
                        }
                    } else if (!canSpawnHere) {
                        SRPWorldEntitySpawner.debugSpawn("ORIGIN getCanSpawnHere returned false | selected=" + biome$spawnlistentry.entityClass.getName() + " | actualClass=" + entityliving.getClass().getName() + " | actualName=" + entityliving.getCommandSenderName() + " | pos=" + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos));
                    } else {
                        SRPWorldEntitySpawner.debugSpawn("ORIGIN spawn failed for " + entityliving.getCommandSenderName() + " at " + SRPWorldEntitySpawner.posToString((BlockPos)blockpos$mutableblockpos) + " | reason=unknown DEFAULT rejection");
                    }
                    j4 += j2;
                }
            }
        }
        return j4;
    }

    private static boolean isPosWithinOrigin(World world, BlockPos pos) {
        SRPWorldData data = SRPWorldData.get(world);
        boolean originsExist = data != null && !data.getorigins("x").isEmpty();
        SRPSaveData saveData225 = SRPSaveData.get(world, 225);
        boolean originlessAllowed = saveData225 != null && saveData225.getDeveLevel() >= SRPConfigSystems.deveOriginlessUse;
        return SRPWorldEntitySpawner.isPosWithinOrigin(world, pos, data, originsExist, originlessAllowed);
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
                if (!SRPWorldEntitySpawner.chunkIntersectsRadius2D(chunkPos.chunkXPos, chunkPos.chunkZPos, originsX.get(i), originsZ.get(i), originsA.get(i))) continue;
                inRange = true;
                break;
            }
            if (!inRange) {
                for (i = 0; i < colonyCount; ++i) {
                    if (!SRPWorldEntitySpawner.chunkIntersectsRadius2D(chunkPos.chunkXPos, chunkPos.chunkZPos, coloniesX.get(i), coloniesZ.get(i), colonyRadii[i])) continue;
                    inRange = true;
                    break;
                }
            }
            if (inRange) continue;
            chunkIterator.remove();
        }
    }

    private static BlockPos getRandomChunkPosition(World worldIn, int x, int z) {
        int i = x * 16 + worldIn.rand.nextInt(16);
        int j = z * 16 + worldIn.rand.nextInt(16);
        int minY = 1;
        int maxY = Math.max(minY + 1, worldIn.getActualHeight() - 2);
        int l = minY + worldIn.rand.nextInt(maxY - minY + 1);
        return new BlockPos(i, l, j);
    }

    @Nullable
    public static Biome.SpawnListEntry getSpawnListEntryForTypeAt(WorldServer worldServerIn, BlockPos pos) {
        Biome.SpawnListEntry chosen;
        SRPSaveData dat = SRPSaveData.get((World)worldServerIn, 72);
        int id = worldServerIn.provider.dimensionId;
        if (dat == null) {
            SRPWorldEntitySpawner.debugSpawn("No spawn entry: save data unavailable in dim " + id + " at " + SRPWorldEntitySpawner.posToString(pos));
            return null;
        }
        if (SRPConfigWorld.originActivated) {
            SRPWorldData data = SRPWorldData.get((World)worldServerIn);
            boolean originsExist = data != null && !data.getorigins("x").isEmpty();
            SRPSaveData saveData225 = SRPSaveData.get((World)worldServerIn, 225);
            boolean originlessAllowed = saveData225 != null && saveData225.getDeveLevel() >= SRPConfigSystems.deveOriginlessUse;
            return SRPWorldEntitySpawner.getSpawnListEntryForTypeAtOrigin(worldServerIn, pos, dat, data, originsExist, originlessAllowed);
        }
        List<Biome.SpawnListEntry> list = SRPSpawning.getSpawns((World)worldServerIn, id, dat.getEvolutionPhase(id), dat);
        Biome.SpawnListEntry spawnListEntry = chosen = list != null && !list.isEmpty() ? (Biome.SpawnListEntry)WeightedRandom.getRandomItem((Random)worldServerIn.rand, list) : null;
        if (chosen == null) {
            SRPWorldEntitySpawner.debugSpawn("Spawn list empty/null in dim " + id + " at " + SRPWorldEntitySpawner.posToString(pos) + " | originActivated=false | phase=" + dat.getEvolutionPhase(id));
        } else {
            SRPWorldEntitySpawner.debugSpawn("Selected spawn entry " + chosen.entityClass.getSimpleName() + " in dim " + id + " at " + SRPWorldEntitySpawner.posToString(pos) + " | originActivated=false | phase=" + dat.getEvolutionPhase(id));
        }
        return chosen;
    }

    @Nullable
    private static Biome.SpawnListEntry getSpawnListEntryForTypeAtOrigin(WorldServer worldServerIn, BlockPos pos, SRPSaveData dat, SRPWorldData data, boolean originsExist, boolean originlessAllowed) {
        Biome.SpawnListEntry chosen;
        int id = worldServerIn.provider.dimensionId;
        if (!SRPWorldEntitySpawner.isPosWithinOrigin((World)worldServerIn, pos, data, originsExist, originlessAllowed)) {
            SRPWorldEntitySpawner.debugSpawn("ORIGIN position rejected (not within origin) at " + SRPWorldEntitySpawner.posToString(pos));
            return null;
        }
        byte phase = dat.getEvolutionPhase(id);
        if (phase == -1) {
            Biome.SpawnListEntry chosen2;
            if (!originsExist) {
                return null;
            }
            List<Biome.SpawnListEntry> list = SRPSpawning.getSpawns((World)worldServerIn, id, phase, dat);
            Biome.SpawnListEntry spawnListEntry = chosen2 = list != null && !list.isEmpty() ? (Biome.SpawnListEntry)WeightedRandom.getRandomItem((Random)worldServerIn.rand, list) : null;
            if (chosen2 == null) {
                SRPWorldEntitySpawner.debugSpawn("Spawn list empty/null in dim " + id + " at " + SRPWorldEntitySpawner.posToString(pos) + " | originActivated=true | phase=" + phase + " | outbreak=true");
            } else {
                SRPWorldEntitySpawner.debugSpawn("Selected spawn entry " + chosen2.entityClass.getSimpleName() + " in dim " + id + " at " + SRPWorldEntitySpawner.posToString(pos) + " | originActivated=true | phase=" + phase + " | outbreak=true");
            }
            return chosen2;
        }
        byte phaseToUse = originsExist ? phase : (byte)0;
        List<Biome.SpawnListEntry> list = SRPSpawning.getSpawns((World)worldServerIn, id, phaseToUse, dat);
        Biome.SpawnListEntry spawnListEntry = chosen = list != null && !list.isEmpty() ? (Biome.SpawnListEntry)WeightedRandom.getRandomItem((Random)worldServerIn.rand, list) : null;
        if (chosen == null) {
            SRPWorldEntitySpawner.debugSpawn("Spawn list empty/null in dim " + id + " at " + SRPWorldEntitySpawner.posToString(pos) + " | originActivated=true | phase=" + phaseToUse + (originsExist ? " | originsPresent=true" : " | forcedPhase=0 | noOrigins=true"));
        } else {
            SRPWorldEntitySpawner.debugSpawn("Selected spawn entry " + chosen.entityClass.getSimpleName() + " in dim " + id + " at " + SRPWorldEntitySpawner.posToString(pos) + " | originActivated=true | phase=" + phaseToUse + (originsExist ? " | originsPresent=true" : " | forcedPhase=0 | noOrigins=true"));
        }
        return chosen;
    }

    public static boolean canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType spawnPlacementTypeIn, World worldIn, BlockPos pos) {
        return SRPWorldEntitySpawner.getSpawnFailureReason(spawnPlacementTypeIn, worldIn, pos) == null;
    }

    public static boolean canCreatureTypeSpawnBody(EntityLiving.SpawnPlacementType spawnPlacementTypeIn, World worldIn, BlockPos pos) {
        return SRPWorldEntitySpawner.canCreatureTypeSpawnAtLocation(spawnPlacementTypeIn, worldIn, pos);
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

    private static BlockPos findGroundSpawnPos(World worldIn, BlockPos start) {
        BlockPos best = null;
        int bestDist = Integer.MAX_VALUE;
        int minY = Math.max(1, start.getY() - 12);
        int maxY = Math.min(worldIn.getActualHeight() - 2, start.getY() + 12);
        for (int y = minY; y <= maxY; ++y) {
            int dist;
            BlockPos candidate = new BlockPos(start.getX(), y, start.getZ());
            BlockPos below = candidate.down();
            IBlockState belowState = BlockStates.get(worldIn, below);
            IBlockState atState = BlockStates.get(worldIn, candidate);
            IBlockState aboveState = BlockStates.get(worldIn, candidate.up());
            Block belowBlock = belowState.getBlock();
            boolean validFloor = !belowState.getMaterial().isLiquid() && belowBlock != Blocks.air && belowBlock != Blocks.leaves && belowBlock != Blocks.leaves2 && belowBlock != Blocks.bedrock && belowBlock != Blocks.BARRIER && belowBlock.canCreatureSpawn(belowState, (IBlockAccess)worldIn, below, EntityLiving.SpawnPlacementType.ON_GROUND);
            boolean openAt = SRPWorldEntitySpawner.isValidEmptySpawnBlock(atState);
            boolean openAbove = SRPWorldEntitySpawner.isValidEmptySpawnBlock(aboveState);
            if (!validFloor || !openAt || !openAbove || (dist = Math.abs(y - start.getY())) >= bestDist) continue;
            bestDist = dist;
            best = candidate;
        }
        return best != null ? best : start;
    }

    private static void applyDebugGlow(EntityLiving entityliving) {
        if (!SRPConfigSystems.debugSpawner || entityliving == null) {
            return;
        }
        entityliving.addPotionEffect(new com.dhanantry.scapeandrunparasites.backport.potion.SRPPotionEffect(MobEffects.GLOWING, 1200, 0, false, false));
    }

    private static BlockPos findWaterSpawnPos(World worldIn, BlockPos start) {
        int x = start.getX();
        int z = start.getZ();
        BlockPos top = WorldAccess.getTopSolidOrLiquidBlock(worldIn, new BlockPos(x, 0, z));
        int topY = Math.max(1, top.getY() + 2);
        BlockPos best = null;
        int bestDist = Integer.MAX_VALUE;
        for (int y = 1; y <= topY; ++y) {
            int dist;
            boolean valid;
            BlockPos pos = new BlockPos(x, y, z);
            IBlockState at = BlockStates.get(worldIn, pos);
            IBlockState below = BlockStates.get(worldIn, pos.down());
            IBlockState above = BlockStates.get(worldIn, pos.up());
            boolean bl = valid = at.getMaterial() == Material.water && below.getMaterial() == Material.water && above.getMaterial() == Material.water;
            if (!valid || (dist = Math.abs(y - start.getY())) >= bestDist) continue;
            bestDist = dist;
            best = pos;
        }
        return best != null ? best : start;
    }

    private static void adjustWaterSpawnPos(World worldIn, BlockPos.MutableBlockPos pos) {
        BlockPos adjusted = SRPWorldEntitySpawner.findWaterSpawnPos(worldIn, pos.toImmutable());
        pos.setPos((Vec3i)adjusted);
    }

    private static BlockPos findAirSpawnPos(World worldIn, BlockPos start) {
        int x = start.getX();
        int z = start.getZ();
        int topY = WorldAccess.getTopSolidOrLiquidBlock(worldIn, new BlockPos(x, 0, z)).getY();
        int minY = Math.max(2, topY + 8);
        int maxY = Math.min(SRPConfigWorld.spawnerSKYLimitUp, worldIn.getActualHeight() - 2);
        if (maxY < minY) {
            return start;
        }
        ArrayList<BlockPos> validPositions = new ArrayList<BlockPos>();
        for (int y = minY; y <= maxY; ++y) {
            boolean valid;
            BlockPos pos = new BlockPos(x, y, z);
            IBlockState at = BlockStates.get(worldIn, pos);
            boolean bl = valid = at.getBlock() == Blocks.air && BlockStates.get(worldIn, pos.up()).getBlock() == Blocks.air && BlockStates.get(worldIn, pos.down()).getBlock() == Blocks.air;
            if (!valid) continue;
            validPositions.add(pos);
        }
        if (validPositions.isEmpty()) {
            return start;
        }
        return (BlockPos)validPositions.get(worldIn.rand.nextInt(validPositions.size()));
    }
}

