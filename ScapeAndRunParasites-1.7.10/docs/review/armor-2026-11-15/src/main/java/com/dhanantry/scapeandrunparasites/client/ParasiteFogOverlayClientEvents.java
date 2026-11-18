/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.renderer.texture.TextureMap
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$ElementType
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Post
 *  net.minecraftforge.client.event.TextureStitchEvent$Pre
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.relauncher.Side
 */
package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.backport.state.BlockStates;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.backport.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.math.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid="srparasites", value={Side.CLIENT})
public final class ParasiteFogOverlayClientEvents {
    private static final ResourceLocation FOG_OVERLAY_TEXTURE = new ResourceLocation("srparasites", "blocks/parasite_fog");
    private static TextureAtlasSprite fogOverlaySprite;

    private ParasiteFogOverlayClientEvents() {
    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        fogOverlaySprite = event.getMap().registerSprite(FOG_OVERLAY_TEXTURE);
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }
        if (mc.gameSettings.hideGUI) {
            return;
        }
        if (fogOverlaySprite == null) {
            return;
        }
        if (!ParasiteFogOverlayClientEvents.isPlayerHeadInFog((EntityPlayer)mc.thePlayer)) {
            return;
        }
        ParasiteFogOverlayClientEvents.renderFogOverlay(mc, event.getResolution().getScaledWidth(), event.getResolution().getScaledHeight());
    }

    private static boolean isPlayerHeadInFog(EntityPlayer player) {
        BlockPos eyePos = new BlockPos(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ);
        IBlockState state = BlockStates.get(player.worldObj, eyePos);
        return state.getBlock() == SRPBlocks.ParasiteFog;
    }

    private static void renderFogOverlay(Minecraft mc, int width, int height) {
        mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        GlStateManager.disableDepth();
        GlStateManager.depthMask((boolean)false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)0.85f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        double z = -90.0;
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(0.0, (double)height, z).tex((double)fogOverlaySprite.getMinU(), (double)fogOverlaySprite.getMaxV()).endVertex();
        buffer.pos((double)width, (double)height, z).tex((double)fogOverlaySprite.getMaxU(), (double)fogOverlaySprite.getMaxV()).endVertex();
        buffer.pos((double)width, 0.0, z).tex((double)fogOverlaySprite.getMaxU(), (double)fogOverlaySprite.getMinV()).endVertex();
        buffer.pos(0.0, 0.0, z).tex((double)fogOverlaySprite.getMinU(), (double)fogOverlaySprite.getMinV()).endVertex();
        tessellator.draw();
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }
}

