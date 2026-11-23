package dev.industrialtechnology.test;

import api.hbm.fluidmk2.*;
import com.hbm.inventory.fluid.*;
import com.hbm.items.ModItems;
import com.hbm.items.machine.IItemFluidIdentifier;
import dev.industrialtechnology.fluid.*;
import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

final class FluidHbmChecks {
    private static final List<TileFluidMachine> fixture=new ArrayList<TileFluidMachine>();
    private static final List<TileFluidMachine> senders=new ArrayList<TileFluidMachine>(),receivers=new ArrayList<TileFluidMachine>();
    private static int waited;
    private static TileFluidMachine tile(WorldServer w,EntityPlayer p,int x,int y,int z,boolean sucker,int face,int kind,int amount){w.setBlock(x,y,z,sucker?FluidMachineRegistry.sucker:FluidMachineRegistry.puller,face,3);TileFluidMachine t=(TileFluidMachine)w.getTileEntity(x,y,z);t.owner(p);contents(t,kind,amount);fixture.add(t);return t;}
    private static void contents(TileFluidMachine t,int kind,int amount){NBTTagCompound n=new NBTTagCompound();n.setByte("Selected",(byte)kind);n.setInteger("Amount",amount);t.readContents(n);}
    private static ItemStack identifier(FluidType type){ItemStack stack=new ItemStack(ModItems.fluid_identifier_multi,1,type.getID());com.hbm.items.machine.ItemFluidIDMulti.setType(stack,type,true);return stack;}
    private static void later(WorldServer w){w.getWorldInfo().incrementTotalWorldTime(w.getTotalWorldTime()+20);}
    private static void remove(WorldServer w,TileFluidMachine t){w.setBlockToAir(t.xCoord,t.yCoord,t.zCoord);}
    public static final class Cancel {
        public boolean drain,place;public Runnable beforeBreak;
        @SubscribeEvent public void breaking(BlockEvent.BreakEvent e){if(beforeBreak!=null)beforeBreak.run();if(drain)e.setCanceled(true);}
        @SubscribeEvent public void placing(BlockEvent.PlaceEvent e){if(place)e.setCanceled(true);}
    }
    public static final class ProtectSelection {
        public int x,y,z;
        @SubscribeEvent public void interact(net.minecraftforge.event.entity.player.PlayerInteractEvent e){if(e.x==x&&e.y==y&&e.z==z)e.setCanceled(true);}
    }
    private static void choose(WorldServer w,FakePlayer p,TileFluidMachine t,FluidType type){p.inventory.setInventorySlotContents(0,identifier(type));t.getBlockType().onBlockActivated(w,t.xCoord,t.yCoord,t.zCoord,p,1,.5F,.5F,.5F);}
    private static void finishSelection(FluidNativeChecks c){
        int ticks=0;
        while(PullerSelection.pending()&&ticks++<512){PullerSelection.step();c.check(PullerSelection.lastVisits<=PullerSelection.CELLS_PER_TICK&&PullerSelection.lastChanges<=PullerSelection.CELLS_PER_TICK,"Connected selection exceeded its shared tick allowance");}
        c.check(!PullerSelection.pending(),"Connected selection did not finish within its bounded frontier");
    }
    private static void selectionChecks(FluidNativeChecks c,WorldServer w,FakePlayer p){
        c.test("One ordinary identifier click configures face-connected None pullers for water and lava, without a GUI",()->{
            for(int kind:new int[]{1,2}){
                int y=100+kind*3;TileFluidMachine root=tile(w,p,32,y,32,false,0,0,0),branch=tile(w,p,33,y,33,false,0,0,0),bridge=tile(w,p,34,y,32,false,0,kind,1750),last=tile(w,p,35,y,32,false,0,0,0),middle=tile(w,p,33,y,32,false,0,0,0);
                Object screen=p.openContainer;choose(w,p,root,kind==1?Fluids.WATER:Fluids.LAVA);finishSelection(c);
                for(TileFluidMachine t:new TileFluidMachine[]{root,branch,bridge,last,middle}){c.check(t.selected()==kind,"Connected puller selection missed a branch or same-fluid bridge");c.check(((IFluidUserMK2)t).getAllTanks()[0].getTankType()==(kind==1?Fluids.WATER:Fluids.LAVA),"Bulk selection did not configure the real HBM tank");remove(w,t);}
                c.check(bridge.amount()==1750&&p.openContainer==screen,"Bulk selection changed stored fluid or opened a GUI");
            }
        });
        c.test("Already selected fluids, suckers, air gaps and diagonal neighbors are preserved",()->{
            TileFluidMachine root=tile(w,p,32,111,32,false,0,0,0),other=tile(w,p,33,111,32,false,0,2,1750),behind=tile(w,p,34,111,32,false,0,0,0),sucker=tile(w,p,32,111,33,true,0,0,0),diagonal=tile(w,p,33,111,33,false,0,0,0);
            choose(w,p,root,Fluids.WATER);finishSelection(c);
            c.check(root.selected()==1&&other.selected()==2&&other.amount()==1750&&behind.selected()==0&&sucker.selected()==0&&diagonal.selected()==0,"Selection crossed a different fluid, sucker or diagonal gap");
            for(TileFluidMachine t:new TileFluidMachine[]{root,other,behind,sucker,diagonal})remove(w,t);
        });
        c.test("Unsupported identifiers, None clearing and a nonempty clicked tank do not start bulk conversion",()->{
            TileFluidMachine root=tile(w,p,32,115,32,false,0,1,0),next=tile(w,p,33,115,32,false,0,0,0);
            choose(w,p,root,FluidType.getEnumFromName("OIL"));c.check(root.selected()==1&&!PullerSelection.pending(),"Unsupported fluid started a batch");
            choose(w,p,root,Fluids.NONE);c.check(root.selected()==0&&next.selected()==0&&!PullerSelection.pending(),"None selection spread beyond the clicked block");
            contents(root,1,1750);choose(w,p,root,Fluids.LAVA);c.check(root.selected()==1&&root.amount()==1750&&next.selected()==0&&!PullerSelection.pending(),"Nonempty tank was converted");
            remove(w,root);remove(w,next);
        });
        c.test("A newer click replaces that player's pending batch and captures its chosen identifier fluid",()->{
            TileFluidMachine root=tile(w,p,32,118,32,false,0,0,0),next=tile(w,p,33,118,32,false,0,0,0);
            choose(w,p,root,Fluids.WATER);choose(w,p,root,Fluids.NONE);PullerSelection.step();c.check(root.selected()==0&&next.selected()==0&&!PullerSelection.pending(),"None clearing did not cancel the previous batch");
            choose(w,p,root,Fluids.WATER);choose(w,p,root,Fluids.LAVA);com.hbm.items.machine.ItemFluidIDMulti.setType(p.getHeldItem(),Fluids.WATER,true);
            finishSelection(c);c.check(root.selected()==2&&next.selected()==2,"A stale batch or later tool change overrode the clicked selection");remove(w,root);remove(w,next);
        });
        c.test("Forge right-click protection stops a bulk edit and traversal beyond the protected puller",()->{
            TileFluidMachine root=tile(w,p,32,121,32,false,0,0,0),protectedTile=tile(w,p,33,121,32,false,0,0,0),behind=tile(w,p,34,121,32,false,0,0,0);
            ProtectSelection protection=new ProtectSelection();protection.x=33;protection.y=121;protection.z=32;MinecraftForge.EVENT_BUS.register(protection);
            try{choose(w,p,root,Fluids.WATER);finishSelection(c);c.check(root.selected()==1&&protectedTile.selected()==0&&behind.selected()==0,"Bulk selection bypassed protection");}
            finally{MinecraftForge.EVENT_BUS.unregister(protection);remove(w,root);remove(w,protectedTile);remove(w,behind);}
        });
        c.test("A large connected group shares 128 cell visits per tick and completes over multiple batches",()->{
            List<TileFluidMachine> many=new ArrayList<TileFluidMachine>();for(int x=32;x<40;x++)for(int y=130;y<138;y++)for(int z=64;z<72;z++)many.add(tile(w,p,x,y,z,false,0,0,0));
            choose(w,p,many.get(0),Fluids.WATER);PullerSelection.step();c.check(PullerSelection.pending()&&PullerSelection.lastVisits<=128&&PullerSelection.lastChanges<=128,"Large selection ran synchronously or exceeded the tick allowance");finishSelection(c);
            for(TileFluidMachine t:many){c.check(t.selected()==1,"Bounded selection did not reach every connected puller");remove(w,t);}
        });
        c.test("Selection is limited to 64 face steps and never loads an adjacent unloaded chunk",()->{
            List<TileFluidMachine> line=new ArrayList<TileFluidMachine>();for(int x=20;x<=85;x++)line.add(tile(w,p,x,145,48,false,0,0,0));choose(w,p,line.get(0),Fluids.LAVA);finishSelection(c);
            c.check(line.get(64).selected()==2&&line.get(65).selected()==0,"Selection exceeded or prematurely stopped before its depth bound");for(TileFluidMachine t:line)remove(w,t);
            int cx=256,cz=256,x=cx*16+15,z=cz*16+7;w.getChunkFromChunkCoords(cx,cz);TileFluidMachine root=tile(w,p,x,120,z,false,0,0,0);
            w.theChunkProviderServer.unloadChunksIfNotNearSpawn(cx+1,cz);w.theChunkProviderServer.unloadQueuedChunks();c.check(!w.getChunkProvider().chunkExists(cx+1,cz),"Boundary fixture neighbor remained loaded");
            choose(w,p,root,Fluids.WATER);c.check(root.selected()==1,"Clicked boundary puller did not select Water");c.check(!w.getChunkProvider().chunkExists(cx+1,cz),"Identifier selection loaded a neighboring chunk before its batch");
            finishSelection(c);c.check(!w.getChunkProvider().chunkExists(cx+1,cz),"Bulk selection forced a neighboring chunk");remove(w,root);
        });
        c.test("Client interactions do not mutate selections, and server world unload clears pending batches",()->{
            TileFluidMachine root=tile(w,p,32,150,32,false,0,0,0),next=tile(w,p,33,150,32,false,0,0,0);boolean remote=w.isRemote;
            try{w.isRemote=true;choose(w,p,root,Fluids.WATER);}finally{w.isRemote=remote;}
            c.check(root.selected()==0&&!PullerSelection.pending(),"Client prediction changed server settings");
            choose(w,p,root,Fluids.LAVA);c.check(PullerSelection.pending(),"Selection did not queue");PullerSelection.unload(w);c.check(!PullerSelection.pending()&&next.selected()==0,"Unloaded-world batch was retained");remove(w,root);remove(w,next);
        });
    }
    static void setup(FluidNativeChecks c,WorldServer w,FakePlayer p){fixture.clear();senders.clear();receivers.clear();waited=0;
        c.test("Real HBM Fluid Identifier selects water/lava, rejects other fluids and never opens a container",()->{TileFluidMachine t=tile(w,p,32,80,32,true,0,0,0);Object screen=p.openContainer;
            for(FluidType type:new FluidType[]{Fluids.WATER,Fluids.LAVA}){ItemStack id=identifier(type);c.check(((IItemFluidIdentifier)id.getItem()).getType(w,32,80,32,id)==type,"Identifier primary-fluid NBT schema");p.inventory.setInventorySlotContents(0,id);c.check(t.getBlockType().onBlockActivated(w,32,80,32,p,1,.5F,.5F,.5F),"Identifier click ignored");c.check(t.selected()==(type==Fluids.WATER?1:2)&&p.openContainer==screen,"Fluid selection or no-GUI rule");}
            p.inventory.setInventorySlotContents(0,identifier(FluidType.getEnumFromName("OIL")));t.getBlockType().onBlockActivated(w,32,80,32,p,1,.5F,.5F,.5F);c.check(t.selected()==2,"Unsupported fluid changed selection");remove(w,t);});
        c.test("Sucker drains a real source into exactly 1000 mB of the selected HBM fluid",()->{for(int kind:new int[]{1,2}){TileFluidMachine t=tile(w,p,36,80,32+kind*4,true,0,kind,0);Block fluid=kind==1?Blocks.water:Blocks.lava;w.setBlock(36,79,t.zCoord,fluid,0,2);c.check(t.work(32,true)&&t.amount()==1000&&w.isAirBlock(36,79,t.zCoord),"Source-to-buffer conservation");c.check(((IFluidUserMK2)t).getAllTanks()[0].getTankType()==(kind==1?Fluids.WATER:Fluids.LAVA),"Wrong HBM fluid ID");c.check(!t.configure(3-kind)&&t.selected()==kind,"Mixed a nonempty tank");remove(w,t);}});
        c.test("Full buffers, unconfigured machines, other liquids and flowing blocks cannot generate fluid",()->{TileFluidMachine t=tile(w,p,40,80,32,true,0,1,8000);w.setBlock(40,79,32,Blocks.water,0,2);c.check(!t.work(32,true)&&t.lastProbes==0&&w.getBlock(40,79,32)==Blocks.water,"Full buffer drained");contents(t,1,0);w.setBlock(40,79,32,Blocks.lava,0,2);c.check(!t.work(32,true)&&t.amount()==0,"Other liquid drained");t.resetSearch();w.setBlock(40,79,32,Blocks.water,3,2);c.check(!t.work(32,true)&&t.amount()==0,"Flowing water duplicated");remove(w,t);});
        c.test("Connected source cells drain incrementally and the per-machine scan budget is enforced",()->{TileFluidMachine t=tile(w,p,44,80,32,true,5,1,0);w.setBlock(45,80,32,Blocks.water,0,2);w.setBlock(46,80,32,Blocks.water,0,2);c.check(t.work(32,true),"First connected source");later(w);for(int i=0;i<50&&t.amount()<2000;i++){t.work(2,true);c.check(t.lastProbes<=2,"Per-machine budget exceeded");later(w);}c.check(t.amount()==2000&&w.isAirBlock(46,80,32),"Did not follow the connected body");remove(w,t);});
        c.test("The connected search cannot reach a source outside the 16-block radius",()->{TileFluidMachine t=tile(w,p,40,85,48,true,5,1,0);for(int i=1;i<=16;i++)w.setBlock(40+i,85,48,Blocks.water,1,2);w.setBlock(57,85,48,Blocks.water,0,2);for(int i=0;i<100;i++){t.work(32,true);later(w);}c.check(t.amount()==0&&w.getBlock(57,85,48)==Blocks.water,"Search escaped its radius");remove(w,t);});
        c.test("Puller consumes one bucket only for a source placed at its outlet; solid blocks and other liquids are retained",()->{TileFluidMachine t=tile(w,p,48,80,32,false,0,2,2000);w.setBlockToAir(48,79,32);c.check(t.work(32,true)&&t.amount()==1000&&TileFluidMachine.matches(2,w.getBlock(48,79,32)),"Lava output conservation");later(w);w.setBlock(48,79,32,Blocks.stone,0,2);c.check(!t.work(32,true)&&t.amount()==1000&&w.getBlock(48,79,32)==Blocks.stone,"Solid block overwritten");later(w);w.setBlock(48,79,32,Blocks.water,0,2);c.check(!t.work(32,true)&&t.amount()==1000,"Other fluid overwritten");remove(w,t);});
        c.test("Forge protection can cancel collection and placement without consuming/creating fluid",()->{Cancel cancel=new Cancel();MinecraftForge.EVENT_BUS.register(cancel);try{TileFluidMachine source=tile(w,p,52,80,32,true,0,1,0);w.setBlock(52,79,32,Blocks.water,0,2);cancel.drain=true;c.check(!source.work(32,true)&&source.amount()==0&&w.getBlock(52,79,32)==Blocks.water,"Break protection bypass");cancel.drain=false;cancel.place=true;TileFluidMachine sink=tile(w,p,56,80,32,false,0,1,1000);w.setBlockToAir(56,79,32);c.check(!sink.work(32,true)&&sink.amount()==1000&&w.isAirBlock(56,79,32),"Placement cancellation lost fluid or did not roll back");remove(w,source);remove(w,sink);}finally{MinecraftForge.EVENT_BUS.unregister(cancel);}});
        c.test("An event-side tank change is rechecked before consuming the world source",()->{final TileFluidMachine t=tile(w,p,60,80,40,true,0,1,0);w.setBlock(60,79,40,Blocks.water,0,2);Cancel listener=new Cancel();listener.beforeBreak=()->((IFluidUserMK2)t).getAllTanks()[0].setFill(8000);MinecraftForge.EVENT_BUS.register(listener);try{c.check(!t.work(32,true)&&t.amount()==8000&&w.getBlock(60,79,40)==Blocks.water,"Event-side fill lost a source");}finally{MinecraftForge.EVENT_BUS.unregister(listener);remove(w,t);}});
        c.test("Water does not get placed or consumed in a Nether-like dimension",()->{TileFluidMachine t=tile(w,p,60,80,32,false,0,1,1000);w.setBlockToAir(60,79,32);boolean old=w.provider.isHellWorld;try{w.provider.isHellWorld=true;c.check(!t.work(32,true)&&t.amount()==1000&&w.isAirBlock(60,79,32),"Nether water duplication/loss");}finally{w.provider.isHellWorld=old;}remove(w,t);});
        c.test("HBM demand and remainder semantics reject wrong types/pressure and bound enormous offers",()->{TileFluidMachine t=tile(w,p,64,80,32,false,0,1,0);IFluidReceiverMK2 sink=(IFluidReceiverMK2)t;c.check(sink.transferFluid(Fluids.LAVA,0,1000)==1000&&sink.getDemand(Fluids.LAVA,0)==0,"Wrong type accepted");c.check(sink.transferFluid(Fluids.WATER,1,1000)==1000,"Wrong pressure accepted");long left=sink.transferFluid(Fluids.WATER,0,Long.MAX_VALUE);c.check(t.amount()==8000&&left==Long.MAX_VALUE-8000,"Overflow/remainder");c.check(sink.getDemand(Fluids.WATER,0)==0,"Full tank demand");remove(w,t);});
        c.test("Native HBM tank edits share the saved buffer, remain bounded and cannot retune a machine",()->{TileFluidMachine t=tile(w,p,66,80,36,false,0,1,1750);com.hbm.inventory.fluid.tank.FluidTank tank=((IFluidUserMK2)t).getAllTanks()[0];tank.setFill(2250);c.check(t.amount()==2250&&t.contents().getInteger("Amount")==2250,"Native tank and saved contents diverged");t.consume(500);c.check(tank.getFill()==1750,"Provider consumption missed native tank");tank.setTankType(FluidType.getEnumFromName("OIL"));c.check(tank.getTankType()==Fluids.WATER&&t.amount()==1750,"Native tank retuned/converted fluid");tank.setFill(Integer.MAX_VALUE);c.check(t.amount()==8000,"Native tank exceeded capacity");tank.withPressure(1);c.check(tank.getPressure()==0&&t.amount()==8000,"Native tank pressure changed");remove(w,t);});
        c.test("Breaking drops exactly one machine stack retaining the selected fluid and partial buffer",()->{TileFluidMachine t=tile(w,p,68,80,32,false,0,2,1750);List<ItemStack> drops=t.getBlockType().getDrops(w,68,80,32,0,0);c.check(drops.size()==1&&drops.get(0).stackSize==1,"Wrong machine drops");TileFluidMachine copy=FluidMachineRegistry.create(false);copy.readContents(drops.get(0).getTagCompound().getCompoundTag("FluidMachine"));c.check(copy.selected()==2&&copy.amount()==1750,"Dropped buffer lost");remove(w,t);});
        c.test("Boundary searches, pipe checks and liquid-neighbor notifications never force the adjacent chunk",()->{
            int cx=256,cz=256,x=cx*16+15,z=cz*16+7;w.getChunkFromChunkCoords(cx,cz);
            w.setBlock(x,80,z,FluidMachineRegistry.sucker,5,2);TileFluidMachine t=(TileFluidMachine)w.getTileEntity(x,80,z);t.owner(p);contents(t,1,0);fixture.add(t);
            w.theChunkProviderServer.unloadChunksIfNotNearSpawn(cx+1,cz);w.theChunkProviderServer.unloadQueuedChunks();
            c.check(!w.getChunkProvider().chunkExists(cx+1,cz),"Could not unload the fixture neighbor");
            for(int i=0;i<8;i++){t.work(32,true);t.network();later(w);}
            c.check(!w.getChunkProvider().chunkExists(cx+1,cz)&&t.amount()==0,"Search/network loaded an adjacent chunk");remove(w,t);
            TileFluidMachine near=tile(w,p,x-1,81,z,true,0,1,0);w.setBlock(x-1,80,z,Blocks.water,0,2);w.setBlock(x,80,z,Blocks.lava,0,2);
            w.theChunkProviderServer.unloadChunksIfNotNearSpawn(cx+1,cz);w.theChunkProviderServer.unloadQueuedChunks();
            c.check(!w.getChunkProvider().chunkExists(cx+1,cz),"Could not unload the liquid-neighbor fixture");
            Block before=w.getBlock(x-1,80,z);int beforeMeta=w.getBlockMetadata(x-1,80,z);c.check(TileFluidMachine.matches(1,before),"Fixture water disappeared");
            c.check(!near.work(32,true)&&near.amount()==0&&w.getBlock(x-1,80,z)==before&&w.getBlockMetadata(x-1,80,z)==beforeMeta,"Unsafe boundary source was changed");
            c.check(!w.getChunkProvider().chunkExists(cx+1,cz),"Liquid boundary work loaded an adjacent chunk");remove(w,near);
        });
        c.test("One hundred machines share the fixed budgets and client unload events leave the server queue intact",()->{FluidMachineScheduler.clear();List<TileFluidMachine> many=new ArrayList<TileFluidMachine>();for(int i=0;i<100;i++){int x=32+(i%10)*2,z=64+(i/10)*2;TileFluidMachine t=tile(w,p,x,90,z,true,0,1,0);w.setBlock(x,89,z,Blocks.water,0,2);t.updateEntity();many.add(t);}boolean remote=w.isRemote;try{w.isRemote=true;new FluidMachineScheduler().unload(new net.minecraftforge.event.world.WorldEvent.Unload(w));}finally{w.isRemote=remote;}FluidMachineScheduler.step();c.check(FluidMachineScheduler.lastVisits<=64&&FluidMachineScheduler.lastProbes<=256&&FluidMachineScheduler.lastAttempts<=4&&FluidMachineScheduler.lastChanges<=4,"Global work bound exceeded");int buckets=0;for(TileFluidMachine t:many){buckets+=t.amount()/1000;remove(w,t);w.setBlockToAir(t.xCoord,89,t.zCoord);}c.check(buckets>0&&buckets<=4,"Unexpected global bucket throughput");FluidMachineScheduler.clear();});
        selectionChecks(c,w,p);
        // Real pipe items create real HBM network nodes; no stand-in receivers are used.
        for(int kind:new int[]{1,2}){int z=48+kind*4;TileFluidMachine source=tile(w,p,64,80,z,true,0,kind,0),sink=tile(w,p,67,80,z,false,0,kind,0);for(int x=63;x<=68;x++)for(int dz=z-1;dz<=z+1;dz++){w.setBlock(x,78,dz,Blocks.stone,0,2);w.setBlock(x,79,dz,Blocks.stone,0,2);}w.setBlock(64,79,z,kind==1?Blocks.water:Blocks.lava,0,2);w.setBlockToAir(67,79,z);for(int x=65;x<=66;x++){ItemStack pipe=new ItemStack(ModItems.fluid_duct,1,(kind==1?Fluids.WATER:Fluids.LAVA).getID());if(!pipe.tryPlaceItemIntoWorld(p,w,x,79,z,1,.5F,1,.5F))throw new AssertionError("Real HBM pipe placement failed");}source.updateEntity();sink.updateEntity();senders.add(source);receivers.add(sink);}
    }
    static boolean tick(FluidNativeChecks c,WorldServer w){
        waited++;boolean ready=true;for(int i=0;i<receivers.size();i++){TileFluidMachine sink=receivers.get(i);ready&=TileFluidMachine.matches(i+1,w.getBlock(sink.xCoord,sink.yCoord-1,sink.zCoord))&&w.getBlockMetadata(sink.xCoord,sink.yCoord-1,sink.zCoord)==0;}
        if(!ready&&waited<300)return false;
        c.test("Actual HBM pipes carry both collected water and lava into pullers which place real source blocks",()->{for(int i=0;i<receivers.size();i++){TileFluidMachine source=senders.get(i),sink=receivers.get(i);c.check(TileFluidMachine.matches(i+1,w.getBlock(sink.xCoord,sink.yCoord-1,sink.zCoord)),"No pipe-delivered source for kind "+(i+1)+"; source="+source.amount()+" sink="+sink.amount());c.check(source.amount()+sink.amount()==0,"Pipe transfer duplicated or stranded fluid");}});
        for(TileFluidMachine tile:fixture)if(tile.live())remove(w,tile);FluidMachineScheduler.clear();return true;
    }
}
