package com.dhanantry.scapeandrunparasites.backport.registry;
import java.util.*;import net.minecraft.block.Block;import net.minecraft.item.Item;import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.Loader;import cpw.mods.fml.common.ModContainer;
/** Modern pending names on real native objects; registered native delegates remain authoritative. */
public final class RegistryNames {
 private RegistryNames(){}
 private static final Map<Object,ResourceLocation> pending=new IdentityHashMap<Object,ResourceLocation>();
 public static String activeDomain(){ModContainer mod=Loader.instance().activeModContainer();return mod==null?"minecraft":mod.getModId();}
 public static ResourceLocation resolve(String value){Objects.requireNonNull(value,"registry name");return new ResourceLocation(value.indexOf(':')<0?activeDomain()+":"+value:value);}
 public static synchronized ResourceLocation get(Block block){Objects.requireNonNull(block,"block");String nativeName=block.delegate.name();return nativeName!=null?new ResourceLocation(nativeName):pending.get(block);}
 public static synchronized ResourceLocation get(Item item){Objects.requireNonNull(item,"item");String nativeName=item.delegate.name();return nativeName!=null?new ResourceLocation(nativeName):pending.get(item);}
 private static void assign(Object value,ResourceLocation previous,ResourceLocation name){Objects.requireNonNull(name,"registry name");if(previous!=null)throw new IllegalStateException("Registry name is already assigned: "+previous+" -> "+name);pending.put(value,name);}
 public static synchronized <T extends Block>T set(T block,String name){return set(block,resolve(name));}
 public static synchronized <T extends Item>T set(T item,String name){return set(item,resolve(name));}
 public static synchronized <T extends Block>T set(T block,ResourceLocation name){assign(block,get(block),name);return block;}
 public static synchronized <T extends Item>T set(T item,ResourceLocation name){assign(item,get(item),name);return item;}
 public static <T extends Block>T set(T block,String domain,String path){return set(block,domain+":"+path);}
 public static <T extends Item>T set(T item,String domain,String path){return set(item,domain+":"+path);}
}
