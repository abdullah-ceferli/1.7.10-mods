package com.dhanantry.scapeandrunparasites.backport.potion;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;

public final class PotionFxEvents {
    private static boolean registered;
    /** Call once from preInit/init. FML tick events and Forge entity events use different buses. */
    public static synchronized void register() {
        if (registered) return;
        registered = true;
        PotionFxEvents handler = new PotionFxEvents();
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance().bus().register(handler);
    }

    @SubscribeEvent
    public void onConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityLivingBase && event.entity.getExtendedProperties(PotionVisibility.KEY) == null) {
            event.entity.registerExtendedProperties(PotionVisibility.KEY, new PotionVisibility());
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.END) PotionVisibility.tick(event.world);
    }
}
