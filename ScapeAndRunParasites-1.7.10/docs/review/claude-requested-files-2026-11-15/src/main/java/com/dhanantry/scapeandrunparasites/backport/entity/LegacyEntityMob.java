package com.dhanantry.scapeandrunparasites.backport.entity;
import net.minecraft.world.World;import com.dhanantry.scapeandrunparasites.backport.datasync.*;
/** Native 1.7 mob with the original modern Entity callback extension point. */
public abstract class LegacyEntityMob extends net.minecraft.entity.monster.EntityMob implements EntityDataListener {
    public LegacyEntityMob(World world){super(world);}
    // The original vanilla Entity callback is empty; subclasses supply model/state reactions.
    public void notifyDataManagerChange(DataParameter<?> key){}
}
