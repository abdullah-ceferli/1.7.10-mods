/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedBush;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedRemain;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import com.dhanantry.scapeandrunparasites.item.ItemBase;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayDeque;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumActionResult;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemGreekFire
extends ItemBase {
    private static final int MAX_SPREAD = 12000;
    private static final int LINK_RADIUS = 8;
    private static final int PARTICLE_EVERY = 8;
    private static final int MAX_PARTICLE_SPAWNS = 24;

    public ItemGreekFire(String name, int maxStack, byte id) {
        super(name, maxStack, id);
        this.maxStackSize = 1;
        this.setMaxDamage(4);
    }

    public boolean isDamageable() {
        return true;
    }

    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        IBlockState state = BlockStates.get(world, pos);
        Block block = state.getBlock();
        if (!this.isTargetBush(block)) {
            return EnumActionResult.FAIL;
        }
        this.burnBushCluster(world, pos);
        world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0f, 0.9f + world.rand.nextFloat() * 0.2f);
        if (!player.isCreative()) {
            player.getHeldItem(hand).damageItem(1, (EntityLivingBase)player);
        }
        return EnumActionResult.SUCCESS;
    }

    private void burnBushCluster(World world, BlockPos origin) {
        ArrayDeque<BlockPos> queue = new ArrayDeque<BlockPos>();
        LongOpenHashSet visited = new LongOpenHashSet();
        queue.add(origin);
        visited.add(ItemGreekFire.packPos(origin.getX(), origin.getY(), origin.getZ()));
        int burned = 0;
        int particleBursts = 0;
        BlockPos.MutableBlockPos scanPos = new BlockPos.MutableBlockPos();
        while (!queue.isEmpty() && burned < 12000) {
            BlockPos current = (BlockPos)queue.poll();
            IBlockState state = BlockStates.get(world, current);
            Block block = state.getBlock();
            if (!this.isTargetBush(block)) continue;
            WorldAccess.setBlockToAir(world, current);
            if (++burned % 8 == 0 && particleBursts < 24) {
                this.spawnSmoke(world, current);
                ++particleBursts;
            }
            int cx = current.getX();
            int cy = current.getY();
            int cz = current.getZ();
            for (int dx = -8; dx <= 8; ++dx) {
                for (int dy = -8; dy <= 8; ++dy) {
                    for (int dz = -8; dz <= 8; ++dz) {
                        long key;
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        int nx = cx + dx;
                        int ny = cy + dy;
                        int nz = cz + dz;
                        if (ny < 0 || ny > 255 || visited.contains(key = ItemGreekFire.packPos(nx, ny, nz))) continue;
                        visited.add(key);
                        scanPos.setPos(nx, ny, nz);
                        if (!this.isTargetBush(BlockStates.get(world, (BlockPos)scanPos).getBlock())) continue;
                        queue.add(new BlockPos(nx, ny, nz));
                    }
                }
            }
        }
        if (burned > 0 && particleBursts == 0) {
            this.spawnSmoke(world, origin);
        }
    }

    private void spawnSmoke(World world, BlockPos pos) {
        if (!(world instanceof WorldServer)) {
            return;
        }
        WorldServer ws = (WorldServer)world;
        double x = (double)pos.getX() + 0.5;
        double y = (double)pos.getY() + 0.5;
        double z = (double)pos.getZ() + 0.5;
        ws.spawnParticle(EnumParticleTypes.CLOUD, x, y, z, 4, 0.25, 0.25, 0.25, 0.01, new int[0]);
        ws.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, 2, 0.2, 0.2, 0.2, 0.01, new int[0]);
    }

    private boolean isTargetBush(Block block) {
        return block instanceof BlockParasiteBush || block instanceof BlockInfestedBush || block instanceof BlockInfestedRemain || block instanceof BlockGore;
    }

    private static long packPos(int x, int y, int z) {
        return (long)(x & 0x3FFFFFF) << 38 | (long)(y & 0xFFF) << 26 | (long)(z & 0x3FFFFFF);
    }
}

