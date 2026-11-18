package com.epherical.croptopia.legacy;
import com.google.gson.*;
import java.util.*;
import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraftforge.common.BiomeDictionary;
public final class Gardens implements IWorldGenerator {
    public void generate(Random r,int cx,int cz,World w,IChunkProvider generator,IChunkProvider chunks){
        if(!Port.CONTENT.generation||w.provider.dimensionId!=0)return;
        int x=cx*16+8+r.nextInt(16),z=cz*16+8+r.nextInt(16);BiomeGenBase b=w.getBiomeGenForCoords(x,z);
        if(Catalog.MOD.equals("culturaldelights")){
            String crop=BiomeDictionary.isBiomeOfType(b,BiomeDictionary.Type.JUNGLE)?"wild_eggplants":BiomeDictionary.isBiomeOfType(b,BiomeDictionary.Type.SWAMP)?"wild_cucumbers":b==BiomeGenBase.plains?"wild_corn":null;
            if(crop!=null&&r.nextInt(25)==0)patch(w,r,x,z,Port.CONTENT.blocks.get(crop),0,64);
            if(BiomeDictionary.isBiomeOfType(b,BiomeDictionary.Type.JUNGLE)&&r.nextInt(10)==0)tree(w,r,x,w.getHeightValue(x,z),z,Port.CONTENT.trees.get(0));
        }else{
            if(r.nextInt(Port.CONTENT.cropRarity)==0){List<Plant> choices=new ArrayList<Plant>();for(Plant p:Port.CONTENT.crops.values())if(matches(b,p.definition.getAsJsonArray("biomes")))choices.add(p);if(!choices.isEmpty()){Plant p=choices.get(r.nextInt(choices.size()));patch(w,r,x,z,p,p.maxAge,64);}}
            List<JsonObject> choices=new ArrayList<JsonObject>();for(JsonObject t:Port.CONTENT.trees)if(matches(b,t.getAsJsonArray("biomes")))choices.add(t);if(!choices.isEmpty()&&r.nextInt(Port.CONTENT.treeRarity)==0)tree(w,r,x,w.getHeightValue(x,z),z,choices.get(r.nextInt(choices.size())));
            if(BiomeDictionary.isBiomeOfType(b,BiomeDictionary.Type.RIVER)){int y=w.getTopSolidOrLiquidBlock(x,z)-1;for(int X=x-2;X<=x+2;X++)for(int Z=z-2;Z<=z+2;Z++)for(int Y=y-2;Y<=y;Y++){Block old=w.getBlock(X,Y,Z);if(old==Blocks.sand||old==Blocks.dirt||old==Blocks.gravel)w.setBlock(X,Y,Z,Port.CONTENT.blocks.get("salt_ore"),0,2);}}
        }
    }
    public static boolean matches(BiomeGenBase b,JsonArray names){if(names==null||names.size()==0)return false;for(JsonElement e:names){String id=e.isJsonPrimitive()?e.getAsString():Catalog.str(e.getAsJsonObject(),"id","");String n=id.substring(id.indexOf(':')+1);String key=nativeName(b);if(n.equals(key))return true;if(n.equals("bamboo_jungle")||n.equals("sparse_jungle")){if(BiomeDictionary.isBiomeOfType(b,BiomeDictionary.Type.JUNGLE))return true;}if(n.equals("mangrove_swamp")&&b==BiomeGenBase.swampland)return true;}return false;}
    public static String nativeName(BiomeGenBase b){int id=b.biomeID&127;String[] names={"ocean","plains","desert","windswept_hills","forest","taiga","swamp","river","nether_wastes","the_end","frozen_ocean","frozen_river","snowy_plains","ice_spikes","mushroom_fields","mushroom_fields","beach","desert","forest","taiga","windswept_hills","jungle","jungle","sparse_jungle","deep_ocean","stony_shore","snowy_beach","birch_forest","birch_forest","dark_forest","snowy_taiga","snowy_taiga","old_growth_pine_taiga","old_growth_spruce_taiga","windswept_forest","savanna","savanna","badlands","wooded_badlands","badlands"};return id<names.length?names[id]:b.biomeName.toLowerCase(Locale.ROOT).replace(' ','_');}
    private static void patch(World w,Random r,int x,int z,Block block,int age,int tries){for(int i=0;i<tries;i++){int X=x+r.nextInt(8)-r.nextInt(8),Z=z+r.nextInt(8)-r.nextInt(8),Y=w.getHeightValue(X,Z);if(w.isAirBlock(X,Y,Z)&&block.canPlaceBlockAt(w,X,Y,Z)&&(block instanceof Plant?((Plant)block).canBlockStay(w,X,Y,Z):true))w.setBlock(X,Y,Z,block,age,2);}}
    public static boolean tree(World w,Random r,int x,int y,int z,JsonObject def){
        JsonObject f=def.has("feature")?def.getAsJsonObject("feature"):new JsonObject();JsonObject trunk=f.has("trunk_placer")?f.getAsJsonObject("trunk_placer"):new JsonObject();int height=Catalog.num(trunk,"base_height",5)+r.nextInt(Catalog.num(trunk,"height_rand_a",2)+1)+r.nextInt(Catalog.num(trunk,"height_rand_b",0)+1);
        String stem="minecraft:oak_log";if(f.has("trunk_provider")){JsonObject provider=f.getAsJsonObject("trunk_provider");if(provider.has("state"))stem=Catalog.str(provider.getAsJsonObject("state"),"Name",stem);}
        net.minecraft.item.ItemStack wood=Port.CONTENT.stack(stem);Block log=wood==null?Blocks.log:Block.getBlockFromItem(wood.getItem());int woodMeta=wood==null?0:wood.getItemDamage();if(log==Blocks.air)log=Blocks.log;
        GardenBlock sapling=Port.CONTENT.blocks.get(Catalog.str(def,"sapling",""));boolean planted=w.getBlock(x,y,z)==sapling;
        if(planted)w.setBlockToAir(x,y,z);
        // The native oak generator supplies checked, obstruction-safe trunk and blob placement.
        WorldGenTrees nativeTree=new WorldGenTrees(false,height,woodMeta,0,false);
        if(!nativeTree.generate(w,r,x,y,z)){if(planted)w.setBlock(x,y,z,sapling,0,3);return false;}
        GardenBlock fruit=Port.CONTENT.blocks.get(Catalog.str(def,"leaves",""));
        for(int X=x-3;X<=x+3;X++)for(int Y=y;Y<=y+height+2;Y++)for(int Z=z-3;Z<=z+3;Z++){
            Block old=w.getBlock(X,Y,Z);if(X==x&&Z==z&&old==Blocks.log)w.setBlock(X,Y,Z,log,woodMeta,2);
            else if(old==Blocks.leaves&&fruit!=null){if(Catalog.yes(def,"wood"))w.setBlock(X,Y,Z,fruit,0,2);else if(r.nextInt(110)<20)w.setBlock(X,Y,Z,fruit,fruit.maxAge,2);else if(Catalog.MOD.equals("culturaldelights"))w.setBlock(X,Y,Z,Port.CONTENT.blocks.get("avocado_leaves"),0,2);}
        }
        return true;
    }
}
