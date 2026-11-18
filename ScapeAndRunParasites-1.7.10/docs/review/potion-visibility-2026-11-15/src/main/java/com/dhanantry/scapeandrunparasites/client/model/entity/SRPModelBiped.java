/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelBiped
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumHandSide
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package com.dhanantry.scapeandrunparasites.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import com.dhanantry.scapeandrunparasites.backport.client.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class SRPModelBiped
extends ModelBiped {
    private byte parent;
    public ModelRenderer jointH;
    public ModelRenderer jointHW;
    public ModelRenderer body;
    public ModelRenderer rightA;
    public ModelRenderer leftA;
    public ModelRenderer rightL;
    public ModelRenderer leftL;
    public ArmPose leftAP = ArmPose.EMPTY;
    public ArmPose rightAP = ArmPose.EMPTY;
    public boolean isSneak;

    public SRPModelBiped() {
        this(0.0f);
    }

    public SRPModelBiped(float modelSize) {
        this(modelSize, 0.0f, 64, 32);
    }

    public SRPModelBiped(float modelSize, float p_i1149_2_, int textureWidthIn, int textureHeightIn) {
        this.textureWidth = textureWidthIn;
        this.textureHeight = textureHeightIn;
        this.jointH = new ModelRenderer((ModelBase)this, 0, 0);
        this.jointH.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, modelSize);
        this.jointH.setRotationPoint(0.0f, 0.0f + p_i1149_2_, 0.0f);
        this.jointHW = new ModelRenderer((ModelBase)this, 32, 0);
        this.jointHW.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, modelSize + 0.5f);
        this.jointHW.setRotationPoint(0.0f, 0.0f + p_i1149_2_, 0.0f);
        this.body = new ModelRenderer((ModelBase)this, 16, 16);
        this.body.addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, modelSize);
        this.body.setRotationPoint(0.0f, 0.0f + p_i1149_2_, 0.0f);
        this.rightA = new ModelRenderer((ModelBase)this, 40, 16);
        this.rightA.addBox(-3.0f, -2.0f, -2.0f, 4, 12, 4, modelSize);
        this.rightA.setRotationPoint(-5.0f, 2.0f + p_i1149_2_, 0.0f);
        this.leftA = new ModelRenderer((ModelBase)this, 40, 16);
        this.leftA.mirror = true;
        this.leftA.addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, modelSize);
        this.leftA.setRotationPoint(5.0f, 2.0f + p_i1149_2_, 0.0f);
        this.rightL = new ModelRenderer((ModelBase)this, 0, 16);
        this.rightL.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, modelSize);
        this.rightL.setRotationPoint(-1.9f, 12.0f + p_i1149_2_, 0.0f);
        this.leftL = new ModelRenderer((ModelBase)this, 0, 16);
        this.leftL.mirror = true;
        this.leftL.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, modelSize);
        this.leftL.setRotationPoint(1.9f, 12.0f + p_i1149_2_, 0.0f);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netjointHYaw, float jointHPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netjointHYaw, jointHPitch, scale, entityIn);
        GlStateManager.pushMatrix();
        if (this.isChild) {
            float f = 2.0f;
            GlStateManager.scale((float)0.75f, (float)0.75f, (float)0.75f);
            GlStateManager.translate((float)0.0f, (float)(16.0f * scale), (float)0.0f);
            this.jointH.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scale((float)0.5f, (float)0.5f, (float)0.5f);
            GlStateManager.translate((float)0.0f, (float)(24.0f * scale), (float)0.0f);
            this.body.render(scale);
            this.rightA.render(scale);
            this.leftA.render(scale);
            this.rightL.render(scale);
            this.leftL.render(scale);
            this.jointHW.render(scale);
        } else {
            if (entityIn.isSneaking()) {
                GlStateManager.translate((float)0.0f, (float)0.2f, (float)0.0f);
            }
            this.jointH.render(scale);
            this.body.render(scale);
            this.rightA.render(scale);
            this.leftA.render(scale);
            this.rightL.render(scale);
            this.leftL.render(scale);
            this.jointHW.render(scale);
        }
        GlStateManager.popMatrix();
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netjointHYaw, float jointHPitch, float scaleFactor, Entity entityIn) {
        this.jointH.offsetY = 0.0f;
        this.jointH.offsetZ = 0.0f;
        this.jointH.rotateAngleX = 0.0f;
        this.leftL.rotateAngleX = 0.0f;
        this.rightL.rotateAngleX = 0.0f;
        switch (this.parent) {
            case 1: {
                this.jointH.offsetY = -0.25f;
                this.jointH.offsetZ = 0.24000001f;
                float f4 = MathHelper.cos((float)(ageInTicks * 0.08f)) * 0.07f;
                this.jointH.rotateAngleX = 0.45f + f4;
                float GS0 = 1.0f;
                if (entityIn.prevPosX == entityIn.posX && entityIn.prevPosZ == entityIn.posZ) break;
                float GS = 1.75f;
                float GD = 0.55f;
                GS0 += 0.45f;
                this.swingX(this.leftL, 0.3f * GS, 5.0f * GD, -1, limbSwing, limbSwingAmount);
                this.swingX(this.rightL, 0.3f * GS, 5.0f * GD, 1, limbSwing, limbSwingAmount);
            }
        }
    }

    public void setParent(byte in) {
        this.parent = in;
    }

    public void setModelAttributes(ModelBase model) {
        super.setModelAttributes(model);
        if (model instanceof SRPModelBiped) {
            SRPModelBiped modelbiped = (SRPModelBiped)model;
            this.leftAP = modelbiped.leftAP;
            this.rightAP = modelbiped.rightAP;
            this.isSneak = modelbiped.isSneak;
        }
    }

    public void setVisible(boolean visible) {
        this.jointH.showModel = visible;
        this.jointHW.showModel = visible;
        this.body.showModel = visible;
        this.rightA.showModel = visible;
        this.leftA.showModel = visible;
        this.rightL.showModel = visible;
        this.leftL.showModel = visible;
    }

    public void postRenderArm(float scale, EnumHandSide side) {
        this.getArmForSide(side).postRender(scale);
    }

    protected ModelRenderer getArmForSide(EnumHandSide side) {
        return side == EnumHandSide.LEFT ? this.leftA : this.rightA;
    }

    protected EnumHandSide getMainHand(Entity entityIn) {
        if (entityIn instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase)entityIn;
            EnumHandSide enumhandside = entitylivingbase.getPrimaryHand();
            return entitylivingbase.swingingHand == EnumHand.MAIN_HAND ? enumhandside : enumhandside.opposite();
        }
        return EnumHandSide.RIGHT;
    }

    public void swingX(ModelRenderer modelRenderer, float speed, float degree, int invert, float limbSwing, float limbSwingAmount) {
        modelRenderer.rotateAngleX = (float)((double)((float)invert * limbSwingAmount * degree) * Math.cos(limbSwing * speed) * (double)limbSwingAmount);
    }

    @SideOnly(value=Side.CLIENT)
    public static enum ArmPose {
        EMPTY,
        ITEM,
        BLOCK,
        BOW_AND_ARROW;

    }
}

