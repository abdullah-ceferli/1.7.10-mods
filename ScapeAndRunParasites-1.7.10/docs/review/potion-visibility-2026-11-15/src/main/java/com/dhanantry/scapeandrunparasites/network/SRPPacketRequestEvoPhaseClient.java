/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.network.SRPPacketUpdateEvoPhaseClient;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SRPPacketRequestEvoPhaseClient
implements IMessage {
    public void fromBytes(ByteBuf buf) {
    }

    public void toBytes(ByteBuf buf) {
    }

    public static class Handler
    implements IMessageHandler<SRPPacketRequestEvoPhaseClient, IMessage> {
        public IMessage onMessage(SRPPacketRequestEvoPhaseClient message, MessageContext ctx) {
            final EntityPlayerMP thePlayer = SRPMain.proxy.getPlayerEntityFromContext(ctx);
            thePlayer.getServer().addScheduledTask(new Runnable(){

                @Override
                public void run() {
                    SRPMain.network.sendTo((IMessage)new SRPPacketUpdateEvoPhaseClient(SRPSaveData.get(thePlayer.worldObj, 110).getEvolutionPhase(thePlayer.worldObj.provider.dimensionId), SRPWorldData.get(thePlayer.worldObj).nearestInfectionPosition(false, EntityQueries.position(thePlayer)) != null), thePlayer);
                }
            });
            return null;
        }
    }
}

