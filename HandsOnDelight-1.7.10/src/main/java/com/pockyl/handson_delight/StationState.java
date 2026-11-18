package com.pockyl.handson_delight;

import com.pockyl.handson_delight.station.*;
import java.util.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;

public final class StationState {
    public final StirSkill skill=new StirSkill();
    public final Frying[] frying=new Frying[6];
    public final long[] turnedSlots=new long[]{-1000,-1000,-1000,-1000,-1000,-1000};
    public final ItemStack[] observed=new ItemStack[6];
    public final List<ItemStack> layers=new ArrayList<ItemStack>();
    public boolean manual;public long attended=-1000,stirred=-1000,turned=-1000;
    public double work,bonus;public float pace=1,stars,spoonX,spoonZ;public int cuts;
    public String recipe="";public UUID cook;
    public final List<ItemStack> visualLayers=new ArrayList<ItemStack>();
    public int visualSerial,visualKind,visualSlot;public long visualAt;public ItemStack visualItem,visualTool,visualResult;
    public void visual(int kind,int slot,ItemStack item,ItemStack tool,ItemStack result,long now){visualSerial++;visualKind=kind;visualSlot=slot;visualAt=now;visualItem=item==null?null:item.copy();visualTool=tool==null?null:tool.copy();visualResult=result==null?null:result.copy();}

    public StationState(){for(int i=0;i<6;i++)frying[i]=new Frying();}
    public NBTTagCompound save(){
        NBTTagCompound t=new NBTTagCompound();t.setBoolean("Manual",manual);t.setDouble("Work",work);t.setDouble("Bonus",bonus);t.setInteger("Cuts",cuts);t.setString("Recipe",recipe);
        t.setFloat("Pace",pace);
        t.setLong("Stirred",stirred);t.setFloat("Stars",stars);t.setFloat("SpoonX",spoonX);t.setFloat("SpoonZ",spoonZ);t.setLong("Turned",turned);
        t.setInteger("VisualSerial",visualSerial);t.setInteger("VisualKind",visualKind);t.setInteger("VisualSlot",visualSlot);t.setLong("VisualAt",visualAt);
        NBTTagList vl=new NBTTagList();for(ItemStack layer:visualLayers)if(layer!=null)vl.appendTag(layer.writeToNBT(new NBTTagCompound()));t.setTag("VisualLayers",vl);
        if(visualItem!=null)t.setTag("VisualItem",visualItem.writeToNBT(new NBTTagCompound()));if(visualTool!=null)t.setTag("VisualTool",visualTool.writeToNBT(new NBTTagCompound()));if(visualResult!=null)t.setTag("VisualResult",visualResult.writeToNBT(new NBTTagCompound()));
        NBTTagList fs=new NBTTagList(),ls=new NBTTagList();
        for(int i=0;i<6;i++){NBTTagCompound f=new NBTTagCompound();frying[i].save(f);f.setLong("VisualTurned",turnedSlots[i]);if(observed[i]!=null)f.setTag("Observed",observed[i].writeToNBT(new NBTTagCompound()));fs.appendTag(f);}
        for(ItemStack s:layers)ls.appendTag(s.writeToNBT(new NBTTagCompound()));t.setTag("Frying",fs);t.setTag("Layers",ls);return t;
    }
    public void load(NBTTagCompound t){
        manual=t.getBoolean("Manual");work=finite(t.getDouble("Work"),32767);bonus=finite(t.getDouble("Bonus"),1);cuts=Math.max(0,Math.min(3,t.getInteger("Cuts")));recipe=t.getString("Recipe");
        pace=(float)finite(t.getFloat("Pace"),1.5);if(pace<=0)pace=1;
        stirred=t.hasKey("Stirred")?t.getLong("Stirred"):-1000;stars=(float)finite(t.getFloat("Stars"),3);spoonX=(float)NativeMath.clamp(t.getFloat("SpoonX"),-1,1);spoonZ=(float)NativeMath.clamp(t.getFloat("SpoonZ"),-1,1);turned=t.getLong("Turned");
        visualLayers.clear();NBTTagList vl=t.getTagList("VisualLayers",10);for(int n=0;n<Math.min(9,vl.tagCount());n++){ItemStack layer=ItemStack.loadItemStackFromNBT(vl.getCompoundTagAt(n));if(layer!=null)visualLayers.add(layer);}
        visualSerial=t.getInteger("VisualSerial");visualKind=t.getInteger("VisualKind");visualSlot=t.getInteger("VisualSlot");visualAt=t.getLong("VisualAt");visualItem=ItemStack.loadItemStackFromNBT(t.getCompoundTag("VisualItem"));visualTool=ItemStack.loadItemStackFromNBT(t.getCompoundTag("VisualTool"));visualResult=ItemStack.loadItemStackFromNBT(t.getCompoundTag("VisualResult"));
        NBTTagList fs=t.getTagList("Frying",10);for(int i=0;i<6;i++){
            NBTTagCompound f=fs.getCompoundTagAt(i);frying[i].load(f);turnedSlots[i]=f.hasKey("VisualTurned")?f.getLong("VisualTurned"):-1000;observed[i]=ItemStack.loadItemStackFromNBT(f.getCompoundTag("Observed"));
        }
        layers.clear();NBTTagList ls=t.getTagList("Layers",10);for(int i=0;i<Math.min(8,ls.tagCount());i++){
            ItemStack s=ItemStack.loadItemStackFromNBT(ls.getCompoundTagAt(i));if(s!=null&&s.stackSize>0){s.stackSize=Math.min(16,Math.min(s.getMaxStackSize(),s.stackSize));layers.add(s);}
        }
    }
    private static double finite(double v,double max){return Double.isNaN(v)||Double.isInfinite(v)?0:Math.max(0,Math.min(max,v));}
}
