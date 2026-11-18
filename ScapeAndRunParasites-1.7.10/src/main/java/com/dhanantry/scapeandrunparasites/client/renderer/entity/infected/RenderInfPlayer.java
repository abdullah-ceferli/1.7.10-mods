/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.entity.RenderLivingBase
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.client.renderer.entity.layers.LayerRenderer
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.MathHelper
 */
package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfPlayer;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.client.renderer.SRPLayerBipedArmor;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPlayer;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.MathHelper;

public class RenderInfPlayer
extends RenderSRP<EntityInfPlayer> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("srparasites:textures/entity/monster/infplayer.png");

    public RenderInfPlayer(RenderManager manager) {
        super(manager, new ModelInfPlayer(), 0.5f);
        this.addLayer((LayerRenderer)new SRPLayerBipedArmor((RenderLivingBase<?>)this));
    }

    protected void preRenderCallback(EntityInfPlayer entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float ff = entitylivingbaseIn.getSelfeFlashIntensity2();
        float f1 = 1.0f + MathHelper.sin((float)(f * 100.0f)) * f * 0.01f;
        f = MathHelper.clamp_float((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)(ff * f3), (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityInfPlayer entity) {
        return TEXTURE;
    }

    protected void applyRotations(EntityInfPlayer entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse((EntityLivingBase)entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

