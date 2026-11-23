package dev.industrialtechnology.test;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dev.industrialtechnology.fluid.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.*;
import net.minecraftforge.common.util.FakePlayer;

@Mod(modid="industrialtech_fluid_checks",name="Fluid machine native checks",version="1",dependencies="required-after:industrialtech")
public final class FluidNativeChecks {
    private final List<String> report=new ArrayList<String>();
    private int ticks,failed;private boolean started,done;
    private WorldServer world;private FakePlayer player;
    @Mod.EventHandler public void init(FMLInitializationEvent e){if(Boolean.getBoolean("industrialtech.fluidTest"))FMLCommonHandler.instance().bus().register(this);}
    void check(boolean value,String text){if(!value)throw new AssertionError(text);}
    void test(String text,Runnable action){try{action.run();report.add("PASS: "+text);}catch(Throwable error){failed++;report.add("FAIL: "+text+" :: "+error);error.printStackTrace();}}
    private TileFluidMachine tile(int x,int z,boolean sucker,int fluid,int amount){world.setBlock(x,80,z,sucker?FluidMachineRegistry.sucker:FluidMachineRegistry.puller,0,3);TileFluidMachine tile=(TileFluidMachine)world.getTileEntity(x,80,z);tile.owner(player);NBTTagCompound data=new NBTTagCompound();data.setByte("Selected",(byte)fluid);data.setInteger("Amount",amount);tile.readContents(data);return tile;}
    private void setup(){
        world=MinecraftServer.getServer().worldServerForDimension(0);
        player=new FakePlayer(world,new com.mojang.authlib.GameProfile(UUID.fromString("954be44e-cea4-4b2c-a09f-62d191ad45f2"),"FluidChecks")){public void sendPlayerAbilities(){}public void addChatMessage(IChatComponent text){}};
        player.playerNetServerHandler=new net.minecraft.network.NetHandlerPlayServer(MinecraftServer.getServer(),new net.minecraft.network.NetworkManager(false),player){@Override public void sendPacket(net.minecraft.network.Packet packet){}};
        player.theItemInWorldManager.setGameType(WorldSettings.GameType.SURVIVAL);player.setPosition(40,81,40);
        for(int x=1;x<=6;x++)for(int z=1;z<=6;z++)world.getChunkFromChunkCoords(x,z);
        test("Both blocks are registered, inactive by default and expose no GUI to an empty hand",()->{
            for(boolean sucker:new boolean[]{true,false}){TileFluidMachine t=tile(24,sucker?24:28,sucker,0,0);world.setBlock(24,79,t.zCoord,Blocks.water,0,2);check(t.selected()==0&&!t.work(32,true)&&t.amount()==0,"Unconfigured machine collected fluid");player.inventory.setInventorySlotContents(0,null);Object open=player.openContainer;check(!t.getBlockType().onBlockActivated(world,t.xCoord,80,t.zCoord,player,1,.5F,.5F,.5F)&&open==player.openContainer,"Unexpected GUI");world.setBlockToAir(t.xCoord,80,t.zCoord);}
        });
        test("Vanilla NBT clamps corrupt amounts and preserves valid fluid/amount/owner without HBM IDs",()->{TileFluidMachine t=tile(25,32,true,1,1750);NBTTagCompound n=new NBTTagCompound();t.writeToNBT(n);TileFluidMachine copy=FluidMachineRegistry.create(true);copy.readFromNBT(n);check(copy.selected()==1&&copy.amount()==1750&&copy.ownerProfile().getId().equals(player.getUniqueID()),"NBT contents/owner lost");n.getCompoundTag("FluidMachine").setInteger("Amount",Integer.MAX_VALUE);copy.readFromNBT(n);check(copy.amount()==8000,"Oversized NBT amount");n.getCompoundTag("FluidMachine").setByte("Selected",(byte)127);copy.readFromNBT(n);check(copy.selected()==0&&copy.amount()==0,"Unknown fluid accepted");world.setBlockToAir(25,80,32);});
        if(Loader.isModLoaded("hbm"))FluidHbmChecks.setup(this,world,player);
        else{
            test("No-HBM runtime links safely and cannot activate from water/lava buckets",()->{TileFluidMachine t=tile(26,32,true,1,0);world.setBlock(26,79,32,Blocks.lava,0,2);check(!t.work(32,true)&&t.amount()==0,"No-HBM machine ran");check(!FluidMachineRegistry.identifier(new ItemStack(net.minecraft.init.Items.water_bucket)),"Bucket accepted as identifier");world.setBlockToAir(26,80,32);});
            save();finish();
        }
    }
    private void save(){
        TileFluidMachine persistent=tile(90,90,false,2,1750);world.setBlock(90,79,90,Blocks.stone,0,2);persistent.markDirty();
        try{world.saveAllChunks(true,null);Files.write(Paths.get("fluid-saved.done"),Arrays.asList("saved"),StandardCharsets.UTF_8);}catch(Exception e){throw new RuntimeException(e);}
    }
    private void restart(){test("A fresh JVM preserves the configured machine type, owner and partial bucket buffer",()->{TileFluidMachine t=(TileFluidMachine)world.getTileEntity(90,80,90);check(t!=null&&!t.isSucker()&&t.selected()==2&&t.amount()==1750&&t.hasOwner(),"World reload lost fluid state");});finish();}
    private void finish(){done=true;report.add("Failed: "+failed);String phase=Files.exists(Paths.get("fluid-restarted.marker"))?"restart":"initial";try{Files.write(Paths.get("fluids-"+phase+".txt"),report,StandardCharsets.UTF_8);}catch(Exception e){throw new RuntimeException(e);}MinecraftServer.getServer().initiateShutdown();}
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent e){if(done||e.phase!=TickEvent.Phase.END||!MinecraftServer.getServer().isDedicatedServer())return;try{
        if(!started){if(++ticks<25)return;started=true;if(Files.exists(Paths.get("fluid-saved.done"))){world=MinecraftServer.getServer().worldServerForDimension(0);world.getChunkFromChunkCoords(90>>4,90>>4);Files.write(Paths.get("fluid-restarted.marker"),Arrays.asList("restart"),StandardCharsets.UTF_8);restart();}else setup();return;}
        if(Loader.isModLoaded("hbm")&&FluidHbmChecks.tick(this,world)){save();finish();}
    }catch(Throwable t){failed++;report.add("FAIL: fixture :: "+t);t.printStackTrace();finish();}}
}
