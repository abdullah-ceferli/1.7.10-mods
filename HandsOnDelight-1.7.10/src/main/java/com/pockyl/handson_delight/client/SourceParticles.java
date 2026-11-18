package com.pockyl.handson_delight.client;

import java.util.*;
import java.nio.FloatBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/** Diegesis SceneParticles integration and HandsOnDelightClient's original particle styles. Units are scene pixels. */
public final class SourceParticles {
    public static final int STEAM=0,BUBBLE=1,SPLASH=2,OIL=3,SMOKE=4,HEAT=5,GRAIN=6,GLINT=7,FAT=8,BUBBLE_POP=9;
    private static final class Style {
        String namespace,prefix;int frames,color;float lo,hi,minLife,maxLife,gravity,drag,grow,wobble,fadeIn;boolean fade;
        Style(String ns,String prefix,int frames,int color,float lo,float hi,float minLife,float maxLife,float gravity,float drag,float grow,float wobble,float fadeIn,boolean fade){this.namespace=ns;this.prefix=prefix;this.frames=frames;this.color=color;this.lo=lo;this.hi=hi;this.minLife=minLife;this.maxLife=maxLife;this.gravity=gravity;this.drag=drag;this.grow=grow;this.wobble=wobble;this.fadeIn=fadeIn;this.fade=fade;}
    }
    private static final Style[] STYLES={
        new Style("diegesis","steam_",12,0x50ffffff,.6F,1,1,1.7F,-3,.7F,2,.5F,.45F,true),
        new Style("diegesis","bubble",1,0xddffffff,.55F,.95F,.6F,1.4F,-6,.8F,1,.4F,0,false),
        new Style("diegesis","splash_",4,0xff3f76e4,1.6F,3,.5F,.9F,40,.3F,1,0,0,true),
        new Style("diegesis","bubble_pop_",7,0xe6f7c27a,.8F,1.3F,.25F,.5F,0,1,.4F,0,0,false),
        new Style("diegesis","steam_",12,0x58c8c6c2,1.8F,2.6F,1.6F,2.4F,-4,.75F,3,1,.3F,true),
        new Style("handson_delight","heat_",4,0x48ffffff,3,4,.8F,1.3F,-5,.6F,.4F,.3F,.4F,true),
        new Style("minecraft","generic",8,0xffffffff,.28F,.42F,.35F,.6F,55,.2F,1,0,0,false),
        new Style("minecraft","spark",5,0xffffffff,.4F,.7F,.3F,.7F,0,1,.2F,0,0,true),
        new Style("minecraft","drip",1,0xffffe7a8,.3F,.45F,.3F,.6F,75,.05F,1,0,0,false),
        new Style("diegesis","bubble_pop_",10,0xddffffff,.8F,1.3F,.45F,.85F,0,1,1.3F,0,0,false)
    };
    private static final class Particle {Style style;float x,y,z,vx,vy,vz,age,life,size,seed,depth;int color;boolean surface;}
    private final List<Particle> particles=new ArrayList<Particle>();private final Random random=new Random();
    public int size(){return particles.size();}
    public void spawn(int type,float x,float y,float z,float vx,float vy,float vz,int count,int color){
        Style style=STYLES[type];float jitter=Math.max(.3F,(float)Math.sqrt(vx*vx+vy*vy+vz*vz)*.25F);
        for(int i=0;i<count;i++){if(particles.size()>=400)particles.remove(0);Particle p=new Particle();p.style=style;p.x=x;p.y=y;p.z=z;p.vx=vx+(float)random.nextGaussian()*jitter;p.vy=vy+(float)random.nextGaussian()*jitter;p.vz=vz+(float)random.nextGaussian()*jitter;p.life=style.minLife+random.nextFloat()*(style.maxLife-style.minLife);p.size=style.lo+random.nextFloat()*(style.hi-style.lo);p.color=color==0?style.color:color;p.seed=random.nextFloat()*100;p.surface=type==BUBBLE;particles.add(p);}
    }
    public void update(float dt,float surface,LiquidSim sim){
        List<Particle> popped=new ArrayList<Particle>();
        Iterator<Particle> i=particles.iterator();while(i.hasNext()){Particle p=i.next();p.age+=dt;if(p.age>=p.life||p.surface&&p.y>=surface){if(p.surface&&p.y>=surface){if(sim!=null)sim.impulse((p.x+4)/8,(4-p.z)/8,.09F);popped.add(p);}i.remove();continue;}Style s=p.style;p.vy-=s.gravity*dt;float drag=(float)Math.pow(1-Math.min(.999F,s.drag),dt);p.vx*=drag;p.vy*=drag;p.vz*=drag;if(s.wobble>0){float t=p.age*6+p.seed;p.vx+=(float)Math.sin(t)*s.wobble*dt*6;p.vz+=(float)Math.cos(t*1.3F)*s.wobble*dt*6;}p.x+=p.vx*dt;p.y+=p.vy*dt;p.z+=p.vz*dt;}
        for(Particle p:popped)spawn(BUBBLE_POP,p.x,surface+.01F,p.z,0,0,0,1,p.color);
    }
    void render(float stationYaw,LiquidSim liquid){
        if(particles.isEmpty())return;Minecraft mc=Minecraft.getMinecraft();GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS&~GL11.GL_TEXTURE_BIT);GL11.glEnable(GL11.GL_TEXTURE_2D);GL11.glEnable(GL11.GL_BLEND);GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);GL11.glDisable(GL11.GL_LIGHTING);GL11.glDisable(GL11.GL_CULL_FACE);GL11.glDepthMask(false);GL11.glAlphaFunc(GL11.GL_GREATER,.003F);
        FloatBuffer m=BufferUtils.createFloatBuffer(16);GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX,m);for(Particle p:particles)p.depth=m.get(2)*p.x+m.get(6)*p.y+m.get(10)*p.z+m.get(14);Collections.sort(particles,new Comparator<Particle>(){public int compare(Particle a,Particle b){return Float.compare(a.depth,b.depth);}});
        for(Particle p:particles){Style s=p.style;float t=p.age/p.life,size=p.size*(1+t*(s.grow-1))/2,alpha=(p.color>>>24)/255F*(s.fade?1-t*t:1);if(s.fadeIn>0)alpha*=Math.min(1,t/s.fadeIn);if(p.surface&&liquid!=null)alpha*=(float)Math.exp(Math.log(Math.max(1-(liquid.color()>>>24)/255F,.02F))*.4F*Math.max(0,2+6.5F*liquid.fraction()-p.y));
            int frame=Math.min(s.frames-1,(int)(t*s.frames));float u=0,v=0,U=1,V=1;
            if(s.namespace.equals("minecraft")){mc.getTextureManager().bindTexture(new ResourceLocation("minecraft:textures/particle/particles.png"));int index=s.prefix.equals("spark")?160+4-frame:s.prefix.equals("drip")?113:7-frame;u=(index%16)/16F;v=(index/16)/16F;U=u+1/16F;V=v+1/16F;}
            else {int swell=s.frames==10?5:2;String frameName=s.prefix.equals("bubble_pop_")?(frame<swell?"bubble":"bubble_pop_"+(frame-swell)):s.prefix+(s.frames==1?"":frame);mc.getTextureManager().bindTexture(new ResourceLocation(s.namespace,"textures/particle/"+frameName+".png"));}
            GL11.glPushMatrix();GL11.glTranslatef(p.x,p.y,p.z);GL11.glRotatef(180-RenderManager.instance.playerViewY+stationYaw,0,1,0);GL11.glRotatef(-RenderManager.instance.playerViewX,1,0,0);Tessellator q=Tessellator.instance;q.startDrawingQuads();q.setColorRGBA_F((p.color>>16&255)/255F,(p.color>>8&255)/255F,(p.color&255)/255F,alpha);q.addVertexWithUV(-size,-size,0,u,V);q.addVertexWithUV(size,-size,0,U,V);q.addVertexWithUV(size,size,0,U,v);q.addVertexWithUV(-size,size,0,u,v);q.draw();GL11.glPopMatrix();
        }
        GL11.glPopAttrib();
    }
}
