package dev.industrialtechnology.fluid;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.*;
import net.minecraftforge.event.world.BlockEvent;

/** Permission events and state rechecks precede every paid world mutation. */
final class FluidWorld {
    private static boolean neighborhood(WorldServer world,int x,int y,int z){
        if(!TileFluidMachine.loaded(world,x,y,z))return false;
        // Liquid neighbors can inspect their own neighbors during vanilla notifications.
        // Require that two-block margin without asking the provider to load any chunk.
        for(int cx=(x-2)>>4;cx<=((x+2)>>4);cx++)for(int cz=(z-2)>>4;cz<=((z+2)>>4);cz++)if(!world.getChunkProvider().chunkExists(cx,cz))return false;
        return true;
    }
    private static FakePlayer actor(TileFluidMachine tile,int x,int y,int z){FakePlayer p=FakePlayerFactory.get((WorldServer)tile.getWorldObj(),tile.ownerProfile());p.setPosition(x+.5,y+.5,z+.5);return p;}
    private static boolean allowed(WorldServer world,FakePlayer player,int x,int y,int z){return world.canMineBlock(player,x,y,z)&&!MinecraftServer.getServer().isBlockProtected(world,x,y,z,player);}
    static boolean drain(TileFluidMachine tile,int x,int y,int z,Block expected){
        WorldServer world=(WorldServer)tile.getWorldObj();if(!neighborhood(world,x,y,z))return false;
        FakePlayer p=actor(tile,x,y,z);if(!allowed(world,p,x,y,z))return false;
        BlockEvent.BreakEvent event=new BlockEvent.BreakEvent(x,y,z,world,expected,0,p);if(MinecraftForge.EVENT_BUS.post(event))return false;
        if(!tile.live()||!TileFluidMachine.matches(tile.selected(),expected)||world.getBlock(x,y,z)!=expected||world.getBlockMetadata(x,y,z)!=0||world.getTileEntity(x,y,z)!=null||tile.amount()>TileFluidMachine.CAPACITY-TileFluidMachine.BUCKET)return false;
        if(!world.setBlock(x,y,z,Blocks.air,0,2))return false;
        tile.amount(tile.amount()+TileFluidMachine.BUCKET);
        world.notifyBlocksOfNeighborChange(x,y,z,expected);return true;
    }
    static boolean place(TileFluidMachine tile,int x,int y,int z){
        WorldServer world=(WorldServer)tile.getWorldObj();if(!neighborhood(world,x,y,z)||tile.selected()==TileFluidMachine.WATER&&world.provider.isHellWorld)return false;
        Block old=world.getBlock(x,y,z);int meta=world.getBlockMetadata(x,y,z);
        if(old!=Blocks.air&&!(TileFluidMachine.matches(tile.selected(),old)&&meta!=0)||world.getTileEntity(x,y,z)!=null)return false;
        FakePlayer p=actor(tile,x,y,z);if(!allowed(world,p,x,y,z))return false;
        int selection=tile.selected();BlockSnapshot before=BlockSnapshot.getBlockSnapshot(world,x,y,z);Block fluid=selection==TileFluidMachine.WATER?Blocks.flowing_water:Blocks.flowing_lava;
        if(!world.setBlock(x,y,z,fluid,0,2))return false;
        BlockEvent.PlaceEvent event=new BlockEvent.PlaceEvent(before,tile.getBlockType(),p);boolean cancel= MinecraftForge.EVENT_BUS.post(event);
        if(cancel){boolean restoring=world.restoringBlockSnapshots;world.restoringBlockSnapshots=true;try{before.restore(true,false);}finally{world.restoringBlockSnapshots=restoring;}return false;}
        // A handler may replace our source, or remove/reconfigure the machine. Pay only for an accepted source.
        if(!tile.live()||tile.selected()!=selection||tile.amount()<TileFluidMachine.BUCKET||!TileFluidMachine.matches(selection,world.getBlock(x,y,z))||world.getBlockMetadata(x,y,z)!=0){boolean restoring=world.restoringBlockSnapshots;world.restoringBlockSnapshots=true;try{before.restore(true,false);}finally{world.restoringBlockSnapshots=restoring;}return false;}
        tile.consume(TileFluidMachine.BUCKET);
        world.markBlockForUpdate(x,y,z);world.notifyBlocksOfNeighborChange(x,y,z,fluid);return true;
    }
    private FluidWorld(){}
}
