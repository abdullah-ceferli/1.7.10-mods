package com.baisylia.culturaldelights.legacy;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import vectorwing.farmersdelight.legacy.*;
import vectorwing.farmersdelight.legacy.api.SourceRecipes;

public final class Catalog {
    public static final String MOD="culturaldelights";
    public final JsonObject data=read("catalog"), loot=read("loot"), source=read("source_data");
    public final Map<String,Item> items=new LinkedHashMap<String,Item>();
    public final Map<String,Plant> crops=new LinkedHashMap<String,Plant>();
    public final Map<String,GardenBlock> blocks=new LinkedHashMap<String,GardenBlock>();
    public final List<JsonObject> trees=new ArrayList<JsonObject>();
    public final Map<String,String> deferred=new LinkedHashMap<String,String>();
    public boolean generation=true;
    public int cropRarity=12,treeRarity=10;
    public final CreativeTabs tab=new CreativeTabs(MOD) { public Item getTabIconItem() { return items.isEmpty()?net.minecraft.init.Items.wheat:items.values().iterator().next(); } };
    public static JsonObject read(String name) {
        InputStream in=Catalog.class.getResourceAsStream("/"+MOD+"/"+name+".json");
        if(in==null) throw new IllegalStateException("Missing original catalog "+name);
        try(InputStreamReader r=new InputStreamReader(in,StandardCharsets.UTF_8)) {return new JsonParser().parse(r).getAsJsonObject();}
        catch(IOException e) {throw new IllegalStateException(e);}
    }
    public static String str(JsonObject j,String key,String fallback) {return j.has(key)?j.get(key).getAsString():fallback;}
    public static int num(JsonObject j,String key,int fallback) {return j.has(key)?j.get(key).getAsInt():fallback;}
    public static boolean yes(JsonObject j,String key) {return j.has(key)&&j.get(key).getAsBoolean();}
    public ItemStack stack(String id) {
        if(id==null||id.isEmpty())return null;
        if(id.indexOf(':')<0) id=MOD+":"+id;
        return Stacks.resolve(id);
    }
    public void register(File file) {
        GameRegistry.registerTileEntity(GardenTile.class,MOD+":feast");
        Configuration cfg=new Configuration(file);cfg.load();
        generation=cfg.getBoolean("generateInNewChunks","world",true,"Generate original crops and trees in newly explored Overworld chunks.");
        cropRarity=cfg.getInt("cropPatchRarity","world",12,1,10000,"Croptopia crop patch rarity; Cultural Delights uses its original rarity of 25.");
        treeRarity=cfg.getInt("fruitTreeRarity","world",10,1,10000,"One fruit-tree placement attempt per this many eligible chunks.");cfg.save();
        for(JsonElement e:data.getAsJsonArray("crops")) {
            JsonObject c=e.getAsJsonObject();String id=str(c,"id","");Plant p=new Plant(id,c,false);crops.put(id,p);GameRegistry.registerBlock(p,(Class<? extends ItemBlock>)null,id);
            if(yes(c,"double")) {Plant top=new Plant("corn_upper",c,true);crops.put("corn_upper",top);GameRegistry.registerBlock(top,(Class<? extends ItemBlock>)null,"corn_upper");}
        }
        for(JsonElement e:data.getAsJsonArray("trees")) {
            JsonObject t=e.getAsJsonObject();trees.add(t);
            block(str(t,"sapling",""),"sapling",t);block(str(t,"leaves",""),"leaves",t);
            if(yes(t,"wood")) for(String id:new String[]{"cinnamon_log","stripped_cinnamon_log","cinnamon_wood","stripped_cinnamon_wood"}) block(id,"wood",t);
        }
        for(Map.Entry<String,JsonElement> e:data.getAsJsonObject("blocks").entrySet()) {
            String id=e.getKey();if(blocks.containsKey(id)||crops.containsKey(id))continue;
            String kind=id.startsWith("wild_")?"wild":id.contains("parmesan_block")||id.equals("exotic_roll_medley")?"feast":id.equals("avocado_pit")?"pit":id.contains("leaves")?"leaves":id.contains("log")||id.endsWith("wood")?"wood":id.startsWith("potted_")?"potted":"solid";
            block(id,kind,trees.isEmpty()?new JsonObject():trees.get(0));
        }
        if(MOD.equals("croptopia")) block("salt_ore","ore",new JsonObject());
        for(Map.Entry<String,JsonElement> e:data.getAsJsonObject("items").entrySet()) {
            String id=e.getKey();JsonObject item=e.getValue().getAsJsonObject();if(blocks.containsKey(id)) {items.put(id,Item.getItemFromBlock(blocks.get(id)));continue;}
            String kind=str(item,"kind","");Item nativeItem;
            if(kind.equals("seed")) {Plant p=null;for(Plant c:crops.values())if(c.seed.equals(id)&&!c.upper){p=c;break;}if(p==null)throw new IllegalStateException("Missing seed crop "+id);nativeItem=new Seeds(p,item);}
            else if(num(item,"hunger",0)>0) nativeItem=new Food(item);
            else nativeItem=new Ingredient(item);
            nativeItem.setUnlocalizedName(MOD+"."+id).setTextureName(str(item,"texture",MOD+":"+id)).setCreativeTab(tab);
            GameRegistry.registerItem(nativeItem,id);items.put(id,nativeItem);
            if(kind.equals("guide")) deferred.put("guide","The original Patchouli guide needs a native book implementation; no replacement interface is added.");
            for(JsonElement fx:item.getAsJsonArray("effects"))if(Catalog.str(fx.getAsJsonObject(),"name","").equals("GLOWING"))deferred.put(id+"/glowing","The modern glowing outline effect has no native 1.7.10 equivalent yet.");
        }
        for(Plant p:crops.values()) {ItemStack drop=stack(p.food);if(drop!=null) OreDictionary.registerOre("crop"+Character.toUpperCase(p.food.charAt(0))+p.food.substring(1),drop);}
        cpw.mods.fml.common.registry.VillagerRegistry.instance().registerVillageTradeHandler(0,new HarvestEvents.Trades());
        if(MOD.equals("croptopia")) HarvestEvents.fishing();
    }
    private void block(String id,String kind,JsonObject info) {
        if(blocks.containsKey(id))return;
        GardenBlock b=new GardenBlock(id,kind,info);blocks.put(id,b);
        if(id.equals("potted_avocado_sapling")) GameRegistry.registerBlock(b,(Class<? extends ItemBlock>)null,id);
        else GameRegistry.registerBlock(b,GardenItem.class,id);
    }
    public void recipes() {
        SourceTags.addCatalog(Catalog.class,"/"+MOD+"/native_tags.json");
        SourceRecipes.addCatalog(MOD,Catalog.class,"/"+MOD+"/native_recipes.json");
        JsonObject originals=read("recipes");
        for(Map.Entry<String,JsonElement> e:originals.entrySet()) {
            String type=str(e.getValue().getAsJsonObject(),"type","");
            if(!(type.startsWith("minecraft:")||type.startsWith("farmersdelight:"))) {
                String reason="Requires modern machine recipe integration: "+type;SourceRecipes.defer(e.getKey(),reason);deferred.put(e.getKey(),reason);
            }
        }
    }
    public void report() {
        JsonObject out=new JsonObject();out.addProperty("items",items.size());out.addProperty("crops",crops.size());out.addProperty("blocks",blocks.size());
        JsonObject skipped=new JsonObject();for(Map.Entry<String,String> e:deferred.entrySet()) skipped.addProperty(e.getKey(),e.getValue());
        for(Map<String,String> map:Arrays.asList(SourceCrafting.DEFERRED,CookingRecipes.DEFERRED,CuttingRecipes.DEFERRED,GrillingRecipes.DEFERRED))for(Map.Entry<String,String> e:map.entrySet())if(e.getKey().startsWith(MOD+":"))skipped.addProperty(e.getKey(),e.getValue());
        out.add("unavailable",skipped);
        File dir=new File("config/"+MOD);dir.mkdirs();try(Writer w=new OutputStreamWriter(new FileOutputStream(new File(dir,"compatibility.json")),StandardCharsets.UTF_8)){new GsonBuilder().setPrettyPrinting().create().toJson(out,w);}catch(IOException e){throw new IllegalStateException(e);}
    }
}
