package com.jkvin114.displaydelight;

import java.io.*;import java.util.*;import com.google.gson.*;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;import net.minecraft.item.ItemStack;

public final class Catalog {
    public static final Map<String,DisplayBlock> blocks=new LinkedHashMap<String,DisplayBlock>();
    private static final Map<String,DisplayBlock> direct=new HashMap<String,DisplayBlock>(),large=new HashMap<String,DisplayBlock>(),small=new HashMap<String,DisplayBlock>();
    public static final class Definition {
        public String id,kind,food;public int max;public boolean steam,fiery;
        public boolean empty(){return kind.startsWith("empty");}
        public boolean plated(){return kind.equals("plate")||kind.equals("small");}
        public boolean small(){return kind.equals("small")||kind.equals("empty_small");}
    }
    public static void register(){
        try{
            Reader reader=new InputStreamReader(Catalog.class.getResourceAsStream("/assets/displaydelight/legacy/catalog.json"),"UTF-8");
            Definition[] definitions=new Gson().fromJson(reader,Definition[].class);reader.close();
            for(Definition d:definitions){if(d.max<1||d.max>10||blocks.containsKey(d.id))throw new IllegalStateException("Invalid food definition "+d.id);
                DisplayBlock b=new DisplayBlock(d);blocks.put(d.id,b);GameRegistry.registerBlock(b,DisplayItem.class,d.id);}
        }catch(IOException e){throw new IllegalStateException("Cannot load display catalog",e);}
    }
    public static String key(ItemStack s){return Item.itemRegistry.getNameForObject(s.getItem())+"@"+s.getItemDamage();}
    public static ItemStack parse(String name){
        if(name==null||name.trim().length()==0)return null;
        String[] parts=name.trim().split("@");int meta=parts.length==1?0:Integer.parseInt(parts[1]);
        Object item=Item.itemRegistry.getObject(parts[0]);return item instanceof Item?new ItemStack((Item)item,1,meta):null;
    }
    public static void bindFoods(){
        direct.clear();large.clear();small.clear();
        for(DisplayBlock b:blocks.values()){
            if(b.definition.empty())continue;
            String name=Settings.config.get("foodMappings",b.definition.id,b.definition.food,"Real edible item registry name, optionally @metadata. Empty disables this food link; decorative display remains available.").getString();
            try{b.food=parse(name);}catch(RuntimeException e){throw new IllegalArgumentException("Invalid food mapping for "+b.definition.id+": "+name,e);}
            if(b.food!=null&&(Settings.vanilla||!name.startsWith("minecraft:"))){
                Map<String,DisplayBlock> map=b.definition.kind.equals("plate")?large:b.definition.kind.equals("small")?small:direct;
                DisplayBlock previous=map.put(key(b.food),b);
                if(previous!=null)throw new IllegalArgumentException("Duplicate food mapping: "+name+" for "+previous.definition.id+" and "+b.definition.id);
            }
        }
        Settings.config.save();
    }
    public static DisplayBlock find(ItemStack stack,String type){return stack==null?null:(type.equals("plate")?large:type.equals("small")?small:direct).get(key(stack));}
    public static DisplayBlock plate(boolean small){return blocks.get(small?"small_food_plate":"food_plate");}
}
