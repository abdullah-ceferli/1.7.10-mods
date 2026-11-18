/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.advancements;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class HuntSeasonAdvancement {
    private static final ResourceLocation ADV_ID = new ResourceLocation("srparasites", "hunt_season");
    private static final String CRITERION = "fifty_in_day";
    private static final int TARGET = 50;
    private static final long WINDOW_TICKS = 24000L;

    public static void register() {
        MinecraftForge.EVENT_BUS.register((Object)new HuntSeasonAdvancement());
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent e) {
        Advancement adv;
        if (e.getEntityLiving().worldObj.isRemote) {
            return;
        }
        Entity src = e.getSource().getEntity();
        if (!(src instanceof EntityPlayerMP)) {
            return;
        }
        EntityLivingBase victim = e.getEntityLiving();
        ResourceLocation id = EntityList.getKey((Entity)victim);
        if (id == null) {
            return;
        }
        if (!"srparasites".equals(id.getResourceDomain())) {
            return;
        }
        EntityPlayerMP killer = (EntityPlayerMP)src;
        long now = victim.worldObj.getTotalWorldTime();
        NBTTagCompound entityData = killer.getEntityData();
        NBTTagCompound persisted = entityData.getCompoundTag("PlayerPersisted");
        long start = persisted.getLong("srpHuntStart");
        int count = persisted.getInteger("srpHuntCount");
        if (start == 0L || now - start > 24000L) {
            start = now;
            count = 1;
        } else {
            ++count;
        }
        persisted.setLong("srpHuntStart", start);
        persisted.setInteger("srpHuntCount", count);
        entityData.setTag("PlayerPersisted", (NBTBase)persisted);
        if (count >= 50 && (adv = killer.getServer().getAdvancementManager().getAdvancement(ADV_ID)) != null) {
            killer.getAdvancements().grantCriterion(adv, CRITERION);
        }
    }
}

