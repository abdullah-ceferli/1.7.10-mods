package com.dhanantry.scapeandrunparasites.backport.entity;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

/** Mirror equipment for attribute queries; native players retain Forge's ISpecialArmor damage path. */
public final class ArmorAttributeEvents {
    private static boolean registered;
    public static synchronized void register() {
        if(registered)return;
        registered=true;
        MinecraftForge.EVENT_BUS.register(new ArmorAttributeEvents());
    }
    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void livingUpdate(LivingEvent.LivingUpdateEvent event) {
        ModernAttributes.syncEquipment(event.entityLiving);
    }
}
