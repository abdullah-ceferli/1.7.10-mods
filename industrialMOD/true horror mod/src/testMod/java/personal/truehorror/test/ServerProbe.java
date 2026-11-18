package personal.truehorror.test;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;
import personal.truehorror.*;
import personal.truehorror.core.*;
import personal.truehorror.entity.*;
import personal.truehorror.network.EffectMessage;
import personal.truehorror.server.*;

public final class ServerProbe extends Probe {
    private int stage,ticks;
    private DreadEntity mover,watcher;
    private double watcherX,watcherZ;
    @SubscribeEvent public void login(PlayerEvent.PlayerLoggedInEvent event){
        EntityPlayerMP player=(EntityPlayerMP)event.player;
        player.setSpawnChunk(new ChunkCoordinates(0,4,-6),true);player.setPositionAndUpdate(player.getCommandSenderName().endsWith("B")?2.5:.5,4,-6.5);
        player.theItemInWorldManager.setGameType(net.minecraft.world.WorldSettings.GameType.CREATIVE);
        try{
            if(has("world-left")&&player.getCommandSenderName().endsWith("A")){
                check(TrueHorror.director.record(player).progress.activeTicks==14321,"Active time survives world reload/reconnect");
                check(TrueHorror.director.record(player).progress.nextScare==20000,"Scare cooldown survives reload");mark("reload-persistence");
            }
        }catch(Throwable ex){failed(ex);}
    }
    @SubscribeEvent public void respawn(PlayerEvent.PlayerRespawnEvent event){
        try{check(TrueHorror.director.record(event.player).progress.activeTicks==14321,"Progress survives death/respawn");mark("respawn-persistence");}
        catch(Throwable ex){failed(ex);}
    }
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END||++ticks%5!=0||has("FAILED"))return;
        MinecraftServer server=MinecraftServer.getServer();if(server==null||server.getConfigurationManager().playerEntityList.isEmpty())return;
        EntityPlayerMP player=(EntityPlayerMP)server.getConfigurationManager().playerEntityList.get(0);
        WorldServer world=(WorldServer)player.worldObj;
        try{
            if(stage==0&&ticks>50){
                if(Boolean.getBoolean("horror.test.two")&&server.getConfigurationManager().playerEntityList.size()<2)return;
                check(!HorrorConfig.worldChanges,"Block changes default off");
                int chunks=world.getChunkProvider().getLoadedChunkCount();
                check(!SafeSpawns.loadedArea(world,1000000,1000000,20),"Far-away chunks are absent");
                DreadEntity guard=Monsters.create(CreatureKind.MIMIC,world);guard.setLocationAndAngles(1000000,4,1000000,0,0);
                java.lang.reflect.Method move=DreadEntity.class.getDeclaredMethod("moveTowards",double.class,double.class,double.class,double.class);move.setAccessible(true);
                move.invoke(guard,1000008D,4D,1000000D,1D);
                check(world.getChunkProvider().getLoadedChunkCount()==chunks,"Navigation guard never generates or loads missing chunks");
                mark("no-forced-chunk-loading");
                PlayerRecord record=TrueHorror.director.record(player);
                check(record.progress.activeTicks==0,"New player starts with grace period");
                record.progress.activeTicks=14321;record.progress.nextScare=20000;record.progress.nextEvent=21000;record.save(player);
                PlayerRecord read=new PlayerRecord(player);check(read.progress.activeTicks==14321&&read.progress.nextScare==20000,"Per-player NBT stores progress and cooldown");
                if(server.getConfigurationManager().playerEntityList.size()>1){
                    EntityPlayerMP other=(EntityPlayerMP)server.getConfigurationManager().playerEntityList.get(1);
                    check(TrueHorror.director.record(other).progress.activeTicks==0,"Second player's progression stays independent");mark("two-player-progression");
                }
                world.setBlock(10,4,1,Blocks.stone);WorldEdits edits=WorldEdits.get(world);
                check(!edits.apply(world,10,4,1,Blocks.air,0,1)&&world.getBlock(10,4,1)==Blocks.stone,"Default protection rejects edits");
                HorrorConfig.worldChanges=true;
                check(edits.apply(world,10,4,1,Blocks.air,0,1),"Optional edit applied");
                world.setBlock(11,4,1,Blocks.stone);check(edits.apply(world,11,4,1,Blocks.air,0,1),"Second optional edit applied");
                world.setBlock(11,4,1,Blocks.brick_block);
                world.setBlock(12,4,1,Blocks.chest);TileEntityChest chest=(TileEntityChest)world.getTileEntity(12,4,1);
                chest.setInventorySlotContents(0,new ItemStack(Items.diamond,3));
                check(!edits.apply(world,12,4,1,Blocks.air,0,1)&&chest.getStackInSlot(0).stackSize==3,"Container and stored items protected");
                NBTTagCompound saved=new NBTTagCompound();edits.writeToNBT(saved);WorldEdits restored=new WorldEdits();restored.readFromNBT(saved);check(restored.pending()==2,"Undo transactions serialize");
                HorrorConfig.worldChanges=false;
                world.setBlock(1,4,-3,Blocks.torch);
                check(EventEngine.execute(player,record,new Environment(player,record),EventType.TORCH_FLICKER,true),"Real nearby torch event executes");
                stage=1;ticks=0;
            }else if(stage==1&&ticks>=40){
                check(world.getBlock(10,4,1)==Blocks.stone,"Expired edit restored original block");
                check(world.getBlock(11,4,1)==Blocks.brick_block,"Undo preserves subsequent player edits");
                eventCatalogue(world,player);
                for(CreatureKind kind:CreatureKind.values()){
                    DreadEntity entity=Monsters.create(kind,world);entity.setLocationAndAngles((kind.ordinal()-3)*3,4,9,180,0);
                    entity.bind(player,0,0,6000);entity.gallery(true);check(world.spawnEntityInWorld(entity),"Entity registered/spawned: "+kind.id);
                }
                check(!TrueHorror.director.canEncounter(player),"Region encounter cap blocks overlapping spawns");
                mark("server-checks");stage=2;
            }else if(stage==2&&has("request-effect")){
                for(Object value:world.getEntitiesWithinAABB(DreadEntity.class,player.boundingBox.expand(64,32,64)))((DreadEntity)value).setDead();
                world.setWorldTime(18000);
                mover=Monsters.create(CreatureKind.MIMIC,world);mover.setLocationAndAngles(player.posX+12,player.posY,player.posZ,90,0);mover.bind(player,2,2,400);world.spawnEntityInWorld(mover);
                watcher=Monsters.create(CreatureKind.WATCHER,world);watcherX=player.posX;watcherZ=player.posZ+10;
                watcher.setLocationAndAngles(watcherX,player.posY,watcherZ,180,0);watcher.bind(player,1,2,400);world.spawnEntityInWorld(watcher);
                for(Object value:server.getConfigurationManager().playerEntityList){EntityPlayerMP p=(EntityPlayerMP)value;p.playerNetServerHandler.setPlayerLocation(p.posX,p.posY,p.posZ,0,0);}
                for(Object value:server.getConfigurationManager().playerEntityList)
                    TrueHorror.network.sendTo(new EffectMessage(1,55,.65F),(EntityPlayerMP)value);
                mark("effect-sent");stage=3;ticks=0;
            }else if(stage==3&&ticks>=40){
                check(mover.getDistanceToEntity(player)<10,"Hunting creature actually navigates toward owner");
                check(watcher.getDistanceSq(watcherX,watcher.posY,watcherZ)<.15,"Watched Watcher remains still");
                for(Object value:server.getConfigurationManager().playerEntityList){EntityPlayerMP p=(EntityPlayerMP)value;p.playerNetServerHandler.setPlayerLocation(p.posX,p.posY,p.posZ,180,0);}
                stage=4;ticks=0;
            }else if(stage==4&&ticks>=40){
                check(watcher.getDistanceSq(watcherX,watcher.posY,watcherZ)>.3,"Watcher moves after players look away");
                mark("navigation-gaze-checks");stage=5;
            }
        }catch(Throwable ex){failed(ex);}
    }
    private void eventCatalogue(WorldServer world,EntityPlayerMP player)throws Exception{
        world.setBlock(4,5,-6,Blocks.glass);
        world.setBlock(-3,4,-6,Blocks.wooden_door,0,3);world.setBlock(-3,5,-6,Blocks.wooden_door,8,3);
        world.setBlock(-4,6,-6,Blocks.leaves,0,3);
        for(int dx=7;dx<10;dx++)for(int dy=4;dy<6;dy++)for(int dz=-8;dz<-6;dz++)world.setBlock(dx,dy,dz,Blocks.stone);
        net.minecraft.entity.passive.EntityCow cow=new net.minecraft.entity.passive.EntityCow(world);cow.setLocationAndAngles(2,4,-5,0,0);world.spawnEntityInWorld(cow);
        PlayerRecord record=TrueHorror.director.record(player);long scare=record.progress.nextScare;
        HorrorConfig.worldChanges=true;
        for(EventType event:EventType.values()){
            for(Object value:world.getEntitiesWithinAABB(DreadEntity.class,player.boundingBox.expand(64,32,64)))((DreadEntity)value).setDead();
            check(EventEngine.execute(player,record,new Environment(player,record),event,true),"Functional event: "+event.name());
        }
        net.minecraftforge.event.entity.PlaySoundAtEntityEvent hush=new net.minecraftforge.event.entity.PlaySoundAtEntityEvent(cow,"mob.cow.say",1,1);
        check(net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(hush),"Animal hush actually suppresses animal sound");
        for(Object value:world.getEntitiesWithinAABB(DreadEntity.class,player.boundingBox.expand(64,32,64)))((DreadEntity)value).setDead();
        HorrorConfig.worldChanges=false;record.progress.nextScare=scare;record.save(player);mark("all-30-events");
    }
}
