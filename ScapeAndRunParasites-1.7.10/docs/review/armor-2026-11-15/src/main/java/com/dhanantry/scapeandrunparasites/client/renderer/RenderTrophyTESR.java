/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.block.BlockEntityTrophy;
import com.dhanantry.scapeandrunparasites.entity.tile.TileEntityTrophy;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraft.world.World;

public class RenderTrophyTESR
extends TileEntitySpecialRenderer<TileEntityTrophy> {
    public void render(TileEntityTrophy te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        World world = te.getWorldObj();
        if (world == null) {
            return;
        }
        BlockPos pos = te.getPos();
        IBlockState state = BlockStates.get(world, pos);
        if (!(state.getBlock() instanceof BlockEntityTrophy)) {
            return;
        }
        BlockEntityTrophy trophy = (BlockEntityTrophy)state.getBlock();
        Entity entity = this.getOrCreateEntity(te, trophy.getMobId());
        if (entity == null) {
            return;
        }
        entity.setSilent(true);
        entity.setNoGravity(true);
        if (!trophy.isAnimate()) {
            entity.ticksExisted = 0;
        } else {
            long t = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
            entity.ticksExisted = (int)(t % 100000L);
        }
        float yaw = Minecraft.getMinecraft().thePlayer != null ? Minecraft.getMinecraft().thePlayer.rotationYaw : 0.0f;
        entity.rotationYaw = -yaw;
        entity.prevRotationYaw = -yaw;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)((float)x + 0.5f), (float)((float)y + 1.0f + trophy.getYOffset()), (float)((float)z + 0.5f));
        float s = trophy.getScale();
        GlStateManager.scale((float)s, (float)s, (float)s);
        if (trophy.getTextureMode() == BlockEntityTrophy.TrophyTextureMode.STONE) {
            GlStateManager.color((float)0.75f, (float)0.75f, (float)0.75f, (float)1.0f);
        } else {
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
        Minecraft.getMinecraft().getRenderManager().renderEntity(entity, 0.0, 0.0, 0.0, 0.0f, partialTicks, false);
        GlStateManager.popMatrix();
    }

    private Entity getOrCreateEntity(TileEntityTrophy te, ResourceLocation mobId) {
        if (te.cachedRenderEntity != null) {
            return te.cachedRenderEntity;
        }
        WorldClient w = Minecraft.getMinecraft().theWorld;
        if (w == null) {
            return null;
        }
        Entity e = EntityList.createEntityByIDFromName((ResourceLocation)mobId, (World)w);
        if (e == null) {
            return null;
        }
        if (e instanceof EntityLiving) {
            ((EntityLiving)e).setAlwaysRenderNameTag(true);
        }
        te.cachedRenderEntity = e;
        return e;
    }
}

