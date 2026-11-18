package personal.truehorror.server;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import personal.truehorror.*;
import personal.truehorror.core.*;
import personal.truehorror.entity.*;
import personal.truehorror.network.EffectMessage;

public final class EventEngine {
    public static void sound(EntityPlayerMP player,String sound,double x,double y,double z,float gain){
        if(HorrorConfig.soundEffects)player.worldObj.playSoundEffect(x,y,z,"truehorror:"+sound,(float)(gain*HorrorConfig.volume),.95F+player.getRNG().nextFloat()*.1F);
    }
    public static boolean execute(EntityPlayerMP player,PlayerRecord record,Environment env,EventType event,boolean force){
        World world=player.worldObj;double angle=Math.toRadians(player.rotationYaw);
        double bx=player.posX+Math.sin(angle)*7,bz=player.posZ-Math.cos(angle)*7;
        if(event.scare&&!force&&record.progress.activeTicks<record.progress.nextScare)return false;
        switch(event){
            case FOOTSTEPS:sound(player,"footsteps",bx,player.posY,bz,.65F);return true;
            case BRANCH_SNAP:sound(player,"branches",bx+4,player.posY+3,bz,.65F);return true;
            case CAVE_BREATH:sound(player,world.rand.nextBoolean()?"breathing":"tunnel_echo",bx,player.posY,bz,.6F);return true;
            case TUNNEL_MINING:sound(player,"mining",bx+8,player.posY-2,bz,.8F);return true;
            case WHISPERS:sound(player,"whispers",bx,player.posY+1,bz,.65F);return true;
            case DISTANT_WAIL:sound(player,"distant_scream",player.posX+16,player.posY+2,player.posZ-16,.9F);return true;
            case SHELTER_KNOCK:sound(player,"knocking",bx,player.posY+1,bz,.8F);return true;
            case GHOST_LAUGHTER:sound(player,"laughter",bx+3,player.posY,bz-3,.6F);return true;
            case WAKE_ECHO:sound(player,"wake",bx,player.posY,bz,.6F);return true;
            case TORCH_FLICKER:
                net.minecraft.util.ChunkCoordinates torch=Nearby.find(world,env.x,env.y,env.z,6,-1,3,(block,meta,x,y,z)->block==Blocks.torch);
                if(torch==null)return false;
                TrueHorror.network.sendTo(new EffectMessage(2,75,.5F),player);sound(player,"torch",torch.posX,torch.posY,torch.posZ,.35F);return true;
            case ANIMAL_STARE:case ANIMAL_HUSH:
                List animals=world.getEntitiesWithinAABB(EntityAnimal.class,player.boundingBox.expand(12,5,12));
                int affected=0;
                for(Object value:animals){
                    EntityAnimal animal=(EntityAnimal)value;
                    if(event==EventType.ANIMAL_HUSH)animal.getEntityData().setLong("TrueHorrorHushUntil",world.getTotalWorldTime()+200);
                    else{animal.getEntityData().setLong("TrueHorrorStareUntil",world.getTotalWorldTime()+160);animal.getEntityData().setString("TrueHorrorStareAt",player.getCommandSenderName());}
                    if(++affected==6)break;
                }
                if(affected>0&&event==EventType.ANIMAL_HUSH)TrueHorror.network.sendTo(new EffectMessage(3,100,.2F),player);
                return affected>0;
            case HILL_SIGHTING:return spawn(player,record,env,CreatureKind.HEROBRINE,0,24,40,false,-1,force);
            case TREE_SENTINEL:return spawn(player,record,env,CreatureKind.WATCHER,1,18,30,true,0,force);
            case WINDOW_VISITOR:
                DreadEntity visitor=Monsters.create(CreatureKind.HEROBRINE,world);
                if(!HorrorConfig.creatures[0]||!TrueHorror.director.canEncounter(player)||SafeSpawns.window(player,visitor)==null)return false;
                visitor.bind(player,0,record.progress.stage(HorrorConfig.firstMinutes,HorrorConfig.progressionSpeed),400);
                if(!world.spawnEntityInWorld(visitor))return false;Scares.scheduled(player,3,force);return true;
            case BED_SENTINEL:return spawn(player,record,env,CreatureKind.HEROBRINE,0,8,12,true,-1,force);
            case SILHOUETTE:return spawn(player,record,env,CreatureKind.PALE_MAN,0,22,32,false,-1,force);
            case STALKER_TRAIL:
                record.progress.warnings++;
                int behavior=record.progress.warnings>=3&&record.progress.caution<2&&record.progress.stage(HorrorConfig.firstMinutes,HorrorConfig.progressionSpeed)>=2?2:1;
                return spawn(player,record,env,CreatureKind.HEROBRINE,behavior,16,26,true,-1,force);
            case CAVE_ENTRANCE_MOVEMENT:return spawn(player,record,env,CreatureKind.CRAWLER,1,12,22,false,-1,force);
            case CORRIDOR_LURE:return spawn(player,record,env,CreatureKind.MIMIC,1,14,24,true,-1,force);
            case SHADOW_CROSSING:return spawn(player,record,env,CreatureKind.SHADOW,1,8,14,false,-1,force);
            case MINING_APPARITION:return spawn(player,record,env,CreatureKind.HEROBRINE,0,7,11,false,1,force);
            case RUSH_FEINT:
                boolean rushed=spawn(player,record,env,CreatureKind.HEROBRINE,3,15,22,false,2,force);
                if(rushed)sound(player,"running",bx,player.posY,bz,.65F);return rushed;
            case DARKENED_APPROACH:return spawn(player,record,env,CreatureKind.PALE_MAN,1,9,15,false,0,force);
            case RARE_CHASE:return spawn(player,record,env,env.has(EventType.CAVE)?CreatureKind.DWELLER:CreatureKind.PALE_MAN,2,16,24,true,2,force);
            case DOOR_OPEN:case MYSTERY_SIGN:case LEAF_TRACE:case WILDERNESS_CAIRN:case SHALLOW_TUNNEL:
                return modify(player,env,event);
            default:return false;
        }
    }
    public static boolean spawn(EntityPlayerMP player,PlayerRecord record,Environment env,CreatureKind kind,int mode,int min,int max,boolean behind,int scare,boolean force){
        if(!HorrorConfig.creatures[kind.ordinal()]||!TrueHorror.director.canEncounter(player))return false;
        if(!HorrorConfig.allowDaylight&&!env.has(EventType.NIGHT)&&!env.has(EventType.CAVE))return false;
        DreadEntity entity=Monsters.create(kind,player.worldObj);
        Vec3 position=SafeSpawns.find(player,entity,min,max,behind,env.has(EventType.CAVE)||env.has(EventType.INDOOR));
        if(position==null)return false;
        int progression=record.progress.stage(HorrorConfig.firstMinutes,HorrorConfig.progressionSpeed);
        entity.bind(player,mode,progression,HorrorConfig.lifetimeSeconds*20);
        if(!player.worldObj.spawnEntityInWorld(entity))return false;
        if(scare>=0)Scares.scheduled(player,scare,force);
        return true;
    }
    private static boolean modify(EntityPlayerMP player,Environment env,EventType event){
        if(!HorrorConfig.worldChanges)return false;
        World world=player.worldObj;WorldEdits edits=WorldEdits.get(world);
        net.minecraft.util.ChunkCoordinates point;
        if(event==EventType.DOOR_OPEN||event==EventType.LEAF_TRACE){
            point=Nearby.find(world,env.x,env.y,env.z,8,-2,3,(block,meta,x,y,z)->
                event==EventType.DOOR_OPEN?block==Blocks.wooden_door&&(meta&12)==0:(block==Blocks.leaves||block==Blocks.leaves2)&&(meta&4)==0);
            if(point==null)return false;
            Block block=world.getBlock(point.posX,point.posY,point.posZ);int meta=world.getBlockMetadata(point.posX,point.posY,point.posZ);
            return edits.apply(world,point.posX,point.posY,point.posZ,event==EventType.DOOR_OPEN?block:Blocks.air,event==EventType.DOOR_OPEN?meta|4:0,event==EventType.DOOR_OPEN?5:60);
        }
        if(event==EventType.SHALLOW_TUNNEL){
            if(edits.pending()>58)return false;
            point=Nearby.find(world,env.x,env.y,env.z,9,-2,2,(block,meta,x,y,z)->{
                if(block!=Blocks.stone||!SafeSpawns.loaded(world,x-1,z-1)||!SafeSpawns.loaded(world,x-1,z+1)
                        ||!SafeSpawns.loaded(world,x+3,z-1)||!SafeSpawns.loaded(world,x+3,z+1)||!world.isAirBlock(x-1,y,z))return false;
                for(int dx=0;dx<3;dx++)for(int dy=0;dy<2;dy++){
                    if(world.getBlock(x+dx,y+dy,z)!=Blocks.stone||world.getTileEntity(x+dx,y+dy,z)!=null)return false;
                    int[][] neighbors={{1,0,0},{-1,0,0},{0,1,0},{0,-1,0},{0,0,1},{0,0,-1}};
                    for(int[] side:neighbors)if(world.getBlock(x+dx+side[0],y+dy+side[1],z+side[2]).getMaterial().isLiquid())return false;
                }return true;
            });
            if(point==null)return false;
            for(int dx=0;dx<3;dx++)for(int dy=0;dy<2;dy++)edits.apply(world,point.posX+dx,point.posY+dy,point.posZ,Blocks.air,0,45);
            return true;
        }
        if(event!=EventType.MYSTERY_SIGN&&event!=EventType.WILDERNESS_CAIRN)return false;
        if(event==EventType.WILDERNESS_CAIRN&&edits.pending()>61)return false;
        point=Nearby.find(world,env.x,env.y,env.z,10,-2,2,(block,meta,x,y,z)->{
            if(!world.isAirBlock(x,y,z)||player.getDistanceSq(x,y,z)<=36)return false;
            Block floor=world.getBlock(x,y-1,z);
            return (floor==Blocks.grass||floor==Blocks.dirt||floor==Blocks.stone)
                    &&(event!=EventType.WILDERNESS_CAIRN||world.isAirBlock(x,y+1,z)&&world.isAirBlock(x,y+2,z));
        });
        if(point==null)return false;
        int x=point.posX,y=point.posY,z=point.posZ;
        if(event==EventType.MYSTERY_SIGN&&edits.apply(world,x,y,z,Blocks.standing_sign,world.rand.nextInt(16),180)){
            TileEntitySign sign=(TileEntitySign)world.getTileEntity(x,y,z);
            if(sign!=null){String[] lines={"you looked away","I remember","not alone","behind you"};sign.signText[1]=lines[world.rand.nextInt(lines.length)];sign.markDirty();world.markBlockForUpdate(x,y,z);edits.refreshExpected(world,x,y,z);}
            return true;
        }
        if(event==EventType.WILDERNESS_CAIRN&&edits.apply(world,x,y,z,Blocks.mossy_cobblestone,0,180)){
            edits.apply(world,x,y+1,z,Blocks.cobblestone,0,180);edits.apply(world,x,y+2,z,Blocks.redstone_torch,5,180);return true;
        }
        return false;
    }
}
