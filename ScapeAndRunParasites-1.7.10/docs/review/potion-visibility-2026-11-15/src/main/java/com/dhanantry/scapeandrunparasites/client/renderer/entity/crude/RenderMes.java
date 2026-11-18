/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.renderer.EntityRenderer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.MathHelper
 */
package com.dhanantry.scapeandrunparasites.client.renderer.entity.crude;

import com.dhanantry.scapeandrunparasites.client.model.entity.crude.ModelMes;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.layer.LayerSnow;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityMes;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.MathHelper;

public class RenderMes
extends RenderMalleable<EntityMes> {
    public static final ResourceLocation TEXTUREM = new ResourceLocation("srparasites:textures/entity/monster/mes.png");
    public static final ResourceLocation TEXTURE1 = new ResourceLocation("srparasites:textures/entity/monster/mes1.png");
    public static final ResourceLocation STEXTURE1 = new ResourceLocation("srparasites:textures/entity/monster/smes1.png");
    public static final ResourceLocation STEXTURE2 = new ResourceLocation("srparasites:textures/entity/monster/smes2.png");
    public static final ResourceLocation STEXTURE3 = new ResourceLocation("srparasites:textures/entity/monster/smes3.png");
    public static final ResourceLocation STEXTURE4 = new ResourceLocation("srparasites:textures/entity/monster/smes4.png");
    public static final ResourceLocation STEXTURE5 = new ResourceLocation("srparasites:textures/entity/monster/smes5.png");

    public RenderMes(RenderManager manager) {
        super(manager, new ModelMes(), 0.7f);
        this.addLayer(new LayerSnow(this, new ResourceLocation("srparasites:textures/entity/layer/messnow.png"), 1.0, 1.0, 1.0));
    }

    protected void preRenderCallback(EntityMes entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + MathHelper.sin((float)(f * 100.0f)) * f * 0.01f;
        f = MathHelper.clamp_float((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityMes entity) {
        switch (entity.getSkin()) {
            case 1: {
                return TEXTURE1;
            }
            case 121: {
                return STEXTURE1;
            }
            case 122: {
                return STEXTURE2;
            }
            case 123: {
                return STEXTURE3;
            }
            case 124: {
                return STEXTURE4;
            }
            case 125: {
                return STEXTURE5;
            }
        }
        return TEXTUREM;
    }

    protected void renderLivingLabel(EntityMes entityIn, String str, double x, double y, double z, int maxDistance) {
        double d0 = entityIn.getDistanceSqToEntity(this.renderManager.livingPlayer);
        if (d0 <= (double)(maxDistance * maxDistance)) {
            boolean flag = entityIn.isSneaking();
            float f = this.renderManager.playerViewY;
            float f1 = this.renderManager.playerViewX;
            boolean flag1 = this.renderManager.options.thirdPersonView == 2;
            float f2 = entityIn.height + 0.5f - (flag ? 0.25f : 0.0f);
            int i = "deadmau5".equals(str) ? -10 : 0;
            EntityRenderer.drawNameplate((FontRenderer)this.getFontRendererFromRenderManager(), (String)str, (float)((float)x), (float)((float)y + f2), (float)((float)z), (int)i, (float)f, (float)f1, (boolean)flag1, (boolean)flag);
        }
    }
}

