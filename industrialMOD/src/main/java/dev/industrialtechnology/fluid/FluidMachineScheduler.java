package dev.industrialtechnology.fluid;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import java.util.*;
import net.minecraftforge.event.world.WorldEvent;

/** Fixed server-wide budgets; a large installation slows fairly instead of expanding the tick cost. */
public final class FluidMachineScheduler {
    public static final int MACHINE_VISITS=64,GLOBAL_PROBES=256,PROBES_PER_MACHINE=32,GLOBAL_ACTIONS=4;
    private static final ArrayDeque<TileFluidMachine> queue=new ArrayDeque<TileFluidMachine>();
    private static final Set<TileFluidMachine> live=Collections.newSetFromMap(new IdentityHashMap<TileFluidMachine,Boolean>());
    public static int lastProbes,lastChanges,lastVisits,lastAttempts;
    static void add(TileFluidMachine tile){if(live.add(tile))queue.add(tile);}
    static void remove(TileFluidMachine tile){live.remove(tile);queue.removeFirstOccurrence(tile);}
    public static void clear(){for(TileFluidMachine tile:queue)tile.dequeued();queue.clear();live.clear();PullerSelection.clear();}
    @SubscribeEvent public void tick(TickEvent.ServerTickEvent event){if(event.phase==TickEvent.Phase.END)step();}
    public static void step(){
        PullerSelection.step();
        lastProbes=lastChanges=lastVisits=lastAttempts=0;int visits=Math.min(MACHINE_VISITS,queue.size());
        while(visits-->0){TileFluidMachine tile=queue.poll();if(tile==null)break;lastVisits++;
            if(!live.contains(tile)||!tile.live()||tile.selected()==TileFluidMachine.NONE){live.remove(tile);tile.dequeued();continue;}
            queue.add(tile);tile.network();
            if(lastProbes>=GLOBAL_PROBES||lastAttempts>=GLOBAL_ACTIONS)continue;
            if(tile.work(Math.min(PROBES_PER_MACHINE,GLOBAL_PROBES-lastProbes),true))lastChanges++;
            lastProbes+=tile.lastProbes;
            if(tile.lastAttempt)lastAttempts++;
        }
    }
    @SubscribeEvent public void unload(WorldEvent.Unload event){if(event.world.isRemote)return;PullerSelection.unload(event.world);Iterator<TileFluidMachine> it=queue.iterator();while(it.hasNext()){TileFluidMachine tile=it.next();if(tile.getWorldObj()==event.world){it.remove();live.remove(tile);tile.dequeued();}}}
}
