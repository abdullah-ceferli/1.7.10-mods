/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.SoundCategory
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.util.handlers.SRPEventHandlerBus;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SRPPacketMusicTrackCancelUpdateClientEvoPhase
implements IMessage {
    private int phase;

    public SRPPacketMusicTrackCancelUpdateClientEvoPhase() {
    }

    public SRPPacketMusicTrackCancelUpdateClientEvoPhase(int in) {
        this.phase = in;
    }

    public void fromBytes(ByteBuf buf) {
        this.phase = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.phase);
    }

    public static class Handler
    implements IMessageHandler<SRPPacketMusicTrackCancelUpdateClientEvoPhase, IMessage> {
        public IMessage onMessage(SRPPacketMusicTrackCancelUpdateClientEvoPhase message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> this.handle(message, ctx));
            return null;
        }

        private void handle(SRPPacketMusicTrackCancelUpdateClientEvoPhase message, MessageContext ctx) {
            Minecraft.getMinecraft().getSoundHandler().stop("", SoundCategory.MUSIC);
            SRPEventHandlerBus.resetSouncTicker(200);
            SRPEventHandlerBus.clientCurrentEvoPhase = (byte)message.phase;
        }
    }
}

