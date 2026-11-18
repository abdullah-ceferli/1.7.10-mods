/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityArmorStand
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingUpdateEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public final class RageSoundHandler {
    private static final String RAGE_SOUND_TIMER = "SRPRageSoundTimer";
    private static final int RAGE_SOUND_MIN_INTERVAL = 60;
    private static final int RAGE_SOUND_MAX_INTERVAL = 120;
    private static final float RAGE_SOUND_VOLUME = 0.3f;
    private static final float RAGE_SOUND_PITCH = 1.0f;

    private RageSoundHandler() {
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity == null || entity.worldObj == null || entity.worldObj.isRemote) {
            return;
        }
        if (entity.isDead || entity.getHealth() <= 0.0f) {
            return;
        }
        if (RageSoundHandler.isBlacklisted(entity)) {
            return;
        }
        PotionEffect rage = entity.getActivePotionEffect(SRPPotions.RAGE_E);
        if (rage == null) {
            entity.getEntityData().removeTag(RAGE_SOUND_TIMER);
            return;
        }
        NBTTagCompound data = entity.getEntityData();
        int timer = data.getInteger(RAGE_SOUND_TIMER);
        if (timer > 0) {
            data.setInteger(RAGE_SOUND_TIMER, timer - 1);
            return;
        }
        entity.worldObj.playSound(null, entity.posX, entity.posY, entity.posZ, SRPSounds.RAGE, SoundCategory.HOSTILE, 0.3f, 1.0f + (entity.getRNG().nextFloat() - entity.getRNG().nextFloat()) * 0.1f);
        data.setInteger(RAGE_SOUND_TIMER, RageSoundHandler.getRandomInterval(entity));
    }

    private static int getRandomInterval(EntityLivingBase entity) {
        int min = Math.max(1, 60);
        int max = Math.max(min, 120);
        return min + entity.getRNG().nextInt(max - min + 1);
    }

    private static boolean isBlacklisted(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            return true;
        }
        if (entity instanceof EntityArmorStand) {
            return true;
        }
        ResourceLocation id = EntityList.getKey((Entity)entity);
        if (id == null) {
            return true;
        }
        String name = id.toString();
        return name.equals("minecraft:player") || name.equals("minecraft:armor_stand") || name.equals("srparasites:buglin") || name.equals("srparasites:biomass") || name.equals("srparasites:beckon_si") || name.equals("srparasites:beckon_sii") || name.equals("srparasites:beckon_siii") || name.equals("srparasites:beckon_siv") || name.equals("srparasites:rooter_si") || name.equals("srparasites:rooter_sii") || name.equals("srparasites:rooter_siii") || name.equals("srparasites:rooter_siv") || name.equals("srparasites:dispatcher_si") || name.equals("srparasites:dispatcher_sii") || name.equals("srparasites:dispatcher_siii") || name.equals("srparasites:dispatcher_siv") || name.equals("srparasites:movingflesh") || name.equals("srparasites:worker") || name.equals("srparasites:dispatcherten") || name.equals("srparasites:rooterball") || name.equals("srparasites:incompleteform_small") || name.equals("srparasites:incompleteform_medium") || name.equals("srparasites:crux_incomplete") || name.equals("srparasites:carrier_worm") || name.equals("srparasites:succor") || name.equals("srparasites:anc_pod");
    }
}

