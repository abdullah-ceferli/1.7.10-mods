/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import java.util.ArrayDeque;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.init.Blocks;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class BlockDiseasedSponge
extends BlockBase {
    public BlockDiseasedSponge() {
        super(Material.sponge, "diseased_sponge", 0.6f, true, false);
        this.setStepSound(SoundType.PLANT);
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
    }

    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, world, pos, blockIn, fromPos);
    }

    public static boolean absorbDeadBlood(World world, BlockPos origin, Block deadBlood) {
        if (deadBlood == null || deadBlood == Blocks.air) {
            return false;
        }
        int removed = 0;
        ArrayDeque<Node> q = new ArrayDeque<Node>();
        q.add(new Node(origin, 0));
        while (!q.isEmpty() && removed < 64) {
            Node n = (Node)q.poll();
            for (EnumFacing f : EnumFacing.values()) {
                BlockPos p = n.pos.offset(f);
                IBlockState st = BlockStates.get(world, p);
                if (st.getBlock() != deadBlood) continue;
                WorldAccess.setBlockToAir(world, p);
                ++removed;
                if (n.depth >= 6) continue;
                q.add(new Node(p, n.depth + 1));
            }
        }
        return removed > 0;
    }

    private static class Node {
        final BlockPos pos;
        final int depth;

        Node(BlockPos pos, int depth) {
            this.pos = pos;
            this.depth = depth;
        }
    }
}

