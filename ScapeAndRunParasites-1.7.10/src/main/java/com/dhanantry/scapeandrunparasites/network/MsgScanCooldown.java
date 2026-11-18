/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.gui.ScannerGui;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MsgScanCooldown
implements IMessage {
    private BlockPos pos;
    private int totalTicks;
    private int remainingTicks;
    private boolean started;

    public MsgScanCooldown() {
    }

    public MsgScanCooldown(BlockPos pos, int totalTicks, int remainingTicks, boolean started) {
        this.pos = pos;
        this.totalTicks = totalTicks;
        this.remainingTicks = remainingTicks;
        this.started = started;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.pos.toLong());
        buf.writeInt(this.totalTicks);
        buf.writeInt(this.remainingTicks);
        buf.writeBoolean(this.started);
    }

    public void fromBytes(ByteBuf buf) {
        this.pos = BlockPos.fromLong((long)buf.readLong());
        this.totalTicks = buf.readInt();
        this.remainingTicks = buf.readInt();
        this.started = buf.readBoolean();
    }

    public static class Handler
    implements IMessageHandler<MsgScanCooldown, IMessage> {
        public IMessage onMessage(MsgScanCooldown msg, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (Minecraft.getMinecraft().currentScreen instanceof ScannerGui) {
                    ((ScannerGui)Minecraft.getMinecraft().currentScreen).onCooldownUpdate(msg.totalTicks, msg.remainingTicks, msg.started);
                }
            });
            return null;
        }
    }
}

