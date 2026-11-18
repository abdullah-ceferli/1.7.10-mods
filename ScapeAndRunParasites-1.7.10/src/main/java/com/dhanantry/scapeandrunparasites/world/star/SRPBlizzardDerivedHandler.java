/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.PlayerEvent$PlayerLoggedOutEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.common.gameevent.TickEvent$PlayerTickEvent
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.backport.entity.EntityQueries;

import com.dhanantry.scapeandrunparasites.backport.math.Boxes;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDerived;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityHeblu;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityKirin;
import com.dhanantry.scapeandrunparasites.network.MsgSyncBlizzardReverse;
import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import com.dhanantry.scapeandrunparasites.world.star.SRPStarWorldData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

@Mod.EventBusSubscriber(modid="srparasites")
public final class SRPBlizzardDerivedHandler {
    private static final double RANGE = 100.0;
    private static final double RANGE_SQ = 10000.0;
    private static final Map<UUID, Boolean> LAST_STATE = new HashMap<UUID, Boolean>();

    private SRPBlizzardDerivedHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!(event.player instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP)event.player;
        if (player.worldObj.isRemote) {
            return;
        }
        if (player.ticksExisted % 10 != 0) {
            return;
        }
        boolean reverse = SRPBlizzardDerivedHandler.shouldReverse(player);
        UUID id = player.getUniqueID();
        Boolean previous = LAST_STATE.get(id);
        if (previous == null || previous != reverse) {
            LAST_STATE.put(id, reverse);
            SRPNetwork.CHANNEL.sendTo((IMessage)new MsgSyncBlizzardReverse(reverse), player);
        }
    }

    private static boolean shouldReverse(EntityPlayerMP player) {
        World world = player.worldObj;
        if (world.provider == null || world.provider.dimensionId != 0) {
            return false;
        }
        if (SRPStarWorldData.get(world).getStarType() != 1) {
            return false;
        }
        AxisAlignedBB area = Boxes.grow(Boxes.entityBox(player), 100.0);
        for (EntityPDerived entity : EntityQueries.entities(world, EntityHeblu.class, area)) {
            if (entity.isDead || !(player.getDistanceSqToEntity((Entity)entity) <= 10000.0)) continue;
            return true;
        }
        for (EntityPDerived entity : EntityQueries.entities(world, EntityKirin.class, area)) {
            if (((EntityKirin)entity).isDead || !(player.getDistanceSqToEntity((Entity)entity) <= 10000.0)) continue;
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        LAST_STATE.remove(event.player.getUniqueID());
    }
}

