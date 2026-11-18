package personal.truehorror.server;

import net.minecraft.entity.player.EntityPlayerMP;
import personal.truehorror.HorrorConfig;
import personal.truehorror.TrueHorror;
import personal.truehorror.network.EffectMessage;
import personal.truehorror.core.ScarePolicy;

public final class Scares {
    public static boolean trigger(EntityPlayerMP player,int style,boolean force){
        return trigger(player,style,force,false);
    }
    public static boolean scheduled(EntityPlayerMP player,int style,boolean force){
        return trigger(player,style,force,true);
    }
    private static boolean trigger(EntityPlayerMP player,int style,boolean force,boolean alreadySelected){
        PlayerRecord record=TrueHorror.director.record(player);
        if(!force&&!ScarePolicy.allowed(record.progress,HorrorConfig.scareFrequency,alreadySelected,player.getRNG()))return false;
        record.progress.nextScare=record.progress.activeTicks+HorrorConfig.scareCooldownSeconds*20L+player.getRNG().nextInt(1201);
        record.save(player);
        String[] sounds={"sting_behind","sting_mining","sting_rush","sting_window"};
        EventEngine.sound(player,sounds[Math.floorMod(style,4)],player.posX,player.posY,player.posZ,.8F);
        TrueHorror.network.sendTo(new EffectMessage(1,55,(float)HorrorConfig.intensity),player);
        return true;
    }
}
