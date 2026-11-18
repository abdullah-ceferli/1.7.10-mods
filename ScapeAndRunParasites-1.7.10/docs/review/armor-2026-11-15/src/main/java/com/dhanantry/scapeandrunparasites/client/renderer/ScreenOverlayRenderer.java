/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$ElementType
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Post
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class ScreenOverlayRenderer {
    private static final ResourceLocation VIRAL = new ResourceLocation("srparasites:textures/gui/screen_viral.png");
    private static final ResourceLocation BLEED = new ResourceLocation("srparasites:textures/gui/screen_bleed.png");
    private static final ResourceLocation VOMIT = new ResourceLocation("srparasites:textures/gui/screen_vomit.png");
    private static final ResourceLocation VISION = new ResourceLocation("srparasites:textures/gui/screen_novision.png");
    private int vomitY = 0;

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledRes = new ScaledResolution(mc);
        int screenWidth = scaledRes.getScaledWidth();
        int screenHeight = scaledRes.getScaledHeight();
        if (mc.thePlayer.isPotionActive(SRPPotions.VIRA_E)) {
            this.screenEffectViral(mc, VIRAL, screenWidth, screenHeight);
        }
        if (mc.thePlayer.isPotionActive(SRPPotions.BLEED_E)) {
            this.screenEffectBleed(mc, BLEED, screenWidth, screenHeight);
        }
        if (mc.thePlayer.isPotionActive(SRPPotions.NOVISION_E)) {
            this.screenEffectVision(mc, VISION, screenWidth, screenHeight);
        }
        if (mc.thePlayer.isPotionActive(SRPPotions.VOMIT_E)) {
            this.screenEffectVomit(mc, VOMIT, screenWidth, screenHeight);
        } else {
            this.vomitY = 0;
        }
    }

    private void screenEffectBleed(Minecraft mc, ResourceLocation in, int screenW, int creenH) {
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        mc.getTextureManager().bindTexture(in);
        int posX = 0;
        int posY = 0;
        int textureWidth = screenW;
        int textureHeight = creenH;
        Gui.drawModalRectWithCustomSizedTexture((int)posX, (int)posY, (float)0.0f, (float)0.0f, (int)textureWidth, (int)textureHeight, (float)textureWidth, (float)textureHeight);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void screenEffectViral(Minecraft mc, ResourceLocation in, int screenW, int creenH) {
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        mc.getTextureManager().bindTexture(in);
        int posX = 0;
        int posY = 0;
        int textureWidth = screenW;
        int textureHeight = creenH;
        Gui.drawModalRectWithCustomSizedTexture((int)posX, (int)posY, (float)0.0f, (float)0.0f, (int)textureWidth, (int)textureHeight, (float)textureWidth, (float)textureHeight);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void screenEffectVomit(Minecraft mc, ResourceLocation in, int screenW, int creenH) {
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        mc.getTextureManager().bindTexture(in);
        int extra = 8;
        int posX = 0;
        int posY = this.vomitY++;
        int textureWidth = screenW;
        int textureHeight = creenH * extra;
        Gui.drawModalRectWithCustomSizedTexture((int)posX, (int)posY, (float)0.0f, (float)0.0f, (int)textureWidth, (int)textureHeight, (float)textureWidth, (float)textureHeight);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        if (this.vomitY >= 0) {
            this.vomitY = -creenH * 7;
        }
    }

    private void screenEffectVision(Minecraft mc, ResourceLocation in, int screenW, int creenH) {
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        mc.getTextureManager().bindTexture(in);
        int posX = 0;
        int posY = 0;
        int textureWidth = screenW;
        int textureHeight = creenH;
        Gui.drawModalRectWithCustomSizedTexture((int)posX, (int)posY, (float)0.0f, (float)0.0f, (int)textureWidth, (int)textureHeight, (float)textureWidth, (float)textureHeight);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}

