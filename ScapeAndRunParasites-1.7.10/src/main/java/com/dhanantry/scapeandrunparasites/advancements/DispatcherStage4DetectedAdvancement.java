/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.server.SPacketSoundEffect
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.EntityJoinWorldEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package com.dhanantry.scapeandrunparasites.advancements;

import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.MsgQlipShake;
import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.Packet;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public final class DispatcherStage4DetectedAdvancement {
    private static final ResourceLocation ADV_ID = new ResourceLocation("srparasites", "qliphoth_overload");
    private static final String CRITERION = "detected_dispatcher_siv";
    private static final double RANGE_SQ = 65536.0;

    public static void register() {
        MinecraftForge.EVENT_BUS.register((Object)new DispatcherStage4DetectedAdvancement());
        System.out.println("[SRP][Qlip] listener registered");
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent e) {
        if (e.getWorld().isRemote) {
            return;
        }
        Entity ent = e.getEntity();
        ResourceLocation id = EntityList.getKey((Entity)ent);
        if (id == null) {
            return;
        }
        if (!"srparasites".equals(id.getResourceDomain())) {
            return;
        }
        if (!"dispatcher_siv".equals(id.getResourcePath())) {
            return;
        }
        WorldServer ws = (WorldServer)e.getWorld();
        for (EntityPlayerMP p : ws.getMinecraftServer().getPlayerList().getPlayers()) {
            boolean granted;
            if (p.worldObj.provider.dimensionId != ws.provider.dimensionId || p.getDistanceSqToEntity(ent) > 65536.0) continue;
            Advancement adv = p.getServer().getAdvancementManager().getAdvancement(ADV_ID);
            boolean bl = granted = adv != null && p.getAdvancements().grantCriterion(adv, CRITERION);
            if (!granted) {
                System.out.println("[SRP][Qlip] grant failed or already done for " + p.getCommandSenderName());
                continue;
            }
            String se = SRPSounds.DISLO_28;
            if (se != null) {
                p.playerNetServerHandler.sendPacket((Packet)com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.packet(se, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.MASTER, p.posX, p.posY, p.posZ, 1.0f, 1.0f));
                com.dhanantry.scapeandrunparasites.backport.sound.SoundAccess.play(p.worldObj, null, p.posX, p.posY, p.posZ, se, com.dhanantry.scapeandrunparasites.backport.sound.ModernSoundCategory.MASTER, 1.0f, 1.0f);
                System.out.println("[SRP][Qlip] played sound " + (se.getRegistryName() == null ? "<no id>" : se.getRegistryName().toString()) + " for " + p.getCommandSenderName());
            } else {
                System.out.println("[SRP][Qlip] DISLO_28 is null (not registered)");
            }
            SRPNetwork.CHANNEL.sendTo((IMessage)new MsgQlipShake(0, 20, true, true, 4.0f), p);
            System.out.println("[SRP][Qlip] sent shake packet to " + p.getCommandSenderName());
        }
    }
}

