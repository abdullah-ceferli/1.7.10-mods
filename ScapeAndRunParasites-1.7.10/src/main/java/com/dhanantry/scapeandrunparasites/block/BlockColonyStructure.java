/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.material.EnumPushReaction
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteSapling;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityKol;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.util.convert.BeckonBlockInfestation;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteBigBall;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyB1;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyB2;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyB3;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyB4;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyBS1;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyBS2;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyBS3;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteMouth;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteNodeCore;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteSpine;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTallFlower;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTenFlower;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTree;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTreeThin;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IProperty;
import com.dhanantry.scapeandrunparasites.backport.state.PropertyInteger;
import com.dhanantry.scapeandrunparasites.backport.state.BlockStateContainer;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.AxisAlignedBB;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class BlockColonyStructure
extends BlockBase {
    public static final PropertyInteger ACTIVE = PropertyInteger.create((String)"active", (int)0, (int)3);

    public BlockColonyStructure(Material material, String name, float hardness, boolean creative, boolean tick, float resistance) {
        super(material, name, hardness, creative, tick, resistance);
        this.setStepSound(SRPSoundTypes.FLESH);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)ACTIVE, (Comparable)Integer.valueOf(0)));
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        int i = 101;
        Random rand = new Random();
        BlockStates.set(worldIn, pos, BlockStates.defaultState(Blocks.air));
        if (worldIn.isRemote) {
            return false;
        }
        switch (i) {
            case -2: {
                this.updateTick(worldIn, pos, state, rand);
                break;
            }
            case 1: {
                EntityKol out = new EntityKol(worldIn);
                out.setPositionAndRotation(pos.getX(), pos.getY() + 1, pos.getZ(), 0.0f, 0.0f);
                worldIn.spawnEntityInWorld((Entity)out);
                out.setOrigin(pos);
                break;
            }
            case 2: {
                WorldGenParasiteColonyB3 tree = new WorldGenParasiteColonyB3(false, 1);
                tree.generate(worldIn, new Random(), pos.up());
                break;
            }
            case 3: {
                BlockStates.set(worldIn, pos, BlockStates.defaultState(Blocks.water), 1);
                break;
            }
            case 4: {
                break;
            }
            case 5: {
                ParasiteEventEntity.spawnFromBlock(worldIn, new String[]{"srparasites:worker;1"}, 5, pos.up());
                break;
            }
            case 6: {
                break;
            }
            case 7: {
                WorldGenParasiteTreeThin gen = new WorldGenParasiteTreeThin(false);
                if (gen.generate(worldIn, rand, pos.up(3)) || BlockStates.get(worldIn, pos.up(3)).getBlock() != Blocks.air) break;
                BlockStates.set(worldIn, pos.up(3), BlockStates.defaultState(SRPBlocks.ParasiteSapling).withProperty(BlockParasiteSapling.VARIANT, (Comparable)((Object)BlockParasiteSapling.EnumType.TREE)));
                break;
            }
            case 8: {
                WorldGenParasiteTree gen = new WorldGenParasiteTree(false);
                if (gen.generate(worldIn, rand, pos.up(3)) || BlockStates.get(worldIn, pos.up(3)).getBlock() != Blocks.air) break;
                BlockStates.set(worldIn, pos.up(3), BlockStates.defaultState(SRPBlocks.ParasiteSapling).withProperty(BlockParasiteSapling.VARIANT, (Comparable)((Object)BlockParasiteSapling.EnumType.TREE)));
                break;
            }
            case 9: {
                WorldGenParasiteTenFlower genF = new WorldGenParasiteTenFlower(false);
                if (genF.generate(worldIn, rand, pos.up(3)) || BlockStates.get(worldIn, pos.up(3)).getBlock() != Blocks.air) break;
                BlockStates.set(worldIn, pos.up(3), BlockStates.defaultState(SRPBlocks.ParasiteSapling).withProperty(BlockParasiteSapling.VARIANT, (Comparable)((Object)BlockParasiteSapling.EnumType.TREE)));
                break;
            }
            case 10: {
                WorldGenParasiteTallFlower genTF = new WorldGenParasiteTallFlower(false);
                if (genTF.generate(worldIn, rand, pos.up(3)) || BlockStates.get(worldIn, pos.up(3)).getBlock() != Blocks.air) break;
                BlockStates.set(worldIn, pos.up(3), BlockStates.defaultState(SRPBlocks.ParasiteSapling).withProperty(BlockParasiteSapling.VARIANT, (Comparable)((Object)BlockParasiteSapling.EnumType.TREE)));
                break;
            }
            case 11: {
                WorldGenParasiteSpine genS = new WorldGenParasiteSpine(false);
                if (genS.generate(worldIn, rand, pos.up(3)) || BlockStates.get(worldIn, pos.up(3)).getBlock() != Blocks.air) break;
                BlockStates.set(worldIn, pos.up(3), BlockStates.defaultState(SRPBlocks.ParasiteSapling).withProperty(BlockParasiteSapling.VARIANT, (Comparable)((Object)BlockParasiteSapling.EnumType.TREE)));
                break;
            }
            case 12: {
                WorldGenParasiteMouth genM = new WorldGenParasiteMouth(false);
                if (genM.generate(worldIn, rand, pos.up(3)) || BlockStates.get(worldIn, pos.up(3)).getBlock() != Blocks.air) break;
                BlockStates.set(worldIn, pos.up(3), BlockStates.defaultState(SRPBlocks.ParasiteSapling).withProperty(BlockParasiteSapling.VARIANT, (Comparable)((Object)BlockParasiteSapling.EnumType.TREE)));
                break;
            }
            case 13: {
                WorldGenParasiteNodeCore genN = new WorldGenParasiteNodeCore(false, 1, 1);
                if (genN.generate(worldIn, rand, pos.up(6)) || BlockStates.get(worldIn, pos.up(6)).getBlock() != Blocks.air) break;
                BlockStates.set(worldIn, pos.up(6), BlockStates.defaultState(SRPBlocks.ParasiteSapling).withProperty(BlockParasiteSapling.VARIANT, (Comparable)((Object)BlockParasiteSapling.EnumType.TREE)));
                break;
            }
            case 14: {
                BeckonBlockInfestation.spawnGenRoofInfested(worldIn, pos.down(1), rand);
                break;
            }
            case 15: {
                WorldGenParasiteBigBall ball = new WorldGenParasiteBigBall(false);
                ball.generate(worldIn, new Random(), pos.up());
                break;
            }
            case 100: {
                int offset = 20;
                WorldGenParasiteColonyB1 bu1 = new WorldGenParasiteColonyB1(false, 1);
                bu1.generate(worldIn, new Random(), pos.east(offset));
                WorldGenParasiteColonyB2 bu2 = new WorldGenParasiteColonyB2(false, 1);
                bu2.generate(worldIn, new Random(), pos.north(offset));
                WorldGenParasiteColonyB3 bu3 = new WorldGenParasiteColonyB3(false, 1);
                bu3.generate(worldIn, new Random(), pos.west(offset));
                WorldGenParasiteColonyB4 b4 = new WorldGenParasiteColonyB4(false, 1);
                b4.generate(worldIn, new Random(), pos.south(offset));
                break;
            }
            case 101: {
                int n = 20;
            }
        }
        return false;
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!WorldAccess.isAreaLoaded(worldIn, pos, 3) || worldIn.isRemote) {
            return;
        }
        if (!SRPConfigWorld.coloniesActivated) {
            return;
        }
        SRPWorldData data = SRPWorldData.get(worldIn);
        if (data.nearestColonyPosition(pos, false) == null) {
            return;
        }
        int meta = BlockStates.meta(this, state);
        block0 : switch (meta) {
            case 1: {
                switch (rand.nextInt(3)) {
                    case 1: {
                        WorldGenParasiteColonyB3 b3 = new WorldGenParasiteColonyB3(false, 2);
                        b3.generate(worldIn, new Random(), pos);
                        break block0;
                    }
                    case 2: {
                        WorldGenParasiteColonyB2 b2 = new WorldGenParasiteColonyB2(false, 2);
                        b2.generate(worldIn, new Random(), pos);
                        break block0;
                    }
                    case 3: {
                        WorldGenParasiteColonyB4 b4 = new WorldGenParasiteColonyB4(false, 2);
                        b4.generate(worldIn, new Random(), pos);
                        break block0;
                    }
                }
                WorldGenParasiteColonyB1 b1 = new WorldGenParasiteColonyB1(false, 2);
                b1.generate(worldIn, new Random(), pos);
                break;
            }
            case 2: {
                switch (rand.nextInt(3)) {
                    case 1: {
                        WorldGenParasiteColonyBS1 bs1 = new WorldGenParasiteColonyBS1(false, 2);
                        bs1.generate(worldIn, new Random(), pos);
                        break block0;
                    }
                    case 2: {
                        WorldGenParasiteColonyBS3 bs3 = new WorldGenParasiteColonyBS3(false, 2);
                        bs3.generate(worldIn, new Random(), pos);
                        break block0;
                    }
                }
                WorldGenParasiteColonyBS2 bs2 = new WorldGenParasiteColonyBS2(false, 2);
                bs2.generate(worldIn, new Random(), pos);
            }
        }
        BlockStates.set(worldIn, pos, BlockStates.fromMeta(this, 3));
    }

    private boolean makePillar(World worldIn, BlockPos pos, int totalCheck, int blocks) {
        pos = pos.up();
        int times = 0;
        Block look = BlockStates.get(worldIn, pos).getBlock();
        while (blocks > 0) {
            boolean g = false;
            if (look == SRPBlocks.ParasiteStain) {
                pos = pos.up();
                look = BlockStates.get(worldIn, pos).getBlock();
                if (!(g = ++times >= totalCheck)) continue;
                return true;
            }
            if (look == Blocks.air || !(look instanceof BlockLeaves)) {
                // empty if block
            }
            g = ++times >= totalCheck;
            this.placePillarBlock(worldIn, pos, --blocks <= 0);
            pos = pos.up();
            look = BlockStates.get(worldIn, pos).getBlock();
            if (!g) continue;
            return true;
        }
        return false;
    }

    private void placePillarBlock(World worldIn, BlockPos pos, boolean flag) {
        AxisAlignedBB axisalignedbb2;
        List moblist2;
        int xx = pos.getX();
        int zz = pos.getZ();
        int yy = pos.getY();
        int range = 10;
        for (int x = xx - range; x <= xx + range; ++x) {
            for (int z = zz - range; z <= zz + range; ++z) {
                BlockPos neww = new BlockPos(x, yy, z);
                float har = BlockStates.get(worldIn, neww).getBlockHardness(worldIn, neww);
                if (har > 7.0f || BlockStates.get(worldIn, neww).getBlock() instanceof BlockBase) continue;
                BlockStates.set(worldIn, neww, BlockStates.defaultState(Blocks.air));
            }
        }
        BlockStates.set(worldIn, pos, BlockStates.defaultState(SRPBlocks.ParasiteStain).withProperty(BlockParasiteStain.VARIANT, (Comparable)((Object)BlockParasiteStain.EnumType.FLESH)));
        if (flag && (moblist2 = EntityQueries.entities(worldIn, EntityItem.class, axisalignedbb2 = Boxes.grow(Boxes.create((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)), 32.0, 16.0, 32.0))).size() > 7) {
            BlockStates.set(worldIn, pos.north(), BlockStates.defaultState(SRPBlocks.ParasiteCanisterActive), 3);
        }
    }

    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.BLOCK;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{ACTIVE});
    }

    public int getMetaFromState(IBlockState state) {
        return (Integer)state.getValue((IProperty)ACTIVE);
    }

    public IBlockState getStateFromMeta(int meta) {
        return BlockStates.defaultState(this).withProperty((IProperty)ACTIVE, (Comparable)Integer.valueOf(meta & 3));
    }
}

