/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.Material
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome$BiomeProperties
 *  net.minecraft.world.chunk.ChunkPrimer
 *  net.minecraft.world.gen.feature.WorldGenAbstractTree
 *  net.minecraft.world.gen.feature.WorldGenerator
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.world.biome;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteBush;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTree;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTreeThin;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenerator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BiomeParasiteDemen
extends BiomeParasiteBase {
    public static WorldGenAbstractTree treeP = new WorldGenParasiteTree(false);
    public static WorldGenAbstractTree treePT = new WorldGenParasiteTreeThin(false);
    public WorldGenerator grassP1 = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.EYE, 1);
    public WorldGenerator grassP2 = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.POP, 2);

    public BiomeParasiteDemen() {
        super(new Biome.BiomeProperties("Parasite Biome Demen").setBaseHeight(0.13f).setHeightVariation(0.5f));
        this.theBiomeDecorator.treesPerChunk = 1;
        this.theBiomeDecorator.extraTreeChance = 0.0f;
        this.theBiomeDecorator.grassPerChunk = 15;
        this.theBiomeDecorator.flowersPerChunk = 4;
        this.theBiomeDecorator.deadBushPerChunk = 2;
        this.theBiomeDecorator.cactiPerChunk = 15;
        this.theBiomeDecorator.bigMushroomsPerChunk = 1;
    }

    public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
        if (rand.nextInt(3) == 0) {
            return treePT;
        }
        return treeP;
    }

    public WorldGenerator getRandomWorldGenForGrass(Random rand) {
        if (rand.nextInt(2) == 0) {
            return this.grassP2;
        }
        return this.grassP1;
    }

    public void decorate(World worldIn, Random rand, BlockPos pos) {
        super.decorate(worldIn, rand, pos);
    }

    public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
        super.genTerrainBlocks(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
        double d0 = 1.0;
        if (d0 > 0.0) {
            int i = x & 0xF;
            int j = z & 0xF;
            for (int k = 255; k >= 0; --k) {
                if (chunkPrimerIn.getBlockState(j, k, i).getMaterial() == Material.air) continue;
                if (k != 62 || chunkPrimerIn.getBlockState(j, k, i).getBlock() == Blocks.water) break;
                chunkPrimerIn.setBlockState(j, k, i, WATER);
                break;
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public int getSkyColorByTemp(float currentTemperature) {
        return SRPConfigWorld.biomeFourSkyColor;
    }

    @SideOnly(value=Side.CLIENT)
    public int getGrassColorAtPos(BlockPos blockPos) {
        return SRPConfigWorld.biomeFourGrassColor;
    }

    @SideOnly(value=Side.CLIENT)
    public int getFoliageColorAtPos(BlockPos blockPos) {
        return SRPConfigWorld.biomeFourFoliageColor;
    }

    public int getWaterColorMultiplier() {
        return SRPConfigWorld.biomeFourWaterColor;
    }

    @Override
    public float getRedValue() {
        return SRPConfigWorld.biomeFourFogRed / 255.0f;
    }

    @Override
    public float getGreenValue() {
        return SRPConfigWorld.biomeFourFogGreen / 255.0f;
    }

    @Override
    public float getBlueValue() {
        return SRPConfigWorld.biomeFourFogBlue / 255.0f;
    }

    @Override
    public String[] getBlockList() {
        return null;
    }

    @Override
    public String getDirt() {
        return null;
    }

    @Override
    public String getGravel() {
        return null;
    }

    @Override
    public String getLog() {
        return null;
    }

    @Override
    public String getStone() {
        return null;
    }

    @Override
    public String getCobblestone() {
        return null;
    }

    @Override
    public String getSand() {
        return null;
    }

    @Override
    public String getSandstone() {
        return null;
    }

    @Override
    public String getLeaves() {
        return null;
    }

    @Override
    public String getLeavesG() {
        return null;
    }

    @Override
    public String getPlank() {
        return null;
    }

    @Override
    public String getBush() {
        return null;
    }

    @Override
    public void spawnGenFeatureParasite(World worldIn, BlockPos pos, Random rand) {
    }

    @Override
    public void spawnGenRoofParasite(World worldIn, BlockPos pos, Random rand) {
    }
}

