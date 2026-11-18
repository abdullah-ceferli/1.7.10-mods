package personal.truehorror.core;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;
public class ScarePolicyTest {
    @Test public void disabledStaysDisabledAndCooldownAppliesToAllTriggers(){
        PlayerProgress p=new PlayerProgress();Random r=new Random(3);
        assertFalse(ScarePolicy.allowed(p,0,true,r));assertTrue(ScarePolicy.allowed(p,1,false,r));
        p.nextScare=1;assertFalse(ScarePolicy.allowed(p,1,false,r));assertFalse(ScarePolicy.allowed(p,1,true,r));
    }
    @Test public void configuredChanceControlsCombatScaresWithoutDoubleGatingScheduledEvents(){
        PlayerProgress p=new PlayerProgress();Random r=new Random(11);int count=0;
        for(int i=0;i<10000;i++)if(ScarePolicy.allowed(p,.16,false,r))count++;
        assertTrue(count>1450&&count<1750);assertTrue(ScarePolicy.allowed(p,.001,true,r));
    }
}
