package com.dhanantry.scapeandrunparasites.backport.entity.movement;
import net.minecraft.entity.Entity;import net.minecraft.nbt.NBTTagCompound;
public final class GravityAccess {
    private GravityAccess(){}
    public static boolean get(Entity entity){return ((EntityMotionState)entity).srp$getNoGravity();}
    public static void set(Entity entity,boolean value){
        EntityMotionState state=(EntityMotionState)entity;if(state.srp$getNoGravity()==value)return;
        state.srp$setNoGravity(value);state.srp$setGravityRevision(state.srp$getGravityRevision()+1);GravityNetwork.changed(entity);
    }
    public static void receive(Entity entity,boolean value,long revision){EntityMotionState state=(EntityMotionState)entity;state.srp$setNoGravity(value);state.srp$setGravityRevision(revision);}
    public static double subtract(double motion,double acceleration,Entity entity){return get(entity)?motion:motion-acceleration;}
    public static void write(Entity entity,NBTTagCompound tag){if(get(entity))tag.setBoolean("NoGravity",true);}
    public static void read(Entity entity,NBTTagCompound tag){set(entity,tag.getBoolean("NoGravity"));}
}
