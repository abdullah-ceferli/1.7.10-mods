/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.SRPMain;
import io.netty.buffer.ByteBuf;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SRPPacketBiomeChange
implements IMessage {
    private BlockPos blockPos;
    private boolean convertToParasite;
    private int type;

    public SRPPacketBiomeChange() {
    }

    public SRPPacketBiomeChange(BlockPos pos, boolean convert, int typeP) {
        this.blockPos = pos;
        this.convertToParasite = convert;
        this.type = typeP;
    }

    public void fromBytes(ByteBuf ByteBuf2) {
        this.blockPos = new BlockPos(ByteBuf2.readInt(), ByteBuf2.readInt(), ByteBuf2.readInt());
        this.convertToParasite = ByteBuf2.readBoolean();
        this.type = ByteBuf2.readInt();
    }

    public void toBytes(ByteBuf ByteBuf2) {
        ByteBuf2.writeInt(this.blockPos.getX());
        ByteBuf2.writeInt(this.blockPos.getY());
        ByteBuf2.writeInt(this.blockPos.getZ());
        ByteBuf2.writeBoolean(this.convertToParasite);
        ByteBuf2.writeInt(this.type);
    }

    public static class Handler
    implements IMessageHandler<SRPPacketBiomeChange, IMessage> {
        public IMessage onMessage(SRPPacketBiomeChange message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> this.handle(message, ctx));
            return null;
        }

        private void handle(SRPPacketBiomeChange message, MessageContext ctx) {
            SRPMain.proxy.spreadBiome(message.blockPos, message.convertToParasite, message.type);
        }
    }
}

