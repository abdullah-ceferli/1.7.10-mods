/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.advancements.AdvancementProgress
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public final class GuerillaAdvancement {
    private static final ResourceLocation ADV_ID = new ResourceLocation("srparasites", "guerilla");
    private static final String CRITERION = "kill_fear_splashed";
    private static final String TAG_APPLIER = "srpFearApplier";
    private static final String TAG_UNTIL = "srpFearUntil";

    private GuerillaAdvancement() {
    }

    public static void register(FMLInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(GuerillaAdvancement.class);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayerMP)) {
            return;
        }
        if (event.getEntityLiving().worldObj.isRemote) {
            return;
        }
        Entity trueSrc = event.getSource().getEntity();
        if (!(trueSrc instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP victim = (EntityPlayerMP)event.getEntityLiving();
        EntityPlayerMP killer = (EntityPlayerMP)trueSrc;
        NBTTagCompound tag = victim.getEntityData();
        if (!tag.hasKey(TAG_APPLIER)) {
            return;
        }
        if (!killer.getUniqueID().toString().equals(tag.getString(TAG_APPLIER))) {
            return;
        }
        long until = tag.getLong(TAG_UNTIL);
        if (victim.worldObj.getTotalWorldTime() > until) {
            return;
        }
        if (!victim.isPotionActive(SRPPotions.FEAR_E)) {
            return;
        }
        Advancement adv = killer.getServer().getAdvancementManager().getAdvancement(ADV_ID);
        if (adv == null) {
            return;
        }
        AdvancementProgress prog = killer.getAdvancements().getProgress(adv);
        if (!prog.isDone()) {
            prog.grantCriterion(CRITERION);
        }
        tag.removeTag(TAG_APPLIER);
        tag.removeTag(TAG_UNTIL);
    }
}

