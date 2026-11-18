package personal.truehorror.core;
import java.util.Random;
public final class ScarePolicy {
    public static boolean allowed(PlayerProgress player,double frequency,boolean alreadySelected,Random random){
        return frequency>0&&player.activeTicks>=player.nextScare&&(alreadySelected||random.nextDouble()<frequency);
    }
}
