package com.pockyl.handson_delight.client;

import java.util.*;
import com.pockyl.handson_delight.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.GL11;
import vectorwing.farmersdelight.legacy.*;

/** Native poses for BoardBehavior's toss, roll, skewer, knead, arrange, sandwich, shape, freeze and press finishes. */
final class AssemblyAnimation {
    private enum Finish {TOSS,ROLL,SKEWER,KNEAD,ARRANGE,SANDWICH,SHAPE,FREEZE,PRESS}
    private static Finish finish(List<ItemStack> layers,ItemStack result){
        String out=id(result),base=layers.isEmpty()?"":id(layers.get(0));if(out.endsWith("dough"))return Finish.KNEAD;
        if(base.contains("bowl"))return out.contains("salad")?Finish.TOSS:Finish.ARRANGE;
        if(base.contains("stick")||base.contains("blaze_rod"))return out.contains("popsicle")?Finish.FREEZE:Finish.SKEWER;
        if(base.contains("bread")||base.contains("burger"))return out.contains("wrap")||out.contains("burrito")?Finish.ROLL:Finish.SANDWICH;
        if(base.contains("tortilla")||base.contains("flatbread")||base.contains("kelp"))return Finish.ROLL;
        if(base.contains("rice"))return Finish.SHAPE;if(base.contains("wheat")||base.contains("dough")||base.contains("flour"))return Finish.KNEAD;return Finish.PRESS;
    }
    private static String id(ItemStack s){return Stacks.empty(s)?"":Item.itemRegistry.getNameForObject(s.getItem());}
    static boolean draw(StationVisuals v,StationState state){
        if(v.assembly.isEmpty()||Stacks.empty(state.visualResult))return false;Finish finish=finish(v.assembly,state.visualResult);int count=v.assembly.size();float duration=finish==Finish.TOSS?.54F:finish==Finish.ROLL?.68F:finish==Finish.SKEWER?.34F+(count-1)*.1F:finish==Finish.KNEAD?.82F:finish==Finish.ARRANGE?.24F+Math.max(0,count-2)*.08F:finish==Finish.SANDWICH?.6F:finish==Finish.SHAPE?.14F+Math.max(1,count-1)*.12F+.18F:finish==Finish.FREEZE?.8F:.3F;
        float age=v.assembleAge;if(age>duration+.28F+.35F)return false;
        if(age<duration+.28F){float melting=NativeMath.clamp((age-duration)/.28F,0,1);for(int i=0;i<count;i++){
            float x=0,z=0,y=i*.65F,rx=0,ry=((i*7919)%41-20)*1.2F,rz=0,scale=1;
            float t=NativeMath.clamp(age/duration,0,1),sin=(float)Math.sin(t*Math.PI);
            switch(finish){
                case TOSS:if(i>0){y+=(4+(i*37%5)*.6F)*(float)Math.sin(Math.PI*NativeMath.clamp(age/.42F,0,1));rx=(i%2==0?1:-1)*40*sin;ry+=120*sin;rz=-rx*.6F;}else rx=-12*(float)Math.sin(Math.min(1,age/.16F)*Math.PI);break;
                case ROLL:float stroke=NativeMath.clamp((age-.12F)/.14F,0,4),wave=(float)Math.sin(stroke*Math.PI)*(1-Math.min(1,stroke/4));z=1.6F*wave;rx=25*wave;y*=.6F;break;
                case SKEWER:if(i>0){float share=count<=2?.5F:.82F-(.82F-.25F)*(i-1)/(count-2F),slide=Easing.outCubic(NativeMath.clamp((age-(i-1)*.1F)/.18F,0,1));z=(1-slide)*5+slide*(share-.5F)*8;y=.65F;rx=70*slide;scale=.65F;}break;
                case KNEAD:float push=(float)Math.sin(Math.max(0,age-.12F)/.14F*Math.PI);scale=1+.12F*Math.max(0,push);y*=.6F*(1-.35F*Math.max(0,push));ry+=Math.max(0,age-.12F)*50;break;
                case ARRANGE:if(i>1){float a=(float)Math.PI*2*(i-2)/Math.max(1,count-2)+.6F,f=Easing.outCubic(NativeMath.clamp((age-Math.max(0,i-2)*.08F)/.24F,0,1));x=(float)Math.cos(a)*2.1F*f;z=(float)Math.sin(a)*2.1F*f;scale=1-.35F*f;y=.65F;}break;
                case SANDWICH:if(i==count-1){y+=3*(float)Math.sin(t*Math.PI);rx=-25*sin;}else y*=1-.2F*t;break;
                case SHAPE:scale=i==0?.8F:1;z=(i-(count-1)/2F-.5F)*.8F*Easing.outCubic(t);y=i==0?0:.65F*.8F+(i-1)*.15F;ry=45;break;
                case FREEZE:if(i>0){x*=1-t;y=.65F+(i-1)*.15F;scale=1-.4F*t;}break;
                default:y*=1-.45F*sin;scale=1+.08F*sin;break;
            }
            scale*=1-melting;if(scale<=.001F)continue;GL11.glPushMatrix();GL11.glTranslatef(x,y,z);GL11.glRotatef(ry,0,1,0);GL11.glRotatef(rx,1,0,0);GL11.glRotatef(rz,0,0,1);GL11.glScalef(scale,scale,scale);ItemVoxels.draw(v.assembly.get(i),i==0?10:9);GL11.glPopMatrix();
        }}else{float pop=NativeMath.clamp((age-duration-.28F)/.35F,0,1);GL11.glPushMatrix();GL11.glTranslatef(0,1.6F*(float)Math.sin(pop*Math.PI),0);ItemVoxels.draw(state.visualResult,10);GL11.glPopMatrix();}
        return true;
    }
}
