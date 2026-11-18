package dev.srpbackport.tests;
import java.io.*;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;
import cpw.mods.fml.common.*;import cpw.mods.fml.common.event.*;import cpw.mods.fml.common.eventhandler.SubscribeEvent;import cpw.mods.fml.common.gameevent.TickEvent;import cpw.mods.fml.common.registry.*;
import net.minecraft.block.*;import net.minecraft.block.material.Material;import net.minecraft.item.*;import net.minecraft.init.*;import net.minecraft.tileentity.TileEntity;import net.minecraft.nbt.NBTTagCompound;import net.minecraft.world.*;import net.minecraft.server.MinecraftServer;import net.minecraft.util.ResourceLocation;
import com.dhanantry.scapeandrunparasites.backport.registry.*;
@Mod(modid="srp_registry_checks",name="Disposable native registration checks",version="1")
public final class NativeRegistryChecks{
 public static Block variant,ordered,noItem,foreign,tileBlock;public static Item form,ordinary,foreignItem;static int ticks,failed,assertions;static boolean done;static WorldServer world;static final List<String> report=new ArrayList<String>();
 public static class SavedTile extends TileEntity{int marker;public void writeToNBT(NBTTagCompound n){super.writeToNBT(n);n.setInteger("marker",marker);}public void readFromNBT(NBTTagCompound n){super.readFromNBT(n);marker=n.getInteger("marker");}}
 public static class TileBlock extends Block{TileBlock(){super(Material.rock);}public boolean hasTileEntity(int meta){return true;}public TileEntity createTileEntity(World w,int meta){return new SavedTile();}}
 interface Check{void run()throws Exception;}static void check(boolean ok,String why){assertions++;if(!ok)throw new AssertionError(why);}static void test(String name,Check fn){try{fn.run();report.add("PASS: "+name);}catch(Throwable e){failed++;report.add("FAIL: "+name+" :: "+e);e.printStackTrace();}}
 static void rejects(Class<? extends Throwable> type,Check fn)throws Exception{try{fn.run();}catch(Throwable e){check(type.isInstance(e),"Wrong exception: "+e);return;}throw new AssertionError("Expected "+type.getName());}
 @Mod.EventHandler public void preInit(FMLPreInitializationEvent event){
  test("Pending names retain active/explicit domains, inherited fluent identity and native vanilla names",()->{
   variant=RegistryFixture.block();form=RegistryFixture.form(variant);ordinary=RegistryFixture.ordinary();ordered=RegistryFixture.ordered();noItem=RegistryNames.set(new RegistryFixture.Parent(),"no_item");
   foreign=RegistryNames.set(new RegistryFixture.Parent(),"explicit_other","foreign");foreignItem=RegistryNames.set(new Item(),new ResourceLocation("explicit_other","foreign_item"));tileBlock=RegistryNames.set(new TileBlock(),"tile");
   check(RegistryNames.get(variant).toString().equals("srp_registry_checks:variant")&&RegistryNames.get(form).equals(RegistryNames.get(variant)),"Default namespace/name");
   check(RegistryFixture.name(Blocks.stone).toString().equals("minecraft:stone")&&RegistryNames.get(Items.stick).toString().equals("minecraft:stick"),"Vanilla delegate names");
   check(RegistryFixture.untouched(new RegistryFixture.Unrelated()).equals("unrelated"),"Unrelated methods rewritten");
   rejects(IllegalStateException.class,()->RegistryNames.set(variant,"second"));rejects(IllegalStateException.class,()->RegistryNames.set(Blocks.stone,"stone_again"));
  });
  test("Block registration precedes custom ItemBlocks without auto-generating replacements",()->{
   NativeContentRegistry.blocks(variant,ordered,noItem,foreign,tileBlock);
   check(Item.getItemFromBlock(variant)==null&&Item.getItemFromBlock(noItem)==null,"An unwanted default item was generated");
   NativeContentRegistry.items(form,ordinary,foreignItem);
   check(Item.getItemFromBlock(variant)==form&&form instanceof RegistryFixture.VariantItem,"Custom ItemBlock replaced");
   check(Item.getIdFromItem(form)==Block.getIdFromBlock(variant)&&Item.getItemFromBlock(noItem)==null,"Native block/item ID relationship");
   check(GameRegistry.findBlock("srp_registry_checks","variant")==variant&&GameRegistry.findItem("srp_registry_checks","variant")==form,"Native name lookup");
   check(RegistryNames.get(foreign).toString().equals("explicit_other:foreign")&&GameRegistry.findBlock("explicit_other","foreign")==foreign&&GameRegistry.findItem("explicit_other","foreign_item")==foreignItem,"Explicit namespace was prepended or lost");
  });
  test("Duplicate names/objects and item-before-block errors reject before changing native entries",()->{
   rejects(IllegalStateException.class,()->NativeContentRegistry.block(variant));rejects(IllegalStateException.class,()->NativeContentRegistry.item(form));
   rejects(IllegalArgumentException.class,()->NativeContentRegistry.block(RegistryNames.set(new RegistryFixture.Parent(),"variant")));
   rejects(IllegalArgumentException.class,()->NativeContentRegistry.item(RegistryNames.set(new Item(),"ordinary")));
   Block unregistered=RegistryNames.set(new RegistryFixture.Parent(),"not_registered");Item wrong=RegistryNames.set(new ItemBlock(unregistered),"wrong");
   rejects(IllegalStateException.class,()->NativeContentRegistry.item(wrong));check(wrong.delegate.name()==null&&GameRegistry.findItem("srp_registry_checks","wrong")==null,"Rejected item entered registry");
  });
  NativeContentRegistry.tile(SavedTile.class,new ResourceLocation("srp_registry_checks","saved_tile"));
 }
 @Mod.EventHandler public void init(FMLInitializationEvent event){
  if(Boolean.getBoolean("srp.registryClientTest")){if(event.getSide().isClient())try{Class.forName("dev.srpbackport.tests.NativeRegistryClientChecks").getMethod("initialize").invoke(null);}catch(Exception e){throw new RuntimeException(e);}}
  else FMLCommonHandler.instance().bus().register(this);
 }
 static void checks(boolean restart){
  test("All sixteen custom item metadata variants survive native stack NBT and block drops",()->{
   for(int meta=0;meta<16;meta++){ItemStack stack=new ItemStack(form,3,meta);NBTTagCompound data=new NBTTagCompound();stack.writeToNBT(data);ItemStack copy=ItemStack.loadItemStackFromNBT(data);check(copy!=null&&copy.getItem()==form&&copy.getItemDamage()==meta&&copy.stackSize==3,"Stack NBT variant "+meta);
    check(((ItemBlock)form).getMetadata(meta)==meta,"Item metadata variant "+meta);ArrayList<ItemStack> drops=variant.getDrops(world,meta,80,0,meta,0);check(drops.size()==1&&drops.get(0).getItem()==form&&drops.get(0).getItemDamage()==meta,"Custom drops variant "+meta);}
  });
  if(restart){
   test("Registered block IDs, sixteen stored metadata variants and tile names survive a real second JVM",()->{
    for(int meta=0;meta<16;meta++)check(world.getBlock(meta,80,0)==variant&&world.getBlockMetadata(meta,80,0)==meta,"Persisted variant "+meta);
    TileEntity saved=world.getTileEntity(20,80,0);check(saved instanceof SavedTile&&((SavedTile)saved).marker==1121710,"Tile data after restart");
    NBTTagCompound n=new NBTTagCompound();saved.writeToNBT(n);check(n.getString("id").equals("srp_registry_checks:saved_tile"),"Tile ResourceLocation ID changed");});
  }else{
   test("Native chunk save retains all metadata and namespaced tile NBT",()->{
    for(int meta=0;meta<16;meta++)check(world.setBlock(meta,80,0,variant,meta,3),"Place variant "+meta);
    world.setBlock(20,80,0,tileBlock,0,3);SavedTile tile=(SavedTile)world.getTileEntity(20,80,0);tile.marker=1121710;tile.markDirty();NBTTagCompound n=new NBTTagCompound();tile.writeToNBT(n);check(n.getString("id").equals("srp_registry_checks:saved_tile"),"Namespaced tile ID");world.saveAllChunks(true,null);Files.write(Paths.get("registry-saved.done"),Arrays.asList("saved"),StandardCharsets.UTF_8);});
  }
 }
 @SubscribeEvent public void tick(TickEvent.ServerTickEvent event){if(done||event.phase!=TickEvent.Phase.END||++ticks!=20)return;String phase=Files.exists(Paths.get("registry-saved.done"))?"restart":"initial";try{world=MinecraftServer.getServer().worldServerForDimension(0);world.getChunkFromChunkCoords(0,0);world.getChunkFromChunkCoords(1,0);checks(phase.equals("restart"));}catch(Throwable e){failed++;report.add("FAIL: setup :: "+e);e.printStackTrace();}finally{done=true;report.add("Assertions: "+assertions);report.add("Failed: "+failed);try{Files.write(Paths.get("registry-"+phase+".txt"),report,StandardCharsets.UTF_8);}catch(IOException e){throw new RuntimeException(e);}MinecraftServer.getServer().initiateShutdown();}}
}
