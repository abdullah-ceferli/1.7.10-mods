/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.entity.layers.LayerRenderer
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderCosmical;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;
import net.minecraft.client.Minecraft;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class LayerCosmicalHacking
implements LayerRenderer<EntityPCosmical> {
    private static ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("srparasites:textures/entity/layer/cosmichasking.png");
    private RenderCosmical creeperRenderer;
    private ModelSRP modelIn;

    public LayerCosmicalHacking(RenderCosmical creeperRendererIn, ModelSRP model) {
        this.creeperRenderer = creeperRendererIn;
        this.modelIn = model;
    }

    public void doRenderLayer(EntityPCosmical entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn.getShadowStatus() && !entitylivingbaseIn.getCloneC() && !entitylivingbaseIn.getTargetedEntityVictims().isEmpty()) {
            boolean flag = entitylivingbaseIn.isInvisible();
            GlStateManager.depthMask((!flag ? 1 : 0) != 0);
            this.creeperRenderer.bindTexture(LIGHTNING_TEXTURE);
            GlStateManager.matrixMode((int)5890);
            GlStateManager.loadIdentity();
            float f = (float)entitylivingbaseIn.ticksExisted + partialTicks;
            GlStateManager.translate((float)(f * 0.01f), (float)(f * 0.01f), (float)0.0f);
            GlStateManager.matrixMode((int)5888);
            GlStateManager.enableBlend();
            float f1 = 0.5f;
            GlStateManager.color((float)178.0f, (float)0.5f, (float)250.5f, (float)1.0f);
            GlStateManager.disableLighting();
            GlStateManager.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE);
            this.modelIn.setModelAttributes(this.creeperRenderer.getMainModel());
            Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
            this.modelIn.render((Entity)entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
            GlStateManager.matrixMode((int)5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode((int)5888);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.depthMask((boolean)flag);
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}

