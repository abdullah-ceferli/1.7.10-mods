/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.entity.RenderLiving
 *  net.minecraft.client.renderer.entity.layers.LayerRenderer
 *  net.minecraft.util.ResourceLocation
 */
package com.dhanantry.scapeandrunparasites.client.renderer.entity.layer;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.client.Minecraft;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class LayerSnow<T extends EntityParasiteBase>
implements LayerRenderer<T> {
    private ResourceLocation LAYER;
    private final RenderLiving parasite;

    public LayerSnow(RenderLiving in, ResourceLocation l, double x, double y, double z) {
        this.parasite = in;
        this.LAYER = l;
    }

    public void doRenderLayer(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!((EntityParasiteBase)entitylivingbaseIn).getColdL()) {
            return;
        }
        this.parasite.bindTexture(this.LAYER);
        GlStateManager.enableBlend();
        if (entitylivingbaseIn.isInvisible()) {
            GlStateManager.depthMask((boolean)false);
        } else {
            GlStateManager.depthMask((boolean)true);
        }
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
        this.parasite.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale + 0.0f);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
        this.parasite.setLightmap(entitylivingbaseIn);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}

