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

public final class RupterKillMilestoneAdvancement {
    private static final ResourceLocation ADV_ID = new ResourceLocation("srparasites", "cut_roots");
    private static final String CRITERION = "reached_1000_rupter_kills";
    private static final int TARGET = 1000;

    public static void register() {
        MinecraftForge.EVENT_BUS.register((Object)new RupterKillMilestoneAdvancement());
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
        if (!"rupter".equals(id.getResourcePath())) {
            return;
        }
        EntityPlayerMP killer = (EntityPlayerMP)src;
        NBTTagCompound entityData = killer.getEntityData();
        NBTTagCompound persisted = entityData.getCompoundTag("PlayerPersisted");
        int count = persisted.getInteger("srpRupterKills");
        persisted.setInteger("srpRupterKills", ++count);
        entityData.setTag("PlayerPersisted", (NBTBase)persisted);
        if (count >= 1000 && (adv = killer.getServer().getAdvancementManager().getAdvancement(ADV_ID)) != null) {
            killer.getAdvancements().grantCriterion(adv, CRITERION);
        }
    }
}

