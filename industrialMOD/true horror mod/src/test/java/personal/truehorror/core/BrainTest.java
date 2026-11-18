package personal.truehorror.core;
import org.junit.Test;
import static org.junit.Assert.*;

public class BrainTest {
    private EncounterBrain.Sense sense(){EncounterBrain.Sense s=new EncounterBrain.Sense();s.age=100;s.distance=20;s.dark=true;return s;}
    @Test public void watcherFreezesUnderGazeAndMovesOnlyWhenUnwatched(){
        EncounterBrain.Sense s=sense();s.watched=true;assertEquals(EncounterState.OBSERVING,EncounterBrain.decide(CreatureKind.WATCHER,s));
        s.watched=false;assertEquals(EncounterState.STALKING,EncounterBrain.decide(CreatureKind.WATCHER,s));
        s.distance=4;assertEquals(EncounterState.DISAPPEARING,EncounterBrain.decide(CreatureKind.WATCHER,s));
    }
    @Test public void herobrineFadesAtApproachInsteadOfGenericAttack(){
        EncounterBrain.Sense s=sense();s.distance=5;
        assertEquals(EncounterState.DISAPPEARING,EncounterBrain.decide(CreatureKind.HEROBRINE,s));
        s.hunt=true;assertEquals(EncounterState.HUNTING,EncounterBrain.decide(CreatureKind.HEROBRINE,s));
        s.distance=1;assertEquals(EncounterState.ATTACKING,EncounterBrain.decide(CreatureKind.HEROBRINE,s));
    }
    @Test public void cautiousHerobrineDisappearsSoonerWhenWatched(){
        EncounterBrain.Sense s=sense();s.watched=true;s.stateTicks=15;
        assertEquals(EncounterState.OBSERVING,EncounterBrain.decide(CreatureKind.HEROBRINE,s));
        s.cautious=true;assertEquals(EncounterState.DISAPPEARING,EncounterBrain.decide(CreatureKind.HEROBRINE,s));
    }
    @Test public void mimicLuresThenRevealsAndCommitsToABoundedChase(){
        EncounterBrain.Sense s=sense();assertEquals(EncounterState.OBSERVING,EncounterBrain.decide(CreatureKind.MIMIC,s));
        s.distance=6;assertEquals(EncounterState.HUNTING,EncounterBrain.decide(CreatureKind.MIMIC,s));
        s.distance=13;s.hunting=true;assertEquals(EncounterState.HUNTING,EncounterBrain.decide(CreatureKind.MIMIC,s));
        s.chaseTicks=221;assertEquals(EncounterState.DISAPPEARING,EncounterBrain.decide(CreatureKind.MIMIC,s));
    }
    @Test public void paleManHasDelayedSprintAndShadowFearsLight(){
        EncounterBrain.Sense s=sense();s.watched=true;s.distance=10;
        assertEquals(EncounterState.OBSERVING,EncounterBrain.decide(CreatureKind.PALE_MAN,s));
        s.age=230;assertEquals(EncounterState.HUNTING,EncounterBrain.decide(CreatureKind.PALE_MAN,s));
        s.bright=true;assertEquals(EncounterState.DISAPPEARING,EncounterBrain.decide(CreatureKind.SHADOW,s));
    }
    @Test public void crawlerAmbushAndDwellerRetreatHaveDifferentPolicies(){
        EncounterBrain.Sense s=sense();s.distance=10;
        assertEquals(EncounterState.HUNTING,EncounterBrain.decide(CreatureKind.CRAWLER,s));
        assertEquals(EncounterState.OBSERVING,EncounterBrain.decide(CreatureKind.DWELLER,s));
        s.injured=true;assertEquals(EncounterState.DISAPPEARING,EncounterBrain.decide(CreatureKind.DWELLER,s));
    }
    @Test public void allMonstersMaterializeBeforeActing(){
        EncounterBrain.Sense s=sense();s.age=2;s.distance=1;s.hunt=true;
        for(CreatureKind kind:CreatureKind.values())assertEquals(EncounterState.MANIFESTING,EncounterBrain.decide(kind,s));
    }
}
