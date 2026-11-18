package personal.truehorror.core;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

public class ProgressionTest {
    @Test public void exactGraceAndEscalationBoundaries(){
        PlayerProgress p=new PlayerProgress();p.activeTicks=11999;assertEquals(0,p.stage(10,1));
        p.activeTicks=12000;assertEquals(1,p.stage(10,1));p.activeTicks=23999;assertEquals(1,p.stage(10,1));
        p.activeTicks=24000;assertEquals(2,p.stage(10,1));p.activeTicks=48000;assertEquals(3,p.stage(10,1));
    }
    @Test public void activeTimeExcludesIdleAndUsesConfiguredSpeed(){
        PlayerProgress p=new PlayerProgress();p.advance(1200,false);assertEquals(0,p.activeTicks);
        p.advance(6000,true);assertEquals(1,p.stage(10,2));assertEquals(0,p.stage(10,1));
    }
    @Test public void randomQuietPeriodsAreBoundedAndNotFixed(){
        PlayerProgress p=new PlayerProgress();p.activeTicks=7500;Set<Long> seen=new HashSet<Long>();Random random=new Random(31);
        for(int i=0;i<100;i++){p.schedule(random,45,150,1);assertTrue(p.nextEvent>=8400&&p.nextEvent<=10500);seen.add(p.nextEvent);}
        assertTrue(seen.size()>50);
    }
    @Test public void recentMemoryExcludesRepetitionAndExpiresOlderEvents(){
        PlayerProgress p=new PlayerProgress();p.remember("a");p.remember("b");p.remember("c");p.remember("d");
        assertTrue(p.sawRecently("a"));p.remember("e");assertFalse(p.sawRecently("a"));assertTrue(p.sawRecently("e"));
    }
    @Test public void gracePeriodNeverSelectsCreaturesOrMajorScares(){
        PlayerProgress p=new PlayerProgress();Random random=new Random(8);
        for(int i=0;i<10000;i++){EventType event=EventType.select(random,0,1023,true,true,true,p);assertNotNull(event);assertFalse(event.creature);assertFalse(event.scare);}
    }
    @Test public void configGatesRemoveHostileScareAndBlockEditEvents(){
        PlayerProgress p=new PlayerProgress();Random random=new Random(9);Set<EventType> seen=new HashSet<EventType>();
        for(int i=0;i<10000;i++){EventType e=EventType.select(random,3,1023,false,false,false,p);assertNotNull(e);assertFalse(e.creature||e.scare||e.modifiesWorld);seen.add(e);}
        assertTrue(seen.contains(EventType.FOOTSTEPS));assertTrue(seen.contains(EventType.WAKE_ECHO));
    }
    @Test public void contextRequirementsAndRecentEventsAreHonored(){
        PlayerProgress p=new PlayerProgress();p.remember(EventType.FOOTSTEPS.name());
        assertFalse(EventType.FOOTSTEPS.eligible(3,0,true,true,true,p));
        assertFalse(EventType.CORRIDOR_LURE.eligible(3,EventType.CAVE,true,true,true,p));
        assertTrue(EventType.CORRIDOR_LURE.eligible(3,EventType.CAVE|EventType.DARK,true,true,true,p));
        assertTrue(EventType.values().length>=20);
    }
}
