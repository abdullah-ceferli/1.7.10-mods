/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.network.MsgSyncProgressSnapshot;
import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import com.dhanantry.scapeandrunparasites.network.SRPProgressSnapshot;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MsgRequestProgressSnapshot
implements IMessage {
    public void fromBytes(ByteBuf buf) {
    }

    public void toBytes(ByteBuf buf) {
    }

    public static class Handler
    implements IMessageHandler<MsgRequestProgressSnapshot, IMessage> {
        public IMessage onMessage(MsgRequestProgressSnapshot message, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            player.getServerForPlayer().addScheduledTask(new Runnable(){

                @Override
                public void run() {
                    SRPProgressSnapshot snapshot = SRPProgressSnapshot.collect(player.worldObj, (EntityPlayer)player);
                    SRPNetwork.CHANNEL.sendTo((IMessage)new MsgSyncProgressSnapshot(snapshot), player);
                }
            });
            return null;
        }
    }
}

