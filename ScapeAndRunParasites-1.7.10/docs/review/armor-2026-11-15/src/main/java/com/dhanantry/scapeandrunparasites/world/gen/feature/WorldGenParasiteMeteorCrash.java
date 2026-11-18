/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockGlass
 *  net.minecraft.block.BlockPane
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteLoot;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubble;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenCustomStructures;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyBase;
import com.dhanantry.scapeandrunparasites.world.gen.feature.util.WorldGenMeteorImpactUtil;
import com.dhanantry.scapeandrunparasites.world.gen.structure.WorldGenStructure;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenParasiteMeteorCrash
extends WorldGenParasiteColonyBase {
    public WorldGenParasiteMeteorCrash(boolean notify, int stage) {
        super(notify, stage);
        this.wall = BlockStates.defaultState(SRPBlocks.ParasiteRubbleDense).withProperty(BlockParasiteRubbleDense.VARIANT, (Comparable)((Object)BlockParasiteRubbleDense.EnumType.WALL));
        this.tacle = BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.FEELER));
        this.floor = BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.DIRT));
    }

    public boolean generate(World worldIn, Random rand, BlockPos posss) {
        int adjustedR;
        int depthDrivenR;
        int openNeeded;
        int baseDepth;
        BlockPos og = posss;
        BlockPos impactCenter = WorldAccess.getTopSolidOrLiquidBlock(worldIn, posss).down();
        if (this.type != 5) {
            String out = "meteor_fragment_large1";
            switch (worldIn.rand.nextInt(9)) {
                case 1: {
                    out = "meteor_fragment_large2";
                    break;
                }
                case 2: {
                    out = "meteor_fragment_large3";
                    break;
                }
                case 3: {
                    out = "meteor_fragment_small1";
                    break;
                }
                case 4: {
                    out = "meteor_fragment_small2";
                    break;
                }
                case 5: {
                    out = "meteor_fragment_small3";
                    break;
                }
                case 6: {
                    out = "meteor_fragment_small4";
                    break;
                }
                case 7: {
                    out = "meteor_fragment_small5";
                    break;
                }
                case 8: {
                    out = "meteor_fragment_small6";
                }
            }
            BlockPos surface = impactCenter;
            BlockPos origin = surface.add(2, 2, 2);
            WorldGenCustomStructures.generateInPosition(new WorldGenStructure(out), rand, worldIn, origin, -2, -2, -2);
            int fires = 18 + rand.nextInt(18);
            int fireRadius = 10;
            for (int i = 0; i < fires; ++i) {
                IBlockState at;
                IBlockState below;
                BlockPos firePos;
                BlockPos top;
                int dz;
                int dx = rand.nextInt(fireRadius * 2 + 1) - fireRadius;
                if (dx * dx + (dz = rand.nextInt(fireRadius * 2 + 1) - fireRadius) * dz > fireRadius * fireRadius || (top = WorldAccess.getTopSolidOrLiquidBlock(worldIn, surface.add(dx, 0, dz)).down()).getY() <= 5 || !WorldAccess.isBlockLoaded(worldIn, firePos = top.up()) || (below = BlockStates.get(worldIn, top)).getMaterial() == Material.air || below.getMaterial() == Material.water || below.getMaterial() == Material.lava || (at = BlockStates.get(worldIn, firePos)).getMaterial() != Material.air) continue;
                BlockStates.set(worldIn, firePos, BlockStates.defaultState(Blocks.fire), 2);
            }
            return true;
        }
        BlockPos enter = impactCenter.down(10);
        for (int i = 0; i < 20; ++i) {
            this.replaceCircleGround(worldIn, enter.up(i), this.type * 7, BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.RED)));
        }
        int rad = this.type;
        posss = impactCenter.down(rad + rad);
        int minCenterY = rad * 16 + 6;
        if (posss.getY() < minCenterY) {
            posss = new BlockPos(posss.getX(), minCenterY, posss.getZ());
        }
        this.generateSphere(worldIn, posss, rad * 16, rad * 16, rand, false, 1, false, 1, 1, 5, BlockStates.defaultState(Blocks.air), BlockStates.defaultState(Blocks.air), BlockStates.defaultState(Blocks.air), 2);
        float yaw = rand.nextFloat() * 360.0f;
        double dirX = -MathHelper.sin((float)(yaw * ((float)Math.PI / 180)));
        double dirZ = MathHelper.cos((float)(yaw * ((float)Math.PI / 180)));
        float steepness = 0.25f + rand.nextFloat() * 0.75f;
        double dirY = -steepness;
        IBlockState rim = BlockStates.defaultState(SRPBlocks.ParasiteRubbleDense).withProperty(BlockParasiteRubbleDense.VARIANT, (Comparable)((Object)BlockParasiteRubbleDense.EnumType.WALL));
        IBlockState rubble = BlockStates.defaultState(SRPBlocks.ParasiteRubble).withProperty(BlockParasiteRubble.VARIANT, (Comparable)((Object)BlockParasiteRubble.EnumType.BRICKS));
        IBlockState stainRed = BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.RED));
        IBlockState cooked = BlockStates.defaultState(SRPBlocks.CookedFlesh);
        BlockPos craterSurface = impactCenter;
        BlockPos tunnelStart = craterSurface.down(3);
        WorldGenMeteorImpactUtil.TunnelResult tunnel = WorldGenMeteorImpactUtil.carveAngledTunnel(worldIn, tunnelStart, 10, 30, dirX, dirY, dirZ);
        int baseR = rad * 8;
        int adjustedDepth = baseDepth = (int)((float)baseR * (0.4f + rand.nextFloat() * 0.2f));
        if (tunnel.anyBroken && (openNeeded = craterSurface.getY() - tunnel.lowestY) > adjustedDepth) {
            adjustedDepth = openNeeded + 3;
        }
        if ((depthDrivenR = (int)((float)adjustedDepth * 1.6f)) > (adjustedR = baseR)) {
            adjustedR = depthDrivenR;
        }
        int bottomY = craterSurface.getY() - adjustedDepth;
        int placeY = Math.max(bottomY + 1, 6);
        BlockPos structPos = new BlockPos(craterSurface.getX(), placeY, craterSurface.getZ());
        WorldGenMeteorImpactUtil.clearVegetationInArea(worldIn, craterSurface, adjustedR * 2, craterSurface.getY() - adjustedDepth - 12, craterSurface.getY() + 50);
        WorldGenMeteorImpactUtil.carveCraterBowl(worldIn, rand, craterSurface, adjustedR, adjustedDepth, steepness, rim, stainRed, cooked);
        WorldGenMeteorImpactUtil.scorchRings(worldIn, rand, craterSurface, adjustedR, stainRed);
        WorldGenMeteorImpactUtil.spawnEjecta(worldIn, rand, craterSurface, adjustedR, dirX, dirZ, rubble, stainRed);
        WorldGenMeteorImpactUtil.microCraters(worldIn, rand, craterSurface, adjustedR, dirX, dirZ, stainRed);
        int poolR = Math.max(4, adjustedR / 6);
        int poolRR = poolR * poolR;
        int skipR = 10;
        int skipRR = skipR * skipR;
        int bottomY2 = craterSurface.getY() - adjustedDepth + 1;
        if (bottomY2 < 6) {
            bottomY2 = 6;
        }
        int poolHeight = 4;
        int topY2 = bottomY2 + poolHeight;
        for (int x = -poolR; x <= poolR; ++x) {
            for (int z = -poolR; z <= poolR; ++z) {
                int d2 = x * x + z * z;
                if (d2 > poolRR || d2 <= skipRR) continue;
                for (int y = bottomY2; y <= topY2; ++y) {
                    IBlockState s;
                    BlockPos p = new BlockPos(craterSurface.getX() + x, y, craterSurface.getZ() + z);
                    if (!WorldAccess.isBlockLoaded(worldIn, p) || (s = BlockStates.get(worldIn, p)).getMaterial() != Material.air) continue;
                    BlockStates.set(worldIn, p, BlockStates.defaultState(SRPBlocks.DeadBlood), 2);
                }
            }
        }
        int half = 22;
        int fix = 2;
        BlockPos meteorPos = structPos.up(14).add(-half - fix, 0, -half - fix);
        WorldGenCustomStructures.generateInPosition(new WorldGenStructure("meteor"), rand, worldIn, meteorPos, 0, 0, 0);
        int i1 = meteorPos.up(14).getY();
        double l1 = meteorPos.up(14).add(half, 0, 0).getX();
        double i2 = meteorPos.up(14).add(0, 0, half).getZ();
        int BGrange = 11;
        for (int k2 = -1 * BGrange; k2 <= 1 * BGrange; ++k2) {
            for (int l2 = -1 * BGrange; l2 <= 1 * BGrange; ++l2) {
                for (int j = -1 * BGrange; j <= 1 * BGrange; ++j) {
                    double i3 = l1 + (double)k2;
                    double k = i1 + j;
                    double l = i2 + (double)l2;
                    BlockPos blockpos = new BlockPos(i3, k, l);
                    IBlockState iblockstate = BlockStates.get(worldIn, blockpos);
                    Block block = iblockstate.getBlock();
                    if (block == Blocks.glass || block == Blocks.stained_glass || block == Blocks.glass_pane || block == Blocks.stained_glass_pane || block instanceof BlockGlass || block instanceof BlockPane && iblockstate.getMaterial() == Material.glass) {
                        BlockStates.set(worldIn, blockpos, BlockStates.fromMeta(SRPBlocks.ParasiteStain, 2), 2);
                        continue;
                    }
                    String name = RegistryNames.get(block).toString();
                    if (block != Blocks.iron_block && block != Blocks.gold_block && block != Blocks.diamond_block) continue;
                    int rollRare = 10;
                    int rollUncommon = 4;
                    if (block == Blocks.gold_block) {
                        rollRare = 7;
                        rollUncommon = 3;
                    }
                    if (block == Blocks.diamond_block) {
                        rollRare = 4;
                        rollUncommon = 2;
                    }
                    if (rand.nextInt(rollRare) == 0) {
                        this.placeLoot(worldIn, blockpos, SRPConfigWorld.blockLootRare, BlockStates.defaultState(SRPBlocks.ParasiteLoot).withProperty(BlockParasiteLoot.VARIANT, (Comparable)((Object)BlockParasiteLoot.EnumType.RARE)));
                        BlockStates.set(worldIn, blockpos.up(), BlockStates.defaultState(SRPBlocks.DeadBlood));
                        continue;
                    }
                    if (rand.nextInt(rollUncommon) == 0) {
                        this.placeLoot(worldIn, blockpos, SRPConfigWorld.blockLootUncommon, BlockStates.defaultState(SRPBlocks.ParasiteLoot).withProperty(BlockParasiteLoot.VARIANT, (Comparable)((Object)BlockParasiteLoot.EnumType.UNCOMMON)));
                        BlockStates.set(worldIn, blockpos.up(), BlockStates.defaultState(SRPBlocks.DeadBlood));
                        continue;
                    }
                    this.placeLoot(worldIn, blockpos, SRPConfigWorld.blockLootCommon, BlockStates.defaultState(SRPBlocks.ParasiteLoot).withProperty(BlockParasiteLoot.VARIANT, (Comparable)((Object)BlockParasiteLoot.EnumType.COMMON)));
                    BlockStates.set(worldIn, blockpos.up(), BlockStates.defaultState(SRPBlocks.DeadBlood));
                }
            }
        }
        WorldGenMeteorImpactUtil.updateWaterAfterImpact(worldIn, craterSurface, adjustedR, adjustedDepth);
        return true;
    }
}

