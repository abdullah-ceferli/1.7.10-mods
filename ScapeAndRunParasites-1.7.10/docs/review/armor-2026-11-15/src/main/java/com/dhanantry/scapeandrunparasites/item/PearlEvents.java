/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.GameRules
 *  net.minecraftforge.event.entity.living.LivingDropsEvent
 *  net.minecraftforge.event.entity.player.PlayerDropsEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public class PearlEvents {
    private static final ResourceLocation SIM = new ResourceLocation("srparasites", "sim_enderman");
    private static final ResourceLocation FERAL = new ResourceLocation("srparasites", "fer_enderman");
    private static final ResourceLocation ASSIMARA = new ResourceLocation("srparasites", "mar_enderman");
    private static final ResourceLocation SIM_HEAD = new ResourceLocation("srparasites", "sim_endermanhead");

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent e) {
        if (e.getEntity().worldObj.isRemote) {
            return;
        }
        ResourceLocation id = EntityList.getKey((Entity)e.getEntity());
        if (id == null) {
            return;
        }
        float chance = 0.0f;
        if (id.equals((Object)SIM)) {
            chance = 0.1f;
        } else if (id.equals((Object)FERAL)) {
            chance = 0.3f;
        } else if (id.equals((Object)ASSIMARA)) {
            chance = 0.4f;
        }
        if (chance <= 0.0f) {
            return;
        }
        if (e.getEntity().worldObj.rand.nextFloat() < chance) {
            ItemStack drop = new ItemStack(SRPItems.pearl);
            e.getDrops().add(new EntityItem(e.getEntity().worldObj, e.getEntity().posX, e.getEntity().posY, e.getEntity().posZ, drop));
        }
    }

    @SubscribeEvent
    public static void onPlayerDrops(PlayerDropsEvent e) {
        boolean isBeholder;
        EntityPlayer player = e.getEntityPlayer();
        GameRules rules = player.worldObj.getGameRules();
        if (!SRPConfigMobs.pearlDestroyedOnBeholderKill) {
            return;
        }
        if (rules.getGameRuleBooleanValue("keepInventory")) {
            return;
        }
        DamageSource src = e.getSource();
        if (src == null) {
            return;
        }
        Entity killer = src.getEntity();
        if (!(killer instanceof EntityLivingBase)) {
            return;
        }
        ResourceLocation killerId = EntityList.getKey((Entity)killer);
        if (killerId == null) {
            return;
        }
        boolean bl = isBeholder = killerId.equals((Object)SIM) || killerId.equals((Object)SIM_HEAD) || killerId.equals((Object)FERAL) || killerId.equals((Object)ASSIMARA);
        if (!isBeholder) {
            return;
        }
        Iterator it = e.getDrops().iterator();
        while (it.hasNext()) {
            EntityItem ent = (EntityItem)it.next();
            ItemStack s = ent.getEntityItem();
            if (s == null || s.getItem() != SRPItems.pearl) continue;
            it.remove();
        }
    }
}

