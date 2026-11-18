/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.EnumDifficulty
 *  net.minecraft.world.GameRules
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.world.DifficultyAccess;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.init.SRPBiomes;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPReference;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.util.convert.BeckonBlockInfestation;
import com.dhanantry.scapeandrunparasites.util.handlers.BiomeUpdateQueue;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteHarlequin;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BlockParasiteSpreading
extends BlockBase {
    public static int blockParasiteCount;
    public boolean isInfestedBlock;

    public BlockParasiteSpreading(Material material, String name, float hardness, boolean creative, boolean isInfested) {
        super(material, name, hardness, creative, true);
        this.isInfestedBlock = isInfested;
    }

    public BlockParasiteSpreading(Material material, String name, float hardness, boolean creative, boolean isInfested, float resistance) {
        this(material, name, hardness, creative, isInfested);
        this.setResistance(resistance);
    }

    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        boolean hasSnowAbove;
        if (!WorldAccess.isAreaLoaded(worldIn, pos, 3)) {
            return;
        }
        IBlockState aboveState = BlockStates.get(worldIn, pos.up());
        Block aboveBlock = aboveState.getBlock();
        boolean bl = hasSnowAbove = aboveBlock == Blocks.snow_layer || aboveBlock == Blocks.snow;
        if (this == SRPBlocks.HarlequinnGrass) {
            if (hasSnowAbove) {
                BlockStates.set(worldIn, pos, BlockStates.defaultState(SRPBlocks.LocsBlock), 3);
                return;
            }
        } else if (this == SRPBlocks.HarleskinnBlock && aboveBlock == SRPBlocks.LocsBlock) {
            BlockStates.set(worldIn, pos, BlockStates.defaultState(SRPBlocks.HarlequinnGrass), 3);
            return;
        }
        if (!this.isInfestedBlock) {
            this.updateTick(worldIn, pos, state, rand);
        } else if (rand.nextDouble() < 0.5) {
            if (worldIn.getBiome(pos) instanceof BiomeParasiteBase) {
                BiomeParasiteBase biomePara = (BiomeParasiteBase)worldIn.getBiome(pos);
                String[] blockList = biomePara.getDirt().split(":");
                Block one = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
                BlockStates.set(worldIn, pos, BlockStates.fromMeta(one, Integer.parseInt(blockList[2])));
            } else {
                int heart = ParasiteEventWorld.canBiomeStillExist(worldIn, pos, true);
                if (heart > 0) {
                    BlockParasiteSpreading.SpreadBiome(worldIn, pos, heart, ParasiteEventWorld.canBiomeStillExistType(worldIn, pos, true));
                }
            }
        } else if (rand.nextDouble() <= 0.05) {
            this.updateTick(worldIn, pos, state, rand);
        }
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) {
            if (this.isInfestedBlock) {
                BlockParasiteSpreading.canInfestBlock(worldIn, pos, rand, BlockStates.meta(this, state), true);
            } else if (worldIn.getBiome(pos) instanceof BiomeParasiteBase) {
                BlockParasiteSpreading.spreadBiomeBlockStain(worldIn, pos, rand);
            } else {
                int heart = ParasiteEventWorld.canBiomeStillExist(worldIn, pos, true);
                if (heart > 0) {
                    BlockParasiteSpreading.SpreadBiome(worldIn, pos, heart, ParasiteEventWorld.canBiomeStillExistType(worldIn, pos, true));
                }
            }
        }
    }

    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        boolean flag = super.removedByPlayer(state, world, pos, player, willHarvest);
        if (world.isRemote) {
            return flag;
        }
        if (SRPConfigSystems.useEvolution && BlockStates.meta(this, state) > 0) {
            SRPSaveData data = SRPSaveData.get(world, 9);
            data.setTotalKills(world.provider.dimensionId, -SRPConfigSystems.valueLossBlockStain, true, world, false, 19);
        }
        return flag;
    }

    public static void SpreadBiome(World worldIn, BlockPos pos, int age, int type) {
        if (!SRPConfigWorld.nodesActivated || !SRPConfigWorld.biomeRegster) {
            return;
        }
        SRPWorldData data = SRPWorldData.get(worldIn);
        int distance = data.getDistanceSpreadByAge(age, false);
        for (int x = pos.getX() - 4; x <= pos.getX() + 4; ++x) {
            for (int z = pos.getZ() - 4; z <= pos.getZ() + 4; ++z) {
                BiomeParasiteBase target;
                Biome current;
                BlockPos convert = new BlockPos(x, pos.getY(), z);
                if (data.isInRangeOfHeart(convert, distance) == -1 || ParasiteEventEntity.checkName(worldIn.getBiome(convert).getRegistryName().toString(), SRPConfigWorld.biomeBlackList, SRPConfigWorld.biomeBlackListInverted) || (current = worldIn.getBiome(convert)) instanceof BiomeParasiteBase || (target = SRPReference.getBiomeFromInt(type)) != null && current == target) continue;
                BlockParasiteSpreading.positionToParasiteBiome(worldIn, convert, type);
                BiomeUpdateQueue.enqueue(convert.getX(), convert.getY(), convert.getZ(), true, type, worldIn.provider.dimensionId);
            }
        }
    }

    public static void spreadBiomeBlockStain(World worldIn, BlockPos pos, Random rand) {
        if (!(worldIn.getBiome(pos) instanceof BiomeParasiteBase)) {
            return;
        }
        BiomeParasiteBase biomePara = (BiomeParasiteBase)worldIn.getBiome(pos);
        BlockPos helper = pos;
        int covertedBlocks = 0;
        for (int dir = 0; dir <= 5; ++dir) {
            helper = BlockParasiteSpreading.directionToSpread(pos, dir);
            BlockParasiteSpreading.spreadToAir(worldIn, helper);
            blockParasiteCount += (covertedBlocks += biomePara.convertBlock(helper, worldIn, rand));
        }
        if (covertedBlocks != 0 && SRPConfigSystems.useEvolution) {
            SRPSaveData.get(worldIn, 76).setTotalKills(worldIn.provider.dimensionId, SRPConfigSystems.valueBlock * covertedBlocks, true, worldIn, true, 20);
        }
    }

    public static void spreadBiomeBlockTrunk(World worldIn, BlockPos pos, Random rand) {
        if (DifficultyAccess.difficulty(worldIn) == EnumDifficulty.PEACEFUL) {
            return;
        }
        BlockPos helper = pos;
        int posx = pos.getX();
        int posz = pos.getZ();
        int posy = pos.getY();
        for (int x = -1; x <= 1; ++x) {
            for (int z = -1; z <= 1; ++z) {
                for (int y = -1; y <= 1; ++y) {
                    helper = new BlockPos(posx + x, posy + y, posz + z);
                    IBlockState lookingState = BlockStates.get(worldIn, helper);
                    Block lookingBlock = lookingState.getBlock();
                    if (!lookingBlock.isWood((IBlockAccess)worldIn, helper) || lookingBlock == SRPBlocks.ParasiteTrunk) continue;
                    BlockStates.set(worldIn, helper, BlockStates.defaultState(SRPBlocks.ParasiteTrunk));
                    BlockParasiteSpreading.spreadToAir(worldIn, helper);
                    if (!SRPConfigSystems.useEvolution) continue;
                    SRPSaveData.get(worldIn, 77).setTotalKills(worldIn.provider.dimensionId, SRPConfigSystems.valueBlock, true, worldIn, true, 21);
                }
            }
        }
    }

    public static void positionToParasiteBiome(World worldIn, BlockPos pos, int type) {
        byte biomeID = (byte)Biome.getIdForBiome((Biome)SRPBiomes.biomeShrouded);
        GameRules rules = worldIn.getGameRules();
        if (rules.getGameRuleBooleanValue("srpForceHarlequin")) {
            BiomeParasiteHarlequin forced;
            biomeID = (byte)Biome.getIdForBiome((Biome)SRPBiomes.biomeHarlequin);
            int inChunkX = pos.getX() & 0xF;
            int inChunkZ = pos.getZ() & 0xF;
            Biome currentForced = worldIn.getBiome(pos);
            if (currentForced == (forced = SRPBiomes.biomeHarlequin)) {
                return;
            }
            WorldAccess.getChunk(worldIn, (BlockPos)pos).getBiomeArray()[inChunkZ << 4 | inChunkX] = biomeID;
            WorldAccess.getChunk(worldIn, pos).setChunkModified();
            worldIn.markBlocksDirtyVertical(pos.getX(), pos.getZ(), 0, pos.getY());
            return;
        }
        BiomeParasiteBase target = SRPReference.getBiomeFromInt(type);
        if (target == null) {
            return;
        }
        Biome current = worldIn.getBiome(pos);
        if (current == target) {
            return;
        }
        biomeID = (byte)Biome.getIdForBiome((Biome)target);
        int inChunkX = pos.getX() & 0xF;
        int inChunkZ = pos.getZ() & 0xF;
        WorldAccess.getChunk(worldIn, (BlockPos)pos).getBiomeArray()[inChunkZ << 4 | inChunkX] = biomeID;
        WorldAccess.getChunk(worldIn, pos).setChunkModified();
        worldIn.markBlocksDirtyVertical(pos.getX(), pos.getZ(), 0, pos.getY());
    }

    public static BlockPos directionToSpread(BlockPos atm, int choice) {
        switch (choice) {
            case 0: {
                atm = atm.north();
                break;
            }
            case 1: {
                atm = atm.east();
                break;
            }
            case 2: {
                atm = atm.west();
                break;
            }
            case 3: {
                atm = atm.south();
                break;
            }
            case 4: {
                atm = atm.up();
                break;
            }
            default: {
                atm = atm.down();
            }
        }
        return atm;
    }

    public static void spreadToAir(World worldIn, BlockPos atm) {
    }

    public static void canInfestBlock(World worldIn, BlockPos pos, Random rand, int stage, boolean fromVenkrol) {
        BeckonBlockInfestation.beckonInfestation(worldIn, pos, rand, stage, fromVenkrol);
    }
}

