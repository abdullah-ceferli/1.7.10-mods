/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.Minecraft
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.client.weather.SRPBlizzardDirectionClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MsgSyncBlizzardReverse
implements IMessage {
    private boolean reversed;

    public MsgSyncBlizzardReverse() {
    }

    public MsgSyncBlizzardReverse(boolean reversed) {
        this.reversed = reversed;
    }

    public void fromBytes(ByteBuf buf) {
        this.reversed = buf.readBoolean();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.reversed);
    }

    public static class Handler
    implements IMessageHandler<MsgSyncBlizzardReverse, IMessage> {
        public IMessage onMessage(final MsgSyncBlizzardReverse message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(new Runnable(){

                @Override
                public void run() {
                    SRPBlizzardDirectionClient.setReverseRequested(message.reversed);
                }
            });
            return null;
        }
    }
}

