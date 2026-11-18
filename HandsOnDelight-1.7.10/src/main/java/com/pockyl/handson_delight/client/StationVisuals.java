package com.pockyl.handson_delight.client;

import com.pockyl.handson_delight.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.item.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Quaternionf;
import vectorwing.farmersdelight.legacy.*;

/** Per-viewer state for the original station motions; gameplay remains server authoritative. */
public final class StationVisuals {
    private static final Map<InventoryTile,StationVisuals> ALL=new WeakHashMap<InventoryTile,StationVisuals>();
    private static Object world;
    public final LiquidSim liquid=new LiquidSim();public final SourceParticles particles=new SourceParticles();
    public float time,dt,tipX,tipZ,grip,swirl,turned,knifeAge=Float.NaN;
    public final float[] itemX=new float[6],itemZ=new float[6],tumble=new float[6],land=new float[9];
    public final ItemStack[] shown=new ItemStack[9];
    public ItemStack cutItem,cutTool,ghost;public float ghostAge,assembleAge=100;public final List<ItemStack> assembly=new ArrayList<ItemStack>();
    public final Matrix4f spoonPose=new Matrix4f();
    private long frame=System.nanoTime();private int serial,meal;private boolean primed;private float homing,sign=1,boilSound,stirSound,steamDebt,bubbleDebt,heatDebt,meltAt=-100;
    private final ItemStack[] melting=new ItemStack[6];private final Random random=new Random();private float lastTipX,lastTipZ;
    private StationVisuals(){Arrays.fill(land,-100);}
    public static StationVisuals of(InventoryTile tile){if(world!=tile.getWorldObj()){ALL.clear();world=tile.getWorldObj();}StationVisuals s=ALL.get(tile);if(s==null){if(ALL.size()>256)ALL.clear();s=new StationVisuals();ALL.put(tile,s);}return s;}
    public static int active(){return ALL.size();}
    public float level(){return 2+6.5F*liquid.fraction();}
    public float landLift(int i){float age=time-land[i];if(age<0||age>=.2F)return 0;float t=age/.2F;return 4*(1-t)*(1-t)+.8F*(float)Math.sin(t*Math.PI);}
    public float meltScale(int i){float age=time-meltAt-i*.07F;if(age<0)return 1;if(age<.16F)return 1+.4F*age/.16F;return Math.max(0,1.4F*(1-(age-.16F)/(.55F-.16F)));}
    public ItemStack melted(int i){return time-meltAt-i*.07F<.55F?melting[i]:null;}
    public void frame(InventoryTile tile,StationScreen focused,float partial){
        long now=System.nanoTime();dt=Math.max(0,Math.min(.05F,(now-frame)/1e9F));frame=now;time+=dt;StationState state=StationHooks.state(tile);
        if(primed&&serial!=state.visualSerial&&tile.getWorldObj().getTotalWorldTime()+partial-state.visualAt<40){
            if(state.visualKind==Actions.CUT){cutItem=copy(state.visualItem);cutTool=copy(state.visualTool);knifeAge=0;particles.spawn(SourceParticles.GRAIN,0,2,0,0,3,0,5,0xff000000|color(state.visualItem));}
            if(state.visualKind==Actions.TAKE||state.visualKind==Actions.SERVE){ghost=copy(state.visualItem);ghostAge=0;if(tile instanceof CookingPotTile){sound(tile,"handson_delight:pot.scoop",.7F,1);liquid.impulse(.5F,.5F,.35F);}else if(tile instanceof CuttingBoardTile)sound(tile,"step.wood",.4F,1);}
            if(state.visualKind==Actions.INSERT){if(tile instanceof CookingPotTile){liquid.impulse(.5F,.5F,.35F);particles.spawn(SourceParticles.SPLASH,0,level(),0,0,5,0,5,0xff000000|liquid.color()&0xffffff);sound(tile,"handson_delight:pot.plop",.5F,1);}else sound(tile,tile instanceof GrillingTile?"farmersdelight:block.skillet.add_food":"dig.wood",.4F,1);}
            if(state.visualKind==Actions.ASSEMBLE){assembleAge=0;assembly.clear();for(ItemStack a:state.visualLayers)assembly.add(copy(a));particles.spawn(SourceParticles.GLINT,0,3,0,0,4,0,4,0xffffe9a0);}
        }
        serial=state.visualSerial;if(!Float.isNaN(knifeAge)){knifeAge+=dt;if(knifeAge>1)knifeAge=Float.NaN;}ghostAge+=dt;assembleAge+=dt;
        if(tile instanceof CookingPotTile){
            CookingPotTile pot=(CookingPotTile)tile;int stacks=0;ItemStack[] inputs=new ItemStack[6];List<ItemStack> food=new ArrayList<ItemStack>();
            for(int i=0;i<6;i++){inputs[i]=pot.getStackInSlot(i);if(!Stacks.empty(inputs[i])){stacks++;food.add(inputs[i]);}}
            int servings=Stacks.size(pot.getStackInSlot(6));if(primed&&servings>meal){for(int i=0;i<6;i++)melting[i]=copy(shown[i]);meltAt=time;particles.spawn(SourceParticles.STEAM,0,level(),0,0,4,0,4,0);particles.spawn(SourceParticles.GLINT,0,level(),0,0,4,0,3,0xffffe9a0);sound(tile,"handson_delight:pot.burst",.55F,1);}
            meal=servings;CookingRecipes.Recipe recipe=CookingRecipes.find(inputs);ItemStack preview=servings>0?pot.getStackInSlot(6):recipe==null?null:recipe.output;
            int water=0xa0000000|tile.getWorldObj().getBiomeGenForCoords(tile.xCoord,tile.zCoord).getWaterColorMultiplier();float cooked=servings>0?1:.3F+.7F*pot.progress/Math.max(1,pot.total);int broth=Stacks.empty(preview)?FoodColors.mix(food):FoodColors.broth(tile.getWorldObj(),preview);
            liquid.setColorOverride(food.isEmpty()&&servings==0?water:blend(water,0xf0000000|broth,cooked));liquid.setMottle(food.isEmpty()&&servings==0?0:cooked);liquid.setWakeLook(1.2F+(9-1.2F)*cooked,.25F+.75F*cooked);
            float wanted=(58+6*stacks)/100F;if(servings>0)wanted=Math.max(wanted,Math.min(1,(74+5*servings)/100F));boolean hot=Heat.heated(tile.getWorldObj(),tile.xCoord,tile.yCoord,tile.zCoord,false);
            boolean hand=focused!=null&&focused.spoonHeld(),stirred=tile.getWorldObj().getTotalWorldTime()-state.stirred<40;float wantX=0,wantZ=0;
            if(hand){double[] p=focused.cursor(level());wantX=(float)p[0]-8;wantZ=(float)p[2]-8;float distance=(float)Math.hypot(wantX,wantZ);if(distance>2.3F){wantX*=2.3F/distance;wantZ*=2.3F/distance;}}
            else if(stirred){wantX=state.spoonX*2.3F;wantZ=state.spoonZ*2.3F;}
            boolean goingHome=!hand&&!stirred;homing=goingHome?Math.min(1,homing+dt/.6F):0;grip=NativeMath.lerp(Math.min(1,dt*(goingHome?2*homing:7)),grip,goingHome?0:1);
            float follow=1-(float)Math.exp(-dt*(hand?18:goingHome?1.8F*homing:9));lastTipX=tipX;lastTipZ=tipZ;tipX+=(wantX-tipX)*follow;tipZ+=(wantZ-tipZ)*follow;float dx=tipX-lastTipX,dz=tipZ-lastTipZ,moved=(float)Math.hypot(dx,dz),cross=lastTipX*dz-lastTipZ*dx;
            if(grip>.5F&&moved>.02F&&Math.abs(cross)>.001F){sign=NativeMath.lerp(Math.min(1,dt*1.5F),sign,Math.signum(cross));float wantedSwirl=Math.min(1,moved/Math.max(.001F,dt)/7);if(wantedSwirl>swirl)swirl=NativeMath.lerp(Math.min(1,dt*2.5F),swirl,wantedSwirl);liquid.leaveWake(.5F+tipX/8,.5F-tipZ/8,.07F,swirl);liquid.impulse(.5F+tipX/8,.5F-tipZ/8,.17F);}
            swirl*=(float)Math.pow(.5F,dt/.9F);float current=(.05F+swirl*1.5F)*sign;turned+=dt*current;liquid.setCurrent(current);liquid.setEdgeBoil(hot?1:0);liquid.update(dt,Math.min(1,wanted),hot?.45F:0);
            float rest=(float)Math.tan(Math.toRadians(22.5)),held=(float)Math.tan(Math.toRadians(7))/2.3F;Vector3f from=new Vector3f(rest,1,0).normalize(),to=new Vector3f(NativeMath.lerp(grip,rest,tipX*held),1,NativeMath.lerp(grip,0,tipZ*held)).normalize();Quaternionf q=new Quaternionf().rotationTo(from,to);Vector3f end=q.transform(new Vector3f(0,3,0));spoonPose.identity().translation(tipX-end.x,3-end.y,tipZ-end.z).rotate(q);
            int rank=0;for(int i=0;i<6;i++)if(!Stacks.empty(inputs[i])){float angle=turned+rank++*(float)Math.PI*2/Math.max(1,stacks),x=(float)Math.cos(angle)*2.2F,z=(float)Math.sin(angle)*2.2F,d=(float)Math.hypot(x-tipX,z-tipZ);if(d<2.1F){float away=2.1F/Math.max(.01F,d);x=tipX+(x-tipX)*away;z=tipZ+(z-tipZ)*away;}itemX[i]=NativeMath.clamp(x,-2.9F,2.9F);itemZ[i]=NativeMath.clamp(z,-2.9F,2.9F);if(tumble[i]>0){tumble[i]+=dt/.7F;if(tumble[i]>=1)tumble[i]=0;}else if(swirl>.3F&&random.nextFloat()<dt*.35F*swirl)tumble[i]=.001F;}
            if(hot){steamDebt+=dt*1.6F;bubbleDebt+=dt*6;while(steamDebt>=1){steamDebt--;particles.spawn(SourceParticles.STEAM,range(3),level(),range(3),0,1.4F,0,1,0);}while(bubbleDebt>=1){bubbleDebt--;particles.spawn(SourceParticles.BUBBLE,range(3),2,range(3),0,3,0,1,0);}boilSound-=dt;if(boilSound<=0){sound(tile,servings>0?"farmersdelight:block.cooking_pot.boil_soup":"farmersdelight:block.cooking_pot.boil",.4F,1);boilSound=recipe==null?2.6F:1.7F;}}
            stirSound-=dt;if(hand&&moved>.01F&&stirSound<=0){sound(tile,"handson_delight:pot.stir",.7F,1);stirSound=.35F;}
        }else if(tile instanceof GrillingTile){GrillingTile grill=(GrillingTile)tile;boolean hot=grill.heated()&&!grill.blocked();if(hot){heatDebt+=dt*1.2F;steamDebt+=dt*1.6F;while(heatDebt>=1){heatDebt--;particles.spawn(SourceParticles.HEAT,range(5),tile instanceof StoveTile?16:1.05F,range(5),0,2,0,1,0);}while(steamDebt>=1){steamDebt--;for(int i=0;i<grill.getSizeInventory();i++)if(!Stacks.empty(grill.getStackInSlot(i))){boolean waits=state.frying[i].waits();particles.spawn(waits?SourceParticles.SMOKE:SourceParticles.STEAM,tile instanceof StoveTile?4.8F-i%3*4.8F:0,tile instanceof StoveTile?16:1.05F,tile instanceof StoveTile?(i<3?3.2F:-3.2F):0,0,1.4F,0,1,0);if(waits)particles.spawn(SourceParticles.OIL,range(3),tile instanceof StoveTile?16:1.05F,range(3),0,0,0,2,0);}}boilSound-=dt;if(boilSound<=0){sound(tile,"farmersdelight:block.skillet.sizzle",.4F,1);boilSound=1;}}}
        particles.update(dt,level(),tile instanceof CookingPotTile?liquid:null);
        for(int i=0;i<tile.getSizeInventory();i++){ItemStack next=tile.getStackInSlot(i);if(primed&&!Stacks.empty(next)&&(Stacks.empty(shown[i])||!Stacks.same(next,shown[i])||next.stackSize>shown[i].stackSize))land[i]=time;shown[i]=copy(next);}primed=true;
    }
    private static ItemStack copy(ItemStack s){return Stacks.empty(s)?null:s.copy();}
    private float range(float extent){return (random.nextFloat()*2-1)*extent;}
    private static int color(ItemStack s){return Stacks.empty(s)?0xb0a070:FoodColors.of(s);}
    private static int blend(int a,int b,float t){int out=0;for(int i=0;i<32;i+=8)out|=Math.round((a>>>i&255)+t*((b>>>i&255)-(a>>>i&255)))<<i;return out;}
    private void sound(InventoryTile tile,String name,float volume,float pitch){tile.getWorldObj().playSoundEffect(tile.xCoord+.5,tile.yCoord+.5,tile.zCoord+.5,name,volume,pitch);}
}
