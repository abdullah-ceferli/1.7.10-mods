package com.baisylia.culturaldelights.legacy;
import com.google.gson.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.enchantment.*;
import vectorwing.farmersdelight.legacy.Stacks;
import vectorwing.farmersdelight.legacy.SourceTags;
/** Interpreter for the pinned mods' block loot contracts, including tool and fortune conditions. */
public final class Loot {
    public static ArrayList<ItemStack> drops(String id,int age,int fortune,ItemStack tool,Random random){
        ArrayList<ItemStack> result=new ArrayList<ItemStack>();JsonObject all=Port.CONTENT.loot;
        if(!all.has("blocks/"+id)){ItemStack self=Port.CONTENT.stack(id);if(self!=null)result.add(self);return result;}
        table(all.getAsJsonObject("blocks/"+id),age,fortune,tool,random,result);return result;
    }
    public static void table(JsonObject table,int age,int fortune,ItemStack tool,Random r,List<ItemStack> out){
        if(!table.has("pools"))return;
        for(JsonElement e:table.getAsJsonArray("pools")) {
            JsonObject pool=e.getAsJsonObject();if(!conditions(pool,age,fortune,tool,r))continue;
            int rolls=(int)value(pool.get("rolls"),r);for(int k=0;k<rolls;k++) {
                List<JsonObject> choices=new ArrayList<JsonObject>();int total=0;
                for(JsonElement en:pool.getAsJsonArray("entries")) {JsonObject entry=en.getAsJsonObject();if(conditions(entry,age,fortune,tool,r)){choices.add(entry);total+=Catalog.num(entry,"weight",1);}}
                if(total==0)continue;int pick=r.nextInt(total);for(JsonObject entry:choices){pick-=Catalog.num(entry,"weight",1);if(pick<0){entry(entry,age,fortune,tool,r,out);break;}}
            }
        }
    }
    private static boolean entry(JsonObject e,int age,int fortune,ItemStack tool,Random r,List<ItemStack> out){
        String type=Catalog.str(e,"type","");if(type.endsWith(":alternatives")){for(JsonElement child:e.getAsJsonArray("children")){JsonObject j=child.getAsJsonObject();if(conditions(j,age,fortune,tool,r)&&entry(j,age,fortune,tool,r,out))return true;}return false;}
        if(type.endsWith(":empty"))return true;
        ItemStack s=Stacks.resolve(Catalog.str(e,"name",""));if(s==null)return true;
        if(e.has("functions"))for(JsonElement fn:e.getAsJsonArray("functions")){JsonObject f=fn.getAsJsonObject();if(!conditions(f,age,fortune,tool,r))continue;String name=Catalog.str(f,"function","");
            if(name.endsWith(":set_count"))s.stackSize=(int)value(f.get("count"),r);
            else if(name.endsWith(":apply_bonus")){String formula=Catalog.str(f,"formula","");JsonObject p=f.getAsJsonObject("parameters");if(formula.endsWith(":binomial_with_bonus_count")){for(int i=0;i<fortune+Catalog.num(p,"extra",0);i++)if(r.nextFloat()<p.get("probability").getAsFloat())s.stackSize++;}else if(formula.endsWith(":uniform_bonus_count"))s.stackSize+=r.nextInt(1+fortune*Catalog.num(p,"bonusMultiplier",1));}
            else if(name.endsWith(":copy_state"))s.setItemDamage(age&7);
            else if(name.endsWith(":limit_count")){JsonElement limit=f.get("limit");if(limit.isJsonPrimitive())s.stackSize=Math.min(s.stackSize,limit.getAsInt());else{JsonObject l=limit.getAsJsonObject();s.stackSize=Math.max(Catalog.num(l,"min",0),Math.min(Catalog.num(l,"max",64),s.stackSize));}}
        }
        if(s.stackSize>0)out.add(s);return true;
    }
    static double value(JsonElement v,Random r){if(v==null)return 1;if(v.isJsonPrimitive())return v.getAsDouble();JsonObject j=v.getAsJsonObject();int min=(int)value(j.get("min"),r),max=(int)value(j.get("max"),r);return min+r.nextInt(Math.max(1,max-min+1));}
    private static boolean conditions(JsonObject obj,int age,int fortune,ItemStack tool,Random r){if(obj.has("conditions"))for(JsonElement c:obj.getAsJsonArray("conditions"))if(!condition(c.getAsJsonObject(),age,fortune,tool,r))return false;return true;}
    private static boolean condition(JsonObject c,int age,int fortune,ItemStack tool,Random r){String name=Catalog.str(c,"condition","");
        if(name.endsWith(":survives_explosion"))return true;
        if(name.endsWith(":block_state_property")){JsonObject properties=c.getAsJsonObject("properties");return !properties.has("age")||properties.get("age").getAsInt()==(age&7);}
        if(name.endsWith(":random_chance"))return r.nextFloat()<c.get("chance").getAsFloat();
        if(name.endsWith(":inverted"))return !condition(c.getAsJsonObject("term"),age,fortune,tool,r);
        if(name.endsWith(":alternative")||name.endsWith(":any_of")){for(JsonElement term:c.getAsJsonArray("terms"))if(condition(term.getAsJsonObject(),age,fortune,tool,r))return true;return false;}
        if(name.endsWith(":table_bonus")){JsonArray chances=c.getAsJsonArray("chances");return r.nextFloat()<chances.get(Math.min(fortune,chances.size()-1)).getAsFloat();}
        if(name.endsWith(":match_tool")){if(tool==null)return false;JsonObject p=c.getAsJsonObject("predicate");if(p.has("items")){boolean match=false;for(JsonElement id:p.getAsJsonArray("items"))match|=Stacks.same(tool,Stacks.resolve(id.getAsString()));if(!match)return false;}
            if(p.has("tag")&&!Stacks.matches(SourceTags.candidates(p.get("tag").getAsString()),tool))return false;
            if(p.has("enchantments"))for(JsonElement en:p.getAsJsonArray("enchantments")){JsonObject e=en.getAsJsonObject();int level=EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId,tool);JsonElement levels=e.get("levels");if(levels!=null&&levels.isJsonObject()&&level<Catalog.num(levels.getAsJsonObject(),"min",0))return false;}
            return true;}
        throw new IllegalArgumentException("Unsupported original loot condition "+name);
    }
    private Loot(){}
}
