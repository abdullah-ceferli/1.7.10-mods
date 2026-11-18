package com.dhanantry.scapeandrunparasites.backport.registry;
import java.lang.reflect.*;import java.util.*;import net.minecraft.block.Block;import net.minecraft.item.*;import net.minecraft.tileentity.TileEntity;import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.registry.GameRegistry;import cpw.mods.fml.common.registry.GameData;
/** Native registration retains custom ItemBlock instances and native FML ID/save/remap bookkeeping. */
public final class NativeContentRegistry {
 private NativeContentRegistry(){}
 private static final Map<Object,Boolean> installed=new IdentityHashMap<Object,Boolean>();
 private static ResourceLocation required(ResourceLocation id){if(id==null)throw new IllegalStateException("No registry name was assigned");return id;}
 private static void ensureFresh(Object value,ResourceLocation name,String official){
  Objects.requireNonNull(value,"registry value");required(name);
  if(official!=null||installed.containsKey(value))throw new IllegalStateException("Object is already registered: "+name);
 }
 // FML 1.7 prepends the active mod even to foreign explicit domains. Preserve those names through
 // the same native GameData registration path, bypassing only that obsolete prefix conversion.
 private static void foreign(Object value,ResourceLocation id,String method,Class<?> type){
  try{Method main=GameData.class.getDeclaredMethod("getMain");main.setAccessible(true);Method register=GameData.class.getDeclaredMethod(method,type,String.class,int.class);register.setAccessible(true);register.invoke(main.invoke(null),value,id.toString(),-1);}
  catch(InvocationTargetException e){Throwable cause=e.getCause();if(cause instanceof RuntimeException)throw (RuntimeException)cause;if(cause instanceof Error)throw (Error)cause;throw new IllegalStateException("Native registration failed",cause);}
  catch(ReflectiveOperationException e){throw new IllegalStateException("Required Forge 1.7.10 registry boundary is unavailable",e);}
 }
 public static synchronized void block(Block block){
  ResourceLocation id=required(RegistryNames.get(block));ensureFresh(block,id,block.delegate.name());
  if(GameData.getBlockRegistry().containsKey(id.toString()))throw new IllegalArgumentException("Duplicate block name: "+id);
  if(id.getResourceDomain().equals(RegistryNames.activeDomain()))GameRegistry.registerBlock(block,(Class<? extends ItemBlock>)null,id.getResourcePath());else foreign(block,id,"registerBlock",Block.class);
  if(!id.equals(RegistryNames.get(block)))throw new IllegalStateException("Native block name changed during registration: "+id);installed.put(block,Boolean.TRUE);
 }
 public static synchronized void item(Item item){
  ResourceLocation id=required(RegistryNames.get(item));ensureFresh(item,id,item.delegate.name());
  if(GameData.getItemRegistry().containsKey(id.toString()))throw new IllegalArgumentException("Duplicate item name: "+id);
  if(item instanceof ItemBlock&&((ItemBlock)item).field_150939_a.delegate.name()==null)throw new IllegalStateException("Register a block before its ItemBlock: "+id);
  if(id.getResourceDomain().equals(RegistryNames.activeDomain()))GameRegistry.registerItem(item,id.getResourcePath());else foreign(item,id,"registerItem",Item.class);
  if(!id.equals(RegistryNames.get(item)))throw new IllegalStateException("Native item name changed during registration: "+id);installed.put(item,Boolean.TRUE);
 }
 public static void blocks(Block...values){for(Block block:values)block(block);}
 public static void items(Item...values){for(Item item:values)item(item);}
 public static void tile(Class<? extends TileEntity> type,ResourceLocation id){GameRegistry.registerTileEntity(type,Objects.requireNonNull(id,"tile id").toString());}
}
