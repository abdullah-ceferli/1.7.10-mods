/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockFlower
 *  net.minecraft.block.BlockFlower$EnumFlowerType
 *  net.minecraft.block.material.Material
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 *  net.minecraft.world.biome.BiomeDecorator
 *  net.minecraft.world.gen.feature.WorldGenAbstractTree
 *  net.minecraft.world.gen.feature.WorldGenLiquids
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.terraingen.DecorateBiomeEvent$Decorate$EventType
 *  net.minecraftforge.event.terraingen.DecorateBiomeEvent$Post
 *  net.minecraftforge.event.terraingen.DecorateBiomeEvent$Pre
 *  net.minecraftforge.event.terraingen.TerrainGen
 *  net.minecraftforge.fml.common.eventhandler.Event
 */
package com.dhanantry.scapeandrunparasites.world.biome;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteBall;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteBigBall;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteBush;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import cpw.mods.fml.common.eventhandler.Event;

public class BiomeParasiteDecorator
extends BiomeDecorator {
    protected BiomeParasiteBase biomeFather;

    public BiomeParasiteDecorator(BiomeParasiteBase biome) {
        this.biomeFather = biome;
    }

    protected void genDecorations(Biome biomeIn, World worldIn, Random random) {
        int k9;
        int i5;
        int j13;
        ChunkPos forgeChunkPos = new ChunkPos(this.chunkPos);
        MinecraftForge.EVENT_BUS.post((Event)new DecorateBiomeEvent.Pre(worldIn, random, forgeChunkPos));
        this.generateOres(worldIn, random);
        if (TerrainGen.decorate((World)worldIn, (Random)random, (ChunkPos)forgeChunkPos, (DecorateBiomeEvent.Decorate.EventType)DecorateBiomeEvent.Decorate.EventType.SAND)) {
            for (int i = 0; i < this.sandPerChunk2; ++i) {
                int j = random.nextInt(16) + 8;
                int k = random.nextInt(16) + 8;
                this.sandGen.generate(worldIn, random, WorldAccess.getTopSolidOrLiquidBlock(worldIn, this.chunkPos.add(j, 0, k)));
            }
        }
        if (TerrainGen.decorate((World)worldIn, (Random)random, (ChunkPos)forgeChunkPos, (DecorateBiomeEvent.Decorate.EventType)DecorateBiomeEvent.Decorate.EventType.CLAY)) {
            for (int i1 = 0; i1 < this.clayPerChunk; ++i1) {
                int l1 = random.nextInt(16) + 8;
                int i6 = random.nextInt(16) + 8;
                this.clayGen.generate(worldIn, random, WorldAccess.getTopSolidOrLiquidBlock(worldIn, this.chunkPos.add(l1, 0, i6)));
            }
        }
        if (TerrainGen.decorate((World)worldIn, (Random)random, (ChunkPos)forgeChunkPos, (DecorateBiomeEvent.Decorate.EventType)DecorateBiomeEvent.Decorate.EventType.SAND_PASS2)) {
            for (int j1 = 0; j1 < this.sandPerChunk; ++j1) {
                int i2 = random.nextInt(16) + 8;
                int j6 = random.nextInt(16) + 8;
                this.gravelAsSandGen.generate(worldIn, random, WorldAccess.getTopSolidOrLiquidBlock(worldIn, this.chunkPos.add(i2, 0, j6)));
            }
        }
        int k1 = this.treesPerChunk;
        if (random.nextFloat() < this.extraTreeChance) {
            ++k1;
        }
        if (TerrainGen.decorate((World)worldIn, (Random)random, (ChunkPos)forgeChunkPos, (DecorateBiomeEvent.Decorate.EventType)DecorateBiomeEvent.Decorate.EventType.TREE)) {
            for (int j2 = 0; j2 < k1; ++j2) {
                int k6 = random.nextInt(16) + 8;
                int l = random.nextInt(16) + 8;
                WorldGenAbstractTree worldgenabstracttree = biomeIn.getRandomTreeFeature(random);
                worldgenabstracttree.setDecorationDefaults();
                BlockPos blockpos = WorldAccess.getHeight(worldIn, this.chunkPos.add(k6, 0, l));
                if (!worldgenabstracttree.generate(worldIn, random, blockpos)) continue;
                worldgenabstracttree.generateSaplings(worldIn, random, blockpos);
            }
        }
        if (TerrainGen.decorate((World)worldIn, (Random)random, (ChunkPos)forgeChunkPos, (DecorateBiomeEvent.Decorate.EventType)DecorateBiomeEvent.Decorate.EventType.BIG_SHROOM)) {
            for (int k2 = 0; k2 < this.bigMushroomsPerChunk; ++k2) {
                int l6 = random.nextInt(16) + 8;
                int k10 = random.nextInt(16) + 8;
                new WorldGenParasiteBigBall(false).generate(worldIn, random, this.chunkPos.add(l6, 0, k10));
            }
        }
        if (TerrainGen.decorate((World)worldIn, (Random)random, (ChunkPos)forgeChunkPos, (DecorateBiomeEvent.Decorate.EventType)DecorateBiomeEvent.Decorate.EventType.FLOWERS)) {
            for (int l2 = 0; l2 < this.flowersPerChunk; ++l2) {
                int k17;
                BlockPos blockpos1;
                BlockFlower.EnumFlowerType blockflower$enumflowertype;
                BlockFlower blockflower;
                int l10;
                int i7 = random.nextInt(16) + 8;
                int j14 = WorldAccess.getHeight(worldIn, this.chunkPos.add(i7, 0, l10 = random.nextInt(16) + 8)).getY() + 32;
                if (j14 <= 0 || BlockStates.defaultState((blockflower = (blockflower$enumflowertype = biomeIn.pickRandomFlower(random, blockpos1 = this.chunkPos.add(i7, k17 = random.nextInt(j14), l10))).getBlockType().getBlock())).getMaterial() == Material.air) continue;
                new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.EYE, 2).generate(worldIn, random, blockpos1);
            }
        }
        if (TerrainGen.decorate((World)worldIn, (Random)random, (ChunkPos)forgeChunkPos, (DecorateBiomeEvent.Decorate.EventType)DecorateBiomeEvent.Decorate.EventType.GRASS)) {
            for (int i3 = 0; i3 < this.grassPerChunk; ++i3) {
                int i11;
                int j7 = random.nextInt(16) + 8;
                int k14 = WorldAccess.getHeight(worldIn, this.chunkPos.add(j7, 0, i11 = random.nextInt(16) + 8)).getY() * 2;
                if (k14 <= 0) continue;
                int l17 = random.nextInt(k14);
                if (random.nextInt(10) == 0) {
                    biomeIn.getRandomWorldGenForGrass(random).generate(worldIn, random, this.chunkPos.add(j7, l17, i11));
                    continue;
                }
                new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.TENDRIL, 4).generate(worldIn, random, this.chunkPos.add(j7, l17, i11));
            }
        }
        if (TerrainGen.decorate((World)worldIn, (Random)random, (ChunkPos)forgeChunkPos, (DecorateBiomeEvent.Decorate.EventType)DecorateBiomeEvent.Decorate.EventType.DEAD_BUSH)) {
            for (int j3 = 0; j3 < this.deadBushPerChunk; ++j3) {
                int j11;
                int k7 = random.nextInt(16) + 8;
                int l14 = WorldAccess.getHeight(worldIn, this.chunkPos.add(k7, 0, j11 = random.nextInt(16) + 8)).getY() * 2;
                if (l14 <= 0) continue;
                int i18 = random.nextInt(l14);
                new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.EYE, 2).generate(worldIn, random, this.chunkPos.add(k7, i18, j11));
            }
        }
        if (TerrainGen.decorate((World)worldIn, (Random)random, (ChunkPos)forgeChunkPos, (DecorateBiomeEvent.Decorate.EventType)DecorateBiomeEvent.Decorate.EventType.LILYPAD)) {
            for (int k3 = 0; k3 < this.waterlilyPerChunk; ++k3) {
                BlockPos blockpos7;
                int k11;
                int l7 = random.nextInt(16) + 8;
                int i15 = WorldAccess.getHeight(worldIn, this.chunkPos.add(l7, 0, k11 = random.nextInt(16) + 8)).getY() * 2;
                if (i15 <= 0) continue;
                int j18 = random.nextInt(i15);
                BlockPos blockpos4 = this.chunkPos.add(l7, j18, k11);
                while (blockpos4.getY() > 0 && WorldAccess.isAirBlock(worldIn, blockpos7 = blockpos4.down())) {
                    blockpos4 = blockpos7;
                }
                this.waterlilyGen.generate(worldIn, random, blockpos4);
            }
        }
        if (TerrainGen.decorate((World)worldIn, (Random)random, (ChunkPos)forgeChunkPos, (DecorateBiomeEvent.Decorate.EventType)DecorateBiomeEvent.Decorate.EventType.SHROOM)) {
            int l8;
            int j4;
            int k12;
            int k8;
            int i4;
            int j12;
            for (int l3 = 0; l3 < this.mushroomsPerChunk; ++l3) {
                int i12;
                int j8;
                int j15;
                if (random.nextInt(4) == 0) {
                    int i8 = random.nextInt(16) + 8;
                    int l11 = random.nextInt(16) + 8;
                    BlockPos blockpos2 = WorldAccess.getHeight(worldIn, this.chunkPos.add(i8, 0, l11));
                    this.mushroomBrownGen.generate(worldIn, random, blockpos2);
                }
                if (random.nextInt(8) != 0 || (j15 = WorldAccess.getHeight(worldIn, this.chunkPos.add(j8 = random.nextInt(16) + 8, 0, i12 = random.nextInt(16) + 8)).getY() * 2) <= 0) continue;
                int k18 = random.nextInt(j15);
                BlockPos blockpos5 = this.chunkPos.add(j8, k18, i12);
                this.mushroomRedGen.generate(worldIn, random, blockpos5);
            }
            if (random.nextInt(4) == 0 && (j12 = WorldAccess.getHeight(worldIn, this.chunkPos.add(i4 = random.nextInt(16) + 8, 0, k8 = random.nextInt(16) + 8)).getY() * 2) > 0) {
                int k15 = random.nextInt(j12);
                this.mushroomBrownGen.generate(worldIn, random, this.chunkPos.add(i4, k15, k8));
            }
            if (random.nextInt(8) == 0 && (k12 = WorldAccess.getHeight(worldIn, this.chunkPos.add(j4 = random.nextInt(16) + 8, 0, l8 = random.nextInt(16) + 8)).getY() * 2) > 0) {
                int l15 = random.nextInt(k12);
                this.mushroomRedGen.generate(worldIn, random, this.chunkPos.add(j4, l15, l8));
            }
        }
        if (TerrainGen.decorate((World)worldIn, (Random)random, (ChunkPos)forgeChunkPos, (DecorateBiomeEvent.Decorate.EventType)DecorateBiomeEvent.Decorate.EventType.REED)) {
            for (int k4 = 0; k4 < this.reedsPerChunk; ++k4) {
                int l12;
                int i9 = random.nextInt(16) + 8;
                int i16 = WorldAccess.getHeight(worldIn, this.chunkPos.add(i9, 0, l12 = random.nextInt(16) + 8)).getY() * 2;
                if (i16 <= 0) continue;
                int l18 = random.nextInt(i16);
                this.reedGen.generate(worldIn, random, this.chunkPos.add(i9, l18, l12));
            }
            for (int l4 = 0; l4 < 10; ++l4) {
                int i13;
                int j9 = random.nextInt(16) + 8;
                int j16 = WorldAccess.getHeight(worldIn, this.chunkPos.add(j9, 0, i13 = random.nextInt(16) + 8)).getY() * 2;
                if (j16 <= 0) continue;
                int i19 = random.nextInt(j16);
                this.reedGen.generate(worldIn, random, this.chunkPos.add(j9, i19, i13));
            }
        }
        if (TerrainGen.decorate((World)worldIn, (Random)random, (ChunkPos)forgeChunkPos, (DecorateBiomeEvent.Decorate.EventType)DecorateBiomeEvent.Decorate.EventType.PUMPKIN) && random.nextInt(32) == 0 && (j13 = WorldAccess.getHeight(worldIn, this.chunkPos.add(i5 = random.nextInt(16) + 8, 0, k9 = random.nextInt(16) + 8)).getY() * 2) > 0) {
            int k16 = random.nextInt(j13);
            new WorldGenParasiteBall(false).generate(worldIn, random, this.chunkPos.add(i5, k16, k9));
        }
        if (TerrainGen.decorate((World)worldIn, (Random)random, (ChunkPos)forgeChunkPos, (DecorateBiomeEvent.Decorate.EventType)DecorateBiomeEvent.Decorate.EventType.CACTUS)) {
            for (int j5 = 0; j5 < this.cactiPerChunk; ++j5) {
                int k13;
                int l9 = random.nextInt(16) + 8;
                int l16 = WorldAccess.getHeight(worldIn, this.chunkPos.add(l9, 0, k13 = random.nextInt(16) + 8)).getY() * 2;
                if (l16 <= 0) continue;
                int j19 = random.nextInt(l16);
                new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.TENDRIL, 4).generate(worldIn, random, this.chunkPos.add(l9, j19, k13));
            }
        }
        if (this.generateLakes) {
            if (TerrainGen.decorate((World)worldIn, (Random)random, (ChunkPos)forgeChunkPos, (DecorateBiomeEvent.Decorate.EventType)DecorateBiomeEvent.Decorate.EventType.LAKE_WATER)) {
                for (int k5 = 0; k5 < 50; ++k5) {
                    int i10 = random.nextInt(16) + 8;
                    int l13 = random.nextInt(16) + 8;
                    int i17 = random.nextInt(248) + 8;
                    if (i17 <= 0) continue;
                    int k19 = random.nextInt(i17);
                    BlockPos blockpos6 = this.chunkPos.add(i10, k19, l13);
                    new WorldGenLiquids((Block)Blocks.flowing_water).generate(worldIn, random, blockpos6);
                }
            }
            if (TerrainGen.decorate((World)worldIn, (Random)random, (ChunkPos)forgeChunkPos, (DecorateBiomeEvent.Decorate.EventType)DecorateBiomeEvent.Decorate.EventType.LAKE_LAVA)) {
                for (int l5 = 0; l5 < 20; ++l5) {
                    int j10 = random.nextInt(16) + 8;
                    int i14 = random.nextInt(16) + 8;
                    int j17 = random.nextInt(random.nextInt(random.nextInt(240) + 8) + 8);
                    BlockPos blockpos3 = this.chunkPos.add(j10, j17, i14);
                    new WorldGenLiquids((Block)Blocks.flowing_lava).generate(worldIn, random, blockpos3);
                }
            }
        }
        MinecraftForge.EVENT_BUS.post((Event)new DecorateBiomeEvent.Post(worldIn, random, forgeChunkPos));
    }
}

