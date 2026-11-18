/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.ITileEntityProvider
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.block.TileEntityFogNullifier;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import com.dhanantry.scapeandrunparasites.backport.util.NonNullList;
import net.minecraft.util.SoundCategory;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import com.dhanantry.scapeandrunparasites.backport.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFogNullifier
extends BlockBase
implements ITileEntityProvider {
    private static final String TAG_USES = "UsesRemaining";

    public BlockFogNullifier(String name) {
        super(Material.rock, name, 2.0f, true, false);
        this.setResistance(10.0f);
        this.setHarvestLevel("pickaxe", 0);
        this.setLightOpacity(0);
    }

    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    public TileEntity createNewTileEntity(World w, int m) {
        return new TileEntityFogNullifier();
    }

    public void onBlockPlacedBy(World w, BlockPos p, IBlockState s, EntityLivingBase placer, ItemStack stack) {
        if (!w.isRemote) {
            TileEntity te = WorldAccess.getTileEntity(w, p);
            if (te instanceof TileEntityFogNullifier) {
                int from = BlockFogNullifier.getUsesFromStack(stack);
                int uses = from >= 0 ? from : Math.max(0, SRPConfigWorld.fogNullifierMaxUses);
                ((TileEntityFogNullifier)te).setUsesRemaining(uses);
                te.markDirty();
            }
            this.attemptConsumeUseAndClear(w, p);
        }
    }

    public void neighborChanged(IBlockState s, World w, BlockPos p, Block b, BlockPos f) {
        if (!w.isRemote) {
            this.attemptConsumeUseAndClear(w, p);
        }
    }

    public boolean onBlockActivated(World w, BlockPos p, IBlockState s, EntityPlayer pl, EnumHand h, EnumFacing f, float x, float y, float z) {
        if (!w.isRemote) {
            return this.attemptConsumeUseAndClear(w, p);
        }
        return true;
    }

    public boolean isOpaqueCube(IBlockState s) {
        return false;
    }

    public boolean renderAsNormalBlock(IBlockState s) {
        return true;
    }

    private boolean attemptConsumeUseAndClear(World w, BlockPos p) {
        TileEntity te = WorldAccess.getTileEntity(w, p);
        if (!(te instanceof TileEntityFogNullifier)) {
            return false;
        }
        TileEntityFogNullifier data = (TileEntityFogNullifier)te;
        if (data.getUsesRemaining() <= 0) {
            return false;
        }
        int cleared = this.clearConnectedFog(w, p);
        if (cleared > 0) {
            w.playSound(null, p, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.9f, 1.0f);
            int left = data.getUsesRemaining() - 1;
            data.setUsesRemaining(left);
            data.markDirty();
            if (left <= 0) {
                w.playSound(null, p, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 0.8f, 0.9f);
                WorldAccess.destroyBlock(w, p, false);
            }
            return true;
        }
        return false;
    }

    private int clearConnectedFog(World w, BlockPos origin) {
        ArrayDeque<BlockPos> q = new ArrayDeque<BlockPos>();
        HashSet<BlockPos> seen = new HashSet<BlockPos>();
        for (EnumFacing f : EnumFacing.faceList) {
            BlockPos n = origin.offset(f);
            if (!this.isParasiteFog(w, n)) continue;
            q.add(n);
            seen.add(n);
        }
        if (q.isEmpty()) {
            return 0;
        }
        int cleared = 0;
        int cap = 500000;
        Random r = w.rand;
        while (!q.isEmpty() && cleared < cap) {
            BlockPos p = (BlockPos)q.pollFirst();
            if (!this.isParasiteFog(w, p)) continue;
            this.spawnDispelParticles(w, p, r);
            BlockStates.set(w, p, BlockStates.defaultState(Blocks.air), 3);
            ++cleared;
            for (EnumFacing f : EnumFacing.faceList) {
                BlockPos n = p.offset(f);
                if (seen.contains(n) || !this.isParasiteFog(w, n)) continue;
                seen.add(n);
                q.addLast(n);
            }
        }
        return cleared;
    }

    private void spawnDispelParticles(World w, BlockPos p, Random r) {
        for (int i = 0; i < 4; ++i) {
            double x = (double)p.getX() + 0.2 + r.nextDouble() * 0.6;
            double y = (double)p.getY() + 0.2 + r.nextDouble() * 0.6;
            double z = (double)p.getZ() + 0.2 + r.nextDouble() * 0.6;
            w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0, 0.0, 0.0, new int[0]);
        }
        if (r.nextFloat() < 0.3f) {
            int extra = 3 + r.nextInt(3);
            for (int i = 0; i < extra; ++i) {
                double x = (double)p.getX() + r.nextDouble();
                double y = (double)p.getY() + r.nextDouble();
                double z = (double)p.getZ() + r.nextDouble();
                w.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0, 0.0, 0.0, new int[0]);
            }
        }
    }

    private boolean isParasiteFog(World w, BlockPos p) {
        Block b = BlockStates.get(w, p).getBlock();
        try {
            if (SRPBlocks.ParasiteFog != null && b == SRPBlocks.ParasiteFog) {
                return true;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        String id = String.valueOf(RegistryNames.get(b));
        return "srparasites:parasitefog".equals(id) || "srparasites:parasitefog".equals(id);
    }

    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess acc, BlockPos pos, IBlockState state, int fortune) {
        int uses;
        World w;
        TileEntity te;
        if (acc instanceof World && (te = WorldAccess.getTileEntity((w = (World)acc), pos)) instanceof TileEntityFogNullifier && (uses = ((TileEntityFogNullifier)te).getUsesRemaining()) > 0) {
            ItemStack stack = new ItemStack((Block)this);
            BlockFogNullifier.setUsesOnStack(stack, uses);
            drops.add((Object)stack);
        }
    }

    public ItemStack getPickBlock(IBlockState s, RayTraceResult hit, World w, BlockPos pos, EntityPlayer player) {
        int uses;
        ItemStack out = new ItemStack((Block)this);
        TileEntity te = WorldAccess.getTileEntity(w, pos);
        if (te instanceof TileEntityFogNullifier && (uses = ((TileEntityFogNullifier)te).getUsesRemaining()) >= 0) {
            BlockFogNullifier.setUsesOnStack(out, uses);
        }
        return out;
    }

    private static int getUsesFromStack(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_USES)) {
            return stack.getTagCompound().getInteger(TAG_USES);
        }
        return -1;
    }

    private static void setUsesOnStack(ItemStack stack, int uses) {
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        tag.setInteger(TAG_USES, Math.max(0, uses));
        stack.setTagCompound(tag);
    }
}

