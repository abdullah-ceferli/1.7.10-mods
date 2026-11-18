/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.SoundEvents
 *  net.minecraftforge.fml.common.network.ByteBufUtils
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.item.tool.IHaveReach;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SRPPacketMeleeRange
implements IMessage {
    private int entityId;

    public SRPPacketMeleeRange() {
    }

    public SRPPacketMeleeRange(int parEntityId) {
        this.entityId = parEntityId;
    }

    public void fromBytes(ByteBuf buf) {
        this.entityId = ByteBufUtils.readVarInt((ByteBuf)buf, (int)4);
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeVarInt((ByteBuf)buf, (int)this.entityId, (int)4);
    }

    public static class Handler
    implements IMessageHandler<SRPPacketMeleeRange, IMessage> {
        public IMessage onMessage(final SRPPacketMeleeRange message, MessageContext ctx) {
            final EntityPlayerMP thePlayer = SRPMain.proxy.getPlayerEntityFromContext(ctx);
            thePlayer.getServer().addScheduledTask(new Runnable(){

                @Override
                public void run() {
                    Entity theEntity = thePlayer.worldObj.getEntityByID(message.entityId);
                    if (thePlayer.getActiveItemStack() == null || theEntity == null) {
                        return;
                    }
                    if (thePlayer.getHeldItemMainhand().getItem() instanceof IHaveReach) {
                        IHaveReach theExtendedReachWeapon = (IHaveReach)thePlayer.getHeldItemMainhand().getItem();
                        double distanceSq = thePlayer.getDistanceSqToEntity(theEntity);
                        double reachSq = theExtendedReachWeapon.getReach() * theExtendedReachWeapon.getReach();
                        if (reachSq >= distanceSq) {
                            thePlayer.attackTargetEntityWithCurrentItem(theEntity);
                            thePlayer.worldObj.playSound((EntityPlayer)null, thePlayer.posX, thePlayer.posY, thePlayer.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, thePlayer.getSoundCategory(), 1.0f, 1.0f);
                            thePlayer.spawnSweepParticles();
                        }
                    }
                }
            });
            return null;
        }
    }
}

