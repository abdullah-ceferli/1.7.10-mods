/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ITickable
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.fml.common.network.NetworkRegistry$TargetPoint
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockInfestationPurifier;
import com.dhanantry.scapeandrunparasites.block.PurifyMappings;
import com.dhanantry.scapeandrunparasites.network.MsgSpawnPureParticles;
import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.UUID;
import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class TileEntityInfestationPurifier
extends TileEntity
implements ITickable {
    private static final int MAX_TOTAL = 131072;
    private static final int NODES_PER_TICK = 32;
    private static final int TICK_INTERVAL = 1;
    private static final int SPREAD_RADIUS = 6;
    private static final ResourceLocation INFEST_REMAIN_RL = new ResourceLocation("srparasites", "infestremain");
    private static Block CACHED_INFEST_REMAIN = null;
    private final ArrayDeque<BlockPos> queue = new ArrayDeque();
    private final HashSet<Long> visited = new HashSet();
    private final HashSet<Long> inQueue = new HashSet();
    private boolean running = false;
    private int total = 0;
    private int tickGate = 0;
    private UUID starter;

    public void startAt(BlockPos start, UUID starterUuid) {
        this.queue.clear();
        this.visited.clear();
        this.inQueue.clear();
        this.running = true;
        this.total = 0;
        this.tickGate = 0;
        this.starter = starterUuid;
        this.offer(start);
        this.markDirty();
    }

    private void offer(BlockPos p) {
        long k = p.toLong();
        if (this.visited.contains(k) || this.inQueue.contains(k)) {
            return;
        }
        this.queue.add(p);
        this.inQueue.add(k);
    }

    public void update() {
        BlockPos cur;
        if (this.worldObj == null || this.worldObj.isRemote || !this.running) {
            return;
        }
        if (this.tickGate++ % 1 != 0) {
            return;
        }
        if (CACHED_INFEST_REMAIN == null) {
            CACHED_INFEST_REMAIN = (Block)Block.blockRegistry.getObject((Object)INFEST_REMAIN_RL);
        }
        int processed = 0;
        while (processed < 32 && !this.queue.isEmpty() && this.total < 131072 && (cur = this.queue.poll()) != null) {
            IBlockState finalState;
            IBlockState st;
            long curKey = cur.toLong();
            this.inQueue.remove(curKey);
            if (!this.visited.add(curKey) || !PurifyMappings.isSrp(st = BlockStates.get(this.worldObj, cur))) continue;
            IBlockState vanilla = PurifyMappings.mapToVanillaState(st);
            if (vanilla != null && BlockStates.set(this.worldObj, cur, finalState = BlockInfestationPurifier.tryCopyCommonProps(st, vanilla), 3)) {
                ++this.total;
                BlockPos up = cur.up();
                if (WorldAccess.isAirBlock(this.worldObj, up) && CACHED_INFEST_REMAIN != null) {
                    BlockStates.set(this.worldObj, up, BlockStates.defaultState(CACHED_INFEST_REMAIN), 3);
                }
                if (this.worldObj instanceof WorldServer && this.worldObj.isDaytime() && WorldAccess.canSeeSky(this.worldObj, cur.up())) {
                    double x = (double)cur.getX() + 0.5;
                    double y = (double)cur.getY() + 1.0;
                    double z = (double)cur.getZ() + 0.5;
                    SRPNetwork.CHANNEL.sendToAllAround((IMessage)new MsgSpawnPureParticles(x, y, z, 8 + this.worldObj.rand.nextInt(4), 0), new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId, x, y, z, 64.0));
                }
            }
            BlockPos minN = cur.add(-1, -1, -1);
            BlockPos maxN = cur.add(1, 1, 1);
            for (BlockPos p : BlockPos.getAllInBox((BlockPos)minN, (BlockPos)maxN)) {
                if (p.equals((Object)cur) || !PurifyMappings.isSrp(BlockStates.get(this.worldObj, p))) continue;
                this.offer(p);
            }
            BlockPos min = cur.add(-6, -6, -6);
            BlockPos max = cur.add(6, 6, 6);
            for (BlockPos p : BlockPos.getAllInBox((BlockPos)min, (BlockPos)max)) {
                if (p.equals((Object)cur) || !PurifyMappings.isSrp(BlockStates.get(this.worldObj, p))) continue;
                this.offer(p);
            }
            ++processed;
        }
        if (this.queue.isEmpty() || this.total >= 131072) {
            this.running = false;
            this.notifyFinished();
        }
    }

    private void notifyFinished() {
        EntityPlayer p;
        if (!(this.worldObj instanceof WorldServer)) {
            return;
        }
        if (this.starter != null && (p = this.worldObj.getPlayerEntityByUUID(this.starter)) != null) {
            p.addChatComponentMessage((IChatComponent)new ChatComponentTranslation("message.srparasites.purifier_purified", new Object[]{this.total}), true);
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("running", this.running);
        nbt.setInteger("total", this.total);
        if (this.starter != null) {
            nbt.setUniqueId("starter", this.starter);
        }
        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.running = nbt.getBoolean("running");
        this.total = nbt.getInteger("total");
        this.starter = nbt.hasUniqueId("starter") ? nbt.getUniqueId("starter") : null;
    }
}

