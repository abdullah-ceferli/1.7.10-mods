package com.dhanantry.scapeandrunparasites.backport.particle.client;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.Minecraft;import net.minecraft.client.particle.EntityFX;import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;import net.minecraft.world.World;import org.lwjgl.opengl.GL11;
/** Native EntityFX implementations of the five newer vanilla effects referenced by SRP. */
@SideOnly(Side.CLIENT)
public abstract class ModernParticleFX extends EntityFX {
    static final ResourceLocation SHEET=new ResourceLocation("srparasites","textures/backport/minecraft112/particles.png");
    static final ResourceLocation BARRIER=new ResourceLocation("srparasites","textures/backport/minecraft112/barrier.png");
    static final ResourceLocation SWEEP=new ResourceLocation("srparasites","textures/backport/minecraft112/sweep.png");
    protected int frame;
    protected ModernParticleFX(World w,double x,double y,double z,double vx,double vy,double vz){super(w,x,y,z,vx,vy,vz);}
    @Override public int getFXLayer(){return 3;}
    @Override public void setParticleTextureIndex(int index){frame=index;}
    protected ResourceLocation texture(){return SHEET;}
    protected float scale(float partial){return 0.1F*particleScale;}
    protected float vertical(){return 1;}
    protected float rotation(float partial){return 0;}
    protected float[] uv(float partial){float u=(frame%16)/16.0F,v=(frame/16)/16.0F;return new float[]{u,u+0.0624375F,v,v+0.0624375F};}
    protected int brightness(float partial){return getBrightnessForRender(partial);}
    @Override public void renderParticle(Tessellator ignored,float partial,float rx,float rz,float ryz,float rxy,float rxz) {
        float[] uv=uv(partial);float size=scale(partial),height=size*vertical();
        float x=(float)(prevPosX+(posX-prevPosX)*partial-interpPosX),y=(float)(prevPosY+(posY-prevPosY)*partial-interpPosY),z=(float)(prevPosZ+(posZ-prevPosZ)*partial-interpPosZ);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);GL11.glPushClientAttrib(GL11.GL_CLIENT_VERTEX_ARRAY_BIT);
        try {
            Minecraft.getMinecraft().getTextureManager().bindTexture(texture());GL11.glDisable(GL11.GL_LIGHTING);GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);GL11.glColor4f(1,1,1,1);
            Tessellator t=Tessellator.instance;t.startDrawingQuads();t.setBrightness(brightness(partial));t.setColorRGBA_F(particleRed,particleGreen,particleBlue,particleAlpha);t.setNormal(0,1,0);
            float angle=rotation(partial);
            if(angle==0) {
            t.addVertexWithUV(x-rx*size-rxy*size,y-rz*height,z-ryz*size-rxz*size,uv[1],uv[3]);
            t.addVertexWithUV(x-rx*size+rxy*size,y+rz*height,z-ryz*size+rxz*size,uv[1],uv[2]);
            t.addVertexWithUV(x+rx*size+rxy*size,y+rz*height,z+ryz*size+rxz*size,uv[0],uv[2]);
            t.addVertexWithUV(x+rx*size-rxy*size,y-rz*height,z+ryz*size-rxz*size,uv[0],uv[3]);            }else{
                float cos=net.minecraft.util.MathHelper.cos(angle),sin=net.minecraft.util.MathHelper.sin(angle);
                int[] sx={-1,-1,1,1},sy={-1,1,1,-1};
                for(int n=0;n<4;n++){float h=sx[n]*cos-sy[n]*sin,v=sy[n]*cos+sx[n]*sin;
                    t.addVertexWithUV(x+rx*size*h+rxy*size*v,y+rz*height*v,z+ryz*size*h+rxz*size*v,n<2?uv[1]:uv[0],n==0||n==3?uv[3]:uv[2]);}
            }
            t.draw();
        }finally{GL11.glPopClientAttrib();GL11.glPopAttrib();}
    }
    public int age(){return particleAge;}public int maximumAge(){return particleMaxAge;}public int textureFrame(){return frame;}
    public static final class Barrier extends ModernParticleFX {
        public Barrier(World w,double x,double y,double z){super(w,x,y,z,0,0,0);particleRed=particleGreen=particleBlue=1;motionX=motionY=motionZ=0;particleGravity=0;particleMaxAge=80;}
        @Override protected ResourceLocation texture(){return BARRIER;}
        @Override protected float scale(float partial){return 0.5F;}
        @Override protected float[] uv(float partial){return new float[]{0,1,0,1};}
    }
    public static final class EndRod extends ModernParticleFX {
        public EndRod(World w,double x,double y,double z,double vx,double vy,double vz){super(w,x,y,z,0,0,0);motionX=vx;motionY=vy;motionZ=vz;particleScale*=0.75F;particleMaxAge=60+rand.nextInt(12);noClip=true;}
        @Override public int getBrightnessForRender(float partial){return 0xF000F0;}
        @Override public void onUpdate(){
            prevPosX=posX;prevPosY=posY;prevPosZ=posZ;if(particleAge++>=particleMaxAge)setDead();
            if(particleAge>particleMaxAge/2){particleAlpha=1-((float)particleAge-(float)(particleMaxAge/2))/(float)particleMaxAge;
                particleRed+=(242/255.0F-particleRed)*0.2F;particleGreen+=(222/255.0F-particleGreen)*0.2F;particleBlue+=(201/255.0F-particleBlue)*0.2F;}
            frame=176+(7-particleAge*8/particleMaxAge);motionY+=(double)(-5.0E-4F);moveEntity(motionX,motionY,motionZ);
            motionX*=(double)0.91F;motionY*=(double)0.91F;motionZ*=(double)0.91F;
            if(onGround){motionX*=(double)0.7F;motionZ*=(double)0.7F;}
        }
    }
    public static final class DragonBreath extends ModernParticleFX {
        private final float initialScale;private boolean touchedGround;
        public DragonBreath(World w,double x,double y,double z,double vx,double vy,double vz){super(w,x,y,z,vx,vy,vz);motionX=vx;motionY=vy;motionZ=vz;
            particleRed=0.7176471F+rand.nextFloat()*(0.8745098F-0.7176471F);particleGreen=0;particleBlue=0.8235294F+rand.nextFloat()*(0.9764706F-0.8235294F);
            particleScale*=0.75F;initialScale=particleScale;particleMaxAge=(int)(20.0/((double)rand.nextFloat()*0.8+0.2));}
        @Override protected float scale(float partial){float growth=((float)particleAge+partial)/(float)particleMaxAge*32;return 0.1F*initialScale*Math.max(0,Math.min(1,growth));}
        @Override public void onUpdate(){
            prevPosX=posX;prevPosY=posY;prevPosZ=posZ;if(particleAge++>=particleMaxAge){setDead();return;}frame=3*particleAge/particleMaxAge+5;
            if(onGround){motionY=0;touchedGround=true;}if(touchedGround)motionY+=0.002;moveEntity(motionX,motionY,motionZ);
            if(posY==prevPosY){motionX*=1.1;motionZ*=1.1;}motionX*=(double)0.96F;motionZ*=(double)0.96F;if(touchedGround)motionY*=(double)0.96F;
        }
    }
    public static final class DamageIndicator extends ModernParticleFX {
        private final float initialScale;
        public DamageIndicator(World w,double x,double y,double z,double vx,double vy,double vz){super(w,x,y,z,0,0,0);
            motionX*=(double)0.1F;motionY*=(double)0.1F;motionZ*=(double)0.1F;motionX+=vx*0.4;motionY+=(vy+1.0)*0.4;motionZ+=vz*0.4;
            particleRed=particleGreen=particleBlue=(float)(Math.random()*(double)0.3F+(double)0.6F);particleScale*=0.75F;initialScale=particleScale;
            particleMaxAge=(int)(6.0/(Math.random()*0.8+0.6));frame=65;onUpdate();particleMaxAge=20;frame=67;}
        @Override protected float scale(float partial){float growth=((float)particleAge+partial)/(float)particleMaxAge*32;return 0.1F*initialScale*Math.max(0,Math.min(1,growth));}
        @Override public void onUpdate(){prevPosX=posX;prevPosY=posY;prevPosZ=posZ;if(particleAge++>=particleMaxAge)setDead();moveEntity(motionX,motionY,motionZ);
            particleGreen=(float)((double)particleGreen*0.96);particleBlue=(float)((double)particleBlue*0.9);motionX*=(double)0.7F;motionY*=(double)0.7F;motionZ*=(double)0.7F;motionY-=(double)0.02F;
            if(onGround){motionX*=(double)0.7F;motionZ*=(double)0.7F;}}
    }
    public static final class Sweep extends ModernParticleFX {
        private final float size;
        public Sweep(World w,double x,double y,double z,double vx){super(w,x,y,z,0,0,0);particleRed=particleGreen=particleBlue=rand.nextFloat()*0.6F+0.4F;size=1-(float)vx*0.5F;particleMaxAge=4;}
        @Override protected ResourceLocation texture(){return SWEEP;}
        @Override protected float scale(float partial){return size;}
        @Override protected float vertical(){return 0.5F;}
        @Override protected int brightness(float partial){return 240<<16;}
        @Override protected float[] uv(float partial){int i=(int)(((float)particleAge+partial)*3/(float)particleMaxAge);float u=(i%4)/4.0F,v=(i/2)/2.0F;return new float[]{u,u+0.24975F,v,v+0.4995F};}
        @Override public void onUpdate(){prevPosX=posX;prevPosY=posY;prevPosZ=posZ;if(++particleAge==particleMaxAge)setDead();}
    }
}
