package personal.truehorror.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum EventType {
    FOOTSTEPS(0,10,0,false,false,false), BRANCH_SNAP(0,6,4,false,false,false),
    CAVE_BREATH(0,7,2,false,false,false), TUNNEL_MINING(0,5,2,false,false,false),
    WHISPERS(0,5,16,false,false,false), DISTANT_WAIL(0,4,1,false,false,false),
    SHELTER_KNOCK(0,6,8,false,false,false), TORCH_FLICKER(0,7,0,false,false,false),
    ANIMAL_STARE(0,5,512,false,false,false), ANIMAL_HUSH(0,4,512,false,false,false),
    WAKE_ECHO(0,15,64,false,false,false), GHOST_LAUGHTER(1,4,16,false,false,false),
    HILL_SIGHTING(1,7,512,true,false,false), TREE_SENTINEL(1,8,5,true,false,false),
    WINDOW_VISITOR(1,8,256,true,true,false), BED_SENTINEL(1,5,129,true,false,false),
    SILHOUETTE(1,7,0,true,false,false), STALKER_TRAIL(1,7,0,true,false,false),
    CAVE_ENTRANCE_MOVEMENT(1,6,2,true,false,false), CORRIDOR_LURE(1,7,18,true,false,false),
    SHADOW_CROSSING(1,6,16,true,false,false), MINING_APPARITION(1,5,34,true,true,false),
    RUSH_FEINT(2,4,0,true,true,false), DARKENED_APPROACH(2,5,16,true,true,false),
    RARE_CHASE(3,3,16,true,true,false), DOOR_OPEN(1,5,8,false,false,true),
    MYSTERY_SIGN(1,4,0,false,false,true), LEAF_TRACE(1,4,4,false,false,true),
    WILDERNESS_CAIRN(1,3,512,false,false,true), SHALLOW_TUNNEL(2,3,2,false,false,true);

    public static final int NIGHT=1,CAVE=2,FOREST=4,INDOOR=8,DARK=16,MINING=32,WAKE=64,BED=128,WINDOW=256,OUTSIDE=512;
    public final int stage,weight,requires;
    public final boolean creature,scare,modifiesWorld;
    EventType(int stage,int weight,int requires,boolean creature,boolean scare,boolean modifiesWorld){
        this.stage=stage;this.weight=weight;this.requires=requires;this.creature=creature;this.scare=scare;this.modifiesWorld=modifiesWorld;
    }
    public boolean eligible(int progression,int context,boolean creatures,boolean scares,boolean edits,PlayerProgress p){
        return progression>=stage&&(context&requires)==requires&&(!creature||creatures)&&(!scare||scares)
                &&(!modifiesWorld||edits)&&!p.sawRecently(name());
    }
    public static EventType select(Random random,int stage,int context,boolean creatures,boolean scares,boolean edits,PlayerProgress p){
        List<EventType> candidates=new ArrayList<EventType>();int total=0;
        for(EventType event:values())if(event.eligible(stage,context,creatures,scares,edits,p)){candidates.add(event);total+=event.weight;}
        if(total==0)return null;
        int choice=random.nextInt(total);
        for(EventType event:candidates){choice-=event.weight;if(choice<0)return event;}
        return null;
    }
}
