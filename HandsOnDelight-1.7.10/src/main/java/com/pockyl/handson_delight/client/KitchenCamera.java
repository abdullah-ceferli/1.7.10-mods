package com.pockyl.handson_delight.client;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.EntityLivingBase;

/** Diegesis FocusController's flight easing, arc, timing and returning player view. */
public final class KitchenCamera {
    private static EntityLivingBase home,lens;private static int view;private static float fov;
    private static Shot from,to;private static long began;private static float seconds;private static boolean leaving;
    private static StationScreen owner;
    private static final class Shot {
        double x,y,z;float yaw,pitch,fov;
        Shot(double x,double y,double z,float yaw,float pitch,float fov){this.x=x;this.y=y;this.z=z;this.yaw=yaw;this.pitch=pitch;this.fov=fov;}
    }
    private static Shot at(EntityLivingBase e,float fov){return new Shot(e.posX,e.posY+e.getEyeHeight(),e.posZ,e.rotationYaw,e.rotationPitch,fov);}
    public static boolean active(){return lens!=null;}
    public static float arrived(){return leaving?0:Math.min(1,(System.nanoTime()-began)/1e9F/Math.max(.001F,seconds));}
    public static void begin(StationScreen screen){
        Minecraft mc=Minecraft.getMinecraft();boolean onward=lens!=null;
        if(!onward){home=mc.renderViewEntity==null?mc.thePlayer:mc.renderViewEntity;view=mc.gameSettings.thirdPersonView;fov=mc.gameSettings.fovSetting;lens=new EntityOtherPlayerMP(mc.theWorld,new GameProfile(UUID.fromString("00000000-0000-0000-0000-00000000c00c"),"KitchenCamera"));from=at(home,fov);}else from=at(lens,mc.gameSettings.fovSetting);
        double[] eye=screen.scene.world(screen.tile,screen.scene.eye),target=screen.scene.world(screen.tile,screen.scene.target),d=SourceScene.sub(target,eye);
        to=new Shot(eye[0],eye[1],eye[2],(float)Math.toDegrees(Math.atan2(-d[0],d[2])),(float)-Math.toDegrees(Math.atan2(d[1],Math.hypot(d[0],d[2]))),screen.scene.fov);
        seconds=onward?.45F:.4F;began=System.nanoTime();owner=screen;leaving=false;mc.gameSettings.thirdPersonView=0;apply(from);
    }
    public static void end(StationScreen screen,boolean animate){if(owner!=screen||lens==null)return;if(!animate){restore();return;}Minecraft mc=Minecraft.getMinecraft();from=at(lens,mc.gameSettings.fovSetting);to=at(home,fov);owner=null;leaving=true;seconds=.4F;began=System.nanoTime();}
    public static void frame(){
        if(lens==null)return;Minecraft mc=Minecraft.getMinecraft();if(mc.theWorld==null||mc.thePlayer==null||lens.worldObj!=mc.theWorld){restore();return;}
        if(leaving)to=at(home,fov);float t=Math.min(1,(System.nanoTime()-began)/1e9F/Math.max(.001F,seconds)),k=Easing.flight(t);
        double distance=Math.sqrt(Math.pow(to.x-from.x,2)+Math.pow(to.y-from.y,2)+Math.pow(to.z-from.z,2));double arc=Math.max(.12,distance*.1)*Math.sin(Math.PI*k);
        float yaw=from.yaw+com.pockyl.handson_delight.NativeMath.wrapDegrees(to.yaw-from.yaw)*k;
        apply(new Shot(from.x+(to.x-from.x)*k,from.y+(to.y-from.y)*k+arc,from.z+(to.z-from.z)*k,yaw,from.pitch+(to.pitch-from.pitch)*k,from.fov+(to.fov-from.fov)*k));
        if(t>=1&&leaving)restore();
    }
    private static void apply(Shot s){Minecraft mc=Minecraft.getMinecraft();lens.setPosition(s.x,s.y-lens.getEyeHeight(),s.z);lens.prevPosX=lens.lastTickPosX=lens.posX;lens.prevPosY=lens.lastTickPosY=lens.posY;lens.prevPosZ=lens.lastTickPosZ=lens.posZ;lens.rotationYaw=lens.prevRotationYaw=s.yaw;lens.rotationPitch=lens.prevRotationPitch=s.pitch;mc.renderViewEntity=lens;mc.gameSettings.fovSetting=s.fov;}
    public static void restore(){Minecraft mc=Minecraft.getMinecraft();if(lens!=null){if(mc.renderViewEntity==lens)mc.renderViewEntity=home==null?mc.thePlayer:home;mc.gameSettings.thirdPersonView=view;mc.gameSettings.fovSetting=fov;}owner=null;lens=null;home=null;leaving=false;}
}
