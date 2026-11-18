package com.dhanantry.scapeandrunparasites.backport.particle.client;
import net.minecraft.client.Minecraft;import net.minecraft.client.renderer.Tessellator;import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.*;import net.minecraft.world.World;import org.lwjgl.opengl.*;
/** Effects also reachable through saved toxic-cloud particle names, beyond fixed SRP call sites. */
public final class ExtraParticleFX {
    private ExtraParticleFX(){}
    public static final class Wake extends ModernParticleFX {
        public Wake(World w,double x,double y,double z,double vx,double vy,double vz){super(w,x,y,z,0,0,0);motionX=vx;motionY=vy;motionZ=vz;particleRed=particleGreen=particleBlue=1;frame=19;setSize(0.01F,0.01F);particleMaxAge=(int)(8/(Math.random()*0.8+0.2));}
        @Override public void onUpdate(){prevPosX=posX;prevPosY=posY;prevPosZ=posZ;moveEntity(motionX,motionY,motionZ);motionX*=(double)0.98F;motionY*=(double)0.98F;motionZ*=(double)0.98F;int n=60-particleMaxAge;setSize(n*0.001F,n*0.001F);frame=19+n%4;if(particleMaxAge--<=0)setDead();}
    }
    public static final class Totem extends ModernParticleFX {
        public Totem(World w,double x,double y,double z,double vx,double vy,double vz){super(w,x,y,z,0,0,0);motionX=vx;motionY=vy;motionZ=vz;particleScale*=0.75F;particleMaxAge=60+rand.nextInt(12);
            if(rand.nextInt(4)==0){particleRed=0.6F+rand.nextFloat()*0.2F;particleGreen=0.6F+rand.nextFloat()*0.3F;particleBlue=rand.nextFloat()*0.2F;}
            else{particleRed=0.1F+rand.nextFloat()*0.2F;particleGreen=0.4F+rand.nextFloat()*0.3F;particleBlue=rand.nextFloat()*0.2F;}}
        @Override public int getBrightnessForRender(float partial){return 0xF000F0;}
        @Override public void onUpdate(){prevPosX=posX;prevPosY=posY;prevPosZ=posZ;if(particleAge++>=particleMaxAge)setDead();if(particleAge>particleMaxAge/2)particleAlpha=1-((float)particleAge-(float)(particleMaxAge/2))/(float)particleMaxAge;frame=176+7-particleAge*8/particleMaxAge;motionY+=(double)(-0.05F);moveEntity(motionX,motionY,motionZ);motionX*=(double)0.6F;motionY*=(double)0.6F;motionZ*=(double)0.6F;if(onGround){motionX*=(double)0.7F;motionZ*=(double)0.7F;}}
    }
    public static final class Spit extends ModernParticleFX {
        public Spit(World w,double x,double y,double z,double vx,double vy,double vz){super(w,x,y,z,vx,vy,vz);motionX=vx+(Math.random()*2-1)*(double)0.05F;motionY=vy+(Math.random()*2-1)*(double)0.05F;motionZ=vz+(Math.random()*2-1)*(double)0.05F;particleRed=particleGreen=particleBlue=rand.nextFloat()*0.3F+0.7F;particleScale=rand.nextFloat()*rand.nextFloat()*6+1;particleMaxAge=(int)(16/((double)rand.nextFloat()*0.8+0.2))+2;particleGravity=0.5F;}
        @Override public void onUpdate(){prevPosX=posX;prevPosY=posY;prevPosZ=posZ;if(particleAge++>=particleMaxAge)setDead();frame=7-particleAge*8/particleMaxAge;motionY+=0.004;moveEntity(motionX,motionY,motionZ);motionX*=(double)0.9F;motionY*=(double)0.9F;motionZ*=(double)0.9F;if(onGround){motionX*=(double)0.7F;motionZ*=(double)0.7F;}motionY-=0.004+0.04*(double)particleGravity;}
    }
    public static final class FallingDust extends ModernParticleFX {
        private final float initialScale,rotationSpeed;private float angle,previousAngle;
        public FallingDust(World w,double x,double y,double z,int color){super(w,x,y,z,0,0,0);motionX=motionY=motionZ=0;particleRed=((color>>16)&255)/255.0F;particleGreen=((color>>8)&255)/255.0F;particleBlue=(color&255)/255.0F;particleScale*=0.75F;particleScale*=0.9F;initialScale=particleScale;particleMaxAge=(int)(32/(Math.random()*0.8+0.2));particleMaxAge=(int)((float)particleMaxAge*0.9F);rotationSpeed=((float)Math.random()-0.5F)*0.1F;angle=(float)Math.random()*((float)Math.PI*2);}
        @Override protected float scale(float partial){return 0.1F*initialScale*Math.max(0,Math.min(1,((float)particleAge+partial)/(float)particleMaxAge*32));}
        @Override protected float rotation(float partial){return previousAngle+(angle-previousAngle)*partial;}
        @Override public void onUpdate(){prevPosX=posX;prevPosY=posY;prevPosZ=posZ;if(particleAge++>=particleMaxAge)setDead();previousAngle=angle;angle+=(float)Math.PI*rotationSpeed*2;if(onGround)angle=previousAngle=0;frame=7-particleAge*8/particleMaxAge;moveEntity(motionX,motionY,motionZ);motionY-=(double)0.003F;motionY=Math.max(motionY,(double)-0.14F);}
    }
    public static final class Appearance extends ModernParticleFX {
        private static final ElderGuardianModel MODEL=new ElderGuardianModel();
        private static final ResourceLocation TEXTURE=new ResourceLocation("srparasites","textures/backport/minecraft112/guardian_elder.png");
        private final float tailPhase=rand.nextFloat();
        public Appearance(World w,double x,double y,double z){super(w,x,y,z,0,0,0);motionX=motionY=motionZ=0;particleGravity=0;particleMaxAge=30;}
        @Override public void renderParticle(Tessellator ignored,float partial,float rx,float rz,float ryz,float rxy,float rxz){
            if(particleAge==0)return;net.minecraft.entity.Entity camera=Minecraft.getMinecraft().renderViewEntity;if(camera==null)return;
            int mode=GL11.glGetInteger(GL11.GL_MATRIX_MODE);GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);GL11.glPushClientAttrib(GL11.GL_CLIENT_VERTEX_ARRAY_BIT);GL11.glMatrixMode(GL11.GL_MODELVIEW);GL11.glPushMatrix();
            try{float progress=((float)particleAge+partial)/(float)particleMaxAge;GL11.glDepthMask(true);GL11.glEnable(GL11.GL_BLEND);GL11.glEnable(GL11.GL_DEPTH_TEST);GL11.glDisable(GL11.GL_LIGHTING);GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);GL11.glColor4f(1,1,1,0.05F+0.5F*MathHelper.sin(progress*(float)Math.PI));
                GL13.glMultiTexCoord2f(OpenGlHelper.lightmapTexUnit,240,240);Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
                GL11.glTranslatef(0,1.8F,0);GL11.glRotatef(180-camera.rotationYaw,0,1,0);GL11.glRotatef(60-150*progress-camera.rotationPitch,1,0,0);GL11.glTranslatef(0,-0.4F,-1.5F);GL11.glScalef(0.42553192F,0.42553192F,0.42553192F);
                // Original RenderElderGuardian scale and RenderLivingBase model transforms.
                GL11.glRotatef(180,0,1,0);GL11.glScalef(-1,-1,1);GL11.glScalef(2.35F,2.35F,2.35F);GL11.glTranslatef(0,-1.501F,0);MODEL.renderAppearance(camera,partial,tailPhase);
            }finally{GL11.glPopMatrix();GL11.glMatrixMode(mode);GL11.glPopClientAttrib();GL11.glPopAttrib();}
        }
    }
}
