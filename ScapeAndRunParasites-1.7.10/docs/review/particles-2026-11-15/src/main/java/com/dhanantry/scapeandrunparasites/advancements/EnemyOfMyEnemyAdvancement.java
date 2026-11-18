/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.monster.EntityCreeper
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.advancements;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class EnemyOfMyEnemyAdvancement {
    private static final ResourceLocation ADV_ID = new ResourceLocation("srparasites", "enemy_enemy");
    private static final String CRITERION = "creeper_killed_parasite";
    private static final double RADIUS_SQ = 4096.0;

    public static void register() {
        MinecraftForge.EVENT_BUS.register((Object)new EnemyOfMyEnemyAdvancement());
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent e) {
        if (e.getEntityLiving().worldObj.isRemote) {
            return;
        }
        Entity killer = e.getSource().getEntity();
        if (!(killer instanceof EntityCreeper)) {
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
        WorldServer ws = (WorldServer)e.getEntity().worldObj;
        for (EntityPlayerMP p : ws.getMinecraftServer().getPlayerList().getPlayers()) {
            Advancement adv;
            if (p.worldObj.provider.dimensionId != ws.provider.dimensionId || p.getDistanceSqToEntity(e.getEntity()) > 4096.0 || (adv = p.getServer().getAdvancementManager().getAdvancement(ADV_ID)) == null) continue;
            p.getAdvancements().grantCriterion(adv, CRITERION);
        }
    }
}

