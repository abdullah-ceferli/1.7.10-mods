/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.projectile.EntityFishHook
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.player.ItemFishedEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package com.dhanantry.scapeandrunparasites.events;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.Locale;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class FishingHooksSRP {
    public static void register() {
        MinecraftForge.EVENT_BUS.register((Object)new FishingHooksSRP());
    }

    @SubscribeEvent
    public void onItemFished(ItemFishedEvent e) {
        EntityFishHook hook = e.getHookEntity();
        if (hook == null) {
            return;
        }
        World w = hook.worldObj;
        if (w.isRemote) {
            return;
        }
        BlockPos pos = new BlockPos(hook.posX, hook.posY, hook.posZ);
        Biome biome = w.getBiome(pos);
        boolean inHarlequin = false;
        ResourceLocation bn = (ResourceLocation)Biome.REGISTRY.getNameForObject((Object)biome);
        if (bn != null) {
            String path = bn.getResourcePath();
            inHarlequin = path != null && path.toLowerCase(Locale.ROOT).contains("harlequin");
        }
        boolean inDeadblood = false;
        ResourceLocation blockName = RegistryNames.get(BlockStates.get(w, pos).getBlock());
        if (blockName != null) {
            String bp = blockName.getResourcePath();
            boolean bl = inDeadblood = bp != null && bp.toLowerCase(Locale.ROOT).contains("deadblood");
        }
        if (inHarlequin || inDeadblood) {
            e.getDrops().clear();
            e.getDrops().add((Object)new ItemStack(SRPItems.fishlin));
        }
    }
}

