package dev.industrialtechnology.fluid;

import cpw.mods.fml.common.eventhandler.Event;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/** Incremental identifier selection for face-connected pullers in loaded chunks. */
public final class PullerSelection {
    public static final int CELLS_PER_TICK=128,MAX_CELLS=8192,MAX_DEPTH=64,MAX_JOBS=16;
    private static final ArrayDeque<Job> jobs=new ArrayDeque<Job>();
    public static int lastVisits,lastChanges;

    private PullerSelection(){}
    public static boolean start(TileFluidMachine root,EntityPlayer player,int side,ItemStack identifier,int kind){
        if(root.isSucker()||!root.live()||kind<TileFluidMachine.WATER||kind>TileFluidMachine.LAVA)return false;
        cancel(player);
        if(jobs.size()>=MAX_JOBS)return false;
        jobs.add(new Job(root,player,side,identifier.copy(),kind));return true;
    }
    public static void cancel(EntityPlayer player){for(Iterator<Job> it=jobs.iterator();it.hasNext();)if(it.next().player.getUniqueID().equals(player.getUniqueID()))it.remove();}
    public static void step(){
        lastVisits=lastChanges=0;
        while(lastVisits<CELLS_PER_TICK&&!jobs.isEmpty()){
            Job job=jobs.remove();
            if(!job.valid())continue;
            Cell cell=job.pending.poll();
            if(cell==null)continue;
            lastVisits++;
            job.visit(cell);
            if(!job.pending.isEmpty())jobs.add(job);
        }
    }
    public static void clear(){jobs.clear();lastVisits=lastChanges=0;}
    public static void unload(World world){for(Iterator<Job> it=jobs.iterator();it.hasNext();)if(it.next().world==world)it.remove();}
    public static boolean pending(){return !jobs.isEmpty();}

    private static final class Job {
        final World world;
        final EntityPlayer player;
        final ItemStack identifier;
        final int side,kind;
        final ArrayDeque<Cell> pending=new ArrayDeque<Cell>();
        final Set<Cell> seen=new HashSet<Cell>();
        Job(TileFluidMachine root,EntityPlayer player,int side,ItemStack identifier,int kind){
            this.world=root.getWorldObj();this.player=player;this.side=side;this.identifier=identifier;this.kind=kind;
            offer(root.xCoord,root.yCoord,root.zCoord,0);
        }
        boolean valid(){return !world.isRemote&&!player.isDead&&player.worldObj==world;}
        void offer(int x,int y,int z,int depth){
            if(depth>MAX_DEPTH||seen.size()>=MAX_CELLS||!TileFluidMachine.loaded(world,x,y,z))return;
            Cell cell=new Cell(x,y,z,depth);if(seen.add(cell))pending.add(cell);
        }
        void visit(Cell cell){
            if(!TileFluidMachine.loaded(world,cell.x,cell.y,cell.z))return;
            TileEntity raw=world.getTileEntity(cell.x,cell.y,cell.z);
            if(!(raw instanceof TileFluidMachine))return;
            TileFluidMachine tile=(TileFluidMachine)raw;
            if(tile.isSucker()||!tile.live()||tile.selected()!=TileFluidMachine.NONE&&tile.selected()!=kind)return;
            if(!world.canMineBlock(player,cell.x,cell.y,cell.z)||!player.canPlayerEdit(cell.x,cell.y,cell.z,side,identifier))return;
            if(tile.selected()==TileFluidMachine.NONE){
                PlayerInteractEvent event=ForgeEventFactory.onPlayerInteract(player,PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK,cell.x,cell.y,cell.z,side,world);
                if(event.isCanceled()||event.useBlock==Event.Result.DENY||event.useItem==Event.Result.DENY)return;
                // Protection listeners may have replaced, filled or configured this tile.
                if(!tile.live()||tile.selected()!=TileFluidMachine.NONE||!tile.configure(kind))return;
                if(!tile.hasOwner())tile.owner(player);
                lastChanges++;
            }
            if(cell.depth>=MAX_DEPTH)return;
            for(ForgeDirection direction:ForgeDirection.VALID_DIRECTIONS){
                offer(cell.x+direction.offsetX,cell.y+direction.offsetY,cell.z+direction.offsetZ,cell.depth+1);
            }
        }
    }
    private static final class Cell {
        final int x,y,z,depth;
        Cell(int x,int y,int z,int depth){this.x=x;this.y=y;this.z=z;this.depth=depth;}
        @Override public boolean equals(Object other){if(!(other instanceof Cell))return false;Cell c=(Cell)other;return x==c.x&&y==c.y&&z==c.z;}
        @Override public int hashCode(){return (x*31+y)*31+z;}
    }
}
