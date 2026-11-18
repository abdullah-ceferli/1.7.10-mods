/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.network.MsgSyncProgressSnapshot;
import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import com.dhanantry.scapeandrunparasites.network.SRPProgressSnapshot;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MsgReconfigureProgressUnlock
implements IMessage {
    private boolean unlockPhase;
    private boolean unlockUD;

    public MsgReconfigureProgressUnlock() {
    }

    public MsgReconfigureProgressUnlock(boolean unlockPhase, boolean unlockUD) {
        this.unlockPhase = unlockPhase;
        this.unlockUD = unlockUD;
    }

    public void fromBytes(ByteBuf buf) {
        this.unlockPhase = buf.readBoolean();
        this.unlockUD = buf.readBoolean();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.unlockPhase);
        buf.writeBoolean(this.unlockUD);
    }

    public static class Handler
    implements IMessageHandler<MsgReconfigureProgressUnlock, IMessage> {
        public IMessage onMessage(final MsgReconfigureProgressUnlock message, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            player.getServerForPlayer().addScheduledTask(new Runnable(){

                @Override
                public void run() {
                    IBestiaryProgress prog = (IBestiaryProgress)player.getCapability(BestiaryCapability.CAP, null);
                    if (prog == null) {
                        return;
                    }
                    boolean changed = false;
                    if (message.unlockPhase && !prog.hasUnlockedProgressPhase() && Handler.consumeOne(player, "srparasites:evclock")) {
                        prog.setUnlockedProgressPhase(true);
                        changed = true;
                    }
                    if (message.unlockUD && !prog.hasUnlockedProgressUD() && Handler.consumeOne(player, "srparasites:levelclock")) {
                        prog.setUnlockedProgressUD(true);
                        changed = true;
                    }
                    SRPProgressSnapshot snapshot = SRPProgressSnapshot.collect(player.worldObj, (EntityPlayer)player);
                    SRPNetwork.CHANNEL.sendTo((IMessage)new MsgSyncProgressSnapshot(snapshot), player);
                }
            });
            return null;
        }

        private static boolean consumeOne(EntityPlayerMP player, String itemId) {
            Item wanted = Item.setTextureName((String)itemId);
            if (wanted == null) {
                return false;
            }
            for (int i = 0; i < player.inventory.mainInventory.size(); ++i) {
                ItemStack stack = (ItemStack)player.inventory.mainInventory.get(i);
                if (stack.isEmpty() || stack.getItem() != wanted) continue;
                stack.shrink(1);
                if (stack.getCount() <= 0) {
                    player.inventory.mainInventory.set(i, (Object)ItemStack.EMPTY);
                }
                player.inventory.markDirty();
                player.inventoryContainer.detectAndSendChanges();
                return true;
            }
            return false;
        }
    }
}

