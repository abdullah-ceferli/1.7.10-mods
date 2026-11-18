/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.ActiveRenderInfo
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$FogMode
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  net.minecraftforge.client.event.EntityViewRenderEvent$FogColors
 *  net.minecraftforge.client.event.EntityViewRenderEvent$FogDensity
 *  net.minecraftforge.client.event.EntityViewRenderEvent$RenderFogEvent
 *  net.minecraftforge.client.event.RenderBlockOverlayEvent
 *  net.minecraftforge.client.event.RenderBlockOverlayEvent$OverlayType
 *  net.minecraftforge.fluids.IFluidBlock
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.lwjgl.opengl.GL11
 */
package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.backport.registry.RegistryNames;

import net.minecraft.block.Block;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fluids.IFluidBlock;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(value={Side.CLIENT}, modid="srparasites")
public final class DeadBloodFogHandler {
    private static final String DEADBLOOD_BLOCK_ID = "srparasites:deadblood";
    private static final String DEADBLOOD_FLUID_NAME = "deadblood";

    private static boolean isDeadBloodState(IBlockState s) {
        if (s == null) {
            return false;
        }
        Block b = s.getBlock();
        ResourceLocation key = RegistryNames.get(b);
        if (key != null && DEADBLOOD_BLOCK_ID.equals(key.toString())) {
            return true;
        }
        if (b instanceof IFluidBlock) {
            IFluidBlock fb = (IFluidBlock)b;
            return fb.getFluid() != null && DEADBLOOD_FLUID_NAME.equals(fb.getFluid().getName());
        }
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public static boolean isEyeInDeadBlood(Entity e, double pt) {
        return e != null && DeadBloodFogHandler.isDeadBloodState(ActiveRenderInfo.getBlockStateAtEntityViewpoint((World)e.worldObj, (Entity)e, (float)((float)pt)));
    }

    @SubscribeEvent
    public static void onFogColors(EntityViewRenderEvent.FogColors event) {
        if (DeadBloodFogHandler.isEyeInDeadBlood(event.getEntity(), event.getRenderPartialTicks())) {
            event.setRed(0.08f);
            event.setGreen(0.2f);
            event.setBlue(0.07f);
        }
    }

    @SubscribeEvent
    public static void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (DeadBloodFogHandler.isEyeInDeadBlood(event.getEntity(), event.getRenderPartialTicks())) {
            event.setDensity(0.48f);
            event.setCanceled(true);
            GlStateManager.setFog((GlStateManager.FogMode)GlStateManager.FogMode.EXP2);
        }
    }

    @SubscribeEvent
    public static void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        if (DeadBloodFogHandler.isEyeInDeadBlood(event.getEntity(), event.getRenderPartialTicks())) {
            GlStateManager.setFog((GlStateManager.FogMode)GlStateManager.FogMode.LINEAR);
            GL11.glFogf((int)2915, (float)0.0f);
            GL11.glFogf((int)2916, (float)2.5f);
        }
    }

    @SubscribeEvent
    public static void onOverlay(RenderBlockOverlayEvent event) {
        if (event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.WATER && DeadBloodFogHandler.isEyeInDeadBlood((Entity)event.getPlayer(), 0.0)) {
            event.setCanceled(true);
        }
    }
}

