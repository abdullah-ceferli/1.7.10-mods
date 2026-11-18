package personal.truehorror.server;

import java.util.List;
import java.util.Locale;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import personal.truehorror.*;
import personal.truehorror.core.*;
import personal.truehorror.entity.*;

public final class HorrorCommand extends CommandBase {
    @Override public String getCommandName(){return "truehorror";}
    @Override public String getCommandUsage(ICommandSender sender){return "/truehorror status | event <ID> | spawn <creature> | time <minutes> | clear | gallery | reload [player]";}
    @Override public int getRequiredPermissionLevel(){return 0;}
    @Override public void processCommand(ICommandSender sender,String[] args){
        String action=args.length==0?"status":args[0].toLowerCase(Locale.ROOT);
        if(!action.equals("status")&&!sender.canCommandSenderUseCommand(2,getCommandName()))throw new CommandException("Operator permission required.");
        if(action.equals("reload")){HorrorConfig.reload();sender.addChatMessage(new ChatComponentText("True Horror configuration reloaded."));return;}
        int targetIndex=action.equals("event")||action.equals("spawn")||action.equals("time")?2:1;
        EntityPlayerMP player=args.length>targetIndex&&sender.canCommandSenderUseCommand(2,getCommandName())?getPlayer(sender,args[targetIndex]):getCommandSenderAsPlayer(sender);
        PlayerRecord record=TrueHorror.director.record(player);
        if(action.equals("status")){
            sender.addChatMessage(new ChatComponentText("True Horror: "+record.progress.activeTicks/1200+" active minutes; stage "+record.progress.stage(HorrorConfig.firstMinutes,HorrorConfig.progressionSpeed)
                    +"; encounters "+record.progress.encounters+"; major encounters begin after "+HorrorConfig.firstMinutes+" minutes."));return;
        }
        if(action.equals("time")&&args.length>=2){
            record.progress.activeTicks=parseIntBounded(sender,args[1],0,100000)*1200L;record.progress.nextEvent=0;record.save(player);
            sender.addChatMessage(new ChatComponentText("True Horror active time updated for "+player.getCommandSenderName()+"."));return;
        }
        if(action.equals("clear")){
            List entities=player.worldObj.getEntitiesWithinAABB(DreadEntity.class,player.boundingBox.expand(96,64,96));
            for(Object entity:entities)((DreadEntity)entity).setDead();sender.addChatMessage(new ChatComponentText("Cleared "+entities.size()+" nearby horror entities."));return;
        }
        if(action.equals("gallery")){
            int created=0;
            for(CreatureKind kind:CreatureKind.values()){
                double x=player.posX+(kind.ordinal()-3)*4,z=player.posZ+9;
                if(!SafeSpawns.loaded(player.worldObj,(int)Math.floor(x),(int)Math.floor(z)))continue;
                DreadEntity entity=Monsters.create(kind,player.worldObj);entity.setLocationAndAngles(x,player.posY,z,180,0);
                entity.bind(player,0,0,6000);entity.gallery(true);
                if(player.worldObj.getCollidingBoundingBoxes(entity,entity.boundingBox).isEmpty()&&player.worldObj.spawnEntityInWorld(entity))created++;
            }
            sender.addChatMessage(new ChatComponentText("Created "+created+" temporary, non-attacking gallery models. /truehorror clear removes them."));return;
        }
        Environment env=new Environment(player,record);
        if(action.equals("spawn")&&args.length>=2){
            CreatureKind kind=CreatureKind.fromId(args[1].toLowerCase(Locale.ROOT));if(kind==null)throw new CommandException("Unknown creature. herobrine, watcher, mimic, crawler, pale_man, shadow, dweller");
            boolean done=EventEngine.spawn(player,record,env,kind,1,10,20,true,-1,true);
            sender.addChatMessage(new ChatComponentText(done?"Encounter created.":"No suitable loaded space, creature disabled, or encounter cap reached."));return;
        }
        if(action.equals("event")&&args.length>=2){
            EventType type;try{type=EventType.valueOf(args[1].toUpperCase(Locale.ROOT));}catch(IllegalArgumentException ex){throw new CommandException("Unknown event ID; see EVENTS.md.");}
            boolean done=EventEngine.execute(player,record,env,type,true);
            sender.addChatMessage(new ChatComponentText(done?"Triggered "+type.name()+".":"Event needs suitable loaded terrain, enabled world changes, animals, or encounter space."));return;
        }
        throw new WrongUsageException(getCommandUsage(sender));
    }
    @Override public List addTabCompletionOptions(ICommandSender sender,String[] args){
        if(args.length==1)return getListOfStringsMatchingLastWord(args,"status","event","spawn","time","clear","gallery","reload");
        if(args.length==2&&args[0].equals("spawn"))return getListOfStringsMatchingLastWord(args,"herobrine","watcher","mimic","crawler","pale_man","shadow","dweller");
        if(args.length==2&&args[0].equals("event")){
            String[] values=new String[EventType.values().length];for(int i=0;i<values.length;i++)values[i]=EventType.values()[i].name();
            return getListOfStringsMatchingLastWord(args,values);
        }return null;
    }
}
