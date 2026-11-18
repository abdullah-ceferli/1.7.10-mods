/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.network.simpleimpl.IMessage
 */
package com.dhanantry.scapeandrunparasites.feature;

import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import com.dhanantry.scapeandrunparasites.network.msg.S2CSetEscapeOffer;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class EscapeOnDeathHandler {
    private static final String PERSIST_TAG = "PlayerPersisted";
    private static final String OFFER_TAG = "srp_offer_escape";
    private static final String WINDOW_TAG = "srp_death_window";
    private static final long WINDOW_MS = 60000L;
    private static final int THRESHOLD = 5;
    private static final Map<UUID, Deque<Long>> streaks = new HashMap<UUID, Deque<Long>>();

    private static boolean isSrparasitesCause(LivingDeathEvent e) {
        if (e.getSource() == null) {
            return false;
        }
        if (e.getSource().getEntity() != null && e.getSource().getEntity().getClass().getName().startsWith("com.dhanantry.scapeandrunparasites")) {
            return true;
        }
        if (e.getSource().getSourceOfDamage() != null && e.getSource().getSourceOfDamage().getClass().getName().startsWith("com.dhanantry.scapeandrunparasites")) {
            return true;
        }
        String dmg = e.getSource().damageType == null ? "" : e.getSource().damageType;
        return dmg.contains("srparasites");
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent e) {
        if (!(e.getEntity() instanceof EntityPlayerMP)) {
            return;
        }
        if (!SRPConfigWorld.escapeEnabled) {
            return;
        }
        if (!EscapeOnDeathHandler.isSrparasitesCause(e)) {
            return;
        }
        EntityPlayerMP p = (EntityPlayerMP)e.getEntity();
        long nowMs = p.worldObj.getTotalWorldTime() * 50L;
        Deque q = streaks.computeIfAbsent(p.getUniqueID(), k -> new ArrayDeque());
        q.addLast(nowMs);
        while (!q.isEmpty() && nowMs - (Long)q.peekFirst() > 60000L) {
            q.removeFirst();
        }
        boolean offer = q.size() > 5;
        NBTTagCompound persisted = p.getEntityData().getCompoundTag(PERSIST_TAG);
        persisted.setBoolean(OFFER_TAG, offer);
        persisted.setLong(WINDOW_TAG, nowMs);
        p.getEntityData().setTag(PERSIST_TAG, (NBTBase)persisted);
        SRPNetwork.CHANNEL.sendTo((IMessage)new S2CSetEscapeOffer(offer), p);
    }

    public static void clearOffer(EntityPlayerMP p) {
        NBTTagCompound persisted = p.getEntityData().getCompoundTag(PERSIST_TAG);
        persisted.setBoolean(OFFER_TAG, false);
        p.getEntityData().setTag(PERSIST_TAG, (NBTBase)persisted);
        SRPNetwork.CHANNEL.sendTo((IMessage)new S2CSetEscapeOffer(false), p);
    }
}

