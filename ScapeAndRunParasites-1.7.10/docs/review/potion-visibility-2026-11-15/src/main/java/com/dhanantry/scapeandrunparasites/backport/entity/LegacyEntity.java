package com.dhanantry.scapeandrunparasites.backport.entity;
import net.minecraft.world.World;import com.dhanantry.scapeandrunparasites.backport.datasync.*;
public abstract class LegacyEntity extends net.minecraft.entity.Entity implements EntityDataListener {
    public LegacyEntity(World world){super(world);}
    // Matches the original vanilla Entity.notifyDataManagerChange default.
    public void notifyDataManagerChange(DataParameter<?> key){}
}
