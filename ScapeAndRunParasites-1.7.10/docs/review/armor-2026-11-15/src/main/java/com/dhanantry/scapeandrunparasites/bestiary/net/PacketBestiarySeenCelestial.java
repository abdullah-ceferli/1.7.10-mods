/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.advancements.AdvancementProgress
 *  net.minecraft.advancements.PlayerAdvancements
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.common.network.ByteBufUtils
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
 *  net.minecraftforge.fml.common.network.simpleimpl.MessageContext
 */
package com.dhanantry.scapeandrunparasites.bestiary.net;

import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.bestiary.net.BestiaryNetwork;
import com.dhanantry.scapeandrunparasites.bestiary.net.PacketBestiarySync;
import com.dhanantry.scapeandrunparasites.client.celestial.CelestialObjectRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketBestiarySeenCelestial
implements IMessage {
    private String id;

    public PacketBestiarySeenCelestial() {
    }

    public PacketBestiarySeenCelestial(String id) {
        this.id = id;
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String((ByteBuf)buf, (String)(this.id == null ? "" : this.id));
    }

    public void fromBytes(ByteBuf buf) {
        this.id = ByteBufUtils.readUTF8String((ByteBuf)buf);
        if (this.id != null && this.id.isEmpty()) {
            this.id = null;
        }
    }

    private static void grantAdvancement(EntityPlayerMP mp, String advancementPath) {
        if (mp == null || mp.mcServer == null) {
            return;
        }
        Advancement adv = mp.mcServer.getAdvancementManager().getAdvancement(new ResourceLocation("srparasites", advancementPath));
        if (adv == null) {
            return;
        }
        PlayerAdvancements pa = mp.getAdvancements();
        AdvancementProgress progress = pa.getProgress(adv);
        for (String criterion : progress.getRemaningCriteria()) {
            pa.grantCriterion(adv, criterion);
        }
    }

    private static void checkCelestialDiscoveryAdvancements(EntityPlayerMP mp, IBestiaryProgress prog) {
        int halfThreshold;
        if (mp == null || prog == null) {
            return;
        }
        int total = CelestialObjectRegistry.getObjectCount();
        if (total <= 0) {
            return;
        }
        int discovered = prog.getSeenCelestials().size();
        if (discovered >= (halfThreshold = CelestialObjectRegistry.getHalfDiscoveryThreshold())) {
            PacketBestiarySeenCelestial.grantAdvancement(mp, "columbus");
        }
        if (discovered >= total) {
            PacketBestiarySeenCelestial.grantAdvancement(mp, "stolas");
        }
    }

    public static class Handler
    implements IMessageHandler<PacketBestiarySeenCelestial, IMessage> {
        public IMessage onMessage(PacketBestiarySeenCelestial msg, MessageContext ctx) {
            EntityPlayerMP mp = ctx.getServerHandler().playerEntity;
            if (mp == null || msg == null || msg.id == null) {
                return null;
            }
            mp.getServerForPlayer().addScheduledTask(() -> {
                IBestiaryProgress prog = (IBestiaryProgress)mp.getCapability(BestiaryCapability.CAP, null);
                if (prog == null) {
                    return;
                }
                if (!prog.hasSeenCelestial(msg.id)) {
                    prog.markCelestialSeen(msg.id);
                }
                PacketBestiarySeenCelestial.checkCelestialDiscoveryAdvancements(mp, prog);
                BestiaryNetwork.CH.sendTo((IMessage)new PacketBestiarySync(prog), mp);
            });
            return null;
        }
    }
}

