package com.pockyl.handson_delight;

import java.lang.reflect.*;
import java.util.*;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vectorwing.farmersdelight.legacy.*;
import com.pockyl.handson_delight.station.Frying;

public final class StationHooks {
    private static final Map<Object,StationState> STATES=Collections.synchronizedMap(new WeakHashMap<Object,StationState>());
    public static StationState state(Object tile){synchronized(STATES){StationState s=STATES.get(tile);if(s==null){s=new StationState();STATES.put(tile,s);}return s;}}
    public static void read(Object tile,NBTTagCompound nbt){if(supported(tile))state(tile).load(nbt.getCompoundTag("handson_delight"));}
    public static void write(Object tile,NBTTagCompound nbt){if(supported(tile))nbt.setTag("handson_delight",state(tile).save());}
    public static boolean supported(Object tile){return tile instanceof CookingPotTile||tile instanceof GrillingTile||tile instanceof CuttingBoardTile;}
    private static ItemStack[] inputs(InventoryTile t){ItemStack[] a=new ItemStack[6];for(int i=0;i<6;i++)a[i]=t.getStackInSlot(i);return a;}
    public static CookingRecipes.Recipe recipe(CookingPotTile t){return CookingRecipes.find(inputs(t));}
    public static int batch(CookingPotTile t,CookingRecipes.Recipe r){
        if(r==null)return 0;int n=64;
        for(int i=0;i<6;i++)if(!Stacks.empty(t.getStackInSlot(i)))n=Math.min(n,t.getStackInSlot(i).stackSize);
        ItemStack meal=t.getStackInSlot(6);
        if(!Stacks.empty(meal)&&!Stacks.same(meal,r.output))return 0;
        return Math.max(0,Math.min(n,(Math.min(64,r.output.getMaxStackSize())-Stacks.size(meal))/r.output.stackSize));
    }
    public static float pace(int batch){return 1.5F/(float)Math.sqrt(Math.max(1,Math.min(16,batch)));}
    public static boolean pot(Object raw){
        CookingPotTile t=(CookingPotTile)raw;World w=t.getWorldObj();
        if(w==null||w.isRemote)return false;StationState s=state(t);long now=w.getTotalWorldTime();
        if(!s.manual||now-s.attended>40){if(s.pace!=1){t.progress=Math.min(t.total-1,Math.round(t.progress/s.pace));s.work=t.progress;s.pace=1;}return false;}
        CookingRecipes.Recipe r=recipe(t);String id=r==null?"":r.id;
        if(!id.equals(s.recipe)){s.recipe=id;s.work=0;t.progress=0;s.pace=1;}
        if(r!=null){float next=pace(batch(t,r));if(s.pace!=next){s.work*=next/s.pace;s.pace=next;t.progress=(int)Math.min(r.time-1,s.work);}}
        t.heated=Heat.heated(w,t.xCoord,t.yCoord,t.zCoord,false);
        PotAccess.prepare(t,r,id);PotAccess.call(PotAccess.serve,t);
        if(r==null){s.work=0;t.progress=0;}
        else if(!t.heated){s.work=Math.max(0,s.work-2);t.progress=(int)s.work;}
        else if(now-s.stirred<=40&&batch(t,r)>0&&PotAccess.canCook(t,r)){
            int n=batch(t,r);s.work+=pace(n);t.progress=(int)Math.min(r.time-1,s.work);
            if(s.work>=r.time){
                EntityPlayer player=s.cook==null?null:w.func_152378_a(s.cook);
                int completed=0;
                for(int i=0;i<n;i++){
                    if(!r.matches(inputs(t))||!PotAccess.canCook(t,r))break;
                    PotAccess.call(PotAccess.cook,t);completed++;
                }
                int grade=s.skill.grade(now);s.bonus+=completed*(grade>=3?0.5:grade>=2?0.25:0);
                int bonus=(int)s.bonus;s.bonus-=bonus;
                ItemStack meal=t.getStackInSlot(6);
                if(meal!=null){int add=Math.min(bonus*r.output.stackSize,Math.min(64,meal.getMaxStackSize())-meal.stackSize);meal.stackSize+=add;}
                if(player!=null)record(player,r.output,completed*r.output.stackSize);
                s.work=0;t.progress=0;t.changed();
            }
        }
        s.stars=s.skill.stars(now);t.markDirty();
        if(now%5==0)t.changed();
        return true;
    }
    public static int openStars(EntityPlayer p,ItemStack dish){int made=history(p).getInteger(key(dish));return made>=32?3:made>=8?2:1;}
    public static String key(ItemStack s){return net.minecraft.item.Item.itemRegistry.getNameForObject(s.getItem())+"/"+s.getItemDamage();}
    public static void record(EntityPlayer p,ItemStack dish,int count){NBTTagCompound h=history(p);String key=key(dish);h.setInteger(key,(int)Math.min(Integer.MAX_VALUE,(long)h.getInteger(key)+count));syncPractice(p);}
    public static void syncPractice(EntityPlayer p){if(p instanceof net.minecraft.entity.player.EntityPlayerMP&&!(p instanceof net.minecraftforge.common.util.FakePlayer))HandsOnDelight.NETWORK.sendTo(new Packets.Practice(history(p)),(net.minecraft.entity.player.EntityPlayerMP)p);}
    private static NBTTagCompound history(EntityPlayer p){
        NBTTagCompound root=p.getEntityData();String name=EntityPlayer.PERSISTED_NBT_TAG;
        if(!root.hasKey(name,10))root.setTag(name,new NBTTagCompound());NBTTagCompound persisted=root.getCompoundTag(name);
        if(!persisted.hasKey("handson_delight",10))persisted.setTag("handson_delight",new NBTTagCompound());return persisted.getCompoundTag("handson_delight");
    }
    public static void stir(CookingPotTile t,EntityPlayer p,float x,float z,float clock){
        if(!Float.isFinite(x)||!Float.isFinite(z)||!Float.isFinite(clock))return;
        StationState s=state(t);long now=t.getWorldObj().getTotalWorldTime();
        s.manual=true;s.attended=now;s.stirred=now;s.cook=p.getUniqueID();s.spoonX=NativeMath.clamp(x,-1,1);s.spoonZ=NativeMath.clamp(z,-1,1);
        CookingRecipes.Recipe r=recipe(t);s.skill.move(now,clock,s.spoonX,s.spoonZ,t.heated&&r!=null,r==null?1:openStars(p,r.output));s.stars=s.skill.stars(now);
    }
    public static boolean grill(Object raw){
        GrillingTile t=(GrillingTile)raw;World w=t.getWorldObj();if(w==null||w.isRemote)return false;
        StationState s=state(t);boolean any=false;for(int i=0;i<t.getSizeInventory();i++)if(s.frying[i].byHand()||s.frying[i].ready())any=true;
        if(!any)return false;
        if(t.blocked()){
            for(int i=0;i<t.getSizeInventory();i++){ItemStack a=t.getStackInSlot(i);t.setInventorySlotContents(i,null);t.drop(a);s.frying[i].farmersDelightsWay();s.observed[i]=null;t.progress[i]=0;}t.changed();return true;
        }
        boolean hot=t.heated(),changed=false;
        for(int i=0;i<t.getSizeInventory();i++){
            ItemStack input=t.getStackInSlot(i);Frying f=s.frying[i];
            if(Stacks.empty(input)){f.farmersDelightsWay();s.observed[i]=null;t.progress[i]=0;continue;}
            if(s.observed[i]!=null&&!Stacks.same(s.observed[i],input)){f.farmersDelightsWay();s.observed[i]=null;t.progress[i]=0;}
            if(f.ready())continue;
            GrillingRecipes.Recipe r=GrillingRecipes.find(input);if(r==null)continue;
            if(!f.byHand()){
                if(!hot){t.progress[i]=Math.max(0,t.progress[i]-2);continue;}
                if(++t.progress[i]>=ticks(t,r)){ItemStack remainder=CookingRecipes.remainder(Stacks.copy(input,1));t.decrStackSize(i,1);t.drop(r.output.copy());t.drop(remainder);t.progress[i]=0;changed=true;}continue;
            }
            f.batch(Math.min(16,input.stackSize));if(!hot)continue;
            int side=Math.max(1,Math.round(ticks(t,r)*(float)Math.sqrt(f.batch())/1.5F/2));
            if(input.getItem()==Items.egg)f.fryThrough(0.5F/side);else f.fry(1F/side);
            if(f.down()>=1&&f.up()>=1){
                int n=f.batch(),count=n*r.output.stackSize;ItemStack result=Stacks.copy(r.output,Math.min(count,r.output.getMaxStackSize()));
                // Remove the batch before emitting anything. Containers retain the native recipe rules.
                ItemStack consumed=Stacks.copy(input,1);t.decrStackSize(i,n);
                for(int j=0;j<n;j++)t.drop(CookingRecipes.remainder(consumed));
                if(Stacks.empty(t.getStackInSlot(i))&&count<=Math.min(t.getInventoryStackLimit(),result.getMaxStackSize())){t.setInventorySlotContents(i,result);f.serve();s.observed[i]=Stacks.copy(result,1);}
                else {while(count>0){int take=Math.min(count,r.output.getMaxStackSize());t.drop(Stacks.copy(r.output,take));count-=take;}f.farmersDelightsWay();s.observed[i]=null;}
                t.progress[i]=0;changed=true;
            }
            if(f.dueToSend())changed=true;
        }
        t.markDirty();if(changed)t.changed();return true;
    }
    private static int ticks(GrillingTile t,GrillingRecipes.Recipe r){
        int fire=0;if(t instanceof SkilletTile)fire=net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.enchantment.Enchantment.fireAspect.effectId,((SkilletTile)t).identity());
        return t instanceof StoveTile?r.time:GrillingRecipes.skilletTime(r.time,fire);
    }
    public static boolean turn(GrillingTile t,int slot){
        if(slot<0||slot>=t.getSizeInventory()||Stacks.empty(t.getStackInSlot(slot)))return false;StationState s=state(t);Frying f=s.frying[slot];long now=t.getWorldObj().getTotalWorldTime();
        if(f.ready()||now-f.turnedAt()<8||GrillingRecipes.find(t.getStackInSlot(slot))==null)return false;
        f.byHand(null);f.batch(Math.min(16,t.getStackInSlot(slot).stackSize));f.turn(now);s.turnedSlots[slot]=now;s.turned=now;visual(t,Actions.TURN,slot,t.getStackInSlot(slot),null,null);s.observed[slot]=Stacks.copy(t.getStackInSlot(slot),1);t.changed();return true;
    }
    public static void breakBoard(World w,int x,int y,int z){
        if(w.isRemote)return;TileEntity raw=w.getTileEntity(x,y,z);if(!(raw instanceof CuttingBoardTile))return;
        CuttingBoardTile t=(CuttingBoardTile)raw;StationState s=state(t);List<ItemStack> extra=new ArrayList<ItemStack>(s.layers);s.layers.clear();for(ItemStack i:extra)t.drop(i);
    }
    private static final ThreadLocal<net.minecraft.entity.player.EntityPlayer> CUTTING_FOR=new ThreadLocal<net.minecraft.entity.player.EntityPlayer>();
    public static boolean boardDrop(Object tile,net.minecraft.item.ItemStack stack){net.minecraft.entity.player.EntityPlayer p=CUTTING_FOR.get();if(p==null)return false;Actions.give(p,(InventoryTile)tile,stack);return true;}
    public static boolean processFor(CuttingBoardTile t,net.minecraft.item.ItemStack tool,net.minecraft.entity.player.EntityPlayer p){CUTTING_FOR.set(p);try{return t.process(tool,p);}finally{CUTTING_FOR.remove();}}
    public static void visual(InventoryTile t,int kind,int slot,net.minecraft.item.ItemStack item,net.minecraft.item.ItemStack tool,net.minecraft.item.ItemStack result){state(t).visual(kind,slot,item,tool,result,t.getWorldObj().getTotalWorldTime());state(t).visualLayers.clear();if(kind==Actions.ASSEMBLE&&t instanceof CuttingBoardTile)for(net.minecraft.item.ItemStack layer:RecipeBook.layers((CuttingBoardTile)t))state(t).visualLayers.add(layer.copy());}
    private static final class PotAccess {
        static final Field recipe=field("recipe"),dirty=field("recipeDirty"),saved=field("savedRecipe");
        static final Method cook=method("cook"),serve=method("serveStored"),can=method("canCook",CookingRecipes.Recipe.class);
        static Field field(String n){try{Field f=CookingPotTile.class.getDeclaredField(n);f.setAccessible(true);return f;}catch(Exception e){throw new IllegalStateException(e);}}
        static Method method(String n,Class<?>...a){try{Method m=CookingPotTile.class.getDeclaredMethod(n,a);m.setAccessible(true);return m;}catch(Exception e){throw new IllegalStateException(e);}}
        static void prepare(CookingPotTile t,CookingRecipes.Recipe r,String id){try{recipe.set(t,r);dirty.setBoolean(t,false);saved.set(t,id);if(r!=null)t.total=r.time;}catch(Exception e){throw new IllegalStateException(e);}}
        static void call(Method m,CookingPotTile t){try{m.invoke(t);}catch(Exception e){throw new IllegalStateException("Native pot callback failed",e);}}
        static boolean canCook(CookingPotTile t,CookingRecipes.Recipe r){try{return (Boolean)can.invoke(t,r);}catch(Exception e){throw new IllegalStateException(e);}}
    }
    private StationHooks(){}
}
