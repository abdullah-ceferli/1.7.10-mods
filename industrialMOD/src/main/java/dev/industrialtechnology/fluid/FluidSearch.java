package dev.industrialtechnology.fluid;

import java.util.BitSet;

/** Incremental flood frontier. Coordinates are relative to the machine, never absolute chunk references. */
public final class FluidSearch {
    public static final int RADIUS=16, WIDTH=RADIUS*2+1, VOLUME=WIDTH*WIDTH*WIDTH, MAX_QUEUE=8192;
    private final BitSet seen=new BitSet(VOLUME);
    private int[] queue=new int[128];
    private int head,size;
    public boolean offer(int x,int y,int z){
        if(Math.abs(x)>RADIUS||Math.abs(y)>RADIUS||Math.abs(z)>RADIUS)return false;
        int key=((x+RADIUS)*WIDTH+y+RADIUS)*WIDTH+z+RADIUS;
        if(seen.get(key)||size>=MAX_QUEUE)return false;
        if(size==queue.length){int[] larger=new int[Math.min(MAX_QUEUE,queue.length*2)];for(int i=0;i<size;i++)larger[i]=queue[(head+i)%queue.length];queue=larger;head=0;}
        seen.set(key);queue[(head+size)%queue.length]=key;size++;return true;
    }
    public int poll(){if(size==0)return -1;int key=queue[head];head=(head+1)%queue.length;size--;return key;}
    public boolean isEmpty(){return size==0;}
    public int queued(){return size;}
    public int visited(){return seen.cardinality();}
    public static int x(int key){return key/(WIDTH*WIDTH)-RADIUS;}
    public static int y(int key){return key/WIDTH%WIDTH-RADIUS;}
    public static int z(int key){return key%WIDTH-RADIUS;}
    public void expand(int key){int x=x(key),y=y(key),z=z(key);offer(x+1,y,z);offer(x-1,y,z);offer(x,y+1,z);offer(x,y-1,z);offer(x,y,z+1);offer(x,y,z-1);}
}
