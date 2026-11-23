package dev.industrialtechnology.hatch;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dev.industrialtechnology.builder.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.inventory.*;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.*;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;
import java.util.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

@Mod(modid="industrialtech_hatch_checks",name="Hatch and liquid checks",version="1",dependencies="required-after:industrialtech")
public final class HatchNativeChecks {
    private int phase,wait=25,failed;private WorldServer w;private FakePlayer p;private final List<String> report=new ArrayList<String>();
    @Mod.EventHandler public void init(FMLInitializationEvent e){if(Boolean.getBoolean("industrialtech.hatchTest"))FMLCommonHandler.instance().bus().register(this);}
    void check(boolean ok,String why){if(!ok)throw new AssertionError(why);}
    void test(String label,Runnable r){try{r.run();report.add("PASS: "+label);}catch(Throwable t){failed++;report.add("FAIL: "+label+": "+t);t.printStackTrace();}}
    public static final class Deny{public int count;public boolean enabled=true;@SubscribeEvent public void event(BlockEvent.PlaceEvent e){if(enabled){count++;e.setCanceled(true);}}}
    private void place(int x,int z,int variant){ItemStack s=new ItemStack(HatchRegistry.block,2,variant);p.inventory.mainInventory[0]=s;p.setPosition(x-2,83,z-2);w.setBlock(x,80,z,Blocks.stone,0,2);check(s.tryPlaceItemIntoWorld(p,w,x,80,z,1,.5F,1,.5F),"Native hatch placement failed");check(s.stackSize==1,"Hatch item count");}
    private int count(Item item){int n=0;for(ItemStack s:p.inventory.mainInventory)if(s!=null&&s.getItem()==item)n+=s.stackSize;return n;}
    private void liquid(Item fluid,int x,int z,int sources){for(int i=0;i<36;i++)p.inventory.mainInventory[i]=null;ItemStack tool=new ItemStack(BuilderRegistry.stick);p.inventory.mainInventory[0]=tool;for(int i=1;i<=sources;i++)p.inventory.mainInventory[i]=new ItemStack(fluid);BuilderData.material(tool,new ItemStack(fluid));BuilderData.tag(tool).setByte("BuildMode",(byte)BuildMode.LINE.ordinal());p.inventory.currentItem=0;p.setPosition(x+.5,84,z-2);w.setBlock(x,82,z,Blocks.stone,0,2);w.setBlock(x+2,82,z,Blocks.stone,0,2);BuilderService.click(p,tool,x,82,z,1);BuilderService.click(p,tool,x+2,82,z,1);BuilderService.step();}
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent e){if(e.phase!=TickEvent.Phase.END||phase==99||--wait>0)return;try{
        if(phase==0){w=MinecraftServer.getServer().worldServerForDimension(0);for(int x=0;x<8;x++)for(int z=0;z<8;z++)w.getChunkFromChunkCoords(x,z);p=new FakePlayer(w,new com.mojang.authlib.GameProfile(UUID.fromString("00000000-0000-0000-0000-000000001313"),"HatchChecks")){public void addChatMessage(IChatComponent c){}public void sendPlayerAbilities(){}};p.playerNetServerHandler=new net.minecraft.network.NetHandlerPlayServer(MinecraftServer.getServer(),new net.minecraft.network.NetworkManager(false),p){public void sendPacket(net.minecraft.network.Packet packet){}};p.theItemInWorldManager.setGameType(WorldSettings.GameType.SURVIVAL);
        if("restart".equals(System.getProperty("industrialtech.hatchPhase"))){test("Fresh server reload preserves offset hatch parts and open collision",()->{TileSlidingHatch t=BlockSlidingHatch.tile(w,40,81,40);check(t!=null&&t.open&&t.variant==2&&BlockSlidingHatch.parts(w,t).size()==4,"Offset/open state lost");check(!t.canUpdate(),"Hatch gained ticking work");});finish();return;}
        test("Normal, edge and corner hatches use one item and exactly 1, 2 and 4 synchronized non-ticking parts",()->{for(int v=0;v<3;v++){int x=24+v*8;place(x,24,v);TileSlidingHatch t=BlockSlidingHatch.tile(w,x,81,24);check(t!=null&&t.variant==v&&BlockSlidingHatch.parts(w,t).size()==(1<<v),"Wrong centered footprint");for(TileSlidingHatch part:BlockSlidingHatch.parts(w,t))check(!part.canUpdate(),"Ticking hatch");}});
        test("The one-item crafting grid cycles normal, edge, corner and back without losing the item",()->{for(int v=0;v<3;v++){InventoryCrafting grid=new InventoryCrafting(new Container(){public boolean canInteractWith(EntityPlayer x){return true;}},2,2);grid.setInventorySlotContents(0,new ItemStack(HatchRegistry.block,1,v));ItemStack result=CraftingManager.getInstance().findMatchingRecipe(grid,w);check(result!=null&&result.getItem()==Item.getItemFromBlock(HatchRegistry.block)&&result.getItemDamage()==(v+1)%3&&result.stackSize==1,"Offset recipe failure");}});
        test("Closed hatch blocks passage and opening removes center collision from every part",()->{TileSlidingHatch t=BlockSlidingHatch.tile(w,40,81,24);AxisAlignedBB mask=AxisAlignedBB.getBoundingBox(40.7,80,24.7,41.3,83,25.3);List<AxisAlignedBB> boxes=new ArrayList<AxisAlignedBB>();for(TileSlidingHatch a:BlockSlidingHatch.parts(w,t))HatchRegistry.block.addCollisionBoxesToList(w,a.xCoord,a.yCoord,a.zCoord,mask,boxes,p);check(!boxes.isEmpty(),"Closed hatch has no lid collision");HatchRegistry.block.onBlockActivated(w,41,81,25,p,1,0,0,0);boxes.clear();for(TileSlidingHatch a:BlockSlidingHatch.parts(w,t))HatchRegistry.block.addCollisionBoxesToList(w,a.xCoord,a.yCoord,a.zCoord,mask,boxes,p);check(t.open&&boxes.isEmpty(),"Open center is blocked");});
        test("Native Forge multi-place cancellation restores all hatch parts and the inventory item",()->{Deny deny=new Deny();MinecraftForge.EVENT_BUS.register(deny);try{ItemStack s=new ItemStack(HatchRegistry.block,1,2);p.inventory.mainInventory[0]=s;p.setPosition(54,84,54);w.setBlock(56,80,56,Blocks.stone,0,2);s.tryPlaceItemIntoWorld(p,w,56,80,56,1,.5F,1,.5F);check(deny.count>0&&s.stackSize==1,"Cancellation or refund missing");for(int dx=0;dx<2;dx++)for(int dz=0;dz<2;dz++)check(w.isAirBlock(56+dx,81,56+dz),"Partial hatch remained");}finally{deny.enabled=false;MinecraftForge.EVENT_BUS.unregister(deny);}});
        test("Breaking a centered part removes its group and drops one hatch",()->{int before=w.loadedEntityList.size();TileSlidingHatch t=BlockSlidingHatch.tile(w,32,81,24);check(HatchRegistry.block.removedByPlayer(w,p,33,81,24,true),"Break refused");check(w.isAirBlock(32,81,24)&&w.isAirBlock(33,81,24),"Orphan part remained");int drops=0;for(Object o:w.loadedEntityList)if(o instanceof net.minecraft.entity.item.EntityItem&&((net.minecraft.entity.item.EntityItem)o).getEntityItem().getItem()==Item.getItemFromBlock(HatchRegistry.block))drops+=((net.minecraft.entity.item.EntityItem)o).getEntityItem().stackSize;check(drops==1,"Wrong group drop count: "+drops);});
        test("Water sheet pays three filled buckets and returns exactly three empty buckets",()->{liquid(Items.water_bucket,64,32,3);check(count(Items.water_bucket)==0&&count(Items.bucket)==3,"Water bucket conservation");for(int x=64;x<67;x++)check(w.getBlock(x,83,32).getMaterial()==net.minecraft.block.material.Material.water&&w.getBlockMetadata(x,83,32)==0,"Water source missing");});
        test("Canceled liquid placements consume no bucket and leave no source",()->{Deny deny=new Deny();MinecraftForge.EVENT_BUS.register(deny);try{liquid(Items.lava_bucket,64,40,3);check(deny.count>0&&count(Items.lava_bucket)==3&&count(Items.bucket)==0&&w.isAirBlock(64,83,40),"Liquid cancellation failed");}finally{deny.enabled=false;MinecraftForge.EVENT_BUS.unregister(deny);}});
        test("Out-of-stock liquid build stops without free sources",()->{liquid(Items.lava_bucket,64,48,1);check(count(Items.lava_bucket)==0&&count(Items.bucket)==1&&w.getBlock(64,83,48).getMaterial()==net.minecraft.block.material.Material.lava&&w.isAirBlock(65,83,48)&&!BuilderService.active(p),"Out-of-stock liquid result");});
        place(40,40,2);HatchRegistry.block.onBlockActivated(w,40,81,40,p,1,0,0,0);phase=1;wait=45;return;}
        if(phase==1){test("Placed sources use native gravity and flow below the selected sheet",()->{check(w.getBlock(65,82,32).getMaterial()==net.minecraft.block.material.Material.water,"Water did not fall like a bucket placement");});finish();}
    }catch(Throwable t){failed++;report.add("FAIL: fixture "+t);t.printStackTrace();finish();}}
    private void finish(){report.add("Failed: "+failed);try{Files.write(Paths.get("hatch-"+System.getProperty("industrialtech.hatchPhase","initial")+".txt"),report,StandardCharsets.UTF_8);}catch(Exception e){throw new RuntimeException(e);}phase=99;MinecraftServer.getServer().initiateShutdown();}
}
