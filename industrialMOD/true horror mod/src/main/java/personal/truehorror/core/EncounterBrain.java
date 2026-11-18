package personal.truehorror.core;

/** Policy separates creature intentions from collision-safe server navigation. */
public final class EncounterBrain {
    public static final class Sense {
        public double distance;
        public boolean watched,dark,bright,cautious,hunt,feint,injured,hunting;
        public int age,stateTicks,darkSeconds,chaseTicks;
    }
    public static EncounterState decide(CreatureKind kind,Sense s) {
        if(s.age<12)return EncounterState.MANIFESTING;
        if(s.chaseTicks>(kind==CreatureKind.DWELLER?320:220))return EncounterState.DISAPPEARING;
        switch(kind){
            case HEROBRINE:
                if(!s.hunt && (s.distance<7 || (s.watched && s.stateTicks>(s.cautious?8:28))))return EncounterState.DISAPPEARING;
                if(s.feint)return s.distance<5?EncounterState.DISAPPEARING:EncounterState.HUNTING;
                if(s.hunt)return s.distance<2?EncounterState.ATTACKING:EncounterState.HUNTING;
                return s.watched?EncounterState.OBSERVING:(s.distance>18?EncounterState.FOLLOWING:EncounterState.STALKING);
            case WATCHER:
                if(s.bright||s.distance<5)return EncounterState.DISAPPEARING;
                return s.watched?EncounterState.OBSERVING:EncounterState.STALKING;
            case MIMIC:
                if(s.bright&&s.age>160)return EncounterState.DISAPPEARING;
                if(s.distance<7||s.hunt||s.hunting||s.age>500)return s.distance<1.9?EncounterState.ATTACKING:EncounterState.HUNTING;
                return EncounterState.OBSERVING;
            case CRAWLER:
                if(s.age>500||s.injured)return EncounterState.DISAPPEARING;
                if(s.distance<11||s.hunt||s.hunting)return s.distance<1.8?EncounterState.ATTACKING:EncounterState.HUNTING;
                return EncounterState.STALKING;
            case PALE_MAN:
                if(s.age>700)return EncounterState.DISAPPEARING;
                if(s.age>220&&(s.watched||s.distance<12)||s.hunt||s.hunting)return s.distance<2?EncounterState.ATTACKING:EncounterState.HUNTING;
                return s.distance>16?EncounterState.STALKING:EncounterState.OBSERVING;
            case SHADOW:
                if(s.bright)return EncounterState.DISAPPEARING;
                if(s.darkSeconds>45||s.hunt)return s.distance<2?EncounterState.ATTACKING:EncounterState.HUNTING;
                return s.watched?EncounterState.DISAPPEARING:EncounterState.FOLLOWING;
            case DWELLER:
                if(s.injured||s.bright&&s.age>180)return EncounterState.DISAPPEARING;
                if(s.distance<10||s.hunt||s.hunting||s.darkSeconds>60)return s.distance<2.4?EncounterState.ATTACKING:EncounterState.HUNTING;
                return s.distance>18?EncounterState.STALKING:EncounterState.OBSERVING;
            default:return EncounterState.DORMANT;
        }
    }
}
