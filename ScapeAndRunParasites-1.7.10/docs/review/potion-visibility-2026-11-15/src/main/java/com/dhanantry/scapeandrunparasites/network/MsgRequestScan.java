/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.backport.world.WorldAccess;

import com.dhanantry.scapeandrunparasites.network.MsgScanCooldown;
import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityRelayController;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MsgRequestScan
implements IMessage {
    private BlockPos pos;

    public MsgRequestScan() {
    }

    public MsgRequestScan(BlockPos pos) {
        this.pos = pos;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.pos.toLong());
    }

    public void fromBytes(ByteBuf buf) {
        this.pos = BlockPos.fromLong((long)buf.readLong());
    }

    public static class Handler
    implements IMessageHandler<MsgRequestScan, IMessage> {
        public IMessage onMessage(MsgRequestScan msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            WorldServer world = player.getServerForPlayer();
            world.addScheduledTask(() -> {
                boolean ok;
                TileEntity te = WorldAccess.getTileEntity(world, msg.pos);
                if (!(te instanceof TileEntityRelayController)) {
                    return;
                }
                TileEntityRelayController rc = (TileEntityRelayController)te;
                boolean started = false;
                if (rc.canScan() && (ok = rc.performScan(player))) {
                    rc.startCooldown();
                    started = true;
                }
                int remaining = rc.getCooldownRemainingTicks();
                int total = rc.getCooldownTotalTicks();
                SRPNetwork.CHANNEL.sendTo((IMessage)new MsgScanCooldown(msg.pos, total, remaining, started), player);
            });
            return null;
        }
    }
}

