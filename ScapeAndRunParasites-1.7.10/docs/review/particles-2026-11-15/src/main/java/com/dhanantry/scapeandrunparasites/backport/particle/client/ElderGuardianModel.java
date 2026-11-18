package com.dhanantry.scapeandrunparasites.backport.particle.client;
import net.minecraft.client.model.*;import net.minecraft.entity.Entity;import net.minecraft.util.MathHelper;
/** Original guardian geometry in the fully extended, stationary elder-appearance pose. */
final class ElderGuardianModel extends ModelBase {
    private final ModelRenderer body,eye;private final ModelRenderer[] spines=new ModelRenderer[12],tail=new ModelRenderer[3];
    ElderGuardianModel(){
        textureWidth=64;textureHeight=64;body=new ModelRenderer(this);
        body.setTextureOffset(0,0).addBox(-6,10,-8,12,12,16);body.setTextureOffset(0,28).addBox(-8,10,-6,2,12,12);
        body.mirror=true;body.setTextureOffset(0,28).addBox(6,10,-6,2,12,12);body.mirror=false;
        body.setTextureOffset(16,40).addBox(-6,8,-6,12,2,12);body.setTextureOffset(16,40).addBox(-6,22,-6,12,2,12);
        for(int i=0;i<12;i++){spines[i]=new ModelRenderer(this,0,0);spines[i].addBox(-1,-4.5F,-1,2,9,2);body.addChild(spines[i]);}
        eye=new ModelRenderer(this,8,0);eye.addBox(-1,15,0,2,2,1);body.addChild(eye);
        tail[0]=new ModelRenderer(this,40,0);tail[0].addBox(-2,14,7,4,4,8);
        tail[1]=new ModelRenderer(this,0,54);tail[1].addBox(0,14,0,3,3,7);
        tail[2]=new ModelRenderer(this);tail[2].setTextureOffset(41,32).addBox(0,14,0,2,2,6);tail[2].setTextureOffset(25,19).addBox(1,10.5F,3,1,9,9);
        body.addChild(tail[0]);tail[0].addChild(tail[1]);tail[1].addChild(tail[2]);
    }
    void renderAppearance(Entity camera,float partial,float tailPhase){
        float[] ax={1.75F,0.25F,0,0,0.5F,0.5F,0.5F,0.5F,1.25F,0.75F,0,0},ay={0,0,0,0,0.25F,1.75F,1.25F,0.75F,0,0,0,0},az={0,0,0.25F,1.75F,0,0,0,0,0,0,0.75F,1.25F};
        float[] px={0,0,8,-8,-8,8,8,-8,0,0,8,-8},py={-8,-8,-8,-8,0,0,0,0,8,8,8,8},pz={8,-8,0,0,-8,-8,8,8,8,-8,0,0};
        for(int i=0;i<12;i++){ModelRenderer s=spines[i];s.rotateAngleX=(float)Math.PI*ax[i];s.rotateAngleY=(float)Math.PI*ay[i];s.rotateAngleZ=(float)Math.PI*az[i];float extent=1+MathHelper.cos(partial*1.5F+i)*0.01F;s.setRotationPoint(px[i]*extent,16+py[i]*extent,pz[i]*extent);}
        eye.rotationPointZ=-8.25F;eye.rotationPointY=camera.prevPosY+camera.getEyeHeight()>0.99875F?0:1;
        com.dhanantry.scapeandrunparasites.backport.math.Vec3d direction=new com.dhanantry.scapeandrunparasites.backport.math.Vec3d(-camera.prevPosX,0,-camera.prevPosZ).normalize().rotateYaw((float)Math.PI/2);
        double dot=direction.zCoord;
        eye.rotationPointX=MathHelper.sqrt_float((float)Math.abs(dot))*2*(float)Math.signum(dot);eye.showModel=true;
        tail[0].rotateAngleY=MathHelper.sin(tailPhase)*(float)Math.PI*0.05F;tail[1].rotateAngleY=MathHelper.sin(tailPhase)*(float)Math.PI*0.1F;tail[2].rotateAngleY=MathHelper.sin(tailPhase)*(float)Math.PI*0.15F;
        tail[1].setRotationPoint(-1.5F,0.5F,14);tail[2].setRotationPoint(0.5F,0.5F,6);body.render(0.0625F);
    }
}
