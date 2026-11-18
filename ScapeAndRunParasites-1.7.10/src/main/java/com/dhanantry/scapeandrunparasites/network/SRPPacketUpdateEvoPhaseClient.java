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
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SRPPacketUpdateEvoPhaseClient
implements IMessage {
    private int phase;
    private boolean vector;

    public SRPPacketUpdateEvoPhaseClient() {
    }

    public SRPPacketUpdateEvoPhaseClient(int in, boolean v) {
        this.phase = in;
        this.vector = v;
    }

    public void fromBytes(ByteBuf buf) {
        this.phase = buf.readInt();
        this.vector = buf.readBoolean();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.phase);
        buf.writeBoolean(this.vector);
    }

    public static class Handler
    implements IMessageHandler<SRPPacketUpdateEvoPhaseClient, IMessage> {
        public IMessage onMessage(SRPPacketUpdateEvoPhaseClient message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> this.handle(message, ctx));
            return null;
        }

        private void handle(SRPPacketUpdateEvoPhaseClient message, MessageContext ctx) {
            int prev = SRPEventHandlerBus.clientVector;
            if (message.vector) {
                if (prev <= 0) {
                    Minecraft.getMinecraft().getSoundHandler().stop("", com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.MUSIC);
                }
                SRPEventHandlerBus.clientVector = 200;
            }
            SRPEventHandlerBus.clientCurrentEvoPhase = (byte)message.phase;
        }
    }
}

