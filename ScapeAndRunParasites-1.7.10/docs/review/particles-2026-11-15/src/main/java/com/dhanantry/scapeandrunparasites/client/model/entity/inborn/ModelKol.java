/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.entity.Entity
 */
package com.dhanantry.scapeandrunparasites.client.model.entity.inborn;

import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelKol
extends ModelSRP {
    public ModelRenderer mainbody;
    public ModelRenderer head;
    public ModelRenderer tail;
    public ModelRenderer middleleftleg;
    public ModelRenderer backleftleg;
    public ModelRenderer frontleftleg;
    public ModelRenderer backrightleg;
    public ModelRenderer middlerightleg;
    public ModelRenderer frontrightleg;
    public ModelRenderer bottom;
    public ModelRenderer rbody_joint;
    public ModelRenderer lbody_joint;
    public ModelRenderer leg_1;
    public ModelRenderer leg_1_1;
    public ModelRenderer leg_1_2;
    public ModelRenderer leg_1_3;
    public ModelRenderer leg_1_4;
    public ModelRenderer leg_1_5;
    public ModelRenderer frontleftarm;
    public ModelRenderer frontrightarm;
    public ModelRenderer leg_1_6;
    public ModelRenderer leg_1_7;
    public ModelRenderer rbody;
    public ModelRenderer lbody;

    public ModelKol() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.tail = new ModelRenderer((ModelBase)this, 0, 7);
        this.tail.setRotationPoint(0.0f, 0.0f, 3.6f);
        this.tail.addBox(-1.5f, -1.5f, -4.0f, 3, 3, 4, 0.0f);
        this.setRotateAngle(this.tail, -0.2268928f, 0.0f, 0.0f);
        this.leg_1_5 = new ModelRenderer((ModelBase)this, 24, 7);
        this.leg_1_5.setRotationPoint(-0.3f, 1.5f, 0.0f);
        this.leg_1_5.addBox(-0.5f, 0.0f, -0.5f, 1, 3, 1, 0.0f);
        this.setRotateAngle(this.leg_1_5, 0.0f, 0.0f, -0.62831855f);
        this.mainbody = new ModelRenderer((ModelBase)this, 0, 0);
        this.mainbody.setRotationPoint(0.0f, 18.9f, 1.7f);
        this.mainbody.addBox(-1.0f, -1.0f, -5.0f, 2, 2, 5, 0.0f);
        this.middleleftleg = new ModelRenderer((ModelBase)this, 0, 0);
        this.middleleftleg.setRotationPoint(0.9f, 1.1f, -2.6f);
        this.middleleftleg.addBox(-0.5f, -1.0f, -0.5f, 1, 3, 1, 0.0f);
        this.setRotateAngle(this.middleleftleg, 0.0f, 0.0f, -0.5235988f);
        this.leg_1_6 = new ModelRenderer((ModelBase)this, 42, 7);
        this.leg_1_6.setRotationPoint(2.6f, -1.1f, 0.9f);
        this.leg_1_6.addBox(-0.5f, 0.0f, -1.0f, 1, 3, 2, 0.0f);
        this.setRotateAngle(this.leg_1_6, -0.62831855f, 0.0f, -0.57595867f);
        this.frontrightarm = new ModelRenderer((ModelBase)this, 36, 7);
        this.frontrightarm.setRotationPoint(-0.9f, -1.5f, 0.7f);
        this.frontrightarm.addBox(0.0f, -0.5f, -0.5f, 3, 1, 1, 0.0f);
        this.setRotateAngle(this.frontrightarm, 0.0f, 2.0943952f, 0.0f);
        this.leg_1_2 = new ModelRenderer((ModelBase)this, 0, 7);
        this.leg_1_2.setRotationPoint(0.3f, 1.5f, 0.0f);
        this.leg_1_2.addBox(-0.5f, 0.0f, -0.5f, 1, 3, 1, 0.0f);
        this.setRotateAngle(this.leg_1_2, 0.0f, 0.0f, 0.62831855f);
        this.head = new ModelRenderer((ModelBase)this, 46, 0);
        this.head.setRotationPoint(0.0f, -1.6f, -4.1f);
        this.head.addBox(-2.0f, -1.5f, -3.0f, 4, 3, 4, 0.0f);
        this.setRotateAngle(this.head, -0.5235988f, 0.0f, 0.0f);
        this.leg_1_1 = new ModelRenderer((ModelBase)this, 58, 0);
        this.leg_1_1.setRotationPoint(0.5f, 1.5f, 0.0f);
        this.leg_1_1.addBox(-0.5f, 0.0f, -0.5f, 1, 3, 1, 0.0f);
        this.setRotateAngle(this.leg_1_1, 0.0f, 0.0f, 0.62831855f);
        this.leg_1_3 = new ModelRenderer((ModelBase)this, 16, 7);
        this.leg_1_3.setRotationPoint(-0.5f, 1.5f, 0.0f);
        this.leg_1_3.addBox(-0.5f, 0.0f, -0.5f, 1, 3, 1, 0.0f);
        this.setRotateAngle(this.leg_1_3, 0.0f, 0.0f, -0.62831855f);
        this.leg_1_4 = new ModelRenderer((ModelBase)this, 20, 7);
        this.leg_1_4.setRotationPoint(-0.5f, 1.5f, 0.0f);
        this.leg_1_4.addBox(-0.5f, 0.0f, -0.5f, 1, 3, 1, 0.0f);
        this.setRotateAngle(this.leg_1_4, 0.0f, 0.0f, -0.62831855f);
        this.bottom = new ModelRenderer((ModelBase)this, 10, 7);
        this.bottom.setRotationPoint(0.0f, 0.7f, -5.2f);
        this.bottom.addBox(-1.0f, -3.0f, 0.0f, 2, 3, 1, 0.0f);
        this.setRotateAngle(this.bottom, 0.61086524f, 0.0f, 0.0f);
        this.frontrightleg = new ModelRenderer((ModelBase)this, 42, 0);
        this.frontrightleg.setRotationPoint(-0.9f, 1.1f, -4.6f);
        this.frontrightleg.addBox(-0.5f, -1.0f, -0.5f, 1, 3, 1, 0.0f);
        this.setRotateAngle(this.frontrightleg, 0.0f, 0.0f, 0.5235988f);
        this.frontleftarm = new ModelRenderer((ModelBase)this, 28, 7);
        this.frontleftarm.setRotationPoint(0.9f, -1.5f, 0.7f);
        this.frontleftarm.addBox(0.0f, -0.5f, -0.5f, 3, 1, 1, 0.0f);
        this.setRotateAngle(this.frontleftarm, 0.0f, 1.1170107f, 0.0f);
        this.leg_1_7 = new ModelRenderer((ModelBase)this, 48, 7);
        this.leg_1_7.setRotationPoint(2.6f, -1.1f, -0.9f);
        this.leg_1_7.addBox(-0.5f, 0.0f, -1.0f, 1, 3, 2, 0.0f);
        this.setRotateAngle(this.leg_1_7, 0.62831855f, 0.0f, -0.57595867f);
        this.backleftleg = new ModelRenderer((ModelBase)this, 9, 0);
        this.backleftleg.setRotationPoint(0.9f, 1.1f, -0.6f);
        this.backleftleg.addBox(-0.5f, -1.0f, -0.5f, 1, 3, 1, 0.0f);
        this.setRotateAngle(this.backleftleg, 0.0f, 0.0f, -0.5235988f);
        this.leg_1 = new ModelRenderer((ModelBase)this, 46, 0);
        this.leg_1.setRotationPoint(0.5f, 1.5f, 0.0f);
        this.leg_1.addBox(-0.5f, 0.0f, -0.5f, 1, 3, 1, 0.0f);
        this.setRotateAngle(this.leg_1, 0.0f, 0.0f, 0.62831855f);
        this.frontleftleg = new ModelRenderer((ModelBase)this, 13, 0);
        this.frontleftleg.setRotationPoint(0.9f, 1.1f, -4.6f);
        this.frontleftleg.addBox(-0.5f, -1.0f, -0.5f, 1, 3, 1, 0.0f);
        this.setRotateAngle(this.frontleftleg, 0.0f, 0.0f, -0.5235988f);
        this.lbody_joint = new ModelRenderer((ModelBase)this, 0, 0);
        this.lbody_joint.setRotationPoint(0.0f, 0.0f, -0.8f);
        this.lbody_joint.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f);
        this.middlerightleg = new ModelRenderer((ModelBase)this, 30, 0);
        this.middlerightleg.setRotationPoint(-0.9f, 1.1f, -2.6f);
        this.middlerightleg.addBox(-0.5f, -1.0f, -0.5f, 1, 3, 1, 0.0f);
        this.setRotateAngle(this.middlerightleg, 0.0f, 0.0f, 0.5235988f);
        this.lbody = new ModelRenderer((ModelBase)this, 30, 0);
        this.lbody.setRotationPoint(0.0f, -1.3f, 0.0f);
        this.lbody.addBox(0.0f, -1.5f, -3.0f, 4, 3, 4, 0.0f);
        this.setRotateAngle(this.lbody, 0.0f, 0.0f, 0.15707964f);
        this.backrightleg = new ModelRenderer((ModelBase)this, 26, 0);
        this.backrightleg.setRotationPoint(-0.9f, 1.1f, -0.6f);
        this.backrightleg.addBox(-0.5f, -1.0f, -0.5f, 1, 3, 1, 0.0f);
        this.setRotateAngle(this.backrightleg, 0.0f, 0.0f, 0.5235988f);
        this.rbody_joint = new ModelRenderer((ModelBase)this, 0, 0);
        this.rbody_joint.setRotationPoint(0.0f, 0.0f, -0.8f);
        this.rbody_joint.addBox(-0.5f, -0.5f, 0.0f, 1, 1, 1, 0.0f);
        this.rbody = new ModelRenderer((ModelBase)this, 14, 0);
        this.rbody.setRotationPoint(0.0f, -1.3f, 0.0f);
        this.rbody.addBox(-4.0f, -1.5f, -3.0f, 4, 3, 4, 0.0f);
        this.setRotateAngle(this.rbody, 0.0f, 0.0f, -0.15707964f);
        this.mainbody.addChild(this.tail);
        this.frontrightleg.addChild(this.leg_1_5);
        this.mainbody.addChild(this.middleleftleg);
        this.frontleftarm.addChild(this.leg_1_6);
        this.bottom.addChild(this.frontrightarm);
        this.frontleftleg.addChild(this.leg_1_2);
        this.mainbody.addChild(this.head);
        this.backleftleg.addChild(this.leg_1_1);
        this.backrightleg.addChild(this.leg_1_3);
        this.middlerightleg.addChild(this.leg_1_4);
        this.mainbody.addChild(this.bottom);
        this.mainbody.addChild(this.frontrightleg);
        this.bottom.addChild(this.frontleftarm);
        this.frontrightarm.addChild(this.leg_1_7);
        this.mainbody.addChild(this.backleftleg);
        this.middleleftleg.addChild(this.leg_1);
        this.mainbody.addChild(this.frontleftleg);
        this.mainbody.addChild(this.lbody_joint);
        this.mainbody.addChild(this.middlerightleg);
        this.lbody_joint.addChild(this.lbody);
        this.mainbody.addChild(this.backrightleg);
        this.mainbody.addChild(this.rbody_joint);
        this.rbody_joint.addChild(this.rbody);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        this.mainbody.render(scale);
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
    }
}

