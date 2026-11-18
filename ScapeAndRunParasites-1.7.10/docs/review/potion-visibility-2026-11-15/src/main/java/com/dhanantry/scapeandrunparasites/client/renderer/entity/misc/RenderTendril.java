/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$Profile
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.ResourceLocation
 */
package com.dhanantry.scapeandrunparasites.client.renderer.entity.misc;

import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelTendrilAnged;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelTendrilBano;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelTendrilCanra;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelTendrilDragonELW;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelTendrilDragonERW;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelTendrilEsor;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelTendrilNogla;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelTendrilShyco;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityTendril;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class RenderTendril
extends RenderSRP<EntityTendril> {
    private static ModelBase shyco = new ModelTendrilShyco();
    private static ModelBase nogla = new ModelTendrilNogla();
    private static ModelBase bano = new ModelTendrilBano();
    private static ModelBase canra = new ModelTendrilCanra();
    private static ModelBase esor = new ModelTendrilEsor();
    private static ModelBase anged = new ModelTendrilAnged();
    private static ModelBase dragoneLW = new ModelTendrilDragonELW();
    private static ModelBase dragoneRW = new ModelTendrilDragonERW();
    public static final ResourceLocation TEXTURESS = new ResourceLocation("srparasites:textures/entity/monster/tendrilshyco.png");
    public static final ResourceLocation TEXTURESN = new ResourceLocation("srparasites:textures/entity/monster/tendrilnogla.png");
    public static final ResourceLocation TEXTURESB = new ResourceLocation("srparasites:textures/entity/monster/tendrilbano.png");
    public static final ResourceLocation TEXTURESC = new ResourceLocation("srparasites:textures/entity/monster/tendrilcanra.png");
    public static final ResourceLocation TEXTURESE = new ResourceLocation("srparasites:textures/entity/monster/tendrilesor.png");
    public static final ResourceLocation TEXTURESA = new ResourceLocation("srparasites:textures/entity/monster/tendrilanged.png");
    public static final ResourceLocation TEXTURESDLW = new ResourceLocation("srparasites:textures/entity/monster/tendrildragonelw.png");
    public static final ResourceLocation TEXTURESDRW = new ResourceLocation("srparasites:textures/entity/monster/tendrildragonerw.png");

    public RenderTendril(RenderManager manager) {
        super(manager, shyco, 0.3f);
    }

    protected ResourceLocation getEntityTexture(EntityTendril entity) {
        switch (entity.getSkin()) {
            case 1: {
                return TEXTURESS;
            }
            case 2: {
                return TEXTURESN;
            }
            case 3: {
                return TEXTURESC;
            }
            case 4: {
                return TEXTURESB;
            }
            case 5: {
                return TEXTURESE;
            }
            case 6: {
                return TEXTURESA;
            }
            case 7: {
                return TEXTURESDLW;
            }
            case 8: {
                return TEXTURESDRW;
            }
        }
        return TEXTURESS;
    }

    protected void renderModel(EntityTendril entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        boolean flag1;
        boolean flag = this.isVisible((EntityLivingBase)entitylivingbaseIn);
        boolean bl = flag1 = !flag && !entitylivingbaseIn.isInvisibleToPlayer((EntityPlayer)Minecraft.getMinecraft().thePlayer);
        if (flag || flag1) {
            if (!this.bindEntityTexture((Entity)entitylivingbaseIn)) {
                return;
            }
            if (flag1) {
                GlStateManager.enableBlendProfile((GlStateManager.Profile)GlStateManager.Profile.TRANSPARENT_MODEL);
            }
            switch (entitylivingbaseIn.getSkin()) {
                case 1: {
                    shyco.render((Entity)entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                    break;
                }
                case 2: {
                    nogla.render((Entity)entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                    break;
                }
                case 3: {
                    canra.render((Entity)entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                    break;
                }
                case 4: {
                    bano.render((Entity)entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                    break;
                }
                case 5: {
                    esor.render((Entity)entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                    break;
                }
                case 6: {
                    anged.render((Entity)entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                    break;
                }
                case 7: {
                    dragoneLW.render((Entity)entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                    break;
                }
                case 8: {
                    dragoneRW.render((Entity)entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                }
            }
            if (flag1) {
                GlStateManager.disableBlendProfile((GlStateManager.Profile)GlStateManager.Profile.TRANSPARENT_MODEL);
            }
        }
    }
}

