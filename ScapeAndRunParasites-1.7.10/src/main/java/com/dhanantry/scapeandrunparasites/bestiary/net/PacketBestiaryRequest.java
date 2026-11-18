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
package com.dhanantry.scapeandrunparasites.bestiary.net;

import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.bestiary.net.BestiaryNetwork;
import com.dhanantry.scapeandrunparasites.bestiary.net.PacketBestiarySync;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketBestiaryRequest
implements IMessage {
    private static final boolean DEBUG = false;

    public void toBytes(ByteBuf buf) {
    }

    public void fromBytes(ByteBuf buf) {
    }

    public static class Handler
    implements IMessageHandler<PacketBestiaryRequest, IMessage> {
        public IMessage onMessage(PacketBestiaryRequest msg, MessageContext ctx) {
            EntityPlayerMP mp = ctx.getServerHandler().playerEntity;
            mp.getServerForPlayer().addScheduledTask(() -> {
                IBestiaryProgress prog = (IBestiaryProgress)mp.getCapability(BestiaryCapability.CAP, null);
                if (prog == null) {
                    System.out.println("[SRP][BESTIARY] Request: player has NO capability");
                    return;
                }
                BestiaryNetwork.CH.sendTo((IMessage)new PacketBestiarySync(prog), mp);
            });
            return null;
        }
    }
}

