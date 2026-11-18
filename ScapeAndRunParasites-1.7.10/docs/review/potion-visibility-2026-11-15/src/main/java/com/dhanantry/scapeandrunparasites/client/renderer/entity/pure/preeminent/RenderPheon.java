/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.MathHelper
 */
package com.dhanantry.scapeandrunparasites.client.renderer.entity.pure.preeminent;

import com.dhanantry.scapeandrunparasites.client.model.entity.pure.preeminent.ModelPheon;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityPheon;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.MathHelper;

public class RenderPheon
extends RenderMalleable<EntityPheon> {
    public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/pheon.png");
    public static final ResourceLocation TEXTURESP = new ResourceLocation("srparasites:textures/entity/monster/pheonsp1.png");

    public RenderPheon(RenderManager manager) {
        super(manager, new ModelPheon(), 1.3f);
    }

    protected void preRenderCallback(EntityPheon entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + MathHelper.sin((float)(f * 100.0f)) * f * 0.01f;
        f = MathHelper.clamp_float((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityPheon entity) {
        switch (entity.getSkin()) {
            case 1: {
                return TEXTURESP;
            }
        }
        return TEXTURES;
    }

    protected void applyRotations(EntityPheon entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse((EntityLivingBase)entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

