package com.pockyl.handson_delight.client;

import com.pockyl.handson_delight.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.input.Keyboard;
import vectorwing.farmersdelight.legacy.InventoryTile;

/** Diegesis's range-three kitchen scan, guarded edge dwell, movement keys and original V hints. */
final class Neighbors extends Gui {
    private final StationScreen screen;private final Map<Integer,InventoryTile> found=new HashMap<Integer,InventoryTile>();
    private final Set<Integer> spent=new HashSet<Integer>();private boolean fresh=true;private int edge=-1;private float rested,scan;
    Neighbors(StationScreen screen){this.screen=screen;}
    private static float distance(int edge,float u,float v){return edge==0?u:edge==1?1-u:edge==2?v:1-v;}
    private void scan(){
        Minecraft mc=Minecraft.getMinecraft();found.clear();Map<Integer,Double> nearest=new HashMap<Integer,Double>();double[] eye=screen.scene.world(screen.tile,screen.scene.eye),target=screen.scene.world(screen.tile,screen.scene.target);double fx=target[0]-eye[0],fz=target[2]-eye[2],length=Math.hypot(fx,fz);fx/=length;fz/=length;
        for(int dy=-1;dy<=1;dy++)for(int dx=-3;dx<=3;dx++)for(int dz=-3;dz<=3;dz++){
            if(dx==0&&dy==0&&dz==0)continue;TileEntity raw=mc.theWorld.getTileEntity(screen.tile.xCoord+dx,screen.tile.yCoord+dy,screen.tile.zCoord+dz);if(!StationHooks.supported(raw)||mc.thePlayer.getDistanceSq(raw.xCoord+.5,raw.yCoord+.5,raw.zCoord+.5)>64)continue;
            double across=-fz*dx+fx*dz,along=fx*dx+fz*dz;int edge=dx==0&&dz==0?(dy>0?2:3):Math.abs(across)>=Math.abs(along)?(across>0?1:0):(along>0?2:3);double d=dx*dx+dy*dy+dz*dz;if(!nearest.containsKey(edge)||d<nearest.get(edge)){nearest.put(edge,d);found.put(edge,(InventoryTile)raw);}
        }
    }
    void draw(int mx,int my,float dt,boolean free){
        scan-=dt;if(scan<=0){scan();scan=.25F;}float u=(float)mx/screen.width,v=(float)my/screen.height;
        if(fresh){fresh=false;for(int i=0;i<4;i++)if(distance(i,u,v)<=.03F)spent.add(i);}
        int at=-1;for(int i=0;i<4;i++){float d=distance(i,u,v);if(d>.03F)spent.remove(i);if(i!=3&&found.containsKey(i)&&!spent.contains(i)&&d<=.03F)at=i;if(i!=3&&found.containsKey(i))hint(i,d);}
        if(at<0||!free){edge=-1;rested=0;return;}if(edge!=at){edge=at;rested=0;}rested+=dt;if(rested>=.3F){spent.add(at);rested=0;go(at);}
    }
    private void hint(int edge,float distance){int alpha=Math.round(0xd0*(1-Math.min(1,Math.max(0,(distance-.03F)/.3F))));if(alpha<4)return;int color=alpha<<24|0xffffff;boolean horizontal=edge>=2,far=edge==1||edge==3;int length=horizontal?screen.width:screen.height,across=horizontal?screen.height:screen.width;for(int i=-7;i<=7;i++){int depth=5+Math.abs(i)/2,from=far?across-depth-2:depth,along=length/2+i;if(horizontal)drawRect(along,from,along+1,from+2,color);else drawRect(from,along,from+2,along+1,color);}}
    boolean key(int key){Minecraft mc=Minecraft.getMinecraft();int e=key==Keyboard.KEY_LEFT||key==mc.gameSettings.keyBindLeft.getKeyCode()?0:key==Keyboard.KEY_RIGHT||key==mc.gameSettings.keyBindRight.getKeyCode()?1:key==Keyboard.KEY_UP||key==mc.gameSettings.keyBindForward.getKeyCode()?2:key==Keyboard.KEY_DOWN||key==mc.gameSettings.keyBindBack.getKeyCode()?3:-1;if(e<0)return false;scan();return go(e);}
    private boolean go(int edge){InventoryTile t=found.get(edge);if(t==null)return false;screen.send(Actions.NEIGHBOR,t.xCoord-screen.tile.xCoord,t.zCoord-screen.tile.zCoord,t.yCoord-screen.tile.yCoord,0,"");return true;}
}
