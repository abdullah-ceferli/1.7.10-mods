/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSand
 *  net.minecraft.block.BlockSandStone
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 *  net.minecraft.world.biome.Biome$BiomeProperties
 *  net.minecraft.world.gen.feature.WorldGenAbstractTree
 */
package com.dhanantry.scapeandrunparasites.world.biome;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubble;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteDecorator;
import java.util.Collection;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public abstract class BiomeParasiteBase
extends Biome {
    protected WorldGenAbstractTree tree;

    public BiomeParasiteBase(Biome.BiomeProperties properties) {
        super(properties);
    }

    public void setBlocks() {
        String[] blockList = this.getDirt().split(":");
        Block one = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
        this.topBlock = BlockStates.fromMeta(one, Integer.parseInt(blockList[2]));
        blockList = this.getStone().split(":");
        one = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
        this.fillerBlock = BlockStates.fromMeta(one, Integer.parseInt(blockList[2]));
        this.theBiomeDecorator = new BiomeParasiteDecorator(this);
    }

    public void mobListClear() {
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
    }

    public abstract float getRedValue();

    public abstract float getGreenValue();

    public abstract float getBlueValue();

    public abstract String[] getBlockList();

    public abstract String getDirt();

    public abstract String getGravel();

    public abstract String getSand();

    public abstract String getLog();

    public abstract String getStone();

    public abstract String getCobblestone();

    public abstract String getSandstone();

    public abstract String getLeaves();

    public abstract String getLeavesG();

    public abstract String getPlank();

    public abstract String getBush();

    public abstract void spawnGenFeatureParasite(World var1, BlockPos var2, Random var3);

    public abstract void spawnGenRoofParasite(World var1, BlockPos var2, Random var3);

    public int convertBlock(BlockPos helper, World worldIn, Random rand) {
        Block one;
        String[] blockList;
        int currentMeta;
        IBlockState lookingState = BlockStates.get(worldIn, helper);
        Material mat = lookingState.getMaterial();
        Block lookingBlock = lookingState.getBlock();
        if (lookingBlock instanceof IMetaName) {
            return 0;
        }
        if (mat == Material.air || mat == Material.water || mat == Material.lava) {
            return 0;
        }
        ResourceLocation rl = RegistryNames.get(lookingBlock);
        if (rl == null) {
            return 0;
        }
        String lookingName = rl.toString();
        if (BiomeParasiteBase.transformBlockList(helper, worldIn, lookingName, currentMeta = BlockStates.meta(lookingBlock, lookingState), blockList = this.getBlockList())) {
            return 1;
        }
        if (ParasiteEventWorld.blockException(worldIn, helper, lookingBlock, lookingState, SRPConfigWorld.blockBBiomeList, SRPConfigWorld.blockBBiomeListWhite, SRPConfigWorld.biomeBlockIMaxH)) {
            return 0;
        }
        if (BiomeParasiteBase.transformBlockList(helper, worldIn, lookingName, currentMeta, blockList)) {
            return 1;
        }
        if (lookingBlock.isWood((IBlockAccess)worldIn, helper) || lookingBlock == SRPBlocks.InfestedTrunk) {
            blockList = this.getLog().split(":");
            Block one2 = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
            BlockStates.set(worldIn, helper, BlockStates.fromMeta(one2, Integer.parseInt(blockList[2])));
            return 1;
        }
        if (lookingBlock.isLeaves(lookingState, (IBlockAccess)worldIn, helper)) {
            BlockPos below;
            blockList = this.getLeaves().split(":");
            Block one3 = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
            BlockStates.set(worldIn, helper, BlockStates.fromMeta(one3, Integer.parseInt(blockList[2])));
            if (rand.nextInt(100) < 30 && WorldAccess.isAirBlock(worldIn, below = helper.down())) {
                blockList = this.getLeavesG().split(":");
                one3 = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
                IBlockState growth = BiomeParasiteBase.setFacingIfPresent(BlockStates.fromMeta(one3, Integer.parseInt(blockList[2])), EnumFacing.DOWN);
                BlockStates.set(worldIn, below, growth, 2);
            }
            return 1;
        }
        if (lookingBlock == SRPBlocks.InfestedStain) {
            blockList = this.getDirt().split(":");
            one = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
            BlockStates.set(worldIn, helper, BlockStates.fromMeta(one, Integer.parseInt(blockList[2])));
            this.spawnGenFeatureParasite(worldIn, helper.up(), rand);
            this.spawnGenRoofParasite(worldIn, helper.down(), rand);
        }
        if (mat == Material.ground || mat == Material.grass) {
            blockList = this.getDirt().split(":");
            one = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
            BlockStates.set(worldIn, helper, BlockStates.fromMeta(one, Integer.parseInt(blockList[2])));
            this.spawnGenFeatureParasite(worldIn, helper.up(), rand);
            this.spawnGenRoofParasite(worldIn, helper.down(), rand);
            return 1;
        }
        if (lookingBlock instanceof BlockSand) {
            blockList = this.getSand().split(":");
            one = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
            BlockStates.set(worldIn, helper, BlockStates.fromMeta(one, Integer.parseInt(blockList[2])));
            this.spawnGenFeatureParasite(worldIn, helper.up(), rand);
            this.spawnGenRoofParasite(worldIn, helper.down(), rand);
            return 1;
        }
        if (lookingBlock instanceof BlockSandStone) {
            blockList = this.getSandstone().split(":");
            one = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
            BlockStates.set(worldIn, helper, BlockStates.fromMeta(one, Integer.parseInt(blockList[2])));
            this.spawnGenFeatureParasite(worldIn, helper.up(), rand);
            this.spawnGenRoofParasite(worldIn, helper.down(), rand);
            return 1;
        }
        if (lookingBlock == Blocks.gravel || mat == Material.sand) {
            blockList = this.getGravel().split(":");
            one = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
            BlockStates.set(worldIn, helper, BlockStates.fromMeta(one, Integer.parseInt(blockList[2])));
            this.spawnGenFeatureParasite(worldIn, helper.up(), rand);
            this.spawnGenRoofParasite(worldIn, helper.down(), rand);
            return 1;
        }
        if (lookingBlock == Blocks.cobblestone) {
            blockList = this.getCobblestone().split(":");
            one = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
            BlockStates.set(worldIn, helper, BlockStates.fromMeta(one, Integer.parseInt(blockList[2])));
            this.spawnGenFeatureParasite(worldIn, helper.up(), rand);
            this.spawnGenRoofParasite(worldIn, helper.down(), rand);
            return 1;
        }
        if (mat == Material.rock) {
            if (lookingBlock == SRPBlocks.BiomeHeart || lookingBlock == SRPBlocks.ColonyHeart) {
                return 0;
            }
            if (lookingBlock == Blocks.obsidian) {
                BlockStates.set(worldIn, helper, BlockStates.defaultState(SRPBlocks.ParasiteRubble).withProperty(BlockParasiteRubble.VARIANT, (Comparable)((Object)BlockParasiteRubble.EnumType.OBSIDIAN)));
            } else if (RegistryNames.get(lookingBlock).toString().contains("brick")) {
                BlockStates.set(worldIn, helper, BlockStates.defaultState(SRPBlocks.ParasiteRubble).withProperty(BlockParasiteRubble.VARIANT, (Comparable)((Object)BlockParasiteRubble.EnumType.BRICKS)));
            } else {
                blockList = this.getStone().split(":");
                one = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
                BlockStates.set(worldIn, helper, BlockStates.fromMeta(one, Integer.parseInt(blockList[2])));
            }
            this.spawnGenFeatureParasite(worldIn, helper.up(), rand);
            this.spawnGenRoofParasite(worldIn, helper.down(), rand);
            return 1;
        }
        if (mat == Material.plants) {
            BlockStates.set(worldIn, helper, BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.FLESH)));
            return 1;
        }
        if (mat == Material.ice || mat == Material.packedIce) {
            BlockStates.set(worldIn, helper, BlockStates.defaultState(SRPBlocks.BloodyIce));
            return 1;
        }
        if (mat == Material.iron) {
            BlockStates.set(worldIn, helper, BlockStates.defaultState(SRPBlocks.ParasiteRubble).withProperty(BlockParasiteRubble.VARIANT, (Comparable)((Object)BlockParasiteRubble.EnumType.METAL)));
            return 1;
        }
        if (mat == Material.wood && lookingBlock != SRPBlocks.InfestedTrunk) {
            if (RegistryNames.get(lookingBlock).toString().contains("mushroom")) {
                BlockStates.set(worldIn, helper, BlockStates.defaultState(SRPBlocks.ParasiteRubble).withProperty(BlockParasiteRubble.VARIANT, (Comparable)((Object)BlockParasiteRubble.EnumType.FUNGUS)));
            } else {
                BlockStates.set(worldIn, helper, BlockStates.defaultState(SRPBlocks.ParasiteRubble).withProperty(BlockParasiteRubble.VARIANT, (Comparable)((Object)BlockParasiteRubble.EnumType.WOOD)));
            }
            return 1;
        }
        return 0;
    }

    private static IBlockState setFacingIfPresent(IBlockState state, EnumFacing face) {
        Collection props = state.getPropertyKeys();
        for (IProperty p : props) {
            IProperty pf;
            if (!p.getName().equalsIgnoreCase("facing") || p.getValueClass() != EnumFacing.class || !(pf = p).getAllowedValues().contains(face)) continue;
            return state.withProperty(pf, (Comparable)face);
        }
        return state;
    }

    public static boolean transformBlockList(BlockPos helper, World worldIn, String name, int meta, String[] rules) {
        if (rules == null || rules.length == 0) {
            return false;
        }
        for (String rule : rules) {
            Block putting;
            String[] nm;
            String[] dst;
            String[] src;
            String[] parts = rule.split(";");
            if (parts.length != 2 || (src = parts[0].split(":")).length != 3 || (dst = parts[1].split(":")).length != 3 || (nm = name.split(":")).length != 2) continue;
            if ("minecraft:glass".equals(name)) {
                System.out.println("[SRP] transformBlockList evaluating GLASS: rule=" + rule + " meta=" + meta);
            }
            if (!nm[0].equals(src[0]) || !nm[1].equals(src[1]) || meta != Integer.parseInt(src[2]) || (putting = Block.getBlockFromName((String)(dst[0] + ":" + dst[1]))) == null) continue;
            int dstMeta = Integer.parseInt(dst[2]);
            BlockStates.set(worldIn, helper, BlockStates.fromMeta(putting, dstMeta), 3);
            return true;
        }
        return false;
    }
}

