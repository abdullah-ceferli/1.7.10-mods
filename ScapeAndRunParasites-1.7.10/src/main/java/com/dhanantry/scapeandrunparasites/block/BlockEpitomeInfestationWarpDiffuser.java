/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.client.util.ITooltipFlag
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import com.dhanantry.scapeandrunparasites.backport.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEpitomeInfestationWarpDiffuser
extends BlockBase {
    private static final int RADIUS = 256;
    private static final int MAX_BLOCKS_PER_TICK = 8192;
    private static final int MAX_BLOCKS_TOTAL = 2000000;
    private static final Map<String, DiffusionJob> JOBS = new HashMap<String, DiffusionJob>();

    public BlockEpitomeInfestationWarpDiffuser() {
        super(Material.rock, "epitome_infestation_warp_diffuser", 15.0f, true, false, 120.0f);
        this.setStepSound(SRPSoundTypes.FLESH);
        this.setLightLevel(0.4f);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }
        String key = BlockEpitomeInfestationWarpDiffuser.getJobKey(worldIn, pos);
        if (JOBS.containsKey(key)) {
            playerIn.addChatComponentMessage((IChatComponent)new ChatComponentTranslation("message.srparasites.diffuser.already_running", new Object[0]), true);
            return true;
        }
        DiffusionJob job = new DiffusionJob(pos);
        JOBS.put(key, job);
        WorldAccess.scheduleUpdate(worldIn, pos, (Block)this, 1);
        playerIn.addChatComponentMessage((IChatComponent)new ChatComponentTranslation("message.srparasites.diffuser.started", new Object[0]), true);
        return true;
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn.isRemote) {
            return;
        }
        String key = BlockEpitomeInfestationWarpDiffuser.getJobKey(worldIn, pos);
        DiffusionJob job = JOBS.get(key);
        if (job == null) {
            return;
        }
        int removedThisTick = this.processJob(worldIn, job);
        if (job.finished || job.queue.isEmpty() || job.removed >= 2000000) {
            JOBS.remove(key);
            ParasiteEventWorld.checkNodeStatus(worldIn);
            ParasiteEventWorld.checkColonyStatus(worldIn);
            if (job.removed > 0) {
                worldIn.playEvent(null, 2001, pos, BlockEpitomeInfestationWarpDiffuser.getStateId((IBlockState)state));
            }
            return;
        }
        WorldAccess.scheduleUpdate(worldIn, pos, (Block)this, 1);
    }

    private int processJob(World world, DiffusionJob job) {
        int removedThisTick = 0;
        while (!job.queue.isEmpty() && removedThisTick < 8192 && job.removed < 2000000) {
            BlockPos current = BlockPos.fromLong((long)((Long)job.queue.poll()));
            for (int x = -1; x <= 1; ++x) {
                for (int y = -1; y <= 1; ++y) {
                    for (int z = -1; z <= 1; ++z) {
                        IBlockState nextState;
                        if (x == 0 && y == 0 && z == 0) continue;
                        BlockPos next = current.add(x, y, z);
                        long nextLong = next.toLong();
                        if (job.visited.contains(nextLong)) continue;
                        job.visited.add(nextLong);
                        if (!this.isWithinRadius(job.origin, next) || !WorldAccess.isBlockLoaded(world, next) || !this.isRemovableSRPBlock(nextState = BlockStates.get(world, next))) continue;
                        BlockStates.set(world, next, BlockStates.defaultState(Blocks.air), 3);
                        job.queue.add(nextLong);
                        job.removed++;
                        if (++removedThisTick < 8192 && job.removed < 2000000) continue;
                        return removedThisTick;
                    }
                }
            }
        }
        job.finished = job.queue.isEmpty();
        return removedThisTick;
    }

    private boolean isWithinRadius(BlockPos origin, BlockPos pos) {
        return Math.abs(pos.getX() - origin.getX()) <= 256 && Math.abs(pos.getY() - origin.getY()) <= 256 && Math.abs(pos.getZ() - origin.getZ()) <= 256;
    }

    private boolean isRemovableSRPBlock(IBlockState state) {
        if (state.getBlock() == this) {
            return false;
        }
        ResourceLocation id = RegistryNames.get(state.getBlock());
        return id != null && "srparasites".equals(id.getResourceDomain());
    }

    private static String getJobKey(World world, BlockPos pos) {
        return world.provider.dimensionId + ":" + pos.toLong();
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format((String)"tooltip.srparasites.epitome_infestation_warp_diffuser.desc", (Object[])new Object[0]));
        tooltip.add(I18n.format((String)"tooltip.srparasites.epitome_infestation_warp_diffuser.desc2", (Object[])new Object[0]));
    }

    private static class DiffusionJob {
        private final BlockPos origin;
        private final Queue<Long> queue = new ArrayDeque<Long>();
        private final Set<Long> visited = new HashSet<Long>();
        private int removed;
        private boolean finished;

        private DiffusionJob(BlockPos origin) {
            this.origin = origin;
            this.queue.add(origin.toLong());
            this.visited.add(origin.toLong());
            this.removed = 0;
            this.finished = false;
        }
    }
}

